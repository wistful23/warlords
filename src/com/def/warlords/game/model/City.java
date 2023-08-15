package com.def.warlords.game.model;

import com.def.warlords.record.Record;
import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;
import com.def.warlords.util.Logger;
import com.def.warlords.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class City implements Record, Locatable, Comparable<City> {

    public static final int MAX_TILE_COUNT = 4;
    public static final int MAX_FACTORY_COUNT = 4;
    public static final int MAX_SOURCE_CITY_COUNT = 4;

    public static final int MAX_DEFENCE = 10;

    private static final int[] defencePrices = {50, 50, 50, 75, 100, 150, 175, 350, 500, 800, 0};

    private String name;
    private Tile mainTile, portTile;
    private int defence;
    private int income;

    private ArmyFactory currentFactory;
    private int remainingTime;

    // Non-recordable.
    private Army producedArmy;
    private Army deliveredArmy;

    // Parents.
    private Empire empire;
    private City targetCity;

    // Children.
    private final List<Tile> tiles = new ArrayList<>(MAX_TILE_COUNT);
    private final List<ArmyFactory> factories = new ArrayList<>(MAX_FACTORY_COUNT);
    private final List<City> sourceCities = new ArrayList<>(MAX_SOURCE_CITY_COUNT);

    public City() {
    }

    public City(String name, Tile mainTile, Tile portTile, int defence, int income) {
        this.name = name;
        this.mainTile = mainTile;
        this.portTile = portTile;
        this.defence = defence;
        this.income = income;
    }

    public String getName() {
        return name;
    }

    public Tile getMainTile() {
        return mainTile;
    }

    public int getDefence() {
        return defence;
    }

    public int getDefencePrice() {
        return defencePrices[defence];
    }

    public void increaseDefence() {
        if (defence < MAX_DEFENCE) {
            ++defence;
        }
    }

    public void decreaseDefence() {
        if (defence > 0) {
            --defence;
        }
    }

    public int getCombatModifier() {
        int modifier = 0;
        if (defence >= 2) ++modifier;
        if (defence >= 7) ++modifier;
        if (defence >= 9) ++modifier;
        return modifier;
    }

    public int getIncome() {
        return income;
    }

    // Dependencies.
    public Empire getEmpire() {
        return empire;
    }

    public boolean isNeutral() {
        return empire != null && empire.isNeutral();
    }

    public City getTargetCity() {
        return targetCity;
    }

    public List<Tile> getTiles() {
        return new ArrayList<>(tiles);
    }

    public int getFactoryCount() {
        return factories.size();
    }

    public ArmyFactory getFactory(int index) {
        return factories.get(index);
    }

    public List<ArmyFactory> getFactories() {
        return new ArrayList<>(factories);
    }

    public int getSourceCityCount() {
        return sourceCities.size();
    }

    public City getSourceCity(int index) {
        return sourceCities.get(index);
    }

    public List<City> getSourceCities() {
        return new ArrayList<>(sourceCities);
    }

    // Occupation.
    public int getArmyCount() {
        return tiles.stream().mapToInt(Tile::getArmyCount).sum();
    }

    public boolean isFull() {
        return tiles.stream().allMatch(Tile::isFull);
    }

    public boolean isPortFull() {
        return portTile != null && portTile.isOccupiedBy(empire) && portTile.isFull();
    }

    public boolean isPortOccupiedByEnemy() {
        return portTile != null && portTile.isOccupiedByEnemy(empire);
    }

    public ArmyList getArmies() {
        if (empire == null) {
            throw new IllegalStateException("City is not registered in an empire");
        }
        // Capacity can be up to MAX_ARMY_COUNT * MAX_TILE_COUNT.
        final ArmyList armies = new ArmyList(ArmyGroup.MAX_ARMY_COUNT, empire);
        for (final Tile tile : tiles) {
            final ArmyGroup tileGroup = tile.getGroup();
            if (tileGroup != null) {
                armies.addAll(tileGroup.getArmies());
            }
        }
        return armies;
    }

    // Producing.
    public ArmyFactory getCurrentFactory() {
        return currentFactory;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public boolean isProducing() {
        return remainingTime != 0;
    }

    public Army getProducedArmy() {
        return producedArmy;
    }

    public Army getDeliveredArmy() {
        return deliveredArmy;
    }

    // Starts producing for this city on `factory`.
    // Returns false if not enough gold for producing.
    public boolean startProducing(ArmyFactory factory) {
        if (empire == null) {
            throw new IllegalStateException("City is not registered in an empire");
        }
        if (factory.getCity() != this) {
            throw new IllegalArgumentException("Factory is not registered in this city");
        }
        if (factory.getType().isNavy() && portTile == null) {
            throw new IllegalArgumentException("No port in this city to produce a navy");
        }
        stopProducing();
        if (empire.pay(factory.getCost())) {
            remainingTime = factory.getTime();
            currentFactory = factory;
            return true;
        }
        return false;
    }

    // Starts producing for `targetCity` city on `factory`.
    // Returns false if not enough gold for producing.
    public boolean startProducing(ArmyFactory factory, City targetCity) {
        if (startProducing(factory)) {
            if (factory.getType().isNavy()) {
                throw new IllegalArgumentException("Cannot deliver a navy");
            }
            if (!targetCity.registerSourceCity(this)) {
                throw new IllegalArgumentException("Target city cannot register this city");
            }
            return true;
        }
        return false;
    }

    // Continues producing on `currentFactory`.
    // Returns false if not enough gold for producing.
    public boolean continueProducing() {
        if (empire == null) {
            throw new IllegalStateException("City is not registered in an empire");
        }
        if (currentFactory == null) {
            throw new IllegalStateException("Current factory is not assigned");
        }
        if (empire.pay(currentFactory.getCost())) {
            final int time = currentFactory.getTime();
            remainingTime = empire.isEnhanced() ? (time + 1) / 2 : time;
            return true;
        }
        // BUG: W doesn't discard `targetCity`.
        stopProducing();
        return false;
    }

    // Stops producing and discards `targetCity`.
    public void stopProducing() {
        remainingTime = 0;
        if (targetCity != null) {
            targetCity.unregisterSourceCity(this);
        }
    }

    // Stops producing all for this city.
    public void stopProducingAll() {
        stopProducing();
        getSourceCities().forEach(City::stopProducing);
    }

    // Produces the army on `currentFactory` and stores it to `producedArmy`.
    // Returns false if the army is not produced at this time.
    // This method has to be called once per turn.
    public boolean produce() {
        if (empire == null) {
            throw new IllegalStateException("City is not registered in an empire");
        }
        producedArmy = null;
        if (remainingTime == 0) {
            return false;
        }
        if (remainingTime > 1) {
            --remainingTime;
            return false;
        }
        if (targetCity != null) {
            Util.assertFalse(currentFactory.getType().isNavy());
            producedArmy = currentFactory.produce();
            final ArmyDelivery delivery = new ArmyDelivery(this, targetCity, ArmyDelivery.MAX_DELIVERY_TIME);
            Util.assertTrue(empire.registerDelivery(delivery));
            Util.assertTrue(delivery.registerArmy(producedArmy));
        } else {
            if (currentFactory.getType().isNavy()) {
                Util.assertNotNull(portTile);
                if (isPortOccupiedByEnemy() || isPortFull()) {
                    // NOTE: W stops producing if the port is full.
                    // Wait for the port to free.
                    return false;
                }
            } else if (isFull()) {
                // Wait for the city to have a room.
                return false;
            }
            producedArmy = currentFactory.produce();
            Util.assertTrue(empire.registerArmy(producedArmy));
            Util.assertTrue(locate(producedArmy));
        }
        remainingTime = 0;
        return true;
    }

    // Creates a hero and locates it to this city.
    // Returns null if there is no room for a hero.
    public Hero hireHero(String name, boolean veteran) {
        if (empire == null) {
            throw new IllegalStateException("City is not registered in an empire");
        }
        if (isFull()) {
            return null;
        }
        final Hero hero = new Hero(name, veteran);
        Util.assertTrue(empire.registerArmy(hero));
        Util.assertTrue(locate(hero));
        return hero;
    }

    // Locates the army to this city and marks it as delivered.
    // Returns false if the army is not located for some reason.
    public boolean deliver(Army army) {
        if (!locate(army)) {
            return false;
        }
        deliveredArmy = army;
        return true;
    }

    // Locates the army to this city.
    // Requires the army is registered in an empire.
    // Returns false if the army is not located for some reason.
    public boolean locate(Army army) {
        if (empire == null) {
            throw new IllegalStateException("City is not registered in an empire");
        }
        final Empire armyEmpire = army.getEmpire();
        if (armyEmpire == null) {
            throw new IllegalArgumentException("Army is not registered in an empire");
        }
        if (armyEmpire != empire) {
            Logger.info("City is occupied by another empire");
            return false;
        }
        if (army.isNavy()) {
            if (portTile == null) {
                Logger.warn("No port in this city to locate a navy");
                return false;
            }
            return portTile.locate(army);
        }
        for (final Tile tile : tiles) {
            if (tile.locate(army)) {
                return true;
            }
        }
        return false;
    }

    public void raze() {
        if (empire == null) {
            throw new IllegalStateException("City is not registered in an empire");
        }
        stopProducingAll();
        getTiles().forEach(Tile::raze);
        empire.unregisterCity(this);
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        out.writeString(name);
        out.writeRecord(mainTile);
        out.writeRecord(portTile);
        out.writeInt(defence);
        out.writeInt(income);
        out.writeRecord(currentFactory);
        out.writeInt(remainingTime);
        // Parents.
        out.writeRecord(empire);
        out.writeRecord(targetCity);
        // Children.
        out.writeRecordList(tiles);
        out.writeRecordList(factories);
        out.writeRecordList(sourceCities);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        name = in.readString();
        mainTile = in.readRecord(Tile::new);
        portTile = in.readRecord(Tile::new);
        defence = in.readInt();
        income = in.readInt();
        currentFactory = in.readRecord(ArmyFactory::new);
        remainingTime = in.readInt();
        // Parents.
        empire = in.readRecord(Empire::new);
        targetCity = in.readRecord(City::new);
        // Children.
        in.readRecordList(tiles, Tile::new);
        in.readRecordList(factories, ArmyFactory::new);
        in.readRecordList(sourceCities, City::new);
    }

    @Override
    public int getPosX() {
        return mainTile.getPosX();
    }

    @Override
    public int getPosY() {
        return mainTile.getPosY();
    }

    @Override
    public int compareTo(City other) {
        return mainTile.compareTo(other.mainTile);
    }

    // Registration.
    void setEmpire(Empire empire) {
        this.empire = empire;
    }

    void setTargetCity(City targetCity) {
        this.targetCity = targetCity;
    }

    boolean registerTile(Tile tile) {
        final City tileCity = tile.getCity();
        // The tile has already registered in this city.
        if (tileCity == this) {
            return true;
        }
        // This city is not registered in an empire but the group of the tile is registered in the empire.
        final ArmyGroup tileGroup = tile.getGroup();
        if (tileGroup != null) {
            if (empire == null && tileGroup.getEmpire() != null) {
                return false;
            }
        }
        // The terrain is not a city type.
        if (!tile.isCity()) {
            return false;
        }
        // First tile must be the main tile.
        if (tiles.isEmpty() && tile != mainTile) {
            return false;
        }
        // No room for the tile.
        if (tiles.size() == MAX_TILE_COUNT) {
            return false;
        }
        // Unregister the tile in the city.
        if (tileCity != null) {
            tileCity.unregisterTile(tile);
        }
        // Register the group of the tile in the empire of this city.
        if (empire != null && tileGroup != null) {
            empire.registerGroup(tileGroup);
        }
        // Set this city in the tile.
        tile.setCity(this);
        // Add the tile in this city.
        tiles.add(tile);
        return true;
    }

    void unregisterTile(Tile tile) {
        // The tile is not registered in this city.
        if (tile.getCity() != this) {
            return;
        }
        // Unset this city in the tile.
        tile.setCity(null);
        // Remove the tile from this city.
        tiles.remove(tile);
    }

    boolean registerFactory(ArmyFactory factory) {
        final City factoryCity = factory.getCity();
        // The factory has already registered in this city.
        if (factoryCity == this) {
            return true;
        }
        // No room for the factory.
        if (factories.size() == MAX_FACTORY_COUNT) {
            return false;
        }
        // Unregister the factory in the city.
        if (factoryCity != null) {
            factoryCity.unregisterFactory(factory);
        }
        // Set this city in the factory.
        factory.setCity(this);
        // Add the factory in this city.
        factories.add(factory);
        return true;
    }

    void unregisterFactory(ArmyFactory factory) {
        // The factory is not registered in this city.
        if (factory.getCity() != this) {
            return;
        }
        // Unset this city in the factory.
        factory.setCity(null);
        // Remove the factory from this city.
        factories.remove(factory);
    }

    boolean registerSourceCity(City sourceCity) {
        // The source city equals this city.
        if (sourceCity == this) {
            return false;
        }
        final City sourceCityTargetCity = sourceCity.getTargetCity();
        // The source city has already registered in this city.
        if (sourceCityTargetCity == this) {
            return true;
        }
        // The source city is not registered in the empire of this city.
        if (sourceCity.getEmpire() != empire) {
            return false;
        }
        // No room for the source city.
        if (sourceCities.size() == MAX_SOURCE_CITY_COUNT) {
            return false;
        }
        // Unregister the source city in the target city.
        if (sourceCityTargetCity != null) {
            sourceCityTargetCity.unregisterSourceCity(sourceCity);
        }
        // Set this city in the source city.
        sourceCity.setTargetCity(this);
        // Add the source city in this city.
        sourceCities.add(sourceCity);
        return true;
    }

    void unregisterSourceCity(City sourceCity) {
        // The source city is not registered in this city.
        if (sourceCity.getTargetCity() != this) {
            return;
        }
        // Unset this city in the source city.
        sourceCity.setTargetCity(null);
        // Remove the source city from this city.
        sourceCities.remove(sourceCity);
    }
}
