package ch.chaos.library;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import ch.chaos.library.Memory.TagItem;
import ch.chaos.library.graphics.IGraphics;
import ch.chaos.library.graphics.indexed.GraphicsIndexedColorImpl;
import ch.chaos.library.graphics.rgb.GraphicsRgbColorImpl;
import ch.chaos.library.settings.Settings;
import ch.pitchtech.modula.runtime.Runtime.IRef;

public class Graphics {

    private final static boolean USE_RGB_MODE = false;
    public final static boolean SEPARATE_GAME_LOOP = false;
    public final static boolean SCALE_XBRZ = true; // false: bicubic

    // CONST

    public static final EnumSet<Modes> cmCopy = EnumSet.of(Modes.snd, Modes.sd);
    public static final EnumSet<Modes> cmOr = EnumSet.of(Modes.nsd, Modes.snd, Modes.sd);
    public static final EnumSet<Modes> cmTrans = cmOr;
    public static final EnumSet<Modes> cmXor = EnumSet.of(Modes.nsd, Modes.snd);
    public static final int pBlack = 0;
    public static final int pLtGrey = 1;
    public static final int pGrey = 2;
    public static final int pDkGrey = 3;
    public static final int pWhite = 4;
    public static final int aSIZEX = Memory.tagUser + 0;
    public static final int aSIZEY = Memory.tagUser + 1;
    public static final int aWIDTH = Memory.tagUser + 2;
    public static final int aHEIGHT = Memory.tagUser + 3;
    public static final int aBARHEIGHT = Memory.tagUser + 4;
    public static final int aCOLOR = Memory.tagUser + 8;
    public static final int aTYPE = Memory.tagUser + 16;
    public static final int atVIRTUAL = 0;
    public static final int atDISPLAY = 1;
    public static final int atBUFFER = 2;
    public static final int atMEMORY = 3;
    public static final int atDIALOG = 4;
    public static final int atMASK = 5;
    public static final int atPRINTER = 6;

    // TYPE


    public static enum Modes {
        nsnd,
        nsd,
        snd,
        sd;
    }

    public static enum TextModes {
        bold,
        italic,
        outline,
        shadow;
    }

    public static enum GraphicsErr {
        gOk,
        gNoMemory,
        gNotSupported,
        gTooComplex,
        gCanceled;
    }

    public static interface AreaPtr { // Opaque type
    }

    public static class Image { // RECORD

        public Object data;
        public long[] palette /* POINTER */;
        public int bitPerPix;
        public int bytePerRow;
        public int width;
        public int height;
        public int zw;
        public int zh;


