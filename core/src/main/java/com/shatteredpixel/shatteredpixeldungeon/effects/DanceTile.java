package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Image;

public class DanceTile extends Image {

    public DanceTile(int pos) {
        super(Assets.Environment.DANCE_FLOOR);
        setFrame(0);
        origin.set(0.5f);
        point(DungeonTilemap.tileToWorld(pos)/*.offset(
                DungeonTilemap.SIZE / 2f,
                DungeonTilemap.SIZE / 2f)*/);
//        scale.set(DungeonTilemap.SIZE * 0.8f);
        alpha(0.5f);
    }

    public void setFrame(int frame){
        frame(frame * 16, 0, 16, 16);
    }

    @Override
    public void update() {
//        float alpha = (float) (Math.sin(Game.timeTotal) + 2) / 6;
//        alpha(alpha);
    }
}
