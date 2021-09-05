package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class PermaBlind extends Buff {
	
	{
		actPriority = HERO_PRIO-1;
	}
	
	private static final int CONFUSE_TIMEOUT = 3;
	private int turnsToConfuse = CONFUSE_TIMEOUT;
	@Override
	public boolean act() {
		boolean weak = true;
		for (int c : PathFinder.NEIGHBOURS8){
			int cell = target.pos() +c;
			if(Dungeon.level.solid[cell]||Dungeon.level.pit[cell]) {
				weak = false;
				turnsToConfuse = Math.min(turnsToConfuse+1,CONFUSE_TIMEOUT);
			}
		}
		if(weak){
			Buff.prolong(target,Blindness.class,1);
			if(--turnsToConfuse<=0){
				Buff.prolong(target,Vertigo.class,1);
			}
		}
		spend(TICK);
		return true;
	}
	
	
	private static final String CONFUSE = "confuse";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CONFUSE,turnsToConfuse);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		turnsToConfuse=bundle.getInt(CONFUSE);
	}
}
