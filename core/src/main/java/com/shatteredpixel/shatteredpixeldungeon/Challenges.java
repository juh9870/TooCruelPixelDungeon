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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Arrowhead;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ascension;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Extermination;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Revealing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfDivination;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMysticalEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.InventoryStone;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.killers.SharedPain;
import com.watabou.utils.Random;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;

public enum Challenges implements Hero.Doom {
	//region T1
	ON_DIET( 0, 1, 1 ),
	FAITH_ARMOR( 1, 1, 2 ),
	PHARMACOPHOBIA( 2, 1, 2 ),
	BARREN_LAND( 3, 1, 2 ) {
		@Override
		protected boolean _isItemBlocked( Item item ) {
			return item instanceof Dewdrop;
		}
	},
	SWARM_INTELLIGENCE( 4, 1, 1.5f ),
	DARKNESS( 5, 1, 1 ),
	/**
	 * 90 - {@link com.shatteredpixel.shatteredpixeldungeon.Challenges#BIOCHIP}
	 */
	CHAMPION_ENEMIES( 19, 1, 2f, reqs( 90 ) ) {
		@Override
		public String description() {
			return super.description() + "\n" + ChampionEnemy.description( new ChampionEnemy.NormalChampionsDeck().values );
		}
	},
	STRONGER_BOSSES( 56, 1, 2f ),
	FORBIDDEN_RUNES( 6, 1, 1.5f ),
	AMNESIA( 7, 1, 1.5f ),
	CURSED( 8, 1, 2 ),
	BLACKJACK( 9, 1, 2 ),
	HORDE( 10, 1, 1f ) {
		@Override
		protected float _nMobsMult() {
			return 2;
		}
	},
	COUNTDOWN( 11, 1, 2 ),
	ANALGESIA( 12, 1, 1.5f ),
	SMALL_LEVELS( 39, 1, 1f ) {
		@Override
		protected float _nRoomsMult() {
			return 0.5f;
		}

		@Override
		protected float _nLootMult() {
			return 0.5f;
		}
	},
	MUTAGEN( 14, 1, 2 ) {
		@Override
		protected float _rareLootChanceMultiplier() {
			if ( EVOLUTION.enabled() ) return 1 / 50f;
			return 4 / 50f;
		}
	},
	REPOPULATION( 15, 1, 2f ),
	FRACTAL_HIVE( 94, 1, 2f, REPOPULATION ),
	EXTREME_CAUTION( 16, 1, 1 ) {
		@Override
		protected float _nTrapsMult() {
			return 2;
		}
	},
	EXTREME_DANGER( 32, 1, 2f, EXTREME_CAUTION ),
	INDIFFERENT_DESIGN( 63, 1, 1f, EXTREME_CAUTION ),
	REPEATER( 75, 1, 1f, EXTREME_CAUTION ),
	DUPLICATOR( 96, 1, 2f, REPEATER ),
	CONCEALMENT( 101, 1, 1f, EXTREME_CAUTION ),
	EXTERMINATION( 17, 1, 1 ),
	STACKING( 18, 1, 1.5f ) {
		@Override
		protected float _nMobsMult() {
			return 1.5f;
		}
	},
	NO_PERKS( 20, 1, 3f ),
	BLOODBAG( 41, 1, 2f ),
	MIRROR_OF_RAGE( 45, 1, 2f ),
	RETIERED( 66, 1, 1f ),
	BARRIER_BREAKER( 68, 1, 1f ),
	TUMBLER( 71, 1, 1f ),
	REVENGE( 76, 1, 1.5f ),
	MIMICS( 86, 1, 1f ),
	//endregion


