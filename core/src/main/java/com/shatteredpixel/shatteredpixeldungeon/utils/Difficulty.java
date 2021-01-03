package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Modifiers;

public enum Difficulty {
	EASY_0(Integer.MIN_VALUE,"easy"),
	NORMAL_1(1,"normal"),
	HARD_2(2,"hard",Badges.Badge.CHAMPION_1),
	VERY_HARD_3(3,"very_hard",Badges.Badge.CHAMPION_2),
	INSANE_4(4,"insane",Badges.Badge.CHAMPION_3),
	SUICIDE_5(5,"suicide",Badges.Badge.CHAMPION_4),
	IMPOSSIBLE_6(5,"impossible",Badges.Badge.CHAMPION_5),
	HELL_7(6,"hell",Badges.Badge.CHAMPION_6),
	LUNATIC_8(7,"lunatic",Badges.Badge.CHAMPION_7),
	
	POINTLESS_9(100,"pointless");
	
	public int margin;
	public String name;
	public Badges.Badge badge;
	
	Difficulty(int margin, String name, Badges.Badge badge) {
		this.margin = margin;
		this.name = name;
		this.badge = badge;
	}
	
	Difficulty(int margin, String name) {
		this.margin = margin;
		this.name = name;
	}
	
	public static int calculateDifficulty(Modifiers modifiers){
		return 0;
	}
	
	public static Difficulty align(int value){
		Difficulty[] values = values();
		for (int i = values.length - 1; i >= 0; i--) {
			if(values[i].margin<=value)return values[i];
		}
		return EASY_0;
	}
}
