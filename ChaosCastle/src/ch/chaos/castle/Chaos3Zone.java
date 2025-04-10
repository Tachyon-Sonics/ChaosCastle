package ch.chaos.castle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.chaos.castle.ChaosObjects.FilterProc;
import ch.chaos.castle.utils.BrickMask;
import ch.chaos.castle.utils.CubicInterpolator;
import ch.chaos.library.Trigo;


public class Chaos3Zone {

    // Imports
    private final Chaos1Zone chaos1Zone;
    private final Chaos2Zone chaos2Zone;
    private final ChaosBase chaosBase;
    private final ChaosGenerator chaosGenerator;
    private final ChaosGraphics chaosGraphics;
    private final ChaosObjects chaosObjects;
    private final Trigo trigo;


    private Chaos3Zone() {
        instance = this; // Set early to handle circular dependencies
        chaos1Zone = Chaos1Zone.instance();
        chaos2Zone = Chaos2Zone.instance();
        chaosBase = ChaosBase.instance();
        chaosGenerator = ChaosGenerator.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosObjects = ChaosObjects.instance();
        trigo = Trigo.instance();
    }


    // CONST

    private static final int fKmk = 0;
    private static final int fPic = 1;
    private static final int fMoneyS = 2;
    private static final int fMoneyMix = 3;
    private static final int fAlienColor = 4;
    private static final int fAlienFour = 5;
    private static final int fCannon1 = 6;
    private static final int fCannon2 = 7;
    private static final int fCartoon = 8;
    private static final int fNone = 9;
    private static final int fAnims1 = 10;
    private static final int fAnims2 = 11;
    private static final int fAnims3 = 12;
    private static final int fAnims4 = 13;
    private static final int fCrunchY = 14;
    private static final int fCrunchX = 15;
    private static final int Back4x4 = 8;
    private static final int BackNone = 9;
    private static final int Back2x2 = 10;
    private static final int BackSmall = 11;
    private static final int BackBig = 12;
    private static final int Back8x8 = 13;
    private static final int Tar = 14;
    private static final int Ground = 15;
    private static final int Ground2 = 16;
    private static final int Ice = 17;
    private static final int Light = 18;
    private static final int Balls = 19;
    private static final int Round4 = 20;
    private static final int FalseBlock = 21;
    private static final int FalseEmpty = 23;
    private static final int EmptyBlock = 24;
    private static final int Sq1Block = 25;
    private static final int Sq4Block = 26;
    private static final int Sq4TravBlock = 27;
    private static final int TravBlock = 28;
    private static final int Fact1Block = 29;
    private static final int Fact2Block = 30;
    private static final int Fact3Block = 31;
    private static final int SimpleBlock = 32;
    private static final int Granit1 = 33;
    private static final int Granit2 = 34;
    private static final int BigBlock = 35;
    private static final int Bricks = 36;
    private static final int Fade1 = 37;
    private static final int Fade2 = 38;
    private static final int Fade3 = 39;
    private static final int FBig1 = 40;
    private static final int FBig2 = 41;
    private static final int FSmall1 = 42;
    private static final int FSmall2 = 43;
    private static final int FRound = 44;
    private static final int FStar = 45;
    private static final int FPanic = 46;
    private static final int F9x9 = 47;
    private static final int Forest1 = 48;
    private static final int Forest7 = 54;
    private static final int Leaf1 = 55;
    private static final int Leaf2 = 56;
    private static final int Leaf3 = 57;
    private static final int Leaf4 = 58;
    private static final int BarLight = 59;
    private static final int BarDark = 60;
    private static final int TravLight = 61;
    private static final int RGBBlock = 62;
    private static final int IceBlock = 63;


    // PROCEDURE

