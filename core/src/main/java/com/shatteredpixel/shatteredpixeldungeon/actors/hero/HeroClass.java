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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Arrowhead;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpectralBlades;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.ElementalBlast;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WarpBeacon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Shockwave;
import com.shatteredpixel.shatteredpixeldungeon.debug.PotionOfDebug;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.debug.ScrollOfDebug;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.AquaBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ForceCube;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.watabou.utils.DeviceCompat;

public enum HeroClass {

    WARRIOR(HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR),
    MAGE(HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK),
    ROGUE(HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER),
    HUNTRESS(HeroSubClass.SNIPER, HeroSubClass.WARDEN);

    private final HeroSubClass[] subClasses;

    HeroClass(HeroSubClass... subClasses) {
        this.subClasses = subClasses;
    }

    private static void initWarrior(Hero hero) {
        (hero.belongings.weapon = new WornShortsword()).identify();
        ThrowingStone stones = new ThrowingStone();
        stones.quantity(3).collect();
        Dungeon.quickslot.setSlot(0, stones);

        if (hero.belongings.armor != null) {
            hero.belongings.armor.affixSeal(new BrokenSeal());
        }

        new PotionOfHealing().identify();
        new ScrollOfRage().identify();
    }

    private static void initMage(Hero hero) {
        MagesStaff staff;

        staff = new MagesStaff(new WandOfMagicMissile());

        (hero.belongings.weapon = staff).identify();
        hero.belongings.weapon.activate(hero);

        Dungeon.quickslot.setSlot(0, staff);

        new ScrollOfUpgrade().identify();
        new PotionOfLiquidFlame().identify();
    }

    private static void initRogue(Hero hero) {
        (hero.belongings.weapon = new Dagger()).identify();

        CloakOfShadows cloak = new CloakOfShadows();
        (hero.belongings.artifact = cloak).identify();
        hero.belongings.artifact.activate(hero);

        ThrowingKnife knives = new ThrowingKnife();
        knives.quantity(3).collect();

        Dungeon.quickslot.setSlot(0, cloak);
        Dungeon.quickslot.setSlot(1, knives);

        new ScrollOfMagicMapping().identify();
        new PotionOfInvisibility().identify();
    }

    private static void initHuntress(Hero hero) {

        (hero.belongings.weapon = new Gloves()).identify();
        SpiritBow bow = new SpiritBow();
        bow.identify().collect();

        if (Challenges.CURSE_ENCHANT.enabled()) bow.enchant();

        Dungeon.quickslot.setSlot(0, bow);

        new PotionOfMindVision().identify();
        new ScrollOfLullaby().identify();
    }

