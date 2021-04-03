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
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.Difficulty;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;

import java.util.ArrayList;

public class WndChallenges extends Window {
	private int WIDTH = Math.min(160,(int)(PixelScene.uiCamera.width*0.9));
	private int HEIGHT = Math.min(200,(int)(PixelScene.uiCamera.height*0.9));
	private static final int TTL_HEIGHT = 18;
	private static final int BTN_HEIGHT = 18;
	private static final int GAP = 1;
	private boolean editable;
	private ArrayList<ChallengeButton> boxes;
	private ArrayList<IconButton> infos;
	private RenderedTextBlock difficultyText;
	private ChallengesBar difficultyBar;
	private ScrollPane textScroll;
	
	private void updateDifficulty(Modifiers modifiers){
		String diff = Messages.get(Difficulty.class,Difficulty.align(modifiers).name);
		difficultyText.text(Messages.get(this,"difficulty", diff, Difficulty.calculateDifficulty(modifiers)));
		difficultyText.setPos(
				(WIDTH - difficultyText.width()) / 2,
//				TTL_HEIGHT+GAP
				1
		);
		PixelScene.align(difficultyText);
		textScroll.content().setSize(WIDTH,difficultyText.height()+2);
		textScroll.scrollTo(0,0);
		
		difficultyBar.update(modifiers);
	}
	
	public WndChallenges(Modifiers modifiers, final boolean editable) {
		super();
		resize(WIDTH, HEIGHT);
		
		this.editable = editable;

		
		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 12);
		title.hardlight(TITLE_COLOR);
		title.setPos(
				(WIDTH - title.width()) / 2,
				(TTL_HEIGHT - title.height()) / 2
		);
		PixelScene.align(title);
		add(title);
		
		float bottom = TTL_HEIGHT;
		
		difficultyText = PixelScene.renderTextBlock("",8);
		difficultyBar = new ChallengesBar();
		difficultyText.maxWidth(WIDTH);

		textScroll = new ScrollPane(new Component());
		add(textScroll);
		textScroll.setRect(0,TTL_HEIGHT+GAP,WIDTH,BTN_HEIGHT);
		textScroll.content().add(difficultyText);
		updateDifficulty(modifiers);

//		add(difficultyText);
		bottom=textScroll.bottom()+GAP*2;
		
		difficultyBar.setRect(0,bottom,WIDTH,BTN_HEIGHT/2-2);
		add(difficultyBar);
		bottom=difficultyBar.bottom();
		
		boxes = new ArrayList<>();
		infos = new ArrayList<>();
		
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
						
						String message = Messages.get(Challenges.class, Challenges.values()[i].name + "_desc");
						if (boxes.get(i).hellChecked())
							message += "\n\n" + Messages.get(Challenges.class, Challenges.values()[i].name + "_hell_desc");
						if (boxes.get(i).hell2Checked())
							message += "\n\n" + Messages.get(Challenges.class, Challenges.values()[i].name + "_hell2_desc");
						ShatteredPixelDungeon.scene().add(
								new WndMessage(message)
						);
						
						break;
					}
				}
			}
		};
		add(pane);
		pane.setRect(0, bottom, WIDTH, HEIGHT - bottom);
		Component content = pane.content();
		
		ArrayList<ChallengeButton> disabled = new ArrayList<>();
		ArrayList<IconButton> disabledInfos = new ArrayList<>();
		
		float pos = 0;
		for (int i = 0; i < Challenges.values().length; i++) {

			ChallengeButton cb = new ChallengeButton(Challenges.values()[i]);
			cb.updateState(modifiers);
			cb.active = editable;
			
			if (i > 0) {
				pos += GAP;
			}
			cb.setRect(0, pos, WIDTH - 16, BTN_HEIGHT);
			
			content.add(cb);
			boxes.add(cb);
			
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
			
			if (editable || cb.checked()) {
				pos = cb.bottom();
			} else {
				disabled.add(cb);
				disabledInfos.add(info);
				if (i > 0) {
					pos -= GAP;
				}
			}
		}
		
		if (!editable) {
			for (int i = 0; i < disabled.size(); i++) {
				pos += GAP;
				
				disabled.get(i).setPos(0, pos);
				disabledInfos.get(i).setRect(disabled.get(i).right(), pos, 16, BTN_HEIGHT);
				
				pos = disabled.get(i).bottom();
			}
		}
		content.setSize(WIDTH, pos);

