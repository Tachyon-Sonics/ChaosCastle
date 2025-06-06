package ch.chaos.castle.utils.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import ch.chaos.castle.utils.Coord;

public class TwilightGenerator extends BinaryLevel {
    
    private BinaryLevel reachable;
    private Coord exit;
    private Coord indicator;
    

    public TwilightGenerator(int width, int height) {
        super(width, height);
    }
    
    public void build(Random rnd) {
        boolean success;
        do {
            success = tryBuild(rnd);
//            if (!success)
//                System.out.println("Retry...");
        } while (!success);
    }
    
    private boolean tryBuild(Random rnd) {
        fillRect(0, 0, width, height, false);
        drawRect(0, 0, width, height, true);
        
        int circleness = rnd.nextInt(8);
        int nbFailures = 0;
        while (nbFailures < 2) {
            BinaryLevel blob = createBlob(rnd, circleness);
            boolean success = placeBlob(blob, rnd);
            if (success) {
                nbFailures = 0;
            } else {
                nbFailures++;
            }
        }
        
        // Keep only reachable, and build walls
        List<List<Coord>> distances = getDistancesFrom(new Coord(1, 1));
        Map<Coord, Integer> distMap = remapDistances(distances);
        Set<Coord> path = new HashSet<>(distMap.keySet());
        fillRect(0, 0, width, height, false);
        reachable = new BinaryLevel(width, height);
        reachable.fillRect(0, 0, width, height, true);
        for (Coord coord : path) {
            reachable.setWall(coord, false);
            for (Coord delta : Coord.n8()) {
                Coord n = coord.add(delta);
                if (!path.contains(n)) {
                    setWall(n, true);
                }
            }
        }
        
        // Check if ok
        int x = 1;
        while (!reachable.isWall(x, 1)) {
            x++;
        }
        while (reachable.isWall(x, 1)) {
            x++;
            if (x >= width)
                return false;
        }
        exit = new Coord(x, 1);
        
        int y = 1;
        while (!reachable.isWall(1, y)) {
            y++;
        }
        while (reachable.isWall(1, y)) {
            y++;
            if (y >= height)
                return false;
        }
        indicator = new Coord(1, 2);
        if (reachable.isWall(indicator))
            return false;
        
        return true;
    }
    
    private BinaryLevel createBlob(Random rnd, int circleness) {
        int itemSize = 3 + rnd.nextInt(5); // 3 .. 7
        int size = itemSize * 2;
        BinaryLevel blob = new BinaryLevel(size, size);
        int nbItems = 2 + rnd.nextInt(4); // 2 .. 5
        double angle = (double) rnd.nextInt(360) * Math.PI * 2.0 / 360.0;
        boolean circle = rnd.nextInt(8) < circleness;
        double div = 4.0;
        if (nbItems == 2 && circle)
            div = 5.0;
        for (int k = 0; k < nbItems; k++) {
            double cx = (double) size / 2.0 + Math.cos(angle) * size / div;
            double cy = (double) size / 2.0 + Math.sin(angle) * size / div;
            int sx = (int) (cx - (double) itemSize / 2.0 + 0.5);
            int sy = (int) (cy - (double) itemSize / 2.0 + 0.5);
            if (circle) {
                blob.fillOval(sx, sy, itemSize, itemSize, true);
            } else {
                blob.fillRect(sx, sy, itemSize, itemSize, true);
            }
            angle += Math.PI * 2.0 / (double) nbItems;
        }
        return blob;
    }
    
    private boolean placeBlob(BinaryLevel blob, Random rnd) {
        Coord start = new Coord(1, 1);
        int nbReachableBefore = nbReachableFrom(this, start);
        int nbBlobWalls = blob.getNbWalls();
        
        // Gather candidates for placement
        List<Coord> candidates = new ArrayList<>();
        for (int x = 1; x < width - blob.getWidth(); x++) {
            for (int y = 1; y < height - blob.getHeight(); y++) {
                int nbTouching = 0;
                int nbOverlappig = 0;
                for (int bx = 0; bx < blob.getWidth(); bx++) {
                    for (int by = 0; by < blob.getHeight(); by++) {
                        if (blob.isWall(bx, by)) {
                            Coord coord = new Coord(x + bx, y + by);
                            if (isWall(coord)) {
                                nbOverlappig++;
                            } else {
                                for (Coord delta : Coord.n4()) {
                                    if (isWall(coord.add(delta))) {
                                        nbTouching++;
                                    }
                                }
                            }
                        }
                    }
                }
                if (nbOverlappig < 3 && nbTouching > 0) {
                    candidates.add(new Coord(x, y));
                }
            }
        }
        
        // Place
        while (!candidates.isEmpty()) {
            int index = rnd.nextInt(candidates.size());
            Coord where = candidates.remove(index);
            BinaryLevel result = this.copy();
            result.drawShape(blob, where, true);
            result.removeDiagonalsMakeWall();
            int nbReachableAfter = nbReachableFrom(result, start);
            if (nbReachableAfter + nbBlobWalls + 10 > nbReachableBefore) {
                // Success. Place it here
                drawShape(blob, where, true);
                removeDiagonalsMakeWall();
                return true;
            }
        }
        
        return false;
    }
    
    private int nbReachableFrom(BinaryLevel level, Coord start) {
        List<List<Coord>> distances = level.getDistancesFrom(start);
        Map<Coord, Integer> reachable = level.remapDistances(distances);
        return reachable.size();
    }
    
    public BinaryLevel getReachable() {
        return reachable;
    }
    
    public Coord getExit() {
        return exit;
    }
    
    public Coord getIndicator() { // Place for a "single speed" bonus?
        return indicator;
    }

    public static void main(String[] args) {
        TwilightGenerator generator = new TwilightGenerator(80, 80);
        generator.build(new Random());
//        System.out.println(generator);
        System.out.println(generator.getReachable().toString());
        System.out.println("EXIT: " + generator.getExit().toShortString());
        System.out.println("Size: " + generator.getReachable().getNbHoles());
    }

}
