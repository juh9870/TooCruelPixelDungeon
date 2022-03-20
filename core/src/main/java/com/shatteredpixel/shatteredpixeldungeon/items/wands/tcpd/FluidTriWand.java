package com.shatteredpixel.shatteredpixeldungeon.items.wands.tcpd;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.DeadlyFluidGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SpikyRoots;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.ListUtils;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.List;

public class FluidTriWand extends TriWand {
	{
		firstEffect = new RootEffect();
		secondEffect = new GasEffect();
		neutralEffect = new NormalEffect();
		image = ItemSpriteSheet.WAND_FLUID;
		randomizeEffect();

		collisionProperties = Ballistica.PROJECTILE;
	}
	private static ItemSprite.Glowing GREEN = new ItemSprite.Glowing( 0x008811 );
	private static ItemSprite.Glowing BROWN = new ItemSprite.Glowing( 0x663300 );

	private final int SNAKES_NUM = 3;
	private int totChrgUsed = 0;

	private int maxSnakeLength(int level) {
		// 1 additional tile every 5 levels
		return 3 + level / 5;
	}

	@Override
	protected void wandUsed() {
		totChrgUsed += chargesPerCast();
		super.wandUsed();
	}

	private int chargesOverLimit() {
		return Math.max( 0, totChrgUsed - WandOfRegrowth.chargeLimit( Dungeon.hero.lvl, level() ) );
	}

	@Override
	public String statsDesc() {
		String desc = super.statsDesc();
		if ( isIdentified() ) {
			int chargeLeft = WandOfRegrowth.chargeLimit( Dungeon.hero.lvl, level() ) - totChrgUsed;
			if ( chargeLeft < 10000 ) {
				desc += "\n" + Messages.get( this, "degradation", Math.max( chargeLeft, 0 ) );
			}
		}
		return desc;
	}

