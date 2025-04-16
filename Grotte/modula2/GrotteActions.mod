IMPLEMENTATION MODULE GrotteActions;
  (*$ CStrings:= FALSE *)
 FROM SYSTEM IMPORT ADR, ADDRESS, SHIFT, VAL;
 FROM Memory IMPORT SET16, CARD8, INT8, CARD16, INT16, CARD32, INT32;
 FROM Trigo IMPORT SQRT;
 FROM GrotteSupport IMPORT OBJECT, Random, WL, BigInit, BigFlush,
  time, Gameover, ReadGame, made, ReadDir, InitK2Dirs, drK2, drsK2,
  BonusLevel, DIRECTION, DisplayInstructions, level, game, score, ppos,
  l2count, blvcount, pvie, pcount, TypeOf, SetColor, attr, ndoor,
  LoadGame, ShowClock, WriteCard, followscore, stat, Playing, GameOver,
  Finish, Break, oldpvie, oldblv, oldl2;
 FROM GrotteSounds IMPORT Sound;
 FROM ANSITerm IMPORT Read, ReadAgain, ReadString, Report, Write, WriteString,
  WriteLn, WriteAt, MoveChar, Ghost, Goto, ClearScreen, Color,
  ClearLine, WaitChar;
 FROM Clock IMPORT TimePtr, noTime, StartTime, WaitTime, GetTime;


 PROCEDURE SGN(v: INT8): INT8;
 BEGIN
  IF v > 0 THEN RETURN 1 ELSIF v < 0 THEN RETURN -1 ELSE RETURN 0 END
 END SGN;

