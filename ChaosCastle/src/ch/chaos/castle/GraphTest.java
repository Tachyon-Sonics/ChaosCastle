package ch.chaos.castle;

import ch.chaos.library.*;
import ch.chaos.library.Graphics.Modes;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;
import java.util.EnumSet;


public class GraphTest {

    // Imports
    private final Checks checks = Checks.instance();
    private final Clock clock = Clock.instance();
    private final Graphics graphics = Graphics.instance();
    private final Input input = Input.instance();
    private final Memory memory = Memory.instance();
    private final Terminal terminal = Terminal.instance();


    // CONST

    private static final int COL = 16;
    private static final int W = 320;
    private static final int H = 240;


    // TYPE

    private static class _Nested0 { // RECORD

        private short a;
        private short r;
        private short g;
        private short b;


        public short getA() {
            return this.a;
        }

        public void setA(short a) {
            this.a = a;
        }

        public short getR() {
            return this.r;
        }

        public void setR(short r) {
            this.r = r;
        }

        public short getG() {
            return this.g;
        }

        public void setG(short g) {
            this.g = g;
        }

        public short getB() {
            return this.b;
        }

        public void setB(short b) {
            this.b = b;
        }


        public void copyFrom(_Nested0 other) {
            this.a = other.a;
            this.r = other.r;
            this.g = other.g;
            this.b = other.b;
        }

        public _Nested0 newCopy() {
            _Nested0 copy = new _Nested0();
            copy.copyFrom(this);
            return copy;
        }

    }


    // VAR

    private long col;
    private int c;
    private int x;
    private int y;
    private int k;
    private int l;
    private short mx;
    private short my;
    private Graphics.AreaPtr a1;
    private Graphics.AreaPtr a2;
    private Graphics.AreaPtr a3;
    private Graphics.AreaPtr msk;
    private Graphics.AreaPtr bob;
    private Graphics.AreaPtr bobmsk;
    private short[][] pix /* POINTER */;
    private _Nested0[][] tc /* POINTER */;
    private boolean b;
    private boolean ok;
    private Clock.TimePtr t;
    private Object[] tags = new Object[11];
    private Input.Event event = new Input.Event();
    private Graphics.Image image = new Graphics.Image();
    private Runtime.RangeSet joy = new Runtime.RangeSet(Memory.SET16_r);
    private boolean b0;


    public long getCol() {
        return this.col;
    }

    public void setCol(long col) {
        this.col = col;
    }

