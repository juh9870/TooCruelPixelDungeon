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

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public class TextTitle extends Component {

    private static final float FONT_SIZE = 9;

    private static final float GAP = 2;

    protected RenderedTextBlock tfLabel;


    public TextTitle() {
        super();
    }

    public TextTitle(String label) {
        super();
        label(label);
    }

    @Override
    protected void createChildren() {

        tfLabel = PixelScene.renderTextBlock((int) FONT_SIZE);
        tfLabel.hardlight(Window.TITLE_COLOR);
        tfLabel.setHightlighting(false);
        add(tfLabel);
    }

    @Override
    protected void layout() {
        tfLabel.maxWidth((int) (width));
        tfLabel.setPos(x, y);
        PixelScene.align(tfLabel);
        height = tfLabel.height();
    }

    public void label(String label) {
        tfLabel.text(label);
    }

    public void label(String label, int color) {
        tfLabel.text(label);
        tfLabel.hardlight(color);
    }

    public void color(int color) {
        tfLabel.hardlight(color);
    }
}
