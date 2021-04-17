package com.shatteredpixel.shatteredpixeldungeon.utils;

/*
  Generates random names based on Markov chains of characters.
  @author Christopher Siu (cesiu)
 * @version 29 Jun 2016
 */

import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Arrays;

public class MarkovNameGen
{
    // all the lowercase characters this generator will handle
    public static final char[] LOWERCHARS = {'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z'};
    // all the vowels
    public static final char[] VOWELS = {'a', 'e', 'i', 'o', 'u', 'y'};
    // all the consonants
    public static final char[] CONSONANTS = {'b', 'c', 'd', 'f', 'g', 'h', 'j',
            'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'};

    // map of lowercase characters to states
    private LinkedHashMap<Character, CharState> states;
    // the special state for the beginning of a name
    private CharState beginState;
    // a random number generator to generate name lengths
    private Random rand;
    // the range of lengths to pick from
    private int max, min;

    /**
     * Constructs a name generator.
     * @param min the minimum length of a generated name, inclusive
     * @param max the maximum length of a generated name, exclusive
     */
    public MarkovNameGen(int min, int max)
    {
        rand = new Random();
        this.min = min;
        this.max = max;
        initGen();
    }

    /**
     * Constructs a seeded name generator.
     * @param min the minimum length of a generated name, inclusive
     * @param max the maximum length of a generated name, exclusive
     * @param seed the seed for the random number generator
     */
    public MarkovNameGen(int min, int max, long seed)
    {
        rand = new Random(seed);
        this.min = min;
        this.max = max;
        initGen();
    }

    /**
     * Initializes a name generator.
     */
    public void initGen()
    {
        // Populate the map with the initial character set.
        states = new LinkedHashMap<Character, CharState>();
        for (char c : LOWERCHARS) {
            states.put(c, new CharState(c, rand));
        }

        LinkedHashMap<CharState, Integer> initState;
        // For every state do:
        for (CharState curState : states.values()) {
            // Create an initial state where consonants and vowels alternate, with
            // an equal chance of picking each next option.
            initState = new LinkedHashMap<CharState, Integer>();

            // If it's a vowel, add all the consonants to its inital state.
            if (Arrays.binarySearch(VOWELS, curState.getChar()) > -1) {
                for (char c : VOWELS) {
                    initState.put(states.get(c), 0);
                }
                for (char c : CONSONANTS) {
                    initState.put(states.get(c), 1);
                }
                // Don't allow two 'y's in a row.
                if (curState.getChar() == 'y') {
                    initState.put(states.get('y'), 0);
                    curState.setTotal(CONSONANTS.length - 1);
                }
                else {
                    curState.setTotal(CONSONANTS.length);
                }
                curState.setMap(initState);
            }
            // Else add all the vowels to its initial state.
            else {
                for (char c : CONSONANTS) {
                    initState.put(states.get(c), 0);
                }
                for (char c : VOWELS) {
                    initState.put(states.get(c), 1);
                }
                curState.setTotal(VOWELS.length);
                curState.setMap(initState);
            }
        }

        // The beginning state has an equal chance of picking any letter.
        beginState = new CharState(' ', rand);
        initState = new LinkedHashMap<CharState, Integer>();
        for (CharState state : states.values()) {
            initState.put(state, 1);
        }
        beginState.setTotal(LOWERCHARS.length);
        beginState.setMap(initState);

        // Add some common combinations.
        addName("sh");
        addName("th");
        addName("tr");
        addName("ch");
        addName("qu");
        addName("br");
        addName("dr");
        addName("cr");
        addName("thr");
        addName("pr");
        addName("ui");
        addName("ae");
        addName("ea");
        addName("fr");
        addName("gr");
        addName("cl");
        addName("wr");
        addName("squ");
        addName("pl");
        addName("bl");
        addName("gl");
        addName("kr");
    }

    /**
     * Adds a name to the chain.
     * @param name the name to be added
     */
    public void addName(String name)
    {
        // Sanitize the string.
        name = name.replaceAll("\\s+", "").toLowerCase();

        // Update the beginning state.
        beginState.addNext(states.get(name.charAt(0)));

        // For every char except the last, tell the corresponding state which
        // char came after it.
        for (int i = 0; i < name.length() - 1; i++) {
            if (!states.containsKey(name.charAt(i))
                    || ! states.containsKey(name.charAt(i + 1))) {
                throw new RuntimeException("Error: invalid character in sample.");
            }
            states.get(name.charAt(i)).addNext(states.get(name.charAt(i + 1)));
        }
    }

