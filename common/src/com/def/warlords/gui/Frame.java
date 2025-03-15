package com.def.warlords.gui;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class Frame extends Component {

    public enum Type {
        PRESSED,
        RELEASED,
        GRAY,
        BLACK
    }

    private final Type type;

    public Frame(int x, int y, int width, int height, Type type) {
        super(x, y, width, height);
        this.type = type;
    }

    @Override
    public void paint(Graphics g) {
        switch (type) {
            case PRESSED:
                FramePainter.drawPressedFrame(g, x, y, width, height);
                break;
            case RELEASED:
                FramePainter.drawReleasedFrame(g, x, y, width, height);
            case GRAY:
                FramePainter.drawGrayFrame(g, x, y, width, height);
                break;
            case BLACK:
                FramePainter.drawBlackFrame(g, x, y, width, height);
                break;
        }
    }
}
