package com.def.warlords.control.form;

import com.def.warlords.graphics.Cursor;
import com.def.warlords.sound.Player;
import com.def.warlords.sound.SoundFactory;
import com.def.warlords.sound.SoundInfo;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class SoundForm extends Form {

    private static final int DELAY_SILENCE = 1000;

    private final SoundInfo soundInfo;

    private Player audioPlayer;

    public SoundForm(FormController controller, SoundInfo soundInfo) {
        super(controller);
        this.soundInfo = soundInfo;
    }

    @Override
    void init() {
        audioPlayer = SoundFactory.getInstance().getAudioPlayer(soundInfo, this::deactivate);
        if (audioPlayer != null) {
            audioPlayer.start();
        } else {
            invokeLater(this::deactivate, DELAY_SILENCE);
        }
    }

    @Override
    public Cursor getCursor(MouseEvent e) {
        return Cursor.EMPTY;
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        stop();
        return false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        stop();
    }

    private void stop() {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }
        deactivate();
    }
}
