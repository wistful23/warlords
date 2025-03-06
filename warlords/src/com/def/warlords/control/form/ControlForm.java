package com.def.warlords.control.form;

import com.def.warlords.control.common.Sprites;
import com.def.warlords.game.Game;
import com.def.warlords.game.Player;
import com.def.warlords.game.PlayerLevel;
import com.def.warlords.graphics.Font;
import com.def.warlords.graphics.FontFactory;
import com.def.warlords.gui.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class ControlForm extends Form {

    private final Game game;

    public ControlForm(FormController controller, Game game) {
        super(controller);
        this.game = game;
    }

    @Override
    void init() {
        add(new GrayPanel(8, 10, 360, 320));
        final Font font = FontFactory.getInstance().getMonospacedFont();
        add(new Label(26, 26, font, "Observe"));
        add(new Label(109, 26, font, "Player"));
        add(new Label(202, 26, font, "Human"));
        add(new Label(298, 26, font, "Enhance"));
        for (int index = 0; index < game.getPlayerCount(); ++index) {
            final int y = 54 + 30 * index;
            final Player player = game.getPlayer(index);
            add(new Label(81, y, font, player.getEmpire().getName()));
            // NOTE: W displays offset computer sprites on top of the human sprite.
            final Image levelImage =
                    add(new Image(260, y - 8, player.isDestroyed()
                            ? Sprites.PLAYER_DESTROYED
                            : Sprites.getPlayerLevelSprite(player.getLevel())));
            final Button observeButton =
                    add(new SwitchButton(34, y - 2, source -> player.setObserved(source.isSelected())));
            observeButton.setEnabled(!player.isDestroyed() && !player.isHuman());
            observeButton.setSelected(player.isObserved());
            final Button enhanceButton =
                    add(new SwitchButton(320, y - 2, source -> player.getEmpire().setEnhanced(source.isSelected())));
            enhanceButton.setEnabled(!player.isDestroyed());
            enhanceButton.setSelected(player.getEmpire().isEnhanced());
            final PlayerLevel computerLevel = player.isHuman() ? PlayerLevel.WARLORD : player.getLevel();
            final Button humanButton = add(new SwitchButton(208, y - 2, source -> {
                player.setLevel(source.isSelected() ? PlayerLevel.HUMAN : computerLevel);
                levelImage.setSprite(Sprites.getPlayerLevelSprite(player.getLevel()));
                observeButton.setEnabled(!source.isSelected());
            }));
            humanButton.setEnabled(!player.isDestroyed());
            humanButton.setSelected(player.isHuman());
        }
        add(new Label(72, 294, font, "Intense Combat"));
        add(new SwitchButton(208, 292,
                source -> game.setIntenseCombat(source.isSelected()))).setSelected(game.isIntenseCombat());
        add(new TextButton(289, 288, "  OK  ", source -> deactivate()));
    }
}
