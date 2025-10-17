package ch.chaos.library;

import java.util.Random;

public class Trigo {

    private static Trigo instance;


    private Trigo() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Trigo instance() {
        if (instance == null)
            new Trigo(); // will set 'instance'
        return instance;
    }

    // CONST


    public static final int TrigoOne = 1024;

    // IMPL

    private final Random random = new Random();


    private double rad(int degree) {
        return (double) degree * Math.PI / 180.0;
    }

    /*
     * 'angle' is in degree.
     * Result should be multiplied by 'TrigoOne' to support int-only calculations
     */
    public int SIN(int angle) {
        return (int) Math.round(Math.sin(rad(angle)) * TrigoOne);
    }

    public int COS(int angle) {
        return (int) Math.round(Math.cos(rad(angle)) * TrigoOne);
    }

    public int SQRT(long val) {
        return (int) (Math.sqrt(val) + 0.5);
    }

    public int SGN(int val) {
        if (val < 0)
            return -1;
        else if (val > 0)
            return 1;
        return 0;
    }

    public int RND() { // 16-bit card
        return random.nextInt(1 << 16);
    }

    public void begin() {

    }

    public void close() {

    }
}
