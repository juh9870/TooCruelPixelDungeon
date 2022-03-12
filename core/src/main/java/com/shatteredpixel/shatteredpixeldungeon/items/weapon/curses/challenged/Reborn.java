package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class Reborn extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		if ( !(attacker instanceof Hero) ) return damage;
		if ( weapon.unique || ((Hero) attacker).belongings.weapon != weapon ) {
			weapon.enchant( Weapon.Enchantment.randomCurse( Reborn.class ) );
		} else {
			((Hero) attacker).belongings.weapon = (Weapon) ScrollOfTransmutation.changeItem( weapon );
			GameScene.showItem( ((Hero) attacker).belongings.weapon );
		}
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}
}