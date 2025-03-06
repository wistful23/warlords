package com.def.warlords.control.form;

import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.Label;
import com.def.warlords.gui.TextButton;

/**
 * @author wistful23
 * @version 1.23
 */
public class QuitForm extends Form {

    public QuitForm(FormController controller) {
        super(controller);
    }

    @Override
    void init() {
        add(new GrayPanel(42, 84, 292, 172));
        add(new Label(120, 138, "Exit to DOS?"));
        add(new TextButton(76, 194, " Exit ", source -> System.exit(0)));
        add(new TextButton(256, 194, "Cancel", source -> deactivate()));
    }
}
