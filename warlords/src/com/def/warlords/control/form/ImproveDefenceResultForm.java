package com.def.warlords.control.form;

import com.def.warlords.game.model.City;
import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.Label;
import com.def.warlords.gui.TextButton;

/**
 * @author wistful23
 * @version 1.23
 */
public class ImproveDefenceResultForm extends ResultForm<Boolean> {

    private final City city;

    public ImproveDefenceResultForm(FormController controller, City city) {
        super(controller);
        this.city = city;
    }

    @Override
    void init() {
        add(new GrayPanel(14, 76, 348, 188));
        add(new Label(14, 102, 348, Label.Alignment.CENTER, "Improve City Defences?"));
        add(new Label(14, 130, 348, Label.Alignment.CENTER, "Current Defences: " + city.getDefence()));
        add(new Label(14, 152, 348, Label.Alignment.CENTER, "Improvement cost " + city.getDefencePrice() + " gp"));
        add(new Label(14, 174, 348, Label.Alignment.CENTER, "You have " + city.getEmpire().getGold() + " gp"));
        add(new TextButton(48, 206, " Yes ", source -> setResult(true)));
        add(new TextButton(228, 206, " No ", source -> setResult(false)));
    }
}
