package com.def.warlords.control;

import com.def.warlords.control.common.Sprites;
import com.def.warlords.game.model.Army;
import com.def.warlords.graphics.Cursor;
import com.def.warlords.gui.Button;
import com.def.warlords.gui.Container;
import com.def.warlords.gui.ImageButton;

import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class CommandBar extends Container {

    private final Button swordButton;

    public CommandBar(MainController controller) {
        super(370, 10, 36, 320);
        final PlayingMap playingMap = controller.getPlayingMap();
        add(new ImageButton(370, 10, Sprites.CMD_BUTTON_FLAG, source -> playingMap.getArmySelection().reset()));
        swordButton = add(new ImageButton(370, 88, true, Sprites.CMD_BUTTON_SWORD,
                source -> playingMap.enableProductionMode()));
        add(new ImageButton(370, 168, true, Sprites.CMD_BUTTON_INFO, source -> {
            controller.showArmies();
            source.release();
        }));
        add(new ImageButton(370, 204, Sprites.CMD_BUTTON_CENTER, source -> playingMap.centerArmySelection()));
        add(new ImageButton(370, 236, Sprites.CMD_BUTTON_NEXT, source -> playingMap.nextArmyGroup(Army.State.ACTIVE)));
        add(new ImageButton(370, 268, Sprites.CMD_BUTTON_QUIT, source -> playingMap.nextArmyGroup(Army.State.QUIT)));
        add(new ImageButton(370, 300, Sprites.CMD_BUTTON_DEFEND,
                source -> playingMap.nextArmyGroup(Army.State.DEFENDED)));
    }

    public void reset() {
        swordButton.release();
    }

    @Override
    public Cursor getCursor(MouseEvent e) {
        return Cursor.TARGET;
    }
}
