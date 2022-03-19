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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Modifiers;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ChallengesBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.Difficulty;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.PointF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

public class WndChallenges extends Window {
    ;
    public static final int[] TIER_COLORS = new int[]{0x79e3d2, 0xff0000, 0x000000, 0xFFFFFF};
    private static final int TTL_HEIGHT = 18;
    private static final int BTN_HEIGHT = 18;
    private static final int GAP = 1;
    private static final Comparator<Challenges> simpleComparator = (a, b) -> {
        int tDiff = (int) Math.signum(a.tier - b.tier);
        if (tDiff != 0) return tDiff;

        return (int) Math.signum(a.ordinal() - b.ordinal());
    };
    private final int WIDTH = Math.min(160, (int) (PixelScene.uiCamera.width * 0.9));
    private final int HEIGHT = (int) (PixelScene.uiCamera.height * 0.9);
    private final boolean editable;
    private final HashMap<Integer, ChallengeButton> boxes;
    private final ArrayList<IconButton> infos;
    private final Challenges[] sorted;
    private final RenderedTextBlock difficultyText;
    private final ChallengesBar difficultyBar;
    private final ScrollPane textScroll;
    private final float oldDifficulty;
    private final boolean needIncrease;
    private final Modifiers modifiers;

    public WndChallenges(Modifiers modifiers, final boolean editable) {
        this(modifiers, editable, false, (o) -> true);
    }

    public WndChallenges(Modifiers modifiers, final boolean editable, final boolean needIncrease, ChallengePredicate editableFilter) {
        super();
        this.modifiers = modifiers;

        oldDifficulty = Difficulty.calculateDifficulty(modifiers);
        this.needIncrease = needIncrease;

        resize(WIDTH, HEIGHT);

        this.editable = editable;


        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, (needIncrease ? "title_more" : "title")), 12);
        title.hardlight(TITLE_COLOR);
        title.setPos(
                (WIDTH - title.width()) / 2,
                (TTL_HEIGHT - title.height()) / 2
        );
        PixelScene.align(title);
        add(title);

        float bottom = TTL_HEIGHT;

        difficultyText = PixelScene.renderTextBlock("", 8);
        difficultyBar = new ChallengesBar();
        difficultyText.maxWidth(WIDTH);

        textScroll = new ScrollPane(new Component());
        add(textScroll);
        textScroll.setRect(0, TTL_HEIGHT + GAP, WIDTH, BTN_HEIGHT);
        textScroll.content().add(difficultyText);
        updateDifficulty(modifiers);

