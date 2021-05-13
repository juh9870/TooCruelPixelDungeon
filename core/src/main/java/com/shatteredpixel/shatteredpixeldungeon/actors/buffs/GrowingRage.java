package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class GrowingRage extends Buff {
    private static final String SLAIN = "slain";
    public int slain = 0;

    {
        type = buffType.NEUTRAL;
    }

    public float multiplier() {
        return 1 + (slain / 32f);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        int p = Math.round(multiplier() * 100) - 100;
        return Messages.get(this, "desc", slain, p, p * 2);
    }

    @Override
    public int icon() {
        return BuffIndicator.UPGRADE;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0x888800);
    }


    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SLAIN, slain);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        slain = bundle.getInt(SLAIN);
    }

}
