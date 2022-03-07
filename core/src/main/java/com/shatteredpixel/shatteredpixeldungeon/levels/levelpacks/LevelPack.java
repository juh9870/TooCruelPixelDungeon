package com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks;

import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.Collection;
import java.util.Collections;

import jdk.vm.ci.code.site.Mark;

public abstract class LevelPack<T extends Marker> implements Bundlable {
    public T curLvl;

    public void init() {

    }

    public Level levelForMarker(T marker) {
        if (marker.equals(amuletFloor())) return new LastLevel();
        boolean boss = bossLevel(curLvl);
        switch (curLvl.chapter()) {
            case SEWERS:
                return boss ? new SewerBossLevel() : new SewerLevel();
            case PRISON:
                return boss ? new PrisonBossLevel() : new PrisonLevel();
            case CAVES:
                return boss ? new CavesBossLevel() : new CavesLevel();
            case CITY:
                return boss ? new CityBossLevel() : new CityLevel();
            case HALLS:
                return boss ? new HallsBossLevel() : new HallsLevel();
            default:
                return new DeadEndLevel();
        }
    }

    public abstract T firstLevel();

    public final String displayDepth() {
        return curLvl.displayName();
    }

    public abstract String levelFileName(T marker);

    public String curLevelFileName() {
        return levelFileName(curLvl);
    }

    public final int scalingFactor() {
        return curLvl.scalingDepth();
    }

    public abstract Level generateNextLevel();

    public final long curLevelSeed() {
        return seedForMarker(curLvl);
    }

    public abstract long seedForMarker(T marker);

    public abstract boolean shopOnLevel(T marker);

    public final boolean shopOnCurLevel() {
        return shopOnLevel(curLvl);
    }

    public abstract boolean bossLevel(T marker);

    public final boolean bossCurLevel() {
        return bossLevel(curLvl);
    }

    public boolean bossNextLevel() {
        return bossLevel(nextLevel());
    }

    public abstract T pitFallTarget(T marker);

    public final T curPitFallTarget() {
        return pitFallTarget(curLvl);
    }

    public abstract T nextLevel(T curLevel);

    public final T nextLevel() {
        return nextLevel(curLvl);
    }

    public abstract T prevLevel(T curLevel);

    public T prevOrFirstLevel(T curLvl) {
        return curLvl.firstLevel() ? curLvl : prevLevel(curLvl);
    }

    public final T prevLevel() {
        return prevLevel(curLvl);
    }

    public abstract T previousShopFloor();

    public abstract T nextBossFloor();

    public abstract T cursedTeleportBack();

    public abstract boolean visitedNextLevel(T marker);

    public abstract boolean posNeeded();

    public abstract boolean souNeeded();

    public abstract boolean asNeeded();

    public abstract int petalsNeeded(DriedRose rose);

    public abstract Marker amuletFloor();

    public abstract T randomFloor();

    public abstract Iterable<T> levels();

    public static final String MARKER = "marker";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(MARKER, curLvl);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        curLvl = (T) bundle.get(MARKER);
    }
}

