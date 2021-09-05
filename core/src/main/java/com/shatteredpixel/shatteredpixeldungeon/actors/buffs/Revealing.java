package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class Revealing extends FlavourBuff {

	@Override
	public boolean attachTo(Char target) {
		if(super.attachTo(target)) {
			Dungeon.observe();
			GameScene.updateFog(target.pos(),2);
			return true;
		}
		return false;
	}
	@Override
	public void detach() {
		super.detach();
		Dungeon.observe();
		GameScene.updateFog(target.pos(),2);
	}
}
