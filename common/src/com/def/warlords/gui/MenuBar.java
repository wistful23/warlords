package com.def.warlords.gui;

import com.def.warlords.graphics.Palette;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class MenuBar extends Container {

    private final int menuHeight;

    private int right = 8;

    public MenuBar(int x, int y, int width, int height, int menuHeight) {
        super(x, y, width, height);
        this.menuHeight = menuHeight;
        setVisible(false);
    }

    public Menu addMenu(int menuWidth, String text, int margin, int dropWidth) {
        final Menu menu = add(new Menu(right, 0, menuWidth, menuHeight, text, margin, dropWidth));
        right += menuWidth;
        return menu;
    }

    @Override
    public void paint(Graphics g) {
        // Bar.
        g.setColor(Palette.WHITE);
        g.fillRect(x, y, width, menuHeight);
        g.setColor(Palette.BLACK);
        g.fillRect(x, menuHeight, width, 2);
        // Menus.
        super.paint(g);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setVisible(true);
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setVisible(false);
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // NOTE: W always selects the last menu if the rest empty space of the menu bar is clicked.
        for (final Component component : components) {
            if (component != activeComponent && component.isEnabled() && component.contains(e.getPoint())) {
                if (activeComponent != null) {
                    activeComponent.setSelected(false);
                }
                activeComponent = component;
                activeComponent.setSelected(true);
                break;
            }
        }
        super.mouseDragged(e);
    }
}
