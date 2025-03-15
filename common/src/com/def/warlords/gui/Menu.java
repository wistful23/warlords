package com.def.warlords.gui;

import com.def.warlords.graphics.Font;
import com.def.warlords.graphics.FontFactory;
import com.def.warlords.graphics.Palette;
import com.def.warlords.util.Toggle;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class Menu extends Container {

    private static final class Divider extends Component {

        Divider(int x, int y, int width, int height) {
            super(x, y, width, height);
            setEnabled(false);
        }

        @Override
        public void paint(Graphics g) {
            g.setColor(Palette.BLACK);
            g.fillRect(x + 1, y, width - 2, height);
        }
    }

    private final String text;
    private final int margin, dropWidth;
    private int bottom;

    public Menu(int x, int y, int width, int height, String text, int margin, int dropWidth) {
        super(x, y, width, height);
        this.text = text;
        this.margin = margin;
        this.dropWidth = dropWidth;
        this.bottom = y + height + 2;
    }

    public void addMenuItem(String text, String hint, int keyCode, MenuItem.Listener listener) {
        addMenuItem(text, hint, keyCode, 0, listener);
    }

    public void addMenuItem(String text, String hint, int keyCode, int keyModifier, MenuItem.Listener listener) {
        addMenuItem(text, hint, keyCode, keyModifier, null, listener);
    }

    public void addMenuItem(String text, String hint, int keyCode, int keyModifier, Toggle toggle) {
        addMenuItem(text, hint, keyCode, keyModifier, toggle, null);
    }

    public void addMenuItem(String text, String hint, int keyCode, int keyModifier,
                            Toggle toggle, MenuItem.Listener listener) {
        add(new MenuItem(x, bottom + 2, dropWidth, height, text, hint, keyCode, keyModifier, toggle, listener));
        bottom += height + 2;
    }

    public void addDivider() {
        add(new Divider(x, bottom + 2, dropWidth, 2));
        bottom += 4;
    }

    @Override
    public void paint(Graphics g) {
        final Font font = FontFactory.getInstance().getMonospacedFont();
        if (!isSelected()) {
            g.setColor(Palette.BLACK);
            font.drawString(g, x + margin, y + 2, text);
            return;
        }
        // Header.
        g.setColor(Palette.BROWN);
        g.fillRect(x, y, width, height);
        g.setColor(Palette.GRAY_LIGHT);
        font.drawString(g, x + margin, y + 2, text);
        // Background.
        g.setColor(Palette.WHITE);
        g.fillRect(x + 1, y + height + 2, dropWidth - 2, bottom - y - height + 2);
        // Frame.
        g.setColor(Palette.BLACK);
        // Left.
        g.fillRect(x, y + height + 2, 1, bottom - y - height + 2);
        // Right.
        g.fillRect(x + dropWidth - 1, y + height + 2, 1, bottom - y - height + 2);
        g.fillRect(x + dropWidth, y + height + 4, 1, bottom - y - height);
        // Bottom.
        g.fillRect(x, bottom + 2, dropWidth + 1, 2);
        g.fillRect(x + 2, bottom + 4, dropWidth - 1, 2);
        // Menu items.
        super.paint(g);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setSelected(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setSelected(false);
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (activeComponent != null) {
            activeComponent.setSelected(false);
            activeComponent = null;
        }
        for (final Component component : components) {
            if (component.isEnabled() && component.contains(e.getPoint())) {
                activeComponent = component;
                activeComponent.setSelected(true);
                break;
            }
        }
    }
}
