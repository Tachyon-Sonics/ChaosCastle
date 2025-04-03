IMPLEMENTATION MODULE ChaosLevels;

 FROM Memory IMPORT SET16, CARD8, CARD16, INT16, CARD32, INT32, TAG1, TAG2,
  TAG3, TAG4, TAG5, TAG6, TAG7, First, Next, Tail;
 FROM Languages IMPORT ADL;
 FROM Trigo IMPORT RND, SQRT, SIN, COS;
 FROM ChaosBase IMPORT Zone, zone, level, powerCountDown, pLife, Anims, AnimSet,
  Obj, ObjPtr, mainPlayer, AnimAlienSet, difficulty, water, snow, stages,
  animList, Frac, score, nbDollar;
 FROM ChaosGraphics IMPORT SetRGB, CycleRGB, castle, castleWidth, castleHeight,
  gameWidth, gameHeight, BW, BH, NbWall, NbBackground, PW, PH, backpx, backpy,
  SOW, SOH, NbClear, dualpf, dualPalette, dualCycle, WaterPalette;
 FROM ChaosImages IMPORT InitPalette;
 FROM ChaosActions IMPORT CreateObj, msgObj, priorities, statPos, moneyPos,
  PopMessage, lifePos, GetCenter;
 FROM ChaosObjects IMPORT PutPlayer, PutExit, PutBlockBonus, PutBlockObj,
  Cadre, Clear, FillCond, OnlyWall, OnlyBackground, Rect, PutAlien2,
  Fill, FillChoose, All, PutObj, FillRandom, ExpRandom, Put, PutChaosObjs,
  PutChaosChain, FlushMarks, Rnd, FindIsolatedPlace, Set, PutAlien1, PutTBonus,
  PutBullet, PutHospital, Get, FillObj, Reset, PutMoney, PutMachine,
  PutRandomObjs, PutMagnetR, PutMagnetA, PutIsolatedObjs, PutFineObj,
  PutExtraLife, PutRandom, PutDeadObj, PutPic, PutCreatorC, PutCreatorR,
  PutCannon3, PutTurret, PutDeltaObjs, PutRAlien1, PutKamikaze, PutBubbleMaker,
  PutExtraPower, PutFour, PutTri, PutMagnet, PutRAlien2;
 FROM ChaosGenerator IMPORT DrawPacman, FlipVert, FlipHorz, Rotate,
  FindIsolatedRect, MakeLink, PutCross, FillEllipse, Road, GCastle, Join;
 FROM Chaos1Zone IMPORT Entry, Groove, Garden, Lake, Site, GhostCastle,
  Machinery, IceRink, Factory, Labyrinth, AddOptions, flipVert, flipHorz,
  rotate, pLife2, pLife3, pLife4;
 FROM Chaos2Zone IMPORT Rooms, Yard, Antarctica, Forest, ZCastle, Lights,
  Plain, UnderWater, Assembly, Jungle;
 FROM ChaosDual IMPORT InstallDual;
 FROM ChaosBonus IMPORT Money, Moneys, TimedBonus, tbDBSpeed, tbSGSpeed,
  tbMagnet, tbInvinsibility, tbSleeper, tbBullet, tbBonusLevel, tbHospital,
  tbFreeFire, tbMaxPower, tbNoMissile, tbDifficulty, tbExit, tbBomb, MoneySet,
  tbHelp, BonusLevel;
 FROM ChaosAlien IMPORT aDbOval, aCartoon, aSmallDrawer, aBigDrawer,
  aHospital, aDiese, aKamikaze, aStar, aBubble, aBumper, aPic, aTri, aTrefle,
  aBig, aSquare, aFlame, aColor;
 FROM ChaosCreator IMPORT cAlienV, cAlienA, cCreatorR, cCreatorC, cCircle,
  cController, cChief, cFour, cQuad, cAlienBox, cNest, cGrid, cPopUp, cGhost;
 FROM ChaosDObj IMPORT doCartoon, doMagnetR, doMagnetA, doSand, doMirror,
  doWindMaker, doBubbleMaker, doFireMaker, doFireWall;
 FROM ChaosMachine IMPORT mTraverse, mCannon1, mCannon2, mCannon3, mTurret,
  mReactor, mDoor;
 FROM ChaosBoss IMPORT bBrotherAlien, bSisterAlien, bMotherAlien,
  bFatherAlien, bMasterAlien1, bMasterAlien2, bFatherHeart, bMasterEye,
  bMasterMouth, bMasterPart0, bMasterPart1, bMasterPart2;
 FROM ChaosSmartBonus IMPORT sbExtraLife;
 IMPORT ChaosDead, ChaosStone, ChaosPlayer, ChaosWeapon;


 PROCEDURE InitMsg;
  VAR
   c: CARD16;
 BEGIN
  FOR c:= 0 TO 3 DO
   msgObj[c]:= NIL;
   priorities[c]:= 0
  END;
 END InitMsg;

(* Levels creation *)

 CONST
  Back4x4 = 8;
  BackNone = 9;
  Back2x2 = 10;
  BackSmall = 11;
  BackBig = 12;
  Back8x8 = 13;
  Tar = 14;
  Ground = 15;
  Ground2 = 16;
  Ice = 17;
  Light = 18;
  Balls = 19;
  Round4 = 20;
  FalseBlock = 21;
  FF9x9 = 22;
  EmptyBlock = 24;
  Sq1Block = 25;
  Sq4Block = 26;
  Sq4TravBlock = 27;
  TravBlock = 28;
  Fact1Block = 29;
  Fact2Block = 30;
  Fact3Block = 31;
  SimpleBlock = 32;
  Granit1 = 33;
  Granit2 = 34;
  BigBlock = 35;
  Bricks = 36;
  Fade1 = 37;
  Fade2 = 38;
  Fade3 = 39;
  FBig1 = 40;
  FBig2 = 41;
  FSmall1 = 42;
  FSmall2 = 43;
  FRound = 44;
  FStar = 45;
  FPanic = 46;
  F9x9 = 47;
  Forest1 = 48;
  Forest7 = 54;
  Leaf1 = 55;
  Leaf2 = 56;
  Leaf3 = 57;
  Leaf4 = 58;
  BarLight = 59;
  BarDark = 60;
  TravLight = 61;
  RGBBlock = 62;
  IceBlock = 63;

