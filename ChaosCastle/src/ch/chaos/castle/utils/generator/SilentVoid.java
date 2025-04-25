package ch.chaos.castle.utils.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.chaos.castle.utils.Coord;

public class SilentVoid extends BinaryLevel {
    
    private final static int OUTER_SIZE = 9;
    private final static int PAD_SIZE = 5;
    
    private final int nbHoles;
    private final int nbEllipses;
    private final int minEllipseSize;
    private final int maxEllipseSize;
    
    private Coord entry;
    

    public SilentVoid(int width, int height, int nbHoles, int nbEllipses, int minEllipseSize, int maxEllipseSize) {
        super(width, height);
        this.nbHoles = nbHoles;
        this.nbEllipses = nbEllipses;
        this.minEllipseSize = minEllipseSize;
        this.maxEllipseSize = maxEllipseSize;
    }
    
    public void build() {
        fillRect(0, 0, width, height, true);
        
        // O--O holes
        Random rnd = new Random();
        for (int k = 0; k < nbHoles; k++) {
            addRandomHole(rnd);
        }
        
        // Exterior barriers
        BinaryLevel outer = this.copy();
        outer.fillFlood(new Coord(0, 0), false);
        
        List<Coord> toClear = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Coord coord = new Coord(x, y);
//                if (!outer.isWall(coord)) {
                    boolean border = false;
                    for (Coord delta : Coord.n8()) {
                        if (!isWall(coord.add(delta))) {
                            border = true;
                        }
                    }
                    if (!border) {
                        toClear.add(coord);
                    }
//                }
            }
        }
        
        // Ellipses
        int placed = 0;
        for (int k = 0; k < nbEllipses; k++) {
            int w = minEllipseSize + rnd.nextInt(maxEllipseSize - minEllipseSize + 1);
            int h = minEllipseSize + rnd.nextInt(maxEllipseSize - minEllipseSize + 1);
            BinaryLevel ellipse = new BinaryLevel(w, h);
            ellipse.fillOval(0, 0, w, h, true);
            
            BinaryLevel mask = new BinaryLevel(w + 2, h + 2);
            mask.drawShape(ellipse, new Coord(1, 1), true);
            mask.growWalls8();
            
            List<Coord> candidates = randomPlacesFor(mask, false, 0, 0, width, height);
            if (!candidates.isEmpty()) {
                int index = rnd.nextInt(candidates.size());
                Coord position = candidates.get(index);
                drawShape(ellipse, position.add(1, 1), true);
                placed++;
            }
        }
        System.out.println("" + placed + " ellipses placed");
        
        removeDiagonalsMakeHole();
        
        // Entry
        entry = new Coord(width / 2, 0);
        while (isWall(entry)) {
            entry = entry.add(0, 1);
            if (entry.y() >= height - 1) {
                entry = null;
                break;
            }
        }
        
        // Clear exterior
        for (Coord coord : toClear) {
            setWall(coord, false);
        }
    }
    
    public Coord getEntry() {
        return entry;
    }

    private void addRandomHole(Random rnd) {
        int sx = PAD_SIZE + rnd.nextInt(width - PAD_SIZE * 2);
        int sy = PAD_SIZE + rnd.nextInt(height - PAD_SIZE * 2);
        int ex = PAD_SIZE + rnd.nextInt(width - PAD_SIZE * 2);
        int ey = PAD_SIZE + rnd.nextInt(height - PAD_SIZE * 2);
        drawLine(sx, sy, ex, ey);
        fillOval(sx - OUTER_SIZE / 2, sy - OUTER_SIZE / 2, OUTER_SIZE, OUTER_SIZE, false);
        fillOval(ex - OUTER_SIZE / 2, ey - OUTER_SIZE / 2, OUTER_SIZE, OUTER_SIZE, false);
    }
    
    private void drawLine(int sx, int sy, int ex, int ey) {
        int nbSteps = Math.abs(ex - sx) + Math.abs(ey - sy) + 2;
        for (int k = 0; k < nbSteps; k++) {
            int px = (ex * k + sx * (nbSteps - k)) / nbSteps;
            int py = (ey * k + sy * (nbSteps - k)) / nbSteps;
            setWall(px, py, false);
            setWall(px + 1, py, false);
            setWall(px, py + 1, false);
        }
    }
    
    public static void main(String[] args) {
        Random rnd = new Random();
        int nbLines = 15 + rnd.nextInt(30); // Based on difficulty?
        int nbEllipses = 10 + rnd.nextInt(15);
        SilentVoid sv = new SilentVoid(120, 60, nbLines, nbEllipses, 3, 9);
        Coord entry;
        do {
            sv.build();
            entry = sv.getEntry();
        } while (entry == null);
        
        // Create reachability mask:
        BinaryLevel reachable = new BinaryLevel(sv.getWidth(), sv.getHeight());
        reachable.fillRect(0, 0, sv.getWidth(), sv.getHeight(), true);
        BinaryLevel copy = sv.copy();
        copy.fillFlood(entry, true, (coord) -> reachable.setWall(coord, false));
        
        // This mask indicates where stuff (aliens, bonus) can be added:
//        System.out.println(reachable.toString());
        
        // Add entry
        Coord cur = new Coord(entry.x(), entry.y() - 1);
        while (cur.y() >= 0) {
            sv.setWall(cur, false);
            sv.setWall(cur.add(-1, 0), true);
            sv.setWall(cur.add(1, 0), true);
            sv.setWall(cur, false);
            cur = cur.add(0, -1);
        };

        System.out.println(sv.toString());
        // Reject if reachable < 1000
        System.out.println(nbLines + " lines, " + nbEllipses + " ellipses; reachable: " + reachable.countHoles());
    }

}
