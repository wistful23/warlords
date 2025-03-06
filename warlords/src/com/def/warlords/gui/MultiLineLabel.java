package com.def.warlords.gui;

import com.def.warlords.graphics.Font;
import com.def.warlords.graphics.FontFactory;

import java.awt.Graphics;
import java.util.stream.Stream;

/**
 * @author wistful23
 * @version 1.23
 */
public class MultiLineLabel extends Component {

    private static final Font font = FontFactory.getInstance().getGothicFont();

    private final int lineSpacing;
    private final String[] lines;

    public MultiLineLabel(int x, int y, int lineSpacing, String[] lines) {
        super(x, y, Stream.of(lines).mapToInt(font::getLength).max().orElse(0),
                (font.getHeight() + lineSpacing) * lines.length - lineSpacing);
        this.lineSpacing = lineSpacing;
        this.lines = lines;
    }

    @Override
    public void paint(Graphics g) {
        // Developer mode.
        super.paint(g);
        // Text.
        int ay = y;
        for (final String line : lines) {
            font.drawString(g, x, ay, line);
            ay += font.getHeight() + lineSpacing;
        }
    }
}
