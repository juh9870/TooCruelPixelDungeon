package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Desert;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class ScorchedEarth extends Buff {
    private static final float WATER_TIME = 20;
    private static final String LEFT = "turns_left";
    private float turnsLeft = maxTime();


    private static float maxTime() {
        return Challenges.DESERT.enabled() ? WATER_TIME * 1.5f : WATER_TIME;
    }

    public static void explode(Hero hero, Item toCurse) {
        new Bomb().explode(hero.pos());
        CursedWand.cursedEffect(null, hero, hero);
        boolean curse = false;
        if (toCurse instanceof Armor) {
            ((Armor) toCurse).inscribe(Armor.Glyph.randomCurse());
            curse = true;
        }
        if (toCurse instanceof MeleeWeapon) {
            ((MeleeWeapon) toCurse).enchant(Weapon.Enchantment.randomCurse());
            curse = true;
        }
        if (toCurse instanceof Wand) {
            curse = true;
        }
        if (curse) {
            toCurse.cursed = toCurse.cursedKnown = true;
        }
    }

    private boolean validTile(int pos) {
        if (Challenges.TOUCH_THE_GRASS.enabled()) {
            int tile = Dungeon.level.map[pos];
            return tile == Terrain.GRASS || tile == Terrain.HIGH_GRASS || tile == Terrain.FURROWED_GRASS;
        }
        return Dungeon.level.water[pos];
    }

    @Override
    public boolean act() {
        if (validTile(target.pos())) {
            turnsLeft = maxTime();
        } else {
            if (turnsLeft <= 0) {
                turnsLeft += maxTime();
                for (int i : PathFinder.NEIGHBOURS4) {
                    int c = target.pos() + i;
                    if (Dungeon.level.flamable[c] || Dungeon.level.passable[c] || Dungeon.level.avoid[c])
                        if (Dungeon.level.water[c]) {
                            Dungeon.level.removeWater(c);
                        }
                    GameScene.add(Blob.seed(c, 4, Fire.class));
                }
                Buff.affect(target, Burning.class).reignite(target);
                if (target instanceof Hero) {
                    Hero h = (Hero) target;
                    if (h.belongings.armor != null && h.belongings.armor.glyph instanceof Brimstone) {
                        explode(h, h.belongings.armor);
                        h.belongings.armor.inscribe(Armor.Glyph.randomCurse());
                        h.belongings.armor.cursed = h.belongings.armor.cursedKnown = true;
                    }
                }
            }
            turnsLeft -= TICK;
        }

        if (Challenges.DESERT.enabled()) {
            GameScene.add(Blob.seed(target.pos(), 1, Desert.class));
        }
        spend(TICK);
        return true;
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", Math.ceil(turnsLeft));
    }

    @Override
    public int icon() {
        return BuffIndicator.THERMOMETER;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, turnsLeft);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (maxTime() - turnsLeft) / maxTime());
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        turnsLeft = bundle.getFloat(LEFT);
    }
}