	//region T2
	FAMINE( 21, 2, 1.5f, ON_DIET ) {
		@Override
		protected boolean _isItemBlocked( Item item ) {
			if ( item instanceof Food && !(item instanceof SmallRation) ) {
				return true;
			} else return item instanceof HornOfPlenty;
		}
	},
	INSOMNIA( 44, 2, 2f, SWARM_INTELLIGENCE ),
	INTOXICATION( 22, 2, 2.5f ),
	PLAGUE( 23, 2, 3f, INTOXICATION ),
	BLINDNESS( 25, 2, 3f, DARKNESS ),
	LOBOTOMY( 26, 2, 3f, AMNESIA ),
	INVASION( 27, 2, 2f ),
	EVOLUTION( 29, 2, 5f, MUTAGEN ),
	RESURRECTION( 93, 2, 4f, REPOPULATION ),
	REBIRTH( 30, 2, 4f ),
	CHAOTIC_CONSTRUCTION( 64, 2, 2f, INDIFFERENT_DESIGN ),
	ELITE_CHAMPIONS( 33, 2, 4f, CHAMPION_ENEMIES ) {
		@Override
		public String description() {
			return super.description() + "\n" + ChampionEnemy.description( new ChampionEnemy.EliteChampionsDeck().values );
		}
	},
	STACKING_SPAWN( 57, 2, 2f, STACKING ),
	STACKING_CHAMPIONS( 60, 2, 2f, STACKING, CHAMPION_ENEMIES ),
	LEGION( 28, 2, 4f ),
	ARROWHEAD( 40, 2, 2.5f ),
	CURSE_MAGNET( 42, 2, 2f, CURSED ),
	CURSE_ENCHANT( 43, 2, 2f, CURSED ) {
		@Override
		protected boolean _isItemBlocked( Item item ) {
			return item instanceof RingOfForce;
		}
	},
	ECTOPLASM( 46, 2, 2f, MIRROR_OF_RAGE ),
	THOUGHTLESS( 48, 2, 2.5f ),
	EXHIBITIONISM( 49, 2, 2f, REPOPULATION ),
	MARATHON( 50, 2, 3f ),
	ROOM_LOCK( 59, 2, 2f ),
	SCORCHED_EARTH( 61, 2, 2f ),
	TOUCH_THE_GRASS( 100, 2, 3f, BARREN_LAND, SCORCHED_EARTH ),
	UNTIERED( 67, 2, 2f, RETIERED ),
	LIMITED_UPGRADES( 69, 2, 2f ),
	DANCE_FLOOR( 70, 2, 4f ),
	SAVING_GRACE( 72, 2, 2f, TUMBLER ),
	REVENGE_FURY( 77, 2, 2f, REVENGE ),
	KING_OF_A_HILL( 84, 2, 4f, CHAMPION_ENEMIES ) {
		@Override
		protected float _nMobsMult() {
			return 1.5f;
		}
	},
	MIMICS_2( 87, 2, 3f, MIMICS ),
	AGNOSIA( 89, 2, 2f ),
	WARM_WELCOME( 97, 2, 3f ),
	HEADSTART( 98, 2, 3f ),
	//endregion


	//region T3
	HEART_OF_HIVE( 24, 3, 7f, INSOMNIA ),
	ASCENSION( 31, 3, 7f, RESURRECTION, REBIRTH ),
	DUNGEON_OF_CHAMPIONS( 34, 3, 7f, ELITE_CHAMPIONS ),
	RACING_THE_DEATH( 35, 3, 7f ),
	MANIFESTING_MYRIADS( 36, 3, 7f, LEGION, HORDE ) {
		@Override
		protected float _nMobsMult() {
			return 1.5f;
		}
	},
	SPIRITUAL_CONNECTION( 47, 3, 5f, ECTOPLASM ),
	ON_A_BEAT( 51, 3, 7f, MARATHON, COUNTDOWN ),
	SHARED_PAIN( 58, 3, 7f ),
	DESERT( 62, 3, 6f, SCORCHED_EARTH ),
	TRAP_TESTING_FACILITY( 65, 3, 6f, EXTREME_DANGER, CHAOTIC_CONSTRUCTION ) {
		@Override
		protected float _nTrapsMult() {
			return 3;
		}
	},
	HUMPPA( 74, 3, 7f, DANCE_FLOOR ),
	HAIL_TO_THE_KING( 85, 3, 7f, KING_OF_A_HILL ),
	//endregion


	//region T4
	INFINITY_MOBS( 55, 4, 16f ) {
		@Override
		protected float _nMobsMult() {
			return 1000;
		}
	},

	THE_LAST_WALTZ( 73, 4, 11f, DANCE_FLOOR, MARATHON ),
	CRAB_RAVE( 92, 4, 0f, THE_LAST_WALTZ, AGNOSIA, HUMPPA, ON_A_BEAT ),
	//endregion


