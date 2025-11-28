package ch.chaos.grotte;

import java.lang.Runnable;
import java.util.EnumSet;

import ch.chaos.library.ANSITerm;
import ch.chaos.library.Checks;
import ch.chaos.library.Clock;
import ch.chaos.library.Files;
import ch.chaos.library.Files.AccessFlags;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


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

    /*$ CStrings:= FALSE */
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
        public void invoke(OBJECT arg1, int arg2, int arg3, int arg4, int arg5, char arg6);
    }

    public static class Attr { // RECORD

        public int nb;
        public int dt;
        public int nt;
        public int timeout;
        public int ct;
        public int co;


        public int getNb() {
            return this.nb;
        }

        public void setNb(int nb) {
            this.nb = nb;
        }

        public int getDt() {
            return this.dt;
        }

        public void setDt(int dt) {
            this.dt = dt;
        }

        public int getNt() {
            return this.nt;
        }

        public void setNt(int nt) {
            this.nt = nt;
        }

        public int getTimeout() {
            return this.timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getCt() {
            return this.ct;
        }

        public void setCt(int ct) {
            this.ct = ct;
        }

        public int getCo() {
            return this.co;
        }

        public void setCo(int co) {
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

        public int x;
        public int y;


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
    public int[][] drsK2 = new int[20][72];
    public int[][] drK2 = new int[20][];
    public _Ppos[] ppos = Runtime.initArray(new _Ppos[4]);
    public int l2count;
    public int oldl2;
    public int blvcount;
    public int oldblv;
    public int pcount;
    public int ndoor;
    public int level;
    public int game;
    public int pvie;
    public int oldpvie;
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

    public int[][] getDrsK2() {
        return this.drsK2;
    }

    public void setDrsK2(int[][] drsK2) {
        this.drsK2 = drsK2;
    }

    public int[][] getDrK2() {
        return this.drK2;
    }

    public void setDrK2(int[][] drK2) {
        this.drK2 = drK2;
    }

    public _Ppos[] getPpos() {
        return this.ppos;
    }

    public void setPpos(_Ppos[] ppos) {
        this.ppos = ppos;
    }

    public int getL2count() {
        return this.l2count;
    }

    public void setL2count(int l2count) {
        this.l2count = l2count;
    }

    public int getOldl2() {
        return this.oldl2;
    }

    public void setOldl2(int oldl2) {
        this.oldl2 = oldl2;
    }

    public int getBlvcount() {
        return this.blvcount;
    }

    public void setBlvcount(int blvcount) {
        this.blvcount = blvcount;
    }

    public int getOldblv() {
        return this.oldblv;
    }

    public void setOldblv(int oldblv) {
        this.oldblv = oldblv;
    }

    public int getPcount() {
        return this.pcount;
    }

    public void setPcount(int pcount) {
        this.pcount = pcount;
    }

    public int getNdoor() {
        return this.ndoor;
    }

    public void setNdoor(int ndoor) {
        this.ndoor = ndoor;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getGame() {
        return this.game;
    }

    public void setGame(int game) {
        this.game = game;
    }

    public int getPvie() {
        return this.pvie;
    }

    public void setPvie(int pvie) {
        this.pvie = pvie;
    }

    public int getOldpvie() {
        return this.oldpvie;
    }

    public void setOldpvie(int oldpvie) {
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
    private int lastgame;
    private int lastlevel;
    private int rndcount;
    private int randomcount;
    private String[] g /* POINTER */;
    private int[] cols = new int[7];


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

    public int getLastgame() {
        return this.lastgame;
    }

    public void setLastgame(int lastgame) {
        this.lastgame = lastgame;
    }

    public int getLastlevel() {
        return this.lastlevel;
    }

    public void setLastlevel(int lastlevel) {
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

    public int[] getCols() {
        return this.cols;
    }

    public void setCols(int[] cols) {
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

    private void WA(int x, int y, String s) {
        aNSITerm.Goto(x, y);
        aNSITerm.WriteString(s);
    }

    public int Random() {
        /*$ OverflowChk:= FALSE */
        randomcount = (randomcount * 13077 + 6925) % 32768;
        return randomcount;
    }

    /*$ POP OverflowChk */
    public int Rnd() {
        /*$ OverflowChk:= FALSE  RangeChk:= FALSE */
        rndcount = (rndcount * 13077 + 6925 + trigo.RND()) % 32768;
        return rndcount;
    }

    /*$ POP OverflowChk  POP RangeChk */
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
        aNSITerm.Color(1);
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
        int x = 0;
        int y = 0;
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);
        String[] lines = new String[5];

        lines[0] = l1;
        lines[1] = l2;
        lines[2] = l3;
        lines[3] = l4;
        lines[4] = l5;
        aNSITerm.Goto(0, 0);
        aNSITerm.Color(1);
        for (y = 0; y <= 19; y++) {
            for (x = 0; x <= 71; x++) {
                aNSITerm.Write('#');
            }
            aNSITerm.WriteLn();
        }
        clock.StartTime(time);
        for (x = 6; x >= 0; x -= 1) {
            aNSITerm.Color(cols[x]);
            WA(7, 6, " &&&&& &&&&& && && &&&&&        &&&  &   & &&&&& &&&&& ");
            WA(7, 7, " &     &   & &&&&& &           &   & &   & &     &   & ");
            WA(7, 8, " & &&& &&&&& & & & &&&&        &   &  & &  &&&&  &&&&& ");
            WA(7, 9, " &   & &   & &   & &           &   &  & &  &     &&    ");
            WA(7, 10, " &&&&& &   & &   & &&&&&        &&&    &   &&&&& & &&& ");
            aNSITerm.WriteLn();
            /* Flush; */
            if (!clock.WaitTime(time, 512))
                clock.StartTime(time);
        }
        if (clock.WaitTime(time, 4096)) {
        }
        for (x = 1; x <= 10; x++) {
            if (((x % 2) != 0))
                aNSITerm.Color(7);
            else
                aNSITerm.Color(6);
            WA(0, 21, "Score: ");
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

        /*$ OverflowChk:= FALSE  RangeChk:= FALSE */
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

    /*$ POP OverflowChk  POP RangeChk */
    private void WriteCode(int c, int s) {
        // VAR
        int i = 0;

        /* It is very unfortunate to dicover that most text font will
            * not properly distinguish 0 and O, or 1, l and I !
            */
        c = (c + s) % 64;
        i = c;
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
        aNSITerm.Color(1);
        aNSITerm.Goto(30, 1);
        aNSITerm.WriteString("************");
        aNSITerm.Goto(30, 3);
        aNSITerm.WriteString("************");
        aNSITerm.WriteAt(30, 2, '*');
        aNSITerm.WriteAt(41, 2, '*');
        aNSITerm.Color(3);
        p.set(0);
        aNSITerm.Goto(32, 2);
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
        aNSITerm.Goto(21, 8);
        aNSITerm.Color(6);
        sum = 17;
        for (y = 0; y <= 26; y++) {
            x = (int) GetVal(p, pass, 6);
            WriteCode(x, y);
            sum += x;
            if (sum >= 64)
                sum -= 63;
        }
        WriteCode(sum, 27);
        /* Flush; */
        aNSITerm.Goto(4, 12);
        aNSITerm.Color(7);
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
        aNSITerm.Color(1);
        aNSITerm.Goto(26, 1);
        aNSITerm.WriteString("*******************");
        aNSITerm.Goto(26, 3);
        aNSITerm.WriteString("*******************");
        aNSITerm.WriteAt(26, 2, '*');
        aNSITerm.WriteAt(44, 2, '*');
        aNSITerm.Color(2);
        aNSITerm.Goto(28, 2);
        aNSITerm.WriteString("Enter Password:");
        aNSITerm.Goto(21, 8);
        aNSITerm.Color(4);
        for (x = 0; x <= 27; x++) {
            aNSITerm.Write('#');
        }
        aNSITerm.Goto(21, 8);
        aNSITerm.Color(6);
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
            blvcount = (int) GetVal(p, pass, 6);
            if (blvcount > 0)
                made[10].excl(0);
            else
                made[10].incl(0);
            score = GetVal(p, pass, 32);
            followscore = ((score + 1000) / 1000) * 1000;
            pvie = (int) GetVal(p, pass, 7);
            l2count = (int) GetVal(p, pass, 6);
        } else {
            aNSITerm.Goto(25, 10);
            aNSITerm.Color(3);
            aNSITerm.WriteString("* Invalid password *");
            aNSITerm.Goto(25, 11);
            aNSITerm.Color(7);
            aNSITerm.WriteString("Press any key");
            WaitAChar(ch);
        }
        if (pvie <= 0)
            Gameover(0);
    }

    private boolean ReadGame_Made(int level, int game) {
        return made[level].contains(game);
    }

    private void ReadGame_ViewGame(int level, int game, boolean on) {
        // VAR
        boolean test1 = false;
        boolean test2 = false;

        if (level != 10) {
            aNSITerm.Goto(game * 3 + 2, level + 8);
        } else {
            aNSITerm.Goto(2, 18);
            if (on) {
                if (!ReadGame_Made(10, 0)) {
                    aNSITerm.Color(2);
                    aNSITerm.WriteString("BONUS LEVEL");
                }
            } else {
                aNSITerm.WriteString("           ");
            }
            return;
        }
        if (ReadGame_Made(level, game)) {
            aNSITerm.Color(1);
        } else {
            test1 = (game == 9);
            test2 = (level >= 7);
            if (test1 != test2)
                aNSITerm.Color(3);
            else
                aNSITerm.Color(2);
            if (test1 && test2)
                aNSITerm.Color(7);
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

    private void ReadGame_DisplayGrotte(int x) {
        WA(x, 0, " 00000  00000   000   00000  00000  00000");
        WA(x, 1, " 0      0   0  0   0    0      0    0");
        WA(x, 2, " 0 000  00000  0   0    0      0    0000");
        WA(x, 3, " 0   0  00     0   0    0      0    0");
        WA(x, 4, " 00000  0 000   000     0      0    00000");
        aNSITerm.Goto(0, 0);
    }

    private void ReadGame_Display(/* VAR */ Runtime.IRef<Integer> left) {
        // VAR
        int c1 = 0;
        int c2 = 0;

        aNSITerm.ClearScreen();
        clock.StartTime(time);
        if (first) {
            for (c1 = 1; c1 <= 3; c1++) {
                aNSITerm.Color(c1);
                ReadGame_DisplayGrotte(0);
                if (!clock.WaitTime(time, 512))
                    clock.StartTime(time);
            }
            for (c1 = 1; c1 <= 14; c1++) {
                ReadGame_DisplayGrotte(c1);
                if (!clock.WaitTime(time, 200))
                    clock.StartTime(time);
            }
            first = false;
        }
        aNSITerm.Color(3);
        ReadGame_DisplayGrotte(15);
        aNSITerm.Color(1);
        WA(28, 5, "(C) 2000 Nicky");
        aNSITerm.Color(5);
        WA(38, 7, "Use the following keys from the numeric");
        WA(38, 8, "pad (Num Lock) to move during the game:");
        WA(40, 9, "{4}: start moving to the left");
        WA(40, 10, "{6}: start moving to the right");
        WA(40, 11, "{5}: stop moving");
        WA(40, 12, "<SPACE> or {0}: jump");
        WA(40, 13, "{1}, {3}, {8}, {2}:");
        WA(40, 14, " fire left, right, up and down");
        WA(40, 15, "{7}, {9}: big fire left and right");
        aNSITerm.Color(4);
        WA(38, 16, "{q}: quit      {e}: give up level");
        WA(38, 17, "{r}: refresh   {m}: choose random");
        WA(38, 18, "{p}: pause     {s}, {l}: save, load");
        WA(38, 19, "{arrow}: move  {shift arrow}: fire");
        aNSITerm.Color(7);
        WA(0, 7, "Choose a level and press <RETURN>:");
        WA(0, 21, "Score: ");
        WriteCard(score);
        WA(36, 21, "Lives: ");
        aNSITerm.Color(3);
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
        if (!ReadGame_Made(10, 0))
            ReadGame_ViewGame(10, 0, true);
    }

    public void ReadGame(/* VAR */ Runtime.IRef<Integer> level, /* VAR */ Runtime.IRef<Integer> game) {
        // VAR
        boolean on = false;
        int gc = 0;
        int c1 = 0;
        int c2 = 0;
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
            level.set(0);
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
                game.set(0);
                level.inc();
                if (level.get() > 10)
                    level.set(0);
            }
        }
        if (gc == 0) {
            aNSITerm.ClearScreen();
            clock.StartTime(time);
            if (clock.WaitTime(time, 3072)) {
            }
            aNSITerm.Color(7);
            aNSITerm.Goto(0, 20);
            aNSITerm.WriteString("**** YOU'VE FINISHED THE GAME !!! ****");
            aNSITerm.Goto(0, 0);
            aNSITerm.Color(3);
            for (c1 = 0; c1 <= 19; c1++) {
                for (c2 = 0; c2 <= 71; c2++) {
                    aNSITerm.Write('%');
                }
                aNSITerm.WriteLn();
            }
            grotteSounds.FinalMusic();
            clock.StartTime(time);
            if (clock.WaitTime(time, 16000)) {
            }
            do {
                score += 1000;
                pvie--;
                aNSITerm.Color(7);
                WA(0, 21, "Score: ");
                WriteCard(score);
                WA(36, 21, "Lives: ");
                aNSITerm.Color(3);
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
            aNSITerm.Goto(0, 22);
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
            if ((level.get() == 10) && (ReadGame_Made(10, 0)))
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
                if (!ReadGame_Made(10, 0)) {
                    level.set(10);
                    game.set(0);
                } else {
                    if (pvie > 25)
                        level.set(125);
                    else
                        level.set(pvie * 5);
                    level.inc(Rnd() % 32);
                    if (level.get() != 0)
                        level.dec();
                    best = level.get();
                    best = (left.get() * best) / 160;
                    level.set(0);
                    game.set(0);
                    while (ReadGame_Made(level.get(), game.get())) {
                        game.inc();
                        if (game.get() >= 10) {
                            game.set(0);
                            level.set((level.get() + 1) % 10);
                        }
                    }
                    while (best > 0) {
                        do {
                            game.inc();
                            if (game.get() >= 10) {
                                game.set(0);
                                level.set((level.get() + 1) % 10);
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
                aNSITerm.Goto(1, 19);
                aNSITerm.Color(6);
                c2 = 40;
                aNSITerm.WriteString("Press {I} for more instructions");
            } else if (c2 == 1) {
                aNSITerm.Goto(1, 19);
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
            game.set(0);
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
        aNSITerm.Goto(67, 20);
        aNSITerm.Color(7);
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
                aNSITerm.Color(1);
            }
            case K0 -> {
                aNSITerm.Color(3);
            }
            case BM, BALL -> {
                aNSITerm.Color(6);
            }
            case PLAYER, BN, DL, L1, L2, SBN -> {
                aNSITerm.Color(2);
            }
            case ASC, PLAT, WOOF, TPLAT -> {
                aNSITerm.Color(4);
            }
            case GN1, GN2, NID, BUB, PIC -> {
                aNSITerm.Color(5);
            }
            default -> {
                if ((ch == 'A') || (ch == '!'))
                    aNSITerm.Color(3);
                else
                    aNSITerm.Color(7);
            }
        }
    }

    private void Complete(OBJECT t, int nvie, int count, char ch) {
        // VAR
        int nx = 0;
        int ny = 0;
        int ns = 0;

        if ((t == OBJECT.K4) || (t == OBJECT.PIC))
            ns = 2;
        else
            ns = 0;
        while (attr[t.ordinal()].nb < count) {
            do {
                if (Random() == 0) {
                }
                nx = Random() % 72;
                ny = Random() % 20;
            } while (aNSITerm.Report(nx, ny) != ' ');
            Create.invoke(t, nx, ny, nvie, ns, ch);
        }
    }

    private void NoMem() {
        WA(0, 20, "***** Not enough memory *****");
        clock.StartTime(time);
        if (clock.WaitTime(time, 1024)) {
        }
    }

    private void DosErr() {
        WA(0, 20, "***** File input error *****");
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
        aNSITerm.Color(7);
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
        int count = 0;
        int length = 0;
        int c = 0;
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
            aNSITerm.Color(7);
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
                    length = memory.StrLength(new Runtime.FieldExprRef<>(current, Choice::getName, Choice::setName));
                    if (Runtime.getChar(current.name, length - 1) == ch.get())
                        break;
                    current = current.next;
                }
                if (current == null)
                    break;
                aNSITerm.Write('{');
                aNSITerm.Write(ch.get());
                aNSITerm.WriteString("}: ");
                length = memory.StrLength(new Runtime.FieldExprRef<>(current, Choice::getName, Choice::setName));
                c = length - 1;
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
                ch.set((char) ((int) ch.get() - 32));
            /* Flush;*/
            aNSITerm.Color(6);
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
        int ppy = 0;
        int px = 0;
        int py = 0;
        int x = 0;
        int y = 0;
        boolean H = false;
        char dd = (char) 0;
        char gd = (char) 0;
        char cd = (char) 0;
        char ch = (char) 0;
        char ch1 = (char) 0;
        char ch2 = (char) 0;
        char ch3 = (char) 0;
        int sd = 0;
        int from = 0;
        int to = 0;
        int by = 0;
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
        /* fill with JUMP up to any H */
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
        /* where JUMP and MR, go on it */
        if (d == '<')
            sd = -1;
        else
            sd = 1;
        gd = d;
        for (c = 1; c <= 2; c++) {
            y = from - by;
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
                            ppy = py - 1;
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
            sd = -sd;
            cd = gd;
            if (gd == '<')
                gd = '>';
            else
                gd = '<';
        }
        /* filling */
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
                drK2[y][x] = (drK2[y][x] * 4) + (int) px;
            }
        }
    }

    public void InitK2Dirs() {
        // VAR
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);
        char d = (char) 0;
        int x = 0;
        int y = 0;
        OBJECT t = OBJECT.EMPTY;

        do {
            g = (String[]) memory.AllocMem(Runtime.sizeOf(2304, String[].class, 72));
            if (g == null) {
                aNSITerm.ClearLine(20);
                aNSITerm.ClearLine(21);
                aNSITerm.Goto(0, 20);
                aNSITerm.Color(3);
                WL("***** Not enough memory *****");
                WL(" Retry [Y/N] ?");
                do {
                    aNSITerm.WaitChar(ch);
                } while (ch.get() == ((char) 014));
                aNSITerm.ClearLine(20);
                aNSITerm.ClearLine(21);
                if ((ch.get() != 'y') && (ch.get() != 'Y'))
                    return;
            }
        } while (g == null);
        for (y = 0; y <= 19; y++) {
            for (x = 0; x <= 71; x++) {
                drK2[y][x] = 0;
                t = TypeOf(aNSITerm.Report(x, y));
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

    /* *** Niveaux Bonus *****/
    private void Find(/* VAR */ Runtime.IRef<Integer> x, /* VAR */ Runtime.IRef<Integer> y, int sx, int sy, int dx, int dy) {
        // VAR
        int w = 0;
        int h = 0;

        w = dx - sx + 1;
        h = dy - sy + 1;
        do {
            x.set(Rnd() % w);
            x.inc(sx);
            y.set(Rnd() % h);
            y.inc(sy);
        } while (aNSITerm.Report(x.get(), y.get()) != ' ');
    }

    private void PutObj(OBJECT t, int n, int sx, int sy, int dx, int dy, int nvie, int nseq, char ch) {
        // VAR
        Runtime.Ref<Integer> x = new Runtime.Ref<>(0);
        Runtime.Ref<Integer> y = new Runtime.Ref<>(0);
        int c = 0;

        for (c = 1; c <= n; c++) {
            Find(x, y, sx, sy, dx, dy);
            Create.invoke(t, x.get(), y.get(), nvie, nseq, ch);
        }
    }

    public void LoadGame(CreateProc C, /* VAR+WRT */ Runtime.IRef<Integer> ax, /* VAR+WRT */ Runtime.IRef<Integer> ay) {
        // VAR
        Runtime.Ref<String> posch = new Runtime.Ref<>("");
        long pos = 0L;
        int bcount = 0;
        int t = 0;
        int c = 0;
        int c1 = 0;
        int c2 = 0;
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);
        Runtime.Ref<Character> tmp = new Runtime.Ref<>((char) 0);

        Create = C;
        aNSITerm.ClearScreen();
        aNSITerm.Goto(0, 20);
        aNSITerm.Color(7);
        aNSITerm.WriteString("Loading, please wait... ");
        aNSITerm.Goto(0, 0);
        ndoor = 0;
        if (level < 10) {
            /* Load */
            fh = files.OpenFile(new Runtime.Ref<>(DecorFile), EnumSet.of(AccessFlags.accessRead));
            checks.Check(fh == files.noFile, Runtime.castToRef(memory.ADS("Can't open data file:"), String.class), Runtime.castToRef(memory.ADS(DecorFile), String.class));
            c1 = level * 10 + game + 1;
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
                            ch.set((char) ((int) ch.get() - 128));
                            checks.Check(files.ReadFileBytes(fh, tmp, 1) != 1, Runtime.castToRef(memory.ADS("Read error in file"), String.class), new Runtime.Ref<>(DecorFile));
                            bcount = (int) tmp.get();
                        } else {
                            bcount = 1;
                        }
                    }
                    bcount--;
                    aNSITerm.Goto(c2, c1);
                    if (ch.get() == 'g')
                        ch.set('£');
                    switch (TypeOf(ch.get())) {
                        case PLAYER -> {
                            Create.invoke(OBJECT.PLAYER, c2, c1, pvie, 0, '*');
                        }
                        case K0 -> {
                            Create.invoke(OBJECT.K0, c2, c1, level + 2, 2, '&');
                        }
                        case K1 -> {
                            Create.invoke(OBJECT.K1, c2, c1, level + 1, 0, '+');
                        }
                        case K2 -> {
                            Create.invoke(OBJECT.K2, c2, c1, level / 2 + 1, 0, 'X');
                        }
                        case K3 -> {
                            Create.invoke(OBJECT.K3, c2, c1, level + 1, 0, '#');
                        }
                        case K4 -> {
                            Create.invoke(OBJECT.K4, c2, c1, 4, 2, 'x');
                        }
                        case GN1 -> {
                            Create.invoke(OBJECT.GN1, c2, c1, 3, 0, ch.get());
                        }
                        case GN2 -> {
                            Create.invoke(OBJECT.GN2, c2, c1, 1, 0, ch.get());
                        }
                        case DL -> {
                            Create.invoke(OBJECT.DL, c2, c1, 1, 0, ' ');
                        }
                        case ASC -> {
                            Create.invoke(OBJECT.ASC, c2, c1, 1, 0, '=');
                        }
                        case PIC -> {
                            Create.invoke(OBJECT.PIC, c2, c1, level, 2, 'V');
                        }
                        case WOOF -> {
                            Create.invoke(OBJECT.WOOF, c2, c1, 1, 0, 'W');
                        }
                        case TPLAT -> {
                            Create.invoke(OBJECT.TPLAT, c2, c1, 1, 0, ' ');
                        }
                        case NID -> {
                            Create.invoke(OBJECT.NID, c2, c1, 2, 0, 'Z');
                        }
                        case BUB -> {
                            Create.invoke(OBJECT.BUB, c2, c1, 4, 0, 'N');
                        }
                        case BN, SBN -> {
                            aNSITerm.Color(2);
                            aNSITerm.Write(ch.get());
                        }
                        default -> {
                            if (ch.get() == '!')
                                ndoor++;
                            if (ch.get() == 'A') {
                                ax.set(c2);
                                ay.set(c1);
                                aNSITerm.Color(7);
                                aNSITerm.Write('0');
                            } else if (ch.get() == 'a') {
                                Create.invoke(OBJECT.PLAT, c2, c1, 1, 0, 'T');
                                ppos[0].x = c2;
                                ppos[0].y = c1;
                            } else if ((ch.get() >= 'b') && (ch.get() <= 'd')) {
                                c = (int) ch.get() - 'a';
                                aNSITerm.Write(' ');
                                ppos[c].x = c2;
                                ppos[c].y = c1;
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
            Complete(OBJECT.K3, level + 1, c1, '#');
            c1 = level / 2 + 1;
            c2 = 0;
            if (game == 6)
                c2 = 2;
            else if (game == 7)
                c2 = 4;
            else if (game >= 5)
                c2 = 6;
            if (pvie > 6)
                c2++;
            Complete(OBJECT.K2, c1, c2, 'X');
            if (game == 9)
                Complete(OBJECT.K1, level + 1, 6, '+');
            else
                Complete(OBJECT.K1, level + 1, (game % 3) * 3, '+');
            if ((game > 1) && (game != 5) && (game != 8))
                Complete(OBJECT.K4, 4, 1, 'x');
        } else {
            grotteBonus.BonusLevel(ax, ay);
        }
        aNSITerm.ClearLine(20);
        aNSITerm.Goto(0, 20);
        aNSITerm.Color(7);
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