//		add(difficultyText);
        bottom = textScroll.bottom() + GAP * 2;

        difficultyBar.setRect(0, bottom, WIDTH, BTN_HEIGHT / 2f - 2);
        add(difficultyBar);
        bottom = difficultyBar.bottom();

        boxes = new HashMap<>();
        infos = new ArrayList<>();
        sorted = Challenges.values();
        Arrays.sort(sorted, getComparator(modifiers, editable, editableFilter));


        ScrollPane pane = new ScrollPane(new Component()) {
            @Override
            public void onClick(float x, float y) {
                int size = boxes.size();
                if (editable) {
                    for (int i = 0; i < size; i++) {
                        if (boxes.get(i).onClick(x, y)) break;
                    }
                }
                size = infos.size();
                for (int i = 0; i < size; i++) {
                    if (infos.get(i).inside(x, y)) {

                        String message = sorted[i].description();
                        String title = Messages.get(Challenges.class, sorted[i].name);
                        ShatteredPixelDungeon.scene().add(
                                new WndTitledMessage(new TextTitle(title), message)
                        );

                        break;
                    }
                }
            }
        };
        add(pane);

        pane.setRect(0, bottom, WIDTH, HEIGHT - bottom - BTN_HEIGHT / 2f - GAP * 2);
        Component content = pane.content();

        ArrayList<Float> scrollTos = new ArrayList<>();

        float pos = 0;
        for (int i = 0; i < sorted.length; i++) {
            Challenges chals = sorted[i];
            ChallengeButton cb = new ChallengeButton(chals, editableFilter);
            cb.updateState(modifiers);
            cb.active = editable && !chals.deprecated();

            boolean checked = modifiers.isChallenged(chals.id);
            boolean filtered = editableFilter.test(chals);
            int tier = chals.tier;


            if (i == 0) {
                if (editable) {
                    if (checked) {
                        pos = delimiter(content, pos, Messages.get(this, "enabled"), 9, TITLE_COLOR, scrollTos);
                    } else {
                        pos = delimiter(content, pos, tierMessage(tier), 10, TITLE_COLOR, scrollTos);
                    }
                } else {
                    pos = delimiter(content, pos, tierMessage(tier), 10, TITLE_COLOR, scrollTos);
                }
            } else {
                pos += GAP;
                boolean prevFiltered = editableFilter.test(sorted[i - 1]);
                boolean prevChecked = modifiers.isChallenged(sorted[i - 1].id);
                int prevTier = sorted[i - 1].tier;
                if (editable) {
                    if (!filtered) {
                        if (prevFiltered) {
                            pos = delimiter(content, pos, Messages.get(this, checked ? "unavailable_enabled" : "unavailable_disabled"), 8, 0xaaaaaa, scrollTos);
                        } else {
                            if (!checked && prevChecked) {
                                pos = delimiter(content, pos, Messages.get(this, "unavailable_disabled"), 8, 0xaaaaaa, scrollTos);
                            }
                        }
                    } else {
                        if (!checked) {
                            if (prevChecked || tier != prevTier) {
                                pos = delimiter(content, pos, tierMessage(tier), 10, TITLE_COLOR, scrollTos);
                            }
                        }
                    }
                } else {
                    if (!checked) {
                        if (prevChecked) {
                            pos = delimiter(content, pos, Messages.get(this, "disabled"), 8, 0xaaaaaa, scrollTos);
                        }
                    } else {
                        if (tier != prevTier) {
                            pos = delimiter(content, pos, tierMessage(tier), 10, TITLE_COLOR, scrollTos);
                        }
                    }
                }
            }

            cb.setRect(0, pos, WIDTH - 16, BTN_HEIGHT);

            content.add(cb);
            boxes.put(chals.id, cb);

            IconButton info = new IconButton(Icons.get(Icons.INFO)) {
                @Override
                protected void layout() {
                    super.layout();
                    hotArea.y = -5000;
                }
            };
            info.setRect(cb.right(), pos, 16, BTN_HEIGHT);
            content.add(info);
            infos.add(info);
            pos = cb.bottom();
        }
        content.setSize(WIDTH, pos);

        bottom = pane.bottom();
        bottom += GAP * 2;
        RedButton btnUp = new RedButton(Messages.get(this, "scroll_up"), 7) {
            @Override
            protected void onClick() {
                PointF curScroll = pane.content().camera.scroll;
                for (int i = scrollTos.size() - 1; i >= 0; i--) {
                    float pos = scrollTos.get(i);
                    if (pos + GAP < curScroll.y) {
                        pane.scrollTo(curScroll.x, pos);
                        pane.fixScroll();
                        return;
                    }
                }
            }
        };
        btnUp.setRect(0, bottom, (WIDTH) / 3f, BTN_HEIGHT / 2f);
        add(btnUp);
        RedButton btnDown = new RedButton(Messages.get(this, "scroll_down"), 7) {
            @Override
            protected void onClick() {
                PointF curScroll = pane.content().camera.scroll;
                for (Float pos : scrollTos) {
                    if (pos - GAP > curScroll.y) {
                        pane.scrollTo(curScroll.x, pos);
                        pane.fixScroll();
                        return;
                    }
                }
            }
        };
        btnDown.setRect(btnUp.right(), bottom, (WIDTH) / 3f, BTN_HEIGHT / 2f);
        add(btnDown);
        RedButton btnClear = new RedButton(Messages.get(this, "clear"), 7) {
            @Override
            protected void onClick() {
                for (int i = 0; i < boxes.size(); i++) {
                    setCheckedNoUpdate(i, false);
                }
                updateCheckState();
            }
        };
        btnClear.setRect(btnDown.right(), bottom, (WIDTH) / 3f, BTN_HEIGHT / 2f);
        add(btnClear);
        if (needIncrease || !editable) {
            btnClear.enable(false);
        }
