package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class Countdown extends FlavourBuff implements Hero.Doom {

    public static final float DESCEND_TIME = 300;
    public static final float ASCEND_TIME = 150;

    {
        type = Buff.buffType.NEUTRAL;
    }

    public static float timeMultiplier() {
        float mult = 1.0f;
        if (Challenges.BIG_LEVELS.enabled()) mult *= 2;
        if (Challenges.BIGGER_LEVELS.enabled()) mult *= 2;
        if (Challenges.HUGE_LEVELS.enabled()) mult *= 2;
        if (Challenges.SMALL_LEVELS.enabled()) mult /= 2;
        return mult;
    }

    @Override
    public boolean act() {
        target.damage((Statistics.deepestFloor.scalingChapter() + 1) * 3, this);
//        BuffIndicator.refreshHero();
        spend(TICK);
        return true;
    }

    @Override
    public float iconFadePercent() {
        float p = Math.max(0, (DESCEND_TIME / 2 - visualcooldown()) / (DESCEND_TIME / 2));
        if (Math.abs(visualcooldown() - 1) <= 1e-4) {
            p = 0;
        }
        return p;
    }

    @Override
    public int icon() {
        if (cooldown() > 0) {
            return BuffIndicator.COUNTDOWN1;
        } else {
            return BuffIndicator.COUNTDOWN2;
        }
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        if (cooldown() > 0) {
            return Messages.get(this, "desc", dispTurns());
        } else {
            return Messages.get(this, "descdeadly");
        }
    }

    @Override
    public void detach() {
        //This buff can't be detached
    }

    @Override
    public void onDeath() {
        Dungeon.fail(getClass());
        GLog.n(Messages.get(this, "ondeath"));
    }
}