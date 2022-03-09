package com.watabou.utils.function;

import java.util.Objects;

@FunctionalInterface
public interface Predicate<T> {
    static <T> Predicate<T> isEqual(Object targetRef) {
        return null == targetRef ? (o) -> o == null : targetRef::equals;
    }

    boolean test(T var1);

    default Predicate<T> and(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> {
            return this.test(t) && other.test(t);
        };
    }

    default Predicate<T> negate() {
        return (t) -> !this.test(t);
    }

    default Predicate<T> or(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> this.test(t) || other.test(t);
    }
}