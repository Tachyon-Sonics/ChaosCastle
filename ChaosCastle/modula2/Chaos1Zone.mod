IMPLEMENTATION MODULE Chaos1Zone;

 FROM Memory IMPORT SET16, CARD8, CARD16, INT16;
 FROM Trigo IMPORT RND, SQRT, SIN, COS;
 FROM Registration IMPORT registered;
 FROM ChaosBase IMPORT Zone, zone, level, powerCountDown, pLife, Anims, AnimSet,
  Obj, ObjPtr, mainPlayer, AnimAlienSet, difficulty, water, snow, stages,
  animList, Frac, specialStage, nbDollar, nbSterling;
 FROM ChaosGraphics IMPORT SetRGB, CycleRGB, castle, castleWidth, castleHeight,
  gameWidth, gameHeight, BW, BH, NbWall, NbBackground, PW, PH, backpx, backpy,
  SOW, SOH, NbClear, dualpf;
 FROM ChaosImages IMPORT InitPalette;
 FROM ChaosActions IMPORT CreateObj, msgObj, priorities, statPos, moneyPos,
  PopMessage, lifePos, GetCenter;
 FROM ChaosObjects IMPORT FilterProc, RandomProc, ChooseProc, Rnd, ExpRandom,
  Set, Reset, Get, Put, Mark, Marked, FlushMarks, Clear, Cadre, All,
  OnlyBackground, OnlyWall, Fill, FillCond, FillRandom, FillChoose, PutRandom,
  PutObj, PutBlockObj, PutFineObj, Put4Objs, PutRandomObjs, FindIsolatedPlace,
  PutIsolatedObjs, PutDeltaObjs, PutGridObjs, PutRndStatObjs, PutChaosObjs,
  PutChaosChain, FillObj, PutPlayer, PutExit, PutKamikaze, PutPic,
  PutBlockBonus, PutTBonus, PutHospital, PutBullet, PutMagnet, PutSleeper,
  PutInvinsibility, PutFreeFire, PutMaxPower, PutChaosSterling, PutMoney,
  PutExtraPower, PutExtraLife, PutRAlien1, PutAlien1, PutAColor, PutColor,
  PutTrefle, PutTri, PutCartoon, PutRAlien2, PutAlien2, PutCFour, PutFour,
  PutQuad, PutABox, PutNest, PutCreatorR, PutCreatorC, PutDeadObj,
  PutBubbleMaker, PutMagnetR, PutMagnetA, PutMachine, PutCannon3, PutTurret,
  Rect, PutIsolated, SetOnlyValue, OnlyValue;
 FROM ChaosGenerator IMPORT MakeLink, DrawPacman, Road, Excavate, PutCross,
  FillEllipse, TripleLoop, GCastle, Cave, DrawFactory, DrawLabyrinth,
  RemIsolated, VRace, DrawCastle, DrawBoxes, Join, FlipVert, FlipHorz, Rotate,
  FindIsolatedRect;
 FROM ChaosBonus IMPORT Money, Moneys, TimedBonus, tbDBSpeed, tbSGSpeed,
  tbMagnet, tbInvinsibility, tbSleeper, tbBullet, tbBonusLevel, tbHospital,
  tbFreeFire, tbMaxPower, tbNoMissile, tbDifficulty, tbExit, tbBomb, MoneySet,
  tbHelp, BonusLevel;
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
 FROM ChaosBoss IMPORT bBrotherAlien, bSisterAlien, bMotherAlien,
  bFatherAlien, bMasterAlien1, bMasterAlien2;

  (* Drawing procs *)

 CONST
  fKmk = 0;
  fPic = 1;
  fMoneyS = 2;
  fMoneyMix = 3;
  fAlienColor = 4;
  fAlienFour = 5;
  fCannon1 = 6;
  fCannon2 = 7;
  fCartoon = 8;
  fNone = 9;
  fAnims1 = 10;
  fAnims2 = 11;
  fAnims3 = 12;
  fAnims4 = 13;
  fCrunchY = 14;
  fCrunchX = 15;

 PROCEDURE RectFill(sx, sy, ex, ey: INT16);
  VAR
   which, add: CARD16;
   s: INT16;
   count, c: CARD8;
 BEGIN
  REPEAT
   which:= RND() MOD 14
  UNTIL which IN fillTypes;
  IF fCrunchX IN fillTypes THEN
   DEC(sx, 2); INC(ex, 2); INC(sy); DEC(ey);
   Fill(sx, sy, ex, ey, Get(sx + 2, sy) MOD NbBackground)
  END;
  IF fCrunchY IN fillTypes THEN
   DEC(sy, 2); DEC(ey, 2); INC(sx); INC(sy);
   Fill(sx, sy, ex, ey, Get(sx, sy + 2) MOD NbBackground)
  END;
  Rect(sx, sy, ex, ey);
  count:= fillCount[which]; add:= fillRndAdd[which];
  IF add <> 0 THEN INC(count, RND() MOD (add + 1)) END;
  CASE which OF
    fKmk:
     count:= count DIV 4;
     PutKamikaze(0, count);
     PutKamikaze(1, count);
     PutKamikaze(2, count);
     PutKamikaze(3, count)
   |fPic:
     count:= count DIV 2;
     PutPic(0, count); PutPic(1, count)
   |fMoneyS:
     PutChaosSterling(count)
   |fMoneyMix:
     PutMoney(MoneySet{m1, m2, m5, st}, count)
   |fAlienColor:
     PutAColor(10, difficulty * 12, count)
   |fAlienFour:
     PutCFour(40, 40 + difficulty * 5, count)
   |fCannon1:
     c:= RND() MOD count;
     PutDeltaObjs(MACHINE, mCannon1, 0, -1, 0, c);
     c:= count - c;
     PutDeltaObjs(MACHINE, mCannon1, 1, 1, 0, c)
   |fCannon2:
     IF Get(sx, sy - 1) >= NbBackground THEN c:= 0; s:= -1 ELSE c:= 1; s:= 1 END;
     PutDeltaObjs(MACHINE, mCannon2, c, 0, s, count)
   |fCartoon:
     PutCartoon(0, 2, count)
   |fNone:
   |fAnims1:
     PutRandomObjs(fKind[1], fSubKind[1], aStat[1], count)
   |fAnims2:
     PutRandomObjs(fKind[2], fSubKind[2], aStat[2], count)
   |fAnims3:
     PutRandomObjs(fKind[3], fSubKind[3], aStat[3], count)
   |fAnims4:
     PutRandomObjs(fKind[4], fSubKind[4], aStat[4], count)
  END
 END RectFill;

 PROCEDURE AddOptions(sx, sy, ex, ey: INT16; nbGrid, nbBumper, nbChief,
                      nbGhost, nbPopup, nbBig, nbSquare: CARD16);
  PROCEDURE AddOption(kind: Anims; sKind: CARD8; cnt, min: CARD16);
  BEGIN
   IF (difficulty >= min) AND (cnt > 0) THEN
    PutRandomObjs(kind, sKind, pLife3 + difficulty * 2, cnt)
   END
  END AddOption;
 BEGIN
  Rect(sx, sy, ex, ey);
  AddOption(ALIEN2, cGrid, nbGrid, 2);
  AddOption(ALIEN1, aBumper, nbBumper, 3);
  AddOption(ALIEN2, cChief, nbChief, 4);
  AddOption(ALIEN2, cGhost, nbGhost, 5);
  AddOption(ALIEN2, cPopUp, nbPopup, 6);
  AddOption(ALIEN1, aBig, nbBig, 7);
  AddOption(ALIEN1, aSquare, nbSquare, 8)
 END AddOptions;

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
  FalseEmpty = 23;
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

