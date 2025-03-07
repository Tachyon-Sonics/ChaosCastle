IMPLEMENTATION MODULE Chaos2Zone;

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
  Rect, PutIsolated;
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
  bFatherAlien, bMasterAlien1, bMasterAlien2, bMasterEye, bMasterMouth;
 FROM Chaos1Zone IMPORT fillTypes, fillCount, fillRndAdd, fKind, fSubKind,
  aStat, AddOptions, RectFill, flipVert, flipHorz, rotate, pLife2, pLife3,
  pLife4;

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

(* Level 11 *)

 PROCEDURE Rooms;
  VAR
   c: CARD16;
   x, y, sz, d, angle: INT16;
   val: CARD8;
 BEGIN
  Clear(60, 60);
  Fill(0, 0, 59, 59, EmptyBlock);
  IF dualpf THEN val:= BackNone ELSE val:= Back8x8 END;
  Fill(10, 9, 49, 11, val);
  Fill(10, 29, 49, 31, val);
  Fill(47, 12, 49, 28, val);
  Fill(10, 32, 12, 51, val);
  PutPlayer(11, 10);
  PutExit(11, 50);
  Rect(11, 9, 48, 11); PutMachine(mTraverse, 1, 1, 1);
  Rect(11, 29, 48, 31); PutMachine(mTraverse, 1, 1, 3);
  fillTypes:= SET16{fAlienColor, fAlienFour, fCartoon, fNone, fAnims1, fAnims2, fAnims3, fAnims4};
  FOR c:= 0 TO 15 DO
   fillCount[c]:= 3; fillRndAdd[c]:= 2
  END;
  fKind[1]:= ALIEN2; fSubKind[1]:= cNest; aStat[1]:= 0;
  fKind[2]:= ALIEN2; fSubKind[2]:= cQuad; aStat[2]:= pLife3;
  fKind[3]:= ALIEN2; fSubKind[3]:= cCreatorC; aStat[3]:= pLife3 + 40 + difficulty * 4;
  fKind[4]:= ALIEN2; fSubKind[4]:= cCreatorR; aStat[4]:= pLife3 + 40 + difficulty * 4;
  FOR c:= 1 TO 40 DO
   sz:= RND() MOD 4 + 1; d:= sz * 2 + 1;
   IF FindIsolatedRect(0, 0, 59, 59, sz, 4, angle, x, y, TRUE) THEN
    IF RND() MOD 2 = 0 THEN
     Fill(x - sz, y - sz, x + sz, y + sz, Back4x4)
    ELSE
     FillEllipse(x - sz, y - sz, d, d, Back4x4)
    END;
    RectFill(x - sz, y - sz, x + sz, y + sz);
    IF c >= 34 THEN val:= FalseEmpty ELSE val:= Back2x2 END;
    MakeLink(x, y, sz, angle, val)
   END
  END;
  PutRandom(0, 0, 29, 29, OnlyWall, Sq1Block, 60);
  PutRandom(30, 0, 59, 29, OnlyWall, Sq4Block, 60);
  PutRandom(30, 30, 59, 59, OnlyWall, Sq4TravBlock, 60);
  PutRandom(0, 30, 29, 59, OnlyWall, BigBlock, 60);
  PutGridObjs(BONUS, TimedBonus, tbHospital, 10, 10, 19, 20, 2, 2);
  Rect(1, 40, 58, 58); PutRAlien2(cAlienBox, 0, 1, 5);
  Rect(1, 1, 58, 58); PutBullet(8);
  Rect(1, 1, 29, 20); PutInvinsibility(1);
  Rect(30, 1, 58, 20); PutFreeFire(1);
  Rect(30, 21, 58, 40); PutSleeper(1);
  Rect(1, 21, 29, 40); PutMaxPower(1);
  Rect(1, 35, 58, 58); PutMagnet(1);
  Rect(1, 1, 58, 58); PutTBonus(tbHelp, 1);
  IF powerCountDown > 1 THEN
   PutRandomObjs(SMARTBONUS, sbExtraPower, 0, 1)
  END;
  PutRandomObjs(SMARTBONUS, sbExtraLife, 0, 2);
  AddOptions(1, 1, 58, 58, 0, 0, 1, 0, 10, 0, 0)
 END Rooms;

