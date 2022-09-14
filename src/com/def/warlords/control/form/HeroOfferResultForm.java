package com.def.warlords.control.form;

import com.def.warlords.game.model.City;
import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.Label;
import com.def.warlords.gui.TextButton;

/**
 * @author wistful23
 * @version 1.23
 */
public class HeroOfferResultForm extends ResultForm<Boolean> {

    private final City city;
    private final int cost;

    public HeroOfferResultForm(FormController controller, City city, int cost) {
        super(controller);
        this.city = city;
        this.cost = cost;
    }

    @Override
    void init() {
        add(new GrayPanel(42, 84, 292, 172));
        add(new Label(42, 118, 292, Label.Alignment.CENTER, "A hero in " + city.getName() + " offers"));
        add(new Label(42, 142, 292, Label.Alignment.CENTER, "to join you for " + cost + " gp!"));
        add(new Label(42, 166, 292, Label.Alignment.CENTER, "You have " + city.getEmpire().getGold() + " gp"));
        add(new TextButton(76, 202, " Accept ", source -> setResult(true)));
        add(new TextButton(232, 202, " Reject ", source -> setResult(false)));
    }
}
