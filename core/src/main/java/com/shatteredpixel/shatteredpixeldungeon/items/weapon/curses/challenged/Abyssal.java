package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.challenged;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Abyssal extends Weapon.Enchantment {
	private static final ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );
	private static final float CHANCE = 0.2f;

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {
		if ( defender.HP <= damage ) return damage;
		if ( Random.Float() < CHANCE * procChanceMultiplier( attacker ) ) {
			int dmg = defender.isImmune( Grim.class ) ? (attacker.damageRoll() + attacker.damageRoll()) : defender.HP;

			defender.damage( dmg, this );
			defender.sprite.emitter().burst( ShadowParticle.UP, 5 );
			attacker.sprite.emitter().burst( ShadowParticle.UP, 5 );
			Buff.affect( attacker, Viscosity.DeferedDamage.class ).prolong( dmg / 2 );
			Sample.INSTANCE.play( Assets.Sounds.DEBUFF, 0.5f );
		}
		return damage;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}
}
