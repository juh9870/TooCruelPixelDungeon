package com.watabou.utils.function;

@FunctionalInterface
public interface Supplier<T> {
    T get();
}
