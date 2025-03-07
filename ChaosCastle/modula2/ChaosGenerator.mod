IMPLEMENTATION MODULE ChaosGenerator;

 FROM Memory IMPORT CARD8, CARD16, INT16, CARD32, INT32, First, Next, Tail;
 FROM Trigo IMPORT RND, SGN, SQRT, SIN, COS;
 FROM ChaosBase IMPORT Anims, Obj, ObjPtr, AnimAlienSet, mainPlayer, pLife,
  Frac, animList;
 FROM ChaosGraphics IMPORT castle, castleWidth, castleHeight, gameWidth, mulS,
  gameHeight, NbWall, NbBackground, NbClear, BW, BH, PW, PH, backpx, backpy,
  dualpf;
 FROM ChaosObjects IMPORT Set, Reset, Fill, Rnd, Put, Get, PutBlockObj,
  FillCond, OnlyWall, PutExit;
 FROM ChaosAlien IMPORT aKamikaze, aPic;
 FROM ChaosMachine IMPORT mCannon2, mCannon1, mTraverse;


(* Construction support *)

 PROCEDURE DrawPacman(open: CARD16; width, height, sx, sy, ex, ey: INT16);
  VAR
   x, y, v, h, z: INT16;
 BEGIN
  y:= ey;
  WHILE y >= sy DO
   x:= ex;
   WHILE x >= sx DO
    Set(x, y);
    IF RND() MOD 16 > open THEN
     h:= RND() MOD 2;
     v:= 1 - h;
     FOR z:= 1 TO width * h + height * v - 1 DO
      Set(x + h * z, y + v * z)
     END
    END;
    DEC(x, width)
   END;
   DEC(y, height)
  END
 END DrawPacman;

 PROCEDURE Road(sx, sy, ex, ey, sz: INT16; val: CARD8);
  VAR
   w, h, m, c, x, y: INT16;
 BEGIN
  w:= ABS(ex - sx); h:= ABS(ey - sy); c:= 0;
  IF w > h THEN m:= w ELSE m:= h END;
  w:= ex - sx; h:= ey - sy;
  WHILE c < m DO
   x:= sx + w * c DIV m;
   y:= sy + h * c DIV m;
   Fill(x - sz, y - sz, x + sz, y + sz, val);
   INC(c)
  END
 END Road;

 PROCEDURE Excavate(sx, ex, mny, mxy, mnh, mxh, sdy, sh, sd, mxsy, mxsh, mnd, mxd: INT16);
  VAR
   x, y, py, h, d, dw, syw, shw: INT16;
 BEGIN
  x:= sx; y:= mny + sdy; h:= sh; d:= sd;
  dw:= mxd - mnd + 1;
  syw:= mxsy * 2 + 1;
  shw:= mxsh * 2 + 1;
  WHILE x <= ex DO
   IF d = 0 THEN
    d:= Rnd(mxd - mnd + 1) + mnd;
    INC(h, Rnd(shw) - mxsh);
    IF h < mnh THEN h:= mnh ELSIF h > mxh THEN h:= mxh END;
    INC(y, Rnd(syw) - mxsy);
    IF y < mny THEN y:= mny ELSIF y + h > mxy THEN y:= mxy - h + 1 END
   END;
   py:= y + h;
   WHILE py <> y DO
    DEC(py);
    Put(x, py, 0)
   END;
   INC(x); DEC(d)
  END
 END Excavate;

 PROCEDURE PutCross(bx, by, w, h: INT16; val: CARD8);
  VAR
   x, y, s, z: INT16;

  PROCEDURE CheckPlace(): BOOLEAN;
   VAR
    px, py: INT16;
  BEGIN
   IF (x - s - 1 <= bx) AND (y - s - 1 <= by) THEN RETURN FALSE END;
   IF (x + s + 2 >= bx + w) AND (y + s + 2 >= by + h) THEN RETURN FALSE END;
   FOR px:= x - s - 1 TO x + s + 1 DO
    FOR py:= y - 1 TO y + 1 DO
     IF Get(px, py) >= NbBackground THEN RETURN FALSE END
    END
   END;
   FOR py:= y - s - 1 TO y + s + 1 DO
    FOR px:= x - 1 TO x + 1 DO
     IF Get(px, py) >= NbBackground THEN RETURN FALSE END
    END
   END;
   RETURN TRUE
  END CheckPlace;

  PROCEDURE FindPlace(): BOOLEAN;
   VAR
    timeOut: CARD16;
  BEGIN
   timeOut:= 50;
   REPEAT
    DEC(timeOut);
    s:= RND() MOD 4 + 1; z:= 2 * s + 1;
    x:= Rnd(w - z) + bx + s;
    y:= Rnd(h - z) + by + s;
    IF CheckPlace() THEN RETURN TRUE END
   UNTIL timeOut = 0;
   RETURN FALSE
  END FindPlace;

  PROCEDURE PutIt;
   VAR
    pz: INT16;
  BEGIN
   FOR pz:= x - s TO x + s DO Put(pz, y, val) END;
   FOR pz:= y - s TO y + s DO Put(x, pz, val) END
  END PutIt;

 BEGIN
  IF FindPlace() THEN PutIt END
 END PutCross;

 PROCEDURE CheckLink(x, y, sz, ml: INT16; VAR angle: INT16): BOOLEAN;
  VAR
   timeOut: CARD16;
   length, px, py, dx, dy: INT16;
 BEGIN
  angle:= (RND() MOD 4) * 90;
  timeOut:= 4;
  REPEAT
   dx:= COS(angle) DIV 1024;
   dy:= SIN(angle) DIV 1024;
   px:= x + dx * (sz + 1); py:= y + dy * (sz + 1);
   length:= 0;
   WHILE (length <= ml) AND (Get(px, py) >= NbBackground) DO
    INC(px, dx); INC(py, dy); INC(length);
    IF (px < 0) OR (py < 0) OR (px >= castleWidth) OR (py >= castleHeight) THEN
     length:= ml + 1
    END
   END;
   IF length <= ml THEN RETURN TRUE END;
   angle:= (angle + 90) MOD 360; DEC(timeOut)
  UNTIL timeOut = 0;
  RETURN FALSE
 END CheckLink;

 PROCEDURE FindIsolatedRect(sx, sy, ex, ey, sz, ml: INT16; VAR angle, x, y: INT16; wall: BOOLEAN): BOOLEAN;
  VAR
   dx, dy, w, h: INT16;
   timeOut, backs: CARD16;
   res: BOOLEAN;
 BEGIN
  w:= ex - sx - sz * 2 - 1; INC(sx, sz + 1);
  h:= ey - sy - sz * 2 - 1; INC(sy, sz + 1);
  timeOut:= 20;
  REPEAT
   DEC(timeOut);
   x:= Rnd(w) + sx;
   y:= Rnd(h) + sy;
   backs:= 0;
   FOR dy:= -sz - 1 TO sz + 1 DO
    FOR dx:= -sz - 1 TO sz + 1 DO
     res:= Get(x + dx, y + dy) < NbBackground;
     IF res = wall THEN INC(backs) END
    END
   END;
   IF (backs = 0) AND wall AND NOT(CheckLink(x, y, sz, ml, angle)) THEN backs:= 1 END
  UNTIL (backs = 0) OR (timeOut = 0);
  RETURN backs = 0
 END FindIsolatedRect;

 PROCEDURE MakeLink(x, y, sz, angle: INT16; val: CARD8);
  VAR
   px, py, dx, dy: INT16;
 BEGIN
  dx:= COS(angle) DIV 1024; dy:= SIN(angle) DIV 1024;
  px:= x + dx * (sz + 1); py:= y + dy * (sz + 1);
  WHILE Get(px, py) >= NbBackground DO
   Put(px, py, val); INC(px, dx); INC(py, dy)
  END
 END MakeLink;

 PROCEDURE FillEllipse(sx, sy, pw, ph: INT32; val: CARD8);
  VAR
   by, ey, ex, x, y, w, h, pw2, ph2, w2, phd: INT32;
   odd: BOOLEAN;
 BEGIN
  odd:= ODD(pw); ph2:= ph * ph;
  pw2:= pw * pw;  phd:= ph2 DIV 2;
  h:= - ph + 1; w:= 1; y:= 0;
  REPEAT
   w2:= ((ph2 - h * h) * pw2 + phd) DIV ph2;
   WHILE w * w < w2 DO INC(w) END;
   IF ODD(w) <> odd THEN DEC(w) END;
   x:= sx + (pw - w) DIV 2;
   by:= sy + y; ey:= sy + ph - y - 1; ex:= x + w - 1;
   Fill(x, by, ex, by, val);
   IF h <> 0 THEN Fill(x, ey, ex, ey, val) END;
   INC(y); INC(h, 2)
  UNTIL h > 0
 END FillEllipse;

 PROCEDURE TripleLoop(val: CARD8);
  VAR
   angle, sz, ml, bs, lx, ly: INT16;
   r, x, y: INT32;
 BEGIN
  lx:= 0; ly:= 0;
  ml:= RND() MOD 8 + 1;
  bs:= RND() MOD 360;
  FOR angle:= 0 TO 702 BY 2 DO
   sz:= (SIN(bs + angle * ml) + 1024) DIV 700 + 1;
   r:= SIN(angle * 3 DIV 2);
   x:= COS(angle) * (2048 + r) DIV 54330;
   y:= SIN(angle) * (2048 + r) DIV 54330;
   INC(x, 62); INC(y, 62);
   IF (lx <> 0) AND (ly <> 0) THEN
    Road(lx, ly, x, y, sz, val)
   END;
   lx:= x; ly:= y
  END
 END TripleLoop;

 PROCEDURE GCastle(sx, sy, ex, ey: INT16; wall, back: CARD8);
  VAR
   x, y: INT16;
   c: CARD16;
 BEGIN
  FOR y:= sy TO ey BY 2 DO
   Fill(sx, y, ex, y, wall);
   FOR c:= 1 TO 3 DO
    x:= Rnd(ex - sx + 1) + 1;
    Put(x, y, back)
   END
  END
 END GCastle;

 PROCEDURE Cave(sx, sy, ex, ey, y1, y2, dx: INT16);
  VAR
   x, d1, d2, t1, t2: INT16;
 BEGIN
  x:= sx; d1:= 1; d2:= -1; t1:= 0; t2:= 0;
  REPEAT
   Fill(x, y1, x, y2, 0);
   INC(x, dx);
   IF t1 = 0 THEN
    IF RND() MOD 2 = 0 THEN d1:= 1 ELSE d1:= -1 END;
    t1:= RND() MOD 8 + 3
   ELSE
    DEC(t1)
   END;
   IF t2 = 0 THEN
    IF RND() MOD 2 = 0 THEN d2:= 1 ELSE d2:= -1 END;
    t2:= RND() MOD 8 + 3
   ELSE
    DEC(t2)
   END;
   IF y1 <= sy THEN d1:= 1 END;
   IF y2 >= ey THEN d2:= -1 END;
   IF y2 + d2 - y1 - d1 < 3 THEN
    IF y1 >= sy + 5 THEN d1:= -1 END;
    IF y2 <= ey - 5 THEN d2:= 1 END
   END;
   INC(y1, d1); INC(y2, d2)
  UNTIL x = ex;
  PutExit(12, y1 - d1);
  PutExit(12, y2 - d2)
 END Cave;

 PROCEDURE DrawFactory;
   (* 100 x 100 *)
  VAR
   x, y, dx, dy, mnx, mxx, nxx, l, s, sz, sz2: INT16;
   pass: CARD16;
   val, v2: CARD8;
 BEGIN
  pass:= 0; x:= 12; y:= 90; s:= -1;
  dx:= 0; dy:= -1; l:= 7;
  mnx:= 6; mxx:= 19; nxx:= 31;
  v2:= 20;
  LOOP
   IF l = 0 THEN
    sz:= RND() MOD 4 + 2;
    CASE pass OF
      0: val:= 13
     |1: val:= 8
     |2: val:= 10
     |3: IF RND() MOD 6 = 0 THEN val:= 14 ELSE val:= 20 END
    END;
    IF dualpf THEN v2:= val; val:= 9 END;
    FillCond(x - sz, y - sz, x + sz, y + sz, OnlyWall, val);
    val:= RND() MOD 4 + pass + 25;
    sz2:= Rnd(sz - 1);
    Fill(x - sz2, y - sz2, x + sz2, y + sz2, val);
    IF ((s < 0) AND (y > 10)) OR ((s > 0) AND (y < 90)) THEN
     IF RND() MOD 3 = 0 THEN
      dy:= 0;
      IF RND() MOD 2 = 0 THEN dx:= -1 ELSE dx:= 1 END;
      IF x <= mnx THEN dx:= 1 ELSIF x >= mxx THEN dx:= -1 END
     ELSIF RND() MOD 16 = 0 THEN
      dy:= -s; dx:= 0
     ELSE
      dy:= s; dx:= 0
     END
    ELSE
     mxx:= nxx + 1;
     IF RND() MOD 3 = 0 THEN dy:= -s; dx:= 0 ELSE dx:= 1; dy:= 0 END
    END;
    l:= 6 + RND() MOD 8;
    INC(x, dx * sz2 + dx); INC(y, dy * sz2 + dy)
   END;
   Put(x, y, v2);
   INC(x, dx); INC(y, dy); DEC(l);
   IF y <= 6 THEN y:= 6; l:= 0 ELSIF y >= 94 THEN y:= 94; l:= 0 END;
   IF x <= mnx THEN
    x:= mnx; dx:= 0; dy:= s
   ELSIF x >= mxx THEN
    x:= mxx; dx:= 0
   END;
   IF x >= nxx THEN
    INC(mnx, 25); INC(nxx, 25); mxx:= mnx + 13;
    IF nxx > 95 THEN nxx:= 95 END;
    s:= -s; INC(pass); IF pass > 3 THEN EXIT END
   END
  END;
  MakeLink(12, 90, -1, -90, 20);
  Put(x, y, 11);
  PutExit(x, y)
 END DrawFactory;

 PROCEDURE DrawLabyrinth(size: INT16);
  VAR
   x, y, px, py: INT16;
   flag: BOOLEAN;

  PROCEDURE Cut;
   VAR
    z, v: INT16;
    h: BOOLEAN;
  BEGIN
   h:= (RND() MOD 2 = 0);
   IF h THEN z:= x ELSE z:= y END;
   v:= Rnd(50);
   flag:= (z * size < v * v);
   v:= Rnd(size) - 2 * z;
   Put(px, py, 0);
   IF v >= 0 THEN
    IF h THEN Put(px + 1, py, 0) ELSE Put(px, py + 1, 0) END
   ELSE
    IF h THEN Put(px - 1, py, 0) ELSE Put(px, py - 1, 0) END
   END
  END Cut;

 BEGIN
  py:= 1;
  FOR y:= 0 TO size - 1 DO
   px:= 1;
   FOR x:= 0 TO size - 1 DO
    Cut;
    IF flag THEN Cut END;
    INC(px, 2)
   END;
   INC(py, 2)
  END
 END DrawLabyrinth;

 PROCEDURE RemIsolated(sx, sy, ex, ey, dx, dy: INT16; val: CARD8);
  VAR
   x, y: INT16;
 BEGIN
  y:= sy;
  WHILE y <= ey DO
   x:= sx;
   WHILE x <= ex DO
    IF (Get(x - 1, y) < NbBackground) AND (Get(x + 1, y) < NbBackground) AND
       (Get(x, y - 1) < NbBackground) AND (Get(x, y + 1) < NbBackground) THEN
     Put(x, y, val)
    END;
    INC(x, dx)
   END;
   INC(y, dy)
  END
 END RemIsolated;

 PROCEDURE VRace(val: CARD8);
  CONST
   W = 120; H = 60;
   ST = W * 4 DIV 16;
   SE = W * 4 DIV 150; BE = W * 4 DIV 50;
   MX = W DIV 2; MY = H DIV 2;
   WP = MX - (SE + BE + ST + 15) DIV 4; (* 46 *)
   HP = MY - (SE + BE + ST + 15) DIV 4; (* 16 *)
  VAR
   dx, dy, dl: INT32;
   ap, at, ae, vp, vt, ve, nt, ne, dt, de, x, y, sz, lx, ly: INT16;
 BEGIN
  lx:= 0; ly:= 0;
  dt:= 7 + Rnd(7);
  de:= 4 + Rnd(7);
  vp:= 2; ap:= 0;
  at:= Rnd(180); nt:= at;
  ae:= Rnd(180); ne:= ae;
  REPEAT
   IF at >= nt THEN
    vt:= vp * dt DIV 2 + vp * Rnd(dt);
    INC(nt, Rnd(200))
   END;
   IF ae >= ne THEN
    ve:= vp * de DIV 2 + vp * Rnd(de);
    INC(ne, Rnd(200))
   END;
   x:= MX + SIN(ap) / 2 * WP / 512;
   y:= MY + SIN(ap * 2) / 2 * HP / 512;
   dx:= -COS(ap * 2); dy:= COS(ap) * HP DIV WP;
   dl:= SQRT(dx * dx + dy * dy);
   dx:= dx * 1024 / dl; dy:= dy * 1024 / dl;
   dl:= SIN(at) * ST / 4096;
   INC(x, dl * dx / 1024); INC(y, dl * dy / 1024);
   sz:= (SIN(ae) * SE / 1024 + BE) / 4;
   IF lx = 0 THEN lx:= x; ly:= y END;
   Road(lx, ly, x, y, sz, val);
   lx:= x; ly:= y;
   INC(ap, vp); INC(at, vt); INC(ae, ve)
  UNTIL ap >= 362
 END VRace;

 PROCEDURE DrawCastle(sx, sy, w, h: INT16);

  PROCEDURE Rect(psx, psy, dy, pex, pey: INT16);
   VAR
    x, y, py: INT16;
    val: CARD8;
  BEGIN
   py:= Rnd(3) + psy;
   IF (psx = sx) OR (pex = w) THEN py:= psy END;
   FOR y:= psy + dy TO pey DO
    FOR x:= psx TO pex DO
     IF ((x <> psx) AND (x <> pex) AND (y <> pey)) OR (y = py) THEN
      val:= 0
     ELSE
      val:= NbBackground + 1
     END;
     IF Get(x, y) = 1 THEN Put(x, y, val) END
    END
   END
  END Rect;

  VAR
   x, y, ex, ey, dy: INT16;
   start: BOOLEAN;
 BEGIN
  w:= w + sx - 1; h:= h + sy - 1;
  Fill(sx, sy, w, h, 1);
  y:= sy; start:= TRUE;
  REPEAT
   x:= sx;
   REPEAT
    ex:= Rnd(6) + 4 + x;
    ey:= Rnd(4) + 3 + y;
    IF ex >= w - 4 THEN ex:= w END;
    IF y = sy THEN dy:= -1 ELSE dy:= -6 END;
    Rect(x, y, dy, ex, ey);
    x:= ex - Rnd(3)
   UNTIL x >= w;
   IF start THEN Put(sx - 1, y + 1, NbBackground) ELSE Put(ex + 1, y + 1, NbBackground) END;
   INC(y, 8); start:= NOT(start)
  UNTIL y >= h
 END DrawCastle;

 PROCEDURE DrawBoxes(sx, sy, w, h: INT16);
  VAR
   x, y, minx, miny, maxx, maxy: INT16;
   c: CARD16;
   k: BOOLEAN;
 BEGIN
  maxy:= -1;
  REPEAT
   k:= TRUE;
   miny:= maxy + 2;
   INC(maxy, Rnd(8) + 6);
   IF maxy > h - 8 THEN maxy:= h - 1; Set(sx + 1, sy + maxy) END;
   FOR x:= 2 TO w - 1 DO Set(sx + x, sy + maxy) END;
   DEC(maxy); maxx:= -1;
   REPEAT
    minx:= maxx + 2;
    INC(maxx, Rnd(8) + 6);
    IF maxx > w - 8 THEN maxx:= w - 1 END;
    FOR y:= miny TO maxy DO Set(sx + maxx, sy + y) END;
    IF maxx <> w - 1 THEN
     IF k THEN Reset(sx + maxx, sy + maxy) ELSE Reset(sx + maxx, sy + miny) END
    END;
    DEC(maxx); y:= miny;
    LOOP
     INC(y, Rnd(3) + 3);
     IF y >= maxy THEN EXIT END;
     FOR x:= minx TO maxx DO Set(sx + x, sy + y) END;
     FOR c:= 1 TO 5 DO
      x:= minx + Rnd(maxx - minx + 1);
      Reset(sx + x, sy + y)
     END
    END;
    k:= NOT(k)
   UNTIL maxx >= w - 2
  UNTIL maxy >= h - 2
 END DrawBoxes;

 PROCEDURE Join(sx, sy, ex, ey, dx, dy: INT16; val: CARD8);
  VAR
   x, y: INT16;
 BEGIN
  x:= sx; y:= sy;
  REPEAT
   Put(x, y, val);
   IF x = ex THEN dx:= 0 END;
   IF y = ey THEN dy:= 0 END;
   IF RND() MOD 2 = 0 THEN INC(x, dx) ELSE INC(y, dy) END
  UNTIL (dx = 0) AND (dy = 0)
 END Join;


