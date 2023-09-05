package com.def.warlords.game.model;

import com.def.warlords.record.Record;
import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;
import com.def.warlords.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class Empire implements Record {

    public static final int MAX_HERO_COUNT = 5;

    private EmpireType type;

    private int gold;
    private int income, upkeep;
    private boolean enhanced;

    private EmpireType archenemy = EmpireType.NEUTRAL;
    private final AttitudeType[] attitudes = new AttitudeType[EmpireType.COUNT];

    // Children.
    private final List<City> cities = new ArrayList<>();
    private final List<ArmyGroup> groups = new ArrayList<>();
    private final List<ArmyDelivery> deliveries = new ArrayList<>();
    private final List<Hero> heroes = new ArrayList<>();

    public Empire() {
    }

    public Empire(EmpireType type) {
        this.type = type;
        this.gold = type.getInitialGold();
        Arrays.fill(attitudes, AttitudeType.APATHY);
    }

    public EmpireType getType() {
        return type;
    }

    public boolean isNeutral() {
        return type == EmpireType.NEUTRAL;
    }

    public String getName() {
        return type.getName();
    }

    public int getGold() {
        return gold;
    }

    public int getPillagedGold() {
        return gold / cities.size() / 2;
    }

    public void addGold(int value) {
        gold += value;
    }

    public int getIncome() {
        return income;
    }

    public int getUpkeep() {
        return upkeep;
    }

    public boolean isEnhanced() {
        return enhanced;
    }

    public void setEnhanced(boolean enhanced) {
        this.enhanced = enhanced;
    }

    public EmpireType getArchenemy() {
        return archenemy;
    }

    public AttitudeType getAttitude(EmpireType empireType) {
        return attitudes[empireType.ordinal()];
    }

    public int getCombatModifier(Tile tile) {
        // (e). Terrain Modifier.
        return type.getTerrainModifier(tile.getTerrain());
    }

    // Dependencies.
    public int getCityCount() {
        return cities.size();
    }

    public City getCapitalCity() {
        return !cities.isEmpty() ? cities.get(0) : null;
    }

    public City getRandomCity() {
        return cities.get(Util.randomInt(cities.size()));
    }

    public List<City> getCities() {
        return new ArrayList<>(cities);
    }

    public List<ArmyGroup> getGroups() {
        return new ArrayList<>(groups);
    }

    public List<ArmyDelivery> getDeliveries() {
        return new ArrayList<>(deliveries);
    }

    // Returns the array that represents the delivery timeline from `sourceCity` to `targetCity`.
    // An array index is equal to the remaining time of delivery which is stored at this index.
    public ArmyDelivery[] getDeliveryTimeline(City sourceCity, City targetCity) {
        final ArmyDelivery[] timeline = new ArmyDelivery[ArmyDelivery.MAX_DELIVERY_TIME];
        for (final ArmyDelivery delivery : deliveries) {
            if (sourceCity == delivery.getSourceCity() && targetCity == delivery.getTargetCity()) {
                timeline[delivery.getRemainingTime() - 1] = delivery;
            }
        }
        return timeline;
    }

    public int getHeroCount() {
        return heroes.size();
    }

    public List<Hero> getHeroes() {
        return new ArrayList<>(heroes);
    }

    // This method has to be called once per turn.
    public void recalculateGold() {
        gold += income - upkeep;
        if (gold < 0) {
            gold = 0;
        }
    }

    // Actions.
    public boolean pay(int cost) {
        if (gold >= cost) {
            gold -= cost;
            return true;
        }
        return false;
    }

    public void capture(City city) {
        final Empire cityEmpire = city.getEmpire();
        if (cityEmpire == null) {
            throw new IllegalArgumentException("City is not registered in an empire");
        }
        if (cityEmpire == this) {
            return;
        }
        // Execute all enemy heroes.
        city.getArmies().stream().filter(Army::isHero).forEach(army -> army.kill(army.getGroup().getTile()));
        // NOTE: W uses the following weird gold calculation.
        final int pillagedGold = cityEmpire.getPillagedGold();
        Util.assertTrue(cityEmpire.pay(pillagedGold * 2));
        addGold(pillagedGold);
        city.decreaseDefence();
        city.stopProducingAll();
        Util.assertTrue(registerCity(city));
    }

    public void destroy() {
        if (!cities.isEmpty()) {
            throw new IllegalStateException("Can't destroy an empire with existing cities");
        }
        // NOTE: W leaves artifacts on the map. Do the same.
        getGroups().forEach(ArmyGroup::kill);
        Util.assertTrue(groups.isEmpty());
        Util.assertTrue(deliveries.isEmpty());
        Util.assertTrue(heroes.isEmpty());
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        out.writeEnum(type);
        out.writeInt(gold);
        out.writeInt(income);
        out.writeInt(upkeep);
        out.writeBoolean(enhanced);
        out.writeEnum(archenemy);
        out.writeEnumArray(attitudes);
        // Children.
        out.writeRecordList(cities);
        out.writeRecordList(groups);
        out.writeRecordList(deliveries);
        out.writeRecordList(heroes);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        type = in.readEnum(EmpireType.values());
        gold = in.readInt();
        income = in.readInt();
        upkeep = in.readInt();
        enhanced = in.readBoolean();
        archenemy = in.readEnum(EmpireType.values());
        in.readEnumArray(attitudes, AttitudeType.values());
        // Children.
        in.readRecordList(cities, City::new);
        in.readRecordList(groups, ArmyGroup::new);
        in.readRecordList(deliveries, ArmyDelivery::new);
        in.readRecordList(heroes, null, Hero::new);
    }

    // Registration.
    // NOTE: `city` may have `sourceCities` registered in another empire.
    boolean registerCity(City city) {
        final Empire cityEmpire = city.getEmpire();
        // The city has already registered in this empire.
        if (cityEmpire == this) {
            return true;
        }
        // Unregister the city in the empire.
        if (cityEmpire != null) {
            cityEmpire.unregisterCity(city);
        }
        // Set this empire in the city.
        city.setEmpire(this);
        // Register the groups of the city in this empire.
        for (final Tile cityTile : city.getTiles()) {
            final ArmyGroup cityGroup = cityTile.getGroup();
            if (cityGroup != null) {
                Util.assertTrue(registerGroup(cityGroup));
            }
        }
        // Add the city in this empire.
        income += city.getIncome();
        cities.add(city);
        return true;
    }

    void unregisterCity(City city) {
        // The city is not registered in this empire.
        if (city.getEmpire() != this) {
            return;
        }
        // Unset this empire in the city.
        city.setEmpire(null);
        // Unregister the groups of the city in this empire.
        for (final Tile cityTile : city.getTiles()) {
            final ArmyGroup cityGroup = cityTile.getGroup();
            if (cityGroup != null) {
                unregisterGroup(cityGroup);
            }
        }
        // Remove the city from this empire.
        income -= city.getIncome();
        cities.remove(city);
    }

    boolean registerGroup(ArmyGroup group) {
        final Empire groupEmpire = group.getEmpire();
        // The group has already registered in this empire.
        if (groupEmpire == this) {
            return true;
        }
        // The city of the group is not register in this empire.
        final Tile groupTile = group.getTile();
        if (groupTile != null) {
            final City groupCity = groupTile.getCity();
            if (groupCity != null && groupCity.getEmpire() != this) {
                return false;
            }
        }
        // Unregister the group in the empire.
        if (groupEmpire != null) {
            groupEmpire.unregisterGroup(group);
        }
        // Set this empire in the group.
        group.setEmpire(this);
        // Register the armies of the group in this empire.
        for (final Army army : group.getArmies()) {
            Util.assertTrue(registerArmy(army));
        }
        // Add the group in this empire.
        groups.add(group);
        return true;
    }

    void unregisterGroup(ArmyGroup group) {
        // The group is not registered in this empire.
        if (group.getEmpire() != this) {
            return;
        }
        // Unset this empire in the group.
        group.setEmpire(null);
        // Unregister the armies of the group in this empire.
        for (final Army army : group.getArmies()) {
            unregisterArmy(army);
        }
        // Unregister the group in the city if the city is registered in an empire.
        final Tile groupTile = group.getTile();
        if (groupTile != null) {
            final City groupCity = groupTile.getCity();
            if (groupCity != null && groupCity.getEmpire() != null) {
                groupTile.unregisterGroup(group);
            }
        }
        // Remove the group from this empire.
        groups.remove(group);
    }

    boolean registerDelivery(ArmyDelivery delivery) {
        final Empire deliveryEmpire = delivery.getEmpire();
        // The delivery has already registered in this empire.
        if (deliveryEmpire == this) {
            return true;
        }
        // Unregister the delivery in the empire.
        if (deliveryEmpire != null) {
            deliveryEmpire.unregisterDelivery(delivery);
        }
        // Set this empire in the delivery.
        delivery.setEmpire(this);
        // Register the army of the group in this empire.
        final Army deliveryArmy = delivery.getArmy();
        if (deliveryArmy != null) {
            registerArmy(deliveryArmy);
        }
        // Add the delivery in this empire.
        deliveries.add(delivery);
        return true;
    }

    void unregisterDelivery(ArmyDelivery delivery) {
        // The delivery is not registered in this empire.
        if (delivery.getEmpire() != this) {
            return;
        }
        // Unset this empire in the delivery.
        delivery.setEmpire(null);
        // Unregister the army of the delivery in this empire.
        final Army deliveryArmy = delivery.getArmy();
        if (deliveryArmy != null) {
            unregisterArmy(deliveryArmy);
        }
        // Remove the delivery from this empire.
        deliveries.remove(delivery);
    }

    boolean registerArmy(Army army) {
        final Empire armyEmpire = army.getEmpire();
        // The army has already registered in this empire.
        if (armyEmpire == this) {
            return true;
        }
        // The group of the army is not registered in this empire.
        final ArmyGroup armyGroup = army.getGroup();
        if (armyGroup != null && armyGroup.getEmpire() != this) {
            return false;
        }
        // The delivery of the army is not registered in this empire.
        final ArmyDelivery armyDelivery = army.getDelivery();
        if (armyDelivery != null && armyDelivery.getEmpire() != this) {
            return false;
        }
        // No room for the hero.
        if (army.isHero() && heroes.size() == MAX_HERO_COUNT) {
            return false;
        }
        // Unregister the army in the empire.
        if (armyEmpire != null) {
            armyEmpire.unregisterArmy(army);
        }
        // Unset this empire in the army.
        army.setEmpire(this);
        // Add the army in this empire.
        upkeep += army.getSupportCost();
        if (army.isHero()) {
            heroes.add((Hero) army);
        }
        return true;
    }

    void unregisterArmy(Army army) {
        // The army is not registered in this empire.
        if (army.getEmpire() != this) {
            return;
        }
        // Unset this empire in the army.
        army.setEmpire(null);
        // Unregister the army in the group if the group is registered in an empire.
        final ArmyGroup armyGroup = army.getGroup();
        if (armyGroup != null && armyGroup.getEmpire() != null) {
            armyGroup.unregisterArmy(army);
        }
        // Unregister the army in the delivery if the delivery is registered in an empire.
        final ArmyDelivery armyDelivery = army.getDelivery();
        if (armyDelivery != null && armyDelivery.getEmpire() != null) {
            armyDelivery.unregisterArmy(army);
        }
        // Remove the army from this empire.
        upkeep -= army.getSupportCost();
        if (army.isHero()) {
            heroes.remove((Hero) army);
        }
    }
}
