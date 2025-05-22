package ch.chaos.castle.utils.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.CoordPairDistance;

public class RandomMarch extends BinaryLevel {
    
    private final int length;
    private final int minHoles;
    private final int maxHoles;
    private final int minFarthestDistance;
    private final Random rnd = new Random();
    private int nbHoles;
    

    public RandomMarch(int width, int height, int length, int minHoles, int maxHoles, int minFarthestDistance) {
        super(width, height);
        this.length = length;
        this.minHoles = minHoles;
        this.maxHoles = maxHoles;
        this.minFarthestDistance = minFarthestDistance;
    }
    
    public void build() {
        CoordPairDistance farthest;
        do {
            System.out.println("Building...");
            build0();
            removeDiagonalsMakeHole();
            
            // Get number of holes
            BinaryLevel filler = copy();
            AtomicInteger nbOutsideWalls = new AtomicInteger();
            filler.fillFlood(new Coord(0, 0), false, (c) -> nbOutsideWalls.incrementAndGet());
            nbHoles = width * height - nbOutsideWalls.get() - length;
            
            // Get farthest points (approximate)
            farthest = guessFarthest8Coords(rnd, 10);
            
            // Check if accepted
        } while (nbHoles < minHoles || nbHoles > maxHoles || farthest.distance() < minFarthestDistance);
    }
    
    /**
     * Build a level by starting from the center, and repeatedly randomly walking in one of the 4
     * directions.
     * <p>
     * Slightly favor directions toward the center. Do not prevent walking to the same place multiple
     * times, however, only stop once {@link #length} distinct places have been reached.
     */
    void build0() {
        fillRect(0, 0, width, height, true);
        int px = width / 2;
        int py = height / 2;
        Coord current = new Coord(px, py);
        setWall(current, false);
        int nbFloor = 1;
        while (nbFloor < length) {
            Coord delta;
            do {
                delta = pickDelta(current, rnd);
            } while (isBoundary(current.add(delta)));
            current = current.add(delta);
            if (isWall(current)) {
                setWall(current, false);
                nbFloor++;
            }
        }
    }
    
    /**
     * Alternate version of {@link #build0()} that prevents walking to the same place twice
     * (and restarts from random location if trapped in a dead-end).
     */
    void build1() {
        fillRect(0, 0, width, height, true);
        int px = width / 2;
        int py = height / 2;
        Coord current = new Coord(px, py);
        List<Coord> marched = new ArrayList<>();
        marched.add(current);
        setWall(current, false);
        int nbFloor = 1;
        while (nbFloor < length) {
            Coord delta;
            do {
                delta = pickWallDelta(current, rnd);
                if (delta == null) {
                    // Nowhere to go. Restart from a random previous coord
                    current = marched.get(rnd.nextInt(marched.size()));
                }
            } while (delta == null);
            current = current.add(delta);
            setWall(current, false);
            marched.add(current);
            nbFloor++;
        }
    }
    
    private Coord pickDelta(Coord current, Random rnd) {
        // Create probability for each delta, according to how far from outside it is
        double[] probs = new double[4];
        double total = 0.0;
        int index = 0;
        for (Coord delta : Coord.n4()) {
            double nbToOutside = nbToOutside(current, delta);
            /*
             * Probability of 'delta' is based on distance to border + some constants.
             * This progressively decrease the probability as we get nearer to the border,
             * but in a sub-linear way.
             * 
             * Experimental tests showed that a 100% linear version (without the below addition)
             * tend to create uninteresting clustered blob in the center.
             * 
             * In fact, making the probability decrease only slightly (by adding a large number
             * below) seems to maximize complex structures.
             */
            double prob = nbToOutside + (width + height) * 10.0;
            if (nbToOutside < (width + height) / 15.0) {
                /*
                 * Decrease the probability if we are close to the border, to prevent creation
                 * of clusters "glued" to the border.
                 */
                prob *= 0.8;
            }
            probs[index] = prob;
            index++;
            total += prob;
        }
        
        // Pick
        double value = rnd.nextDouble() * total;
        index = 0;
        double histo = probs[index];
        while (value > histo && index < 3) {
            index++;
            histo += probs[index];
        }
        return Coord.n4().get(index);
    }
    
    private Coord pickWallDelta(Coord current, Random rnd) {
        // Create probability for each delta, according to how far from outside it is
        List<Double> probs = new ArrayList<>();
        List<Coord> deltas = new ArrayList<>();
        double total = 0.0;
        for (Coord delta : Coord.n4()) {
            if (isWall(current.add(delta)) && !isBoundary(current.add(delta))) {
                double nbToOutside = nbToOutside(current, delta);
                /*
                 * Probability of 'delta' is based on distance to border + some constants.
                 * This progressively decrease the probability as we get nearer to the border,
                 * but in a sub-linear way.
                 * 
                 * Experimental tests showed that a 100% linear version (without the below addition)
                 * tend to create uninteresting clustered blob in the center.
                 * 
                 * In fact, making the probability decrease only slightly (by adding a large number
                 * below) seems to maximize complex structures.
                 */
                double prob = nbToOutside + (width + height) * 10.0;
                if (nbToOutside < (width + height) / 15.0) {
                    /*
                     * Decrease the probability if we are close to the border, to prevent creation
                     * of clusters "glued" to the border.
                     */
                    prob *= 0.8;
                }
                
                deltas.add(delta);
                probs.add(prob);
                total += prob;
            }
        }
        
        // Pick
        assert deltas.size() == probs.size();
        if (deltas.isEmpty())
            return null; // Nowhere to go
        double value = rnd.nextDouble() * total;
        int index = 0;
        double histo = probs.get(index);
        while (value > histo && index + 1 < probs.size()) {
            index++;
            histo += probs.get(index);
        }
        return deltas.get(index);
    }
    
    /**
     * Check how many times 'delta' can be added to 'coord' until we hit the border
     */
    private int nbToOutside(Coord coord, Coord delta) {
        int result = 0;
        while (!isBoundary(coord)) {
            result++;
            coord = coord.add(delta);
        }
        return result;
    }
    
    public int getNbHoles() {
        return nbHoles;
    }
    
    public static void main(String[] args) {
        RandomMarch march = new RandomMarch(80, 80, 2500, 600, Integer.MAX_VALUE, 80); // (200, 300) - (600, Inf) according to difficulty
        march.build();
        System.out.println(march);
        System.out.println("NB holes: " + march.getNbHoles());
        
        {
            CoordPairDistance pair = march.guessFarthest8Coords(new Random(), 10);
            System.out.println("Farthest (estimated): " + pair);
        }

        {
            CoordPairDistance cpd = march.computeFarthest8Coords();
            System.out.println("Farthest (computed): " + cpd);
        }
    }

}
