package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackAmplificationBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Temporal extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	public static final int DAMAGE_COLOR = 0x79e3d2;

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		int dmg = attacker.damageRoll();
		dmg = AttackAmplificationBuff.damageFactor( dmg, attacker.buffs() );
		Buff.append( defender, Wound.class, Wound.DURATION ).set( dmg );
		defender.sprite.showStatus( DAMAGE_COLOR, Integer.toString( dmg ) );
		return 0;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

	public static class Wound extends FlavourBuff {
		public static final float DURATION = 5f;

		{
			type = buffType.NEGATIVE;
		}

		private int damage;

		public void set( int damage ) {
			this.damage = damage;
		}

		@Override
		public boolean act() {
			target.damage( damage, this );
			Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
			return super.act();
		}

		@Override
		public int icon() {
			return BuffIndicator.PREPARATION;
		}

		@Override
		public void tintIcon( Image icon ) {
			icon.hardlight( DAMAGE_COLOR );
		}

		@Override
		public float iconFadePercent() {
			return Math.max( 0, (DURATION - visualcooldown()) / DURATION );
		}

		@Override
		public String toString() {
			return Messages.get( this, "name" );
		}

		@Override
		public String desc() {
			return Messages.get( this, "desc", damage, dispTurns() );
		}

		private static final String DAMAGE = "damage";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( DAMAGE, damage );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			damage = bundle.getInt( DAMAGE );
		}
	}
}
