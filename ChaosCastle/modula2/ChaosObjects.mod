IMPLEMENTATION MODULE ChaosObjects;

 FROM SYSTEM IMPORT ADR;
 FROM Memory IMPORT CARD8, CARD16, INT16, CARD32, INT32, First, Next, Tail;
 FROM Trigo IMPORT RND, SGN, SQRT, SIN, COS;
 FROM ChaosBase IMPORT Anims, Obj, ObjPtr, AnimAlienSet, mainPlayer, pLife,
  Frac, animList, powerCountDown;
 FROM ChaosGraphics IMPORT castle, castleWidth, castleHeight, gameWidth, mulS,
  gameHeight, NbWall, NbBackground, NbClear, BW, BH, PW, PH, backpx, backpy;
 FROM ChaosActions IMPORT CreateObj;
 FROM ChaosBonus IMPORT Money, Moneys, TimedBonus, tbDBSpeed, tbSGSpeed,
  tbMagnet, tbInvinsibility, tbSleeper, tbBullet, tbBonusLevel, tbHospital,
  tbFreeFire, tbMaxPower, tbNoMissile, tbDifficulty, tbExit, tbBomb, MoneySet;
 FROM ChaosSmartBonus IMPORT sbExtraLife, sbExtraPower;
 FROM ChaosAlien IMPORT aDbOval, aCartoon, aSmallDrawer, aBigDrawer,
  aHospital, aDiese, aKamikaze, aStar, aBubble, aBumper, aPic, aTri, aTrefle,
  aBig, aSquare, aFlame, aColor;
 FROM ChaosCreator IMPORT cAlienV, cAlienA, cCreatorR, cCreatorC, cCircle,
  cController, cChief, cFour, cQuad, cAlienBox, cNest, cGrid, cPopUp, cGhost;
 FROM ChaosDObj IMPORT doCartoon, doMagnetR, doMagnetA, doSand, doMirror,
  doWindMaker, doBubbleMaker, doFireMaker, doFireWall;
 FROM ChaosMachine IMPORT mTraverse, mCannon1, mCannon2, mCannon3, mTurret,
  mReactor, mDoor;


 PROCEDURE Rnd(range: INT16): INT16;
  VAR
   mod: CARD16;
 BEGIN
  mod:= range;
  RETURN RND() MOD mod
 END Rnd;

 PROCEDURE ExpRandom(range: INT16): INT16;
  VAR
   cnt: INT16;
 BEGIN
  cnt:= range;
  WHILE (cnt > 2) AND (RND() MOD 16 < 7) DO cnt:= cnt DIV 2 END;
  RETURN Rnd(cnt)
 END ExpRandom;

 PROCEDURE Set(x, y: INT16);
 BEGIN
  castle^[y, x]:= NbBackground
 END Set;

 PROCEDURE Reset(x, y: INT16);
 BEGIN
  castle^[y, x]:= 0
 END Reset;

 PROCEDURE Get(x, y: INT16): CARD8;
 BEGIN
  RETURN castle^[y, x] MOD 64
 END Get;

 PROCEDURE Put(x, y: INT16; v: CARD8);
 BEGIN
  castle^[y, x]:= v
 END Put;

 PROCEDURE Mark(x, y: INT16);
  VAR
   v: POINTER TO CARD8;
 BEGIN
  v:= ADR(castle^[y, x]);
  IF v^ < 64 THEN INC(v^, 64) END
 END Mark;

 PROCEDURE Marked(x, y: INT16): BOOLEAN;
 BEGIN
  RETURN castle^[y, x] >= 64
 END Marked;

 PROCEDURE FlushMarks;
  VAR
   v: POINTER TO CARD8;
   x, y: INT16;
 BEGIN
  FOR y:= 0 TO castleHeight - 1 DO
   FOR x:= 0 TO castleWidth - 1 DO
    v:= ADR(castle^[y, x]);
    v^:= v^ MOD 64
   END
  END
 END FlushMarks;

 PROCEDURE Clear(w, h: INT16);
 BEGIN
  castleWidth:= w; castleHeight:= h;
  gameWidth:= w * BW; gameHeight:= h * BH
 END Clear;

 PROCEDURE Cadre(w, h: INT16);
  VAR
   i: INT16;
 BEGIN
  castleWidth:= w; castleHeight:= h;
  gameWidth:= w * BW; gameHeight:= h * BH;
  DEC(w); DEC(h);
  FOR i:= 0 TO w DO
   Set(i, 0); Set(i, h)
  END;
  FOR i:= 0 TO h DO
   Set(0, i); Set(w, i)
  END
 END Cadre;

