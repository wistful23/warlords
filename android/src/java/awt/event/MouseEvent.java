package java.awt.event;

import java.awt.Point;

public class MouseEvent {

    private static final int NO_BUTTON = 0;

    private final int x, y;
    private final int clickCount;

    public MouseEvent(int x, int y, int clickCount) {
        this.x = x;
        this.y = y;
        this.clickCount = clickCount;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point getPoint() {
        return new Point(x, y);
    }

    public int getClickCount() {
        return clickCount;
    }

    public int getButton() {
        return NO_BUTTON;
    }

    public boolean isShiftDown() {
        return false;
    }

    public boolean isControlDown() {
        return false;
    }

    public boolean isAltDown() {
        return false;
    }
}
