package ch.chaos.castle;

import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.GameStat;
import ch.chaos.castle.ChaosBase.Stones;
import ch.chaos.castle.ChaosBase.Weapon;
import ch.chaos.castle.ChaosBase.Zone;
import ch.chaos.castle.ChaosSounds.SoundList;
import ch.chaos.library.Checks;
import ch.chaos.library.Clock;
import ch.chaos.library.Graphics;
import ch.chaos.library.Graphics.TextModes;
import ch.chaos.library.Input;
import ch.chaos.library.Input.Modifiers;
import ch.chaos.library.Languages;
import ch.chaos.library.Memory;
import ch.chaos.library.Registration;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosPlayer {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosGraphics chaosGraphics;
    private final ChaosInterface chaosInterface;
    private final ChaosSounds chaosSounds;
    private final ChaosWeapon chaosWeapon;
    private final Checks checks;
    private final Clock clock;
    private final Graphics graphics;
    private final Input input;
    private final Languages languages;
    private final Memory memory;
    private final Registration registration;


    private ChaosPlayer() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosInterface = ChaosInterface.instance();
        chaosSounds = ChaosSounds.instance();
        chaosWeapon = ChaosWeapon.instance();
        checks = Checks.instance();
        clock = Clock.instance();
        graphics = Graphics.instance();
        input = Input.instance();
        languages = Languages.instance();
        memory = Memory.instance();
        registration = Registration.instance();
    }


    // CONST

    private static final String AddMsg = "Choose a weapon";


    // TYPE

    private static enum Infos {
        TITLE,
        LEVEL,
        LIFE,
        SCORE,
        DOLLAR,
        STERLING;
    }


    // VAR

    private EnumSet<Weapon> wChanges = EnumSet.noneOf(Weapon.class);
    private EnumSet<Weapon> owChanges = EnumSet.noneOf(Weapon.class);
    private EnumSet<Weapon> nChanges = EnumSet.noneOf(Weapon.class);
    private EnumSet<Weapon> onChanges = EnumSet.noneOf(Weapon.class);
    private EnumSet<Infos> gChanges = EnumSet.noneOf(Infos.class);
    private EnumSet<Infos> ogChanges = EnumSet.noneOf(Infos.class);
    private int dMagnet;
    private int dInv;
    private int dSleeper;
    private int dAir;
    private int dFF;
    private int dMaxPower;
    private int dPlayerPower;
    private int[] oldMpx = new int[2];
    private int[] oldMpy = new int[2];
    private int mpx;
    private int mpy;
    private int infoBackPen;
    private ChaosSounds.Effect[] lifeEffectL = Runtime.initArray(new ChaosSounds.Effect[24]);
    private ChaosSounds.Effect[] lifeEffectR = Runtime.initArray(new ChaosSounds.Effect[24]);
    private ChaosSounds.Effect[] life0Effect = Runtime.initArray(new ChaosSounds.Effect[2]);
    private ChaosSounds.Effect[] life3Effect = Runtime.initArray(new ChaosSounds.Effect[3]);
    private ChaosSounds.Effect[] life2Effect = Runtime.initArray(new ChaosSounds.Effect[5]);
    private ChaosSounds.Effect[] mioEffect = Runtime.initArray(new ChaosSounds.Effect[5]);
    private ChaosSounds.Effect[] life1Effect = Runtime.initArray(new ChaosSounds.Effect[9]);
    private ChaosSounds.Effect[] moneyEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] cBulletEffectL = Runtime.initArray(new ChaosSounds.Effect[7]);
    private ChaosSounds.Effect[] cBulletEffectR = Runtime.initArray(new ChaosSounds.Effect[7]);
    private ChaosSounds.Effect[] bulletEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] cPowerEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] powerEffectL = Runtime.initArray(new ChaosSounds.Effect[12]);
    private ChaosSounds.Effect[] powerEffectR = Runtime.initArray(new ChaosSounds.Effect[13]);
    private ChaosSounds.Effect[] savedEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] dieEffects = Runtime.initArray(new ChaosSounds.Effect[1]);
    private int bulletToAdd;
    private int bombToAdd;
    private int powerToAdd;
    private boolean waitPause;
    private boolean pauseRequest;
    private long prevTime;
    private ChaosSounds.Effect[] aieEffects = Runtime.initArray(new ChaosSounds.Effect[1]);
    private ChaosSounds.Effect[] hitEffect = Runtime.initArray(new ChaosSounds.Effect[1]);
    private long oldtime;


    public EnumSet<Weapon> getWChanges() {
        return this.wChanges;
    }

    public void setWChanges(EnumSet<Weapon> wChanges) {
        this.wChanges = wChanges;
    }

    public EnumSet<Weapon> getOwChanges() {
        return this.owChanges;
    }

    public void setOwChanges(EnumSet<Weapon> owChanges) {
        this.owChanges = owChanges;
    }

    public EnumSet<Weapon> getNChanges() {
        return this.nChanges;
    }

    public void setNChanges(EnumSet<Weapon> nChanges) {
        this.nChanges = nChanges;
    }

    public EnumSet<Weapon> getOnChanges() {
        return this.onChanges;
    }

    public void setOnChanges(EnumSet<Weapon> onChanges) {
        this.onChanges = onChanges;
    }

    public EnumSet<Infos> getGChanges() {
        return this.gChanges;
    }

    public void setGChanges(EnumSet<Infos> gChanges) {
        this.gChanges = gChanges;
    }

    public EnumSet<Infos> getOgChanges() {
        return this.ogChanges;
    }

    public void setOgChanges(EnumSet<Infos> ogChanges) {
        this.ogChanges = ogChanges;
    }

    public int getDMagnet() {
        return this.dMagnet;
    }

    public void setDMagnet(int dMagnet) {
        this.dMagnet = dMagnet;
    }

    public int getDInv() {
        return this.dInv;
    }

    public void setDInv(int dInv) {
        this.dInv = dInv;
    }

    public int getDSleeper() {
        return this.dSleeper;
    }

    public void setDSleeper(int dSleeper) {
        this.dSleeper = dSleeper;
    }

    public int getDAir() {
        return this.dAir;
    }

    public void setDAir(int dAir) {
        this.dAir = dAir;
    }

    public int getDFF() {
        return this.dFF;
    }

    public void setDFF(int dFF) {
        this.dFF = dFF;
    }

    public int getDMaxPower() {
        return this.dMaxPower;
    }

    public void setDMaxPower(int dMaxPower) {
        this.dMaxPower = dMaxPower;
    }

    public int getDPlayerPower() {
        return this.dPlayerPower;
    }

    public void setDPlayerPower(int dPlayerPower) {
        this.dPlayerPower = dPlayerPower;
    }

    public int[] getOldMpx() {
        return this.oldMpx;
    }

    public void setOldMpx(int[] oldMpx) {
        this.oldMpx = oldMpx;
    }

    public int[] getOldMpy() {
        return this.oldMpy;
    }

    public void setOldMpy(int[] oldMpy) {
        this.oldMpy = oldMpy;
    }

    public int getMpx() {
        return this.mpx;
    }

    public void setMpx(int mpx) {
        this.mpx = mpx;
    }

    public int getMpy() {
        return this.mpy;
    }

    public void setMpy(int mpy) {
        this.mpy = mpy;
    }

    public int getInfoBackPen() {
        return this.infoBackPen;
    }

    public void setInfoBackPen(int infoBackPen) {
        this.infoBackPen = infoBackPen;
    }

    public ChaosSounds.Effect[] getLifeEffectL() {
        return this.lifeEffectL;
    }

    public void setLifeEffectL(ChaosSounds.Effect[] lifeEffectL) {
        this.lifeEffectL = lifeEffectL;
    }

    public ChaosSounds.Effect[] getLifeEffectR() {
        return this.lifeEffectR;
    }

    public void setLifeEffectR(ChaosSounds.Effect[] lifeEffectR) {
        this.lifeEffectR = lifeEffectR;
    }

    public ChaosSounds.Effect[] getLife0Effect() {
        return this.life0Effect;
    }

    public void setLife0Effect(ChaosSounds.Effect[] life0Effect) {
        this.life0Effect = life0Effect;
    }

    public ChaosSounds.Effect[] getLife3Effect() {
        return this.life3Effect;
    }

    public void setLife3Effect(ChaosSounds.Effect[] life3Effect) {
        this.life3Effect = life3Effect;
    }

    public ChaosSounds.Effect[] getLife2Effect() {
        return this.life2Effect;
    }

    public void setLife2Effect(ChaosSounds.Effect[] life2Effect) {
        this.life2Effect = life2Effect;
    }

    public ChaosSounds.Effect[] getMioEffect() {
        return this.mioEffect;
    }

    public void setMioEffect(ChaosSounds.Effect[] mioEffect) {
        this.mioEffect = mioEffect;
    }

    public ChaosSounds.Effect[] getLife1Effect() {
        return this.life1Effect;
    }

    public void setLife1Effect(ChaosSounds.Effect[] life1Effect) {
        this.life1Effect = life1Effect;
    }

    public ChaosSounds.Effect[] getMoneyEffect() {
        return this.moneyEffect;
    }

    public void setMoneyEffect(ChaosSounds.Effect[] moneyEffect) {
        this.moneyEffect = moneyEffect;
    }

    public ChaosSounds.Effect[] getCBulletEffectL() {
        return this.cBulletEffectL;
    }

    public void setCBulletEffectL(ChaosSounds.Effect[] cBulletEffectL) {
        this.cBulletEffectL = cBulletEffectL;
    }

    public ChaosSounds.Effect[] getCBulletEffectR() {
        return this.cBulletEffectR;
    }

    public void setCBulletEffectR(ChaosSounds.Effect[] cBulletEffectR) {
        this.cBulletEffectR = cBulletEffectR;
    }

    public ChaosSounds.Effect[] getBulletEffect() {
        return this.bulletEffect;
    }

    public void setBulletEffect(ChaosSounds.Effect[] bulletEffect) {
        this.bulletEffect = bulletEffect;
    }

    public ChaosSounds.Effect[] getCPowerEffect() {
        return this.cPowerEffect;
    }

    public void setCPowerEffect(ChaosSounds.Effect[] cPowerEffect) {
        this.cPowerEffect = cPowerEffect;
    }

    public ChaosSounds.Effect[] getPowerEffectL() {
        return this.powerEffectL;
    }

    public void setPowerEffectL(ChaosSounds.Effect[] powerEffectL) {
        this.powerEffectL = powerEffectL;
    }

    public ChaosSounds.Effect[] getPowerEffectR() {
        return this.powerEffectR;
    }

    public void setPowerEffectR(ChaosSounds.Effect[] powerEffectR) {
        this.powerEffectR = powerEffectR;
    }

    public ChaosSounds.Effect[] getSavedEffect() {
        return this.savedEffect;
    }

    public void setSavedEffect(ChaosSounds.Effect[] savedEffect) {
        this.savedEffect = savedEffect;
    }

    public ChaosSounds.Effect[] getDieEffects() {
        return this.dieEffects;
    }

    public void setDieEffects(ChaosSounds.Effect[] dieEffects) {
        this.dieEffects = dieEffects;
    }

    public int getBulletToAdd() {
        return this.bulletToAdd;
    }

    public void setBulletToAdd(int bulletToAdd) {
        this.bulletToAdd = bulletToAdd;
    }

    public int getBombToAdd() {
        return this.bombToAdd;
    }

    public void setBombToAdd(int bombToAdd) {
        this.bombToAdd = bombToAdd;
    }

    public int getPowerToAdd() {
        return this.powerToAdd;
    }

    public void setPowerToAdd(int powerToAdd) {
        this.powerToAdd = powerToAdd;
    }

    public boolean isWaitPause() {
        return this.waitPause;
    }

    public void setWaitPause(boolean waitPause) {
        this.waitPause = waitPause;
    }

    public boolean isPauseRequest() {
        return this.pauseRequest;
    }

    public void setPauseRequest(boolean pauseRequest) {
        this.pauseRequest = pauseRequest;
    }

    public long getPrevTime() {
        return this.prevTime;
    }

    public void setPrevTime(long prevTime) {
        this.prevTime = prevTime;
    }

    public ChaosSounds.Effect[] getAieEffects() {
        return this.aieEffects;
    }

    public void setAieEffects(ChaosSounds.Effect[] aieEffects) {
        this.aieEffects = aieEffects;
    }

    public ChaosSounds.Effect[] getHitEffect() {
        return this.hitEffect;
    }

    public void setHitEffect(ChaosSounds.Effect[] hitEffect) {
        this.hitEffect = hitEffect;
    }

    public long getOldtime() {
        return this.oldtime;
    }

    public void setOldtime(long oldtime) {
        this.oldtime = oldtime;
    }


    // PROCEDURE

    private void SetP(int pen) {
        if (chaosGraphics.color)
            graphics.SetPen(pen);
        else if (pen > 0)
            graphics.SetPen(1);
        else
            graphics.SetPen(0);
    }

    private void DrawMapLine(int sx, int sy, int dx, int dy) {
        // VAR
        int lx = 0;
        int ly = 0;
        int x = 0;
        int y = 0;
        int px = 0;
        int py = 0;
        int c = 0;
        boolean lon = false;
        boolean on = false;

        lx = sx;
        ly = sy;
        lon = false;
        x = sx;
        y = sy;
        px = mpx + sx;
        py = mpy + sy;
        for (c = 0; c <= 31; c++) {
            on = (px >= 0) && (py >= 0) && (px < chaosGraphics.castleWidth) && (py < chaosGraphics.castleHeight) && (chaosGraphics.castle[py][px] >= ChaosGraphics.NbClear);
            if (on != lon) {
                if (c > 0) {
                    if (lon)
                        SetP(7);
                    else
                        graphics.SetPen(0);
                    graphics.FillRect(chaosGraphics.X.invoke(lx), chaosGraphics.Y.invoke(ly), chaosGraphics.X.invoke(x + dy), chaosGraphics.Y.invoke(y + dx));
                }
                lon = on;
                lx = x;
                ly = y;
            }
            x += dx;
            y += dy;
            px += dx;
            py += dy;
        }
        if (lon)
            SetP(7);
        else
            graphics.SetPen(0);
        graphics.FillRect(chaosGraphics.X.invoke(lx), chaosGraphics.Y.invoke(ly), chaosGraphics.X.invoke(x + dy), chaosGraphics.Y.invoke(y + dx));
    }

    private void SetMapCoords() {
        chaosActions.GetCenter(chaosBase.mainPlayer, new Runtime.FieldRef<>(this::getMpx, this::setMpx), new Runtime.FieldRef<>(this::getMpy, this::setMpy));
        mpx = mpx / ChaosGraphics.BW - 16;
        mpy = mpy / ChaosGraphics.BH - 16;
    }

    private void DrawMap() {
        // VAR
        int y = 0;

        SetMapCoords();
        oldMpx[1] = mpx;
        oldMpy[1] = mpy;
        oldMpx[0] = mpx;
        oldMpy[0] = mpy;
        graphics.SetCopyMode(Graphics.cmCopy);
        chaosGraphics.SetOrigin(ChaosGraphics.PW + 21, 5);
        SetP(4);
        graphics.FillRect(chaosGraphics.X.invoke(0), chaosGraphics.Y.invoke(0), chaosGraphics.X.invoke(38), chaosGraphics.Y.invoke(38));
        SetP(3);
        graphics.FillRect(chaosGraphics.X.invoke(0), chaosGraphics.Y.invoke(0), chaosGraphics.X.invoke(37), chaosGraphics.Y.invoke(37));
        SetP(7);
        graphics.FillRect(chaosGraphics.X.invoke(1), chaosGraphics.Y.invoke(1), chaosGraphics.X.invoke(37), chaosGraphics.Y.invoke(37));
        if (chaosGraphics.color) {
            graphics.SetPen(4);
            graphics.FillRect(chaosGraphics.X.invoke(2), chaosGraphics.Y.invoke(2), chaosGraphics.X.invoke(36), chaosGraphics.Y.invoke(36));
            graphics.SetPen(3);
            graphics.FillRect(chaosGraphics.X.invoke(3), chaosGraphics.Y.invoke(3), chaosGraphics.X.invoke(36), chaosGraphics.Y.invoke(36));
        } else {
            graphics.SetPen(0);
            graphics.FillRect(chaosGraphics.X.invoke(2), chaosGraphics.Y.invoke(2), chaosGraphics.X.invoke(36), chaosGraphics.Y.invoke(36));
        }
        chaosGraphics.SetOrigin(ChaosGraphics.PW + 24, 8);
        for (y = 0; y <= 31; y++) {
            DrawMapLine(0, y, 1, 0);
        }
        SetP(2);
        graphics.FillRect(chaosGraphics.X.invoke(16), chaosGraphics.Y.invoke(16), chaosGraphics.X.invoke(17), chaosGraphics.Y.invoke(17));
        graphics.SetCopyMode(Graphics.cmTrans);
        chaosGraphics.SetOrigin(ChaosGraphics.PW, 0);
    }

    private void ScrollMap() {
        // VAR
        int dx = 0;
        int dy = 0;
        Runtime.Ref<Boolean> first = new Runtime.Ref<>(false);
        Runtime.Ref<Boolean> off = new Runtime.Ref<>(false);

        graphics.GetBuffer(first, off);
        dx = oldMpx[Runtime.ord(first.get())] - mpx;
        dy = oldMpy[Runtime.ord(first.get())] - mpy;
        chaosGraphics.SetOrigin(ChaosGraphics.PW + 24, 8);
        graphics.SetPen(0);
        graphics.FillRect(chaosGraphics.X.invoke(16), chaosGraphics.Y.invoke(16), chaosGraphics.X.invoke(17), chaosGraphics.Y.invoke(17));
        graphics.ScrollRect(chaosGraphics.X.invoke(0), chaosGraphics.Y.invoke(0), chaosGraphics.W.invoke(32), chaosGraphics.H.invoke(32), chaosGraphics.W.invoke(dx), chaosGraphics.H.invoke(dy));
        SetP(2);
        graphics.FillRect(chaosGraphics.X.invoke(16), chaosGraphics.Y.invoke(16), chaosGraphics.X.invoke(17), chaosGraphics.Y.invoke(17));
        while (dx < 0) {
            dx++;
            DrawMapLine(31 + dx, 0, 0, 1);
        }
        while (dx > 0) {
            dx--;
            DrawMapLine(dx, 0, 0, 1);
        }
        while (dy < 0) {
            dy++;
            DrawMapLine(0, 31 + dy, 1, 0);
        }
        while (dy > 0) {
            dy--;
            DrawMapLine(0, dy, 1, 0);
        }
        oldMpx[Runtime.ord(first.get())] = mpx;
        oldMpy[Runtime.ord(first.get())] = mpy;
    }

    private void DrawTitle() {
        // CONST
        final String TTL1 = "Chaos";
        final String TTL2 = " Castle";

        // VAR
        int p1 = 0;

        if ((chaosBase.zone == Zone.Castle) && (chaosBase.gameStat == GameStat.Playing)) {
            DrawMap();
        } else {
            if (chaosGraphics.color)
                p1 = 7;
            else
                p1 = 0;
            SetP(4);
            chaosGraphics.WriteAt(chaosGraphics.X.invoke(5), chaosGraphics.Y.invoke(5), new Runtime.Ref<>(TTL1));
            SetP(3);
            chaosGraphics.WriteAt(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(4), new Runtime.Ref<>(TTL1));
            SetP(p1);
            chaosGraphics.WriteAt(chaosGraphics.X.invoke(5), chaosGraphics.Y.invoke(4), new Runtime.Ref<>(TTL1));
            SetP(4);
            chaosGraphics.WriteAt(chaosGraphics.X.invoke(5), chaosGraphics.Y.invoke(14), new Runtime.Ref<>(TTL2));
            SetP(3);
            chaosGraphics.WriteAt(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(13), new Runtime.Ref<>(TTL2));
            SetP(p1);
            chaosGraphics.WriteAt(chaosGraphics.X.invoke(5), chaosGraphics.Y.invoke(13), new Runtime.Ref<>(TTL2));
        }
    }

    private void DrawLevel() {
        // VAR
        String str = "";
        Runtime.IRef<String> ln = null;

        if ((chaosBase.zone != Zone.Castle) || (chaosBase.gameStat != GameStat.Playing)) {
            if (chaosBase.zone == Zone.Chaos)
                ln = Runtime.castToRef("Chaos", String.class);
            else if (chaosBase.zone == Zone.Castle)
                ln = Runtime.castToRef("Castle", String.class);
            else if (chaosBase.zone == Zone.Family)
                ln = Runtime.castToRef("Family", String.class);
            else
                ln = Runtime.castToRef("*Bonus*", String.class);
            graphics.SetPen(infoBackPen);
            graphics.FillRect(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(24), chaosGraphics.X.invoke(ChaosGraphics.IW - 4), chaosGraphics.Y.invoke(42));
            SetP(6);
            chaosGraphics.WriteAt(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(33), ln);
            chaosGraphics.WriteCard(chaosGraphics.X.invoke(58), chaosGraphics.Y.invoke(33), chaosBase.level[chaosBase.zone.ordinal()] % 100);
            str = "Diff";
            chaosGraphics.WriteAt(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(24), Runtime.castToRef(languages.ADL(str), String.class));
            str = " # ";
            if (chaosBase.difficulty < 10)
                str = Runtime.setChar(str, 1, (char) (48 + chaosBase.difficulty));
            else
                str = "MAX";
            SetP(6);
            chaosGraphics.WriteAt(chaosGraphics.X.invoke(53), chaosGraphics.Y.invoke(24), Runtime.castToRef(languages.ADL(str), String.class));
        }
    }

    private void DrawLife() {
        graphics.SetPen(infoBackPen);
        graphics.FillRect(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(46), chaosGraphics.X.invoke(ChaosGraphics.IW - 4), chaosGraphics.Y.invoke(55));
        SetP(5);
        chaosGraphics.WriteAt(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(46), Runtime.castToRef(languages.ADL("Lives:"), String.class));
        SetP(2);
        chaosGraphics.WriteCard(chaosGraphics.X.invoke(58), chaosGraphics.Y.invoke(46), chaosBase.pLife);
    }

    private void DrawScore() {
        graphics.SetPen(infoBackPen);
        graphics.FillRect(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(55), chaosGraphics.X.invoke(ChaosGraphics.IW - 4), chaosGraphics.Y.invoke(64));
        if ((chaosBase.zone == Zone.Chaos) || !chaosGraphics.color)
            graphics.SetPen(1);
        else
            graphics.SetPen(0);
        chaosGraphics.WriteCard(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(55), chaosBase.score);
    }

    private void DrawDollar() {
        graphics.SetPen(infoBackPen);
        graphics.FillRect(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(64), chaosGraphics.X.invoke(ChaosGraphics.IW / 2), chaosGraphics.Y.invoke(73));
        graphics.SetPen(5);
        chaosGraphics.WriteAt(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(64), Runtime.castToRef("$ ", String.class));
        chaosGraphics.WriteCard(chaosGraphics.X.invoke(12), chaosGraphics.Y.invoke(64), chaosBase.nbDollar);
    }

    private void DrawSterling() {
        graphics.SetPen(infoBackPen);
        graphics.FillRect(chaosGraphics.X.invoke(ChaosGraphics.IW / 2), chaosGraphics.Y.invoke(64), chaosGraphics.X.invoke(ChaosGraphics.IW - 4), chaosGraphics.Y.invoke(73));
        graphics.SetPen(5);
        chaosGraphics.WriteAt(chaosGraphics.X.invoke(ChaosGraphics.IW / 2), chaosGraphics.Y.invoke(64), Runtime.castToRef("£ ", String.class));
        chaosGraphics.WriteCard(chaosGraphics.X.invoke(ChaosGraphics.IW / 2 + 8), chaosGraphics.Y.invoke(64), chaosBase.nbSterling);
    }

    private void DrawTime(int p, int y, int h, int what, /* VAR */ Runtime.IRef<Integer> dWhat) {
        // VAR
        int width = 0;

        if ((p != 0) && (p == infoBackPen))
            p = 1;
        width = what / (ChaosBase.Period / 2);
        if (width > 72)
            width = 72;
        if (width > 0) {
            if (chaosGraphics.color)
                graphics.SetPen(p);
            else
                graphics.SetPen(1);
            graphics.FillRect(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(y), chaosGraphics.X.invoke(width + 4), chaosGraphics.Y.invoke(y + h));
        }
        if (width < 72) {
            graphics.SetPen(infoBackPen);
            graphics.FillRect(chaosGraphics.X.invoke(width + 4), chaosGraphics.Y.invoke(y), chaosGraphics.X.invoke(76), chaosGraphics.Y.invoke(y + h));
        }
        dWhat.set(what);
    }

    private void DrawMagnet() {
        DrawTime(2, 74, 1, chaosBase.magnet, new Runtime.FieldRef<>(this::getDMagnet, this::setDMagnet));
    }

    private void DrawInv() {
        DrawTime(3, 75, 1, chaosBase.invinsibility, new Runtime.FieldRef<>(this::getDInv, this::setDInv));
    }

    private void DrawFF() {
        DrawTime(6, 76, 1, chaosBase.freeFire, new Runtime.FieldRef<>(this::getDFF, this::setDFF));
    }

    private void DrawSleeper() {
        DrawTime(7, 77, 1, chaosBase.sleeper, new Runtime.FieldRef<>(this::getDSleeper, this::setDSleeper));
    }

    private void DrawMaxPower() {
        DrawTime(4, 78, 1, chaosBase.maxPower, new Runtime.FieldRef<>(this::getDMaxPower, this::setDMaxPower));
    }

    private void DrawAir() {
        DrawTime(0, 79, 1, chaosBase.air, new Runtime.FieldRef<>(this::getDAir, this::setDAir));
    }

    private void DrawPlayerPower() {
        DrawTime(5, 80, 2, chaosBase.playerPower * ChaosBase.Period, new Runtime.FieldRef<>(this::getDPlayerPower, this::setDPlayerPower));
    }

    private void DrawNumber(Weapon w, boolean select) {
        // VAR
        int y = 0;

        graphics.SetPen(infoBackPen);
        y = w.ordinal() * 19 + 93;
        graphics.FillRect(chaosGraphics.X.invoke(27), chaosGraphics.Y.invoke(y), chaosGraphics.X.invoke(45), chaosGraphics.Y.invoke(y + 9));
        if (select) {
            SetP(3);
            graphics.SetTextMode(EnumSet.of(TextModes.bold, TextModes.italic));
        } else {
            SetP(4);
            graphics.SetTextMode(EnumSet.noneOf(TextModes.class));
        }
        chaosGraphics.WriteCard(chaosGraphics.X.invoke(27), chaosGraphics.Y.invoke(w.ordinal() * 19 + 93), chaosBase.weaponAttr[w.ordinal()].nbBullet);
        if (select)
            graphics.SetTextMode(EnumSet.noneOf(TextModes.class));
    }

    public Runtime.IRef<String> WeaponToStr(Weapon w) {
        switch (w) {
            case GUN -> {
                return Runtime.castToRef(languages.ADL("Gun"), String.class);
            }
            case FB -> {
                return Runtime.castToRef(languages.ADL("Fireball"), String.class);
            }
            case LASER -> {
                return Runtime.castToRef(languages.ADL("Laser"), String.class);
            }
            case BUBBLE -> {
                return Runtime.castToRef(languages.ADL("Bubble"), String.class);
            }
            case FIRE -> {
                return Runtime.castToRef(languages.ADL("Fire"), String.class);
            }
            case BALL -> {
                return Runtime.castToRef(languages.ADL("Ball"), String.class);
            }
            case STAR -> {
                return Runtime.castToRef(languages.ADL("Star"), String.class);
            }
            case GRENADE -> {
                return Runtime.castToRef(languages.ADL("Grenade"), String.class);
            }
            default -> throw new RuntimeException("Unhandled CASE value " + w);
        }
    }

    private void DrawWeapon(Weapon w, boolean select) {
        // VAR
        Runtime.IRef<String> ln = null;
        int y = 0;
        int p = 0;

        y = w.ordinal() * 19 + 84;
        graphics.SetPen(infoBackPen);
        graphics.FillRect(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(y), chaosGraphics.X.invoke(ChaosGraphics.IW - 4), chaosGraphics.Y.invoke(y + 19));
        if (select) {
            SetP(3);
            graphics.SetTextMode(EnumSet.of(TextModes.bold, TextModes.italic));
        } else {
            SetP(4);
            graphics.SetTextMode(EnumSet.noneOf(TextModes.class));
        }
        ln = WeaponToStr(w);
        chaosGraphics.CenterText(chaosGraphics.X.invoke(4), chaosGraphics.Y.invoke(y), chaosGraphics.W.invoke(ChaosGraphics.IW - 8), ln);
        y += 9;
        graphics.SetTextMode(EnumSet.noneOf(TextModes.class));
        DrawNumber(w, select);
        chaosGraphics.WriteAt(chaosGraphics.X.invoke(45), chaosGraphics.Y.invoke(y), Runtime.castToRef("/ ", String.class));
        chaosGraphics.WriteCard(chaosGraphics.X.invoke(54), chaosGraphics.Y.invoke(y), chaosBase.weaponAttr[w.ordinal()].nbBomb);
        p = chaosBase.weaponAttr[w.ordinal()].power;
        graphics.FillRect(chaosGraphics.X.invoke(9), chaosGraphics.Y.invoke(y), chaosGraphics.X.invoke(18), chaosGraphics.Y.invoke(y + 9));
        SetP(infoBackPen);
        if (p > 3)
            SetP(3);
        graphics.FillRect(chaosGraphics.X.invoke(14), chaosGraphics.Y.invoke(y + 1), chaosGraphics.X.invoke(17), chaosGraphics.Y.invoke(y + 4));
        if (p > 2)
            SetP(3);
        graphics.FillRect(chaosGraphics.X.invoke(10), chaosGraphics.Y.invoke(y + 1), chaosGraphics.X.invoke(13), chaosGraphics.Y.invoke(y + 4));
        if (p > 1)
            SetP(3);
        graphics.FillRect(chaosGraphics.X.invoke(14), chaosGraphics.Y.invoke(y + 5), chaosGraphics.X.invoke(17), chaosGraphics.Y.invoke(y + 8));
        if (p > 0)
            SetP(3);
        graphics.FillRect(chaosGraphics.X.invoke(10), chaosGraphics.Y.invoke(y + 5), chaosGraphics.X.invoke(13), chaosGraphics.Y.invoke(y + 8));
    }

    private void DrawRect(int x1, int y1, int x2, int y2) {
        graphics.DrawLine(x1, y1, x2, y1);
        graphics.DrawLine(x1, y2, x2, y2);
        y1++;
        y2--;
        graphics.DrawLine(x1, y1, x1, y2);
        graphics.DrawLine(x2, y1, x2, y2);
    }

    public void UpdateInfos() {
        // VAR
        Weapon w = Weapon.GUN;

        chaosGraphics.SetOrigin(ChaosGraphics.PW, 0);
        graphics.SetCopyMode(Graphics.cmTrans);
        graphics.SetPat(4);
        graphics.SetTextSize(chaosGraphics.H.invoke(9));
        if (chaosBase.gameStat == GameStat.Playing) {
            if (!gChanges.equals(EnumSet.noneOf(Infos.class))) {
                ogChanges = Runtime.plusSet(ogChanges, gChanges);
                gChanges = EnumSet.copyOf(ogChanges);
            }
            if (!wChanges.equals(EnumSet.noneOf(Weapon.class))) {
                owChanges = Runtime.plusSet(owChanges, wChanges);
                wChanges = EnumSet.copyOf(owChanges);
            }
            if (!nChanges.equals(EnumSet.noneOf(Weapon.class))) {
                onChanges = Runtime.plusSet(onChanges, nChanges);
                nChanges = EnumSet.copyOf(onChanges);
            }
        } else {
            ogChanges = EnumSet.copyOf(gChanges);
            owChanges = EnumSet.copyOf(wChanges);
            onChanges = EnumSet.copyOf(nChanges);
        }
        if (chaosBase.magnet != dMagnet)
            DrawMagnet();
        if (chaosBase.invinsibility != dInv)
            DrawInv();
        if (chaosBase.freeFire != dFF)
            DrawFF();
        if (chaosBase.sleeper != dSleeper)
            DrawSleeper();
        if (chaosBase.maxPower != dMaxPower)
            DrawMaxPower();
        if (chaosBase.air != dAir)
            DrawAir();
        if (chaosBase.playerPower != dPlayerPower)
            DrawPlayerPower();
        if (ogChanges.contains(Infos.TITLE))
            DrawTitle();
        if (ogChanges.contains(Infos.LEVEL))
            DrawLevel();
        if (ogChanges.contains(Infos.LIFE))
            DrawLife();
        if (ogChanges.contains(Infos.SCORE))
            DrawScore();
        if (ogChanges.contains(Infos.DOLLAR))
            DrawDollar();
        if (ogChanges.contains(Infos.STERLING))
            DrawSterling();
        for (int _w = 0; _w < Weapon.values().length; _w++) {
            w = Weapon.values()[_w];
            if (owChanges.contains(w))
                DrawWeapon(w, chaosBase.weaponSelected && (chaosBase.selectedWeapon == w));
            else if (onChanges.contains(w))
                DrawNumber(w, chaosBase.weaponSelected && (chaosBase.selectedWeapon == w));
        }
        ogChanges = EnumSet.copyOf(gChanges);
        gChanges = EnumSet.noneOf(Infos.class);
        owChanges = EnumSet.copyOf(wChanges);
        wChanges = EnumSet.noneOf(Weapon.class);
        onChanges = EnumSet.copyOf(nChanges);
        nChanges = EnumSet.noneOf(Weapon.class);
    }

    public void DrawInfos(boolean db) {
        // CONST
        final int Inv = ((1 << 16) - 1) /* MAX(CARDINAL) */;

        // VAR
        Runtime.Ref<Boolean> first = new Runtime.Ref<>(false);
        Runtime.Ref<Boolean> off = new Runtime.Ref<>(false);

        if (db) {
            graphics.GetBuffer(first, off);
            graphics.SetBuffer(first.get(), true);
        }
        chaosGraphics.SetOrigin(ChaosGraphics.PW, 0);
        dMagnet = Inv;
        dInv = Inv;
        dSleeper = Inv;
        dAir = Inv;
        dMaxPower = Inv;
        dFF = Inv;
        dPlayerPower = Inv;
        graphics.SetCopyMode(Graphics.cmCopy);
        graphics.SetPat(4);
        graphics.SetTextSize(chaosGraphics.H.invoke(9));
        if (!chaosGraphics.color)
            infoBackPen = 0;
        else if (chaosBase.zone == Zone.Chaos)
            infoBackPen = 0;
        else if (chaosBase.zone == Zone.Castle)
            infoBackPen = 1;
        else if (chaosBase.zone == Zone.Family)
            infoBackPen = 7;
        else
            infoBackPen = 2;
        graphics.SetPen(infoBackPen);
        graphics.FillRect(chaosGraphics.X.invoke(0), chaosGraphics.Y.invoke(0), chaosGraphics.X.invoke(ChaosGraphics.IW), chaosGraphics.Y.invoke(ChaosGraphics.IH));
        SetP(5);
        DrawRect(chaosGraphics.X.invoke(0), chaosGraphics.Y.invoke(0), chaosGraphics.X.invoke(ChaosGraphics.IW) - 2, chaosGraphics.Y.invoke(ChaosGraphics.IH) - 2);
        SetP(0);
        DrawRect(chaosGraphics.X.invoke(0) + 2, chaosGraphics.Y.invoke(0) + 2, chaosGraphics.X.invoke(ChaosGraphics.IW), chaosGraphics.Y.invoke(ChaosGraphics.IH));
        SetP(6);
        DrawRect(chaosGraphics.X.invoke(0) + 1, chaosGraphics.Y.invoke(0) + 1, chaosGraphics.X.invoke(ChaosGraphics.IW) - 1, chaosGraphics.Y.invoke(ChaosGraphics.IH) - 1);
        graphics.DrawLine(chaosGraphics.X.invoke(0) + 3, chaosGraphics.Y.invoke(45), chaosGraphics.X.invoke(ChaosGraphics.IW) - 4, chaosGraphics.Y.invoke(45));
        graphics.DrawLine(chaosGraphics.X.invoke(0) + 3, chaosGraphics.Y.invoke(83), chaosGraphics.X.invoke(ChaosGraphics.IW) - 4, chaosGraphics.Y.invoke(83));
        gChanges = EnumSet.of(Infos.TITLE, Infos.LEVEL, Infos.LIFE, Infos.SCORE, Infos.DOLLAR, Infos.STERLING);
        wChanges = EnumSet.of(Weapon.GUN, Weapon.FB, Weapon.LASER, Weapon.BUBBLE, Weapon.FIRE, Weapon.BALL, Weapon.STAR, Weapon.GRENADE);
        UpdateInfos();
        if (db)
            graphics.SetBuffer(first.get(), off.get());
    }

    public void AddLife(ChaosBase.Obj player) {
        if (chaosBase.pLife >= 29)
            return;
        if (player != null)
            player.life++;
        chaosBase.pLife++;
        if ((chaosBase.pLife >= 20) && (chaosBase.stages == 4))
            chaosActions.NextStage();
        if (chaosBase.gameStat == GameStat.Playing) {
            chaosSounds.StereoEffect();
            chaosSounds.SoundEffect(player, lifeEffectL);
            chaosSounds.SoundEffect(player, lifeEffectR);
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("EXTRA LIFE"), String.class), ChaosActions.lifePos, 4);
        }
        gChanges.add(Infos.LIFE);
    }

    public void AddMoney(ChaosBase.Obj player, int dollar, int sterling) {
        // VAR
        Runtime.Ref<String> str = new Runtime.Ref<>("");
        int v = 0;
        int d = 0;

        if (chaosBase.gameStat == GameStat.Playing) {
            chaosSounds.SoundEffect(player, moneyEffect);
            v = 0;
            if (sterling > 0)
                v = sterling;
            else if (dollar > 0)
                v = dollar;
            if (v > 0) {
                d = v / 10;
                if (d > 0)
                    Runtime.setChar(str, 0, (char) (d + 48));
                else
                    Runtime.setChar(str, 0, ' ');
                Runtime.setChar(str, 1, (char) (v % 10 + 48));
                if (sterling > 0)
                    Runtime.setChar(str, 2, '£');
                else
                    Runtime.setChar(str, 2, '$');
                Runtime.setChar(str, 3, ((char) 0));
                chaosActions.PopMessage(str, ChaosActions.moneyPos, 1);
            }
        }
        if (dollar > 0)
            chaosBase.nbDollar += dollar;
        else
            chaosBase.nbDollar -= -dollar;
        if (sterling > 0)
            chaosBase.nbSterling += sterling;
        else
            chaosBase.nbSterling -= -sterling;
        if (chaosBase.nbDollar > 200) {
            if (chaosBase.stages == 2)
                chaosActions.NextStage();
            chaosActions.AddPt(chaosBase.nbDollar - 200);
            chaosBase.nbDollar = 200;
        }
        if (chaosBase.nbSterling > 200) {
            chaosActions.AddPt((chaosBase.nbSterling - 200) * 3);
            chaosBase.nbSterling = 200;
        }
        if (dollar != 0)
            gChanges.add(Infos.DOLLAR);
        if (sterling != 0)
            gChanges.add(Infos.STERLING);
    }

    public void AddToWeapon(ChaosBase.Obj player, Weapon w, /* VAR */ Runtime.IRef<Integer> bullet, /* VAR */ Runtime.IRef<Integer> bomb) {
        if (chaosBase.weaponAttr[w.ordinal()].power == 0)
            return;
        if (chaosBase.gameStat == GameStat.Playing)
            chaosSounds.SoundEffect(player, bulletEffect);
        { // WITH
            ChaosBase.WeaponAttr _weaponAttr = chaosBase.weaponAttr[w.ordinal()];
            if (99 - _weaponAttr.nbBullet >= bullet.get()) {
                _weaponAttr.nbBullet += bullet.get();
                bullet.set(0);
            } else {
                if (chaosBase.gameStat == GameStat.Playing)
                    chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Weapon reloaded"), String.class), ChaosActions.moneyPos, 3);
                bullet.dec(99 - _weaponAttr.nbBullet);
                _weaponAttr.nbBullet = 99;
            }
            if (9 - _weaponAttr.nbBomb >= bomb.get())
                _weaponAttr.nbBomb += bomb.get();
            else
                _weaponAttr.nbBomb = 9;
            bomb.set(0);
        }
        wChanges.add(w);
    }

    public void AddWeapon(ChaosBase.Obj player, int bullet) {
        chaosSounds.StereoEffect();
        chaosSounds.SoundEffect(player, cBulletEffectR);
        chaosSounds.SoundEffect(player, cBulletEffectL);
        chaosActions.PopMessage(Runtime.castToRef(languages.ADL(AddMsg), String.class), ChaosActions.actionPos, 2);
        if (255 - bullet > bulletToAdd)
            bulletToAdd += bullet;
        else
            bulletToAdd = 255;
    }

    public void AddBomb(ChaosBase.Obj player, int bomb) {
        chaosSounds.StereoEffect();
        chaosSounds.SoundEffect(player, cBulletEffectR);
        chaosSounds.SoundEffect(player, cBulletEffectL);
        chaosActions.PopMessage(Runtime.castToRef(languages.ADL(AddMsg), String.class), ChaosActions.actionPos, 2);
        bombToAdd += bomb;
    }

    public void AddPower(ChaosBase.Obj player, int power) {
        powerToAdd += power;
        chaosSounds.SoundEffect(player, cPowerEffect);
        chaosActions.PopMessage(Runtime.castToRef(languages.ADL(AddMsg), String.class), ChaosActions.actionPos, 4);
    }

    public void MakeInvinsible(ChaosBase.Obj player, int time) {
        chaosBase.invinsibility += time;
        player.hitSubLife = 0;
        player.fireSubLife = 0;
        player.posY = chaosGraphics.mulS * 16;
    }

    private void Pause() {
        // VAR
        Input.Event event = new Input.Event(); /* WRT */
        Runtime.Ref<Boolean> redraw = new Runtime.Ref<>(false);
        Runtime.IRef<String> str = null;
        int c = 0;

        if (!waitPause) {
            graphics.GetBuffer(new Runtime.FieldRef<>(this::isWaitPause, this::setWaitPause), redraw);
            if (!waitPause) {
                waitPause = true;
                return;
            }
        }
        waitPause = false;
        pauseRequest = false;
        chaosActions.HotFlush();
        redraw.set(true);
        c = 100;
        input.FlushEvents();
        while (true) {
            if (redraw.get()) {
                str = Runtime.castToRef(languages.ADL("Game paused"), String.class);
                chaosGraphics.SetOrigin(0, 0);
                graphics.SetCopyMode(Graphics.cmTrans);
                graphics.SetPen(0);
                chaosGraphics.CenterText(0, chaosGraphics.Y.invoke(ChaosGraphics.PH / 2 + 1), chaosGraphics.X.invoke(ChaosGraphics.PW), str);
                if (chaosGraphics.color)
                    graphics.SetPen(6);
                chaosGraphics.CenterText(chaosGraphics.X.invoke(2), chaosGraphics.Y.invoke(ChaosGraphics.PH / 2), chaosGraphics.X.invoke(ChaosGraphics.PW), str);
                SetP(5);
                redraw.set(false);
                chaosGraphics.CenterText(0, chaosGraphics.Y.invoke(ChaosGraphics.PH / 2), chaosGraphics.X.invoke(ChaosGraphics.PW), str);
                graphics.SetBuffer(true, false);
                input.SetBusyStat(Input.statWaiting);
            }
            while ((c != 0) && (!Runtime.RangeSet.mul(input.GetStick(), new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyPause).withRange(Input.Joy1, Input.Joy4)).equals(new Runtime.RangeSet(Memory.SET16_r)))) {
                c--;
                graphics.WaitTOF();
            }
            input.WaitEvent();
            input.GetEvent(event);
            if ((event.type == Input.eKEYBOARD) && (event.modifiers.equals(EnumSet.noneOf(Modifiers.class)))) {
                input.SendEvent(event);
                break;
            } else if (event.type != Input.eNUL) {
                chaosInterface.CommonEvent(event);
                redraw.set(true);
            }
            if (!Runtime.RangeSet.mul(input.GetStick(), new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyPause).withRange(Input.Joy1, Input.Joy4)).equals(new Runtime.RangeSet(Memory.SET16_r)))
                break;
            if (chaosBase.gameStat != GameStat.Playing)
                break;
        }
        graphics.SetBuffer(true, true);
        chaosActions.HotInit();
    }

    private void MakePlayer(ChaosBase.Obj player) {
        chaosActions.SetObjLoc(player, 0, 0, 16, 16);
        chaosActions.SetObjRect(player, 2, 2, 14, 14);
        chaosBase.nextGunFireTime = 0;
        prevTime = 0;
        chaosBase.mainPlayer = player;
    }

    private final ChaosBase.MakeProc MakePlayer_ref = this::MakePlayer;

    private void ResetPlayer(ChaosBase.Obj player) {
        bulletToAdd = 0;
        bombToAdd = 0;
        powerToAdd = 0;
        player.life = chaosBase.pLife;
        player.shapeSeq = 0;
        player.stat = 0;
        player.hitSubLife = 1;
        player.fireSubLife = 0;
        chaosBase.playerPower = 36;
        MakePlayer(player);
    }

    private final ChaosBase.ResetProc ResetPlayer_ref = this::ResetPlayer;

    private void AiePlayer(ChaosBase.Obj player, ChaosBase.Obj src, /* VAR+WRT */ Runtime.IRef<Integer> hit, /* VAR+WRT */ Runtime.IRef<Integer> fire) {
        // VAR
        ChaosBase.Obj obj = null;
        ChaosBase.Obj tail = null;
        ChaosBase.Obj todie = null;
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        int c = 0;
        int rem = 0;
        Runtime.Ref<Integer> thit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> tfire = new Runtime.Ref<>(0);
        int nd = 0;
        int ns = 0;
        Anims a = Anims.PLAYER;
        Runtime.Ref<String> msg = new Runtime.Ref<>("");

        rem = (hit.get() + fire.get()) / 2;
        if (rem >= chaosBase.playerPower) {
            chaosActions.Boum(player, EnumSet.of(Stones.stC34), ChaosBase.gravityStyle, ChaosBase.FlameMult * 6 + 15, 1);
            chaosSounds.SoundEffect(player, aieEffects);
            if (chaosBase.zone == Zone.Special) {
                chaosBase.gameStat = GameStat.Finish;
                return;
            }
            chaosBase.playerPower = 0;
            chaosActions.DecLife(player, hit, fire);
            chaosActions.GetCenter(player, px, py);
            for (int _a = Anims.ALIEN2.ordinal(); _a <= Anims.MISSILE.ordinal(); _a++) {
                a = Anims.values()[_a];
                obj = (ChaosBase.Obj) memory.First(chaosBase.animList[a.ordinal()]);
                tail = (ChaosBase.Obj) memory.Tail(chaosBase.animList[a.ordinal()]);
                while (obj != tail) {
                    todie = obj;
                    obj = (ChaosBase.Obj) memory.Next(obj.animNode);
                    if (chaosActions.Collision(todie, player)) {
                        thit.set(todie.hitSubLife);
                        tfire.set(todie.fireSubLife);
                        if (thit.get() > 30)
                            thit.set(30);
                        if (tfire.get() > 30)
                            tfire.set(30);
                        chaosActions.Aie(todie, player, thit, tfire);
                    }
                }
            }
            if (chaosGraphics.color) {
                chaosGraphics.SetTrans(0, 255);
                chaosGraphics.SetRGB(0, 255, 255, 255);
                chaosGraphics.SetRGB(4, 255, 255, 255);
            }
            if (chaosBase.pLife > 0)
                chaosBase.pLife--;
            gChanges.add(Infos.LIFE);
            chaosBase.screenInverted = ChaosBase.Period / 2;
            MakeInvinsible(player, ChaosBase.Period * 4);
            player.life = chaosBase.pLife;
            nd = chaosBase.nbDollar;
            ns = chaosBase.nbSterling;
            if (player.life == 0)
                chaosSounds.SoundEffect(player, life0Effect);
            if ((player.life == 0) && (nd + ns < 20)) {
                chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Game Over"), String.class), ChaosActions.lifePos, 5);
            } else if (player.life > 0) {
                chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Ouch !"), String.class), ChaosActions.lifePos, 3);
                if (player.life <= 3) {
                    if (player.life == 3)
                        chaosSounds.SoundEffect(player, life3Effect);
                    else if (player.life == 2)
                        chaosSounds.SoundEffect(player, life2Effect);
                    else
                        chaosSounds.SoundEffect(player, life1Effect);
                    memory.CopyStr(Runtime.castToRef(languages.ADL("# live(s) left"), String.class), msg, Runtime.sizeOf(40, String.class));
                    c = 0;
                    while ((Runtime.getChar(msg, c) != '#') && (c < Runtime.sizeOf(40, String.class))) {
                        c++;
                    }
                    if (c < Runtime.sizeOf(40, String.class))
                        Runtime.setChar(msg, c, (char) (48 + player.life));
                    chaosActions.PopMessage(msg, ChaosActions.statPos, 2);
                }
            }
        } else {
            chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Ow"), String.class), ChaosActions.lifePos, 2);
            MakeInvinsible(player, ChaosBase.Period);
            chaosSounds.SoundEffect(player, hitEffect);
            hit.set(0);
            fire.set(0);
            chaosBase.playerPower -= rem;
        }
    }

    private final ChaosBase.AieProc AiePlayer_ref = this::AiePlayer;

    public void CheckSelect(/* VAR */ Input.Event e, /* VAR */ Runtime.RangeSet stick, boolean update) {
        // VAR
        int c = 0;
        char oldkey = (char) 0;
        char newkey = (char) 0;
        Weapon w = Weapon.GUN;
        Runtime.Ref<Boolean> first = new Runtime.Ref<>(false);
        Runtime.Ref<Boolean> off = new Runtime.Ref<>(false);

        if (chaosBase.weaponSelected) {
            if (e.type == Input.eKEYBOARD) {
                if ((e.ch >= 'a') && (e.ch <= 'z')) {
                    oldkey = Runtime.getChar(chaosBase.weaponKey, chaosBase.selectedWeapon.ordinal());
                    newkey = e.ch;
                    chaosBase.weaponKey = Runtime.setChar(chaosBase.weaponKey, chaosBase.selectedWeapon.ordinal(), newkey);
                    for (int _w = 0; _w < Weapon.values().length; _w++) {
                        w = Weapon.values()[_w];
                        if ((w != chaosBase.selectedWeapon) && (Runtime.getChar(chaosBase.weaponKey, w.ordinal()) == newkey))
                            chaosBase.weaponKey = Runtime.setChar(chaosBase.weaponKey, w.ordinal(), oldkey);
                    }
                    wChanges.add(chaosBase.selectedWeapon);
                    chaosBase.weaponSelected = false;
                    e.type = Input.eNUL;
                }
            }
            if (stick.contains(Input.Joy1)) {
                chaosBase.weaponSelected = false;
                wChanges.add(chaosBase.selectedWeapon);
                stick.excl(Input.Joy1);
            }
            for (c = Input.Joy2; c <= 11; c++) {
                if (stick.contains(c)) {
                    chaosBase.buttonAssign[c] = chaosBase.selectedWeapon;
                    wChanges.add(chaosBase.selectedWeapon);
                    chaosBase.weaponSelected = false;
                    stick.excl(c);
                }
            }
        }
        if (!((stick.contains(Input.JoyForward)) && (stick.contains(Input.JoyReverse)))) {
            if ((stick.contains(Input.JoyForward)) && !chaosBase.lastJoy.contains(Input.JoyForward)) {
                if (chaosBase.weaponSelected) {
                    wChanges.add(chaosBase.selectedWeapon);
                    if (chaosBase.selectedWeapon == Weapon.GRENADE /* MAX(Weapon) */)
                        chaosBase.selectedWeapon = Weapon.GUN /* MIN(Weapon) */;
                    chaosBase.selectedWeapon = Runtime.next(chaosBase.selectedWeapon);
                    wChanges.add(chaosBase.selectedWeapon);
                } else {
                    chaosBase.weaponSelected = true;
                    wChanges.add(chaosBase.selectedWeapon);
                }
                stick.excl(Input.JoyForward);
            } else if ((stick.contains(Input.JoyReverse)) && !chaosBase.lastJoy.contains(Input.JoyReverse)) {
                if (chaosBase.weaponSelected) {
                    wChanges.add(chaosBase.selectedWeapon);
                    chaosBase.selectedWeapon = Runtime.prev(chaosBase.selectedWeapon);
                    if (chaosBase.selectedWeapon == Weapon.GUN /* MIN(Weapon) */)
                        chaosBase.selectedWeapon = Weapon.GRENADE /* MAX(Weapon) */;
                    wChanges.add(chaosBase.selectedWeapon);
                } else {
                    chaosBase.weaponSelected = true;
                    wChanges.add(chaosBase.selectedWeapon);
                }
                stick.excl(Input.JoyReverse);
            }
        }
        if (update && (!wChanges.equals(EnumSet.noneOf(Weapon.class)))) {
            graphics.GetBuffer(first, off);
            graphics.SetBuffer(first.get(), true);
            UpdateInfos();
            graphics.SetBuffer(first.get(), off.get());
        }
    }

    public void CheckStick() {
        // VAR
        long nextTime = 0L;

        nextTime = clock.GetTime(chaosActions.time);
        if ((prevTime > nextTime) || (nextTime - prevTime > ChaosBase.Period / 40)) {
            chaosBase.lastJoy = Runtime.RangeSet.mul(chaosBase.lastJoy, input.GetStick());
            prevTime = nextTime;
        }
    }

    private void MovePlayer0_PressWeapon(Weapon w, ChaosBase.Obj player) {
        if ((powerToAdd > 0) && (chaosBase.weaponAttr[w.ordinal()].power < 4)) {
            { // WITH
                ChaosBase.WeaponAttr _weaponAttr = chaosBase.weaponAttr[w.ordinal()];
                if (4 - _weaponAttr.power >= powerToAdd) {
                    _weaponAttr.power += powerToAdd;
                    powerToAdd = 0;
                    chaosActions.PopMessage(Runtime.castToRef(languages.ADL("POWER ADDED"), String.class), ChaosActions.actionPos, 5);
                } else {
                    powerToAdd -= 4 - _weaponAttr.power;
                    _weaponAttr.power = 4;
                }
            }
            wChanges.add(w);
            chaosSounds.StereoEffect();
            chaosSounds.SoundEffect(player, powerEffectL);
            chaosSounds.SoundEffect(player, powerEffectR);
        } else if (((bulletToAdd > 0) && (chaosBase.weaponAttr[w.ordinal()].nbBullet < 99)) || ((bombToAdd > 0) && (chaosBase.weaponAttr[w.ordinal()].nbBomb < 99))) {
            if (bulletToAdd != 0) {
                if (chaosBase.weaponAttr[w.ordinal()].power > 0)
                    chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Bullet added"), String.class), ChaosActions.moneyPos, 2);
                else
                    chaosActions.PopMessage(Runtime.castToRef(languages.ADL("not enough power"), String.class), ChaosActions.moneyPos, 2);
            }
            if (bombToAdd != 0) {
                if (chaosBase.weaponAttr[w.ordinal()].power > 0)
                    chaosActions.PopMessage(Runtime.castToRef(languages.ADL("BOMB added"), String.class), ChaosActions.lifePos, 2);
                else
                    chaosActions.PopMessage(Runtime.castToRef(languages.ADL("not enough power"), String.class), ChaosActions.moneyPos, 2);
            }
            if (chaosBase.weaponAttr[w.ordinal()].power > 0) {
                if (bombToAdd > 0) {
                    if (EnumSet.of(Weapon.GUN, Weapon.BALL, Weapon.GRENADE).contains(w))
                        bombToAdd += 2;
                    if (EnumSet.of(Weapon.LASER, Weapon.BUBBLE, Weapon.FIRE).contains(w))
                        bombToAdd++;
                }
                bulletToAdd = bulletToAdd / chaosWeapon.GetBulletPrice(w);
                AddToWeapon(player, w, new Runtime.FieldRef<>(this::getBulletToAdd, this::setBulletToAdd), new Runtime.FieldRef<>(this::getBombToAdd, this::setBombToAdd));
                bulletToAdd = 0;
            }
        } else if (w != Weapon.GUN) {
            nChanges.add(w);
            chaosBase.Fire[w.ordinal()].invoke(player);
        }
    }

    private void MovePlayer0(ChaosBase.Obj player) {
        // CONST
        final int SDSpeed = 2100;
        final int DDSpeed = 1485;
        final Runtime.RangeSet N = new Runtime.RangeSet(Memory.SET16_r);
        final Runtime.RangeSet U = new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyUp);
        final Runtime.RangeSet UL = new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyUp, Input.JoyLeft);
        final Runtime.RangeSet L = new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyLeft);
        final Runtime.RangeSet DL = new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyLeft, Input.JoyDown);
        final Runtime.RangeSet D = new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyDown);
        final Runtime.RangeSet DR = new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyDown, Input.JoyRight);
        final Runtime.RangeSet R = new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyRight);
        final Runtime.RangeSet UR = new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyRight, Input.JoyUp);
        final Runtime.RangeSet BDH = new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyLeft, Input.JoyRight);
        final Runtime.RangeSet BDV = new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyUp, Input.JoyDown);
        final Runtime.RangeSet DirMask = new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyUp, Input.JoyDown, Input.JoyLeft, Input.JoyRight);

        // VAR
        Input.Event event = new Input.Event(); /* WRT */
        Runtime.RangeSet joy = new Runtime.RangeSet(Memory.SET16_r); /* WRT */
        Runtime.RangeSet dir = new Runtime.RangeSet(Memory.SET16_r);
        Runtime.RangeSet tmp = new Runtime.RangeSet(Memory.SET16_r);
        Weapon w = Weapon.GUN;
        Runtime.Ref<Integer> mouseX = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> mouseY = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> px = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> py = new Runtime.Ref<>(0);
        long oldx = 0L;
        long oldy = 0L;
        long newx = 0L;
        long newy = 0L;
        long oldscr = 0L;
        Runtime.Ref<Integer> tohit = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> tofire = new Runtime.Ref<>(0);
        int c = 0;
        Runtime.Ref<String> msg = new Runtime.Ref<>("");
        Runtime.Ref<Boolean> first = new Runtime.Ref<>(false);
        Runtime.Ref<Boolean> off = new Runtime.Ref<>(false);

        chaosBase.mainPlayer = player;
        chaosSounds.ModulateSounds();
        UpdateInfos();
        joy.copyFrom(input.GetStick());
        tmp.copyFrom(joy);
        input.GetEvent(event);
        dir.copyFrom((Runtime.RangeSet.mul(joy, DirMask)));
        if (Runtime.RangeSet.mul(dir, BDH).equals(BDH))
            dir = Runtime.RangeSet.minus(dir, BDH);
        if (Runtime.RangeSet.mul(dir, BDV).equals(BDV))
            dir = Runtime.RangeSet.minus(dir, BDV);
        if (dir.equals(N)) {
            player.dvx = 0;
            player.dvy = 0;
        } else if (dir.equals(U)) {
            player.dvx = 0;
            player.dvy = -SDSpeed;
            player.shapeSeq = 0;
        } else if (dir.equals(UR)) {
            player.dvx = DDSpeed;
            player.dvy = -DDSpeed;
            player.shapeSeq = 1;
        } else if (dir.equals(R)) {
            player.dvx = SDSpeed;
            player.dvy = 0;
            player.shapeSeq = 2;
        } else if (dir.equals(DR)) {
            player.dvx = DDSpeed;
            player.dvy = DDSpeed;
            player.shapeSeq = 3;
        } else if (dir.equals(D)) {
            player.dvx = 0;
            player.dvy = SDSpeed;
            player.shapeSeq = 4;
        } else if (dir.equals(DL)) {
            player.dvx = -DDSpeed;
            player.dvy = DDSpeed;
            player.shapeSeq = 5;
        } else if (dir.equals(L)) {
            player.dvx = -SDSpeed;
            player.dvy = 0;
            player.shapeSeq = 6;
        } else if (dir.equals(UL)) {
            player.dvx = -DDSpeed;
            player.dvy = -DDSpeed;
            player.shapeSeq = 7;
        }
        if (chaosBase.doubleSpeed > 0) {
            player.dvx = player.dvx * 3 / 2;
            player.dvy = player.dvy * 3 / 2;
        }
        px.set(player.shapeSeq);
        player.posX = chaosGraphics.W.invoke(px.get() * 16);
        oldx = player.x;
        oldy = player.y;
        chaosActions.UpdateXY(player);
        if ((oldtime < chaosBase.lasttime) && (chaosBase.lasttime - oldtime > 60)) {
            chaosBase.nextGunFireTime += (chaosBase.lasttime - oldtime);
            chaosBase.nextGunFireTime -= chaosBase.step;
        }
        oldtime = chaosBase.lasttime;
        chaosActions.AvoidBounds(player, 0);
        chaosActions.AvoidBackground(player, 0);
        if (chaosBase.zone == Zone.Castle) {
            SetMapCoords();
            graphics.GetBuffer(first, off);
            if ((oldMpx[Runtime.ord(first.get())] != mpx) || (oldMpy[Runtime.ord(first.get())] != mpy))
                ScrollMap();
        }
        chaosActions.GetCenter(player, px, py);
        newx = player.x;
        newy = player.y;
        player.x = oldx;
        player.y = oldy;
        chaosGraphics.MoveBackground(px.get(), py.get());
        player.x = newx;
        player.y = newy;
        input.GetMouse(mouseX, mouseY);
        if (waitPause || pauseRequest || (Math.abs(chaosActions.lastMouseX - mouseX.get()) + Math.abs(chaosActions.lastMouseY - mouseY.get()) > 6))
            Pause();
        CheckSelect(event, joy, false);
        if ((event.type == Input.eKEYBOARD) && (event.ch != ((char) 0))) {
            if ((event.ch == 'p') || (event.ch == 'P')) {
                pauseRequest = true;
            } else if ((event.ch == 'H') && chaosBase.password) {
                throw new HaltException();
            } else if ((event.ch == '$') && chaosBase.password) {
                chaosBase.nbDollar = 199;
                chaosBase.nbSterling = 199;
                gChanges.add(Infos.DOLLAR);
                gChanges.add(Infos.STERLING);
            } else if ((event.ch == '.') && chaosBase.password) {
                chaosBase.gameStat = GameStat.Finish;
            } else if ((event.ch >= 'a') && (event.ch <= 'z')) {
                for (int _w = 0; _w < Weapon.values().length; _w++) {
                    w = Weapon.values()[_w];
                    if (Runtime.getChar(chaosBase.weaponKey, w.ordinal()) == event.ch) {
                        if ((!event.modifiers.equals(EnumSet.noneOf(Modifiers.class))) || (joy.contains(Input.JoyShift)) || (chaosBase.bombActive)) {
                            wChanges.add(w);
                            chaosBase.Bomb[w.ordinal()].invoke(player);
                            chaosBase.bombActive = false;
                        } else {
                            MovePlayer0_PressWeapon(w, player);
                        }
                    }
                }
            } else if ((event.ch >= 'A') && (event.ch <= 'Z')) {
                for (int _w = 0; _w < Weapon.values().length; _w++) {
                    w = Weapon.values()[_w];
                    if (Runtime.getChar(chaosBase.weaponKey, w.ordinal()) == (char) (event.ch - 'A' + 'a')) {
                        wChanges.add(w);
                        chaosBase.Bomb[w.ordinal()].invoke(player);
                        chaosBase.bombActive = false;
                    }
                }
            }
        } else {
            chaosInterface.CommonEvent(event);
        }
        if (joy.contains(Input.Joy1)) {
            if ((joy.contains(Input.JoyShift)) || (chaosBase.bombActive)) {
                if (chaosBase.lasttime > chaosBase.nextGunFireTime) {
                    wChanges.add(Weapon.GUN);
                    chaosBase.Bomb[Weapon.GUN.ordinal()].invoke(player);
                    chaosBase.bombActive = false;
                    chaosBase.nextGunFireTime = chaosBase.lasttime;
                    chaosBase.nextGunFireTime += ChaosBase.GunFireSpeed;
                }
            } else if ((powerToAdd > 0) && (chaosBase.weaponAttr[Weapon.GUN.ordinal()].power < 4)) {
                MovePlayer0_PressWeapon(Weapon.GUN, player);
            } else {
                chaosBase.gunFiring = true;
            }
        } else {
            chaosBase.gunFiring = false;
        }
        for (c = Input.Joy2; c <= 11; c++) {
            if ((joy.contains(c)) && !chaosBase.lastJoy.contains(c)) {
                if ((joy.contains(Input.JoyShift)) || (chaosBase.bombActive)) {
                    wChanges.add(chaosBase.buttonAssign[c]);
                    chaosBase.Bomb[chaosBase.buttonAssign[c].ordinal()].invoke(player);
                    chaosBase.bombActive = false;
                } else {
                    MovePlayer0_PressWeapon(chaosBase.buttonAssign[c], player);
                }
            }
        }
        if ((joy.contains(Input.JoyPause)) && !chaosBase.lastJoy.contains(Input.JoyPause))
            pauseRequest = true;
        if ((joy.contains(Input.JoyForward)) && (joy.contains(Input.JoyReverse)) && (!chaosBase.lastJoy.contains(Input.JoyForward) || !chaosBase.lastJoy.contains(Input.JoyReverse))) {
            if (!chaosBase.bombActive)
                chaosActions.PopMessage(Runtime.castToRef(languages.ADL("bomb actived"), String.class), ChaosActions.actionPos, 1);
            else
                chaosActions.PopMessage(Runtime.castToRef(languages.ADL("bomb unactived"), String.class), ChaosActions.actionPos, 1);
            chaosBase.weaponSelected = false;
            wChanges.add(chaosBase.selectedWeapon);
            chaosBase.bombActive = !chaosBase.bombActive;
        }
        chaosBase.lastJoy.copyFrom(tmp);
        chaosActions.CheckLeftObjs();
        if (chaosBase.gunFiring) {
            if (chaosBase.lasttime >= chaosBase.nextGunFireTime) {
                chaosBase.Fire[Weapon.GUN.ordinal()].invoke(player);
                if (chaosBase.nextGunFireTime + ChaosBase.Period / 5 < chaosBase.lasttime)
                    chaosBase.nextGunFireTime = chaosBase.lasttime;
                chaosBase.nextGunFireTime += ChaosBase.GunFireSpeed;
            }
        }
        chaosGraphics.AnimPalette(chaosBase.step);
        if ((chaosBase.addpt > 0) && (chaosBase.step < 60)) {
            oldscr = chaosBase.score;
            while (chaosBase.addpt > 0) {
                chaosBase.addpt--;
                chaosBase.score += chaosBase.difficulty;
            }
            if (((chaosBase.score >= 100000) && (oldscr < 100000)) || ((chaosBase.score / 500000) != (oldscr / 500000))) {
                for (int _w = 0; _w < Weapon.values().length; _w++) {
                    w = Weapon.values()[_w];
                    { // WITH
                        ChaosBase.WeaponAttr _weaponAttr = chaosBase.weaponAttr[w.ordinal()];
                        if (_weaponAttr.power > 0) {
                            wChanges.add(w);
                            if (_weaponAttr.nbBomb < 8)
                                _weaponAttr.nbBomb += 2;
                            else
                                _weaponAttr.nbBomb = 9;
                        }
                    }
                }
                chaosSounds.SoundEffect(player, mioEffect);
                chaosActions.PopMessage(Runtime.castToRef(languages.ADL("Mega score bonus"), String.class), ChaosActions.lifePos, 7);
            }
            if (!registration.registered && ((chaosBase.score >= 1000) || (chaosBase.level[Zone.Castle.ordinal()] >= 8)))
                checks.Check(true, Runtime.castToRef(languages.ADL("Please pay the shareware"), String.class), Runtime.castToRef(languages.ADL("if you want to play more"), String.class));
            if ((chaosBase.stages == 5) && (chaosBase.score >= 1000))
                chaosActions.NextStage();
            gChanges.add(Infos.SCORE);
        }
        if (chaosBase.step >= chaosBase.magnet) {
            chaosBase.magnet = 0;
        } else {
            chaosActions.Gravity(player, EnumSet.of(Anims.ALIEN2, Anims.ALIEN1, Anims.MISSILE, Anims.BONUS, Anims.STONE));
            chaosBase.magnet -= chaosBase.step;
        }
        if (chaosBase.step > chaosBase.freeFire)
            chaosBase.freeFire = 0;
        else
            chaosBase.freeFire -= chaosBase.step;
        if (chaosBase.step > chaosBase.maxPower)
            chaosBase.maxPower = 0;
        else
            chaosBase.maxPower -= chaosBase.step;
        if (chaosBase.step > chaosBase.noMissile)
            chaosBase.noMissile = 0;
        else
            chaosBase.noMissile -= chaosBase.step;
        if (chaosBase.step > chaosBase.sleeper)
            chaosBase.sleeper = 0;
        else
            chaosBase.sleeper -= chaosBase.step;
        if (chaosBase.water) {
            if (chaosBase.air == 0) {
                tohit.set(chaosBase.playerPower);
                tofire.set(tohit.get());
                AiePlayer(player, null, tohit, tofire);
                chaosActions.PopMessage(Runtime.castToRef(languages.ADL("() No more air ()"), String.class), ChaosActions.lifePos, 4);
                chaosBase.air = ChaosBase.Period * 60;
            } else if (chaosBase.step > chaosBase.air) {
                chaosBase.air = 0;
            } else {
                chaosBase.air -= chaosBase.step;
                c = chaosBase.air / (ChaosBase.Period * 3);
                if (c != (dAir / (ChaosBase.Period * 3))) {
                    if (c <= 5) {
                        msg.set("() # ()");
                        Runtime.setChar(msg, 3, (char) (48 + c));
                        Runtime.setChar(msg, 7, ((char) 0));
                        chaosActions.PopMessage(msg, ChaosActions.lifePos, 1);
                    } else if (c == 20) {
                        chaosActions.PopMessage(Runtime.castToRef(languages.ADL("() 60 ()"), String.class), ChaosActions.lifePos, 2);
                    } else if (c == 10) {
                        chaosActions.PopMessage(Runtime.castToRef(languages.ADL("() 30 ()"), String.class), ChaosActions.lifePos, 2);
                    }
                }
            }
        }
        if (chaosBase.invinsibility > 0) {
            if (chaosBase.step >= chaosBase.invinsibility)
                chaosBase.invinsibility = 0;
            else
                chaosBase.invinsibility -= chaosBase.step;
            if (chaosBase.invinsibility == 0) {
                if (chaosBase.playerPower == 0)
                    chaosBase.playerPower = 36;
                if (player.life > 0) {
                    player.hitSubLife = 1;
                    player.posY = 0;
                } else if (chaosBase.password) {
                    player.hitSubLife = 1;
                    player.life = 10;
                    chaosBase.pLife = 10;
                    player.posY = 0;
                    chaosSounds.SoundEffect(player, dieEffects);
                    gChanges.add(Infos.LIFE);
                } else if ((chaosBase.nbDollar > 20) || (chaosBase.nbDollar + chaosBase.nbSterling >= 20)) {
                    player.hitSubLife = 1;
                    player.life++;
                    chaosBase.pLife++;
                    player.posY = 0;
                    chaosActions.PopMessage(Runtime.castToRef(languages.ADL("$AVED BY THE CA$H"), String.class), ChaosActions.lifePos, 5);
                    chaosSounds.SoundEffect(player, savedEffect);
                    if (chaosBase.nbDollar >= 20) {
                        chaosBase.nbDollar -= 20;
                    } else {
                        chaosBase.nbSterling -= 20 - chaosBase.nbDollar;
                        chaosBase.nbDollar = 0;
                    }
                    gChanges = Runtime.plusSet(gChanges, EnumSet.of(Infos.LIFE, Infos.DOLLAR, Infos.STERLING));
                } else {
                    chaosActions.Die(player);
                    return;
                }
            }
        }
        if (chaosBase.screenInverted > 0) {
            if (chaosBase.step >= chaosBase.screenInverted) {
                chaosBase.screenInverted = 0;
                if (chaosGraphics.color) {
                    chaosGraphics.SetTrans(0, 0);
                    chaosGraphics.SetRGB(0, 0, 0, 0);
                    chaosGraphics.SetRGB(4, 255, 0, 0);
                }
            } else {
                chaosBase.screenInverted -= chaosBase.step;
            }
        }
    }

    private final ChaosBase.MoveProc MovePlayer0_ref = this::MovePlayer0;

    private void DiePlayer(ChaosBase.Obj player) {
        chaosSounds.SoundEffect(player, dieEffects);
        chaosBase.gameStat = GameStat.Gameover;
    }

    private final ChaosBase.DieProc DiePlayer_ref = this::DiePlayer;

    private void InitParams() {
        // VAR
        ChaosBase.ObjAttr attr = null;
        int c = 0;
        int v = 0;
        int f = 0;

        chaosSounds.SetEffect(lifeEffectL[0], chaosSounds.soundList[SoundList.wVoice.ordinal()], 392, 6272, 180, 12);
        chaosSounds.SetEffect(lifeEffectL[1], chaosSounds.soundList[SoundList.wVoice.ordinal()], 523, 8363, 180, 12);
        chaosSounds.SetEffect(lifeEffectL[2], chaosSounds.soundList[SoundList.wVoice.ordinal()], 698, 11163, 180, 12);
        chaosSounds.SetEffect(lifeEffectL[3], chaosSounds.soundList[SoundList.wVoice.ordinal()], 587, 9387, 180, 12);
        chaosSounds.SetEffect(lifeEffectL[4], chaosSounds.soundList[SoundList.wVoice.ordinal()], 784, 12544, 180, 12);
        chaosSounds.SetEffect(lifeEffectL[5], chaosSounds.soundList[SoundList.wVoice.ordinal()], 1045, 16726, 180, 12);
        chaosSounds.SetEffect(lifeEffectL[6], chaosSounds.soundList[SoundList.wVoice.ordinal()], 1395, 22327, 180, 12);
        chaosSounds.SetEffect(lifeEffectL[7], chaosSounds.soundList[SoundList.wVoice.ordinal()], 1173, 18774, 180, 12);
        for (c = 8; c <= 20; c += 4) {
            v = (21 - c) * 11;
            chaosSounds.SetEffect(lifeEffectL[c], chaosSounds.soundList[SoundList.wVoice.ordinal()], 392, 12544, v, 12);
            chaosSounds.SetEffect(lifeEffectL[c + 1], chaosSounds.soundList[SoundList.wVoice.ordinal()], 587, 18774, v, 12);
            chaosSounds.SetEffect(lifeEffectL[c + 2], chaosSounds.soundList[SoundList.wVoice.ordinal()], 739, 23654, v, 12);
            chaosSounds.SetEffect(lifeEffectL[c + 3], chaosSounds.soundList[SoundList.wVoice.ordinal()], 2352, 25089, v, 12);
        }
        for (c = 0; c <= 23; c++) {
            lifeEffectR[c].copyFrom(lifeEffectL[c]);
        }
        for (c = 8; c <= 23; c++) {
            if ((c / 4) % 2 == 0)
                lifeEffectL[c].volume = lifeEffectL[c].volume / 4;
            else
                lifeEffectR[c].volume = lifeEffectR[c].volume / 4;
        }
        chaosSounds.SetEffect(lifeEffectR[7], chaosSounds.soundList[SoundList.wVoice.ordinal()], 1467, 18774, 180, 12);
        chaosSounds.SetEffect(life0Effect[0], chaosSounds.soundList[SoundList.sPoubelle.ordinal()], 0, 4181, 0, 9);
        chaosSounds.SetEffect(life0Effect[1], chaosSounds.soundList[SoundList.sPoubelle.ordinal()], 0, 4181, 160, 9);
        chaosSounds.SetEffect(life1Effect[0], chaosSounds.soundList[SoundList.wJans.ordinal()], 0, 8363, 120, 9);
        chaosSounds.SetEffect(life1Effect[1], chaosSounds.soundList[SoundList.wVoice.ordinal()], 1394, 12544, 40, 9);
        chaosSounds.SetEffect(life1Effect[2], chaosSounds.soundList[SoundList.wVoice.ordinal()], 3485, 10454, 40, 9);
        chaosSounds.SetEffect(life1Effect[3], chaosSounds.soundList[SoundList.wVoice.ordinal()], 1043, 9387, 120, 9);
        chaosSounds.SetEffect(life1Effect[4], chaosSounds.soundList[SoundList.wVoice.ordinal()], 3717, 11151, 60, 9);
        chaosSounds.SetEffect(life1Effect[5], chaosSounds.soundList[SoundList.wJans.ordinal()], 1394, 8363, 120, 9);
        chaosSounds.SetEffect(life1Effect[6], chaosSounds.soundList[SoundList.wJans.ordinal()], 1742, 10454, 130, 9);
        chaosSounds.SetEffect(life1Effect[7], chaosSounds.soundList[SoundList.wJans.ordinal()], 2060, 12545, 140, 9);
        chaosSounds.SetEffect(life1Effect[8], chaosSounds.soundList[SoundList.wJans.ordinal()], 0, 14868, 150, 9);
        for (c = 0; c <= 2; c++) {
            life3Effect[c].copyFrom(life1Effect[c]);
        }
        for (c = 0; c <= 4; c++) {
            life2Effect[c].copyFrom(life1Effect[c]);
        }
        chaosSounds.SetEffect(aieEffects[0], chaosSounds.soundList[SoundList.sVerre.ordinal()], 0, 0, 255, 14);
        chaosSounds.SetEffect(hitEffect[0], chaosSounds.soundList[SoundList.sPoubelle.ordinal()], 0, 12544, 210, 10);
        chaosSounds.SetEffect(dieEffects[0], chaosSounds.soundList[SoundList.sGong.ordinal()], 0, 0, 255, 14);
        chaosSounds.SetEffect(moneyEffect[0], chaosSounds.soundList[SoundList.sMoney.ordinal()], 0, 0, 70, 4);
        chaosSounds.SetEffect(bulletEffect[0], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 0, 16726, 110, 6);
        chaosSounds.SetEffect(cPowerEffect[0], chaosSounds.soundList[SoundList.sCannon.ordinal()], 0, 16726, 180, 12);
        chaosSounds.SetEffect(savedEffect[0], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 0, 0, 150, 8);
        chaosSounds.SetEffect(cBulletEffectL[0], chaosSounds.soundList[SoundList.sHHat.ordinal()], 1566, 12530, 90, 5);
        chaosSounds.SetEffect(cBulletEffectL[1], chaosSounds.soundList[SoundList.sHHat.ordinal()], 1566, 12530, 80, 5);
        chaosSounds.SetEffect(cBulletEffectL[2], chaosSounds.soundList[SoundList.sHHat.ordinal()], 1566, 12530, 70, 5);
        chaosSounds.SetEffect(cBulletEffectL[3], chaosSounds.soundList[SoundList.sHHat.ordinal()], 1243, 9945, 60, 5);
        chaosSounds.SetEffect(cBulletEffectL[4], chaosSounds.soundList[SoundList.sHHat.ordinal()], 1395, 11163, 70, 5);
        chaosSounds.SetEffect(cBulletEffectL[5], chaosSounds.soundList[SoundList.sHHat.ordinal()], 1395, 11163, 80, 5);
        chaosSounds.SetEffect(cBulletEffectL[6], chaosSounds.soundList[SoundList.sHHat.ordinal()], 0, 11163, 90, 5);
        chaosSounds.SetEffect(cBulletEffectR[0], chaosSounds.soundList[SoundList.sHHat.ordinal()], 1243, 9945, 80, 6);
        chaosSounds.SetEffect(cBulletEffectR[1], chaosSounds.soundList[SoundList.sHHat.ordinal()], 1243, 9945, 80, 6);
        chaosSounds.SetEffect(cBulletEffectR[2], chaosSounds.soundList[SoundList.sHHat.ordinal()], 1566, 12530, 80, 6);
        chaosSounds.SetEffect(cBulletEffectR[3], chaosSounds.soundList[SoundList.sHHat.ordinal()], 1566, 12530, 80, 6);
        chaosSounds.SetEffect(cBulletEffectR[4], chaosSounds.soundList[SoundList.sHHat.ordinal()], 1243, 9945, 80, 6);
        chaosSounds.SetEffect(cBulletEffectR[5], chaosSounds.soundList[SoundList.sHHat.ordinal()], 1243, 9945, 80, 6);
        chaosSounds.SetEffect(cBulletEffectR[6], chaosSounds.soundList[SoundList.sHHat.ordinal()], 0, 11163, 80, 6);
        for (c = 0; c <= 8; c += 4) {
            v = (10 - c) * 24;
            if (c == 0)
                f = 1;
            else
                f = 2;
            chaosSounds.SetEffect(powerEffectL[c], chaosSounds.nulSound, 1045 * f, 8363 * f, v, 15 - c);
            chaosSounds.SetEffect(powerEffectL[c + 1], chaosSounds.nulSound, 1395 * f, 11163 * f, v, 15 - c);
            chaosSounds.SetEffect(powerEffectL[c + 2], chaosSounds.nulSound, 1173 * f, 9387 * f, v, 15 - c);
            chaosSounds.SetEffect(powerEffectL[c + 3], chaosSounds.nulSound, 1566 * f, 12530 * f, v, 15 - c);
        }
        chaosSounds.SetEffect(powerEffectL[0], chaosSounds.soundList[SoundList.wShakuhachi.ordinal()], 1045, 8363, 240, 15);
        chaosSounds.SetEffect(powerEffectR[0], chaosSounds.soundList[SoundList.wShakuhachi.ordinal()], 697, 16726, 0, 15);
        for (c = 1; c <= 12; c++) {
            powerEffectR[c].copyFrom(powerEffectL[c - 1]);
        }
        chaosSounds.SetEffect(mioEffect[0], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 2088, 6265, 180, 15);
        chaosSounds.SetEffect(mioEffect[1], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 2631, 7894, 180, 15);
        chaosSounds.SetEffect(mioEffect[2], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 2344, 7032, 180, 15);
        chaosSounds.SetEffect(mioEffect[3], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 1971, 5913, 180, 15);
        chaosSounds.SetEffect(mioEffect[4], chaosSounds.soundList[SoundList.sCaisse.ordinal()], 0, 6265, 120, 15);
        chaosBase.selectedWeapon = Weapon.GUN;
        chaosBase.selectedWeapon = Runtime.next(chaosBase.selectedWeapon);
        chaosBase.weaponSelected = false;
        for (c = 0; c <= 15; c++) {
            chaosBase.buttonAssign[c] = Weapon.GUN;
        }
        attr = (ChaosBase.ObjAttr) memory.AllocMem(Runtime.sizeOf(130, ChaosBase.ObjAttr.class));
        checks.CheckMem(attr);
        attr.Reset = ResetPlayer_ref;
        attr.Make = MakePlayer_ref;
        attr.Move = MovePlayer0_ref;
        attr.Aie = AiePlayer_ref;
        attr.Die = DiePlayer_ref;
        attr.inerty = 128;
        attr.weight = 48;
        attr.charge = 40;
        attr.coolSpeed = 10;
        attr.aieStKinds = EnumSet.of(Stones.stC34);
        attr.aieSKCount = 1;
        attr.aieStone = 16;
        attr.aieStStyle = ChaosBase.gravityStyle;
        attr.dieStKinds = Runtime.withRange(EnumSet.noneOf(Stones.class), Stones.stC26, Stones.stFLAME2);
        attr.dieSKCount = 12;
        attr.dieStStyle = ChaosBase.fastStyle;
        attr.dieStone = ((1 << 8) - 1) /* MAX(SHORTCARD) */;
        attr.basicType = BasicTypes.NotBase;
        memory.AddHead(chaosBase.attrList[Anims.PLAYER.ordinal()], attr.node);
    }


    // Support

    private static ChaosPlayer instance;

    public static ChaosPlayer instance() {
        if (instance == null)
            new ChaosPlayer(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        bulletToAdd = 0;
        powerToAdd = 0;
        oldtime = 0;
        waitPause = false;
        pauseRequest = false;
        InitParams();
    }

    public void close() {
    }

}
