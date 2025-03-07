IMPLEMENTATION MODULE GrotteBonus;
  (*$ CStrings:= FALSE *)
 FROM Memory IMPORT SET16, CARD8, INT8, CARD16, INT16, CARD32, INT32,
  AllocMem, FreeMem;
 FROM ANSITerm IMPORT Read, ReadAgain,
  ReadString, Report, Write, WriteString, WriteLn, WriteAt, MoveChar,
  Ghost, Goto, ClearScreen, Color, ClearLine, WaitChar;
 FROM Clock IMPORT TimePtr, AllocTime, FreeTime, StartTime, WaitTime, noTime;
 FROM GrotteSupport IMPORT OBJECT, Rnd, Create, pvie, ndoor, ppos, pcount,
  score;


(***** Niveaux Bonus *****)
 VAR
  clock0: TimePtr;

 PROCEDURE WrAt(x, y: INT8; ch: CHAR);
  VAR
   w: BOOLEAN;
 BEGIN
  IF clock0 <> noTime THEN w:= WaitTime(clock0, 1) END;
  WriteAt(x, y, ch)
 END WrAt;

 PROCEDURE Cadre(H: BOOLEAN);
  VAR c: INT8;
 BEGIN
  Goto(0, 0); Color(7);
  FOR c:= 0 TO 71 DO Write("S") END;
  Goto(0, 19);
  FOR c:= 0 TO 71 DO
   IF H THEN Color(6); Write("H") ELSE Color(7); Write("S") END
  END;
  Color(7);
  FOR c:= 0 TO 19 DO
   WrAt(0, c, "S"); WrAt(71, c, "S")
  END;
 END Cadre;

 PROCEDURE Find(VAR x, y: INT16; sx, sy, dx, dy: INT16);
  VAR
   w, h: CARD16;
 BEGIN
  w:= dx - sx + 1; h:= dy - sy + 1;
  REPEAT
   x:= Rnd() MOD w; INC(x, sx);
   y:= Rnd() MOD h; INC(y, sy)
  UNTIL Report(x, y) = " "
 END Find;

 PROCEDURE Put(ch: CHAR; n, sx, sy, dx, dy: INT16);
  VAR x, y, c: INT16;
 BEGIN
  FOR c:= 1 TO n DO
   Find(x, y, sx, sy, dx, dy);
   WrAt(x, y, ch)
  END
 END Put;

 PROCEDURE PutObj(t: OBJECT; n, sx, sy, dx, dy, nvie, nseq: INT16; ch: CHAR);
  VAR x, y, c: INT16;
 BEGIN
  FOR c:= 1 TO n DO
   Find(x, y, sx, sy, dx, dy);
   Create(t, x, y, nvie, nseq, ch)
  END
 END PutObj;

 PROCEDURE PutDeltaObj(t: OBJECT; n, sx, sy, dx, dy, h, v, nvie, nseq: INT16; ch: CHAR);
  VAR x, y, c: INT16;
 BEGIN
  FOR c:= 1 TO n DO
   Find(x, y, sx, sy, dx, dy);
   WHILE Report(x + h, y + v) = " " DO
    INC(x, h); INC(y, v)
   END;
   Create(t, x, y, nvie, nseq, ch)
  END
 END PutDeltaObj;

 PROCEDURE DrawK0(sx, sy, dx, dy, nseq: INT16);
  VAR c1, c2: INT16;
 BEGIN
  c1:= 0; c2:= 1;
  WHILE (pvie > c2 + 2) AND (c2 < 64) DO
   INC(c1); c2:= c2 * 2
  END;
  PutObj(K0, c1, sx, sy, dx, dy, pvie DIV 4 + 1, nseq, "&")
 END DrawK0;

 PROCEDURE BonusLevel1(VAR ax, ay: INT8);
  (* MiniGrotte *)
  VAR dx, dy, x, y, c: INT16;
 BEGIN
  Cadre(FALSE);
  Color(6);
  FOR x:= 40 TO 71 DO WrAt(x, 19, "H") END;
  Color(7);
  FOR y:= 2 TO 18 DO WrAt(39, y, "S") END;
  ax:= 39; ay:= 18;
  dx:= 1;
  FOR y:= 2 TO 14 BY 4 DO
   dy:= Rnd() MOD 3; dx:= 2 - dy;
   FOR x:= 1 TO 30 DO WrAt(x, y + dy, "S") END;
   FOR c:= 1 TO 4 DO
    x:= Rnd() MOD 32 + 1;
    FOR dy:= y + 1 TO y + dx DO WrAt(x, dy, "S") END
   END
  END;
  FOR c:= 1 TO 200 DO
   WrAt(Rnd() MOD 35 + 1, Rnd() MOD 16 + 2, " ")
  END;
  FOR y:= 2 TO 16 BY 2 DO
   x:= 39;
   LOOP
    INC(x, Rnd() MOD 8 + 1);
    dy:= Rnd() MOD 2;
    IF x > 69 THEN EXIT END;
    WrAt(x, y + dy, "S");
    IF Rnd() MOD 2 = 1 THEN WrAt(x + 1, y + dy, "S") END
   END
  END;
  Create(PLAYER, 1, 1, pvie, 0, "*");
  Create(ASC, 37, 16, 1, 0, "=");
  Color(3); WrAt(35, 1, "!"); INC(ndoor);
  PutObj(NID, 15, 1, 1, 70, 18, 2, 0, "Z");
  Color(2);
  Put("%", 8, 1, 1, 70, 8);
  Put("@", 4, 1, 1, 70, 12)
 END BonusLevel1;

 PROCEDURE BonusLevel2(VAR ax, ay: INT8);
  (* Montagnes : et x *)
  VAR
   x, y, c, d, k, mx: INT16;
 BEGIN
  Cadre(FALSE); Color(7);
 (* Mur de droite *)
  y:= 1; x:= 54; c:= 0;
  WHILE y < 18 DO
   IF c <= 0 THEN
    c:= Rnd() MOD 4 + 1;
    d:= Rnd() MOD 3; DEC(d)
   END;
   IF x > 60 THEN d:= -ABS(d) ELSIF x < 44 THEN d:= ABS(d) END;
   WrAt(x, y, ":");
   INC(y); INC(x, d); DEC(c)
  END;
 (* montagnes *)
  FOR k:= 0 TO 2 BY 2 DO
   y:= 5 + k * 4; x:= k + 1;
   mx:= y + 3;
   c:= 0;
   WHILE (Report(x + 2 - k, y) = " ") AND (Report(x + 3 - k, y) = " ") DO
    IF c <= 0 THEN
     c:= Rnd() MOD 4 + 2;
     d:= Rnd() MOD 3; DEC(d)
    END;
    INC(y, d);
    IF (Report(x, y - 1) <> " ") OR (Report(x, y - 2) <> " ") THEN
     INC(y, 2); d:= ABS(d)
    END;
    IF y > mx THEN y:= mx; d:= -ABS(d) END;
    WrAt(x, y, ":");
    INC(x); DEC(c)
   END
  END;
  FOR x:= 3 TO 41 DO
   c:= 18; y:= 18;
   REPEAT DEC(c) UNTIL (Report(x, c) <> " ") OR (c <= 9);
   INC(c, 6);
   WHILE y >= c DO WrAt(x, y, "S"); DEC(y) END
  END;
  Color(3); WrAt(1, 18, "!");
  INC(ndoor, 2);
  WrAt(70, 18, "!"); ax:= 70; ay:= 1;
  Color(6); WrAt(69, 19, "H");
  y:= 18; x:= 65;
  WHILE y > 1 DO
   Create(TPLAT, x, y, 1, 0, " ");
   d:= Rnd() MOD 3;
   DEC(y, d + 1);
   REPEAT
    c:= Rnd() MOD 9; DEC(c, 4);
    IF x + c < 61 THEN c:= ABS(c) ELSIF x + c > 70 THEN c:= -ABS(c) END
   UNTIL (c <> 0) AND NOT((d = 0) AND (ABS(c) = 1));
   INC(x, c)
  END;
  Color(7);
  FOR x:= 61 TO 70 BY 3 DO WrAt(x, 3, ":") END;
  FOR c:= 1 TO 20 DO
   WrAt(Rnd() MOD 64 + 1, Rnd() MOD 16 + 2, " ")
  END;
  Put(" ", 20, 10, 1, 44, 18);
  Create(PLAYER, 1, 1, pvie, 0, "*");
  Create(GN1, 70, 9, 3, 0, "<");
  PutDeltaObj(PIC, 12, 5, 1, 68, 17, 0, -1, 1, 2, "V");
  DrawK0(36, 1, 70, 18, 0);
  PutObj(K4, 20, 1, 1, 70, 12, pvie, 2, "x");
  Color(2);
  Put(".", 80, 1, 1, 44, 18);
  Put("%", 8, 1, 1, 70, 18);
  Put("@", 3, 1, 1, 64, 18);
  Color(7);
  Put("8", 20, 1, 1, 70, 18);
 END BonusLevel2;

 PROCEDURE BonusLevel3(VAR ax, ay: INT8);
  (* Labyrinthe *)
  VAR
   x, y, c: INT16;
   m: CARD16;
   ok: BOOLEAN;
 BEGIN
  m:= Rnd() MOD 4 + 2;
  ax:= 65; ay:= 0;
  Cadre(FALSE); Color(7);
  FOR y:= 2 TO 16 BY 2 DO
   FOR x:= 2 TO 60 BY 2 DO
    WrAt(x, y, "S");
    ok:= FALSE;
    FOR c:= 2 TO 4 BY 2 DO
     IF (y - c <= 0) OR
      ((Report(x - 1, y - c) = "S") OR (Report(x - 2, y - c + 1) = " "))
     THEN
      ok:= TRUE
     END
    END;
    IF (Rnd() MOD m = 0) AND ok THEN
     WrAt(x, y - 1, "S")
    ELSE
     WrAt(x - 1, y, "S")
    END
   END
  END;
  FOR x:= 1 TO 59 BY 2 DO
   IF Report(x, 16) = " " THEN WrAt(x, 18, ":") END
  END;
  FOR y:= 1 TO 16 DO WrAt(62, y, "S") END;
  WrAt(62, 3, " ");
  Create(PLAYER, 1, 1, pvie, 0, "*");
  Color(2);
  FOR x:= 65 TO 70 DO WrAt(x, 1, "@") END;
  Color(7);
  WrAt(31, 18, "S");
  FOR y:= 1 TO 17 BY 2 DO Create(GN1, 70, y, 3, 0, "<") END;
  Create(ASC, 64, 5, 1, 0, "=");
  DrawK0(62, 1, 64, 18, 2);
  IF pvie >= 40 THEN c:= 160 ELSE c:= pvie * 4 END;
  PutObj(GN2, c, 1, 2, 71, 18, 1, 0, "£");
  Create(NID, 62, 3, 2, 0, "Z");
  Create(NID, 62, 18, 2, 0, "Z");
  Color(2);
  FOR x:= 3 TO 57 BY 6 DO WrAt(x, 1, "%") END;
  Put(".", 150, 1, 1, 71, 18)
 END BonusLevel3;

 PROCEDURE BonusLevel4(VAR ax, ay: INT8);
  (* Grotte 2 tages *)
  VAR
   x, y, y2: INT16;
   c: CARD16;
 BEGIN
  c:= 0; Color(7);
  REPEAT
   c:= (c * 17 + 1) MOD 2048;
   y:= c DIV 64; x:= c MOD 64;
   IF y < 20 THEN
    WrAt(x, y, "S");
    IF x < 8 THEN WrAt(x + 64, y, "S") END
   END
  UNTIL c = 0;
  ax:= 0; ay:= 11;
  FOR y2:= 1 TO 10 BY 9 DO
   y:= y2 + 1; c:= 1;
   FOR x:= 1 TO 70 DO
    IF c = 0 THEN
     c:= Rnd() MOD 8;
     INC(y, Rnd() MOD 7); DEC(y, 3);
     IF y < y2 THEN y:= y2 ELSIF y > y2 + 4 THEN y:= y2 + 4 END
    ELSE
     DEC(c)
    END;
    WrAt(x, y, " "); WrAt(x, y + 1, " "); WrAt(x, y + 2, " ");
    WrAt(x, y + 3, " "); WrAt(x, y + 4, " ")
   END
  END;
  FOR y:= 1 TO 18 DO
   FOR x:= 67 TO 70 DO WrAt(x, y, " ") END
  END;
  Color(4);
  Create(ASC, 68, 10, 1, 0, "=");
  Create(ASC, 69, 10, 1, 0, "=");
  DrawK0(1, 1, 70, 18, 2);
  Color(5);
  PutObj(GN2, pvie, 1, 1, 36, 18, 1, 0, "£");
  PutObj(GN1, 6, 36, 1, 70, 9, 3, 0, "<");
  PutObj(NID, 6, 1, 10, 36, 18, 2, 0, "Z");
  PutDeltaObj(PIC, 12, 1, 8, 70, 18, 0, -1, 1, 2, "V");
  Color(2);
  Create(PLAYER, 1, 1, pvie, 0, "*");
  Put("%", 9, 1, 1, 70, 18); Put(".", 120, 1, 1, 70, 18)
 END BonusLevel4;

 PROCEDURE BonusLevel5(VAR ax, ay: INT8);
  (* Château *)
  VAR
   miny, maxy, maxx, minx, x, y, c: INT16;
   d: CARD16;
   k, s: BOOLEAN;
   ch: CHAR;
 BEGIN
  Cadre(FALSE);
  maxy:= -1;
  s:= FALSE;
  REPEAT
   k:= TRUE;
   miny:= maxy + 2;
   INC(maxy, Rnd() MOD 8 + 6);
   IF maxy > 12 THEN maxy:= 19 END;
   Color(7);
   FOR x:= 2 TO 70 DO WrAt(x, maxy, "S") END;
   DEC(maxy); maxx:= -1;
   REPEAT
    Color(7);
    minx:= maxx + 2;
    INC(maxx, Rnd() MOD 16 + 16);
    IF maxx > 64 THEN maxx:= 71 END;
    FOR y:= miny TO maxy DO WrAt(maxx, y, "S") END;
    IF maxx < 71 THEN
     IF k THEN WrAt(maxx, maxy, " ") ELSE WrAt(maxx, miny, " ") END
    END;
    DEC(maxx); y:= miny;
    LOOP
     IF (k AND s) THEN c:= 5 ELSE c:= 2 END;
     d:= c; INC(y, (Rnd() MOD d) + 2);
     IF y >= maxy THEN EXIT END;
     FOR x:= minx TO maxx DO WrAt(x, y, "S") END;
     FOR c:= 1 TO 5 DO
      x:= minx; d:= maxx - minx - 1;
      INC(x, Rnd() MOD d);
      WrAt(x, y, " ")
     END
    END;
    c:= Rnd() MOD 4;
    IF c = 0 THEN
     x:= 1; ch:= "@"
    ELSIF c = 1 THEN
     x:= 3; ch:= "%"
    ELSE
     x:= 7; ch:= "."
    END;
    Color(2);
    Put(ch, x, minx, miny, maxx, maxy);
    k:= NOT(k)
   UNTIL maxx >= 70;
   s:= TRUE
  UNTIL maxy >= 18;
  Create(PLAYER, 70, 1, pvie, 0, "*");
  ax:= 70; ay:= 18;
  Color(3); WrAt(1, 1, "!"); INC(ndoor);
  DrawK0(1, 1, 50, 18, 2);
  Color(2); Put("%", 5, 1, 1, 70, 18);
  PutDeltaObj(PIC, 8, 1, 1, 70, 18, 0, -1, 1, 2, "V");
  PutDeltaObj(GN1, 3, 1, 1, 70, 18, 1, 0, 3, 0, "<");
  PutDeltaObj(GN1, 3, 1, 1, 70, 18, -1, 0, 3, 0, ">");
  PutObj(NID, 6, 1, 5, 70, 18, 2, 0, "Z");
  PutObj(GN2, 12, 1, 1, 50, 18, 1, 0, "£")
 END BonusLevel5;

 PROCEDURE BonusLevel6(VAR ax, ay: INT8);
  (* Lignes *)
  VAR
   x, y, px, py, c, d: INT16;
   t: CARD16;
   k: BOOLEAN;
 BEGIN
  Color(7);
  Cadre(FALSE); d:= 0; py:= 3;
  FOR x:= 1 TO 70 DO
   IF d = 0 THEN
    REPEAT
     c:= Rnd() MOD 6
    UNTIL ABS(c - py) <= 4;
    py:= c;
    d:= Rnd() MOD 8 + 3
   ELSE
    DEC(d)
   END;
   c:= 19 - py;
   WHILE c < 19 DO
    WrAt(x, c, "S");
    INC(c)
   END;
   IF x = 1 THEN
    Create(PLAYER, 1, 18 - py, pvie, 0, "*");
    Color(7)
   END
  END;
  y:= 2;
  WHILE y < 12 DO
   FOR x:= 16 TO 68 DO
    t:= y; k:= (Rnd() MOD 16) > t;
    IF k THEN WrAt(x, y, "S") END
   END;
   INC(y, Rnd() MOD 3 + 2)
  END;
  ax:= 70; ay:= 5; ndoor:= 0;
  WrAt(70, 6, ":");
  y:= 18; x:= 8;
  WHILE y > 3 DO
   px:= x; py:= y;
   IF Report(x, y + 1) <> "S" THEN WrAt(x, y, "S") END;
   d:= Rnd() MOD 3;
   DEC(y, d + 1);
   REPEAT
    c:= Rnd() MOD 9; DEC(c, 4);
    IF x + c < 1 THEN c:= ABS(c) ELSIF x + c > 15 THEN c:= -ABS(c) END
   UNTIL (c <> 0) AND NOT((d = 0) AND (ABS(c) = 1));
   INC(x, c)
  END;
  INC(px);
  IF px < 12 THEN
   Create(PLAT, px, py, 1, 0, "T");
   ppos[0].x:= px; ppos[0].y:= py;
   ppos[1].x:= 15; ppos[1].y:= py;
   pcount:= 1
  END;
  Create(ASC, 70, 10, 1, 0, "=");
  PutDeltaObj(PIC, 8, 1, 1, 70, 18, 0, -1, pvie, 2, "V");
  PutObj(GN2, pvie DIV 8 + 1, 1, 1, 70, 18, 2, 0, "£");
  PutObj(K2, 16, 16, 1, 70, 18, pvie DIV 4 + 1, 0, "X");
  PutObj(NID, 2, 16, 1, 70, 18, 2, 0, "Z");
  PutObj(K0, 1, 16, 1, 70, 18, pvie DIV 4 + 1, 0, "&");
  Color(2);
  Put("%", 10, 1, 1, 69, 12);
  Put("@", 3, 1, 1, 69, 18);
  c:= (Rnd() MOD 4 + 1) * 16;
  Color(6);
  Put("H", c, 16, 3, 69, 18)
 END BonusLevel6;

 PROCEDURE BonusLevel7(VAR ax, ay: INT8);
  (* Grotte 1 etage *)
  VAR
   c, x, y, dx, dy, sx, ex, sy, ey, ox, oy: INT16;
   platdisp: BOOLEAN;
 BEGIN
 (* Remplissage *)
  Color(7);
  FOR c:= 0 TO 9 DO
   FOR y:= c TO 19 - c DO
    WrAt(c, y, "S");
    WrAt(71 - c, y, "S")
   END;
   FOR x:= c + 1 TO 70 - c DO
    WrAt(x, c, "S");
    WrAt(x, 19 - c, "S")
   END
  END;
 (* creuser *)
  platdisp:= TRUE;
  dx:= 1; dy:= 1; ex:= dx; oy:= 0; sx:= ex;
  WHILE ex < 71 DO
   ox:= sx;
   sx:= ex;
   INC(dx, 6 + Rnd() MOD 8);
   IF dx > 64 THEN dx:= 70 END;
   oy:= dy;
   dy:= 1 + Rnd() MOD 8;
   IF dy > oy THEN
    sy:= oy + 12; ey:= dy + 10
   ELSE
    sy:= dy; ey:= oy - 2
   END;
  (* creux *)
   REPEAT
    FOR y:= dy TO dy + 10 DO WrAt(ex, y, " ") END;
    INC(ex)
   UNTIL ex > dx;
   IF sx = 1 THEN
    Create(PLAYER, 1, dy + 9, pvie, 0, "*");
    Color(7)
   END;
  (* sol *)
   c:= Rnd() MOD 4;
   IF c = 0 THEN
    FOR y:= dy + 6 TO dy + 10 DO
     FOR x:= sx TO ex - 1 DO
      IF Rnd() MOD 8 = 0 THEN
       Create(GN2, x, y, 1, 0, "£")
      END;
      IF Rnd() MOD 2 = 0 THEN
       Color(2); WrAt(x, y, "."); Color(7)
      ELSE
       WrAt(x, y, "8")
      END
     END
    END
   ELSIF (c = 1) AND (dy < 7) THEN
    Color(2);
    FOR x:= sx + 1 TO ex - 2 DO WrAt(x, dy + 12, ".") END;
    Color(7);
    WrAt(sx + 1, dy + 11, "8"); WrAt(ex - 2, dy + 11, "8")
   ELSIF c = 2 THEN
    Color(6);
    FOR x:= sx TO ex - 1 BY 2 DO WrAt(x, dy + 11, "H") END;
    Color(7)
   ELSIF c = 3 THEN
    Color(2);
    FOR x:= sx TO ex - 1 DO
     c:= Rnd() MOD 4;
     FOR y:= dy + 10 - c TO dy + 10 DO
      WrAt(x, y, ".")
     END
    END;
    Color(7)
   END;
  (* left *)
   IF (ey - sy >= 3) AND (oy <> 0) THEN
    c:= Rnd() MOD 2; Color(2);
    FOR y:= sy TO ey DO
     FOR x:= ox + 2 TO sx - 1 DO
      IF c = 0 THEN WrAt(x, y, ".") ELSE WrAt(x, y, " ") END
     END
    END
   ELSIF (ey > sy) THEN
    IF dy > oy THEN y:= ey; x:= sy - 1 ELSE y:= sy; x:= ey + 1 END;
    WrAt(sx, x, "8");
    Create(GN1, sx, y, 3, 0, ">")
   END;
   Color(7);
  (* to go up *)
   c:= Rnd() MOD 3;
   IF c = 0 THEN
    Create(ASC, ex - 4, dy + 6, 1, 0, "=");
    Color(7);
    FOR x:= sx TO ex - 1 BY 4 DO
     WrAt(x, dy + 3, "0")
    END
   ELSIF (c = 1) AND platdisp THEN
    Create(PLAT, sx, dy + 7, 1, 0, "T");
    ppos[0].x:= sx; ppos[0].y:= dy + 7;
    ppos[1].x:= ex - 1; ppos[1].y:= dy + 1;
    pcount:= 1; platdisp:= FALSE
   ELSE
    WrAt(sx + 4, dy + 3, ":");
    y:= dy + 3; x:= ex - 5;
    LOOP
     INC(y, Rnd() MOD 4 + 1);
     IF y > dy + 10 THEN EXIT END;
     REPEAT
      c:= Rnd() MOD 7; DEC(c, 3)
     UNTIL (c <> 0) AND (x + c < ex) AND (x + c >= sx);
     INC(x, c);
     WrAt(x, y, ":")
    END
   END;
   Color(7)
  END;
 (* common objects *)
  PutDeltaObj(PIC, 16, 1, 1, 70, 18, 0, -1, pvie, 2, "V");
  PutObj(K1, 16, 16, 1, 70, 18, pvie DIV 4 + 1, 0, "+");
  Color(2);
  Put("%", 10, 1, 1, 70, 18);
  Put("@", 3, 1, 1, 70, 18);
  ax:= 70; ay:= dy + 10;
 END BonusLevel7;

 PROCEDURE BonusLevel8(VAR ax, ay: INT8);
  (* Plateaux *)
  VAR
   minY, x, w, y, dx, dy, c: INT16;
   d: CARD16;
 BEGIN
  Cadre(TRUE);
  Color(7); WrAt(1, 17, ":");
  Color(6); WrAt(1, 12, "H");
  Create(TPLAT, 70, 4, 1, 0, " ");
  Create(PLAYER, 1, 16, pvie, 0, "*");
  FOR minY:= 2 TO 14 BY 6 DO
   x:= 2; y:= minY + 4;
   LOOP
    IF minY < 6 THEN c:= 4 ELSE c:= 8 END;
    d:= c; w:= Rnd() MOD d + 1;
    c:= y;
    REPEAT
     y:= Rnd() MOD 5; INC(y, minY);
     dy:= ABS(c - y); d:= 7 - dy;
     dx:= (Rnd() MOD d) + 1;
    UNTIL (dy <= 4);
    INC(x, dx);
    IF x + w >= 70 THEN w:= 69 - x END;
    IF w <= 0 THEN EXIT END;
    Color(7);
    FOR c:= x TO (x + w - 1) DO
     WrAt(c, y, ":")
    END;
    INC(x, w)
   END
  END;
  Create(ASC, 1, 5, 1, 0, "=");
  Create(PLAT, 70,  12, 1, 0, "T");
  ppos[0].x:= 70; ppos[0].y:= 17;
  ppos[1].x:= 70; ppos[1].y:= 12;
  pcount:= 1;
  Color(7);
  c:= (Rnd() MOD 4) * 20 + 1;
  Put("8", c, 2, 1, 69, 18);
  Color(2);
  Put(".", 40, 1, 1, 70, 9);
  Put("%", 20, 1, 1, 70, 15);
  Put("@", 4, 1, 1, 70, 9);
  DrawK0(1, 1, 70, 10, 2);
  PutObj(GN2, pvie, 1, 1, 70, 18, 1, 0, "£");
  PutObj(GN1, 10, 50, 1, 70, 18, 3, 0, "<");
  PutObj(PIC, 16, 1, 1, 70, 9, pvie DIV 2 + 1, 2, "V");
  ax:= 70; ay:= 1;
 END BonusLevel8;

 PROCEDURE BonusLevelA(VAR ax, ay: INT8);
  (* Fouillis *)
  VAR n: INT8;
 BEGIN
  Cadre(TRUE);
  ax:= 70; ay:= 18;
  Create(PLAYER, 1, 1, pvie, 0, "*");
  DrawK0(40, 1, 70, 18, 2);
  Color(7); Put("S", 150, 1, 1, 70, 18);
  Put(":", 80, 1, 1, 70, 18);
  Put("/", 10, 1, 1, 70, 18); Put("\", 10, 1, 1, 70, 18);
  Color(6); Put("H", 30, 1, 1, 70, 18);
  FOR n:= 2 TO 8 DO WrAt(1, n, " ") END;
  Color(2); Put(".", 120, 1, 1, 70, 18);
  Put("%", 8, 1, 1, 70, 18); Put("@", 3, 36, 1, 70, 18);
  PutObj(TPLAT, 4, 1, 1, 70, 18, 1, 0, " ");
  PutObj(GN2, pvie, 1, 1, 70, 18, 1, 0, "£");
  PutObj(GN1, 6, 36, 1, 70, 18, 3, 0, "<");
  PutObj(PIC, 12, 1, 1, 70, 18, 3, 2, "V");
  PutObj(NID, 4, 1, 1, 70, 18, 2, 0, "Z")
 END BonusLevelA;

 PROCEDURE BonusLevelB(VAR ax, ay: INT8);
  (* H et = *)
  VAR
   x, y, c: INT16;
 BEGIN
  ax:= 70; ay:= 14;
  Create(PLAYER, 1, 5, pvie, 0, "*");
  Create(GN1, 1, 15, 3, 0, ">");
  Color(6);
  FOR y:= 0 TO 19 DO
   WrAt(0, y, "H");
   WrAt(71, y, "H")
  END;
  FOR x:= 1 TO 70 DO
   c:= Rnd() MOD 4;
   FOR y:= 0 TO c DO WrAt(x, y, "H") END;
   c:= Rnd() MOD 4;
   FOR y:= 19 - c TO 19 DO WrAt(x, y, "H") END
  END;
  PutObj(GN2, pvie DIV 4, 1, 1, 70, 18, 1, 0, "£");
  FOR x:= 3 TO 66 BY 7 DO
   Create(ASC, x, 5 + Rnd() MOD 8, 1, 0, "=")
  END;
  Create(TPLAT, 70, 10, 1, 0, " ");
  Color(2); WrAt(70, 5, "$");
  WrAt(1, 6, "."); WrAt(1, 7, ".")
 END BonusLevelB;

 PROCEDURE BonusLevelC(VAR ax, ay: INT8);
  (* 8 *)
 BEGIN
  Cadre(TRUE);
  Color(2);
  Put("%", 8, 5, 1, 66, 18);
  Put("@", 4, 36, 1, 66, 18);
  PutObj(GN2, pvie * 2, 5, 1, 66, 18, 1, 0, "£");
  PutObj(NID, 5, 5, 1, 66, 18, 2, 0, "Z");
  PutObj(GN1, 2, 1, 1, 4, 18, 3, 0, ">");
  PutObj(GN1, 2, 67, 1, 70, 18, 1, 0, "<");
  DrawK0(5, 1, 66, 18, 2);
  Color(7);
  Put("8", 600, 5, 1, 66, 18);
  ax:= 70; ay:= 18;
  Create(PLAYER, 1, 1, pvie, 0, "*")
 END BonusLevelC;

 PROCEDURE BonusLevel(VAR ax, ay: INT8);
  VAR mod: CARD8;
 BEGIN
  clock0:= AllocTime(300);
  mod:= score MOD 8;
  IF (pvie > 10) AND (score MOD 9 = 0) THEN
   BonusLevelB(ax, ay)
  ELSIF (pvie > 25) AND (score MOD 5 = 0) THEN
   BonusLevelC(ax, ay)
  ELSIF (pvie > 25) AND (score MOD 4 = 0) THEN
   BonusLevelA(ax, ay)
  ELSIF mod = 1 THEN
   BonusLevel1(ax, ay)
  ELSIF mod = 2 THEN
   IF score >= 5000 THEN
    BonusLevel2(ax, ay)
   ELSE
    BonusLevel1(ax, ay)
   END
  ELSIF mod = 3 THEN
   BonusLevel3(ax, ay)
  ELSIF mod = 4 THEN
   BonusLevel4(ax, ay)
  ELSIF mod = 5 THEN
   BonusLevel5(ax, ay)
  ELSIF mod = 6 THEN
   BonusLevel6(ax, ay)
  ELSIF mod = 7 THEN
   BonusLevel7(ax, ay)
  ELSE
   BonusLevel8(ax, ay)
  END;
  FreeTime(clock0)
 END BonusLevel;

END GrotteBonus.
