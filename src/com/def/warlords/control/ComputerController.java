package com.def.warlords.control;

import com.def.warlords.game.Game;
import com.def.warlords.game.PlayerController;
import com.def.warlords.game.model.*;

import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class ComputerController implements PlayerController {

    private static final int TURN_DELAY = 500;

    private final MainController controller;

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
    }

    @Override
    public void selectProduction(City city) {
    }

    @Override
    public void beginTurn() {
        controller.disableActiveContainer();
    }

    @Override
    public void playTurn() {
        // Currently the computer does nothing.
        controller.showCapital();
        final Timer timer = controller.createTimer(TURN_DELAY, e -> endTurn());
        timer.setRepeats(false);
        timer.start();
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

    private void endTurn() {
        if (stopComputerMode) {
            controller.showMessage("Hit ESC to stop computer play");
            // NOTE: W stops the computer mode only if ESC is pressed.
            controller.getGame().stopComputerMode();
            stopComputerMode = false;
        }
        controller.enableActiveContainer();
        controller.endTurn();
    }
}
