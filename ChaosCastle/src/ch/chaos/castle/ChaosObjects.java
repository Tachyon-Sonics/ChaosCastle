package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBonus.Moneys;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


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
        public boolean invoke(int arg1, int arg2);
    }

    @FunctionalInterface
    public static interface RandomProc { // PROCEDURE Type
        public int invoke(int arg1);
    }

    @FunctionalInterface
    public static interface ChooseProc { // PROCEDURE Type
        public int invoke(int arg1, int arg2);
    }


    // TYPE

    @FunctionalInterface
    private static interface FillObj_PutObjProc { // PROCEDURE Type
        public void invoke(Anims arg1, int arg2, int arg3, int arg4, int arg5);
    }


    // VAR

    private int onlyValue;
    private int sx;
    private int sy;
    private int ex;
    private int ey;


    public int getOnlyValue() {
        return this.onlyValue;
    }

    public void setOnlyValue(int onlyValue) {
        this.onlyValue = onlyValue;
    }

    public int getSx() {
        return this.sx;
    }

    public void setSx(int sx) {
        this.sx = sx;
    }

    public int getSy() {
        return this.sy;
    }

    public void setSy(int sy) {
        this.sy = sy;
    }

    public int getEx() {
        return this.ex;
    }

    public void setEx(int ex) {
        this.ex = ex;
    }

    public int getEy() {
        return this.ey;
    }

    public void setEy(int ey) {
        this.ey = ey;
    }


    // PROCEDURE

    public int Rnd(int range) {
        // VAR
        int mod = 0;

        mod = range;
        return trigo.RND() % mod;
    }

    public final RandomProc Rnd_ref = this::Rnd;

    public int ExpRandom(int range) {
        // VAR
        int cnt = 0;

        cnt = range;
        while ((cnt > 2) && (trigo.RND() % 16 < 7)) {
            cnt = cnt / 2;
        }
        return Rnd(cnt);
    }

    public final RandomProc ExpRandom_ref = this::ExpRandom;

    public void Set(int x, int y) {
        chaosGraphics.castle[y][x] = ChaosGraphics.NbBackground;
    }

    public void Reset(int x, int y) {
        chaosGraphics.castle[y][x] = 0;
    }

    public int Get(int x, int y) {
        return chaosGraphics.castle[y][x] % 64;
    }

    public void Put(int x, int y, int v) {
        chaosGraphics.castle[y][x] = v;
    }

    public void Mark(int x, int y) {
        // VAR
        Runtime.IRef<Integer> v = null;

        v = new Runtime.ArrayElementRef<>(chaosGraphics.castle[y], x);
        if (v.get() < 64)
            v.set(v.get() + 64);
    }

    public boolean Marked(int x, int y) {
        return chaosGraphics.castle[y][x] >= 64;
    }

    public void FlushMarks() {
        // VAR
        Runtime.IRef<Integer> v = null;
        int x = 0;
        int y = 0;

        for (y = 0; y < chaosGraphics.castleHeight; y++) {
            for (x = 0; x < chaosGraphics.castleWidth; x++) {
                v = new Runtime.ArrayElementRef<>(chaosGraphics.castle[y], x);
                v.set(v.get() % 64);
            }
        }
    }

    public void Clear(int w, int h) {
        chaosGraphics.castleWidth = w;
        chaosGraphics.castleHeight = h;
        chaosGraphics.gameWidth = w * ChaosGraphics.BW;
        chaosGraphics.gameHeight = h * ChaosGraphics.BH;
    }

    public void Cadre(int w, int h) {
        // VAR
        int i = 0;

        chaosGraphics.castleWidth = w;
        chaosGraphics.castleHeight = h;
        chaosGraphics.gameWidth = w * ChaosGraphics.BW;
        chaosGraphics.gameHeight = h * ChaosGraphics.BH;
        w--;
        h--;
        for (i = 0; i <= w; i++) {
            Set(i, 0);
            Set(i, h);
        }
        for (i = 0; i <= h; i++) {
            Set(0, i);
            Set(w, i);
        }
    }

    public boolean All(int px, int py) {
        return true;
    }

    public final FilterProc All_ref = this::All;

    public boolean OnlyBackground(int px, int py) {
        return Get(px, py) < ChaosGraphics.NbClear;
    }

    public final FilterProc OnlyBackground_ref = this::OnlyBackground;

    public boolean OnlyWall(int px, int py) {
        return Get(px, py) >= ChaosGraphics.NbBackground;
    }

    public final FilterProc OnlyWall_ref = this::OnlyWall;

    public void SetOnlyValue(int val) {
        onlyValue = val;
    }

    public boolean OnlyValue(int px, int py) {
        return Get(px, py) == onlyValue;
    }

    public void Fill(int sx, int sy, int ex, int ey, int val) {
        // VAR
        int x = 0;
        int y = 0;

        for (y = sy; y <= ey; y++) {
            for (x = sx; x <= ex; x++) {
                Put(x, y, val);
            }
        }
    }

    public void FillCond(int sx, int sy, int ex, int ey, FilterProc Filter, int val) {
        // VAR
        int x = 0;
        int y = 0;

        for (y = sy; y <= ey; y++) {
            for (x = sx; x <= ex; x++) {
                if (Filter.invoke(x, y))
                    Put(x, y, val);
            }
        }
    }

    public void FillRandom(int sx, int sy, int ex, int ey, int min, int max, FilterProc Filter, RandomProc Random) {
        // VAR
        int x = 0;
        int y = 0;
        int range = 0;

        range = max - min + 1;
        for (y = sy; y <= ey; y++) {
            for (x = sx; x <= ex; x++) {
                if (Filter.invoke(x, y))
                    Put(x, y, Random.invoke(range) + min);
            }
        }
    }

    public void FillChoose(int sx, int sy, int ex, int ey, FilterProc Filter, ChooseProc Choose) {
        // VAR
        int x = 0;
        int y = 0;

        for (y = sy; y <= ey; y++) {
            for (x = sx; x <= ex; x++) {
                if (Filter.invoke(x, y))
                    Put(x, y, Choose.invoke(x, y));
            }
        }
    }

    public void PutRandom(int sx, int sy, int ex, int ey, FilterProc Filter, int val, int cnt) {
        // VAR
        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;
        int timeOut = 0;

        w = ex - sx + 1;
        h = ey - sy + 1;
        while (cnt > 0) {
            timeOut = 50;
            do {
                timeOut--;
                x = Rnd(w) + sx;
                y = Rnd(h) + sy;
            } while (!((timeOut == 0) || Filter.invoke(x, y)));
            if (timeOut != 0)
                Put(x, y, val);
            cnt--;
        }
    }

    public void Rect(int nsx, int nsy, int nex, int ney) {
        sx = nsx;
        sy = nsy;
        ex = nex;
        ey = ney;
    }

    public void PutObj(Anims kind, int subKind, int stat, int px, int py) {
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

    public void PutBlockObj(Anims kind, int subKind, int stat, int px, int py) {
        if (Get(px, py) < ChaosGraphics.NbBackground) {
            Mark(px, py);
            PutObj(kind, subKind, stat, px * ChaosGraphics.BW + ChaosGraphics.BW / 2, py * ChaosGraphics.BH + ChaosGraphics.BH / 2);
        }
    }

    public final FillObj_PutObjProc PutBlockObj_ref = this::PutBlockObj;

    public void PutFineObj(Anims kind, int subKind, int stat, int px, int py, int dx, int dy) {
        // VAR
        int x = 0;
        int y = 0;

        Mark(px, py);
        x = px * ChaosGraphics.BW + ChaosGraphics.BW / 4 + (ChaosGraphics.BW / 2) * dx;
        y = py * ChaosGraphics.BH + ChaosGraphics.BH / 4 + (ChaosGraphics.BH / 2) * dy;
        PutObj(kind, subKind, stat, x, y);
    }

    public void Put4Objs(Anims kind, int subKind, int stat, int px, int py) {
        PutFineObj(kind, subKind, stat, px, py, 0, 0);
        PutFineObj(kind, subKind, stat, px, py, 0, 1);
        PutFineObj(kind, subKind, stat, px, py, 1, 0);
        PutFineObj(kind, subKind, stat, px, py, 1, 1);
    }

    public final FillObj_PutObjProc Put4Objs_ref = this::Put4Objs;

    public void PutRandomObjs(Anims kind, int subKind, int stat, int count) {
        // VAR
        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;
        int timeOut = 0;

        w = ex - sx + 1;
        h = ey - sy + 1;
        while (count > 0) {
            timeOut = 50;
            do {
                timeOut--;
                x = Rnd(w) + sx;
                y = Rnd(h) + sy;
            } while (!(((Get(x, y) < ChaosGraphics.NbClear) && (!Marked(x, y))) || (timeOut == 0)));
            PutBlockObj(kind, subKind, stat, x, y);
            count--;
        }
    }

    public boolean FindIsolatedPlace(int mxw, /* VAR */ Runtime.IRef<Integer> x, /* VAR */ Runtime.IRef<Integer> y) {
        // VAR
        int dx = 0;
        int dy = 0;
        int w = 0;
        int h = 0;
        int px = 0;
        int py = 0;
        int timeOut = 0;
        int walls = 0;

        w = ex - sx + 1;
        h = ey - sy + 1;
        timeOut = 50;
        do {
            timeOut--;
            x.set(Rnd(w) + sx);
            y.set(Rnd(h) + sy);
            walls = 0;
            for (dy = -1; dy <= 1; dy++) {
                for (dx = -1; dx <= 1; dx++) {
                    px = x.get() + dx;
                    py = y.get() + dy;
                    if ((Get(px, py) >= ChaosGraphics.NbBackground) || Marked(px, py))
                        walls++;
                }
            }
        } while (!(((walls <= mxw) && (Get(x.get(), y.get()) < ChaosGraphics.NbClear) && (!Marked(x.get(), y.get()))) || (timeOut == 0)));
        return timeOut != 0;
    }

    public void PutIsolated(int mnc, int mxc, int mns, int mxs, int val) {
        // VAR
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int nsx = 0;
        int nsy = 0;
        int sz = 0;
        int count = 0;

        sz = Rnd(mxs - mns + 1) + mns;
        nsx = Rnd(ex - sx - sz + 2) + sx;
        nsy = Rnd(ey - sy - sz + 2) + sy;
        Rect(nsx, nsy, nsx + sz - 1, nsy + sz - 1);
        count = trigo.RND() % (mxc - mnc + 1) + mnc;
        while (count > 0) {
            if (FindIsolatedPlace(0, x, y))
                Put(x.get(), y.get(), val);
            count--;
        }
    }

    public void PutIsolatedObjs(Anims kind, int subKind, int mns, int mxs, int mxw, int count) {
        // VAR
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int stat = 0;

        while (count > 0) {
            if (FindIsolatedPlace(mxw, x, y)) {
                stat = trigo.RND() % (mxs - mns + 1) + mns;
                Mark(x.get(), y.get());
                x.set(x.get() * ChaosGraphics.BW + ChaosGraphics.BW / 4 + Rnd(ChaosGraphics.BW / 2));
                y.set(y.get() * ChaosGraphics.BH + ChaosGraphics.BH / 4 + Rnd(ChaosGraphics.BH / 2));
                PutObj(kind, subKind, stat, x.get(), y.get());
            }
            count--;
        }
    }

    public void PutDeltaObjs(Anims kind, int subKind, int stat, int dx, int dy, int count) {
        // VAR
        int lx = 0;
        int ly = 0;
        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;
        int timeOut = 0;

        w = ex - sx + 1;
        h = ey - sy + 1;
        while (count > 0) {
            timeOut = 50;
            do {
                timeOut--;
                x = Rnd(w) + sx;
                y = Rnd(h) + sy;
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

    public void PutGridObjs(Anims kind, int subKind, int stat, int sx, int sy, int dx, int dy, int cx, int cy) {
        // VAR
        int x = 0;
        int y = 0;

        for (y = 0; y <= cy; y++) {
            for (x = 0; x <= cx; x++) {
                PutBlockObj(kind, subKind, stat, sx + x * dx, sy + y * dy);
            }
        }
    }

    public void PutRndStatObjs(Anims kind, int subKind, int mnstat, int mxstat, int count) {
        // VAR
        int stat = 0;

        while (count > 0) {
            stat = mnstat + trigo.RND() % (mxstat - mnstat + 1);
            PutRandomObjs(kind, subKind, stat, 1);
            count--;
        }
    }

    public void PutChaosObjs(Anims kind, int subKind, int stat, int sx, int sy, int ex, int ey, int count) {
        // VAR
        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;
        int timeOut = 0;

        w = ex - sx + 1;
        h = ey - sy + 1;
        while (count > 0) {
            timeOut = 50;
            do {
                timeOut--;
                x = Rnd(w) + sx;
                y = Rnd(h) + sy;
            } while (!((Get(x / ChaosGraphics.BW, y / ChaosGraphics.BH) < ChaosGraphics.NbClear) || (timeOut == 0)));
            if (timeOut != 0)
                PutObj(kind, subKind, stat, x, y);
            count--;
        }
    }

    public void PutChaosChain(Anims kind, int subKind, /* VAR */ Runtime.IRef<Integer> start, int step, int sx, int sy, int ex, int ey, int count) {
        while (count > 0) {
            PutChaosObjs(kind, subKind, start.get(), sx, sy, ex, ey, 1);
            start.dec(step);
            count--;
        }
    }

    public void FillObj(Anims kind, int subKind, int stat, int sx, int sy, int ex, int ey, boolean fine) {
        // VAR
        FillObj_PutObjProc PutIt = null;
        int x = 0;
        int y = 0;

        if (fine)
            PutIt = Put4Objs_ref;
        else
            PutIt = PutBlockObj_ref;
        for (y = sy; y <= ey; y++) {
            for (x = sx; x <= ex; x++) {
                PutIt.invoke(kind, subKind, stat, x, y);
            }
        }
    }

    public void PutPlayer(int px, int py) {
        if (chaosBase.pLife == 0)
            chaosBase.pLife = 1;
        PutBlockObj(Anims.PLAYER, 0, 0, px, py);
    }

    public void PutExit(int px, int py) {
        PutBlockObj(Anims.BONUS, ChaosBonus.TimedBonus, ChaosBonus.tbExit, px, py);
    }

    public void PutKamikaze(int stat, int count) {
        // VAR
        int dx = 0;
        int dy = 0;

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
        PutDeltaObjs(Anims.ALIEN1, ChaosAlien.aKamikaze, stat, dx, dy, count);
    }

    public void PutPic(int stat, int count) {
        // VAR
        int dx = 0;

        if (((stat % 2) != 0))
            dx = 1;
        else
            dx = -1;
        PutDeltaObjs(Anims.ALIEN1, ChaosAlien.aPic, stat, dx, 0, count);
    }

    public void PutBlockBonus(int stat, int px, int py) {
        PutBlockObj(Anims.BONUS, ChaosBonus.TimedBonus, stat, px, py);
    }

    public void PutTBonus(int stat, int count) {
        PutRandomObjs(Anims.BONUS, ChaosBonus.TimedBonus, stat, count);
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
        PutChaosObjs(Anims.BONUS, ChaosBonus.Money, Moneys.st.ordinal(), sx * ChaosGraphics.BW, sy * ChaosGraphics.BH, ex * ChaosGraphics.BW, ey * ChaosGraphics.BH, count);
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
            PutRandomObjs(Anims.BONUS, ChaosBonus.Money, money.ordinal(), 1);
            count--;
        }
    }

    public void PutExtraPower(int min, int px, int py) {
        if (chaosBase.powerCountDown > min)
            PutBlockObj(Anims.SMARTBONUS, ChaosSmartBonus.sbExtraPower, 0, px, py);
    }

    public void PutExtraLife(int px, int py) {
        PutBlockObj(Anims.SMARTBONUS, ChaosSmartBonus.sbExtraLife, 0, px, py);
    }

    public void PutRAlien1(int subK, int mins, int maxs, int count) {
        PutRndStatObjs(Anims.ALIEN1, subK, mins, maxs, count);
    }

    public void PutAlien1(int subK, int stat, int count) {
        PutRandomObjs(Anims.ALIEN1, subK, stat, count);
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
        PutRndStatObjs(Anims.ALIEN2, subK, mins, maxs, count);
    }

    public void PutAlien2(int subK, int stat, int count) {
        PutRandomObjs(Anims.ALIEN2, subK, stat, count);
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
        PutRandomObjs(Anims.DEADOBJ, subK, stat, count);
    }

    public void PutBubbleMaker(int stat, int px, int py) {
        PutBlockObj(Anims.DEADOBJ, ChaosDObj.doBubbleMaker, stat, px, py);
    }

    public void PutMagnetR(int maxs, int count) {
        PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doMagnetR, 0, maxs, 1, count);
    }

    public void PutMagnetA(int maxs, int count) {
        PutIsolatedObjs(Anims.DEADOBJ, ChaosDObj.doMagnetA, 0, maxs, 1, count);
    }

    public void PutMachine(int subK, int mins, int maxs, int count) {
        PutRndStatObjs(Anims.MACHINE, subK, mins, maxs, count);
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
