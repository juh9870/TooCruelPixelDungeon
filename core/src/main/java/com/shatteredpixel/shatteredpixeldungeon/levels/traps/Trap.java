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
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.LevelObject;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks.Chapter;
import com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks.Marker;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import com.watabou.utils.Reflection;

public abstract class Trap implements Bundlable {

	//trap colors
	public static final int RED = 0;
	public static final int ORANGE = 1;
	public static final int YELLOW = 2;
	public static final int GREEN = 3;
	public static final int TEAL = 4;
	public static final int VIOLET = 5;
	public static final int WHITE = 6;
	public static final int GREY = 7;
	public static final int BLACK = 8;

	//trap shapes
	public static final int DOTS = 0;
	public static final int WAVES = 1;
	public static final int GRILL = 2;
	public static final int STARS = 3;
	public static final int DIAMOND = 4;
	public static final int CROSSHAIR = 5;
	public static final int LARGE_DOT = 6;

	public static Class<? extends Trap>[] trapClasses = new Class[]{
			AlarmTrap.class,
			BlazingTrap.class,
			BurningTrap.class,
			AlarmTrap.class,
			BlazingTrap.class,
			BurningTrap.class,
			ChillingTrap.class,
			ConfusionTrap.class,
			CorrosionTrap.class,
			CursedWandTrap.class,
			CursingTrap.class,
			DisarmingTrap.class,
			DisintegrationTrap.class,
			DistortionTrap.class,
			ExplosiveTrap.class,
			FlashingTrap.class,
			FlockTrap.class,
			FrostTrap.class,
			GatewayTrap.class,
			GeyserTrap.class,
			GrimTrap.class,
			GrippingTrap.class,
			GuardianTrap.class,
			OozeTrap.class,
			PitfallTrap.class,
			PoisonDartTrap.class,
			RockfallTrap.class,
			ShockingTrap.class,
			StormTrap.class,
			SummoningTrap.class,
			TeleportationTrap.class,
			TenguDartTrap.class,
			ToxicTrap.class,
			WarpingTrap.class,
			WeakeningTrap.class,
			WornDartTrap.class,
	};

	public static Trap randomTrap() {
		return Reflection.newInstance( Random.element( trapClasses ) );
	}

	public static Trap adjustTrap( Trap t ) {
		Marker m = Dungeon.depth();
		int chapter = m.scalingChapter();
		// No grim trap before halls
		if ( chapter < 4 && t instanceof GrimTrap ) t = new DisintegrationTrap();
		// No disintegration and distortion before city
		if ( chapter < 3 && t instanceof DisintegrationTrap ) t = new CursedWandTrap();
		if ( chapter < 3 && t instanceof DistortionTrap ) t = new SummoningTrap();
		// No tengu traps before caves
		if ( chapter < 2 && t instanceof TenguDartTrap ) t = new CursedWandTrap();

		// Gateway doesn't work with randomization
		if ( t instanceof GatewayTrap ) t = new TeleportationTrap();
		return t;
	}

	public int color;
	public int shape;

	public int pos;

	public boolean visible;
	public boolean active = true;
	public boolean disarmedByActivation = !Challenges.REPEATER.enabled();

	public boolean canBeHidden = true;
	public boolean canBeSearched = true;

	private void applyChallengedSprite() {
		if ( Challenges.INDIFFERENT_DESIGN.enabled() ) {
			Random.pushGenerator( Dungeon.seed + Dungeon.levelPack.curLevelFileName().hashCode() );
			color = Random.Int( 8 );
			shape = Random.Int( 7 );
			Random.popGenerator();
		}

	}

	public boolean avoidsHallways = false; //whether this trap should avoid being placed in hallways

	public Trap set( int pos ) {
		this.pos = pos;
		applyChallengedSprite();
		return this;
	}

	public Trap reveal() {
		visible = true;
		applyChallengedSprite();
		if ( Dungeon.level != null ) Dungeon.level.fullFlagsUpdate( pos );
		GameScene.updateMap( pos );
		return this;
	}

	public Trap hide() {
		if ( canBeHidden ) {
			visible = false;
			if ( Dungeon.level != null ) Dungeon.level.fullFlagsUpdate( pos );
			GameScene.updateMap( pos );
			return this;
		} else {
			return reveal();
		}
	}

