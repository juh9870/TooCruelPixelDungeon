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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SummoningTrap extends Trap {

	private static final float DELAY = 2f;

	{
		color = TEAL;
		shape = WAVES;
	}

	@Override
	public void activate() {

		int nMobs = 1;
		if (Random.Int( 2 ) == 0) {
			nMobs++;
			if (Random.Int( 2 ) == 0) {
				nMobs++;
			}
		}

		

		ArrayList<Integer> candidates = getSummonCells(pos,1);
		ArrayList<Integer> respawnPoints = new ArrayList<>();

		while (nMobs > 0 && candidates.size() > 0) {
			int index = Random.index( candidates );

			respawnPoints.add( candidates.remove( index ) );
			nMobs--;
		}

		ArrayList<Mob> mobs = new ArrayList<>();

		for (Integer point : respawnPoints) {
			Mob mob = summonMob(point,DELAY);
			if(mob!=null)mobs.add(mob);
		}

		placeMob(mobs);
	}
	
	public static ArrayList<Integer> getSummonCells(int center, int maxDistance){
		ArrayList<Integer> candidates = new ArrayList<>();
		
		PathFinder.buildDistanceMap(center, BArray.or(Dungeon.level.passable,Dungeon.level.avoid,null),maxDistance);
		
		for (int p = 0; p < PathFinder.distance.length; p++) {
			if (PathFinder.distance[p]<=maxDistance && Actor.findChar( p ) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p]) && !Dungeon.level.pit[p]) {
				candidates.add( p );
			}
		}
		
		return candidates;
	}
	
	public static Mob summonMob(int point, float delay){
		Mob mob;
		do {
			mob = Dungeon.level.createMob();
		} while (Char.hasProp(mob, Char.Property.LARGE) && !Dungeon.level.openSpace[point]);
		if (mob != null) {
			mob.state = mob.WANDERING;
			mob.pos = point;
			GameScene.add(mob, delay);
		}
		return mob;
	}
	
	public static void placeMob(Iterable<Mob> mobs){
		//important to process the visuals and pressing of cells last, so spawned mobs have a chance to occupy cells first
		Trap t;
		for (Mob mob : mobs){
			//manually trigger traps first to avoid sfx spam
			if ((t = Dungeon.level.traps.get(mob.pos)) != null && t.active){
				t.disarm();
				t.reveal();
				t.activate();
			}
			ScrollOfTeleportation.appear(mob, mob.pos);
			Dungeon.level.occupyCell(mob);
		}
	}
}
