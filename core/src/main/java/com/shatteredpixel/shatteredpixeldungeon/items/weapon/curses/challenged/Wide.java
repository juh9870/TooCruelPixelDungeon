package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Wide extends Weapon.Enchantment {
    private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing(0x000000);

    @Override
    public boolean curse() {
        return true;
    }

    private boolean processing = false;
    private static final float DAMAGE_FACTOR = 0.5f;
    private static final int TARGETS = 2;

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage) {
        damage *= DAMAGE_FACTOR;
        if (processing) return damage;
        processing = true;

        HashSet<Char> validTargets = new HashSet<>();

        int pos = attacker.pos();
        for (int i : PathFinder.NEIGHBOURS8) {
            int cell = pos + i;
            if (!Dungeon.level.insideMap(cell)) continue;
            Char ch = Actor.findChar(cell);
            if (ch == null || ch == defender) continue;
            validTargets.add(ch);
        }

        int hits = TARGETS;

        while (!validTargets.isEmpty() && hits > 0) {
            attacker.attack(Random.element(validTargets), 1f, 0f, 1f);
            hits--;
        }

        processing = false;

        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return BLACK;
    }
}