	//region Modifiers
	MOONSHINE_WANDS( 103, 5, 0 ),
	ARCHERY_SCHOOL( 78, 5, -2f ),
	SNIPER_TRAINING( 79, 5, -7f, ARCHERY_SCHOOL ),
	BIOCHIP( 90, 5, 0 ) {
		@Override
		public boolean isCheese() {
			return false;
		}
	},
	CHAOS_WIZARD( 80, 5, -4f ) {
		@Override
		protected boolean _isItemBlocked( Item item ) {
			return item instanceof KindOfWeapon || item instanceof RingOfForce;
		}
	},
	GRINDING( 81, 5, -50f ),
	@Deprecated
	GRINDING_2( 82, 5, -300f ) {
		@Override
		public boolean deprecated() {
			return true;
		}
	},
	@Deprecated
	GRINDING_3( 83, 5, 0 ) {
		@Override
		public boolean deprecated() {
			return true;
		}
	},
	MIMICS_GRIND( 88, 5, 0, GRINDING, MIMICS_2 ),
	SLIDING( 91, 5, -2f ),
	THUNDERSTRUCK( 102, 5, -2f, ARROWHEAD ),
	SECOND_TRY( 95, 5, -4f ) {
		@Override
		protected float _nLootMult() {
			return 0;
		}
	},
	BIG_LEVELS( 13, 5, 0 ) {
		@Override
		protected float _nMobsMult() {
			return 2;
		}

		@Override
		protected float _nTrapsMult() {
			return 2;
		}

		@Override
		protected float _nRoomsMult() {
			return 2;
		}
	},
	BIGGER_LEVELS( 37, 5, 0, BIG_LEVELS ) {
		@Override
		protected float _nMobsMult() {
			return 2;
		}

		@Override
		protected float _nTrapsMult() {
			return 2;
		}

		@Override
		protected float _nRoomsMult() {
			return 2;
		}
	},
	LINEAR( 52, 5, 0, BIG_LEVELS ) {
		@Override
		protected float _nRoomsMult() {
			return 1.5f;
		}

		@Override
		protected float _nMobsMult() {
			return 1.5f;
		}
	},
	HUGE_LEVELS( 38, 5, 0, BIGGER_LEVELS ) {
		@Override
		protected float _nMobsMult() {
			return 3;
		}

		@Override
		protected float _nTrapsMult() {
			return 3;
		}

		@Override
		protected float _nRoomsMult() {
			return 3;
		}
	},
	BIG_ROOMS( 53, 5, 0 ) {
		@Override
		protected float _nMobsMult() {
			return 2;
		}

		@Override
		protected float _nTrapsMult() {
			return 2;
		}

		@Override
		protected float _nRoomsMult() {
			float mult = 1;
			if ( BIG_LEVELS.enabled() ) mult *= 0.87f;
			if ( BIGGER_LEVELS.enabled() ) mult *= 0.87f;
			if ( HUGE_LEVELS.enabled() ) mult *= 0.7;
			return mult;
		}

		@Override
		protected float _roomSizeMult() {
			return 2;
		}
	},
	BIGGER_ROOMS( 54, 5, 0, BIG_ROOMS ) {
		@Override
		protected float _nMobsMult() {
			return 2;
		}

		@Override
		protected float _nTrapsMult() {
			return 2;
		}

		@Override
		protected float _roomSizeMult() {
			return 2;
		}

		@Override
		protected float _nRoomsMult() {
			float mult = 1;
			if ( BIG_LEVELS.enabled() ) mult *= 0.87f;
			if ( BIGGER_LEVELS.enabled() ) mult *= 0.87f;
			if ( HUGE_LEVELS.enabled() ) mult *= 0.7;
			return mult;
		}
	},

	DEBUG( 99, 5, -9001 ),

	//endregion

	//Last id 102
	;
	private static final Challenges[] mappings;
	public static int LEVEL_LIMIT = 3;

	static {
		mappings = new Challenges[Challenges.values().length];
		try {
			for (Challenges value : Challenges.values()) {
				if ( mappings[value.id] != null ) throw new DuplicateChallengeException( mappings[value.id], value );
				mappings[value.id] = value;
			}
		} catch ( IndexOutOfBoundsException e ) {
			throw new ChallengeOutOfBoundsException( e );
		}
		for (int i = 0; i < mappings.length; i++) {
			if ( mappings[i] == null ) throw new MissingChallengeException( i );
		}
	}