(* Filling procs *)

 PROCEDURE All(px, py: INT16): BOOLEAN;
 BEGIN
  RETURN TRUE
 END All;

 PROCEDURE OnlyBackground(px, py: INT16): BOOLEAN;
 BEGIN
  RETURN Get(px, py) < NbClear
 END OnlyBackground;

 PROCEDURE OnlyWall(px, py: INT16): BOOLEAN;
 BEGIN
  RETURN Get(px, py) >= NbBackground
 END OnlyWall;

 VAR
  onlyValue: CARD8;

 PROCEDURE SetOnlyValue(val: CARD8);
 BEGIN
  onlyValue:= val
 END SetOnlyValue;

 PROCEDURE OnlyValue(px, py: INT16): BOOLEAN;
 BEGIN
  RETURN Get(px, py) = onlyValue
 END OnlyValue;

 PROCEDURE Fill(sx, sy, ex, ey: INT16; val: CARD8);
  VAR
   x, y: INT16;
 BEGIN
  FOR y:= sy TO ey DO
   FOR x:= sx TO ex DO
    Put(x, y, val)
   END
  END
 END Fill;

 PROCEDURE FillCond(sx, sy, ex, ey: INT16; Filter: FilterProc; val: CARD8);
  VAR
   x, y: INT16;
 BEGIN
  FOR y:= sy TO ey DO
   FOR x:= sx TO ex DO
    IF Filter(x, y) THEN
     Put(x, y, val)
    END
   END
  END
 END FillCond;

 PROCEDURE FillRandom(sx, sy, ex, ey: INT16; min, max: INT16;
                      Filter: FilterProc; Random: RandomProc);
  VAR
   x, y: INT16;
   range: CARD16;
 BEGIN
  range:= max - min + 1;
  FOR y:= sy TO ey DO
   FOR x:= sx TO ex DO
    IF Filter(x, y) THEN
     Put(x, y, Random(range) + min)
    END
   END
  END
 END FillRandom;

 PROCEDURE FillChoose(sx, sy, ex, ey: INT16; Filter: FilterProc; Choose: ChooseProc);
  VAR
   x, y: INT16;
 BEGIN
  FOR y:= sy TO ey DO
   FOR x:= sx TO ex DO
    IF Filter(x, y) THEN
     Put(x, y, Choose(x, y))
    END
   END
  END
 END FillChoose;

 PROCEDURE PutRandom(sx, sy, ex, ey: INT16; Filter: FilterProc; val, cnt: CARD8);
  VAR
   x, y, w, h: INT16;
   timeOut: CARD16;
 BEGIN
  w:= ex - sx + 1;
  h:= ey - sy + 1;
  WHILE cnt > 0 DO
   timeOut:= 50;
   REPEAT
    DEC(timeOut);
    x:= Rnd(w) + sx;
    y:= Rnd(h) + sy
   UNTIL (timeOut = 0) OR Filter(x, y);
   IF timeOut <> 0 THEN Put(x, y, val) END;
   DEC(cnt)
  END
 END PutRandom;

