package com.watabou.utils;

import com.watabou.utils.function.Supplier;

public final class Misc {
    public static <T> T or(T a, T b) {
        return a == null ? b : a;
    }

    public static <T> T or(T a, Supplier<T> b) {
        if (a == null) return b.get();
        return a;
    }
}
