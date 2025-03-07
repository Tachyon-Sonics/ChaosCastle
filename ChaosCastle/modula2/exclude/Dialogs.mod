IMPLEMENTATION MODULE Dialogs;

 FROM SYSTEM IMPORT ADR, ADDRESS, TAG, LONGSET, SHORTSET, BITSET, CAST, SHIFT;
 FROM IntuitionD IMPORT GadgetFlags, GadgetFlagSet, ActivationFlags,
  ActivationFlagSet, gadgHNone, boolGadget, propGadget, strGadget,
  knobVmin, knobHmin, BoolInfo, BoolInfoPtr, PropInfo,
  PropInfoPtr, PropInfoFlags, PropInfoFlagSet, StringInfo, StringInfoPtr,
  autoFrontPen, autoBackPen, autoDrawMode, autoLeftEdge, autoTopEdge,
  IntuiText, IntuiTextPtr, Border, BorderPtr, Image, ImagePtr, IDCMPFlags,
  IDCMPFlagSet, IntuiMessage, IntuiMessagePtr, customScreen, NewWindow,
  ExtNewWindow, WindowFlags, WindowFlagSet, Window, WindowPtr, ScreenFlags,
  ScreenFlagSet, Screen, ScreenPtr, WaTags;
 FROM IntuitionL IMPORT ActivateGadget, AddGList,
  RefreshGadgets, RefreshGList, RemoveGList, OffGadget, OnGadget, PrintIText,
  OpenWindow, CloseWindow, SetWindowTitles, ModifyIDCMP, ClearMenuStrip,
  LockPubScreen, UnlockPubScreen, MoveWindow, SizeWindow, ChangeWindowBox,
  GetScreenData, DrawBorder, IntuiTextLength, WindowLimits, RefreshWindowFrame,
  ReportMouse, DisplayBeep, intuitionVersion;
 FROM ExecD IMPORT MemReqs, MemReqSet;
 FROM ExecL IMPORT AllocMem, FreeMem, GetMsg, ReplyMsg, Forbid, Permit;
 FROM GraphicsD IMPORT RastPortPtr, ViewPortPtr, TextAttr, TextAttrPtr,
  FontFlags, FontFlagSet, FontStyles, FontStyleSet, jam1, jam2, ColorMap,
  ColorMapPtr, bmaDepth, ViewModes, ViewModeSet;
 FROM GraphicsL IMPORT SetAPen, RectFill, WaitBlit, GetRGB4, ObtainBestPenA,
  ReleasePen, graphicsBase, graphicsVersion, GetBitMapAttr;
 FROM Memory IMPORT SET16, CARD8, INT8, CARD16, INT16, CARD32, INT32,
  TagItem, TagItemPtr, NextTag, CopyStr, LockR, LockW, Unlock;
 FROM AmigaBase IMPORT LinkWindow, UnlinkWindow, globals, memLow,
  programName, WindowControl, GadgetControl, RefreshArea, RefreshImage,
  EndRefreshArea, AskScreenMode, asmHiRes, asmInterlace, asmHam, dither,
  FindBestPen, IsBusyPointer, fontSize;
 FROM Input IMPORT Event, WaitEvent, GetEvent, AddEvents, EventTypeSet,
  eGADGET, SysEvent;
 IMPORT R, IntuitionL, ID: IntuitionD;


 CONST (* RKRM Libraries *)
  RightBorderLowRes =  13;
  RightBorderHiRes =   18;
  BottomBorderLowRes = 11;
  BottomBorderHiRes  = 10;

  propVmin = knobVmin + autoTopEdge * 2;
  propHmin = knobHmin + autoLeftEdge;

  MinWinWidth = 40;
  MinWinHeight = 30;

  dfNOMEM = 1; dbNoMem = 0;
  dfNOTVISIBLE = 2; dbNotVisible = 1;

 TYPE
  GadgetPtr = POINTER TO Gadget;

  CreateProc = PROCEDURE(GadgetPtr): BOOLEAN;
  ActionProc = PROCEDURE(GadgetPtr);
  DrawProc = PROCEDURE(GadgetPtr): INT32;
  EventProc = PROCEDURE(GadgetPtr, VAR Event);
  TagProc = PROCEDURE(GadgetPtr, VAR TagItem);
  TagListProc = PROCEDURE(GadgetPtr, TagItemPtr);
  GetSizeProc = PROCEDURE(GadgetPtr, VAR INT16, VAR INT16);
  SetSizeProc = PROCEDURE(GadgetPtr, INT16, INT16);

  Gadget = RECORD
   parent, next, childs: GadgetPtr;
   type: INT16;
   flags: SET16;
   iflags: SET16;
   Draw: DrawProc;
   Add, Remove: ActionProc;
   TrackEvent: EventProc;
   Modify: TagListProc;
   GetAttr: TagProc;
   GetSize: GetSizeProc;
   SetSize: SetSizeProc;
   Free: ActionProc;
   x, y, w, h: INT16;
   mw, mh, gp: INT16;
   textColor: INT32;
   nbChilds: CARD16;
   data: ADDRESS;
   udata: ADDRESS;
   uid: INT32;
  END;

  Point = RECORD
   x, y: INT16;
  END;

 CONST
  topazAttr = TextAttr{
   name: ADR("topaz.font"),
   ySize: 8,
   style: FontStyleSet{},
   flags: FontFlagSet{romFont}};


 PROCEDURE GetGadgetSize(gadget: GadgetPtr; VAR sw, sh: INT16);
 FORWARD;

 PROCEDURE SetGadgetSize(gadget: GadgetPtr; sw, sh: INT16);
 FORWARD;

  (** Dialogs *)
 TYPE
  Dialog = RECORD
   name: ADDRESS;
   window: WindowPtr;
   screen: ScreenPtr;
   ta: TextAttrPtr;
   firstGadget: ID.GadgetPtr;
   nbGadget: CARD16;
   lightPen, darkPen, fillPen, areas: CARD8;
   refresh: BOOLEAN;
   group: GadgetPtr;
  END;
  DialogPtr = POINTER TO Dialog;


 PROCEDURE PlaceBox(VAR x, y, w, h: INT16; cx, cy, cw, ch: INT16; flags: SET16);
 BEGIN
  IF dbAutoLeft IN flags THEN
   x:= cx;
   IF dbAutoRight IN flags THEN w:= cw END
  ELSIF dbAutoRight IN flags THEN
   x:= cx + cw - w
  ELSE
   x:= cx + (cw - w) DIV 2
  END;
  IF dbAutoUp IN flags THEN
   y:= cy;
   IF dbAutoDown IN flags THEN h:= ch END
  ELSIF dbAutoDown IN flags THEN
   y:= cy + ch - h
  ELSE
   y:= cy + (ch - h) DIV 2
  END
 END PlaceBox;

 PROCEDURE GetScreenSizes(d: DialogPtr; VAR swidth, sheight, l, t, r, b: INT16; dflags: SET16);
  VAR
   s{R.D7}: ScreenPtr;
   cm: ColorMapPtr;
   wbench: Screen;
   rgb: LONGCARD;
   c, depth, rc, gc, bc: INTEGER;
   b1, b2, b3, c1{R.D6}, c2{R.D5}, c3{R.D4}: INTEGER;
   lock, grey: BOOLEAN;
 BEGIN
  lock:= FALSE;
  IF d <> NIL THEN s:= d^.screen ELSE s:= NIL END;
  IF s = NIL THEN
   LockR(globals);
   IF globals^.mainWindow <> NIL THEN
    s:= globals^.mainWindow^.wScreen
   ELSIF intuitionVersion >= 36 THEN
    s:= LockPubScreen(NIL); lock:= TRUE
   ELSIF GetScreenData(ADR(wbench), SIZE(wbench), ScreenFlagSet{wbenchScreen}, NIL) THEN
    s:= ADR(wbench)
   END;
   Unlock(globals)
  END;
  IF d <> NIL THEN
   WITH d^ DO
    IF darkPen = 0 THEN
     darkPen:= FindBestPen(0, 0);
     lightPen:= FindBestPen(0FFFFFFH, 0);
     fillPen:= FindBestPen(0FF8800H, 0)
    END
   END
  END;
  IF s <> NIL THEN
   IF (s <> ADR(wbench)) AND (d <> NIL) THEN d^.screen:= s END;
   cm:= s^.viewPort.colorMap;
   rgb:= GetRGB4(cm, 0);
   rc:= rgb DIV 256; gc:= (rgb DIV 16) MOD 16; bc:= rgb MOD 16;
   IF (rc + gc + bc < 12) AND (d <> NIL) THEN
    d^.darkPen:= FindBestPen(0888888H, 0)
   END;
   WITH s^ DO
    swidth:= width; sheight:= height;
    l:= wBorLeft; t:= wBorTop + SHORTINT(font^.ySize) + 1;
    IF (dbSizeX IN dflags) OR (dbScrollX IN dflags) THEN
     IF screenHires IN flags THEN
      b:= BottomBorderHiRes
     ELSE
      b:= BottomBorderLowRes
     END
    ELSE
     b:= wBorBottom
    END;
    IF (dbSizeY IN dflags) OR (dbScrollY IN dflags) THEN
     IF screenHires IN flags THEN
      r:= RightBorderHiRes
     ELSE
      r:= RightBorderLowRes
     END
    ELSE
     r:= wBorRight
    END
   END
  ELSE
   swidth:= graphicsBase^.normalDisplayColumns;
   sheight:= graphicsBase^.normalDisplayRows;
   l:= autoLeftEdge; r:= l;
   t:= autoTopEdge; b:= t
  END;
  IF lock THEN
   UnlockPubScreen(NIL, s)
  ELSIF s = ADR(wbench) THEN
   s:= NIL
  END
 END GetScreenSizes;

 PROCEDURE AdjustToScreen(gadget: GadgetPtr);
  VAR
   d{R.D7}: DialogPtr;
   scrw, scrh, dummy: INT16;
 BEGIN
  d:= gadget^.data;
  WITH d^ DO
   IF window <> NIL THEN
    WITH window^.wScreen^ DO scrw:= width; scrh:= height END
   ELSE
    GetScreenSizes(d, scrw, scrh, dummy, dummy, dummy, dummy, SET16{})
   END;
   WITH gadget^ DO
    IF w > scrw THEN w:= scrw END;
    IF h > scrh THEN h:= scrh END;
    IF x + w > scrw THEN x:= scrw - w END;
    IF y + h > scrh THEN y:= scrh - h END
   END
  END
 END AdjustToScreen;

 PROCEDURE CharToPixel(val{R.D2}: INT32): INT32;
 BEGIN
  RETURN val * INT16(fontSize) DIV 100
 END CharToPixel;

 PROCEDURE PixelToChar(val{R.D2}: INT32): INT32;
 BEGIN
  RETURN val * 100 DIV INT16(fontSize)
 END PixelToChar;

 PROCEDURE UnlimitWindow(window: WindowPtr);
 BEGIN
  IGNORE WindowLimits(window, MinWinWidth, MinWinHeight, -1, -1)
 END UnlimitWindow;

 PROCEDURE LimitWindow(gadget: GadgetPtr; window: WindowPtr);
  VAR
   minw{R.D7}, minh{R.D6}, maxw{R.D5}, maxh{R.D4}: INT16;
 BEGIN
  WITH gadget^ DO
   minw:= MinWinWidth; minh:= MinWinHeight; maxw:= -1; maxh:= -1;
   IF NOT(dbSizeX IN flags) THEN minw:= w; maxw:= w END;
   IF NOT(dbSizeY IN flags) THEN minh:= h; maxh:= h END;
   IGNORE WindowLimits(window, minw, minh, maxw, maxh)
  END
 END LimitWindow;

 PROCEDURE DrawDialog(gadget: GadgetPtr): INT32;
  VAR
   tags: ARRAY[0..4] OF ADDRESS;
   nw: ExtNewWindow;
   d: DialogPtr;
   s{R.D6}: ScreenPtr;
   rp{R.D7}: RastPortPtr;
  (*$ EntryClear:= TRUE *)
 BEGIN
  LockR(globals);
  WITH globals^ DO
   IF mainWindow <> NIL THEN s:= mainWindow^.wScreen ELSE s:= NIL END
  END;
  Unlock(globals);
  WITH gadget^ DO
   d:= data;
   WITH d^ DO
    IF window = NIL THEN
     IF (group = NIL) OR (dbNoMem IN group^.iflags) THEN
      RETURN DialogNoMem
     END;
     nw.nw.width:= w; nw.nw.height:= h;
     nw.nw.leftEdge:= x; nw.nw.topEdge:= y;
     nw.nw.screen:= s;
     IF s <> NIL THEN
      nw.nw.type:= customScreen
     ELSE
      nw.nw.type:= ScreenFlagSet{wbenchScreen}
     END;
     IF textColor <> -1 THEN
      nw.nw.detailPen:= CAST(SHORTINT, SHORTCARD(FindBestPen(textColor, 1)))
     ELSE
      nw.nw.detailPen:= -1
     END;
     nw.nw.blockPen:= -1;
     nw.nw.idcmpFlags:= IDCMPFlagSet{};
     nw.nw.flags:= WindowFlagSet{windowDrag, windowDepth, simpleRefresh};
     IF dbBorder IN flags THEN INCL(nw.nw.flags, windowClose) END;
     IF dbSelect IN flags THEN INCL(nw.nw.flags, activate) END;
     IF flags * SET16{dbSizeX, dbSizeY} <> SET16{} THEN
      INCL(nw.nw.flags, windowSizing)
     END;
     IF flags * SET16{dbSizeX, dbScrollX} <> SET16{} THEN
      INCL(nw.nw.flags, sizeBBottom)
     END;
     IF flags * SET16{dbSizeY, dbScrollY} <> SET16{} THEN
      INCL(nw.nw.flags, sizeBRight)
     END;
     nw.nw.firstGadget:= firstGadget;
     nw.nw.title:= name;
     IF (dbSizeX IN flags) THEN
      nw.nw.minWidth:= MinWinWidth; nw.nw.maxWidth:= -1
     END;
     IF (dbSizeY IN flags) THEN
      nw.nw.minHeight:= MinWinHeight; nw.nw.maxHeight:= -1
     END;
     nw.extension:= TAG(tags,
      waBusyPointer, IsBusyPointer(),
      waPointerDelay, FALSE,
      0);
     window:= OpenWindow(nw.nw);
     IF window = NIL THEN
      RETURN DialogNoMem
     ELSE
      LinkWindow(window);
      window^.userData:= gadget
     END
    ELSIF refresh THEN
     rp:= window^.rPort;
     SetAPen(rp, 0);
     RectFill(rp, group^.x, group^.y, group^.x + group^.w - 1, group^.y + group^.h - 1);
     RefreshWindowFrame(window);
     refresh:= FALSE
    END
   END
  END;
  RETURN DialogOk
 END DrawDialog;