(* Level 1 *)

 PROCEDURE DrawVertRocs(sx, sy, ey: INT16; val: CARD8);
  VAR
   rx, re, rh, x, y, oy, bx, ex, sh, eh: INT16;
 BEGIN
  y:= sy;
  LOOP
   oy:= y;
   INC(y, RND() MOD 5 + 4);
   IF y > ey THEN EXIT END;
   RectFill(sx, oy, sx + 13, y - 1);
   rx:= Rnd(8) + 1;
   re:= Rnd(9 - rx) + 4;
   rh:= Rnd(8) + 1;
   sh:= Rnd(rh) + 1; eh:= Rnd(rh) + 1;
   bx:= Rnd(re) + rx;
   ex:= Rnd(re) + rx;
   INC(re, rx);
   WHILE (y <= ey) AND (rh > 0) DO
    FOR x:= rx TO re DO
     IF (rh < sh) OR (rh > eh) OR (x < bx) OR (x > ex) THEN
      Put(sx + x, y, val)
     END
    END;
    INC(y); DEC(rh)
   END
  END
 END DrawVertRocs;

 PROCEDURE Entry;
  VAR
   val, cnt: CARD8;
 BEGIN
  fillTypes:= SET16{fMoneyS, fAlienColor, fAlienFour};
  fillCount[fMoneyS]:= 0; fillRndAdd[fMoneyS]:= 0;
  fillCount[fAlienColor]:= 1; fillRndAdd[fAlienColor]:= 6;
  fillCount[fAlienFour]:= 1; fillRndAdd[fAlienFour]:= 1;
  Cadre(16, 120);
  IF dualpf THEN val:= BackNone ELSE val:= Back8x8 END;
  Fill(1, 9, 14, 118, val);
  IF RND() MOD 8 = 0 THEN val:= BigBlock ELSE val:= EmptyBlock END;
  DrawVertRocs(1, 11, 117, val);
  FillCond(1, 9, 3, 118, OnlyBackground, Back4x4);
  FillCond(12, 9, 14, 118, OnlyBackground, Back4x4);
  cnt:= RND() MOD 4 + 4;
  WHILE cnt > 0 DO
   val:= RND() MOD 4;
   CASE val OF
     0: val:= Sq1Block
    |1: val:= Sq4Block
    |2: val:= EmptyBlock
    |3: val:= SimpleBlock
   END;
   PutCross(1, 9, 14, 117, val);
   DEC(cnt)
  END;
  PutRandom(2, 12, 13, 116, OnlyWall, Sq1Block, RND() MOD 64);
  PutRandom(2, 12, 13, 116, OnlyWall, Sq4Block, RND() MOD 32);
  PutRandom(2, 2, 13, 116, OnlyWall, SimpleBlock, RND() MOD 4);
  IF (difficulty >= 3) AND (RND() MOD 2 = 0) THEN
   FillCond(1, 9, 14, 118, OnlyBackground, Ice);
   snow:= TRUE
  END;
  Fill(1, 1, 14, 8, EmptyBlock);
  Fill(1, 1, 3, 3, BackSmall);
  Fill(9, 1, 14, 3, Back2x2);
  Fill(1, 4, 1, 6, FalseEmpty);
  Fill(2, 6, 8, 6, FalseEmpty);
  Fill(9, 4, 9, 7, FalseEmpty);
  Fill(14, 4, 14, 8, NbBackground - 1);
  IF powerCountDown > 10 THEN
   FillObj(SMARTBONUS, sbExtraPower, 0, 1, 1, 2, 1, FALSE)
  END;
  FillObj(SMARTBONUS, sbExtraLife, 0, 9, 1, 12, 1, FALSE);
  FillObj(BONUS, Money, ORD(st), 9, 2, 14, 3, TRUE);
  PutExtraPower(7, 2, 9);
  PutExtraPower(7, 1, 10);
  PutExtraPower(7, 1, 118);
  Rect(1, 9, 14, 118);
  PutIsolated(0, difficulty * 4 - 4, 12, 12, SimpleBlock);
  AddOptions(1, 17, 15, 117, 0, 0, 1, 0, 0, 5, 1);
  PutRandomObjs(BONUS, Money, ORD(st), 8 + RND() MOD 16);
  Rect(1, 50, 14, 70); PutHospital(1); PutTBonus(tbHelp, 1);
  Rect(1, 15, 14, 118); PutBullet(5);
  PutExit(14, 118);
  PutPlayer(1, 9)
 END Entry;

