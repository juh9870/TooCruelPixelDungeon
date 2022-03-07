package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Legion extends Buff {
    private static final float WAVE_DELAY = 200;
    private static final float LOCK_TIME = 20;
    private static final float PROGRESS_PERCENTAGE = 0.3f;
    private static final String WAVE_TURNS = "wave_turns";
    private static final String LAST_WAVE_TURNS = "last_wave_turns";
    private float turnsToNextWave = WAVE_DELAY;
    private float turnsSinceLastWave = 0;

    {
        type = buffType.NEUTRAL;
    }

    private float delay() {
        if (Challenges.MANIFESTING_MYRIADS.enabled()) return WAVE_DELAY / 2f;
        return WAVE_DELAY;
    }

    @Override
    public boolean act() {
        if (!Dungeon.bossLevel()) {
            turnsToNextWave -= TICK;
            turnsSinceLastWave += TICK;

//			GLog.i(Math.floor(turnsToNextWave)+"");

            if (turnsToNextWave <= 0 || turnsSinceLastWave >= delay()) {
                turnsToNextWave = delay();
                int wantSpawn = waveSize();
                for (Mob mob : Dungeon.level.mobs()) {
                    if (mob.alignment == Char.Alignment.ENEMY) wantSpawn -= mob.spawningWeight();
                }

                if (wantSpawn > 0) {
                    GLog.n(Messages.get(Challenges.class, "horde_wave"));
                }

                while (wantSpawn > 0) {
                    int nMobs = Math.min(50, wantSpawn);
                    wantSpawn -= nMobs;
                    int pos = Dungeon.level.randomRespawnCell(null, true);
                    SummoningTrap.summonMobs(pos, nMobs, 5,
                            new SummoningTrap.MobSpawnedAction.HeroBeckoner());
                }
                turnsSinceLastWave = 0;
            }
        }
        spend(TICK);
        return true;
    }

    private int waveSize() {
        int wantSpawn = (int) (Dungeon.level.nMobs() * (Statistics.amuletObtained ? 2 : 1.5));
        if (wantSpawn == 0 && Dungeon.depth().firstLevel()) wantSpawn = (int) (15 * Challenges.nMobsMultiplier());
        return wantSpawn;
    }

    public void consumeDeath() {
        float progress = Math.round(delay() / 2 / (waveSize() * PROGRESS_PERCENTAGE));
        turnsToNextWave += progress;
        if (turnsToNextWave > WAVE_DELAY - turnsSinceLastWave) {
            turnsToNextWave = WAVE_DELAY - turnsSinceLastWave;
        } else if (Dungeon.hero.isAlive() && Math.round(progress) > 0) {
            Dungeon.hero.sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, "delay", Math.round(progress)));
        }
    }

    public float sealTime() {
        return LOCK_TIME * (Challenges.MANIFESTING_MYRIADS.enabled() ? 1.5f : 1) - turnsSinceLastWave;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", Math.round(Math.min(delay() - turnsSinceLastWave, turnsToNextWave)));
    }

    @Override
    public int icon() {
        return BuffIndicator.WEAPON;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0xFFFF00);
    }

    @Override
    public float iconFadePercent() {
        return 1f - Math.min(delay() - turnsSinceLastWave, turnsToNextWave) / delay();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WAVE_TURNS, turnsToNextWave);
        bundle.put(LAST_WAVE_TURNS, turnsSinceLastWave);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        turnsToNextWave = bundle.getFloat(WAVE_TURNS);
        turnsSinceLastWave = bundle.getFloat(LAST_WAVE_TURNS);
    }
}
