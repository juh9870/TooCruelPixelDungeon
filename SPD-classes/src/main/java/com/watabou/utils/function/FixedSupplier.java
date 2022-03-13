package com.watabou.utils.function;

public class FixedSupplier<T> implements Supplier<T> {
	private final T value;

	private FixedSupplier( T value ) {
		this.value = value;
	}

	public static <T> FixedSupplier<T> of( T value ) {
		return new FixedSupplier<>( value );
	}

	@Override
	public T get() {
		return value;
	}
}
