package ch.chaos.castle.utils.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.CoordPairDistance;

public class IndustryGenerator extends BinaryLevel {
    
    private final int nbPipes;
    private final Random rnd = new Random();
    
    
    public IndustryGenerator(int width, int height, int nbPipes) {
        super(width, height);
        this.nbPipes = nbPipes;
    }
    
    public void build() {
        do {
            tryBuild();
        } while (!acceptable());
        
        // Finish
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isBoundary(x, y)) {
                    walls[x][y] = true;
                }
            }
        }
    }
    
    public void tryBuild() {
//        System.out.println("Try build");
        // Initial: 5x5 empty place at the center
        int cx = width / 2;
        int cy = height / 2;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (Math.abs(x - cx) <= 2 && Math.abs(y - cy) <= 2) {
                    walls[x][y] = false;
                } else {
                    walls[x][y] = true;
                }
            }
        }

        // Add random "pipes"
        for (int k = 0; k < nbPipes; k++) {
            addRandomPipe();
        }
    }
    
    /**
     * Whether the built level is acceptable. The criteria here is that we have floors
     * in all directions: top, left, bottom and right
     */
    private boolean acceptable() {
        int bWidth = width / 10 + 1;
        int bHeight = height / 10 + 1;
        int top = getNbEmpty(bWidth, 0, width - bWidth * 2, bHeight);
        int left = getNbEmpty(0, bHeight, bWidth, height - bHeight * 2);
        int bottom = getNbEmpty(bWidth, height - bHeight, width - bWidth * 2, bHeight);
        int right = getNbEmpty(width - bWidth, bHeight, bWidth, height - bHeight * 2);
        return (top > 0 && left > 0 && bottom > 0 && right > 0);
    }
    
    private int getNbEmpty(int sx, int sy, int width, int height) {
        int result = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!isWall(x + sx, y + sy)) {
                    result++;
                }
            }
        }
        return result;
    }
    
    /**
     * Add a random "pipe". A pipe starts near an empty block. It goes in one of
     * the 4 directions for a random length, randomly change the direction, goes
     * for a random length, and so on, until either an empty block or the bounds
     * are reached.
     * <p>
     * The pipe is then filled with empty blocks using a random thickness
     */
    private void addRandomPipe() {
        // Choose an initial coordinate. Must be a wall, and 4-neighbour to an empty space
        Coord current = pickRandomStart();
        
        // Set initial direction to be away from empty block neighbour
        int dx = 0;
        int dy = 0;
        for (Coord delta : Coord.n4()) {
            if (!isWall(current.add(delta))) {
                dx = -delta.x();
                dy = -delta.y();
            }
        }
        
        // Choose initial length
        int length = rndLength();
        
        // Draw the pipe
        List<Coord> pipe = new ArrayList<>();
        pipeLoop:
        while (true) {
            // Build next line
            for (int k = 0; k < length; k++) {
                pipe.add(current);
                current = current.add(dx, dy);
                
                // Stop if bounds are reached
                if (isBoundary(current)) {
                    break pipeLoop;
                }
                
                // Stop if an empty block is reached
                if (!isWall(current)) {
                    break pipeLoop;
                }
                
                // Mark as empty and continue
                /*
                 * Although we will draw the pipe at the end (with random thickness), we draw it here with thickness
                 * 1 so that the pipe stops as soon as it is about to cross itself
                 */
                walls[current.x()][current.y()] = false;
            }
            
            // Turn, avoiding going back or in the same direction
            Coord dir = new Coord(dx, dy);
            while ((dir.x() == -dx && dir.y() == -dy) || (dir.x() == dx && dir.y() == dy)) {
                dir = Coord.n4().get(rnd.nextInt(4));
            }
            dx = dir.x();
            dy = dir.y();
            // Choose a new random length
            length = rndLength();
        }
        
        // Now draw the pipe with a random thickness
        int thickness = 1 + rnd.nextInt(6);
        for (Coord coord : pipe) {
            for (int tx = 0; tx < thickness; tx++) {
                for (int ty = 0; ty < thickness; ty++) {
                    setWall(coord.x() + tx - (thickness / 2), coord.y() + ty - (thickness / 2), false);
                }
            }
        }
    }
    
    private int rndLength() {
        return 5 + rnd.nextInt(15);
    }
    
    /**
     * Pick a random coordinate than is a wall and that is a 4-neighbour of an empty block.
     * Used a a start coordinate for pipes.
     */
    private Coord pickRandomStart() {
        List<Coord> candidates = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Coord coord = new Coord(x, y);
                if (isWall(coord) && nbSurroundingWalls4(coord) == 3) {
                    candidates.add(coord);
                }
            }
        }
        if (candidates.isEmpty())
            return null;
        int index = rnd.nextInt(candidates.size());
        return candidates.get(index);
    }
    
    public static void main(String[] args) {
        IndustryGenerator industry = new IndustryGenerator(120, 120, 60);
        industry.build();
        System.out.println(industry.toString());
        
        Coord start = new Coord(industry.getWidth() / 2, industry.getHeight() / 2);
        List<List<Coord>> distances = industry.getDistancesFrom(start);
        Map<Coord, Integer> distanceMap = industry.remapDistances(distances); 
        int maxDist1 = distances.size() - 1;
        Coord max1 = distances.get(maxDist1).get(0);
        System.out.println("Bomb: " + max1.toShortString() + " at distance " + maxDist1);
        
        distances = industry.getDistancesFrom(max1);
        int maxDist2 = distances.size() - 1;
        Coord max2 = distances.get(maxDist2).get(0);
        int bonusDist = distanceMap.get(max2);
        System.out.println("Bonus: " + max2.toShortString() + "; " + maxDist2 + " from bomb, " + bonusDist + " from start");
        
        CoordPairDistance cpd = industry.guessFarthest8Coords(new Random(), 10);
        System.out.println("Farthest: " + cpd);
    }

}
