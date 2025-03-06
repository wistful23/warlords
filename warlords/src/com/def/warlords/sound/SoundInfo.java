package com.def.warlords.sound;

/**
 * @author wistful23
 * @version 1.23
 */
public enum SoundInfo {

    // @formatter:off
    DRAMATIC("DRAMATIC.wav"),
    DRUMROLL("DRUMROLL.wav"),
    HORN    ("HORN.wav"),
    RING    ("RING.wav"),
    WAR     ("WAR.wav");
    // @formatter:on

    public static final int COUNT = values().length;

    private final String fileName;

    SoundInfo(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
