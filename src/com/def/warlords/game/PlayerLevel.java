package com.def.warlords.game;

/**
 * @author wistful23
 * @version 1.23
 */
public enum PlayerLevel {

    // @formatter:off
    HUMAN   ("Human",    0),
    KNIGHT  ("Knight",   5),
    BARON   ("Baron",    8),
    LORD    ("Lord",    11),
    WARLORD ("Warlord", 14);
    // @formatter:on

    public static final int COUNT = values().length;

    private final String name;
    private final int rating;

    PlayerLevel(String name, int rating) {
        this.name = name;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public PlayerLevel next() {
        return values()[(ordinal() + 1) % COUNT];
    }
}
