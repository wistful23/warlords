package com.def.warlords.control.form;

import com.def.warlords.sound.Sound;
import com.def.warlords.sound.SoundInfo;
import com.def.warlords.util.Timer;

/**
 * @author wistful23
 * @version 1.23
 */
public interface FormController {

    void activateForm(Form form);
    void deactivateForm(Form form);

    Sound getSound(SoundInfo soundInfo, Runnable listener);
    Timer createTimer(Runnable listener);
    void invokeLater(Runnable action, int delay);
}
