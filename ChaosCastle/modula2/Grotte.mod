MODULE Grotte;
  (*$ CStrings:= FALSE *)
 FROM SYSTEM IMPORT ADR, ADDRESS, SHIFT;
 FROM Memory IMPORT SET16, CARD8, INT8, CARD16, INT16, CARD32, INT32;
 FROM GrotteSupport IMPORT OBJECT, Random, WL, BigInit, BigFlush,
  time, Gameover, ReadGame, made, ReadDir, InitK2Dirs, drK2, drsK2,
  BonusLevel, DIRECTION, DisplayInstructions, level, game, score, ppos,
  l2count, blvcount, pvie, pcount, TypeOf, SetColor, attr, ndoor,
  LoadGame, ShowClock, WriteCard, followscore, stat, Playing, GameOver,
  Finish, Break, oldpvie, oldblv, oldl2;
 FROM GrotteActions IMPORT N, G, H, D, B, ND, SQ, MAXOBJ, ObjPtr, Object,
  Delta, Line, MoveProc, Param, MoveAttr, Grtt, BSetArr10, scoreshown, wt,
  deltav, deltah, oldx, oldy, px, py, firedir, mvie, sx, sy, ax, ay,
  rfx, rfy, l2l, l2r, k3c, wtc, at, nlj, nlk, reps, clkt, ch, ch1, ch2, repch,
  lastch, addpt, decpt, oldscore, Move, speed, aiept, dispt, first, object, els,
  e, gn, SGN, Snd, WriteTo, UnLink, Link, SetAttr, New, Create, Dispose, Remove,
  Mur, SetPxPy, MoveProx, ShowLives, ShowLevel, ShowScore, ShowMessage, ShowL2,
  AddPt, WaitKey, SortK3, InitGame, Delay, Aie, Boum, AddL2, DecVie, Fire, SubL2,
  AddBLV, Gn2, MakeGn2, AiePlayer, MoveObj, Confirm, Fixed, GetK2Dir, FireBall,
  Dead, CreateObj, MovePlayer, MoveK0;
 FROM ANSITerm IMPORT Read, ReadAgain, ReadString, Report, Write, WriteString,
  WriteLn, WriteAt, MoveChar, Ghost, Goto, ClearScreen, Color,
  ClearLine, WaitChar;
 FROM Clock IMPORT TimePtr, noTime, StartTime, WaitTime, GetTime;


