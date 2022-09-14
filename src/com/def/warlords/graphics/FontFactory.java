package com.def.warlords.graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class FontFactory {

    private static final FontFactory instance = new FontFactory();

    public static FontFactory getInstance() {
        return instance;
    }

    private final GothicFont gothicFont = new GothicFont();
    private final MonospacedFont monospacedFont = new MonospacedFont();

    private FontFactory() {
    }

    public Font getGothicFont() {
        return gothicFont;
    }

    public Font getMonospacedFont() {
        return monospacedFont;
    }
}
