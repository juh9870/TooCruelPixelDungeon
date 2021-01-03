package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.watabou.utils.Bundle;

import java.util.Arrays;
import java.util.HashSet;

public class Ascension extends Buff {
	
	public int level = 0;
	private static final String LEVEL = "level";
	
	@Override
	public HashSet<Class> immunities() {
		HashSet<Class> immunities = super.immunities();
		
		if(level>=2)
			immunities.addAll(Arrays.asList(Corruption.class, StoneOfAggression.Aggression.class));
		if(level>=4)
			immunities.addAll(Arrays.asList(Grim.class, GrimTrap.class, ScrollOfRetribution.class, ScrollOfPsionicBlast.class));
		
		return immunities;
	}
	
	public HashSet<Char.Property> props(){
		HashSet<Char.Property> props = new HashSet<>();
		
		if(level>=3){
			props.add(Char.Property.INORGANIC);
		}
		
		if(level>=4){
			props.add(Char.Property.BLOB_IMMUNE);
			props.add(Char.Property.FIERY);
			props.add(Char.Property.ICY);
			props.add(Char.Property.ACIDIC);
			props.add(Char.Property.ELECTRIC);
		}
		
		return props;
	}
	
	@Override
	public boolean act() {
		if(Challenges.RESURRECTION.tier(3)) {
			if (level >= 1 && level < 3)
				Buff.affect(target, Stamina.class, 1000);
			if (level >= 2) {
				Buff.affect(target, Bless.class, 1000);
			}
			if (level >= 3) {
				Buff.affect(target, Levitation.class, 1000);
				Buff.affect(target, Adrenaline.class, 1000);
			}
			if (level >= 4) {
				Buff.affect(target, ToxicImbue.class).left = 1000;
				Buff.affect(target, EarthImbue.class, 1000);
				Buff.affect(target, FireImbue.class).left = 1000;
				Buff.affect(target, FrostImbue.class, 1000);
			}
			if (level >= 5) {
				Buff.affect(target, Godspeed.class, 1000);
			}
			if (level >= 6) {
				Buff.affect(target, Barrier.class).setShield(target.HT / 32);
			}
		}
		spend(TICK);
		return true;
	}
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(LEVEL,level);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		level=bundle.getInt(LEVEL);
	}
}
