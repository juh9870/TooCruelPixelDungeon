package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class ChallengesLevelData implements Bundlable {
    public ChampionEnemy.NormalChampionsDeck normalChampionsDeck;
    public ChampionEnemy.EliteChampionsDeck eliteChampionsDeck;

    public void init(Level level, int depth) {
        normalChampionsDeck = new ChampionEnemy.NormalChampionsDeck();
        eliteChampionsDeck = new ChampionEnemy.EliteChampionsDeck();
    }

    public static final String CHAMPION_DECK_NORMAL = "champion_deck_normal";
    public static final String CHAMPION_DECK_ELITE = "champion_deck_elite";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        normalChampionsDeck = (ChampionEnemy.NormalChampionsDeck) bundle.get(CHAMPION_DECK_NORMAL);
        eliteChampionsDeck = (ChampionEnemy.EliteChampionsDeck) bundle.get(CHAMPION_DECK_ELITE);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(CHAMPION_DECK_NORMAL, normalChampionsDeck);
        bundle.put(CHAMPION_DECK_ELITE, eliteChampionsDeck);
    }
}
