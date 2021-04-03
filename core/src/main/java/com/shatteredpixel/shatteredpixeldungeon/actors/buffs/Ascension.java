package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

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
	
	@Override
	public void modifyProperties(HashSet<Char.Property> properties) {
		
		if(level>=3){
			properties.add(Char.Property.INORGANIC);
		}
		
		if(level>=4){
			properties.add(Char.Property.BLOB_IMMUNE);
			properties.add(Char.Property.FIERY);
			properties.add(Char.Property.ICY);
			properties.add(Char.Property.ACIDIC);
			properties.add(Char.Property.ELECTRIC);
		}
	}
	
	@Override
	public boolean act() {
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
			Buff.affect(target, FireImbue.class).left = 1000;
			Buff.affect(target, FrostImbue.class, 1000);
		}
		if (level >= 5) {
			Buff.affect(target, Godspeed.class, 1000);
		}
		if(level==5){
			Buff.affect(target, ForcedAscension.class, 2f);
		}
		if (level >= 6) {
			Buff.affect(target, Barrier.class).setShield(target.HT / 32);
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
	@Override
	public void fx(boolean on) {
		if(on){
			target.sprite.aura(getClass(),0xFF0000,0.5f,true);
		} else {
			target.sprite.clearAura(getClass());
		}
	}
	
	public static class ForcedAscension extends FlavourBuff {
	}

	public static class BannedAscension extends Buff {
	}
}
