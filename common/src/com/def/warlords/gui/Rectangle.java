package com.def.warlords.gui;

import java.awt.Color;
import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class Rectangle extends Component {

    private final Color color;

    public Rectangle(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);
        this.color = color;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}
