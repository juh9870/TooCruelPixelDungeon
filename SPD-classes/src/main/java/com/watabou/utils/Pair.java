package com.watabou.utils;

import java.util.Arrays;
import java.util.Iterator;

public class Pair<T, U> {
	public final T first;
	public final U second;

	public Pair( T first, U second ) {
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean equals( Object o ) {
		if ( !(o instanceof Pair) )
			return false;
		Pair<T, U> up = (Pair<T, U>) o;
		return (up.first == this.first && up.second == this.second) ||
				(up.first == this.second && up.second == this.first);
	}

	@Override
	public int hashCode() {
		int hashFirst = first.hashCode();
		int hashSecond = second.hashCode();
		int maxHash = Math.max( hashFirst, hashSecond );
		return hashFirst * 31 + hashSecond;
	}
}
