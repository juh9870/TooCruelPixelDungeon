package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class Legendary extends BuffEnchantment {
    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public boolean curse() {
        return true;
    }

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        return damage;
    }

    @Override
    public int tierBonus() {
        return 1;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }

    @Override
    protected EnchantmentBuff buff() {
        return new Weak();
    }

    public static int strBonus(Hero hero) {
        if (hero.buff(Weak.class) != null) return -2;
        return 0;
    }

    public class Weak extends EnchantmentBuff {
    }
}