	public final String name;
	public final int id;
	public final float difficulty;
	public final int tier;
	public final int[] requirements;

	Challenges( int id, int tier, float difficulty, Challenges... requirements ) {
		this( id, tier, difficulty, reqs( requirements ) );
	}

	Challenges( int id, int tier, float difficulty, Requirement[] requirements ) {
		this.name = name().toLowerCase();
		this.id = id;
		this.difficulty = difficulty;
		this.tier = tier;
		this.requirements = new int[requirements.length];
		for (int i = 0; i < requirements.length; i++) {
			this.requirements[i] = requirements[i].id;
		}
	}

	public static void enableFromLegacyTier( boolean[] chals, Challenges chal, int tier ) {
		switch (chal) {
			case ON_DIET:
				chals[FAMINE.id] = tier == 2;
				break;
			case PHARMACOPHOBIA:
				chals[INTOXICATION.id] = tier > 1;
				chals[PLAGUE.id] = tier == 3;
				break;
			case SWARM_INTELLIGENCE:
				chals[HEART_OF_HIVE.id] = tier == 2;
				break;
			case AMNESIA:
				chals[LOBOTOMY.id] = tier == 2;
				break;
			case HORDE:
				chals[INVASION.id] = tier > 1;
				chals[LEGION.id] = tier == 3;
				break;
			case COUNTDOWN:
				chals[RACING_THE_DEATH.id] = tier == 2;
				break;
			case REPOPULATION:
				chals[REBIRTH.id] = tier > 1;
				chals[ASCENSION.id] = tier == 3;
				break;
			case EXTREME_CAUTION:
				chals[EXTREME_DANGER.id] = tier == 2;
				break;
			case CHAMPION_ENEMIES:
				chals[ELITE_CHAMPIONS.id] = tier > 1;
				chals[DUNGEON_OF_CHAMPIONS.id] = tier == 3;
				break;
		}
	}

	public static Challenges fromId( int id ) {
		return mappings[id];
	}

	public static float ascendingChance( Mob m ) {

		Ascension buff;
		if ( (buff = m.buff( Ascension.class )) != null ) {
			if ( buff.level >= 2 && m.buff( Extermination.class ) != null ) return 0;
		}
		if ( m.buff( Ascension.ForcedAscension.class ) != null ) return 1;

		float chance = .33f;

		float _m = Math.max( m.maxLvl, Dungeon.scalingFactor() );
		float h = Dungeon.hero.lvl;
		float a = buff == null ? 0 : buff.level;

		if ( Dungeon.hero.lvl > _m ) {

			float o = Math.max( 0, 10 - _m );

			chance = .33f + ((1 - .33f) * ((h - _m + o) * 30 / (35 - o - _m))) / 30;
		}

		if ( Statistics.amuletObtained && chance < .66 ) {
			chance = .66f;
		}

		if ( a > 0 ) {
			if ( h <= _m ) chance *= 0.5 / Math.sqrt( a );
			if ( h <= _m + 2 ) chance *= 0.75 / Math.sqrt( a );
		}

		return chance;
	}

	public static int maxAscension( Mob m ) {
		if ( ASCENSION.enabled() || m.buff( Ascension.ForcedAscension.class ) != null ) {
			return 6;
		} else if ( REBIRTH.enabled() ) {
			return 1;
		}
		return 0;
	}

	public static float nMobsMultiplier() {
		float mult = 1;
		for (Challenges ch : values()) {
			if ( ch.enabled() ) mult *= ch._nMobsMult();
		}
		return mult;
	}

	public static float nLootMultiplier() {
		float mult = 1;
		for (Challenges ch : values()) {
			if ( ch.enabled() ) mult *= ch._nLootMult();
		}
		if ( Challenges.GRINDING.enabled() ) {
			mult *= Math.max( 1, Challenges.roomSizeMult() * Challenges.nRoomsMult() );
		}
		return mult;
	}

	public static float nRoomsMult() {
		float mult = 1;
		for (Challenges ch : values()) {
			if ( ch.enabled() ) mult *= ch._nRoomsMult();
		}
		return mult;
	}

	public static float roomSizeMult() {
		float mult = 1;
		for (Challenges ch : values()) {
			if ( ch.enabled() ) mult *= ch._roomSizeMult();
		}
		return mult;
	}