(* Modifiers *)

 PROCEDURE FlipVert;
  VAR
   obj, tail: ObjPtr;
   gh, ry: INT32;
   x, y, my: INT16;
   a: Anims;
   swp: CARD8;
 BEGIN
  my:= castleHeight - 1;
  gh:= gameHeight; gh:= gh * Frac;
  FOR x:= 0 TO castleWidth - 1 DO
   FOR y:= 0 TO castleHeight DIV 2 - 1 DO
    swp:= castle^[y, x];
    castle^[y, x]:= castle^[my - y, x];
    castle^[my - y, x]:= swp
   END
  END;
  FOR a:= MIN(Anims) TO MAX(Anims) DO
   obj:= First(animList[a]); tail:= Tail(animList[a]);
   WHILE obj <> tail DO
    WITH obj^ DO
     ry:= height DIV mulS;
     y:= gh - y - ry * Frac; midy:= y;
     IF (a = MACHINE) AND (subKind = mCannon2) THEN
      stat:= 1 - stat; attr^.Make(obj)
     ELSIF (a = ALIEN1) AND (subKind = aKamikaze) THEN
      IF stat >= 2 THEN DEC(stat, 2) ELSE INC(stat, 2) END; attr^.Make(obj)
     END;
     obj:= Next(animNode)
    END
   END
  END
 END FlipVert;

 PROCEDURE FlipHorz;
  VAR
   obj, tail: ObjPtr;
   gw, rx: INT32;
   x, y, mx: INT16;
   a: Anims;
   swp: CARD8;
 BEGIN
  mx:= castleWidth - 1;
  gw:= gameWidth; gw:= gw * Frac;
  FOR y:= 0 TO castleHeight - 1 DO
   FOR x:= 0 TO castleWidth DIV 2 - 1 DO
    swp:= castle^[y, x];
    castle^[y, x]:= castle^[y, mx - x];
    castle^[y, mx - x]:= swp
   END
  END;
  FOR a:= MIN(Anims) TO MAX(Anims) DO
   obj:= First(animList[a]); tail:= Tail(animList[a]);
   WHILE obj <> tail DO
    WITH obj^ DO
     rx:= width DIV mulS;
     x:= gw - x - rx * Frac; midx:= x;
     IF (a = MACHINE) AND (subKind = mCannon1) THEN
      stat:= 1 - stat; attr^.Make(obj)
     ELSIF (a = ALIEN1) AND (subKind = aKamikaze) THEN
      IF ODD(stat) THEN DEC(stat) ELSE INC(stat) END; attr^.Make(obj)
     ELSIF (a = ALIEN1) AND (subKind = aPic) THEN
      stat:= 1 - stat; attr^.Make(obj)
     END;
     obj:= Next(animNode)
    END
   END
  END
 END FlipHorz;

 PROCEDURE Rotate;
  VAR
   obj, tail: ObjPtr;
   tmp, rx: INT32;
   x, y, m, t: INT16;
   a: Anims;
   swp: CARD8;
 BEGIN
  IF castleWidth > castleHeight THEN m:= castleWidth - 1 ELSE m:= castleHeight - 1 END;
  FOR y:= 1 TO m DO
   FOR x:= 0 TO y - 1 DO
    swp:= castle^[y, x];
    castle^[y, x]:= castle^[x, y];
    castle^[x, y]:= swp
   END
  END;
  FOR a:= MIN(Anims) TO MAX(Anims) DO
   obj:= First(animList[a]); tail:= Tail(animList[a]);
   WHILE obj <> tail DO
    WITH obj^ DO
     tmp:= x; x:= y; y:= tmp;
     midx:= x; midy:= y;
     rx:= cy - cx;
     INC(x, rx * Frac); DEC(y, rx * Frac);
     IF (a = MACHINE) AND (subKind = mTraverse) THEN
      stat:= 1 - stat; attr^.Reset(obj)
     ELSIF (a = ALIEN1) AND (subKind = aKamikaze) THEN
      IF stat = 1 THEN stat:= 2 ELSIF stat = 2 THEN stat:= 1 END;
      attr^.Make(obj)
     END;
     obj:= Next(animNode)
    END
   END
  END;
  t:= gameWidth; gameWidth:= gameHeight; gameHeight:= t;
  t:= castleWidth; castleWidth:= castleHeight; castleHeight:= t
 END Rotate;

END ChaosGenerator.
