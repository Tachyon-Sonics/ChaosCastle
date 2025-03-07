package ch.chaos.castle;

import ch.chaos.library.ANSITerm;
import ch.chaos.library.Checks;
import ch.chaos.library.Clock;
import ch.chaos.library.Files;
import ch.chaos.library.Files.AccessFlags;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;
import java.lang.Runnable;
import java.util.EnumSet;


public class GrotteSupport {

    // Imports
    private final ANSITerm aNSITerm;
    private final Checks checks;
    private final Clock clock;
    private final Files files;
    private final GrotteBonus grotteBonus;
    private final GrotteSounds grotteSounds;
    private final Memory memory;
    private final Trigo trigo;


    private GrotteSupport() {
        instance = this; // Set early to handle circular dependencies
        aNSITerm = ANSITerm.instance();
        checks = Checks.instance();
        clock = Clock.instance();
        files = Files.instance();
        grotteBonus = GrotteBonus.instance();
        grotteSounds = GrotteSounds.instance();
        memory = Memory.instance();
        trigo = Trigo.instance();
    }


    // TYPE

    public static enum OBJECT {
        EMPTY,
        PLAYER,
        K0,
        K1,
        K2,
        K3,
        K4,
        GN1,
        GN2,
        ASC,
        WOOF,
        L1,
        L2,
        L3,
        BALL,
        PIC,
        PLAT,
        TPLAT,
        NID,
        BUB,
        BN,
        DL,
        BM,
        SBN,
        MR;
    }

    public static enum DIRECTION {
        NUL,
        LEFT,
        JUMP,
        RIGHT;
    }

    @FunctionalInterface
    public static interface CreateProc { // PROCEDURE Type
        public void invoke(OBJECT arg1, byte arg2, byte arg3, byte arg4, byte arg5, char arg6);
    }

    public static class Attr { // RECORD

        public short nb;
        public short dt;
        public short nt;
        public short timeout;
        public short ct;
        public short co;


        public short getNb() {
            return this.nb;
        }

        public void setNb(short nb) {
            this.nb = nb;
        }

        public short getDt() {
            return this.dt;
        }

        public void setDt(short dt) {
            this.dt = dt;
        }

        public short getNt() {
            return this.nt;
        }

        public void setNt(short nt) {
            this.nt = nt;
        }

        public short getTimeout() {
            return this.timeout;
        }

        public void setTimeout(short timeout) {
            this.timeout = timeout;
        }

        public short getCt() {
            return this.ct;
        }

        public void setCt(short ct) {
            this.ct = ct;
        }

        public short getCo() {
            return this.co;
        }

        public void setCo(short co) {
            this.co = co;
        }


        public void copyFrom(Attr other) {
            this.nb = other.nb;
            this.dt = other.dt;
            this.nt = other.nt;
            this.timeout = other.timeout;
            this.ct = other.ct;
            this.co = other.co;
        }

        public Attr newCopy() {
            Attr copy = new Attr();
            copy.copyFrom(this);
            return copy;
        }

    }

    public static enum _Stat {
        Playing,
        GameOver,
        Finish,
        Break;
    }

    public static class _Ppos { // RECORD

        public byte x;
        public byte y;


        public byte getX() {
            return this.x;
        }

        public void setX(byte x) {
            this.x = x;
        }

        public byte getY() {
            return this.y;
        }

        public void setY(byte y) {
            this.y = y;
        }


        public void copyFrom(_Ppos other) {
            this.x = other.x;
            this.y = other.y;
        }

        public _Ppos newCopy() {
            _Ppos copy = new _Ppos();
            copy.copyFrom(this);
            return copy;
        }

    }


    // VAR

    public CreateProc Create;
    public Clock.TimePtr time;
    public long score;
    public long followscore;
    public Runtime.RangeSet[] made = Runtime.initArray(new Runtime.RangeSet[11], () -> new Runtime.RangeSet(Memory.SET16_r));
    public _Stat stat;
    public short[][] drsK2 = new short[20][72];
    public short[][] drK2 = new short[20][];
    public _Ppos[] ppos = Runtime.initArray(new _Ppos[4]);
    public short l2count;
    public short oldl2;
    public short blvcount;
    public short oldblv;
    public short pcount;
    public short ndoor;
    public short level;
    public short game;
    public byte pvie;
    public byte oldpvie;
    public Attr[] attr = Runtime.initArray(new Attr[OBJECT.values().length]);


    public CreateProc getCreate() {
        return this.Create;
    }

    public void setCreate(CreateProc Create) {
        this.Create = Create;
    }

    public Clock.TimePtr getTime() {
        return this.time;
    }

    public void setTime(Clock.TimePtr time) {
        this.time = time;
    }

