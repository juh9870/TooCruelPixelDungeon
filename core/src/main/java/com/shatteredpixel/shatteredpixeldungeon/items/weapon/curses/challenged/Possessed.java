package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Annoying;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.Set;

public class Possessed extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	private static final float BAN_DURATION = 5f;

	private boolean strongHit = false;

	private static float delay( Weapon wep, Char user ) {
		float delay = wep.delayFactor( user );
		if ( wep instanceof SpiritBow ) {
			return delay * Random.chances( 0, 1, 2, 2, 1 ) - 0.01f;
		}
		return delay * Random.chances( 0, 1, 1, 1 ) - 0.01f;
	}

	public static synchronized HeroAction heroAct( Hero user ) {
		if ( user.heroClass == HeroClass.HUNTRESS ) {
			HeroAction action = bowAct( user );
			if ( action != null ) return action;
		}

		// Use raw access
		Weapon wep = (Weapon) user.belongings.weapon;
		if ( wep == null || !wep.hasEnchant( Possessed.class, user ) ) return null;
		Set<Char> targets = getValidTargetsInFov( user, user::canAttack );

		// Melee weapon instantly wakes up all enemies in range
		wakeUpTargets( user, targets );
		if ( user.buff( Delay.class ) != null ) return null;
		if ( user.buff( Ready.class ) == null ) {
			Buff.prolong( user, Delay.class, delay( wep, user ) );
			return null;
		}


		if ( targets.isEmpty() ) return null;

		Possessed possessed = wep.getEnchant( Possessed.class );
		possessed.strongHit = true;
		Buff.detach( user, Ready.class );

		return new HeroAction.Attack( Random.element( targets ) );
	}

	public static synchronized HeroAction bowAct( Hero user ) {
		SpiritBow bow = user.belongings.getItem( SpiritBow.class );
		if ( bow == null || !bow.hasEnchant( Possessed.class, user ) ) return null;
		SpiritBow.SpiritArrow arrow = bow.knockArrow();

		boolean projecting = arrow.hasEffectiveEnchant( Projecting.class, user );
		Set<Char> targets = getValidTargetsInFov( user, ( ch ) -> arrow.targetValid( user, ch.pos(), projecting ) );

		if ( user.buff( BowDelay.class ) != null ) return null;
		if ( user.buff( BowReady.class ) == null ) {
			Buff.prolong( user, BowDelay.class, delay( bow, user ) );
			// Bow only wakes up enemies when preparing
			wakeUpTargets( user, targets );
			return null;
		}

		if ( targets.isEmpty() ) return null;

		Possessed possessed = bow.getEnchant( Possessed.class );
		possessed.strongHit = true;
		Buff.detach( user, BowReady.class );

		user.waitUntilNext = true;
		arrow.cast( user, Random.element( targets ).pos() );
		return new HeroAction.Nothing();
	}

	public static void wakeUpTargets( Hero user, Set<Char> targets ) {
		if ( targets.isEmpty() ) return;
		boolean wokeUp = false;
		for (Char target : targets) {
			if ( !(target instanceof Mob) || ((Mob) target).state != ((Mob) target).SLEEPING ) continue;
			((Mob) target).beckon( user.pos() );
			wokeUp = true;
		}
		if ( !wokeUp ) return;
		Sample.INSTANCE.play( Assets.Sounds.MIMIC );
		Invisibility.dispel();
		GLog.n( Messages.get( Annoying.class, "msg_" + (Random.Int( 5 ) + 1) ) );
		user.sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.3f, 1 );
	}

	@Override
	public boolean curse() {
		return true;
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
			Buff.prolong( target, Ready.class, 2f );
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
			Buff.prolong( target, BowReady.class, 2f );
			return super.act();
		}
	}

	public static class Ready extends FlavourBuff {
	}

	public static class BowReady extends FlavourBuff {
	}
}