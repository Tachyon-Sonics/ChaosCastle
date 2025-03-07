IMPLEMENTATION MODULE ChaosPlayer;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM Checks IMPORT CheckMem, Check;
 FROM Memory IMPORT SET16, CARD8, INT8, CARD16, INT16, CARD32, INT32,
  AllocMem, AddHead, First, Next, Tail, StrPtr, CopyStr;
 FROM Languages IMPORT ADL;
 FROM Clock IMPORT GetTime;
 FROM Input IMPORT eKEYBOARD, JoyLeft, JoyRight, JoyUp, JoyDown, JoyPause,
  JoyReverse, JoyForward, JoyShift, EventTypeSet, Event, GetEvent, FlushEvents,
  GetMouse, GetStick, Joy1, Joy2, Joy3, Joy4, SendEvent, SetBusyStat, statWaiting,
  eNUL, WaitEvent, Modifiers, ModifierSet, eMENU;
 FROM ChaosSounds IMPORT SoundList, soundList, Sound, SoundEffect, Effect,
  SetEffect, ModulateSounds, StereoEffect, nulSound;
 FROM Graphics IMPORT SetArea, SetCopyMode, cmCopy, cmTrans, cmXor,
  GetBuffer, SetBuffer, WaitTOF, FillRect, FillEllipse, SetPen, SetPat,
  DrawLine, SetTextMode, TextModes, TextModeSet, SetTextSize, UpdateArea,
  ScrollRect;
 FROM Registration IMPORT registered;
 FROM ChaosGraphics IMPORT shapeArea, maskArea, SetOrigin, X, Y, mulS,
  W, H, palette, WriteAt, CenterText, WriteCard, color, PW, PH, IW,
  IH, MoveBackground, mainArea, UpdateAnim, cycle, cycleSpeed, cycling,
  DrawBackground, castle, castleWidth, castleHeight, NbClear, BW, BH,
  dualCycle, dualCycleSpeed, dualCycling, dualpf, AnimPalette, SetRGB, SetTrans;
 FROM ChaosBase IMPORT Anims, AnimSet, ObjAttr, ObjAttrPtr, Obj, ObjPtr,
  MakeProc, MoveProc, AieProc, DieProc, attrList, mainPlayer,
  GameStat, Weapon, WeaponSet, WeaponAttr, gameStat, weaponAttr,
  invinsibility, sleeper, doubleSpeed, specialStage, screenInverted,
  pLife, nbDollar, nbSterling, Period, Stones, StoneSet, slowStyle,
  gravityStyle, returnStyle, fastStyle, FlameMult, Zone, zone, level,
  buttonAssign, weaponKey, selectedWeapon, weaponSelected, bombActive, lastJoy,
  gunFiring, FireProc, Fire, Bomb, nextGunFireTime, GunFireSpeed, animList,
  addpt, score, BasicTypes, magnet, air, snow, water, freeFire, maxPower,
  playerPower, noMissile, lasttime, step, difficulty, stages, powerCountDown,
  password;
 FROM ChaosInterface IMPORT CommonEvent;
 FROM ChaosActions IMPORT SetObjLoc, SetObjRect, HotInit, HotFlush, lastMouseX,
  lastMouseY, statPos, moneyPos, lifePos, actionPos, PopMessage, UpdateXY,
  DecLife, Aie, Die, Boum, AvoidBounds, AvoidBackground, CheckLeftObjs,
  Gravity, GetCenter, CreateObj, Collision, NextStage, AddPt, time;
 FROM ChaosWeapon IMPORT GetBulletPrice;

 (** SubKinds of player:
   * 0: player 1/2
   * 1: link player(s)
   * 2: computer
   *)

 TYPE
  Infos = (TITLE, LEVEL, LIFE, SCORE, DOLLAR, STERLING);
  InfoSet = SET OF Infos;

 VAR
  wChanges, owChanges, nChanges, onChanges: WeaponSet;
  gChanges, ogChanges: InfoSet;


 PROCEDURE SetP(pen: CARD8);
 BEGIN
  IF color THEN
   SetPen(pen)
  ELSIF pen > 0 THEN
   SetPen(1)
  ELSE
   SetPen(0)
  END
 END SetP;

 VAR
  dMagnet, dInv, dSleeper, dAir, dFF, dMaxPower, dPlayerPower: CARD16;
  oldMpx, oldMpy: ARRAY[MIN(BOOLEAN)..MAX(BOOLEAN)] OF INT16;
  mpx, mpy: INT16;
  infoBackPen: CARD8;

 PROCEDURE DrawMapLine(sx, sy, dx, dy: INT16);
  VAR
   lx, ly, x, y, px, py, c: INT16;
   lon, on: BOOLEAN;
 BEGIN
  lx:= sx; ly:= sy; lon:= FALSE;
  x:= sx; y:= sy;
  px:= mpx + sx; py:= mpy + sy;
  FOR c:= 0 TO 31 DO
   on:= (px >= 0) AND (py >= 0) AND (px < castleWidth) AND
        (py < castleHeight) AND (castle^[py, px] >= NbClear);
   IF (on <> lon) THEN
    IF c > 0 THEN
     IF lon THEN SetP(7) ELSE SetPen(0) END;
     FillRect(X(lx), Y(ly), X(x + dy), Y(y + dx))
    END;
    lon:= on; lx:= x; ly:= y
   END;
   INC(x, dx); INC(y, dy); INC(px, dx); INC(py, dy)
  END;
  IF lon THEN SetP(7) ELSE SetPen(0) END;
  FillRect(X(lx), Y(ly), X(x + dy), Y(y + dx))
 END DrawMapLine;

 PROCEDURE SetMapCoords;
 BEGIN
  GetCenter(mainPlayer, mpx, mpy);
  mpx:= mpx DIV BW - 16;
  mpy:= mpy DIV BH - 16
 END SetMapCoords;

 PROCEDURE DrawMap;
  VAR
   y: INT16;
 BEGIN
  SetMapCoords;
  oldMpx[TRUE]:= mpx; oldMpy[TRUE]:= mpy;
  oldMpx[FALSE]:= mpx; oldMpy[FALSE]:= mpy;
  SetCopyMode(cmCopy);
  SetOrigin(PW + 21, 5);
  SetP(4); FillRect(X(0), Y(0), X(38), Y(38));
  SetP(3); FillRect(X(0), Y(0), X(37), Y(37));
  SetP(7); FillRect(X(1), Y(1), X(37), Y(37));
  IF color THEN
   SetPen(4); FillRect(X(2), Y(2), X(36), Y(36));
   SetPen(3); FillRect(X(3), Y(3), X(36), Y(36))
  ELSE
   SetPen(0); FillRect(X(2), Y(2), X(36), Y(36))
  END;
  SetOrigin(PW + 24, 8);
  FOR y:= 0 TO 31 DO
   DrawMapLine(0, y, 1, 0)
  END;
  SetP(2); FillRect(X(16), Y(16), X(17), Y(17));
  SetCopyMode(cmTrans); SetOrigin(PW, 0)
 END DrawMap;

 PROCEDURE ScrollMap;
  VAR
   dx, dy: INT16;
   first, off: BOOLEAN;
 BEGIN
  GetBuffer(first, off);
  dx:= oldMpx[first] - mpx; dy:= oldMpy[first] - mpy;
  SetOrigin(PW + 24, 8);
  SetPen(0); FillRect(X(16), Y(16), X(17), Y(17));
  ScrollRect(X(0), Y(0), W(32), H(32), W(dx), H(dy));
  SetP(2); FillRect(X(16), Y(16), X(17), Y(17));
  WHILE dx < 0 DO INC(dx); DrawMapLine(31 + dx, 0, 0, 1) END;
  WHILE dx > 0 DO DEC(dx); DrawMapLine(dx, 0, 0, 1) END;
  WHILE dy < 0 DO INC(dy); DrawMapLine(0, 31 + dy, 1, 0) END;
  WHILE dy > 0 DO DEC(dy); DrawMapLine(0, dy, 1, 0) END;
  oldMpx[first]:= mpx; oldMpy[first]:= mpy
 END ScrollMap;

 PROCEDURE DrawTitle;
  CONST
   TTL1 = "Chaos";
   TTL2 = " Castle";
  VAR
   p1: CARD8;
 BEGIN
  IF (zone = Castle) AND (gameStat = Playing) THEN
   DrawMap
  ELSE
   IF color THEN p1:= 7 ELSE p1:= 0 END;
   SetP(4); WriteAt(X(5), Y(5), ADR(TTL1));
   SetP(3); WriteAt(X(4), Y(4), ADR(TTL1));
   SetP(p1); WriteAt(X(5), Y(4), ADR(TTL1));
   SetP(4); WriteAt(X(5), Y(14), ADR(TTL2));
   SetP(3); WriteAt(X(4), Y(13), ADR(TTL2));
   SetP(p1); WriteAt(X(5), Y(13), ADR(TTL2))
  END
 END DrawTitle;

 PROCEDURE DrawLevel;
  VAR
   str: ARRAY[0..41] OF CHAR;
   ln: StrPtr;
 BEGIN
  IF (zone <> Castle) OR (gameStat <> Playing) THEN
   IF zone = Chaos THEN ln:= ADR("Chaos")
   ELSIF zone = Castle THEN ln:= ADR("Castle")
   ELSIF zone = Family THEN ln:= ADR("Family")
   ELSE ln:= ADR("*Bonus*")
   END;
   SetPen(infoBackPen); FillRect(X(4), Y(24), X(IW - 4), Y(42));
   SetP(6); WriteAt(X(4), Y(33), ln);
   WriteCard(X(58), Y(33), level[zone] MOD 100);
   str:= "Diff";
   WriteAt(X(4), Y(24), ADL(str));
   str:= " # ";
   IF difficulty < 10 THEN
    str[1]:= CHR(48 + difficulty)
   ELSE
    str:= "MAX"
   END;
   SetP(6); WriteAt(X(53), Y(24), ADL(str))
  END
 END DrawLevel;

 PROCEDURE DrawLife;
 BEGIN
  SetPen(infoBackPen); FillRect(X(4), Y(46), X(IW - 4), Y(55));
  SetP(5); WriteAt(X(4), Y(46), ADL("Lives:"));
  SetP(2); WriteCard(X(58), Y(46), pLife)
 END DrawLife;

 PROCEDURE DrawScore;
 BEGIN
  SetPen(infoBackPen); FillRect(X(4), Y(55), X(IW - 4), Y(64));
  IF (zone = Chaos) OR NOT(color) THEN SetPen(1) ELSE SetPen(0) END;
  WriteCard(X(4), Y(55), score)
 END DrawScore;

 PROCEDURE DrawDollar;
 BEGIN
  SetPen(infoBackPen); FillRect(X(4), Y(64), X(IW DIV 2), Y(73));
  SetPen(5); WriteAt(X(4), Y(64), ADR("$ "));
  WriteCard(X(12), Y(64), nbDollar)
 END DrawDollar;

 PROCEDURE DrawSterling;
 BEGIN
  SetPen(infoBackPen); FillRect(X(IW DIV 2), Y(64), X(IW - 4), Y(73));
  SetPen(5); WriteAt(X(IW DIV 2), Y(64), ADR("£ "));
  WriteCard(X(IW DIV 2 + 8), Y(64), nbSterling)
 END DrawSterling;

 PROCEDURE DrawTime(p: CARD8; y, h: INT16; what: CARD16; VAR dWhat: CARD16);
  VAR
   width: INT16;
 BEGIN
  IF (p <> 0) AND (p = infoBackPen) THEN p:= 1 END;
  width:= what DIV (Period DIV 2);
  IF width > 72 THEN width:= 72 END;
  IF width > 0 THEN
   IF color THEN SetPen(p) ELSE SetPen(1) END;
   FillRect(X(4), Y(y), X(width + 4), Y(y + h))
  END;
  IF width < 72 THEN
   SetPen(infoBackPen); FillRect(X(width + 4), Y(y), X(76), Y(y + h))
  END;
  dWhat:= what
 END DrawTime;

 PROCEDURE DrawMagnet;
 BEGIN
  DrawTime(2, 74, 1, magnet, dMagnet)
 END DrawMagnet;

 PROCEDURE DrawInv;
 BEGIN
  DrawTime(3, 75, 1, invinsibility, dInv)
 END DrawInv;

 PROCEDURE DrawFF;
 BEGIN
  DrawTime(6, 76, 1, freeFire, dFF)
 END DrawFF;

 PROCEDURE DrawSleeper;
 BEGIN
  DrawTime(7, 77, 1, sleeper, dSleeper)
 END DrawSleeper;

 PROCEDURE DrawMaxPower;
 BEGIN
  DrawTime(4, 78, 1, maxPower, dMaxPower)
 END DrawMaxPower;

 PROCEDURE DrawAir;
 BEGIN
  DrawTime(0, 79, 1, air, dAir)
 END DrawAir;

 PROCEDURE DrawPlayerPower;
 BEGIN
  DrawTime(5, 80, 2, playerPower * Period, dPlayerPower)
 END DrawPlayerPower;

 PROCEDURE DrawNumber(w: Weapon; select: BOOLEAN);
  VAR
   y: INT16;
 BEGIN
  SetPen(infoBackPen);
  y:= ORD(w) * 19 + 93;
  FillRect(X(27), Y(y), X(45), Y(y + 9));
  IF select THEN
   SetP(3); SetTextMode(TextModeSet{bold, italic})
  ELSE
   SetP(4); SetTextMode(TextModeSet{})
  END;
  WriteCard(X(27), Y(ORD(w) * 19 + 93), weaponAttr[w].nbBullet);
  IF select THEN SetTextMode(TextModeSet{}) END
 END DrawNumber;

 PROCEDURE WeaponToStr(w: Weapon): StrPtr;
 BEGIN
  CASE w OF
    GUN: RETURN ADL("Gun")
   |FB: RETURN ADL("Fireball")
   |LASER: RETURN ADL("Laser")
   |BUBBLE: RETURN ADL("Bubble")
   |FIRE: RETURN ADL("Fire")
   |BALL: RETURN ADL("Ball")
   |STAR: RETURN ADL("Star")
   |GRENADE: RETURN ADL("Grenade")
  END
 END WeaponToStr;

 PROCEDURE DrawWeapon(w: Weapon; select: BOOLEAN);
  VAR
   ln: StrPtr;
   y: INT16;
   p: CARD8;
 BEGIN
  y:= ORD(w) * 19 + 84;
  SetPen(infoBackPen); FillRect(X(4), Y(y), X(IW - 4), Y(y + 19));
  IF select THEN
   SetP(3); SetTextMode(TextModeSet{bold, italic})
  ELSE
   SetP(4); SetTextMode(TextModeSet{})
  END;
  ln:= WeaponToStr(w);
  CenterText(X(4), Y(y), W(IW - 8), ln); INC(y, 9);
  SetTextMode(TextModeSet{});
  DrawNumber(w, select);
  WriteAt(X(45), Y(y), ADR("/ "));
  WriteCard(X(54), Y(y), weaponAttr[w].nbBomb);
  p:= weaponAttr[w].power;
  FillRect(X(9), Y(y), X(18), Y(y + 9));
  SetP(infoBackPen);
  IF p > 3 THEN SetP(3) END;
  FillRect(X(14), Y(y + 1), X(17), Y(y + 4));
  IF p > 2 THEN SetP(3) END;
  FillRect(X(10), Y(y + 1), X(13), Y(y + 4));
  IF p > 1 THEN SetP(3) END;
  FillRect(X(14), Y(y + 5), X(17), Y(y + 8));
  IF p > 0 THEN SetP(3) END;
  FillRect(X(10), Y(y + 5), X(13), Y(y + 8))
 END DrawWeapon;

 PROCEDURE DrawRect(x1, y1, x2, y2: INT16);
 BEGIN
  DrawLine(x1, y1, x2, y1);
  DrawLine(x1, y2, x2, y2);
  INC(y1); DEC(y2);
  DrawLine(x1, y1, x1, y2);
  DrawLine(x2, y1, x2, y2)
 END DrawRect;

 PROCEDURE UpdateInfos;
  VAR
   w: Weapon;
 BEGIN
  SetOrigin(PW, 0);
  SetCopyMode(cmTrans); SetPat(4);
  SetTextSize(H(9));
  IF (gameStat = Playing) THEN
   IF gChanges <> InfoSet{} THEN
    ogChanges:= ogChanges + gChanges; gChanges:= ogChanges
   END;
   IF wChanges <> WeaponSet{} THEN
    owChanges:= owChanges + wChanges; wChanges:= owChanges
   END;
   IF nChanges <> WeaponSet{} THEN
    onChanges:= onChanges + nChanges; nChanges:= onChanges
   END
  ELSE
   ogChanges:= gChanges; owChanges:= wChanges; onChanges:= nChanges
  END;
  IF magnet <> dMagnet THEN DrawMagnet END;
  IF invinsibility <> dInv THEN DrawInv END;
  IF freeFire <> dFF THEN DrawFF END;
  IF sleeper <> dSleeper THEN DrawSleeper END;
  IF maxPower <> dMaxPower THEN DrawMaxPower END;
  IF air <> dAir THEN DrawAir END;
  IF playerPower <> dPlayerPower THEN DrawPlayerPower END;
  IF TITLE IN ogChanges THEN DrawTitle END;
  IF LEVEL IN ogChanges THEN DrawLevel END;
  IF LIFE IN ogChanges THEN DrawLife END;
  IF SCORE IN ogChanges THEN DrawScore END;
  IF DOLLAR IN ogChanges THEN DrawDollar END;
  IF STERLING IN ogChanges THEN DrawSterling END;
  FOR w:= MIN(Weapon) TO MAX(Weapon) DO
   IF w IN owChanges THEN
    DrawWeapon(w, weaponSelected AND (selectedWeapon = w))
   ELSIF w IN onChanges THEN
    DrawNumber(w, weaponSelected AND (selectedWeapon = w))
   END
  END;
  ogChanges:= gChanges; gChanges:= InfoSet{};
  owChanges:= wChanges; wChanges:= WeaponSet{};
  onChanges:= nChanges; nChanges:= WeaponSet{}
 END UpdateInfos;

 PROCEDURE DrawInfos(db: BOOLEAN);
  CONST
   Inv = MAX(CARD16);
  VAR
   first, off: BOOLEAN;
 BEGIN
  IF db THEN
   GetBuffer(first, off);
   SetBuffer(first, TRUE)
  END;
  SetOrigin(PW, 0);
  dMagnet:= Inv; dInv:= Inv;
  dSleeper:= Inv; dAir:= Inv;
  dMaxPower:= Inv; dFF:= Inv;
  dPlayerPower:= Inv;
  SetCopyMode(cmCopy); SetPat(4);
  SetTextSize(H(9));
  IF NOT(color) THEN infoBackPen:= 0
  ELSIF zone = Chaos THEN infoBackPen:= 0
  ELSIF zone = Castle THEN infoBackPen:= 1
  ELSIF zone = Family THEN infoBackPen:= 7
  ELSE infoBackPen:= 2
  END;
  SetPen(infoBackPen);
  FillRect(X(0), Y(0), X(IW), Y(IH));
  SetP(5); DrawRect(X(0), Y(0), X(IW) - 2, Y(IH) - 2);
  SetP(0); DrawRect(X(0) + 2, Y(0) + 2, X(IW), Y(IH));
  SetP(6); DrawRect(X(0) + 1, Y(0) + 1, X(IW) - 1, Y(IH) - 1);
  DrawLine(X(0) + 3, Y(45), X(IW) - 4, Y(45));
  DrawLine(X(0) + 3, Y(83), X(IW) - 4, Y(83));
  gChanges:= InfoSet{TITLE, LEVEL, LIFE, SCORE, DOLLAR, STERLING};
  wChanges:= WeaponSet{GUN, FB, LASER, BUBBLE, FIRE, BALL, STAR, GRENADE};
  UpdateInfos;
  IF db THEN SetBuffer(first, off) END
 END DrawInfos;

 VAR
  lifeEffectL, lifeEffectR: ARRAY[0..23] OF Effect;
  life0Effect: ARRAY[0..1] OF Effect;
  life3Effect: ARRAY[0..2] OF Effect;
  life2Effect, mioEffect: ARRAY[0..4] OF Effect;
  life1Effect: ARRAY[0..8] OF Effect;
  moneyEffect: ARRAY[0..0] OF Effect;
  cBulletEffectL, cBulletEffectR: ARRAY[0..6] OF Effect;
  bulletEffect: ARRAY[0..0] OF Effect;
  cPowerEffect: ARRAY[0..0] OF Effect;
  powerEffectL: ARRAY[0..11] OF Effect;
  powerEffectR: ARRAY[0..12] OF Effect;
  savedEffect: ARRAY[0..0] OF Effect;
  dieEffects: ARRAY[0..0] OF Effect;


 PROCEDURE AddLife(player: ObjPtr);
 BEGIN
  IF pLife >= 29 THEN RETURN END;
  IF player <> NIL THEN INC(player^.life) END;
  INC(pLife);
  IF (pLife >= 20) AND (stages = 4) THEN NextStage END;
  IF gameStat = Playing THEN
   StereoEffect;
   SoundEffect(player, lifeEffectL);
   SoundEffect(player, lifeEffectR);
   PopMessage(ADL("EXTRA LIFE"), lifePos, 4)
  END;
  INCL(gChanges, LIFE)
 END AddLife;

 PROCEDURE AddMoney(player: ObjPtr; dollar, sterling: INT16);
  VAR
   str: ARRAY[0..3] OF CHAR;
   v, d: CARD8;
 BEGIN
  IF gameStat = Playing THEN
   SoundEffect(player, moneyEffect);
   v:= 0;
   IF sterling > 0 THEN v:= sterling ELSIF dollar > 0 THEN v:= dollar END;
   IF v > 0 THEN
    d:= v DIV 10;
    IF d > 0 THEN str[0]:= CHR(d + 48) ELSE str[0]:= " " END;
    str[1]:= CHR(v MOD 10 + 48);
    IF sterling > 0 THEN str[2]:= "£" ELSE str[2]:= "$" END;
    str[3]:= 0C;
    PopMessage(ADR(str), moneyPos, 1)
   END
  END;
  IF dollar > 0 THEN INC(nbDollar, dollar) ELSE DEC(nbDollar, -dollar) END;
  IF sterling > 0 THEN INC(nbSterling, sterling) ELSE DEC(nbSterling, -sterling) END;
  IF nbDollar > 200 THEN
   IF stages = 2 THEN NextStage END;
   AddPt(nbDollar - 200);
   nbDollar:= 200
  END;
  IF nbSterling > 200 THEN
   AddPt((nbSterling - 200) * 3);
   nbSterling:= 200
  END;
  IF dollar <> 0 THEN INCL(gChanges, DOLLAR) END;
  IF sterling <> 0 THEN INCL(gChanges, STERLING) END
 END AddMoney;

 PROCEDURE AddToWeapon(player: ObjPtr; w: Weapon; VAR bullet, bomb: CARD8);
 BEGIN
  IF weaponAttr[w].power = 0 THEN RETURN END;
  IF gameStat = Playing THEN SoundEffect(player, bulletEffect) END;
  WITH weaponAttr[w] DO
   IF 99 - nbBullet >= bullet THEN
    INC(nbBullet, bullet); bullet:= 0
   ELSE
    IF gameStat = Playing THEN
     PopMessage(ADL("Weapon reloaded"), moneyPos, 3)
    END;
    DEC(bullet, 99 - nbBullet); nbBullet:= 99
   END;
   IF 9 - nbBomb >= bomb THEN
    INC(nbBomb, bomb)
   ELSE
    nbBomb:= 9
   END;
   bomb:= 0
  END;
  INCL(wChanges, w)
 END AddToWeapon;

 CONST
  AddMsg = "Choose a weapon";

 VAR
  bulletToAdd, bombToAdd, powerToAdd: CARD8;

 PROCEDURE AddWeapon(player: ObjPtr; bullet: CARD8);
 BEGIN
  StereoEffect;
  SoundEffect(player, cBulletEffectR);
  SoundEffect(player, cBulletEffectL);
  PopMessage(ADL(AddMsg), actionPos, 2);
  IF 255 - bullet > bulletToAdd THEN INC(bulletToAdd, bullet) ELSE bulletToAdd:= 255 END
 END AddWeapon;

 PROCEDURE AddBomb(player: ObjPtr; bomb: CARD8);
 BEGIN
  StereoEffect;
  SoundEffect(player, cBulletEffectR);
  SoundEffect(player, cBulletEffectL);
  PopMessage(ADL(AddMsg), actionPos, 2);
  INC(bombToAdd, bomb)
 END AddBomb;

 PROCEDURE AddPower(player: ObjPtr; power: CARD8);
 BEGIN
  INC(powerToAdd, power);
  SoundEffect(player, cPowerEffect);
  PopMessage(ADL(AddMsg), actionPos, 4)
 END AddPower;

 PROCEDURE MakeInvinsible(player: ObjPtr; time: CARD16);
 BEGIN
  INC(invinsibility, time);
  WITH player^ DO
   hitSubLife:= 0;
   fireSubLife:= 0;
   posY:= mulS * 16
  END
 END MakeInvinsible;

 VAR
  waitPause, pauseRequest: BOOLEAN;

 PROCEDURE Pause;
  VAR
   event: Event;
   redraw: BOOLEAN;
   str: StrPtr;
   c: CARD16;
 BEGIN
  IF NOT(waitPause) THEN
   GetBuffer(waitPause, redraw);
   IF NOT(waitPause) THEN waitPause:= TRUE; RETURN END
  END;
  waitPause:= FALSE;
  pauseRequest:= FALSE;
  HotFlush; redraw:= TRUE;
  c:= 100;
  FlushEvents;
  LOOP
   IF redraw THEN
    str:= ADL("Game paused");
    SetOrigin(0, 0);
    SetCopyMode(cmTrans);
    SetPen(0); CenterText(0, Y(PH DIV 2 + 1), X(PW), str);
    IF color THEN SetPen(6) END;
    CenterText(X(2), Y(PH DIV 2), X(PW), str);
    SetP(5); redraw:= FALSE;
    CenterText(0, Y(PH DIV 2), X(PW), str);
    SetBuffer(TRUE, FALSE);
    SetBusyStat(statWaiting)
   END;
   WHILE (c <> 0) AND (GetStick() * SET16{Joy1..Joy4, JoyPause} <> SET16{})
    DO DEC(c); WaitTOF END;
   WaitEvent;
   GetEvent(event);
   IF (event.type = eKEYBOARD) AND (event.modifiers = ModifierSet{}) THEN
    SendEvent(event); EXIT
   ELSIF event.type <> eNUL THEN
    CommonEvent(event);
    redraw:= TRUE
   END;
   IF (GetStick() * SET16{Joy1..Joy4, JoyPause} <> SET16{}) THEN EXIT END;
   IF gameStat <> Playing THEN EXIT END
  END;
  SetBuffer(TRUE, TRUE);
  HotInit
 END Pause;

 VAR
  prevTime: CARD32;

 PROCEDURE MakePlayer(player: ObjPtr);
 BEGIN
  SetObjLoc(player, 0, 0, 16, 16);
  SetObjRect(player, 2, 2, 14, 14);
  nextGunFireTime:= 0;
  prevTime:= 0;
  mainPlayer:= player
 END MakePlayer;

 PROCEDURE ResetPlayer(player: ObjPtr);
 BEGIN
  bulletToAdd:= 0; bombToAdd:= 0; powerToAdd:= 0;
  WITH player^ DO
   life:= pLife;
   shapeSeq:= 0; stat:= 0;
   hitSubLife:= 1; fireSubLife:= 0;
  END;
  playerPower:= 36;
  MakePlayer(player)
 END ResetPlayer;

 VAR
  aieEffects, hitEffect: ARRAY[0..0] OF Effect;

 PROCEDURE AiePlayer(player, src: ObjPtr; VAR hit, fire: CARD16);
  VAR
   obj, tail, todie: ObjPtr;
   px, py, c: INT16;
   rem, thit, tfire, nd, ns: CARD16;
   a: Anims;
   msg: ARRAY[0..39] OF CHAR;
 BEGIN
  rem:= (hit + fire) DIV 2;
  IF rem >= playerPower THEN
   Boum(player, StoneSet{stC34}, gravityStyle, FlameMult * 6 + 15, 1);
   SoundEffect(player, aieEffects);
   IF zone = Special THEN gameStat:= Finish; RETURN END;
   playerPower:= 0;
   DecLife(player, hit, fire);
   GetCenter(player, px, py);
   FOR a:= ALIEN2 TO MISSILE DO
    obj:= First(animList[a]);
    tail:= Tail(animList[a]);
    WHILE obj <> tail DO
     todie:= obj; obj:= Next(obj^.animNode);
     IF Collision(todie, player) THEN
      WITH todie^ DO thit:= hitSubLife; tfire:= fireSubLife END;
      IF thit > 30 THEN thit:= 30 END;
      IF tfire > 30 THEN tfire:= 30 END;
      Aie(todie, player, thit, tfire)
     END
    END
   END;
   IF color THEN
    SetTrans(0, 255);
    SetRGB(0, 255, 255, 255);
    SetRGB(4, 255, 255, 255)
   END;
   IF pLife > 0 THEN DEC(pLife) END;
   INCL(gChanges, LIFE);
   screenInverted:= Period DIV 2;
   MakeInvinsible(player, Period * 4);
   WITH player^ DO
    life:= pLife; nd:= nbDollar; ns:= nbSterling;
    IF life = 0 THEN SoundEffect(player, life0Effect) END;
    IF (life = 0) AND (nd + ns < 20) THEN
     PopMessage(ADL("Game Over"), lifePos, 5)
    ELSIF life > 0 THEN
     PopMessage(ADL("Ouch !"), lifePos, 3);
     IF life <= 3 THEN
      IF life = 3 THEN
       SoundEffect(player, life3Effect)
      ELSIF life = 2 THEN
       SoundEffect(player, life2Effect)
      ELSE
       SoundEffect(player, life1Effect)
      END;
      CopyStr(ADL("# live(s) left"), ADR(msg), SIZE(msg));
      c:= 0; WHILE (msg[c] <> "#") AND (c < SIZE(msg)) DO INC(c) END;
      IF c < SIZE(msg) THEN msg[c]:= CHR(48 + life) END;
      PopMessage(ADR(msg), statPos, 2)
     END
    END
   END
  ELSE
   PopMessage(ADL("Ow"), lifePos, 2);
   MakeInvinsible(player, Period);
   SoundEffect(player, hitEffect);
   hit:= 0; fire:= 0;
   DEC(playerPower, rem)
  END
 END AiePlayer;

 PROCEDURE CheckSelect(VAR e: Event; VAR stick: SET16; update: BOOLEAN);
  VAR
   c: CARD16;
   oldkey, newkey: CHAR;
   w: Weapon;
   first, off: BOOLEAN;
 BEGIN
  IF weaponSelected THEN
   IF e.type = eKEYBOARD THEN
    IF (e.ch >= "a") AND (e.ch <= "z") THEN
     oldkey:= weaponKey[selectedWeapon];
     newkey:= e.ch;
     weaponKey[selectedWeapon]:= newkey;
     FOR w:= MIN(Weapon) TO MAX(Weapon) DO
      IF (w <> selectedWeapon) AND (weaponKey[w] = newkey) THEN
       weaponKey[w]:= oldkey
      END
     END;
     INCL(wChanges, selectedWeapon);
     weaponSelected:= FALSE;
     e.type:= eNUL
    END
   END;
   IF Joy1 IN stick THEN
    weaponSelected:= FALSE;
    INCL(wChanges, selectedWeapon);
    EXCL(stick, Joy1)
   END;
   FOR c:= Joy2 TO 11 DO
    IF (c IN stick) THEN
     buttonAssign[c]:= selectedWeapon;
     INCL(wChanges, selectedWeapon);
     weaponSelected:= FALSE;
     EXCL(stick, c)
    END
   END
  END;
  IF NOT((JoyForward IN stick) AND (JoyReverse IN stick)) THEN
   IF (JoyForward IN stick) AND NOT(JoyForward IN lastJoy) THEN
    IF weaponSelected THEN
     INCL(wChanges, selectedWeapon);
     IF selectedWeapon = MAX(Weapon) THEN
      selectedWeapon:= MIN(Weapon)
     END;
     INC(selectedWeapon);
     INCL(wChanges, selectedWeapon)
    ELSE
     weaponSelected:= TRUE;
     INCL(wChanges, selectedWeapon)
    END;
    EXCL(stick, JoyForward)
   ELSIF (JoyReverse IN stick) AND NOT(JoyReverse IN lastJoy) THEN
    IF weaponSelected THEN
     INCL(wChanges, selectedWeapon);
     DEC(selectedWeapon);
     IF selectedWeapon = MIN(Weapon) THEN
      selectedWeapon:= MAX(Weapon)
     END;
     INCL(wChanges, selectedWeapon)
    ELSE
     weaponSelected:= TRUE;
     INCL(wChanges, selectedWeapon)
    END;
    EXCL(stick, JoyReverse)
   END
  END;
  IF update AND (wChanges <> WeaponSet{}) THEN
   GetBuffer(first, off);
   SetBuffer(first, TRUE);
   UpdateInfos;
   SetBuffer(first, off)
  END
 END CheckSelect;

 PROCEDURE CheckStick;
  VAR
   nextTime: CARD32;
 BEGIN
  nextTime:= GetTime(time);
  IF (prevTime > nextTime) OR (nextTime - prevTime > Period DIV 40) THEN
   lastJoy:= lastJoy * GetStick();
   prevTime:= nextTime
  END
 END CheckStick;

 VAR
  oldtime: CARD32;

 PROCEDURE MovePlayer0(player: ObjPtr);
  PROCEDURE PressWeapon(w: Weapon);
  BEGIN
   IF (powerToAdd > 0) AND (weaponAttr[w].power < 4) THEN
    WITH weaponAttr[w] DO
     IF 4 - power >= powerToAdd THEN
      INC(power, powerToAdd); powerToAdd:= 0;
      PopMessage(ADL("POWER ADDED"), actionPos, 5)
     ELSE
      DEC(powerToAdd, 4 - power); power:= 4
     END
    END;
    INCL(wChanges, w);
    StereoEffect;
    SoundEffect(player, powerEffectL);
    SoundEffect(player, powerEffectR)
   ELSIF ((bulletToAdd > 0) AND (weaponAttr[w].nbBullet < 99)) OR
         ((bombToAdd > 0) AND (weaponAttr[w].nbBomb < 99)) THEN
    IF bulletToAdd <> 0 THEN
     PopMessage(ADL("Bullet added"), moneyPos, 2)
    END;
    IF bombToAdd <> 0 THEN
     PopMessage(ADL("BOMB added"), lifePos, 2)
    END;
    IF bombToAdd > 0 THEN
     IF (w IN WeaponSet{GUN, BALL, GRENADE}) THEN INC(bombToAdd, 2) END;
     IF (w IN WeaponSet{LASER, BUBBLE, FIRE}) THEN INC(bombToAdd) END
    END;
    bulletToAdd:= bulletToAdd DIV GetBulletPrice(w);
    AddToWeapon(player, w, bulletToAdd, bombToAdd);
    bulletToAdd:= 0
   ELSIF w <> GUN THEN
    INCL(nChanges, w); Fire[w](player)
   END
  END PressWeapon;

  CONST
   SDSpeed = 2100;
   DDSpeed = 1485;
   N  = SET16{};
   U  = SET16{JoyUp};
   UL = SET16{JoyUp, JoyLeft};
   L  = SET16{JoyLeft};
   DL = SET16{JoyLeft, JoyDown};
   D  = SET16{JoyDown};
   DR = SET16{JoyDown, JoyRight};
   R  = SET16{JoyRight};
   UR = SET16{JoyRight, JoyUp};
   BDH = SET16{JoyLeft, JoyRight};
   BDV = SET16{JoyUp, JoyDown};
   DirMask = SET16{JoyUp, JoyDown, JoyLeft, JoyRight};
  VAR
   event: Event;
   joy, dir, tmp: SET16;
   w: Weapon;
   mouseX, mouseY, px, py: INT16;
   oldx, oldy, newx, newy: INT32;
   oldscr: CARD32;
   tohit, tofire, c: CARD16;
   msg: ARRAY[0..7] OF CHAR;
   first, off: BOOLEAN;
 BEGIN
  mainPlayer:= player;
   (* can be changed by BlackHole, but should be right for
      ModulateSounds & co as well as MoveBackground & co *)
  ModulateSounds;
  UpdateInfos;
  WITH player^ DO
 (* Events *)
   joy:= GetStick(); tmp:= joy;
   GetEvent(event);
 (** Directions *)
   dir:= (joy * DirMask);
  (* remove opposites *)
   IF dir * BDH = BDH THEN dir:= dir - BDH END;
   IF dir * BDV = BDV THEN dir:= dir - BDV END;
   IF dir = N THEN dvx:= 0; dvy:= 0
   ELSIF dir = U THEN dvx:= 0; dvy:= -SDSpeed; shapeSeq:= 0
   ELSIF dir = UR THEN dvx:= DDSpeed; dvy:= -DDSpeed; shapeSeq:= 1
   ELSIF dir = R THEN dvx:= SDSpeed; dvy:= 0; shapeSeq:= 2
   ELSIF dir = DR THEN dvx:= DDSpeed; dvy:= DDSpeed; shapeSeq:= 3
   ELSIF dir = D THEN dvx:= 0; dvy:= SDSpeed; shapeSeq:= 4
   ELSIF dir = DL THEN dvx:= -DDSpeed; dvy:= DDSpeed; shapeSeq:= 5
   ELSIF dir = L THEN dvx:= -SDSpeed; dvy:= 0; shapeSeq:= 6
   ELSIF dir = UL THEN dvx:= -DDSpeed; dvy:= -DDSpeed; shapeSeq:= 7
   END;
   IF doubleSpeed > 0 THEN
    dvx:= dvx * 3 DIV 2; dvy:= dvy * 3 DIV 2
   END;
   px:= shapeSeq;
   posX:= W(px * 16);
 (* Move player *)
   oldx:= x; oldy:= y;
   UpdateXY(player);
   IF (oldtime < lasttime) AND (lasttime - oldtime > 60) THEN
    INC(nextGunFireTime, (lasttime - oldtime));
    DEC(nextGunFireTime, step)
   END;
   oldtime:= lasttime;
   AvoidBounds(player, 0);
   AvoidBackground(player, 0);
   IF zone = Castle THEN
    SetMapCoords;
    GetBuffer(first, off);
    IF (oldMpx[first] <> mpx) OR (oldMpy[first] <> mpy) THEN ScrollMap END
   END;
   GetCenter(player, px, py);
   newx:= x; newy:= y;
   x:= oldx; y:= oldy;
   MoveBackground(px, py);
   x:= newx; y:= newy;
  (* Check mouse move *)
   GetMouse(mouseX, mouseY);
   IF waitPause OR pauseRequest OR
     (ABS(lastMouseX - mouseX) + ABS(lastMouseY - mouseY) > 6) THEN
    Pause
   END;
   CheckSelect(event, joy, FALSE);
 (** Keys *)
   IF (event.type = eKEYBOARD) AND (event.ch <> 0C) THEN
    IF (event.ch = "p") OR (event.ch = "P") THEN
     pauseRequest:= TRUE
    ELSIF (event.ch = "H") AND password THEN HALT
    ELSIF (event.ch = "$") AND password THEN
     nbDollar:= 199; nbSterling:= 199;
     INCL(gChanges, DOLLAR); INCL(gChanges, STERLING)
    ELSIF (event.ch = ".") AND password THEN gameStat:= Finish
    ELSIF (event.ch >= "a") AND (event.ch <= "z") THEN
     FOR w:= MIN(Weapon) TO MAX(Weapon) DO
      IF weaponKey[w] = event.ch THEN
       IF (event.modifiers <> ModifierSet{}) OR (JoyShift IN joy) OR (bombActive) THEN
        INCL(wChanges, w); Bomb[w](player); bombActive:= FALSE
       ELSE
        PressWeapon(w)
       END
      END
     END
    ELSIF (event.ch >= "A") AND (event.ch <= "Z") THEN
     FOR w:= MIN(Weapon) TO MAX(Weapon) DO
      IF weaponKey[w] = CHR(ORD(event.ch) - ORD("A") + ORD("a")) THEN
       INCL(wChanges, w); Bomb[w](player); bombActive:= FALSE
      END
     END
    END
   ELSE
    CommonEvent(event)
   END;
 (** Fire buttons *)
   IF Joy1 IN joy THEN
    IF (JoyShift IN joy) OR (bombActive) THEN
     IF lasttime > nextGunFireTime THEN
      INCL(wChanges, GUN); Bomb[GUN](player); bombActive:= FALSE;
      nextGunFireTime:= lasttime; INC(nextGunFireTime, GunFireSpeed)
     END
    ELSIF (powerToAdd > 0) AND (weaponAttr[GUN].power < 4) THEN
     PressWeapon(GUN)
    ELSE
     gunFiring:= TRUE
    END
   ELSE
    gunFiring:= FALSE
   END;
   FOR c:= Joy2 TO 11 DO
    IF (c IN joy) AND NOT(c IN lastJoy) THEN
     IF (JoyShift IN joy) OR (bombActive) THEN
      INCL(wChanges, buttonAssign[c]); Bomb[buttonAssign[c]](player);
      bombActive:= FALSE
     ELSE
      PressWeapon(buttonAssign[c])
     END
    END
   END;
   IF (JoyPause IN joy) AND NOT(JoyPause IN lastJoy) THEN
    pauseRequest:= TRUE
   END;
 (** Weapon selection *)
   IF (JoyForward IN joy) AND (JoyReverse IN joy) AND
      (NOT(JoyForward IN lastJoy) OR NOT(JoyReverse IN lastJoy)) THEN
    IF NOT(bombActive) THEN
     PopMessage(ADL("bomb actived"), actionPos, 1)
    ELSE
     PopMessage(ADL("bomb unactived"), actionPos, 1)
    END;
    weaponSelected:= FALSE;
    INCL(wChanges, selectedWeapon);
    bombActive:= NOT(bombActive)
   END;
   lastJoy:= tmp;
 (* left objs ? *)
   CheckLeftObjs;
 (* Gun autofire *)
   IF gunFiring THEN
    IF lasttime >= nextGunFireTime THEN
     Fire[GUN](player);
     IF nextGunFireTime + Period DIV 5 < lasttime THEN
      nextGunFireTime:= lasttime
     END;
     INC(nextGunFireTime, GunFireSpeed)
    END
   END;
 (* Palette cycling *)
   AnimPalette(step);
 (* AddPt *)
   IF (addpt > 0) AND (step < 60) THEN
    oldscr:= score;
    WHILE addpt > 0 DO
     DEC(addpt); INC(score, difficulty)
    END;
    IF ((score >= 100000) AND (oldscr < 100000)) OR
       ((score DIV 500000) <> (oldscr DIV 500000)) THEN
     FOR w:= MIN(Weapon) TO MAX(Weapon) DO
      WITH weaponAttr[w] DO
       IF power > 0 THEN
        INCL(wChanges, w);
        IF nbBomb < 8 THEN INC(nbBomb, 2) ELSE nbBomb:= 9 END
       END
      END
     END;
     SoundEffect(player, mioEffect);
     PopMessage(ADL("Mega score bonus"), lifePos, 7)
    END;
    IF NOT(registered) AND ((score >= 1000) OR (level[Castle] >= 8)) THEN
     Check(TRUE, ADL("Please pay the shareware"), ADL("if you want to play more"))
    END;
    IF (stages = 5) AND (score >= 1000) THEN
     NextStage
    END;
    INCL(gChanges, SCORE)
   END;
 (* magnet *)
   IF step >= magnet THEN
    magnet:= 0
   ELSE
    Gravity(player, AnimSet{ALIEN2, ALIEN1, MISSILE, BONUS, STONE});
    DEC(magnet, step)
   END;
 (* freeFire *)
   IF step > freeFire THEN freeFire:= 0 ELSE DEC(freeFire, step) END;
 (* maxPower *)
   IF step > maxPower THEN maxPower:= 0 ELSE DEC(maxPower, step) END;
 (* noMissile *)
   IF step > noMissile THEN noMissile:= 0 ELSE DEC(noMissile, step) END;
 (* sleeper *)
   IF step > sleeper THEN sleeper:= 0 ELSE DEC(sleeper, step) END;
 (* air *)
   IF water THEN
    IF air = 0 THEN
     tohit:= playerPower; tofire:= tohit;
     AiePlayer(player, NIL, tohit, tofire);
     PopMessage(ADL("() No more air ()"), lifePos, 4);
     air:= Period * 60
    ELSIF step > air THEN
     air:= 0
    ELSE
     DEC(air, step);
     c:= air DIV (Period * 3);
     IF c <> (dAir DIV (Period * 3)) THEN
      IF c <= 5 THEN
       msg:= "() # ()";
       msg[3]:= CHR(48 + c); msg[7]:= 0C;
       PopMessage(ADR(msg), lifePos, 1)
      ELSIF c = 20 THEN
       PopMessage(ADL("() 60 ()"), lifePos, 2)
      ELSIF c = 10 THEN
       PopMessage(ADL("() 30 ()"), lifePos, 2)
      END
     END
    END
   END;
 (* invinsibility *)
   IF invinsibility > 0 THEN
    IF step >= invinsibility THEN invinsibility:= 0 ELSE DEC(invinsibility, step) END;
    IF invinsibility = 0 THEN
     IF playerPower = 0 THEN playerPower:= 36 END;
     IF life > 0 THEN
      hitSubLife:= 1; posY:= 0
     ELSIF password THEN
      hitSubLife:= 1; life:= 10; pLife:= 10; posY:= 0;
      SoundEffect(player, dieEffects);
      INCL(gChanges, LIFE)
     ELSIF (nbDollar > 20) OR (nbDollar + nbSterling >= 20) THEN
      hitSubLife:= 1; INC(life); INC(pLife); posY:= 0;
      PopMessage(ADL("$AVED BY THE CA$H"), lifePos, 5);
      SoundEffect(player, savedEffect);
      IF nbDollar >= 20 THEN
       DEC(nbDollar, 20)
      ELSE
       DEC(nbSterling, 20 - nbDollar);
       nbDollar:= 0
      END;
      gChanges:= gChanges + InfoSet{LIFE, DOLLAR, STERLING}
     ELSE
      Die(player); RETURN
     END;
    END
   END;
  (* double speed *)
   (* Only changed by the single-speed bonus *)
  (* screenInverted *)
   IF screenInverted > 0 THEN
    IF step >= screenInverted THEN
     screenInverted:= 0;
     IF color THEN
      SetTrans(0, 0);
      SetRGB(0, 0, 0, 0);
      SetRGB(4, 255, 0, 0)
     END
    ELSE
     DEC(screenInverted, step)
    END
   END
  END
 END MovePlayer0;

 PROCEDURE DiePlayer(player: ObjPtr);
 BEGIN
  SoundEffect(player, dieEffects);
  gameStat:= Gameover
 END DiePlayer;

 PROCEDURE InitParams;
  VAR
   attr: ObjAttrPtr;
   c, v, f: CARD16;
 BEGIN
  SetEffect(lifeEffectL[0], soundList[wVoice], 392, 6272, 180, 12);
  SetEffect(lifeEffectL[1], soundList[wVoice], 523, 8363, 180, 12);
  SetEffect(lifeEffectL[2], soundList[wVoice], 698, 11163, 180, 12);
  SetEffect(lifeEffectL[3], soundList[wVoice], 587, 9387, 180, 12);
  SetEffect(lifeEffectL[4], soundList[wVoice], 784, 12544, 180, 12);
  SetEffect(lifeEffectL[5], soundList[wVoice], 1045, 16726, 180, 12);
  SetEffect(lifeEffectL[6], soundList[wVoice], 1395, 22327, 180, 12);
  SetEffect(lifeEffectL[7], soundList[wVoice], 1173, 18774, 180, 12);
  FOR c:= 8 TO 20 BY 4 DO
   v:= (21 - c) * 11;
   SetEffect(lifeEffectL[c], soundList[wVoice], 392, 12544, v, 12);
   SetEffect(lifeEffectL[c + 1], soundList[wVoice], 587, 18774, v, 12);
   SetEffect(lifeEffectL[c + 2], soundList[wVoice], 739, 23654, v, 12);
   SetEffect(lifeEffectL[c + 3], soundList[wVoice], 2352, 25089, v, 12)
  END;
  FOR c:= 0 TO 23 DO
   lifeEffectR[c]:= lifeEffectL[c]
  END;
  FOR c:= 8 TO 23 DO
   IF (c DIV 4) MOD 2 = 0 THEN
    lifeEffectL[c].volume:= lifeEffectL[c].volume DIV 4
   ELSE
    lifeEffectR[c].volume:= lifeEffectR[c].volume DIV 4
   END
  END;
  SetEffect(lifeEffectR[7], soundList[wVoice], 1467, 18774, 180, 12);
  SetEffect(life0Effect[0], soundList[sPoubelle], 0, 4181, 0, 9);
  SetEffect(life0Effect[1], soundList[sPoubelle], 0, 4181, 160, 9);
  SetEffect(life1Effect[0], soundList[wJans], 0, 8363, 120, 9);
  SetEffect(life1Effect[1], soundList[wVoice], 1394, 12544, 40, 9);
  SetEffect(life1Effect[2], soundList[wVoice], 3485, 10454, 40, 9);
  SetEffect(life1Effect[3], soundList[wVoice], 1043, 9387, 120, 9);
  SetEffect(life1Effect[4], soundList[wVoice], 3717, 11151, 60, 9);
  SetEffect(life1Effect[5], soundList[wJans], 1394, 8363, 120, 9);
  SetEffect(life1Effect[6], soundList[wJans], 1742, 10454, 130, 9);
  SetEffect(life1Effect[7], soundList[wJans], 2060, 12545, 140, 9);
  SetEffect(life1Effect[8], soundList[wJans], 0, 14868, 150, 9);
  FOR c:= 0 TO 2 DO life3Effect[c]:= life1Effect[c] END;
  FOR c:= 0 TO 4 DO life2Effect[c]:= life1Effect[c] END;
  SetEffect(aieEffects[0], soundList[sVerre], 0, 0, 255, 14);
  SetEffect(hitEffect[0], soundList[sPoubelle], 0, 12544, 210, 10);
  SetEffect(dieEffects[0], soundList[sGong], 0, 0, 255, 14);
  SetEffect(moneyEffect[0], soundList[sMoney], 0, 0, 70, 4);
  SetEffect(bulletEffect[0], soundList[sCaisse], 0, 16726, 110, 6);
  SetEffect(cPowerEffect[0], soundList[sCannon], 0, 16726, 180, 12);
  SetEffect(savedEffect[0], soundList[sCaisse], 0, 0, 150, 8);
  SetEffect(cBulletEffectL[0], soundList[sHHat], 1566, 12530, 90, 5);
  SetEffect(cBulletEffectL[1], soundList[sHHat], 1566, 12530, 80, 5);
  SetEffect(cBulletEffectL[2], soundList[sHHat], 1566, 12530, 70, 5);
  SetEffect(cBulletEffectL[3], soundList[sHHat], 1243, 9945, 60, 5);
  SetEffect(cBulletEffectL[4], soundList[sHHat], 1395, 11163, 70, 5);
  SetEffect(cBulletEffectL[5], soundList[sHHat], 1395, 11163, 80, 5);
  SetEffect(cBulletEffectL[6], soundList[sHHat], 0, 11163, 90, 5);
  SetEffect(cBulletEffectR[0], soundList[sHHat], 1243, 9945, 80, 6);
  SetEffect(cBulletEffectR[1], soundList[sHHat], 1243, 9945, 80, 6);
  SetEffect(cBulletEffectR[2], soundList[sHHat], 1566, 12530, 80, 6);
  SetEffect(cBulletEffectR[3], soundList[sHHat], 1566, 12530, 80, 6);
  SetEffect(cBulletEffectR[4], soundList[sHHat], 1243, 9945, 80, 6);
  SetEffect(cBulletEffectR[5], soundList[sHHat], 1243, 9945, 80, 6);
  SetEffect(cBulletEffectR[6], soundList[sHHat], 0, 11163, 80, 6);
  FOR c:= 0 TO 8 BY 4 DO
   v:= (10 - c) * 24; IF c = 0 THEN f:= 1 ELSE f:= 2 END;
   SetEffect(powerEffectL[c], nulSound, 1045 * f, 8363 * f, v, 15 - c);
   SetEffect(powerEffectL[c + 1], nulSound, 1395 * f, 11163 * f, v, 15 - c);
   SetEffect(powerEffectL[c + 2], nulSound, 1173 * f, 9387 * f, v, 15 - c);
   SetEffect(powerEffectL[c + 3], nulSound, 1566 * f, 12530 * f, v, 15 - c)
  END;
  SetEffect(powerEffectL[0], soundList[wShakuhachi], 1045, 8363, 240, 15);
  SetEffect(powerEffectR[0], soundList[wShakuhachi], 697, 16726, 0, 15);
  FOR c:= 1 TO 12 DO
   powerEffectR[c]:= powerEffectL[c - 1]
  END;
  SetEffect(mioEffect[0], soundList[sCaisse], 2088, 6265, 180, 15);
  SetEffect(mioEffect[1], soundList[sCaisse], 2631, 7894, 180, 15);
  SetEffect(mioEffect[2], soundList[sCaisse], 2344, 7032, 180, 15);
  SetEffect(mioEffect[3], soundList[sCaisse], 1971, 5913, 180, 15);
  SetEffect(mioEffect[4], soundList[sCaisse], 0, 6265, 120, 15);
  selectedWeapon:= GUN; INC(selectedWeapon);
  weaponSelected:= FALSE;
  FOR c:= 0 TO 15 DO buttonAssign[c]:= GUN END;
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetPlayer;
   Make:= MakePlayer;
   Move:= MovePlayer0;
   Aie:= AiePlayer;
   Die:= DiePlayer;
   inerty:= 128;
   weight:= 48; charge:= 40;
   coolSpeed:= 10;
   aieStKinds:= StoneSet{stC34}; aieSKCount:= 1;
   aieStone:= 16; aieStStyle:= gravityStyle;
   dieStKinds:= StoneSet{stC26..stFLAME2};
   dieSKCount:= 12; dieStStyle:= fastStyle;
   dieStone:= MAX(CARD8);
   basicType:= NotBase;
(* priority:= 0; *)
  END;
  AddHead(attrList[PLAYER], attr^.node)
 END InitParams;

BEGIN

 bulletToAdd:= 0; powerToAdd:= 0;
 oldtime:= 0;
 waitPause:= FALSE;
 pauseRequest:= FALSE;
 InitParams;

END ChaosPlayer.
