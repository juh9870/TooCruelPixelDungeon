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

	POINTLESS_9(1000, "pointless");
	
	private static final HashSet<DifficultyModifier> MODIFIERS;
	
	
	static {
		MODIFIERS =new HashSet<>();
		
		modifier(1,req(Challenges.NO_FOOD));
		modifier(1.5f,req(Challenges.NO_FOOD,2));
		
		modifier(2f,req(Challenges.NO_ARMOR));
		
		modifier(2f,req(Challenges.NO_HEALING));
		modifier(2.5f,req(Challenges.NO_HEALING,2));
		modifier(5f,req(Challenges.NO_HEALING,3));
		
		
		modifier(2f,req(Challenges.NO_HERBALISM));
		
		
		modifier(1.5f,req(Challenges.SWARM_INTELLIGENCE));
		modifier(2.5f,req(Challenges.SWARM_INTELLIGENCE,2));
		
		
		modifier(1f,req(Challenges.DARKNESS));
		modifier(3f,req(Challenges.DARKNESS,2));
		
		modifier(1.5f,req(Challenges.NO_SCROLLS));
		
		modifier(1.5f,req(Challenges.AMNESIA));
		modifier(3f,req(Challenges.AMNESIA,2));
		
		modifier(2f,req(Challenges.CURSED));
		
		modifier(2f,req(Challenges.BLACKJACK));
		
		modifier(1.5f,req(Challenges.HORDE));
		modifier(2f,req(Challenges.HORDE,2));
		modifier(7f,req(Challenges.HORDE,3));
		
		modifier(2f,req(Challenges.COUNTDOWN));
		modifier(5f,req(Challenges.COUNTDOWN,2));
		
		modifier(1.5f,req(Challenges.ANALGESIA));
		
		modifier(1f,req(Challenges.BIG_LEVELS));
		
		modifier(2f,req(Challenges.MUTAGEN));
		modifier(5f,req(Challenges.MUTAGEN,2));
		
		modifier(3f,req(Challenges.RESURRECTION));
		modifier(4f,req(Challenges.RESURRECTION,2));
		modifier(7f,req(Challenges.RESURRECTION,3));
		
		modifier(1f,req(Challenges.EXTREME_CAUTION));
		modifier(2f,req(Challenges.EXTREME_CAUTION,2));
		
		modifier(1f,req(Challenges.EXTERMINATION));
		
		modifier(1.5f,req(Challenges.ROOK));
		
		modifier(2f,req(Challenges.NO_PERKS));
		
		modifier(2f,req(Challenges.CHAMPION_ENEMIES));
		modifier(4f,req(Challenges.CHAMPION_ENEMIES,2));
		modifier(7f,req(Challenges.CHAMPION_ENEMIES,3));
		
		
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
			total+=modifier.modifier;
		}
		
		POINTLESS_9.margin=total;
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
			diff=modifier.apply(modifiers,diff);
		}
		return diff;
	}
	
	public static Difficulty align(Modifiers modifiers) {
		return align(calculateDifficulty(modifiers));
	}
	
	private static final float DELTA = 0.0001f;
	public static Difficulty align(float value) {
		Difficulty[] values = values();
		for (int i = values.length - 1; i >= 0; i--) {
			if (values[i].margin-DELTA <= value) return values[i];
		}
		return EASY_0;
	}
	
	private static Requirement req(Challenges chal){
		return req(chal,1);
	}
	private static Requirement req(Challenges chal, int level){
		return new Requirement(chal.ordinal(),level);
	}
	
	private static void modifier(float mod, Requirement... requirements){
		MODIFIERS.add(new DifficultyModifier(mod,requirements));
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
		int ordinal;
		int level;
		
		public Requirement(int ordinal, int level) {
			this.ordinal = ordinal;
			this.level = level;
		}
		
		public boolean validate(Modifiers mods) {
			return mods.challengeTier(ordinal) >= level;
		}
	}
}