    public void industry() { // TODO continue, disable rotation + symmetry so we can put kamikazes in an intelligent way
        // Create road
        final int BmSize = 30;
        Random rnd = new Random();
        List<BrickMask> brickMasks = new ArrayList<>();
        Map<BrickMask, Integer> lengthMap = new HashMap<>();
        for (int k = 0; k < 6; k++) {
            BrickMask brickMask = BrickMask.buildBrickMask(rnd.nextLong(), BmSize, BmSize);
            brickMasks.add(brickMask);
            int[] path = brickMask.toTravel(rnd.nextLong(), false);
            lengthMap.put(brickMask, path.length);
        }
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
        
        System.out.println(brickMask.toString());
        
        chaosObjects.Clear((short) 120, (short) 120);
        chaosObjects.Fill((short) 0, (short) 0, (short) (BmSize * 4), (short) (BmSize * 4), (short) EmptyBlock);

        // Draw road
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
                    chaosObjects.PutPlayer((short) x, (short) y);
                } else if (i == path.length - 2 && k == nbSteps - 1) {
                    chaosObjects.PutExit((short) x, (short) y);
                }
            }
        }
        
        // Add isolated bricks
        int nbAdded = 0;
        for (int k = 0; k < 10 + chaosBase.difficulty; k++) {
            for (int tries = 0; tries < 20; tries++) {
                boolean added = tryPlaceBrickMaskHole(1, 1, 119, 119, 8, 32, chaosObjects.OnlyWall_ref,
                        rnd, (short) Back4x4, true, 1 + rnd.nextInt(2), (short) EmptyBlock);
                if (added) {
                    nbAdded++;
                    break;
                }
            }
        }
        System.out.println(nbAdded);

        nbAdded = 0;
        for (int k = 0; k < 10 + chaosBase.difficulty; k++) {
            for (int tries = 0; tries < 20; tries++) {
                boolean added = tryPlaceBrickMaskHole(1, 1, 119, 119, 8, 12, chaosObjects.OnlyBackground_ref,
                        rnd, (short) EmptyBlock);
                if (added) {
                    nbAdded++;
                    break;
                }
            }
        }
        System.out.println(nbAdded);
    }
    
    public boolean tryPlaceBrickMaskHole(int sx, int sy, int ex, int ey, int minSize, int maxSize,
            FilterProc filter, Random rnd, int blockType) {
        return tryPlaceBrickMaskHole(sx, sy, ex, ey, minSize, maxSize, filter, rnd, blockType, false, 0, 0);
    }
    
    public boolean tryPlaceBrickMaskHole(int sx, int sy, int ex, int ey, int minSize, int maxSize,
            FilterProc filter, Random rnd, int blockType, boolean link,
            int fillCenterN, int centerBlockType) {
        int width = minSize + (maxSize == minSize ? 0 : rnd.nextInt(maxSize - minSize));
        int height = minSize + (maxSize == minSize ? 0 : rnd.nextInt(maxSize - minSize));
        BrickMask brickMask = BrickMask.buildBrickMask(rnd.nextLong(), width, height);
        int px = sx + rnd.nextInt(ex - sx - width);
        int py = sy + rnd.nextInt(ey - sy - height);
        
        // Check if placeable
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (brickMask.getBrick(x, y)) {
                    if (!isIsolated4(px + x, py + y, filter))
                        return false;
                }
            }
        }
        
        // Place it
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (brickMask.getBrick(x, y)) {
                    chaosObjects.Put((short) (px + x), (short) (py + y), (short) blockType);
                }
            }
        }
        
        // Link TODO simplify: trying link and drawing link should both fill an array of points and hence use the same method
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
                        chaosObjects.Put(ptx, pty, (short) blockType);
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
                    if (brickMask.getBrick(x, y) 
                            && isIsolatedN(px + x, py + y, fillCenterN, reverseFilter)) {
                        centerPoints.add(new int[] {px + x, py + y});
                    }
                }
            }
            for (int[] point : centerPoints) {
                chaosObjects.Put((short) point[0], (short) point[1], (short) centerBlockType);
            }
        }
        
        return true;
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
    
