MODULE ChaosCastle;

(* ChaosCastle
 * All files imported directly or indirectly by this one
 * and starting with 'Chaos' are (c) 1997 - 1999 by Nicky
*)

 FROM SYSTEM IMPORT ADR;
 FROM Checks IMPORT Terminate, Warn;
 FROM Memory IMPORT SET16, CARD8, CARD16, INT16, CARD32, INT32, First, Tail,
  Next, Empty;
 FROM ChaosBase IMPORT Frac, Period, Anims, AnimSet, BasicTypes, ObjAttr,
  ObjAttrPtr, Obj, ObjPtr, MoveProc, attrList, animList, nbAnim,
  objList, emptyObjList, nbEmpty, nbObj, FirstObj, NextObj, TailObj,
  FlushAllObjs, specialStage, pLife, nbDollar, nbSterling, GameStat, gameStat,
  Zone, zone, level, basics, shoot, score, Weapon, weaponAttr, powerCountDown,
  addpt, nbToKill, gunFiring, invinsibility, sleeper, doubleSpeed, magnet, air,
  freeFire, maxPower, noMissile, difficulty, playerPower, hurryUp,
  screenInverted, snow, water, weaponSelected, bombActive, lastJoy, weaponKey,
  nextObj, stages;
 FROM ChaosActions IMPORT HotInit, NextFrame, HotFlush;
 FROM ChaosPlayer IMPORT CheckStick;
 FROM ChaosScreens IMPORT TitleScreen, StartScreen, MakingScreen, ShopScreen,
  StatisticScreen, GameOverScreen;


 PROCEDURE InitPlayVars;
  VAR
   b: BasicTypes;
 BEGIN
  gunFiring:= FALSE;
  invinsibility:= 0; sleeper:= 0;
  maxPower:= 0; freeFire:= 0;
  magnet:= 0; air:= Period * 60;
  doubleSpeed:= 0; hurryUp:= 0;
  screenInverted:= 0; noMissile:= 0;
  playerPower:= 36;
  FOR b:= Bonus TO Animal DO basics[b].total:= 0; basics[b].done:= 0 END;
  shoot.total:= 0; shoot.done:= 0;
  weaponSelected:= FALSE; bombActive:= FALSE;
  lastJoy:= SET16{};
 END InitPlayVars;

 PROCEDURE InitVars;
  VAR
   w: Weapon;
   z: Zone;
 BEGIN
  FOR w:= MIN(Weapon) TO MAX(Weapon) DO
   WITH weaponAttr[w] DO
    power:= 0; nbBullet:= 0; nbBomb:= 0;
   END
  END;
  WITH weaponAttr[GUN] DO
   power:= 1; nbBullet:= 99
  END;
  powerCountDown:= 16;
  difficulty:= 1;
  snow:= FALSE; water:= FALSE;
  pLife:= 7;
  nbDollar:= 0; nbSterling:= 0;
  zone:= Chaos;
  FOR z:= MIN(Zone) TO MAX(Zone) DO level[z]:= 1 END;
  specialStage:= 0;
  stages:= 5;
  addpt:= 0; score:= 0;
  InitPlayVars
 END InitVars;

 PROCEDURE InitCold;
  VAR
   w: Weapon;
 BEGIN
  FOR w:= MIN(Weapon) TO MAX(Weapon) DO
   weaponKey[w]:= 0C
  END;
  InitVars
 END InitCold;

 PROCEDURE Play;
  VAR
   obj, tail: ObjPtr;
   maxl: CARD8;
   a: Anims;
 BEGIN
  WHILE (gameStat <> Gameover) AND (gameStat <> Break) DO
   FlushAllObjs;
   ShopScreen;
   InitPlayVars;
   IF (gameStat <> Break) AND (gameStat <> Gameover) THEN
    MakingScreen;
    Warn(Empty(animList[PLAYER]), ADR("Internal error"), ADR("Level without player"));
   END;
   IF gameStat = Playing THEN
    HotInit;
    WHILE gameStat = Playing DO
     IF Empty(animList[PLAYER]) THEN gameStat:= Finish END;
     FOR a:= MIN(Anims) TO MAX(Anims) DO
      obj:= First(animList[a]);
      tail:= Tail(animList[a]);
      WHILE (obj <> tail) AND (obj^.attr <> NIL) DO
       nextObj:= Next(obj^.animNode);
       obj^.attr^.Move(obj);
       obj:= nextObj
      END;
      CheckStick
     END;
     NextFrame
    END;
    HotFlush;
    StatisticScreen
   END;
   IF gameStat <> Gameover THEN
    IF zone = Chaos THEN
     maxl:= 100
    ELSIF zone = Castle THEN
     maxl:= 20
    ELSIF zone = Family THEN
     maxl:= 10
    ELSE
     maxl:= 24
    END;
    INC(level[zone]);
    IF level[zone] > maxl THEN
     level[zone]:= 1
    END
   ELSE
    GameOverScreen
   END
  END
 END Play;

BEGIN

 InitCold;
 TitleScreen;
 REPEAT
  InitVars;
  StartScreen;
  Play
 UNTIL gameStat = Break;
 Terminate;

END ChaosCastle.
