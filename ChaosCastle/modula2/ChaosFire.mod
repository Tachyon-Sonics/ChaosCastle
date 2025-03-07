IMPLEMENTATION MODULE ChaosFire;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM Memory IMPORT SET16, CARD8, INT8, CARD16, INT16, CARD32, INT32, First,
  Tail, Next, StrPtr, CopyStr, List, Empty;
 FROM Trigo IMPORT RND, SGN, SQRT, SIN, COS;
 FROM ChaosBase IMPORT Anims, AnimSet, Obj, ObjPtr, ObjAttr, ObjAttrPtr,
  mainPlayer, animList, Frac, step, level, Zone, FirstObj, TailObj, NextObj,
  objList, leftObjList, RestartObj, pLife, addpt;
 FROM ChaosGraphics IMPORT gameWidth, gameHeight, castle, BW, BH;
 FROM ChaosSounds IMPORT SoundList, soundList, Sound, Effect, SoundEffect;
 FROM ChaosActions IMPORT GetCenter, CreateObj, SetObjVXY, SetObjAXY,
  PopMessage, moneyPos, Die, SetObjRect, SetObjXY, SetObjLoc;
 FROM ChaosAlien IMPORT aFlame, aPic;
 FROM ChaosBonus IMPORT TimedBonus, tbSGSpeed;
 FROM ChaosMissile IMPORT mAlien1, mAlien2, mAcc2;


 VAR
  masterSeq: CARD8;


 PROCEDURE ShowStat(str: StrPtr; val: CARD16);
  VAR
   buffer: ARRAY[0..39] OF CHAR;
   pos, tmp: CARD16;
 BEGIN
  IF val = 0 THEN SoundEffect(mainPlayer, koEffect) END;
  CopyStr(str, ADR(buffer), 39);
  pos:= 0;
  WHILE buffer[pos] <> "#" DO INC(pos) END;
  tmp:= val DIV 10;
  IF tmp = 0 THEN buffer[pos]:= " " ELSE buffer[pos]:= CHR(48 + tmp) END;
  buffer[pos + 1]:= CHR(48 + val MOD 10);
  PopMessage(ADR(buffer), moneyPos, 3)
 END ShowStat;

 PROCEDURE ParabolicDist(x, y, vx, vy, ax, ay, px, py: INT32): INT16;
  VAR
   t1, t2, d1, d2, dx, dy, delta, mx, my: INT32;
 BEGIN
   (* Adjust unities *)
  ax:= ax / 4; ay:= ay / 4;
  vx:= vx * 4; vy:= vy * 4;
  x:= x * 8 * Frac; y:= y * 8 * Frac;
  px:= px * 8 * Frac; py:= py * 8 * Frac;
  DEC(x, px); DEC(y, py);
   (* x dist *)
  delta:= vx * vx - 4 * x * ax;
  IF delta >= 0 THEN
   delta:= SQRT(delta);
   IF ax <> 0 THEN
    t1:= (-vx + delta) / (2 * ax);
    t2:= (-vx - delta) / (2 * ax)
   ELSIF vx <> 0 THEN
    t1:= -x / vx; t2:= t1
   ELSE
    t1:= 0; t2:= 0
   END;
   IF (t1 < 0) OR (t1 >= 12000) THEN t1:= 0 END;
   IF (t2 < 0) OR (t2 >= 12000) THEN t2:= 0 END;
   IF (t1 >= 6000) OR (t2 >= 6000) THEN RETURN 0 END;
   d1:= ABS(((ay * t1) + vy) * t1 + y);
   d2:= ABS(((ay * t2) + vy) * t2 + y);
   IF d1 < d2 THEN dx:= d1 ELSE dx:= d2 END
  ELSE
   IF ax <> 0 THEN t1:= -vx / (2 * ax) ELSE t1:= 0 END;
   IF t1 < 0 THEN t1:= 0 END;
   mx:= (((ax * t1) + vx) * t1 + x) DIV 65536;
   my:= (((ay * t1) + vy) * t1 + y) DIV 65536;
   dx:= SQRT(mx * mx + my * my) * 65536
  END;
   (* y dist *)
  delta:= vy * vy - 4 * y * ay;
  IF delta >= 0 THEN
   delta:= SQRT(delta);
   IF ay <> 0 THEN
    t1:= (-vy + delta) / (2 * ay);
    t2:= (-vy - delta) / (2 * ay)
   ELSIF vy <> 0 THEN
    t1:= -y / vy; t2:= t1
   ELSE
    t1:= 0; t2:= 0
   END;
   IF (t1 < 0) OR (t1 >= 12000) THEN t1:= 0 END;
   IF (t2 < 0) OR (t2 >= 12000) THEN t2:= 0 END;
   IF (t1 >= 6000) OR (t2 >= 6000) THEN RETURN 0 END;
   d1:= ABS(((ax * t1) + vx) * t1 + x);
   d2:= ABS(((ax * t2) + vx) * t2 + x);
   IF d1 < d2 THEN dy:= d1 ELSE dy:= d2 END
  ELSE
   IF ay <> 0 THEN t1:= -vy / (2 * ay) ELSE t1:= 0 END;
   IF t1 < 0 THEN t1:= 0 END;
   mx:= (((ax * t1) + vx) * t1 + x) DIV 65536;
   my:= (((ay * t1) + vy) * t1 + y) DIV 65536;
   dy:= SQRT(mx * mx + my * my) * 65536
  END;
  IF dx < dy THEN RETURN dx DIV (Frac * 8) ELSE RETURN dy DIV (Frac * 8) END
 END ParabolicDist;

 PROCEDURE FireFlame(src: ObjPtr; ox, oy: INT16; snd: BOOLEAN);
  VAR
   flame: ObjPtr;
   px, py: INT16;
 BEGIN
  IF snd THEN SoundEffect(src, flameEffect) END;
  GetCenter(src, px, py);
  INC(px, ox); INC(py, oy);
  flame:= CreateObj(ALIEN1, aFlame, px, py, 0, 30);
  SetObjVXY(flame, src^.vx, src^.vy)
 END FireFlame;

 PROCEDURE FireMissile(src: ObjPtr; fx, fy, fvx, fvy, ox, oy: INT16; snd: BOOLEAN);
  VAR
   missile: ObjPtr;
 BEGIN
  IF snd THEN SoundEffect(src, missileEffect) END;
  missile:= CreateObj(MISSILE, mAlien2, fx + ox, fy + oy, mAcc2, 12);
  SetObjVXY(missile, fvx, fvy)
 END FireMissile;

 PROCEDURE FireMissileV(src, dst: ObjPtr; ox, oy: INT16; snd: BOOLEAN);
  VAR
   missile: ObjPtr;
   fx, fy, px, py, fvx, fvy: INT16;
   rx, ry, rl: INT32;
 BEGIN
  IF snd THEN SoundEffect(src, missileEffect) END;
  GetCenter(src, fx, fy);
  INC(fx, ox); INC(fy, oy);
  GetCenter(dst, px, py);
  rx:= px - fx; ry:= py - fy;
  rl:= SQRT(rx * rx + ry * ry);
  IF rl <> 0 THEN
   px:= rx * 1536 DIV rl;
   py:= ry * 1536 DIV rl
  ELSE
   px:= 0; py:= 0
  END;
  fvx:= px + dst^.vx;
  fvy:= py + dst^.vy;
  IF src^.subKind = 4 THEN
   missile:= CreateObj(MISSILE, mAlien1, fx, fy, RND() MOD 5, 12)
  ELSE
   missile:= CreateObj(MISSILE, mAlien2, fx, fy, mAcc2, 12)
  END;
  SetObjVXY(missile, fvx, fvy)
 END FireMissileV;

 PROCEDURE FireMissileA(src: ObjPtr; ox, oy: INT16);
  VAR
   missile: ObjPtr;
   rx, ry, rl: INT32;
   px, py, dx, dy, nvx, nvy: INT16;
   nax, nay: INT8;
 BEGIN
  SoundEffect(src, missileEffect);
  GetCenter(src, px, py);
  INC(px, ox); INC(py, oy);
  GetCenter(mainPlayer, dx, dy);
  rx:= dx - px; ry:= dy - py;
  rl:= SQRT(rx * rx + ry * ry);
  IF rl = 0 THEN rl:= 1 END;
  nvx:= mainPlayer^.dvx; nvy:= mainPlayer^.dvy;
  nax:= rx * 64 DIV rl;
  nay:= ry * 64 DIV rl;
  missile:= CreateObj(MISSILE, mAlien2, px, py, mAcc2, 12);
  SetObjVXY(missile, nvx, nvy);
  SetObjAXY(missile, nax, nay)
 END FireMissileA;

 PROCEDURE FireMissileS(fx, fy, fvx, fvy: INT32; src, dest: ObjPtr; ox, oy: INT16; speed, snd: BOOLEAN);
  VAR
   missile: ObjPtr;
   px, py, pvx, pvy: INT32;
   tx, ty, nax, nay: INT16;
   val: CARD16;
 BEGIN
  IF snd THEN SoundEffect(src, missileEffect) END;
  GetCenter(dest, tx, ty);
  px:= tx + ox; py:= ty + oy;
  IF speed THEN
   WITH dest^ DO pvx:= vx; pvy:= vy END
  ELSE
   pvx:= 0; pvy:= 0;
   px:= (px + px + px + fx) DIV 4;
   py:= (py + py + py + fy) DIV 4
  END;
  nax:= ((px - fx) * Frac + (pvx - fvx) * 420) / 22500;
  nay:= ((py - fy) * Frac + (pvy - fvy) * 420) / 22500;
  IF nax < -127 THEN nax:= -127 ELSIF nax > 127 THEN nax:= 127 END;
  IF nay < -127 THEN nay:= -127 ELSIF nay > 127 THEN nay:= 127 END;
  IF (src^.subKind >= 4) AND (src^.moveSeq <= 4) THEN
   IF ((src^.moveSeq = 2) OR (src^.moveSeq = 0)) AND
      ((src^.stat = 3) OR (src^.stat = 6)) THEN
    val:= 4 - src^.shapeSeq MOD 3 - src^.moveSeq
   ELSE
    val:= masterSeq MOD (5 - src^.moveSeq);
    masterSeq:= (masterSeq + 1) MOD 3
   END;
   missile:= CreateObj(MISSILE, mAlien1, fx, fy, val, 12)
  ELSE
   missile:= CreateObj(MISSILE, mAlien2, fx, fy, mAcc2, 12)
  END;
  SetObjVXY(missile, fvx, fvy);
  SetObjAXY(missile, nax, nay)
 END FireMissileS;

 PROCEDURE BoumS(src, dest: ObjPtr; ox, oy, ba, bm, sa, sm, c, as, ms: INT16; follow, speed, snd: BOOLEAN);
  VAR
   sh, fvx, fvy: INT32;
   px, py, tx, ty, angle, mod: INT16;
 BEGIN
  IF snd THEN SoundEffect(src, bombEffect) END;
  GetCenter(src, px, py);
  angle:= ba; mod:= bm;
  tx:= 0; ty:= 0;
  WHILE c > 0 DO
   sh:= SIN(mod) * ms DIV 1024 + 32;
   fvx:= COS(angle) * as DIV 4; fvx:= fvx * sh DIV 32;
   fvy:= SIN(angle) * as DIV 4; fvy:= fvy * sh DIV 32;
   IF follow THEN
    IF turn THEN
     tx:= COS(angle + 90) DIV 32;
     ty:= SIN(angle + 90) DIV 32
    END;
    FireMissileS(px, py, fvx, fvy, src, dest, ox + tx, oy + ty, speed, FALSE)
   ELSE
    FireMissile(src, px, py, fvx, fvy, ox, oy, FALSE)
   END;
   INC(angle, sa); INC(mod, sm); DEC(c)
  END;
  turn:= FALSE
 END BoumS;

 PROCEDURE CloseEnough(bx, by, px, py: INT16): BOOLEAN;
 BEGIN
  RETURN (ABS(px - bx) + ABS(py - by) < 96)
 END CloseEnough;

 PROCEDURE GoTo(boss: ObjPtr; tx, ty: INT16);
  VAR
   px, py: INT16;
 BEGIN
  GetCenter(boss, px, py);
  WITH boss^ DO
   ax:= 0; ay:= 0;
   dvx:= (tx - px) * 16;
   dvy:= (ty - py) * 16
  END
 END GoTo;

 PROCEDURE GoCenter(boss: ObjPtr);
 BEGIN
  GoTo(boss, gameWidth DIV 2, gameHeight DIV 2)
 END GoCenter;

 PROCEDURE ReturnWeapon(sx, sy: INT16);
  VAR
   obj, tail: ObjPtr;
   wx, wy: INT16;
   dx, dy, dl, avx, avy: INT32;
 BEGIN
  obj:= First(animList[WEAPON]); tail:= Tail(animList[WEAPON]);
  WHILE obj <> tail DO
   GetCenter(obj, wx, wy);
   dx:= wx - sx; dy:= wy - sy;
   IF (ABS(dx) < 50) AND (ABS(dy) < 50) THEN
    dl:= SQRT(dx * dx + dy * dy);
    IF (dl < 50) AND (dl > 0) THEN
     avx:= step; avx:= avx * dx * 64 DIV dl;
     avy:= step; avy:= avy * dy * 64 DIV dl;
     INC(obj^.vx, avx); INC(obj^.vy, avy)
    END
   END;
   obj:= Next(obj^.animNode)
  END
 END ReturnWeapon;

 PROCEDURE KillObjs(oKind: Anims; sKind: CARD8);
  VAR
   alien, obj, tail: ObjPtr;
 BEGIN
  obj:= First(animList[oKind]);
  tail:= Tail(animList[oKind]);
  WHILE obj <> tail DO
   alien:= obj;
   obj:= Next(obj^.animNode);
   IF (alien^.subKind = sKind) THEN Die(alien) END
  END
 END KillObjs;

 PROCEDURE Chain(boss: ObjPtr);
  VAR
   alien, tail, cur: ObjPtr;
   px, py: INT16;
   sKind: CARD8;
   cnt: CARD8;

  PROCEDURE SetTo(val: CARD8);
  BEGIN
   IF sKind < val THEN sKind:= val END
  END SetTo;

  CONST
   bBrotherAlien = 0;
   bSisterAlien = 1;
   bMotherAlien = 2;
   bFatherAlien = 3;
   bMasterAlien1 = 4;
   bMasterAlien2 = 5;
   bFatherHeart = 6;
   bMasterEye = 7;
   bMasterMouth = 8;
   bMasterPart0 = 9;
   bMasterPart1 = 10;
   bMasterPart2 = 11;
 BEGIN
  INC(addpt, 500);
  IF level[Family] < 10 THEN RETURN END;
  cnt:= 0; sKind:= 0;
  cur:= First(animList[ALIEN3]);
  tail:= Tail(animList[ALIEN3]);
  WHILE cur <> tail DO
   CASE cur^.subKind OF
     bBrotherAlien: SetTo(1)
    |bMotherAlien: SetTo(2)
    |bSisterAlien: SetTo(3)
    |bMasterAlien1: SetTo(4)
    |bFatherAlien: SetTo(5)
    |bMasterAlien2: SetTo(6)
    ELSE
   END;
   IF cur^.subKind IN SET16{bBrotherAlien, bSisterAlien, bMotherAlien,
                            bFatherAlien, bMasterAlien1, bMasterAlien2} THEN
    INC(cnt)
   END;
   cur:= Next(cur^.animNode)
  END;
  IF cnt < 2 THEN sKind:= 6 END;
  IF sKind < 6 THEN
   GetCenter(boss, px, py);
   CASE sKind OF
     2: sKind:= bSisterAlien
    |3: sKind:= bMasterAlien1;
     alien:= CreateObj(ALIEN3, bMasterEye, px, py, 0, 0);
     alien:= CreateObj(ALIEN3, bMasterEye, px, py, 1, 1);
     alien:= CreateObj(ALIEN3, bMasterMouth, px, py, 0, 0)
    |4: sKind:= bFatherAlien;
     alien:= CreateObj(ALIEN3, bFatherHeart, px, py, 0, 0)
    |5: sKind:= bMasterAlien2;
     alien:= CreateObj(ALIEN3, bMasterPart0, px, py, 0, 0);
     alien:= CreateObj(ALIEN3, bMasterPart0, px, py, 1, 1);
     alien:= CreateObj(ALIEN3, bMasterPart1, px, py, 2, 2);
     alien:= CreateObj(ALIEN3, bMasterPart1, px, py, 3, 3);
     alien:= CreateObj(ALIEN3, bMasterPart2, px, py, 4, 4)
   END;
   alien:= CreateObj(ALIEN3, sKind, px, py, 10, 10)
  ELSIF cnt <= 1 THEN
   px:= 11 * BW + BW DIV 2; py:= 18 * BH + BH DIV 2;
   alien:= CreateObj(ALIEN1, aPic, px, py, 0, 0);
   px:= 17 * BW + BW DIV 2; py:= 40 * BH + BH DIV 2;
   alien:= CreateObj(BONUS, TimedBonus, px, py, tbSGSpeed, tbSGSpeed);
   castle^[40, 17]:= 22
  END
 END Chain;

 PROCEDURE BoumX(src, dest: ObjPtr; cnt: INT16; star: BOOLEAN);
  VAR
   c, sx, sy, dx, dy, rx, ry, fvx, fvy, angle: INTEGER;
 BEGIN
  SoundEffect(src, bombEffect);
  GetCenter(src, sx, sy);
  GetCenter(dest, dx, dy);
  FOR c:= 0 TO cnt - 1 DO
   angle:= c * 360 DIV cnt;
   rx:= COS(angle) DIV 32;
   ry:= SIN(angle) DIV 32;
   IF star THEN
    fvx:= COS(-angle) * 3;
    fvy:= SIN(-angle) * 3
   ELSE
    fvx:= RND() MOD 2048 + 1024; IF RND() MOD 2 = 0 THEN fvx:= -fvx END;
    fvy:= RND() MOD 2048 + 1024; IF RND() MOD 2 = 0 THEN fvy:= -fvy END
   END;
   FireMissileS(sx, sy, fvx, fvy, src, dest, rx, ry, FALSE, FALSE)
  END
 END BoumX;

 PROCEDURE BoumE(src: ObjPtr; cnt, mx, my: INT16);
  VAR
   fx, fy, fvx, fvy, angle, c: INT16;
 BEGIN
  GetCenter(src, fx, fy);
  SoundEffect(src, bombEffect);
  FOR c:= 1 TO cnt DO
   angle:= c * 360 DIV cnt;
   fvx:= COS(angle) * mx DIV 4;
   fvy:= SIN(angle) * my DIV 4;
   FireMissile(src, fx, fy, fvx, fvy, 0, 0, FALSE)
  END
 END BoumE;

 PROCEDURE ResetPart(part: ObjPtr);
 BEGIN
  WITH part^ DO
   hitSubLife:= 0;
   fireSubLife:= 0;
   moveSeq:= 1;
   stat:= life;
   SetObjRect(part, 0, 0, 11, 11)
  END
 END ResetPart;

 PROCEDURE MoveHeart(heart: ObjPtr);
  VAR
   fx, fy: INT16;
 BEGIN
  WITH theFather^ DO
   IF moveSeq = 0 THEN
    heart^.moveSeq:= 0; Die(heart); RETURN
   ELSIF hitSubLife + fireSubLife = 0 THEN
    SetObjLoc(heart, 67, 83, 0, 0)
   ELSE
    SetObjLoc(heart, 67, 83, 6, 6)
   END;
   GetCenter(theFather, fx, fy);
   SetObjXY(heart, fx - 3, fy - 3)
  END
 END MoveHeart;

 PROCEDURE MoveEye(eye: ObjPtr);
  VAR
   px, py, mx, my, dx, dy, r: INT16;
 BEGIN
  GetCenter(theMaster, mx, my);
  IF ODD(eye^.stat) THEN dx:= 1; INC(mx, 17) ELSE dx:= -1; DEC(mx, 18) END;
  DEC(my, 10);
  WITH theMaster^ DO
   IF stat = 4 THEN
    r:= (shapeSeq DIV 16) MOD 12;
    IF dx < 0 THEN r:= 11 - r END;
    IF shapeSeq < 60 THEN
     DEC(my, SIN(shapeSeq * 3) DIV 192);
     INC(mx, SIN(shapeSeq * 3) DIV 256 * dx)
    ELSIF shapeSeq < 240 THEN
     INC(my, SIN(shapeSeq - 60) DIV 64);
     DEC(mx, SIN(shapeSeq - 60) DIV 48 * dx)
    ELSIF shapeSeq < 600 THEN
     DEC(my, SIN((shapeSeq - 240) DIV 2) DIV 32);
     INC(mx, SIN((shapeSeq - 240) DIV 2) DIV 32 * dx)
    END
   END;
   SetObjXY(eye, mx - 5, my - 5);
   IF stat < 4 THEN
    GetCenter(mainPlayer, px, py);
    dx:= ABS(px - mx); dy:= ABS(py - my);
    IF (dx * 3 >= dy) AND (dx <= dy * 3) THEN
     IF my > py THEN
      IF mx > px THEN
       IF dx < dy THEN r:= 0 ELSE r:= 11 END
      ELSE
       IF dx < dy THEN r:= 2 ELSE r:= 3 END
      END
     ELSE
      IF mx > px THEN
       IF dx < dy THEN r:= 8 ELSE r:= 9 END
      ELSE
       IF dx < dy THEN r:= 6 ELSE r:= 5 END
      END
     END
    ELSE
     IF dy > dx THEN
      IF my < py THEN r:= 7 ELSE r:= 1 END
     ELSE
      IF mx < px THEN r:= 4 ELSE r:= 10 END
     END
    END
   ELSIF stat > 4 THEN
    eye^.moveSeq:= 0; Die(eye); RETURN
   END;
   SetObjLoc(eye, (r MOD 10) * 11 + 76, (r DIV 10) * 11 + 32, 11, 11)
  END
 END MoveEye;

 PROCEDURE MoveMouth(mouth: ObjPtr);
  VAR
   off, mx, my, dx: INT16;
 BEGIN
  GetCenter(theMaster, mx, my);
  INC(my, 4); off:= 0;
  WITH theMaster^ DO
   IF stat = 3 THEN
    SetObjLoc(mouth, 32, 200, 0, 0)
   ELSIF stat >= 6 THEN
    mouth^.moveSeq:= 0; Die(mouth); RETURN
   ELSIF stat = 4 THEN
    IF ODD(moveSeq) THEN dx:= 1 ELSE dx:= -1 END;
    IF shapeSeq < 60 THEN
     DEC(mx, SIN(shapeSeq * 3) DIV 256 * dx)
    ELSIF shapeSeq < 240 THEN
     INC(my, SIN((shapeSeq - 60) * 2) DIV 128);
     INC(mx, SIN(shapeSeq - 60) DIV 128 * dx)
    ELSIF shapeSeq < 600 THEN
     INC(my, SIN((shapeSeq - 240) DIV 2) DIV 16)
    END;
    SetObjLoc(mouth, 32, 200, 20, 12)
   ELSE
    off:= (shapeSeq DIV 30) MOD 10;
    IF off >= 5 THEN off:= 10 - off END;
    SetObjLoc(mouth, 32 + off, 200 + off, 20 - off * 2, 12 - off * 2)
   END;
   SetObjXY(mouth, mx - 10 + off, my - 6 + off)
  END
 END MoveMouth;

 PROCEDURE MovePart(part: ObjPtr);
  VAR
   x0, y0, x1, y1, x2, y2, ix, iy: INT16;
   seq, st: CARD16;
 BEGIN
  IF theIllusion = NIL THEN part^.moveSeq:= 0; Die(part); RETURN END;
  WITH theIllusion^ DO
   IF stat = 0 THEN
    seq:= (shapeSeq DIV 64) MOD 8
   ELSIF stat = 33 THEN
    seq:= 7 - (shapeSeq DIV 64) MOD 8
   ELSE
    seq:= 0
   END
  END;
  seq:= (seq + 2) MOD 8;
  st:= part^.stat;
  CASE st OF
    0, 1: SetObjLoc(part, 56, 240, 16, 16)
   |2, 3: SetObjLoc(part, 72, 240, 16, 16)
   ELSE   SetObjLoc(part, 88 + seq * 20, 236, 20, 20)
  END;
  SetObjRect(part, 0, 0, 0, 0);
  CASE seq OF
    0: x0:= 0; y0:= 3; x1:= 0; y1:= -4; x2:= 0; y2:= -2
   |1: x0:= -2; y0:= 3; x1:= 1; y1:= -3; x2:= -2; y2:= -2
   |2: x0:= -3; y0:= 2; x1:= 2; y1:= -3; x2:= -3; y2:= -3
   |3: x0:= -3; y0:= 1; x1:= 3; y1:= -2; x2:= -2; y2:= -4
   |4: x0:= -4; y0:= 0; x1:= 3; y1:= -0; x2:= -4; y2:= 0
   |5: x0:= -3; y0:= -2; x1:= 3; y1:= 1; x2:= -3; y2:= -2
   |6: x0:= -2; y0:= -3; x1:= 3; y1:= 2; x2:= -2; y2:= -3
   |7: x0:= -1; y0:= -3; x1:= 2; y1:= 3; x2:= -1; y2:= -2
  END;
  GetCenter(theIllusion, ix, iy);
  DEC(ix, 8); DEC(iy, 8);
  IF st >= 4 THEN
   SetObjXY(part, ix + x2, iy + y2)
  ELSIF ODD(st) THEN
   SetObjXY(part, ix + x1, iy + y1)
  ELSE
   SetObjXY(part, ix + x0, iy + y0)
  END
 END MovePart;

 PROCEDURE DiePart(part: ObjPtr);
 BEGIN
  IF part^.moveSeq <> 0 THEN part^.life:= 1 END
 END DiePart;

BEGIN

 turn:= FALSE;

END ChaosFire.
