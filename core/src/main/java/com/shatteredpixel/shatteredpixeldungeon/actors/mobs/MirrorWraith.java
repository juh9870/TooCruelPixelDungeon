/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GrowingRage;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WraithSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

public class MirrorWraith extends Mob {

    private static final float SPAWN_DELAY = 2f;
    private static final float LIFETIME = 10f;
    private static final String LEVEL = "level";
    private static final String MULT = "dmg_multiplier";
    private static final String TIMER = "die_timer";
    private float lifetime;
    private int level;
    private float forceMult = 1f;

    {
        spriteClass = MirrorWraithSprite.class;

        HP = HT = 1;
        EXP = 0;

        lifetime = LIFETIME;

        maxLvl = -2;

        flying = true;

        properties.add(Property.UNDEAD);
    }


    public static MirrorWraith spawnAt(int pos, int overkill) {
        if (!Dungeon.level.solid[pos] && Actor.findChar(pos) == null) {
            int level = Dungeon.hero.lvl;
            boolean alive = Dungeon.hero.isAlive();
            MirrorWraith w = new MirrorWraith();

            if (Challenges.SPIRITUAL_CONNECTION.enabled() && alive) {
                w.forceMult = Dungeon.hero.buff(GrowingRage.class).multiplier();
                w.lifetime *= (w.forceMult - 1) * 2 + 1;
            }

            w.adjustStats(level);
            if (Challenges.ECTOPLASM.enabled()) {
                w.HP = w.HT = (int) ((1 + level) * 2 * w.forceMult);
            }
            if (Challenges.SPIRITUAL_CONNECTION.enabled()) {
                w.HP += overkill;
                w.HT += overkill;
            }

            w.pos(pos);
            w.state = w.HUNTING;
            GameScene.add(w, SPAWN_DELAY);

            w.sprite.alpha(0);
            w.sprite.parent.add(new AlphaTweener(w.sprite, 1, 0.5f));

            w.sprite.emitter().burst(ShadowParticle.CURSE, 5);

            return w;
        } else {
            return null;
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, level);
        bundle.put(MULT, forceMult);
        bundle.put(TIMER, lifetime);
    }

    @Override
    public void damage(int dmg, Object src) {
        if (Challenges.ECTOPLASM.enabled()) dmg *= forceMult;
        if (Dungeon.hero.isAlive()) {
            Dungeon.hero.damage(dmg, Dungeon.hero);
            if (!Dungeon.hero.isAlive()) {
                Dungeon.fail(getClass());
                GLog.n(Messages.get(this, "hero_kill"));
            }
        }
        super.damage(dmg, src);
    }

    @Override
    public float speed() {
        return Dungeon.hero.isAlive() ? Dungeon.hero.speed() : super.speed();
    }

    @Override
    protected boolean act() {
        if (lifetime <= 0) {
            die(this);
            return true;
        }
        return super.act();
    }

    private float lifetimeMultiplier() {
        float mult = 1;
        if (Challenges.SPIRITUAL_CONNECTION.enabled()) {
            float p = (float) HP / HT;
            if (p >= 0.5f) {
                // https://www.desmos.com/calculator/d3y5ojeiu8
                mult = (float) (1 / (Math.pow(10 * (p - 0.5f), 2) + 1));
            }
        }
        mult = GameMath.gate(0, mult, 1);
        return mult;
    }

    @Override
    protected void spend(float time) {
        if (!Dungeon.hero.isAlive() || Dungeon.hero.buff(TimekeepersHourglass.timeStasis.class) == null) {
            lifetime -= time * lifetimeMultiplier();
        }
        super.spend(time);
    }

    @Override
    public void die(Object cause) {
        if (cause != this && Challenges.SPIRITUAL_CONNECTION.enabled() && Dungeon.hero.isAlive()) {
            Dungeon.hero.buff(GrowingRage.class).slain++;
        }
        super.die(cause);
    }

    @Override
    public boolean canAscend() {
        return false;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        level = bundle.getInt(LEVEL);
        forceMult = bundle.getFloat(MULT);
        lifetime = bundle.getFloat(TIMER);
        adjustStats(level);
    }

    @Override
    public String description() {
        float time = lifetime / lifetimeMultiplier();
        String display;
        if (time == Float.POSITIVE_INFINITY) {
            display = "???";
        } else {
            display = Long.toString((long) Math.ceil(1d * time + 1));
        }
        return Messages.get(this, "desc", display);
    }

    @Override
    public int damageRoll() {
        return Math.round(Random.NormalIntRange(1 + level / 2, 2 + level) * forceMult);
    }

    @Override
    public int attackSkill(Char target) {
        return Math.round((10 + level) * forceMult);
    }

    @Override
    public int defenseSkill(Char enemy) {
        return 0;
    }

    public void adjustStats(int level) {
        this.level = level;
        enemySeen = true;
    }

    @Override
    public float spawningWeight() {
        return 0f;
    }

    @Override
    public boolean reset() {
        state = WANDERING;
        return true;
    }


    public static class MirrorWraithSprite extends WraithSprite {

        public MirrorWraithSprite() {
            super();
            hardlight(0x888800);
        }

        @Override
        public void resetColor() {
            super.resetColor();
            hardlight(0x888800);
        }
    }
}