	public static float nTrapsMultiplier() {
		float mult = 1;
		for (Challenges ch : values()) {
			if ( ch.enabled() ) mult *= ch._nTrapsMult();
		}
		return mult;
	}

	public static float rareLootChanceMultiplier() {
		float mult = 1;
		for (Challenges ch : values()) {
			if ( ch.enabled() ) mult *= ch._rareLootChanceMultiplier();
		}
		return mult;
	}

	public static boolean isItemBlocked( Item item ) {
		for (Challenges ch : values()) {
			if ( ch.enabled() && ch._isItemBlocked( item ) ) return true;
		}
		return false;
	}

	public static void initGenerator() {
		for (Challenges ch : values()) {
			ch._initGenerator( ch.enabled() );
		}
	}

	public static int checkExterminators() {
		return checkExterminators( false );
	}

	public static int checkExterminators( boolean reveal ) {
		int left = 0;
		if ( EXTERMINATION.enabled() )
			for (Mob m : Dungeon.level.mobs()) {
				if ( m.buff( Extermination.class ) != null ) {
					left++;
					if ( reveal ) Buff.affect( m, Revealing.class, 1f );
				}
			}
		return left;
	}

	public static boolean isItemAutouse( Item item ) {
		if ( THOUGHTLESS.enabled() ) {
			if ( item instanceof InventoryScroll ) return true;
			if ( item instanceof ScrollOfMagicMapping ) return true;
			if ( item instanceof ScrollOfRecharging ) return true;
			if ( item instanceof ScrollOfEnchantment ) return true;
			if ( item instanceof ScrollOfMysticalEnergy ) return true;
			if ( item instanceof ScrollOfDivination ) return true;
			if ( item instanceof ScrollOfForesight ) return true;
			if ( item instanceof Stylus ) return true;
			return item instanceof InventoryStone;
		}
		return false;
	}

	public static Icons icon() {
		return icon( SPDSettings.modifiers() );
	}

