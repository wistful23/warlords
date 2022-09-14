package com.def.warlords.control.common;

import com.def.warlords.game.model.Locatable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author wistful23
 * @version 1.23
 */
public final class GameHelper {

    private static final int LIMIT = 5;

    public static <E extends Locatable> E getNearest(List<E> list, int x, int y, boolean limited) {
        final Stream<E> stream = limited ? list.stream().filter(e -> dist(e, x, y) <= LIMIT) : list.stream();
        return stream.min(Comparator.comparing(e -> dist(e, x, y))).orElse(null);
    }

    private static int dist(Locatable locatable, int x, int y) {
        return Math.max(Math.abs(locatable.getPosX() - x), Math.abs(locatable.getPosY() - y));
    }

    private GameHelper() {
    }
}