(* Level 12 *)

 PROCEDURE Yard;

  PROCEDURE RndRect;
   VAR
    x, y: INT16;
  BEGIN
   x:= Rnd(35); y:= Rnd(35);
   Rect(x, y, x + 3, y + 3)
  END RndRect;

 BEGIN
  Cadre(40, 40);
  FillCond(0, 0, 40, 40, OnlyWall, TravLight);
  Rect(1, 1, 38, 38);
  PutIsolated(5, 20, 10, 30, BarDark);
  FillRandom(1, 1, 39, 39, Back4x4, Back8x8, OnlyBackground, ExpRandom);
  FillCond(18, 18, 22, 22, OnlyBackground, Light);
  IF dualpf THEN
   PutRandom(1, 1, 39, 39, OnlyBackground, BackNone, 255)
  END;
  PutPlayer(2, 2);
  IF (nbSterling >= 190) AND (difficulty >= 2) THEN
   PutBlockObj(ALIEN3, bMasterEye, 0, 19, 20);
   PutBlockObj(ALIEN3, bMasterEye, 1, 21, 20);
   PutBlockObj(ALIEN3, bMasterMouth, 0, 20, 20);
   PutBlockObj(ALIEN3, bMasterAlien1, 0, 20, 20)
  END;
  Rect(20, 20, 38, 38); PutTBonus(tbExit, 1);
  Rect(3, 3, 38, 38);
  PutMagnetR(3, 15); PutMagnetA(3, 15);
  FillObj(ALIEN1, aCartoon, 0, 1, 19, 38, 19, FALSE);
  Rect(10, 3, 29, 38); PutMachine(mCannon1, 0, 1, 5);
  Rect(3, 10, 38, 29); PutMachine(mCannon2, 0, 1, 5);
  Rect(10, 10, 29, 29);
  PutCannon3(5); PutTurret(7);
  Rect(3, 3, 38, 38);
  PutAlien1(aHospital, pLife3, 10);
  PutRAlien1(aKamikaze, 0, 3, 10);
  PutAlien2(cAlienV, pLife3, 10);
  PutFour(pLife3, 10);
  PutBullet(4);
  RndRect; PutTrefle(pLife2, 5);
  RndRect; PutAlien2(cAlienA, pLife3, 10);
  RndRect; PutTri(pLife3, 5);
  Rect(20, 20, 38, 38); PutNest(1, 15);
  Rect(1, 1, 7, 7);
  PutFreeFire(1); PutMaxPower(1);
  AddOptions(10, 10, 38, 38, 5, 5, 1, 3, 0, 5, 3)
 END Yard;

