package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Deck<T> implements Bundlable {

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

    public void reset() {
        probs = defaultProbs.clone();
    }

    protected Filler filler() {
        return new Filler();
    }

    private static final String PROBS = "probs";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        probs = bundle.getFloatArray(PROBS);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(PROBS, probs);
    }

    public class Filler {
        private Map<T, Float> valuesMap = new HashMap<>();
        private float defaultWeight = 1f;

        public Filler defaultWeight(float value) {
            defaultWeight = value;
            return this;
        }

        public Filler add(T value) {
            valuesMap.put(value, defaultWeight);
            return this;
        }

        public Filler add(T[] values) {
            for (T value : values) {
                add(value);
            }
            return this;
        }

        public Filler put(T value, float weight) {
            valuesMap.put(value, weight);
            return this;
        }

        public Filler flat(T[] array, float weight) {
            for (T t : array) {
                put(t, weight);
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
