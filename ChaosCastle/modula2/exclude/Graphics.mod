IMPLEMENTATION MODULE Graphics;
 (*$ OverflowChk:= FALSE  Volatile:= FALSE *)

 FROM SYSTEM IMPORT ADR, ADDRESS, BYTE, BITSET, LONGSET, SHIFT, CAST, TAG,
  ASSEMBLE, REG, SETREG;
 FROM IntuitionD IMPORT Screen, ScreenPtr, NewScreen, Window, WindowPtr,
  NewWindow, ScreenFlags, ScreenFlagSet, WindowFlags, WindowFlagSet,
  IDCMPFlags, IDCMPFlagSet, customScreen, ExtNewScreen, SaTags, WaTags,
  ScreenBuffer, ScreenBufferPtr, oScanStandard, oScanMax, osErrNoMonitor,
  osErrNoChips, osErrUnknownMode, osErrTooDeep, osErrNotAvailable;
 FROM IntuitionL IMPORT OpenScreen, CloseScreen, OpenWindow, CloseWindow,
  OpenWorkBench, CloseWorkBench, intuitionVersion, ScreenToFront,
  ActivateWindow, MakeScreen, RethinkDisplay, ShowTitle, AllocScreenBuffer,
  ChangeScreenBuffer, FreeScreenBuffer, LockPubScreen, UnlockPubScreen,
  GetScreenData;
 FROM GraphicsD IMPORT BitMap, BitMapPtr, RastPort, RastPortPtr, ViewPort,
  ViewPortPtr, FontFlags, FontFlagSet, TextAttr, AreaInfo, TmpRas, TmpRasPtr,
  DrawModes, DrawModeSet, ViewModes, ViewModeSet, TextFontPtr, bmDisplayable,
  bmInterleaved, bmClear, bidtagNominalWidth, bidtagNominalHeight, dtagDisp,
  bidtagDepth, PropertyFlags, PropertyFlagSet, DisplayInfo,
  BitScaleArgs, Rectangle, bmStandard, bmaFlags, invalidID, TextAttrPtr,
  LayerFlags, LayerFlagSet, LayerInfo, LayerInfoPtr, Layer, LayerPtr,
  bidtagDesiredWidth, bidtagDesiredHeight, ChipRevs, ChipRevSet, GfxBase,
  DimensionInfo, dtagDims, specialFlags, Region, RegionPtr;
 FROM GraphicsL IMPORT AllocRaster, FreeRaster, BltBitMap, BltBitMapRastPort,
  BltMaskBitMapRastPort, CloseFont, InitArea, InitBitMap, InitRastPort,
  InitTmpRas, LoadRGB4, SetDrMd, SetRast, WaitBlit, ClipBlit, graphicsVersion,
  AllocBitMap, FreeBitMap, ScrollVPort, OwnBlitter, DisownBlitter,
  BestModeIDA, GetDisplayInfoData, WritePixel, BitMapScale, LoadRGB32,
  ReadPixel, GetBitMapAttr, SetRGB4, WritePixelLine8, graphicsBase,
  BltPattern, BltTemplate, SetAPen, ScrollRaster, SetSoftStyle, Move, Draw,
  AreaMove, AreaDraw, AreaEnd, RectFill, StripFont, OpenFont, SetFont, Text,
  TextLength, Flood, GetAPen, GetDrMd, GetRGB32, GetRGB4, SetRGB32, GetVPModeID,
  NewRegion, OrRectRegion, DisposeRegion;
 FROM GfxMacros IMPORT SetOPen, SetDrPt, SetAfPen, BndryOff, SetWrMsk;
 FROM LayersL IMPORT CreateBehindLayer, DeleteLayer, DisposeLayerInfo,
  NewLayerInfo, MoveLayer, SizeLayer, InstallClipRegion;
 FROM DiskFontD IMPORT DiskFontHeader, DiskFontHeaderPtr;
 FROM OptDiskFontL IMPORT OpenDiskFont, NewScaledDiskFont, diskfontBase,
  diskfontVersion;
 FROM ExecD IMPORT MemReqs, MemReqSet, MsgPortPtr, SignalSemaphore, ExecBase,
  AttnFlags, AttnFlagSet;
 FROM ExecL IMPORT AllocMem, FreeMem, FindTask, WaitPort, GetMsg,
  CreateMsgPort, DeleteMsgPort, TypeOfMem, CopyMem, CopyMemQuick, FindSemaphore,
  RemSemaphore, CloseLibrary, execBase;
 FROM AslD IMPORT aslScreenModeRequest, ScreenModeRequester,
  ScreenModeRequesterPtr, tsmTitleText, tsmInitialDisplayID, tsmDoOverscanType,
  tsmInitialDisplayWidth, tsmInitialDisplayHeight, tsmInitialDisplayDepth,
  tsmInitialInfoOpened, tsmDoWidth, tsmDoHeight, tsmDoDepth, tsmDoAutoScroll,
  tsmInitialAutoScroll, tsmMinWidth, tsmMinHeight, tsmMinDepth,
  tsmPropertyFlags, tsmPropertyMask, tsmFilterFunc, tsmMaxDepth;
 FROM OptAslL IMPORT AllocAslRequest, AslRequest, FreeAslRequest,
  aslBase, aslVersion;
 FROM UtilityD IMPORT Hook, HookPtr, HookProc;
 FROM DosD IMPORT Process, ProcessPtr;
 FROM DosL IMPORT UnLoadSeg, Delay;
 FROM Hardware IMPORT Custom, custom, BC0Flags, BC0FlagSet, BC1Flags,
  BC1FlagSet;
 FROM Checks IMPORT AddTermProc, Ask;
 FROM AmigaBase IMPORT LinkWindow, UnlinkWindow, globals, programName,
  memLow, fontName, askMode, customMask, maskBM, dbGiven, givenDB, fontGiven,
  bSmartRefresh, closeWB, fontSize, maskPlaneGiven, givenMaskPlane,
  RefreshArea, RefreshImage, dither, ditherGiven, AskScreenMode, asmHiRes,
  EndRefreshArea, asmInterlace, asmHam;
 FROM Memory IMPORT TagItem, TagItemPtr, NextTag, StrLength, StrPtr, CARD8,
  CARD16, INT16, CARD32, INT32, tagIgnore, LockR, LockW, Unlock;
 IMPORT GraphicsD, GraphicsL, IntuitionL, R;

 CONST
  screenBitmap = 0; (* IntuitionD is wrong ! *)
  copyBitmap = 1;

  bVirtual = 0;
  bDisplay = 1;
  bBuffer = 2;
  bMemory = 3;
  bDialog = 4;
  bMask = 5;
  bPrinter = 6;

  topazName = "topaz.font";

  topazAttr = TextAttr{
   name: ADR(topazName),
   ySize: 8,
   style: GraphicsD.FontStyleSet{},
   flags: FontFlagSet{romFont}};

 TYPE
  Area = RECORD
   w: WindowPtr;
   li: LayerInfoPtr;
   l: LayerPtr;
   rgn: RegionPtr;
   rp: RastPortPtr;
   bm, bbm, fbm: BitMapPtr;
   font: TextFontPtr;
   fontHd: DiskFontHeaderPtr;
   customPat: ARRAY[0..15] OF BYTE;
   wMask, uMask, mxPen: LONGCARD;
   tx, ty: INTEGER;
   depth, width, height: INTEGER;
   mul, add: INTEGER;
   colors: CARDINAL;
   bltmode: SHORTCARD;
   dbbuffer, combine, planar, hamMode: BOOLEAN;
  END;
  AreaPtr = POINTER TO Area;
  BWArr = ARRAY[0..1] OF CARD16;
  ColorArr = ARRAY[0..31] OF CARD16;
  PatternArr = ARRAY[0..4] OF CARD32;
  Color64To16Arr = ARRAY[0..63] OF CARD8;

 CONST
  Patterns = PatternArr
   {000000000H, 0AAAA0000H, 0AAAA5555H, 05555FFFFH, 0FFFFFFFFH};
  Color64To16 = Color64To16Arr{
   0, 1, 1, 9,     2, 3, 3, 9,     2, 3, 3, 11,   10, 10, 11, 11,
   4, 5, 5, 9,     6, 7, 7, 9,     6, 7, 3, 11,   10, 10, 11, 11,
   4, 5, 5, 13,    6, 7, 5, 13,    6, 6, 8, 8,    14, 14, 8, 15,
   12, 12, 13, 13, 12, 12, 13, 13, 14, 14, 8, 15, 14, 14, 15, 15
  };


 VAR
  area: AreaPtr;
  s: ScreenPtr;
  scBuff: ARRAY[0..1] OF ScreenBufferPtr;
  safePort, dispPort: MsgPortPtr;
  curBuff: CARD16;
  displayed, lfsKilled: BOOLEAN;
  lastErr: GraphicsErr;

  fontAttr: TextAttr;
  screenFont: TextFontPtr;
  newArea: AreaPtr; (* Used by scrModeReq's hook *)
  areainfo: AreaInfo; (* shared between rastports *)
  vectors: ARRAY[0..159] OF CARD16; (* shared *)
  mainBitMap: BitMapPtr;
   (* first bitmap created by openscreen; used as friendbitmap *)
  mainTmpRas: TmpRas; (* shared *)
  tmpRasBm: BitMapPtr;
  tmpRasWidth, tmpRasHeight: INT16;
  oldWindow: WindowPtr; (* for Dos requesters *)
  rgb32: RECORD
   count, num: CARD16;
   rgb: ARRAY[0..255] OF RECORD rc, gc, bc: CARD32 END;
   last: CARD32;
  END;
  rgb4: ARRAY[0..31] OF CARD16;
  areaCnt: CARD16;
  maxDepth: INT16;


 PROCEDURE GetMaxDepth(): CARD16;
  VAR
   tagbuff: ARRAY[0..6] OF CARD32;
 BEGIN
  IF (graphicsVersion >= 39) THEN
   IF BestModeIDA(TAG(tagbuff, bidtagNominalWidth, 320,
            bidtagNominalHeight, 240, bidtagDepth, 24, 0)) <> invalidID THEN
    RETURN 24
   ELSIF BestModeIDA(TAG(tagbuff, bidtagNominalWidth, 320,
            bidtagNominalHeight, 240, bidtagDepth, 16, 0)) <> invalidID THEN
    RETURN 16
   ELSIF BestModeIDA(TAG(tagbuff, bidtagNominalWidth, 320,
            bidtagNominalHeight, 240, bidtagDepth, 15, 0)) <> invalidID THEN
    RETURN 15
   ELSIF BestModeIDA(TAG(tagbuff, bidtagNominalWidth, 320,
            bidtagNominalHeight, 200, bidtagDepth, 8, 0)) <> invalidID THEN
    RETURN 8
   END
  END;
  RETURN 5
 END GetMaxDepth;

 PROCEDURE GetGraphicsSysAttr(VAR what: TagItem);
 BEGIN
  WITH what DO
   IF tag = aSIZEX THEN data:= 320
   ELSIF tag = aSIZEY THEN data:= 240
   ELSIF tag = aWIDTH THEN
    IF area <> NIL THEN data:= area^.width ELSE data:= 320 END
   ELSIF tag = aHEIGHT THEN
    IF area <> NIL THEN data:= area^.height ELSE data:= 240 END
   ELSIF tag = aBARHEIGHT THEN
    IF s <> NIL THEN
     lint:= s^.barHeight + 1
    ELSE
     lint:= 10;
    END
   ELSIF tag = aCOLOR THEN
    data:= SHIFT(2, maxDepth);
    IF NOT(m68020 IN execBase^.attnFlags) THEN
     data:= 2
    ELSIF NOT(m68040 IN execBase^.attnFlags) AND (data > 32) THEN
     data:= 32
    END
   ELSE
    tag:= 0
   END
  END
 END GetGraphicsSysAttr;

 PROCEDURE GetGraphicsErr(): GraphicsErr;
  VAR
   res{R.D0}: GraphicsErr;
 BEGIN
  res:= lastErr;
  lastErr:= gOk;
  RETURN res
 END GetGraphicsErr;

 PROCEDURE DeleteBitMap(bm: BitMapPtr; depth, width, height: INT16; displayable: BOOLEAN);
  VAR
   c{R.D7}: CARD16;
 BEGIN
  WaitBlit;
  IF (graphicsVersion >= 39) AND (maskBM OR displayable OR (depth > 1)) THEN
   FreeBitMap(bm)
  ELSE
   WITH bm^ DO
    FOR c:= 0 TO depth - 1 DO
     IF planes[c] <> NIL THEN
      FreeRaster(planes[c], width, height);
      planes[c]:= NIL
     END
    END
   END;
   FreeMem(bm, SIZE(BitMap))
  END
 END DeleteBitMap;

 PROCEDURE CreateBitMap(depth, width, height: INT16; displayable: BOOLEAN): BitMapPtr;
  VAR
   bm{R.A3}: BitMapPtr;
   c{R.D7}: CARD16;
   flags{R.D6}: LONGSET;
 BEGIN
  IF (graphicsVersion >= 39) AND (maskBM OR displayable OR (depth > 1)) THEN
   IF displayable THEN
    flags:= LONGSET{bmClear, bmDisplayable}
   ELSE
    flags:= LONGSET{bmClear}
   END;
   bm:= AllocBitMap(width, height, depth, flags, mainBitMap)
  ELSE
   bm:= AllocMem(SIZE(BitMap), MemReqSet{memClear});
   IF bm <> NIL THEN
    InitBitMap(bm^, depth, width, height);
    FOR c:= 0 TO depth - 1 DO
     bm^.planes[c]:= AllocRaster(width, height);
     IF bm^.planes[c] = NIL THEN
      DeleteBitMap(bm, depth, width, height, displayable); RETURN NIL
     END
    END
   END
  END;
  RETURN bm
 END CreateBitMap;

 PROCEDURE FreeTmpRas;
 BEGIN
  IF tmpRasBm <> NIL THEN
   DeleteBitMap(tmpRasBm, 1, tmpRasWidth * 16, tmpRasHeight, FALSE);
   tmpRasBm:= NIL;
   tmpRasWidth:= 0; tmpRasHeight:= 0;
   mainTmpRas.rasPtr:= NIL;
   mainTmpRas.size:= 0
  END
 END FreeTmpRas;

 PROCEDURE AssertTmpSize(width{R.D2}, height{R.D3}: INT16);
  VAR
   buff{R.D7}: ADDRESS;
 BEGIN
  width:= (width + 15) DIV 16;
  IF INT32(width * height) > INT32(tmpRasWidth * tmpRasHeight) THEN
   FreeTmpRas;
   tmpRasBm:= CreateBitMap(1, width * 16, height, FALSE);
   IF tmpRasBm <> NIL THEN
    tmpRasWidth:= width; tmpRasHeight:= height;
    mainTmpRas.rasPtr:= tmpRasBm^.planes[0];
    mainTmpRas.size:= INT32(width * height) * 2
   END
  END
 END AssertTmpSize;

 PROCEDURE KillLFS;
  TYPE
   MySemaphore = RECORD
    ss: SignalSemaphore;
    screen: ADDRESS;
    magic: CARD32;
   END;
  VAR
   ms{R.A2}: POINTER TO MySemaphore;
 BEGIN
  IF lfsKilled THEN RETURN ELSE lfsKilled:= TRUE END;
  ms:= CAST(ADDRESS, FindSemaphore(ADR("LFS")));
  IF ms = NIL THEN RETURN END;
  IF ms^.magic <> 142857369 THEN RETURN END;
  RemSemaphore(CAST(ADDRESS, ms));
  FreeMem(ms^.ss.link.name, SIZE(CARD32));
  CloseScreen(ms^.screen);
  FreeMem(ms, SIZE(MySemaphore))
 END KillLFS;

 PROCEDURE InitColors(area: AreaPtr; vp: ViewPortPtr);
  VAR
   c, d{R.D6}, mul2, max, lst: INTEGER;
   div{R.D7}: LONGCARD;
 BEGIN
  WITH area^ DO
   IF hamMode THEN
    IF depth = 8 THEN mul:= 4; add:= 0 ELSE mul:= 2; add:= 8 END
   ELSIF depth > 8 THEN RETURN
   ELSIF depth = 8 THEN mul:= 6; add:= 20
   ELSIF depth = 7 THEN mul:= 5; add:= 2
   ELSIF depth = 6 THEN mul:= 4; add:= 0
   ELSIF depth = 5 THEN mul:= 3; add:= 5
   ELSIF depth = 4 THEN mul:= 2; add:= 8
   ELSE mul:= 2; add:= 0
   END;
   max:= mul * mul * mul - 1;
   div:= MAX(LONGCARD) DIV LONGCARD(mul - 1);
   mul2:= mul * mul;
   IF depth <= 8 THEN lst:= SHIFT(1, depth) - 1 ELSE lst:= 255 END;
   IF hamMode THEN lst:= SHIFT(1, depth - 2) - 1 END;
   rgb32.count:= lst + 1;
   IF graphicsVersion >= 39 THEN
    GetRGB32(vp^.colorMap, 0, rgb32.count, ADR(rgb32.rgb))
   ELSE
    FOR c:= 0 TO 31 DO
     rgb4[c]:= GetRGB4(vp^.colorMap, c)
    END
   END;
   WITH rgb32.rgb[lst] DO rc:= MAX(LONGCARD); gc:= 1073741824; bc:= gc END;
   IF lst < 32 THEN rgb4[lst]:= 0F44H END;
   IF depth = 7 THEN
    WITH rgb32.rgb[1] DO rc:= MAX(LONGCARD); bc:= rc; gc:= rc END
   END;
   FOR c:= 0 TO max DO
    d:= c;
    WITH rgb32.rgb[d + add] DO
     rc:= CARD32(c / mul2) * div;
     gc:= CARD32((c / mul) REM mul) * div;
     bc:= CARD32(c REM mul) * div
    END;
    d:= c + add;
    IF d < 32 THEN
     rgb4[d]:= (((c / mul2) * 15 + mul - 2) / (mul - 1)) * 256
             + ((((c / mul) REM mul) * 15 + mul - 2) / (mul - 1)) * 16
             + ((c REM mul) * 15 + mul - 2) / (mul - 1)
    END
   END;
   IF (mul = 2) AND (add = 8) THEN
    mul:= 4; div:= 64;
    FOR c:= 1 TO 6 DO
     WITH rgb32.rgb[c] DO
      rc:= rgb32.rgb[c + 8].rc DIV 2 + 1;
      gc:= rgb32.rgb[c + 8].gc DIV 2 + 1;
      bc:= rgb32.rgb[c + 8].bc DIV 2 + 1
     END;
     rgb4[c]:= (c DIV 4) * 2048 + ((c DIV 2) MOD 2) * 128 + (c MOD 2) * 8
    END;
    rgb32.rgb[0]:= rgb32.rgb[8]; rgb4[0]:= rgb4[8];
    rgb32.rgb[7].rc:= 1431655765;
    rgb32.rgb[7].gc:= 1431655765;
    rgb32.rgb[7].bc:= 1431655765;
    rgb4[7]:= 0555H;
    rgb32.rgb[8].rc:= 2863311530;
    rgb32.rgb[8].gc:= 2863311530;
    rgb32.rgb[8].bc:= 2863311530;
    rgb4[8]:= 0AAAH;
    add:= -1
   END;
   IF graphicsVersion >= 39 THEN
    LoadRGB32(vp, ADR(rgb32));
   ELSE
    LoadRGB4(vp, ADR(rgb4), 32)
   END
  END
 END InitColors;

 PROCEDURE FilterModes(hook{R.A0}: HookPtr; scrMR{R.A2}, modeID{R.A1}: ADDRESS): ADDRESS;
  VAR
   dInfo: DimensionInfo;
   dWidth{R.D7}, dHeight{R.D6}, min{R.D5}, mind: INTEGER;
 BEGIN
  SETREG(R.A4, hook^.data);
  IF newArea^.colors = 0 THEN mind:= 127 ELSE mind:= 8 END;
  IF GetDisplayInfoData(NIL, ADR(dInfo), SIZE(dInfo), dtagDims, modeID) > 0 THEN
   dWidth:= dInfo.nominal.maxX - dInfo.nominal.minX + 1;
   dHeight:= dInfo.nominal.maxY - dInfo.nominal.minY + 1;
   IF dWidth < dHeight THEN min:= dWidth ELSE min:= dHeight END;
   IF (dInfo.maxOScan.maxX - dInfo.maxOScan.minX + 1 >= newArea^.width) AND
      (dInfo.maxOScan.maxY - dInfo.maxOScan.minY + 1 >= newArea^.height) AND
      (dWidth < newArea^.width * 2) AND (dHeight < newArea^.height * 2) AND
      (ABS((dWidth - newArea^.width) - (dHeight - newArea^.height)) < min / 3) AND
      (INTEGER(dInfo.maxDepth) >= newArea^.depth) AND (INTEGER(dInfo.maxDepth) <= mind) THEN
    RETURN ADDRESS(-1)
   END
  END;
  RETURN NIL
 END FilterModes;

 PROCEDURE CreateArea(tags: TagItemPtr): AreaPtr;
  VAR
   ns: ExtNewScreen;
   wbs: Screen;
   wbsp: ScreenPtr;
   nw: NewWindow;
   req: ScreenModeRequesterPtr;
   hook: Hook;
   area: AreaPtr;
   process: ProcessPtr;
   pens: RECORD
    blockPen, detailPen, textPen, endPen: CARDINAL;
   END;
   errorCode: INT32;
   sizex, sizey, ddepth: INT16;
   display, db: BOOLEAN;
   areaDepth: SHORTCARD;
   colors, modeID, overscanTag, overscan: CARD32;
   viewModes: ViewModeSet;
   propFlags: PropertyFlagSet;
   displayInfo: DisplayInfo;
   dInfo: DimensionInfo;
   dispInfo: DisplayInfo;
   vp{R.D5}: ViewPortPtr;
   tagbuff: ARRAY[0..28] OF CARD32;
   tagHook: LONGCARD;
   c: CARD16;
   asms: BITSET;
   sizeGiven: SHORTINT;
   dbOk, virtual: BOOLEAN;

  PROCEDURE InitRp;
  BEGIN
   WITH area^ DO
    planar:= TRUE;
    IF graphicsVersion >= 39 THEN
     IF NOT(bmStandard IN CAST(LONGSET, GetBitMapAttr(rp^.bitMap, bmaFlags))) THEN
      customMask:= FALSE; planar:= FALSE
     END
    END;
    IF maskPlaneGiven THEN planar:= givenMaskPlane END;
    InitArea(areainfo, ADR(vectors), 64);
    rp^.areaInfo:= ADR(areainfo);
    BndryOff(rp);
    GraphicsL.SetAPen(rp, 1);
    GraphicsL.SetBPen(rp, 0);
    AssertTmpSize(sizex, sizey);
    IF tmpRasBm <> NIL THEN rp^.tmpRas:= ADR(mainTmpRas) END;
    SetArea(area);
    SetCopyMode(CopyMode{snd, sd})
   END
  END InitRp;

  (*$ EntryClear:= TRUE *)
 BEGIN
  pens.endPen:= MAX(CARDINAL); sizeGiven:= 2;
  modeID:= invalidID;
  areaDepth:= maxDepth;
  colors:= 0;
  LOOP
   WITH NextTag(tags)^ DO
    IF tag = 0 THEN EXIT
    ELSIF tag = aSIZEX THEN sizex:= data; DEC(sizeGiven)
    ELSIF tag = aSIZEY THEN sizey:= data; DEC(sizeGiven)
    ELSIF tag = aCOLOR THEN
     IF data <> 0 THEN
      colors:= 2; areaDepth:= 1;
      WHILE colors < data DO
       colors:= colors * 2;
       INC(areaDepth)
      END
     END
    ELSIF tag = aTYPE THEN
     display:= data IN {bVirtual, bDisplay, bBuffer};
     virtual:= (data = bVirtual) OR (data = bDialog);
     db:= (data = bBuffer)
    END
   END
  END;
  IF display THEN
   IF intuitionVersion >= 36 THEN
    wbsp:= LockPubScreen(NIL)
   ELSE
    wbsp:= NIL
   END;
   IF wbsp <> NIL THEN
    IF sizeGiven > 0 THEN
     sizex:= wbsp^.width;
     sizey:= wbsp^.height;
     modeID:= GetVPModeID(ADR(wbsp^.viewPort))
    END;
    viewModes:= wbsp^.viewPort.modes;
    UnlockPubScreen(NIL, wbsp)
   ELSIF GetScreenData(ADR(wbs), SIZE(wbs), ScreenFlagSet{wbenchScreen}, NIL) THEN
    IF sizeGiven > 0 THEN
     sizex:= wbs.width;
     sizey:= wbs.height
    END;
    viewModes:= wbs.viewPort.modes
   ELSIF sizeGiven > 0 THEN
    sizex:= 320; sizey:= 240
   END
  END;
  IF (areaDepth > 5) AND (graphicsVersion < 36) THEN
   lastErr:= gNotSupported;
   RETURN NIL
  END;
  LockR(globals);
  IF (mainBitMap = NIL) AND (globals^.mainWindow <> NIL) THEN
   mainBitMap:= globals^.mainWindow^.wScreen^.rastPort.bitMap
  END;
  Unlock(globals);
  area:= AllocMem(SIZE(Area), MemReqSet{memClear});
  newArea:= area;
  IF area <> NIL THEN
   area^.colors:= colors;
   WITH area^ DO
    depth:= areaDepth;
    mxPen:= SHIFT(1, depth) - 1;
    uMask:= mxPen;
    width:= sizex; height:= sizey;
    IF NOT(display) THEN (* Create bitmap manually *)
     IF NOT(virtual) THEN
      bm:= CreateBitMap(depth, width, height, FALSE)
     ELSE
      bm:= mainBitMap
     END;
     IF bm <> NIL THEN
      fbm:= bm;
      li:= NewLayerInfo();
      IF li <> NIL THEN
       l:= CreateBehindLayer(li, bm, 0, 0, width - 1, height - 1, LayerFlagSet{layerSimple}, NIL);
       IF l <> NIL THEN
        rgn:= NewRegion();
        IF rgn <> NIL THEN
         rp:= l^.rp;
         InitRp;
         SetRast(rp, 0);
         IF rp^.tmpRas <> NIL THEN INC(areaCnt); RETURN area END;
         DisposeRegion(rgn)
        END;
        IGNORE DeleteLayer(l)
       END;
       DisposeLayerInfo(li)
      END;
      DeleteBitMap(bm, depth, width, height, FALSE)
     END;
     FreeMem(area, SIZE(Area));
     lastErr:= gNoMemory;
     RETURN NIL
    END;
   (* display = TRUE -> use OpenScreen *)
    fontAttr:= topazAttr;
    fontAttr.ySize:= fontSize;
    IF fontGiven THEN
     fontAttr.name:= ADR(fontName);
     fontAttr.flags:= FontFlagSet{diskFont}
    END;
    IF diskfontBase <> NIL THEN
     screenFont:= OpenDiskFont(ADR(fontAttr))
    ELSE
     screenFont:= OpenFont(ADR(fontAttr))
    END;
    FOR c:= 0 TO 31 DO rgb4[c]:= 0 END;
    rgb4[1]:= 0FFFH;
    ns.ns.depth:= areaDepth;
    ns.ns.width:= sizex; ns.ns.height:= sizey;
    ns.ns.type:= customScreen + ScreenFlagSet{screenQuiet, screenBehind, nsExtended};
    IF virtual THEN EXCL(ns.ns.type, screenQuiet) END;
    ns.ns.font:= ADR(fontAttr);
    ns.ns.defaultTitle:= programName;
    IF graphicsVersion >= 39 THEN
     IF modeID = invalidID THEN
      modeID:= BestModeIDA(TAG(tagbuff, bidtagNominalWidth, width, bidtagDesiredWidth, width, bidtagNominalHeight, height, bidtagDesiredHeight, height, bidtagDepth, depth, 0))
     END
    ELSE
     IF sizeGiven > 0 THEN
      ns.ns.viewModes:= viewModes
     ELSIF (sizex > 340) OR (sizey > 256) THEN
      INCL(ns.ns.viewModes, hires);
      INCL(ns.ns.viewModes, lace)
     END;
     modeID:= 0
    END;
    IF ((askMode) OR (sizeGiven > 0)) THEN
     IF (aslBase <> NIL) AND (aslVersion >= 38) THEN
      IF sizeGiven > 0 THEN tagHook:= tagIgnore ELSE tagHook:= tsmFilterFunc END;
      propFlags:= specialFlags;
      IF colors = 0 THEN EXCL(propFlags, isHam); depth:= 3 END;
      hook.entry:= FilterModes;
      hook.data:= REG(R.A4);
      req:= AllocAslRequest(aslScreenModeRequest, TAG(tagbuff,
         tsmTitleText, programName,
         tsmInitialDisplayID, modeID,
         tsmPropertyFlags, PropertyFlagSet{},
         tsmPropertyMask, propFlags,
         tagHook, ADR(hook),
         tsmInitialDisplayWidth, sizex,
         tsmInitialDisplayHeight, sizey,
         tsmDoWidth, (sizeGiven > 0),
         tsmDoHeight, (sizeGiven > 0),
         tsmDoOverscanType, (sizeGiven > 0),
         tsmInitialDisplayDepth, maxDepth,
         tsmMinDepth, depth,
         tsmInitialAutoScroll, TRUE,
         tsmDoDepth, (sizeGiven > 0) OR (colors = 0),
         0));
      IF req <> NIL THEN
       IF AslRequest(req, NIL) THEN
        modeID:= req^.displayID;
        IF (INTEGER(req^.displayDepth) > depth) AND
           ((sizeGiven > 0) OR (colors = 0)) THEN
         depth:= req^.displayDepth
        END;
        IF sizeGiven > 0 THEN
         width:= req^.displayWidth; ns.ns.width:= width;
         height:= req^.displayHeight; ns.ns.height:= height
        END;
        mxPen:= SHIFT(1, depth) - 1;
        uMask:= mxPen;
        ns.ns.depth:= depth
       ELSIF (sizeGiven > 0) OR (colors = 0) THEN
        FreeAslRequest(req);
        FreeMem(area, SIZE(Area));
        lastErr:= gCanceled;
        RETURN NIL
       END;
       FreeAslRequest(req)
      END
     ELSIF AskScreenMode <> NIL THEN
      modeID:= 0; depth:= 5;
      asms:= {}; ditherGiven:= TRUE;
      IF hires IN ns.ns.viewModes THEN depth:= 3; INCL(asms, asmHiRes) END;
      IF lace IN ns.ns.viewModes THEN INCL(asms, asmInterlace) END;
      asms:= AskScreenMode(asms, depth);
      IF asmHiRes IN asms THEN
       INCL(ns.ns.viewModes, hires)
      ELSE
       ns.ns.width:= ns.ns.width DIV 2;
       EXCL(ns.ns.viewModes, hires)
      END;
      IF asmInterlace IN asms THEN INCL(ns.ns.viewModes, lace) ELSE EXCL(ns.ns.viewModes, lace) END;
      IF asmHam IN asms THEN depth:= 6; hamMode:= TRUE; INCL(ns.ns.viewModes, ham) END;
      mxPen:= SHIFT(1, depth) - 1;
      uMask:= mxPen;
      ns.ns.depth:= depth
     END
    END;
    IF (graphicsVersion >= 36) AND (modeID <> 0) THEN
     IF GetDisplayInfoData(NIL, ADR(dispInfo), SIZE(dispInfo), dtagDisp, modeID) > 0 THEN
      IF (isHam IN dispInfo.propertyFlags) THEN INCL(ns.ns.viewModes, ham) END
     END
    END;
    ddepth:= depth;
    IF (ham IN ns.ns.viewModes) THEN
     DEC(ddepth, 2); mxPen:= SHIFT(1, ddepth) - 1
    END;
    IF (colors = 0) AND ((ddepth = 6) OR (ddepth = 4)) THEN
     ns.ns.blockPen:= CAST(SHORTINT, SHORTCARD(mxPen));
     pens.textPen:= mxPen;
     pens.blockPen:= mxPen
    ELSE
     ns.ns.blockPen:= 1;
     pens.textPen:= 1;
     pens.blockPen:= 1
    END;
    overscanTag:= tagIgnore; overscan:= 0;
    IF (graphicsVersion >= 36) AND (modeID <> 0) THEN
     IF GetDisplayInfoData(NIL, ADR(dInfo), SIZE(dInfo), dtagDims, modeID) > 0 THEN
      IF (dInfo.nominal.maxX - dInfo.nominal.minX + 1 < width) OR
         (dInfo.nominal.maxY - dInfo.nominal.minY + 1 < height) THEN
       IF (dInfo.stdOScan.maxX - dInfo.stdOScan.minX + 1 < width) OR
          (dInfo.stdOScan.maxY - dInfo.stdOScan.minY + 1 < height) THEN
        overscanTag:= CARD32(saOverscan); overscan:= oScanMax
       ELSE
        overscanTag:= CARD32(saOverscan); overscan:= oScanStandard
       END
      END
     END
    END;
    IF modeID <> 0 THEN
     ns.extension:= TAG(tagbuff, saAutoScroll, TRUE, saDisplayID, modeID,
      saPens, ADR(pens), overscanTag, overscan, saInterleaved, customMask,
      saErrorCode, ADR(errorCode), 0)
    ELSE
     ns.extension:= TAG(tagbuff, saAutoScroll, TRUE, saPens, ADR(pens),
      overscanTag, overscan, saInterleaved, customMask,
      saErrorCode, ADR(errorCode), 0)
    END;
    IF (graphicsVersion >= 36) AND (modeID <> 0) THEN
     IF GetDisplayInfoData(NIL, ADR(displayInfo), SIZE(displayInfo), dtagDisp, modeID) > 0 THEN
      IF (graphicsVersion >= 39) THEN
       dbbuffer:= (isDBuffer IN displayInfo.propertyFlags)
      ELSE
       dbbuffer:= (m68010 IN execBase^.attnFlags) OR (depth > 1)
      END;
      hamMode:= (isHam IN displayInfo.propertyFlags);
      IF (isForeign IN displayInfo.propertyFlags) THEN customMask:= FALSE END
     ELSE
      dbbuffer:= FALSE
     END
    ELSE
     dbbuffer:= (m68010 IN execBase^.attnFlags) OR (depth > 1)
    END;
    IF NOT(ditherGiven) AND askMode AND NOT(hamMode) AND
       (depth <= 8) AND (colors = 0) THEN
     dither:= Ask(ADR("Dithering ?"), NIL, ADR("Yes"), ADR("No"))
    END;
    IF dbGiven THEN dbbuffer:= givenDB END;
    s:= OpenScreen(ns.ns);
    IF s <> NIL THEN
     ShowTitle(s, virtual);
     vp:= ADR(s^.viewPort);
     IF colors <> 0 THEN
      IF NOT(virtual) THEN LoadRGB4(vp, ADR(rgb4), 32) END;
      SetRGB4(vp, 19, 15, 15, 15); (* For busy pointer *)
      SetRGB4(vp, 18, 0, 0, 0);
      SetRGB4(vp, 17, 13, 9, 0)
     ELSE
      InitColors(area, vp)
     END;
     KillLFS;
     IF closeWB THEN Delay(10); IGNORE CloseWorkBench(); Delay(30) END;
     nw.blockPen:= ns.ns.blockPen;
     nw.width:= width; nw.height:= height;
     nw.flags:= WindowFlagSet{backDrop, borderless, simpleRefresh, rmbTrap};
     IF bSmartRefresh THEN EXCL(nw.flags, simpleRefresh) END;
     nw.screen:= s; nw.type:= customScreen;
     w:= OpenWindow(nw);
     IF w <> NIL THEN
      rp:= w^.rPort;
      bm:= rp^.bitMap;
      fbm:= bm;
      mainBitMap:= fbm;
      InitRp;
      IF rp^.tmpRas <> NIL THEN
       IF db THEN
        dbOk:= FALSE;
        IF (intuitionVersion < 39) OR NOT(dbbuffer) THEN
         bbm:= CreateBitMap(depth, width, height, dbbuffer);
         IF bbm <> NIL THEN
          dbOk:= TRUE;
          mainBitMap:= bbm;
          rp^.bitMap:= bbm;
          SetRast(rp, 0)
         END
        ELSE
         curBuff:= 0;
         IF safePort = NIL THEN safePort:= CreateMsgPort() END;
         IF dispPort = NIL THEN dispPort:= CreateMsgPort() END;
         IF (safePort <> NIL) AND (dispPort <> NIL) THEN
          scBuff[0]:= AllocScreenBuffer(s, NIL, LONGSET{screenBitmap});
          IF scBuff[0] <> NIL THEN
           scBuff[1]:= AllocScreenBuffer(s, NIL, LONGSET{copyBitmap});
           IF scBuff[1] = NIL THEN
            FreeScreenBuffer(s, scBuff[0]);
            scBuff[0]:= NIL
           ELSE
            scBuff[0]^.dBufInfo^.safeMessage.replyPort:= safePort;
            scBuff[0]^.dBufInfo^.dispMessage.replyPort:= dispPort;
            scBuff[1]^.dBufInfo^.safeMessage.replyPort:= safePort;
            scBuff[1]^.dBufInfo^.dispMessage.replyPort:= dispPort;
            dbOk:= TRUE
           END
          END
         END
        END
       ELSE
        dbbuffer:= FALSE; dbOk:= TRUE
       END;
       displayed:= TRUE;
       IF dbOk THEN
        LinkWindow(w);
        LockW(globals);
        globals^.graphicWindow:= w;
        Unlock(globals);
        process:= CAST(ProcessPtr, FindTask(NIL));
        oldWindow:= process^.windowPtr;
        process^.windowPtr:= w;
        INC(areaCnt);
        IF virtual THEN
         ScreenToFront(s);
         ActivateWindow(w)
        END;
        RETURN area
       END
      END;
      rp^.bitMap:= fbm;
      rp^.tmpRas:= NIL;
      WaitBlit;
      CloseWindow(w);
      mainBitMap:= NIL
     END;
     WaitBlit;
     CloseScreen(s); s:= NIL
    END
   END;
   FreeMem(area, SIZE(Area))
  END;
  IF (errorCode = osErrNoMonitor) OR (errorCode = osErrNoChips) OR
     (errorCode = osErrUnknownMode) OR (errorCode = osErrNotAvailable) THEN
   lastErr:= gNotSupported
  ELSIF (errorCode = osErrTooDeep) THEN
   lastErr:= gTooComplex
  ELSE
   lastErr:= gNoMemory
  END;
  RETURN NIL
 END CreateArea;
  (*$ POP EntryClear *)

 PROCEDURE DeleteArea(VAR a: AreaPtr);
  VAR
   process{R.D7}: ProcessPtr;
 BEGIN
  IF a = NIL THEN RETURN END;
  SetArea(a);
  SetBuffer(TRUE, FALSE);
  WaitBlit;
  WITH a^ DO
   IF fontHd <> NIL THEN
    StripFont(ADR(fontHd^.tf));
    UnLoadSeg(fontHd^.segment)
   ELSIF font <> NIL THEN
    CloseFont(font)
   END;
   WaitBlit;
   WITH rp^ DO
    tmpRas:= NIL;
    bitMap:= fbm
   END;
   IF w <> NIL THEN
    s^.rastPort.bitMap:= fbm;
    s^.viewPort.rasInfo^.bitMap:= fbm;
    process:= CAST(ProcessPtr, FindTask(NIL));
    process^.windowPtr:= oldWindow;
    UnlinkWindow(w);
    LockW(globals);
    globals^.graphicWindow:= NIL;
    Unlock(globals);
    IF NOT(displayed) AND (dispPort <> NIL) THEN
     WaitPort(dispPort);
     IGNORE GetMsg(dispPort);
     displayed:= TRUE
    END;
    IF scBuff[1] <> NIL THEN FreeScreenBuffer(s, scBuff[1]); scBuff[1]:= NIL END;
    IF scBuff[0] <> NIL THEN FreeScreenBuffer(s, scBuff[0]); scBuff[0]:= NIL END;
    WaitBlit; CloseWindow(w);
    WaitBlit;
    mainBitMap:= NIL;
    CloseScreen(s); s:= NIL;
    IF screenFont <> NIL THEN
     CloseFont(screenFont);
     screenFont:= NIL
    END
   ELSE
    IGNORE DeleteLayer(l);
    DisposeLayerInfo(li);
    IF bm <> NIL THEN DeleteBitMap(bm, depth, width, height, FALSE) END;
    IF rgn <> NIL THEN DisposeRegion(rgn) END
   END;
   IF fbm <> bm THEN bbm:= bm END;
   IF bbm <> NIL THEN
    DeleteBitMap(bbm, depth, width, height, dbbuffer)
   END
  END;
  DEC(areaCnt);
  IF areaCnt = 0 THEN FreeTmpRas END;
  FreeMem(a, SIZE(Area));
  a:= NIL
 END DeleteArea;

 PROCEDURE LoadColors;
  VAR
   count{R.D7}: CARD16;
 BEGIN
  WITH area^ DO
   IF (s = NIL) OR (colors = 0) THEN RETURN END;
   count:= mxPen + 1;
   IF graphicsVersion >= 39 THEN
    IF count < 256 THEN rgb32.rgb[count].rc:= 0 END;
    rgb32.count:= count;
    LoadRGB32(ADR(s^.viewPort), ADR(rgb32))
   ELSE
    LoadRGB4(ADR(s^.viewPort), ADR(rgb4), count)
   END
  END
 END LoadColors;

 PROCEDURE AreaToFront;
 BEGIN
  WITH area^ DO
   IF w <> NIL THEN
    LoadColors;
    ScreenToFront(s);
    ActivateWindow(w)
   END
  END
 END AreaToFront;

 PROCEDURE SwitchArea;
  VAR
   temp{R.D7}: BitMapPtr;
 BEGIN
  WITH area^ DO
   IF (bbm <> NIL) THEN
    IF dbbuffer THEN
     temp:= bbm; bbm:= bm; bm:= temp;
     rp^.bitMap:= bm;
     s^.rastPort.bitMap:= bbm;
     s^.viewPort.rasInfo^.bitMap:= bbm;
     MakeScreen(s); RethinkDisplay;
     LoadColors
    ELSE
     WaitTOF; rp^.bitMap:= bbm; LoadColors;
     BltBitMapRastPort(bbm, 0, 0, ADR(s^.rastPort), 0, 0, width, height, 0C0H)
    END
   ELSIF scBuff[0] <> NIL THEN
    rp^.bitMap:= scBuff[curBuff]^.bitMap;
    curBuff:= 1 - curBuff;
    IF NOT(displayed) THEN
     WaitPort(dispPort); IGNORE GetMsg(dispPort)
    END;
    WaitBlit;
    IF ChangeScreenBuffer(s, scBuff[curBuff]) THEN
     LoadColors;
     WaitPort(safePort); IGNORE GetMsg(safePort);
     displayed:= FALSE
    ELSE
     displayed:= TRUE
    END
   END
  END
 END SwitchArea;

 PROCEDURE UpdateArea;
  VAR
   sbm, dbm, obm: BitMapPtr;
 BEGIN
  WITH area^ DO
   LoadColors;
   IF bbm <> NIL THEN
    IF dbbuffer THEN sbm:= bm; dbm:= bbm ELSE sbm:= bbm; dbm:= bm END;
    obm:= rp^.bitMap; rp^.bitMap:= dbm;
    BltBitMapRastPort(sbm, 0, 0, rp, 0, 0, width, height, 0C0H);
    rp^.bitMap:= obm
   ELSIF scBuff[0] <> NIL THEN
    sbm:= scBuff[1 - curBuff]^.bitMap;
    dbm:= scBuff[curBuff]^.bitMap;
    obm:= rp^.bitMap; rp^.bitMap:= dbm;
    BltBitMapRastPort(sbm, 0, 0, rp, 0, 0, width, height, 0C0H);
    rp^.bitMap:= obm
   END
  END
 END UpdateArea;

 PROCEDURE SetBuffer(first, off: BOOLEAN);
 BEGIN
  WITH area^ DO
   IF bbm <> NIL THEN
    IF dbbuffer THEN
     IF (fbm <> bbm) = first THEN SwitchArea END;
     IF off THEN rp^.bitMap:= bm ELSE rp^.bitMap:= bbm END
    ELSE
     IF off THEN rp^.bitMap:= bbm ELSE rp^.bitMap:= bm END
    END
   ELSIF scBuff[0] <> NIL THEN
    IF (curBuff = 0) <> first THEN SwitchArea END;
    IF off THEN
     rp^.bitMap:= scBuff[1 - curBuff]^.bitMap
    ELSE
     rp^.bitMap:= scBuff[curBuff]^.bitMap
    END
   END
  END
 END SetBuffer;

 PROCEDURE GetBuffer(VAR first, off: BOOLEAN);
 BEGIN
  WITH area^ DO
   IF bbm <> NIL THEN
    IF dbbuffer THEN
     first:= (fbm = bbm);
     off:= (rp^.bitMap = bm)
    ELSE
     first:= TRUE;
     off:= (rp^.bitMap = bbm)
    END
   ELSIF scBuff[0] <> NIL THEN
    first:= (curBuff = 0);
    off:= (rp^.bitMap <> scBuff[curBuff]^.bitMap)
   END
  END
 END GetBuffer;

 PROCEDURE SetArea(a: AreaPtr);
 BEGIN
  area:= ADDRESS(a)
 END SetArea;

 PROCEDURE SetPalette(color, red, green, blue: CARD8);
  VAR
   vp{R.A3}: ViewPortPtr;
   r{R.D7}, g{R.D6}, b{R.D5}: CARD32;
 BEGIN
  WITH area^ DO
   IF w = NIL THEN RETURN END;
   IF color > mxPen THEN RETURN END;
   vp:= ADR(s^.viewPort)
  END;
  IF graphicsVersion >= 39 THEN
   r:= red; INC(r, SHIFT(r, 8)); INC(r, SHIFT(r, 16));
   g:= green; INC(g, SHIFT(g, 8)); INC(g, SHIFT(g, 16));
   b:= blue; INC(b, SHIFT(b, 8)); INC(b, SHIFT(b, 16));
   WITH rgb32.rgb[color] DO rc:= r; gc:= g; bc:= b END
  ELSE
   rgb4[color]:= SHIFT(red DIV 16, 8) + SHIFT(green DIV 16, 4) + blue DIV 16
  END
 END SetPalette;

 PROCEDURE SetCopyMode(dm: CopyMode);
  VAR
   drmd{R.D7}: DrawModeSet;
 BEGIN
  WITH area^ DO
   bltmode:= SHIFT(CAST(SHORTCARD,dm), 4);
   IF (dm = cmOr) THEN
    drmd:= DrawModeSet{};
   ELSIF dm = cmCopy THEN
    IF combine THEN bltmode:= 0E0H END;
    drmd:= DrawModeSet{dm0};
   ELSE (* cmXor *)
    drmd:= DrawModeSet{complement}
   END;
   IF bltmode = 96 THEN
    SetWrMsk(rp, wMask)
   ELSE
    SetWrMsk(rp, uMask)
   END;
   SetDrMd(rp, drmd);
  END
 END SetCopyMode;

 PROCEDURE SetPlanes(planes: CARD32; clear: BOOLEAN);
 BEGIN
  WITH area^ DO
   IF planar THEN
    uMask:= planes;
    SetWrMsk(rp, uMask)
   ELSE
    combine:= NOT(clear) AND (planes <> uMask);
    IF combine THEN
     IF bltmode = 0C0H THEN bltmode:= 0E0H END
    ELSE
     IF bltmode = 0E0H THEN bltmode:= 0C0H END
    END
   END
  END
 END SetPlanes;

 PROCEDURE ShrinkColor(color{R.D1}: CARD32; area{R.A0}: AreaPtr): CARD32;
  VAR
   div{R.D2}, mul{R.D3}: CARD16;
   add{R.D4}: INT16;
 BEGIN
  mul:= area^.mul;
  add:= area^.add;
  div:= (255 + mul) / mul;
  IF add < 0 THEN
   RETURN Color64To16[
      (CARD16(color DIV 65536) DIV 64) * 16
    + (CARD16(color MOD 65536) DIV 16384) * 4
    + CARD16(color MOD 256) DIV 64
   ]
  ELSE
   RETURN ((CARD16(color DIV 65536) DIV div) * mul
        + (CARD16(color MOD 65536) DIV 256) DIV div) * mul
        + CARD16(color MOD 256) DIV div + CARDINAL(add)
  END
 END ShrinkColor;

 PROCEDURE SetColor(vp{R.A0}: ViewPortPtr; num{R.D0}: LONGCARD; color{R.D7}: LONGCARD);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
  	MOVEM.L	D2-D4,-(A7)
	MOVE.L	D7,D1
	CLR.W	D1
	SWAP	D1
	MOVE.L	D1,D2
	LSL.W	#8,D2
	MOVE.B	D1,D2
	MOVE.W	D2,D1
	SWAP	D1
	MOVE.W	D2,D1	(* red *)
	MOVE.L	D7,D2
	CLR.B	D2
	MOVE.W	D2,D3
	LSR.W	#8,D3
	MOVE.B	D3,D2
	MOVE.W	D2,D3
	SWAP	D2
	MOVE.W	D3,D2	(* green *)
	MOVE.L	D7,D3
	MOVE.B	D3,D4
	LSL.W	#8,D3
	MOVE.B	D4,D3
	MOVE.W	D3,D4
	SWAP	D3
	MOVE.W	D4,D3	(* blue *)
	MOVEA.L	graphicsBase(A4),A6
	JSR	-852(A6)
	MOVEM.L	(A7)+,D2-D4
	RTS
  END)
 END SetColor;

 PROCEDURE SetPen(color: CARD32);
 BEGIN
  WITH area^ DO
   IF depth > 8 THEN
    SetColor(ADR(s^.viewPort), 1, color)
   ELSE
    IF colors = 0 THEN color:= ShrinkColor(color, area) END;
    SetAPen(rp, color);
    wMask:= color;
    IF bltmode = 96 THEN SetWrMsk(rp, wMask) END
   END
  END
 END SetPen;

 PROCEDURE SetBPen(color: CARD32);
 BEGIN
  WITH area^ DO
   IF depth > 8 THEN
    SetColor(ADR(s^.viewPort), 0, color)
   ELSE
    IF colors = 0 THEN color:= ShrinkColor(color, area) END;
    GraphicsL.SetBPen(rp, color)
   END
  END
 END SetBPen;

 PROCEDURE SetPat(v: CARD8);
 BEGIN
  IF v > 4 THEN v:= 4 END;
  SetAfPen(area^.rp, ADR(Patterns[v]), 1)
 END SetPat;

 PROCEDURE SetPattern(VAR pattern: Pattern);
  VAR
   c{R.D7}, d{R.D6}: INTEGER;
 BEGIN
  WITH area^ DO
   c:= 0; d:= 0;
   WHILE d < 16 DO
    customPat[d]:= pattern[c]; INC(d);
    customPat[d]:= pattern[c]; INC(d);
    INC(c)
   END;
   SetAfPen(rp, ADR(customPat), 3)
  END
 END SetPattern;

 PROCEDURE DrawPixel(x, y: INT16);
 BEGIN
  IGNORE WritePixel(area^.rp, x, y)
 END DrawPixel;

 PROCEDURE DrawLine(x1, y1, x2, y2: INT16);
 BEGIN
  WITH area^ DO
   Move(rp, x1, y1);
   Draw(rp, x2, y2)
  END
 END DrawLine;

 PROCEDURE OpenPoly(x, y: INT16);
 BEGIN
  IGNORE AreaMove(area^.rp, x, y)
 END OpenPoly;

 PROCEDURE AddLine(x, y: INT16);
 BEGIN
  IGNORE AreaDraw(area^.rp, x, y)
 END AddLine;

 PROCEDURE FillPoly;
 BEGIN
  IGNORE AreaEnd(area^.rp)
 END FillPoly;

 PROCEDURE FillRect(x1, y1, x2, y2: INT16);
  VAR
   px1{R.D4}, py1{R.D5}, px2{R.D6}, py2{R.D7}: INT16;
 BEGIN
  px1:= x1; px2:= x2; py1:= y1; py2:= y2;
  IF (px2 <= px1) OR (py2 <= py1) THEN RETURN END;
  RectFill(area^.rp, px1, py1, px2 - 1, py2 - 1)
 END FillRect;

 PROCEDURE FillEllipse(x1, y1, x2, y2: INT16);
  VAR
   rp{R.A2}: RastPortPtr;
   w2{R.D5}, pw2, ph2, phd: CARD32;
   by, ey, ex, x, y, w{R.D4}, h, pw, ph: INT16;
   odd: BOOLEAN;
 BEGIN
   (*$ OverflowChk:= FALSE  RangeChk:= FALSE *)
  IF (x2 <= x1) OR (y2 <= y1) THEN RETURN END;
  rp:= area^.rp;
  pw:= x2 - x1; ph:= y2 - y1;
  odd:= ODD(pw); ph2:= CARD32(ph * ph);
  pw2:= CARD32(pw * pw); phd:= ph2 DIV 2;
  h:= -ph + 1; w:= 1; y:= 0;
  REPEAT
   IF pw = ph THEN
    w2:= ph2 - CARD32(h * h)
   ELSE
    w2:= ((ph2 - CARD32(h * h)) * pw2 + phd) DIV ph2
   END;
   WHILE CARD32(w * w) < w2 DO INC(w) END;
   IF ODD(w) <> odd THEN DEC(w) END;
   x:= x1 + (pw - w) DIV 2;
   by:= y1 + y; ey:= y2 - y - 1; ex:= x + w - 1;
   RectFill(rp, x, by, ex, by);
   IF h <> 0 THEN RectFill(rp, x, ey, ex, ey) END;
   INC(y); INC(h, 2)
  UNTIL h > 0
 END FillEllipse;
  (*$ POP OverflowChk  POP RangeChk *)

 PROCEDURE FillFlood(x, y: INT16; borderCol: CARD32);
 BEGIN
  WITH area^ DO
   IF ReadPixel(rp, x, y) = INT32(borderCol) THEN
    IGNORE Flood(rp, 1, x, y)
   ELSE
    SetOPen(rp, borderCol);
    IGNORE Flood(rp, 0, x, y)
   END
  END
 END FillFlood;

 PROCEDURE SetTextMode(tm: TextModeSet);
  VAR
   style{R.D7}: GraphicsD.FontStyleSet;
 BEGIN
  IF (bold IN tm) THEN
   style:= GraphicsD.FontStyleSet{GraphicsD.bold}
  ELSE
   style:= GraphicsD.FontStyleSet{}
  END;
  IF (shadow IN tm) THEN INCL(style, GraphicsD.underlined) END;
  IF (italic IN tm) THEN INCL(style, GraphicsD.italic) END;
  WITH area^ DO
   IGNORE SetSoftStyle(rp, style, GraphicsD.FontStyleSet{GraphicsD.bold, GraphicsD.italic, GraphicsD.underlined})
  END
 END SetTextMode;

 PROCEDURE SetTextSize(s: INT16);
  VAR
   ta: TextAttr;
 BEGIN
  DEC(s, CARD16(s) DIV 9);
  ta.name:= ADR(fontName);
  ta.ySize:= s;
  ta.style:= GraphicsD.FontStyleSet{};
  ta.flags:= GraphicsD.FontFlagSet{};
  IF NOT(fontGiven) THEN ta.ySize:= 8; INCL(ta.flags, GraphicsD.romFont) END;
  IF (area^.font <> NIL) AND (INT16(area^.font^.ySize) = s) THEN RETURN END;
  WITH area^ DO
   WaitBlit;
   IF fontHd <> NIL THEN
    StripFont(ADR(fontHd^.tf));
    UnLoadSeg(fontHd^.segment);
    fontHd:= NIL; font:= NIL
   ELSIF font <> NIL THEN
    CloseFont(font); font:= NIL
   END;
   IF (diskfontBase <> NIL) AND (fontGiven) AND
      ((w = NIL) OR NOT(windowRefresh IN w^.flags)) THEN
    font:= OpenDiskFont(ADR(ta))
   ELSE
    font:= OpenFont(ADR(ta))
   END;
   IF font = NIL THEN
    ta.name:= ADR(topazName);
    ta.flags:= FontFlagSet{GraphicsD.romFont};
    font:= OpenFont(ADR(ta))
   END;
   ta.ySize:= s;
   IF (font^.ySize <> ta.ySize) AND (diskfontBase <> NIL) AND (diskfontVersion >= 36) THEN
    fontHd:= NewScaledDiskFont(font, ADR(ta));
    IF fontHd <> NIL THEN
     CloseFont(font);
     font:= ADR(fontHd^.tf)
    END
   END;
   SetFont(rp, font)
  END
 END SetTextSize;

 PROCEDURE SetTextPos(x, y: INT16);
 BEGIN
  WITH area^ DO
   tx:= x; ty:= y + INT16(rp^.font^.baseline) + INT16(rp^.font^.ySize DIV 8)
  END
 END SetTextPos;

 PROCEDURE TextWidth(t: StrPtr): INT16;
  VAR
   length{R.D7}: CARD16;
 BEGIN
  length:= StrLength(t);
  RETURN TextLength(area^.rp, t, length)
 END TextWidth;

 PROCEDURE DrawText(t: StrPtr);
  VAR
   length{R.D7}: INT32;
 BEGIN
  length:= StrLength(t);
  WITH area^ DO
   Move(rp, tx, ty);
   Text(rp, t, length);
   INC(tx, TextLength(rp, t, length))
  END
 END DrawText;

 PROCEDURE FillShadow(ma: AreaPtr; sx, sy: INT16;
                      dx, dy, width, height: INT16);
  VAR
   oldPen, oldMd: CARD32;
 BEGIN
  IF sx MOD 16 = 0 THEN
   WITH ma^.bm^ DO
    BltPattern(area^.rp,
     planes[0] + ADDRESS(bytesPerRow * CARD16(sy) + CARD16(sx) DIV 16 * 2),
     dx, dy, dx + width - 1, dy + height - 1, bytesPerRow)
   END
  ELSE
   WITH area^ DO
    oldPen:= GetAPen(rp); oldMd:= GetDrMd(rp);
    SetDrMd(rp, DrawModeSet{complement});
    SetWrMsk(rp, wMask)
   END;
   RectFill(area^.rp, dx, dy, dx + width - 1, dy + height - 1);
   WITH area^ DO
    SetDrMd(rp, DrawModeSet{});
    SetAPen(rp, 0);
    SetWrMsk(rp, uMask)
   END;
   WITH ma^.bm^ DO
    BltTemplate(
     planes[0] + ADDRESS(bytesPerRow * CARD16(sy) + CARD16(sx) DIV 16 * 2),
     sx MOD 16, bytesPerRow, area^.rp, dx, dy, width, height)
   END;
   WITH area^ DO
    SetDrMd(rp, DrawModeSet{complement});
    SetAPen(rp, oldPen);
    SetWrMsk(rp, wMask)
   END;
   RectFill(area^.rp, dx, dy, dx + width - 1, dy + height - 1);
   WITH area^ DO
    SetDrMd(rp, CAST(DrawModeSet, CHR(oldMd MOD 256)));
    SetWrMsk(rp, uMask)
   END
  END
 END FillShadow;

 PROCEDURE DrawShadow(ma: AreaPtr; sx, sy: INT16;
                      dx, dy, width, height: INT16);
 BEGIN
  WITH ma^.bm^ DO
   BltTemplate(
    planes[0] + ADDRESS(bytesPerRow * CARD16(sy) + CARD16(sx) DIV 16 * 2),
    sx MOD 16, bytesPerRow, area^.rp, dx, dy, width, height)
  END
 END DrawShadow;

 PROCEDURE TrueToHam(true, chunky: ADDRESS; palette: PalettePtr;
            bitPerPix, width, zw: INTEGER; ham8: BOOLEAN);
  TYPE
   ShrtPtr = POINTER TO SHORTCARD;
  VAR
   read{R.A3}, write{R.A2}: ShrtPtr;
   plt{R.A6}: PalettePtr;
   red, green, blue, rerr, gerr, berr, rcur, gcur, bcur: INTEGER;
   enew, emod, nval, hval, rmid, gmid, bmid, rwt, gwt, bwt: INTEGER;
   x: INTEGER;
 BEGIN
  read:= true; write:= chunky; plt:= palette;
  rcur:= 4096; gcur:= 4096; bcur:= 4096;
  rwt:= 0; gwt:= 0; bwt:= 0;
  rmid:= 4096; gmid:= 4096; bmid:= 4096;
  x:= zw;
  WHILE width > 0 DO
   DEC(width); DEC(x);
   IF bitPerPix > 16 THEN
    INC(read);
    red:= read^; INC(read);
    green:= read^; INC(read);
    blue:= read^; INC(read);
    IF x > 0 THEN DEC(read, 4) END
   ELSIF bitPerPix > 8 THEN
    red:= (read^ DIV 4) * 8 MOD 256;
    green:= (read^ MOD 4) * 64; INC(read);
    INC(green, (read^ DIV 32) * 8);
    blue:= (read^ MOD 32) * 8; INC(read);
    IF x > 0 THEN DEC(read, 2) END
   ELSE
    nval:= plt^[read^]; INC(read);
    red:= nval DIV 65536;
    green:= (nval MOD 65536) DIV 256;
    blue:= nval MOD 256;
    IF x > 0 THEN DEC(x) END
   END;
   IF x <= 0 THEN x:= zw END;
   rerr:= ABS(rcur - red); INC(rwt, rerr);
   IF rerr >= 128 THEN rmid:= red ELSE rmid:= (rmid + red) DIV 2 END;
   gerr:= ABS(gcur - green); INC(gwt, gerr);
   IF gerr >= 128 THEN gmid:= green ELSE gmid:= (gmid + green) DIV 2 END;
   berr:= ABS(bcur - blue); INC(bwt, berr);
   IF berr >= 128 THEN bmid:= blue ELSE bmid:= (bmid + blue) DIV 2 END;
   nval:= (red DIV 64) * 16 + (green DIV 64) * 4 + (blue DIV 64);
   IF NOT(ham8) THEN nval:= Color64To16[nval] END;
   WITH rgb32.rgb[nval] DO
    enew:= ABS(red - INT16(rc DIV 16777216))
         + ABS(green - INT16(gc DIV 16777216))
         + ABS(blue - INT16(bc DIV 16777216))
   END;
   IF (rwt + rerr * 2 > gwt + gerr * 2) AND (rwt + rerr * 2 > bwt + berr * 2) THEN (* correct red *)
    IF ham8 THEN hval:= rmid DIV 4 + 128 ELSE hval:= rmid DIV 16 + 32 END;
    rcur:= rmid; emod:= gerr + berr - rwt; rwt:= 0
   ELSIF (gwt + gerr * 2 > bwt + berr * 2) THEN (* correct green *)
    IF ham8 THEN hval:= gmid DIV 4 + 192 ELSE hval:= gmid DIV 16 + 48 END;
    gcur:= gmid; emod:= rerr + berr - gwt; gwt:= 0
   ELSE (* correct blue *)
    IF ham8 THEN hval:= bmid DIV 4 + 64 ELSE hval:= bmid DIV 16 + 16 END;
    bcur:= bmid; emod:= rerr + gerr - bwt; bwt:= 0
   END;
   IF (enew * 4 < emod) THEN (* new color *)
    IF ham8 THEN
     rcur:= (red DIV 64) * 85;
     gcur:= (green DIV 64) * 85;
     bcur:= (blue DIV 64) * 85
    ELSE
     rcur:= (red DIV 128); rcur:= rcur * 256 - rcur;
     gcur:= (green DIV 128); gcur:= gcur * 256 - gcur;
     bcur:= (blue DIV 128); bcur:= bcur * 256 - bcur
    END;
    rwt:= 0; bwt:= 0; gwt:= 0;
    rmid:= red; gmid:= green; bmid:= blue;
    write^:= nval
   ELSE (* Modify color *)
    write^:= hval
   END;
   INC(write)
  END
 END TrueToHam;

 PROCEDURE TrueToClut(true{R.A0}, chunky{R.A1}, palette{R.A6}: ADDRESS;
            bpp{R.D0}, width{R.D1}, div{R.D2}, mul{R.D3}, add{R.D4}, zw{R.D5}: INTEGER);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVEM.L	D2-D7/A2,-(A7)
	SWAP	D4
	SWAP	D3
	MOVE.W	D5,D4		(* Zoom width *)
	MOVE.W	D5,D3
	SWAP	D4		(* Add *)
	SWAP	D3		(* mul *)
	SUBQ.W	#1,D1		(* DEC(width) *)
