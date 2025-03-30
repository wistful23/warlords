package com.def.warlords.control.form;

import com.def.warlords.graphics.Cursor;
import com.def.warlords.sound.Sound;
import com.def.warlords.sound.SoundFactory;
import com.def.warlords.sound.SoundInfo;

import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class SoundForm extends EmptyForm {

    private final SoundInfo soundInfo;

    private Sound sound;

    public SoundForm(FormController controller, SoundInfo soundInfo) {
        super(controller);
        this.soundInfo = soundInfo;
    }

    @Override
    void init() {
        sound = SoundFactory.getInstance().createSound(soundInfo, this::deactivate);
        sound.start();
    }

    @Override
    void close() {
        sound.stop();
    }

    @Override
    public Cursor getCursor(MouseEvent e) {
        return Cursor.EMPTY;
    }
}