(* Level 2 *)

 PROCEDURE Groove;
  VAR
   x, y, z, dx, dy, angle, c: INT16;
   val: CARD8;
 BEGIN
  Cadre(100, 50);
  FOR y:= 0 TO 49 DO
   FOR x:= 0 TO 99 DO
    z:= y - 12 + Rnd(24);
    IF z <= 24 THEN
     val:= Fade3
    ELSIF z <= 40 THEN
     val:= Fade2
    ELSE
     val:= Fade1
    END;
    Put(x, y, val)
   END
  END;
  Excavate(1, 98, 1, 24, 4, 10, 8, 7, 4, 2, 1, 1, 6);
  Excavate(1, 98, 26, 39, 3, 7, 2, 5, 2, 1, 2, 1, 3);
  Excavate(1, 98, 41, 48, 2, 2, 5, 2, 6, 1, 0, 5, 10);
  Fill(98, 1, 98, 37, BackNone);
  Fill(1, 26, 1, 47, BackNone);
  Fill(98, 42, 98, 48, BackNone);
  FOR y:= 0 TO 49 DO
   FOR x:= 0 TO 99 DO
    z:= y - 4 + Rnd(8);
    IF z <= 20 THEN
     val:= BackNone
    ELSIF z <= 40 THEN
     val:= Tar
    ELSE
     val:= Balls
    END;
    IF OnlyBackground(x, y) THEN Put(x, y, val) END
   END
  END;
  Rect(2, 2, 99, 49);
  FOR c:= 1 TO 9 DO
   IF FindIsolatedRect(2, 2, 99, 49, 2, 10, angle, x, y, TRUE) THEN
    MakeLink(x, y, 0, angle, Round4);
    IF COS(angle) = 0 THEN dx:= 1 ELSE dx:= 0 END;
    dy:= 1 - dx;
    FOR z:= -2 TO 2 DO
     Put(x + z * dx, y + z * dy, Round4)
    END;
    Rect(x - 2, y - 2, x + 2, y + 2);
    PutBullet(1);
    IF c <= 4 THEN PutBullet(1) ELSE PutHospital(1) END
   END
  END;
  PutRandom(0, 0, 99, 49, OnlyWall, F9x9, RND() MOD (difficulty * 4 + 1));
  PutRandom(0, 0, 99, 49, OnlyWall, FRound, RND() MOD 32);
  PutRandom(0, 0, 99, 49, OnlyWall, FStar, RND() MOD (64 + difficulty * 6));
  Rect(51, 1, 98, 48);
  PutIsolated(2, difficulty + 2, 48, 48, SimpleBlock);
  Rect(2, 2, 99, 49);
  PutColor(pLife3, 60);
  PutCartoon(2, 2, 10);
  Rect(2, 2, 99, 40); PutFour(pLife3, 30);
  Rect(2, 20, 99, 49); PutQuad(pLife3, 12 + difficulty);
  IF stages >= 5 THEN PutTBonus(tbHelp, 1) END;
  IF stages = 0 THEN
   PutBlockBonus(tbNoMissile, 98, 2)
  END;
  PutExtraPower(3, 1, 26);
  PutExtraPower(3, 98, 1);
  FillObj(DEADOBJ, doCartoon, 0, 98, 3, 98, 10, FALSE);
  PutBlockBonus(tbInvinsibility, 98, 32);
  AddOptions(2, 2, 99, 49, 4, 8, 2, 3, 10, 6, 4);
  PutExit(98, 48);
  PutPlayer(1, 12)
 END Groove;

(* Level 3 *)

 PROCEDURE Garden;
 BEGIN
  Cadre(61, 61);
  DrawPacman(10 - difficulty, 6, 6, 6, 6, 54, 54);
  FillRandom(0, 0, 60, 60, Forest1, Forest7, OnlyWall, Rnd);
  FillCond(0, 0, 60, 60, OnlyBackground, Ground);
  Rect(1, 1, 59, 59);
  PutIsolated(0, 2, 10, 25, Leaf1);
  Rect(1, 1, 59, 59);
  PutIsolated(0, 8, 10, 25, Leaf2);
  Rect(1, 1, 59, 59);
  PutIsolated(0, 8, 10, 25, Leaf3);
  Rect(1, 1, 59, 59);
  PutIsolated(0, 7, 10, 25, SimpleBlock);
  IF (difficulty >= 3) AND (RND() MOD 2 = 0) THEN
   PutRandom(0, 0, 60, 60, OnlyBackground, Ground2, 100)
  END;
  FillObj(ALIEN1, aCartoon, 0, 29, 29, 31, 31, FALSE);
  PutExtraLife(1, 59);
  PutExtraLife(59, 1);
  Rect(30, 30, 40, 40); PutInvinsibility(1); PutTBonus(tbHelp, 1);
  PutGridObjs(BONUS, TimedBonus, tbHospital, 20, 20, 20, 20, 1, 1);
  Rect(2, 2, 58, 58);
  IF powerCountDown >= 6 THEN
   PutRandomObjs(SMARTBONUS, sbExtraPower, 0, 1)
  END;
  PutHospital(1);
  PutCartoon(0, 2, 20);
  PutNest(0, 40 + difficulty);
  AddOptions(2, 2, 59, 59, 4, 10, 2, 0, 1, 3, 0);
  PutExit(57, 57);
  PutPlayer(1, 1)
 END Garden;

