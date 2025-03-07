IMPLEMENTATION MODULE ChaosStone;

 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32,
  AllocMem, AddTail;
 FROM Checks IMPORT CheckMem;
 FROM Trigo IMPORT RND;
 FROM ChaosBase IMPORT Stones, StoneSet, Anims, AnimSet, BasicTypes, Obj,
  ObjPtr, ObjAttr, ObjAttrPtr, attrList, Period, slowStyle, step;
 FROM ChaosGraphics IMPORT shapeArea, maskArea, SetOrigin, X, Y;
 FROM ChaosSounds IMPORT SoundList, soundList, Sound, Effect, SetEffect,
  SoundEffect;
 FROM ChaosActions IMPORT SetObjLoc, SetObjPos, SetObjRect, UpdateXY,
  Die, OutOfScreen, OutOfBounds, InBackground;


 PROCEDURE MakeStone0(stone: ObjPtr);
  VAR
   l, t, r, b: INT16;
  PROCEDURE Set(nl, nt, nr, nb: INT16);
  BEGIN
   l:= nl + 128; r:= nr + 128;
   t:= nt; b:= nb
  END Set;
 BEGIN
  CASE stone^.stat OF
    0: Set(0, 0, 5, 5)
   |1: Set(0, 20, 6, 26)
   |2: Set(10, 16, 12, 18)
   |3: Set(6, 20, 12, 26)
   |4: Set(12, 20, 18, 26)
   |5: Set(10, 0, 17, 7)
   |6: Set(10, 8, 18, 16)
   |7: Set(6, 26, 9, 29)
   |8: Set(-88, 56, -76, 68)
   |9: Set(-76, 56, -64, 68)
  |10: Set(58, 26, 60, 28)
  |11: Set(58, 24, 59, 25)
  |12: Set(18, 24, 26, 32)
  |13: Set(42, 24, 50, 32)
  |14: Set(60, 0, 72, 12)
  |15: Set(30, 0, 38, 8)
  END;
  DEC(r, l); DEC(b, t);
  SetObjLoc(stone, l, t, r, b);
  SetObjRect(stone, 1, 1, r - 1, b - 1)
 END MakeStone0;

 PROCEDURE MakeStone1(stone: ObjPtr);
  VAR
   py: INT16;
   cnt: CARD16;
 BEGIN
  WITH stone^ DO
   cnt:= 18; py:= 0;
   WHILE cnt > shapeSeq DO INC(py, cnt); DEC(cnt, 3) END;
   SetObjLoc(stone, 224, py, cnt, cnt)
  END
 END MakeStone1;

 PROCEDURE ResetStone0(stone: ObjPtr);
 BEGIN
  WITH stone^ DO
   hitSubLife:= 1;
   shapeSeq:= 0;
   moveSeq:= Period DIV 20;
   life:= Period + RND() MOD (Period * 3);
   IF stat = 15 THEN
    moveSeq:= (Period DIV 4) + RND() MOD (Period DIV 4);
    life:= moveSeq * 8 + RND() MOD (Period DIV 2)
   ELSIF stat = 14 THEN
    moveSeq:= (Period DIV 4) + RND() MOD (Period DIV 4);
    life:= moveSeq * 5 + RND() MOD (Period DIV 2)
   ELSIF stat >= 12 THEN
    moveSeq:= (Period DIV 4) + RND() MOD (Period DIV 4);
    life:= moveSeq * 3 + RND() MOD (Period DIV 3)
   ELSIF stat >= 10 THEN
    life:= RND() MOD (Period * 3);
    shapeSeq:= RND() MOD 3
   ELSE
    shapeSeq:= RND() MOD 2
   END
  END;
  MakeStone0(stone)
 END ResetStone0;

 PROCEDURE ResetStone1(stone: ObjPtr);
 BEGIN
  WITH stone^ DO
   hitSubLife:= 1;
   life:= Period DIV 5
  END;
  MakeStone1(stone)
 END ResetStone1;

 VAR
  stoneEffect: ARRAY[0..0] OF Effect;

 PROCEDURE MoveStone0(stone: ObjPtr);
  VAR
   switched, inbg: BOOLEAN;

  PROCEDURE Switch(os, ns: CARD16; nx, ny: INT16);
  BEGIN
   WITH stone^ DO
    IF NOT(switched) AND (os = shapeSeq) THEN
     switched:= TRUE; shapeSeq:= ns;
     SetObjPos(stone, nx + 128, ny)
    END
   END;
  END Switch;

 BEGIN
  UpdateXY(stone);
  WITH stone^ DO
   inbg:= InBackground(stone);
   IF (stat < 10) AND inbg THEN
    SoundEffect(stone, stoneEffect);
    Die(stone); RETURN
   ELSIF OutOfScreen(stone) OR (life < step) OR OutOfBounds(stone) THEN
    Die(stone); RETURN
   END;
   DEC(life, step);
   IF step > moveSeq THEN
    switched:= FALSE;
    CASE stat OF
      0: Switch(0, 1, 0, 5); Switch(1, 0, 0, 0)
     |1: Switch(0, 1, 0, 26); Switch(1, 0, 0, 20)
     |2: Switch(0, 1, 14, 16); Switch(1, 0, 10, 16)
     |7: Switch(0, 1, 12, 26); Switch(1, 0, 6, 26)
     |8: Switch(0, 1, -88, 68); Switch(1, 0, -88, 56)
     |9: Switch(0, 1, -76, 68); Switch(1, 0, -76, 56)
    |10: Switch(0, 1, 58, 28); Switch(1, 2, 58, 30); Switch(2, 0, 58, 26)
    |11:
     Switch(0, 1, 58, 25); Switch(1, 2, 59, 24);
     Switch(2, 3, 59, 25); Switch(3, 0, 58, 24)
    |12: Switch(0, 1, 26, 24); Switch(1, 2, 34, 24)
    |13: Switch(0, 1, 50, 24); Switch(1, 2, 52, 16)
    |14:
     Switch(0, 1, 72, 0);
     Switch(1, 2, 60, 12);
     Switch(2, 3, 72, 12);
     Switch(3, 4, 84, 0)
    |15:
     Switch(0, 1, 38, 0);
     Switch(1, 2, 46, 0);
     Switch(2, 3, 30, 8);
     Switch(3, 4, 38, 8);
     Switch(4, 5, 46, 8);
     Switch(5, 6, 30, 16);
     Switch(6, 7, 38, 16)
    ELSE
    END;
    IF stat >= 12 THEN
     moveSeq:= (Period DIV 4)
    ELSE
     moveSeq:= (Period DIV 20)
    END
   ELSE
    DEC(moveSeq, step)
   END
  END
 END MoveStone0;

 PROCEDURE MoveStone1(stone: ObjPtr);
 BEGIN
  UpdateXY(stone);
  WITH stone^ DO
   IF step > life THEN
    IF shapeSeq = moveSeq THEN moveSeq:= 0 END;
    IF shapeSeq < moveSeq THEN INC(shapeSeq, 3) ELSE DEC(shapeSeq, 3) END;
    IF shapeSeq = 0 THEN Die(stone); RETURN END;
    MakeStone1(stone);
    IF step <= Period DIV 5 THEN
     INC(life, Period DIV 5 - step)
    END
   ELSE
    DEC(life, step)
   END
  END
 END MoveStone1;

 PROCEDURE InitParams;
  VAR
   attr: ObjAttrPtr;
 BEGIN
  SetEffect(stoneEffect[0], soundList[sGun], 0, 0, 12, 1);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetStone0;
   Make:= MakeStone0;
   Move:= MoveStone0;
   charge:= 96;
   basicType:= NotBase;
   priority:= 80;
   toKill:= TRUE
  END;
  AddTail(attrList[STONE], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetStone1;
   Make:= MakeStone1;
   Move:= MoveStone1;
   charge:= 50;
   inerty:= 12;
   dieStKinds:= StoneSet{stFOG1, stFLAME2};
   dieSKCount:= 2; dieStone:= 5;
   dieStStyle:= slowStyle;
   basicType:= NotBase;
   priority:= 70;
   toKill:= TRUE
  END;
  AddTail(attrList[STONE], attr^.node)
 END InitParams;

BEGIN

 InitParams;

END ChaosStone.
