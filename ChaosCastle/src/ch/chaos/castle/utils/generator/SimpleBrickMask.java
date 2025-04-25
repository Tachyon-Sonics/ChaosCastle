package ch.chaos.castle.utils.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.chaos.castle.utils.Coord;

public class SimpleBrickMask extends BinaryLevel {

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

    public static void main(String[] args) {
        SimpleBrickMask mask = new SimpleBrickMask(30, 30);
        mask.build(0.4);
        mask.fillInterior(4);
        mask.removeDiagonalsMakeHole();
        System.out.println(mask.toString());
    }

}
