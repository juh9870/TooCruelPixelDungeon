package com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks;

import com.watabou.utils.SparseArray;

public class DefaultMarker extends Marker.Linear {
    static SparseArray<DefaultMarker> cache = new SparseArray<>();

    public DefaultMarker() {
        super(-1);
    }

    protected DefaultMarker(int depth) {
        super(depth);
    }

    public static DefaultMarker of(int depth) {
        DefaultMarker marker = cache.get(depth);
        if (marker == null) {
            marker = new DefaultMarker(depth);
            cache.put(depth, marker);
        }
        return marker;
    }

    @Override
    public boolean firstLevel() {
        return depth() == 1;
    }

    @Override
    public int scalingDepth() {
        return depth();
    }

    @Override
    public int legacyLevelgenMapping() {
        return depth();
    }

    @Override
    public Chapter chapter() {
        switch ((depth() - 1) / 5) {
            case 0:
                return Chapter.SEWERS;
            case 1:
                return Chapter.PRISON;
            case 2:
                return Chapter.CAVES;
            case 3:
                return Chapter.CITY;
            case 4:
                return Chapter.HALLS;
            default:
                return Chapter.EMPTY;
        }
    }

    @Override
    public int chapterProgression() {
        return (depth() - 1) % 5 + 1;
    }
}