(* Objects creation *)

 VAR
  sx, sy, ex, ey: INT16;

 PROCEDURE Rect(nsx, nsy, nex, ney: INT16);
 BEGIN
  sx:= nsx; sy:= nsy;
  ex:= nex; ey:= ney
 END Rect;

 PROCEDURE PutObj(kind: Anims; subKind: CARD8; stat: CARD16; px, py: INT16);
  VAR
   obj: ObjPtr;
 BEGIN
  IF kind IN AnimAlienSet THEN
   obj:= CreateObj(kind, subKind, px, py, 0, stat)
  ELSE
   obj:= CreateObj(kind, subKind, px, py, stat, 0)
  END;
  IF obj = NIL THEN HALT END;
  IF kind = PLAYER THEN mainPlayer:= obj END
 END PutObj;

 PROCEDURE PutBlockObj(kind: Anims; subKind: CARD8; stat: CARD16; px, py: INT16);
 BEGIN
  IF Get(px, py) < NbBackground THEN
   Mark(px, py);
   PutObj(kind, subKind, stat, px * BW + BW DIV 2, py * BH + BH DIV 2)
  END
 END PutBlockObj;

 PROCEDURE PutFineObj(kind: Anims; subKind: CARD8; stat: CARD16; px, py, dx, dy: INT16);
  VAR
   x, y: INT16;
 BEGIN
  Mark(px, py);
  x:= px * BW + BW DIV 4 + (BW DIV 2) * dx;
  y:= py * BH + BH DIV 4 + (BH DIV 2) * dy;
  PutObj(kind, subKind, stat, x, y)
 END PutFineObj;

 PROCEDURE Put4Objs(kind: Anims; subKind: CARD8; stat: CARD16; px, py: INT16);
 BEGIN
  PutFineObj(kind, subKind, stat, px, py, 0, 0);
  PutFineObj(kind, subKind, stat, px, py, 0, 1);
  PutFineObj(kind, subKind, stat, px, py, 1, 0);
  PutFineObj(kind, subKind, stat, px, py, 1, 1)
 END Put4Objs;

 PROCEDURE PutRandomObjs(kind: Anims; subKind: CARD8; stat: CARD16; count: CARD16);
  VAR
   x, y, w, h: INT16;
   timeOut: CARD16;
 BEGIN
  w:= ex - sx + 1;
  h:= ey - sy + 1;
  WHILE count > 0 DO
   timeOut:= 50;
   REPEAT
    DEC(timeOut);
    x:= Rnd(w) + sx;
    y:= Rnd(h) + sy
   UNTIL ((Get(x, y) < NbClear) AND (NOT Marked(x, y))) OR (timeOut = 0);
   PutBlockObj(kind, subKind, stat, x, y);
   DEC(count)
  END
 END PutRandomObjs;

 PROCEDURE FindIsolatedPlace(mxw: CARD16; VAR x, y: INT16): BOOLEAN;
  VAR
   dx, dy, w, h, px, py: INT16;
   timeOut, walls: CARD16;
 BEGIN
  w:= ex - sx + 1;
  h:= ey - sy + 1;
  timeOut:= 50;
  REPEAT
   DEC(timeOut);
   x:= Rnd(w) + sx;
   y:= Rnd(h) + sy;
   walls:= 0;
   FOR dy:= -1 TO 1 DO
    FOR dx:= -1 TO 1 DO
     px:= x + dx; py:= y + dy;
     IF (Get(px, py) >= NbBackground) OR Marked(px, py) THEN INC(walls) END
    END
   END
  UNTIL ((walls <= mxw) AND (Get(x, y) < NbClear) AND (NOT Marked(x, y))) OR (timeOut = 0);
  RETURN timeOut <> 0
 END FindIsolatedPlace;

 PROCEDURE PutIsolated(mnc, mxc: CARD16; mns, mxs: INT16; val: CARD8);
  VAR
   x, y, nsx, nsy, sz: INT16;
   count: CARD16;
 BEGIN
  sz:= Rnd(mxs - mns + 1) + mns;
  nsx:= Rnd(ex - sx - sz + 2) + sx;
  nsy:= Rnd(ey - sy - sz + 2) + sy;
  Rect(nsx, nsy, nsx + sz - 1, nsy + sz - 1);
  count:= RND() MOD (mxc - mnc + 1) + mnc;
  WHILE count > 0 DO
   IF FindIsolatedPlace(0, x, y) THEN
    Put(x, y, val)
   END;
   DEC(count)
  END;
 END PutIsolated;

 PROCEDURE PutIsolatedObjs(kind: Anims; subKind: CARD8; mns, mxs: CARD16; mxw, count: CARD16);
  VAR
   x, y: INT16;
   stat: CARD16;
 BEGIN
  WHILE count > 0 DO
   IF FindIsolatedPlace(mxw, x, y) THEN
    stat:= RND() MOD (mxs - mns + 1) + mns;
    Mark(x, y);
    x:= x * BW + BW DIV 4 + Rnd(BW DIV 2);
    y:= y * BH + BH DIV 4 + Rnd(BH DIV 2);
    PutObj(kind, subKind, stat, x, y)
   END;
   DEC(count)
  END
 END PutIsolatedObjs;

 PROCEDURE PutDeltaObjs(kind: Anims; subKind: CARD8; stat: CARD16; dx, dy: INT16; count: CARD16);
  VAR
   lx, ly, x, y, w, h: INT16;
   timeOut: CARD16;
 BEGIN
  w:= ex - sx + 1;
  h:= ey - sy + 1;
  WHILE count > 0 DO
   timeOut:= 50;
   REPEAT
    DEC(timeOut);
    x:= Rnd(w) + sx;
    y:= Rnd(h) + sy
   UNTIL ((Get(x, y) < NbClear) AND NOT Marked(x, y)) OR (timeOut = 0);
   IF timeOut <> 0 THEN
    REPEAT
     lx:= x; ly:= y;
     INC(x, dx); INC(y, dy)
    UNTIL (Get(x, y) >= NbClear) OR Marked(x, y);
    PutBlockObj(kind, subKind, stat, lx, ly)
   END;
   DEC(count)
  END
 END PutDeltaObjs;

 PROCEDURE PutGridObjs(kind: Anims; subKind: CARD8; stat: CARD16; sx, sy, dx, dy, cx, cy: INT16);
  VAR
   x, y: INT16;
 BEGIN
  FOR y:= 0 TO cy DO
   FOR x:= 0 TO cx DO
    PutBlockObj(kind, subKind, stat, sx + x * dx, sy + y * dy)
   END
  END
 END PutGridObjs;

 PROCEDURE PutRndStatObjs(kind: Anims; subKind: CARD8; mnstat, mxstat: CARD16; count: CARD16);
  VAR
   stat: CARD16;
 BEGIN
  WHILE count > 0 DO
   stat:= mnstat + RND() MOD (mxstat - mnstat + 1);
   PutRandomObjs(kind, subKind, stat, 1);
   DEC(count)
  END
 END PutRndStatObjs;

 PROCEDURE PutChaosObjs(kind: Anims; subKind: CARD8; stat: CARD16; sx, sy, ex, ey: INT16; count: CARD16);
  VAR
   x, y, w, h: INT16;
   timeOut: CARD16;
 BEGIN
  w:= ex - sx + 1;
  h:= ey - sy + 1;
  WHILE count > 0 DO
   timeOut:= 50;
   REPEAT
    DEC(timeOut);
    x:= Rnd(w) + sx;
    y:= Rnd(h) + sy
   UNTIL (Get(x DIV BW, y DIV BH) < NbClear) OR (timeOut = 0);
   IF timeOut <> 0 THEN PutObj(kind, subKind, stat, x, y) END;
   DEC(count)
  END
 END PutChaosObjs;

 PROCEDURE PutChaosChain(kind: Anims; subKind: CARD8; VAR start: CARD16;
                    step: CARD16; sx, sy, ex, ey: INT16; count: CARD16);
 BEGIN
  WHILE count > 0 DO
   PutChaosObjs(kind, subKind, start, sx, sy, ex, ey, 1);
   DEC(start, step);
   DEC(count)
  END
 END PutChaosChain;

 PROCEDURE FillObj(kind: Anims; subKind: CARD8; stat: CARD16; sx, sy, ex, ey: INT16; fine: BOOLEAN);
  TYPE
   PutObjProc = PROCEDURE(Anims, CARD8, CARD16, INT16, INT16);
  VAR
   PutIt: PutObjProc;
   x, y: INT16;
 BEGIN
  IF fine THEN PutIt:= Put4Objs ELSE PutIt:= PutBlockObj END;
  FOR y:= sy TO ey DO
   FOR x:= sx TO ex DO
    PutIt(kind, subKind, stat, x, y)
   END
  END
 END FillObj;

