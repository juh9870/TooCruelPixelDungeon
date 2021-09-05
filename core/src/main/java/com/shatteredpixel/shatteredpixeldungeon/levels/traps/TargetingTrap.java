package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.watabou.utils.Callback;

public abstract class TargetingTrap extends Trap {
    protected boolean canTarget( Char ch ){
        return true;
    }
    protected Char findTarget(){
        Char target = Actor.findChar(pos);

        if (target != null && !canTarget(target)){
            target = null;
        }

        //find the closest char that can be aimed at
        if (target == null){
            float closestDist = Float.MAX_VALUE;
            for (Char ch : Actor.chars()){
                float curDist = Dungeon.level.trueDistance(pos, ch.pos());
                if (ch.invisible > 0) curDist += 1000;
                Ballistica bolt = new Ballistica(pos, ch.pos(), Ballistica.PROJECTILE);
                if (canTarget(ch) && bolt.collisionPos == ch.pos() && curDist < closestDist){
                    target = ch;
                    closestDist = curDist;
                }
            }
        }

        return target;
    }

    protected abstract void hit(Char target, boolean heroFov);
    protected void noTarget(boolean heroFov){}

    protected void shootProjectile(Char target, Callback callback){
        ((MissileSprite) ShatteredPixelDungeon.scene().recycle(MissileSprite.class)).
                reset(pos, target.sprite, new Dart(), callback);
    }

    @Override
    public void activate() {
        Actor.add(new Actor() {

            {
                //it's a visual effect, gets priority no matter what
                actPriority = VFX_PRIO;
            }

            @Override
            protected boolean act() {

                final Actor toRemove = this;
                final Char finalTarget = findTarget();
                if (finalTarget != null) {
                    if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[finalTarget.pos()]) {
                        shootProjectile(finalTarget,() -> {
                            hit(finalTarget,true);
                            Actor.remove(toRemove);
                            next();
                        });
                        return false;
                    } else {
                        hit(finalTarget,false);
                    }
                } else {
                    noTarget(Dungeon.level.heroFOV[pos]);
                }
                Actor.remove(this);
                return true;
            }
        });
    }
}
