IMPLEMENTATION MODULE ChaosMissile;

 FROM Memory IMPORT CARD16, INT16, CARD32, INT32, AllocMem, AddTail;
 FROM Checks IMPORT CheckMem;
 FROM ChaosBase IMPORT Anims, AnimSet, BasicTypes, Obj, ObjPtr, ObjAttr,
  ObjAttrPtr, attrList, Stones, StoneSet, slowStyle, Frac, Period, AieProc,
  step, noMissile;
 FROM ChaosActions IMPORT SetObjLoc, SetObjRect, UpdateXY, Die, Aie,
  OutOfScreen, OutOfBounds, InBackground, Boum, DoCollision;
 FROM ChaosSounds IMPORT SoundList, soundList, Effect, SetEffect,
  SoundEffect;

 VAR
  missileDieEffect: ARRAY[0..0] OF Effect;

 PROCEDURE MakeMissile1(missile: ObjPtr);
 BEGIN
  WITH missile^ DO
   IF stat = 3 THEN
    SetObjLoc(missile, 78, 67, 7, 7)
   ELSIF stat = 4 THEN
    SetObjLoc(missile, 92, 180, 7, 7)
   ELSE
    SetObjLoc(missile, stat * 7 + 131, 65, 7, 7)
   END;
   SetObjRect(missile, 0, 0, 7, 7)
  END
 END MakeMissile1;

 PROCEDURE MakeMissile2(missile: ObjPtr);
 BEGIN
  WITH missile^ DO
   IF stat >= 2 THEN stat:= 0 ELSE INC(stat) END;
   SetObjLoc(missile, stat * 11 + 78, 67, 7, 7);
   SetObjRect(missile, 0, 0, 7, 7)
  END
 END MakeMissile2;

 PROCEDURE MakeMissile3(missile: ObjPtr);
 BEGIN
  WITH missile^ DO
   IF stat >= 2 THEN stat:= 0 ELSE INC(stat) END;
   SetObjLoc(missile, stat * 11 + 76, 65, 11, 11);
   SetObjRect(missile, 0, 0, 11, 11)
  END
 END MakeMissile3;

 PROCEDURE ResetMissile(missile: ObjPtr);
 BEGIN
  WITH missile^ DO
   shapeSeq:= Period DIV 10;
   moveSeq:= Period * 20;
   IF subKind = mYellow THEN
    fireSubLife:= life * 2 DIV 3
   ELSIF subKind = mRed THEN
    fireSubLife:= life DIV 2
   ELSE
    fireSubLife:= life DIV 3
   END;
   hitSubLife:= life - fireSubLife;
   attr^.Make(missile)
  END
 END ResetMissile;

 PROCEDURE MoveMissile(missile: ObjPtr; victims: AnimSet; proc: AieProc);
  VAR
   ss: StoneSet;
   cnt: CARD16;
 BEGIN
  UpdateXY(missile);
  WITH missile^ DO
   IF (subKind <> mAlien1) THEN
    IF (step >= shapeSeq) THEN
     attr^.Make(missile);
     INC(shapeSeq, Period DIV 10)
    END;
    IF step >= shapeSeq THEN shapeSeq:= 0 ELSE DEC(shapeSeq, step) END
   END;
   IF InBackground(missile) THEN
    IF subKind = mAlien3 THEN
     ss:= StoneSet{stFOG3}; cnt:= 20
    ELSE
     ss:= StoneSet{stFLAME2}; cnt:= subKind + 1
    END;
    Boum(missile, ss, slowStyle, cnt, 1);
    SoundEffect(missile, missileDieEffect);
    Die(missile); RETURN
   ELSIF OutOfScreen(missile) OR (OutOfBounds(missile) AND (subKind <> mAlien2))
      OR (step > moveSeq) OR ((subKind = mAlien1) AND (noMissile > 0)) THEN
    Die(missile); RETURN
   ELSE
    DEC(moveSeq, step)
   END;
   DoCollision(missile, victims, proc, hitSubLife, fireSubLife);
   IF life <> hitSubLife + fireSubLife THEN Die(missile) END
  END
 END MoveMissile;

 PROCEDURE KillIt(victim, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  Die(victim)
 END KillIt;

 PROCEDURE MoveMissile1(missile: ObjPtr);
 BEGIN
  MoveMissile(missile, AnimSet{PLAYER, ALIEN1}, Aie)
 END MoveMissile1;

 PROCEDURE MoveMissile2(missile: ObjPtr);
 BEGIN
  MoveMissile(missile, AnimSet{PLAYER, ALIEN1, ALIEN2}, Aie)
 END MoveMissile2;

 PROCEDURE MoveMissile3(missile: ObjPtr);
 BEGIN
  MoveMissile(missile, AnimSet{ALIEN1, ALIEN2, ALIEN3, MACHINE}, KillIt)
 END MoveMissile3;

 PROCEDURE InitParams;
  VAR
   attr: ObjAttrPtr;
 BEGIN
  SetEffect(missileDieEffect[0], soundList[wCrash], 1673, 16726, 30, 0);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetMissile;
   Make:= MakeMissile1;
   Move:= MoveMissile1;
   weight:= 18; charge:= 120;
   basicType:= NotBase;
   priority:= 20;
   toKill:= TRUE
  END;
  AddTail(attrList[MISSILE], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetMissile;
   Make:= MakeMissile2;
   Move:= MoveMissile2;
   weight:= 30; charge:= 90;
   basicType:= NotBase;
   priority:= 22;
   toKill:= TRUE
  END;
  AddTail(attrList[MISSILE], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetMissile;
   Make:= MakeMissile3;
   Move:= MoveMissile3;
   weight:= 100; charge:= 80;
   basicType:= NotBase;
   priority:= 24;
   toKill:= TRUE
  END;
  AddTail(attrList[MISSILE], attr^.node)
 END InitParams;

BEGIN

 InitParams;

END ChaosMissile.
