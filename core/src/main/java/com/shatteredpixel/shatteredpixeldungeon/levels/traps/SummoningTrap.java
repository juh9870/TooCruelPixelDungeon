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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SummoningTrap extends MobSummonTrap {

    public static final float DELAY = 2f;
    private int maxDistance = 1;
    private float delay = DELAY;
    private int amount = -1;
    private MobSpawnedAction action;

    {
        color = TEAL;
        shape = WAVES;
    }

    public static void summonMobs(int pos, int amount, int maxDistance) {
        if (amount == 0) return;
        summonMobs(pos, amount, maxDistance, null);
    }

    public static void summonMobs(int pos, int amount, int maxDistance, MobSpawnedAction action) {
        summonMobs(pos, amount, maxDistance, Integer.MAX_VALUE, DELAY, action);
    }

    public static void summonMobs(int pos, int amount, int maxDistance, int maxTries, float delay, MobSpawnedAction action) {
        SummoningTrap trap = new SummoningTrap();
        trap.pos = pos;
        trap.amount = amount;
        trap.maxDistance = maxDistance;
        trap.maxTries = maxTries;
        trap.delay = delay;
        trap.action = action;
        trap.activate();
    }

    public static ArrayList<Integer> getSummonCells(int center, int maxDistance) {
        ArrayList<Integer> candidates = new ArrayList<>();

        PathFinder.buildDistanceMap(center, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null), maxDistance);

        for (int p = 0; p < PathFinder.distance.length; p++) {
            if (PathFinder.distance[p] <= maxDistance && Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p]) && !Dungeon.level.pit[p]) {
                candidates.add(p);
            }
        }

        return candidates;
    }

    public static Mob summonMob(int point, float delay) {
        Mob mob;
        do {
            mob = Dungeon.level.createMob();
        } while (Char.hasProp(mob, Char.Property.LARGE) && !Dungeon.level.openSpace[point]);
        if (mob != null) {
            mob.state = mob.WANDERING;
            mob.pos(point);
            GameScene.add(mob, delay);
        }
        return mob;
    }

    @Override
    public void activate() {

        float multiplier = Challenges.nMobsMultiplier();
        int nMobs = 1;
        if (amount < 0) {
            if (Random.Int(2) == 0) {
                nMobs++;
                if (Random.Int(2) == 0) {
                    nMobs++;
                }
            }

            nMobs *= multiplier;
            if (maxTries == Integer.MAX_VALUE)
                maxTries = multiplier == 1 ? 1 : maxTries;
        } else nMobs = amount;


        summonMobs(nMobs);
    }

    public SummoningTrap maxTries(int tries) {
        maxTries = tries;
        return this;
    }

    @Override
    protected MobSummonTrap.SpawnerActor getSpawner(int amount, int maxTries) {
        return new SpawnerActor(maxTries, amount, delay, pos, maxDistance, action);
    }

    public abstract static class MobSpawnedAction implements Bundlable {
        protected abstract void Invoke(Mob mob);

        @Override
        public void storeInBundle(Bundle bundle) {
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
        }

        public static class HeroBeckoner extends MobSpawnedAction {
            @Override
            protected void Invoke(Mob mob) {
                if(Dungeon.hero.isAlive()){
                    mob.beckon(Dungeon.hero.pos());
                }
            }
        }
    }

    public static class SpawnerActor extends MobSummonTrap.SpawnerActor {
        private static final String DELAY = "delay";
        private static final String DISTANCE = "distance";
        private static final String ACTION = "action";
        private ArrayList<Integer> summonCells;
        private float delay;
        private int maxDistance;
        private MobSpawnedAction mobSpawned;

        public SpawnerActor() {
            super();
        }

        public SpawnerActor(int tries, int count, float delay, int pos, int maxDistance, MobSpawnedAction mobSpawned) {
            super(tries, count);
            this.delay = delay;
            this.mobSpawned = mobSpawned;
            this.pos = pos;
            this.maxDistance = maxDistance;
        }

        @Override
        protected boolean spawnMob() {
            if (summonCells.size() == 0) return false;
            int i = Random.index(summonCells);
            int cell = summonCells.remove(i);
            Mob mob = summonMob(cell, delay);
            if (mob == null) return false;
            mobsToPlace.add(mob);
            if (mobSpawned != null)
                mobSpawned.Invoke(mob);
            return true;
        }

        @Override
        protected void actBegin() {
            super.actBegin();
            summonCells = getSummonCells(pos, maxDistance);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DELAY, delay);
            bundle.put(DISTANCE, maxDistance);
            bundle.put(ACTION, mobSpawned);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            maxDistance = bundle.getInt(DISTANCE);
            delay = bundle.getInt(DELAY);
            mobSpawned = (MobSpawnedAction) bundle.get(ACTION);
        }
    }
}
