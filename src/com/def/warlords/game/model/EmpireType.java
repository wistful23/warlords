package com.def.warlords.game.model;

import com.def.warlords.graphics.Palette;

import java.awt.Color;

/**
 * @author wistful23
 * @version 1.23
 */
public enum EmpireType {
    
    // @formatter:off
    NEUTRAL     ("Neutral",      Palette.GRAY,        new int[] { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},   0),
    SIRIANS     ("Sirians",      Palette.WHITE,       new int[] { 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0}, 432),
    STORM_GIANTS("Storm Giants", Palette.YELLOW,      new int[] { 0,  0,  0,  0,  0,  1,  0,  0, -1,  0,  0,  0},  45),
    GREY_DWARVES("Grey Dwarves", Palette.BROWN_LIGHT, new int[] { 0,  0,  0,  0, -1,  2,  0,  0, -1,  0,  0,  0}, 137),
    ORCS_OF_KOR ("Orcs of Kor",  Palette.RED,         new int[] { 0,  0,  0,  0, -1,  0,  0,  0,  1,  0,  0,  0},  55),
    ELVALLIE    ("Elvallie",     Palette.GREEN,       new int[] { 0,  0,  0,  0,  1, -1,  0,  0, -1,  0,  0,  0},  58),
    SELENTINES  ("Selentines",   Palette.BLUE,        new int[] { 0,  0,  1,  1,  0,  0,  0,  0,  0,  0,  0,  0},  85),
    HORSE_LORDS ("Horse Lords",  Palette.BLUE_LIGHT,  new int[] { 1,  1,  0,  0, -1, -1,  0,  1,  0,  0,  0,  0},  77),
    LORD_BANE   ("Lord Bane",    Palette.BLACK,       new int[] { 0,  0,  0,  0, -1,  0,  0,  0,  1,  0,  0,  0},  80);
    // @formatter:on

    public static final int COUNT = values().length;

    private final String name;
    private final Color color;
    private final int[] terrainModifiers;
    private final int initialGold;

    EmpireType(String name, Color color, int[] terrainModifiers, int initialGold) {
        this.name = name;
        this.color = color;
        this.terrainModifiers = terrainModifiers;
        this.initialGold = initialGold;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public int getInitialGold() {
        return initialGold;
    }

    public int getTerrainModifier(TerrainType terrain) {
        return terrainModifiers[terrain.ordinal()];
    }

    public int getOffsetOrdinal() {
        return (this == NEUTRAL ? COUNT : ordinal()) - 1;
    }
}
