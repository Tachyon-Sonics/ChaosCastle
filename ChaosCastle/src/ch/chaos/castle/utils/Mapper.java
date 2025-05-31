package ch.chaos.castle.utils;

import java.util.function.Function;

public class Mapper {
    
    /**
     * Given a random function returning a value from 0 to MAX (exclusive), return a new random function
     * that use the value of the given one to index the given array.
     */
    public static Function<Coord, Integer> index(Function<Coord, Integer> rangeRandom, int[] values) {
        return (Coord coord) -> {
            int index = rangeRandom.apply(coord);
            return values[index];
        };
    }

}
