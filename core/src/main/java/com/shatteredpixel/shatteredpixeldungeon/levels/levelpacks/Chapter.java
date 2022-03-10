package com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks;

import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;

public enum Chapter {
    SEWERS {
        @Override
        Chapter next() {
            return PRISON;
        }

        @Override
        Class<? extends Level> level() {
            return SewerLevel.class;
        }

        @Override
        Class<? extends Level> bossLevel() {
            return SewerBossLevel.class;
        }
    },
    PRISON {
        @Override
        Chapter next() {
            return CAVES;
        }

        @Override
        Class<? extends Level> level() {
            return PrisonLevel.class;
        }

        @Override
        Class<? extends Level> bossLevel() {
            return PrisonBossLevel.class;
        }
    },
    CAVES {
        @Override
        Chapter next() {
            return CITY;
        }

        @Override
        Class<? extends Level> level() {
            return CavesLevel.class;
        }

        @Override
        Class<? extends Level> bossLevel() {
            return CavesBossLevel.class;
        }
    },
    CITY {
        @Override
        Chapter next() {
            return HALLS;
        }

        @Override
        Class<? extends Level> level() {
            return CityLevel.class;
        }

        @Override
        Class<? extends Level> bossLevel() {
            return CityBossLevel.class;
        }
    },
    HALLS {
        @Override
        Chapter next() {
            return EMPTY;
        }

        @Override
        Class<? extends Level> level() {
            return HallsLevel.class;
        }

        @Override
        Class<? extends Level> bossLevel() {
            return HallsBossLevel.class;
        }
    },
    EMPTY {
        @Override
        Chapter next() {
            return EMPTY;
        }

        @Override
        Class<? extends Level> level() {
            return DeadEndLevel.class;
        }

        @Override
        Class<? extends Level> bossLevel() {
            return DeadEndLevel.class;
        }
    };

    public static Chapter fromId(int id) {
        try {
            return values()[id];
        } catch (IndexOutOfBoundsException e) {
            return Chapter.EMPTY;
        }
    }

    abstract Chapter next();

    abstract Class<? extends Level> level();

    abstract Class<? extends Level> bossLevel();

    public int id() {
        return ordinal();
    }
}
