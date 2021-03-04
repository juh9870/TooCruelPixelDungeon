package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.utils.Difficulty;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;


public class Modifiers implements Bundlable {
	public int[] challenges;
	
	public Modifiers(){
		challenges=new int[Challenges.values().length];
		clear();
	}
	
	public Modifiers(int[] challenges){
		this.challenges = challenges.clone();
	}
	
	public void clear(){
		int l = Challenges.values().length;
		for (int i = 0; i < l; i++) {
			challenges[i]=0;
		}
	}
	
	public boolean isChallenged(int id){
		return challengeTier(id)>0;
	}
	
	public boolean isChallenged(){
		for (Integer challenge : challenges) {
			if(challenge>0)return true;
		}
		return false;
	}
	
	public void randomize(long seed){
		Challenges[] values = Challenges.values();
		Random.pushGenerator(seed);
		Random.shuffle(values);
		
		for (int i=0;i<Random.Int(1,values.length);i++){
			challenges[values[i].ordinal()]=Random.Int(1,values[i].maxLevel+1);
		}
		
		Random.popGenerator();
	}
	
	public int challengeTier(int id){
		return challenges[id];
	}
	
	private static final String CHALLENGES = "challenges";
	
	public Difficulty difficulty(){
		return Difficulty.align(Difficulty.calculateDifficulty(this));
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		challenges=Challenges.fromString(bundle.getString(CHALLENGES));
	}
	
	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(CHALLENGES,Challenges.saveString(challenges));
	}
}
