package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.PokerToken;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTradeItem;
import com.watabou.noosa.Image;

public enum Currency {
    GOLD {
        @Override
        public String forSaleText(Item item) {
            return Messages.get(Heap.class, "for_sale", sellPrice(item), item.toString());
        }

        @Override
        public String buyText(int price) {
            return Messages.get(WndTradeItem.class, "buy", price);
        }

        @Override
        public void receive(int amount, Hero hero) {
            new Gold(amount).doPickUp(hero);
        }

        @Override
        public int get() {
            return Dungeon.gold;
        }

        @Override
        public void set(int amount) {
            Dungeon.gold = amount;
        }

        @Override
        public int sellPrice(Item item) {
            return Shopkeeper.sellPrice(item);
        }

        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.GOLD);
        }

        @Override
        public Item item() {
            return new Gold();
        }
    },
    TOKENS {
        @Override
        public void receive(int amount, Hero hero) {
            new PokerToken(amount).doPickUp(hero);
        }

        @Override
        public int get() {
            return Dungeon.tokens;
        }

        @Override
        public void set(int amount) {
            Dungeon.tokens = amount;
        }

        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.POKER_TOKEN);
        }

        @Override
        public Item item() {
            return new PokerToken();
        }
    },
    ENERGY {
        @Override
        public void receive(int amount, Hero hero) {
            Dungeon.energy += amount;
        }

        @Override
        public int get() {
            return Dungeon.energy;
        }

        @Override
        public void set(int amount) {
            Dungeon.energy = amount;
        }

        @Override
        public Image icon() {
            return new ItemSprite(ItemSpriteSheet.ENERGY);
        }

        @Override
        public Item item() {
            return new EnergyCrystal();
        }
    };

    public String forSaleText(Item item) {
        return Messages.get(Currency.class, "for_sale", item.value(), item.toString(), shortName());
    }

    public String buyText(int price) {
        return Messages.get(Currency.class, "buy", price, shortName());
    }

    public String currencyName() {
        return Messages.get(Currency.class, name().toLowerCase() + "_name");
    }

    public String shortName() {
        return Messages.get(Currency.class, name().toLowerCase() + "_symbol");
    }

    public abstract void receive(int amount, Hero hero);

    public abstract int get();

    public abstract void set(int amount);

    public void add(int amount) {
        set(get() + amount);
    }

    public void remove(int amount) {
        set(get() - amount);
    }

    public boolean have(int amount) {
        return get() >= amount;
    }

    public int sellPrice(Item item) {
        return item.value();
    }

    public abstract Image icon();

    public abstract Item item();
}
