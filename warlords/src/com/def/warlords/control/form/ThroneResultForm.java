package com.def.warlords.control.form;

import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.Label;
import com.def.warlords.gui.TextButton;

/**
 * @author wistful23
 * @version 1.23
 */
public class ThroneResultForm extends ResultForm<Boolean> {

    public ThroneResultForm(FormController controller) {
        super(controller);
    }

    @Override
    void init() {
        add(new GrayPanel(42, 84, 292, 172));
        add(new Label(64, 110, "You have found a huge"));
        add(new Label(64, 130, "Golden Throne"));
        add(new Label(64, 158, "Will you sit in the throne?"));
        add(new TextButton(76, 194, " Yes ", source -> setResult(true)));
        add(new TextButton(256, 194, " No ", source -> setResult(false)));
    }
}
