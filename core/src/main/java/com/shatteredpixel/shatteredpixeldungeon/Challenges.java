/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ascension;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Extermination;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Revealing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;

public enum Challenges {
    // T1
    ON_DIET(0, 1, 1),
    FAITH_ARMOR(1, 1, 2),
    PHARMACOPHOBIA(2, 1, 2),
    BARREN_LAND(3, 1, 2) {
        @Override
        protected boolean _isItemBlocked(Item item) {
            return item instanceof Dewdrop;
        }
    },
    SWARM_INTELLIGENCE(4, 1, 1.5f),
    DARKNESS(5, 1, 1),
    FORBIDDEN_RUNES(6, 1, 1.5f),
    AMNESIA(7, 1, 1.5f),
    CURSED(8, 1, 2),
    BLACKJACK(9, 1, 2),
    HORDE(10, 1, 1.5f) {
        @Override
        protected float _nMobsMult() {
            return 2;
        }
    },
    COUNTDOWN(11, 1, 2),
    ANALGESIA(12, 1, 1.5f),
    BIG_LEVELS(13, 1, 1) {
        @Override
        protected float _nMobsMult() {
            return 2;
        }

        @Override
        protected float _nTrapsMult() {
            return 2;
        }
    },
    MUTAGEN(14, 1, 2) {
        @Override
        protected float _rareLootChanceMultiplier() {
            if (EVOLUTION.enabled()) return 1 / 50f;
            return 4 / 50f;
        }
    },
    RESURRECTION(15, 1, 3f),
    EXTREME_CAUTION(16, 1, 1) {
        @Override
        protected float _nTrapsMult() {
            return 4;
        }
    },
    EXTERMINATION(17, 1, 1),
    ROOK(18, 1, 1.5f),
    CHAMPION_ENEMIES(19, 1, 2f),
    NO_PERKS(20, 1, 3f),

    //T2
    FAMINE(21, 2, 1.5f, ON_DIET) {
        @Override
        protected boolean _isItemBlocked(Item item) {
            if (item instanceof Food && !(item instanceof SmallRation)) {
                return true;
            } else return item instanceof HornOfPlenty;
        }
    },
    INTOXICATION(22, 2, 2.5f),
    PLAGUE(23, 2, 3f, INTOXICATION),
    HEART_OF_HIVE(24, 2, 2.5f, SWARM_INTELLIGENCE),
    BLINDNESS(25, 2, 3f, DARKNESS),
    LOBOTOMY(26, 2, 3f, AMNESIA),
    INVASION(27, 2, 2f),
    EVOLUTION(29, 2, 5f, MUTAGEN),
    REBIRTH(30, 2, 4f),
    EXTREME_DANGER(32, 2, 2f, EXTREME_CAUTION),
    ELITE_CHAMPIONS(33, 2, 4f, CHAMPION_ENEMIES),
    LEGION(28, 2, 4f),

    //T3
    ASCENSION(31, 3, 7f, RESURRECTION, REBIRTH),
    DUNGEON_OF_CHAMPIONS(34, 3, 7f, ELITE_CHAMPIONS),
    RACING_THE_DEATH(35, 3, 7f),
    MANIFESTING_MYRIADS(36,3,7f,LEGION,HORDE){
        @Override
        protected float _nMobsMult() {
            return 1.5f;
        }
    },

    //Last id 36
    ;
    private static final Challenges[] mappings;

    static {
        mappings = new Challenges[Challenges.values().length];
        for (Challenges value : Challenges.values()) {
            if (mappings[value.id] != null) throw new DuplicateChallengeException(mappings[value.id], value);
            mappings[value.id] = value;
        }
        for (int i = 0; i < mappings.length; i++) {
            if (mappings[i] == null) throw new MissingChallengeException(i);
        }
    }

    public final String name;
    public final int id;
    public final float difficulty;
    public final int tier;
    public final int[] requirements;

    Challenges(int id, int tier, float difficulty, Challenges... requirements) {
        this.name = name().toLowerCase();
        this.id = id;
        this.difficulty = difficulty;
        this.tier = tier;
        this.requirements = new int[requirements.length];
        for (int i = 0; i < requirements.length; i++) {
            this.requirements[i] = requirements[i].id;
        }
    }

    public static void enableFromLegacyTier(boolean[] chals, Challenges chal, int tier) {
        switch (chal) {
            case ON_DIET:
                chals[FAMINE.id] = tier == 2;
                break;
            case PHARMACOPHOBIA:
                chals[INTOXICATION.id] = tier > 1;
                chals[PLAGUE.id] = tier == 3;
                break;
            case SWARM_INTELLIGENCE:
                chals[HEART_OF_HIVE.id] = tier == 2;
                break;
            case AMNESIA:
                chals[LOBOTOMY.id] = tier == 2;
                break;
            case HORDE:
                chals[INVASION.id] = tier > 1;
                chals[LEGION.id] = tier == 3;
                break;
            case COUNTDOWN:
                chals[RACING_THE_DEATH.id] = tier == 2;
                break;
            case RESURRECTION:
                chals[REBIRTH.id] = tier > 1;
                chals[ASCENSION.id] = tier == 3;
                break;
            case EXTREME_CAUTION:
                chals[EXTREME_DANGER.id] = tier == 2;
                break;
            case CHAMPION_ENEMIES:
                chals[ELITE_CHAMPIONS.id] = tier > 1;
                chals[DUNGEON_OF_CHAMPIONS.id] = tier == 3;
                break;
        }
    }

