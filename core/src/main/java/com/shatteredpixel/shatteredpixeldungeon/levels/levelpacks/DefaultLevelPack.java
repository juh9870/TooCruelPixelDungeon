package com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Countdown;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.Bundle;
import com.watabou.utils.Lazy;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.List;

import jdk.vm.ci.code.site.Mark;


public class DefaultLevelPack extends LevelPack<DefaultMarker> {

    @Override
    public void init() {
        super.init();
        curLvl = DefaultMarker.of(0);
    }

    @Override
    public DefaultMarker firstLevel() {
        return DefaultMarker.of(1);
    }

    public static DefaultLevelPack fromLegacyData(int depth) {
        DefaultLevelPack pack = new DefaultLevelPack();
        pack.curLvl = DefaultMarker.of(depth);
        return pack;
    }

    public static DefaultMarker markerFromDepth(int depth) {
        return DefaultMarker.of(depth);
    }

    public static Marker getOrLoadFromDepth(Bundle bundle, String oldKey, String newKey) {
        if (bundle.contains(newKey)) {
            return (Marker) bundle.get(newKey);
        }
        return markerFromDepth(bundle.getInt(oldKey));
    }

    @Override
    public String levelFileName(DefaultMarker marker) {
        return displayDepth();
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
    public long seedForMarker(DefaultMarker marker) {
        Random.pushGenerator(Dungeon.seed);

        for (int i = 0; i < marker.depth(); i++) {
            Random.Long(); //we don't care about these values, just need to go through them
        }
        long result = Random.Long();

        Random.popGenerator();
        return result;
    }

    @Override
    public boolean shopOnLevel(DefaultMarker marker) {
        return marker.depth() == 6 || marker.depth() == 11 || marker.depth() == 16;
    }

    @Override
    public boolean bossLevel(DefaultMarker marker) {
        return marker.depth() == 5 || marker.depth() == 10 || marker.depth() == 15 || marker.depth() == 20 || marker.depth() == 25;
    }

    @Override
    public DefaultMarker pitFallTarget(DefaultMarker marker) {
        return DefaultMarker.of(curLvl.depth() + 1);
    }

    @Override
    public DefaultMarker nextLevel(DefaultMarker curLevel) {
        return DefaultMarker.of(curLevel.depth() + 1);
    }

    @Override
    public DefaultMarker prevLevel(DefaultMarker curLevel) {
        return DefaultMarker.of(curLevel.depth() - 1);
    }

    @Override
    public DefaultMarker previousShopFloor() {
        return DefaultMarker.of(Math.max(1, (curLvl.depth()) - 1 - (curLvl.depth() - 2) % 5));
    }

    @Override
    public DefaultMarker nextBossFloor() {
        return DefaultMarker.of(5 * (1 + curLvl.depth() / 5));
    }

    @Override
    public DefaultMarker cursedTeleportBack() {
        //each depth has 1 more weight than the previous depth.
        float[] depths = new float[curLvl.depth() - 1];
        for (int i = 1; i < curLvl.depth(); i++) depths[i - 1] = i;
        int depth = 1 + Random.chances(depths);
        return DefaultMarker.of(depth);
    }

    @Override
    public boolean visitedNextLevel(DefaultMarker marker) {
        return Statistics.deepestFloor.compareTo(marker) > 0;
    }

    @Override
    public boolean posNeeded() {
        //2 POS each floor set
        int posLeftThisSet = 2 - (Dungeon.LimitedDrops.STRENGTH_POTIONS.getCount() - (curLvl.depth() / 5) * 2);
        if (posLeftThisSet <= 0) return false;

        int floorThisSet = (curLvl.depth() % 5);

        //pos drops every two floors, (numbers 1-2, and 3-4) with a 50% chance for the earlier one each time.
        int targetPOSLeft = 2 - floorThisSet / 2;
        if (floorThisSet % 2 == 1 && Random.Int(2) == 0) targetPOSLeft--;

        return targetPOSLeft < posLeftThisSet;
    }

    @Override
    public boolean souNeeded() {
        int souLeftThisSet;
        //3 SOU each floor set, 1.5 (rounded) on forbidden runes challenge
        if (Challenges.FORBIDDEN_RUNES.enabled()) {
            souLeftThisSet = Math.round(1.5f - (Dungeon.LimitedDrops.UPGRADE_SCROLLS.getCount() - (curLvl.depth() / 5) * 1.5f));
        } else {
            souLeftThisSet = 3 - (Dungeon.LimitedDrops.UPGRADE_SCROLLS.getCount() - (curLvl.depth() / 5) * 3);
        }
        if (souLeftThisSet <= 0) return false;

        int floorThisSet = (curLvl.depth() % 5);
        //chance is floors left / scrolls left
        return Random.Int(5 - floorThisSet) < souLeftThisSet;
    }

    @Override
    public boolean asNeeded() {
        //1 AS each floor set
        int asLeftThisSet = 1 - (Dungeon.LimitedDrops.ARCANE_STYLI.getCount() - (curLvl.depth() / 5));
        if (asLeftThisSet <= 0) return false;

        int floorThisSet = (curLvl.depth() % 5);
        //chance is floors left / scrolls left
        return Random.Int(5 - floorThisSet) < asLeftThisSet;
    }

    @Override
    public int petalsNeeded(DriedRose rose) {
        return (int) Math.ceil((float) ((curLvl.depth() / 2) - rose.droppedPetals) / 3);
    }

    @Override
    public Marker amuletFloor() {
        return DefaultMarker.of(26);
    }

    @Override
    public DefaultMarker randomFloor() {
        return DefaultMarker.of(Random.Int(25));
    }

    private static final Lazy<Iterable<DefaultMarker>> levels = Lazy.of(() -> {
        List<DefaultMarker> markers = new ArrayList<>();

        for (int i = 1; i <= 26; i++) {
            markers.add(DefaultMarker.of(i));
        }
        return markers;
    });

    @Override
    public Iterable<DefaultMarker> levels() {
        return levels.get();
    }
}