(******** Subroutines ********)

 PROCEDURE Snd(t1, t2: OBJECT; zx, zy: INT8);
  VAR
   dx, dy, dl, stereo, balance: INT16;
   volume: CARD16;
 BEGIN
  WITH object[first[PLAYER]] DO
   dx:= (zx - x); dy:= (y - zy) * 2
  END;
  dl:= SQRT(dx * dx + dy * dy);
  IF dl >= 60 THEN
   RETURN
  ELSE
   volume:= (63 - dl);
   volume:= volume * volume DIV 16
  END;
  IF dl <> 0 THEN
   stereo:= dx * 90 / dl;
   balance:= stereo * 6 / (dl + 6);
   IF dy < 0 THEN
    IF stereo > 0 THEN stereo:= 180 - stereo ELSE stereo:= -180 - stereo END
   END
  ELSE
   stereo:= 0; balance:= 0
  END;
  Sound(t1, t2, volume, stereo, balance)
 END Snd;

 PROCEDURE WriteTo(x, y: INT8; ch: CHAR);
 BEGIN
  SetColor(ch);
  Ghost(x, y, ch)
 END WriteTo;

 PROCEDURE UnLink(o: ObjPtr);
 BEGIN
  IF o = 0 THEN HALT END;
  WITH object[o] DO
   DEC(attr[type].nb);
   IF first[type] = o THEN
    first[type]:= next;
    object[next].prev:= 0
   ELSE
    object[prev].next:= next;
    object[next].prev:= prev
   END
  END
 END UnLink;

 PROCEDURE Link(o: ObjPtr; t: OBJECT);
 BEGIN
  IF o = 0 THEN HALT END;
  WITH object[o] DO
   type:= t;
   INC(attr[type].nb);
   next:= first[type];
   IF next <> 0 THEN object[next].prev:= o END;
   prev:= 0;
   first[type]:= o
  END
 END Link;

 PROCEDURE SetAttr(t: OBJECT);
 BEGIN
  WITH attr[t] DO
   ct:= 0; (* LOOP dt --> 0 END *)
   IF t = ASC THEN nt:= nb; dt:= timeout; RETURN END;
   IF nb = 0 THEN
    nt:= 0; dt:= 1
   ELSE
    IF nb > timeout THEN
     dt:= 1;
     IF timeout = 0 THEN
      nt:= nb
     ELSE
      nt:= (nb + timeout - 1) DIV timeout
     END
    ELSE
     dt:= timeout DIV nb;
     nt:= 1
    END
   END
  END
 END SetAttr;

 PROCEDURE New(t: OBJECT): ObjPtr;
  VAR o: ObjPtr;
 BEGIN
  o:= first[EMPTY];
  IF o = 0 THEN RETURN 0 END;
  UnLink(o);
  Link(o, t);
  WITH object[o] DO
   seq:= 0; dir:= NUL;
   jump:= 0; flags:= SET16{}
  END;
  RETURN o
 END New;

 PROCEDURE Create(t: OBJECT; nx, ny, nvie, nseq: INT8; ch: CHAR);
  VAR new: ObjPtr;
 BEGIN
  IF t = GN2 THEN
   IF ny >= 16 THEN INCL(gn[nx].hset, ny - 16) ELSE INCL(gn[nx].lset, ny) END;
   RETURN
  END;
  new:= New(t);
  IF new = 0 THEN RETURN END;
  Snd(EMPTY, t, nx, ny);
  WITH object[new] DO
   x:= nx; y:= ny;
   vie:= nvie; seq:= nseq;
   ch1:= ch;
   SetColor(ch);
   WriteAt(x, y, ch);
   e[y]^[x]:= new
  END
 END Create;

 PROCEDURE Dispose(o: ObjPtr);
 BEGIN
  IF o = 0 THEN RETURN END;
  WITH object[o] DO
   IF type = EMPTY THEN RETURN END;
   IF type = L1 THEN
    IF ND IN flags THEN DEC(nlk) ELSE DEC(nlj) END
   ELSIF type = L2 THEN
    IF ch1 = "(" THEN DEC(l2l) ELSE DEC(l2r) END
   END;
   IF attr[type].co = o THEN attr[type].co:= next END;
   Snd(type, EMPTY, x, y);
   UnLink(o);
   Link(o, EMPTY);
  END
 END Dispose;

 PROCEDURE Remove(o: ObjPtr);
 BEGIN
  IF o = 0 THEN RETURN END;
  WITH object[o] DO
   IF e[y]^[x] = o THEN WriteAt(x, y, " ") ELSE WriteTo(x, y, Report(x, y)) END
  END;
  Dispose(o)
 END Remove;

 PROCEDURE Mur(ch: CHAR): BOOLEAN;
  VAR t: OBJECT;
 BEGIN
  t:= TypeOf(ch);
  RETURN (t = BM) OR (t = MR)
 END Mur;

 PROCEDURE SetPxPy(nx, ny: INT8);
  VAR o: ObjPtr;
      cv, bv: INT8;
  (* first PLAYER: real player, second: link player *)
  (* pvie = vie de first[PLAYER] *)
 BEGIN
  bv:= MAX(INT8);
  o:= first[PLAYER];
  REPEAT
   WITH object[o] DO
    cv:= ABS(nx - x) + ABS(ny - y); INC(cv, Random() MOD 2);
    IF cv <= bv THEN
     px:= x; py:= y;
     mvie:= vie; bv:= cv
    END;
    o:= next
   END
  UNTIL o = 0
 END SetPxPy;

 PROCEDURE MoveProx;
  VAR t: OBJECT;
      k, nxt: ObjPtr;
      td, dist: INT8;
 BEGIN
  SetPxPy(sx, sy);
  t:= TypeOf(Report(px, py));
  IF (t <> EMPTY) AND (t <> PLAYER) AND (t <> MR) AND (t <> BM) THEN
   IF object[e[py]^[px]].type <> PLAYER THEN Remove(e[py]^[px]) END
  END;
  FOR t:= K1 TO K4 DO
   k:= first[t];
   WHILE k <> 0 DO
    WITH object[k] DO
     nxt:= next;
     td:= ABS(px - x); dist:= ABS(py - y);
     IF ABS(dist - td) < 2 THEN INC(dist, ABS(dist - td)) END;
     IF td > dist THEN dist:= td END;
     IF dist < 4 THEN
      Remove(k)
     ELSIF dist < 8 THEN
      vie:= 1
     END
    END;
    k:= nxt
   END
  END
 END MoveProx;

 PROCEDURE ShowLives;
 BEGIN
  pvie:= object[first[PLAYER]].vie;
  Goto(42, 20); Color(3);
  WriteCard(pvie);
  IF pvie = 9 THEN Write(" ") END
 END ShowLives;

 PROCEDURE ShowLevel;
 BEGIN
  Goto(74, 20); Color(7);
  IF level = 10 THEN
   Write("B"); Write("N")
  ELSE
   IF level = 9 THEN Write("0") ELSE Write(CHR(level + 49)) END;
   Write(CHR(game + 65))
  END
 END ShowLevel;

 PROCEDURE ShowScore;
 BEGIN
  scoreshown:= TRUE;
  Goto(7, 20); Color(7);
  WriteCard(score); Write(" ")
 END ShowScore;

 PROCEDURE ShowMessage;
 BEGIN
  ShowLevel;
  Color(7);
  Goto(0, 20);
  WriteString("Score:");
  ShowScore;
  Goto(35, 20);
  WriteString("Lives:");
  ShowLives;
  ShowClock
 END ShowMessage;

 PROCEDURE ShowL2;
  VAR
   c1: CARD8;
 BEGIN
  Color(7);
  Goto(72, 0);  WriteString("+--+");
  Goto(72, 19); WriteString("+--+");
  FOR c1:= 1 TO 18 DO
   Goto(72, c1);
   IF l2count > 18 - c1 THEN
    Write("|"); Color(2);
    WriteString("()");
    Color(7); Write("|")
   ELSE
    WriteString("|  |")
   END
  END
 END ShowL2;

 PROCEDURE AddPt;
  VAR ch: CHAR;
 BEGIN
  IF wt THEN
   IF wtc > 0 THEN DEC(wtc) END
  ELSE
   INC(wtc);
   IF wtc >= 20 THEN StartTime(time); wtc:= 20 END
  END;
  IF addpt > decpt THEN
   DEC(addpt, decpt); decpt:= 0
  ELSE
   DEC(decpt, addpt); addpt:= 0
  END;
  IF (addpt > 0) OR (decpt > 0) THEN
   scoreshown:= FALSE;
   IF addpt > 0 THEN INC(score); DEC(addpt) END;
   IF decpt > 0 THEN
    IF score > 0 THEN DEC(score) END;
    DEC(decpt)
   END;
   IF score >= followscore THEN
    WITH object[first[PLAYER]] DO
     INC(vie); Snd(K2, BN, x, y)
    END;
    ShowLives; ShowScore; INC(followscore, 1000)
   END
  END;
  IF wtc < 8 THEN
   IF NOT(scoreshown) THEN
    ShowScore
   ELSE
   (*
    INC(rfx); IF rfx > 75 THEN rfx:= 0; INC(rfy) END;
    IF rfy > 21 THEN rfy:= 0 END;
    ch:= Report(rfx, rfy);
    IF (TypeOf(ch) <> L1) OR (rfx > 71) THEN
     IF rfx > 71 THEN
      IF (ch = "(") OR (ch = ")") THEN Color(2) ELSE Color(7) END
     ELSIF rfy > 19 THEN
      IF (rfx >= 42) AND (rfx < 60) THEN Color(3) ELSE Color(7) END
     ELSE
      SetColor(ch)
     END;
     WriteAt(rfx, rfy, ch)
    END
    *)
   END
  END;
  Goto(0, 0);(* Flush; *)
  wt:= WaitTime(time, 16)
 END AddPt;

 PROCEDURE WaitKey;
 BEGIN
  SetPxPy(0, 0);
  REPEAT Read(ch) UNTIL ch <= 3C;
  REPEAT
   IF (addpt > 0) OR (decpt > 0) THEN
    AddPt
   ELSE
    IF NOT(scoreshown) THEN ShowScore END;
    IF NOT WaitTime(time, 300) THEN StartTime(time) END;
    IF at = 0 THEN
     IF clkt = 0 THEN ShowClock; clkt:= 50 ELSE DEC(clkt) END;
     Color(2); WriteAt(px, py, "*"); at:= 1
    ELSE
     Color(3); WriteAt(px, py, "*"); at:= 0
    END;
    Goto(0, 21);