(* Level 13 *)

 PROCEDURE Antarctica;
  CONST
   W = 120; H = 60;
   MX = W DIV 2; MY = H DIV 2;
  VAR
   lx, ly, sz, at: INT16;
   c: CARD16;
   val: CARD8;
 BEGIN
  Clear(W, H);
  water:= (difficulty < 5) OR (RND() MOD 3 <> 0);
  snow:= (stages > 0) OR (RND() MOD 3 <> 0);
  Fill(0, 0, W - 1, H - 1, IceBlock);
  VRace(Ice);
  MakeLink(W DIV 2, H * 2 DIV 5 - 1, 0, 90, Ice);
  PutPlayer(W DIV 2, H * 2 DIV 5);
  Rect(MX DIV 2, 0, MX - 1, MY - 1); PutTBonus(tbExit, 1);
  Rect(0, 0, W DIV 3, MY); PutDeadObj(doBubbleMaker, 0, 1);
  Rect(W * 2 DIV 3, 0, W - 1, MY - 1); PutDeadObj(doBubbleMaker, 0, 1);
  Rect(0, MY, W DIV 3, H - 1); PutDeadObj(doBubbleMaker, 0, 1);
  Rect(W * 2 DIV 3, MY, W - 1, H - 1); PutDeadObj(doBubbleMaker, 0, 1);
  Rect(MX, MY, W * 2 DIV 3, H - 1); PutTBonus(tbDBSpeed, 1);
  PutChaosObjs(DEADOBJ, doSand, 0, 0, 0, W * 10, H * 10, 20);
  Rect(W * 2 DIV 3, 0, W - 1, H - 1); PutDeadObj(doWindMaker, 0, 4);
  Rect(0, MY, MX, H - 1); PutTurret(4);
  Rect(1, 1, W - 2, H - 2);
  PutMagnetR(2, 8);
  PutIsolatedObjs(DEADOBJ, doMirror, 0, 1, 1, 15);
  PutCannon3(2); PutMachine(mTraverse, 0, 1, 4);
  PutNest(0, 4 + difficulty);
  PutCartoon(0, 2, 15);
  Rect(0, MY, W - 1, H - 1); PutRAlien1(aKamikaze, 0, 3, 20);
  fillTypes:= SET16{fNone, fMoneyS, fAlienFour, fAnims1, fAnims2};
  fillCount[fMoneyS]:= 4; fillRndAdd[fMoneyS]:= 4;
  fillCount[fAlienFour]:= 1; fillRndAdd[fAlienFour]:= 3;
  fillCount[fAnims1]:= 1; fillRndAdd[fAnims1]:= 1;
  fillCount[fAnims2]:= 1; fillRndAdd[fAnims2]:= 0;
  fKind[1]:= ALIEN2; fSubKind[1]:= cAlienBox; aStat[1]:= 0;
  fKind[2]:= BONUS; fSubKind[2]:= TimedBonus; aStat[2]:= tbBullet;
  FOR c:= 1 TO 10 + RND() MOD 8 DO
   sz:= RND() MOD 4 + 2;
   IF FindIsolatedRect(2, 2, W - 3, H - 3, sz, 30, at, lx, ly, TRUE) THEN
    Fill(lx - sz + 1, ly - sz, lx + sz - 1, ly + sz, Balls);
    Fill(lx - sz, ly - sz + 1, lx + sz, ly + sz - 1, Balls);
    Fill(lx - sz + 1, ly - sz + 1, lx + sz - 1, ly + sz - 1, Ice);
    PutBubbleMaker(0, lx, ly);
    PutBlockBonus(tbHospital, lx, ly - 1);
    PutBlockBonus(tbBullet, lx, ly + 1);
    IF (c = 16) THEN
     IF stages = 0 THEN PutBlockBonus(tbNoMissile, lx + 1, ly) END;
     PutExtraPower(1, lx - 1, ly)
    END;
    RectFill(lx - sz, ly - sz, lx + sz, ly + sz);
    IF c > 10 THEN val:= FalseBlock ELSE val:= Ice END;
    MakeLink(lx, ly, sz, at, val)
   END
  END;
  Rect(1, 1, W - 2, H - 2);
  PutRandom(0, 0, W - 1, H - 1, OnlyWall, SimpleBlock, RND() MOD 256);
  PutRandom(0, 0, W - 1, H - 1, OnlyWall, BigBlock, RND() MOD 256);
  PutRandom(0, 0, W - 1, H - 1, OnlyWall, Leaf2, RND() MOD 64);
  PutRandom(0, 0, W - 1, H - 1, OnlyWall, Leaf3, RND() MOD 32);
  PutRandomObjs(BONUS, BonusLevel, tbBonusLevel, 1);
  IF (stages = 0) AND (difficulty >= 9) AND (RND() MOD 4 = 0) THEN
   PutRandomObjs(ALIEN3, bSisterAlien, 0, 1)
  END;
  PutRandomObjs(SMARTBONUS, sbExtraLife, 0, 1);
  AddOptions(1, 1, W - 2, H - 2, 4, 4, 0, 2, 10, 0, 1)
 END Antarctica;

