package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.Difficulty;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChallenges;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Modifiers implements Bundlable {
    private static final String CHALLENGES = "challenges";
    private static final String DYNASTY = "dynasty";
    public boolean[] challenges;
    public String dynastyId;

    public Modifiers() {
        challenges = new boolean[Challenges.values().length];
        dynastyId = "";
        clear();
    }

    public Modifiers(boolean[] challenges) {
        this();
        System.arraycopy(challenges, 0, this.challenges, 0, challenges.length);
    }

    public Modifiers(Modifiers old) {
        challenges = old.challenges.clone();
        dynastyId = old.dynastyId;
    }

    public static Modifiers Empty() {
        return new Modifiers();
    }

    public static ChallengesDifference challengesDifference(Modifiers a, Modifiers b) {
        ChallengesDifference diff = new ChallengesDifference();
        for (Challenges chal : Challenges.values()) {
            boolean la = a.isChallenged(chal.id);
            boolean lb = b.isChallenged(chal.id);
            if (la == lb) continue;
            if (!lb) diff.removed.add(chal);
            if (!la) diff.added.add(chal);
        }
        return diff;
    }

    public Rankings.Dynasty getDynasty() {
        return Rankings.INSTANCE.getDynasty(dynastyId);
    }

    public Modifiers setDynasty(String dynastyId) {
        this.dynastyId = dynastyId;
        return this;
    }

    public void clear() {
        BArray.setFalse(challenges);
    }

    public boolean isChallenged(int id) {
        return challenges[id];
    }

    public boolean isChallenged() {
        for (boolean challenge : challenges) {
            if (challenge) return true;
        }
        return false;
    }

    public boolean canEnable(Challenges challenge) {
        for (int req : challenge.requirements) {
            if (!isChallenged(req)) return false;
        }
        return true;
    }

    public Set<Challenges> requirements(Challenges challenge) {
        Set<Challenges> set = new HashSet<>();
        for (int requirement : challenge.requirements) {
            set.add(Challenges.fromId(requirement));
        }
        return set;
    }

    public Set<Challenges> recursiveRequirements(Challenges challenge) {
        Set<Challenges> set = requirements(challenge);
        for (Challenges entry : new HashSet<>(set)) {
            set.addAll(recursiveRequirements(entry));
        }
        return set;
    }

    public boolean canDisable(Challenges challenge) {
        for (Challenges value : Challenges.values()) {
            if (isChallenged(value.id) && value.requires(challenge)) return false;
        }
        return true;
    }

    public Set<Challenges> dependants(Challenges challenge) {
        Set<Challenges> set = new HashSet<>();
        for (Challenges value : Challenges.values()) {
            if (value.requires(challenge)) set.add(value);
        }
        return set;
    }

    public Set<Challenges> recursiveDependants(Challenges challenge) {
        Set<Challenges> set = dependants(challenge);
        for (Challenges entry : new HashSet<>(set)) {
            set.addAll(recursiveDependants(entry));
        }
        return set;
    }

    public void randomize(long seed) {
        Challenges[] values = Challenges.values();
        Random.pushGenerator(seed);
        Random.shuffle(values);

        for (int i = 0; i < Random.NormalIntRange(1, values.length / 2); i++) {
            challenges[values[i].id] = true;
            for (int req : values[i].requirements) {
                challenges[req] = true;
            }
        }

        Random.popGenerator();
    }

    public void fromBigIntString(String str) {
        challenges = Challenges.fromString(new StringBuilder(new BigInteger(str, 36).toString(2)).reverse().toString());
    }

    public WndChallenges.ChallengePredicate select(int amount, int old) {
        HashSet<Challenges> selectable = new HashSet<>();
        HashSet<Challenges> canDisable = new HashSet<>();
        for (Challenges value : Challenges.values()) {
            if (isChallenged(value.id) && canDisable(value)) canDisable.add(value);
            if (!isChallenged(value.id) && canEnable(value)) selectable.add(value);
        }

        Random.pushGenerator(Dungeon.seed);

        while (selectable.size() > amount) {
            selectable.remove(Random.element(selectable));
        }
        while (canDisable.size() > old) {
            canDisable.remove(Random.element(canDisable));
        }
        selectable.addAll(canDisable);

        if (selectable.size() == 0) {
            return null;
        }
        return selectable::contains;
    }

    public Difficulty difficulty() {
        return Difficulty.align(Difficulty.calculateDifficulty(this));
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        challenges = Challenges.fromString(bundle.getString(CHALLENGES));
        dynastyId = bundle.contains(DYNASTY) ? bundle.getString(DYNASTY) : "";
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(CHALLENGES, Challenges.saveString(challenges));
        bundle.put(DYNASTY, dynastyId);
    }

    public static class ChallengesDifference {
        public List<Challenges> removed;
        public List<Challenges> added;

        public ChallengesDifference() {
            this.removed = new ArrayList<>();
            this.added = new ArrayList<>();
        }

        public ChallengesDifference(List<Challenges> removed, List<Challenges> added) {
            this.removed = removed;
            this.added = added;
        }
    }
}
