package com.shatteredpixel.shatteredpixeldungeon.items.wands.tcpd;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.DewGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Dreamfoil;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class CircularTriWand extends TriWand {

	//Light damage source for resistances
	public static class Light {
	}

	private static final ItemSprite.Glowing GOLDEN = new ItemSprite.Glowing( 0xFFD700 );
	private static final ItemSprite.Glowing BLUE = new ItemSprite.Glowing( 0x0000FF );

	{
		neutralEffect = new NormalEffect();
		firstEffect = new LightEffect();
		secondEffect = new StormEffect();

		image = ItemSpriteSheet.WAND_CIRCULAR;
		randomizeEffect();

		collisionProperties = Ballistica.PROJECTILE;
	}

	private static final float UNDEAD_BONUS = 4 / 3f;

	private class StormEffect extends WandEffect {

		@Override
		public ItemSprite.Glowing augmentGlow() {
			return BLUE;
		}

		@Override
		public int indicatorColor() {
			return 0x00AAFF;
		}

		@Override
		public Class<? extends Plant.Seed> catalyst() {
			return Stormvine.Seed.class;
		}

		public int strength( int level ) {
			float strength = 2 + level;
			if ( augmented() ) strength *= 4 / 3f;
			return Math.round( strength );
		}

		@Override
		public void onZap( Ballistica target ) {

			neutralEffect.onZap( target );
			((DewGas) Dungeon.level.blobs.get( DewGas.class )).storm( target.collisionPos );
			StormGas sg = (StormGas) Dungeon.level.blobs.get( StormGas.class );
			sg.setStrength( strength( buffedLvl() ) );

		}

		@Override
		public String desc() {
			return Messages.get( this, "desc",
					StormGas.min( strength( buffedVisiblyUpgraded() ) ),
					StormGas.max( strength( buffedVisiblyUpgraded() ) ) );
		}
	}

	private class LightEffect extends DamageWandEffect {

		@Override
		public ItemSprite.Glowing augmentGlow() {
			return GOLDEN;
		}

		@Override
		public int indicatorColor() {
			return 0xffff00;
		}

		@Override
		public Class<? extends Plant.Seed> catalyst() {
			return Fadeleaf.Seed.class;
		}

		private static final int DIST = 8;

		@Override
		public void onZap( Ballistica target ) {
			boolean[] FOV = new boolean[Dungeon.level.length()];
			Point c = Dungeon.level.cellToPoint( target.collisionPos );
			ShadowCaster.castShadow( c.x, c.y, FOV, Dungeon.level.losBlocking, DIST );

			int sX = Math.max( 0, c.x - DIST );
			int eX = Math.min( Dungeon.level.width() - 1, c.x + DIST );

			int sY = Math.max( 0, c.y - DIST );
			int eY = Math.min( Dungeon.level.height() - 1, c.y + DIST );

			boolean noticed = false;

			float noticeChance = .5f;
			if ( augmented() ) noticeChance = .75f;

			for (int y = sY; y <= eY; y++) {
				int curr = y * Dungeon.level.width() + sX;
				for (int x = sX; x <= eX; x++) {

					if ( FOV[curr] ) {
						Dungeon.level.mapped[curr] = true;

						if ( Dungeon.level.secret[curr] && Random.Float() < noticeChance ) {
							Dungeon.level.discover( curr );

							if ( Dungeon.level.heroFOV[curr] ) {
								GameScene.discoverTile( curr, Dungeon.level.map[curr] );
								ScrollOfMagicMapping.discover( curr );
								noticed = true;
							}
						}

						int distance = augmented() ? 7 : 5;
						Char ch = Actor.findChar( curr );
						if ( ch != null && ch.distance( curUser ) < distance ) {
							wandProc( ch, chargesPerCast() );
							affectTarget( ch );
						}
						CellEmitter.get( curr ).start( ShaftParticle.FACTORY, 0.5f, 1 );
					}
					curr++;
				}
			}

			if ( noticed ) {
				Sample.INSTANCE.play( Assets.Sounds.SECRET );
			}

			Sample.INSTANCE.play( Assets.Sounds.TELEPORT );
			GameScene.updateFog();

			neutralEffect.onZap( target );
		}

		private void affectTarget( Char ch ) {
			if ( ch == curUser ) return;
			int dmg = damageRoll();

			//three in (5+lvl) chance of failing
			if ( Random.Int( 5 + buffedLvl() ) >= 3 ) {
				Buff.prolong( ch, Blindness.class, 2f + (buffedLvl() * 0.333f) );
				ch.sprite.emitter().burst( Speck.factory( Speck.LIGHT ), 6 );
			}

			if ( ch.properties().contains( Char.Property.DEMONIC ) || ch.properties().contains( Char.Property.UNDEAD ) ) {
				ch.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 + buffedLvl() );
				Sample.INSTANCE.play( Assets.Sounds.BURNING );

				ch.damage( Math.round( dmg * UNDEAD_BONUS ), this );
			} else {
				int distance = curUser.distance( ch );
				ch.sprite.centerEmitter().burst( RainbowParticle.BURST, 10 + buffedLvl() );

				float maxDistance = augmented() ? 7 : 5;
				ch.damage( Math.round( dmg * (1 - distance / maxDistance) ), this );
			}
		}

		@Override
		public String desc() {
			int min = min( buffedVisiblyUpgraded() );
			int max = max( buffedVisiblyUpgraded() );
			int dMin = Math.round( min * UNDEAD_BONUS );
			int dMax = Math.round( max * UNDEAD_BONUS );
			return Messages.get( this, "desc", min, max, dMin, dMax );
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			MagicMissile.boltFromChar( curUser.sprite.parent, MagicMissile.RAINBOW, curUser.sprite, bolt.collisionPos, callback );
		}

		public int min( int lvl ) {
			return 1 + lvl;
		}

		public int max( int lvl ) {
			return 5 + 2 * lvl;
		}
	}

	private class NormalEffect extends WandEffect {

		@Override
		public ItemSprite.Glowing augmentGlow() {
			return null;
		}

		@Override
		public int indicatorColor() {
			return 0XBEBEBE;
		}

		@Override
		public Class<? extends Plant.Seed> catalyst() {
			return Dreamfoil.Seed.class;
		}

		@Override
		public void onZap( Ballistica target ) {
			GameScene.add( Blob.seed( target.collisionPos, 50 + 10 * buffedLvl(), DewGas.class ) );
		}

		@Override
		protected void fx( Ballistica bolt, Callback callback ) {
			MagicMissile.boltFromChar( curUser.sprite.parent, MagicMissile.BEACON, curUser.sprite, bolt.collisionPos, callback );
		}
	}
}