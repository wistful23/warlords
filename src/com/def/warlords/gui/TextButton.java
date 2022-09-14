package com.def.warlords.gui;

import com.def.warlords.graphics.Font;
import com.def.warlords.graphics.FontFactory;
import com.def.warlords.graphics.Palette;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class TextButton extends Button {

    private static final Font font = FontFactory.getInstance().getGothicFont();

    private final String text;

    public TextButton(int x, int y, String text) {
        this(x, y, text, false);
    }

    public TextButton(int x, int y, String text, boolean toggle) {
        this(x, y, text, toggle, null);
    }

    public TextButton(int x, int y, String text, Listener listener) {
        this(x, y, text, false, listener);
    }

    public TextButton(int x, int y, String text, boolean toggle, Listener listener) {
        super(x, y, font.getLength(text) + 4, font.getHeight() + 8, toggle, listener);
        this.text = text;
    }

    @Override
    public void paint(Graphics g) {
        // Frame.
        if (isEnabled()) {
            if (pressed) {
                FramePainter.drawPressedFrame(g, x, y, width, height);
            } else {
                FramePainter.drawReleasedFrame(g, x, y, width, height);
            }
        } else {
            FramePainter.drawGrayFrame(g, x, y, width, height);
        }
        // Background.
        g.setColor(Palette.GRAY);
        g.fillRect(x + 1, y + 2, width - 2, height - 4);
        // Text.
        g.setColor(Palette.BLACK);
        font.drawString(g, x + 2, y + 4, text);
    }
}
