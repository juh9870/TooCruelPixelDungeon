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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MirrorWraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AlbinoSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlackjackkeeperSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotHeartSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SuccubusSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SwarmSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.PointF;

import java.util.ArrayList;

public class Cruel_Changes {
    private static TextureFilm film;

    public static void addAllChanges(ArrayList<ChangeInfo> changeInfos) {
        SmartTexture icons = TextureCache.get(Assets.Interfaces.BUFFS_LARGE);
        film = new TextureFilm(icons, 16, 16);

        add_v0_4_0_Changes(changeInfos);
        add_v0_3_0_Changes(changeInfos);
        add_v0_2_0_Changes(changeInfos);
        add_v0_1_0_Changes(changeInfos);
    }

    public static void add_v0_4_0_Changes(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = new ChangeInfo("v0.4.3", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo("v0.4.3a & v0.4.3b", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Fixed challenges window crash\n" +
                        "_-_ Fixed crashes related to falling into the pit with Approaching Infinity challenge\n" +
                        "_-_ Fixed Shared Pain crashes\n" +
                        "_-_ Fixed Rebirth enemies skipping ascension steps"
        ));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Updated to SHPD v0.9.3c\n" +
                        "_-_ Added score display to rankings"));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Fixed bugs related to cursed challenge\n" +
                        "_-_ Fixed Mutagen/Evolution bugged drop rates"
        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ Added 13 new challenges\n" +
                        "_-_ Added score multiplier for dynasty runs\n" +
                        "_-_ Removed Rook challenge"
        ));

        changes = new ChangeInfo("Challenges", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        Image _mapicon = new Image(Assets.Sprites.ITEM_ICONS, 56, 16, 8, 8);
        _mapicon.scale = new PointF(1f, 1f);
        changes.addButton(new ChangeButton(_mapicon, Messages.get(Challenges.class, "big_rooms"), Messages.get(Challenges.class, "big_rooms_desc")));
        _mapicon = new Image(Assets.Sprites.ITEM_ICONS, 56, 16, 8, 8);
        _mapicon.scale = new PointF(1.2f, 1.2f);
        changes.addButton(new ChangeButton(_mapicon, Messages.get(Challenges.class, "bigger_rooms"), Messages.get(Challenges.class, "bigger_rooms_desc")));

        MobSprite sp = new SwarmSprite();
        changes.addButton(new ChangeButton(sp, Messages.get(Challenges.class, "infinity_mobs"), Messages.get(Challenges.class, "infinity_mobs_desc")));

        sp = new RatSprite();

        changes.addButton(new ChangeButton(sp, Messages.get(Challenges.class, "stacking"), Messages.get(Challenges.class, "stacking_desc")));

        sp = new RatSprite();
        changes.addButton(new ChangeButton(sp, Messages.get(Challenges.class, "stacking_spawn"), Messages.get(Challenges.class, "stacking_spawn_desc")));
        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL), Messages.get(Challenges.class, "stacking_champions"), Messages.get(Challenges.class, "stacking_champions_desc")));

        Image blood1 = new Image(Assets.Interfaces.BUFFS_LARGE);
        blood1.frame(film.get(BuffIndicator.BLEEDING));
        blood1.invert();
        changes.addButton(new ChangeButton(blood1, Messages.get(Challenges.class, "shared_pain"), Messages.get(Challenges.class, "shared_pain_desc")));
        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_ON), Messages.get(Challenges.class, "room_lock"), Messages.get(Challenges.class, "room_lock_desc")));

        Image thermometer = new Image(Assets.Interfaces.BUFFS_LARGE);
        thermometer.frame(film.get(BuffIndicator.THERMOMETER));
        changes.addButton(new ChangeButton(thermometer, Messages.get(Challenges.class, "scorched_earth"), Messages.get(Challenges.class, "scorched_earth_desc")));

        Image fire = new Image(Assets.Interfaces.BUFFS_LARGE);
        fire.frame(film.get(BuffIndicator.FIRE));
        changes.addButton(new ChangeButton(fire, Messages.get(Challenges.class, "desert"), Messages.get(Challenges.class, "desert_desc")));
        changes.addButton(new ChangeButton(new Image(Assets.Environment.TERRAIN_FEATURES, 0, 80, 16, 16), Messages.get(Challenges.class, "indifferent_design"), Messages.get(Challenges.class, "indifferent_design_desc")));
        changes.addButton(new ChangeButton(new Image(Assets.Environment.TERRAIN_FEATURES, 128, 16, 16, 16), Messages.get(Challenges.class, "chaotic_construction"), Messages.get(Challenges.class, "chaotic_construction_desc")));
        changes.addButton(new ChangeButton(new Image(Assets.Environment.TERRAIN_FEATURES, 112, 48, 16, 16), Messages.get(Challenges.class, "trap_testing_facility"), Messages.get(Challenges.class, "trap_testing_facility_desc")));

        changes = new ChangeInfo("v0.4.2", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);


        changes = new ChangeInfo("v0.4.2a", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ On a Beat now requires Countdown\n" +
                        "_-_ Mirror of Rage's Mirror Wraiths not have fixed lifetime of 10 turns, which scales with amount of mirror wraiths killed and their HP if when Spiritual Connection is enabled.\n" +
                        "_-_ Added 1 new T2 challenge\n" +
                        "_-_ Racing The Death now counts player actions instead of turns"
        ));
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Fixed Agoraphobia not halving the loot\n" +
                        "_-_ Fixed crash when winning on difficulty normal or lower\n" +
                        "_-_ Fixed Marathon & On a Beat timer reset exploit\n" +
                        "_-_ Fixed levelgen bug with too big levels"
        ));
        changes.addButton(new ChangeButton(Icons.get(Icons.LANGS), Messages.get(ChangesScene.class, "language"),
                "_-_ Added Chinese translations for challenges.\n" +
                        "_-_ Added Korean translations for challenges."
        ));
        _mapicon = new Image(Assets.Effects.EFFECTS, 16, 24, 16, 6);
        changes.addButton(new ChangeButton(_mapicon, Messages.get(Challenges.class, "linear"), Messages.get(Challenges.class, "linear_desc")));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Fixed enemies ascending after dying from chasm"
        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ Reworked challenges system\n" +
                        "_-_ Some challenges no longer requires their previous tiers to be enabled\n" +
                        "_-_ Added 16 new challenges\n" +
                        "_-_ Legion no longer have element of randomness to wave delay\n" +
                        "_-_ Pharmacophobia potions now heals in addition to poisoning too\n" +
                        "_-_ Legion is now T2\n" +
                        "_-_ Plague is now T2\n" +
                        "_-_ Heart of the Hive is now T3\n" +
                        "_-_ Extreme Danger is now T1"
        ));

        changes = new ChangeInfo("Challenges", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        sp = new SwarmSprite();
//		sp.add(CharSprite.State.INVISIBLE);
        changes.addButton(new ChangeButton(sp, Messages.get(Challenges.class, "manifesting_myriads"), Messages.get(Challenges.class, "manifesting_myriads_desc")));
        sp.aura(Cruel_Changes.class, 0xFF0000, 1.0f, false);

        _mapicon = new Image(Assets.Sprites.ITEM_ICONS, 56, 16, 8, 8);
        _mapicon.scale = new PointF(0.5f, 0.5f);
        changes.addButton(new ChangeButton(_mapicon, Messages.get(Challenges.class, "small_levels"), Messages.get(Challenges.class, "small_levels_desc")));
        _mapicon = new Image(Assets.Sprites.ITEM_ICONS, 56, 16, 8, 8);
        _mapicon.scale = new PointF(1.2f, 1.2f);
        changes.addButton(new ChangeButton(_mapicon, Messages.get(Challenges.class, "bigger_levels"), Messages.get(Challenges.class, "bigger_levels_desc")));
        _mapicon = new Image(Assets.Effects.SPELL_ICONS, 16, 0, 16, 16);
//        _mapicon.scale = new PointF(1.5f, 1.5f);
        _mapicon.color(0xFFFF00);
        _mapicon.aa = 1;
        changes.addButton(new ChangeButton(_mapicon, Messages.get(Challenges.class, "huge_levels"), Messages.get(Challenges.class, "huge_levels_desc")));

        Image blood = new Image(Assets.Interfaces.BUFFS_LARGE);
        blood.frame(film.get(BuffIndicator.BLEEDING));
        Image vulnerable = new Image(Assets.Interfaces.BUFFS_LARGE);
        vulnerable.frame(film.get(BuffIndicator.VULNERABLE));
        changes.addButton(new ChangeButton(blood, Messages.get(Challenges.class, "bloodbag"), Messages.get(Challenges.class, "bloodbag_desc")));
        changes.addButton(new ChangeButton(vulnerable, Messages.get(Challenges.class, "arrowhead"), Messages.get(Challenges.class, "arrowhead_desc")));
        changes.addButton(new ChangeButton(new Embers() {
            @Override
            public ItemSprite.Glowing glowing() {
                return new ItemSprite.Glowing(0xff0000, 2f);
            }
        }, Messages.get(Challenges.class, "curse_magnet"), Messages.get(Challenges.class, "curse_magnet_desc")));
        changes.addButton(new ChangeButton(new Embers() {
            @Override
            public ItemSprite.Glowing glowing() {
                return new ItemSprite.Glowing(0x880044, 2f);
            }
        }, Messages.get(Challenges.class, "curse_enchant"), Messages.get(Challenges.class, "curse_enchant_desc")));
        changes.addButton(new ChangeButton(new MirrorWraith.MirrorWraithSprite(), Messages.get(Challenges.class, "mirror_of_rage"), Messages.get(Challenges.class, "mirror_of_rage_desc")));
        changes.addButton(new ChangeButton(new MirrorWraith.MirrorWraithSprite(), Messages.get(Challenges.class, "ectoplasm"), Messages.get(Challenges.class, "ectoplasm_desc")));
        changes.addButton(new ChangeButton(new MirrorWraith.MirrorWraithSprite(), Messages.get(Challenges.class, "spiritual_connection"), Messages.get(Challenges.class, "spiritual_connection_desc")));
        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.SCROLL_UNKNOWN), Messages.get(Challenges.class, "thoughtless"), Messages.get(Challenges.class, "thoughtless_desc")));
        changes.addButton(new ChangeButton(new SuccubusSprite(), Messages.get(Challenges.class, "exhibitionism"), Messages.get(Challenges.class, "exhibitionism_desc")));
        Image clock1 = new Image(Assets.Interfaces.BUFFS_LARGE);
        clock1.frame(film.get(BuffIndicator.COUNTDOWN1));
        clock1.hardlight(0x0000ff);
        Image clock2 = new Image(Assets.Interfaces.BUFFS_LARGE);
        clock2.frame(film.get(BuffIndicator.COUNTDOWN1));
        clock2.hardlight(0x00ffff);
        Image eye = new Image(Assets.Interfaces.BUFFS_LARGE);
        eye.frame(film.get(BuffIndicator.RAGE));
        changes.addButton(new ChangeButton(clock1, Messages.get(Challenges.class, "marathon"), Messages.get(Challenges.class, "marathon_desc")));
        changes.addButton(new ChangeButton(clock2, Messages.get(Challenges.class, "on_a_beat"), Messages.get(Challenges.class, "on_a_beat_desc")));
        changes.addButton(new ChangeButton(eye, Messages.get(Challenges.class, "insomnia"), Messages.get(Challenges.class, "insomnia_desc")));

        changes = new ChangeInfo("v0.4.1", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Speculative fix for weird enemy timeskips.\n" +
                        "_-_ Fixed Toxic champion not summoning guards.\n" +
                        "_-_ Summoning champions not permanently visible (caused by v0.4.0b).\n"
        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ Summoned mobs can no longer Ascend."
        ));

        changes = new ChangeInfo("New content", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new Amulet(), "Dynasties",
                "_-_ Added dynasties (winstreaks) System"
        ));

        changes = new ChangeInfo("v0.4.0", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo("v0.4.0b", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Variety bugs related to Ascension challenge\n" +
                        "_-_ Missing text in sad ghost quest"));
        changes.addButton(new ChangeButton(Icons.CHALLENGE_HELL.get(), Messages.get(Challenges.class, "elite_champions"),
                "_-_ Elite Champion guardians are now delayed by 1 turn after spawning.\n" +
                        "_-_ When Elite Champion spawns, its position is revealed to the player for 2 turns.\n" +
                        "_-_ Restoring champions can no longer ascend."));
        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_OFF), Messages.get(Challenges.class, "extermination"),
                "_-_ When player tries to exit with targets remaining, they will be revealed for 1 turn."));
        changes.addButton(new ChangeButton(Icons.INFO.get(), "Difficulty indicator", "Tweaked difficulties."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.INFO), "Developer Commentary",
                "_-_ Released 05.03.2021\n" +
                        "_-_ 181 days after v0.3.0" +
                        "\n" +
                        "Dev commentary will be added here in the future."));


        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed various bugs. Sadly, I lost changelog file so no details on this one :("));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ Added T2 and T3 versions for new Hostile champions challenge. \n" +
                        "_-_ Added challenge to disable talents. I don't like it, but I'm not a fun of talents system either."
        ));

        changes = new ChangeInfo("New content", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.CHALLENGE_HELL.get(), Messages.get(Challenges.class, "elite_champions"), Messages.get(Challenges.class, "elite_champions_desc")));
        changes.addButton(new ChangeButton(Icons.CHALLENGE_HELL2.get(), Messages.get(Challenges.class, "dungeon_of_champions"), Messages.get(Challenges.class, "dungeon_of_champions_desc")));
        changes.addButton(new ChangeButton(Icons.TALENT.get(), Messages.get(Challenges.class, "no_perks"), Messages.get(Challenges.class, "no_perks_desc")));

        changes.addButton(new ChangeButton(Icons.INFO.get(), "Difficulty indicator", "Added difficulty indicator to challenges window."));
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.ITEM_ICONS, 48, 16, 8, 8), "Music", "Added new bgm track made by PyroJoke. New track have 1/3 chance to start playing if your run difficulty is Very Hard or higher."));
    }

    public static void add_v0_3_0_Changes(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = new ChangeInfo("v0.3.0", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo("v0.3.0b", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed (caused by v0.3.0):\n" +
                        "_-_ Even more crashes related to Rook challenge\n" +
                        "_-_ Crashes related to Blackjack"));

        changes.addButton(new ChangeButton(new SwarmSprite(), Messages.get(Challenges.class, "invasion") + " and " + Messages.get(Challenges.class, "invasion"),
                "_-_ Legion is now T3 challenge\n" +
                        "_-_ Invasion is now T2 challenge\n" +
                        "_-_ Invasion is now weaker on first 2 floors\n" +
                        "_-_ Legion waves delay is increased"));

        changes.addButton(new ChangeButton(Icons.CHALLENGE_OFF.get(), Messages.get(Challenges.class, "rook"),
                "_-_ Diagonal ranged player interactions are now impossible."));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_AMBER), Messages.get(Challenges.class, "plague"),
                "_-_ Increased intoxication gained from taking damage."));

        changes = new ChangeInfo("v0.3.0a", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed (caused by v0.3.0):\n" +
                        "_-_ Various crashes related to Rook challenge"));

        changes.addButton(new ChangeButton(new SwarmSprite(), Messages.get(Challenges.class, "invasion"),
                "_-_ Now seals the level for 20 turns after start of each wave."));

        changes.addButton(new ChangeButton(new Ankh() {
            @Override
            public ItemSprite.Glowing glowing() {
                return new ItemSprite.Glowing(0xFF0000);
            }
        }, Messages.get(Challenges.class, "ascension"),
                "_-_ Tweaked mob rebirth chance"));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_OFF), Messages.get(Challenges.class, "amnesia"),
                "_-_ Magically-mapped cells quickly becomes unknown."));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.INFO), "Developer Commentary",
                "_-_ Released 05.09.2020\n" +
                        "_-_ 315 days after v0.2.0" +
                        "\n" +
                        "Dev commentary will be added here in the future."));
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed (caused by v0.2.0):\n" +
                        "_-_ Racing the Death crash\n" +
                        "_-_ Missing Intoxication description"));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ Added even-more harder version to 3 challenges\n" +
                        "_-_ Added Rook challenge"
        ));

        changes.addButton(new ChangeButton(new Ankh().bless(), Messages.get(Challenges.class, "rebirth"),
                "_-_ Renamed from Resurrection to Rebirth\n" +
                        "_-_ Chance of mob rebirth is now increases with difference between player level and mob max level"));

        changes.addButton(new ChangeButton(new AlbinoSprite(), Messages.get(Challenges.class, "mutagen"),
                "_-_ Chance of spawning rare enemy variation reduced from 50% to 25%"));

        changes.addButton(new ChangeButton(new SwarmSprite(), Messages.get(Challenges.class, "invasion"),
                "_-_ No longer increases chances of spawning mobs from lower floors."));

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_AMBER), Messages.get(Challenges.class, "intoxication"),
                "_-_ Made debuffs duration random\n" +
                        "_-_ Added more possible debuffs.\n" +
                        "_-_ Increased debuff applying delay on lower intoxication levels"));

        changes = new ChangeInfo("New content", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new Ankh() {
            @Override
            public ItemSprite.Glowing glowing() {
                return new ItemSprite.Glowing(0xFF0000);
            }
        }, Messages.get(Challenges.class, "ascension"), Messages.get(Challenges.class, "ascension_desc")));

        changes.addButton(new ChangeButton(new SwarmSprite(), Messages.get(Challenges.class, "legion"), Messages.get(Challenges.class, "legion_desc")));
        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_AMBER), Messages.get(Challenges.class, "plague"), Messages.get(Challenges.class, "plague_desc")));
        changes.addButton(new ChangeButton(Icons.CHALLENGE_OFF.get(), Messages.get(Challenges.class, "rook"), Messages.get(Challenges.class, "rook_desc")));

    }

    public static void add_v0_2_0_Changes(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = new ChangeInfo("v0.2.0", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.INFO), "Developer Commentary",
                "_-_ Released 26.10.2019\n" +
                        "_-_ 75 days after v0.1.0" +
                        "\n" +
                        "Dev commentary will be added here in the future."));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL), "Challenges",
                "_-_ Added nightmare version to 9 challenges"
        ));
        changes.addButton(new ChangeButton(BadgeBanner.image(Badges.Badge.CHAMPION_4.image), "Badges",
                "Added 4 new badges for winning with 9 challenges + 1 nightmare, 12 challenges + 3 nightmare, 15 challenges + 6 nightmare and 18 challenges + 9 nightmare"
        ));

        changes = new ChangeInfo("Challenges", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.POTION_AMBER), Messages.get(Challenges.class, "intoxication"), Messages.get(Challenges.class, "intoxication_desc")));
        changes.addButton(new ChangeButton(new RotHeartSprite(), Messages.get(Challenges.class, "heart_of_hive"), Messages.get(Challenges.class, "heart_of_hive_desc")));
        changes.addButton(new ChangeButton(new ItemSprite(ItemSpriteSheet.TORCH), Messages.get(Challenges.class, "blindness"), Messages.get(Challenges.class, "blindness_desc")));
        changes.addButton(new ChangeButton(Icons.CHALLENGE_HELL.get(), Messages.get(Challenges.class, "lobotomy"), Messages.get(Challenges.class, "lobotomy_desc")));
        changes.addButton(new ChangeButton(new SwarmSprite(), Messages.get(Challenges.class, "invasion"), Messages.get(Challenges.class, "invasion_desc")));
        Image clock = new Image(Assets.Interfaces.BUFFS_LARGE);
        clock.frame(film.get(BuffIndicator.COUNTDOWN2));
        changes.addButton(new ChangeButton(clock, Messages.get(Challenges.class, "racing_the_death"), Messages.get(Challenges.class, "racing_the_death_desc")));
        changes.addButton(new ChangeButton(new AlbinoSprite(), Messages.get(Challenges.class, "evolution"), Messages.get(Challenges.class, "evolution_desc")));
        changes.addButton(new ChangeButton(new Ankh().bless(), Messages.get(Challenges.class, "rebirth"), Messages.get(Challenges.class, "rebirth_desc")));
        changes.addButton(new ChangeButton(new Image(Assets.Environment.TERRAIN_FEATURES, 112, 96, 16, 16), Messages.get(Challenges.class, "extreme_danger"), Messages.get(Challenges.class, "extreme_danger_desc")));
    }

    public static void add_v0_1_0_Changes(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = new ChangeInfo("v0.1.0 - release", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.INFO), "Developer Commentary",
                "_-_ Released August 12th, 2019\n" +
                        "_-_ First release\n" +
                        "_-_ 25 days after Shattered Pixel Dungeon v0.7.4" +
                        "\n" +
                        "Dev commentary will be added here in the future."));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_ON), "Challenges",
                "_-_ Added 11 new challenges\n" +
                        "_-_ Challenges are unlocked by default"));
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.HUNTRESS, 0, 90, 12, 15), "Classes", "All hero classes are unlocked by default"));

        changes = new ChangeInfo("Challenges", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_OFF), Messages.get(Challenges.class, "amnesia"), Messages.get(Challenges.class, "amnesia_desc")));
        changes.addButton(new ChangeButton(new Embers(), Messages.get(Challenges.class, "cursed"), Messages.get(Challenges.class, "cursed_desc")));
        changes.addButton(new ChangeButton(new BlackjackkeeperSprite(), Messages.get(Challenges.class, "blackjack"), Messages.get(Challenges.class, "blackjack_desc")));
        changes.addButton(new ChangeButton(new SwarmSprite(), Messages.get(Challenges.class, "horde"), Messages.get(Challenges.class, "horde_desc")));
        Image clock = new Image(Assets.Interfaces.BUFFS_LARGE);
        clock.frame(film.get(BuffIndicator.COUNTDOWN1));
        changes.addButton(new ChangeButton(clock, Messages.get(Challenges.class, "countdown"), Messages.get(Challenges.class, "countdown_desc")));
        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_OFF), Messages.get(Challenges.class, "analgesia"), Messages.get(Challenges.class, "analgesia_desc")));
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.ITEM_ICONS, 56, 16, 8, 8), Messages.get(Challenges.class, "big_levels"), Messages.get(Challenges.class, "big_levels_desc")));
        changes.addButton(new ChangeButton(new AlbinoSprite(), Messages.get(Challenges.class, "mutagen"), Messages.get(Challenges.class, "mutagen_desc")));
        changes.addButton(new ChangeButton(new Ankh(), Messages.get(Challenges.class, "resurrection"), Messages.get(Challenges.class, "resurrection_desc")));
        changes.addButton(new ChangeButton(new Image(Assets.Environment.TERRAIN_FEATURES, 112, 0, 16, 16), Messages.get(Challenges.class, "extreme_caution"), Messages.get(Challenges.class, "extreme_caution_desc")));
        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_OFF), Messages.get(Challenges.class, "extermination"), Messages.get(Challenges.class, "extermination_desc")));
    }


}
