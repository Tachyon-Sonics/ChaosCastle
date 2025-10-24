package ch.chaos.grotte;

import ch.chaos.grotte.GrotteSupport.OBJECT;
import ch.chaos.library.ANSITerm;
import ch.chaos.library.Clock;
import ch.chaos.library.Memory;
import ch.pitchtech.modula.runtime.Runtime;


public class GrotteBonus {

    // Imports
    private final ANSITerm aNSITerm;
    private final Clock clock;
    private final GrotteSupport grotteSupport;


    private GrotteBonus() {
        instance = this; // Set early to handle circular dependencies
        aNSITerm = ANSITerm.instance();
        clock = Clock.instance();
        grotteSupport = GrotteSupport.instance();
    }


    // VAR

    /*★★★★ Niveaux Bonus *****/
    private Clock.TimePtr clock0;


    public Clock.TimePtr getClock0() {
        return this.clock0;
    }

    public void setClock0(Clock.TimePtr clock0) {
        this.clock0 = clock0;
    }


    // PROCEDURE

    private void WrAt(int x, int y, char ch) {
        // VAR
        boolean w = false;

        if (clock0 != clock.noTime)
            w = clock.WaitTime(clock0, 1);
        aNSITerm.WriteAt(x, y, ch);
    }

    private void Cadre(boolean H) {
        // VAR
        int c = 0;

        aNSITerm.Goto(0, 0);
        aNSITerm.Color(7);
        for (c = 0; c <= 71; c++) {
            aNSITerm.Write('S');
        }
        aNSITerm.Goto(0, 19);
        for (c = 0; c <= 71; c++) {
            if (H) {
                aNSITerm.Color(6);
                aNSITerm.Write('H');
            } else {
                aNSITerm.Color(7);
                aNSITerm.Write('S');
            }
        }
        aNSITerm.Color(7);
        for (c = 0; c <= 19; c++) {
            WrAt(0, c, 'S');
            WrAt(71, c, 'S');
        }
    }

    private void Find(/* VAR */ Runtime.IRef<Integer> x, /* VAR */ Runtime.IRef<Integer> y, int sx, int sy, int dx, int dy) {
        // VAR
        int w = 0;
        int h = 0;

        w = dx - sx + 1;
        h = dy - sy + 1;
        do {
            x.set(grotteSupport.Rnd() % w);
            x.inc(sx);
            y.set(grotteSupport.Rnd() % h);
            y.inc(sy);
        } while (aNSITerm.Report(x.get(), y.get()) != ' ');
    }

    private void Put(char ch, int n, int sx, int sy, int dx, int dy) {
        // VAR
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int c = 0;

        for (c = 1; c <= n; c++) {
            Find(x, y, sx, sy, dx, dy);
            WrAt(x.get(), y.get(), ch);
        }
    }

    private void PutObj(OBJECT t, int n, int sx, int sy, int dx, int dy, int nvie, int nseq, char ch) {
        // VAR
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int c = 0;

        for (c = 1; c <= n; c++) {
            Find(x, y, sx, sy, dx, dy);
            grotteSupport.Create.invoke(t, x.get(), y.get(), nvie, nseq, ch);
        }
    }

    private void PutDeltaObj(OBJECT t, int n, int sx, int sy, int dx, int dy, int h, int v, int nvie, int nseq, char ch) {
        // VAR
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int c = 0;

        for (c = 1; c <= n; c++) {
            Find(x, y, sx, sy, dx, dy);
            while (aNSITerm.Report(x.get() + h, y.get() + v) == ' ') {
                x.inc(h);
                y.inc(v);
            }
            grotteSupport.Create.invoke(t, x.get(), y.get(), nvie, nseq, ch);
        }
    }

    private void DrawK0(int sx, int sy, int dx, int dy, int nseq) {
        // VAR
        int c1 = 0;
        int c2 = 0;

        c1 = 0;
        c2 = 1;
        while ((grotteSupport.pvie > c2 + 2) && (c2 < 64)) {
            c1++;
            c2 = c2 * 2;
        }
        PutObj(OBJECT.K0, c1, sx, sy, dx, dy, grotteSupport.pvie / 4 + 1, nseq, '&');
    }

