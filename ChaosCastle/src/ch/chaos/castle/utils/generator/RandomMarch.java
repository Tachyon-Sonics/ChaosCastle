package ch.chaos.castle.utils.generator;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import ch.chaos.castle.utils.Coord;

public class RandomMarch extends BinaryLevel {
    
    private final int length;
    private final int minHoles;
    private final int maxHoles;
    private final Random rnd = new Random();
    private int nbHoles;
    

    public RandomMarch(int width, int height, int length, int minHoles, int maxHoles) {
        super(width, height);
        this.length = length;
        this.minHoles = minHoles;
        this.maxHoles = maxHoles;
    }
    
    public void build() {
        do {
            System.out.println("Building...");
            build0();
            removeDiagonalsMakeHole();
            BinaryLevel filler = copy();
            AtomicInteger nbOutsideWalls = new AtomicInteger();
            filler.fillFlood(new Coord(0, 0), false, (c) -> nbOutsideWalls.incrementAndGet());
            nbHoles = width * height - nbOutsideWalls.get() - length;
        } while (nbHoles < minHoles || nbHoles > maxHoles);
    }
    
    private void build0() {
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
        RandomMarch march = new RandomMarch(80, 80, 1500, 200, Integer.MAX_VALUE); // (200, 300) - (600, Inf) according to difficulty
        march.build();
        System.out.println(march);
        System.out.println("NB holes: " + march.getNbHoles());
        
        {
            Coord[] pair = march.guessFarthestCoords(new Random(), 10);
            System.out.println("Farthest (estimated): " + Arrays.toString(pair) + " : " + march.getDistancesFrom(pair[0]).size());
        }

        {
            Coord[] pair = march.computeFarthestCoords(new Random());
            System.out.println("Farthest (computed): " + Arrays.toString(pair) + " : " + march.getDistancesFrom(pair[0]).size());
        }
    }

}
