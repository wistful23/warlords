package com.def.warlords.gui;

import com.def.warlords.graphics.Bitmap;
import com.def.warlords.graphics.BitmapFactory;
import com.def.warlords.graphics.BitmapInfo;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

/**
 * @author wistful23
 * @version 1.23
 */
public class SwitchButton extends Button {

    public SwitchButton(int x, int y, Listener listener) {
        super(x, y, 23, 16, false, listener);
    }

    @Override
    public void paint(Graphics g) {
        final Bitmap bitmap = BitmapFactory.getInstance().fetchBitmap(BitmapInfo.ARMIES);
        if (!isEnabled()) {
            bitmap.drawSprite(g, x, y, width, height, 365, 352);
            return;
        }
        if (selected) {
            if (pressed) {
                bitmap.drawSprite(g, x, y, width, height, 365, 322);
            } else {
                bitmap.drawSprite(g, x, y, width, height, 333, 322);
            }
        } else {
            if (pressed) {
                bitmap.drawSprite(g, x, y, width, height, 365, 352);
            } else {
                bitmap.drawSprite(g, x, y, width, height, 333, 352);
            }
        }
    }

    @Override
    public boolean mousePressed(MouseEvent e) {
        pressed = contains(e.getPoint());
        return true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (pressed) {
            pressed = false;
            selected = !selected;
            if (listener != null) {
                listener.buttonClicked(this);
            }
        }
    }
}
