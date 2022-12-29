package com.def.warlords.game;

/**
 * @author wistful23
 * @version 1.23
 */
public interface GameController {

    // Called when the computer mode is turned.
    void onComputerModeTurned();

    // Called when the player is destroyed.
    void onPlayerDestroyed(Player player);

    // Called when all the players are destroyed.
    void onAllPlayersDestroyed();

    // Called when all the human players are destroyed.
    void onAllHumanPlayersDestroyed();

    // Called when the human player destroyed all opponents.
    void onVictory(int playerIndex, Player player);
}