nextw:  CMP.W	#16,D0		(* IF bitPerPix < 16 *)
	BGT	tc		(* THEN *)
	CMP.W	#8,D0
	BGT	tcw
clut:	MOVEQ	#0,D7
	MOVE.B	(A0)+,D7	(* Color number *)
	LSL.W	#2,D7		(* Long align *)
	MOVEQ	#0,D5
	MOVE.B	3(A6,D7.W),D5	(* blue *)
	MOVEQ	#0,D6
	MOVE.B	2(A6,D7.W),D6	(* green *)
	MOVE.B	1(A6,D7.W),D7	(* red *)
	AND.W	#$FF,D7
	BRA	comp
tcw:	MOVEQ	#0,D7
	MOVE.W	(A0)+,D7	(* 0 | red:5 | green:5 | blue:5 *)
	MOVE.L	D7,D5
	AND.W	#$1F,D5		(* blue:5 *)
	LSL.W	#3,D5		(* blue *)
	LSR.W	#5,D7		(* red:5 | green:5 *)
	MOVE.L	D7,D6
	AND.W	#$1F,D6		(* green:5 *)
	LSL.W	#3,D6		(* green *)
	LSR.W	#5,D7		(* red:5 *)
	LSL.W	#3,D7		(* red *)
	BRA	comp		(* ELSE *)
