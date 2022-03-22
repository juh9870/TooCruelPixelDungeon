package com.shatteredpixel.shatteredpixeldungeon.items.wands.tcpd;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.TriWandIndicator;
import com.shatteredpixel.shatteredpixeldungeon.items.AlchemyPredicate;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickRecipe;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.ListUtils;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.watabou.utils.UnorderedPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class TriWand extends Wand {

	public static final String AC_TOGGLE_RANDOM = "TOGGLE_FLOATING";
	public static final String AC_TOGGLE_PITY = "TOGGLE_PITY";

	public enum Effect {
		NONE,
		FIRST,
		SECOND
	}

	private static final float MAX_DISBALANCE = 4f;
	public WandEffect firstEffect;
	public WandEffect secondEffect;
	public WandEffect neutralEffect;
	// > 0 first effect
	// < 0 second effect
	public int balance = 0;
	// > 0 augment first
	// < 0 augment second
	public float augment = 0;
	public WandEffect curEffect;
	private boolean fixedChances = false;
	private boolean noNeutralPity = false;


	private TriWandIndicator indicator;

	@Override
	protected void showAimVisuals() {
		super.showAimVisuals();
		indicator = TriWandIndicator.show( curUser, this );
	}

	@Override
	protected void hideAimVisuals() {
		super.hideAimVisuals();
		if ( indicator != null ) indicator.hide();
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if ( DeviceCompat.isDebug() || Challenges.DEBUG.enabled() ) {
			actions.add( AC_TOGGLE_PITY );
			actions.add( AC_TOGGLE_RANDOM );
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {
		super.execute( hero, action );
		if ( action.equals( AC_TOGGLE_RANDOM ) ) {
			fixedChances = !fixedChances;
			if ( fixedChances ) balance = 0;
		}
		if ( action.equals( AC_TOGGLE_PITY ) ) {
			noNeutralPity = !noNeutralPity;
		}
	}

	@Override
	protected void wandUsed() {
		if ( !fixedChances ) {
			if ( curEffect == firstEffect ) {
				balance++;
			} else if ( curEffect == secondEffect ) {
				balance--;
			}
		}
		super.wandUsed();
	}

	@Override
	public boolean tryToZap( Hero owner, int target ) {
		if ( super.tryToZap( owner, target ) ) {
			randomizeEffect();
			return true;
		}
		return false;
	}

	@Override
	protected int chargesPerCast() {
		return curEffect.chargesPerCast();
	}

	protected float[] effectsChances() {
		return new float[]{
				weightFirst(),  //first effect
				weightSecond(), //second effect
				weightNeutral() //neutral effect
		};
	}

	public float weightFirst() {
		float weight = 1 - balance / MAX_DISBALANCE;
		if ( augment > 0 ) {
			weight *= 2;
		}
		return weight;
	}

	public float weightSecond() {
		float weight = 1 + balance / MAX_DISBALANCE;
		if ( augment < 0 ) {
			weight *= 2;
		}
		return weight;
	}

	public float weightNeutral() {
		if ( level() >= 12 ) return 0;
		if ( curEffect == neutralEffect && !noNeutralPity ) return 0f;
		return (float) Math.pow( 0.85, level() );
	}

	protected void randomizeEffect() {
		int effect = Random.chances( effectsChances() );

		curEffect = new WandEffect[]{
				firstEffect,
				secondEffect,
				neutralEffect
		}[effect];
	}

	@Override
	public String statsDesc() {
		StringBuilder sb = new StringBuilder( /*Messages.get( this, "stats_desc" )*/ );

		float total = weightFirst() + weightSecond() + weightNeutral();

		float[] chances = new float[]{
				weightNeutral() / total,
				weightFirst() / total,
				weightSecond() / total
		};
		WandEffect[] wandEffects = new WandEffect[]{neutralEffect, firstEffect, secondEffect};

		boolean first = true;
		for (int i = 0, wandEffectsLength = wandEffects.length; i < wandEffectsLength; i++) {
			WandEffect wandEffect = wandEffects[i];
			if ( first ) first = false;
			else sb.append( "\n\n" );
			int chance = Math.round( chances[i] * 100 );
			sb.append( "_" )
					.append( wandEffect.name() )
					.append( " (" )
					.append( isIdentified() ? chance : "??" )
					.append( "%):_ " )
					.append( wandEffect.desc() );
			if ( wandEffect.augmented() ) {
				sb.append( "\n_" )
						.append( Messages.get( this, "augment" ) )
						.append( "_: " ).append( wandEffect.augmentDesc() );
			}
		}
		if ( DeviceCompat.isDebug() || Challenges.DEBUG.enabled() ) {
			sb.append( "\n" );
			sb.append( "\nFloating effect chances: " ).append( fixedChances ? "OFF" : "ON" );
			sb.append( "\nNeutral effect pity: " ).append( noNeutralPity ? "OFF" : "ON" );
		}
		return sb.toString();
	}

	@Override
	public ItemSprite.Glowing glowing() {
		if ( firstEffect.augmented() ) return firstEffect.augmentGlow();
		if ( secondEffect.augmented() ) return secondEffect.augmentGlow();
		return null;
	}

	public TriWand augment( Effect effect ) {
		if ( effect == Effect.FIRST ) {
			augment = 1;
		} else if ( effect == Effect.SECOND ) {
			augment = -1;
		} else if ( effect == Effect.NONE ) {
			augment = 0;
		}
		return this;
	}

	public Effect augment() {
		if ( augment > 0 ) return Effect.FIRST;
		if ( augment < 0 ) return Effect.SECOND;
		return Effect.NONE;
	}

	@Override
	public boolean isSimilar( Item item ) {
		return super.isSimilar( item ) && augment() == ((TriWand) item).augment();
	}

	private AlchemyPredicate predicate() {
		return new AlchemyPredicate( this, ( i ) -> i.getClass() == getClass() && ((TriWand) i).augment() == augment() );
	}

	@Override
	public boolean canImbueStaff() {
		return false;
	}

	private static final String CURRENT_EFFECT = "cureffect";
	private static final String BALANCE = "balance";
	private static final String CHANCE_TYPE = "chnaceType";
	private static final String PITY = "pity";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		if ( curEffect == firstEffect ) bundle.put( CURRENT_EFFECT, 0 );
		else if ( curEffect == secondEffect ) bundle.put( CURRENT_EFFECT, 1 );
		else bundle.put( CURRENT_EFFECT, 2 );
		bundle.put( BALANCE, balance );
		bundle.put( CHANCE_TYPE, fixedChances );
		bundle.put( PITY, noNeutralPity );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		curEffect = new WandEffect[]{
				firstEffect,
				secondEffect,
				neutralEffect
		}[bundle.getInt( CURRENT_EFFECT )];
		balance = bundle.getInt( BALANCE );
		fixedChances = bundle.getBoolean( CHANCE_TYPE );
		noNeutralPity = bundle.getBoolean( PITY );
	}

	@Override
	public Item upgrade() {
		augment( Effect.NONE );
		return super.upgrade();
	}

	@Override
	public void onZap( Ballistica attack ) {
		curEffect.onZap( attack );
	}

	@Override
	public int targetingPos( Hero user, int dst ) {
		return curEffect.targetingPos( user, dst );
	}

	@Override
	public void fx( Ballistica bolt, Callback callback ) {
		curEffect.fx( bolt, callback );
	}

	@Override
	public void onHit( MagesStaff staff, Char attacker, Char defender, int damage ) {
		// TODO: do something on hit
	}

	public abstract class WandEffect {
		protected int collisionProperties = Ballistica.MAGIC_BOLT;

		public abstract void onZap( Ballistica target );

		public abstract Class<? extends Item> catalyst();

		protected void fx( Ballistica bolt, Callback callback ) {
			MagicMissile.boltFromChar( curUser.sprite.parent,
					MagicMissile.MAGIC_MISSILE,
					curUser.sprite,
					bolt.collisionPos,
					callback );
			Sample.INSTANCE.play( Assets.Sounds.ZAP, 1, Random.Float( 0.87f, 1.15f ) );
		}

		public int chargesPerCast() {
			return 1;
		}

		public int targetingPos( Hero user, int dst ) {
			return new Ballistica( user.pos(), dst, collisionProperties ).collisionPos;
		}

		public boolean augmented() {
			if ( this == firstEffect ) return augment() == Effect.FIRST;
			if ( this == secondEffect ) return augment() == Effect.SECOND;
			return false;
		}

		public abstract ItemSprite.Glowing augmentGlow();

		public int indicatorColor() {
			if ( augmentGlow() != null ) return augmentGlow().color;
			return 0xffffff;
		}

		public String name() {
			return Messages.get( this, "name" );
		}

		public String desc() {
			return Messages.get( this, "desc" );
		}

		public String augmentDesc() {
			return Messages.get( this, "augment" );
		}
	}

	protected abstract class DamageWandEffect extends WandEffect implements Hero.Doom {
		public int min() {
			return min( buffedLvl() );
		}

		public abstract int min( int lvl );

		public int max() {
			return max( buffedLvl() );
		}

		public abstract int max( int lvl );

		public int damageRoll() {
			return Random.NormalIntRange( min(), max() );
		}

		public int damageRoll( int lvl ) {
			return Random.NormalIntRange( min( lvl ), max( lvl ) );
		}

		@Override
		public void onDeath() {
			Dungeon.fail( getClass() );
			GLog.n( Messages.get( this, "ondeath" ) );
		}
	}

	public static ArrayList<QuickRecipe> recipes() {
		BasisWand basis = new BasisWand();
		basis.identify();
		basis.hideCharges();

		CraftingRecipe craft = new CraftingRecipe();
		Augmentation augment = new Augmentation();
		ClearAugment clearAugment = new ClearAugment();

		ArrayList<QuickRecipe> recipes = new ArrayList<>();

		for (Set<Class<? extends Item>> catalysts : CraftingRecipe.recipes.keySet()) {

			TriWand wand = Reflection.newInstance( CraftingRecipe.recipes.get( catalysts ) );
			wand.identify();
			wand.hideCharges();
			List<Item> items = ListUtils.map( new ArrayList<>( catalysts ), Reflection::newInstance );

			for (UnorderedPair<Item> pair : ListUtils.pairs( items )) {
				if ( pair.first == pair.second ) continue;
				recipes.add( new QuickRecipe( craft, new ArrayList<>( Arrays.asList( basis, pair.first, pair.second ) ) ) );
			}

			Item neutral = Reflection.newInstance( wand.neutralEffect.catalyst() );
			Item first = Reflection.newInstance( wand.firstEffect.catalyst() );
			Item second = Reflection.newInstance( wand.secondEffect.catalyst() );
			TriWand augmented = ((TriWand) wand.clone()).augment( Effect.FIRST );

			recipes.add( new QuickRecipe( augment, new ArrayList<>( Arrays.asList( wand.predicate(), neutral, first ) ) ) );
			recipes.add( new QuickRecipe( augment, new ArrayList<>( Arrays.asList( wand.predicate(), neutral, second ) ) ) );
			recipes.add( new QuickRecipe( clearAugment, new ArrayList<>( Arrays.asList( augmented.predicate(), neutral ) ) ) );
		}
		return recipes;
	}

	public static class Augmentation extends Recipe {
		@Override
		public boolean testIngredients( ArrayList<Item> ingredients ) {
			if ( ingredients.size() != 3 ) return false;
			if ( !(ingredients.get( 0 ) instanceof TriWand) ) {
				return false;
			}
			TriWand wand = (TriWand) ingredients.get( 0 );
			if ( !wand.isIdentified() ) return false;
			if ( wand.augment != 0 ) return false;
			boolean hasNeutral = false;
			boolean hasEffect = false;
			for (int i = 1; i < ingredients.size(); i++) {
				Class<? extends Item> itemClass = ingredients.get( i ).getClass();
				if ( itemClass == wand.neutralEffect.catalyst() ) {
					hasEffect = true;
				}
				if ( itemClass == wand.firstEffect.catalyst() || itemClass == wand.secondEffect.catalyst() ) {
					hasNeutral = true;
				}
			}
			return hasEffect && hasNeutral;
		}

		@Override
		public int cost( ArrayList<Item> ingredients ) {
			return 6;
		}

		@Override
		public Item brew( ArrayList<Item> ingredients ) {
			if ( !testIngredients( ingredients ) ) return null;
			TriWand wand = (TriWand) ingredients.get( 0 );
			wand.augment( getAugment( ingredients ) );
			for (Item ingredient : ingredients) {
				if ( ingredient != wand ) ingredient.quantity( ingredient.quantity() - 1 );
			}
			return wand;
		}

		@Override
		public Item sampleOutput( ArrayList<Item> ingredients ) {
			if ( !testIngredients( ingredients ) ) return null;
			TriWand clone = (TriWand) ingredients.get( 0 ).clone();
			clone.augment( getAugment( ingredients ) );
			return clone;
		}

		private static Effect getAugment( ArrayList<Item> ingredients ) {
			TriWand wand = (TriWand) ingredients.get( 0 );
			for (Item i : ingredients) {
				if ( i.getClass() == wand.firstEffect.catalyst() ) return Effect.FIRST;
				if ( i.getClass() == wand.secondEffect.catalyst() ) return Effect.SECOND;
			}
			throw new IllegalArgumentException( "Invalid ingredients" );
		}

		@Override
		public boolean clearInput() {
			return true;
		}
	}

	public static class ClearAugment extends Recipe {

		@Override
		public boolean testIngredients( ArrayList<Item> ingredients ) {
			if ( ingredients.size() != 2 ) return false;
			if ( !(ingredients.get( 0 ) instanceof TriWand) ) {
				return false;
			}
			TriWand wand = (TriWand) ingredients.get( 0 );
			if ( !wand.isIdentified() ) return false;
			if ( wand.augment() == Effect.NONE ) return false;
			return ingredients.get( 1 ).getClass() == wand.neutralEffect.catalyst();
		}

		@Override
		public int cost( ArrayList<Item> ingredients ) {
			return 6;
		}

		@Override
		public Item brew( ArrayList<Item> ingredients ) {
			TriWand wand = (TriWand) ingredients.get( 0 );
			wand.augment( Effect.NONE );

			for (Item ingredient : ingredients) {
				if ( ingredient != wand ) ingredient.quantity( ingredient.quantity() - 1 );
			}
			return wand;
		}

		@Override
		public Item sampleOutput( ArrayList<Item> ingredients ) {
			if ( !testIngredients( ingredients ) ) return null;
			TriWand clone = (TriWand) ingredients.get( 0 ).clone();
			clone.augment( Effect.NONE );
			return clone;
		}

		@Override
		public boolean clearInput() {
			return true;
		}
	}

	public static class CraftingRecipe extends Recipe {

		private static final Map<Set<Class<? extends Item>>, Class<? extends TriWand>> recipes = new LinkedHashMap<>();

		private static void recipeFor( TriWand wand ) {
			Set<Class<? extends Item>> catalysts = new HashSet<>();
			catalysts.add( wand.firstEffect.catalyst() );
			catalysts.add( wand.secondEffect.catalyst() );
			catalysts.add( wand.neutralEffect.catalyst() );
			recipes.put( catalysts, wand.getClass() );
		}

		static {
			recipeFor( new ThermalTriWand() );
			recipeFor( new FluidTriWand() );
			recipeFor( new CircularTriWand() );
		}

		private static Class<? extends TriWand> getResult( ArrayList<Item> ingredients ) {
			for (Set<Class<? extends Item>> catalysts : recipes.keySet()) {
				if ( catalysts.contains( ingredients.get( 1 ).getClass() ) &&
						catalysts.contains( ingredients.get( 2 ).getClass() ) ) {
					return recipes.get( catalysts );
				}
			}
			return null;
		}

		@Override
		public boolean testIngredients( ArrayList<Item> ingredients ) {
			if ( ingredients.size() != 3 ) return false;
			if ( !(ingredients.get( 0 ) instanceof BasisWand) ) return false;
			if ( !ingredients.get( 0 ).isIdentified() ) return false;
			if ( ingredients.get( 1 ).getClass() == ingredients.get( 2 ).getClass() ) return false;
			return getResult( ingredients ) != null;
		}

		@Override
		public int cost( ArrayList<Item> ingredients ) {
			return 12;
		}

		@Override
		public Item brew( ArrayList<Item> ingredients ) {
			if ( !testIngredients( ingredients ) ) return null;
			TriWand wand = sampleOutput( ingredients );
			for (Item ingredient : ingredients) {
				ingredient.quantity( ingredient.quantity() - 1 );
			}
			return wand;
		}

		@Override
		public TriWand sampleOutput( ArrayList<Item> ingredients ) {
			if ( !testIngredients( ingredients ) ) return null;
			Wand basis = (Wand) ingredients.get( 0 );

			TriWand wand = Reflection.newInstance( getResult( ingredients ) );

			wand.level( 0 );
			int level = basis.level();
			if ( basis.curseInfusionBonus ) level--;
			level -= basis.resinBonus;
			wand.upgrade( level );

			wand.levelKnown = basis.levelKnown;
			wand.curChargeKnown = basis.curChargeKnown;
			wand.cursedKnown = basis.cursedKnown;
			wand.cursed = basis.cursed;
			wand.curseInfusionBonus = basis.curseInfusionBonus;
			wand.resinBonus = basis.resinBonus;

			wand.curCharges = basis.curCharges;
			wand.hideCharges = basis.hideCharges;
			wand.updateLevel();
			return wand;
		}
	}
}