    public void initHero(Hero hero) {

        hero.heroClass = this;
        Talent.initClassTalents(hero);

        Item i = new ClothArmor().identify();
        if (!Challenges.isItemBlocked(i)) hero.belongings.armor = (ClothArmor) i;

        i = new Food();
        if (!Challenges.isItemBlocked(i)) i.collect();

        new VelvetPouch().collect();
        Dungeon.LimitedDrops.VELVET_POUCH.drop();

        Waterskin waterskin = new Waterskin();
        waterskin.collect();

        new ScrollOfIdentify().identify();

        switch (this) {
            case WARRIOR:
                initWarrior(hero);
                break;

            case MAGE:
                initMage(hero);
                break;

            case ROGUE:
                initRogue(hero);
                break;

            case HUNTRESS:
                initHuntress(hero);
                break;
        }
        if (Challenges.CURSE_ENCHANT.enabled()) {
            ((Weapon) hero.belongings.weapon).enchant();
            hero.belongings.armor.inscribe();
        }
        if (Challenges.SCORCHED_EARTH.enabled()) {
            new AquaBlast().quantity(2).collect();
        }

        if (Challenges.HEADSTART.enabled()) {
            new ScrollOfUpgrade().quantity(2).collect();
            Dungeon.LimitedDrops.UPGRADE_SCROLLS.drop();
            Dungeon.LimitedDrops.UPGRADE_SCROLLS.drop();
        }

        if(Challenges.WARM_WELCOME.enabled()){
            new Firebloom.Seed().collect();
        }

        if (DeviceCompat.isDebug() || Challenges.DEBUG.enabled()) {
            new ScrollOfDebug().identify().collect();
            new PotionOfDebug().identify().collect();
		}
        if (DeviceCompat.isDebug()) {
            new ScrollOfIdentify().identify();

            new PotionOfMindVision().quantity(64).identify().collect();
            new PotionOfInvisibility().quantity(64).identify().collect();
            new ScrollOfMagicMapping().quantity(64).identify().collect();
            new Amulet().collect();

            new ScrollOfRage().quantity(64).identify().collect();
//			new ScrollOfUpgrade().quantity(100).identify().collect();
//			new ScrollOfTransmutation().quantity(100).identify().collect();
            new Stylus().identify().collect();
            new StoneOfIntuition().identify().collect();
            new StoneOfIntuition().identify().collect();
            new ScaleArmor().random().collect();
//			new ScrollOfIdentify().quantity(1).identify().collect();
//			new ScrollOfRemoveCurse().quantity(1).identify().collect();
            new ScrollOfPsionicBlast().quantity(1).identify().collect();
//			new ScrollOfDivination().quantity(1).identify().collect();
//			new ScrollOfForesight().quantity(1).identify().collect();
//			new StoneOfAugmentation().quantity(1).identify().collect();
//			new StoneOfIntuition().quantity(1).identify().collect();
//			new ScrollOfRecharging().quantity(100).identify().collect();
//			new Stylus().quantity(1).identify().collect();
//            new ReclaimTrap().setStoredTrap(FlockTrap.class).collect();
//			new ReclaimTrap().setStoredTrap(DistortionTrap.class).collect();
//			Generator.randomWeapon(0).identify().collect();
//			Generator.randomWeapon(1).identify().collect();
//			Generator.randomWeapon(2).identify().collect();
//			Generator.randomWeapon(3).identify().collect();
//			Generator.randomWeapon(4).identify().collect();
            new ForceCube().random().identify().collect();
            new ForceCube().random().identify().collect();
            new ForceCube().random().identify().collect();
//			new WandOfDisintegration().upgrade(5000000).identify().collect();
            new Shortsword().enchant(new Elastic()).upgrade(0).identify().collect();
//			new Blindweed.Seed().collect();
//			hero.STR = 22;

//			while(hero.lvl<30){
//				hero.earnExp(hero.maxExp(), PotionOfExperience.class);
//			}
//			hero.earnExp(Hero.maxExp(30),hero.getClass());
//			new PlateArmor().upgrade(100).collect();
//			new Longsword().upgrade(10).identify().collect();
            new PotionOfHealing().quantity(100).collect();

            Dungeon.gold = 1000000;

//			Buff.affect(hero, MindVision.class,1000000);
//			Buff.affect(hero, Invisibility.class,1000000);
//			new ScrollOfUpgrade().quantity(10).collect();
        }

        for (int s = 0; s < QuickSlot.SIZE; s++) {
            Item item = Dungeon.quickslot.getItem(s);
            if (item == null) {
                Dungeon.quickslot.setSlot(s, waterskin);
                break;
            } else if (!hero.belongings.contains(item)) {
                Dungeon.quickslot.clearSlot(s);
            }
        }

        if (Challenges.CHAOS_WIZARD.enabled() && hero.belongings.weapon != null) {
            hero.belongings.weapon = null;
            Generator.random(Generator.Category.WAND).identify().curse().collect();
        }

        if ( Challenges.THUNDERSTRUCK.enabled() ) {
            Buff.affect(hero, Arrowhead.class ).set(9001);
        }
    }

    public Badges.Badge masteryBadge() {
        switch (this) {
            case WARRIOR:
                return Badges.Badge.MASTERY_WARRIOR;
            case MAGE:
                return Badges.Badge.MASTERY_MAGE;
            case ROGUE:
                return Badges.Badge.MASTERY_ROGUE;
            case HUNTRESS:
                return Badges.Badge.MASTERY_HUNTRESS;
        }
        return null;
    }

