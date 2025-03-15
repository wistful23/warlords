package com.def.warlords.control;

import com.def.warlords.control.common.Sprites;
import com.def.warlords.game.Game;
import com.def.warlords.game.PlayerLevel;
import com.def.warlords.game.model.EmpireType;
import com.def.warlords.graphics.Palette;
import com.def.warlords.gui.*;
import com.def.warlords.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class SetupContainer extends Container {

    private final List<PlayerParams> playerParams = new ArrayList<>(Game.MAX_PLAYER_COUNT);
    private int rating;

    public SetupContainer(MainController controller) {
        super(0, 0, 640, 400);
        add(new Image(8, 10, Sprites.SETUP_PANEL));
        add(new Rectangle(408, 10, 224, 320, Palette.BLACK));
        final Image bannerImage = add(new Image(412, 14, Sprites.SETUP_BANNER));
        // NOTE: W doesn't display the difficulty rating at startup.
        add(new Label(240, 356, "Difficulty Rating"));
        final Label ratingLabel = add(new Label(388, 356, 40, Label.Alignment.LEFT, "0%"));
        for (final EmpireType empireType : EmpireType.values()) {
            if (empireType == EmpireType.NEUTRAL) {
                continue;
            }
            final PlayerParams params = new PlayerParams(empireType, PlayerLevel.HUMAN);
            playerParams.add(params);
            final int row = empireType.ordinal() - 1;
            final int y = 26 + row * 30;
            final Image levelImage = add(new Image(176, y, Sprites.getPlayerLevelSprite(params.getLevel())));
            final Label levelLabel =
                    add(new Label(224, y + 8, 100, Label.Alignment.LEFT, params.getLevel().getName()));
            add(new Label(324, y + 8, "F" + (row + 1)));
            add(new Button(30, y, 315, 30, false, source -> {
                rating -= params.getLevel().getRating();
                params.nextLevel();
                levelImage.setSprite(Sprites.getPlayerLevelSprite(params.getLevel()));
                levelLabel.setText(params.getLevel().getName());
                rating += params.getLevel().getRating();
                // NOTE: W rounds 98 to 100.
                ratingLabel.setText(Util.truncate(rating, 100) + "%");
            }));
        }
        // NOTE: W doesn't display the Load button if there is no save files.
        // NOTE: W doesn't allow to press the Cancel button in the load form.
        // BUG: The pressed sprites for the Start and Load buttons are cut.
        add(new ImageButton(48, 274, Sprites.SETUP_BUTTON_LOAD, source -> controller.loadGame()));
        add(new ImageButton(216, 274, Sprites.SETUP_BUTTON_START, source -> {
            // NOTE: W cuts ellipsis in Begin...
            bannerImage.setSprite(Sprites.SETUP_WAR_BEGIN);
            controller.startGame(playerParams);
        }));
    }
}
