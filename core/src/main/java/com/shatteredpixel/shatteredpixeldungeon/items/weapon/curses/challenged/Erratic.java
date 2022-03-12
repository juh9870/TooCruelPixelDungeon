package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Misc;
import com.watabou.utils.Random;

public class Erratic extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public boolean curse() {
		return true;
	}

	private static float delay( int targets, boolean missile ) {
		if ( targets == 0 ) return 1f;
		if ( targets == 1 ) return 1.25f;
		if ( missile ) {
			return 1 - Math.min( targets - 1, 3 ) * 1f / 6;
		} else {
			return 1 - Math.min( targets - 1, 3 ) * 1f / 4;
		}
	}

	public static float delayMultiplier( Char owner, Weapon wep ) {
		if ( wep.hasEnchant( Erratic.class, owner ) ) {
			return delay( getValidTargetsInFov( owner, wep ).size(), false );
		}
		return 1f;
	}

	public static float missileDelayMultiplier( Char owner, MissileWeapon wep ) {
		boolean projecting = wep.hasEffectiveEnchant( Projecting.class, owner );
		if ( wep.hasEffectiveEnchant( Erratic.class, owner ) ) {
			return delay( getValidTargetsInFov( owner, ( ch ) -> wep.targetValid( owner, ch.pos(), projecting ) ).size(), true );
		}
		return 1f;
	}

	public static Char switchEnemy( Hero owner, Char curTarget ) {
		if ( !(owner.belongings.weapon() instanceof Weapon) ||
				!((Weapon) owner.belongings.weapon()).hasEnchant( Erratic.class, owner ) )
			return curTarget;
		Weapon wep = (Weapon) owner.belongings.weapon();
		return Misc.or( Random.element( getValidTargetsInFov( owner, wep ) ), curTarget );
	}

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		// Look at hero.actAttack or MissileWeapon.throwPos
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}
}
