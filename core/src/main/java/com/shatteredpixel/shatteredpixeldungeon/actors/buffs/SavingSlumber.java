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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;

public class SavingSlumber extends Buff {

    private static final float DURATION = 50;

    private float partialHeal = 0;

    @Override
    public boolean attachTo(Char target) {
        if (!target.isImmune(Sleep.class) && super.attachTo(target)) {
            if (target instanceof Mob) {
                ((Mob) target).state = ((Mob) target).SLEEPING;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean act() {
        if (!(target instanceof Mob)) {
            detach();
            return true;
        }
        if (((Mob) target).state != ((Mob) target).SLEEPING) {
            detach();
            return true;
        }
        partialHeal += target.HT / DURATION;
        if (partialHeal > 1) {
            int heal = (int) Math.floor(partialHeal);
            target.HP = Math.min(target.HP + heal, target.HT);
            if (Dungeon.level.heroFOV[target.pos()])
                target.sprite.showStatus(CharSprite.POSITIVE, Integer.toString(heal));
            partialHeal -= heal;
        }
        if (target.HP >= target.HT) {
            detach();
            return true;
        }
        spend(TICK);
        return true;
    }

    private static final String PARTIAL = "partial_healing";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PARTIAL, partialHeal);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        partialHeal = bundle.getFloat(PARTIAL);
    }
}