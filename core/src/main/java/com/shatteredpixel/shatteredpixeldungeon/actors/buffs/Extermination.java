package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;

public class Extermination extends Buff {
	{
		type=buffType.NEUTRAL;
	}
	
	@Override
	public void fx(boolean on) {
		if(on) target.sprite.add(CharSprite.State.EXTERMINATING);
		else target.sprite.remove(CharSprite.State.EXTERMINATING);
	}
}
