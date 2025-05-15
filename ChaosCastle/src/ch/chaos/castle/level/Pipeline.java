package ch.chaos.castle.level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import ch.chaos.castle.ChaosAlien;
import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus;
import ch.chaos.castle.ChaosCreator;
import ch.chaos.castle.ChaosDObj;
import ch.chaos.castle.ChaosMachine;
import ch.chaos.castle.ChaosObjects.FilterProc;
import ch.chaos.castle.alien.SpriteFiller;
import ch.chaos.castle.alien.SpriteInfo;
import ch.chaos.castle.utils.Coord;
import ch.chaos.castle.utils.CubicInterpolator;
import ch.chaos.castle.utils.MinMax;
import ch.chaos.castle.utils.Rect;
import ch.chaos.castle.utils.generator.BrickMask;
import ch.chaos.castle.utils.generator.MaskAt;

public class Pipeline extends LevelBase {

    public void build() {
        chaos1Zone.rotate = false;
        Random rnd = new Random();
        
        if (chaosBase.difficulty >= 8 && rnd.nextBoolean()) {
            chaosBase.water = true;
        }

        // Create road
        final int BmSize = 30;
        List<BrickMask> brickMasks = new ArrayList<>();
        Map<BrickMask, Integer> lengthMap = new HashMap<>();
        
        // Generate multiple masks
        for (int k = 0; k < 6; k++) {
            BrickMask brickMask = BrickMask.buildBrickMask(rnd.nextLong(), BmSize, BmSize);
            brickMasks.add(brickMask);
            int[] path = brickMask.toTravel(rnd.nextLong(), false);
            lengthMap.put(brickMask, path.length);
        }
        
        // Choose one with complexity matching current difficulty
        brickMasks.sort((bm1, bm2) -> {
            return Integer.compare(lengthMap.get(bm1), lengthMap.get(bm2));
        });
        int index = chaosBase.difficulty - 3;
        if (index < 0)
            index = 0;
        else if (index >= brickMasks.size())
            index = brickMasks.size() - 1;
        BrickMask brickMask = brickMasks.get(index);
        int[] path = brickMask.toTravel(rnd.nextLong(), true); // Each value: y * width + x
        
        // Fill background
        LevelBuilder builder = new LevelBuilder(120, 120, rnd);
        builder.fill(0, 0, BmSize * 4, BmSize * 4, SimpleBlock);
        builder.putRandom(0, 0, 120, 120, builder::isWall, Leaf1, 100);
        builder.putRandom(0, 0, 120, 120, builder::isWall, Leaf2, 100);
        builder.putRandom(0, 0, 120, 120, builder::isWall, Leaf3, 100);
        builder.putRandom(0, 0, 120, 120, builder::isWall, Leaf4, 10);
        builder.putRandom(0, 0, 120, 120, builder::isWall, RGBBlock, 5);

        SpriteFiller filler = new SpriteFiller(rnd);

        // Draw pipeline
        int prevSize = 0;
        int nextSize = 2 + rnd.nextInt(6);
        int sizeCount = 0;
        int sizeIndex = 0;
        for (int i = 1; i < path.length - 1; i++) {
            if (sizeIndex >= sizeCount) {
                prevSize = nextSize;
                nextSize = 2 + rnd.nextInt(7);
                sizeCount = 2 + rnd.nextInt(6);
                sizeIndex = 0;
            }
            int size = (prevSize * (sizeCount - sizeIndex) + nextSize * sizeIndex) / sizeCount;
            sizeIndex++;
            
            int sy1 = path[(i + path.length - 1) % path.length] / BmSize;
            int sx1 = path[(i + path.length - 1) % path.length] % BmSize;
            int sy = path[i] / BmSize;
            int sx = path[i] % BmSize;
            int ey = path[i + 1] / BmSize;
            int ex = path[i + 1] % BmSize;
            int ey1 = path[(i + 2) % path.length] / BmSize;
            int ex1 = path[(i + 2) % path.length] % BmSize;
            int maxDist = Math.max(Math.abs(ey - sy), Math.abs(ex - sx));
            int nbSteps = maxDist * 8;
            for (int k = 0; k < nbSteps; k++) {
                double dx = CubicInterpolator.splineInterpolateY(sx1 * 4, sx * 4, ex * 4, ex1 * 4, (double) k / (double) nbSteps);
                double dy = CubicInterpolator.splineInterpolateY(sy1 * 4, sy * 4, ey * 4, ey1 * 4, (double) k / (double) nbSteps);
                int x = (int) (dx + 0.5);
                int y = (int) (dy + 0.5);

                int sizeL = size / 2;
                chaosGenerator.FillEllipse((short) (x - sizeL), (short) (y - sizeL), size, size, (short) Back2x2);
                if (i == 1 && k == 0) {
                    filler.putPlayer(x, y);
                } else if (i == path.length - 2 && k == nbSteps - 1) {
                    filler.putExit(x, y);
                } else if ((i + 5) % 7 == 0 && k == 0) {
                    if (chaosBase.water) {
                        filler.putBubbleMaker(1, x, y);
                    }
                }
            }
        }

        // Add isolated brickmask bricks
        int nbAdded = 0;
        for (int k = 0; k < 10 + chaosBase.difficulty; k++) {
            for (int tries = 0; tries < 40; tries++) {
                int[] fillTypes;
                if (rnd.nextBoolean())
                    fillTypes = new int[] { Granit1, Granit2 };
                else
                    fillTypes = new int[] { Forest1, Forest2, Forest3, Forest4, Forest5, Forest6, Forest7 };
                MaskAt maskAt = tryPlaceBrickMaskHole(1, 1, 119, 119, 8, 12, chaosObjects.OnlyBackground_ref,
                        rnd, fillTypes);
                if (maskAt != null) {
                    nbAdded++;
                    break;
                }
            }
        }
        
        // Add aliens
        filler.placeRandomS(
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cFour, filler.pLife(3)),
                new Rect(0, 0, 60, 60), filler.background(), 60, 8);
        filler.placeRandomS(
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aDbOval, filler.pLife(3)),
                new Rect(0, 0, 60, 60), filler.background(), 30, 8);
        
        filler.placeRandomS(
                new SpriteInfo(Anims.ALIEN2, ChaosCreator.cQuad, filler.pLife(3)),
                new Rect(60, 0, 60, 60), filler.background(), 10, 8);
        
        filler.placeRandomS(
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aDiese, filler.pLife(3)),
                new Rect(0, 60, 60, 60), filler.background(), 15, 8);

        filler.placeRandomS(
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aColor, filler.life(2, 2, 2) + rnd.nextInt(10)),
                new Rect(60, 60, 60, 60), filler.background(), 30, 8);
        
        Rect anywhere = new Rect(1, 1, 118, 118);
        filler.placeRandom(
                List.of(new SpriteInfo(Anims.DEADOBJ, ChaosDObj.doWindMaker)), 
                anywhere, filler.background8(), filler.nb(12), filler.nb(1));
        filler.placeRandom(
                List.of(new SpriteInfo(Anims.DEADOBJ, ChaosDObj.doFireMaker)), 
                anywhere, filler.background8(), filler.nb(8), filler.nb(0));
        filler.placeRandom(
                List.of(new SpriteInfo(Anims.MACHINE, ChaosMachine.mCannon3)), 
                anywhere, filler.background(), filler.nb(10));
        
        filler.placeRandom(
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aPic, 0), 
                anywhere, filler.wallToBackground(new Coord(1, 0), 6), 10);
        filler.placeRandom(
                new SpriteInfo(Anims.ALIEN1, ChaosAlien.aPic, 1), 
                anywhere, filler.wallToBackground(new Coord(-1, 0), 6), 10);
        
        filler.addOptions(anywhere, filler.background(), 0, 0, 1, 0, 0, 20, 5);
        
        // Add isolated brickmaks holes with link
        nbAdded = 0;
        for (int k = 0; k < 10 + chaosBase.difficulty; k++) {
            for (int tries = 0; tries < 20; tries++) {
                int val = rnd.nextInt(6);
                int[] fillTypes;
                if (val == 0)
                    fillTypes = new int[] { FRound, FStar, F9x9 };
                else if (val == 1)
                    fillTypes = new int[] { BigBlock, BigBlock, TravLight };
                else if (val == 2 || val == 3)
                    fillTypes = new int[] { Forest1, Forest2, Forest3, Forest4, Forest5, Forest6, Forest7 };
                else // 4, 5
                    fillTypes = new int[] { Fade1, Fade2, Fade3 };
                MaskAt maskAt = tryPlaceBrickMaskHole(1, 1, 119, 119, 8, 32, chaosObjects.OnlyWall_ref,
                        rnd, new int[] { Back4x4 }, true, Back4x4, 1 + rnd.nextInt(2),
                        fillTypes);
                if (maskAt != null) {
                    // Walls
                    Rect where = maskAt.rect();
                    Rect where2 = extend2(where);
                    fillFadeFromCenter(where2, 
                            new int[] { Leaf1, Leaf2, Leaf3 }, 
                            new Integer[] { SimpleBlock }, 120, 120, rnd);

                    // Aliens
                    List<SpriteInfo> types = List.of(
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cNest, 0),
                            new SpriteInfo(Anims.ALIEN2, ChaosCreator.cAlienA, chaos1Zone.pLife2),
                            new SpriteInfo(Anims.ALIEN1, ChaosAlien.aFlame, chaosBase.pLife),
                            new SpriteInfo(Anims.ALIEN1, ChaosAlien.aTrefle, chaos1Zone.pLife2),
                            new SpriteInfo(Anims.ALIEN1, ChaosAlien.aBumper, chaos1Zone.pLife2)
                            );
                    int typeIndex = nbAdded % types.size();
                    filler.placeRandom(types.get(typeIndex), maskAt.rect(), 
                            filler.mask(maskAt.where(), maskAt.mask(), false),
                            new MinMax(3, 5));
                    
                    // Bonus
                    filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBullet), maskAt.rect(), 
                            filler.mask(maskAt.where(), maskAt.mask(), false), 1);
                    filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), maskAt.rect(), 
                            filler.mask(maskAt.where(), maskAt.mask(), false), 1);
                    
                    nbAdded++;
                    break;
                }
            }
        }
        
        // More bonus
        Rect whole = new Rect(0, 0, 120, 120);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbSleeper), whole, filler.background(), 1);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbBullet), whole, filler.background(), (20 - nbAdded) * 3 / 2);
        filler.placeRandom(SpriteInfo.tbBonus(ChaosBonus.tbHospital), whole, filler.background(), 20 - nbAdded);
    }
    
    public MaskAt tryPlaceBrickMaskHole(int sx, int sy, int ex, int ey, int minSize, int maxSize,
            FilterProc filter, Random rnd, int... blockTypes) {
        return tryPlaceBrickMaskHole(sx, sy, ex, ey, minSize, maxSize, filter, rnd, blockTypes, false, 0, 0);
    }
    
    public MaskAt tryPlaceBrickMaskHole(int sx, int sy, int ex, int ey, int minSize, int maxSize,
            FilterProc filter, Random rnd, int[] blockTypes, boolean link, int linkType,
            int fillCenterN, int... centerBlockTypes) {
        int width = minSize + (maxSize == minSize ? 0 : rnd.nextInt(maxSize - minSize));
        int height = minSize + (maxSize == minSize ? 0 : rnd.nextInt(maxSize - minSize));
        BrickMask brickMask = BrickMask.buildBrickMask(rnd.nextLong(), width, height);
        int px = sx + rnd.nextInt(ex - sx - width);
        int py = sy + rnd.nextInt(ey - sy - height);
        
        // Check if placeable
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (brickMask.isBrick(x, y)) {
                    if (!isIsolated4(px + x, py + y, filter))
                        return null;
                }
            }
        }
        
        // Place it
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (brickMask.isBrick(x, y)) {
                    int index = rnd.nextInt(blockTypes.length);
                    chaosObjects.Put((short) (px + x), (short) (py + y), (short) blockTypes[index]);
                }
            }
        }
        
        // Link
        if (link) {
            int[] dxs = {-1, 1, 0, 0, -1, -1, 1, 1};
            int[] dys = {0, 0, -1, 1, -1, 1, -1, 1};
            int bestDi = -1;
            int bestDistance = 10000;
            
            // Search for smallest direction
            for (int di = 0; di < dxs.length; di++) {
                int cx = px + (width / 2);
                int cy = py + (height / 2);
                for (int k = 0; k < 255; k++) {
                    boolean inside = (cx >= px && cx <= px + width && cy >= py && cy <= py + height);
                    if (!inside) {
                        if (!filter.invoke((short) cx, (short) cy)) {
                            // found
                            int distance = k;
                            if (di >= 4)
                                distance *= 2;
                            if (distance < bestDistance) {
                                bestDi = di;
                                bestDistance = distance;
                            }
                            break;
                        }
                    }
                    cx += dxs[di];
                    cy += dys[di];
                    if (cx < sx || cx >= ex || cy < sy || cy >= ey) {
                        break;
                    }
                }
            }
            
            // Draw link
            if (bestDi >= 0) {
                List<int[]> toDraw = new ArrayList<>();
                int cx = px + (width / 2);
                int cy = py + (height / 2);
                for (int k = 0; k < 255; k++) {
                    boolean inside = (cx >= px && cx <= px + width && cy >= py && cy <= py + height);
                    if (!inside) {
                        if (!filter.invoke((short) cx, (short) cy)) {
                            // found
                            break;
                        }
                    }
                    toDraw.add(new int[] {cx, cy});
                    if (Math.abs(dxs[bestDi]) + Math.abs(dys[bestDi]) >= 2) {
                        toDraw.add(new int[] {cx + dxs[bestDi], cy});
                    }
                    cx += dxs[bestDi];
                    cy += dys[bestDi];
                    if (cx < sx || cx >= ex || cy < sy || cy >= ey) {
                        break;
                    }
                }
                for (int[] point : toDraw) {
                    short ptx = (short) point[0];
                    short pty = (short) point[1];
                    if (filter.invoke(ptx, pty)) {
                        chaosObjects.Put(ptx, pty, (short) linkType);
                    }
                }
            } else {
                System.out.println("Link failure");
            }
        }
        
        // Fill center
        FilterProc reverseFilter = (x, y) -> !filter.invoke(x, y);
        if (fillCenterN > 0) {
            List<int[]> centerPoints = new ArrayList<>();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (brickMask.isBrick(x, y) 
                            && isIsolatedN(px + x, py + y, fillCenterN, reverseFilter)) {
                        centerPoints.add(new int[] {px + x, py + y});
                    }
                }
            }
            for (int[] point : centerPoints) {
                int index = rnd.nextInt(centerBlockTypes.length);
                chaosObjects.Put((short) point[0], (short) point[1], (short) centerBlockTypes[index]);
            }
        }
        
        return new MaskAt(brickMask.toBinaryLevel(), new Coord(px, py));
    }
    
    public boolean isIsolated4(int px, int py, FilterProc filter) {
        return isIsolatedN(px, py, 1, filter);
    }
    
    public boolean isIsolatedN(int px, int py, int n, FilterProc filter) {
        for (int dx = -n; dx <= n; dx++) {
            for (int dy = -n; dy <= n; dy++) {
                if (!filter.invoke((short) (px + dx), (short) (py + dy)))
                    return false;
            }
        }
        return true;
    }
    
    /**
     * Make the given rect twice as big
     */
    private Rect extend2(Rect rect) {
        return new Rect(rect.x() - rect.w() / 2, rect.y() - rect.h() / 2, rect.w() * 2, rect.h() * 2);
    }
    
    private void fillFadeFromCenter(Rect where, int[] blockTypes, Integer[] whereTypes, int levelWidth, int levelHeight, Random rnd) {
        Set<Integer> whereSet = new HashSet<>(Arrays.asList(whereTypes));
        int cx = (where.x() + where.ex()) / 2;
        int cy = (where.y() + where.ey()) / 2;
        for (int x = where.x(); x < where.ex(); x++) {
            for (int y = where.y(); y < where.ey(); y++) {
                if (x >= 0 && y >= 0 && x < levelWidth && y < levelHeight) {
                    int type = chaosObjects.Get((short) x, (short) y);
                    if (whereSet.contains(type)) {
                        double distance = Math.sqrt((x - cx) * (x - cx)) / (double) (where.w() / 2)
                                + Math.sqrt((y - cy) * (y - cy)) / (double) (where.h() / 2); // 0..1
                        double value = rnd.nextDouble();
                        if (value > distance) {
                            int putIndex = rnd.nextInt(blockTypes.length);
                            chaosObjects.Put((short) x, (short) y, (short) blockTypes[putIndex]);
                        }
                    }
                }
            }
        }
    }


}
