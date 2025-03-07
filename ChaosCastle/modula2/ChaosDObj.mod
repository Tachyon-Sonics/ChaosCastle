IMPLEMENTATION MODULE ChaosDObj;

 FROM SYSTEM IMPORT ADR;
 FROM Memory IMPORT AllocMem, AddTail, CARD16, INT16, First, Tail, Next;
 FROM Checks IMPORT CheckMem;
 FROM Trigo IMPORT RND, SQRT;
 FROM ChaosBase IMPORT Anims, AnimSet, BasicTypes, Obj, ObjPtr, ObjAttr, Frac,
  ObjAttrPtr, attrList, Stones, StoneSet, gravityStyle, slowStyle, Period,
  step, FlameMult, air, AieProc, water, Weapon, ObjFlags, ObjFlagSet, nbAnim,
  animList, NextObj, TailObj, objList, PrevObj, FirstObj;
 FROM ChaosSounds IMPORT SoundList, soundList, Effect, SetEffect,
  SoundEffect, nulSound;
 FROM ChaosActions IMPORT SetObjRect, SetObjLoc, UpdateXY, Aie, Die,
  OutOfScreen, AvoidBounds, AvoidBackground, Leave, DoCollision, Gravity, Burn,
  OutOfBounds, LimitSpeed, Boum, PopMessage, lifePos, GetCenter, CreateObj,
  SetObjVXY, InBackground, PlayerCollision;
 FROM ChaosBonus IMPORT BoumMoney, Moneys, MoneySet;

 VAR
  getAirEffect, bubbleCreateEffect: ARRAY[0..2] OF Effect;
  createFireEffect, createWindEffect, dieWindEffect: ARRAY[0..0] OF Effect;

 PROCEDURE MakeCartoon(cartoon: ObjPtr);
 BEGIN
  SetObjLoc(cartoon, 128, 88, 32, 32);
  SetObjRect(cartoon, 0, 0, 32, 32)
 END MakeCartoon;

 PROCEDURE MakeMagnet(magnet: ObjPtr);
  VAR
   px: INT16;
 BEGIN
  WITH magnet^ DO
   IF moveSeq = 0 THEN px:= 128
   ELSIF moveSeq = 1 THEN px:= 140
   ELSIF moveSeq = 2 THEN px:= 152
   ELSE px:= 164
   END
  END;
  SetObjLoc(magnet, px, 120, 12, 12);
  SetObjRect(magnet, 0, 0, 12, 12)
 END MakeMagnet;

 PROCEDURE MakeMeteor(meteor: ObjPtr);
  VAR
   px, py, sz: INT16;
   shp: CARD16;
 BEGIN
  WITH meteor^ DO
   shp:= shapeSeq DIV (Period DIV 8);
   CASE stat OF
     domStone: px:= 248; py:= 120; sz:= 8
    |domSmall: px:= 224; py:= 70; sz:= 5
    |domMedium: px:= (shp MOD 3) * 8 + 224; py:= 120; sz:= 8
    |domBig: px:= (shp MOD 4) * 12 + 176; py:= 120; sz:= 12
   END
  END;
  SetObjLoc(meteor, px, py, sz, sz);
  IF NOT(displayed IN meteor^.flags) THEN SetObjRect(meteor, 0, 0, sz, sz) END
 END MakeMeteor;

 PROCEDURE MakeSand(sand: ObjPtr);
 BEGIN
  sand^.life:= 1;
  SetObjLoc(sand, 240, 184, 16, 16);
  SetObjRect(sand, 1, 1, 15, 15)
 END MakeSand;

 PROCEDURE MakeWind(wind: ObjPtr);
 BEGIN
  WITH wind^ DO hitSubLife:= 0; fireSubLife:= 0 END;
  SetObjLoc(wind, 224, 68, 8, 8);
  SetObjLoc(wind, 104, 136, 16, 16);
  SetObjRect(wind, -4, -4, 12, 12)
 END MakeWind;

 PROCEDURE MakeBubble(bubble: ObjPtr);
  VAR
   px, py, sz: INT16;
 BEGIN
  WITH bubble^ DO hitSubLife:= 0; fireSubLife:= 0 END;
  CASE bubble^.stat OF
    5: px:= 212; py:= 144; sz:= 12
   |4: px:= 158; py:= 62; sz:= 10
   |3: px:= 248; py:= 164; sz:= 8
   |2: px:= 248; py:= 172; sz:= 6
   |1: px:= 248; py:= 178; sz:= 4
   |0: px:= 248; py:= 182; sz:= 2
  END;
  SetObjLoc(bubble, px, py, sz, sz);
  SetObjRect(bubble, 0, 0, sz, sz)
 END MakeBubble;

 PROCEDURE MakeMirror(mirror: ObjPtr);
  VAR
   px: INT16;
 BEGIN
  WITH mirror^ DO
   IF ODD(stat) THEN px:= 192 ELSE px:= 224 END;
   hitSubLife:= 0; fireSubLife:= 0
  END;
  SetObjLoc(mirror, px, 88, 32, 32);
  SetObjRect(mirror, 1, 1, 31, 31)
 END MakeMirror;

 PROCEDURE MakeWindMaker(maker: ObjPtr);
 BEGIN
  IF ODD(maker^.stat) OR (maker^.shapeSeq <= Period * 4) THEN
   SetObjLoc(maker, 140, 180, 12, 20)
  ELSE
   SetObjLoc(maker, 32 + (3 - (maker^.shapeSeq DIV 32) MOD 4) * 12, 180, 12, 20)
  END;
  SetObjRect(maker, 0, 0, 12, 20)
 END MakeWindMaker;

 PROCEDURE MakeBubbleMaker(maker: ObjPtr);
 BEGIN
  SetObjLoc(maker, 80, 180, 12, 20);
  SetObjRect(maker, 0, 0, 12, 20)
 END MakeBubbleMaker;

 PROCEDURE MakeFireMaker(maker: ObjPtr);
 BEGIN
  SetObjLoc(maker, 32, 200, 20, 12);
  SetObjRect(maker, 0, 0, 20, 12)
 END MakeFireMaker;

 PROCEDURE MakeFireWall(fw: ObjPtr);
 BEGIN
  SetObjLoc(fw, 224, 204, 32, 32);
  SetObjRect(fw, 4, 4, 28, 28)
 END MakeFireWall;

 PROCEDURE MakeWave(wave: ObjPtr);
  VAR
   px, py, sz: INT16;
 BEGIN
  CASE wave^.stat OF
    0: px:= 188; py:= 210; sz:= 2
   |1: px:= 211; py:= 208; sz:= 3
   |2: px:= 208; py:= 208; sz:= 3
   |3: px:= 194; py:= 209; sz:= 3
   |4: px:= 220; py:= 216; sz:= 4
   |5: px:= 216; py:= 216; sz:= 4
   |6: px:= 200; py:= 208; sz:= 4
   |7: px:= 219; py:= 208; sz:= 5
   |8: px:= 214; py:= 208; sz:= 5
   |9: px:= 194; py:= 204; sz:= 5
  |10: px:= 210; py:= 214; sz:= 6
  |11: px:= 204; py:= 214; sz:= 6
  |12: px:= 188; py:= 204; sz:= 6
  |13: px:= 196; py:= 212; sz:= 8
  |14: px:= 188; py:= 212; sz:= 8
  END;
  SetObjLoc(wave, px, py, sz, sz);
  SetObjRect(wave, -2, -2, sz + 2, sz + 2)
 END MakeWave;

 PROCEDURE ResetMagnet(magnet: ObjPtr);
 BEGIN
  WITH magnet^ DO
   moveSeq:= RND() MOD 4;
   shapeSeq:= RND() MOD (Period DIV 10)
  END;
  MakeMagnet(magnet)
 END ResetMagnet;

 PROCEDURE ResetMeteor(meteor: ObjPtr);
 BEGIN
  WITH meteor^ DO
   shapeSeq:= RND() MOD Period;
   moveSeq:= Period * 2;
   INCL(flags, of1);
   life:= 10
  END;
  MakeMeteor(meteor)
 END ResetMeteor;

 PROCEDURE ResetWind(wind: ObjPtr);
 BEGIN
  wind^.moveSeq:= Period DIV 4 + RND() MOD (Period * 4);
  MakeWind(wind)
 END ResetWind;

 PROCEDURE ResetBubble(bubble: ObjPtr);
 BEGIN
  WITH bubble^ DO
   moveSeq:= Period * 2 + RND() MOD Period;
   life:= 1
  END;
  MakeBubble(bubble)
 END ResetBubble;

 PROCEDURE ResetWindMaker(maker: ObjPtr);
 BEGIN
  WITH maker^ DO
   shapeSeq:= Period DIV 10;
   stat:= stat MOD 4;
   moveSeq:= RND() MOD (Period DIV 2) + Period DIV 5
  END;
  MakeWindMaker(maker)
 END ResetWindMaker;

 PROCEDURE ResetBubbleMaker(maker: ObjPtr);
 BEGIN
  WITH maker^ DO
   stat:= stat MOD 2;
   moveSeq:= RND() MOD (Period * 3)
  END;
  MakeBubbleMaker(maker)
 END ResetBubbleMaker;

 PROCEDURE ResetFireMaker(maker: ObjPtr);
 BEGIN
  maker^.moveSeq:= 0;
  MakeFireMaker(maker)
 END ResetFireMaker;

 PROCEDURE ResetWave(wave: ObjPtr);
 BEGIN
  WITH wave^ DO
   hitSubLife:= 0; fireSubLife:= 0;
   life:= 1;
   moveSeq:= Period * 4 + RND() MOD Period
  END;
  MakeWave(wave)
 END ResetWave;

 PROCEDURE MoveCartoon(cartoon: ObjPtr);
 BEGIN
  IF OutOfScreen(cartoon) THEN Leave(cartoon); RETURN END
 END MoveCartoon;

 PROCEDURE MoveMagnet(magnet: ObjPtr);
 BEGIN
  IF OutOfScreen(magnet) THEN Leave(magnet); RETURN END;
  WITH magnet^ DO
   UpdateXY(magnet);
   vx:= 0; vy:= 0; dvx:= 0; dvy:= 0;
   IF step >= shapeSeq THEN
    INC(shapeSeq, Period DIV ((stat + 1) * 4));
    IF subKind = doMagnetA THEN
     moveSeq:= (moveSeq + 1) MOD 4
    ELSIF moveSeq = 0 THEN
     moveSeq:= 3
    ELSE
     DEC(moveSeq)
    END;
    MakeMagnet(magnet)
   END;
   IF step >= shapeSeq THEN shapeSeq:= 0 ELSE DEC(shapeSeq, step) END;
   WITH attr^ DO
    charge:= (stat + 1) * 30;
    IF subKind = doMagnetA THEN charge:= -charge END
   END;
   Gravity(magnet, AnimSet{PLAYER..BONUS})
  END
 END MoveMagnet;

 PROCEDURE MeteorCrash(m1, m2: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  IF (m1 <> m2) AND (m1^.subKind = doMeteor) AND
     (m1^.stat >= domMedium) AND (m1^.stat <= m2^.stat) THEN
   IF m1^.stat = m2^.stat THEN
    Boum(m2, StoneSet{}, slowStyle, FlameMult, 0);
    m2^.life:= 0
   END;
   Boum(m1, StoneSet{}, slowStyle, FlameMult, 0);
   Die(m1)
  END
 END MeteorCrash;

 VAR
  meteorObjAttr: ObjAttr;

 PROCEDURE MoveMeteor(meteor: ObjPtr);
  VAR
   parent, tail, pred, succ: ObjPtr;
   oldAttr: ObjAttrPtr;
   mx, my, px, py: INTEGER;
   dx, dy: LONGINT;
   dist: CARDINAL;
   anims: AnimSet;
   oob: BOOLEAN;
 BEGIN
  WITH meteor^ DO
   dvx:= vx; dvy:= vy;
   UpdateXY(meteor);
   shapeSeq:= (shapeSeq + step) MOD ((Period DIV 8) * 12);
   MakeMeteor(meteor);
   IF stat <> domBig THEN
    IF of1 IN flags THEN
     parent:= NextObj(objNode);
     tail:= TailObj(objList)
    ELSE
     parent:= PrevObj(objNode);
     tail:= FirstObj(objList)
    END;
    IF (parent <> tail) AND (parent^.kind = DEADOBJ) AND
       (parent^.subKind = doMeteor) AND (parent^.stat > stat) THEN
     GetCenter(meteor, mx, my);
     GetCenter(parent, px, py);
     dx:= px - mx; dy:= py - my;
     dist:= SQRT(dx * dx + dy * dy);
     IF of2 IN flags THEN
      IF dist <= hitSubLife THEN hitSubLife:= dist ELSE EXCL(flags, of2) END
     ELSE
      IF dist >= hitSubLife THEN
       hitSubLife:= dist
      ELSE
       INCL(flags, of2);
       IF of1 IN flags THEN
        pred:= PrevObj(objNode); succ:= NextObj(parent^.objNode);
        pred^.objNode.next:= ADR(parent^.objNode);
        succ^.objNode.prev:= ADR(objNode);
        parent^.objNode.prev:= ADR(pred^.objNode);
        objNode.next:= ADR(succ^.objNode);
        objNode.prev:= ADR(parent^.objNode);
        parent^.objNode.next:= ADR(objNode);
        EXCL(flags, of1)
       ELSE
        pred:= PrevObj(parent^.objNode); succ:= NextObj(objNode);
        pred^.objNode.next:= ADR(objNode);
        succ^.objNode.prev:= ADR(parent^.objNode);
        parent^.objNode.next:= ADR(succ^.objNode);
        objNode.prev:= ADR(pred^.objNode);
        objNode.next:= ADR(parent^.objNode);
        parent^.objNode.prev:= ADR(objNode);
        INCL(flags, of1)
       END
      END
     END
    END
   END;
   oob:= OutOfBounds(meteor);
   oldAttr:= attr; attr:= ADR(meteorObjAttr);
   WITH attr^ DO charge:= subKind * 30; charge:= -charge END;
   IF oob THEN anims:= AnimSet{DEADOBJ} ELSE anims:= AnimSet{WEAPON, MISSILE..DEADOBJ} END;
   IF stat = domSmall THEN EXCL(anims, DEADOBJ) END;
   IF (stat <> domStone) AND (anims <> AnimSet{}) THEN
    Gravity(meteor, anims)
   END;
   attr:= oldAttr; attr^.charge:= 90;
   INCL(flags, displayed);
   IF ABS(vx) + ABS(vy) < 50 THEN ay:= 1 ELSE ay:= 0 END;
   IF (moveSeq = Period * 2) THEN
    IF nbAnim[ALIEN2] = 0 THEN Die(meteor); RETURN END;
    IF NOT(oob) THEN DEC(moveSeq) END
   ELSIF (moveSeq = 0) THEN
    IF oob THEN Die(meteor); RETURN END
   ELSE
    IF step >= moveSeq THEN moveSeq:= 0 ELSE DEC(moveSeq, step) END
   END;
   IF stat >= domMedium THEN
    DoCollision(meteor, AnimSet{DEADOBJ}, MeteorCrash, hitSubLife, fireSubLife)
   ELSIF stat = domStone THEN
    hitSubLife:= 10;
    PlayerCollision(meteor, hitSubLife)
   END;
   IF (nbAnim[ALIEN2] + nbAnim[ALIEN1] = 0) AND
      (ABS(vx) < 384) AND (ABS(vy) < 384) THEN
    ay:= 1
   END;
   IF (life = 0) OR OutOfScreen(meteor) THEN Die(meteor) END
  END
 END MoveMeteor;

 PROCEDURE SlowDown(victim, src: ObjPtr; VAR hit, fire: CARD16);
  VAR
   sw, vw: INT16;
 BEGIN
  WITH victim^ DO
   IF (src^.subKind = doBubble) THEN temperature:= 0 END;
   IF (ABS(vx) < 512) AND (ABS(vy) < 512) THEN RETURN END
  END;
  Aie(victim, src, hit, fire);
  WITH src^ DO
   IF (subKind = doWind) AND (victim^.kind <> STONE) THEN moveSeq:= 0; RETURN END;
   sw:= victim^.attr^.weight; vw:= attr^.weight * 5;
   IF sw + vw <> 0 THEN
    vx:= (vx / 64 * vw + victim^.vx / 64 * sw) / (sw + vw) * 64;
    vy:= (vy / 64 * vw + victim^.vy / 64 * sw) / (sw + vw) * 64
   END
  END
 END SlowDown;

 PROCEDURE KillOnMagnet(victim, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  IF (victim^.subKind = doMagnetA) THEN src^.life:= 0 END
 END KillOnMagnet;

 PROCEDURE MoveSand(sand: ObjPtr);
 BEGIN
  IF OutOfScreen(sand) THEN Leave(sand); RETURN END;
  UpdateXY(sand);
  AvoidBackground(sand, 0);
  WITH sand^ DO
   hitSubLife:= 0; fireSubLife:= 0;
   DoCollision(sand, AnimSet{PLAYER, ALIEN3..ALIEN1, BONUS, MACHINE}, SlowDown, hitSubLife, fireSubLife);
   DoCollision(sand, AnimSet{DEADOBJ}, KillOnMagnet, hitSubLife, fireSubLife);
   IF life = 0 THEN Die(sand) END
  END
 END MoveSand;

 PROCEDURE MoveWind(wind: ObjPtr);
 BEGIN
  UpdateXY(wind);
  AvoidBackground(wind, 0);
  WITH wind^ DO
   DoCollision(wind, AnimSet{PLAYER..BONUS, MACHINE}, SlowDown, hitSubLife, fireSubLife);
   IF OutOfScreen(wind) OR (step >= moveSeq) OR
      (ABS(vx) + ABS(vy) < 2000) THEN
    SoundEffect(wind, dieWindEffect);
    Die(wind); RETURN
   END;
   DEC(moveSeq, step)
  END
 END MoveWind;

 PROCEDURE GiveAir(player, bubble: ObjPtr; VAR hit, fire: CARD16);
  VAR
   msg: ARRAY[0..9] OF CHAR;
   togive, val: CARD16;
 BEGIN
  WITH bubble^ DO
   life:= 0;
   IF water THEN
    togive:= Period * stat * 12;
    IF togive = 0 THEN RETURN END;
    IF air < 60000 - togive THEN
     SoundEffect(player, getAirEffect);
     INC(air, togive)
    ELSE
     air:= 60000
    END;
    val:= air DIV Period;
    IF val < 10 THEN
     msg:= "()   ()"; msg[3]:= CHR(48 + val)
    ELSIF val < 100 THEN
     msg:= "()    ()";
     msg[3]:= CHR(48 + val DIV 10);
     msg[4]:= CHR(48 + val MOD 10)
    ELSE
     msg:= "()     ()";
     msg[3]:= CHR(48 + val DIV 100); val:= val MOD 100;
     msg[4]:= CHR(48 + val DIV 10);
     msg[5]:= CHR(48 + val MOD 10)
    END;
    PopMessage(ADR(msg), lifePos, (stat + 1) DIV 2)
   END
  END
 END GiveAir;

 PROCEDURE MoveWave(wave: ObjPtr);
 BEGIN
  IF OutOfScreen(wave) OR InBackground(wave) THEN Die(wave); RETURN END;
  UpdateXY(wave);
  AvoidBackground(wave, 1);
  WITH wave^ DO
   DoCollision(wave, AnimSet{PLAYER, ALIEN3..ALIEN1, BONUS}, SlowDown, hitSubLife, fireSubLife);
   LimitSpeed(wave, 1600);
   dvx:= vx; dvy:= vy;
   IF dvx > 1024 THEN dvx:= 1024 ELSIF dvx < -1024 THEN dvx:= -1024 END;
   IF dvy > 1024 THEN dvy:= 1024 ELSIF dvy < -1024 THEN dvy:= -1024 END;
   IF step > moveSeq THEN
    IF stat < 3 THEN Die(wave); RETURN ELSE DEC(stat, 3) END;
    ResetWave(wave)
   ELSE
    DEC(moveSeq, step)
   END
  END
 END MoveWave;

 PROCEDURE MoveBubble(bubble: ObjPtr);
 BEGIN
  IF OutOfScreen(bubble) THEN Die(bubble); RETURN END;
  AvoidBackground(bubble, 1);
  UpdateXY(bubble);
  WITH bubble^ DO
   DoCollision(bubble, AnimSet{ALIEN3..ALIEN1, BONUS}, SlowDown, hitSubLife, fireSubLife);
   DoCollision(bubble, AnimSet{PLAYER}, GiveAir, hitSubLife, fireSubLife);
   IF step > moveSeq THEN
    IF stat = 0 THEN
     life:= 0
    ELSE
     DEC(stat)
    END;
    ResetBubble(bubble)
   ELSE
    DEC(moveSeq, step)
   END;
   IF life = 0 THEN Die(bubble) END
  END
 END MoveBubble;

 PROCEDURE MedianM(victim, mirror: ObjPtr; VAR hit, fire: CARD16);
  VAR
   mx, my, ox, oy, dx, dy: INT16;
 BEGIN
  GetCenter(mirror, mx, my);
  IF step > 30 THEN
   ox:= victim^.midx DIV Frac; INC(ox, victim^.cx);
   oy:= victim^.midy DIV Frac; INC(oy, victim^.cy)
  ELSE
   GetCenter(victim, ox, oy)
  END;
  dx:= ox - mx; dy:= oy - my;
  WITH victim^ DO
   IF ABS(dx) > ABS(dy) THEN
    IF dx > 0 THEN
     vx:= ABS(vx); dvx:= ABS(dvx)
    ELSE
     vx:= -ABS(vx); dvx:= -ABS(dvx)
    END
   ELSE
    IF dy > 0 THEN
     vy:= ABS(vy); dvy:= ABS(dvy)
    ELSE
     vy:= -ABS(vy); dvy:= -ABS(dvy)
    END
   END
  END
 END MedianM;

 PROCEDURE DiagM(victim, mirror: ObjPtr; VAR hit, fire: CARD16);
  VAR
   mx, my, ox, oy, dx, dy, tv, dtv: INT16;
   c: CARD16;
 BEGIN
  GetCenter(mirror, mx, my);
  FOR c:= (step DIV 32) TO 0 BY -1 DO
   IF c = 1 THEN
    ox:= victim^.x DIV Frac; INC(ox, victim^.cx);
    oy:= victim^.y DIV Frac; INC(oy, victim^.cy)
   ELSE
    GetCenter(victim, ox, oy)
   END;
   dx:= ox - mx; dy:= oy - my;
   WITH victim^ DO
    IF ABS(dx) + ABS(dy) - cx - cy < 16 THEN
     IF dx * dy > 0 THEN
      IF ((dx >= 0) AND (vx + vy <= 0)) OR ((dx <= 0) AND (vx + vy >= 0)) THEN
       tv:= vy; dtv:= dvy;
       vy:= -vx; dvy:= -dvx;
       vx:= -tv; dvx:= -dtv;
       RETURN
      END
     ELSE
      IF ((dx >= 0) AND (vx <= vy)) OR ((dx <= 0) AND (vx >= vy)) THEN
       tv:= vy; dtv:= dvy;
       vy:= vx; dvy:= dvx;
       vx:= tv; dvx:= dtv;
       RETURN
      END
     END
    END
   END
  END
 END DiagM;

 PROCEDURE MoveMirror(mirror: ObjPtr);
  VAR
   What: AieProc;
 BEGIN
  IF OutOfScreen(mirror) THEN Leave(mirror); RETURN END;
  WITH mirror^ DO
   IF ODD(stat) THEN What:= DiagM ELSE What:= MedianM END;
   DoCollision(mirror, AnimSet{PLAYER..MACHINE}, What, hitSubLife, fireSubLife)
  END
 END MoveMirror;

 PROCEDURE Blow(src: ObjPtr; victims: AnimSet);
  VAR
   obj, tail: ObjPtr;
   px, py, ox, oy, rx, ry, spd, lspd, is: INT16;
   k: Anims;
 BEGIN
  GetCenter(src, px, py);
  is:= step;
  FOR k:= MIN(Anims) TO MAX(Anims) DO
   IF k IN victims THEN
    obj:= First(animList[k]);
    tail:= Tail(animList[k]);
    WHILE obj <> tail DO
     GetCenter(obj, ox, oy);
     rx:= ABS(ox - px); ry:= ABS(oy - py);
     IF (rx > ry) AND (rx < 128) THEN
      spd:= 128 - rx;
      DEC(spd, spd * ry DIV rx);
      spd:= spd * is DIV 4;
      lspd:= (oy - py) * ry DIV rx * is DIV 8;
      IF ox < px THEN spd:= -spd END;
      WITH obj^ DO
       INC(vx, spd); INC(vy, lspd);
       IF vx < -4096 THEN vx:= -4096 ELSIF vx > 4096 THEN vx:= 4096 END;
       IF vy < -4096 THEN vy:= -4096 ELSIF vy > 4096 THEN vy:= 4096 END
      END
     END;
     obj:= Next(obj^.animNode)
    END
   END
  END
 END Blow;

 PROCEDURE MoveWindMaker(maker: ObjPtr);
  VAR
   wind: ObjPtr;
   px, py: INT16;
   nvy: INT16;
   oldSeq: CARD16;
 BEGIN
  IF OutOfScreen(maker) THEN Leave(maker); RETURN END;
  WITH maker^ DO
   oldSeq:= shapeSeq;
   IF step >= shapeSeq THEN INC(shapeSeq, 5760) ELSE DEC(shapeSeq, step) END;
   IF ((oldSeq DIV 32) <> (shapeSeq DIV 32)) AND NOT(ODD(stat)) THEN
    MakeWindMaker(maker)
   END;
   IF step >= moveSeq THEN
    IF (shapeSeq > Period * 4) AND ODD(stat) THEN
     GetCenter(maker, px, py);
     nvy:= RND() MOD 1024; DEC(nvy, 512);
     SoundEffect(maker, createWindEffect);
     wind:= CreateObj(DEADOBJ, doWind, px, py, 0, 1);
     SetObjVXY(wind, 3600, nvy);
     wind:= CreateObj(DEADOBJ, doWind, px, py, 0, 1);
     SetObjVXY(wind, -3600, nvy)
    END;
    INC(moveSeq, RND() MOD (Period DIV 2) + Period DIV 5)
   END;
   IF (NOT ODD(stat)) AND (shapeSeq > Period * 4) THEN
    Blow(maker, AnimSet{PLAYER..BONUS})
   END;
   DEC(moveSeq, step);
   hitSubLife:= 7;
   PlayerCollision(maker, hitSubLife)
  END
 END MoveWindMaker;

 PROCEDURE MoveBubbleMaker(maker: ObjPtr);
  VAR
   bubble: ObjPtr;
   px, py: INT16;
   nvx, nvy: INT16;
 BEGIN
  IF OutOfScreen(maker) THEN Leave(maker); RETURN END;
  WITH maker^ DO
   IF step >= moveSeq THEN
    GetCenter(maker, px, py);
    nvy:= RND() MOD 1024; DEC(nvy, 512);
    nvx:= 2048 + RND() MOD 512;
    IF stat = 0 THEN nvx:= nvx DIV 4; nvy:= nvy DIV 4 END;
    IF RND() MOD 2 = 0 THEN nvx:= -nvx END;
    SoundEffect(maker, bubbleCreateEffect);
    bubble:= CreateObj(DEADOBJ, doBubble, px, py, RND() MOD 4 + 2, 1);
    SetObjVXY(bubble, nvx, nvy);
    INC(moveSeq, RND() MOD (Period * (9 - stat * 8)) + Period DIV 4)
   END;
   DEC(moveSeq, step);
   hitSubLife:= 7;
   PlayerCollision(maker, hitSubLife)
  END
 END MoveBubbleMaker;

 PROCEDURE MoveFireMaker(maker: ObjPtr);
  VAR
   fire: ObjPtr;
   px, py: INT16;
   nvx, nvy: INT16;
   power: CARD16;
 BEGIN
  IF OutOfScreen(maker) THEN Leave(maker); RETURN END;
  WITH maker^ DO
   IF step >= moveSeq THEN
    GetCenter(maker, px, py);
    nvx:= RND() MOD 1024; DEC(nvx, 512);
    nvy:= 2048 + RND() MOD 512;
    IF RND() MOD 2 = 0 THEN nvy:= -nvy END;
    power:= RND() MOD 4 + 1;
    SoundEffect(maker, createFireEffect);
    fire:= CreateObj(WEAPON, ORD(FIRE), px, py, 0, power);
    SetObjVXY(fire, nvx, nvy);
    INC(moveSeq, RND() MOD (Period DIV 2) + Period DIV 4)
   END;
   DEC(moveSeq, step)
  END
 END MoveFireMaker;

 PROCEDURE MoveFireWall(fw: ObjPtr);
  VAR
   hit: CARD16;
 BEGIN
  IF OutOfScreen(fw) THEN Leave(fw); RETURN END;
  hit:= 72;
  PlayerCollision(fw, hit)
 END MoveFireWall;

 PROCEDURE InitParams;
  VAR
   attr: ObjAttrPtr;
 BEGIN
  SetEffect(getAirEffect[0], soundList[wVoice], 418, 8363, 90, 4);
  SetEffect(getAirEffect[1], soundList[wVoice], 627, 12544, 90, 4);
  SetEffect(getAirEffect[2], soundList[wVoice], 836, 16726, 90, 4);
  SetEffect(bubbleCreateEffect[0], soundList[wWhite], 279, 8363, 40, 1);
  SetEffect(bubbleCreateEffect[1], soundList[wWhite], 318, 12545, 40, 1);
  SetEffect(bubbleCreateEffect[2], soundList[wWhite], 558, 16726, 40, 1);
  SetEffect(createFireEffect[0], soundList[wNoise], 300, 4181, 40, 1);
  SetEffect(createWindEffect[0], soundList[wWhite], 600, 8363, 40, 1);
  SetEffect(dieWindEffect[0], soundList[wCrash], 300, 4181, 80, 1);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= MakeCartoon;
   Make:= MakeCartoon;
   Move:= MoveCartoon;
   basicType:= NotBase;
   priority:= 95;
   toKill:= FALSE
  END;
  AddTail(attrList[DEADOBJ], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetMagnet;
   Make:= MakeMagnet;
   Move:= MoveMagnet;
   basicType:= NotBase;
   priority:= -80;
   toKill:= FALSE
  END;
  AddTail(attrList[DEADOBJ], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetMagnet;
   Make:= MakeMagnet;
   Move:= MoveMagnet;
   basicType:= NotBase;
   priority:= -80;
   toKill:= FALSE
  END;
  AddTail(attrList[DEADOBJ], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetMeteor;
   Make:= MakeMeteor;
   Move:= MoveMeteor;
   basicType:= NotBase;
   inerty:= 200;
   priority:= -75;
   toKill:= TRUE
  END;
  meteorObjAttr:= attr^;
  AddTail(attrList[DEADOBJ], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= MakeSand;
   Make:= MakeSand;
   Move:= MoveSand;
   inerty:= 30; weight:= 20;
   charge:= -100;
   priority:= 30;
   basicType:= NotBase;
   toKill:= FALSE
  END;
  AddTail(attrList[DEADOBJ], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetWind;
   Make:= MakeWind;
   Move:= MoveWind;
   inerty:= 0; weight:= 40;
   basicType:= NotBase;
   toKill:= FALSE
  END;
  AddTail(attrList[DEADOBJ], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetBubble;
   Make:= MakeBubble;
   Move:= MoveBubble;
   inerty:= 20; weight:= 5;
   charge:= 10;
   priority:= 40;
   basicType:= NotBase;
   toKill:= FALSE
  END;
  AddTail(attrList[DEADOBJ], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= MakeMirror;
   Make:= MakeMirror;
   Move:= MoveMirror;
   priority:= -76;
   basicType:= NotBase;
   toKill:= FALSE
  END;
  AddTail(attrList[DEADOBJ], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetWindMaker;
   Make:= MakeWindMaker;
   Move:= MoveWindMaker;
   priority:= -1;
   basicType:= NotBase;
   toKill:= FALSE
  END;
  AddTail(attrList[DEADOBJ], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetBubbleMaker;
   Make:= MakeBubbleMaker;
   Move:= MoveBubbleMaker;
   priority:= -1;
   basicType:= NotBase;
   toKill:= FALSE
  END;
  AddTail(attrList[DEADOBJ], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetFireMaker;
   Make:= MakeFireMaker;
   Move:= MoveFireMaker;
   priority:= -2;
   basicType:= NotBase;
   toKill:= FALSE
  END;
  AddTail(attrList[DEADOBJ], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= MakeFireWall;
   Make:= MakeFireWall;
   Move:= MoveFireWall;
   priority:= -90;
   basicType:= NotBase;
   toKill:= FALSE
  END;
  AddTail(attrList[DEADOBJ], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetWave;
   Make:= MakeWave;
   Move:= MoveWave;
   inerty:= 20; weight:= 0;
   charge:= 10;
   priority:= 75;
   basicType:= NotBase;
   toKill:= FALSE
  END;
  AddTail(attrList[DEADOBJ], attr^.node)
 END InitParams;

BEGIN

 InitParams;

END ChaosDObj.
