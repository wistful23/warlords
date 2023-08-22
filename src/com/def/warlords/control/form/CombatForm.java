package com.def.warlords.control.form;

import com.def.warlords.control.common.Sprites;
import com.def.warlords.game.Game;
import com.def.warlords.game.model.*;
import com.def.warlords.graphics.Cursor;
import com.def.warlords.graphics.Palette;
import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.Image;
import com.def.warlords.gui.Label;
import com.def.warlords.gui.Rectangle;
import com.def.warlords.util.Util;

import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import static com.def.warlords.control.common.Dimensions.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class CombatForm extends Form {

    private static final int DELAY_NEXT_ROUND = 1000;
    private static final int DELAY_AFTER_ROUND = 150;
    private static final int DELAY_REMOVE_ARMY = 300;
    private static final int DELAY_COMPUTER_MESSAGE = 3000;

    private final Game game;
    private final ArmyList attackingArmies, defendingArmies;
    private final Tile tile;
    private final List<Boolean> protocol;

    private Label messageLabel;
    private final Queue<Image> attackingArmyImages = new ArrayDeque<>();
    private final Queue<Image> defendingArmyImages = new ArrayDeque<>();
    private Image killedArmyImage, bloodImage;

    private long lastRoundTime;
    private int roundCount;
    private Timer nextRoundTimer, removeArmyTimer;

    public CombatForm(FormController controller, Game game, ArmyList attackingArmies, ArmyList defendingArmies,
                      Tile tile, List<Boolean> protocol) {
        super(controller);
        this.game = game;
        this.attackingArmies = attackingArmies;
        this.defendingArmies = defendingArmies;
        this.tile = tile;
        this.protocol = protocol;
    }

    @Override
    void init() {
        add(new GrayPanel(41, 70, 293, 200));
        final Empire attackingEmpire = attackingArmies.getEmpire();
        final Empire defendingEmpire = defendingArmies.getEmpire();
        messageLabel = add(new Label(0, 356, SCREEN_WIDTH, Label.Alignment.CENTER));
        if (game.isComputerTurn()) {
            messageLabel.setText(defendingEmpire.getType().getName() + ": you are being attacked!");
        }
        // Landscape.
        add(new Rectangle(408, 10, 224, 224, Palette.BLUE_LIGHT));
        if (tile.isWater()) {
            add(new Rectangle(408, 234, 224, 96, Palette.BLUE));
            add(new Image(435, 208, Sprites.COMBAT_SHIP));
            add(new Image(538, 208, Sprites.COMBAT_SHIP));
        } else {
            add(new Image(408, 200, Sprites.COMBAT_GRASS));
            // NOTE: W always checks the main (top-left) tile of the city.
            final boolean aroundCity = findTerrainAround(tile, TerrainType.CITY);
            final boolean aroundForest = findTerrainAround(tile, TerrainType.FOREST);
            final boolean aroundHill = findTerrainAround(tile, TerrainType.HILL);
            if (aroundCity) {
                add(new Image(474, 162, Sprites.COMBAT_CITY));
                if (aroundForest) {
                    add(new Image(408, 220, Sprites.COMBAT_FOREST));
                } else if (aroundHill) {
                    add(new Image(411, 222, Sprites.COMBAT_FOREGROUND_HILLS));
                }
            } else if (aroundHill) {
                add(new Image(476, 126, Sprites.COMBAT_BACKGROUND_HILLS));
                if (aroundForest) {
                    add(new Image(408, 220, Sprites.COMBAT_FOREST));
                } else {
                    add(new Image(411, 222, Sprites.COMBAT_FOREGROUND_HILLS));
                }
            } else if (aroundForest) {
                add(new Image(408, 220, Sprites.COMBAT_FOREST));
            }
            // Flags.
            if (!defendingEmpire.isNeutral()) {
                add(new Image(475, 260, Sprites.DEFENDER_FLAG_STAND));
                add(new Image(478, 246, Sprites.getDefenderFlagSprite(defendingEmpire.getType())));
            }
            add(new Image(427, 288, Sprites.ATTACKER_FLAG_STAND));
            add(new Image(433, 268, Sprites.getAttackerFlagSprite(attackingEmpire.getType())));
        }
        // Banners.
        if (!defendingEmpire.isNeutral()) {
            add(new Image(568, 38, Sprites.getEmpireBannerSprite(defendingEmpire.getType())));
        }
        add(new Image(432, 38, Sprites.getEmpireBannerSprite(attackingEmpire.getType())));
        // Defending armies.
        final int rowCount = defendingArmies.size() / ArmyGroup.MAX_ARMY_COUNT;
        for (int row = 0; row < rowCount; ++row) {
            for (int column = 0; column < ArmyGroup.MAX_ARMY_COUNT; ++column) {
                final int x = 187 - ArmyGroup.MAX_ARMY_COUNT * ARMY_WIDTH / 2 + column * ARMY_WIDTH;
                final int y = 90 + row * (ARMY_HEIGHT + 2);
                final int index = row * ArmyGroup.MAX_ARMY_COUNT + column;
                defendingArmyImages.add(add(new Image(x, y, Sprites.getArmySprite(defendingArmies.get(index)))));
            }
        }
        final int columnCount = defendingArmies.size() % ArmyGroup.MAX_ARMY_COUNT;
        for (int column = 0; column < columnCount; ++column) {
            final int x = 187 - columnCount * ARMY_WIDTH / 2 + column * ARMY_WIDTH;
            final int y = 90 + rowCount * (ARMY_HEIGHT + 2);
            final int index = rowCount * ArmyGroup.MAX_ARMY_COUNT + column;
            defendingArmyImages.add(add(new Image(x, y, Sprites.getArmySprite(defendingArmies.get(index)))));
        }
        // Attacking armies.
        for (int index = 0; index < attackingArmies.size(); ++index) {
            final int x = 187 - attackingArmies.size() * ARMY_WIDTH / 2 + index * ARMY_WIDTH;
            attackingArmyImages.add(add(new Image(x, 222, Sprites.getArmySprite(attackingArmies.get(index)))));
        }
        // Timers.
        nextRoundTimer = createTimer(DELAY_NEXT_ROUND, e -> nextRound());
        nextRoundTimer.setRepeats(false);
        removeArmyTimer = createTimer(DELAY_REMOVE_ARMY, e -> removeArmy());
        removeArmyTimer.setRepeats(false);
        // Start.
        if (game.isComputerTurn()) {
            nextRoundTimer.start();
        } else {
            nextRound();
        }
    }

    @Override
    public Cursor getCursor(MouseEvent e) {
        return game.isComputerTurn() ? Cursor.EMPTY : roundCount <= protocol.size() ? Cursor.SWORD : Cursor.MODAL;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        nextRound();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        nextRound();
    }

    private void nextRound() {
        if (System.currentTimeMillis() - lastRoundTime < DELAY_AFTER_ROUND) {
            return;
        }
        nextRoundTimer.stop();
        removeArmy();
        if (roundCount < protocol.size()) {
            killArmy();
            nextRoundTimer.start();
        } else if (roundCount == protocol.size()) {
            final Hero hero = attackingArmies.getHero();
            if (game.isComputerTurn()) {
                final String defendingEmpireName = defendingArmies.getEmpire().getType().getName();
                if (defendingArmyImages.isEmpty()) {
                    messageLabel.setText(hero != null ? hero.getName() + " has won the battle!"
                                                      : defendingEmpireName + ": you have lost!");
                } else {
                    messageLabel.setText(defendingEmpireName + ": you are victorious!");
                }
                nextRoundTimer.setInitialDelay(DELAY_COMPUTER_MESSAGE);
                nextRoundTimer.start();
            } else {
                if (defendingArmyImages.isEmpty()) {
                    if (protocol.isEmpty()) {
                        messageLabel.setText("The garrison has fled before you!");
                    } else {
                        messageLabel.setText(hero != null ? hero.getName() + " has won the battle!"
                                                          : "You are victorious!");
                    }
                } else {
                    messageLabel.setText("You have been defeated!");
                }
            }
        } else if (roundCount == protocol.size() + 1) {
            final City city = tile.getCity();
            if (defendingArmyImages.isEmpty() && !game.isComputerTurn() && city != null && !city.isNeutral()) {
                messageLabel.setText("Your armies pillage " + city.getEmpire().getPillagedGold() + " gp!");
            } else {
                deactivate();
                return;
            }
        } else if (roundCount == protocol.size() + 2) {
            deactivate();
            return;
        } else {
            // This point has to be unreachable.
            Util.fail();
        }
        lastRoundTime = System.currentTimeMillis();
        ++roundCount;
    }

    private void killArmy() {
        Util.assertNull(killedArmyImage);
        Util.assertNull(bloodImage);
        killedArmyImage = protocol.get(roundCount) ? defendingArmyImages.remove() : attackingArmyImages.remove();
        bloodImage = add(new Image(killedArmyImage.getX(), killedArmyImage.getY(), Sprites.ARMY_BLOOD));
        removeArmyTimer.start();
    }

    private void removeArmy() {
        if (killedArmyImage == null) {
            return;
        }
        Util.assertNotNull(bloodImage);
        remove(killedArmyImage);
        remove(bloodImage);
        killedArmyImage = bloodImage = null;
        removeArmyTimer.stop();
    }

    private boolean findTerrainAround(Tile tile, TerrainType terrain) {
        for (final Tile neighborTile : game.getKingdom().getNeighborTiles(tile, true)) {
            if (neighborTile.getTerrain() == terrain) {
                return true;
            }
        }
        return false;
    }
}
