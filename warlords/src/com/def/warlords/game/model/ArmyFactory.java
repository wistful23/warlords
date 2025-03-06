package com.def.warlords.game.model;

import com.def.warlords.record.Record;
import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;

import java.io.IOException;

/**
 * @author wistful23
 * @version 1.23
 */
public class ArmyFactory implements Record {

    private static final String[] suffix = {"th", "st", "nd", "rd", "th"};

    private ArmyType type;
    private int time, cost, strength, movement;

    private int count;

    // Parents.
    private City city;

    public ArmyFactory() {
    }

    public ArmyFactory(ArmyType type, int time, int cost, int strength, int movement) {
        this.type = type;
        this.time = time;
        this.cost = cost;
        this.strength = strength;
        this.movement = movement;
    }

    public ArmyType getType() {
        return type;
    }

    public boolean isNavy() {
        return type.isNavy();
    }

    public boolean isFlying() {
        return type.isFlying();
    }

    public int getTime() {
        return time;
    }

    public int getCost() {
        return cost;
    }

    public int getStrength() {
        return strength;
    }

    public int getMovement() {
        return movement;
    }

    // Dependencies.
    public City getCity() {
        return city;
    }

    public Army produce() {
        ++count;
        final String name;
        if (city != null) {
            name = city.getName() + " " + count + suffix[Math.min(count % 10, 4)] + " " + type.getName();
        } else {
            name = type.getName();
        }
        return new Army(type, name, cost / 2, strength, movement);
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        out.writeEnum(type);
        out.writeInt(time);
        out.writeInt(cost);
        out.writeInt(strength);
        out.writeInt(movement);
        out.writeInt(count);
        // Parents.
        out.writeRecord(city);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        type = in.readEnum(ArmyType.values());
        time = in.readInt();
        cost = in.readInt();
        strength = in.readInt();
        movement = in.readInt();
        count = in.readInt();
        // Parents.
        city = in.readRecord(City::new);
    }

    // Registration.
    void setCity(City city) {
        this.city = city;
    }
}
