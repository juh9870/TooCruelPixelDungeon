/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.AvailableUpdateData;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;
import com.watabou.utils.ColorMath;

public class ChangesButton extends Button {

    protected Image image;

    public ChangesButton() {
        super();

        width = image.width;
        height = image.height;
        if (SPDSettings.updates()) Updates.checkForUpdate();
    }

    boolean updateShown = false;

    @Override
    public void update() {
        super.update();

        if (!updateShown && (Updates.updateAvailable() || Updates.isInstallable())){
            updateShown = true;
        }

        if (updateShown){
            image.color(ColorMath.interpolate( 0xFFFFFF, 0xFF0000, 0.5f + (float)Math.sin(Game.timeTotal*5)/2f));
        }
    }

    @Override
    protected void createChildren() {
        super.createChildren();

        image = Icons.NOTES.get();
        add( image );
    }

    @Override
    protected void layout() {
        super.layout();

        image.x = x;
        image.y = y;
    }

    @Override
    protected void onPointerDown() {
        image.brightness( 1.5f );
        Sample.INSTANCE.play( Assets.Sounds.CLICK );
    }

    @Override
    protected void onPointerUp() {
        image.resetColor();
    }

    @Override
    protected void onClick() {
        if (Updates.isInstallable()){
            Updates.launchInstall();

        } else if (Updates.updateAvailable()){
            AvailableUpdateData update = Updates.updateData();

            ShatteredPixelDungeon.scene().addToFront( new WndOptions(
                    Icons.get(Icons.CHANGES),
                    update.versionName == null ? Messages.get(this,"title") : Messages.get(this,"versioned_title", update.versionName),
                    update.desc == null ? Messages.get(this,"desc") : update.desc,
                    Messages.get(this,"update"),
                    Messages.get(this,"changes")
            ) {
                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        Updates.launchUpdate(Updates.updateData());
                    } else if (index == 1){
                        ChangesScene.changesSelected = 0;
                        ShatteredPixelDungeon.switchNoFade( ChangesScene.class );
                    }
                }
            });

        } else {
            ChangesScene.changesSelected = 0;
            ShatteredPixelDungeon.switchNoFade( ChangesScene.class );
        }
    }
}