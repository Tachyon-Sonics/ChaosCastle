package ch.chaos.library.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FpsStats {

    public final static String INTERNAL = "Internal";
    public final static String EXTERNAL = "External";

    private static final ConcurrentMap<String, FpsStats> stats = new ConcurrentHashMap<>();

    private double value;
    private int count;


    public static FpsStats instance(String name) {
        return stats.computeIfAbsent(name, (n) -> new FpsStats());
    }

    public void accumulate(double value) {
        this.value += value;
        this.count++;
    }

    public double pick() {
        if (count == 0) {
            return Double.NaN;
        } else {
            double result = value / (double) count;
            value = 0.0;
            count = 0;
            return result;
        }
    }

}
