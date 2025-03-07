package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus.Moneys;
import ch.chaos.library.Memory;
import ch.chaos.library.Registration;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class Chaos1Zone {

    // Imports
    private final ChaosBase chaosBase;
    private final ChaosGenerator chaosGenerator;
    private final ChaosGraphics chaosGraphics;
    private final ChaosObjects chaosObjects;
    private final Registration registration;
    private final Trigo trigo;


    private Chaos1Zone() {
        instance = this; // Set early to handle circular dependencies
        chaosBase = ChaosBase.instance();
        chaosGenerator = ChaosGenerator.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosObjects = ChaosObjects.instance();
        registration = Registration.instance();
        trigo = Trigo.instance();
    }


    // VAR

    public boolean flipVert;
    public boolean flipHorz;
    public boolean rotate;
    public short pLife2;
    public short pLife3;
    public short pLife4;
    public Runtime.RangeSet fillTypes = new Runtime.RangeSet(Memory.SET16_r);
    public short[] fillCount = new short[16];
    public short[] fillRndAdd = new short[16];
    public Anims[] fKind = Runtime.initArray(new Anims[4]);
    public short[] fSubKind = new short[4];
    public int[] aStat = new int[4];


    public boolean isFlipVert() {
        return this.flipVert;
    }

    public void setFlipVert(boolean flipVert) {
        this.flipVert = flipVert;
    }

    public boolean isFlipHorz() {
        return this.flipHorz;
    }

    public void setFlipHorz(boolean flipHorz) {
        this.flipHorz = flipHorz;
    }

    public boolean isRotate() {
        return this.rotate;
    }

    public void setRotate(boolean rotate) {
        this.rotate = rotate;
    }

    public short getPLife2() {
        return this.pLife2;
    }

    public void setPLife2(short pLife2) {
        this.pLife2 = pLife2;
    }

    public short getPLife3() {
        return this.pLife3;
    }

    public void setPLife3(short pLife3) {
        this.pLife3 = pLife3;
    }

    public short getPLife4() {
        return this.pLife4;
    }

    public void setPLife4(short pLife4) {
        this.pLife4 = pLife4;
    }

    public Runtime.RangeSet getFillTypes() {
        return this.fillTypes;
    }

    public void setFillTypes(Runtime.RangeSet fillTypes) {
        this.fillTypes = fillTypes;
    }

    public short[] getFillCount() {
        return this.fillCount;
    }

    public void setFillCount(short[] fillCount) {
        this.fillCount = fillCount;
    }

    public short[] getFillRndAdd() {
        return this.fillRndAdd;
    }

    public void setFillRndAdd(short[] fillRndAdd) {
        this.fillRndAdd = fillRndAdd;
    }

    public Anims[] getFKind() {
        return this.fKind;
    }

    public void setFKind(Anims[] fKind) {
        this.fKind = fKind;
    }

    public short[] getFSubKind() {
        return this.fSubKind;
    }

    public void setFSubKind(short[] fSubKind) {
        this.fSubKind = fSubKind;
    }

    public int[] getAStat() {
        return this.aStat;
    }

    public void setAStat(int[] aStat) {
        this.aStat = aStat;
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

    public void RectFill(short sx, short sy, short ex, short ey) {
        // VAR
        int which = 0;
        int add = 0;
        short s = 0;
        short count = 0;
        short c = 0;

        do {
            which = trigo.RND() % 14;
        } while (!fillTypes.contains(which));
        if (fillTypes.contains(fCrunchX)) {
            sx -= 2;
            ex += 2;
            sy++;
            ey--;
            chaosObjects.Fill(sx, sy, ex, ey, (short) (chaosObjects.Get((short) (sx + 2), sy) % ChaosGraphics.NbBackground));
        }
        if (fillTypes.contains(fCrunchY)) {
            sy -= 2;
            ey -= 2;
            sx++;
            sy++;
            chaosObjects.Fill(sx, sy, ex, ey, (short) (chaosObjects.Get(sx, (short) (sy + 2)) % ChaosGraphics.NbBackground));
        }
        chaosObjects.Rect(sx, sy, ex, ey);
        count = fillCount[which];
        add = fillRndAdd[which];
        if (add != 0)
            count += trigo.RND() % (add + 1);
        switch (which) {
            case fKmk -> {
                count = (short) (count / 4);
                chaosObjects.PutKamikaze(0, count);
                chaosObjects.PutKamikaze(1, count);
                chaosObjects.PutKamikaze(2, count);
                chaosObjects.PutKamikaze(3, count);
            }
            case fPic -> {
                count = (short) (count / 2);
                chaosObjects.PutPic(0, count);
                chaosObjects.PutPic(1, count);
            }
            case fMoneyS -> {
                chaosObjects.PutChaosSterling(count);
            }
            case fMoneyMix -> {
                chaosObjects.PutMoney(EnumSet.of(Moneys.m1, Moneys.m2, Moneys.m5, Moneys.st), count);
            }
            case fAlienColor -> {
                chaosObjects.PutAColor(10, chaosBase.difficulty * 12, count);
            }
            case fAlienFour -> {
                chaosObjects.PutCFour(40, 40 + chaosBase.difficulty * 5, count);
            }
            case fCannon1 -> {
                c = (short) (trigo.RND() % count);
                chaosObjects.PutDeltaObjs(Anims.MACHINE, (short) ChaosMachine.mCannon1, 0, (short) -1, (short) 0, c);
                c = (short) (count - c);
                chaosObjects.PutDeltaObjs(Anims.MACHINE, (short) ChaosMachine.mCannon1, 1, (short) 1, (short) 0, c);
            }
            case fCannon2 -> {
                if (chaosObjects.Get(sx, (short) (sy - 1)) >= ChaosGraphics.NbBackground) {
                    c = 0;
                    s = -1;
                } else {
                    c = 1;
                    s = 1;
                }
                chaosObjects.PutDeltaObjs(Anims.MACHINE, (short) ChaosMachine.mCannon2, c, (short) 0, s, count);
            }
            case fCartoon -> {
                chaosObjects.PutCartoon(0, 2, count);
            }
            case fNone -> {
            }
            case fAnims1 -> {
                chaosObjects.PutRandomObjs(fKind[0], fSubKind[0], aStat[0], count);
            }
            case fAnims2 -> {
                chaosObjects.PutRandomObjs(fKind[1], fSubKind[1], aStat[1], count);
            }
            case fAnims3 -> {
                chaosObjects.PutRandomObjs(fKind[2], fSubKind[2], aStat[2], count);
            }
            case fAnims4 -> {
                chaosObjects.PutRandomObjs(fKind[3], fSubKind[3], aStat[3], count);
            }
            default -> throw new RuntimeException("Unhandled CASE value " + which);
        }
    }

    private void AddOptions_AddOption(Anims kind, short sKind, int cnt, int min) {
        if ((chaosBase.difficulty >= min) && (cnt > 0))
            chaosObjects.PutRandomObjs(kind, sKind, pLife3 + chaosBase.difficulty * 2, cnt);
    }

    public void AddOptions(short sx, short sy, short ex, short ey, int nbGrid, int nbBumper, int nbChief, int nbGhost, int nbPopup, int nbBig, int nbSquare) {
        chaosObjects.Rect(sx, sy, ex, ey);
        AddOptions_AddOption(Anims.ALIEN2, (short) ChaosCreator.cGrid, nbGrid, 2);
        AddOptions_AddOption(Anims.ALIEN1, (short) ChaosAlien.aBumper, nbBumper, 3);
        AddOptions_AddOption(Anims.ALIEN2, (short) ChaosCreator.cChief, nbChief, 4);
        AddOptions_AddOption(Anims.ALIEN2, (short) ChaosCreator.cGhost, nbGhost, 5);
        AddOptions_AddOption(Anims.ALIEN2, (short) ChaosCreator.cPopUp, nbPopup, 6);
        AddOptions_AddOption(Anims.ALIEN1, (short) ChaosAlien.aBig, nbBig, 7);
        AddOptions_AddOption(Anims.ALIEN1, (short) ChaosAlien.aSquare, nbSquare, 8);
    }

    private void DrawVertRocs(short sx, short sy, short ey, short val) {
        // VAR
        short rx = 0;
        short re = 0;
        short rh = 0;
        short x = 0;
        short y = 0;
        short oy = 0;
        short bx = 0;
        short ex = 0;
        short sh = 0;
        short eh = 0;

        y = sy;
        while (true) {
            oy = y;
            y += trigo.RND() % 5 + 4;
            if (y > ey)
                break;
            RectFill(sx, oy, (short) (sx + 13), (short) (y - 1));
            rx = (short) (chaosObjects.Rnd((short) 8) + 1);
            re = (short) (chaosObjects.Rnd((short) (9 - rx)) + 4);
            rh = (short) (chaosObjects.Rnd((short) 8) + 1);
            sh = (short) (chaosObjects.Rnd(rh) + 1);
            eh = (short) (chaosObjects.Rnd(rh) + 1);
            bx = (short) (chaosObjects.Rnd(re) + rx);
            ex = (short) (chaosObjects.Rnd(re) + rx);
            re += rx;
            while ((y <= ey) && (rh > 0)) {
                for (x = rx; x <= re; x++) {
                    if ((rh < sh) || (rh > eh) || (x < bx) || (x > ex))
                        chaosObjects.Put((short) (sx + x), y, val);
                }
                y++;
                rh--;
            }
        }
    }

    public void Entry() {
        // VAR
        short val = 0;
        short cnt = 0;

        fillTypes = new Runtime.RangeSet(Memory.SET16_r).with(fMoneyS, fAlienColor, fAlienFour);
        fillCount[fMoneyS] = 0;
        fillRndAdd[fMoneyS] = 0;
        fillCount[fAlienColor] = 1;
        fillRndAdd[fAlienColor] = 6;
        fillCount[fAlienFour] = 1;
        fillRndAdd[fAlienFour] = 1;
        chaosObjects.Cadre((short) 16, (short) 120);
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Back8x8;
        chaosObjects.Fill((short) 1, (short) 9, (short) 14, (short) 118, val);
        if (trigo.RND() % 8 == 0)
            val = BigBlock;
        else
            val = EmptyBlock;
        DrawVertRocs((short) 1, (short) 11, (short) 117, val);
        chaosObjects.FillCond((short) 1, (short) 9, (short) 3, (short) 118, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Back4x4);
        chaosObjects.FillCond((short) 12, (short) 9, (short) 14, (short) 118, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Back4x4);
        cnt = (short) (trigo.RND() % 4 + 4);
        while (cnt > 0) {
            val = (short) (trigo.RND() % 4);
            switch (val) {
                case 0 -> val = Sq1Block;
                case 1 -> val = Sq4Block;
                case 2 -> val = EmptyBlock;
                case 3 -> val = SimpleBlock;
                default -> throw new RuntimeException("Unhandled CASE value " + val);
            }
            chaosGenerator.PutCross((short) 1, (short) 9, (short) 14, (short) 117, val);
            cnt--;
        }
        chaosObjects.PutRandom((short) 2, (short) 12, (short) 13, (short) 116, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Sq1Block, (short) (trigo.RND() % 64));
        chaosObjects.PutRandom((short) 2, (short) 12, (short) 13, (short) 116, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Sq4Block, (short) (trigo.RND() % 32));
        chaosObjects.PutRandom((short) 2, (short) 2, (short) 13, (short) 116, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) SimpleBlock, (short) (trigo.RND() % 4));
        if ((chaosBase.difficulty >= 3) && (trigo.RND() % 2 == 0)) {
            chaosObjects.FillCond((short) 1, (short) 9, (short) 14, (short) 118, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Ice);
            chaosBase.snow = true;
        }
        chaosObjects.Fill((short) 1, (short) 1, (short) 14, (short) 8, (short) EmptyBlock);
        chaosObjects.Fill((short) 1, (short) 1, (short) 3, (short) 3, (short) BackSmall);
        chaosObjects.Fill((short) 9, (short) 1, (short) 14, (short) 3, (short) Back2x2);
        chaosObjects.Fill((short) 1, (short) 4, (short) 1, (short) 6, (short) FalseEmpty);
        chaosObjects.Fill((short) 2, (short) 6, (short) 8, (short) 6, (short) FalseEmpty);
        chaosObjects.Fill((short) 9, (short) 4, (short) 9, (short) 7, (short) FalseEmpty);
        chaosObjects.Fill((short) 14, (short) 4, (short) 14, (short) 8, (short) (ChaosGraphics.NbBackground - 1));
        if (chaosBase.powerCountDown > 10)
            chaosObjects.FillObj(Anims.SMARTBONUS, (short) ChaosSmartBonus.sbExtraPower, 0, (short) 1, (short) 1, (short) 2, (short) 1, false);
        chaosObjects.FillObj(Anims.SMARTBONUS, (short) ChaosSmartBonus.sbExtraLife, 0, (short) 9, (short) 1, (short) 12, (short) 1, false);
        chaosObjects.FillObj(Anims.BONUS, (short) ChaosBonus.Money, Moneys.st.ordinal(), (short) 9, (short) 2, (short) 14, (short) 3, true);
        chaosObjects.PutExtraPower((short) 7, (short) 2, (short) 9);
        chaosObjects.PutExtraPower((short) 7, (short) 1, (short) 10);
        chaosObjects.PutExtraPower((short) 7, (short) 1, (short) 118);
        chaosObjects.Rect((short) 1, (short) 9, (short) 14, (short) 118);
        chaosObjects.PutIsolated(0, chaosBase.difficulty * 4 - 4, (short) 12, (short) 12, (short) SimpleBlock);
        AddOptions((short) 1, (short) 17, (short) 15, (short) 117, 0, 0, 1, 0, 0, 5, 1);
        chaosObjects.PutRandomObjs(Anims.BONUS, (short) ChaosBonus.Money, Moneys.st.ordinal(), 8 + trigo.RND() % 16);
        chaosObjects.Rect((short) 1, (short) 50, (short) 14, (short) 70);
        chaosObjects.PutHospital(1);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.Rect((short) 1, (short) 15, (short) 14, (short) 118);
        chaosObjects.PutBullet(5);
        chaosObjects.PutExit((short) 14, (short) 118);
        chaosObjects.PutPlayer((short) 1, (short) 9);
    }

    public void Groove() {
        // VAR
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short z = 0;
        short dx = 0;
        short dy = 0;
        Runtime.Ref<Short> angle = new Runtime.Ref<>((short) 0);
        short c = 0;
        short val = 0;

        chaosObjects.Cadre((short) 100, (short) 50);
        for (y.set((short) 0); y.get() <= 49; y.inc()) {
            for (x.set((short) 0); x.get() <= 99; x.inc()) {
                z = (short) (y.get() - 12 + chaosObjects.Rnd((short) 24));
                if (z <= 24)
                    val = Fade3;
                else if (z <= 40)
                    val = Fade2;
                else
                    val = Fade1;
                chaosObjects.Put(x.get(), y.get(), val);
            }
        }
        chaosGenerator.Excavate((short) 1, (short) 98, (short) 1, (short) 24, (short) 4, (short) 10, (short) 8, (short) 7, (short) 4, (short) 2, (short) 1, (short) 1, (short) 6);
        chaosGenerator.Excavate((short) 1, (short) 98, (short) 26, (short) 39, (short) 3, (short) 7, (short) 2, (short) 5, (short) 2, (short) 1, (short) 2, (short) 1, (short) 3);
        chaosGenerator.Excavate((short) 1, (short) 98, (short) 41, (short) 48, (short) 2, (short) 2, (short) 5, (short) 2, (short) 6, (short) 1, (short) 0, (short) 5, (short) 10);
        chaosObjects.Fill((short) 98, (short) 1, (short) 98, (short) 37, (short) BackNone);
        chaosObjects.Fill((short) 1, (short) 26, (short) 1, (short) 47, (short) BackNone);
        chaosObjects.Fill((short) 98, (short) 42, (short) 98, (short) 48, (short) BackNone);
        for (y.set((short) 0); y.get() <= 49; y.inc()) {
            for (x.set((short) 0); x.get() <= 99; x.inc()) {
                z = (short) (y.get() - 4 + chaosObjects.Rnd((short) 8));
                if (z <= 20)
                    val = BackNone;
                else if (z <= 40)
                    val = Tar;
                else
                    val = Balls;
                if (chaosObjects.OnlyBackground(x.get(), y.get()))
                    chaosObjects.Put(x.get(), y.get(), val);
            }
        }
        chaosObjects.Rect((short) 2, (short) 2, (short) 99, (short) 49);
        for (c = 1; c <= 9; c++) {
            if (chaosGenerator.FindIsolatedRect((short) 2, (short) 2, (short) 99, (short) 49, (short) 2, (short) 10, angle, x, y, true)) {
                chaosGenerator.MakeLink(x.get(), y.get(), (short) 0, angle.get(), (short) Round4);
                if (trigo.COS(angle.get()) == 0)
                    dx = 1;
                else
                    dx = 0;
                dy = (short) (1 - dx);
                for (z = -2; z <= 2; z++) {
                    chaosObjects.Put((short) (x.get() + z * dx), (short) (y.get() + z * dy), (short) Round4);
                }
                chaosObjects.Rect((short) (x.get() - 2), (short) (y.get() - 2), (short) (x.get() + 2), (short) (y.get() + 2));
                chaosObjects.PutBullet(1);
                if (c <= 4)
                    chaosObjects.PutBullet(1);
                else
                    chaosObjects.PutHospital(1);
            }
        }
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 99, (short) 49, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) F9x9, (short) (trigo.RND() % (chaosBase.difficulty * 4 + 1)));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 99, (short) 49, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) FRound, (short) (trigo.RND() % 32));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 99, (short) 49, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) FStar, (short) (trigo.RND() % (64 + chaosBase.difficulty * 6)));
        chaosObjects.Rect((short) 51, (short) 1, (short) 98, (short) 48);
        chaosObjects.PutIsolated(2, chaosBase.difficulty + 2, (short) 48, (short) 48, (short) SimpleBlock);
        chaosObjects.Rect((short) 2, (short) 2, (short) 99, (short) 49);
        chaosObjects.PutColor(pLife3, 60);
        chaosObjects.PutCartoon(2, 2, 10);
        chaosObjects.Rect((short) 2, (short) 2, (short) 99, (short) 40);
        chaosObjects.PutFour(pLife3, 30);
        chaosObjects.Rect((short) 2, (short) 20, (short) 99, (short) 49);
        chaosObjects.PutQuad(pLife3, 12 + chaosBase.difficulty);
        if (chaosBase.stages >= 5)
            chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        if (chaosBase.stages == 0)
            chaosObjects.PutBlockBonus(ChaosBonus.tbNoMissile, (short) 98, (short) 2);
        chaosObjects.PutExtraPower((short) 3, (short) 1, (short) 26);
        chaosObjects.PutExtraPower((short) 3, (short) 98, (short) 1);
        chaosObjects.FillObj(Anims.DEADOBJ, (short) ChaosDObj.doCartoon, 0, (short) 98, (short) 3, (short) 98, (short) 10, false);
        chaosObjects.PutBlockBonus(ChaosBonus.tbInvinsibility, (short) 98, (short) 32);
        AddOptions((short) 2, (short) 2, (short) 99, (short) 49, 4, 8, 2, 3, 10, 6, 4);
        chaosObjects.PutExit((short) 98, (short) 48);
        chaosObjects.PutPlayer((short) 1, (short) 12);
    }

    public void Garden() {
        chaosObjects.Cadre((short) 61, (short) 61);
        chaosGenerator.DrawPacman(10 - chaosBase.difficulty, (short) 6, (short) 6, (short) 6, (short) 6, (short) 54, (short) 54);
        chaosObjects.FillRandom((short) 0, (short) 0, (short) 60, (short) 60, (short) Forest1, (short) Forest7, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), Runtime.proc(chaosObjects::Rnd, "ChaosObjects.Rnd"));
        chaosObjects.FillCond((short) 0, (short) 0, (short) 60, (short) 60, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Ground);
        chaosObjects.Rect((short) 1, (short) 1, (short) 59, (short) 59);
        chaosObjects.PutIsolated(0, 2, (short) 10, (short) 25, (short) Leaf1);
        chaosObjects.Rect((short) 1, (short) 1, (short) 59, (short) 59);
        chaosObjects.PutIsolated(0, 8, (short) 10, (short) 25, (short) Leaf2);
        chaosObjects.Rect((short) 1, (short) 1, (short) 59, (short) 59);
        chaosObjects.PutIsolated(0, 8, (short) 10, (short) 25, (short) Leaf3);
        chaosObjects.Rect((short) 1, (short) 1, (short) 59, (short) 59);
        chaosObjects.PutIsolated(0, 7, (short) 10, (short) 25, (short) SimpleBlock);
        if ((chaosBase.difficulty >= 3) && (trigo.RND() % 2 == 0))
            chaosObjects.PutRandom((short) 0, (short) 0, (short) 60, (short) 60, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Ground2, (short) 100);
        chaosObjects.FillObj(Anims.ALIEN1, (short) ChaosAlien.aCartoon, 0, (short) 29, (short) 29, (short) 31, (short) 31, false);
        chaosObjects.PutExtraLife((short) 1, (short) 59);
        chaosObjects.PutExtraLife((short) 59, (short) 1);
        chaosObjects.Rect((short) 30, (short) 30, (short) 40, (short) 40);
        chaosObjects.PutInvinsibility(1);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.PutGridObjs(Anims.BONUS, (short) ChaosBonus.TimedBonus, ChaosBonus.tbHospital, (short) 20, (short) 20, (short) 20, (short) 20, (short) 1, (short) 1);
        chaosObjects.Rect((short) 2, (short) 2, (short) 58, (short) 58);
        if (chaosBase.powerCountDown >= 6)
            chaosObjects.PutRandomObjs(Anims.SMARTBONUS, (short) ChaosSmartBonus.sbExtraPower, 0, 1);
        chaosObjects.PutHospital(1);
        chaosObjects.PutCartoon(0, 2, 20);
        chaosObjects.PutNest(0, 40 + chaosBase.difficulty);
        AddOptions((short) 2, (short) 2, (short) 59, (short) 59, 4, 10, 2, 0, 1, 3, 0);
        chaosObjects.PutExit((short) 57, (short) 57);
        chaosObjects.PutPlayer((short) 1, (short) 1);
    }

    public void Lake() {
        // VAR
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short sz = 0;
        short d = 0;
        Runtime.Ref<Short> angle = new Runtime.Ref<>((short) 0);
        short c = 0;
        short val = 0;

        chaosBase.water = (chaosBase.difficulty < 7) || (trigo.RND() % 4 != 0);
        chaosObjects.Cadre((short) 124, (short) 124);
        chaosObjects.Fill((short) 0, (short) 0, (short) 123, (short) 123, (short) SimpleBlock);
        if ((trigo.RND() % 8 == 0) && (chaosBase.difficulty > 2))
            val = Ground2;
        else if (!chaosGraphics.dualpf)
            val = Ground;
        else
            val = BackNone;
        chaosGenerator.TripleLoop(val);
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 123, (short) 123, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Leaf1, (short) (trigo.RND() % 256));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 123, (short) 123, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Leaf2, (short) (trigo.RND() % 256));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 123, (short) 123, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Leaf3, (short) (trigo.RND() % 256));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 123, (short) 123, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Leaf4, (short) (trigo.RND() % 256));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 123, (short) 123, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) EmptyBlock, (short) (trigo.RND() % 64));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 123, (short) 123, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) SimpleBlock, (short) (trigo.RND() % 64));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 123, (short) 123, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) FStar, (short) (trigo.RND() % (chaosBase.difficulty * 20)));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 123, (short) 123, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) BigBlock, (short) (trigo.RND() % (chaosBase.difficulty * 20)));
        chaosObjects.PutPlayer((short) 104, (short) 69);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHelp, (short) 105, (short) 69);
        chaosObjects.PutExit((short) 89, (short) 53);
        chaosObjects.PutBubbleMaker((chaosBase.difficulty < 6 ? 1 : 0), (short) 100, (short) 62);
        chaosObjects.PutBubbleMaker((chaosBase.difficulty < 6 ? 1 : 0), (short) 43, (short) 29);
        chaosObjects.PutBubbleMaker((chaosBase.difficulty < 6 ? 1 : 0), (short) 43, (short) 95);
        if (chaosBase.difficulty < 4) {
            chaosObjects.PutBubbleMaker(0, (short) 90, (short) 112);
            chaosObjects.PutBubbleMaker(0, (short) 90, (short) 12);
            chaosObjects.PutBubbleMaker(0, (short) 5, (short) 62);
        }
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 90, (short) 113);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 90, (short) 11);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 5, (short) 63);
        chaosObjects.PutBlockBonus(ChaosBonus.tbDBSpeed, (short) 43, (short) 62);
        if (chaosBase.difficulty >= 2) {
            fillTypes = new Runtime.RangeSet(Memory.SET16_r).with(fNone, fAnims1, fAnims2, fAnims3, fAnims4);
            for (c = 0; c <= 15; c++) {
                fillCount[c] = 1;
                fillRndAdd[c] = 0;
            }
            fKind[0] = Anims.BONUS;
            fSubKind[0] = ChaosBonus.TimedBonus;
            aStat[0] = ChaosBonus.tbInvinsibility;
            fKind[1] = Anims.BONUS;
            fSubKind[1] = ChaosBonus.TimedBonus;
            aStat[1] = ChaosBonus.tbSleeper;
            fKind[2] = Anims.BONUS;
            fSubKind[2] = ChaosBonus.TimedBonus;
            aStat[2] = ChaosBonus.tbMagnet;
            fKind[3] = Anims.BONUS;
            fSubKind[3] = ChaosBonus.TimedBonus;
            aStat[3] = ChaosBonus.tbBullet;
            for (c = 1; c <= 10; c++) {
                sz = (short) (trigo.RND() % 5 + 1);
                d = (short) (sz * 2 + 1);
                if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 123, (short) 123, sz, (short) 30, angle, px, py, true)) {
                    chaosObjects.Fill((short) (px.get() - sz), (short) (py.get() - sz), (short) (px.get() + sz), (short) (py.get() + sz), (short) Balls);
                    chaosGenerator.FillEllipse(px.get() - sz, py.get() - sz, d, d, (short) Tar);
                    if ((c == 10) && (chaosBase.stages == 0))
                        chaosObjects.PutBlockBonus(ChaosBonus.tbNoMissile, px.get(), py.get());
                    RectFill((short) (px.get() - sz), (short) (py.get() - sz), (short) (px.get() + sz), (short) (py.get() + sz));
                    chaosGenerator.MakeLink(px.get(), py.get(), sz, angle.get(), (short) FalseBlock);
                }
            }
        }
        chaosObjects.Rect((short) 50, (short) 100, (short) 120, (short) 120);
        chaosObjects.PutDeadObj(ChaosDObj.doWindMaker, 0, 3);
        chaosObjects.PutChaosObjs(Anims.DEADOBJ, (short) ChaosDObj.doSand, 0, (short) 2000, (short) 32, (short) 2700, (short) 400, 20);
        chaosObjects.Rect((short) 50, (short) 1, (short) 120, (short) 62);
        chaosObjects.PutQuad(pLife3, 4 + chaosBase.difficulty);
        chaosObjects.Rect((short) 1, (short) 1, (short) 50, (short) 120);
        chaosObjects.PutABox(0, 4 + chaosBase.difficulty / 2);
        chaosObjects.Rect((short) 1, (short) 1, (short) 70, (short) 120);
        chaosObjects.PutColor(pLife3, 20);
        chaosObjects.Rect((short) 30, (short) 80, (short) 60, (short) 110);
        chaosObjects.PutRandomObjs(Anims.BONUS, (short) ChaosBonus.BonusLevel, ChaosBonus.tbBonusLevel, 1);
        chaosObjects.Rect((short) 50, (short) 40, (short) 100, (short) 84);
        chaosObjects.PutCannon3(5);
        chaosObjects.Rect((short) 1, (short) 1, (short) 122, (short) 122);
        chaosObjects.PutBullet(10);
        chaosObjects.PutAlien1(ChaosAlien.aBigDrawer, 0, 1);
        chaosObjects.PutNest(1, 20 + chaosBase.difficulty / 3);
        chaosObjects.PutFour(pLife3, 10);
        chaosObjects.PutCartoon(2, 2, 15);
        chaosObjects.PutChaosSterling(20 - chaosBase.difficulty);
        if (chaosBase.difficulty >= 2)
            chaosObjects.PutTrefle(pLife2, 5);
        AddOptions((short) 1, (short) 1, (short) 122, (short) 122, 2, 0, 0, 0, 12, 0, 0);
        AddOptions((short) 1, (short) 60, (short) 122, (short) 122, 0, 0, 0, 0, 0, chaosBase.difficulty, 0);
        AddOptions((short) 1, (short) 1, (short) 122, (short) 60, 0, 0, 0, 0, 0, 0, chaosBase.difficulty);
        if (chaosBase.powerCountDown > 3) {
            chaosObjects.FillCond((short) 90, (short) 12, (short) 90, (short) 28, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) 21);
            chaosObjects.PutExtraPower((short) 3, (short) 90, (short) 28);
            chaosObjects.FillObj(Anims.DEADOBJ, (short) ChaosDObj.doCartoon, 0, (short) 90, (short) 24, (short) 90, (short) 27, false);
        }
    }

    public void Site() {
        // VAR
        Runtime.Ref<Short> a = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short sz = 0;
        short d = 0;
        int cnt = 0;
        short val = 0;
        short val2 = 0;

        chaosObjects.Clear((short) 100, (short) 100);
        chaosObjects.Fill((short) 0, (short) 0, (short) 99, (short) 99, (short) BackNone);
        chaosObjects.Fill((short) 40, (short) 21, (short) 40, (short) 60, (short) SimpleBlock);
        chaosObjects.Fill((short) 60, (short) 40, (short) 60, (short) 79, (short) SimpleBlock);
        cnt = trigo.RND() % 15 + 7;
        while (cnt > 0) {
            val = (short) (trigo.RND() % 5);
            switch (val) {
                case 0 -> {
                    val = Sq1Block;
                    val2 = Sq4Block;
                }
                case 1 -> {
                    val = Sq4Block;
                    val2 = Sq4TravBlock;
                }
                case 2 -> {
                    val = Sq4TravBlock;
                    val2 = Sq4Block;
                }
                case 3 -> {
                    val = BarDark;
                    val2 = BarDark;
                }
                case 4 -> {
                    val = BigBlock;
                    val2 = EmptyBlock;
                }
                default -> throw new RuntimeException("Unhandled CASE value " + val);
            }
            chaosGenerator.PutCross((short) 20, (short) 20, (short) 60, (short) 60, val);
            if (cnt < 8) {
                sz = (short) (trigo.RND() % 3 + 1);
                if (chaosGenerator.FindIsolatedRect((short) 22, (short) 22, (short) 58, (short) 58, sz, (short) 0, a, x, y, false)) {
                    d = (short) (sz * 2 + 1);
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, val);
                    sz--;
                    d -= 2;
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, val2);
                }
            }
            cnt--;
        }
        chaosObjects.Fill((short) 20, (short) 20, (short) 79, (short) 20, (short) SimpleBlock);
        chaosObjects.Fill((short) 20, (short) 79, (short) 79, (short) 79, (short) SimpleBlock);
        chaosObjects.Fill((short) 20, (short) 21, (short) 20, (short) 78, (short) SimpleBlock);
        chaosObjects.Fill((short) 79, (short) 21, (short) 79, (short) 77, (short) SimpleBlock);
        chaosObjects.Put((short) 79, (short) 78, (short) 21);
        if (!chaosGraphics.dualpf)
            chaosObjects.FillRandom((short) 0, (short) 0, (short) 99, (short) 99, (short) 0, (short) 7, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), Runtime.proc(chaosObjects::ExpRandom, "ChaosObjects.ExpRandom"));
        chaosObjects.FillRandom((short) 20, (short) 20, (short) 79, (short) 79, (short) 12, (short) 13, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), Runtime.proc(chaosObjects::ExpRandom, "ChaosObjects.ExpRandom"));
        chaosObjects.FillCond((short) 45, (short) 45, (short) 54, (short) 54, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) 20);
        chaosObjects.PutPlayer((short) 78, (short) 78);
        chaosObjects.PutExit((short) 21, (short) 21);
        chaosObjects.PutExtraLife((short) 21, (short) 78);
        chaosObjects.Rect((short) 21, (short) 21, (short) 78, (short) 78);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.PutAlien1(ChaosAlien.aSmallDrawer, 0, 1);
        chaosObjects.PutFour(pLife3, 8);
        chaosObjects.Rect((short) 21, (short) 21, (short) 39, (short) 78);
        chaosObjects.PutCreatorR(10);
        chaosObjects.Rect((short) 30, (short) 21, (short) 60, (short) 78);
        chaosObjects.PutCreatorC(30);
        chaosObjects.Rect((short) 60, (short) 21, (short) 78, (short) 78);
        chaosObjects.PutAlien1(ChaosAlien.aStar, pLife3, 8);
        chaosObjects.Rect((short) 21, (short) 40, (short) 60, (short) 78);
        chaosObjects.PutTri(pLife3, 20);
        chaosObjects.Rect((short) 21, (short) 21, (short) 60, (short) 60);
        chaosObjects.PutInvinsibility(1);
        chaosObjects.PutSleeper(1);
        if ((chaosBase.difficulty >= 3) || !registration.registered) {
            chaosObjects.PutMagnet(1);
            chaosObjects.PutTurret(8);
            chaosObjects.PutMagnetR(3, 6);
            chaosObjects.PutMagnetA(1, 2);
        }
        chaosObjects.PutFreeFire(1);
        chaosObjects.Rect((short) 21, (short) 21, (short) 78, (short) 78);
        chaosObjects.PutBullet(30);
        chaosObjects.Rect((short) 0, (short) 0, (short) 99, (short) 99);
        chaosObjects.PutCannon3(7);
        chaosObjects.FillObj(Anims.BONUS, (short) ChaosBonus.Money, Moneys.m10.ordinal(), (short) 99, (short) 0, (short) 99, (short) 0, true);
        chaosObjects.PutBlockBonus(ChaosBonus.tbBullet, (short) 0, (short) 99);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doMagnetR, 3, (short) 97, (short) 97);
        chaosObjects.PutExtraPower((short) 5, (short) 78, (short) 21);
        chaosObjects.PutBlockBonus(ChaosBonus.tbBomb, (short) 0, (short) 0);
        chaosObjects.Fill((short) 49, (short) 49, (short) 51, (short) 51, (short) 20);
        chaosObjects.FillObj(Anims.ALIEN1, (short) ChaosAlien.aCartoon, 0, (short) 49, (short) 49, (short) 51, (short) 51, false);
        chaosObjects.Rect((short) 30, (short) 30, (short) 59, (short) 59);
        chaosObjects.PutAlien1(ChaosAlien.aHospital, 20 + chaosBase.difficulty, 3);
        AddOptions((short) 21, (short) 21, (short) 60, (short) 78, 0, 0, 5, 0, 8, 0, 0);
    }

    public void GhostCastle() {
        // VAR
        short val = 0;

        flipVert = false;
        rotate = false;
        chaosObjects.Cadre((short) 25, (short) 70);
        chaosObjects.Fill((short) 1, (short) 20, (short) 22, (short) 29, (short) Bricks);
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Round4;
        chaosObjects.Fill((short) 1, (short) 20, (short) 2, (short) 21, val);
        chaosObjects.Fill((short) 16, (short) 23, (short) 22, (short) 23, val);
        chaosObjects.Fill((short) 20, (short) 31, (short) 20, (short) 69, (short) Bricks);
        chaosObjects.FillCond((short) 0, (short) 0, (short) 24, (short) 69, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), val);
        chaosGenerator.GCastle((short) 1, (short) 31, (short) 19, (short) 67, (short) Bricks, (short) 8);
        chaosObjects.FillCond((short) 0, (short) 0, (short) 24, (short) 69, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Bricks);
        chaosObjects.PutRandom((short) 21, (short) 31, (short) 23, (short) 69, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) BackBig, (short) (trigo.RND() % 16));
        chaosObjects.PutRandom((short) 21, (short) 31, (short) 23, (short) 69, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) BackSmall, (short) (trigo.RND() % 16));
        chaosObjects.PutRandom((short) 21, (short) 31, (short) 23, (short) 69, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Back8x8, (short) (trigo.RND() % 16));
        chaosObjects.PutRandom((short) 21, (short) 31, (short) 23, (short) 69, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Back4x4, (short) (trigo.RND() % 16));
        chaosObjects.PutRandom((short) 2, (short) 3, (short) 19, (short) 55, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) IceBlock, (short) (trigo.RND() % 4));
        chaosObjects.PutRandom((short) 2, (short) 3, (short) 19, (short) 55, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) SimpleBlock, (short) (trigo.RND() % 4));
        chaosObjects.PutRandom((short) 2, (short) 3, (short) 19, (short) 55, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) EmptyBlock, (short) (trigo.RND() % 4));
        chaosObjects.PutExtraLife((short) 23, (short) 1);
        chaosObjects.PutExtraPower((short) 1, (short) 2, (short) 21);
        chaosObjects.PutBlockBonus(ChaosBonus.tbDBSpeed, (short) 1, (short) 21);
        chaosObjects.PutBlockBonus(ChaosBonus.tbMagnet, (short) 16, (short) 23);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doCartoon, 0, (short) 22, (short) 23);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mDoor, 0, (short) 23, (short) 21);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mReactor, 0, (short) 23, (short) 68);
        chaosObjects.PutExit((short) 21, (short) 68);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHelp, (short) 22, (short) 68);
        chaosObjects.Rect((short) 1, (short) 31, (short) 19, (short) 67);
        chaosObjects.PutBullet(8);
        chaosObjects.PutPlayer((short) 1, (short) 68);
        if (chaosBase.specialStage >= 4)
            chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bBrotherAlien, 0, (short) 17, (short) 3);
        chaosObjects.FillObj(Anims.BONUS, (short) ChaosBonus.Money, Moneys.st.ordinal(), (short) 11, (short) 8, (short) 13, (short) 10, true);
        chaosObjects.Rect((short) 1, (short) 21, (short) 19, (short) 60);
        chaosObjects.PutRandomObjs(Anims.ALIEN2, (short) ChaosCreator.cGhost, pLife3, 10);
        chaosObjects.Rect((short) 21, (short) 30, (short) 23, (short) 67);
        chaosObjects.PutCartoon(0, 2, 15);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doFireMaker, 0, 0, 2, 4);
        if (chaosBase.difficulty > 1) {
            chaosObjects.Rect((short) 1, (short) 21, (short) 1, (short) 66);
            chaosObjects.PutMachine(ChaosMachine.mCannon1, 0, 0, 2);
            chaosObjects.Rect((short) 19, (short) 21, (short) 19, (short) 66);
            chaosObjects.PutMachine(ChaosMachine.mCannon2, 1, 1, 2);
            chaosObjects.Rect((short) 1, (short) 1, (short) 24, (short) 1);
            chaosObjects.PutMachine(ChaosMachine.mCannon2, 0, 0, 4);
        }
        AddOptions((short) 1, (short) 1, (short) 23, (short) 65, 1, 1, 1, 1, 10, 1, 4);
    }

    public void Machinery() {
        // VAR
        short x = 0;
        short y = 0;
        short w = 0;
        short dy = 0;

        chaosObjects.Cadre((short) 80, (short) 40);
        rotate = false;
        chaosObjects.Fill((short) 11, (short) 1, (short) 78, (short) 39, (short) BarLight);
        fillTypes = new Runtime.RangeSet(Memory.SET16_r).with(fCannon1, fCannon2, fAnims1, fCartoon, fNone);
        fillCount[fCannon1] = 1;
        fillRndAdd[fCannon1] = 3;
        fillCount[fCannon2] = 1;
        fillRndAdd[fCannon2] = 3;
        fillCount[fAnims1] = 1;
        fillRndAdd[fAnims1] = 0;
        fillCount[fCartoon] = 1;
        fillRndAdd[fCartoon] = 7;
        fKind[0] = Anims.MACHINE;
        fSubKind[0] = ChaosMachine.mCannon3;
        aStat[0] = 0;
        x = 11;
        y = 1;
        w = (short) (trigo.RND() % 4 + 3);
        dy = -3;
        while (x <= 78) {
            if (trigo.RND() % 4 == 0)
                fillTypes.incl(fCrunchX);
            else
                fillTypes.excl(fCrunchX);
            if (x + w >= 73) {
                w = (short) (79 - x);
                fillTypes.excl(fCrunchX);
            }
            chaosObjects.Fill(x, y, (short) (x + w - 1), (short) (y + 5), (short) 0);
            if (dy < 0)
                RectFill(x, y, (short) (x + w - 1), (short) (y - dy - 1));
            else
                RectFill(x, (short) (y + 6 - dy), (short) (x + w - 1), (short) (y + 5));
            x += w;
            dy = (short) (trigo.RND() % 3 + 3);
            if (trigo.RND() % 2 == 0)
                dy = (short) -dy;
            if (y < 6)
                dy = (short) Math.abs(dy);
            else if (y > 10)
                dy = (short) -Math.abs(dy);
            y += dy;
            w = (short) (trigo.RND() % 4 + 3);
        }
        chaosObjects.Fill((short) 78, y, (short) 78, (short) 35, (short) 0);
        chaosGenerator.Cave((short) 78, (short) 21, (short) 11, (short) 38, (short) 30, (short) 35, (short) -1);
        chaosObjects.FillCond((short) 0, (short) 0, (short) 79, (short) 39, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) BarLight);
        chaosObjects.FillCond((short) 0, (short) 0, (short) 79, (short) 39, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Light);
        chaosObjects.PutPlayer((short) 1, (short) 38);
        chaosObjects.Rect((short) 1, (short) 4, (short) 1, (short) 36);
        chaosObjects.PutDeadObj(ChaosDObj.doMirror, 0, 12);
        chaosObjects.Rect((short) 10, (short) 4, (short) 10, (short) 36);
        chaosObjects.PutDeadObj(ChaosDObj.doMirror, 0, 12);
        chaosObjects.Rect((short) 2, (short) 4, (short) 9, (short) 39);
        chaosObjects.PutMachine(ChaosMachine.mCannon1, 0, 1, 20);
        chaosObjects.Rect((short) 2, (short) 1, (short) 9, (short) 3);
        chaosObjects.PutMachine(ChaosMachine.mCannon2, 0, 0, 3);
        chaosObjects.Rect((short) 11, (short) 21, (short) 78, (short) 38);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 1, 1, 1, 20);
        chaosObjects.Rect((short) 11, (short) 21, (short) 78, (short) 38);
        chaosObjects.PutTurret(15);
        chaosObjects.Rect((short) 5, (short) 1, (short) 78, (short) 38);
        chaosObjects.PutIsolatedObjs(Anims.MACHINE, (short) ChaosMachine.mTraverse, 0, 1, 1, 15);
        chaosObjects.Rect((short) 1, (short) 1, (short) 78, (short) 35);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.PutMagnetA(1, 4);
        chaosObjects.PutMagnetR(3, 30);
        AddOptions((short) 1, (short) 1, (short) 78, (short) 38, 0, 0, 6, 0, 10, 0, 3);
    }

    public void IceRink() {
        // VAR
        short c = 0;
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> a = new Runtime.Ref<>((short) 0);
        short dx = 0;
        short dy = 0;
        short sz = 0;
        short st = 0;
        int count = 0;

        chaosObjects.Clear((short) 80, (short) 80);
        chaosBase.snow = true;
        chaosObjects.Fill((short) 0, (short) 0, (short) 79, (short) 79, (short) IceBlock);
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 79, (short) 79, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) BigBlock, (short) 255);
        chaosObjects.Fill((short) 15, (short) 15, (short) 64, (short) 64, (short) IceBlock);
        chaosObjects.Fill((short) 19, (short) 19, (short) 60, (short) 60, (short) RGBBlock);
        chaosObjects.Fill((short) 20, (short) 20, (short) 59, (short) 59, (short) Ice);
        chaosObjects.PutPlayer((short) 59, (short) 20);
        chaosObjects.PutExit((short) 20, (short) 59);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mTurret, 0, (short) 40, (short) 40);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mCannon3, 0, (short) 40, (short) 40);
        count = trigo.RND() % 128;
        if (count > 64)
            count = count * 2;
        chaosObjects.Rect((short) 20, (short) 20, (short) 59, (short) 59);
        chaosObjects.PutIsolated(count, count, (short) 40, (short) 40, (short) IceBlock);
        count = count / 16;
        chaosObjects.PutIsolated(count, count, (short) 20, (short) 40, (short) RGBBlock);
        fillCount[fMoneyMix] = 3;
        fillRndAdd[fMoneyMix] = 6;
        fillCount[fAnims1] = 1;
        fillRndAdd[fAnims1] = 4;
        fKind[0] = Anims.ALIEN2;
        fSubKind[0] = ChaosCreator.cNest;
        aStat[0] = 0;
        fillCount[fAnims2] = 1;
        fillRndAdd[fAnims2] = 4;
        fKind[1] = Anims.ALIEN2;
        fSubKind[1] = ChaosCreator.cAlienBox;
        aStat[1] = 1;
        count = trigo.RND() % 4 + 6;
        while (count > 0) {
            x.set((short) (chaosObjects.Rnd((short) 40) + 20));
            y.set((short) (chaosObjects.Rnd((short) 40) + 20));
            if (trigo.RND() % 2 == 0) {
                dx = 1;
                dy = 0;
            } else {
                dx = 0;
                dy = 1;
            }
            if (trigo.RND() % 2 == 0) {
                dx = (short) -dx;
                dy = (short) -dy;
            }
            while ((x.get() != 19) && (x.get() != 60) && (y.get() != 19) && (y.get() != 60)) {
                x.inc(dx);
                y.inc(dy);
            }
            sz = (short) (trigo.RND() % 8 + 5);
            st = sz;
            do {
                chaosObjects.Put(x.get(), y.get(), (short) Ice);
                if (sz == st)
                    chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doCartoon, 0, x.get(), y.get());
                x.inc(dx);
                y.inc(dy);
                sz--;
            } while (sz != 0);
            sz = (short) (trigo.RND() % 3 + 3);
            chaosObjects.Fill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz), (short) Ice);
            fillTypes = new Runtime.RangeSet(Memory.SET16_r).with(fAnims1, fAnims2);
            if (trigo.RND() % 4 == 0)
                fillTypes.incl(fCrunchX);
            if (trigo.RND() % 4 == 0)
                fillTypes.incl(fCrunchY);
            RectFill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz));
            count--;
        }
        for (c = 1; c <= chaosObjects.Rnd((short) 4) + 6; c++) {
            sz = (short) (trigo.RND() % 5 + 2);
            if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 79, (short) 79, sz, (short) 10, a, x, y, true)) {
                chaosGenerator.MakeLink(x.get(), y.get(), (short) 0, a.get(), (short) Ice);
                dx = (short) (sz * 2 + 1);
                dy = (short) (sz / 2 * 2 + 1);
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz / 2, dx, dy, (short) Ice);
                fillTypes = new Runtime.RangeSet(Memory.SET16_r).with(fMoneyMix);
                RectFill((short) (x.get() - sz), (short) (y.get() - sz / 2), (short) (x.get() + sz), (short) (y.get() + sz / 2));
            }
        }
        chaosObjects.PutExtraLife((short) 20, (short) 20);
        chaosObjects.PutExtraPower((short) 5, (short) 59, (short) 59);
        chaosObjects.PutGridObjs(Anims.BONUS, (short) ChaosBonus.TimedBonus, ChaosBonus.tbHospital, (short) 25, (short) 25, (short) 15, (short) 15, (short) 2, (short) 2);
        chaosObjects.Rect((short) 30, (short) 20, (short) 59, (short) 50);
        chaosObjects.PutMaxPower(1);
        chaosObjects.Rect((short) 20, (short) 20, (short) 59, (short) 59);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.PutTrefle(chaosBase.pLife, 10);
        chaosObjects.PutAlien1(ChaosAlien.aBubble, pLife2, 50);
        chaosObjects.PutCartoon(0, 0, 20);
        chaosObjects.Rect((short) 1, (short) 1, (short) 79, (short) 79);
        chaosObjects.PutBullet(16);
        chaosObjects.Rect((short) 40, (short) 40, (short) 59, (short) 59);
        chaosObjects.PutQuad(pLife2, 20);
        chaosObjects.PutTri(pLife3, chaosBase.difficulty);
        AddOptions((short) 1, (short) 1, (short) 79, (short) 79, 4, 4, 0, 0, 1, 0, 0);
    }

    public void Factory() {
        // VAR
        short c = 0;
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short dx = 0;
        short dy = 0;
        Runtime.Ref<Short> a = new Runtime.Ref<>((short) 0);
        short z = 0;
        short sz = 0;
        short d = 0;
        short sk = 0;

        chaosObjects.Clear((short) 101, (short) 101);
        if (trigo.RND() % 8 != 0)
            rotate = false;
        chaosBase.water = (chaosBase.difficulty >= 4) && (trigo.RND() % 3 == 0);
        chaosObjects.Fill((short) 0, (short) 0, (short) 100, (short) 100, (short) EmptyBlock);
        chaosGenerator.DrawFactory();
        chaosObjects.PutPlayer((short) 12, (short) 90);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHelp, (short) 12, (short) 88);
        chaosObjects.Rect((short) 1, (short) 1, (short) 98, (short) 98);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doWindMaker, 0, 1, 1, 6);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doBubbleMaker, 1, 1, 1, 15);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doFireMaker, 0, 0, 2, 15);
        chaosObjects.Rect((short) 33, (short) 1, (short) 98, (short) 98);
        chaosObjects.PutKamikaze(0, 6);
        chaosObjects.PutKamikaze(1, 6);
        chaosObjects.PutKamikaze(2, 6);
        chaosObjects.PutKamikaze(3, 6);
        chaosObjects.PutPic(0, 10);
        chaosObjects.PutPic(1, 10);
        chaosObjects.Rect((short) 1, (short) 1, (short) 98, (short) 66);
        chaosObjects.PutTurret(15);
        chaosObjects.Rect((short) 70, (short) 1, (short) 98, (short) 98);
        chaosObjects.PutTri(pLife3, 7);
        chaosObjects.Rect((short) 1, (short) 1, (short) 98, (short) 98);
        chaosObjects.PutCannon3(6);
        chaosObjects.PutCartoon(0, 1, 25);
        for (c = 1; c <= 10; c++) {
            if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 99, (short) 99, (short) 2, (short) 8, a, x, y, true)) {
                chaosGenerator.MakeLink(x.get(), y.get(), (short) 0, a.get(), (short) BackBig);
                if (trigo.COS(a.get()) == 0) {
                    dx = 1;
                    dy = 0;
                } else {
                    dx = 0;
                    dy = 1;
                }
                for (z = -2; z <= 2; z++) {
                    chaosObjects.Put((short) (x.get() + z * dx), (short) (y.get() + z * dy), (short) BackSmall);
                    chaosObjects.Put((short) (x.get() - 2 * dx + z * dy), (short) (y.get() - 2 * dy + z * dx), (short) BackSmall);
                    chaosObjects.Put((short) (x.get() + 2 * dx + z * dy), (short) (y.get() + 2 * dy + z * dx), (short) BackSmall);
                }
                chaosObjects.Rect((short) (x.get() - 2), (short) (y.get() - 2), (short) (x.get() + 2), (short) (y.get() + 2));
                chaosObjects.PutBullet(1);
                chaosObjects.PutAlien1(ChaosAlien.aHospital, pLife2, 1);
            }
        }
        chaosObjects.PutMagnetA(3, 15);
        chaosObjects.PutMagnetR(3, 5);
        if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 99, (short) 99, (short) 1, (short) 20, a, x, y, true)) {
            chaosObjects.Fill((short) (x.get() - 1), (short) (y.get() - 1), (short) (x.get() + 1), (short) (y.get() + 1), (short) Ice);
            chaosObjects.PutBlockObj(Anims.BONUS, (short) ChaosBonus.BonusLevel, ChaosBonus.tbBonusLevel, x.get(), y.get());
            chaosGenerator.MakeLink(x.get(), y.get(), (short) 1, a.get(), (short) 23);
        }
        if (chaosBase.difficulty > 2) {
            for (c = 0; c <= 6; c++) {
                sz = (short) (trigo.RND() % 3 + 1);
                if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 99, (short) 99, sz, (short) 20, a, x, y, true)) {
                    chaosObjects.Rect((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz));
                    d = (short) (sz * 2 + 1);
                    chaosGenerator.MakeLink(x.get(), y.get(), sz, a.get(), (short) FalseEmpty);
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) BackNone);
                    if (sz >= 2)
                        sz -= 2;
                    else
                        sz--;
                    d = (short) (2 * sz + 1);
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) SimpleBlock);
                    sk = (short) (trigo.RND() % 4);
                    if (sk == 0)
                        sk = ChaosAlien.aTri;
                    else if (sk == 1)
                        sk = ChaosAlien.aDiese;
                    else
                        sk = ChaosAlien.aBumper;
                    chaosObjects.PutAlien1(sk, pLife2, (c == 0 ? 1 : 0) * 20 + 1);
                }
            }
        }
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 99, (short) 99, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Sq1Block, (short) (trigo.RND() % 64));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 99, (short) 99, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Sq4Block, (short) (trigo.RND() % 64));
        chaosObjects.PutRandom((short) 10, (short) 0, (short) 99, (short) 99, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Sq4TravBlock, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 20, (short) 0, (short) 99, (short) 99, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) TravBlock, (short) (trigo.RND() % 128));
        chaosObjects.PutRandom((short) 30, (short) 0, (short) 99, (short) 79, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Fact1Block, (short) (trigo.RND() % 256));
        chaosObjects.PutRandom((short) 30, (short) 0, (short) 79, (short) 99, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Fact2Block, (short) (trigo.RND() % 256));
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 99, (short) 59, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Fact3Block, (short) (trigo.RND() % 256));
        AddOptions((short) 30, (short) 1, (short) 98, (short) 98, 5, 5, 3, 4, 1, 0, 0);
    }

    public void Labyrinth() {
        // VAR
        short i = 0;
        short val = 0;

        chaosObjects.Cadre((short) 120, (short) 101);
        flipVert = false;
        rotate = false;
        chaosObjects.Fill((short) 1, (short) 1, (short) 100, (short) 100, (short) ChaosGraphics.NbBackground);
        chaosObjects.Fill((short) 101, (short) 0, (short) 119, (short) 100, (short) BackNone);
        chaosGenerator.DrawLabyrinth((short) 50);
        chaosObjects.FillRandom((short) 0, (short) 0, (short) 100, (short) 100, (short) Granit1, (short) Granit2, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), Runtime.proc(chaosObjects::Rnd, "ChaosObjects.Rnd"));
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Back2x2;
        chaosObjects.FillCond((short) 1, (short) 1, (short) 99, (short) 99, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), val);
        chaosObjects.Fill((short) 1, (short) 1, (short) 2, (short) 2, (short) Back2x2);
        chaosObjects.Put((short) 100, (short) 99, (short) BackNone);
        chaosGenerator.RemIsolated((short) 2, (short) 2, (short) 98, (short) 98, (short) 2, (short) 2, (short) BackSmall);
        chaosObjects.Fill((short) 101, (short) 30, (short) 119, (short) 30, (short) SimpleBlock);
        chaosObjects.Fill((short) 106, (short) 33, (short) 111, (short) 33, (short) SimpleBlock);
        chaosObjects.Fill((short) 114, (short) 33, (short) 119, (short) 33, (short) SimpleBlock);
        chaosObjects.Fill((short) 109, (short) 35, (short) 116, (short) 35, (short) SimpleBlock);
        chaosObjects.Fill((short) 101, (short) 100, (short) 119, (short) 100, (short) SimpleBlock);
        chaosObjects.Fill((short) 102, (short) 50, (short) 106, (short) 50, (short) SimpleBlock);
        chaosObjects.Fill((short) 101, (short) 52, (short) 105, (short) 69, (short) SimpleBlock);
        chaosObjects.Fill((short) 101, (short) 90, (short) 105, (short) 90, (short) SimpleBlock);
        chaosObjects.Fill((short) 103, (short) 52, (short) 104, (short) 69, (short) FalseBlock);
        chaosObjects.Put((short) 102, (short) 90, (short) BackNone);
        chaosObjects.Fill((short) 119, (short) 30, (short) 119, (short) 100, (short) SimpleBlock);
        chaosObjects.Fill((short) 106, (short) 34, (short) 106, (short) 99, (short) SimpleBlock);
        chaosObjects.Fill((short) 102, (short) 40, (short) 102, (short) 50, (short) SimpleBlock);
        chaosObjects.Fill((short) 104, (short) 30, (short) 104, (short) 40, (short) SimpleBlock);
        chaosObjects.Put((short) 119, (short) 30, (short) FalseBlock);
        chaosObjects.PutPlayer((short) 1, (short) 1);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mDoor, 0, (short) 102, (short) 90);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mDoor, 0, (short) 103, (short) 64);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mDoor, 0, (short) 104, (short) 64);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mDoor, 0, (short) 103, (short) 40);
        chaosObjects.PutExtraLife((short) 99, (short) 1);
        chaosObjects.PutExtraLife((short) 1, (short) 99);
        chaosObjects.PutExtraPower((short) 0, (short) 101, (short) 70);
        chaosObjects.PutBlockBonus(ChaosBonus.tbDBSpeed, (short) 99, (short) 99);
        chaosObjects.PutBlockBonus(ChaosBonus.tbMagnet, (short) 105, (short) 70);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mReactor, 0, (short) 102, (short) 98);
        chaosObjects.PutBlockBonus(ChaosBonus.tbMagnet, (short) 105, (short) 4);
        chaosObjects.PutBlockBonus(ChaosBonus.tbDifficulty, (short) 110, (short) 15);
        chaosObjects.PutExit((short) 105, (short) 99);
        chaosObjects.PutExit((short) 110, (short) 15);
        chaosObjects.PutExit((short) 2, (short) 1);
        chaosObjects.PutExit((short) 118, (short) 99);
        for (i = ChaosBonus.tbDBSpeed; i <= ChaosBonus.tbNoMissile; i++) {
            chaosObjects.PutBlockBonus(i, (short) 105, (short) (49 - i));
        }
        chaosObjects.Rect((short) 1, (short) 1, (short) 99, (short) 99);
        chaosObjects.PutTBonus(ChaosBonus.tbDBSpeed, 5);
        chaosObjects.PutTBonus(ChaosBonus.tbSGSpeed, 1);
        chaosObjects.PutMagnet(5);
        chaosObjects.PutInvinsibility(3);
        chaosObjects.PutSleeper(2);
        chaosObjects.PutBullet(10);
        chaosObjects.PutHospital(15);
        chaosObjects.PutMoney(EnumSet.of(Moneys.st), 50);
        chaosObjects.Rect((short) 107, (short) 50, (short) 119, (short) 99);
        chaosObjects.PutAlien2(ChaosCreator.cCircle, 200, 10);
        AddOptions((short) 1, (short) 1, (short) 99, (short) 99, 5, 5, 4, 8, 45, 3, 4);
    }

    private void Init() {
        // VAR
        int x = 0;

        for (x = 0; x <= 15; x++) {
            fillCount[x] = 0;
            fillRndAdd[x] = 0;
        }
    }


    // Support

    private static Chaos1Zone instance;

    public static Chaos1Zone instance() {
        if (instance == null)
            new Chaos1Zone(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        Init();
    }

    public void close() {
    }

}
