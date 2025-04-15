IMPLEMENTATION MODULE ChaosImages;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM Memory IMPORT CARD8, CARD16, INT16, CARD32, INT32, TAG1, TAG2, TAG3,
  TAG4, TAG5, TAG6, TAG7, YES, NO, StrPtr, StrLength, AllocMem, FreeMem,
  GetBitField, SetBitField, ADS;
 FROM Languages IMPORT ADL;
 FROM Trigo IMPORT RND;
 FROM Files IMPORT FilePtr, OpenFile, ReadFileBytes, CloseFile, FileLength,
  noFile, AccessFlags, AccessFlagSet, FileErrorMsg;
 FROM Checks IMPORT Check, CheckMem, CheckMemBool, Ask;
 FROM Input IMPORT Event, GetEvent;
 FROM Dialogs IMPORT GadgetPtr, DialogOk, DialogNoMem, dFLAGS, dRFLAGS, dfSELECT,
  dFILL, dTEXT, dDialog, dProgress, AllocGadget, AddNewGadget, RefreshGadget,
  CreateGadget, ModifyGadget, DeepFreeGadget, noGadget, dfCLOSE, dfBORDER,
  dfJUSTIFY;
 FROM Graphics IMPORT aSIZEX, aSIZEY, aCOLOR, CreateArea, SetArea, SetPalette,
  ScaleRect, DeleteArea, AreaPtr, SetCopyMode, cmCopy, CopyRect, DrawImage,
  cmTrans, cmXor, CopyMode, noArea, DrawShadow, SetPen, SetPat, DrawPixel,
  FillRect, FillEllipse, OpenPoly, AddLine, FillPoly, SetBPen, SetTextSize,
  SetTextPos, TextWidth, DrawText, Image, ImagePtr;
 FROM ChaosBase IMPORT Zone, zone, level, file, d, water, difficulty;
 FROM ChaosGraphics IMPORT SetRGB, CycleRGB, Palette, imageArea, NbBackground,
  NbWall, mulS, color, palette, BW, BH, UpdatePalette, SetOrigin,
  X, Y, W, H, shapeArea, maskArea, mainArea, image2Area, dualpf;

 CONST
  ImagesFile = "Images";
  ObjectsFile = "Objects";
  OpenIErrMsg = "Cannot open data file: Images";
  OpenOErrMsg = "Cannot open data file: Objects";
  ReadIErrMsg = "Error reading file: Images";
  ReadOErrMsg = "Error reading file: Objects";


 PROCEDURE SetMetalPalette;
 BEGIN
  SetRGB(8, 238, 87, 0);
  SetRGB(9, 35, 35, 35);
  SetRGB(10, 92, 92, 92);
  SetRGB(11, 153, 153, 153);
  SetRGB(12, 68, 102, 204);
  SetRGB(13, 221, 85, 85);
  SetRGB(14, 51, 221, 68);
  SetRGB(15, 238, 238, 238)
 END SetMetalPalette;

 PROCEDURE SetGhostPalette;
 BEGIN
  SetMetalPalette;
  CycleRGB(9, 3, 0, 0, 0);
  CycleRGB(10, 3, 0, 0, 0);
  CycleRGB(11, 3, 0, 0, 0)
 END SetGhostPalette;

 PROCEDURE SetOrPalette;
 BEGIN
  SetRGB(8, 255, 68, 0);
  SetRGB(9, 68, 21, 0);
  SetRGB(10, 153, 40, 0);
  SetRGB(11, 205, 80, 0);
  SetRGB(12, 238, 187, 0);
  SetRGB(13, 255, 153, 0);
  SetRGB(14, 255, 102, 0);
  SetRGB(15, 255, 255, 255)
 END SetOrPalette;

 PROCEDURE SetBluePalette;
 BEGIN
  SetRGB(8, 0, 68, 255);
  SetRGB(9, 0, 34, 85);
  SetRGB(10, 0, 51, 170);
  SetRGB(11, 0, 85, 221);
  SetRGB(12, 0, 187, 238);
  SetRGB(13, 0, 153, 255);
  SetRGB(14, 0, 102, 255);
  SetRGB(15, 255, 255, 255)
 END SetBluePalette;

 PROCEDURE SetForestPalette;
 BEGIN
  SetRGB(8, 6, 15, 0);
  SetRGB(9, 70, 34, 0);
  SetRGB(10, 137, 68, 0);
  SetRGB(11, 187, 136, 15);
  SetRGB(12, 0, 55, 0);
  SetRGB(13, 0, 110, 0);
  SetRGB(14, 75, 180, 0);
  SetRGB(15, 255, 255, 255)
 END SetForestPalette;

 PROCEDURE SetBagleyPalette;
 BEGIN
  SetRGB(8, 15, 6, 0);
  SetRGB(9, 80, 34, 34);
  SetRGB(10, 140, 68, 35);
  SetRGB(11, 187, 136, 50);
  SetRGB(12, 85, 51, 0);
  SetRGB(13, 119, 85, 0);
  SetRGB(14, 187, 119, 0);
  SetRGB(15, 255, 255, 255)
 END SetBagleyPalette;

 PROCEDURE SetGraveyardPalette;
 BEGIN
  SetRGB(8, 3, 3, 3);
  SetRGB(9, 68, 68, 68);
  SetRGB(10, 102, 102, 102);
  SetRGB(11, 136, 136, 136);
  SetRGB(12, 51, 51, 51);
  SetRGB(13, 119, 119, 119);
  SetRGB(14, 187, 187, 187);
  SetRGB(15, 255, 255, 255)
 END SetGraveyardPalette;

 PROCEDURE SetWinterPalette;
 BEGIN
  SetRGB(8, 68, 85, 102);
  SetRGB(9, 119, 68, 34);
  SetRGB(10, 204, 221, 238);
  SetRGB(11, 230, 238, 255);
  SetRGB(12, 35, 35, 35);
  SetRGB(13, 193, 210, 252);
  SetRGB(14, 170, 221, 255);
  SetRGB(15, 255, 255, 255);
 END SetWinterPalette;

 PROCEDURE SetJunglePalette;
 BEGIN
  SetForestPalette;
  SetRGB(14, 0, 68, 204)
 END SetJunglePalette;

 VAR
  fadeMn1, fadeMn2, fadeMx1, fadeMx2, lastCastleLevel: CARD8;

 PROCEDURE DoFade(fadeMin, fadeMax: CARD8; cycle: BOOLEAN);
  VAR
   sr, sg, sb, dr, dg, db: CARD16;
   c, r, g, b: CARD16;
 BEGIN
  sr:= 0; sg:= 0; sb:= 0;
  IF fadeMin = 0 THEN
   sr:= 255
  ELSIF fadeMin = 1 THEN
   sg:= 255
  ELSIF fadeMin = 2 THEN
   sb:= 255
  END;
  dr:= 255; dg:= 255; db:= 255;
  IF fadeMax = 0 THEN
   dr:= 0
  ELSIF fadeMax = 1 THEN
   dg:= 0
  ELSIF fadeMax = 2 THEN
   db:= 0
  END;
  FOR c:= 0 TO 5 DO
   r:= (sr * (5 - c) + dr * c) DIV 7;
   g:= (sg * (5 - c) + dg * c) DIV 7;
   b:= (sb * (5 - c) + db * c) DIV 7;
   IF cycle THEN
    CycleRGB(c + 9, 2, r, g, b)
   ELSE
    SetRGB(c + 9, r, g, b)
   END
  END
 END DoFade;

 PROCEDURE SetFadePalette;
 BEGIN
  IF level[Castle] <> lastCastleLevel THEN
   fadeMn1:= RND() MOD 4; fadeMn2:= RND() MOD 4;
   fadeMx1:= RND() MOD 4; fadeMx2:= RND() MOD 4
  END;
  DoFade(fadeMn1, fadeMx1, FALSE);
  IF difficulty >= 2 THEN
   DoFade(fadeMn2, fadeMx2, TRUE)
  END;
  SetRGB(8, 68, 68, 68);
  SetRGB(15, 255, 255, 255)
 END SetFadePalette;

 PROCEDURE SetRGBIcePalette;
 BEGIN
  SetRGB(8, 51, 51, 85);
  SetRGB(9, 102, 102, 119);
  SetRGB(10, 153, 153, 187);
  SetRGB(11, 204, 204, 255);
  SetRGB(12, 0, 70, 105);
  SetRGB(13, 0, 90, 90);
  SetRGB(14, 0, 100, 115);
  SetRGB(15, 204, 255, 255);
 END SetRGBIcePalette;

 PROCEDURE SetAnimPalette;
 BEGIN
  SetRGB(8, 0, 255, 153);
  SetRGB(9, 51, 51, 51);
  SetRGB(10, 102, 102, 102);
  SetRGB(11, 153, 153, 153);
  SetRGB(12, 0, 255, 0);
  SetRGB(13, 204, 0, 0);
  SetRGB(14, 0, 0, 153);
  SetRGB(15, 255, 255, 255);
 END SetAnimPalette;

 PROCEDURE SetAnimatedPalette;
 BEGIN
  SetRGB(8, 0, 0, 255); CycleRGB(8, 4, 0, 255, 0);
  SetRGB(9, 51, 51, 51);
  SetRGB(10, 102, 102, 102);
  SetRGB(11, 153, 153, 153);
  SetRGB(12, 0, 0, 0); CycleRGB(12, 4, 0, 255, 0);
  SetRGB(13, 255, 0, 0); CycleRGB(13, 17, 0, 68, 0);
  SetRGB(14, 0, 0, 255); CycleRGB(14, 4, 0, 0, 0);
  SetRGB(15, 255, 255, 255);
 END SetAnimatedPalette;

 PROCEDURE SetAnimatedPalette2;
 BEGIN
  SetRGB(8, 255, 0, 0); CycleRGB(8, 6, 0, 255, 0);
  SetRGB(9, 51, 51, 51);
  SetRGB(10, 102, 102, 102);
  SetRGB(11, 153, 153, 153);
  SetRGB(12, 255, 0, 0); CycleRGB(12, 6, 0, 0, 0);
  SetRGB(13, 70, 70, 0); CycleRGB(13, 13, 0, 180, 180);
  SetRGB(14, 0, 0, 0); CycleRGB(14, 6, 0, 255, 0);
  SetRGB(15, 255, 255, 255);
 END SetAnimatedPalette2;

 PROCEDURE SetAnimatedPalette3;
 BEGIN
  SetRGB(8, 180, 180, 180); CycleRGB(8, 12, 90, 90, 90);
  SetRGB(9, 51, 51, 51);
  SetRGB(10, 102, 102, 102);
  SetRGB(11, 153, 153, 153);
  SetRGB(12, 180, 180, 180); CycleRGB(12, 6, 0, 0, 0);
  SetRGB(13, 240, 240, 240); CycleRGB(13, 13, 0, 0, 0);
  SetRGB(14, 0, 0, 0); CycleRGB(14, 6, 180, 180, 180);
  SetRGB(15, 255, 255, 255);
 END SetAnimatedPalette3;

 PROCEDURE SetFactoryPalette;
 BEGIN
  SetMetalPalette;
  CycleRGB(8, 13, 0, 0, 0);
  CycleRGB(12, 7, 0, 0, 0);
  CycleRGB(13, 24, 0, 0, 0);
  CycleRGB(14, 19, 0, 0, 0)
 END SetFactoryPalette;

 PROCEDURE SetDarkPalette;
 BEGIN
  SetRGB(8, 119, 44, 0);
  SetRGB(9, 18, 18, 18);
  SetRGB(10, 46, 46, 46);
  SetRGB(11, 77, 77, 77);
  SetRGB(12, 34, 51, 102);
  SetRGB(13, 111, 43, 43);
  SetRGB(14, 26, 111, 34);
  SetRGB(15, 119, 119, 119)
 END SetDarkPalette;

 PROCEDURE SetDarkFactoryPalette;
 BEGIN
  SetDarkPalette;
  CycleRGB(8, 13, 0, 0, 0);
  CycleRGB(12, 7, 0, 0, 0);
  CycleRGB(13, 24, 0, 0, 0);
  CycleRGB(14, 19, 0, 0, 0)
 END SetDarkFactoryPalette;

 PROCEDURE InitImages(): BOOLEAN;
  CONST
   BlockSize = 512; (* BW * BH DIV 2 *)
   DstSize = 10368;
  TYPE
   Data = POINTER TO ARRAY[0..DstSize-1] OF CARD8;
  VAR
   progress: GadgetPtr;
   block, dest: Data;
   bRead: INT16;
   nbcolors, c, pc: CARD16;
   px, py: INT16;
   fill, boost: CARD16;
   oldWater: BOOLEAN;

  PROCEDURE DrawNextBlock;
   VAR
    image: Image;
    e: Event;
    p: INT32;
    x, y, z, dx, dy: INT16;
    v: CARD8;
    patCnt, val, pix, add: CARD16;
  BEGIN
   IF color THEN
    bRead:= ReadFileBytes(file, block, BlockSize);
    IF bRead <> BlockSize THEN Check(TRUE, ADL(ReadIErrMsg), FileErrorMsg()) END;
    IF dualpf THEN
      (* 4bit -> 8 bit *)
     FOR z:= BlockSize - 1 TO 0 BY -1 DO
      v:= block^[z];
      block^[z * 2 + 1]:= v MOD 16;
      block^[z * 2]:= v DIV 16
     END;
     y:= 8
    ELSE
     y:= 4
    END;
    image.data:= block;
    image.bitPerPix:= y;
    image.bytePerRow:= y * 4;
    image.width:= BW; image.height:= BH;
    image.zw:= 1; image.zh:= 1;
    IF mulS > 1 THEN
     SetArea(shapeArea);
     DrawImage(ADR(image), 0, 0, 0, 0, BW, BH);
     SetArea(imageArea);
     ScaleRect(shapeArea, 0, 0, BW, BH,
               W(px), H(py), W(px + BW), H(py + BH))
    ELSE
     SetArea(imageArea);
     DrawImage(ADR(image), 0, 0, px, py, 32, 32)
    END;
    IF dualpf THEN
     FOR z:= 0 TO BlockSize * 2 - 1 DO
      block^[z]:= block^[z] * 16
     END;
     IF mulS > 1 THEN
      SetArea(shapeArea);
      DrawImage(ADR(image), 0, 0, 0, 0, BW, BH);
      SetArea(image2Area);
      ScaleRect(shapeArea, 0, 0, BW, BH,
                W(px), H(py), W(px + BW), H(py + BH))
     ELSE
      SetArea(image2Area);
      DrawImage(ADR(image), 0, 0, px, py, 32, 32)
     END
    END
   ELSE
    SetPen(1); patCnt:= 256; p:= 0;
    bRead:= ReadFileBytes(file, block, BlockSize);
    IF bRead <> BlockSize THEN Check(TRUE, ADL(ReadIErrMsg), FileErrorMsg()) END;
    FOR y:= 0 TO BH - 1 DO
     FOR dy:= 0 TO mulS - 1 DO
      z:= y * BW;
      FOR x:= 0 TO BW - 1 DO
       pix:= GetBitField(ADR(block^[z DIV 2]), (z MOD 2) * 4, 4);
       FOR dx:= 0 TO mulS - 1 DO
        WITH palette[pix] DO
         add:= red; INC(add, green); INC(add, blue)
        END;
        INC(patCnt, add * boost DIV 8);
        IF patCnt >= 512 THEN
         IF patCnt >= 1024 THEN patCnt:= 511 ELSE DEC(patCnt, 512) END;
         val:= 1
        ELSE
         val:= 0
        END;
        pc:= p DIV 8;
        SetBitField(ADR(dest^[pc]), (p MOD 8), 1, val);
        INC(p)
       END;
       INC(z)
      END
     END
    END;
    SetArea(imageArea);
    image.data:= dest;
    image.bitPerPix:= 1;
    image.bytePerRow:= mulS * 4;
    image.width:= BW * mulS;
    image.height:= BH * mulS;
    image.zw:= 1; image.zh:= 1;
    DrawImage(ADR(image), 0, 0, px * mulS, py * mulS, BW * mulS, BH * mulS)
   END;
   INC(fill, 1023);
   IF progress <> noGadget THEN ModifyGadget(progress, TAG1(dFILL, fill)) END;
   GetEvent(e);
   INC(px, BW);
   IF px >= 256 THEN px:= 0; INC(py, BH) END
  END DrawNextBlock;

  CONST
   DTitle = "Loading backgrounds";
  VAR
   dTitle: ADDRESS;

 BEGIN
  oldWater:= water; water:= FALSE;
  DeleteArea(image2Area);
  DeleteArea(imageArea);
  IF color THEN
   IF dualpf THEN nbcolors:= 256 ELSE nbcolors:= 16 END
  ELSE
   nbcolors:= 2
  END;
  imageArea:= CreateArea(TAG3(aSIZEX, W(256), aSIZEY, H(256), aCOLOR, nbcolors));
  IF imageArea = noArea THEN RETURN FALSE END;
  IF dualpf THEN
   image2Area:= CreateArea(TAG3(aSIZEX, W(256), aSIZEY, H(256), aCOLOR, nbcolors));
   IF image2Area = noArea THEN DeleteArea(imageArea); RETURN FALSE END
  END;
  IF dualpf THEN
   block:= AllocMem(BlockSize * 2 * SIZE(CARD8))
  ELSE
   block:= AllocMem(BlockSize * SIZE(CARD8))
  END;
  IF block = NIL THEN RETURN FALSE END;
  IF mulS > 1 THEN
   dest:= AllocMem(mulS * mulS * 128 * SIZE(CARD8))
  ELSE
   dest:= block
  END;
  IF dest = NIL THEN FreeMem(block); RETURN FALSE END;
  px:= 0; py:= 0; fill:= 63;
  file:= OpenFile(ADS(ImagesFile), AccessFlagSet{accessRead});
  Check(file = noFile, ADL(OpenIErrMsg), FileErrorMsg());
  dTitle:= ADL(DTitle);
  d:= CreateGadget(dDialog, TAG2(dTEXT, dTitle, dRFLAGS, dfCLOSE));
  progress:= noGadget;
  IF d <> noGadget THEN
   progress:= AddNewGadget(d, dProgress, TAG3(dRFLAGS, dfBORDER, dTEXT, ADS(""), dFLAGS, dfJUSTIFY));
   IF (progress = noGadget) OR (RefreshGadget(d) <> DialogOk) THEN DeepFreeGadget(d) END
  END;
  FOR c:= 1 TO NbBackground DO
   IF (c = 1) OR (c = 22) THEN boost:= 8 ELSIF c = 16 THEN boost:= 5 END;
   IF (c = 1) OR (c = 22) OR (c = 24) THEN
    SetMetalPalette
   ELSIF c = 15 THEN
    SetForestPalette
   ELSIF (c = 18) OR (c = 20) THEN
    SetRGBIcePalette
   ELSIF (c = 19) OR (c = 21) THEN
    SetAnimPalette
   ELSIF c = 23 THEN
    SetOrPalette
   END;
   SetArea(imageArea);
   DrawNextBlock
  END;
  FOR c:= 1 TO NbWall DO
   IF (c = 1) OR (c = 32) THEN boost:= 8 ELSIF c = 25 THEN boost:= 12 END;
   IF c = 1 THEN
    SetMetalPalette
   ELSIF c = 14 THEN
    SetFadePalette
   ELSIF c = 17 THEN
    SetOrPalette
   ELSIF c = 25 THEN
    SetForestPalette
   ELSIF c = 36 THEN
    SetAnimPalette
   ELSIF c = 39 THEN
    SetRGBIcePalette
   END;
   SetArea(imageArea);
   DrawNextBlock
  END;
  SetMetalPalette;
  SetArea(shapeArea); SetPen(0);
  FillRect(0, 0, W(256), H(256));
  DeepFreeGadget(d);
  IF dest <> block THEN FreeMem(dest) END;
  FreeMem(block);
  CloseFile(file);
