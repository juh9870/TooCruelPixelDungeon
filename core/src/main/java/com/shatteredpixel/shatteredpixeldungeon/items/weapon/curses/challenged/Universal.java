package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.ListUtils;
import com.watabou.utils.UnorderedPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Universal extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@SuppressWarnings("unchecked")
	private static final UnorderedPair<Class<? extends Weapon.Enchantment>>[] incompatibles = new UnorderedPair[]{
			new UnorderedPair<>( Temporal.class, Sapping.class )
	};

	private final List<Weapon.Enchantment> additional;

	public Universal() {
		additional = new ArrayList<>();
		generateEnchantments();
	}

	private void generateEnchantments() {
		additional.clear();
		int limit = 3;
		for (int i = 0; i < limit; i++) {
			Set<Class<? extends Weapon.Enchantment>> conflicts = incompatibles( false );
			Weapon.Enchantment enchantment = Weapon.Enchantment.randomCurse(
					conflicts
			);
			if ( conflicts.contains( enchantment.getClass() ) ) return;
			if ( enchantment instanceof Universal ) {
				limit += 3;
				// Just for display
				((Universal) enchantment).set();
			}
			additional.add( enchantment );
		}
	}

	public Universal set( Weapon.Enchantment... enchants ) {
		additional.clear();
		additional.addAll( Arrays.asList( enchants ) );
		return this;
	}

	@SuppressWarnings("SameParameterValue")
	private Set<Class<? extends Weapon.Enchantment>> incompatibles( boolean allowDuplicates ) {

		List<Class<? extends Weapon.Enchantment>> classes = ListUtils.map( additional, Weapon.Enchantment::getClass );
		Set<Class<? extends Weapon.Enchantment>> banned = new HashSet<>();
		if ( !allowDuplicates ) banned.addAll( classes );

		for (UnorderedPair<Class<? extends Weapon.Enchantment>> conflict : incompatibles) {
			if ( classes.contains( conflict.first ) ) {
				banned.add( conflict.second );
			}
			if ( classes.contains( conflict.second ) ) {
				banned.add( conflict.first );
			}
		}
		return banned;
	}

	@Override
	public boolean curse() {
		return true;
	}

	public boolean hasEnchant( Class<? extends Weapon.Enchantment> type, Char owner ) {
		for (Weapon.Enchantment enchantment : additional) {
			if ( enchantment.getClass() == type ) return owner.buff( MagicImmune.class ) == null;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T extends Weapon.Enchantment> T getEnchant( Class<? extends T> type ) {
		for (Weapon.Enchantment enchantment : additional) {
			if ( enchantment.getClass() == type ) return (T) enchantment;
		}
		return null;
	}

	@Override
	public int levelBonus() {
		int bonus = 0;
		for (Weapon.Enchantment enchantment : additional) {
			bonus += enchantment.levelBonus();
		}
		return bonus + 2;
	}

	@Override
	public int tierBonus() {
		int bonus = 0;
		for (Weapon.Enchantment enchantment : additional) {
			bonus += enchantment.tierBonus();
		}
		return bonus;
	}

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		for (Weapon.Enchantment enchantment : additional) {
			damage = enchantment.proc( weapon, attacker, defender, damage );
		}
		return damage;
	}

	@Override
	public String name( String weaponName ) {
		for (Weapon.Enchantment enchantment : additional) {
			weaponName = enchantment.name( weaponName );
		}
		return super.name( weaponName );
	}

	@Override
	public String desc() {
		return super.desc() + "\n" + ListUtils.join( additional, "\n", Weapon.Enchantment::desc );
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

	public static final String ADDITIONAL = "additional";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ADDITIONAL, additional );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		additional.clear();
		for (Bundlable bundlable : bundle.getCollection( ADDITIONAL )) {
			additional.add( (Weapon.Enchantment) bundlable );
		}
	}
}
