package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class Stacking extends Buff {
    private static final String COUNT = "count";
    public int count;

    public void proc() {
        int cell = target.pos;
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

        HashSet<? extends ChampionEnemy> championBuffs = target.buffs(ChampionEnemy.class);

        for (int i = 0; i < count; i++) {
            Mob mob;
            do {
                mob = Dungeon.level.createMob();
            } while (!Dungeon.level.openSpace[cell] && mob.properties().contains(Char.Property.LARGE));
            mob.pos = cell;
            if (target.buff(Extermination.class) != null) {
                Buff.affect(mob, Extermination.class);
            }
            GameScene.add(mob);
            mob.state = mob.HUNTING;
            if (enemy > 0) {
                mob.beckon(enemy);
            }
            if (Challenges.STACKING_CHAMPIONS.enabled()) {
                for (ChampionEnemy championBuff : championBuffs) {
                    Buff.affect(mob, championBuff.getClass());
                }
            }
        }
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
    public void onDeathProc(Object src) {
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
}