tc:	MOVEQ	#0,D5
	MOVE.L	(A0)+,D7	(* 0 | red | green | blue *)
	MOVEQ	#0,D6
	MOVE.B	D7,D5		(* blue *)
	MOVE.W	D7,D6		(* green | blue *)
	LSR.W	#8,D6		(* green *)
	CLR.W	D7
	SWAP	D7		(* red *)
	AND.W	#$FF,D7
comp:	TST.W	D4
	BGE	std
	LSR.W	#4,D7		(* red / 16 *)
	LSR.W	#6,D6		(* green / 64 *)
	AND.W	#$FC,D7		(* red / 16 MOD 4 = red / 64 * 4 *)
	LSR.W	#6,D5		(* blue / 64 *)
	ADD.W	D6,D7
	LSL.W	#2,D7
	ADD.W	D5,D7
	LEA	Color64To16(PC),A2
	MOVE.B	0(A2,D7.W),D7
	BRA copy
std:	DIVU.W	D2,D7		(* red / div *)
	DIVU.W	D2,D6		(* green / div *)
	DIVU.W	D2,D5		(* blue / div *)
	MULU.W	D3,D7		(* (red / div) * mul *)
	ADD.W	D6,D7		(* (red / div) * mul + (green / div) *)
	MULU.W	D3,D7		(* ((red / div) * mul + (green / div)) * mul *)
	ADD.W	D5,D7		(* ... + (blue / div) *)
	ADD.W	D4,D7		(* ... + (blue / div) + add *)
