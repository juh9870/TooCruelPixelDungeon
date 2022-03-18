package com.shatteredpixel.shatteredpixeldungeon.items.wands.tcpd;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.DamageWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class BasisWand extends DamageWand {
	{
		image = ItemSpriteSheet.WAND_MAGIC_MISSILE_TCPD;
	}

	public int min( int lvl ) {
		return 2 + lvl;
	}

	public int max( int lvl ) {
		return 8 + 2 * lvl;
	}

	@Override
	public void onZap( Ballistica bolt ) {
		Char ch = Actor.findChar( bolt.collisionPos );
		if ( ch != null ) {
			wandProc( ch, chargesPerCast() );
			ch.damage( damageRoll(), this );
			Sample.INSTANCE.play( Assets.Sounds.HIT_MAGIC, 1, Random.Float( 0.87f, 1.15f ) );
		} else {
			Dungeon.level.pressCell( bolt.collisionPos );
		}
	}

	@Override
	public void onHit( MagesStaff staff, Char attacker, Char defender, int damage ) {
	}

	@Override
	public boolean canImbueStaff() {
		return false;
	}
}
