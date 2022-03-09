package com.watabou.utils;

import com.watabou.utils.function.Predicate;

import java.util.Arrays;
import java.util.Locale;

public final class ListUtils {

    public static <T> void shift(T[] elements, Predicate<T> matcher, int amount) {
        int size = elements.length;
        T[] tempArray = Arrays.copyOf(elements, size);
        for (int i = 0; i < tempArray.length; i++) {
            elements[i] = null;
            T element = tempArray[i];
            if (!matcher.test(element)) continue;
            checkIndex(size, i + amount, String.format(Locale.ROOT, "Shifting element from %d to %d caused it go out of [0,%d] bounds!", i, i + amount, size));
            elements[i + amount] = element;
            tempArray[i] = null;
        }

        int offset = 0;
        for (int i = 0; i < tempArray.length; i++) {
            if (elements[i + offset] != null) offset++;
            if (tempArray[i] == null) offset--;
            else elements[i + offset] = tempArray[i];
        }
    }

    private static void checkIndex(int size, int index, String message) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(message);
        }
    }
}
