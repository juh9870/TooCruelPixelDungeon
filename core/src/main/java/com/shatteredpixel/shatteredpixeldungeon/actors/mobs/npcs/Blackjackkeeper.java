/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.BlackjackRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlackjackkeeperSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class Blackjackkeeper extends NPC {

	{
		spriteClass = BlackjackkeeperSprite.class;

		properties.add(Property.IMMOVABLE);
	}

	@Override
	protected boolean act() {
		if(Dungeon.level instanceof RegularLevel) {
			Room r = ((RegularLevel) Dungeon.level).room(pos());
			if (r instanceof BlackjackRoom && ((BlackjackRoom) r).sealed) {
				if(!r.inside(Dungeon.level.cellToPoint(Dungeon.hero.pos()))){
					GLog.w(Messages.get(this,"goodbye"));
					((BlackjackRoom) r).unseal();
					flee();
				}
			}
		}
		
		sprite.turnTo(pos(), Dungeon.hero.pos());
		spend( TICK );
		return true;
	}
	
	@Override
	public void damage( int dmg, Object src ) {
	}
	
	@Override
	public void add( Buff buff ) {
		if (buff.challengeBuff ) super.add(buff);
	}
	
	public void flee() {
		destroy();
		
		sprite.killAndErase();
		CellEmitter.get(pos()).burst( ElmoParticle.FACTORY, 6 );
	}
	
	@Override
	public boolean reset() {
		return true;
	}
	
	@Override
	public boolean interact(Char c) {
		if (c != Dungeon.hero) {
			return true;
		}
		if(Dungeon.level instanceof RegularLevel){
			Room r = ((RegularLevel) Dungeon.level).room(pos());
			if (r instanceof BlackjackRoom){
				if (!((BlackjackRoom) r).sealed){
					GLog.w(Messages.get(this,"welcome"));
					((BlackjackRoom) r).seal();
				} else {
					GLog.w(Messages.get(this,"goodbye"));
					((BlackjackRoom) r).unseal();
					flee();
				}
			}
			else flee();
		} else {
			flee();
		}
		return false;
	}
}
