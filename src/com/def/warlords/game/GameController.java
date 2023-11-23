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
    void onHumanPlayerWon(int playerIndex, Player player);

    // Called when the computer players are surrendered.
    // Returns true if the peace offer is accepted.
    boolean onComputerPlayersSurrendered();
}
