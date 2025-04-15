package ch.chaos.castle;

import java.lang.Runnable;
import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.BasicTypes;
import ch.chaos.castle.ChaosBase.GameStat;
import ch.chaos.castle.ChaosBase.Weapon;
import ch.chaos.castle.ChaosBase.Zone;
import ch.chaos.castle.ChaosSounds.SoundList;
import ch.chaos.library.Checks;
import ch.chaos.library.Clock;
import ch.chaos.library.Graphics;
import ch.chaos.library.Graphics.TextModes;
import ch.chaos.library.Input;
import ch.chaos.library.Languages;
import ch.chaos.library.Memory;
import ch.chaos.library.Registration;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosScreens {

    // Imports
    private final ChaosActions chaosActions;
    private final ChaosBase chaosBase;
    private final ChaosGraphics chaosGraphics;
    private final ChaosImages chaosImages;
    private final ChaosInterface chaosInterface;
    private final ChaosLevels chaosLevels;
    private final ChaosPlayer chaosPlayer;
    private final ChaosSounds chaosSounds;
    private final ChaosWeapon chaosWeapon;
    private final Checks checks;
    private final Clock clock;
    private final Graphics graphics;
    private final Input input;
    private final Languages languages;
    private final Memory memory;
    private final Registration registration;
    private final Trigo trigo;


    private ChaosScreens() {
        instance = this; // Set early to handle circular dependencies
        chaosActions = ChaosActions.instance();
        chaosBase = ChaosBase.instance();
        chaosGraphics = ChaosGraphics.instance();
        chaosImages = ChaosImages.instance();
        chaosInterface = ChaosInterface.instance();
        chaosLevels = ChaosLevels.instance();
        chaosPlayer = ChaosPlayer.instance();
        chaosSounds = ChaosSounds.instance();
        chaosWeapon = ChaosWeapon.instance();
        checks = Checks.instance();
        clock = Clock.instance();
        graphics = Graphics.instance();
        input = Input.instance();
        languages = Languages.instance();
        memory = Memory.instance();
        registration = Registration.instance();
        trigo = Trigo.instance();
    }


    // CONST

    private static final int x1 = ChaosGraphics.PW / 2;
    private static final int x2 = ChaosGraphics.PW * 3 / 4;


    // VAR

    private short p1;
    private short p2;
    private short p3;
    private boolean joy2pressed;
    private String levelName = "";
    private boolean gameMade;
    private boolean[][] wActive = new boolean[Weapon.values().length][2];
    private short[] available = new short[Weapon.values().length];
    private boolean[] dActive = new boolean[5];
    private boolean bomb;
    private boolean up;
    private Weapon yw;
    private int yd;
    private int h;
    private int m;
    private int s;
    private int om;
    private boolean shopClosed;
    private int sl;
    private int n;
    private ChaosInterface.TopScore[] topScores = Runtime.initArray(new ChaosInterface.TopScore[10]);


    public short getP1() {
        return this.p1;
    }

    public void setP1(short p1) {
        this.p1 = p1;
    }

    public short getP2() {
        return this.p2;
    }

    public void setP2(short p2) {
        this.p2 = p2;
    }

    public short getP3() {
        return this.p3;
    }

    public void setP3(short p3) {
        this.p3 = p3;
    }

    public boolean isJoy2pressed() {
        return this.joy2pressed;
    }

    public void setJoy2pressed(boolean joy2pressed) {
        this.joy2pressed = joy2pressed;
    }

    public String getLevelName() {
        return this.levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public boolean isGameMade() {
        return this.gameMade;
    }

    public void setGameMade(boolean gameMade) {
        this.gameMade = gameMade;
    }

    public boolean[][] getWActive() {
        return this.wActive;
    }

    public void setWActive(boolean[][] wActive) {
        this.wActive = wActive;
    }

    public short[] getAvailable() {
        return this.available;
    }

    public void setAvailable(short[] available) {
        this.available = available;
    }

    public boolean[] getDActive() {
        return this.dActive;
    }

    public void setDActive(boolean[] dActive) {
        this.dActive = dActive;
    }

    public boolean isBomb() {
        return this.bomb;
    }

    public void setBomb(boolean bomb) {
        this.bomb = bomb;
    }

    public boolean isUp() {
        return this.up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public Weapon getYw() {
        return this.yw;
    }

    public void setYw(Weapon yw) {
        this.yw = yw;
    }

    public int getYd() {
        return this.yd;
    }

    public void setYd(int yd) {
        this.yd = yd;
    }

    public int getH() {
        return this.h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getM() {
        return this.m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getS() {
        return this.s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public int getOm() {
        return this.om;
    }

    public void setOm(int om) {
        this.om = om;
    }

    public boolean isShopClosed() {
        return this.shopClosed;
    }

    public void setShopClosed(boolean shopClosed) {
        this.shopClosed = shopClosed;
    }

    public int getSl() {
        return this.sl;
    }

    public void setSl(int sl) {
        this.sl = sl;
    }

    public int getN() {
        return this.n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public ChaosInterface.TopScore[] getTopScores() {
        return this.topScores;
    }

    public void setTopScores(ChaosInterface.TopScore[] topScores) {
        this.topScores = topScores;
    }


    // PROCEDURE

    private void SetP(short p) {
        if (chaosGraphics.color)
            graphics.SetPen(p);
        else if (p > 0)
            graphics.SetPen(1);
        else
            graphics.SetPen(0);
    }

    private void TripleWrite(short x, short y, Runtime.IRef<String> s) {
        SetP(p3);
        chaosGraphics.WriteAt(chaosGraphics.X.invoke((short) (x + 1)), chaosGraphics.Y.invoke((short) (y + 1)), s);
        SetP(p2);
        chaosGraphics.WriteAt(chaosGraphics.X.invoke(x), chaosGraphics.Y.invoke(y), s);
        if (chaosGraphics.color)
            graphics.SetPen(p1);
        else if ((p1 == 1) || ((p1 > 1) && (chaosBase.gameStat == GameStat.Gameover)))
            graphics.SetPen(1);
        else
            graphics.SetPen(0);
        chaosGraphics.WriteAt(chaosGraphics.X.invoke((short) (x + 1)), chaosGraphics.Y.invoke(y), s);
    }

    private void TripleCard(short x, short y, long val) {
        SetP(p3);
        chaosGraphics.WriteCard(chaosGraphics.X.invoke((short) (x + 1)), chaosGraphics.Y.invoke((short) (y + 1)), val);
        SetP(p2);
        chaosGraphics.WriteCard(chaosGraphics.X.invoke(x), chaosGraphics.Y.invoke(y), val);
        if (chaosGraphics.color)
            graphics.SetPen(p1);
        else
            graphics.SetPen(0);
        chaosGraphics.WriteCard(chaosGraphics.X.invoke((short) (x + 1)), chaosGraphics.Y.invoke(y), val);
    }

    private void Center(short x, short y, short w, Runtime.IRef<String> s) {
        // VAR
        short d = 0;

        d = (short) ((chaosGraphics.W.invoke(w) - graphics.TextWidth(s)) / 2);
        TripleWrite((short) (x + d / chaosGraphics.mulS), y, s);
    }

    private void TripleCenter(short w, short y, Runtime.IRef<String> s) {
        Center((short) 0, y, w, s);
    }

    private void ResetGraphics() {
        graphics.SetArea(chaosGraphics.mainArea);
        graphics.SetBuffer(true, true);
        chaosGraphics.SetOrigin((short) 0, (short) 0);
        graphics.SetPen(0);
        graphics.SetCopyMode(Graphics.cmTrans);
        graphics.SetTextMode(EnumSet.noneOf(TextModes.class));
    }

    private void UpdateScreen() {
        graphics.SetArea(chaosGraphics.mainArea);
        graphics.SetBuffer(true, false);
        graphics.UpdateArea();
    }

    private void WaitRelease() {
        // VAR
        int cnt = 0;

        cnt = 0;
        while ((!input.GetStick().equals(new Runtime.RangeSet(Memory.SET16_r))) && (cnt < 200)) {
            graphics.WaitTOF();
            cnt++;
        }
        input.FlushEvents();
    }

    private void WaitStart() {
        // CONST
        final String Password = "Nightmare";

        // VAR
        String buffer = "";
        Input.Event event = new Input.Event(); /* WRT */
        GameStat oldstat = GameStat.Start;
        Runtime.RangeSet stick = new Runtime.RangeSet(Memory.SET16_r); /* WRT */
        Runtime.RangeSet tmp = new Runtime.RangeSet(Memory.SET16_r);
        int ppos = 0;

        buffer = Password;
        ppos = 0;
        UpdateScreen();
        graphics.AreaToFront();
        oldstat = chaosBase.gameStat;
        WaitRelease();
        while (true) {
            if (chaosBase.gameStat != oldstat)
                break;
            input.SetBusyStat((short) Input.statWaiting);
            input.WaitEvent();
            stick.copyFrom(input.GetStick());
            tmp.copyFrom(stick);
            input.GetEvent(event);
            if ((event.type == Input.eKEYBOARD) && (event.ch != ((char) 0))) {
                if (event.ch == Runtime.getChar(buffer, ppos)) {
                    if (ppos == 8) {
                        chaosBase.password = true;
                        ppos = 0;
                        chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sGong.ordinal()]);
                    } else {
                        ppos++;
                    }
                } else {
                    ppos = 0;
                }
            }
            if (chaosBase.gameStat != GameStat.Start)
                chaosPlayer.CheckSelect(event, stick, true);
            joy2pressed = (stick.contains(Input.Joy2));
            if (!Runtime.RangeSet.mul(stick, new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyPause).withRange(Input.Joy1, Input.Joy4)).equals(new Runtime.RangeSet(Memory.SET16_r)))
                break;
            chaosInterface.CommonEvent(event);
            chaosBase.lastJoy.copyFrom(tmp);
            UpdateScreen();
        }
    }

    private void DrawStart() {
        ResetGraphics();
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 9));
        p1 = 2;
        TripleCenter((short) ChaosGraphics.PW, (short) 230, Runtime.castToRef(languages.ADL("Press [SPACE] to start"), String.class));
        UpdateScreen();
    }

    public void TitleScreen() {
        chaosInterface.WarmInit();
        chaosInterface.DisableFileMenus();
        chaosImages.InitPalette();
        chaosActions.ZoomMessage(Runtime.castToRef(memory.ADS("ChaosCastle"), String.class), 7, 3, 4);
        graphics.SetBuffer(true, false);
        graphics.SetPen(0);
        graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) ChaosGraphics.SW), chaosGraphics.H.invoke((short) ChaosGraphics.SH));
    }

    private void DrawStartScreen() {
        ResetGraphics();
        if (chaosGraphics.color) {
            chaosGraphics.SetRGB((short) 8, (short) 0, (short) 0, (short) 0);
            graphics.SetPalette((short) 8, (short) 0, (short) 0, (short) 0);
            graphics.SetPen(8);
        }
        graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) ChaosGraphics.SW), chaosGraphics.H.invoke((short) ChaosGraphics.SH));
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 27));
        p1 = 7;
        p2 = 3;
        p3 = 4;
        TripleCenter((short) ChaosGraphics.SW, (short) 0, Runtime.castToRef(memory.ADS("Chaos Castle"), String.class));
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 9));
        TripleCenter((short) ChaosGraphics.SW, (short) 27, Runtime.castToRef(memory.ADS("(C) 1999 by Nicky"), String.class));
        if (registration.registered) {
            TripleCenter((short) ChaosGraphics.SW, (short) 40, Runtime.castToRef(languages.ADL("Full Version"), String.class));
            TripleCenter((short) ChaosGraphics.SW, (short) 49, Runtime.castToRef(languages.ADL("FreeWare"), String.class));
        } else {
            TripleCenter((short) ChaosGraphics.SW, (short) 40, Runtime.castToRef(languages.ADL("Demo version - limited playtime"), String.class));
            TripleCenter((short) ChaosGraphics.SW, (short) 49, Runtime.castToRef(languages.ADL("Register to get a full version"), String.class));
        }
        TripleCenter((short) ChaosGraphics.SW, (short) 63, Runtime.castToRef(languages.ADL("Use the following keys to move:"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 72, Runtime.castToRef(languages.ADL("[7] [8] [9]"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 81, Runtime.castToRef(languages.ADL("[4] [5] [6]"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 90, Runtime.castToRef(languages.ADL("[1] [2] [3]"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 100, Runtime.castToRef(languages.ADL("Use [SPACE] to fire with the Gun."), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 110, Runtime.castToRef(languages.ADL("To use another weapon, you must first"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 119, Runtime.castToRef(languages.ADL("choose yourself the key to use as follow"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 128, Runtime.castToRef(languages.ADL("-Select the weapon with [+] / [-]"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 137, Runtime.castToRef(languages.ADL("-Choose the key ([F1]..[F8] or [a]..[z])"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 146, Runtime.castToRef(languages.ADL("to use or press [SPACE] to cancel."), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 156, Runtime.castToRef(languages.ADL("To make a bomb with any weapon, hold"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 165, Runtime.castToRef(languages.ADL("[SHIFT] while firing or press & release"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 174, Runtime.castToRef(languages.ADL("[+] & [-] simultaneously before you fire"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 184, Runtime.castToRef(languages.ADL("Warning: bombs and bullets are limited."), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 195, Runtime.castToRef(languages.ADL("If you have a joystick / joypad,"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 204, Runtime.castToRef(languages.ADL("[SPACE], [F1]..[F3] = button 1 to 4,"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 213, Runtime.castToRef(languages.ADL("[+], [-] = forward, reverse."), String.class));
        chaosActions.WhiteFade();
        DrawStart();
        UpdateScreen();
    }

    private final Runnable DrawStartScreen_ref = this::DrawStartScreen;

    private void RefreshPlay() {
        graphics.SetArea(chaosGraphics.mainArea);
        graphics.SetBuffer(true, true);
        chaosPlayer.DrawInfos(true);
        chaosGraphics.UpdateAnim();
        graphics.UpdateArea();
        graphics.SetBuffer(true, false);
    }

    private final Runnable RefreshPlay_ref = this::RefreshPlay;

    private Runtime.IRef<String> GetChaosName(short level) {
        // VAR
        short d = 0;

        d = (short) (level / 10);
        if (d == 0) {
            levelName = Runtime.setChar(levelName, 0, (char) (48 + level));
            levelName = Runtime.setChar(levelName, 1, ((char) 0));
        } else {
            levelName = Runtime.setChar(levelName, 0, (char) (48 + d % 10));
            levelName = Runtime.setChar(levelName, 1, (char) (48 + level % 10));
            levelName = Runtime.setChar(levelName, 2, ((char) 0));
        }
        return new Runtime.FieldRef<>(this::getLevelName, this::setLevelName);
    }

    private Runtime.IRef<String> GetCastleName(short level) {
        switch (level) {
            case 1 -> {
                return Runtime.castToRef(languages.ADL("Entry"), String.class);
            }
            case 2 -> {
                return Runtime.castToRef(languages.ADL("Groove"), String.class);
            }
            case 3 -> {
                return Runtime.castToRef(languages.ADL("Garden"), String.class);
            }
            case 4 -> {
                return Runtime.castToRef(languages.ADL("Lake"), String.class);
            }
            case 5 -> {
                return Runtime.castToRef(languages.ADL("Site"), String.class);
            }
            case 6 -> {
                return Runtime.castToRef(languages.ADL("GhostCastle"), String.class);
            }
            case 7 -> {
                return Runtime.castToRef(languages.ADL("Machinery"), String.class);
            }
            case 8 -> {
                return Runtime.castToRef(languages.ADL("Ice Rink"), String.class);
            }
            case 9 -> {
                return Runtime.castToRef(languages.ADL("Factory"), String.class);
            }
            case 10 -> {
                return Runtime.castToRef(languages.ADL("Labyrinth"), String.class);
            }
            case 11 -> {
                return Runtime.castToRef(languages.ADL("Rooms"), String.class);
            }
            case 12 -> {
                return Runtime.castToRef(languages.ADL("Yard"), String.class);
            }
            case 13 -> {
                return Runtime.castToRef(languages.ADL("Antarctica"), String.class);
            }
            case 14 -> {
                return Runtime.castToRef(languages.ADL("Forest"), String.class);
            }
            case 15 -> {
                return Runtime.castToRef(languages.ADL(" Castle "), String.class);
            }
            case 16 -> {
                return Runtime.castToRef(languages.ADL("Lights"), String.class);
            }
            case 17 -> {
                return Runtime.castToRef(languages.ADL("Plain"), String.class);
            }
            case 18 -> {
                return Runtime.castToRef(languages.ADL("Underwater"), String.class);
            }
            case 19 -> {
                return Runtime.castToRef(languages.ADL("Assembly"), String.class);
            }
            case 20 -> {
                return Runtime.castToRef(languages.ADL("Jungle"), String.class);
            }
            default -> throw new RuntimeException("Unhandled CASE value " + level);
        }
    }

    private Runtime.IRef<String> GetSpecialName(short level) {
        if (level == 24)
            return Runtime.castToRef(languages.ADL("ChaosCastle"), String.class);
        else if (level % 8 == 0)
            return Runtime.castToRef(languages.ADL("Winter"), String.class);
        else if (level % 4 == 0)
            return Runtime.castToRef(languages.ADL("Graveyard"), String.class);
        else if (level % 2 == 0)
            return Runtime.castToRef(languages.ADL("Autumn"), String.class);
        else
            return Runtime.castToRef(languages.ADL("Baby Aliens"), String.class);
    }

    private Runtime.IRef<String> GetFamilyName(short level) {
        switch (level) {
            case 1 -> {
                return Runtime.castToRef(languages.ADL("Brother Alien"), String.class);
            }
            case 2 -> {
                return Runtime.castToRef(languages.ADL("Sister Alien"), String.class);
            }
            case 3 -> {
                return Runtime.castToRef(languages.ADL("Mother Alien"), String.class);
            }
            case 4 -> {
                return Runtime.castToRef(languages.ADL("FATHER ALIEN"), String.class);
            }
            case 5 -> {
                return Runtime.castToRef(languages.ADL("KIDS"), String.class);
            }
            case 6 -> {
                return Runtime.castToRef(languages.ADL("PARENTS"), String.class);
            }
            case 7 -> {
                return Runtime.castToRef(languages.ADL("MASTER ALIEN"), String.class);
            }
            case 8 -> {
                return Runtime.castToRef(languages.ADL("MASTER ALIEN 2"), String.class);
            }
            case 9 -> {
                return Runtime.castToRef(languages.ADL("MASTERS"), String.class);
            }
            case 10 -> {
                return Runtime.castToRef(languages.ADL("* FINAL *"), String.class);
            }
            default -> throw new RuntimeException("Unhandled CASE value " + level);
        }
    }

    private void DrawMakingScreen() {
        // VAR
        Runtime.IRef<String> str = null;
        Runtime.IRef<String> st2 = null;

        ResetGraphics();
        graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) ChaosGraphics.SW), chaosGraphics.H.invoke((short) ChaosGraphics.SH));
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 27));
        p1 = 7;
        p2 = 3;
        p3 = 4;
        TripleCenter((short) ChaosGraphics.PW, (short) 0, Runtime.castToRef(languages.ADL("Zone:"), String.class));
        switch (chaosBase.zone) {
            case Chaos -> str = Runtime.castToRef(languages.ADL("Chaos"), String.class);
            case Castle -> str = Runtime.castToRef(languages.ADL("Castle"), String.class);
            case Family -> str = Runtime.castToRef(languages.ADL("Family"), String.class);
            case Special -> str = Runtime.castToRef(languages.ADL("* BONUS *"), String.class);
            default -> throw new RuntimeException("Unhandled CASE value " + chaosBase.zone);
        }
        TripleCenter((short) ChaosGraphics.PW, (short) 30, str);
        TripleCenter((short) ChaosGraphics.PW, (short) 120, Runtime.castToRef(languages.ADL("Level:"), String.class));
        if (chaosBase.zone == Zone.Family) {
            graphics.SetTextSize(chaosGraphics.H.invoke((short) 18));
            str = GetFamilyName(chaosBase.level[Zone.Family.ordinal()]);
        } else if (chaosBase.zone == Zone.Castle) {
            str = GetCastleName(chaosBase.level[Zone.Castle.ordinal()]);
        } else if (chaosBase.zone == Zone.Special) {
            graphics.SetTextSize(chaosGraphics.H.invoke((short) 18));
            str = GetSpecialName(chaosBase.level[Zone.Special.ordinal()]);
        } else {
            str = GetChaosName(chaosBase.level[chaosBase.zone.ordinal()]);
        }
        TripleCenter((short) ChaosGraphics.PW, (short) 150, str);
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 9));
        str = Runtime.castToRef(languages.ADL("Target:"), String.class);
        TripleCenter((short) ChaosGraphics.PW, (short) 60, str);
        TripleCenter((short) ChaosGraphics.PW, (short) 180, str);
        switch (chaosBase.zone) {
            case Chaos -> {
                str = Runtime.castToRef(languages.ADL("Get enough $ to enter"), String.class);
                st2 = Runtime.castToRef(languages.ADL("Castle zone"), String.class);
            }
            case Castle -> {
                str = Runtime.castToRef(languages.ADL("Get enough £ to enter"), String.class);
                st2 = Runtime.castToRef(languages.ADL("Family zone"), String.class);
            }
            case Family -> {
                str = Runtime.castToRef(languages.ADL(""), String.class);
                st2 = Runtime.castToRef(languages.ADL(""), String.class);
            }
            case Special -> {
                str = Runtime.castToRef(languages.ADL("Get bonuses"), String.class);
                st2 = Runtime.castToRef(memory.ADS("  "), String.class);
            }
            default -> throw new RuntimeException("Unhandled CASE value " + chaosBase.zone);
        }
        TripleCenter((short) ChaosGraphics.PW, (short) 70, str);
        TripleCenter((short) ChaosGraphics.PW, (short) 80, st2);
        switch (chaosBase.zone) {
            case Chaos -> {
                str = Runtime.castToRef(languages.ADL("Destroy everything"), String.class);
            }
            case Castle, Special -> {
                str = Runtime.castToRef(languages.ADL("Find the exit"), String.class);
            }
            case Family -> {
                if (chaosBase.level[Zone.Family.ordinal()] > 6)
                    str = Runtime.castToRef(languages.ADL("Shoot them up"), String.class);
                else
                    str = Runtime.castToRef(languages.ADL("Destroy it"), String.class);
            }
            default -> throw new RuntimeException("Unhandled CASE value " + chaosBase.zone);
        }
        TripleCenter((short) ChaosGraphics.PW, (short) 190, str);
        chaosPlayer.DrawInfos(false);
        if (gameMade)
            DrawStart();
        UpdateScreen();
    }

    private final Runnable DrawMakingScreen_ref = this::DrawMakingScreen;

    public void MakingScreen() {
        if ((chaosBase.score > 2000) && !registration.registered)
            checks.Terminate();
        gameMade = false;
        chaosActions.FadeOut();
        DrawMakingScreen();
        chaosActions.FadeIn();
        chaosInterface.Refresh = DrawMakingScreen_ref;
        chaosLevels.MakeCastle();
        gameMade = true;
        chaosBase.gameStat = GameStat.Playing;
        DrawMakingScreen();
        chaosInterface.EnableFileMenus();
        WaitStart();
        graphics.SetPen(0);
        chaosInterface.Refresh = RefreshPlay_ref;
        if (chaosBase.gameStat != GameStat.Playing)
            return;
        graphics.SetBuffer(true, true);
        chaosPlayer.DrawInfos(true);
        chaosGraphics.UpdateAnim();
    }

    private void WeaponToPrice(Weapon w, /* VAR */ Runtime.IRef<Short> bullet, /* VAR */ Runtime.IRef<Short> bombd, /* VAR */ Runtime.IRef<Short> bombs) {
        bullet.set(chaosWeapon.GetBulletPrice(w));
        bombs.set((short) 0);
        switch (w) {
            case GUN -> {
                bombd.set((short) 5);
            }
            case FB -> {
                bombd.set((short) 0);
                bombs.set((short) 90);
            }
            case LASER -> {
                bombd.set((short) 40);
            }
            case BUBBLE -> {
                bombd.set((short) 30);
            }
            case FIRE -> {
                bombd.set((short) 25);
            }
            case STAR -> {
                bombd.set((short) 60);
            }
            case BALL -> {
                bombd.set((short) 20);
            }
            case GRENADE -> {
                bombd.set((short) 10);
            }
            default -> throw new RuntimeException("Unhandled CASE value " + w);
        }
    }

    private Runtime.IRef<String> ItemToPrice(int item, /* VAR */ Runtime.IRef<Short> itemD, /* VAR */ Runtime.IRef<Short> itemS) {
        itemD.set((short) 0);
        itemS.set((short) 0);
        switch (item) {
            case 0 -> {
                itemD.set((short) 15);
                return Runtime.castToRef(memory.ADS("15$"), String.class);
            }
            case 2 -> {
                itemS.set((short) 150);
                return Runtime.castToRef(memory.ADS("150£"), String.class);
            }
            case 3 -> {
                itemD.set((short) 100);
                return Runtime.castToRef(memory.ADS("100$"), String.class);
            }
            default -> {
                return null;
            }
        }
    }

    private boolean DrawWeapon_SetPens(boolean xc, boolean av, short d, short s, Weapon w, /* VAR */ Runtime.IRef<Boolean> weaponOk) {
        // VAR
        boolean tst1 = false;
        boolean tst2 = false;

        tst1 = (chaosBase.nbDollar >= d) 
                && (chaosBase.nbSterling >= s)
                && av 
                && chaosBase.weaponAttr[w.ordinal()].power > 0
                && (available[w.ordinal()] > 0);
        tst2 = up && (w == yw) && xc;
        if (chaosGraphics.color) {
            if (tst1) {
                if (tst2)
                    p1 = 2;
                else
                    p1 = 7;
                weaponOk.set(true);
            } else {
                if (tst2)
                    p1 = 1;
                else
                    p1 = 0;
            }
        } else {
            p2 = 1;
            if (tst1) {
                p3 = 1;
                weaponOk.set(true);
            } else {
                p3 = 0;
            }
            if (tst2)
                p1 = 1;
            else
                p1 = 0;
        }
        return tst1;
    }

    private void DrawWeapon(Weapon w) {
        // VAR
        short y = 0;
        Runtime.IRef<String> str = null;
        Runtime.Ref<Short> bullet = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> bombd = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> bombs = new Runtime.Ref<>((short) 0);
        short val = 0;
        Runtime.Ref<String> ch = new Runtime.Ref<>("");
        Runtime.Ref<Boolean> weaponOk = new Runtime.Ref<>(false);

        y = (short) (w.ordinal() * 10 + 60);
        str = chaosPlayer.WeaponToStr(w);
        WeaponToPrice(w, bullet, bombd, bombs);
        weaponOk.set(false);
        if (available[w.ordinal()] > 0) {
            Runtime.setChar(ch, 0, (char) (48 + bullet.get()));
            Runtime.setChar(ch, 1, '$');
            Runtime.setChar(ch, 2, ((char) 0));
        } else {
            ch.set("NA");
            graphics.SetPen(0);
            graphics.FillRect(chaosGraphics.W.invoke((short) x1), chaosGraphics.H.invoke(y), chaosGraphics.W.invoke((short) ChaosGraphics.PW), chaosGraphics.H.invoke((short) (y + 10)));
        }
        wActive[w.ordinal()][0] = DrawWeapon_SetPens(!bomb, true, bullet.get(), (short) 0, w, weaponOk);
        Center((short) x1, y, (short) (ChaosGraphics.PW / 4), ch);
        val = bombd.get();
        if (available[w.ordinal()] > 0) {
            if (bombs.get() != 0) {
                Runtime.setChar(ch, 2, '£');
                val = bombs.get();
            } else {
                Runtime.setChar(ch, 2, '$');
            }
            Runtime.setChar(ch, 0, (char) (48 + val / 10));
            Runtime.setChar(ch, 1, (char) (48 + val % 10));
            Runtime.setChar(ch, 3, ((char) 0));
        } else {
            ch.set("---");
        }
        wActive[w.ordinal()][1] = DrawWeapon_SetPens(bomb, true, bombd.get(), bombs.get(), w, weaponOk);
        Center((short) x2, y, (short) (ChaosGraphics.PW / 4), ch);
        weaponOk.set(DrawWeapon_SetPens(true, weaponOk.get(), (short) 0, (short) 0, w, weaponOk));
        TripleWrite((short) 0, y, str);
    }

    private void UpdateWeapon(Weapon w) {
        // VAR
        Runtime.Ref<Short> bullet = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> bombd = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> bombs = new Runtime.Ref<>((short) 0);
        boolean t1 = false;
        boolean t2 = false;
        boolean t3 = false;

        WeaponToPrice(w, bullet, bombd, bombs);
        t1 = (chaosBase.nbDollar >= bullet.get());
        t2 = (chaosBase.nbDollar >= bombd.get());
        t3 = (chaosBase.nbSterling >= bombs.get());
        t3 = t2 && t3;
        if ((wActive[w.ordinal()][0] != t1) || (available[w.ordinal()] == 0) || (wActive[w.ordinal()][1] != t3))
            DrawWeapon(w);
    }

    private void BuyWeapon(boolean ten) {
        // VAR
        Runtime.Ref<Short> bullet = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> bombd = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> bombs = new Runtime.Ref<>((short) 0);
        byte bS = 0;
        byte bD = 0;
        byte bl = 0;
        Runtime.Ref<Short> addBullet = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> addBomb = new Runtime.Ref<>((short) 0);

        if (!wActive[yw.ordinal()][Runtime.ord(bomb)]) {
            if (available[yw.ordinal()] > 0)
                chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sPoubelle.ordinal()]);
            else
                chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sCymbale.ordinal()]);
            return;
        }
        WeaponToPrice(yw, bullet, bombd, bombs);
        available[yw.ordinal()]--;
        bl = (byte) (short) bullet.get();
        bS = (byte) (short) bombs.get();
        bD = (byte) (short) bombd.get();
        if (bomb) {
            addBullet.set((short) 0);
            addBomb.set((short) 1);
            chaosPlayer.AddToWeapon(chaosBase.mainPlayer, yw, addBullet, addBomb);
            if (addBomb.get() == 0) {
                chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sMoney.ordinal()]);
                chaosPlayer.AddMoney(chaosBase.mainPlayer, (short) -bD, (short) -bS);
            } else {
                chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sHurryUp.ordinal()]);
            }
        } else {
            addBullet.set((short) 1);
            addBomb.set((short) 0);
            chaosPlayer.AddToWeapon(chaosBase.mainPlayer, yw, addBullet, addBomb);
            if (addBullet.get() == 0) {
                chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sMoney.ordinal()]);
                chaosPlayer.AddMoney(chaosBase.mainPlayer, (short) -bl, (short) 0);
            } else {
                chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sHurryUp.ordinal()]);
            }
        }
    }

    private void DrawItem(int item) {
        // VAR
        Runtime.IRef<String> str = null;
        Runtime.IRef<String> txt1 = null;
        Runtime.IRef<String> txt2 = null;
        short py = 0;
        Runtime.Ref<Short> itemD = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> itemS = new Runtime.Ref<>((short) 0);
        boolean test = false;

        str = ItemToPrice(item, itemD, itemS);
        test = (chaosBase.nbDollar >= itemD.get()) && (chaosBase.nbSterling >= itemS.get());
        if (item == 1)
            test = (chaosBase.specialStage > 0);
        dActive[item] = test;
        if (chaosGraphics.color) {
            if (!up && (yd == item)) {
                if (test)
                    p1 = 2;
                else
                    p1 = 1;
            } else {
                if (test)
                    p1 = 7;
                else
                    p1 = 0;
            }
        } else {
            p2 = 1;
            if (test)
                p3 = 1;
            else
                p3 = 0;
            if (!up && (yd == item))
                p1 = 1;
            else
                p1 = 0;
        }
        txt2 = null;
        switch (item) {
            case 0 -> {
                py = 150;
                txt1 = Runtime.castToRef(languages.ADL("Extra Life"), String.class);
            }
            case 1 -> {
                py = 170;
                txt1 = Runtime.castToRef(languages.ADL("-> Bonus Level"), String.class);
            }
            case 2 -> {
                py = 180;
                txt1 = Runtime.castToRef(languages.ADL("-> Family: "), String.class);
                txt2 = GetFamilyName(chaosBase.level[Zone.Family.ordinal()]);
            }
            case 3 -> {
                py = 190;
                txt1 = Runtime.castToRef(languages.ADL("-> Castle: "), String.class);
                txt2 = GetCastleName(chaosBase.level[Zone.Castle.ordinal()]);
            }
            case 4 -> {
                py = 200;
                txt1 = Runtime.castToRef(languages.ADL("-> Chaos level "), String.class);
                txt2 = GetChaosName(chaosBase.level[Zone.Chaos.ordinal()]);
            }
            default -> throw new RuntimeException("Unhandled CASE value " + item);
        }
        TripleWrite((short) 0, py, txt1);
        if (txt2 != null)
            TripleWrite((short) (graphics.TextWidth(txt1) / chaosGraphics.mulS), py, txt2);
        if (str != null)
            TripleWrite((short) (ChaosGraphics.PW - graphics.TextWidth(str) / chaosGraphics.mulS - 4), py, str);
    }

    private void UpdateItem(int item) {
        // VAR
        Runtime.IRef<String> str = null;
        Runtime.Ref<Short> itemD = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> itemS = new Runtime.Ref<>((short) 0);
        boolean t = false;

        str = ItemToPrice(item, itemD, itemS);
        t = ((chaosBase.nbDollar >= itemD.get()) && (chaosBase.nbSterling >= itemS.get()));
        if (dActive[item] != t)
            DrawItem(item);
    }

    private boolean BuyItem() {
        // VAR
        Runtime.IRef<String> str = null;
        Runtime.Ref<Short> itemD = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> itemS = new Runtime.Ref<>((short) 0);
        short iD = 0;
        short iS = 0;

        if (!dActive[yd]) {
            chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sPoubelle.ordinal()]);
            return false;
        }
        str = ItemToPrice(yd, itemD, itemS);
        iS = itemS.get();
        iD = itemD.get();
        chaosPlayer.AddMoney(chaosBase.mainPlayer, (short) -iD, (short) -iS);
        if (yd == 0) {
            chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sMoney.ordinal()]);
            chaosPlayer.AddLife(chaosBase.mainPlayer);
            return false;
        } else if (yd == 1) {
            if (chaosBase.specialStage > 0) {
                chaosBase.zone = Zone.Special;
                chaosBase.specialStage--;
                return true;
            } else {
                return false;
            }
        } else {
            if (yd == 2)
                chaosBase.zone = Zone.Family;
            else if (yd == 3)
                chaosBase.zone = Zone.Castle;
            else
                chaosBase.zone = Zone.Chaos;
            return true;
        }
    }

    private void DrawTime() {
        // VAR
        Runtime.Ref<String> hour = new Runtime.Ref<>("");

        graphics.SetTextSize(chaosGraphics.H.invoke((short) 9));
        graphics.SetPen(0);
        graphics.FillRect((short) 0, chaosGraphics.H.invoke((short) 30), chaosGraphics.W.invoke((short) ChaosGraphics.PW), chaosGraphics.H.invoke((short) 40));
        clock.GetCurrentTime(new Runtime.FieldRef<>(this::getH, this::setH), new Runtime.FieldRef<>(this::getM, this::setM), new Runtime.FieldRef<>(this::getS, this::setS));
        om = m;
        Runtime.setChar(hour, 0, (char) (h / 10 + 48));
        Runtime.setChar(hour, 1, (char) (h % 10 + 48));
        Runtime.setChar(hour, 3, (char) (m / 10 + 48));
        Runtime.setChar(hour, 4, (char) (m % 10 + 48));
        Runtime.setChar(hour, 2, ':');
        Runtime.setChar(hour, 5, ((char) 0));
        p1 = 0;
        p2 = 3;
        p3 = 4;
        TripleWrite((short) (ChaosGraphics.PW - graphics.TextWidth(hour) - chaosGraphics.mulS), (short) 30, hour);
        clock.StartTime(chaosActions.time);
        clock.TimeEvent(chaosActions.time, ChaosBase.Period * 59);
    }

    private void DrawShopScreen() {
        // VAR
        String str = "";
        Weapon yw = Weapon.GUN;
        int yd = 0;
        int rnd = 0;

        ResetGraphics();
        chaosPlayer.DrawInfos(false);
        ResetGraphics();
        graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) ChaosGraphics.PW), chaosGraphics.H.invoke((short) ChaosGraphics.PH));
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 18));
        p1 = 7;
        p2 = 3;
        p3 = 4;
        TripleCenter((short) ChaosGraphics.PW, (short) 10, Runtime.castToRef(languages.ADL("Chaos' Shop"), String.class));
        DrawTime();
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 9));
        if (shopClosed) {
            Center((short) 0, (short) 50, (short) ChaosGraphics.PW, Runtime.castToRef(languages.ADL("Sorry, the shop is closed."), String.class));
            rnd = trigo.RND() % 8;
            switch (rnd) {
                case 0 -> str = "conflagration";
                case 1 -> str = "inventory / renovation";
                case 2 -> str = "housebreaking";
                case 3 -> str = "Father alien's bad mood";
                case 4 -> str = "leave";
                case 5 -> str = "power failure";
                case 6 -> str = "software failure";
                case 7 -> str = "flood";
                default -> throw new RuntimeException("Unhandled CASE value " + rnd);
            }
            Center((short) 0, (short) 70, (short) ChaosGraphics.PW, Runtime.castToRef(languages.ADL("Cause:"), String.class));
            Center((short) 0, (short) 80, (short) ChaosGraphics.PW, Runtime.castToRef(languages.ADL(str), String.class));
            DrawItem(4);
        } else {
            graphics.SetTextMode(EnumSet.of(TextModes.italic));
            p1 = 7;
            TripleWrite((short) 0, (short) 50, Runtime.castToRef(languages.ADL("Item:"), String.class));
            Center((short) x1, (short) 50, (short) (ChaosGraphics.PW / 4), Runtime.castToRef(languages.ADL("Bullet"), String.class));
            Center((short) x2, (short) 50, (short) (ChaosGraphics.PW / 4), Runtime.castToRef(languages.ADL("Bomb"), String.class));
            graphics.SetTextMode(EnumSet.noneOf(TextModes.class));
            for (int _yw = 0; _yw < Weapon.values().length; _yw++) {
                yw = Weapon.values()[_yw];
                DrawWeapon(yw);
            }
            for (yd = 0; yd <= 4; yd++) {
                DrawItem(yd);
            }
        }
        UpdateScreen();
    }

    private final Runnable DrawShopScreen_ref = this::DrawShopScreen;

    public void ShopScreen() {
        // VAR
        Runtime.RangeSet joy = new Runtime.RangeSet(Memory.SET16_r); /* WRT */
        Runtime.RangeSet fj = new Runtime.RangeSet(Memory.SET16_r);
        Input.Event event = new Input.Event(); /* WRT */
        Input.Event fe = new Input.Event();
        int item = 0;
        Weapon weapon = Weapon.GUN;

        if (chaosBase.gameStat != GameStat.Finish)
            return;
        bomb = false;
        up = false;
        yw = Weapon.GUN /* MIN(Weapon) */;
        yd = 4;
        for (int _weapon = 0; _weapon < Weapon.values().length; _weapon++) {
            weapon = Weapon.values()[_weapon];
            available[weapon.ordinal()] = (short) (20 - chaosBase.difficulty * 2 + trigo.RND() % (32 - 2 * chaosBase.difficulty));
        }
        shopClosed = (chaosBase.difficulty >= 5) && (trigo.RND() % 48 == 0);
        chaosBase.zone = Zone.Chaos;
        chaosActions.FadeOut();
        DrawShopScreen();
        chaosActions.FadeIn();
        chaosInterface.Refresh = DrawShopScreen_ref;
        UpdateScreen();
        graphics.SetBuffer(true, true);
        chaosInterface.EnableFileMenus();
        WaitRelease();
        while (true) {
            input.SetBusyStat((short) Input.statWaiting);
            input.WaitEvent();
            input.GetEvent(event);
            fe.copyFrom(event);
            joy.copyFrom(input.GetStick());
            fj.copyFrom(joy);
            chaosPlayer.CheckSelect(event, joy, true);
            graphics.SetBuffer(true, true);
            chaosGraphics.SetOrigin((short) 0, (short) 0);
            if (shopClosed)
                joy.copyFrom(Runtime.RangeSet.minus(joy, new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyLeft, Input.JoyRight, Input.JoyUp, Input.JoyDown)));
            if (!Runtime.RangeSet.mul(joy, new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyLeft, Input.JoyRight, Input.JoyUp, Input.JoyDown, Input.JoyForward, Input.JoyReverse)).equals(new Runtime.RangeSet(Memory.SET16_r))) {
                up = !up;
                if (!up)
                    DrawWeapon(yw);
                else
                    DrawItem(yd);
                up = !up;
            }
            if (joy.contains(Input.JoyUp)) {
                if (up) {
                    if (yw == Weapon.GUN /* MIN(Weapon) */) {
                        up = false;
                        yd = 4;
                    } else {
                        yw = Runtime.prev(yw);
                    }
                } else {
                    if (yd == 0) {
                        up = true;
                        yw = Weapon.GRENADE /* MAX(Weapon) */;
                    } else {
                        yd--;
                    }
                }
            } else if ((joy.contains(Input.JoyDown))) {
                if (up) {
                    if (yw == Weapon.GRENADE /* MAX(Weapon) */) {
                        up = false;
                        yd = 0;
                    } else {
                        yw = Runtime.next(yw);
                    }
                } else {
                    if (yd == 4) {
                        up = true;
                        yw = Weapon.GUN /* MIN(Weapon) */;
                    } else {
                        yd++;
                    }
                }
            } else if (!Runtime.RangeSet.mul(joy, new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyLeft, Input.JoyRight)).equals(new Runtime.RangeSet(Memory.SET16_r))) {
                bomb = !bomb;
            } else if (!Runtime.RangeSet.mul(joy, new Runtime.RangeSet(Memory.SET16_r).with(Input.Joy1, Input.Joy3, Input.Joy4, Input.JoyPause)).equals(new Runtime.RangeSet(Memory.SET16_r))) {
                if (up) {
                    BuyWeapon(joy.contains(Input.Joy3));
                } else {
                    if (BuyItem()) {
                        if (chaosBase.zone == Zone.Family) {
                            if (chaosBase.level[Zone.Family.ordinal()] == 10)
                                chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sCasserole.ordinal()]);
                            else
                                chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sHa.ordinal()]);
                        } else if (chaosBase.zone == Zone.Special) {
                            chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.wJans.ordinal()]);
                        } else {
                            chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sCaisse.ordinal()]);
                        }
                        break;
                    }
                }
                for (int _weapon = 0; _weapon < Weapon.values().length; _weapon++) {
                    weapon = Weapon.values()[_weapon];
                    UpdateWeapon(weapon);
                }
                for (item = 0; item <= 4; item++) {
                    UpdateItem(item);
                }
                chaosPlayer.UpdateInfos();
                chaosGraphics.SetOrigin((short) 0, (short) 0);
                graphics.SetCopyMode(Graphics.cmTrans);
            }
            if (up)
                DrawWeapon(yw);
            else
                DrawItem(yd);
            clock.GetCurrentTime(new Runtime.FieldRef<>(this::getH, this::setH), new Runtime.FieldRef<>(this::getM, this::setM), new Runtime.FieldRef<>(this::getS, this::setS));
            if (m != om)
                DrawTime();
            if ((fe.type != Input.eNUL) || (!fj.equals(new Runtime.RangeSet(Memory.SET16_r))))
                UpdateScreen();
            graphics.SetBuffer(true, false);
            chaosInterface.CommonEvent(event);
            if (event.type == Input.eTIMER)
                WaitRelease();
            if (chaosBase.gameStat != GameStat.Finish)
                break;
        }
        chaosPlayer.UpdateInfos();
        chaosGraphics.SetOrigin((short) 0, (short) 0);
        UpdateScreen();
    }

    private void DrawStatistics_ShowDecor() {
        // VAR
        Runtime.IRef<String> str = null;
        short x = 0;
        short y = 0;
        short dx = 0;
        short dy = 0;
        short sx = 0;
        short sy = 0;
        short w = 0;
        int p = 0;
        int red = 0;
        int c = 0;
        boolean wall = false;
        boolean lwall = false;

        if (chaosGraphics.color) {
            p = 8;
        } else {
            p = 1;
            graphics.SetCopyMode(Graphics.cmCopy);
            graphics.SetPen(0);
            graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) ChaosGraphics.PW), chaosGraphics.H.invoke((short) ChaosGraphics.PH));
        }
        graphics.SetCopyMode(Graphics.cmXor);
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 18));
        graphics.SetPen(p);
        if (chaosBase.zone == Zone.Castle)
            str = GetCastleName(chaosBase.level[Zone.Castle.ordinal()]);
        else
            str = GetSpecialName(chaosBase.level[Zone.Special.ordinal()]);
        w = (short) (graphics.TextWidth(str) / chaosGraphics.mulS);
        dx = (short) ((ChaosGraphics.PW - w) / 2);
        chaosGraphics.WriteAt(chaosGraphics.X.invoke((short) (dx + 1)), chaosGraphics.Y.invoke((short) 21), str);
        chaosGraphics.WriteAt(chaosGraphics.X.invoke(dx), chaosGraphics.Y.invoke((short) 20), str);
        str = Runtime.castToRef(languages.ADL("Post Mortem Map"), String.class);
        w = (short) (graphics.TextWidth(str) / chaosGraphics.mulS);
        dx = (short) ((ChaosGraphics.PW - w) / 2);
        chaosGraphics.WriteAt(chaosGraphics.X.invoke((short) (dx + 1)), chaosGraphics.Y.invoke((short) 41), str);
        chaosGraphics.WriteAt(chaosGraphics.X.invoke(dx), chaosGraphics.Y.invoke((short) 40), str);
        graphics.SetTextSize((short) 9);
        graphics.SetPen(p);
        dx = (short) ((ChaosGraphics.PW - chaosGraphics.castleWidth) / 2);
        dy = (short) ((ChaosGraphics.PH - 40 - chaosGraphics.castleHeight) / 2 + 40);
        for (y = 0; y < chaosGraphics.castleHeight; y++) {
            sx = 0;
            lwall = false;
            sy = (short) (y + dy);
            for (x = 0; x < chaosGraphics.castleWidth; x++) {
                wall = chaosGraphics.castle[y][x] >= ChaosGraphics.NbClear;
                if (wall) {
                    if (!lwall)
                        sx = x;
                } else {
                    if (lwall)
                        graphics.FillRect(chaosGraphics.W.invoke((short) (sx + dx)), chaosGraphics.H.invoke(sy), chaosGraphics.W.invoke((short) (x + dx)), chaosGraphics.H.invoke((short) (sy + 1)));
                }
                lwall = wall;
            }
            if (lwall)
                graphics.FillRect(chaosGraphics.W.invoke((short) (sx + dx)), chaosGraphics.H.invoke(sy), chaosGraphics.W.invoke((short) (chaosGraphics.castleWidth + dx)), chaosGraphics.H.invoke((short) (sy + 1)));
        }
        graphics.SetCopyMode(Graphics.cmCopy);
        if (chaosBase.water)
            red = 70;
        else
            red = 216;
        if (chaosGraphics.color) {
            for (c = 0; c <= 7; c++) {
                graphics.SetPalette((short) c, (short) 0, (short) 0, (short) 0);
            }
            for (c = 8; c <= 15; c++) {
                graphics.SetPalette((short) c, (short) red, (short) 152, (short) 0);
            }
            for (c = 0; c <= 7; c++) {
                chaosGraphics.palette[c + 8].copyFrom(chaosGraphics.palette[c]);
            }
        }
        UpdateScreen();
        WaitRelease();
        input.SetBusyStat((short) Input.statWaiting);
        do {
            input.FlushEvents();
            input.WaitEvent();
        } while (input.GetStick().equals(new Runtime.RangeSet(Memory.SET16_r)));
        if (chaosGraphics.color)
            chaosActions.FadeFrom((short) red, (short) 152, (short) 0);
        chaosInterface.Refresh = DrawStatistics_ref;
        DrawStatistics();
    }

    private void DrawStatistics_DrawStatistic(short y, Runtime.IRef<String> str, ChaosBase.Statistic stat, short add, int x1, int x2, int x3, /* VAR */ Runtime.IRef<Short> cnt, /* VAR */ Runtime.IRef<Integer> total) {
        // VAR
        long ratio = 0L;
        long div = 0L;

        TripleWrite((short) 0, y, str);
        TripleCard((short) x1, y, stat.total);
        TripleCard((short) x2, y, stat.done);
        ratio = stat.done;
        ratio = ratio * 100;
        if (stat.total != 0) {
            div = stat.total;
            ratio = ratio / div;
            if (ratio > 100)
                ratio = 100;
        } else {
            ratio = 0;
        }
        total.inc(ratio);
        TripleCard((short) x3, y, ratio);
        if (ratio >= 75) {
            str = Runtime.castToRef(languages.ADL("Good"), String.class);
            p1 = 7;
            chaosBase.score += chaosBase.difficulty * 10;
            cnt.inc(add);
        } else {
            str = Runtime.castToRef(languages.ADL("Bad"), String.class);
            p1 = 0;
        }
        TripleWrite((short) (ChaosGraphics.PW - graphics.TextWidth(str) / chaosGraphics.mulS - 4), y, str);
        p1 = 7;
    }

    private void DrawStatistics_Push(String string, /* VAR */ Runtime.IRef<Short> cnt) {
        // VAR
        String text = "";
        short c = 0;

        if (cnt.get() == 0) {
            for (c = 0; c <= (string.length() - 1); c++) {
                text = Runtime.setChar(text, c, Runtime.getChar(string, c));
            }
            text = Runtime.setChar(text, (string.length() - 1) + 1, ((char) 0));
            p1 = 4;
            p2 = 3;
            p3 = 6;
            TripleWrite((short) 16, (short) 180, Runtime.castToRef(languages.ADL(text), String.class));
            p1 = 7;
            p2 = 3;
            p3 = 4;
        }
        cnt.dec();
    }

    private void DrawStatistics() {
        // CONST
        final int x1 = ChaosGraphics.PW / 3;
        final int x2 = ChaosGraphics.PW * 25 / 48;
        final int x3 = ChaosGraphics.PW * 17 / 24;

        // VAR
        Runtime.Ref<Short> cnt = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Integer> total = new Runtime.Ref<>(0);
        Runtime.IRef<String> str = null;
        int c = 0;

        ResetGraphics();
        chaosPlayer.DrawInfos(false);
        ResetGraphics();
        for (c = 0; c <= 15; c++) {
            chaosGraphics.SetTrans((short) c, (short) 255);
        }
        graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) ChaosGraphics.PW), chaosGraphics.H.invoke((short) ChaosGraphics.PH));
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 27));
        p1 = 7;
        p2 = 3;
        p3 = 4;
        if (chaosBase.zone == Zone.Castle)
            str = GetCastleName(chaosBase.level[Zone.Castle.ordinal()]);
        else
            str = GetSpecialName(chaosBase.level[Zone.Special.ordinal()]);
        TripleCenter((short) ChaosGraphics.PW, (short) 0, str);
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 18));
        TripleCenter((short) ChaosGraphics.PW, (short) 30, Runtime.castToRef(languages.ADL("Statistics"), String.class));
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 9));
        graphics.SetTextMode(EnumSet.of(TextModes.italic));
        TripleWrite((short) 0, (short) 70, Runtime.castToRef(languages.ADL("What:"), String.class));
        TripleWrite((short) x1, (short) 70, Runtime.castToRef(languages.ADL("Total"), String.class));
        TripleWrite((short) x2, (short) 70, Runtime.castToRef(languages.ADL("Done"), String.class));
        TripleWrite((short) x3, (short) 70, Runtime.castToRef(languages.ADL("%"), String.class));
        graphics.SetTextMode(EnumSet.noneOf(TextModes.class));
        if (chaosBase.shoot.done > chaosBase.shoot.total)
            chaosBase.shoot.done = chaosBase.shoot.total;
        cnt.set((short) 0);
        total.set(0);
        DrawStatistics_DrawStatistic((short) 90, Runtime.castToRef(languages.ADL("Shoots:"), String.class), chaosBase.shoot, (short) 16, x1, x2, x3, cnt, total);
        DrawStatistics_DrawStatistic((short) 100, Runtime.castToRef(languages.ADL("Treasure:"), String.class), chaosBase.basics[BasicTypes.Bonus.ordinal() - BasicTypes.Bonus.ordinal()], (short) 8, x1, x2, x3, cnt, total);
        DrawStatistics_DrawStatistic((short) 120, Runtime.castToRef(languages.ADL("Mineral:"), String.class), chaosBase.basics[BasicTypes.Mineral.ordinal() - BasicTypes.Bonus.ordinal()], (short) 4, x1, x2, x3, cnt, total);
        DrawStatistics_DrawStatistic((short) 130, Runtime.castToRef(languages.ADL("Vegetal:"), String.class), chaosBase.basics[BasicTypes.Vegetal.ordinal() - BasicTypes.Bonus.ordinal()], (short) 2, x1, x2, x3, cnt, total);
        DrawStatistics_DrawStatistic((short) 140, Runtime.castToRef(languages.ADL("Animal:"), String.class), chaosBase.basics[BasicTypes.Animal.ordinal() - BasicTypes.Bonus.ordinal()], (short) 1, x1, x2, x3, cnt, total);
        TripleWrite((short) 0, (short) 170, Runtime.castToRef(languages.ADL("Advised career:"), String.class));
        DrawStatistics_Push("Milksop", cnt);
        DrawStatistics_Push("Vampire", cnt);
        DrawStatistics_Push("Dabbler", cnt);
        DrawStatistics_Push("Poisoner", cnt);
        DrawStatistics_Push("Somnambulist", cnt);
        DrawStatistics_Push("Yellow", cnt);
        DrawStatistics_Push("Road-sweeper", cnt);
        DrawStatistics_Push("Executioner", cnt);
        DrawStatistics_Push("Tourist", cnt);
        DrawStatistics_Push("Undertaker", cnt);
        DrawStatistics_Push("Beggar", cnt);
        DrawStatistics_Push("Butcher", cnt);
        DrawStatistics_Push("Salesman", cnt);
        DrawStatistics_Push("Criminal", cnt);
        DrawStatistics_Push("Thief", cnt);
        DrawStatistics_Push("Sorcerer", cnt);
        DrawStatistics_Push("Sharpshooter", cnt);
        DrawStatistics_Push("Hunter", cnt);
        DrawStatistics_Push("Peasant", cnt);
        DrawStatistics_Push("Killer", cnt);
        DrawStatistics_Push("Saboteur", cnt);
        DrawStatistics_Push("Soldier", cnt);
        DrawStatistics_Push("Fireman", cnt);
        DrawStatistics_Push("Incendiary", cnt);
        DrawStatistics_Push("Conjurer", cnt);
        DrawStatistics_Push("Ripper", cnt);
        DrawStatistics_Push("Drugdealer", cnt);
        DrawStatistics_Push("Tyrant", cnt);
        DrawStatistics_Push("Destroyer", cnt);
        DrawStatistics_Push("Out-law", cnt);
        DrawStatistics_Push("Devil", cnt);
        DrawStatistics_Push("Terrorist", cnt);
        if (total.get() == 500) {
            graphics.SetPen(0);
            graphics.FillRect(chaosGraphics.W.invoke((short) 16), chaosGraphics.H.invoke((short) 180), chaosGraphics.W.invoke((short) ChaosGraphics.PW), chaosGraphics.H.invoke((short) 190));
            cnt.set((short) 0);
            DrawStatistics_Push("> Nightmare <", cnt);
        }
        p1 = 2;
        if (((chaosBase.stages == 0) || chaosBase.password) && (chaosInterface.Refresh != DrawStatistics_ref))
            DrawStatistics_ShowDecor();
        ResetGraphics();
        TripleCenter((short) ChaosGraphics.PW, (short) 230, Runtime.castToRef(languages.ADL("Press [SPACE] to continue"), String.class));
        UpdateScreen();
    }

    private final Runnable DrawStatistics_ref = this::DrawStatistics;

    public void StatisticScreen() {
        // VAR
        int c = 0;

        if (chaosBase.gameStat != GameStat.Finish)
            return;
        chaosInterface.DisableFileMenus();
        input.SetBusyStat((short) Input.statBusy);
        if ((chaosBase.zone == Zone.Castle) || (chaosBase.zone == Zone.Special)) {
            DrawStatistics();
            chaosInterface.Refresh = DrawStatistics_ref;
            WaitStart();
        }
        chaosActions.FadeOut();
        if (chaosBase.water) {
            graphics.SetPen(0);
            graphics.SetCopyMode(Graphics.cmCopy);
            graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) ChaosGraphics.SW), chaosGraphics.H.invoke((short) ChaosGraphics.SH));
            chaosBase.water = false;
            chaosImages.InitPalette();
            for (c = 0; c <= 15; c++) {
                graphics.SetPalette((short) c, (short) 0, (short) 0, (short) 0);
            }
        }
        chaosInterface.EnableFileMenus();
    }

    private void DrawEndScreen() {
        ResetGraphics();
        graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) ChaosGraphics.SW), chaosGraphics.H.invoke((short) ChaosGraphics.SH));
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 18));
        p1 = 7;
        p2 = 3;
        p3 = 4;
        TripleCenter((short) ChaosGraphics.SW, (short) 10, Runtime.castToRef(languages.ADL("About this game"), String.class));
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 9));
        TripleCenter((short) ChaosGraphics.SW, (short) 50, Runtime.castToRef(languages.ADL("Minerals, Vegetals and Animals;"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 60, Runtime.castToRef(languages.ADL("or in short, Chaos Castle,"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 70, Runtime.castToRef(languages.ADL("has been designed and programmed"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 80, Runtime.castToRef(languages.ADL("in Modula-2 by"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 92, Runtime.castToRef(languages.ADL("Nicky"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 112, Runtime.castToRef(languages.ADL("Thanks to all my friends"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 122, Runtime.castToRef(languages.ADL("for a lot of ideas"), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 132, Runtime.castToRef(languages.ADL("and for play testing."), String.class));
        TripleCenter((short) ChaosGraphics.SW, (short) 155, Runtime.castToRef(languages.ADL("This piece of software is"), String.class));
        if (registration.registered) {
            TripleCenter((short) ChaosGraphics.SW, (short) 165, Runtime.castToRef(languages.ADL("registered to"), String.class));
            TripleCenter((short) ChaosGraphics.SW, (short) 180, registration.userName);
            TripleCenter((short) ChaosGraphics.SW, (short) 190, registration.userAddress);
            TripleCenter((short) ChaosGraphics.SW, (short) 200, registration.userLoc);
        } else {
            TripleCenter((short) ChaosGraphics.SW, (short) 165, Runtime.castToRef(languages.ADL("SHAREWARE"), String.class));
            TripleCenter((short) ChaosGraphics.SW, (short) 180, Runtime.castToRef(languages.ADL("I spent a lot of time making this game;"), String.class));
            TripleCenter((short) ChaosGraphics.SW, (short) 190, Runtime.castToRef(languages.ADL("If you like it, please register."), String.class));
            TripleCenter((short) ChaosGraphics.SW, (short) 200, Runtime.castToRef(languages.ADL("(more in the documentation)"), String.class));
        }
        UpdateScreen();
    }

    private final Runnable DrawEndScreen_ref = this::DrawEndScreen;

    private void UpdateTopScore(/* var */ ChaosInterface.TopScore topScore, int c, int s, int n) {
        // VAR
        Runtime.Ref<String> str = new Runtime.Ref<>("");
        short y = 0;

        graphics.SetBuffer(true, true);
        y = (short) (c * 10 + 50);
        graphics.SetPen(0);
        graphics.SetCopyMode(Graphics.cmTrans);
        graphics.FillRect((short) 0, chaosGraphics.H.invoke(y), chaosGraphics.W.invoke((short) ChaosGraphics.SW), chaosGraphics.H.invoke((short) (y + 10)));
        if (c == n) {
            p1 = 4;
            p2 = 3;
            p3 = 6;
        } else {
            p1 = 7;
            p2 = 3;
            p3 = 4;
        }
        if (c != s)
            p1 = 0;
        else if (!chaosGraphics.color)
            p1 = 1;
        if (c == 10)
            Runtime.setChar(str, 0, '1');
        else
            Runtime.setChar(str, 0, ' ');
        Runtime.setChar(str, 1, (char) (48 + c % 10));
        Runtime.setChar(str, 2, '.');
        Runtime.setChar(str, 3, ((char) 0));
        TripleWrite((short) 0, y, str);
        TripleWrite((short) 30, y, new Runtime.FieldRef<>(topScore::getName, topScore::setName));
        TripleCard((short) (ChaosGraphics.SW * 2 / 3), y, topScore.score);
    }

    private boolean EditTopScore(/* VAR+WRT */ ChaosInterface.TopScore topScore, int n) {
        // VAR
        Input.Event event = new Input.Event(); /* WRT */
        int pos = 0;
        boolean exit = false;

        p1 = 5;
        p2 = 3;
        p3 = 4;
        graphics.SetBuffer(true, true);
        graphics.SetCopyMode(Graphics.cmTrans);
        TripleCenter((short) ChaosGraphics.PW, (short) (n * 10 + 50), Runtime.castToRef(languages.ADL("Enter your name !"), String.class));
        UpdateScreen();
        chaosActions.WhiteFade();
        input.FlushEvents();
        pos = 0;
        exit = false;
        do {
            input.SetBusyStat((short) Input.statWaiting);
            input.WaitEvent();
            input.GetEvent(event);
            if (event.type == Input.eKEYBOARD) {
                topScore.name = Runtime.setChar(topScore.name, pos, ' ');
                if ((event.ch == (char) 8) || (event.ch == (char) 127)) {
                    chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sPoubelle.ordinal()]);
                    if (pos > 0)
                        pos--;
                    topScore.name = Runtime.setChar(topScore.name, pos, '_');
                } else if ((event.ch == (char) 13) || (event.ch == (char) 10)) {
                    if (n == 1)
                        chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.wJans.ordinal()]);
                    else
                        chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sCaisse.ordinal()]);
                    exit = true;
                } else if ((pos < 19) && (event.ch >= ' ') && (event.ch < (char) 127)) {
                    chaosSounds.SimpleSound(chaosSounds.soundList[SoundList.sGun.ordinal()]);
                    topScore.name = Runtime.setChar(topScore.name, pos, event.ch);
                    pos++;
                    topScore.name = Runtime.setChar(topScore.name, pos, '_');
                }
                UpdateTopScore(topScore, n, n, n);
                UpdateScreen();
            }
            chaosInterface.CommonEvent(event);
        } while (!(exit || (chaosBase.gameStat == GameStat.Break)));
        input.SetBusyStat((short) Input.statBusy);
        return pos > 0;
    }

    private void UpdateTopScores() {
        // VAR
        int c = 0;
        short pa = 0;
        short pb = 0;

        ResetGraphics();
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 9));
        for (c = 1; c <= 10; c++) {
            UpdateTopScore(topScores[c - 1], c, sl, n);
        }
        graphics.SetPen(0);
        graphics.FillRect((short) 0, chaosGraphics.H.invoke((short) 170), chaosGraphics.W.invoke((short) ChaosGraphics.SW), chaosGraphics.H.invoke((short) ChaosGraphics.SH));
        if (sl != n) {
            pa = 0;
            pb = 1;
        } else {
            pa = 7;
            pb = 2;
        }
        p1 = pa;
        p2 = 3;
        p3 = 4;
        TripleCenter((short) ChaosGraphics.SW, (short) 170, Runtime.castToRef(languages.ADL("was killed at"), String.class));
        { // WITH
            ChaosInterface.TopScore _topScore = topScores[sl - 1];
            if (_topScore.endZone == Zone.Chaos)
                p1 = pb;
            else
                p1 = pa;
            TripleWrite((short) 10, (short) 180, Runtime.castToRef(languages.ADL("Chaos level"), String.class));
            TripleWrite((short) (ChaosGraphics.SW / 2), (short) 180, GetChaosName(_topScore.endLevel[Zone.Chaos.ordinal()]));
            if (_topScore.endZone == Zone.Castle)
                p1 = pb;
            else
                p1 = pa;
            TripleWrite((short) 10, (short) 190, Runtime.castToRef(languages.ADL("Castle:"), String.class));
            TripleWrite((short) (ChaosGraphics.SW / 2), (short) 190, GetCastleName(_topScore.endLevel[Zone.Castle.ordinal()]));
            if (_topScore.endZone == Zone.Family)
                p1 = pb;
            else
                p1 = pa;
            TripleWrite((short) 10, (short) 200, Runtime.castToRef(languages.ADL("Family:"), String.class));
            TripleWrite((short) (ChaosGraphics.SW / 2), (short) 200, GetFamilyName(_topScore.endLevel[Zone.Family.ordinal()]));
            p1 = pa;
            TripleWrite((short) 10, (short) 210, Runtime.castToRef(languages.ADL("Difficulty:"), String.class));
            TripleCard((short) (ChaosGraphics.SW / 2), (short) 210, _topScore.endDifficulty);
        }
        UpdateScreen();
    }

    private void DrawTopScores() {
        ResetGraphics();
        graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) ChaosGraphics.SW), chaosGraphics.H.invoke((short) ChaosGraphics.SH));
        graphics.SetTextSize(chaosGraphics.H.invoke((short) 27));
        p1 = 7;
        p2 = 3;
        p3 = 4;
        TripleCenter((short) ChaosGraphics.SW, (short) 10, Runtime.castToRef(languages.ADL("HIGHSCORES"), String.class));
        UpdateTopScores();
    }

    private final Runnable DrawTopScores_ref = this::DrawTopScores;

    private void TopScoreWait() {
        // VAR
        Input.Event event = new Input.Event(); /* WRT */
        Runtime.RangeSet joy = new Runtime.RangeSet(Memory.SET16_r);

        do {
            input.SetBusyStat((short) Input.statWaiting);
            input.WaitEvent();
            input.GetEvent(event);
            joy.copyFrom(input.GetStick());
            if ((joy.contains(Input.JoyUp)) && (sl > 1))
                sl--;
            if ((joy.contains(Input.JoyDown)) && (sl < 10))
                sl++;
            if (!Runtime.RangeSet.mul(joy, new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyUp, Input.JoyDown)).equals(new Runtime.RangeSet(Memory.SET16_r)))
                UpdateTopScores();
            chaosInterface.CommonEvent(event);
        } while (!((!Runtime.RangeSet.mul(joy, new Runtime.RangeSet(Memory.SET16_r).with(Input.JoyPause).withRange(Input.Joy1, Input.Joy4)).equals(new Runtime.RangeSet(Memory.SET16_r))) || (chaosBase.gameStat == GameStat.Break)));
    }

    public void StartScreen() {
        chaosBase.gameStat = GameStat.Start;
        chaosActions.FadeOut();
        graphics.SetBuffer(true, false);
        graphics.SetPen(0);
        graphics.SetCopyMode(Graphics.cmCopy);
        graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) ChaosGraphics.SW), chaosGraphics.H.invoke((short) ChaosGraphics.SH));
        chaosImages.InitPalette();
        DrawStartScreen();
        chaosInterface.Refresh = DrawStartScreen_ref;
        chaosInterface.EnableFileMenus();
        chaosBase.gameSeed = clock.GetNewSeed();
        sl = 1;
        n = 0;
        while (true) {
            WaitStart();
            if (!joy2pressed || (chaosBase.gameStat == GameStat.Break))
                break;
            chaosActions.FadeOut();
            chaosInterface.ReadTopScoreList(topScores);
            chaosInterface.Refresh = DrawTopScores_ref;
            chaosInterface.Refresh.run();
            chaosActions.FadeIn();
            TopScoreWait();
            if (chaosBase.gameStat == GameStat.Break)
                break;
            chaosActions.FadeOut();
            chaosInterface.Refresh = DrawStartScreen_ref;
            chaosInterface.Refresh.run();
        }
        chaosInterface.DisableFileMenus();
        chaosInterface.Refresh = null;
    }

    public void GameOverScreen() {
        // VAR
        int c = 0;
        int d = 0;
        int ps = 0;
        Zone z = Zone.Chaos;
        boolean norepl = false;

        if ((chaosBase.gameStat != GameStat.Gameover) || (chaosBase.pLife != 0))
            return;
        chaosInterface.DisableFileMenus();
        chaosActions.FadeOut();
        chaosBase.water = false;
        chaosImages.InitPalette();
        ResetGraphics();
        graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) ChaosGraphics.SW), chaosGraphics.H.invoke((short) ChaosGraphics.SH));
        graphics.SetBuffer(true, false);
        graphics.SetPen(0);
        graphics.SetCopyMode(Graphics.cmTrans);
        graphics.FillRect((short) 0, (short) 0, chaosGraphics.W.invoke((short) ChaosGraphics.SW), chaosGraphics.H.invoke((short) ChaosGraphics.SH));
        chaosActions.ZoomMessage(Runtime.castToRef(languages.ADL("Game Over"), String.class), 0, 3, 4);
        ResetGraphics();
        chaosInterface.ReadTopScoreList(topScores);
        c = 1;
        norepl = false;
        while (true) {
            if ((topScores[c - 1].seed == chaosBase.gameSeed) && !chaosBase.password) {
                if (topScores[c - 1].score < chaosBase.score) {
                    for (d = c; d <= 9; d++) {
                        topScores[d - 1].copyFrom(topScores[d + 1 - 1]);
                    }
                    topScores[9].score = 0;
                } else {
                    ps = c;
                    norepl = true;
                }
                break;
            }
            c++;
            if (c > 10)
                break;
        }
        n = 1;
        sl = 1;
        while ((n <= 10) && (chaosBase.score < topScores[n - 1].score)) {
            n++;
        }
        if (chaosBase.password || norepl)
            n = 11;
        if (n <= 10) {
            sl = n;
            for (c = 10; c >= n + 1; c -= 1) {
                topScores[c - 1].copyFrom(topScores[c - 1 - 1]);
            }
            topScores[n - 1].score = chaosBase.score;
            topScores[n - 1].seed = chaosBase.gameSeed;
            { // WITH
                ChaosInterface.TopScore _topScore = topScores[n - 1];
                for (c = 0; c <= 19; c++) {
                    _topScore.name = Runtime.setChar(_topScore.name, c, ' ');
                }
                for (int _z = Zone.Chaos.ordinal(); _z <= Zone.Family.ordinal(); _z++) {
                    z = Zone.values()[_z];
                    _topScore.endLevel[z.ordinal()] = chaosBase.level[z.ordinal()];
                }
                _topScore.endZone = chaosBase.zone;
                _topScore.endDifficulty = (short) chaosBase.difficulty;
            }
        }
        DrawTopScores();
        chaosInterface.Refresh = DrawTopScores_ref;
        WaitRelease();
        if (n <= 10) {
            if (EditTopScore(topScores[n - 1], n)) {
                chaosInterface.WriteTopScoreList(topScores);
            } else {
                chaosInterface.ReadTopScoreList(topScores);
                n = 11;
                DrawTopScores();
                UpdateScreen();
            }
            WaitRelease();
        }
        chaosActions.FadeIn();
        checks.Warn(chaosBase.password, Runtime.castToRef(languages.ADL("Your score won't be saved"), String.class), Runtime.castToRef(languages.ADL("because you used the password"), String.class));
        if (norepl) {
            n = ps;
            sl = ps;
            UpdateTopScores();
        }
        TopScoreWait();
        chaosActions.FadeOut();
        chaosBase.password = false;
        if (chaosBase.gameStat == GameStat.Break)
            return;
        chaosInterface.Refresh = DrawEndScreen_ref;
        chaosBase.gameStat = GameStat.Start;
        DrawEndScreen();
        chaosActions.FadeIn();
        WaitStart();
        chaosBase.gameStat = GameStat.Gameover;
        chaosInterface.EnableFileMenus();
    }


    // Support

    private static ChaosScreens instance;

    public static ChaosScreens instance() {
        if (instance == null)
            new ChaosScreens(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
    }

    public void close() {
    }

}
