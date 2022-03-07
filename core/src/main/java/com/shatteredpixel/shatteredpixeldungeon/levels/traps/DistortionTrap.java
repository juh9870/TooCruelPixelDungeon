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

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Acidic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Albino;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.ArmoredBrute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bandit;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CausticSlime;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM201;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Senior;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks.Chapter;
import com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks.Marker;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;

public class DistortionTrap extends MobSummonTrap{

	private static final float DELAY = 2f;

	{
		color = TEAL;
		shape = LARGE_DOT;
	}

	private static final ArrayList<Class<?extends Mob>> RARE = new ArrayList<>(Arrays.asList(
			Albino.class, CausticSlime.class,
			Bandit.class,
			ArmoredBrute.class, DM201.class,
			Elemental.ChaosElemental.class, Senior.class,
			Acidic.class));

	@Override
	public void activate() {

		int nMobs = 3;
		if (Random.Int( 2 ) == 0) {
			nMobs++;
			if (Random.Int( 2 ) == 0) {
				nMobs++;
			}
		}

		nMobs *= Math.sqrt(Challenges.nMobsMultiplier());

		summonMobs(nMobs);

	}

	@Override
	protected MobSummonTrap.SpawnerActor getSpawner(int amount, int maxTries) {
		return new SpawnerActor(maxTries,amount,pos);
	}

	public static class SpawnerActor extends MobSummonTrap.SpawnerActor{
		private ArrayList<Integer> summonCells;
		private int summoned = 0;
		public SpawnerActor(){
			super();
		}
		public SpawnerActor(int tries, int count, int pos) {
			super(tries, count);
			this.pos = pos;
		}

		@Override
		protected void actBegin() {
			super.actBegin();
			summonCells = SummoningTrap.getSummonCells(pos, 1);
		}

		@Override
		protected boolean spawnMob() {
			if (summonCells.size() == 0) return false;
			int index = Random.index(summonCells);
			int point = summonCells.remove(index);
			summoned++;
			Mob mob;
			switch (summoned){
				case 1:
					Marker m = Dungeon.depth();
					if ((m.chapter() != Chapter.SEWERS || m.chapterProgression() != 5) && Random.Int(100) == 0) {
						mob = new RatKing();
						break;
					}
				case 3: case 5 : default:
					Marker floor;
					do {
						floor = Dungeon.levelPack.randomFloor();
					} while( Dungeon.bossLevel(floor));
					mob = Reflection.newInstance(Bestiary.getMobRotation(floor).get(0));
					break;
				case 2:
					switch (Random.Int(4)){
						case 0: default:
							Wraith.spawnAt(point);
							return true; //wraiths spawn themselves, no need to do more
						case 1:
							//yes it's intended that these are likely to die right away
							mob = new Piranha();
							break;
						case 2:
							mob = Mimic.spawnAt(point, new ArrayList<>());
							((Mimic)mob).stopHiding();
							mob.alignment = Char.Alignment.ENEMY;
							break;
						case 3:
							mob = Statue.random();
							break;
					}
					break;
				case 4:
					mob = Reflection.newInstance(Random.element(RARE));
					break;
			}

			mob.maxLvl = Hero.MAX_LEVEL;
			mob.state = mob.WANDERING;
			mob.pos(point);
			GameScene.add(mob, DELAY);
			mobsToPlace.add(mob);
			return true;
		}

		private static final String SUMMONED = "summoned";
		private static final String POS = "pos";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(SUMMONED,summoned);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			summoned = bundle.getInt(SUMMONED);
		}
	}
}
