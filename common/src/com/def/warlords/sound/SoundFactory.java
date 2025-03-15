package com.def.warlords.sound;

import com.def.warlords.util.Logger;
import com.def.warlords.util.Toggle;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author wistful23
 * @version 1.23
 */
public final class SoundFactory {

    private static final SoundFactory instance = new SoundFactory();

    public static SoundFactory getInstance() {
        return instance;
    }

    private final Toggle toggle = new Toggle(true);

    private final InputStream[] streams = new InputStream[SoundInfo.COUNT];

    public Toggle getToggle() {
        return toggle;
    }

    public Sound createSound(SoundInfo soundInfo, Runnable listener) {
        final Sound sound = new Sound(listener);
        if (toggle.isOff()) {
            return sound;
        }
        try {
            InputStream stream = streams[soundInfo.ordinal()];
            if (stream == null) {
                final String fileName = soundInfo.getFileName();
                stream = getClass().getResourceAsStream("/sound/" + fileName);
                if (stream == null) {
                    throw new FileNotFoundException("Sound was not found: " + fileName);
                }
                if (!stream.markSupported()) {
                    stream = new BufferedInputStream(stream);
                }
                stream.mark(Integer.MAX_VALUE);
                streams[soundInfo.ordinal()] = stream;
            }
            stream.reset();
            sound.open(new BufferedInputStream(stream));
        } catch (IOException e) {
            Logger.error("Could not open sound stream: " + soundInfo);
            e.printStackTrace();
        }
        return sound;
    }
}
