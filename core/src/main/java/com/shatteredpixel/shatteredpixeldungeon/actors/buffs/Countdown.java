package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Countdown extends FlavourBuff implements Hero.Doom {

    {
        type = Buff.buffType.NEUTRAL;
    }

    @Override
    public boolean act() {
        target.damage(Dungeon.depth/5+1, this);
//        BuffIndicator.refreshHero();
        spend( TICK );
        return true;
    }

    @Override
    public int icon() {
        if (cooldown()>0) {
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
        if (cooldown()>0) {
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
        Dungeon.fail( getClass() );
        GLog.n( Messages.get(this, "ondeath") );
    }
}