    public long getScore() {
        return this.score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getFollowscore() {
        return this.followscore;
    }

    public void setFollowscore(long followscore) {
        this.followscore = followscore;
    }

    public Runtime.RangeSet[] getMade() {
        return this.made;
    }

    public void setMade(Runtime.RangeSet[] made) {
        this.made = made;
    }

    public _Stat getStat() {
        return this.stat;
    }

    public void setStat(_Stat stat) {
        this.stat = stat;
    }

    public short[][] getDrsK2() {
        return this.drsK2;
    }

    public void setDrsK2(short[][] drsK2) {
        this.drsK2 = drsK2;
    }

    public short[][] getDrK2() {
        return this.drK2;
    }

    public void setDrK2(short[][] drK2) {
        this.drK2 = drK2;
    }

    public _Ppos[] getPpos() {
        return this.ppos;
    }

    public void setPpos(_Ppos[] ppos) {
        this.ppos = ppos;
    }

    public short getL2count() {
        return this.l2count;
    }

    public void setL2count(short l2count) {
        this.l2count = l2count;
    }

    public short getOldl2() {
        return this.oldl2;
    }

    public void setOldl2(short oldl2) {
        this.oldl2 = oldl2;
    }

    public short getBlvcount() {
        return this.blvcount;
    }

    public void setBlvcount(short blvcount) {
        this.blvcount = blvcount;
    }

    public short getOldblv() {
        return this.oldblv;
    }

    public void setOldblv(short oldblv) {
        this.oldblv = oldblv;
    }

    public short getPcount() {
        return this.pcount;
    }

    public void setPcount(short pcount) {
        this.pcount = pcount;
    }

    public short getNdoor() {
        return this.ndoor;
    }

    public void setNdoor(short ndoor) {
        this.ndoor = ndoor;
    }

    public short getLevel() {
        return this.level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public short getGame() {
        return this.game;
    }

    public void setGame(short game) {
        this.game = game;
    }

    public byte getPvie() {
        return this.pvie;
    }

    public void setPvie(byte pvie) {
        this.pvie = pvie;
    }

    public byte getOldpvie() {
        return this.oldpvie;
    }

    public void setOldpvie(byte oldpvie) {
        this.oldpvie = oldpvie;
    }

    public Attr[] getAttr() {
        return this.attr;
    }

    public void setAttr(Attr[] attr) {
        this.attr = attr;
    }


    // CONST

    private static final String DecorFile = "Decors";


    // TYPE

    private static class Choice { // RECORD

        private String name = "";
        private boolean drawer;
        private Choice next /* POINTER */;


        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isDrawer() {
            return this.drawer;
        }

        public void setDrawer(boolean drawer) {
            this.drawer = drawer;
        }

        public Choice getNext() {
            return this.next;
        }

        public void setNext(Choice next) {
            this.next = next;
        }


        public void copyFrom(Choice other) {
            this.name = other.name;
            this.drawer = other.drawer;
            this.next = other.next;
        }

        public Choice newCopy() {
            Choice copy = new Choice();
            copy.copyFrom(this);
            return copy;
        }

    }


    // VAR

    private Files.FilePtr fh;
    private boolean termopen;
    private boolean chkesc;
    private boolean dm;
    private boolean no10;
    private boolean first;
    private short lastgame;
    private short lastlevel;
    private int rndcount;
    private int randomcount;
    private String[] g /* POINTER */;
    private short[] cols = new short[7];


    public Files.FilePtr getFh() {
        return this.fh;
    }

    public void setFh(Files.FilePtr fh) {
        this.fh = fh;
    }

    public boolean isTermopen() {
        return this.termopen;
    }

    public void setTermopen(boolean termopen) {
        this.termopen = termopen;
    }

    public boolean isChkesc() {
        return this.chkesc;
    }

    public void setChkesc(boolean chkesc) {
        this.chkesc = chkesc;
    }

    public boolean isDm() {
        return this.dm;
    }

    public void setDm(boolean dm) {
        this.dm = dm;
    }

    public boolean isNo10() {
        return this.no10;
    }

    public void setNo10(boolean no10) {
        this.no10 = no10;
    }

    public boolean isFirst() {
        return this.first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public short getLastgame() {
        return this.lastgame;
    }

    public void setLastgame(short lastgame) {
        this.lastgame = lastgame;
    }

    public short getLastlevel() {
        return this.lastlevel;
    }

    public void setLastlevel(short lastlevel) {
        this.lastlevel = lastlevel;
    }

    public int getRndcount() {
        return this.rndcount;
    }

    public void setRndcount(int rndcount) {
        this.rndcount = rndcount;
    }

    public int getRandomcount() {
        return this.randomcount;
    }

    public void setRandomcount(int randomcount) {
        this.randomcount = randomcount;
    }

    public String[] getG() {
        return this.g;
    }

    public void setG(String[] g) {
        this.g = g;
    }

    public short[] getCols() {
        return this.cols;
    }

    public void setCols(short[] cols) {
        this.cols = cols;
    }


    // PROCEDURE

    public void WL(String s) {
        aNSITerm.WriteString(s);
        aNSITerm.WriteLn();
    }

    private void WaitAChar(/* VAR+WRT */ Runtime.IRef<Character> ch) {
        aNSITerm.WaitChar(ch);
        if ((ch.get() == ((char) 033)) || (ch.get() == ((char) 03)) || (ch.get() == 'q') || (ch.get() == 'Q'))
            checks.Terminate();
    }

    private void WA(byte x, byte y, String s) {
        aNSITerm.Goto(x, y);
        aNSITerm.WriteString(s);
    }

    public int Random() {
        randomcount = (randomcount * 13077 + 6925) % 32768;
        return randomcount;
    }

    public int Rnd() {
        rndcount = (rndcount * 13077 + 6925 + trigo.RND()) % 32768;
        return rndcount;
    }

    public void ReadDir(/* VAR+WRT */ Runtime.IRef<Character> ch) {
        aNSITerm.Read(ch);
        if (ch.get() == ((char) 033)) {
            if (chkesc) {
                ch.set(((char) 03));
                return;
            } else {
                chkesc = true;
            }
            aNSITerm.Read(ch);
        }
        if ((ch.get() == ((char) 0233)) || (ch.get() == '['))
            aNSITerm.Read(ch);
        if ((ch.get() != ((char) 0)) && (ch.get() != ((char) 033)))
            chkesc = false;
    }

    public void BigInit() {
        // VAR
        OBJECT t = OBJECT.EMPTY;

        fh = files.noFile;
        time = clock.noTime;
        dm = false;
        first = true;
        no10 = true;
        stat = _Stat.Finish;
        cols[0] = 6;
        cols[1] = 4;
        cols[2] = 5;
        cols[3] = 1;
        cols[4] = 3;
        cols[5] = 2;
        cols[6] = 7;
        score = 0;
        followscore = 0;
        oldpvie = 1;
        l2count = 0;
        blvcount = 0;
        lastgame = 0;
        lastlevel = 0;
        chkesc = false;
        termopen = false;
        for (int _t = 0; _t < OBJECT.values().length; _t++) {
            t = OBJECT.values()[_t];
            { // WITH
                Attr _attr = attr[t.ordinal()];
                _attr.nb = 0;
                _attr.dt = 0;
                _attr.nt = 0;
                _attr.timeout = 0;
                _attr.ct = 0;
                _attr.co = 0;
            }
        }
        aNSITerm.ClearScreen();
        aNSITerm.Color((short) 1);
        time = clock.AllocTime(1024);
        checks.CheckMemBool(time == clock.noTime);
        clock.StartTime(time);
        randomcount = Rnd();
    }

    public void BigFlush() {
        files.CloseFile(new Runtime.FieldRef<>(this::getFh, this::setFh));
        clock.FreeTime(time);
    }

    public final Runnable BigFlush_ref = this::BigFlush;

    public void WriteCard(long c) {
        // VAR
        boolean z = false;
        long q = 0L;
        long v = 0L;

        z = false;
        q = 1000000000;
        do {
            v = c / q;
            c = c % q;
            q = q / 10;
            if (v == 0) {
                if (z || (q == 0))
                    aNSITerm.Write('0');
            } else {
                aNSITerm.Write((char) (v + 48));
                z = true;
            }
        } while (q != 0);
    }

    public void Gameover(long score) {
        // CONST
        final String l1 = "TTTTT  OOO  PPPPP        SSSS  CCCC  OOO  RRRRR EEEEE  SSSS";
        final String l2 = "  T   O   O P   P       S     C     O   O R   R E     S    ";
        final String l3 = "  T   O   O PPPPP        SSS  C     O   O RRRRR EEEE   SSS ";
        final String l4 = "  T   O   O P               S C     O   O RR    E         S";
        final String l5 = "  T    OOO  P           SSSS   CCCC  OOO  R RRR EEEEE SSSS ";

        // VAR
        byte x = 0;
        byte y = 0;
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);
        String[] lines = new String[5];

        lines[0] = l1;
        lines[1] = l2;
        lines[2] = l3;
        lines[3] = l4;
        lines[4] = l5;
        aNSITerm.Goto((byte) 0, (byte) 0);
        aNSITerm.Color((short) 1);
        for (y = 0; y <= 19; y++) {
            for (x = 0; x <= 71; x++) {
                aNSITerm.Write('#');
            }
            aNSITerm.WriteLn();
        }
        clock.StartTime(time);
        for (x = 6; x >= 0; x -= 1) {
            aNSITerm.Color(cols[x]);
            WA((byte) 7, (byte) 6, " &&&&& &&&&& && && &&&&&        &&&  &   & &&&&& &&&&& ");
            WA((byte) 7, (byte) 7, " &     &   & &&&&& &           &   & &   & &     &   & ");
            WA((byte) 7, (byte) 8, " & &&& &&&&& & & & &&&&        &   &  & &  &&&&  &&&&& ");
            WA((byte) 7, (byte) 9, " &   & &   & &   & &           &   &  & &  &     &&    ");
            WA((byte) 7, (byte) 10, " &&&&& &   & &   & &&&&&        &&&    &   &&&&& & &&& ");
            aNSITerm.WriteLn();
            if (!clock.WaitTime(time, 512))
                clock.StartTime(time);
        }
        if (clock.WaitTime(time, 4096)) {
        }
        for (x = 1; x <= 10; x++) {
            if (((x % 2) != 0))
                aNSITerm.Color((short) 7);
            else
                aNSITerm.Color((short) 6);
            WA((byte) 0, (byte) 21, "Score: ");
            WriteCard(score);
            if (clock.WaitTime(time, 100)) {
            }
        }
        do {
            aNSITerm.Read(ch);
        } while (ch.get() > ((char) 03));
        WaitAChar(ch);
        checks.Terminate();
    }

    private void Set(/* VAR */ Runtime.IRef<Integer> p, Runtime.RangeSet[] pass, boolean v) {
        if (v)
            pass[p.get() / 16].incl(p.get() % 16);
        p.inc();
    }

    private void SetVal(/* VAR+WRT */ Runtime.IRef<Integer> p, Runtime.RangeSet[] pass, long val, long cnt) {
        while (cnt > 0) {
            Set(p, pass, (val % 2) == 1);
            val = val / 2;
            cnt--;
        }
    }

    private boolean Get(/* VAR */ Runtime.IRef<Integer> p, Runtime.RangeSet[] pass) {
        // VAR
        boolean v = false;

        v = pass[p.get() / 16].contains((p.get() % 16));
        p.inc();
        return v;
    }

    private long GetVal(/* VAR+WRT */ Runtime.IRef<Integer> p, Runtime.RangeSet[] pass, long cnt) {
        // VAR
        long s = 0L;
        long v = 0L;

        s = 1;
        v = 0;
        while (cnt > 0) {
            if (Get(p, pass))
                v += s;
            s += s;
            cnt--;
        }
        return v;
    }

    private void WriteCode(int c, int s) {
        // VAR
        short i = 0;

        c = (c + s) % 64;
        i = (short) c;
        if (i == 'O' - 65)
            aNSITerm.Write('.');
        else if (i == 'I' - 65)
            aNSITerm.Write('/');
        else if (i == 'l' - 97 + 26)
            aNSITerm.Write('!');
        else if (i == '0' - 48 + 52)
            aNSITerm.Write('-');
        else if (i == '1' - 48 + 52)
            aNSITerm.Write('+');
        else if (i < 26)
            aNSITerm.Write((char) (c + 65));
        else if (i < 52)
            aNSITerm.Write((char) (c - 26 + 97));
        else if (i < 62)
            aNSITerm.Write((char) (c - 52 + 48));
        else if (i == 62)
            aNSITerm.Write(':');
        else
            aNSITerm.Write('?');
    }

    private int ReadCode(char ch, int s) {
        // VAR
        int v = 0;

        if (ch == '.')
            v = 'O' - 65;
        else if (ch == '/')
            v = 'I' - 65;
        else if (ch == '!')
            v = 'l' - 97 + 26;
        else if (ch == '-')
            v = '0' - 48 + 52;
        else if (ch == '+')
            v = '1' - 48 + 52;
        else if ((ch >= 'A') && (ch <= 'Z'))
            v = ch - 65;
        else if ((ch >= 'a') && (ch <= 'z'))
            v = ch - 97 + 26;
        else if ((ch >= '0') && (ch <= '9'))
            v = ch - 48 + 52;
        else if (ch == ':')
            v = 62;
        else if (ch != '?')
            v = 0;
        else
            v = 63;
        return (v + 64 - s) % 64;
    }

    private void GameToPass() {
        // VAR
        Runtime.RangeSet[] pass = Runtime.initArray(new Runtime.RangeSet[12], () -> new Runtime.RangeSet(Memory.SET16_r));
        int x = 0;
        int y = 0;
        Runtime.Ref<Integer> p = new Runtime.Ref<>(0);
        int sum = 0;
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);

        for (x = 0; x <= 11; x++) {
            pass[x] = new Runtime.RangeSet(Memory.SET16_r);
        }
        aNSITerm.ClearScreen();
        aNSITerm.Color((short) 1);
        aNSITerm.Goto((byte) 30, (byte) 1);
        aNSITerm.WriteString("************");
        aNSITerm.Goto((byte) 30, (byte) 3);
        aNSITerm.WriteString("************");
        aNSITerm.WriteAt((byte) 30, (byte) 2, '*');
        aNSITerm.WriteAt((byte) 41, (byte) 2, '*');
        aNSITerm.Color((short) 3);
        p.set(0);
        aNSITerm.Goto((byte) 32, (byte) 2);
        aNSITerm.WriteString("PASSWORD");
        for (y = 0; y <= 9; y++) {
            for (x = 0; x <= 10; x++) {
                Set(p, pass, made[y].contains(x));
            }
        }
        Set(p, pass, no10);
        if (blvcount > 63)
            blvcount = 63;
        SetVal(p, pass, blvcount, 6);
        SetVal(p, pass, score, 32);
        SetVal(p, pass, pvie, 7);
        SetVal(p, pass, l2count, 6);
        p.set(0);
        aNSITerm.Goto((byte) 21, (byte) 8);
        aNSITerm.Color((short) 6);
        sum = 17;
        for (y = 0; y <= 26; y++) {
            x = (int) GetVal(p, pass, 6);
            WriteCode(x, y);
            sum += x;
            if (sum >= 64)
                sum -= 63;
        }
        WriteCode(sum, 27);
        aNSITerm.Goto((byte) 4, (byte) 12);
        aNSITerm.Color((short) 7);
        clock.StartTime(time);
        if (clock.WaitTime(time, 2048)) {
        }
        do {
            aNSITerm.Read(ch);
        } while (ch.get() > ((char) 03));
        aNSITerm.WriteString("Press any key");
        do {
            aNSITerm.Read(ch);
            if (clock.WaitTime(time, 64)) {
            }
        } while (ch.get() == ((char) 0));
    }

    private void PassToGame() {
        // VAR
        int x = 0;
        int y = 0;
        Runtime.Ref<Integer> p = new Runtime.Ref<>(0);
        int sum = 0;
        Runtime.RangeSet[] pass = Runtime.initArray(new Runtime.RangeSet[12], () -> new Runtime.RangeSet(Memory.SET16_r));
        Runtime.Ref<String> str = new Runtime.Ref<>("");
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);

        for (x = 0; x <= 11; x++) {
            pass[x] = new Runtime.RangeSet(Memory.SET16_r);
        }
        aNSITerm.ClearScreen();
        aNSITerm.Color((short) 1);
        aNSITerm.Goto((byte) 26, (byte) 1);
        aNSITerm.WriteString("*******************");
        aNSITerm.Goto((byte) 26, (byte) 3);
        aNSITerm.WriteString("*******************");
        aNSITerm.WriteAt((byte) 26, (byte) 2, '*');
        aNSITerm.WriteAt((byte) 44, (byte) 2, '*');
        aNSITerm.Color((short) 2);
        aNSITerm.Goto((byte) 28, (byte) 2);
        aNSITerm.WriteString("Enter Password:");
        aNSITerm.Goto((byte) 21, (byte) 8);
        aNSITerm.Color((short) 4);
        for (x = 0; x <= 27; x++) {
            aNSITerm.Write('#');
        }
        aNSITerm.Goto((byte) 21, (byte) 8);
        aNSITerm.Color((short) 6);
        aNSITerm.ReadString(str);
        sum = 17;
        p.set(0);
        for (x = 0; x <= 26; x++) {
            y = ReadCode(Runtime.getChar(str, x), x);
            sum += y;
            if (sum >= 64)
                sum -= 63;
            SetVal(p, pass, y, 6);
        }
        if (sum == ReadCode(Runtime.getChar(str, 27), 27)) {
            p.set(0);
            for (y = 0; y <= 9; y++) {
                for (x = 0; x <= 10; x++) {
                    if (Get(p, pass))
                        made[y].incl(x);
                    else
                        made[y].excl(x);
                }
            }
            no10 = Get(p, pass);
            blvcount = (short) GetVal(p, pass, 6);
            if (blvcount > 0)
                made[10].excl(0);
            else
                made[10].incl(0);
            score = GetVal(p, pass, 32);
            followscore = ((score + 1000) / 1000) * 1000;
            pvie = (byte) GetVal(p, pass, 7);
            l2count = (short) GetVal(p, pass, 6);
        } else {
            aNSITerm.Goto((byte) 25, (byte) 10);
            aNSITerm.Color((short) 3);
            aNSITerm.WriteString("* Invalid password *");
            aNSITerm.Goto((byte) 25, (byte) 11);
            aNSITerm.Color((short) 7);
            aNSITerm.WriteString("Press any key");
            WaitAChar(ch);
        }
        if (pvie <= 0)
            Gameover(0);
    }

