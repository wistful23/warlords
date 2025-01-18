package com.def.warlords.game.model;

/**
 * @author wistful23
 * @version 1.23
 */
public enum CryptType {

    // @formatter:off
    ALLIES      ( 8),
    ARTIFACT    (14),
    // NOTE: W randomly distributes altars, thrones, and gold among 10 crypts.
    ALTAR       ( 4),
    THRONE      ( 3),
    GOLD        ( 3);
    // @formatter:on

    private final int count;

    CryptType(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