copy:	MOVE.B	D7,(A1)+	(* write^:= ...; INC(write) *)
	SWAP	D4		(* zoom *)
	SUBQ.W	#1,D4
	BGT	rev
	SWAP	D3
	MOVE.W	D3,D4
	SWAP	D3
cont:	SWAP	D4
	DBRA	D1,nextw	(* END *)
	MOVEM.L	(A7)+,D2-D7/A2
	RTS
rev:	MOVE.W	D0,D7
	LSR.W	#3,D7
	NEG.W	D7
	LEA	0(A0,D7.W),A0
	BRA	cont
  END)
(* 32bit case only:
  TYPE
   ShrtPtr = POINTER TO SHORTCARD;
  VAR
   read{R.A2}, write{R.A3}: ShrtPtr;
   red{R.D7}, green{R.D6}, blue{R.D5}: INTEGER;
 BEGIN
  rem:= 255 / (mul - 1);
  read:= true; write:= chunky;
  WHILE width > 0 DO
   DEC(width);
   INC(read, gap);
   red:= read^; INC(read);
   green:= read^; INC(read);
   blue:= read^; INC(read);
   write^:= ((red / div) * mul + (green / div)) * mul + (blue / div) + add;
   INC(write)
  END
*)
 END TrueToClut;

 PROCEDURE TrueToDither(true{R.A0}, chunky{R.A1}, palette{R.A6}: ADDRESS;
            bpp{R.D0}, width{R.D1}, div{R.D2}, mul{R.D3}, add{R.D4}, zw{R.D5}: INTEGER);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVEM.L	D2-D7/A2-A5,-(A7)
	SWAP	D2
	SWAP	D3
	MOVE.W	D5,D2
	MOVE.W	D5,D3
	SWAP	D2
	SWAP	D3
	SUBA.L	A2,A2
	MOVEA.W	#255,A3		(** CONST 255 *)
	MOVEA.W	D0,A2		(** bpp -> A2 *)
	MOVE.W	A3,D0		(** 255 *)
	MOVEQ	#0,D7
	MOVE.W	D3,D7		(** mul *)
	SUBQ.W	#1,D7		(** mul - 1 *)
	DIVU.W	D7,D0		(** 255 / (mul - 1) *)
	SWAP	D4
	MOVE.W	D0,D4		(* rem:= 255 / (mul - 1) *)
	MOVEQ	#0,D7		(* red:= 0 *)
	MOVEQ	#0,D6		(* green:= 0 *)
	MOVEQ	#0,D5		(* blue:= 0 *)
	SUBQ.W	#1,D1		(* DEC(width) *)
