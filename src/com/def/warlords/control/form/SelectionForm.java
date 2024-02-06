package com.def.warlords.control.form;

import com.def.warlords.control.common.Sprites;
import com.def.warlords.game.model.Army;
import com.def.warlords.game.model.ArmyList;
import com.def.warlords.graphics.Font;
import com.def.warlords.graphics.FontFactory;
import com.def.warlords.graphics.Palette;
import com.def.warlords.gui.*;
import com.def.warlords.util.Util;

/**
 * @author wistful23
 * @version 1.23
 */
public class SelectionForm extends Form {

    private static final int SHORT_NAME_LENGTH = 15;
    private static final int MAX_NAME_LENGTH = 19;

    private final ArmyList armies;

    public SelectionForm(FormController controller, ArmyList armies) {
        super(controller);
        this.armies = armies;
    }

    @Override
    void init() {
        add(new GrayPanel(8, 10, 360, 320));
        final Font font = FontFactory.getInstance().getMonospacedFont();
        add(new Label(134, 36, font, "Name"));
        add(new Label(224, 36, font, "Bonus Move Strn"));
        armies.arrange(false);
        for (int index = 0; index < armies.size(); ++index) {
            final int y = 60 + 30 * index;
            final Army army = armies.get(index);
            add(new SwitchButton(32, y - 2, source -> army.setSelected(source.isSelected())))
                    .setSelected(army.isSelected());
            final String name = army.isHero() ? army.getName() : army.getType().getName();
            if (name.length() <= SHORT_NAME_LENGTH) {
                add(new Image(62, y - 8, Sprites.getArmySprite(army)));
                add(new Label(102, y, font, name));
            } else {
                add(new Label(70, y, font, Util.truncate(name, MAX_NAME_LENGTH)));
            }
            String bonus = "  -";
            if (army.isHero()) {
                // BUG: W displays incorrect hero bonus value.
                bonus = "+";
                add(new Label(242, y, 16, font, Label.Alignment.RIGHT, Palette.WHITE, army.getCombatModifier() + ""));
            } else if (army.isSpecial() && army.isFlying()) {
                bonus = "s/f";
            } else if (army.isSpecial()) {
                bonus = "spc";
            } else if (army.isFlying()) {
                bonus = "fly";
            }
            add(new Label(232, y, font, Palette.WHITE, bonus));
            add(new Label(282, y, 16, font, Label.Alignment.RIGHT, army.getMovementPoints() + ""));
            add(new Label(330, y, font, army.getTotalStrength() + ""));
        }
        add(new TextButton(289, 288, "  OK  ", source -> deactivate()));
    }
}
