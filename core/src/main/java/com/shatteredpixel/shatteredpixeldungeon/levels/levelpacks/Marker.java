package com.shatteredpixel.shatteredpixeldungeon.levels.levelpacks;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.Arrays;

public abstract class Marker implements Bundlable, Comparable<Marker> {
	public abstract String displayName();

	public abstract Chapter chapter();

	public int chapterId() {
		return chapter().id();
	}

	public abstract int chapterProgression();

	public abstract boolean firstLevel();

	public abstract int scalingDepth();

	public int scalingChapter() {
		return scalingDepth() / 5;
	}

	public abstract int legacyLevelgenMapping();

	public abstract String debugInfo();

	public abstract boolean boss();

	public abstract boolean shop();

	public Class<? extends Level> customLevel() {
		return null;
	}

	@Override
	public abstract boolean equals( Object obj );

	public static abstract class Linear extends Marker {
		private static final String ID = "id";
		private int id;

		protected Linear( int id ) {
			this.id = id;
		}

		@Override
		public String displayName() {
			return Integer.toString( id() );
		}

		@Override
		public boolean equals( Object obj ) {
			return obj instanceof Linear && ((Linear) obj).id() == id();
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			id = bundle.getInt( ID );
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
			bundle.put( ID, id() );
		}

		public int id() {
			return id;
		}

		public void setId( int id ) {
			this.id = id;
		}

		@Override
		public boolean firstLevel() {
			return id == 1;
		}

		@Override
		public int compareTo( Marker marker ) {
			if ( getClass() == marker.getClass() ) {
				return Integer.compare( id, ((Linear) marker).id );
			}
			throw new IllegalArgumentException( "Expected " + getClass() + ", got " + marker.getClass() );
		}

		@Override
		public String debugInfo() {
			return Integer.toString( id );
		}
	}

	public static class Custom extends Linear {
		private static final String LEVEL_PACK_HASH = "levelPackHash";
		private static final String DISPLAY_NAME = "displayName";
		private static final String CHAPTER = "chapter";
		private static final String CHAPTER_PROGRESSION = "chapterProgression";
		private static final String SCALING_DEPTH = "scalingDepth";
		private static final String LEGACY_LEVELGEN_MAPPING = "legacyLevelgenMapping";
		private static final String BOSS = "boss";
		private static final String SHOP = "shop";
		private static final String CUSTOM_LEVEL = "customLevel";

		private int levelPackHash = 0;
		private String displayName = "";
		private Chapter chapter = Chapter.EMPTY;
		private int chapterProgression = 0;
		private int scalingDepth = 0;
		private int legacyLevelgenMapping = 0;
		private boolean boss = false;
		private boolean shop = false;
		private Class<? extends Level> customLevel;

		public Custom() {
			super( -1 );
		}

		/**
		 * @param id                    level id, usually assigned by levelpack. Used for comparison
		 * @param levelPackHash         hash of current levelpack and challenged
		 * @param displayName           display name of the level
		 * @param chapter               chapter of the level
		 * @param chapterProgression    index of this level in a chapter. Should be in [1,5] bounds
		 * @param scalingDepth          number used for damage, mob strength, and some drop generation
		 * @param legacyLevelgenMapping mappings used for legacy level generation. Should match vanilla as closely as possible
		 * @param boss                  wherever this level is a boss level. Boss levels should have {@code chapterProgression} of 5
		 */
		public Custom( int id, int levelPackHash, String displayName, Chapter chapter, int chapterProgression, int scalingDepth, int legacyLevelgenMapping, boolean boss ) {
			super( id );
			this.levelPackHash = levelPackHash;
			this.displayName = displayName;
			this.chapter = chapter;
			this.chapterProgression = chapterProgression;
			this.scalingDepth = scalingDepth;
			this.legacyLevelgenMapping = legacyLevelgenMapping;
			this.boss = boss;
		}

		public static Custom fromLegacy( int depth ) {
			int progress = (depth - 1) % 5 + 1;
			Chapter chapter = Chapter.fromId( (depth - 1) / 5 );
			return new Marker.Custom( depth, 0, Integer.toString( depth ), chapter, progress, depth, depth, progress == 5 );
		}

