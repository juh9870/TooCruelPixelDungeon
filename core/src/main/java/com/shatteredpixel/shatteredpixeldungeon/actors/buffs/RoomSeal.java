package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.HashMap;
import java.util.HashSet;

public class RoomSeal extends Buff implements Hero.Doom {
    private static final String CELLS = "cells";
    private static final float LIMIT = 5;
    private static final int ARENA_SIZE = 3;
    private static final String COOLDOWN = "cooldown";
    private final HashMap<Integer, Emitter> emitters = new HashMap<>();
    private final HashSet<Integer> cells = new HashSet<>();
    private float cooldown = LIMIT;
    private boolean fx;
    private Emitter spriteEmitter;

    {
        type = Buff.buffType.NEUTRAL;
    }

    @Override
    public boolean act() {
        if (!cells.isEmpty()) {
            if (!cells.contains(target.pos())) {
                target.damage((Statistics.deepestFloor.scalingChapter() + 1) * 3, this);
                if(target instanceof Hero){
                    ((Hero) target).interrupt();
                }
            }

            if ((cooldown -= TICK) <= 0) {
                unlock();
            }
        }
        updateEmitter();
        spend(TICK);
        return true;
    }

    private void updateEmitter() {
        if (spriteEmitter == null) return;
        if (!cells.isEmpty() && !cells.contains(target.pos())) {
            if (!spriteEmitter.on) {
                spriteEmitter.pour(FlameParticle.FACTORY(0x2277EE), 0.06f);
            }
        } else {
            spriteEmitter.on = false;
        }
    }

    public void lock(Char caller) {
        if (Dungeon.bossLevel()) return;
        cooldown = LIMIT;
        if (cells.isEmpty()) {
            PathFinder.buildDistanceMap(target.pos(), BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null), ARENA_SIZE);
            for (int i = 0; i < Dungeon.level.length(); i++) {
                if (PathFinder.distance[i] <= ARENA_SIZE) {
                    cells.add(i);
                }
            }
            BuffIndicator.refreshHero();
            showBorders();
        }

        if (!cells.isEmpty() && !cells.contains(caller.pos()) && Dungeon.level.distance(target.pos(), caller.pos()) > ARENA_SIZE) {
            int length = cells.size();
            for (int i : PathFinder.NEIGHBOURS9) {
                int c = caller.pos() + i;
                cells.add(c);
            }
            if (length != cells.size()) {
                showBorders();
            }
        }
    }

    public void unlock() {
        if (!cells.isEmpty()) {
            cells.clear();
            BuffIndicator.refreshHero();
            clearBorders();
        }
    }

    private void showBorders() {
        clearBorders();
        if (fx && !cells.isEmpty()) {
            for (Integer cell : cells) {
                for (int i : PathFinder.NEIGHBOURS8) {
                    int c = cell + i;
                    if (Dungeon.level.insideMap(c) && !cells.contains(c)) {
                        emit(c);
                    }
                }
            }
        }
    }

    private void emit(int c) {
        if (!Dungeon.level.passable[c] && !Dungeon.level.avoid[c]) return;
        if (emitters.containsKey(c)) return;
        Emitter e = CellEmitter.get(c);
        e.pour(FlameParticle.FACTORY(0x2277EE), 0.02f);
        emitters.put(c, e);
    }

    private void clearBorders() {
        for (Emitter emitter : emitters.values()) {
            emitter.on = false;
        }
        emitters.clear();
    }

    @Override
    public void fx(boolean on) {
        fx = on;
        if (fx) {
            showBorders();
            spriteEmitter = target.sprite.emit(getClass());
            spriteEmitter.autoKill = false;
        } else {
            clearBorders();
            target.sprite.killEmitter(getClass());
            spriteEmitter = null;
        }
    }

    @Override
    public float iconFadePercent() {
        return Math.min(0, (LIMIT - cooldown) / LIMIT);
    }

    @Override
    public int icon() {
        if (!cells.isEmpty()) {
            return BuffIndicator.LOCKED_FLOOR;
        } else {
            return BuffIndicator.NONE;
        }
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0, 1, 1);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }

    @Override
    public void onDeath() {
        Dungeon.fail(getClass());
        GLog.n(Messages.get(this, "ondeath"));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        int[] cells = new int[this.cells.size()];
        int i = 0;
        for (Integer cell : this.cells) {
            cells[i++] = cell;
        }
        bundle.put(CELLS, cells);
        bundle.put(COOLDOWN, cooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        int[] targetCells = bundle.getIntArray(CELLS);
        for (int cell : targetCells) {
            cells.add(cell);
        }
        cooldown = bundle.getInt(COOLDOWN);
    }
}
