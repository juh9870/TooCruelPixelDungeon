package com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks;

public enum Chapter {
    SEWERS {
        @Override
        Chapter next() {
            return PRISON;
        }
    },
    PRISON {
        @Override
        Chapter next() {
            return CAVES;
        }
    },
    CAVES {
        @Override
        Chapter next() {
            return CITY;
        }
    },
    CITY {
        @Override
        Chapter next() {
            return HALLS;
        }
    },
    HALLS {
        @Override
        Chapter next() {
            return EMPTY;
        }
    },
    EMPTY {
        @Override
        Chapter next() {
            return EMPTY;
        }
    };

    abstract Chapter next();
}