//		resize( WIDTH, (int)pos );
	}
	
	private void updateCheckState(){
		if (editable) {
			Modifiers modifiers = SPDSettings.modifiers();
			for (int i = 0; i < boxes.size(); i++) {
				ChallengeButton box = boxes.get(i);
				if (box.hell2Checked()) {
					modifiers.challenges[i] = 3;
				} else if (box.hellChecked()) {
					modifiers.challenges[i] = 2;
				} else if (box.checked()) {
					modifiers.challenges[i] = 1;
				} else {
					modifiers.challenges[i] = 0;
				}
			}
			SPDSettings.modifiers(modifiers);
		}
		updateDifficulty(SPDSettings.modifiers());
	}
	
	@Override
	public void onBackPressed() {
		
		updateCheckState();
		
		super.onBackPressed();
	}
	
	private class ChallengeButton extends CheckBox {
		
		IconCheckBox hellCheckbox;
		IconCheckBox hell2Checkbox;
		Challenges challenge;
		
		public ChallengeButton(Challenges challenge) {
			super(Messages.get(Challenges.class, challenge.name));
			this.challenge = challenge;
			hellCheckbox.visible = hellCheckbox.active = challenge.maxLevel >= 2;
			hell2Checkbox.visible = hell2Checkbox.active = challenge.maxLevel >= 3;
		}
		
		@Override
		protected void createChildren() {
			super.createChildren();
			hellCheckbox = new IconCheckBox(Icons.DIAMOND_CHECKED);
			add(hellCheckbox);
			hell2Checkbox = new IconCheckBox(Icons.RED_CHECKED);
			add(hell2Checkbox);
		}
		
		public boolean hellChecked() {
			return hellCheckbox.checked();
		}
		
		public boolean hell2Checked() {
			return hell2Checkbox.checked();
		}
		
		protected boolean onClick(float x, float y) {
			if (!inside(x, y)) return false;
			
			Sample.INSTANCE.play(Assets.Sounds.CLICK);
			
			if (checked() && hellCheckbox.active && hellCheckbox.inside(x, y)) {
				hellCheckbox.checked(!hellCheckbox.checked());
				if (!hellCheckbox.checked()) hell2Checkbox.checked(false);
			} else if (checked() && hell2Checkbox.active && hell2Checkbox.inside(x, y)) {
				if (!hellChecked()) hellCheckbox.checked(true);
				else hell2Checkbox.checked(!hell2Checkbox.checked());
			} else
				super.onClick();
			updateText();
			updateCheckState();
			
			return true;
		}
		
		private void updateText() {
			if (hell2Checkbox.checked()) {
				text(Messages.get(Challenges.class, challenge.name + "_hell2"));
//				text.hardlight(0xb33636);
				text.hardlight(0xff0000);
//				text.invert();
			} else if (hellCheckbox.checked()) {
				text(Messages.get(Challenges.class, challenge.name + "_hell"));
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
			if (!checked()) {
				hellCheckbox.checked(false);
				hell2Checkbox.checked(false);
			}
			updateText();
		}
		
		public void updateState(Modifiers modifiers) {
			checked((modifiers.challengeTier(challenge.ordinal())) >= 1);
			if (hellCheckbox.active)
				hellCheckbox.checked((modifiers.challengeTier(challenge.ordinal())) >= 2);
			if (hell2Checkbox.active)
				hell2Checkbox.checked((modifiers.challengeTier(challenge.ordinal())) >= 3);
			updateText();
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			hotArea.width = hotArea.height = 0;
			
			float margin = hellCheckbox.width() / 4;
			
			hellCheckbox.setRect(icon.x - hellCheckbox.width() - margin, icon.y, icon.width, icon.height);
			hell2Checkbox.setRect(hellCheckbox.left() - hell2Checkbox.width() - margin, icon.y, icon.width, icon.height);
		}
	}
	
	private static class IconCheckBox extends IconButton {
		private boolean checked = false;
		private Icons checkedIcon;
		
		public IconCheckBox(Icons icon) {
			super(Icons.UNCHECKED.get());
			this.checkedIcon = icon;
		}
		
		public boolean checked() {
			return checked;
		}
		
		public void checked(boolean checked) {
			this.checked = checked;
			if (checked) {
				icon(checkedIcon.get());
			} else {
				icon(Icons.UNCHECKED.get());
			}
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			hotArea.width = hotArea.height = 0;
		}
		
		@Override
		public void onClick() {
			super.onClick();
			checked(!checked);
		}
	}
}