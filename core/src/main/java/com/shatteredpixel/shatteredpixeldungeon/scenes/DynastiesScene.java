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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChallenges;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndDynastyInfo;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.ui.Button;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collections;

public class DynastiesScene extends PixelScene {

    private static final float ROW_HEIGHT_MAX = 20;
    private static final float ROW_HEIGHT_MIN = 12;

    private static final float MAX_ROW_WIDTH = 160;

    private static final float GAP = 4;

    private Archs archs;

    @Override
    public void create() {

        super.create();

        Music.INSTANCE.playTracks(
                new String[]{Assets.Music.THEME_1, Assets.Music.THEME_2},
                new float[]{1, 1},
                false);

        int w = Camera.main.width;
        int h = Camera.main.height;

        archs = new Archs();
        archs.setSize(w, h);
        add(archs);

        Rankings.INSTANCE.load();
        Rankings.INSTANCE.updateDynasties();

        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 9);
        title.hardlight(Window.TITLE_COLOR);
        title.setPos(
                (w - title.width()) / 2f,
                (20 - title.height()) / 2f
        );
        align(title);
        add(title);

        ArrayList<Rankings.Dynasty> sorted = new ArrayList<>(Rankings.INSTANCE.dynasties.values());
        Collections.sort(sorted, Rankings.dynastyComparator);

        if (sorted.size() > 0) {
//			ScrollPane sp = new ScrollPane(new Component());
            Component comp = new Component();

            int amount = sorted.size();
            float rowHeight = ROW_HEIGHT_MAX;

            float left = (w - Math.min(MAX_ROW_WIDTH, w)) / 2 + GAP + 2;

            float bot = 0;
            int pos = 0;
            ArrayList<Record> rows = new ArrayList<>();
            for (Rankings.Dynasty dynasty : sorted) {
                if (dynasty == null) continue;
                Record row = new Record(pos, dynasty);
                row.setRect(2, 2 + pos * rowHeight, w - left * 2, rowHeight);
                comp.add(row);
                rows.add(row);

                bot = row.bottom();
                pos++;
            }
            float mh = h - title.bottom() - GAP * 2;

            comp.setSize(w - left * 2 + 4, bot + 4);
            if (comp.height() > mh) {
                ScrollPane sp = new ScrollPane(comp);
                add(sp);
                sp.setRect(left - 2, title.bottom() + GAP, comp.width(), mh);
            } else {
                comp.setPos(left - 2, title.bottom() + GAP);
                add(comp);
                for (Record row : rows) {
                    row.setPos(row.left() + comp.left(), row.top() + comp.top());
                }
            }
        }

        ExitButton btnExit = new ExitButton();
        btnExit.setPos(Camera.main.width - btnExit.width(), 0);
        add(btnExit);

        RedButton btnRankings = new RedButton(Messages.get(RankingsScene.class, "title"), 5) {
            @Override
            protected void onClick() {
                super.onClick();
                ShatteredPixelDungeon.switchNoFade(RankingsScene.class);
            }
        };
        btnRankings.setRect(1, 1, 36, 12);
        add(btnRankings);

        fadeIn();
    }

    @Override
    protected void onBackPressed() {
        ShatteredPixelDungeon.switchNoFade(TitleScene.class);
    }

    public static class Record extends Button {

        private static final float GAP = 4;

        private static final int FLARE_WIN = 0x888866;
        protected ItemSprite shield;
        private Rankings.Dynasty dynasty;
        private BitmapText position;
        private RenderedTextBlock desc;
        private Image chalice;
        private BitmapText maxDifficulty;
        private Image classIcon;
        private BitmapText length;

        public Record(int pos, Rankings.Dynasty dynasty) {
            super();

            this.dynasty = dynasty;

            position.text(Integer.toString(pos + 1));
            position.measure();

            desc.text(Messages.titleCase(dynasty.name));

            if (dynasty.epic) {
                desc.hardlight(WndChallenges.TIER_COLORS[0]);
            }

            if (!dynasty.finished) {
                if (dynasty.epic) {
                    shield.view(ItemSpriteSheet.AMULET, null);
                } else {
                    shield.view(ItemSpriteSheet.ANKH, null);
                }
            } else if (dynasty.epic) {
                shield.view(ItemSpriteSheet.TOMB, new ItemSprite.Glowing(FLARE_WIN));
                shield.hardlight(FLARE_WIN);
            }
            maxDifficulty.text(Float.toString(dynasty.maxDifficulty()));
            maxDifficulty.measure();
            chalice.copy(Icons.CHALLENGE_ON.get());

            add(chalice);
            add(maxDifficulty);
            length.text(Integer.toString(dynasty.records.size()));
            length.measure();
            add(length);

            HeroClass cl = dynasty.mostUsedClass();
            classIcon.copy(Icons.get(cl));
            if (cl == HeroClass.ROGUE) {
                //cloak of shadows needs to be brightened a bit
                classIcon.brightness(2f);
            }
        }

        @Override
        protected void createChildren() {

            super.createChildren();

            shield = new ItemSprite(ItemSpriteSheet.TOMB, null);
            add(shield);

            position = new BitmapText(PixelScene.pixelFont);
            add(position);

            desc = renderTextBlock(7);
            add(desc);

            maxDifficulty = new BitmapText(PixelScene.pixelFont);

            chalice = new Image();

            classIcon = new Image();
            add(classIcon);

            length = new BitmapText(PixelScene.pixelFont);
        }

        @Override
        protected void layout() {

            super.layout();

            shield.x = x;
            shield.y = y + (height - shield.height) / 2f;
            align(shield);

            position.x = shield.x + (shield.width - position.width()) / 2f;
            position.y = shield.y + (shield.height - position.height()) / 2f + 1;
            align(position);

            classIcon.x = x + width - 16 + (16 - classIcon.width()) / 2f;
            classIcon.y = shield.y + (16 - classIcon.height()) / 2f;
            align(classIcon);

            length.x = classIcon.x + (classIcon.width - length.width()) / 2f;
            length.y = classIcon.y + (classIcon.height - length.height()) / 2f + 1;
            align(length);

            chalice.x = x + width - 32 + (16 - chalice.width()) / 2f;
            chalice.y = shield.y + (16 - chalice.height()) / 2f;
            align(chalice);

            maxDifficulty.x = chalice.x + (chalice.width - maxDifficulty.width()) / 2f;
            maxDifficulty.y = chalice.y + (chalice.height - maxDifficulty.height()) / 2f + 1;
            align(maxDifficulty);

            desc.maxWidth((int) (chalice.x - (shield.x + shield.width + GAP)));
            desc.setPos(shield.x + shield.width + GAP, shield.y + (shield.height - desc.height()) / 2f + 1);
            align(desc);
        }

        @Override
        protected void onClick() {
            ShatteredPixelDungeon.scene().addToFront(new WndDynastyInfo(dynasty));
        }
    }
}