(*$ POP EntryClear *)

 PROCEDURE HideDialog(gadget: GadgetPtr);
  VAR
   d{R.D7}: DialogPtr;
 BEGIN
  d:= gadget^.data;
  IF d <> NIL THEN
   WITH d^ DO
    IF window <> NIL THEN
     IF firstGadget <> NIL THEN
      WaitBlit;
      IGNORE RemoveGList(window, firstGadget, -1)
     END;
     UnlinkWindow(window);
     WaitBlit;
     CloseWindow(window);
     window:= NIL
    END
   END
  END
 END HideDialog;

 PROCEDURE ModifyDialog(gadget: GadgetPtr; tags: TagItemPtr);
  VAR
   d{R.D7}: DialogPtr;
   oldTags: TagItemPtr;
   scrw, scrh: INT16;
   moved{R.D6}, resized{R.D5}: BOOLEAN;
 BEGIN
  moved:= FALSE; resized:= FALSE;
  oldTags:= tags;
  d:= gadget^.data;
  WITH d^ DO
   LOOP
    WITH NextTag(tags)^ DO
     IF tag = 0 THEN EXIT
     ELSIF tag = dTEXT THEN
      name:= addr;
      IF window <> NIL THEN
       SetWindowTitles(window, name, programName)
      END
     ELSIF (tag = dXPOS) OR (tag = dYPOS) THEN moved:= TRUE
     ELSIF (tag = dWIDTH) OR (tag = dHEIGHT) THEN resized:= TRUE
     END
    END
   END;
   IF window <> NIL THEN
    AdjustToScreen(gadget);
    WITH gadget^ DO
     IF resized THEN UnlimitWindow(window) END;
     IF intuitionVersion < 36 THEN
      IF moved THEN
       MoveWindow(window, x - window^.leftEdge, y - window^.topEdge)
      END;
      IF resized THEN
       SizeWindow(window, w - window^.width, h - window^.height)
      END
     ELSE
      IF moved OR resized THEN
       ChangeWindowBox(window, x, y, w, h)
      END
     END;
     IF resized THEN LimitWindow(gadget, window) END
    END
   END;
   IF group <> NIL THEN
    ModifyGadget(group, oldTags);
    EXCL(group^.flags, dbBorder);
    group^.flags:= group^.flags + SET16{dbAutoLeft..dbAutoDown}
   END
  END
 END ModifyDialog;

 PROCEDURE GetDialogAttr(gadget: GadgetPtr; VAR what: TagItem);
  VAR
   d{R.D7}: DialogPtr;
 BEGIN
  d:= gadget^.data;
  WITH d^ DO
   WITH what DO
    IF tag = dTEXT THEN data:= name END
   END
  END
 END GetDialogAttr;

 PROCEDURE GetDialogSize(gadget: GadgetPtr; VAR width, height: INT16);
  VAR
   iText: IntuiText;
   d{R.D7}: DialogPtr;
   t, l, b, r, scrw, scrh, mnw{R.D6}, gp{R.D5}: INT16;
 BEGIN
  d:= gadget^.data;
  gp:= gadget^.gp * 2;
  WITH d^ DO
   GetScreenSizes(d, scrw, scrh, l, t, r, b, gadget^.flags);
   IF screen <> NIL THEN
    ta:= screen^.font
   ELSE
    ta:= ADR(topazAttr)
   END;
   WITH iText DO
    frontPen:= 1; backPen:= 0; drawMode:= jam1;
    leftEdge:= 0; topEdge:= 0; nextText:= NIL;
    iTextFont:= ta; iText:= name
   END;
   IF NOT(dbSizeX IN gadget^.flags) THEN
    mnw:= IntuiTextLength(ADR(iText)) + 64
   ELSE
    mnw:= 64
   END;
   LOOP
    GetGadgetSize(group, width, height);
    IF width < mnw THEN width:= mnw END;
    INC(width, l + r + gp);
    INC(height, t + b + gp);
    IF (width <= scrw) AND (height <= scrh) THEN EXIT END;
    IF width > scrw THEN width:= scrw END;
    IF height > scrh THEN height:= scrh END;
    IF ta = ADR(topazAttr) THEN EXIT END;
    ta:= ADR(topazAttr)
   END;
   DEC(width, gp); DEC(height, gp);
   IF (window <> NIL) AND (dbSizeX IN gadget^.flags) THEN
    width:= window^.width
   END;
   IF (window <> NIL) AND (dbSizeY IN gadget^.flags) THEN
    height:= window^.height
   END;
   IF (width <> gadget^.w) OR (height <> gadget^.h) THEN
    refresh:= TRUE
   END
  END
 END GetDialogSize;

 PROCEDURE SetDialogSize(gadget: GadgetPtr; width, height: INT16);
  VAR
   d{R.D7}: DialogPtr;
   rp{R.D6}: RastPortPtr;
   l, t, r, b, scrw, scrh: INT16;
 BEGIN
  d:= gadget^.data;
  AdjustToScreen(gadget);
  GetScreenSizes(d, scrw, scrh, l, t, r, b, gadget^.flags);
  WITH gadget^ DO
   IF (x = 0) AND (y = 0) THEN
    PlaceBox(x, y, w, h, 0, 0, scrw, scrh, flags)
   END;
   flags:= flags - SET16{dbAutoLeft..dbAutoDown}
  END;
  WITH d^ DO
   IF window <> NIL THEN
    WITH gadget^ DO
     IF (x <> window^.leftEdge) OR (y <> window^.topEdge) THEN
      IF (window^.leftEdge + w <= scrw) AND (window^.topEdge + h <= scrh) THEN
       x:= window^.leftEdge; y:= window^.topEdge
      ELSE
       MoveWindow(window, x - window^.leftEdge, y - window^.topEdge)
      END
     END;
     IF (w <> window^.width) OR (h <> window^.height) THEN
      UnlimitWindow(window);
      SizeWindow(window, w - window^.width, h - window^.height);
      LimitWindow(gadget, window)
     END
    END
   END;
   group^.x:= l; group^.y:= t;
   SetGadgetSize(group, gadget^.w - (l + r), gadget^.h - (t + b));
   IF (window <> NIL) AND refresh THEN
    rp:= window^.rPort;
    SetAPen(rp, 0);
    RectFill(rp, l, t, width - r - 1, height - b - 1);
    RefreshWindowFrame(window);
    refresh:= FALSE
   END
  END
 END SetDialogSize;

 PROCEDURE FreeDialog(gadget: GadgetPtr);
  VAR
   d{R.D7}: DialogPtr;
 BEGIN
  d:= gadget^.data;
  IF d <> NIL THEN
   HideDialog(gadget);
   WITH d^ DO
    IF gadget^.nbChilds > 0 THEN FreeGadget(group) END
   END;
   FreeMem(d, SIZE(Dialog));
   gadget^.data:= NIL
  END
 END FreeDialog;

 PROCEDURE CreateDialog(gadget: GadgetPtr): BOOLEAN;
  VAR
   d{R.D7}: DialogPtr;
 BEGIN
  d:= AllocMem(SIZE(Dialog), MemReqSet{memClear});
  IF d <> NIL THEN
   d^.group:= AddNewGadget(gadget, dGroup, NIL);
   IF d^.group <> NIL THEN
    WITH gadget^ DO
     Draw:= DrawDialog;
     Modify:= ModifyDialog;
     GetAttr:= GetDialogAttr;
     GetSize:= GetDialogSize;
     SetSize:= SetDialogSize;
     Free:= FreeDialog;
     data:= d
    END;
    RETURN TRUE
   END;
   FreeMem(d, SIZE(Dialog))
  END;
  RETURN FALSE
 END CreateDialog;


  (** Misc *)
 PROCEDURE RefreshWindow(window: WindowPtr): BOOLEAN;
  VAR
   gadget{R.D7}: GadgetPtr;
   dialog{R.D6}: DialogPtr;
 BEGIN
  gadget:= window^.userData;
  dialog:= gadget^.data;
  IF (gadget^.flags * SET16{dbSizeX, dbSizeY} = SET16{}) THEN
   IGNORE DrawGadget(gadget)
  ELSE
   IGNORE RefreshGadget(gadget)
  END;
  RETURN (dialog^.areas > 0)
 END RefreshWindow;

 PROCEDURE LinkGadget(d: DialogPtr; gadget: GadgetPtr; VAR gad: ID.Gadget);
  VAR
   tmp: ID.GadgetPtr;
 BEGIN
  IF (dbNotVisible IN gadget^.iflags) THEN RETURN END;
  tmp:= d^.firstGadget;
  WHILE tmp <> NIL DO
   IF tmp = ADR(gad) THEN RETURN END; (* Already present *)
   tmp:= tmp^.nextGadget;
  END;
  IF d^.window = NIL THEN
   gad.nextGadget:= d^.firstGadget
  ELSE
   IGNORE IntuitionL.AddGadget(d^.window, ADR(gad), 0);
   IF gadget^.w = 0 THEN
    d^.refresh:= TRUE
   END
  END;
  d^.firstGadget:= ADR(gad);
  INC(d^.nbGadget)
 END LinkGadget;

 PROCEDURE UnlinkGadget(d: DialogPtr; VAR gad: ID.Gadget);
  VAR
   prevGadgetPtr{R.A3}: POINTER TO ID.GadgetPtr;
 BEGIN
  IF (d^.window = NIL) THEN
   prevGadgetPtr:= ADR(d^.firstGadget);
   WHILE prevGadgetPtr^ <> ADR(gad) DO
    IF prevGadgetPtr^ = NIL THEN RETURN END;
    prevGadgetPtr:= ADR(prevGadgetPtr^^.nextGadget)
   END;
   prevGadgetPtr^:= gad.nextGadget
  ELSE
   IF d^.firstGadget = ADR(gad) THEN
    d^.firstGadget:= gad.nextGadget
   END;
   IF IntuitionL.RemoveGadget(d^.window, ADR(gad)) = -1 THEN RETURN END;
   d^.refresh:= TRUE
  END;
  DEC(d^.nbGadget);
  IF d^.nbGadget = 0 THEN d^.firstGadget:= NIL END
 END UnlinkGadget;

  (*$ OverflowChk:= FALSE *)
 CONST
  MAXBODY = 0FFFFH;
  MAXPOT = 0FFFFH;

 PROCEDURE FindScrollerValues(total, displayable, top, overlap: CARDINAL;
                              VAR body, pot: CARDINAL);
  VAR
   hidden{R.D7}: CARDINAL;
 BEGIN
  IF overlap >= displayable THEN overlap:= displayable - 1 END;
  IF displayable >= total THEN hidden:= 0 ELSE hidden:= total - displayable END;
  IF top > hidden THEN top:= hidden END;
  IF hidden > 0 THEN
   body:= LONGCARD((displayable - overlap) * MAXBODY) DIV (total - overlap)
  ELSE
   body:= MAXBODY
  END;
  IF hidden > 0 THEN
   pot:= LONGCARD(top * MAXPOT) DIV hidden
  ELSE
   pot:= 0
  END
 END FindScrollerValues;

 PROCEDURE FindScrollerTop(total, displayable, pot: CARDINAL): CARDINAL;
  VAR
   hidden{R.D7}: CARDINAL;
 BEGIN
  IF displayable >= total THEN hidden:= 0 ELSE hidden:= total - displayable END;
  RETURN (LONGCARD(hidden * pot) + MAXPOT DIV 2) DIV 65536
 END FindScrollerTop;
  (*$ POP OverflowChk *)

 TYPE
  BoxPts = ARRAY[0..5] OF Point;
  BoxBorders = ARRAY[0..3] OF Border;

 PROCEDURE InitBorders(VAR bb: BoxBorders; VAR coords: BoxPts);
 BEGIN
  WITH bb[0] DO
   drawMode:= jam1; count:= 3;
   xy:= ADR(coords[0]);
   nextBorder:= ADR(bb[1])
  END;
  WITH bb[1] DO
   drawMode:= jam1; count:= 3;
   xy:= ADR(coords[3]);
   nextBorder:= NIL
  END;
  bb[2]:= bb[0]; bb[3]:= bb[1];
  bb[2].nextBorder:= ADR(bb[3])
 END InitBorders;

 PROCEDURE SetBox(gadget: GadgetPtr; VAR box: BoxPts);
 BEGIN
  WITH gadget^ DO
   box[0].x:= 0; box[0].y:= h - 1;
   box[1].x:= 0; box[1].y:= 0;
   box[2].x:= w - 1; box[2].y:= 0;
   box[3]:= box[2];
   box[4].x:= box[2].x; box[4].y:= box[0].y;
   box[5]:= box[0]
  END
 END SetBox;

 PROCEDURE SetSquareBox(VAR box: BoxPts; s: INT16);
 BEGIN
  box[0].x:= 0; box[0].y:= s;
  box[1].x:= 0; box[1].y:= 0;
  box[2].x:= s; box[2].y:= 0;
  box[3]:= box[2];
  box[4].x:= box[2].x; box[4].y:= box[0].y;
  box[5]:= box[0]
 END SetSquareBox;

 PROCEDURE SetBordersPens(VAR bb: BoxBorders; d: DialogPtr);
 BEGIN
  bb[0].frontPen:= d^.lightPen;
  bb[1].frontPen:= d^.darkPen;
  bb[2].frontPen:= d^.darkPen;
  bb[3].frontPen:= d^.lightPen
 END SetBordersPens;

 PROCEDURE GetDialog(gadget: GadgetPtr): DialogPtr;
 BEGIN
  WHILE gadget^.parent <> NIL DO
   gadget:= gadget^.parent
  END;
  IF gadget^.type <> dDialog THEN
   RETURN NIL
  END;
  RETURN gadget^.data
 END GetDialog;

 PROCEDURE GetWindow(gadget: GadgetPtr): WindowPtr;
  VAR
   d{R.D7}: DialogPtr;
 BEGIN
  d:= GetDialog(gadget);
  IF d = NIL THEN RETURN NIL END;
  RETURN d^.window
 END GetWindow;

 PROCEDURE GetRastPort(gadget: GadgetPtr): RastPortPtr;
  VAR
   d{R.D7}: DialogPtr;
 BEGIN
  d:= GetDialog(gadget);
  IF d = NIL THEN RETURN NIL END;
  WITH d^ DO
   IF window <> NIL THEN
    RETURN window^.rPort
   ELSE
    RETURN NIL
   END
  END
 END GetRastPort;


  (** Groups *)
 TYPE
  INT16Arr = ARRAY[0..63] OF INT16;
  INT16ArrPtr = POINTER TO INT16Arr;
  Group = RECORD
   bb: BoxBorders;
   coords: BoxPts;
   iniw, inih: INT16; (* Size of the first layout *)
   iw, ih, sx, sy: INT16; (* Inner size and scroll position *)
   ss, sz: INT16ArrPtr;
   ssize, zsize: CARD16;
   span: CARD16;
   hgad, vgad: ID.Gadget; (* Scrollers *)
   hpi, vpi: PropInfo;
   himage, vimage: Image;
   scroll: BOOLEAN;
  END;
  GroupPtr = POINTER TO Group;

 PROCEDURE AddGroup(gadget: GadgetPtr);
  VAR
   grp{R.D7}: GroupPtr;
   d{R.D6}: DialogPtr;
 BEGIN
  IF (dbNotVisible IN gadget^.iflags) THEN RETURN END;
  grp:= gadget^.data;
  WITH grp^ DO
   IF gadget^.parent^.type = dDialog THEN
    hgad.flags:= hgad.flags + GadgetFlagSet{gRelBottom, gRelWidth};
    vgad.flags:= vgad.flags + GadgetFlagSet{gRelRight, gRelHeight};
    INCL(hgad.activation, bottomBorder);
    INCL(vgad.activation, rightBorder)
   END
  END;
  d:= GetDialog(gadget);
  IF (d <> NIL) THEN
   WITH d^ DO
    IF window <> NIL THEN
     IF (dbScrollY IN gadget^.flags) THEN
      IGNORE IntuitionL.AddGadget(window, ADR(grp^.vgad), -1)
     END;
     IF (dbScrollX IN gadget^.flags) THEN
      IGNORE IntuitionL.AddGadget(window, ADR(grp^.hgad), -1)
     END
    END
   END
  END
 END AddGroup;

 PROCEDURE RemoveGroup(gadget: GadgetPtr);
  VAR
   grp{R.D7}: GroupPtr;
   d{R.D6}: DialogPtr;
 BEGIN
  grp:= gadget^.data;
  d:= GetDialog(gadget);
  IF (d <> NIL) THEN
   IF (dbScrollY IN gadget^.flags) THEN UnlinkGadget(d, grp^.vgad) END;
   IF (dbScrollX IN gadget^.flags) THEN UnlinkGadget(d, grp^.hgad) END
  END
 END RemoveGroup;

 PROCEDURE DrawGroup(gadget: GadgetPtr): INT32;
  VAR
   group{R.D7}: GroupPtr;
   rp{R.D6}: RastPortPtr;
 BEGIN
  WITH gadget^ DO
   group:= data;
   WITH group^ DO
    IF ((ss = NIL) OR (sz = NIL)) AND (nbChilds > 0) THEN RETURN DialogNoMem END
   END;
   IF NOT(dbBorder IN flags) THEN RETURN DialogOk END;
   rp:= GetRastPort(gadget);
   WITH group^ DO
    SetBordersPens(bb, GetDialog(gadget));
    IF (dbSelect IN flags) THEN
     DrawBorder(rp, ADR(bb[2]), x, y)
    ELSE
     DrawBorder(rp, ADR(bb[0]), x, y)
    END
   END
  END;
  RETURN DialogOk
 END DrawGroup;

 PROCEDURE ModifyGroup(gadget: GadgetPtr; tags: TagItemPtr);
  VAR
   group{R.D7}: GroupPtr;
 BEGIN
  group:= gadget^.data;
  LOOP
   WITH NextTag(tags)^ DO
    IF tag = 0 THEN EXIT
    ELSIF tag = dSPAN THEN group^.span:= data
    END
   END
  END
 END ModifyGroup;

 PROCEDURE GetGroupSize(gadget: GadgetPtr; VAR width, height: INT16);
  TYPE
   INT16Ptr = POINTER TO INT16;
  VAR
   child: GadgetPtr;
   group: GroupPtr;
   span, spos, zpos: CARD16;
   s, z: INT16Ptr;
   ms, mz: INT16ArrPtr;
   cw, ch: INT16;
   scd: INT16;
 BEGIN
  width:= 0; height:= 0;
  WITH gadget^ DO
   IF nbChilds = 0 THEN RETURN END;
   group:= data;
   WITH group^ DO
    IF ss <> NIL THEN FreeMem(ss, SIZE(INT16) * ssize); ss:= NIL END;
    IF sz <> NIL THEN FreeMem(sz, SIZE(INT16) * zsize); sz:= NIL END;
    IF span = 0 THEN
     ssize:= nbChilds; zsize:= 1
    ELSE
     ssize:= span;
     zsize:= (nbChilds + span - 1) DIV span
    END;
    ss:= AllocMem(SIZE(INT16) * ssize, MemReqSet{});
    sz:= AllocMem(SIZE(INT16) * zsize, MemReqSet{});
    IF (ss = NIL) OR (sz = NIL) THEN RETURN END;
    ms:= ss; mz:= sz
   END;
   span:= group^.span;
   IF (dbVDir IN flags) THEN
    s:= ADR(ch); z:= ADR(cw);
   ELSE
    s:= ADR(cw); z:= ADR(ch);
   END;
   spos:= 0; zpos:= 0;
   child:= childs;
   WHILE child <> NIL DO
    IF spos = 0 THEN mz^[zpos]:= 0 END;
    IF zpos = 0 THEN ms^[spos]:= 0 END;
    GetGadgetSize(child, cw, ch);
    IF s^ > ms^[spos] THEN ms^[spos]:= s^ END;
    IF z^ > mz^[zpos] THEN mz^[zpos]:= z^ END;
    child:= child^.next;
    INC(spos);
    IF spos = span THEN spos:= 0; INC(zpos) END
   END;
   cw:= 0; ch:= 0;
   FOR spos:= 0 TO group^.ssize - 1 DO INC(s^, ms^[spos]) END;
   FOR zpos:= 0 TO group^.zsize - 1 DO INC(z^, mz^[zpos]) END;
   width:= cw; height:= ch;
   group^.iw:= cw; group^.ih:= ch;
   IF (dbBorder IN flags) THEN
    INC(width, 4); INC(height, 4)
   END;
   IF flags * SET16{dbScrollX, dbScrollY} <> SET16{} THEN
    IF parent^.type <> dDialog THEN
     WITH group^ DO
      IF (dbScrollY IN flags) THEN
       EXCL(vpi.flags, propBorderless);
       IF (inih <> 0) THEN height:= inih ELSE inih:= height END;
       INC(width, propHmin)
      END;
      IF (dbScrollX IN flags) THEN
       EXCL(hpi.flags, propBorderless);
       IF (iniw <> 0) THEN width:= iniw ELSE iniw:= width END;
       INC(height, propVmin)
      END
     END
    END
   END
  END
 END GetGroupSize;

 PROCEDURE SetGroupSize(gadget: GadgetPtr; width, height: INT16);
  CONST
   dbAutoWidth = SET16{dbAutoLeft, dbAutoRight};
   dbAutoHeight = SET16{dbAutoUp, dbAutoDown};
  TYPE
   INT16Ptr = POINTER TO INT16;
  VAR
   group, sgrp: GroupPtr;
   child, scroller: GadgetPtr;
   d: DialogPtr;
   scnt, zcnt, spos, zpos, span, ys: INT16;
   px, py, cw, ch, fw, fh, sps, sfs, add: INT16;
   s, z, ps, pz, fs, fz: INT16Ptr;
   ms, mz: INT16ArrPtr;
   test1, test2: BOOLEAN;
 BEGIN
  WITH gadget^ DO
   group:= data;
   WITH group^ DO
    IF (ss = NIL) OR (sz = NIL) THEN RETURN END;
    ms:= ss; mz:= sz;
    scnt:= ssize; zcnt:= zsize
   END;
   span:= group^.span;
   fw:= width - w; fh:= height - h;
   PlaceBox(x, y, w, h, x, y, width, height, flags);
   SetBox(gadget, group^.coords);
   IF width <> w THEN fw:= 0 END;
   IF height <> h THEN fh:= 0 END;
   IF (dbScrollY IN flags) THEN
    IF parent^.type <> dDialog THEN DEC(w, propHmin) END;
    fh:= 0;
    WITH group^ DO
     IF sy + h > ih THEN sy:= ih - h END;
     IF sy < 0 THEN sy:= 0 END
    END
   END;
   IF (dbScrollX IN flags) THEN
    IF parent^.type <> dDialog THEN DEC(h, propVmin) END;
    fw:= 0;
    WITH group^ DO
     IF sx + w > iw THEN sx:= iw - w END;
     IF sx < 0 THEN sx:= 0 END
    END
   END;
    (* Layout childs *)
   px:= x; py:= y;
   IF (dbBorder IN flags) THEN INC(px, 2); INC(py, 2) END;
   IF (dbVDir IN flags) THEN
    s:= ADR(ch); z:= ADR(cw); sps:= py; sfs:= fh;
    fs:= ADR(fh); fz:= ADR(fw); ps:= ADR(py); pz:= ADR(px)
   ELSE
    s:= ADR(cw); z:= ADR(ch); sps:= px; sfs:= fw;
    fs:= ADR(fw); fz:= ADR(fh); ps:= ADR(px); pz:= ADR(py)
   END;
   spos:= 0; zpos:= 0;
   child:= childs;
   WHILE child <> NIL DO
    s^:= ms^[spos]; z^:= mz^[zpos]; (* Get adjusted size *)
    add:= (fs^ + scnt - 1) DIV scnt;
    INC(s^, add); DEC(fs^, add);
    add:= (fz^ + zcnt - 1) DIV zcnt;
    INC(z^, add);
    child^.x:= px; child^.y:= py;
    IF (dbScrollX IN flags) THEN DEC(child^.x, group^.sx) END;
    IF (dbScrollY IN flags) THEN DEC(child^.y, group^.sy) END;
    SetGadgetSize(child, cw, ch);
    scroller:= gadget;
    WHILE (scroller <> NIL) AND
      (scroller^.flags * SET16{dbScrollX, dbScrollY} = SET16{}) DO
     scroller:= scroller^.parent
    END;
    IF (scroller <> NIL) THEN
     WITH scroller^ DO
      sgrp:= data;
      test1:= (child^.x < x) OR (child^.x + child^.w > x + w) OR
              (child^.y < y) OR (child^.y + child^.h > y + h);
      test2:= ((child^.x + child^.w <= x) OR (child^.x >= x + w) OR
              (child^.y + child^.h <= y) OR (child^.y >= y + h));
      IF (test1 AND (child^.type <> dGroup) AND
         (child^.type <> dArea) AND (child^.type <> dImage))
         OR test2 THEN
       IF NOT(dbNotVisible IN child^.iflags) THEN
        INCL(child^.iflags, dbNotVisible);
        IF child^.Remove <> NIL THEN child^.Remove(child) END
       END
      ELSE
       IF (dbNotVisible IN child^.iflags) OR (sgrp^.scroll) THEN
        EXCL(child^.iflags, dbNotVisible);
        IF sgrp^.scroll AND (child^.Remove <> NIL) THEN child^.Remove(child) END;
        IF child^.Add <> NIL THEN child^.Add(child) END
       END
      END
     END
    END;
    INC(ps^, s^);
    INC(spos); DEC(scnt);
    IF spos = span THEN
     spos:= 0; scnt:= group^.ssize;
     DEC(fz^, add);
     fs^:= sfs; ps^:= sps;
     INC(pz^, z^);
     INC(zpos); DEC(zcnt)
    END;
    child:= child^.next
   END;
   IF flags * SET16{dbScrollX, dbScrollY} <> SET16{} THEN
    d:= GetDialog(gadget);
    IF (dbBorder IN flags) THEN add:= 2 ELSE add:= 0 END;
    WITH group^ DO
     scroll:= FALSE;
     IF (dbScrollY IN gadget^.flags) THEN LinkGadget(d, gadget, vgad) END;
     IF (dbScrollX IN gadget^.flags) THEN LinkGadget(d, gadget, hgad) END;
     IF gadget^.parent^.type = dDialog THEN
      vgad.leftEdge:= -13; vgad.width:= 10;
      ys:= d^.ta^.ySize;
      IF d^.screen <> NIL THEN
       vgad.topEdge:= d^.screen^.wBorTop + ys + 2;
       IF NOT(screenHires IN d^.screen^.flags) THEN
        vgad.leftEdge:= -9; vgad.width:= 7
       END
      ELSE
       vgad.topEdge:= autoTopEdge + ys + 2
      END;
      vgad.height:= -vgad.topEdge - 11;
      hgad.leftEdge:= 3; hgad.topEdge:= -7;
      hgad.width:= -23; hgad.height:= 6
     ELSE
      vgad.leftEdge:= x + w - add; vgad.topEdge:= y + add;
      vgad.width:= propHmin; vgad.height:= h - add * 2;
      hgad.leftEdge:= x + add; hgad.topEdge:= y + h - add;
      hgad.width:= w - add * 2; hgad.height:= propVmin;
     END;
     IF (w >= 0) AND (dbScrollX IN flags) THEN
      FindScrollerValues(iw, w - add * 2, sx, w DIV 16, hpi.horizBody, hpi.horizPot)
     END;
     IF (h >= 0) AND (dbScrollY IN flags) THEN
      FindScrollerValues(ih, h - add * 2, sy, h DIV 16, vpi.vertBody, vpi.vertPot)
     END
    END;
    IF parent^.type <> dDialog THEN
     IF (dbScrollY IN flags) THEN INC(w, propHmin) END;
     IF (dbScrollX IN flags) THEN INC(h, propVmin) END
    END
   END
  END
 END SetGroupSize;

 PROCEDURE FreeGroup(gadget: GadgetPtr);
  VAR
   group{R.D7}: GroupPtr;
 BEGIN
  WITH gadget^ DO
   IF data <> NIL THEN
    group:= data;
    WITH group^ DO
     IF ss <> NIL THEN FreeMem(ss, SIZE(INT16) * ssize); ss:= NIL END;
     IF sz <> NIL THEN FreeMem(sz, SIZE(INT16) * zsize); sz:= NIL END
    END;
    FreeMem(data, SIZE(Group));
    data:= NIL
   END
  END
 END FreeGroup;

 PROCEDURE CreateGroup(gadget: GadgetPtr): BOOLEAN;
  VAR
   group{R.D7}: GroupPtr;
 BEGIN
  group:= AllocMem(SIZE(Group), MemReqSet{memClear});
  IF group = NIL THEN RETURN FALSE END;
  InitBorders(group^.bb, group^.coords);
  WITH group^ DO
   vgad.gadgetType:= propGadget;
   vgad.flags:= gadgHNone;
   vgad.activation:= ActivationFlagSet{gadgImmediate, relVerify};
   vgad.gadgetRender:= ADR(vimage);
   vgad.specialInfo:= ADR(vpi);
   vgad.userData:= gadget;
   hgad:= vgad;
   hgad.gadgetRender:= ADR(himage);
   hgad.specialInfo:= ADR(hpi);
   vpi.flags:= PropInfoFlagSet{autoKnob, propBorderless, propNewLook, freeVert};
   hpi.flags:= PropInfoFlagSet{autoKnob, propBorderless, propNewLook, freeHoriz}
  END;
  WITH gadget^ DO
   flags:= flags + SET16{dbSelect, dbAutoLeft..dbAutoDown};
   Draw:= DrawGroup;
   Add:= AddGroup;
   Remove:= RemoveGroup;
   Modify:= ModifyGroup;
   GetSize:= GetGroupSize;
   SetSize:= SetGroupSize;
   Free:= FreeGroup;
   data:= group
  END;
  RETURN TRUE
 END CreateGroup;


  (** Buttons, Booleans, Cycles *)
 TYPE
  Button = RECORD
   gad: ID.Gadget;
   bb: BoxBorders;
   coords: BoxPts;
   text: IntuiText;
   mlen: INT16;
   fill: CARD16;
  END;
  ButtonPtr = POINTER TO Button;
  Checkbox = RECORD
   b: Button;
   cbb: BoxBorders;
   ccoords: ARRAY[0..3] OF Point;
  END;
  CheckboxPtr = POINTER TO Checkbox;
  Switch = RECORD
   b: Button;
   bb2: BoxBorders;
   coords2: BoxPts;
  END;
  SwitchPtr = POINTER TO Switch;

 PROCEDURE SetCheckbox(gadget: GadgetPtr; d: DialogPtr);
  VAR
   cb{R.A3}: CheckboxPtr;
   s{R.D7}, z{R.D6}, c{R.D5}: INT16;
 BEGIN
  cb:= gadget^.data;
  WITH cb^ DO
   WITH cbb[0] DO
    drawMode:= jam1; count:= 2;
    frontPen:= d^.darkPen;
    xy:= ADR(ccoords[0])
   END;
   cbb[1]:= cbb[0];
   cbb[1].xy:= ADR(ccoords[2]);
   cbb[0].nextBorder:= ADR(cbb[1]);
   cbb[1].nextBorder:= NIL;
   b.bb[1].nextBorder:= ADR(cbb[2]);
   cbb[2]:= cbb[0]; cbb[3]:= cbb[1];
   cbb[2].frontPen:= 0; cbb[3].frontPen:= 0;
   cbb[2].nextBorder:= ADR(cbb[3]);
   cbb[3].nextBorder:= NIL;
   b.bb[3].nextBorder:= ADR(cbb[0]);
   b.bb[2].frontPen:= d^.lightPen;
   b.bb[3].frontPen:= d^.darkPen;
   s:= b.text.iTextFont^.ySize + autoTopEdge * 2 - 1;
   SetSquareBox(b.coords, s);
   ccoords[0].x:= 3; ccoords[0].y:= 3;
   ccoords[1].x:= s - 3; ccoords[1].y:= s - 3;
   ccoords[2].x:= s - 3; ccoords[2].y:= 3;
   ccoords[3].x:= 3; ccoords[3].y:= s - 3;
   z:= (gadget^.h - s - 1) DIV 2;
   FOR c:= 0 TO 3 DO INC(ccoords[c].y, z) END;
   FOR c:= 0 TO 5 DO INC(b.coords[c].y, z) END
  END
 END SetCheckbox;

 PROCEDURE SetSwitch(gadget: GadgetPtr; d: DialogPtr);
  VAR
   switch{R.A3}: SwitchPtr;
   c: CARD16;
   s{R.D7}, z{R.D6}: INT16;
 BEGIN
  switch:= gadget^.data;
  WITH switch^ DO
   s:= b.text.iTextFont^.ySize + 2;
   InitBorders(bb2, coords2);
   bb2[1].nextBorder:= ADR(b.bb[0]);
   b.bb[3].nextBorder:= ADR(bb2[2]);
   b.coords[0].x:= 0; b.coords[0].y:= s * 2 DIV 3 - 1;
   b.coords[1].x:= 0; b.coords[1].y:= s DIV 3 - 1;
   b.coords[2].x:= s - 1; b.coords[2].y:= s DIV 2 - 1;
   b.coords[3]:= b.coords[2];
   b.coords[4].x:= 1; b.coords[4].y:= s * 2 DIV 3 - 1;
   b.coords[5].x:= 1; b.coords[5].y:= s DIV 3;
   coords2[0].x:= s DIV 2 - 1; coords2[0].y:= s - 1;
   coords2[1].x:= s DIV 3 - 1; coords2[1].y:= 0;
   coords2[2].x:= s * 2 DIV 3 - 1; coords2[2].y:= 0;
   coords2[3].x:= s DIV 3; coords2[3].y:= 1;
   coords2[4].x:= coords2[2].x; coords2[4].y:= 1;
   coords2[5]:= coords2[0];
   b.gad.gadgetRender:= ADR(bb2[0]);
   b.gad.selectRender:= ADR(b.bb[2]);
   b.bb[2].frontPen:= 0; b.bb[3].frontPen:= 0;
   bb2[2].frontPen:= d^.lightPen;
   bb2[3].frontPen:= d^.darkPen;
   z:= (gadget^.h - s) DIV 2;
   FOR c:= 0 TO 5 DO
    INC(b.coords[c].x, s * INT16(b.fill));
    INC(coords2[c].x, s * INT16(b.fill));
    INC(b.coords[c].y, z); INC(coords2[c].y, z)
   END
  END
 END SetSwitch;

 PROCEDURE AddButton(gadget: GadgetPtr);
   (* Only uses button^.gad, first field *)
  VAR
   button{R.D7}: ButtonPtr;
   d{R.D6}: DialogPtr;
 BEGIN
  IF (dbNotVisible IN gadget^.iflags) THEN RETURN END;
  button:= gadget^.data;
  d:= GetDialog(gadget);
  IF d <> NIL THEN
   LinkGadget(d, gadget, button^.gad)
  END
 END AddButton;

 PROCEDURE RemoveButton(gadget: GadgetPtr);
   (* Only uses button^.gad, first field *)
  VAR
   button{R.D7}: ButtonPtr;
   d{R.D6}: DialogPtr;
 BEGIN
  button:= gadget^.data;
  d:= GetDialog(gadget);
  IF d <> NIL THEN
   UnlinkGadget(d, button^.gad)
  END
 END RemoveButton;

 PROCEDURE DrawProgress(gadget: GadgetPtr): INT32;
  VAR
   button{R.D7}: ButtonPtr;
   d{R.D6}: DialogPtr;
   rp{R.D5}: RastPortPtr;
   s1, s2: INT16;
   wide: CARD16;
 BEGIN
  button:= gadget^.data;
  d:= GetDialog(gadget);
  rp:= GetRastPort(gadget);
  IF (d = NIL) OR (rp = NIL) THEN RETURN DialogNoMem END;
  WITH gadget^ DO
   IF (dbBorder IN flags) THEN s1:= 1; s2:= 2 ELSE s1:= 0; s2:= 1 END;
    (*$ OverflowChk:= FALSE *)
   IF w >= 1 THEN
    wide:= CARD32(button^.fill * CARD16(w - 1)) DIV 65536
   ELSE
    wide:= 0
   END;
    (*$ POP OverflowChk *)
   IF INT16(wide) + s1 < w - s2 THEN
    SetAPen(rp, 0);
    RectFill(rp, x + INT16(wide) + s1, y + s1, x + w - s2, y + h - s2)
   END;
   IF wide <> 0 THEN
    SetAPen(rp, d^.fillPen);
    RectFill(rp, x + s1, y + s1, x + INT16(wide), y + h - s2)
   END;
   PrintIText(rp, ADR(button^.text), x, y); WaitBlit
  END;
  RETURN DialogOk
 END DrawProgress;

 PROCEDURE DrawSwitch(gadget: GadgetPtr): INT32;
  VAR
   switch: SwitchPtr;
   d: DialogPtr;
   rp{R.D5}: RastPortPtr;
   count{R.D7}, p{R.D6}: INT16;
 BEGIN
  switch:= gadget^.data;
  d:= GetDialog(gadget);
  rp:= GetRastPort(gadget);
  IF (d = NIL) OR (rp = NIL) THEN RETURN DialogNoMem END;
  WITH switch^ DO
   count:= b.fill;
   WITH gadget^ DO
    WHILE count > 0 DO
     p:= x + INT16(b.text.iTextFont^.ySize + 2) * (count * 2 - 1) DIV 2 - 2;
     SetAPen(rp, d^.lightPen);
     RectFill(rp, p, y - gp * 2, p, y + h - 1);
     SetAPen(rp, d^.darkPen); INC(p);
     RectFill(rp, p, y - gp * 2, p, y + h - 1);
     DEC(count)
    END
   END
  END;
  RETURN DialogOk
 END DrawSwitch;

 PROCEDURE EraseGadget(gadget: GadgetPtr; VAR gad: ID.Gadget): INT16;
  VAR
   window{R.D7}: WindowPtr;
   rp{R.D6}: RastPortPtr;
   pos{R.D5}: INT16;
 BEGIN
  pos:= -1;
  window:= GetWindow(gadget);
  IF window <> NIL THEN
   pos:= IntuitionL.RemoveGadget(window, ADR(gad));
   IF NOT(dbNotVisible IN gadget^.iflags) THEN
    rp:= window^.rPort;
    SetAPen(rp, 0);
    WITH gadget^ DO RectFill(rp, x, y, x + w - 1, y + h - 1) END
   END
  END;
  RETURN pos
 END EraseGadget;

 PROCEDURE ReplaceGadget(gadget: GadgetPtr; VAR gad: ID.Gadget; pos: INT16);
  VAR
   window{R.D7}: WindowPtr;
 BEGIN
  IF (dbNotVisible IN gadget^.iflags) THEN RETURN END;
  window:= GetWindow(gadget);
  IF window <> NIL THEN
   IGNORE IntuitionL.AddGadget(window, ADR(gad), pos);
   RefreshGList(ADR(gad), window, NIL, 1);
   IF gadget^.Draw <> NIL THEN IGNORE gadget^.Draw(gadget) END
  END
 END ReplaceGadget;

 PROCEDURE ModifyButton(gadget: GadgetPtr; tags: TagItemPtr);
  VAR
   button{R.D7}: ButtonPtr;
   flags{R.D6}: SET16;
   gadPos{R.D5}: INT16;
 BEGIN
  button:= gadget^.data;
  flags:= gadget^.flags;
  gadPos:= EraseGadget(gadget, button^.gad);
  WITH button^ DO
   LOOP
    WITH NextTag(tags)^ DO
     IF tag = 0 THEN EXIT
     ELSIF tag = dTEXT THEN text.iText:= addr
     ELSIF tag = dTXTLEN THEN mlen:= data MOD 65536
     ELSIF tag = dTEXTCOLOR THEN
     ELSIF tag = dTEXTMODE THEN
     ELSIF tag = dFILL THEN fill:= data MOD 65536
     END
    END
   END;
    (* Update flags *)
   IF (gadget^.type <> dButton) AND (gadget^.type <> dBool) THEN
    INCL(gad.flags, gadgHImage); EXCL(gad.flags, gadgHBox)
   ELSE
    gad.flags:= gad.flags - GadgetFlagSet{gadgHBox, gadgHImage}
   END;
   gad.flags:= gad.flags - GadgetFlagSet{gadgDisabled, selected};
   gad.activation:= gad.activation - ActivationFlagSet{relVerify, gadgImmediate};
   IF NOT(dbActive IN flags) THEN INCL(gad.flags, gadgDisabled) END;
   IF (dbSelect IN flags) THEN INCL(gad.flags, selected) END;
   IF gadget^.type <> dSwitch THEN
    IF (dbBorder IN flags) THEN
     gad.gadgetRender:= ADR(bb[0]); gad.selectRender:= ADR(bb[2])
    ELSE
     gad.gadgetRender:= NIL; gad.selectRender:= NIL
    END
   END;
   IF gadget^.type <> dLabel THEN
    IF (dbUpEvent IN flags) THEN INCL(gad.activation, relVerify) END;
    IF (dbDownEvent IN flags) THEN INCL(gad.activation, gadgImmediate) END
   END;
   ReplaceGadget(gadget, gad, gadPos)
  END
 END ModifyButton;

 PROCEDURE GetButtonAttr(gadget: GadgetPtr; VAR what: TagItem);
  VAR
   button{R.D7}: ButtonPtr;
   flags{R.D6}: SET16;
 BEGIN
  button:= gadget^.data;
  flags:= gadget^.flags - SET16{dbActive, dbSelect, dbBorder, dbUpEvent, dbDownEvent};
  WITH button^ DO
   IF NOT(gadgDisabled IN gad.flags) THEN INCL(flags, dbActive) END;
   IF (selected IN gad.flags) THEN INCL(flags, dbSelect) END;
   IF (gad.gadgetRender = ADR(bb[0])) THEN INCL(flags, dbBorder) END;
   IF (relVerify IN gad.activation) THEN INCL(flags, dbUpEvent) END;
   IF (gadgImmediate IN gad.activation) THEN INCL(flags, dbDownEvent) END;
   gadget^.flags:= flags;
   WITH what DO
    IF tag = dFLAGS THEN bset:= flags
    ELSIF tag = dTEXT THEN addr:= text.iText
    END
   END
  END
 END GetButtonAttr;

 PROCEDURE GetButtonSize(gadget: GadgetPtr; VAR width, height: INT16);
  VAR
   button{R.A3}: ButtonPtr;
   d{R.D6}: DialogPtr;
   ta{R.D5}: TextAttrPtr;
   minw{R.D4}: INT16;
 BEGIN
  button:= gadget^.data;
  d:= GetDialog(gadget);
  width:= 0; height:= 0;
  WITH button^ DO
   IF d <> NIL THEN
    ta:= d^.ta
   ELSE
    ta:= ADR(topazAttr)
   END;
   IF text.iText <> NIL THEN
    gad.gadgetText:= ADR(text);
    text.iTextFont:= ta
   ELSE
    gad.gadgetText:= NIL
   END;
   IF gad.gadgetText <> NIL THEN
    width:= IntuiTextLength(gad.gadgetText)
   END;
   minw:= INT16(ta^.ySize) * mlen;
   height:= ta^.ySize;
   IF width < minw THEN width:= minw END;
   IF (gadget^.type = dLabel) THEN
    INC(width, (height + 2) * INT16(fill))
   END;
   IF (gadget^.type = dSwitch) THEN
    INC(height, 2);
    INC(width, height * INT16(fill + 1) + 1)
   ELSIF (gadget^.type = dCheckbox) OR (gadget^.type = dRadio) THEN
    INC(height, autoTopEdge * 2);
    INC(width, height + 2)
   ELSIF (dbBorder IN gadget^.flags) THEN (* Borders *)
    INC(width, autoLeftEdge);
    INC(height, autoTopEdge * 2)
   END
  END
 END GetButtonSize;

 PROCEDURE SetButtonSize(gadget: GadgetPtr; width, height: INT16);
  VAR
   button{R.D7}: ButtonPtr;
   window{R.D6}: WindowPtr;
   d{R.D5}: DialogPtr;
   fw, fh: INT16;
 BEGIN
  button:= gadget^.data;
  d:= GetDialog(gadget);
   (* Prepare for drawing *)
  WITH button^ DO
   SetBordersPens(bb, d);
   IF gadget^.textColor <> -1 THEN
    text.frontPen:= FindBestPen(gadget^.textColor, 0)
   ELSE
    text.frontPen:= d^.darkPen
   END;
   LinkGadget(d, gadget, gad)
  END;
   (* Set size *)
  WITH gadget^ DO
   WITH button^ DO
    fw:= w; fh:= h;
    PlaceBox(x, y, w, h, x, y, width, height, flags);
    text.leftEdge:= (w - fw) DIV 2;
    text.topEdge:= (h - fh) DIV 2;
    IF (type = dLabel) THEN
     text.leftEdge:= (text.iTextFont^.ySize + 2) * fill
    END;
    IF (type = dSwitch) THEN
     text.leftEdge:= (text.iTextFont^.ySize + 2) * (fill + 1) + 1;
     INC(text.topEdge)
    ELSIF (type = dCheckbox) OR (type = dRadio) THEN
     text.leftEdge:= text.iTextFont^.ySize + autoTopEdge * 2 + 2;
     INC(text.topEdge, autoTopEdge)
    ELSIF (dbBorder IN flags) THEN
     INC(text.leftEdge, autoLeftEdge DIV 2);
     INC(text.topEdge, autoTopEdge)
    END;
    gad.leftEdge:= x; gad.topEdge:= y;
    gad.width:= w; gad.height:= h;
    IF type = dSwitch THEN
     SetSwitch(gadget, d)
    ELSIF type = dCheckbox THEN
     SetCheckbox(gadget, d)
    ELSE
     SetBox(gadget, coords)
    END
   END
  END
 END SetButtonSize;

 PROCEDURE FreeButton(gadget: GadgetPtr);
 BEGIN
  WITH gadget^ DO
   IF data <> NIL THEN
    FreeMem(data, SIZE(Button));
    data:= NIL
   END
  END
 END FreeButton;

 PROCEDURE FreeCheckbox(gadget: GadgetPtr);
 BEGIN
  WITH gadget^ DO
   IF data <> NIL THEN
    FreeMem(data, SIZE(Checkbox));
    data:= NIL
   END
  END
 END FreeCheckbox;

 PROCEDURE FreeSwitch(gadget: GadgetPtr);
 BEGIN
  WITH gadget^ DO
   IF data <> NIL THEN
    FreeMem(data, SIZE(Switch));
    data:= NIL
   END
  END
 END FreeSwitch;

 PROCEDURE InitButton(VAR button: Button; gadget: GadgetPtr);
 BEGIN
  WITH button DO
   InitBorders(bb, coords);
   text.drawMode:= jam1;
   gad.gadgetType:= boolGadget;
   gad.flags:= gadgHNone;
   WITH gadget^ DO
    IF type <> dLabel THEN
     gad.activation:= ActivationFlagSet{relVerify}
    ELSE
     gad.activation:= ActivationFlagSet{}
    END;
    IF (type <> dButton) AND (type <> dCycle) AND
       (type <> dProgress) AND (type <> dLabel) THEN
     INCL(gad.activation, toggleSelect)
    END;
    IF type <> dLabel THEN
     gad.gadgetRender:= ADR(bb[0]);
     gad.selectRender:= ADR(bb[2])
    END
   END;
   gad.userData:= gadget
  END;
  WITH gadget^ DO
   IF type = dLabel THEN
    flags:= flags + SET16{dbActive}
   ELSE
    flags:= flags + SET16{dbBorder, dbActive, dbUpEvent}
   END;
   Add:= AddButton;
   Remove:= RemoveButton;
   Modify:= ModifyButton;
   GetAttr:= GetButtonAttr;
   GetSize:= GetButtonSize;
   SetSize:= SetButtonSize;
   Free:= FreeButton;
   data:= ADR(button)
  END
 END InitButton;

 PROCEDURE CreateButton(gadget: GadgetPtr): BOOLEAN;
  VAR
   button{R.D7}: ButtonPtr;
 BEGIN
  button:= AllocMem(SIZE(Button), MemReqSet{memClear});
  IF button = NIL THEN RETURN FALSE END;
  InitButton(button^, gadget);
  RETURN TRUE
 END CreateButton;

 PROCEDURE CreateProgress(gadget: GadgetPtr): BOOLEAN;
 BEGIN
  IF CreateButton(gadget) THEN
   WITH gadget^ DO
    Draw:= DrawProgress;
    INCL(flags, dbSelect);
    EXCL(flags, dbUpEvent)
   END;
   RETURN TRUE
  ELSE
   RETURN FALSE
  END
 END CreateProgress;

 PROCEDURE CreateCheckbox(gadget: GadgetPtr): BOOLEAN;
  VAR
   cb{R.D7}: CheckboxPtr;
 BEGIN
  cb:= AllocMem(SIZE(Checkbox), MemReqSet{memClear});
  IF cb = NIL THEN RETURN FALSE END;
  InitButton(cb^.b, gadget);
  WITH gadget^ DO
   Free:= FreeCheckbox;
   data:= cb
  END;
  RETURN TRUE
 END CreateCheckbox;

 PROCEDURE CreateSwitch(gadget: GadgetPtr): BOOLEAN;
  VAR
   switch{R.D7}: SwitchPtr;
 BEGIN
  switch:= AllocMem(SIZE(Switch), MemReqSet{memClear});
  IF switch = NIL THEN RETURN FALSE END;
  InitButton(switch^.b, gadget);
  WITH gadget^ DO
   Draw:= DrawSwitch;
   Free:= FreeSwitch;
   data:= switch
  END;
  RETURN TRUE
 END CreateSwitch;

  (** Scrollers, Sliders *)
 TYPE
  Prop = RECORD
   gad: ID.Gadget;
   pi: PropInfo;
   image: Image;
   fill, span: CARD16;
  END;
  PropPtr = POINTER TO Prop;

 PROCEDURE ModifyProp(gadget: GadgetPtr; tags: TagItemPtr);
  VAR
   prop{R.D7}: PropPtr;
   flags{R.D6}: SET16;
   gadPos{R.D5}: INT16;
 BEGIN
  prop:= gadget^.data;
  flags:= gadget^.flags;
  gadPos:= EraseGadget(gadget, prop^.gad);
  WITH prop^ DO
   LOOP
    WITH NextTag(tags)^ DO
     IF tag = 0 THEN EXIT
     ELSIF tag = dFILL THEN fill:= data MOD 65536
     ELSIF tag = dSPAN THEN span:= data MOD 65536
     END
    END
   END;
   IF span = 0 THEN span:= 1 END;
    (* Update flags *)
   gad.flags:= gad.flags - GadgetFlagSet{gadgDisabled, selected};
   gad.activation:= gad.activation - ActivationFlagSet{relVerify, followMouse};
   IF NOT(dbActive IN flags) THEN INCL(gad.flags, gadgDisabled) END;
   IF (dbUpEvent IN flags) THEN INCL(gad.activation, relVerify) END;
   IF (dbDownEvent IN flags) THEN INCL(gad.activation, followMouse) END;
    (* Update fill & span *)
   IF (dbVDir IN gadget^.flags) THEN
    INCL(pi.flags, freeVert); EXCL(pi.flags, freeHoriz)
   ELSE
    INCL(pi.flags, freeHoriz); EXCL(pi.flags, freeVert)
   END;
   IF gadget^.type = dSlider THEN
    IF (dbVDir IN flags) THEN
     pi.vertPot:= fill;
     pi.vertBody:= span
    ELSE
     pi.horizPot:= fill;
     pi.horizBody:= span
    END
   ELSE
    IF (dbVDir IN flags) THEN
     FindScrollerValues(65535, span, fill, span DIV 16, pi.vertBody, pi.vertPot)
    ELSE
     FindScrollerValues(65535, span, fill, span DIV 16, pi.horizBody, pi.horizPot)
    END
   END;
   ReplaceGadget(gadget, gad, gadPos)
  END
 END ModifyProp;

 PROCEDURE GetPropAttr(gadget: GadgetPtr; VAR what: TagItem);
  VAR
   prop{R.D7}: PropPtr;
   flags{R.D6}: SET16;
 BEGIN
  prop:= gadget^.data;
  flags:= gadget^.flags - SET16{dbActive, dbSelect, dbUpEvent, dbDownEvent};
  WITH prop^ DO
   IF NOT(gadgDisabled IN gad.flags) THEN INCL(flags, dbActive) END;
   IF (relVerify IN gad.activation) THEN INCL(flags, dbUpEvent) END;
   IF (followMouse IN gad.activation) THEN INCL(flags, dbDownEvent) END;
   gadget^.flags:= flags;
   IF (dbVDir IN flags) THEN
    fill:= FindScrollerTop(65535, span, pi.vertPot)
   ELSE
    fill:= FindScrollerTop(65535, span, pi.horizPot)
   END;
   WITH what DO
    IF tag = dFLAGS THEN bset:= flags
    ELSIF tag = dFILL THEN data:= fill
    ELSIF tag = dSPAN THEN data:= span
    END
   END
  END
 END GetPropAttr;

 PROCEDURE GetPropSize(gadget: GadgetPtr; VAR width, height: INT16);
 BEGIN
  WITH gadget^ DO
   IF (dbVDir IN flags) THEN
    width:= propHmin; height:= propVmin * 2
   ELSE
    width:= propHmin * 2; height:= propVmin
   END
  END
 END GetPropSize;

 PROCEDURE SetPropSize(gadget: GadgetPtr; width, height: INT16);
  VAR
   prop{R.D7}: PropPtr;
   d{R.D6}: DialogPtr;
 BEGIN
  prop:= gadget^.data;
  d:= GetDialog(gadget);
   (* Prepare for drawing *)
  WITH prop^ DO
   LinkGadget(d, gadget, gad);
    (* Set size *)
   WITH gadget^ DO
    PlaceBox(x, y, w, h, x, y, width, height, flags);
    gad.leftEdge:= x; gad.topEdge:= y;
    gad.width:= w; gad.height:= h
   END
  END
 END SetPropSize;

 PROCEDURE FreeProp(gadget: GadgetPtr);
 BEGIN
  WITH gadget^ DO
   IF data <> NIL THEN
    FreeMem(data, SIZE(Prop));
    data:= NIL
   END
  END
 END FreeProp;

 PROCEDURE CreateProp(gadget: GadgetPtr): BOOLEAN;
  VAR
   prop{R.D7}: PropPtr;
 BEGIN
  prop:= AllocMem(SIZE(Prop), MemReqSet{memClear});
  IF prop = NIL THEN RETURN FALSE END;
  WITH prop^ DO
   gad.gadgetType:= propGadget;
   gad.flags:= gadgHNone;
   gad.activation:= ActivationFlagSet{relVerify};
   gad.gadgetRender:= ADR(image);
   gad.specialInfo:= ADR(pi);
   gad.userData:= gadget;
   span:= 8192;
   pi.flags:= PropInfoFlagSet{autoKnob, propNewLook};
   IF gadget^.type = dSlider THEN INCL(pi.flags, propBorderless) END;
   pi.vertBody:= 1; pi.horizBody:= 1
  END;
  WITH gadget^ DO
   flags:= flags + SET16{dbBorder, dbActive, dbUpEvent, dbAutoLeft..dbAutoDown};
   Add:= AddButton;
   Remove:= RemoveButton;
   Modify:= ModifyProp;
   GetAttr:= GetPropAttr;
   GetSize:= GetPropSize;
   SetSize:= SetPropSize;
   Free:= FreeProp;
   data:= prop
  END;
  RETURN TRUE
 END CreateProp;


  (** TextEdit, IntEdit *)
 TYPE
  TextEdit = RECORD
   gad: ID.Gadget;
   si: StringInfo;
   bb: BoxBorders;
   coords1, coords2: BoxPts;
   tbuff: ARRAY[0..11] OF CHAR;
  END;
  TextEditPtr = POINTER TO TextEdit;

 PROCEDURE ModifyTextEdit(gadget: GadgetPtr; tags: TagItemPtr);
  VAR
   text: ARRAY[0..11] OF CHAR;
   value: LONGINT;
   pos: CARD16;
   neg: BOOLEAN;
   te: TextEditPtr;
   flags: SET16;
   gadPos: INT16;
 BEGIN
  te:= gadget^.data;
  flags:= gadget^.flags;
  gadPos:= EraseGadget(gadget, te^.gad);
  WITH te^ DO
   LOOP
    WITH NextTag(tags)^ DO
     IF tag = 0 THEN EXIT
     ELSIF tag = dTEXT THEN si.buffer:= addr
     ELSIF tag = dTXTLEN THEN si.maxChars:= data
     ELSIF tag = dINTVAL THEN si.longInt:= lint
     END
    END
   END;
    (* Update flags *)
   gad.flags:= gad.flags - GadgetFlagSet{gadgDisabled, selected};
   gad.activation:= gad.activation - ActivationFlagSet{relVerify, gadgImmediate};
   IF NOT(dbActive IN flags) THEN INCL(gad.flags, gadgDisabled) END;
   IF (dbUpEvent IN flags) THEN INCL(gad.activation, relVerify) END;
   IF (dbDownEvent IN flags) THEN INCL(gad.activation, gadgImmediate) END;
   IF gadget^.type = dIntEdit THEN
    value:= ABS(si.longInt);
    neg:= (si.longInt < 0);
    pos:= 11; text[11]:= 0C;
    REPEAT
     DEC(pos);
     text[pos]:= CHR(48 + value MOD 10);
     value:= value DIV 10
    UNTIL value = 0;
    IF neg THEN DEC(pos); text[pos]:= "-" END;
    CopyStr(ADR(text[pos]), si.buffer, si.maxChars)
   END;
   ReplaceGadget(gadget, gad, gadPos)
  END
 END ModifyTextEdit;

 PROCEDURE DrawTextEdit(gadget: GadgetPtr): INT32;
  VAR
   te{R.D7}: TextEditPtr;
   rp{R.D6}: RastPortPtr;
 BEGIN
  WITH gadget^ DO
   IF (dbBorder IN flags) THEN
    te:= data;
    rp:= GetRastPort(gadget);
    WITH te^ DO
     SetBordersPens(bb, GetDialog(gadget));
     DrawBorder(rp, ADR(bb[0]), x, y)
    END
   END
  END;
  RETURN DialogOk
 END DrawTextEdit;

 PROCEDURE GetTextEditAttr(gadget: GadgetPtr; VAR what: TagItem);
  VAR
   te{R.D7}: TextEditPtr;
   flags{R.D6}: SET16;
 BEGIN
  te:= gadget^.data;
  flags:= gadget^.flags - SET16{dbActive, dbSelect, dbUpEvent, dbDownEvent};
  WITH te^ DO
   IF NOT(gadgDisabled IN gad.flags) THEN INCL(flags, dbActive) END;
   IF (selected IN gad.flags) THEN INCL(flags, dbSelect) END;
   IF (relVerify IN gad.activation) THEN INCL(flags, dbUpEvent) END;
   IF (gadgImmediate IN gad.activation) THEN INCL(flags, dbDownEvent) END;
   gadget^.flags:= flags;
   WITH what DO
    IF tag = dFLAGS THEN bset:= flags
    ELSIF tag = dTEXT THEN addr:= si.buffer
    ELSIF tag = dTXTLEN THEN data:= si.maxChars
    ELSIF tag = dINTVAL THEN lint:= si.longInt
    END
   END
  END
 END GetTextEditAttr;

 PROCEDURE GetTextEditSize(gadget: GadgetPtr; VAR width, height: INT16);
  VAR
   te{R.D7}: TextEditPtr;
   tta{R.D6}: TextAttrPtr;
   d{R.D5}: DialogPtr;
   cnt: INT16;
 BEGIN
  te:= gadget^.data;
  d:= GetDialog(gadget);
  width:= 0; height:= 0;
  WITH te^ DO
   IF d <> NIL THEN
    tta:= d^.ta
   ELSE
    tta:= ADR(topazAttr)
   END;
   IF si.maxChars < 8 THEN cnt:= si.maxChars ELSE cnt:= 8 END;
   width:= cnt * INT16(tta^.ySize);
   height:= tta^.ySize;
   IF (dbBorder IN gadget^.flags) THEN
    INC(width, autoLeftEdge);
    INC(height, autoTopEdge * 2)
   END
  END
 END GetTextEditSize;

 PROCEDURE SetTextEditSize(gadget: GadgetPtr; width, height: INT16);
  VAR
   te{R.D7}: TextEditPtr;
   d{R.D6}: DialogPtr;
   fh{R.D5}, c{R.D4}: INT16;
 BEGIN
  te:= gadget^.data;
  d:= GetDialog(gadget);
   (* Prepare for drawing *)
  WITH te^ DO
   SetBordersPens(bb, d);
   LinkGadget(d, gadget, gad);
    (* Set size *)
   WITH gadget^ DO
    fh:= h;
    PlaceBox(x, y, w, h, x, y, width, height, flags);
    gad.leftEdge:= x; gad.width:= width;
    gad.topEdge:= y + (h - fh) DIV 2;
    gad.height:= fh;
    IF (dbBorder IN flags) THEN
     INC(gad.topEdge, autoTopEdge);
     INC(gad.leftEdge, autoLeftEdge DIV 2);
     DEC(gad.width, autoLeftEdge);
     DEC(gad.height, autoTopEdge * 2);
    END;
    SetBox(gadget, coords1);
    DEC(w, 2); DEC(h, 2);
    SetBox(gadget, coords2);
    INC(w, 2); INC(h, 2);
    FOR c:= 0 TO 5 DO
     INC(coords2[c].x); INC(coords2[c].y)
    END
   END
  END
 END SetTextEditSize;

 PROCEDURE FreeTextEdit(gadget: GadgetPtr);
 BEGIN
  WITH gadget^ DO
   IF data <> NIL THEN
    FreeMem(data, SIZE(TextEdit));
    data:= NIL
   END
  END
 END FreeTextEdit;

 PROCEDURE CreateTextEdit(gadget: GadgetPtr): BOOLEAN;
  VAR
   te{R.D7}: TextEditPtr;
 BEGIN
  te:= AllocMem(SIZE(TextEdit), MemReqSet{memClear});
  IF te = NIL THEN RETURN FALSE END;
  WITH te^ DO
   InitBorders(bb, coords1);
   bb[2].xy:= ADR(coords2[0]);
   bb[3].xy:= ADR(coords2[3]);
   bb[1].nextBorder:= ADR(bb[2]);
   gad.gadgetType:= strGadget;
   gad.flags:= GadgetFlagSet{};
   gad.activation:= ActivationFlagSet{relVerify};
   IF gadget^.type = dIntEdit THEN INCL(gad.activation, longint) END;
   gad.specialInfo:= ADR(si);
   si.buffer:= ADR(tbuff);
   si.maxChars:= 12;
   tbuff[0]:= 0C;
   gad.userData:= gadget;
  END;
  WITH gadget^ DO
   flags:= flags + SET16{dbBorder, dbActive, dbUpEvent, dbAutoLeft, dbAutoRight};
   Add:= AddButton;
   Remove:= RemoveButton;
   Modify:= ModifyTextEdit;
   Draw:= DrawTextEdit;
   GetAttr:= GetTextEditAttr;
   GetSize:= GetTextEditSize;
   SetSize:= SetTextEditSize;
   Free:= FreeTextEdit;
   data:= te
  END;
  RETURN TRUE
 END CreateTextEdit;

  (** Area, Image *)
 PROCEDURE ModifyImage(gadget: GadgetPtr; tags: TagItemPtr);
 BEGIN
  LOOP
   WITH NextTag(tags)^ DO
    IF (tag = dAREA) OR (tag = dIMAGE) THEN gadget^.data:= addr END
   END
  END
 END ModifyImage;

 PROCEDURE GetImageAttr(gadget: GadgetPtr; VAR what: TagItem);
 BEGIN
  IF (what.tag = dAREA) OR (what.tag = dIMAGE) THEN
   what.addr:= gadget^.data
  END
 END GetImageAttr;

 PROCEDURE AddArea(gadget: GadgetPtr);
  VAR
   d{R.D7}: DialogPtr;
 BEGIN
  IF gadget^.type = dArea THEN
   d:= GetDialog(gadget);
   IF d <> NIL THEN INC(d^.areas) END
  END
 END AddArea;

 PROCEDURE RemoveArea(gadget: GadgetPtr);
  VAR
   d{R.D7}: DialogPtr;
 BEGIN
  IF gadget^.type = dArea THEN
   d:= GetDialog(gadget);
   IF d <> NIL THEN DEC(d^.areas) END
  END
 END RemoveArea;

 PROCEDURE DrawImage(gadget: GadgetPtr): INT32;
  VAR
   group{R.D7}: GadgetPtr;
   rp{R.D6}: RastPortPtr;
   sx, sy, dx, dy, width, height: INT16;
 BEGIN
  WITH gadget^ DO
   IF (data = NIL) OR (type <> dImage) THEN RETURN DialogOk END;
   sx:= 0; sy:= 0;
   dx:= x; dy:= y;
   width:= w; height:= h;
   group:= parent
  END;
  IF group = NIL THEN RETURN DialogOk END;
  WITH group^ DO
   IF x > dx THEN INC(sx, x - dx); DEC(width, x - dx); dx:= x END;
   IF y > dy THEN INC(sy, y - dy); DEC(height, y - dy); dy:= y END;
   IF width > w THEN width:= w END;
   IF height > h THEN height:= h END
  END;
  IF (width <= 0) OR (height <= 0) THEN RETURN DialogOk END;
  IF (RefreshImage = NIL) THEN HALT END;
  rp:= GetRastPort(gadget);
  WITH gadget^ DO
   RefreshImage(rp, data, sx, sy, width, height, dx, dy)
  END;
  RETURN DialogOk
 END DrawImage;

 PROCEDURE CreateImage(gadget: GadgetPtr): BOOLEAN;
 BEGIN
  WITH gadget^ DO
   Modify:= ModifyImage;
   Draw:= DrawImage;
   Add:= AddArea;
   Remove:= RemoveArea;
   GetAttr:= GetImageAttr;
   data:= NIL
  END
 END CreateImage;

  (** Generic calls *)
 PROCEDURE AllocGadget(type: INT16): GadgetPtr;
  VAR
   gadget{R.D7}: GadgetPtr;
   Create{R.D6}: CreateProc;
 BEGIN
  gadget:= AllocMem(SIZE(Gadget), MemReqSet{memClear});
  IF gadget <> NIL THEN
   gadget^.type:= type;
   IF type <> dDialog THEN
    gadget^.gp:= 1;
   END;
   CASE type OF
     dDialog: Create:= CreateDialog
    |dGroup: Create:= CreateGroup;
    |dButton, dBool, dCycle, dLabel: Create:= CreateButton
    |dProgress: Create:= CreateProgress
    |dCheckbox, dRadio: Create:= CreateCheckbox
    |dSwitch: Create:= CreateSwitch
    |dSlider, dScroller: Create:= CreateProp
    |dTextEdit, dIntEdit: Create:= CreateTextEdit
