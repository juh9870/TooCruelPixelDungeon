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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Disabled;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.ListUtils;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.TabableView;

public class BetterDisplacing extends Weapon.Enchantment {

	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	private static final float STUN_DURATION = 2f;

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		if ( damage > defender.HP ) return damage;

		// Up to 6x chance with HP loss
		float hp = 1 + (1 - (1f * defender.HP / defender.HT)) * 5;

		float procChance = 1 / 12f * procChanceMultiplier( attacker ) * hp;
		if ( Random.Float() < procChance && !defender.properties().contains( Char.Property.IMMOVABLE ) ) {

			List<Char> chars = new ArrayList<>( Actor.chars() );
			Random.shuffle( chars );
			ListUtils.filter( chars, ( c ) -> c != attacker &&
					c != defender &&
					!c.properties().contains( Char.Property.IMMOVABLE ) );
			ListUtils.sortF( chars, ( c ) -> 1f * c.HP / c.HT );

			if ( chars.isEmpty() ) return damage;

			Char target = chars.get( 0 );
			ScrollOfTeleportation.swap( defender, target );
			Buff.prolong( target, Disabled.class, STUN_DURATION );
			Buff.affect( defender, MagicalSleep.class ).ignoreNextHit = true;
		}

		return damage;
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
