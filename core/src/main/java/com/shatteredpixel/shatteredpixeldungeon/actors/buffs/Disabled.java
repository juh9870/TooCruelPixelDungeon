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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class Disabled extends FlavourBuff {

	{
		type = buffType.NEGATIVE;
		announced = true;
	}

	private float initialDuration = -1;

	@Override
	public boolean attachTo( Char target ) {
		if ( super.attachTo( target ) ) {
			target.paralysed++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void detach() {
		super.detach();
		if ( target.paralysed > 0 )
			target.paralysed--;
	}

	@Override
	public int icon() {
		return BuffIndicator.PARALYSIS;
	}

	@Override
	protected void postpone( float time ) {
		super.postpone( time );
		initialDuration = time;
	}

	@Override
	public float iconFadePercent() {
		if ( initialDuration < 0 ) return super.iconFadePercent();
		return Math.max( 0, (initialDuration - visualcooldown()) / initialDuration );
	}

	@Override
	public void fx( boolean on ) {
		if ( on ) target.sprite.add( CharSprite.State.PARALYSED );
		else if ( target.paralysed <= 1 ) target.sprite.remove( CharSprite.State.PARALYSED );
	}

	@Override
	public String heroMessage() {
		return Messages.get( Paralysis.class, "heromsg" );
	}

	@Override
	public String toString() {
		return Messages.get( Paralysis.class, "name" );
	}

	@Override
	public String desc() {
		return Messages.get( Paralysis.class, "desc", dispTurns() );
	}
}
