package com.def.warlords.game;

import com.def.warlords.game.model.City;

/**
 * @author wistful23
 * @version 1.23
 */
public interface TurnController {

    // Called every turn at the very beginning.
    void beginTurn();

    // Called every turn after all reports have been finished.
    void playTurn();

    // Called on the first turn only.
    // Returns a hero name.
    String getFirstHeroName(City city, String initialName);

    // Called when a hero offer occurs in the middle of the game.
    // Returns a hero name or null if the hero offer is not accepted.
    String getHeroName(City city, int cost, String initialName);

    // Called if a hero brings allies.
    void onAlliesBrought(int count);

    // Called on every successful delivery.
    void reportDelivery(City sourceCity, City targetCity);

    // Called on every successful production.
    // Returns true if the city continues producing.
    boolean reportProduction(City city);

    // Called on every production failure.
    void reportProductionFailure(City city);

    // Called when there is not enough gold for producing.
    void reportNoGoldForProducing();

    // Called on the first turn only for the capital city.
    void selectProduction(City city);
}
