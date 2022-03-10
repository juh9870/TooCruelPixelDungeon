package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Stacking extends Buff {
    private static final String COUNT = "count";
    public int count;

    public void proc() {
        int cell = target.pos();
        if (Dungeon.level.pit[cell] || !Dungeon.level.openSpace[cell]) {
            ArrayList<Integer> suitable = new ArrayList<>();
            int dist = Integer.MAX_VALUE;
            PathFinder.buildDistanceMap(cell, BArray.not(Dungeon.level.solid, null));
            int w = Dungeon.level.width();
            int tX = cell % w;
            int tY = cell / w;
            for (int x = tX - 10; x <= tX + 10; x++) {
                for (int y = tY - 10; y <= tY + 10; y++) {
                    int c = x + y * w;
                    if (!Dungeon.level.insideMap(c)) continue;
                    if (Dungeon.level.pit[c]) continue;
                    int distance = PathFinder.distance[c];
                    if (distance <= dist) {
                        if (Dungeon.level.passable[c] || Dungeon.level.avoid[c]) {
                            if (distance < dist) {
                                suitable.clear();
                                dist = distance;
                            }
                            suitable.add(c);
                        }
                    }
                }
            }
            if (suitable.size() > 0) {
                Random.shuffle(suitable);
                for (Integer c : suitable) {
                    cell = c;
                    if (!Dungeon.level.avoid[cell]) break;
                }
            }
        }

        int enemy = target instanceof Mob ? ((Mob) target).enemyPos() : -1;


        ArrayList<Class<? extends Buff>> buffs = new ArrayList<>();
        if (Challenges.STACKING_CHAMPIONS.enabled()) {
            HashSet<? extends ChampionEnemy> championBuffs = target.buffs(ChampionEnemy.class);
            for (ChampionEnemy championBuff : championBuffs) {
                buffs.add(championBuff.getClass());
            }
        }
        if (target.buff(Extermination.class) != null) {
            buffs.add(Extermination.class);
        }
        SummoningTrap.summonMobs(cell, count, 2, new StackingSpawnAction(buffs, enemy));
        detach();
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            target.sprite.emit(Stacking.class).pour(SmokeParticle.FACTORY, 0.2f);
        } else {
            target.sprite.killEmitter(Stacking.class);
        }
    }

    @Override
    public int icon() {
        return BuffIndicator.UPGRADE;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.invert();
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", count + 1);
    }

    @Override
    public void onDeathProc(Object src, boolean fakeDeath) {
        proc();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(COUNT, count);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        count = bundle.getInt(COUNT);
    }

    public static class StackingSpawnAction extends SummoningTrap.MobSpawnedAction {

        private List<Class<? extends Buff>> toAdd;
        private int beckonPos;
        public static final String TO_ADD = "toAdd";
        public static final String BECKON = "beckon";

        public StackingSpawnAction() {
            toAdd = new ArrayList<>();
        }

        public StackingSpawnAction(Collection<Class<? extends Buff>> buffs, int beckonPos) {
            this();
            toAdd.addAll(buffs);
            this.beckonPos = beckonPos;
        }

        @Override
        protected void Invoke(Mob mob) {
            for (Class<? extends Buff> cl : toAdd) {
                Buff.affect(mob, cl);
            }
            if (beckonPos >= 0) {
                mob.beckon(beckonPos);
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TO_ADD, toAdd.toArray(new Class[0]));
            bundle.put(BECKON, beckonPos);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            toAdd = new ArrayList<>();
            for (Class cl : bundle.getClassArray(TO_ADD)) {
                toAdd.add(cl);
            }

            beckonPos = bundle.getInt(BECKON);
        }
    }
}
