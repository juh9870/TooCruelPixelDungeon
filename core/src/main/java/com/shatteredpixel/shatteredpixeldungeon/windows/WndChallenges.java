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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class WndChallenges extends Window {

	private static final int WIDTH		= 130;
	private static final int HEIGHT		= 150;
	private static final int TTL_HEIGHT    = 12;
	private static final int BTN_HEIGHT    = 18;
	private static final int GAP        = 1;

	private boolean editable;
	private ArrayList<ChallengeButton> boxes;
	private ArrayList<IconButton> infos;

	public WndChallenges(int checked, int hell, final boolean editable ) {

		super();

		this.editable = editable;

		RenderedText title = PixelScene.renderText( Messages.get(this, "title"), 9 );
		title.hardlight( TITLE_COLOR );
		title.x = (WIDTH - title.width()) / 2;
		title.y = (TTL_HEIGHT - title.height()) / 2;
		PixelScene.align(title);
		add( title );

		resize(WIDTH,HEIGHT);

		boxes = new ArrayList<>();
		infos = new ArrayList<>();

		ScrollPane pane = new ScrollPane(new Component()){
			@Override
			public void onClick(float x, float y) {
				int size = boxes.size();
				if (editable) {
                    for (int i = 0; i < size; i++) {
						if(boxes.get(i).onClick(x, y))break;
                    }
                }
				size = infos.size();
				for (int i=0; i < size; i++) {
					if (infos.get( i ).inside(x,y)) {
						
						String message = Messages.get(Challenges.class, Challenges.values()[i].name+"_desc");
						if (boxes.get(i).hellChecked())
							message += "\n\n"+Messages.get(Challenges.class, Challenges.values()[i].name+"_hell_desc");
						ShatteredPixelDungeon.scene().add(
								new WndMessage(message)
						);
						
						break;
					}
				}
			}
		};
		add(pane);
		pane.setRect(0,TTL_HEIGHT,WIDTH,HEIGHT-TTL_HEIGHT);
		Component content = pane.content();
		
		ArrayList<ChallengeButton> disabled = new ArrayList<>();
		ArrayList<IconButton> disabledInfos = new ArrayList<>();

		float pos = 0;
		for (int i=0; i < Challenges.values().length; i++) {
			final int id = i;
			
			ChallengeButton cb = new ChallengeButton( Challenges.values()[i] );
			cb.updateState(checked,hell);
			cb.active = editable;

			if (i > 0) {
				pos += GAP;
			}
			cb.setRect( 0, pos, WIDTH-16, BTN_HEIGHT );

			content.add( cb );
			boxes.add( cb );
			
			IconButton info = new IconButton(Icons.get(Icons.INFO)){
				@Override
				protected void layout() {
					super.layout();
					hotArea.y=-5000;
				}
			};
			info.setRect(cb.right(), pos, 16, BTN_HEIGHT);
			content.add(info);
			infos.add(info);
			
			if(editable || cb.checked()) {
				pos = cb.bottom();
			} else {
				disabled.add(cb);
				disabledInfos.add(info);
				if (i > 0) {
					pos -= GAP;
				}
			}
		}
		
		if(!editable){
			for (int i=0; i<disabled.size(); i++){
				pos += GAP;
				
				disabled.get(i).setPos(0,pos);
				disabledInfos.get(i).setRect(disabled.get(i).right(), pos, 16, BTN_HEIGHT);
				
				pos = disabled.get(i).bottom();
			}
		}
		content.setSize(WIDTH,pos);

//		resize( WIDTH, (int)pos );
	}

	@Override
	public void onBackPressed() {

		if (editable) {
			int value = 0;
			int hell = 0;
			for (int i=0; i < boxes.size(); i++) {
				if (boxes.get( i ).checked()) {
					value |= Challenges.values()[i].id;
				}
				if (boxes.get( i ).hellChecked()) {
					hell |= Challenges.values()[i].id;
				}
			}
			SPDSettings.challenges( value );
			SPDSettings.hellChallenges( hell );
		}

		super.onBackPressed();
	}
	
	private static class ChallengeButton extends CheckBox {
		
		IconCheckBox hellCheckbox;
		Challenges challenge;
		
		public ChallengeButton(Challenges challenge){
			super(Messages.get(Challenges.class, challenge.name));
			this.challenge=challenge;
			hellCheckbox.visible=hellCheckbox.active=challenge.hell_enabled;
		}
		
		@Override
		protected void createChildren() {
			super.createChildren();
			hellCheckbox = new IconCheckBox();
			add(hellCheckbox);
		}
		
		public boolean hellChecked(){
			return hellCheckbox.checked();
		}
		
		protected boolean onClick(float x, float y) {
			if (!inside(x,y))return false;
			
			Sample.INSTANCE.play( Assets.SND_CLICK );
			
			if(checked()&&hellCheckbox.active&&hellCheckbox.inside(x,y)){
				hellCheck(!hellCheckbox.checked());
				
			} else
				super.onClick();
			
			return true;
		}
		
		private void hellCheck(boolean checked){
			hellCheckbox.checked(checked);
			if(hellCheckbox.checked()){
				text(Messages.get(Challenges.class, challenge.name+"_hell"));
				text.hardlight(0x79e3d2);
//				text.invert();
			} else {
				text(Messages.get(Challenges.class, challenge.name));
				text.resetColor();
			}
		}
		
		@Override
		public void checked(boolean value) {
			super.checked(value);
			if(!checked()){
				hellCheck(false);
			}
		}
		
		public void updateState(int challenges, int hell){
			checked((challenge.id & challenges)!=0);
			if (hellCheckbox.active)
				hellCheck((challenge.id & hell)!=0);
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			hotArea.width=hotArea.height=0;
			
			float margin = (height - text.baseLine()) / 2;
			
			hellCheckbox.setRect(icon.x-hellCheckbox.width()-margin,icon.y,icon.width,icon.height);
			
		}
	}
	private static class IconCheckBox extends IconButton {
		private boolean checked = false;
		public IconCheckBox(){
			super(Icons.UNCHECKED.get());
		}
		
		public boolean checked(){
			return checked;
		}
		
		public void checked(boolean checked){
			this.checked=checked;
			if (checked){
				icon(Icons.RED_CHECKED.get());
			} else {
				icon(Icons.UNCHECKED.get());
			}
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			hotArea.width=hotArea.height=0;
		}
		
		@Override
		public void onClick() {
			super.onClick();
			checked(!checked);
		}
	}
}