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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.NoReward;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon.Enchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Statue extends Mob {
	
	{
		spriteClass = StatueSprite.class;

		EXP = 0;
		state = PASSIVE;
		
		properties.add(Property.INORGANIC);
	}
	
	protected Weapon weapon;
	
	public Statue() {
		super();
		

		weapon = (MeleeWeapon) Generator.random(Generator.Category.WEAPON);
		weapon.cursed=false;
		
		weapon.enchant( Enchantment.random() );
		
		HP = HT = 15 + Dungeon.scalingFactor() * 5;
		defenseSkill = 4 + Dungeon.scalingFactor();
	}
	
	private static final String WEAPON	= "weapon";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( WEAPON, weapon );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		weapon = (Weapon)bundle.get( WEAPON );
	}
	
	@Override
	public boolean canAscend() {
		return false;
	}
	
	@Override
	protected boolean act() {
		if (Dungeon.level.heroFOV[pos()] && !(this instanceof HolderStatue)) {
			Notes.add( Notes.Landmark.STATUE );
		}
		return super.act();
	}
	
	@Override
	public int damageRoll() {
		return weapon.damageRoll(this);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return (int)((9 + Dungeon.scalingFactor()) * weapon.accuracyFactor(this));
	}
	
	@Override
	public float attackDelay() {
		return super.attackDelay()*weapon.delayFactor( this );
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return super.canAttack(enemy) || weapon.canReach(this, enemy.pos());
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, Dungeon.scalingFactor() + weapon.defenseFactor(this));
	}
	
	@Override
	public void add(Buff buff) {
		super.add(buff);
		if (state == PASSIVE && buff.type == Buff.buffType.NEGATIVE){
			state = HUNTING;
		}
	}

	@Override
	public void damage( int dmg, Object src ) {

		if (state == PASSIVE) {
			state = HUNTING;

			for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
				Char c = Actor.findChar(i+ pos());
				if(c instanceof Statue){
					((Statue) c).state = ((Statue) c).HUNTING;
				}
			}
		}
		
		super.damage( dmg, src );
	}

	public void wakeup(){
		if (state == PASSIVE) {
			state = HUNTING;
		}
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		damage = weapon.proc( this, enemy, damage );
		if (!enemy.isAlive() && enemy == Dungeon.hero){
			Dungeon.fail(getClass());
			GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
		}
		return damage;
	}
	
	@Override
	public void beckon( int cell ) {
		// Do nothing
	}

	@Override
	public void rollToDropLoot() {
		super.rollToDropLoot();
		if (buff(NoReward.class) == null) {
			weapon.identify();
			if (Challenges.CURSED.enabled()) {
				weapon.cursed = true;
				weapon.enchant(Enchantment.randomCurse());
			}
			Dungeon.level.drop(weapon, pos()).sprite.drop();
		}
	}
	
	@Override
	public void destroy() {
		Notes.remove( Notes.Landmark.STATUE );
		super.destroy();
	}

	@Override
	public float spawningWeight() {
		return 0f;
	}

	@Override
	public boolean reset() {
		state = PASSIVE;
		return true;
	}

	@Override
	public String description() {
		return Messages.get(this, "desc", weapon.name());
	}
	
	{
		resistances.add(Grim.class);
	}

	public static Statue random(){
		if (Random.Int(10) == 0){
			return new ArmoredStatue();
		} else {
			return new Statue();
		}
	}
	
}
