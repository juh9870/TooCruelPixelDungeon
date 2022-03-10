package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class Universal extends Weapon.Enchantment {
    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);
    private final List<Weapon.Enchantment> additional;

    public Universal() {
        additional = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            additional.add(Weapon.Enchantment.randomCurse(
                    ListUtils.map(additional, Weapon.Enchantment::getClass)
            ));
        }
    }

    @Override
    public boolean curse() {
        return true;
    }

    public boolean hasEnchant(Class<? extends Weapon.Enchantment> type, Char owner) {
        for (Weapon.Enchantment enchantment : additional) {
            if (enchantment.getClass() == type) return owner.buff(MagicImmune.class) == null;
        }
        return false;
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
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        for (Weapon.Enchantment enchantment : additional) {
            damage = enchantment.proc(weapon, attacker, defender, damage);
        }
        return damage;
    }

    @Override
    public String name(String weaponName) {
        for (Weapon.Enchantment enchantment : additional) {
            weaponName = enchantment.name(weaponName);
        }
        return super.name(weaponName);
    }

    @Override
    public String desc() {
        return super.desc() + "\n" + ListUtils.join(additional, "\n", Weapon.Enchantment::desc);
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

    public static final String ADDITIONAL = "additional";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ADDITIONAL, additional);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        additional.clear();
        for (Bundlable bundlable : bundle.getCollection(ADDITIONAL)) {
            additional.add((Weapon.Enchantment) bundlable);
        }
    }
}
