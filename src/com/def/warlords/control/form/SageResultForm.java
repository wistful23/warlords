package com.def.warlords.control.form;

import com.def.warlords.gui.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class SageResultForm extends ResultForm<SageResult> {

    public SageResultForm(FormController controller) {
        super(controller);
    }

    @Override
    void init() {
        add(new GrayPanel(42, 28, 292, 284));
        add(new Label(64, 54, "A sign says"));
        add(new Frame(101, 82, 181, 114, Frame.Type.BLACK));
        add(new MultiLineLabel(106, 90, 2, new String[] {
                "The Great Sage",
                "Master of Wisdom,",
                "Information on:",
                "Magical Items",
                "and Locations"
        }));
        add(new MultiLineLabel(64, 202, 2, new String[] {
                "The sage welcomes you",
                "What do you wish to know?"
        }));
        add(new TextButton(72, 250, "Items", source -> setResult(SageResult.ITEMS)));
        add(new TextButton(136, 250, "Locations", source -> setResult(SageResult.LOCATIONS)));
        add(new TextButton(232, 250, "Cancel", source -> setResult(SageResult.CANCEL)));
    }
}