    private boolean ReadGame_Made(short level, short game) {
        return made[level].contains(game);
    }

    private void ReadGame_ViewGame(short level, short game, boolean on) {
        // VAR
        boolean test1 = false;
        boolean test2 = false;

        if (level != 10) {
            aNSITerm.Goto((byte) (game * 3 + 2), (byte) (level + 8));
        } else {
            aNSITerm.Goto((byte) 2, (byte) 18);
            if (on) {
                if (!ReadGame_Made((short) 10, (short) 0)) {
                    aNSITerm.Color((short) 2);
                    aNSITerm.WriteString("BONUS LEVEL");
                }
            } else {
                aNSITerm.WriteString("           ");
            }
            return;
        }
        if (ReadGame_Made(level, game)) {
            aNSITerm.Color((short) 1);
        } else {
            test1 = (game == 9);
            test2 = (level >= 7);
            if (test1 != test2)
                aNSITerm.Color((short) 3);
            else
                aNSITerm.Color((short) 2);
            if (test1 && test2)
                aNSITerm.Color((short) 7);
        }
        if (on) {
            if (ReadGame_Made(level, game)) {
                aNSITerm.WriteString("[]");
            } else {
                aNSITerm.Write((char) ((level + 1) % 10 + 48));
                aNSITerm.Write((char) (game + 65));
            }
        } else {
            aNSITerm.WriteString("  ");
        }
    }

