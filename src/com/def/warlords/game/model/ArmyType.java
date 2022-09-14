package com.def.warlords.game.model;

/**
 * @author wistful23
 * @version 1.23
 */
public enum ArmyType {

    // @formatter:off
    GIANTS  ("Giant Warriors",  new int[] { 1,  1, -1, -1,  5,  4, -1,  2,  5,  2,  1,  2}, -1,  0,  0),
    HVY_INF ("Heavy Infantry",  new int[] { 1,  1, -1, -1,  4,  6, -1,  2,  5,  2,  1,  2}, -1, -1, -1),
    LT_INF  ("Light Infantry",  new int[] { 1,  1, -1, -1,  4,  6, -1,  2,  5,  2,  1,  2}, -1, -1, -1),
    DWARVES ("Dwarven Legions", new int[] { 1,  1, -1, -1,  6,  3, -1,  2,  6,  2,  1,  2}, -1,  1,  0),
    CAVALRY ("Cavalry",         new int[] { 1,  1, -1, -1,  5,  6, -1,  2,  5,  2,  1,  2}, -1, -1, -1),
    NAVY    ("Navy",            new int[] {-1,  2,  1,  2, -1, -1, -1, -1, -1, -1, -1, -1},  0,  0,  0),
    ARCHERS ("Elven Archers",   new int[] { 1,  1, -1, -1,  2,  5, -1,  2,  6,  2,  1,  2},  1, -1, -1),
    WOLVES  ("Wolf Riders",     new int[] { 1,  1, -1, -1,  4,  6, -1,  2,  4,  2,  1,  2}, -1, -1,  0),
    PEGASI  ("Pegasi",          new int[] { 1,  1,  2,  2,  2,  2,  3,  2,  2,  2,  2,  2},  1,  0,  0),
    GRIFFINS("Griffins",        new int[] { 1,  1,  2,  2,  2,  2,  3,  2,  2,  2,  2,  2},  0,  1,  0),
    WIZARDS ("Wizards",         new int[] { 1,  1, -1, -1,  4,  6, -1,  2,  5,  2,  1,  2},  0,  0,  0),
    UNDEAD  ("Undead",          new int[] { 1,  1, -1, -1,  4,  5, -1,  2,  4,  2,  1,  2}, -1,  0,  1),
    DRAGONS ("Dragons",         new int[] { 1,  1,  2,  2,  2,  2,  3,  2,  2,  2,  2,  2},  0,  0,  0),
    DEMONS  ("Demons",          new int[] { 1,  1, -1, -1,  4,  5, -1,  2,  5,  2,  1,  2}, -1,  0,  0),
    DEVILS  ("Devils",          new int[] { 1,  1, -1, -1,  4,  5, -1,  2,  5,  2,  1,  2}, -1,  0,  0),
    HERO    ("Hero",            new int[] { 1,  1, -1, -1,  4,  6, -1,  2,  5,  2,  1,  2},  0,  0,  0);
    // @formatter:on

    public static final int COUNT = values().length;

    public static final int FORBIDDEN_MOVEMENT_COST = Integer.MAX_VALUE;

    private final String name;
    private final int[] movementCosts;
    private final int forestModifier;
    private final int hillModifier;
    private final int marshModifier;

    ArmyType(String name, int[] movementCosts, int forestModifier, int hillModifier, int marshModifier) {
        this.name = name;
        this.movementCosts = movementCosts;
        this.forestModifier = forestModifier;
        this.hillModifier = hillModifier;
        this.marshModifier = marshModifier;
    }

    public String getName() {
        return name;
    }

    public int getMovementCost(TerrainType terrain) {
        final int movementCost = movementCosts[terrain.ordinal()];
        return movementCost != -1 ? movementCost : FORBIDDEN_MOVEMENT_COST;
    }

    public int getTerrainModifier(TerrainType terrain) {
        switch (terrain) {
            case FOREST:
                return forestModifier;
            case HILL:
                return hillModifier;
            case MARSH:
                return marshModifier;
        }
        return 0;
    }

    public boolean isHero() {
        return this == HERO;
    }

    public boolean isNavy() {
        return this == NAVY;
    }

    public boolean isFlying() {
        return this == PEGASI || this == GRIFFINS || this == DRAGONS;
    }

    public boolean isSpecial() {
        return this == WIZARDS || this == UNDEAD || this == DRAGONS || this == DEMONS || this == DEVILS;
    }
}
