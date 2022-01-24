package com.watabou.utils;

@FunctionalInterface
public interface Supplier<T> {
    T get();
}
