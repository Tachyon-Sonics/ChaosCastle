package ch.chaos.castle;

import ch.chaos.castle.GrotteSupport.OBJECT;
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

    private Clock.TimePtr clock0;


    public Clock.TimePtr getClock0() {
        return this.clock0;
    }

    public void setClock0(Clock.TimePtr clock0) {
        this.clock0 = clock0;
    }


    // PROCEDURE

    private void WrAt(byte x, byte y, char ch) {
        // VAR
        boolean w = false;

        if (clock0 != clock.noTime)
            w = clock.WaitTime(clock0, 1);
        aNSITerm.WriteAt(x, y, ch);
    }

    private void Cadre(boolean H) {
        // VAR
        byte c = 0;

        aNSITerm.Goto((byte) 0, (byte) 0);
        aNSITerm.Color((short) 7);
        for (c = 0; c <= 71; c++) {
            aNSITerm.Write('S');
        }
        aNSITerm.Goto((byte) 0, (byte) 19);
        for (c = 0; c <= 71; c++) {
            if (H) {
                aNSITerm.Color((short) 6);
                aNSITerm.Write('H');
            } else {
                aNSITerm.Color((short) 7);
                aNSITerm.Write('S');
            }
        }
        aNSITerm.Color((short) 7);
        for (c = 0; c <= 19; c++) {
            WrAt((byte) 0, c, 'S');
            WrAt((byte) 71, c, 'S');
        }
    }

    private void Find(/* VAR */ Runtime.IRef<Short> x, /* VAR */ Runtime.IRef<Short> y, short sx, short sy, short dx, short dy) {
        // VAR
        int w = 0;
        int h = 0;

        w = dx - sx + 1;
        h = dy - sy + 1;
        do {
            x.set((short) (grotteSupport.Rnd() % w));
            x.inc(sx);
            y.set((short) (grotteSupport.Rnd() % h));
            y.inc(sy);
        } while (aNSITerm.Report((byte) (short) x.get(), (byte) (short) y.get()) != ' ');
    }

    private void Put(char ch, short n, short sx, short sy, short dx, short dy) {
        // VAR
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short c = 0;

        for (c = 1; c <= n; c++) {
            Find(x, y, sx, sy, dx, dy);
            WrAt((byte) (short) x.get(), (byte) (short) y.get(), ch);
        }
    }

    private void PutObj(OBJECT t, short n, short sx, short sy, short dx, short dy, short nvie, short nseq, char ch) {
        // VAR
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short c = 0;

        for (c = 1; c <= n; c++) {
            Find(x, y, sx, sy, dx, dy);
            grotteSupport.Create.invoke(t, (byte) (short) x.get(), (byte) (short) y.get(), (byte) nvie, (byte) nseq, ch);
        }
    }

    private void PutDeltaObj(OBJECT t, short n, short sx, short sy, short dx, short dy, short h, short v, short nvie, short nseq, char ch) {
        // VAR
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short c = 0;

        for (c = 1; c <= n; c++) {
            Find(x, y, sx, sy, dx, dy);
            while (aNSITerm.Report((byte) (x.get() + h), (byte) (y.get() + v)) == ' ') {
                x.inc(h);
                y.inc(v);
            }
            grotteSupport.Create.invoke(t, (byte) (short) x.get(), (byte) (short) y.get(), (byte) nvie, (byte) nseq, ch);
        }
    }

    private void DrawK0(short sx, short sy, short dx, short dy, short nseq) {
        // VAR
        short c1 = 0;
        short c2 = 0;

        c1 = 0;
        c2 = 1;
        while ((grotteSupport.pvie > c2 + 2) && (c2 < 64)) {
            c1++;
            c2 = (short) (c2 * 2);
        }
        PutObj(OBJECT.K0, c1, sx, sy, dx, dy, (short) (grotteSupport.pvie / 4 + 1), nseq, '&');
    }

    private void BonusLevel1(/* VAR */ Runtime.IRef<Byte> ax, /* VAR */ Runtime.IRef<Byte> ay) {
        // VAR
        short dx = 0;
        short dy = 0;
        short x = 0;
        short y = 0;
        short c = 0;

        Cadre(false);
        aNSITerm.Color((short) 6);
        for (x = 40; x <= 71; x++) {
            WrAt((byte) x, (byte) 19, 'H');
        }
        aNSITerm.Color((short) 7);
        for (y = 2; y <= 18; y++) {
            WrAt((byte) 39, (byte) y, 'S');
        }
        ax.set((byte) 39);
        ay.set((byte) 18);
        dx = 1;
        for (y = 2; y <= 14; y += 4) {
            dy = (short) (grotteSupport.Rnd() % 3);
            dx = (short) (2 - dy);
            for (x = 1; x <= 30; x++) {
                WrAt((byte) x, (byte) (y + dy), 'S');
            }
            for (c = 1; c <= 4; c++) {
                x = (short) (grotteSupport.Rnd() % 32 + 1);
                for (dy = (short) (y + 1); dy <= y + dx; dy++) {
                    WrAt((byte) x, (byte) dy, 'S');
                }
            }
        }
        for (c = 1; c <= 200; c++) {
            WrAt((byte) (grotteSupport.Rnd() % 35 + 1), (byte) (grotteSupport.Rnd() % 16 + 2), ' ');
        }
        for (y = 2; y <= 16; y += 2) {
            x = 39;
            while (true) {
                x += grotteSupport.Rnd() % 8 + 1;
                dy = (short) (grotteSupport.Rnd() % 2);
                if (x > 69)
                    break;
                WrAt((byte) x, (byte) (y + dy), 'S');
                if (grotteSupport.Rnd() % 2 == 1)
                    WrAt((byte) (x + 1), (byte) (y + dy), 'S');
            }
        }
        grotteSupport.Create.invoke(OBJECT.PLAYER, (byte) 1, (byte) 1, grotteSupport.pvie, (byte) 0, '*');
        grotteSupport.Create.invoke(OBJECT.ASC, (byte) 37, (byte) 16, (byte) 1, (byte) 0, '=');
        aNSITerm.Color((short) 3);
        WrAt((byte) 35, (byte) 1, '!');
        grotteSupport.ndoor++;
        PutObj(OBJECT.NID, (short) 15, (short) 1, (short) 1, (short) 70, (short) 18, (short) 2, (short) 0, 'Z');
        aNSITerm.Color((short) 2);
        Put('%', (short) 8, (short) 1, (short) 1, (short) 70, (short) 8);
        Put('@', (short) 4, (short) 1, (short) 1, (short) 70, (short) 12);
    }

    private void BonusLevel2(/* VAR */ Runtime.IRef<Byte> ax, /* VAR */ Runtime.IRef<Byte> ay) {
        // VAR
        short x = 0;
        short y = 0;
        short c = 0;
        short d = 0;
        short k = 0;
        short mx = 0;

        Cadre(false);
        aNSITerm.Color((short) 7);
        y = 1;
        x = 54;
        c = 0;
        while (y < 18) {
            if (c <= 0) {
                c = (short) (grotteSupport.Rnd() % 4 + 1);
                d = (short) (grotteSupport.Rnd() % 3);
                d--;
            }
            if (x > 60)
                d = (short) -Math.abs(d);
            else if (x < 44)
                d = (short) Math.abs(d);
            WrAt((byte) x, (byte) y, ':');
            y++;
            x += d;
            c--;
        }
        for (k = 0; k <= 2; k += 2) {
            y = (short) (5 + k * 4);
            x = (short) (k + 1);
            mx = (short) (y + 3);
            c = 0;
            while ((aNSITerm.Report((byte) (x + 2 - k), (byte) y) == ' ') && (aNSITerm.Report((byte) (x + 3 - k), (byte) y) == ' ')) {
                if (c <= 0) {
                    c = (short) (grotteSupport.Rnd() % 4 + 2);
                    d = (short) (grotteSupport.Rnd() % 3);
                    d--;
                }
                y += d;
                if ((aNSITerm.Report((byte) x, (byte) (y - 1)) != ' ') || (aNSITerm.Report((byte) x, (byte) (y - 2)) != ' ')) {
                    y += 2;
                    d = (short) Math.abs(d);
                }
                if (y > mx) {
                    y = mx;
                    d = (short) -Math.abs(d);
                }
                WrAt((byte) x, (byte) y, ':');
                x++;
                c--;
            }
        }
        for (x = 3; x <= 41; x++) {
            c = 18;
            y = 18;
            do {
                c--;
            } while (!((aNSITerm.Report((byte) x, (byte) c) != ' ') || (c <= 9)));
            c += 6;
            while (y >= c) {
                WrAt((byte) x, (byte) y, 'S');
                y--;
            }
        }
        aNSITerm.Color((short) 3);
        WrAt((byte) 1, (byte) 18, '!');
        grotteSupport.ndoor += 2;
        WrAt((byte) 70, (byte) 18, '!');
        ax.set((byte) 70);
        ay.set((byte) 1);
        aNSITerm.Color((short) 6);
        WrAt((byte) 69, (byte) 19, 'H');
        y = 18;
        x = 65;
        while (y > 1) {
            grotteSupport.Create.invoke(OBJECT.TPLAT, (byte) x, (byte) y, (byte) 1, (byte) 0, ' ');
            d = (short) (grotteSupport.Rnd() % 3);
            y -= d + 1;
            do {
                c = (short) (grotteSupport.Rnd() % 9);
                c -= 4;
                if (x + c < 61)
                    c = (short) Math.abs(c);
                else if (x + c > 70)
                    c = (short) -Math.abs(c);
            } while (!((c != 0) && !((d == 0) && (Math.abs(c) == 1))));
            x += c;
        }
        aNSITerm.Color((short) 7);
        for (x = 61; x <= 70; x += 3) {
            WrAt((byte) x, (byte) 3, ':');
        }
        for (c = 1; c <= 20; c++) {
            WrAt((byte) (grotteSupport.Rnd() % 64 + 1), (byte) (grotteSupport.Rnd() % 16 + 2), ' ');
        }
        Put(' ', (short) 20, (short) 10, (short) 1, (short) 44, (short) 18);
        grotteSupport.Create.invoke(OBJECT.PLAYER, (byte) 1, (byte) 1, grotteSupport.pvie, (byte) 0, '*');
        grotteSupport.Create.invoke(OBJECT.GN1, (byte) 70, (byte) 9, (byte) 3, (byte) 0, '<');
        PutDeltaObj(OBJECT.PIC, (short) 12, (short) 5, (short) 1, (short) 68, (short) 17, (short) 0, (short) -1, (short) 1, (short) 2, 'V');
        DrawK0((short) 36, (short) 1, (short) 70, (short) 18, (short) 0);
        PutObj(OBJECT.K4, (short) 20, (short) 1, (short) 1, (short) 70, (short) 12, grotteSupport.pvie, (short) 2, 'x');
        aNSITerm.Color((short) 2);
        Put('.', (short) 80, (short) 1, (short) 1, (short) 44, (short) 18);
        Put('%', (short) 8, (short) 1, (short) 1, (short) 70, (short) 18);
        Put('@', (short) 3, (short) 1, (short) 1, (short) 64, (short) 18);
        aNSITerm.Color((short) 7);
        Put('8', (short) 20, (short) 1, (short) 1, (short) 70, (short) 18);
    }

    private void BonusLevel3(/* VAR */ Runtime.IRef<Byte> ax, /* VAR */ Runtime.IRef<Byte> ay) {
        // VAR
        short x = 0;
        short y = 0;
        short c = 0;
        int m = 0;
        boolean ok = false;

        m = grotteSupport.Rnd() % 4 + 2;
        ax.set((byte) 65);
        ay.set((byte) 0);
        Cadre(false);
        aNSITerm.Color((short) 7);
        for (y = 2; y <= 16; y += 2) {
            for (x = 2; x <= 60; x += 2) {
                WrAt((byte) x, (byte) y, 'S');
                ok = false;
                for (c = 2; c <= 4; c += 2) {
                    if ((y - c <= 0) || ((aNSITerm.Report((byte) (x - 1), (byte) (y - c)) == 'S') || (aNSITerm.Report((byte) (x - 2), (byte) (y - c + 1)) == ' ')))
                        ok = true;
                }
                if ((grotteSupport.Rnd() % m == 0) && ok)
                    WrAt((byte) x, (byte) (y - 1), 'S');
                else
                    WrAt((byte) (x - 1), (byte) y, 'S');
            }
        }
        for (x = 1; x <= 59; x += 2) {
            if (aNSITerm.Report((byte) x, (byte) 16) == ' ')
                WrAt((byte) x, (byte) 18, ':');
        }
        for (y = 1; y <= 16; y++) {
            WrAt((byte) 62, (byte) y, 'S');
        }
        WrAt((byte) 62, (byte) 3, ' ');
        grotteSupport.Create.invoke(OBJECT.PLAYER, (byte) 1, (byte) 1, grotteSupport.pvie, (byte) 0, '*');
        aNSITerm.Color((short) 2);
        for (x = 65; x <= 70; x++) {
            WrAt((byte) x, (byte) 1, '@');
        }
        aNSITerm.Color((short) 7);
        WrAt((byte) 31, (byte) 18, 'S');
        for (y = 1; y <= 17; y += 2) {
            grotteSupport.Create.invoke(OBJECT.GN1, (byte) 70, (byte) y, (byte) 3, (byte) 0, '<');
        }
        grotteSupport.Create.invoke(OBJECT.ASC, (byte) 64, (byte) 5, (byte) 1, (byte) 0, '=');
        DrawK0((short) 62, (short) 1, (short) 64, (short) 18, (short) 2);
        if (grotteSupport.pvie >= 40)
            c = 160;
        else
            c = (short) (grotteSupport.pvie * 4);
        PutObj(OBJECT.GN2, c, (short) 1, (short) 2, (short) 71, (short) 18, (short) 1, (short) 0, '£');
        grotteSupport.Create.invoke(OBJECT.NID, (byte) 62, (byte) 3, (byte) 2, (byte) 0, 'Z');
        grotteSupport.Create.invoke(OBJECT.NID, (byte) 62, (byte) 18, (byte) 2, (byte) 0, 'Z');
        aNSITerm.Color((short) 2);
        for (x = 3; x <= 57; x += 6) {
            WrAt((byte) x, (byte) 1, '%');
        }
        Put('.', (short) 150, (short) 1, (short) 1, (short) 71, (short) 18);
    }

    private void BonusLevel4(/* VAR */ Runtime.IRef<Byte> ax, /* VAR */ Runtime.IRef<Byte> ay) {
        // VAR
        short x = 0;
        short y = 0;
        short y2 = 0;
        int c = 0;

        c = 0;
        aNSITerm.Color((short) 7);
        do {
            c = (c * 17 + 1) % 2048;
            y = (short) (c / 64);
            x = (short) (c % 64);
            if (y < 20) {
                WrAt((byte) x, (byte) y, 'S');
                if (x < 8)
                    WrAt((byte) (x + 64), (byte) y, 'S');
            }
        } while (c != 0);
        ax.set((byte) 0);
        ay.set((byte) 11);
        for (y2 = 1; y2 <= 10; y2 += 9) {
            y = (short) (y2 + 1);
            c = 1;
            for (x = 1; x <= 70; x++) {
                if (c == 0) {
                    c = grotteSupport.Rnd() % 8;
                    y += grotteSupport.Rnd() % 7;
                    y -= 3;
                    if (y < y2)
                        y = y2;
                    else if (y > y2 + 4)
                        y = (short) (y2 + 4);
                } else {
                    c--;
                }
                WrAt((byte) x, (byte) y, ' ');
                WrAt((byte) x, (byte) (y + 1), ' ');
                WrAt((byte) x, (byte) (y + 2), ' ');
                WrAt((byte) x, (byte) (y + 3), ' ');
                WrAt((byte) x, (byte) (y + 4), ' ');
            }
        }
        for (y = 1; y <= 18; y++) {
            for (x = 67; x <= 70; x++) {
                WrAt((byte) x, (byte) y, ' ');
            }
        }
        aNSITerm.Color((short) 4);
        grotteSupport.Create.invoke(OBJECT.ASC, (byte) 68, (byte) 10, (byte) 1, (byte) 0, '=');
        grotteSupport.Create.invoke(OBJECT.ASC, (byte) 69, (byte) 10, (byte) 1, (byte) 0, '=');
        DrawK0((short) 1, (short) 1, (short) 70, (short) 18, (short) 2);
        aNSITerm.Color((short) 5);
        PutObj(OBJECT.GN2, grotteSupport.pvie, (short) 1, (short) 1, (short) 36, (short) 18, (short) 1, (short) 0, '£');
        PutObj(OBJECT.GN1, (short) 6, (short) 36, (short) 1, (short) 70, (short) 9, (short) 3, (short) 0, '<');
        PutObj(OBJECT.NID, (short) 6, (short) 1, (short) 10, (short) 36, (short) 18, (short) 2, (short) 0, 'Z');
        PutDeltaObj(OBJECT.PIC, (short) 12, (short) 1, (short) 8, (short) 70, (short) 18, (short) 0, (short) -1, (short) 1, (short) 2, 'V');
        aNSITerm.Color((short) 2);
        grotteSupport.Create.invoke(OBJECT.PLAYER, (byte) 1, (byte) 1, grotteSupport.pvie, (byte) 0, '*');
        Put('%', (short) 9, (short) 1, (short) 1, (short) 70, (short) 18);
        Put('.', (short) 120, (short) 1, (short) 1, (short) 70, (short) 18);
    }

    private void BonusLevel5(/* VAR */ Runtime.IRef<Byte> ax, /* VAR */ Runtime.IRef<Byte> ay) {
        // VAR
        short miny = 0;
        short maxy = 0;
        short maxx = 0;
        short minx = 0;
        short x = 0;
        short y = 0;
        short c = 0;
        int d = 0;
        boolean k = false;
        boolean s = false;
        char ch = (char) 0;

        Cadre(false);
        maxy = -1;
        s = false;
        do {
            k = true;
            miny = (short) (maxy + 2);
            maxy += grotteSupport.Rnd() % 8 + 6;
            if (maxy > 12)
                maxy = 19;
            aNSITerm.Color((short) 7);
            for (x = 2; x <= 70; x++) {
                WrAt((byte) x, (byte) maxy, 'S');
            }
            maxy--;
            maxx = -1;
            do {
                aNSITerm.Color((short) 7);
                minx = (short) (maxx + 2);
                maxx += grotteSupport.Rnd() % 16 + 16;
                if (maxx > 64)
                    maxx = 71;
                for (y = miny; y <= maxy; y++) {
                    WrAt((byte) maxx, (byte) y, 'S');
                }
                if (maxx < 71) {
                    if (k)
                        WrAt((byte) maxx, (byte) maxy, ' ');
                    else
                        WrAt((byte) maxx, (byte) miny, ' ');
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
                        WrAt((byte) x, (byte) y, 'S');
                    }
                    for (c = 1; c <= 5; c++) {
                        x = minx;
                        d = maxx - minx - 1;
                        x += grotteSupport.Rnd() % d;
                        WrAt((byte) x, (byte) y, ' ');
                    }
                }
                c = (short) (grotteSupport.Rnd() % 4);
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
                aNSITerm.Color((short) 2);
                Put(ch, x, minx, miny, maxx, maxy);
                k = !k;
            } while (maxx < 70);
            s = true;
        } while (maxy < 18);
        grotteSupport.Create.invoke(OBJECT.PLAYER, (byte) 70, (byte) 1, grotteSupport.pvie, (byte) 0, '*');
        ax.set((byte) 70);
        ay.set((byte) 18);
        aNSITerm.Color((short) 3);
        WrAt((byte) 1, (byte) 1, '!');
        grotteSupport.ndoor++;
        DrawK0((short) 1, (short) 1, (short) 50, (short) 18, (short) 2);
        aNSITerm.Color((short) 2);
        Put('%', (short) 5, (short) 1, (short) 1, (short) 70, (short) 18);
        PutDeltaObj(OBJECT.PIC, (short) 8, (short) 1, (short) 1, (short) 70, (short) 18, (short) 0, (short) -1, (short) 1, (short) 2, 'V');
        PutDeltaObj(OBJECT.GN1, (short) 3, (short) 1, (short) 1, (short) 70, (short) 18, (short) 1, (short) 0, (short) 3, (short) 0, '<');
        PutDeltaObj(OBJECT.GN1, (short) 3, (short) 1, (short) 1, (short) 70, (short) 18, (short) -1, (short) 0, (short) 3, (short) 0, '>');
        PutObj(OBJECT.NID, (short) 6, (short) 1, (short) 5, (short) 70, (short) 18, (short) 2, (short) 0, 'Z');
        PutObj(OBJECT.GN2, (short) 12, (short) 1, (short) 1, (short) 50, (short) 18, (short) 1, (short) 0, '£');
    }

    private void BonusLevel6(/* VAR */ Runtime.IRef<Byte> ax, /* VAR */ Runtime.IRef<Byte> ay) {
        // VAR
        short x = 0;
        short y = 0;
        short px = 0;
        short py = 0;
        short c = 0;
        short d = 0;
        int t = 0;
        boolean k = false;

        aNSITerm.Color((short) 7);
        Cadre(false);
        d = 0;
        py = 3;
        for (x = 1; x <= 70; x++) {
            if (d == 0) {
                do {
                    c = (short) (grotteSupport.Rnd() % 6);
                } while (Math.abs(c - py) > 4);
                py = c;
                d = (short) (grotteSupport.Rnd() % 8 + 3);
            } else {
                d--;
            }
            c = (short) (19 - py);
            while (c < 19) {
                WrAt((byte) x, (byte) c, 'S');
                c++;
            }
            if (x == 1) {
                grotteSupport.Create.invoke(OBJECT.PLAYER, (byte) 1, (byte) (18 - py), grotteSupport.pvie, (byte) 0, '*');
                aNSITerm.Color((short) 7);
            }
        }
        y = 2;
        while (y < 12) {
            for (x = 16; x <= 68; x++) {
                t = y;
                k = (grotteSupport.Rnd() % 16) > t;
                if (k)
                    WrAt((byte) x, (byte) y, 'S');
            }
            y += grotteSupport.Rnd() % 3 + 2;
        }
        ax.set((byte) 70);
        ay.set((byte) 5);
        grotteSupport.ndoor = 0;
        WrAt((byte) 70, (byte) 6, ':');
        y = 18;
        x = 8;
        while (y > 3) {
            px = x;
            py = y;
            if (aNSITerm.Report((byte) x, (byte) (y + 1)) != 'S')
                WrAt((byte) x, (byte) y, 'S');
            d = (short) (grotteSupport.Rnd() % 3);
            y -= d + 1;
            do {
                c = (short) (grotteSupport.Rnd() % 9);
                c -= 4;
                if (x + c < 1)
                    c = (short) Math.abs(c);
                else if (x + c > 15)
                    c = (short) -Math.abs(c);
            } while (!((c != 0) && !((d == 0) && (Math.abs(c) == 1))));
            x += c;
        }
        px++;
        if (px < 12) {
            grotteSupport.Create.invoke(OBJECT.PLAT, (byte) px, (byte) py, (byte) 1, (byte) 0, 'T');
            grotteSupport.ppos[0].x = (byte) px;
            grotteSupport.ppos[0].y = (byte) py;
            grotteSupport.ppos[1].x = 15;
            grotteSupport.ppos[1].y = (byte) py;
            grotteSupport.pcount = 1;
        }
        grotteSupport.Create.invoke(OBJECT.ASC, (byte) 70, (byte) 10, (byte) 1, (byte) 0, '=');
        PutDeltaObj(OBJECT.PIC, (short) 8, (short) 1, (short) 1, (short) 70, (short) 18, (short) 0, (short) -1, grotteSupport.pvie, (short) 2, 'V');
        PutObj(OBJECT.GN2, (short) (grotteSupport.pvie / 8 + 1), (short) 1, (short) 1, (short) 70, (short) 18, (short) 2, (short) 0, '£');
        PutObj(OBJECT.K2, (short) 16, (short) 16, (short) 1, (short) 70, (short) 18, (short) (grotteSupport.pvie / 4 + 1), (short) 0, 'X');
        PutObj(OBJECT.NID, (short) 2, (short) 16, (short) 1, (short) 70, (short) 18, (short) 2, (short) 0, 'Z');
        PutObj(OBJECT.K0, (short) 1, (short) 16, (short) 1, (short) 70, (short) 18, (short) (grotteSupport.pvie / 4 + 1), (short) 0, '&');
        aNSITerm.Color((short) 2);
        Put('%', (short) 10, (short) 1, (short) 1, (short) 69, (short) 12);
        Put('@', (short) 3, (short) 1, (short) 1, (short) 69, (short) 18);
        c = (short) ((grotteSupport.Rnd() % 4 + 1) * 16);
        aNSITerm.Color((short) 6);
        Put('H', c, (short) 16, (short) 3, (short) 69, (short) 18);
    }

    private void BonusLevel7(/* VAR */ Runtime.IRef<Byte> ax, /* VAR */ Runtime.IRef<Byte> ay) {
        // VAR
        short c = 0;
        short x = 0;
        short y = 0;
        short dx = 0;
        short dy = 0;
        short sx = 0;
        short ex = 0;
        short sy = 0;
        short ey = 0;
        short ox = 0;
        short oy = 0;
        boolean platdisp = false;

        aNSITerm.Color((short) 7);
        for (c = 0; c <= 9; c++) {
            for (y = c; y <= 19 - c; y++) {
                WrAt((byte) c, (byte) y, 'S');
                WrAt((byte) (71 - c), (byte) y, 'S');
            }
            for (x = (short) (c + 1); x <= 70 - c; x++) {
                WrAt((byte) x, (byte) c, 'S');
                WrAt((byte) x, (byte) (19 - c), 'S');
            }
        }
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
            dy = (short) (1 + grotteSupport.Rnd() % 8);
            if (dy > oy) {
                sy = (short) (oy + 12);
                ey = (short) (dy + 10);
            } else {
                sy = dy;
                ey = (short) (oy - 2);
            }
            do {
                for (y = dy; y <= dy + 10; y++) {
                    WrAt((byte) ex, (byte) y, ' ');
                }
                ex++;
            } while (ex <= dx);
            if (sx == 1) {
                grotteSupport.Create.invoke(OBJECT.PLAYER, (byte) 1, (byte) (dy + 9), grotteSupport.pvie, (byte) 0, '*');
                aNSITerm.Color((short) 7);
            }
            c = (short) (grotteSupport.Rnd() % 4);
            if (c == 0) {
                for (y = (short) (dy + 6); y <= dy + 10; y++) {
                    for (x = sx; x < ex; x++) {
                        if (grotteSupport.Rnd() % 8 == 0)
                            grotteSupport.Create.invoke(OBJECT.GN2, (byte) x, (byte) y, (byte) 1, (byte) 0, '£');
                        if (grotteSupport.Rnd() % 2 == 0) {
                            aNSITerm.Color((short) 2);
                            WrAt((byte) x, (byte) y, '.');
                            aNSITerm.Color((short) 7);
                        } else {
                            WrAt((byte) x, (byte) y, '8');
                        }
                    }
                }
            } else if ((c == 1) && (dy < 7)) {
                aNSITerm.Color((short) 2);
                for (x = (short) (sx + 1); x <= ex - 2; x++) {
                    WrAt((byte) x, (byte) (dy + 12), '.');
                }
                aNSITerm.Color((short) 7);
                WrAt((byte) (sx + 1), (byte) (dy + 11), '8');
                WrAt((byte) (ex - 2), (byte) (dy + 11), '8');
            } else if (c == 2) {
                aNSITerm.Color((short) 6);
                for (x = sx; x <= ex - 1; x += 2) {
                    WrAt((byte) x, (byte) (dy + 11), 'H');
                }
                aNSITerm.Color((short) 7);
            } else if (c == 3) {
                aNSITerm.Color((short) 2);
                for (x = sx; x < ex; x++) {
                    c = (short) (grotteSupport.Rnd() % 4);
                    for (y = (short) (dy + 10 - c); y <= dy + 10; y++) {
                        WrAt((byte) x, (byte) y, '.');
                    }
                }
                aNSITerm.Color((short) 7);
            }
            if ((ey - sy >= 3) && (oy != 0)) {
                c = (short) (grotteSupport.Rnd() % 2);
                aNSITerm.Color((short) 2);
                for (y = sy; y <= ey; y++) {
                    for (x = (short) (ox + 2); x < sx; x++) {
                        if (c == 0)
                            WrAt((byte) x, (byte) y, '.');
                        else
                            WrAt((byte) x, (byte) y, ' ');
                    }
                }
            } else if ((ey > sy)) {
                if (dy > oy) {
                    y = ey;
                    x = (short) (sy - 1);
                } else {
                    y = sy;
                    x = (short) (ey + 1);
                }
                WrAt((byte) sx, (byte) x, '8');
                grotteSupport.Create.invoke(OBJECT.GN1, (byte) sx, (byte) y, (byte) 3, (byte) 0, '>');
            }
            aNSITerm.Color((short) 7);
            c = (short) (grotteSupport.Rnd() % 3);
            if (c == 0) {
                grotteSupport.Create.invoke(OBJECT.ASC, (byte) (ex - 4), (byte) (dy + 6), (byte) 1, (byte) 0, '=');
                aNSITerm.Color((short) 7);
                for (x = sx; x <= ex - 1; x += 4) {
                    WrAt((byte) x, (byte) (dy + 3), '0');
                }
            } else if ((c == 1) && platdisp) {
                grotteSupport.Create.invoke(OBJECT.PLAT, (byte) sx, (byte) (dy + 7), (byte) 1, (byte) 0, 'T');
                grotteSupport.ppos[0].x = (byte) sx;
                grotteSupport.ppos[0].y = (byte) (dy + 7);
                grotteSupport.ppos[1].x = (byte) (ex - 1);
                grotteSupport.ppos[1].y = (byte) (dy + 1);
                grotteSupport.pcount = 1;
                platdisp = false;
            } else {
                WrAt((byte) (sx + 4), (byte) (dy + 3), ':');
                y = (short) (dy + 3);
                x = (short) (ex - 5);
                while (true) {
                    y += grotteSupport.Rnd() % 4 + 1;
                    if (y > dy + 10)
                        break;
                    do {
                        c = (short) (grotteSupport.Rnd() % 7);
                        c -= 3;
                    } while (!((c != 0) && (x + c < ex) && (x + c >= sx)));
                    x += c;
                    WrAt((byte) x, (byte) y, ':');
                }
            }
            aNSITerm.Color((short) 7);
        }
        PutDeltaObj(OBJECT.PIC, (short) 16, (short) 1, (short) 1, (short) 70, (short) 18, (short) 0, (short) -1, grotteSupport.pvie, (short) 2, 'V');
        PutObj(OBJECT.K1, (short) 16, (short) 16, (short) 1, (short) 70, (short) 18, (short) (grotteSupport.pvie / 4 + 1), (short) 0, '+');
        aNSITerm.Color((short) 2);
        Put('%', (short) 10, (short) 1, (short) 1, (short) 70, (short) 18);
        Put('@', (short) 3, (short) 1, (short) 1, (short) 70, (short) 18);
        ax.set((byte) 70);
        ay.set((byte) (dy + 10));
    }

    private void BonusLevel8(/* VAR */ Runtime.IRef<Byte> ax, /* VAR */ Runtime.IRef<Byte> ay) {
        // VAR
        short minY = 0;
        short x = 0;
        short w = 0;
        short y = 0;
        short dx = 0;
        short dy = 0;
        short c = 0;
        int d = 0;

        Cadre(true);
        aNSITerm.Color((short) 7);
        WrAt((byte) 1, (byte) 17, ':');
        aNSITerm.Color((short) 6);
        WrAt((byte) 1, (byte) 12, 'H');
        grotteSupport.Create.invoke(OBJECT.TPLAT, (byte) 70, (byte) 4, (byte) 1, (byte) 0, ' ');
        grotteSupport.Create.invoke(OBJECT.PLAYER, (byte) 1, (byte) 16, grotteSupport.pvie, (byte) 0, '*');
        for (minY = 2; minY <= 14; minY += 6) {
            x = 2;
            y = (short) (minY + 4);
            while (true) {
                if (minY < 6)
                    c = 4;
                else
                    c = 8;
                d = c;
                w = (short) (grotteSupport.Rnd() % d + 1);
                c = y;
                do {
                    y = (short) (grotteSupport.Rnd() % 5);
                    y += minY;
                    dy = (short) Math.abs(c - y);
                    d = 7 - dy;
                    dx = (short) ((grotteSupport.Rnd() % d) + 1);
                } while (dy > 4);
                x += dx;
                if (x + w >= 70)
                    w = (short) (69 - x);
                if (w <= 0)
                    break;
                aNSITerm.Color((short) 7);
                for (c = x; c <= (x + w - 1); c++) {
                    WrAt((byte) c, (byte) y, ':');
                }
                x += w;
            }
        }
        grotteSupport.Create.invoke(OBJECT.ASC, (byte) 1, (byte) 5, (byte) 1, (byte) 0, '=');
        grotteSupport.Create.invoke(OBJECT.PLAT, (byte) 70, (byte) 12, (byte) 1, (byte) 0, 'T');
        grotteSupport.ppos[0].x = 70;
        grotteSupport.ppos[0].y = 17;
        grotteSupport.ppos[1].x = 70;
        grotteSupport.ppos[1].y = 12;
        grotteSupport.pcount = 1;
        aNSITerm.Color((short) 7);
        c = (short) ((grotteSupport.Rnd() % 4) * 20 + 1);
        Put('8', c, (short) 2, (short) 1, (short) 69, (short) 18);
        aNSITerm.Color((short) 2);
        Put('.', (short) 40, (short) 1, (short) 1, (short) 70, (short) 9);
        Put('%', (short) 20, (short) 1, (short) 1, (short) 70, (short) 15);
        Put('@', (short) 4, (short) 1, (short) 1, (short) 70, (short) 9);
        DrawK0((short) 1, (short) 1, (short) 70, (short) 10, (short) 2);
        PutObj(OBJECT.GN2, grotteSupport.pvie, (short) 1, (short) 1, (short) 70, (short) 18, (short) 1, (short) 0, '£');
        PutObj(OBJECT.GN1, (short) 10, (short) 50, (short) 1, (short) 70, (short) 18, (short) 3, (short) 0, '<');
        PutObj(OBJECT.PIC, (short) 16, (short) 1, (short) 1, (short) 70, (short) 9, (short) (grotteSupport.pvie / 2 + 1), (short) 2, 'V');
        ax.set((byte) 70);
        ay.set((byte) 1);
    }

    private void BonusLevelA(/* VAR */ Runtime.IRef<Byte> ax, /* VAR */ Runtime.IRef<Byte> ay) {
        // VAR
        byte n = 0;

        Cadre(true);
        ax.set((byte) 70);
        ay.set((byte) 18);
        grotteSupport.Create.invoke(OBJECT.PLAYER, (byte) 1, (byte) 1, grotteSupport.pvie, (byte) 0, '*');
        DrawK0((short) 40, (short) 1, (short) 70, (short) 18, (short) 2);
        aNSITerm.Color((short) 7);
        Put('S', (short) 150, (short) 1, (short) 1, (short) 70, (short) 18);
        Put(':', (short) 80, (short) 1, (short) 1, (short) 70, (short) 18);
        Put('/', (short) 10, (short) 1, (short) 1, (short) 70, (short) 18);
        Put('\\', (short) 10, (short) 1, (short) 1, (short) 70, (short) 18);
        aNSITerm.Color((short) 6);
        Put('H', (short) 30, (short) 1, (short) 1, (short) 70, (short) 18);
        for (n = 2; n <= 8; n++) {
            WrAt((byte) 1, n, ' ');
        }
        aNSITerm.Color((short) 2);
        Put('.', (short) 120, (short) 1, (short) 1, (short) 70, (short) 18);
        Put('%', (short) 8, (short) 1, (short) 1, (short) 70, (short) 18);
        Put('@', (short) 3, (short) 36, (short) 1, (short) 70, (short) 18);
        PutObj(OBJECT.TPLAT, (short) 4, (short) 1, (short) 1, (short) 70, (short) 18, (short) 1, (short) 0, ' ');
        PutObj(OBJECT.GN2, grotteSupport.pvie, (short) 1, (short) 1, (short) 70, (short) 18, (short) 1, (short) 0, '£');
        PutObj(OBJECT.GN1, (short) 6, (short) 36, (short) 1, (short) 70, (short) 18, (short) 3, (short) 0, '<');
        PutObj(OBJECT.PIC, (short) 12, (short) 1, (short) 1, (short) 70, (short) 18, (short) 3, (short) 2, 'V');
        PutObj(OBJECT.NID, (short) 4, (short) 1, (short) 1, (short) 70, (short) 18, (short) 2, (short) 0, 'Z');
    }

    private void BonusLevelB(/* VAR */ Runtime.IRef<Byte> ax, /* VAR */ Runtime.IRef<Byte> ay) {
        // VAR
        short x = 0;
        short y = 0;
        short c = 0;

        ax.set((byte) 70);
        ay.set((byte) 14);
        grotteSupport.Create.invoke(OBJECT.PLAYER, (byte) 1, (byte) 5, grotteSupport.pvie, (byte) 0, '*');
        grotteSupport.Create.invoke(OBJECT.GN1, (byte) 1, (byte) 15, (byte) 3, (byte) 0, '>');
        aNSITerm.Color((short) 6);
        for (y = 0; y <= 19; y++) {
            WrAt((byte) 0, (byte) y, 'H');
            WrAt((byte) 71, (byte) y, 'H');
        }
        for (x = 1; x <= 70; x++) {
            c = (short) (grotteSupport.Rnd() % 4);
            for (y = 0; y <= c; y++) {
                WrAt((byte) x, (byte) y, 'H');
            }
            c = (short) (grotteSupport.Rnd() % 4);
            for (y = (short) (19 - c); y <= 19; y++) {
                WrAt((byte) x, (byte) y, 'H');
            }
        }
        PutObj(OBJECT.GN2, (short) (grotteSupport.pvie / 4), (short) 1, (short) 1, (short) 70, (short) 18, (short) 1, (short) 0, '£');
        for (x = 3; x <= 66; x += 7) {
            grotteSupport.Create.invoke(OBJECT.ASC, (byte) x, (byte) (5 + grotteSupport.Rnd() % 8), (byte) 1, (byte) 0, '=');
        }
        grotteSupport.Create.invoke(OBJECT.TPLAT, (byte) 70, (byte) 10, (byte) 1, (byte) 0, ' ');
        aNSITerm.Color((short) 2);
        WrAt((byte) 70, (byte) 5, '$');
        WrAt((byte) 1, (byte) 6, '.');
        WrAt((byte) 1, (byte) 7, '.');
    }

    private void BonusLevelC(/* VAR */ Runtime.IRef<Byte> ax, /* VAR */ Runtime.IRef<Byte> ay) {
        Cadre(true);
        aNSITerm.Color((short) 2);
        Put('%', (short) 8, (short) 5, (short) 1, (short) 66, (short) 18);
        Put('@', (short) 4, (short) 36, (short) 1, (short) 66, (short) 18);
        PutObj(OBJECT.GN2, (short) (grotteSupport.pvie * 2), (short) 5, (short) 1, (short) 66, (short) 18, (short) 1, (short) 0, '£');
        PutObj(OBJECT.NID, (short) 5, (short) 5, (short) 1, (short) 66, (short) 18, (short) 2, (short) 0, 'Z');
        PutObj(OBJECT.GN1, (short) 2, (short) 1, (short) 1, (short) 4, (short) 18, (short) 3, (short) 0, '>');
        PutObj(OBJECT.GN1, (short) 2, (short) 67, (short) 1, (short) 70, (short) 18, (short) 1, (short) 0, '<');
        DrawK0((short) 5, (short) 1, (short) 66, (short) 18, (short) 2);
        aNSITerm.Color((short) 7);
        Put('8', (short) 600, (short) 5, (short) 1, (short) 66, (short) 18);
        ax.set((byte) 70);
        ay.set((byte) 18);
        grotteSupport.Create.invoke(OBJECT.PLAYER, (byte) 1, (byte) 1, grotteSupport.pvie, (byte) 0, '*');
    }

    public void BonusLevel(/* VAR+WRT */ Runtime.IRef<Byte> ax, /* VAR+WRT */ Runtime.IRef<Byte> ay) {
        // VAR
        short mod = 0;

        clock0 = clock.AllocTime(300);
        mod = (short) (grotteSupport.score % 8);
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