	public static String saveString( boolean[] challenges ) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < challenges.length; i++) {
			s.append( challenges[i] ? 1 : 0 );
		}
		return s.toString();
	}

	public static String displayString( boolean[] challenges ) {
		String str = new StringBuilder( saveString( challenges ) ).reverse().toString();
		return new BigInteger( str, 2 ).toString( 36 );
	}

	public static boolean[] fromString( String challenges ) {
		boolean[] ret = new boolean[Challenges.values().length];
		int l = Math.min( Challenges.values().length, challenges.length() );
		for (int i = 0; i < l; i++) {
			int tier = Integer.parseInt( Character.toString( challenges.charAt( i ) ) );
			ret[i] = tier > 0;
			if ( tier > 1 ) enableFromLegacyTier( ret, fromId( i ), tier );
		}
		return ret;
	}

	public static boolean[] fromLegacy( int... levels ) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < levels.length; i++) {
			String str = Integer.toString( levels[i], 2 );
			int l = str.length();
			int t = 0;
			for (int j = 0; j < l; j++) {
				t += str.charAt( l - j - 1 ) == '1' ? 1 : 0;
			}
			s.append( t );
		}
		return fromString( s.toString() );
	}

	public static Icons icon( Modifiers modifiers ) {
		int l = 0;
		for (int i = 0; i < modifiers.challenges.length; i++) {
			if ( modifiers.challenges[i] ) {
				int t = fromId( i ).tier;
				if ( t < 5 )
					l = Math.max( l, t );
			}
		}
		if ( l <= 0 ) {
			return Icons.CHALLENGE_OFF;
		} else if ( l == 1 ) {
			return Icons.CHALLENGE_ON;
		} else if ( l == 2 ) {
			return Icons.CHALLENGE_HELL;
		} else if ( l == 3 ) {
			return Icons.CHALLENGE_HELL2;
		} else {
			return Icons.CHALLENGE_HELL3;
		}
	}

	public static boolean isActionBanned( Item item, String action ) {
		if ( item.cursed ) {
			if ( Challenges.CURSE_MAGNET.enabled() ) {
				return action.equals( Item.AC_DROP ) || action.equals( Item.AC_THROW );
			}
		}
		return false;
	}

	public static double secondsPerTurn() {
		if ( MARATHON.enabled() ) {
			if ( ON_A_BEAT.enabled() ) return 2;
			if ( HUMPPA.enabled() ) return 12;
			return 8;
		}
		return 1e64;
	}

	public static boolean isTooManyMobs() {
		return nMobsMultiplier() > 16;
	}

	public static int distributeDamage( Mob target, HashSet<Char> mobs, int amount ) {

		if ( mobs.size() == 0 ) return amount;

		HashSet<Char> cloneSet = new HashSet<>( mobs );
		for (Char mob : mobs) {
			if ( mob instanceof NPC || mob.properties().contains( Char.Property.BOSS ) ) {
				cloneSet.remove( mob );
			}
		}
		if ( Dungeon.hero.isAlive() ) {
			cloneSet.add( Dungeon.hero );
		}
		mobs = cloneSet;

		int targetDmg = amount;
		amount = (int) Math.ceil( amount / 2f );
		if ( !target.properties().contains( Char.Property.BOSS ) ) {
			targetDmg = amount;
		}
		int damage = amount / mobs.size();
		int leftovers = amount % mobs.size();

		ArrayList<Char> targets = new ArrayList<>( mobs );
		if ( leftovers > 0 ) {
			Random.shuffle( targets );
		}
		for (Char targ : targets) {
			int dmg = damage;
			if ( leftovers-- > 0 ) {
				dmg++;
			}
			if ( dmg > 0 ) {
				targ.damage( dmg, SharedPain.INSTANCE );
			}
		}
		return targetDmg;
	}

	public static Requirement req( Challenges chal ) {
		return new Requirement.Enabled( chal.id );
	}

	public static Requirement[] reqs( Challenges... chals ) {
		Requirement[] reqs = new Requirement[chals.length];
		for (int i = 0; i < chals.length; i++) {
			reqs[i] = req( chals[i] );
		}
		return reqs;
	}

	public static Requirement req( int id ) {
		return new Requirement.Enabled( id );
	}

	public static Requirement[] reqs( int... ids ) {
		Requirement[] reqs = new Requirement[ids.length];
		for (int i = 0; i < ids.length; i++) {
			reqs[i] = req( ids[i] );
		}
		return reqs;
	}

	public String description() {
		return Messages.get( Challenges.class, name + "_desc" );
	}


	public boolean deprecated() {
		return false;
	}

	protected float _nMobsMult() {
		return 1;
	}

	protected float _nLootMult() {
		return 1;
	}

	protected float _nRoomsMult() {
		return 1;
	}

	protected float _roomSizeMult() {
		return 1;
	}

	protected float _nTrapsMult() {
		return 1;
	}

	protected float _rareLootChanceMultiplier() {
		return 1;
	}

	protected void _initGenerator( boolean active ) {
	}

	public boolean isModifier() {
		return tier == 5;
	}

	public boolean isCheese() {
		return isModifier();
	}

	public boolean enabled() {
		return Dungeon.isChallenged( this.id );
	}

	public boolean requires( Challenges other ) {
		for (int requirement : this.requirements) {
			if ( requirement == other.id ) return true;
			if ( fromId( requirement ).requires( other ) ) return true;
		}
		return false;
	}

	public String localizedName() {
		return Messages.get( Challenges.class, name );
	}

	protected boolean _isItemBlocked( Item item ) {
		return false;
	}

	@Override
	public void onDeath() {
		Dungeon.fail( getClass() );
		GLog.n( Messages.get( this, "ondeath" ) );
	}

	public static class DuplicateChallengeException extends Error {
		public DuplicateChallengeException( Challenges a, Challenges b ) {
			super( String.format( "Challenges %s and %s have conflicting ids.", a.name, b.name ) );
		}
	}

	public static class MissingChallengeException extends Error {
		public MissingChallengeException( int id ) {
			super( "Challenge id " + id + " is empty." );
		}
	}

	public static class ChallengeOutOfBoundsException extends Error {
		public ChallengeOutOfBoundsException( Throwable cause ) {
			super( "One of challenges is out of bounds.", cause );
		}
	}

	private static class Requirement {
		public int id;

		public Requirement( int id ) {
			this.id = id;
		}

		public static class Enabled extends Requirement {

			public Enabled( int id ) {
				super( id );
			}
		}
	}
}