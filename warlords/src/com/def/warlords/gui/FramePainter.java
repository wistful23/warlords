package com.def.warlords.gui;

import com.def.warlords.graphics.Palette;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
final class FramePainter {

    public static void drawPressedFrame(Graphics g, int x, int y, int width, int height) {
        // Top and left.
        g.setColor(Palette.BLACK);
        g.fillRect(x, y, width, 2);
        g.fillRect(x, y + 2, 1, height - 2);
        // Bottom and right.
        g.setColor(Palette.GRAY_LIGHT);
        g.fillRect(x + 1, y + height - 2, width - 1, 2);
        g.fillRect(x + width - 1, y + 2, 1, height - 4);
    }

    public static void drawReleasedFrame(Graphics g, int x, int y, int width, int height) {
        // Top and left.
        g.setColor(Palette.GRAY_LIGHT);
        g.fillRect(x, y, width - 1, 2);
        g.fillRect(x, y + 2, 1, height - 4);
        // Bottom and right.
        g.setColor(Palette.BLACK);
        g.fillRect(x, y + height - 2, width, 2);
        g.fillRect(x + width - 1, y, 1, height - 2);
    }

    public static void drawGrayFrame(Graphics g, int x, int y, int width, int height) {
        g.setColor(Palette.GRAY_LIGHT);
        g.fillRect(x, y, width, 2);
        g.fillRect(x, y + 2, 1, height - 2);
        g.fillRect(x + 1, y + height - 2, width - 1, 2);
        g.fillRect(x + width - 1, y + 2, 1, height - 4);
    }

    public static void drawBlackFrame(Graphics g, int x, int y, int width, int height) {
        // NOTE: W breaks the bottom right corner in some cases.
        g.setColor(Palette.BLACK);
        g.fillRect(x, y, width - 1, 2);
        g.fillRect(x, y + 2, 1, height - 4);
        g.fillRect(x, y + height - 2, width, 2);
        g.fillRect(x + width - 1, y, 1, height - 2);
    }

    private FramePainter() {
    }
}