(*    Flush; *)
   END;
   Read(ch)
  UNTIL ch <> 0C;
  Color(2); WriteAt(px, py, "*");
  ReadAgain;
  ClearLine(21)
 END WaitKey;

 PROCEDURE SortK3(k: ObjPtr);
  VAR no: ObjPtr;
 BEGIN
  WITH object[k] DO
   INCL(flags, 3);
   no:= next;
   IF first[K3] = k THEN first[K3]:= no END;
   IF prev <> 0 THEN object[prev].next:= no END;
   object[no].prev:= prev; prev:= next;
   next:= object[no].next; object[no].next:= k;
   IF next <> 0 THEN object[next].prev:= k END
  END
 END SortK3;

(********** Loading **********)

 PROCEDURE InitGame(level, game: CARD8);
  VAR eo: CARD8;
      oc: OBJECT;
      c1, c2: INT8;
 BEGIN
 (* Init Vars *)
  nlj:= 0; nlk:= 0;
  l2l:= 0; l2r:= 0;
  oldscore:= score;
  oldpvie:= pvie;
  oldblv:= blvcount;
  oldl2:= l2count;
  FOR c1:= 0 TO 71 DO gn[c1].hset:= SET16{}; gn[c1].lset:= SET16{} END;
  FOR c1:= 0 TO 19 DO
   FOR c2:= 0 TO 71 DO
    e[c1]^[c2]:= 0
   END
  END;
  INCL(made[level], game);
  firedir:= D;
  pcount:= 0;
  FOR oc:= MIN(OBJECT) TO MAX(OBJECT) DO
   first[oc]:= 0;
   attr[oc].nb:= 0
  END;
  FOR eo:= 1 TO MAXOBJ DO
   WITH object[eo] DO
    IF eo = MAXOBJ THEN next:= 0 ELSE next:= eo + 1 END;
    prev:= eo - 1; type:= EMPTY
   END
  END;
  first[EMPTY]:= 1;
  attr[EMPTY].nb:= MAXOBJ;
  LoadGame(Create, ax, ay);
  IF (level = 10) THEN
   DEC(blvcount);
   IF blvcount > 0 THEN EXCL(made[10], 0) END
  END;
  WriteString("Initialising, please wait...");
  eo:= first[K3];
  IF eo <> 0 THEN
   WHILE (object[eo].next <> 0) DO eo:= object[eo].next END;
   WHILE eo <> 0 DO
    WITH object[eo] DO
     SetPxPy(x, y);
     WHILE (next <> 0) AND
      (ABS(x - px) + ABS(y - py) >
      ABS(object[next].x - px) + ABS(object[next].y - py)) DO
      SortK3(eo)
     END;
     eo:= prev
    END
   END
  END;
  InitK2Dirs;
  eo:= first[ASC];
  WHILE eo <> 0 DO
   WITH object[eo] DO
    by:= y;
    WHILE (by < 19) AND NOT(Mur(Report(x, by))) DO INC(by) END;
    eo:= next
   END
  END;
  WITH object[first[PLAYER]] DO
   jump:= 0; dir:= NUL;
   sx:= x; sy:= y
  END;
  MoveProx;
  FOR oc:= MIN(OBJECT) TO MAX(OBJECT) DO
   WITH attr[oc] DO
    ct:= dt;
    timeout:= speed[oc];
    co:= first[oc]
   END;
   SetAttr(oc)
  END;
  ClearLine(20);
  ShowMessage;
  ShowL2;
  Goto(0, 21); WriteString("Press any key to start");
  Snd(PLAYER, PLAYER, sx, sy);
  WaitKey;
  stat:= Playing;
  StartTime(time);
 END InitGame;