(* Bonus level 1 *)

 PROCEDURE BabyAliens;
  CONST
   Alien1Set = SET16{aCartoon, aDbOval, aHospital, aDiese, aStar, aBubble, aBumper, aTri};
   Alien2Set = SET16{cCreatorR, cCreatorC, cAlienBox, cNest};
  VAR
   rnd, c: CARD16;
   width, height, w, h: INT16;
   angle, sz, x, y: INT16;
   back, wall: CARD8;
 BEGIN
  width:= 20 + Rnd(50);
  height:= 20 + Rnd(50);
  Cadre(width, height);
  rnd:= RND() MOD 3;
  IF rnd = 0 THEN
   w:= Rnd(4) + 2; h:= Rnd(4) + 2;
   DrawPacman(RND() MOD 8 + 4, w, h, 2, 2, width - w - 1, height - h - 1);
   PutPlayer(1, 1); PutExit(width - 2, height - 2)
  ELSIF rnd = 1 THEN
   w:= width DIV 2; h:= height DIV 2;
   Fill(0, 0, width - 1, height - 1, NbBackground);
   Fill(w - 4, h - 4, w + 4, h + 4, 0);
   Fill(w - 3, h - 3, w + 3, h + 3, NbBackground);
   PutPlayer(w - 4, h - 4); PutExit(w + 4, h + 4);
   FOR c:= 1 TO 40 DO
    sz:= RND() MOD 4 + 1;
    IF FindIsolatedRect(0, 0, width - 1, height - 1, sz, 15, angle, x, y, TRUE) THEN
     Fill(x - sz, y - sz, x + sz, y + sz, 0);
     MakeLink(x, y, sz, angle, 0)
    END
   END
  ELSE
   PutPlayer(1, 1); PutExit(width - 2, height - 2);
   FOR c:= 1 TO 4 + RND() MOD 16 DO
    sz:= RND() MOD 3 + 1;
    IF FindIsolatedRect(1, 1, width - 2, height - 2, sz, 0, angle, x, y, FALSE) THEN
     Fill(x - sz, y - sz, x + sz, y + sz, NbBackground)
    END
   END;
   FOR c:= 1 TO 4 + RND() MOD 32 DO
    PutCross(1, 1, width - 2, height - 1, NbBackground)
   END;
   Rect(1, 1, width - 2, height - 2);
   FOR c:= 1 TO 4 + RND() MOD 8 DO
    IF FindIsolatedPlace(1, x, y) THEN
     IF RND() MOD 2 = 0 THEN
      PutBlockObj(DEADOBJ, doFireWall, 0, x, y)
     ELSE
      Set(x, y)
     END
    END
   END
  END;
  rnd:= RND() MOD 4;
  CASE rnd OF
    0: back:= Light; wall:= BarLight
   |1: back:= Round4; wall:= BarDark
   |2: back:= Back4x4; wall:= Fact1Block + RND() MOD 3
   |3: back:= BackSmall; wall:= TravLight
  END;
  FillCond(0, 0, width - 1, height - 1, OnlyWall, wall);
  FillCond(1, 1, width - 2, height - 2, OnlyBackground, back);
  FOR c:= 1 TO 2 DO
   x:= Rnd(width - 10); y:= Rnd(height - 10);
   Rect(x + 1, y + 1, x + 8, y + 8);
   REPEAT c:= RND() MOD 16 UNTIL c IN Alien1Set;
   PutAlien1(c, pLife3, 10 + difficulty)
  END;
  x:= Rnd(width - 10); y:= Rnd(height - 10);
  Rect(x + 1, y + 1, x + 8, y + 8);
  REPEAT c:= RND() MOD 16 UNTIL c IN Alien2Set;
  PutAlien2(c, 80, 10 + difficulty);
  Rect(4, 4, width - 2, height - 2);
  PutTBonus(tbBomb, 3);
  PutTBonus(tbSleeper, 1);
  PutTBonus(tbInvinsibility, 1);
  PutBullet(16); PutHospital(3);
  PutAlien2(cCircle, 100, 15);
  PutAlien2(cChief, 50, 10);
  AddOptions(4, 4, 30, 30, 0, 0, 0, 5, 0, 0, 0)
 END BabyAliens;

 PROCEDURE Spider;
  VAR
   c: CARD16;
   x, y, sz, angle, d: INT16;
   val: CARD8;
 BEGIN
  Clear(60, 60);
  Fill(0, 0, 59, 59, NbBackground);
  FillEllipse(26, 0, 8, 8, 0);
  FOR c:= 1 TO 25 DO
   sz:= RND() MOD 5 + 1; d:= 2 * sz + 1;
   IF FindIsolatedRect(0, 0, 59, 59, sz, 10, angle, x, y, TRUE) THEN
    IF RND() MOD 2 = 0 THEN
     Fill(x - sz, y - sz, x + sz, y + sz, 0)
    ELSE
     FillEllipse(x - sz, y - sz, d, d, 0)
    END;
    MakeLink(x, y, sz, angle, 0)
   END
  END;
  FOR y:= 0 TO 59 DO
   FOR x:= 0 TO 59 DO
    IF Get(x, y) >= NbBackground THEN Reset(x, y) ELSE Set(x, y) END
   END
  END;
  PutPlayer(27, 0); PutExit(32, 0);
  FillRandom(0, 0, 59, 59, Forest1, Forest7, OnlyWall, Rnd);
  FOR c:= 1 TO RND() MOD 8 + 5 DO
   val:= RND() MOD 4 + Leaf1;
   PutCross(1, 1, 58, 58, val)
  END;
  FOR c:= 1 TO RND() MOD 8 + 5 DO
   val:= RND() MOD 4 + Leaf1;
   sz:= RND() MOD 2 + 1;
   IF FindIsolatedRect(1, 1, 58, 58, sz, 0, angle, x, y, FALSE) THEN
    Fill(x - sz, y - sz, x + sz, y + sz, val)
   END
  END;
  FOR c:= 1 TO 30 DO
   sz:= RND() MOD 4; d:= 2 * sz + 1;
   IF FindIsolatedRect(0, 0, 59, 59, sz, 10, angle, x, y, TRUE) THEN
    IF RND() MOD 2 = 0 THEN
     Fill(x - sz, y - sz, x + sz, y + sz, 0)
    ELSE
     FillEllipse(x - sz, y - sz, d, d, 0)
    END;
    MakeLink(x, y, sz, angle, 0)
   END
  END;
  FillCond(0, 0, 59, 59, OnlyBackground, Ground2);
  FOR c:= 1 TO 3 DO
   IF FindIsolatedRect(1, 1, 58, 58, 1, 0, angle, x, y, FALSE) THEN
    FillObj(ALIEN1, aCartoon, RND() MOD 2 + 1, x - 1, y - 1, x + 1, y + 1, TRUE)
   END
  END;
  Rect(1, 1, 58, 58);
  PutAlien2(cNest, 0, 28);
  PutTBonus(tbMagnet, 1);
  PutTBonus(tbFreeFire, 1);
  PutTBonus(tbBomb, 1);
  PutMoney(MoneySet{m1, m2, m5, st}, 18);
  PutBullet(16); PutHospital(4);
  PutAlien1(aCartoon, 0, 13);
  PutAlien2(cChief, 50, 12);
  PutMachine(mCannon3, 0, 0, 8);
  IF difficulty >= 10 THEN
   PutRandomObjs(ALIEN3, bSisterAlien, 0, 1)
  END;
  AddOptions(1, 1, 59, 59, 3, 3, 0, 0, 5, 4, 1)
 END Spider;

 PROCEDURE Graveyard;
  PROCEDURE RndRect;
   VAR
    x, y: INT16;
  BEGIN
   x:= Rnd(96); y:= Rnd(96);
   Rect(x, y, x + 30, y + 30)
  END RndRect;

  VAR
   c: CARD16;
   angle, sz, d, lx, ly, ml, x, y: INT16;
   r: INT32;
   val: CARD8;
 BEGIN
  water:= level[Special] = 20;
  Clear(127, 127);
  Fill(0, 0, 126, 126, SimpleBlock);
  lx:= 0; ly:= 0;
  ml:= RND() MOD 6 + 2;
  FOR angle:= 0 TO 360 BY 2 DO
   r:= SIN(angle * ml);
   x:= COS(angle) * (2048 + r) DIV 54330;
   y:= SIN(angle) * (2048 + r) DIV 54330;
   INC(x, 63); INC(y, 63);
   IF (lx <> 0) AND (ly <> 0) THEN
    Road(lx, ly, x, y, 2, Ground)
   END;
   lx:= x; ly:= y
  END;
  PutRandom(0, 0, 126, 126, OnlyWall, Leaf1, 255);
  PutRandom(0, 0, 126, 126, OnlyWall, Leaf2, 255);
  PutRandom(0, 0, 126, 126, OnlyWall, Leaf3, 255);
  PutRandom(0, 0, 126, 126, OnlyWall, Fact2Block, 50);
  PutPlayer(100, 63);
  PutBlockBonus(tbSleeper, 100, 63);
  PutExit(102, 63);
  FOR c:= 1 TO 40 DO
   sz:= RND() MOD 3 + 2; d:= sz * 2 + 1;
   IF FindIsolatedRect(0, 0, 126, 126, sz, 30, angle, x, y, TRUE) THEN
    FillEllipse(x - sz, y - sz, d, d, Balls);
    Rect(x - sz, y - sz, x + sz, y + sz);
    IF (c >= 25) AND ODD(c) THEN
     val:= FalseBlock
    ELSIF dualpf THEN
     val:= BackNone
    ELSE
     val:= Balls
    END;
    MakeLink(x, y, sz, angle, val);
    DEC(sz, 2); DEC(d, 4);
    FillEllipse(x - sz, y - sz, d, d, Granit1 + RND() MOD 2);
    PutDeadObj(doBubbleMaker, 0, 1);
    IF ODD(c) THEN
     IF ODD(c DIV 2) THEN PutBullet(1) ELSE PutHospital(1) END
    ELSE
     PutMoney(MoneySet{m1, m2, m5, st}, 2)
    END;
    IF c > 34 THEN PutTBonus(tbBomb, 1) END
   END
  END;
  Rect(0, 0, 126, 126); PutTBonus(tbHelp, 1);
  PutDeadObj(doFireMaker, 0, 6);
  PutDeadObj(doWindMaker, 1, 3);
  PutTBonus(tbMagnet, 4);
  PutTBonus(tbInvinsibility, 3);
  PutTBonus(tbSleeper, 2);
  PutTBonus(tbFreeFire, 2);
  PutTBonus(tbMaxPower, 1);
  PutTBonus(tbDBSpeed, 2);
  PutTBonus(tbSGSpeed, 1);
  PutAlien1(aBigDrawer, 100, 3);
  PutAlien1(aSmallDrawer, 100, 3);
  PutDeadObj(doFireWall, 0, 10);
  AddOptions(32, 32, 95, 95, 0, 4, 0, 2, 10, 8, 1);
  RndRect; PutAlien2(cCircle, 100, 20);
  RndRect; PutAlien2(cChief, 50, 10);
  RndRect; PutPic(0, 3); PutPic(1, 3);
  RndRect; PutCreatorC(10);
  RndRect; PutCreatorR(1);
  RndRect; PutCannon3(3); PutDeadObj(doWindMaker, 0, 2)
 END Graveyard;

 PROCEDURE Winter;
  VAR
   lx, ly, px, py, s1, s2, a1, a2: INT16;
   c: CARD16;
   val: CARD8;
 BEGIN
  Clear(120, 38); snow:= TRUE;
  water:= (difficulty >= 6) AND (RND() MOD 8 = 0);
  Fill(0, 0, 119, 37, SimpleBlock);
  a1:= 0; a2:= 0;
  lx:= 0; ly:= 0;
  s1:= Rnd(16) + 6;
  s2:= Rnd(16) + 6;
  FOR px:= 3 TO 116 DO
   py:= (SIN(a1) * 3 + SIN(a2) * 2) / 366 + 16;
   IF lx <> 0 THEN
    Road(lx, ly, px, py, 2, Ice)
   END;
   lx:= px; ly:= py;
   INC(a1, s1); INC(a2, s2)
  END;
  px:= 116;
  Fill(px - 2, py - 2, px + 2, py + 2, Ice);
  Fill(119, py, 119, 37, FalseBlock);
  Fill(100, 37, 118, 37, Ground2);
  Fill(97, 35, 99, 37, Ground);
  PutBlockObj(BONUS, BonusLevel, tbBonusLevel, 98, 36);
  PutExit(97, 35);
  PutPlayer(1, 16); PutBlockBonus(tbInvinsibility, 1, 16);
  PutExit(px, py);
  FillRandom(1, 1, 118, 36, Forest1, Forest7, OnlyWall, Rnd);
  Rect(1, 1, 118, 31);
  FOR c:= 0 TO RND() MOD 24 + 8 DO
   IF FindIsolatedPlace(0, px, py) THEN
    val:= Leaf1 + RND() MOD 5;
    IF val > Leaf4 THEN val:= IceBlock END;
    Put(px, py, val)
   END
  END;
  Rect(20, 1, 70, 31);
  PutDeltaObjs(MACHINE, mCannon2, 0, 0, -1, 10);
  PutDeltaObjs(MACHINE, mCannon2, 1, 0, 1, 10);
  Rect(50, 1, 100, 31);
  PutMachine(mCannon1, 0, 1, 4);
  PutTurret(10); PutCannon3(8);
  Rect(80, 1, 118, 31);
  PutDeadObj(doFireMaker, 0, 7);
  PutDeadObj(doWindMaker, 0, 3);
  PutDeadObj(doWindMaker, 1, 4);
  Rect(1, 1, 118, 37);
  PutDeadObj(doBubbleMaker, 0, 10);
  PutAlien2(cNest, 0, 15);
  PutRAlien1(aCartoon, 0, 2, 15);
  PutRAlien1(aKamikaze, 0, 3, 15);
  PutTBonus(tbBomb, 2);
  PutBullet(14); PutHospital(7);
  PutMoney(MoneySet{m2, m2, m5, st}, 20)
 END Winter;

 PROCEDURE Panic;
  VAR
   px, py, pl, r: INTEGER;
 BEGIN
  Clear(41, 41); water:= TRUE;
  FOR py:= 0 TO 40 DO
   FOR px:= 0 TO 40 DO
    pl:= SQRT((px - 20) * (px - 20) + (py - 20) * (py - 20));
    r:= RND() MOD 16 + 4;
    IF pl > r THEN Put(px, py, FPanic) ELSE Put(px, py, 0) END
   END
  END;
  IF dualpf THEN
   FillCond(1, 1, 39, 39, OnlyBackground, BackNone)
  ELSE
   FillRandom(1, 1, 39, 39, 0, 7, OnlyBackground, Rnd)
  END;
  Fill(20, 1, 20, 39, Ice);
  PutPlayer(20, 22);
  PutExit(20, 30);
  PutBlockBonus(tbHelp, 20, 26);
  PutBlockObj(BONUS, BonusLevel, tbBonusLevel, 20, 39);
  FOR py:= 33 TO 38 DO
   PutBlockBonus(tbInvinsibility, 20, py)
  END;
  FOR py:= 1 TO 4 DO
   PutBlockBonus(tbSleeper, 20, py)
  END;
  PutBlockObj(ALIEN3, bSisterAlien, 0, 20, 8);
  PutBlockObj(ALIEN3, bMotherAlien, 0, 18, 20);
  PutBlockObj(ALIEN3, bMotherAlien, 0, 22, 20);
  PutBlockObj(ALIEN3, bMasterEye, 0, 19, 18);
  PutBlockObj(ALIEN3, bMasterEye, 1, 21, 18);
  PutBlockObj(ALIEN3, bMasterMouth, 0, 20, 18);
  PutBlockObj(ALIEN3, bMasterAlien1, 0, 20, 18);
  Rect(1, 1, 39, 39);
  PutTBonus(tbBomb, 8);
  PutIsolatedObjs(DEADOBJ, doBubbleMaker, 0, 1, 0, 15);
  PutIsolatedObjs(DEADOBJ, doMirror, 0, 1, 0, 6);
  PutMagnetR(3, 8); PutMagnetA(1, 3);
  PutIsolatedObjs(DEADOBJ, doWindMaker, 0, 1, 0, 6);
  PutChaosObjs(DEADOBJ, doSand, 0, BW * 10, BH * 10, BW * 30, BH * 20, 20);
  PutRAlien1(aCartoon, 0, 2, 8);
  PutHospital(16); PutBullet(10);
  PutMoney(MoneySet{m5, st}, 15);
  PutMoney(MoneySet{m3}, 1)
 END Panic;

