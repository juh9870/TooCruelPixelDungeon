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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.NoReward;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class PoolRoom extends SpecialRoom {

	private static final int NPIRANHAS	= 3;
	
	@Override
	public int minWidth() {
		return (int)(6 + /*Math.sqrt*/(Challenges.roomSizeMult()));
	}
	
	@Override
	public int minHeight() {
		return (int)(6 + /*Math.sqrt*/(Challenges.roomSizeMult()));
	}
	
	public void paint(Level level ) {
		
		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.WATER );
		
		Door door = entrance();
		door.set( Door.Type.REGULAR );

		int x = -1;
		int y = -1;
		if (door.x == left) {
			
			x = right - 1;
			y = top + height() / 2;
			Painter.fill(level, left+1, top+1, 1, height()-2, Terrain.EMPTY_SP);
			
		} else if (door.x == right) {
			
			x = left + 1;
			y = top + height() / 2;
			Painter.fill(level, right-1, top+1, 1, height()-2, Terrain.EMPTY_SP);
			
		} else if (door.y == top) {
			
			x = left + width() / 2;
			y = bottom - 1;
			Painter.fill(level, left+1, top+1, width()-2, 1, Terrain.EMPTY_SP);
			
		} else if (door.y == bottom) {
			
			x = left + width() / 2;
			y = top + 1;
			Painter.fill(level, left+1, bottom-1, width()-2, 1, Terrain.EMPTY_SP);
			
		}
		
		int pos = x + y * level.width();
		level.drop( prize( level ), pos ).type = Heap.Type.CHEST;
		Painter.set( level, pos, Terrain.PEDESTAL );
		
		level.addItemToSpawn( new PotionOfInvisibility() );

		int nFish = NPIRANHAS;
		float mult = Challenges.nMobsMultiplier();
		nFish = (int) Math.max(nFish * Math.sqrt(mult), nFish + mult - 1);

		ArrayList<Mob> piranhas = new ArrayList<>();

		if (nFish > 10) {
			HashSet<Integer> path = new HashSet<>(new Ballistica(level.pointToCell(door), pos, Ballistica.STOP_TARGET, level).path);
			ArrayList<Point> points = points(1);
			Random.shuffle(points);
			for (int i = (int) (points.size() * 0.1f); i < points.size() && nFish > 0; i++) {
				int cell = level.pointToCell(points.get(i));
				if (level.map[cell] != Terrain.WATER || level.findMob(cell) != null || path.contains(cell)) continue;

				Piranha piranha = new Piranha();
				piranha.pos(cell);
				level.addMob(piranha);
				piranhas.add(piranha);
				nFish--;
			}
		} else {
			for (int i = 0; i < nFish; i++) {
				Piranha piranha = new Piranha();
				int _pos = -1;
				do {
					_pos = level.pointToCell(random());
				} while (level.map[_pos] != Terrain.WATER || level.findMob(_pos) != null);
				piranha.pos(_pos);
				level.addMob(piranha);
				piranhas.add(piranha);
			}
		}
		unlootMobs(piranhas,3);
	}

	public static void unlootMobs(ArrayList<Mob> piranhas, int count){
		if (piranhas.size() <= count) {
			return;
		}
		Random.shuffle(piranhas);
		for (int i = 0; i < count; i++) {
			piranhas.remove(0);
		}
		for (Mob pirahna : piranhas) {
			Buff.affect(pirahna, NoReward.class);
		}
	}
	
	private static Item prize( Level level ) {

		Item prize;

		if (Random.Int(3) == 0){
			prize = level.findPrizeItem();
			if (prize != null)
				return prize;
		}

		//1 floor set higher in probability, never cursed
		do {
			if (Random.Int(2) == 0) {
				prize = Generator.randomWeapon((Dungeon.scalingChapter()) + 1);
			} else {
				prize = Generator.randomArmor((Dungeon.scalingChapter()) + 1);
			}
		} while ((prize.cursed&&!Challenges.CURSED.enabled()) || Challenges.isItemBlocked(prize));
		prize.cursedKnown = !Challenges.CURSED.enabled();
		
		//33% chance for an extra update.
		if (Random.Int(3) == 0){
			prize.upgrade();
		}

		return prize;
	}
}
