package com.def.warlords.game;

import com.def.warlords.game.model.*;
import com.def.warlords.record.Record;
import com.def.warlords.record.RecordInputStream;
import com.def.warlords.record.RecordOutputStream;
import com.def.warlords.util.Logger;
import com.def.warlords.util.Util;

import java.io.IOException;
import java.util.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class Game implements Record {

    public static final int MAX_PLAYER_COUNT = 8;

    public static final int TOWER_PRICE = 100;

    private static final int ATTACK_MOVEMENT_COST = 2;
    private static final int SEARCH_MOVEMENT_COST = 4;

    private static final int MAX_CRYPT_ALLY_COUNT = 2;

    private static final int MIN_CRYPT_GOLD = 300;
    private static final int MAX_CRYPT_GOLD = 1200;

    private static final int MIN_SAGE_GOLD = 1000;
    private static final int MAX_SAGE_GOLD = 2000;

    private static final int HERO_IGNORE_CHANCE = 2;
    private static final int HERO_DOWNGRADE_CHANCE = 3;

    private static final int INDEX_SHIFT = 3;

    private Kingdom kingdom;
    private final List<Player> players = new ArrayList<>(MAX_PLAYER_COUNT);

    private GameController controller;

    private int currentPlayerIndex = -1;
    private int currentTurnCount;
    // NOTE: Currently this AI option is unused.
    private boolean intenseCombat;

    public Game() {
    }

    public Game(Kingdom kingdom) {
        this.kingdom = kingdom;
    }

    public void setGameController(GameController controller) {
        this.controller = controller;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

    public void addPlayer(EmpireType empireType, PlayerLevel level) {
        if (players.size() == MAX_PLAYER_COUNT) {
            throw new IllegalStateException("Cannot add more than " + MAX_PLAYER_COUNT + " players");
        }
        players.add(new Player(kingdom, kingdom.getEmpire(empireType), level));
    }

    public int getPlayerCount() {
        return players.size();
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getCurrentTurnCount() {
        return currentTurnCount;
    }

    public boolean isIntenseCombat() {
        return intenseCombat;
    }

    public void setIntenseCombat(boolean intenseCombat) {
        this.intenseCombat = intenseCombat;
    }

    public void nextPlayer() {
        // NOTE: W supports the surrender.
        final int prevPlayerIndex = currentPlayerIndex;
        Player currentPlayer;
        boolean turnChanged = false;
        boolean playerDestroyed = false;
        do {
            if (++currentPlayerIndex == players.size()) {
                currentPlayerIndex = 0;
            }
            currentPlayer = players.get(currentPlayerIndex);
            if (!currentPlayer.isDestroyed() && currentPlayer.getCityCount() == 0) {
                currentPlayer.destroy();
                playerDestroyed = true;
                // NOTE: W reports destroyed players before first player turn.
                controller.onPlayerDestroyed(currentPlayer);
            }
            if (currentPlayerIndex == 0) {
                if (turnChanged) {
                    controller.onAllPlayersDestroyed();
                    return;
                }
                turnChanged = true;
                ++currentTurnCount;
            }
            // NOTE: Currently skip all AI players.
        } while (currentPlayer.isDestroyed() || !currentPlayer.isHuman());
        if (playerDestroyed && currentPlayerIndex == prevPlayerIndex) {
            // NOTE: W stops producing armies in all cities.
            controller.onVictory(currentPlayerIndex, currentPlayer);
        }
        if (currentTurnCount == 1) {
            currentPlayer.start(controller);
        } else {
            currentPlayer.turn(controller);
        }
    }

    public void build(ArmySelection selection) {
        if (selection.isEmpty()) {
            return;
        }
        final Tile tile = selection.getSelectedGroup().getTile();
        final City city = tile.getCity();
        if (city != null) {
            improveCityDefence(city);
        } else {
            buildTower(tile);
        }
    }

    private void improveCityDefence(City city) {
        if (city.getDefence() >= City.MAX_DEFENCE) {
            controller.onImproveCityDefenceStatus(GameController.BuildStatus.PROHIBITED);
            return;
        }
        final Empire empire = city.getEmpire();
        final int defencePrice = city.getDefencePrice();
        if (empire.getGold() < defencePrice) {
            controller.onImproveCityDefenceStatus(GameController.BuildStatus.NOT_ENOUGH_GOLD);
            return;
        }
        if (controller.isImproveCityDefenceApproved(city)) {
            Util.assertTrue(empire.pay(defencePrice));
            city.increaseDefence();
            controller.onImproveCityDefenceStatus(GameController.BuildStatus.COMPLETED);
        }
    }

    private void buildTower(Tile tile) {
        if (tile.getTerrain() != TerrainType.PLAIN || tile.getBuilding() != null) {
            controller.onBuildTowerStatus(GameController.BuildStatus.PROHIBITED);
            return;
        }
        final Empire empire = tile.getGroup().getEmpire();
        if (empire.getGold() < TOWER_PRICE) {
            controller.onBuildTowerStatus(GameController.BuildStatus.NOT_ENOUGH_GOLD);
            return;
        }
        if (controller.isBuildTowerApproved()) {
            Util.assertTrue(empire.pay(TOWER_PRICE));
            tile.buildTower(empire);
            controller.onBuildTowerStatus(GameController.BuildStatus.COMPLETED);
        }
    }

    public void raze(ArmySelection selection) {
        if (selection.isEmpty()) {
            return;
        }
        final Tile tile = selection.getSelectedGroup().getTile();
        if (controller.isRazeApproved(tile)) {
            final City city = tile.getCity();
            if (city != null) {
                city.raze();
                controller.onCityRazed(city);
            } else if (tile.isTower()) {
                tile.raze();
                controller.onTowerRazed();
            }
        }
    }

    public void search(ArmySelection selection) {
        if (selection.isEmpty()) {
            return;
        }
        // NOTE: W has a buggy logic of handling movement points, e.g. an army without MP can search with a group.
        final ArmyList armies = selection.getSelectedArmies();
        final Tile tile = selection.getSelectedGroup().getTile();
        final Building building = tile.getBuilding();
        if (building != null) {
            if (building.isTemple() && armies.getMovementPoints() > 0) {
                controller.onTempleFound(armies.bless(building));
                return;
            }
            // Only a hero can search in other types of buildings.
            final Hero hero = armies.getHero();
            if (hero != null && hero.getMovementPoints() > 0) {
                if (building.isLibrary()) {
                    controller.onLibraryFound();
                    hero.move(SEARCH_MOVEMENT_COST);
                    return;
                }
                if (building.isSage()) {
                    final int gold = !building.isExplored() ? Util.randomInt(MIN_SAGE_GOLD, MAX_SAGE_GOLD) : 0;
                    if (controller.onSageFound(gold)) {
                        hero.move(SEARCH_MOVEMENT_COST);
                    }
                    building.setExplored(true);
                    return;
                }
                if (building.isCrypt() && !building.isExplored()) {
                    if (searchCrypt((Crypt) building, hero)) {
                        // Spend all movement points.
                        hero.reset();
                        building.setExplored(true);
                    }
                    selection.reset();
                    return;
                }
            }
        }
        if (armies.getMovementPoints() > 0) {
            // Search lost artifacts.
            controller.onTerrainSearch(tile.getArtifacts());
            armies.move(SEARCH_MOVEMENT_COST);
        }
    }

    // Returns false if the hero is slain.
    private boolean searchCrypt(Crypt crypt, Hero hero) {
        final CryptType type = crypt.getCryptType();
        final GuardType guard = crypt.getGuardType();
        if (type == CryptType.ALLIES) {
            final int allyCount = Util.randomInt(MAX_CRYPT_ALLY_COUNT) + 1;
            controller.onAlliesJoined(hero, guard, allyCount);
            hero.joinAllies(kingdom, kingdom.getAllyFactory(guard), allyCount);
            return true;
        }
        // NOTE: W increases the chance of survival if the hero has armies.
        final boolean slain = Util.randomInt(hero.getTotalStrength() + 1) == 0;
        controller.onGuardFight(hero, guard, slain);
        final Tile tile = hero.getGroup().getTile();
        if (slain) {
            hero.kill(tile);
            return false;
        }
        switch (type) {
            case ARTIFACT:
                final Artifact artifact = crypt.getArtifact();
                controller.onArtifactFound(hero, artifact);
                Util.assertTrue(tile.locate(artifact));
                break;
            case ALTAR:
                if (controller.onAltarFound()) {
                    final boolean ignored = Util.randomInt(HERO_IGNORE_CHANCE) == 0;
                    if (!ignored) {
                        hero.increaseStrength();
                    }
                    controller.onAltarResult(hero, ignored);
                }
                break;
            case THRONE:
                if (controller.onThroneFound()) {
                    final boolean downgraded = Util.randomInt(HERO_DOWNGRADE_CHANCE) == 0;
                    if (downgraded) {
                        hero.decreaseStrength();
                    } else {
                        hero.increaseStrength();
                    }
                    controller.onThroneResult(hero, downgraded);
                }
                break;
            case GOLD:
                final int value = Util.randomInt(MIN_CRYPT_GOLD, MAX_CRYPT_GOLD);
                controller.onGoldFound(hero, value);
                hero.getEmpire().addGold(value);
                break;
            default:
                Util.fail();
        }
        return true;
    }

    // Finds and returns a path to `target` tile.
    public Queue<Tile> findPath(ArmySelection selection, Tile target) {
        if (selection.isEmpty()) {
            return null;
        }
        final ArmyList armies = selection.getSelectedArmies();
        if (armies.getMovementCost(target.getTerrain()) == ArmyType.FORBIDDEN_MOVEMENT_COST ||
                target.isOccupiedByEnemy(armies.getEmpire())) {
            return null;
        }
        final Tile source = selection.getSelectedGroup().getTile();
        final Map<Tile, Integer> valuesIgnoreGroups = findPath(armies, source, target, true);
        if (!valuesIgnoreGroups.containsKey(target)) {
            return null;
        }
        Map<Tile, Integer> values = valuesIgnoreGroups;
        final Map<Tile, Integer> valuesRespectGroups = findPath(armies, source, target, false);
        if (valuesRespectGroups.containsKey(target)) {
            final int movementCostRespectGroups = valuesRespectGroups.get(target) >> INDEX_SHIFT;
            final int movementCostIgnoreGroups = valuesIgnoreGroups.get(target) >> INDEX_SHIFT;
            Util.assertTrue(movementCostRespectGroups >= movementCostIgnoreGroups);
            if (movementCostRespectGroups == movementCostIgnoreGroups) {
                values = valuesRespectGroups;
            }
        }
        final Deque<Tile> path = new ArrayDeque<>();
        while (target != source) {
            path.addFirst(target);
            target = kingdom.getNeighborTile(target, values.get(target) & ((1 << INDEX_SHIFT) - 1), -1);
        }
        return path;
    }

    // Moves the selection from one tile to another.
    // Returns true if the selection is moved successfully.
    public boolean move(ArmySelection selection, Tile tile) {
        if (selection.isEmpty()) {
            return false;
        }
        final Empire empire = selection.getSelectedGroup().getEmpire();
        final ArmyList armies = selection.getSelectedArmies();
        final ArmyGroup group = tile.getGroup();
        TerrainType terrain = tile.getTerrain();
        if (group != null && group.getEmpire() == empire) {
            // Friendly group.
            if (group.getArmyCount() + armies.getCount() > ArmyGroup.MAX_ARMY_COUNT) {
                // Overflow. Don't reset the selection.
                return false;
            }
            if (group.isNavy() && !armies.isNavy()) {
                // Boarding a ship. Using the PLAIN terrain since any army spends 2 MP when boarding.
                terrain = TerrainType.PLAIN;
            }
        }
        int movementCost = armies.getMovementCost(terrain);
        if (movementCost == ArmyType.FORBIDDEN_MOVEMENT_COST) {
            // The selection can't move to the terrain. Don't reset the selection.
            return false;
        }
        if (tile.isOccupiedByEnemy(empire)) {
            movementCost = ATTACK_MOVEMENT_COST;
        }
        if (armies.getMovementPoints() < movementCost) {
            // Not enough movement points to move.
            armies.updateState(Army.State.QUIT);
            selection.reset();
            return false;
        }
        if (!tile.isOccupied() || tile.isOccupiedBy(empire)) {
            // Make a regular movement.
            move(selection, tile, movementCost);
            return true;
        }
        final City city = tile.getCity();
        if (city != null) {
            // Attack a city.
            if (attack(armies, city.getArmies(), tile)) {
                empire.capture(city);
                // NOTE: W doesn't quit the selection if no movement points.
                move(selection, tile, movementCost);
                // Always reset the selection.
                selection.reset();
                controller.selectProduction(city);
                return true;
            }
        } else if (group != null) {
            // Attack an army group.
            if (attack(armies, group.getArmies(), tile)) {
                // NOTE: W doesn't quit the selection if no movement points.
                move(selection, tile, movementCost);
                return true;
            }
        }
        // The selected armies were defeated.
        selection.reset();
        return false;
    }

    // Moves the selection to the tile spending `movementCost`.
    private void move(ArmySelection selection, Tile tile, int movementCost) {
        selection.moveTo(tile);
        final ArmyList armies = selection.getSelectedArmies();
        armies.move(movementCost);
        if (armies.getMovementPoints() == 0) {
            // Quit the selection if no movement points.
            armies.updateState(Army.State.QUIT);
            selection.reset();
        }
        // Conquer towers.
        for (final Tile neighborTile : kingdom.getNeighborTiles(tile, true)) {
            if (neighborTile.isTower() && !neighborTile.isOccupied()) {
                neighborTile.buildTower(armies.getEmpire());
            }
        }
    }

    // Finds the shortest path to `target` tile. Ignores friendly groups if `ignoreGroups` is true.
    private Map<Tile, Integer> findPath(ArmyList armies, Tile source, Tile target, boolean ignoreGroups) {
        final Map<Tile, Integer> values = new HashMap<>();
        final Queue<Tile> queue = new PriorityQueue<>(Comparator.comparing(values::get));
        values.put(source, 0);
        queue.add(source);
        while (!queue.isEmpty() && queue.peek() != target) {
            final Tile from = queue.poll();
            final List<Tile> neighborTiles = kingdom.getNeighborTiles(from, false);
            for (int index = 0; index < neighborTiles.size(); ++index) {
                final Tile to = neighborTiles.get(index);
                final int movementCost = armies.getMovementCost(to.getTerrain());
                if (movementCost == ArmyType.FORBIDDEN_MOVEMENT_COST || to.isOccupiedByEnemy(armies.getEmpire())) {
                    continue;
                }
                if (!ignoreGroups) {
                    // Always friendly group.
                    final ArmyGroup group = to.getGroup();
                    if (group != null && group.getArmyCount() + armies.getCount() > ArmyGroup.MAX_ARMY_COUNT) {
                        continue;
                    }
                }
                final int value = (((values.get(from) >> INDEX_SHIFT) + movementCost) << INDEX_SHIFT) + index;
                if (value < values.getOrDefault(to, Integer.MAX_VALUE)) {
                    values.put(to, value);
                    queue.add(to);
                }
            }
        }
        return values;
    }

    // Returns true if `attackingArmies` win.
    // After calling this method, `attackingArmies` and `defendingArmies` may contain unregistered armies.
    private boolean attack(ArmyList attackingArmies, ArmyList defendingArmies, Tile tile) {
        Util.assertFalse(attackingArmies.isEmpty());
        attackingArmies.arrange(true);
        defendingArmies.arrange(true);
        final List<Boolean> combatProtocol = new ArrayList<>();
        int attackingArmyHealth = 0;
        int defendingArmyHealth = 0;
        int attackingArmyStrength = 0;
        int defendingArmyStrength = 0;
        int attackingArmyIndex = 0;
        int defendingArmyIndex = 0;
        final int afcm = attackingArmies.afcm(tile);
        final int dfcm = defendingArmies.dfcm(tile);
        Logger.info("COMBAT AFCM=" + afcm + " DFCM=" + dfcm);
        while (true) {
            if (attackingArmyHealth == 0) {
                if (attackingArmyIndex == attackingArmies.size()) {
                    break;
                }
                final Army army = attackingArmies.get(attackingArmyIndex++);
                attackingArmyStrength = army.getTotalStrength(tile, afcm);
                attackingArmyHealth = Army.MAX_HEALTH;
                Logger.info(army.getName() + " AS=" + attackingArmyStrength);
            }
            if (defendingArmyHealth == 0) {
                if (defendingArmyIndex == defendingArmies.size()) {
                    break;
                }
                final Army army = defendingArmies.get(defendingArmyIndex++);
                defendingArmyStrength = army.getTotalStrength(tile, dfcm);
                defendingArmyHealth = Army.MAX_HEALTH;
                Logger.info(army.getName() + " DS=" + defendingArmyStrength);
            }
            // Fight.
            Logger.info("AS=" + attackingArmyStrength + " AH=" + attackingArmyHealth +
                    " vs DS=" + defendingArmyStrength + " DH=" + defendingArmyHealth);
            while (attackingArmyHealth > 0 && defendingArmyHealth > 0) {
                final int attackingRoll = Util.randomInt(Army.MAX_STRENGTH + 1) + 1;
                final int defendingRoll = Util.randomInt(Army.MAX_STRENGTH + 1) + 1;
                String resultLog = "draw";
                if (attackingRoll > defendingArmyStrength && defendingRoll <= attackingArmyStrength) {
                    --defendingArmyHealth;
                    resultLog = "A hits";
                }
                if (defendingRoll > attackingArmyStrength && attackingRoll <= defendingArmyStrength) {
                    --attackingArmyHealth;
                    resultLog = "D hits";
                }
                Logger.info("AR=" + attackingRoll + " DR=" + defendingRoll + " " + resultLog);
            }
            Logger.info((defendingArmyHealth > 0
                    ? attackingArmies.get(attackingArmyIndex - 1).getName()
                    : defendingArmies.get(defendingArmyIndex - 1).getName()) + " KILLED");
            combatProtocol.add(attackingArmyHealth > 0);
        }
        controller.onCombat(attackingArmies, defendingArmies, tile, combatProtocol);
        attackingArmyIndex = 0;
        defendingArmyIndex = 0;
        for (final boolean attackingArmyWon : combatProtocol) {
            if (attackingArmyWon) {
                defendingArmies.get(defendingArmyIndex++).kill(tile);
            } else {
                attackingArmies.get(attackingArmyIndex++).kill(tile);
            }
        }
        return attackingArmyHealth > 0;
    }

    @Override
    public void write(RecordOutputStream out) throws IOException {
        out.writeRecord(kingdom);
        out.writeRecordList(players);
        out.writeInt(currentPlayerIndex);
        out.writeInt(currentTurnCount);
        out.writeBoolean(intenseCombat);
    }

    @Override
    public void read(RecordInputStream in) throws IOException {
        kingdom = in.readRecord(Kingdom::new);
        in.readRecordList(players, Player::new);
        currentPlayerIndex = in.readInt();
        currentTurnCount = in.readInt();
        intenseCombat = in.readBoolean();
    }
}
