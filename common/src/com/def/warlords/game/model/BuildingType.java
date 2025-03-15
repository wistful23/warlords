package com.def.warlords.game.model;

/**
 * @author wistful23
 * @version 1.23
 */
public enum BuildingType {

    // @formatter:off
    RUINS   ("Ruins"),
    CRYPT   ("Crypt"),
    TEMPLE  ("Healers"),
    SAGE    ("Sage"),
    LIBRARY ("Library");
    // @formatter:on

    private final String name;

    BuildingType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isCrypt() {
        return this == RUINS || this == CRYPT;
    }
}
