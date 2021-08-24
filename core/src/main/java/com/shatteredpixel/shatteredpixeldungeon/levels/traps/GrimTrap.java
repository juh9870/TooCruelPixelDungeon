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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class GrimTrap extends TargetingTrap {

	{
		color = GREY;
		shape = LARGE_DOT;
		
		canBeHidden = false;
		avoidsHallways = true;
	}

	@Override
	protected void hit(Char target, boolean heroFov) {
		int damage;
		//almost kill the player
		if (target == Dungeon.hero && ((float)target.HP/target.HT) >= 0.9f){
			damage = target.HP-1;
			//kill 'em
		} else {
			damage = target.HP;
		}

		target.damage(damage, this);
		if (target == Dungeon.hero) {
			Sample.INSTANCE.play(Assets.Sounds.CURSED);
			if (!target.isAlive()) {
				Dungeon.fail( GrimTrap.class );
				GLog.n( Messages.get(GrimTrap.class, "ondeath") );
			}
		} else {
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
		}
		target.sprite.emitter().burst(ShadowParticle.UP, 10);
	}

	@Override
	protected void noTarget(boolean heroFov) {
		CellEmitter.get(pos).burst(ShadowParticle.UP, 10);
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
	}

	@Override
	protected void shootProjectile(Char target, Callback callback) {
		((MagicMissile) ShatteredPixelDungeon.scene().recycle(MagicMissile.class)).reset(
				MagicMissile.SHADOW,
				DungeonTilemap.tileCenterToWorld(pos),
				target.sprite.center(),
				callback);
	}
}
