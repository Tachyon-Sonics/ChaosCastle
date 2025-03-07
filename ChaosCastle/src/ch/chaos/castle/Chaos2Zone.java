package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus.Moneys;
import ch.chaos.library.Memory;
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

    public void Rooms() {
        // VAR
        int c = 0;
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short sz = 0;
        short d = 0;
        Runtime.Ref<Short> angle = new Runtime.Ref<>((short) 0);
        short val = 0;

        chaosObjects.Clear((short) 60, (short) 60);
        chaosObjects.Fill((short) 0, (short) 0, (short) 59, (short) 59, (short) EmptyBlock);
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Back8x8;
        chaosObjects.Fill((short) 10, (short) 9, (short) 49, (short) 11, val);
        chaosObjects.Fill((short) 10, (short) 29, (short) 49, (short) 31, val);
        chaosObjects.Fill((short) 47, (short) 12, (short) 49, (short) 28, val);
        chaosObjects.Fill((short) 10, (short) 32, (short) 12, (short) 51, val);
        chaosObjects.PutPlayer((short) 11, (short) 10);
        chaosObjects.PutExit((short) 11, (short) 50);
        chaosObjects.Rect((short) 11, (short) 9, (short) 48, (short) 11);
        chaosObjects.PutMachine(ChaosMachine.mTraverse, 1, 1, 1);
        chaosObjects.Rect((short) 11, (short) 29, (short) 48, (short) 31);
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
            sz = (short) (trigo.RND() % 4 + 1);
            d = (short) (sz * 2 + 1);
            if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 59, (short) 59, sz, (short) 4, angle, x, y, true)) {
                if (trigo.RND() % 2 == 0)
                    chaosObjects.Fill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz), (short) Back4x4);
                else
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) Back4x4);
                chaos1Zone.RectFill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz));
                if (c >= 34)
                    val = FalseEmpty;
                else
                    val = Back2x2;
                chaosGenerator.MakeLink(x.get(), y.get(), sz, angle.get(), val);
            }
        }
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 29, (short) 29, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Sq1Block, (short) 60);
        chaosObjects.PutRandom((short) 30, (short) 0, (short) 59, (short) 29, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Sq4Block, (short) 60);
        chaosObjects.PutRandom((short) 30, (short) 30, (short) 59, (short) 59, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Sq4TravBlock, (short) 60);
        chaosObjects.PutRandom((short) 0, (short) 30, (short) 29, (short) 59, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) BigBlock, (short) 60);
        chaosObjects.PutGridObjs(Anims.BONUS, (short) ChaosBonus.TimedBonus, ChaosBonus.tbHospital, (short) 10, (short) 10, (short) 19, (short) 20, (short) 2, (short) 2);
        chaosObjects.Rect((short) 1, (short) 40, (short) 58, (short) 58);
        chaosObjects.PutRAlien2(ChaosCreator.cAlienBox, 0, 1, 5);
        chaosObjects.Rect((short) 1, (short) 1, (short) 58, (short) 58);
        chaosObjects.PutBullet(8);
        chaosObjects.Rect((short) 1, (short) 1, (short) 29, (short) 20);
        chaosObjects.PutInvinsibility(1);
        chaosObjects.Rect((short) 30, (short) 1, (short) 58, (short) 20);
        chaosObjects.PutFreeFire(1);
        chaosObjects.Rect((short) 30, (short) 21, (short) 58, (short) 40);
        chaosObjects.PutSleeper(1);
        chaosObjects.Rect((short) 1, (short) 21, (short) 29, (short) 40);
        chaosObjects.PutMaxPower(1);
        chaosObjects.Rect((short) 1, (short) 35, (short) 58, (short) 58);
        chaosObjects.PutMagnet(1);
        chaosObjects.Rect((short) 1, (short) 1, (short) 58, (short) 58);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        if (chaosBase.powerCountDown > 1)
            chaosObjects.PutRandomObjs(Anims.SMARTBONUS, (short) ChaosSmartBonus.sbExtraPower, 0, 1);
        chaosObjects.PutRandomObjs(Anims.SMARTBONUS, (short) ChaosSmartBonus.sbExtraLife, 0, 2);
        chaos1Zone.AddOptions((short) 1, (short) 1, (short) 58, (short) 58, 0, 0, 1, 0, 10, 0, 0);
    }

    private void Yard_RndRect() {
        // VAR
        short x = 0;
        short y = 0;

        x = chaosObjects.Rnd((short) 35);
        y = chaosObjects.Rnd((short) 35);
        chaosObjects.Rect(x, y, (short) (x + 3), (short) (y + 3));
    }

    public void Yard() {
        chaosObjects.Cadre((short) 40, (short) 40);
        chaosObjects.FillCond((short) 0, (short) 0, (short) 40, (short) 40, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) TravLight);
        chaosObjects.Rect((short) 1, (short) 1, (short) 38, (short) 38);
        chaosObjects.PutIsolated(5, 20, (short) 10, (short) 30, (short) BarDark);
        chaosObjects.FillRandom((short) 1, (short) 1, (short) 39, (short) 39, (short) Back4x4, (short) Back8x8, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), Runtime.proc(chaosObjects::ExpRandom, "ChaosObjects.ExpRandom"));
        chaosObjects.FillCond((short) 18, (short) 18, (short) 22, (short) 22, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Light);
        if (chaosGraphics.dualpf)
            chaosObjects.PutRandom((short) 1, (short) 1, (short) 39, (short) 39, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) BackNone, (short) 255);
        chaosObjects.PutPlayer((short) 2, (short) 2);
        if ((chaosBase.nbSterling >= 190) && (chaosBase.difficulty >= 2)) {
            chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterEye, 0, (short) 19, (short) 20);
            chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterEye, 1, (short) 21, (short) 20);
            chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterMouth, 0, (short) 20, (short) 20);
            chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterAlien1, 0, (short) 20, (short) 20);
        }
        chaosObjects.Rect((short) 20, (short) 20, (short) 38, (short) 38);
        chaosObjects.PutTBonus(ChaosBonus.tbExit, 1);
        chaosObjects.Rect((short) 3, (short) 3, (short) 38, (short) 38);
        chaosObjects.PutMagnetR(3, 15);
        chaosObjects.PutMagnetA(3, 15);
        chaosObjects.FillObj(Anims.ALIEN1, (short) ChaosAlien.aCartoon, 0, (short) 1, (short) 19, (short) 38, (short) 19, false);
        chaosObjects.Rect((short) 10, (short) 3, (short) 29, (short) 38);
        chaosObjects.PutMachine(ChaosMachine.mCannon1, 0, 1, 5);
        chaosObjects.Rect((short) 3, (short) 10, (short) 38, (short) 29);
        chaosObjects.PutMachine(ChaosMachine.mCannon2, 0, 1, 5);
        chaosObjects.Rect((short) 10, (short) 10, (short) 29, (short) 29);
        chaosObjects.PutCannon3(5);
        chaosObjects.PutTurret(7);
        chaosObjects.Rect((short) 3, (short) 3, (short) 38, (short) 38);
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
        chaosObjects.Rect((short) 20, (short) 20, (short) 38, (short) 38);
        chaosObjects.PutNest(1, 15);
        chaosObjects.Rect((short) 1, (short) 1, (short) 7, (short) 7);
        chaosObjects.PutFreeFire(1);
        chaosObjects.PutMaxPower(1);
        chaos1Zone.AddOptions((short) 10, (short) 10, (short) 38, (short) 38, 5, 5, 1, 3, 0, 5, 3);
    }

    public void Antarctica() {
        // CONST
        final int W = 120;
        final int H = 60;
        final int MX = W / 2;
        final int MY = H / 2;

        // VAR
        Runtime.Ref<Short> lx = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> ly = new Runtime.Ref<>((short) 0);
        short sz = 0;
        Runtime.Ref<Short> at = new Runtime.Ref<>((short) 0);
        int c = 0;
        short val = 0;

        chaosObjects.Clear((short) W, (short) H);
        chaosBase.water = (chaosBase.difficulty < 5) || (trigo.RND() % 3 != 0);
        chaosBase.snow = (chaosBase.stages > 0) || (trigo.RND() % 3 != 0);
        chaosObjects.Fill((short) 0, (short) 0, (short) (W - 1), (short) (H - 1), (short) IceBlock);
        chaosGenerator.VRace((short) Ice);
        chaosGenerator.MakeLink((short) (W / 2), (short) (H * 2 / 5 - 1), (short) 0, (short) 90, (short) Ice);
        chaosObjects.PutPlayer((short) (W / 2), (short) (H * 2 / 5));
        chaosObjects.Rect((short) (MX / 2), (short) 0, (short) (MX - 1), (short) (MY - 1));
        chaosObjects.PutTBonus(ChaosBonus.tbExit, 1);
        chaosObjects.Rect((short) 0, (short) 0, (short) (W / 3), (short) MY);
        chaosObjects.PutDeadObj(ChaosDObj.doBubbleMaker, 0, 1);
        chaosObjects.Rect((short) (W * 2 / 3), (short) 0, (short) (W - 1), (short) (MY - 1));
        chaosObjects.PutDeadObj(ChaosDObj.doBubbleMaker, 0, 1);
        chaosObjects.Rect((short) 0, (short) MY, (short) (W / 3), (short) (H - 1));
        chaosObjects.PutDeadObj(ChaosDObj.doBubbleMaker, 0, 1);
        chaosObjects.Rect((short) (W * 2 / 3), (short) MY, (short) (W - 1), (short) (H - 1));
        chaosObjects.PutDeadObj(ChaosDObj.doBubbleMaker, 0, 1);
        chaosObjects.Rect((short) MX, (short) MY, (short) (W * 2 / 3), (short) (H - 1));
        chaosObjects.PutTBonus(ChaosBonus.tbDBSpeed, 1);
        chaosObjects.PutChaosObjs(Anims.DEADOBJ, (short) ChaosDObj.doSand, 0, (short) 0, (short) 0, (short) (W * 10), (short) (H * 10), 20);
        chaosObjects.Rect((short) (W * 2 / 3), (short) 0, (short) (W - 1), (short) (H - 1));
        chaosObjects.PutDeadObj(ChaosDObj.doWindMaker, 0, 4);
        chaosObjects.Rect((short) 0, (short) MY, (short) MX, (short) (H - 1));
        chaosObjects.PutTurret(4);
        chaosObjects.Rect((short) 1, (short) 1, (short) (W - 2), (short) (H - 2));
        chaosObjects.PutMagnetR(2, 8);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 0, 1, 1, 15);
        chaosObjects.PutCannon3(2);
        chaosObjects.PutMachine(ChaosMachine.mTraverse, 0, 1, 4);
        chaosObjects.PutNest(0, 4 + chaosBase.difficulty);
        chaosObjects.PutCartoon(0, 2, 15);
        chaosObjects.Rect((short) 0, (short) MY, (short) (W - 1), (short) (H - 1));
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
            sz = (short) (trigo.RND() % 4 + 2);
            if (chaosGenerator.FindIsolatedRect((short) 2, (short) 2, (short) (W - 3), (short) (H - 3), sz, (short) 30, at, lx, ly, true)) {
                chaosObjects.Fill((short) (lx.get() - sz + 1), (short) (ly.get() - sz), (short) (lx.get() + sz - 1), (short) (ly.get() + sz), (short) Balls);
                chaosObjects.Fill((short) (lx.get() - sz), (short) (ly.get() - sz + 1), (short) (lx.get() + sz), (short) (ly.get() + sz - 1), (short) Balls);
                chaosObjects.Fill((short) (lx.get() - sz + 1), (short) (ly.get() - sz + 1), (short) (lx.get() + sz - 1), (short) (ly.get() + sz - 1), (short) Ice);
                chaosObjects.PutBubbleMaker(0, lx.get(), ly.get());
                chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, lx.get(), (short) (ly.get() - 1));
                chaosObjects.PutBlockBonus(ChaosBonus.tbBullet, lx.get(), (short) (ly.get() + 1));
                if (c == 16) {
                    if (chaosBase.stages == 0)
                        chaosObjects.PutBlockBonus(ChaosBonus.tbNoMissile, (short) (lx.get() + 1), ly.get());
                    chaosObjects.PutExtraPower((short) 1, (short) (lx.get() - 1), ly.get());
                }
                chaos1Zone.RectFill((short) (lx.get() - sz), (short) (ly.get() - sz), (short) (lx.get() + sz), (short) (ly.get() + sz));
                if (c > 10)
                    val = FalseBlock;
                else
                    val = Ice;
                chaosGenerator.MakeLink(lx.get(), ly.get(), sz, at.get(), val);
            }
        }
        chaosObjects.Rect((short) 1, (short) 1, (short) (W - 2), (short) (H - 2));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) (W - 1), (short) (H - 1), Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) SimpleBlock, (short) (trigo.RND() % 256));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) (W - 1), (short) (H - 1), Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) BigBlock, (short) (trigo.RND() % 256));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) (W - 1), (short) (H - 1), Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Leaf2, (short) (trigo.RND() % 64));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) (W - 1), (short) (H - 1), Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Leaf3, (short) (trigo.RND() % 32));
        chaosObjects.PutRandomObjs(Anims.BONUS, (short) ChaosBonus.BonusLevel, ChaosBonus.tbBonusLevel, 1);
        if ((chaosBase.stages == 0) && (chaosBase.difficulty >= 9) && (trigo.RND() % 4 == 0))
            chaosObjects.PutRandomObjs(Anims.ALIEN3, (short) ChaosBoss.bSisterAlien, 0, 1);
        chaosObjects.PutRandomObjs(Anims.SMARTBONUS, (short) ChaosSmartBonus.sbExtraLife, 0, 1);
        chaos1Zone.AddOptions((short) 1, (short) 1, (short) (W - 2), (short) (H - 2), 4, 4, 0, 2, 10, 0, 1);
    }

    public void Forest() {
        // VAR
        int c = 0;
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short sz = 0;
        short d = 0;
        Runtime.Ref<Short> angle = new Runtime.Ref<>((short) 0);

        chaosObjects.Clear((short) 60, (short) 60);
        chaos1Zone.rotate = false;
        chaosObjects.Fill((short) 0, (short) 0, (short) 59, (short) 59, (short) ChaosGraphics.NbBackground);
        chaosGenerator.FillEllipse(26, 26, 8, 8, (short) Ground);
        chaosObjects.PutPlayer((short) 29, (short) 29);
        chaosObjects.PutExit((short) 30, (short) 30);
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
            sz = (short) (trigo.RND() % 4 + 1);
            d = (short) (sz * 2 + 1);
            if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 59, (short) 59, sz, (short) 10, angle, x, y, true)) {
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) Ground);
                chaos1Zone.RectFill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz));
                chaosGenerator.MakeLink(x.get(), y.get(), sz, angle.get(), (short) Ground2);
            }
        }
        chaosObjects.FillRandom((short) 0, (short) 0, (short) 59, (short) 59, (short) Forest1, (short) Forest7, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), Runtime.proc(chaosObjects::Rnd, "ChaosObjects.Rnd"));
        chaosObjects.Rect((short) 26, (short) 26, (short) 33, (short) 33);
        for (c = 2; c <= 4; c++) {
            chaosObjects.PutRandomObjs(chaos1Zone.fKind[c - 1], chaos1Zone.fSubKind[c - 1], chaos1Zone.aStat[c - 1], 12);
        }
        chaosObjects.Rect((short) 1, (short) 1, (short) 58, (short) 58);
        chaosObjects.PutBullet(10);
        chaosObjects.PutAlien1(ChaosAlien.aHospital, chaos1Zone.pLife4, 10);
        chaosObjects.PutSleeper(1);
        chaosObjects.PutInvinsibility(1);
        chaosObjects.PutFreeFire(1);
        if (chaosBase.powerCountDown > 2)
            chaosObjects.PutRandomObjs(Anims.SMARTBONUS, (short) ChaosSmartBonus.sbExtraPower, 0, 1);
        if (chaosBase.stages == 0)
            chaosObjects.PutTBonus(ChaosBonus.tbNoMissile, 1);
        chaos1Zone.AddOptions((short) 1, (short) 1, (short) 58, (short) 58, 0, 10, 2, 10, 10, 4, 0);
    }

    public void ZCastle() {
        // VAR
        short val = 0;

        chaosObjects.Cadre((short) 40, (short) 40);
        chaosBase.snow = (chaosBase.difficulty >= 5) && (trigo.RND() % 3 == 0);
        chaosGenerator.DrawCastle((short) 2, (short) 1, (short) 36, (short) 37);
        chaosObjects.FillCond((short) 0, (short) 0, (short) 39, (short) 39, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) SimpleBlock);
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Back4x4;
        chaosObjects.FillCond((short) 0, (short) 0, (short) 39, (short) 39, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), val);
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 38, (short) 38, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Back8x8, (short) 40);
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 38, (short) 38, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Back2x2, (short) 40);
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 38, (short) 38, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Sq4Block, (short) 10);
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 38, (short) 38, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) BigBlock, (short) 10);
        chaosObjects.Put((short) 1, (short) 18, (short) FalseBlock);
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 38, (short) 38, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) FalseBlock, (short) chaosBase.difficulty);
        chaosObjects.PutPlayer((short) 1, (short) 1);
        chaosObjects.PutExit((short) 38, (short) 38);
        chaosObjects.Rect((short) 1, (short) 1, (short) 38, (short) 19);
        chaosObjects.PutQuad(chaos1Zone.pLife4, 20);
        chaosObjects.Rect((short) 1, (short) 20, (short) 38, (short) 38);
        chaosObjects.PutFour(chaos1Zone.pLife4, 30);
        chaosObjects.Rect((short) 1, (short) 1, (short) 19, (short) 38);
        chaosObjects.PutAlien1(ChaosAlien.aHospital, chaos1Zone.pLife3, 8);
        chaosObjects.PutBullet(8);
        chaosObjects.Rect((short) 1, (short) 1, (short) 38, (short) 38);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.PutCartoon(0, 0, 50);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doWindMaker, 0, 1, 0, 3);
        chaosObjects.Rect((short) 20, (short) 1, (short) 38, (short) 25);
        chaosObjects.PutInvinsibility(1);
        chaosObjects.PutSleeper(1);
        chaosObjects.PutTBonus(ChaosBonus.tbDBSpeed, 1);
        chaos1Zone.AddOptions((short) 1, (short) 20, (short) 38, (short) 38, 1, 20, 1, 10, 10, 0, 0);
    }

    public void Lights() {
        // VAR
        short x = 0;
        short y = 0;
        short z = 0;
        short val = 0;

        chaosObjects.Clear((short) 127, (short) 20);
        chaosBase.water = (chaosBase.difficulty >= 2) && (trigo.RND() % 6 == 0);
        chaosObjects.Fill((short) 0, (short) 0, (short) 39, (short) 0, (short) BarLight);
        chaosObjects.Fill((short) 0, (short) 0, (short) 0, (short) 19, (short) BarLight);
        for (y = 0; y <= 19; y++) {
            for (x = 40; x <= 127; x++) {
                z = (short) (x - 15 + chaosObjects.Rnd((short) 30));
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
        chaosGenerator.DrawBoxes((short) 0, (short) 0, (short) 40, (short) 20);
        chaosObjects.Put((short) 39, (short) 18, (short) 0);
        chaosObjects.FillCond((short) 0, (short) 0, (short) 39, (short) 19, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Light);
        chaosObjects.FillCond((short) 0, (short) 0, (short) 39, (short) 19, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) BarLight);
        chaosObjects.PutPlayer((short) 38, (short) 1);
        chaosObjects.PutExit((short) 39, (short) 18);
        chaosObjects.Rect((short) 50, (short) 0, (short) 127, (short) 19);
        chaosObjects.PutMoney(EnumSet.of(Moneys.st), 15);
        if (chaosBase.powerCountDown > 2) {
            chaosObjects.PutBlockBonus(ChaosBonus.tbDBSpeed, (short) 45, (short) 16);
            chaosObjects.PutExtraPower((short) 2, (short) 126, (short) 18);
        }
        chaosObjects.Rect((short) 1, (short) 1, (short) 38, (short) 18);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 0, 1, 0, 10);
        chaosObjects.Rect((short) 1, (short) 1, (short) 30, (short) 18);
        chaosObjects.PutMagnetR(3, 8);
        chaosObjects.PutMagnetA(2, 8);
        chaosObjects.Rect((short) 1, (short) 1, (short) 38, (short) 9);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doWindMaker, 0, 1, 0, 3);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doBubbleMaker, 0, 1, 0, 3);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doFireMaker, 0, 1, 0, 3);
        chaosObjects.PutChaosObjs(Anims.DEADOBJ, (short) ChaosDObj.doSand, 0, (short) ChaosGraphics.BW, (short) ChaosGraphics.BH, (short) (ChaosGraphics.BW * 8), (short) (ChaosGraphics.BH * 8), 16);
        chaosObjects.Rect((short) 1, (short) 9, (short) 38, (short) 17);
        chaosObjects.PutDeltaObjs(Anims.MACHINE, (short) ChaosMachine.mCannon1, 0, (short) -1, (short) -1, 3);
        chaosObjects.PutDeltaObjs(Anims.MACHINE, (short) ChaosMachine.mCannon1, 1, (short) 1, (short) 1, 3);
        chaosObjects.PutDeltaObjs(Anims.MACHINE, (short) ChaosMachine.mCannon2, 0, (short) -1, (short) -1, 3);
        chaosObjects.PutDeltaObjs(Anims.MACHINE, (short) ChaosMachine.mCannon2, 1, (short) 1, (short) 1, 3);
        chaosObjects.PutTurret(2);
        chaosObjects.Rect((short) 1, (short) 1, (short) 38, (short) 18);
        chaosObjects.PutCreatorC(12);
        chaosObjects.PutCartoon(0, 2, 20);
        chaosObjects.PutBullet(8);
        chaosObjects.PutHospital(5);
        chaosObjects.Rect((short) 25, (short) 12, (short) 38, (short) 18);
        chaosObjects.PutMagnet(1);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doMagnetA, 3, (short) 50, (short) 10);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doMagnetA, 3, (short) 50, (short) 10);
        chaosObjects.PutExit((short) 50, (short) 10);
        chaos1Zone.AddOptions((short) 60, (short) 1, (short) 120, (short) 18, 4, 0, 0, 8, 10, 1, 1);
    }

    private void Plain_RndRect(/* VAR+WRT */ Runtime.IRef<Short> x, /* VAR+WRT */ Runtime.IRef<Short> y) {
        x.set((short) (chaosObjects.Rnd((short) 51) + 4));
        y.set((short) (chaosObjects.Rnd((short) 49) + 6));
        chaosObjects.Rect((short) (x.get() - 4), (short) (y.get() - 4), (short) (x.get() + 4), (short) (y.get() + 4));
    }

    public void Plain() {
        // VAR
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> a = new Runtime.Ref<>((short) 0);
        short sz = 0;
        short d = 0;
        short dx = 0;
        short dy = 0;
        int c = 0;
        short val = 0;

        chaosObjects.Cadre((short) 60, (short) 60);
        chaosObjects.Clear((short) 60, (short) 70);
        chaosObjects.FillCond((short) 0, (short) 0, (short) 59, (short) 59, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) SimpleBlock);
        chaosObjects.Fill((short) 0, (short) 60, (short) 59, (short) 69, (short) BackNone);
        for (c = 1; c <= 4 + trigo.RND() % 16; c++) {
            sz = (short) (trigo.RND() % 3 + 1);
            d = (short) (sz * 2 + 1);
            if (chaosGenerator.FindIsolatedRect((short) 1, (short) 1, (short) 58, (short) 58, sz, (short) 0, a, x, y, false)) {
                val = (short) (trigo.RND() % 4);
                switch (val) {
                    case 0 -> val = Sq1Block;
                    case 1 -> val = EmptyBlock;
                    default -> val = Sq4Block;
                }
                if (trigo.RND() % 3 == 0)
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, val);
                else
                    chaosObjects.Fill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz), val);
                if ((val == EmptyBlock) && (c > 10)) {
                    chaosObjects.Put((short) (x.get() - sz), y.get(), (short) FalseEmpty);
                    sz--;
                    chaosObjects.Fill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz), (short) FalseEmpty);
                    if (chaosBase.stages == 0)
                        chaosObjects.PutBlockBonus(ChaosBonus.tbNoMissile, x.get(), y.get());
                } else if (trigo.RND() % 3 == 0) {
                    dx = (short) (chaosObjects.Rnd((short) 3) - 1);
                    dy = (short) (chaosObjects.Rnd((short) 3) - 1);
                    if ((dx != 0) || (dy != 0)) {
                        d -= 2;
                        sz--;
                        x.inc(dx);
                        y.inc(dy);
                        chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) Ground);
                    }
                }
            }
        }
        for (c = 1; c <= 4 + trigo.RND() % 32; c++) {
            val = (short) (trigo.RND() % 4);
            switch (val) {
                case 0 -> val = BigBlock;
                case 1 -> val = Leaf3;
                case 2 -> val = Fade3;
                case 3 -> val = RGBBlock;
                default -> throw new RuntimeException("Unhandled CASE value " + val);
            }
            chaosGenerator.PutCross((short) 1, (short) 1, (short) 58, (short) 58, val);
        }
        chaosObjects.FillCond((short) 1, (short) 1, (short) 58, (short) 58, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Ground);
        chaosObjects.PutRandom((short) 1, (short) 1, (short) 58, (short) 58, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Ground2, (short) 180);
        chaosObjects.Put((short) (chaosObjects.Rnd((short) 58) + 1), (short) 59, (short) FalseBlock);
        chaosObjects.PutPlayer((short) 1, (short) 1);
        chaosObjects.PutExit((short) 58, (short) 58);
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
        chaosObjects.Rect((short) 1, (short) 1, (short) 58, (short) 58);
        chaosObjects.PutHospital(6);
        chaosObjects.PutBullet(6);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 0, 1, 0, 5);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doFireWall, 0, 0, 0, 3);
        if (chaosBase.powerCountDown > 1) {
            chaosObjects.PutExtraPower((short) 1, (short) 0, (short) 69);
            chaosObjects.PutBlockBonus(ChaosBonus.tbBullet, (short) 0, (short) 68);
            chaosObjects.PutBlockBonus(ChaosBonus.tbBomb, (short) 1, (short) 69);
        }
        chaosObjects.Rect((short) 0, (short) 65, (short) 59, (short) 69);
        chaosObjects.PutAlien1(ChaosAlien.aSmallDrawer, 60, 1);
        chaos1Zone.AddOptions((short) 1, (short) 1, (short) 58, (short) 58, 5, 10, 0, 0, 0, 4, 4);
        chaos1Zone.AddOptions((short) 0, (short) 69, (short) 59, (short) 69, 0, 0, 0, 20, 0, 0, 0);
    }

    public void UnderWater() {
        // VAR
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> a = new Runtime.Ref<>((short) 0);
        short sz = 0;
        int c = 0;
        short val = 0;

        chaosObjects.Clear((short) 100, (short) 70);
        chaosBase.water = (chaosBase.stages != 0) || (trigo.RND() % 4 != 0);
        chaosObjects.Fill((short) 0, (short) 0, (short) 99, (short) 69, (short) SimpleBlock);
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Tar;
        chaosGenerator.FillEllipse(1, 1, 98, 68, val);
        for (c = 1; c <= 16 + trigo.RND() % 16; c++) {
            sz = (short) (2 + chaosObjects.Rnd((short) 5));
            if (chaosGenerator.FindIsolatedRect((short) 1, (short) 1, (short) 98, (short) 68, sz, (short) 0, a, x, y, false))
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, sz * 2 + 1, sz * 2 + 1, (short) SimpleBlock);
        }
        for (c = 1; c <= 12; c++) {
            sz = (short) (chaosObjects.Rnd((short) 2) + 1);
            if (chaosGenerator.FindIsolatedRect((short) 1, (short) 1, (short) 98, (short) 68, sz, (short) 15, a, x, y, true)) {
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, sz * 2 + 1, sz * 2 + 1, (short) Ground);
                chaosObjects.PutBubbleMaker(0, x.get(), y.get());
                chaosObjects.PutBlockObj(Anims.ALIEN2, (short) ChaosCreator.cNest, 1, (short) (x.get() - 1), y.get());
                chaosObjects.PutBlockObj(Anims.ALIEN1, (short) ChaosAlien.aHospital, chaos1Zone.pLife3, (short) (x.get() + 1), y.get());
                if (c >= 10 + trigo.RND() % 3)
                    val = FalseBlock;
                else
                    val = Ground2;
                if ((c == 12) && (chaosBase.powerCountDown > 2)) {
                    chaosObjects.PutExtraPower((short) 2, x.get(), (short) (y.get() - 1));
                    chaosObjects.PutBlockObj(Anims.BONUS, (short) ChaosBonus.BonusLevel, ChaosBonus.tbBonusLevel, x.get(), (short) (y.get() + 1));
                }
                chaosGenerator.MakeLink(x.get(), y.get(), sz, a.get(), val);
            }
        }
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 99, (short) 69, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Leaf1, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 99, (short) 69, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Leaf2, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 99, (short) 69, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Leaf3, (short) (trigo.RND() % 256));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 99, (short) 69, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Leaf4, (short) (trigo.RND() % 256));
        chaosObjects.PutPlayer((short) 1, (short) 34);
        chaosObjects.PutExit((short) 98, (short) 34);
        chaosObjects.PutBubbleMaker(1, (short) 49, (short) 1);
        chaosObjects.PutBubbleMaker(1, (short) 50, (short) 68);
        chaosObjects.Rect((short) 50, (short) 1, (short) 99, (short) 69);
        chaosObjects.PutCreatorR(20);
        chaosObjects.PutCreatorC(20);
        chaosObjects.Rect((short) 1, (short) 1, (short) 49, (short) 69);
        chaosObjects.PutFour(chaos1Zone.pLife4, 20);
        chaosObjects.PutTri(chaos1Zone.pLife4, 7);
        chaosObjects.Rect((short) 1, (short) 1, (short) 20, (short) 69);
        chaosObjects.PutTBonus(ChaosBonus.tbDBSpeed, 2);
        chaosObjects.Rect((short) 1, (short) 1, (short) 99, (short) 69);
        chaosObjects.PutAlien1(ChaosAlien.aBubble, chaos1Zone.pLife3, 20);
        chaosObjects.PutMagnet(4);
        if (chaosBase.nbDollar == 0)
            chaosObjects.PutMoney(EnumSet.of(Moneys.m3), 10 + chaosBase.difficulty);
        if (chaosBase.stages == 0) {
            chaosObjects.PutAlien2(ChaosCreator.cCircle, 120, 5);
            chaosObjects.PutTurret(5);
            chaosObjects.Rect((short) 1, (short) 1, (short) 49, (short) 69);
            chaosObjects.PutTBonus(ChaosBonus.tbNoMissile, 2);
        }
        chaosObjects.PutChaosObjs(Anims.DEADOBJ, (short) ChaosDObj.doSand, 0, (short) 2880, (short) 1280, (short) 3168, (short) 1920, 15);
        chaos1Zone.AddOptions((short) 1, (short) 1, (short) 98, (short) 68, 1, 1, 0, 6, 20, 3, 3);
    }

    public void Assembly() {
        // VAR
        short x = 0;
        short val = 0;

        chaosObjects.Cadre((short) 15, (short) 70);
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Round4;
        chaosObjects.FillCond((short) 0, (short) 0, (short) 14, (short) 69, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) SimpleBlock);
        chaosGenerator.GCastle((short) 1, (short) 7, (short) 13, (short) 25, (short) Bricks, val);
        chaosGenerator.DrawPacman(6, (short) 2, (short) 2, (short) 2, (short) 27, (short) 12, (short) 39);
        chaosObjects.Fill((short) 1, (short) 41, (short) 12, (short) 41, (short) SimpleBlock);
        chaosObjects.FillRandom((short) 1, (short) 27, (short) 13, (short) 40, (short) Granit1, (short) Granit2, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), Runtime.proc(chaosObjects::Rnd, "ChaosObjects.Rnd"));
        chaosObjects.FillCond((short) 1, (short) 1, (short) 13, (short) 69, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), val);
        x = (short) (chaosObjects.Rnd((short) 10) + 3);
        chaosObjects.Fill((short) 0, (short) 47, (short) (x - 1), (short) 55, (short) SimpleBlock);
        chaosObjects.Fill((short) (x + 1), (short) 47, (short) 14, (short) 55, (short) SimpleBlock);
        chaosObjects.PutPlayer((short) 8, (short) 3);
        chaosObjects.PutBlockObj(Anims.ALIEN1, (short) ChaosAlien.aKamikaze, 0, (short) 6, (short) 1);
        chaosObjects.PutBlockObj(Anims.ALIEN1, (short) ChaosAlien.aKamikaze, 1, (short) 10, (short) 1);
        chaosObjects.PutBlockObj(Anims.ALIEN1, (short) ChaosAlien.aKamikaze, 2, (short) 6, (short) 5);
        chaosObjects.PutBlockObj(Anims.ALIEN1, (short) ChaosAlien.aKamikaze, 3, (short) 10, (short) 5);
        chaosObjects.PutExit((short) 13, (short) 68);
        chaosObjects.PutBlockBonus(ChaosBonus.tbDBSpeed, (short) 13, (short) 40);
        chaosObjects.PutBlockBonus(ChaosBonus.tbSGSpeed, x, (short) 56);
        chaosObjects.Fill((short) 1, (short) 48, (short) 13, (short) 48, (short) FalseBlock);
        if ((chaosBase.difficulty >= 3) && (chaosBase.difficulty <= 7))
            chaosObjects.PutBlockBonus(ChaosBonus.tbDifficulty, x, (short) 49);
        chaosObjects.Put(x, (short) 48, (short) Round4);
        val = (short) (trigo.RND() % 3 + ChaosBonus.tbMagnet);
        chaosObjects.PutBlockBonus(val, (short) 1, (short) 48);
        chaosObjects.PutBlockBonus(val, (short) 13, (short) 48);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, x, (short) 52);
        chaosObjects.PutFineObj(Anims.ALIEN1, (short) ChaosAlien.aKamikaze, 0, (short) 6, (short) 44, (short) 0, (short) 0);
        chaosObjects.PutFineObj(Anims.ALIEN1, (short) ChaosAlien.aKamikaze, 1, (short) 6, (short) 44, (short) 1, (short) 0);
        chaosObjects.PutFineObj(Anims.ALIEN1, (short) ChaosAlien.aKamikaze, 2, (short) 6, (short) 44, (short) 0, (short) 1);
        chaosObjects.PutFineObj(Anims.ALIEN1, (short) ChaosAlien.aKamikaze, 3, (short) 6, (short) 44, (short) 1, (short) 1);
        chaosObjects.Rect((short) 1, (short) 41, (short) 13, (short) 46);
        chaosObjects.PutBullet(17);
        chaosObjects.PutChaosObjs(Anims.ALIEN2, (short) ChaosCreator.cNest, 0, (short) (ChaosGraphics.BW * 2), (short) (ChaosGraphics.BH * 57), (short) (ChaosGraphics.BW * 13), (short) (ChaosGraphics.BH * 68), 15);
        chaosObjects.PutChaosObjs(Anims.ALIEN2, (short) ChaosCreator.cCreatorR, 80, (short) (ChaosGraphics.BW * 2), (short) (ChaosGraphics.BH * 57), (short) (ChaosGraphics.BW * 13), (short) (ChaosGraphics.BH * 68), 15);
        chaosObjects.PutChaosObjs(Anims.ALIEN2, (short) ChaosCreator.cCreatorC, 80, (short) (ChaosGraphics.BW * 2), (short) (ChaosGraphics.BH * 57), (short) (ChaosGraphics.BW * 13), (short) (ChaosGraphics.BH * 68), 15);
        chaosObjects.PutChaosObjs(Anims.ALIEN2, (short) ChaosCreator.cFour, chaos1Zone.pLife4, (short) (ChaosGraphics.BW * 2), (short) (ChaosGraphics.BH * 57), (short) (ChaosGraphics.BW * 13), (short) (ChaosGraphics.BH * 68), 12);
        chaosObjects.PutChaosObjs(Anims.ALIEN2, (short) ChaosCreator.cQuad, chaos1Zone.pLife4, (short) (ChaosGraphics.BW * 2), (short) (ChaosGraphics.BH * 57), (short) (ChaosGraphics.BW * 13), (short) (ChaosGraphics.BH * 68), 10);
        chaosObjects.PutChaosObjs(Anims.MACHINE, (short) ChaosMachine.mTurret, 0, (short) (ChaosGraphics.BW * 2), (short) (ChaosGraphics.BH * 57), (short) (ChaosGraphics.BW * 13), (short) (ChaosGraphics.BH * 68), 12);
        chaosObjects.PutChaosObjs(Anims.MACHINE, (short) ChaosMachine.mCannon3, 0, (short) (ChaosGraphics.BW * 2), (short) (ChaosGraphics.BH * 57), (short) (ChaosGraphics.BW * 13), (short) (ChaosGraphics.BH * 68), 8);
        chaos1Zone.AddOptions((short) 1, (short) 10, (short) 13, (short) 68, 4, 4, 4, 4, 4, 4, 4);
    }

    private void Jungle_RndRect(/* VAR+WRT */ Runtime.IRef<Short> x, /* VAR+WRT */ Runtime.IRef<Short> y) {
        x.set((short) (chaosObjects.Rnd((short) 40) + 10));
        y.set((short) (chaosObjects.Rnd((short) 40) + 10));
        chaosObjects.Rect((short) (x.get() - 9), (short) (y.get() - 9), (short) (x.get() + 9), (short) (y.get() + 9));
    }

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
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short sz = 0;
        Runtime.Ref<Short> angle = new Runtime.Ref<>((short) 0);
        int c = 0;
        int lf = 0;

        chaosObjects.Clear((short) 60, (short) 60);
        chaosObjects.PutPlayer((short) 0, (short) 0);
        chaosObjects.PutBlockBonus(ChaosBonus.tbBomb, (short) 59, (short) 59);
        chaosBase.water = (chaosBase.difficulty >= 7) && (trigo.RND() % 4 == 0);
        chaosObjects.Fill((short) 0, (short) 0, (short) 59, (short) 59, (short) SimpleBlock);
        chaosGenerator.Join((short) 1, (short) 1, (short) 58, (short) 58, (short) 1, (short) 1, (short) Ground2);
        chaosObjects.PutExit((short) 58, (short) 58);
        for (c = 1; c <= 30; c++) {
            sz = (short) (trigo.RND() % 4 + 2);
            if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 59, (short) 59, sz, (short) 15, angle, x, y, true)) {
                chaosObjects.Fill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz), (short) Ground);
                chaosGenerator.MakeLink(x.get(), y.get(), sz, angle.get(), (short) Ground2);
                sz--;
                if (chaosGraphics.dualpf)
                    chaosObjects.Fill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz), (short) BackNone);
            }
        }
        chaosObjects.FillRandom((short) 0, (short) 0, (short) 59, (short) 59, (short) Forest1, (short) Forest7, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), Runtime.proc(chaosObjects::Rnd, "ChaosObjects.Rnd"));
        for (c = 0; c <= 15; c++) {
            if (Alien1L.contains(c)) {
                Jungle_RndRect(x, y);
                chaosObjects.PutAlien1(c, chaos1Zone.pLife4, 4);
                chaosObjects.Rect((short) 1, (short) 1, (short) 58, (short) 58);
                chaosObjects.PutAlien1(c, chaos1Zone.pLife4, 1);
            } else if (Alien1S.contains(c)) {
                Jungle_RndRect(x, y);
                chaosObjects.PutRAlien1(c, 0, 3, 4);
                chaosObjects.Rect((short) 1, (short) 1, (short) 58, (short) 58);
                chaosObjects.PutRAlien1(c, 0, 3, 1);
            }
            if (Alien2L.contains(c)) {
                if (Alien2B.contains(c))
                    lf = 70 + chaosBase.difficulty * 3;
                else
                    lf = chaos1Zone.pLife4;
                Jungle_RndRect(x, y);
                chaosObjects.PutAlien2(c, lf, 5);
                chaosObjects.Rect((short) 1, (short) 1, (short) 58, (short) 58);
                chaosObjects.PutAlien2(c, lf, 1);
            } else if (Alien2S.contains(c)) {
                Jungle_RndRect(x, y);
                chaosObjects.PutRAlien2(c, 0, 3, 4);
                chaosObjects.Rect((short) 1, (short) 1, (short) 58, (short) 58);
                chaosObjects.PutRAlien2(c, 0, 3, 1);
            }
            if (DObjs.contains(c)) {
                chaosObjects.Rect((short) 1, (short) 1, (short) 58, (short) 58);
                chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) c, 0, 3, 0, 4);
            }
            if (Machines.contains(c)) {
                Jungle_RndRect(x, y);
                chaosObjects.PutMachine(c, 0, 1, 4);
            }
            if (TBonus.contains(c)) {
                chaosObjects.Rect((short) 1, (short) 1, (short) 58, (short) 58);
                chaosObjects.PutTBonus(c, 1);
            }
        }
        chaosObjects.Rect((short) 1, (short) 1, (short) 58, (short) 58);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.PutTBonus(ChaosBonus.tbBomb, 1);
        chaosObjects.PutTBonus(ChaosBonus.tbBonusLevel, 1);
        chaosObjects.PutHospital(8);
        chaosObjects.PutBullet(8);
        chaosObjects.PutAlien1(ChaosAlien.aBigDrawer, 0, 1);
        chaosObjects.PutAlien1(ChaosAlien.aSmallDrawer, 0, 1);
        chaos1Zone.AddOptions((short) 1, (short) 1, (short) 58, (short) 58, 3, 3, 3, 3, 3, 3, 3);
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
