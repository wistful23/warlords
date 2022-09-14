package com.def.warlords.gui;

import com.def.warlords.graphics.Sprite;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class Image extends Component {

    private Sprite sprite;

    public Image(int x, int y, Sprite sprite) {
        super(x, y, sprite.getWidth(), sprite.getHeight());
        this.sprite = sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void paint(Graphics g) {
        // Developer mode.
        super.paint(g);
        // Sprite.
        sprite.draw(g, x, y);
    }
}
