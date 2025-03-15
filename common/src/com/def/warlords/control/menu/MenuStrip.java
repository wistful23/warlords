package com.def.warlords.control.menu;

import com.def.warlords.gui.Menu;
import com.def.warlords.gui.MenuBar;

import java.awt.event.KeyEvent;

import static com.def.warlords.control.common.Dimensions.SCREEN_WIDTH;

/**
 * @author wistful23
 * @version 1.23
 */
public class MenuStrip extends MenuBar {

    public MenuStrip(MenuController controller) {
        super(0, 0, SCREEN_WIDTH, 10, 18);
        // About menu.
        final Menu aboutMenu = addMenu(64, "About", 12, 150);
        aboutMenu.addMenuItem("About Warlords", "?", KeyEvent.VK_SLASH, source -> controller.about());
        // Game menu.
        final Menu gameMenu = addMenu(48, "Game", 4, 158);
        gameMenu.addMenuItem("Observe", "alt O", KeyEvent.VK_O, KeyEvent.ALT_DOWN_MASK, controller.getObserveToggle());
        gameMenu.addMenuItem("Sound", "alt M", KeyEvent.VK_M, KeyEvent.ALT_DOWN_MASK, controller.getSoundToggle());
        gameMenu.addDivider();
        gameMenu.addMenuItem("Save Game", "alt S", KeyEvent.VK_S, KeyEvent.ALT_DOWN_MASK,
                source -> controller.saveGame());
        gameMenu.addMenuItem("Load Game", "alt L", KeyEvent.VK_L, KeyEvent.ALT_DOWN_MASK,
                source -> controller.loadGame());
        gameMenu.addDivider();
        gameMenu.addMenuItem("Quit", "^ Q  ", KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK, source -> controller.quit());
        // Orders menu.
        final Menu ordersMenu = addMenu(80, "Orders", 4, 110);
        ordersMenu.addMenuItem("Build", "- B", KeyEvent.VK_B, source -> controller.build());
        ordersMenu.addMenuItem("Capital", "- C", KeyEvent.VK_C, source -> controller.showCapital());
        ordersMenu.addMenuItem("Disband", "- Q", KeyEvent.VK_Q, source -> controller.disband());
        ordersMenu.addMenuItem("Raze", "- R", KeyEvent.VK_R, source -> controller.raze());
        // Reports menu.
        final Menu reportsMenu = addMenu(72, "Reports", 4, 134);
        reportsMenu.addMenuItem("Armies", "- A", KeyEvent.VK_A, source -> controller.showArmiesReport());
        reportsMenu.addMenuItem("Cities", "- S", KeyEvent.VK_S, source -> controller.showCitiesReport());
        reportsMenu.addMenuItem("Gold", "- G", KeyEvent.VK_G, source -> controller.showGoldReport());
        reportsMenu.addMenuItem("Hatreds", "- H", KeyEvent.VK_H, source -> controller.showHatredsReport());
        reportsMenu.addMenuItem("Production", "- P", KeyEvent.VK_P, source -> controller.showProductionReport());
        reportsMenu.addMenuItem("Winning", "- W", KeyEvent.VK_W, source -> controller.showWinningReport());
        // Heroes menu.
        final Menu heroesMenu = addMenu(64, "Heroes", 4, 102);
        heroesMenu.addMenuItem("Drop", "- D", KeyEvent.VK_D, source -> controller.dropItem());
        heroesMenu.addMenuItem("Find", "- F", KeyEvent.VK_F, source -> controller.findHero());
        heroesMenu.addMenuItem("Inv", "- I", KeyEvent.VK_I, source -> controller.showInventory());
        heroesMenu.addMenuItem("Search", "- Z", KeyEvent.VK_Z, source -> controller.search());
        heroesMenu.addMenuItem("Take", "- T", KeyEvent.VK_T, source -> controller.takeItem());
        // View menu.
        // NOTE: W draws the strip image from the resources. The view menu text is mismatched.
        final Menu viewMenu = addMenu(48, "View", 4, 134);
        viewMenu.addMenuItem("Stack", "- M", KeyEvent.VK_M, source -> controller.showArmySelection());
        viewMenu.addMenuItem("Production", "- L", KeyEvent.VK_L, source -> controller.showProduction());
        viewMenu.addMenuItem("Heroes", "- K", KeyEvent.VK_K, source -> controller.showHeroes());
        viewMenu.addMenuItem("Ruins", "- J", KeyEvent.VK_J, source -> controller.showRuins());
        viewMenu.addDivider();
        viewMenu.addMenuItem("Control", "- O", KeyEvent.VK_O, source -> controller.showControl());
        // Turn menu.
        final Menu turnMenu = addMenu(64, "Turn", 4, 134);
        turnMenu.addMenuItem("End Turn", "alt E", KeyEvent.VK_E, KeyEvent.ALT_DOWN_MASK,
                source -> controller.endTurn());
    }
}
