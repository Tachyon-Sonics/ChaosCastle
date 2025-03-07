IMPLEMENTATION MODULE ChaosMachine;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32, AllocMem, AddTail;
 FROM Checks IMPORT CheckMem;
 FROM Trigo IMPORT RND, SGN, SQRT, SIN, COS;
 FROM ChaosGraphics IMPORT SetTrans, SetRGB, color;
 FROM ChaosBase IMPORT Anims, AnimSet, BasicTypes, Obj, ObjPtr, ObjAttr,
  ObjAttrPtr, attrList, Stones, StoneSet, gravityStyle, slowStyle, fastStyle,
  Frac, Period, lasttime, step, pLife, difficulty, mainPlayer, addpt, FlameMult,
  powerCountDown, sleeper, screenInverted;
 FROM ChaosSounds IMPORT SoundList, soundList, Sound, Effect, SetEffect,
  SoundEffect, nulSound;
 FROM ChaosActions IMPORT SetObjLoc, SetObjRect, SetObjVXY, SetObjAXY,
  LimitSpeed, UpdateXY, Aie, Die, DecLife, DoCollision, OutOfScreen, Leave,
  GetCenter, Burn, CreateObj, AvoidBackground, SetObjXY, PlayerCollision, nlObj;
 FROM ChaosMissile IMPORT mAlien1, mAlien2, mAlien3, mYellow, mAcc2, mBig;


 VAR
  smallFireEffect: ARRAY[0..0] OF Effect;
  mediumFireEffect: ARRAY[0..0] OF Effect;
  bigFireEffect: ARRAY[0..0] OF Effect;
  aieCannonEffect, aieCannon3Effect, dieDoorEffect: ARRAY[0..0] OF Effect;
  dieCannonEffect: ARRAY[0..15] OF Effect;
  dieCannon3Effect: ARRAY[0..23] OF Effect;
  dieTurretEffect: ARRAY[0..15] OF Effect;


 PROCEDURE MakeTraverse(traverse: ObjPtr);
  VAR
   px, py, sx, sy: INT16;
 BEGIN
  IF traverse^.stat = 0 THEN
   px:= 109; py:= 68; sx:= 19; sy:= 8
  ELSE
   px:= 184; py:= 53; sx:= 8; sy:= 19
  END;
  SetObjLoc(traverse, px, py, sx, sy);
  SetObjRect(traverse, 0, 0, sx, sy)
 END MakeTraverse;

 PROCEDURE MakeCannon1(cannon: ObjPtr);
  VAR
   px, ex, sx: INT16;
 BEGIN
  IF cannon^.stat = 0 THEN
   px:= 0; sx:= 0; ex:= 14
  ELSE
   px:= 16; sx:= 2; ex:= 16
  END;
  SetObjLoc(cannon, px, 212, 16, 28);
  SetObjRect(cannon, sx, 0, ex, 28)
 END MakeCannon1;

 PROCEDURE MakeCannon2(cannon: ObjPtr);
  VAR
   px, sy, ey: INT16;
 BEGIN
  IF cannon^.stat = 0 THEN
   px:= 28; sy:= 0; ey:= 14
  ELSE
   px:= 0; sy:= 2; ey:= 16
  END;
  SetObjLoc(cannon, px, 240, 28, 16);
  SetObjRect(cannon, 0, sy, 28, ey)
 END MakeCannon2;

 PROCEDURE MakeCannon3(cannon: ObjPtr);
 BEGIN
  SetObjLoc(cannon, 152, 180, 20, 20);
  SetObjRect(cannon, 0, 0, 20, 20)
 END MakeCannon3;

 PROCEDURE MakeTurret(turret: ObjPtr);
 BEGIN
  SetObjLoc(turret, 32, 212, 28, 28);
  SetObjRect(turret, 2, 2, 26, 26)
 END MakeTurret;

 PROCEDURE MakeReactor(reactor: ObjPtr);
 BEGIN
  SetObjLoc(reactor, 224, 184, 16, 20);
  SetObjRect(reactor, 0, 0, 16, 20)
 END MakeReactor;

 PROCEDURE MakeDoor(door: ObjPtr);
 BEGIN
  WITH door^ DO
   hitSubLife:= 0; fireSubLife:= 0;
   life:= 100
  END;
  SetObjLoc(door, 224, 84, 32, 6);
  SetObjRect(door, 0, -2, 32, 8)
 END MakeDoor;

 PROCEDURE ResetTraverse(traverse: ObjPtr);
 BEGIN
  WITH traverse^ DO
   dvx:= 0; dvy:= 0;
   stat:= life; life:= 1;
   hitSubLife:= 0; fireSubLife:= 0;
   IF RND() MOD 2 = 0 THEN
    IF stat = 1 THEN dvy:= 1200 ELSE dvx:= 1200 END
   ELSE
    IF stat = 1 THEN dvy:= -1200 ELSE dvx:= -1200 END
   END
  END;
  MakeTraverse(traverse)
 END ResetTraverse;

 PROCEDURE ResetCannon(cannon: ObjPtr);
 BEGIN
  WITH cannon^ DO
   stat:= life;
   life:= 40 + difficulty * 10;
   IF subKind = mCannon3 THEN life:= life * 2 END;
   fireSubLife:= life; hitSubLife:= life;
   moveSeq:= RND() MOD Period;
   attr^.Make(cannon)
  END
 END ResetCannon;

 PROCEDURE ResetTurret(turret: ObjPtr);
 BEGIN
  WITH turret^ DO
   life:= 40 + difficulty * 10;
   fireSubLife:= life; hitSubLife:= life;
   moveSeq:= RND() MOD (Period DIV 4); shapeSeq:= RND() MOD 8
  END;
  MakeTurret(turret)
 END ResetTurret;

 PROCEDURE ResetReactor(reactor: ObjPtr);
 BEGIN
  WITH reactor^ DO
   life:= 100; hitSubLife:= 1; fireSubLife:= 1;
   dvx:= 0; dvy:= 0;
   moveSeq:= 0
  END;
  MakeReactor(reactor)
 END ResetReactor;

 PROCEDURE MoveTraverse(traverse: ObjPtr);
 BEGIN
  IF OutOfScreen(traverse) THEN Leave(traverse); RETURN END;
  UpdateXY(traverse);
  AvoidBackground(traverse, 4);
  traverse^.life:= 18;
  PlayerCollision(traverse, traverse^.life)
 END MoveTraverse;

 PROCEDURE MoveCannon(cannon: ObjPtr);
  VAR
   missile: ObjPtr;
   px, py, dx, dy, nvx, nvy, swp: INT16;
   time: CARD16;
 BEGIN
  IF OutOfScreen(cannon) THEN Leave(cannon); RETURN END;
  Burn(cannon);
  WITH cannon^ DO
   IF step > moveSeq THEN
    GetCenter(cannon, px, py); nvy:= 0; dy:= 0;
    IF stat = 0 THEN dx:= 6; nvx:= 2048 ELSE dx:= -6; nvx:= -2048 END;
    IF subKind = mCannon2 THEN
     swp:= dx; dx:= dy; dy:= swp;
     nvy:= nvx; nvx:= 0
    END;
    IF sleeper = 0 THEN
     missile:= CreateObj(MISSILE, mAlien1, px + dx, py + dy, mYellow, 12);
     SetObjVXY(missile, nvx, nvy);
     SoundEffect(cannon, smallFireEffect)
    END;
    time:= pLife + 16 - powerCountDown;
    IF time >= 20 THEN time:= 1 ELSE time:= (21 - time) END;
    INC(moveSeq, Period * time - Period DIV 4 - RND() MOD (Period DIV 8))
   END;
   DEC(moveSeq, step);
   PlayerCollision(cannon, life);
   IF life = 0 THEN Die(cannon) END
  END
 END MoveCannon;

 PROCEDURE MoveCannon3(cannon: ObjPtr);
  VAR
   missile: ObjPtr;
   nvx, nvy, speed: INT32;
   px, py: INT16;
   angle, dv1, dv2, m2, cnt, st, bs: INT16;
   time: CARD16;
 BEGIN
  IF OutOfScreen(cannon) THEN Leave(cannon); RETURN END;
  Burn(cannon);
  WITH cannon^ DO
   IF step > moveSeq THEN
    GetCenter(cannon, px, py);
    IF difficulty >= 2 THEN
     IF difficulty >= 8 THEN
      cnt:= 36; m2:= 1
     ELSIF difficulty >= 5 THEN
      cnt:= 24; m2:= 0
     ELSE
      cnt:= 15; m2:= 0
     END;
     dv1:= RND() MOD 5 + 1; IF dv1 = 5 THEN dv1:= 6 END;
     dv2:= RND() MOD 5 + 1; IF dv2 = 5 THEN dv2:= 6 END;
     bs:= RND() MOD 360; st:= 360 DIV cnt; angle:= 0;
     SoundEffect(cannon, mediumFireEffect);
     WHILE cnt > 0 DO
      nvx:= COS(angle); nvy:= SIN(angle);
      speed:= SIN(angle * dv1 + bs) + SIN(angle * dv2 + bs) * m2 DIV 2 + 2048;
      nvx:= nvx * speed DIV 1024;
      nvy:= nvy * speed DIV 1024;
      missile:= CreateObj(MISSILE, mAlien2, px, py, mAcc2, 12);
      SetObjVXY(missile, nvx, nvy);
      DEC(cnt); INC(angle, st)
     END
    END;
    IF pLife >= 20 THEN time:= 4 ELSE time:= 24 - pLife END;
    INC(moveSeq, Period * time)
   END;
   DEC(moveSeq, step);
   PlayerCollision(cannon, life);
   IF life = 0 THEN Die(cannon) END
  END
 END MoveCannon3;

 PROCEDURE MoveTurret(turret: ObjPtr);
  VAR
   missile: ObjPtr;
   px, py, nvx, nvy, angle, dx, dy: INT16;
   cnt, time: CARD16;
   sk: CARD8;
 BEGIN
  IF OutOfScreen(turret) THEN Leave(turret); RETURN END;
  Burn(turret);
  WITH turret^ DO
   IF step > moveSeq THEN
    IF difficulty <= 2 THEN cnt:= 1 ELSE cnt:= difficulty - 2 END;
    IF sleeper <> 0 THEN cnt:= (cnt - 1) DIV 2 END;
    GetCenter(turret, px, py);
    missile:= ADR(nlObj);
    angle:= shapeSeq * 45;
    WHILE cnt > 0 DO
     nvx:= COS(angle) * 2;
     nvy:= SIN(angle) * 2;
     dx:= COS(angle) DIV 102;
     dy:= SIN(angle) DIV 102;
     DEC(cnt); sk:= (cnt + 2) MOD 4;
     missile:= CreateObj(MISSILE, mAlien1, px + dx, py + dy, sk, 8 + sk * 2);
     SetObjVXY(missile, nvx, nvy);
     INC(angle, 45)
    END;
    IF (missile <> ADR(nlObj)) THEN SoundEffect(missile, smallFireEffect) END;
    IF pLife > 20 THEN time:= 8 ELSE time:= 28 - pLife END;
    INC(moveSeq, Period * time DIV 28 + RND() MOD 16);
    shapeSeq:= (shapeSeq + 1) MOD 8
   END;
   DEC(moveSeq, step);
   IF powerCountDown < 5 THEN
    PlayerCollision(turret, life)
   END;
   IF life = 0 THEN Die(turret) END
  END
 END MoveTurret;

 PROCEDURE MoveReactor(reactor: ObjPtr);
 BEGIN
  IF OutOfScreen(reactor) THEN Leave(reactor); RETURN END;
  WITH reactor^ DO
   IF step >= moveSeq THEN moveSeq:= 0 ELSE DEC(moveSeq, step) END
  END
 END MoveReactor;

 PROCEDURE AieCannon(cannon, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  DecLife(cannon, hit, fire);
  SoundEffect(cannon, aieCannonEffect)
 END AieCannon;

 PROCEDURE AieCannon3(cannon, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  DecLife(cannon, hit, fire);
  SoundEffect(cannon, aieCannon3Effect)
 END AieCannon3;

 PROCEDURE AieReactor(reactor, src: ObjPtr; VAR hit, fire: CARD16);
  VAR
   missile: ObjPtr;
   px, py: INT16;
 BEGIN
  hit:= 0; fire:= 0;
  IF reactor^.moveSeq = 0 THEN
   GetCenter(reactor, px, py);
   missile:= CreateObj(MISSILE, mAlien3, px, py, mBig, 20);
   SetObjVXY(missile, 0, -2400);
   SoundEffect(reactor, bigFireEffect);
   reactor^.moveSeq:= Period * 3
  END
 END AieReactor;

 PROCEDURE DieCannon(cannon: ObjPtr);
 BEGIN
  SoundEffect(cannon, dieCannonEffect)
 END DieCannon;

 PROCEDURE DieCannon3(cannon: ObjPtr);
 BEGIN
  INC(addpt, difficulty DIV 2);
  SoundEffect(cannon, dieCannon3Effect)
 END DieCannon3;

 PROCEDURE DieTurret(turret: ObjPtr);
 BEGIN
  INC(addpt);
  SoundEffect(turret, dieTurretEffect)
 END DieTurret;

 PROCEDURE DieReactor(reactor: ObjPtr);
 BEGIN
  reactor^.life:= 100
 END DieReactor;

 PROCEDURE Push(victim, door: ObjPtr; VAR hit, fire: CARD16);
  VAR
   px, py, ox, oy: INT16;
 BEGIN
  IF victim^.kind = MISSILE THEN Die(victim); RETURN END;
  GetCenter(door, px, py);
  GetCenter(victim, ox, oy);
  INC(py, door^.cy + 1); DEC(ox, victim^.cx);
  SetObjXY(victim, ox, py);
  WITH victim^ DO
   vy:= ABS(vy); dvy:= ABS(dvy); ay:= ABS(ay)
  END
 END Push;

 PROCEDURE MoveDoor(door: ObjPtr);
 BEGIN
  IF OutOfScreen(door) THEN Leave(door); RETURN END;
  WITH door^ DO
   DoCollision(door, AnimSet{PLAYER..ALIEN1, MISSILE, SMARTBONUS, BONUS, MACHINE},
               Push, hitSubLife, fireSubLife)
  END
 END MoveDoor;

 PROCEDURE DieDoor(door: ObjPtr);
 BEGIN
  SoundEffect(door, dieDoorEffect);
  IF color THEN
   SetTrans(0, 192);
   SetRGB(0, 255, 127, 0);
   SetRGB(4, 0, 255, 255);
  END;
  screenInverted:= Period DIV 4
 END DieDoor;

 PROCEDURE InitParams;
  VAR
   attr: ObjAttrPtr;
   c, v, f, r1, r2: INT16;
 BEGIN
  SetEffect(smallFireEffect[0], soundList[sMissile], 0, 0, 110, 3);
  SetEffect(mediumFireEffect[0], soundList[sPouf], 5000, 0, 190, 8);
  SetEffect(bigFireEffect[0], soundList[sCasserole], 0, 16726, 240, 9);
  SetEffect(aieCannonEffect[0], soundList[sLaser], 0, 16726, 80, 1);
  SetEffect(dieDoorEffect[0], soundList[sPouf], 0, 4181, 255, 12);
  FOR c:= 0 TO 15 DO
   IF (c DIV 4) MOD 2 = 0 THEN v:= 9377 ELSE v:= 7442 END;
   SetEffect(dieCannonEffect[c], soundList[sHHat], v DIV 24, v, (16 - c) * 9, 5)
  END;
  SetEffect(aieCannon3Effect[0], soundList[sHHat], 0, 0, 140, 1);
  r1:= 0;
  FOR c:= 0 TO 15 DO
   r1:= (r1 * 17 + 5) MOD 8;
   v:= (16 - c) * (r1 + 4);
   SetEffect(dieTurretEffect[c], soundList[sHHat], 658, 10525, v, 5)
  END;
  r1:= 0; r2:= 0;
  FOR c:= 0 TO 23 DO
   r1:= (r1 * 9 + 5) MOD 16;
   r2:= (r2 * 13 + 9) MOD 16;
   f:= 4181 + r1 * 836;
   v:= f DIV (r2 + 8);
   SetEffect(dieCannon3Effect[c], soundList[sHHat], v, f, (24 - c) * 6, 6)
  END;
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetTraverse;
   Make:= MakeTraverse;
   Move:= MoveTraverse;
   weight:= 100; inerty:= 30; priority:= -75;
   dieStKinds:= StoneSet{stSTAR2}; dieSKCount:= 1;
   dieStone:= 9; dieStStyle:= slowStyle;
   basicType:= NotBase
  END;
  AddTail(attrList[MACHINE], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetCannon;
   Make:= MakeCannon1;
   Move:= MoveCannon;
   Aie:= AieCannon;
   Die:= DieCannon;
   weight:= 120;
   heatSpeed:= 10; coolSpeed:= 10;
   refreshSpeed:= 30;
   aieStKinds:= StoneSet{stFOG2}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stC26}; dieSKCount:= 1;
   dieStone:= 9; dieStStyle:= slowStyle;
   basicType:= Mineral;
   priority:= -20;
   toKill:= TRUE
  END;
  AddTail(attrList[MACHINE], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetCannon;
   Make:= MakeCannon2;
   Move:= MoveCannon;
   Aie:= AieCannon;
   Die:= DieCannon;
   weight:= 120;
   heatSpeed:= 10; coolSpeed:= 10;
   refreshSpeed:= 30;
   aieStKinds:= StoneSet{stFOG2}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stC26}; dieSKCount:= 1;
   dieStone:= 9; dieStStyle:= slowStyle;
   basicType:= Mineral;
   priority:= -20;
   toKill:= TRUE
  END;
  AddTail(attrList[MACHINE], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetCannon;
   Make:= MakeCannon3;
   Move:= MoveCannon3;
   Aie:= AieCannon3;
   Die:= DieCannon3;
   weight:= 120;
   heatSpeed:= 8; coolSpeed:= 10;
   refreshSpeed:= 30;
   aieStKinds:= StoneSet{stFOG3}; aieSKCount:= 1;
   aieStone:= 5; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stC26}; dieSKCount:= 1;
   dieStone:= 18; dieStStyle:= slowStyle;
   basicType:= Mineral;
   priority:= -20;
   toKill:= TRUE
  END;
  AddTail(attrList[MACHINE], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetTurret;
   Make:= MakeTurret;
   Move:= MoveTurret;
   Aie:= AieCannon;
   Die:= DieTurret;
   weight:= 140;
   heatSpeed:= 10; coolSpeed:= 10;
   refreshSpeed:= 30;
   aieStKinds:= StoneSet{stFOG3}; aieSKCount:= 1;
   aieStone:= 5; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stC26}; dieSKCount:= 1;
   dieStone:= 12; dieStStyle:= fastStyle;
   basicType:= Mineral;
   priority:= -20;
   toKill:= TRUE
  END;
  AddTail(attrList[MACHINE], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetReactor;
   Make:= MakeReactor;
   Move:= MoveReactor;
   Aie:= AieReactor;
   Die:= DieReactor;
   charge:= 60;
   aieStKinds:= StoneSet{stFOG1, stFOG2, stFOG3}; aieSKCount:= 3;
   aieStone:= FlameMult + 12; aieStStyle:= slowStyle;
   basicType:= NotBase;
   priority:= -19;
   toKill:= FALSE
  END;
  AddTail(attrList[MACHINE], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= MakeDoor;
   Make:= MakeDoor;
   Move:= MoveDoor;
   Die:= DieDoor;
   dieStKinds:= StoneSet{stSTAR2}; dieSKCount:= 1;
   dieStone:= FlameMult * 6 + 28; dieStStyle:= fastStyle;
   basicType:= NotBase;
   priority:= 92;
   toKill:= FALSE
  END;
  AddTail(attrList[MACHINE], attr^.node)
 END InitParams;

BEGIN

 InitParams;

END ChaosMachine.
