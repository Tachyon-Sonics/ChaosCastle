package ch.chaos.castle.utils.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.chaos.castle.utils.Coord;

public class Park extends BinaryLevel {
    
    private final int minSize;
    private final int maxSize;
    private final int minRingSize;
    private final int maxRingSize;
    
    private List<Coord> exits = new ArrayList<>();
    private Coord start;

    
    /**
     * @param minSize minimum blob size
     * @param maxSize maximum blob size
     */
    public Park(int width, int height, int minSize, int maxSize, int minRingSize, int maxRingSize) {
        super(width, height);
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.minRingSize = minRingSize;
        this.maxRingSize = maxRingSize;
    }
    
    public void build() {
        fillRect(0, 0, width, height, true);
        Random rnd = new Random();
        
        // Initial blob
        DoubleCyclo init = createBlob(rnd);
        placeBlobRandomly(init, rnd, this::isInsideBounds);
        
        // Additional blobs
        int nbFailures = 0;
        do {
            DoubleCyclo blob = createBlob(rnd);
            boolean success = placeBlobRandomly(blob, rnd, this::isTouchingAnother);
            if (!success)
                nbFailures++;
        } while (nbFailures < 5);
        
        removeDiagonalsMakeWall();
        
        // Create exit
        exits.add(firstHole(new Coord(1, 1), new Coord(1, 1)));
        exits.add(firstHole(new Coord(width - 2, 1), new Coord(-1, 1)));
        exits.add(firstHole(new Coord(1, height - 2), new Coord(1, -1)));
        exits.add(firstHole(new Coord(width - 2, height - 2), new Coord(-1, -1)));
        
        // Create start
        start = firstHole(new Coord(width / 2, height / 2), Coord.n8());
    }
    
    private DoubleCyclo createBlob(Random rnd) {
        while (true) {
            int size = minSize + rnd.nextInt(maxSize - minSize + 1);
            DoubleCyclo blob = new DoubleCyclo(size, 1, 1, (period1, period2) -> true);
            blob.build();
            boolean success = blob.fillFlood(new Coord(blob.getWidth() / 2, blob.getHeight() / 2), false);
            if (success) {
                int distance = minRingSize + rnd.nextInt(maxRingSize - minRingSize + 1);
                blob.fillInterior(distance);
                return blob;
            }
        }
    }
    
    private boolean placeBlobRandomly(BinaryLevel blob, Random rnd, IBlobCondition condition) {
        List<Coord> candidates = new ArrayList<>();
        for (int x = -minSize; x < width; x++) {
            for (int y = -minSize; y < height; y++) {
                Coord where = new Coord(x, y);
                if (condition.isValid(blob, where)) {
                    candidates.add(where);
                }
            }
        }
        if (candidates.isEmpty()) {
            return false;
        }
        int index = rnd.nextInt(candidates.size());
        Coord where = candidates.get(index);
        placeBlobAt(blob, where);
        return true;
    }
    
    private void placeBlobAt(BinaryLevel blob, Coord where) {
        for (int x = 0; x < blob.getWidth(); x++) {
            for (int y = 0; y < blob.getHeight(); y++) {
                if (!blob.isWall(x, y)) {
                    setWall(where.add(x, y), false);
                }
            }
        }
    }
    
    @FunctionalInterface
    static interface IBlobCondition {
        
        public boolean isValid(BinaryLevel blob, Coord where);
    
    }
    
    private boolean isInsideBounds(BinaryLevel blob, Coord where) {
        for (int x = 0; x < blob.getWidth(); x++) {
            for (int y = 0; y < blob.getHeight(); y++) {
                if (!blob.isWall(x, y)) {
                    if (isBoundary(where.add(x, y)))
                        return false;
                }
            }
        }
        return true;
    }
    
    private boolean isTouchingAnother(BinaryLevel blob, Coord where) {
        if (!isInsideBounds(blob, where))
            return false;
        boolean touch = false;
        for (int x = 0; x < blob.getWidth(); x++) {
            for (int y = 0; y < blob.getHeight(); y++) {
                if (!blob.isWall(x, y)) {
                    for (Coord delta : Coord.n4()) {
                        if (!isWall(where.add(x, y))) {
                            return false; // Must touch, but not cross
                        }
                        Coord target = where.add(x, y).add(delta);
                        if (!isWall(target)) {
                            touch = true;
                        }
                    }
                }
            }
        }
        return touch;
    }
    
    private Coord firstHole(Coord start, Coord delta) {
        while (isWall(start)) {
            start = start.add(delta);
        }
        return start;
    }
    
    private Coord firstHole(Coord start, List<Coord> deltas) {
        List<Coord> currents = new ArrayList<>();
        for (int i = 0; i < deltas.size(); i++) {
            currents.add(start);
        }
        
        int max = 100;
        while (max > 0) {
            assert currents.size() == deltas.size();
            for (int i = 0; i < currents.size(); i++) {
                Coord current = currents.get(i);
                if (!isWall(current)) {
                    return current;
                }
                Coord delta = deltas.get(i);
                currents.set(i, current.add(delta));
            }
            max--;
        }
        return null;
    }
    
    public List<Coord> getExits() {
        return exits;
    }
    
    public Coord getStart() {
        return start;
    }

    public static void main(String[] args) {
        Park blobs = new Park(100, 100, 20, 40, 2, 9); // '9' -> 11 - (difficulty - 3);
        blobs.build();
        System.out.println(blobs.toString());
        System.out.println("Start: " + blobs.getStart() + "; Exists: " + Coord.toShortString(blobs.getExits()));
    }

}
