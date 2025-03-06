package com.def.warlords.control.form;

import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.Label;
import com.def.warlords.gui.MultiLineLabel;
import com.def.warlords.gui.TextButton;

/**
 * @author wistful23
 * @version 1.23
 */
public class SurrenderResultForm extends ResultForm<Boolean> {

    public SurrenderResultForm(FormController controller) {
        super(controller);
    }

    @Override
    void init() {
        // NOTE: W displays a slightly different panel frame.
        add(new GrayPanel(13, 14, 350, 312));
        add(new Label(92, 42, "MIGHTY WARLORD!!"));
        add(new MultiLineLabel(36, 70, 6, new String[] {
                "The fickle tides of war have turned",
                "your way and the Gods favour you!",
                "The few remaining leaders send",
                "ambassadors to offer you all their",
                "lands if you will but spare their",
                "insignificant, wretched lives!"
        }));
        add(new TextButton(44, 230, " Accept a grovelling surrender! ", source -> setResult(true)));
        add(new TextButton(44, 266, " Off with their miserable heads! ", source -> setResult(false)));
    }
}
