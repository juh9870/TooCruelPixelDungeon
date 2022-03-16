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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Disabled;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class Sawing extends Weapon.Enchantment {

	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	private static final float DAMAGE_SCALE = 0.75f;
	private static final float TURNS_LIMIT = 50;


	boolean processing = false;

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		if ( !processing ) {
			processing = true;

			float duration = 0;
			boolean hit = false;
			do {
				hit = attacker.attack( defender );
				duration += weapon.delayFactor( attacker );
			} while (hit && defender.isAlive() && duration < TURNS_LIMIT);

			if ( duration > 0 ) {
				Buff.prolong( attacker, Disabled.class, duration );
			}

			processing = false;
		}
		return (int) (damage * DAMAGE_SCALE);
	}

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}
}
