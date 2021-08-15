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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WindParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.utils.Bundle;

public class Desert extends Blob {

    private static final int LIMIT = 4;
    private static final int EXPANSION = 6;
    private static final String WITHERED = "withered";
    private boolean[] withered;

    {
        //acts before the hero, to ensure terrain is adjusted correctly
        actPriority = HERO_PRIO + 1;
    }

    public static void wither(int cell) {
        if (Dungeon.level.map[cell] == Terrain.WATER) {
            Dungeon.level.removeWater(cell);
            CellEmitter.get(cell).burst(FlameParticle.FACTORY(0x666666), 3);
        }
        if ((Dungeon.level.map[cell] == Terrain.GRASS ||
                Dungeon.level.map[cell] == Terrain.FURROWED_GRASS ||
                Dungeon.level.map[cell] == Terrain.HIGH_GRASS) &&
                !Dungeon.level.hasCustomTerrain(cell)) {
            Level.set(cell, Terrain.EMPTY);
            GameScene.updateMap(cell);
            if (Dungeon.level.heroFOV[cell]) {
                CellEmitter.get(cell).burst(FlameParticle.FACTORY(0xAA4700), 3);
            }
        }
        Plant plant = Dungeon.level.plants.get(cell);
        if (plant != null) {
            plant.wither();
        }
    }

    @Override
    protected void evolve() {

        int cell;
        boolean[] blocking = Dungeon.level.solid;

        Level l = Dungeon.level;
        for (int i = area.top - 1; i <= area.bottom; i++) {
            for (int j = area.left - 1; j <= area.right; j++) {
                cell = j + i * Dungeon.level.width();
                if (!Dungeon.level.insideMap(cell)) continue;
                if (blocking[cell]) {
                    off[cell] = 0;
                    continue;
                }
                if (cur[cell] > 0) {
                    if (cur[cell] < EXPANSION) {
                        off[cell] = cur[cell] + 1;
                    }
                    if (off[cell] >= LIMIT) {
                        if (!withered[cell]) {
                            wither(cell);
                            withered[cell] = true;
                        }
                    }
                } else {
                    boolean expand = false;
                    if (j > area.left && !blocking[cell - 1]) {
                        expand |= cur[cell - 1] >= EXPANSION;
                    }
                    if (j < area.right - 1 && !blocking[cell + 1]) {
                        expand |= cur[cell + 1] >= EXPANSION;
                    }
                    if (i > area.top && !blocking[cell - Dungeon.level.width()]) {
                        expand |= cur[cell - Dungeon.level.width()] >= EXPANSION;
                    }
                    if (i < area.bottom - 1 && !blocking[cell + Dungeon.level.width()]) {
                        expand |= cur[cell + Dungeon.level.width()] >= EXPANSION;
                    }
                    if (expand) {
                        off[cell] = 1;

                        if (i < area.top)
                            area.top = i;
                        else if (i >= area.bottom)
                            area.bottom = i + 1;
                        if (j < area.left)
                            area.left = j;
                        else if (j >= area.right)
                            area.right = j + 1;
                    }
                }

                volume += off[cell];
            }
        }
    }

    @Override
    public void seed(Level level, int cell, int amount) {
        super.seed(level, cell, amount);
        withered = new boolean[level.length()];
    }

    @Override
    public void clear(int cell) {
        super.clear(cell);
        withered[cell] = false;
    }

    @Override
    public void fullyClear() {
        super.fullyClear();
        BArray.setFalse(withered);
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);

        emitter.pour(WindParticle.FACTORY(0xAA4700), 0.1f);
    }

    @Override
    public String tileDesc(int cell) {
        return Messages.get(this, "desc");
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WITHERED, withered);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        withered = bundle.getBooleanArray(WITHERED);
    }
}
