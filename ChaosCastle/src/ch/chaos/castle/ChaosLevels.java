package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.Zone;
import ch.chaos.castle.ChaosBonus.Moneys;
import ch.chaos.library.Languages;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosLevels {

    // Imports
    private final Chaos1Zone chaos1Zone;
    private final Chaos2Zone chaos2Zone;
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosDual chaosDual;
    private final ChaosGenerator chaosGenerator;
    private final ChaosGraphics chaosGraphics;
    private final ChaosImages chaosImages;
    private final ChaosObjects chaosObjects;
    private final Languages languages;
    private final Trigo trigo;


    private ChaosLevels() {
        instance = this; // Set early to handle circular dependencies
        chaos1Zone = Chaos1Zone.instance();
        chaos2Zone = Chaos2Zone.instance();
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosDual = ChaosDual.instance();
        chaosGenerator = ChaosGenerator.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosImages = ChaosImages.instance();
        chaosObjects = ChaosObjects.instance();
        languages = Languages.instance();
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
    private static final int FF9x9 = 22;
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


    // VAR

    private short val1;
    private short val2;


    public short getVal1() {
        return this.val1;
    }

    public void setVal1(short val1) {
        this.val1 = val1;
    }

    public short getVal2() {
        return this.val2;
    }

    public void setVal2(short val2) {
        this.val2 = val2;
    }


    // PROCEDURE

    private void InitMsg() {
        // VAR
        int c = 0;

        for (c = 0; c <= 3; c++) {
            chaosActions.msgObj[c] = null;
            chaosActions.priorities[c] = 0;
        }
    }

    private void BabyAliens() {
        // CONST
        final Runtime.RangeSet Alien1Set = new Runtime.RangeSet(Memory.SET16_r).with(ChaosAlien.aCartoon, ChaosAlien.aDbOval, ChaosAlien.aHospital, ChaosAlien.aDiese, ChaosAlien.aStar, ChaosAlien.aBubble, ChaosAlien.aBumper, ChaosAlien.aTri);
        final Runtime.RangeSet Alien2Set = new Runtime.RangeSet(Memory.SET16_r).with(ChaosCreator.cCreatorR, ChaosCreator.cCreatorC, ChaosCreator.cAlienBox, ChaosCreator.cNest);

        // VAR
        int rnd = 0;
        int c = 0;
        short width = 0;
        short height = 0;
        short w = 0;
        short h = 0;
        Runtime.Ref<Short> angle = new Runtime.Ref<>((short) 0);
        short sz = 0;
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short back = 0;
        short wall = 0;

        width = (short) (20 + chaosObjects.Rnd((short) 50));
        height = (short) (20 + chaosObjects.Rnd((short) 50));
        chaosObjects.Cadre(width, height);
        rnd = trigo.RND() % 3;
        if (rnd == 0) {
            w = (short) (chaosObjects.Rnd((short) 4) + 2);
            h = (short) (chaosObjects.Rnd((short) 4) + 2);
            chaosGenerator.DrawPacman(trigo.RND() % 8 + 4, w, h, (short) 2, (short) 2, (short) (width - w - 1), (short) (height - h - 1));
            chaosObjects.PutPlayer((short) 1, (short) 1);
            chaosObjects.PutExit((short) (width - 2), (short) (height - 2));
        } else if (rnd == 1) {
            w = (short) (width / 2);
            h = (short) (height / 2);
            chaosObjects.Fill((short) 0, (short) 0, (short) (width - 1), (short) (height - 1), (short) ChaosGraphics.NbBackground);
            chaosObjects.Fill((short) (w - 4), (short) (h - 4), (short) (w + 4), (short) (h + 4), (short) 0);
            chaosObjects.Fill((short) (w - 3), (short) (h - 3), (short) (w + 3), (short) (h + 3), (short) ChaosGraphics.NbBackground);
            chaosObjects.PutPlayer((short) (w - 4), (short) (h - 4));
            chaosObjects.PutExit((short) (w + 4), (short) (h + 4));
            for (c = 1; c <= 40; c++) {
                sz = (short) (trigo.RND() % 4 + 1);
                if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) (width - 1), (short) (height - 1), sz, (short) 15, angle, x, y, true)) {
                    chaosObjects.Fill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz), (short) 0);
                    chaosGenerator.MakeLink(x.get(), y.get(), sz, angle.get(), (short) 0);
                }
            }
        } else {
            chaosObjects.PutPlayer((short) 1, (short) 1);
            chaosObjects.PutExit((short) (width - 2), (short) (height - 2));
            for (c = 1; c <= 4 + trigo.RND() % 16; c++) {
                sz = (short) (trigo.RND() % 3 + 1);
                if (chaosGenerator.FindIsolatedRect((short) 1, (short) 1, (short) (width - 2), (short) (height - 2), sz, (short) 0, angle, x, y, false))
                    chaosObjects.Fill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz), (short) ChaosGraphics.NbBackground);
            }
            for (c = 1; c <= 4 + trigo.RND() % 32; c++) {
                chaosGenerator.PutCross((short) 1, (short) 1, (short) (width - 2), (short) (height - 1), (short) ChaosGraphics.NbBackground);
            }
            chaosObjects.Rect((short) 1, (short) 1, (short) (width - 2), (short) (height - 2));
            for (c = 1; c <= 4 + trigo.RND() % 8; c++) {
                if (chaosObjects.FindIsolatedPlace(1, x, y)) {
                    if (trigo.RND() % 2 == 0)
                        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doFireWall, 0, x.get(), y.get());
                    else
                        chaosObjects.Set(x.get(), y.get());
                }
            }
        }
        rnd = trigo.RND() % 4;
        switch (rnd) {
            case 0 -> {
                back = Light;
                wall = BarLight;
            }
            case 1 -> {
                back = Round4;
                wall = BarDark;
            }
            case 2 -> {
                back = Back4x4;
                wall = (short) (Fact1Block + trigo.RND() % 3);
            }
            case 3 -> {
                back = BackSmall;
                wall = TravLight;
            }
            default -> throw new RuntimeException("Unhandled CASE value " + rnd);
        }
        chaosObjects.FillCond((short) 0, (short) 0, (short) (width - 1), (short) (height - 1), Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), wall);
        chaosObjects.FillCond((short) 1, (short) 1, (short) (width - 2), (short) (height - 2), Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), back);
        for (c = 1; c <= 2; c++) {
            x.set(chaosObjects.Rnd((short) (width - 10)));
            y.set(chaosObjects.Rnd((short) (height - 10)));
            chaosObjects.Rect((short) (x.get() + 1), (short) (y.get() + 1), (short) (x.get() + 8), (short) (y.get() + 8));
            do {
                c = trigo.RND() % 16;
            } while (!Alien1Set.contains(c));
            chaosObjects.PutAlien1(c, chaos1Zone.pLife3, 10 + chaosBase.difficulty);
        }
        x.set(chaosObjects.Rnd((short) (width - 10)));
        y.set(chaosObjects.Rnd((short) (height - 10)));
        chaosObjects.Rect((short) (x.get() + 1), (short) (y.get() + 1), (short) (x.get() + 8), (short) (y.get() + 8));
        do {
            c = trigo.RND() % 16;
        } while (!Alien2Set.contains(c));
        chaosObjects.PutAlien2(c, 80, 10 + chaosBase.difficulty);
        chaosObjects.Rect((short) 4, (short) 4, (short) (width - 2), (short) (height - 2));
        chaosObjects.PutTBonus(ChaosBonus.tbBomb, 3);
        chaosObjects.PutTBonus(ChaosBonus.tbSleeper, 1);
        chaosObjects.PutTBonus(ChaosBonus.tbInvinsibility, 1);
        chaosObjects.PutBullet(16);
        chaosObjects.PutHospital(3);
        chaosObjects.PutAlien2(ChaosCreator.cCircle, 100, 15);
        chaosObjects.PutAlien2(ChaosCreator.cChief, 50, 10);
        chaos1Zone.AddOptions((short) 4, (short) 4, (short) 30, (short) 30, 0, 0, 0, 5, 0, 0, 0);
    }

    private void Spider() {
        // VAR
        int c = 0;
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short sz = 0;
        Runtime.Ref<Short> angle = new Runtime.Ref<>((short) 0);
        short d = 0;
        short val = 0;

        chaosObjects.Clear((short) 60, (short) 60);
        chaosObjects.Fill((short) 0, (short) 0, (short) 59, (short) 59, (short) ChaosGraphics.NbBackground);
        chaosGenerator.FillEllipse(26, 0, 8, 8, (short) 0);
        for (c = 1; c <= 25; c++) {
            sz = (short) (trigo.RND() % 5 + 1);
            d = (short) (2 * sz + 1);
            if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 59, (short) 59, sz, (short) 10, angle, x, y, true)) {
                if (trigo.RND() % 2 == 0)
                    chaosObjects.Fill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz), (short) 0);
                else
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) 0);
                chaosGenerator.MakeLink(x.get(), y.get(), sz, angle.get(), (short) 0);
            }
        }
        for (y.set((short) 0); y.get() <= 59; y.inc()) {
            for (x.set((short) 0); x.get() <= 59; x.inc()) {
                if (chaosObjects.Get(x.get(), y.get()) >= ChaosGraphics.NbBackground)
                    chaosObjects.Reset(x.get(), y.get());
                else
                    chaosObjects.Set(x.get(), y.get());
            }
        }
        chaosObjects.PutPlayer((short) 27, (short) 0);
        chaosObjects.PutExit((short) 32, (short) 0);
        chaosObjects.FillRandom((short) 0, (short) 0, (short) 59, (short) 59, (short) Forest1, (short) Forest7, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), Runtime.proc(chaosObjects::Rnd, "ChaosObjects.Rnd"));
        for (c = 1; c <= trigo.RND() % 8 + 5; c++) {
            val = (short) (trigo.RND() % 4 + Leaf1);
            chaosGenerator.PutCross((short) 1, (short) 1, (short) 58, (short) 58, val);
        }
        for (c = 1; c <= trigo.RND() % 8 + 5; c++) {
            val = (short) (trigo.RND() % 4 + Leaf1);
            sz = (short) (trigo.RND() % 2 + 1);
            if (chaosGenerator.FindIsolatedRect((short) 1, (short) 1, (short) 58, (short) 58, sz, (short) 0, angle, x, y, false))
                chaosObjects.Fill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz), val);
        }
        for (c = 1; c <= 30; c++) {
            sz = (short) (trigo.RND() % 4);
            d = (short) (2 * sz + 1);
            if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 59, (short) 59, sz, (short) 10, angle, x, y, true)) {
                if (trigo.RND() % 2 == 0)
                    chaosObjects.Fill((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz), (short) 0);
                else
                    chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) 0);
                chaosGenerator.MakeLink(x.get(), y.get(), sz, angle.get(), (short) 0);
            }
        }
        chaosObjects.FillCond((short) 0, (short) 0, (short) 59, (short) 59, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Ground2);
        for (c = 1; c <= 3; c++) {
            if (chaosGenerator.FindIsolatedRect((short) 1, (short) 1, (short) 58, (short) 58, (short) 1, (short) 0, angle, x, y, false))
                chaosObjects.FillObj(Anims.ALIEN1, (short) ChaosAlien.aCartoon, trigo.RND() % 2 + 1, (short) (x.get() - 1), (short) (y.get() - 1), (short) (x.get() + 1), (short) (y.get() + 1), true);
        }
        chaosObjects.Rect((short) 1, (short) 1, (short) 58, (short) 58);
        chaosObjects.PutAlien2(ChaosCreator.cNest, 0, 28);
        chaosObjects.PutTBonus(ChaosBonus.tbMagnet, 1);
        chaosObjects.PutTBonus(ChaosBonus.tbFreeFire, 1);
        chaosObjects.PutTBonus(ChaosBonus.tbBomb, 1);
        chaosObjects.PutMoney(EnumSet.of(Moneys.m1, Moneys.m2, Moneys.m5, Moneys.st), 18);
        chaosObjects.PutBullet(16);
        chaosObjects.PutHospital(4);
        chaosObjects.PutAlien1(ChaosAlien.aCartoon, 0, 13);
        chaosObjects.PutAlien2(ChaosCreator.cChief, 50, 12);
        chaosObjects.PutMachine(ChaosMachine.mCannon3, 0, 0, 8);
        if (chaosBase.difficulty >= 10)
            chaosObjects.PutRandomObjs(Anims.ALIEN3, (short) ChaosBoss.bSisterAlien, 0, 1);
        chaos1Zone.AddOptions((short) 1, (short) 1, (short) 59, (short) 59, 3, 3, 0, 0, 5, 4, 1);
    }

    private void Graveyard_RndRect() {
        // VAR
        short x = 0;
        short y = 0;

        x = chaosObjects.Rnd((short) 96);
        y = chaosObjects.Rnd((short) 96);
        chaosObjects.Rect(x, y, (short) (x + 30), (short) (y + 30));
    }

    private void Graveyard() {
        // VAR
        int c = 0;
        Runtime.Ref<Short> angle = new Runtime.Ref<>((short) 0);
        short sz = 0;
        short d = 0;
        short lx = 0;
        short ly = 0;
        short ml = 0;
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        int r = 0;
        short val = 0;

        chaosBase.water = chaosBase.level[Zone.Special.ordinal()] == 20;
        chaosObjects.Clear((short) 127, (short) 127);
        chaosObjects.Fill((short) 0, (short) 0, (short) 126, (short) 126, (short) SimpleBlock);
        lx = 0;
        ly = 0;
        ml = (short) (trigo.RND() % 6 + 2);
        for (angle.set((short) 0); angle.get() <= 360; angle.inc(2)) {
            r = trigo.SIN((short) (angle.get() * ml));
            x.set((short) (trigo.COS(angle.get()) * (2048 + r) / 54330));
            y.set((short) (trigo.SIN(angle.get()) * (2048 + r) / 54330));
            x.inc(63);
            y.inc(63);
            if ((lx != 0) && (ly != 0))
                chaosGenerator.Road(lx, ly, x.get(), y.get(), (short) 2, (short) Ground);
            lx = x.get();
            ly = y.get();
        }
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 126, (short) 126, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Leaf1, (short) 255);
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 126, (short) 126, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Leaf2, (short) 255);
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 126, (short) 126, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Leaf3, (short) 255);
        chaosObjects.PutRandom((short) 0, (short) 0, (short) 126, (short) 126, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) Fact2Block, (short) 50);
        chaosObjects.PutPlayer((short) 100, (short) 63);
        chaosObjects.PutBlockBonus(ChaosBonus.tbSleeper, (short) 100, (short) 63);
        chaosObjects.PutExit((short) 102, (short) 63);
        for (c = 1; c <= 40; c++) {
            sz = (short) (trigo.RND() % 3 + 2);
            d = (short) (sz * 2 + 1);
            if (chaosGenerator.FindIsolatedRect((short) 0, (short) 0, (short) 126, (short) 126, sz, (short) 30, angle, x, y, true)) {
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) Balls);
                chaosObjects.Rect((short) (x.get() - sz), (short) (y.get() - sz), (short) (x.get() + sz), (short) (y.get() + sz));
                if ((c >= 25) && ((c % 2) != 0))
                    val = FalseBlock;
                else if (chaosGraphics.dualpf)
                    val = BackNone;
                else
                    val = Balls;
                chaosGenerator.MakeLink(x.get(), y.get(), sz, angle.get(), val);
                sz -= 2;
                d -= 4;
                chaosGenerator.FillEllipse(x.get() - sz, y.get() - sz, d, d, (short) (Granit1 + trigo.RND() % 2));
                chaosObjects.PutDeadObj(ChaosDObj.doBubbleMaker, 0, 1);
                if (((c % 2) != 0)) {
                    if ((((c / 2) % 2) != 0))
                        chaosObjects.PutBullet(1);
                    else
                        chaosObjects.PutHospital(1);
                } else {
                    chaosObjects.PutMoney(EnumSet.of(Moneys.m1, Moneys.m2, Moneys.m5, Moneys.st), 2);
                }
                if (c > 34)
                    chaosObjects.PutTBonus(ChaosBonus.tbBomb, 1);
            }
        }
        chaosObjects.Rect((short) 0, (short) 0, (short) 126, (short) 126);
        chaosObjects.PutTBonus(ChaosBonus.tbHelp, 1);
        chaosObjects.PutDeadObj(ChaosDObj.doFireMaker, 0, 6);
        chaosObjects.PutDeadObj(ChaosDObj.doWindMaker, 1, 3);
        chaosObjects.PutTBonus(ChaosBonus.tbMagnet, 4);
        chaosObjects.PutTBonus(ChaosBonus.tbInvinsibility, 3);
        chaosObjects.PutTBonus(ChaosBonus.tbSleeper, 2);
        chaosObjects.PutTBonus(ChaosBonus.tbFreeFire, 2);
        chaosObjects.PutTBonus(ChaosBonus.tbMaxPower, 1);
        chaosObjects.PutTBonus(ChaosBonus.tbDBSpeed, 2);
        chaosObjects.PutTBonus(ChaosBonus.tbSGSpeed, 1);
        chaosObjects.PutAlien1(ChaosAlien.aBigDrawer, 100, 3);
        chaosObjects.PutAlien1(ChaosAlien.aSmallDrawer, 100, 3);
        chaosObjects.PutDeadObj(ChaosDObj.doFireWall, 0, 10);
        chaos1Zone.AddOptions((short) 32, (short) 32, (short) 95, (short) 95, 0, 4, 0, 2, 10, 8, 1);
        Graveyard_RndRect();
        chaosObjects.PutAlien2(ChaosCreator.cCircle, 100, 20);
        Graveyard_RndRect();
        chaosObjects.PutAlien2(ChaosCreator.cChief, 50, 10);
        Graveyard_RndRect();
        chaosObjects.PutPic(0, 3);
        chaosObjects.PutPic(1, 3);
        Graveyard_RndRect();
        chaosObjects.PutCreatorC(10);
        Graveyard_RndRect();
        chaosObjects.PutCreatorR(1);
        Graveyard_RndRect();
        chaosObjects.PutCannon3(3);
        chaosObjects.PutDeadObj(ChaosDObj.doWindMaker, 0, 2);
    }

    private void Winter() {
        // VAR
        short lx = 0;
        short ly = 0;
        Runtime.Ref<Short> px = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> py = new Runtime.Ref<>((short) 0);
        short s1 = 0;
        short s2 = 0;
        short a1 = 0;
        short a2 = 0;
        int c = 0;
        short val = 0;

        chaosObjects.Clear((short) 120, (short) 38);
        chaosBase.snow = true;
        chaosBase.water = (chaosBase.difficulty >= 6) && (trigo.RND() % 8 == 0);
        chaosObjects.Fill((short) 0, (short) 0, (short) 119, (short) 37, (short) SimpleBlock);
        a1 = 0;
        a2 = 0;
        lx = 0;
        ly = 0;
        s1 = (short) (chaosObjects.Rnd((short) 16) + 6);
        s2 = (short) (chaosObjects.Rnd((short) 16) + 6);
        for (px.set((short) 3); px.get() <= 116; px.inc()) {
            py.set((short) ((trigo.SIN(a1) * 3 + trigo.SIN(a2) * 2) / 366 + 16));
            if (lx != 0)
                chaosGenerator.Road(lx, ly, px.get(), py.get(), (short) 2, (short) Ice);
            lx = px.get();
            ly = py.get();
            a1 += s1;
            a2 += s2;
        }
        px.set((short) 116);
        chaosObjects.Fill((short) (px.get() - 2), (short) (py.get() - 2), (short) (px.get() + 2), (short) (py.get() + 2), (short) Ice);
        chaosObjects.Fill((short) 119, py.get(), (short) 119, (short) 37, (short) FalseBlock);
        chaosObjects.Fill((short) 100, (short) 37, (short) 118, (short) 37, (short) Ground2);
        chaosObjects.Fill((short) 97, (short) 35, (short) 99, (short) 37, (short) Ground);
        chaosObjects.PutBlockObj(Anims.BONUS, (short) ChaosBonus.BonusLevel, ChaosBonus.tbBonusLevel, (short) 98, (short) 36);
        chaosObjects.PutExit((short) 97, (short) 35);
        chaosObjects.PutPlayer((short) 1, (short) 16);
        chaosObjects.PutBlockBonus(ChaosBonus.tbInvinsibility, (short) 1, (short) 16);
        chaosObjects.PutExit(px.get(), py.get());
        chaosObjects.FillRandom((short) 1, (short) 1, (short) 118, (short) 36, (short) Forest1, (short) Forest7, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), Runtime.proc(chaosObjects::Rnd, "ChaosObjects.Rnd"));
        chaosObjects.Rect((short) 1, (short) 1, (short) 118, (short) 31);
        for (c = 0; c <= trigo.RND() % 24 + 8; c++) {
            if (chaosObjects.FindIsolatedPlace(0, px, py)) {
                val = (short) (Leaf1 + trigo.RND() % 5);
                if (val > Leaf4)
                    val = IceBlock;
                chaosObjects.Put(px.get(), py.get(), val);
            }
        }
        chaosObjects.Rect((short) 20, (short) 1, (short) 70, (short) 31);
        chaosObjects.PutDeltaObjs(Anims.MACHINE, (short) ChaosMachine.mCannon2, 0, (short) 0, (short) -1, 10);
        chaosObjects.PutDeltaObjs(Anims.MACHINE, (short) ChaosMachine.mCannon2, 1, (short) 0, (short) 1, 10);
        chaosObjects.Rect((short) 50, (short) 1, (short) 100, (short) 31);
        chaosObjects.PutMachine(ChaosMachine.mCannon1, 0, 1, 4);
        chaosObjects.PutTurret(10);
        chaosObjects.PutCannon3(8);
        chaosObjects.Rect((short) 80, (short) 1, (short) 118, (short) 31);
        chaosObjects.PutDeadObj(ChaosDObj.doFireMaker, 0, 7);
        chaosObjects.PutDeadObj(ChaosDObj.doWindMaker, 0, 3);
        chaosObjects.PutDeadObj(ChaosDObj.doWindMaker, 1, 4);
        chaosObjects.Rect((short) 1, (short) 1, (short) 118, (short) 37);
        chaosObjects.PutDeadObj(ChaosDObj.doBubbleMaker, 0, 10);
        chaosObjects.PutAlien2(ChaosCreator.cNest, 0, 15);
        chaosObjects.PutRAlien1(ChaosAlien.aCartoon, 0, 2, 15);
        chaosObjects.PutRAlien1(ChaosAlien.aKamikaze, 0, 3, 15);
        chaosObjects.PutTBonus(ChaosBonus.tbBomb, 2);
        chaosObjects.PutBullet(14);
        chaosObjects.PutHospital(7);
        chaosObjects.PutMoney(EnumSet.of(Moneys.m2, Moneys.m2, Moneys.m5, Moneys.st), 20);
    }

    private void Panic() {
        // VAR
        short px = 0;
        short py = 0;
        short pl = 0;
        short r = 0;

        chaosObjects.Clear((short) 41, (short) 41);
        chaosBase.water = true;
        for (py = 0; py <= 40; py++) {
            for (px = 0; px <= 40; px++) {
                pl = (short) trigo.SQRT((px - 20) * (px - 20) + (py - 20) * (py - 20));
                r = (short) (trigo.RND() % 16 + 4);
                if (pl > r)
                    chaosObjects.Put(px, py, (short) FPanic);
                else
                    chaosObjects.Put(px, py, (short) 0);
            }
        }
        if (chaosGraphics.dualpf)
            chaosObjects.FillCond((short) 1, (short) 1, (short) 39, (short) 39, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) BackNone);
        else
            chaosObjects.FillRandom((short) 1, (short) 1, (short) 39, (short) 39, (short) 0, (short) 7, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), Runtime.proc(chaosObjects::Rnd, "ChaosObjects.Rnd"));
        chaosObjects.Fill((short) 20, (short) 1, (short) 20, (short) 39, (short) Ice);
        chaosObjects.PutPlayer((short) 20, (short) 22);
        chaosObjects.PutExit((short) 20, (short) 30);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHelp, (short) 20, (short) 26);
        chaosObjects.PutBlockObj(Anims.BONUS, (short) ChaosBonus.BonusLevel, ChaosBonus.tbBonusLevel, (short) 20, (short) 39);
        for (py = 33; py <= 38; py++) {
            chaosObjects.PutBlockBonus(ChaosBonus.tbInvinsibility, (short) 20, py);
        }
        for (py = 1; py <= 4; py++) {
            chaosObjects.PutBlockBonus(ChaosBonus.tbSleeper, (short) 20, py);
        }
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bSisterAlien, 0, (short) 20, (short) 8);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMotherAlien, 0, (short) 18, (short) 20);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMotherAlien, 0, (short) 22, (short) 20);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterEye, 0, (short) 19, (short) 18);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterEye, 1, (short) 21, (short) 18);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterMouth, 0, (short) 20, (short) 18);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterAlien1, 0, (short) 20, (short) 18);
        chaosObjects.Rect((short) 1, (short) 1, (short) 39, (short) 39);
        chaosObjects.PutTBonus(ChaosBonus.tbBomb, 8);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doBubbleMaker, 0, 1, 0, 15);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 0, 1, 0, 6);
        chaosObjects.PutMagnetR(3, 8);
        chaosObjects.PutMagnetA(1, 3);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doWindMaker, 0, 1, 0, 6);
        chaosObjects.PutChaosObjs(Anims.DEADOBJ, (short) ChaosDObj.doSand, 0, (short) (ChaosGraphics.BW * 10), (short) (ChaosGraphics.BH * 10), (short) (ChaosGraphics.BW * 30), (short) (ChaosGraphics.BH * 20), 20);
        chaosObjects.PutRAlien1(ChaosAlien.aCartoon, 0, 2, 8);
        chaosObjects.PutHospital(16);
        chaosObjects.PutBullet(10);
        chaosObjects.PutMoney(EnumSet.of(Moneys.m5, Moneys.st), 15);
        chaosObjects.PutMoney(EnumSet.of(Moneys.m3), 1);
    }

    private short CheckerBoard(short x, short y) {
        if ((((x + y) % 2) != 0))
            return val1;
        else
            return val2;
    }

    private void Brother() {
        chaosObjects.Cadre((short) 21, (short) 21);
        val1 = FSmall1;
        val2 = FSmall2;
        chaosObjects.FillChoose((short) 0, (short) 0, (short) 20, (short) 20, Runtime.proc(chaosObjects::All, "ChaosObjects.All"), Runtime.proc(this::CheckerBoard, "ChaosLevels.CheckerBoard"));
        chaosObjects.Fill((short) 7, (short) 1, (short) 13, (short) 19, (short) 11);
        chaosObjects.Fill((short) 1, (short) 7, (short) 19, (short) 13, (short) 11);
        chaosObjects.PutPlayer((short) 7, (short) 19);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHelp, (short) 7, (short) 1);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bBrotherAlien, 0, (short) 11, (short) 1);
    }

    private void Sister() {
        chaosObjects.Cadre((short) 16, (short) 24);
        chaosBase.water = true;
        val1 = FSmall1;
        val2 = FSmall2;
        chaosObjects.FillChoose((short) 0, (short) 0, (short) 15, (short) 23, Runtime.proc(chaosObjects::All, "ChaosObjects.All"), Runtime.proc(this::CheckerBoard, "ChaosLevels.CheckerBoard"));
        chaosObjects.Fill((short) 1, (short) 1, (short) 14, (short) 22, (short) Light);
        val1 = FBig1;
        val2 = FBig2;
        chaosObjects.FillChoose((short) 7, (short) 10, (short) 8, (short) 13, Runtime.proc(chaosObjects::All, "ChaosObjects.All"), Runtime.proc(this::CheckerBoard, "ChaosLevels.CheckerBoard"));
        chaosObjects.PutObj(Anims.DEADOBJ, (short) ChaosDObj.doBubbleMaker, 0, (short) (ChaosGraphics.BW * 8), (short) (ChaosGraphics.BH * 9));
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bSisterAlien, 0, (short) 9, (short) 14);
        chaosObjects.PutPlayer((short) 6, (short) 22);
    }

    private void Mother() {
        // VAR
        short x = 0;
        short y = 0;
        short zx = 0;
        short zy = 0;

        chaosObjects.Clear((short) 11, (short) 11);
        if (chaosGraphics.dualpf) {
            for (x = 0; x <= 10; x++) {
                for (y = 0; y <= 10; y++) {
                    zx = (short) ((x - 7) + chaosObjects.Rnd((short) 5));
                    zy = (short) ((y - 7) + chaosObjects.Rnd((short) 5));
                    if ((Math.abs(zx) <= 1) || (Math.abs(zy) <= 1))
                        chaosObjects.Put(x, y, (short) Round4);
                    else
                        chaosObjects.Put(x, y, (short) BackNone);
                }
            }
        } else {
            chaosObjects.FillRandom((short) 0, (short) 0, (short) 10, (short) 10, (short) 0, (short) 7, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), Runtime.proc(chaosObjects::ExpRandom, "ChaosObjects.ExpRandom"));
        }
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMotherAlien, 0, (short) 5, (short) 3);
        chaosObjects.PutPlayer((short) 5, (short) 7);
    }

    private void Father() {
        // VAR
        short x = 0;

        chaosObjects.Clear((short) 11, (short) 13);
        val1 = FRound;
        val2 = FStar;
        chaosObjects.FillChoose((short) 0, (short) 0, (short) 10, (short) 12, Runtime.proc(chaosObjects::All, "ChaosObjects.All"), Runtime.proc(this::CheckerBoard, "ChaosLevels.CheckerBoard"));
        chaosObjects.Fill((short) 1, (short) 3, (short) 9, (short) 11, (short) Back2x2);
        chaosObjects.Fill((short) 4, (short) 1, (short) 6, (short) 2, (short) BackSmall);
        chaosObjects.Put((short) 5, (short) 0, (short) BackSmall);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 0, (short) 4, (short) 1);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 0, (short) 5, (short) 0);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 0, (short) 6, (short) 1);
        for (x = 4; x <= 6; x++) {
            chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mDoor, 0, x, (short) 2);
            chaosObjects.PutBlockObj(Anims.ALIEN2, (short) ChaosCreator.cNest, 0, (short) 5, (short) 1);
        }
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bFatherHeart, 0, (short) 5, (short) 5);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bFatherAlien, 0, (short) 5, (short) 5);
        chaosObjects.PutPlayer((short) 5, (short) 9);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHelp, (short) 9, (short) 3);
        chaosObjects.PutExit((short) 1, (short) 3);
    }

    private void Master() {
        // VAR
        short z = 0;
        short d = 0;
        short val = 0;

        chaosObjects.Clear((short) 11, (short) 11);
        if (chaosGraphics.dualpf) {
            for (z = 5; z >= 0; z -= 1) {
                d = (short) (z * 2 + 1);
                if (((z % 2) != 0))
                    val = BackBig;
                else
                    val = BackNone;
                chaosGenerator.FillEllipse(5 - z, 5 - z, d, d, val);
            }
        } else {
            chaosObjects.FillRandom((short) 0, (short) 0, (short) 10, (short) 10, (short) 0, (short) 7, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), Runtime.proc(chaosObjects::ExpRandom, "ChaosObjects.ExpRandom"));
        }
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterEye, 0, (short) 4, (short) 3);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterEye, 1, (short) 6, (short) 3);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterMouth, 0, (short) 5, (short) 4);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterAlien1, 0, (short) 5, (short) 3);
        chaosObjects.PutBlockBonus(ChaosBonus.tbInvinsibility, (short) 0, (short) 0);
        chaosObjects.PutBlockBonus(ChaosBonus.tbInvinsibility, (short) 10, (short) 0);
        chaosObjects.PutBlockBonus(ChaosBonus.tbMagnet, (short) 0, (short) 10);
        chaosObjects.PutBlockBonus(ChaosBonus.tbMagnet, (short) 10, (short) 10);
        chaosObjects.PutPlayer((short) 5, (short) 7);
    }

    private void Illusion() {
        chaosObjects.Cadre((short) 36, (short) 18);
        chaosObjects.FillCond((short) 0, (short) 0, (short) 35, (short) 17, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) BackBig);
        chaosObjects.FillCond((short) 0, (short) 0, (short) 35, (short) 17, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) F9x9);
        val1 = FSmall1;
        val2 = FBig2;
        chaosObjects.FillChoose((short) 9, (short) 5, (short) 12, (short) 11, Runtime.proc(chaosObjects::All, "ChaosObjects.All"), Runtime.proc(this::CheckerBoard, "ChaosLevels.CheckerBoard"));
        val1 = FSmall2;
        val2 = FBig1;
        chaosObjects.FillChoose((short) 23, (short) 5, (short) 26, (short) 11, Runtime.proc(chaosObjects::All, "ChaosObjects.All"), Runtime.proc(this::CheckerBoard, "ChaosLevels.CheckerBoard"));
        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 1, (short) 15, (short) 7);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 1, (short) 20, (short) 7);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 0, (short) 17, (short) 10);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 0, (short) 18, (short) 10);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mReactor, 0, (short) 2, (short) 16);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mReactor, 0, (short) 33, (short) 16);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doMagnetA, 3, (short) 3, (short) 13);
        chaosObjects.PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doMagnetA, 3, (short) 32, (short) 13);
        chaosObjects.Rect((short) 10, (short) 1, (short) 25, (short) 16);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doWindMaker, 0, 1, 0, 2);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doBubbleMaker, 0, 1, 0, 2);
        chaosObjects.Rect((short) 1, (short) 1, (short) 34, (short) 3);
        chaosObjects.PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doFireMaker, 0, 1, 0, 2);
        chaosObjects.Rect((short) 5, (short) 1, (short) 30, (short) 16);
        chaosObjects.PutMagnetR(3, 6);
        chaosObjects.PutMagnetA(1, 6);
        chaosObjects.PutPlayer((short) 1, (short) 1);
        if (chaosBase.difficulty >= 4)
            chaosObjects.PutObj(Anims.ALIEN2, (short) ChaosCreator.cController, 0, (short) 0, (short) 0);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterAlien2, 10, (short) 34, (short) 1);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterPart0, 0, (short) 34, (short) 1);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterPart0, 1, (short) 34, (short) 1);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterPart1, 2, (short) 34, (short) 1);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterPart1, 3, (short) 34, (short) 1);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterPart2, 4, (short) 34, (short) 1);
    }

    private void Kids() {
        chaosObjects.Cadre((short) 24, (short) 24);
        chaosBase.snow = true;
        chaosObjects.Fill((short) 6, (short) 6, (short) 17, (short) 17, (short) ChaosGraphics.NbBackground);
        chaosObjects.Fill((short) 9, (short) 9, (short) 14, (short) 14, (short) 0);
        chaosObjects.Fill((short) 11, (short) 6, (short) 12, (short) 17, (short) 0);
        chaosObjects.Fill((short) 6, (short) 11, (short) 17, (short) 12, (short) 0);
        if (trigo.RND() % 2 == 0) {
            val1 = FSmall1;
            val2 = FBig2;
        } else {
            val1 = FSmall2;
            val2 = FBig1;
        }
        chaosObjects.FillChoose((short) 0, (short) 0, (short) 23, (short) 23, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), Runtime.proc(this::CheckerBoard, "ChaosLevels.CheckerBoard"));
        chaosObjects.FillCond((short) 1, (short) 1, (short) 22, (short) 22, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Ice);
        chaosObjects.PutChaosObjs(Anims.DEADOBJ, (short) ChaosDObj.doSand, 0, (short) 0, (short) 0, (short) 735, (short) 735, 10);
        chaosObjects.PutPlayer((short) 1, (short) 22);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bBrotherAlien, 0, (short) 21, (short) 22);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bSisterAlien, 0, (short) 1, (short) 1);
    }

    private void Parents() {
        chaosObjects.Cadre((short) 17, (short) 17);
        chaosObjects.Fill((short) 0, (short) 0, (short) 16, (short) 16, (short) FRound);
        chaosObjects.Fill((short) 1, (short) 1, (short) 15, (short) 15, (short) Ice);
        chaosObjects.Fill((short) 2, (short) 2, (short) 14, (short) 14, (short) FStar);
        chaosObjects.Fill((short) 3, (short) 2, (short) 13, (short) 13, (short) Ice);
        chaosObjects.Fill((short) 8, (short) 0, (short) 8, (short) 16, (short) Ice);
        chaosObjects.Fill((short) 0, (short) 8, (short) 16, (short) 8, (short) Ice);
        chaosObjects.Fill((short) 0, (short) 0, (short) 4, (short) 4, (short) Ice);
        chaosObjects.Fill((short) 0, (short) 12, (short) 4, (short) 16, (short) Ice);
        chaosObjects.Fill((short) 12, (short) 0, (short) 16, (short) 4, (short) Ice);
        chaosObjects.Fill((short) 12, (short) 12, (short) 16, (short) 16, (short) Ice);
        chaosObjects.PutBlockBonus(ChaosBonus.tbMagnet, (short) 2, (short) 2);
        chaosObjects.PutBlockBonus(ChaosBonus.tbMagnet, (short) 15, (short) 2);
        chaosObjects.PutBlockBonus(ChaosBonus.tbMagnet, (short) 2, (short) 15);
        chaosObjects.PutBlockBonus(ChaosBonus.tbMagnet, (short) 15, (short) 15);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 0, (short) 8);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 16, (short) 8);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 8, (short) 0);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 8, (short) 16);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bFatherHeart, 0, (short) 7, (short) 12);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bFatherAlien, 0, (short) 7, (short) 12);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMotherAlien, 0, (short) 11, (short) 12);
        chaosObjects.PutPlayer((short) 8, (short) 5);
    }

    private void Masters() {
        chaosObjects.Cadre((short) 17, (short) 17);
        if (chaosGraphics.dualpf)
            val1 = BackNone;
        else
            val1 = Back8x8;
        chaosObjects.Fill((short) 1, (short) 1, (short) 15, (short) 15, (short) Back4x4);
        chaosGenerator.FillEllipse(0, 0, 17, 17, val1);
        chaosGenerator.FillEllipse(2, 2, 13, 13, (short) ChaosGraphics.NbBackground);
        chaosGenerator.FillEllipse(4, 4, 9, 9, val1);
        chaosObjects.Fill((short) 7, (short) 2, (short) 9, (short) 14, (short) BackSmall);
        chaosObjects.Fill((short) 2, (short) 7, (short) 14, (short) 9, (short) BackSmall);
        val1 = FBig1;
        val2 = FBig2;
        chaosObjects.FillChoose((short) 0, (short) 0, (short) 16, (short) 16, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), Runtime.proc(this::CheckerBoard, "ChaosLevels.CheckerBoard"));
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 6, (short) 6);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 10, (short) 6);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 6, (short) 10);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 10, (short) 10);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mDoor, 0, (short) 8, (short) 5);
        chaosObjects.PutFineObj(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 1, (short) 1, (short) 8, (short) 0, (short) 1);
        chaosObjects.PutFineObj(Anims.DEADOBJ, (short) ChaosDObj.doMirror, 1, (short) 15, (short) 8, (short) 1, (short) 1);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mReactor, 0, (short) 1, (short) 15);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mReactor, 0, (short) 15, (short) 15);
        chaosObjects.PutBlockBonus(ChaosBonus.tbInvinsibility, (short) 1, (short) 1);
        chaosObjects.PutBlockBonus(ChaosBonus.tbInvinsibility, (short) 15, (short) 1);
        chaosObjects.PutPlayer((short) 5, (short) 9);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterEye, 0, (short) 9, (short) 5);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterEye, 1, (short) 9, (short) 5);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterMouth, 0, (short) 9, (short) 5);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterAlien1, 0, (short) 9, (short) 5);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterAlien2, 10, (short) 8, (short) 8);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterPart0, 0, (short) 8, (short) 8);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterPart0, 1, (short) 8, (short) 8);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterPart1, 2, (short) 8, (short) 8);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterPart1, 3, (short) 8, (short) 8);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMasterPart2, 4, (short) 8, (short) 8);
    }

    private void Final() {
        chaosObjects.Cadre((short) 19, (short) 99);
        chaosObjects.Fill((short) 0, (short) 0, (short) 18, (short) 40, (short) F9x9);
        chaosObjects.Fill((short) 3, (short) 28, (short) 16, (short) 31, (short) Leaf3);
        chaosObjects.Fill((short) 10, (short) 32, (short) 17, (short) 34, (short) BackNone);
        chaosObjects.Fill((short) 0, (short) 58, (short) 18, (short) 98, (short) F9x9);
        chaosObjects.Fill((short) 1, (short) 1, (short) 17, (short) 19, (short) 0);
        chaosObjects.Fill((short) 1, (short) 1, (short) 3, (short) 3, (short) 6);
        chaosObjects.Fill((short) 17, (short) 20, (short) 17, (short) 39, (short) FF9x9);
        chaosObjects.Fill((short) 2, (short) 20, (short) 2, (short) 33, (short) FF9x9);
        chaosObjects.Fill((short) 3, (short) 25, (short) 9, (short) 25, (short) FF9x9);
        chaosObjects.Fill((short) 2, (short) 33, (short) 2, (short) 33, (short) Ground2);
        chaosObjects.FillCond((short) 0, (short) 40, (short) 18, (short) 58, Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), (short) F9x9);
        chaosObjects.FillCond((short) 0, (short) 40, (short) 18, (short) 58, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), (short) Round4);
        chaosObjects.Fill((short) 4, (short) 44, (short) 6, (short) 46, (short) FBig1);
        chaosObjects.Fill((short) 5, (short) 45, (short) 5, (short) 45, (short) FPanic);
        chaosObjects.Fill((short) 12, (short) 44, (short) 14, (short) 46, (short) FBig2);
        chaosObjects.Fill((short) 13, (short) 45, (short) 13, (short) 45, (short) FPanic);
        chaosObjects.Fill((short) 4, (short) 52, (short) 6, (short) 54, (short) FBig2);
        chaosObjects.Fill((short) 5, (short) 53, (short) 5, (short) 53, (short) FPanic);
        chaosObjects.Fill((short) 12, (short) 52, (short) 14, (short) 54, (short) FBig1);
        chaosObjects.Fill((short) 13, (short) 53, (short) 13, (short) 53, (short) FPanic);
        chaosObjects.FillObj(Anims.SMARTBONUS, (short) ChaosSmartBonus.sbExtraLife, 0, (short) 1, (short) 41, (short) 1, (short) 41, true);
        chaosObjects.FillObj(Anims.BONUS, (short) ChaosBonus.TimedBonus, ChaosBonus.tbMagnet, (short) 1, (short) 57, (short) 1, (short) 57, true);
        chaosObjects.FillObj(Anims.BONUS, (short) ChaosBonus.TimedBonus, ChaosBonus.tbInvinsibility, (short) 17, (short) 41, (short) 17, (short) 41, true);
        chaosObjects.FillObj(Anims.BONUS, (short) ChaosBonus.TimedBonus, ChaosBonus.tbSleeper, (short) 17, (short) 57, (short) 17, (short) 57, true);
        chaosObjects.FillObj(Anims.BONUS, (short) ChaosBonus.BonusLevel, ChaosBonus.tbBonusLevel, (short) 9, (short) 25, (short) 9, (short) 25, true);
        chaosObjects.PutBlockObj(Anims.BONUS, (short) ChaosBonus.BonusLevel, ChaosBonus.tbBonusLevel, (short) 2, (short) 33);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHelp, (short) 17, (short) 35);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 1, (short) 49);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 17, (short) 49);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 9, (short) 41);
        chaosObjects.PutBlockBonus(ChaosBonus.tbHospital, (short) 9, (short) 57);
        chaosObjects.PutBlockObj(Anims.MACHINE, (short) ChaosMachine.mReactor, 0, (short) 9, (short) 57);
        chaosObjects.PutBlockBonus(ChaosBonus.tbDifficulty, (short) 2, (short) 2);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bBrotherAlien, 0, (short) 10, (short) 50);
        chaosObjects.PutBlockObj(Anims.ALIEN3, (short) ChaosBoss.bMotherAlien, 0, (short) 10, (short) 50);
        chaosObjects.PutPlayer((short) 16, (short) 56);
    }

    private void MakeChaos() {
        // CONST
        final int MaxY = ChaosGraphics.PH * 2 / 3 - 10;

        // VAR
        Runtime.Ref<Integer> stl = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> c = new Runtime.Ref<>(0);
        int total = 0;
        int cStep = 0;
        int cCurrent = 0;
        short cnt = 0;
        short cntAlienS = 0;
        short cntAlienV = 0;
        short cntAlienA = 0;
        short cLevel = 0;
        short dv = 0;
        short fib1 = 0;
        short fib2 = 0;

        chaosObjects.Clear((short) ChaosGraphics.SOW, (short) ChaosGraphics.SOH);
        chaosBase.water = (chaosBase.difficulty >= 6) && (chaosBase.level[Zone.Chaos.ordinal()] == 13);
        chaosGraphics.gameWidth = ChaosGraphics.PW;
        chaosGraphics.gameHeight = ChaosGraphics.PH;
        chaosObjects.FillRandom((short) 0, (short) 0, (short) (ChaosGraphics.SOW - 1), (short) (ChaosGraphics.SOH - 1), (short) 0, (short) 7, Runtime.proc(chaosObjects::OnlyBackground, "ChaosObjects.OnlyBackground"), Runtime.proc(chaosObjects::ExpRandom, "ChaosObjects.ExpRandom"));
        cLevel = chaosBase.level[Zone.Chaos.ordinal()];
        total = 0;
        cnt = -1;
        dv = (short) trigo.SQRT(cLevel);
        while (dv * dv > cLevel) {
            dv--;
        }
        while (dv > 1) {
            if (cLevel % dv == 0)
                cnt++;
            dv--;
        }
        if (cnt > 0) {
            total += cnt;
            chaosObjects.PutChaosObjs(Anims.ALIEN1, (short) ChaosAlien.aSmallDrawer, 0, (short) 10, (short) 10, (short) (ChaosGraphics.PW - 10), (short) MaxY, cnt);
        }
        fib1 = 3;
        fib2 = 5;
        cnt = 1;
        while (fib2 < cLevel) {
            if (cnt < 3)
                cnt++;
            dv = fib2;
            fib2 += fib1;
            fib1 = dv;
        }
        if ((fib2 == cLevel) || (cLevel == 100)) {
            total += cnt;
            chaosObjects.PutChaosObjs(Anims.ALIEN1, (short) ChaosAlien.aBigDrawer, 0, (short) 20, (short) 20, (short) (ChaosGraphics.PW - 20), (short) MaxY, cnt);
        }
        cnt = 0;
        cStep = 6;
        cCurrent = 7;
        for (c.set(1); c.get() <= cLevel; c.inc()) {
            if (cCurrent == 0) {
                cStep--;
                if (cStep == 0) {
                    cStep = 7;
                    cnt++;
                }
                cCurrent = cStep;
            }
            cCurrent--;
        }
        if (cCurrent == 0)
            cnt++;
        c.set(cnt * 80);
        if (cLevel % 33 == 0)
            c.inc(160);
        chaosObjects.PutChaosChain(Anims.ALIEN2, (short) ChaosCreator.cCreatorR, c, 80, (short) 20, (short) 20, (short) (ChaosGraphics.PW - 20), (short) MaxY, cnt);
        total += cnt;
        cntAlienV = 0;
        cntAlienA = 0;
        cntAlienS = 0;
        if (cLevel < 7)
            cntAlienS = cLevel;
        else
            cntAlienS = 6;
        if (cLevel % 33 != 0) {
            total += cntAlienS;
            stl.set((cntAlienS + chaosBase.difficulty - 1) * 8);
            while ((cLevel % 2 == 0) && (cntAlienS > 0)) {
                cLevel = (short) (cLevel / 2);
                cntAlienS--;
                cntAlienV++;
            }
            while ((cLevel % 3 == 0) && (cntAlienS > 0)) {
                cLevel = (short) (cLevel / 3);
                cntAlienS--;
                cntAlienA++;
            }
            if (cLevel == 100) {
                cntAlienA++;
                cntAlienS--;
            }
            chaosObjects.PutChaosChain(Anims.ALIEN2, (short) ChaosCreator.cAlienA, stl, 8, (short) 10, (short) 10, (short) (ChaosGraphics.PW - 10), (short) MaxY, cntAlienA);
            chaosObjects.PutChaosChain(Anims.ALIEN2, (short) ChaosCreator.cAlienV, stl, 8, (short) 10, (short) 10, (short) (ChaosGraphics.PW - 10), (short) MaxY, cntAlienV);
            chaosObjects.PutChaosChain(Anims.ALIEN1, (short) ChaosAlien.aDbOval, stl, 8, (short) 10, (short) 10, (short) (ChaosGraphics.PW - 10), (short) MaxY, cntAlienS);
        } else if (chaosBase.difficulty >= 3) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Mind the meteorites"), String.class), (short) ChaosActions.statPos, (short) 3);
        }
        if (chaosBase.level[Zone.Chaos.ordinal()] % 20 == 0) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Big trouble"), String.class), (short) ChaosActions.statPos, (short) 3);
            chaosObjects.PutChaosObjs(Anims.ALIEN2, (short) ChaosCreator.cCircle, 200, (short) 20, (short) 20, (short) (ChaosGraphics.PW - 20), (short) MaxY, 1);
            total++;
        }
        if (chaosBase.level[Zone.Chaos.ordinal()] == 100)
            chaosObjects.PutChaosObjs(Anims.BONUS, (short) ChaosBonus.TimedBonus, ChaosBonus.tbDifficulty, (short) 10, (short) 10, (short) (ChaosGraphics.PW - 10), (short) MaxY, 1);
        cLevel = chaosBase.level[Zone.Chaos.ordinal()];
        dv = (short) trigo.SQRT(cLevel);
        if ((dv * dv == cLevel) && ((cLevel >= 9) || (chaosBase.level[Zone.Castle.ordinal()] > 1))) {
            c.set(50 + chaosBase.pLife * 4);
            chaosObjects.PutChaosObjs(Anims.ALIEN1, (short) ChaosAlien.aHospital, c.get(), (short) 10, (short) 10, (short) (ChaosGraphics.PW - 10), (short) MaxY, 1);
        }
        chaosObjects.PutObj(Anims.ALIEN2, (short) ChaosCreator.cController, 0, (short) 0, (short) 0);
        chaosObjects.PutPlayer((short) (ChaosGraphics.SOW / 2 - 1), (short) (ChaosGraphics.SOH - 2));
        if ((cLevel == 1) && (chaosBase.level[Zone.Castle.ordinal()] == 1)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Just warming up"), String.class), (short) ChaosActions.statPos, (short) 3);
        } else if ((cLevel == 5)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("The drawer may help you"), String.class), (short) ChaosActions.statPos, (short) 3);
        } else if ((cLevel == 7)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Too easy?"), String.class), (short) ChaosActions.statPos, (short) 3);
        } else if ((cLevel == 9)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Something new"), String.class), (short) ChaosActions.statPos, (short) 3);
        } else if ((cLevel == 17) && (chaosBase.score < 1000)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Still a long way before"), String.class), (short) ChaosActions.statPos, (short) 3);
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Father Alien"), String.class), (short) ChaosActions.moneyPos, (short) 3);
        } else if ((cLevel == 50)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("To be or not to be"), String.class), (short) ChaosActions.statPos, (short) 3);
        } else if ((cLevel == 37)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Getting harder"), String.class), (short) ChaosActions.statPos, (short) 3);
        } else if ((cLevel == 79)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Coming to the end"), String.class), (short) ChaosActions.statPos, (short) 3);
        } else if ((cLevel == 98)) {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Prepare yourself for level 100"), String.class), (short) ChaosActions.statPos, (short) 3);
        }
    }

    public void MakeCastle() {
        // VAR
        short x = 0;
        short y = 0;
        int c = 0;
        int lvl = 0;

        InitMsg();
        for (x = 0; x <= 127; x++) {
            for (y = 0; y <= 126; y++) {
                chaosGraphics.castle[y][x] = BackNone;
            }
        }
        chaosBase.water = false;
        chaosBase.snow = false;
        chaos1Zone.flipVert = true;
        chaos1Zone.flipHorz = true;
        chaos1Zone.rotate = true;
        chaos1Zone.pLife2 = (short) (chaosBase.pLife * 2);
        chaos1Zone.pLife3 = (short) (chaosBase.pLife * 3);
        chaos1Zone.pLife4 = (short) (chaos1Zone.pLife2 + 18 + chaosBase.difficulty * 2);
        chaosDual.InstallDual();
        for (x = 0; x <= 127; x++) {
            for (y = 0; y <= 126; y++) {
                chaosGraphics.castle[y][x] = 0;
            }
        }
        if (chaosBase.zone == Zone.Chaos) {
            chaos1Zone.flipVert = false;
            chaos1Zone.flipHorz = false;
            chaos1Zone.rotate = false;
            MakeChaos();
        } else if (chaosBase.zone == Zone.Castle) {
            switch (chaosBase.level[Zone.Castle.ordinal()]) {
                case 1 -> chaos1Zone.Entry();
                case 2 -> chaos1Zone.Groove();
                case 3 -> chaos1Zone.Garden();
                case 4 -> chaos1Zone.Lake();
                case 5 -> chaos1Zone.Site();
                case 6 -> chaos1Zone.GhostCastle();
                case 7 -> chaos1Zone.Machinery();
                case 8 -> chaos1Zone.IceRink();
                case 9 -> chaos1Zone.Factory();
                case 10 -> chaos1Zone.Labyrinth();
                case 11 -> chaos2Zone.Rooms();
                case 12 -> chaos2Zone.Yard();
                case 13 -> chaos2Zone.Antarctica();
                case 14 -> chaos2Zone.Forest();
                case 15 -> chaos2Zone.ZCastle();
                case 16 -> chaos2Zone.Lights();
                case 17 -> chaos2Zone.Plain();
                case 18 -> chaos2Zone.UnderWater();
                case 19 -> chaos2Zone.Assembly();
                case 20 -> chaos2Zone.Jungle();
                default -> throw new RuntimeException("Unhandled CASE value " + chaosBase.level[Zone.Castle.ordinal()]);
            }
        } else if (chaosBase.zone == Zone.Family) {
            chaos1Zone.flipVert = false;
            chaos1Zone.flipHorz = false;
            chaos1Zone.rotate = false;
            switch (chaosBase.level[Zone.Family.ordinal()]) {
                case 1 -> Brother();
                case 2 -> Sister();
                case 3 -> Mother();
                case 4 -> Father();
                case 5 -> Master();
                case 6 -> Illusion();
                case 7 -> Kids();
                case 8 -> Parents();
                case 9 -> Masters();
                case 10 -> Final();
                default -> throw new RuntimeException("Unhandled CASE value " + chaosBase.level[Zone.Family.ordinal()]);
            }
            if (chaosBase.level[Zone.Family.ordinal()] == 10)
                chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Good luck !"), String.class), (short) ChaosActions.lifePos, (short) 4);
            else
                chaosActions.PopMessage(Runtime.castToRef(languages.ADL("* PANIC *"), String.class), (short) ChaosActions.lifePos, (short) 4);
        } else {
            lvl = chaosBase.level[Zone.Special.ordinal()];
            if (lvl % 24 == 0) {
                Panic();
            } else if (lvl % 8 == 0) {
                Winter();
            } else if (lvl % 4 == 0) {
                chaosActions.PopMessage(Runtime.castToRef(languages.ADL("That's where you'll end"), String.class), (short) ChaosActions.lifePos, (short) 4);
                Graveyard();
            } else if (lvl % 2 == 0) {
                Spider();
            } else {
                BabyAliens();
            }
        }
        if (chaos1Zone.flipVert && (trigo.RND() % 2 == 0))
            chaosGenerator.FlipVert();
        if (chaos1Zone.flipHorz && (trigo.RND() % 2 == 0))
            chaosGenerator.FlipHorz();
        if (chaos1Zone.rotate && (trigo.RND() % 2 == 0))
            chaosGenerator.Rotate();
        chaosActions.GetCenter(chaosBase.mainPlayer, new Runtime.FieldRef<>(chaosGraphics::getBackpx, chaosGraphics::setBackpx), new Runtime.FieldRef<>(chaosGraphics::getBackpy, chaosGraphics::setBackpy));
        chaosGraphics.backpx -= ChaosGraphics.PW / 2;
        chaosGraphics.backpy -= ChaosGraphics.PH / 2;
        chaosObjects.FlushMarks();
        if (chaosBase.water && (chaosBase.zone != Zone.Chaos)) {
            if (chaosGraphics.dualpf) {
                for (c = 0; c <= 15; c++) {
                    { // WITH
                        ChaosGraphics.Palette _palette = chaosGraphics.dualPalette[c];
                        chaosGraphics.WaterPalette(new Runtime.FieldRef<>(_palette::getRed, _palette::setRed), new Runtime.FieldRef<>(_palette::getGreen, _palette::setGreen), new Runtime.FieldRef<>(_palette::getBlue, _palette::setBlue));
                    }
                    { // WITH
                        ChaosGraphics.Palette _palette = chaosGraphics.dualCycle[c];
                        chaosGraphics.WaterPalette(new Runtime.FieldRef<>(_palette::getRed, _palette::setRed), new Runtime.FieldRef<>(_palette::getGreen, _palette::setGreen), new Runtime.FieldRef<>(_palette::getBlue, _palette::setBlue));
                    }
                }
            }
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Warning: you are under water"), String.class), (short) ChaosActions.statPos, (short) 3);
            chaosObjects.PutObj(Anims.ALIEN2, (short) ChaosCreator.cController, 0, (short) 0, (short) 0);
        }
        if (chaosBase.snow)
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Warning: you are running on ice"), String.class), (short) ChaosActions.moneyPos, (short) 3);
        chaosImages.InitPalette();
    }


    // Support

    private static ChaosLevels instance;

    public static ChaosLevels instance() {
        if (instance == null)
            new ChaosLevels(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
    }

    public void close() {
    }

}
