package com.def.warlords.control;

import com.def.warlords.game.Game;

/**
 * @author wistful23
 * @version 1.23
 */
public class RecordData {

    private final int posX, posY;
    private final Game game;

    public RecordData(int posX, int posY, Game game) {
        this.posX = posX;
        this.posY = posY;
        this.game = game;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Game getGame() {
        return game;
    }
}
