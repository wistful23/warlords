package com.def.warlords.control;

import com.def.warlords.control.common.GameHelper;
import com.def.warlords.control.form.*;
import com.def.warlords.control.menu.MenuController;
import com.def.warlords.control.menu.MenuStrip;
import com.def.warlords.game.ArmySelection;
import com.def.warlords.game.Game;
import com.def.warlords.game.GameController;
import com.def.warlords.game.Player;
import com.def.warlords.game.model.*;
import com.def.warlords.graphics.Bitmap;
import com.def.warlords.graphics.BitmapFactory;
import com.def.warlords.graphics.BitmapInfo;
import com.def.warlords.graphics.Cursor;
import com.def.warlords.gui.Container;
import com.def.warlords.platform.PlatformHolder;
import com.def.warlords.sound.Sound;
import com.def.warlords.sound.SoundInfo;
import com.def.warlords.util.Logger;
import com.def.warlords.util.Timer;
import com.def.warlords.util.Toggle;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import static com.def.warlords.control.common.Dimensions.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class MainController implements FormController, MenuController, GameController {

    private final DeveloperController developerController = new DeveloperController(this);

    private Bitmap mainBitmap;
    private Container activeContainer;
    private Form activeForm;

    private final Container mainContainer = new Container(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

    private final Toggle observeToggle = new Toggle(true);
    private final Toggle soundToggle = new Toggle(true);

    // A - Playing Map
    // B - Strategic Map
    // C - Info/Production Screen
    // D - Command Bar
    // E - Menu Strip
    private final PlayingMap playingMap = new PlayingMap(this);
    private final StrategicMap strategicMap = new StrategicMap(this);
    private final InfoScreen infoScreen = new InfoScreen(this);
    private final ProductionScreen productionScreen = new ProductionScreen(this);
    private final CommandBar commandBar = new CommandBar(this);
    private final MenuStrip menuStrip = new MenuStrip(this);

    private final HumanController humanController = new HumanController(this);
    private final ComputerController computerController = new ComputerController(this);

    private Game game;

    private SurrenderMode surrenderMode;

    private int currentRecordIndex = -1;

    public MainController() {
        mainContainer.add(playingMap);
        mainContainer.add(strategicMap);
        mainContainer.add(infoScreen);
        mainContainer.add(productionScreen);
        mainContainer.add(commandBar);
        mainContainer.add(menuStrip);
        productionScreen.setVisible(false);
    }

    public void start() {
        mainBitmap = BitmapFactory.getInstance().fetchBitmap(BitmapInfo.MAIN);
        new SoundForm(this, SoundInfo.HORN).activate();
        mainBitmap = BitmapFactory.getInstance().transformMainBitmap();
        activeContainer = new SetupContainer(this);
    }

    // Form controller.
    @Override
    public void activateForm(Form form) {
        if (activeForm != null) {
            throw new IllegalStateException("Form " + activeForm + " is already active");
        }
        activeForm = form;
        PlatformHolder.getPlatform().startSecondaryLoop();
    }

    @Override
    public void deactivateForm(Form form) {
        if (activeForm != form) {
            return;
        }
        activeForm = null;
        PlatformHolder.getPlatform().stopSecondaryLoop();
    }

    @Override
    public Sound getSound(SoundInfo soundInfo, Runnable listener) {
        if (soundToggle.isOff()) {
            return null;
        }
        try {
            return PlatformHolder.getPlatform().getSound("sound/" + soundInfo.getFileName(), listener);
        } catch (IOException e) {
            Logger.error("Cannot get sound for " + soundInfo);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Timer createTimer(Runnable listener) {
        return new Timer(() -> PlatformHolder.getPlatform().invokeLater(listener, 0));
    }

    @Override
    public void invokeLater(Runnable action, int delay) {
        PlatformHolder.getPlatform().invokeLater(action, developerController.getAdjustedDelay(delay));
    }

    public void enableActiveContainer() {
        if (activeContainer.isEnabled()) {
            throw new IllegalStateException("Active container is already enabled");
        }
        activeContainer.setEnabled(true);
    }

    public boolean disableActiveContainer() {
        if (!activeContainer.isEnabled()) {
            return false;
        }
        activeContainer.setEnabled(false);
        return true;
    }

    public void showMessage(String text) {
        showMessage(text, false);
    }

    public void showMessage(String text, boolean timed) {
        if (!infoScreen.isVisible()) {
            throw new IllegalStateException("Info Screen has to be visible");
        }
        infoScreen.setVisible(false);
        new MessageForm(this, text, timed).activate();
        infoScreen.setVisible(true);
    }

    public void activateProductionScreen(City city) {
        productionScreen.init(city);
        // Disable controls.
        commandBar.setEnabled(false);
        menuStrip.setEnabled(false);
        // Enable the production mode.
        strategicMap.setMode(StrategicMap.Mode.PRODUCTION_SOURCE);
        strategicMap.setSourceCity(city);
        playingMap.enableProductionMode();
        // Switch screens.
        infoScreen.setVisible(false);
        productionScreen.setVisible(true);
    }

    public void deactivateProductionScreen(String message) {
        // Switch screens.
        productionScreen.setVisible(false);
        infoScreen.setVisible(true);
        if (message != null) {
            showMessage(message);
        }
        productionScreen.reset();
        commandBar.reset();
        // Disable the production mode.
        strategicMap.setMode(StrategicMap.Mode.CITIES);
        playingMap.disableProductionMode();
        // Enable controls.
        commandBar.setEnabled(true);
        menuStrip.setEnabled(true);
    }

    public PlayingMap getPlayingMap() {
        return playingMap;
    }

    public StrategicMap getStrategicMap() {
        return strategicMap;
    }

    public InfoScreen getInfoScreen() {
        return infoScreen;
    }

    public ProductionScreen getProductionScreen() {
        return productionScreen;
    }

    public Game getGame() {
        return game;
    }

    public void startGame(List<PlayerParams> playerParams) {
        new SoundForm(this, SoundInfo.DRUMROLL).activate();
        final Kingdom kingdom = new Kingdom();
        if (!kingdom.init()) {
            Logger.error("Failed to initialize Kingdom");
            return;
        }
        game = new Game(kingdom);
        game.setControllers(humanController, computerController);
        for (final PlayerParams params : playerParams) {
            game.addPlayer(params.getEmpireType(), params.getLevel());
        }
        activeContainer = mainContainer;
        game.nextPlayer(this);
    }

    public boolean isCurrentPlayerObserved() {
        if (observeToggle.isOn()) {
            return true;
        }
        final Player player = game.getCurrentPlayer();
        return player.isHuman() || player.isObserved();
    }

    public SurrenderMode getSurrenderMode() {
        return surrenderMode;
    }

    // Command Bar Controller.
    public void showArmies() {
        strategicMap.setMode(StrategicMap.Mode.ARMIES);
        showMessage("Your armies ...");
        strategicMap.setMode(StrategicMap.Mode.CITIES);
    }

    // Menu Controller.
    @Override
    public void about() {
        infoScreen.setVisible(false);
        new AboutForm(this).activate();
        infoScreen.setVisible(true);
    }

    @Override
    public Toggle getObserveToggle() {
        return observeToggle;
    }

    @Override
    public Toggle getSoundToggle() {
        return soundToggle;
    }

    @Override
    public void saveGame() {
        final RecordForm recordsForm = new RecordForm(this, RecordType.SAVE,
                RecordHelper.loadRecordHeadlines(), currentRecordIndex);
        final int index = recordsForm.getResult();
        if (index >= 0 && RecordHelper.save(index, recordsForm.getCurrentHeadline(),
                playingMap.getPosX(), playingMap.getPosY(), game)) {
            currentRecordIndex = index;
        }
    }

    @Override
    public void loadGame() {
        final int index = new RecordForm(this, RecordType.LOAD,
                RecordHelper.loadRecordHeadlines(), currentRecordIndex).getResult();
        if (index < 0) {
            return;
        }
        final RecordData recordData = RecordHelper.load(index);
        if (recordData == null) {
            return;
        }
        playingMap.reset();
        playingMap.setPos(recordData.getPosX(), recordData.getPosY());
        game = recordData.getGame();
        game.setControllers(humanController, computerController);
        currentRecordIndex = index;
        // Ensure the main container is active.
        activeContainer = mainContainer;
    }

    @Override
    public void quit() {
        new QuitForm(this).activate();
    }

    @Override
    public void build() {
        game.build(playingMap.getArmySelection());
    }

    @Override
    public void showCapital() {
        final City capital = game.getCurrentPlayer().getCapitalCity();
        if (capital != null) {
            playingMap.center(capital.getPosX(), capital.getPosY());
        }
    }

    @Override
    public void disband() {
        final ArmySelection selection = playingMap.getArmySelection();
        if (selection.isEmpty()) {
            return;
        }
        final ArmyList armies = selection.getSelectedArmies();
        if (armies.isHeroList()) {
            showMessage("You cannot disband heroes!");
        } else {
            if (new DisbandResultForm(this).getResult()) {
                armies.disband();
            }
        }
    }

    @Override
    public void raze() {
        game.raze(playingMap.getArmySelection());
    }

    @Override
    public void showArmiesReport() {
        showStatReport(StatReportType.ARMIES);
    }

    @Override
    public void showCitiesReport() {
        showStatReport(StatReportType.CITIES);
    }

    @Override
    public void showGoldReport() {
        showStatReport(StatReportType.GOLD);
    }

    @Override
    public void showHatredsReport() {
        infoScreen.setVisible(false);
        new HatredReportForm(this, game).activate();
        infoScreen.setVisible(true);
    }

    @Override
    public void showProductionReport() {
        showStatReport(StatReportType.PRODUCTION);
    }

    @Override
    public void showWinningReport() {
        showStatReport(StatReportType.WINNING);
    }

    @Override
    public void dropItem() {
        final ArmySelection armySelection = playingMap.getArmySelection();
        final Hero hero = !armySelection.isEmpty() ? armySelection.getSelectedArmies().getHero() : null;
        if (hero == null) {
            showMessage("Hero must be selected to drop an item");
            return;
        }
        final Artifact artifact = showArtifactList("Dropping an item ...", hero.getArtifacts());
        if (artifact != null) {
            hero.dropArtifact(artifact);
        }
    }

    @Override
    public void findHero() {
        strategicMap.setMode(StrategicMap.Mode.HEROES);
        showMessage("Your heroes...");
        strategicMap.setMode(StrategicMap.Mode.CITIES);
    }

    @Override
    public void showInventory() {
        final ArmySelection armySelection = playingMap.getArmySelection();
        final Hero hero = !armySelection.isEmpty() ? armySelection.getSelectedArmies().getHero() : null;
        if (hero == null) {
            showMessage("Hero must be selected to Inventory");
            return;
        }
        showArtifactList("Inventory of items ...", hero.getArtifacts());
    }

    @Override
    public void search() {
        // NOTE: W doesn't center the army selection if it is not visible.
        game.search(playingMap.getArmySelection());
    }

    @Override
    public void takeItem() {
        final ArmySelection armySelection = playingMap.getArmySelection();
        final Hero hero = !armySelection.isEmpty() ? armySelection.getSelectedArmies().getHero() : null;
        if (hero == null) {
            showMessage("Hero must be selected to take an item");
            return;
        }
        final Artifact artifact = showArtifactList("Taking an item ...", hero.getGroup().getTile().getArtifacts());
        if (artifact != null) {
            hero.takeArtifact(artifact);
        }
    }

    @Override
    public void showArmySelection() {
        final ArmySelection selection = playingMap.getArmySelection();
        if (!selection.isEmpty()) {
            new SelectionForm(this, selection.getSelectedGroup().getArmies()).activate();
        }
    }

    @Override
    public void showProduction() {
        final City city = GameHelper.getNearest(game.getCurrentPlayer().getCities(),
                playingMap.getPosX() + 4, playingMap.getPosY() + 4, false);
        if (city != null) {
            new ProductionExForm(this, city).activate();
        }
    }

    @Override
    public void showHeroes() {
        final Hero hero = GameHelper.getNearest(game.getCurrentPlayer().getHeroes(),
                playingMap.getPosX() + 4, playingMap.getPosY() + 4, false);
        if (hero != null) {
            new HeroForm(this, game.getKingdom(), hero).activate();
        }
    }

    @Override
    public void showRuins() {
        strategicMap.setMode(StrategicMap.Mode.RUINS);
        showMessage("Status of special locations");
        strategicMap.setMode(StrategicMap.Mode.CITIES);
    }

    @Override
    public void showControl() {
        new ControlForm(this, game).activate();
    }

    @Override
    public void endTurn() {
        playingMap.reset();
        game.nextPlayer(this);
    }

    // Game Controller.
    @Override
    public void onComputerModeTurned() {
        showMessage("And so the war continues...");
        showMessage("...without you!");
        // NOTE: W automatically turns on the observation mode for the computer mode.
    }

    @Override
    public void onPlayerDestroyed(Player player) {
        if (player.isHuman()) {
            showMessage("Wretched  " + player.getEmpire().getName() + "!  For you, the war is over!");
        } else {
            showMessage(player.getEmpire().getName() + " are no longer a threat!", true);
        }
    }

    @Override
    public void onAllPlayersDestroyed() {
        showMessage("As no players are involved in the..");
        showMessage("game I bid you a fond 'FAREWELL' ...");
        showMessage("Hit any key to return to DOS.");
        System.exit(0);
    }

    @Override
    public void onAllHumanPlayersDestroyed() {
        showMessage("As no further human resistance is ...");
        showMessage("possible the battle for Illuria ...");
        showMessage("will continue without you ...");
        showMessage("Hit 'ESC' to stop the war and ...");
        showMessage("visit the sites of your old battles.");
        observeToggle.turnOn();
    }

    @Override
    public void onHumanPlayerWon(int playerIndex, Player player) {
        // NOTE: W plays the horn sound.
        showMessage("Player " + (playerIndex + 1) + ", " + player.getEmpire().getName() + ": You have won!");
        showMessage("You now rule all of Illuria ...");
        showMessage("You may now inspect your domain.");
    }

    @Override
    public boolean onComputerPlayersSurrendered() {
        surrenderMode = SurrenderMode.PEACE_OFFER;
        showMessage("Your seneschal reports strangers at the gate!");
        showMessage("Crawling towards you they humbly present a scroll");
        // NOTE: W hides Info Screen.
        if (new SurrenderResultForm(this).getResult()) {
            surrenderMode = null;
            showMessage("Under your enlightened rule ...");
            showMessage("Illuria has entered a new golden age ...");
            showMessage("You may now inspect your domain.");
            return true;
        }
        surrenderMode = SurrenderMode.PEACE_OFFER_REFUSED;
        showMessage("No Quarter !!!");
        surrenderMode = null;
        return false;
    }

    private void showStatReport(StatReportType type) {
        if (!infoScreen.isVisible()) {
            throw new IllegalStateException("Info Screen has to be visible");
        }
        infoScreen.setVisible(false);
        new StatReportForm(this, type, game).activate();
        infoScreen.setVisible(true);
    }

    private Artifact showArtifactList(String message, List<Artifact> artifacts) {
        if (!infoScreen.isVisible()) {
            throw new IllegalStateException("Info Screen has to be visible");
        }
        infoScreen.setVisible(false);
        final Artifact artifact = new ItemResultForm<>(this, message, artifacts, Artifact::getName).getResult();
        infoScreen.setVisible(true);
        return artifact;
    }

    // Main paint.
    public void paint(Graphics g) {
        // Background.
        if (mainBitmap != null) {
            mainBitmap.drawSprite(g, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0);
        }
        if (activeContainer != null) {
            activeContainer.paint(g);
        }
        if (activeForm != null) {
            activeForm.paint(g);
        }
    }

    public void drawCursor(Graphics g, MouseEvent e) {
        Cursor cursor = Cursor.EMPTY;
        if (activeForm != null) {
            cursor = activeForm.getCursor(e);
        } else if (activeContainer != null && activeContainer.isEnabled()) {
            cursor = activeContainer.getCursor(e);
        }
        cursor.draw(g, e.getPoint());
    }

    public void mousePressed(MouseEvent e) {
        if (activeForm != null) {
            activeForm.mousePressed(e);
        } else if (activeContainer != null) {
            activeContainer.mousePressed(e);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (activeForm != null) {
            activeForm.mouseReleased(e);
        } else if (activeContainer != null) {
            activeContainer.mouseReleased(e);
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (activeForm != null) {
            activeForm.mouseDragged(e);
        } else if (activeContainer != null) {
            activeContainer.mouseDragged(e);
        }
    }

    public void keyPressed(KeyEvent e, boolean mouseReleased) {
        if (developerController.processKeyEvent(e)) {
            return;
        }
        if (computerController.processKeyEvent(e)) {
            return;
        }
        if (mouseReleased) {
            if (activeForm != null) {
                activeForm.keyPressed(e);
            } else if (activeContainer != null) {
                activeContainer.keyPressed(e);
            }
        }
    }
}
