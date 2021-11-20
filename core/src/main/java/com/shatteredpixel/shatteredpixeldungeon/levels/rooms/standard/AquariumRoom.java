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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PoolRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AquariumRoom extends StandardRoom {
	
	@Override
	public int minWidth() {
		return Math.max(super.minWidth(), 7);
	}
	
	@Override
	public int minHeight() {
		return Math.max(super.minHeight(), 7);
	}
	
	@Override
	public float[] sizeCatProbs() {
		return new float[]{3, 1, 0};
	}
	
	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY );
		Painter.fill( level, this, 2, Terrain.EMPTY_SP );
		Painter.fill( level, this, 3, Terrain.WATER );
		
		int minDim = Math.min(width(), height());
		int numFish = (minDim - 4)/3; //1-3 fish, depending on room size
		float mult = Challenges.nMobsMultiplier();
		numFish = (int) Math.max(numFish * Math.sqrt(mult), numFish + mult - 1);
		ArrayList<Point> points = points(3);
		ArrayList<Mob> piranhas = new ArrayList<>();
		Random.shuffle(points);
		for (int i = 0; i < points.size() && numFish > 0; i++) {
			int cell = level.pointToCell(points.get(i));
			if (level.map[cell] != Terrain.WATER || level.findMob(cell) != null) continue;

			Piranha piranha = new Piranha();
			piranha.pos(cell);
			level.addMob(piranha);
			piranhas.add(piranha);
			numFish--;
		}

		PoolRoom.unlootMobs(piranhas, 3);

		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}
	}
	
}