    /**
     * Resets the frequencies of a letter combination to zero.
     * @param name the name to be removed
     */
    public void removeName(String name)
    {
        // Sanitize the string.
        name = name.replaceAll("\\s+", "").toLowerCase();

        // For every char except the last, tell the corresponding state which
        // char should be reset.
        for (int i = 0; i < name.length() - 1; i++) {
            if (!states.containsKey(name.charAt(i))
                    || ! states.containsKey(name.charAt(i + 1))) {
                throw new RuntimeException("Error: invalid character in sample.");
            }
            states.get(name.charAt(i)).resetNext(states.get(name.charAt(i + 1)));
        }
    }

    /**
     * Generates a name from the chain.
     * @return the generated name
     */
    public String getName()
    {
        // Pick an initial character.
        CharState curState = beginState.next();
        String retStr = "" + curState.getChar();

        // For each remaining spot in the desired name do:
        for (int i = rand.nextInt(max - min) + min; i > 1; i--) {
            // Query the current state for the random next state.
            curState = curState.next();
            retStr += curState.getChar();
        }

        return retStr;
    }

    /**
     * Returns a string representation of all the characters and their states.
     * @return the string
     */
    public String toString()
    {
        String retStr = "";

        for (CharState state : states.values()) {
            retStr += state.toString();
        }

        return retStr;
    }

    /**
     * Represents one character state.
     */
    private class CharState
    {
        // the character represented by this state
        char curChar;
        // the total number of next states seen
        int total;
        // a map of states to their quantities
        LinkedHashMap<CharState, Integer> nexts;
        // a random number generator for picking the next state
        Random rand;

        /**
         * Constructs a state representing a character.
         * @param curChar the character
         * @param rand a random number generator
         */
        private CharState(char curChar, Random rand)
        {
            this.curChar = curChar;
            this.rand = rand;
        }

        /**
         * Returns the character represented by this state.
         * @return the character
         */
        private char getChar()
        {
            return curChar;
        }

        /**
         * Sets the total number of next states seen.
         * @param total the new total
         */
        private void setTotal(int total)
        {
            this.total = total;
        }

        /**
         * Sets the map of states to quantities.
         * @param nexts the new map
         */
        private void setMap(LinkedHashMap<CharState, Integer> nexts)
        {
            this.nexts = nexts;
        }

        /**
         * Returns a random next state.
         * @return the next state.
         */
        private CharState next()
        {
            // Generate a random number within the total number of states seen.
            int choice = rand.nextInt(total);
            int chance = 0;

            // For each state in the map do:
            for (CharState posNext : nexts.keySet()) {
                // Add the chance of that state.
                chance += nexts.get(posNext);
                // If the choice is within the chance, then return the state.
                if (choice < chance) {
                    return posNext;
                }
            }

            // If we got this far, we somehow generated a number too large.
            throw new RuntimeException("Error: '" + curChar + "' invalid choice ("
                    + choice + ") for total of " + total + ".");
        }

        /**
         * Indicates that a new next state has been seen.
         * @param newNext the state that was seen
         */
        private void addNext(CharState newNext)
        {
            // Increment that state's quantity and the total.
            nexts.put(newNext, nexts.get(newNext) + 1);
            ++total;
        }

        /**
         * Indicates that a next state should be reset to zero.
         * @param oldNext the state to be reset
         */
        private void resetNext(CharState oldNext)
        {
            // Reset the state's quantity and remove it from the total.
            total -= nexts.get(oldNext);
            nexts.put(oldNext, 0);
        }

        /**
         * Returns a string representation of this state and its nexts.
         * @return the string
         */
        public String toString() {
            String retStr = "" + curChar + ":\n   ";

            for (CharState state : nexts.keySet()) {
                retStr += "[" + state.getChar() + ":" + nexts.get(state) + "]";
            }

            return retStr + "\n";
        }
    }
}