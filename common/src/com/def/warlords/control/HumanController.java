package com.def.warlords.control;

import com.def.warlords.control.common.GameHelper;
import com.def.warlords.control.form.*;
import com.def.warlords.game.Player;
import com.def.warlords.game.PlayerController;
import com.def.warlords.game.model.*;
import com.def.warlords.sound.SoundInfo;
import com.def.warlords.util.Util;

import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class HumanController implements PlayerController {

    private final MainController controller;

    private boolean endDeliveryReport;
    private boolean endProductionReport;

    public HumanController(MainController controller) {
        this.controller = controller;
    }

    @Override
    public void beginTurn() {
        endDeliveryReport = false;
        endProductionReport = false;
        controller.showCapital();
        final Player player = controller.getGame().getCurrentPlayer();
        showMessage(player.getEmpire().getName() + ": Click when ready!");
        if (player.getGold() == 0) {
            showMessage("Your treasuries are exhausted!");
        }
    }

    @Override
    public void playTurn() {
    }

    @Override
    public String getFirstHeroName(City city, String initialName) {
        final StrategicMap strategicMap = controller.getStrategicMap();
        strategicMap.setMode(StrategicMap.Mode.HERO_OFFER);
        strategicMap.setSourceCity(city);
        showMessage("In " + city.getName() + ", a hero emerges!");
        // NOTE: W doesn't show the city on Strategic Map when requesting the hero name.
        final String name = new HeroNameResultForm(controller, initialName).getResult();
        strategicMap.setMode(StrategicMap.Mode.CITIES);
        return name;
    }

    @Override
    public String getHeroName(City city, int cost, String initialName) {
        // NOTE: W hides Info Screen.
        final StrategicMap strategicMap = controller.getStrategicMap();
        strategicMap.setMode(StrategicMap.Mode.HERO_OFFER);
        strategicMap.setSourceCity(city);
        String name = null;
        if (Util.isTrue(new HeroOfferResultForm(controller, city, cost).getResult())) {
            name = new HeroNameResultForm(controller, initialName).getResult();
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
        final StrategicMap strategicMap = controller.getStrategicMap();
        final InfoScreen infoScreen = controller.getInfoScreen();
        strategicMap.setMode(StrategicMap.Mode.DELIVERY_REPORT);
        strategicMap.setSourceCity(sourceCity);
        strategicMap.setTargetCity(targetCity);
        infoScreen.setVisible(false);
        final ReportResult result = new DeliveryReportResultForm(controller, targetCity).getResult();
        infoScreen.setVisible(true);
        endDeliveryReport = result == null || result == ReportResult.END_REPORT;
        strategicMap.setMode(StrategicMap.Mode.CITIES);
    }

    @Override
    public boolean reportProduction(City city) {
        if (endProductionReport) {
            return true;
        }
        final StrategicMap strategicMap = controller.getStrategicMap();
        final InfoScreen infoScreen = controller.getInfoScreen();
        strategicMap.setMode(StrategicMap.Mode.PRODUCTION_REPORT);
        strategicMap.setSourceCity(city);
        infoScreen.setVisible(false);
        final ReportResult result = new ProductionReportResultForm(controller, city).getResult();
        infoScreen.setVisible(true);
        endProductionReport = result == null || result == ReportResult.END_REPORT;
        strategicMap.setMode(StrategicMap.Mode.CITIES);
        return result == ReportResult.YES || result == ReportResult.END_REPORT;
    }

    @Override
    public void reportProductionFailure(City city) {
        // NOTE: W always displays this message even if 'End Turn' is pressed. Do the same.
        // NOTE: W displays the empty Strategic Map.
        final StrategicMap strategicMap = controller.getStrategicMap();
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
        return Util.isTrue(new ImproveDefenceResultForm(controller, city).getResult());
    }

    @Override
    public void onImproveCityDefenceStatus(PlayerController.BuildStatus status) {
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
        return Util.isTrue(new BuildTowerResultForm(controller, controller.getGame().getCurrentPlayer()).getResult());
    }

    @Override
    public void onBuildTowerStatus(PlayerController.BuildStatus status) {
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
        return Util.isTrue(new RazeResultForm(controller).getResult());
    }

    @Override
    public void onCityRazed(City city) {
        final PlayingMap playingMap = controller.getPlayingMap();
        playingMap.setRazeMode(true);
        new SoundForm(controller, SoundInfo.WAR).activate();
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
                final Crypt crypt = controller.getGame().getKingdom().getRandomCrypt();
                if (crypt.isExplored()) {
                    showMessage(crypt.getName() + " is uninhabited!");
                } else {
                    showMessage("A " + crypt.getGuardType().getName() + " lives in " + crypt.getName() + "!");
                }
                break;
            case 2:
                // Artifact information.
                final Artifact artifact = controller.getGame().getKingdom().getRandomArtifact();
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
        final SageResult result = new SageResultForm(controller).getResult();
        if (result == null || result == SageResult.CANCEL) {
            return false;
        }
        final Kingdom kingdom = controller.getGame().getKingdom();
        if (result == SageResult.LOCATIONS) {
            // Building information.
            final Building building =
                    new ItemResultForm<>(controller, null, kingdom.getBuildings(), Building::getName).getResult();
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
                    new ItemResultForm<>(controller, null, kingdom.getArtifacts(), Artifact::getName).getResult();
            if (artifact == null) {
                return false;
            }
            showMessage("The " + artifact.getName() + " can be found ...");
            final Hero hero = artifact.getHero();
            final Tile tile = artifact.getTile();
            final Crypt crypt = artifact.getCrypt();
            if (hero != null) {
                final City city = GameHelper.getNearest(kingdom.getCities(), hero);
                showMessage("... near " + city.getName() + " with a hero of the " + hero.getEmpire().getName());
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
        final PlayingMap playingMap = controller.getPlayingMap();
        playingMap.setSearchMode(PlayingMap.SearchMode.SEARCH);
        new SoundForm(controller, SoundInfo.DRAMATIC).activate();
        if (count > 1) {
            showMessage(count + " " + guard.getName() + "s offer to join " + hero.getName());
        } else {
            showMessage("A " + guard.getName() + " offers to join " + hero.getName());
        }
        playingMap.setSearchMode(PlayingMap.SearchMode.NONE);
    }

    public void onGuardFight(Hero hero, GuardType guard, boolean slain) {
        final PlayingMap playingMap = controller.getPlayingMap();
        playingMap.setSearchMode(PlayingMap.SearchMode.SEARCH);
        new SoundForm(controller, SoundInfo.DRAMATIC).activate();
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
        return Util.isTrue(new AltarResultForm(controller).getResult());
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
        return Util.isTrue(new ThroneResultForm(controller).getResult());
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
        final PlayingMap playingMap = controller.getPlayingMap();
        final InfoScreen infoScreen = controller.getInfoScreen();
        playingMap.setCombatTile(tile);
        new SoundForm(controller, SoundInfo.WAR).activate();
        infoScreen.setVisible(false);
        new CombatForm(controller, controller.getGame(), attackingArmies, defendingArmies, tile, protocol).activate();
        playingMap.setCombatTile(null);
        infoScreen.setVisible(true);
    }

    public void selectProduction(City city) {
        controller.activateProductionScreen(city);
    }

    private void showMessage(String text) {
        controller.showMessage(text);
    }
}
