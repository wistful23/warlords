package com.def.warlords.control.form;

import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.InputBox;
import com.def.warlords.gui.Label;
import com.def.warlords.gui.TextButton;

/**
 * @author wistful23
 * @version 1.23
 */
public class HeroNameResultForm extends ResultForm<String> {

    private final String initialName;

    public HeroNameResultForm(FormController controller, String initialName) {
        super(controller);
        this.initialName = initialName;
    }

    @Override
    void init() {
        add(new GrayPanel(86, 108, 203, 124));
        add(new Label(131, 132, "Name of Hero"));
        final InputBox inputBox =
                add(new InputBox(109, 156, 156, 19, initialName, this::createTimer,
                        add(new TextButton(127, 186, "Name", true)), null));
        add(new TextButton(205, 186, "Done", source -> {
            inputBox.commitEditing();
            setResult(inputBox.getText());
        }));
        inputBox.startEditing();
    }
}
