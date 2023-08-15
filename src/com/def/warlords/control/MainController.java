package com.def.warlords.control;

import com.def.warlords.control.common.GameHelper;
import com.def.warlords.control.form.*;
import com.def.warlords.control.menu.MenuController;
import com.def.warlords.control.menu.MenuStrip;
import com.def.warlords.game.*;
import com.def.warlords.game.model.*;
import com.def.warlords.graphics.Bitmap;
import com.def.warlords.graphics.BitmapFactory;
import com.def.warlords.graphics.BitmapInfo;
import com.def.warlords.graphics.Cursor;
import com.def.warlords.gui.Container;
import com.def.warlords.sound.Sound;
import com.def.warlords.sound.SoundFactory;
import com.def.warlords.sound.SoundInfo;
import com.def.warlords.util.Logger;
import com.def.warlords.util.Toggle;
import com.def.warlords.util.Util;

import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.SecondaryLoop;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static com.def.warlords.control.common.Dimensions.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class MainController extends JComponent
        implements FormController, MenuController, GameController, PlayerController {

    private final DeveloperController developerController = new DeveloperController(this);

    private final Mouse mouse = new Mouse();

    private Bitmap mainBitmap = BitmapFactory.getInstance().fetchBitmap(BitmapInfo.MAIN);

    private Container activeContainer;

    private SecondaryLoop secondaryLoop;
    private Form activeForm;

    private final Container mainContainer = new Container(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

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

    private final ComputerController computerController = new ComputerController(this);

    private Game game;

    private int currentRecordIndex = -1;

    private boolean endDeliveryReport;
    private boolean endProductionReport;

    public MainController() {
        mainContainer.add(playingMap);
        mainContainer.add(strategicMap);
        mainContainer.add(infoScreen);
        mainContainer.add(productionScreen);
        mainContainer.add(commandBar);
        mainContainer.add(menuStrip);
        productionScreen.setVisible(false);
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setFocusable(true);
        addKeyListener(new Keyboard());
    }

    public void start() {
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
        secondaryLoop = getToolkit().getSystemEventQueue().createSecondaryLoop();
        if (!secondaryLoop.enter()) {
            throw new IllegalStateException("Secondary loop error");
        }
    }

    @Override
    public void deactivateForm(Form form) {
        if (activeForm != form) {
            throw new IllegalArgumentException("Form " + form + " is inactive now");
        }
        activeForm = null;
        if (!secondaryLoop.exit()) {
            throw new IllegalStateException("Secondary loop error");
        }
    }

    @Override
    public Timer createTimer(int delay, ActionListener listener) {
        final Timer timer = new Timer(delay, listener);
        timer.addActionListener(e -> repaint());
        return timer;
    }

    @Override
    public Sound createSound(SoundInfo soundInfo, Runnable listener) {
        return SoundFactory.getInstance().createSound(soundInfo, () -> {
            repaint();
            listener.run();
        });
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
        if (timed) {
            new TimedMessageForm(this, text).activate();
        } else {
            new MessageForm(this, text).activate();
        }
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
        game.setControllers(this, computerController);
        for (final PlayerParams params : playerParams) {
            game.addPlayer(params.getEmpireType(), params.getLevel());
        }
        activeContainer = mainContainer;
        game.nextPlayer(this);
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
        new AboutForm(this).activate();
    }

    @Override
    public Toggle getObserveToggle() {
        return new Toggle(true);
    }

    @Override
    public Toggle getSoundToggle() {
        return SoundFactory.getInstance().getToggle();
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
        game.setControllers(this, computerController);
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
        final Artifact result = showArtifactList("Dropping an item ...", hero.getArtifacts());
        if (result != null) {
            hero.dropArtifact(result);
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
        final Artifact result = showArtifactList("Taking an item ...", hero.getGroup().getTile().getArtifacts());
        if (result != null) {
            hero.takeArtifact(result);
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
    }

    @Override
    public void onPlayerDestroyed(Player player) {
        if (player.isHuman()) {
            showMessage("Wretched  " + player.getEmpireType().getName() + "!  For you, the war is over!");
        } else {
            showMessage(player.getEmpireType().getName() + " are no longer a threat!", true);
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
    }

    @Override
    public void onVictory(int playerIndex, Player player) {
        // NOTE: W plays the horn sound.
        showMessage("Player " + (playerIndex + 1) + ", " + player.getEmpireType().getName() + ": You have won!");
        showMessage("You now rule all of Illuria ...");
        showMessage("You may now inspect your domain.");
    }

    // Player Controller.
    @Override
    public void beginTurn() {
        endDeliveryReport = false;
        endProductionReport = false;
        showCapital();
        final Player player = game.getCurrentPlayer();
        showMessage(player.getEmpireType().getName() + ": Click when ready!");
        if (player.getGold() == 0) {
            showMessage("Your treasuries are exhausted!");
        }
    }

    @Override
    public void playTurn() {
    }

    @Override
    public String getFirstHeroName(City city, String initialName) {
        strategicMap.setMode(StrategicMap.Mode.HERO_OFFER);
        strategicMap.setSourceCity(city);
        showMessage("In " + city.getName() + ", a hero emerges!");
        // NOTE: W doesn't show the city on Strategic Map when requesting the hero name.
        final String name = new HeroNameResultForm(this, initialName).getResult();
        strategicMap.setMode(StrategicMap.Mode.CITIES);
        return name;
    }

    @Override
    public String getHeroName(City city, int cost, String initialName) {
        // NOTE: W hides Info Screen.
        strategicMap.setMode(StrategicMap.Mode.HERO_OFFER);
        strategicMap.setSourceCity(city);
        String name = null;
        if (new HeroOfferResultForm(this, city, cost).getResult()) {
            name = new HeroNameResultForm(this, initialName).getResult();
        }
        strategicMap.setMode(StrategicMap.Mode.CITIES);
        return name;
    }

    @Override
    public void onAlliesBrought(int count) {
        showMessage("And the hero brings " + count + " allies!");
    }

    @Override
    public void reportDelivery(City sourceCity, City targetCity) {
        if (endDeliveryReport) {
            return;
        }
        strategicMap.setMode(StrategicMap.Mode.DELIVERY_REPORT);
        strategicMap.setSourceCity(sourceCity);
        strategicMap.setTargetCity(targetCity);
        infoScreen.setVisible(false);
        final ReportResult result = new DeliveryReportResultForm(this, targetCity).getResult();
        infoScreen.setVisible(true);
        endDeliveryReport = result == ReportResult.END_REPORT;
        strategicMap.setMode(StrategicMap.Mode.CITIES);
    }

    @Override
    public boolean reportProduction(City city) {
        if (endProductionReport) {
            return true;
        }
        strategicMap.setMode(StrategicMap.Mode.PRODUCTION_REPORT);
        strategicMap.setSourceCity(city);
        infoScreen.setVisible(false);
        final ReportResult result = new ProductionReportResultForm(this, city).getResult();
        infoScreen.setVisible(true);
        endProductionReport = result == ReportResult.END_REPORT;
        strategicMap.setMode(StrategicMap.Mode.CITIES);
        return result != ReportResult.NO;
    }

    @Override
    public void reportProductionFailure(City city) {
        // NOTE: W always displays this message even if 'End Turn' is pressed. Do the same.
        // NOTE: W displays the empty Strategic Map.
        strategicMap.setMode(StrategicMap.Mode.PRODUCTION_REPORT);
        strategicMap.setSourceCity(city);
        if (city.getCurrentFactory().getType().isNavy()) {
            if (city.isPortOccupiedByEnemy()) {
                showMessage(city.getName() + ": Port occupied by enemy! Cannot produce.");
            } else if (city.isPortFull()) {
                showMessage(city.getName() + ": Port full. No room to produce.");
            } else {
                throw new IllegalStateException("Can't detect navy production failure");
            }
        } else if (city.isFull()) {
            showMessage(city.getName() + ": City full. No room to produce.");
        } else {
            throw new IllegalStateException("Can't detect production failure");
        }
        strategicMap.setMode(StrategicMap.Mode.CITIES);
    }

    @Override
    public void reportNoGoldForProducing() {
        // NOTE: W displays this message once after 'End Turn' is pressed.
        if (endProductionReport) {
            return;
        }
        // NOTE: W displays the empty Strategic Map.
        showMessage("You do not have enough gold!");
    }

    @Override
    public boolean isImproveCityDefenceApproved(City city) {
        return new ImproveDefenceResultForm(this, city).getResult();
    }

    @Override
    public void onImproveCityDefenceStatus(BuildStatus status) {
        switch (status) {
            case PROHIBITED:
                showMessage("Your defences are already legendary!");
                return;
            case NOT_ENOUGH_GOLD:
                showMessage("You do not have sufficient gold!");
                return;
            case COMPLETED:
                showMessage("You have improved your defences!");
                return;
        }
        throw new IllegalStateException("Unknown status: " + status);
    }

    @Override
    public boolean isBuildTowerApproved() {
        return new BuildTowerResultForm(this, game.getCurrentPlayer()).getResult();
    }

    @Override
    public void onBuildTowerStatus(BuildStatus status) {
        switch (status) {
            case PROHIBITED:
                showMessage("Towers must be built on plains!");
                return;
            case NOT_ENOUGH_GOLD:
                showMessage("You do not have sufficient gold!");
                return;
            case COMPLETED:
                showMessage("You have built a tower!");
                return;
        }
        throw new IllegalStateException("Unknown status: " + status);
    }

    @Override
    public boolean isRazeApproved(Tile tile) {
        if (!tile.isCity() && !tile.isTower()) {
            showMessage("You can only raze cities and towers");
            return false;
        }
        return new RazeResultForm(this).getResult();
    }

    @Override
    public void onCityRazed(City city) {
        playingMap.setRazeMode(true);
        new SoundForm(this, SoundInfo.WAR).activate();
        // NOTE: W displays the message when playing the sound.
        showMessage("Your troops ravage " + city.getName() + "!");
        showMessage(city.getName() + " is in ruins!");
        playingMap.setRazeMode(false);
    }

    @Override
    public void onTowerRazed() {
        showMessage("The tower is destroyed!");
    }

    @Override
    public void onTerrainSearch(List<Artifact> artifacts) {
        if (artifacts.isEmpty()) {
            showMessage("You have found nothing!");
        } else {
            for (final Artifact artifact : artifacts) {
                showMessage("You have found: " + artifact.getName());
            }
        }
    }

    @Override
    public void onTempleFound(int count) {
        showMessage("You have found a temple...");
        if (count == 0) {
            showMessage("You have already received our blessing!");
        } else if (count == 1) {
            showMessage("You have been blessed!  Seek more blessings in far temples!");
        } else {
            showMessage(count + " Armies have been blessed!  Seek more blessings in far temples!");
        }
    }

    @Override
    public void onLibraryFound() {
        showMessage("You enter a great Library...");
        showMessage("Searching through the books, you find...");
        switch (Util.randomInt(3)) {
            case 0:
                // A piece of ancient wisdom.
                showMessage(Kingdom.getRandomWisdomNote());
                break;
            case 1:
                // Crypt information.
                final Crypt crypt = game.getKingdom().getRandomCrypt();
                if (crypt.isExplored()) {
                    showMessage(crypt.getName() + " is uninhabited!");
                } else {
                    showMessage("A " + crypt.getGuardType().getName() + " lives in " + crypt.getName() + "!");
                }
                break;
            case 2:
                // Artifact information.
                final Artifact artifact = game.getKingdom().getRandomArtifact();
                if (artifact.getHero() != null) {
                    showMessage("The " + artifact.getName() + " is owned by a hero!");
                } else if (artifact.getTile() != null) {
                    // BUG: W always displays the land the library belongs to.
                    showMessage(
                            "The " + artifact.getName() + " is lying in " + Kingdom.getLands(artifact.getTile()) + "!");
                } else if (artifact.getCrypt() != null) {
                    showMessage("The " + artifact.getName() + " is in the " + artifact.getCrypt().getName() + "!");
                } else {
                    // NOTE: Can't reproduce it in W. We also can't reach this state.
                    showMessage("The " + artifact.getName() + " is lost from Illuria!");
                }
                break;
            default:
                Util.fail();
        }
    }

    @Override
    public boolean onSageFound(int gold) {
        if (gold > 0) {
            showMessage("You are greeted warmly...");
            showMessage("... the Seer gives you a gem...");
            showMessage("...worth " + gold + " gp");
        }
        // NOTE: W doesn't display Info Screen for first search.
        final SageResult result = new SageResultForm(this).getResult();
        if (result == SageResult.CANCEL) {
            return false;
        }
        final Kingdom kingdom = game.getKingdom();
        if (result == SageResult.LOCATIONS) {
            // Building information.
            final Building building =
                    new ItemResultForm<>(this, null, kingdom.getBuildings(), Building::getName).getResult();
            if (building == null) {
                return false;
            }
            if (building.isCrypt()) {
                if (building.isExplored()) {
                    // NOTE: W displays 'Nothing evil lives here!' for explored crypt except allies.
                    showMessage("It has already been explored!");
                    // NOTE: W displays content of explored crypt except artifacts.
                } else {
                    final Crypt crypt = (Crypt) building;
                    showMessage("It is inhabited by a " + crypt.getGuardType().getName() + "!");
                    switch (crypt.getCryptType()) {
                        case ALLIES:
                            showMessage("Friendship is possible!");
                            break;
                        case ARTIFACT:
                            showMessage("An ancient artifact is there!");
                            showMessage("The " + crypt.getArtifact().getName() + "!");
                            break;
                        case ALTAR:
                            showMessage("An altar to an ancient God resides there");
                            break;
                        case THRONE:
                            showMessage("A throne from days past sits there gleaming!");
                            break;
                        case GOLD:
                            showMessage("I believe there is much gold there!");
                    }
                }
            } else {
                showMessage("Nothing evil lives here!");
                if (building.isTemple()) {
                    showMessage("Blessed is he who visits this place!");
                } else if (building.isLibrary()) {
                    showMessage("It is a truly magnificent collection of books!");
                } else if (building.isSage()) {
                    showMessage("Ahhh grasshopper! A place to seek wisdom!");
                }
            }
            return true;
        }
        if (result == SageResult.ITEMS) {
            // Artifact information.
            final Artifact artifact =
                    new ItemResultForm<>(this, null, kingdom.getArtifacts(), Artifact::getName).getResult();
            if (artifact == null) {
                return false;
            }
            showMessage("The " + artifact.getName() + " can be found ...");
            final Hero hero = artifact.getHero();
            final Tile tile = artifact.getTile();
            final Crypt crypt = artifact.getCrypt();
            if (hero != null) {
                final City city = GameHelper.getNearest(kingdom.getCities(), hero);
                showMessage(
                        "... near " + city.getName() + " with a hero of the " + hero.getEmpire().getType().getName());
            } else if (tile != null) {
                final City city = GameHelper.getNearest(kingdom.getCities(), tile);
                // Razed city is NEUTRAL.
                final EmpireType empireType =
                        city.getEmpire() != null ? city.getEmpire().getType() : EmpireType.NEUTRAL;
                showMessage("... in the vicinity of the " + empireType.getName() + " city of " + city.getName());
            } else if (crypt != null) {
                showMessage("... at " + crypt.getName() + "!");
            } else {
                // NOTE: Can't reproduce it in W. We also can't reach this state.
                showMessage("... no more in the lands of Illuria!");
            }
            return true;
        }
        Util.fail();
        return false;
    }

    public void onAlliesJoined(Hero hero, GuardType guard, int count) {
        playingMap.setSearchMode(PlayingMap.SearchMode.SEARCH);
        new SoundForm(this, SoundInfo.DRAMATIC).activate();
        if (count > 1) {
            showMessage(count + " " + guard.getName() + "s offer to join " + hero.getName());
        } else {
            showMessage("A " + guard.getName() + " offers to join " + hero.getName());
        }
        playingMap.setSearchMode(PlayingMap.SearchMode.NONE);
    }

    public void onGuardFight(Hero hero, GuardType guard, boolean slain) {
        playingMap.setSearchMode(PlayingMap.SearchMode.SEARCH);
        new SoundForm(this, SoundInfo.DRAMATIC).activate();
        showMessage(hero.getName() + " encounters a " + guard.getName() + "!");
        if (slain) {
            // NOTE: W doesn't display a hero under the death icon.
            playingMap.setSearchMode(PlayingMap.SearchMode.DEATH);
            showMessage("...and is slain by it!");
        } else {
            showMessage("...and is victorious!");
        }
        playingMap.setSearchMode(PlayingMap.SearchMode.NONE);
    }

    public void onArtifactFound(Hero hero, Artifact artifact) {
        showMessage(hero.getName() + " has found the " + artifact.getName() + "!");
    }

    public boolean onAltarFound() {
        // NOTE: W hides Info Screen.
        return new AltarResultForm(this).getResult();
    }

    public void onAltarResult(Hero hero, boolean ignored) {
        if (ignored) {
            showMessage("The Gods ignore " + hero.getName());
        } else {
            showMessage("The Gods hear! " + hero.getName() + "'s strength increases to " + hero.getStrength() + "!");
        }
    }

    public boolean onThroneFound() {
        // NOTE: W hides Info Screen.
        return new ThroneResultForm(this).getResult();
    }

    public void onThroneResult(Hero hero, boolean downgraded) {
        if (downgraded) {
            showMessage(hero.getName() + "'s strength decreases to " + hero.getStrength() + "!");
        } else {
            showMessage(hero.getName() + "'s strength increases to " + hero.getStrength() + "!");
        }
    }

    public void onGoldFound(Hero hero, int value) {
        showMessage(hero.getName() + " has found " + value + " gp!");
    }

    public void onCombat(ArmyList attackingArmies, ArmyList defendingArmies, Tile tile, List<Boolean> protocol) {
        playingMap.setCombatTile(tile);
        new SoundForm(this, SoundInfo.WAR).activate();
        infoScreen.setVisible(false);
        new CombatForm(this, game.getKingdom(), attackingArmies, defendingArmies, tile, protocol).activate();
        playingMap.setCombatTile(null);
        infoScreen.setVisible(true);
    }

    public void selectProduction(City city) {
        activateProductionScreen(city);
    }

    // Main paint.
    @Override
    public void paint(Graphics g) {
        // Background.
        mainBitmap.drawSprite(g, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0);
        if (activeContainer != null) {
            activeContainer.paint(g);
        }
        if (activeForm != null) {
            activeForm.paint(g);
        }
        mouse.paint(g);
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
        final Artifact result = new ItemResultForm<>(this, message, artifacts, Artifact::getName).getResult();
        infoScreen.setVisible(true);
        return result;
    }

    private final class Keyboard extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            repaint();
            if (developerController.processKeyEvent(e)) {
                return;
            }
            if (computerController.processKeyEvent(e)) {
                return;
            }
            if (activeForm != null) {
                activeForm.keyPressed(e);
            } else if (activeContainer != null) {
                activeContainer.keyPressed(e);
            }
            mouse.updateEvent(e.getModifiersEx());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            repaint();
            mouse.updateEvent(e.getModifiersEx());
        }
    }

    private final class Mouse extends MouseAdapter {

        private MouseEvent e;

        private Mouse() {
            final BufferedImage emptyImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            setCursor(getToolkit().createCustomCursor(emptyImage, new Point(), ""));
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public void paint(Graphics g) {
            if (e == null) {
                return;
            }
            Cursor cursor = Cursor.EMPTY;
            if (activeForm != null) {
                cursor = activeForm.getCursor(e);
            } else if (activeContainer != null && activeContainer.isEnabled()) {
                cursor = activeContainer.getCursor(e);
            }
            cursor.draw(g, e.getPoint());
        }

        public void updateEvent(int modifiers) {
            if (e != null) {
                e = new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), modifiers, e.getX(), e.getY(),
                        e.getClickCount(), e.isPopupTrigger());
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            repaint();
            this.e = e;
            if (activeForm != null) {
                activeForm.mousePressed(e);
            } else if (activeContainer != null) {
                activeContainer.mousePressed(e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            repaint();
            this.e = e;
            if (activeForm != null) {
                activeForm.mouseReleased(e);
            } else if (activeContainer != null) {
                activeContainer.mouseReleased(e);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            repaint();
            this.e = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            repaint();
            this.e = e;
            if (activeForm != null) {
                activeForm.mouseDragged(e);
            } else if (activeContainer != null) {
                activeContainer.mouseDragged(e);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            repaint();
            this.e = e;
        }
    }
}
