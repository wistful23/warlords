package com.def.warlords.gui;

import com.def.warlords.graphics.Cursor;
import com.def.warlords.graphics.Palette;
import com.def.warlords.util.DeveloperMode;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.*;

/**
 * @author wistful23
 * @version 1.23
 */
public class Component implements MouseListener, MouseMotionListener, KeyListener {

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

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