(* Level 4 *)

 PROCEDURE Lake;
  VAR
   px, py, sz, d, angle: INT16;
   c, val: CARD8;
 BEGIN
  water:= (difficulty < 7) OR (RND() MOD 4 <> 0);
  Cadre(124, 124);
  Fill(0, 0, 123, 123, SimpleBlock);
  IF (RND() MOD 8 = 0) AND (difficulty > 2) THEN
   val:= Ground2
  ELSIF NOT(dualpf) THEN
   val:= Ground
  ELSE
   val:= BackNone
  END;
  TripleLoop(val);
  PutRandom(0, 0, 123, 123, OnlyWall, Leaf1, RND() MOD 256);
  PutRandom(0, 0, 123, 123, OnlyWall, Leaf2, RND() MOD 256);
  PutRandom(0, 0, 123, 123, OnlyWall, Leaf3, RND() MOD 256);
  PutRandom(0, 0, 123, 123, OnlyWall, Leaf4, RND() MOD 256);
  PutRandom(0, 0, 123, 123, OnlyWall, EmptyBlock, RND() MOD 64);
  PutRandom(0, 0, 123, 123, OnlyWall, SimpleBlock, RND() MOD 64);
  PutRandom(0, 0, 123, 123, OnlyWall, FStar, RND() MOD (difficulty * 20));
  PutRandom(0, 0, 123, 123, OnlyWall, BigBlock, RND() MOD (difficulty * 20));
  PutPlayer(104, 69); PutBlockBonus(tbHelp, 105, 69);
  PutExit(89, 53);
  PutBubbleMaker(ORD(difficulty < 6), 100, 62);
  PutBubbleMaker(ORD(difficulty < 6), 43, 29);
  PutBubbleMaker(ORD(difficulty < 6), 43, 95);
  IF difficulty < 4 THEN
   PutBubbleMaker(0, 90, 112);
   PutBubbleMaker(0, 90, 12);
   PutBubbleMaker(0, 5, 62)
  END;
  PutBlockBonus(tbHospital, 90, 113);
  PutBlockBonus(tbHospital, 90, 11);
  PutBlockBonus(tbHospital, 5, 63);
  PutBlockBonus(tbDBSpeed, 43, 62);
  IF difficulty >= 2 THEN
   fillTypes:= SET16{fNone, fAnims1, fAnims2, fAnims3, fAnims4};
   FOR c:= 0 TO 15 DO
    fillCount[c]:= 1; fillRndAdd[c]:= 0
   END;
   fKind[1]:= BONUS; fSubKind[1]:= TimedBonus; aStat[1]:= tbInvinsibility;
   fKind[2]:= BONUS; fSubKind[2]:= TimedBonus; aStat[2]:= tbSleeper;
   fKind[3]:= BONUS; fSubKind[3]:= TimedBonus; aStat[3]:= tbMagnet;
   fKind[4]:= BONUS; fSubKind[4]:= TimedBonus; aStat[4]:= tbBullet;
   FOR c:= 1 TO 10 DO
    sz:= RND() MOD 5 + 1; d:= sz * 2 + 1;
    IF FindIsolatedRect(0, 0, 123, 123, sz, 30, angle, px, py, TRUE) THEN
     Fill(px - sz, py - sz, px + sz, py + sz, Balls);
     FillEllipse(px - sz, py - sz, d, d, Tar);
     IF (c = 10) AND (stages = 0) THEN
      PutBlockBonus(tbNoMissile, px, py)
     END;
     RectFill(px - sz, py - sz, px + sz, py + sz);
     MakeLink(px, py, sz, angle, FalseBlock)
    END
   END
  END;
  Rect(50, 100, 120, 120); PutDeadObj(doWindMaker, 0, 3);
  PutChaosObjs(DEADOBJ, doSand, 0, 2000, 32, 2700, 400, 20);
  Rect(50, 1, 120, 62); PutQuad(pLife3, 4 + difficulty);
  Rect(1, 1, 50, 120); PutABox(0, 4 + difficulty DIV 2);
  Rect(1, 1, 70, 120); PutColor(pLife3, 20);
  Rect(30, 80, 60, 110); PutRandomObjs(BONUS, BonusLevel, tbBonusLevel, 1);
  Rect(50, 40, 100, 84); PutCannon3(5);
  Rect(1, 1, 122, 122); PutBullet(10);
  PutAlien1(aBigDrawer, 0, 1);
  PutNest(1, 20 + difficulty DIV 3);
  PutFour(pLife3, 10);
  PutCartoon(2, 2, 15);
  PutChaosSterling(20 - difficulty);
  IF difficulty >= 2 THEN
   PutTrefle(pLife2, 5)
  END;
  AddOptions(1, 1, 122, 122, 2, 0, 0, 0, 12, 0, 0);
  AddOptions(1, 60, 122, 122, 0, 0, 0, 0, 0, difficulty, 0);
  AddOptions(1, 1, 122, 60, 0, 0, 0, 0, 0, 0, difficulty);
  IF powerCountDown > 3 THEN
   FillCond(90, 12, 90, 28, OnlyWall, 21);
   PutExtraPower(3, 90, 28);
   FillObj(DEADOBJ, doCartoon, 0, 90, 24, 90, 27, FALSE)
  END
 END Lake;

