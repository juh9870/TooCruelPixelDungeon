package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Intoxication extends Buff {
    public static final float POION_INTOXICATION = 55;
    public static final float EXOTIC_INTOXICATION = 80;
    private static final float BASE = 50;
    private static final float DANGER_1 = BASE * 2f;
    private static final float DANGER_2 = BASE * 3f;
    private static final float DANGER_3 = BASE * 4f;
    private static final float DANGER_4 = BASE * 5f;
    private static final String LEVEL = "level";
    protected float level = 0;

    {
        type = buffType.NEGATIVE;
    }

    public static void applyMajor(Char targ) {
        switch (Random.Int(6)) {
            case 0:
                Buff.affect(targ, Corrosion.class).set(targ.HT / 15, targ.HT / 20);
                break;
            case 1:
                Buff.prolong(targ, Paralysis.class, Random.NormalFloat(7, 13));
                break;
            case 2:
                Buff.prolong(targ, Weakness.class, Random.NormalFloat(15, 25));
                break;
            case 3:
                Buff.prolong(targ, Frost.class, Random.NormalFloat(15, 25));
                break;
            case 4:
                Buff.prolong(targ, Slow.class, Random.NormalFloat(20, 35));
                break;
            case 5:
                Buff.prolong(targ, Degrade.class, Random.NormalFloat(15, 25));
                break;
        }

    }

    public static void applyMinor(Char targ) {
        switch (Random.Int(6)) {
            case 0:
                Buff.affect(targ, Vulnerable.class, Random.Int(9, 15));
                break;
            case 1:
                Buff.prolong(targ, Blindness.class, Random.Int(9, 15));
                break;
            case 2:
                Buff.affect(targ, Chill.class, Random.NormalIntRange(7, 13));
                break;
            case 3:
                if (Challenges.RACING_THE_DEATH.enabled()) applyMinor(targ);
                else Buff.prolong(targ, Vertigo.class, Random.NormalFloat(7, 13));
                break;
            case 4:
                Buff.prolong(targ, Cripple.class, Random.NormalFloat(7, 13));
                break;
            case 5:
                Buff.prolong(targ, Roots.class, Random.NormalFloat(7, 13));
                break;
        }
    }

    @Override
    public boolean act() {
        if (level >= DANGER_1) {
            if (level > DANGER_2 || Random.Float() < (level - DANGER_1) / (DANGER_2 - DANGER_1)) {
                applyMinor(target);
            }

            if (level >= DANGER_2) {
                if (level > DANGER_3 || Random.Float() < (level - DANGER_3) / (DANGER_3 - DANGER_2)) {
                    applyMajor(target);
                }
                if (level > DANGER_3 || Random.Float() < (level - DANGER_3) / (DANGER_3 - DANGER_2)) {
                    applyMinor(target);
                }

                if (level >= DANGER_3) {
                    if (level > DANGER_4 || Random.Float() < (level - DANGER_3) / (DANGER_4 - DANGER_3)) {
                        applyMajor(target);
                    }
                }
            }
        }
        int rnd = Random.Int(4, 6);

        if (level <= DANGER_2) rnd *= 1.6f;
        else if (level <= DANGER_3) rnd *= 1.2f;

        level -= rnd;
        if (level <= 0) detach();
        spend(rnd);
        return true;
    }

    public String toxicLevel() {
        String[] levels = new String[]{
                "t_none",
                "t_light",
                "t_medium",
                "t_heavy",
                "t_deadly",
        };
        String l;
        if (level > DANGER_4) l = levels[4];
        else if (level > DANGER_3) l = levels[3];
        else if (level > DANGER_2) l = levels[2];
        else if (level > DANGER_1) l = levels[1];
        else l = levels[0];
        return Messages.get(this, l);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", (int) level, toxicLevel());
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public int icon() {
        return BuffIndicator.INTOXICATION;
    }

    public void set(float level) {
        this.level = Math.max(this.level, level);
    }

    public void extend(float duration) {
        this.level += duration;
    }

    public void processHit(int damage, Object source) {

        if (damage <= 0) return;

        if (source instanceof Hunger ||
                source instanceof Viscosity.DeferedDamage ||
                source instanceof Burning ||
                source instanceof Blob ||
                source instanceof Countdown ||
                source instanceof RacingTheDeath) return;

        float power = 1f * damage / target.HT;

        //losing 25% hp equal to 1 base toxic level
        power *= 4 * BASE;

        power /= 1.5;

        //extra intoxication equal to received damage
        power += damage;

        extend(power);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, level);

    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        level = bundle.getInt(LEVEL);
    }
}