    private void ReadGame_DisplayGrotte(byte x) {
        WA(x, (byte) 0, " 00000  00000   000   00000  00000  00000");
        WA(x, (byte) 1, " 0      0   0  0   0    0      0    0");
        WA(x, (byte) 2, " 0 000  00000  0   0    0      0    0000");
        WA(x, (byte) 3, " 0   0  00     0   0    0      0    0");
        WA(x, (byte) 4, " 00000  0 000   000     0      0    00000");
        aNSITerm.Goto((byte) 0, (byte) 0);
    }

    private void ReadGame_Display(/* VAR */ Runtime.IRef<Integer> left) {
        // VAR
        short c1 = 0;
        short c2 = 0;

        aNSITerm.ClearScreen();
        clock.StartTime(time);
        if (first) {
            for (c1 = 1; c1 <= 3; c1++) {
                aNSITerm.Color(c1);
                ReadGame_DisplayGrotte((byte) 0);
                if (!clock.WaitTime(time, 512))
                    clock.StartTime(time);
            }
            for (c1 = 1; c1 <= 14; c1++) {
                ReadGame_DisplayGrotte((byte) c1);
                if (!clock.WaitTime(time, 200))
                    clock.StartTime(time);
            }
            first = false;
        }
        aNSITerm.Color((short) 3);
        ReadGame_DisplayGrotte((byte) 15);
        aNSITerm.Color((short) 1);
        WA((byte) 28, (byte) 5, "(C) 2000 Nicky");
        aNSITerm.Color((short) 5);
        WA((byte) 38, (byte) 7, "Use the following keys from the numeric");
        WA((byte) 38, (byte) 8, "pad (Num Lock) to move during the game:");
        WA((byte) 40, (byte) 9, "{4}: start moving to the left");
        WA((byte) 40, (byte) 10, "{6}: start moving to the right");
        WA((byte) 40, (byte) 11, "{5}: stop moving");
        WA((byte) 40, (byte) 12, "<SPACE> or {0}: jump");
        WA((byte) 40, (byte) 13, "{1}, {3}, {8}, {2}:");
        WA((byte) 40, (byte) 14, " fire left, right, up and down");
        WA((byte) 40, (byte) 15, "{7}, {9}: big fire left and right");
        aNSITerm.Color((short) 4);
        WA((byte) 38, (byte) 16, "{q}: quit      {e}: give up level");
        WA((byte) 38, (byte) 17, "{r}: refresh   {m}: choose random");
        WA((byte) 38, (byte) 18, "{p}: pause     {s}, {l}: save, load");
        WA((byte) 38, (byte) 19, "{arrow}: move  {shift arrow}: fire");
        aNSITerm.Color((short) 7);
        WA((byte) 0, (byte) 7, "Choose a level and press <RETURN>:");
        WA((byte) 0, (byte) 21, "Score: ");
        WriteCard(score);
        WA((byte) 36, (byte) 21, "Lives: ");
        aNSITerm.Color((short) 3);
        WriteCard(pvie);
        ShowClock();
        left.set(0);
        for (c1 = 0; c1 <= 9; c1++) {
            for (c2 = 0; c2 <= 9; c2++) {
                if (!ReadGame_Made(c1, c2))
                    left.inc();
                ReadGame_ViewGame(c1, c2, true);
            }
        }
        if (!ReadGame_Made((short) 10, (short) 0))
            ReadGame_ViewGame((short) 10, (short) 0, true);
    }

    public void ReadGame(/* VAR */ Runtime.IRef<Short> level, /* VAR */ Runtime.IRef<Short> game) {
        // VAR
        boolean on = false;
        short gc = 0;
        short c1 = 0;
        short c2 = 0;
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);
        int best = 0;
        Runtime.Ref<Integer> left = new Runtime.Ref<>(0);

