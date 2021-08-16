package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public interface AttackAmplificationBuff {
    static int damageFactor(int damage, Collection<Buff> buffs) {
        SortedSet<AttackAmplificationBuff> set = new TreeSet<>((a, b) -> a.damageFactorPriority() - b.damageFactorPriority());
        for (Buff buff : buffs) {
            if (buff instanceof AttackAmplificationBuff) {
                set.add((AttackAmplificationBuff) buff);
            }
        }
        for (AttackAmplificationBuff value : set) {
            damage = value.damageFactor(damage);
        }
        return damage;
    }

    default int damageFactorPriority() {
        return 0;
    }

    int damageFactor(int dmg);
}
