package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Modifiers;

import java.util.HashSet;

public enum Difficulty {
    EASY_0(Integer.MIN_VALUE, "easy"),
    NORMAL_1(4, "normal"),
    HARD_2(8, "hard", Badges.Badge.CHAMPION_1),
    VERY_HARD_3(12, "very_hard", Badges.Badge.CHAMPION_2),
    INSANE_4(16, "insane", Badges.Badge.CHAMPION_3),
    SUICIDE_5(20, "suicide", Badges.Badge.CHAMPION_4),
    IMPOSSIBLE_6(24, "impossible", Badges.Badge.CHAMPION_5),
    HELL_7(28, "hell", Badges.Badge.CHAMPION_6),
    LUNATIC_8(32, "lunatic", Badges.Badge.CHAMPION_7),
    LUNATIC_P_9(40, "lunatic_p"),
    LUNATIC_PP_10(50, "lunatic_pp"),
    LUNATIC_PPP_11(60, "lunatic_ppp"),
    LUNATIC_PPPP_12(70, "lunatic_pppp"),
    ULTIMATE_13(80, "ultimate"),
    ISEKAI_14(90, "isekai"),

    BOUNTY_15(1000, "bounty"),
    LEONARD_16(1000, "leonard"),
    SIA_17(1000, "sia");

    private static final HashSet<DifficultyModifier> MODIFIERS;
    private static final float DELTA = 0.0001f;

    static {
        MODIFIERS = new HashSet<>();

        for (Challenges value : Challenges.values()) {
            modifier(value.difficulty, req(value));
        }


//		modifier(0.2f,req(Challenges.NO_FOOD,2),req(Challenges.NO_HEALING));
//
//		modifier(0.2f,req(Challenges.EXTERMINATION),req(Challenges.HORDE));
//		modifier(0.4f,req(Challenges.EXTERMINATION),req(Challenges.COUNTDOWN));
//		modifier(0.6f,req(Challenges.EXTERMINATION),req(Challenges.COUNTDOWN),req(Challenges.HORDE));
//
//		modifier(0.6f,req(Challenges.DARKNESS,2),req(Challenges.COUNTDOWN,2));
//		modifier(0.6f,req(Challenges.ROOK),req(Challenges.COUNTDOWN,2));


        float total = 0;

        for (DifficultyModifier modifier : MODIFIERS) {
            if (modifier.modifier < 0) continue;
            total += modifier.modifier;
        }

        SIA_17.margin = total;
        LEONARD_16.margin = total - 25;
        BOUNTY_15.margin = total - 50;
    }

    public float margin;
    public String name;
    public Badges.Badge badge;

    Difficulty(float margin, String name, Badges.Badge badge) {
        this.margin = margin;
        this.name = name;
        this.badge = badge;
    }

    Difficulty(float margin, String name) {
        this.margin = margin;
        this.name = name;
    }

    public static float calculateDifficulty(Modifiers modifiers) {
        float diff = 0.0f;
        for (DifficultyModifier modifier : MODIFIERS) {
            diff = modifier.apply(modifiers, diff);
        }
        return diff;
    }

    public static Difficulty align(Modifiers modifiers) {
        return align(calculateDifficulty(modifiers));
    }

    public static Difficulty align(float value) {
        Difficulty[] values = values();
        for (int i = values.length - 1; i >= 0; i--) {
            if (values[i].margin - DELTA <= value) return values[i];
        }
        return EASY_0;
    }

    private static Requirement req(Challenges chal) {
        return new Requirement(chal.id);
    }

    private static void modifier(float mod, Requirement... requirements) {
        MODIFIERS.add(new DifficultyModifier(mod, requirements));
    }

    public static class DifficultyModifier {
        public float modifier;
        public Requirement[] requirements;

        public DifficultyModifier(float modifier, Requirement[] requirements) {
            this.modifier = modifier;
            this.requirements = requirements;
        }

        public float apply(Modifiers mods, float value) {
            for (Requirement requirement : requirements) {
                if (!requirement.validate(mods)) return value;
            }
            return value + modifier;
        }
    }

    public static class Requirement {
        int id;

        public Requirement(int id) {
            this.id = id;
        }

        public boolean validate(Modifiers mods) {
            return mods.isChallenged(id);
        }
    }
}