    public static Challenges fromId(int id) {
        return mappings[id];
    }

    public static float ascendingChance(Mob m) {

        Ascension buff;
        if ((buff = m.buff(Ascension.class)) != null) {
            if (buff.level >= 2 && m.buff(Extermination.class) != null) return 0;
        }
        if (m.buff(Ascension.ForcedAscension.class) != null) return 1;

        float chance = .33f;

        float _m = Math.max(m.maxLvl, Dungeon.depth);
        float h = Dungeon.hero.lvl;
        float a = buff == null ? 0 : buff.level;

        if (Dungeon.hero.lvl > _m) {

            float o = Math.max(0, 10 - _m);

            chance = .33f + ((1 - .33f) * ((h - _m + o) * 30 / (35 - o - _m))) / 30;
        }

        if (Statistics.amuletObtained && chance < .66) {
            chance = .66f;
        }

        if (a > 0) {
            if (h <= _m) chance *= 0.5 / Math.sqrt(a);
            if (h <= _m + 2) chance *= 0.75 / Math.sqrt(a);
        }

        return chance;
    }

    public static int maxAscension(Mob m) {
        if (ASCENSION.enabled() || m.buff(Ascension.ForcedAscension.class) != null) {
            return 6;
        } else if (REBIRTH.enabled()) {
            return 1;
        }
        return 0;
    }

    public static float nMobsMultiplier() {
        float mult = 1;
        for (Challenges ch : values()) {
            if (ch.enabled()) mult *= ch._nMobsMult();
        }
        return mult;
    }

    public static float nTrapsMultiplier() {
        float mult = 1;
        for (Challenges ch : values()) {
            if (ch.enabled()) mult *= ch._nTrapsMult();
        }
        return mult;
    }

    public static float rareLootChanceMultiplier() {
        float mult = 1;
        for (Challenges ch : values()) {
            if (ch.enabled()) mult *= ch._rareLootChanceMultiplier();
        }
        return mult;
    }

    public static boolean isItemBlocked(Item item) {
        for (Challenges ch : values()) {
            if (ch.enabled() && ch._isItemBlocked(item)) return true;
        }
        return false;
    }

    public static int checkExterminators() {
        int left = 0;
        if (EXTERMINATION.enabled())
            for (Mob m : Dungeon.level.mobs) {
                if (m.buff(Extermination.class) != null) {
                    left++;
                    Buff.affect(m, Revealing.class, 1f);
                }
            }
        return left;
    }

    public static Icons icon() {
        return icon(SPDSettings.modifiers());
    }

    public static String saveString(boolean[] challenges) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < challenges.length; i++) {
            s.append(challenges[i] ? 1 : 0);
        }
        return s.toString();
    }

    public static boolean[] fromString(String challenges) {
        boolean[] ret = new boolean[Challenges.values().length];
        int l = Math.min(Challenges.values().length, challenges.length());
        for (int i = 0; i < l; i++) {
            int tier = Integer.parseInt(Character.toString(challenges.charAt(i)));
            ret[i] = tier > 0;
            if (tier > 1) enableFromLegacyTier(ret, fromId(i), tier);
        }
        return ret;
    }

    public static boolean[] fromLegacy(int... levels) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < levels.length; i++) {
            String str = Integer.toString(levels[i], 2);
            int l = str.length();
            int t = 0;
            for (int j = 0; j < l; j++) {
                t += str.charAt(l - j - 1) == '1' ? 1 : 0;
            }
            s.append(t);
        }
        return fromString(s.toString());
    }

    public static Icons icon(Modifiers modifiers) {
        int l = 0;
        for (int i = 0; i < modifiers.challenges.length; i++) {
            if (modifiers.challenges[i])
                l = Math.max(l, fromId(i).tier);
        }
        if (l <= 0) {
            return Icons.CHALLENGE_OFF;
        } else if (l == 1) {
            return Icons.CHALLENGE_ON;
        } else if (l == 2) {
            return Icons.CHALLENGE_HELL;
        }
        return Icons.CHALLENGE_HELL2;
    }

    protected float _nMobsMult() {
        return 1;
    }

    protected float _nTrapsMult() {
        return 1;
    }

    protected float _rareLootChanceMultiplier() {
        return 1;
    }

    public boolean enabled() {
        return Dungeon.isChallenged(this.id);
    }

    public boolean requires(Challenges other) {
        for (int requirement : this.requirements) {
            if (requirement == other.id) return true;
            if (fromId(requirement).requires(other)) return true;
        }
        return false;
    }

    public String localizedName() {
        return Messages.get(Challenges.class, name);
    }

    protected boolean _isItemBlocked(Item item) {
        return false;
    }

    public static class DuplicateChallengeException extends Error {
        public DuplicateChallengeException(Challenges a, Challenges b) {
            super(String.format("Challenges %s and %s have conflicting ids.", a.name, b.name));
        }
    }

    public static class MissingChallengeException extends Error {
        public MissingChallengeException(int id) {
            super("Challenge id " + id + " is empty.");
        }
    }
}