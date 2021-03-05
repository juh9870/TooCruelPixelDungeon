package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Modifiers;

import java.util.HashSet;

public enum Difficulty {
	EASY_0(Integer.MIN_VALUE, "easy"),
	NORMAL_1(1, "normal"),
	HARD_2(2, "hard", Badges.Badge.CHAMPION_1),
	VERY_HARD_3(3, "very_hard", Badges.Badge.CHAMPION_2),
	INSANE_4(4, "insane", Badges.Badge.CHAMPION_3),
	SUICIDE_5(5, "suicide", Badges.Badge.CHAMPION_4),
	IMPOSSIBLE_6(5, "impossible", Badges.Badge.CHAMPION_5),
	HELL_7(6, "hell", Badges.Badge.CHAMPION_6),
	LUNATIC_8(7, "lunatic", Badges.Badge.CHAMPION_7),
	
	POINTLESS_9(100, "pointless");
	
	private static final HashSet<DifficultyModifier> MODIFIERS;
	
	
	static {
		MODIFIERS =new HashSet<>();
		
		modifier(0.1f,req(Challenges.NO_FOOD));
		modifier(0.2f,req(Challenges.NO_FOOD,2));
		
		modifier(0.2f,req(Challenges.NO_ARMOR));
		
		modifier(0.2f,req(Challenges.NO_HEALING));
		modifier(0.2f,req(Challenges.NO_HEALING,2));
		modifier(0.3f,req(Challenges.NO_HEALING,3));
		
		
		modifier(0.1f,req(Challenges.NO_HERBALISM));
		
		
		modifier(0.1f,req(Challenges.SWARM_INTELLIGENCE));
		modifier(0.3f,req(Challenges.SWARM_INTELLIGENCE,2));
		
		
		modifier(0.1f,req(Challenges.DARKNESS));
		modifier(0.3f,req(Challenges.DARKNESS,2));
		
		modifier(0.3f,req(Challenges.NO_SCROLLS));
		
		modifier(0.2f,req(Challenges.AMNESIA));
		modifier(0.1f,req(Challenges.AMNESIA,2));
		
		modifier(0.2f,req(Challenges.CURSED));
		
		modifier(0.3f,req(Challenges.BLACKJACK));
		
		modifier(0.2f,req(Challenges.HORDE));
		modifier(0.3f,req(Challenges.HORDE,2));
		modifier(2.0f,req(Challenges.HORDE,3));
		
		modifier(0.2f,req(Challenges.COUNTDOWN));
		modifier(0.6f,req(Challenges.COUNTDOWN,2));
		
		modifier(0.3f,req(Challenges.ANALGESIA));
		
		modifier(0.2f,req(Challenges.BIG_LEVELS));
		
		modifier(0.2f,req(Challenges.MUTAGEN));
		modifier(0.3f,req(Challenges.MUTAGEN,2));
		
		modifier(0.2f,req(Challenges.RESURRECTION));
		modifier(0.3f,req(Challenges.RESURRECTION,2));
		modifier(0.4f,req(Challenges.RESURRECTION,3));
		
		modifier(0.1f,req(Challenges.EXTREME_CAUTION));
		modifier(0.2f,req(Challenges.EXTREME_CAUTION,2));
		
		modifier(0.2f,req(Challenges.EXTERMINATION));
		
		modifier(0.2f,req(Challenges.ROOK));
		
		modifier(0.2f,req(Challenges.NO_PERKS));
		
		modifier(0.2f,req(Challenges.CHAMPION_ENEMIES));
		modifier(0.3f,req(Challenges.CHAMPION_ENEMIES,2));
		modifier(2.0f,req(Challenges.CHAMPION_ENEMIES,3));
		
		
		modifier(0.2f,req(Challenges.NO_FOOD,2),req(Challenges.NO_HEALING));
		
		modifier(0.2f,req(Challenges.EXTERMINATION),req(Challenges.HORDE));
		modifier(0.4f,req(Challenges.EXTERMINATION),req(Challenges.COUNTDOWN));
		modifier(0.6f,req(Challenges.EXTERMINATION),req(Challenges.COUNTDOWN),req(Challenges.HORDE));
		
		modifier(0.6f,req(Challenges.DARKNESS,2),req(Challenges.COUNTDOWN,2));
		modifier(0.6f,req(Challenges.ROOK),req(Challenges.COUNTDOWN,2));
		
		
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
	
	Difficulty(int margin, String name) {
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
	
	static class Requirement {
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