(* Level 5 *)

 PROCEDURE Site;
  VAR
   a, x, y, sz, d: INT16;
   cnt: CARD16;
   val, val2: CARD8;
 BEGIN
  Clear(100, 100);
  Fill(0, 0, 99, 99, BackNone);
  Fill(40, 21, 40, 60, SimpleBlock);
  Fill(60, 40, 60, 79, SimpleBlock);
  cnt:= RND() MOD 15 + 7;
  WHILE cnt > 0 DO
   val:= RND() MOD 5;
   CASE val OF
     0: val:= Sq1Block; val2:= Sq4Block
    |1: val:= Sq4Block; val2:= Sq4TravBlock
    |2: val:= Sq4TravBlock; val2:= Sq4Block
    |3: val:= BarDark; val2:= BarDark
    |4: val:= BigBlock; val2:= EmptyBlock
   END;
   PutCross(20, 20, 60, 60, val);
   IF cnt < 8 THEN
    sz:= RND() MOD 3 + 1;
    IF FindIsolatedRect(22, 22, 58, 58, sz, 0, a, x, y, FALSE) THEN
     d:= sz * 2 + 1;
     FillEllipse(x - sz, y - sz, d, d, val);
     DEC(sz); DEC(d, 2);
     FillEllipse(x - sz, y - sz, d, d, val2)
    END
   END;
   DEC(cnt)
  END;
  Fill(20, 20, 79, 20, SimpleBlock);
  Fill(20, 79, 79, 79, SimpleBlock);
  Fill(20, 21, 20, 78, SimpleBlock);
  Fill(79, 21, 79, 77, SimpleBlock);
  Put(79, 78, 21);
  IF NOT(dualpf) THEN
   FillRandom(0, 0, 99, 99, 0, 7, OnlyBackground, ExpRandom)
  END;
  FillRandom(20, 20, 79, 79, 12, 13, OnlyBackground, ExpRandom);
  FillCond(45, 45, 54, 54, OnlyBackground, 20);
  PutPlayer(78, 78);
  PutExit(21, 21);
  PutExtraLife(21, 78);
  Rect(21, 21, 78, 78); PutTBonus(tbHelp, 1);
  PutAlien1(aSmallDrawer, 0, 1);
  PutFour(pLife3, 8);
  Rect(21, 21, 39, 78); PutCreatorR(10);
  Rect(30, 21, 60, 78); PutCreatorC(30);
  Rect(60, 21, 78, 78); PutAlien1(aStar, pLife3, 8);
  Rect(21, 40, 60, 78); PutTri(pLife3, 20);
  Rect(21, 21, 60, 60);
  PutInvinsibility(1);
  PutSleeper(1);
  IF (difficulty >= 3) OR NOT(registered) THEN
   PutMagnet(1); PutTurret(8);
   PutMagnetR(3, 6); PutMagnetA(1, 2)
  END;
  PutFreeFire(1);
  Rect(21, 21, 78, 78); PutBullet(30);
  Rect(0, 0, 99, 99); PutCannon3(7);
  FillObj(BONUS, Money, ORD(m10), 99, 0, 99, 0, TRUE);
  PutBlockBonus(tbBullet, 0, 99);
  PutBlockObj(DEADOBJ, doMagnetR, 3, 97, 97);
  PutExtraPower(5, 78, 21);
  PutBlockBonus(tbBomb, 0, 0);
  Fill(49, 49, 51, 51, 20);
  FillObj(ALIEN1, aCartoon, 0, 49, 49, 51, 51, FALSE);
  Rect(30, 30, 59, 59); PutAlien1(aHospital, 20 + difficulty, 3);
  AddOptions(21, 21, 60, 78, 0, 0, 5, 0, 8, 0, 0)
 END Site;

(* Level 6 *)

 PROCEDURE GhostCastle;
  VAR
   val: CARD8;
 BEGIN
  flipVert:= FALSE; rotate:= FALSE;
  Cadre(25, 70);
  Fill(1, 20, 22, 29, Bricks);
  IF dualpf THEN val:= BackNone ELSE val:= Round4 END;
  Fill(1, 20, 2, 21, val);
  Fill(16, 23, 22, 23, val);
  Fill(20, 31, 20, 69, Bricks);
  FillCond(0, 0, 24, 69, OnlyBackground, val);
  GCastle(1, 31, 19, 67, Bricks, 8);
  FillCond(0, 0, 24, 69, OnlyWall, Bricks);
  PutRandom(21, 31, 23, 69, OnlyBackground, BackBig, RND() MOD 16);
  PutRandom(21, 31, 23, 69, OnlyBackground, BackSmall, RND() MOD 16);
  PutRandom(21, 31, 23, 69, OnlyBackground, Back8x8, RND() MOD 16);
  PutRandom(21, 31, 23, 69, OnlyBackground, Back4x4, RND() MOD 16);
  PutRandom(2, 3, 19, 55, OnlyWall, IceBlock, RND() MOD 4);
  PutRandom(2, 3, 19, 55, OnlyWall, SimpleBlock, RND() MOD 4);
  PutRandom(2, 3, 19, 55, OnlyWall, EmptyBlock, RND() MOD 4);
  PutExtraLife(23, 1);
  PutExtraPower(1, 2, 21);
  PutBlockBonus(tbDBSpeed, 1, 21);
  PutBlockBonus(tbMagnet, 16, 23);
  PutBlockObj(DEADOBJ, doCartoon, 0, 22, 23);
  PutBlockObj(MACHINE, mDoor, 0, 23, 21);
  PutBlockObj(MACHINE, mReactor, 0, 23, 68);
  PutExit(21, 68); PutBlockBonus(tbHelp, 22, 68);
  Rect(1, 31, 19, 67); PutBullet(8);
  PutPlayer(1, 68);
  IF specialStage >= 4 THEN
   PutBlockObj(ALIEN3, bBrotherAlien, 0, 17, 3)
  END;
  FillObj(BONUS, Money, ORD(st), 11, 8, 13, 10, TRUE);
  Rect(1, 21, 19, 60); PutRandomObjs(ALIEN2, cGhost, pLife3, 10);
  Rect(21, 30, 23, 67); PutCartoon(0, 2, 15);
  PutIsolatedObjs(DEADOBJ, doFireMaker, 0, 0, 2, 4);
  IF difficulty > 1 THEN
   Rect(1, 21, 1, 66); PutMachine(mCannon1, 0, 0, 2);
   Rect(19, 21, 19, 66); PutMachine(mCannon2, 1, 1, 2);
   Rect(1, 1, 24, 1); PutMachine(mCannon2, 0, 0, 4)
  END;
  AddOptions(1, 1, 23, 65, 1, 1, 1, 1, 10, 1, 4)
 END GhostCastle;

