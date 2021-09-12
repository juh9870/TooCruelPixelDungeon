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
        if (Challenges.GRINDING_3.enabled())
            return Dungeon.depth;
        return 1;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            if (Challenges.GRINDING_3.enabled() && !restored) {
                float mult = (float) Math.pow(SCALING_FACTOR, Dungeon.depth - 1);
                target.HT *= mult;
                target.HP *= mult;
            }
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        //This buff can't be detached
    }

    @Override
    public float damageFactor(float dmg) {
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
