package ch.chaos.castle.level;

import java.util.Random;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus;
import ch.chaos.castle.ChaosCreator;
import ch.chaos.castle.alien.SpriteFiller;
import ch.chaos.castle.alien.SpriteInfo;
import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.MinMax;
import ch.chaos.castle.utils.Rect;
import ch.chaos.castle.utils.generator.BinaryLevel;
import ch.chaos.castle.utils.generator.Park;

public class CityPark extends LevelBase {
    
    public void build() {
        Park blobs = new Park(100, 100, 20, 40, 2, 9); // '9' -> 11 - (difficulty - 3);
        blobs.build();
        BinaryLevel interior = blobs.getInterior();
        
        Random rnd = new Random();
        LevelBuilder builder = new LevelBuilder(blobs.getWidth(), blobs.getHeight(), rnd);
        SpriteFiller filler = new SpriteFiller(rnd);
        
        builder.fillRandom(0, 0, blobs.getWidth(), blobs.getHeight(), Forest1, Forest7, builder::anywhere, builder::randomly);
        blobs.forHoles((Coord coord) -> {
            if (blobs.isIsolated8(coord, false))
                builder.put(coord, Ground);
            else
                builder.put(coord, Ground2);
        });
        blobs.forWalls((Coord coord) -> {
            if (interior.isWall(coord)) { // Center of blob
                if (rnd.nextInt(10) == 0) {
                    int index = rnd.nextInt(6);
                    int block = Sq1Block + index;
                    if (block == Fact2Block)
                        block = Fact3Block;
                    builder.put(coord, block);
                } else {
                    builder.put(coord, EmptyBlock);
                }
            } else { // Outside walls
                if (!blobs.isIsolated8(coord, true)) {
                    if (rnd.nextInt(10) == 0) {
                        int index = rnd.nextInt(3);
                        builder.put(coord, Leaf1 + index);
                    } else {
                        builder.put(coord, SimpleBlock);
                    }
                }
            }
        });
        
        Rect anywhere = new Rect(0, 0, blobs.getWidth(), blobs.getHeight());
        filler.placeRandom(new SpriteInfo(Anims.ALIEN2, ChaosCreator.cNest), anywhere, filler.background(), 200);
        filler.setPreventUsed(false);
        filler.placeRandom(new SpriteInfo(Anims.ALIEN2, ChaosCreator.cAlienA, filler.pLife(2)), anywhere, filler.background(), 50);
        filler.placeRandom(new SpriteInfo(Anims.ALIEN2, ChaosCreator.cAlienV, filler.pLife(2)), anywhere, filler.background(), 50);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), anywhere, filler.background(), 30);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBullet), anywhere, filler.background(), 10);
        filler.placeRandom(new SpriteInfo(Anims.BONUS, ChaosBonus.Money, ChaosBonus.Moneys.m5.ordinal()),
                anywhere, filler.background(), MinMax.value(16)); // remove
        filler.setPreventUsed(true);
        
        // Inside of blobs, interior
        for (BinaryLevel blob : blobs.getBlobs().keySet()) {
            // Create blob's interior walls
            BinaryLevel plot = blob.copy();
            plot.fillFlood(new Coord(0, 0), false);
            Coord where = blobs.getBlobs().get(blob);
            Coord center = where.add(centerOfGravity(plot));

            if (rnd.nextInt(3) > 0) {
                int choice = rnd.nextInt(4);
                int[] types = switch (choice) {
                    case 0 -> new int[] { Tar };
                    case 1 -> new int[] { Granit1, Granit2 };
                    case 2 -> new int[] { Bricks };
                    case 3 -> new int[] { Back4x4, Back2x2, BackBig, Back8x8 };
                    case 4 -> new int[] { Forest1, Forest2, Forest3, Forest4, Forest5, Forest6, Forest7 };
                    default -> throw new IllegalStateException();
                };
                
                // Fill
                for (int x = 0; x < blob.getWidth(); x++) {
                    for (int y = 0; y < blob.getHeight(); y++) {
                        Coord coord = where.add(x, y);
                        if (!interior.isOutside(coord) && interior.isWall(coord) && plot.isWall(x, y)) {
                            if (coord.equals(center)) {
                                builder.put(coord, Fact2Block);
                            } else if (blobs.isIsolated8(coord, true)) {
                                int index = rnd.nextInt(types.length);
                                builder.put(coord, types[index]);
                            }
                        }
                    }
                }
            } else {
                if (!interior.isOutside(center) && interior.isWall(center) && plot.isWall(center)) {
                    builder.put(center, Fact2Block);
                }
            }
        }
        
        Coord start = blobs.getStart();
        filler.putPlayer(start.x(), start.y());
        for (Coord exit : blobs.getExits())
            filler.putExit(exit.x(), exit.y());
    }
    
    private static Coord centerOfGravity(BinaryLevel blob) {
        int sx = 0;
        int sy = 0;
        int count = 0;
        for (int x = 0; x < blob.getWidth(); x++) {
            for (int y = 0; y < blob.getHeight(); y++) {
                if (blob.isWall(x, y)) {
                    sx += x;
                    sy += y;
                    count++;
                }
            }
        }
        if (count == 0)
            return new Coord(blob.getWidth() / 2, blob.getHeight() / 2);
        int cx = (sx + count / 2) / count;
        int cy = (sy + count / 2) / count;
        return new Coord(cx, cy);
    }

}
