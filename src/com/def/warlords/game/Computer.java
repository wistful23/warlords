package com.def.warlords.game;

import com.def.warlords.game.model.*;
import com.def.warlords.util.Util;

import java.util.*;
import java.util.stream.Collectors;

public final class Computer {

    private static final int BLITZ_TURN_COUNT = 10;

    private static final Comparator<Army> COMPARATOR_ARMY_ATTACK =
            Comparator.comparing(Army::isHero).thenComparing(Army::isFlying).thenComparing(Army::isSpecial)
                    .thenComparing(Army::getMovementPoints).thenComparing(Army::getTotalStrength).reversed();

    private static final Comparator<ArmyFactory> COMPARATOR_FACTORY_BLITZ =
            Comparator.comparing(Computer::getFactoryBlitzWeight).thenComparing(Computer::getFactoryAttackWeight);

    private static final Comparator<ArmyFactory> COMPARATOR_FACTORY_DEFENCE =
            Comparator.comparing(Computer::getFactoryDefenceWeight).thenComparing(Computer::getFactoryAttackWeight);

    private static final Comparator<ArmyFactory> COMPARATOR_FACTORY_ATTACK =
            Comparator.comparing(Computer::getFactoryAttackWeight);

    private static int getFactoryBlitzWeight(ArmyFactory factory) {
        return 5 * factory.getTime() - factory.getMovement();
    }

    private static int getFactoryDefenceWeight(ArmyFactory factory) {
        return 5 * factory.getTime() - 3 * factory.getStrength();
    }

    private static int getFactoryAttackWeight(ArmyFactory factory) {
        return 3 * factory.getTime() - 6 * factory.getStrength() - factory.getMovement();
    }

    private static void startProducing(City city, Comparator<ArmyFactory> comparator) {
        if (city.isProducing()) {
            return;
        }
        city.getFactories().stream().filter(Util.not(ArmyFactory::isNavy)).min(comparator)
                .ifPresent(city::startProducing);
    }

    private static void startProducing(City sourceCity, City targetCity, Comparator<ArmyFactory> comparator) {
        Util.assertFalse(sourceCity.isProducing());
        sourceCity.getFactories().stream().filter(Util.not(ArmyFactory::isNavy)).min(comparator)
                .ifPresent(factory -> sourceCity.startProducing(factory, targetCity));
    }

    private static int getCityWeight(City city, int dist) {
        final int cityArmyCount = city.getArmyCount();
        return dist + cityArmyCount - (cityArmyCount == 0 || city.isNeutral() ? ArmyGroup.MAX_ARMY_COUNT : 0);
    }

    private static int getMinCityWeight(int dist) {
        return dist - ArmyGroup.MAX_ARMY_COUNT;
    }

    private static int getAttackingArmyCount(City city, int dist) {
        final int cityArmyCount = city.getArmyCount();
        if (cityArmyCount == 0 || city.isNeutral()) {
            return 1;
        }
        return Math.min(dist / ArmyGroup.MAX_ARMY_COUNT + cityArmyCount + 1, ArmyGroup.MAX_ARMY_COUNT);
    }

    private static int getDefendingArmyCount(int armyCount, int dist) {
        return Math.min(armyCount * ArmyGroup.MAX_ARMY_COUNT / dist, ArmyGroup.MAX_ARMY_COUNT);
    }

    private static void selectSubsetArmies(ArmyList armies, ArmyList subsetArmies) {
        Util.assertTrue(subsetArmies.size() > 0 && subsetArmies.size() <= armies.size());
        if (subsetArmies.size() < armies.size()) {
            armies.forEach(army -> army.setSelected(false));
            subsetArmies.forEach(army -> army.setSelected(true));
        }
    }

    private final Game game;

    public Computer(Game game) {
        this.game = game;
    }

