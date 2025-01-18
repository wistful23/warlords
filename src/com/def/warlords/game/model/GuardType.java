package com.def.warlords.game.model;

/**
 * @author wistful23
 * @version 1.23
 */
public enum GuardType {

    // @formatter:off
    // The order must match the ally factories.
    DRAGON  ("Dragon",  0),
    DEMON   ("Demon",   1),
    DEVIL   ("Devil",   1),
    WIZARD  ("Wizard",  2),
    GHOST   ("Ghost",   2),
    // The rest of the guards can't be allies.
    TROLL   ("Troll",   3),
    GIANT   ("Giant",   3),
    WOLF    ("Wolf",    4),
    GOBLIN  ("Goblin",  4);
    // @formatter:on

    public static final int COUNT = values().length;

    public static final int ALLY_COUNT = 5;

    private final String name;
    private final int weakness;

    GuardType(String name, int weakness) {
        this.name = name;
        this.weakness = weakness;
    }

    public String getName() {
        return name;
    }

    public int getWeakness() {
        return weakness;
    }
}
