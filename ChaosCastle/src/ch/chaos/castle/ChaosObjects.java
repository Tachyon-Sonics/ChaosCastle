package ch.chaos.castle;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus.Moneys;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;
import java.util.EnumSet;


public class ChaosObjects {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosGraphics chaosGraphics;
    private final Trigo trigo;


    private ChaosObjects() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosGraphics = ChaosGraphics.instance();
        trigo = Trigo.instance();
    }


    // TYPE

    @FunctionalInterface
    public static interface FilterProc { // PROCEDURE Type
        public boolean invoke(short arg1, short arg2);
    }

    @FunctionalInterface
    public static interface RandomProc { // PROCEDURE Type
        public short invoke(short arg1);
    }

    @FunctionalInterface
    public static interface ChooseProc { // PROCEDURE Type
        public short invoke(short arg1, short arg2);
    }


    // TYPE

    @FunctionalInterface
    private static interface FillObj_PutObjProc { // PROCEDURE Type
        public void invoke(Anims arg1, short arg2, int arg3, short arg4, short arg5);
    }


    // VAR

    private short onlyValue;
    private short sx;
    private short sy;
    private short ex;
    private short ey;


    public short getOnlyValue() {
        return this.onlyValue;
    }

    public void setOnlyValue(short onlyValue) {
        this.onlyValue = onlyValue;
    }

    public short getSx() {
        return this.sx;
    }

    public void setSx(short sx) {
        this.sx = sx;
    }

    public short getSy() {
        return this.sy;
    }

    public void setSy(short sy) {
        this.sy = sy;
    }

    public short getEx() {
        return this.ex;
    }

    public void setEx(short ex) {
        this.ex = ex;
    }

    public short getEy() {
        return this.ey;
    }

    public void setEy(short ey) {
        this.ey = ey;
    }


    // PROCEDURE

    public short Rnd(short range) {
        // VAR
        int mod = 0;

        mod = range;
        return (short) (trigo.RND() % mod);
    }

    public short ExpRandom(short range) {
        // VAR
        short cnt = 0;

        cnt = range;
        while ((cnt > 2) && (trigo.RND() % 16 < 7)) {
            cnt = (short) (cnt / 2);
        }
        return Rnd(cnt);
    }

    public void Set(short x, short y) {
        chaosGraphics.castle[y][x] = ChaosGraphics.NbBackground;
    }

    public void Reset(short x, short y) {
        chaosGraphics.castle[y][x] = 0;
    }

    public short Get(short x, short y) {
        return (short) (chaosGraphics.castle[y][x] % 64);
    }

    public void Put(short x, short y, short v) {
        chaosGraphics.castle[y][x] = v;
    }

    public void Mark(short x, short y) {
        // VAR
        Runtime.IRef<Short> v = null;

        v = new Runtime.ArrayElementRef<>(chaosGraphics.castle[y], x);
        if (v.get() < 64)
            v.set((short) (v.get() + 64));
    }

    public boolean Marked(short x, short y) {
        return chaosGraphics.castle[y][x] >= 64;
    }

    public void FlushMarks() {
        // VAR
        Runtime.IRef<Short> v = null;
        short x = 0;
        short y = 0;

        for (y = 0; y < chaosGraphics.castleHeight; y++) {
            for (x = 0; x < chaosGraphics.castleWidth; x++) {
                v = new Runtime.ArrayElementRef<>(chaosGraphics.castle[y], x);
                v.set((short) (v.get() % 64));
            }
        }
    }

    public void Clear(short w, short h) {
        chaosGraphics.castleWidth = w;
        chaosGraphics.castleHeight = h;
        chaosGraphics.gameWidth = (short) (w * ChaosGraphics.BW);
        chaosGraphics.gameHeight = (short) (h * ChaosGraphics.BH);
    }

    public void Cadre(short w, short h) {
        // VAR
        short i = 0;

        chaosGraphics.castleWidth = w;
        chaosGraphics.castleHeight = h;
        chaosGraphics.gameWidth = (short) (w * ChaosGraphics.BW);
        chaosGraphics.gameHeight = (short) (h * ChaosGraphics.BH);
        w--;
        h--;
        for (i = 0; i <= w; i++) {
            Set(i, (short) 0);
            Set(i, h);
        }
        for (i = 0; i <= h; i++) {
            Set((short) 0, i);
            Set(w, i);
        }
    }

    public boolean All(short px, short py) {
        return true;
    }

    public boolean OnlyBackground(short px, short py) {
        return Get(px, py) < ChaosGraphics.NbClear;
    }

    public boolean OnlyWall(short px, short py) {
        return Get(px, py) >= ChaosGraphics.NbBackground;
    }

    public void SetOnlyValue(short val) {
        onlyValue = val;
    }

    public boolean OnlyValue(short px, short py) {
        return Get(px, py) == onlyValue;
    }

    public void Fill(short sx, short sy, short ex, short ey, short val) {
        // VAR
        short x = 0;
        short y = 0;

        for (y = sy; y <= ey; y++) {
            for (x = sx; x <= ex; x++) {
                Put(x, y, val);
            }
        }
    }

    public void FillCond(short sx, short sy, short ex, short ey, FilterProc Filter, short val) {
        // VAR
        short x = 0;
        short y = 0;

        for (y = sy; y <= ey; y++) {
            for (x = sx; x <= ex; x++) {
                if (Filter.invoke(x, y))
                    Put(x, y, val);
            }
        }
    }

    public void FillRandom(short sx, short sy, short ex, short ey, short min, short max, FilterProc Filter, RandomProc Random) {
        // VAR
        short x = 0;
        short y = 0;
        int range = 0;

        range = max - min + 1;
        for (y = sy; y <= ey; y++) {
            for (x = sx; x <= ex; x++) {
                if (Filter.invoke(x, y))
                    Put(x, y, (short) (Random.invoke((short) range) + min));
            }
        }
    }

    public void FillChoose(short sx, short sy, short ex, short ey, FilterProc Filter, ChooseProc Choose) {
        // VAR
        short x = 0;
        short y = 0;

        for (y = sy; y <= ey; y++) {
            for (x = sx; x <= ex; x++) {
                if (Filter.invoke(x, y))
                    Put(x, y, Choose.invoke(x, y));
            }
        }
    }

    public void PutRandom(short sx, short sy, short ex, short ey, FilterProc Filter, short val, short cnt) {
        // VAR
        short x = 0;
        short y = 0;
        short w = 0;
        short h = 0;
        int timeOut = 0;

        w = (short) (ex - sx + 1);
        h = (short) (ey - sy + 1);
        while (cnt > 0) {
            timeOut = 50;
            do {
                timeOut--;
                x = (short) (Rnd(w) + sx);
                y = (short) (Rnd(h) + sy);
            } while (!((timeOut == 0) || Filter.invoke(x, y)));
            if (timeOut != 0)
                Put(x, y, val);
            cnt--;
        }
    }

    public void Rect(short nsx, short nsy, short nex, short ney) {
        sx = nsx;
        sy = nsy;
        ex = nex;
        ey = ney;
    }

    public void PutObj(Anims kind, short subKind, int stat, short px, short py) {
        // VAR
        ChaosBase.Obj obj = null;

        if (ChaosBase.AnimAlienSet.contains(kind))
            obj = chaosActions.CreateObj(kind, subKind, px, py, 0, stat);
        else
            obj = chaosActions.CreateObj(kind, subKind, px, py, stat, 0);
        if (obj == null)
            throw new HaltException();
        if (kind == Anims.PLAYER)
            chaosBase.mainPlayer = obj;
    }

    public void PutBlockObj(Anims kind, short subKind, int stat, short px, short py) {
        if (Get(px, py) < ChaosGraphics.NbBackground) {
            Mark(px, py);
            PutObj(kind, subKind, stat, (short) (px * ChaosGraphics.BW + ChaosGraphics.BW / 2), (short) (py * ChaosGraphics.BH + ChaosGraphics.BH / 2));
        }
    }

    public void PutFineObj(Anims kind, short subKind, int stat, short px, short py, short dx, short dy) {
        // VAR
        short x = 0;
        short y = 0;

        Mark(px, py);
        x = (short) (px * ChaosGraphics.BW + ChaosGraphics.BW / 4 + (ChaosGraphics.BW / 2) * dx);
        y = (short) (py * ChaosGraphics.BH + ChaosGraphics.BH / 4 + (ChaosGraphics.BH / 2) * dy);
        PutObj(kind, subKind, stat, x, y);
    }

    public void Put4Objs(Anims kind, short subKind, int stat, short px, short py) {
        PutFineObj(kind, subKind, stat, px, py, (short) 0, (short) 0);
        PutFineObj(kind, subKind, stat, px, py, (short) 0, (short) 1);
        PutFineObj(kind, subKind, stat, px, py, (short) 1, (short) 0);
        PutFineObj(kind, subKind, stat, px, py, (short) 1, (short) 1);
    }

    public void PutRandomObjs(Anims kind, short subKind, int stat, int count) {
        // VAR
        short x = 0;
        short y = 0;
        short w = 0;
        short h = 0;
        int timeOut = 0;

        w = (short) (ex - sx + 1);
        h = (short) (ey - sy + 1);
        while (count > 0) {
            timeOut = 50;
            do {
                timeOut--;
                x = (short) (Rnd(w) + sx);
                y = (short) (Rnd(h) + sy);
            } while (!(((Get(x, y) < ChaosGraphics.NbClear) && (!Marked(x, y))) || (timeOut == 0)));
            PutBlockObj(kind, subKind, stat, x, y);
            count--;
        }
    }

    public boolean FindIsolatedPlace(int mxw, /* VAR */ Runtime.IRef<Short> x, /* VAR */ Runtime.IRef<Short> y) {
        // VAR
        short dx = 0;
        short dy = 0;
        short w = 0;
        short h = 0;
        short px = 0;
        short py = 0;
        int timeOut = 0;
        int walls = 0;

        w = (short) (ex - sx + 1);
        h = (short) (ey - sy + 1);
        timeOut = 50;
        do {
            timeOut--;
            x.set((short) (Rnd(w) + sx));
            y.set((short) (Rnd(h) + sy));
            walls = 0;
            for (dy = -1; dy <= 1; dy++) {
                for (dx = -1; dx <= 1; dx++) {
                    px = (short) (x.get() + dx);
                    py = (short) (y.get() + dy);
                    if ((Get(px, py) >= ChaosGraphics.NbBackground) || Marked(px, py))
                        walls++;
                }
            }
        } while (!(((walls <= mxw) && (Get(x.get(), y.get()) < ChaosGraphics.NbClear) && (!Marked(x.get(), y.get()))) || (timeOut == 0)));
        return timeOut != 0;
    }

    public void PutIsolated(int mnc, int mxc, short mns, short mxs, short val) {
        // VAR
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short nsx = 0;
        short nsy = 0;
        short sz = 0;
        int count = 0;

        sz = (short) (Rnd((short) (mxs - mns + 1)) + mns);
        nsx = (short) (Rnd((short) (ex - sx - sz + 2)) + sx);
        nsy = (short) (Rnd((short) (ey - sy - sz + 2)) + sy);
        Rect(nsx, nsy, (short) (nsx + sz - 1), (short) (nsy + sz - 1));
        count = trigo.RND() % (mxc - mnc + 1) + mnc;
        while (count > 0) {
            if (FindIsolatedPlace(0, x, y))
                Put(x.get(), y.get(), val);
            count--;
        }
    }

    public void PutIsolatedObjs(Anims kind, short subKind, int mns, int mxs, int mxw, int count) {
        // VAR
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        int stat = 0;

        while (count > 0) {
            if (FindIsolatedPlace(mxw, x, y)) {
                stat = trigo.RND() % (mxs - mns + 1) + mns;
                Mark(x.get(), y.get());
                x.set((short) (x.get() * ChaosGraphics.BW + ChaosGraphics.BW / 4 + Rnd((short) (ChaosGraphics.BW / 2))));
                y.set((short) (y.get() * ChaosGraphics.BH + ChaosGraphics.BH / 4 + Rnd((short) (ChaosGraphics.BH / 2))));
                PutObj(kind, subKind, stat, x.get(), y.get());
            }
            count--;
        }
    }

    public void PutDeltaObjs(Anims kind, short subKind, int stat, short dx, short dy, int count) {
        // VAR
        short lx = 0;
        short ly = 0;
        short x = 0;
        short y = 0;
        short w = 0;
        short h = 0;
        int timeOut = 0;

        w = (short) (ex - sx + 1);
        h = (short) (ey - sy + 1);
        while (count > 0) {
            timeOut = 50;
            do {
                timeOut--;
                x = (short) (Rnd(w) + sx);
                y = (short) (Rnd(h) + sy);
            } while (!(((Get(x, y) < ChaosGraphics.NbClear) && !Marked(x, y)) || (timeOut == 0)));
            if (timeOut != 0) {
                do {
                    lx = x;
                    ly = y;
                    x += dx;
                    y += dy;
                } while (!((Get(x, y) >= ChaosGraphics.NbClear) || Marked(x, y)));
                PutBlockObj(kind, subKind, stat, lx, ly);
            }
            count--;
        }
    }

    public void PutGridObjs(Anims kind, short subKind, int stat, short sx, short sy, short dx, short dy, short cx, short cy) {
        // VAR
        short x = 0;
        short y = 0;

        for (y = 0; y <= cy; y++) {
            for (x = 0; x <= cx; x++) {
                PutBlockObj(kind, subKind, stat, (short) (sx + x * dx), (short) (sy + y * dy));
            }
        }
    }

    public void PutRndStatObjs(Anims kind, short subKind, int mnstat, int mxstat, int count) {
        // VAR
        int stat = 0;

        while (count > 0) {
            stat = mnstat + trigo.RND() % (mxstat - mnstat + 1);
            PutRandomObjs(kind, subKind, stat, 1);
            count--;
        }
    }

    public void PutChaosObjs(Anims kind, short subKind, int stat, short sx, short sy, short ex, short ey, int count) {
        // VAR
        short x = 0;
        short y = 0;
        short w = 0;
        short h = 0;
        int timeOut = 0;

        w = (short) (ex - sx + 1);
        h = (short) (ey - sy + 1);
        while (count > 0) {
            timeOut = 50;
            do {
                timeOut--;
                x = (short) (Rnd(w) + sx);
                y = (short) (Rnd(h) + sy);
            } while (!((Get((short) (x / ChaosGraphics.BW), (short) (y / ChaosGraphics.BH)) < ChaosGraphics.NbClear) || (timeOut == 0)));
            if (timeOut != 0)
                PutObj(kind, subKind, stat, x, y);
            count--;
        }
    }

    public void PutChaosChain(Anims kind, short subKind, /* VAR */ Runtime.IRef<Integer> start, int step, short sx, short sy, short ex, short ey, int count) {
        while (count > 0) {
            PutChaosObjs(kind, subKind, start.get(), sx, sy, ex, ey, 1);
            start.dec(step);
            count--;
        }
    }

    public void FillObj(Anims kind, short subKind, int stat, short sx, short sy, short ex, short ey, boolean fine) {
        // VAR
        FillObj_PutObjProc PutIt = null;
        short x = 0;
        short y = 0;

        if (fine)
            PutIt = Runtime.proc(this::Put4Objs, "ChaosObjects.Put4Objs");
        else
            PutIt = Runtime.proc(this::PutBlockObj, "ChaosObjects.PutBlockObj");
        for (y = sy; y <= ey; y++) {
            for (x = sx; x <= ex; x++) {
                PutIt.invoke(kind, subKind, stat, x, y);
            }
        }
    }

    public void PutPlayer(short px, short py) {
        if (chaosBase.pLife == 0)
            chaosBase.pLife = 1;
        PutBlockObj(Anims.PLAYER, (short) 0, 0, px, py);
    }

    public void PutExit(short px, short py) {
        PutBlockObj(Anims.BONUS, (short) ChaosBonus.TimedBonus, ChaosBonus.tbExit, px, py);
    }

    public void PutKamikaze(int stat, int count) {
        // VAR
        short dx = 0;
        short dy = 0;

        switch (stat % 4) {
            case 0 -> {
                dx = -1;
                dy = -1;
            }
            case 1 -> {
                dx = 1;
                dy = -1;
            }
            case 2 -> {
                dx = -1;
                dy = 1;
            }
            case 3 -> {
                dx = 1;
                dy = 1;
            }
            default -> throw new RuntimeException("Unhandled CASE value " + stat % 4);
        }
        PutDeltaObjs(Anims.ALIEN1, (short) ChaosAlien.aKamikaze, stat, dx, dy, count);
    }

    public void PutPic(int stat, int count) {
        // VAR
        short dx = 0;

        if (((stat % 2) != 0))
            dx = 1;
        else
            dx = -1;
        PutDeltaObjs(Anims.ALIEN1, (short) ChaosAlien.aPic, stat, dx, (short) 0, count);
    }

    public void PutBlockBonus(int stat, short px, short py) {
        PutBlockObj(Anims.BONUS, (short) ChaosBonus.TimedBonus, stat, px, py);
    }

    public void PutTBonus(int stat, int count) {
        PutRandomObjs(Anims.BONUS, (short) ChaosBonus.TimedBonus, stat, count);
    }

    public void PutHospital(int count) {
        PutTBonus(ChaosBonus.tbHospital, count);
    }

    public void PutBullet(int count) {
        PutTBonus(ChaosBonus.tbBullet, count);
    }

    public void PutMagnet(int count) {
        PutTBonus(ChaosBonus.tbMagnet, count);
    }

    public void PutSleeper(int count) {
        PutTBonus(ChaosBonus.tbSleeper, count);
    }

    public void PutInvinsibility(int count) {
        PutTBonus(ChaosBonus.tbInvinsibility, count);
    }

    public void PutFreeFire(int count) {
        PutTBonus(ChaosBonus.tbFreeFire, count);
    }

    public void PutMaxPower(int count) {
        PutTBonus(ChaosBonus.tbMaxPower, count);
    }

    public void PutChaosSterling(int count) {
        PutChaosObjs(Anims.BONUS, (short) ChaosBonus.Money, Moneys.st.ordinal(), (short) (sx * ChaosGraphics.BW), (short) (sy * ChaosGraphics.BH), (short) (ex * ChaosGraphics.BW), (short) (ey * ChaosGraphics.BH), count);
    }

    public void PutMoney(EnumSet<Moneys> which, int count) {
        // VAR
        Moneys money = Moneys.m1;
        int c = 0;

        while (count > 0) {
            do {
                c = trigo.RND() % 6;
                money = Moneys.m1 /* MIN(Moneys) */;
                while (c > 0) {
                    c--;
                    money = Runtime.next(money);
                }
            } while (!which.contains(money));
            PutRandomObjs(Anims.BONUS, (short) ChaosBonus.Money, money.ordinal(), 1);
            count--;
        }
    }

    public void PutExtraPower(short min, short px, short py) {
        if (chaosBase.powerCountDown > min)
            PutBlockObj(Anims.SMARTBONUS, (short) ChaosSmartBonus.sbExtraPower, 0, px, py);
    }

    public void PutExtraLife(short px, short py) {
        PutBlockObj(Anims.SMARTBONUS, (short) ChaosSmartBonus.sbExtraLife, 0, px, py);
    }

    public void PutRAlien1(int subK, int mins, int maxs, int count) {
        PutRndStatObjs(Anims.ALIEN1, (short) subK, mins, maxs, count);
    }

    public void PutAlien1(int subK, int stat, int count) {
        PutRandomObjs(Anims.ALIEN1, (short) subK, stat, count);
    }

    public void PutAColor(int mins, int maxs, int count) {
        PutRAlien1(ChaosAlien.aColor, mins, maxs, count);
    }

    public void PutColor(int stat, int count) {
        PutAlien1(ChaosAlien.aColor, stat, count);
    }

    public void PutTrefle(int stat, int count) {
        PutAlien1(ChaosAlien.aTrefle, stat, count);
    }

    public void PutTri(int stat, int count) {
        PutAlien1(ChaosAlien.aTri, stat, count);
    }

    public void PutCartoon(int mins, int maxs, int count) {
        PutRAlien1(ChaosAlien.aCartoon, mins, maxs, count);
    }

    public void PutRAlien2(int subK, int mins, int maxs, int count) {
        PutRndStatObjs(Anims.ALIEN2, (short) subK, mins, maxs, count);
    }

    public void PutAlien2(int subK, int stat, int count) {
        PutRandomObjs(Anims.ALIEN2, (short) subK, stat, count);
    }

    public void PutCFour(int mins, int maxs, int count) {
        PutRAlien2(ChaosCreator.cFour, mins, maxs, count);
    }

    public void PutFour(int stat, int count) {
        PutAlien2(ChaosCreator.cFour, stat, count);
    }

    public void PutQuad(int stat, int count) {
        PutAlien2(ChaosCreator.cQuad, stat, count);
    }

    public void PutABox(int stat, int count) {
        PutAlien2(ChaosCreator.cAlienBox, stat, count);
    }

    public void PutNest(int stat, int count) {
        PutAlien2(ChaosCreator.cNest, stat, count);
    }

    public void PutCreatorR(int count) {
        PutAlien2(ChaosCreator.cCreatorR, 80, count);
    }

    public void PutCreatorC(int count) {
        PutAlien2(ChaosCreator.cCreatorC, 80, count);
    }

    public void PutDeadObj(int subK, int stat, int count) {
        PutRandomObjs(Anims.DEADOBJ, (short) subK, stat, count);
    }

    public void PutBubbleMaker(int stat, short px, short py) {
        PutBlockObj(Anims.DEADOBJ, (short) ChaosDObj.doBubbleMaker, stat, px, py);
    }

    public void PutMagnetR(int maxs, int count) {
        PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doMagnetR, 0, maxs, 1, count);
    }

    public void PutMagnetA(int maxs, int count) {
        PutIsolatedObjs(Anims.DEADOBJ, (short) ChaosDObj.doMagnetA, 0, maxs, 1, count);
    }

    public void PutMachine(int subK, int mins, int maxs, int count) {
        PutRndStatObjs(Anims.MACHINE, (short) subK, mins, maxs, count);
    }

    public void PutCannon3(int count) {
        PutMachine(ChaosMachine.mCannon3, 0, 0, count);
    }

    public void PutTurret(int count) {
        PutMachine(ChaosMachine.mTurret, 0, 0, count);
    }


    // Support

    private static ChaosObjects instance;

    public static ChaosObjects instance() {
        if (instance == null)
            new ChaosObjects(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
    }

    public void close() {
    }

}
