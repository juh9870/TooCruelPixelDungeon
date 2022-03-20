package com.shatteredpixel.shatteredpixeldungeon.items.wands.tcpd;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Chilling;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.function.Consumer;
import com.watabou.utils.function.Predicate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class ThermalTriWand extends TriWand {

	{
		firstEffect = new FireEffect();
		secondEffect = new FrostEffect();
		neutralEffect = new NormalEffect();
		randomizeEffect();
		image = ItemSpriteSheet.WAND_THERMAL;
	}
	private static ItemSprite.Glowing ORANGE = new ItemSprite.Glowing( 0xFF4400 );
	private static ItemSprite.Glowing TEAL = new ItemSprite.Glowing( 0x00FFFF );

	private static final int aoeBallisticaParams = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID;

	public static final float ELEMENTAL_BONUS = 4 / 3f;

	private static Set<Integer> aoe( boolean augmented, Ballistica target, Predicate<Integer> filter, Consumer<Integer> action ) {
		int[] aoe = augmented ? PathFinder.NEIGHBOURS25 : PathFinder.NEIGHBOURS9;

		Set<Integer> validCells = new LinkedHashSet<>();

		for (int o : aoe) {
			int cell = target.collisionPos + o;
			if ( new Ballistica( target.collisionPos, cell, aoeBallisticaParams ).collisionPos != cell )
				continue;
			if ( cell == curUser.pos() ) continue;
			if ( !filter.test( cell ) ) continue;
			validCells.add( cell );
			if ( action != null ) action.accept( cell );
		}
		WandOfBlastWave.BlastWave.blast( target.collisionPos, augmented ? 3 : 5 );
		return validCells;
	}

	private void blastCell( int cell, int damage ) {
		Dungeon.level.pressCell( cell );
		if ( cell == curUser.pos() ) return;

		if ( Dungeon.level.map[cell] == Terrain.DOOR ) {
			Level.set( cell, Terrain.OPEN_DOOR );
			GameScene.updateMap( cell );
		}

		Char targ;
		if ( (targ = Actor.findChar( cell )) != null ) {
			targ.damage( damage, this );
			wandProc( targ, chargesPerCast() );
		}
	}

	private class FireEffect extends NormalEffect {

		@Override
		public ItemSprite.Glowing augmentGlow() {
			return ORANGE;
		}

		@Override
		public int indicatorColor() {
			return ORANGE.color;
		}

		@Override
		public Class<? extends Plant.Seed> catalyst() {
			return Firebloom.Seed.class;
		}

		@Override
		public void onZap( Ballistica target ) {
			boolean augmented = augmented();
			int damage = damageRoll();
			Set<Integer> validCells = aoe( augmented, target,
					( cell ) -> Dungeon.level.flamable[cell] || !(Dungeon.level.solid[cell] || Dungeon.level.pit[cell]),
					( cell ) -> {
						int dmg = damage;
						Char ch = Actor.findChar( cell );
						if ( ch != null ) {
							if ( ch.buff( Frost.class ) == null || ch.buff( Chill.class ) == null ) {
								Buff.detach( ch, Frost.class );
								Buff.detach( ch, Chill.class );
								dmg *= ELEMENTAL_BONUS;
								CellEmitter.get( cell ).burst( SmokeParticle.FACTORY, 4 );
							}

							GameScene.add( Blob.seed( cell, 1, Fire.class ) );
							wandProc( ch, chargesPerCast() );
							CellEmitter.get( cell ).burst( FlameParticle.FACTORY, 2 );
						}
						blastCell( cell, dmg );
					} );

			int burnCellsNum = (int) Math.ceil( validCells.size() * burnPercentage( buffedLvl() ) );
			for (int i = 0; i < burnCellsNum && !validCells.isEmpty(); i++) {
				int cell = Random.element( validCells );
				validCells.remove( cell );
				GameScene.add( Blob.seed( cell, burnDuration(), Fire.class ) );
				CellEmitter.get( cell ).burst( FlameParticle.FACTORY, 5 );
			}
		}

		private float burnPercentage( int level ) {
			return Math.min( 1, 0.4f + level / 10f );
		}

		private int burnDuration() {
			return augmented() ? 3 : 2;
		}

		@Override
		public String desc() {
			int burn = Math.round( burnPercentage( buffedVisiblyUpgraded() ) * 100 );
			return Messages.get( this, "desc", burn, burnDuration() );
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.FIRE,
					curUser.sprite,
					bolt.collisionPos,
					callback );
			Sample.INSTANCE.play( Assets.Sounds.ZAP );
		}
	}

	private class FrostEffect extends NormalEffect {

		@Override
		public ItemSprite.Glowing augmentGlow() {
			return TEAL;
		}

		@Override
		public int indicatorColor() {
			return TEAL.color;
		}

		@Override
		public Class<? extends Plant.Seed> catalyst() {
			return Icecap.Seed.class;
		}

		@Override
		public void onZap( Ballistica target ) {
			boolean augmented = augmented();
			int damage = damageRoll();
			aoe( augmented, target, ( c ) -> !Dungeon.level.solid[c] && !Dungeon.level.pit[c], ( cell ) -> {
				Heap heap = Dungeon.level.heaps.get( cell );
				if ( heap != null ) {
					heap.freeze();
				}

				Char ch = Actor.findChar( cell );
				if ( ch != null ) {
					int dmg = damage;
					boolean freeze = false;

					if ( ch.buff( Burning.class ) != null || Fire.volumeAt( cell, Fire.class ) > 0 ) {
						Fire.cleatAt( cell, Fire.class );
						Buff.detach( ch, Burning.class );
						dmg *= ELEMENTAL_BONUS;
						CellEmitter.get( cell ).burst( SmokeParticle.FACTORY, 4 );
						freeze = augmented();
					}

					if ( ch.buff( Chill.class ) == null ) {
						ch.sprite.burst( 0xFF99CCFF, buffedLvl() / 2 + 2 );
					}

					blastCell( cell, dmg );

					if ( ch.buff( Frost.class ) != null ) {
						return; //do nothing, can't affect a frozen target
					}

					int duration = chillDuration( Dungeon.level.water[ch.pos()], buffedLvl() );
					if ( ch.isAlive() ) {
						Buff.affect( ch, Chill.class, duration );
						Chill chill = ch.buff( Chill.class );
						if ( chill != null && chill.cooldown() > Chill.DURATION ) {
							Buff.affect( ch, Frost.class, chill.cooldown() );
						}
					}
					if ( freeze ) {
						Buff.affect( ch, Frost.class, duration );
					}
				}
			} );
		}

		private int chillDuration( boolean water, int level ) {
			return water ? 4 + level : 2 + level;
		}

		@Override
		public String desc() {
			int chill = chillDuration( false, buffedVisiblyUpgraded() );
			int waterChill = chillDuration( true, buffedVisiblyUpgraded() );
			return Messages.get( this, "desc", chill, waterChill );
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.FROST,
					curUser.sprite,
					bolt.collisionPos,
					callback );
			Sample.INSTANCE.play( Assets.Sounds.ZAP );
		}
	}

	private class NormalEffect extends DamageWandEffect {

		@Override
		public ItemSprite.Glowing augmentGlow() {
			return null;
		}

		@Override
		public int indicatorColor() {
			return 0x330000;
		}

		@Override
		public Class<? extends Plant.Seed> catalyst() {
			return Blindweed.Seed.class;
		}

		public int min( int lvl ) {
			return 1 + lvl;
		}

		public int max( int lvl ) {
			return 5 + 2 * lvl;
		}

		@Override
		public String desc() {
			int min = min( buffedVisiblyUpgraded() );
			int max = max( buffedVisiblyUpgraded() );
			return Messages.get( this, "desc", min, max );
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.FIRE,
					curUser.sprite,
					bolt.collisionPos,
					callback );
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.SMOKE,
					curUser.sprite,
					bolt.collisionPos,
					() -> {
					} );
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.MAGIC_MISSILE,
					curUser.sprite,
					bolt.collisionPos,
					() -> {
					} );
			Sample.INSTANCE.play( Assets.Sounds.ZAP );
		}

		@Override
		public void onZap( Ballistica target ) {
			for (int c : PathFinder.NEIGHBOURS9) {
				int cell = target.collisionPos + c;
				blastCell( cell, damageRoll() );
			}
			WandOfBlastWave.BlastWave.blast( target.collisionPos );
		}
	}
}