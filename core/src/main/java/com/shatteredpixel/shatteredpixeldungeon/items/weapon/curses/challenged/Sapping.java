package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class Sapping extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	private static final float FACTOR = 1.5f;

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		damage *= FACTOR;
		Buff.affect( defender, Viscosity.DeferedDamage.class ).prolong( damage );
		defender.sprite.showStatus( Temporal.DAMAGE_COLOR, Integer.toString( damage ) );
		return 0;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}
}