	public void trigger() {
		if ( active ) {
			if ( Dungeon.level.heroFOV[pos] ) {
				Sample.INSTANCE.play( Assets.Sounds.TRAP );
			}
			if ( (disarmedByActivation || Challenges.CHAOTIC_CONSTRUCTION.enabled()) && !Challenges.REPEATER.enabled() ) {
				disarm();
				Dungeon.level.discover( pos );
			}
			if ( Challenges.DUPLICATOR.enabled() ) {
				if ( !getClass().isAnonymousClass() ) {
					int nTraps = 2;
					for (int i = 0; i < nTraps; i++) {
						int cell = Dungeon.level.randomTrapCell();
						if ( cell == -1 ) break;
						Trap t = Dungeon.level.setTrap( clone(), cell );
//						Level.set(cell, Terrain.TRAP);
						t.reveal();
					}
				}
			}
			if ( !Challenges.CHAOTIC_CONSTRUCTION.enabled() ) {
				activate();
				return;
			}

			Random.pushGenerator( Dungeon.seed + Dungeon.levelPack.curLevelFileName().hashCode() );
			int repeats = Challenges.TRAP_TESTING_FACILITY.enabled() ? Random.NormalIntRange( 2, 5 ) : 1;
			Class<? extends Trap>[] traps = new Class[repeats];
			for (int i = 0; i < repeats; i++) {
				traps[i] = Random.element( trapClasses );
			}
			Random.popGenerator();
			for (Class<? extends Trap> trap : traps) {
				Trap t = Reflection.newInstance( trap );

				t = adjustTrap( t );

				if ( DeviceCompat.isDebug() ) {
					t = new FlockTrap();
				}

				if ( t == null ) {
					activate();
					return;
				}
				t.pos = pos;
				t.activate();
			}
		}
	}

	public abstract void activate();

	@Override
	protected Trap clone() {
		if ( getClass().isAnonymousClass() ) return null;
		Trap t = Reflection.newInstance( getClass() );
		Bundle b = new Bundle();
		storeInBundle( b );
		t.restoreFromBundle( b );
		return t;
	}

	public void disarm() {
		active = false;
		Dungeon.level.disarmTrap( pos );
	}

	public String name() {
		if ( Challenges.INDIFFERENT_DESIGN.enabled() ) return Messages.get( this, "name_unknown" );
		return Messages.get( this, "name" );
	}

	public String desc() {
		if ( Challenges.INDIFFERENT_DESIGN.enabled() ) return Messages.get( this, "desc_unknown" );
		return Messages.get( this, "desc" );
	}

	private static final String POS = "pos";
	private static final String VISIBLE = "visible";
	private static final String ACTIVE = "active";

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		pos = bundle.getInt( POS );
		visible = bundle.getBoolean( VISIBLE );
		if ( bundle.contains( ACTIVE ) ) {
			active = bundle.getBoolean( ACTIVE );
		}
		applyChallengedSprite();
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( POS, pos );
		bundle.put( VISIBLE, visible );
		bundle.put( ACTIVE, active );
	}

	public static class TrapTrigger extends LevelObject {
		public static void plant( int pos, Trap trap, float delay, boolean show ) {
			TrapTrigger trapTrigger = new TrapTrigger();
			Dungeon.level.setObject( trapTrigger, pos );
			trapTrigger.postpone( delay );
			Dungeon.level.setTrap( trap, pos );
			GameScene.updateMap( pos );

			if ( show && !trap.visible ) {
				PrisonBossLevel.FadingTraps fading = new PrisonBossLevel.FadingTraps();
				fading.setFadeDelay( delay );
				fading.setCoveringArea( new Rect( Dungeon.level.cellToPoint( pos ) ) );
				GameScene.add( fading, false );
				Dungeon.level.customTiles.add( fading );
			}
		}

		@Override
		protected boolean act() {
			Trap t = Dungeon.level.getTrap( pos );
			if ( t != null ) {
				t.reveal();
				t.trigger();
			}
			Dungeon.level.removeObject( this );
			return true;
		}
	}
}
