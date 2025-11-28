package ch.chaos.castle;

import java.lang.Runnable;

import ch.chaos.library.Checks;
import ch.chaos.library.Graphics;
import ch.chaos.library.Graphics.GraphicsErr;
import ch.chaos.library.Memory;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosGraphics {

    // Imports
    private final ChaosBase chaosBase;
    private final Checks checks;
    private final Graphics graphics;
    private final Memory memory;


    private ChaosGraphics() {
        instance = this; // Set early to handle circular dependencies
        chaosBase = ChaosBase.instance();
        checks = Checks.instance();
        graphics = Graphics.instance();
        memory = Memory.instance();
    }


    // CONST

    /* Screen, Play-area, Info-area */
    public static final int SW = 320;
    public static final int PW = 240;
    public static final int IW = 80;
    public static final int SH = 240;
    public static final int PH = SH;
    public static final int IH = SH;
    /* Off screen size */
    public static final int OW = 288;
    public static final int OH = 288;
    public static final int BW = 32;
    public static final int BH = 32;
    public static final int SOW = 8;
    public static final int SOH = 8;
    public static final int NbBackground = 24;
    public static final int NbWall = 40;
    public static final int NbClear = 21;


    // TYPE

    public static class Palette { // RECORD

        public int red;
        public int green;
        public int blue;


        public int getRed() {
            return this.red;
        }

        public void setRed(int red) {
            this.red = red;
        }

        public int getGreen() {
            return this.green;
        }

        public void setGreen(int green) {
            this.green = green;
        }

        public int getBlue() {
            return this.blue;
        }

        public void setBlue(int blue) {
            this.blue = blue;
        }


        public void copyFrom(Palette other) {
            this.red = other.red;
            this.green = other.green;
            this.blue = other.blue;
        }

        public Palette newCopy() {
            Palette copy = new Palette();
            copy.copyFrom(this);
            return copy;
        }

    }

    public static enum Explosions {
        Low,
        Medium,
        High;
    }

    @FunctionalInterface
    public static interface AdjustProc { // PROCEDURE Type
        public int invoke(int arg1);
    }


    // VAR

    public int[][] castle /* POINTER */;
    public int[][] dual /* POINTER */;
    public int castleWidth;
    public int castleHeight;
    public int gameWidth;
    public int gameHeight;
    public int backpx;
    public int backpy;
    public int dualpx;
    public int dualpy;
    public Graphics.AreaPtr mainArea;
    public Graphics.AreaPtr buffArea;
    public Graphics.AreaPtr dualArea;
    /* 256 x 256 */
    public Graphics.AreaPtr shapeArea;
    public Graphics.AreaPtr maskArea;
    /* 256 x 256 */
    public Graphics.AreaPtr imageArea;
    public Graphics.AreaPtr image2Area;
    public int mulS;
    public int dualSpeed;
    public boolean color;
    public boolean dualpf;
    public boolean dfltGraphic;
    public Explosions explosions;
    public Runtime.RangeSet cycling = new Runtime.RangeSet(Memory.SET16_r);
    public Runtime.RangeSet dualCycling = new Runtime.RangeSet(Memory.SET16_r);
    public Palette[] palette = Runtime.initArray(new Palette[16]);
    public Palette[] cycle = Runtime.initArray(new Palette[16]);
    public Palette[] dualPalette = Runtime.initArray(new Palette[16]);
    public Palette[] dualCycle = Runtime.initArray(new Palette[16]);
    public int[] cycleSpeed = new int[16];
    public int[] dualCycleSpeed = new int[16];
    public int[] transparent = new int[16];
    public AdjustProc X;
    public AdjustProc Y;
    public AdjustProc W;
    public AdjustProc H;


    public int[][] getCastle() {
        return this.castle;
    }

    public void setCastle(int[][] castle) {
        this.castle = castle;
    }

    public int[][] getDual() {
        return this.dual;
    }

    public void setDual(int[][] dual) {
        this.dual = dual;
    }

    public int getCastleWidth() {
        return this.castleWidth;
    }

    public void setCastleWidth(int castleWidth) {
        this.castleWidth = castleWidth;
    }

    public int getCastleHeight() {
        return this.castleHeight;
    }

    public void setCastleHeight(int castleHeight) {
        this.castleHeight = castleHeight;
    }

    public int getGameWidth() {
        return this.gameWidth;
    }

    public void setGameWidth(int gameWidth) {
        this.gameWidth = gameWidth;
    }

    public int getGameHeight() {
        return this.gameHeight;
    }

    public void setGameHeight(int gameHeight) {
        this.gameHeight = gameHeight;
    }

    public int getBackpx() {
        return this.backpx;
    }

    public void setBackpx(int backpx) {
        this.backpx = backpx;
    }

    public int getBackpy() {
        return this.backpy;
    }

    public void setBackpy(int backpy) {
        this.backpy = backpy;
    }

    public int getDualpx() {
        return this.dualpx;
    }

    public void setDualpx(int dualpx) {
        this.dualpx = dualpx;
    }

    public int getDualpy() {
        return this.dualpy;
    }

    public void setDualpy(int dualpy) {
        this.dualpy = dualpy;
    }

    public Graphics.AreaPtr getMainArea() {
        return this.mainArea;
    }

    public void setMainArea(Graphics.AreaPtr mainArea) {
        this.mainArea = mainArea;
    }

    public Graphics.AreaPtr getBuffArea() {
        return this.buffArea;
    }

    public void setBuffArea(Graphics.AreaPtr buffArea) {
        this.buffArea = buffArea;
    }

    public Graphics.AreaPtr getDualArea() {
        return this.dualArea;
    }

    public void setDualArea(Graphics.AreaPtr dualArea) {
        this.dualArea = dualArea;
    }

    public Graphics.AreaPtr getShapeArea() {
        return this.shapeArea;
    }

    public void setShapeArea(Graphics.AreaPtr shapeArea) {
        this.shapeArea = shapeArea;
    }

    public Graphics.AreaPtr getMaskArea() {
        return this.maskArea;
    }

    public void setMaskArea(Graphics.AreaPtr maskArea) {
        this.maskArea = maskArea;
    }

    public Graphics.AreaPtr getImageArea() {
        return this.imageArea;
    }

    public void setImageArea(Graphics.AreaPtr imageArea) {
        this.imageArea = imageArea;
    }

    public Graphics.AreaPtr getImage2Area() {
        return this.image2Area;
    }

    public void setImage2Area(Graphics.AreaPtr image2Area) {
        this.image2Area = image2Area;
    }

    public int getMulS() {
        return this.mulS;
    }

    public void setMulS(int mulS) {
        this.mulS = mulS;
    }

    public int getDualSpeed() {
        return this.dualSpeed;
    }

    public void setDualSpeed(int dualSpeed) {
        this.dualSpeed = dualSpeed;
    }

    public boolean isColor() {
        return this.color;
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public boolean isDualpf() {
        return this.dualpf;
    }

    public void setDualpf(boolean dualpf) {
        this.dualpf = dualpf;
    }

    public boolean isDfltGraphic() {
        return this.dfltGraphic;
    }

    public void setDfltGraphic(boolean dfltGraphic) {
        this.dfltGraphic = dfltGraphic;
    }

    public Explosions getExplosions() {
        return this.explosions;
    }

    public void setExplosions(Explosions explosions) {
        this.explosions = explosions;
    }

    public Runtime.RangeSet getCycling() {
        return this.cycling;
    }

    public void setCycling(Runtime.RangeSet cycling) {
        this.cycling = cycling;
    }

    public Runtime.RangeSet getDualCycling() {
        return this.dualCycling;
    }

    public void setDualCycling(Runtime.RangeSet dualCycling) {
        this.dualCycling = dualCycling;
    }

    public Palette[] getPalette() {
        return this.palette;
    }

    public void setPalette(Palette[] palette) {
        this.palette = palette;
    }

    public Palette[] getCycle() {
        return this.cycle;
    }

    public void setCycle(Palette[] cycle) {
        this.cycle = cycle;
    }

    public Palette[] getDualPalette() {
        return this.dualPalette;
    }

    public void setDualPalette(Palette[] dualPalette) {
        this.dualPalette = dualPalette;
    }

    public Palette[] getDualCycle() {
        return this.dualCycle;
    }

    public void setDualCycle(Palette[] dualCycle) {
        this.dualCycle = dualCycle;
    }

    public int[] getCycleSpeed() {
        return this.cycleSpeed;
    }

    public void setCycleSpeed(int[] cycleSpeed) {
        this.cycleSpeed = cycleSpeed;
    }

    public int[] getDualCycleSpeed() {
        return this.dualCycleSpeed;
    }

    public void setDualCycleSpeed(int[] dualCycleSpeed) {
        this.dualCycleSpeed = dualCycleSpeed;
    }

    public int[] getTransparent() {
        return this.transparent;
    }

    public void setTransparent(int[] transparent) {
        this.transparent = transparent;
    }

    public AdjustProc getX() {
        return this.X;
    }

    public void setX(AdjustProc X) {
        this.X = X;
    }

    public AdjustProc getY() {
        return this.Y;
    }

    public void setY(AdjustProc Y) {
        this.Y = Y;
    }

    public AdjustProc getW() {
        return this.W;
    }

    public void setW(AdjustProc W) {
        this.W = W;
    }

    public AdjustProc getH() {
        return this.H;
    }

    public void setH(AdjustProc H) {
        this.H = H;
    }


    // VAR

    private int buffdx;
    private int buffdy;
    private int buffpx;
    private int buffpy;
    private int dbuffdx;
    private int dbuffdy;
    private int dbuffpx;
    private int dbuffpy;
    private int backox;
    private int backoy;
    private int backaddx;
    private int backaddy;
    private int backtox;
    private int backtoy;
    private int stepmod;
    /* Palette */
    private int[] cpPos = new int[16];
    private int[] dcpPos = new int[16];
    private Runtime.RangeSet cpReverse = new Runtime.RangeSet(Memory.SET16_r);
    private Runtime.RangeSet dcpReverse = new Runtime.RangeSet(Memory.SET16_r);
    private int OX;
    private int OY;


    public int getBuffdx() {
        return this.buffdx;
    }

    public void setBuffdx(int buffdx) {
        this.buffdx = buffdx;
    }

    public int getBuffdy() {
        return this.buffdy;
    }

    public void setBuffdy(int buffdy) {
        this.buffdy = buffdy;
    }

    public int getBuffpx() {
        return this.buffpx;
    }

    public void setBuffpx(int buffpx) {
        this.buffpx = buffpx;
    }

    public int getBuffpy() {
        return this.buffpy;
    }

    public void setBuffpy(int buffpy) {
        this.buffpy = buffpy;
    }

    public int getDbuffdx() {
        return this.dbuffdx;
    }

    public void setDbuffdx(int dbuffdx) {
        this.dbuffdx = dbuffdx;
    }

    public int getDbuffdy() {
        return this.dbuffdy;
    }

    public void setDbuffdy(int dbuffdy) {
        this.dbuffdy = dbuffdy;
    }

    public int getDbuffpx() {
        return this.dbuffpx;
    }

    public void setDbuffpx(int dbuffpx) {
        this.dbuffpx = dbuffpx;
    }

    public int getDbuffpy() {
        return this.dbuffpy;
    }

    public void setDbuffpy(int dbuffpy) {
        this.dbuffpy = dbuffpy;
    }

    public int getBackox() {
        return this.backox;
    }

    public void setBackox(int backox) {
        this.backox = backox;
    }

    public int getBackoy() {
        return this.backoy;
    }

    public void setBackoy(int backoy) {
        this.backoy = backoy;
    }

    public int getBackaddx() {
        return this.backaddx;
    }

    public void setBackaddx(int backaddx) {
        this.backaddx = backaddx;
    }

    public int getBackaddy() {
        return this.backaddy;
    }

    public void setBackaddy(int backaddy) {
        this.backaddy = backaddy;
    }

    public int getBacktox() {
        return this.backtox;
    }

    public void setBacktox(int backtox) {
        this.backtox = backtox;
    }

    public int getBacktoy() {
        return this.backtoy;
    }

    public void setBacktoy(int backtoy) {
        this.backtoy = backtoy;
    }

    public int getStepmod() {
        return this.stepmod;
    }

    public void setStepmod(int stepmod) {
        this.stepmod = stepmod;
    }

    public int[] getCpPos() {
        return this.cpPos;
    }

    public void setCpPos(int[] cpPos) {
        this.cpPos = cpPos;
    }

    public int[] getDcpPos() {
        return this.dcpPos;
    }

    public void setDcpPos(int[] dcpPos) {
        this.dcpPos = dcpPos;
    }

    public Runtime.RangeSet getCpReverse() {
        return this.cpReverse;
    }

    public void setCpReverse(Runtime.RangeSet cpReverse) {
        this.cpReverse = cpReverse;
    }

    public Runtime.RangeSet getDcpReverse() {
        return this.dcpReverse;
    }

    public void setDcpReverse(Runtime.RangeSet dcpReverse) {
        this.dcpReverse = dcpReverse;
    }

    public int getOX() {
        return this.OX;
    }

    public void setOX(int OX) {
        this.OX = OX;
    }

    public int getOY() {
        return this.OY;
    }

    public void setOY(int OY) {
        this.OY = OY;
    }


    // PROCEDURE

    public boolean OpenScreen() {
        // VAR
        long nbColor = 0L;
        GraphicsErr err = GraphicsErr.gOk;

        CloseScreen();
        SetOrigin(0, 0);
        if (color) {
            if (dualpf)
                nbColor = 256;
            else
                nbColor = 16;
        } else {
            nbColor = 2;
        }
        /* kill ev. last error */
        err = graphics.GetGraphicsErr();
        mainArea = graphics.CreateArea((Memory.TagItem) memory.TAG4(Graphics.aSIZEX, W.invoke(SW), Graphics.aSIZEY, H.invoke(SH), Graphics.aCOLOR, nbColor, Graphics.aTYPE, Graphics.atBUFFER));
        if (mainArea == graphics.noArea)
            return false;
        buffArea = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, W.invoke(OW), Graphics.aSIZEY, H.invoke(OH), Graphics.aCOLOR, nbColor));
        if (buffArea == graphics.noArea)
            return false;
        if (dualpf) {
            dualArea = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, W.invoke(OW), Graphics.aSIZEY, H.invoke(OH), Graphics.aCOLOR, nbColor));
            if (dualArea == graphics.noArea)
                return false;
        }
        shapeArea = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, W.invoke(256), Graphics.aSIZEY, H.invoke(296), Graphics.aCOLOR, nbColor));
        if (shapeArea == graphics.noArea)
            return false;
        maskArea = graphics.CreateArea((Memory.TagItem) memory.TAG4(Graphics.aSIZEX, W.invoke(256), Graphics.aSIZEY, H.invoke(296), Graphics.aCOLOR, 2, Graphics.aTYPE, Graphics.atMASK));
        if (maskArea == graphics.noArea)
            return false;
        graphics.SetArea(mainArea);
        graphics.SetBuffer(true, false);
        UpdatePalette();
        graphics.AreaToFront();
        return true;
    }

    private void SetBackgroundPos(int px, int py) {
        // CONST
        final int RW = PW / 2;
        final int RH = PH / 2;

        // VAR
        int maxx = 0;
        int maxy = 0;
        int xstep = 0;
        int ystep = 0;

        maxx = gameWidth - PW;
        maxy = gameHeight - PH;
        backox = backpx;
        backoy = backpy;
        xstep = (chaosBase.step + stepmod) / 3;
        ystep = xstep;
        stepmod = (chaosBase.step + 2) % 3;
        if (chaosBase.mainPlayer.dvx > 0)
            backaddx = 32;
        else if (chaosBase.mainPlayer.dvx < 0)
            backaddx = -32;
        if (chaosBase.mainPlayer.dvy > 0)
            backaddy = 32;
        else if (chaosBase.mainPlayer.dvy < 0)
            backaddy = -32;
        backtox = px + backaddx - RW;
        backtoy = py + backaddy - RH;
        if (Math.abs(backtox - backpx) > 100)
            xstep = xstep * 2;
        if (Math.abs(backtoy - backpy) > 100)
            ystep = ystep * 2;
        if (Math.abs(backtox - backpx) < xstep)
            backpx = backtox;
        else if (backpx < backtox)
            backpx += xstep;
        else
            backpx -= xstep;
        if (Math.abs(backtoy - backpy) < ystep)
            backpy = backtoy;
        else if (backpy < backtoy)
            backpy += ystep;
        else
            backpy -= ystep;
        if (backpx < 0)
            backpx = 0;
        else if (backpx > maxx)
            backpx = maxx;
        if (backpy < 0)
            backpy = 0;
        else if (backpy > maxy)
            backpy = maxy;
        if (dualpf) {
            dualpx = backpx / dualSpeed;
            dualpy = backpy / dualSpeed;
        }
    }

    private void DrawBlocks(int x, int y, int w, int h) {
        // VAR
        int bx = 0;
        int by = 0;
        int cx = 0;
        int cy = 0;
        int dx = 0;
        int dy = 0;

        dy = (buffdy + h) % (SOH + 1);
        cy = y + h;
        graphics.SetArea(buffArea);
        if (dualpf)
            graphics.SetPlanes(15, true);
        while (cy > y) {
            cy--;
            if (dy == 0)
                dy = SOH;
            else
                dy--;
            dx = (buffdx + w) % (SOW + 1);
            cx = x + w;
            while (cx > x) {
                cx--;
                if (dx == 0)
                    dx = SOW;
                else
                    dx--;
                if ((cx < castleWidth) && (cy < castleHeight)) {
                    bx = (castle[cy][cx]);
                    by = (bx / 8) * BH;
                    bx = (bx % 8) * BW;
                    graphics.CopyRect(imageArea, W.invoke(bx), H.invoke(by), W.invoke(dx * BW), H.invoke(dy * BH), W.invoke(BW), H.invoke(BH));
                }
            }
        }
    }

    private void DrawDBlocks(int x, int y, int w, int h) {
        // VAR
        int bx = 0;
        int by = 0;
        int cx = 0;
        int cy = 0;
        int dx = 0;
        int dy = 0;

        dy = (dbuffdy + h) % (SOH + 1);
        cy = y + h;
        graphics.SetArea(dualArea);
        graphics.SetPlanes(240, true);
        while (cy > y) {
            cy--;
            if (dy == 0)
                dy = SOH;
            else
                dy--;
            dx = (dbuffdx + w) % (SOW + 1);
            cx = x + w;
            while (cx > x) {
                cx--;
                if (dx == 0)
                    dx = SOW;
                else
                    dx--;
                if ((cx < 64) && (cy < 64)) {
                    bx = (dual[cy][cx]);
                    by = (bx / 8) * BH;
                    bx = (bx % 8) * BW;
                    graphics.CopyRect(image2Area, W.invoke(bx), H.invoke(by), W.invoke(dx * BW), H.invoke(dy * BH), W.invoke(BW), H.invoke(BH));
                }
            }
        }
    }

    private void RenderBlocks() {
        // VAR
        int px = 0;
        int py = 0;
        int mx = 0;
        int my = 0;
        int w1 = 0;
        int w2 = 0;
        int h1 = 0;
        int h2 = 0;

        px = backpx / BW;
        py = backpy / BH;
        mx = backpx % BW;
        my = backpy % BH;
        while (buffpx < px) {
            DrawBlocks(buffpx + SOW + 1, buffpy, 1, SOH + 1);
            buffpx++;
            buffdx = (buffdx + 1) % (SOW + 1);
        }
        while (buffpx > px) {
            if (buffdx <= 0)
                buffdx = SOW;
            else
                buffdx--;
            buffpx--;
            DrawBlocks(buffpx, buffpy, 1, SOH + 1);
        }
        while (buffpy < py) {
            DrawBlocks(buffpx, buffpy + SOH + 1, SOW + 1, 1);
            buffpy++;
            buffdy = (buffdy + 1) % (SOH + 1);
        }
        while (buffpy > py) {
            if (buffdy <= 0)
                buffdy = SOH;
            else
                buffdy--;
            buffpy--;
            DrawBlocks(buffpx, buffpy, SOW + 1, 1);
        }
        /* Render */
        px = buffdx * BW + mx;
        py = buffdy * BH + my;
        w1 = OW - px;
        w2 = PW - w1;
        if (w1 > PW)
            w1 = PW;
        h1 = OH - py;
        h2 = PH - h1;
        if (h1 > PH)
            h1 = PH;
        graphics.SetArea(mainArea);
        graphics.SetCopyMode(Graphics.cmCopy);
        if (dualpf)
            graphics.SetPlanes(15, true);
        if (h1 > 0) {
            if (w1 > 0)
                graphics.CopyRect(buffArea, W.invoke(px), H.invoke(py), 0, 0, W.invoke(w1), H.invoke(h1));
            if (w2 > 0)
                graphics.CopyRect(buffArea, 0, H.invoke(py), W.invoke(w1), 0, W.invoke(w2), H.invoke(h1));
        }
        if (h2 > 0) {
            if (w1 > 0)
                graphics.CopyRect(buffArea, W.invoke(px), 0, 0, H.invoke(h1), W.invoke(w1), H.invoke(h2));
            if (w2 > 0)
                graphics.CopyRect(buffArea, 0, 0, H.invoke(w1), H.invoke(h1), W.invoke(w2), H.invoke(h2));
        }
        /* Render 2nd playfield */
        if (dualpf) {
            px = dualpx / BW;
            py = dualpy / BH;
            mx = dualpx % BW;
            my = dualpy % BH;
            while (dbuffpx < px) {
                DrawDBlocks(dbuffpx + SOW + 1, dbuffpy, 1, SOH + 1);
                dbuffpx++;
                dbuffdx = (dbuffdx + 1) % (SOW + 1);
            }
            while (dbuffpx > px) {
                if (dbuffdx <= 0)
                    dbuffdx = SOW;
                else
                    dbuffdx--;
                dbuffpx--;
                DrawDBlocks(dbuffpx, dbuffpy, 1, SOH + 1);
            }
            while (dbuffpy < py) {
                DrawDBlocks(dbuffpx, dbuffpy + SOH + 1, SOW + 1, 1);
                dbuffpy++;
                dbuffdy = (dbuffdy + 1) % (SOH + 1);
            }
            while (dbuffpy > py) {
                if (dbuffdy <= 0)
                    dbuffdy = SOH;
                else
                    dbuffdy--;
                dbuffpy--;
                DrawDBlocks(dbuffpx, dbuffpy, SOW + 1, 1);
            }
            /* Render */
            px = dbuffdx * BW + mx;
            py = dbuffdy * BH + my;
            w1 = OW - px;
            w2 = PW - w1;
            if (w1 > PW)
                w1 = PW;
            h1 = OH - py;
            h2 = PH - h1;
            if (h1 > PH)
                h1 = PH;
            graphics.SetArea(mainArea);
            graphics.SetCopyMode(Graphics.cmCopy);
            graphics.SetPlanes(240, false);
            if (h1 > 0) {
                if (w1 > 0)
                    graphics.CopyRect(dualArea, W.invoke(px), H.invoke(py), 0, 0, W.invoke(w1), H.invoke(h1));
                if (w2 > 0)
                    graphics.CopyRect(dualArea, 0, H.invoke(py), W.invoke(w1), 0, W.invoke(w2), H.invoke(h1));
            }
            if (h2 > 0) {
                if (w1 > 0)
                    graphics.CopyRect(dualArea, W.invoke(px), 0, 0, H.invoke(h1), W.invoke(w1), H.invoke(h2));
                if (w2 > 0)
                    graphics.CopyRect(dualArea, 0, 0, H.invoke(w1), H.invoke(h1), W.invoke(w2), H.invoke(h2));
            }
            graphics.SetPlanes(255, true);
            graphics.SetCopyMode(Graphics.cmCopy);
        }
    }

    public void RenderObjects() {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.Obj tail = null;
        int nwdth = 0;
        int px = 0;
        int py = 0;

        graphics.SetArea(mainArea);
        obj = (ChaosBase.Obj) chaosBase.FirstObj(chaosBase.objList);
        tail = (ChaosBase.Obj) chaosBase.TailObj(chaosBase.objList);
        if (mulS != 1) {
            while (obj != tail) {
                px = (int) (obj.x / ChaosBase.Frac);
                py = (int) (obj.y / ChaosBase.Frac);
                px = W.invoke(px - backpx);
                py = H.invoke(py - backpy);
                nwdth = obj.width;
                if (px + nwdth > W.invoke(PW))
                    nwdth = W.invoke(PW) - px;
                if (nwdth > 0)
                    graphics.CopyShadow(shapeArea, maskArea, obj.posX, obj.posY, px, py, nwdth, obj.height);
                obj = (ChaosBase.Obj) chaosBase.NextObj(obj.objNode);
            }
        } else {
            while (obj != tail) {
                px = (int) (obj.x / ChaosBase.Frac);
                py = (int) (obj.y / ChaosBase.Frac);
                px -= backpx;
                py -= backpy;
                nwdth = obj.width;
                if (px + nwdth > PW)
                    nwdth = PW - px;
                if (nwdth > 0)
                    graphics.CopyShadow(shapeArea, maskArea, obj.posX, obj.posY, px, py, nwdth, obj.height);
                obj = (ChaosBase.Obj) chaosBase.NextObj(obj.objNode);
            }
        }
    }

    public void DrawBackground() {
        // VAR
        int px = 0;
        int py = 0;

        px = (int) (chaosBase.mainPlayer.x / ChaosBase.Frac);
        px += chaosBase.mainPlayer.cx;
        py = (int) (chaosBase.mainPlayer.y / ChaosBase.Frac);
        py += chaosBase.mainPlayer.cy;
        SetBackgroundPos(px, py);
        /* Draw bufferArea */
        buffpx = backpx / BW;
        buffpy = backpy / BH;
        buffdx = 0;
        buffdy = 0;
        DrawBlocks(buffpx, buffpy, SOW + 1, SOH + 1);
        if (dualpf) {
            /* Draw dualArea */
            dbuffpx = dualpx / BW;
            dbuffpy = dualpy / BH;
            dbuffdx = 0;
            dbuffdy = 0;
            DrawDBlocks(dbuffpx, dbuffpy, SOW + 1, SOH + 1);
        }
        RenderBlocks();
    }

    public void MoveBackground(int px, int py) {
        if ((chaosBase.screenInverted > 0) && !color) {
            graphics.SetArea(mainArea);
            graphics.SetCopyMode(Graphics.cmXor);
            graphics.SetPen(1);
            graphics.FillRect(0, 0, W.invoke(PW), H.invoke(PH));
            graphics.SetCopyMode(Graphics.cmCopy);
        }
        RenderObjects();
        graphics.SwitchArea();
        SetBackgroundPos(px, py);
        RenderBlocks();
    }

    public void UpdateAnim() {
        graphics.SetArea(mainArea);
        DrawBackground();
        RenderObjects();
    }

    public void CloseScreen() {
        graphics.DeleteArea(new Runtime.FieldRef<>(this::getMaskArea, this::setMaskArea));
        graphics.DeleteArea(new Runtime.FieldRef<>(this::getShapeArea, this::setShapeArea));
        graphics.DeleteArea(new Runtime.FieldRef<>(this::getBuffArea, this::setBuffArea));
        graphics.DeleteArea(new Runtime.FieldRef<>(this::getDualArea, this::setDualArea));
        graphics.DeleteArea(new Runtime.FieldRef<>(this::getMainArea, this::setMainArea));
    }

    public void WaterPalette(/* VAR */ Runtime.IRef<Integer> r, /* VAR */ Runtime.IRef<Integer> g, /* VAR */ Runtime.IRef<Integer> b) {
        if (chaosBase.water) {
            r.set(r.get() / 4);
            if (255 - r.get() < b.get())
                b.set(255);
            else
                b.inc(r.get());
            g.dec(g.get() / 5);
        }
    }

    public void SetRGB(int col, /* WRT */ int _r, /* WRT */ int _g, /* WRT */ int _b) {
        Runtime.Ref<Integer> r = new Runtime.Ref<>(_r);
        Runtime.Ref<Integer> g = new Runtime.Ref<>(_g);
        Runtime.Ref<Integer> b = new Runtime.Ref<>(_b);

        cycling.excl(col);
        WaterPalette(r, g, b);
        { // WITH
            Palette _palette = palette[col];
            _palette.red = r.get();
            _palette.green = g.get();
            _palette.blue = b.get();
        }
    }

    public void CycleRGB(int col, int spd, /* WRT */ int _r, /* WRT */ int _g, /* WRT */ int _b) {
        Runtime.Ref<Integer> r = new Runtime.Ref<>(_r);
        Runtime.Ref<Integer> g = new Runtime.Ref<>(_g);
        Runtime.Ref<Integer> b = new Runtime.Ref<>(_b);

        cycling.incl(col);
        WaterPalette(r, g, b);
        cycleSpeed[col] = spd;
        { // WITH
            Palette _palette = cycle[col];
            _palette.red = r.get();
            _palette.green = g.get();
            _palette.blue = b.get();
        }
    }

    public void DualCycleRGB(int col, int spd, /* WRT */ int _r, /* WRT */ int _g, /* WRT */ int _b) {
        Runtime.Ref<Integer> r = new Runtime.Ref<>(_r);
        Runtime.Ref<Integer> g = new Runtime.Ref<>(_g);
        Runtime.Ref<Integer> b = new Runtime.Ref<>(_b);

        dualCycling.incl(col);
        WaterPalette(r, g, b);
        dualCycleSpeed[col] = spd;
        { // WITH
            Palette _palette = dualCycle[col];
            _palette.red = r.get();
            _palette.green = g.get();
            _palette.blue = b.get();
        }
    }

    public void SetTrans(int col, int trans) {
        transparent[col] = trans;
    }

    public void CopyToDual() {
        // VAR
        int x = 0;
        int y = 0;

        /* castle */
        for (y = 0; y <= 63; y++) {
            for (x = 0; x <= 63; x++) {
                dual[y][x] = castle[y][x];
            }
        }
        Runtime.copyArray(true, dualPalette, palette);
        Runtime.copyArray(true, dualCycle, cycle);
        dualCycling.copyFrom(cycling);
        Runtime.copyArray(true, dualCycleSpeed, cycleSpeed);
    }

    private void AnimCycle(int c, Palette cycle, int speed, int step, /* VAR */ Runtime.IRef<Integer> pos, /* VAR */ Runtime.RangeSet flags, /* VAR */ Runtime.IRef<Integer> r, /* VAR */ Runtime.IRef<Integer> g, /* VAR */ Runtime.IRef<Integer> b) {
        // VAR
        int add = 0;
        long dred = 0L;
        long dgreen = 0L;
        long dblue = 0L;
        long red = 0L;
        long green = 0L;
        long blue = 0L;
        long p = 0L;

        dred = cycle.red;
        dgreen = cycle.green;
        dblue = cycle.blue;
        dred -= r.get();
        dgreen -= g.get();
        dblue -= b.get();
        add = speed * step;
        if (add > 4096)
            add = 4096;
        if (flags.contains(c)) {
            if (add > pos.get()) {
                pos.set(add - pos.get());
                flags.excl(c);
            } else {
                pos.dec(add);
            }
        } else {
            pos.inc(add);
            if (pos.get() >= 4096) {
                flags.incl(c);
                pos.set(8192 - pos.get());
            }
        }
        red = r.get();
        green = g.get();
        blue = b.get();
        p = pos.get();
        red += dred * p / 4096;
        green += dgreen * p / 4096;
        blue += dblue * p / 4096;
        r.set((int) red);
        g.set((int) green);
        b.set((int) blue);
    }

    public void AnimPalette(int step) {
        // VAR
        Palette[] frtPalette = Runtime.initArray(new Palette[16]);
        Runtime.Ref<Integer> r1 = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> g1 = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> b1 = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> r2 = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> g2 = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> b2 = new Runtime.Ref<>(0);
        int t1 = 0;
        int t2 = 0;
        int c = 0;
        int d = 0;

        graphics.SetArea(mainArea);
        if (color) {
            if (dualpf) {
                for (c = 0; c <= 15; c++) {
                    { // WITH
                        Palette _palette = palette[c];
                        r1.set(_palette.red);
                        g1.set(_palette.green);
                        b1.set(_palette.blue);
                    }
                    if (cycling.contains(c))
                        AnimCycle(c, cycle[c], cycleSpeed[c], step, new Runtime.ArrayElementRef<>(cpPos, c), cpReverse, r1, g1, b1);
                    { // WITH
                        Palette _palette = frtPalette[c];
                        _palette.red = r1.get();
                        _palette.green = g1.get();
                        _palette.blue = b1.get();
                    }
                }
                for (d = 0; d <= 15; d++) {
                    { // WITH
                        Palette _palette = dualPalette[d];
                        r2.set(_palette.red);
                        g2.set(_palette.green);
                        b2.set(_palette.blue);
                    }
                    if (dualCycling.contains(d))
                        AnimCycle(d, dualCycle[d], dualCycleSpeed[d], step, new Runtime.ArrayElementRef<>(dcpPos, d), dcpReverse, r2, g2, b2);
                    for (c = 0; c <= 15; c++) {
                        { // WITH
                            Palette _palette = frtPalette[c];
                            r1.set(_palette.red);
                            g1.set(_palette.green);
                            b1.set(_palette.blue);
                        }
                        t1 = transparent[c];
                        t2 = 255 - t1;
                        graphics.SetPalette(d * 16 + c, (r1.get() * t1 + r2.get() * t2) / 255, (g1.get() * t1 + g2.get() * t2) / 255, (b1.get() * t1 + b2.get() * t2) / 255);
                    }
                }
            } else {
                for (c = 0; c <= 15; c++) {
                    { // WITH
                        Palette _palette = palette[c];
                        r1.set(_palette.red);
                        g1.set(_palette.green);
                        b1.set(_palette.blue);
                    }
                    if (cycling.contains(c))
                        AnimCycle(c, cycle[c], cycleSpeed[c], step, new Runtime.ArrayElementRef<>(cpPos, c), cpReverse, r1, g1, b1);
                    graphics.SetPalette(c, r1.get(), g1.get(), b1.get());
                }
            }
        } else {
            graphics.SetPalette(0, 0, 0, 0);
            graphics.SetPalette(1, 255, 255, 255);
        }
    }

    public void UpdatePalette() {
        AnimPalette(0);
    }

    public void ResetCycle(boolean end) {
        // VAR
        Runtime.Ref<Integer> r = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> g = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> b = new Runtime.Ref<>(0);
        int c = 0;

        for (c = 0; c <= 15; c++) {
            if (end) {
                cpPos[c] = 4096;
                dcpPos[c] = 4096;
            } else {
                { // WITH
                    Palette _palette = palette[c];
                    r.set(_palette.red);
                    g.set(_palette.green);
                    b.set(_palette.blue);
                }
                AnimCycle(c, cycle[c], cycleSpeed[c], 0, new Runtime.ArrayElementRef<>(cpPos, c), cpReverse, r, g, b);
                { // WITH
                    Palette _palette = palette[c];
                    _palette.red = r.get();
                    _palette.green = g.get();
                    _palette.blue = b.get();
                }
                { // WITH
                    Palette _palette = dualPalette[c];
                    r.set(_palette.red);
                    g.set(_palette.green);
                    b.set(_palette.blue);
                }
                AnimCycle(c, dualCycle[c], dualCycleSpeed[c], 0, new Runtime.ArrayElementRef<>(dcpPos, c), dcpReverse, r, g, b);
                { // WITH
                    Palette _palette = dualPalette[c];
                    _palette.red = r.get();
                    _palette.green = g.get();
                    _palette.blue = b.get();
                }
                cpPos[c] = 0;
                dcpPos[c] = 0;
            }
        }
        cycling = new Runtime.RangeSet(Memory.SET16_r);
        dualCycling = new Runtime.RangeSet(Memory.SET16_r);
        if (end) {
            cpReverse = new Runtime.RangeSet(Memory.SET16_r).withRange(0, 15);
            dcpReverse = new Runtime.RangeSet(Memory.SET16_r).withRange(0, 15);
        } else {
            cpReverse = new Runtime.RangeSet(Memory.SET16_r);
            dcpReverse = new Runtime.RangeSet(Memory.SET16_r);
        }
    }

    public void WriteAt(int x, int y, Runtime.IRef<String> t) {
        graphics.SetTextPos(x, y);
        graphics.DrawText(t);
    }

    public void CenterText(int x, int y, int w, Runtime.IRef<String> t) {
        // VAR
        int pw = 0;

        pw = graphics.TextWidth(t);
        x += (w - pw) / 2;
        graphics.SetTextPos(x, y);
        graphics.DrawText(t);
    }

    private void WriteShort(boolean z, int v) {
        // VAR
        int q = 0;
        int d = 0;
        Runtime.Ref<String> ch = new Runtime.Ref<>("");

        Runtime.setChar(ch, 1, ((char) 0));
        q = 1000;
        while (q > 0) {
            d = v / q;
            v = v % q;
            q = q / 10;
            if (q == 0)
                z = true;
            if ((d != 0) || z) {
                Runtime.setChar(ch, 0, (char) ('0' + d));
                graphics.DrawText(ch);
                z = true;
            }
        }
    }

    public void WriteCard(int x, int y, long v) {
        // VAR
        boolean z = false;
        long q = 0L;
        int d = 0;
        Runtime.Ref<String> ch = new Runtime.Ref<>("");

        graphics.SetTextPos(x, y);
        Runtime.setChar(ch, 1, ((char) 0));
        z = false;
        if (v >= 10000) {
            q = 1000000000;
            while (q >= 10000) {
                d = (int) (v / q);
                v = v % q;
                q = q / 10;
                if ((d != 0) || z) {
                    Runtime.setChar(ch, 0, (char) ('0' + d));
                    graphics.DrawText(ch);
                    z = true;
                }
            }
        }
        WriteShort(z, (int) v);
    }

    private int FastS(int s) {
        return s;
    }

    private final AdjustProc FastS_ref = this::FastS;

    private int MultS(int s) {
        return s * mulS;
    }

    private final AdjustProc MultS_ref = this::MultS;

    private int FastX(int x) {
        return x + OX;
    }

    private final AdjustProc FastX_ref = this::FastX;

    private int FastY(int y) {
        return y + OY;
    }

    private final AdjustProc FastY_ref = this::FastY;

    private int MultX(int x) {
        return x * mulS + OX;
    }

    private final AdjustProc MultX_ref = this::MultX;

    private int MultY(int y) {
        return y * mulS + OY;
    }

    private final AdjustProc MultY_ref = this::MultY;

    public void SetOrigin(int ox, int oy) {
        OX = ox * mulS;
        OY = oy * mulS;
        if (mulS == 1) {
            W = FastS_ref;
            H = FastS_ref;
            if (OX == 0)
                X = FastS_ref;
            else
                X = FastX_ref;
            if (OY == 0)
                Y = FastS_ref;
            else
                Y = FastY_ref;
        } else {
            W = MultS_ref;
            H = MultS_ref;
            if (OX == 0)
                X = MultS_ref;
            else
                X = MultX_ref;
            if (OY == 0)
                Y = MultS_ref;
            else
                Y = MultY_ref;
        }
    }

    private void Close() {
        CloseScreen();
        memory.FreeMem(new Runtime.FieldRef<>(this::getDual, this::setDual).asAdrRef());
        memory.FreeMem(new Runtime.FieldRef<>(this::getCastle, this::setCastle).asAdrRef());
    }

    private final Runnable Close_ref = this::Close;

    private void InitColors() {
        // VAR
        int c = 0;
        int f = 0;

        for (c = 0; c <= 15; c++) {
            SetRGB(c, 0, 0, 0);
            cycle[c].copyFrom(palette[c]);
            cpPos[c] = 0;
            dcpPos[c] = 0;
            cpReverse = new Runtime.RangeSet(Memory.SET16_r);
            dcpReverse = new Runtime.RangeSet(Memory.SET16_r);
        }
        CopyToDual();
        SetRGB(0, 0, 0, 0);
        SetRGB(1, 127, 127, 127);
        for (c = 8; c <= 15; c++) {
            f = c * 17;
            SetRGB(c, f, f, f);
        }
        SetRGB(2, 255, 255, 255);
        CycleRGB(2, 40, 127, 255, 255);
        SetRGB(3, 255, 255, 0);
        CycleRGB(3, 200, 255, 200, 0);
        SetRGB(4, 255, 0, 0);
        SetRGB(5, 0, 255, 0);
        SetRGB(6, 0, 0, 255);
        SetRGB(7, 210, 140, 0);
        for (c = 0; c <= 15; c++) {
            SetTrans(c, 255);
        }
    }


    // Support

    private static ChaosGraphics instance;

    public static ChaosGraphics instance() {
        if (instance == null)
            new ChaosGraphics(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        mainArea = graphics.noArea;
        buffArea = graphics.noArea;
        shapeArea = graphics.noArea;
        maskArea = graphics.noArea;
        imageArea = graphics.noArea;
        image2Area = graphics.noArea;
        dualArea = graphics.noArea;
        backpx = 0;
        backpy = 0;
        dualpx = 0;
        dualpy = 0;
        backaddx = 0;
        backaddy = 0;
        stepmod = 0;
        dualSpeed = 3;
        dualpf = false;
        castle = null;
        dual = null;
        checks.AddTermProc(Close_ref);
        castle = (int[][]) memory.AllocMem(Runtime.sizeOf(65024, int[][].class, 127, 128));
        checks.CheckMem(castle);
        dual = (int[][]) memory.AllocMem(Runtime.sizeOf(16384, int[][].class, 64, 64));
        checks.CheckMem(dual);
        mulS = 1;
        SetOrigin(0, 0);
        InitColors();
    }

    public void close() {
    }

}