(* Level 7 *)

 PROCEDURE Machinery;
  VAR
   x, y, w, dy: INT16;
 BEGIN
  Cadre(80, 40); rotate:= FALSE;
  Fill(11, 1, 78, 39, BarLight);
  fillTypes:= SET16{fCannon1, fCannon2, fAnims1, fCartoon, fNone};
  fillCount[fCannon1]:= 1; fillRndAdd[fCannon1]:= 3;
  fillCount[fCannon2]:= 1; fillRndAdd[fCannon2]:= 3;
  fillCount[fAnims1]:= 1; fillRndAdd[fAnims1]:= 0;
  fillCount[fCartoon]:= 1; fillRndAdd[fCartoon]:= 7;
  fKind[1]:= MACHINE; fSubKind[1]:= mCannon3; aStat[1]:= 0;
  x:= 11; y:= 1; w:= RND() MOD 4 + 3; dy:= -3;
  WHILE x <= 78 DO
   IF RND() MOD 4 = 0 THEN INCL(fillTypes, fCrunchX) ELSE EXCL(fillTypes, fCrunchX) END;
   IF x + w >= 73 THEN w:= 79 - x; EXCL(fillTypes, fCrunchX) END;
   Fill(x, y, x + w - 1, y + 5, 0);
   IF dy < 0 THEN
    RectFill(x, y, x + w - 1, y - dy - 1)
   ELSE
    RectFill(x, y + 6 - dy, x + w - 1, y + 5)
   END;
   INC(x, w);
   dy:= RND() MOD 3 + 3; IF RND() MOD 2 = 0 THEN dy:= -dy END;
   IF y < 6 THEN dy:= ABS(dy) ELSIF y > 10 THEN dy:= -ABS(dy) END;
   INC(y, dy);
   w:= RND() MOD 4 + 3
  END;
  Fill(78, y, 78, 35, 0);
  Cave(78, 21, 11, 38, 30, 35, -1);
  FillCond(0, 0, 79, 39, OnlyWall, BarLight);
  FillCond(0, 0, 79, 39, OnlyBackground, Light);
  PutPlayer(1, 38);
  Rect(1, 4, 1, 36); PutDeadObj(doMirror, 0, 12);
  Rect(10, 4, 10, 36); PutDeadObj(doMirror, 0, 12);
  Rect(2, 4, 9, 39); PutMachine(mCannon1, 0, 1, 20);
  Rect(2, 1, 9, 3); PutMachine(mCannon2, 0, 0, 3);
  Rect(11, 21, 78, 38); PutIsolatedObjs(DEADOBJ, doMirror, 1, 1, 1, 20);
  Rect(11, 21, 78, 38); PutTurret(15);
  Rect(5, 1, 78, 38); PutIsolatedObjs(MACHINE, mTraverse, 0, 1, 1, 15);
  Rect(1, 1, 78, 35); PutTBonus(tbHelp, 1);
  PutMagnetA(1, 4); PutMagnetR(3, 30);
  AddOptions(1, 1, 78, 38, 0, 0, 6, 0, 10, 0, 3)
 END Machinery;

