package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class Agnosia extends Buff {

    private Image image;
    public final Class<? extends CharSprite> spriteClass = RatSprite.class;
    public int color = 0;

    private void reset(){
        image = new Image(Reflection.newInstance(spriteClass));
        color = ColorMath.interpolate( Random.Float(),0xFF0000,0xFF7F00, 0xFFFF00, 0x00FF00, 0x00FFFF, 0x0000FF, 0x9400D3);
        image.color(color);
    }

    private void showImage() {
        reset();
        GameScene.effect(image);
        target.sprite.bindCustomVisual(Agnosia.class, image);
    }

    private void hideImage() {
        target.sprite.unbindCustomVisual(Agnosia.class);
        image.killAndErase();
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            showImage();
        } else if (target.invisible == 0) {
            hideImage();
        }
    }
}
