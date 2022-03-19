package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.function.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlchemyPredicate extends Item {
	private final Item display;
	private final Predicate<Item> predicate;

	public AlchemyPredicate( Item display, Predicate<Item> predicate ) {
		this.display = display;
		this.predicate = predicate;
	}

	@Override
	public int image() {
		return display.image();
	}

	@Override
	public String name() {
		return display.name();
	}

	@Override
	public String desc() {
		return display.desc();
	}

	@Override
	public String info() {
		return display.info();
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return display.glowing();
	}

	public Item display() {
		return display;
	}

	public boolean test( Item item ) {
		return this.predicate.test( item );
	}

	public static ArrayList<Item> extract( ArrayList<Item> list ) {
		ArrayList<Item> result = new ArrayList<>();
		for (Item item : list) {
			if ( item instanceof AlchemyPredicate ) {
				result.add( ((AlchemyPredicate) item).display() );
			} else {
				result.add( item );
			}
		}
		return result;
	}
}
