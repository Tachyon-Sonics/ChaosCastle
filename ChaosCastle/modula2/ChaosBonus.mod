IMPLEMENTATION MODULE ChaosBonus;

 FROM SYSTEM IMPORT ADR;
 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32, AllocMem,
  AddTail, CopyStr, StrPtr;
 FROM Checks IMPORT CheckMem, Check;
 FROM Languages IMPORT ADL;
 FROM Trigo IMPORT RND;
 FROM ChaosBase IMPORT Anims, AnimSet, BasicTypes, Obj, ObjPtr, ObjAttr,
  ObjAttrPtr, attrList, gravityStyle, doubleSpeed, sleeper, invinsibility,
  magnet, specialStage, GameStat, gameStat, Period, AieProc, Stones, StoneSet,
  fastStyle, playerPower, freeFire, maxPower, noMissile, difficulty, Zone, zone,
  stages, WeaponAttr, weaponAttr, Weapon, password, level;
 FROM ChaosSounds IMPORT SoundList, soundList, Sound, Effect, SetEffect,
  SoundEffect, StereoEffect, nulSound;
 FROM ChaosActions IMPORT SetObjVXY, SetObjAXY, SetObjLoc, SetObjRect,
  UpdateXY, Die, OutOfBounds, OutOfScreen, AvoidBackground,
  GetCenter, Leave, CreateObj, DoCollision, PopMessage, statPos, DoToPlayer,
  DoToPlayerProc, LimitSpeed, actionPos, lifePos, moneyPos, NextStage,
  InBackground, AvoidBounds;
 FROM ChaosPlayer IMPORT AddMoney, AddWeapon, AddBomb, MakeInvinsible;


 PROCEDURE BoumMoney(obj: ObjPtr; coins: MoneySet; ns, nb: CARD16);
  VAR
   px, py, vx, vy: INT16;
   money: ObjPtr;
   rnd: CARD16;
   kind: Moneys;
 BEGIN
  IF zone = Family THEN RETURN END;
  GetCenter(obj, px, py);
  WHILE nb > 0 DO
   DEC(nb);
   rnd:= RND() MOD ns;
   kind:= MIN(Moneys);
   LOOP
    WHILE NOT(kind IN coins) DO INC(kind) END;
    IF rnd = 0 THEN EXIT END;
    INC(kind); DEC(rnd)
   END;
   vx:= RND() MOD 1024; DEC(vx, 512); INC(vx, obj^.vx DIV 4);
   vy:= RND() MOD 1024; DEC(vy, 896); INC(vy, obj^.vy DIV 4);
   money:= CreateObj(BONUS, Money, px, py, ORD(kind), 1);
   SetObjVXY(money, vx, vy);
   IF zone <> Castle THEN
    SetObjAXY(money, 0, 16)
   ELSE
    WITH money^ DO dvx:= 0; dvy:= 0 END
   END
  END
 END BoumMoney;

 PROCEDURE MakeMoney(money: ObjPtr);
  VAR
   px, py, ps: INT16;
 BEGIN
  WITH money^ DO
   hitSubLife:= 1;
   IF stat > 4 THEN
    px:= 212; py:= 12; ps:= 12
   ELSE
    px:= 246; py:= stat * 10; ps:= 10
   END
  END;
  SetObjLoc(money, px, py, ps, ps);
  SetObjRect(money, 0, 0, ps, ps)
 END MakeMoney;

 PROCEDURE MakeBonus(bonus: ObjPtr);
  VAR
   px, py, pw, ph: INT16;
 BEGIN
  pw:= 12; ph:= 12; py:= 32;
  bonus^.hitSubLife:= 1;
  CASE bonus^.stat OF
    tbDBSpeed: px:= 0; pw:= 14; ph:= 24
   |tbSGSpeed: px:= 14; pw:= 14; ph:= 24
   |tbMagnet: px:= 28
   |tbInvinsibility: px:= 28; py:= 44
   |tbSleeper: px:= 40
   |tbBullet: px:= 40; py:= 44
   |tbBonusLevel: px:= 64
   |tbHospital: px:= 64; py:= 44
   |tbFreeFire: px:= 64; py:= 56
   |tbMaxPower: px:= 64; py:= 68
   |tbNoMissile: px:= 212; py:= 132
   |tbDifficulty: px:= 64; py:= 80
   |tbExit: px:= 192; py:= 24; pw:= 32; ph:= 32
   |tbBomb: px:= 224; py:= 76; pw:= 8; ph:= 8
   |tbHelp: px:= 240; py:= 256; pw:= 16; ph:= 16
  END;
  SetObjLoc(bonus, px, py, pw, ph);
  SetObjRect(bonus, 0, 0, pw, ph)
 END MakeBonus;

 PROCEDURE GiveToPlayer(p, m: ObjPtr);
  VAR
   dollar, sterling: INT8;
 BEGIN
  dollar:= 0; sterling:= 0;
  CASE m^.stat OF
    0: dollar:= 1
   |1: dollar:= 2
   |2: dollar:= 3
   |3: dollar:= 5
   |4: dollar:= 10
   |5: sterling:= 1
  END;
  AddMoney(p, dollar, sterling);
  Die(m)
 END GiveToPlayer;

 PROCEDURE MoveMoney(money: ObjPtr);
 BEGIN
  IF OutOfScreen(money) THEN
   IF OutOfBounds(money) THEN Die(money) ELSE Leave(money) END;
   RETURN
  END;
  IF OutOfBounds(money) AND (money^.y > 0) THEN Die(money); RETURN END;
  IF zone <> Chaos THEN AvoidBackground(money, 2) END;
  UpdateXY(money);
  LimitSpeed(money, 1920);
  DoToPlayer(money, GiveToPlayer)
 END MoveMoney;

 VAR
  sleeperEffectL, sleeperEffectR, blvEffect: ARRAY[0..0] OF Effect;
  magnetEffect: ARRAY[0..8] OF Effect;
  invEffectL, invEffectR: ARRAY[0..0] OF Effect;
  diffEffect: ARRAY[0..3] OF Effect;
  ffEffectL: ARRAY[0..1] OF Effect;
  ffEffectR: ARRAY[0..2] OF Effect;
  sgSpeedEffectL, sgSpeedEffectR,
   dbSpeedEffectL, dbSpeedEffectR: ARRAY[0..2] OF Effect;
  hospitalEffect: ARRAY[0..12] OF Effect;
  maxPowerEffect: ARRAY[0..3] OF Effect;

 PROCEDURE DBSpeed(p, o: ObjPtr);
 BEGIN
  doubleSpeed:= 1;
  PopMessage(ADL("High speed"), statPos, 2);
  StereoEffect;
  SoundEffect(p, dbSpeedEffectR);
  SoundEffect(p, dbSpeedEffectL);
  Die(o)
 END DBSpeed;

 PROCEDURE SGSpeed(p, o: ObjPtr);
 BEGIN
  doubleSpeed:= 0;
  PopMessage(ADL("Normal speed"), statPos, 1);
  StereoEffect;
  SoundEffect(p, sgSpeedEffectL);
  SoundEffect(p, sgSpeedEffectR);
  Die(o)
 END SGSpeed;

 PROCEDURE Magnet(p, o: ObjPtr);
 BEGIN
  INC(magnet, Period * 40);
  SoundEffect(p, magnetEffect);
  PopMessage(ADL("Magnet"), statPos, 3);
  Die(o)
 END Magnet;

 PROCEDURE Invinsibility(p, o: ObjPtr);
 BEGIN
  MakeInvinsible(p, Period * 30);
  StereoEffect;
  SoundEffect(p, invEffectR);
  SoundEffect(p, invEffectL);
  PopMessage(ADL("Invinsibility"), statPos, 3);
  Die(o)
 END Invinsibility;

 PROCEDURE Sleeper(p, o: ObjPtr);
 BEGIN
  INC(sleeper, Period * 50);
  StereoEffect;
  SoundEffect(p, sleeperEffectL);
  SoundEffect(p, sleeperEffectR);
  PopMessage(ADL("* Soporific *"), statPos, 3);
  Die(o)
 END Sleeper;

 PROCEDURE Bullet(p, o: ObjPtr);
 BEGIN
  AddWeapon(p, 40);
  Die(o)
 END Bullet;

 PROCEDURE Bomb(p, o: ObjPtr);
 BEGIN
  AddBomb(p, 1);
  Die(o)
 END Bomb;

 PROCEDURE AddSS(p, o: ObjPtr);
 BEGIN
  SoundEffect(p, blvEffect);
  IF specialStage <  40 THEN INC(specialStage) END;
  Die(o)
 END AddSS;

 PROCEDURE Hospital(p, o: ObjPtr);
 BEGIN
  playerPower:= 36;
  SoundEffect(p, hospitalEffect);
  Die(o)
 END Hospital;

 PROCEDURE FreeFire(p, o: ObjPtr);
 BEGIN
  INC(freeFire, Period * 50);
  PopMessage(ADL("Free Fire"), statPos, 3);
  StereoEffect;
  SoundEffect(p, ffEffectL);
  SoundEffect(p, ffEffectR);
  Die(o)
 END FreeFire;

 PROCEDURE MaxPower(p, o: ObjPtr);
 BEGIN
  INC(maxPower, Period * 40);
  PopMessage(ADL("Maximum Power"), statPos, 3);
  SoundEffect(p, maxPowerEffect);
  Die(o)
 END MaxPower;

 PROCEDURE NoMissile(p, o: ObjPtr);
 BEGIN
  INC(noMissile, Period * 60);
  PopMessage(ADL("No missile"), statPos, 3);
  Die(o)
 END NoMissile;

 PROCEDURE Difficulty(p, o: ObjPtr);
  VAR
   str: ARRAY[0..32] OF CHAR;
   c: CARD16;
   a: CARD8;
   w: Weapon;
 BEGIN
  SoundEffect(p, diffEffect);
  IF difficulty <= 9 THEN
   IF (zone = Castle) AND (stages = 1) THEN NextStage END;
   INC(difficulty);
   CopyStr(ADL("Current level: #"), ADR(str), SIZE(str));
   c:= 0; WHILE (c < 30) AND (str[c] <> "#") DO INC(c) END;
   IF c < 30 THEN
    IF difficulty < 10 THEN
     str[c]:= CHR(ORD("0") + difficulty)
    ELSE
     str[c]:= "M"; str[c+1]:= "A"; str[c+2]:= "X"; str[c+3]:= 0C
    END
   END;
   PopMessage(ADL("Increasing difficulty"), lifePos, 8);
   PopMessage(ADR(str), actionPos, 8)
  ELSE
   Check(password, ADL("Ok, you finished the game."), ADL("But now, try it without the password !"));
   a:= 0;
   FOR w:= MIN(Weapon) TO MAX(Weapon) DO
    IF weaponAttr[w].power > 1 THEN a:= 1 END
   END;
   FOR w:= MIN(Weapon) TO MAX(Weapon) DO
    WITH weaponAttr[w] DO
     IF power > 1 THEN
      IF w = GUN THEN power:= 1 ELSE power:= a END
     END
    END
   END;
   gameStat:= Finish
  END;
  Die(o)
 END Difficulty;

 PROCEDURE Exit(p, o: ObjPtr);
 BEGIN
  IF (gameStat = Playing) AND (zone <> Family) THEN gameStat:= Finish END;
  Die(o)
 END Exit;

 PROCEDURE Help(p, o: ObjPtr);
  VAR
   str1, str2: StrPtr;
 BEGIN
  str1:= NIL; str2:= NIL;
  IF zone = Castle THEN
   IF level[Castle] = 1 THEN
    IF difficulty = 1 THEN
     str1:= ADL("Target:");
     str2:= ADL("Find the EXIT")
    ELSE
     str1:= ADL("The hospital-aliens may");
     str2:= ADL("help you if you're broken")
    END
   ELSIF level[Castle] = 2 THEN
    str1:= ADL("1st stage before PMM:");
    str2:= ADL("Get a score of 1000")
   ELSIF level[Castle] = 3 THEN
    str1:= ADL("Diamond-bonuses enable");
    str2:= ADL("bonus levels")
   ELSIF level[Castle] = 7 THEN
    IF stages >= 4 THEN
     str1:= ADL("2nd stage before PMM:");
     str2:= ADL("Get 20 lives")
    ELSE
     str1:= ADL("The Mother Alien");
     str2:= ADL("doesn't like gun bombs")
    END
   ELSIF level[Castle] = 4 THEN
    str1:= ADL("The EXIT is");
    str2:= ADL("near the center")
   ELSIF level[Castle] = 5 THEN
    IF difficulty = 1 THEN
     str1:= ADL("Many levels contains");
     str2:= ADL("hidden passages")
    ELSE
     str1:= ADL("The 1st Skull Bonus is");
     str2:= ADL("hidden in Chaos Level 100")
    END
   ELSIF level[Castle] = 6 THEN
    str1:= ADL("Only the canon near you");
    str2:= ADL("can destroy some items")
   ELSIF level[Castle] = 8 THEN
    IF stages >= 3 THEN
     str1:= ADL("3rd stage before PMM:");
     str2:= ADL("Wait for five 'hurry up's")
    ELSE
     str1:= ADL("The cash can save you if");
     str2:= ADL("you are out of lives")
    END
   ELSIF level[Castle] = 9 THEN
    str1:= ADL("Balance yourself if you");
    str2:= ADL("are blocked by a magnet")
   ELSIF level[Castle] = 11 THEN
    IF stages >= 2 THEN
     str1:= ADL("4th stage before PMM:");
     str2:= ADL("Get 100$")
    ELSIF weaponAttr[GUN].power <> 4 THEN
     str1:= ADL("Hint:");
     str2:= ADL("Increase gun's power")
    ELSE
     str1:= ADL("Hint: Let the Sister Alien");
     str2:= ADL("create hospital-aliens")
    END
   ELSIF level[Castle] = 15 THEN
    IF stages >= 1 THEN
     str1:= ADL("Last stage before PMM:");
     str2:= ADL("Find the 2nd Skull Bonus")
    ELSE
     str1:= ADL("Aliens release a heart if");
     str2:= ADL("lives=1 and level MOD 7=0")
    END
   ELSIF level[Castle] = 16 THEN
    str1:= ADL("3rd Skull Bonus is hidden in");
    str2:= ADL("the zone 'Family' (last level)")
   ELSIF level[Castle] = 20 THEN
    IF difficulty <= 2 THEN
     str1:= ADL("The Skull Bonus raises");
     str2:= ADL("the game's difficulty")
    ELSE
     str1:= ADL("Mind bonus level 24");
     str2:= ADL("hehehehehehe...")
    END
   END
  ELSIF zone = Family THEN
   IF level[Family] = 1 THEN
    str1:= ADL("After you've killed him");
    str2:= ADL("go near the center")
   ELSIF level[Family] = 4 THEN
    str1:= ADL("Sorry, no help available");
    str2:= ADL("ha ha ha")
   ELSE
    str1:= ADL("Make sure there's NOTHING");
    str2:= ADL("left before going on")
   END
  ELSIF zone = Special THEN
   IF level[Special] = 24 THEN
    str1:= ADL("Try to collect bonuses");
    str2:= ADL("rather than to kill")
   ELSE
    str1:= ADL("The 2nd Skull Bonus is");
    str2:= ADL("hidden in the Labyrinth")
   END
  END;
  IF str1 <> NIL THEN PopMessage(str1, statPos, 8) END;
  IF str2 <> NIL THEN PopMessage(str2, moneyPos, 8) END;
  Die(o)
 END Help;

 PROCEDURE MoveBonus(bonus: ObjPtr);
  VAR
   What: DoToPlayerProc;
 BEGIN
  IF OutOfScreen(bonus) THEN Leave(bonus); RETURN END;
  UpdateXY(bonus);
  AvoidBackground(bonus, 2);
  WITH bonus^ DO
   IF stat = tbDifficulty THEN AvoidBounds(bonus, 3) END;
   IF OutOfBounds(bonus) OR
      (InBackground(bonus) AND (bonus^.stat <> tbBomb)) THEN
    Die(bonus); RETURN
   END;
   dvx:= 0; dvy:= 0;
   CASE stat OF
     tbDBSpeed: What:= DBSpeed
    |tbSGSpeed: What:= SGSpeed
    |tbMagnet: What:= Magnet
    |tbInvinsibility: What:= Invinsibility
    |tbSleeper: What:= Sleeper
    |tbBullet: What:= Bullet
    |tbBonusLevel: What:= AddSS
    |tbHospital: What:= Hospital
    |tbFreeFire: What:= FreeFire
    |tbMaxPower: What:= MaxPower
    |tbNoMissile: What:= NoMissile
    |tbDifficulty: What:= Difficulty
    |tbExit: What:= Exit
    |tbBomb: What:= Bomb
    |tbHelp: What:= Help
   END;
   DoToPlayer(bonus, What)
  END
 END MoveBonus;

 PROCEDURE InitParams;
  VAR
   attr: ObjAttrPtr;
   c, d, v: CARD16;
 BEGIN
  SetEffect(sleeperEffectL[0], soundList[sCaisse], 0, 4181, 80, 14);
  SetEffect(sleeperEffectR[0], soundList[sCaisse], 0, 4694, 80, 12);
  SetEffect(blvEffect[0], soundList[wJans], 0, 0, 200, 13);
  SetEffect(magnetEffect[0], soundList[wWhite], 697, 0, 8, 13);
  SetEffect(magnetEffect[8], nulSound, 0, 0, 8, 1);
  FOR c:= 1 TO 4 DO
   d:= 8 - c;
   v:= (c + 1) * 50;
   v:= (v * v) DIV 280;
   SetEffect(magnetEffect[c], nulSound, 697, 0, v, 13);
   SetEffect(magnetEffect[d], nulSound, 0, 0, v, c * 3 + 1);
  END;
  SetEffect(sgSpeedEffectL[0], soundList[wPanflute], 16726, 25089, 110, 6);
  SetEffect(sgSpeedEffectL[1], nulSound, 1742, 20908, 110, 6);
  SetEffect(sgSpeedEffectL[2], nulSound, 1394, 16726, 110, 6);
  SetEffect(sgSpeedEffectR[0], soundList[wPanflute], 11152, 16726, 110, 4);
  SetEffect(sgSpeedEffectR[1], nulSound, 1565, 18774, 110, 4);
  SetEffect(sgSpeedEffectR[2], nulSound, 1394, 16726, 110, 4);
  SetEffect(dbSpeedEffectR[0], soundList[wPanflute], 11151, 16726, 220, 11);
  SetEffect(dbSpeedEffectR[1], nulSound, 1742, 20908, 220, 11);
  SetEffect(dbSpeedEffectR[2], nulSound, 2091, 25089, 220, 11);
  SetEffect(dbSpeedEffectL[0], soundList[wPanflute], 16726, 25089, 220, 9);
  SetEffect(dbSpeedEffectL[1], nulSound, 1863, 22352, 220, 9);
  SetEffect(dbSpeedEffectL[2], nulSound, 2091, 25089, 220, 9);
  SetEffect(invEffectL[0], soundList[sGong], 0, 16726, 240, 12);
  SetEffect(invEffectR[0], soundList[sGong], 0, 14901, 240, 14);
  SetEffect(ffEffectL[0], soundList[sCaisse], 2000, 12544, 140, 11);
  SetEffect(ffEffectL[1], soundList[sCaisse], 0, 12544, 100, 11);
  SetEffect(ffEffectR[0], soundList[sCaisse], 160, 8363, 0, 11);
  SetEffect(ffEffectR[1], soundList[sCaisse], 2000, 12544, 140, 11);
  SetEffect(ffEffectR[2], soundList[sCaisse], 0, 12544, 100, 11);
  FOR c:= 0 TO 3 DO
   SetEffect(diffEffect[c], soundList[sHa], 0, 0, 240 - c * 60, 15 - c * 4)
  END;
  SetEffect(maxPowerEffect[0], soundList[wShakuhachi], 929, 8363, 150, 11);
  SetEffect(maxPowerEffect[1], nulSound, 1595, 11163, 150, 11);
  SetEffect(maxPowerEffect[2], nulSound, 1043, 9387, 150, 11);
  SetEffect(maxPowerEffect[3], nulSound, 1790, 12530, 150, 11);
  SetEffect(hospitalEffect[0], soundList[aPanflute], 0, 16726, 240, 14);
  SetEffect(hospitalEffect[1], soundList[wPanflute], 1440, 17623, 30, 13);
  FOR c:= 2 TO 12 DO
   SetEffect(hospitalEffect[c], nulSound, 1440, 16726 + 897 * c, (16 - c) * 2, 12)
  END;
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= MakeMoney;
   Make:= MakeMoney;
   Move:= MoveMoney;
   charge:= -64;
   weight:= 8;
   inerty:= 6;
   basicType:= Bonus;
   priority:= 60;
   toKill:= TRUE
  END;
  AddTail(attrList[BONUS], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= MakeBonus;
   Make:= MakeBonus;
   Move:= MoveBonus;
   charge:= 32;
   weight:= 16;
   inerty:= 4;
   dieStone:= 8; dieStStyle:= fastStyle;
   dieSKCount:= 1; dieStKinds:= StoneSet{stRBOX};
   basicType:= Bonus;
   priority:= 75;
   toKill:= TRUE
  END;
  AddTail(attrList[BONUS], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= MakeBonus;
   Make:= MakeBonus;
   Move:= MoveBonus;
   charge:= 32;
   weight:= 16;
   inerty:= 4;
   dieStone:= 8; dieStStyle:= fastStyle;
   dieSKCount:= 1; dieStKinds:= StoneSet{stRBOX};
   basicType:= Bonus;
   priority:= 75;
   toKill:= FALSE
  END;
  AddTail(attrList[BONUS], attr^.node);
 END InitParams;

BEGIN

 InitParams;

END ChaosBonus.
