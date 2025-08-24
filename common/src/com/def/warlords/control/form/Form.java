package com.def.warlords.control.form;

import com.def.warlords.gui.Container;

import static com.def.warlords.control.common.Dimensions.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class Form extends Container {

    final FormController controller;

    public Form(FormController controller) {
        super(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        this.controller = controller;
    }

    public final void activate() {
        controller.activateForm(this);
    }

    public final void deactivate() {
        controller.deactivateForm(this);
    }

    public void start() {
        reset();
    }

    public void stop() {
    }

    void invokeLater(Runnable action, int delay) {
        controller.invokeLater(action, delay);
    }

    void reset() {
        clear();
        init();
    }

    void init() {
    }
}
