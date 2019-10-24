package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Intoxication extends Buff {
	public static final float POION_INTOXICATION = 55;
	public static final float EXOTIC_INTOXICATION = 80;
	private static final float BASE = 50;
	private static final float DANGER_1 = BASE *2f;
	private static final float DANGER_2 = BASE *3f;
	private static final float DANGER_3 = BASE *4f;
	private static final float DANGER_4 = BASE *5f;
	
	public static void applyMajor(Char targ){
		switch (Random.Int(5)){
			case 0:
				Buff.affect(targ,Corrosion.class).set(targ.HT/15,targ.HT/20);
				break;
			case 1:
				Buff.prolong(targ,Paralysis.class,10f);
				break;
			case 2:
				Buff.prolong(targ, Weakness.class,20f);
				break;
			case 3:
				Buff.prolong(targ, Frost.class,20f);
				break;
			case 4:
				Buff.prolong(targ, Slow.class,30f);
				break;
		}
		
	}
	public static void applyMinor(Char targ){
		switch (Random.Int(6)){
			case 0:
				Buff.affect(targ,Bleeding.class).set(targ.HT/10);
				break;
			case 1:
				Buff.prolong(targ,Blindness.class,15);
				break;
			case 2:
				Buff.affect(targ,Ooze.class).set(10);
				break;
			case 3:
				Buff.prolong(targ,Vertigo.class,10);
				break;
			case 4:
				Buff.prolong(targ,Cripple.class,10);
				break;
			case 5:
				Buff.prolong(targ,Roots.class,5);
				break;
		}
	}
	
	{
		type=buffType.NEGATIVE;
	}
	
	@Override
	public boolean act() {
		if (level>=DANGER_1){
			if (level>DANGER_2||Random.Float()<(level-DANGER_1)/(DANGER_2-DANGER_1)){
				applyMinor(target);
			}
			
			if (level>=DANGER_2){
				if (level>DANGER_3||Random.Float()<(level-DANGER_3)/(DANGER_3-DANGER_2)){
					applyMajor(target);
				}
				if (level>DANGER_3||Random.Float()<(level-DANGER_3)/(DANGER_3-DANGER_2)){
					applyMinor(target);
				}
				
				if (level>=DANGER_3){
					if (level>DANGER_4||Random.Float()<(level-DANGER_3)/(DANGER_4-DANGER_3)){
						applyMajor(target);
					}
				}
			}
		}
		int rnd = Random.Int(4,6);
		level-=rnd;
		if(level<=0)detach();
		spend(rnd);
		return true;
	}
	
	public String toxicLevel(){
		String[] levels = new String[]{
				"t_none",
				"t_light",
				"t_medium",
				"t_heavy",
				"t_deadly",
		};
		String l;
		if (level>DANGER_4)l=levels[4];
		else if (level>DANGER_3)l=levels[3];
		else if (level>DANGER_2)l=levels[2];
		else if (level>DANGER_1)l=levels[1];
		else l=levels[0];
		return Messages.get(this,l);
	}
	
	@Override
	public String desc() {
		return Messages.get(this,"desc",(int)level,toxicLevel());
	}
	
	@Override
	public String toString() {
		return Messages.get(this,"name");
	}
	
	@Override
	public int icon() {
		return BuffIndicator.INTOXICATION;
	}
	
	
	public void set( float level ) {
		this.level = Math.max(this.level, level);
	}
	
	public void extend( float duration ) {
		this.level += duration;
	}
	
	
	protected float level=0;
	
	private static final String LEVEL	= "level";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVEL, level );
		
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getInt( LEVEL );
	}
}
