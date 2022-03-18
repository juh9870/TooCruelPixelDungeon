package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class SpikyRoots extends Buff implements Hero.Doom {

	private float damage = 1;
	protected float left;

	private static final String DAMAGE = "damage";
	private static final String LEFT = "left";

	{
		type = buffType.NEGATIVE;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( DAMAGE, damage );
		bundle.put( LEFT, left );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		damage = bundle.getFloat( DAMAGE );
		left = bundle.getFloat( LEFT );
	}

	public void set( float duration, int damage ) {
		this.left = Math.max( duration, left );
		if ( this.damage < damage ) this.damage = damage;
	}

	@Override
	public int icon() {
		return BuffIndicator.ROOTS;
	}

	@Override
	public void tintIcon( Image icon ) {
		icon.hardlight( 1f, 0.75f, 0f );
	}

	@Override
	public String toString() {
		return Messages.get( this, "name" );
	}

	@Override
	public String heroMessage() {
		return Messages.get( this, "heromsg" );
	}

	@Override
	public String desc() {
		return Messages.get( this, "desc", dispTurns( left ), (int) damage );
	}

	@Override
	public boolean attachTo( Char target ) {
		if ( !target.isImmune( Roots.class ) && !target.flying && super.attachTo( target ) ) {
			target.rooted++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void detach() {
		target.rooted--;
		super.detach();
	}

	@Override
	public boolean act() {
		if ( target.isAlive() ) {
			if ( damage < (Dungeon.scalingFactor() / 2) + 2 ) {
				damage++;
			} else {
				damage += 0.5f;
			}
			target.damage( (int) Math.round( damage ), this );

			spend( TICK );
			if ( (left -= TICK) <= 0 ) {
				detach();
			}
		} else {
			detach();
		}

		return true;
	}

	@Override
	public void onDeath() {
		Dungeon.fail( getClass() );
		GLog.n( Messages.get( this, "ondeath" ) );
	}
}