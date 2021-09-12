/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.NoReward;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HolderStatue extends Statue {

	{
		spriteClass = StatueSprite.class;
	}

	public ArrayList<Item> items;

	public HolderStatue(){
		super();

		//reduced HP
		HP = HT = 10 + Dungeon.depth * 3;
	}

	public HolderStatue(MeleeWeapon wep, List<Item> items){
		this();
		weapon = wep;
		this.items = new ArrayList<>(items);
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		if(!isAlive()){
			if (items != null) {
				for (Item item : items) {
					Dungeon.level.drop( item, pos()).sprite.drop();
				}
				items = null;
			}
		}
	}

	private static final String ITEMS	= "items";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		if (items != null) bundle.put( ITEMS, items );
	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		if (bundle.contains( ITEMS )) {
			items = new ArrayList<>((Collection<Item>) ((Collection<?>) bundle.getCollection(ITEMS)));
		}
		super.restoreFromBundle(bundle);
	}
}
