package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WindParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class RevengeRage extends Buff implements AttackAmplificationBuff {
    private int boost;

    public void add(int amount) {
        boost += amount;
        boost = Math.min(boost, 9001);
    }

    {
        type = buffType.POSITIVE;
    }

    @Override
    public Type damageFactorPriority() {
        return Type.FLAT_2;
    }

    @Override
    public float damageFactor(float dmg) {
        return dmg + boost;
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
        return Messages.get(this, "desc", boost);
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            target.sprite.emit(RevengeRage.class).pour(WindParticle.FACTORY(0xFF0000), 0.1f);

        } else {
            target.sprite.killEmitter(RevengeRage.class);
        }
    }

    private static final String BOOST = "attack_boost";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BOOST, boost);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        boost = bundle.getInt(BOOST);
    }
}
