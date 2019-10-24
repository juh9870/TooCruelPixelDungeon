/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
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

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Extermanation;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.watabou.utils.Random;

import java.util.ArrayList;

public enum Challenges {
	NO_FOOD("no_food"){
		@Override
		protected boolean _isItemBlocked(Item item) {
			if (item instanceof Food && !(item instanceof SmallRation)) {
				return true;
			} else if (item instanceof HornOfPlenty){
				return true;
			}
			return false;
		}
	},
	NO_ARMOR("no_armor"){
		@Override
		protected boolean _isItemBlocked(Item item) {
			if (item instanceof Armor && !(item instanceof ClothArmor || item instanceof ClassArmor)) {
				return true;
			}
			return false;
		}
	},
	NO_HEALING("no_healing", true){
		@Override
		protected boolean _isItemBlocked(Item item) {
			if (item instanceof PotionOfHealing){
				return true;
			} else if (item instanceof Blandfruit
					&& ((Blandfruit) item).potionAttrib instanceof PotionOfHealing){
				return true;
			}
			return false;
		}
	},
	NO_HERBALISM("no_herbalism"){
		@Override
		protected boolean _isItemBlocked(Item item) {
			if (item instanceof Dewdrop) {
				return true;
			}
			return false;
		}
	},
	SWARM_INTELLIGENCE("swarm_intelligence",true),
	DARKNESS("darkness",true),
	NO_SCROLLS("no_scrolls"),
	AMNESIA("amnesia",true),
	CURSED("cursed"),
	BLACKJACK("blackjack"),
	HORDE("horde",true){
		@Override
		protected float _nMobsMult(){
			return 2;
		}
	},
	COUNTDOWN("countdown"),
	ANALGESIA("analgesia"),
	BIG_LEVELS("big_levels"){
		@Override
		protected float _nMobsMult(){
			return 2;
		}

		@Override
		protected float _nTrapsMult() {
			return 2;
		}
	},
	MUTAGEN("mutagen",true),
	RESURRECTION("resurrection",true),
	EXTREME_CAUTION("extreme_caution",true){
		@Override
		protected float _nTrapsMult() {
			return 4;
		}
	},
	EXTERMINATION("extermination");
	public int id;
	public String name;
	public boolean hell_enabled;

	Challenges(String name){
		id = (int) Math.pow(2,this.ordinal());
		this.name=name;
	}
	
	Challenges(String name, boolean hell_enabled){
		this(name);
		this.hell_enabled=hell_enabled;
	}

	protected float _nMobsMult(){
		return 1;
	}

	protected float _nTrapsMult(){
		return 1;
	}

	public boolean enabled(){
		return Dungeon.isChallenged(this.id);
	}
	public boolean hell(){
		return Dungeon.isHellChallenged(this.id);
	}

	protected boolean _isItemBlocked( Item item ){
		return false;
	}

	public static float ascendingChance(){
		if(Statistics.amuletObtained)return .66f;
		return .33f;
	}
	
	public static float nMobsMultiplier(){
		float mult = 1;
		for (Challenges ch : values()){
			if (ch.enabled())mult*=ch._nMobsMult();
		}
		return mult;
	}
	public static float nTrapsMultiplier(){
		float mult = 1;
		for (Challenges ch : values()){
			if (ch.enabled())mult*=ch._nTrapsMult();
		}
		return mult;
	}
	public static boolean isItemBlocked( Item item ){
		for (Challenges ch : values()){
			if (ch.enabled()&&ch._isItemBlocked(item))return true;
		}
		return false;
	}
	public static int exterminatorsLeft(){
		int left = 0;
		if (EXTERMINATION.enabled())
			for (Mob m : Dungeon.level.mobs){
				if (m.buff(Extermanation.class)!=null)left++;
			}
		return left;
	}
	
	public static int randomChallenges(){
		Challenges[] values = Challenges.values();
		
		Random.shuffle(values);
		
		int result = 0;
		
		for (int i=0;i<Random.Int(1,values.length);i++){
			result |= values[i].id;
		}
		
		return result;
	}

}