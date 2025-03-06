package com.def.warlords.gui;

import com.def.warlords.graphics.SpritePair;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class ImageButton extends Button {

    private final SpritePair spritePair;

    public ImageButton(int x, int y, SpritePair spritePair, Listener listener) {
        this(x, y, false, spritePair, listener);
    }

    public ImageButton(int x, int y, boolean toggle, SpritePair spritePair, Listener listener) {
        super(x, y, spritePair.getWidth(), spritePair.getHeight(), toggle, listener);
        this.spritePair = spritePair;
    }

    @Override
    public void paint(Graphics g) {
        // Developer mode.
        super.paint(g);
        // Sprite.
        if (pressed) {
            spritePair.drawFirst(g, x, y);
        } else {
            spritePair.drawSecond(g, x, y);
        }
    }
}
