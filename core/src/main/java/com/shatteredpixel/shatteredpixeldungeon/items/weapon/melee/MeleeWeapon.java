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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged.Erratic;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.function.Lazy;

public class MeleeWeapon extends Weapon {
	
	private int tier;
	private final Lazy<Integer> tierBonus = tierBonus(0, this::tier);

	@Override
	public int min(int lvl) {
		return  buffedTier() +  //base
				lvl;    //level scaling
	}

	@Override
	public int max(int lvl) {
		return  5*(buffedTier() +1) +    //base
				lvl*(buffedTier() +1);   //level scaling
	}

	public int STRReq(int lvl){
		return STRReq(strTier(), lvl);
	}
	
	@Override
	public int damageRoll(Char owner) {
		int damage = augment.damageFactor(super.damageRoll( owner ));

		if (owner instanceof Hero) {
			int exStr = ((Hero)owner).STR() - STRReq();
			if (exStr > 0) {
				damage += Random.IntRange( 0, exStr );
			}
		}
		
		return damage;
	}

	@Override
	protected float baseDelay(Char owner) {
		float delay = super.baseDelay(owner);
		delay *= Erratic.delayMultiplier(owner, this);
		return delay;
	}

	@Override
	public String info() {

		String info = desc();

		if (levelKnown) {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", buffedTier(), augment.damageFactor(min()), augment.damageFactor(max()), STRReq());
			if (STRReq() > Dungeon.hero.STR()  && !Challenges.ANALGESIA.enabled()) {
				info += " " + Messages.get(Weapon.class, "too_heavy");
			} else if (Dungeon.hero.STR() > STRReq() && !Challenges.ANALGESIA.enabled()){
				info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
			}
		} else {
			info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", buffedTier(), min(0), max(0), STRReq(0));
			if (STRReq(0) > Dungeon.hero.STR() && !Challenges.ANALGESIA.enabled()) {
				info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
			}
		}

		String statsInfo = statsInfo();
		if (!statsInfo.equals("")) info += "\n\n" + statsInfo;

		switch (augment) {
			case SPEED:
				info += " " + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += " " + Messages.get(Weapon.class, "stronger");
				break;
			case NONE:
		}

		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			info += " " + enchantment.desc();
		}

		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
		}
		
		return info;
	}
	
	public String statsInfo(){
		return Messages.get(this, "stats_desc");
	}
	
	@Override
	public int value() {
		int price = 20 * buffedTier();
		if (hasGoodEnchant()) {
			price *= 1.5;
		}
		if (cursedKnown && (cursed || hasCurseEnchant())) {
			price /= 2;
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	@Override
	public boolean isSimilar(Item item) {
		return super.isSimilar(item) && ((MeleeWeapon) item).buffedTier() == buffedTier();
	}

	private static final String TIER = "tier";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TIER, tier());
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(TIER))
			tier(bundle.getInt(TIER));
	}

	@Override
	public int buffedLvl() {
		int lvl = super.buffedLvl();
		if (Challenges.UNTIERED.enabled()) lvl -= tierBonus.get();
		if (enchantment != null) lvl += enchantment.levelBonus();
		return lvl;
	}

	public int buffedTier() {
		int t = tier() + tierBonus.get();
		if (enchantment != null) {
			t += enchantment.tierBonus();
		}
		return t;
	}

	public int tier() {
		return tier;
	}

	public int strTier() {
		if (Challenges.UNTIERED.enabled())
			return tier();
		return buffedTier();
	}

	public void tier(int tier) {
		this.tier = tier;
	}
}
