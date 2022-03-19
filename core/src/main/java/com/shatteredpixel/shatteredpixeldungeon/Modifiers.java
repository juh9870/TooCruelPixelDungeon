package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.Difficulty;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChallenges;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.noosa.Game;
import com.watabou.noosa.Scene;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Modifiers implements Bundlable {
	private static final String CHALLENGES = "challenges";
	private static final String DYNASTY = "dynasty";
	public boolean[] challenges;
	public String dynastyId;

	public Modifiers() {
		challenges = new boolean[Challenges.values().length];
		dynastyId = "";
		clearChallenges();
	}

	public Modifiers( boolean[] challenges ) {
		this();
		System.arraycopy( challenges, 0, this.challenges, 0, challenges.length );
	}

	public Modifiers( Modifiers old ) {
		challenges = old.challenges.clone();
		dynastyId = old.dynastyId;
	}

	public static Modifiers Empty() {
		return new Modifiers();
	}

	public static ChallengesDifference challengesDifference( Modifiers a, Modifiers b ) {
		ChallengesDifference diff = new ChallengesDifference();
		for (Challenges chal : Challenges.values()) {
			boolean la = a.isChallenged( chal.id );
			boolean lb = b.isChallenged( chal.id );
			if ( la == lb ) continue;
			if ( !lb ) diff.removed.add( chal );
			if ( !la ) diff.added.add( chal );
		}
		return diff;
	}

	public Rankings.Dynasty getDynasty() {
		return Rankings.INSTANCE.getDynasty( dynastyId );
	}

	public Modifiers setDynasty( String dynastyId ) {
		this.dynastyId = dynastyId;
		return this;
	}

	public void clearChallenges() {
		BArray.setFalse( challenges );
	}

	public boolean isChallenged( int id ) {
		return challenges[id];
	}

	public boolean isChallenged() {
		for (boolean challenge : challenges) {
			if ( challenge ) return true;
		}
		return false;
	}

	public boolean isCheesed() {
		for (int i = 0; i < challenges.length; i++) {
			if ( challenges[i] && Challenges.fromId( i ).isCheese() ) return true;
		}
		return false;
	}

	public boolean canEnable( Challenges challenge ) {
		for (int req : challenge.requirements) {
			if ( !isChallenged( req ) ) return false;
		}
		return true;
	}

	public Set<Challenges> requirements( Challenges challenge ) {
		Set<Challenges> set = new HashSet<>();
		for (int requirement : challenge.requirements) {
			set.add( Challenges.fromId( requirement ) );
		}
		return set;
	}

	public Set<Challenges> recursiveRequirements( Challenges challenge ) {
		Set<Challenges> set = requirements( challenge );
		for (Challenges entry : new HashSet<>( set )) {
			set.addAll( recursiveRequirements( entry ) );
		}
		return set;
	}

	public boolean canDisable( Challenges challenge ) {
		for (Challenges value : Challenges.values()) {
			if ( isChallenged( value.id ) && value.requires( challenge ) ) return false;
		}
		return true;
	}

	public Set<Challenges> dependants( Challenges challenge ) {
		Set<Challenges> set = new HashSet<>();
		for (Challenges value : Challenges.values()) {
			if ( value.requires( challenge ) ) set.add( value );
		}
		return set;
	}

	public Set<Challenges> recursiveDependantsFilterEnabled( Challenges challenge, Modifiers modifiers ) {
		Set<Challenges> set = dependants( challenge );
		Set<Challenges> retSet = new HashSet<>();
		for (Challenges entry : new HashSet<>( set )) {
			if ( modifiers.isChallenged( entry.id ) ) {
				retSet.add( entry );
				retSet.addAll( recursiveDependantsFilterEnabled( entry, modifiers ) );
			}
		}
		return retSet;
	}

	public Set<Challenges> recursiveDependants( Challenges challenge ) {
		Set<Challenges> set = dependants( challenge );
		for (Challenges entry : new HashSet<>( set )) {
			set.addAll( recursiveDependants( entry ) );
		}
		return set;
	}

	public void randomize( long seed ) {
		Challenges[] values = Challenges.values();
		Random.pushGenerator( seed );
		Random.shuffle( values );
		float targetDifficulty = Random.Float( Difficulty.NORMAL_1.margin, Difficulty.VERY_HARD_3.margin );
		float maxOvershoot = targetDifficulty / 2f;

		BArray.setFalse( challenges );

		boolean[] oldChals;

		for (Challenges value : values) {
			if ( value.isModifier() ) continue;
			if ( value.deprecated() ) continue;
			oldChals = Arrays.copyOf( challenges, challenges.length );
			challenges[value.id] = true;
			for (Challenges req : recursiveRequirements( value )) {
				challenges[req.id] = true;
			}
			float diff = Difficulty.calculateDifficulty( this );
			if ( diff > targetDifficulty + maxOvershoot ) {
				challenges = oldChals;
			} else if ( diff > targetDifficulty ) {
				break;
			}
		}

		Random.popGenerator();
	}

	public void fromBigIntString( String str ) {
		challenges = Challenges.fromString( new StringBuilder( new BigInteger( str, 36 ).toString( 2 ) ).reverse().toString() );
	}

	public WndChallenges.ChallengePredicate select( int amount, int old ) {
		HashSet<Challenges> selectable = new HashSet<>();
		HashSet<Challenges> canDisable = new HashSet<>();
		for (Challenges value : Challenges.values()) {
			if ( value.isModifier() ) continue;
			if ( value.deprecated() ) continue;
			if ( value.difficulty <= 0 ) continue;
			if ( isChallenged( value.id ) && canDisable( value ) ) canDisable.add( value );
			if ( !isChallenged( value.id ) && canEnable( value ) ) selectable.add( value );
		}

		Random.pushGenerator( Dungeon.seed );

		while (selectable.size() > amount) {
			selectable.remove( Random.element( selectable ) );
		}
		while (canDisable.size() > old) {
			canDisable.remove( Random.element( canDisable ) );
		}
		selectable.addAll( canDisable );

		if ( selectable.size() == 0 ) {
			return null;
		}
		return selectable::contains;
	}

	public Difficulty difficulty() {
		return Difficulty.align( Difficulty.calculateDifficulty( this ) );
	}

	public boolean validateRun( Scene scene ) {
		for (Challenges val : Challenges.values()) {
			if ( isChallenged( val.id ) && val.deprecated() ) {
				scene.add( new WndError( Messages.get( this, "disabled_challenges_message" ) ) {
					@Override
					public void onBackPressed() {
						super.onBackPressed();
						Game.switchScene( StartScene.class );
					}
				} );
				return false;
			}
		}
		return true;
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		challenges = Challenges.fromString( bundle.getString( CHALLENGES ) );
		dynastyId = bundle.contains( DYNASTY ) ? bundle.getString( DYNASTY ) : "";
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( CHALLENGES, Challenges.saveString( challenges ) );
		bundle.put( DYNASTY, dynastyId );
	}

	public static class ChallengesDifference {
		public List<Challenges> removed;
		public List<Challenges> added;

		public ChallengesDifference() {
			this.removed = new ArrayList<>();
			this.added = new ArrayList<>();
		}

		public ChallengesDifference( List<Challenges> removed, List<Challenges> added ) {
			this.removed = removed;
			this.added = added;
		}
	}
}
