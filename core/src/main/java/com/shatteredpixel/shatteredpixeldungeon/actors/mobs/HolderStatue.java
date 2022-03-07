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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.watabou.utils.Bundle;

import java.util.Collection;
import java.util.LinkedList;

public class HolderStatue extends Statue {

    {
        spriteClass = StatueSprite.class;
    }

    public HolderStatue() {
        super();

        //reduced HP
        HP = HT = 10 + Dungeon.scalingFactor() * 3;
    }

    public Heap heap;

    public HolderStatue(MeleeWeapon wep, Heap heap) {
        this();
        weapon = wep;
        heap.copyTo(this.heap = new Heap());

    }

    @Override
    public void rollToDropLoot() {
        super.rollToDropLoot();
        if (heap != null) {
            HolderMimic.dropHeap(heap, this.pos());
            heap = null;
        }
    }

    private static final String ITEMS = "items";
    private static final String HEAP = "heap";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (heap != null) bundle.put(HEAP, heap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void restoreFromBundle(Bundle bundle) {
        if (bundle.contains(ITEMS)) {
            heap = new Heap();
            heap.items = new LinkedList<>((Collection<Item>) ((Collection<?>) bundle.getCollection(ITEMS)));
        } else if (bundle.contains(HEAP)) {
            heap = (Heap) bundle.get(HEAP);
        }
        super.restoreFromBundle(bundle);
    }
}
