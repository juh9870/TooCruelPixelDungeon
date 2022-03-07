package com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks;

import com.sun.source.tree.BreakTree;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.Objects;

public abstract class Marker implements Bundlable, Comparable<Marker> {
    public abstract String displayName();

    public abstract Chapter chapter();
    public int chapterId(){
        return chapter().ordinal();
    }

    public abstract int chapterProgression();

    public boolean firstLevel() {
        return chapter() == Chapter.SEWERS && chapterProgression() == 1;
    }

    public abstract int scalingDepth();

    public abstract int legacyLevelgenMapping();

    public abstract String debugInfo();

    @Override
    public abstract boolean equals(Object obj);

    public static abstract class Linear extends Marker {
        private int depth;
        private static final String DEPTH = "depth";

        protected Linear(int depth) {
            this.depth = depth;
        }

        @Override
        public String displayName() {
            return Integer.toString(depth());
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Linear && ((Linear) obj).depth() == depth();
        }

        @Override
        public int hashCode() {
            return depth;
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            depth = bundle.getInt(DEPTH);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put(DEPTH, depth());
        }

        public int depth() {
            return depth;
        }

        @Override
        public int compareTo(Marker marker) {
            if (getClass() == marker.getClass()) {
                return Integer.compare(depth, ((Linear) marker).depth);
            }
            throw new IllegalArgumentException("Expected " + getClass() + ", got " + marker.getClass());
        }

        @Override
        public String debugInfo() {
            return Integer.toString(depth);
        }
    }
}
