package com.shatteredpixel.shatteredpixeldungeon.actors.levelobjects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.LevelObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;

public class DelayedMobSpawn extends LevelObject {
    private Mob mob;

    public DelayedMobSpawn(Mob mob) {
        this.mob = mob;
    }

    @Override
    protected boolean act() {
        if (Actor.findChar(pos) == null) {
            GameScene.add(mob);
            mob.pos(pos);
            Dungeon.level.removeObject(this);
        } else {
            spend(TICK);
        }
        return true;
    }


    private static final String MOB = "mob";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(MOB, mob);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        mob = (Mob) bundle.get(MOB);
    }
}
