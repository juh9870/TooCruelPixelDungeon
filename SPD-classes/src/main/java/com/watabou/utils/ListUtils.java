package com.watabou.utils;

import com.watabou.utils.function.Function;
import com.watabou.utils.function.Predicate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ListUtils {

	public static <T> void shift( T[] elements, Predicate<T> matcher, int amount ) {
		int size = elements.length;
		T[] tempArray = Arrays.copyOf( elements, size );
		for (int i = 0; i < tempArray.length; i++) {
			elements[i] = null;
			T element = tempArray[i];
			if ( !matcher.test( element ) ) continue;
			checkIndex( size, i + amount, String.format( Locale.ROOT, "Shifting element from %d to %d caused it go out of [0,%d] bounds!", i, i + amount, size ) );
			elements[i + amount] = element;
			tempArray[i] = null;
		}

		int offset = 0;
		for (int i = 0; i < tempArray.length; i++) {
			if ( elements[i + offset] != null ) offset++;
			if ( tempArray[i] == null ) offset--;
			else elements[i + offset] = tempArray[i];
		}
	}

	public static <T, U> U[] map( T[] array, Class<U> targetClass, Function<T, U> function ) {
		U[] names = (U[]) Array.newInstance( targetClass, array.length );
		for (int i = 0; i < array.length; i++) {
			names[i] = function.apply( array[i] );
		}
		return names;
	}

	public static <T, U> List<U> map( List<T> source, Function<T, U> function ) {
		List<U> target = new ArrayList<>();
		for (T t : source) {
			target.add( function.apply( t ) );
		}
		return target;
	}

	public static <T> String join( Iterable<T> iterable, String separator, Function<T, String> toString ) {
		StringBuilder sb = new StringBuilder();
		for (T t : iterable) {
			if ( sb.length() > 0 ) sb.append( separator );
			sb.append( toString.apply( t ) );
		}
		return sb.toString();
	}

	public static <T> boolean containsAll( Collection<T> collection, Iterable<T> content ) {
		for (T t : content) {
			if ( !collection.contains( t ) ) return false;
		}
		return true;
	}

	public static <T> boolean containsAll( Iterable<T> collection, Iterable<T> content ) {
		return containsAll( toList( content ), content );
	}

	public static <T> boolean containsAny( Collection<T> collection, Iterable<T> content ) {
		for (T t : content) {
			if ( collection.contains( t ) ) return true;
		}
		return false;
	}

	public static <T> boolean containsAny( Iterable<T> collection, Iterable<T> content ) {
		return containsAny( toList( content ), content );
	}

	public static <T> List<T> toList( Iterable<T> iterable ) {
		List<T> list = new ArrayList<>();
		for (T t : iterable) {
			list.add( t );
		}
		return list;
	}

	public static <T> Set<UnorderedPair<T>> pairs( Collection<T> items ) {
		return pairs( new ArrayList<>( items ) );
	}

	public static <T> Set<UnorderedPair<T>> pairs( List<T> items ) {
		Set<UnorderedPair<T>> set = new HashSet<>();
		for (int i = 0; i < items.size(); i++) {
			for (int j = i; j < items.size(); j++) {
				set.add( new UnorderedPair<>( items.get( i ), items.get( j ) ) );
			}
		}
		return set;
	}

	public static <T> T[] combine( T[]... arrays ) {
		int length = 0;
		for (T[] array : arrays) {
			length += array.length;
		}
		T[] combination = Arrays.copyOf( arrays[0], length );
		int cur = arrays[0].length;
		for (int i = 1; i < arrays.length; i++) {
			System.arraycopy( arrays[i], 0, combination, cur, arrays[i].length );
			cur += arrays[i].length;
		}

		return combination;
	}

	public static <T> void sort( List<T> list, Function<T, Integer> keyExtractor ) {
		Collections.sort( list, ( a, b ) -> {
			return keyExtractor.apply( a ) - keyExtractor.apply( b );
		} );
	}

	public static <T> void sortF( List<T> list, Function<T, Float> keyExtractor ) {
		Collections.sort( list, ( a, b ) -> {
			return (int) Math.signum( keyExtractor.apply( a ) - keyExtractor.apply( b ) );
		} );
	}

	public static <T> void filter( List<T> list, Predicate<T> filter ) {
		for (Iterator<T> iterator = list.iterator(); iterator.hasNext(); ) {
			T t = iterator.next();
			if ( !filter.test( t ) )
				iterator.remove();
		}
	}

	public static <T> boolean any( List<T> list, Predicate<T> filter ) {
		for (T t : list) {
			if ( filter.test( t ) ) return true;
		}
		return false;
	}

	public static <T> boolean all( List<T> list, Predicate<T> filter ) {
		if ( list.isEmpty() ) return false;
		for (T t : list) {
			if ( !filter.test( t ) ) return false;
		}
		return true;
	}

	private static void checkIndex( int size, int index, String message ) {
		if ( index < 0 || index >= size ) {
			throw new IndexOutOfBoundsException( message );
		}
	}
}