(* Level 14 *)

 PROCEDURE Forest;
  VAR
   c: CARD16;
   x, y, sz, d, angle: INT16;
 BEGIN
  Clear(60, 60); rotate:= FALSE;
  Fill(0, 0, 59, 59, NbBackground);
  FillEllipse(26, 26, 8, 8, Ground);
  PutPlayer(29, 29); PutExit(30, 30);
  fillTypes:= SET16{fCartoon, fNone, fPic, fAnims2, fAnims3, fAnims4};
  FOR c:= 0 TO 15 DO
   fillCount[c]:= 2; fillRndAdd[c]:= 2
  END;
  fKind[2]:= ALIEN2; fSubKind[2]:= cNest; aStat[2]:= 0;
  fKind[3]:= MACHINE; fSubKind[3]:= mTurret; aStat[3]:= 0;
  fKind[4]:= ALIEN1; fSubKind[4]:= aTrefle; aStat[4]:= pLife4;
  FOR c:= 1 TO 45 DO
   sz:= RND() MOD 4 + 1; d:= sz * 2 + 1;
   IF FindIsolatedRect(0, 0, 59, 59, sz, 10, angle, x, y, TRUE) THEN
    FillEllipse(x - sz, y - sz, d, d, Ground);
    RectFill(x - sz, y - sz, x + sz, y + sz);
    MakeLink(x, y, sz, angle, Ground2)
   END
  END;
  FillRandom(0, 0, 59, 59, Forest1, Forest7, OnlyWall, Rnd);
  Rect(26, 26, 33, 33);
  FOR c:= 2 TO 4 DO
   PutRandomObjs(fKind[c], fSubKind[c], aStat[c], 12)
  END;
  Rect(1, 1, 58, 58);
  PutBullet(10); PutAlien1(aHospital, pLife4, 10);
  PutSleeper(1); PutInvinsibility(1); PutFreeFire(1);
  IF powerCountDown > 2 THEN
   PutRandomObjs(SMARTBONUS, sbExtraPower, 0, 1)
  END;
  IF stages = 0 THEN
   PutTBonus(tbNoMissile, 1)
  END;
  AddOptions(1, 1, 58, 58, 0, 10, 2, 10, 10, 4, 0)
 END Forest;

(* Level 15 *)

 PROCEDURE ZCastle;
  VAR
   val: CARD8;
 BEGIN
  Cadre(40, 40);
  snow:= (difficulty >= 5) AND (RND() MOD 3 = 0);
  DrawCastle(2, 1, 36, 37);
  FillCond(0, 0, 39, 39, OnlyWall, SimpleBlock);
  IF dualpf THEN val:= BackNone ELSE val:= Back4x4 END;
  FillCond(0, 0, 39, 39, OnlyBackground, val);
  PutRandom(1, 1, 38, 38, OnlyBackground, Back8x8, 40);
  PutRandom(1, 1, 38, 38, OnlyBackground, Back2x2, 40);
  PutRandom(1, 1, 38, 38, OnlyWall, Sq4Block, 10);
  PutRandom(1, 1, 38, 38, OnlyWall, BigBlock, 10);
  Put(1, 18, FalseBlock);
  PutRandom(1, 1, 38, 38, OnlyWall, FalseBlock, difficulty);
  PutPlayer(1, 1); PutExit(38, 38);
  Rect(1, 1, 38, 19); PutQuad(pLife4, 20);
  Rect(1, 20, 38, 38); PutFour(pLife4, 30);
  Rect(1, 1, 19, 38); PutAlien1(aHospital, pLife3, 8); PutBullet(8);
  Rect(1, 1, 38, 38); PutTBonus(tbHelp, 1);
  PutCartoon(0, 0, 50);
  PutIsolatedObjs(DEADOBJ, doWindMaker, 0, 1, 0, 3);
  Rect(20, 1, 38, 25);
  PutInvinsibility(1);
  PutSleeper(1);
  PutTBonus(tbDBSpeed, 1);
  AddOptions(1, 20, 38, 38, 1, 20, 1, 10, 10, 0, 0)
 END ZCastle;

