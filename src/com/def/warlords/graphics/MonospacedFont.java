package com.def.warlords.graphics;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
class MonospacedFont implements Font {

    // Font table dimension.
    private static final int COLUMN_COUNT = 32;

    // Font table cell size.
    private static final int CELL_WIDTH = 8;
    private static final int CELL_HEIGHT = 16;

    @Override
    public int getHeight() {
        return CELL_HEIGHT;
    }

    @Override
    public int getLength(String s) {
        int length = 0;
        for (int index = 0; index < s.length(); ++index) {
            final char ch = s.charAt(index);
            if (ch >= CHAR_FIRST && ch <= CHAR_LAST) {
                length += CELL_WIDTH;
            } else if (ch == CHAR_TAB) {
                length += CELL_WIDTH >> 1;
            }
        }
        return length;
    }

    @Override
    public void drawString(Graphics g, int x, int y, String s) {
        final Bitmap bitmap = BitmapFactory.getInstance().transformBitmap(BitmapInfo.FONTS, g.getColor());
        for (int index = 0; index < s.length(); ++index) {
            final char ch = s.charAt(index);
            if (ch >= CHAR_FIRST && ch <= CHAR_LAST) {
                final int pos = s.charAt(index) - CHAR_FIRST;
                final int tx = pos % COLUMN_COUNT * CELL_WIDTH;
                final int ty = pos / COLUMN_COUNT * CELL_HEIGHT;
                bitmap.drawSprite(g, x, y, CELL_WIDTH, CELL_HEIGHT, tx, ty);
                x += CELL_WIDTH;
            } else if (ch == CHAR_TAB) {
                x += CELL_WIDTH >> 1;
            }
        }
    }
}
