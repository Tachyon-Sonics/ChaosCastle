package ch.chaos.castle.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleBrickMask extends BinaryLevelBuilderBase {

    public SimpleBrickMask(int width, int height) {
        super(width, height);
    }
    
    public void build(double fillFraction) {
        // Setup
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                setWall(x, y, true);
            }
        }
        setWall(width / 2, height / 2, false);
        List<Coord> holes = new ArrayList<>();
        holes.add(new Coord(width / 2, height / 2));
        List<Coord> borders = new ArrayList<>();
        for (Coord delta : Coord.n4()) {
            borders.add(new Coord(width / 2 + delta.x(), height / 2 + delta.y()));
        }
        Random rnd = new Random();
        int nbBlocks = (int) (width * height  * fillFraction);
        
        // Grow
        for (int k = 0; k < nbBlocks; k++) {
            // Add a new hole
            int index = rnd.nextInt(borders.size());
            Coord newBlock = borders.remove(index);
            setWall(newBlock.x(), newBlock.y(), false);
            holes.add(newBlock);
            
            // Update borders
            for (Coord delta : Coord.n4()) {
                Coord coord = new Coord(newBlock.x() + delta.x(), newBlock.y() + delta.y());
                if (holes.contains(coord)) {
                    borders.remove(coord);
                } else if (!borders.contains(coord) && !isBoundary(coord)) {
                    borders.add(coord);
                }
            }
        }
    }

    /**
     * Fill all empty space whose distance to a wall (euclidean) is greater than the given value
     */
    public void fillInterior(int distance) {
        List<Coord> toFill = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (!isWall(x, y)) {
                    boolean isFree = true;
                    for (int dx = -distance; dx <= distance; dx++) {
                        for (int dy = -distance; dy <= distance; dy++) {
                            if (dx * dx + dy * dy <= distance * distance) {
                                if (isWall(x + dx, y + dy)) {
                                    isFree = false;
                                }
                            }
                        }
                    }
//                    
//                    for (int k = 1; k <= distance; k++) {
//                        if (isWall(x - k, y) || isWall(x + k, y) || isWall(x, y - k) || isWall(x, y + k)) {
//                            isFree = false;
//                        }
//                    }
                    if (isFree) {
                        toFill.add(new Coord(x, y));
                    }
                }
            }
        }
        for (Coord coord : toFill) {
            setWall(coord, true);
        }
    }
    
    public static void main(String[] args) {
        SimpleBrickMask mask = new SimpleBrickMask(30, 30);
        mask.build(0.4);
        mask.fillInterior(4);
        mask.removeDiagonals();
        System.out.println(mask.toString());
    }

}
