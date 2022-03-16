package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

import java.util.Set;

public class Possessed extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	private static final float BAN_DURATION = 5f;

	private boolean strongHit = false;

	@Override
	public boolean curse() {
		return true;
	}

	private static float delay( Weapon wep, Char user ) {
		float delay = wep.delayFactor( user );
		// Buff disappears slightly before the next attack is ready
		return delay * Random.oneOf( 1, 2, 3 ) - 0.01f;
	}

	public synchronized static HeroAction heroAct( Hero user ) {
		if ( user.heroClass == HeroClass.HUNTRESS ) {
			HeroAction action = bowAct( user );
			if ( action != null ) return action;
		}

		// Use raw access
		Weapon wep = (Weapon) user.belongings.weapon;
		if ( wep == null || !wep.hasEnchant( Possessed.class, user ) ) return null;

		if ( user.buff( Delay.class ) != null ) return null;
		if ( user.buff( Ready.class ) == null ) {
			Buff.prolong( user, Delay.class, delay( wep, user ) );
			return null;
		}

		Set<Char> targets = getValidTargetsInFov( user, user::canAttack );

		if ( targets.isEmpty() ) return null;

		Possessed possessed = wep.getEnchant( Possessed.class );
		possessed.strongHit = true;
		Buff.detach( user, Ready.class );

		return new HeroAction.Attack( Random.element( targets ) );
	}

	public static HeroAction bowAct( Hero user ) {
		SpiritBow bow = user.belongings.getItem( SpiritBow.class );
		if ( bow == null || !bow.hasEnchant( Possessed.class, user ) ) return null;
		SpiritBow.SpiritArrow arrow = bow.knockArrow();

		if ( user.buff( BowDelay.class ) != null ) return null;
		if ( user.buff( BowReady.class ) == null ) {
			Buff.prolong( user, BowDelay.class, delay( bow, user ) );
			return null;
		}

		boolean projecting = arrow.hasEffectiveEnchant( Projecting.class, user );
		Set<Char> targets = getValidTargetsInFov( user, ( ch ) -> arrow.targetValid( user, ch.pos(), projecting ) );
		if ( targets.isEmpty() ) return null;

		Possessed possessed = bow.getEnchant( Possessed.class );
		possessed.strongHit = true;
		Buff.detach( user, BowDelay.class );

		user.waitUntilNext = true;
		arrow.cast( user, Random.element( targets ).pos() );
		return new HeroAction.Nothing();
	}

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		if ( attacker instanceof Hero ) {
			if ( strongHit ) {
				strongHit = false;
				return damage * 2;
			} else {
				if ( weapon instanceof SpiritBow ) {
					Buff.prolong( attacker, BowDelay.class, BAN_DURATION );
				} else {
					Buff.prolong( attacker, Delay.class, BAN_DURATION );
				}
				return 0;
			}
		} else {
			if ( attacker.buff( Ready.class ) != null ) return damage * 2;
			if ( attacker.buff( Delay.class ) == null )
				Buff.prolong( attacker, Delay.class, delay( weapon, attacker ) );
			return 0;
		}
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

	public static class Delay extends FlavourBuff {
		{
			// Act before hero
			actPriority = HERO_PRIO + 1;
		}

		@Override
		public boolean act() {
			Buff.prolong( target, Ready.class, 3f );
			return super.act();
		}
	}

	public static class BowDelay extends FlavourBuff {
		{
			// Act before hero
			actPriority = HERO_PRIO + 1;
		}

		@Override
		public boolean act() {
			Buff.prolong( target, BowReady.class, 3f );
			return super.act();
		}
	}

	public static class Ready extends FlavourBuff {
	}

	public static class BowReady extends FlavourBuff {
	}
}