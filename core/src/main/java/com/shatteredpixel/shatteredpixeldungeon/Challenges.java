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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Extermanation;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;

import java.util.Arrays;

public enum Challenges {
	NO_FOOD("no_food",2) {
		@Override
		protected boolean _isItemBlocked(Item item) {
			if (tier(2)) {
				if (item instanceof Food && !(item instanceof SmallRation)) {
					return true;
				} else return item instanceof HornOfPlenty;
			}
			return false;
		}
	},
	NO_ARMOR("no_armor"),
	NO_HEALING("no_healing", 3),
	NO_HERBALISM("no_herbalism") {
		@Override
		protected boolean _isItemBlocked(Item item) {
			return item instanceof Dewdrop;
		}
	},
	SWARM_INTELLIGENCE("swarm_intelligence", 2),
	DARKNESS("darkness", 2),
	NO_SCROLLS("no_scrolls"),
	AMNESIA("amnesia", 2),
	CURSED("cursed"),
	BLACKJACK("blackjack"),
	HORDE("horde", 3) {
		@Override
		protected float _nMobsMult() {
			return 2;
		}
	},
	COUNTDOWN("countdown", 2),
	ANALGESIA("analgesia"),
	BIG_LEVELS("big_levels") {
		@Override
		protected float _nMobsMult() {
			return 2;
		}
		
		@Override
		protected float _nTrapsMult() {
			return 2;
		}
	},
	MUTAGEN("mutagen", 2) {
		@Override
		protected float _rareLootChanceMultiplier() {
			if (tier(2)) return 1 / 50f;
			return 4 / 50f;
		}
	},
	RESURRECTION("resurrection", 3),
	EXTREME_CAUTION("extreme_caution", 2) {
		@Override
		protected float _nTrapsMult() {
			return 4;
		}
	},
	EXTERMINATION("extermination"),
	ROOK("rook"),
	CHAMPION_ENEMIES("champion_enemies",3),
	NO_PERKS("perks");
	public String name;
	public int maxLevel;
	
	Challenges(String name) {
		this(name,1);
	}
	
	Challenges(String name, int maxLevel) {
		this.name = name;
		this.maxLevel = maxLevel;
	}
	
	public static float ascendingChance(Mob m) {
		
		Ascension buff;
		if ((buff = m.buff(Ascension.class)) != null) {
			if (buff.level >= 2 && m.buff(Extermanation.class) != null) return 0;
		}
		if (m.buff(Ascension.ForcedAscension.class) != null) return 1;
		
		float chance = .33f;
		
		float _m = m.maxLvl;
		float h = Dungeon.hero.lvl;
		float a = buff == null ? 0 : buff.level;
		
		if (Dungeon.hero.lvl > m.maxLvl) {
			
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
		int tier = RESURRECTION.tier();
		if (m.buff(ChampionEnemy.Restoring.class) != null) return 1;
		if (tier >= 3 || m.buff(Ascension.ForcedAscension.class) != null) {
			return 6;
		} else if (tier == 2) {
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
	
	public static int exterminatorsLeft() {
		int left = 0;
		if (EXTERMINATION.enabled())
			for (Mob m : Dungeon.level.mobs) {
				if (m.buff(Extermanation.class) != null) left++;
			}
		return left;
	}
	
	public static Icons icon() {
		return icon(SPDSettings.modifiers());
	}
	
	public static String saveString(int[] challenges) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < challenges.length; i++) {
			s.append(challenges[i]);
		}
		return s.toString();
	}
	
	public static int[] fromString(String challenges) {
		int[] ret = new int[Challenges.values().length];
		int l = Challenges.values().length;
		for (int i = 0; i < l; i++) {
			if (i < challenges.length()) {
				ret[i] = Integer.parseInt(Character.toString(challenges.charAt(i)));
			} else {
				ret[i] = 0;
			}
		}
		return ret;
	}
	
	public static int[] fromLegacy(int... levels) {
		int[] arr = new int[Challenges.values().length];
		Arrays.fill(arr, 0);
		for (int i = 0; i < levels.length; i++) {
			String str = Integer.toString(levels[i], 2);
			int l = str.length();
			for (int j = 0; j < l; j++) {
				arr[j] += str.charAt(l - j - 1) == '1' ? 1 : 0;
			}
		}
		return arr;
	}
	
	public static Icons icon(Modifiers modifiers) {
		int l = 0;
		for (int i = 0; i < modifiers.challenges.length; i++) {
			l = Math.max(l, modifiers.challenges[i]);
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
		return tier(1);
	}
	
	public boolean tier(int required) {
		return tier() >= required;
	}
	
	public int tier() {
		return Dungeon.challengeTier(this.ordinal());
	}
	
	protected boolean _isItemBlocked(Item item) {
		return false;
	}
}