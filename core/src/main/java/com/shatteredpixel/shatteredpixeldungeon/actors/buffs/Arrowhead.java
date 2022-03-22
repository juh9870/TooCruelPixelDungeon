package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class Arrowhead extends Buff implements DamageAmplificationBuff {
	private static final String STACKS = "arrowhead_stacks";
	private final float COOLDOWN = Challenges.THUNDERSTRUCK.enabled() ? 420 : 20;
	private int stacks;

	{
		type = buffType.POSITIVE;
	}

	@Override
	public boolean act() {
		stacks--;
		if ( stacks <= 0 ) {
			detach();
			return true;
		}
		spend( COOLDOWN );
		return true;
	}

	@Override
	public int icon() {
		return BuffIndicator.FURY;
	}

	@Override
	public String toString() {
		return Messages.get( this, "name" );
	}

	@Override
	public String desc() {
		return Messages.get( this, "desc", stacks, Math.round( stacks * 100 * 0.3f ), Math.round( stacks * 100 * 0.1f ) );
	}

	@Override
	public float damageMultiplier( Object source ) {
		if ( source instanceof Hunger ) return 1f;
		return 1 + stacks * 0.3f;
	}

	public Arrowhead addStack() {
		stacks++;
		postpone( COOLDOWN );
		return this;
	}

	public Arrowhead set( int stacks ) {
		this.stacks = stacks;
		postpone( COOLDOWN );
		return this;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( STACKS, stacks );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		stacks = bundle.getInt( STACKS );
	}

	public static class MobArrowhead extends Buff implements DamageAmplificationBuff {
		{
			challengeBuff = true;
		}

		@Override
		public float damageMultiplier( Object source ) {
			Arrowhead arrowhead = Dungeon.hero.buff( Arrowhead.class );
			if ( arrowhead == null ) return 1;
			return (1 + arrowhead.stacks * 0.1f);
		}
	}
}
