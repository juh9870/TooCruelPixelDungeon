package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.LevelObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public abstract class MobSummonTrap extends Trap {
    protected int maxTries = Integer.MAX_VALUE;

    public static void placeMob(Iterable<Mob> mobs) {
        //important to process the visuals and pressing of cells last, so spawned mobs have a chance to occupy cells first
        Trap t;
        for (Mob mob : mobs) {
            //manually trigger traps first to avoid sfx spam
            if ((t = Dungeon.level.getTrap(mob.pos())) != null && t.active) {
                if(t.disarmedByActivation) t.disarm();
                t.reveal();
                t.activate();
            }
            ScrollOfTeleportation.appear(mob, mob.pos());
            Dungeon.level.occupyCell(mob);
        }
    }

    protected abstract SpawnerActor getSpawner(int amount, int maxTries);

    protected void summonMobs(int amount) {
        Dungeon.level.setObject(getSpawner(amount, maxTries), pos);
    }

    public abstract static class SpawnerActor extends LevelObject {
        private static final String TRIES = "tries";
        private static final String COUNT = "spawnsLeft";
        protected ArrayList<Mob> mobsToPlace;
        private int tries;
        private int count;

        {
            //it's technically a visual effect, gets priority no matter what
            actPriority = VFX_PRIO;
        }

        public SpawnerActor() {
        }

        public SpawnerActor(int tries, int count) {
            this.tries = tries;
            this.count = count;
        }

        @Override
        protected boolean act() {
            if (tries > 0) {
                actBegin();
                while (count > 0) {
                    if (!spawnMob()) {
                        actEnd();
                        spend(TICK);
                        tries--;
                        return true;
                    }
                    count--;
                }
                actEnd();
            }
            Dungeon.level.removeObject(this);
            return true;
        }

        protected abstract boolean spawnMob();

        protected void actBegin() {
            mobsToPlace = new ArrayList<>();
        }

        protected void actEnd() {
            placeMob(mobsToPlace);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TRIES, tries);
            bundle.put(COUNT, count);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            tries = bundle.getInt(TRIES);
            count = bundle.getInt(COUNT);
        }
    }
}
