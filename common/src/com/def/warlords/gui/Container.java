package com.def.warlords.gui;

import com.def.warlords.graphics.Cursor;
import com.def.warlords.util.Util;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class Container extends Component {

    final List<Component> components = new ArrayList<>();

    Component activeComponent;

    public Container(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public <T extends Component> T add(T component) {
        components.add(component);
        return component;
    }

    public void remove(Component component) {
        if (activeComponent != null) {
            throw new IllegalStateException("Container has active component");
        }
        components.remove(component);
    }

    public void clear() {
        if (activeComponent != null) {
            throw new IllegalStateException("Container has active component");
        }
        components.clear();
    }

    @Override
    public void paint(Graphics g) {
        if (!isVisible()) {
            return;
        }
        for (final Component component : components) {
            if (component.isVisible()) {
                component.paint(g);
            }
        }
    }

    @Override
    public Cursor getCursor(MouseEvent e) {
        if (activeComponent != null) {
            return activeComponent.getCursor(e);
        }
        if (!isVisible()) {
            return Cursor.DEFAULT;
        }
        for (final Component component : Util.reverse(components)) {
            if (component.isVisible() && component.contains(e.getPoint())) {
                return component.getCursor(e);
            }
        }
        return super.getCursor(e);
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        if (activeComponent != null) {
            throw new IllegalStateException("Container has active component");
        }
        if (!isEnabled()) {
            return false;
        }
        for (final Component component : Util.reverse(components)) {
            if (component.isEnabled() && component.contains(e.getPoint())) {
                if (component.mousePressed(e)) {
                    activeComponent = component;
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (activeComponent != null) {
            final Component component = activeComponent;
            activeComponent = null;
            component.mouseReleased(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (activeComponent != null) {
            activeComponent.mouseDragged(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!isEnabled()) {
            return;
        }
        for (final Component component : Util.reverse(components)) {
            if (component.isEnabled()) {
                component.keyPressed(e);
            }
        }
    }
}
