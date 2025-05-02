package ch.chaos.castle.level;

import java.util.List;
import java.util.Random;

import ch.chaos.castle.ChaosAlien;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosCreator;
import ch.chaos.castle.ChaosDObj;
import ch.chaos.castle.alien.SpriteFiller;
import ch.chaos.castle.alien.SpriteInfo;
import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.Rect;
import ch.chaos.castle.utils.generator.BinaryLevel;
import ch.chaos.castle.utils.generator.PondGenerator;

public class Pond extends LevelBase {

    public void build() {
        Random rnd = new Random();

        chaos1Zone.rotate = false;
        chaos1Zone.flipVert = false;
        chaosBase.water = true;
        if (chaosBase.difficulty >= 9 && rnd.nextInt(4) == 0)
            chaosBase.water = false;
        
        PondGenerator generator = new PondGenerator(100, 60);
        // [0.1 .. 0.8]:                                    [0 .. 0.35] + [0.1 .. 0.45]  
        double persistence = (double) (chaosBase.difficulty - 3) / 20.0 + 0.1 + rnd.nextDouble() * 0.15;
        boolean success;
        do {
            generator.build(rnd, persistence);
            generator.drawRect(0, 0, generator.getWidth(), generator.getHeight(), true);
            success = generator.retainReachableFrom(new Coord(generator.getWidth() / 2, generator.getHeight() / 2));
        } while (!success);
        generator.removeDiagonalsMakeHole();
        generator.fillOval(32, 20, 36, 20, false);
        
        chaosObjects.Clear((short) 100, (short) 100);
        chaosObjects.FillRandom((short) 0, (short) 0, (short) 99, (short) 99, (short) Forest1, (short) Forest7, 
                chaosObjects.All_ref, chaosObjects.Rnd_ref);
        generator.forHoles((Coord coord) -> {
            chaosObjects.Put((short) coord.x(), (short) coord.y(), (short) Ground);
        });
        
        // Player: left-most position
        int x = 0;
        playerLoop:
        while (x < 100) {
            for (int y = 0; y < 60; y++) {
                if (chaosObjects.OnlyBackground((short) x, (short) y)) {
                    chaosObjects.PutPlayer((short) x, (short) y);
                    break playerLoop;
                }
            }
            x++;
        }
        
        // Exit: right-most position
        x = 99;
        exitLoop:
        while (x >= 0) {
            for (int y = 59; y >= 0; y--) {
                if (chaosObjects.OnlyBackground((short) x, (short) y)) {
                    chaosObjects.PutExit((short) x, (short) y);
                    break exitLoop;
                }
            }
            x--;
        }
        
        SpriteFiller filler = new SpriteFiller(rnd);
        
        // Place circles with cross
        for (int k = 0; k < 8; k++) {
            int size = 5 + rnd.nextInt(4) * 2; // 5 - 7 - 9 - 11
            BinaryLevel circle = new BinaryLevel(size, size);
            circle.fillOval(0, 0, size, size, true);
            BinaryLevel surround = circle.copy();
            BinaryLevel mask = circle.grownWalls8Mask().grownWalls8Mask();
            
            // Cross
            int mid = (size / 2);
            circle.fillRect(mid, 0, 1, size, false);
            circle.fillRect(0, mid, size, 1, false);
            
            // Inner circle
            if (size > 5 && rnd.nextBoolean()) {
                int maxAdd = (size - 5) / 2 - 1; // 7 -> 0, 9 -> 1, 11 -> 2
                int inner = 3 + (maxAdd <= 0 ? 0 : rnd.nextInt(maxAdd)) * 2; // 3 / 5 / 7
                circle.fillOval(mid - inner / 2, mid - inner / 2, inner, inner, false);
            }
            
            // Now place it
            List<Coord> candidates = generator.allPlacesFor(mask, false, 1, 1, 98, 58);
            if (!candidates.isEmpty()) {
                int index = rnd.nextInt(candidates.size());
                Coord position = candidates.get(index).add(2, 2);
                generator.drawShape(circle, position, true);
                int[] leafTypes = { Leaf1, Leaf2, Leaf3 };
                circle.forWalls((Coord coord) -> { // Bricks / leafs for structure
                    Coord target = coord.add(position);
                    if (rnd.nextInt(3) == 0) {
                        int type = leafTypes[rnd.nextInt(leafTypes.length)];
                        chaosObjects.Put((short) target.x(), (short) target.y(), (short) type);
                    } else {
                        chaosObjects.Put((short) target.x(), (short) target.y(), (short) SimpleBlock);
                    }
                });
                circle.forHoles((Coord coord) -> {
                    Coord target = coord.add(position);
                    if (surround.isWall(coord)) { // Ground2 for interior
                        chaosObjects.Put((short) target.x(), (short) target.y(), (short) Ground2);
                    }
                });
                Coord center = position.add(size / 2, size / 2);
                filler.putBlockObj(new SpriteInfo(Anims.ALIEN2, ChaosCreator.cNest), center);
            }
        }
        
        chaosObjects.Rect((short) 1, (short) 1, (short) 98, (short) 58);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doBubbleMaker, 0, 1, 0, 15);
        
        Rect anywhere = new Rect(1, 1, 98, 98);
        filler.placeRandom(
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aPic, 0), 
                anywhere, filler.wallToBackground(new Coord(1, 0), 6), 3 + chaosBase.difficulty);
        filler.placeRandom(
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aPic, 1), 
                anywhere, filler.wallToBackground(new Coord(-1, 0), 6), 3 + chaosBase.difficulty);
        
        /*
         * TODO continue Pond with:
         * - bonus / aliens in the periphery (use "corner" predicate: 3 consecutive in sorted n-8?)
         * - isolated treffles with links anywhere (hopefully mostly on the bottom)
         */
    }
    
}