(* Level 16 *)

 PROCEDURE Lights;
  VAR
   x, y, z: INT16;
   val: CARD8;
 BEGIN
  Clear(127, 20);
  water:= (difficulty >= 2) AND (RND() MOD 6 = 0);
  Fill(0, 0, 39, 0, BarLight);
  Fill(0, 0, 0, 19, BarLight);
  FOR y:= 0 TO 19 DO
   FOR x:= 40 TO 127 DO
    z:= x - 15 + Rnd(30);
    IF z < 60 THEN
     val:= BackNone
    ELSIF z < 80 THEN
     val:= Back8x8
    ELSIF z < 100 THEN
     val:= Back4x4
    ELSE
     val:= Back2x2
    END;
    Put(x, y, val)
   END
  END;
  DrawBoxes(0, 0, 40, 20);
  Put(39, 18, 0);
  FillCond(0, 0, 39, 19, OnlyBackground, Light);
  FillCond(0, 0, 39, 19, OnlyWall, BarLight);
  PutPlayer(38, 1); PutExit(39, 18);
  Rect(50, 0, 127, 19); PutMoney(MoneySet{st}, 15);
  IF powerCountDown > 2 THEN
   PutBlockBonus(tbDBSpeed, 45, 16);
   PutExtraPower(2, 126, 18)
  END;
  Rect(1, 1, 38, 18); PutIsolatedObjs(DEADOBJ, doMirror, 0, 1, 0, 10);
  Rect(1, 1, 30, 18); PutMagnetR(3, 8); PutMagnetA(2, 8);
  Rect(1, 1, 38, 9);
  PutIsolatedObjs(DEADOBJ, doWindMaker, 0, 1, 0, 3);
  PutIsolatedObjs(DEADOBJ, doBubbleMaker, 0, 1, 0, 3);
  PutIsolatedObjs(DEADOBJ, doFireMaker, 0, 1, 0, 3);
  PutChaosObjs(DEADOBJ, doSand, 0, BW, BH, BW * 8, BH * 8, 16);
  Rect(1, 9, 38, 17);
  PutDeltaObjs(MACHINE, mCannon1, 0, -1, -1, 3);
  PutDeltaObjs(MACHINE, mCannon1, 1, 1, 1, 3);
  PutDeltaObjs(MACHINE, mCannon2, 0, -1, -1, 3);
  PutDeltaObjs(MACHINE, mCannon2, 1, 1, 1, 3);
  PutTurret(2);
  Rect(1, 1, 38, 18);
  PutCreatorC(12);
  PutCartoon(0, 2, 20);
  PutBullet(8); PutHospital(5);
  Rect(25, 12, 38, 18); PutMagnet(1);
  PutBlockObj(DEADOBJ, doMagnetA, 3, 50, 10);
  PutBlockObj(DEADOBJ, doMagnetA, 3, 50, 10);
  PutExit(50, 10);
  AddOptions(60, 1, 120, 18, 4, 0, 0, 8, 10, 1, 1)
 END Lights;

(* Level 17 *)

 PROCEDURE Plain;
  VAR
   x, y, a, sz, d, dx, dy: INT16;
   c: CARD16;
   val: CARD8;
  PROCEDURE RndRect;
  BEGIN
   x:= Rnd(51) + 4; y:= Rnd(49) + 6;
   Rect(x - 4, y - 4, x + 4, y + 4)
  END RndRect;
 BEGIN
  Cadre(60, 60); Clear(60, 70);
  FillCond(0, 0, 59, 59, OnlyWall, SimpleBlock);
  Fill(0, 60, 59, 69, BackNone);
  FOR c:= 1 TO 4 + RND() MOD 16 DO
   sz:= RND() MOD 3 + 1; d:= sz * 2 + 1;
   IF FindIsolatedRect(1, 1, 58, 58, sz, 0, a, x, y, FALSE) THEN
    val:= RND() MOD 4;
    CASE val OF
      0: val:= Sq1Block
     |1: val:= EmptyBlock
     ELSE val:= Sq4Block
    END;
    IF RND() MOD 3 = 0 THEN
     FillEllipse(x - sz, y - sz, d, d, val)
    ELSE
     Fill(x - sz, y - sz, x + sz, y + sz, val)
    END;
    IF (val = EmptyBlock) AND (c > 10) THEN
     Put(x - sz, y, FalseEmpty); DEC(sz);
     Fill(x - sz, y - sz, x + sz, y + sz, FalseEmpty);
     IF stages = 0 THEN PutBlockBonus(tbNoMissile, x, y) END
    ELSIF RND() MOD 3 = 0 THEN
     dx:= Rnd(3) - 1; dy:= Rnd(3) - 1;
     IF (dx <> 0) OR (dy <> 0) THEN
      DEC(d, 2); DEC(sz); INC(x, dx); INC(y, dy);
      FillEllipse(x - sz, y - sz, d, d, Ground)
     END
    END
   END
  END;
  FOR c:= 1 TO 4 + RND() MOD 32 DO
   val:= RND() MOD 4;
   CASE val OF
     0: val:= BigBlock
    |1: val:= Leaf3
    |2: val:= Fade3
    |3: val:= RGBBlock
   END;
   PutCross(1, 1, 58, 58, val)
  END;
  FillCond(1, 1, 58, 58, OnlyBackground, Ground);
  PutRandom(1, 1, 58, 58, OnlyBackground, Ground2, 180);
  Put(Rnd(58) + 1, 59, FalseBlock);
  PutPlayer(1, 1); PutExit(58, 58);
  RndRect; PutABox(0, 4);
  RndRect; PutABox(1, 4);
  RndRect; PutNest(0, 10);
  RndRect; PutTri(pLife4, 8);
  RndRect; PutAlien1(aStar, pLife4, 15);
  RndRect; PutAlien1(aBubble, pLife4, 15);
  RndRect; PutTrefle(pLife3, 10);
  RndRect; PutAlien1(aDiese, pLife4, 15);
  RndRect; PutRAlien1(aKamikaze, 0, 3, 10);
  RndRect; PutAlien1(aCartoon, 0, 20);
  RndRect; PutCartoon(1, 2, 20);
  Rect(1, 1, 58, 58);
  PutHospital(6); PutBullet(6);
  PutIsolatedObjs(DEADOBJ, doMirror, 0, 1, 0, 5);
  PutIsolatedObjs(DEADOBJ, doFireWall, 0, 0, 0, 3);
  IF powerCountDown > 1 THEN
   PutExtraPower(1, 0, 69);
   PutBlockBonus(tbBullet, 0, 68);
   PutBlockBonus(tbBomb, 1, 69)
  END;
  Rect(0, 65, 59, 69); PutAlien1(aSmallDrawer, 60, 1);
  AddOptions(1, 1, 58, 58, 5, 10, 0, 0, 0, 4, 4);
  AddOptions(0, 69, 59, 69, 0, 0, 0, 20, 0, 0, 0)
 END Plain;

