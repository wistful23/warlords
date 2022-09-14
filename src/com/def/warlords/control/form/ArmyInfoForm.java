package com.def.warlords.control.form;

import com.def.warlords.control.common.Sprites;
import com.def.warlords.game.model.ArmyFactory;
import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.Image;
import com.def.warlords.gui.Label;
import com.def.warlords.gui.TextButton;

/**
 * @author wistful23
 * @version 1.23
 */
public class ArmyInfoForm extends Form {

    private final ArmyFactory armyFactory;

    public ArmyInfoForm(FormController controller, ArmyFactory armyFactory) {
        super(controller);
        this.armyFactory = armyFactory;
    }

    @Override
    void init() {
        add(new GrayPanel(14, 66, 348, 208));
        add(new Label(188, 96, armyFactory.getType().getName()));
        add(new Label(188, 128, armyFactory.getCity().getName()));
        add(new Label(188, 160, "Move: " + armyFactory.getMovement()));
        add(new Label(188, 192, "Strength: " + armyFactory.getStrength()));
        add(new Image(40, 88, Sprites.getArmyFactorySprite(armyFactory)));
        add(new TextButton(256, 224, "  OK  ", source -> deactivate()));
    }
}