    private void BonusLevel1(/* VAR */ Runtime.IRef<Integer> ax, /* VAR */ Runtime.IRef<Integer> ay) {
        // VAR
        int dx = 0;
        int dy = 0;
        int x = 0;
        int y = 0;
        int c = 0;

        /* MiniGrotte */
        Cadre(false);
        aNSITerm.Color(6);
        for (x = 40; x <= 71; x++) {
            WrAt(x, 19, 'H');
        }
        aNSITerm.Color(7);
        for (y = 2; y <= 18; y++) {
            WrAt(39, y, 'S');
        }
        ax.set(39);
        ay.set(18);
        dx = 1;
        for (y = 2; y <= 14; y += 4) {
            dy = grotteSupport.Rnd() % 3;
            dx = 2 - dy;
            for (x = 1; x <= 30; x++) {
                WrAt(x, y + dy, 'S');
            }
            for (c = 1; c <= 4; c++) {
                x = grotteSupport.Rnd() % 32 + 1;
                for (dy = y + 1; dy <= y + dx; dy++) {
                    WrAt(x, dy, 'S');
                }
            }
        }
        for (c = 1; c <= 200; c++) {
            WrAt(grotteSupport.Rnd() % 35 + 1, grotteSupport.Rnd() % 16 + 2, ' ');
        }
        for (y = 2; y <= 16; y += 2) {
            x = 39;
            while (true) {
                x += grotteSupport.Rnd() % 8 + 1;
                dy = grotteSupport.Rnd() % 2;
                if (x > 69)
                    break;
                WrAt(x, y + dy, 'S');
                if (grotteSupport.Rnd() % 2 == 1)
                    WrAt(x + 1, y + dy, 'S');
            }
        }
        grotteSupport.Create.invoke(OBJECT.PLAYER, 1, 1, grotteSupport.pvie, 0, '*');
        grotteSupport.Create.invoke(OBJECT.ASC, 37, 16, 1, 0, '=');
        aNSITerm.Color(3);
        WrAt(35, 1, '!');
        grotteSupport.ndoor++;
        PutObj(OBJECT.NID, 15, 1, 1, 70, 18, 2, 0, 'Z');
        aNSITerm.Color(2);
        Put('%', 8, 1, 1, 70, 8);
        Put('@', 4, 1, 1, 70, 12);
    }

    private void BonusLevel2(/* VAR */ Runtime.IRef<Integer> ax, /* VAR */ Runtime.IRef<Integer> ay) {
        // VAR
        int x = 0;
        int y = 0;
        int c = 0;
        int d = 0;
        int k = 0;
        int mx = 0;

        /* Montagnes : et x */
        Cadre(false);
        aNSITerm.Color(7);
        /* Mur de droite */
        y = 1;
        x = 54;
        c = 0;
        while (y < 18) {
            if (c <= 0) {
                c = grotteSupport.Rnd() % 4 + 1;
                d = grotteSupport.Rnd() % 3;
                d--;
            }
            if (x > 60)
                d = (int) -Math.abs(d);
            else if (x < 44)
                d = (int) Math.abs(d);
            WrAt(x, y, ':');
            y++;
            x += d;
            c--;
        }
        /* montagnes */
        for (k = 0; k <= 2; k += 2) {
            y = 5 + k * 4;
            x = k + 1;
            mx = y + 3;
            c = 0;
            while ((aNSITerm.Report(x + 2 - k, y) == ' ') && (aNSITerm.Report(x + 3 - k, y) == ' ')) {
                if (c <= 0) {
                    c = grotteSupport.Rnd() % 4 + 2;
                    d = grotteSupport.Rnd() % 3;
                    d--;
                }
                y += d;
                if ((aNSITerm.Report(x, y - 1) != ' ') || (aNSITerm.Report(x, y - 2) != ' ')) {
                    y += 2;
                    d = (int) Math.abs(d);
                }
                if (y > mx) {
                    y = mx;
                    d = (int) -Math.abs(d);
                }
                WrAt(x, y, ':');
                x++;
                c--;
            }
        }
        for (x = 3; x <= 41; x++) {
            c = 18;
            y = 18;
            do {
                c--;
            } while (!((aNSITerm.Report(x, c) != ' ') || (c <= 9)));
            c += 6;
            while (y >= c) {
                WrAt(x, y, 'S');
                y--;
            }
        }
        aNSITerm.Color(3);
        WrAt(1, 18, '!');
        grotteSupport.ndoor += 2;
        WrAt(70, 18, '!');
        ax.set(70);
        ay.set(1);
        aNSITerm.Color(6);
        WrAt(69, 19, 'H');
        y = 18;
        x = 65;
        while (y > 1) {
            grotteSupport.Create.invoke(OBJECT.TPLAT, x, y, 1, 0, ' ');
            d = grotteSupport.Rnd() % 3;
            y -= d + 1;
            do {
                c = grotteSupport.Rnd() % 9;
                c -= 4;
                if (x + c < 61)
                    c = (int) Math.abs(c);
                else if (x + c > 70)
                    c = (int) -Math.abs(c);
            } while (!((c != 0) && !((d == 0) && (Math.abs(c) == 1))));
            x += c;
        }
        aNSITerm.Color(7);
        for (x = 61; x <= 70; x += 3) {
            WrAt(x, 3, ':');
        }
        for (c = 1; c <= 20; c++) {
            WrAt(grotteSupport.Rnd() % 64 + 1, grotteSupport.Rnd() % 16 + 2, ' ');
        }
        Put(' ', 20, 10, 1, 44, 18);
        grotteSupport.Create.invoke(OBJECT.PLAYER, 1, 1, grotteSupport.pvie, 0, '*');
        grotteSupport.Create.invoke(OBJECT.GN1, 70, 9, 3, 0, '<');
        PutDeltaObj(OBJECT.PIC, 12, 5, 1, 68, 17, 0, -1, 1, 2, 'V');
        DrawK0(36, 1, 70, 18, 0);
        PutObj(OBJECT.K4, 20, 1, 1, 70, 12, grotteSupport.pvie, 2, 'x');
        aNSITerm.Color(2);
        Put('.', 80, 1, 1, 44, 18);
        Put('%', 8, 1, 1, 70, 18);
        Put('@', 3, 1, 1, 64, 18);
        aNSITerm.Color(7);
        Put('8', 20, 1, 1, 70, 18);
    }

