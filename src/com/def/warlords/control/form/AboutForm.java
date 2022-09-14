package com.def.warlords.control.form;

import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.Label;
import com.def.warlords.gui.MultiLineLabel;
import com.def.warlords.gui.TextButton;

/**
 * @author wistful23
 * @version 1.23
 */
public class AboutForm extends Form {

    public AboutForm(FormController controller) {
        super(controller);
    }

    @Override
    void init() {
        add(new GrayPanel(14, 16, 348, 308));
        add(new Label(56, 34, "W A R L O R D S  Ver 2.10"));
        add(new Label(36, 62, "Design, Graphics & Programming"));
        add(new Label(92, 84, "by Steve Fawkner"));
        add(new MultiLineLabel(36, 110, 6, new String[] {
                "AI by Roger Keating",
                "IBM version by Stephen Hart",
                "IBM utilities by Simon Hayes",
                "A Gregor Whiley Production"
        }));
        add(new MultiLineLabel(36, 210, 6, new String[] {
                "Ideas (radical and clever) by",
                "Janeen Andrews, Karl-Peter Baum,",
                "Phillip Grinstein, Mark Hill,",
                "Justin Weaver and TIM"
        }));
        add(new TextButton(273, 280, " Cancel ", source -> deactivate()));
    }
}
