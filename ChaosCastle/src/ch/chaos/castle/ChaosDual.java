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
        short c = 0;

        chaosGraphics.SetTrans((short) 0, (short) 0);
        for (c = 1; c <= 15; c++) {
            chaosGraphics.SetTrans(c, (short) 255);
        }
    }

    private void DrawStars() {
        chaosObjects.FillRandom((short) 0, (short) 0, (short) 63, (short) 63, (short) 0, (short) 7, chaosObjects.All_ref, chaosObjects.ExpRandom_ref);
    }

    private void DrawEntry_DrawLetter_DrawPattern(int pat, /* VAR */ Runtime.IRef<Short> z) {
        // VAR
        short x = 0;

        for (x = 0; x <= 8; x++) {
            if (((pat % 2) != 0)) {
                chaosObjects.Set(z.get(), (short) (9 - x));
                chaosObjects.Set((short) (x + 2), z.get());
            }
            pat = pat / 2;
        }
        z.inc();
    }

    private void DrawEntry_DrawLetter(int b1, int b2, int b3, int b4, int b5, short add, /* VAR */ Runtime.IRef<Short> z) {
        DrawEntry_DrawLetter_DrawPattern(b1, z);
        DrawEntry_DrawLetter_DrawPattern(b2, z);
        DrawEntry_DrawLetter_DrawPattern(b3, z);
        DrawEntry_DrawLetter_DrawPattern(b4, z);
        DrawEntry_DrawLetter_DrawPattern(b5, z);
        z.set((short) (z.get() - 5 + add));
    }

    private void DrawEntry() {
        // VAR
        Runtime.Ref<Short> z = new Runtime.Ref<>((short) 0);

        z.set((short) 6);
        DrawEntry_DrawLetter(124, 130, 257, 257, 257, (short) 6, z);
        DrawEntry_DrawLetter(511, 16, 16, 15, 0, (short) 5, z);
        DrawEntry_DrawLetter(14, 17, 10, 31, 0, (short) 5, z);
        DrawEntry_DrawLetter(14, 17, 17, 17, 14, (short) 6, z);
        DrawEntry_DrawLetter(9, 21, 21, 21, 18, (short) 6, z);
        DrawEntry_DrawLetter(124, 130, 257, 257, 257, (short) 6, z);
        DrawEntry_DrawLetter(14, 17, 10, 31, 0, (short) 5, z);
        DrawEntry_DrawLetter(9, 21, 21, 21, 18, (short) 6, z);
        DrawEntry_DrawLetter(32, 510, 33, 0, 0, (short) 4, z);
        DrawEntry_DrawLetter(511, 0, 0, 0, 0, (short) 2, z);
        DrawEntry_DrawLetter(14, 21, 21, 21, 8, (short) 5, z);
        chaosObjects.FillCond((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.OnlyWall_ref, (short) SimpleBlock);
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.OnlyWall_ref, (short) Leaf2, (short) (trigo.RND() % 64 + 32));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.OnlyWall_ref, (short) Leaf3, (short) (trigo.RND() % 16 + 4));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.OnlyBackground_ref, (short) Sq1Block, (short) (trigo.RND() % 128 + 64));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.OnlyBackground_ref, (short) Sq4Block, (short) (trigo.RND() % 64 + 16));
        chaosObjects.FillCond((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.OnlyBackground_ref, (short) EmptyBlock);
    }

    private void GrooveTrans() {
        // VAR
        int c = 0;

        for (c = 0; c <= 5; c++) {
            chaosGraphics.SetTrans((short) (c + 9), (short) (255 - (5 - c) * (chaosBase.difficulty - 1) * 5));
        }
    }

    private void IceTrans(int deg) {
        // VAR
        int c = 0;
        short t = 0;

        t = (short) (deg + trigo.RND() % (256 - deg) - chaosBase.difficulty * 6);
        for (c = 8; c <= 15; c++) {
            chaosGraphics.SetTrans((short) c, t);
        }
    }

    private void AnimTrans() {
        chaosGraphics.SetTrans((short) 8, (short) 128);
        chaosGraphics.SetTrans((short) 12, (short) 128);
        chaosGraphics.SetTrans((short) 13, (short) 192);
        chaosGraphics.SetTrans((short) 14, (short) 128);
    }

    private void DrawGround() {
        chaosObjects.Fill((short) 0, (short) 0, (short) 63, (short) 63, (short) Ground);
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.All_ref, (short) Ground2, (short) (trigo.RND() % 128 + 128));
    }

    private void DrawFactory() {
        // VAR
        short c = 0;
        Runtime.Ref<Short> a = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short sz = 0;
        short d = 0;

        chaosObjects.Fill((short) 0, (short) 0, (short) 63, (short) 63, (short) SimpleBlock);
        chaosObjects.Fill((short) 1, (short) 1, (short) 62, (short) 62, (short) BackNone);
        for (c = 1; c <= 20; c++) {
            sz = (short) (trigo.RND() % 4 + 1);
            d = (short) (sz * 2 + 1);
            if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 63, (short) 63, sz, (short) 60, a, x, y, false))
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) SimpleBlock);
        }
        chaosObjects.FillRandom((short) 1, (short) 1, (short) 62, (short) 62, (short) Granit1, (short) Granit2, chaosObjects.OnlyWall_ref, chaosObjects.Rnd_ref);
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 62, (short) 62, chaosObjects.OnlyBackground_ref, (short) EmptyBlock, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 62, (short) 62, chaosObjects.OnlyBackground_ref, (short) Sq1Block, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 62, (short) 62, chaosObjects.OnlyBackground_ref, (short) Sq4Block, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 62, (short) 62, chaosObjects.OnlyBackground_ref, (short) Sq4TravBlock, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 62, (short) 62, chaosObjects.OnlyBackground_ref, (short) TravBlock, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 15, (short) 15, (short) 44, (short) 44, chaosObjects.OnlyBackground_ref, (short) Fact1Block, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 15, (short) 15, (short) 44, (short) 44, chaosObjects.OnlyBackground_ref, (short) Fact2Block, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 15, (short) 15, (short) 44, (short) 44, chaosObjects.OnlyBackground_ref, (short) Fact3Block, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 62, (short) 62, chaosObjects.OnlyBackground_ref, (short) BigBlock, (short) (trigo.RND() % 16));
        chaosObjects.FillCond((short) 1, (short) 1, (short) 62, (short) 62, chaosObjects.OnlyBackground_ref, (short) SimpleBlock);
    }

    private void DrawFactory2() {
        // VAR
        short c = 0;
        Runtime.Ref<Short> a = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short sz = 0;
        short d = 0;

        chaosObjects.Fill((short) 0, (short) 0, (short) 39, (short) 39, (short) SimpleBlock);
        chaosObjects.Fill((short) 1, (short) 1, (short) 38, (short) 38, (short) BackNone);
        for (c = 1; c <= 10; c++) {
            sz = (short) (trigo.RND() % 3 + 1);
            d = (short) (sz * 2 + 1);
            if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 39, (short) 39, sz, (short) 40, a, x, y, false))
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) SimpleBlock);
        }
        chaosObjects.FillRandom((short) 1, (short) 1, (short) 62, (short) 62, (short) Granit1, (short) Granit2, chaosObjects.OnlyWall_ref, chaosObjects.Rnd_ref);
        for (c = 1; c <= 10; c++) {
            sz = (short) (trigo.RND() % 3 + 1);
            d = (short) (sz * 2 + 1);
            if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 39, (short) 39, sz, (short) 40, a, x, y, false))
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) Bricks);
        }
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 38, (short) 38, chaosObjects.OnlyBackground_ref, (short) Ground, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 38, (short) 38, chaosObjects.OnlyBackground_ref, (short) Ground2, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 38, (short) 38, chaosObjects.OnlyBackground_ref, (short) EmptyBlock, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 38, (short) 38, chaosObjects.OnlyBackground_ref, (short) Sq1Block, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 38, (short) 38, chaosObjects.OnlyBackground_ref, (short) Sq4Block, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 38, (short) 38, chaosObjects.OnlyBackground_ref, (short) Sq4TravBlock, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 38, (short) 38, chaosObjects.OnlyBackground_ref, (short) TravBlock, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 38, (short) 38, chaosObjects.OnlyBackground_ref, (short) BarDark, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 15, (short) 15, (short) 44, (short) 44, chaosObjects.OnlyBackground_ref, (short) Fact2Block, (short) (trigo.RND() % 16));
        chaosObjects.PutRandom((short) 15, (short) 15, (short) 44, (short) 44, chaosObjects.OnlyBackground_ref, (short) Fact1Block, (short) (trigo.RND() % 8));
        chaosObjects.PutRandom((short) 15, (short) 15, (short) 44, (short) 44, chaosObjects.OnlyBackground_ref, (short) Fact3Block, (short) (trigo.RND() % 8));
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 62, (short) 62, chaosObjects.OnlyBackground_ref, (short) BigBlock, (short) (trigo.RND() % 16));
        chaosObjects.FillCond((short) 1, (short) 1, (short) 62, (short) 62, chaosObjects.OnlyBackground_ref, (short) SimpleBlock);
    }

    private void DrawGroundLeaves() {
        // VAR
        short c = 0;

        DrawGround();
        for (c = Leaf1; c <= Leaf4; c++) {
            chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.All_ref, c, (short) (trigo.RND() % 128 + 16));
        }
    }

    private void DrawSquares() {
        chaosObjects.Fill((short) 0, (short) 0, (short) 63, (short) 63, (short) Back8x8);
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.All_ref, (short) Back2x2, (short) (trigo.RND() % 256));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.All_ref, (short) Back4x4, (short) (trigo.RND() % 256));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.All_ref, (short) BackSmall, (short) (trigo.RND() % 256));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.All_ref, (short) BackBig, (short) (trigo.RND() % 256));
    }

    private void DrawBalls() {
        chaosObjects.Fill((short) 0, (short) 0, (short) 63, (short) 63, (short) Balls);
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.All_ref, (short) IceBlock, (short) (trigo.RND() % 256));
    }

    private void DrawGranit() {
        chaosObjects.FillRandom((short) 0, (short) 0, (short) 63, (short) 63, (short) Granit1, (short) Granit2, chaosObjects.All_ref, chaosObjects.Rnd_ref);
    }

    private void DrawCastle() {
        chaosObjects.Fill((short) 0, (short) 0, (short) 63, (short) 63, (short) Bricks);
    }

    private void DrawRound4(int n, int m) {
        // VAR
        short x = 0;
        short y = 0;
        int c = 0;
        int r = 0;
        short val = 0;

        chaosObjects.Fill((short) 0, (short) 0, (short) 63, (short) 63, (short) Round4);
        for (c = 0; c <= trigo.RND() % n + m; c++) {
            x = (short) (trigo.RND() % 64);
            r = trigo.RND() % 7;
            switch (r) {
                case 0, 1, 2 -> val = BarDark;
                case 3, 4 -> val = Sq4Block;
                case 5 -> val = Sq1Block;
                case 6 -> val = BigBlock;
                default -> throw new RuntimeException("Unhandled CASE value " + r);
            }
            chaosObjects.Fill(x, (short) 0, x, (short) 63, val);
            y = (short) (trigo.RND() % 64);
            r = trigo.RND() % 7;
            switch (r) {
                case 0, 1, 2 -> val = BarDark;
                case 3, 4 -> val = Sq4Block;
                case 5 -> val = Sq1Block;
                case 6 -> val = BigBlock;
                default -> throw new RuntimeException("Unhandled CASE value " + r);
            }
            chaosObjects.Fill((short) 0, y, (short) 63, y, val);
        }
    }

    private void DrawLights(int n) {
        // VAR
        short x = 0;
        short y = 0;
        int c = 0;

        chaosObjects.Fill((short) 0, (short) 0, (short) 63, (short) 63, (short) Light);
        for (c = 0; c <= trigo.RND() % n; c++) {
            x = (short) (trigo.RND() % 64);
            chaosObjects.Fill(x, (short) 0, x, (short) 63, (short) BarLight);
        }
        for (c = 0; c <= trigo.RND() % n; c++) {
            y = (short) (trigo.RND() % 64);
            chaosObjects.Fill((short) 0, y, (short) 63, y, (short) BarLight);
        }
    }

    private void DrawForest() {
        chaosObjects.FillRandom((short) 0, (short) 0, (short) 63, (short) 63, (short) Forest1, (short) Forest7, chaosObjects.All_ref, chaosObjects.Rnd_ref);
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.All_ref, (short) Leaf2, (short) (trigo.RND() % 32));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.All_ref, (short) Leaf3, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.All_ref, (short) Ground, (short) (trigo.RND() % 64));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 63, (short) 63, chaosObjects.All_ref, (short) Ground2, (short) (trigo.RND() % 16));
    }

    private void DrawFade() {
        // VAR
        short x = 0;
        short y = 0;

        for (y = 0; y <= 63; y++) {
            for (x = 0; x <= 63; x++) {
                chaosObjects.Put(x, y, (short) (Fade1 + trigo.RND() % 3));
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
                    chaosGraphics.SetTrans((short) 8, (short) 128);
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
                case 3, 5, 6, 10 -> {
                    DrawStars();
                    chaosGraphics.dualSpeed = 7;
                }
                case 4 -> {
                    DrawGround();
                    chaosGraphics.dualSpeed = 3;
                }
                case 7 -> {
                    DrawForest();
                    IceTrans(128);
                    chaosGraphics.dualSpeed = 3;
                }
                case 8 -> {
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