(* Level 18 *)

 PROCEDURE UnderWater;
  VAR
   x, y, a, sz: INT16;
   c: CARD16;
   val: CARD8;
 BEGIN
  Clear(100, 70);
  water:= (stages <> 0) OR (RND() MOD 4 <> 0);
  Fill(0, 0, 99, 69, SimpleBlock);
  IF dualpf THEN val:= BackNone ELSE val:= Tar END;
  FillEllipse(1, 1, 98, 68, val);
  FOR c:= 1 TO 16 + RND() MOD 16 DO
   sz:= 2 + Rnd(5);
   IF FindIsolatedRect(1, 1, 98, 68, sz, 0, a, x, y, FALSE) THEN
    FillEllipse(x - sz, y - sz, sz * 2 + 1, sz * 2 + 1, SimpleBlock)
   END
  END;
  FOR c:= 1 TO 12 DO
   sz:= Rnd(2) + 1;
   IF FindIsolatedRect(1, 1, 98, 68, sz, 15, a, x, y, TRUE) THEN
    FillEllipse(x - sz, y - sz, sz * 2 + 1, sz * 2 + 1, Ground);
    PutBubbleMaker(0, x, y);
    PutBlockObj(ALIEN2, cNest, 1, x - 1, y);
    PutBlockObj(ALIEN1, aHospital, pLife3, x + 1, y);
    IF c >= 10 + RND() MOD 3 THEN val:= FalseBlock ELSE val:= Ground2 END;
    IF (c = 12) AND (powerCountDown > 2) THEN
     PutExtraPower(2, x, y - 1);
     PutBlockObj(BONUS, BonusLevel, tbBonusLevel, x, y + 1)
    END;
    MakeLink(x, y, sz, a, val)
   END
  END;
  PutRandom(0, 0, 99, 69, OnlyWall, Leaf1, RND() MOD 128);
  PutRandom(0, 0, 99, 69, OnlyWall, Leaf2, RND() MOD 128);
  PutRandom(0, 0, 99, 69, OnlyWall, Leaf3, RND() MOD 256);
  PutRandom(0, 0, 99, 69, OnlyWall, Leaf4, RND() MOD 256);
  PutPlayer(1, 34); PutExit(98, 34);
  PutBubbleMaker(1, 49, 1);
  PutBubbleMaker(1, 50, 68);
  Rect(50, 1, 99, 69);
  PutCreatorR(20); PutCreatorC(20);
  Rect(1, 1, 49, 69);
  PutFour(pLife4, 20); PutTri(pLife4, 7);
  Rect(1, 1, 20, 69); PutTBonus(tbDBSpeed, 2);
  Rect(1, 1, 99, 69);
  PutAlien1(aBubble, pLife3, 20);
  PutMagnet(4);
  IF nbDollar = 0 THEN
   PutMoney(MoneySet{m3}, 10 + difficulty)
  END;
  IF stages = 0 THEN
   PutAlien2(cCircle, 120, 5);
   PutTurret(5);
   Rect(1, 1, 49, 69); PutTBonus(tbNoMissile, 2)
  END;
  PutChaosObjs(DEADOBJ, doSand, 0, 2880, 1280, 3168, 1920, 15);
  AddOptions(1, 1, 98, 68, 1, 1, 0, 6, 20, 3, 3)
 END UnderWater;

