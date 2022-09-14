package com.def.warlords.game.model;

/**
 * @author wistful23
 * @version 1.23
 */
public enum GuardType {

    // @formatter:off
    // The order must match the ally factories.
    DRAGON  ("Dragon"),
    DEMON   ("Demon"),
    DEVIL   ("Devil"),
    WIZARD  ("Wizard"),
    GHOST   ("Ghost"),
    // The rest of the guards can't be allies.
    TROLL   ("Troll"),
    GIANT   ("Giant"),
    WOLF    ("Wolf"),
    GOBLIN  ("Goblin");
    // @formatter:on

    public static final int COUNT = values().length;

    public static final int ALLY_COUNT = 5;

    private final String name;

    GuardType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
