package com.def.warlords.game.model;

/**
 * @author wistful23
 * @version 1.23
 */
public enum AttitudeType {

    // @formatter:off
    APATHY  ("Apathy"),
    DISTRUST("Distrust"),
    DISLIKE ("Dislike"),
    DISDAIN ("Disdain"),
    DISGUST ("Disgust"),
    HATRED  ("Hatred"),
    LOATHING("Loathing");
    // @formatter:on

    private final String name;

    AttitudeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
