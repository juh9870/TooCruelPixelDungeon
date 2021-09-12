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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.NoReward;
import com.shatteredpixel.shatteredpixeldungeon.actors.levelobjects.DelayedMobSpawn;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.PokerToken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.BlackjackRoom;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MimicSprite;
import com.watabou.utils.Random;

import java.util.List;

public class HolderMimic extends Mimic {
    {
        spriteClass = MimicSprite.class;
    }

    @Override
    public void setLevel(int level) {
        float mult = 0.75f;
        if(Challenges.MIMICS_GRIND.enabled())mult = 1.33f;
        super.setLevel(Math.round(level * mult));
    }

    @Override
    protected void generatePrize() {
        if (Challenges.MIMICS_GRIND.enabled()) super.generatePrize();
        // No additional reward
    }

    public static void spawnAt(int pos, List<Item> items, Level level) {
        MeleeWeapon wep = null;
        boolean hasTokens = false;
        for (Item item : items) {
            if (item instanceof MeleeWeapon && wep==null) {
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
            mob = new HolderStatue((MeleeWeapon) wep.clone(), items);
            mob.pos(pos);
            Buff.affect(mob, NoReward.class);
        } else {
            mob = spawnAt(pos, items, HolderMimic.class);
        }
        if (Actor.findChar(pos) == null) {
            level.addMob(mob);
        } else {
            level.setObject(new DelayedMobSpawn(mob), pos);
        }
    }
}
