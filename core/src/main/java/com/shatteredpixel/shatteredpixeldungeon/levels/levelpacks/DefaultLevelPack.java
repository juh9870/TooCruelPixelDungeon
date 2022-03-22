package com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.List;


public class DefaultLevelPack extends LinearLevelPack<Marker.Custom> {
	@Deprecated
	public static DefaultLevelPack fromLegacyData( int depth ) {
		DefaultLevelPack pack = new DefaultLevelPack();
		pack.curLvl = Marker.Custom.fromLegacy( depth );
		return pack;
	}

	@Deprecated
	public static Marker.Custom markerFromLegacyDepth( int depth ) {
		return Marker.Custom.fromLegacy( depth );
	}

	@Deprecated
	public static Marker getOrLoadFromDepth( Bundle bundle, String oldKey, String newKey ) {
		if ( bundle.contains( newKey ) ) {
			return (Marker) bundle.get( newKey );
		}
		return markerFromLegacyDepth( bundle.getInt( oldKey ) );
	}

	private Integer levelPackHash() {
		int hash = 0;
		if ( Challenges.HEADSTART.enabled() ) {
			hash += 0b0001;
		}
		if ( Challenges.WARM_WELCOME.enabled() ) {
			hash += 0b0010;
		}
		return hash;
	}

	@Override
	protected List<Marker.Custom> build() {
		int length = 5;
		LinearLevelPackBuilder builder = new LinearLevelPackBuilder( levelPackHash() )
				.chapterWithLevels( Chapter.SEWERS, length )
				.chapterWithLevels( Chapter.PRISON, length )
				.chapterWithLevels( Chapter.CAVES, length )
				.chapterWithLevels( Chapter.CITY, length )
				.chapterWithLevels( Chapter.HALLS, length )
				.amuletLevel();

		builder.forEach( ( m ) -> {
			if ( m.chapter() != Chapter.SEWERS && m.chapter() != Chapter.HALLS && m.chapterProgression() == 1 ) {
				m.setShop( true );
			}
		} );

		if ( Challenges.WARM_WELCOME.enabled() ) {
			builder.shift( Marker.Custom::boss, -4 );
		}
		if ( Challenges.HEADSTART.enabled() ) {
			builder.filter( ( marker ) -> marker.chapter() != Chapter.SEWERS );
		}
		if ( Challenges.AMNESIA.enabled() ) {
			builder.applyNamings( ( marker, index ) -> "??" );
		} else {
			builder.applyNamings( ( marker, index ) -> Integer.toString( index ) );
		}
		return builder.apply();
	}

	@Override
	protected Marker.Custom dummyLevelForId( int id ) {
		return new Marker.Custom( id, levelPackHash(), Integer.toString( id ), Chapter.EMPTY, (id - 1) % 5 + 1, id, id, false );
	}

	@Override
	public boolean posNeeded() {
		//2 POS each floor set
		int posLeftThisSet = 2 - (Dungeon.LimitedDrops.STRENGTH_POTIONS.getCount() - curLvl.scalingChapter() * 2);
		if ( posLeftThisSet <= 0 ) return false;

		int floorThisSet = curLvl.chapterProgression();

		//pos drops every two floors, (numbers 1-2, and 3-4) with a 50% chance for the earlier one each time.
		int targetPOSLeft = 2 - floorThisSet / 2;
		if ( floorThisSet % 2 == 1 && Random.Int( 2 ) == 0 ) targetPOSLeft--;

		return targetPOSLeft < posLeftThisSet;
	}

	@Override
	public boolean souNeeded() {
		int souLeftThisSet;
		//3 SOU each floor set, 1.5 (rounded) on forbidden runes challenge
		if ( Challenges.FORBIDDEN_RUNES.enabled() ) {
			souLeftThisSet = Math.round( 1.5f - (Dungeon.LimitedDrops.UPGRADE_SCROLLS.getCount() - (curLvl.scalingChapter()) * 1.5f) );
		} else {
			souLeftThisSet = 3 - (Dungeon.LimitedDrops.UPGRADE_SCROLLS.getCount() - (curLvl.scalingChapter()) * 3);
		}
		if ( souLeftThisSet <= 0 ) return false;

		int floorThisSet = curLvl.chapterProgression();
		//chance is floors left / scrolls left
		return Random.Int( 5 - floorThisSet ) < souLeftThisSet;
	}

	@Override
	public boolean asNeeded() {
		//1 AS each floor set
		int asLeftThisSet = 1 - (Dungeon.LimitedDrops.ARCANE_STYLI.getCount() - (curLvl.scalingChapter()));
		if ( asLeftThisSet <= 0 ) return false;

		int floorThisSet = curLvl.chapterProgression();
		//chance is floors left / scrolls left
		return Random.Int( 5 - floorThisSet ) < asLeftThisSet;
	}

	@Override
	public int petalsNeeded( DriedRose rose ) {
		return (int) Math.ceil( (float) ((curLvl.scalingDepth() / 2) - rose.droppedPetals) / 3 );
	}
}

