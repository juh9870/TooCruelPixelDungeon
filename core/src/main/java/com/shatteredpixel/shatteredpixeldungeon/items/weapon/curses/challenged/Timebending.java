package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimescaleBuff;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Timebending extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		Buff.affect( defender, BentTime.class ).add( 1 );
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

	public static class BentTime extends TimescaleBuff {
		public static final float DURATION = 2f;

		private static final float SLOWDOWN = 0.8f;
		private static final float SPEEDUP = 1.3f;

		{
			type = buffType.NEGATIVE;
		}

		private int stacks = 0;

		public void add( int stacks ) {
			if ( this.stacks >= 0 ) {
				this.stacks += stacks;
				postpone( DURATION );
			}
			if ( this.stacks > 5 ) {
				spend( -cooldown() - 1 );
			}
		}

		@Override
		public int icon() {
			return stacks > 0 ? BuffIndicator.TIME : BuffIndicator.HASTE;
		}

		public String toString() {
			return Messages.get( this, stacks > 0 ? "name" : "name_fast" );
		}

		public String desc() {
			return Messages.get( this, stacks > 0 ? "desc" : "desc_fast", speedFactor(), visualcooldown() );
		}

		@Override
		public void tintIcon( Image icon ) {
			icon.hardlight( 0, 1, 2 );
		}

		@Override
		public boolean act() {
			if ( stacks > 0 ) {
				stacks = -stacks;
				postpone( DURATION * 1.5f );
				SpellSprite.show( target, SpellSprite.CLOCk );
				Sample.INSTANCE.play( Assets.Sounds.EVOKE );
				return true;
			}
			return super.act();
		}

		@Override
		public float speedFactor() {
			if ( stacks > 0 ) {
				return (float) Math.pow( SLOWDOWN, stacks );
			} else {
				return (float) Math.pow( SPEEDUP, -stacks );
			}
		}

		private static final String STACKS = "stacks";

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
	}
}