(* Brother Alien *)

 VAR
  val1, val2: CARD8;

 PROCEDURE CheckerBoard(x, y: INT16): CARD8;
 BEGIN
  IF ODD(x + y) THEN RETURN val1 ELSE RETURN val2 END
 END CheckerBoard;

 PROCEDURE Brother;
 BEGIN
  Cadre(21, 21);
  val1:= FSmall1; val2:= FSmall2;
  FillChoose(0, 0, 20, 20, All, CheckerBoard);
  Fill(7, 1, 13, 19, 11);
  Fill(1, 7, 19, 13, 11);
  PutPlayer(7, 19);
  PutBlockBonus(tbHelp, 7, 1);
  PutBlockObj(ALIEN3, bBrotherAlien, 0, 11, 1)
 END Brother;

 PROCEDURE Sister;
 BEGIN
  Cadre(16, 24); water:= TRUE;
  val1:= FSmall1; val2:= FSmall2;
  FillChoose(0, 0, 15, 23, All, CheckerBoard);
  Fill(1, 1, 14, 22, Light);
  val1:= FBig1; val2:= FBig2;
  FillChoose(7, 10, 8, 13, All, CheckerBoard);
  PutObj(DEADOBJ, doBubbleMaker, 0, BW * 8, BH * 9);
  PutBlockObj(ALIEN3, bSisterAlien, 0, 9, 14);
  PutPlayer(6, 22)
 END Sister;

 PROCEDURE Mother;
  VAR
   x, y, zx, zy: INT16;
 BEGIN
  Clear(11, 11);
  IF dualpf THEN
   FOR x:= 0 TO 10 DO
    FOR y:= 0 TO 10 DO
     zx:= (x - 7) + Rnd(5);
     zy:= (y - 7) + Rnd(5);
     IF (ABS(zx) <= 1) OR (ABS(zy) <= 1) THEN
      Put(x, y, Round4)
     ELSE
      Put(x, y, BackNone)
     END
    END
   END
  ELSE
   FillRandom(0, 0, 10, 10, 0, 7, OnlyBackground, ExpRandom)
  END;
  PutBlockObj(ALIEN3, bMotherAlien, 0, 5, 3);
  PutPlayer(5, 7)
 END Mother;

 PROCEDURE Father;
  VAR
   x: INT16;
 BEGIN
  Clear(11, 13);
  val1:= FRound; val2:= FStar;
  FillChoose(0, 0, 10, 12, All, CheckerBoard);
  Fill(1, 3, 9, 11, Back2x2);
  Fill(4, 1, 6, 2, BackSmall); Put(5, 0, BackSmall);
  PutBlockObj(DEADOBJ, doMirror, 0, 4, 1);
  PutBlockObj(DEADOBJ, doMirror, 0, 5, 0);
  PutBlockObj(DEADOBJ, doMirror, 0, 6, 1);
  FOR x:= 4 TO 6 DO
   PutBlockObj(MACHINE, mDoor, 0, x, 2);
   PutBlockObj(ALIEN2, cNest, 0, 5, 1)
  END;
  PutBlockObj(ALIEN3, bFatherHeart, 0, 5, 5);
  PutBlockObj(ALIEN3, bFatherAlien, 0, 5, 5);
  PutPlayer(5, 9);
  PutBlockBonus(tbHelp, 9, 3); PutExit(1, 3)
 END Father;

 PROCEDURE Master;
  VAR
   z, d: INT16;
   val: CARD8;
 BEGIN
  Clear(11, 11);
  IF dualpf THEN
   FOR z:= 5 TO 0 BY -1 DO
    d:= z * 2 + 1;
    IF ODD(z) THEN val:= BackBig ELSE val:= BackNone END;
    FillEllipse(5 - z, 5 - z, d, d, val)
   END
  ELSE
   FillRandom(0, 0, 10, 10, 0, 7, OnlyBackground, ExpRandom)
  END;
  PutBlockObj(ALIEN3, bMasterEye, 0, 4, 3);
  PutBlockObj(ALIEN3, bMasterEye, 1, 6, 3);
  PutBlockObj(ALIEN3, bMasterMouth, 0, 5, 4);
  PutBlockObj(ALIEN3, bMasterAlien1, 0, 5, 3);
  PutBlockBonus(tbInvinsibility, 0, 0);
  PutBlockBonus(tbInvinsibility, 10, 0);
  PutBlockBonus(tbMagnet, 0, 10);
  PutBlockBonus(tbMagnet, 10, 10);
  PutPlayer(5, 7);
 END Master;

 PROCEDURE Illusion;
 BEGIN
  Cadre(36, 18);
  FillCond(0, 0, 35, 17, OnlyBackground, BackBig);
  FillCond(0, 0, 35, 17, OnlyWall, F9x9);
  val1:= FSmall1; val2:= FBig2;
  FillChoose(9, 5, 12, 11, All, CheckerBoard);
  val1:= FSmall2; val2:= FBig1;
  FillChoose(23, 5, 26, 11, All, CheckerBoard);
  PutBlockObj(DEADOBJ, doMirror, 1, 15, 7);
  PutBlockObj(DEADOBJ, doMirror, 1, 20, 7);
  PutBlockObj(DEADOBJ, doMirror, 0, 17, 10);
  PutBlockObj(DEADOBJ, doMirror, 0, 18, 10);
  PutBlockObj(MACHINE, mReactor, 0, 2, 16);
  PutBlockObj(MACHINE, mReactor, 0, 33, 16);
  PutBlockObj(DEADOBJ, doMagnetA, 3, 3, 13);
  PutBlockObj(DEADOBJ, doMagnetA, 3, 32,13);
  Rect(10, 1, 25, 16);
  PutIsolatedObjs(DEADOBJ, doWindMaker, 0, 1, 0, 2);
  PutIsolatedObjs(DEADOBJ, doBubbleMaker, 0, 1, 0, 2);
  Rect(1, 1, 34, 3);
  PutIsolatedObjs(DEADOBJ, doFireMaker, 0, 1, 0, 2);
  Rect(5, 1, 30, 16); PutMagnetR(3, 6); PutMagnetA(1, 6);
  PutPlayer(1, 1);
  IF difficulty >= 4 THEN
   PutObj(ALIEN2, cController, 0, 0, 0)
  END;
  PutBlockObj(ALIEN3, bMasterAlien2, 10, 34, 1);
  PutBlockObj(ALIEN3, bMasterPart0, 0, 34, 1);
  PutBlockObj(ALIEN3, bMasterPart0, 1, 34, 1);
  PutBlockObj(ALIEN3, bMasterPart1, 2, 34, 1);
  PutBlockObj(ALIEN3, bMasterPart1, 3, 34, 1);
  PutBlockObj(ALIEN3, bMasterPart2, 4, 34, 1)
 END Illusion;

 PROCEDURE Kids;
 BEGIN
  Cadre(24, 24); snow:= TRUE;
  Fill(6, 6, 17, 17, NbBackground);
  Fill(9, 9, 14, 14, 0);
  Fill(11, 6, 12, 17, 0);
  Fill(6, 11, 17, 12, 0);
  IF RND() MOD 2 = 0 THEN
   val1:= FSmall1; val2:= FBig2
  ELSE
   val1:= FSmall2; val2:= FBig1
  END;
  FillChoose(0, 0, 23, 23, OnlyWall, CheckerBoard);
  FillCond(1, 1, 22, 22, OnlyBackground, Ice);
  PutChaosObjs(DEADOBJ, doSand, 0, 0, 0, 735, 735, 10);
  PutPlayer(1, 22);
  PutBlockObj(ALIEN3, bBrotherAlien, 0, 21, 22);
  PutBlockObj(ALIEN3, bSisterAlien, 0, 1, 1)
 END Kids;

 PROCEDURE Parents;
 BEGIN
  Cadre(17, 17);
  Fill(0, 0, 16, 16, FRound);
  Fill(1, 1, 15, 15, Ice);
  Fill(2, 2, 14, 14, FStar);
  Fill(3, 2, 13, 13, Ice);
  Fill(8, 0, 8, 16, Ice);
  Fill(0, 8, 16, 8, Ice);
  Fill(0, 0, 4, 4, Ice);
  Fill(0, 12, 4, 16, Ice);
  Fill(12, 0, 16, 4, Ice);
  Fill(12, 12, 16, 16, Ice);
  PutBlockBonus(tbMagnet, 2, 2);
  PutBlockBonus(tbMagnet, 15, 2);
  PutBlockBonus(tbMagnet, 2, 15);
  PutBlockBonus(tbMagnet, 15, 15);
  PutBlockBonus(tbHospital, 0, 8);
  PutBlockBonus(tbHospital, 16, 8);
  PutBlockBonus(tbHospital, 8, 0);
  PutBlockBonus(tbHospital, 8, 16);
  PutBlockObj(ALIEN3, bFatherHeart, 0, 7, 12);
  PutBlockObj(ALIEN3, bFatherAlien, 0, 7, 12);
  PutBlockObj(ALIEN3, bMotherAlien, 0, 11, 12);
  PutPlayer(8, 5)
 END Parents;

 PROCEDURE Masters;
 BEGIN
  Cadre(17, 17);
  IF dualpf THEN val1:= BackNone ELSE val1:= Back8x8 END;
  Fill(1, 1, 15, 15, Back4x4);
  FillEllipse(0, 0, 17, 17, val1);
  FillEllipse(2, 2, 13, 13, NbBackground);
  FillEllipse(4, 4, 9, 9, val1);
  Fill(7, 2, 9, 14, BackSmall);
  Fill(2, 7, 14, 9, BackSmall);
  val1:= FBig1; val2:= FBig2;
  FillChoose(0, 0, 16, 16, OnlyWall, CheckerBoard);
  PutBlockBonus(tbHospital, 6, 6);
  PutBlockBonus(tbHospital, 10, 6);
  PutBlockBonus(tbHospital, 6, 10);
  PutBlockBonus(tbHospital, 10, 10);
  PutBlockObj(MACHINE, mDoor, 0, 8, 5);
  PutFineObj(DEADOBJ, doMirror, 1, 1, 8, 0, 1);
  PutFineObj(DEADOBJ, doMirror, 1, 15, 8, 1, 1);
  PutBlockObj(MACHINE, mReactor, 0, 1, 15);
  PutBlockObj(MACHINE, mReactor, 0, 15, 15);
  PutBlockBonus(tbInvinsibility, 1, 1);
  PutBlockBonus(tbInvinsibility, 15, 1);
  PutPlayer(5, 9);
  PutBlockObj(ALIEN3, bMasterEye, 0, 9, 5);
  PutBlockObj(ALIEN3, bMasterEye, 1, 9, 5);
  PutBlockObj(ALIEN3, bMasterMouth, 0, 9, 5);
  PutBlockObj(ALIEN3, bMasterAlien1, 0, 9, 5);
  PutBlockObj(ALIEN3, bMasterAlien2, 10, 8, 8);
  PutBlockObj(ALIEN3, bMasterPart0, 0, 8, 8);
  PutBlockObj(ALIEN3, bMasterPart0, 1, 8, 8);
  PutBlockObj(ALIEN3, bMasterPart1, 2, 8, 8);
  PutBlockObj(ALIEN3, bMasterPart1, 3, 8, 8);
  PutBlockObj(ALIEN3, bMasterPart2, 4, 8, 8)
 END Masters;

 PROCEDURE Final;
 BEGIN
  Cadre(19, 99);
  Fill(0, 0, 18, 40, F9x9);
  Fill(3, 28, 16, 31, Leaf3);
  Fill(10, 32, 17, 34, BackNone);
  Fill(0, 58, 18, 98, F9x9);
  Fill(1, 1, 17, 19, 0);
  Fill(1, 1, 3, 3, 6);
  Fill(17, 20, 17, 39, FF9x9);
  Fill(2, 20, 2, 33, FF9x9);
  Fill(3, 25, 9, 25, FF9x9);
  Fill(2, 33, 2, 33, Ground2);
  FillCond(0, 40, 18, 58, OnlyWall, F9x9);
  FillCond(0, 40, 18, 58, OnlyBackground, Round4);
  Fill(4, 44, 6, 46, FBig1); Fill(5, 45, 5, 45, FPanic);
  Fill(12, 44, 14, 46, FBig2); Fill(13, 45, 13, 45, FPanic);
  Fill(4, 52, 6, 54, FBig2); Fill(5, 53, 5, 53, FPanic);
  Fill(12, 52, 14, 54, FBig1); Fill(13, 53, 13, 53, FPanic);
  FillObj(SMARTBONUS, sbExtraLife, 0, 1, 41, 1, 41, TRUE);
  FillObj(BONUS, TimedBonus, tbMagnet, 1, 57, 1, 57, TRUE);
  FillObj(BONUS, TimedBonus, tbInvinsibility, 17, 41, 17, 41, TRUE);
  FillObj(BONUS, TimedBonus, tbSleeper, 17, 57, 17, 57, TRUE);
  FillObj(BONUS, BonusLevel, tbBonusLevel, 9, 25, 9, 25, TRUE);
  PutBlockObj(BONUS, BonusLevel, tbBonusLevel, 2, 33);
  PutBlockBonus(tbHelp, 17, 35);
  PutBlockBonus(tbHospital, 1, 49);
  PutBlockBonus(tbHospital, 17, 49);
  PutBlockBonus(tbHospital, 9, 41);
  PutBlockBonus(tbHospital, 9, 57);
  PutBlockObj(MACHINE, mReactor, 0, 9, 57);
  PutBlockBonus(tbDifficulty, 2, 2);
  PutBlockObj(ALIEN3, bBrotherAlien, 0, 10, 50);
  PutBlockObj(ALIEN3, bMotherAlien, 0, 10, 50);
  PutPlayer(16, 56)
 END Final;

