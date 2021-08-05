package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;

import java.util.ArrayList;

public abstract class MobSummonTrap extends Trap {
    protected int maxTries = Integer.MAX_VALUE;

    protected abstract SpawnerActor getSpawner(int amount, int maxTries);

    protected void summonMobs(int amount) {
        Actor.add(getSpawner(amount,maxTries));
    }

    public static void placeMob(Iterable<Mob> mobs){
        //important to process the visuals and pressing of cells last, so spawned mobs have a chance to occupy cells first
        Trap t;
        for (Mob mob : mobs){
            //manually trigger traps first to avoid sfx spam
            if ((t = Dungeon.level.traps.get(mob.pos)) != null && t.active){
                t.disarm();
                t.reveal();
                t.activate();
            }
            ScrollOfTeleportation.appear(mob, mob.pos);
            Dungeon.level.occupyCell(mob);
        }
    }

    protected abstract static class SpawnerActor extends Actor{
        private int tries;
        private int count;
        protected ArrayList<Mob> mobsToPlace;

        public SpawnerActor(int tries, int count) {
            this.tries = tries;
            this.count = count;
        }

        {
            //it's technically a visual effect, gets priority no matter what
            actPriority = VFX_PRIO;
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
            Actor.remove(this);
            return true;
        }

        protected abstract boolean spawnMob();

        protected void actBegin() {
            mobsToPlace = new ArrayList<>();
        }
        protected void actEnd() {
            placeMob(mobsToPlace);
        }
    }
}
