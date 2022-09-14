package com.def.warlords.gui;

import com.def.warlords.graphics.Font;
import com.def.warlords.graphics.FontFactory;
import com.def.warlords.graphics.Palette;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class MenuItem extends Component {

    public interface Listener {
        void menuItemClicked(MenuItem source);
    }

    private final String text;
    private final String hint;
    private final int keyCode, keyModifier;
    private final Listener listener;

    MenuItem(int x, int y, int width, int height, String text, String hint, int keyCode, int keyModifier,
             Listener listener) {
        super(x, y, width, height);
        this.text = text;
        this.hint = hint;
        this.keyCode = keyCode;
        this.keyModifier = keyModifier;
        this.listener = listener;
    }

    @Override
    public void paint(Graphics g) {
        final Font font = FontFactory.getInstance().getMonospacedFont();
        if (selected) {
            g.setColor(Palette.BROWN_LIGHT);
            g.fillRect(x + 1, y, width - 2, height);
            g.setColor(Palette.YELLOW);
        } else {
            g.setColor(Palette.BLACK);
        }
        font.drawString(g, x + 11, y + 2, text);
        font.drawString(g, x + width - font.getLength(hint) - 10, y + 2, hint);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (keyCode == e.getKeyCode() && keyModifier == e.getModifiersEx()) {
            listener.menuItemClicked(this);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (selected) {
            listener.menuItemClicked(this);
            selected = false;
        }
    }
}
