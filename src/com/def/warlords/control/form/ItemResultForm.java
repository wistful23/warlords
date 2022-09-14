package com.def.warlords.control.form;

import com.def.warlords.control.MainController;
import com.def.warlords.gui.*;

import java.util.List;

/**
 * @author wistful23
 * @version 1.23
 */
public class ItemResultForm<I> extends ResultForm<I> {

    private final String message;
    private final List<I> items;
    private final ItemRepresentation<I> itemRepresentation;

    public ItemResultForm(MainController controller, String message, List<I> items,
                          ItemRepresentation<I> itemRepresentation) {
        super(controller);
        this.message = message;
        this.items = items;
        this.itemRepresentation = itemRepresentation;
    }

    @Override
    void init() {
        if (message != null) {
            add(new Label(0, 356, 640, Label.Alignment.CENTER, message));
        }
        add(new GrayPanel(42, 84, 292, 172));
        final ItemList<I> itemList =
                add(new ItemList<>(124, 106, 189, 78, 3, items, itemRepresentation,
                        add(new TextButton(66, 110, " Up ")), add(new TextButton(66, 150, "Down")), null));
        add(new TextButton(76, 202, " OK ",
                source -> setResult(itemList.getCurrentItem()))).setEnabled(!items.isEmpty());
        add(new TextButton(256, 202, "Cancel", source -> setResult(null)));
    }
}
