package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.Set;

public class Zealot extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public boolean curse() {
		return true;
	}

	public static void randomAction( Hero hero ) {
		if ( hero.curAction != null ) return;
		if ( hero.buff( ZealotBuff.class ) == null ) return;


		Set<Char> targets = getValidTargetsInFov( hero, ( c ) -> !hero.isCharmedBy( c ) );
		// If we targeted something but got interrupted, so we continue attacking it
		if ( hero.lastAction instanceof HeroAction.Attack && targets.contains( ((HeroAction.Attack) hero.lastAction).target ) ) {
			hero.resume();
			return;
		}
		int len = Dungeon.level.length();
		boolean[] p = Dungeon.level.passable;
		boolean[] v = Dungeon.level.visited;
		boolean[] m = Dungeon.level.mapped;
		boolean[] passable = new boolean[len];
		for (int i = 0; i < len; i++) {
			passable[i] = p[i] && (v[i] || m[i]);
		}
		if ( !targets.isEmpty() ) {
			while (!targets.isEmpty()) {
				Char target = Random.element( targets );
				targets.remove( target );
				PathFinder.Path path = Dungeon.findPath( hero, target.pos(), passable, hero.fieldOfView, true );
				if ( path != null && path.size() <= Dungeon.level.distance( hero.pos(), target.pos() ) * 1.5f ) {
					hero.curAction = new HeroAction.Attack( target );
					return;
				}
			}
		}


		if ( Random.Boolean() ) {
			hero.spend( Actor.TICK );
			hero.curAction = new HeroAction.Nothing();
		}

		PathFinder.Path path;
		do {
			int movePos = Dungeon.level.randomDestination( hero );
			path = Dungeon.findPath( hero, movePos, passable, hero.fieldOfView, true );
		} while (path == null || path.size() < 1);
		hero.curAction = new HeroAction.Move( Dungeon.level.randomDestination( hero ) );
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

		{
			type = buffType.NEGATIVE;
		}

		// Not saving duration, because it's only visual, and hero shouldn't be able to exit the game while it's active anyways
		float maxDuration = -1;

		public static float duration() {
			return Random.NormalIntRange( 8, 15 );
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

		// Not saving id, because buff is supposed to be 0-duration
		public Char attacker;

		@Override
		public void onDeathProc( Object src, boolean fakeDeath ) {
			if ( attacker instanceof Hero ) {
				Buff.affect( attacker, ZealotBuff.class ).setDuration();
			} else {
				Buff.prolong( attacker, StoneOfAggression.Aggression.class, ZealotBuff.duration() );
			}
			CellEmitter.center( attacker.pos() ).start( Speck.factory( Speck.SCREAM ), 0.3f, 1 );
			detach();
		}
	}
}