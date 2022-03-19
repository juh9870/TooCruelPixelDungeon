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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Revealing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks.DefaultLevelPack;
import com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks.LevelPack;
import com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks.Marker;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.ChallengesData;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndResurrect;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Dungeon {

	//enum of items which have limited spawns, records how many have spawned
	//could all be their own separate numbers, but this allows iterating, much nicer for bundling/initializing.
	public static enum LimitedDrops {
		//limited world drops
		STRENGTH_POTIONS,
		UPGRADE_SCROLLS,
		ARCANE_STYLI,

		//Health potion sources
		//enemies
		SWARM_HP,
		NECRO_HP,
		BAT_HP,
		WARLOCK_HP,
		//Demon spawners are already limited in their spawnrate, no need to limit their health drops
		//alchemy
		COOKING_HP,
		BLANDFRUIT_SEED,

		//Other limited enemy drops
		SLIME_WEP,
		SKELE_WEP,
		THEIF_MISC,
		GUARD_ARM,
		SHAMAN_WAND,
		DM200_EQUIP,
		GOLEM_EQUIP,

		//containers
		VELVET_POUCH(true),
		SCROLL_HOLDER(true),
		POTION_BANDOLIER(true),
		MAGICAL_HOLSTER(true);

		private int count = 0;

		LimitedDrops() {
			this(false);
		}
		LimitedDrops(boolean unique) {
			this.unique = unique;
		}

		private boolean unique;

		//for items which can only be dropped once, should directly access count otherwise.
		public boolean dropped(){
			return getCount() != 0;
		}
		public void drop(){
			setCount(1);
		}

		public static void reset(){
			for (LimitedDrops lim : values()){
				lim.setCount(0);
			}
		}

		public static void store( Bundle bundle ){
			for (LimitedDrops lim : values()){
				bundle.put(lim.name(), lim.getCount());
			}
		}

		public static void restore( Bundle bundle ){
			for (LimitedDrops lim : values()){
				if (bundle.contains(lim.name())){
					lim.setCount(bundle.getInt(lim.name()));
				} else {
					lim.setCount(0);
				}

			}
		}

		public int getCount() {
			return count;
		}

		public LimitedDrops setCount(int count) {
			this.count = count;
			return this;
		}
	}

    public static Modifiers modifiers = new Modifiers();
    public static ChallengesData extraData = new ChallengesData();
    public static boolean challengesInform;

	public static int mobsToChampion;

	public static Hero hero;
	public static Level level;

	public static QuickSlot quickslot = new QuickSlot();

	public static LevelPack levelPack;

	public static int gold;
	public static int tokens;
	public static int energy;

	public static HashSet<Integer> chapters;

	public static Map<Marker,ArrayList<Item>> droppedItems;
	public static Map<Marker,ArrayList<Item>> portedItems;

	public static ArrayList<String> versions;
	public static int version;

	public static long seed;

	public static void init() {

		version = Game.versionCode;
		versions = new ArrayList<>();
		versions.add("v" + Game.version);
		mobsToChampion = -1;

        seed = DungeonSeed.randomSeed();

		modifiers = SPDSettings.modifiers();
		extraData.init();
		challengesInform = false;
		if (!modifiers.isChallenged()) {
			modifiers.randomize(seed);
			challengesInform = true;
		}
		SPDSettings.modifiers(new Modifiers(modifiers).setDynasty(""));

//		if (DeviceCompat.isDebug()) {
//			seed = Long.parseLong("2lq439du", 36);
//		}

//        if(DeviceCompat.isDebug()){
//        	modifiers.fromBigIntString("axq5j8k4jb9hsf0g");
//		}
		
		Generator.Category.hardReset();

		levelPack = new DefaultLevelPack();
		levelPack.init();

		Actor.clear();
		Actor.resetNextID();

		Random.pushGenerator( seed );

			Scroll.initLabels();
			Potion.initColors();
			Ring.initGems();

			SpecialRoom.initForRun();
			SecretRoom.initForRun();
			Challenges.initGenerator();

		Random.resetGenerators();

		Statistics.reset();
		Notes.reset();

		quickslot.reset();
		QuickSlotButton.reset();


		gold = 0;
		tokens = 0;
		energy = 0;

		droppedItems = new HashMap<>();
		portedItems = new HashMap<>();

		LimitedDrops.reset();

		chapters = new HashSet<>();

		Ghost.Quest.reset();
		Wandmaker.Quest.reset();
		Blacksmith.Quest.reset();
		Imp.Quest.reset();

		Generator.fullReset();
		hero = new Hero();
		hero.live();

		Badges.reset();

		GamesInProgress.selectedClass.initHero( hero );
	}

    public static boolean isChallenged(int id) {
        return modifiers.isChallenged(id);
	}

	public static Level newLevel() {

		Dungeon.level = null;
		Actor.clear();

		Level level = levelPack.generateNextLevel();
		level.create();

		Statistics.qualifiedForNoKilling = !bossLevel();

		return level;
	}

	public static void resetLevel() {

		Actor.clear();

		level.reset();
		switchLevel( level, level.entrance );
	}

	public static long seedCurDepth(){
		return levelPack.curLevelSeed();
	}

	public static boolean shopOnLevel() {
		return levelPack.curLvl.shop();
	}

	public static boolean bossLevel() {
		return levelPack.curLvl.boss();
	}
	public static boolean bossNextLevel() {
		return levelPack.bossNextLevel();
	}

	public static boolean bossLevel( Marker depth ) {
		return depth.boss();
	}

	public static int scalingFactor(){
		return levelPack.scalingFactor();
	}
	public static int scalingChapter(){
		return levelPack.curLvl.scalingChapter();
	}
	public static int legacyDepth(){
		return levelPack.curLvl.legacyLevelgenMapping();
	}

	public static String displayDepth(){
		return levelPack.displayDepth();
	}

	public static Marker depth(){
		return levelPack.curLvl;
	}

	public static void switchLevel( final Level level, int pos ) {

		if (pos == -2){
			pos = level.exit;
		} else if (pos < 0 || pos >= level.length() || (!level.passable[pos] && !level.avoid[pos])){
			pos = level.entrance;
		}

		PathFinder.setMapSize(level.width(), level.height());

		Dungeon.level = level;
		Mob.restoreAllies( level, pos );
		Actor.init();

		level.addRespawner();

		hero.pos(pos);

		for(Mob m : level.mobs().toArray(new Mob[0])){
			if (m.pos() == hero.pos()){
				//displace mob
				for(int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(m.pos() +i) == null && level.passable[m.pos() + i]){
						m.pos(m.pos() + i);
						break;
					}
				}

				if (m.pos() == hero.pos()){
					m.destroy();
				}
			}
		}

		Light light = hero.buff( Light.class );
		hero.viewDistance = light == null ? level.viewDistance : Math.max( Light.DISTANCE, level.viewDistance );

		hero.curAction = hero.lastAction = null;

		observe();
		try {
			saveAll();
		} catch (IOException e) {
			ShatteredPixelDungeon.reportException(e);
			/*This only catches IO errors. Yes, this means things can go wrong, and they can go wrong catastrophically.
			But when they do the user will get a nice 'report this issue' dialogue, and I can fix the bug.*/
		}
	}

	public static void dropToChasm( Item item ) {
		Marker depth = levelPack.curPitFallTarget();
		ArrayList<Item> dropped = Dungeon.droppedItems.get( depth );
		if (dropped == null) {
			Dungeon.droppedItems.put( depth, dropped = new ArrayList<>() );
		}
		dropped.add( item );
	}

	public static boolean posNeeded() {
		return levelPack.posNeeded();
	}

	public static boolean souNeeded() {
		return levelPack.souNeeded();
	}

	public static boolean asNeeded() {
		return levelPack.asNeeded();
	}

	private static final String START_VERSION= "start_version";
	private static final String VERSION		= "version";
	private static final String SEED		= "seed";
    private static final String MODIFIERS   = "modifiers";
    private static final String EXTRA_DATA  = "extraData";
	private static final String CHALLENGES	= "challenges";
    private static final String HELL_CHALS  = "hell_challenges";
	private static final String MOBS_TO_CHAMPION	= "mobs_to_champion";
	private static final String HERO		= "hero";
	private static final String DEPTH		= "depth";
	private static final String LEVEL_PACK	= "levelpack";
	private static final String GOLD		= "gold";
	private static final String BJTOKENS	= "bj_tokens";
	private static final String ENERGY		= "energy";
	private static final String DROPPED     = "dropped%s";
	private static final String PORTED      = "ported%s";
	private static final String LEVEL		= "level";
	private static final String LIMDROPS    = "limited_drops";
	private static final String CHAPTERS	= "chapters";
	private static final String QUESTS		= "quests";
	private static final String BADGES		= "badges";

	public static void saveGame( int save ) {
		try {
			Bundle bundle = new Bundle();

			version = Game.versionCode;
			bundle.put( VERSION, version );
			bundle.put( START_VERSION, versions.toArray(new String[0]));
			bundle.put( SEED, seed );
            bundle.put(MODIFIERS, modifiers);
			bundle.put( MOBS_TO_CHAMPION, mobsToChampion );
			bundle.put( HERO, hero );
			bundle.put( LEVEL_PACK, levelPack );

			bundle.put( GOLD, gold );
			bundle.put( BJTOKENS, tokens );
			bundle.put( ENERGY, energy );

			for (Marker d : droppedItems.keySet()) {
				bundle.put(Messages.format(DROPPED, levelPack.levelFileName(d)), droppedItems.get(d));
			}

			for (Marker p : portedItems.keySet()){
				bundle.put(Messages.format(PORTED, levelPack.levelFileName(p)), portedItems.get(p));
			}

			quickslot.storePlaceholders( bundle );

			Bundle limDrops = new Bundle();
			LimitedDrops.store( limDrops );
			bundle.put ( LIMDROPS, limDrops );

			int count = 0;
			int ids[] = new int[chapters.size()];
			for (Integer id : chapters) {
				ids[count++] = id;
			}
			bundle.put( CHAPTERS, ids );

			Bundle quests = new Bundle();
			Ghost		.Quest.storeInBundle( quests );
			Wandmaker	.Quest.storeInBundle( quests );
			Blacksmith	.Quest.storeInBundle( quests );
			Imp			.Quest.storeInBundle( quests );
			bundle.put( QUESTS, quests );

			SpecialRoom.storeRoomsInBundle( bundle );
			SecretRoom.storeRoomsInBundle( bundle );

			Statistics.storeInBundle( bundle );
			Notes.storeInBundle( bundle );
			Generator.storeInBundle( bundle );

			Scroll.save( bundle );
			Potion.save( bundle );
			Ring.save( bundle );

			Actor.storeNextID( bundle );

			Bundle badges = new Bundle();
			Badges.saveLocal( badges );
			bundle.put( BADGES, badges );

			FileUtils.bundleToFile( GamesInProgress.gameFile(save), bundle);

		} catch (IOException e) {
			GamesInProgress.setUnknown( save );
			ShatteredPixelDungeon.reportException(e);
		}
	}

	public static void saveLevel( int save ) throws IOException {
		Bundle bundle = new Bundle();
		bundle.put( LEVEL, level );

		FileUtils.bundleToFile(GamesInProgress.depthFile( save, levelPack.curLevelFileName()), bundle);
	}

	public static void saveAll() throws IOException {
		if (hero != null && (hero.isAlive() || WndResurrect.instance != null)) {

			Actor.fixTime();
			saveGame( GamesInProgress.curSlot );
			saveLevel( GamesInProgress.curSlot );

            GamesInProgress.set(GamesInProgress.curSlot, levelPack.curLvl, modifiers, hero);

		}
	}

	public static void loadGame( int save ) throws IOException {
		loadGame( save, true );
	}

	public static void loadGame( int save, boolean fullLoad ) throws IOException {

		Bundle bundle = FileUtils.bundleFromFile( GamesInProgress.gameFile( save ) );

		version = bundle.getInt( VERSION );
		if(bundle.contains(START_VERSION)) {
			versions = new ArrayList<>(Arrays.asList(bundle.getStringArray( START_VERSION )));
		}
		else {
			versions = new ArrayList<>();
			versions.add("???");
			versions.add("b" + version);
		}

		seed = bundle.contains( SEED ) ? bundle.getLong( SEED ) : DungeonSeed.randomSeed();

		Actor.clear();
		Actor.restoreNextID( bundle );

		quickslot.reset();
		QuickSlotButton.reset();

        if (bundle.contains(MODIFIERS)) {
            Dungeon.modifiers = (Modifiers) bundle.get(MODIFIERS);
        } else {

            int challenges = bundle.getInt(CHALLENGES);
            int[] hellChallenges = bundle.getIntArray(HELL_CHALS);
            if (hellChallenges == null)
                hellChallenges = new int[]{bundle.getInt(HELL_CHALS), 0};

            Dungeon.modifiers = new Modifiers(Challenges.fromLegacy(challenges, hellChallenges[0], hellChallenges[1]));
        }

        Challenges.initGenerator();

		Dungeon.mobsToChampion = bundle.getInt( MOBS_TO_CHAMPION );

		Dungeon.level = null;
//		Dungeon.depth = -1;
		Dungeon.levelPack = null;

				Scroll.restore( bundle );
		Potion.restore( bundle );
		Ring.restore( bundle );

		quickslot.restorePlaceholders( bundle );

		if (fullLoad) {

			String curVersion = "v"+Game.version;
			if(!versions.get(versions.size()-1).equals(curVersion)){
				versions.add(curVersion);
			}

			LimitedDrops.restore( bundle.getBundle(LIMDROPS) );

			chapters = new HashSet<>();
			int ids[] = bundle.getIntArray( CHAPTERS );
			if (ids != null) {
				for (int id : ids) {
					chapters.add( id );
				}
			}

			Bundle quests = bundle.getBundle( QUESTS );
			if (!quests.isNull()) {
				Ghost.Quest.restoreFromBundle( quests );
				Wandmaker.Quest.restoreFromBundle( quests );
				Blacksmith.Quest.restoreFromBundle( quests );
				Imp.Quest.restoreFromBundle( quests );
			} else {
				Ghost.Quest.reset();
				Wandmaker.Quest.reset();
				Blacksmith.Quest.reset();
				Imp.Quest.reset();
			}

			SpecialRoom.restoreRoomsFromBundle(bundle);
			SecretRoom.restoreRoomsFromBundle(bundle);
		}

		Bundle badges = bundle.getBundle(BADGES);
		if (!badges.isNull()) {
			Badges.loadLocal( badges );
		} else {
			Badges.reset();
		}

		Notes.restoreFromBundle( bundle );

		hero = null;
		hero = (Hero)bundle.get( HERO );

		if (bundle.contains(DEPTH)) {
			int depth = bundle.getInt( DEPTH );
			Dungeon.levelPack = DefaultLevelPack.fromLegacyData(depth);
		} else {
			levelPack = (LevelPack) bundle.get( LEVEL_PACK );
		}

		gold = bundle.getInt( GOLD );
		tokens = bundle.getInt( BJTOKENS );
		energy = bundle.getInt( ENERGY );

		if (bundle.contains(EXTRA_DATA)) {
			extraData = (ChallengesData) bundle.get(EXTRA_DATA);
		} else {
			extraData.init();
		}

		Statistics.restoreFromBundle( bundle );
		Generator.restoreFromBundle( bundle );

		droppedItems = new HashMap<>();
		portedItems = new HashMap<>();
		for (Object o : levelPack.levels()) {
			Marker m = (Marker) o;

			//dropped items
			ArrayList<Item> items = new ArrayList<>();
			String key = Messages.format( DROPPED, levelPack.levelFileName(m) );
			if (bundle.contains(key))
				for (Bundlable b : bundle.getCollection( key ) ) {
					items.add( (Item)b );
				}
			if (!items.isEmpty()) {
				droppedItems.put( m, items );
			}

			//ported items
			items = new ArrayList<>();
			key = Messages.format( PORTED, levelPack.levelFileName(m) );
			if (bundle.contains(key))
				for (Bundlable b : bundle.getCollection( key )) {
					items.add( (Item)b );
				}
			if (!items.isEmpty()) {
				portedItems.put( m, items );
			}
		}
	}

	public static Level loadLevel( int save ) throws IOException {

		Dungeon.level = null;
		Actor.clear();

		Bundle bundle = FileUtils.bundleFromFile( GamesInProgress.depthFile( save, levelPack.curLevelFileName())) ;

		Level level = (Level)bundle.get( LEVEL );

		if (level == null){
			throw new IOException();
		} else {
			return level;
		}
	}

	public static void deleteGame( int save, boolean deleteLevels ) {

		FileUtils.deleteFile(GamesInProgress.gameFile(save));

		if (deleteLevels) {
			FileUtils.deleteDir(GamesInProgress.gameFolder(save));
		}

		GamesInProgress.delete( save );
	}

	public static void preview( GamesInProgress.Info info, Bundle bundle ) {
		if(bundle.contains( DEPTH )){
			info.depth = DefaultLevelPack.markerFromLegacyDepth(bundle.getInt( DEPTH ));
		} else {
			LevelPack pack = (LevelPack) bundle.get(LEVEL_PACK);
			info.depth = pack.curLvl;
		}
		info.version = bundle.getInt( VERSION );
        if (bundle.contains(MODIFIERS)) {
            info.modifiers = (Modifiers) bundle.get(MODIFIERS);
        } else {
            int challenges = bundle.getInt(CHALLENGES);
            int[] hellChallenges = bundle.getIntArray(HELL_CHALS);
            if (hellChallenges == null)
                hellChallenges = new int[]{bundle.getInt(HELL_CHALS), 0};

            info.modifiers = new Modifiers(Challenges.fromLegacy(challenges, hellChallenges[0], hellChallenges[1]));
        }
		Hero.preview( info, bundle.getBundle( HERO ) );
		Statistics.preview( info, bundle );
	}

	public static void fail( Class cause ) {
		if (WndResurrect.instance == null) {
			Rankings.INSTANCE.submit( false, cause );
		}
	}

	public static void win( Class cause ) {

		hero.belongings.identify();

		Rankings.INSTANCE.submit( true, cause );
	}

	//default to recomputing based on max hero vision, in case vision just shrank/grew
	public static void observe(){
		int dist = Math.max(Dungeon.hero.viewDistance, 8);
		dist *= 1f + 0.25f*Dungeon.hero.pointsInTalent(Talent.FARSIGHT);

		if (Dungeon.hero.buff(MagicalSight.class) != null){
			dist = Math.max( dist, MagicalSight.DISTANCE );
		}

		observe( dist+1 );
	}

	public static void observe( int dist ) {

		if (level == null) {
			return;
		}

		if(Dungeon.hero.isAlive()) {
			level.updateFieldOfView(hero, level.heroFOV);
		} else {
			for (int i = 0; i < level.heroFOV.length; i++) {
				if (Dungeon.level.discoverable[i]) level.heroFOV[i] = true;
			}
		}

		int x = hero.pos() % level.width();
		int y = hero.pos() / level.width();

		//left, right, top, bottom
		int l = Math.max( 0, x - dist );
		int r = Math.min( x + dist, level.width() - 1 );
		int t = Math.max( 0, y - dist );
		int b = Math.min( y + dist, level.height() - 1 );

		int width = r - l + 1;
		int height = b - t + 1;

		int pos = l + t * level.width();

		for (int i = t; i <= b; i++) {
			BArray.or( level.visited, level.heroFOV, pos, width, level.visited );
			pos+=level.width();
		}
        if (Challenges.AMNESIA.enabled()) {

            if (level.needUpdateFog != null) {
                level.needUpdateFog = BArray.and(BArray.not(level.heroFOV, null), BArray.or(level.visited, level.needUpdateFog, null), null);
            } else
                level.needUpdateFog = BArray.and(level.visited, BArray.not(level.heroFOV, null), null);
            level.visited = level.heroFOV;
        }

		GameScene.updateFog(l, t, width, height);

        boolean mw = hero.buff(MindVision.class) != null;
        for (Mob m : level.mobs().toArray(new Mob[0])){
			if (mw || m.properties().contains(Char.Property.ALWAYS_VISIBLE) || m.buff(Revealing.class) != null) {
				BArray.or( level.visited, level.heroFOV, m.pos() - 1 - level.width(), 3, level.visited );
				BArray.or( level.visited, level.heroFOV, m.pos(), 3, level.visited );
				BArray.or( level.visited, level.heroFOV, m.pos() - 1 + level.width(), 3, level.visited );
				//updates adjacent cells too
				GameScene.updateFog(m.pos(), 2);
			}
		}

		if (hero.buff(Awareness.class) != null){
			for (Heap h : level.heaps.valueList()){
				BArray.or( level.visited, level.heroFOV, h.pos - 1 - level.width(), 3, level.visited );
				BArray.or( level.visited, level.heroFOV, h.pos - 1, 3, level.visited );
				BArray.or( level.visited, level.heroFOV, h.pos - 1 + level.width(), 3, level.visited );
				GameScene.updateFog(h.pos, 2);
			}
		}

		for (TalismanOfForesight.CharAwareness c : hero.buffs(TalismanOfForesight.CharAwareness.class)){
			Char ch = (Char) Actor.findById(c.charID);
			if (ch == null) continue;
			BArray.or( level.visited, level.heroFOV, ch.pos() - 1 - level.width(), 3, level.visited );
			BArray.or( level.visited, level.heroFOV, ch.pos() - 1, 3, level.visited );
			BArray.or( level.visited, level.heroFOV, ch.pos() - 1 + level.width(), 3, level.visited );
			GameScene.updateFog(ch.pos(), 2);
		}

		for (TalismanOfForesight.HeapAwareness h : hero.buffs(TalismanOfForesight.HeapAwareness.class)){
			if (!Dungeon.depth().equals(h.depth)) continue;
			BArray.or( level.visited, level.heroFOV, h.pos - 1 - level.width(), 3, level.visited );
			BArray.or( level.visited, level.heroFOV, h.pos - 1, 3, level.visited );
			BArray.or( level.visited, level.heroFOV, h.pos - 1 + level.width(), 3, level.visited );
			GameScene.updateFog(h.pos, 2);
		}

		for (RevealedArea a : hero.buffs(RevealedArea.class)){
			if (Dungeon.depth().equals(a.depth)) continue;
			BArray.or( level.visited, level.heroFOV, a.pos - 1 - level.width(), 3, level.visited );
			BArray.or( level.visited, level.heroFOV, a.pos - 1, 3, level.visited );
			BArray.or( level.visited, level.heroFOV, a.pos - 1 + level.width(), 3, level.visited );
			GameScene.updateFog(a.pos, 2);
		}

		for (Char ch : Actor.chars()){
			if (ch instanceof WandOfWarding.Ward
					|| ch instanceof WandOfRegrowth.Lotus
					|| ch instanceof SpiritHawk.HawkAlly){
				x = ch.pos() % level.width();
				y = ch.pos() / level.width();

				//left, right, top, bottom
				dist = ch.viewDistance+1;
				l = Math.max( 0, x - dist );
				r = Math.min( x + dist, level.width() - 1 );
				t = Math.max( 0, y - dist );
				b = Math.min( y + dist, level.height() - 1 );

				width = r - l + 1;
				height = b - t + 1;

				pos = l + t * level.width();

				for (int i = t; i <= b; i++) {
					BArray.or( level.visited, level.heroFOV, pos, width, level.visited );
					pos+=level.width();
				}
				GameScene.updateFog(ch.pos(), dist);
			}
		}

		GameScene.afterObserve();
	}

	//we store this to avoid having to re-allocate the array with each pathfind
	private static boolean[] passable;

	private static void setupPassable(){
		if (passable == null || passable.length != Dungeon.level.length())
			passable = new boolean[Dungeon.level.length()];
		else
			BArray.setFalse(passable);
	}

	public static PathFinder.Path findPath(Char ch, int to, boolean[] pass, boolean[] vis, boolean chars) {

		setupPassable();
		if (ch.flying || ch.buff( Amok.class ) != null) {
			BArray.or( pass, Dungeon.level.avoid, passable );
		} else {
			System.arraycopy( pass, 0, passable, 0, Dungeon.level.length() );
		}

		if (chars && Char.hasProp(ch, Char.Property.LARGE)){
			BArray.and( passable, Dungeon.level.openSpace, passable );
		}

		if (chars) {
			for (Char c : Actor.chars()) {
				if (vis[c.pos()]) {
					passable[c.pos()] = false;
				}
			}
		}

		return PathFinder.find(ch.pos(), to, passable );

	}

	public static int findStep(Char ch, int to, boolean[] pass, boolean[] visible, boolean chars ) {

		if (Dungeon.level.adjacent(ch.pos(), to )) {
			return Actor.findChar( to ) == null && (pass[to] || Dungeon.level.avoid[to]) ? to : -1;
		}

		setupPassable();
		if (ch.flying || ch.buff( Amok.class ) != null) {
			BArray.or( pass, Dungeon.level.avoid, passable );
		} else {
			System.arraycopy( pass, 0, passable, 0, Dungeon.level.length() );
		}

		if (Char.hasProp(ch, Char.Property.LARGE)){
			BArray.and( passable, Dungeon.level.openSpace, passable );
		}

		if (chars){
			for (Char c : Actor.chars()) {
				if (visible[c.pos()]) {
					passable[c.pos()] = false;
				}
			}
		}

		return PathFinder.getStep(ch.pos(), to, passable );

	}

	public static int flee( Char ch, int from, boolean[] pass, boolean[] visible, boolean chars ) {

		setupPassable();
		if (ch.flying) {
			BArray.or( pass, Dungeon.level.avoid, passable );
		} else {
			System.arraycopy( pass, 0, passable, 0, Dungeon.level.length() );
		}

		if (Char.hasProp(ch, Char.Property.LARGE)){
			BArray.and( passable, Dungeon.level.openSpace, passable );
		}

		passable[ch.pos()] = true;

		//only consider chars impassable if our retreat path runs into them
		int step = PathFinder.getStepBack( ch.pos(), from, passable );
		int tries = 10;
		while (step != -1 && Actor.findChar(step) != null && tries-- > 0) {
			passable[step] = false;
			step = PathFinder.getStepBack( ch.pos(), from, passable );
		}
		if (tries <= 0) return -1;
		return step;

	}

}
