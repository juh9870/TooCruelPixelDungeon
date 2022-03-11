package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EnergyParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Destabilizing extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		// Additional target at every other level
		int targets = Math.max( weapon.buffedLvl(), 0 ) / 2 + 2;
		Buff.affect( defender, Destabilized.class ).targets( targets ).addStacks( 1 );
		Buff.affect( attacker, Destabilized.class ).targets( targets ).addStacks( 1 );
		Buff.prolong( attacker, Magnetic.class, Magnetic.DURATION );
		Emitter e = defender.sprite.centerEmitter();
		if ( e != null ) e.burst( EnergyParticle.FACTORY, 7 );
		Sample.INSTANCE.play( Assets.Sounds.CHARGEUP, 0.5f );
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}


	public static class Destabilized extends Buff {

		{
			type = buffType.NEGATIVE;
			revivePersists = true;
		}

		private int stacks;
		private int targetsCount = 2;

		public Destabilized targets( int targetsCount ) {
			this.targetsCount = Math.max( this.targetsCount, targetsCount );
			return this;
		}

		public Destabilized addStacks( int stacks ) {
			this.stacks += stacks;
			return this;
		}

		@Override
		public void onDeathProc( Object src, boolean fakeDeath ) {
			int pos = target.pos();
			PathFinder.buildDistanceMap( target.pos(), BArray.not( Dungeon.level.solid, null ) );
			List<Char> chars = new ArrayList<>();
			Map<Char, Float> charsPriorities = new HashMap<>();
			for (Char ch : Actor.chars()) {
				if ( ch == target ) continue;
				float priority = Dungeon.level.trueDistance( pos, ch.pos() );
				if ( ch.buff( Magnetic.class ) != null ) priority /= 2f;
				charsPriorities.put( ch, priority );
				chars.add( ch );
			}
			Collections.sort( chars, ( a, b ) -> (int) Math.signum( charsPriorities.get( a ) - charsPriorities.get( b ) ) );
			HashSet<Char> targets = new HashSet<>();
			int targetsLeft = this.targetsCount;
			for (Char ch : chars) {
				if ( ch == target ) continue;
				Ballistica bolt = new Ballistica( pos, ch.pos(), Ballistica.PROJECTILE );
				if ( bolt.collisionPos == ch.pos() ) {
					targets.add( ch );
					targetsLeft--;
					if ( targetsLeft <= 0 ) break;
				}
			}
			Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );

			for (Char ch : targets) {
				ch.sprite.parent.addToFront( new Lightning( target.sprite.center(), ch.sprite.center(), null ) );
				ch.damage( damage(), this );
				if ( stacks > 1 )
					Buff.affect( ch, Destabilized.class ).targets( Math.max( 0, targetsCount - 1 ) ).addStacks( stacks / 2 );
			}
			if ( target instanceof Hero ) detach();
		}

		private int damage() {
			return (int) (target.HT / 20f * stacks);
		}

		@Override
		public int icon() {
			return BuffIndicator.RECHARGING;
		}

		@Override
		public void tintIcon( Image icon ) {
			icon.hardlight( 0x79e3d2 );
		}

		@Override
		public String toString() {
			return Messages.get( this, "name" );
		}

		@Override
		public String desc() {
			return Messages.get( this, "desc", stacks * 5, targetsCount, damage() );
		}

		private static final String STACKS = "stacks";
		private static final String TARGETS = "targets";

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( STACKS, stacks );
			bundle.put( TARGETS, targetsCount );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			stacks = bundle.getInt( STACKS );
			targetsCount = bundle.getInt( TARGETS );
		}
	}

	public static class Magnetic extends FlavourBuff {
		public static final float DURATION = 5f;
	}
}