(* Level 8 *)

 PROCEDURE IceRink;
  VAR
   c, x, y, a, dx, dy, sz, st: INT16;
   count: CARD16;
 BEGIN
  Clear(80, 80); snow:= TRUE;
  Fill(0, 0, 79, 79, IceBlock);
  PutRandom(0, 0, 79, 79, OnlyWall, BigBlock, 255);
  Fill(15, 15, 64, 64, IceBlock);
  Fill(19, 19, 60, 60, RGBBlock);
  Fill(20, 20, 59, 59, Ice);
  PutPlayer(59, 20);
  PutExit(20, 59);
  PutBlockObj(MACHINE, mTurret, 0, 40, 40);
  PutBlockObj(MACHINE, mCannon3, 0, 40, 40);
  count:= RND() MOD 128; IF count > 64 THEN count:= count * 2 END;
  Rect(20, 20, 59, 59); PutIsolated(count, count, 40, 40, IceBlock);
  count:= count DIV 16; PutIsolated(count, count, 20, 40, RGBBlock);
  fillCount[fMoneyMix]:= 3; fillRndAdd[fMoneyMix]:= 6;
  fillCount[fAnims1]:= 1; fillRndAdd[fAnims1]:= 4;
  fKind[1]:= ALIEN2; fSubKind[1]:= cNest; aStat[1]:= 0;
  fillCount[fAnims2]:= 1; fillRndAdd[fAnims2]:= 4;
  fKind[2]:= ALIEN2; fSubKind[2]:= cAlienBox; aStat[2]:= 1;
  count:= RND() MOD 4 + 6;
  WHILE count > 0 DO
   x:= Rnd(40) + 20; y:= Rnd(40) + 20;
   IF RND() MOD 2 = 0 THEN dx:= 1; dy:= 0 ELSE dx:= 0; dy:= 1 END;
   IF RND() MOD 2 = 0 THEN dx:= -dx; dy:= -dy END;
   WHILE (x <> 19) AND (x <> 60) AND (y <> 19) AND (y <> 60) DO
    INC(x, dx); INC(y, dy)
   END;
   sz:= RND() MOD 8 + 5; st:= sz;
   REPEAT
    Put(x, y, Ice);
    IF sz = st THEN PutBlockObj(DEADOBJ, doCartoon, 0, x, y) END;
    INC(x, dx); INC(y, dy);
    DEC(sz)
   UNTIL sz = 0;
   sz:= RND() MOD 3 + 3;
   Fill(x - sz, y - sz, x + sz, y + sz, Ice);
   fillTypes:= SET16{fAnims1, fAnims2};
   IF RND() MOD 4 = 0 THEN INCL(fillTypes, fCrunchX) END;
   IF RND() MOD 4 = 0 THEN INCL(fillTypes, fCrunchY) END;
   RectFill(x - sz, y - sz, x + sz, y + sz);
   DEC(count)
  END;
  FOR c:= 1 TO Rnd(4) + 6 DO
   sz:= RND() MOD 5 + 2;
   IF FindIsolatedRect(0, 0, 79, 79, sz, 10, a, x, y, TRUE) THEN
    MakeLink(x, y, 0, a, Ice);
    dx:= sz * 2 + 1; dy:= sz DIV 2 * 2 + 1;
    FillEllipse(x - sz, y - sz DIV 2, dx, dy, Ice);
    fillTypes:= SET16{fMoneyMix};
    RectFill(x - sz, y - sz DIV 2, x + sz, y + sz DIV 2)
   END
  END;
  PutExtraLife(20, 20);
  PutExtraPower(5, 59, 59);
  PutGridObjs(BONUS, TimedBonus, tbHospital, 25, 25, 15, 15, 2, 2);
  Rect(30, 20, 59, 50); PutMaxPower(1);
  Rect(20, 20, 59, 59);
  PutTBonus(tbHelp, 1);
  PutTrefle(pLife, 10);
  PutAlien1(aBubble, pLife2, 50);
  PutCartoon(0, 0, 20);
  Rect(1, 1, 79, 79); PutBullet(16);
  Rect(40, 40, 59, 59);
  PutQuad(pLife2, 20);
  PutTri(pLife3, difficulty);
  AddOptions(1, 1, 79, 79, 4, 4, 0, 0, 1, 0, 0)
 END IceRink;

(* Level 9 *)

 PROCEDURE Factory;
  VAR
   c, x, y, dx, dy, a, z, sz, d: INT16;
   sk: CARD8;
 BEGIN
  Clear(101, 101);
  IF RND() MOD 8 <> 0 THEN rotate:= FALSE END;
  water:= (difficulty >= 4) AND (RND() MOD 3 = 0);
  Fill(0, 0, 100, 100, EmptyBlock);
  DrawFactory;
  PutPlayer(12, 90); PutBlockBonus(tbHelp, 12, 88);
  Rect(1, 1, 98, 98);
  PutIsolatedObjs(DEADOBJ, doWindMaker, 0, 1, 1, 6);
  PutIsolatedObjs(DEADOBJ, doBubbleMaker, 1, 1, 1, 15);
  PutIsolatedObjs(DEADOBJ, doFireMaker, 0, 0, 2, 15);
  Rect(33, 1, 98, 98);
  PutKamikaze(0, 6); PutKamikaze(1, 6);
  PutKamikaze(2, 6); PutKamikaze(3, 6);
  PutPic(0, 10); PutPic(1, 10);
  Rect(1, 1, 98, 66); PutTurret(15);
  Rect(70, 1, 98, 98); PutTri(pLife3, 7);
  Rect(1, 1, 98, 98);
  PutCannon3(6);
  PutCartoon(0, 1, 25);
  PutMagnetA(3, 15);
  PutMagnetR(3, 5);
  FOR c:= 1 TO 10 DO
   IF FindIsolatedRect(0, 0, 99, 99, 2, 8, a, x, y, TRUE) THEN
    MakeLink(x, y, 0, a, BackBig);
    IF COS(a) = 0 THEN dx:= 1; dy:= 0 ELSE dx:= 0; dy:= 1 END;
    FOR z:= -2 TO 2 DO
     Put(x + z * dx, y + z * dy, BackSmall);
     Put(x - 2 * dx + z * dy, y - 2 * dy + z * dx, BackSmall);
     Put(x + 2 * dx + z * dy, y + 2 * dy + z * dx, BackSmall)
    END;
    Rect(x - 2, y - 2, x + 2, y + 2);
    PutBullet(1); PutAlien1(aHospital, pLife2, 1)
   END
  END;
  IF FindIsolatedRect(0, 0, 99, 99, 1, 20, a, x, y, TRUE) THEN
   Fill(x - 1, y - 1, x + 1, y + 1, Ice);
   PutBlockObj(BONUS, BonusLevel, tbBonusLevel, x, y);
   MakeLink(x, y, 1, a, 23)
  END;
  IF difficulty > 2 THEN
   FOR c:= 0 TO 6 DO
    sz:= RND() MOD 3 + 1;
    IF FindIsolatedRect(0, 0, 99, 99, sz, 20, a, x, y, TRUE) THEN
     Rect(x - sz, y - sz, x + sz, y + sz);
     d:= sz * 2 + 1;
     MakeLink(x, y, sz, a, FalseEmpty);
     FillEllipse(x - sz, y - sz, d, d, BackNone);
     IF sz >= 2 THEN DEC(sz, 2) ELSE DEC(sz) END;
     d:= 2 * sz + 1;
     FillEllipse(x - sz, y - sz, d, d, SimpleBlock);
     sk:= RND() MOD 4;
     IF sk = 0 THEN sk:= aTri ELSIF sk = 1 THEN sk:= aDiese ELSE sk:= aBumper END;
     PutAlien1(sk, pLife2, ORD(c = 0) * 20 + 1)
    END
   END
  END;
  PutRandom(0, 0, 99, 99, OnlyWall, Sq1Block, RND() MOD 64);
  PutRandom(0, 0, 99, 99, OnlyWall, Sq4Block, RND() MOD 64);
  PutRandom(10, 0, 99, 99, OnlyWall, Sq4TravBlock, RND() MOD 128);
  PutRandom(20, 0, 99, 99, OnlyWall, TravBlock, RND() MOD 128);
  PutRandom(30, 0, 99, 79, OnlyWall, Fact1Block, RND() MOD 256);
  PutRandom(30, 0, 79, 99, OnlyWall, Fact2Block, RND() MOD 256);
  PutRandom(0, 0, 99, 59, OnlyWall, Fact3Block, RND() MOD 256);
  AddOptions(30, 1, 98, 98, 5, 5, 3, 4, 1, 0, 0)
 END Factory;