nextw:	CMPA.W	#16,A2		(* IF bitPerPix < 16 *)
	BGT	tc		(* THEN *)
	CMPA.W	#8,A2
	BGT	tcw
clut:	MOVEQ	#0,D0
	MOVE.B	(A0)+,D0
	LSL.W	#2,D0
	MOVE.L	0(A6,D0.W),D0
	SWAP	D0
	ADD.W	D0,D7
	BGE	rdp
	MOVEQ	#0,D7
rdp:	CMP.W	A3,D7
	BLE	rdk
	MOVE.W	A3,D7
rdk:	CLR.W	D0
	ROL.L	#8,D0
	ADD.W	D0,D6
	BGE	grp
	MOVEQ	#0,D6
grp:	CMP.W	A3,D6
	BLE	grk
	MOVE.W	A3,D6
grk:	CLR.W	D0
	ROL.L	#8,D0
	ADD.W	D0,D5
	BGE	brp
	MOVEQ	#0,D5
brp:	CMP.W	A3,D5
	BLE	bok
	MOVE.W	A3,D5
	BRA	bok
tcw:	MOVEQ	#0,D0
	MOVE.W	(A0)+,D0	(** 0:17 | red:5 | green:5 | blue:5 *)
	LSL.L	#6,D0		(** 0:11 | red:5 | green:5 | blue:5 | 0:6 *)
	SWAP	D0		(** green:5 | blue:5 | 0:17 | red:5 *)
	LSL.W	#3,D0		(** green:5 | blue:5 | 0:14 | red:8 *)
	ADD.W	D0,D7
	BGE	rp
	MOVEQ	#0,D7
