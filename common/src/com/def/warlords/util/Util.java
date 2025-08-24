package com.def.warlords.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.function.Predicate;

/**
 * @author wistful23
 * @version 1.23
 */
public final class Util {

    private static final Random random = new Random();

    // Generates int form [0..right) interval.
    public static int randomInt(int right) {
        return random.nextInt(right);
    }

    // Generates int from [left..right) interval.
    public static int randomInt(int left, int right) {
        return random.nextInt(right - left) + left;
    }

    public static boolean isTrue(Boolean value) {
        return value != null && value;
    }

    public static void fail() {
        assert false;
    }

    public static void assertTrue(boolean value) {
        assert value;
    }

    public static void assertFalse(boolean value) {
        assert !value;
    }

    public static void assertNull(Object value) {
        assert value == null;
    }

    public static void assertNotNull(Object value) {
        assert value != null;
    }

    public static int truncate(int value, int limit) {
        return Math.min(value, limit);
    }

    public static String truncate(String s, int limit) {
        return s.length() > limit ? s.substring(0, limit) : s;
    }

    public static String trimStringSuffix(String s) {
        int index = s.length() - 1;
        while (index >= 0 && s.charAt(index) == ' ') {
            --index;
        }
        return s.substring(0, index + 1);
    }

    public static <E> E nextElement(List<E> list, E element) {
        if (list.isEmpty()) {
            return null;
        }
        final int index = list.indexOf(element);
        if (index == -1 || index == list.size() - 1) {
            return list.get(0);
        }
        return list.get(index + 1);
    }

    public static <E> E prevElement(List<E> list, E element) {
        if (list.isEmpty()) {
            return null;
        }
        final int index = list.indexOf(element);
        if (index == -1) {
            return list.get(0);
        }
        if (index == 0) {
            return list.get(list.size() - 1);
        }
        return list.get(index - 1);
    }

    public static <E> Predicate<E> not(Predicate<E> predicate) {
        return predicate.negate();
    }

    public static <E> Iterable<E> reverse(List<? extends E> list) {
        return new ReversedList<>(list);
    }

    private static class ReversedList<E> implements Iterable<E> {

        private final List<? extends E> list;

        ReversedList(List<? extends E> list) {
            this.list = list;
        }

        @Override
        public Iterator<E> iterator() {
            final ListIterator<? extends E> iterator = list.listIterator(list.size());
            return new Iterator<E>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasPrevious();
                }

                @Override
                public E next() {
                    return iterator.previous();
                }

                @Override
                public void remove() {
                    iterator.remove();
                }
            };
        }
    }

    private Util() {
    }
}