(*    |dChoice: Create:= CreateChoice
    |dArea, dImage: Create:= CreateCustom *)
    ELSE Create:= NIL
   END;
   IF (Create <> NIL) AND Create(gadget) THEN
    RETURN gadget
   END;
   FreeMem(gadget, SIZE(Gadget))
  END;
  RETURN NIL
 END AllocGadget;

 PROCEDURE ModifyGadget(gadget: GadgetPtr; tags: TagItemPtr);
  VAR
   oldTags{R.D7}: TagItemPtr;
   dummy: TagItem;
   nflags{R.D6}, nmask{R.D5}: SET16;
 BEGIN
  oldTags:= tags;
  nmask:= SET16{}; nflags:= SET16{};
  WITH gadget^ DO
   IF GetAttr <> NIL THEN
    dummy.tag:= 0;
    GetAttr(gadget, dummy) (* Update gadget flags *)
   END;
   LOOP
    WITH NextTag(tags)^ DO
     IF tag = 0 THEN EXIT
     ELSIF tag = dFLAGS THEN nflags:= nflags + bset; nmask:= nmask + nflags
     ELSIF tag = dRFLAGS THEN nmask:= nmask + bset
     ELSIF tag = dMASK THEN nmask:= bset
     ELSIF tag = dGAPSIZE THEN gp:= lint
     ELSIF tag = dXPOS THEN x:= CharToPixel(lint)
     ELSIF tag = dYPOS THEN y:= CharToPixel(lint)
     ELSIF tag = dWIDTH THEN mw:= CharToPixel(ABS(lint)); w:= mw
     ELSIF tag = dHEIGHT THEN mh:= CharToPixel(ABS(lint)); h:= mh
     ELSIF tag = dTEXTCOLOR THEN textColor:= data
     ELSIF tag = dUDATA THEN udata:= addr
     ELSIF tag = dUID THEN uid:= lint
     END
    END
   END;
   flags:= flags - nmask + (nflags * nmask);
   IF Modify <> NIL THEN
    Modify(gadget, oldTags)
   END
  END
 END ModifyGadget;

 PROCEDURE CreateGadget(type: INT16; tags: TagItemPtr): GadgetPtr;
  VAR
   gadget{R.D7}: GadgetPtr;
 BEGIN
  gadget:= AllocGadget(type);
  IF gadget <> NIL THEN
   gadget^.textColor:= -1;
   ModifyGadget(gadget, tags)
  END;
  RETURN gadget
 END CreateGadget;

 PROCEDURE GetGadgetAttr(gadget: GadgetPtr; VAR what: TagItem);
  VAR
   scrw, scrh, dummy: INT16;
 BEGIN
  IF gadget <> NIL THEN
   WITH gadget^ DO
    WITH what DO
     IF tag = dFLAGS THEN bset:= flags
     ELSIF tag = dGAPSIZE THEN lint:= gp
     ELSIF tag = dXPOS THEN lint:= PixelToChar(x)
     ELSIF tag = dYPOS THEN lint:= PixelToChar(y)
     ELSIF tag = dWIDTH THEN lint:= PixelToChar(w)
     ELSIF tag = dHEIGHT THEN lint:= PixelToChar(h)
     ELSIF tag = dUDATA THEN addr:= udata
     ELSIF tag = dUID THEN lint:= uid
     END
    END;
    IF GetAttr <> NIL THEN GetAttr(gadget, what) END
   END
  ELSE
   GetScreenSizes(NIL, scrw, scrh, dummy, dummy, dummy, dummy, SET16{});
   WITH what DO
    IF tag = dWIDTH THEN lint:= PixelToChar(scrw)
    ELSIF tag = dHEIGHT THEN lint:= PixelToChar(scrh)
    ELSIF tag = dGAPSIZE THEN lint:= PixelToChar(CharToPixel(100) + 4)
    ELSE tag:= 0
    END
   END
  END
 END GetGadgetAttr;

 PROCEDURE GetGadgetSize(gadget: GadgetPtr; VAR sw, sh: INT16);
 BEGIN
  WITH gadget^ DO
   sw:= w; sh:= h;
   IF GetSize <> NIL THEN GetSize(gadget, sw, sh) END;
   IF (sw < mw) THEN sw:= mw END;
   IF (sh < mh) THEN sh:= mh END;
   w:= sw; h:= sh;
   INC(sw, gp * 2); INC(sh, gp * 2)
  END
 END GetGadgetSize;

 PROCEDURE SetGadgetSize(gadget: GadgetPtr; sw, sh: INT16);
 BEGIN
  WITH gadget^ DO
   DEC(sw, gp * 2); DEC(sh, gp * 2);
   INC(x, gp); INC(y, gp);
   IF SetSize <> NIL THEN
    SetSize(gadget, sw, sh)
   ELSE
    w:= sw; h:= sh
   END
  END
 END SetGadgetSize;

 PROCEDURE SetGadgetRect(gadget: GadgetPtr; sx, sy, sw, sh: INT16);
  VAR
   w, h: INT16;
 BEGIN
  GetGadgetSize(gadget, w, h);
  WITH gadget^ DO
   x:= sx - gp; y:= sy - gp;
   SetGadgetSize(gadget, sw + gp * 2, sh + gp * 2)
  END
 END SetGadgetRect;

 PROCEDURE DrawGadget(gadget: GadgetPtr): INT32;
  VAR
   child{R.D7}: GadgetPtr;
   res{R.D6}: INT32;
 BEGIN
  IF gadget = NIL THEN RETURN DialogNoMem END;
  res:= DialogOk;
  WITH gadget^ DO
   IF (dbNotVisible IN iflags) THEN RETURN DialogOk END;
   IF Draw <> NIL THEN res:= Draw(gadget) END;
   child:= childs;
   WHILE child <> NIL DO
    IF res <> DialogOk THEN RETURN res END;
    res:= DrawGadget(child);
    child:= child^.next
   END
  END;
  RETURN res
 END DrawGadget;

 PROCEDURE HideGadget(gadget: GadgetPtr);
  VAR
   d{R.D7}: DialogPtr;
 BEGIN
  IF gadget^.type = dDialog THEN
   HideDialog(gadget)
  ELSE
   INCL(gadget^.iflags, dbNotVisible);
   d:= GetDialog(gadget);
   IF d <> NIL THEN d^.refresh:= TRUE END
  END
 END HideGadget;

 PROCEDURE ShowGadget(gadget: GadgetPtr);
  VAR
   d{R.D7}: DialogPtr;
 BEGIN
  IF gadget^.type = dDialog THEN
   IF DrawDialog(gadget) = DialogNoMem THEN memLow:= TRUE END
  ELSE
   EXCL(gadget^.iflags, dbNotVisible);
   d:= GetDialog(gadget);
   IF d <> NIL THEN d^.refresh:= TRUE END
  END
 END ShowGadget;

 PROCEDURE RefreshGadget(gadget: GadgetPtr): INT32;
  VAR
   sw, sh: INT16;
 BEGIN
  IF gadget = NIL THEN RETURN DialogNoMem END;
  WITH gadget^ DO
   GetGadgetSize(gadget, sw, sh);
   SetGadgetSize(gadget, sw, sh);
   RETURN DrawGadget(gadget)
  END
 END RefreshGadget;

 PROCEDURE BeginRefresh(gadget: GadgetPtr; VAR x, y, w, h: INT16);
  VAR
   window{R.D7}: WindowPtr;
 BEGIN
  window:= GetWindow(gadget);
  WITH gadget^ DO
   IF (RefreshArea = NIL) THEN HALT END;
   IF type = dArea THEN
    RefreshArea(window, data, x, y, w, h, x, y)
   END
  END
 END BeginRefresh;

 PROCEDURE EndRefresh(gadget: GadgetPtr): BOOLEAN;
  VAR
   window{R.D7}: WindowPtr;
 BEGIN
  window:= GetWindow(gadget);
  WITH gadget^ DO
   IF (EndRefreshArea = NIL) THEN HALT END;
   IF type = dArea THEN
    RETURN EndRefreshArea(window, data)
   ELSE
    RETURN TRUE
   END
  END
 END EndRefresh;

 PROCEDURE AttachGadget(gadget: GadgetPtr);
  VAR
   child{R.D7}: GadgetPtr;
 BEGIN
  WITH gadget^ DO
   child:= childs;
   WHILE child <> NIL DO
    AttachGadget(child);
    child:= child^.next
   END;
   IF Add <> NIL THEN Add(gadget) END
  END
 END AttachGadget;

 PROCEDURE AddGadget(parent, gadget, before: GadgetPtr);
  VAR
   lastGadgetPtr{R.D7}: POINTER TO GadgetPtr;
 BEGIN
  IF parent = NIL THEN RETURN END;
  IF (gadget = NIL) OR (dbNoMem IN gadget^.iflags) THEN
   INCL(parent^.iflags, dbNoMem); RETURN
  END;
  IF (parent^.type = dDialog) AND (parent^.childs <> NIL) THEN
   parent:= parent^.childs
  END;
  INC(parent^.nbChilds);
  lastGadgetPtr:= ADR(parent^.childs);
  WHILE lastGadgetPtr^ <> before DO
   lastGadgetPtr:= ADR(lastGadgetPtr^^.next)
  END;
  gadget^.next:= before;
  lastGadgetPtr^:= gadget;
  gadget^.parent:= parent;
  AttachGadget(gadget)
 END AddGadget;

 PROCEDURE AddNewGadget(parent: GadgetPtr; type: INT16; tags: TagItemPtr): GadgetPtr;
  VAR
   gadget{R.D7}: GadgetPtr;
 BEGIN
  IF (parent = NIL) OR (dbNoMem IN parent^.iflags) THEN RETURN NIL END;
  gadget:= CreateGadget(type, tags);
  IF gadget <> NIL THEN
   AddGadget(parent, gadget, NIL)
  END;
  RETURN gadget
 END AddNewGadget;

 PROCEDURE DetachGadget(gadget: GadgetPtr);
  VAR
   child{R.D7}: GadgetPtr;
 BEGIN
  WITH gadget^ DO
   child:= childs;
   WHILE child <> NIL DO
    DetachGadget(child);
    child:= child^.next
   END;
   IF Remove <> NIL THEN Remove(gadget) END
  END
 END DetachGadget;

 PROCEDURE RemoveGadget(gadget: GadgetPtr);
  VAR
   lastGadgetPtr{R.D7}: POINTER TO GadgetPtr;
 BEGIN
  lastGadgetPtr:= ADR(gadget^.parent^.childs);
  LOOP
   IF lastGadgetPtr^ = NIL THEN gadget^.parent:= NIL; RETURN END;
   IF lastGadgetPtr^ = gadget THEN EXIT END;
   lastGadgetPtr:= ADR(lastGadgetPtr^^.next)
  END;
  WITH gadget^ DO
   DEC(parent^.nbChilds);
   lastGadgetPtr^:= next;
   next:= NIL;
   DetachGadget(gadget);
   parent:= NIL
  END;
 END RemoveGadget;

 PROCEDURE FreeGadget(VAR gadget: GadgetPtr);
  VAR
   tofree{R.D7}: GadgetPtr;
 BEGIN
  IF gadget <> NIL THEN
   WITH gadget^ DO
    IF type <> dDialog THEN (* Dialogs removes their only child (group) *)
     WHILE childs <> NIL DO
      tofree:= childs;
      RemoveGadget(tofree)
     END
    END;
    IF parent <> NIL THEN RemoveGadget(gadget) END;
    IF Free <> NIL THEN Free(gadget) END
   END;
   FreeMem(gadget, SIZE(Gadget));
   gadget:= NIL
  END
 END FreeGadget;

 PROCEDURE DeepFreeGadget(VAR gadget: GadgetPtr);
  VAR
   tofree: GadgetPtr;
 BEGIN
  IF gadget <> NIL THEN
   WITH gadget^ DO
    WHILE childs <> NIL DO
     tofree:= childs;
     DeepFreeGadget(tofree)
    END;
    IF parent <> NIL THEN RemoveGadget(gadget) END;
    IF Free <> NIL THEN Free(gadget) END
   END;
   FreeMem(gadget, SIZE(Gadget));
   gadget:= NIL
  END;
 END DeepFreeGadget;

 PROCEDURE GadgetToAddress(gadget{R.D0}: GadgetPtr): ADDRESS;
 BEGIN
  RETURN gadget
 END GadgetToAddress;

 PROCEDURE AddressToGadget(addr{R.D0}: ADDRESS): GadgetPtr;
 BEGIN
  RETURN addr
 END AddressToGadget;

 VAR
  lastG: ID.GadgetPtr;

 PROCEDURE CheckGadget(msg: IntuiMessagePtr): BOOLEAN;
  VAR
   g: ID.GadgetPtr;
   gadget: GadgetPtr;
   group: GroupPtr;
   d: DialogPtr;
   window: WindowPtr;
   rp: RastPortPtr;
   osx, osy, subV, subH: INT16;
 BEGIN
  WITH msg^ DO
   IF class = IDCMPFlagSet{intuiTicks} THEN
    g:= lastG
   ELSE
    g:= CAST(ID.GadgetPtr, msg^.iAddress)
   END;
   gadget:= NIL; lastG:= g;
   IF g <> NIL THEN
    gadget:= g^.userData
   END;
   IF gadget <> NIL THEN
    IF gadget^.type = dGroup THEN
     window:= GetWindow(gadget);
     LockR(globals);
     WITH globals^ DO
      IF class = IDCMPFlagSet{gadgetDown} THEN
       IGNORE ModifyIDCMP(window, idcmp + IDCMPFlagSet{intuiTicks});
       Unlock(globals); RETURN TRUE
      ELSIF class = IDCMPFlagSet{gadgetUp} THEN
       IGNORE ModifyIDCMP(window, idcmp);
       lastG:= NIL
      END
     END;
     Unlock(globals);
     WITH gadget^ DO
      subV:= 0; subH:= 0;
      IF parent^.type <> dDialog THEN
       IF (dbScrollX IN flags) THEN subV:= propVmin END;
       IF (dbScrollY IN flags) THEN subH:= propHmin END
      END;
      group:= data
     END;
     WITH group^ DO
      osx:= sx; osy:= sy;
      IF (dbScrollX IN gadget^.flags) THEN
       sx:= FindScrollerTop(iw, gadget^.w - subH, hpi.horizPot)
      END;
      IF (dbScrollY IN gadget^.flags) THEN
       sy:= FindScrollerTop(ih, gadget^.h - subV, vpi.vertPot)
      END;
      IF (sx = osx) AND (sy = osy) THEN RETURN TRUE END;
      scroll:= TRUE
     END;
     rp:= window^.rPort;
     SetAPen(rp, 0);
     WITH gadget^ DO
      RectFill(rp, x, y, x + w - subH - 1, y + h - subV - 1);
      SetGadgetRect(gadget, x, y, w, h)
     END;
     IGNORE RefreshGList(window^.firstGadget, window, NIL, -1);
     IGNORE DrawGadget(gadget);
     RETURN TRUE
    END
   END;
   gadget:= idcmpWindow^.userData;
   IF gadget <> NIL THEN
    IF gadget^.type = dDialog THEN
     IF class = IDCMPFlagSet{activeWindow} THEN
      RETURN dbDownEvent IN gadget^.flags
     ELSIF class = IDCMPFlagSet{inactiveWindow} THEN
      RETURN dbUpEvent IN gadget^.flags
     ELSIF class = IDCMPFlagSet{newSize} THEN
      gadget:= idcmpWindow^.userData;
      IF gadget <> NIL THEN
       d:= gadget^.data;
       LimitWindow(gadget, d^.window)
      END
     END
    END
   END
  END;
  RETURN FALSE
 END CheckGadget;

 PROCEDURE ModalDialog(dialog: GadgetPtr;
           VAR gadget: GadgetPtr; VAR bNum: CARD8; VAR up: BOOLEAN);
  VAR
   dlg: DialogPtr;
   event: Event;
 BEGIN
  WHILE dialog^.type <> dDialog DO
   dialog:= dialog^.parent
  END;
  ShowGadget(dialog);
  dlg:= dialog^.data;
  IF dlg^.window = NIL THEN
   event.type:= eGADGET;
   event.gadget:= dialog;
   event.bNum:= SysEvent
  END;
  AddEvents(EventTypeSet{eGADGET});
  LOOP
   WaitEvent;
   GetEvent(event);
   IF event.type = eGADGET THEN
    gadget:= event.gadget;
    bNum:= event.bNum;
    up:= event.gadgetUp
   END;
   IF GetDialog(gadget) = dlg THEN EXIT END;
   DisplayBeep(dlg^.screen)
  END
 END ModalDialog;

 PROCEDURE GetScreenMode(init: BITSET; VAR depth: INTEGER): BITSET;
  VAR
   tags: ARRAY[0..6] OF LONGCARD;
   event: Event;
   what: TagItem;
   res: BITSET;
   dialog, group, hiRes, interlace, ham, dodit, lbl, dep, grp2, ok: GadgetPtr;
   flag: BOOLEAN;
 BEGIN
  res:= init;
  dialog:= CreateGadget(dDialog, TAG(tags, dTEXT, ADR("Screen mode:"), dFLAGS, dfVDIR, 0));
  group:= AddNewGadget(dialog, dGroup, TAG(tags, dFLAGS, dfVDIR + dfBORDER, 0));
  hiRes:= AddNewGadget(group, dCheckbox, TAG(tags, dTEXT, ADR("High Res"), dFLAGS, dfAUTOLEFT + dfSELECT * ORD(asmHiRes IN init), 0));
  interlace:= AddNewGadget(group, dCheckbox, TAG(tags, dTEXT, ADR("Interlace"), dFLAGS, dfAUTOLEFT + dfSELECT * ORD(asmInterlace IN init), 0));
  ham:= AddNewGadget(group, dCheckbox, TAG(tags, dTEXT, ADR("HAM"), dFLAGS, dfAUTOLEFT + dfSELECT * ORD(asmHam IN init) + dfACTIVE * ORD(NOT(asmHiRes IN init)), dMASK, dfSELECT + dfACTIVE + dfJUSTIFY, 0));
  dodit:= AddNewGadget(group, dCheckbox, TAG(tags, dTEXT, ADR("Dithering"), dFLAGS, dfAUTOLEFT + dfSELECT * ORD(dither), 0));
  grp2:= AddNewGadget(group, dGroup, TAG(tags, dRFLAGS, dfJUSTIFY, 0));
  lbl:= AddNewGadget(grp2, dLabel, TAG(tags, dTEXT, ADR("Depth: "), 0));
  dep:= AddNewGadget(grp2, dIntEdit, TAG(tags, dINTVAL, depth, 0));
  ok:= AddNewGadget(dialog, dButton, TAG(tags, dTEXT, ADR("Ok"), 0));
  IF RefreshGadget(dialog) = DialogOk THEN
   AddEvents(EventTypeSet{eGADGET});
   LOOP
    WaitEvent;
    GetEvent(event);
    IF event.type = eGADGET THEN
     what.tag:= dINTVAL; GetGadgetAttr(dep, what); depth:= what.data MOD 256;
     IF event.gadget = ok THEN EXIT END;
     what.tag:= dFLAGS; GetGadgetAttr(hiRes, what); flag:= (dbSelect IN what.bset);
     IF depth < 3 THEN depth:= 3 END;
     IF depth > 5 THEN depth:= 5 END;
     IF flag AND (depth > 4) THEN depth:= 4 END;
     ModifyGadget(ham, TAG(tags, dFLAGS, (dfACTIVE + dfSELECT) * ORD(NOT flag), dMASK, dfACTIVE + dfSELECT * ORD(flag), 0));
     what.tag:= dFLAGS; GetGadgetAttr(ham, what); flag:= (dbSelect IN what.bset);
     IF flag THEN depth:= 6 END;
     ModifyGadget(dep, TAG(tags, dFLAGS, dfACTIVE * ORD(NOT flag), dMASK, dfACTIVE, dINTVAL, depth, 0));
     ModifyGadget(dodit, TAG(tags, dFLAGS, (dfACTIVE + dfSELECT) * ORD(NOT flag), dMASK, dfACTIVE + dfSELECT * ORD(flag), 0))
    END
   END;
   what.tag:= dFLAGS; GetGadgetAttr(hiRes, what);
   IF (dbSelect IN what.bset) THEN INCL(res, asmHiRes) ELSE EXCL(res, asmHiRes) END;
   what.tag:= dFLAGS; GetGadgetAttr(interlace, what);
   IF (dbSelect IN what.bset) THEN INCL(res, asmInterlace) ELSE EXCL(res, asmInterlace) END;
   what.tag:= dFLAGS; GetGadgetAttr(ham, what);
   IF (dbSelect IN what.bset) THEN INCL(res, asmHam) ELSE EXCL(res, asmHam) END;
   what.tag:= dFLAGS; GetGadgetAttr(dodit, what);
   dither:= (dbSelect IN what.bset)
  END;
  DeepFreeGadget(dialog);
  RETURN res
 END GetScreenMode;


BEGIN

 WindowControl:= RefreshWindow;
 GadgetControl:= CheckGadget;
 AskScreenMode:= GetScreenMode

END Dialogs.
