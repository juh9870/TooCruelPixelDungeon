package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class Arrowhead extends Buff implements DamageAmplificationBuff, AttackAmplificationBuff {
    private static final String STACKS = "arrowhead_stacks";
    private static final float COOLDOWN = 20;
    private int stacks;

    {
        type = buffType.POSITIVE;
    }

    @Override
    public boolean act() {
        stacks--;
        if (stacks <= 0) {
            detach();
            return true;
        }
        spend(COOLDOWN);
        return true;
    }

    @Override
    public int icon() {
        return BuffIndicator.FURY;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", stacks, Math.round(stacks * 100 * 0.3f), Math.round(stacks * 100 * 0.1f));
    }

    @Override
    public float damageMultiplier() {
        return 1 + stacks * 0.3f;
    }

    @Override
    public int damageFactor(int dmg) {
        return (int) (dmg * (1 + stacks * 0.1f));
    }

    public void addStack() {
        stacks++;
        postpone(COOLDOWN);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STACKS, stacks);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        stacks = bundle.getInt(STACKS);
    }
}
