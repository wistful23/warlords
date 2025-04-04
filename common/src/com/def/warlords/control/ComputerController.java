package com.def.warlords.control;

import com.def.warlords.control.form.CombatForm;
import com.def.warlords.game.*;
import com.def.warlords.game.model.*;
import com.def.warlords.util.Util;

import java.awt.event.KeyEvent;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class ComputerController implements PlayerController {

    private static final int DELAY_TURN = 500;

    private final MainController controller;

    private Computer computer;
    private Player player;

    private boolean stopComputerMode;

    public ComputerController(MainController controller) {
        this.controller = controller;
    }

    public boolean processKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            final Game game = controller.getGame();
            if (game != null && game.isComputerMode()) {
                stopComputerMode = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public void beginTurn() {
        if (!controller.disableActiveContainer()) {
            throw new IllegalStateException("Active container is already disabled");
        }
        computer = new Computer(controller.getGame());
        player = controller.getGame().getCurrentPlayer();
    }

    @Override
    public void playTurn() {
        if (controller.isCurrentPlayerObserved()) {
            controller.showCapital();
        }
        controller.invokeLater(() -> moveNextGroup(true), DELAY_TURN);
    }

    @Override
    public String getFirstHeroName(City city, String initialName) {
        return initialName;
    }

    @Override
    public String getHeroName(City city, int cost, String initialName) {
        return initialName;
    }

    @Override
    public void onAlliesBrought(int count) {
    }

    @Override
    public void reportDelivery(City sourceCity, City targetCity) {
    }

    @Override
    public boolean reportProduction(City city) {
        // Never continue producing.
        return false;
    }

    @Override
    public void reportProductionFailure(City city) {
    }

    @Override
    public void reportNoGoldForProducing() {
    }

    @Override
    public boolean isImproveCityDefenceApproved(City city) {
        // Always improve the city defence.
        return true;
    }

    @Override
    public void onImproveCityDefenceStatus(BuildStatus status) {
    }

    @Override
    public boolean isBuildTowerApproved() {
        // Always build a tower.
        return true;
    }

    @Override
    public void onBuildTowerStatus(BuildStatus status) {
    }

    @Override
    public boolean isRazeApproved(Tile tile) {
        // Always raze a city or a tower.
        return true;
    }

    @Override
    public void onCityRazed(City city) {
    }

    @Override
    public void onTowerRazed() {
    }

    @Override
    public void onTerrainSearch(List<Artifact> artifacts) {
    }

    @Override
    public void onTempleFound(int blessedCount) {
    }

    @Override
    public void onLibraryFound() {
    }

    @Override
    public boolean onSageFound(int gold) {
        // Always ignore the sage.
        return false;
    }

    @Override
    public void onAlliesJoined(Hero hero, GuardType guard, int count) {
    }

    @Override
    public void onGuardFight(Hero hero, GuardType guard, boolean slain) {
    }

    @Override
    public void onArtifactFound(Hero hero, Artifact artifact) {
    }

    @Override
    public boolean onAltarFound() {
        // Always pray at the altar.
        return true;
    }

    @Override
    public void onAltarResult(Hero hero, boolean ignored) {
    }

    @Override
    public boolean onThroneFound() {
        // Always sit in the throne.
        return true;
    }

    @Override
    public void onThroneResult(Hero hero, boolean downgraded) {
    }

    @Override
    public void onGoldFound(Hero hero, int value) {
    }

    @Override
    public void onCombat(ArmyList attackingArmies, ArmyList defendingArmies, Tile tile, List<Boolean> protocol) {
        // NOTE: W displays the combat if the current player attacks a human player.
        if (!controller.isCurrentPlayerObserved() || defendingArmies.getEmpire().isNeutral()) {
            return;
        }
        final InfoScreen infoScreen = controller.getInfoScreen();
        infoScreen.setVisible(false);
        new CombatForm(controller, controller.getGame(), attackingArmies, defendingArmies, tile, protocol).activate();
        infoScreen.setVisible(true);
    }

    @Override
    public void selectProduction(City city) {
    }

    private void moveNextGroup(boolean prevGroupFinished) {
        if (!prevGroupFinished) {
            controller.getPlayingMap().updateArmySelection(Army.State.QUIT);
        }
        if (moveActiveGroup(false) || moveActiveGroup(true)) {
            return;
        }
        final boolean improveDefences = player.getLevel().compareTo(PlayerLevel.LORD) >= 0;
        computer.processCities(player.getCities(), improveDefences);
        endTurn();
    }

    private boolean moveActiveGroup(boolean moveCityGroup) {
        final PlayingMap playingMap = controller.getPlayingMap();
        final boolean searchBuildings = player.getLevel().compareTo(PlayerLevel.BARON) >= 0;
        for (final ArmyGroup group : player.getGroups()) {
            final Tile tile = group.getTile();
            final Hero hero = group.getArmies().getHero();
            if (searchBuildings && hero != null && tile.getArtifactCount() > 0) {
                // Take the artifacts.
                for (final Artifact artifact : tile.getArtifacts()) {
                    hero.takeArtifact(artifact);
                }
            }
            if (searchBuildings && hero != null && hero.isActive()) {
                // Search the building.
                final Building building = tile.getBuilding();
                if (building != null && !building.isExplored()) {
                    playingMap.selectArmyGroup(group, true);
                    controller.getGame().search(playingMap.getArmySelection());
                }
            }
            if (tile.isCity() == moveCityGroup && group.isActive()) {
                playingMap.selectArmyGroup(group, true);
                final Tile target = computer.findTarget(playingMap.getArmySelection(), searchBuildings);
                if (target != null) {
                    Util.assertTrue(playingMap.moveArmySelection(target, false, this::moveNextGroup));
                    return true;
                }
                playingMap.updateArmySelection(Army.State.QUIT);
            }
        }
        return false;
    }

    private void endTurn() {
        if (stopComputerMode) {
            controller.showMessage("Hit ESC to stop computer play");
            // NOTE: W stops the computer mode only if ESC is pressed.
            controller.getGame().setAllPlayersHuman();
            stopComputerMode = false;
        }
        controller.enableActiveContainer();
        controller.endTurn();
    }
}
