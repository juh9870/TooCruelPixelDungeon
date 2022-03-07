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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.PoisonDart;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class PoisonDartTrap extends TargetingTrap {

	{
		color = GREEN;
		shape = CROSSHAIR;
		
		canBeHidden = false;
		avoidsHallways = true;
	}
	
	protected int poisonAmount(){
		return 8 + Math.round(2*Dungeon.scalingFactor() / 3f);
	}


	protected void shootProjectile(Char target, Callback callback){
		((MissileSprite) ShatteredPixelDungeon.scene().recycle(MissileSprite.class)).
				reset(pos, target.sprite, new PoisonDart(), callback);
	}

	@Override
	protected void hit(Char target, boolean heroFov) {
		int dmg = Random.NormalIntRange(4, 8) - target.drRoll();
		target.damage(dmg, this);
		if (target == Dungeon.hero && !target.isAlive()){
			Dungeon.fail( getClass() );
		}
		Buff.affect( target, Poison.class ).set( poisonAmount() );
		if(heroFov){
			Sample.INSTANCE.play(Assets.Sounds.HIT, 1, 1, Random.Float(0.8f, 1.25f));
			target.sprite.bloodBurstA(target.sprite.center(), dmg);
			target.sprite.flash();
		}
	}
}
