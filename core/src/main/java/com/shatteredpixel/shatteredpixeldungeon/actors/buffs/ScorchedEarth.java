package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Desert;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class ScorchedEarth extends Buff {
    private static final float WATER_TIME = 20;
    private static final String LEFT = "turns_left";
    private float turnsLeft = maxTime();


    private static float maxTime(){
        return Challenges.DESERT.enabled() ? WATER_TIME * 1.5f : WATER_TIME;
    }
    @Override
    public boolean act() {
        if (Dungeon.level.water[target.pos]) {
            turnsLeft = maxTime();
        } else {
            if (turnsLeft <= 0) {
                turnsLeft += maxTime() / 4;
                for (int i : PathFinder.NEIGHBOURS4) {
                    int c = target.pos + i;
                    if (Dungeon.level.water[c]) {
                        Dungeon.level.removeWater(c);
                    }
                    Buff.affect(target, Burning.class).reignite(target);
                    GameScene.add(Blob.seed(c, 4, Fire.class));
                }
            }
            turnsLeft -= TICK;
        }

        if (Challenges.DESERT.enabled()) {
            GameScene.add(Blob.seed(target.pos, 1, Desert.class));
        }
        spend(TICK);
        return true;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", Math.ceil(turnsLeft));
    }

    @Override
    public int icon() {
        return BuffIndicator.THERMOMETER;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, turnsLeft);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (maxTime() - turnsLeft) / maxTime());
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        turnsLeft = bundle.getFloat(LEFT);
    }
}
