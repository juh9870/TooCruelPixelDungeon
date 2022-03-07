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

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.Difficulty;
import com.shatteredpixel.shatteredpixeldungeon.utils.NamesGenerator;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public enum Rankings {

	INSTANCE;

	public static final int TABLE_SIZE	= 11;

	public static final String RANKINGS_FILE = "rankings.dat";

	public ArrayList<Record> records;
    public HashMap<String, Dynasty> dynasties;
	public int lastRecord;
	public int totalNumber;
	public int wonNumber;

	public void submit( boolean win, Class cause ) {

		load();

		Record rec = new Record();

		rec.cause = cause;
		rec.win		= win;
		rec.heroClass	= Dungeon.hero.heroClass;
		rec.armorTier	= Dungeon.hero.tier();
		rec.herolevel	= Dungeon.hero.lvl;
		rec.depth		= Dungeon.displayDepth();
		rec.score		= score( win );
		rec.version 	= Dungeon.versions.toArray(new String[0]);

		INSTANCE.saveGameData(rec);

		rec.gameID = UUID.randomUUID().toString();

		records.add( rec );

		Collections.sort( records, scoreComparator );

		lastRecord = records.indexOf( rec );
        Dynasty dyn = Dungeon.modifiers.getDynasty();

        if (dyn != null) {
            dyn.records.add(rec);
            if (!win) dyn.finished = true;
            else rec.score *= dyn.scoreMultiplier();
        }
		int size = records.size();
		while (size > TABLE_SIZE) {

			if (lastRecord == size - 1) {
				records.remove( size - 2 );
				lastRecord--;
			} else {
				records.remove( size - 1 );
			}

			size = records.size();
		}

		totalNumber++;
		if (win) {
			wonNumber++;
		}

		Badges.validateGamesPlayed();

		save();
	}

    public Dynasty getDynasty(String id) {
        if (id.length() == 0) return null;
        if (dynasties == null) load();
        return dynasties.get(id);
    }
	private int score( boolean win ) {
        return (int) ((Statistics.goldCollected + Dungeon.hero.lvl * (win ? 26 : Dungeon.scalingFactor()) * 100) * (win ? 2 : 1)
                * Difficulty.calculateDifficulty(Dungeon.modifiers));
	}

	public static final String HERO = "hero";
	public static final String STATS = "stats";
	public static final String BADGES = "badges";
	public static final String HANDLERS = "handlers";
	public static final String CHALLENGES = "challenges";
	public static final String MODIFIERS = "modifiers";
	private static final String DYNASTIES = "dynasties";

	public void saveGameData(Record rec){
		rec.gameData = new Bundle();

		Belongings belongings = Dungeon.hero.belongings;

		//save the hero and belongings
		ArrayList<Item> allItems = (ArrayList<Item>) belongings.backpack.items.clone();
		//remove items that won't show up in the rankings screen
		for (Item item : belongings.backpack.items.toArray( new Item[0])) {
			if (item instanceof Bag){
				for (Item bagItem : ((Bag) item).items.toArray( new Item[0])){
					if (Dungeon.quickslot.contains(bagItem)) belongings.backpack.items.add(bagItem);
				}
			}
			if (!Dungeon.quickslot.contains(item)) {
				belongings.backpack.items.remove(item);
			}
		}

		//remove all buffs (ones tied to equipment will be re-applied)
		for(Buff b : Dungeon.hero.buffs()){
			Dungeon.hero.remove(b);
		}

		rec.gameData.put( HERO, Dungeon.hero );

		//save stats
		Bundle stats = new Bundle();
		Statistics.storeInBundle(stats);
		rec.gameData.put( STATS, stats);

		//save badges
		Bundle badges = new Bundle();
		Badges.saveLocal(badges);
		rec.gameData.put( BADGES, badges);

		//save handler information
		Bundle handler = new Bundle();
		Scroll.saveSelectively(handler, belongings.backpack.items);
		Potion.saveSelectively(handler, belongings.backpack.items);
		//include potentially worn rings
		if (belongings.misc != null)        belongings.backpack.items.add(belongings.misc);
		if (belongings.ring != null)        belongings.backpack.items.add(belongings.ring);
		Ring.saveSelectively(handler, belongings.backpack.items);
		rec.gameData.put( HANDLERS, handler);

		//restore items now that we're done saving
		belongings.backpack.items = allItems;

		//save challenges
        rec.gameData.put(MODIFIERS, Dungeon.modifiers);
	}

	public void loadGameData(Record rec){
		Bundle data = rec.gameData;

		Actor.clear();
		Dungeon.hero = null;
		Dungeon.level = null;
		Generator.fullReset();
		Notes.reset();
		Dungeon.quickslot.reset();
		QuickSlotButton.reset();

		Bundle handler = data.getBundle(HANDLERS);
		Scroll.restore(handler);
		Potion.restore(handler);
		Ring.restore(handler);

		Badges.loadLocal(data.getBundle(BADGES));

		Dungeon.hero = (Hero)data.get(HERO);

		Statistics.restoreFromBundle(data.getBundle(STATS));

        if (data.contains(MODIFIERS)) {
            Dungeon.modifiers = (Modifiers) data.get(MODIFIERS);
        } else {
            Dungeon.modifiers = new Modifiers(Challenges.fromLegacy(data.getInt(CHALLENGES)));
        }

		Dungeon.versions = new ArrayList<>(Arrays.asList(rec.version));

	}

	private static final String RECORDS	= "records";
	private static final String LATEST	= "latest";
	private static final String TOTAL	= "total";
	private static final String WON     = "won";

	public void save() {
		Bundle bundle = new Bundle();
		bundle.put( RECORDS, records );
        bundle.put(DYNASTIES, dynasties.values());
		bundle.put( LATEST, lastRecord );
		bundle.put( TOTAL, totalNumber );
		bundle.put( WON, wonNumber );

		try {
			FileUtils.bundleToFile( RANKINGS_FILE, bundle);
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
		}

	}

	public void load() {

		if (records != null) {
			return;
		}

		records = new ArrayList<>();
        dynasties = new HashMap<>();

		try {
			Bundle bundle = FileUtils.bundleFromFile( RANKINGS_FILE );

			for (Bundlable record : bundle.getCollection( RECORDS )) {
				records.add( (Record)record );
			}
            if (bundle.contains(DYNASTIES)) {
                for (Bundlable dynasty : bundle.getCollection(DYNASTIES)) {
                    Dynasty dyn = (Dynasty) dynasty;
                    dynasties.put(dyn.id, dyn);
                }
            }
			lastRecord = bundle.getInt( LATEST );

			totalNumber = bundle.getInt( TOTAL );
			if (totalNumber == 0) {
				totalNumber = records.size();
			}

			wonNumber = bundle.getInt( WON );
			if (wonNumber == 0) {
				for (Record rec : records) {
					if (rec.win) {
						wonNumber++;
					}
				}
			}

            updateDynasties();
		} catch (IOException e) {
		}
	}

    public void updateDynasties() {
        HashSet<String> active = new HashSet<>();
        boolean needSave = false;
        for (GamesInProgress.Info info : GamesInProgress.checkAll()) {
            active.add(info.modifiers.dynastyId);
        }

        for (Dynasty dynasty : dynasties.values()) {
            if (dynasty.finished) continue;
            if (!active.contains(dynasty.id)) {
                dynasty.finished = true;
                needSave = true;
            }
        }

        for (Dynasty dynasty : new HashSet<>(dynasties.values())) {
            if (!dynasty.finished) continue;
            if (dynasty.length() < 2) {
                dynasties.remove(dynasty.id);
                needSave = true;
            }
        }
        if (needSave) save();
    }
	public static class Record implements Bundlable {

		private static final String CAUSE   = "cause";
		private static final String WIN		= "win";
		private static final String SCORE	= "score";
		private static final String CLASS	= "class";
		private static final String TIER	= "tier";
		private static final String LEVEL	= "level";
		private static final String DEPTH_OLD = "depth";
		private static final String DEPTH	= "sdepth";
		private static final String DATA	= "gameData";
		private static final String ID      = "gameID";
		private static final String VERSION = "version";

		public Class cause;
		public boolean win;

		public HeroClass heroClass;
		public int armorTier;
		public int herolevel;
		public String depth;
		public String[] version;

		public Bundle gameData;
		public String gameID;

		public int score;

		public String desc(){
			if (cause == null) {
				return Messages.get(this, "something");
			} else {
				String result = Messages.get(cause, "rankings_desc", (Messages.get(cause, "name")));
				if (result.contains("!!!NO TEXT FOUND!!!")){
					return Messages.get(this, "something");
				} else {
					return result;
				}
			}
		}

        public Modifiers modifiers() {
            return (Modifiers) gameData.get(MODIFIERS);
        }
		@Override
		public void restoreFromBundle( Bundle bundle ) {

			if (bundle.contains( CAUSE )) {
				cause   = bundle.getClass( CAUSE );
			} else {
				cause = null;
			}

			win		= bundle.getBoolean( WIN );
			score	= bundle.getInt( SCORE );

			heroClass	= bundle.getEnum( CLASS, HeroClass.class );
			armorTier	= bundle.getInt( TIER );

			if (bundle.contains(DATA))  gameData = bundle.getBundle(DATA);
			if (bundle.contains(ID))   gameID = bundle.getString(ID);

			if (gameID == null) gameID = UUID.randomUUID().toString();

			if(bundle.contains(DEPTH_OLD)){
				depth = Integer.toString(bundle.getInt(DEPTH_OLD));
			} else {
				depth = bundle.getString(DEPTH);
			}
			herolevel = bundle.getInt( LEVEL );

			if (bundle.contains( VERSION ))
				version = bundle.getStringArray( VERSION );
			else version = new String[]{"???"};
		}

		@Override
		public void storeInBundle( Bundle bundle ) {

			if (cause != null) bundle.put( CAUSE, cause );

			bundle.put( WIN, win );
			bundle.put( SCORE, score );

			bundle.put( CLASS, heroClass );
			bundle.put( TIER, armorTier );
			bundle.put( LEVEL, herolevel );
			bundle.put( DEPTH, depth );

			if (gameData != null) bundle.put( DATA, gameData );
			bundle.put( ID, gameID );
			bundle.put( VERSION, version );
		}
	}
	public static class Dynasty implements Bundlable {

		private static final String RECORDS = "records";
		private static final String FINISHED = "finished";
		private static final String EPIC = "epic";
		private static final String SURFACE = "surface";
		private static final String NAME = "name";
		private static final String ID = "id";

		public ArrayList<Record> records = new ArrayList<>();
		public boolean epic;
		public boolean surface;
		public boolean finished;
		public String name;

		public String id;

		public Dynasty() {
			id = UUID.randomUUID().toString();
		}

		public int score() {
			int score = 0;
			for (Record record : records) {
				score += record.score;
			}
			return score;
		}

        public float scoreMultiplier() {
            if (epic) {
                return (float) Math.pow(1.25f, (length()-1));
            } else {
                return 1 + (0.1f * (length()-1));
            }
        }

		public float maxDifficulty() {
			float max = 0;
			for (Record record : records) {
				max = Math.max(max, Difficulty.calculateDifficulty((Modifiers) record.gameData.get(MODIFIERS)));
			}
			return max;
		}

		public int length() {
			int l = records.size();
			if (!records.get(l - 1).win) {
				l--;
			}
			return l;
		}

		public HeroClass mostUsedClass() {
			HashMap<HeroClass, Integer> amounts = new HashMap<>();


			for (Record record : records) {
				amounts.put(record.heroClass, amounts.containsKey(record.heroClass) ? amounts.get(record.heroClass) + 1 : 1);
			}

			int max = -1;
			HeroClass maxClass = records.get(0).heroClass;
			for (HeroClass heroClass : amounts.keySet()) {
				if (amounts.get(heroClass) > max) {
					max = amounts.get(heroClass);
					maxClass = heroClass;
				}
			}
			return maxClass;
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			records = new ArrayList<>();
			for (Bundlable record : bundle.getCollection(RECORDS)) {
				records.add((Record) record);
			}
			finished = bundle.getBoolean(FINISHED);
			surface = bundle.getBoolean(SURFACE);
			epic = bundle.getBoolean(EPIC);
			name = bundle.getString(NAME);
			id = bundle.getString(ID);

			if (name == null || name.length() == 0) name = NamesGenerator.dynastyName(epic);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			bundle.put(RECORDS, records);
			bundle.put(FINISHED, finished);
			bundle.put(SURFACE, surface);
			bundle.put(EPIC, epic);
			bundle.put(NAME, name);
			bundle.put(ID, id);
		}
	}
	private static final Comparator<Record> scoreComparator = new Comparator<Rankings.Record>() {
		@Override
		public int compare( Record lhs, Record rhs ) {
			int result = 0;
			if(lhs.score > 0 && rhs.score > 0) result = (int)Math.signum( rhs.score - lhs.score );
			else if (lhs.score < 0 && rhs.score > 0) result = 1;
			else if (rhs.score < 0 && lhs.score > 0) result = -1;
			if (result == 0) {
				return (int)Math.signum( rhs.gameID.hashCode() - lhs.gameID.hashCode());
			} else{
				return result;
			}
		}
	};
	public static final Comparator<Dynasty> dynastyComparator = new Comparator<Rankings.Dynasty>() {
		@Override
		public int compare(Dynasty lhs, Dynasty rhs) {
			int result = (int) Math.signum(rhs.records.size() - lhs.records.size());
			if (result == 0) {

				int ls = lhs.score();
				int rs = rhs.score();
				if(ls > 0 && rs > 0) result = (int) Math.signum( rs - ls );
				else if (ls < 0 && rs > 0) result = 1;
				else if (rs < 0 && ls > 0) result = -1;
				if (result == 0) {
					result = (int) Math.signum(rhs.records.get(0).gameID.hashCode() - lhs.records.get(0).hashCode());
				}
			}
			return result;
		}
	};
}
