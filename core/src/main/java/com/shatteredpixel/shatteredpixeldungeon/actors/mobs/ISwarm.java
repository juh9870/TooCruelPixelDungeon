package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ascension;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MMO;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;

public interface ISwarm {

    float SPLIT_DELAY = 1f;

    static <T extends Mob, U extends Buff & ISwarm> T split(T original, T clone) {
        return split(original, clone, null, (ISwarm) null);
    }

    static <T extends Mob, U extends Buff & ISwarm> T split(T original, T clone, ISwarm parentSwarm, Class<U> swarmBuffClass) {
        ISwarm swarm = null;
        if (swarmBuffClass != null) swarm = Buff.affect(clone, swarmBuffClass);
        return split(original, clone, parentSwarm, swarm);
    }

    static <T extends Mob, U extends Buff & ISwarm> T split(T original, T clone, ISwarm parentSwarm, ISwarm childSwarm) {
        if (parentSwarm == null && original instanceof ISwarm) {
            parentSwarm = (ISwarm) original;
        }
        if (childSwarm == null && clone instanceof ISwarm) {
            childSwarm = (ISwarm) clone;
        }
        if (parentSwarm != null && childSwarm != null) {
            childSwarm.setGeneration(parentSwarm.generation() + 1);
        }
        clone.EXP = 0;
        if (original.buff(Burning.class) != null) {
            Buff.affect(clone, Burning.class).reignite(clone);
        }
        if (original.buff(Poison.class) != null) {
            Buff.affect(clone, Poison.class).set(2);
        }
        if (original.buff(Corruption.class) != null) {
            Buff.affect(clone, Corruption.class);
        }
        if (original.buff(MMO.class) != null) {
            Buff.affect(clone, MMO.class);
        }

        if (Challenges.ELITE_CHAMPIONS.enabled()) {
            for (Buff buff : original.buffs()) {
                if (buff instanceof ChampionEnemy) {
                    Buff.affect(clone, buff.getClass());
                }
            }
        }

        Buff.affect(clone, Ascension.BannedAscension.class);
        return clone;
    }

    int generation();

    void setGeneration(int generation);
}
