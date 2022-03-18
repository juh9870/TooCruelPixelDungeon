package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StormGas extends Blob {

	private static final int MAX_BOLT_DIST = 8;

	protected int strength = 0;
	protected ArrayList<Lightning.Arc> arcs;


	@Override
	protected void evolve() {
		super.evolve();

		HashMap<Integer, Float> AOE = new HashMap<>();

		if ( volume <= 0 ) {
			strength = 0;
			return;
		}

		int pos;
		Char ch;
		for (int i = area.left; i < area.right; i++) {
			for (int j = area.top; j < area.bottom; j++) {
				pos = i + j * Dungeon.level.width();
				if ( cur[pos] > 0 ) {
					float weight = 1;
					if ( (ch = Actor.findChar( pos )) != null && ch != Dungeon.hero ) weight *= 4096;
					if ( Dungeon.level.water[pos] ) weight *= 4;
					AOE.put( pos, weight );
				}
			}
		}

		if ( AOE.size() < 2 ) return;

		// http://juh9870.com/rnd/?code=return%20Math.random()*Math.random()*2%1;
		int lightnings = Math.min( Random.roundWeighted( (float) ((1 - Random.Float() * Random.Float() * 3 % 1) * Math.sqrt( AOE.size() )) ), AOE.size() - 1 );
		int iters = 0;

		if ( lightnings <= 0 ) return;

		System.out.println( "Arcs: " + lightnings );

		arcs = new ArrayList<>();
		Set<Integer> strikeCells = new HashSet<>();

		int from = Random.chances( AOE );
		AOE.remove( from );
		int to;
		while (lightnings > 0 && iters++ < 1000) {
			to = Random.chances( AOE );
			Ballistica bolt = new Ballistica( from, to, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET | Ballistica.IGNORE_SOFT_SOLID );
			if ( bolt.collisionPos != to ) continue;
			if ( bolt.dist > MAX_BOLT_DIST ) continue;

			lightnings--;

			if ( Dungeon.level.heroFOV[from] || Dungeon.level.heroFOV[to] ) {
				Char ch1;
				if ( (ch = Actor.findChar( from )) != null && Dungeon.level.heroFOV[from] ) {
					if ( (ch1 = Actor.findChar( to )) != null && Dungeon.level.heroFOV[to] ) {
						arcs.add( new Lightning.Arc( ch.sprite.center(), ch1.sprite.center() ) );
					} else {
						arcs.add( new Lightning.Arc( ch.sprite.center(), to ) );
					}
				} else if ( (ch1 = Actor.findChar( to )) != null && Dungeon.level.heroFOV[to] ) {
					arcs.add( new Lightning.Arc( from, ch1.sprite.center() ) );
				} else {
					arcs.add( new Lightning.Arc( from, to ) );
				}
			}
			List<Integer> targs = bolt.subPath( 0, bolt.dist );
			strikeCells.addAll( targs );
			AOE.remove( bolt.collisionPos );
			if ( Random.Boolean() )
				from = bolt.collisionPos;

		}
		if ( !arcs.isEmpty() )
			CellEmitter.get( from ).parent.add( new Lightning( arcs, null ) );
		for (Integer o : strikeCells) {
			strikeCell( o );
		}
	}

	protected void strikeCell( int cell ) {
		Char ch = Actor.findChar( cell );
		if ( ch != null && !ch.isImmune( getClass() ) ) {
			float dmg = Random.Float( min( strength ), max( strength ) );
			if ( Dungeon.level.water[cell] && !ch.flying ) dmg *= 1.5;
			ch.damage( (int) Math.round( dmg ), this );

			if ( ch == Dungeon.hero ) Camera.main.shake( 2, 0.3f );
			ch.sprite.centerEmitter().burst( SparkParticle.FACTORY, 3 );
			ch.sprite.flash();
		}
	}

	public static int min(int strength){
		return Math.round(strength * 0.8f);
	}
	public static int max(int strength){
		return Math.round(strength * 1.1f);
	}

	public void setStrength( int strength ) {
		this.strength = Math.max( strength, this.strength );
	}

	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );

		emitter.pour( Speck.factory( Speck.STORMGAS ), 0.2f );
	}

	@Override
	public String tileDesc( int cell ) {
		return Messages.get( this, "desc" );
	}

	private static final String STRENGTH = "strength";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( STRENGTH, strength );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		strength = bundle.getInt( STRENGTH );
	}
}