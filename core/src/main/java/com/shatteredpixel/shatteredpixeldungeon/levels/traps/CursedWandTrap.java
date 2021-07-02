package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class CursedWandTrap extends TargetingTrap {
    @Override
    protected void noTarget(boolean heroFov) {
        CellEmitter.get(pos).burst(ShadowParticle.UP, 10);
        Sample.INSTANCE.play(Assets.Sounds.BURNING);
    }

    @Override
    protected void shootProjectile(Char target, Callback callback) {
        ((MagicMissile) ShatteredPixelDungeon.scene().recycle(MagicMissile.class)).reset(
                MagicMissile.RAINBOW,
                DungeonTilemap.tileCenterToWorld(pos),
                target.sprite.center(),
                callback);
    }

    @Override
    protected void hit(Char target, boolean heroFov) {
        int casts = 1 + Random.Int(3);
        for (int i = 0; i < casts; i++) {
            if (!target.isAlive()) return;
            CursedWand.cursedEffect(null, target, target);
        }
    }
}
