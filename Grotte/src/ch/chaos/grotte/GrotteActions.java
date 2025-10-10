package ch.chaos.grotte;

import ch.chaos.grotte.GrotteSupport.DIRECTION;
import ch.chaos.grotte.GrotteSupport.OBJECT;
import ch.chaos.grotte.GrotteSupport._Stat;
import ch.chaos.library.ANSITerm;
import ch.chaos.library.Clock;
import ch.chaos.library.Memory;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class GrotteActions {

    // Imports
    private final ANSITerm aNSITerm;
    private final Clock clock;
    private final GrotteSounds grotteSounds;
    private final GrotteSupport grotteSupport;
    private final Trigo trigo;


    private GrotteActions() {
        instance = this; // Set early to handle circular dependencies
        aNSITerm = ANSITerm.instance();
        clock = Clock.instance();
        grotteSounds = GrotteSounds.instance();
        grotteSupport = GrotteSupport.instance();
        trigo = Trigo.instance();
    }


    // CONST

    public static final int N = 0;
    public static final int G = 1;
    public static final int H = 2;
    public static final int D = 3;
    public static final int B = 4;
    public static final int ND = 0;
    public static final int SQ = 1;
    public static final int MAXOBJ = 96;


    // TYPE

    public static class Object { // RECORD

        public int next;
        public int prev;
        public OBJECT type;
        public Runtime.RangeSet flags = new Runtime.RangeSet(Memory.SET16_r);
        public int x;
        public int y;
        public int vie;
        public int seq;
        public boolean b0;
        // CASE "b0" {
        public int jump; // TRUE
        public DIRECTION dir; // TRUE
        public int d; // FALSE
        public int v; // FALSE
        public int bx; // FALSE
        public int by; // FALSE
        // }
        public char ch1;
        public char ch2;


        public int getNext() {
            return this.next;
        }

        public void setNext(int next) {
            this.next = next;
        }

        public int getPrev() {
            return this.prev;
        }

        public void setPrev(int prev) {
            this.prev = prev;
        }

        public OBJECT getType() {
            return this.type;
        }

        public void setType(OBJECT type) {
            this.type = type;
        }

        public Runtime.RangeSet getFlags() {
            return this.flags;
        }

        public void setFlags(Runtime.RangeSet flags) {
            this.flags = flags;
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

        public int getVie() {
            return this.vie;
        }

        public void setVie(int vie) {
            this.vie = vie;
        }

        public int getSeq() {
            return this.seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }

        public boolean isB0() {
            return this.b0;
        }

        public void setB0(boolean b0) {
            this.b0 = b0;
        }

        public int getJump() {
            return this.jump;
        }

        public void setJump(int jump) {
            this.jump = jump;
        }

        public DIRECTION getDir() {
            return this.dir;
        }

        public void setDir(DIRECTION dir) {
            this.dir = dir;
        }

        public int getD() {
            return this.d;
        }

        public void setD(int d) {
            this.d = d;
        }

        public int getV() {
            return this.v;
        }

        public void setV(int v) {
            this.v = v;
        }

        public int getBx() {
            return this.bx;
        }

        public void setBx(int bx) {
            this.bx = bx;
        }

        public int getBy() {
            return this.by;
        }

        public void setBy(int by) {
            this.by = by;
        }

        public char getCh1() {
            return this.ch1;
        }

        public void setCh1(char ch1) {
            this.ch1 = ch1;
        }

        public char getCh2() {
            return this.ch2;
        }

        public void setCh2(char ch2) {
            this.ch2 = ch2;
        }


        public void copyFrom(Object other) {
            this.next = other.next;
            this.prev = other.prev;
            this.type = other.type;
            this.flags.copyFrom(other.flags);
            this.x = other.x;
            this.y = other.y;
            this.vie = other.vie;
            this.seq = other.seq;
            this.b0 = other.b0;
            this.jump = other.jump;
            this.dir = other.dir;
            this.d = other.d;
            this.v = other.v;
            this.bx = other.bx;
            this.by = other.by;
            this.ch1 = other.ch1;
            this.ch2 = other.ch2;
        }

        public Object newCopy() {
            Object copy = new Object();
            copy.copyFrom(this);
            return copy;
        }

    }

    @FunctionalInterface
    public static interface MoveProc { // PROCEDURE Type
        public void invoke(int arg1);
    }

    public static class _Gn { // RECORD

        public Runtime.RangeSet hset = new Runtime.RangeSet(Memory.SET16_r);
        public Runtime.RangeSet lset = new Runtime.RangeSet(Memory.SET16_r);


        public Runtime.RangeSet getHset() {
            return this.hset;
        }

        public void setHset(Runtime.RangeSet hset) {
            this.hset = hset;
        }

        public Runtime.RangeSet getLset() {
            return this.lset;
        }

        public void setLset(Runtime.RangeSet lset) {
            this.lset = lset;
        }


        public void copyFrom(_Gn other) {
            this.hset.copyFrom(other.hset);
            this.lset.copyFrom(other.lset);
        }

        public _Gn newCopy() {
            _Gn copy = new _Gn();
            copy.copyFrom(this);
            return copy;
        }

    }


    // VAR

    public boolean scoreshown;
    public boolean wt;
    public int[] deltav = new int[5];
    public int[] deltah = new int[5];
    public int oldx;
    public int oldy;
    public int px;
    public int py;
    public int firedir;
    public int sx;
    public int sy;
    public int ax;
    public int ay;
    public int rfx;
    public int rfy;
    public int l2l;
    public int l2r;
    public int mvie;
    public int k3c;
    public int wtc;
    public int at;
    public int nlj;
    public int nlk;
    public int reps;
    public int clkt;
    public char ch;
    public char ch1;
    public char ch2;
    public char repch;
    public char lastch;
    public long addpt;
    public long decpt;
    public long oldscore;
    public MoveProc[] Move = new MoveProc[OBJECT.values().length];
    public int[] speed = new int[OBJECT.values().length];
    public int[] aiept = new int[OBJECT.values().length];
    public int[] dispt = new int[OBJECT.values().length];
    public int[] first = new int[OBJECT.values().length];
    public Object[] object = Runtime.initArray(new Object[MAXOBJ + 1]);
    public int[][] els = new int[20][72];
    public int[][] e = new int[20][];
    public _Gn[] gn = Runtime.initArray(new _Gn[72]);


    public boolean isScoreshown() {
        return this.scoreshown;
    }

    public void setScoreshown(boolean scoreshown) {
        this.scoreshown = scoreshown;
    }

    public boolean isWt() {
        return this.wt;
    }

    public void setWt(boolean wt) {
        this.wt = wt;
    }

    public int[] getDeltav() {
        return this.deltav;
    }

    public void setDeltav(int[] deltav) {
        this.deltav = deltav;
    }

    public int[] getDeltah() {
        return this.deltah;
    }

    public void setDeltah(int[] deltah) {
        this.deltah = deltah;
    }

    public int getOldx() {
        return this.oldx;
    }

    public void setOldx(int oldx) {
        this.oldx = oldx;
    }

    public int getOldy() {
        return this.oldy;
    }

    public void setOldy(int oldy) {
        this.oldy = oldy;
    }

    public int getPx() {
        return this.px;
    }

    public void setPx(int px) {
        this.px = px;
    }

    public int getPy() {
        return this.py;
    }

    public void setPy(int py) {
        this.py = py;
    }

    public int getFiredir() {
        return this.firedir;
    }

    public void setFiredir(int firedir) {
        this.firedir = firedir;
    }

    public int getSx() {
        return this.sx;
    }

    public void setSx(int sx) {
        this.sx = sx;
    }

    public int getSy() {
        return this.sy;
    }

    public void setSy(int sy) {
        this.sy = sy;
    }

    public int getAx() {
        return this.ax;
    }

    public void setAx(int ax) {
        this.ax = ax;
    }

    public int getAy() {
        return this.ay;
    }

    public void setAy(int ay) {
        this.ay = ay;
    }

    public int getRfx() {
        return this.rfx;
    }

    public void setRfx(int rfx) {
        this.rfx = rfx;
    }

    public int getRfy() {
        return this.rfy;
    }

    public void setRfy(int rfy) {
        this.rfy = rfy;
    }

    public int getL2l() {
        return this.l2l;
    }

    public void setL2l(int l2l) {
        this.l2l = l2l;
    }

    public int getL2r() {
        return this.l2r;
    }

    public void setL2r(int l2r) {
        this.l2r = l2r;
    }

    public int getMvie() {
        return this.mvie;
    }

    public void setMvie(int mvie) {
        this.mvie = mvie;
    }

    public int getK3c() {
        return this.k3c;
    }

    public void setK3c(int k3c) {
        this.k3c = k3c;
    }

    public int getWtc() {
        return this.wtc;
    }

    public void setWtc(int wtc) {
        this.wtc = wtc;
    }

    public int getAt() {
        return this.at;
    }

    public void setAt(int at) {
        this.at = at;
    }

    public int getNlj() {
        return this.nlj;
    }

    public void setNlj(int nlj) {
        this.nlj = nlj;
    }

    public int getNlk() {
        return this.nlk;
    }

    public void setNlk(int nlk) {
        this.nlk = nlk;
    }

    public int getReps() {
        return this.reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getClkt() {
        return this.clkt;
    }

    public void setClkt(int clkt) {
        this.clkt = clkt;
    }

    public char getCh() {
        return this.ch;
    }

    public void setCh(char ch) {
        this.ch = ch;
    }

    public char getCh1() {
        return this.ch1;
    }

    public void setCh1(char ch1) {
        this.ch1 = ch1;
    }

    public char getCh2() {
        return this.ch2;
    }

    public void setCh2(char ch2) {
        this.ch2 = ch2;
    }

    public char getRepch() {
        return this.repch;
    }

    public void setRepch(char repch) {
        this.repch = repch;
    }

    public char getLastch() {
        return this.lastch;
    }

    public void setLastch(char lastch) {
        this.lastch = lastch;
    }

    public long getAddpt() {
        return this.addpt;
    }

    public void setAddpt(long addpt) {
        this.addpt = addpt;
    }

    public long getDecpt() {
        return this.decpt;
    }

    public void setDecpt(long decpt) {
        this.decpt = decpt;
    }

    public long getOldscore() {
        return this.oldscore;
    }

    public void setOldscore(long oldscore) {
        this.oldscore = oldscore;
    }

    public MoveProc[] getMove() {
        return this.Move;
    }

    public void setMove(MoveProc[] Move) {
        this.Move = Move;
    }

    public int[] getSpeed() {
        return this.speed;
    }

    public void setSpeed(int[] speed) {
        this.speed = speed;
    }

    public int[] getAiept() {
        return this.aiept;
    }

    public void setAiept(int[] aiept) {
        this.aiept = aiept;
    }

    public int[] getDispt() {
        return this.dispt;
    }

    public void setDispt(int[] dispt) {
        this.dispt = dispt;
    }

    public int[] getFirst() {
        return this.first;
    }

    public void setFirst(int[] first) {
        this.first = first;
    }

    public Object[] getObject() {
        return this.object;
    }

    public void setObject(Object[] object) {
        this.object = object;
    }

    public int[][] getEls() {
        return this.els;
    }

    public void setEls(int[][] els) {
        this.els = els;
    }

    public int[][] getE() {
        return this.e;
    }

    public void setE(int[][] e) {
        this.e = e;
    }

    public _Gn[] getGn() {
        return this.gn;
    }

    public void setGn(_Gn[] gn) {
        this.gn = gn;
    }


    // VAR

    private boolean doorsnd;
    private int plfd;
    private int gnfd;
    private int d0;
    private int d1;
    private int d2;


    public boolean isDoorsnd() {
        return this.doorsnd;
    }

    public void setDoorsnd(boolean doorsnd) {
        this.doorsnd = doorsnd;
    }

    public int getPlfd() {
        return this.plfd;
    }

    public void setPlfd(int plfd) {
        this.plfd = plfd;
    }

    public int getGnfd() {
        return this.gnfd;
    }

    public void setGnfd(int gnfd) {
        this.gnfd = gnfd;
    }

    public int getD0() {
        return this.d0;
    }

    public void setD0(int d0) {
        this.d0 = d0;
    }

    public int getD1() {
        return this.d1;
    }

    public void setD1(int d1) {
        this.d1 = d1;
    }

    public int getD2() {
        return this.d2;
    }

    public void setD2(int d2) {
        this.d2 = d2;
    }


    // PROCEDURE

    public int SGN(int v) {
        if (v > 0)
            return 1;
        else if (v < 0)
            return -1;
        else
            return 0;
    }

    public void Snd(OBJECT t1, OBJECT t2, int zx, int zy) {
        // VAR
        int dx = 0;
        int dy = 0;
        int dl = 0;
        int stereo = 0;
        int balance = 0;
        int volume = 0;

        { // WITH
            Object _object = object[first[OBJECT.PLAYER.ordinal()]];
            dx = (zx - _object.x);
            dy = (_object.y - zy) * 2;
        }
        dl = trigo.SQRT(dx * dx + dy * dy);
        if (dl >= 60) {
            return;
        } else {
            volume = (63 - dl);
            volume = volume * volume / 16;
        }
        if (dl != 0) {
            stereo = dx * 90 / dl;
            balance = stereo * 6 / (dl + 6);
            if (dy < 0) {
                if (stereo > 0)
                    stereo = 180 - stereo;
                else
                    stereo = -180 - stereo;
            }
        } else {
            stereo = 0;
            balance = 0;
        }
        grotteSounds.Sound(t1, t2, volume, stereo, balance);
    }

    public void WriteTo(int x, int y, char ch) {
        grotteSupport.SetColor(ch);
        aNSITerm.Ghost(x, y, ch);
    }

    public void UnLink(int o) {
        if (o == 0)
            throw new HaltException();
        { // WITH
            Object _object = object[o];
            grotteSupport.attr[_object.type.ordinal()].nb--;
            if (first[_object.type.ordinal()] == o) {
                first[_object.type.ordinal()] = _object.next;
                object[_object.next].prev = 0;
            } else {
                object[_object.prev].next = _object.next;
                object[_object.next].prev = _object.prev;
            }
        }
    }

    public void Link(int o, OBJECT t) {
        if (o == 0)
            throw new HaltException();
        { // WITH
            Object _object = object[o];
            _object.type = t;
            grotteSupport.attr[_object.type.ordinal()].nb++;
            _object.next = first[_object.type.ordinal()];
            if (_object.next != 0)
                object[_object.next].prev = o;
            _object.prev = 0;
            first[_object.type.ordinal()] = o;
        }
    }

    public void SetAttr(OBJECT t) {
        { // WITH
            GrotteSupport.Attr _attr = grotteSupport.attr[t.ordinal()];
            _attr.ct = 0;
            if (t == OBJECT.ASC) {
                _attr.nt = _attr.nb;
                _attr.dt = _attr.timeout;
                return;
            }
            if (_attr.nb == 0) {
                _attr.nt = 0;
                _attr.dt = 1;
            } else {
                if (_attr.nb > _attr.timeout) {
                    _attr.dt = 1;
                    if (_attr.timeout == 0)
                        _attr.nt = _attr.nb;
                    else
                        _attr.nt = (_attr.nb + _attr.timeout - 1) / _attr.timeout;
                } else {
                    _attr.dt = _attr.timeout / _attr.nb;
                    _attr.nt = 1;
                }
            }
        }
    }

    public int New(OBJECT t) {
        // VAR
        int o = 0;

        o = first[OBJECT.EMPTY.ordinal()];
        if (o == 0)
            return 0;
        UnLink(o);
        Link(o, t);
        { // WITH
            Object _object = object[o];
            _object.seq = 0;
            _object.dir = DIRECTION.NUL;
            _object.jump = 0;
            _object.flags = new Runtime.RangeSet(Memory.SET16_r);
        }
        return o;
    }

    public void Create(OBJECT t, int nx, int ny, int nvie, int nseq, char ch) {
        // VAR
        int _new = 0;

        if (t == OBJECT.GN2) {
            if (ny >= 16)
                gn[nx].hset.incl(ny - 16);
            else
                gn[nx].lset.incl(ny);
            return;
        }
        _new = New(t);
        if (_new == 0)
            return;
        Snd(OBJECT.EMPTY, t, nx, ny);
        { // WITH
            Object _object = object[_new];
            _object.x = nx;
            _object.y = ny;
            _object.vie = nvie;
            _object.seq = nseq;
            _object.ch1 = ch;
            grotteSupport.SetColor(ch);
            aNSITerm.WriteAt(_object.x, _object.y, ch);
            e[_object.y][_object.x] = _new;
        }
    }

    public final GrotteSupport.CreateProc Create_ref = this::Create;

    public void Dispose(int o) {
        if (o == 0)
            return;
        { // WITH
            Object _object = object[o];
            if (_object.type == OBJECT.EMPTY)
                return;
            if (_object.type == OBJECT.L1) {
                if (_object.flags.contains(ND))
                    nlk--;
                else
                    nlj--;
            } else if (_object.type == OBJECT.L2) {
                if (_object.ch1 == '(')
                    l2l--;
                else
                    l2r--;
            }
            if (grotteSupport.attr[_object.type.ordinal()].co == o)
                grotteSupport.attr[_object.type.ordinal()].co = _object.next;
            Snd(_object.type, OBJECT.EMPTY, _object.x, _object.y);
            UnLink(o);
            Link(o, OBJECT.EMPTY);
        }
    }

    public void Remove(int o) {
        if (o == 0)
            return;
        { // WITH
            Object _object = object[o];
            if (e[_object.y][_object.x] == o)
                aNSITerm.WriteAt(_object.x, _object.y, ' ');
            else
                WriteTo(_object.x, _object.y, aNSITerm.Report(_object.x, _object.y));
        }
        Dispose(o);
    }

    public boolean Mur(char ch) {
        // VAR
        OBJECT t = OBJECT.EMPTY;

        t = grotteSupport.TypeOf(ch);
        return (t == OBJECT.BM) || (t == OBJECT.MR);
    }

    public void SetPxPy(int nx, int ny) {
        // VAR
        int o = 0;
        int cv = 0;
        int bv = 0;

        bv = Byte.MAX_VALUE /* MAX(SHORTINT) */;
        o = first[OBJECT.PLAYER.ordinal()];
        do {
            { // WITH
                Object _object = object[o];
                cv = Math.abs(nx - _object.x) + Math.abs(ny - _object.y);
                cv += grotteSupport.Random() % 2;
                if (cv <= bv) {
                    px = _object.x;
                    py = _object.y;
                    mvie = _object.vie;
                    bv = cv;
                }
                o = _object.next;
            }
        } while (o != 0);
    }

    public void MoveProx() {
        // VAR
        OBJECT t = OBJECT.EMPTY;
        int k = 0;
        int nxt = 0;
        int td = 0;
        int dist = 0;

        SetPxPy(sx, sy);
        t = grotteSupport.TypeOf(aNSITerm.Report(px, py));
        if ((t != OBJECT.EMPTY) && (t != OBJECT.PLAYER) && (t != OBJECT.MR) && (t != OBJECT.BM)) {
            if (object[e[py][px]].type != OBJECT.PLAYER)
                Remove(e[py][px]);
        }
        for (int _t = OBJECT.K1.ordinal(); _t <= OBJECT.K4.ordinal(); _t++) {
            t = OBJECT.values()[_t];
            k = first[t.ordinal()];
            while (k != 0) {
                { // WITH
                    Object _object = object[k];
                    nxt = _object.next;
                    td = (int) Math.abs(px - _object.x);
                    dist = (int) Math.abs(py - _object.y);
                    if (Math.abs(dist - td) < 2)
                        dist += Math.abs(dist - td);
                    if (td > dist)
                        dist = td;
                    if (dist < 4)
                        Remove(k);
                    else if (dist < 8)
                        _object.vie = 1;
                }
                k = nxt;
            }
        }
    }

    public void ShowLives() {
        grotteSupport.pvie = object[first[OBJECT.PLAYER.ordinal()]].vie;
        aNSITerm.Goto(42, 20);
        aNSITerm.Color(3);
        grotteSupport.WriteCard(grotteSupport.pvie);
        if (grotteSupport.pvie == 9)
            aNSITerm.Write(' ');
    }

    public void ShowLevel() {
        aNSITerm.Goto(74, 20);
        aNSITerm.Color(7);
        if (grotteSupport.level == 10) {
            aNSITerm.Write('B');
            aNSITerm.Write('N');
        } else {
            if (grotteSupport.level == 9)
                aNSITerm.Write('0');
            else
                aNSITerm.Write((char) (grotteSupport.level + 49));
            aNSITerm.Write((char) (grotteSupport.game + 65));
        }
    }

    public void ShowScore() {
        scoreshown = true;
        aNSITerm.Goto(7, 20);
        aNSITerm.Color(7);
        grotteSupport.WriteCard(grotteSupport.score);
        aNSITerm.Write(' ');
    }

    public void ShowMessage() {
        ShowLevel();
        aNSITerm.Color(7);
        aNSITerm.Goto(0, 20);
        aNSITerm.WriteString("Score:");
        ShowScore();
        aNSITerm.Goto(35, 20);
        aNSITerm.WriteString("Lives:");
        ShowLives();
        grotteSupport.ShowClock();
    }

    public void ShowL2() {
        // VAR
        int c1 = 0;

        aNSITerm.Color(7);
        aNSITerm.Goto(72, 0);
        aNSITerm.WriteString("+--+");
        aNSITerm.Goto(72, 19);
        aNSITerm.WriteString("+--+");
        for (c1 = 1; c1 <= 18; c1++) {
            aNSITerm.Goto(72, c1);
            if (grotteSupport.l2count > 18 - c1) {
                aNSITerm.Write('|');
                aNSITerm.Color(2);
                aNSITerm.WriteString("()");
                aNSITerm.Color(7);
                aNSITerm.Write('|');
            } else {
                aNSITerm.WriteString("|  |");
            }
        }
    }

    public void AddPt() {
        // VAR
        char ch = (char) 0;

        if (wt) {
            if (wtc > 0)
                wtc--;
        } else {
            wtc++;
            if (wtc >= 20) {
                clock.StartTime(grotteSupport.time);
                wtc = 20;
            }
        }
        if (addpt > decpt) {
            addpt -= decpt;
            decpt = 0;
        } else {
            decpt -= addpt;
            addpt = 0;
        }
        if ((addpt > 0) || (decpt > 0)) {
            scoreshown = false;
            if (addpt > 0) {
                grotteSupport.score++;
                addpt--;
            }
            if (decpt > 0) {
                if (grotteSupport.score > 0)
                    grotteSupport.score--;
                decpt--;
            }
            if (grotteSupport.score >= grotteSupport.followscore) {
                { // WITH
                    Object _object = object[first[OBJECT.PLAYER.ordinal()]];
                    _object.vie++;
                    Snd(OBJECT.K2, OBJECT.BN, _object.x, _object.y);
                }
                ShowLives();
                ShowScore();
                grotteSupport.followscore += 1000;
            }
        }
        if (wtc < 8) {
            if (!scoreshown)
                ShowScore();
        }
        aNSITerm.Goto(0, 0);
        wt = clock.WaitTime(grotteSupport.time, 16);
    }

    public void WaitKey() {
        SetPxPy(0, 0);
        do {
            aNSITerm.Read(new Runtime.FieldRef<>(this::getCh, this::setCh));
        } while (ch > ((char) 03));
        do {
            if ((addpt > 0) || (decpt > 0)) {
                AddPt();
            } else {
                if (!scoreshown)
                    ShowScore();
                if (!clock.WaitTime(grotteSupport.time, 300))
                    clock.StartTime(grotteSupport.time);
                if (at == 0) {
                    if (clkt == 0) {
                        grotteSupport.ShowClock();
                        clkt = 50;
                    } else {
                        clkt--;
                    }
                    aNSITerm.Color(2);
                    aNSITerm.WriteAt(px, py, '*');
                    at = 1;
                } else {
                    aNSITerm.Color(3);
                    aNSITerm.WriteAt(px, py, '*');
                    at = 0;
                }
                aNSITerm.Goto(0, 21);
            }
            aNSITerm.Read(new Runtime.FieldRef<>(this::getCh, this::setCh));
        } while (ch == ((char) 0));
        aNSITerm.Color(2);
        aNSITerm.WriteAt(px, py, '*');
        aNSITerm.ReadAgain();
        aNSITerm.ClearLine(21);
    }

    public void SortK3(int k) {
        // VAR
        int no = 0;

        { // WITH
            Object _object = object[k];
            _object.flags.incl(3);
            no = _object.next;
            if (first[OBJECT.K3.ordinal()] == k)
                first[OBJECT.K3.ordinal()] = no;
            if (_object.prev != 0)
                object[_object.prev].next = no;
            object[no].prev = _object.prev;
            _object.prev = _object.next;
            _object.next = object[no].next;
            object[no].next = k;
            if (_object.next != 0)
                object[_object.next].prev = k;
        }
    }

    public void InitGame(int level, int game) {
        // VAR
        int eo = 0;
        OBJECT oc = OBJECT.EMPTY;
        int c1 = 0;
        int c2 = 0;

        nlj = 0;
        nlk = 0;
        l2l = 0;
        l2r = 0;
        oldscore = grotteSupport.score;
        grotteSupport.oldpvie = grotteSupport.pvie;
        grotteSupport.oldblv = grotteSupport.blvcount;
        grotteSupport.oldl2 = grotteSupport.l2count;
        for (c1 = 0; c1 <= 71; c1++) {
            gn[c1].hset = new Runtime.RangeSet(Memory.SET16_r);
            gn[c1].lset = new Runtime.RangeSet(Memory.SET16_r);
        }
        for (c1 = 0; c1 <= 19; c1++) {
            for (c2 = 0; c2 <= 71; c2++) {
                e[c1][c2] = 0;
            }
        }
        grotteSupport.made[level].incl(game);
        firedir = D;
        grotteSupport.pcount = 0;
        for (int _oc = 0; _oc < OBJECT.values().length; _oc++) {
            oc = OBJECT.values()[_oc];
            first[oc.ordinal()] = 0;
            grotteSupport.attr[oc.ordinal()].nb = 0;
        }
        for (eo = 1; eo <= MAXOBJ; eo++) {
            { // WITH
                Object _object = object[eo];
                if (eo == MAXOBJ)
                    _object.next = 0;
                else
                    _object.next = eo + 1;
                _object.prev = eo - 1;
                _object.type = OBJECT.EMPTY;
            }
        }
        first[OBJECT.EMPTY.ordinal()] = 1;
        grotteSupport.attr[OBJECT.EMPTY.ordinal()].nb = MAXOBJ;
        grotteSupport.LoadGame(Create_ref, new Runtime.FieldRef<>(this::getAx, this::setAx), new Runtime.FieldRef<>(this::getAy, this::setAy));
        if (level == 10) {
            grotteSupport.blvcount--;
            if (grotteSupport.blvcount > 0)
                grotteSupport.made[10].excl(0);
        }
        aNSITerm.WriteString("Initialising, please wait...");
        eo = first[OBJECT.K3.ordinal()];
        if (eo != 0) {
            while (object[eo].next != 0) {
                eo = object[eo].next;
            }
            while (eo != 0) {
                { // WITH
                    Object _object = object[eo];
                    SetPxPy(_object.x, _object.y);
                    while ((_object.next != 0) && (Math.abs(_object.x - px) + Math.abs(_object.y - py) > Math.abs(object[_object.next].x - px) + Math.abs(object[_object.next].y - py))) {
                        SortK3(eo);
                    }
                    eo = _object.prev;
                }
            }
        }
        grotteSupport.InitK2Dirs();
        eo = first[OBJECT.ASC.ordinal()];
        while (eo != 0) {
            { // WITH
                Object _object = object[eo];
                _object.by = _object.y;
                while ((_object.by < 19) && !Mur(aNSITerm.Report(_object.x, _object.by))) {
                    _object.by++;
                }
                eo = _object.next;
            }
        }
        { // WITH
            Object _object = object[first[OBJECT.PLAYER.ordinal()]];
            _object.jump = 0;
            _object.dir = DIRECTION.NUL;
            sx = _object.x;
            sy = _object.y;
        }
        MoveProx();
        for (int _oc = 0; _oc < OBJECT.values().length; _oc++) {
            oc = OBJECT.values()[_oc];
            { // WITH
                GrotteSupport.Attr _attr = grotteSupport.attr[oc.ordinal()];
                _attr.ct = _attr.dt;
                _attr.timeout = speed[oc.ordinal()];
                _attr.co = first[oc.ordinal()];
            }
            SetAttr(oc);
        }
        aNSITerm.ClearLine(20);
        ShowMessage();
        ShowL2();
        aNSITerm.Goto(0, 21);
        aNSITerm.WriteString("Press any key to start");
        Snd(OBJECT.PLAYER, OBJECT.PLAYER, sx, sy);
        WaitKey();
        grotteSupport.stat = _Stat.Playing;
        clock.StartTime(grotteSupport.time);
    }

    public void Delay(int d) {
        // VAR
        int c = 0;

        for (c = 1; c <= d / 16; c++) {
            AddPt();
        }
    }

    public void Aie(OBJECT t1, OBJECT t2, int o, int n) {
        // VAR
        int i = 0;

        i = n;
        { // WITH
            Object _object = object[o];
            if (_object.seq == 2) {
                _object.seq = 0;
                return;
            }
            if (_object.vie <= i) {
                if ((t2 != OBJECT.BN) && (t2 != OBJECT.K0)) {
                    decpt += dispt[t2.ordinal()];
                } else {
                    if (!_object.flags.contains(ND))
                        addpt += dispt[t2.ordinal()];
                }
                _object.vie = 0;
                Remove(o);
            } else {
                _object.vie -= n;
                if (!_object.flags.contains(ND)) {
                    if ((t2 != OBJECT.BN) && (t2 != OBJECT.K0))
                        decpt += aiept[t2.ordinal()];
                    else
                        addpt += aiept[t2.ordinal()];
                }
                Snd(t1, t2, _object.x, _object.y);
            }
        }
    }

    public void Boum(int nx, int ny) {
        // VAR
        int _new = 0;

        _new = New(OBJECT.BM);
        if (_new == 0)
            return;
        { // WITH
            Object _object = object[_new];
            _object.x = nx;
            _object.y = ny;
        }
        aNSITerm.Ghost(nx - 1, ny - 1, '\\');
        aNSITerm.Ghost(nx, ny - 1, '|');
        aNSITerm.Ghost(nx + 1, ny - 1, '/');
        aNSITerm.Ghost(nx - 1, ny, '-');
        aNSITerm.Ghost(nx + 1, ny, '-');
        aNSITerm.Ghost(nx - 1, ny + 1, '/');
        aNSITerm.Ghost(nx, ny + 1, '|');
        aNSITerm.Ghost(nx + 1, ny + 1, '\\');
    }

    public void AddL2() {
        if (grotteSupport.l2count < 18) {
            grotteSupport.l2count++;
            aNSITerm.Color(2);
            aNSITerm.WriteAt(73, 19 - grotteSupport.l2count, '(');
            aNSITerm.Write(')');
        }
    }

    public void DecVie(int p) {
        if (grotteSupport.level == 10) {
            grotteSupport.stat = _Stat.Finish;
            return;
        }
        { // WITH
            Object _object = object[p];
            Snd(OBJECT.PLAYER, OBJECT.EMPTY, _object.x, _object.y);
        }
        rfx = 0;
        rfy = 20;
        clock.StartTime(grotteSupport.time);
        Delay(1024);
        while ((addpt > 0) || (decpt > 0)) {
            AddPt();
        }
        ShowScore();
        AddL2();
        AddL2();
        AddL2();
        { // WITH
            Object _object = object[p];
            aNSITerm.WriteAt(_object.x, _object.y, ' ');
            _object.x = sx;
            _object.y = sy;
            _object.jump = 0;
            _object.dir = DIRECTION.NUL;
            e[_object.y][_object.x] = p;
            _object.vie--;
            ShowLives();
            if (_object.vie == 0) {
                Delay(3072);
                Snd(OBJECT.EMPTY, OBJECT.EMPTY, _object.x, _object.y);
                grotteSupport.stat = _Stat.GameOver;
                return;
            }
        }
        MoveProx();
        aNSITerm.Color(2);
        aNSITerm.WriteAt(sx, sy, '*');
        aNSITerm.Goto(0, 21);
        aNSITerm.Color(6);
        aNSITerm.WriteString("Press any key to start");
        WaitKey();
    }

    public void Fire(OBJECT t, int fx, int fy, int fd, boolean nid) {
        // VAR
        int _new = 0;
        int c = 0;

        if (fd == N)
            return;
        if (t == OBJECT.L1) {
            if (nid) {
                if (nlk >= 6)
                    return;
            } else {
                if (nlj >= 8) {
                    c = grotteSupport.Random() % nlj;
                    _new = first[OBJECT.L1.ordinal()];
                    while (object[_new].flags.contains(ND)) {
                        _new = object[_new].next;
                    }
                    while (c > 0) {
                        do {
                            _new = object[_new].next;
                            if (_new == 0)
                                throw new HaltException();
                        } while (object[_new].flags.contains(ND));
                        c--;
                    }
                    Remove(_new);
                }
            }
        }
        _new = New(t);
        if (_new == 0)
            return;
        if (t == OBJECT.L1) {
            if (nid)
                nlk++;
            else
                nlj++;
        }
        Snd(OBJECT.EMPTY, t, fx, fy);
        { // WITH
            Object _object = object[_new];
            if (nid)
                _object.flags.incl(ND);
            _object.x = fx;
            _object.y = fy;
            _object.d = fd;
            if (t == OBJECT.L1) {
                if (((_object.d % 2) != 0))
                    _object.ch1 = '-';
                else
                    _object.ch1 = '|';
                _object.vie = 1;
                if (nid)
                    _object.seq = -6;
                else
                    _object.seq = -12;
            } else if (t == OBJECT.L2) {
                if (_object.d == G) {
                    _object.ch1 = '(';
                    l2l++;
                } else {
                    _object.ch1 = ')';
                    l2r++;
                }
                _object.vie = 5;
                _object.seq = -128;
            } else {
                if (_object.d == G)
                    _object.ch1 = '{';
                else
                    _object.ch1 = '}';
            }
        }
        Move[t.ordinal()].invoke(_new);
    }

    public void SubL2() {
        if (grotteSupport.l2count > 0) {
            aNSITerm.WriteAt(73, 19 - grotteSupport.l2count, ' ');
            aNSITerm.Write(' ');
            grotteSupport.l2count--;
        }
    }

    public void AddBLV() {
        grotteSupport.made[10].excl(0);
        grotteSupport.blvcount++;
    }

    public boolean Gn2(int x, int y) {
        if (y < 16)
            return gn[x].lset.contains(y);
        else
            return gn[x].hset.contains((y - 16));
    }

    public void MakeGn2(int nx, int ny) {
        // VAR
        int g = 0;

        if (aNSITerm.Report(nx, ny) != ' ')
            return;
        g = New(OBJECT.GN2);
        if (g == 0)
            return;
        Snd(OBJECT.EMPTY, OBJECT.GN2, nx, ny);
        if (ny < 16)
            gn[nx].lset.excl(ny);
        else
            gn[nx].hset.excl(ny - 16);
        aNSITerm.Color(5);
        { // WITH
            Object _object = object[g];
            _object.x = nx;
            _object.y = ny;
            _object.vie = 1;
            e[_object.y][_object.x] = g;
            _object.seq = 9;
            aNSITerm.WriteAt(_object.x, _object.y, '£');
        }
    }

    public void AiePlayer(int ax, int ay, char ch, int o) {
        Snd(grotteSupport.TypeOf(ch), OBJECT.PLAYER, ax, ay);
        aNSITerm.Color(2);
        Boum(ax, ay);
        grotteSupport.SetColor(ch);
        aNSITerm.MoveChar(oldx, oldy, ' ', ax, ay, ch);
        DecVie(e[ay][ax]);
        if (object[o].type != OBJECT.EMPTY)
            Remove(o);
    }

    public void MoveObj(int o, int c, int x, int y, char dch) {
        if ((oldx != x) || (oldy != y) || (aNSITerm.Report(x, y) != dch)) {
            aNSITerm.Color(c);
            if (e[oldy][oldx] == o)
                aNSITerm.MoveChar(oldx, oldy, ' ', x, y, dch);
            else
                aNSITerm.WriteAt(x, y, dch);
        }
        e[y][x] = o;
    }

    public boolean Confirm() {
        // VAR
        Runtime.Ref<Character> ch = new Runtime.Ref<>((char) 0);

        aNSITerm.Goto(0, 21);
        aNSITerm.Color(3);
        aNSITerm.WriteString("*** REALY QUIT [Y/N] ***");
        aNSITerm.WaitChar(ch);
        aNSITerm.ClearLine(21);
        return (ch.get() < ' ') || (ch.get() == 'y') || (ch.get() == 'Y') || (ch.get() == 'J') || (ch.get() == 'j') || (ch.get() == 'O') || (ch.get() == 'o') || (ch.get() == 'Q') || (ch.get() == 'q');
    }

    public boolean Fixed(char ch) {
        // VAR
        OBJECT t = OBJECT.EMPTY;

        t = grotteSupport.TypeOf(ch);
        return ((t == OBJECT.L1) || (t == OBJECT.L2) || (t == OBJECT.GN1) || (t.ordinal() >= OBJECT.PLAT.ordinal())) && (ch != '.');
    }

    public DIRECTION GetK2Dir(int x, int y, int z) {
        return DIRECTION.values()[(grotteSupport.drK2[y][x] >>> (z * 2)) % 4];
    }

    public void FireBall(int nx, int ny, int jmp, DIRECTION nd) {
        // VAR
        int b = 0;

        if (aNSITerm.Report(nx, ny) != ' ')
            return;
        b = New(OBJECT.BALL);
        if (b == 0)
            return;
        { // WITH
            Object _object = object[b];
            _object.x = nx;
            _object.y = ny;
            _object.vie = 1;
            _object.dir = nd;
            _object.jump = 0;
            _object.by = jmp;
            e[_object.y][_object.x] = b;
            aNSITerm.Color(6);
            aNSITerm.WriteAt(_object.x, _object.y, 'O');
        }
        if ((nd == DIRECTION.NUL) && (ny > 0) && (aNSITerm.Report(nx, ny - 1) == 'N'))
            Snd(OBJECT.K4, OBJECT.EMPTY, nx, ny);
    }

    public boolean Dead(char ch) {
        // VAR
        OBJECT t = OBJECT.EMPTY;

        t = grotteSupport.TypeOf(ch);
        return (t == OBJECT.EMPTY) || (t == OBJECT.ASC) || (t == OBJECT.BN) || (t == OBJECT.SBN) || (t == OBJECT.MR) || (t == OBJECT.BM);
    }

    public void CreateObj(int nx, int ny) {
        // VAR
        int r = 0;
        int mx = 0;
        OBJECT t = OBJECT.EMPTY;
        int _new = 0;

        r = mvie;
        r = grotteSupport.Random() % r;
        mx = 6;
        if (r > 24) {
            t = OBJECT.K0;
            ch = '&';
            mx = 5;
        } else if (r > 22) {
            t = OBJECT.GN2;
            ch = '£';
        } else if (r > 20) {
            t = OBJECT.PIC;
            ch = 'V';
            mx = 10;
        } else if (r > 16) {
            t = OBJECT.K4;
            ch = 'x';
        } else if (r > 12) {
            t = OBJECT.K2;
            ch = 'X';
        } else if (r > 8) {
            t = OBJECT.K1;
            ch = '+';
        } else {
            t = OBJECT.K3;
            ch = '#';
            mx = 30;
        }
        if (grotteSupport.attr[t.ordinal()].nb > mx) {
            aNSITerm.WriteAt(nx, ny, ' ');
            return;
        }
        _new = New(t);
        if (_new == 0) {
            aNSITerm.WriteAt(nx, ny, ' ');
            return;
        }
        { // WITH
            Object _object = object[_new];
            _object.x = nx;
            _object.y = ny;
            if (t == OBJECT.K4)
                _object.seq = 2;
            else if (t == OBJECT.GN2)
                _object.seq = 9;
            r = mvie;
            _object.vie = grotteSupport.Random() % (r + grotteSupport.level) / 4 + 1;
            _object.flags.incl(ND);
        }
        grotteSupport.SetColor(ch);
        aNSITerm.WriteAt(nx, ny, ch);
        e[ny][nx] = first[t.ordinal()];
    }

    public void MovePlayer(int p) {
        // VAR
        int nd = 0;
        int nx = 0;
        int ny = 0;
        int best = 0;
        int pt = 0;
        int ql2 = 0;
        int c1 = 0;
        int c2 = 0;
        OBJECT t = OBJECT.EMPTY;
        OBJECT ft = OBJECT.EMPTY;
        OBJECT bft = OBJECT.EMPTY;

        if (grotteSupport.stat != _Stat.Playing)
            return;
        if (grotteSupport.ndoor != 0)
            doorsnd = false;
        if ((grotteSupport.attr[OBJECT.K0.ordinal()].nb == 0) && (grotteSupport.ndoor == 0) && wt) {
            if (at == 0)
                at = 8;
            at--;
            if (at == 4) {
                aNSITerm.Color(2);
                aNSITerm.WriteAt(ax, ay, 'A');
            } else if (at == 0) {
                if (!doorsnd) {
                    { // WITH
                        Object _object = object[p];
                        Snd(OBJECT.PLAYER, OBJECT.PLAYER, _object.x, _object.y + 1);
                    }
                    doorsnd = true;
                }
                aNSITerm.Color(3);
                aNSITerm.WriteAt(ax, ay, 'A');
            }
        }
        if ((wtc == 0) && (clkt == 0)) {
            grotteSupport.ShowClock();
            clkt = 50;
        }
        if (clkt != 0)
            clkt--;
        { // WITH
            Object _object = object[p];
            if (_object.vie > 98)
                _object.vie = 98;
            pt = 10 - (grotteSupport.level % 10);
            grotteSupport.ReadDir(new Runtime.FieldRef<>(this::getCh, this::setCh));
            if (ch > ((char) 03)) {
                if (ch != repch) {
                    repch = ch;
                    reps = 1;
                } else {
                    reps++;
                }
                do {
                    grotteSupport.ReadDir(new Runtime.FieldRef<>(this::getCh, this::setCh));
                    if (reps < 50)
                        reps++;
                } while (!((ch != repch) || (ch <= ((char) 03))));
                reps--;
                if (ch > ((char) 03))
                    aNSITerm.ReadAgain();
            }
            if (reps == 0)
                repch = ((char) 0);
            else
                reps--;
            if (repch == 'o')
                repch = lastch;
            else
                lastch = repch;
            oldx = _object.x;
            oldy = _object.y;
            if (ch == ((char) 03)) {
                grotteSupport.stat = _Stat.Break;
                aNSITerm.ReadAgain();
                return;
            }
            switch (repch) {
                case '4', 'D', ((char) 037) -> {
                    _object.dir = DIRECTION.LEFT;
                    firedir = G;
                }
                case '6', 'C', ((char) 036) -> {
                    _object.dir = DIRECTION.RIGHT;
                    firedir = D;
                }
                case '5', 'B', ((char) 035) -> {
                    _object.dir = DIRECTION.NUL;
                }
                case '8', ((char) 027), ((char) 0234), ((char) 0227) -> {
                    Fire(OBJECT.L1, _object.x, _object.y, H, false);
                }
                case '2', ((char) 030), ((char) 0235), ((char) 0230) -> {
                    Fire(OBJECT.L1, _object.x, _object.y, B, false);
                }
                case '1', ((char) 0237) -> {
                    Fire(OBJECT.L1, _object.x, _object.y, G, false);
                }
                case '3', ((char) 0236) -> {
                    Fire(OBJECT.L1, _object.x, _object.y, D, false);
                }
                case '7', ((char) 032), ((char) 0232) -> {
                    if (grotteSupport.l2count > 0) {
                        Fire(OBJECT.L2, _object.x, _object.y, G, false);
                        SubL2();
                    }
                }
                case '9', ((char) 031), ((char) 0231) -> {
                    if (grotteSupport.l2count > 0) {
                        Fire(OBJECT.L2, _object.x, _object.y, D, false);
                        SubL2();
                    }
                }
                case 'f', ((char) 015), ((char) 012) -> {
                    c2 = 0;
                    bft = OBJECT.L1;
                    for (nd = G; nd <= B; nd++) {
                        ft = OBJECT.L1;
                        c1 = 0;
                        nx = _object.x;
                        ny = _object.y;
                        best = 0;
                        if (nd == G)
                            ql2 = l2l;
                        else
                            ql2 = l2r;
                        while (true) {
                            c1++;
                            nx += deltah[nd];
                            ny += deltav[nd];
                            if ((nx <= 0) || (ny <= 0) || (nx >= 71) || (ny >= 19))
                                break;
                            t = grotteSupport.TypeOf(aNSITerm.Report(nx, ny));
                            if ((t == OBJECT.PLAT) && (best == 0))
                                best = 1;
                            if ((t == OBJECT.MR) || (t == OBJECT.BM) || (t == OBJECT.ASC) || (t == OBJECT.BN))
                                break;
                            if ((t == OBJECT.K0) || (t == OBJECT.K1) || (t == OBJECT.K2) || (t == OBJECT.K3) || (t == OBJECT.L3) || (t == OBJECT.GN1) || (t == OBJECT.GN2) || (t == OBJECT.BALL) || (t == OBJECT.PIC) || (t == OBJECT.NID) || (t == OBJECT.BUB)) {
                                if (((t == OBJECT.GN1) || (t == OBJECT.NID) || (t == OBJECT.BUB) || (c1 <= 2)) && (grotteSupport.l2count > 0) && ((nd % 2) != 0) && (ql2 < 1))
                                    ft = OBJECT.L2;
                                best = 16 - c1;
                                break;
                            }
                            if (c1 >= 14)
                                break;
                        }
                        if (best > c2) {
                            bft = ft;
                            firedir = nd;
                            c2 = best;
                        }
                    }
                    if (bft == OBJECT.L2)
                        SubL2();
                    Fire(bft, _object.x, _object.y, firedir, false);
                }
                case ((char) 011) -> {
                    if (((firedir % 2) != 0))
                        firedir = 4 - firedir;
                    else
                        firedir = D;
                }
                case '0', ' ', 'A', ((char) 034) -> {
                    ch = aNSITerm.Report(_object.x, _object.y + 1);
                    if ((ch != ' ') && (ch != '|') && (ch != 'H'))
                        _object.jump = 5;
                }
                case 'Q', 'q', ((char) 03), ((char) 033) -> {
                    if (Confirm()) {
                        grotteSupport.stat = _Stat.Break;
                        addpt = 0;
                    }
                }
                case 'e' -> {
                    grotteSupport.stat = _Stat.Finish;
                    addpt = 0;
                    decpt = 0;
                    if (grotteSupport.score > oldscore)
                        grotteSupport.score = oldscore;
                    if (grotteSupport.pvie > grotteSupport.oldpvie)
                        grotteSupport.pvie = grotteSupport.oldpvie;
                    grotteSupport.blvcount = grotteSupport.oldblv;
                    grotteSupport.l2count = grotteSupport.oldl2;
                    grotteSupport.oldpvie = 0;
                }
                case 'W', 'w', 'r', 'R' -> {
                    aNSITerm.Color(3);
                    aNSITerm.Goto(0, 21);
                    aNSITerm.WriteString("Please wait...");
                    for (c1 = 0; c1 <= 21; c1++) {
                        for (c2 = 0; c2 <= 75; c2++) {
                            WriteTo(c2, c1, aNSITerm.Report(c2, c1));
                        }
                    }
                    ShowMessage();
                    ShowL2();
                    aNSITerm.ClearLine(21);
                    clock.StartTime(grotteSupport.time);
                }
                case 'P', 'p', ((char) 014) -> {
                    aNSITerm.Goto(0, 21);
                    aNSITerm.Color(7);
                    aNSITerm.WriteString("Game paused, press any key to continue");
                    WaitKey();
                }
                default -> {
                }
            }
            if (_object.dir == DIRECTION.LEFT)
                _object.x--;
            else if (_object.dir == DIRECTION.RIGHT)
                _object.x++;
            px = _object.x;
            py = _object.y;
            aNSITerm.Color(2);
            ch = aNSITerm.Report(_object.x, _object.y);
            switch (grotteSupport.TypeOf(ch)) {
                case SBN -> {
                    aNSITerm.WriteAt(_object.x, _object.y, ' ');
                    if (ch == '.') {
                        Snd(OBJECT.K1, OBJECT.BN, _object.x, _object.y);
                        addpt += pt;
                    } else if (ch == '$') {
                        _object.vie++;
                        ShowLives();
                        Snd(OBJECT.K2, OBJECT.BN, _object.x, _object.y);
                        AddBLV();
                    } else if (ch == '@') {
                        Snd(OBJECT.K3, OBJECT.BN, _object.x, _object.y);
                        addpt += 100;
                    }
                }
                case BN -> {
                    if (e[_object.y][_object.x] == 0) {
                        Snd(OBJECT.EMPTY, OBJECT.BN, _object.x, _object.y);
                        { // WITH
                            Object _object2 = object[New(OBJECT.BN)];
                            _object2.x = px;
                            _object2.y = py;
                            e[_object2.y][_object2.x] = first[OBJECT.BN.ordinal()];
                        }
                        aNSITerm.WriteAt(oldx, oldy, ' ');
                        addpt += 20;
                        if (first[OBJECT.BN.ordinal()] != 0)
                            Move[OBJECT.BN.ordinal()].invoke(first[OBJECT.BN.ordinal()]);
                        AddL2();
                        AddL2();
                    } else {
                        _object.x = oldx;
                        _object.dir = DIRECTION.NUL;
                    }
                }
                case L1 -> {
                    if (object[e[_object.y][_object.x]].flags.contains(ND)) {
                        _object.x = oldx;
                        _object.dir = DIRECTION.NUL;
                    }
                }
                case L2, EMPTY, PLAYER -> {
                }
                default -> {
                    if (ch == 'A') {
                        aNSITerm.MoveChar(oldx, oldy, ' ', _object.x, _object.y, '*');
                        grotteSupport.stat = _Stat.Finish;
                        return;
                    } else if (ch == '!') {
                        aNSITerm.Color(7);
                        Snd(OBJECT.K4, OBJECT.BN, _object.x, _object.y);
                        aNSITerm.WriteAt(_object.x, _object.y, ' ');
                        if (Mur(aNSITerm.Report(_object.x, _object.y - 1)) && Mur(aNSITerm.Report(_object.x, _object.y + 1)) && !Mur(aNSITerm.Report(_object.x - oldx + _object.x, _object.y))) {
                            aNSITerm.WriteAt(_object.x, _object.y, 'I');
                            _object.x += _object.x - oldx;
                        }
                        sx = _object.x;
                        sy = _object.y;
                        grotteSupport.ndoor--;
                    } else {
                        _object.x = oldx;
                        _object.dir = DIRECTION.NUL;
                    }
                }
            }
            if (_object.jump > 1) {
                _object.jump--;
                _object.y--;
            } else if (_object.jump == 1) {
                _object.jump--;
            } else {
                _object.y++;
            }
            px = _object.x;
            py = _object.y;
            ch = aNSITerm.Report(_object.x, _object.y);
            switch (grotteSupport.TypeOf(ch)) {
                case SBN -> {
                    if (ch == '.') {
                        Snd(OBJECT.K1, OBJECT.BN, _object.x, _object.y);
                        addpt += pt;
                    } else if (ch == '$') {
                        _object.vie++;
                        ShowLives();
                        Snd(OBJECT.K2, OBJECT.BN, _object.x, _object.y);
                        AddBLV();
                    } else if (ch == '@') {
                        Snd(OBJECT.K3, OBJECT.BN, _object.x, _object.y);
                        addpt += 100;
                    }
                }
                case BN -> {
                    if (e[_object.y][_object.x] == 0) {
                        Snd(OBJECT.EMPTY, OBJECT.BN, _object.x, _object.y);
                        { // WITH
                            Object _object2 = object[New(OBJECT.BN)];
                            _object2.x = px;
                            _object2.y = py;
                            e[_object2.y][_object2.x] = first[OBJECT.BN.ordinal()];
                        }
                        aNSITerm.WriteAt(oldx, oldy, ' ');
                        addpt += 20;
                        if (first[OBJECT.BN.ordinal()] != 0)
                            Move[OBJECT.BN.ordinal()].invoke(first[OBJECT.BN.ordinal()]);
                        AddL2();
                        AddL2();
                    } else {
                        _object.y = oldy;
                        if (_object.jump > 1)
                            _object.jump = 1;
                    }
                }
                case L1 -> {
                    if (object[e[_object.y][_object.x]].flags.contains(ND))
                        _object.y = oldy;
                }
                case L2, EMPTY, PLAYER -> {
                }
                case BM -> {
                    aNSITerm.Color(6);
                    Boum(_object.x, oldy);
                    aNSITerm.Color(2);
                    aNSITerm.MoveChar(oldx, oldy, ' ', _object.x, oldy, '*');
                    oldx = _object.x;
                    c1 = oldy;
                    oldy = _object.y;
                    _object.y = c1;
                    aNSITerm.Color(7);
                    aNSITerm.WriteAt(oldx, oldy, '0');
                    DecVie(p);
                    return;
                }
                default -> {
                    if (ch == 'A') {
                        grotteSupport.stat = _Stat.Finish;
                    } else if (ch == '!') {
                        sx = _object.x;
                        sy = _object.y;
                        Snd(OBJECT.K4, OBJECT.BN, _object.x, _object.y);
                        grotteSupport.ndoor--;
                    } else if (ch == '/') {
                        if (_object.jump > 0)
                            _object.dir = DIRECTION.RIGHT;
                        else
                            _object.dir = DIRECTION.LEFT;
                        _object.y = oldy;
                        if (_object.jump > 1)
                            _object.jump = 1;
                    } else if (ch == '\\') {
                        if (_object.jump > 0)
                            _object.dir = DIRECTION.LEFT;
                        else
                            _object.dir = DIRECTION.RIGHT;
                        _object.y = oldy;
                        if (_object.jump > 1)
                            _object.jump = 1;
                    } else {
                        _object.y = oldy;
                        if (_object.jump > 1)
                            _object.jump = 1;
                    }
                }
            }
            MoveObj(p, 2, _object.x, _object.y, '*');
            if ((_object.x != oldx) || (_object.y != oldy)) {
                if (_object.jump > 0)
                    Snd(OBJECT.PLAYER, OBJECT.GN1, _object.x, _object.y);
                else
                    Snd(OBJECT.PLAYER, OBJECT.GN2, _object.x, _object.y);
            }
            for (c1 = 1; c1 <= 5; c1++) {
                if (c1 < _object.x) {
                    c2 = _object.x - c1;
                    if (Gn2(c2, _object.y))
                        MakeGn2(c2, _object.y);
                }
                c2 = _object.x + c1;
                if (c2 < 72) {
                    if (Gn2(c2, _object.y))
                        MakeGn2(c2, _object.y);
                }
            }
        }
    }

    public final MoveProc MovePlayer_ref = this::MovePlayer;

    private int ReportObj(int x, int y, int d) {
        // VAR
        char ch = (char) 0;
        int c = 0;
        OBJECT t = OBJECT.EMPTY;
        int danger = 0;

        c = 0;
        d0 = 0;
        danger = 0;
        d2 = 0;
        while (true) {
            c++;
            x -= deltah[d];
            y -= deltav[d];
            if ((x < 0) || (y < 0) || (x > 71) || (y > 19))
                break;
            ch = aNSITerm.Report(x, y);
            if (ch == ':') {
                if (d > H)
                    d -= 2;
                else
                    d += 2;
            } else if (ch == '/') {
                d = 5 - d;
            } else if (ch == '\\') {
                if (((d % 2) != 0))
                    d++;
                else
                    d--;
            } else {
                t = grotteSupport.TypeOf(ch);
                if (((d % 2) != 0) && (t == OBJECT.PLAYER) && (plfd > 0)) {
                    d2 += 2;
                } else if (t == OBJECT.GN2) {
                    d2 += 1;
                } else if ((t == OBJECT.L1) || (t == OBJECT.L2) || ((t == OBJECT.PIC) && (c < 4))) {
                    if (object[e[y][x]].d == d) {
                        if (t == OBJECT.L2)
                            d2 += 5;
                        else
                            d2++;
                    }
                }
                if (c <= 5)
                    danger = d2;
                if (c <= 4)
                    d0 = d2;
                if (((t == OBJECT.K4) || (t == OBJECT.GN1) || (t == OBJECT.NID) || (t == OBJECT.PLAT) || (t == OBJECT.BN) || (t == OBJECT.SBN) || (t == OBJECT.MR) || (t == OBJECT.BM)) && (ch != '.'))
                    break;
            }
            if (c >= 6)
                break;
        }
        return danger;
    }

    public void MoveK0(int k) {
        // VAR
        int[] danger = new int[B - N + 1];
        int best = 0;
        int tmp = 0;
        boolean fuite = false;
        int c1 = 0;
        int td = 0;
        int mo = 0;
        char mch = (char) 0;

        fuite = false;
        { // WITH
            Object _object = object[k];
            if ((_object.x == 0) || (_object.y == 0) || (_object.x == 71) || (_object.y == 19))
                throw new HaltException();
            SetPxPy(_object.x, _object.y);
            if (((px % 2) != 0))
                gnfd = 10;
            else if (gnfd > 0)
                gnfd--;
            if (nlj > 0)
                plfd = 10;
            else if (plfd > 0)
                plfd--;
            if (_object.seq == 1) {
                td = _object.d;
                if (_object.v == 1) {
                    if (td <= G)
                        td = B;
                    else
                        td--;
                } else {
                    if (td == B)
                        td = G;
                    else
                        td++;
                }
                ch = aNSITerm.Report(_object.x + deltah[td], _object.y + deltav[td]);
                if ((ch == 'I') || (ch == '8'))
                    ch = ' ';
                if (!Fixed(ch)) {
                    if (_object.flags.contains(1)) {
                        _object.seq = 0;
                        _object.flags.excl(1);
                        return;
                    } else {
                        _object.flags.incl(1);
                    }
                    _object.d = td;
                } else {
                    _object.flags.excl(1);
                }
            } else if (_object.seq == 0) {
                _object.flags.excl(1);
                if (px > _object.x)
                    _object.d = D;
                else
                    _object.d = G;
                if (py > _object.y)
                    td = B;
                else
                    td = H;
                if (px == _object.x)
                    _object.d = td;
                if (py == _object.y)
                    td = _object.d;
                if (Fixed(aNSITerm.Report(_object.x + deltah[_object.d], _object.y))) {
                    if (!Fixed(aNSITerm.Report(_object.x, _object.y + deltav[td])))
                        _object.d = td;
                } else if (!Fixed(aNSITerm.Report(_object.x, _object.y + deltav[td]))) {
                    if (Math.abs(px - _object.x) + 1 < Math.abs(py - _object.y) * 2)
                        _object.d = td;
                }
                c1 = Math.abs(px - _object.x) + Math.abs(py - _object.y) * 2;
                if (c1 >= 6) {
                    tmp = mvie;
                    if ((grotteSupport.Random() % 128 + 4) < tmp) {
                        if (aNSITerm.Report(_object.x + deltah[SGN(px - _object.x) + 2], _object.y) == ' ') {
                            if (c1 < 22)
                                Fire(OBJECT.L3, _object.x, _object.y, SGN(px - _object.x) + 2, true);
                            else
                                Fire(OBJECT.L1, _object.x, _object.y, (grotteSupport.Random() % 2) * 2 + 1, true);
                        }
                    }
                    tmp = mvie;
                    if (((grotteSupport.Random() % 256 + 7) < tmp) && ((_object.d % 2) != 0))
                        Fire(OBJECT.L1, _object.x, _object.y, (grotteSupport.Random() % 2) * 2 + 2, true);
                }
            }
            for (td = N; td <= B; td++) {
                danger[td - N] = 0;
            }
            for (td = G; td <= B; td++) {
                ch = aNSITerm.Report(_object.x + deltah[td], _object.y + deltav[td]);
                if (Fixed(ch))
                    danger[td - N] = 1024;
                d1 = ReportObj(_object.x, _object.y, td);
                if (td > H)
                    c1 = td - 2;
                else
                    c1 = td + 2;
                danger[N - N] += d1;
                danger[td - N] += d0;
                danger[c1 - N] += d2;
            }
            danger[G - N] += ReportObj(_object.x - 1, _object.y, H) + ReportObj(_object.x - 1, _object.y, B);
            danger[D - N] += ReportObj(_object.x + 1, _object.y, H) + ReportObj(_object.x + 1, _object.y, B);
            danger[H - N] += ReportObj(_object.x, _object.y - 1, G) + ReportObj(_object.x, _object.y - 1, D);
            danger[B - N] += ReportObj(_object.x, _object.y + 1, G) + ReportObj(_object.x, _object.y + 1, D);
            if ((danger[_object.d - N] % 1024 > 0) || (danger[N - N] > 0)) {
                fuite = true;
                _object.seq = 0;
                best = danger[_object.d - N];
                for (td = N; td <= B; td++) {
                    if (danger[td - N] <= best) {
                        _object.d = td;
                        best = danger[td - N];
                    }
                }
            }
            if (_object.seq == 2) {
                if (aNSITerm.Report(_object.x, _object.y) != '&') {
                    aNSITerm.Color(3);
                    aNSITerm.WriteAt(_object.x, _object.y, '&');
                    e[_object.y][_object.x] = k;
                }
                return;
            }
            oldx = _object.x;
            oldy = _object.y;
            _object.x += deltah[_object.d];
            _object.y += deltav[_object.d];
            ch = aNSITerm.Report(_object.x, _object.y);
            mch = ' ';
            if ((ch == 'I') || (ch == '8'))
                ch = ' ';
            if (ch == '*') {
                AiePlayer(_object.x, _object.y, '&', k);
                return;
            } else if (Fixed(ch)) {
                if (_object.seq == 1) {
                    if ((_object.x == 0) || (_object.x == 71) || (_object.y == 0) || (_object.y == 19)) {
                        if (_object.d < D)
                            _object.d += 2;
                        else
                            _object.d -= 2;
                        if (_object.v == 1)
                            _object.v = 0;
                        else
                            _object.v = 1;
                    }
                }
                _object.x = oldx;
                _object.y = oldy;
                if (!fuite) {
                    if (_object.seq == 0) {
                        _object.bx = _object.x;
                        _object.by = _object.y;
                        do {
                            if (Math.abs(px - _object.bx) >= Math.abs(py - _object.by) * 2) {
                                if (px > _object.bx)
                                    _object.bx++;
                                else
                                    _object.bx--;
                            } else {
                                if (py > _object.by)
                                    _object.by++;
                                else
                                    _object.by--;
                            }
                            ch = aNSITerm.Report(_object.bx, _object.by);
                        } while (Fixed(ch));
                        _object.seq = 1;
                    }
                    if (_object.v == 1) {
                        if (_object.d == B)
                            _object.d = G;
                        else
                            _object.d++;
                    } else {
                        if (_object.d <= G)
                            _object.d = B;
                        else
                            _object.d--;
                    }
                }
            } else if ((ch != ' ') && (ch != '.') && (ch != '&') && (ch != '=')) {
                mo = e[_object.y][_object.x];
                mch = ch;
                { // WITH
                    Object _object2 = object[mo];
                    _object2.x = oldx;
                    _object2.y = oldy;
                }
                e[oldy][oldx] = mo;
            }
            if (mch != ' ') {
                grotteSupport.SetColor(mch);
                aNSITerm.WriteAt(oldx, oldy, mch);
                aNSITerm.Color(3);
                aNSITerm.WriteAt(_object.x, _object.y, '&');
            } else {
                MoveObj(k, 3, _object.x, _object.y, '&');
            }
            if ((_object.x != oldx) || (_object.y != oldy))
                Snd(OBJECT.BM, OBJECT.EMPTY, _object.x, _object.y);
            e[_object.y][_object.x] = k;
            if ((_object.bx == _object.x) && (_object.by == _object.y))
                _object.seq = 0;
        }
    }

    public final MoveProc MoveK0_ref = this::MoveK0;


    // Support

    private static GrotteActions instance;

    public static GrotteActions instance() {
        if (instance == null)
            new GrotteActions(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
    }

    public void close() {
    }

}
