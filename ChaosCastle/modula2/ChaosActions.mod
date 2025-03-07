IMPLEMENTATION MODULE ChaosActions;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM Checks IMPORT CheckMemBool, Check, AddTermProc;
 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32, StrPtr,
  First, Next, Tail, CopyStr;
 FROM Trigo IMPORT SIN, COS, SQRT, RND, SGN;
 FROM Languages IMPORT ADL;
 FROM Clock IMPORT TimePtr, AllocTime, StartTime, WaitTime,
  GetTime, FreeTime, noTime;
 FROM Input IMPORT SetBusyStat, statReady, statWaiting, GetMouse, AddEvents,
  RemEvents, EventTypeSet, eMENU, statBusy, eREFRESH;
 FROM Graphics IMPORT SetCopyMode, cmCopy, cmTrans, cmXor, SetArea, SetBuffer,
  SetPalette, ScaleRect, SwitchArea, UpdateArea, GetBuffer, SetPen, SetPat,
  SetTextMode, SetTextSize, SetTextPos, TextWidth, DrawText, FillRect;
 FROM ChaosBase IMPORT Anims, AnimSet, ObjAttr, ObjAttrPtr, Obj, ObjPtr,
  attrList, animList, nbAnim, NewObj, DisposeObj, FirstObj,
  NextObj, TailObj, ConvertObj, objList, Frac, Period, addpt, BasicTypes,
  basics, Warm, Hot, MaxHot, AieProc, sleeper, Stones, StoneSet, slowStyle,
  gravityStyle, returnStyle, fastStyle, FlameMult, screenInverted,
  Weapon, GameStat, gameStat, nextGunFireTime, GunFireSpeed, AnimAlienSet,
  DeadObj, Message, LeaveObj, RestartObj, leftObjList, water, snow, shoot,
  lasttime, step, mainPlayer, stages, nbObj, ObjFlags, ObjFlagSet;
 FROM ChaosGraphics IMPORT Explosions, explosions, mulS,
  color, gameWidth, gameHeight, shapeArea, maskArea, PW, PH, BW, BH,
  Castle, castle, NbBackground, UpdateAnim, SW, SH,
  OW, OH, backpx, backpy, UpdatePalette, mainArea, W, H, castleWidth,
  castleHeight, RenderObjects, Palette, palette, buffArea, SetOrigin,
  MoveBackground, dualPalette, ResetCycle, dualpf, CycleRGB, DualCycleRGB,
  AnimPalette, SetTrans;


 PROCEDURE PopMessage(msg: StrPtr; pos, pri: CARD8);
  VAR
   obj: ObjPtr;
   yInP: INT16;
   xPos, yPos, ySize: INT16;
 BEGIN
  IF pri >= priorities[pos] THEN
  (* kill previous obj *)
   IF msgObj[pos] <> NIL THEN
    DisposeObj(msgObj[pos]);
    msgObj[pos]:= NIL
   END;
   priorities[pos]:= pri;
   ySize:= H(9);
   SetArea(shapeArea); SetTextSize(ySize);
   xPos:= (W(PW) - TextWidth(msg)) DIV 2;
   IF xPos < 0 THEN xPos:= xPos * 2 END;
   yPos:= pos * 10;
   yInP:= (yPos + ((PH - 40) DIV 2));
   yPos:= H(yPos + 256);
  (* Erase previous message *)
   SetPen(0); SetCopyMode(cmCopy);
   FillRect(0, yPos, W(PW), yPos + ySize + mulS);
   SetCopyMode(cmTrans);
   IF color THEN SetPen(4) ELSE SetPen(1) END;
   SetTextPos(xPos, yPos + mulS); DrawText(msg);
   IF color THEN SetPen(3) END;
   SetTextPos(xPos + mulS, yPos); DrawText(msg);
   IF color THEN SetPen(7) ELSE SetPen(0) END;
   SetTextPos(xPos, yPos); DrawText(msg);
   SetArea(maskArea); SetPen(0); SetCopyMode(cmCopy);
   FillRect(0, yPos, W(PW), yPos + ySize + mulS);
  (* Draw current message *)
   SetPen(1); SetCopyMode(cmTrans); SetTextSize(ySize);
   SetTextPos(xPos, yPos + mulS); DrawText(msg);
   SetTextPos(xPos + mulS, yPos); DrawText(msg);
   SetTextPos(xPos, yPos); DrawText(msg);
  (* Set corresponding obj *)
   SetArea(mainArea);
   obj:= CreateObj(DEAD, Message, backpx + PW DIV 2, backpy + yInP + 5, yInP, 1);
   IF obj <> ADR(nlObj) THEN
    msgObj[pos]:= obj;
    WITH obj^ DO
     moveSeq:= pri * 256; shapeSeq:= pos
    END;
    SetObjLoc(obj, 0, 256 + 10 * pos, PW, 0)
   END
  END
 END PopMessage;

 VAR
  out: BOOLEAN;

 PROCEDURE ZoomMessage(msg: StrPtr; p1, p2, p3: CARD16);
  CONST
   MX = SW DIV 2;
   MY = SH DIV 2;
  VAR
   delay: CARD32;
   width, sx, sy: INT16;
   c, r, g, b, f: CARD16;
 BEGIN
  IF NOT(color) THEN p1:= 0; p2:= 1; p3:= 1 END;
  SetArea(buffArea); SetCopyMode(cmTrans);
  SetPen(0); FillRect(0, 0, W(SW), H(SH));
  SetTextSize(27);
  width:= TextWidth(msg) + mulS;
  SetTextPos(mulS, mulS); SetPen(p3); DrawText(msg);
  SetTextPos(0, 0); SetPen(p2); DrawText(msg);
  SetTextPos(mulS, 0); SetPen(p1); DrawText(msg);
  SetCopyMode(cmCopy);
  SetArea(mainArea); SetBuffer(TRUE, TRUE);
  SetCopyMode(cmCopy); SetPen(0);
  SetOrigin(0, 0);
  FillRect(0, 0, W(SW), H(SH));
  StartTime(time);
  LOOP
   delay:= GetTime(time);
   IF delay >= 512 THEN delay:= 512 END;
   f:= 256 - delay DIV 2;
   sy:= delay * delay DIV 2048 + 1;
   IF sy >= 120 THEN EXIT END;
   IF sy > 80 THEN
    sx:= sy DIV 2 * width DIV 15
   ELSE
    sx:= sy * width DIV 30
   END;
   ScaleRect(buffArea, 0, 0, width, 30,
             W(MX - sx), H(MY - sy), W(MX + sx), H(MY + sy));
   IF color THEN
    FOR c:= 0 TO 7 DO
     WITH palette[c] DO r:= red; g:= green; b:= blue END;
     SetPalette(c, r * f DIV 256, g * f DIV 256, b * f DIV 256)
    END
   END;
   SwitchArea
  END;
  out:= TRUE; FadeOut
 END ZoomMessage;

 PROCEDURE AddPt(count: CARD16);
 BEGIN
  INC(addpt, count)
 END AddPt;

 PROCEDURE NextStage;
  VAR
   buffer: ARRAY[0..59] OF CHAR;
   msg: StrPtr;
   c: CARD16;
 BEGIN
  DEC(stages);
  IF stages = 0 THEN
   msg:= ADL("PMM (Post Mortem Map) enabled")
  ELSE
   msg:= ADL("! # stages left before PMM");
   CopyStr(msg, ADR(buffer), SIZE(buffer));
   c:= 0;
   WHILE (buffer[c] <> "#") AND (buffer[c] <> 0C) DO INC(c) END;
   IF buffer[c] = "#" THEN buffer[c]:= CHR(48 + stages) END;
   msg:= ADR(buffer)
  END;
  PopMessage(msg, statPos, 6)
 END NextStage;

 PROCEDURE CreateObj(kind: Anims; subKind: CARD8;
                     nx, ny: INT16; nStat, nLife: CARD16): ObjPtr;
  VAR
   obj: ObjPtr;
 BEGIN
  obj:= NewObj(kind, subKind);
  IF obj <> NIL THEN
   WITH obj^ DO
    x:= nx; x:= x * Frac; y:= ny; y:= y * Frac;
    midx:= x; midy:= y;
    vx:= 0; vy:= 0; dvx:= 0; dvy:= 0;
    ax:= 0; ay:= 0; cx:= 0; cy:= 0;
    temperature:= 0;
    width:= 0; height:= 0;
    flags:= ObjFlagSet{};
    IF gameStat = Playing THEN INCL(flags, nested) END;
    IF attr^.basicType <> NotBase THEN
     INC(basics[attr^.basicType].total)
    END;
    life:= nLife; stat:= nStat;
    IF attr^.Reset <> NIL THEN attr^.Reset(obj) END
   END
  ELSE
   obj:= ADR(nlObj)
  END;
  RETURN obj
 END CreateObj;

 PROCEDURE SetObjXY(obj: ObjPtr; nx, ny: INT32);
 BEGIN
  WITH obj^ DO
   x:= nx * Frac; y:= ny * Frac;
   midx:= x; midy:= y
  END
 END SetObjXY;

 PROCEDURE SetObjVXY(obj: ObjPtr; nvx, nvy: INT16);
 BEGIN
  WITH obj^ DO
   vx:= nvx; dvx:= vx;
   vy:= nvy; dvy:= vy
  END
 END SetObjVXY;

 PROCEDURE SetObjAXY(obj: ObjPtr; nax, nay: INT8);
 BEGIN
  WITH obj^ DO
   ax:= nax; ay:= nay;
   dvx:= 0; dvy:= 0
  END
 END SetObjAXY;

 PROCEDURE SetObjPos(obj: ObjPtr; nPosX, nPosY: INT16);
 BEGIN
  WITH obj^ DO posX:= nPosX * mulS; posY:= nPosY * mulS END
 END SetObjPos;

 PROCEDURE SetObjLoc(obj: ObjPtr; nPosX, nPosY, nWidth, nHeight: INT16);
  VAR
   z: INT32;
   temp: INT16;
 BEGIN
  WITH obj^ DO
   posX:= nPosX * mulS; posY:= nPosY * mulS;
   temp:= nWidth * mulS;
   IF width <> temp THEN
    z:= cx; width:= temp;
    cx:= nWidth DIV 2; DEC(z, cx);
    z:= z * Frac;
    INC(x, z); INC(midx, z)
   END;
   temp:= nHeight * mulS;
   IF height <> temp THEN
    z:= cy; height:= temp;
    cy:= nHeight DIV 2; DEC(z, cy);
    z:= z * Frac;
    INC(y, z); INC(midy, z)
   END
  END
 END SetObjLoc;

 PROCEDURE SetObjRect(obj: ObjPtr; nLeft, nTop, nRight, nBottom: INT32);
 BEGIN
  WITH obj^ DO
   left:= nLeft * Frac;
   top:= nTop * Frac;
   right:= nRight * Frac;
   bottom:= nBottom * Frac
  END
 END SetObjRect;

 PROCEDURE GetCenter(obj: ObjPtr; VAR px, py: INT16);
 BEGIN
  WITH obj^ DO
   px:= x DIV Frac; INC(px, cx);
   py:= y DIV Frac; INC(py, cy)
  END
 END GetCenter;

 PROCEDURE Boum(obj: ObjPtr; subKinds: StoneSet; style, cnt, skcnt: CARD8);
  CONST
   SV = 4; (* Frac DIV 1024 *)
   MAXSTONES = MAX(CARD8) - 64;
  VAR
   flames, stones, c: CARD8;
   subK: Stones;
   angle, step: CARD16;
   newObj: ObjPtr;
   px, py, nvx, nvy: INT16;
   nax, nay: INT8;
 BEGIN
 (* explosions *)
  flames:= cnt DIV FlameMult;
  stones:= cnt MOD FlameMult;
  IF explosions = Low THEN
   stones:= stones DIV 8
  ELSIF explosions = High THEN
   stones:= stones * 2
  END;
  GetCenter(obj, px, py);
 (** flames *)
  IF flames > 0 THEN
   DEC(flames);
   nvx:= obj^.vx; nvy:= obj^.vy;
   newObj:= CreateObj(STONE, 1, px, py, 0, 1);
   WITH newObj^ DO
    vx:= nvx; vy:= nvy;
    shapeSeq:= 9; moveSeq:= 18
   END
  END;
  IF flames > 0 THEN
   angle:= RND() MOD 360;
   step:= 360 DIV flames;
   REPEAT
    DEC(flames);
    nvx:= COS(angle); nvy:= SIN(angle);
    INC(nvx, obj^.vx); INC(nvy, obj^.vy);
    newObj:= CreateObj(STONE, 1, px, py, 0, 1);
    WITH newObj^ DO
     vx:= nvx; vy:= nvy;
     shapeSeq:= 6; moveSeq:= 12
    END;
    INC(angle, step)
   UNTIL flames = 0
  END;
 (** stones *)
  angle:= RND() MOD 360;
  IF stones > 0 THEN step:= 360 DIV stones END;
  WHILE stones > 0 DO
   DEC(stones);
   c:= RND() MOD skcnt; subK:= MIN(Stones);
   LOOP
    WHILE NOT(subK IN subKinds) DO INC(subK) END;
    IF c = 0 THEN EXIT END;
    INC(subK); DEC(c)
   END;
   CASE style OF
   slowStyle:
    nvx:= RND() MOD 512; DEC(nvx, 256); INC(nvx, obj^.vx DIV 2);
    nvy:= RND() MOD 512; DEC(nvy, 256); INC(nvy, obj^.vy DIV 2);
    nax:= 0; nay:= 0
   |gravityStyle:
    nvx:= RND() MOD 1024; DEC(nvx, 512); INC(nvx, obj^.vx DIV 2);
    nvy:= RND() MOD 1024; DEC(nvy, 896); INC(nvy, obj^.vy DIV 2);
    nax:= 0; nay:= 16
   |returnStyle:
    nvx:= COS(angle) * SV; nvy:= SIN(angle) * SV;
    nax:= - nvx DIV (Period DIV 5); nay:= - nvy DIV (Period DIV 5);
    INC(angle, step)
   |fastStyle:
    nvx:= RND() MOD 2048; DEC(nvx, 1024); INC(nvx, obj^.vx DIV 2);
    nvy:= RND() MOD 2048; DEC(nvy, 1024); INC(nvy, obj^.vy DIV 2);
    nax:= 0; nay:= 0
   END;
   IF nbObj < MAXSTONES THEN
    newObj:= CreateObj(STONE, 0, px, py, ORD(subK), 1);
    WITH newObj^ DO
     vx:= nvx; vy:= nvy;
     ax:= nax; ay:= nay
    END
   END
  END
 END Boum;

 PROCEDURE Die(obj: ObjPtr);
  VAR
   flame: ObjPtr;
   tmp, maxpower, power: CARD16;
   px, py, sin, cos, rnd, angle: INT16;
 BEGIN
  obj^.life:= 0;
  WITH obj^.attr^ DO
   IF Die <> NIL THEN Die(obj) END;
   IF obj^.life <> 0 THEN RETURN END;
   IF basicType <> NotBase THEN INC(basics[basicType].done) END;
   tmp:= heatSpeed;
   Boum(obj, dieStKinds, dieStStyle, dieStone, dieSKCount)
  END;
  IF tmp > 0 THEN
   GetCenter(obj, px, py);
   WITH obj^ DO
    IF temperature > Warm THEN tmp:= temperature DIV 2048 + 1 ELSE tmp:= 0 END;
    maxpower:= tmp DIV 2 + 1;
    WHILE tmp > 0 DO
      (* Random direction *)
     angle:= RND() MOD 360;
     cos:= COS(angle);
     sin:= SIN(angle);
      (* Random speed *)
     rnd:= RND() MOD 16;
     sin:= sin * rnd DIV 8 + vy DIV 2;
     cos:= cos * rnd DIV 8 + vx DIV 2;
      (* Random power *)
     power:= RND() MOD maxpower + 1;
     flame:= CreateObj(WEAPON, ORD(FIRE), px, py, 0, power);
     SetObjVXY(flame, cos, sin);
     DEC(tmp)
    END
   END
  END;
  obj^.width:= 0; obj^.height:= 0;
  ConvertObj(obj, DEAD, DeadObj)
 END Die;

 PROCEDURE DecLife(obj: ObjPtr; VAR hit, fire: CARD16);
  VAR
   subHit, subFire: CARD16;
 BEGIN
  subHit:= hit; subFire:= fire;
  WITH obj^ DO
   IF subHit > hitSubLife THEN subHit:= hitSubLife END;
   IF subFire > fireSubLife THEN subFire:= fireSubLife END;
   IF subHit > life THEN subHit:= life END;
   DEC(life, subHit);
   IF subFire > life THEN subFire:= life END;
   DEC(life, subFire)
  END;
  subHit:= (subHit * 3 + hit + 3) DIV 4; DEC(hit, subHit);
  subFire:= (subFire * 3 + fire + 3) DIV 4; DEC(fire, subFire)
 END DecLife;

 PROCEDURE Aie(victim, src: ObjPtr; VAR hit, fire: CARD16);
  VAR
   dhit, dfire, ttaie: CARD16;
   sw, vw: INT16;
   stones: CARD8;
 BEGIN
  dhit:= hit; dfire:= fire; ttaie:= dhit + dfire;
  WITH victim^ DO
   IF (hitSubLife = 0) AND (fireSubLife = 0) THEN RETURN END;
    (* Collision *)
   sw:= src^.attr^.weight; vw:= attr^.weight;
   IF sw + vw <> 0 THEN
    vx:= (vx DIV 32 * vw + src^.vx DIV 32 * sw) / (sw + vw) * 32;
    vy:= (vy DIV 32 * vw + src^.vy DIV 32 * sw) / (sw + vw) * 32
   END;
   IF (src^.kind = WEAPON) AND
       NOT((src^.subKind = ORD(FIRE)) AND (nested IN src^.flags)) THEN
    INC(shoot.done)
   END;
    (* Lives *)
   IF ttaie = 0 THEN RETURN END;
   IF attr^.Aie <> NIL THEN
    attr^.Aie(victim, src, hit, fire)
   ELSE
    DecLife(victim, hit, fire)
   END;
   DEC(ttaie, hit + fire);
   IF (life = 0) AND (hitSubLife + fireSubLife > 0) THEN
    Die(victim); RETURN
   END;
    (* temperature *)
   dfire:= attr^.heatSpeed * dfire;
   IF dfire < MaxHot DIV 16 THEN
    INC(temperature, dfire * 16)
   ELSE
    temperature:= MaxHot
   END;
   IF temperature >= MaxHot THEN temperature:= MaxHot END;
   dhit:= attr^.refreshSpeed * dhit;
   IF dhit >= MaxHot DIV 16 THEN dhit:= MaxHot ELSE dhit:= dhit * 16 END;
   IF temperature < dhit THEN temperature:= 0 ELSE DEC(temperature, dhit) END;
    (* Stones *)
   WITH attr^ DO
    stones:= aieStone;
    IF ttaie < 16 THEN DEC(stones, (stones MOD FlameMult) * (16 - ttaie) DIV 16) END;
    IF ttaie <= 4 THEN stones:= stones MOD FlameMult END;
    Boum(victim, aieStKinds, aieStStyle, stones, aieSKCount)
   END
  END
 END Aie;

 PROCEDURE Collision(obj1, obj2: ObjPtr): BOOLEAN;
  VAR
   px, py, mx, my, w, h: INT32;
 BEGIN
  WITH obj1^ DO
   px:= x + left; py:= y + top;
   mx:= midx + left; my:= midy + top;
   w:= right - left; h:= bottom - top
  END;
  WITH obj2^ DO
   RETURN
    (((px > x + left - w) AND (px < x + right) AND
      (py > y + top - h) AND (py < y + bottom)) OR
     ((step > 30) AND
      (mx > midx + left - w) AND (mx < midx + right) AND
      (my > midy + top - h) AND (my < midy + bottom)))
  END
 END Collision;

 PROCEDURE DoCollision(obj: ObjPtr; victims: AnimSet; Do: AieProc; VAR hit, fire: CARD16);
  VAR
   px, py, mx, my, w, h: INT32;
   victim, next, tail: ObjPtr;
   a: Anims;
 BEGIN
  WITH obj^ DO
   px:= x + left; py:= y + top;
   mx:= midx + left; my:= midy + top;
   w:= right - left; h:= bottom - top
  END;
  FOR a:= MIN(Anims) TO MACHINE DO
   IF a IN victims THEN
    victim:= First(animList[a]);
    tail:= Tail(animList[a]);
    WHILE victim <> tail DO
     WITH victim^ DO
      next:= Next(animNode);
      IF (obj <> victim) AND
        (((px > x + left - w) AND (px < x + right) AND
          (py > y + top - h) AND (py < y + bottom)) OR
         ((step > 30) AND
          (mx > midx + left - w) AND (mx < midx + right) AND
          (my > midy + top - h) AND (my < midy + bottom))) THEN
       Do(victim, obj, hit, fire)
      END
     END;
     victim:= next
    END
   END
  END
 END DoCollision;

 PROCEDURE PlayerCollision(obj: ObjPtr; VAR hit: CARD16);
  VAR
   victim, next, tail: ObjPtr;
   fire: CARD16;
 BEGIN
  fire:= 0;
  victim:= First(animList[PLAYER]);
  tail:= Tail(animList[PLAYER]);
  WHILE victim <> tail DO
   next:= Next(victim^.animNode);
   IF Collision(obj, victim) THEN
    Aie(victim, obj, hit, fire)
   END;
   victim:= next
  END
 END PlayerCollision;

 PROCEDURE DoToPlayer(obj: ObjPtr; Do: DoToPlayerProc);
  VAR
   player, tail, next: ObjPtr;
 BEGIN
  player:= First(animList[PLAYER]);
  tail:= Tail(animList[PLAYER]);
  WHILE player <> tail DO
   next:= Next(player^.animNode);
   IF Collision(obj, player) THEN
    Do(player, obj); RETURN
   END;
   player:= next
  END
 END DoToPlayer;

 PROCEDURE OutOfScreen(obj: ObjPtr): BOOLEAN;
  CONST
   DX = PW DIV 2;
   DY = PH DIV 2;
  VAR
   px, py: INT16;
 BEGIN
  WITH obj^ DO
   px:= x DIV Frac; DEC(px, backpx);
   py:= y DIV Frac; DEC(py, backpy);
   RETURN (px + width <= -DX)
       OR (py + height <= -DY)
       OR (px >= PW + DX)
       OR (py >= PH + DY)
  END
 END OutOfScreen;

 PROCEDURE OutOfBounds(obj: ObjPtr): BOOLEAN;
  VAR
   gw, gh: INT32;
 BEGIN
  gw:= gameWidth; gw:= gw * Frac;
  gh:= gameHeight; gh:= gh * Frac;
  WITH obj^ DO
   RETURN (x + left >= gw) OR (y + top >= gh)
       OR (x + right < 0) OR (y + bottom < 0)
  END
 END OutOfBounds;

 PROCEDURE AvoidBounds(obj: ObjPtr; return: INT16);
  VAR
   gw, gh: INT32;
 BEGIN
  gw:= gameWidth; gw:= gw * Frac;
  gh:= gameHeight; gh:= gh * Frac;
  WITH obj^ DO
   IF (x + left < 0) THEN
    x:= -left;
    dvx:= ABS(dvx) * return DIV 4;
    vx:= ABS(vx) * return DIV 4
   ELSIF (x + right > gw) THEN
    x:= gw - right;
    dvx:= -ABS(dvx) * return DIV 4;
    vx:= -ABS(vx) * return DIV 4
   END;
   IF (y + top < 0) THEN
    y:= -top;
    dvy:= ABS(dvy) * return DIV 4;
    vy:= ABS(vy) * return DIV 4
   ELSIF (y + bottom > gh) THEN
    y:= gh - bottom;
    dvy:= -ABS(dvy) * return DIV 4;
    vy:= -ABS(vy) * return DIV 4
   END
  END
 END AvoidBounds;

 PROCEDURE CheckRect(obj: ObjPtr; tx, ty: INT32; VAR rx, ry: INT16; VAR cr: CARD16);
  VAR
   px, py, dx, dy: INT16;
   corner: CARD16;
 BEGIN
  dx:= 0; dy:= 0; corner:= 0;
  WITH obj^ DO
   py:= (ty + top) DIV Frac;
   IF (py >= 0) AND (py < gameHeight) THEN
    px:= (tx + left) DIV Frac;
    IF (px >= 0) AND (px < gameWidth) AND
       (castle^[py DIV BH, px DIV BW] >= NbBackground) THEN
     INC(dx); INC(dy); INC(corner)
    END;
    px:= (tx + right - Frac) DIV Frac;
    IF (px >= 0) AND (px < gameWidth) AND
       (castle^[py DIV BH, px DIV BW] >= NbBackground) THEN
     DEC(dx); INC(dy); INC(corner)
    END
   END;
   py:= (ty + bottom - Frac) DIV Frac;
   IF (py >= 0) AND (py < gameHeight) THEN
    px:= (tx + left) DIV Frac;
    IF (px >= 0) AND (px < gameWidth) AND
       (castle^[py DIV BH, px DIV BW] >= NbBackground) THEN
     INC(dx); DEC(dy); INC(corner)
    END;
    px:= (tx + right - Frac) DIV Frac;
    IF (px >= 0) AND (px < gameWidth) AND
       (castle^[py DIV BH, px DIV BW] >= NbBackground) THEN
     DEC(dx); DEC(dy); INC(corner)
    END
   END
  END;
  rx:= dx; ry:= dy; cr:= corner
 END CheckRect;

 PROCEDURE InBackground(obj: ObjPtr): BOOLEAN;
  VAR
   dx, dy: INT16;
   corner: CARD16;
 BEGIN
  WITH obj^ DO
   CheckRect(obj, x, y, dx, dy, corner)
  END;
  RETURN corner > 0
 END InBackground;

 PROCEDURE AvoidRect(obj: ObjPtr; tx, ty: INT32; return: INT16; VAR corner: CARD16);
  VAR
   oldx, oldy: INT32;
   olddvx, olddvy, oldvx, oldvy, dx, dy: INT16;
 BEGIN
  CheckRect(obj, tx, ty, dx, dy, corner);
  WITH obj^ DO
   oldx:= x; oldy:= y; oldvx:= vx; oldvy:= vy;
   olddvx:= dvx; olddvy:= dvy;
   IF dx > 0 THEN
    x:= ((tx + left) DIV Frac DIV BW + 1) * BW * Frac - left;
    vx:= ABS(vx) * return DIV 4; dvx:= ABS(dvx) * return DIV 4
   ELSIF dx < 0 THEN
    x:= ((tx + right - 1) DIV Frac DIV BW * BW + 1) * Frac - right - 1;
    vx:= -ABS(vx) * return DIV 4; dvx:= -ABS(dvx) * return DIV 4
   END;
   IF dy > 0 THEN
    y:= ((ty + top) DIV Frac DIV BH + 1) * BH * Frac - top;
    vy:= ABS(vy) * return DIV 4; dvy:= ABS(dvy) * return DIV 4
   ELSIF dy < 0 THEN
    y:= ((ty + bottom - 1) DIV Frac DIV BH * BH + 1) * Frac - bottom - 1;
    vy:= -ABS(vy) * return / 4; dvy:= -ABS(dvy) * return / 4
   END;
   IF (corner = 1) AND (oldvx <> 0) AND (oldvy <> 0) THEN
    IF ABS(x - oldx) < ABS(y - oldy) THEN
     y:= oldy; vy:= oldvy; dvy:= olddvy
    ELSE
     x:= oldx; vx:= oldvx; dvx:= olddvx
    END
   END
  END
 END AvoidRect;

 PROCEDURE AvoidBackground(obj: ObjPtr; return: INT16);
  VAR
   corner: CARD16;
 BEGIN
  corner:= 0;
  WITH obj^ DO
   IF step > 30 THEN AvoidRect(obj, midx, midy, return, corner) END;
   IF corner = 0 THEN AvoidRect(obj, x, y, return, corner) END
  END
 END AvoidBackground;

 PROCEDURE AvoidAnims(obj: ObjPtr; anims: AnimSet);
  VAR
   aobj, tail: ObjPtr;
   k: Anims;
   dx, dy, dv, dh, dist: INT16;
   px, py, ax, ay: INT16;
   vx, vy: INT16;
   svx, svy, tt: INT32;
 BEGIN
  tt:= 0; svx:= 0; svy:= 0;
  GetCenter(obj, px, py);
  FOR k:= MIN(Anims) TO MAX(Anims) DO
   IF k IN anims THEN
    aobj:= First(animList[k]);
    tail:= Tail(animList[k]);
    WHILE aobj <> tail DO
     GetCenter(aobj, ax, ay);
     dy:= ay - py; dx:= ax - px;
     dv:= ABS(dy); dh:= ABS(dx);
     IF dv > dh THEN dist:= dv ELSE dist:= dh END;
     IF (dist < 60) AND (aobj^.vx DIV 16 * dx + aobj^.vy DIV 16 * dy < 0) THEN
      vx:= (aobj^.vy * 3 DIV 2);
      vy:= - (aobj^.vx * 3 DIV 2);
      IF (vy DIV 32 * dy + vx DIV 32 * dx > 0) THEN
       vx:= -vx; vy:= -vy
      END;
      INC(vx, aobj^.vx DIV 2);
      INC(vy, aobj^.vy DIV 2);
      INC(svx, vx); INC(svy, vy);
      INC(tt)
     END;
     aobj:= Next(aobj^.animNode)
    END
   END
  END;
  IF tt > 0 THEN
   WITH obj^ DO
    dvx:= svx DIV tt; dvy:= svy DIV tt;
    ax:= 0; ay:= 0
   END
  END
 END AvoidAnims;

 PROCEDURE Gravity(obj: ObjPtr; victims: AnimSet);
  (* Electronic gravity ! *)
  VAR
   aobj, tail: ObjPtr;
   charge, istep: INT16;
   px, py, rx, ry, r, r2: INT16;
   k: Anims;
 BEGIN
  GetCenter(obj, px, py);
  istep:= step;
  FOR k:= MIN(Anims) TO MAX(Anims) DO
   IF k IN victims THEN
    aobj:= First(animList[k]);
    tail:= Tail(animList[k]);
    WHILE aobj <> tail DO
     GetCenter(aobj, rx, ry);
     DEC(rx, px); DEC(ry, py);
     IF (obj <> aobj) AND (ABS(rx) < 128) AND (ABS(ry) < 128) THEN
      r2:= rx * rx + ry * ry;
      IF r2 < 256 THEN r2:= 256 END;
      r:= SQRT(r2);
      rx:= rx * 256 / r;
      ry:= ry * 256 / r;
      WITH aobj^ DO
       charge:= - (attr^.charge * obj^.attr^.charge) DIV 128;
       IF water THEN charge:= charge DIV 2 END;
       DEC(vx, ((rx * charge) / r2) * istep);
       IF vx > 4096 THEN vx:= 4096 ELSIF vx < -4096 THEN vx:= -4096 END;
       DEC(vy, ((ry * charge) / r2) * istep);
       IF vy > 4096 THEN vy:= 4096 ELSIF vy < -4096 THEN vy:= -4096 END
      END
     END;
     aobj:= Next(aobj^.animNode)
    END
   END
  END
 END Gravity;

 PROCEDURE Burn(obj: ObjPtr);
  VAR
   flame: ObjPtr;
   tmp, stp, size, delay, power, maxpower: CARD16;
   px, py, nx, ny, rnd, cos, sin: INT16;
   angle: INT16;
 BEGIN
  WITH obj^ DO
   IF (temperature < Warm) OR water THEN RETURN END;
   GetCenter(obj, px, py);
   size:= (width + height) DIV (mulS * 4);
   tmp:= (temperature - Warm) DIV 14;
   maxpower:= tmp DIV 350 + 1;
   stp:= step;
   WHILE stp > 0 DO
    IF RND() < tmp THEN
      (* Random direction *)
     angle:= RND() MOD 360;
     cos:= COS(angle);
     sin:= SIN(angle);
      (* Random position within obj *)
     rnd:= RND() MOD (size + 1);
     nx:= px + cos DIV 16 * rnd DIV 64;
     ny:= py + sin DIV 16 * rnd DIV 64;
      (* Random speed *)
     rnd:= RND() MOD 16;
     sin:= sin * rnd DIV 16 + vy * 3 DIV 4;
     cos:= cos * rnd DIV 16 + vx * 3 DIV 4;
      (* Random power *)
     power:= RND() MOD maxpower + 1;
      (* Random delay before flame fires *)
     delay:= RND() MOD Period;
     flame:= CreateObj(WEAPON, ORD(FIRE), nx, ny, delay, power);
     SetObjVXY(flame, cos, sin)
    END;
    DEC(stp)
   END
  END
 END Burn;

 PROCEDURE LimitSpeed(obj: ObjPtr; max: INT16);
  VAR
   ovx, ovy, speed: INT16;
 BEGIN
  WITH obj^ DO
   IF (ABS(vx * 2) < max) AND (ABS(vy * 2) < max) THEN RETURN END;
   ovx:= (vx + 32) DIV 64; ovy:= (vy + 32) DIV 64;
   speed:= SQRT(ovx * ovx + ovy * ovy);
   IF speed * 64 > max THEN
    max:= max DIV 64;
    vx:= vx / speed * max;
    vy:= vy / speed * max
   END;
  END
 END LimitSpeed;

 PROCEDURE Leave(obj: ObjPtr);
 BEGIN
  LeaveObj(obj)
 END Leave;

 VAR
  leftObj, leftTail: ObjPtr;

 PROCEDURE CheckLeftObjs;
  VAR
   testObj: ObjPtr;
   cnt: CARD16;
 BEGIN
  cnt:= step DIV 4;
  LOOP
   IF leftObj = leftTail THEN
    leftObj:= FirstObj(leftObjList)
   END;
   IF leftObj = leftTail THEN EXIT END;
   testObj:= leftObj; leftObj:= NextObj(leftObj^.objNode);
   IF NOT(OutOfScreen(testObj)) THEN
    RestartObj(testObj)
   ELSIF cnt > 0 THEN
    DEC(cnt)
   ELSE
    EXIT
   END
  END
 END CheckLeftObjs;

 PROCEDURE FadeIn;
  VAR
   lf, f, c: CARD16;
 BEGIN
  IF NOT(color) OR NOT(out) THEN out:= FALSE; RETURN END;
  RemEvents(EventTypeSet{eMENU});
  SetArea(mainArea);
  ResetCycle(TRUE);
  FOR c:= 0 TO 15 DO
   CycleRGB(c, 16, 0, 0, 0);
   DualCycleRGB(c, 16, 0, 0, 0)
  END;
  StartTime(time); out:= FALSE;
  lf:= 0;
  FOR f:= 1 TO 256 DO
   IF WaitTime(time, 1) OR (f = 256) THEN
    AnimPalette(f - lf); lf:= f;
    UpdateArea
   END
  END;
  AddEvents(EventTypeSet{eMENU})
 END FadeIn;

 PROCEDURE FadeOut;
  VAR
   lf, f, c: CARD16;
 BEGIN
  SetBusyStat(statBusy);
  SetArea(mainArea);
  IF color AND NOT(out) THEN
   RemEvents(EventTypeSet{eMENU});
   ResetCycle(FALSE);
   FOR c:= 0 TO 15 DO
    CycleRGB(c, 16, 0, 0, 0);
    DualCycleRGB(c, 16, 0, 0, 0)
   END;
   StartTime(time); lf:= 256;
   FOR f:= 255 TO 0 BY -1 DO
    IF WaitTime(time, 1) OR (f = 0) THEN
     AnimPalette(lf - f); lf:= f;
     UpdateArea
    END
   END;
   AddEvents(EventTypeSet{eMENU})
  ELSE
   SetBuffer(TRUE, FALSE);
   SetPen(0); FillRect(0, 0, W(SW), H(SH))
  END;
  out:= TRUE
 END FadeOut;

 PROCEDURE WhiteFade;
  VAR
   lf, f, c: CARD16;
 BEGIN
  IF NOT(color) OR NOT(out) THEN out:= FALSE; RETURN END;
  RemEvents(EventTypeSet{eMENU});
  SetArea(mainArea);
  ResetCycle(TRUE);
  FOR c:= 0 TO 15 DO
   CycleRGB(c, 16, 255, 255, 255)
  END;
  StartTime(time); out:= FALSE;
  FOR f:= 0 TO 255 BY 3 DO
   IF WaitTime(time, 1) OR (f = 255) THEN
    FOR c:= 0 TO 15 DO SetPalette(c, f, f, f) END;
    UpdateArea
   END
  END;
  lf:= 256;
  FOR f:= 255 TO 0 BY -1 DO
   IF WaitTime(time, 2) OR (f = 0) THEN
    AnimPalette(lf - f); lf:= f;
    UpdateArea
   END
  END;
  AddEvents(EventTypeSet{eMENU})
 END WhiteFade;

 PROCEDURE FadeFrom(r, g, b: CARD8);
  VAR
   fr, fg, fb, nr, ng, nb, f, c: CARD16;
 BEGIN
  SetBusyStat(statBusy);
  IF NOT(color) THEN RETURN END;
  FOR c:= 0 TO 15 DO SetTrans(c, 255) END;
  fr:= r; fg:= g; fb:= b;
  SetArea(mainArea);
  StartTime(time);
  FOR f:= 1 TO 256 DO
   IF WaitTime(time, 1) OR (f = 256) THEN
    FOR c:= 0 TO 7 DO
     WITH palette[c] DO nr:= red; ng:= green; nb:= blue END;
     SetPalette(c, nr * f DIV 256, ng * f DIV 256, nb * f DIV 256)
    END;
    FOR c:= 8 TO 15 DO
     WITH palette[c - 8] DO nr:= red; ng:= green; nb:= blue END;
     nr:= nr * f + fr * (256 - f);
     nb:= nb * f + fb * (256 - f);
     ng:= ng * f + fg * (256 - f);
     SetPalette(c, nr DIV 256, ng DIV 256, nb DIV 256)
    END;
    UpdateArea
   END
  END
 END FadeFrom;

 PROCEDURE UpdateXY(obj: ObjPtr);
  VAR
   oldx, oldy, mstep: INT32;
   nax, nay, max, may, ndvx, ndvy, mvx, mvy, dstep: INT16;
   tocool, cstep: CARD16;
 BEGIN
  WITH obj^ DO
   mvx:= vx; mvy:= vy;
   IF (sleeper > 0) AND (kind IN AnimSet{ALIEN2, ALIEN1}) THEN
    ndvx:= 0; ndvy:= 0;
    nax:= 0; nay:= 0
   ELSE
    ndvx:= dvx; ndvy:= dvy;
    nax:= ax; nay:= ay
   END;
   mstep:= step;
   cstep:= step + adelay;
   adelay:= cstep MOD 8;
   cstep:= cstep DIV 8;
   tocool:= cstep * attr^.coolSpeed;
   dstep:= cstep;
   IF (tocool <= temperature) AND NOT(water) THEN
    DEC(temperature, tocool)
   ELSE
    temperature:= 0
   END;
   IF (nax = 0) AND (nay = 0) THEN
    max:= attr^.inerty;
    IF water THEN
     IF (SGN(ndvx) * SGN(vx) <= 0) AND (SGN(ndvy) * SGN(vy) <= 0) THEN
      max:= max * 2
     ELSE
      max:= max / 2
     END
    END;
    IF snow THEN
     max:= max / 3
    END;
    may:= max;
    DEC(ndvx, vx); DEC(ndvy, vy);
    IF ndvx > 0 THEN
     max:= max * dstep; IF max > ndvx THEN max:= ndvx END
    ELSE
     max:= -max * dstep; IF max < ndvx THEN max:= ndvx END
    END;
    IF ndvy > 0 THEN
     may:= may * dstep; IF may > ndvy THEN may:= ndvy END
    ELSE
     may:= -may * dstep; IF may < ndvy THEN may:= ndvy END
    END;
    INC(vx, max); INC(vy, may)
   ELSE
    max:= ax; may:= ay;
    INC(vx, max * dstep); dvx:= vx;
    INC(vy, may * dstep); dvy:= vy
   END;
   IF vx > 4096 THEN vx:= 4096 ELSIF vx < -4096 THEN vx:= -4096 END;
   IF vy > 4096 THEN vy:= 4096 ELSIF vy < -4096 THEN vy:= -4096 END;
   mvx:= (mvx + vx) / 2;
   mvy:= (mvy + vy) / 2;
   oldx:= x; oldy:= y;
   IF water THEN
    INC(x, mvx / 3 * mstep); (* LONG(mvx DIV 3) *)
    INC(y, mvy / 3 * mstep)
   ELSE
    INC(x, mvx / 2 * mstep);
    INC(y, mvy / 2 * mstep)
   END;
   midx:= (x + oldx) DIV 2;
   midy:= (y + oldy) DIV 2
  END
 END UpdateXY;

 PROCEDURE NextFrame;
 BEGIN
  step:= GetTime(time) - lasttime;
  INC(lasttime, step);
  IF step > 60 THEN step:= 60 END
 END NextFrame;

 PROCEDURE HotFlush;
 BEGIN
  UpdatePalette;
  RenderObjects;
  SetArea(mainArea);
  SetBuffer(TRUE, FALSE);
  AddEvents(EventTypeSet{eMENU, eREFRESH});
  SetBusyStat(statWaiting)
 END HotFlush;

 PROCEDURE HotInit;
 BEGIN
  SetBusyStat(statReady);
  RemEvents(EventTypeSet{eMENU, eREFRESH});
  SetArea(mainArea);
  SetBuffer(TRUE, TRUE);
  UpdatePalette;
  IF (screenInverted > 0) AND color THEN
   SetPalette(0, 255, 255, 255);
   SetPalette(4, 255, 255, 255)
  END;
  GetMouse(lastMouseX, lastMouseY);
  nextGunFireTime:= GunFireSpeed;
  leftTail:= TailObj(leftObjList);
  leftObj:= leftTail;
  lasttime:= 0; step:= Period DIV 60;
  StartTime(time)
 END HotInit;

 PROCEDURE Close;
 BEGIN
  FreeTime(time)
 END Close;

BEGIN

 time:= AllocTime(Period);
 CheckMemBool(time = noTime);
 out:= TRUE;
 AddTermProc(Close);

END ChaosActions.
