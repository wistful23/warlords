package com.def.warlords.control.form;

import com.def.warlords.graphics.Cursor;
import com.def.warlords.sound.Sound;
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

    private Sound sound;

    public SoundForm(FormController controller, SoundInfo soundInfo) {
        super(controller);
        this.soundInfo = soundInfo;
    }

    @Override
    void init() {
        sound = controller.getSound(soundInfo, this::deactivate);
        if (sound != null) {
            sound.start();
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
        if (sound != null) {
            sound.stop();
        }
        deactivate();
    }
}
