package com.def.warlords.graphics;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class Sprite {

    final int width, height;

    public Sprite(int width, int height) {
        if (width <= 0 || height <= 0 || (height & 1) > 0) {
            throw new IllegalArgumentException("Invalid size");
        }
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void draw(Graphics g, int x, int y) {
    }
}
