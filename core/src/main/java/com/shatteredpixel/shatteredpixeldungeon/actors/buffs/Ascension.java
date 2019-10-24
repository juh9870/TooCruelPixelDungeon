package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;

public class Ascension extends Buff {
	@Override
	public boolean attachTo(Char target) {
		if(target.properties().contains(Char.Property.BOSS) ||
				target.properties().contains(Char.Property.MINIBOSS) ||
				target instanceof NPC ||
				target instanceof Statue ||
				target instanceof Hero
		) return false;
		return super.attachTo(target);
	}
}
