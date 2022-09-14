package com.def.warlords.graphics;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class OffsetSprite extends Sprite {

    private final Sprite sprite;
    private final int dx, dy;

    public OffsetSprite(Sprite sprite, int dx, int dy) {
        super(sprite.getWidth() + dx, sprite.getHeight() + dy);
        this.sprite = sprite;
        this.dx = dx;
        this.dy = dy;
    }

    public OffsetSprite(int width, int height, BitmapInfo bitmapInfo, int tx, int ty, int dx, int dy) {
        super(width + dx, height + dy);
        if ((dy & 1) > 0) {
            throw new IllegalArgumentException("Invalid offset");
        }
        this.sprite = new SimpleSprite(width, height, bitmapInfo, tx, ty);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        sprite.draw(g, x + dx, y + dy);
    }
}