    private void BonusLevel3(/* VAR */ Runtime.IRef<Integer> ax, /* VAR */ Runtime.IRef<Integer> ay) {
        // VAR
        int x = 0;
        int y = 0;
        int c = 0;
        int m = 0;
        boolean ok = false;

        /* Labyrinthe */
        m = grotteSupport.Rnd() % 4 + 2;
        ax.set(65);
        ay.set(0);
        Cadre(false);
        aNSITerm.Color(7);
        for (y = 2; y <= 16; y += 2) {
            for (x = 2; x <= 60; x += 2) {
                WrAt(x, y, 'S');
                ok = false;
                for (c = 2; c <= 4; c += 2) {
                    if ((y - c <= 0) || ((aNSITerm.Report(x - 1, y - c) == 'S') || (aNSITerm.Report(x - 2, y - c + 1) == ' ')))
                        ok = true;
                }
                if ((grotteSupport.Rnd() % m == 0) && ok)
                    WrAt(x, y - 1, 'S');
                else
                    WrAt(x - 1, y, 'S');
            }
        }
        for (x = 1; x <= 59; x += 2) {
            if (aNSITerm.Report(x, 16) == ' ')
                WrAt(x, 18, ':');
        }
        for (y = 1; y <= 16; y++) {
            WrAt(62, y, 'S');
        }
        WrAt(62, 3, ' ');
        grotteSupport.Create.invoke(OBJECT.PLAYER, 1, 1, grotteSupport.pvie, 0, '*');
        aNSITerm.Color(2);
        for (x = 65; x <= 70; x++) {
            WrAt(x, 1, '@');
        }
        aNSITerm.Color(7);
        WrAt(31, 18, 'S');
        for (y = 1; y <= 17; y += 2) {
            grotteSupport.Create.invoke(OBJECT.GN1, 70, y, 3, 0, '<');
        }
        grotteSupport.Create.invoke(OBJECT.ASC, 64, 5, 1, 0, '=');
        DrawK0(62, 1, 64, 18, 2);
        if (grotteSupport.pvie >= 40)
            c = 160;
        else
            c = grotteSupport.pvie * 4;
        PutObj(OBJECT.GN2, c, 1, 2, 71, 18, 1, 0, '£');
        grotteSupport.Create.invoke(OBJECT.NID, 62, 3, 2, 0, 'Z');
        grotteSupport.Create.invoke(OBJECT.NID, 62, 18, 2, 0, 'Z');
        aNSITerm.Color(2);
        for (x = 3; x <= 57; x += 6) {
            WrAt(x, 1, '%');
        }
        Put('.', 150, 1, 1, 71, 18);
    }

