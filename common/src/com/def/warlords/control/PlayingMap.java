package com.def.warlords.control;

import com.def.warlords.game.ArmySelection;
import com.def.warlords.game.Game;
import com.def.warlords.game.model.*;
import com.def.warlords.graphics.*;
import com.def.warlords.gui.Component;
import com.def.warlords.util.DeveloperMode;
import com.def.warlords.util.Timer;
import com.def.warlords.util.Util;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Queue;
import java.util.function.Consumer;

import static com.def.warlords.control.common.Dimensions.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class PlayingMap extends Component {

    public enum SearchMode {
        NONE,
        SEARCH,
        DEATH
    }

    private static final int MAP_X = 8;
    private static final int MAP_Y = 10;
    private static final int MAP_WIDTH = 360;
    private static final int MAP_HEIGHT = 320;

    private static final int MAP_WINDOW_WIDTH = 9;
    private static final int MAP_WINDOW_HEIGHT = 8;

    private static final int ARMY_FRAME_COUNT = 4;

    private static final int DELAY_FRAME_ANIMATION = 200;
    private static final int DELAY_ARMY_MOVE = 150;

    private static final int[] num_dx = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
    private static final int[] num_dy = {1, 1, 1, 0, 0, 0, -1, -1, -1};
    private static final int[] arrow_dx = {-1, 0, 1, 0};
    private static final int[] arrow_dy = {0, -1, 0, 1};
    private static final int[] arrow_kp_dx = {0, 0, -1, 1};
    private static final int[] arrow_kp_dy = {-1, 1, 0, 0};

    private final MainController controller;

    // The top left corner position.
    private int py, px;

    private Tile selectedTile;
    private final ArmySelection selection = new ArmySelection();

    private boolean productionMode;
    private boolean razeMode;
    private SearchMode searchMode = SearchMode.NONE;
    private Tile combatTile;

    private Timer armyMoveTimer;
    private Timer armyFrameTimer;
    private int armyFrameIndex;

    public PlayingMap(MainController controller) {
        super(MAP_X, MAP_Y, MAP_WIDTH, MAP_HEIGHT);
        this.controller = controller;
    }

    public void enableProductionMode() {
        productionMode = true;
        selection.reset();
    }

    public void disableProductionMode() {
        productionMode = false;
    }

    public void setRazeMode(boolean razeMode) {
        this.razeMode = razeMode;
    }

    public void setSearchMode(SearchMode searchMode) {
        this.searchMode = searchMode;
    }

    public void setCombatTile(Tile combatTile) {
        this.combatTile = combatTile;
    }

    public int getPosX() {
        return px;
    }

    public int getPosY() {
        return py;
    }

    public int getCenterX() {
        return px + 4;
    }

    public int getCenterY() {
        return py + 4;
    }

    public void setPos(int x, int y) {
        px = x;
        if (px < 0) px = 0;
        if (px + MAP_WINDOW_WIDTH > Kingdom.MAP_WIDTH) {
            px = Kingdom.MAP_WIDTH - MAP_WINDOW_WIDTH;
        }
        py = y;
        if (py < 0) py = 0;
        if (py + MAP_WINDOW_HEIGHT > Kingdom.MAP_HEIGHT) {
            py = Kingdom.MAP_HEIGHT - MAP_WINDOW_HEIGHT;
        }
    }

    public void center(int x, int y) {
        setPos(x - 4, y - 4);
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }

    public ArmySelection getArmySelection() {
        return selection;
    }

    public void centerArmySelection() {
        if (selection.isEmpty()) {
            return;
        }
        center(selection.getSelectedGroup().getPosX(), selection.getSelectedGroup().getPosY());
    }

    public void updateArmySelection(Army.State state) {
        if (selection.isEmpty()) {
            return;
        }
        selection.getSelectedArmies().updateState(state);
    }

    public void selectArmyGroup(ArmyGroup group, boolean activeOnly) {
        if (group == null) {
            selection.reset();
            return;
        }
        selectedTile = null;
        if (controller.isCurrentPlayerObserved()) {
            startArmyFrameAnimation();
            selection.selectAll(group, activeOnly);
            centerArmySelection();
        } else {
            selection.selectAll(group, activeOnly);
        }
    }

    public void nextArmyGroup(Army.State prevArmyState) {
        if (selection.isEmpty() && prevArmyState == Army.State.DEFENDED) {
            return;
        }
        updateArmySelection(prevArmyState);
        selectArmyGroup(controller.getGame().getCurrentPlayer().nextArmyGroup(), false);
    }

    public boolean moveArmySelection(Tile tile, boolean respectEnemies, Consumer<Boolean> callback) {
        if (selection.isEmpty() || selection.getSelectedGroup() == tile.getGroup()) {
            return false;
        }
        final Queue<Tile> path = controller.getGame().findPath(selection, tile, respectEnemies);
        if (path == null) {
            return false;
        }
        Util.assertNull(armyMoveTimer);
        if (controller.isCurrentPlayerObserved()) {
            final boolean disabled = controller.disableActiveContainer();
            armyMoveTimer = controller.createTimer(() -> {
                final boolean finished = path.isEmpty();
                if (finished || !move(path.remove())) {
                    armyMoveTimer = null;
                    if (disabled) {
                        controller.enableActiveContainer();
                    }
                    if (callback != null) {
                        callback.accept(finished);
                    }
                    return;
                }
                armyMoveTimer.start(DELAY_ARMY_MOVE);
            });
            armyMoveTimer.start(DELAY_ARMY_MOVE);
        } else {
            final Consumer<Boolean> callbackLater =
                    finished -> controller.createTimer(() -> callback.accept(finished)).start(0);
            boolean finished = true;
            for (final Tile to : path) {
                if (!controller.getGame().move(selection, to)) {
                    finished = false;
                    break;
                }
            }
            callbackLater.accept(finished);
        }
        return true;
    }

    public void reset() {
        selectedTile = null;
        selection.reset();
    }

    @Override
    public void paint(Graphics g) {
        final SurrenderMode surrenderMode = controller.getSurrenderMode();
        if (surrenderMode != null) {
            g.setColor(Palette.BLACK);
            g.fillRect(MAP_X, MAP_Y, MAP_WIDTH, MAP_HEIGHT);
            Bitmap.drawSprite(g, 13, 14, 350, 312, surrenderMode.getBitmapInfo(), 0, 0);
            return;
        }
        if (razeMode) {
            Bitmap.drawSprite(g, MAP_X, MAP_Y, MAP_WIDTH, MAP_HEIGHT, BitmapInfo.RAZE, 0, 0);
            return;
        }
        final Kingdom kingdom = controller.getGame().getKingdom();
        final ArmyGroup selectedGroup = selection.getSelectedGroup();
        final int selectedArmyCount = selectedGroup != null ? selectedGroup.getSelectedArmyCount() : 0;
        for (int dy = 0; dy < MAP_WINDOW_HEIGHT; ++dy) {
            for (int dx = 0; dx < MAP_WINDOW_WIDTH; ++dx) {
                final int cx = MAP_X + dx * TILE_WIDTH;
                final int cy = MAP_Y + dy * TILE_HEIGHT;
                // Tile.
                final Tile tile = kingdom.getTile(px + dx, py + dy);
                PlayingMapPainter.drawTile(g, cx, cy, tile);
                // Artifact.
                if (tile.getArtifactCount() > 0) {
                    Bitmap.drawSprite(g, cx, cy, TILE_WIDTH, TILE_HEIGHT, BitmapInfo.ARMIES, 192, 332);
                }
                // Army.
                final ArmyGroup group = tile.getGroup();
                if (group != null) {
                    final Army frontArmy;
                    if (group == selectedGroup && selectedArmyCount > 0) {
                        frontArmy = selection.getSelectedArmies().getFirst();
                    } else {
                        frontArmy = group.getArmies().getFirst();
                    }
                    PlayingMapPainter.drawArmyGroup(g, cx, cy, group.getArmyCount(), frontArmy);
                }
            }
        }
        // Selected tile.
        if (DeveloperMode.isOn() && selectedTile != null) {
            final int y = selectedTile.getPosY() - py;
            final int x = selectedTile.getPosX() - px;
            if (y >= 0 && y < MAP_WINDOW_HEIGHT && x >= 0 && x < MAP_WINDOW_WIDTH) {
                g.setColor(Palette.RED);
                g.drawRect(MAP_X + x * TILE_WIDTH, MAP_Y + y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
            }
        }
        // Combat.
        if (combatTile != null) {
            // NOTE: W doesn't display WAR if it's cut.
            for (int dy = -1; dy <= 1; ++dy) {
                for (int dx = -1; dx <= 1; ++dx) {
                    final int y = combatTile.getPosY() + dy - py;
                    final int x = combatTile.getPosX() + dx - px;
                    if (y >= 0 && y < MAP_WINDOW_HEIGHT && x >= 0 && x < MAP_WINDOW_WIDTH) {
                        Bitmap.drawSprite(g, MAP_X + x * TILE_WIDTH, MAP_Y + y * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT,
                                BitmapInfo.ARMIES, SCREEN_WIDTH - TILE_WIDTH * (2 - dx), TILE_HEIGHT * (dy + 1));
                    }
                }
            }
            // Draw only WAR if a combat happens.
            return;
        }
        if (selectedArmyCount > 0) {
            final int y = selectedGroup.getPosY() - py;
            final int x = selectedGroup.getPosX() - px;
            if (y >= 0 && y < MAP_WINDOW_HEIGHT && x >= 0 && x < MAP_WINDOW_WIDTH) {
                final int cx = MAP_X + x * TILE_WIDTH;
                final int cy = MAP_Y + y * TILE_HEIGHT;
                if (searchMode != SearchMode.NONE) {
                    // Search.
                    Bitmap.drawSprite(g, cx, cy, TILE_WIDTH, TILE_HEIGHT,
                            BitmapInfo.ARMIES, 192, 252 + (searchMode == SearchMode.DEATH ? TILE_HEIGHT : 0));
                } else {
                    // Army frame.
                    // BUG: W doesn't display the army frame if the selection tries to attack an enemy in a forbidden
                    // terrain, although the selection retains.
                    final EmpireType empireType = selectedGroup.getEmpire().getType();
                    final Bitmap bitmap =
                            BitmapFactory.getInstance().transformBitmap(BitmapInfo.CURS,
                                    empireType == EmpireType.LORD_BANE ? Palette.GRAY_LIGHT : empireType.getColor());
                    bitmap.drawSprite(g, cx, cy, TILE_WIDTH, TILE_HEIGHT,
                            armyFrameIndex * 64, selectedArmyCount > 1 ? TILE_HEIGHT : 0);
                }
            }
        }
    }

    @Override
    public Cursor getCursor(MouseEvent e) {
        final Game game = controller.getGame();
        final Kingdom kingdom = game.getKingdom();
        final int cx = (e.getX() - MAP_X) / TILE_WIDTH + px;
        final int cy = (e.getY() - MAP_Y) / TILE_HEIGHT + py;
        final Tile tile = kingdom.getTile(cx, cy);
        Util.assertNotNull(tile);
        final Empire empire = game.getCurrentPlayer().getEmpire();
        // Production.
        if (productionMode) {
            return tile.isCity() && tile.isOccupiedBy(empire) ? Cursor.TOWER : Cursor.DEFAULT;
        }
        // Info.
        if (e.isShiftDown()) {
            return Cursor.INFO;
        }
        // Select.
        if (selection.isEmpty()) {
            return tile.getGroup() != null && tile.isOccupiedBy(empire) ? Cursor.TARGET : Cursor.INFO;
        }
        final ArmyGroup group = selection.getSelectedGroup();
        if (tile == group.getTile()) {
            return Cursor.TARGET;
        }
        // Move.
        if (tile.isOccupiedByEnemy(empire)) {
            return Cursor.SWORD;
        }
        // 5\\\|||||///
        // 4\\\\|||////
        // 3\\\\|||////
        // 2-\\\\|////-
        // 1---\\|//---
        // 0-----0-----
        //  54321012345
        final int dx = cx - group.getPosX();
        final int dy = cy - group.getPosY();
        if (dy < 0 && Math.abs(dx) < (1 - dy) / 2) return Cursor.UP;
        if (dy > 0 && Math.abs(dx) < (dy + 1) / 2) return Cursor.DOWN;
        if (dx < 0 && Math.abs(dy) < (1 - dx) / 2) return Cursor.LEFT;
        if (dx > 0 && Math.abs(dy) < (dx + 1) / 2) return Cursor.RIGHT;
        if (dy < 0 && dx < 0) return Cursor.UP_LEFT;
        if (dy < 0 && dx > 0) return Cursor.UP_RIGHT;
        if (dy > 0 && dx < 0) return Cursor.DOWN_LEFT;
        if (dy > 0 && dx > 0) return Cursor.DOWN_RIGHT;
        // This point has to be unreachable.
        Util.fail();
        return Cursor.EMPTY;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        final int keyCode = e.getKeyCode();
        // Next.
        if (keyCode == KeyEvent.VK_N) {
            nextArmyGroup(Army.State.ACTIVE);
            return;
        }
        // Direction.
        int dx = 0;
        int dy = 0;
        if (keyCode >= KeyEvent.VK_1 && keyCode <= KeyEvent.VK_9) {
            dx = num_dx[keyCode - KeyEvent.VK_1];
            dy = num_dy[keyCode - KeyEvent.VK_1];
        } else if (keyCode >= KeyEvent.VK_NUMPAD1 && keyCode <= KeyEvent.VK_NUMPAD9) {
            dx = num_dx[keyCode - KeyEvent.VK_NUMPAD1];
            dy = num_dy[keyCode - KeyEvent.VK_NUMPAD1];
        } else if (keyCode >= KeyEvent.VK_LEFT && keyCode <= KeyEvent.VK_DOWN) {
            dx = arrow_dx[keyCode - KeyEvent.VK_LEFT];
            dy = arrow_dy[keyCode - KeyEvent.VK_LEFT];
        } else if (keyCode >= KeyEvent.VK_KP_UP && keyCode <= KeyEvent.VK_KP_RIGHT) {
            dx = arrow_kp_dx[keyCode - KeyEvent.VK_KP_UP];
            dy = arrow_kp_dy[keyCode - KeyEvent.VK_KP_UP];
        }
        // Move map.
        if (selection.isEmpty()) {
            setPos(px + dx, py + dy);
            return;
        }
        final int sx = selection.getSelectedGroup().getPosX();
        final int sy = selection.getSelectedGroup().getPosY();
        // Center.
        if (keyCode == KeyEvent.VK_SPACE) {
            center(sx, sy);
            return;
        }
        // Move army.
        if (dx != 0 || dy != 0) {
            selectedTile = null;
            final Tile tile = controller.getGame().getKingdom().getTile(sx + dx, sy + dy);
            move(tile);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        selectedTile = null;
        final Game game = controller.getGame();
        final Kingdom kingdom = game.getKingdom();
        final int sx = (e.getX() - MAP_X) / TILE_WIDTH + px;
        final int sy = (e.getY() - MAP_Y) / TILE_HEIGHT + py;
        final Tile tile = kingdom.getTile(sx, sy);
        Util.assertNotNull(tile);
        // Production.
        if (productionMode) {
            controller.getProductionScreen().notifyCity(tile.getCity());
            return;
        }
        // Info.
        if (e.isShiftDown()) {
            selectedTile = tile;
            return;
        }
        // Reset.
        if (!selection.isEmpty() && (e.isControlDown() || e.getButton() == MouseEvent.BUTTON3)) {
            selection.reset();
            return;
        }
        // Move.
        final ArmyGroup group = tile.getGroup();
        if (!selection.isEmpty() && selection.getSelectedGroup() != group) {
            if (kingdom.getNeighborTiles(selection.getSelectedGroup().getTile(), false).contains(tile)) {
                move(tile);
            } else {
                moveArmySelection(tile, true, null);
            }
            return;
        }
        // Select.
        final Empire empire = game.getCurrentPlayer().getEmpire();
        if (group != null && group.getEmpire() == empire) {
            startArmyFrameAnimation();
            if (e.isAltDown() || e.getClickCount() > 1) {
                selection.selectAll(group, false);
            } else {
                selection.select(group);
            }
            updateArmySelection(Army.State.ACTIVE);
            return;
        }
        // Info.
        selectedTile = tile;
    }

    private boolean move(Tile tile) {
        if (selection.isEmpty()) {
            return false;
        }
        final int sx = selection.getSelectedGroup().getPosX();
        final int sy = selection.getSelectedGroup().getPosY();
        if (sx < px || sy < py || sx >= px + MAP_WINDOW_WIDTH || sy >= py + MAP_WINDOW_HEIGHT) {
            // NOTE: W doesn't center the selection if it moves to a forbidden terrain.
            center(sx, sy);
        }
        if (tile == null || !controller.getGame().move(selection, tile)) {
            return false;
        }
        final int nx = tile.getPosX();
        final int ny = tile.getPosY();
        if (nx < px || ny < py || nx >= px + MAP_WINDOW_WIDTH || ny >= py + MAP_WINDOW_HEIGHT) {
            center(nx, ny);
        } else {
            if (nx < sx && sx - px < 5 && px > 0) --px;
            if (nx > sx && sx - px > 4 && px + MAP_WINDOW_WIDTH < Kingdom.MAP_WIDTH) ++px;
            if (ny < sy && sy - py < 5 && py > 0) --py;
            if (ny > sy && sy - py > 4 && py + MAP_WINDOW_HEIGHT < Kingdom.MAP_HEIGHT) ++py;
        }
        return true;
    }

    private void startArmyFrameAnimation() {
        armyFrameIndex = 0;
        if (armyFrameTimer != null) {
            return;
        }
        armyFrameTimer = controller.createTimer(() -> {
            if (selection.isEmpty()) {
                armyFrameTimer.stop();
                armyFrameTimer = null;
                return;
            }
            if (++armyFrameIndex >= ARMY_FRAME_COUNT) {
                armyFrameIndex = 0;
            }
        });
        armyFrameTimer.start(DELAY_FRAME_ANIMATION, true);
    }
}
