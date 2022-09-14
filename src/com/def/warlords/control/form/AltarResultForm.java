package com.def.warlords.control.form;

import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.Label;
import com.def.warlords.gui.TextButton;

/**
 * @author wistful23
 * @version 1.23
 */
public class AltarResultForm extends ResultForm<Boolean> {

    public AltarResultForm(FormController controller) {
        super(controller);
    }

    @Override
    void init() {
        add(new GrayPanel(42, 84, 292, 172));
        add(new Label(64, 118, "You have found a Holy Altar"));
        add(new Label(64, 158, "Will you pray at this Altar?"));
        add(new TextButton(76, 194, " Yes ", source -> setResult(true)));
        add(new TextButton(256, 194, " No ", source -> setResult(false)));
    }
}