    private void BonusLevel4(/* VAR */ Runtime.IRef<Integer> ax, /* VAR */ Runtime.IRef<Integer> ay) {
        // VAR
        int x = 0;
        int y = 0;
        int y2 = 0;
        int c = 0;

        /* Grotte 2 tages */
        c = 0;
        aNSITerm.Color(7);
        do {
            c = (c * 17 + 1) % 2048;
            y = c / 64;
            x = c % 64;
            if (y < 20) {
                WrAt(x, y, 'S');
                if (x < 8)
                    WrAt(x + 64, y, 'S');
            }
        } while (c != 0);
        ax.set(0);
        ay.set(11);
        for (y2 = 1; y2 <= 10; y2 += 9) {
            y = y2 + 1;
            c = 1;
            for (x = 1; x <= 70; x++) {
                if (c == 0) {
                    c = grotteSupport.Rnd() % 8;
                    y += grotteSupport.Rnd() % 7;
                    y -= 3;
                    if (y < y2)
                        y = y2;
                    else if (y > y2 + 4)
                        y = y2 + 4;
                } else {
                    c--;
                }
                WrAt(x, y, ' ');
                WrAt(x, y + 1, ' ');
                WrAt(x, y + 2, ' ');
                WrAt(x, y + 3, ' ');
                WrAt(x, y + 4, ' ');
            }
        }
        for (y = 1; y <= 18; y++) {
            for (x = 67; x <= 70; x++) {
                WrAt(x, y, ' ');
            }
        }
        aNSITerm.Color(4);
        grotteSupport.Create.invoke(OBJECT.ASC, 68, 10, 1, 0, '=');
        grotteSupport.Create.invoke(OBJECT.ASC, 69, 10, 1, 0, '=');
        DrawK0(1, 1, 70, 18, 2);
        aNSITerm.Color(5);
        PutObj(OBJECT.GN2, grotteSupport.pvie, 1, 1, 36, 18, 1, 0, '£');
        PutObj(OBJECT.GN1, 6, 36, 1, 70, 9, 3, 0, '<');
        PutObj(OBJECT.NID, 6, 1, 10, 36, 18, 2, 0, 'Z');
        PutDeltaObj(OBJECT.PIC, 12, 1, 8, 70, 18, 0, -1, 1, 2, 'V');
        aNSITerm.Color(2);
        grotteSupport.Create.invoke(OBJECT.PLAYER, 1, 1, grotteSupport.pvie, 0, '*');
        Put('%', 9, 1, 1, 70, 18);
        Put('.', 120, 1, 1, 70, 18);
    }

    private void BonusLevel5(/* VAR */ Runtime.IRef<Integer> ax, /* VAR */ Runtime.IRef<Integer> ay) {
        // VAR
        int miny = 0;
        int maxy = 0;
        int maxx = 0;
        int minx = 0;
        int x = 0;
        int y = 0;
        int c = 0;
        int d = 0;
        boolean k = false;
        boolean s = false;
        char ch = (char) 0;

        /* Château */
        Cadre(false);
        maxy = -1;
        s = false;
        do {
            k = true;
            miny = maxy + 2;
            maxy += grotteSupport.Rnd() % 8 + 6;
            if (maxy > 12)
                maxy = 19;
            aNSITerm.Color(7);
            for (x = 2; x <= 70; x++) {
                WrAt(x, maxy, 'S');
            }
            maxy--;
            maxx = -1;
            do {
                aNSITerm.Color(7);
                minx = maxx + 2;
                maxx += grotteSupport.Rnd() % 16 + 16;
                if (maxx > 64)
                    maxx = 71;
                for (y = miny; y <= maxy; y++) {
                    WrAt(maxx, y, 'S');
                }
                if (maxx < 71) {
                    if (k)
                        WrAt(maxx, maxy, ' ');
                    else
                        WrAt(maxx, miny, ' ');
                }
                maxx--;
                y = miny;
                while (true) {
                    if (k && s)
                        c = 5;
                    else
                        c = 2;
                    d = c;
                    y += (grotteSupport.Rnd() % d) + 2;
                    if (y >= maxy)
                        break;
                    for (x = minx; x <= maxx; x++) {
                        WrAt(x, y, 'S');
                    }
                    for (c = 1; c <= 5; c++) {
                        x = minx;
                        d = maxx - minx - 1;
                        x += grotteSupport.Rnd() % d;
                        WrAt(x, y, ' ');
                    }
                }
                c = grotteSupport.Rnd() % 4;
                if (c == 0) {
                    x = 1;
                    ch = '@';
                } else if (c == 1) {
                    x = 3;
                    ch = '%';
                } else {
                    x = 7;
                    ch = '.';
                }
                aNSITerm.Color(2);
                Put(ch, x, minx, miny, maxx, maxy);
                k = !k;
            } while (maxx < 70);
            s = true;
        } while (maxy < 18);
        grotteSupport.Create.invoke(OBJECT.PLAYER, 70, 1, grotteSupport.pvie, 0, '*');
        ax.set(70);
        ay.set(18);
        aNSITerm.Color(3);
        WrAt(1, 1, '!');
        grotteSupport.ndoor++;
        DrawK0(1, 1, 50, 18, 2);
        aNSITerm.Color(2);
        Put('%', 5, 1, 1, 70, 18);
        PutDeltaObj(OBJECT.PIC, 8, 1, 1, 70, 18, 0, -1, 1, 2, 'V');
        PutDeltaObj(OBJECT.GN1, 3, 1, 1, 70, 18, 1, 0, 3, 0, '<');
        PutDeltaObj(OBJECT.GN1, 3, 1, 1, 70, 18, -1, 0, 3, 0, '>');
        PutObj(OBJECT.NID, 6, 1, 5, 70, 18, 2, 0, 'Z');
        PutObj(OBJECT.GN2, 12, 1, 1, 50, 18, 1, 0, '£');
    }

