IMPLEMENTATION MODULE ChaosDual;

 FROM Memory IMPORT SET16, CARD8, CARD16, INT16;
 FROM Trigo IMPORT RND, SQRT, SIN, COS;
 FROM ChaosBase IMPORT Zone, zone, level, difficulty;
 FROM ChaosGraphics IMPORT SetTrans, castle, NbWall, NbBackground, NbClear,
  CopyToDual, dualSpeed;
 FROM ChaosImages IMPORT InitDualPalette;
 FROM ChaosObjects IMPORT FilterProc, RandomProc, ChooseProc, Rnd, ExpRandom,
  Set, Reset, Get, Put, Mark, Marked, FlushMarks, Clear, Cadre, All,
  OnlyBackground, OnlyWall, Fill, FillCond, FillRandom, FillChoose, PutRandom;
 FROM ChaosGenerator IMPORT FillEllipse, FindIsolatedRect;


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


 PROCEDURE ResetTrans;
  VAR
   c: CARD8;
 BEGIN
  SetTrans(0, 0);
  FOR c:= 1 TO 15 DO
   SetTrans(c, 255)
  END
 END ResetTrans;

 PROCEDURE DrawStars;
 BEGIN
  FillRandom(0, 0, 63, 63, 0, 7, All, ExpRandom)
 END DrawStars;

 PROCEDURE DrawEntry;
  VAR
   z: INT16;

  PROCEDURE DrawLetter(b1, b2, b3, b4, b5: CARD16; add: INT16);

   PROCEDURE DrawPattern(pat: CARD16);
    VAR
     x: INT16;
   BEGIN
    FOR x:= 0 TO 8 DO
     IF ODD(pat) THEN
      Set(z, 9 - x); Set(x + 2, z)
     END;
     pat:= pat DIV 2
    END;
    INC(z)
   END DrawPattern;

  BEGIN
   DrawPattern(b1);
   DrawPattern(b2);
   DrawPattern(b3);
   DrawPattern(b4);
   DrawPattern(b5);
   z:= z - 5 + add
  END DrawLetter;

 BEGIN
  z:= 6;
  DrawLetter(124, 130, 257, 257, 257, 6); (* C *)
  DrawLetter(511, 16, 16, 15, 0, 5);      (* h *)
  DrawLetter(14, 17, 10, 31, 0, 5);       (* a *)
  DrawLetter(14, 17, 17, 17, 14, 6);      (* o *)
  DrawLetter(9, 21, 21, 21, 18, 6);       (* s *)
  DrawLetter(124, 130, 257, 257, 257, 6); (* C *)
  DrawLetter(14, 17, 10, 31, 0, 5);      (* a *)
  DrawLetter(9, 21, 21, 21, 18, 6);       (* s *)
  DrawLetter(32, 510, 33, 0, 0, 4);       (* t *)
  DrawLetter(511, 0, 0, 0, 0, 2);         (* l *)
  DrawLetter(14, 21, 21, 21, 8, 5);       (* e *)
  FillCond(0, 0, 63, 63, OnlyWall, SimpleBlock);
  PutRandom(0, 0, 63, 63, OnlyWall, Leaf2, RND() MOD 64 + 32);
  PutRandom(0, 0, 63, 63, OnlyWall, Leaf3, RND() MOD 16 + 4);
  PutRandom(0, 0, 63, 63, OnlyBackground, Sq1Block, RND() MOD 128 + 64);
  PutRandom(0, 0, 63, 63, OnlyBackground, Sq4Block, RND() MOD 64 + 16);
  FillCond(0, 0, 63, 63, OnlyBackground, EmptyBlock)
 END DrawEntry;

 PROCEDURE GrooveTrans;
  VAR
   c: CARD16;
 BEGIN
  FOR c:= 0 TO 5 DO
   SetTrans(c + 9, 255 - (5 - c) * (difficulty - 1) * 5)
  END
 END GrooveTrans;

 PROCEDURE IceTrans(deg: CARD16);
  VAR
   c: CARD16;
   t: CARD8;
 BEGIN
  t:= deg + RND() MOD (256 - deg) - difficulty * 6;
  FOR c:= 8 TO 15 DO
   SetTrans(c, t)
  END
 END IceTrans;

 PROCEDURE AnimTrans;
 BEGIN
  SetTrans(8, 128);
  SetTrans(12, 128);
  SetTrans(13, 192);
  SetTrans(14, 128)
 END AnimTrans;

 PROCEDURE DrawGround;
 BEGIN
  Fill(0, 0, 63, 63, Ground);
  PutRandom(0, 0, 63, 63, All, Ground2, RND() MOD 128 + 128)
 END DrawGround;

 PROCEDURE DrawFactory;
  VAR
   c, a, x, y, sz, d: INT16;
 BEGIN
  Fill(0, 0, 63, 63, SimpleBlock);
  Fill(1, 1, 62, 62, BackNone);
  FOR c:= 1 TO 20 DO
   sz:= RND() MOD 4 + 1; d:= sz * 2 + 1;
   IF FindIsolatedRect(0, 0, 63, 63, sz, 60, a, x, y, FALSE) THEN
    FillEllipse(x - sz, y - sz, d, d, SimpleBlock)
   END
  END;
  FillRandom(1, 1, 62, 62, Granit1, Granit2, OnlyWall, Rnd);
  PutRandom(1, 1, 62, 62, OnlyBackground, EmptyBlock, RND() MOD 128);
  PutRandom(1, 1, 62, 62, OnlyBackground, Sq1Block, RND() MOD 128);
  PutRandom(1, 1, 62, 62, OnlyBackground, Sq4Block, RND() MOD 128);
  PutRandom(1, 1, 62, 62, OnlyBackground, Sq4TravBlock, RND() MOD 128);
  PutRandom(1, 1, 62, 62, OnlyBackground, TravBlock, RND() MOD 128);
  PutRandom(15, 15, 44, 44, OnlyBackground, Fact1Block, RND() MOD 128);
  PutRandom(15, 15, 44, 44, OnlyBackground, Fact2Block, RND() MOD 128);
  PutRandom(15, 15, 44, 44, OnlyBackground, Fact3Block, RND() MOD 128);
  PutRandom(1, 1, 62, 62, OnlyBackground, BigBlock, RND() MOD 16);
  FillCond(1, 1, 62, 62, OnlyBackground, SimpleBlock)
 END DrawFactory;

 PROCEDURE DrawFactory2;
  VAR
   c, a, x, y, sz, d: INT16;
 BEGIN
  Fill(0, 0, 39, 39, SimpleBlock);
  Fill(1, 1, 38, 38, BackNone);
  FOR c:= 1 TO 10 DO
   sz:= RND() MOD 3 + 1; d:= sz * 2 + 1;
   IF FindIsolatedRect(0, 0, 39, 39, sz, 40, a, x, y, FALSE) THEN
    FillEllipse(x - sz, y - sz, d, d, SimpleBlock)
   END
  END;
  FillRandom(1, 1, 62, 62, Granit1, Granit2, OnlyWall, Rnd);
  FOR c:= 1 TO 10 DO
   sz:= RND() MOD 3 + 1; d:= sz * 2 + 1;
   IF FindIsolatedRect(0, 0, 39, 39, sz, 40, a, x, y, FALSE) THEN
    FillEllipse(x - sz, y - sz, d, d, Bricks)
   END
  END;
  PutRandom(1, 1, 38, 38, OnlyBackground, Ground, RND() MOD 128);
  PutRandom(1, 1, 38, 38, OnlyBackground, Ground2, RND() MOD 128);
  PutRandom(1, 1, 38, 38, OnlyBackground, EmptyBlock, RND() MOD 128);
  PutRandom(1, 1, 38, 38, OnlyBackground, Sq1Block, RND() MOD 128);
  PutRandom(1, 1, 38, 38, OnlyBackground, Sq4Block, RND() MOD 128);
  PutRandom(1, 1, 38, 38, OnlyBackground, Sq4TravBlock, RND() MOD 128);
  PutRandom(1, 1, 38, 38, OnlyBackground, TravBlock, RND() MOD 128);
  PutRandom(1, 1, 38, 38, OnlyBackground, BarDark, RND() MOD 128);
  PutRandom(15, 15, 44, 44, OnlyBackground, Fact2Block, RND() MOD 16);
  PutRandom(15, 15, 44, 44, OnlyBackground, Fact1Block, RND() MOD 8);
  PutRandom(15, 15, 44, 44, OnlyBackground, Fact3Block, RND() MOD 8);
  PutRandom(1, 1, 62, 62, OnlyBackground, BigBlock, RND() MOD 16);
  FillCond(1, 1, 62, 62, OnlyBackground, SimpleBlock)
 END DrawFactory2;

 PROCEDURE DrawGroundLeaves;
  VAR
   c: CARD8;
 BEGIN
  DrawGround;
  FOR c:= Leaf1 TO Leaf4 DO
   PutRandom(0, 0, 63, 63, All, c, RND() MOD 128 + 16)
  END
 END DrawGroundLeaves;

 PROCEDURE DrawSquares;
 BEGIN
  Fill(0, 0, 63, 63, Back8x8);
  PutRandom(0, 0, 63, 63, All, Back2x2, RND() MOD 256);
  PutRandom(0, 0, 63, 63, All, Back4x4, RND() MOD 256);
  PutRandom(0, 0, 63, 63, All, BackSmall, RND() MOD 256);
  PutRandom(0, 0, 63, 63, All, BackBig, RND() MOD 256)
 END DrawSquares;

 PROCEDURE DrawBalls;
 BEGIN
  Fill(0, 0, 63, 63, Balls);
  PutRandom(0, 0, 63, 63, All, IceBlock, RND() MOD 256)
 END DrawBalls;

 PROCEDURE DrawGranit;
 BEGIN
  FillRandom(0, 0, 63, 63, Granit1, Granit2, All, Rnd)
 END DrawGranit;

 PROCEDURE DrawCastle;
 BEGIN
  Fill(0, 0, 63, 63, Bricks)
 END DrawCastle;

 PROCEDURE DrawRound4(n, m: CARD16);
  VAR
   x, y: INT16;
   c, r: CARD16;
   val: CARD8;
 BEGIN
  Fill(0, 0, 63, 63, Round4);
  FOR c:= 0 TO RND() MOD n + m DO
   x:= RND() MOD 64;
   r:= RND() MOD 7;
   CASE r OF
     0, 1, 2: val:= BarDark
    |3, 4: val:= Sq4Block
    |5: val:= Sq1Block
    |6: val:= BigBlock
   END;
   Fill(x, 0, x, 63, val);
   y:= RND() MOD 64;
   r:= RND() MOD 7;
   CASE r OF
     0, 1, 2: val:= BarDark
    |3, 4: val:= Sq4Block
    |5: val:= Sq1Block
    |6: val:= BigBlock
   END;
   Fill(0, y, 63, y, val)
  END
 END DrawRound4;

 PROCEDURE DrawLights(n: CARD16);
  VAR
   x, y: INT16;
   c: CARD16;
 BEGIN
  Fill(0, 0, 63, 63, Light);
  FOR c:= 0 TO RND() MOD n DO
   x:= RND() MOD 64;
   Fill(x, 0, x, 63, BarLight)
  END;
  FOR c:= 0 TO RND() MOD n DO
   y:= RND() MOD 64;
   Fill(0, y, 63, y, BarLight)
  END
 END DrawLights;

 PROCEDURE DrawForest;
 BEGIN
  FillRandom(0, 0, 63, 63, Forest1, Forest7, All, Rnd);
  PutRandom(0, 0, 63, 63, All, Leaf2, RND() MOD 32);
  PutRandom(0, 0, 63, 63, All, Leaf3, RND() MOD 128);
  PutRandom(0, 0, 63, 63, All, Ground, RND() MOD 64);
  PutRandom(0, 0, 63, 63, All, Ground2, RND() MOD 16)
 END DrawForest;

 PROCEDURE DrawFade;
  VAR
   x, y: INT16;
 BEGIN
  FOR y:= 0 TO 63 DO
   FOR x:= 0 TO 63 DO
    Put(x, y, Fade1 + RND() MOD 3)
   END
  END
 END DrawFade;

 PROCEDURE InstallDual;
 BEGIN
  InitDualPalette;
  ResetTrans;
  IF zone = Castle THEN
   CASE level[Castle] OF
     1: DrawEntry; dualSpeed:= 2
    |2: DrawStars; dualSpeed:= 8; GrooveTrans
    |3, 5, 10, 19: DrawStars; dualSpeed:= 8
    |4: DrawGround; dualSpeed:= 3
    |6: DrawRound4(15, 10); IceTrans(160); dualSpeed:= 5
    |7, 16: DrawStars; AnimTrans; dualSpeed:= 8
    |8: IF difficulty >= 7 THEN DrawLights(1) ELSE DrawSquares END;
        IceTrans(128); dualSpeed:= 4
    |9: DrawFactory; SetTrans(8, 128); dualSpeed:= 2
    |11: DrawSquares; dualSpeed:= 3
    |12: DrawLights(1); AnimTrans; dualSpeed:= 6
    |13: IF difficulty = 7 THEN DrawLights(1) ELSE DrawRound4(2, 2) END;
         IceTrans(192); dualSpeed:= 3
    |14: DrawGranit; dualSpeed:= 3
    |15: DrawGroundLeaves; dualSpeed:= 3
    |17: DrawCastle; dualSpeed:= 4
    |18: DrawBalls; dualSpeed:= 2
    |20: DrawFactory2; dualSpeed:= 2
   END
  ELSIF zone = Family THEN
   CASE level[Family] OF
     1: DrawForest; dualSpeed:= 4
    |2: DrawGround; dualSpeed:= 4
    |3, 7, 8, 10: DrawStars; dualSpeed:= 7
    |4: DrawGround; dualSpeed:= 3
    |5: DrawForest; IceTrans(128); dualSpeed:= 3
    |6: DrawFactory2; IceTrans(160); dualSpeed:= 3
    |9: DrawForest; dualSpeed:= 2
   END
  ELSIF zone = Special THEN
   IF level[Special] = 24 THEN
    DrawStars; dualSpeed:= 10
   ELSIF level[Special] MOD 8 = 0 THEN
    DrawFade; dualSpeed:= 2
   ELSIF level[Special] MOD 4 = 0 THEN
    DrawStars; dualSpeed:= 8
   ELSIF level[Special] MOD 2 = 0 THEN
    DrawGroundLeaves; dualSpeed:= 3
   ELSE
    DrawForest; dualSpeed:= 4
   END
  END;
  CopyToDual
 END InstallDual;

END ChaosDual.
