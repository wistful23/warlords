package com.def.warlords.game.model;

import com.def.warlords.record.Record;
import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;
import com.def.warlords.util.Util;

import java.io.IOException;

/**
 * @author wistful23
 * @version 1.23
 */
public class ArmyDelivery implements Record {

    public static final int MAX_DELIVERY_TIME = 2;

    private City sourceCity, targetCity;

    private int remainingTime;

    // Parents.
    private Empire empire;

    // Children.
    private Army army;

    public ArmyDelivery() {
    }

    public ArmyDelivery(City sourceCity, City targetCity, int time) {
        if (time <= 0 || time > MAX_DELIVERY_TIME) {
            throw new IllegalArgumentException("Invalid delivery time");
        }
        this.sourceCity = sourceCity;
        this.targetCity = targetCity;
        this.remainingTime = time;
    }

    public City getSourceCity() {
        return sourceCity;
    }

    public City getTargetCity() {
        return targetCity;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    // Dependencies.
    public Empire getEmpire() {
        return empire;
    }

    public Army getArmy() {
        return army;
    }

    // Delivers the army to the target city.
    // Returns false if the army is not delivered at this time or can't be located in the target city.
    // This method has to be called once per turn.
    public boolean deliver() {
        boolean delivered = false;
        if (--remainingTime == 0) {
            // NOTE: W silently disbands the army if it can't be located in the target city. Do the same.
            delivered = targetCity.deliver(army);
            unregisterSelf();
        }
        return delivered;
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        out.writeRecord(sourceCity);
        out.writeRecord(targetCity);
        out.writeInt(remainingTime);
        // Parents.
        out.writeRecord(empire);
        // Children.
        out.writeRecord(army);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        sourceCity = in.readRecord(City::new);
        targetCity = in.readRecord(City::new);
        remainingTime = in.readInt();
        // Parents.
        empire = in.readRecord(Empire::new);
        // Children.
        army = in.readRecord(Army::new);
    }

    // Registration.
    void setEmpire(Empire empire) {
        this.empire = empire;
    }

    boolean registerArmy(Army army) {
        // The army has already registered in this delivery.
        if (this.army == army) {
            return true;
        }
        // This delivery is not registered in an empire but the army is registered in the empire.
        if (empire == null && army.getEmpire() != null) {
            return false;
        }
        // Can't delivery a hero.
        if (army.isHero()) {
            return false;
        }
        // Unregister the army in the delivery.
        final ArmyDelivery armyDelivery = army.getDelivery();
        if (armyDelivery != null) {
            armyDelivery.unregisterArmy(army);
        }
        // Unregister the army in the group.
        final ArmyGroup armyGroup = army.getGroup();
        if (armyGroup != null) {
            armyGroup.unregisterArmy(army);
        }
        // Register the army in the empire of this delivery.
        if (empire != null) {
            Util.assertTrue(empire.registerArmy(army));
        }
        // Set this delivery in the army.
        army.setDelivery(this);
        // Add the army in this delivery.
        this.army = army;
        return true;
    }

    void unregisterArmy(Army army) {
        // The army is not registered in this delivery.
        if (this.army != army) {
            return;
        }
        // Unset this delivery in the army.
        army.setDelivery(null);
        // Remove the army from this delivery.
        this.army = null;
        // Self-destruct.
        unregisterSelf();
    }

    void unregisterSelf() {
        if (empire != null) {
            empire.unregisterDelivery(this);
        }
    }
}
