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
import com.watabou.utils.Reflection;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import jdk.vm.ci.code.site.Mark;

public abstract class LevelPack<T extends Marker> implements Bundlable, Iterable<T> {
    public static final String MARKER = "marker";
    public T curLvl;

    public void init() {

    }

    public Class<? extends Level> levelClassForMarker(T marker) {
        if (marker.customLevel() != null) return marker.customLevel();
        return marker.boss() ? marker.chapter().bossLevel() : marker.chapter().level();
    }

    public Level levelForMarker(T marker) {
        return Reflection.newInstance(levelClassForMarker(marker));
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

    public boolean bossNextLevel() {
        return nextLevel().boss();
    }

    public T pitFallTarget(T marker) {
        return nextLevel();
    }

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

    @Override
    public Iterator<T> iterator() {
        return levels().iterator();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(MARKER, curLvl);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        curLvl = (T) bundle.get(MARKER);
    }
}