(* Main procedures *)

 PROCEDURE MakeChaos;
  CONST
   MaxY = PH * 2 DIV 3 - 10;
  VAR
   stl, c, total: CARD16;
   cStep, cCurrent: CARD16;
   cnt: INT16;
   cntAlienS, cntAlienV, cntAlienA: CARD8;
   cLevel, dv, fib1, fib2: CARD8;
 BEGIN
   (* Background *)
  Clear(SOW, SOH);
  water:= (difficulty >= 6) AND (level[Chaos] = 13);
  gameWidth:= PW; gameHeight:= PH;
  FillRandom(0, 0, SOW - 1, SOH - 1, 0, 7, OnlyBackground, ExpRandom);
  cLevel:= level[Chaos];
  total:= 0;
   (* Small drawer *)
  cnt:= -1;
  dv:= SQRT(cLevel);
  WHILE dv * dv > cLevel DO DEC(dv) END;
  WHILE dv > 1 DO
   IF cLevel MOD dv = 0 THEN INC(cnt) END;
   DEC(dv)
  END;
  IF cnt > 0 THEN
   INC(total, cnt);
   PutChaosObjs(ALIEN1, aSmallDrawer, 0, 10, 10, PW - 10, MaxY, cnt)
  END;
   (* Big drawer *)
  fib1:= 3; fib2:= 5; cnt:= 1;
  WHILE fib2 < cLevel DO
   IF cnt < 3 THEN INC(cnt) END;
   dv:= fib2; INC(fib2, fib1); fib1:= dv
  END;
  IF (fib2 = cLevel) OR (cLevel = 100) THEN
   INC(total, cnt);
   PutChaosObjs(ALIEN1, aBigDrawer, 0, 20, 20, PW - 20, MaxY, cnt)
  END;
   (* Creator *)
  cnt:= 0;
  cStep:= 6; cCurrent:= 7;
  FOR c:= 1 TO cLevel DO
   IF cCurrent = 0 THEN
    DEC(cStep);
    IF cStep = 0 THEN
     cStep:= 7; INC(cnt)
    END;
    cCurrent:= cStep
   END;
   DEC(cCurrent)
  END;
  IF cCurrent = 0 THEN INC(cnt) END;
  c:= cnt * 80; IF cLevel MOD 33 = 0 THEN INC(c, 160) END;
  PutChaosChain(ALIEN2, cCreatorR, c, 80, 20, 20, PW - 20, MaxY, cnt);
  INC(total, cnt);
   (* Aliens *)
  cntAlienV:= 0; cntAlienA:= 0;
  cntAlienS:= 0;
  IF cLevel < 7 THEN cntAlienS:= cLevel ELSE cntAlienS:= 6 END;
  IF (cLevel MOD 33 <> 0) THEN
   INC(total, cntAlienS);
   stl:= (cntAlienS + difficulty - 1) * 8;
   WHILE (cLevel MOD 2 = 0) AND (cntAlienS > 0) DO
    cLevel:= cLevel DIV 2;
    DEC(cntAlienS); INC(cntAlienV)
   END;
   WHILE (cLevel MOD 3 = 0) AND (cntAlienS > 0) DO
    cLevel:= cLevel DIV 3;
    DEC(cntAlienS); INC(cntAlienA)
   END;
   IF cLevel = 100 THEN INC(cntAlienA); DEC(cntAlienS) END;
   PutChaosChain(ALIEN2, cAlienA, stl, 8, 10, 10, PW - 10, MaxY, cntAlienA);
   PutChaosChain(ALIEN2, cAlienV, stl, 8, 10, 10, PW - 10, MaxY, cntAlienV);
   PutChaosChain(ALIEN1, aDbOval, stl, 8, 10, 10, PW - 10, MaxY, cntAlienS)
  ELSIF difficulty >= 3 THEN
   PopMessage(ADL("Mind the meteorites"), statPos, 3)
  END;
   (* Circle *)
  IF level[Chaos] MOD 20 = 0 THEN
   PopMessage(ADL("Big trouble"), statPos, 3);
   PutChaosObjs(ALIEN2, cCircle, 200, 20, 20, PW - 20, MaxY, 1);
   INC(total)
  END;
  IF level[Chaos] = 100 THEN
   PutChaosObjs(BONUS, TimedBonus, tbDifficulty, 10, 10, PW - 10, MaxY, 1)
  END;
   (* Hospital *)
  cLevel:= level[Chaos];
  dv:= SQRT(cLevel);
  IF (dv * dv = cLevel) AND ((cLevel >= 9) OR (level[Castle] > 1)) THEN
   c:= 50 + pLife * 4;
   PutChaosObjs(ALIEN1, aHospital, c, 10, 10, PW - 10, MaxY, 1)
  END;
   (* Controller *)
  PutObj(ALIEN2, cController, 0, 0, 0);
   (* Player *)
  PutPlayer(SOW DIV 2 - 1, SOH - 2);
   (* Messages *)
  IF (cLevel = 1) AND (level[Castle] = 1) THEN
   PopMessage(ADL("Just warming up"), statPos, 3)
  ELSIF (cLevel = 5) THEN
   PopMessage(ADL("The drawer may help you"), statPos, 3)
  ELSIF (cLevel = 7) THEN
   PopMessage(ADL("Too easy?"), statPos, 3)
  ELSIF (cLevel = 9) THEN
   PopMessage(ADL("Something new"), statPos, 3)
  ELSIF (cLevel = 17) AND (score < 1000) THEN
   PopMessage(ADL("Still a long way before"), statPos, 3);
   PopMessage(ADL("Father Alien"), moneyPos, 3)
  ELSIF (cLevel = 50) THEN
   PopMessage(ADL("To be or not to be"), statPos, 3)
  ELSIF (cLevel = 37) THEN
   PopMessage(ADL("Getting harder"), statPos, 3)
  ELSIF (cLevel = 79) THEN
   PopMessage(ADL("Coming to the end"), statPos, 3);
  ELSIF (cLevel = 98) THEN
   PopMessage(ADL("Prepare yourself for level 100"), statPos, 3)
  END
 END MakeChaos;

 PROCEDURE MakeCastle;
  VAR
   x, y: INT16;
   c, lvl: CARD16;
 BEGIN
  InitMsg;
  FOR x:= 0 TO 127 DO
   FOR y:= 0 TO 126 DO
    castle^[y, x]:= BackNone
   END
  END;
  water:= FALSE; snow:= FALSE;
  flipVert:= TRUE; flipHorz:= TRUE; rotate:= TRUE;
  pLife2:= pLife * 2; pLife3:= pLife * 3;
  pLife4:= pLife2 + 18 + difficulty * 2;
  InstallDual;
  FOR x:= 0 TO 127 DO
   FOR y:= 0 TO 126 DO
    castle^[y, x]:= 0
   END
  END;
  IF zone = Chaos THEN
   flipVert:= FALSE; flipHorz:= FALSE; rotate:= FALSE;
   MakeChaos
  ELSIF zone = Castle THEN
   CASE level[Castle] OF
     1: Entry
    |2: Groove
    |3: Garden
    |4: Lake
    |5: Site
    |6: GhostCastle
    |7: Machinery
    |8: IceRink
    |9: Factory
   |10: Labyrinth
   |11: Rooms
   |12: Yard
   |13: Antarctica
   |14: Forest
   |15: ZCastle
   |16: Lights
   |17: Plain
   |18: UnderWater
   |19: Assembly
   |20: Jungle
   END;
  ELSIF zone = Family THEN
   flipVert:= FALSE; flipHorz:= FALSE; rotate:= FALSE;
   CASE level[Family] OF
     1: Brother
    |2: Sister
    |3: Mother
    |4: Father
    |5: Kids
    |6: Parents
    |7: Master
    |8: Illusion
    |9: Masters
   |10: Final
   END;
   IF level[Family] = 10 THEN
    PopMessage(ADL("Good luck !"), lifePos, 4)
   ELSE
    PopMessage(ADL("* PANIC *"), lifePos, 4)
   END
  ELSE
   lvl:= level[Special];
   IF lvl MOD 24 = 0 THEN
    Panic
   ELSIF lvl MOD 8 = 0 THEN
    Winter
   ELSIF lvl MOD 4 = 0 THEN
    PopMessage(ADL("That's where you'll end"), lifePos, 4);
    Graveyard
   ELSIF lvl MOD 2 = 0 THEN
    Spider
   ELSE
    BabyAliens
   END
  END;
  IF flipVert AND (RND() MOD 2 = 0) THEN FlipVert END;
  IF flipHorz AND (RND() MOD 2 = 0) THEN FlipHorz END;
  IF rotate AND (RND() MOD 2 = 0) THEN Rotate END;
  GetCenter(mainPlayer, backpx, backpy);
  DEC(backpx, PW DIV 2); DEC(backpy, PH DIV 2);
  FlushMarks;
  IF water AND (zone <> Chaos) THEN
   IF dualpf THEN
    FOR c:= 0 TO 15 DO
     WITH dualPalette[c] DO WaterPalette(red, green, blue) END;
     WITH dualCycle[c] DO WaterPalette(red, green, blue) END
    END
   END;
   PopMessage(ADL("Warning: you are under water"), statPos, 3);
   PutObj(ALIEN2, cController, 0, 0, 0)
  END;
  IF snow THEN
   PopMessage(ADL("Warning: you are running on ice"), moneyPos, 3)
  END;
  InitPalette
 END MakeCastle;

END ChaosLevels.
