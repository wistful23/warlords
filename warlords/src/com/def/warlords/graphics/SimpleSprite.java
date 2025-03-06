package com.def.warlords.graphics;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class SimpleSprite extends Sprite {

    private final BitmapInfo bitmapInfo;
    private final int tx, ty;

    public SimpleSprite(int width, int height, BitmapInfo bitmapInfo, int tx, int ty) {
        super(width, height);
        if ((ty & 1) > 0) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
        this.bitmapInfo = bitmapInfo;
        this.tx = tx;
        this.ty = ty;
    }

    @Override
    public void draw(Graphics g, int x, int y) {
        Bitmap.drawSprite(g, x, y, width, height, bitmapInfo, tx, ty);
    }
}
