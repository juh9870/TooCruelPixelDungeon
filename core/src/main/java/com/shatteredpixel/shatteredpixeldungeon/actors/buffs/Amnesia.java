package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Amnesia extends Buff {
	
	@Override
	public boolean act() {
		
		ArrayList<Integer> cells = new ArrayList<>();
		
		for(int i=0;i<Dungeon.level.length();i++){
			if(Dungeon.level.mapped[i])cells.add(i);
		}
		
		if(cells.size()>0) {
			Random.shuffle(cells);
			
			int size = cells.size();
			boolean fullUpdate = size>10;
			int toDelete = Math.max(Random.NormalIntRange((int)Math.floor(size/3f),(int)Math.ceil(size/2f)),Math.min(size,3));
			while(toDelete-->0){
				int cell = cells.remove(--size);
				Dungeon.level.mapped[cell]=false;
				Dungeon.level.needUpdateFog[cell]=true;
				if(!fullUpdate)GameScene.updateFog(cell,1);
			}
			if(fullUpdate)GameScene.updateFog();
		}
		
		
		spend(TICK);
		return true;
	}
	
	@Override
	public void postpone(float time) {
		super.postpone(time);
	}
}
