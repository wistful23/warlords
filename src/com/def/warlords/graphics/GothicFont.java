package com.def.warlords.graphics;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
class GothicFont implements Font {

    // Font table dimension.
    private static final int COLUMN_COUNT = 16;

    // Font table cell size.
    private static final int CELL_WIDTH = 16;
    private static final int CELL_HEIGHT = 18;

    // NOTE: W doesn't use {|}~ characters.
    private static final int[] charWidth = {
            /* */ 8, /*!*/ 5, /*"*/ 8, /*#*/11, /*$*/10, /*%*/11, /*&*/12, /*'*/ 5,
            /*(*/ 8, /*)*/ 8, /*\**/9, /*+*/ 9, /*,*/ 5, /*-*/ 7, /*.*/ 5, /*/*/11,
            /*0*/ 8, /*1*/ 4, /*2*/ 8, /*3*/ 7, /*4*/ 9, /*5*/ 8, /*6*/ 8, /*7*/ 8,
            /*8*/ 8, /*9*/ 8, /*:*/ 5, /*;*/ 5, /*<*/ 7, /*=*/ 7, /*>*/ 7, /*?*/ 6,
            /*@*/15, /*A*/16, /*B*/13, /*C*/ 9, /*D*/14, /*E*/10, /*F*/13, /*G*/11,
            /*H*/15, /*I*/ 7, /*J*/ 8, /*K*/16, /*L*/12, /*M*/15, /*N*/15, /*O*/11,
            /*P*/14, /*Q*/11, /*R*/15, /*S*/10, /*T*/13, /*U*/11, /*V*/14, /*W*/16,
            /*X*/12, /*Y*/12, /*Z*/11, /*[*/ 6, /*\*/10, /*]*/ 6, /*^*/ 6, /*_*/10,
            /*`*/ 5, /*a*/10, /*b*/ 8, /*c*/ 8, /*d*/ 9, /*e*/ 8, /*f*/ 7, /*g*/ 8,
            /*h*/ 9, /*i*/ 5, /*j*/ 5, /*k*/ 9, /*l*/ 5, /*m*/14, /*n*/10, /*o*/ 8,
            /*p*/ 9, /*q*/ 9, /*r*/ 9, /*s*/ 8, /*t*/ 7, /*u*/10, /*v*/10, /*w*/14,
            /*x*/ 9, /*y*/ 9, /*z*/10, /*{*/ 6, /*|*/ 2, /*}*/ 6, /*~*/ 8
    };

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
                length += charWidth[ch - CHAR_FIRST];
            }
        }
        return length;
    }

    @Override
    public void drawString(Graphics g, int x, int y, String s) {
        final Bitmap bitmap = BitmapFactory.getInstance().fetchBitmap(BitmapInfo.FANT);
        for (int index = 0; index < s.length(); ++index) {
            final char ch = s.charAt(index);
            if (ch >= CHAR_FIRST && ch <= CHAR_LAST) {
                final int pos = ch - CHAR_FIRST;
                final int tx = pos % COLUMN_COUNT * CELL_WIDTH;
                final int ty = pos / COLUMN_COUNT * CELL_HEIGHT;
                bitmap.drawSprite(g, x, y, charWidth[pos], CELL_HEIGHT, tx, ty);
                x += charWidth[pos];
            }
        }
    }
}
