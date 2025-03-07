IMPLEMENTATION MODULE ChaosAlien;

 FROM Memory IMPORT CARD16, INT16, CARD32, INT32, AllocMem, AddTail;
 FROM Checks IMPORT CheckMem;
 FROM Trigo IMPORT RND, SQRT, SIN, COS;
 FROM ChaosBase IMPORT Anims, AnimSet, BasicTypes, Obj, ObjPtr, ObjAttr,
  ObjAttrPtr, attrList, Stones, StoneSet, gravityStyle, slowStyle, Frac, Period,
  step, difficulty, powerCountDown, fastStyle, zone, Zone, level, nbDollar,
  pLife, mainPlayer, addpt, returnStyle, FlameMult, Weapon, WeaponAttr,
  weaponAttr, ObjFlags, ObjFlagSet;
 FROM ChaosGraphics IMPORT gameWidth, gameHeight;
 FROM ChaosSounds IMPORT SoundList, soundList, Sound, Effect, SetEffect,
  SoundEffect, nulSound;
 FROM ChaosActions IMPORT SetObjLoc, SetObjRect, UpdateXY, Aie, Die,
  OutOfScreen, AvoidBounds, AvoidBackground, Leave, DoCollision, DecLife,
  GetCenter, Burn, LimitSpeed, CreateObj, SetObjVXY, SetObjAXY, InBackground,
  AvoidAnims, PlayerCollision, OutOfBounds;
 FROM ChaosBonus IMPORT BoumMoney, Moneys, MoneySet, TimedBonus, tbMagnet,
  tbInvinsibility, tbSleeper, tbBullet, tbHospital;
 FROM ChaosSmartBonus IMPORT sbExtraLife;

 VAR
  zeroFire: CARD16;


 PROCEDURE MakeCartoon(cartoon: ObjPtr);
  VAR
   px, py, sz: INT16;
 BEGIN
  WITH cartoon^ DO
   IF stat = 0 THEN px:= 160; py:= 88; sz:= 32
   ELSIF stat = 1 THEN px:= 176; py:= 72; sz:= 16
   ELSE px:= 160; py:= 72; sz:= 16
   END
  END;
  SetObjLoc(cartoon, px, py, sz, sz);
  SetObjRect(cartoon, 0, 0, sz, sz)
 END MakeCartoon;

 PROCEDURE MakeAlien0(alien: ObjPtr);
 BEGIN
  SetObjLoc(alien, 0, 76, 20, 20);
  SetObjRect(alien, 0, 0, 20, 20)
 END MakeAlien0;

 PROCEDURE MakeSmallDrawer(drawer: ObjPtr);
 BEGIN
  SetObjLoc(drawer, 168, 60, 16, 12);
  SetObjRect(drawer, 0, 0, 16, 12)
 END MakeSmallDrawer;

 PROCEDURE MakeBigDrawer(drawer: ObjPtr);
 BEGIN
  SetObjLoc(drawer, 96, 76, 32, 20);
  SetObjRect(drawer, 0, 0, 32, 20)
 END MakeBigDrawer;

 PROCEDURE MakeHospital(hospital: ObjPtr);
 BEGIN
  SetObjLoc(hospital, 20, 76, 20, 20);
  SetObjRect(hospital, 0, 0, 20, 20)
 END MakeHospital;

 PROCEDURE MakeDiese(diese: ObjPtr);
 BEGIN
  SetObjLoc(diese, 144, 72, 15, 15);
  SetObjRect(diese, 0, 0, 15, 15)
 END MakeDiese;

 PROCEDURE MakeKamikaze(kamikaze: ObjPtr);
  VAR
   px, py: INT16;
 BEGIN
  WITH kamikaze^ DO
   py:= (stat DIV 2) * 16;
   px:= (stat MOD 2) * 16
  END;
  SetObjLoc(kamikaze, px + 96, py + 96, 16, 16);
  SetObjRect(kamikaze, 0, 0, 16, 16)
 END MakeKamikaze;

 PROCEDURE MakePic(pic: ObjPtr);
  VAR
   py: INT16;
 BEGIN
  py:= pic^.stat * 16 + 128;
  SetObjLoc(pic, 80, py, 16, 16);
  SetObjRect(pic, 0, 0, 16, 16)
 END MakePic;

 PROCEDURE MakeStar(star: ObjPtr);
 BEGIN
  SetObjLoc(star, 64, 148, 16, 16);
  SetObjRect(star, 0, 0, 16, 16)
 END MakeStar;

 PROCEDURE MakeBubble(bubble: ObjPtr);
 BEGIN
  SetObjLoc(bubble, 64, 164, 16, 16);
  SetObjRect(bubble, 1, 1, 15, 15)
 END MakeBubble;

 PROCEDURE MakeBumper(bumper: ObjPtr);
 BEGIN
  SetObjLoc(bumper, 76, 76, 20, 20);
  SetObjRect(bumper, 0, 0, 20, 20)
 END MakeBumper;

 PROCEDURE MakeTri(tri: ObjPtr);
 BEGIN
  SetObjLoc(tri, 224, 164, 24, 20);
  SetObjRect(tri, 4, 4, 20, 16)
 END MakeTri;

 PROCEDURE MakeTrefle(trefle: ObjPtr);
 BEGIN
  SetObjLoc(trefle, 80, 160, 20, 20);
  SetObjRect(trefle, 0, 0, 20, 20)
 END MakeTrefle;

 PROCEDURE MakeBig(big: ObjPtr);
 BEGIN
  SetObjLoc(big, 100, 180, 20, 20);
  SetObjRect(big, 1, 1, 19, 19)
 END MakeBig;

 PROCEDURE MakeSquare(square: ObjPtr);
 BEGIN
  SetObjLoc(square, 101, 101, 22, 22);
  SetObjRect(square, 0, 0, 22, 22)
 END MakeSquare;

 PROCEDURE MakeFlame(flame: ObjPtr);
  VAR
   py: INT16;
 BEGIN
  IF flame^.stat = 0 THEN py:= 172 ELSE py:= 184 END;
  SetObjLoc(flame, 200, py, 12, 12);
  SetObjRect(flame, 0, 0, 12, 12)
 END MakeFlame;

 PROCEDURE MakeColor(color: ObjPtr);
  VAR
   px: INT16;
 BEGIN
  px:= color^.life DIV 20;
  IF px > 4 THEN px:= 4 END;
  px:= px * 20 + 100;
  SetObjLoc(color, px, 160, 20, 20);
  SetObjRect(color, 0, 0, 20, 20)
 END MakeColor;

 PROCEDURE ResetCartoon(cartoon: ObjPtr);
 BEGIN
  WITH cartoon^ DO
   stat:= life MOD 3;
   IF stat = 0 THEN life:= 60 ELSE life:= 20 END;
   hitSubLife:= life; fireSubLife:= life
  END;
  MakeCartoon(cartoon)
 END ResetCartoon;

 PROCEDURE ResetAlien0(alien: ObjPtr);
  VAR
   nvx, nvy: INT16;
 BEGIN
  WITH alien^ DO
   moveSeq:= Period * 5 + Period * (RND() MOD 10);
   hitSubLife:= life; fireSubLife:= life;
   nvx:= RND() MOD 1024; nvy:= RND() MOD 1024;
   dvx:= nvx - 512; dvy:= nvy - 512;
   attr^.Make(alien)
  END
 END ResetAlien0;

 PROCEDURE ResetSmallDrawer(drawer: ObjPtr);
 BEGIN
  WITH drawer^ DO
   life:= (8 - powerCountDown DIV 2 + difficulty) * 5;
   hitSubLife:= life; fireSubLife:= life
  END;
  MakeSmallDrawer(drawer)
 END ResetSmallDrawer;

 PROCEDURE ResetBigDrawer(drawer: ObjPtr);
 BEGIN
  WITH drawer^ DO
   life:= (9 - powerCountDown DIV 2 + difficulty) * 8;
   hitSubLife:= life; fireSubLife:= life
  END;
  MakeBigDrawer(drawer)
 END ResetBigDrawer;

 PROCEDURE ResetHospital(hospital: ObjPtr);
  VAR
   angle: INT16;
 BEGIN
  WITH hospital^ DO
   hitSubLife:= life; fireSubLife:= life;
   angle:= RND() MOD 360;
   vx:= COS(angle); vy:= SIN(angle)
  END;
  MakeHospital(hospital)
 END ResetHospital;

 PROCEDURE ResetDiese(diese: ObjPtr);
 BEGIN
  WITH diese^ DO
   fireSubLife:= life;
   hitSubLife:= life
  END;
  MakeDiese(diese)
 END ResetDiese;

 PROCEDURE ResetKamikaze(kamikaze: ObjPtr);
 BEGIN
  WITH kamikaze^ DO
   stat:= life MOD 4; life:= 30;
   hitSubLife:= life; fireSubLife:= life;
   moveSeq:= 2
  END;
  MakeKamikaze(kamikaze)
 END ResetKamikaze;

 PROCEDURE ResetPic(pic: ObjPtr);
 BEGIN
  WITH pic^ DO
   stat:= life MOD 2; life:= 40 + difficulty * 16;
   hitSubLife:= life; fireSubLife:= life;
   moveSeq:= 2
  END;
  MakePic(pic)
 END ResetPic;

 PROCEDURE ResetStar(star: ObjPtr);
 BEGIN
  WITH star^ DO
   moveSeq:= 0;
   hitSubLife:= life; fireSubLife:= 0
  END;
  MakeStar(star)
 END ResetStar;

 PROCEDURE ResetBubble(bubble: ObjPtr);
 BEGIN
  WITH bubble^ DO
   moveSeq:= 0;
   hitSubLife:= 0; fireSubLife:= life
  END;
  MakeBubble(bubble)
 END ResetBubble;

 PROCEDURE ResetBumper(bumper: ObjPtr);
 BEGIN
  WITH bumper^ DO
   moveSeq:= 0;
   hitSubLife:= life; fireSubLife:= life
  END;
  MakeBumper(bumper)
 END ResetBumper;

 PROCEDURE ResetTri(tri: ObjPtr);
 BEGIN
  WITH tri^ DO
   fireSubLife:= life DIV 3;
   hitSubLife:= life - fireSubLife
  END;
  MakeTri(tri)
 END ResetTri;

 PROCEDURE ResetTrefle(trefle: ObjPtr);
 BEGIN
  WITH trefle^ DO
   hitSubLife:= life DIV 3;
   fireSubLife:= life - hitSubLife
  END;
  MakeTrefle(trefle)
 END ResetTrefle;

 PROCEDURE ResetBig(big: ObjPtr);
 BEGIN
  WITH big^ DO
   moveSeq:= 0;
   fireSubLife:= life DIV 4;
   hitSubLife:= life - fireSubLife
  END;
  MakeBig(big)
 END ResetBig;

 PROCEDURE ResetSquare(square: ObjPtr);
 BEGIN
  WITH square^ DO
   moveSeq:= 0;
   hitSubLife:= life DIV 4;
   fireSubLife:= life - hitSubLife
  END;
  MakeSquare(square)
 END ResetSquare;

 PROCEDURE ResetFlame(flame: ObjPtr);
 BEGIN
  WITH flame^ DO
   fireSubLife:= life DIV 2;
   hitSubLife:= life;
   shapeSeq:= 0; stat:= 0
  END;
  MakeFlame(flame)
 END ResetFlame;

 PROCEDURE ResetColor(color: ObjPtr);
 BEGIN
  WITH color^ DO
   hitSubLife:= 20; fireSubLife:= 10;
   moveSeq:= 0
  END;
  MakeColor(color)
 END ResetColor;

 VAR
  aieAlien0Effect, aieHospitalEffect, aieCartoonEffect,
  aieBigEffect, dieBigEffect,
  aieSquareEffect, aieTriEffect: ARRAY[0..0] OF Effect;
  dieSquareEffect: ARRAY[0..13] OF Effect;
  aieDrawerEffect, aieTrefleEffect: ARRAY[0..1] OF Effect;
  dieAlien0Effect, dieSBEffect: ARRAY[0..8] OF Effect;
  dieDieseEffect, dieBigDrawerEffect, dieSmallDrawerEffect: ARRAY[0..16] OF Effect;
  aieKmkEffect, aiePicEffect: ARRAY[0..9] OF Effect;
  dieKmkEffect: ARRAY[0..7] OF Effect;
  dieFlameEffect: ARRAY[0..2] OF Effect;
  dieHospitalEffect, dieBumperEffect: ARRAY[0..15] OF Effect;
  dieSCartoonEffect: ARRAY[0..8] OF Effect;
  dieCartoonEffect, dieTriEffect: ARRAY[0..11] OF Effect;
  dieTrefleEffect: ARRAY[0..23] OF Effect;

 PROCEDURE Stop(victim, cartoon: ObjPtr; VAR hit, fire: CARD16);
  CONST
   RDST = Frac * 4;
  VAR
   px, py, ox, oy: INT16;
   sx, sy, sz, iz: INT32;
 BEGIN
  IF (victim^.kind = ALIEN1) AND (victim^.subKind = aCartoon) THEN RETURN END;
  WITH cartoon^ DO
   sx:= x; sy:= y; sz:= right
  END;
  GetCenter(victim, ox, oy);
  GetCenter(cartoon, px, py);
  WITH victim^ DO
   IF ABS(ox - px) > ABS(oy - py) THEN
    IF ox < px THEN
     iz:= x + right - sx; IF iz > RDST THEN iz:= RDST END;
     DEC(x, iz); vx:= -ABS(vx DIV 4)
    ELSE
     iz:= sx + sz - x; IF iz > RDST THEN iz:= RDST END;
     INC(x, iz); vx:= ABS(vx DIV 4)
    END
   ELSE
    IF oy < py THEN
     iz:= y + bottom - sy; IF iz > RDST THEN iz:= RDST END;
     DEC(y, iz); vy:= -ABS(vy DIV 4)
    ELSE
     iz:= sy + sz - y; IF iz > RDST THEN iz:= RDST END;
     INC(y, iz); vy:= ABS(vy DIV 4)
    END
   END
  END
 END Stop;

 PROCEDURE MoveCartoon(cartoon: ObjPtr);
 BEGIN
  IF OutOfScreen(cartoon) THEN Leave(cartoon); RETURN END;
  WITH cartoon^ DO
   vx:= 0; vy:= 0; dvx:= 0; dvy:= 0;
   DoCollision(cartoon, AnimSet{PLAYER, ALIEN3..MISSILE, SMARTBONUS, BONUS},
               Stop, hitSubLife, fireSubLife)
  END;
  UpdateXY(cartoon);
  Burn(cartoon)
 END MoveCartoon;

 PROCEDURE MoveDrawer(drawer: ObjPtr);
 BEGIN
  IF OutOfScreen(drawer) THEN Leave(drawer); RETURN END;
  AvoidBackground(drawer, 2);
  AvoidBounds(drawer, 2);
  WITH drawer^ DO
   dvx:= 0; dvy:= 0;
   DoCollision(drawer, AnimSet{PLAYER, ALIEN3..MISSILE, SMARTBONUS, BONUS},
               Stop, hitSubLife, fireSubLife)
  END;
  UpdateXY(drawer);
  Burn(drawer)
 END MoveDrawer;

 PROCEDURE MoveAlien0(alien: ObjPtr);
 BEGIN
  IF OutOfScreen(alien) THEN Leave(alien); RETURN END;
  UpdateXY(alien);
  LimitSpeed(alien, 1536);
  AvoidBounds(alien, 4);
  AvoidBackground(alien, 4);
  Burn(alien);
  WITH alien^ DO
   IF step > moveSeq THEN ResetAlien0(alien) ELSE DEC(moveSeq, step) END;
   PlayerCollision(alien, life);
   IF life = 0 THEN Die(alien) END
  END
 END MoveAlien0;

 PROCEDURE MoveHospital(hospital: ObjPtr);
  VAR
   nvx, nvy, nvl: INT16;
 BEGIN
  IF OutOfScreen(hospital) THEN Leave(hospital); RETURN END;
  WITH hospital^ DO
   nvx:= vx DIV 32; nvy:= vy DIV 32;
   nvl:= SQRT(nvx * nvx + nvy * nvy);
   IF nvl = 0 THEN
    ResetHospital(hospital)
   ELSIF ABS(nvl - 1024) > 256 THEN
    vx:= vx * 4 DIV nvl * 8;
    vy:= vy * 4 DIV nvl * 8
   END;
   dvx:= 0; dvy:= 0;
   ax:= vy DIV 64;
   ay:= -vx DIV 64
  END;
  UpdateXY(hospital);
  AvoidBounds(hospital, 4);
  AvoidBackground(hospital, 4);
  Burn(hospital);
  PlayerCollision(hospital, hospital^.life);
  IF hospital^.life = 0 THEN Die(hospital) END
 END MoveHospital;

 PROCEDURE MoveDiese(diese: ObjPtr);
  VAR
   px, py, mx, my, dx, dy, dl, speed: INT16;
 BEGIN
  IF zone <> Castle THEN
   AvoidBounds(diese, 0)
  ELSIF OutOfScreen(diese) THEN
   Leave(diese); RETURN
  END;
  UpdateXY(diese);
  AvoidBackground(diese, 0);
  Burn(diese);
  GetCenter(diese, mx, my);
  GetCenter(mainPlayer, px, py);
  dx:= px - mx; dy:= py - my;
  WITH diese^ DO
   IF (ABS(dx) < 128) AND (ABS(dy) < 128) THEN
    dl:= SQRT(dx * dx + dy * dy);
    IF dl <> 0 THEN
     speed:= 4 + difficulty DIV 2;
     dvx:= dx * 128 DIV dl * speed;
     dvy:= dy * 128 DIV dl * speed
    END
   ELSE
    dvx:= 0; dvy:= 0
   END;
   PlayerCollision(diese, life);
   IF life = 0 THEN Die(diese) END
  END
 END MoveDiese;

 PROCEDURE MovePic(pic: ObjPtr);
  VAR
   px, py, kx, ky, dx: INT16;
 BEGIN
  IF OutOfScreen(pic) THEN Leave(pic); RETURN END;
  UpdateXY(pic);
  WITH pic^ DO
   dx:= (stat * 2); DEC(dx);
   IF moveSeq = 2 THEN
    dvx:= 0; dvy:= 0;
    GetCenter(pic, kx, ky);
    GetCenter(mainPlayer, px, py);
    DEC(kx, px); DEC(ky, py);
    IF (kx * dx > 0) AND (ABS(ky) < ABS(kx)) AND (ABS(ky) < 120) THEN
     moveSeq:= 1
    END
   ELSIF moveSeq = 1 THEN
    SoundEffect(pic, aiePicEffect);
    moveSeq:= 0;
    dvx:= -dx * 4096
   ELSE
    DoCollision(pic, AnimSet{PLAYER, ALIEN1}, Aie, hitSubLife, fireSubLife);
    IF (hitSubLife = 0) OR (fireSubLife = 0) OR InBackground(pic) OR OutOfBounds(pic) THEN
     Die(pic)
    END
   END
  END
 END MovePic;

 PROCEDURE MoveKamikaze(kamikaze: ObjPtr);
  VAR
   px, py, kx, ky, dx, dy: INT16;
 BEGIN
  IF OutOfScreen(kamikaze) THEN Leave(kamikaze); RETURN END;
  UpdateXY(kamikaze);
  WITH kamikaze^ DO
   dy:= (stat DIV 2) * 2; DEC(dy);
   dx:= (stat MOD 2) * 2; DEC(dx);
   IF moveSeq = 2 THEN
    dvx:= 0; dvy:= 0;
    GetCenter(kamikaze, kx, ky);
    GetCenter(mainPlayer, px, py);
    DEC(kx, px); DEC(ky, py);
    IF (kx * dx > 0) AND (ky * dy > 0) THEN
     IF (ABS(kx) < 120) AND (ABS(ky) < 120) THEN
      moveSeq:= 1
     END
    END
   ELSIF moveSeq = 1 THEN
    SoundEffect(kamikaze, aieKmkEffect);
    moveSeq:= 0;
    dvx:= -dx * 2896;
    dvy:= -dy * 2896
   END;
   PlayerCollision(kamikaze, hitSubLife);
   IF (hitSubLife <> life) OR InBackground(kamikaze) OR OutOfBounds(kamikaze) THEN
    Die(kamikaze)
   END
  END
 END MoveKamikaze;

 PROCEDURE MoveBumper(bumper: ObjPtr);
  VAR
   nax, nay: INT16;
 BEGIN
  IF OutOfScreen(bumper) THEN Leave(bumper); RETURN END;
  LimitSpeed(bumper, 1600);
  UpdateXY(bumper);
  AvoidBounds(bumper, 4);
  AvoidBackground(bumper, 4);
  Burn(bumper);
  WITH bumper^ DO
   IF step > moveSeq THEN
    nax:= RND() MOD 256; nay:= RND() MOD 256;
    SetObjAXY(bumper, nax - 128, nay - 128);
    INC(moveSeq, Period DIV 3 + RND() MOD (Period * 2))
   END;
   DEC(moveSeq, step);
   PlayerCollision(bumper, life);
   IF life = 0 THEN Die(bumper) END
  END
 END MoveBumper;

 PROCEDURE MoveStarBubble(obj: ObjPtr);
  VAR
   px, py, mx, my: INT16;
   dx, dy, dl, speed: INT32;
 BEGIN
  IF OutOfScreen(obj) THEN Leave(obj); RETURN END;
  UpdateXY(obj);
  AvoidBounds(obj, 2);
  AvoidBackground(obj, 3);
  Burn(obj);
  WITH obj^ DO
   IF step > moveSeq THEN
    GetCenter(obj, mx, my);
    GetCenter(mainPlayer, px, py);
    dx:= px - mx; dy:= py - my;
    dl:= SQRT(dx * dx + dy * dy);
    IF dl <> 0 THEN
     speed:= 7 + difficulty DIV 3;
     dvx:= dx * 64 DIV dl * speed - 512;
     dvy:= dy * 64 DIV dl * speed - 512;
     INC(dvx, RND() MOD 1024);
     INC(dvy, RND() MOD 1024)
    END;
    INC(moveSeq, Period * (10 - difficulty + RND() MOD 8) + Period DIV 3)
   END;
   DEC(moveSeq, step);
   PlayerCollision(obj, life);
   IF life = 0 THEN Die(obj) END
  END
 END MoveStarBubble;

 PROCEDURE MoveTri(tri: ObjPtr);
  VAR
   dx, dy, dl, speed: INT32;
   px, py, mx, my: INT16;
 BEGIN
  IF OutOfScreen(tri) THEN Leave(tri); RETURN END;
  UpdateXY(tri);
  AvoidBackground(tri, 0);
  Burn(tri);
  WITH tri^ DO
   IF (vx = dvx) AND (vy = dvy) THEN
    IF (dvx = 0) AND (dvy = 0) THEN
     GetCenter(tri, mx, my);
     GetCenter(mainPlayer, px, py);
     dx:= px - mx; dy:= py - my;
     dl:= SQRT(dx * dx + dy * dy);
     IF dl <> 0 THEN
      speed:= 1300 + difficulty * 80;
      dvx:= dx * speed DIV dl;
      dvy:= dy * speed DIV dl
     END
    ELSE
     dvx:= 0; dvy:= 0
    END
   END;
   PlayerCollision(tri, life);
   IF life = 0 THEN Die(tri) END
  END
 END MoveTri;

 PROCEDURE MoveTrefle(trefle: ObjPtr);
  VAR
   tx, ty: INT16;
 BEGIN
  IF OutOfScreen(trefle) THEN Leave(trefle); RETURN END;
  UpdateXY(trefle);
  AvoidBounds(trefle, 0);
  AvoidBackground(trefle, 0);
  AvoidAnims(trefle, AnimSet{WEAPON, ALIEN3, MISSILE});
  LimitSpeed(trefle, 1300);
  Burn(trefle);
  WITH trefle^ DO
   IF step > moveSeq THEN
    dvx:= RND() MOD 512; dvy:= RND() MOD 512;
    GetCenter(trefle, tx, ty);
    IF tx > gameWidth DIV 2 THEN dvx:= -dvx END;
    IF ty > gameHeight DIV 2 THEN dvy:= -dvy END;
    INC(moveSeq, Period + RND() MOD Period)
   END;
   DEC(moveSeq, step);
   PlayerCollision(trefle, life);
   IF life = 0 THEN Die(trefle) END
  END
 END MoveTrefle;

 PROCEDURE MoveBig(big: ObjPtr);
  VAR
   angle: INT16;
   anims: AnimSet;
 BEGIN
  IF OutOfScreen(big) THEN Leave(big); RETURN END;
  UpdateXY(big);
  AvoidBounds(big, 4);
  AvoidBackground(big, 4);
  LimitSpeed(big, 1024);
  Burn(big);
  WITH big^ DO
   IF moveSeq = 0 THEN
    moveSeq:= 1;
    angle:= RND() MOD 360;
    dvx:= COS(angle);
    dvy:= SIN(angle)
   END;
   IF subKind = aBig THEN anims:= AnimSet{MACHINE} ELSE anims:= AnimSet{} END;
   DoCollision(big, AnimSet{PLAYER} + anims, Aie, zeroFire, life);
   IF life = 0 THEN Die(big) END
  END
 END MoveBig;

 PROCEDURE MoveFlame(flame: ObjPtr);
  VAR
   dx, dy, dl: INT32;
   px, py, fx, fy, dv, nax, nay: INT16;
 BEGIN
  IF OutOfScreen(flame) THEN Leave(flame); RETURN END;
  UpdateXY(flame);
  AvoidBackground(flame, 1);
  LimitSpeed(flame, 1536);
  Burn(flame);
  GetCenter(flame, fx, fy);
  GetCenter(mainPlayer, px, py);
  dx:= px - fx; dy:= py - fy;
  dl:= SQRT(dx * dx + dy * dy);
  IF dl <> 0 THEN
   nax:= dx * 64 DIV dl;
   nay:= dy * 64 DIV dl
  ELSE
   nax:= 0; nay:= 0
  END;
  WITH flame^ DO
   fx:= vx DIV 32; fy:= vy DIV 32;
   dv:= SQRT(fx * fx + fy * fy);
   IF dv <> 0 THEN
    DEC(nax, fx * 32 DIV dv);
    DEC(nay, fy * 32 DIV dv)
   END;
   ax:= nax; ay:= nay;
   IF step > shapeSeq THEN
    INC(shapeSeq, Period DIV 10);
    IF RND() MOD 8 <> 0 THEN stat:= 1 - stat END;
    MakeFlame(flame)
   END;
   IF step >= shapeSeq THEN shapeSeq:= 0 ELSE DEC(shapeSeq, step) END;
   PlayerCollision(flame, life);
   IF life = 0 THEN Die(flame) END
  END
 END MoveFlame;

 PROCEDURE AieCartoon(cartoon, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  DecLife(cartoon, hit, fire);
  SoundEffect(cartoon, aieCartoonEffect)
 END AieCartoon;

 PROCEDURE AieDrawer(drawer, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  DecLife(drawer, hit, fire);
  SoundEffect(drawer, aieDrawerEffect)
 END AieDrawer;

 PROCEDURE AieAlien0(alien, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  DecLife(alien, hit, fire);
  alien^.attr^.Make(alien);
  IF alien^.life > 0 THEN SoundEffect(alien, aieAlien0Effect) END
 END AieAlien0;

 PROCEDURE AieDiese(diese, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  DecLife(diese, hit, fire);
  IF diese^.life > 0 THEN SoundEffect(diese, aieAlien0Effect) END
 END AieDiese;

 PROCEDURE AieHospital(hospital, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  SoundEffect(hospital, aieHospitalEffect);
  DecLife(hospital, hit, fire)
 END AieHospital;

 PROCEDURE AieKamikaze(kamikaze, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  WITH kamikaze^ DO
   IF moveSeq = 2 THEN moveSeq:= 1 ELSE Die(kamikaze) END
  END;
  hit:= 0; fire:= 0
 END AieKamikaze;

 PROCEDURE AiePic(pic, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  WITH pic^ DO
   IF moveSeq = 2 THEN moveSeq:= 1 END
  END
 END AiePic;

 PROCEDURE AieSBB(obj, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  WITH obj^ DO
   IF ((hitSubLife > 0) AND (hit > 0)) OR ((fireSubLife > 0) AND (fire > 0)) THEN
    SoundEffect(obj, aieAlien0Effect)
   END
  END;
  DecLife(obj, hit, fire)
 END AieSBB;

 PROCEDURE AieTri(tri, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  SoundEffect(tri, aieTriEffect);
  DecLife(tri, hit, fire)
 END AieTri;

 PROCEDURE AieTrefle(trefle, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  INC(addpt, 1);
  SoundEffect(trefle, aieTrefleEffect);
  DecLife(trefle, hit, fire);
 END AieTrefle;

 PROCEDURE AieBig(big, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  INC(addpt, 4);
  SoundEffect(big, aieBigEffect);
  big^.moveSeq:= 0;
  DecLife(big, hit, fire)
 END AieBig;

 PROCEDURE AieSquare(square, src: ObjPtr; VAR hit, fire: CARD16);
 BEGIN
  square^.moveSeq:= 0;
  SoundEffect(square, aieSquareEffect);
  DecLife(square, hit, fire)
 END AieSquare;

 PROCEDURE DieCartoon(cartoon: ObjPtr);
 BEGIN
  IF cartoon^.stat = 0 THEN
   SoundEffect(cartoon, dieCartoonEffect)
  ELSE
   SoundEffect(cartoon, dieSCartoonEffect)
  END
 END DieCartoon;

 PROCEDURE DieAlien0(alien: ObjPtr);
  VAR
   heart: ObjPtr;
   px, py: INT16;
 BEGIN
  INC(addpt);
  SoundEffect(alien, dieAlien0Effect);
  IF (zone = Chaos) AND NOT(nested IN alien^.flags) THEN
   BoumMoney(alien, MoneySet{m1}, 1, 1)
  END;
  IF (level[zone] MOD 7 = 0) AND (difficulty < 10) AND (pLife = 1) THEN
   GetCenter(alien, px, py);
   heart:= CreateObj(SMARTBONUS, sbExtraLife, px, py, 1, 1);
   WITH heart^ DO ay:= 8; vy:= -512; dvy:= vy END
  END
 END DieAlien0;

 PROCEDURE DieDiese(diese: ObjPtr);
 BEGIN
  INC(addpt);
  SoundEffect(diese, dieDieseEffect)
 END DieDiese;

 PROCEDURE DieKamikaze(kamikaze: ObjPtr);
 BEGIN
  SoundEffect(kamikaze, dieKmkEffect)
 END DieKamikaze;

 PROCEDURE DieStarBubble(obj: ObjPtr);
 BEGIN
  INC(addpt, 2);
  SoundEffect(obj, dieSBEffect)
 END DieStarBubble;

 PROCEDURE DieBumper(bumper: ObjPtr);
 BEGIN
  INC(addpt, 3);
  IF zone <> Chaos THEN BoumMoney(bumper, MoneySet{m1}, 1, 3) END;
  SoundEffect(bumper, dieBumperEffect)
 END DieBumper;

 PROCEDURE DieTri(tri: ObjPtr);
 BEGIN
  IF NOT(nested IN tri^.flags) THEN BoumMoney(tri, MoneySet{st}, 1, 2) END;
  SoundEffect(tri, dieTriEffect);
 END DieTri;

 PROCEDURE DieTrefle(trefle: ObjPtr);
 BEGIN
  SoundEffect(trefle, dieTrefleEffect);
  IF NOT(nested IN trefle^.flags) THEN
   BoumMoney(trefle, MoneySet{st}, 1, 1)
  END
 END DieTrefle;

 PROCEDURE DieBig(big: ObjPtr);
 BEGIN
  SoundEffect(big, dieBigEffect)
 END DieBig;

 PROCEDURE DieSquare(square: ObjPtr);
 BEGIN
  IF NOT(nested IN square^.flags) THEN
   BoumMoney(square, MoneySet{m3}, 1, 3)
  END;
  SoundEffect(square, dieSquareEffect)
 END DieSquare;

 PROCEDURE DieFlame(flame: ObjPtr);
 BEGIN
  SoundEffect(flame, dieFlameEffect)
 END DieFlame;

 PROCEDURE DieSmallDrawer(drawer: ObjPtr);
  VAR
   heart: ObjPtr;
   px, py: INT16;
 BEGIN
  SoundEffect(drawer, dieSmallDrawerEffect);
  IF difficulty < 9 THEN
   GetCenter(drawer, px, py);
   heart:= CreateObj(SMARTBONUS, sbExtraLife, px, py, 1, 1);
   WITH heart^ DO ay:= 8; vy:= -384; dvy:= vy END
  END;
  IF difficulty < 10 THEN BoumMoney(drawer, MoneySet{m1, m2, m5}, 3, 6) END
 END DieSmallDrawer;

 PROCEDURE DieBigDrawer(drawer: ObjPtr);
  VAR
   bonus: ObjPtr;
   random, max, ssKind, bullets: CARD16;
   px, py: INT16;
   w: Weapon;
 BEGIN
  SoundEffect(drawer, dieBigDrawerEffect);
  max:= 30;
  IF difficulty < 10 THEN BoumMoney(drawer, MoneySet{m5}, 1, 7) END;
  GetCenter(drawer, px, py);
  random:= RND() MOD max;
  ssKind:= tbHospital;
  IF (random >= 6) AND (powerCountDown <= 14) THEN ssKind:= tbBullet END;
  IF random >= 16 THEN ssKind:= tbMagnet END;
  IF random >= 26 THEN ssKind:= tbInvinsibility END;
  IF random >= 29 THEN ssKind:= tbSleeper END;
  bullets:= 0;
  FOR w:= MIN(Weapon) TO MAX(Weapon) DO
   INC(bullets, weaponAttr[w].nbBullet)
  END;
  IF (bullets < 100 + RND() MOD 64) AND (powerCountDown <= 14) THEN
   ssKind:= tbBullet
  END;
  bonus:= CreateObj(BONUS, TimedBonus, px, py, ssKind, 1);
  IF zone <> Castle THEN SetObjAXY(bonus, 0, 12) END
 END DieBigDrawer;

 PROCEDURE DieHospital(hospital: ObjPtr);
  VAR
   bonus: ObjPtr;
   px, py: INT16;
 BEGIN
  SoundEffect(hospital, dieHospitalEffect);
  GetCenter(hospital, px, py);
  IF difficulty < 10 THEN
   bonus:= CreateObj(BONUS, TimedBonus, px, py, tbHospital, 1);
   SetObjVXY(bonus, hospital^.vx DIV 2, hospital^.vy DIV 2);
   IF zone <> Castle THEN SetObjAXY(bonus, 0, 12) END
  END;
  IF nbDollar = 0 THEN
   BoumMoney(hospital, MoneySet{m1, m2, m5}, 3, 11 - difficulty)
  END
 END DieHospital;

 PROCEDURE InitParams;
  VAR
   attr: ObjAttrPtr;
   c, v, f: INT16;
 BEGIN
  SetEffect(aieAlien0Effect[0], soundList[sCymbale], 0, 0, 120, 1);
  SetEffect(dieAlien0Effect[0], soundList[aCrash], 0, 0, 160, 4);
  SetEffect(dieSBEffect[0], soundList[aCrash], 0, 0, 160, 4);
  SetEffect(dieAlien0Effect[1], soundList[wCrash], 1164, 0, 160, 2);
  SetEffect(dieSBEffect[1], soundList[wCrash], 388, 0, 160, 2);
  FOR c:= 7 TO 1 BY -1 DO
   SetEffect(dieAlien0Effect[9 - c], nulSound, 1164, 0, c * 20, 2);
   SetEffect(dieSBEffect[9 - c], nulSound, 388, 0, c * 20, 2)
  END;
  SetEffect(dieDieseEffect[0], soundList[aCrash], 0, 0, 180, 4);
  SetEffect(dieDieseEffect[1], soundList[wCrash], 582, 0, 180, 3);
  FOR c:= 2 TO 16 DO
   v:= ((17 - c) * 11) * (ABS(3 - (c - 1) MOD 6) + 1) DIV 4;
   SetEffect(dieDieseEffect[c], nulSound, 582, 0, v, 3)
  END;
  SetEffect(aieKmkEffect[0], soundList[aPanflute], 0, 12544, 192, 2);
  SetEffect(aieKmkEffect[1], soundList[wPanflute], 25088, 12544, 24, 1);
  SetEffect(aiePicEffect[0], soundList[aCrash], 0, 10537, 100, 2);
  SetEffect(aiePicEffect[1], soundList[wCrash], 21074, 10537, 100, 2);
  FOR c:= 2 TO 9 DO
   SetEffect(aieKmkEffect[c], nulSound, 1568, 12544, (10 - c) * 3, 1);
   SetEffect(aiePicEffect[c], nulSound, 1317, 10537, (10 - c) * 12, 1)
  END;
  v:= 0;
  FOR c:= 0 TO 7 DO
   SetEffect(dieKmkEffect[c], soundList[aCrash], 0, 5575 + 995 * v, (8 - c) * 24, 4);
   v:= (v * 9 + 5) MOD 8
  END;
  SetEffect(aieHospitalEffect[0], soundList[aPanflute], 0, 16726, 80, 1);
  FOR c:= 0 TO 15 DO
   v:= ((c + 1) MOD 2) * 2987 + 15787;
   SetEffect(dieHospitalEffect[c], soundList[wPanflute], 1045, v, (16 - c) * 11, 4)
  END;
  SetEffect(aieTrefleEffect[0], soundList[sCymbale], 0, 12544, 100, 1);
  SetEffect(aieTrefleEffect[1], soundList[sCymbale], 0, 6272, 100, 1);
  SetEffect(dieCartoonEffect[0], soundList[wNoise], 418, 4181, 100, 5);
  SetEffect(dieCartoonEffect[1], soundList[wNoise], 836, 8363, 100, 5);
  SetEffect(dieCartoonEffect[2], soundList[wWhite], 1673, 16726, 150, 5);
  SetEffect(dieCartoonEffect[3], soundList[wNoise], 836, 8363, 100, 5);
  SetEffect(dieSCartoonEffect[0], soundList[wNoise], 1673, 16726, 100, 3);
  FOR c:= 4 TO 11 DO
   v:= 418 + (c MOD 2) * 418;
   f:= 4181 + (c MOD 2) * 4181;
   SetEffect(dieCartoonEffect[c], soundList[wNoise], v, f, (12 - c) * 11, 4);
   SetEffect(dieSCartoonEffect[c - 3], soundList[wNoise], v, f, (12 - c) * 11, 4)
  END;
  FOR c:= 0 TO 23 DO
   v:= ABS(c MOD 4 - 2) + 1;
   SetEffect(dieTrefleEffect[c], soundList[wCrash], 700, 4182 + v * 3485, (24 - c) * v * 3, 4)
  END;
  SetEffect(aieCartoonEffect[0], soundList[wNoise], 800, 8363, 40, 1);
  SetEffect(aieDrawerEffect[0], soundList[wNoise], 400, 12544, 80, 1);
  SetEffect(aieDrawerEffect[1], soundList[wNoise], 400, 6272, 80, 1);
  SetEffect(dieBigDrawerEffect[0], soundList[wNoise], 582, 8860, 160, 5);
  SetEffect(dieSmallDrawerEffect[0], soundList[wNoise], 582, 0, 160, 5);
  FOR c:= 1 TO 16 DO
   v:= (17 - c) * 8;
   SetEffect(dieBigDrawerEffect[c], nulSound, 582, 12544 - (c MOD 2) * 6969, v, 4);
   v:= v * (c MOD 2);
   SetEffect(dieSmallDrawerEffect[c], nulSound, 582, 0, v, 4)
  END;
  SetEffect(aieBigEffect[0], soundList[aPanflute], 0, 4181, 100, 2);
  SetEffect(dieBigEffect[0], soundList[sVerre], 0, 16726, 192, 4);
  SetEffect(aieSquareEffect[0], soundList[wVoice], 300, 0, 200, 2);
  SetEffect(dieSquareEffect[0], soundList[wNoise], 209, 4181, 192, 4);
  SetEffect(dieSquareEffect[1], soundList[wNoise], 418, 8363, 192, 4);
  SetEffect(dieSquareEffect[2], soundList[wNoise], 836, 16726, 192, 4);
  SetEffect(dieSquareEffect[3], soundList[sHHat], 0, 8363, 192, 4);
  SetEffect(dieSquareEffect[4], soundList[sHHat], 0, 16726, 192, 4);
  SetEffect(dieSquareEffect[5], soundList[sCymbale], 0, 8363, 192, 4);
  SetEffect(dieSquareEffect[6], soundList[wCrash], 418, 8363, 220, 4);
  SetEffect(dieSquareEffect[7], soundList[wCrash], 279, 5575, 220, 4);
  SetEffect(dieSquareEffect[8], soundList[wCrash], 418, 4181, 220, 4);
  SetEffect(dieSquareEffect[9], soundList[sHurryUp], 0, 16726, 220, 4);
  SetEffect(dieSquareEffect[10], soundList[sGun], 0, 8363, 160, 4);
  SetEffect(dieSquareEffect[11], soundList[wVoice], 6000, 9387, 210, 4);
  SetEffect(dieSquareEffect[12], soundList[wWhite], 523, 4181, 192, 4);
  SetEffect(dieSquareEffect[13], soundList[wWhite], 8363, 16726, 192, 4);
  SetEffect(aieTriEffect[0], soundList[wCrash], 522, 4181, 180, 1);
  FOR c:= 0 TO 11 DO
   v:= (12 - c) * 13 * ((c + 1) MOD 2);
   SetEffect(dieTriEffect[c], soundList[wNoise], 896, 12544, v, 4)
  END;
  FOR c:= 0 TO 15 DO
   f:= (c MOD 4);
   v:= (16 - c) * 12; IF f = 3 THEN v:= 0 END;
   IF f = 0 THEN f:= 16726 ELSIF f = 1 THEN f:= 8363 ELSE f:= 4181 END;
   SetEffect(dieBumperEffect[c], soundList[wNoise], f DIV 20, f, v, 4)
  END;
  SetEffect(dieFlameEffect[0], soundList[wNoise], 836, 16726, 150, 3);
  SetEffect(dieFlameEffect[1], soundList[wNoise], 418, 8363, 150, 3);
  SetEffect(dieFlameEffect[2], soundList[wNoise], 209, 4181, 150, 3);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetCartoon;
   Make:= MakeCartoon;
   Move:= MoveCartoon;
   Aie:= AieCartoon;
   Die:= DieCartoon;
   weight:= 100;
   heatSpeed:= 170;
   refreshSpeed:= 140;
   coolSpeed:= 0;
   aieStKinds:= StoneSet{stFOG3}; aieSKCount:= 1;
   aieStone:= 1; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stFOG3}; dieSKCount:= 1;
   dieStone:= 8; dieStStyle:= slowStyle;
   basicType:= Mineral;
   priority:= -70;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetAlien0;
   Make:= MakeAlien0;
   Move:= MoveAlien0;
   Aie:= AieAlien0;
   Die:= DieAlien0;
   charge:= 70;
   weight:= 12;
   inerty:= 24;
   heatSpeed:= 100;
   refreshSpeed:= 100;
   coolSpeed:= 10;
   aieStKinds:= StoneSet{stC35}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= gravityStyle;
   dieStKinds:= StoneSet{stC35}; dieSKCount:= 1;
   dieStone:= 6; dieStStyle:= gravityStyle;
   basicType:= Animal;
   priority:= -60;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetSmallDrawer;
   Make:= MakeSmallDrawer;
   Move:= MoveDrawer;
   Aie:= AieDrawer;
   Die:= DieSmallDrawer;
   charge:= 20; weight:= 50;
   inerty:= 30; priority:= 8;
   heatSpeed:= 75;
   refreshSpeed:= 50;
   coolSpeed:= 10;
   aieStKinds:= StoneSet{stFLAME2}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stCBOX, stFOG1}; dieSKCount:= 2;
   dieStone:= 7; dieStStyle:= fastStyle;
   basicType:= Bonus;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetBigDrawer;
   Make:= MakeBigDrawer;
   Move:= MoveDrawer;
   Aie:= AieDrawer;
   Die:= DieBigDrawer;
   charge:= 10; weight:= 100;
   inerty:= 60; priority:= 10;
   heatSpeed:= 75;
   refreshSpeed:= 50;
   coolSpeed:= 10;
   aieStKinds:= StoneSet{stFLAME1}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stCBOX, stRBOX, stFOG1}; dieSKCount:= 3;
   dieStone:= 13; dieStStyle:= fastStyle;
   basicType:= Bonus;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetHospital;
   Make:= MakeHospital;
   Move:= MoveHospital;
   Aie:= AieHospital;
   Die:= DieHospital;
   charge:= 15; weight:= 40;
   priority:= -30;
   heatSpeed:= 30;
   refreshSpeed:= 30;
   coolSpeed:= 30;
   aieStKinds:= StoneSet{stC35}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stC35}; dieSKCount:= 1;
   dieStone:= 5; dieStStyle:= fastStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetDiese;
   Make:= MakeDiese;
   Move:= MoveDiese;
   Aie:= AieDiese;
   Die:= DieDiese;
   charge:= 50; weight:= 50;
   inerty:= 50; priority:= -59;
   heatSpeed:= 40;
   refreshSpeed:= 35;
   coolSpeed:= 15;
   aieStKinds:= StoneSet{stC35}; aieSKCount:= 1;
   aieStone:= 1; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stC35}; dieSKCount:= 1;
   dieStone:= 4; dieStStyle:= slowStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetKamikaze;
   Make:= MakeKamikaze;
   Move:= MoveKamikaze;
   Aie:= AieKamikaze;
   Die:= DieKamikaze;
   charge:= 80; weight:= 100;
   inerty:= 100; priority:= -52;
   dieStKinds:= StoneSet{stCROSS, stFLAME1, stFLAME2, stFOG1}; dieSKCount:= 4;
   dieStone:= 20; dieStStyle:= fastStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetStar;
   Make:= MakeStar;
   Move:= MoveStarBubble;
   Aie:= AieSBB;
   Die:= DieStarBubble;
   charge:= 10; weight:= 50;
   inerty:= 32; priority:= -55;
   aieStKinds:= StoneSet{stC35, stFLAME1, stFLAME2}; aieSKCount:= 3;
   aieStone:= 5; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stFOG1, stFLAME1, stFLAME2}; dieSKCount:= 3;
   dieStone:= 9; dieStStyle:= fastStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetBubble;
   Make:= MakeBubble;
   Move:= MoveStarBubble;
   Aie:= AieSBB;
   Die:= DieStarBubble;
   charge:= 100; weight:= 50;
   inerty:= 32; priority:= -55;
   heatSpeed:= 80;
   refreshSpeed:= 5;
   aieStKinds:= StoneSet{stC35, stFLAME1, stFLAME2}; aieSKCount:= 3;
   aieStone:= 5; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stRE, stFLAME1, stFLAME2}; dieSKCount:= 3;
   dieStone:= 9; dieStStyle:= fastStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetBumper;
   Make:= MakeBumper;
   Move:= MoveBumper;
   Aie:= AieSBB;
   Die:= DieBumper;
   charge:= 30; weight:= 60;
   inerty:= 60; priority:= -54;
   heatSpeed:= 30;
   refreshSpeed:= 100;
   coolSpeed:= 20;
   aieStKinds:= StoneSet{stFLAME1, stFLAME2}; aieSKCount:= 2;
   aieStone:= 6; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stC35}; dieSKCount:= 1;
   dieStone:= 12; dieStStyle:= returnStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetPic;
   Make:= MakePic;
   Move:= MovePic;
   Aie:= AiePic;
   Die:= DieKamikaze;
   charge:= 40; weight:= 120;
   inerty:= 160; priority:= -52;
   dieStKinds:= StoneSet{stFLAME2}; dieSKCount:= 1;
   dieStone:= 31; dieStStyle:= fastStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetTri;
   Make:= MakeTri;
   Move:= MoveTri;
   Aie:= AieTri;
   Die:= DieTri;
   charge:= 8; weight:= 40;
   inerty:= 100; priority:= -54;
   heatSpeed:= 30;
   refreshSpeed:= 100;
   coolSpeed:= 15;
   aieStKinds:= StoneSet{stFLAME2}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stC35}; dieSKCount:= 1;
   dieStone:= 5; dieStStyle:= slowStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetTrefle;
   Make:= MakeTrefle;
   Move:= MoveTrefle;
   Aie:= AieTrefle;
   Die:= DieTrefle;
   charge:= 55; weight:= 20;
   inerty:= 200; priority:= -59;
   heatSpeed:= 100;
   refreshSpeed:= 100;
   coolSpeed:= 10;
   aieStKinds:= StoneSet{stFLAME1}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stCROSS}; dieSKCount:= 1;
   dieStone:= 5; dieStStyle:= slowStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetBig;
   Make:= MakeBig;
   Move:= MoveBig;
   Aie:= AieBig;
   Die:= DieBig;
   charge:= 5; weight:= 80;
   inerty:= 100; priority:= -40;
   heatSpeed:= 100;
   refreshSpeed:= 20;
   coolSpeed:= 10;
   aieStKinds:= StoneSet{stFOG1, stFOG2}; aieSKCount:= 2;
   aieStone:= 4; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{stFOG3}; dieSKCount:= 1;
   dieStone:= FlameMult + 16; dieStStyle:= slowStyle;
   basicType:= Mineral;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetSquare;
   Make:= MakeSquare;
   Move:= MoveBig;
   Aie:= AieSquare;
   Die:= DieSquare;
   charge:= 60; weight:= 30;
   inerty:= 60; priority:= -40;
   heatSpeed:= 20;
   refreshSpeed:= 100;
   coolSpeed:= 60;
   aieStKinds:= StoneSet{stC26}; aieSKCount:= 1;
   aieStone:= 5; aieStStyle:= slowStyle;
   dieStKinds:= StoneSet{}; dieSKCount:= 0;
   dieStone:= FlameMult * 7; dieStStyle:= slowStyle;
   basicType:= Animal;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetFlame;
   Make:= MakeFlame;
   Move:= MoveFlame;
   Aie:= AieAlien0;
   Die:= DieFlame;
   charge:= 50;
   weight:= 30;
   inerty:= 40;
   heatSpeed:= 50;
   refreshSpeed:= 50;
   coolSpeed:= 12;
   aieStKinds:= StoneSet{stFLAME2}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= fastStyle;
   dieStKinds:= StoneSet{stFLAME1}; dieSKCount:= 1;
   dieStone:= 6; dieStStyle:= slowStyle;
   basicType:= Animal;
   priority:= -30;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node);
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Reset:= ResetColor;
   Make:= MakeColor;
   Move:= MoveAlien0;
   Aie:= AieAlien0;
   Die:= DieAlien0;
   charge:= 70;
   weight:= 12;
   inerty:= 24;
   heatSpeed:= 100;
   refreshSpeed:= 100;
   coolSpeed:= 10;
   aieStKinds:= StoneSet{stC35}; aieSKCount:= 1;
   aieStone:= 3; aieStStyle:= gravityStyle;
   dieStKinds:= StoneSet{stC35}; dieSKCount:= 1;
   dieStone:= 6; dieStStyle:= gravityStyle;
   basicType:= Animal;
   priority:= -60;
   toKill:= TRUE
  END;
  AddTail(attrList[ALIEN1], attr^.node)
 END InitParams;

BEGIN

 zeroFire:= 0;
 InitParams;

END ChaosAlien.
