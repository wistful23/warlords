package com.def.warlords.graphics;

import java.awt.Graphics;
import java.awt.Point;

/**
 * @author wistful23
 * @version 1.23
 */
// NOTE: We don't follow the W's inconsistent logic for displaying the mouse cursor.
public enum Cursor {

    // @formatter:off
    // NOTE: W doesn't display the cursor when playing music.
    EMPTY,
    DEFAULT     ( 9, 22,   0,  0),
    MODAL       ( 9, 22,   0, 24),
    INFO        ( 9, 24,  32,  0),
    MAGNIFIER   (25, 34,  80,  0,  3,  4),
    TARGET      (25, 26, 112,  0, 12, 12),
    TOWER       (14, 46,  16,  0,  6, 20),
    SWORD       (27, 32,  48,  0, 12, 16),
    UP          (18, 34,   0, 50,  8, 18),
    UP_LEFT     (28, 28,  72, 90, 14, 14),
    UP_RIGHT    (28, 28,  24, 50, 12, 14),
    DOWN        (18, 34, 128, 50,  8, 14),
    DOWN_LEFT   (28, 28,   0, 90, 14, 12),
    DOWN_RIGHT  (28, 28,  96, 50, 12, 12),
    LEFT        (33, 18,  32, 90, 17,  8),
    RIGHT       (33, 18,  56, 50, 14,  8);
    // @formatter:on

    private final Sprite sprite;
    private final int hx, hy;

    Cursor() {
        sprite = null;
        hx = hy = 0;
    }

    Cursor(int width, int height, int tx, int ty) {
        this(width, height, tx, ty, 0, 0);
    }

    Cursor(int width, int height, int tx, int ty, int hx, int hy) {
        this.sprite = new SimpleSprite(width, height, BitmapInfo.MOUSE, tx, ty);
        this.hx = hx;
        this.hy = hy;
    }

    public void draw(Graphics g, Point p) {
        if (sprite != null) {
            sprite.draw(g, p.x - hx, p.y - hy);
        }
    }
}