rp:	CMP.W	A3,D7
	BLE	rk
	MOVE.W	A3,D7
rk:	CLR.B	D0
	ROL.L	#5,D0		(** blue:5 | ... | green:5 *)
	LSL.W	#3,D0		(** blue:5 | ... | green:8 *)
	ADD.W	D0,D6
	BGE	gp
	MOVEQ	#0,D6
gp:	CMP.W	A3,D6
	BLE	gk
	MOVE.W	A3,D6
gk:	CLR.B	D0
	ROL.L	#8,D0
	ADD.W	D0,D5
	BGE	bp
	MOVEQ	#0,D5
bp:	CMP.W	A3,D5
	BLE	bok
	MOVE.W	A3,D5
	BRA	bok
tc:	MOVE.L	(A0)+,D0
	SWAP	D0
	AND.W	#$FF,D0
	ADD.W	D0,D7		(* INC(red, read^); INC(read) *)
	BGE	rpos		(* IF red < 0 THEN *)
	MOVEQ	#0,D7		(* red:= 0 *)
rpos:	CMP.W	A3,D7		(* IF red > 255 *)
	BLE	rok		(* THEN *)
	MOVE.W	A3,D7		(* red:= 255 *)
rok:	CLR.W	D0
	ROL.L	#8,D0		(** green *)
	ADD.W	D0,D6		(* INC(green, read^); INC(read) *)
	BGE	gpos		(* IF green < 0 THEN *)
	MOVEQ	#0,D6		(* green:= 0 *)
gpos:	CMP.W	A3,D6		(* IF green > 255 *)
	BLE	gok		(* THEN *)
	MOVE.W	A3,D6		(* green:= 255 *)
gok:	CLR.W	D0
	ROL.L	#8,D0		(** blue *)
	ADD.W	D0,D5		(* INC(blue, read^); INC(read) *)
	BGE	bpos		(* IF blue < 0 THEN *)
	MOVEQ	#0,D5		(* blue:= 0 *)
bpos:	CMP.W	A3,D5		(* IF blue > 255 *)
	BLE	bok		(* THEN *)
	MOVE.W	A3,D5		(* blue:= 255 *)
bok:	MOVE.L	D7,D0		(** red *)
	DIVU.W	D2,D0		(** red / div *)
	MOVEA.W	D0,A4
	MULU.W	D4,D0		(** (red / div) * rem *)
	SUB.W	D0,D7		(* DEC(red, (red / div) * rem *)
	MOVE.W	A4,D0		(** (red / div) *)
	MULU.W	D3,D0		(** (red / div) * mul *)
	MOVE.W	D0,A5
	MOVE.L	D6,D0		(** green *)
	DIVU.W	D2,D0		(** green / div *)
	MOVEA.W	D0,A4
	MULU.W	D4,D0		(** (green / div) * rem *)
	SUB.W	D0,D6		(* DEC(green, (green / div) * rem *)
	MOVE.W	A4,D0		(** (green / div) *)
	ADD.W	A5,D0		(** (red / div) * mul + (green / div) *)
	MULU.W	D3,D0		(** (...) * mul *)
	MOVEA.W	D0,A5
	MOVE.L	D5,D0		(** blue *)
	DIVU.W	D2,D0		(** blue / div *)
	MOVEA.W	D0,A4
	MULU.W	D4,D0		(** (blue / div) * rem *)
	SUB.W	D0,D5		(* DEC(blue, (blue / div) * rem) *)
	MOVE.W	A4,D0		(** (blue / div) *)
	ADD.W	A5,D0		(** (...) * mul + (blue / div) *)
	SWAP	D4		(** add *)
	TST.W	D4
	BGE	ad
	LEA	Color64To16(PC),A4
	MOVE.B	0(A4,D0.W),D0
	BRA	copy
ad:	ADD.W	D4,D0		(** (...) * mul + (blue / div) + add *)
copy:	MOVE.B	D0,(A1)+	(* write^:= %; INC(write) *)
	SWAP	D2		(* zoom *)
	SUBQ.W	#1,D2
	BGT	rev
	SWAP	D3
	MOVE.W	D3,D2
	SWAP	D3
cont:	SWAP	D2
	SWAP	D4		(** rem *)
	DBRA	D1,nextw	(* END *)
	MOVEM.L	(A7)+,D2-D7/A2-A5
	RTS
rev:	MOVE.W	A2,D0
	LSR.W	#3,D0
	NEG.W	D0
	LEA	0(A0,D0.W),A0
	BRA	cont
  END)
(* 32bit case only:
  TYPE
   ShrtPtr = POINTER TO SHORTCARD;
  VAR
   read{R.A2}, write{R.A3}: ShrtPtr;
   red{R.D7}, green{R.D6}, blue{R.D5}, rem{R.D4}: INTEGER;
 BEGIN
  rem:= 255 / (mul - 1);
  read:= true; write:= chunky;
  red:= 0; green:= 0; blue:= 0;
  WHILE width > 0 DO
   DEC(width);
   INC(read, gap);
   INC(red, read^); INC(read);
   IF red < 0 THEN red:= 0 ELSIF red > 255 THEN red:= 255 END;
   INC(green, read^); INC(read);
   IF green < 0 THEN green:= 0 ELSIF green > 255 THEN green:= 255 END;
   INC(blue, read^); INC(read);
   IF blue < 0 THEN blue:= 0 ELSIF blue > 255 THEN blue:= 255 END;
   write^:= ((red / div) * mul + (green / div)) * mul + (blue / div) + add;
   DEC(red, (red / div) * rem);
   DEC(green, (green / div) * rem);
   DEC(blue, (blue / div) * rem);
   INC(write)
  END
*)
 END TrueToDither;

 PROCEDURE PixmapToChunky(pixmap{R.A0}, chunky{R.A1}: ADDRESS;
                          bitPerPix{R.D0}, width{R.D1}: INT16);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVEM.L	D5-D7,-(A7)
  	MOVEQ	#16,D5		(* CONST 16 *)
  	MOVEQ	#3,D2
	MOVEQ	#0,D6		(* bitIn:= 0 *)
	MOVEQ	#0,D7		(* data:= 0 *)
	DBRA	D1,nextw	(* DEC(w) *)
	BRA	rts		(* WHILE w >= 0 *)
nextw:	CMP.W	D0,D6		(* IF bitIn - bitPerPix < 0 *)
	BGE	endw		(* DO *)
	SWAP	D7		(* data:= SHIFT(data, 16) *)
	MOVE.W	(A0)+,D7	(* + read^; INC(read, 2) *)
	ADD.W	D5,D6		(* INC(bitIn, 16) *)
endw:	SUB.W	D0,D6		(* DEC(bitIn, bitPerPix) *)
	ROR.L	D6,D7
	MOVE.B	D7,(A1)+
	CLR.B	D7
	ROL.L	D6,D7
	DBRA	D1,nextw	(* END *)
rts:	MOVEM.L	(A7)+,D5-D7
	RTS
  END)
(*
  VAR
   read{R.A2}: POINTER TO CARDINAL;
   write{R.A3}: POINTER TO SHORTCARD;
   data{R.D7}: LONGCARD;
   bitIn{R.D6}, w{R.D5}: INT16;
   out{R.D4}: SHORTCARD;
 BEGIN
  w:= width;
  read:= pixmap; write:= chunky;
  bitIn:= 0; data:= 0;
  WHILE w > 0 DO
   DEC(w);
   WHILE bitIn < bitPerPix DO
    data:= SHIFT(data, 16) + read^;
    INC(read, 2); INC(bitIn, 16)
   END;
   DEC(bitIn, bitPerPix);
   out:= SHIFT(data, -bitIn);
   DEC(data, SHIFT(LONGCARD(out), bitIn));
   write^:= out; INC(write)
  END
*)
 END PixmapToChunky;

 PROCEDURE CustomC2P(chunky{R.A0}: ADDRESS; bm{R.A1}: BitMapPtr; depth{R.D0}, width{R.D1}: LONGINT);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
  	MOVEM.L	D2-D7/A2-A4,-(A7)
  	MOVEA.L	D1,A4				(* width *)
  	MOVE.L	#32,D3				(* CONST 32 *)
  	MOVE.L	#24,D2				(* CONST 24 *)
	SUBQ.W	#1,D0				(* d:= depth; DEC(d) *)
nextd:	MOVEA.L	A0,A3				(* peek:= chunky *)
	MOVE.W	D0,D1				(** d *)
	LSL.W	#2,D1				(** d.L *)
	MOVEA.L	A1,A2				(** bm *)
	MOVEA.L	BitMap.planes(A2,D1.W),A2	(* poke:= bm^.planes[d] *)
	MOVE.L	D0,D4				(** d *)
	ADD.W	D2,D4				(* bitIn:= 24 + d *)
	MOVE.L	(A3)+,D6			(* in:= peek^; INC(peek, 4) *)
	MOVEQ	#31,D5				(* bitOut:= 31 *)
	MOVEQ	#0,D7				(* out:= {} *)
	MOVE.W	A4,D1				(* x:= width *)
	DBRA	D1,nextx			(* DEC(x) *)
	BRA	nextd
nextx:	BTST.L	D4,D6				(* IF bitIn IN in *)
	BEQ	L01				(* THEN *)
	BSET.L	D5,D7				(* INCL(out, bitOut) *)
L01:	SUBQ.W	#8,D4				(* DEC(bitIn, 8 *)
	BGE.S	L02				(* IF bitIn < 0 THEN *)
	MOVE.L	(A3)+,D6			(* in:= peek^; INC(peek, 4) *)
	ADD.W	D3,D4				(* INC(bitIn, 32) *)
L02:	DBRA	D5,L03				(* DEC(bitOut); IF bitOut < 0 THEN *)
	MOVE.L	D7,(A2)+			(* poke^:= out; INC(poke, 4) *)
	MOVEQ	#31,D5				(* bitOut:= 31 *)
	MOVEQ	#0,D7				(* out:= {} *)
L03:	DBRA	D1,nextx
	DBRA	D0,nextd
	MOVEM.L	(A7)+,D2-D7/A2-A4
	RTS
  END)
(*
  VAR
   peek{R.A3}, poke{R.A2}: POINTER TO LONGSET;
   x{R.A0}, d{R.A6}, bitIn{R.D4}, bitOut{R.D5}: LONGINT;
   in{R.D6}, out{R.D7}: LONGSET;
 BEGIN
  d:= depth;
  WHILE d > 0 DO
   DEC(d);
   peek:= chunky; poke:= bm^.planes[d];
   bitIn:= 24 + d; in:= peek^;
   bitOut:= 31; out:= LONGSET{};
   x:= width;
   WHILE x > 0 DO
    DEC(x);
    IF bitIn IN in THEN INCL(out, bitOut) END;
    DEC(bitIn, 8); DEC(bitOut);
    IF bitIn < 0 THEN INC(peek, 4); in:= peek^; INC(bitIn, 32) END;
    IF bitOut < 0 THEN poke^:= out; INC(poke, 4); bitOut:= 31; out:= LONGSET{} END
   END
  END
*)
 END CustomC2P;

 PROCEDURE WriteChunkyPixels(gfx{R.A6}: ADDRESS; rp{R.A0}: RastPortPtr;
           xstart{R.D0}, ystart{R.D1}, xstop{R.D2}, ystop{R.D3}: INT32;
           array{R.A2}: ADDRESS; bytesperrow{R.D4}: CARD32);
 CODE -1056;

 PROCEDURE ChunkyToLine(dst: RastPortPtr; sx, sw, dx, dy, dw: INT16;
                        chunky: ADDRESS; tmp: RastPortPtr);
  VAR
   bm: BitMapPtr;
 BEGIN
  IF (graphicsVersion >= 36) AND (tmp <> NIL) THEN
   IGNORE WritePixelLine8(dst, dx, dy, dw, chunky + ADDRESS(sx), tmp)
  ELSIF (graphicsVersion >= 40) THEN
   WriteChunkyPixels(graphicsBase, dst, sx, dy, sx + dw - 1, dy, chunky, sw)
  ELSE
   bm:= tmp^.bitMap;
   CustomC2P(chunky, bm, dst^.bitMap^.depth, sw);
   BltBitMapRastPort(bm, sx, 0, dst, dx, dy, dw, 1, 0C0H)
  END
 END ChunkyToLine;

 PROCEDURE TrueToTrue(src{R.A2}, dst{R.A3}, rgb32{R.D4}, palette{R.D5}: ADDRESS;
                      bitPerPix{R.D2}, md{R.D3}, zw{R.D6}: INT16): ADDRESS;
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVEM.L	D7/A2-A5,-(A7)
	MOVEA.L	D4,A4		(* rgb32 *)
	MOVEA.L	D5,A5		(* palette *)
	MOVEQ	#0,D7
	MOVEQ	#0,D1
	MOVE.W	D6,D0