//		resize( WIDTH, (int)pos );
    }

    private static Comparator<Challenges> getComparator(Modifiers mods, boolean editable, ChallengePredicate editableFilter) {
        return (a, b) -> {
            boolean ea = editableFilter.test(a);
            boolean eb = editableFilter.test(b);
            if (mods != null && ea == eb) {
                ea = mods.isChallenged(a.id);
                eb = mods.isChallenged(b.id);
            }
            if (ea && !eb) return -1;
            if (!ea && eb) return 1;
            return simpleComparator.compare(a, b);
        };
    }

    private String tierMessage(int tier) {
        if (tier < 5)
            return Messages.get(this, "tier", tier);
        return Messages.get(this, "modifiers");
    }

    private float delimiter(Component parent, float pos, String text, int size, int color, ArrayList<Float> scrollList) {
        scrollList.add(pos);
        pos += GAP * 3;
        RenderedTextBlock tb = PixelScene.renderTextBlock(text, size);
        if (color >= 0) tb.hardlight(color);
        tb.maxWidth(WIDTH - GAP * 2);
        tb.setPos((int) ((WIDTH - tb.width()) / 2), pos);
        PixelScene.align(tb);
        parent.add(tb);
        return tb.bottom() + GAP * 3;
    }

    private void updateDifficulty(Modifiers modifiers) {
        String diff = Messages.get(Difficulty.class, Difficulty.align(modifiers).name);
        String text = Messages.get(this, "difficulty", diff, Difficulty.calculateDifficulty(modifiers));
        if (modifiers.isCheesed()) {
            text = Messages.get(this, "difficulty_mod", text);
        }
        difficultyText.text(text);
        difficultyText.setPos(
                (WIDTH - difficultyText.width()) / 2,
//				TTL_HEIGHT+GAP
                1
        );
        PixelScene.align(difficultyText);
        textScroll.content().setSize(WIDTH, difficultyText.height() + 2);
        textScroll.scrollTo(0, 0);

        difficultyBar.update(modifiers);
    }

    private void updateCheckState() {
        if (editable) {
            for (int i = 0; i < boxes.size(); i++) {
                ChallengeButton box = boxes.get(i);
                modifiers.challenges[i] = box.checked();
            }
        }
        updateDifficulty(modifiers);
    }

    private void setCheckedNoUpdate(int id, boolean checked) {
        boxes.get(id).checked(checked);
    }

    @Override
    public void onBackPressed() {

        updateCheckState();

        if (needIncrease && Difficulty.calculateDifficulty(modifiers) <= oldDifficulty) {
            ShatteredPixelDungeon.scene().addToFront(new WndMessage(Messages.get(WndChallenges.class, "need_more", oldDifficulty)));
            return;
        }

        SPDSettings.modifiers(modifiers);

        super.onBackPressed();
    }

    public interface ChallengePredicate {
        boolean test(Challenges challenge);
    }

    private class ChallengeButton extends CheckBox {
        Challenges challenge;
        ChallengePredicate filter;

        public ChallengeButton(Challenges challenge, ChallengePredicate filter) {
            super(Messages.get(Challenges.class, challenge.name));
            this.filter = filter;
            this.challenge = challenge;
            if (!filter.test(challenge)) icon.alpha(0.3f);
            text(Messages.get(Challenges.class, challenge.name));
            if (challenge.tier > 1 && !challenge.isModifier()) {
                text.hardlight(TIER_COLORS[challenge.tier - 2]);
            }

            if (challenge.deprecated()) {
                enable(false);
                if (editable) {
                    checked(false);
                }
            }
        }

        @Override
        public void checked(boolean value) {
            super.checked(value);
//            if(value && !editable){
//                super.checked(false);
//            }
            icon.copy(Icons.get(checked() ? tierChecked() : Icons.UNCHECKED));
        }

        public Icons tierChecked() {
            switch (challenge.tier) {
                case 2:
                    return Icons.DIAMOND_CHECKED;
                case 3:
                    return Icons.RED_CHECKED;
                default:
                    return Icons.CHECKED;
            }
        }

        private void notification(String key, Set<Challenges> displayChallenges, boolean targetState) {
            StringBuilder sb = new StringBuilder();
            Challenges[] sorted = displayChallenges.toArray(new Challenges[0]);
            Arrays.sort(sorted, simpleComparator);
            for (int i = 0; i < sorted.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append("_").append(sorted[i].localizedName()).append("_");
            }
            ShatteredPixelDungeon.scene().addToFront(new WndOptions(
                    Messages.get(WndChallenges.class, "notification_title"),
                    Messages.get(WndChallenges.class, key, sb.toString()),
                    Messages.get(WndChallenges.class, "yes"),
                    Messages.get(WndChallenges.class, "no")
            ) {
                @Override
                protected void onSelect(int index) {
                    if (index != 0) return;
                    for (Challenges chal : displayChallenges) {
                        setCheckedNoUpdate(chal.id, targetState);
                    }
                    setCheckedNoUpdate(challenge.id, targetState);
                    updateCheckState();
                }
            });
        }

        protected boolean onClick(float x, float y) {
            if (!inside(x, y)) return false;
            if (!active) return false;

            Sample.INSTANCE.play(Assets.Sounds.CLICK);
            if (filter.test(challenge)) {
                Modifiers mods = modifiers;
                boolean canClick = true;
                if (checked()) {
                    if (!mods.canDisable(challenge)) {
                        canClick = false;
                        notification("cant_disable", mods.recursiveDependantsFilterEnabled(challenge, modifiers), false);
                    }
                } else {
                    if (!mods.canEnable(challenge)) {
                        canClick = false;
                        notification("cant_enable", mods.recursiveRequirements(challenge), true);
                    }
                }
                if (canClick) {
                    super.onClick();
                    updateCheckState();
                }
            }

            return true;
        }

        @Override
        public void enable(boolean value) {
            if (challenge.deprecated()) value = false;
            super.enable(value);
        }

        public void updateState(Modifiers modifiers) {
            checked(modifiers.isChallenged(challenge.id));
        }

        @Override
        protected void layout() {
            super.layout();
            hotArea.width = hotArea.height = 0;
        }
    }
}