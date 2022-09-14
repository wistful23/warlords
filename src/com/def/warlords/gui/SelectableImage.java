package com.def.warlords.gui;

import com.def.warlords.graphics.Sprite;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class SelectableImage extends Component {

    public interface Listener {
        void imageClicked(SelectableImage source);
    }

    private final Sprite sprite;
    private final Listener listener;

    public SelectableImage(int x, int y, Sprite sprite, Listener listener) {
        super(x, y, sprite.getWidth() + 2, sprite.getHeight() + 4);
        this.sprite = sprite;
        this.listener = listener;
    }

    @Override
    public void paint(Graphics g) {
        // Developer mode.
        super.paint(g);
        // Sprite.
        sprite.draw(g, x + 1, y + 2);
        if (selected) {
            FramePainter.drawReleasedFrame(g, x, y, width, height);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        selected = true;
        if (listener != null) {
            listener.imageClicked(this);
        }
    }
}
