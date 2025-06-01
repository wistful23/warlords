package com.def.warlords.util;

/**
 * @author wistful23
 * @version 1.23
 */
public final class DeveloperMode {

    private static boolean on;

    public static boolean isOn() {
        return on;
    }

    public static void invert() {
        on = !on;
        Logger.dev("Developer mode: " + (on ? "ON" : "OFF"));
    }

    private DeveloperMode() {
    }
}
