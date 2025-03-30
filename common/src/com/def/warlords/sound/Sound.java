package com.def.warlords.sound;

import com.def.warlords.control.Platform;
import com.def.warlords.util.Timer;

import java.io.IOException;

/**
 * @author wistful23
 * @version 1.23
 */
public class Sound {

    private static final int DELAY_SILENCE = 1000;

    private Runnable listener;

    private Player player;
    private Timer timer;

    public Sound(Runnable listener) {
        this.listener = listener;
    }

    public void init(Platform platform, SoundInfo soundInfo) throws IOException {
        if (player != null) {
            throw new IllegalStateException("Sound is already initialized");
        }
        player = platform.getAudioPlayer("sound/" + soundInfo.getFileName(), this::notifyListener);
    }

    public void start() {
        if (player != null) {
            player.start();
        } else if (timer == null) {
            timer = new Timer(this::notifyListener);
            timer.start(DELAY_SILENCE);
        }
    }

    public void stop() {
        listener = null;
        if (player != null) {
            player.stop();
        } else if (timer != null) {
            timer.stop();
        }
    }

    private void notifyListener() {
        if (listener != null) {
            final Runnable localListener = listener;
            listener = null;
            localListener.run();
        }
    }
}
