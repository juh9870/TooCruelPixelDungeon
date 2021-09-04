package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.watabou.utils.Bundle;

public class MMO extends Buff implements AttackAmplificationBuff {
    private static final float SCALING_FACTOR = 1.5f;

    private boolean restored = false;

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

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            if (Challenges.GRINDING_3.enabled() && !restored) {
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

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        restored = true;
    }
}