    // Finds and returns the target tile proposed to move the selection.
    // Returns null if no move is proposed.
    // NOTE: This method may change the selection configuration.
    public Tile findTarget(ArmySelection selection, boolean searchBuildings) {
        if (selection.isEmpty()) {
            return null;
        }
        final ArmyList armies = selection.getSelectedArmies();
        final Tile source = selection.getSelectedGroup().getTile();
        final City city = source.getCity();
        if (city == null) {
            // Try to move the rested armies.
            final ArmyList restedArmies =
                    new ArmyList(armies.stream().filter(Army::isRested).collect(Collectors.toList()),
                            armies.getEmpire());
            if (restedArmies.size() > 0 && restedArmies.size() < armies.size()) {
                final Tile target = findTarget(restedArmies, source, searchBuildings, false);
                if (target != null) {
                    selectSubsetArmies(armies, restedArmies);
                    return target;
                }
            }
            // Always move the selection if out of city.
            return findTarget(armies, source, searchBuildings, true);
        }
        final int defendingArmyCount = getDefendingArmyCount(city);
        final int maxArmyCount = city.getArmyCount() - defendingArmyCount;
        if (maxArmyCount > 0) {
            // Leave enough armies to defend the city.
            final ArmyList attackingArmies = maxArmyCount >= armies.getCount() ? armies : new ArmyList(
                    armies.stream().sorted(COMPARATOR_ARMY_ATTACK).limit(maxArmyCount).collect(Collectors.toList()),
                    armies.getEmpire());
            final boolean alwaysMoveHero = defendingArmyCount <= 1 && attackingArmies.getHero() != null;
            if (attackingArmies.stream().anyMatch(Army::isFlying)) {
                // Try to move the flying armies.
                final ArmyList flyingArmies = new ArmyList(
                        attackingArmies.stream().filter(army -> army.isHero() || army.isFlying())
                                .collect(Collectors.toList()),
                        attackingArmies.getEmpire());
                final boolean alwaysMoveFlying =
                        defendingArmyCount <= 1 && flyingArmies.getCount() >= ArmyGroup.MAX_ARMY_COUNT / 2;
                final Tile target =
                        findTarget(flyingArmies, source, searchBuildings, alwaysMoveHero || alwaysMoveFlying);
                if (target != null) {
                    selectSubsetArmies(armies, flyingArmies);
                    return target;
                }
            }
            // Try to move the attacking armies.
            final Tile target = findTarget(attackingArmies, source, searchBuildings, alwaysMoveHero);
            if (target != null) {
                selectSubsetArmies(armies, attackingArmies);
                return target;
            }
        }
        if (armies.getCount() >= ArmyGroup.MAX_ARMY_COUNT / 2 && armies.getCount() % 2 == 0) {
            final Army fastestArmy = armies.stream().filter(Util.not(Army::isHero))
                    .max(Comparator.comparing(Army::getMovementPoints)).orElse(null);
            if (fastestArmy != null) {
                // Try to move the fastest army to capture a neutral or empty city.
                final ArmyList fastestArmies = new ArmyList(1, armies.getEmpire());
                fastestArmies.add(fastestArmy);
                final Tile target = findTarget(fastestArmies, source, ArmyGroup.MAX_ARMY_COUNT, 0, false, false);
                if (target != null) {
                    selectSubsetArmies(armies, fastestArmies);
                    return target;
                }
            }
        }
        if (searchBuildings && armies.getHero() != null) {
            // Try to find artifacts inside the source city.
            for (final Tile tile : city.getTiles()) {
                if (tile.getArtifactCount() > 0 && tile.canLocate(armies)) {
                    return tile;
                }
            }
        }
        final Tile mainTile = city.getMainTile();
        if (mainTile != source && mainTile.canLocate(armies)) {
            // Move the selection to the main tile of the source city.
            return mainTile;
        }
        return null;
    }

    // Calculates the army count needed to defend the city (DAC).
    // NOTE: This method doesn't handle navy and flying enemy groups.
    private int getDefendingArmyCount(City city) {
        int defendingArmyCount = 0;
        final Map<Tile, Integer> distances = new HashMap<>();
        final Queue<Tile> queue = new ArrayDeque<>();
        for (final Tile tile : city.getTiles()) {
            distances.put(tile, 0);
            queue.add(tile);
        }
        while (!queue.isEmpty()) {
            final Tile from = queue.poll();
            final int dist = distances.get(from) + 1;
            if (defendingArmyCount >= getDefendingArmyCount(ArmyGroup.MAX_ARMY_COUNT, dist)) {
                break;
            }
            for (final Tile to : game.getKingdom().getNeighborTiles(from, false)) {
                if (distances.containsKey(to) || to.isWater() || to.isMountain()) {
                    continue;
                }
                distances.put(to, dist);
                queue.add(to);
                // Check non-neutral enemy groups.
                final ArmyGroup group = to.getGroup();
                if (group == null || group.getEmpire().isNeutral() || group.getEmpire() == city.getEmpire()) {
                    continue;
                }
                defendingArmyCount = Math.max(defendingArmyCount, getDefendingArmyCount(group.getArmyCount(), dist));
                if (defendingArmyCount == ArmyGroup.MAX_ARMY_COUNT) {
                    return defendingArmyCount;
                }
            }
        }
        return defendingArmyCount;
    }

    private Tile findTarget(ArmyList armies, Tile source, boolean searchBuildings, boolean allowFallback) {
        return findTarget(armies, source, Integer.MAX_VALUE, ArmyGroup.MAX_ARMY_COUNT / 2,
                searchBuildings, allowFallback);
    }