    public String title() {
        return Messages.get(HeroClass.class, name());
    }

    public String desc() {
        return Messages.get(HeroClass.class, name() + "_desc");
    }

    public HeroSubClass[] subClasses() {
        return subClasses;
    }

    public ArmorAbility[] armorAbilities() {
        switch (this) {
            case WARRIOR:
            default:
                return new ArmorAbility[]{new HeroicLeap(), new Shockwave(), new Endure()};
            case MAGE:
                return new ArmorAbility[]{new ElementalBlast(), new WildMagic(), new WarpBeacon()};
            case ROGUE:
                return new ArmorAbility[]{new SmokeBomb(), new DeathMark(), new ShadowClone()};
            case HUNTRESS:
                return new ArmorAbility[]{new SpectralBlades(), new NaturesPower(), new SpiritHawk()};
        }
    }

    public String spritesheet() {
        switch (this) {
            case WARRIOR:
            default:
                return Assets.Sprites.WARRIOR;
            case MAGE:
                return Assets.Sprites.MAGE;
            case ROGUE:
                return Assets.Sprites.ROGUE;
            case HUNTRESS:
                return Assets.Sprites.HUNTRESS;
        }
    }

    public String splashArt() {
        switch (this) {
            case WARRIOR:
            default:
                return Assets.Splashes.WARRIOR;
            case MAGE:
                return Assets.Splashes.MAGE;
            case ROGUE:
                return Assets.Splashes.ROGUE;
            case HUNTRESS:
                return Assets.Splashes.HUNTRESS;
        }
    }

    public String[] perks() {
        switch (this) {
            case WARRIOR:
            default:
                return new String[]{
                        Messages.get(HeroClass.class, "warrior_perk1"),
                        Messages.get(HeroClass.class, "warrior_perk2"),
                        Messages.get(HeroClass.class, "warrior_perk3"),
                        Messages.get(HeroClass.class, "warrior_perk4"),
                        Messages.get(HeroClass.class, "warrior_perk5"),
                };
            case MAGE:
                return new String[]{
                        Messages.get(HeroClass.class, "mage_perk1"),
                        Messages.get(HeroClass.class, "mage_perk2"),
                        Messages.get(HeroClass.class, "mage_perk3"),
                        Messages.get(HeroClass.class, "mage_perk4"),
                        Messages.get(HeroClass.class, "mage_perk5"),
                };
            case ROGUE:
                return new String[]{
                        Messages.get(HeroClass.class, "rogue_perk1"),
                        Messages.get(HeroClass.class, "rogue_perk2"),
                        Messages.get(HeroClass.class, "rogue_perk3"),
                        Messages.get(HeroClass.class, "rogue_perk4"),
                        Messages.get(HeroClass.class, "rogue_perk5"),
                };
            case HUNTRESS:
                return new String[]{
                        Messages.get(HeroClass.class, "huntress_perk1"),
                        Messages.get(HeroClass.class, "huntress_perk2"),
                        Messages.get(HeroClass.class, "huntress_perk3"),
                        Messages.get(HeroClass.class, "huntress_perk4"),
                        Messages.get(HeroClass.class, "huntress_perk5"),
                };
        }
    }

    public boolean isUnlocked() {
        //always unlock on debug builds
        if (DeviceCompat.isDebug()) return true;

        //always unlocked in Too Cruel Pixel Dungeon
        return true;
		
		/*switch (this){
			case WARRIOR: default:
				return true;
			case MAGE:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE);
			case ROGUE:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE);
			case HUNTRESS:
				return Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS);
		}*/
    }

    public String unlockMsg() {
        switch (this) {
            case WARRIOR:
            default:
                return "";
            case MAGE:
                return Messages.get(HeroClass.class, "mage_unlock");
            case ROGUE:
                return Messages.get(HeroClass.class, "rogue_unlock");
            case HUNTRESS:
                return Messages.get(HeroClass.class, "huntress_unlock");
        }
    }

}
