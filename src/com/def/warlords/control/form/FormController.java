package com.def.warlords.control.form;

import com.def.warlords.sound.Sound;
import com.def.warlords.sound.SoundInfo;

import javax.swing.Timer;
import java.awt.event.ActionListener;

/**
 * @author wistful23
 * @version 1.23
 */
public interface FormController {

    void activateForm(Form form);
    void deactivateForm(Form form);

    Timer createTimer(int delay, ActionListener listener);
    Sound createSound(SoundInfo soundInfo, Runnable listener);
}
