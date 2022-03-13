package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ascension;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.KothBanned;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.watabou.utils.Random;

public interface ISwarm {

	float SPLIT_DELAY = 1f;

	static <T extends Mob, U extends Buff & ISwarm> T split( T original, T clone ) {
		return split( original, clone, null, (ISwarm) null );
	}

	static <T extends Mob, U extends Buff & ISwarm> T split( T original, T clone, ISwarm parentSwarm, Class<U> swarmBuffClass ) {
		ISwarm swarm = null;
		if ( swarmBuffClass != null ) swarm = Buff.affect( clone, swarmBuffClass );
		return split( original, clone, parentSwarm, swarm );
	}

	static <T extends Mob, U extends Buff & ISwarm> T split( T original, T clone, ISwarm parentSwarm, ISwarm childSwarm ) {
		if ( parentSwarm == null && original instanceof ISwarm ) {
			parentSwarm = (ISwarm) original;
		}
		if ( childSwarm == null && clone instanceof ISwarm ) {
			childSwarm = (ISwarm) clone;
		}
		if ( parentSwarm != null && childSwarm != null ) {
			childSwarm.setGeneration( parentSwarm.generation() + 1 );
		}
		clone.EXP = 0;
		if ( original.buff( Burning.class ) != null ) {
			Buff.affect( clone, Burning.class ).reignite( clone );
		}
		if ( original.buff( Poison.class ) != null ) {
			Buff.affect( clone, Poison.class ).set( 2 );
		}
		if ( original.buff( AllyBuff.class ) != null ) {
			Buff.affect( clone, Corruption.class );
		}

		if ( Challenges.ELITE_CHAMPIONS.enabled() ) {
			for (Buff buff : original.buffs()) {
				if ( buff != null &&
						!(buff instanceof ChampionEnemy.EliteChampion) ) {
					Buff.append( clone, buff.getClass() );
				}
			}

			if ( Challenges.DUNGEON_OF_CHAMPIONS.enabled() ) {
				ChampionEnemy.EliteChampion buff = Random.element( original.buffs( ChampionEnemy.EliteChampion.class ) );

				if ( buff != null ) {
					Buff.append( clone, buff.getClass() ).guardiansCooldown = buff.guardsSummonCooldown();
				}
			}
		}

		if ( Challenges.KING_OF_A_HILL.enabled() ) {
			Buff.prolong( clone, KothBanned.class, KothBanned.BAN_DURATION );
		}

		Buff.affect( clone, Ascension.BannedAscension.class );
		return clone;
	}

	int generation();

	void setGeneration( int generation );
}
