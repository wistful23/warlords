package com.def.warlords.graphics;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public interface Font {

    char CHAR_FIRST = ' ';
    char CHAR_LAST = '~';
    char CHAR_TAB = '\t';

    int getHeight();

    int getLength(String s);

    void drawString(Graphics g, int x, int y, String s);
}
