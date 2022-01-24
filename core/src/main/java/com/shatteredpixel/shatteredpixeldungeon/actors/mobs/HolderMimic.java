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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.NoReward;
import com.shatteredpixel.shatteredpixeldungeon.actors.levelobjects.DelayedMobSpawn;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.DummyItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.PokerToken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.BlackjackRoom;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.Currency;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.Collections;
import java.util.HashSet;

public class HolderMimic extends Mimic {
    {
        spriteClass = MimicSprite.class;
    }

    public Heap heap;

    @Override
    public void setLevel(int level) {
        float mult = 0.75f;
        if (Challenges.MIMICS_GRIND.enabled()) mult = 1.33f;
        super.setLevel(Math.round(level * mult));
    }

    private static final String HEAP = "heap";

    @Override
    protected void generatePrize() {
        if (Challenges.MIMICS_GRIND.enabled()) super.generatePrize();
        // No additional reward
    }

    @Override
    public void rollToDropLoot() {
        super.rollToDropLoot();
        HolderMimic.dropHeap(heap, this.pos());
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (heap != null) bundle.put(HEAP, heap);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        if (bundle.contains(HEAP)) {
            heap = (Heap) bundle.get(HEAP);
        }
        super.restoreFromBundle(bundle);
    }

    public static void spawnAt(int pos, Level level, Heap heap) {
        MeleeWeapon wep = null;
        boolean hasTokens = false;
        for (Item item : heap.items) {
            if (item instanceof MeleeWeapon && wep == null) {
                wep = (MeleeWeapon) item;
            }
            if (item instanceof PokerToken) {
                hasTokens = true;
            }
        }
        BlackjackRoom room;
        if (wep == null && Challenges.BLACKJACK.enabled() && hasTokens
                && (room = level.getBlackjackRoom()) != null && room.sellItems.size() > 0) {
            Item item = Random.element(room.sellItems);
            if (item instanceof MeleeWeapon) wep = (MeleeWeapon) item;
        }
        Mob mob;
        if (wep != null && Challenges.MIMICS_2.enabled()) {
            mob = new HolderStatue((MeleeWeapon) wep.clone(), heap);
            mob.pos(pos);
            Buff.affect(mob, NoReward.class);
        } else {
            mob = spawnAt(pos, Collections.EMPTY_LIST, HolderMimic.class);
            heap.copyTo(((HolderMimic) mob).heap = new Heap());
        }
        if (Actor.findChar(pos) == null) {
            level.addMob(mob);
            if (ShatteredPixelDungeon.scene() instanceof GameScene) {
                GameScene.add(mob);
            }
        } else {
            level.setObject(new DelayedMobSpawn(mob), pos);
        }
    }

    public static void dropHeap(Heap heap, int pos) {
        if (heap == null) return;
        if (Dungeon.level.heaps.containsKey(pos) ||
                Dungeon.level.pit[pos] ||
                !(Dungeon.level.passable[pos] || Dungeon.level.avoid[pos])
        ) {
            PathFinder.buildDistanceMap(pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));
            HashSet<Integer> validCells = new HashSet<>();
            int minDistance = Integer.MAX_VALUE;
            Mob m;
            for (int i = 0; i < PathFinder.distance.length; i++) {
                int dist = PathFinder.distance[i];
                if (dist <= minDistance) {
                    if (Dungeon.level.heaps.containsKey(i) || !(Dungeon.level.passable[i] || Dungeon.level.avoid[i]) || Dungeon.level.pit[i]) {
                        continue;
                    }
                    if ((m = Dungeon.level.findMob(i)) != null && (m.properties().contains(Property.IMMOVABLE) || m instanceof NPC)) {
                        continue;
                    }
                    if (dist == minDistance) {
                        validCells.add(i);
                    } else {
                        validCells.clear();
                        validCells.add(i);
                        minDistance = dist;
                    }
                }
            }
            if (validCells.size() > 0) {
                pos = Random.element(validCells);
            } else {
                for (Item item : heap.items) {
                    Dungeon.level.drop(item, pos);
                }
                return;
            }
        }

        Heap h = Dungeon.level.drop(new DummyItem(), pos);
        heap.copyTo(h);
        h.sprite.link();
        h.sprite.drop();
    }

    public static void clearForSaleHeaps(Currency currency) {
        for (Mob mob : Dungeon.level.mobs()) {
            if (mob instanceof HolderMimic &&
                    ((HolderMimic) mob).heap.type == Heap.Type.FOR_SALE &&
                    ((HolderMimic) mob).heap.currency == currency) {
                ((HolderMimic) mob).heap = null;
                ((HolderMimic) mob).wakeup();
            }
            if (mob instanceof HolderStatue &&
                    ((HolderStatue) mob).heap.type == Heap.Type.FOR_SALE &&
                    ((HolderStatue) mob).heap.currency == currency) {
                ((HolderStatue) mob).heap = null;
                ((HolderStatue) mob).wakeup();
            }
        }
    }
}
