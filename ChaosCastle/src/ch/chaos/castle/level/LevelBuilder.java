package ch.chaos.castle.level;

import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Function;

import ch.chaos.castle.ChaosBase;
import ch.chaos.castle.ChaosGraphics;
import ch.chaos.castle.ChaosObjects;
import ch.chaos.castle.utils.Coord;

public class LevelBuilder {
    
    private final int width;
    private final int height;
    private final Random rnd;

    private final ChaosObjects chaosObjects;
    private final ChaosBase chaosBase;
    private final ChaosGraphics chaosGraphics;

    
    public LevelBuilder(int width, int height, Random rnd) {
        this.width = width;
        this.height = height;
        this.rnd = rnd;
        this.chaosObjects = ChaosObjects.instance();
        this.chaosBase = ChaosBase.instance();
        this.chaosGraphics = ChaosGraphics.instance();
        
        chaosObjects.Clear((short) width, (short) height);
    }
    
    /**
     * Fill with random wall for floor tiles
     * @param min tile value, inclusive
     * @param max tile value, inclusive
     * @param where where to fill, such as {@link #onlyWall()} or {@link #onlyBackground()}
     * @param how random function, such as {@link #randonly()} or {@link #expRandom()}
     */
    public void fillRandom(int px, int py, int width, int height, int min, int max, 
            BiPredicate<Integer, Integer> where, Function<Integer, Integer> how) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (where.test(x + px, y + py)) {
                    int value = min + how.apply(max - min + 1);
                    put(x + px, y + py, value);
                }
            }
        }
    }
    
    public void putRandom(int px, int py, int width, int height, BiPredicate<Integer, Integer> where, int value, int amount) {
        for (int i = 0; i < amount; i++) {
            int nbTries = 100;
            int x, y;
            do {
                x = px + rnd.nextInt(width);
                y = py + rnd.nextInt(height);
                nbTries--;
            } while (nbTries > 0 && !where.test(x, y));
            if (where.test(x, y)) {
                put(x, y, value);
            }
        }
    }
    
    public void fill(int px, int py, int width, int height, int value) {
        chaosObjects.Fill((short) px, (short) py, (short) (px + width - 1), (short) (py + height - 1), (short) value);
    }
    
    /**
     * Put a wall of floor tile at the given tile coordinate
     */
    public void put(int x, int y, int value) {
        chaosObjects.Put((short) x, (short) y, (short) value);
    }
    
    public void put(Coord coord, int value) {
        put(coord.x(), coord.y(), value);
    }
    
    public boolean anywhere(int x, int y) {
        return true;
    }
    
    public boolean isWall(int x, int y) {
        return chaosObjects.OnlyWall((short) (int) x, (short) (int) y);
    }

    public boolean isBackground(int x, int y) {
        return chaosObjects.OnlyBackground((short) (int) x, (short) (int) y);
    }

    public boolean isBackgroundOrFalse(int x, int y) {
        return !chaosObjects.OnlyWall((short) (int) x, (short) (int) y);
    }
    
    public int randomly(int range) {
        return (int) chaosObjects.Rnd((short) (int) range);
    }

    public int expRandom(int range) {
        return (int) chaosObjects.ExpRandom((short) (int) range);
    }

}
