package com.def.warlords.game.model;

import com.def.warlords.record.Record;
import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;
import com.def.warlords.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wistful23
 * @version 1.23
 */
public class ArmyGroup implements Record, Locatable, Comparable<ArmyGroup> {

    public static final int MAX_ARMY_COUNT = 8;

    // Parents.
    private Empire empire;
    private Tile tile;

    // Children.
    private final List<Army> armies = new ArrayList<>(MAX_ARMY_COUNT);

    // Dependencies.
    public Empire getEmpire() {
        return empire;
    }

    public Tile getTile() {
        return tile;
    }

    public int getArmyCount() {
        return armies.size();
    }

    public int getSelectedArmyCount() {
        return (int) armies.stream().filter(Army::isSelected).count();
    }

    public boolean isActive() {
        return armies.stream().anyMatch(army -> army.getState() == Army.State.ACTIVE);
    }

    public boolean isNavy() {
        return armies.stream().anyMatch(Army::isNavy);
    }

    public boolean isFull() {
        return armies.size() == MAX_ARMY_COUNT;
    }

    public Army getArmy(int index) {
        return armies.get(index);
    }

    public ArmyList getArmies() {
        return new ArmyList(armies, empire);
    }

    public ArmyList getSelectedArmies() {
        return new ArmyList(armies.stream().filter(Army::isSelected).collect(Collectors.toList()), empire);
    }

    // Selection.
    public boolean isSelected() {
        return armies.stream().allMatch(Army::isSelected);
    }

    public void setSelected(boolean selected) {
        armies.forEach(army -> army.setSelected(selected));
    }

    // Actions.
    public void rest() {
        armies.forEach(Army::rest);
    }

    public void kill() {
        getArmies().forEach(army -> army.kill(tile));
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        // Parents.
        out.writeRecord(empire);
        out.writeRecord(tile);
        // Children.
        out.writeRecordList(armies);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        // Parents.
        empire = in.readRecord(Empire::new);
        tile = in.readRecord(Tile::new);
        // Children.
        in.readRecordList(armies, Army::new, Hero::new);
    }

    @Override
    public int getPosX() {
        return tile.getPosX();
    }

    @Override
    public int getPosY() {
        return tile.getPosY();
    }

    @Override
    public int compareTo(ArmyGroup other) {
        return tile.compareTo(other.tile);
    }

    // Registration.
    void setEmpire(Empire empire) {
        this.empire = empire;
    }

    void setTile(Tile tile) {
        this.tile = tile;
    }

    boolean registerArmy(Army army) {
        final ArmyGroup armyGroup = army.getGroup();
        // The army has already registered in this group.
        if (armyGroup == this) {
            return true;
        }
        // This group is not registered in an empire but the army is registered in the empire.
        if (empire == null && army.getEmpire() != null) {
            return false;
        }
        // No room for the army.
        if (armies.size() == MAX_ARMY_COUNT) {
            return false;
        }
        // Unregister the army in the group.
        if (armyGroup != null) {
            armyGroup.unregisterArmy(army);
        }
        // Unregister the army in the delivery.
        final ArmyDelivery armyDelivery = army.getDelivery();
        if (armyDelivery != null) {
            armyDelivery.unregisterArmy(army);
        }
        // Register the army in the empire of this group.
        if (empire != null) {
            Util.assertTrue(empire.registerArmy(army));
        }
        // Set this group in the army.
        army.setGroup(this);
        // Add the army in this group.
        armies.add(army);
        return true;
    }

    void unregisterArmy(Army army) {
        // The army is not registered in this group.
        if (army.getGroup() != this) {
            return;
        }
        // Unset this group in the army.
        army.setGroup(null);
        // Remove the army from this group.
        armies.remove(army);
        // Self-destruct.
        if (armies.isEmpty()) {
            unregisterSelf();
        }
    }

    void unregisterSelf() {
        if (empire != null) {
            empire.unregisterGroup(this);
        }
        if (tile != null) {
            tile.unregisterGroup(this);
        }
    }
}
