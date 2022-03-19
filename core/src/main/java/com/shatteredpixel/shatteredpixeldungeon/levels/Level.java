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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.badlogic.gdx.utils.IntMap;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.LevelObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.DanceFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Desert;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Revealing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shadows;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stacking;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.HolderMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WindParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.PokerToken;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.AquaBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.HighGrass;
import com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks.Chapter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.BlackjackRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.Currency;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public abstract class Level implements Bundlable {
	public static final int SIZE_LIMIT = 16383;
	public static enum Feeling {
		NONE,
		CHASM,
		WATER,
		GRASS,
		DARK,
		LARGE,
		TRAPS,
		SECRETS
	}

	protected int width;
	protected int height;
	protected int length;

	protected static final float TIME_TO_RESPAWN	= 50;

	public int version;

	public int[] map;
	public boolean[] visited;
	public boolean[] mapped;
	public boolean[] discoverable;

    //Variable for Amnesia challenge
    public boolean[] needUpdateFog;
    public int viewDistance = Challenges.DARKNESS.enabled() ? 2 : 8;

	public boolean[] heroFOV;

	public boolean[] passable;
	public boolean[] losBlocking;
	public boolean[] flamable;
	public boolean[] secret;
	public boolean[] solid;
	public boolean[] avoid;
	public boolean[] water;
	public boolean[] pit;

	public boolean[] openSpace;

	public Feeling feeling = Feeling.NONE;

	public int entrance;
	public int exit;

	//when a boss level has become locked.
	public boolean locked = false;

	private HashSet<Mob> mobs;
	private HashMap<Integer,Mob> mobsPositioned;
	private HashSet<Mob> dispositionedMobs;
	public HashSet<LevelObject> objects;
	public SparseArray<Heap> heaps;
	public HashMap<Class<? extends Blob>,Blob> blobs;
	public SparseArray<Plant> plants;
	private SparseArray<Trap> traps;
	public HashSet<CustomTilemap> customTiles;
	public HashSet<CustomTilemap> customWalls;

	protected ArrayList<Item> itemsToSpawn = new ArrayList<>();
	protected ArrayList<Item> guaranteedItems = new ArrayList<>();

	protected Group visuals;

	public int color1 = 0x004400;
	public int color2 = 0x88CC44;

	private static final String VERSION     = "version";
	private static final String WIDTH       = "width";
	private static final String HEIGHT      = "height";
	private static final String MAP			= "map";
	private static final String VISITED		= "visited";
	private static final String MAPPED		= "mapped";
	private static final String ENTRANCE	= "entrance";
	private static final String EXIT		= "exit";
	private static final String LOCKED      = "locked";
	private static final String HEAPS		= "heaps";
	private static final String PLANTS		= "plants";
	private static final String TRAPS       = "traps";
	private static final String CUSTOM_TILES= "customTiles";
	private static final String CUSTOM_WALLS= "customWalls";
	private static final String MOBS		= "mobs";
	private static final String BLOBS		= "blobs";
	private static final String FEELING		= "feeling";
	private static final String OBJECTS		= "levelObjects";
	private static final String EXTRA_DATA	= "extraData";

	public void create() {

		Random.pushGenerator( Dungeon.seedCurDepth() );
		do {
			width = height = length = 0;

			mobs = new HashSet<>();
			dispositionedMobs = new HashSet<>();
			mobsPositioned = new HashMap<>();
			objects = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new HashMap<>();
			plants = new SparseArray<>();
			traps = new SparseArray<>();
			customTiles = new HashSet<>();
			customWalls = new HashSet<>();

		} while (!build());

		for (int i = 0; i < map.length; i++) {
			if ( map[i] == Terrain.NO_PAINT ) map[i] = Terrain.EMPTY;
		}

		if (!(Dungeon.bossLevel())) {
			int bonusNum = (int) Math.round(Math.sqrt(Challenges.nRoomsMult() * Challenges.roomSizeMult() - 1)) + 1;

			if(this instanceof RegularLevel){
				bonusNum = Math.round(((RegularLevel) this).enlargedFactor() * Challenges.roomSizeMult() - 1) / 2 + 1;
				if(Challenges.GRINDING.enabled()){
					bonusNum = Math.round(((RegularLevel) this).enlargedFactor() * Challenges.roomSizeMult() - 1) + 1;
				}
			}

			if (Challenges.FAMINE.enabled()) {
				for (int i = 0; i < bonusNum; i++) {
					addItemToSpawn(new SmallRation());
				}
			} else {
				for (int i = 0; i < bonusNum; i++) {
					addItemToSpawn(Generator.random(Generator.Category.FOOD));
				}
			}

			if (Challenges.DARKNESS.enabled()) {
				for (int i = 0; i < bonusNum; i++) {
					addItemToSpawn( new Torch() );
				}
			}
			if (Challenges.DESERT.enabled()) {
				for (int i = 0; i < bonusNum; i++) {
					addItemToSpawn( new AquaBlast() );
				}
			}

			if (Challenges.SECOND_TRY.enabled()) {
				int d = Dungeon.legacyDepth() - 1 - Dungeon.legacyDepth() / 5;
				long seed = Dungeon.seed + Challenges.SECOND_TRY.id + d / 3;

				int offset = d - d / 3 * 3;
				if (seed % 3 == offset) {
					addItemToSpawn(Generator.randomWeapon());
				}
				seed = Math.abs(((seed + 347) * 397) ^ (seed * 349));
				if (seed % 3 == offset) {
					addItemToSpawn(Generator.randomArmor());
				}
				seed = Math.abs(((seed + 347) * 397) ^ (seed * 349));
				if (seed % 3 == offset) {
					addItemToSpawn(Generator.random(Generator.Category.RING));
				}
				seed = Math.abs(((seed + 347) * 397) ^ (seed * 349));
				if (seed % 3 == offset) {
					addItemToSpawn(Generator.random(Generator.Category.WAND));
				}
				seed = Math.abs(((seed + 347) * 397) ^ (seed * 349));
				if (seed % 3 == offset) {
					addItemToSpawn(Generator.randomMissile());
				}
				seed = Math.abs(((seed + 347) * 397) ^ (seed * 349));
				if (seed % 3 == offset) {
					addItemToSpawn(Generator.random());
				}
			}

			if (Dungeon.posNeeded()) {
				addItemToSpawn( new PotionOfStrength() );
				Dungeon.LimitedDrops.STRENGTH_POTIONS.setCount(Dungeon.LimitedDrops.STRENGTH_POTIONS.getCount() + 1);
			}
			if (Dungeon.souNeeded()) {
				addItemToSpawn( new ScrollOfUpgrade() );
				Dungeon.LimitedDrops.UPGRADE_SCROLLS.setCount(Dungeon.LimitedDrops.UPGRADE_SCROLLS.getCount() + 1);
			}
			if (Dungeon.asNeeded()) {
				addItemToSpawn( new Stylus() );
				Dungeon.LimitedDrops.ARCANE_STYLI.setCount(Dungeon.LimitedDrops.ARCANE_STYLI.getCount() + 1);
			}
			//one scroll of transmutation is guaranteed to spawn somewhere on chapter 2-4
			int enchChapter = (int)((Dungeon.seed / 10) % 3) + 1;
			if ( Dungeon.scalingChapter() == enchChapter &&
					Dungeon.seed % 4 + 1 == Dungeon.depth().chapterProgression() ){
				addItemToSpawn( new StoneOfEnchantment() );
			}

			if (Dungeon.depth().chapter() == Chapter.SEWERS &&
					Dungeon.depth().chapterProgression() == ((Dungeon.seed % 3) + 1)) {
				addItemToSpawn(new StoneOfIntuition());
			}

			if (!Dungeon.depth().firstLevel()) {
				//50% chance of getting a level feeling
				//~7.15% chance for each feeling
				switch (Random.Int( 14 )) {
					case 0:
						if(Challenges.LINEAR.enabled()){
							feeling = Feeling.LARGE;
						} else {
							feeling = Feeling.CHASM;
						}
						break;
					case 1:
						feeling = Feeling.WATER;
						break;
					case 2:
						feeling = Feeling.GRASS;
						break;
					case 3:
						feeling = Feeling.DARK;
						for (int i = 0; i < bonusNum; i++) {
							addItemToSpawn(new Torch());
						}
						viewDistance = Math.round(viewDistance/2f);
						break;
					case 4:
						feeling = Feeling.LARGE;
						for (int i = 0; i < bonusNum; i++) {
							addItemToSpawn(Generator.random(Generator.Category.FOOD));
						}
						break;
					case 5:
						feeling = Feeling.TRAPS;
						break;
					case 6:
						feeling = Feeling.SECRETS;
						break;
				}
			}
		}

		buildFlagMaps();
		cleanWalls();

		createMobs();
		createItems();
		recalculateMobsPositions();
		postCreate();

		if (Challenges.DANCE_FLOOR.enabled()) {
			GameScene.add(Blob.seed(0, 1, DanceFloor.class, this));
		}

		Random.popGenerator();
	}

	public void setSize(int w, int h){

		width = w;
		height = h;
		length = w * h;

		map = new int[length];
		Arrays.fill( map, feeling == Level.Feeling.CHASM ? Terrain.CHASM : Terrain.WALL );

		visited     = new boolean[length];
		mapped      = new boolean[length];

		heroFOV     = new boolean[length];

		passable	= new boolean[length];
		losBlocking	= new boolean[length];
		flamable	= new boolean[length];
		secret		= new boolean[length];
		solid		= new boolean[length];
		avoid		= new boolean[length];
		water		= new boolean[length];
		pit			= new boolean[length];

		openSpace   = new boolean[length];

		PathFinder.setMapSize(w, h);
	}

	public void reset() {

		for (Mob mob : mobs.toArray( new Mob[0] )) {
			if (!mob.reset()) {
				mobs.remove( mob );
			}
		}
		createMobs();
	}

	public void recalculateMobsPositions() {
		mobsPositioned.clear();
		for (Mob mob : mobs) {
			addMob(mob);
		}
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {

		version = bundle.getInt( VERSION );

		//saves from before v0.9.0b are not supported
		if (version < ShatteredPixelDungeon.v0_9_0b){
			throw new RuntimeException("old save");
		}

		setSize( bundle.getInt(WIDTH), bundle.getInt(HEIGHT));

		mobs = new HashSet<>();
		dispositionedMobs = new HashSet<>();
		mobsPositioned = new HashMap<>();
		objects = new HashSet<>();
		heaps = new SparseArray<>();
		blobs = new HashMap<>();
		plants = new SparseArray<>();
		traps = new SparseArray<>();
		customTiles = new HashSet<>();
		customWalls = new HashSet<>();

		map		= bundle.getIntArray( MAP );

		visited	= bundle.getBooleanArray( VISITED );
		mapped	= bundle.getBooleanArray( MAPPED );

		entrance	= bundle.getInt( ENTRANCE );
		exit		= bundle.getInt( EXIT );

		locked      = bundle.getBoolean( LOCKED );

		Collection<Bundlable> collection = bundle.getCollection( HEAPS );
		for (Bundlable h : collection) {
			Heap heap = (Heap)h;
			if (!heap.isEmpty())
				heaps.put( heap.pos, heap );
		}

		collection = bundle.getCollection( PLANTS );
		for (Bundlable p : collection) {
			Plant plant = (Plant)p;
			plants.put( plant.pos, plant );
		}

		collection = bundle.getCollection( TRAPS );
		for (Bundlable p : collection) {
			Trap trap = (Trap)p;
			traps.put( trap.pos, trap );
		}

		collection = bundle.getCollection( CUSTOM_TILES );
		for (Bundlable p : collection) {
			CustomTilemap vis = (CustomTilemap)p;
			customTiles.add(vis);
		}

		collection = bundle.getCollection( CUSTOM_WALLS );
		for (Bundlable p : collection) {
			CustomTilemap vis = (CustomTilemap)p;
			customWalls.add(vis);
		}

		collection = bundle.getCollection( MOBS );
		for (Bundlable m : collection) {
			Mob mob = (Mob)m;
			if (mob != null) {
				addMob( mob );
			}
		}

		collection = bundle.getCollection( OBJECTS );
		for (Bundlable m : collection) {
			LevelObject obj = (LevelObject)m;
			if (obj != null) {
				objects.add( obj );
			}
		}

		collection = bundle.getCollection( BLOBS );
		for (Bundlable b : collection) {
			Blob blob = (Blob)b;
			blobs.put( blob.getClass(), blob );
		}

		feeling = bundle.getEnum( FEELING, Feeling.class );
		if (feeling == Feeling.DARK)
			viewDistance = Math.round(viewDistance/2f);

		if (bundle.contains( "mobs_to_spawn" )) {
			for (Class<? extends Mob> mob : bundle.getClassArray("mobs_to_spawn")) {
				if (mob != null) mobsToSpawn.add(mob);
			}
		}

		if (bundle.contains( "respawner" )){
			respawner = (Respawner) bundle.get("respawner");
		}

		if ( version < ShatteredPixelDungeon.TCPD_v1_1_0 && !(this instanceof CavesBossLevel) ) {
			for (int i = 0; i < map.length; i++) {
				if ( map[i] == Terrain.TECHNICAL || map[i] == Terrain.NO_PAINT || map[i] == Terrain.TECHNICAL_2 ) {
					map[i] = Terrain.EMPTY;
				}
			}
		}

		buildFlagMaps();
		cleanWalls();
		recalculateMobsPositions();
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( VERSION, Game.versionCode );
		bundle.put( WIDTH, width );
		bundle.put( HEIGHT, height );
		bundle.put( MAP, map );
		bundle.put( VISITED, visited );
		bundle.put( MAPPED, mapped );
		bundle.put( ENTRANCE, entrance );
		bundle.put( EXIT, exit );
		bundle.put( LOCKED, locked );
		bundle.put( HEAPS, heaps.valueList() );
		bundle.put( PLANTS, plants.valueList() );
		bundle.put( TRAPS, traps.valueList() );
		bundle.put( CUSTOM_TILES, customTiles );
		bundle.put( CUSTOM_WALLS, customWalls );
		bundle.put( MOBS, mobs );
		bundle.put( OBJECTS, objects );
		bundle.put( BLOBS, blobs.values() );
		bundle.put( FEELING, feeling );
		bundle.put( "mobs_to_spawn", mobsToSpawn.toArray(new Class[0]));
		bundle.put( "respawner", respawner );
	}

	public int tunnelTile() {
		return feeling == Feeling.CHASM ? Terrain.EMPTY_SP : Terrain.EMPTY;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public int length() {
		return length;
	}

	public String tilesTex() {
		return null;
	}

	public String waterTex() {
		return null;
	}

	abstract protected boolean build();

	private ArrayList<Class<?extends Mob>> mobsToSpawn = new ArrayList<>();

	public Mob createMob() {
		if (mobsToSpawn == null || mobsToSpawn.isEmpty()) {
			mobsToSpawn = Bestiary.getMobRotation(Dungeon.depth());
		}

		Mob m = Reflection.newInstance(mobsToSpawn.remove(0));
        if (Challenges.CHAMPION_ENEMIES.enabled()) {
            ChampionEnemy.rollForChampion(m, mobs);
		}
		return m;
	}

	abstract protected void createMobs();

	abstract protected void createItems();

    public BlackjackRoom getBlackjackRoom() {
        return null;
    }

    protected void blackjackHeaps() {
        BlackjackRoom room = getBlackjackRoom();
        if (room == null) return;
        Heap h;
		for (int c : heaps.keyArray()) {
			h = heaps.get(c);
			if (h != null) {
				if (h.type == Heap.Type.FOR_SALE) continue;
				int tokensCount = 0;
				for (Item i : (LinkedList<Item>) h.items.clone()) {
					if (Currency.TOKENS.sellPrice(i) > 0) {
						tokensCount += (int) (Currency.TOKENS.sellPrice(i) * Random.NormalFloat(0.33f, 1f));
						switch (h.type) {
							case LOCKED_CHEST:
								room.chestItems.add(i);
								break;
							case CRYSTAL_CHEST:
								room.crystalItems.add(i);
								break;
							default:
								room.sellItems.add(i);
						}
						h.items.remove(i);
					}
				}
				if (h.items.size() == 0) {
					heaps.remove(c);
				}
				if (tokensCount > 0) {
					Heap gold = drop(new PokerToken(tokensCount), c);
					gold.haunted = h.haunted;
					if (h.type == Heap.Type.SKELETON
							|| h.type == Heap.Type.REMAINS
							|| h.type == Heap.Type.CHEST
							|| h.type == Heap.Type.TOMB) {
						gold.type = h.type;
					}
				}
			}
		}
    }

	protected void mimicsHeaps() {
		Heap h;
		for (int c : heaps.keyArray()) {
			h = heaps.get(c);
			if (h != null) {
				if (h.type == Heap.Type.CHEST ||
						Challenges.MIMICS_2.enabled()) {
					heaps.remove(c);
					HolderMimic.spawnAt(c, this, h);
				}
			}
		}
	}

	protected void applySecondTry() {
    	int barricades = 0;
		for (int i = 0; i < length; i++) {
			if (map[i] == Terrain.LOCKED_DOOR) {
				set(i, Terrain.DOOR, this);
			}
			if (map[i] == Terrain.BARRICADE) {
				set(i, Terrain.EMBERS, this);
				barricades++;
			}
		}

		Heap h;
		for (int c : heaps.keyArray()) {
			h = heaps.get(c);
			if (h.type == Heap.Type.FOR_SALE) continue;
			for (Item item : new ArrayList<>(h.items)) {
				if (item.unique) continue;
				if (!guaranteedItems.contains(item) || item instanceof Key) h.items.remove(item);
				if (item instanceof PotionOfLiquidFlame && barricades-- > 0) h.items.remove(item);
			}
			if (h.items.isEmpty()) {
				heaps.remove(c);
			}
		}

		for (Blob blob : blobs.values()) {
			if(blob instanceof WellWater){
				blob.fullyClear(this);
			}
		}
	}

	protected void applyThunderstruck(){
		for (Trap trap : traps().valueList()) {
			trap.reveal();
		}
	}

    protected void postCreate(){
    	if(!Dungeon.bossLevel() && Challenges.SECOND_TRY.enabled()){
			applySecondTry();
		}
    	if(!Dungeon.bossLevel() && Challenges.BLACKJACK.enabled()){
    		blackjackHeaps();
		}
    	if(!Dungeon.bossLevel() && Challenges.MIMICS.enabled()){
    		mimicsHeaps();
		}
    	if(Challenges.THUNDERSTRUCK.enabled()){
			applyThunderstruck();
	    }
	}
	public void seal(){
		if (!locked) {
			locked = true;
			Buff.affect(Dungeon.hero, LockedFloor.class);
		}
	}

	public void unseal(){
		if (locked) {
			locked = false;
			if (Dungeon.hero.buff(LockedFloor.class) != null){
				Dungeon.hero.buff(LockedFloor.class).detach();
			}
		}
	}

	public ArrayList<Item> getItemsToPreserveFromSealedResurrect(){
		ArrayList<Item> items = new ArrayList<>();
		for (Heap h : heaps.valueList()){
			if (h.type == Heap.Type.HEAP) items.addAll(h.items);
		}
		for (Mob m : mobs){
			for (PinCushion b : m.buffs(PinCushion.class)){
				items.addAll(b.getStuckItems());
			}
		}
		for (HeavyBoomerang.CircleBack b : Dungeon.hero.buffs(HeavyBoomerang.CircleBack.class)){
			if (b.activeDepth().equals(Dungeon.depth())) items.add(b.cancel());
		}
		return items;
	}

	public Group addVisuals() {
		if (visuals == null || visuals.parent == null){
			visuals = new Group();
		} else {
			visuals.clear();
			visuals.camera = null;
		}
		for (int i=0; i < length(); i++) {
			if (pit[i]) {
				visuals.add( new WindParticle.Wind( i ) );
				if (i >= width() && water[i-width()]) {
					visuals.add( new FlowParticle.Flow( i - width() ) );
				}
			}
		}
		return visuals;
	}

	public int nMobs() {
		return 0;
	}

	public Mob findMob( int pos ){
    	Mob mob = mobsPositioned.get(pos);
    	if(mob!=null) return mob;
    	return null;
	}

	public HashSet<Mob> mobs() {
		return mobs;
	}

	public void addMob(Mob mob) {
		mobs.add(mob);
		Mob old = mobsPositioned.get(mob.pos());
		if (old != null && mobs.contains(old)) {
			dispositionedMobs.add(old);
		}
		mobsPositioned.put(mob.pos(), mob);
	}

	public void removeMob(Char ch) {
		mobs.remove(ch);
		Mob old = mobsPositioned.get(ch.pos());
		if (old == ch) {
			mobsPositioned.remove(ch.pos());
		}
	}

	public void moveMob(Mob mob, int oldPos, int newPos){
		if (!mobs.contains(mob)) return;
		mobsPositioned.remove(oldPos);
		Mob oldChar = mobsPositioned.get(newPos);
		if (oldChar != null && mobs.contains(oldChar)) {
			dispositionedMobs.add(oldChar);
		}
		mobsPositioned.put(newPos, mob);
	}

	public void fixDispositionedMobs(){
		if (dispositionedMobs.size() > 0) {
			Mob[] toRemove = new Mob[dispositionedMobs.size()];
			int i = 0;
			for (Mob ch : dispositionedMobs) {
				if (!mobs.contains(ch)) {
					toRemove[i] = ch;
					i++;
					continue;
				}
				Mob old = findMob(ch.pos());
				if (old == null || !mobs.contains(old)) {
					mobsPositioned.put(ch.pos(), ch);
					toRemove[i] = ch;
					i++;
					continue;
				}
			}

			for (i -= 1; i >= 0; i--) {
				dispositionedMobs.remove(toRemove[i]);
			}
		}
	}

	private Respawner respawner;

	public Actor addRespawner() {
		if (respawner == null){
			respawner = new Respawner();
			Actor.addDelayed(respawner, Actor.TICK);
		} else {
			Actor.add(respawner);
		}
		return respawner;
	}

	public static class Respawner extends Actor {
		{
			actPriority = BUFF_PRIO; //as if it were a buff.
		}

		private float waitTime = 0f;
		private static final String WAIT_TIME = "wait_time";

		@Override
		protected boolean act() {
			float count = 0;

			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
				if (mob.alignment == Char.Alignment.ENEMY && !mob.properties().contains(Char.Property.MINIBOSS)) {
					count += mob.spawningWeight();
					Stacking stack = mob.buff(Stacking.class);
					if (stack != null) count += stack.count;
				}
			}

			float nMobs = Dungeon.level.nMobs();
			float leftover = nMobs - count;
			leftover = Math.min(leftover, 1000);

			boolean fractal = false;

			float cooldown = Dungeon.level.respawnCooldown();
			if (Challenges.REPOPULATION.enabled()) {
				fractal = Challenges.FRACTAL_HIVE.enabled();
				cooldown *= GameMath.gate(0, 1.1f * (1 - leftover / nMobs) - 0.1f, 1);
				if (fractal) {
					cooldown = Math.min(cooldown, TIME_TO_RESPAWN / 2f);
				}
			}

			if (waitTime >= cooldown && (leftover > 0 || fractal)) {

				boolean allowNearHero = Challenges.EXHIBITIONISM.enabled() || Challenges.SMALL_LEVELS.enabled();
				PathFinder.buildDistanceMap(Dungeon.hero.pos(), BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));

				Mob mob = Dungeon.level.createMob();
				mob.state = mob.WANDERING;
				mob.pos(Dungeon.level.randomRespawnCell(mob));
				if (Dungeon.hero.isAlive() && mob.pos() != -1 && (PathFinder.distance[mob.pos()] >= 12 || allowNearHero)) {
					GameScene.add(mob);
					if (Statistics.amuletObtained) {
						mob.beckon(Dungeon.hero.pos());
					}
					if (!mob.buffs(ChampionEnemy.class).isEmpty()) {
//                        GLog.w(Messages.get(ChampionEnemy.class, "warn"));
					}
					if (Challenges.STACKING_SPAWN.enabled()) {
						Buff.affect(mob, Stacking.class).count = Math.max((int) leftover, 1);
					}
					spend(Math.min(cooldown, TICK));
					waitTime = 1;
				} else {
					//try again in 1 turn
					spend(TICK);
					waitTime++;
				}
			} else {
				spend(TICK);
				waitTime++;
			}

			return true;
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(WAIT_TIME, waitTime);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			if (bundle.contains(WAIT_TIME)) {
				waitTime = bundle.getInt(WAIT_TIME);
			} else {
				waitTime = TIME_TO_RESPAWN;
			}
		}
	}

	public float respawnCooldown(){
        if (Challenges.RESURRECTION.enabled()) {
            return 1f;
        }
		if (Statistics.amuletObtained){
			return TIME_TO_RESPAWN/2f;
		} else if (Dungeon.level.feeling == Feeling.DARK){
			return 2*TIME_TO_RESPAWN/3f;
		} else {
			return TIME_TO_RESPAWN;
		}
	}

	public int randomRespawnCell(Char ch) {
		return randomRespawnCell(ch, false);
	}
	public int randomRespawnCell(Char ch, boolean ignoreMobs) {
		int cell;
        boolean allowHeroFov = Challenges.EXHIBITIONISM.enabled();
        boolean allowStacking = ignoreMobs || Challenges.STACKING.enabled();
		do {
			cell = Random.Int( length() );
        } while ((Dungeon.level == this && (heroFOV[cell] && !allowHeroFov))
				|| !passable[cell]
				|| (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell])
				|| (allowStacking || Actor.findChar( cell ) != null));
		return cell;
	}

	public int randomDestination( Char ch ) {
		int cell;
		do {
			cell = Random.Int( length() );
		} while (!passable[cell]
				|| (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell]));
		return cell;
	}

	public int randomTrapCell() {
		int cell;
		do {
			cell = Random.Int( length() );
		} while (!canPlaceTrap(cell));
		return cell;
	}

	public boolean canPlaceTrap(int cell) {
		return (Terrain.flags[map[cell]] & Terrain.SOLID) == 0 &&
				(Terrain.flags[map[cell]] & Terrain.PIT) == 0 &&
				map[cell] != Terrain.DOOR &&
				map[cell] != Terrain.OPEN_DOOR &&
				cell != entrance &&
				cell != exit &&
				!hasCustomTerrain( cell );
	}

	public void addItemToSpawn( Item item ) {
		if (item != null) {
			itemsToSpawn.add( item );
			guaranteedItems.add( item );
		}
	}

	public Item findPrizeItem(){ return findPrizeItem(null); }

	public Item findPrizeItem(Class<?extends Item> match){
		if (itemsToSpawn.size() == 0)
			return null;

		if (match == null){
			Item item = Random.element(itemsToSpawn);
			itemsToSpawn.remove(item);
			return item;
		}

		for (Item item : itemsToSpawn){
			if (match.isInstance(item)){
				itemsToSpawn.remove( item );
				return item;
			}
		}

		return null;
	}

	public void updateTerrainFlags(int cell){
		int flags = Terrain.flags[map[cell]];
		passable[cell]		= (flags & Terrain.PASSABLE) != 0;
		losBlocking[cell]	= (flags & Terrain.LOS_BLOCKING) != 0;
		flamable[cell]		= (flags & Terrain.FLAMABLE) != 0;
		secret[cell]		= (flags & Terrain.SECRET) != 0;
		solid[cell]			= (flags & Terrain.SOLID) != 0;
		avoid[cell]			= (flags & Terrain.AVOID) != 0;
		water[cell]			= (flags & Terrain.LIQUID) != 0;
		pit[cell]			= (flags & Terrain.PIT) != 0;
	}

	public void updateAdditionalFlags(int cell) {
		Web w = (Web) blobs.get(Web.class);
		if (w != null && w.volume > 0) {
			solid[cell] = solid[cell] || w.cur[cell] > 0;
			flamable[cell] = flamable[cell] || w.cur[cell] > 0;
		}
		Trap t = getTrap(cell);
		if (t != null) {
			avoid[cell] = avoid[cell] || (t.visible && t.active);
			passable[cell] = passable[cell] && !(t.visible && t.active);
			secret[cell] = secret[cell] || (!t.visible && t.active);
		}

		if (cell < width() || cell > length - width() || cell % width() == 0 || (cell + 1) % width() == 0) {
			passable[cell] = avoid[cell] = false;
			losBlocking[cell] = solid[cell] = true;
		}
	}

	public void fullFlagsUpdate(int cell){
		updateTerrainFlags(cell);
		updateAdditionalFlags(cell);
	}

	public void buildFlagMaps() {

		for (int i=0; i < length(); i++) {
			updateTerrainFlags(i);
		}

		Web w = (Web) blobs.get(Web.class);
		if (w != null && w.volume > 0){
			for (int i=0; i < length(); i++) {
				solid[i] = solid[i] || w.cur[i] > 0;
				flamable[i] = flamable[i] || w.cur[i] > 0;
			}
		}

		for (IntMap.Entry<Trap> trap : traps) {
			int cell = trap.key;
			avoid[cell] = avoid[cell] || (trap.value.visible && trap.value.active);
			passable[cell] = passable[cell] && !(trap.value.visible && trap.value.active);
			secret[cell] = secret[cell] || (!trap.value.visible && trap.value.active);
		}

		int lastRow = length() - width();
		for (int i=0; i < width(); i++) {
			passable[i] = avoid[i] = false;
			losBlocking[i] = solid[i] = true;
			passable[lastRow + i] = avoid[lastRow + i] = false;
			losBlocking[lastRow + i] = solid[lastRow + i] = true;
		}
		for (int i=width(); i < lastRow; i += width()) {
			passable[i] = avoid[i] = false;
			losBlocking[i] = solid[i] = true;
			passable[i + width()-1] = avoid[i + width()-1] = false;
			losBlocking[i + width()-1] = solid[i + width()-1] = true;
		}

		//an open space is large enough to fit large mobs. A space is open when it is not solid
		// and there is and open corner with both adjacent cells opens
		for (int i=0; i < length(); i++) {
			if (solid[i]){
				openSpace[i] = false;
			} else {
				for (int j = 1; j < PathFinder.CIRCLE8.length; j += 2) {
					if (solid[i + PathFinder.CIRCLE8[j]]) {
						openSpace[i] = false;
					} else if (!solid[i + PathFinder.CIRCLE8[(j + 1) % 8]]
							&& !solid[i + PathFinder.CIRCLE8[(j + 2) % 8]]) {
						openSpace[i] = true;
						break;
					}
				}
			}
		}

	}

	public void destroy( int pos ) {
		//if raw tile type is flammable or empty
		int terr = map[pos];
		if (terr == Terrain.EMPTY || terr == Terrain.EMPTY_DECO
				|| (Terrain.flags[map[pos]] & Terrain.FLAMABLE) != 0) {
			set(pos, Terrain.EMBERS);
		}
		Blob web = blobs.get(Web.class);
		if (web != null){
			web.clear(pos);
		}
	}

	public void cleanWalls() {
		if (discoverable == null || discoverable.length != length) {
			discoverable = new boolean[length()];
		}

		for (int i=0; i < length(); i++) {

			boolean d = false;

            for (int j = 0; j < 9; j++) {
                int n = i + PathFinder.NEIGHBOURS9[j];
				if (n >= 0 && n < length() && map[n] != Terrain.WALL && map[n] != Terrain.WALL_DECO) {
					d = true;
					break;
				}
			}

			discoverable[i] = d;
		}
	}

	public static void set( int cell, int terrain ){
		set( cell, terrain, Dungeon.level );
	}

	public static void set( int cell, int terrain, Level level ) {
		Painter.set( level, cell, terrain );

//		if (terrain != Terrain.TRAP && terrain != Terrain.SECRET_TRAP && terrain != Terrain.INACTIVE_TRAP){
//			level.traps.remove( cell );
//		}

		level.fullFlagsUpdate( cell );

		for (int i : PathFinder.NEIGHBOURS9){
			i = cell + i;
			if (level.solid[i]){
				level.openSpace[i] = false;
			} else {
				for (int j = 1; j < PathFinder.CIRCLE8.length; j += 2){
					if (level.solid[i+PathFinder.CIRCLE8[j]]) {
						level.openSpace[i] = false;
					} else if (!level.solid[i+PathFinder.CIRCLE8[(j+1)%8]]
							&& !level.solid[i+PathFinder.CIRCLE8[(j+2)%8]]){
						level.openSpace[i] = true;
						break;
					}
				}
			}
		}
	}

	public Heap drop( Item item, int cell ) {

		if (item == null || Challenges.isItemBlocked(item)){

			//create a dummy heap, give it a dummy sprite, don't add it to the game, and return it.
			//effectively nullifies whatever the logic calling this wants to do, including dropping items.
			Heap heap = new Heap();
			ItemSprite sprite = heap.sprite = new ItemSprite();
			sprite.link(heap);
			return heap;

		}

		Heap heap = heaps.get( cell );
		if (heap == null) {

			heap = new Heap();
			heap.seen = Dungeon.level == this && heroFOV[cell];
			heap.pos = cell;
			heap.drop(item);
			if (map[cell] == Terrain.CHASM || (Dungeon.level != null && pit[cell])) {
				Dungeon.dropToChasm( item );
				GameScene.discard( heap );
			} else {
				heaps.put( cell, heap );
				GameScene.add( heap );
			}

		} else if (heap.type == Heap.Type.LOCKED_CHEST || heap.type == Heap.Type.CRYSTAL_CHEST) {

			int n;
			do {
                n = cell + PathFinder.NEIGHBOURS8[Random.Int(8)];
			} while (!passable[n] && !avoid[n]);
			return drop( item, n );

		} else {
			heap.drop(item);
		}

		if (Dungeon.level != null && ShatteredPixelDungeon.scene() instanceof GameScene) {
			pressCell( cell );
		}

		return heap;
	}

	public Plant plant( Plant.Seed seed, int pos ) {

        if (Challenges.BARREN_LAND.enabled()) {
			return null;
		}

		Plant plant = plants.get( pos );
		if (plant != null) {
			plant.wither();
		}

		if (map[pos] == Terrain.HIGH_GRASS ||
				map[pos] == Terrain.FURROWED_GRASS ||
				map[pos] == Terrain.EMPTY ||
				map[pos] == Terrain.EMBERS ||
				map[pos] == Terrain.EMPTY_DECO) {
			set(pos, Terrain.GRASS, this);
			GameScene.updateMap(pos);
		}

		plant = seed.couch( pos, this );
		plants.put( pos, plant );

		GameScene.plantSeed( pos );

		for (Char ch : Actor.chars()){
			if (ch instanceof WandOfRegrowth.Lotus
					&& ((WandOfRegrowth.Lotus) ch).inRange(pos)
					&& Actor.findChar(pos) != null){
				plant.trigger();
				return null;
			}
		}

		return plant;
	}

	public void uproot( int pos ) {
		plants.remove( pos );
		GameScene.updateMap( pos );
	}

	public <T extends LevelObject> T setObject( T obj, int pos ) {
		obj.pos = pos;
		Actor.add( obj );
		objects.add( obj );
		return obj;
	}

	public void removeObject( LevelObject obj ) {
		objects.remove( obj );
		Actor.remove( obj );
	}

	public Trap setTrap( Trap trap, int pos ) {
		Trap existingTrap = getTrap( pos );
		if ( existingTrap != null ) {
			traps.remove( pos );
		}
		trap.set( pos );
		traps.put( pos, trap );
		fullFlagsUpdate( pos );
		GameScene.updateMap( pos );
		return trap;
	}

	public void disarmTrap( int pos ) {
		Trap t = getTrap( pos );
		if ( t != null ) {
			t.active = false;
			fullFlagsUpdate( pos );
			GameScene.updateMap( pos );
		}
	}

	public void removeTrap(int pos){
		Trap t = getTrap( pos );
		if ( t != null ) {
			traps.remove( t.pos );
			fullFlagsUpdate( pos );
			GameScene.updateMap( pos );
		}
	}

	public void clearTraps( boolean update ) {
		if ( !update ) this.traps.clear();
		else {
			SparseArray<Trap> traps = new SparseArray<>( this.traps );
			this.traps.clear();
			for (IntMap.Entry<Trap> trap : traps) {
				fullFlagsUpdate( trap.key );
			}
		}
	}

	public SparseArray<Trap> traps(){
		return traps;
	}

	public Trap getTrap( int pos ) {
		return traps.get( pos );
	}

	public boolean hasActiveTrap( int pos ) {
		Trap t = getTrap( pos );
		return t != null && t.active;
	}
	public boolean hasSecretTrap( int pos ) {
		Trap t = getTrap( pos );
		return t != null && !t.visible && t.active;
	}

	public boolean hasInactiveTrap( int pos ) {
		Trap t = getTrap( pos );
		return t != null && !t.active && t.visible;
	}

	public void discover( int cell ) {
		set( cell, Terrain.discover( map[cell] ) );
		Trap trap = getTrap( cell );
		if (trap != null)
			trap.reveal();
		GameScene.updateMap( cell );
	}

	public boolean setCellToWater(boolean includeTraps, int cell) {
		//if a custom tilemap is over that cell, don't put water there
		if (hasCustomTerrain(cell)) return false;
		boolean trap = getTrap(cell) != null;

		int terr = map[cell];
		Blob droughtBlob = blobs.get(Desert.class);
		if (!trap && (terr == Terrain.EMPTY || terr == Terrain.GRASS ||
				terr == Terrain.EMBERS || terr == Terrain.EMPTY_SP ||
				terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS
				|| terr == Terrain.EMPTY_DECO)) {
			set(cell, Terrain.WATER);
			if (droughtBlob != null) droughtBlob.clear(cell);
			GameScene.updateMap(cell);
			return true;
		} else if (includeTraps && trap) {
			set(cell, Terrain.WATER);
			Dungeon.level.removeTrap(cell);
			if (droughtBlob != null) droughtBlob.clear(cell);
			GameScene.updateMap(cell);
			return true;
		}

		return false;
	}

	public boolean hasCustomTerrain(int cell){
		Point p = cellToPoint(cell);
		for (CustomTilemap cust : customTiles){
			Point custPoint = new Point(p);
			custPoint.x -= cust.tileX;
			custPoint.y -= cust.tileY;
			if (custPoint.x >= 0 && custPoint.y >= 0
					&& custPoint.x < cust.tileW && custPoint.y < cust.tileH){
				if (cust.image(custPoint.x, custPoint.y) != null){
					return true;
				}
			}
		}
		return false;
	}

	public boolean removeWater( int cell ){
		int terr = map[cell];
		if (terr != Terrain.WATER) return false;
		if(hasCustomTerrain(cell)) return false;

		set(cell, Terrain.EMPTY);
		GameScene.updateMap(cell);
		return true;
	}

	public int fallCell( boolean fallIntoPit ) {
		int result;
		do {
			result = randomRespawnCell( null);
		} while (getTrap(result) != null
				|| findMob(result) != null);
		return result;
	}

	public void occupyCell( Char ch ){
		if (!ch.isImmune(Web.class) && Blob.volumeAt(ch.pos(), Web.class) > 0){
			blobs.get(Web.class).clear(ch.pos());
			Web.affectChar( ch );
		}

		if (!ch.flying){

			if ( (map[ch.pos()] == Terrain.GRASS || map[ch.pos()] == Terrain.EMBERS)
					&& ch == Dungeon.hero && Dungeon.hero.hasTalent(Talent.REJUVENATING_STEPS)
					&& ch.buff(Talent.RejuvenatingStepsCooldown.class) == null){

				if (Dungeon.hero.buff(LockedFloor.class) != null && !Dungeon.hero.buff(LockedFloor.class).regenOn()){
					set(ch.pos(), Terrain.FURROWED_GRASS);
				} else if (ch.buff(Talent.RejuvenatingStepsFurrow.class) != null && ch.buff(Talent.RejuvenatingStepsFurrow.class).count() >= 200) {
					set(ch.pos(), Terrain.FURROWED_GRASS);
				} else {
					set(ch.pos(), Terrain.HIGH_GRASS);
					Buff.count(ch, Talent.RejuvenatingStepsFurrow.class, 3 - Dungeon.hero.pointsInTalent(Talent.REJUVENATING_STEPS));
				}
				GameScene.updateMap(ch.pos());
				Buff.affect(ch, Talent.RejuvenatingStepsCooldown.class, 15f - 5f*Dungeon.hero.pointsInTalent(Talent.REJUVENATING_STEPS));
			}

			if (pit[ch.pos()]){
				if (ch == Dungeon.hero) {
					Chasm.heroFall(ch.pos());
				} else if (ch instanceof Mob) {
					Chasm.mobFall( (Mob)ch );
				}
				return;
			}

			//characters which are not the hero or a sheep 'soft' press cells
			pressCell(ch.pos(), ch instanceof Hero || ch instanceof Sheep);
		} else {
			if (map[ch.pos()] == Terrain.DOOR){
				Door.enter(ch.pos());
			}
		}
	}

	//public method for forcing the hard press of a cell. e.g. when an item lands on it
	public void pressCell( int cell ){
		pressCell( cell, true );
	}

	//a 'soft' press ignores hidden traps
	//a 'hard' press triggers all things
	private void pressCell( int cell, boolean hard ) {

		Trap trap = getTrap( cell );
		if ( trap != null && !trap.visible ) {
			if ( hard )
				GLog.i( Messages.get( Level.class, "hidden_trap", trap.name() ) );
			else
				trap = null;
		}

		switch (map[cell]) {

//		case Terrain.SECRET_TRAP:
//			if (hard) {
//				trap = traps.get( cell );
//				if(trap == null){
//					set(cell,Terrain.EMPTY);
//				} else {
//					GLog.i(Messages.get(Level.class, "hidden_trap", trap.name()));
//				}
//			}
//			break;
//
//		case Terrain.TRAP:
//			trap = traps.get( cell );
//			break;

		case Terrain.HIGH_GRASS:
		case Terrain.FURROWED_GRASS:
			HighGrass.trample( this, cell);
			break;

		case Terrain.WELL:
			WellWater.affectCell( cell );
			break;

		case Terrain.DOOR:
			Door.enter( cell );
			break;
		}

		if (trap != null) {

			TimekeepersHourglass.timeFreeze timeFreeze =
					Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);

			Swiftthistle.TimeBubble bubble =
					Dungeon.hero.buff(Swiftthistle.TimeBubble.class);

			if (bubble != null){

				Sample.INSTANCE.play(Assets.Sounds.TRAP);

				discover(cell);

				bubble.setDelayedPress(cell);

			} else if (timeFreeze != null){

				Sample.INSTANCE.play(Assets.Sounds.TRAP);

				discover(cell);

				timeFreeze.setDelayedPress(cell);

			} else {

				if (Dungeon.hero.pos() == cell) {
					Dungeon.hero.interrupt();
				}

				trap.trigger();

			}
		}

		Plant plant = plants.get( cell );
		if (plant != null) {
			plant.trigger();
		}

		if (hard && Blob.volumeAt(cell, Web.class) > 0){
			blobs.get(Web.class).clear(cell);
		}
	}

	private static boolean[] heroMindFov;

	private static boolean[] modifiableBlocking;

	public void updateFieldOfView( Char c, boolean[] fieldOfView ) {

		// Ignore issue if mob is out of bounds, but only in release version
		if(!insideMap(c.pos()) && !DeviceCompat.isDebug()){
			BArray.setFalse(fieldOfView);
			return;
		}

		int cx = c.pos() % width();
		int cy = c.pos() / width();

		boolean sighted = c.buff( Blindness.class ) == null && c.buff( Shadows.class ) == null
						&& c.buff( TimekeepersHourglass.timeStasis.class ) == null && c.isAlive();
		if (sighted) {
			boolean[] blocking;

			if (modifiableBlocking == null || modifiableBlocking.length != Dungeon.level.losBlocking.length){
				modifiableBlocking = new boolean[Dungeon.level.losBlocking.length];
			}

			if ((c instanceof Hero && ((Hero) c).subClass == HeroSubClass.WARDEN)
				|| c instanceof YogFist.SoiledFist) {
				System.arraycopy(Dungeon.level.losBlocking, 0, modifiableBlocking, 0, modifiableBlocking.length);
				blocking = modifiableBlocking;
				for (int i = 0; i < blocking.length; i++){
					if (blocking[i] && (Dungeon.level.map[i] == Terrain.HIGH_GRASS || Dungeon.level.map[i] == Terrain.FURROWED_GRASS)){
						blocking[i] = false;
					}
				}
			} else if (c.alignment == Char.Alignment.ENEMY
					&& Dungeon.level.blobs.containsKey(SmokeScreen.class)
					&& Dungeon.level.blobs.get(SmokeScreen.class).volume > 0) {
				System.arraycopy(Dungeon.level.losBlocking, 0, modifiableBlocking, 0, modifiableBlocking.length);
				blocking = modifiableBlocking;
				Blob s = Dungeon.level.blobs.get(SmokeScreen.class);
				for (int i = 0; i < blocking.length; i++){
					if (!blocking[i] && s.cur[i] > 0){
						blocking[i] = true;
					}
				}
			} else {
				blocking = Dungeon.level.losBlocking;
			}

			int viewDist = c.viewDistance;
			if (c instanceof Hero){
				viewDist *= 1f + 0.25f*((Hero) c).pointsInTalent(Talent.FARSIGHT);
			}

			ShadowCaster.castShadow( cx, cy, fieldOfView, blocking, viewDist );
		} else {
			BArray.setFalse(fieldOfView);
		}

		int sense = 1;
		//Currently only the hero can get mind vision
		if (c.isAlive() && c == Dungeon.hero) {
			for (Buff b : c.buffs( MindVision.class )) {
				sense = Math.max( ((MindVision)b).distance, sense );
			}
			if (c.buff(MagicalSight.class) != null){
				sense = Math.max( MagicalSight.DISTANCE, sense );
			}
		}

		//uses rounding
		if (!sighted || sense > 1) {

			int[][] rounding = ShadowCaster.rounding;

			int left, right;
			int pos;
			for (int y = Math.max(0, cy - sense); y <= Math.min(height()-1, cy + sense); y++) {
				if (rounding[sense][Math.abs(cy - y)] < Math.abs(cy - y)) {
					left = cx - rounding[sense][Math.abs(cy - y)];
				} else {
					left = sense;
					while (rounding[sense][left] < rounding[sense][Math.abs(cy - y)]){
						left--;
					}
					left = cx - left;
				}
				right = Math.min(width()-1, cx + cx - left);
				left = Math.max(0, left);
				pos = left + y * width();
				System.arraycopy(discoverable, pos, fieldOfView, pos, right - left + 1);
			}
		}

		if (c instanceof SpiritHawk.HawkAlly && Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE) >= 3){
			int range = 1+(Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE)-2);
			for (Mob mob : mobs) {
				int p = mob.pos();
				if (!fieldOfView[p] && distance(c.pos(), p) <= range) {
					for (int i : PathFinder.NEIGHBOURS9) {
						fieldOfView[mob.pos() + i] = true;
					}
				}
			}
		}

		//Currently only the hero can get mind vision or awareness
		if (c.isAlive() && c == Dungeon.hero) {

			if (heroMindFov == null || heroMindFov.length != length()){
				heroMindFov = new boolean[length];
			} else {
				BArray.setFalse(heroMindFov);
			}

			Dungeon.hero.mindVisionEnemies.clear();
			if (c.buff( MindVision.class ) != null) {
				for (Mob mob : mobs) {
					for (int i : PathFinder.NEIGHBOURS9) {
						heroMindFov[mob.pos() + i] = true;
					}
				}
			} else {
				Hero h = (Hero) c;
				int range = 1+h.pointsInTalent(Talent.HEIGHTENED_SENSES);
				for (Mob mob : mobs) {
					int p = mob.pos();
                    if ((mob.properties().contains(Char.Property.ALWAYS_VISIBLE) || mob.buff(Revealing.class) != null) && !fieldOfView[p]) {
                        for (int i : PathFinder.NEIGHBOURS9) heroMindFov[p + i] = true;
                    }
                    if (((Hero) c).hasTalent(Talent.HEIGHTENED_SENSES))
                        if (!fieldOfView[p] && distance(c.pos(), p) <= 1 + range) {
						for (int i : PathFinder.NEIGHBOURS9) {
							heroMindFov[mob.pos() + i] = true;
						}
					}
				}
			}

			if (c.buff( Awareness.class ) != null) {
				for (Heap heap : heaps.valueList()) {
					int p = heap.pos;
					for (int i : PathFinder.NEIGHBOURS9) heroMindFov[p+i] = true;
				}
			}

			for (TalismanOfForesight.CharAwareness a : c.buffs(TalismanOfForesight.CharAwareness.class)){
				Char ch = (Char) Actor.findById(a.charID);
				if (ch == null) {
					continue;
				}
				int p = ch.pos();
				for (int i : PathFinder.NEIGHBOURS9) heroMindFov[p+i] = true;
			}

			for (TalismanOfForesight.HeapAwareness h : c.buffs(TalismanOfForesight.HeapAwareness.class)){
				if (!Dungeon.depth().equals(h.depth)) continue;
				for (int i : PathFinder.NEIGHBOURS9) heroMindFov[h.pos+i] = true;
			}

			for (Mob m : mobs){
				if (m instanceof WandOfWarding.Ward
						|| m instanceof WandOfRegrowth.Lotus
						|| m instanceof SpiritHawk.HawkAlly){
					if (m.fieldOfView == null || m.fieldOfView.length != length()){
						m.fieldOfView = new boolean[length()];
						Dungeon.level.updateFieldOfView( m, m.fieldOfView );
					}
					BArray.or(heroMindFov, m.fieldOfView, heroMindFov);
				}
			}

			for (RevealedArea a : c.buffs(RevealedArea.class)){
				if (!Dungeon.depth().equals(a.depth)) continue;
				for (int i : PathFinder.NEIGHBOURS9) heroMindFov[a.pos+i] = true;
			}

			//set mind vision chars
			for (Mob mob : mobs) {
				if (heroMindFov[mob.pos()] && !fieldOfView[mob.pos()]){
					Dungeon.hero.mindVisionEnemies.add(mob);
				}
			}

			BArray.or(heroMindFov, fieldOfView, fieldOfView);

		}

		if (c == Dungeon.hero) {
			for (Heap heap : heaps.valueList())
				if (!heap.seen && fieldOfView[heap.pos])
					heap.seen = true;

			DanceFloor dance = (DanceFloor) blobs.get(DanceFloor.class);
			if (dance != null) {
				dance.updateFov();
			}
		}
	}

	public int distance( int a, int b ) {
		int ax = a % width();
		int ay = a / width();
		int bx = b % width();
		int by = b / width();
		return Math.max( Math.abs( ax - bx ), Math.abs( ay - by ) );
	}

	public boolean adjacent( int a, int b ) {
		return distance( a, b ) == 1;
	}

	//uses pythagorean theorum for true distance, as if there was no movement grid
	public float trueDistance(int a, int b){
		int ax = a % width();
		int ay = a / width();
		int bx = b % width();
		int by = b / width();
		return (float)Math.sqrt(Math.pow(Math.abs( ax - bx ), 2) + Math.pow(Math.abs( ay - by ), 2));
	}

	//returns true if the input is a valid tile within the level
	public boolean insideMap( int tile ){
				//top and bottom row and beyond
		return !((tile < width || tile >= length - width) ||
				//left and right column
				(tile % width == 0 || tile % width == width-1));
	}

	public Point cellToPoint( int cell ){
		return new Point(cell % width(), cell / width());
	}

	public int pointToCell( Point p ){
		return p.x + p.y*width();
	}

	public String tileName( int tile ) {

		switch (tile) {
			case Terrain.CHASM:
				return Messages.get(Level.class, "chasm_name");
			case Terrain.EMPTY:
			case Terrain.EMPTY_SP:
			case Terrain.EMPTY_DECO:
//			case Terrain.SECRET_TRAP:
				return Messages.get(Level.class, "floor_name");
			case Terrain.GRASS:
				return Messages.get(Level.class, "grass_name");
			case Terrain.WATER:
				return Messages.get(Level.class, "water_name");
			case Terrain.WALL:
			case Terrain.WALL_DECO:
			case Terrain.SECRET_DOOR:
				return Messages.get(Level.class, "wall_name");
			case Terrain.DOOR:
				return Messages.get(Level.class, "closed_door_name");
			case Terrain.OPEN_DOOR:
				return Messages.get(Level.class, "open_door_name");
			case Terrain.ENTRANCE:
				return Messages.get(Level.class, "entrace_name");
			case Terrain.EXIT:
				return Messages.get(Level.class, "exit_name");
			case Terrain.EMBERS:
				return Messages.get(Level.class, "embers_name");
			case Terrain.FURROWED_GRASS:
				return Messages.get(Level.class, "furrowed_grass_name");
			case Terrain.LOCKED_DOOR:
				return Messages.get(Level.class, "locked_door_name");
			case Terrain.PEDESTAL:
				return Messages.get(Level.class, "pedestal_name");
			case Terrain.BARRICADE:
				return Messages.get(Level.class, "barricade_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(Level.class, "high_grass_name");
			case Terrain.LOCKED_EXIT:
				return Messages.get(Level.class, "locked_exit_name");
			case Terrain.UNLOCKED_EXIT:
				return Messages.get(Level.class, "unlocked_exit_name");
			case Terrain.SIGN:
				return Messages.get(Level.class, "sign_name");
			case Terrain.WELL:
				return Messages.get(Level.class, "well_name");
			case Terrain.EMPTY_WELL:
				return Messages.get(Level.class, "empty_well_name");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(Level.class, "statue_name");
			case Terrain.BOOKSHELF:
				return Messages.get(Level.class, "bookshelf_name");
			case Terrain.ALCHEMY:
				return Messages.get(Level.class, "alchemy_name");
			default:
				return Messages.get(Level.class, "default_name");
		}
	}

	public String tileDesc( int tile ) {

		switch (tile) {
			case Terrain.CHASM:
				return Messages.get(Level.class, "chasm_desc");
			case Terrain.WATER:
				return Messages.get(Level.class, "water_desc");
			case Terrain.ENTRANCE:
				return Messages.get(Level.class, "entrance_desc");
			case Terrain.EXIT:
			case Terrain.UNLOCKED_EXIT:
				return Messages.get(Level.class, "exit_desc");
			case Terrain.EMBERS:
				return Messages.get(Level.class, "embers_desc");
			case Terrain.HIGH_GRASS:
			case Terrain.FURROWED_GRASS:
				return Messages.get(Level.class, "high_grass_desc");
			case Terrain.LOCKED_DOOR:
				return Messages.get(Level.class, "locked_door_desc");
			case Terrain.LOCKED_EXIT:
				return Messages.get(Level.class, "locked_exit_desc");
			case Terrain.BARRICADE:
				return Messages.get(Level.class, "barricade_desc");
			case Terrain.SIGN:
				return Messages.get(Level.class, "sign_desc");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(Level.class, "statue_desc");
			case Terrain.ALCHEMY:
				return Messages.get(Level.class, "alchemy_desc");
			case Terrain.EMPTY_WELL:
				return Messages.get(Level.class, "empty_well_desc");
			default:
				return "";
		}
	}
}