(********** MoveProcs **********)

 PROCEDURE MoveK1(k: ObjPtr);
 BEGIN
  WITH object[k] DO
   IF (x = 0) OR (y = 0) OR (x = 71) OR (y = 19) THEN
    Remove(k); RETURN
   END;
   IF seq >= 0 THEN
    IF Random() MOD 16 > 4 THEN
     SetPxPy(x, y);
     IF Random() MOD 2 = 0 THEN
      IF px < x THEN d:= G ELSE d:= D END
     ELSE
      IF py < y THEN d:= H ELSE d:= B END
     END
    ELSE
     d:= Random() MOD 5
    END;
    DEC(seq, Random() MOD 16)
   ELSE
    INC(seq)
   END;
   oldx:= x; oldy:= y;
   INC(x, deltah[d]); INC(y, deltav[d]);
   ch:= Report(x, y);
   IF ch = "*" THEN
    AiePlayer(x, y, "+", k); RETURN
   ELSIF (ch <> " ") AND (ch <> ".") THEN
    x:= oldx; y:= oldy; seq:= 0
   ELSE
    MoveObj(k, 1, x, y, "+")
   END
  END
 END MoveK1;

 PROCEDURE MoveK2(k: ObjPtr);
  VAR rd: DIRECTION;
 BEGIN
  WITH object[k] DO
   IF (x = 0) OR (y = 0) OR (x = 71) OR (y = 19) THEN
    Remove(k); RETURN
   END;
   IF seq >= 0 THEN
    SetPxPy(x, y);
    IF y <= py THEN by:= 2 ELSE by:= 0 END;
    IF x > px THEN INC(by) END;
   ELSE
    INC(seq)
   END;
   rd:= GetK2Dir(x, y, by);
   ch:= Report(x, y + 1);
   IF (rd = NUL) OR (rd = JUMP) THEN
    IF dir <> NUL THEN dir:= NUL ELSE
     IF (ch <> " ") AND (ch <> "H") AND (ch <> ".") THEN
      IF by >= 2 THEN
       seq:= Random() MOD 16; seq:= -seq;
       IF Random() MOD 2 = 0 THEN by:= 7 - by ELSE by:= by MOD 2 END
      ELSE
       jump:= 5
      END
     END
    END
   ELSE
    dir:= rd
   END;
   IF px = x THEN dir:= NUL END;
   IF (by < 2) AND (ch <> " ") AND (ch <> "H") THEN jump:= 5 END;
   oldx:= x; oldy:= y;
   IF dir = LEFT THEN DEC(x) ELSIF dir = RIGHT THEN INC(x) END;
   ch:= Report(x, y);
   IF ch = "*" THEN
    AiePlayer(x, y, "X", k); RETURN
   ELSIF ch <> " " THEN
    x:= oldx
   END;
   IF jump > 1 THEN
    DEC(jump); DEC(y)
   ELSIF jump = 1 THEN
    jump:= 0
   ELSE
    INC(y)
   END;
   ch:= Report(x, y);
   IF ch = "*" THEN
    AiePlayer(x, y, "X", k); RETURN
   ELSIF (ch = "H") OR (ch = "&") THEN
    IF jump > 0 THEN
     y:= oldy; jump:= 0
    ELSE
     Color(1); x:= oldx; y:= oldy; Boum(x, y);
     Remove(k); RETURN
    END
   ELSIF (ch <> " ") AND (ch <> ".") THEN
    IF jump > 1 THEN
      jump:= 1
    END;
    y:= oldy
   END;
   MoveObj(k, 1, x, y, "X")
  END
 END MoveK2;

 PROCEDURE MoveK3(k: ObjPtr);
 BEGIN
  IF first[K3] = k THEN k3c:= 0 END;
  IF k3c > 8 THEN attr[K3].co:= k; RETURN END;
  WITH object[k] DO
   IF 3 IN flags THEN EXCL(flags, 3); RETURN END;
   oldx:= x; oldy:= y;
   SetPxPy(x, y);
   IF px < x THEN DEC(x) ELSIF px > x THEN INC(x) END;
   ch:= Report(x, y);
   IF (ch <> " ") AND (ch <> "*") THEN x:= oldx END;
   IF py < y THEN DEC(y) ELSIF py > y THEN INC(y) END;
   ch:= Report(x, y);
   IF ch = "*" THEN
    AiePlayer(x, y, "#", k); RETURN
   ELSIF ch <> " " THEN
    y:= oldy
   END;
   MoveObj(k, 1, x, y, "#");
   IF (oldx <> x) OR (oldy <> y) THEN INC(k3c) END;
   IF next <> 0 THEN (* Sorting *)
    IF ABS(x - px) + ABS(y - py) >
          ABS(object[next].x - px) + ABS(object[next].y - py) THEN
     SortK3(k)
    END
   END
  END
 END MoveK3;

 PROCEDURE MoveK4(k: ObjPtr);
 BEGIN
  WITH object[k] DO
   IF (x = 0) OR (y = 0) OR (x = 71) OR (y = 19) THEN
    Remove(k); RETURN
   END;
   oldx:= x; oldy:= y;
   IF dir = NUL THEN dir:= LEFT END;
   IF (seq <> 2) AND (dir <> JUMP) THEN jump:= 5; dir:= JUMP END;
   IF dir = LEFT THEN DEC(x) ELSIF dir = RIGHT THEN INC(x) END;
   ch:= Report(x, y);
   IF ch = "*" THEN
    x:= oldx; dir:= JUMP; jump:= 5; seq:= 0
   ELSIF (ch <> ".") AND (ch <> " ") THEN
    x:= oldx;
    IF Random() MOD 4 = 0 THEN
     IF Report(x, y + 1) <> " " THEN jump:= 5 END
    END;
    IF (Random() MOD 8 = 0) OR (jump = 0) THEN
     IF dir = LEFT THEN dir:= RIGHT ELSIF dir = RIGHT THEN dir:= LEFT END
    END
   END;
   IF jump > 1 THEN
    DEC(jump); DEC(y)
   ELSIF jump = 1 THEN
    IF dir = JUMP THEN
     FireBall(x - 1, y, 6, LEFT);
     FireBall(x + 1, y, 6, RIGHT);
     FireBall(x, y - 1, 7, NUL);
     Boum(x, y);
     x:= oldx; y:= oldy; Remove(k); RETURN
    ELSE
     jump:= 0
    END
   ELSE
    INC(y)
   END;
   ch:= Report(x, y);
   IF (ch = "*") OR (ch = "H") THEN
    y:= oldy; x:= oldx;
    IF dir <> JUMP THEN dir:= JUMP; jump:= 5; seq:= 0 END
   ELSIF (ch <> ".") AND (ch <> " ") THEN
    y:= oldy
   END;
   MoveObj(k, 1, x, y, "x")
  END
 END MoveK4;

 PROCEDURE MoveGn1(g: ObjPtr);
  VAR k0: ObjPtr;
 BEGIN
  WITH object[g] DO
   IF (y = 0) OR (y = 19) THEN HALT END;
   SetPxPy(x, y); oldx:= x; oldy:= y;
   IF ch1 <> "Y" THEN
    IF ODD(py) THEN INC(y) ELSE DEC(y) END
   END;
   IF ch1 = "<" THEN d:= G ELSIF ch1 = ">" THEN d:= D ELSE d:= B END;
   IF Report(x, y) = " " THEN
    MoveObj(g, 5, x, y, ch1)
   ELSE
    y:= oldy
   END;
   oldy:= y;
   k0:= first[K0];
   WHILE k0 <> 0 DO
    WITH object[k0] DO
     IF (seq <> 2) AND ((x = oldx) OR (y = oldy))
     AND ((ABS(x - oldx) < 6) OR (ABS(y - oldy) < 6)) THEN
      RETURN
     END;
     k0:= next
    END
   END;
   IF ODD(px) THEN Fire(L1, x, y, d, TRUE) END
  END
 END MoveGn1;

 PROCEDURE MoveGn2(g: ObjPtr);
 BEGIN
  WITH object[g] DO
   DEC(seq);
   IF seq <= 0 THEN
    Remove(g)
   ELSIF seq = 4 THEN
    Fire(L1, x, y, G, TRUE);
    Fire(L1, x, y, D, TRUE);
    Fire(L1, x, y, H, TRUE)
   END
  END
 END MoveGn2;

 PROCEDURE MoveAsc(a: ObjPtr);
 BEGIN
  WITH object[a] DO
   IF (y < 2) OR Mur(Report(x, y - 2)) THEN
    WriteAt(x, y, " ");
    y:= by;
    RETURN
   END;
   oldx:= x; oldy:= y;
   REPEAT
    DEC(oldy); ch:= Report(x, oldy)
   UNTIL Dead(ch);
   IF NOT(Mur(ch)) AND (ch <> "=") THEN
    INC(oldy);
    WHILE oldy <> y DO
     DEC(object[e[oldy]^[x]].y);
     e[oldy - 1]^[x]:= e[oldy]^[x];
     ch:= Report(x, oldy); SetColor(ch); WriteAt(x, oldy - 1, ch);
     INC(oldy)
    END
   ELSIF y <> by THEN
    oldy:= y - 1;
    INC(object[e[oldy]^[x]].y);
    e[y]^[x]:= e[oldy]^[x];
    ch:= Report(x, oldy); SetColor(ch); WriteAt(x, y, ch)
   END;
   oldy:= y; DEC(y); Color(4);
   IF Report(x, oldy) <> "=" THEN
    Color(4); WriteAt(x, y, "=");
    e[y]^[x]:= a
   ELSE
    MoveObj(a, 4, x, y, "=")
   END
  END
 END MoveAsc;

 PROCEDURE MoveL1(l: ObjPtr);
  VAR bl: ObjPtr;
      t1, t2: BOOLEAN;
 BEGIN
  WITH object[l] DO
   IF ODD(d) THEN ch1:= "-" ELSE ch1:= "|" END;
   IF ND IN flags THEN v:= 1 ELSE v:= 2 END;
   ch2:= Report(x, y); oldx:= x; oldy:= y;
   IF (ch2 = ch1) AND (e[y]^[x] = l) THEN ch2:= " " END;
   INC(x, deltah[d]); INC(y, deltav[d]);
   IF (x < 0) OR (y < 0) OR (x > 71) OR (y > 19) THEN
    x:= oldx; y:= oldy; Remove(l); RETURN
   END;
   ch:= Report(x, y);
   IF ch = "8" THEN ch:= "." END;
   IF ch = " " THEN
    IF ch2 = " " THEN
     MoveObj(l, v, x, y, ch1);
    ELSE
     WriteTo(oldx, oldy, ch2); Color(v); WriteAt(x, y, ch1)
    END
   ELSE
    IF ch2 = " " THEN
     Color(v); WriteAt(oldx, oldy, " ")
    ELSE
     WriteTo(oldx, oldy, ch2); Color(v)
    END;
    IF ch <> "*" THEN Ghost(x, y, ch1) END
   END;
   CASE TypeOf(ch) OF
     EMPTY:
    |BN, SBN:
     INC(seq);
     IF ch = "." THEN
      Snd(L1, BM, x, y);
      WriteAt(x, y, " "); Boum(x, y); Dispose(l); RETURN
     ELSE
      IF d > H THEN DEC(d, 2) ELSE INC(d, 2) END
     END
    |PLAYER:
     IF ND IN flags THEN
      Boum(x, y); Snd(L1, PLAYER, x, y);
      DecVie(e[y]^[x]); Remove(l); RETURN
     END
    |L1:
     bl:= e[y]^[x];
     t1:= (ND IN flags);
     t2:= (ND IN object[bl].flags);
     IF t1 <> t2 THEN INC(decpt) END;
     IF t1 OR t2 THEN
      Boum(x, y); Snd(L1, L1, x, y);
      Remove(bl); Dispose(l); RETURN
     END
    |L3:
     IF NOT(ND IN flags) THEN
      bl:= e[y]^[x];
      Boum(x, y); Snd(L1, L3, x, y);
      Remove(bl); Dispose(l); RETURN
     ELSE
      WriteTo(x, y, ch);
      Dispose(l); RETURN
     END
    |K0..K4, BALL, GN2, PIC:
     bl:= e[y]^[x]; WriteTo(x, y, ch);
     Aie(L1, object[bl].type, bl, 1);
     Boum(x, y); Dispose(l); RETURN
    |PLAT:
      WITH object[first[PLAT]] DO
       IF (x = ppos[0].x) AND (y = ppos[0].y) THEN seq:= 1 END
      END;
      Color(4); Boum(x, y);
      WriteTo(x, y, "T");
      Dispose(l); RETURN
   ELSE
    IF ch = "/" THEN d:= 5 - d
    ELSIF ch = "\" THEN IF ODD(d) THEN INC(d) ELSE DEC(d) END
    ELSIF (ch = ":") OR ((x = ax) AND (y = ay)) OR (ch = "!") THEN
     INC(seq);
     IF ODD(d) THEN
      IF d = G THEN d:= D ELSE d:= G END
     ELSE
      seq:= 0
     END
    ELSE
     seq:= 0
    END
   END;
   IF (ch = " ") OR (ch = ch1) THEN
    e[y]^[x]:= l
   END;
   IF seq >= 0 THEN
    Remove(l)
   END
  END
 END MoveL1;

 PROCEDURE MoveL2(l: ObjPtr);
  VAR bl: ObjPtr;
 BEGIN
  WITH object[l] DO
   IF d = G THEN ch1:= "(" ELSE ch1:= ")" END;
   ch2:= Report(x, y); oldx:= x; oldy:= y;
   IF (ch2 = ch1) AND (e[y]^[x] = l) THEN ch2:= " " END;
   INC(x, deltah[d]);
   IF (x < 0) OR (x > 71) THEN
    x:= oldx; Remove(l); RETURN
   END;
   ch:= Report(x, y);
   IF (ch = " ") OR (ch = ch1) THEN
    IF ch2 = " " THEN
     MoveObj(l, 2, x, y, ch1)
    ELSE
     e[y]^[x]:= l;
     WriteTo(oldx, y, ch2); Color(2); WriteAt(x, y, ch1)
    END
   ELSE
    IF ch2 = " " THEN
     Color(2); WriteAt(oldx, y, " ")
    ELSE
     WriteTo(oldx, y, ch2); Color(2)
    END;
    IF ch <> "*" THEN Ghost(x, y, ch1) END
   END;
   CASE TypeOf(ch) OF
     EMPTY, PLAYER, L2:
    |L1, L3:
     bl:= e[y]^[x];
     IF ND IN object[bl].flags THEN
      Boum(x, y); Snd(L1, L1, x, y); INC(addpt);
      Remove(bl); RETURN
     END
    |K0..K4, BALL, GN1, GN2, PIC, NID, BUB:
     bl:= e[y]^[x]; WriteTo(x, y, ch);
     v:= vie;
     IF object[bl].vie < v THEN v:= object[bl].vie END;
     Aie(L1, object[bl].type, bl, v);
     Boum(x, y); DEC(vie, v);
     IF vie = 0 THEN Dispose(l); RETURN END;
     IF Report(x, y) = " " THEN WriteAt(x, y, ch1); e[y]^[x]:= l END
   ELSE
    IF ch = "." THEN WriteAt(x, y, ch1); e[y]^[x]:= l
    ELSIF (ch = "A") OR (TypeOf(ch) = SBN) OR (ch = "%")
       OR (ch = "!") OR (ch = ":") THEN
     INC(seq, 5);
     IF d = G THEN d:= D; DEC(l2l); INC(l2r) ELSE d:= G; DEC(l2r); INC(l2l) END
    ELSE
     WriteTo(x, y, ch); Boum(oldx, y);
     Dispose(l); RETURN
    END
   END;
   INC(seq);
   IF seq >= 0 THEN Remove(l); RETURN END;
  END
 END MoveL2;

 PROCEDURE MoveL3(l: ObjPtr);
 BEGIN
  WITH object[l] DO
   IF d = G THEN ch1:= "{" ELSE ch1:= "}" END;
   ch2:= Report(x, y); oldx:= x; oldy:= y;
   IF (ch2 = ch1) AND (e[y]^[x] = l) THEN ch2:= " " END;
   INC(x, deltah[d]);
   IF (x < 0) OR (x > 71) THEN
    x:= oldx; Remove(l); RETURN
   END;
   ch:= Report(x, y);
   IF ch <> " " THEN
    x:= oldx; oldy:= y; Dispose(l);
    IF ch2 = " " THEN CreateObj(x, y) END
   ELSE
    IF ch2 = " " THEN
     MoveObj(l, 1, x, y, ch1)
    ELSE
     WriteTo(oldx, oldy, ch2);
     e[y]^[x]:= l;
     Color(1); WriteAt(x, y, ch1)
    END
   END
  END
 END MoveL3;

 PROCEDURE MoveBall(b: ObjPtr);
  VAR bo: ObjPtr;
      z: INT8;
      t: OBJECT;
 BEGIN
  WITH object[b] DO
   IF (x = 0) OR (y = 0) OR (x = 71) OR (y = 19) THEN
    Remove(b); RETURN
   END;
   Color(6);
   oldx:= x; oldy:= y;
   IF jump > 1 THEN
    DEC(y); DEC(jump)
   ELSIF jump = 1 THEN
    DEC(jump)
   ELSE
    INC(y)
   END;
   ch:= Report(x, y);
   IF ch = "*" THEN
    AiePlayer(x, y, "O", b); RETURN
   ELSIF (ch = "+") OR (ch = "X") THEN
    bo:= e[y]^[x]; Boum(x, y);
    Aie(BALL, object[bo].type, bo, 1);
    x:= oldx; y:= oldy;
    Remove(b); RETURN
   ELSIF (ch <> ".") AND (ch <> " ") AND (ch <> "O") THEN
    x:= oldx; y:= oldy; Boum(x, y); Remove(b); RETURN
   END;
   IF dir = LEFT THEN DEC(x) ELSIF dir = RIGHT THEN INC(x) END;
   ch:= Report(x, y); t:= TypeOf(ch);
   IF (t <> EMPTY) AND (t <> PLAYER) AND (t <> SBN) AND (t <> K1) AND
      (t <> K2) AND (t <> BALL) THEN
    IF dir = LEFT THEN dir:= RIGHT ELSE dir:= LEFT END;
    x:= oldx;
   ELSIF ch = "*" THEN
    AiePlayer(x, y, "O", b); RETURN
   END;
   IF dir <> NUL THEN
    z:= x - oldx + x; t:= TypeOf(Report(z, y));
    IF (t <> EMPTY) AND (t <> PLAYER) AND (t <> SBN) AND (t <> K1) AND (t <> K2) AND (t <> BALL) THEN
     IF dir = LEFT THEN dir:= RIGHT ELSE dir:= LEFT END
    END
   END;
   z:= y - oldy + y; t:= TypeOf(Report(x, z));
   IF (t <> EMPTY) AND (t <> PLAYER) AND (t <> SBN) AND (t <> K1) AND (t <> K2) AND (t <> BALL) THEN
    IF jump > 0 THEN
     DEC(by, jump - 1); jump:= 1
    ELSE
     jump:= by;
     IF by > 0 THEN DEC(by) END
    END
   END;
   MoveObj(b, 6, x, y, "O")
  END
 END MoveBall;

 PROCEDURE MovePic(p: ObjPtr);
  VAR bo: ObjPtr;
      i: INT8;
 BEGIN
  WITH object[p] DO
   IF y = 19 THEN Remove(p); RETURN END;
   IF seq = 2 THEN
    SetPxPy(x, y); d:= N;
    i:= Random() MOD 8;
    IF (py > y) AND (ABS(x - px) <= i)
    AND (Random() MOD 4 = 0) THEN
     seq:= 1
    END
   ELSE
    IF d <> B THEN d:= B; RETURN END;
    oldx:= x; oldy:= y; INC(y);
    ch:= Report(x, y);
    IF ch = "8" THEN ch:= "." END;
    IF (ch = ">") OR (ch = "\") THEN
     ch1:= Report(x + 1, y);
     IF ch1 = " " THEN INC(x); ch:= ch1 END
    ELSIF (ch = "<") OR (ch = "/") THEN
     ch1:= Report(x - 1, y);
     IF ch1 = " " THEN DEC(x); ch:= ch1 END
    END;
    Color(5);
    CASE TypeOf(ch) OF
      PLAYER:
       AiePlayer(x, y, "V", p); RETURN
     |EMPTY:
     |K0..K4, L1, L2, BALL, GN2:
      bo:= e[y]^[x];
      Aie(PIC, object[bo].type, bo, object[bo].vie);
      Boum(x, y); y:= oldy
    ELSE
     IF ch = "." THEN Snd(PIC, BM, x, y); Boum(x, y) ELSE
      y:= oldy; x:= oldx;
      Boum(x, y); Remove(p);
      RETURN
     END
    END;
    MoveObj(p, 5, x, y, "V")
   END
  END
 END MovePic;

 PROCEDURE MovePlat(p: ObjPtr);
  VAR dx, dy: INT8;
      cc: CARD8;
      pl: ObjPtr;
      c1: CHAR;
      t: OBJECT;
 BEGIN
  WITH object[p] DO
   bx:= ppos[seq].x; by:= ppos[seq].y;
   IF (x = bx) AND (y = by) THEN
    IF Report(x, y) = " " THEN
     Color(4); WriteAt(x, y, "T"); e[y]^[x]:= p
    END;
    IF seq = 0 THEN RETURN END;
    INC(seq); cc:= seq; IF cc > pcount THEN seq:= 0 END;
    bx:= ppos[seq].x; by:= ppos[seq].y
   END;
   c1:= Report(x, y);
   SetPxPy(x, y);
   pl:= e[y - 1]^[x];
   oldx:= x; oldy:= y;
   IF (px = oldx) AND (py < oldy - 1) THEN RETURN END;
   IF x <> bx THEN
    IF x < bx THEN INC(x) ELSE DEC(x) END
   ELSE
    IF y < by THEN INC(y) ELSE DEC(y) END
   END;
   IF (x < 0) OR (y < 0) OR (x > 71) OR (y > 19) THEN HALT END;
   IF 0 IN flags THEN
    EXCL(flags, 0); INCL(flags, 1)
   ELSE
    EXCL(flags, 1)
   END;
   t:= TypeOf(Report(x, y));
   IF (t = BM) OR (t = MR) OR (t = SBN) OR (t = NID) OR (t = BUB) OR (t = PIC) THEN
    INCL(flags, 0)
   END;
   IF 1 IN flags THEN
    Color(4);
    IF 0 IN flags THEN
     Ghost(x, y, "T")
    ELSE
     e[y]^[x]:= p; WriteAt(x, y, "T")
    END;
    WriteTo(oldx, oldy, c1)
   ELSE
    IF 0 IN flags THEN
     Color(4); Ghost(x, y, "T");
     IF e[oldy]^[oldx] = p THEN WriteAt(oldx, oldy, " ") END
    ELSE
     MoveObj(p, 4, x, y, "T")
    END
   END;
   Snd(PLAYER, GN2, x, y);
   c1:= Report(x, y - 1);
   dx:= x - oldx; dy:= y - oldy;
   t:= TypeOf(c1);
   IF (px = oldx) AND (py = oldy - 1) AND
      ((t = EMPTY) OR (t = BN) OR (t = SBN) OR (t = L1) OR (t = L2))
   THEN
    WITH object[pl] DO
     IF (dy <> -1) THEN WriteAt(x, y, " ") END;
     INC(x, dx); INC(y, dy);
     e[y]^[x]:= pl; Color(2);
     IF c1 = " " THEN WriteAt(x, y, "*") ELSE Ghost(x, y, "*") END
    END
   END
  END
 END MovePlat;

 PROCEDURE MoveWoof(p: ObjPtr);
  VAR
   pl: ObjPtr;
   ch: CHAR;
   t: OBJECT;
 BEGIN
  WITH object[p] DO
   SetPxPy(x, y);
   IF (px = x) AND (py < y - 1) THEN RETURN END;
   oldx:= x; oldy:= y;
   IF seq = 0 THEN INC(x) ELSE DEC(x) END;
   IF Report(x, y) = " " THEN
    MoveObj(p, 4, x, y, "W");
    Snd(PLAYER, GN2, x, y);
    IF (px = oldx) AND (py = y - 1) THEN
     pl:= e[py]^[px];
     ch:= Report(x, py);
     t:= TypeOf(ch);
     IF (t = EMPTY) OR (t = BN) OR (t = SBN) OR (t = L1) OR (t = L2) THEN
      WITH object[pl] DO
       WriteAt(x, y, " ");
       x:= object[p].x;
       e[y]^[x]:= pl; Color(2);
       IF ch = " " THEN WriteAt(x, y, "*") ELSE Ghost(x, y, "*") END
      END
     END
    END
   ELSE
    x:= oldx;
    IF seq = 0 THEN seq:= 1 ELSE seq:= 0 END
   END
  END
 END MoveWoof;

 PROCEDURE MoveTPlat(p: ObjPtr);
  VAR ch: CHAR;
 BEGIN
  WITH object[p] DO
   SetPxPy(x, y);
   IF (py = y) AND (ABS(x - px) < 6) THEN
    ch:= Report(x, y);
    IF (ch = " ") OR (ch = "U") THEN
     seq:= 15; Color(4); WriteAt(x, y, "U"); e[y]^[x]:= p
    END
   END;
   IF seq > 0 THEN
    DEC(seq);
    IF seq = 0 THEN WriteAt(x, y, " ") END
   END
  END
 END MoveTPlat;

 PROCEDURE Nid(n: ObjPtr);
  VAR
   c: CARD16;
 BEGIN
  WITH object[n] DO
   SetPxPy(x, y);
   IF seq < 0 THEN INC(seq); RETURN END;
   IF mvie < 4 THEN RETURN END;
   IF bx = G THEN bx:= D ELSE bx:= G END;
   IF Report(x + deltah[bx], y) = " " THEN
    Fire(L3, x, y, bx, TRUE)
   END;
   Color(5); WriteAt(x, y, "Z"); e[y]^[x]:= n;
   IF mvie > 30 THEN by:= 120 ELSE by:= mvie * 4 END;
   c:= 124 - by; seq:= Random() MOD c; seq:= -seq
  END
 END Nid;

 PROCEDURE Bub(b: ObjPtr);
  VAR
   c: CARD16;
 BEGIN
  WITH object[b] DO
   SetPxPy(x, y);
   IF seq < 0 THEN INC(seq); RETURN END;
   Color(5); WriteAt(x, y, "N"); e[y]^[x]:= b;
   FireBall(x - 1, y, 6, LEFT);
   FireBall(x + 1, y, 6, RIGHT);
   FireBall(x, y + 1, 6, NUL);
   IF mvie > 20 THEN by:= 120 ELSE by:= mvie * 4 END;
   c:= 124 - by; seq:= Random() MOD c; seq:= -seq - 3
  END
 END Bub;

 PROCEDURE ChuteBonus(b: ObjPtr);
  VAR bo: ObjPtr;
 BEGIN
  WITH object[b] DO
   oldx:= x; oldy:= y;
   INC(y); IF y > 19 THEN HALT END;
   IF Report(x, y) = "/" THEN DEC(x)
   ELSIF Report(x, y) = "\" THEN INC(x)
   END;
   Color(2);
   ch:= Report(x, y);
   IF ch = "." THEN
    Snd(K1, BN, x, y);
    INC(addpt, 10)
   ELSIF (ch = " ") OR (ch = "*") OR (ch = "8") THEN
   ELSIF Dead(ch) OR (ch = "T") OR (ch = "W") THEN
    y:= oldy; x:= oldx;
    INC(addpt, 10);
    Boum(x, y); Remove(b);
    RETURN
   ELSE
    Snd(K0, BN, x, y);
    bo:= e[y]^[x];
    Boum(x, y);
    WITH object[bo] DO
     seq:= 0;
     Aie(BN, type, bo, vie);
     INC(addpt, dispt[type] * 10)
    END
   END;
   MoveObj(b, 2, x, y, "%")
  END
 END ChuteBonus;

 PROCEDURE CheckDl(dl: ObjPtr);
  VAR ch: CHAR;
 BEGIN
  WITH object[dl] DO
   SetPxPy(x, y);
   IF (py = y) AND (ABS(x - px) < 6) THEN
    ch:= Report(x, y);
    IF ch = " " THEN
     Remove(dl);
     Color(2); WriteAt(x, y, "$");
     Boum(x, y)
    END
   END
  END
 END CheckDl;

 PROCEDURE EraseBoum(b: ObjPtr);
  VAR
   c1, c2: INT8;
 BEGIN
  WITH object[b] DO
   FOR c1:= -1 TO 1 DO
    FOR c2:= -1 TO 1 DO
     IF (c1 <> 0) OR (c2 <> 0) THEN
      WriteTo(x + c2, y + c1, Report(x + c2, y + c1))
     END
    END
   END
  END;
  Dispose(b)
 END EraseBoum;

 PROCEDURE EmptyMove(o: ObjPtr);
 BEGIN
 END EmptyMove;

 PROCEDURE InitVars;
  VAR t: OBJECT;
      c, x: CARD16;
 BEGIN
  deltav[0]:= 0; deltah[0]:= 0;
  deltav[1]:= 0; deltah[1]:= -1;
  deltav[2]:= -1; deltah[2]:= 0;
  deltav[3]:= 0; deltah[3]:= 1;
  deltav[4]:= 1; deltah[4]:= 0;
  FOR t:= MIN(OBJECT) TO MAX(OBJECT) DO
   Move[t]:= EmptyMove; speed[t]:= 255;
   aiept[t]:= 0; dispt[t]:= 0
  END;
  Move[PLAYER]:= MovePlayer; speed[PLAYER]:= 15;
  Move[K0]:= MoveK0; speed[K0]:= 31; dispt[K0]:= 50;
  Move[K1]:= MoveK1; speed[K1]:= 37; dispt[K1]:= 10; aiept[K1]:= 3;
  Move[K2]:= MoveK2; speed[K2]:= 34; dispt[K2]:= 2; aiept[K2]:= 2;
  Move[K3]:= MoveK3; speed[K3]:= 47; dispt[K3]:= 3; aiept[K3]:= 1;
  Move[K4]:= MoveK4; speed[K4]:= 29; dispt[K4]:= 20;
  Move[GN1]:= MoveGn1; speed[GN1]:= 231; dispt[GN1]:= 13;
  Move[GN2]:= MoveGn2; speed[GN2]:= 15; dispt[GN2]:= 4;
  Move[ASC]:= MoveAsc; speed[ASC]:= 40;
  Move[L1]:= MoveL1; speed[L1]:= 8;
  Move[L2]:= MoveL2; speed[L2]:= 12;
  Move[L3]:= MoveL3; speed[L3]:= 13;
  Move[BALL]:= MoveBall; speed[BALL]:= 17; dispt[BALL]:= 11; aiept[BALL]:= 1;
  Move[PIC]:= MovePic; speed[PIC]:= 19; dispt[PIC]:= 9;
  Move[PLAT]:= MovePlat; speed[PLAT]:= 30;
  Move[WOOF]:= MoveWoof; speed[WOOF]:= 30;
  Move[TPLAT]:= MoveTPlat; speed[TPLAT]:= 15;
  Move[NID]:= Nid; speed[NID]:= 64; dispt[NID]:= 40; aiept[NID]:= 7;
  Move[BUB]:= Bub; speed[BUB]:= 64; dispt[BUB]:= 30; aiept[BUB]:= 5;
  Move[BN]:= ChuteBonus; speed[BN]:= 17; dispt[BN]:= 50;
  Move[DL]:= CheckDl; speed[DL]:= 15;
  Move[BM]:= EraseBoum; speed[BM]:= 5;
  followscore:= 1000;
  pvie:= 5;
  repch:= 0C; reps:= 0;
  FOR c:= 0 TO 19 DO
   e[c]:= ADR(els[c]);
   FOR x:= 0 TO 71 DO
    e[c]^[x]:= 0
   END;
   drK2[c]:= ADR(drsK2[c])
  END;
  at:= 0; nlj:= 0; nlk:= 0; addpt:= 0; decpt:= 0;
  oldscore:= 0; clkt:= 0; rfx:= 0; rfy:= 0
 END InitVars;

 PROCEDURE GameFinish;
 BEGIN
  IF stat <> Finish THEN RETURN END;
  Color(7); Goto(0, 21);
  WriteString("YOU MADE IT !!!");
  rfx:= 0; rfy:= 20;
  StartTime(time);
  Delay(1536);
  REPEAT Read(ch) UNTIL ch <= 3C;
  WHILE addpt > 0 DO
   Read(ch);
   IF ch <> 0C THEN INC(score, addpt - 5); addpt:= 5 END;
   AddPt;
  END;
  ShowScore; WriteString("    ");
(*  Flush; *)
  IF WaitTime(time, 800) THEN END
 END GameFinish;

 PROCEDURE Play;
  VAR t: OBJECT;
      nc: CARD8;
      mo: ObjPtr;
 BEGIN
  wtc:= 0;
  REPEAT
   IF first[PLAYER] = 0 THEN HALT END;
   FOR t:= MIN(OBJECT) TO MAX(OBJECT) DO
    WITH attr[t] DO
     IF nb = 0 THEN timeout:= speed[t]; ct:= timeout END;
     IF ct <= 1 THEN
      FOR nc:= 1 TO nt DO
       IF co <> 0 THEN
        mo:= co;
        co:= object[co].next;
        Move[t](mo)
       END
      END;
      ct:= dt
     ELSE
      DEC(ct)
     END;
     DEC(timeout);
     IF timeout = 0 THEN
      timeout:= speed[t];
      SetAttr(t);
      co:= first[t]
     END
    END
   END;
   AddPt
  UNTIL stat <> Playing;
  GameFinish;
 END Play;


(***********)
(**)BEGIN(**)
(***********)

 InitVars;
 REPEAT
  ReadGame(level, game);
  InitGame(level, game);
  Play;
  IF stat = GameOver THEN Gameover(score) END;
 UNTIL stat = Break;

END Grotte.
