package com.watabou.utils.function;

import java.util.Objects;

@FunctionalInterface
public interface BiFunction<T, U, R> {
    R apply(T var1, U var2);

    default <V> java.util.function.BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (t, u) -> {
            return after.apply(this.apply(t, u));
        };
    }
}
