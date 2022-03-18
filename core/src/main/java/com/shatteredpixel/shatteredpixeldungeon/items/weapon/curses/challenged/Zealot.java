package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.CurrencyItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.ListUtils;
import com.watabou.utils.Misc;
import com.watabou.utils.Pair;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Zealot extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	private static HeroAction lastAction = null;

	public static void randomAction( Hero hero ) {
		if ( hero.curAction != null ) return;
		if ( hero.buff( ZealotBuff.class ) == null ) return;

		// Continue last action first
		if ( continueLast( hero ) ) return;
		// Attack enemies next
		if ( actEnemy( hero ) ) return;
		// Pick up items next
		if ( tryPickUp( hero ) ) return;
		// Use items next
		if ( actUseItems( hero ) ) return;
		// Explore next
		if ( actExplore( hero ) ) return;


		// Detach buff if nothing to do
		Buff.detach( hero, ZealotBuff.class );
	}

	private static void setAction( Hero hero, HeroAction action ) {
		lastAction = hero.curAction = action;
		hero.lastAction = null;
	}

	private static boolean continueLast( Hero hero ) {
		if ( lastAction instanceof HeroAction.Attack && ((HeroAction.Attack) lastAction).target.isAlive() ) {
			setAction( hero, new HeroAction.Move( ((HeroAction.Attack) lastAction).target.pos() ) );
			lastAction = null;
			return true;
		}
		return false;
	}

	private static boolean actEnemy( Hero hero ) {
		if ( hero.heroClass == HeroClass.HUNTRESS && actBowEnemy( hero ) ) return true;

		boolean canMove = hero.rooted <= 0;
		Set<Char> targets = getValidTargetsInFov( hero,
				( c ) -> !hero.isCharmedBy( c ) &&
						!c.isInvulnerable( hero.getClass() ) &&
						!(c instanceof NPC) &&
						(canMove || hero.canAttack( c )) &&
						// Do not attack enemies that we can't hit
						Char.hit( hero, c, 10 ) &&
						Misc.run( c.buff( BadEnemy.class ), b -> b != null, b -> b.cooldown() < BadEnemy.THRESHOLD, b -> true )
		);
		// If we targeted something but got interrupted, so we continue attacking it
		if ( hero.lastAction instanceof HeroAction.Attack && targets.contains( ((HeroAction.Attack) hero.lastAction).target ) ) {
			hero.resume();
			return true;
		}
		int len = Dungeon.level.length();
		boolean[] p = Dungeon.level.passable;
		boolean[] v = Dungeon.level.visited;
		boolean[] m = Dungeon.level.mapped;
		boolean[] passable = new boolean[len];
		for (int i = 0; i < len; i++) {
			passable[i] = p[i] && (v[i] || m[i]);
		}

		List<Pair<Char, Float>> sortedEnemies = new ArrayList<>();
		for (Char target : targets) {
			int priority = 0;
			// Prioritize enemies we can attack without moving
			if ( hero.canAttack( target ) ) {
				priority += 5;
				// Additionally adjacent enemies
				if ( target.distance( hero ) <= 1 ) priority += 5;
			}
			sortedEnemies.add( new Pair<>( target, Dungeon.level.trueDistance( target.pos(), hero.pos() ) - priority ) );
		}
		ListUtils.sortF( sortedEnemies, ( pair ) -> pair.second );
		if ( !sortedEnemies.isEmpty() ) {
			while (!sortedEnemies.isEmpty()) {
				Char target = sortedEnemies.get( 0 ).first;
				sortedEnemies.remove( 0 );
				PathFinder.Path path = Dungeon.findPath( hero, target.pos(), passable, hero.fieldOfView, true );
				if ( path != null && path.size() <= Dungeon.level.distance( hero.pos(), target.pos() ) * 1.5f ) {
					setAction( hero, new HeroAction.Attack( target ) );
					Buff.affect( target, BadEnemy.class, 1f );
					return true;
				}
			}
		}
		return false;
	}

	private static boolean actBowEnemy( Hero hero ) {
		SpiritBow bow = hero.belongings.getItem( SpiritBow.class );
		if ( bow == null ) return false;
		SpiritBow.SpiritArrow arrow = bow.knockArrow();

		boolean projecting = arrow.hasEffectiveEnchant( Projecting.class, hero );
		Set<Char> targets = getValidTargetsInFov( hero,
				( ch ) -> arrow.targetValid( hero, ch.pos(), projecting ) &&
						!ch.isInvulnerable( hero.getClass() ) &&
						!(ch instanceof NPC) &&
						// Do not attack enemies that we can't hit
						Char.hit( hero, ch, 10 ) &&
						Misc.run( ch.buff( BadEnemy.class ), b -> b != null, b -> b.cooldown() < BadEnemy.THRESHOLD, b -> true )
		);
		if ( targets.isEmpty() ) return false;

		// Don't use bow in melee
		for (Char target : targets) {
			if ( target.distance( hero ) <= 1 && hero.canAttack( target ) && hero.belongings.weapon != null ) {
				return false;
			}
		}
		List<Char> sorted = new ArrayList<>( targets );
		ListUtils.sortF( sorted, ( ch ) -> Dungeon.level.trueDistance( ch.pos(), hero.pos() ) );

		hero.waitUntilNext = true;
		arrow.cast( hero, sorted.get( 0 ).pos() );
		Buff.affect( sorted.get( 0 ), BadEnemy.class, 1f );
		setAction( hero, new HeroAction.Nothing() );
		return true;
	}

	private static boolean actExplore( Hero hero ) {
		if ( hero.rooted > 0 ) return false;

		List<Pair<Integer, Integer>> interestingCells = new ArrayList<>();

		int len = Dungeon.level.length();
		boolean[] p = Dungeon.level.passable;
		boolean[] v = Dungeon.level.visited;
		boolean[] m = Dungeon.level.mapped;
		boolean[] passable = new boolean[len];
		for (int i = 0; i < len; i++) {
			passable[i] = p[i] && (v[i] || m[i]);

			int priority = 0;
			if ( passable[i] ) {

				// Move at most 1 tile to visit mapped but not visited cells
				if ( !v[i] && m[i] ) {
					priority = 1;
				}

				// Move 1 additional tile to visit unexplored not mapped cells nearby
				if ( v[i] && !m[i] ) {
					for (int o : PathFinder.NEIGHBOURS8) {
						int pos = i + o;
						if ( p[pos] && !v[pos] && !m[pos] ) {
							priority = 1;
							break;
						}
					}
				}

				// Pick up keys
				Heap h = Dungeon.level.heaps.get( i );
				if ( h != null && (heapInteresting( hero, h ) || (h.type.needUnlock && h.canUnlock())) ) {
					priority = 1;
				}

				// Exit last
				if ( i == Dungeon.level.exit )
					priority = -10000;

			}

			// Unlock locked doors if we have a key
			if ( Dungeon.level.map[i] == Terrain.LOCKED_DOOR && Notes.keyCount( new IronKey( Dungeon.depth() ) ) > 0 ) {
				priority = 1;
				passable[i] = true;
			}

			if ( priority != 0 ) {
				interestingCells.add( new Pair<>( i, priority ) );
			}
		}
		PathFinder.buildDistanceMap( Dungeon.hero.pos(), passable );
		interestingCells = ListUtils.map( interestingCells, ( pair ) -> new Pair<>( pair.first, PathFinder.distance[pair.first] - pair.second ) );
		ListUtils.sort( interestingCells, ( pair ) -> pair.second );

		PathFinder.Path path;
		int movePos;
		do {
			if ( !interestingCells.isEmpty() ) {
				movePos = interestingCells.get( 0 ).first;
				interestingCells.remove( 0 );
				if ( movePos == hero.pos() ) break;
			} else {
				// No interesting places to go
				return false;
			}
			path = Dungeon.findPath( hero, movePos, passable, hero.fieldOfView, true );
		} while (path == null || path.size() < 1);
		hero.clearPath();
		Heap h = Dungeon.level.heaps.get( movePos );
		if ( movePos == Dungeon.level.exit ) {
			setAction( hero, new HeroAction.Descend( movePos ) );
		} else if ( Dungeon.level.map[movePos] == Terrain.LOCKED_DOOR ) {
			setAction( hero, new HeroAction.Unlock( movePos ) );
		} else if ( h != null && canPickUp( hero, h ) ) {
			setAction( hero, new HeroAction.PickUp( movePos ) );
		} else if ( h != null && h.type.needUnlock && h.canUnlock() && h.type != Heap.Type.CRYSTAL_CHEST ) {
			setAction( hero, new HeroAction.OpenChest( movePos ) );
		} else {
			setAction( hero, new HeroAction.Move( movePos ) );
		}
		return true;
	}

	private static boolean actUseItems( Hero hero ) {
		if ( hero.buff( Hunger.class ).isStarving() ) {
			List<Food> foods = hero.belongings.getAllItems( Food.class );
			if ( !foods.isEmpty() ) {
				// Eat high-satiety food first
				ListUtils.sortF( foods, f -> -f.energy );
				Food food = foods.get( 0 );
				hero.waitUntilNext = true;
				food.execute( hero, Food.AC_EAT );
				return true;
			}
		}
		return false;
	}

	private static boolean tryPickUp( Hero hero ) {
		Heap h = Dungeon.level.heaps.get( hero.pos() );
		if ( h == null ) return false;
		if ( h.peek() instanceof Key ) {
			setAction( hero, new HeroAction.PickUp( hero.pos() ) );
			return true;
		}
		boolean interesting = heapInteresting( hero, h );
		if ( !interesting ) return false;
		int n;
		do {
			do {
				n = hero.pos() + PathFinder.NEIGHBOURS8[Random.Int( PathFinder.NEIGHBOURS8.length )];
			} while (!Dungeon.level.passable[n] && !Dungeon.level.avoid[n]);
			Dungeon.level.drop( h.pickUp(), n ).sprite.drop( hero.pos() );
		} while (!h.isEmpty() && !canPickUp( hero, h ));
		return tryPickUp( hero );
	}

	private static boolean heapInteresting( Hero hero, Heap heap ) {
		if ( !heapValid( hero, heap ) ) return false;
		if ( !heap.canUnlock() ) return false;
		List<Bag> bags = hero.belongings.getBags();
		ListUtils.filter( bags, ( b ) -> b.getClass() != Bag.class );
		return heap.get( i -> itemValid( hero, bags, i ) ) != null;
	}

	private static boolean canPickUp( Hero hero, Heap heap ) {
		if ( !heapValid( hero, heap ) ) return false;
		List<Bag> bags = hero.belongings.getBags();
		ListUtils.filter( bags, ( b ) -> b.getClass() != Bag.class );
		return itemValid( hero, bags, heap.peek() );
	}

	private static boolean heapValid( Hero hero, Heap heap ) {
		// Don't buy items
		if ( heap.type == Heap.Type.FOR_SALE ) return false;
		return !heap.type.needUnlock;
	}

	private static boolean itemValid( Hero hero, List<Bag> bags, Item item ) {
		return item instanceof Key ||
				item instanceof CurrencyItem ||
				ListUtils.any( bags, b -> b.canHold( item ) ) ||
				(item instanceof Food && hero.belongings.backpack.canHold( item ));
	}

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		Buff.affect( defender, ZealotTracker.class ).attacker = attacker;
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

	public static class ZealotBuff extends FlavourBuff {

		// Not saving duration, because it's only visual, and hero shouldn't be able to exit the game while it's active anyways
		float maxDuration = -1;

		{
			type = buffType.NEGATIVE;
		}

		public static float duration() {
			return Random.NormalIntRange( 800, 1500 );
		}

		@Override
		public boolean attachTo( Char target ) {
			if ( super.attachTo( target ) ) {
				if ( target instanceof Hero ) {
					((Hero) target).outOfControl++;
				}
				return true;
			}
			return false;
		}

		@Override
		public void detach() {
			super.detach();
			if ( target instanceof Hero ) {
				((Hero) target).outOfControl--;
			}
		}

		public void setDuration() {
			postpone( maxDuration = duration() );
		}

		@Override
		public boolean act() {
			if ( target instanceof Hero ) {
				((Hero) target).interrupt();
			}
			return super.act();
		}

		@Override
		public int icon() {
			return BuffIndicator.RAGE;
		}

		@Override
		public void tintIcon( Image icon ) {
			icon.hardlight( 0xFFFF44 );
		}

		@Override
		public float iconFadePercent() {
			if ( maxDuration <= 0 ) return super.iconFadePercent();
			return Math.max( 0, (maxDuration - visualcooldown()) / maxDuration );
		}

		@Override
		public String toString() {
			return Messages.get( this, "name" );
		}

		@Override
		public String desc() {
			return Messages.get( this, "desc", dispTurns() );
		}
	}

	public static class ZealotTracker extends FlavourBuff {

		// Not saving attacker, because buff is supposed to be 0-duration
		public Char attacker;

		@Override
		public void onDeathProc( Object src, boolean fakeDeath ) {
			if ( attacker.buff( ZealotBuff.class ) == null && attacker.buff( StoneOfAggression.Aggression.class ) == null )
				CellEmitter.center( attacker.pos() ).start( Speck.factory( Speck.SCREAM ), 0.3f, 1 );
			if ( attacker instanceof Hero ) {
				Buff.affect( attacker, ZealotBuff.class ).setDuration();
			} else {
				Buff.prolong( attacker, StoneOfAggression.Aggression.class, ZealotBuff.duration() );
			}
			detach();
		}
	}

	public static class BadEnemy extends FlavourBuff {
		public static final float THRESHOLD = 20f;
	}
}