        on = true;
        do {
            aNSITerm.Read(ch);
        } while (ch.get() > ((char) 03));
        level.set(lastlevel);
        game.set(lastgame);
        if (level.get() > 10)
            level.set((short) 0);
        if (oldpvie == 0) {
            made[level.get()].excl(game.get());
            oldpvie = 1;
            if (blvcount <= 0)
                made[10].incl(0);
        }
        ReadGame_Display(left);
        gc = 102;
        while (ReadGame_Made(level.get(), game.get()) && (gc > 0)) {
            game.inc();
            gc--;
            if ((game.get() > 9) || ((game.get() > 0) && (level.get() == 10))) {
                game.set((short) 0);
                level.inc();
                if (level.get() > 10)
                    level.set((short) 0);
            }
        }
        if (gc == 0) {
            aNSITerm.ClearScreen();
            clock.StartTime(time);
            if (clock.WaitTime(time, 3072)) {
            }
            aNSITerm.Color((short) 7);
            aNSITerm.Goto((byte) 0, (byte) 20);
            aNSITerm.WriteString("**** YOU'VE FINISHED THE GAME !!! ****");
            aNSITerm.Goto((byte) 0, (byte) 0);
            aNSITerm.Color((short) 3);
            for (c1 = 0; c1 <= 19; c1++) {
                for (c2 = 0; c2 <= 71; c2++) {
                    aNSITerm.Write('%');
                }
                aNSITerm.WriteLn();
            }
            grotteSounds.FinalMusic();
            clock.StartTime(time);
            if (clock.WaitTime(time, 10000)) {
            }
            do {
                score += 1000;
                pvie--;
                aNSITerm.Color((short) 7);
                WA((byte) 0, (byte) 21, "Score: ");
                WriteCard(score);
                WA((byte) 36, (byte) 21, "Lives: ");
                aNSITerm.Color((short) 3);
                WriteCard(pvie);
                aNSITerm.Write(' ');
                if (clock.WaitTime(time, 1800)) {
                }
            } while (pvie != 0);
            grotteSounds.GameOverSound();
            Gameover(score);
        }
        clock.StartTime(time);
        c1 = 3;
        c2 = 0;
        do {
            aNSITerm.Goto((byte) 0, (byte) 22);
            if (Random() == 0) {
            }
            ReadDir(ch);
            if ((ch.get() == 'A') || (ch.get() == ((char) 034)))
                ch.set('8');
            if ((ch.get() == 'B') || (ch.get() == ((char) 035)))
                ch.set('2');
            if ((ch.get() == 'C') || (ch.get() == ((char) 036)))
                ch.set('6');
            if ((ch.get() == 'D') || (ch.get() == ((char) 037)))
                ch.set('4');
            if (ch.get() != ((char) 0)) {
                ReadGame_ViewGame(level.get(), game.get(), true);
                on = true;
                c1 = 1;
            }
            if ((ch.get() == '4') && (game.get() > 0))
                game.dec();
            if ((ch.get() == '6') && (game.get() < 9))
                game.inc();
            if ((ch.get() == '2') && (level.get() < 10))
                level.inc();
            if ((level.get() == 10) && (ReadGame_Made((short) 10, (short) 0)))
                level.dec();
            if ((ch.get() == '8') && (level.get() > 0))
                level.dec();
            if ((ch.get() == 's') || (ch.get() == 'S')) {
                GameToPass();
                ReadGame_Display(left);
            }
            if ((ch.get() == 'l') || (ch.get() == 'L')) {
                PassToGame();
                ReadGame_Display(left);
            }
            if ((ch.get() == 'r') || (ch.get() == 'R') || (ch.get() == 'w') || (ch.get() == 'W'))
                ReadGame_Display(left);
            if ((ch.get() == 'M') || (ch.get() == 'm')) {
                if (!ReadGame_Made((short) 10, (short) 0)) {
                    level.set((short) 10);
                    game.set((short) 0);
                } else {
                    if (pvie > 25)
                        level.set((short) 125);
                    else
                        level.set((short) (pvie * 5));
                    level.inc(Rnd() % 32);
                    if (level.get() != 0)
                        level.dec();
                    best = level.get();
                    best = (left.get() * best) / 160;
                    level.set((short) 0);
                    game.set((short) 0);
                    while (ReadGame_Made(level.get(), game.get())) {
                        game.inc();
                        if (game.get() >= 10) {
                            game.set((short) 0);
                            level.set((short) ((level.get() + 1) % 10));
                        }
                    }
                    while (best > 0) {
                        do {
                            game.inc();
                            if (game.get() >= 10) {
                                game.set((short) 0);
                                level.set((short) ((level.get() + 1) % 10));
                            }
                        } while (ReadGame_Made(level.get(), game.get()));
                        best--;
                    }
                }
            }
            if ((ch.get() == 'q') || (ch.get() == 'Q') || (ch.get() == ((char) 03)) || (ch.get() == ((char) 033))) {
                aNSITerm.ClearScreen();
                checks.Terminate();
            }
            if ((ch.get() == 'I') || (ch.get() == 'i')) {
                DisplayInstructions();
                ReadGame_Display(left);
            }
            if (!clock.WaitTime(time, 64))
                clock.StartTime(time);
            if (c2 == 0) {
                ShowClock();
                aNSITerm.Goto((byte) 1, (byte) 19);
                aNSITerm.Color((short) 6);
                c2 = 40;
                aNSITerm.WriteString("Press {I} for more instructions");
            } else if (c2 == 1) {
                aNSITerm.Goto((byte) 1, (byte) 19);
                aNSITerm.WriteString("                               ");
            }
            c1--;
            if (c1 == 0) {
                on = !on;
                ReadGame_ViewGame(level.get(), game.get(), on);
                c1 = 6;
                c2--;
            }
        } while (!(((ch.get() == ((char) 015)) || (ch.get() == ((char) 012))) && (!ReadGame_Made(level.get(), game.get()))));
        if (level.get() == 10)
            game.set((short) 0);
        if (!made[level.get()].contains(10)) {
            made[level.get()].incl(10);
            made[level.get()].excl(9);
        }
        if ((game.get() == 9) && no10) {
            no10 = false;
            made[7] = new Runtime.RangeSet(Memory.SET16_r).with(9);
            made[8] = new Runtime.RangeSet(Memory.SET16_r).with(9);
            made[9] = new Runtime.RangeSet(Memory.SET16_r).with(9);
        }
        if (level.get() < 10) {
            lastlevel = level.get();
            lastgame = game.get();
        }
    }

    public void ShowClock() {
        // VAR
        Runtime.Ref<Integer> h = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> m = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> s = new Runtime.Ref<>(0);

        clock.GetCurrentTime(h, m, s);
        aNSITerm.Goto((byte) 67, (byte) 20);
        aNSITerm.Color((short) 7);
        aNSITerm.Write((char) (h.get() / 10 + 48));
        aNSITerm.Write((char) (h.get() % 10 + 48));
        aNSITerm.Write(':');
        aNSITerm.Write((char) (m.get() / 10 + 48));
        aNSITerm.Write((char) (m.get() % 10 + 48));
    }

    public OBJECT TypeOf(char ch) {
        // VAR
        OBJECT t = OBJECT.EMPTY;

        switch (ch) {
            case '*' -> t = OBJECT.PLAYER;
            case ' ' -> t = OBJECT.EMPTY;
            case '&' -> t = OBJECT.K0;
            case '+' -> t = OBJECT.K1;
            case 'X' -> t = OBJECT.K2;
            case '#' -> t = OBJECT.K3;
            case 'x' -> t = OBJECT.K4;
            case '>', '<', 'Y' -> t = OBJECT.GN1;
            case '£' -> t = OBJECT.GN2;
            case '=' -> t = OBJECT.ASC;
            case '-', '|' -> t = OBJECT.L1;
            case ')', '(' -> t = OBJECT.L2;
            case '}', '{' -> t = OBJECT.L3;
            case 'O' -> t = OBJECT.BALL;
            case 'V' -> t = OBJECT.PIC;
            case 'T' -> t = OBJECT.PLAT;
            case 'W' -> t = OBJECT.WOOF;
            case 'U' -> t = OBJECT.TPLAT;
            case 'Z' -> t = OBJECT.NID;
            case 'N' -> t = OBJECT.BUB;
            case '%' -> t = OBJECT.BN;
            case 's' -> t = OBJECT.DL;
            case 'H' -> t = OBJECT.BM;
            case '.', '$', '@' -> t = OBJECT.SBN;
            default -> t = OBJECT.MR;
        }
        return t;
    }

    public void SetColor(char ch) {
        switch (TypeOf(ch)) {
            case K1, K2, K3, K4, L3 -> {
                aNSITerm.Color((short) 1);
            }
            case K0 -> {
                aNSITerm.Color((short) 3);
            }
            case BM, BALL -> {
                aNSITerm.Color((short) 6);
            }
            case PLAYER, BN, DL, L1, L2, SBN -> {
                aNSITerm.Color((short) 2);
            }
            case ASC, PLAT, WOOF, TPLAT -> {
                aNSITerm.Color((short) 4);
            }
            case GN1, GN2, NID, BUB, PIC -> {
                aNSITerm.Color((short) 5);
            }
            default -> {
                if ((ch == 'A') || (ch == '!'))
                    aNSITerm.Color((short) 3);
                else
                    aNSITerm.Color((short) 7);
            }
        }
    }

    private void Complete(OBJECT t, byte nvie, short count, char ch) {
        // VAR
        byte nx = 0;
        byte ny = 0;
        byte ns = 0;

        if ((t == OBJECT.K4) || (t == OBJECT.PIC))
            ns = 2;
        else
            ns = 0;
        while (attr[t.ordinal()].nb < count) {
            do {
                if (Random() == 0) {
                }
                nx = (byte) (Random() % 72);
                ny = (byte) (Random() % 20);
            } while (aNSITerm.Report(nx, ny) != ' ');
            Create.invoke(t, nx, ny, nvie, ns, ch);
        }
    }

    private void NoMem() {
        WA((byte) 0, (byte) 20, "***** Not enough memory *****");
        clock.StartTime(time);
        if (clock.WaitTime(time, 1024)) {
        }
    }

    private void DosErr() {
        WA((byte) 0, (byte) 20, "***** File input error *****");
        clock.StartTime(time);
        if (clock.WaitTime(time, 1024)) {
        }
    }

    private void DisplayFile(Runtime.IRef<String> name) {
        // VAR
        Runtime.Ref<Files.FilePtr> fh = new Runtime.Ref<>(null);
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);
        Runtime.Ref<Character> fch = new Runtime.Ref<>((char) 0);
        int y = 0;

        ch.set(((char) 0));
        aNSITerm.ClearScreen();
        fch.set(((char) 0));
        aNSITerm.Color((short) 7);
        y = 0;
        fh.set(files.OpenFile(name, EnumSet.of(AccessFlags.accessRead)));
        if (fh.get() == files.noFile) {
            DosErr();
            return;
        }
        while ((files.ReadFileBytes(fh.get(), fch, 1) == 1) && (ch.get() != ((char) 03)) && (ch.get() != ((char) 033)) && (ch.get() != 'Q') && (ch.get() != 'q')) {
            if (fch.get() < ' ') {
                aNSITerm.WriteLn();
                y++;
            } else {
                aNSITerm.Write(fch.get());
            }
            if (y >= 22) {
                aNSITerm.WaitChar(ch);
                aNSITerm.ClearScreen();
                y = 0;
            }
        }
        files.CloseFile(fh);
    }

    private void DisplayChoice(Runtime.IRef<String> drawer) {
        // VAR
        Runtime.Ref<Files.DirectoryPtr> d = new Runtime.Ref<>(null);
        Memory.TagItem[] tags = Runtime.initArray(new Memory.TagItem[3]);
        Choice first = null;
        Runtime.Ref<Choice> _new = new Runtime.Ref<>(null);
        Choice current = null;
        short count = 0;
        short length = 0;
        short c = 0;
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);

        count = 0;
        first = null;
        current = null;
        d.set(files.OpenDirectory(drawer));
        if (d.get() == files.noDir)
            return;
        while (true) {
            tags[0].tag = Files.fNAME;
            tags[1].tag = Files.fFLAGS;
            tags[2].tag = 0;
            if (!files.DirectoryNext(d.get(), tags[0]))
                break;
            _new.set((Choice) memory.AllocMem(Runtime.sizeOf(137, Choice.class)));
            if (_new.get() == null) {
                NoMem();
                break;
            }
            if (current != null)
                current.next = _new.get();
            current = _new.get();
            current.next = null;
            if (first == null)
                first = _new.get();
            memory.CopyStr(Runtime.castToRef(tags[0].addr, String.class), new Runtime.FieldExprRef<>(_new.get(), Choice::getName, Choice::setName), Runtime.sizeOf(128, String.class));
            _new.get().drawer = (tags[1].data / Files.afFILE) % 2 == 0;
        }
        files.CloseDirectory(d);
        do {
            aNSITerm.ClearScreen();
            aNSITerm.Color((short) 7);
            WL("{0}: <---");
            count = 0;
            while (true) {
                count++;
                if (count < 10)
                    ch.set((char) (48 + count));
                else
                    ch.set((char) (55 + count));
                current = first;
                while (true) {
                    if (current == null)
                        break;
                    length = (short) memory.StrLength(new Runtime.FieldExprRef<>(current, Choice::getName, Choice::setName));
                    if (Runtime.getChar(current.name, length - 1) == ch.get())
                        break;
                    current = current.next;
                }
                if (current == null)
                    break;
                aNSITerm.Write('{');
                aNSITerm.Write(ch.get());
                aNSITerm.WriteString("}: ");
                length = (short) memory.StrLength(new Runtime.FieldExprRef<>(current, Choice::getName, Choice::setName));
                c = (short) (length - 1);
                while (true) {
                    if (c <= 0) {
                        c = 0;
                        break;
                    }
                    c--;
                    ch.set(Runtime.getChar(current.name, c));
                    if ((ch.get() == ':') || (ch.get() == '/') || (ch.get() == '\\')) {
                        c++;
                        break;
                    }
                }
                while ((c < length - 2) && (Runtime.getChar(current.name, c) != ((char) 0))) {
                    aNSITerm.Write(Runtime.getChar(current.name, c));
                    c++;
                }
                aNSITerm.WriteLn();
            }
            aNSITerm.WaitChar(ch);
            if ((ch.get() >= 'a') && (ch.get() <= 'k'))
                ch.set((char) ((char) ch.get() - 32));
            aNSITerm.Color((short) 6);
            aNSITerm.Write(ch.get());
            if (ch.get() > '0') {
                current = first;
                while ((current != null) && (Runtime.getChar(current.name, memory.StrLength(new Runtime.FieldExprRef<>(current, Choice::getName, Choice::setName)) - 1) != ch.get())) {
                    current = current.next;
                }
                if (current != null) {
                    if (current.drawer)
                        DisplayChoice(new Runtime.FieldExprRef<>(current, Choice::getName, Choice::setName));
                    else
                        DisplayFile(new Runtime.FieldExprRef<>(current, Choice::getName, Choice::setName));
                }
            }
        } while (!((ch.get() <= '0') || (ch.get() == 'q') || (ch.get() == 'Q')));
        current = first;
        while (current != null) {
            _new.set(current);
            current = current.next;
            memory.FreeMem(_new.asAdrRef());
        }
    }

    public void DisplayInstructions() {
        dm = false;
        DisplayChoice(Runtime.castToRef(memory.ADS("Docs"), String.class));
    }

    private void InitK2Dirs_InitDir(boolean up, char d) {
        // VAR
        short ppy = 0;
        short px = 0;
        short py = 0;
        short x = 0;
        short y = 0;
        boolean H = false;
        char dd = (char) 0;
        char gd = (char) 0;
        char cd = (char) 0;
        char ch = (char) 0;
        char ch1 = (char) 0;
        char ch2 = (char) 0;
        char ch3 = (char) 0;
        short sd = 0;
        short from = 0;
        short to = 0;
        short by = 0;
        int c = 0;

        if (up) {
            dd = ' ';
            from = 1;
            to = 18;
            by = 1;
        } else {
            dd = '|';
            from = 18;
            to = 1;
            by = -1;
        }
        H = false;
        for (x = 0; x <= 71; x++) {
            for (y = 19; y >= 0; y -= 1) {
                ch = Runtime.getChar(g[x], y);
                if (ch == 'H')
                    H = true;
                else if (ch == '0')
                    H = false;
                if ((ch != 'H') && (ch != '0')) {
                    if (H)
                        g[x] = Runtime.setChar(g[x], y, dd);
                    else
                        g[x] = Runtime.setChar(g[x], y, ' ');
                }
            }
        }
        if (d == '<')
            sd = -1;
        else
            sd = 1;
        gd = d;
        for (c = 1; c <= 2; c++) {
            y = (short) (from - by);
            do {
                y += by;
                for (x = 1; x <= 70; x++) {
                    ch1 = Runtime.getChar(g[x + sd], y + 1);
                    ch2 = Runtime.getChar(g[x], y + 1);
                    if ((Runtime.getChar(g[x], y) == dd) && (ch1 != 'H') && (ch2 != '0') && (!up || (ch1 == '0') || (ch2 == 'H')) && (Runtime.getChar(g[x + sd], y) == ' ')) {
                        px = x;
                        py = y;
                        ch = Runtime.getChar(g[px + sd], py);
                        while ((py > 0) && (px > 0) && (px < 71) && (Runtime.getChar(g[px], py) == dd) && ((ch == ' ') || (ch == gd)) && (Runtime.getChar(g[px + sd], py + 1) != 'H')) {
                            g[px] = Runtime.setChar(g[px], py, gd);
                            ppy = (short) (py - 1);
                            while (!up && (Runtime.getChar(g[px], ppy) == '|') && (ppy > 0)) {
                                g[px] = Runtime.setChar(g[px], ppy, ' ');
                                ppy--;
                            }
                            if ((Runtime.getChar(g[px], py - 1) == ' ') && (Runtime.getChar(g[px + sd], py) != 'H'))
                                g[px] = Runtime.setChar(g[px], py - 1, gd);
                            py--;
                            px -= sd;
                        }
                    }
                }
            } while (y != to);
            sd = (short) -sd;
            cd = gd;
            if (gd == '<')
                gd = '>';
            else
                gd = '<';
        }
        for (y = 1; y <= 18; y++) {
            for (x = 1; x <= 70; x++) {
                ch = Runtime.getChar(g[x], y);
                if ((ch != '0') && (ch != 'H')) {
                    ch1 = Runtime.getChar(g[x], y - 1);
                    if (up && (Runtime.getChar(g[x], y + 1) == '0') && (ch1 != '0') && (ch1 != 'H')) {
                        g[x] = Runtime.setChar(g[x], y, ' ');
                    } else {
                        ch2 = Runtime.getChar(g[x + sd], y);
                        ch3 = Runtime.getChar(g[x + sd], y + 1);
                        if ((up && ((ch1 == '0') || (ch1 == 'H'))) || ((ch2 != cd) && (ch2 != '0') && (ch2 != 'H') && (ch == ' ') && (ch3 != 'H') && (ch3 != '|')))
                            g[x] = Runtime.setChar(g[x], y, gd);
                    }
                }
                ch = Runtime.getChar(g[x], y);
                if (ch == '<')
                    px = 1;
                else if (ch == '>')
                    px = 3;
                else
                    px = 0;
                drK2[y][x] = (short) ((drK2[y][x] * 4) + (short) px);
            }
        }
    }

    public void InitK2Dirs() {
        // VAR
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);
        char d = (char) 0;
        short x = 0;
        short y = 0;
        OBJECT t = OBJECT.EMPTY;

        do {
            g = (String[]) memory.AllocMem(Runtime.sizeOf(2304, String[].class, 72));
            if (g == null) {
                aNSITerm.ClearLine((byte) 20);
                aNSITerm.ClearLine((byte) 21);
                aNSITerm.Goto((byte) 0, (byte) 20);
                aNSITerm.Color((short) 3);
                WL("***** Not enough memory *****");
                WL(" Retry [Y/N] ?");
                do {
                    aNSITerm.WaitChar(ch);
                } while (ch.get() == ((char) 014));
                aNSITerm.ClearLine((byte) 20);
                aNSITerm.ClearLine((byte) 21);
                if ((ch.get() != 'y') && (ch.get() != 'Y'))
                    return;
            }
        } while (g == null);
        for (y = 0; y <= 19; y++) {
            for (x = 0; x <= 71; x++) {
                drK2[y][x] = 0;
                t = TypeOf(aNSITerm.Report((byte) x, (byte) y));
                if (t == OBJECT.BM)
                    g[x] = Runtime.setChar(g[x], y, 'H');
                else if (t == OBJECT.MR)
                    g[x] = Runtime.setChar(g[x], y, '0');
                else
                    g[x] = Runtime.setChar(g[x], y, ' ');
            }
        }
        d = '<';
        InitK2Dirs_InitDir(false, d);
        d = '>';
        InitK2Dirs_InitDir(false, d);
        d = '<';
        InitK2Dirs_InitDir(true, d);
        d = '>';
        InitK2Dirs_InitDir(true, d);
        memory.FreeMem(new Runtime.FieldRef<>(this::getG, this::setG).asAdrRef());
    }

    private void Find(/* VAR */ Runtime.IRef<Short> x, /* VAR */ Runtime.IRef<Short> y, short sx, short sy, short dx, short dy) {
        // VAR
        int w = 0;
        int h = 0;

        w = dx - sx + 1;
        h = dy - sy + 1;
        do {
            x.set((short) (Rnd() % w));
            x.inc(sx);
            y.set((short) (Rnd() % h));
            y.inc(sy);
        } while (aNSITerm.Report((byte) (short) x.get(), (byte) (short) y.get()) != ' ');
    }

    private void PutObj(OBJECT t, short n, short sx, short sy, short dx, short dy, short nvie, short nseq, char ch) {
        // VAR
        Runtime.Ref<Short> x = new Runtime.Ref<>((short) 0);
        Runtime.Ref<Short> y = new Runtime.Ref<>((short) 0);
        short c = 0;

        for (c = 1; c <= n; c++) {
            Find(x, y, sx, sy, dx, dy);
            Create.invoke(t, (byte) (short) x.get(), (byte) (short) y.get(), (byte) nvie, (byte) nseq, ch);
        }
    }

    public void LoadGame(CreateProc C, /* VAR+WRT */ Runtime.IRef<Byte> ax, /* VAR+WRT */ Runtime.IRef<Byte> ay) {
        // VAR
        Runtime.Ref<String> posch = new Runtime.Ref<>("");
        int pos = 0;
        short bcount = 0;
        short t = 0;
        short c = 0;
        short c1 = 0;
        short c2 = 0;
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);
        Runtime.Ref<Character> tmp = new Runtime.Ref<>((char) 0);

        Create = C;
        aNSITerm.ClearScreen();
        aNSITerm.Goto((byte) 0, (byte) 20);
        aNSITerm.Color((short) 7);
        aNSITerm.WriteString("Loading, please wait... ");
        aNSITerm.Goto((byte) 0, (byte) 0);
        ndoor = 0;
        if (level < 10) {
            fh = files.OpenFile(new Runtime.Ref<>(DecorFile), EnumSet.of(AccessFlags.accessRead));
            checks.Check(fh == files.noFile, Runtime.castToRef(memory.ADS("Can't open data file:"), String.class), Runtime.castToRef(memory.ADS(DecorFile), String.class));
            c1 = (short) (level * 10 + game + 1);
            c2 = c1;
            while (c1 > 0) {
                checks.Check(files.ReadFileBytes(fh, posch, 4) != 4, Runtime.castToRef(memory.ADS("Error reading table in file"), String.class), new Runtime.Ref<>(DecorFile));
                pos = ((Runtime.getChar(posch, 0) * 256 + Runtime.getChar(posch, 1)) * 256 + Runtime.getChar(posch, 2)) * 256 + Runtime.getChar(posch, 3);
                c1--;
            }
            pos += 400 - c2 * 4;
            checks.Check(files.SkipFileBytes(fh, pos) != pos, Runtime.castToRef(memory.ADS(DecorFile), String.class), Runtime.castToRef(memory.ADS("bad jump table"), String.class));
            bcount = 0;
            for (c1 = 0; c1 <= 19; c1++) {
                for (c2 = 0; c2 <= 71; c2++) {
                    if (bcount == 0) {
                        checks.Check(files.ReadFileBytes(fh, ch, 1) != 1, Runtime.castToRef(memory.ADS("Read error in file"), String.class), new Runtime.Ref<>(DecorFile));
                        if (ch.get() > ((char) 0177)) {
                            ch.set((char) ((char) ch.get() - 128));
                            checks.Check(files.ReadFileBytes(fh, tmp, 1) != 1, Runtime.castToRef(memory.ADS("Read error in file"), String.class), new Runtime.Ref<>(DecorFile));
                            bcount = (short) (char) tmp.get();
                        } else {
                            bcount = 1;
                        }
                    }
                    bcount--;
                    aNSITerm.Goto((byte) c2, (byte) c1);
                    if (ch.get() == 'g')
                        ch.set('£');
                    switch (TypeOf(ch.get())) {
                        case PLAYER -> {
                            Create.invoke(OBJECT.PLAYER, (byte) c2, (byte) c1, pvie, (byte) 0, '*');
                        }
                        case K0 -> {
                            Create.invoke(OBJECT.K0, (byte) c2, (byte) c1, (byte) (level + 2), (byte) 2, '&');
                        }
                        case K1 -> {
                            Create.invoke(OBJECT.K1, (byte) c2, (byte) c1, (byte) (level + 1), (byte) 0, '+');
                        }
                        case K2 -> {
                            Create.invoke(OBJECT.K2, (byte) c2, (byte) c1, (byte) (level / 2 + 1), (byte) 0, 'X');
                        }
                        case K3 -> {
                            Create.invoke(OBJECT.K3, (byte) c2, (byte) c1, (byte) (level + 1), (byte) 0, '#');
                        }
                        case K4 -> {
                            Create.invoke(OBJECT.K4, (byte) c2, (byte) c1, (byte) 4, (byte) 2, 'x');
                        }
                        case GN1 -> {
                            Create.invoke(OBJECT.GN1, (byte) c2, (byte) c1, (byte) 3, (byte) 0, ch.get());
                        }
                        case GN2 -> {
                            Create.invoke(OBJECT.GN2, (byte) c2, (byte) c1, (byte) 1, (byte) 0, ch.get());
                        }
                        case DL -> {
                            Create.invoke(OBJECT.DL, (byte) c2, (byte) c1, (byte) 1, (byte) 0, ' ');
                        }
                        case ASC -> {
                            Create.invoke(OBJECT.ASC, (byte) c2, (byte) c1, (byte) 1, (byte) 0, '=');
                        }
                        case PIC -> {
                            Create.invoke(OBJECT.PIC, (byte) c2, (byte) c1, (byte) level, (byte) 2, 'V');
                        }
                        case WOOF -> {
                            Create.invoke(OBJECT.WOOF, (byte) c2, (byte) c1, (byte) 1, (byte) 0, 'W');
                        }
                        case TPLAT -> {
                            Create.invoke(OBJECT.TPLAT, (byte) c2, (byte) c1, (byte) 1, (byte) 0, ' ');
                        }
                        case NID -> {
                            Create.invoke(OBJECT.NID, (byte) c2, (byte) c1, (byte) 2, (byte) 0, 'Z');
                        }
                        case BUB -> {
                            Create.invoke(OBJECT.BUB, (byte) c2, (byte) c1, (byte) 4, (byte) 0, 'N');
                        }
                        case BN, SBN -> {
                            aNSITerm.Color((short) 2);
                            aNSITerm.Write(ch.get());
                        }
                        default -> {
                            if (ch.get() == '!')
                                ndoor++;
                            if (ch.get() == 'A') {
                                ax.set((byte) c2);
                                ay.set((byte) c1);
                                aNSITerm.Color((short) 7);
                                aNSITerm.Write('0');
                            } else if (ch.get() == 'a') {
                                Create.invoke(OBJECT.PLAT, (byte) c2, (byte) c1, (byte) 1, (byte) 0, 'T');
                                ppos[0].x = (byte) c2;
                                ppos[0].y = (byte) c1;
                            } else if ((ch.get() >= 'b') && (ch.get() <= 'd')) {
                                c = (short) ((char) ch.get() - 'a');
                                aNSITerm.Write(' ');
                                ppos[c].x = (byte) c2;
                                ppos[c].y = (byte) c1;
                                t = c;
                                if (pcount < t)
                                    pcount = t;
                            } else {
                                SetColor(ch.get());
                                aNSITerm.Write(ch.get());
                            }
                        }
                    }
                }
                if (c1 < 19) {
                    if (files.ReadFileBytes(fh, ch, 1) == 1) {
                    }
                    checks.Check((bcount != 0) || (ch.get() != ((char) 012)), Runtime.castToRef(memory.ADS(DecorFile), String.class), Runtime.castToRef(memory.ADS("wrong format"), String.class));
                    aNSITerm.WriteLn();
                }
            }
            files.CloseFile(new Runtime.FieldRef<>(this::getFh, this::setFh));
            c1 = 20;
            if (game % 3 == 0)
                c1 = 25;
            if (game % 2 == 0)
                c1 = 15;
            Complete(OBJECT.K3, (byte) (level + 1), c1, '#');
            c1 = (short) (level / 2 + 1);
            c2 = 0;
            if (game == 6)
                c2 = 2;
            else if (game == 7)
                c2 = 4;
            else if (game >= 5)
                c2 = 6;
            if (pvie > 6)
                c2++;
            Complete(OBJECT.K2, (byte) c1, c2, 'X');
            if (game == 9)
                Complete(OBJECT.K1, (byte) (level + 1), (short) 6, '+');
            else
                Complete(OBJECT.K1, (byte) (level + 1), (short) ((game % 3) * 3), '+');
            if ((game > 1) && (game != 5) && (game != 8))
                Complete(OBJECT.K4, (byte) 4, (short) 1, 'x');
        } else {
            grotteBonus.BonusLevel(ax, ay);
        }
        aNSITerm.ClearLine((byte) 20);
        aNSITerm.Goto((byte) 0, (byte) 20);
        aNSITerm.Color((short) 7);
    }


    // Support

    private static GrotteSupport instance;

    public static GrotteSupport instance() {
        if (instance == null)
            new GrotteSupport(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        made[0] = new Runtime.RangeSet(Memory.SET16_r).with(9);
        made[1] = new Runtime.RangeSet(Memory.SET16_r).with(9);
        made[2] = new Runtime.RangeSet(Memory.SET16_r).with(9);
        made[3] = new Runtime.RangeSet(Memory.SET16_r).with(9);
        made[4] = new Runtime.RangeSet(Memory.SET16_r).with(9);
        made[5] = new Runtime.RangeSet(Memory.SET16_r).with(9);
        made[6] = new Runtime.RangeSet(Memory.SET16_r).with(9);
        made[7] = new Runtime.RangeSet(Memory.SET16_r).withRange(0, 9);
        made[8] = new Runtime.RangeSet(Memory.SET16_r).withRange(0, 9);
        made[9] = new Runtime.RangeSet(Memory.SET16_r).withRange(0, 9);
        made[10] = new Runtime.RangeSet(Memory.SET16_r).with(0, 10);
        checks.AddTermProc(BigFlush_ref);
        BigInit();
    }

    public void close() {
    }

}
