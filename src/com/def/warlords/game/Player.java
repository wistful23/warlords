package com.def.warlords.game;

import com.def.warlords.game.model.*;
import com.def.warlords.record.Record;
import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;
import com.def.warlords.util.Util;

import java.io.IOException;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class Player implements Record {

    private static final int MIN_HERO_OFFER_CHANCE = 10;
    private static final int MAX_HERO_OFFER_CHANCE = 3;

    private static final int MIN_HERO_PRICE = 300;
    private static final int MAX_HERO_PRICE = 1300;

    private static final int MAX_CITY_ALLY_COUNT = 3;

    private Kingdom kingdom;
    private Empire empire;
    private PlayerLevel level;

    private boolean observed;
    private boolean destroyed;
    private ArmyGroup currentGroup;

    public Player() {
    }

    public Player(Kingdom kingdom, Empire empire, PlayerLevel level) {
        this.kingdom = kingdom;
        this.empire = empire;
        this.level = level;
    }

    public PlayerLevel getLevel() {
        return level;
    }

    public void setLevel(PlayerLevel level) {
        this.level = level;
    }

    public boolean isHuman() {
        return level == PlayerLevel.HUMAN;
    }

    public boolean isObserved() {
        return observed;
    }

    public void setObserved(boolean observed) {
        this.observed = observed;
    }

    public Empire getEmpire() {
        return empire;
    }

    public EmpireType getEmpireType() {
        return empire.getType();
    }

    public City getCapitalCity() {
        return empire.getCapitalCity();
    }

    public int getGold() {
        return empire.getGold();
    }

    public int getCityCount() {
        return empire.getCityCount();
    }

    public List<City> getCities() {
        return empire.getCities();
    }

    public List<ArmyGroup> getGroups() {
        return empire.getGroups();
    }

    public List<Hero> getHeroes() {
        return empire.getHeroes();
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        empire.destroy();
        destroyed = true;
    }

    public void start(TurnController controller) {
        controller.beginTurn();
        final City capital = empire.getCapitalCity();
        final String heroName = controller.getFirstHeroName(capital, kingdom.getRandomHeroName());
        if (heroName == null) {
            throw new IllegalStateException("Hero name is null");
        }
        Util.assertNotNull(capital.hireHero(heroName, false));
        controller.selectProduction(capital);
    }

    public void turn(TurnController controller) {
        empire.recalculateGold();
        empire.getGroups().forEach(ArmyGroup::rest);
        controller.beginTurn();
        // Hero hiring.
        hireHero(controller);
        // Delivery report.
        for (final ArmyDelivery delivery : empire.getDeliveries()) {
            if (delivery.deliver()) {
                // NOTE: `delivery` is unregistered at this point!
                controller.reportDelivery(delivery.getSourceCity(), delivery.getTargetCity());
            }
        }
        // Production report.
        final List<City> cities = empire.getCities();
        cities.sort(null);
        for (final City city : cities) {
            final int remainingTime = city.getRemainingTime();
            final boolean produced = city.produce();
            if (produced) {
                if (controller.reportProduction(city)) {
                    if (!city.continueProducing()) {
                        controller.reportNoGoldForProducing();
                    }
                } else {
                    city.stopProducing();
                }
            } else if (remainingTime == 1) {
                // The army can't be produced for some reason.
                // Report it and wait for the next turn.
                controller.reportProductionFailure(city);
            }
        }
    }

    private void hireHero(TurnController controller) {
        final int gold = empire.getGold();
        if (empire.getHeroCount() >= Empire.MAX_HERO_COUNT || gold <= MIN_HERO_PRICE) {
            return;
        }
        // As more gold as more chances for a hero offer.
        if (Util.randomInt(Math.max(MIN_HERO_OFFER_CHANCE - gold / MIN_HERO_PRICE, MAX_HERO_OFFER_CHANCE)) > 0) {
            return;
        }
        final City city = empire.getRandomCity();
        if (city.isFull()) {
            return;
        }
        final int cost = Util.randomInt(MIN_HERO_PRICE, Math.min(gold, MAX_HERO_PRICE));
        final String heroName = controller.getHeroName(city, cost, kingdom.getRandomHeroName());
        if (heroName == null) {
            return;
        }
        Util.assertTrue(empire.pay(cost));
        final Hero hero = city.hireHero(heroName, true);
        Util.assertNotNull(hero);
        final int allyCount = empire.getHeroCount() > 1 ? Util.randomInt(MAX_CITY_ALLY_COUNT + 1) : 0;
        if (allyCount > 0) {
            controller.onAlliesBrought(allyCount);
            hero.joinAllies(kingdom, kingdom.getRandomAllyFactory(), allyCount);
        }
        // BUG: W doesn't update Playing Map.
    }

    public ArmyGroup nextArmyGroup() {
        final List<ArmyGroup> groups = empire.getGroups();
        final int startIndex = groups.indexOf(currentGroup);
        for (int delta = 0; delta < groups.size(); ++delta) {
            final ArmyGroup group = groups.get((startIndex + delta + 1) % groups.size());
            if (group.isActive()) {
                return currentGroup = group;
            }
        }
        return currentGroup = null;
    }

    public int getArmiesReport() {
        int count = 0;
        for (final ArmyGroup group : empire.getGroups()) {
            count += group.getArmyCount();
        }
        return count;
    }

    public int getCitiesReport() {
        return empire.getCityCount() * 6;
    }

    public int getGoldReport() {
        return empire.getGold() / 20;
    }

    public int getProductionReport() {
        if (empire.getCityCount() == 0) {
            return 0;
        }
        int count = 0;
        for (final City city : empire.getCities()) {
            if (city.isProducing()) {
                ++count;
            }
        }
        return count * 100 / empire.getCityCount() * 4;
    }

    public int getWinningReport() {
        // NOTE: W calculates winning in an unknown way.
        return (getArmiesReport() + getCitiesReport() + getGoldReport()) / 3;
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        out.writeRecord(kingdom);
        out.writeRecord(empire);
        out.writeInt(level.ordinal());
        out.writeBoolean(observed);
        out.writeBoolean(destroyed);
        out.writeRecord(currentGroup);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        kingdom = in.readRecord(Kingdom::new);
        empire = in.readRecord(Empire::new);
        level = PlayerLevel.values()[in.readInt()];
        observed = in.readBoolean();
        destroyed = in.readBoolean();
        currentGroup = in.readRecord(ArmyGroup::new);
    }
}
