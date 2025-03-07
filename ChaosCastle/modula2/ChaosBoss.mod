IMPLEMENTATION MODULE ChaosBoss;

 FROM Memory IMPORT SET16, CARD8, INT8, CARD16, INT16, CARD32, INT32,
  AllocMem, AddTail, CopyStr, StrPtr, First, Tail, Next;
 FROM Checks IMPORT CheckMem;
 FROM Languages IMPORT ADL;
 FROM Trigo IMPORT RND, SQRT, SGN, SIN, COS;
 FROM ChaosBase IMPORT Anims, AnimSet, BasicTypes, Obj, ObjPtr, ObjAttr,
  ObjAttrPtr, attrList, Stones, StoneSet, gravityStyle, slowStyle, fastStyle,
  FlameMult, Frac, Period, lasttime, step, mainPlayer, pLife, difficulty,
  addpt, GameStat, gameStat, level, Zone, zone, ObjFlags, ObjFlagSet, water,
  snow, Weapon, GetAnimAttr, animList, nbAnim, MaxHot, screenInverted,
  nbDollar, nbSterling;
 FROM ChaosGraphics IMPORT BW, BH, gameWidth, gameHeight;
 FROM ChaosSounds IMPORT SoundList, soundList, Sound, Effect, SetEffect,
  SoundEffect, nulSound;
 FROM ChaosActions IMPORT SetObjLoc, SetObjRect, UpdateXY, Aie, Die, DecLife,
  DoCollision, OutOfScreen, OutOfBounds, AvoidBounds, AvoidBackground,
  InBackground, Leave, AvoidAnims, Gravity, Burn, GetCenter, CreateObj,
  SetObjVXY, SetObjAXY, statPos, actionPos, moneyPos, lifePos, PopMessage, Boum,
  LimitSpeed, SetObjXY, PlayerCollision;
 FROM ChaosFire IMPORT missileEffect, flameEffect, bombEffect, createEffect,
  huEffect, poufEffect, aieEffect, koEffect, ShowStat, ParabolicDist,
  FireFlame, FireMissile, FireMissileV, FireMissileA, FireMissileS, BoumS,
  CloseEnough, GoTo, GoCenter, ReturnWeapon, KillObjs, BoumX, turn, theFather,
  theMaster, theIllusion, laugh, ResetPart, MoveHeart, MoveEye, MoveMouth,
  MovePart, DiePart, Chain, BoumE;
 FROM ChaosMissile IMPORT mAlien1, mAlien2, mAcc2;
 FROM ChaosAlien IMPORT aDbOval, aHospital, aDiese, aStar, aBubble, aBumper,
  aPic, aKamikaze, aTri, aTrefle, aBig, aSquare, aFlame, aColor, aCartoon;
 FROM ChaosCreator IMPORT cAlienV, cAlienA, cCreatorR, cCreatorC, cCircle,
  cHurryUp, cChief, cFour, cAlienBox, cNest, cGrid, cMissile, cPopUp, cGhost,
  cQuad;
 FROM ChaosMachine IMPORT mCannon1, mCannon2, mCannon3, mTurret, mDoor;
 FROM ChaosDObj IMPORT doMagnetR, doMagnetA, doWindMaker, doMirror;

 VAR
  brotherEffect, sisterEffect, motherEffect, fatherEffect: ARRAY[0..0] OF Effect;
  haha1Effect: ARRAY[0..3] OF Effect;
  haha2Effect: ARRAY[0..5] OF Effect;

 PROCEDURE MakeBrotherAlien(boss: ObjPtr);
  VAR
   py: INT16;
 BEGIN
  WITH boss^ DO
   IF hitSubLife + fireSubLife = 0 THEN py:= 148 ELSE py:= 96 END
  END;
  SetObjLoc(boss, 0, py, 64, 32);
  SetObjRect(boss, 1, 1, 63, 31)
 END MakeBrotherAlien;

 PROCEDURE ResetBrotherAlien(boss: ObjPtr);
 BEGIN
  WITH boss^ DO
   life:= 1500; fireSubLife:= 10; hitSubLife:= 40;
   moveSeq:= 30; (* life DIV 50 *)
   shapeSeq:= 0; (* time before changing dir *)
   stat:= 0; (* time before firing / getting vincible *)
  END;
  MakeBrotherAlien(boss)
 END ResetBrotherAlien;

 PROCEDURE MoveBrotherAlien(boss: ObjPtr);
  VAR
   hit: CARD16;
   bx, by, px, py: INT16;
 BEGIN
  UpdateXY(boss);
  AvoidBounds(boss, 3);
  AvoidBackground(boss, 3);
  Burn(boss);
  WITH boss^ DO
   IF step >= shapeSeq THEN
    IF hitSubLife + fireSubLife = 0 THEN
     IF (moveSeq < 10) AND (moveSeq > 0) THEN
      BoumE(boss, 30, 8, 16)
     END;
     INC(shapeSeq, Period)
    ELSE
     IF moveSeq < 20 THEN
      BoumE(boss, 30, 16, 8)
     END;
     INC(shapeSeq, Period * (RND() MOD 8 + 5))
    END
   END;
   DEC(shapeSeq, step);
   IF moveSeq = 0 THEN GoCenter(boss) END;
   IF step > stat THEN
    IF hitSubLife + fireSubLife = 0 THEN
     IF moveSeq = 0 THEN
      GetCenter(boss, bx, by);
      GetCenter(mainPlayer, px, py);
      IF CloseEnough(bx, by, px, py) THEN Die(boss) END;
      RETURN
     ELSE
      hitSubLife:= 40; fireSubLife:= 10
     END;
     MakeBrotherAlien(boss)
    END;
    IF moveSeq < 10 THEN
     FireFlame(boss, 0, 0, TRUE)
    ELSIF moveSeq < 20 THEN
     FireMissileV(boss, mainPlayer, 0, 0, TRUE)
    ELSIF moveSeq < 30 THEN
     FireMissileA(boss, 0, 0)
    END;
    dvx:= RND() MOD 4096; DEC(dvx, 2048);
    dvy:= RND() MOD 4096; DEC(dvy, 2048);
    INC(stat, Period DIV 4 + RND() MOD (Period * 10 DIV (difficulty + 4)))
   END;
   DEC(stat, step)
  END;
  hit:= 50;
  PlayerCollision(boss, hit)
 END MoveBrotherAlien;

 PROCEDURE AieBrotherAlien(boss, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  hit:= 0; fire:= 0;
  WITH boss^ DO
   IF hitSubLife + fireSubLife = 0 THEN RETURN END;
   DEC(moveSeq);
   SoundEffect(boss, aieEffect);
   hitSubLife:= 0; fireSubLife:= 0;
   MakeBrotherAlien(boss);
   IF life > 0 THEN stat:= Period * 2 ELSE stat:= Period * 10 END;
   shapeSeq:= Period + Period DIV 4;
   life:= moveSeq * 50 + 1;
   ShowStat(ADL("Brother: ##"), moveSeq)
  END
 END AieBrotherAlien;

 PROCEDURE DieBrotherAlien(boss: ObjPtr);
  VAR
   hit, fire: CARD16;
   cnt: INT16;
 BEGIN
  WITH boss^ DO
   IF moveSeq = 0 THEN
    Chain(boss);
    SoundEffect(boss, brotherEffect);
    Boum(boss, StoneSet{stFOG3, stFOG4}, slowStyle, 15, 2);
    Boum(boss, StoneSet{stCE, stRE, stCROSS}, gravityStyle, 15, 3);
    Boum(boss, StoneSet{stC26, stC35}, fastStyle, 15, 2);
    FOR cnt:= 1 TO 10 DO
     vx:= COS(cnt * 36) * 4;
     vy:= SIN(cnt * 36) * 4;
     FireFlame(boss, 0, 0, FALSE)
    END;
    vx:= 0; vy:= 0
   ELSE
    life:= moveSeq * 50 + 1;
    hit:= 40; fire:= 10;
    Aie(boss, boss, hit, fire)
   END
  END
 END DieBrotherAlien;

 PROCEDURE MakeSisterAlien(boss: ObjPtr);
  VAR
   ss, sz, sx, sy: INT16;
 BEGIN
  WITH boss^ DO
   IF shapeSeq >= 512 THEN
    sz:= 0
   ELSE
    ss:= (shapeSeq DIV 16) MOD 16;
    IF ss < 8 THEN
     sz:= 8 - ss
    ELSE
     sz:= ss - 7
    END
   END
  END;
  sx:= 28 - sz * 2; sy:= 16 - sz * 2;
  SetObjLoc(boss, 60 + sz, 224 + sz, sx, sy);
  SetObjRect(boss, 0, 0, 28, 16)
 END MakeSisterAlien;

 PROCEDURE ResetSisterAlien(boss: ObjPtr);
 BEGIN
  WITH boss^ DO
   life:= 1500; fireSubLife:= 40; hitSubLife:= 10;
   moveSeq:= 30; (* life DIV 50 *)
   shapeSeq:= 128; (* shape cycle pos *)
   stat:= 0; (* time, in shape cycles, before obj. creation *)
  END;
  MakeSisterAlien(boss)
 END ResetSisterAlien;

 PROCEDURE MoveSisterAlien(boss: ObjPtr);
  VAR
   obj: ObjPtr;
   aAttr: ObjAttrPtr;
   px, py: INT16;
   hit, kCnt, sStat, cnt, mul: CARD16;
   nKind: Anims;
   sKind: CARD8;
 BEGIN
  UpdateXY(boss);
  AvoidBounds(boss, 4);
  AvoidBackground(boss, 4);
  Burn(boss);
  WITH boss^ DO
   IF (shapeSeq >= 512) AND (step + 512 > shapeSeq) THEN
    BoumS(boss, mainPlayer, 0, 0, 0, 0, 15, 0, 24, 13, 0, FALSE, FALSE, TRUE)
   END;
   IF step > shapeSeq THEN
    IF hitSubLife + fireSubLife = 0 THEN
     IF moveSeq = 0 THEN
      Die(boss); RETURN
     ELSE
      hitSubLife:= 10; fireSubLife:= 40
     END
    END;
    IF stat = 0 THEN
     kCnt:= 0; mul:= 1;
     LOOP
      IF kCnt > 6 THEN kCnt:= 0; INC(mul) END;
      sStat:= 20 + pLife * 5; cnt:= 1;
      CASE kCnt OF
        0: nKind:= MACHINE; sKind:= mCannon3;
       |1: nKind:= ALIEN2; sKind:= cFour
       |2: nKind:= ALIEN2; sKind:= cCreatorR
       |3: nKind:= ALIEN2; sKind:= cQuad
       |4: nKind:= MACHINE; sKind:= mTurret;
       |5: nKind:= ALIEN1; sKind:= aHospital
       |6: nKind:= ALIEN2; sKind:= cNest
      END;
      aAttr:= GetAnimAttr(nKind, sKind);
      IF aAttr^.nbObj < cnt * mul THEN
       GetCenter(boss, px, py);
       SoundEffect(boss, huEffect);
       obj:= CreateObj(nKind, sKind, px, py, sStat, sStat);
       EXIT
      END;
      INC(kCnt)
     END;
     dvx:= RND() MOD 3072; DEC(dvx, 1536);
     dvy:= RND() MOD 3072; DEC(dvy, 1536);
     stat:= RND() MOD 3 + 1
    ELSE
     DEC(stat);
     IF stat = 0 THEN dvx:= 0; dvy:= 0 END
    END;
    FireMissileV(boss, mainPlayer, 0, 0, TRUE);
    INC(shapeSeq, 512)
   END;
   DEC(shapeSeq, step);
  END;
  MakeSisterAlien(boss);
  hit:= 50;
  PlayerCollision(boss, hit)
 END MoveSisterAlien;

 PROCEDURE AieSisterAlien(boss, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  hit:= 0; fire:= 0;
  WITH boss^ DO
   IF hitSubLife + fireSubLife = 0 THEN RETURN END;
   DEC(moveSeq);
   SoundEffect(boss, aieEffect);
   hitSubLife:= 0; fireSubLife:= 0;
   IF moveSeq > 0 THEN shapeSeq:= 1024 ELSE shapeSeq:= 2048; dvx:= 0; dvy:= 0 END;
   stat:= 0;
   life:= moveSeq * 50 + 1;
   ShowStat(ADL("Sister: ##"), moveSeq)
  END
 END AieSisterAlien;

 PROCEDURE DieSisterAlien(boss: ObjPtr);
  VAR
   hit, fire: CARD16;
 BEGIN
  WITH boss^ DO
   IF moveSeq = 0 THEN
    Chain(boss);
    SoundEffect(boss, sisterEffect);
    BoumS(boss, mainPlayer, 0, 0, 0, 0, 12, 18, 60, 6, 10, FALSE, FALSE, FALSE)
   ELSE
    life:= moveSeq * 50 + 1;
    hit:= 10; fire:= 40;
    Aie(boss, boss, hit, fire)
   END
  END
 END DieSisterAlien;

 PROCEDURE MakeMotherAlien(boss: ObjPtr);
  VAR
   px, py: INT16;
 BEGIN
  WITH boss^ DO
   CASE stat OF
     0, 1, 3, 4:
      px:= 172; py:= 180
    |2:
     IF (nested IN flags) THEN
      px:= 172; py:= 180; EXCL(flags, nested)
     ELSE
      px:= 64; py:= 200; INCL(flags, nested)
     END
    |5:
     px:= 64; py:= 200
   END;
   SetObjLoc(boss, px, py, 24, 24);
   SetObjRect(boss, 1, 1, 23, 23)
  END
 END MakeMotherAlien;

 PROCEDURE ResetMotherAlien(boss: ObjPtr);
 BEGIN
  WITH boss^ DO
   hitSubLife:= 30; fireSubLife:= 20;
   moveSeq:= 9 + difficulty; (* life DIV 50 *)
   life:= moveSeq * 50 + 1;
   shapeSeq:= 0; (* countDown before next stat *)
   stat:= 5; (* 0: kmk + end  1: wait -> 0  3: wait-> 2  2: fires
                4: center + aliens       5: center + fire *)
  END;
  MakeMotherAlien(boss)
 END ResetMotherAlien;

 PROCEDURE MoveMotherAlien(boss: ObjPtr);
  VAR
   alien: ObjPtr;
   oldSeq, hit, nLife: CARD16;
   cnt, c, angle, bx, by, px, py, nSpeed: INT16;
   nKind: Anims;
   sKind: CARD8;
 BEGIN
  UpdateXY(boss);
  AvoidBackground(boss, 4);
  Burn(boss);
  GetCenter(mainPlayer, px, py);
  GetCenter(boss, bx, by);
  WITH boss^ DO
   IF step > shapeSeq THEN
    IF (stat = 0) AND CloseEnough(bx, by, px, py) THEN
     shapeSeq:= 0;
     Die(boss); RETURN
    ELSIF (stat = 1) AND (nbAnim[ALIEN2] + nbAnim[ALIEN1] = 0) THEN
     SoundEffect(boss, poufEffect);
     stat:= 0; shapeSeq:= Period * 40
    ELSIF stat = 2 THEN
     hitSubLife:= 20; fireSubLife:= 30;
     shapeSeq:= Period * 2; stat:= 5
    ELSIF (stat = 3) AND (nbAnim[ALIEN2] + nbAnim[ALIEN1] = 0) THEN
     BoumS(boss, mainPlayer, 0, 0, 0, 0, 15, 0, 24, 15, 0, TRUE, TRUE, TRUE);
     stat:= 2; shapeSeq:= Period * (20 + RND() MOD 16)
    ELSIF stat = 4 THEN
     IF moveSeq = 0 THEN stat:= 1 ELSE stat:= 3 END;
     SoundEffect(boss, createEffect);
     nKind:= ALIEN2; nSpeed:= 4; nLife:= pLife * 2 + 40;
     CASE moveSeq OF
       0: sKind:= cChief; cnt:= 4; INC(nLife, 30)
      |1: sKind:= cCircle; cnt:= 4; INC(nLife, 100); nSpeed:= 3
      |2: sKind:= cHurryUp; cnt:= 6; nSpeed:= 2
      |3: sKind:= cCreatorC; cnt:= 7; INC(nLife, 90); nSpeed:= 1
      |4: sKind:= cCreatorR; cnt:= 5; INC(nLife, 60); nSpeed:= 1
      |5: sKind:= cAlienBox; cnt:= 3; INC(nLife, 90); nSpeed:= 2
      |6: sKind:= cNest; cnt:= 9; INC(nLife, 60)
      |7: sKind:= cFour; cnt:= 12
      |8: sKind:= cQuad; cnt:= 11
      |9: sKind:= cAlienA; cnt:= 12
     |10: sKind:= cAlienV; cnt:= 11
     |11: sKind:= cGrid; cnt:= 6
     |12: sKind:= aBumper; cnt:= 11; nKind:= ALIEN1
     |13: sKind:= cChief; cnt:= 1
     |14: sKind:= cGhost; cnt:= 5;
     |15: sKind:= cPopUp; cnt:= 12;
     |16: sKind:= aBig; cnt:= 7; nKind:= ALIEN1
     |17: sKind:= aSquare; cnt:= 10; nKind:= ALIEN1
     |18: sKind:= bBrotherAlien; cnt:= 1; nKind:= ALIEN3
     END;
     FOR c:= 1 TO cnt DO
      angle:= c * 360 DIV cnt;
      px:= bx + COS(angle) DIV 64;
      py:= by + SIN(angle) DIV 64;
      alien:= CreateObj(nKind, sKind, px, py, 0, nLife);
      SetObjVXY(alien, COS(angle) * nSpeed, SIN(angle) * nSpeed)
     END
    ELSIF stat = 5 THEN
     BoumS(boss, mainPlayer, 0, 0, 0, 0, 15, 0, 24, 15, 0, TRUE, TRUE, TRUE);
     hitSubLife:= 0; fireSubLife:= 0;
     stat:= 2; shapeSeq:= Period * (20 + RND() MOD 16)
    END
   END;
   oldSeq:= shapeSeq;
   IF step > shapeSeq THEN shapeSeq:= 0 ELSE DEC(shapeSeq, step) END;
   CASE stat OF
   0:
    IF shapeSeq < Period * 10 THEN
     GoCenter(boss)
    ELSE
     GetCenter(mainPlayer, px, py);
     GetCenter(boss, bx, by);
     ReturnWeapon(bx, by);
     SetObjAXY(boss, SGN(px - bx) * 64, SGN(py - by) * 64);
     LimitSpeed(boss, 2200);
     IF (oldSeq DIV 32) <> (shapeSeq DIV 32) THEN
      GetCenter(boss, px, py);
      c:= RND() MOD 3;
      IF c = 0 THEN
       SoundEffect(boss, missileEffect);
       nKind:= MISSILE; sKind:= mAlien2; cnt:= mAcc2
      ELSIF c = 1 THEN
       nKind:= STONE; cnt:= ORD(stRBOX); sKind:= 0
      ELSE
       nKind:= STONE; cnt:= ORD(stCBOX); sKind:= 0
      END;
      alien:= CreateObj(nKind, sKind, px, py, cnt, 12);
      bx:= RND() MOD 2048; by:= RND() MOD 2048;
      SetObjVXY(alien, vx + bx - 1024, vy + by - 1536);
      SetObjAXY(alien, 0, 48)
     END
    END
   |1, 3:
    GetCenter(boss, bx, by);
    IF ((ax >= 0) AND (bx > gameWidth DIV 2 + 117)) OR (ax = 0) THEN
     ax:= RND() MOD 32; ax:= -ax - 16
    ELSIF (ax < 0) AND (bx < gameWidth DIV 2 - 117) THEN
     ax:= RND() MOD 32 + 16
    END;
    IF ((ay >= 0) AND (by > gameHeight DIV 2 + 117)) OR (ay = 0) THEN
     ay:= RND() MOD 32; ay:= -ay - 16
    ELSIF (ay < 0) AND (by < gameHeight DIV 2 - 117) THEN
     ay:= RND() MOD 32 + 16
    END;
    LimitSpeed(boss, 1536);
    ReturnWeapon(bx, by)
   |2:
    GetCenter(boss, px, py);
    ReturnWeapon(px, py);
    IF step > hitSubLife THEN
     FireMissileS(px, py, vx, vy, boss, mainPlayer, 0, 0, TRUE, TRUE);
     INC(hitSubLife, Period DIV 5 + RND() MOD (Period * 8 DIV 5));
     bx:= RND() MOD 64; by:= RND() MOD 64;
     INC(px, RND() MOD 2); INC(py, RND() MOD 2);
     SetObjAXY(boss, SGN(gameWidth DIV 2 - px) * bx, SGN(gameHeight DIV 2 - py) * by)
    END;
    LimitSpeed(boss, 1536);
    DEC(hitSubLife, step);
   |4, 5:
    IF (stat = 4) AND (shapeSeq < Period) AND (oldSeq >= Period) THEN
     SoundEffect(boss, huEffect)
    END;
    GoCenter(boss)
   END
  END;
  MakeMotherAlien(boss);
  hit:= 50;
  PlayerCollision(boss, hit)
 END MoveMotherAlien;

 PROCEDURE AieMotherAlien(boss, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  IF src^.kind = WEAPON THEN Die(src) END;
  WITH boss^ DO
   IF fireSubLife = 0 THEN RETURN END;
   DEC(moveSeq); life:= moveSeq * 50 + 1;
   ShowStat(ADL("Mother: ##"), moveSeq);
   IF moveSeq <> 0 THEN SoundEffect(boss, aieEffect) END;
   hitSubLife:= 0; fireSubLife:= 0;
   stat:= 4; shapeSeq:= Period * 2
  END
 END AieMotherAlien;

 PROCEDURE DieMotherAlien(boss: ObjPtr);
  VAR
   hit, fire: CARD16;
 BEGIN
  WITH boss^ DO
   IF (moveSeq = 0) AND (stat = 0) AND (shapeSeq = 0) THEN
    Chain(boss);
    SoundEffect(boss, motherEffect);
    temperature:= MaxHot; turn:= level[Family] > 4;
    BoumS(boss, boss, 0, 0, 0, 0, 12, 18, 60, 12, 10, TRUE, TRUE, FALSE)
   ELSE
    life:= moveSeq * 50 + 1;
    hit:= 20; fire:= 30;
    Aie(boss, boss, hit, fire)
   END
  END
 END DieMotherAlien;

 PROCEDURE MakeFatherAlien(boss: ObjPtr);
  VAR
   px, py, sz, dt: INT16;
   v: CARD16;
 BEGIN
  sz:= 16; dt:= 4;
  WITH boss^ DO
   v:= (shapeSeq DIV 32) MOD 4;
   IF ODD(moveSeq) THEN v:= 3 - v END;
   CASE v OF
     0: px:= 172; py:= 208; sz:= 12; dt:= 2
    |1: px:= 172; py:= 220
    |2: px:= 188; py:= 220
    |3: px:= 204; py:= 220
   END
  END;
  SetObjLoc(boss, px, py, sz, sz);
  SetObjRect(boss, dt, dt, dt + 8, dt + 8)
 END MakeFatherAlien;

 PROCEDURE ResetFatherAlien(boss: ObjPtr);
 BEGIN
  theFather:= boss;
  WITH boss^ DO
   hitSubLife:= 20; fireSubLife:= 30;
   moveSeq:= 9 + difficulty DIV 2;
   life:= moveSeq * 50 + 1;
   shapeSeq:= 4;
   stat:= 4
  END;
  MakeFatherAlien(boss)
 END ResetFatherAlien;

 PROCEDURE MoveFatherAlien(boss: ObjPtr);
  VAR
   alien: ObjPtr;
   cattr: ObjAttrPtr;
   lx, ly, dl: INT32;
   c, angle, bx, by, px, py, mul, nvx, nvy, nax, nay: INT16;
   oldSeq, dv, skcnt, hit, nLife: CARD16;
   sList: SET16;
   nKind: Anims;
   sKind: CARD8;
 BEGIN
  UpdateXY(boss);
  AvoidBackground(boss, 4);
  AvoidBounds(boss, 4);
  LimitSpeed(boss, 3072);
  GetCenter(mainPlayer, px, py);
  GetCenter(boss, bx, by);
  WITH boss^ DO
   IF step > shapeSeq THEN
    SoundEffect(boss, huEffect);
    CASE stat OF
    0:
     stat:= 3 + RND() MOD 2; shapeSeq:= Period * (20 + RND() MOD 16)
    |1:
     stat:= 2; shapeSeq:= Period * 10
    |2:
     IF (nbAnim[ALIEN2] < 8) AND (nbAnim[ALIEN1] < 6) THEN
      IF (moveSeq MOD 3 = 0) THEN stat:= 3 ELSE stat:= 4 END;
      shapeSeq:= Period * (20 + RND() MOD 16)
     ELSE
      shapeSeq:= Period * 10
     END
    |3:
     IF moveSeq = 0 THEN
      KillObjs(MACHINE, mDoor);
      KillObjs(DEADOBJ, doMirror);
      stat:= 5; shapeSeq:= Period * 50
     ELSE
      stat:= 6; shapeSeq:= Period * 8
     END
    |4:
     stat:= 6; shapeSeq:= Period * 8
    |5:
     IF CloseEnough(bx, by, px, py) AND
        (nbAnim[ALIEN2] + nbAnim[ALIEN1] = 0) THEN
      shapeSeq:= 0; Die(boss); RETURN
     ELSE
      shapeSeq:= Period * 2
     END
    |6:
     cattr:= GetAnimAttr(ALIEN2, cNest);
     IF (cattr^.nbObj < 3) AND (level[Family] < 10) THEN
      alien:= CreateObj(ALIEN2, cNest, 5 * BW + BW DIV 2, 3 * BW DIV 2, 0, 100)
     END;
     stat:= 0; shapeSeq:= Period * (20 + RND() MOD 16)
    |7:
     stat:= 1;
     shapeSeq:= Period * (7 + ORD(moveSeq = 1) * 6)
    END;
    IF stat = 0 THEN
     hitSubLife:= 30; fireSubLife:= 20
    ELSE
     hitSubLife:= 0; fireSubLife:= 0
    END
   END;
   oldSeq:= shapeSeq;
   IF step > shapeSeq THEN shapeSeq:= 0 ELSE DEC(shapeSeq, step) END;
   CASE stat OF
   0: (* fight vs player *)
    lx:= px - bx; ly:= py - by;
    dl:= SQRT(lx * lx + ly * ly);
    IF dl <> 0 THEN
     dvx:= lx * 128 DIV dl * 16;
     dvy:= ly * 128 DIV dl * 16
    END;
    dv:= (shapeSeq DIV (Period DIV 3));
    IF (oldSeq DIV (Period DIV 3) <> dv) THEN
     IF dv MOD 2 = 0 THEN
      dv:= RND() MOD 3;
      IF dv = 0 THEN
       FireMissileV(boss, mainPlayer, 0, 0, TRUE)
      ELSIF dv = 1 THEN
       FireMissileA(boss, 0, 0)
      ELSE
       FireMissileS(bx, by, -dvx, -dvy, boss, mainPlayer, 0, 0, TRUE, TRUE)
      END
     ELSIF ((bx < 64) OR (by < 128) OR
            (bx > gameWidth - 64) OR (by > gameHeight - 64)) AND
            (shapeSeq > Period * 2) THEN
      dv:= RND() MOD 8;
      IF dv < 4 THEN
       nKind:= ALIEN1; sKind:= aFlame
      ELSIF dv < 6 THEN
       nKind:= ALIEN1; sKind:= aDiese
      ELSE
       nKind:= ALIEN2; sKind:= cFour
      END;
      SoundEffect(boss, flameEffect);
      alien:= CreateObj(nKind, sKind, bx, by, 0, pLife + 10);
      SetObjVXY(alien, dvx, dvy)
     END
    END;
    IF dl < 56 THEN
     dvx:= -dvx; dvy:= -dvy
    ELSIF dl < 64 THEN
     dvx:= 0; dvy:= 0
    END;
    AvoidAnims(boss, AnimSet{WEAPON})
   |1: (* create objs *)
    lx:= vx; ly:= vy;
    dl:= SQRT(lx * lx + ly * ly);
    IF dl = 0 THEN
     vx:= 1536; vy:= 0
    ELSE
     vx:= lx * 1536 DIV dl;
     vy:= ly * 1536 DIV dl
    END;
    dvx:= 0; dvy:= 0;
    ax:= -vy DIV 64;
    ay:= vy DIV 64;
    dv:= (shapeSeq DIV (Period DIV 2));
    IF (oldSeq DIV (Period DIV 2)) <> dv THEN
     nKind:= ALIEN2; skcnt:= 3;
     CASE moveSeq OF
       0, 14: sList:= SET16{cChief, cCircle, cCreatorC}
      |1, 13: sList:= SET16{cCreatorR, cCreatorC, cNest}
      |2: sList:= SET16{cChief, cCreatorC, cAlienBox}
      |3: sList:= SET16{cNest, cCreatorR, cQuad}
      |4: sList:= SET16{aTrefle, aTri, aHospital}; nKind:= ALIEN1
      |5: sList:= SET16{cQuad, cFour, cCreatorC}
      |6: sList:= SET16{cCreatorR, cAlienV, cAlienA}
      |7: sList:= SET16{cNest, cFour, cAlienA}
      |8: sList:= SET16{cAlienBox, cQuad, cAlienV}
      |9: sList:= SET16{cAlienV, cAlienA, cFour}
     |10: sList:= SET16{cGrid, cChief, cHurryUp}
     |11: sList:= SET16{cPopUp, cGhost}; skcnt:= 2
     |12: sList:= SET16{aKamikaze, aPic, aStar}; nKind:= ALIEN1
     END;
     skcnt:= RND() MOD skcnt;
     sKind:= 0; WHILE NOT(sKind IN sList) DO INC(sKind) END;
     WHILE skcnt > 0 DO
      REPEAT sKind:= (sKind + 1) MOD 16 UNTIL sKind IN sList;
      DEC(skcnt)
     END;
     IF (nKind = ALIEN2) AND
        (sKind IN SET16{cCircle, cCreatorR, cCreatorC, cNest}) THEN
      nLife:= pLife * 2 + 120
     ELSE
      nLife:= pLife * 3 + 10
     END;
     SoundEffect(boss, flameEffect);
     INC(nLife, RND() MOD 4);
     alien:= CreateObj(nKind, sKind, bx, by, RND() MOD 4, nLife);
     angle:= RND() MOD 360;
     SetObjVXY(alien, COS(angle), SIN(angle))
    END;
   |2: (* wait until no objs *)
    IF ((ax >= 0) AND (bx > gameWidth DIV 2 + 118)) OR (ax = 0) THEN
     ax:= RND() MOD 32; ax:= -ax - 16
    ELSIF (ax < 0) AND (bx < gameWidth DIV 2 - 118) THEN
     ax:= RND() MOD 32 + 16
    END;
    IF ((ay >= 0) AND (by > gameHeight DIV 2 + 118)) OR (ay = 0) THEN
     ay:= RND() MOD 32; ay:= -ay - 16
    ELSIF (ay < 0) AND (by < gameHeight DIV 2 - 118) THEN
     ay:= RND() MOD 32 + 16
    END;
    LimitSpeed(boss, 1536);
    ReturnWeapon(bx, by)
   |3: (* center + fire around *)
    GoCenter(boss); ReturnWeapon(bx, by);
    dv:= (shapeSeq DIV (Period DIV 3));
    IF (oldSeq DIV (Period DIV 3)) <> dv THEN
     SoundEffect(boss, missileEffect);
     IF (ABS(px - bx) < 56) AND (ABS(py - by) < 56) AND (RND() MOD 6 = 0) THEN
      BoumS(boss, boss, 0, 0, RND() MOD 18, 0, 18, 0, 20, 12, 0, FALSE, FALSE, FALSE);
     END;
     FOR c:= 1 TO 4 DO
      angle:= RND() MOD 360;
      nvx:= COS(angle) * 2; nvy:= SIN(angle) * 2;
      nax:= RND() MOD 200; DEC(nax, 100);
      nay:= RND() MOD 200; DEC(nay, 100);
      IF ParabolicDist(bx, by, nvx, nvy, nax, nay, px, py) > 24 THEN
       alien:= CreateObj(MISSILE, mAlien1, bx, by, RND() MOD 3, 12);
       SetObjVXY(alien, nvx, nvy);
       SetObjAXY(alien, nax, nay)
      END
     END
    END
   |4: (* spiral firing *)
    GoTo(boss, gameWidth DIV 2, gameHeight DIV 2 + 70);
    ReturnWeapon(bx, by);
    dv:= shapeSeq DIV 60;
    IF (oldSeq DIV 60 <> dv) AND (ABS(vx) < 64) AND (ABS(vy) < 64) THEN
     SoundEffect(boss, missileEffect);
     dv:= (dv MOD 30);
     IF ODD(moveSeq DIV 2) THEN dv:= 29 - dv END;
     angle:= dv * 12 + shapeSeq DIV 750; mul:= (moveSeq DIV 3) + 1;
     IF NOT ODD(moveSeq) THEN mul:= mul * 4 END;
     alien:= CreateObj(MISSILE, mAlien2, bx, by, mAcc2, 12);
     SetObjVXY(alien, COS(angle) * 4 DIV mul, SIN(angle) DIV 2);
     IF ODD(moveSeq) THEN
      mul:= mul * 16;
      SetObjAXY(alien, -COS(angle) DIV mul, -SIN(angle) DIV 32)
     END
    END
   |5: (* kamikaze + boum *)
    IF shapeSeq > Period * 10 THEN
     ax:= 0; ay:= 0;
     dvx:= SGN(px - bx) * 800;
     dvy:= SGN(py - by) * 800;
     dv:= shapeSeq DIV Period;
     IF oldSeq DIV Period <> dv THEN
      dv:= RND() MOD 4;
      IF dv = 0 THEN
       FireFlame(boss, 0, 0, TRUE)
      ELSIF dv = 1 THEN
       FireMissileV(boss, mainPlayer, 0, 0, TRUE)
      ELSIF dv = 2 THEN
       FireMissileA(boss, 0, 0)
      ELSE
       FireMissileS(bx, by, -dvx, -dvy, boss, mainPlayer, 0, 0, TRUE, TRUE)
      END
     END;
    ELSE
     GoCenter(boss);
     IF (shapeSeq = 0) AND CloseEnough(bx, by, px, py) THEN
      shapeSeq:= 0; Die(boss); RETURN
     END
    END
   |6, 7:
    GoCenter(boss);
    ReturnWeapon(bx, by)
   END
  END;
  MakeFatherAlien(boss);
  hit:= 50;
  PlayerCollision(boss, hit)
 END MoveFatherAlien;

 PROCEDURE AieFatherAlien(boss, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  hit:= 0; fire:= 0;
  IF src^.kind = WEAPON THEN Die(src) END;
  WITH boss^ DO
   IF fireSubLife = 0 THEN RETURN END;
   DEC(moveSeq); life:= moveSeq * 50 + 1;
   ShowStat(ADL("FATHER: ##"), moveSeq);
   IF moveSeq <> 0 THEN SoundEffect(boss, aieEffect) END;
   hitSubLife:= 0; fireSubLife:= 0;
   stat:= 7; shapeSeq:= Period
  END
 END AieFatherAlien;

 PROCEDURE DieFatherAlien(boss: ObjPtr);
  VAR
   hit, fire: CARD16;
   angle, ox, oy, c: INT16;
 BEGIN
  WITH boss^ DO
   IF (moveSeq = 0) AND (stat = 5) AND (shapeSeq = 0) THEN
    Chain(boss);
    SoundEffect(boss, fatherEffect);
    BoumS(boss, boss, 0, 0, 6, 0, 18, 0, 20, 16, 0, TRUE, FALSE, FALSE);
    angle:= RND() MOD 120;
    FOR c:= 0 TO 2 DO
     ox:= COS(angle) DIV 16; oy:= SIN(angle) DIV 16;
     BoumS(boss, boss, ox, oy, 0, 0, 18, 0, 20, 8, 0, TRUE, FALSE, FALSE);
     INC(angle, 120)
    END
   ELSE
    life:= moveSeq * 50 + 1;
    hit:= 30; fire:= 20;
    Aie(boss, boss, hit, fire)
   END
  END
 END DieFatherAlien;

 PROCEDURE MakeMasterAlien(boss: ObjPtr);
 BEGIN
  SetObjLoc(boss, 88, 200, 84, 36);
  SetObjRect(boss, 8, 4, 68, 28)
 END MakeMasterAlien;

 PROCEDURE ResetMasterAlien(boss: ObjPtr);
 BEGIN
  theMaster:= boss; laugh:= FALSE;
  WITH boss^ DO
   hitSubLife:= 0; fireSubLife:= 0;
   moveSeq:= 15;
   life:= moveSeq * 50 + 1;
   stat:= 3; shapeSeq:= 1
  END;
  MakeMasterAlien(boss)
 END ResetMasterAlien;

 PROCEDURE MoveMasterAlien(boss: ObjPtr);
  VAR
   fObj, alien: ObjPtr;
   dx, dy, dl: INT32;
   angle, bx, by, px, py, mx, my: INT16;
   hit, oldSeq: CARD16;
   sKind: CARD8;
   speed, inv: BOOLEAN;
 BEGIN
  UpdateXY(boss);
  AvoidBackground(boss, 1);
  LimitSpeed(boss, 2048);
  inv:= (screenInverted > 0);
  IF laugh <> inv THEN
   IF laugh THEN
    hit:= nbDollar; INC(hit, nbSterling);
    IF (pLife = 0) AND (hit < 20) THEN
     SoundEffect(boss, haha2Effect)
    ELSE
     SoundEffect(boss, haha1Effect)
    END
   END;
   laugh:= NOT(laugh)
  END;
  GetCenter(mainPlayer, px, py);
  GetCenter(boss, bx, by);
  WITH boss^ DO
   IF step >= shapeSeq THEN
    CASE stat OF
     1: stat:= 2; shapeSeq:= Period * (20 + RND() MOD 20)
    |2: stat:= 3; shapeSeq:= Period * 3
    |3, 4:
     IF moveSeq = 0 THEN
      stat:= 5; shapeSeq:= Period * 30
     ELSE
      IF moveSeq > 10 THEN
       fObj:= mainPlayer; speed:= TRUE
      ELSIF moveSeq > 5 THEN
       fObj:= boss; speed:= FALSE
      ELSE
       fObj:= mainPlayer; speed:= FALSE
      END;
      angle:= RND() MOD 360;
      CASE moveSeq MOD 5 OF
       0: BoumS(boss, fObj, 0, 0, 0, angle, 12, 18, 60, 12, 10, TRUE, speed, TRUE)
      |4: BoumS(boss, fObj, 0, 0, 0, 0, 15, 0, 24, 14, 0, TRUE, speed, TRUE)
      |3: BoumS(boss, fObj, 0, 0, 0, angle, 15, 45, 24, 12, 10, TRUE, speed, TRUE)
      |2: SoundEffect(boss, bombEffect);
          shapeSeq:= 2;
          BoumS(boss, fObj, -56, 8, 0, 0, 15, 0, 24, 13, 0, TRUE, speed, FALSE);
          shapeSeq:= 1;
          BoumS(boss, fObj, 56, 8, 0, 0, 15, 0, 24, 13, 0, TRUE, speed, FALSE);
          shapeSeq:= 0;
          BoumS(boss, fObj, 0, -60, 0, 0, 15, 0, 24, 13, 0, TRUE, speed, FALSE)
      |1: BoumS(boss, fObj, 0, 0, 0, 0, 15, 105, 24, 12, 10, TRUE, speed, TRUE)
      END;
      stat:= 1; shapeSeq:= Period * 4
     END
    |5: stat:= 6
    |6:
     IF CloseEnough(bx, by, px, py) AND
        ((nbAnim[ALIEN2] + nbAnim[ALIEN1] +
          nbAnim[MISSILE] + nbAnim[STONE] = 0) OR
         (level[Family] > 6)) THEN
      shapeSeq:= 0; Die(boss); RETURN
     ELSE
      shapeSeq:= Period * 2
     END
    END
   END;
   IF stat = 3 THEN
    hitSubLife:= 25; fireSubLife:= 25
   ELSE
    fireSubLife:= 0
   END;
   oldSeq:= shapeSeq;
   IF step >= shapeSeq THEN shapeSeq:= 0 ELSE DEC(shapeSeq, step) END;
   CASE stat OF
   1: (* wait boum *)
    GoCenter(boss);
    ReturnWeapon(bx, by)
   |2: (* fires *)
    ReturnWeapon(bx, by);
    IF step > hitSubLife THEN
     mx:= RND() MOD 64; my:= RND() MOD 64;
     INC(bx, RND() MOD 2); INC(by, RND() MOD 2);
     SetObjAXY(boss, SGN(gameWidth DIV 2 - bx) * mx, SGN(gameHeight DIV 2 - by) * my);
     CASE (moveSeq MOD 5) OF
     0:
      SoundEffect(boss, missileEffect);
      FireMissileV(boss, mainPlayer, -28, 4, FALSE);
      FireMissileV(boss, mainPlayer, 28, 4, FALSE);
      INC(hitSubLife, RND() MOD Period)
     |4:
      SoundEffect(boss, missileEffect);
      angle:= ((shapeSeq DIV 60) MOD 30) * 12;
      mx:= COS(angle) * 2; my:= SIN(angle) * 2;
      FireMissileS(bx - 28, by + 4, mx, my, boss, mainPlayer, 0, 0, TRUE, FALSE);
      mx:= -mx; my:= -my;
      FireMissileS(bx + 28, by + 4, mx, my, boss, mainPlayer, 0, 0, TRUE, FALSE);
      INC(hitSubLife, Period DIV 3)
     |3:
      IF RND() MOD 2 = 0 THEN mx:= 28 ELSE mx:= -28 END;
      FireFlame(boss, mx, 4, TRUE);
      INC(hitSubLife, RND() MOD Period + Period DIV 2)
     |2:
      SoundEffect(boss, missileEffect);
      dx:= bx - 28 - px; dy:= by + 4 - py;
      dl:= SQRT(dx * dx + dy * dy);
      IF dl <> 0 THEN
       dx:= dx * 3072 DIV dl;
       dy:= dy * 3072 DIV dl
      END;
      FireMissileS(bx - 28, by + 4, dx, dy, boss, mainPlayer, 0, 0, TRUE, FALSE);
      dx:= bx + 28 - px; dy:= by + 4 - py;
      dl:= SQRT(dx * dx + dy * dy);
      IF dl <> 0 THEN
       dx:= dx * 3072 DIV dl;
       dy:= dy * 3072 DIV dl
      END;
      FireMissileS(bx + 28, by + 4, dx, dy, boss, mainPlayer, 0, 0, TRUE, FALSE);
      INC(hitSubLife, RND() MOD Period)
     |1:
      SoundEffect(boss, missileEffect);
      angle:= ((shapeSeq DIV 60) MOD 30) * 12;
      FireMissileS(bx, by, COS(angle) * 2, SIN(angle) * 2, boss, mainPlayer, 0, 0, TRUE, FALSE);
      INC(angle, 120);
      IF moveSeq < 5 THEN
       FireMissileS(bx, by, COS(angle) * 2, SIN(angle) * 2, boss, mainPlayer, 0, 0, TRUE, FALSE)
      END;
      INC(angle, 120);
      IF moveSeq < 10 THEN
       FireMissileS(bx, by, COS(angle) * 2, SIN(angle) * 2, boss, mainPlayer, 0, 0, TRUE, FALSE)
      END;
      INC(hitSubLife, Period DIV 4)
     END
    END;
    IF step > hitSubLife THEN hitSubLife:= 0 ELSE DEC(hitSubLife, step) END;
    LimitSpeed(boss, 1536)
   |3: (* go center *)
    GoCenter(boss)
   |4: (* aie recover *)
    IF (oldSeq >= Period) AND (shapeSeq < Period) THEN
     SoundEffect(boss, huEffect)
    END;
    GoCenter(boss);
    ReturnWeapon(bx, by)
   |5: (* kamikaze *)
    IF (shapeSeq DIV Period) <> (oldSeq DIV Period) THEN
     SoundEffect(boss, createEffect);
     sKind:= RND() MOD 4;
     IF sKind = 0 THEN
      sKind:= cNest
     ELSIF sKind = 1 THEN
      sKind:= cCircle
     ELSIF sKind = 2 THEN
      sKind:= cCreatorR
     ELSE
      sKind:= cCreatorC
     END;
     alien:= CreateObj(ALIEN2, sKind, bx, by, 0, 80)
    END;
    GoTo(boss, px, py)
   |6: (* wait & explode *)
    GoCenter(boss)
   END
  END;
  MakeMasterAlien(boss);
  hit:= 50;
  PlayerCollision(boss, hit)
 END MoveMasterAlien;

 PROCEDURE AieMasterAlien(boss, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  hit:= 0; fire:= 0;
  IF src^.kind = WEAPON THEN Die(src) END;
  WITH boss^ DO
   IF fireSubLife = 0 THEN RETURN END;
   DEC(moveSeq); life:= moveSeq * 50 + 1;
   ShowStat(ADL("MASTER: ##"), moveSeq);
   IF moveSeq <> 0 THEN SoundEffect(boss, aieEffect) END;
   hitSubLife:= 0; fireSubLife:= 0;
   stat:= 4; shapeSeq:= Period * 2
  END
 END AieMasterAlien;

 PROCEDURE DieMasterAlien(boss: ObjPtr);
  VAR
   hit, fire: CARD16;
 BEGIN
  WITH boss^ DO
   IF (moveSeq = 0) AND (stat = 6) AND (shapeSeq = 0) THEN
    Chain(boss);
    SoundEffect(boss, sisterEffect);
    SoundEffect(boss, motherEffect);
    shapeSeq:= 2;
    BoumS(boss, boss, -69, 40, 0, 0, 12, 18, 60, 12, 10, TRUE, FALSE, FALSE);
    shapeSeq:= 1;
    BoumS(boss, boss,  69, 40, 0, 0, 12, 18, 60, 12, 10, TRUE, FALSE, FALSE);
    shapeSeq:= 0;
    BoumS(boss, boss, 0, -80, 0, 0, 12, 18, 60, 12, 10, TRUE, FALSE, FALSE)
   ELSE
    life:= moveSeq * 50 + 1;
    hit:= 25; fire:= 25;
    Aie(boss, boss, hit, fire)
   END
  END
 END DieMasterAlien;

 PROCEDURE MakeIllusion(boss: ObjPtr);
 BEGIN
  SetObjLoc(boss, 59, 243, 10, 10);
  SetObjRect(boss, -3, -3, 19, 19)
 END MakeIllusion;

 PROCEDURE ResetIllusion(boss: ObjPtr);
 BEGIN
  theIllusion:= boss;
  WITH boss^ DO
   hitSubLife:= 10; fireSubLife:= 10;
   moveSeq:= 10 + difficulty DIV 2;
   life:= moveSeq * 50 + 1;
   stat:= 0; (* kind of alien to create, 0 = crash recover *)
   shapeSeq:= 1
  END;
  MakeIllusion(boss)
 END ResetIllusion;

 PROCEDURE MoveIllusion(boss: ObjPtr);
  VAR
   alien: ObjPtr;
   nLife, oldSeq, dv: CARD16;
   bx, by, px, py, angle: INT16;
   sKind: CARD8;
   nKind: Anims;
 BEGIN
  UpdateXY(boss);
  AvoidBounds(boss, 2);
  AvoidBackground(boss, 4);
  GetCenter(boss, bx, by);
  GetCenter(mainPlayer, px, py);
  WITH boss^ DO
   IF (stat <> 33) OR (shapeSeq > Period) THEN
    dvx:= vx; dvy:= vy;
    IF dvx < -1800 THEN dvx:= -1800 ELSIF dvx > 1800 THEN dvx:= 1800 END;
    IF dvy < -1800 THEN dvy:= -1800 ELSIF dvy > 1800 THEN dvy:= 1800 END
   ELSE
    IF level[Family] <= 6 THEN dvx:= 0; dvy:= 0 ELSE GoCenter(boss) END
   END;
   IF step >= shapeSeq THEN
    IF stat < 33 THEN
     shapeSeq:= Period * (20 + RND() MOD 16);
     IF moveSeq = 0 THEN stat:= 33 ELSE stat:= RND() MOD 32 + 1 END
    ELSE
     IF CloseEnough(bx, by, px, py) THEN
      KillObjs(DEADOBJ, doMagnetR);
      KillObjs(DEADOBJ, doMagnetA);
      KillObjs(DEADOBJ, doWindMaker);
      KillObjs(DEADOBJ, doMirror);
      shapeSeq:= 0; Die(boss); RETURN
     ELSE
      shapeSeq:= 511
     END
    END
   END;
   oldSeq:= shapeSeq;
   IF step >= shapeSeq THEN shapeSeq:= 0 ELSE DEC(shapeSeq, step) END;
   dv:= Period * (3 + moveSeq DIV 3);
   IF stat = 0 THEN GoCenter(boss) END;
   IF (shapeSeq DIV dv) <> (oldSeq DIV dv) THEN
    IF (RND() MOD 8 = 0) OR (vx = 0) THEN
     BoumX(boss, boss, 48, ODD(moveSeq));
     angle:= RND() MOD 360;
     vx:= COS(angle) * 3 DIV 2;
     vy:= SIN(angle) * 3 DIV 2
    END;
    IF (stat > 0) AND (stat <= 29) THEN
     IF stat <= 12 THEN
      nKind:= MACHINE; nLife:= RND() MOD 2
     ELSIF stat <= 15 THEN
      nKind:= ALIEN2; nLife:= 80
     ELSIF stat <= 19 THEN
      nKind:= ALIEN2; nLife:= pLife * 2
     ELSIF stat <= 27 THEN
      nKind:= ALIEN1; nLife:= pLife * 2
     ELSE
      nKind:= ALIEN1; nLife:= RND() MOD 4
     END;
     CASE stat OF
       1, 2, 3: sKind:= mCannon1
      |4, 5, 6: sKind:= mCannon2
      |7, 8, 9: sKind:= mCannon3
      |10, 11, 12: sKind:= mTurret
      |13: sKind:= cCreatorR
      |14: sKind:= cCreatorC
      |15: sKind:= cNest
      |16: sKind:= cAlienV
      |17: sKind:= cAlienA
      |18: sKind:= cFour
      |19: sKind:= cQuad
      |20: sKind:= aTri
      |21: sKind:= aDbOval
      |22: sKind:= aHospital
      |23: sKind:= aDiese
      |24: sKind:= aTrefle
      |25: sKind:= aColor
      |26: sKind:= aStar
      |27: sKind:= aBubble
      |28: sKind:= aKamikaze
      |29: sKind:= aPic
     ELSE
     END;
     alien:= CreateObj(nKind, sKind, bx, by, nLife, nLife);
     SoundEffect(boss, huEffect)
    ELSIF stat = 33 THEN
     SoundEffect(boss, createEffect);
     IF RND() MOD 3 = 0 THEN sKind:= cCircle ELSE sKind:= cChief END;
     alien:= CreateObj(ALIEN2, sKind, bx, by, 0, pLife * 3)
    END
   END
  END;
  MakeIllusion(boss)
 END MoveIllusion;

 PROCEDURE AieIllusion(boss, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  hit:= 0; fire:= 0;
  IF src^.kind = WEAPON THEN Die(src) END
 END AieIllusion;

 PROCEDURE DieIllusion(boss: ObjPtr);
  VAR
   off, rx, ry: INT16;
 BEGIN
  WITH boss^ DO
   IF (moveSeq = 0) AND (stat = 33) AND (shapeSeq = 0) THEN
    Chain(boss);
    SoundEffect(boss, brotherEffect);
    SoundEffect(boss, fatherEffect);
    rx:= 0; ry:= 0;
    FOR off:= 0 TO 3 DO
     turn:= TRUE;
     BoumS(boss, boss, rx, ry, 0, 0, 12, 0, 30, 12, 0, TRUE, FALSE, FALSE);
     rx:= COS(off * 120) DIV 16;
     ry:= SIN(off * 120) DIV 16
    END;
    theIllusion:= NIL;
    RETURN
   ELSIF (stat > 0) AND (stat <= 32) THEN
    Boum(boss, StoneSet{stFOG3}, fastStyle, 31, 1);
    DEC(moveSeq);
    life:= moveSeq * 50;
    ShowStat(ADL("Illusion: ##"), moveSeq);
    IF moveSeq <> 0 THEN SoundEffect(boss, aieEffect) END;
    ax:= 0; ay:= 0; dvx:= 0; dvy:= 0; vx:= 0; vy:= -4000;
    stat:= 0; shapeSeq:= 511
   END;
   life:= moveSeq * 50 + 1
  END
 END DieIllusion;

 PROCEDURE InitParams;
  VAR
   attr: ObjAttrPtr;
   c: CARD16;
 BEGIN
  SetEffect(missileEffect[0], soundList[sMissile], 0, 0, 100, 4);
  SetEffect(flameEffect[0], soundList[sHHat], 0, 0, 180, 4);
  SetEffect(bombEffect[0], soundList[sPouf], 0, 0, 220, 7);
  SetEffect(huEffect[0], soundList[sHurryUp], 0, 0, 255, 4);
  SetEffect(poufEffect[0], soundList[sPouf], 0, 4181, 255, 14);
  SetEffect(createEffect[0], soundList[aPanflute], 0, 4181, 220, 12);
  SetEffect(aieEffect[0], soundList[sGong], 0, 16726, 200, 13);
  SetEffect(koEffect[0], soundList[sHa], 0, 0, 240, 14);
  SetEffect(brotherEffect[0], soundList[sVerre], 0, 4181, 255, 15);
  SetEffect(sisterEffect[0], soundList[sGong], 0, 4181, 255, 15);
  SetEffect(motherEffect[0], soundList[sCasserole], 0, 4181, 255, 15);
  SetEffect(fatherEffect[0], soundList[sCannon], 0, 4181, 255, 15);
  SetEffect(haha1Effect[0], soundList[wJans], 0, 16726, 0, 8);
  SetEffect(haha2Effect[0], soundList[wJans], 0, 0, 0, 8);
  FOR c:= 1 TO 3 DO
   SetEffect(haha1Effect[c], soundList[sHa], 0, 0, 160, 8);
   SetEffect(haha2Effect[c], soundList[sHa], 0, 0, 250, 8)
  END;
  SetEffect(haha2Effect[4], soundList[sHa], 0, 7032, 250, 8);
  SetEffect(haha2Effect[5], soundList[sHa], 0, 7032, 250, 8);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetBrotherAlien;
   Make:= MakeBrotherAlien;
   Move:= MoveBrotherAlien;
   Aie:= AieBrotherAlien;
   Die:= DieBrotherAlien;
   charge:= 16; weight:= 100;
   inerty:= 192; priority:= -65;
   heatSpeed:= 20;
   refreshSpeed:= 40;
   coolSpeed:= 120;
   aieStKinds:= StoneSet{stSTAR1}; aieSKCount:= 1;
   aieStone:= FlameMult * 7 + 15; aieStStyle:= fastStyle;
   dieStKinds:= StoneSet{stSTAR1, stSTAR2}; dieSKCount:= 2;
   dieStone:= FlameMult * 7 + 31; dieStStyle:= fastStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN3], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetSisterAlien;
   Make:= MakeSisterAlien;
   Move:= MoveSisterAlien;
   Aie:= AieSisterAlien;
   Die:= DieSisterAlien;
   charge:= 24; weight:= 60;
   inerty:= 192; priority:= -65;
   heatSpeed:= 80;
   refreshSpeed:= 100;
   coolSpeed:= 25;
   aieStKinds:= StoneSet{stSTAR1}; aieSKCount:= 1;
   aieStone:= FlameMult * 7 + 15; aieStStyle:= fastStyle;
   dieStKinds:= StoneSet{stSTAR1, stSTAR2}; dieSKCount:= 2;
   dieStone:= FlameMult * 7; dieStStyle:= fastStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN3], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetMotherAlien;
   Make:= MakeMotherAlien;
   Move:= MoveMotherAlien;
   Aie:= AieMotherAlien;
   Die:= DieMotherAlien;
   charge:= 16; weight:= 70;
   inerty:= 192; priority:= -65;
   heatSpeed:= 50;
   refreshSpeed:= 100;
   coolSpeed:= 40;
   aieStKinds:= StoneSet{stSTAR1}; aieSKCount:= 1;
   aieStone:= FlameMult * 6 + 15; aieStStyle:= fastStyle;
   dieStKinds:= StoneSet{stSTAR1, stSTAR2}; dieSKCount:= 2;
   dieStone:= FlameMult * 7; dieStStyle:= fastStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN3], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetFatherAlien;
   Make:= MakeFatherAlien;
   Move:= MoveFatherAlien;
   Aie:= AieFatherAlien;
   Die:= DieFatherAlien;
   charge:= 16; weight:= 70;
   inerty:= 300; priority:= -63;
   aieStKinds:= StoneSet{stSTAR1}; aieSKCount:= 1;
   aieStone:= FlameMult * 6 + 15; aieStStyle:= fastStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN3], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetMasterAlien;
   Make:= MakeMasterAlien;
   Move:= MoveMasterAlien;
   Aie:= AieMasterAlien;
   Die:= DieMasterAlien;
   charge:= 16; weight:= 70;
   inerty:= 100; priority:= -64;
   aieStKinds:= StoneSet{stSTAR1}; aieSKCount:= 1;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN3], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetIllusion;
   Make:= MakeIllusion;
   Move:= MoveIllusion;
   Aie:= AieIllusion;
   Die:= DieIllusion;
   charge:= 30; weight:= 100;
   inerty:= 20; priority:= 110;
   aieStKinds:= StoneSet{stSTAR1}; aieSKCount:= 1;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN3], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetPart;
   Move:= MoveHeart;
   Die:= DiePart;
   basicType:= NotBase;
   toKill:= FALSE
  END;
  AddTail(attrList[ALIEN3], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetPart;
   Move:= MoveEye;
   Die:= DiePart;
   basicType:= NotBase;
   toKill:= FALSE
  END;
  AddTail(attrList[ALIEN3], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetPart;
   Move:= MoveMouth;
   Die:= DiePart;
   basicType:= NotBase;
   toKill:= FALSE
  END;
  AddTail(attrList[ALIEN3], attr^.node);
  FOR c:= 0 TO 2 DO
   attr:= AllocMem(SIZE(ObjAttr));
   CheckMem(attr);
   WITH attr^ DO
    Reset:= ResetPart;
    Move:= MovePart;
    Die:= DiePart;
    basicType:= NotBase;
    priority:= 99 + c;
    toKill:= FALSE
   END;
   AddTail(attrList[ALIEN3], attr^.node)
  END
 END InitParams;

BEGIN

 InitParams;

END ChaosBoss.
