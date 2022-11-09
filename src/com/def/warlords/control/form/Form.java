package com.def.warlords.control.form;

import com.def.warlords.gui.Container;
import com.def.warlords.sound.Sound;
import com.def.warlords.sound.SoundInfo;

import javax.swing.Timer;
import java.awt.event.ActionListener;

import static com.def.warlords.control.common.Dimensions.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class Form extends Container {

    private final FormController controller;

    public Form(FormController controller) {
        super(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.controller = controller;
    }

    public final void activate() {
        reset();
        controller.activateForm(this);
    }

    public final void deactivate() {
        close();
        controller.deactivateForm(this);
    }

    Timer createTimer(int delay, ActionListener listener) {
        return controller.createTimer(delay, listener);
    }

    Sound createSound(SoundInfo soundInfo, Runnable listener) {
        return controller.createSound(soundInfo, listener);
    }

    void reset() {
        clear();
        init();
    }

    void init() {
    }

    void close() {
    }
}
