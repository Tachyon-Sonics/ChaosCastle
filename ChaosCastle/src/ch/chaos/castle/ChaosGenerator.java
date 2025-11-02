package ch.chaos.castle;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosGenerator {

    // Imports
    private final ChaosBase chaosBase;
    private final ChaosGraphics chaosGraphics;
    private final ChaosObjects chaosObjects;
    private final Memory memory;
    private final Trigo trigo;


    private ChaosGenerator() {
        instance = this; // Set early to handle circular dependencies
        chaosBase = ChaosBase.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosObjects = ChaosObjects.instance();
        memory = Memory.instance();
        trigo = Trigo.instance();
    }


    // PROCEDURE

    /* Construction support */
    public void DrawPacman(int open, int width, int height, int sx, int sy, int ex, int ey) {
        // VAR
        int x = 0;
        int y = 0;
        int v = 0;
        int h = 0;
        int z = 0;

        y = ey;
        while (y >= sy) {
            x = ex;
            while (x >= sx) {
                chaosObjects.Set(x, y);
                if (trigo.RND() % 16 > open) {
                    h = trigo.RND() % 2;
                    v = 1 - h;
                    for (z = 1; z < width * h + height * v; z++) {
                        chaosObjects.Set(x + h * z, y + v * z);
                    }
                }
                x -= width;
            }
            y -= height;
        }
    }

    public void Road(int sx, int sy, int ex, int ey, int sz, int val) {
        // VAR
        int w = 0;
        int h = 0;
        int m = 0;
        int c = 0;
        int x = 0;
        int y = 0;

        w = (int) Math.abs(ex - sx);
        h = (int) Math.abs(ey - sy);
        c = 0;
        if (w > h)
            m = w;
        else
            m = h;
        w = ex - sx;
        h = ey - sy;
        while (c < m) {
            x = sx + w * c / m;
            y = sy + h * c / m;
            chaosObjects.Fill(x - sz, y - sz, x + sz, y + sz, val);
            c++;
        }
    }

    public void Excavate(int sx, int ex, int mny, int mxy, int mnh, int mxh, int sdy, int sh, int sd, int mxsy, int mxsh, int mnd, int mxd) {
        // VAR
        int x = 0;
        int y = 0;
        int py = 0;
        int h = 0;
        int d = 0;
        int dw = 0;
        int syw = 0;
        int shw = 0;

        x = sx;
        y = mny + sdy;
        h = sh;
        d = sd;
        dw = mxd - mnd + 1;
        syw = mxsy * 2 + 1;
        shw = mxsh * 2 + 1;
        while (x <= ex) {
            if (d == 0) {
                d = chaosObjects.Rnd(mxd - mnd + 1) + mnd;
                h += chaosObjects.Rnd(shw) - mxsh;
                if (h < mnh)
                    h = mnh;
                else if (h > mxh)
                    h = mxh;
                y += chaosObjects.Rnd(syw) - mxsy;
                if (y < mny)
                    y = mny;
                else if (y + h > mxy)
                    y = mxy - h + 1;
            }
            py = y + h;
            while (py != y) {
                py--;
                chaosObjects.Put(x, py, 0);
            }
            x++;
            d--;
        }
    }

    private boolean PutCross_CheckPlace(int bx, int by, int w, int h, int x, int y, int s) {
        // VAR
        int px = 0;
        int py = 0;

        if ((x - s - 1 <= bx) && (y - s - 1 <= by))
            return false;
        if ((x + s + 2 >= bx + w) && (y + s + 2 >= by + h))
            return false;
        for (px = x - s - 1; px <= x + s + 1; px++) {
            for (py = y - 1; py <= y + 1; py++) {
                if (chaosObjects.Get(px, py) >= ChaosGraphics.NbBackground)
                    return false;
            }
        }
        for (py = y - s - 1; py <= y + s + 1; py++) {
            for (px = x - 1; px <= x + 1; px++) {
                if (chaosObjects.Get(px, py) >= ChaosGraphics.NbBackground)
                    return false;
            }
        }
        return true;
    }

    private boolean PutCross_FindPlace(int bx, int by, int w, int h, /* VAR */ Runtime.IRef<Integer> x, /* VAR */ Runtime.IRef<Integer> y, /* VAR */ Runtime.IRef<Integer> s, /* VAR */ Runtime.IRef<Integer> z) {
        // VAR
        int timeOut = 0;

        timeOut = 50;
        do {
            timeOut--;
            s.set(trigo.RND() % 4 + 1);
            z.set(2 * s.get() + 1);
            x.set(chaosObjects.Rnd(w - z.get()) + bx + s.get());
            y.set(chaosObjects.Rnd(h - z.get()) + by + s.get());
            if (PutCross_CheckPlace(bx, by, w, h, x.get(), y.get(), s.get()))
                return true;
        } while (timeOut != 0);
        return false;
    }

    private void PutCross_PutIt(int val, int x, int y, int s) {
        // VAR
        int pz = 0;

        for (pz = x - s; pz <= x + s; pz++) {
            chaosObjects.Put(pz, y, val);
        }
        for (pz = y - s; pz <= y + s; pz++) {
            chaosObjects.Put(x, pz, val);
        }
    }

    public void PutCross(int bx, int by, int w, int h, int val) {
        // VAR
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> s = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> z = new Runtime.Ref<>(0);

        if (PutCross_FindPlace(bx, by, w, h, x, y, s, z))
            PutCross_PutIt(val, x.get(), y.get(), s.get());
    }

    private boolean CheckLink(int x, int y, int sz, int ml, /* VAR */ Runtime.IRef<Integer> angle) {
        // VAR
        int timeOut = 0;
        int length = 0;
        int px = 0;
        int py = 0;
        int dx = 0;
        int dy = 0;

        angle.set((trigo.RND() % 4) * 90);
        timeOut = 4;
        do {
            dx = trigo.COS(angle.get()) / 1024;
            dy = trigo.SIN(angle.get()) / 1024;
            px = x + dx * (sz + 1);
            py = y + dy * (sz + 1);
            length = 0;
            while ((length <= ml) && (chaosObjects.Get(px, py) >= ChaosGraphics.NbBackground)) {
                px += dx;
                py += dy;
                length++;
                if ((px < 0) || (py < 0) || (px >= chaosGraphics.castleWidth) || (py >= chaosGraphics.castleHeight))
                    length = ml + 1;
            }
            if (length <= ml)
                return true;
            angle.set((angle.get() + 90) % 360);
            timeOut--;
        } while (timeOut != 0);
        return false;
    }

    public boolean FindIsolatedRect(int sx, int sy, int ex, int ey, int sz, int ml, /* VAR+WRT */ Runtime.IRef<Integer> angle, /* VAR */ Runtime.IRef<Integer> x, /* VAR */ Runtime.IRef<Integer> y, boolean wall) {
        // VAR
        int dx = 0;
        int dy = 0;
        int w = 0;
        int h = 0;
        int timeOut = 0;
        int backs = 0;
        boolean res = false;

        w = ex - sx - sz * 2 - 1;
        sx += sz + 1;
        h = ey - sy - sz * 2 - 1;
        sy += sz + 1;
        timeOut = 20;
        do {
            timeOut--;
            x.set(chaosObjects.Rnd(w) + sx);
            y.set(chaosObjects.Rnd(h) + sy);
            backs = 0;
            for (dy = -sz - 1; dy <= sz + 1; dy++) {
                for (dx = -sz - 1; dx <= sz + 1; dx++) {
                    res = chaosObjects.Get(x.get() + dx, y.get() + dy) < ChaosGraphics.NbBackground;
                    if (res == wall)
                        backs++;
                }
            }
            if ((backs == 0) && wall && !CheckLink(x.get(), y.get(), sz, ml, angle))
                backs = 1;
        } while (!((backs == 0) || (timeOut == 0)));
        return backs == 0;
    }

    public void MakeLink(int x, int y, int sz, int angle, int val) {
        // VAR
        int px = 0;
        int py = 0;
        int dx = 0;
        int dy = 0;

        dx = trigo.COS(angle) / 1024;
        dy = trigo.SIN(angle) / 1024;
        px = x + dx * (sz + 1);
        py = y + dy * (sz + 1);
        while (chaosObjects.Get(px, py) >= ChaosGraphics.NbBackground) {
            chaosObjects.Put(px, py, val);
            px += dx;
            py += dy;
        }
    }

    public void FillEllipse(long sx, long sy, long pw, long ph, int val) {
        // VAR
        long by = 0L;
        long ey = 0L;
        long ex = 0L;
        long x = 0L;
        long y = 0L;
        long w = 0L;
        long h = 0L;
        long pw2 = 0L;
        long ph2 = 0L;
        long w2 = 0L;
        long phd = 0L;
        boolean odd = false;

        odd = ((pw % 2) != 0);
        ph2 = ph * ph;
        pw2 = pw * pw;
        phd = ph2 / 2;
        h = -ph + 1;
        w = 1;
        y = 0;
        do {
            w2 = ((ph2 - h * h) * pw2 + phd) / ph2;
            while (w * w < w2) {
                w++;
            }
            if (((w % 2) != 0) != odd)
                w--;
            x = sx + (pw - w) / 2;
            by = sy + y;
            ey = sy + ph - y - 1;
            ex = x + w - 1;
            chaosObjects.Fill((int) x, (int) by, (int) ex, (int) by, val);
            if (h != 0)
                chaosObjects.Fill((int) x, (int) ey, (int) ex, (int) ey, val);
            y++;
            h += 2;
        } while (h <= 0);
    }

    public void TripleLoop(int val) {
        // VAR
        int angle = 0;
        int sz = 0;
        int ml = 0;
        int bs = 0;
        int lx = 0;
        int ly = 0;
        long r = 0L;
        long x = 0L;
        long y = 0L;

        lx = 0;
        ly = 0;
        ml = trigo.RND() % 8 + 1;
        bs = trigo.RND() % 360;
        for (angle = 0; angle <= 702; angle += 2) {
            sz = (trigo.SIN(bs + angle * ml) + 1024) / 700 + 1;
            r = trigo.SIN(angle * 3 / 2);
            x = trigo.COS(angle) * (2048 + r) / 54330;
            y = trigo.SIN(angle) * (2048 + r) / 54330;
            x += 62;
            y += 62;
            if ((lx != 0) && (ly != 0))
                Road(lx, ly, (int) x, (int) y, sz, val);
            lx = (int) x;
            ly = (int) y;
        }
    }

    public void GCastle(int sx, int sy, int ex, int ey, int wall, int back) {
        // VAR
        int x = 0;
        int y = 0;
        int c = 0;

        for (y = sy; y <= ey; y += 2) {
            chaosObjects.Fill(sx, y, ex, y, wall);
            for (c = 1; c <= 3; c++) {
                x = chaosObjects.Rnd(ex - sx + 1) + 1;
                chaosObjects.Put(x, y, back);
            }
        }
    }

    public void Cave(int sx, int sy, int ex, int ey, int y1, int y2, int dx) {
        // VAR
        int x = 0;
        int d1 = 0;
        int d2 = 0;
        int t1 = 0;
        int t2 = 0;

        x = sx;
        d1 = 1;
        d2 = -1;
        t1 = 0;
        t2 = 0;
        do {
            chaosObjects.Fill(x, y1, x, y2, 0);
            x += dx;
            if (t1 == 0) {
                if (trigo.RND() % 2 == 0)
                    d1 = 1;
                else
                    d1 = -1;
                t1 = trigo.RND() % 8 + 3;
            } else {
                t1--;
            }
            if (t2 == 0) {
                if (trigo.RND() % 2 == 0)
                    d2 = 1;
                else
                    d2 = -1;
                t2 = trigo.RND() % 8 + 3;
            } else {
                t2--;
            }
            if (y1 <= sy)
                d1 = 1;
            if (y2 >= ey)
                d2 = -1;
            if (y2 + d2 - y1 - d1 < 3) {
                if (y1 >= sy + 5)
                    d1 = -1;
                if (y2 <= ey - 5)
                    d2 = 1;
            }
            y1 += d1;
            y2 += d2;
        } while (x != ex);
        chaosObjects.PutExit(12, y1 - d1);
        chaosObjects.PutExit(12, y2 - d2);
    }

    public void DrawFactory() {
        // VAR
        int x = 0;
        int y = 0;
        int dx = 0;
        int dy = 0;
        int mnx = 0;
        int mxx = 0;
        int nxx = 0;
        int l = 0;
        int s = 0;
        int sz = 0;
        int sz2 = 0;
        int pass = 0;
        int val = 0;
        int v2 = 0;

        /* 100 x 100 */
        pass = 0;
        x = 12;
        y = 90;
        s = -1;
        dx = 0;
        dy = -1;
        l = 7;
        mnx = 6;
        mxx = 19;
        nxx = 31;
        v2 = 20;
        while (true) {
            if (l == 0) {
                sz = trigo.RND() % 4 + 2;
                switch (pass) {
                    case 0 -> {
                        val = 13;
                    }
                    case 1 -> {
                        val = 8;
                    }
                    case 2 -> {
                        val = 10;
                    }
                    case 3 -> {
                        if (trigo.RND() % 6 == 0)
                            val = 14;
                        else
                            val = 20;
                    }
                    default -> throw new RuntimeException("Unhandled CASE value " + pass);
                }
                if (chaosGraphics.dualpf) {
                    v2 = val;
                    val = 9;
                }
                chaosObjects.FillCond(x - sz, y - sz, x + sz, y + sz, chaosObjects.OnlyWall_ref, val);
                val = trigo.RND() % 4 + pass + 25;
                sz2 = chaosObjects.Rnd(sz - 1);
                chaosObjects.Fill(x - sz2, y - sz2, x + sz2, y + sz2, val);
                if (((s < 0) && (y > 10)) || ((s > 0) && (y < 90))) {
                    if (trigo.RND() % 3 == 0) {
                        dy = 0;
                        if (trigo.RND() % 2 == 0)
                            dx = -1;
                        else
                            dx = 1;
                        if (x <= mnx)
                            dx = 1;
                        else if (x >= mxx)
                            dx = -1;
                    } else if (trigo.RND() % 16 == 0) {
                        dy = -s;
                        dx = 0;
                    } else {
                        dy = s;
                        dx = 0;
                    }
                } else {
                    mxx = nxx + 1;
                    if (trigo.RND() % 3 == 0) {
                        dy = -s;
                        dx = 0;
                    } else {
                        dx = 1;
                        dy = 0;
                    }
                }
                l = 6 + trigo.RND() % 8;
                x += dx * sz2 + dx;
                y += dy * sz2 + dy;
            }
            chaosObjects.Put(x, y, v2);
            x += dx;
            y += dy;
            l--;
            if (y <= 6) {
                y = 6;
                l = 0;
            } else if (y >= 94) {
                y = 94;
                l = 0;
            }
            if (x <= mnx) {
                x = mnx;
                dx = 0;
                dy = s;
            } else if (x >= mxx) {
                x = mxx;
                dx = 0;
            }
            if (x >= nxx) {
                mnx += 25;
                nxx += 25;
                mxx = mnx + 13;
                if (nxx > 95)
                    nxx = 95;
                s = -s;
                pass++;
                if (pass > 3)
                    break;
            }
        }
        MakeLink(12, 90, -1, -90, 20);
        chaosObjects.Put(x, y, 11);
        chaosObjects.PutExit(x, y);
    }

    private void DrawLabyrinth_Cut(int size, int x, int y, int px, int py, /* VAR */ Runtime.IRef<Boolean> flag) {
        // VAR
        int z = 0;
        int v = 0;
        boolean h = false;

        h = (trigo.RND() % 2 == 0);
        if (h)
            z = x;
        else
            z = y;
        v = chaosObjects.Rnd(50);
        flag.set((z * size < v * v));
        v = chaosObjects.Rnd(size) - 2 * z;
        chaosObjects.Put(px, py, 0);
        if (v >= 0) {
            if (h)
                chaosObjects.Put(px + 1, py, 0);
            else
                chaosObjects.Put(px, py + 1, 0);
        } else {
            if (h)
                chaosObjects.Put(px - 1, py, 0);
            else
                chaosObjects.Put(px, py - 1, 0);
        }
    }

    public void DrawLabyrinth(int size) {
        // VAR
        int x = 0;
        int y = 0;
        int px = 0;
        int py = 0;
        Runtime.Ref<Boolean> flag = new Runtime.Ref<>(false);

        py = 1;
        for (y = 0; y < size; y++) {
            px = 1;
            for (x = 0; x < size; x++) {
                DrawLabyrinth_Cut(size, x, y, px, py, flag);
                if (flag.get())
                    DrawLabyrinth_Cut(size, x, y, px, py, flag);
                px += 2;
            }
            py += 2;
        }
    }

    public void RemIsolated(int sx, int sy, int ex, int ey, int dx, int dy, int val) {
        // VAR
        int x = 0;
        int y = 0;

        y = sy;
        while (y <= ey) {
            x = sx;
            while (x <= ex) {
                if ((chaosObjects.Get(x - 1, y) < ChaosGraphics.NbBackground) && (chaosObjects.Get(x + 1, y) < ChaosGraphics.NbBackground) && (chaosObjects.Get(x, y - 1) < ChaosGraphics.NbBackground) && (chaosObjects.Get(x, y + 1) < ChaosGraphics.NbBackground))
                    chaosObjects.Put(x, y, val);
                x += dx;
            }
            y += dy;
        }
    }

    public void VRace(int val) {
        // CONST
        final int W = 120;
        final int H = 60;
        final int ST = W * 4 / 16;
        final int SE = W * 4 / 150;
        final int BE = W * 4 / 50;
        final int MX = W / 2;
        final int MY = H / 2;
        final int WP = MX - (SE + BE + ST + 15) / 4;
        final int HP = MY - (SE + BE + ST + 15) / 4;

        // VAR
        long dx = 0L;
        long dy = 0L;
        long dl = 0L;
        int ap = 0;
        int at = 0;
        int ae = 0;
        int vp = 0;
        int vt = 0;
        int ve = 0;
        int nt = 0;
        int ne = 0;
        int dt = 0;
        int de = 0;
        int x = 0;
        int y = 0;
        int sz = 0;
        int lx = 0;
        int ly = 0;

        /* 46 */
        /* 16 */
        lx = 0;
        ly = 0;
        dt = 7 + chaosObjects.Rnd(7);
        de = 4 + chaosObjects.Rnd(7);
        vp = 2;
        ap = 0;
        at = chaosObjects.Rnd(180);
        nt = at;
        ae = chaosObjects.Rnd(180);
        ne = ae;
        do {
            if (at >= nt) {
                vt = vp * dt / 2 + vp * chaosObjects.Rnd(dt);
                nt += chaosObjects.Rnd(200);
            }
            if (ae >= ne) {
                ve = vp * de / 2 + vp * chaosObjects.Rnd(de);
                ne += chaosObjects.Rnd(200);
            }
            x = MX + trigo.SIN(ap) / 2 * WP / 512;
            y = MY + trigo.SIN(ap * 2) / 2 * HP / 512;
            dx = -trigo.COS(ap * 2);
            dy = trigo.COS(ap) * HP / WP;
            dl = trigo.SQRT(dx * dx + dy * dy);
            dx = dx * 1024 / dl;
            dy = dy * 1024 / dl;
            dl = trigo.SIN(at) * ST / 4096;
            x += dl * dx / 1024;
            y += dl * dy / 1024;
            sz = (trigo.SIN(ae) * SE / 1024 + BE) / 4;
            if (lx == 0) {
                lx = x;
                ly = y;
            }
            Road(lx, ly, x, y, sz, val);
            lx = x;
            ly = y;
            ap += vp;
            at += vt;
            ae += ve;
        } while (ap < 362);
    }

    private void DrawCastle_Rect(int psx, int psy, int dy, int pex, int pey, int sx, int w) {
        // VAR
        int x = 0;
        int y = 0;
        int py = 0;
        int val = 0;

        py = chaosObjects.Rnd(3) + psy;
        if ((psx == sx) || (pex == w))
            py = psy;
        for (y = psy + dy; y <= pey; y++) {
            for (x = psx; x <= pex; x++) {
                if (((x != psx) && (x != pex) && (y != pey)) || (y == py))
                    val = 0;
                else
                    val = ChaosGraphics.NbBackground + 1;
                if (chaosObjects.Get(x, y) == 1)
                    chaosObjects.Put(x, y, val);
            }
        }
    }

    public void DrawCastle(int sx, int sy, int w, int h) {
        // VAR
        int x = 0;
        int y = 0;
        int ex = 0;
        int ey = 0;
        int dy = 0;
        boolean start = false;

        w = w + sx - 1;
        h = h + sy - 1;
        chaosObjects.Fill(sx, sy, w, h, 1);
        y = sy;
        start = true;
        do {
            x = sx;
            do {
                ex = chaosObjects.Rnd(6) + 4 + x;
                ey = chaosObjects.Rnd(4) + 3 + y;
                if (ex >= w - 4)
                    ex = w;
                if (y == sy)
                    dy = -1;
                else
                    dy = -6;
                DrawCastle_Rect(x, y, dy, ex, ey, sx, w);
                x = ex - chaosObjects.Rnd(3);
            } while (x < w);
            if (start)
                chaosObjects.Put(sx - 1, y + 1, ChaosGraphics.NbBackground);
            else
                chaosObjects.Put(ex + 1, y + 1, ChaosGraphics.NbBackground);
            y += 8;
            start = !start;
        } while (y < h);
    }

    public void DrawBoxes(int sx, int sy, int w, int h) {
        // VAR
        int x = 0;
        int y = 0;
        int minx = 0;
        int miny = 0;
        int maxx = 0;
        int maxy = 0;
        int c = 0;
        boolean k = false;

        maxy = -1;
        do {
            k = true;
            miny = maxy + 2;
            maxy += chaosObjects.Rnd(8) + 6;
            if (maxy > h - 8) {
                maxy = h - 1;
                chaosObjects.Set(sx + 1, sy + maxy);
            }
            for (x = 2; x < w; x++) {
                chaosObjects.Set(sx + x, sy + maxy);
            }
            maxy--;
            maxx = -1;
            do {
                minx = maxx + 2;
                maxx += chaosObjects.Rnd(8) + 6;
                if (maxx > w - 8)
                    maxx = w - 1;
                for (y = miny; y <= maxy; y++) {
                    chaosObjects.Set(sx + maxx, sy + y);
                }
                if (maxx != w - 1) {
                    if (k)
                        chaosObjects.Reset(sx + maxx, sy + maxy);
                    else
                        chaosObjects.Reset(sx + maxx, sy + miny);
                }
                maxx--;
                y = miny;
                while (true) {
                    y += chaosObjects.Rnd(3) + 3;
                    if (y >= maxy)
                        break;
                    for (x = minx; x <= maxx; x++) {
                        chaosObjects.Set(sx + x, sy + y);
                    }
                    for (c = 1; c <= 5; c++) {
                        x = minx + chaosObjects.Rnd(maxx - minx + 1);
                        chaosObjects.Reset(sx + x, sy + y);
                    }
                }
                k = !k;
            } while (maxx < w - 2);
        } while (maxy < h - 2);
    }

    public void Join(int sx, int sy, int ex, int ey, int dx, int dy, int val) {
        // VAR
        int x = 0;
        int y = 0;

        x = sx;
        y = sy;
        do {
            chaosObjects.Put(x, y, val);
            if (x == ex)
                dx = 0;
            if (y == ey)
                dy = 0;
            if (trigo.RND() % 2 == 0)
                x += dx;
            else
                y += dy;
        } while (!((dx == 0) && (dy == 0)));
    }

    /* Modifiers */
    public void FlipVert() {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.Obj tail = null;
        long gh = 0L;
        long ry = 0L;
        int x = 0;
        int y = 0;
        int my = 0;
        Anims a = Anims.PLAYER;
        int swp = 0;

        my = chaosGraphics.castleHeight - 1;
        gh = chaosGraphics.gameHeight;
        gh = gh * ChaosBase.Frac;
        for (x = 0; x < chaosGraphics.castleWidth; x++) {
            for (y = 0; y < chaosGraphics.castleHeight / 2; y++) {
                swp = chaosGraphics.castle[y][x];
                chaosGraphics.castle[y][x] = chaosGraphics.castle[my - y][x];
                chaosGraphics.castle[my - y][x] = swp;
            }
        }
        for (int _a = 0; _a < Anims.values().length; _a++) {
            a = Anims.values()[_a];
            obj = (ChaosBase.Obj) memory.First(chaosBase.animList[a.ordinal()]);
            tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[a.ordinal()]);
            while (obj != tail) {
                ry = obj.height / chaosGraphics.mulS;
                obj.y = gh - obj.y - ry * ChaosBase.Frac;
                obj.midy = obj.y;
                if ((a == Anims.MACHINE) && (obj.subKind == ChaosMachine.mCannon2)) {
                    obj.stat = 1 - obj.stat;
                    obj.attr.Make.invoke(obj);
                } else if ((a == Anims.ALIEN1) && (obj.subKind == ChaosAlien.aKamikaze)) {
                    if (obj.stat >= 2)
                        obj.stat -= 2;
                    else
                        obj.stat += 2;
                    obj.attr.Make.invoke(obj);
                }
                obj = (ChaosBase.Obj) memory.Next(obj.animNode);
            }
        }
    }

    public void FlipHorz() {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.Obj tail = null;
        long gw = 0L;
        long rx = 0L;
        int x = 0;
        int y = 0;
        int mx = 0;
        Anims a = Anims.PLAYER;
        int swp = 0;

        mx = chaosGraphics.castleWidth - 1;
        gw = chaosGraphics.gameWidth;
        gw = gw * ChaosBase.Frac;
        for (y = 0; y < chaosGraphics.castleHeight; y++) {
            for (x = 0; x < chaosGraphics.castleWidth / 2; x++) {
                swp = chaosGraphics.castle[y][x];
                chaosGraphics.castle[y][x] = chaosGraphics.castle[y][mx - x];
                chaosGraphics.castle[y][mx - x] = swp;
            }
        }
        for (int _a = 0; _a < Anims.values().length; _a++) {
            a = Anims.values()[_a];
            obj = (ChaosBase.Obj) memory.First(chaosBase.animList[a.ordinal()]);
            tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[a.ordinal()]);
            while (obj != tail) {
                rx = obj.width / chaosGraphics.mulS;
                obj.x = gw - obj.x - rx * ChaosBase.Frac;
                obj.midx = obj.x;
                if ((a == Anims.MACHINE) && (obj.subKind == ChaosMachine.mCannon1)) {
                    obj.stat = 1 - obj.stat;
                    obj.attr.Make.invoke(obj);
                } else if ((a == Anims.ALIEN1) && (obj.subKind == ChaosAlien.aKamikaze)) {
                    if (((obj.stat % 2) != 0))
                        obj.stat--;
                    else
                        obj.stat++;
                    obj.attr.Make.invoke(obj);
                } else if ((a == Anims.ALIEN1) && (obj.subKind == ChaosAlien.aPic)) {
                    obj.stat = 1 - obj.stat;
                    obj.attr.Make.invoke(obj);
                }
                obj = (ChaosBase.Obj) memory.Next(obj.animNode);
            }
        }
    }

    public void Rotate() {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.Obj tail = null;
        long tmp = 0L;
        long rx = 0L;
        int x = 0;
        int y = 0;
        int m = 0;
        int t = 0;
        Anims a = Anims.PLAYER;
        int swp = 0;

        if (chaosGraphics.castleWidth > chaosGraphics.castleHeight)
            m = chaosGraphics.castleWidth - 1;
        else
            m = chaosGraphics.castleHeight - 1;
        for (y = 1; y <= m; y++) {
            for (x = 0; x < y; x++) {
                swp = chaosGraphics.castle[y][x];
                chaosGraphics.castle[y][x] = chaosGraphics.castle[x][y];
                chaosGraphics.castle[x][y] = swp;
            }
        }
        for (int _a = 0; _a < Anims.values().length; _a++) {
            a = Anims.values()[_a];
            obj = (ChaosBase.Obj) memory.First(chaosBase.animList[a.ordinal()]);
            tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[a.ordinal()]);
            while (obj != tail) {
                tmp = obj.x;
                obj.x = obj.y;
                obj.y = tmp;
                obj.midx = obj.x;
                obj.midy = obj.y;
                rx = obj.cy - obj.cx;
                obj.x += rx * ChaosBase.Frac;
                obj.y -= rx * ChaosBase.Frac;
                if ((a == Anims.MACHINE) && (obj.subKind == ChaosMachine.mTraverse)) {
                    obj.stat = 1 - obj.stat;
                    obj.attr.Reset.invoke(obj);
                } else if ((a == Anims.ALIEN1) && (obj.subKind == ChaosAlien.aKamikaze)) {
                    if (obj.stat == 1)
                        obj.stat = 2;
                    else if (obj.stat == 2)
                        obj.stat = 1;
                    obj.attr.Make.invoke(obj);
                }
                obj = (ChaosBase.Obj) memory.Next(obj.animNode);
            }
        }
        t = chaosGraphics.gameWidth;
        chaosGraphics.gameWidth = chaosGraphics.gameHeight;
        chaosGraphics.gameHeight = t;
        t = chaosGraphics.castleWidth;
        chaosGraphics.castleWidth = chaosGraphics.castleHeight;
        chaosGraphics.castleHeight = t;
    }


    // Support

    private static ChaosGenerator instance;

    public static ChaosGenerator instance() {
        if (instance == null)
            new ChaosGenerator(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
    }

    public void close() {
    }

}
