package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.Currency;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class EnergyCrystal extends CurrencyItem {

	private static final String TXT_VALUE	= "%+d";

	{
		image = ItemSpriteSheet.ENERGY;
	}

	public EnergyCrystal() {
		this( 1 );
	}

	public EnergyCrystal( int value ) {
		this.quantity = value;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {

		currency().add(quantity);
		//TODO Statistics.goldCollected += quantity;
		//Badges.validateGoldCollected();

		GameScene.pickUp( this, pos );
		hero.sprite.showStatus( 0x44CCFF, TXT_VALUE, quantity );
		hero.spendAndNext( TIME_TO_PICK_UP );

		Sample.INSTANCE.play( Assets.Sounds.ITEM );

		return true;
	}

	@Override
	public Item random() {
		quantity = Random.IntRange( 4, 6 );
		return this;
	}

	@Override
	public Currency currency() {
		return Currency.ENERGY;
	}
}