    private void BonusLevel6(/* VAR */ Runtime.IRef<Integer> ax, /* VAR */ Runtime.IRef<Integer> ay) {
        // VAR
        int x = 0;
        int y = 0;
        int px = 0;
        int py = 0;
        int c = 0;
        int d = 0;
        int t = 0;
        boolean k = false;

        /* Lignes */
        aNSITerm.Color(7);
        Cadre(false);
        d = 0;
        py = 3;
        for (x = 1; x <= 70; x++) {
            if (d == 0) {
                do {
                    c = grotteSupport.Rnd() % 6;
                } while (Math.abs(c - py) > 4);
                py = c;
                d = grotteSupport.Rnd() % 8 + 3;
            } else {
                d--;
            }
            c = 19 - py;
            while (c < 19) {
                WrAt(x, c, 'S');
                c++;
            }
            if (x == 1) {
                grotteSupport.Create.invoke(OBJECT.PLAYER, 1, 18 - py, grotteSupport.pvie, 0, '*');
                aNSITerm.Color(7);
            }
        }
        y = 2;
        while (y < 12) {
            for (x = 16; x <= 68; x++) {
                t = y;
                k = (grotteSupport.Rnd() % 16) > t;
                if (k)
                    WrAt(x, y, 'S');
            }
            y += grotteSupport.Rnd() % 3 + 2;
        }
        ax.set(70);
        ay.set(5);
        grotteSupport.ndoor = 0;
        WrAt(70, 6, ':');
        y = 18;
        x = 8;
        while (y > 3) {
            px = x;
            py = y;
            if (aNSITerm.Report(x, y + 1) != 'S')
                WrAt(x, y, 'S');
            d = grotteSupport.Rnd() % 3;
            y -= d + 1;
            do {
                c = grotteSupport.Rnd() % 9;
                c -= 4;
                if (x + c < 1)
                    c = (int) Math.abs(c);
                else if (x + c > 15)
                    c = (int) -Math.abs(c);
            } while (!((c != 0) && !((d == 0) && (Math.abs(c) == 1))));
            x += c;
        }
        px++;
        if (px < 12) {
            grotteSupport.Create.invoke(OBJECT.PLAT, px, py, 1, 0, 'T');
            grotteSupport.ppos[0].x = px;
            grotteSupport.ppos[0].y = py;
            grotteSupport.ppos[1].x = 15;
            grotteSupport.ppos[1].y = py;
            grotteSupport.pcount = 1;
        }
        grotteSupport.Create.invoke(OBJECT.ASC, 70, 10, 1, 0, '=');
        PutDeltaObj(OBJECT.PIC, 8, 1, 1, 70, 18, 0, -1, grotteSupport.pvie, 2, 'V');
        PutObj(OBJECT.GN2, grotteSupport.pvie / 8 + 1, 1, 1, 70, 18, 2, 0, '£');
        PutObj(OBJECT.K2, 16, 16, 1, 70, 18, grotteSupport.pvie / 4 + 1, 0, 'X');
        PutObj(OBJECT.NID, 2, 16, 1, 70, 18, 2, 0, 'Z');
        PutObj(OBJECT.K0, 1, 16, 1, 70, 18, grotteSupport.pvie / 4 + 1, 0, '&');
        aNSITerm.Color(2);
        Put('%', 10, 1, 1, 69, 12);
        Put('@', 3, 1, 1, 69, 18);
        c = (grotteSupport.Rnd() % 4 + 1) * 16;
        aNSITerm.Color(6);
        Put('H', c, 16, 3, 69, 18);
    }

