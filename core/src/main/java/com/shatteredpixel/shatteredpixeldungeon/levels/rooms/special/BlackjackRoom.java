package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.HolderMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.HolderStatue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blackjackkeeper;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.Currency;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class BlackjackRoom extends SpecialRoom {
    public ArrayList<Item> sellItems;
    public ArrayList<Item> chestItems;
    public ArrayList<Item> crystalItems;
    public boolean sealed;
    private int[] terrain;

    @Override
    public int minWidth() {
        return 7;
    }

    @Override
    public int minHeight() {
        return 7;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.EMPTY);

        placeShopkeeper(level);

        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
        }
        sealed = false;
        sellItems = new ArrayList<>();
        chestItems = new ArrayList<>();
        crystalItems = new ArrayList<>();
        terrain = new int[level.length()];
    }

    private void placeShopkeeper(Level level) {

        int pos = level.pointToCell(center());
        System.out.println(level.pointToCell(center()));

        Mob shopkeeper = new Blackjackkeeper();
        shopkeeper.pos(pos);
        level.addMob(shopkeeper);

    }

    private HashSet<Integer> findFreeCells() {
        int margins = 0;
        boolean found = false;
        HashSet<Integer> result = new HashSet<>();
        do {
            if (width() - 2 - 2 * margins <= 0 || height() - 2 - 2 * margins <= 0) break;
            int y1 = top + 1 + margins;
            int y2 = bottom - 1 - margins;
            for (int y = y1; y <= y2; y++) {
                int x1 = left + 1 + margins;
                int x2 = right - 1 - margins;
                if (y == y1 || y == y2) {
                    for (int x = x1; x <= x2; x++) {
                        int c = Dungeon.level.pointToCell(new Point(x, y));
                        if (Dungeon.level.heaps.get(c) == null) {
                            found = true;
                            result.add(c);
                        }
                    }
                } else {
                    int c1 = Dungeon.level.pointToCell(new Point(x1, y));
                    int c2 = Dungeon.level.pointToCell(new Point(x2, y));
                    if (Dungeon.level.heaps.get(c1) == null) {
                        found = true;
                        result.add(c1);
                    }
                    if (c1 != c2 && Dungeon.level.heaps.get(c2) == null) {
                        found = true;
                        result.add(c2);
                    }
                }
            }
            margins++;
        } while (!found);
        result.remove(Dungeon.level.pointToCell(center()));
        return result;
    }

    private void placeItems(ArrayList<Item> items, Heap.Type type, boolean paid) {
        HashSet<Integer> cells = findFreeCells();
        while (items.size() > 0) {
            if (cells.size() == 0) {
                cells = findFreeCells();
                if (cells.size() == 0) return;
            }
            int c = Random.element(cells);
            cells.remove(c);
            Heap h = Dungeon.level.drop(items.get(0), c);
            items.remove(0);
            h.type = type;
            h.paid = paid;
            h.currency = Currency.TOKENS;
            if (Challenges.MIMICS_2.enabled()) {
                HolderMimic.spawnAt(c, Dungeon.level, h);
                h.destroy();
            } else {
                h.sprite.link();
                h.sprite.drop();
            }
            CellEmitter.get(c).burst(ElmoParticle.FACTORY, 4);
        }
    }

    public void seal() {
        sealed = true;
        terrain = new int[Dungeon.level.length()];
        for (int x = left; x < right + 1; x++) {
            for (int y = top; y < bottom + 1; y++) {
                int c = Dungeon.level.pointToCell(new Point(x, y));
                terrain[c] = Dungeon.level.map[c];
                if (x == left || x == right || y == top || y == bottom) Level.set(c, Terrain.WALL);
                else {
                    Level.set(c, Terrain.EMPTY_SP);
                }
                CellEmitter.get(c).burst(ElmoParticle.FACTORY, 2);
            }
        }
        GameScene.updateMap();
        placeItems(crystalItems, Heap.Type.CRYSTAL_CHEST, true);
        placeItems(chestItems, Heap.Type.LOCKED_CHEST, true);
        placeItems(sellItems, Heap.Type.FOR_SALE, false);
    }

    public void unseal() {
        sealed = false;
        Heap h;
        for (int x = left; x < right + 1; x++) {
            for (int y = top; y < bottom + 1; y++) {
                int c = Dungeon.level.pointToCell(new Point(x, y));
                if (Dungeon.level.map[c] != terrain[c])
                    CellEmitter.get(c).burst(ElmoParticle.FACTORY, 2);
                Level.set(c, terrain[c]);
                if ((h = Dungeon.level.heaps.get(c)) != null) {
                    if (h.type == Heap.Type.FOR_SALE || h.paid) {
                        CellEmitter.get(c).burst(ElmoParticle.FACTORY, 4);
                        h.destroy();
                    }
                }
            }
        }
        if (Challenges.MIMICS.enabled())
            HolderMimic.clearForSaleHeaps(Currency.TOKENS);
        GameScene.updateMap();
    }


    private static final String ITEMS_SELL = "items_sell";
    private static final String ITEMS_CHEST = "items_chest";
    private static final String ITEMS_CRYSTAL = "items_crystal";
    private static final String SEALED = "sealed";
    private static final String TERRAIN = "terrain";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ITEMS_SELL, sellItems);
        bundle.put(ITEMS_CHEST, chestItems);
        bundle.put(ITEMS_CRYSTAL, crystalItems);
        bundle.put(SEALED, sealed);
        bundle.put(TERRAIN, terrain);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        Collection<Bundlable> collection = bundle.getCollection(ITEMS_SELL);
        sellItems = new ArrayList<>();
        for (Bundlable b : collection) sellItems.add((Item) b);

        collection = bundle.getCollection(ITEMS_CHEST);
        chestItems = new ArrayList<>();
        for (Bundlable b : collection) chestItems.add((Item) b);

        collection = bundle.getCollection(ITEMS_CRYSTAL);
        crystalItems = new ArrayList<>();
        for (Bundlable b : collection) crystalItems.add((Item) b);

        sealed = bundle.getBoolean(SEALED);
        terrain = bundle.getIntArray(TERRAIN);
    }
}
