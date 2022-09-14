package com.def.warlords.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * @author wistful23
 * @version 1.23
 */
public class Bitmap {

    public static void drawSprite(Graphics g, int x, int y, int width, int height,
                                  BitmapInfo bitmapInfo, int tx, int ty) {
        final Bitmap bitmap = BitmapFactory.getInstance().fetchBitmap(bitmapInfo);
        bitmap.drawSprite(g, x, y, width, height, tx, ty);
    }

    private final BufferedImage image;

    public Bitmap(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void drawSprite(Graphics g, int x, int y, int width, int height, int tx, int ty) {
        if ((ty & 1) > 0 || (height & 1) > 0) {
            throw new IllegalArgumentException("Invalid sprite parameters");
        }
        g.drawImage(image, x, y, x + width, y + height, tx, ty >> 1, tx + width, (ty + height) >> 1, null);
    }
}