(* Level 10 *)

 PROCEDURE Labyrinth;
  VAR
   i: INT16;
   val: CARD8;
 BEGIN
  Cadre(120, 101); flipVert:= FALSE; rotate:= FALSE;
  Fill(1, 1, 100, 100, NbBackground);
  Fill(101, 0, 119, 100, BackNone);
  DrawLabyrinth(50);
  FillRandom(0, 0, 100, 100, Granit1, Granit2, OnlyWall, Rnd);
  IF dualpf THEN val:= BackNone ELSE val:= Back2x2 END;
  FillCond(1, 1, 99, 99, OnlyBackground, val);
  Fill(1, 1, 2, 2, Back2x2);
  Put(100, 99, BackNone);
  RemIsolated(2, 2, 98, 98, 2, 2, BackSmall);
  Fill(101, 30, 119, 30, SimpleBlock);
  Fill(106, 33, 111, 33, SimpleBlock);
  Fill(114, 33, 119, 33, SimpleBlock);
  Fill(109, 35, 116, 35, SimpleBlock);
  Fill(101, 100, 119, 100, SimpleBlock);
  Fill(102, 50, 106, 50, SimpleBlock);
  Fill(101, 52, 105, 69, SimpleBlock);
  Fill(101, 90, 105, 90, SimpleBlock);
  Fill(103, 52, 104, 69, FalseBlock);
  Put(102, 90, BackNone);
  Fill(119, 30, 119, 100, SimpleBlock);
  Fill(106, 34, 106, 99, SimpleBlock);
  Fill(102, 40, 102, 50, SimpleBlock);
  Fill(104, 30, 104, 40, SimpleBlock);
  Put(119, 30, FalseBlock);
  PutPlayer(1, 1);
  PutBlockObj(MACHINE, mDoor, 0, 102, 90);
  PutBlockObj(MACHINE, mDoor, 0, 103, 64);
  PutBlockObj(MACHINE, mDoor, 0, 104, 64);
  PutBlockObj(MACHINE, mDoor, 0, 103, 40);
  PutExtraLife(99, 1);
  PutExtraLife(1, 99);
  PutExtraPower(0, 101, 70);
  PutBlockBonus(tbDBSpeed, 99, 99);
  PutBlockBonus(tbMagnet, 105, 70);
  PutBlockObj(MACHINE, mReactor, 0, 102, 98);
  PutBlockBonus(tbMagnet, 105, 4);
  PutBlockBonus(tbDifficulty, 110, 15);
  PutExit(105, 99);
  PutExit(110, 15);
  PutExit(2, 1);
  PutExit(118, 99);
  FOR i:= tbDBSpeed TO tbNoMissile DO
   PutBlockBonus(i, 105, 49 - i)
  END;
  Rect(1, 1, 99, 99);
  PutTBonus(tbDBSpeed, 5);
  PutTBonus(tbSGSpeed, 1);
  PutMagnet(5);
  PutInvinsibility(3);
  PutSleeper(2);
  PutBullet(10);
  PutHospital(15);
  PutMoney(MoneySet{st}, 50);
  Rect(107, 50, 119, 99); PutAlien2(cCircle, 200, 10);
  AddOptions(1, 1, 99, 99, 5, 5, 4, 8, 45, 3, 4)
 END Labyrinth;

 PROCEDURE Init;
  VAR
   x: CARD16;
 BEGIN
  FOR x:= 0 TO 15 DO fillCount[x]:= 0; fillRndAdd[x]:= 0 END;
 END Init;

BEGIN

 Init;

END Chaos1Zone.