(*  SetArea(mainArea); CopyRect(imageArea, 0, 0, 0, H(-16), W(256), H(256)); *)
  water:= oldWater;
  InitPalette;
  RETURN TRUE
 END InitImages;

 PROCEDURE RenderObjects;
  VAR
   progress: GadgetPtr;
   size, total: CARD32;
   bRead: INT32;
   tofill: CARD16;
   highx, highy: BOOLEAN;

  PROCEDURE GetChar(VAR ch: CHAR);
   VAR
    fill: CARD32;
  BEGIN
   bRead:= ReadFileBytes(file, ADR(ch), 1);
   IF bRead <> 1 THEN Check(TRUE, ADL(ReadOErrMsg), FileErrorMsg()) END;
   DEC(size); DEC(tofill);
   IF tofill = 0 THEN
    fill:= ((total - size) * 257 DIV total) * 255;
    IF progress <> noGadget THEN ModifyGadget(progress, TAG1(dFILL, fill)) END;
    tofill:= 200
   END
  END GetChar;

  PROCEDURE GetVal(VAR val: INT16);
   VAR
    ch: CHAR;
  BEGIN
   GetChar(ch);
   val:= ORD(ch)
  END GetVal;

  PROCEDURE GetCoords(VAR x, y: INT16);
  BEGIN
   GetVal(x); GetVal(y);
   IF highx AND (x < 64) THEN INC(x, 256) END;
   IF highy AND (y < 64) THEN INC(y, 256) END
  END GetCoords;

  CONST
   DTitle = "Loading objects";
  VAR
   dTitle: ADDRESS;
   xvect, yvect: ARRAY[0..19] OF INT16;
   xdec, ydec: ARRAY[0..19] OF BOOLEAN;
   cnt, c: CARD16;
   lightColor, darkColor, bpen: INT16;
   x1, y1, x2, y2, sz, w, col, p, pat, msk: INT16;
   sx, sy, ex, ey: INT16;
   str: ARRAY[0..4] OF CHAR;
   ch: CHAR;
   cm: CopyMode;
   triColor: BOOLEAN;

  PROCEDURE XFillRect(sx, sy, ex, ey: INT16);
   VAR
    i: INT16;
  BEGIN
   SetCopyMode(cmCopy);
   FOR i:= 0 TO mulS - 1 DO
    SetPat((i * 4 + mulS DIV 2) DIV mulS);
    SetBPen(lightColor);
    FillRect(sx, sy, ex, ey);
    INC(sx); INC(sy);
    SetBPen(darkColor);
    FillRect(sx, sy, ex, ey);
    DEC(ex); DEC(ey)
   END;
   SetPat(pat); SetBPen(bpen);
   FillRect(sx, sy, ex, ey);
   triColor:= FALSE;
   SetCopyMode(cm)
  END XFillRect;

  PROCEDURE XFillEllipse(sx, sy, ex, ey: INT16);
   VAR
    i: INT16;
  BEGIN
   SetCopyMode(cmCopy);
   FOR i:= 0 TO mulS - 1 DO
    SetPat((i * 4 + mulS DIV 2) DIV mulS);
    SetBPen(lightColor);
    FillEllipse(sx, sy, ex, ey);
    INC(sx); INC(sy);
    SetBPen(darkColor);
    FillEllipse(sx, sy, ex, ey);
    DEC(ex); DEC(ey)
   END;
   SetPat(pat); SetBPen(bpen);
   FillEllipse(sx, sy, ex, ey);
   triColor:= FALSE;
   SetCopyMode(cm)
  END XFillEllipse;

  PROCEDURE XFillPoly;
   VAR
    i: INT16;
    c: CARD16;
  BEGIN
   SetArea(shapeArea);
   SetCopyMode(cmCopy);
   FOR i:= 0 TO mulS - 1 DO
    SetPat((i * 4 + mulS DIV 2) DIV mulS);
    SetBPen(lightColor);
    FOR c:= 0 TO cnt - 1 DO
     IF c > 0 THEN
      AddLine(xvect[c], yvect[c])
     ELSE
      OpenPoly(xvect[0], yvect[0])
     END;
     IF NOT(xdec[c]) THEN INC(xvect[c]) END;
     IF NOT(ydec[c]) THEN INC(yvect[c]) END
    END;
    FillPoly;
    SetBPen(darkColor);
    FOR c:= 0 TO cnt - 1 DO
     IF c > 0 THEN
      AddLine(xvect[c], yvect[c])
     ELSE
      OpenPoly(xvect[0], yvect[0])
     END;
     IF xdec[c] THEN DEC(xvect[c]) END;
     IF ydec[c] THEN DEC(yvect[c]) END
    END;
    FillPoly
   END;
   SetPat(pat); SetBPen(bpen);
   FOR c:= 0 TO cnt - 1 DO
    IF c > 0 THEN
     AddLine(xvect[c], yvect[c])
    ELSE
     OpenPoly(xvect[0], yvect[0])
    END
   END;
   FillPoly;
   cnt:= 0;
   triColor:= FALSE;
   SetCopyMode(cm)
  END XFillPoly;

 BEGIN
  cnt:= 0; triColor:= FALSE;
  pat:= 4; bpen:= 0;
  SetOrigin(0, 0);
  SetArea(maskArea); SetPen(1);
  SetArea(shapeArea); SetPen(1);
  file:= OpenFile(ADS(ObjectsFile), AccessFlagSet{accessRead});
  Check(file = noFile, ADL(OpenOErrMsg), FileErrorMsg());
  dTitle:= ADL(DTitle);
  d:= CreateGadget(dDialog, TAG2(dTEXT, dTitle, dRFLAGS, dfCLOSE));
  progress:= noGadget;
  IF d <> noGadget THEN
   progress:= AddNewGadget(d, dProgress, TAG3(dRFLAGS, dfBORDER, dTEXT, ADS(""), dFLAGS, dfJUSTIFY));
   IF (progress = noGadget) OR (RefreshGadget(d) <> DialogOk) THEN DeepFreeGadget(d) END
  END;
  size:= FileLength(file); tofill:= 200;
  total:= size;
  WHILE size > 0 DO
   GetChar(ch);
   IF ch >= 200C THEN highy:= TRUE; DEC(ch, 128) ELSE highy:= FALSE END;
   IF ch >= 140C THEN highx:= TRUE; DEC(ch, 32) ELSE highx:= FALSE END;
   CASE ch OF
   "C":
    GetVal(col); GetVal(p); GetVal(msk);
    SetArea(maskArea); SetPat(msk);
    SetArea(shapeArea);
    IF color THEN SetPen(col) ELSE SetPat(p) END
   |"P":
    GetVal(pat); GetVal(bpen);
    SetArea(shapeArea);
    IF color THEN SetPat(pat); SetBPen(bpen) END
   |"X":
    GetVal(lightColor); GetVal(darkColor);
    triColor:= color;
   |"M":
    GetVal(msk);
    IF msk = 0 THEN cm:= cmCopy
    ELSIF msk = 1 THEN cm:= cmTrans
    ELSE cm:= cmXor
    END;
    SetArea(maskArea);
    SetCopyMode(cm);
    SetArea(shapeArea);
    SetCopyMode(cm)
   |"R":
    GetCoords(x1, y1);
    GetCoords(x2, y2);
    sx:= X(x1); sy:= Y(y1);
    ex:= X(x2); ey:= Y(y2);
    SetArea(maskArea); FillRect(sx, sy, ex, ey);
    SetArea(shapeArea);
    IF triColor THEN
     XFillRect(sx, sy, ex, ey)
    ELSE
     FillRect(sx, sy, ex, ey)
    END
   |"E":
    GetCoords(x1, y1);
    GetCoords(x2, y2);
    sx:= X(x1); sy:= Y(y1);
    ex:= X(x2); ey:= Y(y2);
    SetArea(maskArea); FillEllipse(sx, sy, ex, ey);
    SetArea(shapeArea);
    IF triColor THEN
     XFillEllipse(sx, sy, ex, ey)
    ELSE
     FillEllipse(sx, sy, ex, ey)
    END
   |"T":
    GetCoords(x1, y1);
    GetVal(w); GetVal(sz);
    GetVal(col);
    c:= 0;
    REPEAT
     GetChar(ch); str[c]:= ch;
     INC(c)
    UNTIL (c > 4) OR (ch = '"');
    DEC(c); str[c]:= 0C;
    SetArea(shapeArea);
    SetTextSize(H(sz));
    x1:= X(x1) + (W(w - 1) - TextWidth(ADR(str))) DIV 2;
    y1:= Y(y1);
    SetTextPos(x1, y1);
    IF NOT(color) THEN SetPen(col) END;
    DrawText(ADR(str));
    IF NOT(color) THEN SetPen(1) END;
    SetArea(maskArea);
    SetTextSize(H(sz));
    SetTextPos(x1, y1);
    DrawText(ADR(str))
   |"L":
    GetCoords(x1, y1);
    GetVal(msk);
    x1:= X(x1); IF ODD(msk) THEN DEC(x1) END;
    y1:= Y(y1); IF msk >= 2 THEN DEC(y1) END;
    SetArea(maskArea);
    IF cnt > 0 THEN AddLine(x1, y1) ELSE OpenPoly(x1, y1) END;
    xdec[cnt]:= ODD(msk);
    ydec[cnt]:= (msk >= 2);
    xvect[cnt]:= x1; yvect[cnt]:= y1; INC(cnt)
   |"F":
    SetArea(maskArea); FillPoly;
    IF triColor THEN
     XFillPoly
    ELSE
     SetArea(shapeArea);
     FOR c:= 0 TO cnt - 1 DO
      IF c > 0 THEN
       AddLine(xvect[c], yvect[c])
      ELSE
       OpenPoly(xvect[0], yvect[0])
      END
     END;
     FillPoly;
     cnt:= 0
    END
   ELSE
   END
  END;
  CloseFile(file);
  SetArea(maskArea); SetPat(4);
  SetBPen(0); SetPen(1); SetCopyMode(cmXor);
  FillRect(0, 0, W(256), H(256));
  SetArea(shapeArea); SetPat(4);
  SetBPen(0); SetPen(0); SetCopyMode(cmTrans);
  DrawShadow(maskArea, 0, 0, 0, 0, W(256), H(256));
  SetArea(maskArea);
  FillRect(0, 0, W(256), H(256));
  DeepFreeGadget(d)
 END RenderObjects;

 PROCEDURE InitPalette;
  VAR
   rnd: CARD16;
 BEGIN
  IF color THEN
   SetRGB(0, 0, 0, 0);
   SetRGB(1, 127, 127, 127);
  ELSE
   SetRGB(0, 0, 0, 0);
   SetRGB(1, 255, 255, 255)
  END;
  SetRGB(2, 255, 255, 255); CycleRGB(2, 40, 127, 255, 255);
  SetRGB(3, 255, 255, 0); CycleRGB(3, 200, 255, 200, 0);
  SetRGB(4, 255, 0, 0);
  SetRGB(5, 0, 255, 0);
  SetRGB(6, 0, 0, 255);
  SetRGB(7, 216, 152, 0);
  IF zone = Chaos THEN
   SetMetalPalette
  ELSIF zone = Castle THEN
   CASE level[Castle] OF
     1, 8, 13: SetRGBIcePalette
    |2: SetFadePalette
    |3, 4, 11, 14, 17, 18: SetForestPalette
    |5: SetAnimPalette
    |6, 10, 15, 19: SetMetalPalette
    |7: SetAnimatedPalette
    |9: SetFactoryPalette
   |12, 16: SetAnimatedPalette2
   |20: SetJunglePalette
   END;
   lastCastleLevel:= level[Castle];
  ELSIF zone = Family THEN
   CASE level[Family] OF
     1, 2, 4, 6, 8, 9, 10: SetOrPalette
    |3: SetMetalPalette
    |5: SetBluePalette
    |7: SetFactoryPalette
   END
  ELSE
   IF level[Special] = 24 THEN
    SetMetalPalette
   ELSIF level[Special] MOD 8 = 0 THEN
    SetWinterPalette
   ELSIF level[Special] MOD 4 = 0 THEN
    SetGraveyardPalette
   ELSIF level[Special] MOD 2 = 0 THEN
    SetBagleyPalette
   ELSE
    rnd:= RND() MOD 3;
    IF rnd = 0 THEN
     SetAnimatedPalette
    ELSIF rnd = 1 THEN
     SetAnimatedPalette2
    ELSE
     SetAnimatedPalette3
    END
   END
  END;
  UpdatePalette
 END InitPalette;

 PROCEDURE InitDualPalette;
 BEGIN
  SetRGB(0, 0, 0, 0);
  SetRGB(1, 127, 127, 127);
  SetRGB(2, 255, 255, 255); CycleRGB(2, 40, 127, 255, 255);
  SetRGB(3, 255, 255, 0); CycleRGB(3, 200, 255, 200, 0);
  SetRGB(4, 255, 0, 0);
  SetRGB(5, 0, 255, 0);
  SetRGB(6, 0, 0, 255);
  SetRGB(7, 216, 152, 0);
  IF zone = Chaos THEN
   SetMetalPalette
  ELSIF zone = Castle THEN
   CASE level[Castle] OF
     1, 4: SetForestPalette
    |2, 3, 5, 7, 10, 14, 16, 17, 19: SetMetalPalette
    |6: SetGhostPalette
    |8: IF difficulty >= 7 THEN SetAnimatedPalette ELSE SetMetalPalette END;
    |9: IF ODD(difficulty) THEN SetDarkFactoryPalette ELSE SetDarkPalette END
   |11: SetDarkPalette
   |12: SetAnimatedPalette
   |13: IF difficulty = 7 THEN SetAnimatedPalette2 ELSE SetForestPalette END
   |15: IF ODD(difficulty) THEN SetForestPalette ELSE SetJunglePalette END
   |18: SetRGBIcePalette
   |20: IF ODD(difficulty) THEN SetDarkPalette ELSE SetDarkFactoryPalette END
   END
  ELSIF zone = Family THEN
   CASE level[Family] OF
     3, 7, 8, 10: SetMetalPalette
    |1, 2, 9: SetForestPalette
    |4: SetGraveyardPalette
    |5: SetBagleyPalette
    |6: SetDarkFactoryPalette
   END
  ELSE
   IF level[Special] = 24 THEN
    SetMetalPalette
   ELSIF level[Special] MOD 8 = 0 THEN
    SetFadePalette
   ELSIF level[Special] MOD 4 = 0 THEN
    SetMetalPalette
   ELSIF level[Special] MOD 2 = 0 THEN
    SetGraveyardPalette
   ELSIF difficulty > 6 THEN
    SetJunglePalette
   ELSIF difficulty > 3 THEN
    SetBagleyPalette
   ELSE
    SetForestPalette
   END
  END
 END InitDualPalette;

BEGIN

 lastCastleLevel:= 0

END ChaosImages.
