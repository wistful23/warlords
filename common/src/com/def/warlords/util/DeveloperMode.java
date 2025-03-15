package com.def.warlords.util;

/**
 * @author wistful23
 * @version 1.23
 */
public class DeveloperMode {

    private static boolean on;

    public static boolean isOn() {
        return on;
    }

    public static void invert() {
        if (on) {
            System.out.println("Developer mode: OFF");
        }
        on = !on;
        if (on) {
            System.out.println("Developer mode: ON");
        }
    }

    private DeveloperMode() {
    }
}
