package com.def.warlords.control;

import com.def.warlords.control.common.Dimensions;
import com.def.warlords.game.ArmySelection;
import com.def.warlords.game.Game;
import com.def.warlords.game.model.*;
import com.def.warlords.graphics.Font;
import com.def.warlords.graphics.FontFactory;
import com.def.warlords.gui.Container;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class InfoScreen extends Container {

    private final MainController controller;

    public InfoScreen(MainController controller) {
        super(0, 332, 640, 68);
        this.controller = controller;
    }

    @Override
    public void paint(Graphics g) {
        // NOTE: W doesn't support dynamic updates.
        final Font font = FontFactory.getInstance().getGothicFont();
        final Game game = controller.getGame();
        final Empire empire = game.getCurrentPlayer().getEmpire();
        if (game.isComputerTurn()) {
            final EmpireType empireType = empire.getType();
            final boolean singular = empireType == EmpireType.ELVALLIE || empireType == EmpireType.LORD_BANE;
            final String text = empire.getName() + (singular ? " is" : " are") + " moving!";
            font.drawString(g, (Dimensions.SCREEN_WIDTH - font.getLength(text) + 1) / 2, 356, text);
            return;
        }
        Command command = Command.NONE;
        final Tile tile = controller.getPlayingMap().getSelectedTile();
        final ArmySelection selection = controller.getPlayingMap().getArmySelection();
        if (tile != null) {
            command = Command.INFO;
        } else if (!selection.isEmpty()) {
            command = Command.MOVE;
        }
        font.drawString(g, 81, 336, "Name:");
        font.drawString(g, 143, 336, empire.getName());
        font.drawString(g, 410, 336, "Command:");
        font.drawString(g, 503, 336, command.getName());
        switch (command) {
            case NONE:
                font.drawString(g, 72, 356, "Income:");
                font.drawString(g, 143, 356, empire.getIncome() + " gp");
                font.drawString(g, 86, 376, "Cities:");
                font.drawString(g, 143, 376, empire.getCityCount() + "");
                font.drawString(g, 251, 356, "Gold:");
                font.drawString(g, 301, 356, empire.getGold() + " gp");
                font.drawString(g, 247, 376, "Turn:");
                font.drawString(g, 301, 376, game.getCurrentTurnCount() + "");
                font.drawString(g, 412, 356, "Upkeep:");
                font.drawString(g, 483, 356, empire.getUpkeep() + " gp");
                break;
            case INFO:
                final City city = tile.getCity();
                if (city != null) {
                    font.drawString(g, 100, 356, "City:");
                    font.drawString(g, 143, 356, city.getName());
                    font.drawString(g, 63, 376, "Defence:");
                    font.drawString(g, 143, 376, city.getDefence() + "");
                    font.drawString(g, 415, 356, "Owner:");
                    font.drawString(g, 483, 356, city.getEmpire().getName());
                    font.drawString(g, 410, 376, "Income:");
                    font.drawString(g, 483, 376, city.getIncome() + " gp");
                } else {
                    final Building building = tile.getBuilding();
                    if (building != null) {
                        font.drawString(g, 57, 356, "Location:");
                        font.drawString(g, 143, 356, building.getName());
                        font.drawString(g, 416, 356, "Status:");
                        font.drawString(g, 483, 356,
                                building.isCrypt() ? building.isExplored() ? "Explored" : "Unexplored"
                                                   : building.getType().getName());
                    } else {
                        font.drawString(g, 65, 356, "Terrain:");
                        font.drawString(g, 143, 356, tile.getTerrain().getName());
                    }
                    font.drawString(g, 77, 376, "Lands:");
                    // NOTE: W checks the lands by the top left tile of Playing Map.
                    font.drawString(g, 143, 376, Kingdom.getLands(tile));
                }
                break;
            case MOVE:
                final ArmyList armies = selection.getSelectedArmies();
                font.drawString(g, 84, 356, "Move:");
                font.drawString(g, 143, 356, armies.getMovementPoints() + "");
                font.drawString(g, 82, 376, "Army:");
                font.drawString(g, 143, 376, armies.size() > 1 ? "Group" : armies.getFirst().getName());
                font.drawString(g, 395, 356, "Strength:");
                font.drawString(g, 483, 356, armies.size() > 1 ? "-" : armies.getFirst().getStrength() + "");
                break;
        }
    }

    // NOTE: W doesn't use most of the commands.
    //  +   Move Army
    //      Battle
    //      Move Map
    //  *   Select Army
    //  *   Group
    //  +   Info
    //  *   View
    //      Production
    //      Build
    private enum Command {

        NONE("None"),
        INFO("Info"),
        MOVE("Move Army");

        private final String name;

        Command(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
