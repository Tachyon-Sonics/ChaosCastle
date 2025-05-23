package ch.chaos.castle.utils.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.simplex.SimplexNoise;

public class JunkYardGenerator extends BinaryLevel {
    
    public JunkYardGenerator(int width, int height) {
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

        for (int x = 0; x < xResolution; x++) {
            for (int y = 0; y < yResolution; y++) {
                
                // Fractional simplex noise
                int nx = (int) (xStart + x * ((XEnd - xStart) / xResolution));
                int ny = (int) (yStart + y * ((yEnd - yStart) / yResolution));
                double noise = simplexNoise.getNoise(nx, ny);
                
                // Reduce near borders
                double bx = (double) ((width / 10) - Math.min(width / 10, Math.min(x, width - x - 1))) / (double) (width / 10);
                double by = (double) ((height / 10) - Math.min(height / 10, Math.min(y, height - y - 1))) / (double) (height / 10);
                double border = Math.max(bx,  by);
                double borderCorrection = border / 2.0;
                
                // Global Correction (experimental)
                double globalCorrection = -0.15;
                
                // Final value
                double value = noise + globalCorrection + borderCorrection;
                
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
        JunkYardGenerator yard = new JunkYardGenerator(80, 80);
        Random rnd = new Random();
        boolean success;
        do {
            double persistence = 0.4 + rnd.nextDouble() * 0.4;
            System.out.println("Persistence: " + persistence);
            yard.build(rnd, persistence);
            yard.drawRect(0, 0, yard.getWidth(), yard.getHeight(), true);
            Coord center = new Coord(yard.getWidth() / 2, yard.getHeight() / 2);
            boolean fromCenter = yard.retainReachableFrom(center);
            List<List<Coord>> distances = yard.getDistancesFrom(center);
            Map<Coord, Integer> distMap = yard.remapDistances(distances);
            if (fromCenter)
                System.out.println("Points: " + distMap.size() + ": " + (distMap.size() * 100 / (yard.getWidth() * yard.getHeight())) + "%");
            boolean enougPoints = distMap.size() * 2.5 > yard.getWidth() * yard.getHeight();
            success = fromCenter && enougPoints;
        } while (!success);
        yard.removeDiagonalsMakeHole();
        System.out.println(yard.toString());
    }

}
