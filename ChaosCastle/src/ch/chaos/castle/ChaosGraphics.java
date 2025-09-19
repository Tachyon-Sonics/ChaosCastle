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

    public static final int SW = 320;
    public static final int PW = 240;
    public static final int IW = 80;
    public static final int SH = 240;
    public static final int PH = SH;
    public static final int IH = SH;
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

        public short red;
        public short green;
        public short blue;


        public short getRed() {
            return this.red;
        }

        public void setRed(short red) {
            this.red = red;
        }

        public short getGreen() {
            return this.green;
        }

        public void setGreen(short green) {
            this.green = green;
        }

        public short getBlue() {
            return this.blue;
        }

        public void setBlue(short blue) {
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
        public short invoke(short arg1);
    }


    // VAR

    public short[][] castle /* POINTER */;
    public short[][] dual /* POINTER */;
    public short castleWidth;
    public short castleHeight;
    public short gameWidth;
    public short gameHeight;
    public short backpx;
    public short backpy;
    public short dualpx;
    public short dualpy;
    public Graphics.AreaPtr mainArea;
    public Graphics.AreaPtr buffArea;
    public Graphics.AreaPtr dualArea;
    public Graphics.AreaPtr shapeArea;
    public Graphics.AreaPtr maskArea;
    public Graphics.AreaPtr imageArea;
    public Graphics.AreaPtr image2Area;
    public short mulS;
    public short dualSpeed;
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
    public short[] cycleSpeed = new short[16];
    public short[] dualCycleSpeed = new short[16];
    public short[] transparent = new short[16];
    public AdjustProc X;
    public AdjustProc Y;
    public AdjustProc W;
    public AdjustProc H;


    public short[][] getCastle() {
        return this.castle;
    }

    public void setCastle(short[][] castle) {
        this.castle = castle;
    }

    public short[][] getDual() {
        return this.dual;
    }

    public void setDual(short[][] dual) {
        this.dual = dual;
    }

    public short getCastleWidth() {
        return this.castleWidth;
    }

    public void setCastleWidth(short castleWidth) {
        this.castleWidth = castleWidth;
    }

    public short getCastleHeight() {
        return this.castleHeight;
    }

    public void setCastleHeight(short castleHeight) {
        this.castleHeight = castleHeight;
    }

    public short getGameWidth() {
        return this.gameWidth;
    }

    public void setGameWidth(short gameWidth) {
        this.gameWidth = gameWidth;
    }

    public short getGameHeight() {
        return this.gameHeight;
    }

    public void setGameHeight(short gameHeight) {
        this.gameHeight = gameHeight;
    }

    public short getBackpx() {
        return this.backpx;
    }

    public void setBackpx(short backpx) {
        this.backpx = backpx;
    }

    public short getBackpy() {
        return this.backpy;
    }

    public void setBackpy(short backpy) {
        this.backpy = backpy;
    }

    public short getDualpx() {
        return this.dualpx;
    }

    public void setDualpx(short dualpx) {
        this.dualpx = dualpx;
    }

    public short getDualpy() {
        return this.dualpy;
    }

    public void setDualpy(short dualpy) {
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

    public short getMulS() {
        return this.mulS;
    }

    public void setMulS(short mulS) {
        this.mulS = mulS;
    }

    public short getDualSpeed() {
        return this.dualSpeed;
    }

    public void setDualSpeed(short dualSpeed) {
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

    public short[] getCycleSpeed() {
        return this.cycleSpeed;
    }

    public void setCycleSpeed(short[] cycleSpeed) {
        this.cycleSpeed = cycleSpeed;
    }

    public short[] getDualCycleSpeed() {
        return this.dualCycleSpeed;
    }

    public void setDualCycleSpeed(short[] dualCycleSpeed) {
        this.dualCycleSpeed = dualCycleSpeed;
    }

    public short[] getTransparent() {
        return this.transparent;
    }

    public void setTransparent(short[] transparent) {
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

    private short buffdx;
    private short buffdy;
    private short buffpx;
    private short buffpy;
    private short dbuffdx;
    private short dbuffdy;
    private short dbuffpx;
    private short dbuffpy;
    private short backox;
    private short backoy;
    private short backaddx;
    private short backaddy;
    private short backtox;
    private short backtoy;
    private int stepmod;
    private int[] cpPos = new int[16];
    private int[] dcpPos = new int[16];
    private Runtime.RangeSet cpReverse = new Runtime.RangeSet(Memory.SET16_r);
    private Runtime.RangeSet dcpReverse = new Runtime.RangeSet(Memory.SET16_r);
    private short OX;
    private short OY;


    public short getBuffdx() {
        return this.buffdx;
    }

    public void setBuffdx(short buffdx) {
        this.buffdx = buffdx;
    }

    public short getBuffdy() {
        return this.buffdy;
    }

    public void setBuffdy(short buffdy) {
        this.buffdy = buffdy;
    }

    public short getBuffpx() {
        return this.buffpx;
    }

    public void setBuffpx(short buffpx) {
        this.buffpx = buffpx;
    }

    public short getBuffpy() {
        return this.buffpy;
    }

    public void setBuffpy(short buffpy) {
        this.buffpy = buffpy;
    }

    public short getDbuffdx() {
        return this.dbuffdx;
    }

    public void setDbuffdx(short dbuffdx) {
        this.dbuffdx = dbuffdx;
    }

    public short getDbuffdy() {
        return this.dbuffdy;
    }

    public void setDbuffdy(short dbuffdy) {
        this.dbuffdy = dbuffdy;
    }

    public short getDbuffpx() {
        return this.dbuffpx;
    }

    public void setDbuffpx(short dbuffpx) {
        this.dbuffpx = dbuffpx;
    }

    public short getDbuffpy() {
        return this.dbuffpy;
    }

    public void setDbuffpy(short dbuffpy) {
        this.dbuffpy = dbuffpy;
    }

    public short getBackox() {
        return this.backox;
    }

    public void setBackox(short backox) {
        this.backox = backox;
    }

    public short getBackoy() {
        return this.backoy;
    }

    public void setBackoy(short backoy) {
        this.backoy = backoy;
    }

    public short getBackaddx() {
        return this.backaddx;
    }

    public void setBackaddx(short backaddx) {
        this.backaddx = backaddx;
    }

    public short getBackaddy() {
        return this.backaddy;
    }

    public void setBackaddy(short backaddy) {
        this.backaddy = backaddy;
    }

    public short getBacktox() {
        return this.backtox;
    }

    public void setBacktox(short backtox) {
        this.backtox = backtox;
    }

    public short getBacktoy() {
        return this.backtoy;
    }

    public void setBacktoy(short backtoy) {
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

    public short getOX() {
        return this.OX;
    }

    public void setOX(short OX) {
        this.OX = OX;
    }

    public short getOY() {
        return this.OY;
    }

    public void setOY(short OY) {
        this.OY = OY;
    }


    // PROCEDURE

    public boolean OpenScreen() {
        // VAR
        long nbColor = 0L;
        GraphicsErr err = GraphicsErr.gOk;

        CloseScreen();
        SetOrigin((short) 0, (short) 0);
        if (color) {
            if (dualpf)
                nbColor = 256;
            else
                nbColor = 16;
        } else {
            nbColor = 2;
        }
        err = graphics.GetGraphicsErr();
        mainArea = graphics.CreateArea((Memory.TagItem) memory.TAG4(Graphics.aSIZEX, W.invoke((short) SW), Graphics.aSIZEY, H.invoke((short) SH), Graphics.aCOLOR, nbColor, Graphics.aTYPE, Graphics.atBUFFER));
        if (mainArea == graphics.noArea)
            return false;
        buffArea = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, W.invoke((short) OW), Graphics.aSIZEY, H.invoke((short) OH), Graphics.aCOLOR, nbColor));
        if (buffArea == graphics.noArea)
            return false;
        if (dualpf) {
            dualArea = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, W.invoke((short) OW), Graphics.aSIZEY, H.invoke((short) OH), Graphics.aCOLOR, nbColor));
            if (dualArea == graphics.noArea)
                return false;
        }
        shapeArea = graphics.CreateArea((Memory.TagItem) memory.TAG3(Graphics.aSIZEX, W.invoke((short) 256), Graphics.aSIZEY, H.invoke((short) 296), Graphics.aCOLOR, nbColor));
        if (shapeArea == graphics.noArea)
            return false;
        maskArea = graphics.CreateArea((Memory.TagItem) memory.TAG4(Graphics.aSIZEX, W.invoke((short) 256), Graphics.aSIZEY, H.invoke((short) 296), Graphics.aCOLOR, 2, Graphics.aTYPE, Graphics.atMASK));
        if (maskArea == graphics.noArea)
            return false;
        graphics.SetArea(mainArea);
        graphics.SetBuffer(true, false);
        UpdatePalette();
        graphics.AreaToFront();
        return true;
    }

    private void SetBackgroundPos(short px, short py) {
        // CONST
        final int RW = PW / 2;
        final int RH = PH / 2;

        // VAR
        short maxx = 0;
        short maxy = 0;
        short xstep = 0;
        short ystep = 0;

        maxx = (short) (gameWidth - PW);
        maxy = (short) (gameHeight - PH);
        backox = backpx;
        backoy = backpy;
        xstep = (short) ((chaosBase.step + 2) / 3);
        ystep = xstep;
        stepmod = (chaosBase.step + stepmod) % 3;
        if (chaosBase.mainPlayer.dvx > 0)
            backaddx = 32;
        else if (chaosBase.mainPlayer.dvx < 0)
            backaddx = -32;
        if (chaosBase.mainPlayer.dvy > 0)
            backaddy = 32;
        else if (chaosBase.mainPlayer.dvy < 0)
            backaddy = -32;
        backtox = (short) (px + backaddx - RW);
        backtoy = (short) (py + backaddy - RH);
        if (Math.abs(backtox - backpx) > 100)
            xstep = (short) (xstep * 2);
        if (Math.abs(backtoy - backpy) > 100)
            ystep = (short) (ystep * 2);
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
            dualpx = (short) (backpx / dualSpeed);
            dualpy = (short) (backpy / dualSpeed);
        }
    }

    private void DrawBlocks(short x, short y, short w, short h) {
        // VAR
        short bx = 0;
        short by = 0;
        short cx = 0;
        short cy = 0;
        short dx = 0;
        short dy = 0;

        dy = (short) ((buffdy + h) % (SOH + 1));
        cy = (short) (y + h);
        graphics.SetArea(buffArea);
        if (dualpf)
            graphics.SetPlanes(15, true);
        while (cy > y) {
            cy--;
            if (dy == 0)
                dy = SOH;
            else
                dy--;
            dx = (short) ((buffdx + w) % (SOW + 1));
            cx = (short) (x + w);
            while (cx > x) {
                cx--;
                if (dx == 0)
                    dx = SOW;
                else
                    dx--;
                if ((cx < castleWidth) && (cy < castleHeight)) {
                    bx = (castle[cy][cx]);
                    by = (short) ((bx / 8) * BH);
                    bx = (short) ((bx % 8) * BW);
                    graphics.CopyRect(imageArea, W.invoke(bx), H.invoke(by), W.invoke((short) (dx * BW)), H.invoke((short) (dy * BH)), W.invoke((short) BW), H.invoke((short) BH));
                }
            }
        }
    }

    private void DrawDBlocks(short x, short y, short w, short h) {
        // VAR
        short bx = 0;
        short by = 0;
        short cx = 0;
        short cy = 0;
        short dx = 0;
        short dy = 0;

        dy = (short) ((dbuffdy + h) % (SOH + 1));
        cy = (short) (y + h);
        graphics.SetArea(dualArea);
        graphics.SetPlanes(240, true);
        while (cy > y) {
            cy--;
            if (dy == 0)
                dy = SOH;
            else
                dy--;
            dx = (short) ((dbuffdx + w) % (SOW + 1));
            cx = (short) (x + w);
            while (cx > x) {
                cx--;
                if (dx == 0)
                    dx = SOW;
                else
                    dx--;
                if ((cx < 64) && (cy < 64)) {
                    bx = (dual[cy][cx]);
                    by = (short) ((bx / 8) * BH);
                    bx = (short) ((bx % 8) * BW);
                    graphics.CopyRect(image2Area, W.invoke(bx), H.invoke(by), W.invoke((short) (dx * BW)), H.invoke((short) (dy * BH)), W.invoke((short) BW), H.invoke((short) BH));
                }
            }
        }
    }

    private void RenderBlocks() {
        // VAR
        short px = 0;
        short py = 0;
        short mx = 0;
        short my = 0;
        short w1 = 0;
        short w2 = 0;
        short h1 = 0;
        short h2 = 0;

        px = (short) (backpx / BW);
        py = (short) (backpy / BH);
        mx = (short) (backpx % BW);
        my = (short) (backpy % BH);
        while (buffpx < px) {
            DrawBlocks((short) (buffpx + SOW + 1), buffpy, (short) 1, (short) (SOH + 1));
            buffpx++;
            buffdx = (short) ((buffdx + 1) % (SOW + 1));
        }
        while (buffpx > px) {
            if (buffdx <= 0)
                buffdx = SOW;
            else
                buffdx--;
            buffpx--;
            DrawBlocks(buffpx, buffpy, (short) 1, (short) (SOH + 1));
        }
        while (buffpy < py) {
            DrawBlocks(buffpx, (short) (buffpy + SOH + 1), (short) (SOW + 1), (short) 1);
            buffpy++;
            buffdy = (short) ((buffdy + 1) % (SOH + 1));
        }
        while (buffpy > py) {
            if (buffdy <= 0)
                buffdy = SOH;
            else
                buffdy--;
            buffpy--;
            DrawBlocks(buffpx, buffpy, (short) (SOW + 1), (short) 1);
        }
        px = (short) (buffdx * BW + mx);
        py = (short) (buffdy * BH + my);
        w1 = (short) (OW - px);
        w2 = (short) (PW - w1);
        if (w1 > PW)
            w1 = PW;
        h1 = (short) (OH - py);
        h2 = (short) (PH - h1);
        if (h1 > PH)
            h1 = PH;
        graphics.SetArea(mainArea);
        graphics.SetCopyMode(Graphics.cmCopy);
        if (dualpf)
            graphics.SetPlanes(15, true);
        if (h1 > 0) {
            if (w1 > 0)
                graphics.CopyRect(buffArea, W.invoke(px), H.invoke(py), (short) 0, (short) 0, W.invoke(w1), H.invoke(h1));
            if (w2 > 0)
                graphics.CopyRect(buffArea, (short) 0, H.invoke(py), W.invoke(w1), (short) 0, W.invoke(w2), H.invoke(h1));
        }
        if (h2 > 0) {
            if (w1 > 0)
                graphics.CopyRect(buffArea, W.invoke(px), (short) 0, (short) 0, H.invoke(h1), W.invoke(w1), H.invoke(h2));
            if (w2 > 0)
                graphics.CopyRect(buffArea, (short) 0, (short) 0, H.invoke(w1), H.invoke(h1), W.invoke(w2), H.invoke(h2));
        }
        if (dualpf) {
            px = (short) (dualpx / BW);
            py = (short) (dualpy / BH);
            mx = (short) (dualpx % BW);
            my = (short) (dualpy % BH);
            while (dbuffpx < px) {
                DrawDBlocks((short) (dbuffpx + SOW + 1), dbuffpy, (short) 1, (short) (SOH + 1));
                dbuffpx++;
                dbuffdx = (short) ((dbuffdx + 1) % (SOW + 1));
            }
            while (dbuffpx > px) {
                if (dbuffdx <= 0)
                    dbuffdx = SOW;
                else
                    dbuffdx--;
                dbuffpx--;
                DrawDBlocks(dbuffpx, dbuffpy, (short) 1, (short) (SOH + 1));
            }
            while (dbuffpy < py) {
                DrawDBlocks(dbuffpx, (short) (dbuffpy + SOH + 1), (short) (SOW + 1), (short) 1);
                dbuffpy++;
                dbuffdy = (short) ((dbuffdy + 1) % (SOH + 1));
            }
            while (dbuffpy > py) {
                if (dbuffdy <= 0)
                    dbuffdy = SOH;
                else
                    dbuffdy--;
                dbuffpy--;
                DrawDBlocks(dbuffpx, dbuffpy, (short) (SOW + 1), (short) 1);
            }
            px = (short) (dbuffdx * BW + mx);
            py = (short) (dbuffdy * BH + my);
            w1 = (short) (OW - px);
            w2 = (short) (PW - w1);
            if (w1 > PW)
                w1 = PW;
            h1 = (short) (OH - py);
            h2 = (short) (PH - h1);
            if (h1 > PH)
                h1 = PH;
            graphics.SetArea(mainArea);
            graphics.SetCopyMode(Graphics.cmCopy);
            graphics.SetPlanes(240, false);
            if (h1 > 0) {
                if (w1 > 0)
                    graphics.CopyRect(dualArea, W.invoke(px), H.invoke(py), (short) 0, (short) 0, W.invoke(w1), H.invoke(h1));
                if (w2 > 0)
                    graphics.CopyRect(dualArea, (short) 0, H.invoke(py), W.invoke(w1), (short) 0, W.invoke(w2), H.invoke(h1));
            }
            if (h2 > 0) {
                if (w1 > 0)
                    graphics.CopyRect(dualArea, W.invoke(px), (short) 0, (short) 0, H.invoke(h1), W.invoke(w1), H.invoke(h2));
                if (w2 > 0)
                    graphics.CopyRect(dualArea, (short) 0, (short) 0, H.invoke(w1), H.invoke(h1), W.invoke(w2), H.invoke(h2));
            }
            graphics.SetPlanes(255, true);
            graphics.SetCopyMode(Graphics.cmCopy);
        }
    }

    public void RenderObjects() {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.Obj tail = null;
        short nwdth = 0;
        short px = 0;
        short py = 0;

        graphics.SetArea(mainArea);
        obj = (ChaosBase.Obj) chaosBase.FirstObj(chaosBase.objList);
        tail = (ChaosBase.Obj) chaosBase.TailObj(chaosBase.objList);
        if (mulS != 1) {
            while (obj != tail) {
                px = (short) (obj.x / ChaosBase.Frac);
                py = (short) (obj.y / ChaosBase.Frac);
                px = W.invoke((short) (px - backpx));
                py = H.invoke((short) (py - backpy));
                nwdth = obj.width;
                if (px + nwdth > W.invoke((short) PW))
                    nwdth = (short) (W.invoke((short) PW) - px);
                if (nwdth > 0)
                    graphics.CopyShadow(shapeArea, maskArea, obj.posX, obj.posY, px, py, nwdth, obj.height);
                obj = (ChaosBase.Obj) chaosBase.NextObj(obj.objNode);
            }
        } else {
            while (obj != tail) {
                px = (short) (obj.x / ChaosBase.Frac);
                py = (short) (obj.y / ChaosBase.Frac);
                px -= backpx;
                py -= backpy;
                nwdth = obj.width;
                if (px + nwdth > PW)
                    nwdth = (short) (PW - px);
                if (nwdth > 0)
                    graphics.CopyShadow(shapeArea, maskArea, obj.posX, obj.posY, px, py, nwdth, obj.height);
                obj = (ChaosBase.Obj) chaosBase.NextObj(obj.objNode);
            }
        }
    }

    public void DrawBackground() {
        // VAR
        short px = 0;
        short py = 0;

        px = (short) (chaosBase.mainPlayer.x / ChaosBase.Frac);
        px += chaosBase.mainPlayer.cx;
        py = (short) (chaosBase.mainPlayer.y / ChaosBase.Frac);
        py += chaosBase.mainPlayer.cy;
        SetBackgroundPos(px, py);
        buffpx = (short) (backpx / BW);
        buffpy = (short) (backpy / BH);
        buffdx = 0;
        buffdy = 0;
        DrawBlocks(buffpx, buffpy, (short) (SOW + 1), (short) (SOH + 1));
        if (dualpf) {
            dbuffpx = (short) (dualpx / BW);
            dbuffpy = (short) (dualpy / BH);
            dbuffdx = 0;
            dbuffdy = 0;
            DrawDBlocks(dbuffpx, dbuffpy, (short) (SOW + 1), (short) (SOH + 1));
        }
        RenderBlocks();
    }

    public void MoveBackground(short px, short py) {
        if ((chaosBase.screenInverted > 0) && !color) {
            graphics.SetArea(mainArea);
            graphics.SetCopyMode(Graphics.cmXor);
            graphics.SetPen(1);
            graphics.FillRect((short) 0, (short) 0, W.invoke((short) PW), H.invoke((short) PH));
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

    public void WaterPalette(/* VAR */ Runtime.IRef<Short> r, /* VAR */ Runtime.IRef<Short> g, /* VAR */ Runtime.IRef<Short> b) {
        if (chaosBase.water) {
            r.set((short) (r.get() / 4));
            if (255 - r.get() < b.get())
                b.set((short) 255);
            else
                b.inc(r.get());
            g.dec(g.get() / 5);
        }
    }

    public void SetRGB(short col, /* WRT */ short _r, /* WRT */ short _g, /* WRT */ short _b) {
        Runtime.Ref<Short> r = new Runtime.Ref<>(_r);
        Runtime.Ref<Short> g = new Runtime.Ref<>(_g);
        Runtime.Ref<Short> b = new Runtime.Ref<>(_b);

        cycling.excl(col);
        WaterPalette(r, g, b);
        { // WITH
            Palette _palette = palette[col];
            _palette.red = r.get();
            _palette.green = g.get();
            _palette.blue = b.get();
        }
    }

    public void CycleRGB(short col, short spd, /* WRT */ short _r, /* WRT */ short _g, /* WRT */ short _b) {
        Runtime.Ref<Short> r = new Runtime.Ref<>(_r);
        Runtime.Ref<Short> g = new Runtime.Ref<>(_g);
        Runtime.Ref<Short> b = new Runtime.Ref<>(_b);

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

    public void DualCycleRGB(short col, short spd, /* WRT */ short _r, /* WRT */ short _g, /* WRT */ short _b) {
        Runtime.Ref<Short> r = new Runtime.Ref<>(_r);
        Runtime.Ref<Short> g = new Runtime.Ref<>(_g);
        Runtime.Ref<Short> b = new Runtime.Ref<>(_b);

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

    public void SetTrans(short col, short trans) {
        transparent[col] = trans;
    }

    public void CopyToDual() {
        // VAR
        short x = 0;
        short y = 0;

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
        int dred = 0;
        int dgreen = 0;
        int dblue = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        int p = 0;

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
        r.set(red);
        g.set(green);
        b.set(blue);
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
        short c = 0;
        short d = 0;

        graphics.SetArea(mainArea);
        if (color) {
            if (dualpf) {
                for (c = 0; c <= 15; c++) {
                    { // WITH
                        Palette _palette = palette[c];
                        r1.set((int) _palette.red);
                        g1.set((int) _palette.green);
                        b1.set((int) _palette.blue);
                    }
                    if (cycling.contains(c))
                        AnimCycle(c, cycle[c], cycleSpeed[c], step, new Runtime.ArrayElementRef<>(cpPos, c), cpReverse, r1, g1, b1);
                    { // WITH
                        Palette _palette = frtPalette[c];
                        _palette.red = (short) (int) r1.get();
                        _palette.green = (short) (int) g1.get();
                        _palette.blue = (short) (int) b1.get();
                    }
                }
                for (d = 0; d <= 15; d++) {
                    { // WITH
                        Palette _palette = dualPalette[d];
                        r2.set((int) _palette.red);
                        g2.set((int) _palette.green);
                        b2.set((int) _palette.blue);
                    }
                    if (dualCycling.contains(d))
                        AnimCycle(d, dualCycle[d], dualCycleSpeed[d], step, new Runtime.ArrayElementRef<>(dcpPos, d), dcpReverse, r2, g2, b2);
                    for (c = 0; c <= 15; c++) {
                        { // WITH
                            Palette _palette = frtPalette[c];
                            r1.set((int) _palette.red);
                            g1.set((int) _palette.green);
                            b1.set((int) _palette.blue);
                        }
                        t1 = transparent[c];
                        t2 = 255 - t1;
                        graphics.SetPalette((short) (d * 16 + c), (short) ((r1.get() * t1 + r2.get() * t2) / 255), (short) ((g1.get() * t1 + g2.get() * t2) / 255), (short) ((b1.get() * t1 + b2.get() * t2) / 255));
                    }
                }
            } else {
                for (c = 0; c <= 15; c++) {
                    { // WITH
                        Palette _palette = palette[c];
                        r1.set((int) _palette.red);
                        g1.set((int) _palette.green);
                        b1.set((int) _palette.blue);
                    }
                    if (cycling.contains(c))
                        AnimCycle(c, cycle[c], cycleSpeed[c], step, new Runtime.ArrayElementRef<>(cpPos, c), cpReverse, r1, g1, b1);
                    graphics.SetPalette(c, (short) (int) r1.get(), (short) (int) g1.get(), (short) (int) b1.get());
                }
            }
        } else {
            graphics.SetPalette((short) 0, (short) 0, (short) 0, (short) 0);
            graphics.SetPalette((short) 1, (short) 255, (short) 255, (short) 255);
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
                    r.set((int) _palette.red);
                    g.set((int) _palette.green);
                    b.set((int) _palette.blue);
                }
                AnimCycle(c, cycle[c], cycleSpeed[c], 0, new Runtime.ArrayElementRef<>(cpPos, c), cpReverse, r, g, b);
                { // WITH
                    Palette _palette = palette[c];
                    _palette.red = (short) (int) r.get();
                    _palette.green = (short) (int) g.get();
                    _palette.blue = (short) (int) b.get();
                }
                { // WITH
                    Palette _palette = dualPalette[c];
                    r.set((int) _palette.red);
                    g.set((int) _palette.green);
                    b.set((int) _palette.blue);
                }
                AnimCycle(c, dualCycle[c], dualCycleSpeed[c], 0, new Runtime.ArrayElementRef<>(dcpPos, c), dcpReverse, r, g, b);
                { // WITH
                    Palette _palette = dualPalette[c];
                    _palette.red = (short) (int) r.get();
                    _palette.green = (short) (int) g.get();
                    _palette.blue = (short) (int) b.get();
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

    public void WriteAt(short x, short y, Runtime.IRef<String> t) {
        graphics.SetTextPos(x, y);
        graphics.DrawText(t);
    }

    public void CenterText(short x, short y, short w, Runtime.IRef<String> t) {
        // VAR
        short pw = 0;

        pw = graphics.TextWidth(t);
        x += (w - pw) / 2;
        graphics.SetTextPos(x, y);
        graphics.DrawText(t);
    }

    private void WriteShort(boolean z, int v) {
        // VAR
        int q = 0;
        short d = 0;
        Runtime.Ref<String> ch = new Runtime.Ref<>("");

        Runtime.setChar(ch, 1, ((char) 0));
        q = 1000;
        while (q > 0) {
            d = (short) (v / q);
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

    public void WriteCard(short x, short y, long v) {
        // VAR
        boolean z = false;
        long q = 0L;
        short d = 0;
        Runtime.Ref<String> ch = new Runtime.Ref<>("");

        graphics.SetTextPos(x, y);
        Runtime.setChar(ch, 1, ((char) 0));
        z = false;
        if (v >= 10000) {
            q = 1000000000;
            while (q >= 10000) {
                d = (short) (v / q);
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

    private short FastS(short s) {
        return s;
    }

    private final AdjustProc FastS_ref = this::FastS;

    private short MultS(short s) {
        return (short) (s * mulS);
    }

    private final AdjustProc MultS_ref = this::MultS;

    private short FastX(short x) {
        return (short) (x + OX);
    }

    private final AdjustProc FastX_ref = this::FastX;

    private short FastY(short y) {
        return (short) (y + OY);
    }

    private final AdjustProc FastY_ref = this::FastY;

    private short MultX(short x) {
        return (short) (x * mulS + OX);
    }

    private final AdjustProc MultX_ref = this::MultX;

    private short MultY(short y) {
        return (short) (y * mulS + OY);
    }

    private final AdjustProc MultY_ref = this::MultY;

    public void SetOrigin(short ox, short oy) {
        OX = (short) (ox * mulS);
        OY = (short) (oy * mulS);
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
            SetRGB((short) c, (short) 0, (short) 0, (short) 0);
            cycle[c].copyFrom(palette[c]);
            cpPos[c] = 0;
            dcpPos[c] = 0;
            cpReverse = new Runtime.RangeSet(Memory.SET16_r);
            dcpReverse = new Runtime.RangeSet(Memory.SET16_r);
        }
        CopyToDual();
        SetRGB((short) 0, (short) 0, (short) 0, (short) 0);
        SetRGB((short) 1, (short) 127, (short) 127, (short) 127);
        for (c = 8; c <= 15; c++) {
            f = c * 17;
            SetRGB((short) c, (short) f, (short) f, (short) f);
        }
        SetRGB((short) 2, (short) 255, (short) 255, (short) 255);
        CycleRGB((short) 2, (short) 40, (short) 127, (short) 255, (short) 255);
        SetRGB((short) 3, (short) 255, (short) 255, (short) 0);
        CycleRGB((short) 3, (short) 200, (short) 255, (short) 200, (short) 0);
        SetRGB((short) 4, (short) 255, (short) 0, (short) 0);
        SetRGB((short) 5, (short) 0, (short) 255, (short) 0);
        SetRGB((short) 6, (short) 0, (short) 0, (short) 255);
        SetRGB((short) 7, (short) 210, (short) 140, (short) 0);
        for (c = 0; c <= 15; c++) {
            SetTrans((short) c, (short) 255);
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
        castle = (short[][]) memory.AllocMem(Runtime.sizeOf(32512, short[][].class, 127, 128));
        checks.CheckMem(castle);
        dual = (short[][]) memory.AllocMem(Runtime.sizeOf(8192, short[][].class, 64, 64));
        checks.CheckMem(dual);
        mulS = 1;
        SetOrigin((short) 0, (short) 0);
        InitColors();
    }

    public void close() {
    }

}