(* Level 19 *)

 PROCEDURE Assembly;
  VAR
   x: INT16;
   val: CARD8;
 BEGIN
  Cadre(15, 70);
  IF dualpf THEN val:= BackNone ELSE val:= Round4 END;
  FillCond(0, 0, 14, 69, OnlyWall, SimpleBlock);
  GCastle(1, 7, 13, 25, Bricks, val);
  DrawPacman(6, 2, 2, 2, 27, 12, 39);
  Fill(1, 41, 12, 41, SimpleBlock);
  FillRandom(1, 27, 13, 40, Granit1, Granit2, OnlyWall, Rnd);
  FillCond(1, 1, 13, 69, OnlyBackground, val);
  x:= Rnd(10) + 3;
  Fill(0, 47, x - 1, 55, SimpleBlock);
  Fill(x + 1, 47, 14, 55, SimpleBlock);
  PutPlayer(8, 3);
  PutBlockObj(ALIEN1, aKamikaze, 0, 6, 1);
  PutBlockObj(ALIEN1, aKamikaze, 1, 10, 1);
  PutBlockObj(ALIEN1, aKamikaze, 2, 6, 5);
  PutBlockObj(ALIEN1, aKamikaze, 3, 10, 5);
  PutExit(13, 68);
  PutBlockBonus(tbDBSpeed, 13, 40);
  PutBlockBonus(tbSGSpeed, x, 56);
  Fill(1, 48, 13, 48, FalseBlock);
  IF (difficulty >= 3) AND (difficulty <= 7) THEN
   PutBlockBonus(tbDifficulty, x, 49)
  END;
  Put(x, 48, Round4);
  val:= RND() MOD 3 + tbMagnet;
  PutBlockBonus(val, 1, 48);
  PutBlockBonus(val, 13, 48);
  PutBlockBonus(tbHospital, x, 52);
  PutFineObj(ALIEN1, aKamikaze, 0, 6, 44, 0, 0);
  PutFineObj(ALIEN1, aKamikaze, 1, 6, 44, 1, 0);
  PutFineObj(ALIEN1, aKamikaze, 2, 6, 44, 0, 1);
  PutFineObj(ALIEN1, aKamikaze, 3, 6, 44, 1, 1);
  Rect(1, 41, 13, 46); PutBullet(17);
  PutChaosObjs(ALIEN2, cNest, 0, BW * 2, BH * 57, BW * 13, BH * 68, 15);
  PutChaosObjs(ALIEN2, cCreatorR, 80, BW * 2, BH * 57, BW * 13, BH * 68, 15);
  PutChaosObjs(ALIEN2, cCreatorC, 80, BW * 2, BH * 57, BW * 13, BH * 68, 15);
  PutChaosObjs(ALIEN2, cFour, pLife4, BW * 2, BH * 57, BW * 13, BH * 68, 12);
  PutChaosObjs(ALIEN2, cQuad, pLife4, BW * 2, BH * 57, BW * 13, BH * 68, 10);
  PutChaosObjs(MACHINE, mTurret, 0, BW * 2, BH * 57, BW * 13, BH * 68, 12);
  PutChaosObjs(MACHINE, mCannon3, 0, BW * 2, BH * 57, BW * 13, BH * 68, 8);
  AddOptions(1, 10, 13, 68, 4, 4, 4, 4, 4, 4, 4)
 END Assembly;

