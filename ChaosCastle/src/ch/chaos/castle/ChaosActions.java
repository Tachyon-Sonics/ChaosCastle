package ch.chaos.castle;

import java.lang.Runnable;
import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.GameStat;
import ch.chaos.castle.ChaosBase.ObjFlags;
import ch.chaos.castle.ChaosBase.Stones;
import ch.chaos.castle.ChaosBase.Weapon;
import ch.chaos.castle.ChaosGraphics.Explosions;
import ch.chaos.library.Checks;
import ch.chaos.library.Clock;
import ch.chaos.library.Graphics;
import ch.chaos.library.Input;
import ch.chaos.library.Languages;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosActions {

    // Imports
    private final ChaosBase chaosBase;
    private final ChaosGraphics chaosGraphics;
    private final Checks checks;
    private final Clock clock;
    private final Graphics graphics;
    private final Input input;
    private final Languages languages;
    private final Memory memory;
    private final Trigo trigo;


    private ChaosActions() {
        instance = this; // Set early to handle circular dependencies
        chaosBase = ChaosBase.instance();
        chaosGraphics = ChaosGraphics.instance();
        checks = Checks.instance();
        clock = Clock.instance();
        graphics = Graphics.instance();
        input = Input.instance();
        languages = Languages.instance();
        memory = Memory.instance();
        trigo = Trigo.instance();
    }


    // CONST

    public static final int statPos = 0;
    public static final int moneyPos = 1;
    public static final int lifePos = 2;
    public static final int actionPos = 3;


    // TYPE

    @FunctionalInterface
    public static interface DoToPlayerProc { // PROCEDURE Type
        public void invoke(ChaosBase.Obj arg1, ChaosBase.Obj arg2);
    }


    // VAR

    @SuppressWarnings("unchecked")
    public Runtime.IRef<String>[] messages = new Runtime.IRef[4];
    public int[] priorities = new int[4];
    public ChaosBase.Obj[] msgObj = new ChaosBase.Obj[4];
    public ChaosBase.Obj nlObj = new ChaosBase.Obj();
    public Clock.TimePtr time;
    public int lastMouseX;
    public int lastMouseY;


    public Runtime.IRef<String>[] getMessages() {
        return this.messages;
    }

    public void setMessages(Runtime.IRef<String>[] messages) {
        this.messages = messages;
    }

    public int[] getPriorities() {
        return this.priorities;
    }

    public void setPriorities(int[] priorities) {
        this.priorities = priorities;
    }

    public ChaosBase.Obj[] getMsgObj() {
        return this.msgObj;
    }

    public void setMsgObj(ChaosBase.Obj[] msgObj) {
        this.msgObj = msgObj;
    }

    public ChaosBase.Obj getNlObj() {
        return this.nlObj;
    }

    public void setNlObj(ChaosBase.Obj nlObj) {
        this.nlObj = nlObj;
    }

    public Clock.TimePtr getTime() {
        return this.time;
    }

    public void setTime(Clock.TimePtr time) {
        this.time = time;
    }

    public int getLastMouseX() {
        return this.lastMouseX;
    }

    public void setLastMouseX(int lastMouseX) {
        this.lastMouseX = lastMouseX;
    }

    public int getLastMouseY() {
        return this.lastMouseY;
    }

    public void setLastMouseY(int lastMouseY) {
        this.lastMouseY = lastMouseY;
    }


    // VAR

    private boolean out;
    private ChaosBase.Obj leftObj /* POINTER */;
    private ChaosBase.Obj leftTail /* POINTER */;


    public boolean isOut() {
        return this.out;
    }

    public void setOut(boolean out) {
        this.out = out;
    }

    public ChaosBase.Obj getLeftObj() {
        return this.leftObj;
    }

    public void setLeftObj(ChaosBase.Obj leftObj) {
        this.leftObj = leftObj;
    }

    public ChaosBase.Obj getLeftTail() {
        return this.leftTail;
    }

    public void setLeftTail(ChaosBase.Obj leftTail) {
        this.leftTail = leftTail;
    }


    // PROCEDURE

    public void PopMessage(Runtime.IRef<String> msg, int pos, int pri) {
        // VAR
        ChaosBase.Obj obj = null;
        int yInP = 0;
        int xPos = 0;
        int yPos = 0;
        int ySize = 0;

        if (pri >= priorities[pos]) {
            if (msgObj[pos] != null) {
                chaosBase.DisposeObj(msgObj[pos]);
                msgObj[pos] = null;
            }
            priorities[pos] = pri;
            ySize = chaosGraphics.H.invoke(9);
            graphics.SetArea(chaosGraphics.shapeArea);
            graphics.SetTextSize(ySize);
            xPos = (chaosGraphics.W.invoke(ChaosGraphics.PW) - graphics.TextWidth(msg)) / 2;
            if (xPos < 0)
                xPos = xPos * 2;
            yPos = pos * 10;
            yInP = (yPos + ((ChaosGraphics.PH - 40) / 2));
            yPos = chaosGraphics.H.invoke(yPos + 256);
            graphics.SetPen(0);
            graphics.SetCopyMode(Graphics.cmCopy);
            graphics.FillRect(0, yPos, chaosGraphics.W.invoke(ChaosGraphics.PW), yPos + ySize + chaosGraphics.mulS);
            graphics.SetCopyMode(Graphics.cmTrans);
            if (chaosGraphics.color)
                graphics.SetPen(4);
            else
                graphics.SetPen(1);
            graphics.SetTextPos(xPos, yPos + chaosGraphics.mulS);
            graphics.DrawText(msg);
            if (chaosGraphics.color)
                graphics.SetPen(3);
            graphics.SetTextPos(xPos + chaosGraphics.mulS, yPos);
            graphics.DrawText(msg);
            if (chaosGraphics.color)
                graphics.SetPen(7);
            else
                graphics.SetPen(0);
            graphics.SetTextPos(xPos, yPos);
            graphics.DrawText(msg);
            graphics.SetArea(chaosGraphics.maskArea);
            graphics.SetPen(0);
            graphics.SetCopyMode(Graphics.cmCopy);
            graphics.FillRect(0, yPos, chaosGraphics.W.invoke(ChaosGraphics.PW), yPos + ySize + chaosGraphics.mulS);
            graphics.SetPen(1);
            graphics.SetCopyMode(Graphics.cmTrans);
            graphics.SetTextSize(ySize);
            graphics.SetTextPos(xPos, yPos + chaosGraphics.mulS);
            graphics.DrawText(msg);
            graphics.SetTextPos(xPos + chaosGraphics.mulS, yPos);
            graphics.DrawText(msg);
            graphics.SetTextPos(xPos, yPos);
            graphics.DrawText(msg);
            graphics.SetArea(chaosGraphics.mainArea);
            obj = CreateObj(Anims.DEAD, ChaosBase.Message, chaosGraphics.backpx + ChaosGraphics.PW / 2, chaosGraphics.backpy + yInP + 5, yInP, 1);
            if (obj != nlObj) {
                msgObj[pos] = obj;
                obj.moveSeq = pri * 256;
                obj.shapeSeq = pos;
                SetObjLoc(obj, 0, 256 + 10 * pos, ChaosGraphics.PW, 0);
            }
        }
    }

    public void ZoomMessage(Runtime.IRef<String> msg, int p1, int p2, int p3) {
        // CONST
        final int MX = ChaosGraphics.SW / 2;
        final int MY = ChaosGraphics.SH / 2;

        // VAR
        long delay = 0L;
        int width = 0;
        int sx = 0;
        int sy = 0;
        int c = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        int f = 0;

        if (!chaosGraphics.color) {
            p1 = 0;
            p2 = 1;
            p3 = 1;
        }
        graphics.SetArea(chaosGraphics.buffArea);
        graphics.SetCopyMode(Graphics.cmTrans);
        graphics.SetPen(0);
        graphics.FillRect(0, 0, chaosGraphics.W.invoke(ChaosGraphics.SW), chaosGraphics.H.invoke(ChaosGraphics.SH));
        graphics.SetTextSize(27);
        width = graphics.TextWidth(msg) + chaosGraphics.mulS;
        graphics.SetTextPos(chaosGraphics.mulS, chaosGraphics.mulS);
        graphics.SetPen(p3);
        graphics.DrawText(msg);
        graphics.SetTextPos(0, 0);
        graphics.SetPen(p2);
        graphics.DrawText(msg);
        graphics.SetTextPos(chaosGraphics.mulS, 0);
        graphics.SetPen(p1);
        graphics.DrawText(msg);
        graphics.SetCopyMode(Graphics.cmCopy);
        graphics.SetArea(chaosGraphics.mainArea);
        graphics.SetBuffer(true, true);
        graphics.SetCopyMode(Graphics.cmCopy);
        graphics.SetPen(0);
        chaosGraphics.SetOrigin(0, 0);
        graphics.FillRect(0, 0, chaosGraphics.W.invoke(ChaosGraphics.SW), chaosGraphics.H.invoke(ChaosGraphics.SH));
        clock.StartTime(time);
        while (true) {
            delay = clock.GetTime(time);
            if (delay >= 512)
                delay = 512;
            f = (int) (256 - delay / 2);
            sy = (int) (delay * delay / 2048 + 1);
            if (sy >= 120)
                break;
            if (sy > 80)
                sx = sy / 2 * width / 15;
            else
                sx = sy * width / 30;
            graphics.ScaleRect(chaosGraphics.buffArea, 0, 0, width, 30, chaosGraphics.W.invoke(MX - sx), chaosGraphics.H.invoke(MY - sy), chaosGraphics.W.invoke(MX + sx), chaosGraphics.H.invoke(MY + sy));
            if (chaosGraphics.color) {
                for (c = 0; c <= 7; c++) {
                    { // WITH
                        ChaosGraphics.Palette _palette = chaosGraphics.palette[c];
                        r = _palette.red;
                        g = _palette.green;
                        b = _palette.blue;
                    }
                    graphics.SetPalette(c, r * f / 256, g * f / 256, b * f / 256);
                }
            }
            graphics.SwitchArea();
        }
        out = true;
        FadeOut();
    }

    public void AddPt(int count) {
        chaosBase.addpt += count;
    }

    public void NextStage() {
        // VAR
        Runtime.Ref<String> buffer = new Runtime.Ref<>("");
        Runtime.IRef<String> msg = null;
        int c = 0;

        chaosBase.stages--;
        if (chaosBase.stages == 0) {
            msg = Runtime.castToRef(languages.ADL("PMM (Post Mortem Map) enabled"), String.class);
        } else {
            msg = Runtime.castToRef(languages.ADL("! # stages left before PMM"), String.class);
            memory.CopyStr(msg, buffer, Runtime.sizeOf(60, String.class));
            c = 0;
            while ((Runtime.getChar(buffer, c) != '#') && (Runtime.getChar(buffer, c) != ((char) 0))) {
                c++;
            }
            if (Runtime.getChar(buffer, c) == '#')
                Runtime.setChar(buffer, c, (char) (48 + chaosBase.stages));
            msg = buffer;
        }
        PopMessage(msg, statPos, 6);
    }

    public ChaosBase.Obj CreateObj(Anims kind, int subKind, int nx, int ny, int nStat, int nLife) {
        // VAR
        ChaosBase.Obj obj = null;

        obj = chaosBase.NewObj(kind, subKind);
        if (obj != null) {
            obj.x = nx;
            obj.x = obj.x * ChaosBase.Frac;
            obj.y = ny;
            obj.y = obj.y * ChaosBase.Frac;
            obj.midx = obj.x;
            obj.midy = obj.y;
            obj.vx = 0;
            obj.vy = 0;
            obj.dvx = 0;
            obj.dvy = 0;
            obj.ax = 0;
            obj.ay = 0;
            obj.cx = 0;
            obj.cy = 0;
            obj.temperature = 0;
            obj.width = 0;
            obj.height = 0;
            obj.flags = EnumSet.noneOf(ObjFlags.class);
            if (chaosBase.gameStat == GameStat.Playing)
                obj.flags.add(ObjFlags.nested);
            if (obj.attr.basicType != BasicTypes.NotBase)
                chaosBase.basics[obj.attr.basicType.ordinal() - BasicTypes.Bonus.ordinal()].total++;
            obj.life = nLife;
            obj.stat = nStat;
            if (obj.attr.Reset != null)
                obj.attr.Reset.invoke(obj);
        } else {
            obj = nlObj;
        }
        return obj;
    }

    public void SetObjXY(ChaosBase.Obj obj, long nx, long ny) {
        obj.x = nx * ChaosBase.Frac;
        obj.y = ny * ChaosBase.Frac;
        obj.midx = obj.x;
        obj.midy = obj.y;
    }

    public void SetObjVXY(ChaosBase.Obj obj, int nvx, int nvy) {
        obj.vx = nvx;
        obj.dvx = obj.vx;
        obj.vy = nvy;
        obj.dvy = obj.vy;
    }

    public void SetObjAXY(ChaosBase.Obj obj, int nax, int nay) {
        obj.ax = nax;
        obj.ay = nay;
        obj.dvx = 0;
        obj.dvy = 0;
    }

    public void SetObjPos(ChaosBase.Obj obj, int nPosX, int nPosY) {
        obj.posX = nPosX * chaosGraphics.mulS;
        obj.posY = nPosY * chaosGraphics.mulS;
    }

    public void SetObjLoc(ChaosBase.Obj obj, int nPosX, int nPosY, int nWidth, int nHeight) {
        // VAR
        long z = 0L;
        int temp = 0;

        obj.posX = nPosX * chaosGraphics.mulS;
        obj.posY = nPosY * chaosGraphics.mulS;
        temp = nWidth * chaosGraphics.mulS;
        if (obj.width != temp) {
            z = obj.cx;
            obj.width = temp;
            obj.cx = nWidth / 2;
            z -= obj.cx;
            z = z * ChaosBase.Frac;
            obj.x += z;
            obj.midx += z;
        }
        temp = nHeight * chaosGraphics.mulS;
        if (obj.height != temp) {
            z = obj.cy;
            obj.height = temp;
            obj.cy = nHeight / 2;
            z -= obj.cy;
            z = z * ChaosBase.Frac;
            obj.y += z;
            obj.midy += z;
        }
    }

    public void SetObjRect(ChaosBase.Obj obj, long nLeft, long nTop, long nRight, long nBottom) {
        obj.left = nLeft * ChaosBase.Frac;
        obj.top = nTop * ChaosBase.Frac;
        obj.right = nRight * ChaosBase.Frac;
        obj.bottom = nBottom * ChaosBase.Frac;
    }

    public void GetCenter(ChaosBase.Obj obj, /* VAR */ Runtime.IRef<Integer> px, /* VAR */ Runtime.IRef<Integer> py) {
        px.set((int) (obj.x / ChaosBase.Frac));
        px.inc(obj.cx);
        py.set((int) (obj.y / ChaosBase.Frac));
        py.inc(obj.cy);
    }

    public void Boum(ChaosBase.Obj obj, EnumSet<Stones> subKinds, int style, int cnt, int skcnt) {
        // CONST
        final int SV = 4;
        final int MAXSTONES = ((1 << 8) - 1) /* MAX(SHORTCARD) */ - 64;

        // VAR
        int flames = 0;
        int stones = 0;
        int c = 0;
        Stones subK = Stones.stC26;
        int angle = 0;
        int step = 0;
        ChaosBase.Obj newObj = null;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int nvx = 0;
        int nvy = 0;
        int nax = 0;
        int nay = 0;

        flames = cnt / ChaosBase.FlameMult;
        stones = cnt % ChaosBase.FlameMult;
        if (chaosGraphics.explosions == Explosions.Low)
            stones = stones / 8;
        else if (chaosGraphics.explosions == Explosions.High)
            stones = stones * 2;
        GetCenter(obj, px, py);
        if (flames > 0) {
            flames--;
            nvx = obj.vx;
            nvy = obj.vy;
            newObj = CreateObj(Anims.STONE, 1, px.get(), py.get(), 0, 1);
            newObj.vx = nvx;
            newObj.vy = nvy;
            newObj.shapeSeq = 9;
            newObj.moveSeq = 18;
        }
        if (flames > 0) {
            angle = trigo.RND() % 360;
            step = 360 / flames;
            do {
                flames--;
                nvx = trigo.COS(angle);
                nvy = trigo.SIN(angle);
                nvx += obj.vx;
                nvy += obj.vy;
                newObj = CreateObj(Anims.STONE, 1, px.get(), py.get(), 0, 1);
                newObj.vx = nvx;
                newObj.vy = nvy;
                newObj.shapeSeq = 6;
                newObj.moveSeq = 12;
                angle += step;
            } while (flames != 0);
        }
        angle = trigo.RND() % 360;
        if (stones > 0)
            step = 360 / stones;
        while (stones > 0) {
            stones--;
            c = trigo.RND() % skcnt;
            subK = Stones.stC26 /* MIN(Stones) */;
            while (true) {
                while (!subKinds.contains(subK)) {
                    subK = Runtime.next(subK);
                }
                if (c == 0)
                    break;
                subK = Runtime.next(subK);
                c--;
            }
            switch (style) {
                case ChaosBase.slowStyle -> {
                    nvx = trigo.RND() % 512;
                    nvx -= 256;
                    nvx += obj.vx / 2;
                    nvy = trigo.RND() % 512;
                    nvy -= 256;
                    nvy += obj.vy / 2;
                    nax = 0;
                    nay = 0;
                }
                case ChaosBase.gravityStyle -> {
                    nvx = trigo.RND() % 1024;
                    nvx -= 512;
                    nvx += obj.vx / 2;
                    nvy = trigo.RND() % 1024;
                    nvy -= 896;
                    nvy += obj.vy / 2;
                    nax = 0;
                    nay = 16;
                }
                case ChaosBase.returnStyle -> {
                    nvx = trigo.COS(angle) * SV;
                    nvy = trigo.SIN(angle) * SV;
                    nax = -(nvx / (ChaosBase.Period / 5));
                    nay = -(nvy / (ChaosBase.Period / 5));
                    angle += step;
                }
                case ChaosBase.fastStyle -> {
                    nvx = trigo.RND() % 2048;
                    nvx -= 1024;
                    nvx += obj.vx / 2;
                    nvy = trigo.RND() % 2048;
                    nvy -= 1024;
                    nvy += obj.vy / 2;
                    nax = 0;
                    nay = 0;
                }
                default -> throw new RuntimeException("Unhandled CASE value " + style);
            }
            if (chaosBase.nbObj < MAXSTONES) {
                newObj = CreateObj(Anims.STONE, 0, px.get(), py.get(), subK.ordinal(), 1);
                newObj.vx = nvx;
                newObj.vy = nvy;
                newObj.ax = nax;
                newObj.ay = nay;
            }
        }
    }

    public void Die(ChaosBase.Obj obj) {
        // VAR
        ChaosBase.Obj flame = null;
        int tmp = 0;
        int maxpower = 0;
        int power = 0;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int sin = 0;
        int cos = 0;
        int rnd = 0;
        int angle = 0;

        obj.life = 0;
        { // WITH
            ChaosBase.ObjAttr _objAttr = obj.attr;
            if (_objAttr.Die != null)
                _objAttr.Die.invoke(obj);
            if (obj.life != 0)
                return;
            if (_objAttr.basicType != BasicTypes.NotBase)
                chaosBase.basics[_objAttr.basicType.ordinal() - BasicTypes.Bonus.ordinal()].done++;
            tmp = _objAttr.heatSpeed;
            Boum(obj, _objAttr.dieStKinds, _objAttr.dieStStyle, _objAttr.dieStone, _objAttr.dieSKCount);
        }
        if (tmp > 0) {
            GetCenter(obj, px, py);
            if (obj.temperature > ChaosBase.Warm)
                tmp = obj.temperature / 2048 + 1;
            else
                tmp = 0;
            maxpower = tmp / 2 + 1;
            while (tmp > 0) {
                angle = trigo.RND() % 360;
                cos = trigo.COS(angle);
                sin = trigo.SIN(angle);
                rnd = trigo.RND() % 16;
                sin = sin * rnd / 8 + obj.vy / 2;
                cos = cos * rnd / 8 + obj.vx / 2;
                power = trigo.RND() % maxpower + 1;
                flame = CreateObj(Anims.WEAPON, Weapon.FIRE.ordinal(), px.get(), py.get(), 0, power);
                SetObjVXY(flame, cos, sin);
                tmp--;
            }
        }
        obj.width = 0;
        obj.height = 0;
        chaosBase.ConvertObj(obj, Anims.DEAD, ChaosBase.DeadObj);
    }

    public void DecLife(ChaosBase.Obj obj, /* VAR */ Runtime.IRef<Integer> hit, /* VAR */ Runtime.IRef<Integer> fire) {
        // VAR
        int subHit = 0;
        int subFire = 0;

        subHit = hit.get();
        subFire = fire.get();
        if (subHit > obj.hitSubLife)
            subHit = obj.hitSubLife;
        if (subFire > obj.fireSubLife)
            subFire = obj.fireSubLife;
        if (subHit > obj.life)
            subHit = obj.life;
        obj.life -= subHit;
        if (subFire > obj.life)
            subFire = obj.life;
        obj.life -= subFire;
        subHit = (subHit * 3 + hit.get() + 3) / 4;
        hit.dec(subHit);
        subFire = (subFire * 3 + fire.get() + 3) / 4;
        fire.dec(subFire);
    }

    public void Aie(ChaosBase.Obj victim, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        // VAR
        int dhit = 0;
        int dfire = 0;
        int ttaie = 0;
        int sw = 0;
        int vw = 0;
        int stones = 0;

        dhit = hit.get();
        dfire = fire.get();
        ttaie = dhit + dfire;
        if ((victim.hitSubLife == 0) && (victim.fireSubLife == 0))
            return;
        sw = src.attr.weight;
        vw = victim.attr.weight;
        if (sw + vw != 0) {
            victim.vx = (victim.vx / 32 * vw + src.vx / 32 * sw) / (sw + vw) * 32;
            victim.vy = (victim.vy / 32 * vw + src.vy / 32 * sw) / (sw + vw) * 32;
        }
        if ((src.kind == Anims.WEAPON) && !((src.subKind == Weapon.FIRE.ordinal()) && (src.flags.contains(ObjFlags.nested))))
            chaosBase.shoot.done++;
        if (ttaie == 0)
            return;
        if (victim.attr.Aie != null)
            victim.attr.Aie.invoke(victim, src, hit, fire);
        else
            DecLife(victim, hit, fire);
        ttaie -= hit.get() + fire.get();
        if ((victim.life == 0) && (victim.hitSubLife + victim.fireSubLife > 0)) {
            Die(victim);
            return;
        }
        dfire = victim.attr.heatSpeed * dfire;
        if (dfire < ChaosBase.MaxHot / 16)
            victim.temperature += dfire * 16;
        else
            victim.temperature = ChaosBase.MaxHot;
        if (victim.temperature >= ChaosBase.MaxHot)
            victim.temperature = ChaosBase.MaxHot;
        dhit = victim.attr.refreshSpeed * dhit;
        if (dhit >= ChaosBase.MaxHot / 16)
            dhit = ChaosBase.MaxHot;
        else
            dhit = dhit * 16;
        if (victim.temperature < dhit)
            victim.temperature = 0;
        else
            victim.temperature -= dhit;
        stones = victim.attr.aieStone;
        if (ttaie < 16)
            stones -= (stones % ChaosBase.FlameMult) * (16 - ttaie) / 16;
        if (ttaie <= 4)
            stones = stones % ChaosBase.FlameMult;
        Boum(victim, victim.attr.aieStKinds, victim.attr.aieStStyle, stones, victim.attr.aieSKCount);
    }

    public final ChaosBase.AieProc Aie_ref = this::Aie;

    public boolean Collision(ChaosBase.Obj obj1, ChaosBase.Obj obj2) {
        // VAR
        long px = 0L;
        long py = 0L;
        long mx = 0L;
        long my = 0L;
        long w = 0L;
        long h = 0L;

        px = obj1.x + obj1.left;
        py = obj1.y + obj1.top;
        mx = obj1.midx + obj1.left;
        my = obj1.midy + obj1.top;
        w = obj1.right - obj1.left;
        h = obj1.bottom - obj1.top;
        return (((px > obj2.x + obj2.left - w) && (px < obj2.x + obj2.right) && (py > obj2.y + obj2.top - h) && (py < obj2.y + obj2.bottom)) || ((chaosBase.step > 30) && (mx > obj2.midx + obj2.left - w) && (mx < obj2.midx + obj2.right) && (my > obj2.midy + obj2.top - h) && (my < obj2.midy + obj2.bottom)));
    }

    public void DoCollision(ChaosBase.Obj obj, EnumSet<Anims> victims, ChaosBase.AieProc Do, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        // VAR
        long px = 0L;
        long py = 0L;
        long mx = 0L;
        long my = 0L;
        long w = 0L;
        long h = 0L;
        ChaosBase.Obj victim = null;
        ChaosBase.Obj next = null;
        ChaosBase.Obj tail = null;
        Anims a = Anims.PLAYER;

        px = obj.x + obj.left;
        py = obj.y + obj.top;
        mx = obj.midx + obj.left;
        my = obj.midy + obj.top;
        w = obj.right - obj.left;
        h = obj.bottom - obj.top;
        for (int _a = 0; _a <= Anims.MACHINE.ordinal(); _a++) {
            a = Anims.values()[_a];
            if (victims.contains(a)) {
                victim = (ChaosBase.Obj) memory.First(chaosBase.animList[a.ordinal()]);
                tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[a.ordinal()]);
                while (victim != tail) {
                    next = (ChaosBase.Obj) memory.Next(victim.animNode);
                    if ((obj != victim) && (((px > victim.x + victim.left - w) && (px < victim.x + victim.right) && (py > victim.y + victim.top - h) && (py < victim.y + victim.bottom)) || ((chaosBase.step > 30) && (mx > victim.midx + victim.left - w) && (mx < victim.midx + victim.right) && (my > victim.midy + victim.top - h) && (my < victim.midy + victim.bottom))))
                        Do.invoke(victim, obj, hit, fire);
                    victim = next;
                }
            }
        }
    }

    public void PlayerCollision(ChaosBase.Obj obj, /* VAR+WRT */ Runtime.IRef<Integer> hit) {
        // VAR
        ChaosBase.Obj victim = null;
        ChaosBase.Obj next = null;
        ChaosBase.Obj tail = null;
        Runtime.Ref<Integer> fire = new Runtime.Ref<>(0);

        fire.set(0);
        victim = (ChaosBase.Obj) memory.First(chaosBase.animList[Anims.PLAYER.ordinal()]);
        tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[Anims.PLAYER.ordinal()]);
        while (victim != tail) {
            next = (ChaosBase.Obj) memory.Next(victim.animNode);
            if (Collision(obj, victim))
                Aie(victim, obj, hit, fire);
            victim = next;
        }
    }

    public void DoToPlayer(ChaosBase.Obj obj, DoToPlayerProc Do) {
        // VAR
        ChaosBase.Obj player = null;
        ChaosBase.Obj tail = null;
        ChaosBase.Obj next = null;

        player = (ChaosBase.Obj) memory.First(chaosBase.animList[Anims.PLAYER.ordinal()]);
        tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[Anims.PLAYER.ordinal()]);
        while (player != tail) {
            next = (ChaosBase.Obj) memory.Next(player.animNode);
            if (Collision(obj, player)) {
                Do.invoke(player, obj);
                return;
            }
            player = next;
        }
    }

    public boolean OutOfScreen(ChaosBase.Obj obj) {
        // CONST
        final int DX = ChaosGraphics.PW / 2;
        final int DY = ChaosGraphics.PH / 2;

        // VAR
        int px = 0;
        int py = 0;

        px = (int) (obj.x / ChaosBase.Frac);
        px -= chaosGraphics.backpx;
        py = (int) (obj.y / ChaosBase.Frac);
        py -= chaosGraphics.backpy;
        return (px + obj.width <= -DX) || (py + obj.height <= -DY) || (px >= ChaosGraphics.PW + DX) || (py >= ChaosGraphics.PH + DY);
    }

    public boolean OutOfBounds(ChaosBase.Obj obj) {
        // VAR
        long gw = 0L;
        long gh = 0L;

        gw = chaosGraphics.gameWidth;
        gw = gw * ChaosBase.Frac;
        gh = chaosGraphics.gameHeight;
        gh = gh * ChaosBase.Frac;
        return (obj.x + obj.left >= gw) || (obj.y + obj.top >= gh) || (obj.x + obj.right < 0) || (obj.y + obj.bottom < 0);
    }

    public void AvoidBounds(ChaosBase.Obj obj, int _return) {
        // VAR
        long gw = 0L;
        long gh = 0L;

        gw = chaosGraphics.gameWidth;
        gw = gw * ChaosBase.Frac;
        gh = chaosGraphics.gameHeight;
        gh = gh * ChaosBase.Frac;
        if (obj.x + obj.left < 0) {
            obj.x = -obj.left;
            obj.dvx = Math.abs(obj.dvx) * _return / 4;
            obj.vx = Math.abs(obj.vx) * _return / 4;
        } else if ((obj.x + obj.right > gw)) {
            obj.x = gw - obj.right;
            obj.dvx = -(Math.abs(obj.dvx) * _return / 4);
            obj.vx = -(Math.abs(obj.vx) * _return / 4);
        }
        if (obj.y + obj.top < 0) {
            obj.y = -obj.top;
            obj.dvy = Math.abs(obj.dvy) * _return / 4;
            obj.vy = Math.abs(obj.vy) * _return / 4;
        } else if ((obj.y + obj.bottom > gh)) {
            obj.y = gh - obj.bottom;
            obj.dvy = -(Math.abs(obj.dvy) * _return / 4);
            obj.vy = -(Math.abs(obj.vy) * _return / 4);
        }
    }

    private void CheckRect(ChaosBase.Obj obj, long tx, long ty, /* VAR */ Runtime.IRef<Integer> rx, /* VAR */ Runtime.IRef<Integer> ry, /* VAR */ Runtime.IRef<Integer> cr) {
        // VAR
        int px = 0;
        int py = 0;
        int dx = 0;
        int dy = 0;
        int corner = 0;

        dx = 0;
        dy = 0;
        corner = 0;
        py = (int) ((ty + obj.top) / ChaosBase.Frac);
        if ((py >= 0) && (py < chaosGraphics.gameHeight)) {
            px = (int) ((tx + obj.left) / ChaosBase.Frac);
            if ((px >= 0) && (px < chaosGraphics.gameWidth) && (chaosGraphics.castle[py / ChaosGraphics.BH][px / ChaosGraphics.BW] >= ChaosGraphics.NbBackground)) {
                dx++;
                dy++;
                corner++;
            }
            px = (int) ((tx + obj.right - ChaosBase.Frac) / ChaosBase.Frac);
            if ((px >= 0) && (px < chaosGraphics.gameWidth) && (chaosGraphics.castle[py / ChaosGraphics.BH][px / ChaosGraphics.BW] >= ChaosGraphics.NbBackground)) {
                dx--;
                dy++;
                corner++;
            }
        }
        py = (int) ((ty + obj.bottom - ChaosBase.Frac) / ChaosBase.Frac);
        if ((py >= 0) && (py < chaosGraphics.gameHeight)) {
            px = (int) ((tx + obj.left) / ChaosBase.Frac);
            if ((px >= 0) && (px < chaosGraphics.gameWidth) && (chaosGraphics.castle[py / ChaosGraphics.BH][px / ChaosGraphics.BW] >= ChaosGraphics.NbBackground)) {
                dx++;
                dy--;
                corner++;
            }
            px = (int) ((tx + obj.right - ChaosBase.Frac) / ChaosBase.Frac);
            if ((px >= 0) && (px < chaosGraphics.gameWidth) && (chaosGraphics.castle[py / ChaosGraphics.BH][px / ChaosGraphics.BW] >= ChaosGraphics.NbBackground)) {
                dx--;
                dy--;
                corner++;
            }
        }
        rx.set(dx);
        ry.set(dy);
        cr.set(corner);
    }

    public boolean InBackground(ChaosBase.Obj obj) {
        // VAR
        Runtime.Ref<Integer> dx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> dy = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> corner = new Runtime.Ref<>(0);

        CheckRect(obj, obj.x, obj.y, dx, dy, corner);
        return corner.get() > 0;
    }

    private void AvoidRect(ChaosBase.Obj obj, long tx, long ty, int _return, /* VAR+WRT */ Runtime.IRef<Integer> corner) {
        // VAR
        long oldx = 0L;
        long oldy = 0L;
        int olddvx = 0;
        int olddvy = 0;
        int oldvx = 0;
        int oldvy = 0;
        Runtime.Ref<Integer> dx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> dy = new Runtime.Ref<>(0);

        CheckRect(obj, tx, ty, dx, dy, corner);
        oldx = obj.x;
        oldy = obj.y;
        oldvx = obj.vx;
        oldvy = obj.vy;
        olddvx = obj.dvx;
        olddvy = obj.dvy;
        if (dx.get() > 0) {
            obj.x = ((tx + obj.left) / ChaosBase.Frac / ChaosGraphics.BW + 1) * ChaosGraphics.BW * ChaosBase.Frac - obj.left;
            obj.vx = Math.abs(obj.vx) * _return / 4;
            obj.dvx = Math.abs(obj.dvx) * _return / 4;
        } else if (dx.get() < 0) {
            obj.x = ((tx + obj.right - 1) / ChaosBase.Frac / ChaosGraphics.BW * ChaosGraphics.BW + 1) * ChaosBase.Frac - obj.right - 1;
            obj.vx = -(Math.abs(obj.vx) * _return / 4);
            obj.dvx = -(Math.abs(obj.dvx) * _return / 4);
        }
        if (dy.get() > 0) {
            obj.y = ((ty + obj.top) / ChaosBase.Frac / ChaosGraphics.BH + 1) * ChaosGraphics.BH * ChaosBase.Frac - obj.top;
            obj.vy = Math.abs(obj.vy) * _return / 4;
            obj.dvy = Math.abs(obj.dvy) * _return / 4;
        } else if (dy.get() < 0) {
            obj.y = ((ty + obj.bottom - 1) / ChaosBase.Frac / ChaosGraphics.BH * ChaosGraphics.BH + 1) * ChaosBase.Frac - obj.bottom - 1;
            obj.vy = -(Math.abs(obj.vy) * _return / 4);
            obj.dvy = -(Math.abs(obj.dvy) * _return / 4);
        }
        if ((corner.get() == 1) && (oldvx != 0) && (oldvy != 0)) {
            if (Math.abs(obj.x - oldx) < Math.abs(obj.y - oldy)) {
                obj.y = oldy;
                obj.vy = oldvy;
                obj.dvy = olddvy;
            } else {
                obj.x = oldx;
                obj.vx = oldvx;
                obj.dvx = olddvx;
            }
        }
    }

    public void AvoidBackground(ChaosBase.Obj obj, int _return) {
        // VAR
        Runtime.Ref<Integer> corner = new Runtime.Ref<>(0);

        corner.set(0);
        if (chaosBase.step > 30)
            AvoidRect(obj, obj.midx, obj.midy, _return, corner);
        if (corner.get() == 0)
            AvoidRect(obj, obj.x, obj.y, _return, corner);
    }

    public void AvoidAnims(ChaosBase.Obj obj, EnumSet<Anims> anims) {
        // VAR
        ChaosBase.Obj aobj = null;
        ChaosBase.Obj tail = null;
        Anims k = Anims.PLAYER;
        int dx = 0;
        int dy = 0;
        int dv = 0;
        int dh = 0;
        int dist = 0;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> ax = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> ay = new Runtime.Ref<>(0);
        int vx = 0;
        int vy = 0;
        long svx = 0L;
        long svy = 0L;
        long tt = 0L;

        tt = 0;
        svx = 0;
        svy = 0;
        GetCenter(obj, px, py);
        for (int _k = 0; _k < Anims.values().length; _k++) {
            k = Anims.values()[_k];
            if (anims.contains(k)) {
                aobj = (ChaosBase.Obj) memory.First(chaosBase.animList[k.ordinal()]);
                tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[k.ordinal()]);
                while (aobj != tail) {
                    GetCenter(aobj, ax, ay);
                    dy = ay.get() - py.get();
                    dx = ax.get() - px.get();
                    dv = (int) Math.abs(dy);
                    dh = (int) Math.abs(dx);
                    if (dv > dh)
                        dist = dv;
                    else
                        dist = dh;
                    if ((dist < 60) && (aobj.vx / 16 * dx + aobj.vy / 16 * dy < 0)) {
                        vx = (aobj.vy * 3 / 2);
                        vy = -(aobj.vx * 3 / 2);
                        if (vy / 32 * dy + vx / 32 * dx > 0) {
                            vx = -vx;
                            vy = -vy;
                        }
                        vx += aobj.vx / 2;
                        vy += aobj.vy / 2;
                        svx += vx;
                        svy += vy;
                        tt++;
                    }
                    aobj = (ChaosBase.Obj) memory.Next(aobj.animNode);
                }
            }
        }
        if (tt > 0) {
            obj.dvx = (int) (svx / tt);
            obj.dvy = (int) (svy / tt);
            obj.ax = 0;
            obj.ay = 0;
        }
    }

    public void Gravity(ChaosBase.Obj obj, EnumSet<Anims> victims) {
        // VAR
        ChaosBase.Obj aobj = null;
        ChaosBase.Obj tail = null;
        int charge = 0;
        int istep = 0;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> rx = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> ry = new Runtime.Ref<>(0);
        int r = 0;
        int r2 = 0;
        Anims k = Anims.PLAYER;

        GetCenter(obj, px, py);
        istep = chaosBase.step;
        for (int _k = 0; _k < Anims.values().length; _k++) {
            k = Anims.values()[_k];
            if (victims.contains(k)) {
                aobj = (ChaosBase.Obj) memory.First(chaosBase.animList[k.ordinal()]);
                tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[k.ordinal()]);
                while (aobj != tail) {
                    GetCenter(aobj, rx, ry);
                    rx.dec(px.get());
                    ry.dec(py.get());
                    if ((obj != aobj) && (Math.abs(rx.get()) < 128) && (Math.abs(ry.get()) < 128)) {
                        r2 = rx.get() * rx.get() + ry.get() * ry.get();
                        if (r2 < 256)
                            r2 = 256;
                        r = trigo.SQRT(r2);
                        rx.set(rx.get() * 256 / r);
                        ry.set(ry.get() * 256 / r);
                        charge = -((aobj.attr.charge * obj.attr.charge) / 128);
                        if (chaosBase.water)
                            charge = charge / 2;
                        aobj.vx -= ((rx.get() * charge) / r2) * istep;
                        if (aobj.vx > 4096)
                            aobj.vx = 4096;
                        else if (aobj.vx < -4096)
                            aobj.vx = -4096;
                        aobj.vy -= ((ry.get() * charge) / r2) * istep;
                        if (aobj.vy > 4096)
                            aobj.vy = 4096;
                        else if (aobj.vy < -4096)
                            aobj.vy = -4096;
                    }
                    aobj = (ChaosBase.Obj) memory.Next(aobj.animNode);
                }
            }
        }
    }

    public void Burn(ChaosBase.Obj obj) {
        // VAR
        ChaosBase.Obj flame = null;
        int tmp = 0;
        int stp = 0;
        int size = 0;
        int delay = 0;
        int power = 0;
        int maxpower = 0;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int nx = 0;
        int ny = 0;
        int rnd = 0;
        int cos = 0;
        int sin = 0;
        int angle = 0;

        if ((obj.temperature < ChaosBase.Warm) || chaosBase.water)
            return;
        GetCenter(obj, px, py);
        size = (obj.width + obj.height) / (chaosGraphics.mulS * 4);
        tmp = (obj.temperature - ChaosBase.Warm) / 14;
        maxpower = tmp / 350 + 1;
        stp = chaosBase.step;
        while (stp > 0) {
            if (trigo.RND() < tmp) {
                angle = trigo.RND() % 360;
                cos = trigo.COS(angle);
                sin = trigo.SIN(angle);
                rnd = trigo.RND() % (size + 1);
                nx = px.get() + cos / 16 * rnd / 64;
                ny = py.get() + sin / 16 * rnd / 64;
                rnd = trigo.RND() % 16;
                sin = sin * rnd / 16 + obj.vy * 3 / 4;
                cos = cos * rnd / 16 + obj.vx * 3 / 4;
                power = trigo.RND() % maxpower + 1;
                delay = trigo.RND() % ChaosBase.Period;
                flame = CreateObj(Anims.WEAPON, Weapon.FIRE.ordinal(), nx, ny, delay, power);
                SetObjVXY(flame, cos, sin);
            }
            stp--;
        }
    }

    public void LimitSpeed(ChaosBase.Obj obj, int max) {
        // VAR
        int ovx = 0;
        int ovy = 0;
        int speed = 0;

        if ((Math.abs(obj.vx * 2) < max) && (Math.abs(obj.vy * 2) < max))
            return;
        ovx = (obj.vx + 32) / 64;
        ovy = (obj.vy + 32) / 64;
        speed = trigo.SQRT(ovx * ovx + ovy * ovy);
        if (speed * 64 > max) {
            max = max / 64;
            obj.vx = obj.vx / speed * max;
            obj.vy = obj.vy / speed * max;
        }
    }

    public void Leave(ChaosBase.Obj obj) {
        chaosBase.LeaveObj(obj);
    }

    public void CheckLeftObjs() {
        // VAR
        ChaosBase.Obj testObj = null;
        int cnt = 0;

        cnt = chaosBase.step;
        while (true) {
            if (leftObj == leftTail)
                leftObj = (ChaosBase.Obj) chaosBase.FirstObj(chaosBase.leftObjList);
            if (leftObj == leftTail)
                break;
            testObj = leftObj;
            leftObj = (ChaosBase.Obj) chaosBase.NextObj(leftObj.objNode);
            if (!OutOfScreen(testObj))
                chaosBase.RestartObj(testObj);
            else if (cnt > 0)
                cnt--;
            else
                break;
        }
    }

    public void FadeIn() {
        // VAR
        int lf = 0;
        int f = 0;
        int c = 0;

        if (!chaosGraphics.color || !out) {
            out = false;
            return;
        }
        input.RemEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eMENU));
        graphics.SetArea(chaosGraphics.mainArea);
        chaosGraphics.ResetCycle(true);
        for (c = 0; c <= 15; c++) {
            chaosGraphics.CycleRGB(c, 16, 0, 0, 0);
            chaosGraphics.DualCycleRGB(c, 16, 0, 0, 0);
        }
        clock.StartTime(time);
        out = false;
        lf = 0;
        for (f = 1; f <= 256; f++) {
            if (clock.WaitTime(time, 1) || (f == 256)) {
                chaosGraphics.AnimPalette(f - lf);
                lf = f;
                graphics.UpdateArea();
            }
        }
        input.AddEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eMENU));
    }

    public void FadeOut() {
        // VAR
        int lf = 0;
        int f = 0;
        int c = 0;

        input.SetBusyStat(Input.statBusy);
        graphics.SetArea(chaosGraphics.mainArea);
        if (chaosGraphics.color && !out) {
            input.RemEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eMENU));
            chaosGraphics.ResetCycle(false);
            for (c = 0; c <= 15; c++) {
                chaosGraphics.CycleRGB(c, 16, 0, 0, 0);
                chaosGraphics.DualCycleRGB(c, 16, 0, 0, 0);
            }
            clock.StartTime(time);
            lf = 256;
            for (f = 255; f >= 0; f -= 1) {
                if (clock.WaitTime(time, 1) || (f == 0)) {
                    chaosGraphics.AnimPalette(lf - f);
                    lf = f;
                    graphics.UpdateArea();
                }
            }
            input.AddEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eMENU));
        } else {
            graphics.SetBuffer(true, false);
            graphics.SetPen(0);
            graphics.FillRect(0, 0, chaosGraphics.W.invoke(ChaosGraphics.SW), chaosGraphics.H.invoke(ChaosGraphics.SH));
        }
        out = true;
    }

    public void WhiteFade() {
        // VAR
        int lf = 0;
        int f = 0;
        int c = 0;

        if (!chaosGraphics.color || !out) {
            out = false;
            return;
        }
        input.RemEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eMENU));
        graphics.SetArea(chaosGraphics.mainArea);
        chaosGraphics.ResetCycle(true);
        for (c = 0; c <= 15; c++) {
            chaosGraphics.CycleRGB(c, 16, 255, 255, 255);
        }
        clock.StartTime(time);
        out = false;
        for (f = 0; f <= 255; f += 3) {
            if (clock.WaitTime(time, 1) || (f == 255)) {
                for (c = 0; c <= 15; c++) {
                    graphics.SetPalette(c, f, f, f);
                }
                graphics.UpdateArea();
            }
        }
        lf = 256;
        for (f = 255; f >= 0; f -= 1) {
            if (clock.WaitTime(time, 2) || (f == 0)) {
                chaosGraphics.AnimPalette(lf - f);
                lf = f;
                graphics.UpdateArea();
            }
        }
        input.AddEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eMENU));
    }

    public void FadeFrom(int r, int g, int b) {
        // VAR
        int fr = 0;
        int fg = 0;
        int fb = 0;
        int nr = 0;
        int ng = 0;
        int nb = 0;
        int f = 0;
        int c = 0;

        input.SetBusyStat(Input.statBusy);
        if (!chaosGraphics.color)
            return;
        for (c = 0; c <= 15; c++) {
            chaosGraphics.SetTrans(c, 255);
        }
        fr = r;
        fg = g;
        fb = b;
        graphics.SetArea(chaosGraphics.mainArea);
        clock.StartTime(time);
        for (f = 1; f <= 256; f++) {
            if (clock.WaitTime(time, 1) || (f == 256)) {
                for (c = 0; c <= 7; c++) {
                    { // WITH
                        ChaosGraphics.Palette _palette = chaosGraphics.palette[c];
                        nr = _palette.red;
                        ng = _palette.green;
                        nb = _palette.blue;
                    }
                    graphics.SetPalette(c, nr * f / 256, ng * f / 256, nb * f / 256);
                }
                for (c = 8; c <= 15; c++) {
                    { // WITH
                        ChaosGraphics.Palette _palette = chaosGraphics.palette[c - 8];
                        nr = _palette.red;
                        ng = _palette.green;
                        nb = _palette.blue;
                    }
                    nr = nr * f + fr * (256 - f);
                    nb = nb * f + fb * (256 - f);
                    ng = ng * f + fg * (256 - f);
                    graphics.SetPalette(c, nr / 256, ng / 256, nb / 256);
                }
                graphics.UpdateArea();
            }
        }
    }

    public void UpdateXY(ChaosBase.Obj obj) {
        // VAR
        long oldx = 0L;
        long oldy = 0L;
        long mstep = 0L;
        int nax = 0;
        int nay = 0;
        int max = 0;
        int may = 0;
        int ndvx = 0;
        int ndvy = 0;
        int mvx = 0;
        int mvy = 0;
        int dstep = 0;
        int tocool = 0;
        int cstep = 0;

        mvx = obj.vx;
        mvy = obj.vy;
        if ((chaosBase.sleeper > 0) && (EnumSet.of(Anims.ALIEN2, Anims.ALIEN1).contains(obj.kind))) {
            ndvx = 0;
            ndvy = 0;
            nax = 0;
            nay = 0;
        } else {
            ndvx = obj.dvx;
            ndvy = obj.dvy;
            nax = obj.ax;
            nay = obj.ay;
        }
        mstep = chaosBase.step;
        cstep = chaosBase.step + obj.adelay;
        obj.adelay = cstep % 8;
        cstep = cstep / 8;
        tocool = cstep * obj.attr.coolSpeed;
        dstep = cstep;
        if ((tocool <= obj.temperature) && !chaosBase.water)
            obj.temperature -= tocool;
        else
            obj.temperature = 0;
        if ((nax == 0) && (nay == 0)) {
            max = obj.attr.inerty;
            if (chaosBase.water) {
                if ((trigo.SGN(ndvx) * trigo.SGN(obj.vx) <= 0) && (trigo.SGN(ndvy) * trigo.SGN(obj.vy) <= 0))
                    max = max * 2;
                else
                    max = max / 2;
            }
            if (chaosBase.snow)
                max = max / 3;
            may = max;
            ndvx -= obj.vx;
            ndvy -= obj.vy;
            if (ndvx > 0) {
                max = max * dstep;
                if (max > ndvx)
                    max = ndvx;
            } else {
                max = -(max * dstep);
                if (max < ndvx)
                    max = ndvx;
            }
            if (ndvy > 0) {
                may = may * dstep;
                if (may > ndvy)
                    may = ndvy;
            } else {
                may = -(may * dstep);
                if (may < ndvy)
                    may = ndvy;
            }
            obj.vx += max;
            obj.vy += may;
        } else {
            max = obj.ax;
            may = obj.ay;
            obj.vx += max * dstep;
            obj.dvx = obj.vx;
            obj.vy += may * dstep;
            obj.dvy = obj.vy;
        }
        if (obj.vx > 4096)
            obj.vx = 4096;
        else if (obj.vx < -4096)
            obj.vx = -4096;
        if (obj.vy > 4096)
            obj.vy = 4096;
        else if (obj.vy < -4096)
            obj.vy = -4096;
        mvx = (mvx + obj.vx) / 2;
        mvy = (mvy + obj.vy) / 2;
        oldx = obj.x;
        oldy = obj.y;
        if (chaosBase.water) {
            obj.x += mvx / 3 * mstep;
            obj.y += mvy / 3 * mstep;
        } else {
            obj.x += mvx / 2 * mstep;
            obj.y += mvy / 2 * mstep;
        }
        obj.midx = (obj.x + oldx) / 2;
        obj.midy = (obj.y + oldy) / 2;
    }

    public void NextFrame() {
        chaosBase.step = (int) (clock.GetTime(time) - chaosBase.lasttime);
        chaosBase.lasttime += chaosBase.step;
        if (chaosBase.step > 60)
            chaosBase.step = 60;
    }

    public void HotFlush() {
        chaosGraphics.UpdatePalette();
        chaosGraphics.RenderObjects();
        graphics.SetArea(chaosGraphics.mainArea);
        graphics.SetBuffer(true, false);
        input.AddEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eMENU, Input.eREFRESH));
        input.SetBusyStat(Input.statWaiting);
    }

    public void HotInit() {
        input.SetBusyStat(Input.statReady);
        input.RemEvents(new Runtime.RangeSet(Input.EventTypes).with(Input.eMENU, Input.eREFRESH));
        graphics.SetArea(chaosGraphics.mainArea);
        graphics.SetBuffer(true, true);
        chaosGraphics.UpdatePalette();
        if ((chaosBase.screenInverted > 0) && chaosGraphics.color) {
            graphics.SetPalette(0, 255, 255, 255);
            graphics.SetPalette(4, 255, 255, 255);
        }
        input.GetMouse(new Runtime.FieldRef<>(this::getLastMouseX, this::setLastMouseX), new Runtime.FieldRef<>(this::getLastMouseY, this::setLastMouseY));
        chaosBase.nextGunFireTime = ChaosBase.GunFireSpeed;
        leftTail = (ChaosBase.Obj) chaosBase.TailObj(chaosBase.leftObjList);
        leftObj = leftTail;
        chaosBase.lasttime = 0;
        chaosBase.step = ChaosBase.Period / 60;
        clock.StartTime(time);
    }

    private void Close() {
        clock.FreeTime(time);
    }

    private final Runnable Close_ref = this::Close;


    // Support

    private static ChaosActions instance;

    public static ChaosActions instance() {
        if (instance == null)
            new ChaosActions(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        time = clock.AllocTime(ChaosBase.Period);
        checks.CheckMemBool(time == clock.noTime);
        out = true;
        checks.AddTermProc(Close_ref);
    }

    public void close() {
    }

}
