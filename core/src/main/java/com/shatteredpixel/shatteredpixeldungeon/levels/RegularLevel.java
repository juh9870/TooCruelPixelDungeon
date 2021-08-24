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

import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Extermination;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stacking;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldenMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SmallRation;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.GuidePage;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.FigureEightBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LineBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LoopBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.RegularBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.TunnelRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.BlackjackRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public abstract class RegularLevel extends Level {
	
	protected ArrayList<Room> rooms;
	
	protected Builder builder;
	
	protected Room roomEntrance;
	protected Room roomExit;
    protected float nRooms = -1;

    protected static ArrayList<Room> essentialRooms = new ArrayList<>();

	@Override
	protected boolean build() {

		builder = builder();

		if (builder instanceof RegularBuilder) {
			if (Challenges.LINEAR.enabled()) {
				((RegularBuilder) builder).setPathLength(1, new float[]{1});
				((RegularBuilder) builder).setPathVariance(0);
			}
			if (Challenges.BIGGER_LEVELS.enabled()) {
				((RegularBuilder) builder).setExtraConnectionChance(1f);
			}
		}
		ArrayList<Room> initRooms = initRooms();
		initRooms.addAll(essentialRooms);
		essentialRooms.clear();
		boolean painted = false;
		float toRemove = 0;
		do {
			Random.shuffle(initRooms);
			do {
				for (Room r : initRooms) {
					r.neigbours.clear();
					r.connected.clear();
				}
				rooms = builder.build((ArrayList<Room>) initRooms.clone());
			} while (rooms == null);

			painted = painter().paint(this, rooms);
			if (painted) break;
			if (length > Level.SIZE_LIMIT) {
				float mult = Challenges.roomSizeMult();
				toRemove += 0.3334 * mult;
				for (int i = 0; i < initRooms.size() && toRemove >= 1; i++) {
					Room r = initRooms.get(i);
					if (r instanceof StandardRoom && !r.important && !r.preserve) {
						initRooms.remove(i);
						i--;
						toRemove--;
						nRooms--;
					}
				}
				// We are out of rooms to remove but level is still too large
				if (toRemove >= 1) {
					break;
				}
			} else {
				break;
			}
		} while (true);

		if (!painted) {
			for (int i = 0; i < initRooms.size(); i++) {
				Room r = initRooms.get(i);
				if (r.preserve) {
					essentialRooms.add(r);
				}
			}
		}
		return painted;
	}
		
    protected HashSet<Room> emptyRooms(){
        HashSet<Room> empty = new HashSet<>();
        for (Room room : rooms) {
            if(room.isEmpty())empty.add(room);
        }
        return empty;
	}

	public float enlargedFactor(){
		int plannedRooms = standardRooms(feeling==Feeling.LARGE);
		// Entrance and exit
		plannedRooms += 2;

		int actual = 0;
		for (int i = 0; i < rooms.size(); i++) {
			if(rooms.get(i) instanceof StandardRoom) actual++;
		}

		return (float) actual / plannedRooms;
	}
	
	protected ArrayList<Room> initRooms() {
		ArrayList<Room> initRooms = new ArrayList<>();
		initRooms.add ( roomEntrance = new EntranceRoom());
		initRooms.add( roomExit = new ExitRoom());

		//force max standard rooms and multiple by 1.5x for large levels
		int standards = standardRooms(feeling == Feeling.LARGE);
		if (feeling == Feeling.LARGE){
			standards = (int)Math.ceil(standards * 1.5f);
		}
        standards *= Challenges.nRoomsMult();
        if(nRooms>0){
            standards=(int)Math.floor(nRooms);
        } else {
            nRooms=standards;
        }
		for (int i = 0; i < standards; i++) {
			StandardRoom s;
			do {
				s = StandardRoom.createRoom();
			} while (!s.setSizeCat( standards-i ));
			i += s.sizeCat.roomValue-1;
			initRooms.add(s);
		}
		
		if (Dungeon.shopOnLevel())
			initRooms.add(new ShopRoom());

        if (Challenges.BLACKJACK.enabled() && !Dungeon.bossLevel() && Dungeon.depth != 21) {
            initRooms.add(new BlackjackRoom());
        }
		//force max special rooms and add one more for large levels
		int specials = specialRooms(feeling == Feeling.LARGE);
        if (Challenges.SMALL_LEVELS.enabled()) specials /= 2;
		if (feeling == Feeling.LARGE){
			specials++;
		}
		if (Challenges.GRINDING.enabled()) specials *= Challenges.nRoomsMult();
		SpecialRoom.initForFloor();
		for (int i = 0; i < specials; i++) {
			SpecialRoom s = SpecialRoom.createRoom();
			if (s instanceof PitRoom) specials++;
			initRooms.add(s);
		}
		
		int secrets = SecretRoom.secretsForFloor(Dungeon.depth);
		//one additional secret for secret levels
		if (feeling == Feeling.SECRETS) secrets++;
		for (int i = 0; i < secrets; i++) {
			initRooms.add(SecretRoom.createRoom());
		}
		
		return initRooms;
	}
	
	protected int standardRooms(boolean forceMax){
		return 0;
	}
	
	protected int specialRooms(boolean forceMax){
		return 0;
	}
	
	protected Builder builder(){
        if (Challenges.LINEAR.enabled())
            return new LineBuilder();
		if (Random.Int(2) == 0){
			return new LoopBuilder()
					.setLoopShape( 2 ,
							Random.Float(0f, 0.65f),
							Random.Float(0f, 0.50f));
		} else {
			return new FigureEightBuilder()
					.setLoopShape( 2 ,
							Random.Float(0.3f, 0.8f),
							0f);
		}

	}
	
	protected abstract Painter painter();
	
	protected int nTraps() {
		if(Challenges.TRAP_TESTING_FACILITY.enabled()){
			return (int) ((3 + (Dungeon.depth / 3)) * Challenges.nTrapsMultiplier());
		} else if (Challenges.EXTREME_DANGER.enabled()) {
            return (int) (Random.NormalIntRange(1 + (Dungeon.depth / 6), 3 + (Dungeon.depth / 3)) * Challenges.nTrapsMultiplier());
        } else
            return (int) (Random.NormalIntRange(2, 3 + (Dungeon.depth / 5)) * Challenges.nTrapsMultiplier());
	}
	
	protected Class<?>[] trapClasses(){
		return new Class<?>[]{WornDartTrap.class};
	}

	protected float[] trapChances() {
		return new float[]{1};
	}
	
	@Override
	public int nMobs() {
        if (Dungeon.depth <= 1) return Challenges.RESURRECTION.enabled() ? (int) (8 * Challenges.nMobsMultiplier()) : 0;

		int mobs = 3 + Dungeon.depth % 5 + Random.Int(3);
		if (feeling == Feeling.LARGE){
			mobs = (int)Math.ceil(mobs * 1.33f);
		}
        mobs *= (int) Math.ceil(Challenges.nMobsMultiplier());
		return mobs;
	}

	private boolean tryCreateMobInRoom(Mob mob, Room roomToSpawn){
		int tries = 30;
		do {
			mob.pos = pointToCell(roomToSpawn.random());
			tries--;
		} while (tries >= 0 && (!passable[mob.pos] || solid[mob.pos] || mob.pos == exit
				|| (!openSpace[mob.pos] && mob.properties().contains(Char.Property.LARGE)) || findMob(mob.pos) != null));

		if (tries < 0) {
			return false;
		}
		if (Challenges.EXTERMINATION.enabled()) {
			Buff.affect(mob, Extermination.class);
		}
		mobs.add(mob);
		return true;
	}
	
	@Override
	protected void createMobs() {
		//on floor 1, 8 pre-set mobs are created so the player can get level 2.
        int mobsToSpawn = Dungeon.depth == 1 ? (int) (8 * Challenges.nMobsMultiplier()) : nMobs();

        boolean allowEntrance = Challenges.EXHIBITIONISM.enabled();
        boolean allowCorridors = Challenges.isTooManyMobs();
		ArrayList<Room> stdRooms = new ArrayList<>();
		for (Room room : rooms) {
            if (room instanceof StandardRoom && (room != roomEntrance || allowEntrance)) {
				for (int i = 0; i < ((StandardRoom) room).sizeCat.roomValue; i++) {
					stdRooms.add(room);
				}
            } else if(allowCorridors && room instanceof TunnelRoom){
                stdRooms.add(room);
			}
		}
		Random.shuffle(stdRooms);
		Iterator<Room> stdRoomIter = stdRooms.iterator();

		boolean stacking = Challenges.STACKING.enabled();

		if(Challenges.INFINITY_MOBS.enabled() && stacking){
			while (stdRoomIter.hasNext()) {
				Room r = stdRoomIter.next();
				int count = 0;
				for (int i = r.left; i < r.right; i++) {
					for (int j = r.top; j < r.bottom; j++) {
						int c = pointToCell(new Point(i, j));
						if (passable[c] && !solid[c] && c != exit && c != entrance) {
							count++;
						}
					}
				}
				Mob m = createMob();
				tryCreateMobInRoom(m, r);
				Buff.affect(m, Stacking.class).count = count;
			}
		} else if (!Challenges.isTooManyMobs() || stacking) {
			HashMap<Room, Stacking> roomMobMap = new HashMap<>();
			while (mobsToSpawn > 0) {
				Room roomToSpawn;

				if (!stdRoomIter.hasNext()) {
					stdRoomIter = stdRooms.iterator();
				}
				roomToSpawn = stdRoomIter.next();

				if (stacking && roomMobMap.containsKey(roomToSpawn)) {
					Stacking stack = roomMobMap.get(roomToSpawn);
					stack.count++;
					mobsToSpawn--;
					if (Random.Int(4) == 0) {
						stack.count++;
						mobsToSpawn--;
					}
				} else {
					Mob mob = createMob();
					if (tryCreateMobInRoom(mob, roomToSpawn)) {
						mobsToSpawn--;
						if(stacking){
							roomMobMap.put(roomToSpawn, Buff.affect(mob, Stacking.class));
						}
						if (mobsToSpawn > 0 && Random.Int(4) == 0) {
							mob = createMob();
							if (tryCreateMobInRoom(mob, roomToSpawn)) {
								mobsToSpawn--;
							}
						}
					}
				}
			}
		} else {
			HashSet<Integer> cells = new HashSet<>();
			HashSet<Integer> largeCells = new HashSet<>();
			while (stdRoomIter.hasNext()) {
				Room r = stdRoomIter.next();
				for (int i = r.left; i < r.right; i++) {
					for (int j = r.top; j < r.bottom; j++) {
						int c = pointToCell(new Point(i, j));
						if (passable[c] && !solid[c] && c != exit && c != entrance) {
							cells.add(c);
							if (openSpace[c]) largeCells.add(c);
						}
					}
				}
			}
			while (mobsToSpawn > 0) {
				Mob mob = createMob();
				if (cells.size() <= 0) break;
				HashSet<Integer> set = mob.properties().contains(Char.Property.LARGE) ? largeCells : cells;

				mobsToSpawn--;
				if (set.size() > 0) {
					int cell = Random.element(set);
					mob.pos = cell;
					cells.remove(cell);
					largeCells.remove(cell);
					if (Challenges.EXTERMINATION.enabled()) {
						Buff.affect(mob, Extermination.class);
					}
					mobs.add(mob);
				}
			}
		}

		for (Mob m : mobs){
			if (map[m.pos] == Terrain.HIGH_GRASS || map[m.pos] == Terrain.FURROWED_GRASS) {
				map[m.pos] = Terrain.GRASS;
				losBlocking[m.pos] = false;
			}

		}

	}

	@Override
	public int randomRespawnCell(Char ch, boolean ignoreMobs) {
		int count = 0;
		int cell = -1;
		boolean allowHeroFov = Challenges.EXHIBITIONISM.enabled();
		boolean mindVision = Dungeon.hero.buff(MindVision.class) != null;
		boolean allowStacking = ignoreMobs || Challenges.STACKING.enabled();

		while (true) {

			if (++count > 30) {
				return -1;
			}

            Room room = allowHeroFov ? randomBiasedRoom(StandardRoom.class) : randomRoom(StandardRoom.class);
            if (room == null || (room == roomEntrance && !allowHeroFov)) {
				continue;
			}

			cell = pointToCell(room.random(1));
            if ((!heroFOV[cell] || allowHeroFov || mindVision)
					&& (allowStacking || Actor.findChar( cell ) == null)
					&& passable[cell]
					&& !solid[cell]
					&& (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])
					&& room.canPlaceCharacter(cellToPoint(cell), this)
					&& cell != exit) {
				return cell;
			}

		}
	}
	
	@Override
	public int randomDestination( Char ch ) {
		
		int count = 0;
		int cell = -1;
		
		while (true) {
			
			if (++count > 30) {
				return -1;
			}
			
			Room room = Random.element( rooms );
			if (room == null) {
				continue;
			}
			
			cell = pointToCell(room.random());
			if (passable[cell] && (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])) {
				return cell;
			}
			
		}
	}
	
	@Override
	protected void createItems() {
		
		// drops 3/4/5 items 60%/30%/10% of the time
		int nItems = 3 + Random.chances(new float[]{6, 3, 1});

		if (feeling == Feeling.LARGE){
			nItems += 2;
		}
		
        nItems *= Challenges.nLootMultiplier();
		for (int i=0; i < nItems; i++) {

			Item toDrop = Generator.random();
			if (toDrop == null) continue;

			int cell = randomDropCell();
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}

			Heap.Type type = null;
			switch (Random.Int( 20 )) {
			case 0:
				type = Heap.Type.SKELETON;
				break;
			case 1:
			case 2:
			case 3:
			case 4:
				type = Heap.Type.CHEST;
				break;
			case 5:
				if (Dungeon.depth > 1 && findMob(cell) == null){
					mobs.add(Mimic.spawnAt(cell, toDrop));
					continue;
				}
				type = Heap.Type.CHEST;
				break;
			default:
				type = Heap.Type.HEAP;
				break;
			}

			if ((toDrop instanceof Artifact && Random.Int(2) == 0) ||
					(toDrop.isUpgradable() && Random.Int(4 - toDrop.level()) == 0)){

				if (Dungeon.depth > 1 && Random.Int(10) == 0 && findMob(cell) == null){
					mobs.add(Mimic.spawnAt(cell, toDrop, GoldenMimic.class));
				} else {
					Heap dropped = drop(toDrop, cell);
					if (heaps.get(cell) == dropped) {
						dropped.type = Heap.Type.LOCKED_CHEST;
						addItemToSpawn(new GoldenKey(Dungeon.depth));
					}
				}
			} else {
				Heap dropped = drop( toDrop, cell );
				dropped.type = type;
				if (type == Heap.Type.SKELETON){
					dropped.setHauntedIfCursed();
				}
			}
			
		}

		for (Item item : itemsToSpawn) {
			int cell = randomDropCell();
			drop( item, cell ).type = Heap.Type.HEAP;
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}
		}

		//use a separate generator for this to prevent held items, meta progress, and talents from affecting levelgen
		Random.pushGenerator( Dungeon.seedCurDepth() );

		Item item = Bones.get();
		if (item != null) {
			int cell = randomDropCell();
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}
			drop( item, cell ).setHauntedIfCursed().type = Heap.Type.REMAINS;
		}

		DriedRose rose = Dungeon.hero.belongings.getItem( DriedRose.class );
		if (rose != null && rose.isIdentified() && !rose.cursed){
			//aim to drop 1 petal every 2 floors
			int petalsNeeded = (int) Math.ceil((float)((Dungeon.depth / 2) - rose.droppedPetals) / 3);

			for (int i=1; i <= petalsNeeded; i++) {
				//the player may miss a single petal and still max their rose.
				if (rose.droppedPetals < 11) {
					item = new DriedRose.Petal();
					int cell = randomDropCell();
					drop( item, cell ).type = Heap.Type.HEAP;
					if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
						map[cell] = Terrain.GRASS;
						losBlocking[cell] = false;
					}
					rose.droppedPetals++;
				}
			}
		}

		//cached rations try to drop in a special room on floors 2/3/4/6/7/8, to a max of 4/6
		if (Dungeon.hero.hasTalent(Talent.CACHED_RATIONS)){
			Talent.CachedRationsDropped dropped = Buff.affect(Dungeon.hero, Talent.CachedRationsDropped.class);
			if (dropped.count() < 2 + 2*Dungeon.hero.pointsInTalent(Talent.CACHED_RATIONS)){
				int cell;
				int tries = 100;
				do {
					cell = randomDropCell(SpecialRoom.class);
				} while (tries-- > 0 && (room(cell) instanceof SecretRoom || room(cell) instanceof ShopRoom));
				if (!(room(cell) instanceof SecretRoom || room(cell) instanceof ShopRoom) && cell != -1) {
					if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
						map[cell] = Terrain.GRASS;
						losBlocking[cell] = false;
					}
					drop(new SmallRation(), cell).type = Heap.Type.CHEST;
					dropped.countUp(1);
				}
			}
		}

		//guide pages
		Collection<String> allPages = Document.ADVENTURERS_GUIDE.pageNames();
		ArrayList<String> missingPages = new ArrayList<>();
		for ( String page : allPages){
			if (!Document.ADVENTURERS_GUIDE.isPageFound(page)){
				missingPages.add(page);
			}
		}

		//a total of 6 pages drop randomly, the rest are specially dropped or are given at the start
		missingPages.remove(Document.GUIDE_SEARCHING);

		//chance to find a page is 0/25/50/75/100% for floors 1/2/3/4/5+
		float dropChance = 0.25f*(Dungeon.depth-1);
		if (!missingPages.isEmpty() && Random.Float() < dropChance){
			GuidePage p = new GuidePage();
			p.page(missingPages.get(0));
			int cell = randomDropCell();
			if (map[cell] == Terrain.HIGH_GRASS || map[cell] == Terrain.FURROWED_GRASS) {
				map[cell] = Terrain.GRASS;
				losBlocking[cell] = false;
			}
			drop( p, cell );
		}

		Random.popGenerator();

	}
	
	public ArrayList<Room> rooms() {
		return new ArrayList<>(rooms);
	}
	
    @Override
    protected BlackjackRoom getBlackjackRoom() {
        for (Room r : rooms) {
            if (r instanceof BlackjackRoom) return (BlackjackRoom) r;
        }
        return null;
    }
	//FIXME pit rooms shouldn't be problematic enough to warrant this
	public boolean hasPitRoom(){
		for (Room r : rooms) {
			if (r instanceof PitRoom) {
				return true;
			}
		}
		return false;
	}
	
	protected Room randomRoom( Class<?extends Room> type ) {
		Random.shuffle( rooms );
		for (Room r : rooms) {
			if (type.isInstance(r)) {
				return r;
			}
		}
		return null;
	}
	
    protected Room randomBiasedRoom(Class<? extends Room> type) {
        ArrayList<Room> rooms = new ArrayList<>(this.rooms);
        Room playerRoom = room(Dungeon.hero.pos);
        for (int i = 0; i < 2; i++) {
            rooms.add(playerRoom);
        }
        Random.shuffle(rooms);
        for (Room r : rooms) {
            if (type.isInstance(r) || r == playerRoom) {
                return r;
            }
        }
        return null;
    }
	public Room room( int pos ) {
		for (Room room : rooms) {
			if (room.inside( cellToPoint(pos) )) {
				return room;
			}
		}
		
		return null;
	}

	protected int randomDropCell(){
		return randomDropCell(StandardRoom.class);
	}
	
	protected int randomDropCell( Class<?extends Room> roomType ) {
		int tries = 100;
		while (tries-- > 0) {
			Room room = randomRoom( roomType );
			if (room == null){
				return -1;
			}
			if (room != roomEntrance) {
				int pos = pointToCell(room.random());
                boolean allowMobs = Challenges.isTooManyMobs();
				if (passable[pos] && !solid[pos]
						&& pos != exit
						&& heaps.get(pos) == null
                        && (allowMobs || findMob(pos) == null)) {
					
					Trap t = traps.get(pos);
					
					//items cannot spawn on traps which destroy items
					if (t == null ||
							! (t instanceof BurningTrap || t instanceof BlazingTrap
							|| t instanceof ChillingTrap || t instanceof FrostTrap
							|| t instanceof ExplosiveTrap || t instanceof DisintegrationTrap)) {
						
						return pos;
					}
				}
			}
		}
		return -1;
	}
	
	@Override
	public int fallCell( boolean fallIntoPit ) {
		if (fallIntoPit) {
			for (Room room : rooms) {
				if (room instanceof PitRoom) {
					int result;
					do {
						result = pointToCell(room.random());
					} while (traps.get(result) != null
							|| findMob(result) != null
							|| heaps.get(result) != null);
					return result;
				}
			}
		}
		if(Challenges.isTooManyMobs()){
			HashSet<Integer> cells = new HashSet<>();
			for (Room r : rooms) {
				if(r instanceof SpecialRoom) continue;
				for (int i = r.left; i < r.right; i++) {
					for (int j = r.top; j < r.bottom; j++) {
						int c = pointToCell(new Point(i, j));
						if (passable[c] && !solid[c]) {
							cells.add(c);
						}
					}
				}
			}
			for (Mob mob : mobs) {
				cells.remove(mob.pos);
			}
			return Random.element(cells);
		}
		return super.fallCell( false );
	}
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( "rooms", rooms );
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		
		rooms = new ArrayList<>( (Collection<Room>) ((Collection<?>) bundle.getCollection( "rooms" )) );
		for (Room r : rooms) {
			r.onLevelLoad( this );
			if (r instanceof EntranceRoom ){
				roomEntrance = r;
			} else if (r instanceof ExitRoom ){
				roomExit = r;
			}
		}
	}
	
}
