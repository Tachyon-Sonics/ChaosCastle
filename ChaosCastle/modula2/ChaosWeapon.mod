IMPLEMENTATION MODULE ChaosWeapon;

 FROM SYSTEM IMPORT ADR;
 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32, AllocMem,
  AddTail, FreeMem, First, Tail, Next;
 FROM Checks IMPORT CheckMem;
 FROM Languages IMPORT ADL;
 FROM Trigo IMPORT RND, SIN, COS, SQRT;
 FROM ChaosBase IMPORT Anims, AnimSet, BasicTypes, Obj, ObjPtr, ObjAttr,
  ObjAttrPtr, attrList, gravityStyle, doubleSpeed, sleeper, invinsibility,
  magnet, specialStage, GameStat, gameStat, Period, AieProc, Stones, StoneSet,
  slowStyle, fastStyle, FireProc, Weapon, WeaponSet, WeaponAttr, weaponAttr,
  Fire, Bomb, freeFire, maxPower, shoot, MaxHot, animList, mainPlayer,
  GetAnimAttr, Frac, lasttime, step, water, ObjFlags, ObjFlagSet, nbAnim;
 FROM ChaosSounds IMPORT SoundList, soundList, Effect, SetEffect,
  SoundEffect, StereoEffect, nulSound;
 FROM ChaosActions IMPORT UpdateXY, SetObjXY, SetObjVXY, SetObjAXY, SetObjPos,
  SetObjLoc, SetObjRect, Die, OutOfBounds, OutOfScreen, InBackground,
  GetCenter, Leave, CreateObj, DoCollision, PopMessage, statPos, Aie, actionPos,
  AvoidBackground, Boum, Burn, Collision, Gravity, AvoidBounds, nlObj;


 PROCEDURE GetBulletPrice(w: Weapon): CARD8;
 BEGIN
  CASE w OF
    GUN: RETURN 0
   |FB: RETURN 3
   |LASER: RETURN 6
   |BUBBLE: RETURN 2
   |FIRE: RETURN 1
   |STAR: RETURN 7
   |BALL: RETURN 4
   |GRENADE: RETURN 9
  END
 END GetBulletPrice;

 PROCEDURE CheckBullet(w: Weapon): BOOLEAN;
 BEGIN
  WITH weaponAttr[w] DO
   IF (nbBullet > 0) AND (power > 0) THEN
    IF freeFire = 0 THEN DEC(nbBullet) END;
    IF power < 4 THEN INC(shoot.total, power) ELSE INC(shoot.total, 3) END;
    RETURN TRUE
   ELSE
    IF power = 0 THEN
     PopMessage(ADL("not enough power"), actionPos, 1)
    ELSE
     PopMessage(ADL("no bullet"), actionPos, 1)
    END;
    RETURN FALSE
   END
  END
 END CheckBullet;

 PROCEDURE CheckBomb(w: Weapon): BOOLEAN;
 BEGIN
  WITH weaponAttr[w] DO
   IF (nbBomb > 0) AND (power > 0) THEN
    DEC(nbBomb); RETURN TRUE
   ELSE
    RETURN FALSE
   END
  END
 END CheckBomb;

 PROCEDURE GetPower(w: Weapon): CARD16;
 BEGIN
  IF maxPower = 0 THEN
   RETURN weaponAttr[w].power
  ELSE
   RETURN 4
  END
 END GetPower;

 VAR
  newWeapon: ObjPtr;

 PROCEDURE StdFire(player: ObjPtr; w: Weapon; speed: INT16; addV, stdV: BOOLEAN);
  CONST
   rOff = 7;
   dOff = 5;
  VAR
   wattr: ObjAttrPtr;
   nvx, nvy, px, py: INT16;
   diaspeed: INT16;
 BEGIN
  GetCenter(player, px, py);
  WITH player^ DO
   IF stdV OR ((ABS(vx) < 64) AND (ABS(vy) < 64)) THEN
    nvx:= 0; nvy:= 0;
    diaspeed:= speed * 5 DIV 7;
    CASE shapeSeq OF
      0: nvy:= -speed; DEC(py, rOff)
     |1: nvy:= -diaspeed; nvx:= diaspeed; DEC(py, dOff); INC(px, dOff)
     |2: nvx:= speed; INC(px, rOff)
     |3: nvx:= diaspeed; nvy:= nvx; INC(px, dOff); INC(py, dOff)
     |4: nvy:= speed; INC(py, rOff)
     |5: nvy:= diaspeed; nvx:= -diaspeed; DEC(px, dOff); INC(py, dOff)
     |6: nvx:= -speed; DEC(px, rOff)
     |7: nvx:= -diaspeed; nvy:= nvx; DEC(px, dOff); DEC(py, dOff)
    END
   ELSE
    nvx:= vx DIV 32; nvy:= vy DIV 32;
    diaspeed:= SQRT(nvx * nvx + nvy * nvy);
    speed:= speed DIV 32;
    nvx:= (nvx * speed DIV diaspeed) * 32;
    nvy:= (nvy * speed DIV diaspeed) * 32
   END;
   IF addV THEN
    INC(nvx, vx); INC(nvy, vy);
    IF nvx >= 4096 THEN nvx:= 4095 ELSIF nvx <= -4096 THEN nvx:= -4095 END;
    IF nvy >= 4096 THEN nvy:= 4095 ELSIF nvy <= -4096 THEN nvy:= -4095 END
   END;
   IF w <> BUBBLE THEN
    wattr:= GetAnimAttr(WEAPON, ORD(w));
    DEC(vx, nvx DIV 128 * wattr^.weight);
    DEC(vy, nvy DIV 128 * wattr^.weight)
   END
  END;
  newWeapon:= CreateObj(WEAPON, ORD(w), px, py, 0, 0);
  SetObjVXY(newWeapon, nvx, nvy)
 END StdFire;

 PROCEDURE StdBomb(player: ObjPtr; w: Weapon; speed: INT16; nstat, count: CARD16; addV: BOOLEAN);
  VAR
   obj: ObjPtr;
   cnt, angle, base, power: CARD16;
   cx, cy, nvx, nvy: INT16;
 BEGIN
  power:= GetPower(w);
  GetCenter(player, cx, cy);
  speed:= speed DIV 128;
  base:= RND() MOD (60 DIV count + 1);
  FOR cnt:= 0 TO count - 1 DO
   angle:= cnt * 360 DIV count + base;
   nvx:= (speed * COS(angle)) DIV 8;
   nvy:= (speed * SIN(angle)) DIV 8;
   IF addV THEN
    WITH player^ DO INC(nvx, vx); INC(nvy, vy) END;
    IF nvx >= 4096 THEN nvx:= 4095 ELSIF nvx <= -4096 THEN nvx:= -4095 END;
    IF nvy >= 4096 THEN nvy:= 4095 ELSIF nvy <= -4096 THEN nvy:= -4095 END
   END;
   obj:= CreateObj(WEAPON, ORD(w), cx, cy, nstat, 0);
   SetObjVXY(obj, nvx, nvy)
  END;
 END StdBomb;

 CONST
  MAXF = 32;

 TYPE
  FollowData = RECORD
   currentObj, tailObj, bestObj: ObjPtr;
   lasttime: CARD32;
   bestCount, bestDist, wait: CARD16;
   anim: Anims;
   finish, used: BOOLEAN;
  END;

 VAR
  follow: ARRAY[1..MAXF] OF FollowData;

 PROCEDURE SetNextFollowAnim(num: CARD16);
 BEGIN
  WITH follow[num] DO
   REPEAT
    IF anim = MAX(Anims) THEN
     anim:= MIN(Anims); finish:= TRUE
    ELSE
     INC(anim)
    END
   UNTIL (anim IN AnimSet{ALIEN3..ALIEN1, MACHINE});
   currentObj:= First(animList[anim]);
   tailObj:= Tail(animList[anim])
  END
 END SetNextFollowAnim;

 PROCEDURE ResetFollow(num: CARD16; time: CARD32);
 BEGIN
  WITH follow[num] DO
   anim:= MIN(Anims); finish:= FALSE;
   SetNextFollowAnim(num);
   bestObj:= NIL;
   bestCount:= MAX(CARD16); bestDist:= MAX(CARD16);
   lasttime:= time;
   wait:= 10
  END
 END ResetFollow;

 PROCEDURE AllocFollow(time: CARD32): CARD16;
  VAR
   num: CARD16;
 BEGIN
  num:= MAXF;
  WHILE num > 0 DO
   IF NOT(follow[num].used) THEN
    follow[num].used:= TRUE;
    ResetFollow(num, time);
    RETURN num
   END;
   DEC(num)
  END;
  RETURN 0
 END AllocFollow;

 PROCEDURE FreeFollow(num: CARD16);
 BEGIN
  IF num <> 0 THEN
   follow[num].used:= FALSE
  END
 END FreeFollow;

 PROCEDURE CheckFollow(num: CARD16; newtime, step: CARD32; src: ObjPtr; chkall: BOOLEAN);
  VAR
   rx, ry: INT32;
   sx, sy, cx, cy: INT16;
   count, dist, cnt, rem: CARD16;
 BEGIN
  WITH follow[num] DO
   IF (wait = 0) AND
      (NOT(bestObj^.kind IN AnimSet{ALIEN3..ALIEN1, MACHINE}) OR
      (bestObj^.hitSubLife + bestObj^.fireSubLife = 0)) THEN
    ResetFollow(num, lasttime)
   END;
   WHILE lasttime < newtime DO
    INC(lasttime, step);
    IF NOT(finish) THEN
     IF (wait > 0) AND (bestObj <> NIL) THEN DEC(wait) END;
     IF (currentObj = NIL) OR (currentObj = tailObj) THEN
      SetNextFollowAnim(num)
     ELSIF currentObj^.kind <> anim THEN
      currentObj:= First(animList[anim])
     ELSE
      count:= 0;
      IF chkall THEN
       FOR cnt:= 1 TO MAXF DO
        IF follow[cnt].used AND (follow[cnt].bestObj = currentObj) THEN
         INC(count)
        END
       END;
       IF count > 4 THEN count:= 4 END
      END;
      GetCenter(src, sx, sy);
      GetCenter(currentObj, cx, cy);
      WITH currentObj^ DO rem:= hitSubLife + fireSubLife END;
      IF rem > 100 THEN rem:= 100 END;
      rx:= sx + src^.vx DIV 32 - cx;
      ry:= sy + src^.vy DIV 32 - cy;
      dist:= SQRT(rx * rx + ry * ry);
      INC(dist, 100 - rem);
      INC(dist, count * 128);
      IF (dist < bestDist) AND (rem > 0) THEN
       bestObj:= currentObj;
       bestCount:= count; bestDist:= dist
      END;
      currentObj:= Next(currentObj^.animNode)
     END
    ELSIF bestObj <> NIL THEN
     wait:= 0
    ELSE
     ResetFollow(num, lasttime)
    END
   END
  END
 END CheckFollow;

 VAR
  gunFireEffect, gunBombEffectL, gunKillEffect,
   fbKillEffect, laserFireEffect, bubbleBombEffect,
   starFireEffect, grenadeBombEffect, ballFireEffect1,
   ballFireEffect2, ballFireEffect3: ARRAY[0..0] OF Effect;
  gunBombEffectR: ARRAY[0..1] OF Effect;
  fbFireEffect: ARRAY[0..2] OF Effect;
  laserBombEffectL, laserBombEffectR: ARRAY[0..8] OF Effect;
  bubbleFireEffectL: ARRAY[0..7] OF Effect;
  bubbleFireEffectR: ARRAY[0..12] OF Effect;
  fireFireEffect: ARRAY[0..7] OF Effect;
  ballBombEffectR: ARRAY[0..30] OF Effect;
  ballBombEffectL: ARRAY[0..15] OF Effect;
  grenadeFireEffect: ARRAY[0..3] OF Effect;

 PROCEDURE MakeGun(gun: ObjPtr);
 BEGIN
  SetObjLoc(gun, 240, 20, 6, 6);
  SetObjRect(gun, 0, 0, 6, 6)
 END MakeGun;

 PROCEDURE MakeFB(fb: ObjPtr);
  VAR
   px, py: INT16;
 BEGIN
  WITH fb^ DO
   IF stat = 0 THEN
    IF temperature < 15000 THEN
     px:= 0; py:= 56
    ELSIF temperature < 21000 THEN
     px:= 0; py:= 66
    ELSIF shapeSeq = 0 THEN
     px:= 10; py:= 56
    ELSE
     px:= 10; py:= 66
    END;
    SetObjLoc(fb, px, py, 10, 10);
    SetObjRect(fb, 0, 0, 10, 10)
   ELSE
    SetObjLoc(fb, 192, 56, 32, 32);
    SetObjRect(fb, 0, 0, 32, 32)
   END
  END
 END MakeFB;

 PROCEDURE MakeLaser(laser: ObjPtr);
  VAR
   dt, sz, py: CARD16;
 BEGIN
  WITH laser^ DO
   IF life <= 70 THEN dt:= 1
   ELSIF life <= 100 THEN dt:= 2
   ELSIF life <= 130 THEN dt:= 3
   ELSE dt:= 4
   END;
   sz:= dt * 2 + 2;
   IF shapeSeq = 0 THEN py:= 50 ELSE py:= 60 END
  END;
  SetObjLoc(laser, 238 - dt, py - dt, sz, sz);
  SetObjRect(laser, 0, 0, sz, sz)
 END MakeLaser;

 PROCEDURE MakeBubble(bubble: ObjPtr);
  VAR
   px, py, sz: INT16;
 BEGIN
  WITH bubble^ DO
   sz:= 10;
   IF life <= 20 THEN px:= 20; py:= 56; sz:= 8
   ELSIF life <= 30 THEN px:= 20; py:= 66
   ELSIF life <= 40 THEN px:= 30; py:= 56
   ELSE px:= 30; py:= 66
   END
  END;
  SetObjLoc(bubble, px, py, sz, sz);
  SetObjRect(bubble, 0, 0, sz, sz)
 END MakeBubble;

 PROCEDURE MakeFire(fire: ObjPtr);
  VAR
   px, sz: INT16;
 BEGIN
  WITH fire^ DO
   IF life <= 1 THEN px:= 169; sz:= 5
   ELSIF life = 2 THEN px:= 153; sz:= 8
   ELSE px:= 131; sz:= 11
   END;
   IF shapeSeq <> 0 THEN INC(px, sz) END
  END;
  SetObjLoc(fire, px, 54, sz, sz);
  INC(sz);
  SetObjRect(fire, -1, -1, sz, sz)
 END MakeFire;

 PROCEDURE MakeBall(ball: ObjPtr);
  VAR
   offset, px, py: INT16;
   max: CARD16;
 BEGIN
  WITH ball^ DO
   IF life > 50 THEN
    offset:= 22; max:= 3
   ELSIF life > 40 THEN
    offset:= 18; max:= 4
   ELSIF life > 30 THEN
    offset:= 12; max:= 6
   ELSE
    offset:= 0; max:= 12
   END;
   INC(offset, stat MOD max);
   px:= (offset MOD 10) * 11 + 76;
   py:= (offset DIV 10) * 11 + 32;
   SetObjLoc(ball, px, py, 11, 11);
   SetObjRect(ball, 0, 0, 11, 11)
  END
 END MakeBall;

 PROCEDURE MakeStar(star: ObjPtr);
  VAR
   py: INT16;
 BEGIN
  IF star^.shapeSeq DIV 32 = 0 THEN py:= 0 ELSE py:= 12 END;
  SetObjLoc(star, 146, py, 12, 12);
  SetObjRect(star, 0, 0, 12, 12)
 END MakeStar;

 PROCEDURE MakeGrenade(grenade: ObjPtr);
  VAR
   px, py: INT16;
 BEGIN
  WITH grenade^ DO
   IF stat = 0 THEN
    SetObjLoc(grenade, 244, 52, 12, 8);
    SetObjRect(grenade, 0, 0, 12, 8)
   ELSE
    IF stat = 1 THEN px:= 244; py:= 60
    ELSIF stat = 2 THEN px:= 244; py:= 72
    ELSE px:= 232; py:= 72
    END;
    SetObjLoc(grenade, px, py, 12, 12);
    SetObjRect(grenade, 0, 0, 12, 12)
   END
  END
 END MakeGrenade;

 PROCEDURE ResetGun(gun: ObjPtr);
  VAR
   power: CARD16;
 BEGIN
  power:= GetPower(GUN);
  WITH gun^ DO
   life:= 8 + power * 2;
   IF life = 16 THEN life:= 24 END;
   moveSeq:= Period * 2 + Period DIV 2 * power;
   hitSubLife:= life DIV 2;
   fireSubLife:= life - hitSubLife
  END;
  MakeGun(gun)
 END ResetGun;

 PROCEDURE ResetFB(fb: ObjPtr);
  VAR
   p: CARD16;
 BEGIN
  WITH fb^ DO
   moveSeq:= Period * 30;
   shapeSeq:= RND() MOD 2;
   p:= GetPower(FB);
   life:= 37 + p * 5;
   hitSubLife:= 7;
   fireSubLife:= 28;
   IF (stat = 0) AND (p >= 3) THEN moveSeq:= 1 ELSE moveSeq:= 0 END;
   IF stat = 0 THEN
    temperature:= p * 6000
   ELSE
    hitSubLife:= 0; fireSubLife:= 0
   END
  END;
  MakeFB(fb)
 END ResetFB;

 PROCEDURE ResetLaser(laser: ObjPtr);
  VAR
   power: CARD16;
 BEGIN
  power:= GetPower(LASER);
  WITH laser^ DO
   shapeSeq:= RND() MOD 2;
   moveSeq:= Period DIV 8;
   life:= power * 30 + 40;
   IF stat = 1 THEN INC(life, 50) END;
   IF power = 4 THEN stat:= AllocFollow(lasttime) ELSE stat:= 0 END;
   fireSubLife:= life DIV 5;
   hitSubLife:= life - fireSubLife
  END;
  MakeLaser(laser)
 END ResetLaser;

 PROCEDURE ResetBubble(bubble: ObjPtr);
  VAR
   power: CARD16;
 BEGIN
  power:= GetPower(BUBBLE);
  WITH bubble^ DO
   moveSeq:= Period * 30;
   life:= power * 10 + 8;
   fireSubLife:= 0;
   hitSubLife:= life
  END;
  MakeBubble(bubble)
 END ResetBubble;

 PROCEDURE ResetFire(fire: ObjPtr);
 BEGIN
  WITH fire^ DO
   hitSubLife:= 0;
   fireSubLife:= life;
   moveSeq:= Period DIV 3;
   shapeSeq:= RND() MOD 2
  END;
  MakeFire(fire)
 END ResetFire;

 PROCEDURE ResetBall(ball: ObjPtr);
 BEGIN
  WITH ball^ DO
   life:= GetPower(BALL) * 10 + 20;
   hitSubLife:= life * 2 DIV 5;
   fireSubLife:= life - hitSubLife;
   stat:= RND() MOD 12;
   IF RND() MOD 2 = 0 THEN INCL(flags, nested) ELSE EXCL(flags, nested) END;
   shapeSeq:= Period DIV 10;
   moveSeq:= AllocFollow(lasttime)
  END;
  MakeBall(ball)
 END ResetBall;

 PROCEDURE ResetStar(star: ObjPtr);
  VAR
   power: CARD16;
 BEGIN
  power:= GetPower(STAR);
  WITH star^ DO
   life:= power * 20;
   fireSubLife:= life * 2 DIV 5;
   hitSubLife:= life - fireSubLife;
   IF displayed IN flags THEN
    stat:= Period DIV 6
   ELSE
    moveSeq:= Period * 29 + Period DIV 2;
    shapeSeq:= (RND() MOD 2) * 32;
    stat:= 0
   END
  END;
  MakeStar(star)
 END ResetStar;

 PROCEDURE ResetGrenade(grenade: ObjPtr);
 BEGIN
  WITH grenade^ DO
   shapeSeq:= Period DIV 5;
   IF stat <> 0 THEN
    temperature:= MaxHot;
    moveSeq:= Period * 30
   ELSE
    moveSeq:= Period * 3
   END;
   life:= 1
  END;
  MakeGrenade(grenade)
 END ResetGrenade;

 PROCEDURE MoveGun(gun: ObjPtr);
  VAR
   kill: BOOLEAN;
 BEGIN
  WITH gun^ DO
   DoCollision(gun, AnimSet{ALIEN3..ALIEN1, MACHINE},
               Aie, hitSubLife, fireSubLife);
   kill:= InBackground(gun);
   IF kill THEN
    SoundEffect(gun, gunKillEffect);
    vx:= 0; vy:= 0;
    Boum(gun, StoneSet{stFLAME2}, slowStyle, GetPower(GUN), 1)
   END;
   IF kill OR OutOfScreen(gun) OR OutOfBounds(gun) OR
      (step > moveSeq) OR (hitSubLife + fireSubLife < life) THEN
    Die(gun); RETURN
   END;
   DEC(moveSeq, step);
   UpdateXY(gun)
  END
 END MoveGun;

 PROCEDURE KillIt(victim, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  WITH src^ DO hitSubLife:= 100; fireSubLife:= 100 END;
  Die(victim)
 END KillIt;

 PROCEDURE MoveFB(fb: ObjPtr);
  VAR
   Do: AieProc;
   kill: BOOLEAN;
 BEGIN
  WITH fb^ DO
   IF stat = 0 THEN Do:= Aie ELSE Do:= KillIt END;
   DoCollision(fb, AnimSet{ALIEN3..ALIEN1, MISSILE, MACHINE},
                   Do, hitSubLife, fireSubLife);
   kill:= (stat = 0) AND InBackground(fb);
   IF kill THEN
    SoundEffect(fb, fbKillEffect);
    vx:= vx DIV 4; vy:= vy DIV 4;
    Boum(fb, StoneSet{stFOG2}, slowStyle, GetPower(FB) * 2, 1)
   END;
   IF kill OR OutOfScreen(fb) OR OutOfBounds(fb) OR
     ((stat = 0) AND (hitSubLife + fireSubLife = 0)) THEN
    Die(fb); RETURN
   END;
   IF moveSeq > 0 THEN
    IF step >= moveSeq THEN
     IF stat = 0 THEN
      shapeSeq:= 1 - shapeSeq;
      MakeFB(fb);
      IF hitSubLife < 7 THEN INC(hitSubLife) END;
      IF fireSubLife < 28 THEN INC(fireSubLife) END;
      INC(moveSeq, Period DIV 16)
     ELSE
      Die(fb); RETURN
     END
    END;
    IF step >= moveSeq THEN moveSeq:= 1 ELSE DEC(moveSeq, step) END
   END;
   Burn(fb);
   UpdateXY(fb)
  END
 END MoveFB;

 PROCEDURE MoveLaser(laser: ObjPtr);
  VAR
   rx, ry, rl: INT32;
   bx, by, lx, ly: INT16;
   kill: BOOLEAN;
 BEGIN
  WITH laser^ DO
   DoCollision(laser, AnimSet{ALIEN3..ALIEN1, MACHINE},
               Aie, hitSubLife, fireSubLife);
   life:= hitSubLife + fireSubLife;
   kill:= InBackground(laser);
   IF kill THEN
    SoundEffect(laser, gunKillEffect);
    vx:= 0; vy:= 0;
    Boum(laser, StoneSet{stFLAME1}, slowStyle, GetPower(LASER) * 2, 1)
   END;
   IF kill OR OutOfScreen(laser) OR OutOfBounds(laser) OR (life = 0) THEN
    FreeFollow(stat);
    Die(laser); RETURN
   END;
   IF step >= moveSeq THEN
    shapeSeq:= 1 - shapeSeq;
    MakeLaser(laser);
    IF (nbAnim[ALIEN3] + nbAnim[ALIEN2] + nbAnim[ALIEN1] = 0) AND
       (ABS(vx) < 384) AND (ABS(vy) < 384) THEN
     ay:= 1
    END;
    INC(moveSeq, Period DIV 8)
   END;
   IF step > moveSeq THEN moveSeq:= 0 ELSE DEC(moveSeq, step) END;
   IF stat <> 0 THEN
    CheckFollow(stat, lasttime, Period DIV 50, laser, TRUE);
    WITH follow[stat] DO
     IF (wait = 0) THEN
      GetCenter(bestObj, bx, by);
      INC(bx, bestObj^.vx DIV 128);
      INC(by, bestObj^.vy DIV 128);
      GetCenter(laser, lx, ly);
      rx:= bx - lx; ry:= by - ly;
      rl:= SQRT(rx * rx + ry * ry);
      IF rl <> 0 THEN
       rx:= rx * 95 DIV rl;
       ry:= ry * 95 DIV rl
      END;
      DEC(rx, vx DIV 128);
      DEC(ry, vy DIV 128);
      IF rx > 127 THEN rx:= 127 ELSIF rx < -127 THEN rx:= -127 END;
      IF ry > 127 THEN ry:= 127 ELSIF ry < -127 THEN ry:= -127 END;
      SetObjAXY(laser, rx, ry)
     END;
     finish:= FALSE
    END
   END;
   UpdateXY(laser)
  END
 END MoveLaser;

 PROCEDURE MoveBubble(bubble: ObjPtr);
  VAR
   clone: ObjPtr;
   px, py, nvx, nvy: INT16;
 BEGIN
  WITH bubble^ DO
   DoCollision(bubble, AnimSet{ALIEN3..ALIEN1, MACHINE},
               Aie, hitSubLife, fireSubLife);
   IF (hitSubLife = 0) OR OutOfScreen(bubble) OR
      OutOfBounds(bubble) OR (step > moveSeq) THEN
    Die(bubble); RETURN
   ELSIF (life > hitSubLife) OR InBackground(bubble) THEN
    IF stat = 0 THEN
     IF (life > hitSubLife) OR (hitSubLife <= 5) THEN
      IF (life = hitSubLife) THEN
       SoundEffect(bubble, gunKillEffect)
      END;
      Die(bubble); RETURN
     ELSE
      AvoidBackground(bubble, 4);
      DEC(life, 5); DEC(hitSubLife, 5)
     END
    ELSE
     DEC(stat);
     life:= hitSubLife;
     MakeBubble(bubble);
     AvoidBackground(bubble, 4);
     GetCenter(bubble, px, py);
     nvx:= vx - 1024; nvy:= vy - 1024;
     INC(nvx, RND() MOD 2048);
     INC(nvy, RND() MOD 2048);
     clone:= CreateObj(WEAPON, ORD(BUBBLE), px, py, stat, 0);
     SetObjVXY(clone, nvx, nvy)
    END
   END;
   DEC(moveSeq, step);
   UpdateXY(bubble)
  END
 END MoveBubble;

 PROCEDURE MoveFire(fire: ObjPtr);
 BEGIN
  WITH fire^ DO
   IF stat = 0 THEN
    DoCollision(fire, AnimSet{ALIEN3..ALIEN1, MACHINE},
                Aie, hitSubLife, fireSubLife)
   END;
   IF (life > fireSubLife) OR InBackground(fire) OR (life = 0) OR water THEN
    vx:= vx DIV 2; vy:= vy DIV 2;
    Die(fire); RETURN
   END;
   shapeSeq:= 1 - shapeSeq;
   IF step > stat THEN stat:= 0 ELSE DEC(stat, step) END;
   IF step >= moveSeq THEN
    INC(moveSeq, Period DIV 4);
    DEC(life, (life DIV 5) + 1); fireSubLife:= life
   END;
   DEC(moveSeq, step);
   MakeFire(fire);
   UpdateXY(fire)
  END
 END MoveFire;

 PROCEDURE MoveBall(ball: ObjPtr);
  VAR
   rx, ry, rl: INT32;
   dx, dy, px, py, fx, fy, dv, ds: INT16;
   spd: CARD16;
   kill: BOOLEAN;
 BEGIN
  WITH ball^ DO
   DoCollision(ball, AnimSet{ALIEN3..ALIEN1, MACHINE},
               Aie, hitSubLife, fireSubLife);
   life:= hitSubLife + fireSubLife;
   hitSubLife:= life * 2 DIV 5;
   fireSubLife:= life - hitSubLife;
   kill:= InBackground(ball);
   IF kill THEN
    SoundEffect(ball, fbKillEffect);
    vx:= vx DIV 4; vy:= vy DIV 4;
    Boum(ball, StoneSet{stFOG2}, slowStyle, life DIV 16 + 1, 1)
   END;
   IF OutOfScreen(ball) OR OutOfBounds(ball) OR kill OR (life = 0) OR
      ((vx = 0) AND (vy = 0)) THEN
    FreeFollow(moveSeq);
    Die(ball); RETURN
   END;
   IF step >= shapeSeq THEN
    INC(shapeSeq, Period DIV 10);
    IF nested IN flags THEN
     stat:= (stat + 1) MOD 12
    ELSIF stat = 0 THEN
     stat:= 11
    ELSE
     DEC(stat)
    END;
    MakeBall(ball)
   END;
   IF step > shapeSeq THEN shapeSeq:= 0 ELSE DEC(shapeSeq, step) END;
   rx:= vx; ry:= vy;
   rl:= SQRT(rx * rx + ry * ry);
   IF rl <> 0 THEN
    vx:= rx * 2200 DIV rl;
    vy:= ry * 2200 DIV rl
   ELSE
    vx:= 2200
   END;
   IF moveSeq = 0 THEN
    moveSeq:= AllocFollow(lasttime)
   ELSE
    IF life > 50 THEN spd:= Period DIV 30 ELSE spd:= Period DIV 12 END;
    CheckFollow(moveSeq, lasttime, spd, ball, (life > 50));
    WITH follow[moveSeq] DO
     IF (wait = 0) THEN
      GetCenter(bestObj, fx, fy);
      GetCenter(ball, px, py);
      dx:= fx - px; dy:= fy - py;
      WHILE ABS(dx) > 256 DO dx:= dx DIV 2 END;
      WHILE ABS(dy) > 256 DO dy:= dy DIV 2 END;
      fx:= vy DIV 64; fy:= vx DIV 64;
      IF (fx * dx) < (fy * dy) THEN
       fx:= -fx
      ELSE
       fy:= -fy
      END;
      dv:= step;
      IF life > 50 THEN ds:= 9
      ELSIF life > 40 THEN ds:= 7
      ELSIF life > 30 THEN ds:= 5
      ELSE ds:= 3
      END;
      INC(vx, fx * dv * ds DIV 16);
      INC(vy, fy * dv * ds DIV 16);
      dvx:= vx; dvy:= vy
     END
    END
   END;
   UpdateXY(ball)
  END
 END MoveBall;

 PROCEDURE MoveStar(star: ObjPtr);
  VAR
   px, py, sx, sy: INT16;
   nvx, nvy, nvl: INT32;
   anims: AnimSet;
 BEGIN
  WITH star^ DO
   IF (ax <> 0) OR (ay <> 0) THEN HALT END;
   IF stat = 0 THEN
    anims:= AnimSet{ALIEN3..ALIEN1};
    IF weaponAttr[STAR].power > 2 THEN INCL(anims, MACHINE) END;
    DoCollision(star, anims, Aie, hitSubLife, fireSubLife);
    IF life > hitSubLife + fireSubLife THEN ResetStar(star) END
   ELSIF step > stat THEN
    stat:= 0
   ELSE
    DEC(stat, step)
   END;
   IF (step > moveSeq) OR OutOfScreen(star) OR
    ((moveSeq < Period * 29) AND Collision(star, mainPlayer)) THEN
    Die(star); RETURN
   END;
   DEC(moveSeq, step);
   shapeSeq:= (shapeSeq + step) MOD 64;
   IF moveSeq < Period * 29 THEN
    GetCenter(mainPlayer, px, py);
    GetCenter(star, sx, sy);
    nvx:= px - sx; nvy:= py - sy;
    nvl:= SQRT(nvx * nvx + nvy * nvy);
    IF nvl <> 0 THEN
     nvx:= nvx * 3000 DIV nvl;
     nvy:= nvy * 3000 DIV nvl
    END;
    dvx:= nvx; dvy:= nvy
   END;
   MakeStar(star);
   UpdateXY(star);
   INCL(flags, displayed)
  END
 END MoveStar;

 PROCEDURE MoveGrenade(grenade: ObjPtr);
 BEGIN
  WITH grenade^ DO
   dvx:= 0; dvy:= 0;
   AvoidBounds(grenade, 4);
   AvoidBackground(grenade, 4);
   IF stat <> 0 THEN
    IF (step >= shapeSeq) THEN
     INC(shapeSeq, Period DIV 5);
     IF stat = 3 THEN stat:= 1 ELSE INC(stat) END;
     MakeGrenade(grenade)
    END;
    mainPlayer:= grenade;
    DEC(shapeSeq, step);
    Burn(grenade);
    Gravity(grenade, AnimSet{ALIEN3..ALIEN1, MACHINE})
   END;
   IF (step > moveSeq) THEN
    IF stat = 0 THEN
     IF GetPower(GRENADE) = 4 THEN
      StdBomb(grenade, BUBBLE, 3000, 0, 15, FALSE);
      SoundEffect(grenade, bubbleBombEffect)
     ELSE
      SoundEffect(grenade, grenadeBombEffect);
      StdBomb(grenade, GUN, 3250, 1, 3, FALSE)
     END
    END;
    Die(grenade); RETURN
   ELSE
    DEC(moveSeq, step)
   END;
   UpdateXY(grenade)
  END
 END MoveGrenade;

 PROCEDURE FireGun(player: ObjPtr);
 BEGIN
  SoundEffect(player, gunFireEffect);
  INC(shoot.total);
  StdFire(player, GUN, 3250, FALSE, FALSE)
 END FireGun;

 PROCEDURE FireFB(player: ObjPtr);
 BEGIN
  IF CheckBullet(FB) THEN
   SoundEffect(player, fbFireEffect);
   StdFire(player, FB, 1000, TRUE, FALSE)
  END
 END FireFB;

 PROCEDURE FireLaser(player: ObjPtr);
 BEGIN
  IF CheckBullet(LASER) THEN
   SoundEffect(player, laserFireEffect);
   StdFire(player, LASER, 4090, FALSE, TRUE)
  END
 END FireLaser;

 PROCEDURE FireBubble(player: ObjPtr);
  VAR
   bvx, bvy: INT16;
 BEGIN
  IF CheckBullet(BUBBLE) THEN
   StereoEffect;
   SoundEffect(player, bubbleFireEffectL);
   SoundEffect(player, bubbleFireEffectR);
   StdFire(player, BUBBLE, 2000, TRUE, FALSE);
   IF (GetPower(BUBBLE) = 4) AND (newWeapon <> ADR(nlObj)) THEN
    WITH newWeapon^ DO bvx:= vx DIV 4; bvy:= vy DIV 4 END;
    StdFire(player, BUBBLE, 2000, TRUE, FALSE);
    WITH newWeapon^ DO
     SetObjVXY(newWeapon, vx + bvy, vy - bvx)
    END;
    StdFire(player, BUBBLE, 2000, TRUE, FALSE);
    WITH newWeapon^ DO
     SetObjVXY(newWeapon, vx - bvy, vy + bvx)
    END
   END
  END
 END FireBubble;

 PROCEDURE FireFire(player: ObjPtr);
  CONST
   speed = 4000;
   diaspeed = 2828;
  VAR
   fire: ObjPtr;
   power, cnt: CARD16;
   nvx, nvy, svx, svy, px, py: INT16;
 BEGIN
  IF CheckBullet(FIRE) THEN
   SoundEffect(player, fireFireEffect);
   power:= GetPower(FIRE);
   INC(shoot.total, power);
   cnt:= power * 2;
   GetCenter(player, px, py);
   nvx:= 0; nvy:= 0;
   CASE player^.shapeSeq OF
     0: nvy:= -speed
    |1: nvy:= -diaspeed; nvx:= diaspeed
    |2: nvx:= speed
    |3: nvx:= diaspeed; nvy:= nvx
    |4: nvy:= speed
    |5: nvy:= diaspeed; nvx:= -diaspeed
    |6: nvx:= -speed
    |7: nvx:= -diaspeed; nvy:= nvx
   END;
   IF power >= 4 THEN power:= 5 END;
   WHILE cnt > 0 DO
    svx:= nvx - 512; INC(svx, RND() MOD 1024);
    svy:= nvy - 512; INC(svy, RND() MOD 1024);
    fire:= CreateObj(WEAPON, ORD(FIRE), px, py, 0, power);
    EXCL(fire^.flags, nested);
    SetObjVXY(fire, svx, svy);
    DEC(cnt)
   END
  END
 END FireFire;

 PROCEDURE FireBall(player: ObjPtr);
  VAR
   rnd: CARD16;
 BEGIN
  IF CheckBullet(BALL) THEN
   rnd:= RND() MOD 8;
   IF rnd < 3 THEN
    SoundEffect(player, ballFireEffect1)
   ELSIF rnd < 6 THEN
    SoundEffect(player, ballFireEffect3)
   ELSE
    SoundEffect(player, ballFireEffect2)
   END;
   StdFire(player, BALL, 1024, FALSE, FALSE)
  END
 END FireBall;

 PROCEDURE FireStar(player: ObjPtr);
  VAR
   attr: ObjAttrPtr;
   max: CARD16;
 BEGIN
  IF CheckBullet(STAR) THEN
   IF GetPower(STAR) = 4 THEN max:= 3 ELSE max:= 1 END;
   attr:= GetAnimAttr(WEAPON, ORD(STAR));
   IF attr^.nbObj < max THEN
    SoundEffect(player, starFireEffect);
    StdFire(player, STAR, 3000, TRUE, TRUE)
   END
  END
 END FireStar;

 PROCEDURE FireGrenade(player: ObjPtr);
 BEGIN
  IF CheckBullet(GRENADE) THEN
   SoundEffect(player, grenadeFireEffect);
   StdFire(player, GRENADE, 2000, TRUE, FALSE)
  END
 END FireGrenade;

 PROCEDURE GunBomb(player: ObjPtr);
 BEGIN
  IF CheckBomb(GUN) THEN
   PopMessage(ADL("Bomb"), statPos, 1);
   StereoEffect;
   SoundEffect(player, gunBombEffectL);
   SoundEffect(player, gunBombEffectR);
   StdBomb(player, GUN, 3250, 1, 20, FALSE)
  END
 END GunBomb;

 PROCEDURE FBBomb(player: ObjPtr);
 BEGIN
  IF CheckBomb(FB) THEN
   PopMessage(ADL("Cold Fire"), statPos, 2);
   StdBomb(player, FB, 2090, 1, 30, FALSE)
  END
 END FBBomb;

 PROCEDURE LaserBomb(player: ObjPtr);
 BEGIN
  IF CheckBomb(LASER) THEN
   PopMessage(ADL("Supernovae"), statPos, 2);
   StereoEffect;
   SoundEffect(player, laserBombEffectL);
   SoundEffect(player, laserBombEffectR);
   StdBomb(player, LASER, 4090, 1, 30, FALSE)
  END
 END LaserBomb;

 PROCEDURE BubbleBomb(player: ObjPtr);
  VAR
   q: CARD16;
 BEGIN
  IF CheckBomb(BUBBLE) THEN
   PopMessage(ADL("Fragmentation Bomb"), statPos, 2);
   q:= GetPower(BUBBLE) + 2;
   SoundEffect(player, bubbleBombEffect);
   StdBomb(player, BUBBLE, 2000, q, q, TRUE)
  END
 END BubbleBomb;

 PROCEDURE FireBomb(player: ObjPtr);
  VAR
   obj, tail: ObjPtr;
   a: Anims;
 BEGIN
  IF CheckBomb(FIRE) THEN
   PopMessage(ADL("Pyrotechnics"), statPos, 2);
   FOR a:= MIN(Anims) TO MAX(Anims) DO
    IF a IN AnimSet{ALIEN3..ALIEN1, MACHINE} THEN
     obj:= First(animList[a]);
     tail:= Tail(animList[a]);
     WHILE obj <> tail DO
      IF NOT OutOfScreen(obj) THEN
       obj^.temperature:= MaxHot
      END;
      obj:= Next(obj^.animNode)
     END
    END
   END
  END
 END FireBomb;

 PROCEDURE BallBomb(player: ObjPtr);
 BEGIN
  IF CheckBomb(BALL) THEN
   PopMessage(ADL("Hunters"), statPos, 2);
   StereoEffect;
   SoundEffect(player, ballBombEffectR);
   SoundEffect(player, ballBombEffectL);
   StdBomb(player, BALL, 1024, 0, 24, FALSE)
  END
 END BallBomb;

 PROCEDURE StarBomb(player: ObjPtr);
  VAR
   obj, next, tail: ObjPtr;
   hit, fire: CARD16;
   a: Anims;
 BEGIN
  IF CheckBomb(STAR) THEN
   PopMessage(ADL("Big Bang"), statPos, 2);
   SoundEffect(player, gunFireEffect);
   FOR a:= ALIEN3 TO MACHINE DO
    IF a IN AnimSet{ALIEN3, ALIEN2, ALIEN1, MACHINE} THEN
     obj:= First(animList[a]); tail:= Tail(animList[a]);
     WHILE obj <> tail DO
      next:= Next(obj^.animNode);
      IF NOT OutOfScreen(obj) THEN
       hit:= 100; fire:= 100;
       Aie(obj, player, hit, fire)
      END;
      obj:= next
     END
    END
   END
  END
 END StarBomb;

 PROCEDURE GrenadeBomb(player: ObjPtr);
 BEGIN
  IF CheckBomb(GRENADE) THEN
   PopMessage(ADL("Black Hole"), statPos, 2);
   StdBomb(player, GRENADE, 0, 1, 1, TRUE)
  END
 END GrenadeBomb;

 PROCEDURE InitParams;
  VAR
   attrs: ObjAttr;
   attr: ObjAttrPtr;
   c, d, e: CARD16;
  PROCEDURE AddAttrs;
   VAR
    attr: ObjAttrPtr;
  BEGIN
   attr:= AllocMem(SIZE(ObjAttr));
   CheckMem(attr);
   attr^:= attrs;
   AddTail(attrList[WEAPON], attr^.node)
  END AddAttrs;
 BEGIN
  FOR c:= 1 TO MAXF DO follow[c].used:= FALSE END;
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  attrs:= attr^;
  FreeMem(attr);
  WITH attrs DO
    (* Gun *)
   SetEffect(gunFireEffect[0], soundList[sGun], 0, 4181, 50, 2);
   SetEffect(gunBombEffectL[0], soundList[sPouf], 0, 8363, 160, 5);
   SetEffect(gunBombEffectR[0], soundList[sPouf], 167, 8363, 0, 5);
   SetEffect(gunBombEffectR[1], soundList[sPouf], 0, 8363, 160, 5);
   SetEffect(gunKillEffect[0], soundList[wWhite], 1673, 16726, 8, 0);
   Fire[GUN]:= FireGun;
   Bomb[GUN]:= GunBomb;
   Reset:= ResetGun;
   Make:= MakeGun;
   Move:= MoveGun;
   weight:= 24; charge:= 90;
   basicType:= NotBase;
   priority:= 50;
   toKill:= TRUE;
   AddAttrs;
    (* FB *)
   SetEffect(fbFireEffect[0], soundList[wNoise], 418, 4181, 120, 4);
   SetEffect(fbFireEffect[1], soundList[wNoise], 627, 6265, 120, 4);
   SetEffect(fbFireEffect[2], soundList[wNoise], 527, 5268, 120, 4);
   SetEffect(fbKillEffect[0], soundList[wNoise], 1673, 16726, 16, 0);
   Fire[FB]:= FireFB;
   Bomb[FB]:= FBBomb;
   Reset:= ResetFB;
   Make:= MakeFB;
   Move:= MoveFB;
   weight:= 60; charge:= 6;
   basicType:= NotBase;
   priority:= 51;
   toKill:= TRUE;
   AddAttrs;
    (* Laser *)
   SetEffect(laserFireEffect[0], soundList[sLaser], 0, 8363, 100, 3);
   FOR c:= 0 TO 3 DO
    d:= (c + 1) * 48;
    SetEffect(laserBombEffectL[c], soundList[wNoise], 523, 4181, d, 5);
    SetEffect(laserBombEffectL[8 - c], nulSound, 1045, 4181, d, 5)
   END;
   SetEffect(laserBombEffectL[4], nulSound, 1045, 4181, 192, 5);
   SetEffect(laserBombEffectR[0], soundList[wNoise], 4181, 8363, 96, 5);
   FOR c:= 1 TO 8 DO
    d:= (9 - c) * 1045 + 8366;
    e:= (9 - c) * 30;
    SetEffect(laserBombEffectR[c], nulSound, 1673, d, e, 5)
   END;
   Fire[LASER]:= FireLaser;
   Bomb[LASER]:= LaserBomb;
   Reset:= ResetLaser;
   Make:= MakeLaser;
   Move:= MoveLaser;
   weight:= 40; charge:= 120;
   basicType:= NotBase;
   priority:= 51;
   toKill:= TRUE;
   AddAttrs;
    (* Bubble *)
   SetEffect(bubbleBombEffect[0], soundList[sCannon], 0, 0, 192, 5);
   SetEffect(bubbleFireEffectL[0], soundList[wWhite], 1097, 16726, 120, 3);
   SetEffect(bubbleFireEffectR[0], soundList[wWhite], 2090, 16726, 120, 3);
   FOR c:= 1 TO 7 DO
    SetEffect(bubbleFireEffectL[c], nulSound, 1097, 16726, (8 - c) * 15, 3)
   END;
   FOR c:= 0 TO 3 DO
    d:= 4 - c;
    SetEffect(bubbleFireEffectR[c * 3 + 1], nulSound, 279, 8363, d * 25, 3);
    SetEffect(bubbleFireEffectR[c * 3 + 2], nulSound, 318, 12544, d * 25, 3);
    SetEffect(bubbleFireEffectR[c * 3 + 3], nulSound, 558, 16726, d * 25, 3)
   END;
   Fire[BUBBLE]:= FireBubble;
   Bomb[BUBBLE]:= BubbleBomb;
   Reset:= ResetBubble;
   Make:= MakeBubble;
   Move:= MoveBubble;
   weight:= 10; charge:= 80;
   basicType:= NotBase;
   priority:= 51;
   toKill:= TRUE;
   AddAttrs;
    (* Fire *)
   FOR c:= 0 TO 3 DO
    d:= c + 1;
    SetEffect(fireFireEffect[c], nulSound, 300, 4181, d * 20, 3);
    SetEffect(fireFireEffect[7 - c], nulSound, 400, 4181, d * 20, 3)
   END;
   SetEffect(fireFireEffect[0], soundList[wNoise], 300, 4181, 20, 3);
   Fire[FIRE]:= FireFire;
   Bomb[FIRE]:= FireBomb;
   Reset:= ResetFire;
   Make:= MakeFire;
   Move:= MoveFire;
   weight:= 3; charge:= 30;
   dieStone:= 1; dieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stFOG1};
   dieSKCount:= 1;
   basicType:= NotBase;
   priority:= 55;
   toKill:= TRUE;
   AddAttrs;
   dieStone:= 0;
    (* Ball *)
   SetEffect(ballFireEffect1[0], soundList[aPanflute], 0, 8363, 110, 3);
   SetEffect(ballFireEffect2[0], soundList[aPanflute], 0, 9387, 110, 3);
   SetEffect(ballFireEffect3[0], soundList[aPanflute], 0, 10536, 110, 3);
   SetEffect(ballBombEffectR[0], soundList[aPanflute], 0, 4694, 250, 5);
   FOR c:= 1 TO 6 DO
    SetEffect(ballBombEffectR[c], soundList[wWhite], 0, 18774, c * c * 4, 5)
   END;
   SetEffect(ballBombEffectR[7], soundList[wPanflute], 1671, 12530, 4, 5);
   SetEffect(ballBombEffectR[8], soundList[wPanflute], 1671, 21073, 9, 5);
   SetEffect(ballBombEffectR[9], soundList[wPanflute], 2810, 14065, 14, 5);
   SetEffect(ballBombEffectR[10], soundList[wPanflute], 1875, 18774, 21, 5);
   SetEffect(ballBombEffectR[11], soundList[wPanflute], 2503, 22327, 32, 5);
   SetEffect(ballBombEffectR[12], soundList[wPanflute], 2977, 15787, 50, 5);
   SetEffect(ballBombEffectR[13], soundList[wPanflute], 2105, 12000, 70, 5);
   SetEffect(ballBombEffectR[14], soundList[wPanflute], 1600, 16726, 70, 5);
   SetEffect(ballBombEffectR[15], soundList[wPanflute], 2230, 14000, 70, 5);
   SetEffect(ballBombEffectR[16], soundList[wPanflute], 1867, 18800, 70, 5);
   SetEffect(ballBombEffectR[17], soundList[wPanflute], 2507, 15000, 70, 5);
   SetEffect(ballBombEffectR[18], soundList[wPanflute], 2000, 12500, 70, 5);
   SetEffect(ballBombEffectR[19], soundList[wPanflute], 1667, 11000, 70, 5);
   SetEffect(ballBombEffectR[20], soundList[wPanflute], 1467, 16700, 70, 5);
   SetEffect(ballBombEffectR[21], soundList[wPanflute], 2227, 21000, 70, 5);
   SetEffect(ballBombEffectR[22], soundList[wPanflute], 2800, 25000, 70, 5);
   SetEffect(ballBombEffectR[23], soundList[wPanflute], 3333, 15900, 70, 5);
   SetEffect(ballBombEffectR[24], soundList[wPanflute], 2120, 18700, 70, 5);
   SetEffect(ballBombEffectR[25], soundList[wPanflute], 2493, 22300, 50, 5);
   SetEffect(ballBombEffectR[26], soundList[wPanflute], 2973, 28130, 32, 5);
   SetEffect(ballBombEffectR[27], soundList[wPanflute], 3751, 16726, 21, 5);
   SetEffect(ballBombEffectR[28], soundList[wPanflute], 2230, 22100, 14, 5);
   SetEffect(ballBombEffectR[29], soundList[wPanflute], 2947, 14800, 9, 5);
   SetEffect(ballBombEffectR[30], soundList[wPanflute], 1973, 13000, 4, 5);
   SetEffect(ballBombEffectL[0], soundList[wShakuhachi], 17838, 4694, 80, 5);
   FOR c:= 1 TO 15 DO
    SetEffect(ballBombEffectL[c], nulSound, 588, 4694, (16 - c) * 5, 5)
   END;
   Fire[BALL]:= FireBall;
   Bomb[BALL]:= BallBomb;
   Reset:= ResetBall;
   Make:= MakeBall;
   Move:= MoveBall;
   weight:= 80; charge:= 20;
   basicType:= NotBase;
   priority:= 49;
   toKill:= TRUE;
   AddAttrs;
    (* Star *)
   SetEffect(starFireEffect[0], soundList[sCasserole], 0, 16726, 100, 3);
   Fire[STAR]:= FireStar;
   Bomb[STAR]:= StarBomb;
   Reset:= ResetStar;
   Make:= MakeStar;
   Move:= MoveStar;
   weight:= 10; charge:= 30;
   inerty:= 96;
   priority:= 51;
   basicType:= NotBase;
   toKill:= TRUE;
   AddAttrs;
    (* Grenade *)
   SetEffect(grenadeBombEffect[0], soundList[sPouf], 0, 16726, 160, 5);
   SetEffect(grenadeFireEffect[0], soundList[sHHat], 697, 8363, 120, 3);
   SetEffect(grenadeFireEffect[1], soundList[sCymbale], 697, 8363, 120, 3);
   SetEffect(grenadeFireEffect[2], soundList[sHHat], 1045, 12544, 120, 3);
   SetEffect(grenadeFireEffect[3], soundList[aCrash], 0, 0, 120, 3);
   Fire[GRENADE]:= FireGrenade;
   Bomb[GRENADE]:= GrenadeBomb;
   Reset:= ResetGrenade;
   Make:= MakeGrenade;
   Move:= MoveGrenade;
   weight:= 50; charge:= -120;
   inerty:= 32;
   priority:= 47;
   basicType:= NotBase;
   toKill:= TRUE;
   dieStone:= 4; dieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stFOG2};
   dieSKCount:= 1;
   AddAttrs
  END
 END InitParams;

BEGIN

 InitParams;

END ChaosWeapon.
