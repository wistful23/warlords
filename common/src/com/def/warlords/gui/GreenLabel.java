package com.def.warlords.gui;

import com.def.warlords.graphics.Font;
import com.def.warlords.graphics.FontFactory;
import com.def.warlords.graphics.Palette;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class GreenLabel extends Label {

    public GreenLabel(int x, int y, int width, String text) {
        this(x, y, width, Alignment.LEFT, text);
    }

    public GreenLabel(int x, int y, int width, Alignment alignment, String text) {
        this(x, y, width, FontFactory.getInstance().getMonospacedFont(), alignment, text);
    }

    private GreenLabel(int x, int y, int width, Font font, Alignment alignment, String text) {
        super(x, y, width, font.getHeight() + 6, font, alignment, Palette.BLACK, text);
    }

    @Override
    public void paint(Graphics g) {
        FramePainter.drawPressedFrame(g, x, y, width, height);
        // Background.
        g.setColor(Palette.GREEN_DARK);
        g.fillRect(x + 1, y + 2, width - 2, height - 4);
        // Text.
        int ax = x;
        switch (alignment) {
            case LEFT:
                ax += 3;
                break;
            case CENTER:
                ax += (width - font.getLength(text) + 1) / 2;
                break;
            case RIGHT:
                ax += width - font.getLength(text) - 4;
                break;
        }
        g.setColor(Palette.YELLOW);
        font.drawString(g, ax, y + 4, text);
    }
}
