package com.watabou.utils;

import com.watabou.utils.function.Consumer;
import com.watabou.utils.function.Function;
import com.watabou.utils.function.Predicate;
import com.watabou.utils.function.Supplier;

public final class Misc {
	public static <T> T or( T a, T b ) {
		return a == null ? b : a;
	}

	public static <T> T or( T a, Supplier<T> b ) {
		if ( a == null ) return b.get();
		return a;
	}

	public static <T, U> U run( T object, Predicate<T> condition, Function<T, U> ifTrue, Function<T, U> ifFalse ) {
		if ( condition.test( object ) ) {
			return ifTrue.apply( object );
		}
		return ifFalse.apply( object );
	}
}
