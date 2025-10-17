package ch.chaos.castle;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.Zone;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosFire {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosGraphics chaosGraphics;
    private final ChaosSounds chaosSounds;
    private final Memory memory;
    private final Trigo trigo;


    private ChaosFire() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosSounds = ChaosSounds.instance();
        memory = Memory.instance();
        trigo = Trigo.instance();
    }


    // VAR

    public ChaosSounds.Effect[] missileEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    public ChaosSounds.Effect[] flameEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    public ChaosSounds.Effect[] bombEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    public ChaosSounds.Effect[] createEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    public ChaosSounds.Effect[] huEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    public ChaosSounds.Effect[] poufEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    public ChaosSounds.Effect[] aieEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    public ChaosSounds.Effect[] koEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    public ChaosBase.Obj theFather /* POINTER */;
    public ChaosBase.Obj theMaster /* POINTER */;
    public ChaosBase.Obj theIllusion /* POINTER */;
    public boolean laugh;
    public boolean turn;


    public ChaosSounds.Effect[] getMissileEffect() {
        return this.missileEffect;
    }

    public void setMissileEffect(ChaosSounds.Effect[] missileEffect) {
        this.missileEffect = missileEffect;
    }

    public ChaosSounds.Effect[] getFlameEffect() {
        return this.flameEffect;
    }

    public void setFlameEffect(ChaosSounds.Effect[] flameEffect) {
        this.flameEffect = flameEffect;
    }

    public ChaosSounds.Effect[] getBombEffect() {
        return this.bombEffect;
    }

    public void setBombEffect(ChaosSounds.Effect[] bombEffect) {
        this.bombEffect = bombEffect;
    }

    public ChaosSounds.Effect[] getCreateEffect() {
        return this.createEffect;
    }

    public void setCreateEffect(ChaosSounds.Effect[] createEffect) {
        this.createEffect = createEffect;
    }

    public ChaosSounds.Effect[] getHuEffect() {
        return this.huEffect;
    }

    public void setHuEffect(ChaosSounds.Effect[] huEffect) {
        this.huEffect = huEffect;
    }

    public ChaosSounds.Effect[] getPoufEffect() {
        return this.poufEffect;
    }

    public void setPoufEffect(ChaosSounds.Effect[] poufEffect) {
        this.poufEffect = poufEffect;
    }

    public ChaosSounds.Effect[] getAieEffect() {
        return this.aieEffect;
    }

    public void setAieEffect(ChaosSounds.Effect[] aieEffect) {
        this.aieEffect = aieEffect;
    }

    public ChaosSounds.Effect[] getKoEffect() {
        return this.koEffect;
    }

    public void setKoEffect(ChaosSounds.Effect[] koEffect) {
        this.koEffect = koEffect;
    }

    public ChaosBase.Obj getTheFather() {
        return this.theFather;
    }

    public void setTheFather(ChaosBase.Obj theFather) {
        this.theFather = theFather;
    }

    public ChaosBase.Obj getTheMaster() {
        return this.theMaster;
    }

    public void setTheMaster(ChaosBase.Obj theMaster) {
        this.theMaster = theMaster;
    }

    public ChaosBase.Obj getTheIllusion() {
        return this.theIllusion;
    }

    public void setTheIllusion(ChaosBase.Obj theIllusion) {
        this.theIllusion = theIllusion;
    }

    public boolean isLaugh() {
        return this.laugh;
    }

    public void setLaugh(boolean laugh) {
        this.laugh = laugh;
    }

    public boolean isTurn() {
        return this.turn;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }


    // VAR

    private int masterSeq;


    public int getMasterSeq() {
        return this.masterSeq;
    }

    public void setMasterSeq(int masterSeq) {
        this.masterSeq = masterSeq;
    }


    // PROCEDURE

    public void ShowStat(Runtime.IRef<String> str, int val) {
        // VAR
        Runtime.Ref<String> buffer = new Runtime.Ref<>("");
        int pos = 0;
        int tmp = 0;

        if (val == 0)
            chaosSounds.SoundEffect(chaosBase.mainPlayer, koEffect);
        memory.CopyStr(str, buffer, 39);
        pos = 0;
        while (Runtime.getChar(buffer, pos) != '#') {
            pos++;
        }
        tmp = val / 10;
        if (tmp == 0)
            Runtime.setChar(buffer, pos, ' ');
        else
            Runtime.setChar(buffer, pos, (char) (48 + tmp));
        Runtime.setChar(buffer, pos + 1, (char) (48 + val % 10));
        chaosActions.PopMessage(buffer, ChaosActions.moneyPos, 3);
    }

    public int ParabolicDist(long x, long y, long vx, long vy, long ax, long ay, long px, long py) {
        // VAR
        long t1 = 0L;
        long t2 = 0L;
        long d1 = 0L;
        long d2 = 0L;
        long dx = 0L;
        long dy = 0L;
        long delta = 0L;
        long mx = 0L;
        long my = 0L;

        ax = ax / 4;
        ay = ay / 4;
        vx = vx * 4;
        vy = vy * 4;
        x = x * 8 * ChaosBase.Frac;
        y = y * 8 * ChaosBase.Frac;
        px = px * 8 * ChaosBase.Frac;
        py = py * 8 * ChaosBase.Frac;
        x -= px;
        y -= py;
        delta = vx * vx - 4 * x * ax;
        if (delta >= 0) {
            delta = trigo.SQRT(delta);
            if (ax != 0) {
                t1 = (-vx + delta) / (2 * ax);
                t2 = (-vx - delta) / (2 * ax);
            } else if (vx != 0) {
                t1 = -(x / vx);
                t2 = t1;
            } else {
                t1 = 0;
                t2 = 0;
            }
            if ((t1 < 0) || (t1 >= 12000))
                t1 = 0;
            if ((t2 < 0) || (t2 >= 12000))
                t2 = 0;
            if ((t1 >= 6000) || (t2 >= 6000))
                return 0;
            d1 = Math.abs(((ay * t1) + vy) * t1 + y);
            d2 = Math.abs(((ay * t2) + vy) * t2 + y);
            if (d1 < d2)
                dx = d1;
            else
                dx = d2;
        } else {
            if (ax != 0)
                t1 = -(vx / (2 * ax));
            else
                t1 = 0;
            if (t1 < 0)
                t1 = 0;
            mx = (((ax * t1) + vx) * t1 + x) / 65536;
            my = (((ay * t1) + vy) * t1 + y) / 65536;
            dx = trigo.SQRT(mx * mx + my * my) * 65536;
        }
        delta = vy * vy - 4 * y * ay;
        if (delta >= 0) {
            delta = trigo.SQRT(delta);
            if (ay != 0) {
                t1 = (-vy + delta) / (2 * ay);
                t2 = (-vy - delta) / (2 * ay);
            } else if (vy != 0) {
                t1 = -(y / vy);
                t2 = t1;
            } else {
                t1 = 0;
                t2 = 0;
            }
            if ((t1 < 0) || (t1 >= 12000))
                t1 = 0;
            if ((t2 < 0) || (t2 >= 12000))
                t2 = 0;
            if ((t1 >= 6000) || (t2 >= 6000))
                return 0;
            d1 = Math.abs(((ax * t1) + vx) * t1 + x);
            d2 = Math.abs(((ax * t2) + vx) * t2 + x);
            if (d1 < d2)
                dy = d1;
            else
                dy = d2;
        } else {
            if (ay != 0)
                t1 = -(vy / (2 * ay));
            else
                t1 = 0;
            if (t1 < 0)
                t1 = 0;
            mx = (((ax * t1) + vx) * t1 + x) / 65536;
            my = (((ay * t1) + vy) * t1 + y) / 65536;
            dy = trigo.SQRT(mx * mx + my * my) * 65536;
        }
        if (dx < dy)
            return (int) (dx / (ChaosBase.Frac * 8));
        else
            return (int) (dy / (ChaosBase.Frac * 8));
    }

    public void FireFlame(ChaosBase.Obj src, int ox, int oy, boolean snd) {
        // VAR
        ChaosBase.Obj flame = null;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);

        if (snd)
            chaosSounds.SoundEffect(src, flameEffect);
        chaosActions.GetCenter(src, px, py);
        px.inc(ox);
        py.inc(oy);
        flame = chaosActions.CreateObj(Anims.ALIEN1, ChaosAlien.aFlame, px.get(), py.get(), 0, 30);
        chaosActions.SetObjVXY(flame, src.vx, src.vy);
    }

    public void FireMissile(ChaosBase.Obj src, int fx, int fy, int fvx, int fvy, int ox, int oy, boolean snd) {
        // VAR
        ChaosBase.Obj missile = null;

        if (snd)
            chaosSounds.SoundEffect(src, missileEffect);
        missile = chaosActions.CreateObj(Anims.MISSILE, ChaosMissile.mAlien2, fx + ox, fy + oy, ChaosMissile.mAcc2, 12);
        chaosActions.SetObjVXY(missile, fvx, fvy);
    }

    public void FireMissileV(ChaosBase.Obj src, ChaosBase.Obj dst, int ox, int oy, boolean snd) {
        // VAR
        ChaosBase.Obj missile = null;
        Runtime.Ref<Integer> fx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fy = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int fvx = 0;
        int fvy = 0;
        long rx = 0L;
        long ry = 0L;
        long rl = 0L;

        if (snd)
            chaosSounds.SoundEffect(src, missileEffect);
        chaosActions.GetCenter(src, fx, fy);
        fx.inc(ox);
        fy.inc(oy);
        chaosActions.GetCenter(dst, px, py);
        rx = px.get() - fx.get();
        ry = py.get() - fy.get();
        rl = trigo.SQRT(rx * rx + ry * ry);
        if (rl != 0) {
            px.set((int) (rx * 1536 / rl));
            py.set((int) (ry * 1536 / rl));
        } else {
            px.set(0);
            py.set(0);
        }
        fvx = px.get() + dst.vx;
        fvy = py.get() + dst.vy;
        if (src.subKind == 4)
            missile = chaosActions.CreateObj(Anims.MISSILE, ChaosMissile.mAlien1, fx.get(), fy.get(), trigo.RND() % 5, 12);
        else
            missile = chaosActions.CreateObj(Anims.MISSILE, ChaosMissile.mAlien2, fx.get(), fy.get(), ChaosMissile.mAcc2, 12);
        chaosActions.SetObjVXY(missile, fvx, fvy);
    }

    public void FireMissileA(ChaosBase.Obj src, int ox, int oy) {
        // VAR
        ChaosBase.Obj missile = null;
        long rx = 0L;
        long ry = 0L;
        long rl = 0L;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> dx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> dy = new Runtime.Ref<>(0);
        int nvx = 0;
        int nvy = 0;
        int nax = 0;
        int nay = 0;

        chaosSounds.SoundEffect(src, missileEffect);
        chaosActions.GetCenter(src, px, py);
        px.inc(ox);
        py.inc(oy);
        chaosActions.GetCenter(chaosBase.mainPlayer, dx, dy);
        rx = dx.get() - px.get();
        ry = dy.get() - py.get();
        rl = trigo.SQRT(rx * rx + ry * ry);
        if (rl == 0)
            rl = 1;
        nvx = chaosBase.mainPlayer.dvx;
        nvy = chaosBase.mainPlayer.dvy;
        nax = (int) (rx * 64 / rl);
        nay = (int) (ry * 64 / rl);
        missile = chaosActions.CreateObj(Anims.MISSILE, ChaosMissile.mAlien2, px.get(), py.get(), ChaosMissile.mAcc2, 12);
        chaosActions.SetObjVXY(missile, nvx, nvy);
        chaosActions.SetObjAXY(missile, nax, nay);
    }
    
    public void FireMissileS(long fx, long fy, long fvx, long fvy, ChaosBase.Obj src, ChaosBase.Obj dest, int ox, int oy, boolean speed, boolean snd) {
        // VAR
        ChaosBase.Obj missile = null;
        long px = 0L;
        long py = 0L;
        long pvx = 0L;
        long pvy = 0L;
        Runtime.Ref<Integer> tx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> ty = new Runtime.Ref<>(0);
        int nax = 0;
        int nay = 0;
        int val = 0;

        if (snd)
            chaosSounds.SoundEffect(src, missileEffect);
        chaosActions.GetCenter(dest, tx, ty);
        px = tx.get() + ox;
        py = ty.get() + oy;
        if (speed) {
            pvx = dest.vx;
            pvy = dest.vy;
        } else {
            pvx = 0;
            pvy = 0;
            px = (px + px + px + fx) / 4;
            py = (py + py + py + fy) / 4;
        }
        nax = (int) (((px - fx) * ChaosBase.Frac + (pvx - fvx) * 420) / 22500);
        nay = (int) (((py - fy) * ChaosBase.Frac + (pvy - fvy) * 420) / 22500);
        if (nax < -127)
            nax = -127;
        else if (nax > 127)
            nax = 127;
        if (nay < -127)
            nay = -127;
        else if (nay > 127)
            nay = 127;
        if ((src.subKind >= 4) && (src.moveSeq <= 4)) {
            if (((src.moveSeq == 2) || (src.moveSeq == 0)) && ((src.stat == 3) || (src.stat == 6))) {
                val = 4 - src.shapeSeq % 3 - src.moveSeq;
            } else {
                val = masterSeq % (5 - src.moveSeq);
                masterSeq = (masterSeq + 1) % 3;
            }
            missile = chaosActions.CreateObj(Anims.MISSILE, ChaosMissile.mAlien1, (int) fx, (int) fy, val, 12);
        } else {
            missile = chaosActions.CreateObj(Anims.MISSILE, ChaosMissile.mAlien2, (int) fx, (int) fy, ChaosMissile.mAcc2, 12);
        }
        chaosActions.SetObjVXY(missile, (int) fvx, (int) fvy);
        chaosActions.SetObjAXY(missile, nax, nay);
    }

    public void BoumS(ChaosBase.Obj src, ChaosBase.Obj dest, int ox, int oy, int ba, int bm, int sa, int sm, int c, int as, int ms, boolean follow, boolean speed, boolean snd) {
        // VAR
        long sh = 0L;
        long fvx = 0L;
        long fvy = 0L;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int tx = 0;
        int ty = 0;
        int angle = 0;
        int mod = 0;

        if (snd)
            chaosSounds.SoundEffect(src, bombEffect);
        chaosActions.GetCenter(src, px, py);
        angle = ba;
        mod = bm;
        tx = 0;
        ty = 0;
        while (c > 0) {
            sh = trigo.SIN(mod) * ms / 1024 + 32;
            fvx = trigo.COS(angle) * as / 4;
            fvx = fvx * sh / 32;
            fvy = trigo.SIN(angle) * as / 4;
            fvy = fvy * sh / 32;
            if (follow) {
                if (turn) {
                    tx = trigo.COS(angle + 90) / 32;
                    ty = trigo.SIN(angle + 90) / 32;
                }
                FireMissileS(px.get(), py.get(), fvx, fvy, src, dest, ox + tx, oy + ty, speed, false);
            } else {
                FireMissile(src, px.get(), py.get(), (int) fvx, (int) fvy, ox, oy, false);
            }
            angle += sa;
            mod += sm;
            c--;
        }
        turn = false;
    }

    public boolean CloseEnough(int bx, int by, int px, int py) {
        return (Math.abs(px - bx) + Math.abs(py - by) < 96);
    }

    public void GoTo(ChaosBase.Obj boss, int tx, int ty) {
        // VAR
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);

        chaosActions.GetCenter(boss, px, py);
        boss.ax = 0;
        boss.ay = 0;
        boss.dvx = (tx - px.get()) * 16;
        boss.dvy = (ty - py.get()) * 16;
    }

    public void GoCenter(ChaosBase.Obj boss) {
        GoTo(boss, chaosGraphics.gameWidth / 2, chaosGraphics.gameHeight / 2);
    }

    public void ReturnWeapon(int sx, int sy) {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.Obj tail = null;
        Runtime.Ref<Integer> wx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> wy = new Runtime.Ref<>(0);
        long dx = 0L;
        long dy = 0L;
        long dl = 0L;
        long avx = 0L;
        long avy = 0L;

        obj = (ChaosBase.Obj) memory.First(chaosBase.animList[Anims.WEAPON.ordinal()]);
        tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[Anims.WEAPON.ordinal()]);
        while (obj != tail) {
            chaosActions.GetCenter(obj, wx, wy);
            dx = wx.get() - sx;
            dy = wy.get() - sy;
            if ((Math.abs(dx) < 50) && (Math.abs(dy) < 50)) {
                dl = trigo.SQRT(dx * dx + dy * dy);
                if ((dl < 50) && (dl > 0)) {
                    avx = chaosBase.step;
                    avx = avx * dx * 64 / dl;
                    avy = chaosBase.step;
                    avy = avy * dy * 64 / dl;
                    obj.vx += avx;
                    obj.vy += avy;
                }
            }
            obj = (ChaosBase.Obj) memory.Next(obj.animNode);
        }
    }

    public void KillObjs(Anims oKind, int sKind) {
        // VAR
        ChaosBase.Obj alien = null;
        ChaosBase.Obj obj = null;
        ChaosBase.Obj tail = null;

        obj = (ChaosBase.Obj) memory.First(chaosBase.animList[oKind.ordinal()]);
        tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[oKind.ordinal()]);
        while (obj != tail) {
            alien = obj;
            obj = (ChaosBase.Obj) memory.Next(obj.animNode);
            if (alien.subKind == sKind)
                chaosActions.Die(alien);
        }
    }

    private void Chain_SetTo(int val, /* VAR */ Runtime.IRef<Integer> sKind) {
        if (sKind.get() < val)
            sKind.set(val);
    }

    public void Chain(ChaosBase.Obj boss) {
        // CONST
        final int bBrotherAlien = 0;
        final int bSisterAlien = 1;
        final int bMotherAlien = 2;
        final int bFatherAlien = 3;
        final int bMasterAlien1 = 4;
        final int bMasterAlien2 = 5;
        final int bFatherHeart = 6;
        final int bMasterEye = 7;
        final int bMasterMouth = 8;
        final int bMasterPart0 = 9;
        final int bMasterPart1 = 10;
        final int bMasterPart2 = 11;

        // VAR
        ChaosBase.Obj alien = null;
        ChaosBase.Obj tail = null;
        ChaosBase.Obj cur = null;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> sKind = new Runtime.Ref<>(0);
        int cnt = 0;

        chaosBase.addpt += 500;
        if (chaosBase.level[Zone.Family.ordinal()] < 10)
            return;
        cnt = 0;
        sKind.set(0);
        cur = (ChaosBase.Obj) memory.First(chaosBase.animList[Anims.ALIEN3.ordinal()]);
        tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[Anims.ALIEN3.ordinal()]);
        while (cur != tail) {
            switch (cur.subKind) {
                case bBrotherAlien -> {
                    Chain_SetTo(1, sKind);
                }
                case bMotherAlien -> {
                    Chain_SetTo(2, sKind);
                }
                case bSisterAlien -> {
                    Chain_SetTo(3, sKind);
                }
                case bMasterAlien1 -> {
                    Chain_SetTo(4, sKind);
                }
                case bFatherAlien -> {
                    Chain_SetTo(5, sKind);
                }
                case bMasterAlien2 -> {
                    Chain_SetTo(6, sKind);
                }
                default -> {
                }
            }
            if (new Runtime.RangeSet(Memory.SET16_r).with(bBrotherAlien, bSisterAlien, bMotherAlien, bFatherAlien, bMasterAlien1, bMasterAlien2).contains(cur.subKind))
                cnt++;
            cur = (ChaosBase.Obj) memory.Next(cur.animNode);
        }
        if (cnt < 2)
            sKind.set(6);
        if (sKind.get() < 6) {
            chaosActions.GetCenter(boss, px, py);
            switch (sKind.get()) {
                case 2 -> {
                    sKind.set(bSisterAlien);
                }
                case 3 -> {
                    sKind.set(bMasterAlien1);
                    alien = chaosActions.CreateObj(Anims.ALIEN3, bMasterEye, px.get(), py.get(), 0, 0);
                    alien = chaosActions.CreateObj(Anims.ALIEN3, bMasterEye, px.get(), py.get(), 1, 1);
                    alien = chaosActions.CreateObj(Anims.ALIEN3, bMasterMouth, px.get(), py.get(), 0, 0);
                }
                case 4 -> {
                    sKind.set(bFatherAlien);
                    alien = chaosActions.CreateObj(Anims.ALIEN3, bFatherHeart, px.get(), py.get(), 0, 0);
                }
                case 5 -> {
                    sKind.set(bMasterAlien2);
                    alien = chaosActions.CreateObj(Anims.ALIEN3, bMasterPart0, px.get(), py.get(), 0, 0);
                    alien = chaosActions.CreateObj(Anims.ALIEN3, bMasterPart0, px.get(), py.get(), 1, 1);
                    alien = chaosActions.CreateObj(Anims.ALIEN3, bMasterPart1, px.get(), py.get(), 2, 2);
                    alien = chaosActions.CreateObj(Anims.ALIEN3, bMasterPart1, px.get(), py.get(), 3, 3);
                    alien = chaosActions.CreateObj(Anims.ALIEN3, bMasterPart2, px.get(), py.get(), 4, 4);
                }
                default -> throw new RuntimeException("Unhandled CASE value " + sKind.get());
            }
            alien = chaosActions.CreateObj(Anims.ALIEN3, sKind.get(), px.get(), py.get(), 10, 10);
        } else if (cnt <= 1) {
            px.set(11 * ChaosGraphics.BW + ChaosGraphics.BW / 2);
            py.set(18 * ChaosGraphics.BH + ChaosGraphics.BH / 2);
            alien = chaosActions.CreateObj(Anims.ALIEN1, ChaosAlien.aPic, px.get(), py.get(), 0, 0);
            px.set(17 * ChaosGraphics.BW + ChaosGraphics.BW / 2);
            py.set(40 * ChaosGraphics.BH + ChaosGraphics.BH / 2);
            alien = chaosActions.CreateObj(Anims.BONUS, ChaosBonus.TimedBonus, px.get(), py.get(), ChaosBonus.tbSGSpeed, ChaosBonus.tbSGSpeed);
            chaosGraphics.castle[40][17] = 22;
        }
    }

    public void BoumX(ChaosBase.Obj src, ChaosBase.Obj dest, int cnt, boolean star) {
        // VAR
        int c = 0;
        Runtime.Ref<Integer> sx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> sy = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> dx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> dy = new Runtime.Ref<>(0);
        int rx = 0;
        int ry = 0;
        int fvx = 0;
        int fvy = 0;
        int angle = 0;

        chaosSounds.SoundEffect(src, bombEffect);
        chaosActions.GetCenter(src, sx, sy);
        chaosActions.GetCenter(dest, dx, dy);
        for (c = 0; c < cnt; c++) {
            angle = c * 360 / cnt;
            rx = trigo.COS(angle) / 32;
            ry = trigo.SIN(angle) / 32;
            if (star) {
                fvx = trigo.COS(-angle) * 3;
                fvy = trigo.SIN(-angle) * 3;
            } else {
                fvx = trigo.RND() % 2048 + 1024;
                if (trigo.RND() % 2 == 0)
                    fvx = -fvx;
                fvy = trigo.RND() % 2048 + 1024;
                if (trigo.RND() % 2 == 0)
                    fvy = -fvy;
            }
            FireMissileS(sx.get(), sy.get(), fvx, fvy, src, dest, rx, ry, false, false);
        }
    }

    public void BoumE(ChaosBase.Obj src, int cnt, int mx, int my) {
        // VAR
        Runtime.Ref<Integer> fx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fy = new Runtime.Ref<>(0);
        int fvx = 0;
        int fvy = 0;
        int angle = 0;
        int c = 0;

        chaosActions.GetCenter(src, fx, fy);
        chaosSounds.SoundEffect(src, bombEffect);
        for (c = 1; c <= cnt; c++) {
            angle = c * 360 / cnt;
            fvx = trigo.COS(angle) * mx / 4;
            fvy = trigo.SIN(angle) * my / 4;
            FireMissile(src, fx.get(), fy.get(), fvx, fvy, 0, 0, false);
        }
    }

    public void ResetPart(ChaosBase.Obj part) {
        part.hitSubLife = 0;
        part.fireSubLife = 0;
        part.moveSeq = 1;
        part.stat = part.life;
        chaosActions.SetObjRect(part, 0, 0, 11, 11);
    }

    public final ChaosBase.ResetProc ResetPart_ref = this::ResetPart;

    public void MoveHeart(ChaosBase.Obj heart) {
        // VAR
        Runtime.Ref<Integer> fx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> fy = new Runtime.Ref<>(0);

        if (theFather.moveSeq == 0) {
            heart.moveSeq = 0;
            chaosActions.Die(heart);
            return;
        } else if (theFather.hitSubLife + theFather.fireSubLife == 0) {
            chaosActions.SetObjLoc(heart, 67, 83, 0, 0);
        } else {
            chaosActions.SetObjLoc(heart, 67, 83, 6, 6);
        }
        chaosActions.GetCenter(theFather, fx, fy);
        chaosActions.SetObjXY(heart, fx.get() - 3, fy.get() - 3);
    }

    public final ChaosBase.MoveProc MoveHeart_ref = this::MoveHeart;

    public void MoveEye(ChaosBase.Obj eye) {
        // VAR
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> mx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> my = new Runtime.Ref<>(0);
        int dx = 0;
        int dy = 0;
        int r = 0;

        chaosActions.GetCenter(theMaster, mx, my);
        if (((eye.stat % 2) != 0)) {
            dx = 1;
            mx.inc(17);
        } else {
            dx = -1;
            mx.dec(18);
        }
        my.dec(10);
        if (theMaster.stat == 4) {
            r = (theMaster.shapeSeq / 16) % 12;
            if (dx < 0)
                r = 11 - r;
            if (theMaster.shapeSeq < 60) {
                my.dec(trigo.SIN(theMaster.shapeSeq * 3) / 192);
                mx.inc(trigo.SIN(theMaster.shapeSeq * 3) / 256 * dx);
            } else if (theMaster.shapeSeq < 240) {
                my.inc(trigo.SIN(theMaster.shapeSeq - 60) / 64);
                mx.dec(trigo.SIN(theMaster.shapeSeq - 60) / 48 * dx);
            } else if (theMaster.shapeSeq < 600) {
                my.dec(trigo.SIN((theMaster.shapeSeq - 240) / 2) / 32);
                mx.inc(trigo.SIN((theMaster.shapeSeq - 240) / 2) / 32 * dx);
            }
        }
        chaosActions.SetObjXY(eye, mx.get() - 5, my.get() - 5);
        if (theMaster.stat < 4) {
            chaosActions.GetCenter(chaosBase.mainPlayer, px, py);
            dx = (int) Math.abs(px.get() - mx.get());
            dy = (int) Math.abs(py.get() - my.get());
            if ((dx * 3 >= dy) && (dx <= dy * 3)) {
                if (my.get() > py.get()) {
                    if (mx.get() > px.get()) {
                        if (dx < dy)
                            r = 0;
                        else
                            r = 11;
                    } else {
                        if (dx < dy)
                            r = 2;
                        else
                            r = 3;
                    }
                } else {
                    if (mx.get() > px.get()) {
                        if (dx < dy)
                            r = 8;
                        else
                            r = 9;
                    } else {
                        if (dx < dy)
                            r = 6;
                        else
                            r = 5;
                    }
                }
            } else {
                if (dy > dx) {
                    if (my.get() < py.get())
                        r = 7;
                    else
                        r = 1;
                } else {
                    if (mx.get() < px.get())
                        r = 4;
                    else
                        r = 10;
                }
            }
        } else if (theMaster.stat > 4) {
            eye.moveSeq = 0;
            chaosActions.Die(eye);
            return;
        }
        chaosActions.SetObjLoc(eye, (r % 10) * 11 + 76, (r / 10) * 11 + 32, 11, 11);
    }

    public final ChaosBase.MoveProc MoveEye_ref = this::MoveEye;

    public void MoveMouth(ChaosBase.Obj mouth) {
        // VAR
        int off = 0;
        Runtime.Ref<Integer> mx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> my = new Runtime.Ref<>(0);
        int dx = 0;

        chaosActions.GetCenter(theMaster, mx, my);
        my.inc(4);
        off = 0;
        if (theMaster.stat == 3) {
            chaosActions.SetObjLoc(mouth, 32, 200, 0, 0);
        } else if (theMaster.stat >= 6) {
            mouth.moveSeq = 0;
            chaosActions.Die(mouth);
            return;
        } else if (theMaster.stat == 4) {
            if (((theMaster.moveSeq % 2) != 0))
                dx = 1;
            else
                dx = -1;
            if (theMaster.shapeSeq < 60) {
                mx.dec(trigo.SIN(theMaster.shapeSeq * 3) / 256 * dx);
            } else if (theMaster.shapeSeq < 240) {
                my.inc(trigo.SIN((theMaster.shapeSeq - 60) * 2) / 128);
                mx.inc(trigo.SIN(theMaster.shapeSeq - 60) / 128 * dx);
            } else if (theMaster.shapeSeq < 600) {
                my.inc(trigo.SIN((theMaster.shapeSeq - 240) / 2) / 16);
            }
            chaosActions.SetObjLoc(mouth, 32, 200, 20, 12);
        } else {
            off = (theMaster.shapeSeq / 30) % 10;
            if (off >= 5)
                off = 10 - off;
            chaosActions.SetObjLoc(mouth, 32 + off, 200 + off, 20 - off * 2, 12 - off * 2);
        }
        chaosActions.SetObjXY(mouth, mx.get() - 10 + off, my.get() - 6 + off);
    }

    public final ChaosBase.MoveProc MoveMouth_ref = this::MoveMouth;

    public void MovePart(ChaosBase.Obj part) {
        // VAR
        int x0 = 0;
        int y0 = 0;
        int x1 = 0;
        int y1 = 0;
        int x2 = 0;
        int y2 = 0;
        Runtime.Ref<Integer> ix = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> iy = new Runtime.Ref<>(0);
        int seq = 0;
        int st = 0;

        if (theIllusion == null) {
            part.moveSeq = 0;
            chaosActions.Die(part);
            return;
        }
        if (theIllusion.stat == 0)
            seq = (theIllusion.shapeSeq / 64) % 8;
        else if (theIllusion.stat == 33)
            seq = 7 - (theIllusion.shapeSeq / 64) % 8;
        else
            seq = 0;
        seq = (seq + 2) % 8;
        st = part.stat;
        switch (st) {
            case 0, 1 -> chaosActions.SetObjLoc(part, 56, 240, 16, 16);
            case 2, 3 -> chaosActions.SetObjLoc(part, 72, 240, 16, 16);
            default -> chaosActions.SetObjLoc(part, 88 + seq * 20, 236, 20, 20);
        }
        chaosActions.SetObjRect(part, 0, 0, 0, 0);
        switch (seq) {
            case 0 -> {
                x0 = 0;
                y0 = 3;
                x1 = 0;
                y1 = -4;
                x2 = 0;
                y2 = -2;
            }
            case 1 -> {
                x0 = -2;
                y0 = 3;
                x1 = 1;
                y1 = -3;
                x2 = -2;
                y2 = -2;
            }
            case 2 -> {
                x0 = -3;
                y0 = 2;
                x1 = 2;
                y1 = -3;
                x2 = -3;
                y2 = -3;
            }
            case 3 -> {
                x0 = -3;
                y0 = 1;
                x1 = 3;
                y1 = -2;
                x2 = -2;
                y2 = -4;
            }
            case 4 -> {
                x0 = -4;
                y0 = 0;
                x1 = 3;
                y1 = -0;
                x2 = -4;
                y2 = 0;
            }
            case 5 -> {
                x0 = -3;
                y0 = -2;
                x1 = 3;
                y1 = 1;
                x2 = -3;
                y2 = -2;
            }
            case 6 -> {
                x0 = -2;
                y0 = -3;
                x1 = 3;
                y1 = 2;
                x2 = -2;
                y2 = -3;
            }
            case 7 -> {
                x0 = -1;
                y0 = -3;
                x1 = 2;
                y1 = 3;
                x2 = -1;
                y2 = -2;
            }
            default -> throw new RuntimeException("Unhandled CASE value " + seq);
        }
        chaosActions.GetCenter(theIllusion, ix, iy);
        ix.dec(8);
        iy.dec(8);
        if (st >= 4)
            chaosActions.SetObjXY(part, ix.get() + x2, iy.get() + y2);
        else if (((st % 2) != 0))
            chaosActions.SetObjXY(part, ix.get() + x1, iy.get() + y1);
        else
            chaosActions.SetObjXY(part, ix.get() + x0, iy.get() + y0);
    }

    public final ChaosBase.MoveProc MovePart_ref = this::MovePart;

    public void DiePart(ChaosBase.Obj part) {
        if (part.moveSeq != 0)
            part.life = 1;
    }

    public final ChaosBase.DieProc DiePart_ref = this::DiePart;


    // Support

    private static ChaosFire instance;

    public static ChaosFire instance() {
        if (instance == null)
            new ChaosFire(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        turn = false;
    }

    public void close() {
    }

}
