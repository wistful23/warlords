package com.def.warlords.control.form;

import com.def.warlords.graphics.Cursor;
import com.def.warlords.sound.Sound;
import com.def.warlords.sound.SoundInfo;

import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class SoundForm extends EmptyForm {

    private static final int DELAY_SILENCE = 1000;

    private final SoundInfo soundInfo;

    private Sound sound;

    public SoundForm(FormController controller, SoundInfo soundInfo) {
        super(controller);
        this.soundInfo = soundInfo;
    }

    @Override
    public void start() {
        if (sound != null) {
            throw new IllegalStateException("Sound is already started");
        }
        sound = controller.getSound(soundInfo, this::deactivate);
        if (sound != null) {
            sound.start();
        } else {
            invokeLater(this::deactivate, DELAY_SILENCE);
        }
    }

    @Override
    public void stop() {
        if (sound != null) {
            sound.stop();
        }
    }

    @Override
    public Cursor getCursor(MouseEvent e) {
        return Cursor.EMPTY;
    }
}
