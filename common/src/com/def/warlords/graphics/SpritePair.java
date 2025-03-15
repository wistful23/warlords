package com.def.warlords.graphics;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class SpritePair extends Sprite {

    private final BitmapInfo bitmapInfo;
    private final int tx1, ty1, tx2, ty2;

    public SpritePair(int width, int height, BitmapInfo bitmapInfo, int tx1, int ty1, int tx2, int ty2) {
        super(width, height);
        if ((ty1 & 1) > 0 || (ty2 & 1) > 0) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
        this.bitmapInfo = bitmapInfo;
        this.tx1 = tx1;
        this.ty1 = ty1;
        this.tx2 = tx2;
        this.ty2 = ty2;
    }

    public void drawFirst(Graphics g, int x, int y) {
        Bitmap.drawSprite(g, x, y, width, height, bitmapInfo, tx1, ty1);
    }

    public void drawSecond(Graphics g, int x, int y) {
        Bitmap.drawSprite(g, x, y, width, height, bitmapInfo, tx2, ty2);
    }
}
