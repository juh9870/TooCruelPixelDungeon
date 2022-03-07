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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class StatueRoom extends SpecialRoom {

	public void paint( Level level ) {

		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY );

		Point c = center();
		int cx = c.x;
		int cy = c.y;
		
		Door door = entrance();
		
		door.set( Door.Type.LOCKED );
		level.addItemToSpawn( new IronKey( Dungeon.depth() ) );
		
		if (door.x == left) {
			
			Painter.fill( level, right - 1, top + 1, 1, height() - 2 , Terrain.STATUE );
			cx = right - 2;
			
		} else if (door.x == right) {
			
			Painter.fill( level, left + 1, top + 1, 1, height() - 2 , Terrain.STATUE );
			cx = left + 2;
			
		} else if (door.y == top) {
			
			Painter.fill( level, left + 1, bottom - 1, width() - 2, 1 , Terrain.STATUE );
			cy = bottom - 2;
			
		} else if (door.y == bottom) {
			
			Painter.fill( level, left + 1, top + 1, width() - 2, 1 , Terrain.STATUE );
			cy = top + 2;
			
		}

		int nStatues = 1;
		float mult = Challenges.nMobsMultiplier();
		nStatues = (int) Math.max(nStatues * Math.sqrt(mult), nStatues + mult - 1);

		if (nStatues > 1) {
			ArrayList<Mob> statues = new ArrayList<>();
			// 1 drop per 8 statues in a room
			int dropStatues = (int) Math.ceil(nStatues / 8f);
			// Offset target center a bit to randomize statues distribution around the main statue
			final float fcx = cx + Random.Float()/10 - 0.05f;
			final float fcy = cy + Random.Float()/10 - 0.05f;
			Point[] points = points(1).toArray(new Point[0]);
			Arrays.sort(points,(a,b)-> {
				float adx=fcx-a.x;
				float ady=fcy-a.y;
				float bdx=fcx-b.x;
				float bdy=fcy-b.y;
				return (int) Math.signum(adx * adx + ady * ady - (bdx * bdx + bdy * bdy));
			});
			for (int i = 0; i < points.length && nStatues > 0; i++) {
				int cell = level.pointToCell(points[i]);

				if ((Terrain.flags[level.map[cell]] & Terrain.PASSABLE) == 0) continue;
				Statue statue = Statue.random();
				statue.pos(cell);
				level.addMob(statue);
				statues.add(statue);
				nStatues--;
			}
			PoolRoom.unlootMobs(statues, dropStatues);
		} else {
			Statue statue = Statue.random();
			statue.pos(cx + cy * level.width());
			level.addMob(statue);
		}
	}
}
