IMPLEMENTATION MODULE ChaosGraphics;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM Memory IMPORT CARD8, CARD16, INT16, CARD32, INT32, AllocMem, FreeMem,
  TAG1, TAG2, TAG3, TAG4, TAG5, TAG6, TAG7, YES, NO, NextTag, TagItem,
  TagItemPtr, StrPtr, SET16;
 FROM Checks IMPORT CheckMem, CheckMemBool, AddTermProc;
 FROM Graphics IMPORT Modes, CopyMode, cmCopy, cmXor, cmTrans, aSIZEX, aSIZEY,
  aTYPE, aCOLOR, atBUFFER, atMASK, CreateArea, DeleteArea, SwitchArea,
  SetArea, SetPalette, SetCopyMode, CopyRect, CopyShadow, WaitTOF, GetBuffer,
  SetBuffer, AreaPtr, noArea, AreaToFront, DrawLine, FillRect,
  FillEllipse, SetPen, SetPat, SetTextSize, SetTextMode, SetTextPos, DrawText,
  TextWidth, GraphicsErr, GetGraphicsErr, cmOr, SetPlanes;
 FROM ChaosBase IMPORT Frac, Period, Obj, ObjPtr, FirstObj, TailObj, NextObj,
  objList, mainPlayer, lasttime, step, water, screenInverted;

 VAR
  buffdx, buffdy, buffpx, buffpy: INT16;
  dbuffdx, dbuffdy, dbuffpx, dbuffpy: INT16;


 PROCEDURE OpenScreen(): BOOLEAN;
  VAR
   nbColor: CARD32;
   err: GraphicsErr;
 BEGIN
  CloseScreen;
  SetOrigin(0, 0);
  IF color THEN
   IF dualpf THEN nbColor:= 256 ELSE nbColor:= 16 END
  ELSE
   nbColor:= 2
  END;
  err:= GetGraphicsErr(); (* kill ev. last error *)
  mainArea:= CreateArea(TAG4(aSIZEX, W(SW), aSIZEY, H(SH), aCOLOR, nbColor, aTYPE, atBUFFER));
  IF mainArea = noArea THEN RETURN FALSE END;
  buffArea:= CreateArea(TAG3(aSIZEX, W(OW), aSIZEY, H(OH), aCOLOR, nbColor));
  IF buffArea = noArea THEN RETURN FALSE END;
  IF dualpf THEN
   dualArea:= CreateArea(TAG3(aSIZEX, W(OW), aSIZEY, H(OH), aCOLOR, nbColor));
   IF dualArea = noArea THEN RETURN FALSE END
  END;
  shapeArea:= CreateArea(TAG3(aSIZEX, W(256), aSIZEY, H(296), aCOLOR, nbColor));
  IF shapeArea = noArea THEN RETURN FALSE END;
  maskArea:= CreateArea(TAG4(aSIZEX, W(256), aSIZEY, H(296), aCOLOR, 2, aTYPE, atMASK));
  IF maskArea = noArea THEN RETURN FALSE END;
  SetArea(mainArea);
  SetBuffer(TRUE, FALSE);
  UpdatePalette;
  AreaToFront;
  RETURN TRUE
 END OpenScreen;

 VAR
  backox, backoy, backaddx, backaddy, backtox, backtoy: INT16;
  stepmod: CARD16;

 PROCEDURE SetBackgroundPos(px, py: INT16);
  CONST
   RW = PW DIV 2;
   RH = PH DIV 2;
  VAR
   maxx, maxy, xstep, ystep: INT16;
 BEGIN
  maxx:= gameWidth - PW;
  maxy:= gameHeight - PH;
  backox:= backpx; backoy:= backpy;
  WITH mainPlayer^ DO
   xstep:= (step + stepmod) DIV 3; ystep:= xstep;
   stepmod:= (step + 2) MOD 3;
   IF dvx > 0 THEN backaddx:= 32 ELSIF dvx < 0 THEN backaddx:= -32 END;
   IF dvy > 0 THEN backaddy:= 32 ELSIF dvy < 0 THEN backaddy:= -32 END;
   backtox:= px + backaddx - RW;
   backtoy:= py + backaddy - RH;
   IF ABS(backtox - backpx) > 100 THEN xstep:= xstep * 2 END;
   IF ABS(backtoy - backpy) > 100 THEN ystep:= ystep * 2 END;
   IF ABS(backtox - backpx) < xstep THEN backpx:= backtox
   ELSIF backpx < backtox THEN INC(backpx, xstep)
   ELSE DEC(backpx, xstep)
   END;
   IF ABS(backtoy - backpy) < ystep THEN backpy:= backtoy
   ELSIF backpy < backtoy THEN INC(backpy, ystep)
   ELSE DEC(backpy, ystep)
   END
  END;
  IF backpx < 0 THEN backpx:= 0 ELSIF backpx > maxx THEN backpx:= maxx END;
  IF backpy < 0 THEN backpy:= 0 ELSIF backpy > maxy THEN backpy:= maxy END;
  IF dualpf THEN
   dualpx:= backpx DIV dualSpeed;
   dualpy:= backpy DIV dualSpeed
  END
 END SetBackgroundPos;

 PROCEDURE DrawBlocks(x, y, w, h: INT16);
  VAR
   bx, by, cx, cy, dx, dy: INT16;
 BEGIN
  dy:= (buffdy + h) MOD (SOH + 1);
  cy:= y + h;
  SetArea(buffArea);
  IF dualpf THEN SetPlanes(15, TRUE) END;
  WHILE cy > y DO
   DEC(cy);
   IF dy = 0 THEN dy:= SOH ELSE DEC(dy) END;
   dx:= (buffdx + w) MOD (SOW + 1);
   cx:= x + w;
   WHILE cx > x DO
    DEC(cx);
    IF dx = 0 THEN dx:= SOW ELSE DEC(dx) END;
    IF (cx < castleWidth) AND (cy < castleHeight) THEN
     bx:= (castle^[cy, cx]);
     by:= (bx DIV 8) * BH;
     bx:= (bx MOD 8) * BW;
     CopyRect(imageArea, W(bx), H(by), W(dx * BW), H(dy * BH), W(BW), H(BH))
    END
   END
  END
 END DrawBlocks;

 PROCEDURE DrawDBlocks(x, y, w, h: INT16);
  VAR
   bx, by, cx, cy, dx, dy: INT16;
 BEGIN
  dy:= (dbuffdy + h) MOD (SOH + 1);
  cy:= y + h;
  SetArea(dualArea);
  SetPlanes(240, TRUE);
  WHILE cy > y DO
   DEC(cy);
   IF dy = 0 THEN dy:= SOH ELSE DEC(dy) END;
   dx:= (dbuffdx + w) MOD (SOW + 1);
   cx:= x + w;
   WHILE cx > x DO
    DEC(cx);
    IF dx = 0 THEN dx:= SOW ELSE DEC(dx) END;
    IF (cx < 64) AND (cy < 64) THEN
     bx:= (dual^[cy, cx]);
     by:= (bx DIV 8) * BH;
     bx:= (bx MOD 8) * BW;
     CopyRect(image2Area, W(bx), H(by), W(dx * BW), H(dy * BH), W(BW), H(BH))
    END
   END
  END
 END DrawDBlocks;

 PROCEDURE RenderBlocks;
  VAR
   px, py, mx, my, w1, w2, h1, h2: INT16;
 BEGIN
  px:= backpx DIV BW; py:= backpy DIV BH;
  mx:= backpx MOD BW; my:= backpy MOD BH;
  WHILE buffpx < px DO
   DrawBlocks(buffpx + SOW + 1, buffpy, 1, SOH + 1); INC(buffpx);
   buffdx:= (buffdx + 1) MOD (SOW + 1)
  END;
  WHILE buffpx > px DO
   IF buffdx <= 0 THEN buffdx:= SOW ELSE DEC(buffdx) END;
   DEC(buffpx); DrawBlocks(buffpx, buffpy, 1, SOH + 1)
  END;
  WHILE buffpy < py DO
   DrawBlocks(buffpx, buffpy + SOH + 1, SOW + 1, 1); INC(buffpy);
   buffdy:= (buffdy + 1) MOD (SOH + 1)
  END;
  WHILE buffpy > py DO
   IF buffdy <= 0 THEN buffdy:= SOH ELSE DEC(buffdy) END;
   DEC(buffpy); DrawBlocks(buffpx, buffpy, SOW + 1, 1)
  END;
 (* Render *)
  px:= buffdx * BW + mx; py:= buffdy * BH + my;
  w1:= OW - px; w2:= PW - w1;
  IF w1 > PW THEN w1:= PW END;
  h1:= OH - py; h2:= PH - h1;
  IF h1 > PH THEN h1:= PH END;
  SetArea(mainArea); SetCopyMode(cmCopy);
  IF dualpf THEN SetPlanes(15, TRUE) END;
  IF h1 > 0 THEN
   IF w1 > 0 THEN CopyRect(buffArea, W(px), H(py), 0, 0, W(w1), H(h1)) END;
   IF w2 > 0 THEN CopyRect(buffArea, 0, H(py), W(w1), 0, W(w2), H(h1)) END
  END;
  IF h2 > 0 THEN
   IF w1 > 0 THEN CopyRect(buffArea, W(px), 0, 0, H(h1), W(w1), H(h2)) END;
   IF w2 > 0 THEN CopyRect(buffArea, 0, 0, H(w1), H(h1), W(w2), H(h2)) END
  END;
  IF dualpf THEN (* Render 2nd playfield *)
   px:= dualpx DIV BW; py:= dualpy DIV BH;
   mx:= dualpx MOD BW; my:= dualpy MOD BH;
   WHILE dbuffpx < px DO
    DrawDBlocks(dbuffpx + SOW + 1, dbuffpy, 1, SOH + 1); INC(dbuffpx);
    dbuffdx:= (dbuffdx + 1) MOD (SOW + 1)
   END;
   WHILE dbuffpx > px DO
    IF dbuffdx <= 0 THEN dbuffdx:= SOW ELSE DEC(dbuffdx) END;
    DEC(dbuffpx); DrawDBlocks(dbuffpx, dbuffpy, 1, SOH + 1)
   END;
   WHILE dbuffpy < py DO
    DrawDBlocks(dbuffpx, dbuffpy + SOH + 1, SOW + 1, 1); INC(dbuffpy);
    dbuffdy:= (dbuffdy + 1) MOD (SOH + 1)
   END;
   WHILE dbuffpy > py DO
    IF dbuffdy <= 0 THEN dbuffdy:= SOH ELSE DEC(dbuffdy) END;
    DEC(dbuffpy); DrawDBlocks(dbuffpx, dbuffpy, SOW + 1, 1)
   END;
  (* Render *)
   px:= dbuffdx * BW + mx; py:= dbuffdy * BH + my;
   w1:= OW - px; w2:= PW - w1;
   IF w1 > PW THEN w1:= PW END;
   h1:= OH - py; h2:= PH - h1;
   IF h1 > PH THEN h1:= PH END;
   SetArea(mainArea); SetCopyMode(cmCopy);
   SetPlanes(240, FALSE);
   IF h1 > 0 THEN
    IF w1 > 0 THEN CopyRect(dualArea, W(px), H(py), 0, 0, W(w1), H(h1)) END;
    IF w2 > 0 THEN CopyRect(dualArea, 0, H(py), W(w1), 0, W(w2), H(h1)) END
   END;
   IF h2 > 0 THEN
    IF w1 > 0 THEN CopyRect(dualArea, W(px), 0, 0, H(h1), W(w1), H(h2)) END;
    IF w2 > 0 THEN CopyRect(dualArea, 0, 0, H(w1), H(h1), W(w2), H(h2)) END
   END;
   SetPlanes(255, TRUE);
   SetCopyMode(cmCopy)
  END
 END RenderBlocks;

 PROCEDURE RenderObjects;
  VAR
   obj, tail: ObjPtr;
   nwdth, px, py: INT16;
 BEGIN
  SetArea(mainArea);
  obj:= FirstObj(objList);
  tail:= TailObj(objList);
  IF mulS <> 1 THEN
   WHILE obj <> tail DO
    WITH obj^ DO
     px:= x DIV Frac; py:= y DIV Frac;
     px:= W(px - backpx);
     py:= H(py - backpy);
     nwdth:= width;
     IF px + nwdth > W(PW) THEN nwdth:= W(PW) - px END;
     IF nwdth > 0 THEN
      CopyShadow(shapeArea, maskArea, posX, posY, px, py, nwdth, height)
     END
    END;
    obj:= NextObj(obj^.objNode)
   END
  ELSE
   WHILE obj <> tail DO
    WITH obj^ DO
     px:= x DIV Frac; py:= y DIV Frac;
     DEC(px, backpx); DEC(py, backpy);
     nwdth:= width;
     IF px + nwdth > PW THEN nwdth:= PW - px END;
     IF nwdth > 0 THEN
      CopyShadow(shapeArea, maskArea, posX, posY, px, py, nwdth, height)
     END
    END;
    obj:= NextObj(obj^.objNode)
   END
  END
 END RenderObjects;

 PROCEDURE DrawBackground;
  VAR
   px, py: INT16;
 BEGIN
  WITH mainPlayer^ DO
   px:= x DIV Frac; INC(px, cx);
   py:= y DIV Frac; INC(py, cy)
  END;
  SetBackgroundPos(px, py);
 (* Draw bufferArea *)
  buffpx:= backpx DIV BW; buffpy:= backpy DIV BH;
  buffdx:= 0; buffdy:= 0;
  DrawBlocks(buffpx, buffpy, SOW + 1, SOH + 1);
  IF dualpf THEN
 (* Draw dualArea *)
   dbuffpx:= dualpx DIV BW; dbuffpy:= dualpy DIV BH;
   dbuffdx:= 0; dbuffdy:= 0;
   DrawDBlocks(dbuffpx, dbuffpy, SOW + 1, SOH + 1)
  END;
  RenderBlocks
 END DrawBackground;

 PROCEDURE MoveBackground(px, py: INT16);
 BEGIN
  IF (screenInverted > 0) AND NOT(color) THEN
   SetArea(mainArea); SetCopyMode(cmXor);
   SetPen(1); FillRect(0, 0, W(PW), H(PH));
   SetCopyMode(cmCopy)
  END;
  RenderObjects;
  SwitchArea;
  SetBackgroundPos(px, py);
  RenderBlocks
 END MoveBackground;

 PROCEDURE UpdateAnim;
 BEGIN
  SetArea(mainArea);
  DrawBackground;
  RenderObjects
 END UpdateAnim;

 PROCEDURE CloseScreen;
 BEGIN
  DeleteArea(maskArea);
  DeleteArea(shapeArea);
  DeleteArea(buffArea);
  DeleteArea(dualArea);
  DeleteArea(mainArea)
 END CloseScreen;

 PROCEDURE WaterPalette(VAR r, g, b: CARD8);
 BEGIN
  IF water THEN
   r:= r DIV 4;
   IF 255 - r < b THEN b:= 255 ELSE INC(b, r) END;
   DEC(g, g DIV 5)
  END
 END WaterPalette;

 PROCEDURE SetRGB(col, r, g, b: CARD8);
 BEGIN
  EXCL(cycling, col);
  WaterPalette(r, g, b);
  WITH palette[col] DO
   red:= r; green:= g; blue:= b
  END
 END SetRGB;

 PROCEDURE CycleRGB(col, spd, r, g, b: CARD8);
 BEGIN
  INCL(cycling, col);
  WaterPalette(r, g, b);
  cycleSpeed[col]:= spd;
  WITH cycle[col] DO
   red:= r; green:= g; blue:= b
  END
 END CycleRGB;

 PROCEDURE DualCycleRGB(col, spd, r, g, b: CARD8);
 BEGIN
  INCL(dualCycling, col);
  WaterPalette(r, g, b);
  dualCycleSpeed[col]:= spd;
  WITH dualCycle[col] DO
   red:= r; green:= g; blue:= b
  END
 END DualCycleRGB;

 PROCEDURE SetTrans(col, trans: CARD8);
 BEGIN
  transparent[col]:= trans
 END SetTrans;

 PROCEDURE CopyToDual;
  VAR
   x, y: INT16;
 BEGIN
 (* castle *)
  FOR y:= 0 TO 63 DO
   FOR x:= 0 TO 63 DO
    dual^[y, x]:= castle^[y, x]
   END
  END;
 (* Palette *)
  dualPalette:= palette;
  dualCycle:= cycle;
  dualCycling:= cycling;
  dualCycleSpeed:= cycleSpeed
 END CopyToDual;

 VAR
  cpPos, dcpPos: ARRAY[0..15] OF CARD16;
  cpReverse, dcpReverse: SET16;

 PROCEDURE AnimCycle(c: CARD16; cycle: Palette; speed, step: CARD16;
                     VAR pos: CARD16; VAR flags: SET16; VAR r, g, b: CARD16);
  VAR
   add: CARD16;
   dred, dgreen, dblue, red, green, blue, p: INT32;
 BEGIN
  WITH cycle DO dred:= red; dgreen:= green; dblue:= blue END;
  DEC(dred, r); DEC(dgreen, g); DEC(dblue, b);
  add:= speed * step; IF add > 4096 THEN add:= 4096 END;
  IF (c IN flags) THEN
   IF add > pos THEN pos:= add - pos; EXCL(flags, c) ELSE DEC(pos, add) END
  ELSE
   INC(pos, add); IF pos >= 4096 THEN INCL(flags, c); pos:= 8192 - pos END
  END;
  red:= r; green:= g; blue:= b;
  p:= pos;
  INC(red, dred * p DIV 4096);
  INC(green, dgreen * p DIV 4096);
  INC(blue, dblue * p DIV 4096);
  r:= red; g:= green; b:= blue
 END AnimCycle;

 PROCEDURE AnimPalette(step: CARD16);
  VAR
   frtPalette: PaletteEntries;
   r1, g1, b1, r2, g2, b2, t1, t2: CARD16;
   c, d: CARD8;
 BEGIN
  SetArea(mainArea);
  IF color THEN
   IF dualpf THEN
    FOR c:= 0 TO 15 DO
     WITH palette[c] DO r1:= red; g1:= green; b1:= blue END;
     IF (c IN cycling) THEN
      AnimCycle(c, cycle[c], cycleSpeed[c], step, cpPos[c], cpReverse, r1, g1, b1)
     END;
     WITH frtPalette[c] DO red:= r1; green:= g1; blue:= b1 END
    END;
    FOR d:= 0 TO 15 DO
     WITH dualPalette[d] DO r2:= red; g2:= green; b2:= blue END;
     IF (d IN dualCycling) THEN
      AnimCycle(d, dualCycle[d], dualCycleSpeed[d], step, dcpPos[d], dcpReverse, r2, g2, b2)
     END;
     FOR c:= 0 TO 15 DO
      WITH frtPalette[c] DO r1:= red; g1:= green; b1:= blue END;
      t1:= transparent[c]; t2:= 255 - t1;
      SetPalette(d * 16 + c, (r1 * t1 + r2 * t2) DIV 255,
                             (g1 * t1 + g2 * t2) DIV 255,
                             (b1 * t1 + b2 * t2) DIV 255);
     END
    END
   ELSE
    FOR c:= 0 TO 15 DO
     WITH palette[c] DO r1:= red; g1:= green; b1:= blue END;
     IF (c IN cycling) THEN
      AnimCycle(c, cycle[c], cycleSpeed[c], step, cpPos[c], cpReverse, r1, g1, b1)
     END;
     SetPalette(c, r1, g1, b1)
    END
   END
  ELSE
   SetPalette(0, 0, 0, 0);
   SetPalette(1, 255, 255, 255)
  END
 END AnimPalette;

 PROCEDURE UpdatePalette;
 BEGIN
  AnimPalette(0)
 END UpdatePalette;

 PROCEDURE ResetCycle(end: BOOLEAN);
  VAR
   r, g, b, c: CARD16;
 BEGIN
  FOR c:= 0 TO 15 DO
   IF end THEN
    cpPos[c]:= 4096; dcpPos[c]:= 4096
   ELSE
    WITH palette[c] DO r:= red; g:= green; b:= blue END;
    AnimCycle(c, cycle[c], cycleSpeed[c], 0, cpPos[c], cpReverse, r, g, b);
    WITH palette[c] DO red:= r; green:= g; blue:= b END;
    WITH dualPalette[c] DO r:= red; g:= green; b:= blue END;
    AnimCycle(c, dualCycle[c], dualCycleSpeed[c], 0, dcpPos[c], dcpReverse, r, g, b);
    WITH dualPalette[c] DO red:= r; green:= g; blue:= b END;
    cpPos[c]:= 0; dcpPos[c]:= 0
   END
  END;
  cycling:= SET16{};
  dualCycling:= SET16{};
  IF end THEN
   cpReverse:= SET16{0..15}; dcpReverse:= SET16{0..15}
  ELSE
   cpReverse:= SET16{}; dcpReverse:= SET16{}
  END
 END ResetCycle;

 PROCEDURE WriteAt(x, y: INT16; t: StrPtr);
 BEGIN
  SetTextPos(x, y);
  DrawText(t)
 END WriteAt;

 PROCEDURE CenterText(x, y, w: INT16; t: StrPtr);
  VAR
   pw: INT16;
 BEGIN
  pw:= TextWidth(t);
  INC(x, (w - pw) DIV 2);
  SetTextPos(x, y);
  DrawText(t)
 END CenterText;

 PROCEDURE WriteShort(z: BOOLEAN; v: CARD16);
  VAR
   q: CARD16;
   d: INT16;
   ch: ARRAY[0..1] OF CHAR;
 BEGIN
  ch[1]:= 0C;
  q:= 1000;
  WHILE q > 0 DO
   d:= v DIV q;
   v:= v MOD q;
   q:= q DIV 10;
   IF q = 0 THEN z:= TRUE END;
   IF (d <> 0) OR z THEN
    ch[0]:= CHR(ORD("0") + d);
    DrawText(ADR(ch));
    z:= TRUE
   END
  END
 END WriteShort;

 PROCEDURE WriteCard(x, y: INT16; v: CARD32);
  VAR
   z: BOOLEAN;
   q: CARD32;
   d: INT16;
   ch: ARRAY[0..1] OF CHAR;
 BEGIN
  SetTextPos(x, y);
  ch[1]:= 0C;
  z:= FALSE;
  IF v >= 10000 THEN
   q:= 1000000000;
   WHILE q >= 10000 DO
    d:= v DIV q;
    v:= v MOD q;
    q:= q DIV 10;
    IF (d <> 0) OR z THEN
     ch[0]:= CHR(ORD("0") + d);
     DrawText(ADR(ch));
     z:= TRUE
    END
   END
  END;
  WriteShort(z, v);
 END WriteCard;

 VAR
  OX, OY: INT16;

 PROCEDURE FastS(s: INT16): INT16;
 BEGIN
  RETURN s
 END FastS;

 PROCEDURE MultS(s: INT16): INT16;
 BEGIN
  RETURN s * mulS
 END MultS;

 PROCEDURE FastX(x: INT16): INT16;
 BEGIN
  RETURN x + OX
 END FastX;

 PROCEDURE FastY(y: INT16): INT16;
 BEGIN
  RETURN y + OY
 END FastY;

 PROCEDURE MultX(x: INT16): INT16;
 BEGIN
  RETURN x * mulS + OX
 END MultX;

 PROCEDURE MultY(y: INT16): INT16;
 BEGIN
  RETURN y * mulS + OY
 END MultY;

 PROCEDURE SetOrigin(ox, oy: INT16);
 BEGIN
  OX:= ox * mulS;
  OY:= oy * mulS;
  IF mulS = 1 THEN
   W:= FastS; H:= FastS;
   IF OX = 0 THEN X:= FastS ELSE X:= FastX END;
   IF OY = 0 THEN Y:= FastS ELSE Y:= FastY END
  ELSE
   W:= MultS; H:= MultS;
   IF OX = 0 THEN X:= MultS ELSE X:= MultX END;
   IF OY = 0 THEN Y:= MultS ELSE Y:= MultY END
  END
 END SetOrigin;

 PROCEDURE Close;
 BEGIN
  CloseScreen;
  FreeMem(dual);
  FreeMem(castle)
 END Close;

 PROCEDURE InitColors;
  VAR
   c, f: CARD16;
 BEGIN
  FOR c:= 0 TO 15 DO
   SetRGB(c, 0, 0, 0);
   cycle[c]:= palette[c];
   cpPos[c]:= 0; dcpPos[c]:= 0;
   cpReverse:= SET16{};
   dcpReverse:= SET16{}
  END;
  CopyToDual;
  SetRGB(0, 0, 0, 0);
  SetRGB(1, 127, 127, 127);
  FOR c:= 8 TO 15 DO
   f:= c * 17; SetRGB(c, f, f, f)
  END;
  SetRGB(2, 255, 255, 255); CycleRGB(2, 40, 127, 255, 255);
  SetRGB(3, 255, 255, 0); CycleRGB(3, 200, 255, 200, 0);
  SetRGB(4, 255, 0, 0);
  SetRGB(5, 0, 255, 0);
  SetRGB(6, 0, 0, 255);
  SetRGB(7, 210, 140, 0);
  FOR c:= 0 TO 15 DO SetTrans(c, 255) END
 END InitColors;

BEGIN

 mainArea:= noArea; buffArea:= noArea;
 shapeArea:= noArea; maskArea:= noArea;
 imageArea:= noArea; image2Area:= noArea;
 dualArea:= noArea;
 backpx:= 0; backpy:= 0;
 dualpx:= 0; dualpy:= 0;
 backaddx:= 0; backaddy:= 0;
 stepmod:= 0; dualSpeed:= 3;
 dualpf:= FALSE;
 castle:= NIL; dual:= NIL;
 AddTermProc(Close);
 castle:= AllocMem(SIZE(Castle));
 CheckMem(castle);
 dual:= AllocMem(SIZE(Dual));
 CheckMem(dual);
 mulS:= 1;
 SetOrigin(0, 0);
 InitColors;

END ChaosGraphics.
