package com.def.warlords.sound;

import com.def.warlords.util.Timer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.awt.EventQueue;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author wistful23
 * @version 1.23
 */
public class Sound {

    private static final int DELAY_SILENCE = 1000;

    private Runnable listener;

    private Clip clip;
    private Timer timer;

    public Sound(Runnable listener) {
        this.listener = listener;
    }

    public void open(InputStream stream) throws IOException {
        if (clip != null) {
            throw new IllegalStateException("Sound is already open");
        }
        try {
            final Clip localClip = AudioSystem.getClip();
            localClip.open(AudioSystem.getAudioInputStream(stream));
            localClip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    EventQueue.invokeLater(this::notifyListener);
                }
            });
            clip = localClip;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void start() {
        if (clip != null) {
            clip.start();
        } else if (timer == null) {
            timer = new Timer(this::notifyListener);
            timer.start(DELAY_SILENCE);
        }
    }

    public void stop() {
        listener = null;
        if (clip != null) {
            clip.stop();
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
