package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface AttackAmplificationBuff {
    enum Type {
        FLAT(0),
        MULTIPLY(1),
        FLAT_2(2);
        public final int value;

        Type(int value) {
            this.value = value;
        }
    }

    static int damageFactor(int damage, Collection<Buff> buffs) {
        List<AttackAmplificationBuff> set = new ArrayList<>();
        for (Buff buff : buffs) {
            if (buff instanceof AttackAmplificationBuff) {
                set.add((AttackAmplificationBuff) buff);
            }
        }
        Collections.sort(set, (a, b) -> a.damageFactorPriority().value - b.damageFactorPriority().value);
        float dmg = damage;
        for (AttackAmplificationBuff value : set) {
            dmg = value.damageFactor(dmg);
        }
        return (int)dmg;
    }

    static String damageFormula(Collection<Buff> buffs) {
        float f1 = 0;
        float m1 = 1;

        float f2 = 0;
        for (Buff buff : buffs) {
            if (buff instanceof AttackAmplificationBuff) {
                AttackAmplificationBuff b = (AttackAmplificationBuff) buff;
                switch (b.damageFactorPriority()) {
                    case FLAT:
                        f1 = b.damageFactor(f1);
                        break;
                    case MULTIPLY:
                        m1 = b.damageFactor(m1);
                        break;
                    case FLAT_2:
                        f2 = b.damageFactor(f2);
                        break;
                }
            }
        }

        return Messages.get(AttackAmplificationBuff.class, "formula", (int) f1, m1, (int) f2);
    }

    default Type damageFactorPriority() {
        return Type.MULTIPLY;
    }

    float damageFactor(float dmg);
}
