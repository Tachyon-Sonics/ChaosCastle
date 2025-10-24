package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus.Moneys;
import ch.chaos.library.Memory;
import ch.chaos.library.Registration;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class Chaos2Zone {

    // Imports
    private final Chaos1Zone chaos1Zone;
    private final ChaosBase chaosBase;
    private final ChaosGenerator chaosGenerator;
    private final ChaosGraphics chaosGraphics;
    private final ChaosObjects chaosObjects;
    private final Trigo trigo;


    private Chaos2Zone() {
        instance = this; // Set early to handle circular dependencies
        chaos1Zone = Chaos1Zone.instance();
        chaosBase = ChaosBase.instance();
        chaosGenerator = ChaosGenerator.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosObjects = ChaosObjects.instance();
        trigo = Trigo.instance();
    }


    // CONST

    /* Drawing procs */
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
    /* Levels creation */
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

    /* Level 11 */
    public void Rooms() {
        // VAR
        int c = 0;
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int sz = 0;
        int d = 0;
        Runtime.Ref<Integer> angle = new Runtime.Ref<>(0);
        int val = 0;

        chaosObjects.Clear(60, 60);
        chaosObjects.Fill(0, 0, 59, 59, EmptyBlock);
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Back8x8;
        chaosObjects.Fill(10, 9, 49, 11, val);
        chaosObjects.Fill(10, 29, 49, 31, val);
        chaosObjects.Fill(47, 12, 49, 28, val);
        chaosObjects.Fill(10, 32, 12, 51, val);
        chaosObjects.PutPlayer(11, 10);
        chaosObjects.PutExit(11, 50);
        chaosObjects.Rect(11, 9, 48, 11);
        chaosObjects.PutMachine(ChaosMachine.mTraverse, 1, 1, 1);
        chaosObjects.Rect(11, 29, 48, 31);
        chaosObjects.PutMachine(ChaosMachine.mTraverse, 1, 1, 3);
        chaos1Zone.fillTypes = new Runtime.RangeSet(Memory.SET16_r).with(fAlienColor, fAlienFour, fCartoon, fNone, fAnims1, fAnims2, fAnims3, fAnims4);
        for (c = 0; c <= 15; c++) {
            chaos1Zone.fillCount[c] = 3;
            chaos1Zone.fillRndAdd[c] = 2;
        }
        chaos1Zone.fKind[0] = Anims.ALIEN2;
        chaos1Zone.fSubKind[0] = ChaosCreator.cNest;
        chaos1Zone.aStat[0] = 0;
        chaos1Zone.fKind[1] = Anims.ALIEN2;
        chaos1Zone.fSubKind[1] = ChaosCreator.cQuad;
        chaos1Zone.aStat[1] = chaos1Zone.pLife3;
        chaos1Zone.fKind[2] = Anims.ALIEN2;
        chaos1Zone.fSubKind[2] = ChaosCreator.cCreatorC;
        chaos1Zone.aStat[2] = chaos1Zone.pLife3 + 40 + chaosBase.difficulty * 4;
        chaos1Zone.fKind[3] = Anims.ALIEN2;
        chaos1Zone.fSubKind[3] = ChaosCreator.cCreatorR;
        chaos1Zone.aStat[3] = chaos1Zone.pLife3 + 40 + chaosBase.difficulty * 4;
        for (c = 1; c <= 40; c++) {
            sz = trigo.RND() % 4 + 1;
            d = sz * 2 + 1;
            if (chaosGenerator.FindIsolatedRect(0, 0, 59, 59, sz, 4, angle, x, y, true)) {
                if (trigo.RND() % 2 == 0)
                    chaosObjects.Fill(x.get() - sz, y.get() - sz, x.get() + sz, y.get() + sz, Back4x4);
                else
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, Back4x4);
                chaos1Zone.RectFill(x.get() - sz, y.get() - sz, x.get() + sz, y.get() + sz);
                if (c >= 34)
                    val = FalseEmpty;
                else
                    val = Back2x2;
                chaosGenerator.MakeLink(x.get(), y.get(), sz, angle.get(), val);
            }
        }
        chaosObjects.PutRandom(0, 0, 29, 29, chaosObjects.OnlyWall_ref, Sq1Block, 60);
        chaosObjects.PutRandom(30, 0, 59, 29, chaosObjects.OnlyWall_ref, Sq4Block, 60);
        chaosObjects.PutRandom(30, 30, 59, 59, chaosObjects.OnlyWall_ref, Sq4TravBlock, 60);
        chaosObjects.PutRandom(0, 30, 29, 59, chaosObjects.OnlyWall_ref, BigBlock, 60);
        chaosObjects.PutGridObjs(Anims.BONUS, ChaosBonus.TimedBonus, ChaosBonus.tbHospital, 10, 10, 19, 20, 2, 2);
        chaosObjects.Rect(1, 40, 58, 58);
        chaosObjects.PutRAlien2(ChaosCreator.cAlienBox, 0, 1, 5);
        chaosObjects.Rect(1, 1, 58, 58);
        chaosObjects.PutBullet(8);
        chaosObjects.Rect(1, 1, 29, 20);
        chaosObjects.PutInvinsibility(1);
        chaosObjects.Rect(30, 1, 58, 20);
        chaosObjects.PutFreeFire(1);
        chaosObjects.Rect(30, 21, 58, 40);
        chaosObjects.PutSleeper(1);
        chaosObjects.Rect(1, 21, 29, 40);
        chaosObjects.PutMaxPower(1);
        chaosObjects.Rect(1, 35, 58, 58);
        chaosObjects.PutMagnet(1);
        chaosObjects.Rect(1, 1, 58, 58);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        if (chaosBase.powerCountDown > 1)
            chaosObjects.PutRandomObjs(Anims.SMARTBONUS, ChaosSmartBonus.sbExtraPower, 0, 1);
        chaosObjects.PutRandomObjs(Anims.SMARTBONUS, ChaosSmartBonus.sbExtraLife, 0, 2);
        chaos1Zone.AddOptions(1, 1, 58, 58, 0, 0, 1, 0, 10, 0, 0);
    }

    /* Level 12 */
    private void Yard_RndRect() {
        // VAR
        int x = 0;
        int y = 0;

        x = chaosObjects.Rnd(35);
        y = chaosObjects.Rnd(35);
        chaosObjects.Rect(x, y, x + 3, y + 3);
    }

    public void Yard() {
        chaosObjects.Cadre(40, 40);
        chaosObjects.FillCond(0, 0, 40, 40, chaosObjects.OnlyWall_ref, TravLight);
        chaosObjects.Rect(1, 1, 38, 38);
        chaosObjects.PutIsolated(5, 20, 10, 30, BarDark);
        chaosObjects.FillRandom(1, 1, 39, 39, Back4x4, Back8x8, chaosObjects.OnlyBackground_ref, chaosObjects.ExpRandom_ref);
        chaosObjects.FillCond(18, 18, 22, 22, chaosObjects.OnlyBackground_ref, Light);
        if (chaosGraphics.dualpf)
            chaosObjects.PutRandom(1, 1, 39, 39, chaosObjects.OnlyBackground_ref, BackNone, 255);
        chaosObjects.PutPlayer(2, 2);
        if ((chaosBase.nbSterling >= 190) && (chaosBase.difficulty >= 2)) {
            chaosObjects.PutBlockObj(Anims.ALIEN3, ChaosBoss.bMasterEye, 0, 19, 20);
            chaosObjects.PutBlockObj(Anims.ALIEN3, ChaosBoss.bMasterEye, 1, 21, 20);
            chaosObjects.PutBlockObj(Anims.ALIEN3, ChaosBoss.bMasterMouth, 0, 20, 20);
            chaosObjects.PutBlockObj(Anims.ALIEN3, ChaosBoss.bMasterAlien1, 0, 20, 20);
        }
        chaosObjects.Rect(20, 20, 38, 38);
        chaosObjects.PutTBonus(ChaosBonus.tbExit, 1);
        chaosObjects.Rect(3, 3, 38, 38);
        chaosObjects.PutMagnetR(3, 15);
        chaosObjects.PutMagnetA(3, 15);
        chaosObjects.FillObj(Anims.ALIEN1, ChaosAlien.aCartoon, 0, 1, 19, 38, 19, false);
        chaosObjects.Rect(10, 3, 29, 38);
        chaosObjects.PutMachine(ChaosMachine.mCannon1, 0, 1, 5);
        chaosObjects.Rect(3, 10, 38, 29);
        chaosObjects.PutMachine(ChaosMachine.mCannon2, 0, 1, 5);
        chaosObjects.Rect(10, 10, 29, 29);
        chaosObjects.PutCannon3(5);
        chaosObjects.PutTurret(7);
        chaosObjects.Rect(3, 3, 38, 38);
        chaosObjects.PutAlien1(ChaosAlien.aHospital, chaos1Zone.pLife3, 10);
        chaosObjects.PutRAlien1(ChaosAlien.aKamikaze, 0, 3, 10);
        chaosObjects.PutAlien2(ChaosCreator.cAlienV, chaos1Zone.pLife3, 10);
        chaosObjects.PutFour(chaos1Zone.pLife3, 10);
        chaosObjects.PutBullet(4);
        Yard_RndRect();
        chaosObjects.PutTrefle(chaos1Zone.pLife2, 5);
        Yard_RndRect();
        chaosObjects.PutAlien2(ChaosCreator.cAlienA, chaos1Zone.pLife3, 10);
        Yard_RndRect();
        chaosObjects.PutTri(chaos1Zone.pLife3, 5);
        chaosObjects.Rect(20, 20, 38, 38);
        chaosObjects.PutNest(1, 15);
        chaosObjects.Rect(1, 1, 7, 7);
        chaosObjects.PutFreeFire(1);
        chaosObjects.PutMaxPower(1);
        chaos1Zone.AddOptions(10, 10, 38, 38, 5, 5, 1, 3, 0, 5, 3);
    }

    /* Level 13 */
    public void Antarctica() {
        // CONST
        final int W = 120;
        final int H = 60;
        final int MX = W / 2;
        final int MY = H / 2;

        // VAR
        Runtime.Ref<Integer> lx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> ly = new Runtime.Ref<>(0);
        int sz = 0;
        Runtime.Ref<Integer> at = new Runtime.Ref<>(0);
        int c = 0;
        int val = 0;

        chaosObjects.Clear(W, H);
        chaosBase.water = (chaosBase.difficulty < 5) || (trigo.RND() % 3 != 0);
        chaosBase.snow = (chaosBase.stages > 0) || (trigo.RND() % 3 != 0);
        chaosObjects.Fill(0, 0, W - 1, H - 1, IceBlock);
        chaosGenerator.VRace(Ice);
        chaosGenerator.MakeLink(W / 2, H * 2 / 5 - 1, 0, 90, Ice);
        chaosObjects.PutPlayer(W / 2, H * 2 / 5);
        chaosObjects.Rect(MX / 2, 0, MX - 1, MY - 1);
        chaosObjects.PutTBonus(ChaosBonus.tbExit, 1);
        chaosObjects.Rect(0, 0, W / 3, MY);
        chaosObjects.PutDeadObj(ChaosDObj.doBubbleMaker, 0, 1);
        chaosObjects.Rect(W * 2 / 3, 0, W - 1, MY - 1);
        chaosObjects.PutDeadObj(ChaosDObj.doBubbleMaker, 0, 1);
        chaosObjects.Rect(0, MY, W / 3, H - 1);
        chaosObjects.PutDeadObj(ChaosDObj.doBubbleMaker, 0, 1);
        chaosObjects.Rect(W * 2 / 3, MY, W - 1, H - 1);
        chaosObjects.PutDeadObj(ChaosDObj.doBubbleMaker, 0, 1);
        chaosObjects.Rect(MX, MY, W * 2 / 3, H - 1);
        chaosObjects.PutTBonus(ChaosBonus.tbDBSpeed, 1);
        chaosObjects.PutChaosObjs(Anims.DEADOBJ, ChaosDObj.doSand, 0, 0, 0, W * 10, H * 10, 20);
        chaosObjects.Rect(W * 2 / 3, 0, W - 1, H - 1);
        chaosObjects.PutDeadObj(ChaosDObj.doWindMaker, 0, 4);
        chaosObjects.Rect(0, MY, MX, H - 1);
        chaosObjects.PutTurret(4);
        chaosObjects.Rect(1, 1, W - 2, H - 2);
        chaosObjects.PutMagnetR(2, 8);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doMirror, 0, 1, 1, 15);
        chaosObjects.PutCannon3(2);
        chaosObjects.PutMachine(ChaosMachine.mTraverse, 0, 1, 4);
        chaosObjects.PutNest(0, 4 + chaosBase.difficulty);
        chaosObjects.PutCartoon(0, 2, 15);
        chaosObjects.Rect(0, MY, W - 1, H - 1);
        chaosObjects.PutRAlien1(ChaosAlien.aKamikaze, 0, 3, 20);
        chaos1Zone.fillTypes = new Runtime.RangeSet(Memory.SET16_r).with(fNone, fMoneyS, fAlienFour, fAnims1, fAnims2);
        chaos1Zone.fillCount[fMoneyS] = 4;
        chaos1Zone.fillRndAdd[fMoneyS] = 4;
        chaos1Zone.fillCount[fAlienFour] = 1;
        chaos1Zone.fillRndAdd[fAlienFour] = 3;
        chaos1Zone.fillCount[fAnims1] = 1;
        chaos1Zone.fillRndAdd[fAnims1] = 1;
        chaos1Zone.fillCount[fAnims2] = 1;
        chaos1Zone.fillRndAdd[fAnims2] = 0;
        chaos1Zone.fKind[0] = Anims.ALIEN2;
        chaos1Zone.fSubKind[0] = ChaosCreator.cAlienBox;
        chaos1Zone.aStat[0] = 0;
        chaos1Zone.fKind[1] = Anims.BONUS;
        chaos1Zone.fSubKind[1] = ChaosBonus.TimedBonus;
        chaos1Zone.aStat[1] = ChaosBonus.tbBullet;
        for (c = 1; c <= 10 + trigo.RND() % 8; c++) {
            sz = trigo.RND() % 4 + 2;
            if (chaosGenerator.FindIsolatedRect(2, 2, W - 3, H - 3, sz, 30, at, lx, ly, true)) {
                chaosObjects.Fill(lx.get() - sz + 1, ly.get() - sz, lx.get() + sz - 1, ly.get() + sz, Balls);
                chaosObjects.Fill(lx.get() - sz, ly.get() - sz + 1, lx.get() + sz, ly.get() + sz - 1, Balls);
                chaosObjects.Fill(lx.get() - sz + 1, ly.get() - sz + 1, lx.get() + sz - 1, ly.get() + sz - 1, Ice);
                chaosObjects.PutBubbleMaker(0, lx.get(), ly.get());
                chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, lx.get(), ly.get() - 1);
                chaosObjects.PutBlockBonus(ChaosBonus.tbBullet, lx.get(), ly.get() + 1);
                if (c == 16) {
                    if (chaosBase.stages == 0)
                        chaosObjects.PutBlockBonus(ChaosBonus.tbNoMissile, lx.get() + 1, ly.get());
                    chaosObjects.PutExtraPower(1, lx.get() - 1, ly.get());
                }
                chaos1Zone.RectFill(lx.get() - sz, ly.get() - sz, lx.get() + sz, ly.get() + sz);
                if (c > 10)
                    val = FalseBlock;
                else
                    val = Ice;
                chaosGenerator.MakeLink(lx.get(), ly.get(), sz, at.get(), val);
            }
        }
        chaosObjects.Rect(1, 1, W - 2, H - 2);
        chaosObjects.PutRandom(0, 0, W - 1, H - 1, chaosObjects.OnlyWall_ref, SimpleBlock, trigo.RND() % 256);
        chaosObjects.PutRandom(0, 0, W - 1, H - 1, chaosObjects.OnlyWall_ref, BigBlock, trigo.RND() % 256);
        chaosObjects.PutRandom(0, 0, W - 1, H - 1, chaosObjects.OnlyWall_ref, Leaf2, trigo.RND() % 64);
        chaosObjects.PutRandom(0, 0, W - 1, H - 1, chaosObjects.OnlyWall_ref, Leaf3, trigo.RND() % 32);
        chaosObjects.PutRandomObjs(Anims.BONUS, ChaosBonus.BonusLevel, ChaosBonus.tbBonusLevel, 1);
        if ((chaosBase.stages == 0) && (chaosBase.difficulty >= 9) && (trigo.RND() % 4 == 0))
            chaosObjects.PutRandomObjs(Anims.ALIEN3, ChaosBoss.bSisterAlien, 0, 1);
        chaosObjects.PutRandomObjs(Anims.SMARTBONUS, ChaosSmartBonus.sbExtraLife, 0, 1);
        chaos1Zone.AddOptions(1, 1, W - 2, H - 2, 4, 4, 0, 2, 10, 0, 1);
    }

    /* Level 14 */
    public void Forest() {
        // VAR
        int c = 0;
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int sz = 0;
        int d = 0;
        Runtime.Ref<Integer> angle = new Runtime.Ref<>(0);

        chaosObjects.Clear(60, 60);
        chaos1Zone.rotate = false;
        chaosObjects.Fill(0, 0, 59, 59, ChaosGraphics.NbBackground);
        chaosGenerator.FillEllipse(26, 26, 8, 8, Ground);
        chaosObjects.PutPlayer(29, 29);
        chaosObjects.PutExit(30, 30);
        chaos1Zone.fillTypes = new Runtime.RangeSet(Memory.SET16_r).with(fCartoon, fNone, fPic, fAnims2, fAnims3, fAnims4);
        for (c = 0; c <= 15; c++) {
            chaos1Zone.fillCount[c] = 2;
            chaos1Zone.fillRndAdd[c] = 2;
        }
        chaos1Zone.fKind[1] = Anims.ALIEN2;
        chaos1Zone.fSubKind[1] = ChaosCreator.cNest;
        chaos1Zone.aStat[1] = 0;
        chaos1Zone.fKind[2] = Anims.MACHINE;
        chaos1Zone.fSubKind[2] = ChaosMachine.mTurret;
        chaos1Zone.aStat[2] = 0;
        chaos1Zone.fKind[3] = Anims.ALIEN1;
        chaos1Zone.fSubKind[3] = ChaosAlien.aTrefle;
        chaos1Zone.aStat[3] = chaos1Zone.pLife4;
        for (c = 1; c <= 45; c++) {
            sz = trigo.RND() % 4 + 1;
            d = sz * 2 + 1;
            if (chaosGenerator.FindIsolatedRect(0, 0, 59, 59, sz, 10, angle, x, y, true)) {
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, Ground);
                chaos1Zone.RectFill(x.get() - sz, y.get() - sz, x.get() + sz, y.get() + sz);
                chaosGenerator.MakeLink(x.get(), y.get(), sz, angle.get(), Ground2);
            }
        }
        chaosObjects.FillRandom(0, 0, 59, 59, Forest1, Forest7, chaosObjects.OnlyWall_ref, chaosObjects.Rnd_ref);
        chaosObjects.Rect(26, 26, 33, 33);
        for (c = 2; c <= 4; c++) {
            chaosObjects.PutRandomObjs(chaos1Zone.fKind[c - 1], chaos1Zone.fSubKind[c - 1], chaos1Zone.aStat[c - 1], 12);
        }
        chaosObjects.Rect(1, 1, 58, 58);
        chaosObjects.PutBullet(10);
        chaosObjects.PutAlien1(ChaosAlien.aHospital, chaos1Zone.pLife4, 10);
        chaosObjects.PutSleeper(1);
        chaosObjects.PutInvinsibility(1);
        chaosObjects.PutFreeFire(1);
        if (chaosBase.powerCountDown > 2)
            chaosObjects.PutRandomObjs(Anims.SMARTBONUS, ChaosSmartBonus.sbExtraPower, 0, 1);
        if (chaosBase.stages == 0)
            chaosObjects.PutTBonus(ChaosBonus.tbNoMissile, 1);
        chaos1Zone.AddOptions(1, 1, 58, 58, 0, 10, 2, 10, 10, 4, 0);
    }

    /* Level 15 */
    public void ZCastle() {
        // VAR
        int val = 0;

        chaosObjects.Cadre(40, 40);
        chaosBase.snow = (chaosBase.difficulty >= 5) && (trigo.RND() % 3 == 0);
        chaosGenerator.DrawCastle(2, 1, 36, 37);
        chaosObjects.FillCond(0, 0, 39, 39, chaosObjects.OnlyWall_ref, SimpleBlock);
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Back4x4;
        chaosObjects.FillCond(0, 0, 39, 39, chaosObjects.OnlyBackground_ref, val);
        chaosObjects.PutRandom(1, 1, 38, 38, chaosObjects.OnlyBackground_ref, Back8x8, 40);
        chaosObjects.PutRandom(1, 1, 38, 38, chaosObjects.OnlyBackground_ref, Back2x2, 40);
        chaosObjects.PutRandom(1, 1, 38, 38, chaosObjects.OnlyWall_ref, Sq4Block, 10);
        chaosObjects.PutRandom(1, 1, 38, 38, chaosObjects.OnlyWall_ref, BigBlock, 10);
        chaosObjects.Put(1, 18, FalseBlock);
        chaosObjects.PutRandom(1, 1, 38, 38, chaosObjects.OnlyWall_ref, FalseBlock, chaosBase.difficulty);
        chaosObjects.PutPlayer(1, 1);
        chaosObjects.PutExit(38, 38);
        chaosObjects.Rect(1, 1, 38, 19);
        chaosObjects.PutQuad(chaos1Zone.pLife4, 20);
        chaosObjects.Rect(1, 20, 38, 38);
        chaosObjects.PutFour(chaos1Zone.pLife4, 30);
        chaosObjects.Rect(1, 1, 19, 38);
        chaosObjects.PutAlien1(ChaosAlien.aHospital, chaos1Zone.pLife3, 8);
        chaosObjects.PutBullet(8);
        chaosObjects.Rect(1, 1, 38, 38);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.PutCartoon(0, 0, 50);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doWindMaker, 0, 1, 0, 3);
        chaosObjects.Rect(20, 1, 38, 25);
        chaosObjects.PutInvinsibility(1);
        chaosObjects.PutSleeper(1);
        if (chaosBase.difficulty < 5)
            chaosObjects.PutTBonus(ChaosBonus.tbDBSpeed, 1);
        chaos1Zone.AddOptions(1, 20, 38, 38, 1, 20, 1, 10, 10, 0, 0);
    }

    /* Level 16 */
    public void Lights() {
        // VAR
        int x = 0;
        int y = 0;
        int z = 0;
        int val = 0;

        chaosObjects.Clear(127, 20);
        chaosBase.water = (chaosBase.difficulty >= 2) && (trigo.RND() % 6 == 0);
        chaosObjects.Fill(0, 0, 39, 0, BarLight);
        chaosObjects.Fill(0, 0, 0, 19, BarLight);
        for (y = 0; y <= 19; y++) {
            for (x = 40; x <= 127; x++) {
                z = x - 15 + chaosObjects.Rnd(30);
                if (z < 60)
                    val = BackNone;
                else if (z < 80)
                    val = Back8x8;
                else if (z < 100)
                    val = Back4x4;
                else
                    val = Back2x2;
                chaosObjects.Put(x, y, val);
            }
        }
        chaosGenerator.DrawBoxes(0, 0, 40, 20);
        chaosObjects.Put(39, 18, 0);
        chaosObjects.FillCond(0, 0, 39, 19, chaosObjects.OnlyBackground_ref, Light);
        chaosObjects.FillCond(0, 0, 39, 19, chaosObjects.OnlyWall_ref, BarLight);
        chaosObjects.PutPlayer(38, 1);
        chaosObjects.PutExit(39, 18);
        chaosObjects.Rect(50, 0, 127, 19);
        chaosObjects.PutMoney(EnumSet.of(Moneys.st), 15);
        if (chaosBase.powerCountDown > 2) {
            chaosObjects.PutBlockBonus(ChaosBonus.tbDBSpeed, 45, 16);
            chaosObjects.PutExtraPower(2, 126, 18);
        }
        chaosObjects.Rect(1, 1, 38, 18);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doMirror, 0, 1, 0, 10);
        chaosObjects.Rect(1, 1, 30, 18);
        chaosObjects.PutMagnetR(3, 8);
        chaosObjects.PutMagnetA(2, 8);
        chaosObjects.Rect(1, 1, 38, 9);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doWindMaker, 0, 1, 0, 3);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doBubbleMaker, 0, 1, 0, 3);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doFireMaker, 0, 1, 0, 3);
        chaosObjects.PutChaosObjs(Anims.DEADOBJ, ChaosDObj.doSand, 0, ChaosGraphics.BW, ChaosGraphics.BH, ChaosGraphics.BW * 8, ChaosGraphics.BH * 8, 16);
        chaosObjects.Rect(1, 9, 38, 17);
        chaosObjects.PutDeltaObjs(Anims.MACHINE, ChaosMachine.mCannon1, 0, -1, -1, 3);
        chaosObjects.PutDeltaObjs(Anims.MACHINE, ChaosMachine.mCannon1, 1, 1, 1, 3);
        chaosObjects.PutDeltaObjs(Anims.MACHINE, ChaosMachine.mCannon2, 0, -1, -1, 3);
        chaosObjects.PutDeltaObjs(Anims.MACHINE, ChaosMachine.mCannon2, 1, 1, 1, 3);
        chaosObjects.PutTurret(2);
        chaosObjects.Rect(1, 1, 38, 18);
        chaosObjects.PutCreatorC(12);
        chaosObjects.PutCartoon(0, 2, 20);
        chaosObjects.PutBullet(8);
        chaosObjects.PutHospital(5);
        chaosObjects.Rect(25, 12, 38, 18);
        chaosObjects.PutMagnet(1);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, ChaosDObj.doMagnetA, 3, 50, 10);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, ChaosDObj.doMagnetA, 3, 50, 10);
        chaosObjects.PutExit(50, 10);
        chaos1Zone.AddOptions(60, 1, 120, 18, 4, 0, 0, 8, 10, 1, 1);
    }

    /* Level 17 */
    private void Plain_RndRect(/* VAR+WRT */ Runtime.IRef<Integer> x, /* VAR+WRT */ Runtime.IRef<Integer> y) {
        x.set(chaosObjects.Rnd(51) + 4);
        y.set(chaosObjects.Rnd(49) + 6);
        chaosObjects.Rect(x.get() - 4, y.get() - 4, x.get() + 4, y.get() + 4);
    }

    public void Plain() {
        // VAR
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> a = new Runtime.Ref<>(0);
        int sz = 0;
        int d = 0;
        int dx = 0;
        int dy = 0;
        int c = 0;
        int val = 0;

        chaosObjects.Cadre(60, 60);
        chaosObjects.Clear(60, 70);
        chaosObjects.FillCond(0, 0, 59, 59, chaosObjects.OnlyWall_ref, SimpleBlock);
        chaosObjects.Fill(0, 60, 59, 69, BackNone);
        for (c = 1; c <= 4 + trigo.RND() % 16; c++) {
            sz = trigo.RND() % 3 + 1;
            d = sz * 2 + 1;
            if (chaosGenerator.FindIsolatedRect(1, 1, 58, 58, sz, 0, a, x, y, false)) {
                val = trigo.RND() % 4;
                switch (val) {
                    case 0 -> val = Sq1Block;
                    case 1 -> val = EmptyBlock;
                    default -> val = Sq4Block;
                }
                if (trigo.RND() % 3 == 0)
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, val);
                else
                    chaosObjects.Fill(x.get() - sz, y.get() - sz, x.get() + sz, y.get() + sz, val);
                if ((val == EmptyBlock) && (c > 10)) {
                    chaosObjects.Put(x.get() - sz, y.get(), FalseEmpty);
                    sz--;
                    chaosObjects.Fill(x.get() - sz, y.get() - sz, x.get() + sz, y.get() + sz, FalseEmpty);
                    if (chaosBase.stages == 0)
                        chaosObjects.PutBlockBonus(ChaosBonus.tbNoMissile, x.get(), y.get());
                } else if (trigo.RND() % 3 == 0) {
                    dx = chaosObjects.Rnd(3) - 1;
                    dy = chaosObjects.Rnd(3) - 1;
                    if ((dx != 0) || (dy != 0)) {
                        d -= 2;
                        sz--;
                        x.inc(dx);
                        y.inc(dy);
                        chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, Ground);
                    }
                }
            }
        }
        for (c = 1; c <= 4 + trigo.RND() % 32; c++) {
            val = trigo.RND() % 4;
            switch (val) {
                case 0 -> val = BigBlock;
                case 1 -> val = Leaf3;
                case 2 -> val = Fade3;
                case 3 -> val = RGBBlock;
                default -> throw new RuntimeException("Unhandled CASE value " + val);
            }
            chaosGenerator.PutCross(1, 1, 58, 58, val);
        }
        chaosObjects.FillCond(1, 1, 58, 58, chaosObjects.OnlyBackground_ref, Ground);
        chaosObjects.PutRandom(1, 1, 58, 58, chaosObjects.OnlyBackground_ref, Ground2, 180);
        chaosObjects.Put(chaosObjects.Rnd(58) + 1, 59, FalseBlock);
        chaosObjects.PutPlayer(1, 1);
        chaosObjects.PutExit(58, 58);
        Plain_RndRect(x, y);
        chaosObjects.PutABox(0, 4);
        Plain_RndRect(x, y);
        chaosObjects.PutABox(1, 4);
        Plain_RndRect(x, y);
        chaosObjects.PutNest(0, 10);
        Plain_RndRect(x, y);
        chaosObjects.PutTri(chaos1Zone.pLife4, 8);
        Plain_RndRect(x, y);
        chaosObjects.PutAlien1(ChaosAlien.aStar, chaos1Zone.pLife4, 15);
        Plain_RndRect(x, y);
        chaosObjects.PutAlien1(ChaosAlien.aBubble, chaos1Zone.pLife4, 15);
        Plain_RndRect(x, y);
        chaosObjects.PutTrefle(chaos1Zone.pLife3, 10);
        Plain_RndRect(x, y);
        chaosObjects.PutAlien1(ChaosAlien.aDiese, chaos1Zone.pLife4, 15);
        Plain_RndRect(x, y);
        chaosObjects.PutRAlien1(ChaosAlien.aKamikaze, 0, 3, 10);
        Plain_RndRect(x, y);
        chaosObjects.PutAlien1(ChaosAlien.aCartoon, 0, 20);
        Plain_RndRect(x, y);
        chaosObjects.PutCartoon(1, 2, 20);
        chaosObjects.Rect(1, 1, 58, 58);
        chaosObjects.PutHospital(6);
        chaosObjects.PutBullet(6);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doMirror, 0, 1, 0, 5);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doFireWall, 0, 0, 0, 3);
        if (chaosBase.powerCountDown > 1) {
            chaosObjects.PutExtraPower(1, 0, 69);
            chaosObjects.PutBlockBonus(ChaosBonus.tbBullet, 0, 68);
            chaosObjects.PutBlockBonus(ChaosBonus.tbBomb, 1, 69);
        }
        chaosObjects.Rect(0, 65, 59, 69);
        chaosObjects.PutAlien1(ChaosAlien.aSmallDrawer, 60, 1);
        chaos1Zone.AddOptions(1, 1, 58, 58, 5, 10, 0, 0, 0, 4, 4);
        chaos1Zone.AddOptions(0, 69, 59, 69, 0, 0, 0, 20, 0, 0, 0);
    }

    /* Level 18 */
    public void UnderWater() {
        // VAR
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> a = new Runtime.Ref<>(0);
        int sz = 0;
        int c = 0;
        int val = 0;

        chaosObjects.Clear(100, 70);
        chaosBase.water = (chaosBase.stages != 0) || (trigo.RND() % 4 != 0);
        chaosObjects.Fill(0, 0, 99, 69, SimpleBlock);
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Tar;
        chaosGenerator.FillEllipse(1, 1, 98, 68, val);
        for (c = 1; c <= 16 + trigo.RND() % 16; c++) {
            sz = 2 + chaosObjects.Rnd(5);
            if (chaosGenerator.FindIsolatedRect(1, 1, 98, 68, sz, 0, a, x, y, false))
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, sz * 2 + 1, sz * 2 + 1, SimpleBlock);
        }
        for (c = 1; c <= 12; c++) {
            sz = chaosObjects.Rnd(2) + 1;
            if (chaosGenerator.FindIsolatedRect(1, 1, 98, 68, sz, 15, a, x, y, true)) {
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, sz * 2 + 1, sz * 2 + 1, Ground);
                chaosObjects.PutBubbleMaker(0, x.get(), y.get());
                chaosObjects.PutBlockObj(Anims.ALIEN2, ChaosCreator.cNest, 1, x.get() - 1, y.get());
                chaosObjects.PutBlockObj(Anims.ALIEN1, ChaosAlien.aHospital, chaos1Zone.pLife3, x.get() + 1, y.get());
                if (c >= 10 + trigo.RND() % 3)
                    val = FalseBlock;
                else
                    val = Ground2;
                if ((c == 12) && (chaosBase.powerCountDown > 2)) {
                    chaosObjects.PutExtraPower(2, x.get(), y.get() - 1);
                    chaosObjects.PutBlockObj(Anims.BONUS, ChaosBonus.BonusLevel, ChaosBonus.tbBonusLevel, x.get(), y.get() + 1);
                }
                chaosGenerator.MakeLink(x.get(), y.get(), sz, a.get(), val);
            }
        }
        chaosObjects.PutRandom(0, 0, 99, 69, chaosObjects.OnlyWall_ref, Leaf1, trigo.RND() % 128);
        chaosObjects.PutRandom(0, 0, 99, 69, chaosObjects.OnlyWall_ref, Leaf2, trigo.RND() % 128);
        chaosObjects.PutRandom(0, 0, 99, 69, chaosObjects.OnlyWall_ref, Leaf3, trigo.RND() % 256);
        chaosObjects.PutRandom(0, 0, 99, 69, chaosObjects.OnlyWall_ref, Leaf4, trigo.RND() % 256);
        chaosObjects.PutPlayer(1, 34);
        chaosObjects.PutExit(98, 34);
        chaosObjects.PutBubbleMaker(1, 49, 1);
        chaosObjects.PutBubbleMaker(1, 50, 68);
        chaosObjects.Rect(50, 1, 99, 69);
        chaosObjects.PutCreatorR(20);
        chaosObjects.PutCreatorC(20);
        chaosObjects.Rect(1, 1, 49, 69);
        chaosObjects.PutFour(chaos1Zone.pLife4, 20);
        chaosObjects.PutTri(chaos1Zone.pLife4, 7);
        chaosObjects.Rect(1, 1, 20, 69);
        chaosObjects.PutTBonus(ChaosBonus.tbDBSpeed, 2);
        chaosObjects.Rect(1, 1, 99, 69);
        chaosObjects.PutAlien1(ChaosAlien.aBubble, chaos1Zone.pLife3, 20);
        chaosObjects.PutMagnet(4);
        if (chaosBase.nbDollar == 0)
            chaosObjects.PutMoney(EnumSet.of(Moneys.m3), 10 + chaosBase.difficulty);
        if (chaosBase.stages == 0) {
            chaosObjects.PutAlien2(ChaosCreator.cCircle, 120, 5);
            chaosObjects.PutTurret(5);
            chaosObjects.Rect(1, 1, 49, 69);
            chaosObjects.PutTBonus(ChaosBonus.tbNoMissile, 2);
        }
        chaosObjects.PutChaosObjs(Anims.DEADOBJ, ChaosDObj.doSand, 0, 2880, 1280, 3168, 1920, 15);
        chaos1Zone.AddOptions(1, 1, 98, 68, 1, 1, 0, 6, 20, 3, 3);
    }

    /* Level 19 */
    public void Assembly() {
        // VAR
        int x = 0;
        int val = 0;

        chaosObjects.Cadre(15, 70);
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Round4;
        chaosObjects.FillCond(0, 0, 14, 69, chaosObjects.OnlyWall_ref, SimpleBlock);
        chaosGenerator.GCastle(1, 7, 13, 25, Bricks, val);
        chaosGenerator.DrawPacman(6, 2, 2, 2, 27, 12, 39);
        chaosObjects.Fill(1, 41, 12, 41, SimpleBlock);
        chaosObjects.FillRandom(1, 27, 13, 40, Granit1, Granit2, chaosObjects.OnlyWall_ref, chaosObjects.Rnd_ref);
        chaosObjects.FillCond(1, 1, 13, 69, chaosObjects.OnlyBackground_ref, val);
        x = chaosObjects.Rnd(10) + 3;
        chaosObjects.Fill(0, 47, x - 1, 55, SimpleBlock);
        chaosObjects.Fill(x + 1, 47, 14, 55, SimpleBlock);
        chaosObjects.PutPlayer(8, 3);
        chaosObjects.PutBlockObj(Anims.ALIEN1, ChaosAlien.aKamikaze, 0, 6, 1);
        chaosObjects.PutBlockObj(Anims.ALIEN1, ChaosAlien.aKamikaze, 1, 10, 1);
        chaosObjects.PutBlockObj(Anims.ALIEN1, ChaosAlien.aKamikaze, 2, 6, 5);
        chaosObjects.PutBlockObj(Anims.ALIEN1, ChaosAlien.aKamikaze, 3, 10, 5);
        chaosObjects.PutExit(13, 68);
        chaosObjects.PutBlockBonus(ChaosBonus.tbDBSpeed, 13, 40);
        chaosObjects.PutBlockBonus(ChaosBonus.tbSGSpeed, x, 56);
        chaosObjects.Fill(1, 48, 13, 48, FalseBlock);
        if ((chaosBase.difficulty >= 3) && (chaosBase.difficulty <= 7))
            chaosObjects.PutBlockBonus(ChaosBonus.tbDifficulty, x, 47);
        chaosObjects.Put(x, 48, Round4);
        val = trigo.RND() % 3 + ChaosBonus.tbMagnet;
        chaosObjects.PutBlockBonus(val, 1, 48);
        chaosObjects.PutBlockBonus(val, 13, 48);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, x, 52);
        chaosObjects.PutFineObj(Anims.ALIEN1, ChaosAlien.aKamikaze, 0, 6, 44, 0, 0);
        chaosObjects.PutFineObj(Anims.ALIEN1, ChaosAlien.aKamikaze, 1, 6, 44, 1, 0);
        chaosObjects.PutFineObj(Anims.ALIEN1, ChaosAlien.aKamikaze, 2, 6, 44, 0, 1);
        chaosObjects.PutFineObj(Anims.ALIEN1, ChaosAlien.aKamikaze, 3, 6, 44, 1, 1);
        chaosObjects.Rect(1, 41, 13, 46);
        chaosObjects.PutBullet(17);
        chaosObjects.PutChaosObjs(Anims.ALIEN2, ChaosCreator.cNest, 0, ChaosGraphics.BW * 2, ChaosGraphics.BH * 57, ChaosGraphics.BW * 13, ChaosGraphics.BH * 68, 15);
        chaosObjects.PutChaosObjs(Anims.ALIEN2, ChaosCreator.cCreatorR, 80, ChaosGraphics.BW * 2, ChaosGraphics.BH * 57, ChaosGraphics.BW * 13, ChaosGraphics.BH * 68, 15);
        chaosObjects.PutChaosObjs(Anims.ALIEN2, ChaosCreator.cCreatorC, 80, ChaosGraphics.BW * 2, ChaosGraphics.BH * 57, ChaosGraphics.BW * 13, ChaosGraphics.BH * 68, 15);
        chaosObjects.PutChaosObjs(Anims.ALIEN2, ChaosCreator.cFour, chaos1Zone.pLife4, ChaosGraphics.BW * 2, ChaosGraphics.BH * 57, ChaosGraphics.BW * 13, ChaosGraphics.BH * 68, 12);
        chaosObjects.PutChaosObjs(Anims.ALIEN2, ChaosCreator.cQuad, chaos1Zone.pLife4, ChaosGraphics.BW * 2, ChaosGraphics.BH * 57, ChaosGraphics.BW * 13, ChaosGraphics.BH * 68, 10);
        chaosObjects.PutChaosObjs(Anims.MACHINE, ChaosMachine.mTurret, 0, ChaosGraphics.BW * 2, ChaosGraphics.BH * 57, ChaosGraphics.BW * 13, ChaosGraphics.BH * 68, 12);
        chaosObjects.PutChaosObjs(Anims.MACHINE, ChaosMachine.mCannon3, 0, ChaosGraphics.BW * 2, ChaosGraphics.BH * 57, ChaosGraphics.BW * 13, ChaosGraphics.BH * 68, 8);
        chaos1Zone.AddOptions(1, 10, 13, 68, 4, 4, 4, 4, 4, 4, 4);
    }

    private void Jungle_RndRect(/* VAR+WRT */ Runtime.IRef<Integer> x, /* VAR+WRT */ Runtime.IRef<Integer> y) {
        x.set(chaosObjects.Rnd(40) + 10);
        y.set(chaosObjects.Rnd(40) + 10);
        chaosObjects.Rect(x.get() - 9, y.get() - 9, x.get() + 9, y.get() + 9);
    }

    /* Level 20 */
    public void Jungle() {
        // CONST
        final Runtime.RangeSet Alien1L = new Runtime.RangeSet(Memory.SET16_r).with(ChaosAlien.aDbOval, ChaosAlien.aHospital, ChaosAlien.aDiese, ChaosAlien.aStar, ChaosAlien.aBubble, ChaosAlien.aTri, ChaosAlien.aTrefle);
        final Runtime.RangeSet Alien1S = new Runtime.RangeSet(Memory.SET16_r).with(ChaosAlien.aCartoon, ChaosAlien.aKamikaze, ChaosAlien.aPic);
        final Runtime.RangeSet Alien2L = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cAlienV, ChaosCreator.cAlienA, ChaosCreator.cCreatorR, ChaosCreator.cCreatorC, ChaosCreator.cFour, ChaosCreator.cQuad);
        final Runtime.RangeSet Alien2B = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cCreatorR, ChaosCreator.cCreatorC);
        final Runtime.RangeSet Alien2S = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cAlienBox, ChaosCreator.cNest);
        final Runtime.RangeSet DObjs = new Runtime.RangeSet(Memory.SET16_r).with(ChaosDObj.doMagnetA, ChaosDObj.doMagnetR, ChaosDObj.doSand, ChaosDObj.doMirror, ChaosDObj.doWindMaker, ChaosDObj.doBubbleMaker, ChaosDObj.doFireMaker, ChaosDObj.doFireWall);
        final Runtime.RangeSet Machines = new Runtime.RangeSet(Memory.SET16_r).with(ChaosMachine.mTraverse, ChaosMachine.mCannon1, ChaosMachine.mCannon2, ChaosMachine.mCannon3, ChaosMachine.mTurret);
        final Runtime.RangeSet TBonus = new Runtime.RangeSet(Memory.SET16_r).with(ChaosBonus.tbDBSpeed, ChaosBonus.tbSGSpeed, ChaosBonus.tbMagnet, ChaosBonus.tbInvinsibility, ChaosBonus.tbSleeper, ChaosBonus.tbFreeFire, ChaosBonus.tbMaxPower);

        // VAR
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int sz = 0;
        Runtime.Ref<Integer> angle = new Runtime.Ref<>(0);
        int c = 0;
        int lf = 0;

        chaosObjects.Clear(60, 60);
        chaosObjects.PutPlayer(0, 0);
        chaosObjects.PutBlockBonus(ChaosBonus.tbBomb, 59, 59);
        chaosBase.water = (chaosBase.difficulty >= 7) && (trigo.RND() % 7 == 0);
        chaosObjects.Fill(0, 0, 59, 59, SimpleBlock);
        chaosGenerator.Join(1, 1, 58, 58, 1, 1, Ground2);
        chaosObjects.PutExit(58, 58);
        for (c = 1; c <= 30; c++) {
            sz = trigo.RND() % 4 + 2;
            if (chaosGenerator.FindIsolatedRect(0, 0, 59, 59, sz, 15, angle, x, y, true)) {
                chaosObjects.Fill(x.get() - sz, y.get() - sz, x.get() + sz, y.get() + sz, Ground);
                chaosGenerator.MakeLink(x.get(), y.get(), sz, angle.get(), Ground2);
                sz--;
                if (chaosGraphics.dualpf)
                    chaosObjects.Fill(x.get() - sz, y.get() - sz, x.get() + sz, y.get() + sz, BackNone);
            }
        }
        chaosObjects.FillRandom(0, 0, 59, 59, Forest1, Forest7, chaosObjects.OnlyWall_ref, chaosObjects.Rnd_ref);
        for (c = 0; c <= 15; c++) {
            if (Alien1L.contains(c)) {
                Jungle_RndRect(x, y);
                chaosObjects.PutAlien1(c, chaos1Zone.pLife4, 4);
                chaosObjects.Rect(1, 1, 58, 58);
                chaosObjects.PutAlien1(c, chaos1Zone.pLife4, 1);
            } else if (Alien1S.contains(c)) {
                Jungle_RndRect(x, y);
                chaosObjects.PutRAlien1(c, 0, 3, 4);
                chaosObjects.Rect(1, 1, 58, 58);
                chaosObjects.PutRAlien1(c, 0, 3, 1);
            }
            if (Alien2L.contains(c)) {
                if (Alien2B.contains(c))
                    lf = 70 + chaosBase.difficulty * 3;
                else
                    lf = chaos1Zone.pLife4;
                Jungle_RndRect(x, y);
                chaosObjects.PutAlien2(c, lf, 5);
                chaosObjects.Rect(1, 1, 58, 58);
                chaosObjects.PutAlien2(c, lf, 1);
            } else if (Alien2S.contains(c)) {
                Jungle_RndRect(x, y);
                chaosObjects.PutRAlien2(c, 0, 3, 4);
                chaosObjects.Rect(1, 1, 58, 58);
                chaosObjects.PutRAlien2(c, 0, 3, 1);
            }
            if (DObjs.contains(c)) {
                chaosObjects.Rect(1, 1, 58, 58);
                chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, c, 0, 3, 0, 4);
            }
            if (Machines.contains(c)) {
                Jungle_RndRect(x, y);
                chaosObjects.PutMachine(c, 0, 1, 4);
            }
            if (TBonus.contains(c)) {
                chaosObjects.Rect(1, 1, 58, 58);
                chaosObjects.PutTBonus(c, 1);
            }
        }
        chaosObjects.Rect(1, 1, 58, 58);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.PutTBonus(ChaosBonus.tbBomb, 1);
        chaosObjects.PutTBonus(ChaosBonus.tbBonusLevel, 1);
        chaosObjects.PutHospital(8);
        chaosObjects.PutBullet(8);
        chaosObjects.PutAlien1(ChaosAlien.aBigDrawer, 0, 1);
        chaosObjects.PutAlien1(ChaosAlien.aSmallDrawer, 0, 1);
        chaos1Zone.AddOptions(1, 1, 58, 58, 3, 3, 3, 3, 3, 3, 3);
    }


    // Support

    private static Chaos2Zone instance;

    public static Chaos2Zone instance() {
        if (instance == null)
            new Chaos2Zone(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
    }

    public void close() {
    }

}
