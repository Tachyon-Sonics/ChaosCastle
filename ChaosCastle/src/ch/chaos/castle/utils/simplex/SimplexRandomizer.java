package ch.chaos.castle.utils.simplex;

import java.util.Arrays;
import java.util.Random;

import ch.chaos.castle.utils.Coord;

/**
 * Return random numbers between 0 and the given range (exclusive), based on simplex noise.
 */
public class SimplexRandomizer {
    
    private final static int HISTOGRAM_LEVELS = 3000;
    
    private final int width;
    private final int height;
    private final int range;
    
    private final SimplexNoise noiseGenerator;
    
    private int[][] values;
    
    
    public SimplexRandomizer(int width, int height, double persistance, int range, Random rnd) {
        this.width = width;
        this.height = height;
        this.range = range;
        
        this.noiseGenerator = new SimplexNoise(Math.max(width, height), persistance, rnd.nextInt());
        init();
    }
    
    private void init() {
        // Generate noise
        double[][] noise = new double[width][height];
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double value = noiseGenerator.getNoise(x, y);
                if (value < min)
                    min = value;
                if (value > max)
                    max = value;
                noise[x][y] = value;
            }
        }
        
        // Create histogram
        int[] histogram = new int[HISTOGRAM_LEVELS];
        int total = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double normalized = (noise[x][y] - min) / (max - min);
                int histogramIndex = (int) (normalized * HISTOGRAM_LEVELS);
                
                // Clamp, should not be necessary, unless we have roundoff errors
                if (histogramIndex < 0)
                    histogramIndex = 0;
                else if (histogramIndex >= HISTOGRAM_LEVELS)
                    histogramIndex = HISTOGRAM_LEVELS - 1;
                
                // Add
                histogram[histogramIndex]++;
                total++;
            }
        }
        
        // Create noise to value table
        double[] noise2value = new double[range];
        
//        System.out.println("Min: " + min + ", max: " + max);
//        System.out.println("Histogram: "+ Arrays.toString(histogram));
//        System.out.println("Total: " + total);
        
        for (int k = 0; k < range; k++) {
            int toReach = (k * total + range / 2) / range;
            int current = 0;
            int index = 0;
            while (current < toReach && index < HISTOGRAM_LEVELS) {
                current += histogram[index];
                index++;
            }
            double value = min + (max - min) * (double) index / (double) HISTOGRAM_LEVELS;
            noise2value[k] = value;
            
//            System.out.println(k + ": toReach " + toReach + " -> " + value);
        }
        
        // Fill final values
        this.values = new int[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double value = noise[x][y];
                int index = 0;
                while (index < range && noise2value[index] <= value)
                    index++;
                this.values[x][y] = index - 1;
            }
        }
    }
    
    public int valueAt(Coord coord) {
        return this.values[coord.x()][coord.y()];
    }
    
    public static void main(String[] args) {
        SimplexRandomizer sr = new SimplexRandomizer(50, 50, 0.6, 4, new Random());
        int[] histo = new int[10];
        for (int y = 0; y < 50; y++) {
            for (int x = 0; x < 50; x++) {
                int value = sr.valueAt(new Coord(x, y));
                histo[value]++;
                System.out.print(value);
            }
            System.out.println();
        }
        System.out.println("Repartition: " + Arrays.toString(histo));
    }

}