    public int getC() {
        return this.c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getK() {
        return this.k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getL() {
        return this.l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public short getMx() {
        return this.mx;
    }

    public void setMx(short mx) {
        this.mx = mx;
    }

    public short getMy() {
        return this.my;
    }

    public void setMy(short my) {
        this.my = my;
    }

    public Graphics.AreaPtr getA1() {
        return this.a1;
    }

    public void setA1(Graphics.AreaPtr a1) {
        this.a1 = a1;
    }

    public Graphics.AreaPtr getA2() {
        return this.a2;
    }

    public void setA2(Graphics.AreaPtr a2) {
        this.a2 = a2;
    }

    public Graphics.AreaPtr getA3() {
        return this.a3;
    }

    public void setA3(Graphics.AreaPtr a3) {
        this.a3 = a3;
    }

    public Graphics.AreaPtr getMsk() {
        return this.msk;
    }

    public void setMsk(Graphics.AreaPtr msk) {
        this.msk = msk;
    }

    public Graphics.AreaPtr getBob() {
        return this.bob;
    }

    public void setBob(Graphics.AreaPtr bob) {
        this.bob = bob;
    }

    public Graphics.AreaPtr getBobmsk() {
        return this.bobmsk;
    }

    public void setBobmsk(Graphics.AreaPtr bobmsk) {
        this.bobmsk = bobmsk;
    }

    public short[][] getPix() {
        return this.pix;
    }

    public void setPix(short[][] pix) {
        this.pix = pix;
    }

    public _Nested0[][] getTc() {
        return this.tc;
    }

    public void setTc(_Nested0[][] tc) {
        this.tc = tc;
    }

    public boolean isB() {
        return this.b;
    }

    public void setB(boolean b) {
        this.b = b;
    }

    public boolean isOk() {
        return this.ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public Clock.TimePtr getT() {
        return this.t;
    }

    public void setT(Clock.TimePtr t) {
        this.t = t;
    }

    public Object[] getTags() {
        return this.tags;
    }

    public void setTags(Object[] tags) {
        this.tags = tags;
    }

    public Input.Event getEvent() {
        return this.event;
    }

    public void setEvent(Input.Event event) {
        this.event = event;
    }

    public Graphics.Image getImage() {
        return this.image;
    }

    public void setImage(Graphics.Image image) {
        this.image = image;
    }

    public Runtime.RangeSet getJoy() {
        return this.joy;
    }

    public void setJoy(Runtime.RangeSet joy) {
        this.joy = joy;
    }

    public boolean isB0() {
        return this.b0;
    }

    public void setB0(boolean b0) {
        this.b0 = b0;
    }


    // Life Cycle

    private void begin() {
        Memory.instance().begin();
        Checks.instance().begin();
        Graphics.instance().begin();
        Menus.instance().begin();
        Input.instance().begin();
        Clock.instance().begin();
        Dialogs.instance().begin();
        Terminal.instance().begin();

        t = clock.AllocTime(1000);
        if (t == null)
            throw new HaltException();
        a1 = graphics.CreateArea((Memory.TagItem) memory.TAG4(Graphics.aSIZEX, W, Graphics.aSIZEY, H, Graphics.aCOLOR, 0, Graphics.aTYPE, Graphics.atDISPLAY));
        if (a1 == null)
            throw new HaltException();
        graphics.SetArea(a1);
        graphics.AreaToFront();
        terminal.WriteString("Drawings: ");
        clock.StartTime(t);
        graphics.SetPen(0xFFFF00);
        graphics.DrawLine((short) 0, (short) 0, (short) 319, (short) 199);
        b = false;
        for (c = 90; c >= 2; c -= 4) {
            if (b)
                graphics.SetPen(0xFF0000);
            else
                graphics.SetPen(0x00FF00);
            b = !b;
            graphics.FillEllipse((short) (160 - c), (short) (100 - c), (short) (160 + c), (short) (100 + c));
        }
        for (x = 20; x <= 300; x += 20) {
            graphics.FillEllipse((short) (x - 8), (short) 5, (short) (x + 8), (short) 35);
        }
        graphics.SetPen(0x00FFFF);
        graphics.FillRect((short) 100, (short) 100, (short) 200, (short) 200);
        graphics.SetPen(0x0000FF);
        graphics.FillEllipse((short) 100, (short) 100, (short) 200, (short) 200);
        graphics.SetPen(0xDD9900);
        graphics.OpenPoly((short) 10, (short) 10);
        graphics.AddLine((short) 80, (short) 10);
        graphics.AddLine((short) 40, (short) 20);
        graphics.AddLine((short) 80, (short) 30);
        graphics.AddLine((short) 10, (short) 30);
        graphics.FillPoly();
        graphics.SetCopyMode(EnumSet.of(Modes.snd, Modes.sd, Modes.nsd));
        for (c = 0; c <= 4; c++) {
            graphics.SetPat((short) c);
            graphics.FillRect((short) 160, (short) (c * 16), (short) 260, (short) (c * 16 + 16));
        }
        graphics.AreaToFront();
        graphics.SetCopyMode(EnumSet.of(Modes.nsd, Modes.sd, Modes.snd));
        graphics.SetTextSize((short) 48);
        c = graphics.TextWidth(Runtime.castToRef("Text", String.class));
        graphics.SetTextPos((short) ((320 - c) / 2), (short) 50);
        graphics.DrawText(Runtime.castToRef("Text", String.class));
        graphics.SetCopyMode(EnumSet.of(Modes.nsd, Modes.snd));
        for (x = 0; x <= 190; x += 10) {
            graphics.DrawLine((short) 0, (short) x, (short) 319, (short) x);
        }
        graphics.SetArea(a1);
        graphics.SetCopyMode(EnumSet.of(Modes.snd, Modes.sd));
        input.SetBusyStat((short) 1);
        graphics.SetPen(0);
        graphics.FillRect((short) 0, (short) 0, (short) 32, (short) 32);
        graphics.SetPen(0x00FF00);
        graphics.FillEllipse((short) 0, (short) 0, (short) 24, (short) 24);
        graphics.SetPen(0x0044FF);
        graphics.FillEllipse((short) 6, (short) 6, (short) 18, (short) 18);
        terminal.WriteInt((int) clock.GetTime(t), (short) -1);
        terminal.WriteLn();
        clock.StartTime(t);
        b0 = clock.WaitTime(t, 2000);
        terminal.WriteString("Blit scale: ");
        clock.StartTime(t);
        graphics.SetArea(a1);
        for (y = 50; y <= 149; y += 10) {
            for (x = 1; x <= 100; x += 10) {
                graphics.ScaleRect(a1, (short) 0, (short) 0, (short) 24, (short) 24, (short) x, (short) y, (short) (x + 100), (short) (y + 50));
            }
        }
        graphics.ScaleRect(a1, (short) 0, (short) 0, (short) 240, (short) 200, (short) 240, (short) 0, (short) 320, (short) 200);
        graphics.ScaleRect(a1, (short) 0, (short) 0, (short) 240, (short) 150, (short) 0, (short) 150, (short) 240, (short) 200);
        graphics.ScaleRect(a1, (short) 0, (short) 0, (short) 240, (short) 150, (short) 240, (short) 150, (short) 320, (short) 200);
        for (x = 5; x <= 200; x += 5) {
            graphics.ScaleRect(a1, (short) 0, (short) 0, (short) 24, (short) 24, (short) (160 - x), (short) 30, (short) (160 + x), (short) (30 + x));
        }
        terminal.WriteInt((int) clock.GetTime(t), (short) -1);
        terminal.WriteLn();
        terminal.WriteString("Write pixel: ");
        clock.StartTime(t);
        col = 0;
        for (y = 0; y <= 127; y++) {
            for (x = 0; x <= 255; x++) {
                col = (long) x * 65536 + (long) (255 - x) * 256 + (long) y * 2;
                graphics.SetPen(col);
                graphics.DrawPixel((short) x, (short) y);
            }
        }
        terminal.WriteInt((int) clock.GetTime(t), (short) -1);
        terminal.WriteLn();
        b0 = clock.WaitTime(t, 1000);
        tc = (_Nested0[][]) memory.AllocMem(Runtime.sizeOf(614400, _Nested0[][].class, 240, 320));
        checks.CheckMem(tc);
        for (y = 0; y <= 239; y++) {
            for (x = 0; x <= 319; x++) {
                { // WITH
                    _Nested0 __Nested0 = tc[y][x];
                    __Nested0.r = (short) ((x * y) % 256);
                    __Nested0.g = (short) ((x * 256 / (y + 1)) % 256);
                    __Nested0.b = (short) ((x * x + y * y) % 256);
                }
            }
        }
        terminal.WriteString("True color -> Planar: ");
        clock.StartTime(t);
        image.data = tc;
        image.bitPerPix = 32;
        image.bytePerRow = 4 * 320;
        image.width = 320;
        image.height = 240;
        image.zw = 1;
        image.zh = 1;
        graphics.DrawImage(image, (short) 0, (short) 0, (short) 0, (short) 0, (short) 320, (short) 240);
        terminal.WriteInt((int) clock.GetTime(t), (short) -1);
        terminal.WriteLn();
        memory.FreeMem(new Runtime.FieldRef<>(this::getTc, this::setTc).asAdrRef());
        input.AddEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eMOUSE));
        do {
            input.WaitEvent();
            input.GetEvent(event);
        } while (event.type != Input.eMOUSE);
        pix = (short[][]) memory.AllocMem(Runtime.sizeOf(131072, short[][].class, 256, 256));
        checks.CheckMem(pix);
        for (y = 0; y <= 255; y++) {
            for (x = 0; x <= 255; x++) {
                pix[y][x] = (short) x;
            }
        }
        graphics.FillRect((short) 0, (short) 0, (short) 320, (short) 256);
        terminal.WriteString("Chunky (8bit) -> Planar: ");
        clock.StartTime(t);
        image.data = pix;
        image.bitPerPix = 8;
        image.bytePerRow = 256;
        image.width = 256;
        image.height = 256;
        image.zw = 1;
        image.zh = 1;
        graphics.DrawImage(image, (short) 0, (short) 0, (short) 0, (short) 0, (short) 256, (short) 256);
        terminal.WriteInt((int) clock.GetTime(t), (short) -1);
        terminal.WriteLn();
        clock.StartTime(t);
        b0 = clock.WaitTime(t, 1000);
        graphics.FillRect((short) 0, (short) 0, (short) 256, (short) 256);
        terminal.WriteString("PixMap (4bit) -> Planar: ");
        clock.StartTime(t);
        image.data = pix;
        image.bitPerPix = 4;
        image.bytePerRow = 256;
        image.width = 256;
        image.height = 256;
        graphics.DrawImage(image, (short) 0, (short) 0, (short) 0, (short) 0, (short) 256, (short) 256);
        terminal.WriteInt((int) clock.GetTime(t), (short) -1);
        terminal.WriteLn();
        clock.StartTime(t);
        b0 = clock.WaitTime(t, 1000);
        graphics.FillRect((short) 0, (short) 0, (short) 256, (short) 256);
        terminal.WriteString("Pixmap (1bit) -> Planar: ");
        clock.StartTime(t);
        image.data = pix;
        image.bitPerPix = 1;
        image.bytePerRow = 256;
        image.width = 256;
        image.height = 256;
        graphics.DrawImage(image, (short) 0, (short) 0, (short) 0, (short) 0, (short) 256, (short) 256);
        terminal.WriteInt((int) clock.GetTime(t), (short) -1);
        terminal.WriteLn();
        clock.StartTime(t);
        b0 = clock.WaitTime(t, 1000);
        graphics.DeleteArea(new Runtime.FieldRef<>(this::getA1, this::setA1));
        memory.FreeMem(new Runtime.FieldRef<>(this::getPix, this::setPix).asAdrRef());
        a1 = graphics.CreateArea((Memory.TagItem) memory.TAG4(Graphics.aSIZEX, W, Graphics.aSIZEY, H, Graphics.aCOLOR, COL, Graphics.aTYPE, Graphics.atBUFFER));
        if (a1 == null)
            throw new HaltException();
        graphics.SetArea(a1);
        graphics.SetPalette((short) 0, (short) 0, (short) 0, (short) 0);
        graphics.SetPalette((short) 1, (short) 255, (short) 0, (short) 0);
        graphics.SetPalette((short) 2, (short) 0, (short) 255, (short) 0);
        graphics.SetPalette((short) 3, (short) 0, (short) 0, (short) 255);
        graphics.SetPalette((short) 4, (short) 255, (short) 255, (short) 255);
        graphics.SetPalette((short) 5, (short) 255, (short) 255, (short) 0);
        graphics.SetPalette((short) 15, (short) 0, (short) 255, (short) 255);
        a2 = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, 32, Graphics.aSIZEY, 32, Graphics.aCOLOR, COL));
        msk = graphics.CreateArea((Memory.TagItem) memory.TAG4(Graphics.aSIZEX, 32, Graphics.aSIZEY, 32, Graphics.aCOLOR, 2, Graphics.aTYPE, Graphics.atMASK));
        if ((a1 == null) || (a2 == null) || (msk == null))
            throw new HaltException();
        graphics.SetArea(a1);
        graphics.AreaToFront();
        graphics.SetArea(a2);
        graphics.SetPen(1);
        graphics.FillEllipse((short) 0, (short) 0, (short) 32, (short) 32);
        graphics.SetArea(msk);
        graphics.SetPen(1);
        graphics.SetPat((short) 1);
        graphics.FillEllipse((short) 0, (short) 0, (short) 32, (short) 32);
        bob = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, 64, Graphics.aSIZEY, 24, Graphics.aCOLOR, COL));
        bobmsk = graphics.CreateArea((Memory.TagItem) memory.TAG4(Graphics.aSIZEX, 64, Graphics.aSIZEY, 24, Graphics.aCOLOR, 2, Graphics.aTYPE, Graphics.atMASK));
        if ((bob == null) || (bobmsk == null))
            throw new HaltException();
        graphics.SetArea(bob);
        graphics.SetPen(5);
        graphics.FillEllipse((short) 0, (short) 0, (short) 24, (short) 24);
        graphics.SetArea(bobmsk);
        graphics.SetPen(1);
        graphics.FillEllipse((short) 0, (short) 0, (short) 24, (short) 24);
        graphics.SetArea(a1);
        input.SetBusyStat((short) 2);
        graphics.SetCopyMode(EnumSet.of(Modes.snd, Modes.sd));
        a3 = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, W + 32, Graphics.aSIZEY, H + 32, Graphics.aCOLOR, COL));
        if (a3 == null)
            throw new HaltException();
        graphics.SetArea(a3);
        graphics.FillRect((short) 0, (short) 0, (short) (W + 32), (short) (H + 32));
        for (y = 0; y <= H + 31; y += 32) {
            for (x = 0; x <= W + 31; x += 32) {
                graphics.CopyRect(a2, (short) 0, (short) 0, (short) x, (short) y, (short) 32, (short) 32);
            }
        }
        graphics.SetArea(a1);
        terminal.WriteString("Blit test 2: ");
        clock.StartTime(t);
        for (k = 0; k <= 239; k++) {
            graphics.CopyRect(a3, (short) (k % 32), (short) (k % 32), (short) 0, (short) 0, (short) 32, (short) 32);
            graphics.CopyRect(a3, (short) (32 + k % 32), (short) (k % 32), (short) 32, (short) 0, (short) (W - 32), (short) 32);
            graphics.CopyRect(a3, (short) (k % 32), (short) (32 + k % 32), (short) 0, (short) 32, (short) 32, (short) (H - 32));
            graphics.CopyRect(a3, (short) (32 + k % 32), (short) (32 + k % 32), (short) 32, (short) 32, (short) (W - 32), (short) (H - 32));
            graphics.SetPalette((short) 1, (short) ((k % 16) * 16), (short) 0, (short) (255 - (k % 16) * 16));
            graphics.SetPalette((short) 5, (short) (255 - (k % 16) * 16), (short) 255, (short) 0);
            x = 0;
            for (y = 0; y <= H - 25; y += 25) {
                graphics.CopyMask(bob, bobmsk, (short) 0, (short) 0, (short) k, (short) y, (short) 24, (short) 24);
            }
            graphics.SwitchArea();
        }
        terminal.WriteInt((int) clock.GetTime(t), (short) -1);
        terminal.WriteLn();
        input.SetBusyStat((short) 0);
        clock.StartTime(t);
        while (true) {
            k = (int) (clock.GetTime(t) / 20);
            if (k >= 240)
                break;
            graphics.CopyRect(a3, (short) (k % 32), (short) (k % 32), (short) 0, (short) 0, (short) 32, (short) 32);
            graphics.CopyRect(a3, (short) (32 + k % 32), (short) (k % 32), (short) 32, (short) 0, (short) (W - 32), (short) 32);
            graphics.CopyRect(a3, (short) (k % 32), (short) (32 + k % 32), (short) 0, (short) 32, (short) 32, (short) (H - 32));
            graphics.CopyRect(a3, (short) (32 + k % 32), (short) (32 + k % 32), (short) 32, (short) 32, (short) (W - 32), (short) (H - 32));
            graphics.SetPalette((short) 1, (short) ((k % 16) * 16), (short) 0, (short) (255 - (k % 16) * 16));
            graphics.SetPalette((short) 5, (short) (255 - (k % 16) * 16), (short) 255, (short) 0);
            x = 0;
            for (y = 0; y <= H - 25; y += 25) {
                for (x = 0; x <= 100; x += 25) {
                    graphics.CopyMask(bob, bobmsk, (short) 0, (short) 0, (short) (k + x), (short) y, (short) 24, (short) 24);
                }
            }
            graphics.SwitchArea();
        }
        graphics.SetBuffer(true, false);
        input.AddEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eKEYBOARD, Input.eMOUSE, Input.eMENU, Input.eSYS, Input.eTIMER));
        clock.StartTime(t);
        clock.TimeEvent(t, 400);
        do {
            input.WaitEvent();
            input.GetEvent(event);
            switch (event.type) {
                case Input.eNUL -> {
                    terminal.WriteString("NUL");
                }
                case Input.eKEYBOARD -> {
                    terminal.WriteString("KEYBOARD");
                }
                case Input.eMOUSE -> {
                    terminal.WriteString("MOUSE");
                }
                case Input.eMENU -> {
                    terminal.WriteString("MENU");
                }
                case Input.eSYS -> {
                    terminal.WriteString("SYS");
                }
                case Input.eTIMER -> {
                    terminal.WriteString("TIMER");
                }
                default -> {
                }
            }
            terminal.WriteInt((int) event.menu, (short) 4);
            terminal.WriteLn();
            joy.copyFrom(input.GetStick());
            for (c = 0; c <= 15; c++) {
                if (joy.contains(c))
                    terminal.Write('*');
                else
                    terminal.Write('-');
            }
            input.GetMouse(new Runtime.FieldRef<>(this::getMx, this::setMx), new Runtime.FieldRef<>(this::getMy, this::setMy));
            terminal.WriteInt(mx, (short) 4);
            terminal.WriteInt(my, (short) 4);
            terminal.WriteLn();
            terminal.WriteLn();
        } while (!((event.type == Input.eKEYBOARD) && (event.ch == ((char) 03))));
        input.RemEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eKEYBOARD, Input.eMOUSE, Input.eSYS, Input.eTIMER));
    }

    private void close() {
        Terminal.instance().close();
        Dialogs.instance().close();
        Clock.instance().close();
        Input.instance().close();
        Menus.instance().close();
        Graphics.instance().close();
        Checks.instance().close();
        Memory.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        GraphTest instance = new GraphTest();
        try {
            instance.begin();
        } catch (HaltException ex) {
            // Normal termination
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            instance.close();
        }
    }

}
