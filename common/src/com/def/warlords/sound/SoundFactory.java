package com.def.warlords.sound;

import com.def.warlords.control.Platform;
import com.def.warlords.util.Logger;
import com.def.warlords.util.Toggle;

import java.io.IOException;

/**
 * @author wistful23
 * @version 1.23
 */
public final class SoundFactory {

    private static SoundFactory instance;

    public static void createInstance(Platform platform) {
        if (instance != null) {
            throw new IllegalStateException("Sound factory is already created");
        }
        instance = new SoundFactory(platform);
    }

    public static SoundFactory getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Sound factory is not created");
        }
        return instance;
    }

    private final Platform platform;

    private final Toggle toggle = new Toggle(true);

    private SoundFactory(Platform platform) {
        this.platform = platform;
    }

    public Toggle getToggle() {
        return toggle;
    }

    public Sound createSound(SoundInfo soundInfo, Runnable listener) {
        final Sound sound = new Sound(listener);
        if (toggle.isOff()) {
            return sound;
        }
        try {
            sound.init(platform, soundInfo);
        } catch (IOException e) {
            Logger.error("Could not initialize sound: " + soundInfo);
            e.printStackTrace();
        }
        return sound;
    }
}
