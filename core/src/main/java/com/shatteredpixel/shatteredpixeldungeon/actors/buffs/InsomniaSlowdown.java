package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

public class InsomniaSlowdown extends TimescaleBuff {
    {
        type = buffType.NEUTRAL;
    }

    public static float DURATION = 5f;

    @Override
    public float speedFactor() {
        return 0.5f;
    }
}
