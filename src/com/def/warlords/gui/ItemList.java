package com.def.warlords.gui;

import com.def.warlords.graphics.Font;
import com.def.warlords.graphics.FontFactory;
import com.def.warlords.graphics.Palette;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class ItemList<I> extends Component {

    public interface Listener<I> {
        void itemSelected(I item);
    }

    private final int slotCount;
    private final List<? extends I> list;
    private final ItemRepresentation<? super I> itemRepresentation;
    private final Button upButton, downButton;
    private final Listener<I> listener;

    private int currentIndex = -1;

    public ItemList(int x, int y, int width, int height, int slotCount,
                    List<? extends I> list, ItemRepresentation<? super I> itemRepresentation,
                    Button upButton, Button downButton, Listener<I> listener) {
        super(x, y, width, height);
        this.slotCount = slotCount;
        this.list = new ArrayList<>(list);
        this.itemRepresentation = itemRepresentation;
        this.upButton = upButton;
        this.downButton = downButton;
        this.listener = listener;
        upButton.setListener(source -> updateCurrentIndex(currentIndex - 1));
        downButton.setListener(source -> updateCurrentIndex(currentIndex + 1));
        updateCurrentIndex(0);
    }

    public I getCurrentItem() {
        if (currentIndex != -1) {
            return list.get(currentIndex);
        }
        return null;
    }

    @Override
    public void paint(Graphics g) {
        FramePainter.drawBlackFrame(g, x, y, width, height);
        final Font font = FontFactory.getInstance().getGothicFont();
        // NOTE: W doesn't have a fixed slot height.
        final int slotHeight = height / slotCount;
        for (int slotIndex = 0; slotIndex < slotCount; ++slotIndex) {
            if (slotIndex > 0) {
                g.setColor(Palette.RED);
                g.fillRect(x + 1, y + slotHeight * slotIndex, width - 2, 2);
            }
            if (currentIndex != -1) {
                final int itemIndex = currentIndex - slotCount / 2 + slotIndex;
                if (itemIndex >= 0 && itemIndex < list.size()) {
                    g.setColor(Palette.BLACK);
                    font.drawString(g, x + 10, y + slotHeight * slotIndex + 4,
                            itemRepresentation.toString(list.get(itemIndex)));
                }
            }
        }
    }

    private void updateCurrentIndex(int index) {
        if (index >= 0 && index < list.size()) {
            currentIndex = index;
            if (listener != null) {
                listener.itemSelected(list.get(currentIndex));
            }
        }
        // NOTE: W disables the up/down buttons only if the item list contains at most one item.
        upButton.setEnabled(currentIndex > 0);
        downButton.setEnabled(currentIndex < list.size() - 1);
    }
}
