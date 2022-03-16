package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursedWandTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.HashSet;
import java.util.Set;

public class Trapping extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	private static final float CHANCE = 0.25f;

	@Override
	public boolean curse() {
		return true;
	}


	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		if ( Random.Float() < CHANCE * procChanceMultiplier( attacker ) ) {
			Set<Integer> validCells = new HashSet<>();
			for (int i : PathFinder.NEIGHBOURS8) {
				int pos = defender.pos() + i;
				if ( Dungeon.level.insideMap( pos ) && Dungeon.level.canPlaceTrap( pos ) && !Dungeon.level.hasActiveTrap( pos ) ) {
					validCells.add( pos );
				}
			}
			if ( validCells.size() > 0 ) {
				int target = Random.element( validCells );
				Trap t;
				do {
					t = Trap.adjustTrap( Trap.randomTrap() );
				} while (t instanceof CursedWandTrap);
				if ( t.canBeHidden ) t.hide();
				Trap.TrapTrigger.plant( target, t, 2f, true );
			}
		}
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}
}