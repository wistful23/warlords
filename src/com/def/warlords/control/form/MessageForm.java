package com.def.warlords.control.form;

import com.def.warlords.control.common.Dimensions;
import com.def.warlords.gui.Label;

/**
 * @author wistful23
 * @version 1.23
 */
public class MessageForm extends EmptyForm {

    private final String text;

    public MessageForm(FormController controller, String text) {
        super(controller);
        this.text = text;
    }

    @Override
    void init() {
        add(new Label(0, 356, Dimensions.SCREEN_WIDTH, Label.Alignment.CENTER, text));
    }
}
