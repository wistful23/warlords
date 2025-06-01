package com.def.warlords.game.model;

import com.def.warlords.util.Logger;

/**
 * @author wistful23
 * @version 1.23
 */
public final class DeveloperHelper {

    public static void locateArmy(Empire empire, Tile tile, Army army) {
        if (!empire.registerArmy(army)) {
            Logger.dev("Could not register army " + army.getName() + " in " + empire.getType());
            return;
        }
        if (!tile.locate(army)) {
            Logger.dev("Could not locate army " + army.getName() + " to " + tile.getTerrain().getName());
            empire.unregisterArmy(army);
            return;
        }
        Logger.dev("Located army " + army.getName() + " to " + tile.getTerrain().getName());
    }

    public static void locateArmy(Empire empire, City city, Army army) {
        if (!empire.registerArmy(army)) {
            Logger.dev("Could not register army " + army.getName() + " in " + empire.getType());
            return;
        }
        if (!city.locate(army)) {
            Logger.dev("Could not locate army " + army.getName() + " to " + city.getName());
            empire.unregisterArmy(army);
            return;
        }
        Logger.dev("Located army " + army.getName() + " to " + city.getName());
    }

    private DeveloperHelper() {
    }
}
