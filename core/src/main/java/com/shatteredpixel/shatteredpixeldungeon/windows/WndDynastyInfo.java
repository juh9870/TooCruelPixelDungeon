package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Modifiers;
import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.RankingsScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

import static com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene.align;

public class WndDynastyInfo extends Window {

    public static final int COLOR_NEGATIVE = 0xFF4444;
    public static final int COLOR_POSITIVE = 0x44FF44;
    private static final int TTL_HEIGHT = 18;
    private static final int BTN_HEIGHT = 18;
    private static final int GAP = 4;
    private static final int PADDINGS = 2;
    private static final int TEXT_WIN = 0xFFFF88;
    private final int WIDTH = Math.min(160, (int) (PixelScene.uiCamera.width * 0.9f));
    private final int HEIGHT = (int) (PixelScene.uiCamera.height * 0.95f);
    private Rankings.Dynasty dynasty;
    private Component base;


    public WndDynastyInfo(Rankings.Dynasty dyn) {
        super(0, 0, Chrome.Type.SCROLL.get());
        this.dynasty = dyn;

        resize(WIDTH, HEIGHT);
        ScrollPane sp = new ScrollPane(new Component());
        add(sp);
        sp.setRect(0, 0, WIDTH, HEIGHT);
        base = sp.content();

        float top = PADDINGS;
        top = createInfo(top);

        if (!dynasty.finished || dynasty.records.get(dynasty.records.size() - 1).win) {
            top = entry(dynasty.finished ? -1 : -2, top);
        }

        for (int i = dynasty.records.size() - 1; i >= 0; i--) {
            top = entry(i, top);
        }
        base.setSize(WIDTH, top + PADDINGS);
        align(base);

//        resize((int)base.width(),(int)base.height());
//        sp.setSize(base.width(),base.height());
    }

    private float createInfo(float top) {
        NinePatch bg = Chrome.Type.OUTLINE_THIN.get();
        bg.hardlight(0x8e866c);
        bg.y = top;
        bg.width = WIDTH * .95f;
        bg.x = (WIDTH - bg.width) / 2;
        base.add(bg);

        top += bg.marginTop() + PADDINGS;

        int mw = (int) bg.innerWidth() - PADDINGS * 2;

        top = middleText(dynasty.name, 9, TITLE_COLOR, top, mw).bottom() + GAP;
        if (dynasty.epic) {
            top = middleText(Messages.get(this, "epic"), 8, WndChallenges.TIER_COLORS[0], top, mw).bottom() + GAP;
        }
        top = middleText(Messages.get(this, dynasty.surface ? "belongs_surface" : "belongs_dungeon"), 8, -1, top, mw).bottom() + GAP;

        top += GAP * 2;

        float left = bg.x + bg.marginLeft() + PADDINGS;

        int amount = dynasty.length();
        top = infoLine(base, Messages.get(this, "runs"), Integer.toString(amount), left, top, mw);
        top = infoLine(base, Messages.get(this, "difficulty"), Float.toString(dynasty.maxDifficulty()), left, top, mw);

        bg.height = top - bg.y + bg.marginBottom();
        bg.size(bg.width, bg.height);
        align(bg);

        top = createChains(bg.y + bg.height + bg.marginBottom()-1,3);
        return top-1;
    }

    private float entry(int id, float top) {
        NinePatch outline = Chrome.get(id < 0 ? Chrome.Type.TOAST : Chrome.Type.TOAST_TR);
        base.add(outline);

        float mw1 = WIDTH * 0.95f - outline.marginHor() - PADDINGS * 2;
        Component row;
        Rankings.Record cur = null;
        if (id >= 0) {
            cur = dynasty.records.get(id);
            row = new RankingsScene.Record(id, false, cur);
            row.setSize(Math.min(mw1, RankingsScene.MAX_ROW_WIDTH), RankingsScene.ROW_HEIGHT_MAX);
            base.add(row);
        } else {
            RenderedTextBlock bl = PixelScene.renderTextBlock(Messages.get(this, id==-1?"lost":"wip"), 8);
            row = bl;
            bl.maxWidth((int) mw1);
            base.add(bl);
            align(bl);
        }

        center(top + outline.marginTop() + PADDINGS, row);
        outline.size(mw1 + outline.marginHor() + PADDINGS * 2, row.height() + outline.marginVer() + PADDINGS * 2);
        outline.x = (WIDTH - outline.width) / 2;
        outline.y = top;
        align(outline);

        top = outline.y + outline.height;

        if (id == 0) return top;

        top = createChains(top - 1, 2);

        if (cur == null) return top - 1;

        Rankings.Record prev = dynasty.records.get(id - 1);

        Modifiers.ChallengesDifference diff = Modifiers.challengesDifference(prev.modifiers(), cur.modifiers());
        if (diff.added.size() == 0 && diff.removed.size() == 0) return top - 1;

        NinePatch textBg = Chrome.Type.OUTLINE_THIN.get();
        textBg.hardlight(0x8e866c);
        base.add(textBg);

        textBg.y = top;
        int mtw = (int) (WIDTH * 0.95f) - textBg.marginHor() - PADDINGS * 2;
        int mw = 0;

        top += PADDINGS + textBg.marginTop();

        for (Challenges entry : diff.added) {
            RenderedTextBlock bl = middleText("+" + entry.localizedName(), 8, COLOR_POSITIVE, top, mtw);
            mw = Math.max(mw, (int) bl.width());
            top = bl.bottom() + GAP;
        }
        for (Challenges entry : diff.removed) {
            RenderedTextBlock bl = middleText("-" + entry.localizedName(), 8, COLOR_NEGATIVE, top, mtw);
            mw = Math.max(mw, (int) bl.width());
            top = bl.bottom() + GAP;
        }

        textBg.size(mw + PADDINGS * 2 + textBg.marginHor(), top - textBg.y - GAP + PADDINGS * 2);
        textBg.x = (WIDTH - textBg.width) / 2;
        align(textBg);

        top = textBg.y + textBg.height;

        top = createChains(top, 2) - 1;

        return top;
    }

    private float createChains(float top, int amount) {
        for (int i = amount; i > 0; i--) {
            Image c = new Image(Effects.get(Effects.Type.CHAIN));
            base.add(c);
            c.y = top;
            c.x = (WIDTH - c.width) / 2;
            top += c.height;
            align(c);
        }
        return top;
    }

    private float infoLine(Group parent, String label, String value, float left, float pos, float width) {

        RenderedTextBlock txt = PixelScene.renderTextBlock(label, 7);
        txt.setPos(left, pos);
        parent.add(txt);

        txt = PixelScene.renderTextBlock(value, 7);
        txt.setPos(width * 0.7f, pos);
        align(txt);
        parent.add(txt);

        return pos + GAP + txt.height();
    }

    private RenderedTextBlock middleText(String text, int textSize, int color, float top, int maxWidth) {
        RenderedTextBlock block = PixelScene.renderTextBlock(text, textSize);
        if (color > 0) block.hardlight(color);
        block.align(RenderedTextBlock.CENTER_ALIGN);
        base.add(block);
        block.maxWidth(maxWidth);
        center(top, block);
        align(block);

        return block;
    }

    private void center(float y, Component c) {
        c.setPos((WIDTH - c.width()) / 2, y);
    }

}