        public Object getData() {
            return this.data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public long[] getPalette() {
            return this.palette;
        }

        public void setPalette(long[] palette) {
            this.palette = palette;
        }

        public int getBitPerPix() {
            return this.bitPerPix;
        }

        public void setBitPerPix(int bitPerPix) {
            this.bitPerPix = bitPerPix;
        }

        public int getBytePerRow() {
            return this.bytePerRow;
        }

        public void setBytePerRow(int bytePerRow) {
            this.bytePerRow = bytePerRow;
        }

        public int getWidth() {
            return this.width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return this.height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getZw() {
            return this.zw;
        }

        public void setZw(int zw) {
            this.zw = zw;
        }

        public int getZh() {
            return this.zh;
        }

        public void setZh(int zh) {
            this.zh = zh;
        }

        public void copyFrom(Image other) {
            this.data = other.data;
            this.palette = other.palette;
            this.bitPerPix = other.bitPerPix;
            this.bytePerRow = other.bytePerRow;
            this.width = other.width;
            this.height = other.height;
            this.zw = other.zw;
            this.zh = other.zh;
        }

        public Image newCopy() {
            Image copy = new Image();
            copy.copyFrom(this);
            return copy;
        }

    }

    // VAR


    public AreaPtr noArea;


    public AreaPtr getNoArea() {
        return this.noArea;
    }

    public void setNoArea(AreaPtr noArea) {
        this.noArea = noArea;
    }

    // Impl


    static {
        /*
         * On Windows, 3840x2160 (default) may have a x2 scale, while another resolution like 1920x1080 may have a 1.5 scale.
         * Unfortunately, it seems extremely hard to get the "1.5" factor after switching to 1920x1080 for instance.
         * The scaling factor only appears quite lately, after a few paints.
         * 
         * Hence the simplest solution here is to disable any scale factor when a custom resolution is used:
         * (Note: the launcher should already do it)
         * TODO check on Linux and MacOS
         */
        if (Settings.appMode().isFullScreen()) {
            System.setProperty("sun.java2d.uiScale", "1.0");
        }
    }

    private static Graphics instance;
    private final IGraphics target;


    public Graphics() {
        instance = this;
        if (USE_RGB_MODE) {
            this.target = new GraphicsRgbColorImpl();
        } else {
            this.target = new GraphicsIndexedColorImpl();
        }
    }

    public static Graphics instance() {
        if (instance == null)
            new Graphics(); // will set 'instance'
        return instance;
    }

    public static int scale(int value) {
        return value * Settings.appMode().getInnerScale();
    }

    public static int unscale(int value) {
        int scale = Settings.appMode().getInnerScale();
        return (value + scale / 2) / scale;
    }

    public static void resetScale(Graphics2D g2) {
        AffineTransform transform = g2.getTransform();
        transform.setToScale(1.0, 1.0);
        g2.setTransform(transform);
    }

    public void GetGraphicsSysAttr(TagItem what) {
        target.GetGraphicsSysAttr(what);
    }

    public GraphicsErr GetGraphicsErr() {
        return target.GetGraphicsErr();
    }

    public AreaPtr CreateArea(TagItem tags) {
        AtomicReference<AreaPtr> result = new AtomicReference<>();
        try {
            SwingUtilities.invokeAndWait(() -> {
                result.set(target.CreateArea(tags));
            });
        } catch (InvocationTargetException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return result.get();
    }

    public void DeleteArea(IRef<AreaPtr> a) {
        target.DeleteArea(a);
    }

    public void AreaToFront() {
        try {
            SwingUtilities.invokeAndWait(target::AreaToFront);
        } catch (InvocationTargetException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void SwitchArea() {
        target.SwitchArea();
    }

    public void UpdateArea() {
        target.UpdateArea();
    }

    public void SetBuffer(boolean first, boolean off) {
        target.SetBuffer(first, off);
    }

    public void GetBuffer(IRef<Boolean> first, IRef<Boolean> off) {
        target.GetBuffer(first, off);
    }

    public void SetArea(AreaPtr a) {
        target.SetArea(a);
    }

    public void SetPalette(int color, int red, int green, int blue) {
        target.SetPalette(color, red, green, blue);
    }

    public void SetCopyMode(EnumSet<Modes> dm) {
        target.SetCopyMode(dm);
    }

    public void SetPlanes(long planes, boolean clear) {
        target.SetPlanes(planes, clear);
    }

    public void SetPen(long color) {
        target.SetPen(color);
    }

    public void SetBPen(long color) {
        target.SetBPen(color);
    }

    public void SetPat(int v) {
        target.SetPat(v);
    }

    public void SetPattern(byte[] pattern) {
        target.SetPattern(pattern);
    }

    public void DrawPixel(int x, int y) {
        target.DrawPixel(x, y);
    }

    public void DrawLine(int x1, int y1, int x2, int y2) {
        target.DrawLine(x1, y1, x2, y2);
    }

    public void OpenPoly(int x, int y) {
        target.OpenPoly(x, y);
    }

    public void AddLine(int x, int y) {
        target.AddLine(x, y);
    }

    public void FillPoly() {
        target.FillPoly();
    }

    public void FillRect(int x1, int y1, int x2, int y2) {
        target.FillRect(x1, y1, x2, y2);
    }

    public void FillEllipse(int x1, int y1, int x2, int y2) {
        target.FillEllipse(x1, y1, x2, y2);
    }

    public void FillFlood(int x, int y, long borderCol) {
        target.FillFlood(x, y, borderCol);
    }

    public void SetTextMode(EnumSet<TextModes> tm) {
        target.SetTextMode(tm);
    }

    public void SetTextSize(int s) {
        target.SetTextSize(s);
    }

    public void SetTextPos(int x, int y) {
        target.SetTextPos(x, y);
    }

    public int TextWidth(IRef<String> t) {
        return target.TextWidth(t);
    }

    public void DrawText(IRef<String> t) {
        target.DrawText(t);
    }

    public void FillShadow(AreaPtr ma, int sx, int sy, int dx, int dy, int width, int height) {
        target.FillShadow(ma, sx, sy, dx, dy, width, height);
    }

    public void DrawShadow(AreaPtr ma, int sx, int sy, int dx, int dy, int width, int height) {
        target.DrawShadow(ma, sx, sy, dx, dy, width, height);
    }

    public void DrawImage(Image image, int sx, int sy, int dx, int dy, int width, int height) {
        target.DrawImage(image, sx, sy, dx, dy, width, height);
    }

    public void CopyRect(AreaPtr sa, int sx, int sy, int dx, int dy, int width, int height) {
        target.CopyRect(sa, sx, sy, dx, dy, width, height);
    }

    public void CopyShadow(AreaPtr sa, AreaPtr ma, int sx, int sy, int dx, int dy, int width, int height) {
        target.CopyShadow(sa, ma, sx, sy, dx, dy, width, height);
    }

    public void CopyMask(AreaPtr sa, AreaPtr ma, int sx, int sy, int dx, int dy, int width, int height) {
        target.CopyMask(sa, ma, sx, sy, dx, dy, width, height);
    }

    public void ScrollRect(int x, int y, int width, int height, int dx, int dy) {
        target.ScrollRect(x, y, width, height, dx, dy);
    }

    public void ScaleRect(AreaPtr sa, int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2) {
        target.ScaleRect(sa, sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2);
    }

    public void WaitTOF() {
        target.WaitTOF();
    }

    public void begin() {
//        GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        for (GraphicsDevice gDev : gEnv.getScreenDevices()) {
//            System.out.println("Screen: " + gDev);
//            for (DisplayMode displayMode : gDev.getDisplayModes()) {
//                System.out.println("  Display mode: " + displayMode);
//            }
//        }
    }

    public void close() {

    }

}
