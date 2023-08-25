package com.def.warlords.control.form;

import com.def.warlords.control.common.Sprites;
import com.def.warlords.game.Game;
import com.def.warlords.game.Player;
import com.def.warlords.game.model.EmpireType;
import com.def.warlords.gui.GrayPanel;
import com.def.warlords.gui.Image;
import com.def.warlords.gui.Label;

import static com.def.warlords.control.common.Dimensions.SCREEN_WIDTH;

/**
 * @author wistful23
 * @version 1.23
 */
public class HatredReportForm extends EmptyForm {

    private final Game game;

    public HatredReportForm(FormController controller, Game game) {
        super(controller);
        this.game = game;
    }

    @Override
    void init() {
        add(new Label(0, 356, SCREEN_WIDTH, Label.Alignment.CENTER, "How the other leaders view you"));
        add(new GrayPanel(14, 16, 348, 308));
        final EmpireType currentEmpireType = game.getCurrentPlayer().getEmpire().getType();
        for (int index = 0; index < game.getPlayerCount(); ++index) {
            final int y = 42 + 32 * index;
            final Player player = game.getPlayer(index);
            add(new Label(36, y, player.getEmpire().getName()));
            add(new Image(176, y - 10, Sprites.getPlayerLevelSprite(player.getLevel())));
            final String attitude = player.getEmpire().getAttitude(currentEmpireType).getName();
            add(new Label(220, y, player.isDestroyed() ? "Deceased" : player.isHuman() ? "Human" : attitude));
            add(new Image(312, y - 10, player.isDestroyed()
                    ? Sprites.PLAYER_DESTROYED
                    : Sprites.getEmpireSwordSprite(player.getEmpire().getArchenemy())));
        }
    }
}
