package com.def.warlords.control.form;

import com.def.warlords.control.common.Dimensions;
import com.def.warlords.gui.Label;

/**
 * @author wistful23
 * @version 1.23
 */
public class MessageForm extends EmptyForm {

    private static final int DELAY_MESSAGE = 3000;

    private final String text;
    private final boolean timed;

    public MessageForm(FormController controller, String text, boolean timed) {
        super(controller);
        this.text = text;
        this.timed = timed;
    }

    @Override
    void init() {
        add(new Label(0, 356, Dimensions.SCREEN_WIDTH, Label.Alignment.CENTER, text));
        if (timed) {
            invokeLater(this::deactivate, DELAY_MESSAGE);
        }
    }
}
