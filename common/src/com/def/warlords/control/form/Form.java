package com.def.warlords.control.form;

import com.def.warlords.gui.Container;
import com.def.warlords.util.Timer;

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

    Timer createTimer(Runnable listener) {
        return controller.createTimer(listener);
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
