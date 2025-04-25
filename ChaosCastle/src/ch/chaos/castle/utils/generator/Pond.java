package ch.chaos.castle.utils.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.simplex.SimplexNoise;

public class Pond extends BinaryLevel {
    
    private final double SCALE_FACTOR = 1.6;
    
    public Pond(int width, int height) {
        super(width, height);
    }
    
    public void build(Random rnd, double persistence) {
        SimplexNoise simplexNoise = new SimplexNoise(Math.max(width, height), persistence, rnd.nextInt());
        
        double xStart = 0;
        double XEnd = 500;
        double yStart = 0;
        double yEnd = 500;

        int xResolution = width;
        int yResolution = height;
        
        int xCenter = xResolution / 2;
        int yCenter = yResolution / 2;
        int xRadius = xResolution / 2;
        int yRadius = yResolution / 2;

        for (int x = 0; x < xResolution; x++) {
            for (int y = 0; y < yResolution; y++) {
                
                // Fractional simplex noise
                int nx = (int) (xStart + x * ((XEnd - xStart) / xResolution));
                int ny = (int) (yStart + y * ((yEnd - yStart) / yResolution));
                double noise = simplexNoise.getNoise(nx, ny);
                
                // Correction toward a center ellipse (Euclidean distance)
                double dx = (double) Math.abs(x - xCenter) / (double) xRadius;
                double dy = (double) Math.abs(y - yCenter) / (double) yRadius;
                double distance = Math.sqrt(dx * dx + dy * dy) / SCALE_FACTOR;
                double correction = distance * 2.0 - 1.0;
                
                // Final value
                double value = noise + correction;
                
                // Quantize
                boolean wall = (value > 0.0);
                setWall(x, y, wall);
            }
        }
    }
    
    public boolean retainReachableFrom(Coord root) {
        List<Coord> reachable = new ArrayList<>();
        boolean success = fillFlood(root, true, reachable::add);
        if (!success)
            return false;
        fillRect(0, 0, width, height, true);
        for (Coord coord : reachable) {
            setWall(coord, false);
        }
        return true;
    }
    
    public static void main(String[] args) {
        Pond pond = new Pond(100, 60);
        Random rnd = new Random();
        // (difficulty - 7) / 20 [0-0.35] + 0.1 [0.1-0.45] + rnd.nextDouble() * 0.15
        double persistence = 0.1 + rnd.nextDouble() * 0.7; // Should be based on difficulty
        System.out.println("Persistence: " + persistence);
        boolean success;
        do {
            pond.build(rnd, persistence);
            pond.drawRect(0, 0, pond.getWidth(), pond.getHeight(), true);
            success = pond.retainReachableFrom(new Coord(pond.getWidth() / 2, pond.getHeight() / 2));
        } while (!success);
        pond.removeDiagonalsMakeHole();
        pond.fillOval(32, 20, 36, 20, false);
        System.out.println(pond.toString());
    }

}