    private void BonusLevel7(/* VAR */ Runtime.IRef<Integer> ax, /* VAR */ Runtime.IRef<Integer> ay) {
        // VAR
        int c = 0;
        int x = 0;
        int y = 0;
        int dx = 0;
        int dy = 0;
        int sx = 0;
        int ex = 0;
        int sy = 0;
        int ey = 0;
        int ox = 0;
        int oy = 0;
        boolean platdisp = false;

        /* Grotte 1 etage */
        /* Remplissage */
        aNSITerm.Color(7);
        for (c = 0; c <= 9; c++) {
            for (y = c; y <= 19 - c; y++) {
                WrAt(c, y, 'S');
                WrAt(71 - c, y, 'S');
            }
            for (x = c + 1; x <= 70 - c; x++) {
                WrAt(x, c, 'S');
                WrAt(x, 19 - c, 'S');
            }
        }
        /* creuser */
        platdisp = true;
        dx = 1;
        dy = 1;
        ex = dx;
        oy = 0;
        sx = ex;
        while (ex < 71) {
            ox = sx;
            sx = ex;
            dx += 6 + grotteSupport.Rnd() % 8;
            if (dx > 64)
                dx = 70;
            oy = dy;
            dy = 1 + grotteSupport.Rnd() % 8;
            if (dy > oy) {
                sy = oy + 12;
                ey = dy + 10;
            } else {
                sy = dy;
                ey = oy - 2;
            }
            do {
                for (y = dy; y <= dy + 10; y++) {
                    WrAt(ex, y, ' ');
                }
                ex++;
            } while (ex <= dx);
            if (sx == 1) {
                grotteSupport.Create.invoke(OBJECT.PLAYER, 1, dy + 9, grotteSupport.pvie, 0, '*');
                aNSITerm.Color(7);
            }
            c = grotteSupport.Rnd() % 4;
            if (c == 0) {
                for (y = dy + 6; y <= dy + 10; y++) {
                    for (x = sx; x < ex; x++) {
                        if (grotteSupport.Rnd() % 8 == 0)
                            grotteSupport.Create.invoke(OBJECT.GN2, x, y, 1, 0, '£');
                        if (grotteSupport.Rnd() % 2 == 0) {
                            aNSITerm.Color(2);
                            WrAt(x, y, '.');
                            aNSITerm.Color(7);
                        } else {
                            WrAt(x, y, '8');
                        }
                    }
                }
            } else if ((c == 1) && (dy < 7)) {
                aNSITerm.Color(2);
                for (x = sx + 1; x <= ex - 2; x++) {
                    WrAt(x, dy + 12, '.');
                }
                aNSITerm.Color(7);
                WrAt(sx + 1, dy + 11, '8');
                WrAt(ex - 2, dy + 11, '8');
            } else if (c == 2) {
                aNSITerm.Color(6);
                for (x = sx; x <= ex - 1; x += 2) {
                    WrAt(x, dy + 11, 'H');
                }
                aNSITerm.Color(7);
            } else if (c == 3) {
                aNSITerm.Color(2);
                for (x = sx; x < ex; x++) {
                    c = grotteSupport.Rnd() % 4;
                    for (y = dy + 10 - c; y <= dy + 10; y++) {
                        WrAt(x, y, '.');
                    }
                }
                aNSITerm.Color(7);
            }
            if ((ey - sy >= 3) && (oy != 0)) {
                c = grotteSupport.Rnd() % 2;
                aNSITerm.Color(2);
                for (y = sy; y <= ey; y++) {
                    for (x = ox + 2; x < sx; x++) {
                        if (c == 0)
                            WrAt(x, y, '.');
                        else
                            WrAt(x, y, ' ');
                    }
                }
            } else if ((ey > sy)) {
                if (dy > oy) {
                    y = ey;
                    x = sy - 1;
                } else {
                    y = sy;
                    x = ey + 1;
                }
                WrAt(sx, x, '8');
                grotteSupport.Create.invoke(OBJECT.GN1, sx, y, 3, 0, '>');
            }
            aNSITerm.Color(7);
            c = grotteSupport.Rnd() % 3;
            if (c == 0) {
                grotteSupport.Create.invoke(OBJECT.ASC, ex - 4, dy + 6, 1, 0, '=');
                aNSITerm.Color(7);
                for (x = sx; x <= ex - 1; x += 4) {
                    WrAt(x, dy + 3, '0');
                }
            } else if ((c == 1) && platdisp) {
                grotteSupport.Create.invoke(OBJECT.PLAT, sx, dy + 7, 1, 0, 'T');
                grotteSupport.ppos[0].x = sx;
                grotteSupport.ppos[0].y = dy + 7;
                grotteSupport.ppos[1].x = ex - 1;
                grotteSupport.ppos[1].y = dy + 1;
                grotteSupport.pcount = 1;
                platdisp = false;
            } else {
                WrAt(sx + 4, dy + 3, ':');
                y = dy + 3;
                x = ex - 5;
                while (true) {
                    y += grotteSupport.Rnd() % 4 + 1;
                    if (y > dy + 10)
                        break;
                    do {
                        c = grotteSupport.Rnd() % 7;
                        c -= 3;
                    } while (!((c != 0) && (x + c < ex) && (x + c >= sx)));
                    x += c;
                    WrAt(x, y, ':');
                }
            }
            aNSITerm.Color(7);
        }
        /* common objects */
        PutDeltaObj(OBJECT.PIC, 16, 1, 1, 70, 18, 0, -1, grotteSupport.pvie, 2, 'V');
        PutObj(OBJECT.K1, 16, 16, 1, 70, 18, grotteSupport.pvie / 4 + 1, 0, '+');
        aNSITerm.Color(2);
        Put('%', 10, 1, 1, 70, 18);
        Put('@', 3, 1, 1, 70, 18);
        ax.set(70);
        ay.set(dy + 10);
    }

