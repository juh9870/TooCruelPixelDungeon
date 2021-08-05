package com.shatteredpixel.shatteredpixeldungeon.actors;

import com.watabou.utils.Bundle;

public class LevelObject extends Actor {
    public int pos;

    @Override
    protected boolean act() {
        diactivate();
        return true;
    }

    private static final String POS = "pos";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(POS, pos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        pos = bundle.getInt(POS);
    }
}