	private static final String TOTAL = "totChrgUsed";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( TOTAL, totChrgUsed );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		totChrgUsed = bundle.getInt( TOTAL );
	}

	private class RootEffect extends NormalEffect {
		@Override
		public ItemSprite.Glowing augmentGlow() {
			return BROWN;
		}

		@Override
		public int indicatorColor() {
			return 0xFFAA00;
		}

		@Override
		public Class<? extends Plant.Seed> catalyst() {
			return Earthroot.Seed.class;
		}

		@Override
		public String desc() {
			return Messages.get( this, "desc", strength( visiblyUpgraded() ), damage( visiblyUpgraded() ) );
		}

		@Override
		protected void cellFx( int cell ) {
			super.cellFx( cell );
			CellEmitter.bottom( cell ).start( EarthParticle.FACTORY, 0.05f, 8 );
		}

		@Override
		protected void affectCell( int cell ) {
			super.affectCell( cell );

			Char ch = Actor.findChar( cell );
			if ( ch != null && ch != curUser ) {
				Buff.affect( ch, SpikyRoots.class ).set( strength( buffedLvl() ), damage( buffedLvl() ) );
			}
		}

		private int strength( int level ) {
			int strength = (4 + level / 5);
			if ( augmented() ) strength *= 4 / 3f;
			return strength;
		}

		private int damage( int level ) {
			return level + 1;
		}
	}

	private class GasEffect extends NormalEffect {

		@Override
		public ItemSprite.Glowing augmentGlow() {
			return GREEN;
		}

		@Override
		public int indicatorColor() {
			return 0x00FF00;
		}

		@Override
		public Class<? extends Plant.Seed> catalyst() {
			return Sungrass.Seed.class;
		}

		@Override
		public String desc() {
			int str = strength( visiblyUpgraded() );
			int min = DeadlyFluidGas.min( str );
			int max = DeadlyFluidGas.max( str );
			return Messages.get( this, "desc", min, max );
		}

		@Override
		protected void affectCell( int cell ) {
			super.affectCell( cell );

			DeadlyFluidGas g = Blob.seed( cell, seedAmount( buffedLvl() ), DeadlyFluidGas.class );
			g.setStrength( strength( buffedLvl() ) );
			GameScene.add( g );
		}

		public int strength(int level){
			return level + 1;
		}

		public int seedAmount( int level ) {
			float strength = 5 + 1.5f * level;
			if ( augmented() ) strength *= 4 / 3f;
			return Math.round( strength );
		}
	}

	private class NormalEffect extends WandEffect {

		@Override
		public ItemSprite.Glowing augmentGlow() {
			return null;
		}

		@Override
		public int indicatorColor() {
			return 0x003300;
		}

		int completedNum = 0;
		List<Integer> affectedCells;
		List<Integer> grassCells;
		float furrowedChance = 0;

		@Override
		public Class<? extends Plant.Seed> catalyst() {
			return Sorrowmoss.Seed.class;
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.FOLIAGE_SPECIFIC,
					curUser.sprite,
					bolt.collisionPos,
					() -> runSnakes( bolt.collisionPos, callback ) );
		}

		@Override
		public void onZap( Ballistica target ) {
			// All logic is applied in fx. Not the best way, but otherwise it looks shit
		}

		@Override
		public String desc() {
			return Messages.get( this, "desc", SNAKES_NUM, maxSnakeLength(buffedVisiblyUpgraded()) );
		}

		private void runSnakes( int target, Callback callback ) {
			affectedCells = new ArrayList<>();
			grassCells = new ArrayList<>();
			int maxSnakeLength = maxSnakeLength(buffedLvl());
			PathFinder.buildDistanceMap( target, Dungeon.level.passable, maxSnakeLength );
			ArrayList<Integer>[] snakes = new ArrayList[SNAKES_NUM];

			if ( totChrgUsed >= WandOfRegrowth.chargeLimit( Dungeon.hero.lvl, level() ) ) {
				furrowedChance = (chargesOverLimit() + 1) / 5f;
			}

			completedNum = 0;
			affectedCells.add( target );
			for (int i = 0; i < snakes.length; i++) {

				ArrayList<Integer> snake = snakes[i] = new ArrayList<>();
				snake.add( target );

				for (int j = 0; j < maxSnakeLength; j++) {
					Integer cell = step( snake );
					if ( cell != null ) {
						snake.add( cell );
						affectedCells.add( cell );
					} else break;
				}
			}

			int grassToPlace = Math.round( (3.67f + buffedLvl() / 3f) );
			Point targetP = Dungeon.level.cellToPoint( target );
			// Shuffle list first, so cells with same sorting priority are shuffled;
			Random.shuffle( affectedCells );
			// Then sort cells using Manhattan distance
			ListUtils.sort( affectedCells, c -> {
				Point p = Dungeon.level.cellToPoint( c );
				return Math.abs( targetP.x - p.x ) + Math.abs( targetP.y - p.y );
			} );

			for (int i = 0; i < affectedCells.size() && grassToPlace > 0; i++) {
				if ( !validGrassCell( affectedCells.get( i ) ) ) continue;
				grassCells.add( affectedCells.get( i ) );
				grassToPlace--;
			}

			for (ArrayList<Integer> snake : snakes) {
				moveSnake( snake, 0, callback );
			}
			if ( Dungeon.level.heroFOV[target] )
				cellFx( target );
			affectCell( target );
		}

		private void moveSnake( ArrayList<Integer> snake, int index, Callback callback ) {
			if ( index < snake.size() - 1 ) {
				MagicMissile.boltFromCell(
						MagicMissile.FOLIAGE_SNAKE,
						snake.get( index ),
						snake.get( index + 1 ),
						100,
						() -> {
							int cell = snake.get( index + 1 );
							if ( Dungeon.level.heroFOV[cell] ) {
								cellFx( cell );
							}
							affectCell( cell );
							moveSnake( snake, index + 1, callback );
						}
				);
			} else {
				synchronized (this) {
					completedNum++;
					if ( completedNum >= SNAKES_NUM ) {
						callback.call();
						completedNum = 0;
					}
				}
			}
		}

		protected void cellFx( int cell ) {
			CellEmitter.get( cell ).burst( LeafParticle.LEVEL_SPECIFIC, 10 );
		}

		protected boolean validGrassCell( int cell ) {
			int terr = Dungeon.level.map[cell];
			if ( !(terr == Terrain.EMPTY || terr == Terrain.EMBERS || terr == Terrain.EMPTY_DECO ||
					terr == Terrain.GRASS || terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS) )
				return false;
			if ( Char.hasProp( Actor.findChar( cell ), Char.Property.IMMOVABLE ) ) return false;
			return Dungeon.level.plants.get( cell ) == null;
		}

		protected void affectCell( int cell ) {
			if ( validGrassCell( cell ) ) {
				if ( grassCells.contains( cell ) ) {
					if ( Random.Float() >= furrowedChance ) {
						Level.set( cell, Terrain.HIGH_GRASS );
					} else if ( Dungeon.level.map[cell] != Terrain.HIGH_GRASS ) {
						Level.set( cell, Terrain.FURROWED_GRASS );
					}
				} else {
					if ( Dungeon.level.map[cell] != Terrain.HIGH_GRASS &&
							Dungeon.level.map[cell] != Terrain.FURROWED_GRASS ) {
						Level.set( cell, Terrain.GRASS );
					}
				}
				GameScene.updateMap( cell );
			}
		}

		private Integer step( ArrayList<Integer> snake ) {
			float[] steps = new float[]{
					0,
					0,
					0,
					0,
			};

			boolean foundCell = false;

			int c = snake.get( snake.size() - 1 );

			//Snakes can't move diagonally
			for (int i = 0; i < PathFinder.NEIGHBOURS4.length; i++) {
				int cell = c + PathFinder.NEIGHBOURS4[i];

				if ( Dungeon.level.insideMap( cell ) && Dungeon.level.passable[cell] && !snake.contains( cell ) ) {
					Char ch = Actor.findChar( cell );

					//Cells with enemies have extreme priority
					if ( ch != null && ch.alignment == Char.Alignment.ENEMY ) steps[i] = 4096;
						//Try to get as far as possible from origin
					else if ( PathFinder.distance[cell] > PathFinder.distance[c] ) steps[i] = 2;
						//We still have chance to go closer to origin
					else steps[i] = 1;

					//Only go there if have no other options
					if ( affectedCells.contains( cell ) ) steps[i] = 0.01f;
					foundCell = true;
				}
			}

			if ( foundCell ) {
				return c + PathFinder.NEIGHBOURS4[Random.chances( steps )];
			}

			return null;
		}
	}
}
