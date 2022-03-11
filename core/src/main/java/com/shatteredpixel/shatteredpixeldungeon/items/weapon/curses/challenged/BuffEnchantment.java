package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

public abstract class BuffEnchantment extends Weapon.Enchantment {
	protected EnchantmentBuff buff = null;

	@Override
	public void activate( Char ch ) {
		super.activate( ch );
		buff = buff();
		buff.attachTo( ch );
	}

	@Override
	public void deactivate( Char ch ) {
		super.deactivate( ch );
		ch.remove( buff );
		buff = null;
	}

	protected abstract EnchantmentBuff buff();


	public class EnchantmentBuff extends Buff {
		@Override
		public boolean act() {

			spend( TICK );

			return true;
		}
	}
}
