package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

public class MMO extends Buff implements AttackAmplificationBuff {
    private static final float SCALING_FACTOR = 1.5f;
    private static final float HERO_SCALING = 1.02f;

    public static int exp(Mob mob) {
        if (Challenges.GRINDING_3.enabled())
            return (int) (mob.EXP * (Math.pow(SCALING_FACTOR, Dungeon.depth - 1)));
        return 1;
    }

    public static float modifier() {
        if (Challenges.GRINDING_3.enabled())
            return (float) Math.pow(SCALING_FACTOR, Dungeon.depth - 1);
        return 1;
    }

    public static float skillMod() {
        return Dungeon.depth;
    }

    public static float getHeroScaling() {
        if (Challenges.GRINDING_3.enabled())
            return HERO_SCALING;
        return 1f;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            if (Challenges.GRINDING_3.enabled()) {
                target.HT *= Math.pow(SCALING_FACTOR, Dungeon.depth - 1);
                target.HP = target.HT;
            }
            return true;
        }
        return false;
    }

    @Override
    public int damageFactor(int dmg) {
        if (Challenges.GRINDING_3.enabled())
            return (int) (dmg * Math.pow(SCALING_FACTOR, Dungeon.depth - 1));
        return dmg;
    }

}
