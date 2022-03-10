package com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks;

import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.ListUtils;
import com.watabou.utils.function.BiFunction;
import com.watabou.utils.function.Consumer;
import com.watabou.utils.function.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("SameParameterValue")
public class LinearLevelPackBuilder {
    private final int levelPackHash;
    private List<Marker.Custom> markers = new ArrayList<>();
    private Chapter curChapter = Chapter.EMPTY;
    private Marker.Custom last = null;
    private int displayId = 1;

    public LinearLevelPackBuilder(int levelPackHash) {
        this.levelPackHash = levelPackHash;
        level(Chapter.EMPTY, "0", 0, 0, 0, false);
    }

    private static int calculateDefaultDepth(int id, Chapter chapter, int progression) {
        if (chapter == Chapter.EMPTY) return id;
        return chapter.id() * 5 + Math.min(progression, 5);
    }

    public LinearLevelPackBuilder chapter(Chapter chapter) {
        curChapter = chapter;
        return this;
    }

    //region single level
    public LinearLevelPackBuilder bossLevel() {
        return level(5, true);
    }

    public LinearLevelPackBuilder amuletLevel() {
        return level(Chapter.EMPTY, "AM", 1, 26, 26, false)
                .customLevel(LastLevel.class);
    }

    public LinearLevelPackBuilder level() {
        return level(last.chapterProgression() + 1);
    }

    public LinearLevelPackBuilder level(int chapterProgression) {
        return level(chapterProgression, false);
    }

    public LinearLevelPackBuilder level(int chapterProgression, boolean boss) {
        int depth = calculateDefaultDepth(-1, curChapter, chapterProgression);
        return level(curChapter, Integer.toString(displayId++), chapterProgression, depth, depth, boss);
    }

    public LinearLevelPackBuilder level(Chapter chapter, String displayName, int chapterProgression, int scalingDepth, int legacyLevelgenMapping, boolean boss) {
        last = new Marker.Custom(-1, levelPackHash, displayName, chapter, chapterProgression, scalingDepth, legacyLevelgenMapping, boss);
        markers.add(last);
        return this;
    }
    //endregion

    //region multiple levels
    public LinearLevelPackBuilder levels(int amount) {
        for (int i = 0; i < amount; i++) {
            level(i + 1, false);
        }
        return this;
    }

    public LinearLevelPackBuilder chapterWithLevels(Chapter chapter, int length) {
        chapter(chapter);
        levels(length - 1);
        bossLevel();
        return this;
    }
    //endregion

    //region parameters
    public LinearLevelPackBuilder action(Consumer<Marker.Custom> actions) {
        actions.accept(last);
        return this;
    }
    public LinearLevelPackBuilder forEach(Consumer<Marker.Custom> actions) {
        for (Marker.Custom marker : markers) {
            actions.accept(marker);
        }
        return this;
    }

    public LinearLevelPackBuilder customLevel(Class<? extends Level> level) {
        last.setCustomLevel(level);
        return this;
    }
    //endregion

    public LinearLevelPackBuilder setAutonamingStartPoint(int number) {
        displayId = number;
        return this;
    }


    public LinearLevelPackBuilder shift(Predicate<Marker.Custom> matcher, int amount) {
        Marker.Custom[] array = markers.toArray(new Marker.Custom[0]);
        ListUtils.shift(array, matcher, amount);
        markers = new ArrayList<>(Arrays.asList(array));
        return this;
    }

    @SuppressWarnings("Java8CollectionRemoveIf")
    public LinearLevelPackBuilder filter(Predicate<Marker.Custom> filter) {
        for (Marker.Custom custom : markers.toArray(new Marker.Custom[0])) {
            if (!filter.test(custom)) markers.remove(custom);
        }
        return this;
    }

    public LinearLevelPackBuilder applyNamings(BiFunction<Marker.Custom, Integer, String> naming) {
        for (int i = 0; i < markers.size(); i++) {
            Marker.Custom marker = markers.get(i);
            marker.setDisplayName(naming.apply(marker, i));
        }
        return this;
    }

    public List<Marker.Custom> apply() {
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).setId(i);
        }
        return markers;
    }
}
