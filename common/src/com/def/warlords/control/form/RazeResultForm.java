package com.def.warlords.control.form;

import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.Label;
import com.def.warlords.gui.TextButton;

/**
 * @author wistful23
 * @version 1.23
 */
public class RazeResultForm extends ResultForm<Boolean> {

    public RazeResultForm(FormController controller) {
        super(controller);
    }

    @Override
    void init() {
        add(new GrayPanel(42, 84, 292, 172));
        add(new Label(94, 138, "This won't be popular!"));
        add(new TextButton(76, 194, " Raze it! ", source -> setResult(true)));
        add(new TextButton(240, 194, " Cancel ", source -> setResult(false)));
    }
}
