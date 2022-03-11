package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Deck<T> implements Bundlable {

    private static final String PROBS = "probs";
    public T[] values;
    public float[] defaultProbs;
    public float[] probs = null;

    public T get() {
        if (probs == null) {
            reset();
        }
        int i = Random.chances(probs);
        if (i == -1) {
            reset();
            i = Random.chances(probs);
        }
        probs[i]--;
        return values[i];
    }

    public void add(T value) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == value) {
                probs[i]++;
                return;
            }
        }
    }

    public void reset() {
        probs = defaultProbs.clone();
    }

    protected Filler filler() {
        return new Filler();
    }

    public static Deck<Boolean> randomPicker(int hits, int total) {
        return new RandomFillerDeck(hits, total);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        probs = bundle.getFloatArray(PROBS);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(PROBS, probs);
    }

    public static class RandomFillerDeck extends Deck<Boolean> {
        public RandomFillerDeck(int hits, int total) {
            filler().add(true, hits);
            filler().add(false, total - hits);
        }
    }

    public class Filler {
        private final Map<T, Float> valuesMap = new LinkedHashMap<>();
        private float defaultWeight = 1f;

        public Filler defaultWeight(float value) {
            if (defaultWeight < 0) throw new IllegalArgumentException("Weight must non-negative");
            defaultWeight = value;
            return this;
        }

        public Filler add(T value) {
            valuesMap.put(value, defaultWeight);
            return this;
        }

        public Filler add(T value, float weight) {
            if (weight < 0) throw new IllegalArgumentException("Weight must non-negative");
            valuesMap.put(value, weight);
            return this;
        }

        public Filler addAll(T[] values) {
            for (T value : values) {
                add(value);
            }
            return this;
        }

        public Filler addAll(T[] array, float weight) {
            for (T t : array) {
                add(t, weight);
            }
            return this;
        }

        public void apply(T[] array) {
            values = Arrays.copyOf(array, valuesMap.size());
            defaultProbs = new float[valuesMap.size()];
            int i = 0;
            for (Map.Entry<T, Float> entry : valuesMap.entrySet()) {
                values[i] = entry.getKey();
                defaultProbs[i] = entry.getValue();
                i++;
            }
            reset();
        }
    }
}
