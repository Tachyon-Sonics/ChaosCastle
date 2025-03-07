IMPLEMENTATION MODULE ChaosSmartBonus;

 FROM SYSTEM IMPORT ADR;
 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32, AllocMem, AddTail;
 FROM Checks IMPORT CheckMem;
 FROM ChaosBase IMPORT Anims, AnimSet, BasicTypes, Obj, ObjPtr, ObjAttr,
  ObjAttrPtr, attrList, gravityStyle, doubleSpeed, sleeper, invinsibility,
  specialStage, GameStat, gameStat, Period, AieProc, Stones, StoneSet,
  powerCountDown;
 FROM ChaosActions IMPORT SetObjRect, SetObjLoc, UpdateXY, Aie, Die,
  OutOfBounds, OutOfScreen, AvoidBackground, GetCenter, Leave, DoCollision,
  PopMessage, statPos, DoToPlayer, DoToPlayerProc;
 FROM ChaosPlayer IMPORT AddLife, AddPower;


 PROCEDURE MakeBonus(bonus: ObjPtr);
  VAR
   py: INT16;
 BEGIN
  WITH bonus^ DO
   hitSubLife:= 1;
   IF subKind = sbExtraLife THEN py:= 32 ELSE py:= 44 END;
   SetObjLoc(bonus, 52, py, 12, 12);
   SetObjRect(bonus, 0, 0, 12, 12)
  END
 END MakeBonus;

 PROCEDURE Life(player, bonus: ObjPtr);
 BEGIN
  AddLife(player);
  Die(bonus)
 END Life;

 PROCEDURE Power(player, bonus: ObjPtr);
 BEGIN
  AddPower(player, 1);
  IF powerCountDown > 0 THEN DEC(powerCountDown) END;
  Die(bonus)
 END Power;

 PROCEDURE MoveBonus(bonus: ObjPtr);
  VAR
   What: DoToPlayerProc;
   h, f: CARD16;
 BEGIN
  IF OutOfScreen(bonus) THEN Leave(bonus); RETURN END;
  UpdateXY(bonus);
  IF OutOfBounds(bonus) THEN Die(bonus); RETURN END;
  AvoidBackground(bonus, 1);
  IF bonus^.subKind = sbExtraLife THEN What:= Life ELSE What:= Power END;
  h:= 200; f:= 200;
  DoCollision(bonus, AnimSet{ALIEN3, ALIEN2, ALIEN1, MISSILE,
              DEADOBJ, MACHINE}, Aie, h, f);
  IF (h < 200) OR (f < 200) THEN
   WITH bonus^ DO
    vy:= -ABS(vy); IF vy < -1024 THEN vy:= -1024 END;
    dvy:= vy
   END
  END;
  DoToPlayer(bonus, What)
 END MoveBonus;

 PROCEDURE InitParams;
  VAR
   attr: ObjAttrPtr;
 BEGIN
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= MakeBonus;
   Make:= MakeBonus;
   Move:= MoveBonus;
   charge:= 4;
   weight:= 32;
   basicType:= Bonus;
   dieStone:= 12; dieStStyle:= gravityStyle;
   dieSKCount:= 1; dieStKinds:= StoneSet{stCBOX};
   priority:= 90;
   toKill:= TRUE
  END;
  AddTail(attrList[SMARTBONUS], attr^.node)
 END InitParams;

BEGIN

 InitParams;
 InitParams;

END ChaosSmartBonus.
