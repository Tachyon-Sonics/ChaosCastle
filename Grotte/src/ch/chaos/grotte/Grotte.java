package ch.chaos.grotte;

import ch.chaos.grotte.GrotteSupport.DIRECTION;
import ch.chaos.grotte.GrotteSupport.OBJECT;
import ch.chaos.grotte.GrotteSupport._Stat;
import ch.chaos.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class Grotte {

    // Imports
    private final ANSITerm aNSITerm = ANSITerm.instance();
    private final Clock clock = Clock.instance();
    private final GrotteActions grotteActions = GrotteActions.instance();
    private final GrotteSupport grotteSupport = GrotteSupport.instance();


    // PROCEDURE

    /* ******** MoveProcs **********/
    private void MoveK1(int k) {
        /*$ CStrings:= FALSE */
        { // WITH
            GrotteActions.Object _object = grotteActions.object[k];
            if ((_object.x == 0) || (_object.y == 0) || (_object.x == 71) || (_object.y == 19)) {
                grotteActions.Remove(k);
                return;
            }
            if (_object.seq >= 0) {
                if (grotteSupport.Random() % 16 > 4) {
                    grotteActions.SetPxPy(_object.x, _object.y);
                    if (grotteSupport.Random() % 2 == 0) {
                        if (grotteActions.px < _object.x)
                            _object.d = GrotteActions.G;
                        else
                            _object.d = GrotteActions.D;
                    } else {
                        if (grotteActions.py < _object.y)
                            _object.d = GrotteActions.H;
                        else
                            _object.d = GrotteActions.B;
                    }
                } else {
                    _object.d = grotteSupport.Random() % 5;
                }
                _object.seq -= grotteSupport.Random() % 16;
            } else {
                _object.seq++;
            }
            grotteActions.oldx = _object.x;
            grotteActions.oldy = _object.y;
            _object.x += grotteActions.deltah[_object.d];
            _object.y += grotteActions.deltav[_object.d];
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
            if (grotteActions.ch == '*') {
                grotteActions.AiePlayer(_object.x, _object.y, '+', k);
                return;
            } else if ((grotteActions.ch != ' ') && (grotteActions.ch != '.')) {
                _object.x = grotteActions.oldx;
                _object.y = grotteActions.oldy;
                _object.seq = 0;
            } else {
                grotteActions.MoveObj(k, 1, _object.x, _object.y, '+');
            }
        }
    }

    private final GrotteActions.MoveProc MoveK1_ref = this::MoveK1;

    private void MoveK2(int k) {
        // VAR
        DIRECTION rd = DIRECTION.NUL;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[k];
            if ((_object.x == 0) || (_object.y == 0) || (_object.x == 71) || (_object.y == 19)) {
                grotteActions.Remove(k);
                return;
            }
            if (_object.seq >= 0) {
                grotteActions.SetPxPy(_object.x, _object.y);
                if (_object.y <= grotteActions.py)
                    _object.by = 2;
                else
                    _object.by = 0;
                if (_object.x > grotteActions.px)
                    _object.by++;
            } else {
                _object.seq++;
            }
            rd = grotteActions.GetK2Dir(_object.x, _object.y, _object.by);
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y + 1);
            if ((rd == DIRECTION.NUL) || (rd == DIRECTION.JUMP)) {
                if (_object.dir != DIRECTION.NUL) {
                    _object.dir = DIRECTION.NUL;
                } else {
                    if ((grotteActions.ch != ' ') && (grotteActions.ch != 'H') && (grotteActions.ch != '.')) {
                        if (_object.by >= 2) {
                            _object.seq = grotteSupport.Random() % 16;
                            _object.seq = -_object.seq;
                            if (grotteSupport.Random() % 2 == 0)
                                _object.by = 7 - _object.by;
                            else
                                _object.by = _object.by % 2;
                        } else {
                            _object.jump = 5;
                        }
                    }
                }
            } else {
                _object.dir = rd;
            }
            if (grotteActions.px == _object.x)
                _object.dir = DIRECTION.NUL;
            if ((_object.by < 2) && (grotteActions.ch != ' ') && (grotteActions.ch != 'H'))
                _object.jump = 5;
            grotteActions.oldx = _object.x;
            grotteActions.oldy = _object.y;
            if (_object.dir == DIRECTION.LEFT)
                _object.x--;
            else if (_object.dir == DIRECTION.RIGHT)
                _object.x++;
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
            if (grotteActions.ch == '*') {
                grotteActions.AiePlayer(_object.x, _object.y, 'X', k);
                return;
            } else if (grotteActions.ch != ' ') {
                _object.x = grotteActions.oldx;
            }
            if (_object.jump > 1) {
                _object.jump--;
                _object.y--;
            } else if (_object.jump == 1) {
                _object.jump = 0;
            } else {
                _object.y++;
            }
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
            if (grotteActions.ch == '*') {
                grotteActions.AiePlayer(_object.x, _object.y, 'X', k);
                return;
            } else if ((grotteActions.ch == 'H') || (grotteActions.ch == '&')) {
                if (_object.jump > 0) {
                    _object.y = grotteActions.oldy;
                    _object.jump = 0;
                } else {
                    aNSITerm.Color(1);
                    _object.x = grotteActions.oldx;
                    _object.y = grotteActions.oldy;
                    grotteActions.Boum(_object.x, _object.y);
                    grotteActions.Remove(k);
                    return;
                }
            } else if ((grotteActions.ch != ' ') && (grotteActions.ch != '.')) {
                if (_object.jump > 1)
                    _object.jump = 1;
                _object.y = grotteActions.oldy;
            }
            grotteActions.MoveObj(k, 1, _object.x, _object.y, 'X');
        }
    }

    private final GrotteActions.MoveProc MoveK2_ref = this::MoveK2;

    private void MoveK3(int k) {
        if (grotteActions.first[OBJECT.K3.ordinal()] == k)
            grotteActions.k3c = 0;
        if (grotteActions.k3c > 8) {
            grotteSupport.attr[OBJECT.K3.ordinal()].co = k;
            return;
        }
        { // WITH
            GrotteActions.Object _object = grotteActions.object[k];
            if (_object.flags.contains(3)) {
                _object.flags.excl(3);
                return;
            }
            grotteActions.oldx = _object.x;
            grotteActions.oldy = _object.y;
            grotteActions.SetPxPy(_object.x, _object.y);
            if (grotteActions.px < _object.x)
                _object.x--;
            else if (grotteActions.px > _object.x)
                _object.x++;
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
            if ((grotteActions.ch != ' ') && (grotteActions.ch != '*'))
                _object.x = grotteActions.oldx;
            if (grotteActions.py < _object.y)
                _object.y--;
            else if (grotteActions.py > _object.y)
                _object.y++;
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
            if (grotteActions.ch == '*') {
                grotteActions.AiePlayer(_object.x, _object.y, '#', k);
                return;
            } else if (grotteActions.ch != ' ') {
                _object.y = grotteActions.oldy;
            }
            grotteActions.MoveObj(k, 1, _object.x, _object.y, '#');
            if ((grotteActions.oldx != _object.x) || (grotteActions.oldy != _object.y))
                grotteActions.k3c++;
            /* Sorting */
            if (_object.next != 0) {
                if (Math.abs(_object.x - grotteActions.px) + Math.abs(_object.y - grotteActions.py) > Math.abs(grotteActions.object[_object.next].x - grotteActions.px) + Math.abs(grotteActions.object[_object.next].y - grotteActions.py))
                    grotteActions.SortK3(k);
            }
        }
    }

    private final GrotteActions.MoveProc MoveK3_ref = this::MoveK3;

    private void MoveK4(int k) {
        { // WITH
            GrotteActions.Object _object = grotteActions.object[k];
            if ((_object.x == 0) || (_object.y == 0) || (_object.x == 71) || (_object.y == 19)) {
                grotteActions.Remove(k);
                return;
            }
            grotteActions.oldx = _object.x;
            grotteActions.oldy = _object.y;
            if (_object.dir == DIRECTION.NUL)
                _object.dir = DIRECTION.LEFT;
            if ((_object.seq != 2) && (_object.dir != DIRECTION.JUMP)) {
                _object.jump = 5;
                _object.dir = DIRECTION.JUMP;
            }
            if (_object.dir == DIRECTION.LEFT)
                _object.x--;
            else if (_object.dir == DIRECTION.RIGHT)
                _object.x++;
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
            if (grotteActions.ch == '*') {
                _object.x = grotteActions.oldx;
                _object.dir = DIRECTION.JUMP;
                _object.jump = 5;
                _object.seq = 0;
            } else if ((grotteActions.ch != '.') && (grotteActions.ch != ' ')) {
                _object.x = grotteActions.oldx;
                if (grotteSupport.Random() % 4 == 0) {
                    if (aNSITerm.Report(_object.x, _object.y + 1) != ' ')
                        _object.jump = 5;
                }
                if ((grotteSupport.Random() % 8 == 0) || (_object.jump == 0)) {
                    if (_object.dir == DIRECTION.LEFT)
                        _object.dir = DIRECTION.RIGHT;
                    else if (_object.dir == DIRECTION.RIGHT)
                        _object.dir = DIRECTION.LEFT;
                }
            }
            if (_object.jump > 1) {
                _object.jump--;
                _object.y--;
            } else if (_object.jump == 1) {
                if (_object.dir == DIRECTION.JUMP) {
                    grotteActions.FireBall(_object.x - 1, _object.y, 6, DIRECTION.LEFT);
                    grotteActions.FireBall(_object.x + 1, _object.y, 6, DIRECTION.RIGHT);
                    grotteActions.FireBall(_object.x, _object.y - 1, 7, DIRECTION.NUL);
                    grotteActions.Boum(_object.x, _object.y);
                    _object.x = grotteActions.oldx;
                    _object.y = grotteActions.oldy;
                    grotteActions.Remove(k);
                    return;
                } else {
                    _object.jump = 0;
                }
            } else {
                _object.y++;
            }
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
            if ((grotteActions.ch == '*') || (grotteActions.ch == 'H')) {
                _object.y = grotteActions.oldy;
                _object.x = grotteActions.oldx;
                if (_object.dir != DIRECTION.JUMP) {
                    _object.dir = DIRECTION.JUMP;
                    _object.jump = 5;
                    _object.seq = 0;
                }
            } else if ((grotteActions.ch != '.') && (grotteActions.ch != ' ')) {
                _object.y = grotteActions.oldy;
            }
            grotteActions.MoveObj(k, 1, _object.x, _object.y, 'x');
        }
    }

    private final GrotteActions.MoveProc MoveK4_ref = this::MoveK4;

    private void MoveGn1(int g) {
        // VAR
        int k0 = 0;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[g];
            if ((_object.y == 0) || (_object.y == 19))
                throw new HaltException();
            grotteActions.SetPxPy(_object.x, _object.y);
            grotteActions.oldx = _object.x;
            grotteActions.oldy = _object.y;
            if (_object.ch1 != 'Y') {
                if (((grotteActions.py % 2) != 0))
                    _object.y++;
                else
                    _object.y--;
            }
            if (_object.ch1 == '<')
                _object.d = GrotteActions.G;
            else if (_object.ch1 == '>')
                _object.d = GrotteActions.D;
            else
                _object.d = GrotteActions.B;
            if (aNSITerm.Report(_object.x, _object.y) == ' ')
                grotteActions.MoveObj(g, 5, _object.x, _object.y, _object.ch1);
            else
                _object.y = grotteActions.oldy;
            grotteActions.oldy = _object.y;
            k0 = grotteActions.first[OBJECT.K0.ordinal()];
            while (k0 != 0) {
                { // WITH
                    GrotteActions.Object _object2 = grotteActions.object[k0];
                    if ((_object2.seq != 2) && ((_object2.x == grotteActions.oldx) || (_object2.y == grotteActions.oldy)) && ((Math.abs(_object2.x - grotteActions.oldx) < 6) || (Math.abs(_object2.y - grotteActions.oldy) < 6)))
                        return;
                    k0 = _object2.next;
                }
            }
            if (((grotteActions.px % 2) != 0))
                grotteActions.Fire(OBJECT.L1, _object.x, _object.y, _object.d, true);
        }
    }

    private final GrotteActions.MoveProc MoveGn1_ref = this::MoveGn1;

    private void MoveGn2(int g) {
        { // WITH
            GrotteActions.Object _object = grotteActions.object[g];
            _object.seq--;
            if (_object.seq <= 0) {
                grotteActions.Remove(g);
            } else if (_object.seq == 4) {
                grotteActions.Fire(OBJECT.L1, _object.x, _object.y, GrotteActions.G, true);
                grotteActions.Fire(OBJECT.L1, _object.x, _object.y, GrotteActions.D, true);
                grotteActions.Fire(OBJECT.L1, _object.x, _object.y, GrotteActions.H, true);
            }
        }
    }

    private final GrotteActions.MoveProc MoveGn2_ref = this::MoveGn2;

    private void MoveAsc(int a) {
        { // WITH
            GrotteActions.Object _object = grotteActions.object[a];
            if ((_object.y < 2) || grotteActions.Mur(aNSITerm.Report(_object.x, _object.y - 2))) {
                aNSITerm.WriteAt(_object.x, _object.y, ' ');
                _object.y = _object.by;
                return;
            }
            grotteActions.oldx = _object.x;
            grotteActions.oldy = _object.y;
            do {
                grotteActions.oldy--;
                grotteActions.ch = aNSITerm.Report(_object.x, grotteActions.oldy);
            } while (!grotteActions.Dead(grotteActions.ch));
            if (!grotteActions.Mur(grotteActions.ch) && (grotteActions.ch != '=')) {
                grotteActions.oldy++;
                while (grotteActions.oldy != _object.y) {
                    grotteActions.object[grotteActions.e[grotteActions.oldy][_object.x]].y--;
                    grotteActions.e[grotteActions.oldy - 1][_object.x] = grotteActions.e[grotteActions.oldy][_object.x];
                    grotteActions.ch = aNSITerm.Report(_object.x, grotteActions.oldy);
                    grotteSupport.SetColor(grotteActions.ch);
                    aNSITerm.WriteAt(_object.x, grotteActions.oldy - 1, grotteActions.ch);
                    grotteActions.oldy++;
                }
            } else if (_object.y != _object.by) {
                grotteActions.oldy = _object.y - 1;
                grotteActions.object[grotteActions.e[grotteActions.oldy][_object.x]].y++;
                grotteActions.e[_object.y][_object.x] = grotteActions.e[grotteActions.oldy][_object.x];
                grotteActions.ch = aNSITerm.Report(_object.x, grotteActions.oldy);
                grotteSupport.SetColor(grotteActions.ch);
                aNSITerm.WriteAt(_object.x, _object.y, grotteActions.ch);
            }
            grotteActions.oldy = _object.y;
            _object.y--;
            aNSITerm.Color(4);
            if (aNSITerm.Report(_object.x, grotteActions.oldy) != '=') {
                aNSITerm.Color(4);
                aNSITerm.WriteAt(_object.x, _object.y, '=');
                grotteActions.e[_object.y][_object.x] = a;
            } else {
                grotteActions.MoveObj(a, 4, _object.x, _object.y, '=');
            }
        }
    }

    private final GrotteActions.MoveProc MoveAsc_ref = this::MoveAsc;

    private void MoveL1(int l) {
        // VAR
        int bl = 0;
        boolean t1 = false;
        boolean t2 = false;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[l];
            if (((_object.d % 2) != 0))
                _object.ch1 = '-';
            else
                _object.ch1 = '|';
            if (_object.flags.contains(GrotteActions.ND))
                _object.v = 1;
            else
                _object.v = 2;
            _object.ch2 = aNSITerm.Report(_object.x, _object.y);
            grotteActions.oldx = _object.x;
            grotteActions.oldy = _object.y;
            if ((_object.ch2 == _object.ch1) && (grotteActions.e[_object.y][_object.x] == l))
                _object.ch2 = ' ';
            _object.x += grotteActions.deltah[_object.d];
            _object.y += grotteActions.deltav[_object.d];
            if ((_object.x < 0) || (_object.y < 0) || (_object.x > 71) || (_object.y > 19)) {
                _object.x = grotteActions.oldx;
                _object.y = grotteActions.oldy;
                grotteActions.Remove(l);
                return;
            }
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
            if (grotteActions.ch == '8')
                grotteActions.ch = '.';
            if (grotteActions.ch == ' ') {
                if (_object.ch2 == ' ') {
                    grotteActions.MoveObj(l, _object.v, _object.x, _object.y, _object.ch1);
                } else {
                    grotteActions.WriteTo(grotteActions.oldx, grotteActions.oldy, _object.ch2);
                    aNSITerm.Color(_object.v);
                    aNSITerm.WriteAt(_object.x, _object.y, _object.ch1);
                }
            } else {
                if (_object.ch2 == ' ') {
                    aNSITerm.Color(_object.v);
                    aNSITerm.WriteAt(grotteActions.oldx, grotteActions.oldy, ' ');
                } else {
                    grotteActions.WriteTo(grotteActions.oldx, grotteActions.oldy, _object.ch2);
                    aNSITerm.Color(_object.v);
                }
                if (grotteActions.ch != '*')
                    aNSITerm.Ghost(_object.x, _object.y, _object.ch1);
            }
            switch (grotteSupport.TypeOf(grotteActions.ch)) {
                case EMPTY -> {
                }
                case BN, SBN -> {
                    _object.seq++;
                    if (grotteActions.ch == '.') {
                        grotteActions.Snd(OBJECT.L1, OBJECT.BM, _object.x, _object.y);
                        aNSITerm.WriteAt(_object.x, _object.y, ' ');
                        grotteActions.Boum(_object.x, _object.y);
                        grotteActions.Dispose(l);
                        return;
                    } else {
                        if (_object.d > GrotteActions.H)
                            _object.d -= 2;
                        else
                            _object.d += 2;
                    }
                }
                case PLAYER -> {
                    if (_object.flags.contains(GrotteActions.ND)) {
                        grotteActions.Boum(_object.x, _object.y);
                        grotteActions.Snd(OBJECT.L1, OBJECT.PLAYER, _object.x, _object.y);
                        grotteActions.DecVie(grotteActions.e[_object.y][_object.x]);
                        grotteActions.Remove(l);
                        return;
                    }
                }
                case L1 -> {
                    bl = grotteActions.e[_object.y][_object.x];
                    t1 = (_object.flags.contains(GrotteActions.ND));
                    t2 = (grotteActions.object[bl].flags.contains(GrotteActions.ND));
                    if (t1 != t2)
                        grotteActions.decpt++;
                    if (t1 || t2) {
                        grotteActions.Boum(_object.x, _object.y);
                        grotteActions.Snd(OBJECT.L1, OBJECT.L1, _object.x, _object.y);
                        grotteActions.Remove(bl);
                        grotteActions.Dispose(l);
                        return;
                    }
                }
                case L3 -> {
                    if (!_object.flags.contains(GrotteActions.ND)) {
                        bl = grotteActions.e[_object.y][_object.x];
                        grotteActions.Boum(_object.x, _object.y);
                        grotteActions.Snd(OBJECT.L1, OBJECT.L3, _object.x, _object.y);
                        grotteActions.Remove(bl);
                        grotteActions.Dispose(l);
                        return;
                    } else {
                        grotteActions.WriteTo(_object.x, _object.y, grotteActions.ch);
                        grotteActions.Dispose(l);
                        return;
                    }
                }
                case K0, K1, K2, K3, K4, BALL, GN2, PIC -> {
                    bl = grotteActions.e[_object.y][_object.x];
                    grotteActions.WriteTo(_object.x, _object.y, grotteActions.ch);
                    grotteActions.Aie(OBJECT.L1, grotteActions.object[bl].type, bl, 1);
                    grotteActions.Boum(_object.x, _object.y);
                    grotteActions.Dispose(l);
                    return;
                }
                case PLAT -> {
                    { // WITH
                        GrotteActions.Object _object2 = grotteActions.object[grotteActions.first[OBJECT.PLAT.ordinal()]];
                        if ((_object2.x == grotteSupport.ppos[0].x) && (_object2.y == grotteSupport.ppos[0].y))
                            _object2.seq = 1;
                    }
                    aNSITerm.Color(4);
                    grotteActions.Boum(_object.x, _object.y);
                    grotteActions.WriteTo(_object.x, _object.y, 'T');
                    grotteActions.Dispose(l);
                    return;
                }
                default -> {
                    if (grotteActions.ch == '/') {
                        _object.d = 5 - _object.d;
                    } else if (grotteActions.ch == '\\') {
                        if (((_object.d % 2) != 0))
                            _object.d++;
                        else
                            _object.d--;
                    } else if ((grotteActions.ch == ':') || ((_object.x == grotteActions.ax) && (_object.y == grotteActions.ay)) || (grotteActions.ch == '!')) {
                        _object.seq++;
                        if (((_object.d % 2) != 0)) {
                            if (_object.d == GrotteActions.G)
                                _object.d = GrotteActions.D;
                            else
                                _object.d = GrotteActions.G;
                        } else {
                            _object.seq = 0;
                        }
                    } else {
                        _object.seq = 0;
                    }
                }
            }
            if ((grotteActions.ch == ' ') || (grotteActions.ch == _object.ch1))
                grotteActions.e[_object.y][_object.x] = l;
            if (_object.seq >= 0)
                grotteActions.Remove(l);
        }
    }

    private final GrotteActions.MoveProc MoveL1_ref = this::MoveL1;

    private void MoveL2(int l) {
        // VAR
        int bl = 0;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[l];
            if (_object.d == GrotteActions.G)
                _object.ch1 = '(';
            else
                _object.ch1 = ')';
            _object.ch2 = aNSITerm.Report(_object.x, _object.y);
            grotteActions.oldx = _object.x;
            grotteActions.oldy = _object.y;
            if ((_object.ch2 == _object.ch1) && (grotteActions.e[_object.y][_object.x] == l))
                _object.ch2 = ' ';
            _object.x += grotteActions.deltah[_object.d];
            if ((_object.x < 0) || (_object.x > 71)) {
                _object.x = grotteActions.oldx;
                grotteActions.Remove(l);
                return;
            }
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
            if ((grotteActions.ch == ' ') || (grotteActions.ch == _object.ch1)) {
                if (_object.ch2 == ' ') {
                    grotteActions.MoveObj(l, 2, _object.x, _object.y, _object.ch1);
                } else {
                    grotteActions.e[_object.y][_object.x] = l;
                    grotteActions.WriteTo(grotteActions.oldx, _object.y, _object.ch2);
                    aNSITerm.Color(2);
                    aNSITerm.WriteAt(_object.x, _object.y, _object.ch1);
                }
            } else {
                if (_object.ch2 == ' ') {
                    aNSITerm.Color(2);
                    aNSITerm.WriteAt(grotteActions.oldx, _object.y, ' ');
                } else {
                    grotteActions.WriteTo(grotteActions.oldx, _object.y, _object.ch2);
                    aNSITerm.Color(2);
                }
                if (grotteActions.ch != '*')
                    aNSITerm.Ghost(_object.x, _object.y, _object.ch1);
            }
            switch (grotteSupport.TypeOf(grotteActions.ch)) {
                case EMPTY, PLAYER, L2 -> {
                }
                case L1, L3 -> {
                    bl = grotteActions.e[_object.y][_object.x];
                    if (grotteActions.object[bl].flags.contains(GrotteActions.ND)) {
                        grotteActions.Boum(_object.x, _object.y);
                        grotteActions.Snd(OBJECT.L1, OBJECT.L1, _object.x, _object.y);
                        grotteActions.addpt++;
                        grotteActions.Remove(bl);
                        return;
                    }
                }
                case K0, K1, K2, K3, K4, BALL, GN1, GN2, PIC, NID, BUB -> {
                    bl = grotteActions.e[_object.y][_object.x];
                    grotteActions.WriteTo(_object.x, _object.y, grotteActions.ch);
                    _object.v = _object.vie;
                    if (grotteActions.object[bl].vie < _object.v)
                        _object.v = grotteActions.object[bl].vie;
                    grotteActions.Aie(OBJECT.L1, grotteActions.object[bl].type, bl, _object.v);
                    grotteActions.Boum(_object.x, _object.y);
                    _object.vie -= _object.v;
                    if (_object.vie == 0) {
                        grotteActions.Dispose(l);
                        return;
                    }
                    if (aNSITerm.Report(_object.x, _object.y) == ' ') {
                        aNSITerm.WriteAt(_object.x, _object.y, _object.ch1);
                        grotteActions.e[_object.y][_object.x] = l;
                    }
                }
                default -> {
                    if (grotteActions.ch == '.') {
                        aNSITerm.WriteAt(_object.x, _object.y, _object.ch1);
                        grotteActions.e[_object.y][_object.x] = l;
                    } else if ((grotteActions.ch == 'A') || (grotteSupport.TypeOf(grotteActions.ch) == OBJECT.SBN) || (grotteActions.ch == '%') || (grotteActions.ch == '!') || (grotteActions.ch == ':')) {
                        _object.seq += 5;
                        if (_object.d == GrotteActions.G) {
                            _object.d = GrotteActions.D;
                            grotteActions.l2l--;
                            grotteActions.l2r++;
                        } else {
                            _object.d = GrotteActions.G;
                            grotteActions.l2r--;
                            grotteActions.l2l++;
                        }
                    } else {
                        grotteActions.WriteTo(_object.x, _object.y, grotteActions.ch);
                        grotteActions.Boum(grotteActions.oldx, _object.y);
                        grotteActions.Dispose(l);
                        return;
                    }
                }
            }
            _object.seq++;
            if (_object.seq >= 0) {
                grotteActions.Remove(l);
                return;
            }
        }
    }

    private final GrotteActions.MoveProc MoveL2_ref = this::MoveL2;

    private void MoveL3(int l) {
        { // WITH
            GrotteActions.Object _object = grotteActions.object[l];
            if (_object.d == GrotteActions.G)
                _object.ch1 = '{';
            else
                _object.ch1 = '}';
            _object.ch2 = aNSITerm.Report(_object.x, _object.y);
            grotteActions.oldx = _object.x;
            grotteActions.oldy = _object.y;
            if ((_object.ch2 == _object.ch1) && (grotteActions.e[_object.y][_object.x] == l))
                _object.ch2 = ' ';
            _object.x += grotteActions.deltah[_object.d];
            if ((_object.x < 0) || (_object.x > 71)) {
                _object.x = grotteActions.oldx;
                grotteActions.Remove(l);
                return;
            }
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
            if (grotteActions.ch != ' ') {
                _object.x = grotteActions.oldx;
                grotteActions.oldy = _object.y;
                grotteActions.Dispose(l);
                if (_object.ch2 == ' ')
                    grotteActions.CreateObj(_object.x, _object.y);
            } else {
                if (_object.ch2 == ' ') {
                    grotteActions.MoveObj(l, 1, _object.x, _object.y, _object.ch1);
                } else {
                    grotteActions.WriteTo(grotteActions.oldx, grotteActions.oldy, _object.ch2);
                    grotteActions.e[_object.y][_object.x] = l;
                    aNSITerm.Color(1);
                    aNSITerm.WriteAt(_object.x, _object.y, _object.ch1);
                }
            }
        }
    }

    private final GrotteActions.MoveProc MoveL3_ref = this::MoveL3;

    private void MoveBall(int b) {
        // VAR
        int bo = 0;
        int z = 0;
        OBJECT t = OBJECT.EMPTY;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[b];
            if ((_object.x == 0) || (_object.y == 0) || (_object.x == 71) || (_object.y == 19)) {
                grotteActions.Remove(b);
                return;
            }
            aNSITerm.Color(6);
            grotteActions.oldx = _object.x;
            grotteActions.oldy = _object.y;
            if (_object.jump > 1) {
                _object.y--;
                _object.jump--;
            } else if (_object.jump == 1) {
                _object.jump--;
            } else {
                _object.y++;
            }
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
            if (grotteActions.ch == '*') {
                grotteActions.AiePlayer(_object.x, _object.y, 'O', b);
                return;
            } else if ((grotteActions.ch == '+') || (grotteActions.ch == 'X')) {
                bo = grotteActions.e[_object.y][_object.x];
                grotteActions.Boum(_object.x, _object.y);
                grotteActions.Aie(OBJECT.BALL, grotteActions.object[bo].type, bo, 1);
                _object.x = grotteActions.oldx;
                _object.y = grotteActions.oldy;
                grotteActions.Remove(b);
                return;
            } else if ((grotteActions.ch != '.') && (grotteActions.ch != ' ') && (grotteActions.ch != 'O')) {
                _object.x = grotteActions.oldx;
                _object.y = grotteActions.oldy;
                grotteActions.Boum(_object.x, _object.y);
                grotteActions.Remove(b);
                return;
            }
            if (_object.dir == DIRECTION.LEFT)
                _object.x--;
            else if (_object.dir == DIRECTION.RIGHT)
                _object.x++;
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
            t = grotteSupport.TypeOf(grotteActions.ch);
            if ((t != OBJECT.EMPTY) && (t != OBJECT.PLAYER) && (t != OBJECT.SBN) && (t != OBJECT.K1) && (t != OBJECT.K2) && (t != OBJECT.BALL)) {
                if (_object.dir == DIRECTION.LEFT)
                    _object.dir = DIRECTION.RIGHT;
                else
                    _object.dir = DIRECTION.LEFT;
                _object.x = grotteActions.oldx;
            } else if (grotteActions.ch == '*') {
                grotteActions.AiePlayer(_object.x, _object.y, 'O', b);
                return;
            }
            if (_object.dir != DIRECTION.NUL) {
                z = _object.x - grotteActions.oldx + _object.x;
                t = grotteSupport.TypeOf(aNSITerm.Report(z, _object.y));
                if ((t != OBJECT.EMPTY) && (t != OBJECT.PLAYER) && (t != OBJECT.SBN) && (t != OBJECT.K1) && (t != OBJECT.K2) && (t != OBJECT.BALL)) {
                    if (_object.dir == DIRECTION.LEFT)
                        _object.dir = DIRECTION.RIGHT;
                    else
                        _object.dir = DIRECTION.LEFT;
                }
            }
            z = _object.y - grotteActions.oldy + _object.y;
            t = grotteSupport.TypeOf(aNSITerm.Report(_object.x, z));
            if ((t != OBJECT.EMPTY) && (t != OBJECT.PLAYER) && (t != OBJECT.SBN) && (t != OBJECT.K1) && (t != OBJECT.K2) && (t != OBJECT.BALL)) {
                if (_object.jump > 0) {
                    _object.by -= _object.jump - 1;
                    _object.jump = 1;
                } else {
                    _object.jump = _object.by;
                    if (_object.by > 0)
                        _object.by--;
                }
            }
            grotteActions.MoveObj(b, 6, _object.x, _object.y, 'O');
        }
    }

    private final GrotteActions.MoveProc MoveBall_ref = this::MoveBall;

    private void MovePic(int p) {
        // VAR
        int bo = 0;
        int i = 0;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[p];
            if (_object.y == 19) {
                grotteActions.Remove(p);
                return;
            }
            if (_object.seq == 2) {
                grotteActions.SetPxPy(_object.x, _object.y);
                _object.d = GrotteActions.N;
                i = grotteSupport.Random() % 8;
                if ((grotteActions.py > _object.y) && (Math.abs(_object.x - grotteActions.px) <= i) && (grotteSupport.Random() % 4 == 0))
                    _object.seq = 1;
            } else {
                if (_object.d != GrotteActions.B) {
                    _object.d = GrotteActions.B;
                    return;
                }
                grotteActions.oldx = _object.x;
                grotteActions.oldy = _object.y;
                _object.y++;
                grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
                if (grotteActions.ch == '8')
                    grotteActions.ch = '.';
                if ((grotteActions.ch == '>') || (grotteActions.ch == '\\')) {
                    _object.ch1 = aNSITerm.Report(_object.x + 1, _object.y);
                    if (_object.ch1 == ' ') {
                        _object.x++;
                        grotteActions.ch = _object.ch1;
                    }
                } else if ((grotteActions.ch == '<') || (grotteActions.ch == '/')) {
                    _object.ch1 = aNSITerm.Report(_object.x - 1, _object.y);
                    if (_object.ch1 == ' ') {
                        _object.x--;
                        grotteActions.ch = _object.ch1;
                    }
                }
                aNSITerm.Color(5);
                switch (grotteSupport.TypeOf(grotteActions.ch)) {
                    case PLAYER -> {
                        grotteActions.AiePlayer(_object.x, _object.y, 'V', p);
                        return;
                    }
                    case EMPTY -> {
                    }
                    case K0, K1, K2, K3, K4, L1, L2, BALL, GN2 -> {
                        bo = grotteActions.e[_object.y][_object.x];
                        grotteActions.Aie(OBJECT.PIC, grotteActions.object[bo].type, bo, grotteActions.object[bo].vie);
                        grotteActions.Boum(_object.x, _object.y);
                        _object.y = grotteActions.oldy;
                    }
                    default -> {
                        if (grotteActions.ch == '.') {
                            grotteActions.Snd(OBJECT.PIC, OBJECT.BM, _object.x, _object.y);
                            grotteActions.Boum(_object.x, _object.y);
                        } else {
                            _object.y = grotteActions.oldy;
                            _object.x = grotteActions.oldx;
                            grotteActions.Boum(_object.x, _object.y);
                            grotteActions.Remove(p);
                            return;
                        }
                    }
                }
                grotteActions.MoveObj(p, 5, _object.x, _object.y, 'V');
            }
        }
    }

    private final GrotteActions.MoveProc MovePic_ref = this::MovePic;

    private void MovePlat(int p) {
        // VAR
        int dx = 0;
        int dy = 0;
        int cc = 0;
        int pl = 0;
        char c1 = (char) 0;
        OBJECT t = OBJECT.EMPTY;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[p];
            _object.bx = grotteSupport.ppos[_object.seq].x;
            _object.by = grotteSupport.ppos[_object.seq].y;
            if ((_object.x == _object.bx) && (_object.y == _object.by)) {
                if (aNSITerm.Report(_object.x, _object.y) == ' ') {
                    aNSITerm.Color(4);
                    aNSITerm.WriteAt(_object.x, _object.y, 'T');
                    grotteActions.e[_object.y][_object.x] = p;
                }
                if (_object.seq == 0)
                    return;
                _object.seq++;
                cc = _object.seq;
                if (cc > grotteSupport.pcount)
                    _object.seq = 0;
                _object.bx = grotteSupport.ppos[_object.seq].x;
                _object.by = grotteSupport.ppos[_object.seq].y;
            }
            c1 = aNSITerm.Report(_object.x, _object.y);
            grotteActions.SetPxPy(_object.x, _object.y);
            pl = grotteActions.e[_object.y - 1][_object.x];
            grotteActions.oldx = _object.x;
            grotteActions.oldy = _object.y;
            if ((grotteActions.px == grotteActions.oldx) && (grotteActions.py < grotteActions.oldy - 1))
                return;
            if (_object.x != _object.bx) {
                if (_object.x < _object.bx)
                    _object.x++;
                else
                    _object.x--;
            } else {
                if (_object.y < _object.by)
                    _object.y++;
                else
                    _object.y--;
            }
            if ((_object.x < 0) || (_object.y < 0) || (_object.x > 71) || (_object.y > 19))
                throw new HaltException();
            if (_object.flags.contains(0)) {
                _object.flags.excl(0);
                _object.flags.incl(1);
            } else {
                _object.flags.excl(1);
            }
            t = grotteSupport.TypeOf(aNSITerm.Report(_object.x, _object.y));
            if ((t == OBJECT.BM) || (t == OBJECT.MR) || (t == OBJECT.SBN) || (t == OBJECT.NID) || (t == OBJECT.BUB) || (t == OBJECT.PIC))
                _object.flags.incl(0);
            if (_object.flags.contains(1)) {
                aNSITerm.Color(4);
                if (_object.flags.contains(0)) {
                    aNSITerm.Ghost(_object.x, _object.y, 'T');
                } else {
                    grotteActions.e[_object.y][_object.x] = p;
                    aNSITerm.WriteAt(_object.x, _object.y, 'T');
                }
                grotteActions.WriteTo(grotteActions.oldx, grotteActions.oldy, c1);
            } else {
                if (_object.flags.contains(0)) {
                    aNSITerm.Color(4);
                    aNSITerm.Ghost(_object.x, _object.y, 'T');
                    if (grotteActions.e[grotteActions.oldy][grotteActions.oldx] == p)
                        aNSITerm.WriteAt(grotteActions.oldx, grotteActions.oldy, ' ');
                } else {
                    grotteActions.MoveObj(p, 4, _object.x, _object.y, 'T');
                }
            }
            grotteActions.Snd(OBJECT.PLAYER, OBJECT.GN2, _object.x, _object.y);
            c1 = aNSITerm.Report(_object.x, _object.y - 1);
            dx = _object.x - grotteActions.oldx;
            dy = _object.y - grotteActions.oldy;
            t = grotteSupport.TypeOf(c1);
            if ((grotteActions.px == grotteActions.oldx) && (grotteActions.py == grotteActions.oldy - 1) && ((t == OBJECT.EMPTY) || (t == OBJECT.BN) || (t == OBJECT.SBN) || (t == OBJECT.L1) || (t == OBJECT.L2))) {
                { // WITH
                    GrotteActions.Object _object2 = grotteActions.object[pl];
                    if (dy != -1)
                        aNSITerm.WriteAt(_object2.x, _object2.y, ' ');
                    _object2.x += dx;
                    _object2.y += dy;
                    grotteActions.e[_object2.y][_object2.x] = pl;
                    aNSITerm.Color(2);
                    if (c1 == ' ')
                        aNSITerm.WriteAt(_object2.x, _object2.y, '*');
                    else
                        aNSITerm.Ghost(_object2.x, _object2.y, '*');
                }
            }
        }
    }

    private final GrotteActions.MoveProc MovePlat_ref = this::MovePlat;

    private void MoveWoof(int p) {
        // VAR
        int pl = 0;
        char ch = (char) 0;
        OBJECT t = OBJECT.EMPTY;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[p];
            grotteActions.SetPxPy(_object.x, _object.y);
            if ((grotteActions.px == _object.x) && (grotteActions.py < _object.y - 1))
                return;
            grotteActions.oldx = _object.x;
            grotteActions.oldy = _object.y;
            if (_object.seq == 0)
                _object.x++;
            else
                _object.x--;
            if (aNSITerm.Report(_object.x, _object.y) == ' ') {
                grotteActions.MoveObj(p, 4, _object.x, _object.y, 'W');
                grotteActions.Snd(OBJECT.PLAYER, OBJECT.GN2, _object.x, _object.y);
                if ((grotteActions.px == grotteActions.oldx) && (grotteActions.py == _object.y - 1)) {
                    pl = grotteActions.e[grotteActions.py][grotteActions.px];
                    ch = aNSITerm.Report(_object.x, grotteActions.py);
                    t = grotteSupport.TypeOf(ch);
                    if ((t == OBJECT.EMPTY) || (t == OBJECT.BN) || (t == OBJECT.SBN) || (t == OBJECT.L1) || (t == OBJECT.L2)) {
                        { // WITH
                            GrotteActions.Object _object2 = grotteActions.object[pl];
                            aNSITerm.WriteAt(_object2.x, _object2.y, ' ');
                            _object2.x = grotteActions.object[p].x;
                            grotteActions.e[_object2.y][_object2.x] = pl;
                            aNSITerm.Color(2);
                            if (ch == ' ')
                                aNSITerm.WriteAt(_object2.x, _object2.y, '*');
                            else
                                aNSITerm.Ghost(_object2.x, _object2.y, '*');
                        }
                    }
                }
            } else {
                _object.x = grotteActions.oldx;
                if (_object.seq == 0)
                    _object.seq = 1;
                else
                    _object.seq = 0;
            }
        }
    }

    private final GrotteActions.MoveProc MoveWoof_ref = this::MoveWoof;

    private void MoveTPlat(int p) {
        // VAR
        char ch = (char) 0;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[p];
            grotteActions.SetPxPy(_object.x, _object.y);
            if ((grotteActions.py == _object.y) && (Math.abs(_object.x - grotteActions.px) < 6)) {
                ch = aNSITerm.Report(_object.x, _object.y);
                if ((ch == ' ') || (ch == 'U')) {
                    _object.seq = 15;
                    aNSITerm.Color(4);
                    aNSITerm.WriteAt(_object.x, _object.y, 'U');
                    grotteActions.e[_object.y][_object.x] = p;
                }
            }
            if (_object.seq > 0) {
                _object.seq--;
                if (_object.seq == 0)
                    aNSITerm.WriteAt(_object.x, _object.y, ' ');
            }
        }
    }

    private final GrotteActions.MoveProc MoveTPlat_ref = this::MoveTPlat;

    private void Nid(int n) {
        // VAR
        int c = 0;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[n];
            grotteActions.SetPxPy(_object.x, _object.y);
            if (_object.seq < 0) {
                _object.seq++;
                return;
            }
            if (grotteActions.mvie < 4)
                return;
            if (_object.bx == GrotteActions.G)
                _object.bx = GrotteActions.D;
            else
                _object.bx = GrotteActions.G;
            if (aNSITerm.Report(_object.x + grotteActions.deltah[_object.bx], _object.y) == ' ')
                grotteActions.Fire(OBJECT.L3, _object.x, _object.y, _object.bx, true);
            aNSITerm.Color(5);
            aNSITerm.WriteAt(_object.x, _object.y, 'Z');
            grotteActions.e[_object.y][_object.x] = n;
            if (grotteActions.mvie > 30)
                _object.by = 120;
            else
                _object.by = grotteActions.mvie * 4;
            c = 124 - _object.by;
            _object.seq = grotteSupport.Random() % c;
            _object.seq = -_object.seq;
        }
    }

    private final GrotteActions.MoveProc Nid_ref = this::Nid;

    private void Bub(int b) {
        // VAR
        int c = 0;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[b];
            grotteActions.SetPxPy(_object.x, _object.y);
            if (_object.seq < 0) {
                _object.seq++;
                return;
            }
            aNSITerm.Color(5);
            aNSITerm.WriteAt(_object.x, _object.y, 'N');
            grotteActions.e[_object.y][_object.x] = b;
            grotteActions.FireBall(_object.x - 1, _object.y, 6, DIRECTION.LEFT);
            grotteActions.FireBall(_object.x + 1, _object.y, 6, DIRECTION.RIGHT);
            grotteActions.FireBall(_object.x, _object.y + 1, 6, DIRECTION.NUL);
            if (grotteActions.mvie > 20)
                _object.by = 120;
            else
                _object.by = grotteActions.mvie * 4;
            c = 124 - _object.by;
            _object.seq = grotteSupport.Random() % c;
            _object.seq = -_object.seq - 3;
        }
    }

    private final GrotteActions.MoveProc Bub_ref = this::Bub;

    private void ChuteBonus(int b) {
        // VAR
        int bo = 0;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[b];
            grotteActions.oldx = _object.x;
            grotteActions.oldy = _object.y;
            _object.y++;
            if (_object.y > 19)
                throw new HaltException();
            if (aNSITerm.Report(_object.x, _object.y) == '/')
                _object.x--;
            else if (aNSITerm.Report(_object.x, _object.y) == '\\')
                _object.x++;
            aNSITerm.Color(2);
            grotteActions.ch = aNSITerm.Report(_object.x, _object.y);
            if (grotteActions.ch == '.') {
                grotteActions.Snd(OBJECT.K1, OBJECT.BN, _object.x, _object.y);
                grotteActions.addpt += 10;
            } else if ((grotteActions.ch == ' ') || (grotteActions.ch == '*') || (grotteActions.ch == '8')) {
            } else if (grotteActions.Dead(grotteActions.ch) || (grotteActions.ch == 'T') || (grotteActions.ch == 'W')) {
                _object.y = grotteActions.oldy;
                _object.x = grotteActions.oldx;
                grotteActions.addpt += 10;
                grotteActions.Boum(_object.x, _object.y);
                grotteActions.Remove(b);
                return;
            } else {
                grotteActions.Snd(OBJECT.K0, OBJECT.BN, _object.x, _object.y);
                bo = grotteActions.e[_object.y][_object.x];
                grotteActions.Boum(_object.x, _object.y);
                { // WITH
                    GrotteActions.Object _object2 = grotteActions.object[bo];
                    _object2.seq = 0;
                    grotteActions.Aie(OBJECT.BN, _object2.type, bo, _object2.vie);
                    grotteActions.addpt += grotteActions.dispt[_object2.type.ordinal()] * 10;
                }
            }
            grotteActions.MoveObj(b, 2, _object.x, _object.y, '%');
        }
    }

    private final GrotteActions.MoveProc ChuteBonus_ref = this::ChuteBonus;

    private void CheckDl(int dl) {
        // VAR
        char ch = (char) 0;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[dl];
            grotteActions.SetPxPy(_object.x, _object.y);
            if ((grotteActions.py == _object.y) && (Math.abs(_object.x - grotteActions.px) < 6)) {
                ch = aNSITerm.Report(_object.x, _object.y);
                if (ch == ' ') {
                    grotteActions.Remove(dl);
                    aNSITerm.Color(2);
                    aNSITerm.WriteAt(_object.x, _object.y, '$');
                    grotteActions.Boum(_object.x, _object.y);
                }
            }
        }
    }

    private final GrotteActions.MoveProc CheckDl_ref = this::CheckDl;

    private void EraseBoum(int b) {
        // VAR
        int c1 = 0;
        int c2 = 0;

        { // WITH
            GrotteActions.Object _object = grotteActions.object[b];
            for (c1 = -1; c1 <= 1; c1++) {
                for (c2 = -1; c2 <= 1; c2++) {
                    if ((c1 != 0) || (c2 != 0))
                        grotteActions.WriteTo(_object.x + c2, _object.y + c1, aNSITerm.Report(_object.x + c2, _object.y + c1));
                }
            }
        }
        grotteActions.Dispose(b);
    }

    private final GrotteActions.MoveProc EraseBoum_ref = this::EraseBoum;

    private void EmptyMove(int o) {
    }

    private final GrotteActions.MoveProc EmptyMove_ref = this::EmptyMove;

    private void InitVars() {
        // VAR
        OBJECT t = OBJECT.EMPTY;
        int c = 0;
        int x = 0;

        grotteActions.deltav[0] = 0;
        grotteActions.deltah[0] = 0;
        grotteActions.deltav[1] = 0;
        grotteActions.deltah[1] = -1;
        grotteActions.deltav[2] = -1;
        grotteActions.deltah[2] = 0;
        grotteActions.deltav[3] = 0;
        grotteActions.deltah[3] = 1;
        grotteActions.deltav[4] = 1;
        grotteActions.deltah[4] = 0;
        for (int _t = 0; _t < OBJECT.values().length; _t++) {
            t = OBJECT.values()[_t];
            grotteActions.Move[t.ordinal()] = EmptyMove_ref;
            grotteActions.speed[t.ordinal()] = 255;
            grotteActions.aiept[t.ordinal()] = 0;
            grotteActions.dispt[t.ordinal()] = 0;
        }
        grotteActions.Move[OBJECT.PLAYER.ordinal()] = grotteActions.MovePlayer_ref;
        grotteActions.speed[OBJECT.PLAYER.ordinal()] = 15;
        grotteActions.Move[OBJECT.K0.ordinal()] = grotteActions.MoveK0_ref;
        grotteActions.speed[OBJECT.K0.ordinal()] = 31;
        grotteActions.dispt[OBJECT.K0.ordinal()] = 50;
        grotteActions.Move[OBJECT.K1.ordinal()] = MoveK1_ref;
        grotteActions.speed[OBJECT.K1.ordinal()] = 37;
        grotteActions.dispt[OBJECT.K1.ordinal()] = 10;
        grotteActions.aiept[OBJECT.K1.ordinal()] = 3;
        grotteActions.Move[OBJECT.K2.ordinal()] = MoveK2_ref;
        grotteActions.speed[OBJECT.K2.ordinal()] = 34;
        grotteActions.dispt[OBJECT.K2.ordinal()] = 2;
        grotteActions.aiept[OBJECT.K2.ordinal()] = 2;
        grotteActions.Move[OBJECT.K3.ordinal()] = MoveK3_ref;
        grotteActions.speed[OBJECT.K3.ordinal()] = 47;
        grotteActions.dispt[OBJECT.K3.ordinal()] = 3;
        grotteActions.aiept[OBJECT.K3.ordinal()] = 1;
        grotteActions.Move[OBJECT.K4.ordinal()] = MoveK4_ref;
        grotteActions.speed[OBJECT.K4.ordinal()] = 29;
        grotteActions.dispt[OBJECT.K4.ordinal()] = 20;
        grotteActions.Move[OBJECT.GN1.ordinal()] = MoveGn1_ref;
        grotteActions.speed[OBJECT.GN1.ordinal()] = 231;
        grotteActions.dispt[OBJECT.GN1.ordinal()] = 13;
        grotteActions.Move[OBJECT.GN2.ordinal()] = MoveGn2_ref;
        grotteActions.speed[OBJECT.GN2.ordinal()] = 15;
        grotteActions.dispt[OBJECT.GN2.ordinal()] = 4;
        grotteActions.Move[OBJECT.ASC.ordinal()] = MoveAsc_ref;
        grotteActions.speed[OBJECT.ASC.ordinal()] = 40;
        grotteActions.Move[OBJECT.L1.ordinal()] = MoveL1_ref;
        grotteActions.speed[OBJECT.L1.ordinal()] = 8;
        grotteActions.Move[OBJECT.L2.ordinal()] = MoveL2_ref;
        grotteActions.speed[OBJECT.L2.ordinal()] = 12;
        grotteActions.Move[OBJECT.L3.ordinal()] = MoveL3_ref;
        grotteActions.speed[OBJECT.L3.ordinal()] = 13;
        grotteActions.Move[OBJECT.BALL.ordinal()] = MoveBall_ref;
        grotteActions.speed[OBJECT.BALL.ordinal()] = 17;
        grotteActions.dispt[OBJECT.BALL.ordinal()] = 11;
        grotteActions.aiept[OBJECT.BALL.ordinal()] = 1;
        grotteActions.Move[OBJECT.PIC.ordinal()] = MovePic_ref;
        grotteActions.speed[OBJECT.PIC.ordinal()] = 19;
        grotteActions.dispt[OBJECT.PIC.ordinal()] = 9;
        grotteActions.Move[OBJECT.PLAT.ordinal()] = MovePlat_ref;
        grotteActions.speed[OBJECT.PLAT.ordinal()] = 30;
        grotteActions.Move[OBJECT.WOOF.ordinal()] = MoveWoof_ref;
        grotteActions.speed[OBJECT.WOOF.ordinal()] = 30;
        grotteActions.Move[OBJECT.TPLAT.ordinal()] = MoveTPlat_ref;
        grotteActions.speed[OBJECT.TPLAT.ordinal()] = 15;
        grotteActions.Move[OBJECT.NID.ordinal()] = Nid_ref;
        grotteActions.speed[OBJECT.NID.ordinal()] = 64;
        grotteActions.dispt[OBJECT.NID.ordinal()] = 40;
        grotteActions.aiept[OBJECT.NID.ordinal()] = 7;
        grotteActions.Move[OBJECT.BUB.ordinal()] = Bub_ref;
        grotteActions.speed[OBJECT.BUB.ordinal()] = 64;
        grotteActions.dispt[OBJECT.BUB.ordinal()] = 30;
        grotteActions.aiept[OBJECT.BUB.ordinal()] = 5;
        grotteActions.Move[OBJECT.BN.ordinal()] = ChuteBonus_ref;
        grotteActions.speed[OBJECT.BN.ordinal()] = 17;
        grotteActions.dispt[OBJECT.BN.ordinal()] = 50;
        grotteActions.Move[OBJECT.DL.ordinal()] = CheckDl_ref;
        grotteActions.speed[OBJECT.DL.ordinal()] = 15;
        grotteActions.Move[OBJECT.BM.ordinal()] = EraseBoum_ref;
        grotteActions.speed[OBJECT.BM.ordinal()] = 5;
        grotteSupport.followscore = 1000;
        grotteSupport.pvie = 5;
        grotteActions.repch = ((char) 0);
        grotteActions.reps = 0;
        for (c = 0; c <= 19; c++) {
            grotteActions.e[c] = grotteActions.els[c];
            for (x = 0; x <= 71; x++) {
                grotteActions.e[c][x] = 0;
            }
            grotteSupport.drK2[c] = grotteSupport.drsK2[c];
        }
        grotteActions.at = 0;
        grotteActions.nlj = 0;
        grotteActions.nlk = 0;
        grotteActions.addpt = 0;
        grotteActions.decpt = 0;
        grotteActions.oldscore = 0;
        grotteActions.clkt = 0;
        grotteActions.rfx = 0;
        grotteActions.rfy = 0;
    }

    private void GameFinish() {
        if (grotteSupport.stat != _Stat.Finish)
            return;
        aNSITerm.Color(7);
        aNSITerm.Goto(0, 21);
        aNSITerm.WriteString("YOU MADE IT !!!");
        grotteActions.rfx = 0;
        grotteActions.rfy = 20;
        clock.StartTime(grotteSupport.time);
        grotteActions.Delay(1536);
        do {
            aNSITerm.Read(new Runtime.FieldRef<>(grotteActions::getCh, grotteActions::setCh));
        } while (grotteActions.ch > ((char) 03));
        while (grotteActions.addpt > 0) {
            aNSITerm.Read(new Runtime.FieldRef<>(grotteActions::getCh, grotteActions::setCh));
            if (grotteActions.ch != ((char) 0)) {
                grotteSupport.score += grotteActions.addpt - 5;
                grotteActions.addpt = 5;
            }
            grotteActions.AddPt();
        }
        grotteActions.ShowScore();
        aNSITerm.WriteString("    ");
        /*  Flush; */
        if (clock.WaitTime(grotteSupport.time, 800)) {
        }
    }

    private void Play() {
        // VAR
        OBJECT t = OBJECT.EMPTY;
        int nc = 0;
        int mo = 0;

        grotteActions.wtc = 0;
        do {
            if (grotteActions.first[OBJECT.PLAYER.ordinal()] == 0)
                throw new HaltException();
            for (int _t = 0; _t < OBJECT.values().length; _t++) {
                t = OBJECT.values()[_t];
                { // WITH
                    GrotteSupport.Attr _attr = grotteSupport.attr[t.ordinal()];
                    if (_attr.nb == 0) {
                        _attr.timeout = grotteActions.speed[t.ordinal()];
                        _attr.ct = _attr.timeout;
                    }
                    if (_attr.ct <= 1) {
                        for (nc = 1; nc <= _attr.nt; nc++) {
                            if (_attr.co != 0) {
                                mo = _attr.co;
                                _attr.co = grotteActions.object[_attr.co].next;
                                grotteActions.Move[t.ordinal()].invoke(mo);
                            }
                        }
                        _attr.ct = _attr.dt;
                    } else {
                        _attr.ct--;
                    }
                    _attr.timeout--;
                    if (_attr.timeout == 0) {
                        _attr.timeout = grotteActions.speed[t.ordinal()];
                        grotteActions.SetAttr(t);
                        _attr.co = grotteActions.first[t.ordinal()];
                    }
                }
            }
            grotteActions.AddPt();
        } while (grotteSupport.stat == _Stat.Playing);
        GameFinish();
    }


    // Life Cycle

    private void begin() {
        Memory.instance().begin();
        Clock.instance().begin();
        Checks.instance().begin();
        ANSITerm.instance().begin();
        Trigo.instance().begin();
        Files.instance().begin();
        Sounds.instance().begin();
        GrotteSounds.instance().begin();
        GrotteBonus.instance().begin();
        GrotteSupport.instance().begin();
        GrotteActions.instance().begin();

        /* *********/
        /**/
        /**/
        /* *********/
        InitVars();
        do {
            grotteSupport.ReadGame(new Runtime.FieldRef<>(grotteSupport::getLevel, grotteSupport::setLevel), new Runtime.FieldRef<>(grotteSupport::getGame, grotteSupport::setGame));
            grotteActions.InitGame(grotteSupport.level, grotteSupport.game);
            Play();
            if (grotteSupport.stat == _Stat.GameOver)
                grotteSupport.Gameover(grotteSupport.score);
        } while (grotteSupport.stat != _Stat.Break);
    }

    private void close() {
        GrotteActions.instance().close();
        GrotteSupport.instance().close();
        GrotteBonus.instance().close();
        GrotteSounds.instance().close();
        Sounds.instance().close();
        Files.instance().close();
        Trigo.instance().close();
        ANSITerm.instance().close();
        Checks.instance().close();
        Clock.instance().close();
        Memory.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        Grotte instance = new Grotte();
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