(******** Events *******)

 PROCEDURE Delay(d: CARD16);
  VAR c: CARD16;
 BEGIN
  FOR c:= 1 TO d DIV 16 DO AddPt END
 END Delay;

 PROCEDURE Aie(t1, t2: OBJECT; o: ObjPtr; n: CARD8);
  VAR
   i: INT8;
 BEGIN
  i:= n;
  WITH object[o] DO
   IF seq = 2 THEN seq:= 0; RETURN END;
   IF vie <= i THEN
    IF (t2 <> BN) AND (t2 <> K0) THEN
     INC(decpt, dispt[t2])
    ELSE
     IF NOT(ND IN flags) THEN INC(addpt, dispt[t2]) END
    END;
    vie:= 0; Remove(o)
   ELSE
    DEC(vie, n);
    IF NOT(ND IN flags) THEN
     IF (t2 <> BN) AND (t2 <> K0) THEN
      INC(decpt, aiept[t2])
     ELSE
      INC(addpt, aiept[t2])
     END
    END;
    Snd(t1, t2, x, y)
   END
  END
 END Aie;

 PROCEDURE Boum(nx, ny: INT8);
  VAR new: ObjPtr;
 BEGIN
  new:= New(BM);
  IF new = 0 THEN RETURN END;
  WITH object[new] DO
   x:= nx; y:= ny
  END;
  Ghost(nx-1, ny-1, "\"); Ghost(nx, ny-1, "|"); Ghost(nx+1, ny-1, "/");
  Ghost(nx-1, ny,   "-");                       Ghost(nx+1, ny,   "-");
  Ghost(nx-1, ny+1, "/"); Ghost(nx, ny+1, "|"); Ghost(nx+1, ny+1, "\");
 END Boum;

 PROCEDURE AddL2;
 BEGIN
  IF l2count < 18 THEN
   INC(l2count); Color(2);
   WriteAt(73, 19 - l2count, "("); Write(")")
  END
 END AddL2;

 PROCEDURE DecVie(p: ObjPtr);
 BEGIN
  IF level = 10 THEN stat:= Finish; RETURN END;
  WITH object[p] DO
   Snd(PLAYER, EMPTY, x, y)
  END;
  rfx:= 0; rfy:= 20;
  StartTime(time);
  Delay(1024);
  WHILE (addpt > 0) OR (decpt > 0) DO AddPt END;
  ShowScore;
  AddL2; AddL2; AddL2;
  WITH object[p] DO
   WriteAt(x, y, " ");
   x:= sx; y:= sy; jump:= 0; dir:= NUL;
   e[y]^[x]:= p;
   DEC(vie); ShowLives;
   IF vie = 0 THEN
    Delay(3072);
    Snd(EMPTY, EMPTY, x, y);
    stat:= GameOver;
    RETURN
   END
  END;
  MoveProx; Color(2); WriteAt(sx, sy, "*");
  Goto(0, 21); Color(6);
  WriteString("Press any key to start");
  WaitKey
 END DecVie;

 PROCEDURE Fire(t: OBJECT; fx, fy, fd: INT8; nid: BOOLEAN);
  VAR
   new: ObjPtr;
   c: CARD8;
 BEGIN
  IF fd = N THEN RETURN END;
  IF t = L1 THEN
   IF nid THEN
    IF nlk >= 6 THEN RETURN END
   ELSE
    IF nlj >= 8 THEN
     c:= Random() MOD nlj;
     new:= first[L1];
     WHILE ND IN object[new].flags DO new:= object[new].next END;
     WHILE c > 0 DO
      REPEAT new:= object[new].next; IF new = 0 THEN HALT END
      UNTIL NOT(ND IN object[new].flags);
      DEC(c)
     END;
     Remove(new)
    END
   END
  END;
  new:= New(t);
  IF new = 0 THEN RETURN END;
  IF t = L1 THEN IF nid THEN INC(nlk) ELSE INC(nlj) END END;
  Snd(EMPTY, t, fx, fy);
  WITH object[new] DO
   IF nid THEN INCL(flags, ND) END;
   x:= fx; y:= fy; d:= fd;
   IF t = L1 THEN
    IF ODD(d) THEN ch1:= "-" ELSE ch1:= "|" END;
    vie:= 1; IF nid THEN seq:= -6 ELSE seq:= -12 END
   ELSIF t = L2 THEN
    IF d = G THEN ch1:= "("; INC(l2l) ELSE ch1:= ")"; INC(l2r) END;
    vie:= 5; seq:= -128
   ELSE
    IF d = G THEN ch1:= "{" ELSE ch1:= "}" END
   END
  END;
  Move[t](new)
 END Fire;

 PROCEDURE SubL2;
 BEGIN
  IF l2count > 0 THEN
   WriteAt(73, 19 - l2count, " "); Write(" ");
   DEC(l2count)
  END
 END SubL2;

 PROCEDURE AddBLV;
 BEGIN
  EXCL(made[10], 0);
  INC(blvcount)
 END AddBLV;

 PROCEDURE Gn2(x, y: INT8): BOOLEAN;
 BEGIN
  IF y < 16 THEN RETURN y IN gn[x].lset ELSE RETURN (y - 16) IN gn[x].hset END
 END Gn2;

 PROCEDURE MakeGn2(nx, ny: INT8);
  VAR g: ObjPtr;
 BEGIN
  IF Report(nx, ny) <> " " THEN RETURN END;
  g:= New(GN2);
  IF g = 0 THEN RETURN END;
  Snd(EMPTY, GN2, nx, ny);
  IF ny < 16 THEN EXCL(gn[nx].lset, ny) ELSE EXCL(gn[nx].hset, ny - 16) END;
  Color(5);
  WITH object[g] DO
   x:= nx; y:= ny; vie:= 1;
   e[y]^[x]:= g;
   seq:= 9; WriteAt(x, y, "£")
  END
 END MakeGn2;

 PROCEDURE AiePlayer(ax, ay: INT8; ch: CHAR; o: ObjPtr);
 BEGIN
  Snd(TypeOf(ch), PLAYER, ax, ay);
  Color(2); Boum(ax, ay);
  SetColor(ch);
  MoveChar(oldx, oldy, " ", ax, ay, ch);
  DecVie(e[ay]^[ax]);
  IF object[o].type <> EMPTY THEN Remove(o) END
 END AiePlayer;

 PROCEDURE MoveObj(o: ObjPtr; c, x, y: INT8; dch: CHAR);
 BEGIN
  IF (oldx <> x) OR (oldy <> y) OR (Report(x, y) <> dch) THEN
   Color(c);
   IF e[oldy]^[oldx] = o THEN
    MoveChar(oldx, oldy, " ", x, y, dch)
   ELSE
    WriteAt(x, y, dch)
   END
  END;
  e[y]^[x]:= o
 END MoveObj;

 PROCEDURE Confirm(): BOOLEAN;
  VAR ch: CHAR;
 BEGIN
  Goto(0, 21); Color(3);
  WriteString("*** REALY QUIT [Y/N] ***");
  WaitChar(ch);
  ClearLine(21);
  RETURN (ch < " ") OR (ch = "y") OR (ch = "Y")
      OR (ch = "J") OR (ch = "j") OR (ch = "O")
      OR (ch = "o") OR (ch = "Q") OR (ch = "q")
 END Confirm;

(***** Move help *****)

 PROCEDURE Fixed(ch: CHAR): BOOLEAN;
  VAR t: OBJECT;
 BEGIN
  t:= TypeOf(ch);
  RETURN ((t = L1) OR (t = L2) OR (t = GN1) OR (t >= PLAT)) AND (ch <> ".")
 END Fixed;

 PROCEDURE GetK2Dir(x, y, z: INT8): DIRECTION;
 BEGIN
  RETURN VAL(DIRECTION, SHIFT(drK2[y]^[x], - z * 2) MOD 4)
 END GetK2Dir;

 PROCEDURE FireBall(nx, ny: INT8; jmp: CARD8; nd: DIRECTION);
  VAR b: ObjPtr;
 BEGIN
  IF Report(nx, ny) <> " " THEN RETURN END;
  b:= New(BALL);
  IF b = 0 THEN RETURN END;
  WITH object[b] DO
   x:= nx; y:= ny; vie:= 1;
   dir:= nd; jump:= 0; by:= jmp;
   e[y]^[x]:= b; Color(6);
   WriteAt(x, y, "O")
  END;
  IF (nd = NUL) AND (ny > 0) AND (Report(nx, ny - 1) = "N") THEN
   Snd(K4, EMPTY, nx, ny)
  END
 END FireBall;

 PROCEDURE Dead(ch: CHAR): BOOLEAN;
  VAR t: OBJECT;
 BEGIN
  t:= TypeOf(ch);
  RETURN (t = EMPTY) OR (t = ASC) OR (t = BN) OR (t = SBN) OR (t = MR) OR (t = BM)
 END Dead;

 PROCEDURE CreateObj(nx, ny: INT8);
  VAR r, mx: CARD8;
      t: OBJECT;
      new: ObjPtr;
 BEGIN
  r:= mvie;
  r:= Random() MOD r;
  mx:= 6;
  IF r > 24 THEN
   t:= K0; ch:= "&"; mx:= 5
  ELSIF r > 22 THEN
   t:= GN2; ch:= "£"
  ELSIF r > 20 THEN
   t:= PIC; ch:= "V"; mx:= 10
  ELSIF r > 16 THEN
   t:= K4; ch:= "x"
  ELSIF r > 12 THEN
   t:= K2; ch:= "X"
  ELSIF r > 8 THEN
   t:= K1; ch:= "+"
  ELSE
   t:= K3; ch:= "#"; mx:= 30
  END;
  IF (attr[t].nb > mx) THEN WriteAt(nx, ny, " "); RETURN END;
  new:= New(t);
  IF new = 0 THEN WriteAt(nx, ny, " "); RETURN END;
  WITH object[new] DO
   x:= nx; y:= ny;
   IF t = K4 THEN seq:= 2 ELSIF t = GN2 THEN seq:= 9 END;
   r:= mvie;
   vie:= Random() MOD (r + level) DIV 4 + 1;
   INCL(flags, ND)
  END;
  SetColor(ch);
  WriteAt(nx, ny, ch);
  e[ny]^[nx]:= first[t]
 END CreateObj;


 VAR
  doorsnd: BOOLEAN;

(********** MoveProcs **********)

 PROCEDURE MovePlayer(p: ObjPtr);
  VAR nd, nx, ny, best: INT8;
      pt, ql2: INT16;
      c1, c2: INT8;
      t, ft, bft: OBJECT;
 BEGIN
  IF stat <> Playing THEN RETURN END;
  IF ndoor <> 0 THEN doorsnd:= FALSE END;
  IF (attr[K0].nb = 0) AND (ndoor = 0) AND wt THEN
   IF at = 0 THEN at:= 8 END;
   DEC(at);
   IF at = 4 THEN
    Color(2); WriteAt(ax, ay, "A")
   ELSIF at = 0 THEN
    IF NOT(doorsnd) THEN
     WITH object[p] DO Snd(PLAYER, PLAYER, x, y + 1) END;
     doorsnd:= TRUE
    END;
    Color(3); WriteAt(ax, ay, "A")
   END
  END;
  IF (wtc = 0) AND (clkt = 0) THEN ShowClock; clkt:= 50 END;
  IF clkt <> 0 THEN DEC(clkt) END;
  WITH object[p] DO
   IF vie > 98 THEN vie:= 98; END;
   pt:= 10 - (level MOD 10);
   ReadDir(ch);
   IF ch > 3C THEN
    IF ch <> repch THEN repch:= ch; reps:= 1 ELSE INC(reps) END;
    REPEAT
     ReadDir(ch);
     IF reps < 50 THEN INC(reps) END
    UNTIL (ch <> repch) OR (ch <= 3C);
    DEC(reps);
    IF ch > 3C THEN ReadAgain END
   END;
   IF reps = 0 THEN repch:= 0C ELSE DEC(reps) END;
   IF repch = "o" THEN repch:= lastch ELSE lastch:= repch END;
   oldx:= x; oldy:= y;
   IF ch = 3C THEN
    stat:= Break; ReadAgain;
    RETURN
   END;
   CASE repch OF
     "4", "D", 37C: dir:= LEFT; firedir:= G
    |"6", "C", 36C: dir:= RIGHT; firedir:= D
    |"5", "B", 35C: dir:= NUL
    |"8", 27C, 234C, 227C: Fire(L1, x, y, H, FALSE)
    |"2", 30C, 235C, 230C: Fire(L1, x, y, B, FALSE)
    |"1", 237C: Fire(L1, x, y, G, FALSE)
    |"3", 236C: Fire(L1, x, y, D, FALSE)
    |"7", 32C, 232C: IF l2count > 0 THEN Fire(L2, x, y, G, FALSE); SubL2 END
    |"9", 31C, 231C: IF l2count > 0 THEN Fire(L2, x, y, D, FALSE); SubL2 END
    |"f", 15C, 12C:
     c2:= 0; bft:= L1;
     FOR nd:= G TO B DO
      ft:= L1;
      c1:= 0; nx:= x; ny:= y; best:= 0;
      IF nd = G THEN ql2:= l2l ELSE ql2:= l2r END;
      LOOP
       INC(c1);
       INC(nx, deltah[nd]); INC(ny, deltav[nd]);
       IF (nx <= 0) OR (ny <= 0) OR (nx >= 71) OR (ny >= 19) THEN EXIT END;
       t:= TypeOf(Report(nx, ny));
       IF (t = PLAT) AND (best = 0) THEN best:= 1 END;
       IF (t = MR) OR (t = BM) OR (t = ASC) OR (t = BN) THEN EXIT END;
       IF (t = K0) OR (t = K1) OR (t = K2) OR (t = K3) OR (t = L3) OR
          (t = GN1) OR (t = GN2) OR (t = BALL) OR (t = PIC) OR
          (t = NID) OR (t = BUB) THEN
        IF ((t = GN1) OR (t = NID) OR (t = BUB) OR (c1 <= 2)) AND
           (l2count > 0) AND ODD(nd) AND (ql2 < 1) THEN ft:= L2 END;
        best:= 16 - c1; EXIT
       END;
       IF c1 >= 14 THEN EXIT END
      END;
      IF best > c2 THEN bft:= ft; firedir:= nd; c2:= best END
     END;
     IF bft = L2 THEN SubL2 END;
     Fire(bft, x, y, firedir, FALSE)
    |11C: IF ODD(firedir) THEN firedir:= 4 - firedir ELSE firedir:= D END
    |"0", " ", "A", 34C:
     ch:= Report(x, y + 1);
     IF (ch <> " ") AND (ch <> "|") AND (ch <> "H") THEN jump:= 5 END
    |"Q", "q", 3C, 33C: IF Confirm() THEN stat:= Break; addpt:= 0 END
    |"e": stat:= Finish; addpt:= 0; decpt:= 0;
      IF score > oldscore THEN score:= oldscore END;
      IF pvie > oldpvie THEN pvie:= oldpvie END;
      blvcount:= oldblv; l2count:= oldl2; oldpvie:= 0
    |"W", "w", "r", "R":
     Color(3); Goto(0, 21);
     WriteString("Please wait...");
     FOR c1:= 0 TO 21 DO
      FOR c2:= 0 TO 75 DO
       WriteTo(c2, c1, Report(c2, c1))
      END
     END;
     ShowMessage;
     ShowL2;
     ClearLine(21);
     StartTime(time)
    |"P", "p", 14C:
     Goto(0, 21); Color(7);
     WriteString("Game paused, press any key to continue");
     WaitKey
    ELSE
   END;
   IF dir = LEFT THEN DEC(x) ELSIF dir = RIGHT THEN INC(x) END;
   px:= x; py:= y; Color(2);
   ch:= Report(x, y);
   CASE TypeOf(ch) OF
     SBN:
      WriteAt(x, y, " ");
      IF ch = "." THEN
       Snd(K1, BN, x, y);
       INC(addpt, pt)
      ELSIF ch = "$" THEN
       INC(vie); ShowLives;
       Snd(K2, BN, x, y); AddBLV
      ELSIF ch = "@" THEN
       Snd(K3, BN, x, y); INC(addpt, 100)
      END
    |BN:
      IF e[y]^[x] = 0 THEN
       Snd(EMPTY, BN, x, y);
       WITH object[New(BN)] DO
        x:= px; y:= py; e[y]^[x]:= first[BN]
       END;
       WriteAt(oldx, oldy, " ");
       INC(addpt, 20);
       IF first[BN] <> 0 THEN Move[BN](first[BN]) END;
       AddL2; AddL2
      ELSE
       x:= oldx; dir:= NUL
      END
    |L1: IF ND IN object[e[y]^[x]].flags THEN x:= oldx; dir:= NUL END
    |L2, EMPTY, PLAYER:
   ELSE
    IF ch = "A" THEN
     MoveChar(oldx, oldy, " ", x, y, "*");
     stat:= Finish; RETURN
    ELSIF ch = "!" THEN
     Color(7); Snd(K4, BN, x, y);
     WriteAt(x, y, " ");
     IF Mur(Report(x, y-1)) AND Mur(Report(x, y+1))
        AND NOT(Mur(Report(x - oldx + x, y))) THEN
      WriteAt(x, y, "I"); INC(x, x - oldx)
     END;
     sx:= x; sy:= y; DEC(ndoor)
    ELSE
     x:= oldx; dir:= NUL
    END
   END;
   IF jump > 1 THEN
    DEC(jump); DEC(y)
   ELSIF jump = 1 THEN
    DEC(jump)
   ELSE
    INC(y)
   END;
   px:= x; py:= y;
   ch:= Report(x, y);
   CASE TypeOf(ch) OF
     SBN:
      IF ch = "." THEN
       Snd(K1, BN, x, y); INC(addpt, pt)
      ELSIF ch = "$" THEN
       INC(vie); ShowLives;
       Snd(K2, BN, x, y); AddBLV
      ELSIF ch = "@" THEN
       Snd(K3, BN, x, y); INC(addpt, 100)
      END
    |BN:
      IF e[y]^[x] = 0 THEN
       Snd(EMPTY, BN, x, y);
       WITH object[New(BN)] DO
        x:= px; y:= py; e[y]^[x]:= first[BN]
       END;
       WriteAt(oldx, oldy, " ");
       INC(addpt, 20);
       IF first[BN] <> 0 THEN Move[BN](first[BN]) END;
       AddL2; AddL2
      ELSE
       y:= oldy;
       IF jump > 1 THEN jump:= 1 END;
      END
    |L1: IF ND IN object[e[y]^[x]].flags THEN y:= oldy; END
    |L2, EMPTY, PLAYER:
    |BM:
      Color(6); Boum(x, oldy);
      Color(2); MoveChar(oldx, oldy, " ", x, oldy, "*");
      oldx:= x; c1:= oldy; oldy:= y; y:= c1;
      Color(7); WriteAt(oldx, oldy, "0"); DecVie(p); RETURN
   ELSE
    IF ch = "A" THEN
     stat:= Finish
    ELSIF ch = "!" THEN
     sx:= x; sy:= y;
     Snd(K4, BN, x, y); DEC(ndoor)
    ELSIF ch = "/" THEN
     IF jump > 0 THEN dir:= RIGHT ELSE dir:= LEFT END; y:= oldy;
     IF jump > 1 THEN jump:= 1 END
    ELSIF ch = "\" THEN
     IF jump > 0 THEN dir:= LEFT ELSE dir:= RIGHT END; y:= oldy;
     IF jump > 1 THEN jump:= 1 END
    ELSE
     y:= oldy;
     IF jump > 1 THEN jump:= 1 END
    END
   END;
   MoveObj(p, 2, x, y, "*");
   IF (x <> oldx) OR (y <> oldy) THEN
    IF jump > 0 THEN Snd(PLAYER, GN1, x, y) ELSE Snd(PLAYER, GN2, x, y) END
   END;
   FOR c1:= 1 TO 5 DO
    IF c1 < x THEN
     c2:= x - c1;
     IF Gn2(c2, y) THEN MakeGn2(c2, y) END
    END;
    c2:= x + c1;
    IF c2 < 72 THEN
     IF Gn2(c2, y) THEN MakeGn2(c2, y) END
    END
   END
  END
 END MovePlayer;

  VAR plfd, gnfd: CARD8;
      d0, d1, d2: CARD16;

 PROCEDURE ReportObj(x, y, d: INT8): CARD16;
  VAR ch: CHAR;
      c: INT8;
      t: OBJECT;
      danger: CARD16;
 BEGIN
  c:= 0; d0:= 0; danger:= 0; d2:= 0;
  LOOP
   INC(c);
   DEC(x, deltah[d]); DEC(y, deltav[d]);
   IF (x < 0) OR (y < 0) OR (x > 71) OR (y > 19) THEN EXIT END;
   ch:= Report(x, y);
   IF ch = ":" THEN
    IF d > H THEN DEC(d, 2) ELSE INC(d, 2) END
   ELSIF ch = "/" THEN
    d:= 5 - d
   ELSIF ch = "\" THEN
    IF ODD(d) THEN INC(d) ELSE DEC(d) END
   ELSE
    t:= TypeOf(ch);
    IF ODD(d) AND (t = PLAYER) AND (plfd > 0) THEN
     INC(d2, 2)
    ELSIF t = GN2 THEN
     INC(d2, 1)
    ELSIF (t = L1) OR (t = L2) OR ((t = PIC) AND (c < 4)) THEN
     IF object[e[y]^[x]].d = d THEN
      IF t = L2 THEN INC(d2, 5) ELSE INC(d2) END
     END
    END;
    IF c <= 5 THEN danger:= d2 END;
    IF c <= 4 THEN d0:= d2 END;
    IF ((t = K4) OR (t = GN1) OR (t = NID) OR (t = PLAT) OR (t = BN) OR
        (t = SBN) OR (t = MR) OR (t = BM)) AND (ch <> ".") THEN
     EXIT
    END
   END;
   IF c >= 6 THEN EXIT END
  END;
  RETURN danger
 END ReportObj;

 PROCEDURE MoveK0(k: ObjPtr);
  VAR
   danger: ARRAY[N..B] OF CARD16;
   best, tmp: CARD16;
   fuite: BOOLEAN;
   c1, td: INT8;
   mo: ObjPtr;
   mch: CHAR;
 BEGIN
  fuite:= FALSE;
  WITH object[k] DO
   IF (x = 0) OR (y = 0) OR (x = 71) OR (y = 19) THEN HALT END;
   SetPxPy(x, y);
   IF ODD(px) THEN gnfd:= 10 ELSIF gnfd > 0 THEN DEC(gnfd) END;
   IF nlj > 0 THEN plfd:= 10 ELSIF plfd > 0 THEN DEC(plfd) END;
   IF seq = 1 THEN (* contournement *)
    td:= d;
    IF v = 1 THEN
     IF td <= G THEN td:= B ELSE DEC(td) END
    ELSE
     IF td = B THEN td:= G ELSE INC(td) END
    END;
    ch:= Report(x + deltah[td], y + deltav[td]);
    IF (ch = "I") OR (ch = "8") THEN ch:= " " END;
    IF NOT Fixed(ch) THEN
     IF 1 IN flags THEN
      seq:= 0; EXCL(flags, 1); RETURN
     ELSE
      INCL(flags, 1)
     END;
     d:= td
    ELSE
     EXCL(flags, 1)
    END;
   ELSIF seq = 0 THEN (* rapprochement *)
    EXCL(flags, 1);
    IF px > x THEN d:= D ELSE d:= G END;
    IF py > y THEN td:= B ELSE td:= H END;
    IF px = x THEN d:= td END;
    IF py = y THEN td:= d END;
    IF Fixed(Report(x + deltah[d], y)) THEN
     IF NOT Fixed(Report(x, y + deltav[td])) THEN d:= td END
    ELSIF NOT Fixed(Report(x, y + deltav[td])) THEN
     IF ABS(px - x) + 1 < ABS(py - y) * 2 THEN d:= td END
    END;
    c1:= ABS(px - x) + ABS(py - y) * 2;
    IF (c1 >= 6) THEN (* Fire *)
     tmp:= mvie;
     IF (Random() MOD 128 + 4) < tmp THEN
      IF Report(x + deltah[SGN(px - x) + 2], y) = " " THEN
       IF (c1 < 22) THEN
        Fire(L3, x, y, SGN(px - x) + 2, TRUE)
       ELSE
        Fire(L1, x, y, (Random() MOD 2) * 2 + 1, TRUE)
       END
      END
     END;
     tmp:= mvie;
     IF ((Random() MOD 256 + 7) < tmp) AND ODD(d) THEN
      Fire(L1, x, y, (Random() MOD 2) * 2 + 2, TRUE)
     END
    END
   END;
  (* controle des projectiles dangereux *)
   FOR td:= N TO B DO danger[td]:= 0 END;
   FOR td:= G TO B DO
    ch:= Report(x + deltah[td], y + deltav[td]);
    IF Fixed(ch) THEN
     danger[td]:= 1024
    END;
    d1:= ReportObj(x, y, td);
    IF td > H THEN c1:= td - 2 ELSE c1:= td + 2 END;
    INC(danger[N], d1);
    INC(danger[td], d0);
    INC(danger[c1], d2);
   END;
   INC(danger[G], ReportObj(x - 1, y, H) + ReportObj(x - 1, y, B));
   INC(danger[D], ReportObj(x + 1, y, H) + ReportObj(x + 1, y, B));
   INC(danger[H], ReportObj(x, y - 1, G) + ReportObj(x, y - 1, D));
   INC(danger[B], ReportObj(x, y + 1, G) + ReportObj(x, y + 1, D));
   IF (danger[d] MOD 1024 > 0) OR (danger[N] > 0) THEN
    fuite:= TRUE; seq:= 0;
    best:= danger[d];
    FOR td:= N TO B DO
     IF danger[td] <= best THEN d:= td; best:= danger[td] END
    END
   END;
   IF seq = 2 THEN
    IF Report(x, y) <> "&" THEN
     Color(3); WriteAt(x, y, "&"); e[y]^[x]:= k
    END;
    RETURN
   END;
   oldx:= x; oldy:= y;
   INC(x, deltah[d]); INC(y, deltav[d]);
   ch:= Report(x, y); mch:= " ";
   IF (ch = "I") OR (ch = "8") THEN ch:= " " END;
   IF ch = "*" THEN
    AiePlayer(x, y, "&", k); RETURN
   ELSIF Fixed(ch) THEN
    IF seq = 1 THEN
     IF (x = 0) OR (x = 71) OR (y = 0) OR (y = 19) THEN
      (* inverse le sens de recherche *)
      IF d < D THEN INC(d, 2) ELSE DEC(d, 2) END;
      IF v = 1 THEN v:= 0 ELSE v:= 1 END
     END
    END;
    x:= oldx; y:= oldy;
    IF NOT fuite THEN
     IF seq = 0 THEN (* nouveau contournement *)
      bx:= x; by:= y;
      REPEAT
       IF ABS(px - bx) >= ABS(py - by) * 2 THEN
        IF px > bx THEN INC(bx) ELSE DEC(bx) END
       ELSE
        IF py > by THEN INC(by) ELSE DEC(by) END
       END;
       ch:= Report(bx, by)
      UNTIL NOT Fixed(ch);
      seq:= 1
     END;
     IF v = 1 THEN
      IF d = B THEN d:= G ELSE INC(d) END
     ELSE
      IF d <= G THEN d:= B ELSE DEC(d) END
     END
    END
   ELSIF (ch <> " ") AND (ch <> ".") AND (ch <> "&") AND (ch <> "=") THEN
    mo:= e[y]^[x]; mch:= ch;
    WITH object[mo] DO x:= oldx; y:= oldy END;
    e[oldy]^[oldx]:= mo
   END;
   IF mch <> " " THEN
    SetColor(mch); WriteAt(oldx, oldy, mch);
    Color(3); WriteAt(x, y, "&")
   ELSE
    MoveObj(k, 3, x, y, "&")
   END;
   IF (x <> oldx) OR (y <> oldy) THEN Snd(BM, EMPTY, x, y) END;
   e[y]^[x]:= k;
   IF (bx = x) AND (by = y) THEN seq:= 0 END
  END
 END MoveK0;

END GrotteActions.
