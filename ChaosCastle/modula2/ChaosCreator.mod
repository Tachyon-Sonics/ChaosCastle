IMPLEMENTATION MODULE ChaosCreator;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32, AllocMem, AddTail;
 FROM Checks IMPORT CheckMem;
 FROM Languages IMPORT ADL;
 FROM Trigo IMPORT RND, SQRT, SIN, COS, SGN;
 FROM ChaosBase IMPORT Anims, AnimSet, BasicTypes, Obj, ObjPtr, ObjAttr,
  ObjAttrPtr, attrList, Stones, StoneSet, gravityStyle, slowStyle, Frac,
  Period, lasttime, step, pLife, difficulty, mainPlayer, FlameMult, fastStyle,
  addpt, nbAnim, GameStat, gameStat, level, Zone, returnStyle, Weapon,
  WeaponAttr, weaponAttr, nbDollar, nbSterling, stages, powerCountDown,
  GetAnimAttr, water, zone, ObjFlags, ObjFlagSet, sleeper;
 FROM ChaosGraphics IMPORT BW, BH, PW, PH, gameWidth, gameHeight,
  castle, NbBackground, castleWidth, castleHeight, backpx, backpy;
 FROM ChaosSounds IMPORT SoundList, soundList, Sound, Effect, SetEffect,
  SoundEffect, nulSound;
 FROM ChaosActions IMPORT SetObjLoc, SetObjRect, UpdateXY, Aie, Die,
  DecLife, DoCollision, OutOfScreen, AvoidBounds, AvoidBackground, Leave,
  GetCenter, Burn, CreateObj, LimitSpeed, SetObjVXY, moneyPos, PopMessage,
  OutOfBounds, SetObjAXY, statPos, Gravity, AvoidAnims, InBackground,
  SetObjXY, NextStage, PlayerCollision;
 FROM ChaosPlayer IMPORT AddMoney;
 FROM ChaosBonus IMPORT BoumMoney, Moneys, MoneySet, TimedBonus, tbBullet;
 FROM ChaosDObj IMPORT doMeteor, domStone, domSmall, domMedium, domBig, doWave;
 FROM ChaosMissile IMPORT mAlien1, mBlue, mRed, mYellow, mWhite, mGreen;
 FROM ChaosAlien IMPORT aDbOval, aDiese, aTri, aBig, aColor, aSquare,
  aFlame, aHospital, aKamikaze, aTrefle, aStar, aBubble, aBumper;

 VAR
  hurryupEffect, missileEffect, aieCircleEffect,
  dieCircleEffect, dieHUEffect, aieAlienEffect,
  aieCreatorEffect, aieHUEffect, aieChiefEffect, dieGhostEffect,
  circleFireEffect, diePopupEffect, aieGhostEffect: ARRAY[0..0] OF Effect;
  dieAlienVEffect, dieAlienAEffect: ARRAY[0..16] OF Effect;
  dieABEffect: ARRAY[0..11] OF Effect;
  dieCreatorREffect, dieNestEffect: ARRAY[0..23] OF Effect;
  dieCreatorCEffect: ARRAY[0..7] OF Effect;
  dieFourEffect: ARRAY[0..8] OF Effect;
  popupEffect: ARRAY[0..2] OF Effect;
  aieGridEffect: ARRAY[0..3] OF Effect;


 PROCEDURE MakeAlienV(alien: ObjPtr);
  VAR
   px: INT16;
 BEGIN
  IF alien^.moveSeq < Period THEN px:= 20 ELSE px:= 0 END;
  SetObjLoc(alien, px, 128, 20, 20);
  SetObjRect(alien, 0, 0, 20, 20)
 END MakeAlienV;

 PROCEDURE MakeAlienA(alien: ObjPtr);
  VAR
   px: INT16;
 BEGIN
  WITH alien^ DO
   IF (moveSeq < Period) AND (shapeSeq = 0) THEN px:= 60 ELSE px:= 40 END
  END;
  SetObjLoc(alien, px, 128, 20, 20);
  SetObjRect(alien, 0, 0, 20, 20)
 END MakeAlienA;

 PROCEDURE MakeFour(four: ObjPtr);
  VAR
   py: INT16;
 BEGIN
  IF four^.moveSeq < Period THEN py:= 184 ELSE py:= 160 END;
  SetObjLoc(four, 200, py, 24, 24);
  SetObjRect(four, 0, 0, 24, 24)
 END MakeFour;

 PROCEDURE MakeQuad(quad: ObjPtr);
 BEGIN
  SetObjLoc(quad, 5, 185, 22, 22);
  SetObjRect(quad, 0, 0, 22, 22)
 END MakeQuad;

 PROCEDURE MakeCreatorR(creator: ObjPtr);
 BEGIN
  SetObjLoc(creator, 96, 128, 32, 32);
  SetObjRect(creator, 0, 0, 32, 32)
 END MakeCreatorR;

 PROCEDURE MakeCreatorC(creator: ObjPtr);
 BEGIN
  IF creator^.moveSeq >= Period * 3 THEN
   SetObjLoc(creator, 128, 132, 28, 28);
   SetObjRect(creator, 0, 0, 28, 28)
  ELSE
   SetObjLoc(creator, 156, 132, 28, 28)
  END
 END MakeCreatorC;

 PROCEDURE MakeCircle(circle: ObjPtr);
 BEGIN
  IF circle^.shapeSeq = 0 THEN
   SetObjLoc(circle, 64, 96, 32, 32)
  ELSE
   SetObjLoc(circle, 224, 132, 32, 32)
  END;
  SetObjRect(circle, 1, 1, 31, 31)
 END MakeCircle;

 PROCEDURE FireMeteorite(px, py, mvx, mvy: INT16; ns: CARD16);
  VAR
   mt: ObjPtr;
 BEGIN
  mt:= CreateObj(DEADOBJ, doMeteor, px, py, ns, 1);
  IF ns = domSmall THEN mvx:= mvx * 2; mvy:= mvy * 2 END;
  SetObjVXY(mt, mvx, mvy)
 END FireMeteorite;

 VAR
  huCnt: CARD16;

 PROCEDURE MakeController(ctrl: ObjPtr);
  CONST
   MX = PW DIV 2;
   MY = PH DIV 2;
  VAR
   hu, wv: ObjPtr;
   wa: ObjAttrPtr;
   mvx, mvy, speed: INT32;
   px, py, nvx, angle: INT16;
   md, rt, dv, nb: CARD16;
   mf: BOOLEAN;
 BEGIN
  WITH ctrl^ DO
   IF moveSeq = 0 THEN
    IF zone <> Castle THEN
      (* hurryup *)
     IF stat = 0 THEN
      PopMessage(ADL("Hurry up !"), statPos, 1);
      IF (huCnt = 0) AND (stages = 3) THEN
       NextStage
      ELSIF huCnt > 0 THEN
       DEC(huCnt)
      END;
      IF RND() MOD 2 = 0 THEN
       px:= -BW; nvx:= 800
      ELSE
       px:= gameWidth + BW; nvx:= -800
      END;
      md:= (gameHeight DIV 2); py:= RND() MOD md;
      hu:= CreateObj(ALIEN2, cHurryUp, px, py, 1, 100);
      SetObjVXY(hu, nvx, 0);
      SoundEffect(hu, hurryupEffect)
     END;
     stat:= 0;
     IF pLife > 20 THEN
      moveSeq:= (Period * 20)
     ELSE
      moveSeq:= Period * (80 - pLife * 2) DIV 2
     END;
     IF zone <> Chaos THEN moveSeq:= moveSeq * 4 END
    END
   END;
   IF shapeSeq = 0 THEN
    IF water THEN
     wa:= GetAnimAttr(DEADOBJ, doWave);
     IF wa^.nbObj < 12 THEN
      angle:= RND() MOD 360;
      px:= COS(angle) DIV 16 * PW DIV 64;
      py:= SIN(angle) DIV 16 * PH DIV 64;
      INC(px, backpx + MX); INC(py, backpy + MY);
      speed:= RND() MOD 512 + 512;
      INC(angle, RND() MOD 256); DEC(angle, 128);
      mvx:= COS(angle); mvy:= SIN(angle);
      mvx:= -mvx * speed DIV 1024;
      mvy:= -mvy * speed DIV 1024;
      wv:= CreateObj(DEADOBJ, doWave, px, py, RND() MOD 15, 1);
      SetObjVXY(wv, mvx, mvy)
     END;
     shapeSeq:= Period DIV 2
    END;
    IF (difficulty >= 3) AND ((zone = Chaos) OR ((zone = Family) AND (level[Family] = 6))) THEN
      (* meteorite *)
     mf:= FALSE;
     angle:= RND() MOD 360;
     px:= COS(angle) DIV 16 * PW DIV 64;
     py:= SIN(angle) DIV 16 * PH DIV 64;
     INC(px, backpx + MX); INC(py, backpy + MY);
     speed:= RND() MOD 1024 + 512;
     INC(angle, RND() MOD 64); DEC(angle, 32);
     mvx:= COS(angle); mvy:= SIN(angle);
     mvx:= -mvx * speed DIV 1024;
     mvy:= -mvy * speed DIV 1024;
     IF level[Castle] > 1 THEN
      rt:= SQRT(level[Chaos]); dv:= 2; nb:= 0;
      WHILE (nb = 0) AND (dv <= rt) DO
       IF level[Chaos] MOD dv = 0 THEN INC(nb) END;
       INC(dv)
      END;
      IF (nb = 0) OR (level[Chaos] MOD 33 = 0) OR
         ((difficulty >= 6) AND (level[Chaos] MOD 10 = 0)) THEN
       FireMeteorite(px, py, mvx, mvy, domBig); mf:= TRUE
      END;
      IF difficulty >= 5 THEN
       md:= level[Chaos] MOD 13;
       IF (md MOD 8 = 0) THEN
        px:= PW - px; mvx:= -mvx;
        py:= PH - py; mvy:= -mvy; mf:= TRUE;
        FireMeteorite(px, py, mvx, mvy, domMedium)
       END
      END;
      md:= level[Chaos] MOD 10;
      IF (md = 9) OR ((difficulty >= 4) AND (md = 3)) THEN
       IF RND() MOD 2 = 0 THEN
        px:= PW - px; py:= PH - py;
        mvx:= -mvx; mvy:= -mvy
       ELSE
        INC(px, mvy DIV 64); DEC(py, mvx DIV 64);
        IF mf AND (RND() MOD 8 = 0) THEN
         FireMeteorite(px, py, mvx, mvy, domSmall);
         DEC(px, mvy DIV 24); INC(py, mvx DIV 32)
        END
       END;
       FireMeteorite(px, py, mvx, mvy, domSmall)
      END
     END;
     IF (difficulty >= 7) AND ((level[Chaos] MOD 7 = 0) OR (level[Chaos] = 100)) THEN
      py:= PH - py; mvy:= -mvy;
      px:= PW - px; mvx:= -mvx;
      FireMeteorite(px, py, mvx, mvy, domStone)
     END;
     nb:= (10 - difficulty DIV 2);
     IF level[Chaos] MOD 33 = 0 THEN nb:= nb DIV 3 END;
     shapeSeq:= RND() MOD (Period * nb)
    END
   END
  END
 END MakeController;

 PROCEDURE MakeHurryUp(hu: ObjPtr);
 BEGIN
  SetObjLoc(hu, 40, 80, 24, 16);
  SetObjRect(hu, 0, 0, 24, 16)
 END MakeHurryUp;

 PROCEDURE MakeChief(chief: ObjPtr);
 BEGIN
  SetObjLoc(chief, 128, 72, 16, 16);
  SetObjRect(chief, 0, 0, 16, 16)
 END MakeChief;

 PROCEDURE MakeABox(abox: ObjPtr);
 BEGIN
  SetObjLoc(abox, 0, 180, 32, 32);
  SetObjRect(abox, 0, 0, 32, 32)
 END MakeABox;

 PROCEDURE MakeNest(nest: ObjPtr);
 BEGIN
  SetObjLoc(nest, 184, 132, 28, 28);
  SetObjRect(nest, 3, 3, 25, 25)
 END MakeNest;

 PROCEDURE MakeGrid(grid: ObjPtr);
 BEGIN
  SetObjLoc(grid, 96, 96, 32, 32);
  SetObjRect(grid, 2, 2, 30, 30)
 END MakeGrid;

 PROCEDURE MakeMissile(missile: ObjPtr);
  VAR
   px: INT16;
 BEGIN
  WITH missile^ DO hitSubLife:= 1; fireSubLife:= 1 END;
  IF missile^.stat = 0 THEN px:= 84 ELSE px:= 64 END;
  SetObjLoc(missile, px, 100, 12, 24);
  SetObjRect(missile, 0, 0, 12, 24)
 END MakeMissile;

 PROCEDURE MakePopUp(popup: ObjPtr);
  VAR
   sz: INT16;
 BEGIN
  IF popup^.stat = 2 THEN sz:= 0 ELSE sz:= 12 END;
  SetObjLoc(popup, 52, 200, sz, sz);
  SetObjRect(popup, 0, 0, sz, sz)
 END MakePopUp;

 PROCEDURE MakeGhost(ghost: ObjPtr);
 BEGIN
  SetObjLoc(ghost, 120, 180, 20, 20);
  SetObjRect(ghost, 0, 0, 20, 20)
 END MakeGhost;

 PROCEDURE ResetAlienV(alien: ObjPtr);
  VAR
   nvx, nvy: INT16;
   time: CARD16;
 BEGIN
  WITH alien^ DO
   IF pLife > 20 THEN time:= 60 ELSE time:= pLife * 3 END;
   time:= 80 - time;
   IF difficulty >= time THEN time:= 1 ELSE DEC(time, difficulty) END;
   IF displayed IN flags THEN
    moveSeq:= Period * (RND() MOD time + 2)
   ELSE
    moveSeq:= Period + RND() MOD (Period * 2)
   END;
   shapeSeq:= 0;
   hitSubLife:= life; fireSubLife:= life;
   nvx:= RND() MOD 2048; nvy:= RND() MOD 2048;
   dvx:= nvx - 1024; dvy:= nvy - 1024
  END;
  MakeAlienV(alien)
 END ResetAlienV;

 PROCEDURE ResetAlienA(alien: ObjPtr);
  VAR
   nax, nay: INT8;
   time: CARD16;
 BEGIN
  WITH alien^ DO
   IF pLife > 20 THEN time:= 20 ELSE time:= pLife END;
   time:= 26 - time;
   IF difficulty >= time THEN time:= 1 ELSE DEC(time, difficulty) END;
   IF displayed IN flags THEN
    moveSeq:= Period * (RND() MOD time + 2);
    IF shapeSeq > 3 THEN shapeSeq:= 0 ELSE INC(shapeSeq) END
   ELSE
    moveSeq:= Period * 2 + RND() MOD (Period * 2);
    shapeSeq:= 0
   END;
   hitSubLife:= life; fireSubLife:= life;
   nax:= RND() MOD 48; nay:= RND() MOD 48;
   ax:= nax - 24; ay:= nay - 24
  END;
  MakeAlienA(alien)
 END ResetAlienA;

 PROCEDURE ResetFour(four: ObjPtr);
  VAR
   time: CARD16;
 BEGIN
  WITH four^ DO
   IF pLife > 20 THEN time:= 20 ELSE time:= pLife END;
   time:= 31 - time; DEC(time, difficulty);
   IF displayed IN flags THEN
    moveSeq:= Period * (RND() MOD time + 1)
   ELSE
    moveSeq:= Period
   END;
   shapeSeq:= 0;
   hitSubLife:= life; fireSubLife:= life DIV 8 + 1;
   dvx:= RND() MOD 2048; DEC(dvx, 1024);
   dvy:= RND() MOD 2048; DEC(dvy, 1024)
  END;
  MakeFour(four)
 END ResetFour;

 PROCEDURE ResetQuad(quad: ObjPtr);
  VAR
   time: CARD16;
 BEGIN
  WITH quad^ DO
   shapeSeq:= 0;
   IF pLife > 20 THEN time:= 10 ELSE time:= pLife DIV 2 END;
   time:= 20 - time; DEC(time, difficulty DIV 2);
   IF displayed IN flags THEN
    moveSeq:= Period DIV 2 * (RND() MOD time + 1)
   ELSE
    moveSeq:= Period
   END;
   fireSubLife:= life; hitSubLife:= life DIV 8 + 1;
   dvx:= RND() MOD 2048; DEC(dvx, 1024);
   dvy:= RND() MOD 2048; DEC(dvy, 1024)
  END;
  MakeQuad(quad)
 END ResetQuad;

 PROCEDURE ResetCreatorR(creator: ObjPtr);
  VAR
   nvz: POINTER TO INT16;
   time: CARD16;
 BEGIN
  WITH creator^ DO
   time:= pLife + difficulty;
   IF time > 20 THEN time:= 20 END;
   time:= (23 - time) * 2;
   moveSeq:= Period * (RND() MOD time + 5);
   hitSubLife:= life; fireSubLife:= hitSubLife;
   dvx:= 0; dvy:= 0;
   IF RND() MOD 2 = 0 THEN nvz:= ADR(dvx) ELSE nvz:= ADR(dvy) END;
   IF RND() MOD 2 = 0 THEN nvz^:= 1024 ELSE nvz^:= -1024 END;
  END;
  MakeCreatorR(creator)
 END ResetCreatorR;

 PROCEDURE ResetCreatorC(creator: ObjPtr);
  VAR
   time: CARD16;
   angle: INT16;
 BEGIN
  WITH creator^ DO
   time:= pLife + difficulty;
   IF time > 20 THEN time:= 20 END;
   time:= (25 - time) * 2;
   moveSeq:= Period * (RND() MOD time + 3);
   stat:= 3;
   shapeSeq:= RND() MOD 15;
   hitSubLife:= life; fireSubLife:= hitSubLife;
   angle:= RND() MOD 360;
   dvx:= COS(angle); dvy:= SIN(angle)
  END;
  MakeCreatorC(creator)
 END ResetCreatorC;

 PROCEDURE ResetCircle(circle: ObjPtr);
 BEGIN
  WITH circle^ DO
   IF stat = 0 THEN
    IF pLife >= 30 THEN stat:= 0 ELSE stat:= 15 - pLife DIV 2 END
   END;
   moveSeq:= RND() MOD (Period * 2);
   dvx:= RND() MOD 4096; DEC(dvx, 2048);
   dvy:= RND() MOD 4096; DEC(dvy, 2048);
   IF NOT(displayed IN flags) THEN
    shapeSeq:= 0;
    MakeCircle(circle); stat:= 0
   END
  END
 END ResetCircle;

 PROCEDURE ResetController(ctrl: ObjPtr);
 BEGIN
  huCnt:= 4;
  SetObjXY(ctrl, -PW, -PH);
  WITH ctrl^ DO
   hitSubLife:= 0; fireSubLife:= 0;
   SetObjLoc(ctrl, 0, 0, 0, 0);
   SetObjRect(ctrl, 0, 0, 0, 0);
    (* moveSeq: time down to next hurryup *)
    (* shapeSeq: time down to next meteorite *)
  (* tell MakeController to reinit both values: *)
   stat:= 1;
   moveSeq:= 0;
   shapeSeq:= 0;
  END;
  MakeController(ctrl)
 END ResetController;

 PROCEDURE ResetHurryUp(hu: ObjPtr);
 BEGIN
  WITH hu^ DO
   moveSeq:= 0;
   stat:= Period * 3
  END;
  MakeHurryUp(hu)
 END ResetHurryUp;

 TYPE
  ChiefData = RECORD
   dstx, dsty, tox, toy, crx, cry, angle: INT16;
   dir, used: BOOLEAN;
  END;

 VAR
  chiefData: ARRAY[1..4] OF ChiefData;

 PROCEDURE ResetChief(chief: ObjPtr);
  VAR
   c: CARD16;
 BEGIN
  FOR c:= 1 TO 4 DO chiefData[c].used:= FALSE END;
  WITH chief^ DO
   stat:= 0;
   shapeSeq:= 0;
   hitSubLife:= life DIV 2;
   fireSubLife:= life - hitSubLife
  END;
  MakeChief(chief)
 END ResetChief;

 PROCEDURE ResetABox(abox: ObjPtr);
 BEGIN
  WITH abox^ DO
   dvx:= 0; dvy:= 0;
   stat:= life;
   life:= difficulty * 10;
   hitSubLife:= 50;
   fireSubLife:= 50
  END;
  MakeABox(abox)
 END ResetABox;

 PROCEDURE ResetNest(nest: ObjPtr);
 BEGIN
  WITH nest^ DO
   stat:= life;
   life:= 80 + difficulty * 10;
   hitSubLife:= life;
   fireSubLife:= life;
   moveSeq:= RND() MOD (Period * 10)
  END;
  MakeNest(nest)
 END ResetNest;

 PROCEDURE ResetGrid(grid: ObjPtr);
 BEGIN
  WITH grid^ DO
   hitSubLife:= life; fireSubLife:= life;
   stat:= Period * (RND() MOD 10 + 5);
   dvx:= RND() MOD 1024 + 1024; ax:= 0;
   dvy:= RND() MOD 1024 + 1024; ay:= 0;
   shapeSeq:= RND() MOD 64 + 12;
   moveSeq:= RND() MOD 64 + 12
  END;
  MakeGrid(grid)
 END ResetGrid;

 PROCEDURE ResetPopUp(popup: ObjPtr);
 BEGIN
  WITH popup^ DO
   hitSubLife:= life; fireSubLife:= life;
   stat:= 2; dvx:= 0; dvy:= 0
  END;
  MakePopUp(popup)
 END ResetPopUp;

 PROCEDURE ResetGhost(ghost: ObjPtr);
 BEGIN
  WITH ghost^ DO
   hitSubLife:= 0; fireSubLife:= life;
   moveSeq:= 0
  END;
  MakeGhost(ghost)
 END ResetGhost;

 PROCEDURE FireMissileV(src: ObjPtr; sk: CARD8);
  VAR
   missile: ObjPtr;
   rx, ry, rl: INT32;
   px, py, dx, dy, nvx, nvy: INT16;
 BEGIN
  IF sleeper <> 0 THEN RETURN END;
  SoundEffect(src, missileEffect);
  GetCenter(src, px, py);
  GetCenter(mainPlayer, dx, dy);
  rx:= dx - px; ry:= dy - py;
  rl:= SQRT(rx * rx + ry * ry);
  IF rl = 0 THEN rl:= 1 END;
  nvx:= rx * 2048 DIV rl;
  nvy:= ry * 2048 DIV rl;
  missile:= CreateObj(MISSILE, mAlien1, px, py, sk, 8 + sk * 2);
  SetObjVXY(missile, nvx, nvy)
 END FireMissileV;

 PROCEDURE FireMissileA(src: ObjPtr; ox, oy: INT16; snd: BOOLEAN);
  VAR
   missile: ObjPtr;
   rx, ry, rl: INT32;
   px, py, dx, dy, nvx, nvy: INT16;
   nax, nay: INT8;
   sk: CARD8;
 BEGIN
  IF sleeper <> 0 THEN RETURN END;
  IF snd THEN SoundEffect(src, missileEffect) END;
  GetCenter(src, px, py);
  INC(px, ox); INC(py, oy);
  GetCenter(mainPlayer, dx, dy);
  rx:= dx - px; ry:= dy - py;
  rl:= SQRT(rx * rx + ry * ry);
  IF rl = 0 THEN rl:= 1 END;
  WITH mainPlayer^ DO nvx:= dvx; nvy:= dvy END;
  nax:= rx * 64 DIV rl;
  nay:= ry * 64 DIV rl;
  IF water THEN sk:= mWhite ELSE sk:= mRed END;
  missile:= CreateObj(MISSILE, mAlien1, px, py, sk, 10);
  SetObjVXY(missile, nvx, nvy);
  WITH missile^ DO ax:= nax; ay:= nay END
 END FireMissileA;

 PROCEDURE MoveAlienAV(alien: ObjPtr);
 BEGIN
  IF OutOfScreen(alien) THEN Leave(alien); RETURN END;
  UpdateXY(alien);
  LimitSpeed(alien, 1024);
  AvoidBounds(alien, 4);
  AvoidBackground(alien, 4);
  Burn(alien);
  WITH alien^ DO
   INCL(flags, displayed);
   IF (moveSeq >= Period) AND (step > moveSeq - Period) THEN
    DEC(moveSeq, step);
    dvx:= 0; dvy:= 0;
    ax:= 0; ay:= 0;
    attr^.Make(alien)
   ELSIF step > moveSeq THEN
     (* Fire *)
    IF shapeSeq = 0 THEN
     IF subKind = cAlienV THEN
      FireMissileV(alien, mRed)
     ELSIF subKind = cQuad THEN
      FireMissileV(alien, mBlue)
     ELSIF subKind = cAlienA THEN
      FireMissileA(alien, 0, 0, TRUE)
     ELSE
      FireMissileA(alien, -6, -6, FALSE);
      FireMissileA(alien, -6, 6, FALSE);
      FireMissileA(alien, 6, -6, FALSE);
      FireMissileA(alien, 6, 6, TRUE)
     END
    END;
    attr^.Reset(alien)
   ELSE
    DEC(moveSeq, step)
   END;
   PlayerCollision(alien, life);
   IF life = 0 THEN Die(alien) END
  END
 END MoveAlienAV;

 PROCEDURE MoveCreatorR(creator: ObjPtr);
  VAR
   alien: ObjPtr;
   px, py: INT16;
   max, random: CARD16;
   nKind: Anims;
   sKind: CARD8;
 BEGIN
  IF OutOfScreen(creator) THEN Leave(creator); RETURN END;
  UpdateXY(creator);
  AvoidBounds(creator, 4);
  AvoidBackground(creator, 4);
  Burn(creator);
  WITH creator^ DO
   IF step > moveSeq THEN
    GetCenter(creator, px, py);
    IF difficulty < 4 THEN max:= difficulty ELSE max:= 3 END;
    IF pLife >= 10 THEN INC(max) END;
    IF pLife >= 20 THEN INC(max) END;
    random:= RND() MOD max;
    nKind:= ALIEN1; sKind:= aDbOval;
    IF random >= 1 THEN nKind:= ALIEN2; sKind:= cAlienV END;
    IF random >= 2 THEN sKind:= cAlienA END;
    IF ((nbDollar >= 200) OR (nbSterling >= 180)) AND (RND() MOD 2 = 0) THEN
     nKind:= ALIEN2; sKind:= cGhost
    ELSIF (pLife >= 25) AND (RND() MOD 2 = 0) THEN
     nKind:= ALIEN2; sKind:= cChief
    END;
    IF (difficulty >= 4) AND (RND() MOD 4 = 0) THEN
     nKind:= ALIEN1;
     IF RND() MOD 2 = 0 THEN sKind:= aBumper ELSE sKind:= aTri END
    ELSIF (difficulty >= 8) AND (RND() MOD 4 = 0) THEN
     nKind:= ALIEN2; sKind:= cCircle
    ELSIF (difficulty >= 8) AND (RND() MOD 3 = 0) THEN
     nKind:= ALIEN2; sKind:= cNest;
     IF (pLife >= 25) AND (nbAnim[ALIEN3] = 0) THEN nKind:= ALIEN3; sKind:= 1 END
    ELSIF (difficulty >= 10) AND (pLife >= 10) AND (nbAnim[ALIEN3] = 0) THEN
     nKind:= ALIEN3; sKind:= 0
    END;
    SoundEffect(creator, hurryupEffect);
    alien:= CreateObj(nKind, sKind, px, py, 0, pLife * 3);
    ResetCreatorR(creator)
   ELSIF moveSeq < Period * 3 THEN
    DEC(moveSeq, step);
    dvx:= 0; dvy:= 0
   ELSE
    IF ABS(vx) + ABS(vy) > 1024 THEN
     dvx:= vx; dvy:= vy
    END;
    DEC(moveSeq, step)
   END;
   PlayerCollision(creator, life);
   IF life = 0 THEN Die(creator) END
  END
 END MoveCreatorR;

 PROCEDURE MoveCreatorC(creator: ObjPtr);
  VAR
   alien: ObjPtr;
   px, py, angle: INT16;
   nKind: Anims;
   sKind: CARD8;
 BEGIN
  IF OutOfScreen(creator) THEN Leave(creator); RETURN END;
  UpdateXY(creator);
  AvoidBounds(creator, 4);
  AvoidBackground(creator, 4);
  Burn(creator);
  WITH creator^ DO
   IF step > moveSeq THEN
    SoundEffect(creator, hurryupEffect);
    GetCenter(creator, px, py);
    nKind:= ALIEN1;
    IF (difficulty >= 6) AND (stat = 0) AND (RND() MOD 3 = 0) THEN
     nKind:= ALIEN2; sKind:= cChief
    ELSIF (stages = 0) AND (nbDollar = 200) AND (RND() MOD 4 = 0) THEN
     sKind:= aTrefle
    ELSIF (difficulty = 10) AND (stat = 0) AND (RND() MOD 2 = 0) THEN
     IF RND() MOD 4 = 0 THEN
      sKind:= aSquare
     ELSIF RND() MOD 2 = 0 THEN
      sKind:= aBig
     ELSE
      nKind:= ALIEN2; sKind:= cAlienBox
     END
    ELSE
     sKind:= aDiese
    END;
    alien:= CreateObj(nKind, sKind, px, py, 0, pLife * 3);
    angle:= stat * 90 + shapeSeq;
    SetObjVXY(alien, COS(angle) * 3, SIN(angle) * 3);
    IF stat = 0 THEN
     ResetCreatorC(creator)
    ELSE
     moveSeq:= Period DIV 4;
     DEC(stat)
    END
   ELSIF moveSeq < Period * 3 THEN
    DEC(moveSeq, step);
    dvx:= 0; dvy:= 0
   ELSE
    IF ABS(vx) + ABS(vy) > 1024 THEN
     dvx:= vx; dvy:= vy
    END;
    DEC(moveSeq, step);
    IF moveSeq < Period * 3 THEN MakeCreatorC(creator) END
   END;
   PlayerCollision(creator, life);
   IF life = 0 THEN Die(creator) END
  END
 END MoveCreatorC;

 PROCEDURE BoumMissile(obj: ObjPtr; force: BOOLEAN);
  VAR
   missile: ObjPtr;
   c: CARD16;
   nvx, nvy, px, py, angle: INT16;
   sk: CARD8;
 BEGIN
  IF NOT(force) AND (difficulty < 9) THEN RETURN END;
  SoundEffect(obj, circleFireEffect);
  GetCenter(obj, px, py);
  angle:= RND() MOD 32;
  FOR c:= 1 TO 20 DO
   nvx:= COS(angle) * 3 DIV 2;
   nvy:= SIN(angle) * 3 DIV 2;
   IF obj^.subKind = cPopUp THEN sk:= c MOD 5 ELSE sk:= mBlue END;
   missile:= CreateObj(MISSILE, mAlien1, px, py, sk, 9);
   SetObjVXY(missile, nvx, nvy);
   INC(angle, 18)
  END
 END BoumMissile;

 PROCEDURE MoveCircle(circle: ObjPtr);
  VAR
   hit: CARD16;
 BEGIN
  IF OutOfScreen(circle) THEN Leave(circle); RETURN END;
  UpdateXY(circle);
  AvoidBounds(circle, 3);
  AvoidBackground(circle, 3);
  Burn(circle);
  WITH circle^ DO
   INCL(flags, displayed);
   IF step >= shapeSeq THEN
    IF shapeSeq > 0 THEN
     DEC(life);
     IF life = 0 THEN Die(circle); RETURN END;
     shapeSeq:= 0; MakeCircle(circle)
    END;
    shapeSeq:= 0;
    hitSubLife:= 40; fireSubLife:= 40
   ELSE
    DEC(shapeSeq, step)
   END;
   IF step > moveSeq THEN
    IF stat <= 1 THEN
     stat:= 0;
     BoumMissile(circle, TRUE)
    ELSE
     DEC(stat)
    END;
    ResetCircle(circle)
   ELSE
    DEC(moveSeq, step)
   END;
   hit:= 50;
   PlayerCollision(circle, hit)
  END
 END MoveCircle;

 PROCEDURE MoveController(ctrl: ObjPtr);
 BEGIN
  WITH ctrl^ DO
   IF step > moveSeq THEN moveSeq:= 0 ELSE DEC(moveSeq, step) END;
   IF step > shapeSeq THEN shapeSeq:= 0 ELSE DEC(shapeSeq, step) END;
   IF (moveSeq = 0) OR (shapeSeq = 0) THEN MakeController(ctrl) END;
   IF (nbAnim[ALIEN3] + nbAnim[ALIEN2] + nbAnim[ALIEN1] <= 1) AND
      ((zone = Chaos) OR (zone = Family)) THEN Die(ctrl) END
  END
 END MoveController;

 PROCEDURE MoveHurryUp(hu: ObjPtr);
  VAR
   missile: ObjPtr;
   px, py, nay: INT16;
 BEGIN
  UpdateXY(hu);
  Burn(hu);
  WITH hu^ DO
   IF OutOfBounds(hu) THEN
    IF stat = 0 THEN stat:= 7; Die(hu); RETURN END;
    hitSubLife:= 0; fireSubLife:= 0
   ELSE
    hitSubLife:= life;
    fireSubLife:= life;
    IF step > moveSeq THEN
     SoundEffect(hu, missileEffect);
     GetCenter(hu, px, py);
     IF py > gameHeight DIV 2 THEN nay:= -40 ELSE nay:= 40 END;
     missile:= CreateObj(MISSILE, mAlien1, px, py, mGreen, 8);
     SetObjAXY(missile, 0, nay);
     moveSeq:= RND() MOD Period + Period DIV 4 - step
    ELSE
     DEC(moveSeq, step)
    END
   END;
   IF step > stat THEN stat:= 0 ELSE DEC(stat, step) END;
   ax:= 0; ay:= 0;
   IF vx > 0 THEN
    dvx:= 800; dvy:= 0
   ELSIF vx < 0 THEN
    dvx:= -800; dvy:= 0
   ELSE
    dvx:= 0; dvy:= 800
   END;
   PlayerCollision(hu, life);
   IF life = 0 THEN Die(hu) END
  END
 END MoveHurryUp;

 PROCEDURE MoveChief(chief: ObjPtr);
  VAR
   new: ObjPtr;
   lx, ly, dl, speed: INT32;
   px, py, mx, my, dx, dy, nvx: INT16;
   rnd, sKind, nStat, hit, fire: CARD16;
   nKind: Anims;
   bg: BOOLEAN;
 BEGIN
  UpdateXY(chief);
  Burn(chief);
  nvx:= 5 + difficulty; speed:= nvx;
  LimitSpeed(chief, nvx * 256);
  AvoidBounds(chief, 0);
  bg:= InBackground(chief);
  IF bg THEN AvoidBackground(chief, 0) END;
  WITH chief^ DO
   IF stat = 0 THEN
     (* Follow player *)
    GetCenter(chief, mx, my);
      (* fire ? *)
    IF step > shapeSeq THEN
     INC(shapeSeq, Period * (1 + RND() MOD 8));
     rnd:= RND() MOD 3;
     IF (stages = 0) AND (RND() MOD 4 = 0) THEN BoumMissile(chief, TRUE) END;
     IF rnd = 0 THEN
      FireMissileV(chief, RND() MOD 4)
     ELSIF rnd = 1 THEN
      FireMissileA(chief, 0, 0, TRUE)
     ELSE
      nStat:= RND() MOD 2;
      IF nStat = 0 THEN nvx:= 2700 ELSE nvx:= -2700 END;
      IF rnd = 2 THEN
       nKind:= ALIEN2; sKind:= cMissile
      ELSE
       nKind:= ALIEN1; sKind:= aFlame; INC(my, 16)
      END;
      SoundEffect(chief, hurryupEffect);
      new:= CreateObj(nKind, sKind, mx, my, nStat, pLife);
      SetObjVXY(new, nvx, 0)
     END
    END;
    IF NOT OutOfScreen(chief) THEN DEC(shapeSeq, step) END;
      (* player *)
    GetCenter(mainPlayer, px, py);
    dx:= px - mx; dy:= py - my;
    lx:= dx; ly:= dy;
    dl:= SQRT(lx * lx + ly * ly);
    IF dl <> 0 THEN
     dvx:= lx * 128 DIV dl * speed;
     dvy:= ly * 128 DIV dl * speed
    END;
    IF dl < 56 THEN
     dvx:= -dvx; dvy:= -dvy
    ELSIF dl < 64 THEN
     dvx:= 0; dvy:= 0
    END;
    IF bg AND (ABS(vx) < 400) AND (ABS(vy) < 400) THEN
     moveSeq:= 4;
     WHILE (moveSeq > 0) AND chiefData[moveSeq].used DO
      DEC(moveSeq)
     END;
     IF moveSeq <> 0 THEN
      WITH chiefData[moveSeq] DO
       IF ABS(dx) > ABS(dy) THEN
        IF dx > 0 THEN angle:= 0 ELSE angle:= 180 END;
        dir:= SGN(dx) <> SGN(dy)
       ELSE
        IF dy > 0 THEN angle:= 90 ELSE angle:= 270 END;
        dir:= SGN(dx) = SGN(dy)
       END;
       IF dir THEN angle:= (angle - 90) MOD 360 ELSE angle:= (angle + 90) MOD 360 END;
       dstx:= mx DIV BW; dsty:= my DIV BH;
       crx:= dstx; cry:= dsty;
       tox:= crx; toy:= cry;
       px:= px DIV BW; py:= py DIV BH;
       LOOP
        IF (dstx = px) AND (dsty = py) THEN EXIT END;
        IF ABS(px - dstx) > ABS(py - dsty) THEN
         IF px > dstx THEN INC(dstx) ELSE DEC(dstx) END
        ELSE
         IF py > dsty THEN INC(dsty) ELSE DEC(dsty) END
        END;
        IF (dstx < 0) THEN dstx:= 0 END;
        IF (dsty < 0) THEN dsty:= 0 END;
        stat:= 1; used:= TRUE;
        IF castle^[dsty, dstx] < NbBackground THEN EXIT END
       END
      END
     END
    END
   ELSE (* stat = 1 *)
    WITH chiefData[moveSeq] DO
     IF gameStat <> Playing THEN used:= FALSE END;
     GetCenter(chief, mx, my);
     px:= tox * BW + BW DIV 2;
     py:= toy * BH + BH DIV 2;
     lx:= px - mx; ly:= py - my;
     IF (ABS(lx) >= 64) OR (ABS(ly) >= 64) THEN
      stat:= 0; used:= FALSE; dl:= 0
     ELSE
      dl:= SQRT(lx * lx + ly * ly)
     END;
     IF dl <> 0 THEN
      dvx:= lx * 128 DIV dl * speed;
      dvy:= ly * 128 DIV dl * speed
     END;
     IF ABS(dl) <= 16 THEN
      crx:= tox; cry:= toy; rnd:= 6;
      IF dir THEN angle:= (angle + 90) MOD 360 ELSE angle:= (angle - 90) MOD 360 END;
      LOOP
       tox:= crx + COS(angle) DIV 1024;
       toy:= cry + SIN(angle) DIV 1024;
       IF (tox = 0) OR (toy = 0) OR (tox = castleWidth - 1) OR (toy = castleHeight - 1) THEN
        dir:= NOT(dir)
       END;
       IF (tox >= 0) AND (toy >= 0) AND (tox < castleWidth) AND
          (toy < castleHeight) AND (castle^[toy, tox] < NbBackground) THEN
        EXIT
       END;
       DEC(rnd); IF rnd = 0 THEN stat:= 0; used:= FALSE; EXIT END;
       IF dir THEN angle:= (angle - 90) MOD 360 ELSE angle:= (angle + 90) MOD 360 END
      END;
      IF (crx = dstx) AND (cry = dsty) THEN
       stat:= 0; used:= FALSE
      END
     END
    END
   END;
   dx:= dvx; dy:= dvy;
   AvoidAnims(chief, AnimSet{WEAPON});
   IF ((dx <> dvx) OR (dy <> dvy)) AND (stat = 1) THEN
    stat:= 0; chiefData[moveSeq].used:= FALSE
   END;
   Gravity(chief, AnimSet{MISSILE});
   hit:= life DIV 2; fire:= life - hit;
   DoCollision(chief, AnimSet{PLAYER, ALIEN1}, Aie, hit, fire);
   IF life = 0 THEN Die(chief) END
  END
 END MoveChief;

 PROCEDURE MoveABox(abox: ObjPtr);
  VAR
   hit: CARD16;
 BEGIN
  IF OutOfScreen(abox) THEN Leave(abox); RETURN END;
  UpdateXY(abox);
  AvoidBounds(abox, 3);
  AvoidBackground(abox, 3);
  Burn(abox);
  hit:= 5;
  PlayerCollision(abox, hit)
 END MoveABox;

 PROCEDURE MoveNest(nest: ObjPtr);
  VAR
   new: ObjPtr;
   px, py, nvx: INT16;
   time, nstat, nlife, rnd: CARD16;
   nkind: Anims;
   nsk: CARD8;
 BEGIN
  IF OutOfScreen(nest) THEN Leave(nest); RETURN END;
  WITH nest^ DO
   vx:= 0; vy:= 0; dvx:= 0; dvy:= 0;
   UpdateXY(nest);
   Burn(nest);
   IF step > moveSeq THEN
    nlife:= pLife * 2;
    IF stat = 1 THEN
     nkind:= ALIEN2; nsk:= cMissile;
     nstat:= RND() MOD 2
    ELSE
     rnd:= RND() MOD 64;
     IF (rnd = 0) AND (stages = 0) THEN
      nkind:= ALIEN2; nsk:= cHurryUp
     ELSIF rnd < 10 THEN
      IF (difficulty >= 7) AND ODD(rnd) THEN
       nkind:= ALIEN1; nsk:= aBig
      ELSE
       nkind:= ALIEN2;
       IF RND() MOD 2 = 0 THEN nsk:= cFour ELSE nsk:= cQuad END
      END
     ELSE
      nkind:= ALIEN1;
      IF (difficulty >= 3) AND ODD(rnd) THEN
       nsk:= aTri
      ELSE
       nsk:= aColor; nlife:= difficulty * 6 + RND() MOD 32
      END
     END
    END;
    GetCenter(nest, px, py);
    new:= CreateObj(nkind, nsk, px, py, nstat, nlife);
    IF stat = 1 THEN
     IF nstat = 0 THEN nvx:= 3000 ELSE nvx:= -3000 END;
     SetObjVXY(new, nvx, 0)
    END;
    SoundEffect(nest, hurryupEffect);
    IF pLife > 20 THEN time:= 10 ELSE time:= pLife DIV 2 END;
    time:= 15 - time - difficulty DIV 3;
    INC(moveSeq, Period * (RND() MOD time + 1))
   END;
   DEC(moveSeq, step)
  END
 END MoveNest;

 PROCEDURE MoveGrid(grid: ObjPtr);
  VAR
   ovx, ovy: INT16;
 BEGIN
  IF OutOfScreen(grid) THEN Leave(grid); RETURN END;
  WITH grid^ DO
   ovx:= ABS(dvx); ovy:= ABS(dvy);
   UpdateXY(grid);
   AvoidBounds(grid, 4);
   AvoidBackground(grid, 4);
   dvx:= ovx; dvy:= ovy;
   IF vx >= dvx THEN ax:= shapeSeq; ax:= -ax ELSIF -vx >= dvx THEN ax:= shapeSeq END;
   IF vy >= dvy THEN ay:= moveSeq; ay:= -ay ELSIF -vy >= dvy THEN ay:= moveSeq END;
   IF step > stat THEN ResetGrid(grid) END;
   DEC(stat, step);
   PlayerCollision(grid, life);
   IF life = 0 THEN Die(grid) END
  END
 END MoveGrid;

 PROCEDURE MoveMissile(missile: ObjPtr);
 BEGIN
  IF OutOfScreen(missile) THEN Leave(missile); RETURN END;
  UpdateXY(missile);
  IF OutOfBounds(missile) OR InBackground(missile) THEN Die(missile) END
 END MoveMissile;

 PROCEDURE MovePopUp(popup: ObjPtr);
  VAR
   missile: ObjPtr;
   px, py, mx, my, nvx, nvy, angle: INT16;
 BEGIN
  IF OutOfScreen(popup) THEN Leave(popup); RETURN END;
  UpdateXY(popup);
  AvoidBackground(popup, 1);
  AvoidBounds(popup, 1);
  WITH popup^ DO
   dvx:= 0; dvy:= 0;
   IF stat = 2 THEN
    GetCenter(mainPlayer, px, py);
    GetCenter(popup, mx, my);
    IF (ABS(px - mx) < 130) AND (ABS(py - my) < 130) THEN
     stat:= 1;
     SoundEffect(popup, popupEffect);
     moveSeq:= Period * 3 DIV 2;
     MakePopUp(popup)
    END
   ELSE
    IF step > moveSeq THEN
     IF stat = 1 THEN
      GetCenter(popup, px, py);
      SoundEffect(popup, missileEffect);
      FOR angle:= 0 TO 270 BY 90 DO
       nvx:= COS(angle) * 2;
       nvy:= SIN(angle) * 2;
       missile:= CreateObj(MISSILE, mAlien1, px, py, mRed, 10);
       SetObjVXY(missile, nvx, nvy)
      END;
      stat:= 0;
      INC(moveSeq, Period * 3 DIV 2)
     ELSE
      Die(popup); RETURN
     END
    END;
    DEC(moveSeq, step)
   END
  END
 END MovePopUp;

 PROCEDURE RemMoney(player, ghost: ObjPtr; VAR hit, fire: CARD16);
  VAR
   md, ms: INT8;
 BEGIN
  ghost^.moveSeq:= Period DIV 2;
  IF nbDollar > 0 THEN md:= -1 ELSE md:= 0 END;
  IF nbSterling > 0 THEN ms:= -1 ELSE ms:= 0 END;
  IF (md <> 0) OR (ms <> 0) THEN AddMoney(player, md, ms) END
 END RemMoney;

 PROCEDURE MoveGhost(ghost: ObjPtr);
  VAR
   mx, my, px, py, speed: INT16;
 BEGIN
  IF OutOfScreen(ghost) THEN Leave(ghost); RETURN END;
  UpdateXY(ghost);
  AvoidBackground(ghost, 0);
  GetCenter(mainPlayer, px, py);
  GetCenter(ghost, mx, my);
  WITH ghost^ DO
   speed:= (800 + difficulty * 100);
   dvx:= SGN(px - mx) * speed;
   dvy:= SGN(py - my) * speed;
   IF moveSeq = 0 THEN
    DoCollision(ghost, AnimSet{PLAYER}, RemMoney, hitSubLife, fireSubLife)
   ELSIF step > moveSeq THEN
    moveSeq:= 0
   ELSE
    DEC(moveSeq, step)
   END
  END
 END MoveGhost;

 PROCEDURE AieAlienAV(alien, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  DecLife(alien, hit, fire);
  IF alien^.life > 0 THEN SoundEffect(alien, aieAlienEffect) END
 END AieAlienAV;

 PROCEDURE AieCreator(creator, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  DecLife(creator, hit, fire);
  SoundEffect(creator, aieCreatorEffect)
 END AieCreator;

 PROCEDURE AieCircle(circle, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  DecLife(circle, hit, fire);
  SoundEffect(circle, aieCircleEffect);
  WITH circle^ DO
   shapeSeq:= Period * 2;
   MakeCircle(circle);
   IF (life = 0) AND (zone = Chaos) THEN
    PopMessage(ADL("Get the money"), moneyPos, 2)
   END;
   INC(life);
   hitSubLife:= 0; fireSubLife:= 0
  END
 END AieCircle;

 PROCEDURE AieHurryUp(hu, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  DecLife(hu, hit, fire);
  SoundEffect(hu, aieHUEffect)
 END AieHurryUp;

 PROCEDURE AieChief(chief, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  DecLife(chief, hit, fire);
  SoundEffect(chief, aieChiefEffect)
 END AieChief;

 PROCEDURE AieABox(abox, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  DecLife(abox, hit, fire);
  SoundEffect(abox, aieCreatorEffect);
 END AieABox;

 PROCEDURE AieNest(nest, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  DecLife(nest, hit, fire);
  SoundEffect(nest, aieCreatorEffect)
 END AieNest;

 PROCEDURE AieMissile(missile, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  missile^.life:= 0
 END AieMissile;

 PROCEDURE AieGrid(grid, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  ResetGrid(grid);
  SoundEffect(grid, aieGridEffect);
  DecLife(grid, hit, fire)
 END AieGrid;

 PROCEDURE AiePopUp(popup, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  SoundEffect(popup, aieAlienEffect);
  DecLife(popup, hit, fire)
 END AiePopUp;

 PROCEDURE AieGhost(ghost, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  SoundEffect(ghost, aieGhostEffect);
  DecLife(ghost, hit, fire)
 END AieGhost;

 PROCEDURE DieAlienV(alien: ObjPtr);
 BEGIN
  INC(addpt);
  SoundEffect(alien, dieAlienVEffect);
  BoumMissile(alien, FALSE);
  IF NOT(nested IN alien^.flags) THEN BoumMoney(alien, MoneySet{m1, m2}, 2, 1) END
 END DieAlienV;

 PROCEDURE DieAlienA(alien: ObjPtr);
 BEGIN
  INC(addpt);
  SoundEffect(alien, dieAlienAEffect);
  BoumMissile(alien, FALSE);
  IF NOT(nested IN alien^.flags) THEN BoumMoney(alien, MoneySet{m2}, 1, 2) END
 END DieAlienA;

 PROCEDURE DieFour(four: ObjPtr);
 BEGIN
  BoumMissile(four, FALSE);
  IF four^.subKind = cQuad THEN
   BoumMoney(four, MoneySet{st}, 1, 1)
  ELSE
   INC(addpt)
  END;
  SoundEffect(four, dieFourEffect)
 END DieFour;

 PROCEDURE DieCreatorR(creator: ObjPtr);
  VAR
   diese: ObjPtr;
   px, py, angle, cnt, c: INT16;
   sKind: CARD8;
 BEGIN
  INC(addpt, pLife);
  IF difficulty >= 5 THEN
   IF difficulty >= 7 THEN cnt:= 5 ELSE cnt:= 3 END;
   GetCenter(creator, px, py);
   angle:= RND() MOD 360; c:= cnt;
   WHILE c > 0 DO
    IF (difficulty = 10) AND (RND() MOD 4 = 0) THEN
     sKind:= aFlame
    ELSE
     sKind:= aDiese
    END;
    diese:= CreateObj(ALIEN1, sKind, px, py, 0, pLife * 3);
    SetObjVXY(diese, COS(angle) * 3, SIN(angle) * 3);
    INC(angle, 360 DIV cnt); DEC(c)
   END
  END;
  IF (difficulty < 7) AND (zone = Chaos) THEN
   BoumMoney(creator, MoneySet{m1, m2, m5}, 3, 5)
  END;
  SoundEffect(creator, dieCreatorREffect)
 END DieCreatorR;

 PROCEDURE DieCreatorC(creator: ObjPtr);
 BEGIN
  SoundEffect(creator, dieCreatorCEffect);
  BoumMissile(creator, FALSE);
  INC(addpt, pLife)
 END DieCreatorC;

 PROCEDURE DieCircle(circle: ObjPtr);
  VAR
   chief: ObjPtr;
   px, py, angle, cnt, c: INT16;
 BEGIN
  IF (difficulty >= 9) THEN
   IF difficulty = 9 THEN cnt:= 2 ELSE cnt:= 3 END;
   angle:= RND() MOD 360; c:= cnt;
   GetCenter(circle, px, py);
   WHILE c > 0 DO
    chief:= CreateObj(ALIEN2, cChief, px, py, 0, pLife * 4);
    SetObjVXY(chief, COS(angle) * 4, SIN(angle) * 4);
    INC(angle, 360 DIV cnt); DEC(c)
   END
  END;
  INC(addpt, 10);
  SoundEffect(circle, dieCircleEffect);
  IF zone = Chaos THEN
   BoumMoney(circle, MoneySet{m5}, 1, 10 - difficulty)
  END
 END DieCircle;

 PROCEDURE DieHurryUp(hu: ObjPtr);
 BEGIN
  IF hu^.stat <> 7 THEN SoundEffect(hu, dieHUEffect) END
 END DieHurryUp;

 PROCEDURE DieChief(chief: ObjPtr);
 BEGIN
  WITH chief^ DO
   IF stat = 1 THEN chiefData[moveSeq].used:= FALSE END
  END;
  SoundEffect(chief, dieCircleEffect);
  INC(addpt, 8);
  BoumMissile(chief, FALSE);
  IF (zone = Castle) OR (zone = Chaos) THEN
   IF (chief^.attr^.nbObj = 1) AND NOT (nested IN chief^.flags) THEN
    BoumMoney(chief, MoneySet{m10}, 1, 5 - (difficulty + 5) DIV 3)
   END
  END;
 END DieChief;

 PROCEDURE DieABox(abox: ObjPtr);
  VAR
   new: ObjPtr;
   px, py: INT16;
   nstat, nlife, rnd, cnt: CARD16;
   nkind: Anims;
   nsk: CARD8;
 BEGIN
  SoundEffect(abox, dieABEffect);
  FOR cnt:= 1 TO 5 + difficulty DIV 3 DO
   rnd:= RND() MOD 64;
   nlife:= pLife; nkind:= ALIEN1; nstat:= 0;
   IF abox^.stat = 0 THEN nsk:= aStar ELSE nsk:= aBubble END;
   IF (difficulty >= 8) AND (rnd < 10) THEN nsk:= aKamikaze; nstat:= 1 END;
   IF (difficulty >= 10) AND (rnd >= 32) AND (cnt = 0) THEN
    nkind:= ALIEN2; nsk:= cCircle; nstat:= 0
   END;
   IF (difficulty >= 5) AND (rnd = 0) THEN nsk:= aHospital END;
   GetCenter(abox, px, py);
   new:= CreateObj(nkind, nsk, px, py, nstat, nlife)
  END
 END DieABox;

 PROCEDURE DieNest(nest: ObjPtr);
  VAR
   bonus: ObjPtr;
   px, py: INT16;
   bullets: CARD16;
   w: Weapon;
 BEGIN
  bullets:= 0;
  FOR w:= MIN(Weapon) TO MAX(Weapon) DO
   INC(bullets, weaponAttr[w].nbBullet)
  END;
  IF bullets < 100 + RND() MOD 200 THEN
   GetCenter(nest, px, py);
   bonus:= CreateObj(BONUS, TimedBonus, px, py, tbBullet, 1)
  END;
  SoundEffect(nest, dieNestEffect)
 END DieNest;

 PROCEDURE DieGrid(grid: ObjPtr);
 BEGIN
  BoumMoney(grid, MoneySet{st}, 1, 2);
  BoumMissile(grid, TRUE)
 END DieGrid;

 PROCEDURE DieMissile(missile: ObjPtr);
  VAR
   new: ObjPtr;
   px, py: INT16;
   nstat, nlife, rnd: CARD16;
   nkind: Anims;
   nsk: CARD8;
 BEGIN
  rnd:= RND() MOD 64;
  nstat:= 0; nlife:= difficulty * 6 + RND() MOD 32;
  nkind:= ALIEN1; nsk:= aColor;
  IF (difficulty >= 6) AND (rnd < 25) THEN
   nkind:= ALIEN2; nsk:= cGrid; nlife:= pLife * 2
  ELSIF (difficulty >= 7) AND (rnd < 50) THEN
   nkind:= ALIEN2; nsk:= cCreatorR; nlife:= (pLife + difficulty) * 4
  ELSIF (difficulty >= 8) AND (rnd > 50) THEN
   nsk:= aSquare; nlife:= pLife
  END;
  IF (difficulty >= 10) AND (rnd > 65 - pLife) THEN
   nlife:= (pLife + difficulty) * 3; nkind:= ALIEN2; nstat:= 1;
   IF rnd = 64 THEN nsk:= cHurryUp ELSE nsk:= cCreatorC END
  END;
  GetCenter(missile, px, py);
  new:= CreateObj(nkind, nsk, px, py, nstat, nlife)
 END DieMissile;

 PROCEDURE DiePopUp(popup: ObjPtr);
 BEGIN
  BoumMissile(popup, FALSE);
  SoundEffect(popup, diePopupEffect)
 END DiePopUp;

 PROCEDURE DieGhost(ghost: ObjPtr);
 BEGIN
  SoundEffect(ghost, dieGhostEffect)
 END DieGhost;

 PROCEDURE InitParams;
  VAR
   attr: ObjAttrPtr;
   c, v: INT16;
   snd: SoundList;
 BEGIN
  SetEffect(hurryupEffect[0], soundList[sHurryUp], 0, 0, 200, 1);
  SetEffect(missileEffect[0], soundList[sMissile], 0, 0, 110, 4);
  SetEffect(aieCircleEffect[0], soundList[sGong], 0, 16726, 200, 7);
  SetEffect(dieCircleEffect[0], soundList[sCasserole], 0, 0, 250, 9);
  SetEffect(aieAlienEffect[0], soundList[sCymbale], 0, 0, 140, 1);
  SetEffect(aieHUEffect[0], soundList[sHurryUp], 0, 16726, 180, 1);
  SetEffect(dieHUEffect[0], soundList[sCannon], 4500, 0, 170, 4);
  SetEffect(aieCreatorEffect[0], soundList[sHHat], 0, 4181, 180, 3);
  SetEffect(aieChiefEffect[0], soundList[sPoubelle], 0, 0, 220, 5);
  SetEffect(dieAlienVEffect[0], soundList[aCrash], 0, 0, 180, 4);
  SetEffect(dieAlienVEffect[1], soundList[wCrash], 582, 0, 180, 3);
  SetEffect(dieAlienAEffect[0], soundList[aCrash], 0, 8860, 180, 4);
  SetEffect(dieAlienAEffect[1], soundList[wCrash], 582, 7451, 180, 3);
  FOR c:= 2 TO 16 DO
   v:= (17 - c) * 11;
   SetEffect(dieAlienAEffect[c], nulSound, 582, 8860 - (c MOD 2) * 1409, v, 3);
   v:= v * (c MOD 2);
   SetEffect(dieAlienVEffect[c], nulSound, 582, 0, v, 3)
  END;
  FOR c:= 0 TO 23 DO
   v:= ABS(c MOD 4 - 2) + 1;
   SetEffect(dieCreatorREffect[c], soundList[wWhite], 700, 8363 + v * 2788, (24 - c) * v * 7 DIV 2, 5);
   SetEffect(dieNestEffect[c], soundList[wNoise], 700, 8363 + v * 2788, (24 - c) * v * 3, 5)
  END;
  v:= 0;
  FOR c:= 0 TO 7 DO
   SetEffect(dieCreatorCEffect[c], soundList[wWhite], 0, 5575 + 995 * v, (8 - c) * 24, 5);
   v:= (v * 9 + 5) MOD 8
  END;
  SetEffect(dieFourEffect[0], soundList[aCrash], 0, 0, 192, 4);
  FOR c:= 1 TO 8 DO
   SetEffect(dieFourEffect[c], soundList[wCrash], 1500, 8363, (9 - c) * 24, 4)
  END;
  FOR c:= 0 TO 11 DO
   v:= c MOD 3;
   IF v = 0 THEN snd:= wNoise ELSIF v = 1 THEN snd:= wWhite ELSE snd:= wCrash END;
   SetEffect(dieABEffect[c], soundList[snd], 836, 8363, (12 - c) * 16, 5)
  END;
  SetEffect(circleFireEffect[0], soundList[sPouf], 5500, 0, 190, 6);
  SetEffect(popupEffect[0], soundList[wVoice], 929, 8363, 100, 4);
  SetEffect(popupEffect[1], soundList[wVoice], 1394, 12544, 100, 4);
  SetEffect(popupEffect[2], soundList[wVoice], 1162, 10454, 100, 4);
  SetEffect(diePopupEffect[0], soundList[sCymbale], 0, 4181, 100, 4);
  SetEffect(aieGridEffect[0], soundList[wCrash], 836, 16726, 130, 2);
  SetEffect(aieGridEffect[1], soundList[wCrash], 627, 12544, 130, 2);
  SetEffect(aieGridEffect[2], soundList[wCrash], 523, 10454, 130, 2);
  SetEffect(aieGridEffect[3], soundList[wCrash], 418, 8363, 130, 2);
  SetEffect(aieGhostEffect[0], soundList[sHurryUp], 0, 4181, 100, 1);
  SetEffect(dieGhostEffect[0], soundList[wWhite], 0, 0, 160, 4);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetAlienV;
   Make:= MakeAlienV;
   Move:= MoveAlienAV;
   Aie:= AieAlienAV;
   Die:= DieAlienV;
   charge:= 40; weight:= 24;
   inerty:= 60; priority:= -58;
   heatSpeed:= 40;
   refreshSpeed:= 40;
   coolSpeed:= 25;
   aieStKinds:= StoneSet{stC35}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= gravityStyle;
   dieStKinds:= StoneSet{stC35}; dieSKCount:= 1;
   dieStone:= 7; dieStStyle:= gravityStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetAlienA;
   Make:= MakeAlienA;
   Move:= MoveAlienAV;
   Aie:= AieAlienAV;
   Die:= DieAlienA;
   charge:= 30; weight:= 48;
   inerty:= 60; priority:= -56;
   heatSpeed:= 25;
   refreshSpeed:= 25;
   coolSpeed:= 35;
   aieStKinds:= StoneSet{stC35}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= gravityStyle;
   dieStKinds:= StoneSet{stC35}; dieSKCount:= 1;
   dieStone:= 8; dieStStyle:= gravityStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetCreatorR;
   Make:= MakeCreatorR;
   Move:= MoveCreatorR;
   Aie:= AieCreator;
   Die:= DieCreatorR;
   charge:= 16; weight:= 100;
   inerty:= 14; priority:= -70;
   heatSpeed:= 80;
   refreshSpeed:= 90;
   coolSpeed:= 150;
   aieStKinds:= StoneSet{}; aieSKCount:= 0;
   aieStone:= FlameMult; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stCE}; dieSKCount:= 1;
   dieStone:= 10; dieStStyle:= gravityStyle;
   basicType:= Vegetal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetCircle;
   Make:= MakeCircle;
   Move:= MoveCircle;
   Aie:= AieCircle;
   Die:= DieCircle;
   charge:= 16; weight:= 100;
   inerty:= 256; priority:= -65;
   heatSpeed:= 20;
   refreshSpeed:= 40;
   coolSpeed:= 120;
   aieStKinds:= StoneSet{stFOG2}; aieSKCount:= 1;
   aieStone:= 10; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stFOG4}; dieSKCount:= 1;
   dieStone:= FlameMult * 4 + 20; dieStStyle:= fastStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetCreatorC;
   Make:= MakeCreatorC;
   Move:= MoveCreatorC;
   Aie:= AieCreator;
   Die:= DieCreatorC;
   charge:= 24; weight:= 80;
   inerty:= 16; priority:= -70;
   heatSpeed:= 60;
   refreshSpeed:= 60;
   coolSpeed:= 75;
   aieStKinds:= StoneSet{}; aieSKCount:= 0;
   aieStone:= FlameMult; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stRE}; dieSKCount:= 1;
   dieStone:= 10; dieStStyle:= slowStyle;
   basicType:= Vegetal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetController;
   Move:= MoveController;
   basicType:= NotBase
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetHurryUp;
   Make:= MakeHurryUp;
   Move:= MoveHurryUp;
   Aie:= AieHurryUp;
   Die:= DieHurryUp;
   charge:= 12; weight:= 80;
   inerty:= 32; priority:= -68;
   heatSpeed:= 30;
   refreshSpeed:= 60;
   coolSpeed:= 5;
   aieStKinds:= StoneSet{stCROSS}; aieSKCount:= 1;
   aieStone:= 1; aieStStyle:= fastStyle;
   dieStKinds:= StoneSet{stCROSS}; dieSKCount:= 1;
   dieStone:= 12; dieStStyle:= gravityStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetChief;
   Make:= MakeChief;
   Move:= MoveChief;
   Aie:= AieChief;
   Die:= DieChief;
   charge:= 24; weight:= 75;
   inerty:= 300; priority:= -52;
   heatSpeed:= 0;
   refreshSpeed:= 10;
   coolSpeed:= 4;
   aieStKinds:= StoneSet{stCROSS}; aieSKCount:= 1;
   aieStone:= 7; aieStStyle:= fastStyle;
   dieStKinds:= StoneSet{stFOG4}; dieSKCount:= 1;
   dieStone:= 25; dieStStyle:= slowStyle;
   basicType:= Vegetal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetFour;
   Make:= MakeFour;
   Move:= MoveAlienAV;
   Aie:= AieAlienAV;
   Die:= DieFour;
   charge:= 15; weight:= 64;
   inerty:= 50; priority:= -56;
   heatSpeed:= 70;
   refreshSpeed:= 70;
   coolSpeed:= 35;
   aieStKinds:= StoneSet{stC35}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= fastStyle;
   dieStKinds:= StoneSet{stC35}; dieSKCount:= 1;
   dieStone:= 8; dieStStyle:= slowStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetABox;
   Make:= MakeABox;
   Move:= MoveABox;
   Aie:= AieABox;
   Die:= DieABox;
   charge:= 0; weight:= 100;
   inerty:= 10; priority:= -65;
   heatSpeed:= 200;
   refreshSpeed:= 90;
   coolSpeed:= 100;
   aieStKinds:= StoneSet{}; aieSKCount:= 0;
   aieStone:= FlameMult; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stRE}; dieSKCount:= 1;
   dieStone:= 7; dieStStyle:= slowStyle;
   basicType:= Vegetal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetNest;
   Make:= MakeNest;
   Move:= MoveNest;
   Aie:= AieNest;
   Die:= DieNest;
   weight:= 160; priority:= -76;
   heatSpeed:= 10;
   refreshSpeed:= 30;
   coolSpeed:= 60;
   aieStKinds:= StoneSet{}; aieSKCount:= 0;
   aieStone:= FlameMult; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stCE}; dieSKCount:= 1;
   dieStone:= FlameMult * 4 + 9; dieStStyle:= slowStyle;
   basicType:= Vegetal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetGrid;
   Make:= MakeGrid;
   Move:= MoveGrid;
   Aie:= AieGrid;
   Die:= DieGrid;
   charge:= -60; weight:= 90;
   inerty:= 100; priority:= -63;
   aieStKinds:= StoneSet{stC35}; aieSKCount:= 1;
   aieStone:= 5; aieStStyle:= returnStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= MakeMissile;
   Make:= MakeMissile;
   Move:= MoveMissile;
   Aie:= AieMissile;
   Die:= DieMissile;
   charge:= 2; weight:= 30;
   inerty:= 50; priority:= -30;
   basicType:= Vegetal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetPopUp;
   Make:= MakePopUp;
   Move:= MovePopUp;
   Aie:= AiePopUp;
   Die:= DiePopUp;
   charge:= 20; weight:= 20;
   inerty:= 40; priority:= -20;
   heatSpeed:= 25;
   refreshSpeed:= 25;
   coolSpeed:= 10;
   aieStKinds:= StoneSet{stC35}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stFOG1}; dieSKCount:= 1;
   dieStone:= 3; dieStStyle:= slowStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetGhost;
   Make:= MakeGhost;
   Move:= MoveGhost;
   Aie:= AieGhost;
   Die:= DieGhost;
   charge:= 0; weight:= 10;
   inerty:= 90; priority:= -64;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetQuad;
   Make:= MakeQuad;
   Move:= MoveAlienAV;
   Aie:= AieAlienAV;
   Die:= DieFour;
   charge:= 15; weight:= 64;
   inerty:= 50; priority:= -56;
   heatSpeed:= 90;
   refreshSpeed:= 90;
   coolSpeed:= 10;
   aieStKinds:= StoneSet{stC35}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= fastStyle;
   dieStKinds:= StoneSet{stC35}; dieSKCount:= 1;
   dieStone:= 8; dieStStyle:= slowStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN2], attr^.node)
 END InitParams;

BEGIN

 InitParams;

END ChaosCreator.