    private void BonusLevel8(/* VAR */ Runtime.IRef<Integer> ax, /* VAR */ Runtime.IRef<Integer> ay) {
        // VAR
        int minY = 0;
        int x = 0;
        int w = 0;
        int y = 0;
        int dx = 0;
        int dy = 0;
        int c = 0;
        int d = 0;

        /* Plateaux */
        Cadre(true);
        aNSITerm.Color(7);
        WrAt(1, 17, ':');
        aNSITerm.Color(6);
        WrAt(1, 12, 'H');
        grotteSupport.Create.invoke(OBJECT.TPLAT, 70, 4, 1, 0, ' ');
        grotteSupport.Create.invoke(OBJECT.PLAYER, 1, 16, grotteSupport.pvie, 0, '*');
        for (minY = 2; minY <= 14; minY += 6) {
            x = 2;
            y = minY + 4;
            while (true) {
                if (minY < 6)
                    c = 4;
                else
                    c = 8;
                d = c;
                w = grotteSupport.Rnd() % d + 1;
                c = y;
                do {
                    y = grotteSupport.Rnd() % 5;
                    y += minY;
                    dy = (int) Math.abs(c - y);
                    d = 7 - dy;
                    dx = (grotteSupport.Rnd() % d) + 1;
                } while (dy > 4);
                x += dx;
                if (x + w >= 70)
                    w = 69 - x;
                if (w <= 0)
                    break;
                aNSITerm.Color(7);
                for (c = x; c <= (x + w - 1); c++) {
                    WrAt(c, y, ':');
                }
                x += w;
            }
        }
        grotteSupport.Create.invoke(OBJECT.ASC, 1, 5, 1, 0, '=');
        grotteSupport.Create.invoke(OBJECT.PLAT, 70, 12, 1, 0, 'T');
        grotteSupport.ppos[0].x = 70;
        grotteSupport.ppos[0].y = 17;
        grotteSupport.ppos[1].x = 70;
        grotteSupport.ppos[1].y = 12;
        grotteSupport.pcount = 1;
        aNSITerm.Color(7);
        c = (grotteSupport.Rnd() % 4) * 20 + 1;
        Put('8', c, 2, 1, 69, 18);
        aNSITerm.Color(2);
        Put('.', 40, 1, 1, 70, 9);
        Put('%', 20, 1, 1, 70, 15);
        Put('@', 4, 1, 1, 70, 9);
        DrawK0(1, 1, 70, 10, 2);
        PutObj(OBJECT.GN2, grotteSupport.pvie, 1, 1, 70, 18, 1, 0, '£');
        PutObj(OBJECT.GN1, 10, 50, 1, 70, 18, 3, 0, '<');
        PutObj(OBJECT.PIC, 16, 1, 1, 70, 9, grotteSupport.pvie / 2 + 1, 2, 'V');
        ax.set(70);
        ay.set(1);
    }

    private void BonusLevelA(/* VAR */ Runtime.IRef<Integer> ax, /* VAR */ Runtime.IRef<Integer> ay) {
        // VAR
        int n = 0;

        /* Fouillis */
        Cadre(true);
        ax.set(70);
        ay.set(18);
        grotteSupport.Create.invoke(OBJECT.PLAYER, 1, 1, grotteSupport.pvie, 0, '*');
        DrawK0(40, 1, 70, 18, 2);
        aNSITerm.Color(7);
        Put('S', 150, 1, 1, 70, 18);
        Put(':', 80, 1, 1, 70, 18);
        Put('/', 10, 1, 1, 70, 18);
        Put('\\', 10, 1, 1, 70, 18);
        aNSITerm.Color(6);
        Put('H', 30, 1, 1, 70, 18);
        for (n = 2; n <= 8; n++) {
            WrAt(1, n, ' ');
        }
        aNSITerm.Color(2);
        Put('.', 120, 1, 1, 70, 18);
        Put('%', 8, 1, 1, 70, 18);
        Put('@', 3, 36, 1, 70, 18);
        PutObj(OBJECT.TPLAT, 4, 1, 1, 70, 18, 1, 0, ' ');
        PutObj(OBJECT.GN2, grotteSupport.pvie, 1, 1, 70, 18, 1, 0, '£');
        PutObj(OBJECT.GN1, 6, 36, 1, 70, 18, 3, 0, '<');
        PutObj(OBJECT.PIC, 12, 1, 1, 70, 18, 3, 2, 'V');
        PutObj(OBJECT.NID, 4, 1, 1, 70, 18, 2, 0, 'Z');
    }

