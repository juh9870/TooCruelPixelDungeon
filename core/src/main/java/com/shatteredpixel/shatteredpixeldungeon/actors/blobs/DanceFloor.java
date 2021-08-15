package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.DamageAmplificationBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Godspeed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.DanceTile;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

public class DanceFloor extends Blob implements Hero.Doom {
    private static final int[] colors = new int[]{
            0, 0, 0, 0,
            // Red > Green ? Blue ? Black > Yellow > Purple
            0xff0000, 0xff3333, 0xcc0000, 0xa12f2f,
            0x0ff00, 0x4aff4a, 0x00b000, 0x199119,
            0x0000ff, 0x3d3dff, 0x0000ad, 0x262696,
            0xffffff, 0xd4d4d4, 0xcfcfcf, 0xa6a6a6,
            0xffff00, 0xffff4a, 0xa3a300, 0x999928,
            0xff00ff, 0xff57ff, 0x730073, 0x8f248f
    };
    private static final int RED = 1;
    private static final int GREEN = 2;
    private static final int BLUE = 3;
    private static final int BLACK = 4;
    private static final int YELLOW = 5;
    private static final int PURPLE = 6;
    private static final int LAST_COLOR = PURPLE;

    private static final int CYCLE_LENGTH = 4;
    private static final int PAUSE_LENGTH = 2;
    private static final int SQUARE_SIZE = 3;

    private final SparseArray<DanceTile> squares = new SparseArray<>();

    @Override
    protected void evolve() {
        int width = Dungeon.level.width();
        int heroSquare = getSquare(Dungeon.hero.pos, width);

        for (int cell = 0; cell < cur.length; cell++) {
            int value = cur[cell];
            int color = value & 0xF;
            int rotationResetColor = (value >> 4) & 0xF;
            int time = (value >> 8) & 0xF;
            boolean pause = ((value >> 12) & 1) != 0;
            if (time > 1) {
                if (!pause && rotationResetColor == 0 && getSquare(cell, width) == heroSquare) {
                    rotationResetColor = color - 1;
                    if (rotationResetColor <= 0) rotationResetColor = LAST_COLOR;
                }
                time--;
            } else {
                pause = !pause;
                if (pause) time = PAUSE_LENGTH;
                else {
                    time = CYCLE_LENGTH;
                    color = color % LAST_COLOR + 1;
                    if (rotationResetColor == 0) {
                        if (color == RED || color == BLACK)
                            color = color % LAST_COLOR + 1;
                    } else {
                        if (color == rotationResetColor) {
                            rotationResetColor = 0;
                        }
                    }
                }
            }
            off[cell] = storeData(color, rotationResetColor, time, pause);
            volume += off[cell];
            if (!pause && !Dungeon.level.solid[cell]) {
                Char character = Actor.findChar(cell);
                if (character != null) applyEffect(character, color);
            }
//            updateCellGraphic(cell, color, pause);
        }
    }

    private void applyEffect(Char target, int color) {
        switch (color) {
            case RED:
                if (target == Dungeon.hero)
                    target.damage(target.HT / (CYCLE_LENGTH + 1), this);
                else
                    target.damage((Dungeon.depth / 5 + 1) * 3, this);
            case GREEN:
                Buff.prolong(target, DanceSpeed.class, 1);
                break;
            case BLUE:
                Buff.prolong(target, DancingStun.class, 1);
                break;
            case BLACK:
                int dmg = (Dungeon.depth / 5 + 1) * 5;
                if (target == Dungeon.hero) {
                    dmg = target.HT / (CYCLE_LENGTH - 1);
                }
                Buff.affect(target, DancingDeferedDamage.class).prolong(dmg);
                break;
            case YELLOW:
                if (target == Dungeon.hero) break;
                Buff.prolong(target, RewardBoost.class, 1);
                break;
            case PURPLE:
                Buff.prolong(target, DancingDoom.class, 1);
        }
    }

    private int getSquare(int cell, int levelWidth) {
        int x = cell % levelWidth;
        int y = cell / levelWidth;
        int squaresPerRow = (int) Math.ceil(1f * levelWidth / SQUARE_SIZE);
        return (x / SQUARE_SIZE) + (y / SQUARE_SIZE) * (squaresPerRow);
    }

    private void updateCellGraphic(int cell, int colorIndex, boolean hide) {
        int color = colors[colorIndex * 4 + Random.Int(4)];
        DanceTile img = squares.get(cell);
        if (Dungeon.level.heroFOV[cell] && !Dungeon.level.solid[cell]) {
            if (img == null) {
                img = new DanceTile(cell);
                if (!GameScene.lowEffect(img)) return;
                squares.put(cell, img);
            }
            img.color(color);
            img.setFrame(colorIndex - 1);
            img.visible = !hide;

        } else {
            if (img != null) {
                img.killAndErase();
                squares.remove(cell);
            }
        }
    }

    public void updateFov() {
        for (int i = 0; i < cur.length; i++) {
            int value = cur[i];
            updateCellGraphic(i, value & 0xF, ((value >> 12) & 1) != 0);
        }
    }

    private int storeData(int color, int rotationResetColor, int time, boolean pause) {
        return color + (rotationResetColor << 4) + (time << 8) + ((pause ? 1 : 0) << 12);
    }

    @Override
    public void seed(Level level, int cell, int amount) {
        if (volume > 0) return;
        if (cur == null) cur = new int[level.length()];
        if (off == null) off = new int[cur.length];

        int row = level.width();
        int[] squares = new int[row * row];
        Random.pushGenerator(Dungeon.seed + Dungeon.depth);
        for (int i = 0; i < squares.length; i++) {
            int color = Random.Int(LAST_COLOR) + 1;
            if (color == RED || color == BLACK) {
                color = color % LAST_COLOR + 1;
            }
            squares[i] = color;
        }
        Random.popGenerator();
        int squaresPerRow = (int) Math.ceil(1f * row / SQUARE_SIZE);
        for (int i = 0; i < cur.length; i++) {
            int x = i % row;
            int y = i / row;
            int square = (x / SQUARE_SIZE) + (y / SQUARE_SIZE) * (squaresPerRow);
            int color = squares[square];
            cur[i] = storeData(color, 0, 0, false);
            volume += cur[i];
        }
        area.union(0, 0);
        area.union((cur.length - 1) % level.width(), (cur.length - 1) / level.width());
    }

    @Override
    public void onDeath() {

    }

    public static class DancingStun extends Paralysis {
        @Override
        public void processDamage(int damage) {
        }

        @Override
        public float iconFadePercent() {
            return 0;
        }
    }

    public static class DancingDoom extends FlavourBuff implements DamageAmplificationBuff {
        {
            type = buffType.NEGATIVE;
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.DARKENED);
            else if (target.invisible == 0) target.sprite.remove(CharSprite.State.DARKENED);
        }

        @Override
        public int icon() {
            return BuffIndicator.CORRUPT;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc");
        }

        @Override
        public float damageMultiplier() {
            return 2f;
        }
    }

    public static class DancingDeferedDamage extends Viscosity.DeferedDamage {
    }

    public static class RewardBoost extends FlavourBuff {
    }

    public static class DanceSpeed extends Godspeed {
    }
}