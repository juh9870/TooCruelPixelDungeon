package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.utils.Difficulty;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChallenges;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Modifiers implements Bundlable {
    private static final String CHALLENGES = "challenges";
    private static final String DYNASTY = "dynasty";
    public int[] challenges;
    public String dynastyId;

    public Modifiers() {
        challenges = new int[Challenges.values().length];
        dynastyId="";
        clear();
    }

    public Modifiers(int[] challenges) {
        this();
        System.arraycopy(challenges, 0,this.challenges, 0, challenges.length);
    }

    public Modifiers(Modifiers old){
        challenges=old.challenges.clone();
        dynastyId=old.dynastyId;
    }

    public static Modifiers Empty(){
        return new Modifiers();
    }

    public Rankings.Dynasty getDynasty() {
        return Rankings.INSTANCE.getDynasty(dynastyId);
    }

    public Modifiers setDynasty(String dynastyId) {
        this.dynastyId = dynastyId;
        return this;
    }

    public void clear() {
        int l = Challenges.values().length;
        for (int i = 0; i < l; i++) {
            challenges[i] = 0;
        }
    }

    public boolean isChallenged(int id) {
        return challengeTier(id) > 0;
    }

    public boolean isChallenged() {
        for (Integer challenge : challenges) {
            if (challenge > 0) return true;
        }
        return false;
    }

    public void randomize(long seed) {
        Challenges[] values = Challenges.values();
        Random.pushGenerator(seed);
        Random.shuffle(values);

        for (int i = 0; i < Random.Int(1, values.length); i++) {
            challenges[values[i].ordinal()] = Random.Int(1, values[i].maxLevel + 1);
        }

        Random.popGenerator();
    }

    public static class ChallengesDifference {
        public List<Challenges.Entry> removed;
        public List<Challenges.Entry> added;

        public ChallengesDifference() {
            this.removed = new ArrayList<>();
            this.added = new ArrayList<>();
        }
        public ChallengesDifference(List<Challenges.Entry> removed, List<Challenges.Entry> added) {
            this.removed = removed;
            this.added = added;
        }
    }

    public static ChallengesDifference challengesDifference(Modifiers a, Modifiers b){
        ChallengesDifference diff = new ChallengesDifference();
        for (Challenges chal : Challenges.values()) {
            int la = a.challengeTier(chal.ordinal());
            int lb = b.challengeTier(chal.ordinal());
            if(la==lb)continue;
            if(la>lb){
                for (int i = la; i > lb; i--) {
                    diff.removed.add(new Challenges.Entry(chal,i));
                }
            }
            if(la<lb){
                for (int i = la; i < lb; i++) {
                    diff.added.add(new Challenges.Entry(chal,i+1));
                }
            }
        }
        return diff;
    }

    public WndChallenges.ChallengePredicate select(int amount,int old) {
        HashMap<Challenges, Integer> selected = new HashMap<Challenges, Integer>();
        HashMap<Challenges, Integer> enabled = new HashMap<Challenges, Integer>();
        for (Challenges value : Challenges.values()) {
            int curTier = challengeTier(value.ordinal());
            for (int i = 1; i <= value.maxLevel; i++) {
                if (curTier >= i) {
                    enabled.put(value,i);
                    continue;
                }
                if (curTier < i - 1) continue;
                selected.put(value, i);
                break;
            }
        }

        Random.pushGenerator(Dungeon.seed);

        while (selected.size() > amount) {
            selected.remove(Random.element(selected.keySet()));
        }
        while (enabled.size() > old) {
            enabled.remove(Random.element(enabled.keySet()));
        }
        for (Challenges chal : enabled.keySet()) {
            selected.put(chal,enabled.get(chal));
        }

        if (selected.size() == 0) {
            return null;
        }
        return (challenge, level) -> selected.containsKey(challenge) && (level < 0 || selected.get(challenge) == level);
    }

    public int challengeTier(int id) {
        return challenges[id];
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
}
