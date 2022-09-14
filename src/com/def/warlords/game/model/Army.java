package com.def.warlords.game.model;

import com.def.warlords.record.Record;
import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;
import com.def.warlords.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class Army implements Record, Locatable, Comparable<Army> {

    public static final int MAX_STRENGTH = 9;

    public static final int MAX_HEALTH = 2;

    public enum State {
        ACTIVE,
        QUIT,
        DEFENDED
    }

    private ArmyType type;
    private String name;
    private int supportCost;
    private int strength;
    private int movement;

    private int movementPoints;

    // NOTE: W supports QUIT and DEFENDED states simultaneously.
    private State state = State.ACTIVE;

    // Temples where this army was blessed.
    private final List<Building> temples = new ArrayList<>();

    // Non-recordable.
    private boolean selected;

    // Parents.
    private Empire empire;
    private ArmyGroup group;
    private ArmyDelivery delivery;

    public Army() {
    }

    public Army(ArmyType type, String name, int supportCost, int strength, int movement) {
        this.type = type;
        this.name = name;
        this.supportCost = supportCost;
        this.strength = strength;
        this.movement = movement;
        this.movementPoints = movement;
    }

    public String getName() {
        return name;
    }

    public ArmyType getType() {
        return type;
    }

    public boolean isHero() {
        return type.isHero();
    }

    public boolean isNavy() {
        return type.isNavy();
    }

    public boolean isFlying() {
        return type.isFlying();
    }

    public boolean isSpecial() {
        return type.isSpecial();
    }

    public int ordinal() {
        return type.isNavy() ? ArmyType.COUNT : type.ordinal();
    }

    public int getCombatModifier() {
        return 0;
    }

    public int getSupportCost() {
        return supportCost;
    }

    // Strength.
    public int getStrength() {
        return strength;
    }

    public int getTotalStrength() {
        return strength;
    }

    public int getTotalStrength(Tile tile, int fcm) {
        return Util.truncate(getTotalStrength() + type.getTerrainModifier(tile.getTerrain()) + fcm, MAX_STRENGTH);
    }

    public void increaseStrength() {
        if (strength < MAX_STRENGTH) {
            ++strength;
        }
    }

    public void decreaseStrength() {
        if (strength > 0) {
            --strength;
        }
    }

    // State.
    public State getState() {
        return state;
    }

    public void updateState(State state) {
        if (state == State.ACTIVE && this.state == State.QUIT) {
            // The army can't change its state from QUIT to ACTIVE.
            return;
        }
        this.state = state;
    }

    // Movement.
    public int getMovementPoints() {
        return movementPoints;
    }

    public int getMovementCost(TerrainType terrain) {
        // MYTH: There is an artifact that allows a hero to fly.
        return type.getMovementCost(terrain);
    }

    public void move(int movementCost) {
        if (movementCost == ArmyType.FORBIDDEN_MOVEMENT_COST) {
            throw new IllegalArgumentException("Forbidden movement cost");
        }
        movementPoints -= movementCost;
        if (movementPoints < 0) {
            movementPoints = 0;
        }
    }

    public void reset() {
        movementPoints = 0;
    }

    public void rest() {
        movementPoints = movement + Math.min(2, movementPoints);
        if (state != State.DEFENDED) {
            state = State.ACTIVE;
        }
    }

    // Selection.
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // Dependencies.
    public Empire getEmpire() {
        return empire;
    }

    public ArmyGroup getGroup() {
        return group;
    }

    public ArmyDelivery getDelivery() {
        return delivery;
    }

    // Actions.
    public boolean bless(Building temple) {
        if (temples.contains(temple)) {
            return false;
        }
        temples.add(temple);
        increaseStrength();
        return true;
    }

    public void kill(Tile tile) {
        unregisterSelf();
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        out.writeEnum(type);
        out.writeString(name);
        out.writeInt(supportCost);
        out.writeInt(strength);
        out.writeInt(movement);
        out.writeInt(movementPoints);
        out.writeEnum(state);
        out.writeRecordList(temples);
        // Parents.
        out.writeRecord(empire);
        out.writeRecord(group);
        out.writeRecord(delivery);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        type = in.readEnum(ArmyType.values());
        name = in.readString();
        supportCost = in.readInt();
        strength = in.readInt();
        movement = in.readInt();
        movementPoints = in.readInt();
        state = in.readEnum(State.values());
        in.readRecordList(temples, Building::new);
        // Parents.
        empire = in.readRecord(Empire::new);
        group = in.readRecord(ArmyGroup::new);
        delivery = in.readRecord(ArmyDelivery::new);
    }

    @Override
    public int getPosX() {
        return group.getPosX();
    }

    @Override
    public int getPosY() {
        return group.getPosY();
    }

    @Override
    public int compareTo(Army other) {
        return group.compareTo(other.group);
    }

    @Override
    public String toString() {
        return super.toString() + '#' + name;
    }

    // Registration.
    void setEmpire(Empire empire) {
        this.empire = empire;
    }

    void setGroup(ArmyGroup group) {
        this.group = group;
    }

    void setDelivery(ArmyDelivery delivery) {
        this.delivery = delivery;
    }

    void unregisterSelf() {
        if (empire != null) {
            empire.unregisterArmy(this);
        }
        if (group != null) {
            group.unregisterArmy(this);
        }
        if (delivery != null) {
            delivery.unregisterArmy(this);
        }
    }
}
