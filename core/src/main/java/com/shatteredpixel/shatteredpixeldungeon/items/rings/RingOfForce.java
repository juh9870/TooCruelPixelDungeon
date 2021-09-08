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

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class RingOfForce extends Ring {

    private int untieredBonus;

    {
        icon = ItemSpriteSheet.Icons.RING_FORCE;
    }

    public static int armedDamageBonus(Char ch) {
        return getBuffedBonus(ch, Force.class);
    }

    private static float tier(int str) {
        float tier = Math.max(1, (str - 8) / 2f);
        //each str point after 18 is half as effective
        if (tier > 5) {
            tier = 5 + (tier - 5) / 2f;
        }
        return tier;
    }

    // *** Weapon-like properties ***

    public static int damageRoll(Hero hero) {
        if (hero.buff(Force.class) != null) {
            int level = getBuffedBonus(hero, Force.class);
            float tier = tier(hero.STR());
            if (Challenges.UNTIERED.enabled()) tier = 1;
            return Random.NormalIntRange(min(level, tier), max(level, tier));
        } else {
            //attack without any ring of force influence
            return Random.NormalIntRange(1, Math.max(hero.STR() - 8, 1));
        }
    }

    //same as equivalent tier weapon
    private static int min(int lvl, float tier) {
        return Math.max(0, Math.round(
                tier +  //base
                        lvl     //level scaling
        ));
    }

    //same as equivalent tier weapon
    private static int max(int lvl, float tier) {
        return Math.max(0, Math.round(
                5 * (tier + 1) +    //base
                        lvl * (tier + 1)    //level scaling
        ));
    }

    @Override
    protected RingBuff buff() {
        return new Force();
    }

	@Override
	public int buffedLvl() {
		if (Challenges.UNTIERED.enabled()) {
			untieredBonus = (int) tier(Dungeon.hero.STR()) - 1;
		}
		return super.buffedLvl() + untieredBonus;
	}

    @Override
    public String statsInfo() {
        float tier = tier(Dungeon.hero.STR());
        if (Challenges.UNTIERED.enabled()) tier = 1;
        if (isIdentified()) {
            int level = soloBuffedBonus();
            return Messages.get(this, "stats", min(level, tier), max(level, tier), level);
        } else {
            return Messages.get(this, "typical_stats", min(1, tier), max(1, tier), 1);
        }
    }

    public class Force extends RingBuff {
    }
}

