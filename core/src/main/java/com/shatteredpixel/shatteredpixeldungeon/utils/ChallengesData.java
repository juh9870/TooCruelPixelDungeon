package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class ChallengesData implements Bundlable {
    public ChampionEnemy.NormalChampionsDeck normalChampionsDeck;
    public ChampionEnemy.EliteChampionsDeck eliteChampionsDeck;

    public ChampionEnemy.NormalChampionsDeck kingOfAHillChampionsDeck;
    public ChampionEnemy.EliteChampionsDeck hailToTheKingChampions;

    public void init() {
        normalChampionsDeck = new ChampionEnemy.NormalChampionsDeck();
        eliteChampionsDeck = new ChampionEnemy.EliteChampionsDeck();
        kingOfAHillChampionsDeck = new ChampionEnemy.NormalChampionsDeck();
        hailToTheKingChampions = new ChampionEnemy.EliteChampionsDeck();
    }

    public static final String CHAMPION_DECK_NORMAL = "champion_deck_normal";
    public static final String CHAMPION_DECK_ELITE = "champion_deck_elite";
    public static final String KING_DECK_NORMAL = "champion_deck_normal";
    public static final String KING_DECK_ELITE = "champion_deck_elite";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        normalChampionsDeck = (ChampionEnemy.NormalChampionsDeck) bundle.get(CHAMPION_DECK_NORMAL);
        eliteChampionsDeck = (ChampionEnemy.EliteChampionsDeck) bundle.get(CHAMPION_DECK_ELITE);
        kingOfAHillChampionsDeck = (ChampionEnemy.NormalChampionsDeck) bundle.get(KING_DECK_NORMAL);
        hailToTheKingChampions = (ChampionEnemy.EliteChampionsDeck) bundle.get(KING_DECK_ELITE);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(CHAMPION_DECK_NORMAL, normalChampionsDeck);
        bundle.put(CHAMPION_DECK_ELITE, eliteChampionsDeck);
        bundle.put(KING_DECK_NORMAL, kingOfAHillChampionsDeck);
        bundle.put(KING_DECK_ELITE, hailToTheKingChampions);
    }
}
