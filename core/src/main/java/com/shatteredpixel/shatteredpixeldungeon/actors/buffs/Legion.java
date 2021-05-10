package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Legion extends Buff {

    private static final float WAVE_DELAY = 200;
    private static final float LOCK_TIME = 20;
    private static final float PROGRESS_PERCENTAGE = 0.20f;
    private static final String WAVE_TURNS = "wave_turns";
    private static final String LAST_WAVE_TURNS = "last_wave_turns";
    private float turnsToNextWave = WAVE_DELAY;
    private float turnsSinceLastWave = 0;

    @Override
    public boolean act() {
        if (!Dungeon.bossLevel() && Dungeon.depth != 21) {
            turnsToNextWave -= TICK * Random.Float(.9f, 1.1f);

//			GLog.i(Math.floor(turnsToNextWave)+"");

            if (turnsToNextWave <= 0 || turnsSinceLastWave >= WAVE_DELAY) {
                turnsToNextWave = WAVE_DELAY;
                if (Challenges.MANIFESTING_MYRIADS.enabled()) {
                    turnsToNextWave/=2f;
                }
                int wantSpawn = waveSize();
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob.alignment == Char.Alignment.ENEMY) wantSpawn--;
                }

                if (wantSpawn > 0) {
                    GLog.n(Messages.get(Challenges.class, "horde_wave"));
                }

                while (wantSpawn > 0) {
                    Mob mob = Dungeon.level.createMob();
                    if (Challenges.MANIFESTING_MYRIADS.enabled()) {
                        Buff.affect(mob, NoReward.class);
                    }
                    mob.state = mob.WANDERING;
                    mob.pos = Dungeon.level.randomRespawnCell(mob);
                    if (Dungeon.hero.isAlive() && mob.pos != -1 && Dungeon.level.distance(Dungeon.hero.pos, mob.pos) >= 4) {
                        GameScene.add(mob);
                        wantSpawn--;
                        mob.beckon(Dungeon.hero.pos);
                    }
                }
                turnsToNextWave = WAVE_DELAY;
                if (Challenges.MANIFESTING_MYRIADS.enabled()) turnsToNextWave /= 2;
                turnsSinceLastWave = 0;
            }
        }
        spend(TICK);
        turnsSinceLastWave += TICK;
        return true;
    }

    private int waveSize() {
        int wantSpawn = (int) (Dungeon.level.nMobs() * (Statistics.amuletObtained ? 2 : 1.5));
        if (wantSpawn == 0 && Dungeon.depth == 1) wantSpawn = (int) (15 * Challenges.nMobsMultiplier());
        return wantSpawn;
    }

    public void consumeDeath() {
        float progress = Math.round(WAVE_DELAY / 2 / (waveSize() * PROGRESS_PERCENTAGE));
        turnsToNextWave += progress;
    }

    public float sealTime() {
        return LOCK_TIME * (Challenges.MANIFESTING_MYRIADS.enabled() ? 1.5f : 1) - turnsSinceLastWave;
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