(* Level 20 *)

 PROCEDURE Jungle;
  CONST
   Alien1L = SET16{aDbOval, aHospital, aDiese, aStar, aBubble, aTri, aTrefle};
   Alien1S = SET16{aCartoon, aKamikaze, aPic};
   Alien2L = SET16{cAlienV, cAlienA, cCreatorR, cCreatorC, cFour, cQuad};
   Alien2B = SET16{cCreatorR, cCreatorC};
   Alien2S = SET16{cAlienBox, cNest};
   DObjs = SET16{doMagnetA, doMagnetR, doSand, doMirror, doWindMaker, doBubbleMaker, doFireMaker, doFireWall};
   Machines = SET16{mTraverse, mCannon1, mCannon2, mCannon3, mTurret};
   TBonus = SET16{tbDBSpeed, tbSGSpeed, tbMagnet, tbInvinsibility, tbSleeper,
                   tbFreeFire, tbMaxPower};
  VAR
   x, y, sz, angle: INT16;
   c, lf: CARD16;
  PROCEDURE RndRect;
  BEGIN
   x:= Rnd(40) + 10; y:= Rnd(40) + 10;
   Rect(x - 9, y - 9, x + 9, y + 9)
  END RndRect;
 BEGIN
  Clear(60, 60);
  PutPlayer(0, 0);
  PutBlockBonus(tbBomb, 59, 59);
  water:= (difficulty >= 7) AND (RND() MOD 4 = 0);
  Fill(0, 0, 59, 59, SimpleBlock);
  Join(1, 1, 58, 58, 1, 1, Ground2);
  PutExit(58, 58);
  FOR c:= 1 TO 30 DO
   sz:= RND() MOD 4 + 2;
   IF FindIsolatedRect(0, 0, 59, 59, sz, 15, angle, x, y, TRUE) THEN
    Fill(x - sz, y - sz, x + sz, y + sz, Ground);
    MakeLink(x, y, sz, angle, Ground2);
    DEC(sz);
    IF dualpf THEN
     Fill(x - sz, y - sz, x + sz, y + sz, BackNone)
    END
   END
  END;
  FillRandom(0, 0, 59, 59, Forest1, Forest7, OnlyWall, Rnd);
  FOR c:= 0 TO 15 DO
   IF c IN Alien1L THEN
    RndRect; PutAlien1(c, pLife4, 4);
    Rect(1, 1, 58, 58); PutAlien1(c, pLife4, 1)
   ELSIF c IN Alien1S THEN
    RndRect; PutRAlien1(c, 0, 3, 4);
    Rect(1, 1, 58, 58); PutRAlien1(c, 0, 3, 1)
   END;
   IF c IN Alien2L THEN
    IF c IN Alien2B THEN lf:= 70 + difficulty * 3 ELSE lf:= pLife4 END;
    RndRect; PutAlien2(c, lf, 5);
    Rect(1, 1, 58, 58); PutAlien2(c, lf, 1)
   ELSIF c IN Alien2S THEN
    RndRect; PutRAlien2(c, 0, 3, 4);
    Rect(1, 1, 58, 58); PutRAlien2(c, 0, 3, 1)
   END;
   IF c IN DObjs THEN
    Rect(1, 1, 58, 58); PutIsolatedObjs(DEADOBJ, c, 0, 3, 0, 4)
   END;
   IF c IN Machines THEN
    RndRect; PutMachine(c, 0, 1, 4)
   END;
   IF c IN TBonus THEN
    Rect(1, 1, 58, 58); PutTBonus(c, 1)
   END;
  END;
  Rect(1, 1, 58, 58); PutTBonus(tbHelp, 1);
  PutTBonus(tbBomb, 1);
  PutTBonus(tbBonusLevel, 1);
  PutHospital(8); PutBullet(8);
  PutAlien1(aBigDrawer, 0, 1);
  PutAlien1(aSmallDrawer, 0, 1);
  AddOptions(1, 1, 58, 58, 3, 3, 3, 3, 3, 3, 3)
 END Jungle;

END Chaos2Zone.
