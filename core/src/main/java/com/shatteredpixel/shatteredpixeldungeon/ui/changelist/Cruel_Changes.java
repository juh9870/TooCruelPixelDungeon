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

package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AlbinoSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlackjackkeeperSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SwarmSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class Cruel_Changes {
	
	public static void addAllChanges( ArrayList<ChangeInfo> changeInfos ){
		add_v0_1_0_Changes(changeInfos);
	}

	public static void add_v0_1_0_Changes(ArrayList<ChangeInfo> changeInfos){
		ChangeInfo changes = new ChangeInfo( "0.1.0 - release", true, "");
		changes.hardlight(Window.TITLE_COLOR);
		changeInfos.add(changes);
		
		changes = new ChangeInfo(Messages.get(ChangesScene.class, "new"),false,null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton( new ChangeButton(Icons.get(Icons.INFO), "Developer Commentary",
				"_-_ Released August 12th, 2019\n" +
						"_-_ First release\n" +
						"_-_ 25 ays after Shattered Pixel Dungeon v0.7.4" +
						"\n" +
						"Dev commentary will be added here in the future."));
		
		changes.addButton( new ChangeButton(Icons.get(Icons.CHALLENGE_ON), "Challenges",
				"_-_ Added 11 new challenges\n" +
						"_-_ Challenges are unlocked by default"));
		changes.addButton(new ChangeButton(new Image(Assets.HUNTRESS, 0, 90, 12, 15),"Huntress","Huntress is unlocked by default"));
		
		changes = new ChangeInfo("Challenges",false,null);
		changes.hardlight( Window.TITLE_COLOR );
		changeInfos.add(changes);
		
		changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_OFF),Messages.get(Challenges.class,"amnesia"),Messages.get(Challenges.class,"amnesia_desc")));
		changes.addButton(new ChangeButton(new Embers(),Messages.get(Challenges.class,"cursed"),Messages.get(Challenges.class,"cursed_desc")));
		changes.addButton(new ChangeButton(new BlackjackkeeperSprite(),Messages.get(Challenges.class,"blackjack"),Messages.get(Challenges.class,"blackjack_desc")));
		changes.addButton(new ChangeButton(new SwarmSprite(),Messages.get(Challenges.class,"horde"),Messages.get(Challenges.class,"horde_desc")));
		changes.addButton(new ChangeButton(new Image(Assets.BUFFS_LARGE, 224, 32, 16, 16),Messages.get(Challenges.class,"countdown"),Messages.get(Challenges.class,"countdown_desc")));
		changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_OFF),Messages.get(Challenges.class,"analgesia"),Messages.get(Challenges.class,"analgesia_desc")));
		changes.addButton(new ChangeButton(new Image(Assets.CONS_ICONS, 14, 16, 7, 7),Messages.get(Challenges.class,"big_levels"),Messages.get(Challenges.class,"big_levels_desc")));
		changes.addButton(new ChangeButton(new AlbinoSprite(),Messages.get(Challenges.class,"mutagen"),Messages.get(Challenges.class,"mutagen_desc")));
		changes.addButton(new ChangeButton(new Ankh(),Messages.get(Challenges.class,"resurrection"),Messages.get(Challenges.class,"resurrection_desc")));
		changes.addButton(new ChangeButton(new Image(Assets.TERRAIN_FEATURES, 112, 0, 16, 16),Messages.get(Challenges.class,"extreme_caution"),Messages.get(Challenges.class,"extreme_caution_desc")));
		changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_OFF),Messages.get(Challenges.class,"extermination"),Messages.get(Challenges.class,"extermination_desc")));
	}
	
	
}
