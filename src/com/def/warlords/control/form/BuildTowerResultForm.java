package com.def.warlords.control.form;

import com.def.warlords.game.Game;
import com.def.warlords.game.Player;
import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.Label;
import com.def.warlords.gui.TextButton;

/**
 * @author wistful23
 * @version 1.23
 */
public class BuildTowerResultForm extends ResultForm<Boolean> {

    private final Player player;

    public BuildTowerResultForm(FormController controller, Player player) {
        super(controller);
        this.player = player;
    }

    @Override
    void init() {
        add(new GrayPanel(14, 76, 348, 188));
        add(new Label(14, 102, 348, Label.Alignment.CENTER, "Build a Tower?"));
        // NOTE: W doesn't display 'gp' suffix.
        add(new Label(14, 130, 348, Label.Alignment.CENTER, "Cost: " + Game.TOWER_PRICE + " gp"));
        add(new Label(14, 174, 348, Label.Alignment.CENTER, "You have " + player.getGold() + " gp"));
        add(new TextButton(48, 206, " Yes ", source -> setResult(true)));
        add(new TextButton(228, 206, " No ", source -> setResult(false)));
    }
}
