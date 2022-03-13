package com.watabou.utils;

import java.util.Arrays;
import java.util.Iterator;

// https://stackoverflow.com/a/55797156
public class UnorderedPair<T> implements Iterable<T> {
	public final T first, second;

	public UnorderedPair( T first, T second ) {
		this.first = first;
		this.second = second;
	}

	@Override
	public boolean equals( Object o ) {
		if ( !(o instanceof UnorderedPair) )
			return false;
		UnorderedPair<T> up = (UnorderedPair<T>) o;
		return (up.first == this.first && up.second == this.second) ||
				(up.first == this.second && up.second == this.first);
	}

	@Override
	public int hashCode() {
		int hashFirst = first.hashCode();
		int hashSecond = second.hashCode();
		int maxHash = Math.max( hashFirst, hashSecond );
		int minHash = Math.min( hashFirst, hashSecond );
		return minHash * 31 + maxHash;
	}

	@Override
	public Iterator<T> iterator() {
		return Arrays.asList( first, second ).iterator();
	}
}