fill:	MOVE.B	D7,0(A3,D1.W)
	ADDQ.W	#1,D1
	SUBQ.W	#1,D0
	BGT	fill
	MOVE.W	D6,D0
	ADDQ.W	#1,D7
	CMP.W	D3,D7
	BLT	fill
	MOVE.W	D3,(A4)		(* rgb32.count:= md *)
	CMP.W	#256,D3		(* IF md < 256 *)
	BGE	L01		(* THEN *)
	MOVE.W	D3,D0
	MULU.W	#12,D0
	CLR.L	4(A4,D0.L)	(* rgb32.rgb[md].rc:= 0 *)
L01:	MOVEQ	#0,D7		(* FOR d:= 0 TO md - 1 DO *)
next:	MOVE.W	D7,D0
	LSL.W	#2,D0
	MOVE.W	D0,D1
	ADD.W	D1,D0
	ADD.W	D1,D0		(* d * 12 -> D0 *)
	LEA	4(A4,D0.W),A0	(* WITH rgb32.rgb[d] DO *)
	CMP.W	#16,D2		(* IF bitPerPix <= 16 *)
	BGT	tcl		(* THEN *)
	CMP.W	#8,D2		(* IF bitPerPix <= 8 *)
	BGT	tcw		(* THEN *)
clut:	MOVEQ	#0,D0
	MOVE.B	(A2)+,D0
	LSL.W	#2,D0
	MOVE.L	0(A5,D0.W),D0
	BRA	comp
tcw:	MOVEQ	#0,D1
	MOVE.W	(A2)+,D0
	MOVE.W	D0,D1
	AND.W	#$7C00,D1
	LSL.L	#8,D1
	ADD.L	D1,D1
	MOVE.W	D0,D1
	AND.W	#$3E0,D1
	LSL.W	#6,D1
	MOVE.B	D0,D1
	AND.B	#$1F,D1
	LSL.B	#3,D1
	MOVE.L	D1,D0
	BRA	comp
tcl:	MOVE.L	(A2)+,D0	(** A|R|G|B *)
comp:	MOVE.B	D0,D1		(** *|*|*|B *)
	LSL.W	#8,D1		(** *|*|B|0 *)
	MOVE.B	D0,D1		(** *|*|B|B *)
	MOVEA.W	D1,A1
	SWAP	D1		(** B|B|*|* *)
	MOVE.W	A1,D1		(** B|B|B|B *)
	MOVE.L	D1,8(A0)	(* bc:= blue *)
	LSR.L	#8,D0		(** 0|A|R|G *)
	MOVE.B	D0,D1		(** *|*|*|G *)
	LSL.W	#8,D1		(** *|*|G|0 *)
	MOVE.B	D0,D1		(** *|*|G|G *)
	MOVEA.W	D1,A1
	SWAP	D1		(** G|G|*|* *)
	MOVE.W	A1,D1		(** G|G|G|G *)
	MOVE.L	D1,4(A0)	(* gc:= green *)
	LSR.W	#8,D0		(** 0|A|0|R *)
	MOVE.B	D0,D1		(** *|*|*|R *)
	LSL.W	#8,D1		(** *|*|R|0 *)
	MOVE.B	D0,D1		(** *|*|R|R *)
	MOVEA.W	D1,A1
	SWAP	D1		(** R|R|*|* *)
	MOVE.W	A1,D1		(** R|R|R|R *)
	MOVE.L	D1,(A0)		(* rc:= red *)
	ADDQ.W	#1,D7
	CMP.W	D3,D7
	BLT	next		(* END FOR *)
	MOVE.L	A2,D0		(* RETURN src *)
	MOVEM.L	(A7)+,D7/A2-A5
	RTS
  END)
 END TrueToTrue;

 PROCEDURE BlitImage(rp: RastPortPtr; depth: INT16; image: ImagePtr;
                     sx, sy, dx, dy, width, height: INT16);
  TYPE
   ShrtPtr = POINTER TO SHORTCARD;
  VAR
   tmprp: RastPort;
   tmpbm: BitMap;
   Convert: PROCEDURE(ADDRESS{R.A0}, ADDRESS{R.A1}, ADDRESS{R.A6},
    INTEGER{R.D0}, INTEGER{R.D1}, INTEGER{R.D2}, INTEGER{R.D3}, INTEGER{R.D4}, INTEGER{R.D5});
   read, write, chunky: ShrtPtr;
   planePtr, nxtRow: ADDRESS;
   step, bpr: CARD16;
   bitPerPix, bytePerRow: CARD16;
   x, y, dh, dw, d, md: INT16;
   div, mul, add: INTEGER;
 BEGIN
  bitPerPix:= image^.bitPerPix;
  bytePerRow:= image^.bytePerRow;
  IF bitPerPix >= 16 THEN (* True color source *)
   IF depth > 8 THEN (* True color to true color *)
    tmprp:= rp^;
    tmprp.layer:= NIL;
    InitBitMap(tmpbm, depth, 256, 1);
    tmprp.bitMap:= ADR(tmpbm);
    AssertTmpSize(256, 8);
    planePtr:= mainTmpRas.rasPtr;
    IF planePtr = NIL THEN memLow:= TRUE; RETURN END;
    FOR d:= 0 TO 7 DO
     tmpbm.planes[d]:= planePtr; INC(planePtr, 32)
    END;
    bitPerPix:= bitPerPix DIV 8;
    dw:= image^.zw;
    IF dw <= 0 THEN dw:= 1 END;
    chunky:= AllocMem(dw * 256, MemReqSet{});
    IF chunky <> NIL THEN
(*
     read:= chunky;
     FOR d:= 0 TO 255 DO read^:= d; INC(read) END;
*)
     nxtRow:= image^.data + LONGINT(CARD16(sy) * bytePerRow) + LONGINT(sx * INT16(bitPerPix));
     bitPerPix:= bitPerPix * 8;
     rgb32.num:= 0;
     y:= 0;
     LOOP
      dh:= image^.zh;
      REPEAT
       IF y >= height THEN EXIT END;
       read:= nxtRow;
       x:= 0;
       WHILE x + dw <= width DO
        md:= (width - x) / dw; IF md > 256 THEN md:= 256 END;

        read:= TrueToTrue(read, chunky, ADR(rgb32), image^.palette, bitPerPix, md, dw);
 (*
        rgb32.count:= md;
        IF md < 256 THEN rgb32.rgb[md].rc:= 0 END;
        FOR d:= 0 TO md - 1 DO
         INC(read); (* Alpha, if present *)
         WITH rgb32.rgb[d] DO
          rc:= SHIFT(LONGCARD(read^), 24) + 7FFFFFH; INC(read);
          gc:= SHIFT(LONGCARD(read^), 24) + 7FFFFFH; INC(read);
          bc:= SHIFT(LONGCARD(read^), 24) + 7FFFFFH; INC(read)
         END
        END;
*)
        LoadRGB32(ADR(s^.viewPort), ADR(rgb32));
        ChunkyToLine(rp, 0, md * dw, dx + x, dy + y, md * dw, chunky, ADR(tmprp));
        INC(x, md * dw)
       END;
       INC(y); DEC(dh)
      UNTIL dh <= 0;
      INC(nxtRow, bytePerRow)
     END;
     FreeMem(chunky, dw * 256)
    ELSE
     memLow:= TRUE
    END
   ELSE
    step:= SHIFT(((CARD16(width) + 15) DIV 16), 4);
    tmprp:= rp^;
    tmprp.layer:= NIL;
    bpr:= step DIV 8;
    InitBitMap(tmpbm, depth, bpr, 1);
    tmprp.bitMap:= ADR(tmpbm);
    AssertTmpSize(step, depth);
    planePtr:= mainTmpRas.rasPtr;
    IF planePtr = NIL THEN memLow:= TRUE; RETURN END;
    FOR d:= 0 TO depth - 1 DO
     tmpbm.planes[d]:= planePtr; INC(planePtr, bpr)
    END;
    chunky:= AllocMem(step, MemReqSet{});
    IF chunky <> NIL THEN (* ready *)
     bitPerPix:= bitPerPix DIV 8;
     nxtRow:= image^.data + LONGINT(CARD16(sy) * bytePerRow) + LONGINT(CARD16(sx) * bitPerPix);
     bitPerPix:= bitPerPix * 8;
     IF area^.hamMode THEN
      y:= 0;
      LOOP
       dh:= image^.zh;
       REPEAT
        IF y >= height THEN EXIT END;
        TrueToHam(nxtRow, chunky, image^.palette, bitPerPix, width, image^.zw, (depth = 8));
        ChunkyToLine(rp, 0, width, dx, dy + y, width, chunky, ADR(tmprp));
        INC(y); DEC(dh)
       UNTIL dh <= 0;
       INC(nxtRow, bytePerRow)
      END
     ELSE
      mul:= area^.mul; add:= area^.add;
      div:= (255 + mul) / mul;
      IF dither THEN Convert:= TrueToDither ELSE Convert:= TrueToClut END;
      y:= 0;
      LOOP
       dh:= image^.zh;
       REPEAT
        IF y >= height THEN EXIT END;
        Convert(nxtRow, chunky, image^.palette, bitPerPix, width, div, mul, add, image^.zw);
        ChunkyToLine(rp, 0, width, dx, dy + y, width, chunky, ADR(tmprp));
        INC(y); DEC(dh)
       UNTIL dh <= 0;
       INC(nxtRow, bytePerRow)
      END
     END;
     FreeMem(chunky, step)
    ELSE
     memLow:= TRUE
    END
   END
  ELSE (* Clut -> Clut *)
   IF (image^.zw <= 1) AND (image^.zh <= 1) THEN
    IF (bitPerPix = 1) AND (depth = 1) THEN
     InitBitMap(tmpbm, 1, bytePerRow * 8, image^.height);
     IF chip IN TypeOfMem(image^.data) THEN
      tmpbm.planes[0]:= image^.data
     ELSE
      AssertTmpSize(SHIFT(bytePerRow, 3), image^.height);
      IF mainTmpRas.rasPtr = NIL THEN memLow:= TRUE; RETURN END;
      tmpbm.planes[0]:= mainTmpRas.rasPtr;
      CopyMemQuick(image^.data, tmpbm.planes[0], bytePerRow * CARD16(image^.height))
     END;
     BltBitMapRastPort(ADR(tmpbm), sx, sy, rp, dx, dy, width, height, 0C0H);
     RETURN
    ELSIF (bitPerPix = 8) AND (graphicsVersion >= 40) THEN
     WriteChunkyPixels(graphicsBase, rp, dx, dy, dx + width - 1, dy + height - 1, image^.data + LONGINT(CARD16(sy) * bytePerRow) + LONGINT(sx), bytePerRow);
     RETURN
    END
   END;
 (* Try line by line copy *)
   dw:= image^.zw;
   IF dw < 1 THEN dw:= 1 END;
   step:= SHIFT(((CARD16(image^.width * dw) + 15) DIV 16), 4);
   tmprp:= rp^;
   tmprp.layer:= NIL;
   bpr:= step DIV 8;
   IF depth > 8 THEN md:= 7 ELSE md:= depth - 1 END;
   InitBitMap(tmpbm, md + 1, bpr, 1);
   tmprp.bitMap:= ADR(tmpbm);
   AssertTmpSize(bpr, md + 1);
   planePtr:= mainTmpRas.rasPtr;
   IF planePtr = NIL THEN memLow:= TRUE; RETURN END;
   FOR d:= 0 TO md DO
    tmpbm.planes[d]:= planePtr; INC(planePtr, bpr)
   END;
   chunky:= AllocMem(step, MemReqSet{});
   IF chunky = NIL THEN memLow:= TRUE; RETURN END;
   nxtRow:= image^.data + LONGINT(CARD16(sy) * bytePerRow);
   y:= 0;
   LOOP
    dh:= image^.zh;
    REPEAT
     IF y >= height THEN EXIT END;
     IF bitPerPix = 8 THEN
      CopyMem(nxtRow, chunky, step)
     ELSE
      PixmapToChunky(nxtRow, chunky, bitPerPix, image^.width)
     END;
     IF dw > 1 THEN (* Expand horizontally *)
      read:= chunky; INC(read, image^.width);
      write:= chunky; INC(write, image^.width * dw);
      FOR x:= 0 TO width - 1 DO
       DEC(read);
       d:= dw;
       REPEAT
        DEC(write);
        write^:= read^;
        DEC(d)
       UNTIL d <= 0
      END
     END;
     ChunkyToLine(rp, sx, image^.width * dw, dx, dy + y, width, chunky, ADR(tmprp));
     INC(y); DEC(dh)
    UNTIL dh <= 0;
    INC(nxtRow, bytePerRow)
   END;
   FreeMem(chunky, step)
  END
 END BlitImage;

 PROCEDURE DrawImage(image: ImagePtr; sx, sy: INT16;
                     dx, dy, width, height: INT16);
 BEGIN
  BlitImage(area^.rp, area^.depth, image, sx, sy, dx, dy, width, height)
 END DrawImage;

