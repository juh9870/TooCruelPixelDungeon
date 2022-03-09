package com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Countdown;
import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.function.Lazy;

import java.util.ArrayList;
import java.util.List;

public abstract class LinearLevelPack<T extends Marker.Linear> extends LevelPack<T> {
    private static final String LEVELS = "levels";
    private List<T> levels = null;
    private final Lazy<Marker> amuletFloor = Lazy.of(() -> {
        for (T level : levels) {
            if (levelClassForMarker(level).equals(LastLevel.class)) return level;
        }
        throw new RuntimeException("No goal found!");
    });

    @Override
    public void init() {
        super.init();
        levels = build();
        curLvl = levels.get(0);
    }

    @Override
    public T nextLevel(T curLevel) {
        return get(curLevel.id() + 1);
    }

    @Override
    public T prevLevel(T curLevel) {
        return get(curLevel.id() - 1);
    }

    @Override
    public Level generateNextLevel() {
        curLvl = nextLevel();
        Marker newDeepestFloor = null;
        if (curLvl.compareTo(Statistics.deepestFloor) > 0) {
            if (Challenges.COUNTDOWN.enabled()) {
                Buff.prolong(Dungeon.hero, Countdown.class, Countdown.DESCEND_TIME * Countdown.timeMultiplier());
            }
            newDeepestFloor = curLvl;

            Statistics.completedWithNoKilling = Statistics.qualifiedForNoKilling;
        } else if (Statistics.amuletObtained && curLvl.compareTo(Statistics.amuletHighestFloor) < 0) {
            Statistics.amuletHighestFloor = curLvl;
            if (Challenges.COUNTDOWN.enabled()) {
                Buff.prolong(Dungeon.hero, Countdown.class, Countdown.ASCEND_TIME * Countdown.timeMultiplier());
            }
        }

        Level level = levelForMarker(curLvl);
        if (!(level instanceof DeadEndLevel)) Statistics.deepestFloor = newDeepestFloor;
        return level;
    }

    @Override
    public String levelFileName(T marker) {
        return Integer.toString(marker.id());
    }

    protected abstract List<T> build();

    @Override
    public long seedForMarker(T marker) {
        Random.pushGenerator(Dungeon.seed);

        for (int i = 0; i < marker.id(); i++) {
            Random.Long(); //we don't care about these values, just need to go through them
        }
        long result = Random.Long();

        Random.popGenerator();
        return result;
    }

    protected T get(int id) {
        if (id < 0 || id >= levels.size()) return dummyLevelForId(id);
        return levels.get(id);
    }

    protected abstract T dummyLevelForId(int id);

    @Override
    public T previousShopFloor() {
        T cur = curLvl;
        while (!cur.shop()) {
            cur = prevLevel(cur);
            if (cur.id() == 1) return cur;
        }
        return cur;
    }

    @Override
    public T nextBossFloor() {
        T cur = curLvl;
        while (!cur.boss()) {
            cur = nextLevel(cur);
            if (cur.chapter() == Chapter.EMPTY) return null;
        }
        return cur;
    }

    @Override
    public T cursedTeleportBack() {
        //each depth has 1 more weight than the previous depth.
        float[] depths = new float[curLvl.id() - 1];
        for (int i = 1; i < curLvl.id(); i++) depths[i - 1] = i;
        int depth = 1 + Random.chances(depths);
        return get(depth);
    }

    @Override
    public T firstLevel() {
        return levels.get(1);
    }

    @Override
    public boolean visitedNextLevel(T marker) {
        return Statistics.deepestFloor.compareTo(marker) > 0;
    }

    @Override
    public Iterable<T> levels() {
        return levels;
    }

    @Override
    public T randomFloor() {
        return Random.element(levels);
    }

    @Override
    public Marker amuletFloor() {
        return amuletFloor.get();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVELS, levels);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        levels = new ArrayList<>();
        for (Bundlable element : bundle.getCollection(LEVELS)) {
            levels.add((T) element);
        }
    }
}
