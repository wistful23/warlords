package com.def.warlords.control;

import com.def.warlords.control.common.GameHelper;
import com.def.warlords.game.Computer;
import com.def.warlords.game.Game;
import com.def.warlords.game.model.*;
import com.def.warlords.util.DeveloperMode;
import com.def.warlords.util.Logger;
import com.def.warlords.util.Util;

import java.awt.event.KeyEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class DeveloperController {

    private static final int MAX_DELAY_FACTOR = 5;

    private static final int BONUS = 1000;

    private final MainController controller;

    private int delayFactor = 1;

    public DeveloperController(MainController controller) {
        this.controller = controller;
    }

    public int getAdjustedDelay(int delay) {
        return delay / delayFactor;
    }

    public boolean processKeyEvent(KeyEvent e) {
        if (e.getModifiersEx() != (KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK)) {
            return false;
        }
        final int keyCode = e.getKeyCode();
        // Turn on/off the developer mode.
        if (keyCode == KeyEvent.VK_D) {
            DeveloperMode.invert();
            return true;
        }
        // Turn on/off the verbose logging.
        if (keyCode == KeyEvent.VK_L) {
            Logger.invertVerbose();
            return true;
        }
        // Reduce all delays.
        if (keyCode == KeyEvent.VK_F) {
            delayFactor = delayFactor % MAX_DELAY_FACTOR + 1;
            Logger.dev("Delay factor: " + delayFactor);
            return true;
        }
        final Game game = controller.getGame();
        if (game == null || game.isComputerTurn()) {
            return true;
        }
        final PlayingMap playingMap = controller.getPlayingMap();
        // Make the selected armies rest.
        if (keyCode == KeyEvent.VK_R) {
            final ArmyGroup selectedGroup = playingMap.getArmySelection().getSelectedGroup();
            if (selectedGroup != null) {
                selectedGroup.rest();
            }
        }
        final Kingdom kingdom = game.getKingdom();
        final Empire empire = game.getCurrentPlayer().getEmpire();
        // Capture the whole kingdom.
        if (keyCode == KeyEvent.VK_A) {
            for (final City city : kingdom.getCities()) {
                if (city.getEmpire() != null) {
                    empire.capture(city);
                }
            }
        }
        // Add gold.
        if (keyCode == KeyEvent.VK_G) {
            empire.addGold(BONUS);
        }
        // Produce a hero or ally in the selected tile.
        if ((keyCode == KeyEvent.VK_H) || (keyCode >= KeyEvent.VK_1 && keyCode <= KeyEvent.VK_5)) {
            final Tile tile = playingMap.getSelectedTile();
            if (tile != null) {
                final Army army;
                if (keyCode == KeyEvent.VK_H) {
                    army = new Hero(kingdom.getRandomHeroName(), true);
                } else {
                    final ArmyFactory allyFactory = kingdom.getAllyFactory(keyCode - KeyEvent.VK_1);
                    army = allyFactory.produce();
                }
                DeveloperHelper.locateArmy(empire, tile, army);
            } else {
                Logger.dev("Select tile to locate army");
            }
        }
        // Produce an army in the nearest city.
        if (keyCode >= KeyEvent.VK_F5 && keyCode <= KeyEvent.VK_F8) {
            final int factoryIndex = keyCode - KeyEvent.VK_F5;
            final City city = GameHelper.getNearest(empire.getCities(),
                    playingMap.getCenterX(), playingMap.getCenterY(), false);
            if (factoryIndex < city.getFactoryCount()) {
                final Army army = city.getFactory(factoryIndex).produce();
                DeveloperHelper.locateArmy(empire, city, army);
            }
        }
        final Computer computer = new Computer(game);
        // Move the selected armies by the computer.
        if (keyCode == KeyEvent.VK_M) {
            final Tile target = computer.findTarget(playingMap.getArmySelection(), true);
            if (target != null) {
                Util.assertTrue(playingMap.moveArmySelection(target, false, null));
            } else {
                Logger.dev("No move is proposed");
            }
        }
        // Process the cities by the computer.
        if (keyCode == KeyEvent.VK_C) {
            computer.processCities(game.getCurrentPlayer().getCities(), true);
            Logger.dev("Processed cities");
        }
        return true;
    }
}
