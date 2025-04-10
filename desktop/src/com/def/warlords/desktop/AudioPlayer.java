package com.def.warlords.desktop;

import com.def.warlords.sound.Player;

import javax.sound.sampled.*;
import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioPlayer implements Player {

    private Clip clip;

    public void init(InputStream in, Runnable listener, Runnable repaint) throws IOException {
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new BufferedInputStream(in)));
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    EventQueue.invokeLater(listener);
                    repaint.run();
                }
            });
        } catch (LineUnavailableException | UnsupportedAudioFileException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void start() {
        clip.start();
    }

    @Override
    public void stop() {
        clip.stop();
    }
}
