IMPLEMENTATION MODULE ChaosScreens;

 FROM SYSTEM IMPORT ADR;
 FROM Memory IMPORT SET16, CARD8, INT8, CARD16, INT16, CARD32, INT32,
  TAG7, StrPtr, CopyStr, ADS;
 FROM Checks IMPORT Warn, Terminate;
 FROM Trigo IMPORT RND;
 FROM Languages IMPORT ADL;
 FROM Input IMPORT eKEYBOARD, Joy1, Joy2, Joy3, Joy4, JoyPause, statWaiting,
  statReady, statBusy, Event, WaitEvent, GetEvent, FlushEvents, SetBusyStat,
  GetStick, JoyLeft, JoyRight, JoyUp, JoyDown, JoyForward, JoyReverse,
  AddEvents, RemEvents, EventTypes, EventTypeSet, eMENU, eNUL, eTIMER,
  BeginRefresh, EndRefresh;
 FROM Clock IMPORT GetCurrentTime, StartTime, TimeEvent, GetNewSeed;
 FROM Graphics IMPORT CopyMode, cmCopy, cmTrans, cmXor, SetCopyMode, SetArea,
  UpdateArea, SetBuffer, AreaToFront, WaitTOF, TextModes, TextModeSet,
  SetTextMode, SetTextSize, SetTextPos, TextWidth, DrawText, SetPen, FillRect,
  SetPalette, SwitchArea;
 FROM Registration IMPORT userName, userAddress, userLoc, registered;
 FROM ChaosSounds IMPORT SoundList, soundList, SimpleSound;
 FROM ChaosBase IMPORT Weapon, specialStage, nbDollar, nbSterling, Zone,
  zone, level, Statistic, basics, shoot, GameStat, gameStat, WeaponSet,
  WeaponAttr, weaponAttr, mainPlayer, BasicTypes, water, Period, lastJoy,
  difficulty, score, password, pLife, stages, gameSeed;
 FROM ChaosGraphics IMPORT PW, PH, mainArea, shapeArea, maskArea, mulS,
  color, WriteAt, CenterText, WriteCard, SetOrigin, X, Y, W, H, IW, IH,
  SetRGB, UpdatePalette, UpdateAnim, SW, SH, buffArea, NbClear, castle,
  castleWidth, castleHeight, palette, SetTrans;
 FROM ChaosImages IMPORT InitPalette;
 FROM ChaosInterface IMPORT Refresh, CommonEvent, WarmInit, EnableFileMenus,
  DisableFileMenus, TopScore, TopScoreList, ReadTopScoreList,
  WriteTopScoreList;
 FROM ChaosActions IMPORT FadeIn, FadeOut, FadeFrom, WhiteFade, ZoomMessage,
  time;
 FROM ChaosPlayer IMPORT UpdateInfos, DrawInfos, AddLife, AddMoney,
  AddToWeapon, WeaponToStr, CheckSelect;
 FROM ChaosWeapon IMPORT GetBulletPrice;
 FROM ChaosLevels IMPORT MakeCastle;


 PROCEDURE SetP(p: CARD8);
 BEGIN
  IF color THEN
   SetPen(p)
  ELSIF p > 0 THEN
   SetPen(1)
  ELSE
   SetPen(0)
  END
 END SetP;

 VAR
  p1, p2, p3: CARD8;

 PROCEDURE TripleWrite(x, y: INT16; s: StrPtr);
 BEGIN
  SetP(p3); WriteAt(X(x + 1), Y(y + 1), s);
  SetP(p2); WriteAt(X(x), Y(y), s);
  IF color THEN SetPen(p1) ELSIF (p1 = 1) OR ((p1 > 1) AND (gameStat = Gameover)) THEN SetPen(1) ELSE SetPen(0) END;
  WriteAt(X(x + 1), Y(y), s)
 END TripleWrite;

 PROCEDURE TripleCard(x, y: INT16; val: CARD32);
 BEGIN
  SetP(p3); WriteCard(X(x + 1), Y(y + 1), val);
  SetP(p2); WriteCard(X(x), Y(y), val);
  IF color THEN SetPen(p1) ELSE SetPen(0) END;
  WriteCard(X(x + 1), Y(y), val)
 END TripleCard;

 PROCEDURE Center(x, y, w: INT16; s: StrPtr);
  VAR
   d: INT16;
 BEGIN
  d:= (W(w) - TextWidth(s)) DIV 2;
  TripleWrite(x + d DIV mulS, y, s)
 END Center;

 PROCEDURE TripleCenter(w, y: INT16; s: StrPtr);
 BEGIN
  Center(0, y, w, s)
 END TripleCenter;

 PROCEDURE ResetGraphics;
 BEGIN
  SetArea(mainArea);
  SetBuffer(TRUE, TRUE);
  SetOrigin(0, 0);
  SetPen(0); SetCopyMode(cmTrans);
  SetTextMode(TextModeSet{})
 END ResetGraphics;

 PROCEDURE UpdateScreen;
 BEGIN
  SetArea(mainArea);
  SetBuffer(TRUE, FALSE);
  UpdateArea
 END UpdateScreen;

 PROCEDURE WaitRelease;
  VAR
   cnt: CARD16;
 BEGIN
  cnt:= 0;
  WHILE (GetStick() <> SET16{}) AND (cnt < 200) DO
   WaitTOF; INC(cnt)
  END;
  FlushEvents
 END WaitRelease;

 VAR
  joy2pressed: BOOLEAN;

 PROCEDURE WaitStart;
  CONST
   Password = "Nightmare";
  VAR
   buffer: ARRAY[0..9] OF CHAR;
   event: Event;
   oldstat: GameStat;
   stick, tmp: SET16;
   ppos: CARD16;
 BEGIN
  buffer:= Password; ppos:= 0;
  UpdateScreen;
  AreaToFront;
  oldstat:= gameStat;
  WaitRelease;
  LOOP
   IF gameStat <> oldstat THEN EXIT END;
   SetBusyStat(statWaiting);
   WaitEvent;
   stick:= GetStick(); tmp:= stick;
   GetEvent(event);
   IF (event.type = eKEYBOARD) AND (event.ch <> 0C) THEN
    IF event.ch = buffer[ppos] THEN
     IF ppos = 8 THEN
      password:= TRUE; ppos:= 0;
      SimpleSound(soundList[sGong])
     ELSE
      INC(ppos)
     END
    ELSE
     ppos:= 0
    END
   END;
   IF gameStat <> Start THEN CheckSelect(event, stick, TRUE) END;
   joy2pressed:= (Joy2 IN stick);
   IF stick * SET16{Joy1..Joy4, JoyPause} <> SET16{} THEN EXIT END;
   CommonEvent(event);
   lastJoy:= tmp;
   UpdateScreen
  END
 END WaitStart;

 PROCEDURE DrawStart;
 BEGIN
  ResetGraphics;
  SetTextSize(H(9));
  p1:= 2;
  TripleCenter(PW, 230, ADL("Press [SPACE] to start"));
  UpdateScreen
 END DrawStart;

 PROCEDURE TitleScreen;
 BEGIN
  WarmInit;
  DisableFileMenus;
  InitPalette;
  ZoomMessage(ADS("ChaosCastle"), 7, 3, 4);
  SetBuffer(TRUE, FALSE);
  SetPen(0);
  FillRect(0, 0, W(SW), H(SH))
 END TitleScreen;

 PROCEDURE DrawStartScreen;
 BEGIN
  ResetGraphics;
  IF color THEN
   SetRGB(8, 0, 0, 0);
   SetPalette(8, 0, 0, 0);
   SetPen(8)
  END;
  FillRect(0, 0, W(SW), H(SH));
  SetTextSize(H(27));
  p1:= 7; p2:= 3; p3:= 4;
  TripleCenter(SW, 0, ADS("Chaos Castle"));
  SetTextSize(H(9));
  TripleCenter(SW, 27, ADS("(C) 1999 by Nicky"));
  IF registered THEN
   TripleCenter(SW, 40, ADL("Full Version"));
   TripleCenter(SW, 49, ADL("FreeWare"))
  ELSE
   TripleCenter(SW, 40, ADL("Demo version - limited playtime"));
   TripleCenter(SW, 49, ADL("Register to get a full version"))
  END;
  TripleCenter(SW, 63, ADL("Use the following keys to move:"));
  TripleCenter(SW, 72, ADL("[7] [8] [9]"));
  TripleCenter(SW, 81, ADL("[4] [5] [6]"));
  TripleCenter(SW, 90, ADL("[1] [2] [3]"));
  TripleCenter(SW, 100, ADL("Use [SPACE] to fire with the Gun."));
  TripleCenter(SW, 110, ADL("To use another weapon, you must first"));
  TripleCenter(SW, 119, ADL("choose yourself the key to use as follow"));
  TripleCenter(SW, 128, ADL("-Select the weapon with [+] / [-]"));
  TripleCenter(SW, 137, ADL("-Choose the key ([F1]..[F8] or [a]..[z])"));
  TripleCenter(SW, 146, ADL("to use or press [SPACE] to cancel."));
  TripleCenter(SW, 156, ADL("To make a bomb with any weapon, hold"));
  TripleCenter(SW, 165, ADL("[SHIFT] while firing or press & release"));
  TripleCenter(SW, 174, ADL("[+] & [-] simultaneously before you fire"));
  TripleCenter(SW, 184, ADL("Warning: bombs and bullets are limited."));
  TripleCenter(SW, 195, ADL("If you have a joystick / joypad,"));
  TripleCenter(SW, 204, ADL("[SPACE], [F1]..[F3] = button 1 to 4,"));
  TripleCenter(SW, 213, ADL("[+], [-] = forward, reverse."));
  WhiteFade;
  DrawStart;
  UpdateScreen
 END DrawStartScreen;

 PROCEDURE RefreshPlay;
 BEGIN
  SetArea(mainArea);
  SetBuffer(TRUE, TRUE);
  DrawInfos(TRUE);
  UpdateAnim;
  UpdateArea;
  SetBuffer(TRUE, FALSE)
 END RefreshPlay;

 VAR
  levelName: ARRAY[0..19] OF CHAR;

 PROCEDURE GetChaosName(level: CARD8): StrPtr;
  VAR
   d: CARD8;
 BEGIN
  d:= level DIV 10;
  IF d = 0 THEN
   levelName[0]:= CHR(48 + level);
   levelName[1]:= 0C
  ELSE
   levelName[0]:= CHR(48 + d MOD 10);
   levelName[1]:= CHR(48 + level MOD 10);
   levelName[2]:= 0C
  END;
  RETURN ADR(levelName)
 END GetChaosName;

 PROCEDURE GetCastleName(level: CARD8): StrPtr;
 BEGIN
  CASE level OF
    1: RETURN ADL("Entry")
   |2: RETURN ADL("Groove")
   |3: RETURN ADL("Garden")
   |4: RETURN ADL("Lake")
   |5: RETURN ADL("Site")
   |6: RETURN ADL("GhostCastle")
   |7: RETURN ADL("Machinery")
   |8: RETURN ADL("Ice Rink")
   |9: RETURN ADL("Factory")
   |10: RETURN ADL("Labyrinth")
   |11: RETURN ADL("Rooms")
   |12: RETURN ADL("Yard")
   |13: RETURN ADL("Antarctica")
   |14: RETURN ADL("Forest")
   |15: RETURN ADL(" Castle ")
   |16: RETURN ADL("Lights")
   |17: RETURN ADL("Plain")
   |18: RETURN ADL("Underwater")
   |19: RETURN ADL("Assembly")
   |20: RETURN ADL("Jungle")
  END
 END GetCastleName;

 PROCEDURE GetSpecialName(level: CARD8): StrPtr;
 BEGIN
  IF level = 24 THEN
   RETURN ADL("ChaosCastle")
  ELSIF level MOD 8 = 0 THEN
   RETURN ADL("Winter")
  ELSIF level MOD 4 = 0 THEN
   RETURN ADL("Graveyard")
  ELSIF level MOD 2 = 0 THEN
   RETURN ADL("Autumn")
  ELSE
   RETURN ADL("Baby Aliens")
  END
 END GetSpecialName;

 PROCEDURE GetFamilyName(level: CARD8): StrPtr;
 BEGIN
  CASE level OF
    1: RETURN ADL("Brother Alien")
   |2: RETURN ADL("Sister Alien")
   |3: RETURN ADL("Mother Alien")
   |4: RETURN ADL("FATHER ALIEN")
   |5: RETURN ADL("KIDS")
   |6: RETURN ADL("PARENTS")
   |7: RETURN ADL("MASTER ALIEN")
   |8: RETURN ADL("MASTER ALIEN 2")
   |9: RETURN ADL("MASTERS")
   |10: RETURN ADL("* FINAL *")
  END
 END GetFamilyName;

 VAR
  gameMade: BOOLEAN;

 PROCEDURE DrawMakingScreen;
  VAR
   str, st2: StrPtr;
 BEGIN
  ResetGraphics;
  FillRect(0, 0, W(SW), H(SH));
  SetTextSize(H(27));
  p1:= 7; p2:= 3; p3:= 4;
  TripleCenter(PW, 0, ADL("Zone:"));
  CASE zone OF
    Chaos: str:= ADL("Chaos")
   |Castle: str:= ADL("Castle")
   |Family: str:= ADL("Family")
   |Special: str:= ADL("* BONUS *")
  END;
  TripleCenter(PW, 30, str);
  TripleCenter(PW, 120, ADL("Level:"));
  IF zone = Family THEN
   SetTextSize(H(18));
   str:= GetFamilyName(level[Family])
  ELSIF zone = Castle THEN
   str:= GetCastleName(level[Castle])
  ELSIF zone = Special THEN
   SetTextSize(H(18));
   str:= GetSpecialName(level[Special])
  ELSE
   str:= GetChaosName(level[zone])
  END;
  TripleCenter(PW, 150, str);
  SetTextSize(H(9));
  str:= ADL("Target:");
  TripleCenter(PW, 60, str);
  TripleCenter(PW, 180, str);
  CASE zone OF
    Chaos: str:= ADL("Get enough $ to enter"); st2:= ADL("Castle zone")
   |Castle: str:= ADL("Get enough £ to enter"); st2:= ADL("Family zone")
   |Family: str:= ADL(""); st2:= ADL("")
   |Special: str:= ADL("Get bonuses"); st2:= ADS("  ")
  END;
  TripleCenter(PW, 70, str);
  TripleCenter(PW, 80, st2);
  CASE zone OF
    Chaos: str:= ADL("Destroy everything")
   |Castle, Special: str:= ADL("Find the exit")
   |Family:
    IF level[Family] > 6 THEN
     str:= ADL("Shoot them up")
    ELSE
     str:= ADL("Destroy it")
    END
  END;
  TripleCenter(PW, 190, str);
  DrawInfos(FALSE);
  IF gameMade THEN DrawStart END;
  UpdateScreen
 END DrawMakingScreen;

 PROCEDURE MakingScreen;
 BEGIN
  IF (score > 2000) AND NOT(registered) THEN Terminate END;
  gameMade:= FALSE;
  FadeOut;
  DrawMakingScreen;
  FadeIn;
  Refresh:= DrawMakingScreen;
  MakeCastle;
  gameMade:= TRUE;
  gameStat:= Playing;
  DrawMakingScreen;
  EnableFileMenus;
  WaitStart;
  SetPen(0);
  Refresh:= RefreshPlay;
  IF gameStat <> Playing THEN RETURN END;
  SetBuffer(TRUE, TRUE);
  DrawInfos(TRUE);
  UpdateAnim
 END MakingScreen;

 PROCEDURE WeaponToPrice(w: Weapon; VAR bullet, bombd, bombs: CARD8);
 BEGIN
  bullet:= GetBulletPrice(w);
  bombs:= 0;
  CASE w OF
    GUN:  bombd:= 5
   |FB: bombd:= 0; bombs:= 90
   |LASER: bombd:= 40
   |BUBBLE: bombd:= 30
   |FIRE: bombd:= 25
   |STAR: bombd:= 60
   |BALL: bombd:= 20
   |GRENADE: bombd:= 10
  END
 END WeaponToPrice;

 PROCEDURE ItemToPrice(item: CARD16; VAR itemD, itemS: CARD8): StrPtr;
 BEGIN
  itemD:= 0; itemS:= 0;
  CASE item OF
    0: itemD:= 15; RETURN ADS("15$")
   |2: itemS:= 150; RETURN ADS("150£")
   |3: itemD:= 100; RETURN ADS("100$")
   ELSE RETURN NIL
  END
 END ItemToPrice;

 CONST
  x1 = PW DIV 2;
  x2 = PW * 3 DIV 4;
 VAR
  wActive: ARRAY[MIN(Weapon)..MAX(Weapon)],[FALSE..TRUE] OF BOOLEAN;
  available: ARRAY[MIN(Weapon)..MAX(Weapon)] OF CARD8;
  dActive: ARRAY[0..4] OF BOOLEAN;
  bomb, up: BOOLEAN;
  yw: Weapon;
  yd: CARD16;

 PROCEDURE DrawWeapon(w: Weapon);
  VAR
   y: INT16;
   str: StrPtr;
   bullet, bombd, bombs, val: CARD8;
   ch: ARRAY[0..3] OF CHAR;
   weaponOk: BOOLEAN;

  PROCEDURE SetPens(xc, av: BOOLEAN; d, s: CARD8): BOOLEAN;
   VAR
    tst1, tst2: BOOLEAN;
  BEGIN
   tst1:= (nbDollar >= d) AND (nbSterling >= s) AND av AND (available[w] > 0) AND (weaponAttr[w].power > 0);
   tst2:= up AND (w = yw) AND xc;
   IF color THEN
    IF tst1 THEN
     IF tst2 THEN p1:= 2 ELSE p1:= 7 END;
     weaponOk:= TRUE
    ELSE
     IF tst2 THEN p1:= 1 ELSE p1:= 0 END
    END
   ELSE
    p2:= 1;
    IF tst1 THEN p3:= 1; weaponOk:= TRUE ELSE p3:= 0 END;
    IF tst2 THEN p1:= 1 ELSE p1:= 0 END
   END;
   RETURN tst1;
  END SetPens;
 BEGIN
  y:= ORD(w) * 10 + 60;
  str:= WeaponToStr(w);
  WeaponToPrice(w, bullet, bombd, bombs);
  weaponOk:= FALSE;
  IF available[w] > 0 THEN
   ch[0]:= CHR(48 + bullet);
   ch[1]:= "$"; ch[2]:= 0C
  ELSE
   ch:= "NA"; SetPen(0);
   FillRect(W(x1), H(y), W(PW), H(y + 10))
  END;
  wActive[w][FALSE]:= SetPens(NOT bomb, TRUE, bullet, 0);
  Center(x1, y, PW DIV 4, ADR(ch));
  val:= bombd;
  IF available[w] > 0 THEN
   IF bombs <> 0 THEN ch[2]:= "£"; val:= bombs ELSE ch[2]:= "$" END;
   ch[0]:= CHR(48 + val DIV 10);
   ch[1]:= CHR(48 + val MOD 10);
   ch[3]:= 0C
  ELSE
   ch:= "---"
  END;
  wActive[w][TRUE]:= SetPens(bomb, TRUE, bombd, bombs);
  Center(x2, y, PW DIV 4, ADR(ch));
  weaponOk:= SetPens(TRUE, weaponOk, 0, 0);
  TripleWrite(0, y, str)
 END DrawWeapon;

 PROCEDURE UpdateWeapon(w: Weapon);
  VAR
   bullet, bombd, bombs: CARD8;
   t1, t2, t3: BOOLEAN;
 BEGIN
  WeaponToPrice(w, bullet, bombd, bombs);
  t1:= (nbDollar >= bullet); t2:= (nbDollar >= bombd);
  t3:= (nbSterling >= bombs); t3:= t2 AND t3;
  IF (wActive[w][FALSE] <> t1) OR (available[w] = 0) OR
     (wActive[w][TRUE] <> t3) THEN
   DrawWeapon(w)
  END
 END UpdateWeapon;

 PROCEDURE BuyWeapon(ten: BOOLEAN);
  VAR
   bullet, bombd, bombs: CARD8;
   bS, bD, bl: INT8;
   addBullet, addBomb: CARD8;
 BEGIN
  IF NOT(wActive[yw][bomb]) THEN
   IF available[yw] > 0 THEN
    SimpleSound(soundList[sPoubelle])
   ELSE
    SimpleSound(soundList[sCymbale])
   END;
   RETURN
  END;
  WeaponToPrice(yw, bullet, bombd, bombs);
  DEC(available[yw]);
  bl:= bullet; bS:= bombs; bD:= bombd;
  IF bomb THEN
   addBullet:= 0; addBomb:= 1;
   AddToWeapon(mainPlayer, yw, addBullet, addBomb);
   IF addBomb = 0 THEN
    SimpleSound(soundList[sMoney]);
    AddMoney(mainPlayer, -bD, -bS)
   ELSE
    SimpleSound(soundList[sHurryUp])
   END
  ELSE
   addBullet:= 1; addBomb:= 0;
   AddToWeapon(mainPlayer, yw, addBullet, addBomb);
   IF addBullet = 0 THEN
    SimpleSound(soundList[sMoney]);
    AddMoney(mainPlayer, -bl, 0)
   ELSE
    SimpleSound(soundList[sHurryUp])
   END
  END
 END BuyWeapon;

 PROCEDURE DrawItem(item: CARD16);
  VAR
   str, txt1, txt2: StrPtr;
   py: INT16;
   itemD, itemS: CARD8;
   test: BOOLEAN;
 BEGIN
  str:= ItemToPrice(item, itemD, itemS);
  test:= (nbDollar >= itemD) AND (nbSterling >= itemS);
  IF item = 1 THEN test:= (specialStage > 0) END;
  dActive[item]:= test;
  IF color THEN
   IF NOT(up) AND (yd = item) THEN
    IF test THEN p1:= 2 ELSE p1:= 1 END
   ELSE
    IF test THEN p1:= 7 ELSE p1:= 0 END
   END
  ELSE
   p2:= 1;
   IF test THEN p3:= 1 ELSE p3:= 0 END;
   IF NOT(up) AND (yd = item) THEN p1:= 1 ELSE p1:= 0 END
  END;
  txt2:= NIL;
  CASE item OF
    0: py:= 150; txt1:= ADL("Extra Life")
   |1: py:= 170; txt1:= ADL("-> Bonus Level")
   |2: py:= 180; txt1:= ADL("-> Family: "); txt2:= GetFamilyName(level[Family])
   |3: py:= 190; txt1:= ADL("-> Castle: "); txt2:= GetCastleName(level[Castle])
   |4: py:= 200; txt1:= ADL("-> Chaos level "); txt2:= GetChaosName(level[Chaos])
  END;
  TripleWrite(0, py, txt1);
  IF txt2 <> NIL THEN
   TripleWrite(TextWidth(txt1) DIV mulS, py, txt2)
  END;
  IF str <> NIL THEN
   TripleWrite(PW - TextWidth(str) DIV mulS - 4, py, str)
  END
 END DrawItem;

 PROCEDURE UpdateItem(item: CARD16);
  VAR
   str: StrPtr;
   itemD, itemS: CARD8;
   t: BOOLEAN;
 BEGIN
  str:= ItemToPrice(item, itemD, itemS);
  t:= ((nbDollar >= itemD) AND (nbSterling >= itemS));
  IF dActive[item] <> t THEN
   DrawItem(item)
  END
 END UpdateItem;

 PROCEDURE BuyItem(): BOOLEAN;
  VAR
   str: StrPtr;
   itemD, itemS: CARD8;
   iD, iS: INT16;
 BEGIN
  IF NOT(dActive[yd]) THEN
   SimpleSound(soundList[sPoubelle]);
   RETURN FALSE
  END;
  str:= ItemToPrice(yd, itemD, itemS);
  iS:= itemS; iD:= itemD;
  AddMoney(mainPlayer, -iD, -iS);
  IF yd = 0 THEN
   SimpleSound(soundList[sMoney]);
   AddLife(mainPlayer);
   RETURN FALSE
  ELSIF yd = 1 THEN
   IF specialStage > 0 THEN
    zone:= Special;
    DEC(specialStage); RETURN TRUE
   ELSE
    RETURN FALSE
   END
  ELSE
   IF yd = 2 THEN
    zone:= Family
   ELSIF yd = 3 THEN
    zone:= Castle
   ELSE
    zone:= Chaos
   END;
   RETURN TRUE
  END
 END BuyItem;

 VAR
  h, m, s, om: CARD16;

 PROCEDURE DrawTime;
  VAR
   hour: ARRAY[0..5] OF CHAR;
 BEGIN
  SetTextSize(H(9)); SetPen(0);
  FillRect(0, H(30), W(PW), H(40));
  GetCurrentTime(h, m, s); om:= m;
  hour[0]:= CHR(h DIV 10 + 48); hour[1]:= CHR(h MOD 10 + 48);
  hour[3]:= CHR(m DIV 10 + 48); hour[4]:= CHR(m MOD 10 + 48);
  hour[2]:= ":"; hour[5]:= 0C; p1:= 0; p2:= 3; p3:= 4;
  TripleWrite(PW - TextWidth(ADR(hour)) - mulS, 30, ADR(hour));
  StartTime(time); TimeEvent(time, Period * 59)
 END DrawTime;

 VAR
  shopClosed: BOOLEAN;

 PROCEDURE DrawShopScreen;
  VAR
   str: ARRAY[0..41] OF CHAR;
   yw: Weapon;
   yd, rnd: CARD16;
 BEGIN
  ResetGraphics;
  DrawInfos(FALSE);
  ResetGraphics;
  FillRect(0, 0, W(PW), H(PH));
  SetTextSize(H(18));
  p1:= 7; p2:= 3; p3:= 4;
  TripleCenter(PW, 10, ADL("Chaos' Shop"));
  DrawTime;
  SetTextSize(H(9));
  IF shopClosed THEN
   Center(0, 50, PW, ADL("Sorry, the shop is closed."));
   rnd:= RND() MOD 8;
   CASE rnd OF
     0: str:= "conflagration"
    |1: str:= "inventory / renovation"
    |2: str:= "housebreaking"
    |3: str:= "Father alien's bad mood"
    |4: str:= "leave"
    |5: str:= "power failure"
    |6: str:= "software failure"
    |7: str:= "flood"
   END;
   Center(0, 70, PW, ADL("Cause:"));
   Center(0, 80, PW, ADL(str));
   DrawItem(4)
  ELSE
   SetTextMode(TextModeSet{italic}); p1:= 7;
   TripleWrite(0, 50, ADL("Item:"));
   Center(x1, 50, PW DIV 4, ADL("Bullet"));
   Center(x2, 50, PW DIV 4, ADL("Bomb"));
   SetTextMode(TextModeSet{});
   FOR yw:= MIN(Weapon) TO MAX(Weapon) DO
    DrawWeapon(yw)
   END;
   FOR yd:= 0 TO 4 DO
    DrawItem(yd)
   END
  END;
  UpdateScreen
 END DrawShopScreen;

 PROCEDURE ShopScreen;
  VAR
   joy, fj: SET16;
   event, fe: Event;
   item: CARD16;
   weapon: Weapon;
 BEGIN
  IF gameStat <> Finish THEN RETURN END;
  bomb:= FALSE; up:= FALSE;
  yw:= MIN(Weapon); yd:= 4;
  FOR weapon:= MIN(Weapon) TO MAX(Weapon) DO
   available[weapon]:= 20 - difficulty * 2 + RND() MOD (32 - 2 * difficulty)
  END;
  shopClosed:= (difficulty >= 5) AND (RND() MOD 48 = 0);
  zone:= Chaos;
  FadeOut;
  DrawShopScreen;
  FadeIn;
  Refresh:= DrawShopScreen;
  UpdateScreen; SetBuffer(TRUE, TRUE);
  EnableFileMenus;
  WaitRelease;
  LOOP
   SetBusyStat(statWaiting);
   WaitEvent;
   GetEvent(event); fe:= event;
   joy:= GetStick(); fj:= joy;
   CheckSelect(event, joy, TRUE);
   SetBuffer(TRUE, TRUE);
   SetOrigin(0, 0);
   IF shopClosed THEN joy:= joy - SET16{JoyLeft, JoyRight, JoyUp, JoyDown} END;
   IF joy * SET16{JoyLeft, JoyRight, JoyUp, JoyDown, JoyForward, JoyReverse} <> SET16{} THEN
    up:= NOT(up);
    IF NOT(up) THEN DrawWeapon(yw) ELSE DrawItem(yd) END;
    up:= NOT(up)
   END;
   IF (JoyUp IN joy) THEN
    IF up THEN
     IF yw = MIN(Weapon) THEN up:= FALSE; yd:= 4 ELSE DEC(yw) END
    ELSE
     IF yd = 0 THEN up:= TRUE; yw:= MAX(Weapon) ELSE DEC(yd) END
    END
   ELSIF (JoyDown IN joy) THEN
    IF up THEN
     IF yw = MAX(Weapon) THEN up:= FALSE; yd:= 0 ELSE INC(yw) END
    ELSE
     IF yd = 4 THEN up:= TRUE; yw:= MIN(Weapon) ELSE INC(yd) END
    END
   ELSIF joy * SET16{JoyLeft, JoyRight} <> SET16{} THEN
    bomb:= NOT(bomb)
   ELSIF joy * SET16{Joy1, Joy3, Joy4, JoyPause} <> SET16{} THEN
    IF up THEN
     BuyWeapon(Joy3 IN joy)
    ELSE
     IF BuyItem() THEN
      IF zone = Family THEN
       IF level[Family] = 10 THEN
        SimpleSound(soundList[sCasserole])
       ELSE
        SimpleSound(soundList[sHa])
       END
      ELSIF zone = Special THEN
       SimpleSound(soundList[wJans])
      ELSE
       SimpleSound(soundList[sCaisse])
      END;
      EXIT
     END
    END;
    FOR weapon:= MIN(Weapon) TO MAX(Weapon) DO UpdateWeapon(weapon) END;
    FOR item:= 0 TO 4 DO UpdateItem(item) END;
    UpdateInfos; SetOrigin(0, 0);
    SetCopyMode(cmTrans)
   END;
   IF up THEN DrawWeapon(yw) ELSE DrawItem(yd) END;
   GetCurrentTime(h, m, s);
   IF m <> om THEN DrawTime END;
   IF (fe.type <> eNUL) OR (fj <> SET16{}) THEN UpdateScreen END;
   SetBuffer(TRUE, FALSE);
   CommonEvent(event);
   IF event.type = eTIMER THEN WaitRelease END;
   IF gameStat <> Finish THEN EXIT END
  END;
  UpdateInfos; SetOrigin(0, 0);
  UpdateScreen
 END ShopScreen;

 PROCEDURE DrawStatistics;

  PROCEDURE ShowDecor;
   VAR
    str: StrPtr;
    x, y, dx, dy, sx, sy, w: INT16;
    p, red, c: CARD16;
    wall, lwall: BOOLEAN;
  BEGIN
   IF color THEN
    p:= 8
   ELSE
    p:= 1; SetCopyMode(cmCopy);
    SetPen(0); FillRect(0, 0, W(PW), H(PH))
   END;
   SetCopyMode(cmXor);
   SetTextSize(H(18)); SetPen(p);
   IF zone = Castle THEN
    str:= GetCastleName(level[Castle])
   ELSE
    str:= GetSpecialName(level[Special])
   END;
   w:= TextWidth(str) DIV mulS;
   dx:= (PW - w) DIV 2;
   WriteAt(X(dx + 1), Y(21), str);
   WriteAt(X(dx), Y(20), str);
   str:= ADL("Post Mortem Map");
   w:= TextWidth(str) DIV mulS;
   dx:= (PW - w) DIV 2;
   WriteAt(X(dx + 1), Y(41), str);
   WriteAt(X(dx), Y(40), str);
   SetTextSize(9); SetPen(p);
   dx:= (PW - castleWidth) DIV 2;
   dy:= (PH - 40 - castleHeight) DIV 2 + 40;
   FOR y:= 0 TO castleHeight - 1 DO
    sx:= 0; lwall:= FALSE; sy:= y + dy;
    FOR x:= 0 TO castleWidth - 1 DO
     wall:= castle^[y, x] >= NbClear;
     IF wall THEN
      IF NOT(lwall) THEN sx:= x END
     ELSE
      IF lwall THEN
       FillRect(W(sx + dx), H(sy), W(x + dx), H(sy + 1))
      END
     END;
     lwall:= wall
    END;
    IF lwall THEN
     FillRect(W(sx + dx), H(sy), W(castleWidth + dx), H(sy + 1))
    END
   END;
   SetCopyMode(cmCopy);
   IF water THEN red:= 70 ELSE red:= 216 END;
   IF color THEN
    FOR c:= 0 TO 7 DO SetPalette(c, 0, 0, 0) END;
    FOR c:= 8 TO 15 DO SetPalette(c, red, 152, 0) END;
    FOR c:= 0 TO 7 DO palette[c + 8]:= palette[c] END
   END;
   UpdateScreen;
   WaitRelease;
   SetBusyStat(statWaiting);
   REPEAT
    FlushEvents;
    WaitEvent
   UNTIL GetStick() <> SET16{};
   IF color THEN
    FadeFrom(red, 152, 0)
   END;
   Refresh:= DrawStatistics;
   DrawStatistics
  END ShowDecor;

  CONST
   x1 = PW DIV 3;
   x2 = PW * 25 DIV 48;
   x3 = PW * 17 DIV 24;
  VAR
   cnt: INT16;
   total: CARD16;

  PROCEDURE DrawStatistic(y: INT16; str: StrPtr; stat: Statistic; add: INT16);
   VAR
    ratio, div: CARD32;
  BEGIN
   TripleWrite(0, y, str);
   TripleCard(x1, y, stat.total);
   TripleCard(x2, y, stat.done);
   ratio:= stat.done; ratio:= ratio * 100;
   IF stat.total <> 0 THEN
    div:= stat.total;
    ratio:= ratio DIV div;
    IF ratio > 100 THEN ratio:= 100 END
   ELSE
    ratio:= 0
   END;
   INC(total, ratio);
   TripleCard(x3, y, ratio);
   IF ratio >= 75 THEN
    str:= ADL("Good"); p1:= 7;
    INC(score, difficulty * 10);
    INC(cnt, add)
   ELSE
    str:= ADL("Bad"); p1:= 0
   END;
   TripleWrite(PW - TextWidth(str) DIV mulS - 4, y, str);
   p1:= 7
  END DrawStatistic;

  PROCEDURE Push(string: ARRAY OF CHAR);
   VAR
    text: ARRAY[0..22] OF CHAR;
    c: INT16;
  BEGIN
   IF cnt = 0 THEN
    FOR c:= 0 TO HIGH(string) DO text[c]:= string[c] END;
    text[HIGH(string) + 1]:= 0C;
    p1:= 4; p2:= 3; p3:= 6;
    TripleWrite(16, 180, ADL(text));
    p1:= 7; p2:= 3; p3:= 4
   END;
   DEC(cnt)
  END Push;

  VAR
   str: StrPtr;
   c: CARD16;

 BEGIN
  ResetGraphics;
  DrawInfos(FALSE);
  ResetGraphics;
  FOR c:= 0 TO 15 DO SetTrans(c, 255) END;
  FillRect(0, 0, W(PW), H(PH));
  SetTextSize(H(27));
  p1:= 7; p2:= 3; p3:= 4;
  IF zone = Castle THEN
   str:= GetCastleName(level[Castle])
  ELSE
   str:= GetSpecialName(level[Special])
  END;
  TripleCenter(PW, 0, str);
  SetTextSize(H(18));
  TripleCenter(PW, 30, ADL("Statistics"));
  SetTextSize(H(9));
  SetTextMode(TextModeSet{italic});
  TripleWrite(0, 70, ADL("What:"));
  TripleWrite(x1, 70, ADL("Total"));
  TripleWrite(x2, 70, ADL("Done"));
  TripleWrite(x3, 70, ADL("%"));
  SetTextMode(TextModeSet{});
  IF shoot.done > shoot.total THEN shoot.done:= shoot.total END;
  cnt:= 0; total:= 0;
  DrawStatistic(90, ADL("Shoots:"), shoot, 16);
  DrawStatistic(100, ADL("Treasure:"), basics[Bonus], 8);
  DrawStatistic(120, ADL("Mineral:"), basics[Mineral], 4);
  DrawStatistic(130, ADL("Vegetal:"), basics[Vegetal], 2);
  DrawStatistic(140, ADL("Animal:"), basics[Animal], 1);
  TripleWrite(0, 170, ADL("Advised career:"));
  Push("Milksop");
  Push("Vampire");
  Push("Dabbler");
  Push("Poisoner");
  Push("Somnambulist");
  Push("Yellow");
  Push("Road-sweeper");
  Push("Executioner");
  Push("Tourist");
  Push("Undertaker");
  Push("Beggar");
  Push("Butcher");
  Push("Salesman");
  Push("Criminal");
  Push("Thief");
  Push("Sorcerer");
  Push("Sharpshooter");
  Push("Hunter");
  Push("Peasant");
  Push("Killer");
  Push("Saboteur");
  Push("Soldier");
  Push("Fireman");
  Push("Incendiary");
  Push("Conjurer");
  Push("Ripper");
  Push("Drugdealer");
  Push("Tyrant");
  Push("Destroyer");
  Push("Out-law");
  Push("Devil");
  Push("Terrorist");
  IF total = 500 THEN
   SetPen(0); FillRect(W(16), H(180), W(PW), H(190));
   cnt:= 0; Push("> Nightmare <")
  END;
  p1:= 2;
  IF ((stages = 0) OR password) AND (Refresh <> ADR(DrawStatistics)) THEN
   ShowDecor
  END;
  ResetGraphics;
  TripleCenter(PW, 230, ADL("Press [SPACE] to continue"));
  UpdateScreen
 END DrawStatistics;

 PROCEDURE StatisticScreen;
  VAR
   c: CARD16;
 BEGIN
  IF gameStat <> Finish THEN RETURN END;
  DisableFileMenus;
  SetBusyStat(statBusy);
  IF (zone = Castle) OR (zone = Special) THEN
   DrawStatistics;
   Refresh:= DrawStatistics;
   WaitStart
  END;
  FadeOut;
  IF water THEN
   SetPen(0); SetCopyMode(cmCopy);
   FillRect(0, 0, W(SW), H(SH));
   water:= FALSE; InitPalette;
   FOR c:= 0 TO 15 DO SetPalette(c, 0, 0, 0) END
  END;
  EnableFileMenus
 END StatisticScreen;

 PROCEDURE DrawEndScreen;
 BEGIN
  ResetGraphics;
  FillRect(0, 0, W(SW), H(SH));
  SetTextSize(H(18));
  p1:= 7; p2:= 3; p3:= 4;
  TripleCenter(SW, 10, ADL("About this game"));
  SetTextSize(H(9));
  TripleCenter(SW, 50, ADL("Minerals, Vegetals and Animals;"));
  TripleCenter(SW, 60, ADL("or in short, Chaos Castle,"));
  TripleCenter(SW, 70, ADL("has been designed and programmed"));
  TripleCenter(SW, 80, ADL("in Modula-2 by"));
  TripleCenter(SW, 92, ADL("Nicky"));
  TripleCenter(SW, 112, ADL("Thanks to all my friends"));
  TripleCenter(SW, 122, ADL("for a lot of ideas"));
  TripleCenter(SW, 132, ADL("and for play testing."));
  TripleCenter(SW, 155, ADL("This piece of software is"));
  IF registered THEN
   TripleCenter(SW, 165, ADL("registered to"));
   TripleCenter(SW, 180, userName);
   TripleCenter(SW, 190, userAddress);
   TripleCenter(SW, 200, userLoc)
  ELSE
   TripleCenter(SW, 165, ADL("SHAREWARE"));
   TripleCenter(SW, 180, ADL("I spent a lot of time making this game;"));
   TripleCenter(SW, 190, ADL("If you like it, please register."));
   TripleCenter(SW, 200, ADL("(more in the documentation)"))
  END;
  UpdateScreen
 END DrawEndScreen;

 PROCEDURE UpdateTopScore(VAR topScore: TopScore; c, s, n: CARD16);
  VAR
   str: ARRAY[0..3] OF CHAR;
   y: INT16;
 BEGIN
  SetBuffer(TRUE, TRUE);
  y:= c * 10 + 50;
  SetPen(0); SetCopyMode(cmTrans);
  FillRect(0, H(y), W(SW), H(y + 10));
  WITH topScore DO
   IF c = n THEN p1:= 4; p2:= 3; p3:= 6 ELSE p1:= 7; p2:= 3; p3:= 4 END;
   IF c <> s THEN p1:= 0 ELSIF NOT(color) THEN p1:= 1 END;
   IF c = 10 THEN str[0]:= "1" ELSE str[0]:= " " END;
   str[1]:= CHR(48 + c MOD 10);
   str[2]:= "."; str[3]:= 0C;
   TripleWrite(0, y, ADR(str));
   TripleWrite(30, y, ADR(name));
   TripleCard(SW * 2 DIV 3, y, score)
  END
 END UpdateTopScore;

 PROCEDURE EditTopScore(VAR topScore: TopScore; n: CARD16): BOOLEAN;
  VAR
   event: Event;
   pos: CARD16;
   exit: BOOLEAN;
 BEGIN
  p1:= 5; p2:= 3; p3:= 4;
  SetBuffer(TRUE, TRUE); SetCopyMode(cmTrans);
  TripleCenter(PW, n * 10 + 50, ADL("Enter your name !"));
  UpdateScreen;
  WhiteFade;
  FlushEvents;
  pos:= 0; exit:= FALSE;
  REPEAT
   SetBusyStat(statWaiting);
   WaitEvent;
   GetEvent(event);
   IF event.type = eKEYBOARD THEN
    topScore.name[pos]:= " ";
    IF (event.ch = CHR(8)) OR (event.ch = CHR(127)) THEN
     SimpleSound(soundList[sPoubelle]);
     IF pos > 0 THEN DEC(pos) END;
     topScore.name[pos]:= "_"
    ELSIF (event.ch = CHR(13)) OR (event.ch = CHR(10)) THEN
     IF n = 1 THEN
      SimpleSound(soundList[wJans])
     ELSE
      SimpleSound(soundList[sCaisse])
     END;
     exit:= TRUE
    ELSIF (pos < 19) AND (event.ch >= " ") AND (event.ch < CHR(127)) THEN
     SimpleSound(soundList[sGun]);
     topScore.name[pos]:= event.ch; INC(pos);
     topScore.name[pos]:= "_"
    END;
    UpdateTopScore(topScore, n, n, n);
    UpdateScreen
   END;
   CommonEvent(event)
  UNTIL exit OR (gameStat = Break);
  SetBusyStat(statBusy);
  RETURN pos > 0
 END EditTopScore;

 VAR
  sl, n: CARD16;
  topScores: TopScoreList;

 PROCEDURE UpdateTopScores;
  VAR
   c: CARD16;
   pa, pb: CARD8;
 BEGIN
  ResetGraphics;
  SetTextSize(H(9));
  FOR c:= 1 TO 10 DO
   UpdateTopScore(topScores[c], c, sl, n)
  END;
  SetPen(0); FillRect(0, H(170), W(SW), H(SH));
  IF sl <> n THEN pa:= 0; pb:= 1 ELSE pa:= 7; pb:= 2 END;
  p1:= pa; p2:= 3; p3:= 4;
  TripleCenter(SW, 170, ADL("was killed at"));
  WITH topScores[sl] DO
   IF endZone = Chaos THEN p1:= pb ELSE p1:= pa END;
   TripleWrite(10, 180, ADL("Chaos level"));
   TripleWrite(SW DIV 2, 180, GetChaosName(endLevel[Chaos]));
   IF endZone = Castle THEN p1:= pb ELSE p1:= pa END;
   TripleWrite(10, 190, ADL("Castle:"));
   TripleWrite(SW DIV 2, 190, GetCastleName(endLevel[Castle]));
   IF endZone = Family THEN p1:= pb ELSE p1:= pa END;
   TripleWrite(10, 200, ADL("Family:"));
   TripleWrite(SW DIV 2, 200, GetFamilyName(endLevel[Family]));
   p1:= pa;
   TripleWrite(10, 210, ADL("Difficulty:"));
   TripleCard(SW DIV 2, 210, endDifficulty)
  END;
  UpdateScreen
 END UpdateTopScores;

 PROCEDURE DrawTopScores;
 BEGIN
  ResetGraphics;
  FillRect(0, 0, W(SW), H(SH));
  SetTextSize(H(27));
  p1:= 7; p2:= 3; p3:= 4;
  TripleCenter(SW, 10, ADL("HIGHSCORES"));
  UpdateTopScores
 END DrawTopScores;

 PROCEDURE TopScoreWait;
  VAR
   event: Event;
   joy: SET16;
 BEGIN
  REPEAT
   SetBusyStat(statWaiting);
   WaitEvent;
   GetEvent(event);
   joy:= GetStick();
   IF (JoyUp IN joy) AND (sl > 1) THEN DEC(sl) END;
   IF (JoyDown IN joy) AND (sl < 10) THEN INC(sl) END;
   IF joy * SET16{JoyUp, JoyDown} <> SET16{} THEN
    UpdateTopScores
   END;
   CommonEvent(event)
  UNTIL (joy * SET16{Joy1..Joy4, JoyPause} <> SET16{}) OR (gameStat = Break)
 END TopScoreWait;

 PROCEDURE StartScreen;
 BEGIN
  gameStat:= Start;
  FadeOut;
  SetBuffer(TRUE, FALSE);
  SetPen(0); SetCopyMode(cmCopy);
  FillRect(0, 0, W(SW), H(SH));
  InitPalette;
  DrawStartScreen;
  Refresh:= DrawStartScreen;
  EnableFileMenus;
  gameSeed:= GetNewSeed();
  sl:= 1; n:= 0;
  LOOP
   WaitStart;
   IF NOT(joy2pressed) OR (gameStat = Break) THEN EXIT END;
   FadeOut;
   ReadTopScoreList(topScores);
   Refresh:= DrawTopScores;
   Refresh; FadeIn;
   TopScoreWait;
   IF gameStat = Break THEN EXIT END;
   FadeOut;
   Refresh:= DrawStartScreen;
   Refresh
  END;
  DisableFileMenus;
  Refresh:= NIL
 END StartScreen;

 PROCEDURE GameOverScreen;
  VAR
   c, d, ps: CARD16;
   z: Zone;
   norepl: BOOLEAN;
 BEGIN
  IF (gameStat <> Gameover) OR (pLife <> 0) THEN RETURN END;
  DisableFileMenus;
  FadeOut;
  water:= FALSE; InitPalette;
  ResetGraphics;
  FillRect(0, 0, W(SW), H(SH));
  SetBuffer(TRUE, FALSE);
  SetPen(0); SetCopyMode(cmTrans);
  FillRect(0, 0, W(SW), H(SH));
  ZoomMessage(ADL("Game Over"), 0, 3, 4);
  ResetGraphics;
  ReadTopScoreList(topScores);
  c:= 1; norepl:= FALSE;
  LOOP
   IF (topScores[c].seed = gameSeed) AND NOT(password) THEN
    IF topScores[c].score < score THEN
     FOR d:= c TO 9 DO
      topScores[d]:= topScores[d + 1]
     END;
     topScores[10].score:= 0
    ELSE
     ps:= c; norepl:= TRUE
    END;
    EXIT
   END;
   INC(c);
   IF c > 10 THEN EXIT END
  END;
  n:= 1; sl:= 1;
  WHILE (n <= 10) AND (score < topScores[n].score) DO INC(n) END;
  IF password OR norepl THEN n:= 11 END;
  IF n <= 10 THEN
   sl:= n;
   FOR c:= 10 TO n + 1 BY -1 DO
    topScores[c]:= topScores[c - 1]
   END;
   topScores[n].score:= score;
   topScores[n].seed:= gameSeed;
   WITH topScores[n] DO
    FOR c:= 0 TO 19 DO name[c]:= " " END;
    FOR z:= Chaos TO Family DO endLevel[z]:= level[z] END;
    endZone:= zone;
    endDifficulty:= difficulty
   END
  END;
  DrawTopScores;
  Refresh:= DrawTopScores;
  WaitRelease;
  IF n <= 10 THEN
   IF EditTopScore(topScores[n], n) THEN;
    WriteTopScoreList(topScores)
   ELSE
    ReadTopScoreList(topScores);
    n:= 11;
    DrawTopScores;
    UpdateScreen
   END;
   WaitRelease
  END;
  FadeIn;
  Warn(password, ADL("Your score won't be saved"), ADL("because you used the password"));
  IF norepl THEN n:= ps; sl:= ps; UpdateTopScores END;
  TopScoreWait;
  FadeOut;
  password:= FALSE;
  IF gameStat = Break THEN RETURN END;
  Refresh:= DrawEndScreen;
  gameStat:= Start;
  DrawEndScreen;
  FadeIn;
  WaitStart;
  gameStat:= Gameover;
  EnableFileMenus
 END GameOverScreen;

END ChaosScreens.
