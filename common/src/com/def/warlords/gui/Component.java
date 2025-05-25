package com.def.warlords.gui;

import com.def.warlords.graphics.Cursor;
import com.def.warlords.graphics.Palette;
import com.def.warlords.util.DeveloperMode;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class Component {

    final int x, y, width, height;

    private boolean enabled = true;
    private boolean visible = true;
    boolean selected;

    private Object tag;

    protected Component(int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid component size");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public boolean contains(Point p) {
        return p.x >= x && p.y >= y && p.x < x + width && p.y < y + height;
    }

    public void paint(Graphics g) {
        if (DeveloperMode.isOn()) {
            g.setColor(Palette.YELLOW);
            g.drawRect(x, y, width, height);
        }
    }

    public Cursor getCursor(MouseEvent e) {
        return Cursor.DEFAULT;
    }

    // The subclass has to return true to make this component active.
    public boolean mousePressed(MouseEvent e) {
        return false;
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }
}
