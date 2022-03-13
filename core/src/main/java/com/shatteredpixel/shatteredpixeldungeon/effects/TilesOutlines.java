package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TilesOutlines extends Group {

	private static PointF[] points( int cell ) {
		PointF first = DungeonTilemap.tileToWorld( cell );
		return new PointF[]{
				first,
				first.clone().offset( DungeonTilemap.SIZE, 0 ),
				first.clone().offset( 0, DungeonTilemap.SIZE ),
				first.clone().offset( DungeonTilemap.SIZE, DungeonTilemap.SIZE ),
		};
	}

	private static PointF[] neighbours( PointF point ) {
		return new PointF[]{
				point.clone().offset( DungeonTilemap.SIZE, 0 ),
				point.clone().offset( -DungeonTilemap.SIZE, 0 ),
				point.clone().offset( 0, DungeonTilemap.SIZE ),
				point.clone().offset( 0, -DungeonTilemap.SIZE ),
		};
	}

	private float duration;
	private float elapsed;
	private int steps = 0;
	private final Set<Line> lines = new HashSet<>();

	public void show( Set<Integer> cells, int origin, int color, float duration, boolean durationPerStep ) {
		if ( duration == 0 ) return;
		this.duration = duration;
		this.elapsed = 0;
		steps = 0;
		this.lines.clear();

		Set<PointF> points = new HashSet<>();
		Set<PointF> visiblePoints = new HashSet<>();
		for (Integer cell : cells) {
			points.addAll( Arrays.asList( points( cell ) ) );
			if ( Dungeon.level.heroFOV[cell] ) {
				visiblePoints.addAll( Arrays.asList( points( cell ) ) );
			}
		}

		List<PointF> curSet = new ArrayList<>();
		List<PointF> nextSet = new ArrayList<>();

		curSet.add( Random.element( points( origin ) ) );

		revive();
		do {
			points.removeAll( curSet );
			Random.shuffle( curSet );
			for (PointF from : curSet) {
				for (PointF to : neighbours( from )) {
					if ( !points.contains( to ) ) continue;
					nextSet.add( to );
					if ( !visiblePoints.containsAll( Arrays.asList( from, to ) ) ) continue;
					ColorBlock block = (ColorBlock) recycle( ColorBlock.class );
					block.color( color );
					block.size( 1f, 1f );
					block.alpha( (color >>> 24) / (float) 0xFF );
					block.revive();
					block.visible = false;
					lines.add( new Line( block, from, to, steps ) );
				}
			}
			if ( nextSet.isEmpty() && !points.isEmpty() ) throw new RuntimeException( "Disjointed cells groups" );
			curSet = nextSet;
			nextSet = new ArrayList<>();
			steps++;
		} while (!points.isEmpty());
		if ( durationPerStep ) this.duration *= steps;
	}

	@Override
	public synchronized void update() {
		super.update();

		for (Line line : lines) {
			line.update( elapsed );
		}
		elapsed += Game.elapsed;
		if ( elapsed > duration ) {
			kill();
		}
	}

	private class Line {
		private final ColorBlock target;
		private final PointF from;
		private final PointF to;
		private final int step;
		private final float size;
		private final float angle;
		private boolean dead = false;

		public Line( ColorBlock target, PointF from, PointF to, int step ) {
			this.target = target;
			this.from = from;
			this.to = to;
			this.step = step;
			size = PointF.distance( from, to );
			angle = (int) (PointF.angle( from, to ) / Math.PI * 180);
		}

		private void update( float elapsed ) {
			if ( dead ) return;
			float delay = TilesOutlines.this.duration * step / steps;
			float duration = TilesOutlines.this.duration / steps * 2;

			float progress = (elapsed - delay) / duration;
			if ( progress < 0 ) {
				target.visible = false;
			} else if ( progress < 0.5 ) {
				target.visible = true;
				target.x = from.x;
				target.y = from.y;
				target.size( size * progress * 2, target.height() );
				target.origin.set( 0, 0.5f );
				target.angle = angle;
			} else if ( progress < 1 ) {
				target.x = to.x;
				target.y = to.y;
				target.size( size * (1 - (progress - 0.5f) * 2), target.height() );
				target.origin.set( 0, 0.5f );
				target.angle = angle + 180;
			} else if ( progress > 1 ) {
				target.kill();
				dead = true;
			}
		}
	}
}