(* For Dialogs *)
 PROCEDURE PrepareArea(window: WindowPtr; data: ADDRESS; sx, sy, sw, sh, dx, dy: INT16);
  VAR
   rect: Rectangle;
 BEGIN
  area:= data;
  WITH area^ DO
   bm:= window^.rPort^.bitMap;
   rp^.bitMap:= bm;
   IGNORE MoveLayer(l, dx - sx, dy - sy);
   IGNORE SizeLayer(l, l^.bounds.maxX + 1 - sx + sw, l^.bounds.maxY + 1- sy + sh);
   rect.minX:= sx; rect.minY:= sy;
   rect.maxX:= sx + sw - 1;
   rect.maxY:= sy + sh - 1;
   IF NOT OrRectRegion(rgn, ADR(rect)) THEN memLow:= TRUE END;
   IGNORE InstallClipRegion(l, rgn)
  END
 END PrepareArea;

 PROCEDURE FinishArea(window: WindowPtr; data: ADDRESS): BOOLEAN;
 BEGIN
  area:= data;
  WITH area^ DO
   IGNORE InstallClipRegion(l, NIL);
   bm:= NIL
  END;
  RETURN TRUE
 END FinishArea;

 PROCEDURE CopyImage(rp: RastPortPtr; data: ADDRESS; sx, sy, w, h, dx, dy: INT16);
  VAR
   image: ImagePtr;
 BEGIN
  image:= data;
  BlitImage(rp, rp^.bitMap^.depth, image, sx, sy, dx, dy, w, h)
 END CopyImage;

 PROCEDURE CopyRect(sa: AreaPtr; sx, sy: INT16;
                                 dx, dy, width, height: INT16);
 BEGIN
  WITH area^ DO
   IF bltmode = 96 THEN SetWrMsk(rp, uMask) END
  END;
  BltBitMapRastPort(sa^.bm, sx, sy, area^.rp, dx, dy, width, height, area^.bltmode);
  WITH area^ DO
   IF bltmode = 96 THEN SetWrMsk(rp, wMask) END
  END
 END CopyRect;

 PROCEDURE CopyShadow(sa, ma: AreaPtr; sx, sy: INT16;
                                       dx, dy, width, height: INT16);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
   BRA	CopyMask(PC)
  END)
 END CopyShadow;

 (*$ OverflowChk:= FALSE  RangeChk:= FALSE *)
 PROCEDURE CopyMask(sa, ma: AreaPtr; sx, sy: INT16;
                                     dx, dy, width, height: INT16);
  VAR
   ssize, dsize, ssh, dsh, owidth: INT16;
   oldPen, oldMd: LONGCARD;

  PROCEDURE CustomMask;
   VAR
    apt, bpt{R.A2}, cpt{R.A3}: ADDRESS;
    c, size, bsize, ax, cx, ash, csh: INT16;
    amd, bmd, cmd, amod, bmod{R.D4}, cmod{R.D5}: CARD16;
    astep, bstep, cstep: CARD32;
    bltafwm{R.D6}, bltalwm{R.D7}: CARD16;
    bma, bmb, bmc: BitMapPtr;
    bc0: BC0FlagSet;
    bc1: BC1FlagSet;
  BEGIN
   ax:= (sx DIV 16) * 2; cx:= (dx DIV 16) * 2;
   bma:= ma^.bm; amod:= bma^.bytesPerRow;
   astep:= CARD32(amod * CARD16(sy)) + CARD32(ax);
   bmb:= sa^.bm; bmod:= bmb^.bytesPerRow;
   bstep:= CARD32(bmod * CARD16(sy)) + CARD32(ax);
   bmc:= area^.rp^.bitMap; cmod:= bmc^.bytesPerRow;
   cstep:= CARD32(cmod * CARD16(dy)) + CARD32(cx);
   size:= ssize * 2;
   amd:= amod; DEC(amod, size);
   bmd:= bmod; DEC(bmod, size);
   cmd:= cmod; DEC(cmod, size);
   FOR c:= 0 TO area^.depth - 1 DO
    bsize:= height;
    apt:= bma^.planes[0] + ADDRESS(astep);
    bpt:= bmb^.planes[c] + ADDRESS(bstep);
    cpt:= bmc^.planes[c] + ADDRESS(cstep);
    ash:= ssh; csh:= dsh;
    IF ash <= csh THEN
     ASSEMBLE(
	MOVEQ	#16,D3
	SUB.W	ash(A5),D3
	MOVEQ	#1,D6
	LSL.L	D3,D6
	SUBQ.L	#1,D6
	END);
(*   bltafwm:= SHIFT(1, 16 - ash) - 1; *)
     DEC(csh, ash);
     ash:= (ash + width) MOD 16;
     ASSEMBLE(
	MOVEQ	#16,D7
	SUB.W	ash(A5),D7
	MOVEQ	#1,D3
	LSL.L	D7,D3
	MOVEQ	#1,D7
	SWAP	D7
	SUB.L	D3,D7
	END);
(*   bltalwm:= 65536 - SHIFT(1, 16 - ash); *)
     bc1:= BC1FlagSet{}
    ELSE
     ASSEMBLE(
	MOVEQ	#16,D3
	SUB.W	ash(A5),D3
	MOVEQ	#1,D7
	LSL.L	D3,D7
	SUBQ.L	#1,D7
	END);
(*   bltalwm:= SHIFT(1, 16 - ash) - 1; *)
     csh:= ash - csh;
     ash:= (ash + width) MOD 16;
     ASSEMBLE(
	MOVEQ	#16,D6
	SUB.W	ash(A5),D6
	MOVEQ	#1,D3
	LSL.L	D6,D3
	MOVEQ	#1,D6
	SWAP	D6
	SUB.L	D3,D6
	END);
(*   bltafwm:= 65536 - SHIFT(1, 16 - ash); *)
     INC(apt, CARD32(CARD16(bsize - 1) * amd) + CARD32(size) - 2);
     INC(bpt, CARD32(CARD16(bsize - 1) * bmd) + CARD32(size) - 2);
     INC(cpt, CARD32(CARD16(bsize - 1) * cmd) + CARD32(size) - 2);
     bc1:= BC1FlagSet{desc}
    END;
    bsize:= size DIV 2 + SHIFT(bsize, 6);
    csh:= SHIFT(csh, 12);
    bc0:= BC0FlagSet{nanbc, nabc, abnc, abc, dest, srcC, srcB, srcA}
        + CAST(BC0FlagSet, csh);
    bc1:= bc1 + CAST(BC1FlagSet, csh);
    WaitBlit;
    custom.bltapt:= apt; custom.bltbpt:= bpt;
    custom.bltcpt:= cpt; custom.bltdpt:= cpt;
    custom.bltamod:= amod; custom.bltbmod:= bmod;
    custom.bltcmod:= cmod; custom.bltdmod:= cmod;
    custom.bltafwm:= CAST(BITSET, bltafwm);
    custom.bltalwm:= CAST(BITSET, bltalwm);
    custom.bltcon0:= bc0;
    custom.bltcon1:= bc1;
    custom.bltsize:= bsize;
   END
  END CustomMask;

 BEGIN
  IF customMask AND ((area^.w = NIL) OR NOT(windowRefresh IN area^.w^.flags)) THEN
   IF dx < 0 THEN INC(width, dx); DEC(sx, dx); dx:= 0 END;
   IF dy < 0 THEN INC(height, dy); DEC(sy, dy); dy:= 0 END;
   IF dx + width > area^.width THEN width:= area^.width - dx END;
   IF dy + height > area^.height THEN height:= area^.height - dy END;
   IF (width <= 0) OR (height <= 0) THEN RETURN END;
   OwnBlitter;
   ssh:= (sx MOD 16); dsh:= (dx MOD 16);
   ssize:= (width + ssh + 16) DIV 16;
   dsize:= (width + dsh + 15) DIV 16;
   IF ssize >= dsize THEN
    CustomMask
   ELSE
    owidth:= width;
    width:= 16 - dsh;
    ssize:= 1;
    CustomMask;
    INC(sx, width); INC(dx, width);
    width:= owidth - width;
    ssh:= (sx MOD 16); dsh:= 0;
    ssize:= dsize - 1;
    CustomMask
   END;
   DisownBlitter
  ELSIF customMask AND area^.planar AND (graphicsVersion >= 39) THEN
   oldPen:= GetAPen(area^.rp); oldMd:= GetDrMd(area^.rp);
   SetAPen(area^.rp, 0); SetDrMd(area^.rp, DrawModeSet{});
   WITH ma^.bm^ DO
    BltTemplate(
     planes[0] + ADDRESS(bytesPerRow * CARD16(sy) + CARD16(sx) DIV 16 * 2),
     sx MOD 16, bytesPerRow, area^.rp, dx, dy, width, height)
   END;
   SetAPen(area^.rp, oldPen);
   SetDrMd(area^.rp, CAST(DrawModeSet, CHR(oldMd MOD 256)));
   BltBitMapRastPort(sa^.bm, sx, sy, area^.rp, dx, dy, width, height, 060H)
  ELSE
   BltMaskBitMapRastPort(sa^.bm, sx, sy, area^.rp, dx, dy, width, height, 0E0H, ma^.bm^.planes[0])
  END
 END CopyMask;
(*$ POP OverflowChk  POP RangeChk *)

 PROCEDURE ScrollRect(x, y, width, height, dx, dy: INT16);
 BEGIN
  ScrollRaster(area^.rp, -dx, -dy, x, y, x + width - 1, y + height - 1)
 END ScrollRect;

 PROCEDURE ScaleRect(sa: AreaPtr; sx1, sy1, sx2, sy2: INT16;
                                  dx1, dy1, dx2, dy2: INT16);
  VAR
   sca: BitScaleArgs;
   tmpbm: BitMap;
   c, pi, po: INT16;
   in, out: INT32;
   mask: SHORTCARD;
 BEGIN
  pi:= dx2 - dx1; po:= dy2 - dy1;
  in:= sx2 - sx1; out:= sy2 - sy1;
   (*$ OverflowChk:= FALSE *)
  IF dx1 < 0 THEN DEC(sx1, INT32(dx1 * INT16(in)) / pi); dx1:= 0 END;
  IF dy1 < 0 THEN DEC(sy1, INT32(dy1 * INT16(out)) / po); dy1:= 0 END;
  IF dx2 > area^.width THEN DEC(sx2, INT32((dx2 - area^.width) * INT16(in)) / pi); dx2:= area^.width END;
  IF dy2 > area^.height THEN DEC(sy2, INT32((dy2 - area^.height) * INT16(out)) / po); dy2:= area^.height END;
  IF (dx1 >= dx2) OR (dy1 >= dy2) THEN RETURN END;
   (*$ POP OverflowChk *)
  IF (graphicsVersion >= 36) AND (area^.bltmode = 0C0H) THEN
   sca.srcBitMap:= sa^.bm;
   sca.destBitMap:= area^.rp^.bitMap;
   sca.srcX:= sx1; sca.srcY:= sy1;
   sca.destX:= dx1; sca.destY:= dy1;
   sca.srcWidth:= sx2 - sx1;
   sca.srcHeight:= sy2 - sy1;
   sca.srcXFactor:= sca.srcWidth;
   sca.srcYFactor:= sca.srcHeight;
   sca.xDestFactor:= dx2 - dx1;
   sca.yDestFactor:= dy2 - dy1;
   sca.flags:= 0;
   sca.xDDA:= 0; sca.yDDA:= 0;
   sca.reserved1:= 0; sca.reserved2:= 0;
   BitMapScale(sca)
  ELSE
   DEC(dy2, dy1); DEC(dx2, dx1);
   DEC(sy2, sy1); DEC(sx2, sx1);
    (*$ OverflowChk:= FALSE  RangeChk:= FALSE *)
   InitBitMap(tmpbm, area^.depth, area^.width, area^.height);
   FOR c:= 0 TO area^.depth - 1 DO
    tmpbm.planes[c]:= mainTmpRas.rasPtr
   END;
   mask:= 1;
   FOR c:= 0 TO area^.depth - 1 DO
    out:= (dx2 * sx2); in:= out;
    po:= dx2; pi:= sx2;
    WHILE po > 0 DO
     DEC(out, sx2); DEC(po);
     WHILE out < in DO DEC(in, dx2); DEC(pi) END;
     IGNORE BltBitMap(sa^.bm, sx1 + pi, sy1, ADR(tmpbm), po, 0, 1, sy2, 0C0H, mask, NIL)
    END;
    out:= (dy2 * sy2); in:= out;
    po:= dy2; pi:= sy2;
    WHILE po > 0 DO
     DEC(out, sy2); DEC(po);
     WHILE out < in DO DEC(in, dy2); DEC(pi) END;
     IGNORE BltBitMap(ADR(tmpbm), 0, pi, area^.rp^.bitMap, dx1, dy1 + po, dx2, 1, area^.bltmode, mask, NIL)
    END;
    INC(mask, mask)
   END
   (*$ POP OverflowChk  POP RangeChk *)
  END
 END ScaleRect;

 PROCEDURE WaitTOF;
 BEGIN
  WaitBlit;
  GraphicsL.WaitTOF
 END WaitTOF;

 PROCEDURE Close;
 BEGIN
  IF safePort <> NIL THEN
   DeleteMsgPort(safePort); safePort:= NIL
  END;
  IF dispPort <> NIL THEN
   DeleteMsgPort(dispPort); dispPort:= NIL
  END;
  IF closeWB THEN Delay(10); IGNORE OpenWorkBench(); Delay(30) END;
  FreeTmpRas
 END Close;

BEGIN

 rgb32.count:= 1;
 lastErr:= gOk;
 RefreshArea:= PrepareArea;
 EndRefreshArea:= FinishArea;
 RefreshImage:= CopyImage;
 maxDepth:= GetMaxDepth();
 AddTermProc(Close);

END Graphics.
