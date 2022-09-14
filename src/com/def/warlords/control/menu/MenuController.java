package com.def.warlords.control.menu;

/**
 * @author wistful23
 * @version 1.23
 */
public interface MenuController {

    // About.
    void about();

    // Game.
    void switchObserveOption();
    void switchSoundOption();

    void saveGame();
    void loadGame();

    void quit();

    // Orders.
    void build();
    void showCapital();
    void disband();
    void raze();

    // Reports.
    void showArmiesReport();
    void showCitiesReport();
    void showGoldReport();
    void showHatredsReport();
    void showProductionReport();
    void showWinningReport();

    // Heroes.
    void dropItem();
    void findHero();
    void showInventory();
    void search();
    void takeItem();

    // View.
    void showArmySelection();
    void showProduction();
    void showHeroes();
    void showRuins();

    void showControl();

    // Turn.
    void endTurn();
}
