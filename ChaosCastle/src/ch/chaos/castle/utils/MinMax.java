package ch.chaos.castle.utils;

import java.util.Random;

/**
 * @param min inclusive
 * @param max inclusive
 */
public record MinMax(int min, int max) {

    public static MinMax value(int val) {
        return new MinMax(val, val);
    }
    
    public int pick(Random rnd) {
        if (min == max)
            return min;
        return min + rnd.nextInt(max - min + 1);
    }
}
