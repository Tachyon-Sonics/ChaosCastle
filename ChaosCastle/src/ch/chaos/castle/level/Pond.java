package ch.chaos.castle.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.chaos.castle.ChaosAlien;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus;
import ch.chaos.castle.ChaosBonus.Moneys;
import ch.chaos.castle.ChaosCreator;
import ch.chaos.castle.ChaosDObj;
import ch.chaos.castle.ChaosMachine;
import ch.chaos.castle.alien.SpriteFiller;
import ch.chaos.castle.alien.SpriteInfo;
import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.MinMax;
import ch.chaos.castle.utils.Rect;
import ch.chaos.castle.utils.generator.BinaryLevel;
import ch.chaos.castle.utils.generator.MaskAt;
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
        
        LevelBuilder builder = new LevelBuilder(100, 90, rnd);
        builder.fillRandom(0, 0, 100, 90, Forest1, Forest7, builder::anywhere, builder::randomly);
        generator.forHoles((Coord coord) -> {
            builder.put(coord, Ground);
        });
        
        SpriteFiller filler = new SpriteFiller(rnd);
        
        // Player: left-most position
        {
            int x = 0;
            playerLoop:
            while (x < 100) {
                for (int y = 0; y < 60; y++) {
                    if (builder.isBackground(x, y)) {
                        filler.putPlayer(x, y);
                        break playerLoop;
                    }
                }
                x++;
            }
        }
        
        // Exit: right-most position
        {
            int x = 99;
            exitLoop:
            while (x >= 0) {
                for (int y = 59; y >= 0; y--) {
                    if (builder.isBackground(x, y)) {
                        filler.putExit(x, y);
                        break exitLoop;
                    }
                }
                x--;
            }
        }
        
        // Place stuff on corners and dead ends
        Rect topHalf = new Rect(1, 1, 98, 58);
        {
            List<SpriteInfo> items = List.of(
                    new SpriteInfo(Anims.ALIEN2, ChaosCreator.cNest),
                    new SpriteInfo(Anims.ALIEN1, ChaosAlien.aCartoon, 0)
                    );
            int nbPlaced = filler.placeRandom(items, topHalf, (Coord coord) -> {
                return isCorner(generator, coord, 7);
            }, MinMax.value(5));
            nbPlaced += filler.placeRandom(items, topHalf, (Coord coord) -> {
                return isCorner(generator, coord, 6);
            }, MinMax.value(6 - nbPlaced));
            nbPlaced += filler.placeRandom(items, topHalf, (Coord coord) -> {
                return isCorner(generator, coord, 5);
            }, MinMax.value(8 - nbPlaced));
            filler.placeRandom(items, topHalf, (Coord coord) -> {
                return isCorner(generator, coord, 4);
            }, MinMax.value(10 - nbPlaced));
        }
        {
            List<SpriteInfo> items = List.of(
                    new SpriteInfo(Anims.BONUS, ChaosBonus.Money, Moneys.m1.ordinal()),
                    new SpriteInfo(Anims.BONUS, ChaosBonus.Money, Moneys.m2.ordinal()),
                    new SpriteInfo(Anims.BONUS, ChaosBonus.Money, Moneys.st.ordinal()),
                    new SpriteInfo(Anims.BONUS, ChaosBonus.Money, Moneys.st.ordinal()),
                    new SpriteInfo(Anims.BONUS, ChaosBonus.Money, Moneys.st.ordinal()),
                    SpriteInfo.tbBonus(ChaosBonus.tbBullet)
                    );
            int nbPlaced = filler.placeRandom(items, topHalf, (Coord coord) -> {
                return isCorner(generator, coord, 7);
            }, MinMax.value(15), null, 8);
            nbPlaced += filler.placeRandom(items, topHalf, (Coord coord) -> {
                return isCorner(generator, coord, 6);
            }, MinMax.value(24 - nbPlaced), null, 8);
            nbPlaced += filler.placeRandom(items, topHalf, (Coord coord) -> {
                return isCorner(generator, coord, 5);
            }, MinMax.value(27 - nbPlaced), null, 8);
            filler.placeRandom(items, topHalf, (Coord coord) -> {
                return isCorner(generator, coord, 4);
            }, MinMax.value(30 - nbPlaced), null, 8);
        }
        
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
                        builder.put(target, type);
                    } else {
                        builder.put(target, SimpleBlock);
                    }
                });
                circle.forHoles((Coord coord) -> {
                    Coord target = coord.add(position);
                    if (surround.isWall(coord)) { // Ground2 for interior
                        builder.put(target, Ground2);
                    }
                });
                Coord center = position.add(size / 2, size / 2);
                filler.putBlockObj(new SpriteInfo(Anims.ALIEN2, ChaosCreator.cNest), center);
            }
        }
        
        Rect anywhere = new Rect(1, 1, 98, 58);
        filler.placeRandom(new SpriteInfo(Anims.MACHINE, ChaosMachine.mTurret), anywhere, 
                filler.backgroundDistance(3), (chaosBase.difficulty - 3) * 2);
        filler.placeRandom(new SpriteInfo(Anims.MACHINE, ChaosMachine.mCannon3), anywhere, 
                filler.backgroundDistance(3), (chaosBase.difficulty - 2));
        filler.placeRandomS(new SpriteInfo(Anims.ALIEN2, ChaosCreator.cFour, chaos1Zone.pLife3), anywhere, 
                filler.background(), 15, 8);
        filler.placeRandom(List.of(new SpriteInfo(Anims.DEADOBJ, ChaosDObj.doBubbleMaker)), anywhere,
                filler.background8(), MinMax.value(15), new MinMax(0, 1));
        
        Rect lastFourth = new Rect(75, 1, 99 - 75, 58);
        filler.placeRandom(List.of(new SpriteInfo(Anims.DEADOBJ, ChaosDObj.doSand)), lastFourth,
                filler.background(), new MinMax(80, 120), null, 16);
        
        filler.placeRandom(
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aPic, 0), 
                anywhere, filler.wallToBackground(new Coord(1, 0), 6), 3 + chaosBase.difficulty);
        filler.placeRandom(
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aPic, 1), 
                anywhere, filler.wallToBackground(new Coord(-1, 0), 6), 3 + chaosBase.difficulty);
        filler.placeRandom(
                List.of(new SpriteInfo(Anims.DEADOBJ, ChaosDObj.doMirror, 1)),
                anywhere, filler.background8(), new MinMax(10, 20), null, 8);
        
        filler.addOptions(anywhere, filler.background(), 0, 6, 0, 2, 15, 5, 5);
        
        // Bottom treffles
        BinaryLevel fullPond = new BinaryLevel(100, 90);
        fullPond.fillRect(0, 60, 100, 30, true);
        fullPond.drawShape(generator, new Coord(0, 0), true);
        int[] linkTypes = { Balls, Balls, Ground2, BackBig, Round4, FalseEmpty };
        int[] interiorTypes = { Balls, Light, Ground, Back8x8, Tar, FalseEmpty };
        for (int k = 0; k < 6; k++) {
            int tries = 0;
            MaskAt placement;
            do {
                int diameter = 3 + rnd.nextInt(4) * 2; // 3 -- 9
                int size = diameter * 2 + 3;
                int mid = size / 2;
                int offsetY = (diameter == 9 ? 3 : (diameter <= 5 ? 1 : 2));
                BinaryLevel treffle = new BinaryLevel(size, size);
                treffle.fillRect(0, 0, size, size, true);
                treffle.fillOval(0, mid - diameter / 2 - offsetY, diameter, diameter, false);
                treffle.fillOval(mid + 2, mid - diameter / 2 - offsetY, diameter, diameter, false);
                treffle.fillOval(mid - diameter / 2, mid + 2, diameter, diameter, false);
                treffle.fillRect(mid - 1, mid - offsetY, 3, 1, false); // ---
                treffle.fillRect(mid, mid - offsetY, 1, diameter, false); // |
                placement = placeWithUpLink(fullPond, treffle, 10, rnd);
                
                if (placement != null) {
                    BinaryLevel shape = placement.mask();
                    Coord where = placement.where();
                    shape.invert();
                    
                    // Create up link
                    Coord center = where.add(shape.getWidth() / 2, shape.getHeight() / 2 - 1);
                    while (fullPond.isWall(center) && center.y() > 1) {
                        fullPond.setWall(center, false);
                        builder.put(center, linkTypes[k]);
                        center = center.add(0, -1);
                    }
                    
                    // Create interior
                    int type = interiorTypes[k];
                    fullPond.drawShape(shape, where, false);
                    shape.forWalls((Coord coord) -> {
                        Coord target = coord.add(where);
                        builder.put(target, type);
                    });
                    
                    // Add curiosities
                    Rect curRect = new Rect(where.x(), where.y(), shape.getWidth(), shape.getHeight());
                    int[] tbTypes = { ChaosBonus.tbFreeFire, ChaosBonus.tbSleeper, ChaosBonus.tbNoMissile, ChaosBonus.tbMagnet };
                    if (k < tbTypes.length && chaosBase.difficulty - 3 < rnd.nextInt(7)) {
                        filler.placeRandom(SpriteInfo.tbBonus(tbTypes[k]), curRect, filler.backgroundOrFalse(), 1);
                    }
                    List<SpriteInfo> infos = List.of(
                            new SpriteInfo(Anims.ALIEN1, ChaosAlien.aBumper, filler.pLife(3)),
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cCreatorC, filler.life(40, 3, 4)),
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cQuad, filler.pLife(3)),
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cCreatorR, filler.life(40, 3, 4)),
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cNest),
                            new SpriteInfo(Anims.BONUS, ChaosBonus.Money, Moneys.st.ordinal())
                            );
                    SpriteInfo info = infos.get(k);
                    filler.placeRandom(info, 
                            curRect,
                            filler.backgroundOrFalse(), new MinMax(6, 10));
                    filler.placeRandom(List.of(new SpriteInfo(Anims.DEADOBJ, ChaosDObj.doBubbleMaker)), curRect,
                            filler.backgroundOrFalse8(), MinMax.value(1), MinMax.value(0));
                }
                tries++;
            } while (placement == null && tries < 10);
        }
        
        anywhere = new Rect(1, 1, 98, 88);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), anywhere, filler.background(), MinMax.value(10));
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBullet), anywhere, filler.background(), MinMax.value(12));
        
        // Bottom blocks
        int[] btTypes = { F9x9, FStar, FRound, Fade2, Fade1, Fade3, Fact2Block };
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 20; y++) {
                int r = y + rnd.nextInt(11) - 5; // 0 .. 24
                if (r >= 0 && builder.isWall(x, y + 70)) {
                    int index = r / 4; // 0 .. 6
                    builder.put(x, y + 70, btTypes[index]);
                }
            }
        }
        
        /*
         * TODO continue Pond with:
         * - Review Sand: cluster with fine positioning around exit
         * - Small items like money: add random fine positioning
         * - review all
         */
    }
    
    private boolean isCorner(BinaryLevel level, Coord coord, int minConsecutive) {
        if (level.isWall(coord))
            return false;
        List<Coord> surround = Coord.clockwiseN8();
        boolean wall = false;
        int consecutive = 0;
        for (int i = 0; i < surround.size() * 2; i++) {
            Coord delta = surround.get(i % surround.size());
            Coord next = coord.add(delta);
            if (level.isWall(next)) {
                if (!wall) {
                    wall = true;
                    consecutive = 1;
                } else {
                    consecutive++;
                    if (consecutive >= minConsecutive) {
                        return true;
                    }
                }
            } else {
                wall = false;
                consecutive = 0;
            }
        }
        return false;
    }
    
    private MaskAt placeWithUpLink(BinaryLevel pond, BinaryLevel shape, int maxLinkLength, Random rnd) {
        BinaryLevel mask = shape.grownWalls8Mask();
        List<Coord> candidates = pond.allPlacesFor(mask, true, 0, 0, pond.getWidth(), pond.getHeight());
        List<Coord> valid = new ArrayList<>();
        for (Coord candidate : candidates) {
            Coord center = candidate.add(mask.getWidth() / 2, mask.getHeight() / 2 - 1);
            int length = 0;
            while (center.y() > 0 && length < maxLinkLength) {
                if (!pond.isWall(center)) {
                    valid.add(candidate.add(1, 1));
                    break;
                }
                center = center.add(0, -1);
                length++;
            }
        }
        if (valid.isEmpty()) {
            return null;
        }
        int index = rnd.nextInt(valid.size());
        Coord where = valid.get(index);
        return new MaskAt(shape, where);
    }
    
}
