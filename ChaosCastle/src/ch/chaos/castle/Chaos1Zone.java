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
    public int pLife2;
    public int pLife3;
    public int pLife4;
    public Runtime.RangeSet fillTypes = new Runtime.RangeSet(Memory.SET16_r);
    public int[] fillCount = new int[16];
    public int[] fillRndAdd = new int[16];
    public Anims[] fKind = Runtime.initArray(new Anims[4]);
    public int[] fSubKind = new int[4];
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

    public int getPLife2() {
        return this.pLife2;
    }

    public void setPLife2(int pLife2) {
        this.pLife2 = pLife2;
    }

    public int getPLife3() {
        return this.pLife3;
    }

    public void setPLife3(int pLife3) {
        this.pLife3 = pLife3;
    }

    public int getPLife4() {
        return this.pLife4;
    }

    public void setPLife4(int pLife4) {
        this.pLife4 = pLife4;
    }

    public Runtime.RangeSet getFillTypes() {
        return this.fillTypes;
    }

    public void setFillTypes(Runtime.RangeSet fillTypes) {
        this.fillTypes = fillTypes;
    }

    public int[] getFillCount() {
        return this.fillCount;
    }

    public void setFillCount(int[] fillCount) {
        this.fillCount = fillCount;
    }

    public int[] getFillRndAdd() {
        return this.fillRndAdd;
    }

    public void setFillRndAdd(int[] fillRndAdd) {
        this.fillRndAdd = fillRndAdd;
    }

    public Anims[] getFKind() {
        return this.fKind;
    }

    public void setFKind(Anims[] fKind) {
        this.fKind = fKind;
    }

    public int[] getFSubKind() {
        return this.fSubKind;
    }

    public void setFSubKind(int[] fSubKind) {
        this.fSubKind = fSubKind;
    }

    public int[] getAStat() {
        return this.aStat;
    }

    public void setAStat(int[] aStat) {
        this.aStat = aStat;
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

    public void RectFill(int sx, int sy, int ex, int ey) {
        // VAR
        int which = 0;
        int add = 0;
        int s = 0;
        int count = 0;
        int c = 0;

        do {
            which = trigo.RND() % 14;
        } while (!fillTypes.contains(which));
        if (fillTypes.contains(fCrunchX)) {
            sx -= 2;
            ex += 2;
            sy++;
            ey--;
            chaosObjects.Fill(sx, sy, ex, ey, chaosObjects.Get(sx + 2, sy) % ChaosGraphics.NbBackground);
        }
        if (fillTypes.contains(fCrunchY)) {
            sy -= 2;
            ey -= 2;
            sx++;
            sy++;
            chaosObjects.Fill(sx, sy, ex, ey, chaosObjects.Get(sx, sy + 2) % ChaosGraphics.NbBackground);
        }
        chaosObjects.Rect(sx, sy, ex, ey);
        count = fillCount[which];
        add = fillRndAdd[which];
        if (add != 0)
            count += trigo.RND() % (add + 1);
        switch (which) {
            case fKmk -> {
                count = count / 4;
                chaosObjects.PutKamikaze(0, count);
                chaosObjects.PutKamikaze(1, count);
                chaosObjects.PutKamikaze(2, count);
                chaosObjects.PutKamikaze(3, count);
            }
            case fPic -> {
                count = count / 2;
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
                c = trigo.RND() % count;
                chaosObjects.PutDeltaObjs(Anims.MACHINE, ChaosMachine.mCannon1, 0, -1, 0, c);
                c = count - c;
                chaosObjects.PutDeltaObjs(Anims.MACHINE, ChaosMachine.mCannon1, 1, 1, 0, c);
            }
            case fCannon2 -> {
                if (chaosObjects.Get(sx, sy - 1) >= ChaosGraphics.NbBackground) {
                    c = 0;
                    s = -1;
                } else {
                    c = 1;
                    s = 1;
                }
                chaosObjects.PutDeltaObjs(Anims.MACHINE, ChaosMachine.mCannon2, c, 0, s, count);
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

    private void AddOptions_AddOption(Anims kind, int sKind, int cnt, int min) {
        if ((chaosBase.difficulty >= min) && (cnt > 0))
            chaosObjects.PutRandomObjs(kind, sKind, pLife3 + chaosBase.difficulty * 2, cnt);
    }

    public void AddOptions(int sx, int sy, int ex, int ey, int nbGrid, int nbBumper, int nbChief, int nbGhost, int nbPopup, int nbBig, int nbSquare) {
        chaosObjects.Rect(sx, sy, ex, ey);
        AddOptions_AddOption(Anims.ALIEN2, ChaosCreator.cGrid, nbGrid, 2);
        AddOptions_AddOption(Anims.ALIEN1, ChaosAlien.aBumper, nbBumper, 3);
        AddOptions_AddOption(Anims.ALIEN2, ChaosCreator.cChief, nbChief, 4);
        AddOptions_AddOption(Anims.ALIEN2, ChaosCreator.cGhost, nbGhost, 5);
        AddOptions_AddOption(Anims.ALIEN2, ChaosCreator.cPopUp, nbPopup, 6);
        AddOptions_AddOption(Anims.ALIEN1, ChaosAlien.aBig, nbBig, 7);
        AddOptions_AddOption(Anims.ALIEN1, ChaosAlien.aSquare, nbSquare, 8);
    }

    /* Level 1 */
    private void DrawVertRocs(int sx, int sy, int ey, int val) {
        // VAR
        int rx = 0;
        int re = 0;
        int rh = 0;
        int x = 0;
        int y = 0;
        int oy = 0;
        int bx = 0;
        int ex = 0;
        int sh = 0;
        int eh = 0;

        y = sy;
        while (true) {
            oy = y;
            y += trigo.RND() % 5 + 4;
            if (y > ey)
                break;
            RectFill(sx, oy, sx + 13, y - 1);
            rx = chaosObjects.Rnd(8) + 1;
            re = chaosObjects.Rnd(9 - rx) + 4;
            rh = chaosObjects.Rnd(8) + 1;
            sh = chaosObjects.Rnd(rh) + 1;
            eh = chaosObjects.Rnd(rh) + 1;
            bx = chaosObjects.Rnd(re) + rx;
            ex = chaosObjects.Rnd(re) + rx;
            re += rx;
            while ((y <= ey) && (rh > 0)) {
                for (x = rx; x <= re; x++) {
                    if ((rh < sh) || (rh > eh) || (x < bx) || (x > ex))
                        chaosObjects.Put(sx + x, y, val);
                }
                y++;
                rh--;
            }
        }
    }

    public void Entry() {
        // VAR
        int val = 0;
        int cnt = 0;

        fillTypes = new Runtime.RangeSet(Memory.SET16_r).with(fMoneyS, fAlienColor, fAlienFour);
        fillCount[fMoneyS] = 0;
        fillRndAdd[fMoneyS] = 0;
        fillCount[fAlienColor] = 1;
        fillRndAdd[fAlienColor] = 6;
        fillCount[fAlienFour] = 1;
        fillRndAdd[fAlienFour] = 1;
        chaosObjects.Cadre(16, 120);
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Back8x8;
        chaosObjects.Fill(1, 9, 14, 118, val);
        if (trigo.RND() % 8 == 0)
            val = BigBlock;
        else
            val = EmptyBlock;
        DrawVertRocs(1, 11, 117, val);
        chaosObjects.FillCond(1, 9, 3, 118, chaosObjects.OnlyBackground_ref, Back4x4);
        chaosObjects.FillCond(12, 9, 14, 118, chaosObjects.OnlyBackground_ref, Back4x4);
        cnt = trigo.RND() % 4 + 4;
        while (cnt > 0) {
            val = trigo.RND() % 4;
            switch (val) {
                case 0 -> val = Sq1Block;
                case 1 -> val = Sq4Block;
                case 2 -> val = EmptyBlock;
                case 3 -> val = SimpleBlock;
                default -> throw new RuntimeException("Unhandled CASE value " + val);
            }
            chaosGenerator.PutCross(1, 9, 14, 117, val);
            cnt--;
        }
        chaosObjects.PutRandom(2, 12, 13, 116, chaosObjects.OnlyWall_ref, Sq1Block, trigo.RND() % 64);
        chaosObjects.PutRandom(2, 12, 13, 116, chaosObjects.OnlyWall_ref, Sq4Block, trigo.RND() % 32);
        chaosObjects.PutRandom(2, 2, 13, 116, chaosObjects.OnlyWall_ref, SimpleBlock, trigo.RND() % 4);
        if ((chaosBase.difficulty >= 3) && (trigo.RND() % 2 == 0)) {
            chaosObjects.FillCond(1, 9, 14, 118, chaosObjects.OnlyBackground_ref, Ice);
            chaosBase.snow = true;
        }
        chaosObjects.Fill(1, 1, 14, 8, EmptyBlock);
        chaosObjects.Fill(1, 1, 3, 3, BackSmall);
        chaosObjects.Fill(9, 1, 14, 3, Back2x2);
        chaosObjects.Fill(1, 4, 1, 6, FalseEmpty);
        chaosObjects.Fill(2, 6, 8, 6, FalseEmpty);
        chaosObjects.Fill(9, 4, 9, 7, FalseEmpty);
        chaosObjects.Fill(14, 4, 14, 8, ChaosGraphics.NbBackground - 1);
        if (chaosBase.powerCountDown > 10)
            chaosObjects.FillObj(Anims.SMARTBONUS, ChaosSmartBonus.sbExtraPower, 0, 1, 1, 2, 1, false);
        chaosObjects.FillObj(Anims.SMARTBONUS, ChaosSmartBonus.sbExtraLife, 0, 9, 1, 12, 1, false);
        chaosObjects.FillObj(Anims.BONUS, ChaosBonus.Money, Moneys.st.ordinal(), 9, 2, 14, 3, true);
        chaosObjects.PutExtraPower(7, 2, 9);
        chaosObjects.PutExtraPower(7, 1, 10);
        chaosObjects.PutExtraPower(7, 1, 118);
        chaosObjects.Rect(1, 9, 14, 118);
        chaosObjects.PutIsolated(0, chaosBase.difficulty * 4 - 4, 12, 12, SimpleBlock);
        AddOptions(1, 17, 15, 117, 0, 0, 1, 0, 0, 5, 1);
        chaosObjects.PutRandomObjs(Anims.BONUS, ChaosBonus.Money, Moneys.st.ordinal(), 8 + trigo.RND() % 16);
        chaosObjects.Rect(1, 50, 14, 70);
        chaosObjects.PutHospital(1);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.Rect(1, 15, 14, 118);
        chaosObjects.PutBullet(5);
        chaosObjects.PutExit(14, 118);
        chaosObjects.PutPlayer(1, 9);
    }

    /* Level 2 */
    public void Groove() {
        // VAR
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int z = 0;
        int dx = 0;
        int dy = 0;
        Runtime.Ref<Integer> angle = new Runtime.Ref<>(0);
        int c = 0;
        int val = 0;

        chaosObjects.Cadre(100, 50);
        for (y.set(0); y.get() <= 49; y.inc()) {
            for (x.set(0); x.get() <= 99; x.inc()) {
                z = y.get() - 12 + chaosObjects.Rnd(24);
                if (z <= 24)
                    val = Fade3;
                else if (z <= 40)
                    val = Fade2;
                else
                    val = Fade1;
                chaosObjects.Put(x.get(), y.get(), val);
            }
        }
        chaosGenerator.Excavate(1, 98, 1, 24, 4, 10, 8, 7, 4, 2, 1, 1, 6);
        chaosGenerator.Excavate(1, 98, 26, 39, 3, 7, 2, 5, 2, 1, 2, 1, 3);
        chaosGenerator.Excavate(1, 98, 41, 48, 2, 2, 5, 2, 6, 1, 0, 5, 10);
        chaosObjects.Fill(98, 1, 98, 37, BackNone);
        chaosObjects.Fill(1, 26, 1, 47, BackNone);
        chaosObjects.Fill(98, 42, 98, 48, BackNone);
        for (y.set(0); y.get() <= 49; y.inc()) {
            for (x.set(0); x.get() <= 99; x.inc()) {
                z = y.get() - 4 + chaosObjects.Rnd(8);
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
        chaosObjects.Rect(2, 2, 99, 49);
        for (c = 1; c <= 9; c++) {
            if (chaosGenerator.FindIsolatedRect(2, 2, 99, 49, 2, 10, angle, x, y, true)) {
                chaosGenerator.MakeLink(x.get(), y.get(), 0, angle.get(), Round4);
                if (trigo.COS(angle.get()) == 0)
                    dx = 1;
                else
                    dx = 0;
                dy = 1 - dx;
                for (z = -2; z <= 2; z++) {
                    chaosObjects.Put(x.get() + z * dx, y.get() + z * dy, Round4);
                }
                chaosObjects.Rect(x.get() - 2, y.get() - 2, x.get() + 2, y.get() + 2);
                chaosObjects.PutBullet(1);
                if (c <= 4)
                    chaosObjects.PutBullet(1);
                else
                    chaosObjects.PutHospital(1);
            }
        }
        chaosObjects.PutRandom(0, 0, 99, 49, chaosObjects.OnlyWall_ref, F9x9, trigo.RND() % (chaosBase.difficulty * 4 + 1));
        chaosObjects.PutRandom(0, 0, 99, 49, chaosObjects.OnlyWall_ref, FRound, trigo.RND() % 32);
        chaosObjects.PutRandom(0, 0, 99, 49, chaosObjects.OnlyWall_ref, FStar, trigo.RND() % (64 + chaosBase.difficulty * 6));
        chaosObjects.Rect(51, 1, 98, 48);
        chaosObjects.PutIsolated(2, chaosBase.difficulty + 2, 48, 48, SimpleBlock);
        chaosObjects.Rect(2, 2, 99, 49);
        chaosObjects.PutColor(pLife3, 60);
        chaosObjects.PutCartoon(2, 2, 10);
        chaosObjects.Rect(2, 2, 99, 40);
        chaosObjects.PutFour(pLife3, 30);
        chaosObjects.Rect(2, 20, 99, 49);
        chaosObjects.PutQuad(pLife3, 12 + chaosBase.difficulty);
        if (chaosBase.stages >= 5)
            chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        if (chaosBase.stages == 0)
            chaosObjects.PutBlockBonus(ChaosBonus.tbNoMissile, 98, 2);
        chaosObjects.PutExtraPower(3, 1, 26);
        chaosObjects.PutExtraPower(3, 98, 1);
        chaosObjects.FillObj(Anims.DEADOBJ, ChaosDObj.doCartoon, 0, 98, 3, 98, 10, false);
        chaosObjects.PutBlockBonus(ChaosBonus.tbInvinsibility, 98, 32);
        AddOptions(2, 2, 99, 49, 4, 8, 2, 3, 10, 6, 4);
        chaosObjects.PutExit(98, 48);
        chaosObjects.PutPlayer(1, 12);
    }

    /* Level 3 */
    public void Garden() {
        chaosObjects.Cadre(61, 61);
        chaosGenerator.DrawPacman(10 - chaosBase.difficulty, 6, 6, 6, 6, 54, 54);
        chaosObjects.FillRandom(0, 0, 60, 60, Forest1, Forest7, chaosObjects.OnlyWall_ref, chaosObjects.Rnd_ref);
        chaosObjects.FillCond(0, 0, 60, 60, chaosObjects.OnlyBackground_ref, Ground);
        chaosObjects.Rect(1, 1, 59, 59);
        chaosObjects.PutIsolated(0, 2, 10, 25, Leaf1);
        chaosObjects.Rect(1, 1, 59, 59);
        chaosObjects.PutIsolated(0, 8, 10, 25, Leaf2);
        chaosObjects.Rect(1, 1, 59, 59);
        chaosObjects.PutIsolated(0, 8, 10, 25, Leaf3);
        chaosObjects.Rect(1, 1, 59, 59);
        chaosObjects.PutIsolated(0, 7, 10, 25, SimpleBlock);
        if ((chaosBase.difficulty >= 3) && (trigo.RND() % 2 == 0))
            chaosObjects.PutRandom(0, 0, 60, 60, chaosObjects.OnlyBackground_ref, Ground2, 100);
        chaosObjects.FillObj(Anims.ALIEN1, ChaosAlien.aCartoon, 0, 29, 29, 31, 31, false);
        chaosObjects.PutExtraLife(1, 59);
        chaosObjects.PutExtraLife(59, 1);
        chaosObjects.Rect(30, 30, 40, 40);
        chaosObjects.PutInvinsibility(1);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.PutGridObjs(Anims.BONUS, ChaosBonus.TimedBonus, ChaosBonus.tbHospital, 20, 20, 20, 20, 1, 1);
        chaosObjects.Rect(2, 2, 58, 58);
        if (chaosBase.powerCountDown >= 6)
            chaosObjects.PutRandomObjs(Anims.SMARTBONUS, ChaosSmartBonus.sbExtraPower, 0, 1);
        chaosObjects.PutHospital(1);
        chaosObjects.PutCartoon(0, 2, 20);
        chaosObjects.PutNest(0, 40 + chaosBase.difficulty);
        AddOptions(2, 2, 59, 59, 4, 10, 2, 0, 1, 3, 0);
        chaosObjects.PutExit(57, 57);
        chaosObjects.PutPlayer(1, 1);
    }

    /* Level 4 */
    public void Lake() {
        // VAR
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int sz = 0;
        int d = 0;
        Runtime.Ref<Integer> angle = new Runtime.Ref<>(0);
        int c = 0;
        int val = 0;

        chaosBase.water = (chaosBase.difficulty < 7) || (trigo.RND() % 4 != 0);
        chaosObjects.Cadre(124, 124);
        chaosObjects.Fill(0, 0, 123, 123, SimpleBlock);
        if ((trigo.RND() % 8 == 0) && (chaosBase.difficulty > 2))
            val = Ground2;
        else if (!chaosGraphics.dualpf)
            val = Ground;
        else
            val = BackNone;
        chaosGenerator.TripleLoop(val);
        chaosObjects.PutRandom(0, 0, 123, 123, chaosObjects.OnlyWall_ref, Leaf1, trigo.RND() % 256);
        chaosObjects.PutRandom(0, 0, 123, 123, chaosObjects.OnlyWall_ref, Leaf2, trigo.RND() % 256);
        chaosObjects.PutRandom(0, 0, 123, 123, chaosObjects.OnlyWall_ref, Leaf3, trigo.RND() % 256);
        chaosObjects.PutRandom(0, 0, 123, 123, chaosObjects.OnlyWall_ref, Leaf4, trigo.RND() % 256);
        chaosObjects.PutRandom(0, 0, 123, 123, chaosObjects.OnlyWall_ref, EmptyBlock, trigo.RND() % 64);
        chaosObjects.PutRandom(0, 0, 123, 123, chaosObjects.OnlyWall_ref, SimpleBlock, trigo.RND() % 64);
        chaosObjects.PutRandom(0, 0, 123, 123, chaosObjects.OnlyWall_ref, FStar, trigo.RND() % (chaosBase.difficulty * 20));
        chaosObjects.PutRandom(0, 0, 123, 123, chaosObjects.OnlyWall_ref, BigBlock, trigo.RND() % (chaosBase.difficulty * 20));
        chaosObjects.PutPlayer(104, 69);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHelp, 105, 69);
        chaosObjects.PutExit(89, 53);
        chaosObjects.PutBubbleMaker((chaosBase.difficulty < 6 ? 1 : 0), 100, 62);
        chaosObjects.PutBubbleMaker((chaosBase.difficulty < 6 ? 1 : 0), 43, 29);
        chaosObjects.PutBubbleMaker((chaosBase.difficulty < 6 ? 1 : 0), 43, 95);
        if (chaosBase.difficulty < 4) {
            chaosObjects.PutBubbleMaker(0, 90, 112);
            chaosObjects.PutBubbleMaker(0, 90, 12);
            chaosObjects.PutBubbleMaker(0, 5, 62);
        }
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, 90, 113);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, 90, 11);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, 5, 63);
        chaosObjects.PutBlockBonus(ChaosBonus.tbDBSpeed, 43, 62);
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
                sz = trigo.RND() % 5 + 1;
                d = sz * 2 + 1;
                if (chaosGenerator.FindIsolatedRect(0, 0, 123, 123, sz, 30, angle, px, py, true)) {
                    chaosObjects.Fill(px.get() - sz, py.get() - sz, px.get() + sz, py.get() + sz, Balls);
                    chaosGenerator.FillEllipse(px.get() - sz, py.get() - sz, d, d, Tar);
                    if ((c == 10) && (chaosBase.stages == 0))
                        chaosObjects.PutBlockBonus(ChaosBonus.tbNoMissile, px.get(), py.get());
                    RectFill(px.get() - sz, py.get() - sz, px.get() + sz, py.get() + sz);
                    chaosGenerator.MakeLink(px.get(), py.get(), sz, angle.get(), FalseBlock);
                }
            }
        }
        chaosObjects.Rect(50, 100, 120, 120);
        chaosObjects.PutDeadObj(ChaosDObj.doWindMaker, 0, 3);
        chaosObjects.PutChaosObjs(Anims.DEADOBJ, ChaosDObj.doSand, 0, 2000, 32, 2700, 400, 20);
        chaosObjects.Rect(50, 1, 120, 62);
        chaosObjects.PutQuad(pLife3, 4 + chaosBase.difficulty);
        chaosObjects.Rect(1, 1, 50, 120);
        chaosObjects.PutABox(0, 4 + chaosBase.difficulty / 2);
        chaosObjects.Rect(1, 1, 70, 120);
        chaosObjects.PutColor(pLife3, 20);
        chaosObjects.Rect(30, 80, 60, 110);
        chaosObjects.PutRandomObjs(Anims.BONUS, ChaosBonus.BonusLevel, ChaosBonus.tbBonusLevel, 1);
        chaosObjects.Rect(50, 40, 100, 84);
        chaosObjects.PutCannon3(5);
        chaosObjects.Rect(1, 1, 122, 122);
        chaosObjects.PutBullet(10);
        chaosObjects.PutAlien1(ChaosAlien.aBigDrawer, 0, 1);
        chaosObjects.PutNest(1, 20 + chaosBase.difficulty / 3);
        chaosObjects.PutFour(pLife3, 10);
        chaosObjects.PutCartoon(2, 2, 15);
        chaosObjects.PutChaosSterling(20 - chaosBase.difficulty);
        if (chaosBase.difficulty >= 2)
            chaosObjects.PutTrefle(pLife2, 5);
        AddOptions(1, 1, 122, 122, 2, 0, 0, 0, 12, 0, 0);
        AddOptions(1, 60, 122, 122, 0, 0, 0, 0, 0, chaosBase.difficulty, 0);
        AddOptions(1, 1, 122, 60, 0, 0, 0, 0, 0, 0, chaosBase.difficulty);
        if (chaosBase.powerCountDown > 3) {
            chaosObjects.FillCond(90, 12, 90, 28, chaosObjects.OnlyWall_ref, 21);
            chaosObjects.PutExtraPower(3, 90, 28);
            chaosObjects.FillObj(Anims.DEADOBJ, ChaosDObj.doCartoon, 0, 90, 24, 90, 27, false);
        }
    }

    /* Level 5 */
    public void Site() {
        // VAR
        Runtime.Ref<Integer> a = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int sz = 0;
        int d = 0;
        int cnt = 0;
        int val = 0;
        int val2 = 0;

        chaosObjects.Clear(100, 100);
        chaosObjects.Fill(0, 0, 99, 99, BackNone);
        chaosObjects.Fill(40, 21, 40, 60, SimpleBlock);
        chaosObjects.Fill(60, 40, 60, 79, SimpleBlock);
        cnt = trigo.RND() % 15 + 7;
        while (cnt > 0) {
            val = trigo.RND() % 5;
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
            chaosGenerator.PutCross(20, 20, 60, 60, val);
            if (cnt < 8) {
                sz = trigo.RND() % 3 + 1;
                if (chaosGenerator.FindIsolatedRect(22, 22, 58, 58, sz, 0, a, x, y, false)) {
                    d = sz * 2 + 1;
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, val);
                    sz--;
                    d -= 2;
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, val2);
                }
            }
            cnt--;
        }
        chaosObjects.Fill(20, 20, 79, 20, SimpleBlock);
        chaosObjects.Fill(20, 79, 79, 79, SimpleBlock);
        chaosObjects.Fill(20, 21, 20, 78, SimpleBlock);
        chaosObjects.Fill(79, 21, 79, 77, SimpleBlock);
        chaosObjects.Put(79, 78, 21);
        if (!chaosGraphics.dualpf)
            chaosObjects.FillRandom(0, 0, 99, 99, 0, 7, chaosObjects.OnlyBackground_ref, chaosObjects.ExpRandom_ref);
        chaosObjects.FillRandom(20, 20, 79, 79, 12, 13, chaosObjects.OnlyBackground_ref, chaosObjects.ExpRandom_ref);
        chaosObjects.FillCond(45, 45, 54, 54, chaosObjects.OnlyBackground_ref, 20);
        chaosObjects.PutPlayer(78, 78);
        chaosObjects.PutExit(21, 21);
        chaosObjects.PutExtraLife(21, 78);
        chaosObjects.Rect(21, 21, 78, 78);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.PutAlien1(ChaosAlien.aSmallDrawer, 0, 1);
        chaosObjects.PutFour(pLife3, 8);
        chaosObjects.Rect(21, 21, 39, 78);
        chaosObjects.PutCreatorR(10);
        chaosObjects.Rect(30, 21, 60, 78);
        chaosObjects.PutCreatorC(30);
        chaosObjects.Rect(60, 21, 78, 78);
        chaosObjects.PutAlien1(ChaosAlien.aStar, pLife3, 8);
        chaosObjects.Rect(21, 40, 60, 78);
        chaosObjects.PutTri(pLife3, 20);
        chaosObjects.Rect(21, 21, 60, 60);
        chaosObjects.PutInvinsibility(1);
        chaosObjects.PutSleeper(1);
        if ((chaosBase.difficulty >= 3) || !registration.registered) {
            chaosObjects.PutMagnet(1);
            chaosObjects.PutTurret(8);
            chaosObjects.PutMagnetR(3, 6);
            chaosObjects.PutMagnetA(1, 2);
        }
        chaosObjects.PutFreeFire(1);
        chaosObjects.Rect(21, 21, 78, 78);
        chaosObjects.PutBullet(30);
        chaosObjects.Rect(0, 0, 99, 99);
        chaosObjects.PutCannon3(7);
        chaosObjects.FillObj(Anims.BONUS, ChaosBonus.Money, Moneys.m10.ordinal(), 99, 0, 99, 0, true);
        chaosObjects.PutBlockBonus(ChaosBonus.tbBullet, 0, 99);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, ChaosDObj.doMagnetR, 3, 97, 97);
        chaosObjects.PutExtraPower(5, 78, 21);
        chaosObjects.PutBlockBonus(ChaosBonus.tbBomb, 0, 0);
        chaosObjects.Fill(49, 49, 51, 51, 20);
        chaosObjects.FillObj(Anims.ALIEN1, ChaosAlien.aCartoon, 0, 49, 49, 51, 51, false);
        chaosObjects.Rect(30, 30, 59, 59);
        chaosObjects.PutAlien1(ChaosAlien.aHospital, 20 + chaosBase.difficulty, 3);
        AddOptions(21, 21, 60, 78, 0, 0, 5, 0, 8, 0, 0);
    }

    /* Level 6 */
    public void GhostCastle() {
        // VAR
        int val = 0;

        flipVert = false;
        rotate = false;
        chaosObjects.Cadre(25, 70);
        chaosObjects.Fill(1, 20, 22, 29, Bricks);
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Round4;
        chaosObjects.Fill(1, 20, 2, 21, val);
        chaosObjects.Fill(16, 23, 22, 23, val);
        chaosObjects.Fill(20, 31, 20, 69, Bricks);
        chaosObjects.FillCond(0, 0, 24, 69, chaosObjects.OnlyBackground_ref, val);
        chaosGenerator.GCastle(1, 31, 19, 67, Bricks, 8);
        chaosObjects.FillCond(0, 0, 24, 69, chaosObjects.OnlyWall_ref, Bricks);
        chaosObjects.PutRandom(21, 31, 23, 69, chaosObjects.OnlyBackground_ref, BackBig, trigo.RND() % 16);
        chaosObjects.PutRandom(21, 31, 23, 69, chaosObjects.OnlyBackground_ref, BackSmall, trigo.RND() % 16);
        chaosObjects.PutRandom(21, 31, 23, 69, chaosObjects.OnlyBackground_ref, Back8x8, trigo.RND() % 16);
        chaosObjects.PutRandom(21, 31, 23, 69, chaosObjects.OnlyBackground_ref, Back4x4, trigo.RND() % 16);
        chaosObjects.PutRandom(2, 3, 19, 55, chaosObjects.OnlyWall_ref, IceBlock, trigo.RND() % 4);
        chaosObjects.PutRandom(2, 3, 19, 55, chaosObjects.OnlyWall_ref, SimpleBlock, trigo.RND() % 4);
        chaosObjects.PutRandom(2, 3, 19, 55, chaosObjects.OnlyWall_ref, EmptyBlock, trigo.RND() % 4);
        chaosObjects.PutExtraLife(23, 1);
        chaosObjects.PutExtraPower(1, 2, 21);
        chaosObjects.PutBlockBonus(ChaosBonus.tbDBSpeed, 1, 21);
        chaosObjects.PutBlockBonus(ChaosBonus.tbMagnet, 16, 23);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, ChaosDObj.doCartoon, 0, 22, 23);
        chaosObjects.PutBlockObj(Anims.MACHINE, ChaosMachine.mDoor, 0, 23, 21);
        chaosObjects.PutBlockObj(Anims.MACHINE, ChaosMachine.mReactor, 0, 23, 68);
        chaosObjects.PutExit(21, 68);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHelp, 22, 68);
        chaosObjects.Rect(1, 31, 19, 67);
        chaosObjects.PutBullet(8);
        chaosObjects.PutPlayer(1, 68);
        if (chaosBase.specialStage >= 4)
            chaosObjects.PutBlockObj(Anims.ALIEN3, ChaosBoss.bBrotherAlien, 0, 17, 3);
        chaosObjects.FillObj(Anims.BONUS, ChaosBonus.Money, Moneys.st.ordinal(), 11, 8, 13, 10, true);
        chaosObjects.Rect(1, 21, 19, 60);
        chaosObjects.PutRandomObjs(Anims.ALIEN2, ChaosCreator.cGhost, pLife3, 10);
        chaosObjects.Rect(21, 30, 23, 67);
        chaosObjects.PutCartoon(0, 2, 15);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doFireMaker, 0, 0, 2, 4);
        if (chaosBase.difficulty > 1) {
            chaosObjects.Rect(1, 21, 1, 66);
            chaosObjects.PutMachine(ChaosMachine.mCannon1, 0, 0, 2);
            chaosObjects.Rect(19, 21, 19, 66);
            chaosObjects.PutMachine(ChaosMachine.mCannon2, 1, 1, 2);
            chaosObjects.Rect(1, 1, 24, 1);
            chaosObjects.PutMachine(ChaosMachine.mCannon2, 0, 0, 4);
        }
        AddOptions(1, 1, 23, 65, 1, 1, 1, 1, 10, 1, 4);
    }

    /* Level 7 */
    public void Machinery() {
        // VAR
        int x = 0;
        int y = 0;
        int w = 0;
        int dy = 0;

        chaosObjects.Cadre(80, 40);
        rotate = false;
        chaosObjects.Fill(11, 1, 78, 39, BarLight);
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
        w = trigo.RND() % 4 + 3;
        dy = -3;
        while (x <= 78) {
            if (trigo.RND() % 4 == 0)
                fillTypes.incl(fCrunchX);
            else
                fillTypes.excl(fCrunchX);
            if (x + w >= 73) {
                w = 79 - x;
                fillTypes.excl(fCrunchX);
            }
            chaosObjects.Fill(x, y, x + w - 1, y + 5, 0);
            if (dy < 0)
                RectFill(x, y, x + w - 1, y - dy - 1);
            else
                RectFill(x, y + 6 - dy, x + w - 1, y + 5);
            x += w;
            dy = trigo.RND() % 3 + 3;
            if (trigo.RND() % 2 == 0)
                dy = -dy;
            if (y < 6)
                dy = (int) Math.abs(dy);
            else if (y > 10)
                dy = (int) -Math.abs(dy);
            y += dy;
            w = trigo.RND() % 4 + 3;
        }
        chaosObjects.Fill(78, y, 78, 35, 0);
        chaosGenerator.Cave(78, 21, 11, 38, 30, 35, -1);
        chaosObjects.FillCond(0, 0, 79, 39, chaosObjects.OnlyWall_ref, BarLight);
        chaosObjects.FillCond(0, 0, 79, 39, chaosObjects.OnlyBackground_ref, Light);
        chaosObjects.PutPlayer(1, 38);
        chaosObjects.Rect(1, 4, 1, 36);
        chaosObjects.PutDeadObj(ChaosDObj.doMirror, 0, 12);
        chaosObjects.Rect(10, 4, 10, 36);
        chaosObjects.PutDeadObj(ChaosDObj.doMirror, 0, 12);
        chaosObjects.Rect(2, 4, 9, 39);
        chaosObjects.PutMachine(ChaosMachine.mCannon1, 0, 1, 20);
        chaosObjects.Rect(2, 1, 9, 3);
        chaosObjects.PutMachine(ChaosMachine.mCannon2, 0, 0, 3);
        chaosObjects.Rect(11, 21, 78, 38);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doMirror, 1, 1, 1, 20);
        chaosObjects.Rect(11, 21, 78, 38);
        chaosObjects.PutTurret(15);
        chaosObjects.Rect(5, 1, 78, 38);
        chaosObjects.PutIsolatedObjs(Anims.MACHINE, ChaosMachine.mTraverse, 0, 1, 1, 15);
        chaosObjects.Rect(1, 1, 78, 35);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.PutMagnetA(1, 4);
        chaosObjects.PutMagnetR(3, 30);
        AddOptions(1, 1, 78, 38, 0, 0, 6, 0, 10, 0, 3);
    }

    /* Level 8 */
    public void IceRink() {
        // VAR
        int c = 0;
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> a = new Runtime.Ref<>(0);
        int dx = 0;
        int dy = 0;
        int sz = 0;
        int st = 0;
        int count = 0;

        chaosObjects.Clear(80, 80);
        chaosBase.snow = true;
        chaosObjects.Fill(0, 0, 79, 79, IceBlock);
        chaosObjects.PutRandom(0, 0, 79, 79, chaosObjects.OnlyWall_ref, BigBlock, 255);
        chaosObjects.Fill(15, 15, 64, 64, IceBlock);
        chaosObjects.Fill(19, 19, 60, 60, RGBBlock);
        chaosObjects.Fill(20, 20, 59, 59, Ice);
        chaosObjects.PutPlayer(59, 20);
        chaosObjects.PutExit(20, 59);
        chaosObjects.PutBlockObj(Anims.MACHINE, ChaosMachine.mTurret, 0, 40, 40);
        chaosObjects.PutBlockObj(Anims.MACHINE, ChaosMachine.mCannon3, 0, 40, 40);
        count = trigo.RND() % 128;
        if (count > 64)
            count = count * 2;
        chaosObjects.Rect(20, 20, 59, 59);
        chaosObjects.PutIsolated(count, count, 40, 40, IceBlock);
        count = count / 16;
        chaosObjects.PutIsolated(count, count, 20, 40, RGBBlock);
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
            x.set(chaosObjects.Rnd(40) + 20);
            y.set(chaosObjects.Rnd(40) + 20);
            if (trigo.RND() % 2 == 0) {
                dx = 1;
                dy = 0;
            } else {
                dx = 0;
                dy = 1;
            }
            if (trigo.RND() % 2 == 0) {
                dx = -dx;
                dy = -dy;
            }
            while ((x.get() != 19) && (x.get() != 60) && (y.get() != 19) && (y.get() != 60)) {
                x.inc(dx);
                y.inc(dy);
            }
            sz = trigo.RND() % 8 + 5;
            st = sz;
            do {
                chaosObjects.Put(x.get(), y.get(), Ice);
                if (sz == st)
                    chaosObjects.PutBlockObj(Anims.DEADOBJ, ChaosDObj.doCartoon, 0, x.get(), y.get());
                x.inc(dx);
                y.inc(dy);
                sz--;
            } while (sz != 0);
            sz = trigo.RND() % 3 + 3;
            chaosObjects.Fill(x.get() - sz, y.get() - sz, x.get() + sz, y.get() + sz, Ice);
            fillTypes = new Runtime.RangeSet(Memory.SET16_r).with(fAnims1, fAnims2);
            if (trigo.RND() % 4 == 0)
                fillTypes.incl(fCrunchX);
            if (trigo.RND() % 4 == 0)
                fillTypes.incl(fCrunchY);
            RectFill(x.get() - sz, y.get() - sz, x.get() + sz, y.get() + sz);
            count--;
        }
        for (c = 1; c <= chaosObjects.Rnd(4) + 6; c++) {
            sz = trigo.RND() % 5 + 2;
            if (chaosGenerator.FindIsolatedRect(0, 0, 79, 79, sz, 10, a, x, y, true)) {
                chaosGenerator.MakeLink(x.get(), y.get(), 0, a.get(), Ice);
                dx = sz * 2 + 1;
                dy = sz / 2 * 2 + 1;
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz / 2, dx, dy, Ice);
                fillTypes = new Runtime.RangeSet(Memory.SET16_r).with(fMoneyMix);
                RectFill(x.get() - sz, y.get() - sz / 2, x.get() + sz, y.get() + sz / 2);
            }
        }
        chaosObjects.PutExtraLife(20, 20);
        chaosObjects.PutExtraPower(5, 59, 59);
        chaosObjects.PutGridObjs(Anims.BONUS, ChaosBonus.TimedBonus, ChaosBonus.tbHospital, 25, 25, 15, 15, 2, 2);
        chaosObjects.Rect(30, 20, 59, 50);
        chaosObjects.PutMaxPower(1);
        chaosObjects.Rect(20, 20, 59, 59);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.PutTrefle(chaosBase.pLife, 10);
        chaosObjects.PutAlien1(ChaosAlien.aBubble, pLife2, 50);
        chaosObjects.PutCartoon(0, 0, 20);
        chaosObjects.Rect(1, 1, 79, 79);
        chaosObjects.PutBullet(16);
        chaosObjects.Rect(40, 40, 59, 59);
        chaosObjects.PutQuad(pLife2, 20);
        chaosObjects.PutTri(pLife3, chaosBase.difficulty);
        AddOptions(1, 1, 79, 79, 4, 4, 0, 0, 1, 0, 0);
    }

    /* Level 9 */
    public void Factory() {
        // VAR
        int c = 0;
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int dx = 0;
        int dy = 0;
        Runtime.Ref<Integer> a = new Runtime.Ref<>(0);
        int z = 0;
        int sz = 0;
        int d = 0;
        int sk = 0;

        chaosObjects.Clear(101, 101);
        if (trigo.RND() % 8 != 0)
            rotate = false;
        chaosBase.water = (chaosBase.difficulty >= 4) && (trigo.RND() % 3 == 0);
        chaosObjects.Fill(0, 0, 100, 100, EmptyBlock);
        chaosGenerator.DrawFactory();
        chaosObjects.PutPlayer(12, 90);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHelp, 12, 88);
        chaosObjects.Rect(1, 1, 98, 98);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doWindMaker, 0, 1, 1, 6);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doBubbleMaker, 1, 1, 1, 15);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doFireMaker, 0, 0, 2, 15);
        chaosObjects.Rect(33, 1, 98, 98);
        chaosObjects.PutKamikaze(0, 6);
        chaosObjects.PutKamikaze(1, 6);
        chaosObjects.PutKamikaze(2, 6);
        chaosObjects.PutKamikaze(3, 6);
        chaosObjects.PutPic(0, 10);
        chaosObjects.PutPic(1, 10);
        chaosObjects.Rect(1, 1, 98, 66);
        chaosObjects.PutTurret(15);
        chaosObjects.Rect(70, 1, 98, 98);
        chaosObjects.PutTri(pLife3, 7);
        chaosObjects.Rect(1, 1, 98, 98);
        chaosObjects.PutCannon3(6);
        chaosObjects.PutCartoon(0, 1, 25);
        chaosObjects.PutMagnetA(3, 15);
        chaosObjects.PutMagnetR(3, 5);
        for (c = 1; c <= 10; c++) {
            if (chaosGenerator.FindIsolatedRect(0, 0, 99, 99, 2, 8, a, x, y, true)) {
                chaosGenerator.MakeLink(x.get(), y.get(), 0, a.get(), BackBig);
                if (trigo.COS(a.get()) == 0) {
                    dx = 1;
                    dy = 0;
                } else {
                    dx = 0;
                    dy = 1;
                }
                for (z = -2; z <= 2; z++) {
                    chaosObjects.Put(x.get() + z * dx, y.get() + z * dy, BackSmall);
                    chaosObjects.Put(x.get() - 2 * dx + z * dy, y.get() - 2 * dy + z * dx, BackSmall);
                    chaosObjects.Put(x.get() + 2 * dx + z * dy, y.get() + 2 * dy + z * dx, BackSmall);
                }
                chaosObjects.Rect(x.get() - 2, y.get() - 2, x.get() + 2, y.get() + 2);
                chaosObjects.PutBullet(1);
                chaosObjects.PutAlien1(ChaosAlien.aHospital, pLife2, 1);
            }
        }
        if (chaosGenerator.FindIsolatedRect(0, 0, 99, 99, 1, 20, a, x, y, true)) {
            chaosObjects.Fill(x.get() - 1, y.get() - 1, x.get() + 1, y.get() + 1, Ice);
            chaosObjects.PutBlockObj(Anims.BONUS, ChaosBonus.BonusLevel, ChaosBonus.tbBonusLevel, x.get(), y.get());
            chaosGenerator.MakeLink(x.get(), y.get(), 1, a.get(), 23);
        }
        if (chaosBase.difficulty > 2) {
            for (c = 0; c <= 6; c++) {
                sz = trigo.RND() % 3 + 1;
                if (chaosGenerator.FindIsolatedRect(0, 0, 99, 99, sz, 20, a, x, y, true)) {
                    chaosObjects.Rect(x.get() - sz, y.get() - sz, x.get() + sz, y.get() + sz);
                    d = sz * 2 + 1;
                    chaosGenerator.MakeLink(x.get(), y.get(), sz, a.get(), FalseEmpty);
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, BackNone);
                    if (sz >= 2)
                        sz -= 2;
                    else
                        sz--;
                    d = 2 * sz + 1;
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, SimpleBlock);
                    sk = trigo.RND() % 4;
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
        chaosObjects.PutRandom(0, 0, 99, 99, chaosObjects.OnlyWall_ref, Sq1Block, trigo.RND() % 64);
        chaosObjects.PutRandom(0, 0, 99, 99, chaosObjects.OnlyWall_ref, Sq4Block, trigo.RND() % 64);
        chaosObjects.PutRandom(10, 0, 99, 99, chaosObjects.OnlyWall_ref, Sq4TravBlock, trigo.RND() % 128);
        chaosObjects.PutRandom(20, 0, 99, 99, chaosObjects.OnlyWall_ref, TravBlock, trigo.RND() % 128);
        chaosObjects.PutRandom(30, 0, 99, 79, chaosObjects.OnlyWall_ref, Fact1Block, trigo.RND() % 256);
        chaosObjects.PutRandom(30, 0, 79, 99, chaosObjects.OnlyWall_ref, Fact2Block, trigo.RND() % 256);
        chaosObjects.PutRandom(0, 0, 99, 59, chaosObjects.OnlyWall_ref, Fact3Block, trigo.RND() % 256);
        AddOptions(30, 1, 98, 98, 5, 5, 3, 4, 1, 0, 0);
    }

    /* Level 10 */
    public void Labyrinth() {
        // VAR
        int i = 0;
        int val = 0;

        chaosObjects.Cadre(120, 101);
        flipVert = false;
        rotate = false;
        chaosObjects.Fill(1, 1, 100, 100, ChaosGraphics.NbBackground);
        chaosObjects.Fill(101, 0, 119, 100, BackNone);
        chaosGenerator.DrawLabyrinth(50);
        chaosObjects.FillRandom(0, 0, 100, 100, Granit1, Granit2, chaosObjects.OnlyWall_ref, chaosObjects.Rnd_ref);
        if (chaosGraphics.dualpf)
            val = BackNone;
        else
            val = Back2x2;
        chaosObjects.FillCond(1, 1, 99, 99, chaosObjects.OnlyBackground_ref, val);
        chaosObjects.Fill(1, 1, 2, 2, Back2x2);
        chaosObjects.Put(100, 99, BackNone);
        chaosGenerator.RemIsolated(2, 2, 98, 98, 2, 2, BackSmall);
        chaosObjects.Fill(101, 30, 119, 30, SimpleBlock);
        chaosObjects.Fill(106, 33, 111, 33, SimpleBlock);
        chaosObjects.Fill(114, 33, 119, 33, SimpleBlock);
        chaosObjects.Fill(109, 35, 116, 35, SimpleBlock);
        chaosObjects.Fill(101, 100, 119, 100, SimpleBlock);
        chaosObjects.Fill(102, 50, 106, 50, SimpleBlock);
        chaosObjects.Fill(101, 52, 105, 69, SimpleBlock);
        chaosObjects.Fill(101, 90, 105, 90, SimpleBlock);
        chaosObjects.Fill(103, 52, 104, 69, FalseBlock);
        chaosObjects.Put(102, 90, BackNone);
        chaosObjects.Fill(119, 30, 119, 100, SimpleBlock);
        chaosObjects.Fill(106, 34, 106, 99, SimpleBlock);
        chaosObjects.Fill(102, 40, 102, 50, SimpleBlock);
        chaosObjects.Fill(104, 30, 104, 40, SimpleBlock);
        chaosObjects.Put(119, 30, FalseBlock);
        chaosObjects.PutPlayer(1, 1);
        chaosObjects.PutBlockObj(Anims.MACHINE, ChaosMachine.mDoor, 0, 102, 90);
        chaosObjects.PutBlockObj(Anims.MACHINE, ChaosMachine.mDoor, 0, 103, 64);
        chaosObjects.PutBlockObj(Anims.MACHINE, ChaosMachine.mDoor, 0, 104, 64);
        chaosObjects.PutBlockObj(Anims.MACHINE, ChaosMachine.mDoor, 0, 103, 40);
        chaosObjects.PutExtraLife(99, 1);
        chaosObjects.PutExtraLife(1, 99);
        chaosObjects.PutExtraPower(0, 101, 70);
        chaosObjects.PutBlockBonus(ChaosBonus.tbDBSpeed, 99, 99);
        chaosObjects.PutBlockBonus(ChaosBonus.tbMagnet, 105, 70);
        chaosObjects.PutBlockObj(Anims.MACHINE, ChaosMachine.mReactor, 0, 102, 98);
        chaosObjects.PutBlockBonus(ChaosBonus.tbMagnet, 105, 4);
        chaosObjects.PutBlockBonus(ChaosBonus.tbDifficulty, 110, 15);
        chaosObjects.PutExit(105, 99);
        chaosObjects.PutExit(110, 15);
        chaosObjects.PutExit(2, 1);
        chaosObjects.PutExit(118, 99);
        for (i = ChaosBonus.tbDBSpeed; i <= ChaosBonus.tbNoMissile; i++) {
            chaosObjects.PutBlockBonus(i, 105, 49 - i);
        }
        chaosObjects.Rect(1, 1, 99, 99);
        chaosObjects.PutTBonus(ChaosBonus.tbDBSpeed, 5);
        chaosObjects.PutTBonus(ChaosBonus.tbSGSpeed, 1);
        chaosObjects.PutMagnet(5);
        chaosObjects.PutInvinsibility(3);
        chaosObjects.PutSleeper(2);
        chaosObjects.PutBullet(10);
        chaosObjects.PutHospital(15);
        chaosObjects.PutMoney(EnumSet.of(Moneys.st), 50);
        chaosObjects.Rect(107, 50, 119, 99);
        chaosObjects.PutAlien2(ChaosCreator.cCircle, 200, 10);
        AddOptions(1, 1, 99, 99, 5, 5, 4, 8, 45, 3, 4);
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