//    public void Factory() {
//        // VAR
//        short c = 0;
//        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
//        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
//        short dx = 0;
//        short dy = 0;
//        Runtime.Ref<Short> a = new Runtime.Ref<>((short) 0);
//        short z = 0;
//        short sz = 0;
//        short d = 0;
//        short sk = 0;
//
//        chaosObjects.Clear((short) 101, (short) 101);
//        if (trigo.RND() % 8 != 0)
//            rotate = false;
//        chaosBase.water = (chaosBase.difficulty >= 4) && (trigo.RND() % 3 == 0);
//        chaosObjects.Fill((short) 0, (short) 0, (short) 100, (short) 100, (short) EmptyBlock);
//        chaosGenerator.DrawFactory();
//        chaosObjects.PutPlayer((short) 12, (short) 90);
//        chaosObjects.PutBlockBonus(ChaosBonus.tbHelp, (short) 12, (short) 88);
//        chaosObjects.Rect((short) 1, (short) 1, (short) 98, (short) 98);
//        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doWindMaker, 0, 1, 1, 6);
//        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doBubbleMaker, 1, 1, 1, 15);
//        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doFireMaker, 0, 0, 2, 15);
//        chaosObjects.Rect((short) 33, (short) 1, (short) 98, (short) 98);
//        chaosObjects.PutKamikaze(0, 6);
//        chaosObjects.PutKamikaze(1, 6);
//        chaosObjects.PutKamikaze(2, 6);
//        chaosObjects.PutKamikaze(3, 6);
//        chaosObjects.PutPic(0, 10);
//        chaosObjects.PutPic(1, 10);
//        chaosObjects.Rect((short) 1, (short) 1, (short) 98, (short) 66);
//        chaosObjects.PutTurret(15);
//        chaosObjects.Rect((short) 70, (short) 1, (short) 98, (short) 98);
//        chaosObjects.PutTri(pLife3, 7);
//        chaosObjects.Rect((short) 1, (short) 1, (short) 98, (short) 98);
//        chaosObjects.PutCannon3(6);
//        chaosObjects.PutCartoon(0, 1, 25);
//        for (c = 1; c <= 10; c++) {
//            if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 99, (short) 99, (short) 2, (short) 8, a, x, y, true)) {
//                chaosGenerator.MakeLink(x.get(), y.get(), (short) 0, a.get(), (short) BackBig);
//                if (trigo.COS(a.get()) == 0) {
//                    dx = 1;
//                    dy = 0;
//                } else {
//                    dx = 0;
//                    dy = 1;
//                }
//                for (z = -2; z <= 2; z++) {
//                    chaosObjects.Put((short) (x.get() + z * dx), (short) (y.get() + z * dy), (short) BackSmall);
//                    chaosObjects.Put((short) (x.get() - 2 * dx + z * dy), (short) (y.get() - 2 * dy + z * dx), (short) BackSmall);
//                    chaosObjects.Put((short) (x.get() + 2 * dx + z * dy), (short) (y.get() + 2 * dy + z * dx), (short) BackSmall);
//                }
//                chaosObjects.Rect((short) (x.get() - 2), (short) (y.get() - 2), (short) (x.get() + 2), (short) (y.get() + 2));
//                chaosObjects.PutBullet(1);
//                chaosObjects.PutAlien1(ChaosAlien.aHospital, pLife2, 1);
//            }
//        }
//        chaosObjects.PutMagnetA(3, 15);
//        chaosObjects.PutMagnetR(3, 5);
//        if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 99, (short) 99, (short) 1, (short) 20, a, x, y, true)) {
//            chaosObjects.Fill((short) (x.get() - 1), (short) (y.get() - 1), (short) (x.get() + 1), (short) (y.get() + 1), (short) Ice);
//            chaosObjects.PutBlockObj(Anims.BONUS, (short) ChaosBonus.BonusLevel, ChaosBonus.tbBonusLevel, x.get(), y.get());
//            chaosGenerator.MakeLink(x.get(), y.get(), (short) 1, a.get(), (short) 23);
//        }
//        if (chaosBase.difficulty > 2) {
//            for (c = 0; c <= 6; c++) {
//                sz = (short) (trigo.RND() % 3 + 1);
//                if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 99, (short) 99, sz, (short) 20, a, x, y, true)) {
//                    chaosObjects.Rect((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz));
//                    d = (short) (sz * 2 + 1);
//                    chaosGenerator.MakeLink(x.get(), y.get(), sz, a.get(), (short) FalseEmpty);
//                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) BackNone);
//                    if (sz >= 2)
//                        sz -= 2;
//                    else
//                        sz--;
//                    d = (short) (2 * sz + 1);
//                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) SimpleBlock);
//                    sk = (short) (trigo.RND() % 4);
//                    if (sk == 0)
//                        sk = ChaosAlien.aTri;
//                    else if (sk == 1)
//                        sk = ChaosAlien.aDiese;
//                    else
//                        sk = ChaosAlien.aBumper;
//                    chaosObjects.PutAlien1(sk, pLife2, (c == 0 ? 1 : 0) * 20 + 1);
//                }
//            }
//        }
//        chaosObjects.PutRandom((short) 0, (short) 0, (short) 99, (short) 99, chaosObjects.OnlyWall_ref, (short) Sq1Block, (short) (trigo.RND() % 64));
//        chaosObjects.PutRandom((short) 0, (short) 0, (short) 99, (short) 99, chaosObjects.OnlyWall_ref, (short) Sq4Block, (short) (trigo.RND() % 64));
//        chaosObjects.PutRandom((short) 10, (short) 0, (short) 99, (short) 99, chaosObjects.OnlyWall_ref, (short) Sq4TravBlock, (short) (trigo.RND() % 128));
//        chaosObjects.PutRandom((short) 20, (short) 0, (short) 99, (short) 99, chaosObjects.OnlyWall_ref, (short) TravBlock, (short) (trigo.RND() % 128));
//        chaosObjects.PutRandom((short) 30, (short) 0, (short) 99, (short) 79, chaosObjects.OnlyWall_ref, (short) Fact1Block, (short) (trigo.RND() % 256));
//        chaosObjects.PutRandom((short) 30, (short) 0, (short) 79, (short) 99, chaosObjects.OnlyWall_ref, (short) Fact2Block, (short) (trigo.RND() % 256));
//        chaosObjects.PutRandom((short) 0, (short) 0, (short) 99, (short) 59, chaosObjects.OnlyWall_ref, (short) Fact3Block, (short) (trigo.RND() % 256));
//        chaos1Zone.AddOptions((short) 30, (short) 1, (short) 98, (short) 98, 5, 5, 3, 4, 1, 0, 0);
//    }
//


    // Support

    private static Chaos3Zone instance;

    public static Chaos3Zone instance() {
        if (instance == null)
            new Chaos3Zone(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
    }

    public void close() {
    }

}