		public void setLevelPackHash( int levelPackHash ) {
			this.levelPackHash = levelPackHash;
		}

		public void setDisplayName( String displayName ) {
			this.displayName = displayName;
		}

		public void setChapter( Chapter chapter ) {
			this.chapter = chapter;
		}

		public void setChapterProgression( int chapterProgression ) {
			this.chapterProgression = chapterProgression;
		}

		public void setScalingDepth( int scalingDepth ) {
			this.scalingDepth = scalingDepth;
		}

		public void setLegacyLevelgenMapping( int legacyLevelgenMapping ) {
			this.legacyLevelgenMapping = legacyLevelgenMapping;
		}

		public void setCustomLevel( Class<? extends Level> level ) {
			customLevel = level;
		}

		public void setBoss( boolean boss ) {
			this.boss = boss;
		}

		public void setShop( boolean shop ) {
			this.shop = shop;
		}

		@Override
		public String displayName() {
			return displayName;
		}

		@Override
		public Chapter chapter() {
			return chapter;
		}

		@Override
		public int chapterProgression() {
			return chapterProgression;
		}

		@Override
		public int scalingDepth() {
			return scalingDepth;
		}

		@Override
		public int legacyLevelgenMapping() {
			return legacyLevelgenMapping;
		}

		public boolean boss() {
			return boss;
		}

		@Override
		public boolean shop() {
			return shop;
		}

		@Override
		public Class<? extends Level> customLevel() {
			return customLevel;
		}

		@Override
		public String debugInfo() {
			return "{" + levelPackHash + ":" + id() + "}";
		}

		@Override
		public boolean equals( Object o ) {
			if ( this == o ) return true;
			if ( o == null || getClass() != o.getClass() ) return false;
			Custom custom = (Custom) o;
			return custom.id() == id() && custom.levelPackHash == levelPackHash;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode( new Object[]{id(), levelPackHash} );
		}

		@Override
		public int compareTo( Marker marker ) {
			if ( getClass() == marker.getClass() ) {
				Custom other = (Custom) marker;
				if ( other.levelPackHash != levelPackHash )
					throw new IllegalArgumentException( "Levelpack ID mismatch. Expected" + levelPackHash + ", got " + other.levelPackHash );
				return Integer.compare( id(), other.id() );
			}
			throw new IllegalArgumentException( "Expected " + getClass() + ", got " + marker.getClass() );
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
			super.storeInBundle( bundle );
			bundle.put( LEVEL_PACK_HASH, levelPackHash );
			bundle.put( DISPLAY_NAME, displayName );
			bundle.put( CHAPTER, chapter );
			bundle.put( CHAPTER_PROGRESSION, chapterProgression );
			bundle.put( SCALING_DEPTH, scalingDepth );
			bundle.put( LEGACY_LEVELGEN_MAPPING, legacyLevelgenMapping );
			bundle.put( BOSS, boss );
			bundle.put( SHOP, shop );
			if ( customLevel != null )
				bundle.put( CUSTOM_LEVEL, customLevel );
		}

		@Override
		public void restoreFromBundle( Bundle bundle ) {
			super.restoreFromBundle( bundle );
			levelPackHash = bundle.getInt( LEVEL_PACK_HASH );
			displayName = bundle.getString( DISPLAY_NAME );
			chapter = bundle.getEnum( CHAPTER, Chapter.class );
			chapterProgression = bundle.getInt( CHAPTER_PROGRESSION );
			scalingDepth = bundle.getInt( SCALING_DEPTH );
			legacyLevelgenMapping = bundle.getInt( LEGACY_LEVELGEN_MAPPING );
			boss = bundle.getBoolean( BOSS );
			shop = bundle.getBoolean( SHOP );
			if ( bundle.contains( CUSTOM_LEVEL ) )
				customLevel = bundle.getClass( CUSTOM_LEVEL );
		}
	}
}
