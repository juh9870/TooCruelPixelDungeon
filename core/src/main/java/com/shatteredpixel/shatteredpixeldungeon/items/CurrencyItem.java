package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.utils.Currency;

import java.util.ArrayList;

public abstract class CurrencyItem extends Item {
    {
        stackable = true;
    }

    public abstract Currency currency();

    @Override
    public ArrayList<String> actions(Hero hero ) {
        return new ArrayList<>();
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }
}
