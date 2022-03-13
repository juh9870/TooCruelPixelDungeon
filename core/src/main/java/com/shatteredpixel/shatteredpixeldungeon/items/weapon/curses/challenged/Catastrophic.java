package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.MovingVisual;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;

import java.util.HashSet;
import java.util.Set;

public class Catastrophic extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	private static final int DISTANCE = 2;
	private static final float FACTOR = 0.5f;

	@Override
	public boolean curse() {
		return true;
	}

	private boolean processing = false;

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		if ( processing ) return damage;
		processing = true;
		int image = weapon.image;
		if ( weapon instanceof SpiritBow ) {
			image = ((SpiritBow) weapon).knockArrow().image;
		}
		Set<Integer> cells = new HashSet<>();
		if ( Dungeon.level.distance( attacker.pos(), defender.pos() ) <= DISTANCE ) {
			PathFinder.buildDistanceMap( defender.pos(), BArray.not( Dungeon.level.solid, null ) );
			for (int i = 0; i < Dungeon.level.length(); i++) {
				if ( PathFinder.distance[i] <= DISTANCE ) {
					cells.add( i );
				}
			}
		} else {
			Ballistica bal = new Ballistica( attacker.pos(), defender.pos(), Ballistica.PROJECTILE );
			for (int i = 1; i < bal.path.size(); i++) {
				int cell = bal.path.get( i );
				for (int o : PathFinder.NEIGHBOURS9) {
					if ( !Dungeon.level.solid[cell + o] ) {
						cells.add( cell + o );
					}
				}
				if ( cell == defender.pos() ) break;
			}
		}

		for (Integer cell : cells) {
			Char ch = Actor.findChar( cell );
			float angle = 180 - ItemSpriteSheet.weaponAngle( image );
			if ( ch != null ) {
				if ( ch != defender ) attacker.attack( ch, ch == attacker ? FACTOR : 1f, 0, Char.INFINITE_ACCURACY );
				if ( Dungeon.level.heroFOV[cell] ) {
					MovingVisual.show( MovingVisual.fromChar( ch ),
							new ItemSprite( image ),
							angle,
							new float[]{0.1f, 0.1f, 0.2f},
							new PointF( 0, -DungeonTilemap.SIZE - ch.sprite.height() / 2 ),
							new PointF( 0, 0 ),
							null );
				}
			}
		}
		GameScene.tilesOutlines().show( cells, attacker.pos(), 0x88000000 + Temporal.DAMAGE_COLOR, 1, false );
		processing = false;

		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}
}