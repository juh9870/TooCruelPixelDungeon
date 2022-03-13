package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class RevengeFury extends FlavourBuff implements AttackAmplificationBuff, DamageAmplificationBuff {

    @Override
    public float damageFactor(float dmg) {
        return dmg * 2;
    }

    @Override
    public int icon() {
        return BuffIndicator.RAGE;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

    @Override
    public float damageMultiplier( Object source ) {
        if (target.properties().contains(Char.Property.BOSS)) return 1f;
        return 1.2f;
    }
}
