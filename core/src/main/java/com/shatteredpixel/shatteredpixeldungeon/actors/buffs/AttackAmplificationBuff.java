package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public interface AttackAmplificationBuff {
	default int damageFactorPriority(){
		return 0;
	}
	int damageFactor(int dmg);

	public static int damageFactor(int damage, Collection<Buff> buffs){
		SortedMap<Integer,AttackAmplificationBuff> map = new TreeMap<>();
		for (Buff buff : buffs) {
			if(buff instanceof AttackAmplificationBuff){
				map.put(((AttackAmplificationBuff) buff).damageFactorPriority(),(AttackAmplificationBuff)buff);
			}
		}
		for (AttackAmplificationBuff value : map.values()) {
			damage = value.damageFactor(damage);
		}
		return damage;
	}
}
