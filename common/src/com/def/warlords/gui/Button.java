package com.def.warlords.gui;

import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class Button extends Component {

    public interface Listener {
        void buttonClicked(Button source);
    }

    private final boolean toggle;
    Listener listener;

    boolean pressed;

    public Button(int x, int y, int width, int height, boolean toggle, Listener listener) {
        super(x, y, width, height);
        this.toggle = toggle;
        this.listener = listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void click() {
        if (toggle) {
            pressed = true;
            selected = true;
        }
        if (listener != null) {
            listener.buttonClicked(this);
        }
    }

    public void release() {
        pressed = false;
        selected = false;
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        if (!selected) {
            pressed = contains(e.getPoint());
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!selected && pressed) {
            if (!toggle) {
                pressed = false;
            } else {
                selected = true;
            }
            if (listener != null) {
                listener.buttonClicked(this);
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        pressed = contains(e.getPoint());
    }
}
