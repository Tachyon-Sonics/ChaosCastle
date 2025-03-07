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

    public void DrawPacman(int open, short width, short height, short sx, short sy, short ex, short ey) {
        // VAR
        short x = 0;
        short y = 0;
        short v = 0;
        short h = 0;
        short z = 0;

        y = ey;
        while (y >= sy) {
            x = ex;
            while (x >= sx) {
                chaosObjects.Set(x, y);
                if (trigo.RND() % 16 > open) {
                    h = (short) (trigo.RND() % 2);
                    v = (short) (1 - h);
                    for (z = 1; z < width * h + height * v; z++) {
                        chaosObjects.Set((short) (x + h * z), (short) (y + v * z));
                    }
                }
                x -= width;
            }
            y -= height;
        }
    }

    public void Road(short sx, short sy, short ex, short ey, short sz, short val) {
        // VAR
        short w = 0;
        short h = 0;
        short m = 0;
        short c = 0;
        short x = 0;
        short y = 0;

        w = (short) Math.abs(ex - sx);
        h = (short) Math.abs(ey - sy);
        c = 0;
        if (w > h)
            m = w;
        else
            m = h;
        w = (short) (ex - sx);
        h = (short) (ey - sy);
        while (c < m) {
            x = (short) (sx + w * c / m);
            y = (short) (sy + h * c / m);
            chaosObjects.Fill((short) (x - sz), (short) (y - sz), (short) (x + sz), (short) (y + sz), val);
            c++;
        }
    }

    public void Excavate(short sx, short ex, short mny, short mxy, short mnh, short mxh, short sdy, short sh, short sd, short mxsy, short mxsh, short mnd, short mxd) {
        // VAR
        short x = 0;
        short y = 0;
        short py = 0;
        short h = 0;
        short d = 0;
        short dw = 0;
        short syw = 0;
        short shw = 0;

        x = sx;
        y = (short) (mny + sdy);
        h = sh;
        d = sd;
        dw = (short) (mxd - mnd + 1);
        syw = (short) (mxsy * 2 + 1);
        shw = (short) (mxsh * 2 + 1);
        while (x <= ex) {
            if (d == 0) {
                d = (short) (chaosObjects.Rnd((short) (mxd - mnd + 1)) + mnd);
                h += chaosObjects.Rnd(shw) - mxsh;
                if (h < mnh)
                    h = mnh;
                else if (h > mxh)
                    h = mxh;
                y += chaosObjects.Rnd(syw) - mxsy;
                if (y < mny)
                    y = mny;
                else if (y + h > mxy)
                    y = (short) (mxy - h + 1);
            }
            py = (short) (y + h);
            while (py != y) {
                py--;
                chaosObjects.Put(x, py, (short) 0);
            }
            x++;
            d--;
        }
    }

    private boolean PutCross_CheckPlace(short bx, short by, short w, short h, short x, short y, short s) {
        // VAR
        short px = 0;
        short py = 0;

        if ((x - s - 1 <= bx) && (y - s - 1 <= by))
            return false;
        if ((x + s + 2 >= bx + w) && (y + s + 2 >= by + h))
            return false;
        for (px = (short) (x - s - 1); px <= x + s + 1; px++) {
            for (py = (short) (y - 1); py <= y + 1; py++) {
                if (chaosObjects.Get(px, py) >= ChaosGraphics.NbBackground)
                    return false;
            }
        }
        for (py = (short) (y - s - 1); py <= y + s + 1; py++) {
            for (px = (short) (x - 1); px <= x + 1; px++) {
                if (chaosObjects.Get(px, py) >= ChaosGraphics.NbBackground)
                    return false;
            }
        }
        return true;
    }

    private boolean PutCross_FindPlace(short bx, short by, short w, short h, /* VAR */ Runtime.IRef<Short> x, /* VAR */ Runtime.IRef<Short> y, /* VAR */ Runtime.IRef<Short> s, /* VAR */ Runtime.IRef<Short> z) {
        // VAR
        int timeOut = 0;

        timeOut = 50;
        do {
            timeOut--;
            s.set((short) (trigo.RND() % 4 + 1));
            z.set((short) (2 * s.get() + 1));
            x.set((short) (chaosObjects.Rnd((short) (w - z.get())) + bx + s.get()));
            y.set((short) (chaosObjects.Rnd((short) (h - z.get())) + by + s.get()));
            if (PutCross_CheckPlace(bx, by, w, h, x.get(), y.get(), s.get()))
                return true;
        } while (timeOut != 0);
        return false;
    }

    private void PutCross_PutIt(short val, short x, short y, short s) {
        // VAR
        short pz = 0;

        for (pz = (short) (x - s); pz <= x + s; pz++) {
            chaosObjects.Put(pz, y, val);
        }
        for (pz = (short) (y - s); pz <= y + s; pz++) {
            chaosObjects.Put(x, pz, val);
        }
    }

    public void PutCross(short bx, short by, short w, short h, short val) {
        // VAR
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> s = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> z = new Runtime.Ref<>((short) 0);

        if (PutCross_FindPlace(bx, by, w, h, x, y, s, z))
            PutCross_PutIt(val, x.get(), y.get(), s.get());
    }

    private boolean CheckLink(short x, short y, short sz, short ml, /* VAR */ Runtime.IRef<Short> angle) {
        // VAR
        int timeOut = 0;
        short length = 0;
        short px = 0;
        short py = 0;
        short dx = 0;
        short dy = 0;

        angle.set((short) ((trigo.RND() % 4) * 90));
        timeOut = 4;
        do {
            dx = (short) (trigo.COS(angle.get()) / 1024);
            dy = (short) (trigo.SIN(angle.get()) / 1024);
            px = (short) (x + dx * (sz + 1));
            py = (short) (y + dy * (sz + 1));
            length = 0;
            while ((length <= ml) && (chaosObjects.Get(px, py) >= ChaosGraphics.NbBackground)) {
                px += dx;
                py += dy;
                length++;
                if ((px < 0) || (py < 0) || (px >= chaosGraphics.castleWidth) || (py >= chaosGraphics.castleHeight))
                    length = (short) (ml + 1);
            }
            if (length <= ml)
                return true;
            angle.set((short) ((angle.get() + 90) % 360));
            timeOut--;
        } while (timeOut != 0);
        return false;
    }

    public boolean FindIsolatedRect(short sx, short sy, short ex, short ey, short sz, short ml, /* VAR+WRT */ Runtime.IRef<Short> angle, /* VAR */ Runtime.IRef<Short> x, /* VAR */ Runtime.IRef<Short> y, boolean wall) {
        // VAR
        short dx = 0;
        short dy = 0;
        short w = 0;
        short h = 0;
        int timeOut = 0;
        int backs = 0;
        boolean res = false;

        w = (short) (ex - sx - sz * 2 - 1);
        sx += sz + 1;
        h = (short) (ey - sy - sz * 2 - 1);
        sy += sz + 1;
        timeOut = 20;
        do {
            timeOut--;
            x.set((short) (chaosObjects.Rnd(w) + sx));
            y.set((short) (chaosObjects.Rnd(h) + sy));
            backs = 0;
            for (dy = (short) (-sz - 1); dy <= sz + 1; dy++) {
                for (dx = (short) (-sz - 1); dx <= sz + 1; dx++) {
                    res = chaosObjects.Get((short) (x.get() + dx), (short) (y.get() + dy)) < ChaosGraphics.NbBackground;
                    if (res == wall)
                        backs++;
                }
            }
            if ((backs == 0) && wall && !CheckLink(x.get(), y.get(), sz, ml, angle))
                backs = 1;
        } while (!((backs == 0) || (timeOut == 0)));
        return backs == 0;
    }

    public void MakeLink(short x, short y, short sz, short angle, short val) {
        // VAR
        short px = 0;
        short py = 0;
        short dx = 0;
        short dy = 0;

        dx = (short) (trigo.COS(angle) / 1024);
        dy = (short) (trigo.SIN(angle) / 1024);
        px = (short) (x + dx * (sz + 1));
        py = (short) (y + dy * (sz + 1));
        while (chaosObjects.Get(px, py) >= ChaosGraphics.NbBackground) {
            chaosObjects.Put(px, py, val);
            px += dx;
            py += dy;
        }
    }

    public void FillEllipse(int sx, int sy, int pw, int ph, short val) {
        // VAR
        int by = 0;
        int ey = 0;
        int ex = 0;
        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;
        int pw2 = 0;
        int ph2 = 0;
        int w2 = 0;
        int phd = 0;
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
            chaosObjects.Fill((short) x, (short) by, (short) ex, (short) by, val);
            if (h != 0)
                chaosObjects.Fill((short) x, (short) ey, (short) ex, (short) ey, val);
            y++;
            h += 2;
        } while (h <= 0);
    }

    public void TripleLoop(short val) {
        // VAR
        short angle = 0;
        short sz = 0;
        short ml = 0;
        short bs = 0;
        short lx = 0;
        short ly = 0;
        int r = 0;
        int x = 0;
        int y = 0;

        lx = 0;
        ly = 0;
        ml = (short) (trigo.RND() % 8 + 1);
        bs = (short) (trigo.RND() % 360);
        for (angle = 0; angle <= 702; angle += 2) {
            sz = (short) ((trigo.SIN((short) (bs + angle * ml)) + 1024) / 700 + 1);
            r = trigo.SIN((short) (angle * 3 / 2));
            x = trigo.COS(angle) * (2048 + r) / 54330;
            y = trigo.SIN(angle) * (2048 + r) / 54330;
            x += 62;
            y += 62;
            if ((lx != 0) && (ly != 0))
                Road(lx, ly, (short) x, (short) y, sz, val);
            lx = (short) x;
            ly = (short) y;
        }
    }

    public void GCastle(short sx, short sy, short ex, short ey, short wall, short back) {
        // VAR
        short x = 0;
        short y = 0;
        int c = 0;

        for (y = sy; y <= ey; y += 2) {
            chaosObjects.Fill(sx, y, ex, y, wall);
            for (c = 1; c <= 3; c++) {
                x = (short) (chaosObjects.Rnd((short) (ex - sx + 1)) + 1);
                chaosObjects.Put(x, y, back);
            }
        }
    }

    public void Cave(short sx, short sy, short ex, short ey, short y1, short y2, short dx) {
        // VAR
        short x = 0;
        short d1 = 0;
        short d2 = 0;
        short t1 = 0;
        short t2 = 0;

        x = sx;
        d1 = 1;
        d2 = -1;
        t1 = 0;
        t2 = 0;
        do {
            chaosObjects.Fill(x, y1, x, y2, (short) 0);
            x += dx;
            if (t1 == 0) {
                if (trigo.RND() % 2 == 0)
                    d1 = 1;
                else
                    d1 = -1;
                t1 = (short) (trigo.RND() % 8 + 3);
            } else {
                t1--;
            }
            if (t2 == 0) {
                if (trigo.RND() % 2 == 0)
                    d2 = 1;
                else
                    d2 = -1;
                t2 = (short) (trigo.RND() % 8 + 3);
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
        chaosObjects.PutExit((short) 12, (short) (y1 - d1));
        chaosObjects.PutExit((short) 12, (short) (y2 - d2));
    }

    public void DrawFactory() {
        // VAR
        short x = 0;
        short y = 0;
        short dx = 0;
        short dy = 0;
        short mnx = 0;
        short mxx = 0;
        short nxx = 0;
        short l = 0;
        short s = 0;
        short sz = 0;
        short sz2 = 0;
        int pass = 0;
        short val = 0;
        short v2 = 0;

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
                sz = (short) (trigo.RND() % 4 + 2);
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
                chaosObjects.FillCond((short) (x - sz), (short) (y - sz), (short) (x + sz), (short) (y + sz), Runtime.proc(chaosObjects::OnlyWall, "ChaosObjects.OnlyWall"), val);
                val = (short) (trigo.RND() % 4 + pass + 25);
                sz2 = chaosObjects.Rnd((short) (sz - 1));
                chaosObjects.Fill((short) (x - sz2), (short) (y - sz2), (short) (x + sz2), (short) (y + sz2), val);
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
                        dy = (short) -s;
                        dx = 0;
                    } else {
                        dy = s;
                        dx = 0;
                    }
                } else {
                    mxx = (short) (nxx + 1);
                    if (trigo.RND() % 3 == 0) {
                        dy = (short) -s;
                        dx = 0;
                    } else {
                        dx = 1;
                        dy = 0;
                    }
                }
                l = (short) (6 + trigo.RND() % 8);
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
                mxx = (short) (mnx + 13);
                if (nxx > 95)
                    nxx = 95;
                s = (short) -s;
                pass++;
                if (pass > 3)
                    break;
            }
        }
        MakeLink((short) 12, (short) 90, (short) -1, (short) -90, (short) 20);
        chaosObjects.Put(x, y, (short) 11);
        chaosObjects.PutExit(x, y);
    }

    private void DrawLabyrinth_Cut(short size, short x, short y, short px, short py, /* VAR */ Runtime.IRef<Boolean> flag) {
        // VAR
        short z = 0;
        short v = 0;
        boolean h = false;

        h = (trigo.RND() % 2 == 0);
        if (h)
            z = x;
        else
            z = y;
        v = chaosObjects.Rnd((short) 50);
        flag.set((z * size < v * v));
        v = (short) (chaosObjects.Rnd(size) - 2 * z);
        chaosObjects.Put(px, py, (short) 0);
        if (v >= 0) {
            if (h)
                chaosObjects.Put((short) (px + 1), py, (short) 0);
            else
                chaosObjects.Put(px, (short) (py + 1), (short) 0);
        } else {
            if (h)
                chaosObjects.Put((short) (px - 1), py, (short) 0);
            else
                chaosObjects.Put(px, (short) (py - 1), (short) 0);
        }
    }

    public void DrawLabyrinth(short size) {
        // VAR
        short x = 0;
        short y = 0;
        short px = 0;
        short py = 0;
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

    public void RemIsolated(short sx, short sy, short ex, short ey, short dx, short dy, short val) {
        // VAR
        short x = 0;
        short y = 0;

        y = sy;
        while (y <= ey) {
            x = sx;
            while (x <= ex) {
                if ((chaosObjects.Get((short) (x - 1), y) < ChaosGraphics.NbBackground) && (chaosObjects.Get((short) (x + 1), y) < ChaosGraphics.NbBackground) && (chaosObjects.Get(x, (short) (y - 1)) < ChaosGraphics.NbBackground) && (chaosObjects.Get(x, (short) (y + 1)) < ChaosGraphics.NbBackground))
                    chaosObjects.Put(x, y, val);
                x += dx;
            }
            y += dy;
        }
    }

    public void VRace(short val) {
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
        int dx = 0;
        int dy = 0;
        int dl = 0;
        short ap = 0;
        short at = 0;
        short ae = 0;
        short vp = 0;
        short vt = 0;
        short ve = 0;
        short nt = 0;
        short ne = 0;
        short dt = 0;
        short de = 0;
        short x = 0;
        short y = 0;
        short sz = 0;
        short lx = 0;
        short ly = 0;

        lx = 0;
        ly = 0;
        dt = (short) (7 + chaosObjects.Rnd((short) 7));
        de = (short) (4 + chaosObjects.Rnd((short) 7));
        vp = 2;
        ap = 0;
        at = chaosObjects.Rnd((short) 180);
        nt = at;
        ae = chaosObjects.Rnd((short) 180);
        ne = ae;
        do {
            if (at >= nt) {
                vt = (short) (vp * dt / 2 + vp * chaosObjects.Rnd(dt));
                nt += chaosObjects.Rnd((short) 200);
            }
            if (ae >= ne) {
                ve = (short) (vp * de / 2 + vp * chaosObjects.Rnd(de));
                ne += chaosObjects.Rnd((short) 200);
            }
            x = (short) (MX + trigo.SIN(ap) / 2 * WP / 512);
            y = (short) (MY + trigo.SIN((short) (ap * 2)) / 2 * HP / 512);
            dx = -trigo.COS((short) (ap * 2));
            dy = trigo.COS(ap) * HP / WP;
            dl = trigo.SQRT(dx * dx + dy * dy);
            dx = dx * 1024 / dl;
            dy = dy * 1024 / dl;
            dl = trigo.SIN(at) * ST / 4096;
            x += dl * dx / 1024;
            y += dl * dy / 1024;
            sz = (short) ((trigo.SIN(ae) * SE / 1024 + BE) / 4);
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

    private void DrawCastle_Rect(short psx, short psy, short dy, short pex, short pey, short sx, short w) {
        // VAR
        short x = 0;
        short y = 0;
        short py = 0;
        short val = 0;

        py = (short) (chaosObjects.Rnd((short) 3) + psy);
        if ((psx == sx) || (pex == w))
            py = psy;
        for (y = (short) (psy + dy); y <= pey; y++) {
            for (x = psx; x <= pex; x++) {
                if (((x != psx) && (x != pex) && (y != pey)) || (y == py))
                    val = 0;
                else
                    val = (short) (ChaosGraphics.NbBackground + 1);
                if (chaosObjects.Get(x, y) == 1)
                    chaosObjects.Put(x, y, val);
            }
        }
    }

    public void DrawCastle(short sx, short sy, short w, short h) {
        // VAR
        short x = 0;
        short y = 0;
        short ex = 0;
        short ey = 0;
        short dy = 0;
        boolean start = false;

        w = (short) (w + sx - 1);
        h = (short) (h + sy - 1);
        chaosObjects.Fill(sx, sy, w, h, (short) 1);
        y = sy;
        start = true;
        do {
            x = sx;
            do {
                ex = (short) (chaosObjects.Rnd((short) 6) + 4 + x);
                ey = (short) (chaosObjects.Rnd((short) 4) + 3 + y);
                if (ex >= w - 4)
                    ex = w;
                if (y == sy)
                    dy = -1;
                else
                    dy = -6;
                DrawCastle_Rect(x, y, dy, ex, ey, sx, w);
                x = (short) (ex - chaosObjects.Rnd((short) 3));
            } while (x < w);
            if (start)
                chaosObjects.Put((short) (sx - 1), (short) (y + 1), (short) ChaosGraphics.NbBackground);
            else
                chaosObjects.Put((short) (ex + 1), (short) (y + 1), (short) ChaosGraphics.NbBackground);
            y += 8;
            start = !start;
        } while (y < h);
    }

    public void DrawBoxes(short sx, short sy, short w, short h) {
        // VAR
        short x = 0;
        short y = 0;
        short minx = 0;
        short miny = 0;
        short maxx = 0;
        short maxy = 0;
        int c = 0;
        boolean k = false;

        maxy = -1;
        do {
            k = true;
            miny = (short) (maxy + 2);
            maxy += chaosObjects.Rnd((short) 8) + 6;
            if (maxy > h - 8) {
                maxy = (short) (h - 1);
                chaosObjects.Set((short) (sx + 1), (short) (sy + maxy));
            }
            for (x = 2; x < w; x++) {
                chaosObjects.Set((short) (sx + x), (short) (sy + maxy));
            }
            maxy--;
            maxx = -1;
            do {
                minx = (short) (maxx + 2);
                maxx += chaosObjects.Rnd((short) 8) + 6;
                if (maxx > w - 8)
                    maxx = (short) (w - 1);
                for (y = miny; y <= maxy; y++) {
                    chaosObjects.Set((short) (sx + maxx), (short) (sy + y));
                }
                if (maxx != w - 1) {
                    if (k)
                        chaosObjects.Reset((short) (sx + maxx), (short) (sy + maxy));
                    else
                        chaosObjects.Reset((short) (sx + maxx), (short) (sy + miny));
                }
                maxx--;
                y = miny;
                while (true) {
                    y += chaosObjects.Rnd((short) 3) + 3;
                    if (y >= maxy)
                        break;
                    for (x = minx; x <= maxx; x++) {
                        chaosObjects.Set((short) (sx + x), (short) (sy + y));
                    }
                    for (c = 1; c <= 5; c++) {
                        x = (short) (minx + chaosObjects.Rnd((short) (maxx - minx + 1)));
                        chaosObjects.Reset((short) (sx + x), (short) (sy + y));
                    }
                }
                k = !k;
            } while (maxx < w - 2);
        } while (maxy < h - 2);
    }

    public void Join(short sx, short sy, short ex, short ey, short dx, short dy, short val) {
        // VAR
        short x = 0;
        short y = 0;

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

    public void FlipVert() {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.Obj tail = null;
        int gh = 0;
        int ry = 0;
        short x = 0;
        short y = 0;
        short my = 0;
        Anims a = Anims.PLAYER;
        short swp = 0;

        my = (short) (chaosGraphics.castleHeight - 1);
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
        int gw = 0;
        int rx = 0;
        short x = 0;
        short y = 0;
        short mx = 0;
        Anims a = Anims.PLAYER;
        short swp = 0;

        mx = (short) (chaosGraphics.castleWidth - 1);
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
        int tmp = 0;
        int rx = 0;
        short x = 0;
        short y = 0;
        short m = 0;
        short t = 0;
        Anims a = Anims.PLAYER;
        short swp = 0;

        if (chaosGraphics.castleWidth > chaosGraphics.castleHeight)
            m = (short) (chaosGraphics.castleWidth - 1);
        else
            m = (short) (chaosGraphics.castleHeight - 1);
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
