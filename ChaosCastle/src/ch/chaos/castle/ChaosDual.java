package ch.chaos.castle;

import ch.chaos.castle.ChaosBase.Zone;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosDual {

    // Imports
    private final ChaosBase chaosBase;
    private final ChaosGenerator chaosGenerator;
    private final ChaosGraphics chaosGraphics;
    private final ChaosImages chaosImages;
    private final ChaosObjects chaosObjects;
    private final Trigo trigo;


    private ChaosDual() {
        instance = this; // Set early to handle circular dependencies
        chaosBase = ChaosBase.instance();
        chaosGenerator = ChaosGenerator.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosImages = ChaosImages.instance();
        chaosObjects = ChaosObjects.instance();
        trigo = Trigo.instance();
    }


    // CONST

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

    private void ResetTrans() {
        // VAR
        int c = 0;

        chaosGraphics.SetTrans(0, 0);
        for (c = 1; c <= 15; c++) {
            chaosGraphics.SetTrans(c, 255);
        }
    }

    private void DrawStars() {
        chaosObjects.FillRandom(0, 0, 63, 63, 0, 7, chaosObjects.All_ref, chaosObjects.ExpRandom_ref);
    }

    private void DrawEntry_DrawLetter_DrawPattern(int pat, /* VAR */ Runtime.IRef<Integer> z) {
        // VAR
        int x = 0;

        for (x = 0; x <= 8; x++) {
            if (((pat % 2) != 0)) {
                chaosObjects.Set(z.get(), 9 - x);
                chaosObjects.Set(x + 2, z.get());
            }
            pat = pat / 2;
        }
        z.inc();
    }

    private void DrawEntry_DrawLetter(int b1, int b2, int b3, int b4, int b5, int add, /* VAR */ Runtime.IRef<Integer> z) {
        DrawEntry_DrawLetter_DrawPattern(b1, z);
        DrawEntry_DrawLetter_DrawPattern(b2, z);
        DrawEntry_DrawLetter_DrawPattern(b3, z);
        DrawEntry_DrawLetter_DrawPattern(b4, z);
        DrawEntry_DrawLetter_DrawPattern(b5, z);
        z.set(z.get() - 5 + add);
    }

    private void DrawEntry() {
        // VAR
        Runtime.Ref<Integer> z = new Runtime.Ref<>(0);

        z.set(6);
        /* C */
        DrawEntry_DrawLetter(124, 130, 257, 257, 257, 6, z);
        /* h */
        DrawEntry_DrawLetter(511, 16, 16, 15, 0, 5, z);
        /* a */
        DrawEntry_DrawLetter(14, 17, 10, 31, 0, 5, z);
        /* o */
        DrawEntry_DrawLetter(14, 17, 17, 17, 14, 6, z);
        /* s */
        DrawEntry_DrawLetter(9, 21, 21, 21, 18, 6, z);
        /* C */
        DrawEntry_DrawLetter(124, 130, 257, 257, 257, 6, z);
        /* a */
        DrawEntry_DrawLetter(14, 17, 10, 31, 0, 5, z);
        /* s */
        DrawEntry_DrawLetter(9, 21, 21, 21, 18, 6, z);
        /* t */
        DrawEntry_DrawLetter(32, 510, 33, 0, 0, 4, z);
        /* l */
        DrawEntry_DrawLetter(511, 0, 0, 0, 0, 2, z);
        /* e */
        DrawEntry_DrawLetter(14, 21, 21, 21, 8, 5, z);
        chaosObjects.FillCond(0, 0, 63, 63, chaosObjects.OnlyWall_ref, SimpleBlock);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.OnlyWall_ref, Leaf2, trigo.RND() % 64 + 32);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.OnlyWall_ref, Leaf3, trigo.RND() % 16 + 4);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.OnlyBackground_ref, Sq1Block, trigo.RND() % 128 + 64);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.OnlyBackground_ref, Sq4Block, trigo.RND() % 64 + 16);
        chaosObjects.FillCond(0, 0, 63, 63, chaosObjects.OnlyBackground_ref, EmptyBlock);
    }

    private void GrooveTrans() {
        // VAR
        int c = 0;

        for (c = 0; c <= 5; c++) {
            chaosGraphics.SetTrans(c + 9, 255 - (5 - c) * (chaosBase.difficulty - 1) * 5);
        }
    }

    private void IceTrans(int deg) {
        // VAR
        int c = 0;
        int t = 0;

        t = deg + trigo.RND() % (256 - deg) - chaosBase.difficulty * 6;
        for (c = 8; c <= 15; c++) {
            chaosGraphics.SetTrans(c, t);
        }
    }

    private void AnimTrans() {
        chaosGraphics.SetTrans(8, 128);
        chaosGraphics.SetTrans(12, 128);
        chaosGraphics.SetTrans(13, 192);
        chaosGraphics.SetTrans(14, 128);
    }

    private void DrawGround() {
        chaosObjects.Fill(0, 0, 63, 63, Ground);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.All_ref, Ground2, trigo.RND() % 128 + 128);
    }

    private void DrawFactory() {
        // VAR
        int c = 0;
        Runtime.Ref<Integer> a = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int sz = 0;
        int d = 0;

        chaosObjects.Fill(0, 0, 63, 63, SimpleBlock);
        chaosObjects.Fill(1, 1, 62, 62, BackNone);
        for (c = 1; c <= 20; c++) {
            sz = trigo.RND() % 4 + 1;
            d = sz * 2 + 1;
            if (chaosGenerator.FindIsolatedRect(0, 0, 63, 63, sz, 60, a, x, y, false))
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, SimpleBlock);
        }
        chaosObjects.FillRandom(1, 1, 62, 62, Granit1, Granit2, chaosObjects.OnlyWall_ref, chaosObjects.Rnd_ref);
        chaosObjects.PutRandom(1, 1, 62, 62, chaosObjects.OnlyBackground_ref, EmptyBlock, trigo.RND() % 128);
        chaosObjects.PutRandom(1, 1, 62, 62, chaosObjects.OnlyBackground_ref, Sq1Block, trigo.RND() % 128);
        chaosObjects.PutRandom(1, 1, 62, 62, chaosObjects.OnlyBackground_ref, Sq4Block, trigo.RND() % 128);
        chaosObjects.PutRandom(1, 1, 62, 62, chaosObjects.OnlyBackground_ref, Sq4TravBlock, trigo.RND() % 128);
        chaosObjects.PutRandom(1, 1, 62, 62, chaosObjects.OnlyBackground_ref, TravBlock, trigo.RND() % 128);
        chaosObjects.PutRandom(15, 15, 44, 44, chaosObjects.OnlyBackground_ref, Fact1Block, trigo.RND() % 128);
        chaosObjects.PutRandom(15, 15, 44, 44, chaosObjects.OnlyBackground_ref, Fact2Block, trigo.RND() % 128);
        chaosObjects.PutRandom(15, 15, 44, 44, chaosObjects.OnlyBackground_ref, Fact3Block, trigo.RND() % 128);
        chaosObjects.PutRandom(1, 1, 62, 62, chaosObjects.OnlyBackground_ref, BigBlock, trigo.RND() % 16);
        chaosObjects.FillCond(1, 1, 62, 62, chaosObjects.OnlyBackground_ref, SimpleBlock);
    }

    private void DrawFactory2() {
        // VAR
        int c = 0;
        Runtime.Ref<Integer> a = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int sz = 0;
        int d = 0;

        chaosObjects.Fill(0, 0, 39, 39, SimpleBlock);
        chaosObjects.Fill(1, 1, 38, 38, BackNone);
        for (c = 1; c <= 10; c++) {
            sz = trigo.RND() % 3 + 1;
            d = sz * 2 + 1;
            if (chaosGenerator.FindIsolatedRect(0, 0, 39, 39, sz, 40, a, x, y, false))
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, SimpleBlock);
        }
        chaosObjects.FillRandom(1, 1, 62, 62, Granit1, Granit2, chaosObjects.OnlyWall_ref, chaosObjects.Rnd_ref);
        for (c = 1; c <= 10; c++) {
            sz = trigo.RND() % 3 + 1;
            d = sz * 2 + 1;
            if (chaosGenerator.FindIsolatedRect(0, 0, 39, 39, sz, 40, a, x, y, false))
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, Bricks);
        }
        chaosObjects.PutRandom(1, 1, 38, 38, chaosObjects.OnlyBackground_ref, Ground, trigo.RND() % 128);
        chaosObjects.PutRandom(1, 1, 38, 38, chaosObjects.OnlyBackground_ref, Ground2, trigo.RND() % 128);
        chaosObjects.PutRandom(1, 1, 38, 38, chaosObjects.OnlyBackground_ref, EmptyBlock, trigo.RND() % 128);
        chaosObjects.PutRandom(1, 1, 38, 38, chaosObjects.OnlyBackground_ref, Sq1Block, trigo.RND() % 128);
        chaosObjects.PutRandom(1, 1, 38, 38, chaosObjects.OnlyBackground_ref, Sq4Block, trigo.RND() % 128);
        chaosObjects.PutRandom(1, 1, 38, 38, chaosObjects.OnlyBackground_ref, Sq4TravBlock, trigo.RND() % 128);
        chaosObjects.PutRandom(1, 1, 38, 38, chaosObjects.OnlyBackground_ref, TravBlock, trigo.RND() % 128);
        chaosObjects.PutRandom(1, 1, 38, 38, chaosObjects.OnlyBackground_ref, BarDark, trigo.RND() % 128);
        chaosObjects.PutRandom(15, 15, 44, 44, chaosObjects.OnlyBackground_ref, Fact2Block, trigo.RND() % 16);
        chaosObjects.PutRandom(15, 15, 44, 44, chaosObjects.OnlyBackground_ref, Fact1Block, trigo.RND() % 8);
        chaosObjects.PutRandom(15, 15, 44, 44, chaosObjects.OnlyBackground_ref, Fact3Block, trigo.RND() % 8);
        chaosObjects.PutRandom(1, 1, 62, 62, chaosObjects.OnlyBackground_ref, BigBlock, trigo.RND() % 16);
        chaosObjects.FillCond(1, 1, 62, 62, chaosObjects.OnlyBackground_ref, SimpleBlock);
    }

    private void DrawGroundLeaves() {
        // VAR
        int c = 0;

        DrawGround();
        for (c = Leaf1; c <= Leaf4; c++) {
            chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.All_ref, c, trigo.RND() % 128 + 16);
        }
    }

    private void DrawSquares() {
        chaosObjects.Fill(0, 0, 63, 63, Back8x8);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.All_ref, Back2x2, trigo.RND() % 256);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.All_ref, Back4x4, trigo.RND() % 256);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.All_ref, BackSmall, trigo.RND() % 256);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.All_ref, BackBig, trigo.RND() % 256);
    }

    private void DrawBalls() {
        chaosObjects.Fill(0, 0, 63, 63, Balls);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.All_ref, IceBlock, trigo.RND() % 256);
    }

    private void DrawGranit() {
        chaosObjects.FillRandom(0, 0, 63, 63, Granit1, Granit2, chaosObjects.All_ref, chaosObjects.Rnd_ref);
    }

    private void DrawCastle() {
        chaosObjects.Fill(0, 0, 63, 63, Bricks);
    }

    private void DrawRound4(int n, int m) {
        // VAR
        int x = 0;
        int y = 0;
        int c = 0;
        int r = 0;
        int val = 0;

        chaosObjects.Fill(0, 0, 63, 63, Round4);
        for (c = 0; c <= trigo.RND() % n + m; c++) {
            x = trigo.RND() % 64;
            r = trigo.RND() % 7;
            switch (r) {
                case 0, 1, 2 -> val = BarDark;
                case 3, 4 -> val = Sq4Block;
                case 5 -> val = Sq1Block;
                case 6 -> val = BigBlock;
                default -> throw new RuntimeException("Unhandled CASE value " + r);
            }
            chaosObjects.Fill(x, 0, x, 63, val);
            y = trigo.RND() % 64;
            r = trigo.RND() % 7;
            switch (r) {
                case 0, 1, 2 -> val = BarDark;
                case 3, 4 -> val = Sq4Block;
                case 5 -> val = Sq1Block;
                case 6 -> val = BigBlock;
                default -> throw new RuntimeException("Unhandled CASE value " + r);
            }
            chaosObjects.Fill(0, y, 63, y, val);
        }
    }

    private void DrawLights(int n) {
        // VAR
        int x = 0;
        int y = 0;
        int c = 0;

        chaosObjects.Fill(0, 0, 63, 63, Light);
        for (c = 0; c <= trigo.RND() % n; c++) {
            x = trigo.RND() % 64;
            chaosObjects.Fill(x, 0, x, 63, BarLight);
        }
        for (c = 0; c <= trigo.RND() % n; c++) {
            y = trigo.RND() % 64;
            chaosObjects.Fill(0, y, 63, y, BarLight);
        }
    }

    private void DrawForest() {
        chaosObjects.FillRandom(0, 0, 63, 63, Forest1, Forest7, chaosObjects.All_ref, chaosObjects.Rnd_ref);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.All_ref, Leaf2, trigo.RND() % 32);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.All_ref, Leaf3, trigo.RND() % 128);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.All_ref, Ground, trigo.RND() % 64);
        chaosObjects.PutRandom(0, 0, 63, 63, chaosObjects.All_ref, Ground2, trigo.RND() % 16);
    }

    private void DrawFade() {
        // VAR
        int x = 0;
        int y = 0;

        for (y = 0; y <= 63; y++) {
            for (x = 0; x <= 63; x++) {
                chaosObjects.Put(x, y, Fade1 + trigo.RND() % 3);
            }
        }
    }

    public void InstallDual() {
        chaosImages.InitDualPalette();
        ResetTrans();
        if (chaosBase.zone == Zone.Castle) {
            switch (chaosBase.level[Zone.Castle.ordinal()]) {
                case 1 -> {
                    DrawEntry();
                    chaosGraphics.dualSpeed = 2;
                }
                case 2 -> {
                    DrawStars();
                    chaosGraphics.dualSpeed = 8;
                    GrooveTrans();
                }
                case 3, 5, 10, 19 -> {
                    DrawStars();
                    chaosGraphics.dualSpeed = 8;
                }
                case 4 -> {
                    DrawGround();
                    chaosGraphics.dualSpeed = 3;
                }
                case 6 -> {
                    DrawRound4(15, 10);
                    IceTrans(160);
                    chaosGraphics.dualSpeed = 5;
                }
                case 7, 16 -> {
                    DrawStars();
                    AnimTrans();
                    chaosGraphics.dualSpeed = 8;
                }
                case 8 -> {
                    if (chaosBase.difficulty >= 7)
                        DrawLights(1);
                    else
                        DrawSquares();
                    IceTrans(128);
                    chaosGraphics.dualSpeed = 4;
                }
                case 9 -> {
                    DrawFactory();
                    chaosGraphics.SetTrans(8, 128);
                    chaosGraphics.dualSpeed = 2;
                }
                case 11 -> {
                    DrawSquares();
                    chaosGraphics.dualSpeed = 3;
                }
                case 12 -> {
                    DrawLights(1);
                    AnimTrans();
                    chaosGraphics.dualSpeed = 6;
                }
                case 13 -> {
                    if (chaosBase.difficulty == 7)
                        DrawLights(1);
                    else
                        DrawRound4(2, 2);
                    IceTrans(192);
                    chaosGraphics.dualSpeed = 3;
                }
                case 14 -> {
                    DrawGranit();
                    chaosGraphics.dualSpeed = 3;
                }
                case 15 -> {
                    DrawGroundLeaves();
                    chaosGraphics.dualSpeed = 3;
                }
                case 17 -> {
                    DrawCastle();
                    chaosGraphics.dualSpeed = 4;
                }
                case 18 -> {
                    DrawBalls();
                    chaosGraphics.dualSpeed = 2;
                }
                case 20 -> {
                    DrawFactory2();
                    chaosGraphics.dualSpeed = 2;
                }
                default -> throw new RuntimeException("Unhandled CASE value " + chaosBase.level[Zone.Castle.ordinal()]);
            }
        } else if (chaosBase.zone == Zone.Family) {
            switch (chaosBase.level[Zone.Family.ordinal()]) {
                case 1 -> {
                    DrawForest();
                    chaosGraphics.dualSpeed = 4;
                }
                case 2 -> {
                    DrawGround();
                    chaosGraphics.dualSpeed = 4;
                }
                case 3, 7, 8, 10 -> {
                    DrawStars();
                    chaosGraphics.dualSpeed = 7;
                }
                case 4 -> {
                    DrawGround();
                    chaosGraphics.dualSpeed = 3;
                }
                case 5 -> {
                    DrawForest();
                    IceTrans(128);
                    chaosGraphics.dualSpeed = 3;
                }
                case 6 -> {
                    DrawFactory2();
                    IceTrans(160);
                    chaosGraphics.dualSpeed = 3;
                }
                case 9 -> {
                    DrawForest();
                    chaosGraphics.dualSpeed = 2;
                }
                default -> throw new RuntimeException("Unhandled CASE value " + chaosBase.level[Zone.Family.ordinal()]);
            }
        } else if (chaosBase.zone == Zone.Special) {
            if (chaosBase.level[Zone.Special.ordinal()] == 24) {
                DrawStars();
                chaosGraphics.dualSpeed = 10;
            } else if (chaosBase.level[Zone.Special.ordinal()] % 8 == 0) {
                DrawFade();
                chaosGraphics.dualSpeed = 2;
            } else if (chaosBase.level[Zone.Special.ordinal()] % 4 == 0) {
                DrawStars();
                chaosGraphics.dualSpeed = 8;
            } else if (chaosBase.level[Zone.Special.ordinal()] % 2 == 0) {
                DrawGroundLeaves();
                chaosGraphics.dualSpeed = 3;
            } else {
                DrawForest();
                chaosGraphics.dualSpeed = 4;
            }
        }
        chaosGraphics.CopyToDual();
    }


    // Support

    private static ChaosDual instance;

    public static ChaosDual instance() {
        if (instance == null)
            new ChaosDual(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
    }

    public void close() {
    }

}