    private void BonusLevelB(/* VAR */ Runtime.IRef<Integer> ax, /* VAR */ Runtime.IRef<Integer> ay) {
        // VAR
        int x = 0;
        int y = 0;
        int c = 0;

        /* H et = */
        ax.set(70);
        ay.set(14);
        grotteSupport.Create.invoke(OBJECT.PLAYER, 1, 5, grotteSupport.pvie, 0, '*');
        grotteSupport.Create.invoke(OBJECT.GN1, 1, 15, 3, 0, '>');
        aNSITerm.Color(6);
        for (y = 0; y <= 19; y++) {
            WrAt(0, y, 'H');
            WrAt(71, y, 'H');
        }
        for (x = 1; x <= 70; x++) {
            c = grotteSupport.Rnd() % 4;
            for (y = 0; y <= c; y++) {
                WrAt(x, y, 'H');
            }
            c = grotteSupport.Rnd() % 4;
            for (y = 19 - c; y <= 19; y++) {
                WrAt(x, y, 'H');
            }
        }
        PutObj(OBJECT.GN2, grotteSupport.pvie / 4, 1, 1, 70, 18, 1, 0, '£');
        for (x = 3; x <= 66; x += 7) {
            grotteSupport.Create.invoke(OBJECT.ASC, x, 5 + grotteSupport.Rnd() % 8, 1, 0, '=');
        }
        grotteSupport.Create.invoke(OBJECT.TPLAT, 70, 10, 1, 0, ' ');
        aNSITerm.Color(2);
        WrAt(70, 5, '$');
        WrAt(1, 6, '.');
        WrAt(1, 7, '.');
    }

    private void BonusLevelC(/* VAR */ Runtime.IRef<Integer> ax, /* VAR */ Runtime.IRef<Integer> ay) {
        /* 8 */
        Cadre(true);
        aNSITerm.Color(2);
        Put('%', 8, 5, 1, 66, 18);
        Put('@', 4, 36, 1, 66, 18);
        PutObj(OBJECT.GN2, grotteSupport.pvie * 2, 5, 1, 66, 18, 1, 0, '£');
        PutObj(OBJECT.NID, 5, 5, 1, 66, 18, 2, 0, 'Z');
        PutObj(OBJECT.GN1, 2, 1, 1, 4, 18, 3, 0, '>');
        PutObj(OBJECT.GN1, 2, 67, 1, 70, 18, 1, 0, '<');
        DrawK0(5, 1, 66, 18, 2);
        aNSITerm.Color(7);
        Put('8', 600, 5, 1, 66, 18);
        ax.set(70);
        ay.set(18);
        grotteSupport.Create.invoke(OBJECT.PLAYER, 1, 1, grotteSupport.pvie, 0, '*');
    }

    public void BonusLevel(/* VAR+WRT */ Runtime.IRef<Integer> ax, /* VAR+WRT */ Runtime.IRef<Integer> ay) {
        // VAR
        int mod = 0;

        clock0 = clock.AllocTime(300);
        mod = (int) (grotteSupport.score % 8);
        if ((grotteSupport.pvie > 10) && (grotteSupport.score % 9 == 0)) {
            BonusLevelB(ax, ay);
        } else if ((grotteSupport.pvie > 25) && (grotteSupport.score % 5 == 0)) {
            BonusLevelC(ax, ay);
        } else if ((grotteSupport.pvie > 25) && (grotteSupport.score % 4 == 0)) {
            BonusLevelA(ax, ay);
        } else if (mod == 1) {
            BonusLevel1(ax, ay);
        } else if (mod == 2) {
            if (grotteSupport.score >= 5000)
                BonusLevel2(ax, ay);
            else
                BonusLevel1(ax, ay);
        } else if (mod == 3) {
            BonusLevel3(ax, ay);
        } else if (mod == 4) {
            BonusLevel4(ax, ay);
        } else if (mod == 5) {
            BonusLevel5(ax, ay);
        } else if (mod == 6) {
            BonusLevel6(ax, ay);
        } else if (mod == 7) {
            BonusLevel7(ax, ay);
        } else {
            BonusLevel8(ax, ay);
        }
        clock.FreeTime(clock0);
    }


    // Support

    private static GrotteBonus instance;

    public static GrotteBonus instance() {
        if (instance == null)
            new GrotteBonus(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
    }

    public void close() {
    }

}
