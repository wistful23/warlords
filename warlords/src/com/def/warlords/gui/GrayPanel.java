package com.def.warlords.gui;

import com.def.warlords.graphics.Bitmap;
import com.def.warlords.graphics.BitmapFactory;
import com.def.warlords.graphics.BitmapInfo;
import com.def.warlords.graphics.Palette;

import java.awt.Graphics;

/**
 * @author wistful23
 * @version 1.23
 */
public class GrayPanel extends Component {

    private static final int PANEL_WIDTH = 360;
    private static final int PANEL_HEIGHT = 320;

    private static final int FRAME_DEPTH = 14;
    private static final int FRAME_RESERVED_WIDTH = 300;

    public GrayPanel(int x, int y, int width, int height) {
        super(x, y, width, height);
        if (width < 2 * FRAME_DEPTH || height < 2 * FRAME_DEPTH) {
            throw new IllegalArgumentException("Invalid panel size");
        }
    }

    @Override
    public void paint(Graphics g) {
        final Bitmap bitmap = BitmapFactory.getInstance().fetchBitmap(BitmapInfo.SETUP);
        if (width > PANEL_WIDTH) {
            // Top.
            bitmap.drawSprite(g, x, y, FRAME_RESERVED_WIDTH, FRAME_DEPTH, 0, 0);
            bitmap.drawSprite(g, x + FRAME_RESERVED_WIDTH, y, width - FRAME_DEPTH - FRAME_RESERVED_WIDTH, FRAME_DEPTH,
                    FRAME_DEPTH, 0);
            // Bottom.
            bitmap.drawSprite(g, x, y + height - FRAME_DEPTH, FRAME_RESERVED_WIDTH, FRAME_DEPTH,
                    0, PANEL_HEIGHT - FRAME_DEPTH);
            bitmap.drawSprite(g, x + FRAME_RESERVED_WIDTH, y + height - FRAME_DEPTH,
                    width - FRAME_DEPTH - FRAME_RESERVED_WIDTH, FRAME_DEPTH, FRAME_DEPTH, 0);
        } else {
            // Top.
            bitmap.drawSprite(g, x, y, width - FRAME_DEPTH, FRAME_DEPTH, 0, 0);
            // Bottom.
            bitmap.drawSprite(g, x, y + height - FRAME_DEPTH, width - FRAME_DEPTH, FRAME_DEPTH,
                    0, PANEL_HEIGHT - FRAME_DEPTH);
        }
        // Left.
        bitmap.drawSprite(g, x, y + FRAME_DEPTH, FRAME_DEPTH, height - 2 * FRAME_DEPTH, 0, FRAME_DEPTH);
        // Right.
        bitmap.drawSprite(g, x + width - FRAME_DEPTH, y, FRAME_DEPTH, height - FRAME_DEPTH,
                PANEL_WIDTH - FRAME_DEPTH, 0);
        // Corner.
        bitmap.drawSprite(g, x + width - FRAME_DEPTH, y + height - FRAME_DEPTH, FRAME_DEPTH, FRAME_DEPTH,
                PANEL_WIDTH - FRAME_DEPTH, PANEL_HEIGHT - FRAME_DEPTH);
        // Background.
        g.setColor(Palette.GRAY);
        g.fillRect(x + FRAME_DEPTH, y + FRAME_DEPTH, width - 2 * FRAME_DEPTH, height - 2 * FRAME_DEPTH);
    }
}