    private Tile findTarget(ArmyList armies, Tile source,
                            int commonDistLimit, int defenceDistLimit,
                            boolean searchBuildings, boolean allowFallback) {
        final Hero hero = armies.getHero();
        final boolean canHideHero = allowFallback && hero != null && armies.getCount() < ArmyGroup.MAX_ARMY_COUNT / 2;
        final boolean canSearchBuildings = searchBuildings && hero != null;
        final boolean canSearchAllBuildings = canSearchBuildings &&
                hero.getCombatModifier() < 2 && armies.stream().allMatch(army -> army == hero || army.isFlying());
        Tile bestTarget = null;
        Tile fallbackTarget = null;
        int currentCityWeight = Integer.MAX_VALUE;
        final Map<City, Integer> dac = new HashMap<>();
        final Map<Tile, Integer> distances = new HashMap<>();
        final Queue<Tile> queue = new ArrayDeque<>();
        distances.put(source, 0);
        queue.add(source);
        while (!queue.isEmpty()) {
            final Tile from = queue.poll();
            final int dist = distances.get(from) + 1;
            if (dist > commonDistLimit || currentCityWeight <= getMinCityWeight(dist)) {
                return bestTarget != null ? bestTarget : allowFallback ? fallbackTarget : null;
            }
            for (final Tile to : game.getKingdom().getNeighborTiles(from, false)) {
                if (distances.containsKey(to) || armies.getMovementCost(to) == ArmyType.FORBIDDEN_MOVEMENT_COST) {
                    continue;
                }
                distances.put(to, dist);
                queue.add(to);
                if (canSearchAllBuildings || (dist <= ArmyGroup.MAX_ARMY_COUNT && canSearchBuildings)) {
                    // Try to find buildings.
                    final Building building = to.getBuilding();
                    if (building != null && !building.isExplored() && (building.isCrypt() || building.isSage())) {
                        return to;
                    }
                    // Try to find artifacts.
                    if (to.getArtifactCount() > 0 && to.canLocate(armies)) {
                        return to;
                    }
                }
                final City city = to.getCity();
                if (city == null || city == source.getCity()) {
                    // Do nothing inside the source city.
                    continue;
                }
                if (city.getEmpire() == armies.getEmpire()) {
                    // Try to defend the city.
                    if (dist <= defenceDistLimit && to.canLocate(armies) &&
                            city.getArmyCount() < dac.computeIfAbsent(city, this::getDefendingArmyCount)) {
                        return to;
                    }
                    // Try to hide the hero in the city.
                    if (canHideHero && bestTarget == null && dist <= ArmyGroup.MAX_ARMY_COUNT * 2 &&
                            to.canLocate(armies) && dac.computeIfAbsent(city, this::getDefendingArmyCount) > 1) {
                        bestTarget = to;
                    }
                } else {
                    // Try to attack the city.
                    final int cityWeight = getCityWeight(city, dist);
                    if (cityWeight < currentCityWeight) {
                        if (armies.getCount() >= getAttackingArmyCount(city, dist)) {
                            bestTarget = to;
                        }
                        fallbackTarget = to;
                        currentCityWeight = cityWeight;
                    }
                }
            }
        }
        return bestTarget != null ? bestTarget : allowFallback ? fallbackTarget : null;
    }

    // Select the best armies to produce in `cities`.
    public void selectProduction(List<City> cities) {
        final Map<City, Integer> dac = new HashMap<>();
        cities.forEach(city -> dac.put(city, getDefendingArmyCount(city)));
        cities.sort(Comparator.comparing(dac::get));
        // The blitz period.
        if (game.getCurrentTurnCount() <= BLITZ_TURN_COUNT) {
            Util.reverse(cities).forEach(city -> startProducing(city, COMPARATOR_FACTORY_BLITZ));
            return;
        }
        // Produce a defending army in the cities with DAC[8..1].
        for (final City city : Util.reverse(cities)) {
            if (dac.get(city) < 1) {
                break;
            }
            if (city.getArmyCount() >= dac.get(city)) {
                continue;
            }
            startProducing(city, COMPARATOR_FACTORY_DEFENCE);
        }
        // Produce an attacking army in the cities with DAC[8..2].
        for (final City city : Util.reverse(cities)) {
            if (dac.get(city) < 2) {
                break;
            }
            startProducing(city, COMPARATOR_FACTORY_ATTACK);
        }
        // Deliver from the cities with DAC[0..1] to the cities with DAC[8..4].
        for (final City sourceCity : cities) {
            if (dac.get(sourceCity) > 1) {
                break;
            }
            if (sourceCity.isProducing()) {
                continue;
            }
            City targetCity = null;
            for (final City city : Util.reverse(cities)) {
                if (dac.get(city) < 4 || (targetCity != null && dac.get(city) < dac.get(targetCity))) {
                    break;
                }
                if (city.getSourceCityCount() >= City.MAX_SOURCE_CITY_COUNT) {
                    continue;
                }
                if (targetCity == null || city.getSourceCityCount() < targetCity.getSourceCityCount()) {
                    targetCity = city;
                }
            }
            if (targetCity == null) {
                break;
            }
            startProducing(sourceCity, targetCity,
                    targetCity.getArmyCount() < dac.get(targetCity) ? COMPARATOR_FACTORY_DEFENCE
                                                                    : COMPARATOR_FACTORY_ATTACK);
        }
        // Produce a flying army in the cities with DAC[0..1].
        for (final City city : cities) {
            if (dac.get(city) > 1) {
                break;
            }
            if (city.isProducing()) {
                continue;
            }
            city.getFactories().stream().filter(ArmyFactory::isFlying).min(COMPARATOR_FACTORY_ATTACK)
                    .ifPresent(city::startProducing);
        }
    }
}
