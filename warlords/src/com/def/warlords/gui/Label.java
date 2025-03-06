package com.def.warlords.gui;

import com.def.warlords.graphics.Font;
import com.def.warlords.graphics.FontFactory;
import com.def.warlords.graphics.Palette;

import java.awt.Color;
import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class Label extends Component {

    public enum Alignment {
        LEFT,
        CENTER,
        RIGHT
    }

    final Font font;
    final Alignment alignment;
    final Color color;
    String text;

    public Label(int x, int y, String text) {
        this(x, y, FontFactory.getInstance().getGothicFont(), text);
    }

    public Label(int x, int y, int width, Alignment alignment) {
        this(x, y, width, FontFactory.getInstance().getGothicFont(), alignment, "");
    }

    public Label(int x, int y, int width, Alignment alignment, String text) {
        this(x, y, width, FontFactory.getInstance().getGothicFont(), alignment, text);
    }

    public Label(int x, int y, Font font, String text) {
        this(x, y, font.getLength(text), font, Alignment.LEFT, text);
    }

    public Label(int x, int y, Font font, Color color, String text) {
        this(x, y, font.getLength(text), font, Alignment.LEFT, color, text);
    }

    public Label(int x, int y, int width, Font font, Alignment alignment, String text) {
        this(x, y, width, font, alignment, Palette.BLACK, text);
    }

    public Label(int x, int y, int width, Font font, Alignment alignment, Color color, String text) {
        this(x, y, width, font.getHeight(), font, alignment, color, text);
    }

    Label(int x, int y, int width, int height, Font font, Alignment alignment, Color color, String text) {
        super(x, y, width, height);
        this.font = font;
        this.alignment = alignment;
        this.color = color;
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void paint(Graphics g) {
        // Developer mode.
        super.paint(g);
        // Text.
        int ax = x;
        switch (alignment) {
            case CENTER:
                ax += (width - font.getLength(text) + 1) / 2;
                break;
            case RIGHT:
                ax += width - font.getLength(text);
                break;
        }
        g.setColor(color);
        font.drawString(g, ax, y, text);
    }
}