(* Specific obj creation *)

 PROCEDURE PutPlayer(px, py: INT16);
 BEGIN
  IF pLife = 0 THEN pLife:= 1 END;
  PutBlockObj(PLAYER, 0, 0, px, py)
 END PutPlayer;

 PROCEDURE PutExit(px, py: INT16);
 BEGIN
  PutBlockObj(BONUS, TimedBonus, tbExit, px, py)
 END PutExit;

 PROCEDURE PutKamikaze(stat, count: CARD16);
  VAR
   dx, dy: INT16;
 BEGIN
  CASE stat MOD 4 OF
   0: dx:= -1; dy:= -1
  |1: dx:= 1; dy:= -1
  |2: dx:= -1; dy:= 1
  |3: dx:= 1; dy:= 1
  END;
  PutDeltaObjs(ALIEN1, aKamikaze, stat, dx, dy, count)
 END PutKamikaze;

 PROCEDURE PutPic(stat, count: CARD16);
  VAR
   dx: INT16;
 BEGIN
  IF ODD(stat) THEN dx:= 1 ELSE dx:= -1 END;
  PutDeltaObjs(ALIEN1, aPic, stat, dx, 0, count)
 END PutPic;

 PROCEDURE PutBlockBonus(stat: CARD16; px, py: INT16);
 BEGIN
  PutBlockObj(BONUS, TimedBonus, stat, px, py)
 END PutBlockBonus;

 PROCEDURE PutTBonus(stat, count: CARD16);
 BEGIN
  PutRandomObjs(BONUS, TimedBonus, stat, count)
 END PutTBonus;

 PROCEDURE PutHospital(count: CARD16);
 BEGIN
  PutTBonus(tbHospital, count)
 END PutHospital;

 PROCEDURE PutBullet(count: CARD16);
 BEGIN
  PutTBonus(tbBullet, count)
 END PutBullet;

 PROCEDURE PutMagnet(count: CARD16);
 BEGIN
  PutTBonus(tbMagnet, count)
 END PutMagnet;

 PROCEDURE PutSleeper(count: CARD16);
 BEGIN
  PutTBonus(tbSleeper, count)
 END PutSleeper;

 PROCEDURE PutInvinsibility(count: CARD16);
 BEGIN
  PutTBonus(tbInvinsibility, count)
 END PutInvinsibility;

 PROCEDURE PutFreeFire(count: CARD16);
 BEGIN
  PutTBonus(tbFreeFire, count)
 END PutFreeFire;

 PROCEDURE PutMaxPower(count: CARD16);
 BEGIN
  PutTBonus(tbMaxPower, count)
 END PutMaxPower;

 PROCEDURE PutChaosSterling(count: CARD16);
 BEGIN
  PutChaosObjs(BONUS, Money, ORD(st), sx * BW, sy * BH, ex * BW, ey * BH, count)
 END PutChaosSterling;

 PROCEDURE PutMoney(which: MoneySet; count: CARD16);
  VAR
   money: Moneys;
   c: CARD16;
 BEGIN
  WHILE count > 0 DO
   REPEAT
    c:= RND() MOD 6;
    money:= MIN(Moneys);
    WHILE c > 0 DO DEC(c); INC(money) END
   UNTIL money IN which;
   PutRandomObjs(BONUS, Money, ORD(money), 1);
   DEC(count)
  END
 END PutMoney;

 PROCEDURE PutExtraPower(min: CARD8; px, py: INT16);
 BEGIN
  IF powerCountDown > min THEN
   PutBlockObj(SMARTBONUS, sbExtraPower, 0, px, py)
  END
 END PutExtraPower;

 PROCEDURE PutExtraLife(px, py: INT16);
 BEGIN
  PutBlockObj(SMARTBONUS, sbExtraLife, 0, px, py)
 END PutExtraLife;

 PROCEDURE PutRAlien1(subK, mins, maxs, count: CARD16);
 BEGIN
  PutRndStatObjs(ALIEN1, subK, mins, maxs, count)
 END PutRAlien1;

 PROCEDURE PutAlien1(subK, stat, count: CARD16);
 BEGIN
  PutRandomObjs(ALIEN1, subK, stat, count)
 END PutAlien1;

 PROCEDURE PutAColor(mins, maxs, count: CARD16);
 BEGIN
  PutRAlien1(aColor, mins, maxs, count)
 END PutAColor;

 PROCEDURE PutColor(stat, count: CARD16);
 BEGIN
  PutAlien1(aColor, stat, count)
 END PutColor;

 PROCEDURE PutTrefle(stat, count: CARD16);
 BEGIN
  PutAlien1(aTrefle, stat, count)
 END PutTrefle;

 PROCEDURE PutTri(stat, count: CARD16);
 BEGIN
  PutAlien1(aTri, stat, count)
 END PutTri;

 PROCEDURE PutCartoon(mins, maxs, count: CARD16);
 BEGIN
  PutRAlien1(aCartoon, mins, maxs, count)
 END PutCartoon;

 PROCEDURE PutRAlien2(subK, mins, maxs, count: CARD16);
 BEGIN
  PutRndStatObjs(ALIEN2, subK, mins, maxs, count)
 END PutRAlien2;

 PROCEDURE PutAlien2(subK, stat, count: CARD16);
 BEGIN
  PutRandomObjs(ALIEN2, subK, stat, count)
 END PutAlien2;

 PROCEDURE PutCFour(mins, maxs, count: CARD16);
 BEGIN
  PutRAlien2(cFour, mins, maxs, count)
 END PutCFour;

 PROCEDURE PutFour(stat, count: CARD16);
 BEGIN
  PutAlien2(cFour, stat, count)
 END PutFour;

 PROCEDURE PutQuad(stat, count: CARD16);
 BEGIN
  PutAlien2(cQuad, stat, count)
 END PutQuad;

 PROCEDURE PutABox(stat, count: CARD16);
 BEGIN
  PutAlien2(cAlienBox, stat, count)
 END PutABox;

 PROCEDURE PutNest(stat, count: CARD16);
 BEGIN
  PutAlien2(cNest, stat, count)
 END PutNest;

 PROCEDURE PutCreatorR(count: CARD16);
 BEGIN
  PutAlien2(cCreatorR, 80, count)
 END PutCreatorR;

 PROCEDURE PutCreatorC(count: CARD16);
 BEGIN
  PutAlien2(cCreatorC, 80, count)
 END PutCreatorC;

 PROCEDURE PutDeadObj(subK, stat, count: CARD16);
 BEGIN
  PutRandomObjs(DEADOBJ, subK, stat, count)
 END PutDeadObj;

 PROCEDURE PutBubbleMaker(stat: CARD16; px, py: INT16);
 BEGIN
  PutBlockObj(DEADOBJ, doBubbleMaker, stat, px, py)
 END PutBubbleMaker;

 PROCEDURE PutMagnetR(maxs, count: CARD16);
 BEGIN
  PutIsolatedObjs(DEADOBJ, doMagnetR, 0, maxs, 1, count)
 END PutMagnetR;

 PROCEDURE PutMagnetA(maxs, count: CARD16);
 BEGIN
  PutIsolatedObjs(DEADOBJ, doMagnetA, 0, maxs, 1, count)
 END PutMagnetA;

 PROCEDURE PutMachine(subK, mins, maxs, count: CARD16);
 BEGIN
  PutRndStatObjs(MACHINE, subK, mins, maxs, count)
 END PutMachine;

 PROCEDURE PutCannon3(count: CARD16);
 BEGIN
  PutMachine(mCannon3, 0, 0, count)
 END PutCannon3;

 PROCEDURE PutTurret(count: CARD16);
 BEGIN
  PutMachine(mTurret, 0, 0, count)
 END PutTurret;

END ChaosObjects.
