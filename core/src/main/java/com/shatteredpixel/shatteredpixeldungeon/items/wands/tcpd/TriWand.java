package com.shatteredpixel.shatteredpixeldungeon.items.wands.tcpd;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class TriWand extends Wand {

	public static final String AC_IMBUE = "augment";

	private static final float MAX_DISBALANCE = 4f;
	public WandEffect firstEffect;
	public WandEffect secondEffect;
	public WandEffect neutralEffect;
	// > 0 first effect
	// < 0 second effect
	public int balance = 0;
	// > 0 augment first
	// < 0 augment second
	public float augment = 0;
	public WandEffect curEffect;

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_IMBUE );
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if ( action.equals( AC_IMBUE ) ) {
//			if ( imbue == 1 ) imbue = 2;
//			else imbue = 1;
		}
	}

	@Override
	protected void wandUsed() {
		if ( curEffect == firstEffect ) {
			balance++;
		} else if ( curEffect == secondEffect ) {
			balance--;
		}
		randomizeEffect();
		super.wandUsed();
	}

	@Override
	protected int chargesPerCast() {
		return curEffect.chargesPerCast();
	}

	protected float[] effectsChances() {
		return new float[]{
				weightFirst(),  //first effect
				weightSecond(), //second effect
				weightNeutral() //neutral effect
		};
	}

	public float weightFirst() {
		float weight = 1 - balance / MAX_DISBALANCE;
		if ( augment > 0 ) {
			weight *= 2;
		}
		return weight;
	}

	public float weightSecond() {
		float weight = 1 + balance / MAX_DISBALANCE;
		if ( augment < 0 ) {
			weight *= 2;
		}
		return weight;
	}

	public float weightNeutral() {
		if ( level() >= 12 ) return 0;
		return (float) Math.pow( 0.85, level() );
	}

	protected void randomizeEffect() {
		int effect = Random.chances( effectsChances() );

		if ( effect == 2 && curEffect == neutralEffect ) {
			randomizeEffect();
			return;
		}

		curEffect = new WandEffect[]{
				firstEffect,
				secondEffect,
				neutralEffect
		}[effect];
	}

	@Override
	public String statsDesc() {
		StringBuilder sb = new StringBuilder( /*Messages.get( this, "stats_desc" )*/ );

		float total = weightFirst() + weightSecond() + weightNeutral();

		float[] chances = new float[]{
				weightNeutral() / total,
				weightFirst() / total,
				weightSecond() / total
		};
		WandEffect[] wandEffects = new WandEffect[]{neutralEffect, firstEffect, secondEffect};

		boolean first = true;
		for (int i = 0, wandEffectsLength = wandEffects.length; i < wandEffectsLength; i++) {
			WandEffect wandEffect = wandEffects[i];
			if ( first ) first = false;
			else sb.append( "\n" );
			int chance = Math.round( chances[i] * 100 );
			sb.append( "_" )
					.append( wandEffect.name() )
					.append( " (" )
					.append( isIdentified() ? chance : "??" )
					.append( "%):_ " )
					.append( wandEffect.desc() );
			if ( wandEffect.augmented() ) {
				sb.append( " _" )
						.append( Messages.get( this, "augment" ) )
						.append( "_: " ).append( wandEffect.augmentDesc() );
			}
		}
		return sb.toString();
	}

	private static final String CURRENT_EFFECT = "cureffect";
	private static final String BALANCE = "balance";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		if ( curEffect == firstEffect ) bundle.put( CURRENT_EFFECT, 0 );
		else if ( curEffect == secondEffect ) bundle.put( CURRENT_EFFECT, 1 );
		else bundle.put( CURRENT_EFFECT, 2 );
		bundle.put( BALANCE, balance );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		curEffect = new WandEffect[]{
				firstEffect,
				secondEffect,
				neutralEffect
		}[bundle.getInt( CURRENT_EFFECT )];
		balance = bundle.getInt( BALANCE );
	}

	@Override
	public void onZap( Ballistica attack ) {
		curEffect.onZap( attack );
	}

	@Override
	public int targetingPos( Hero user, int dst ) {
		return curEffect.targetingPos( user, dst );
	}

	@Override
	public void fx( Ballistica bolt, Callback callback ) {
		curEffect.fx( bolt, callback );
	}

	@Override
	public void onHit( MagesStaff staff, Char attacker, Char defender, int damage ) {
		// TODO: do something on hit
	}

	protected abstract class WandEffect {
		protected int collisionProperties = Ballistica.MAGIC_BOLT;

		public abstract void onZap( Ballistica target );

		protected void fx( Ballistica bolt, Callback callback ) {
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.MAGIC_MISSILE,
					curUser.sprite,
					bolt.collisionPos,
					callback );
			Sample.INSTANCE.play( Assets.Sounds.ZAP, 1, Random.Float( 0.87f, 1.15f ) );
		}

		public int chargesPerCast() {
			return 1;
		}

		public int targetingPos( Hero user, int dst ) {
			return new Ballistica( user.pos(), dst, collisionProperties ).collisionPos;
		}

		public boolean augmented() {
			if ( this == firstEffect ) return augment > 0;
			if ( this == secondEffect ) return augment < 0;
			return false;
		}

		public String name() {
			return Messages.get( this, "name" );
		}

		public String desc() {
			return Messages.get( this, "desc" );
		}

		public String augmentDesc() {
			return Messages.get( this, "augment" );
		}
	}

	protected abstract class DamageWandEffect extends WandEffect implements Hero.Doom {
		public int min() {
			return min( buffedLvl() );
		}

		public abstract int min( int lvl );

		public int max() {
			return max( buffedLvl() );
		}

		public abstract int max( int lvl );

		public int damageRoll() {
			return Random.NormalIntRange( min(), max() );
		}

		public int damageRoll( int lvl ) {
			return Random.NormalIntRange( min( lvl ), max( lvl ) );
		}

		@Override
		public void onDeath() {
			Dungeon.fail( getClass() );
			GLog.n( Messages.get( this, "ondeath" ) );
		}
	}
}