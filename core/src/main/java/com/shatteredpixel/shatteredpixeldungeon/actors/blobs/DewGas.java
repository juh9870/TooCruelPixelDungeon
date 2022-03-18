package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DewGas extends Blob {

	@Override
	protected void evolve() {
		super.evolve();

		Char ch;
		int cell;

		ArrayList<Blob> blobs = new ArrayList<>();
		for (Class c : new BlobImmunity().immunities()) {
			Blob b = Dungeon.level.blobs.get( c );
			if ( b != null && b.volume > 0 ) {
				blobs.add( b );
			}
		}

		for (int i = area.left; i < area.right; i++) {
			for (int j = area.top; j < area.bottom; j++) {
				cell = i + j * Dungeon.level.width();
				if ( cur[cell] > 0 ) {

					boolean cleared = false;
					for (Blob blob : blobs) {
						int value = blob.cur[cell];
						if ( value > 0 ) {
							cleared = true;
							if ( value <= cur[cell] ) {
								blob.clear( cell );
								cur[cell] -= value;
								volume -= value;
							} else {
								blob.cur[cell] -= cur[cell];
								blob.volume -= cur[cell];
								clear( cell );
							}
						}
					}

					if ( cleared && Dungeon.level.heroFOV[i] ) {
						CellEmitter.get( i ).burst( Speck.factory( Speck.DISCOVER ), 2 );
					}

					if ( cur[cell] >= 5 && Random.Float() < .05 ) {
						Dungeon.level.drop( new Dewdrop(), cell ).sprite.drop();
						cur[cell] -= 5;
						volume -= 5;
					}
				}
			}
		}
	}

	public void storm( int cell ) {
		storm( cell, new HashSet<>(), null );
	}

	private void storm( int cell, Set<Integer> exploded, Blob storm ) {
		if ( !Dungeon.level.insideMap( cell ) || cur[cell] <= 0 || exploded.contains( cell ) ) return;
		exploded.add( cell );
		if ( storm == null || storm.cur[cell] <= 0 ) {
			storm = Blob.seed( cell, cur[cell], StormGas.class );
			GameScene.add( storm );
		}
		for (int c : PathFinder.NEIGHBOURS4) {
			storm( cell + c, exploded, storm );
		}
	}

	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );

		emitter.pour( Speck.factory( Speck.DEWGAS ), 0.4f );
	}

	@Override
	public String tileDesc(int cell) {
		return Messages.get(this, "desc");
	}
}