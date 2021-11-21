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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MirrorWraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Ankh;
import com.shatteredpixel.shatteredpixeldungeon.items.LostBackpack;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Embers;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.AlbinoSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlackjackkeeperSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrabSprite;
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
import com.watabou.utils.PointF;

import java.util.ArrayList;

public class Cruel_Changes {
    private static TextureFilm film;

    public static void addAllChanges(ArrayList<ChangeInfo> changeInfos) {
        SmartTexture icons = TextureCache.get(Assets.Interfaces.BUFFS_LARGE);
        film = new TextureFilm(icons, 16, 16);

        add_v1_0_0_Changes(changeInfos);
        add_v0_4_0_Changes(changeInfos);
        add_v0_3_0_Changes(changeInfos);
        add_v0_2_0_Changes(changeInfos);
        add_v0_1_0_Changes(changeInfos);
    }

    public static void add_v1_0_0_Changes(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = new ChangeInfo("v1.0.0", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo("v1.0.4", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ Tweaked Second Try"
        ));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "_-_ Fixed slow enemies glitch\n" +
                        "_-_ Fixed Mirror Wrath perish time\n" +
                        "_-_ Fixed Countdown icon fade"
        ));

        changes = new ChangeInfo("v1.0.3", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ Added 3 new challenges\n" +
                        "_-_ Removed Korean MMO and Grindlands\n" +
                        "_-_ Tweaked Fort Knox\n" +
                        "_-_ Tweaked Flowing Champion\n" +
                        "_-_ Tweaked Insomnia\n" +
                        "_-_ Moved all enlargement challenges to Modifiers"
        ));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                        "_-_ Fixed crash related to Combo ability\n" +
                                "_-_ Fixed crash related to Scroll of Enchantment\n" +
                                "_-_ Fixed crash related to Dynasties\n" +
                                "_-_ Fixed crashes related to character movement\n" +
                                "_-_ Fixed issues with Retiered\n" +
                                "_-_ Fixed missing Blackjack room on floor 21\n" +
                                "_-_ Fixed chains issues with Straight Path\n" +
                                "_-_ Fixed multiple issues related to Dance Dance\n" +
                                "_-_ Fixed missing pit room in even more crashes\n" +
                                "_-_ Speculatively fixed freezes with King of a Hill"
        ));

        changes.addButton(new ChangeButton(new Ankh(), Messages.get(Challenges.class, "repopulation"), Messages.get(Challenges.class, "repopulation_desc")));
        changes.addButton(new ChangeButton(new RotHeartSprite(), Messages.get(Challenges.class, "fractal_hive"), Messages.get(Challenges.class, "fractal_hive_desc")));
        changes.addButton(new ChangeButton(new LostBackpack(), Messages.get(Challenges.class, "second_try"), Messages.get(Challenges.class, "second_try_desc")));

        changes = new ChangeInfo("v1.0.2", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ Added 1 new challenge\n" +
                        "_-_ Hail to the King no longer depends on Elite Champions\n" +
                        "_-_ Nerfed Stone champion\n" +
                        "_-_ Reworked Scorched Earth interaction with Brimstone"
        ));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                        "_-_ Fixed crash related to mob position conflicts\n" +
                                "_-_ Fixed crash related to concurrent modification while trying to displace a mob\n" +
                                "_-_ Fixed crash related to missing mob FoV\n" +
                                "_-_ Fixed rankings sorting\n" +
                                "_-_ Added debug info button in game menu"
        ));
        CharSprite sp = new CrabSprite();
        sp.color(0xFFFF00);
        changes.addButton(new ChangeButton(sp, Messages.get(Challenges.class, "crab_rave"), Messages.get(Challenges.class, "crab_rave_desc")));

        changes = new ChangeInfo("v1.0.1", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ Added 1 new modifier\n" +
                        "_-_ Reworked/nerfed Summoning Champion\n" +
                        "_-_ Nerfed Stone champion\n" +
                        "_-_ Nerfed swarm/swarming champions interactions with champion titles" +
                        "_-_ Champions can no longer reduce received damage to 0 when attacked by a hero\n" +
                        "_-_ Fixed typos in challenges descriptions"
        ));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                        "_-_ Fixed various of crashes caused by recent code optimizations"
        ));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Updated to SHPD v1.0.3\n" +
                        "_-_ Major optimizations to game code for better performance with large mobs amounts"));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ Added 5 new challenges and 2 modifiers\n" +
                        "_-_ Added 3 new champion titles and 3 new elite champion titles\n" +
                        "_-_ Limited Revenge Rage attack bonus to _Over 9000!_\n" +
                        "_-_ Champions are now using deck-based spawning system\n" +
                        "_-_ Tweaked Korean MMO again..."
        ));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Fixed a lot of bugs, for full list of bug fixes refer to commit history"
        ));

        changes = new ChangeInfo("Challenges", false, null);
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL), Messages.get(Challenges.class, "king_of_a_hill"), Messages.get(Challenges.class, "king_of_a_hill_desc")));
        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), Messages.get(Challenges.class, "hail_to_the_king"), Messages.get(Challenges.class, "hail_to_the_king_desc")));

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.ITEMS, 64, 32, 16, 14), Messages.get(Challenges.class, "mimics"), Messages.get(Challenges.class, "mimics_desc")));
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.ITEMS, 80, 32, 16, 14), Messages.get(Challenges.class, "mimics_2"), Messages.get(Challenges.class, "mimics_2_desc")));
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.ITEMS, 96, 32, 16, 14), Messages.get(Challenges.class, "mimics_grind"), Messages.get(Challenges.class, "mimics_grind_desc")));
    }

    public static void add_v0_4_0_Changes(ArrayList<ChangeInfo> changeInfos) {
        ChangeInfo changes = new ChangeInfo("v0.4.4", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo("v0.4.4b & v0.4.4c", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ More reworks to MMO modifier"
        ));

        changes = new ChangeInfo("v0.4.4a", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton(new ChangeButton(Icons.get(Icons.PREFS), Messages.get(ChangesScene.class, "misc"),
                "_-_ Updated to SHPD v1.0.0\n" +
                        "_-_ Enabled update checker"));
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Fixed major bug influencing damage multipliers\n" +
                        "_-_ Fixed couple of bugs caused by extreme conditions\n" +
                        "_-_ Fixed even more levelgen issues"
        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ Added modifiers system and 5 modifiers!\n" +
                        "_-_ Improved challenges selection window\n" +
                        "_-_ Tweaked Intoxication\n" +
                        "_-_ Brimstone no longer grants immunity to Scorched Earth burning\n" +
                        "_-_ Dance Dance no longer affect sleeping enemies\n" +
                        "_-_ Increased amount of guaranteed loot (food, torches) with level-enlarging challenges"
        ));

        changes = new ChangeInfo(Messages.get(ChangesScene.class, "changes"), false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Fixed Dwarf King freeze in older runs"
        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ Added 8 new challenges, for the grand total of 78 challenges!\n" +
                        "_-_ Reworked Arrowhead challenge\n" +
                        "_-_ Fixed Countdown and Shared Pain descriptions\n" +
                        "_-_ Tweaked Grouping challenge to have more adequate numbers\n" +
                        "_-_ Switched long/short clicks for opening backpack/quick use window when On a Beat is enabled"
        ));

        changes = new ChangeInfo("Challenges", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        Image danceFloorImg = new Image(Assets.Environment.DANCE_FLOOR, 0, 0, 16, 16);
        danceFloorImg.color(0xFF0000);
        changes.addButton(new ChangeButton(danceFloorImg, Messages.get(Challenges.class, "dance_floor"), Messages.get(Challenges.class, "dance_floor_desc")));
        danceFloorImg = new Image(Assets.Environment.DANCE_FLOOR, 16, 0, 16, 16);
        danceFloorImg.color(0x00FF00);
        changes.addButton(new ChangeButton(danceFloorImg, Messages.get(Challenges.class, "humppa"), Messages.get(Challenges.class, "humppa_desc")));
        danceFloorImg = new Image(Assets.Environment.DANCE_FLOOR, 32, 0, 16, 16);
        danceFloorImg.color(0x0000FF);
        changes.addButton(new ChangeButton(danceFloorImg, Messages.get(Challenges.class, "the_last_waltz"), Messages.get(Challenges.class, "the_last_waltz_desc")));
        changes.addButton(new ChangeButton(new Ankh(), Messages.get(Challenges.class, "tumbler"), Messages.get(Challenges.class, "tumbler_desc")));
        changes.addButton(new ChangeButton(new Ankh().bless(), Messages.get(Challenges.class, "saving_grace"), Messages.get(Challenges.class, "saving_grace_desc")));
        changes.addButton(new ChangeButton(new Image(Assets.Environment.TERRAIN_FEATURES, 128, 0, 16, 16), Messages.get(Challenges.class, "repeater"), Messages.get(Challenges.class, "repeater_desc")));

        Image rage = new Image(Assets.Interfaces.BUFFS_LARGE);
        rage.frame(film.get(BuffIndicator.FURY));
        changes.addButton(new ChangeButton(rage, Messages.get(Challenges.class, "revenge"), Messages.get(Challenges.class, "revenge_desc")));

        rage = new Image(Assets.Interfaces.BUFFS_LARGE);
        rage.frame(film.get(BuffIndicator.RAGE));
        changes.addButton(new ChangeButton(rage, Messages.get(Challenges.class, "revenge_fury"), Messages.get(Challenges.class, "revenge_fury_desc")));

        Image vulnerable = new Image(Assets.Interfaces.BUFFS_LARGE);
        vulnerable.frame(film.get(BuffIndicator.VULNERABLE));
        changes.addButton(new ChangeButton(vulnerable, Messages.get(Challenges.class, "arrowhead"), Messages.get(Challenges.class, "arrowhead_desc")));

        changes = new ChangeInfo("v0.4.3", true, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);

        changes = new ChangeInfo("v0.4.3d & v0.4.3e", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Fixed Retiered/Untiered sometimes not applying their bonus\n" +
                        "_-_ Fixed Patch room causing freeze with negative fill\n" +
                        "_-_ Fixed seeds description when Lobotomy is enabled\n" +
                        "_-_ Fixed Dwarf King freeze with Shared Pain\n" +
                        "_-_ Fixed Armored statue causing freeze with Cursed challenge\n" +
                        "_-_ Fixed more levelgen issues"
        ));
        changes.addButton(new ChangeButton(new TimekeepersHourglass(), "Balance changes",
                "Balance Changes:\n" +
                        "_-_ Ring of Force and missile weapons are now affected by Untiered\n" +
                        "_-_ Blackjack shops now use their own currency instead of gold\n" +
                        "_-_ Piranhas and Living Statues are now affected by challenges that increase mob count\n" +
                        "_-_ When shared pain is enabled, bosses take full damage when attacked, but no damage from shared pain caused by other mobs. When attacking a boss it still causes Shared Pain damage to other enemies as much as 50% of the damage taken by a boss."
        ));

        changes = new ChangeInfo("v0.4.3c", false, "");
        changes.hardlight(Window.TITLE_COLOR);
        changeInfos.add(changes);
        changes.addButton(new ChangeButton(new Image(Assets.Sprites.SPINNER, 144, 0, 16, 16), Messages.get(ChangesScene.class, "bugfixes"),
                "Fixed:\n" +
                        "_-_ Fixed patch rooms causing level generation to stuck\n" +
                        "_-_ Fixed Straight Path causing level generation to stuck\n" +
                        "_-_ Fixed champions having all titles at once\n" +
                        "_-_ Fixed crash causes by using Mind Blast in Demon Halls\n" +
                        "_-_ Fixed excessive mob revealing with Extermination"
        ));
        changes.addButton(new ChangeButton(new TimekeepersHourglass(), "Balance changes",
                "Balance Changes:\n" +
                        "_-_ Summoning trap will now spawn more mobs is challenges increase mob count\n" +
                        "_-_ Stacking Threat challenge now allow enemies to spawn on top of one another\n" +
                        "_-_ Champions can now have multiple of the same title\n" +
                        "_-_ Increased Scorched Earth proc delay\n" +
                        "_-_ Manifesting Myriads no longer remove EXP and loot from Legion waves"
        ));

        changes.addButton(new ChangeButton(Icons.get(Icons.CHALLENGE_HELL2), "Challenges",
                "_-_ Added 4 new challenges\n" +
                        "_-_ Amulet of Yendor can now be upgraded on runs with difficulty of Hard or above\n" +
                        "_-_ Added a badge for upgrading Amulet of Yendor to +15\n" +
                        "_-_ Legion waves are now spawned in clusters of 50 mobs instead of spreading around the level"
        ));
        changes.addButton(new ChangeButton(new RunicBlade(), Messages.get(Challenges.class, "retiered"), Messages.get(Challenges.class, "retiered_desc")));
        changes.addButton(new ChangeButton(new Flail(), Messages.get(Challenges.class, "untiered"), Messages.get(Challenges.class, "untiered_desc")));
        changes.addButton(new ChangeButton(new Image(Assets.Environment.TILES_SEWERS, 0, 80, 16, 16), Messages.get(Challenges.class, "barrier_breaker"), Messages.get(Challenges.class, "barrier_breaker_desc")));
        changes.addButton(new ChangeButton(new Image(Assets.Effects.SPECKS, 28, 0, 7, 7), Messages.get(Challenges.class, "limited_upgrades"), Messages.get(Challenges.class, "limited_upgrades_desc")));


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
        vulnerable = new Image(Assets.Interfaces.BUFFS_LARGE);
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
