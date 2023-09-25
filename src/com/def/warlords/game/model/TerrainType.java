package com.def.warlords.game.model;

/**
 * @author wistful23
 * @version 1.23
 */
public enum TerrainType {

    // @formatter:off
    ROAD    ("Road"),
    BRIDGE  ("Bridge"),
    WATER   ("Water"),
    SHORE   ("Shore"),
    FOREST  ("Forest"),
    HILL    ("Hill"),
    MOUNTAIN("Mountain"),
    PLAIN   ("Plain"),
    MARSH   ("Marsh"),
    TOWER   ("Tower"),
    CITY    ("City"),
    RUINS   ("Ruins");
    // @formatter:on

    public static TerrainType valueOf(int value) {
        if (value == -128 || value == 0) {
            return PLAIN;
        } else if (value >= -127 && value <= -119) {
            return TOWER;
        } else if (value >= -118 && value <= -116) {
            // Temple, library, sage, crypt, ruins.
            return RUINS;
        } else if (value == -115) {
            return MARSH;
        } else if (value >= -114 && value <= -100) {
            return ROAD;
        } else if (value == -92 || value == -86) {
            return WATER;
        } else if (value >= -99 && value <= -87) {
            return SHORE;
        } else if (value >= -85 && value <= -72) {
            return FOREST;
        } else if (value >= -71 && value <= -58) {
            return HILL;
        } else if (value >= -57 && value <= -43) {
            return MOUNTAIN;
        } else if (value >= -42 && value <= -7) {
            return CITY;
        } else if (value >= -6 && value <= -3) {
            return BRIDGE;
        }
        throw new IllegalArgumentException("Illegal value: " + value);
    }

    private final String name;

    TerrainType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
