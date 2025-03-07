IMPLEMENTATION MODULE Dialogs;

 FROM SYSTEM IMPORT ADR, ADDRESS, TAG, LONGSET, SHORTSET, SHIFT;
 FROM IntuitionD IMPORT GadgetFlags, GadgetFlagSet, ActivationFlags,
  ActivationFlagSet, boolGadget, propGadget, strGadget, Gadget, GadgetPtr,
  BoolInfo, BoolInfoPtr, PropInfoFlags, PropInfoFlagSet, PropInfo,
  PropInfoPtr, StringInfo, StringInfoPtr, autoFrontPen, autoBackPen,
  autoDrawMode, autoLeftEdge, autoTopEdge, IntuiText, IntuiTextPtr, Border,
  BorderPtr, Image, ImagePtr, IDCMPFlags, IDCMPFlagSet, IntuiMessage,
  IntuiMessagePtr, WindowFlags, WindowFlagSet, customScreen, Window,
  WindowPtr, Screen, ScreenPtr, NewWindow, ExtNewWindow, gadgHNone, WaTags,
  ScreenFlags, ScreenFlagSet, knobVmin, knobHmin;
 FROM IntuitionL IMPORT ActivateGadget, AddGList, BeginRefresh,
  CloseWindow, EndRefresh, IntuiTextLength, OffGadget, OnGadget, OpenWindow,
  PrintIText, RefreshGadgets, RefreshGList, RemoveGList,
  ModifyIDCMP, ClearMenuStrip, SetWindowTitles, intuitionVersion,
  LockPubScreen, UnlockPubScreen;
 FROM ExecD IMPORT MemReqs, MemReqSet;
 FROM ExecL IMPORT AllocMem, FreeMem, GetMsg, ReplyMsg, Forbid, Permit;
 FROM GraphicsD IMPORT TextAttr, TextAttrPtr, jam1, jam2, RastPortPtr,
  FontFlags, FontFlagSet, FontStyles, FontStyleSet;
 FROM GraphicsL IMPORT SetAPen, RectFill, GetRGB4, WaitBlit;
 FROM Memory IMPORT SET16, CARD16, INT16, CARD32, INT32, TagItem, TagItemPtr,
  NextTag, TAG3, tagMore;
 FROM AmigaBase IMPORT LinkWindow, UnlinkWindow, mainWindow, memLow;
 IMPORT R, IntuitionL;

 TYPE
  PIPtr = PropInfoPtr;
  SIPtr = StringInfoPtr;


 CONST
  bVDir = 0;
  bAtEnd = 1;
  bScroll = 2;
  bActive = 7;
  bSelect = 8;
  bBorder = 2;
  bHitEvent = 9;
  bRelEvent = 10;
  bAutoLeft = 3;
  bAutoRight = 4;
  bAutoUp = 5;
  bAutoDown = 6;

 TYPE
  DialogPtr = POINTER TO Dialog;
  ItemPtr = POINTER TO Item;
  Group = RECORD
   parentGroup: ItemPtr;
   firstItem: ItemPtr;
   nbItem: CARD16;
   vert, fromend: BOOLEAN;
  END;
  Item = RECORD
   nextItem: ItemPtr;
   id: CARD16;
   type: INT16; (* -1 = group *)
   width, height: CARD16; (* user desired  (used as minimum) *)
   gapSize: CARD16;
   mark: ADDRESS;
   gad: Gadget;
   group: Group;
   fill, span: CARD16;
   flags: SET16;
  END;
  Dialog = RECORD
   name: ADDRESS;
   w: WindowPtr;
   group: Item;
   currentGroup: ItemPtr;
   firstGadget: GadgetPtr;
   nbGadget: CARD16;
   fillPen: CARDINAL;
   success: INT16;
   closeBox, active: BOOLEAN;
  END;

 CONST
  topazAttr = TextAttr{
   name: ADR("topaz.font"),
   ySize: 8,
   style: FontStyleSet{},
   flags: FontFlagSet{romFont}};

 VAR
  s: ScreenPtr;
  ta: TextAttrPtr;

 PROCEDURE AllocDialog(tags: TagItemPtr): DialogPtr;
  VAR
   dialog{R.D7}: DialogPtr;
 BEGIN
  dialog:= AllocMem(SIZE(Dialog), MemReqSet{memClear});
  IF dialog <> NIL THEN
   WITH dialog^ DO
    closeBox:= TRUE; active:= TRUE;
    IF mainWindow <> NIL THEN
     s:= mainWindow^.wScreen
    ELSIF intuitionVersion >= 36 THEN
     s:= LockPubScreen(NIL)
    ELSE
     s:= NIL
    END;
    IF s <> NIL THEN ta:= s^.font ELSE ta:= ADR(topazAttr) END;
    success:= DialogOk;
    group.type:= -1;
    currentGroup:= ADR(group);
    LOOP
     WITH NextTag(tags)^ DO
      IF tag = 0 THEN EXIT
      ELSIF tag = dNAME THEN name:= data
      ELSIF tag = dACTIVE THEN active:= (data <> 0)
      ELSIF tag = dCLOSE THEN closeBox:= (data <> 0)
      END
     END
    END
   END
  ELSE
   memLow:= TRUE
  END;
  RETURN dialog
 END AllocDialog;

 PROCEDURE ModifyDialog(d: DialogPtr; tags: TagItemPtr);
 BEGIN
  WITH d^ DO
   LOOP
    WITH NextTag(tags)^ DO
     IF tag = 0 THEN EXIT
     ELSIF tag = dNAME THEN
      name:= data;
      IF w <> NIL THEN
       SetWindowTitles(w, name, NIL)
      END
     END
    END
   END
  END
 END ModifyDialog;

  (*$ OverflowChk:= FALSE *)
 CONST
  MAXBODY = 0FFFFH;
  MAXPOT = 0FFFFH;

 PROCEDURE FindScrollerValues(total, displayable, top, overlap: CARDINAL;
                              VAR body, pot: CARDINAL);
  VAR
   hidden: CARDINAL;
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
   hidden: CARDINAL;
 BEGIN
  IF displayable >= total THEN hidden:= 0 ELSE hidden:= total - displayable END;
  RETURN (LONGCARD(hidden * pot) + MAXPOT DIV 2) DIV 65536
 END FindScrollerTop;

 PROCEDURE FindSliderValues(numlevels, level: CARDINAL;
                            VAR body, pot: CARDINAL);
 BEGIN
  IF numlevels > 0 THEN
   body:= MAXBODY DIV numlevels
  ELSE
   body:= MAXBODY
  END;
  IF numlevels > 1 THEN
   pot:= LONGCARD(level * MAXPOT) DIV (numlevels - 1)
  ELSE
   pot:= 0
  END
 END FindSliderValues;

 PROCEDURE FindSliderLevel(numlevels, pot: CARDINAL): CARDINAL;
 BEGIN
  IF numlevels > 1 THEN
   RETURN (LONGCARD(pot * (numlevels - 1)) + MAXPOT DIV 2) DIV MAXPOT
  ELSE
   RETURN 0
  END
 END FindSliderLevel;
  (*$ POP OverflowChk *)

 PROCEDURE MakeItem(d: DialogPtr; item: ItemPtr; tags: TagItemPtr);
  CONST
   cTYPE = 0;
   cMASK = 1;
   cTEXT = 2;
   cGAP = 5;
   cHILITE = 6;
  VAR
   gText: POINTER TO ARRAY[0..255] OF CHAR;
   gTxtSize: CARD16;
   gIntVal, div: INT32;
   stringInfo: StringInfoPtr;
   propInfo: PropInfoPtr;
   border1{R.D7}, border2{R.D6}, border3{R.D5}, border4: BorderPtr;
   mask, flgs: SET16;
   color: LONGCARD;
   pen1, pen2, min, max, med, val, tmp, c, r, g, b, r0, b0, g0: INTEGER;
   changes: SHORTSET;
   zero: BOOLEAN;
 BEGIN
  gText:= NIL; gTxtSize:= item^.span; gIntVal:= 0;
  changes:= SHORTSET{}; mask:= SET16{}; flgs:= SET16{};
  pen1:= 1; pen2:= 2;
  IF s <> NIL THEN
   min:= MAX(INTEGER); max:= 0;
   med:= 0; d^.fillPen:= 7;
   color:= GetRGB4(s^.viewPort.colorMap, 0);
   b0:= color MOD 16; r0:= color DIV 256;
   g0:= (color DIV 16) MOD 16;
   FOR c:= 0 TO SHIFT(1, s^.rastPort.bitMap^.depth) - 1 DO
    color:= GetRGB4(s^.viewPort.colorMap, c);
    b:= color MOD 16; r:= color DIV 256;
    g:= (color DIV 16) MOD 16;
    val:= r + g + b;
    IF val >= max THEN max:= val; pen2:= c END;
    tmp:= ABS(r - r0) + ABS(g - g0) + ABS(b - b0);
    val:= (15 - val);
    val:= val + tmp - ABS(val - tmp);
    IF val >= min THEN min:= val; pen1:= c END;
    val:= ABS(r - g) + ABS(r - b) + ABS(g - b) + r DIV 4;
    IF val >= med THEN med:= val; d^.fillPen:= c END
   END
  END;
  WITH item^ DO
   LOOP
    WITH NextTag(tags)^ DO
     IF tag = 0 THEN EXIT
     ELSIF tag = gID THEN
      id:= data; gad.gadgetID:= id
     ELSIF tag = gFLAGS THEN flgs:= bset;
      group.vert:= (bVDir IN bset);
      group.fromend:= (bAtEnd IN bset)
     ELSIF tag = gMASK THEN
      mask:= bset; INCL(changes, cMASK)
     ELSIF tag = gMINWIDTH THEN
      width:= data
     ELSIF tag = gMINHEIGHT THEN
      height:= data
     ELSIF tag = gGAPSIZE THEN
      gapSize:= data; INCL(changes, cGAP)
     ELSIF tag = gFILL THEN
      IF data < 65536 THEN fill:= data ELSE fill:= 65535 END
     ELSIF tag = gSPAN THEN
      IF data < 65536 THEN span:= data ELSE span:= 65535 END
     ELSIF tag = gTYPE THEN
      INCL(changes, cTYPE);
      type:= data;
      IF (data = gBUTTON) OR (data = gCYCLE) OR (data = gCHOICE) THEN
       gad.gadgetType:= boolGadget; EXCL(gad.activation, toggleSelect)
      ELSIF (data = gBOOL) OR (data = gCHECK) OR (data = gRADIO) OR (data = gSWITCH) THEN
       gad.gadgetType:= boolGadget; INCL(gad.activation, toggleSelect)
      ELSIF (data = gSCROLLER) OR (data = gSLIDER) THEN
       gad.gadgetType:= propGadget
      ELSIF data = gSTRNG THEN
       gad.gadgetType:= strGadget; EXCL(gad.activation, longint)
      ELSIF data = gINT THEN
       gad.gadgetType:= strGadget; INCL(gad.activation, longint)
      END
     ELSIF tag = gTEXT THEN
      gText:= addr; INCL(changes, cTEXT)
     ELSIF tag = gTXTSIZE THEN
      gTxtSize:= data; span:= data
     ELSIF tag = gINTVAL THEN
      gIntVal:= lint
     ELSIF tag = gHILITE THEN
      INCL(changes, cHILITE);
      IF data = hNONE THEN
       INCL(gad.flags, gadgHBox); INCL(gad.flags, gadgHImage)
      ELSIF data = hINVBORD THEN
       EXCL(gad.flags, gadgHBox); INCL(gad.flags, gadgHImage)
      ELSIF data = hINVALL THEN
       EXCL(gad.flags, gadgHBox); EXCL(gad.flags, gadgHImage)
      END
     END
    END
   END;
   IF NOT(cMASK IN changes) THEN mask:= flgs END;
   flags:= (flags - mask) + flgs * mask;
   IF bActive IN mask THEN
    IF bActive IN flgs THEN
     EXCL(gad.flags, gadgDisabled)
    ELSE
     INCL(gad.flags, gadgDisabled)
    END
   END;
   IF bSelect IN mask THEN
    IF bSelect IN flgs THEN
     INCL(gad.flags, selected)
    ELSE
     EXCL(gad.flags, selected)
    END
   END;
   IF bHitEvent IN mask THEN
    IF bHitEvent IN flgs THEN
     IF (type = gSCROLLER) OR (type = gSLIDER) THEN
      INCL(gad.activation, followMouse)
     ELSE
      INCL(gad.activation, gadgImmediate)
     END
    ELSE
     IF (type = gSCROLLER) OR (type = gSLIDER) THEN
      EXCL(gad.activation, followMouse)
     ELSE
      EXCL(gad.activation, gadgImmediate)
     END
    END
   END;
   IF bRelEvent IN mask THEN
    IF bRelEvent IN flgs THEN
     INCL(gad.activation, relVerify)
    ELSE
     EXCL(gad.activation, relVerify)
    END
   END;
   IF cTYPE IN changes THEN
    IF NOT(cHILITE IN changes) THEN
     IF (type = gBUTTON) OR (type = gBOOL) THEN
      IF NOT((bBorder IN mask) AND NOT(bBorder IN flgs)) THEN
       EXCL(gad.flags, gadgHBox); EXCL(gad.flags, gadgHImage)
      END
     ELSIF (type = gCYCLE) OR (type = gCHOICE) OR
           (type = gRADIO) OR (type = gCHECK) OR (type = gSWITCH) THEN
      EXCL(gad.flags, gadgHBox); INCL(gad.flags, gadgHImage)
     END
    END;
    IF (type >= gSCROLLER) THEN
     EXCL(gad.flags, gadgHBox); EXCL(gad.flags, gadgHImage)
    END;
    IF NOT(bHitEvent IN mask) THEN
     IF (type = gSCROLLER) OR (type = gSLIDER) THEN
      EXCL(gad.activation, followMouse)
     ELSE
      EXCL(gad.activation, gadgImmediate)
     END
    END;
    IF NOT(bBorder IN mask) AND (type < gSCROLLER) AND (type >= 0) THEN
     INCL(mask, bBorder); INCL(flgs, bBorder)
    END;
    IF NOT(bRelEvent IN mask) THEN
     IF (gadgHBox IN gad.flags) AND (type < gSTRNG) THEN
      EXCL(gad.activation, relVerify)
     ELSE
      INCL(gad.activation, relVerify)
     END
    END
   END;
   IF (type = gSCROLLER) OR (type = gSLIDER) THEN
    EXCL(mask, bBorder); (* No choice for them *)
    IF gad.gadgetRender = NIL THEN
     gad.gadgetRender:= AllocMem(SIZE(Image), MemReqSet{memClear});
     IF gad.gadgetRender = NIL THEN d^.success:= NoMem END
    END;
    IF gad.specialInfo = NIL THEN
     propInfo:= AllocMem(SIZE(PropInfo), MemReqSet{memClear})
    ELSE
     propInfo:= gad.specialInfo
    END;
    gad.specialInfo:= propInfo;
    IF propInfo <> NIL THEN
     WITH propInfo^ DO
      flags:= PropInfoFlagSet{autoKnob, propNewLook};
      IF type = gSLIDER THEN INCL(flags, propBorderless) END;
      IF (bVDir IN item^.flags) THEN
       INCL(flags, freeVert);
       IF type = gSLIDER THEN
        vertPot:= fill; vertBody:= 1
       ELSE
        FindScrollerValues(65535, span, fill, span DIV 16, vertBody, vertPot)
       END
      ELSE
       INCL(flags, freeHoriz);
       IF type = gSLIDER THEN
        horizPot:= fill; horizBody:= 1
       ELSE
        FindScrollerValues(65535, span, fill, span DIV 16, horizBody, horizPot)
       END
      END
     END
    ELSE
     d^.success:= NoMem
    END
   END;
   IF cTEXT IN changes THEN
    IF type < gSCROLLER THEN
     IF (gText <> NIL) THEN
      IF gad.gadgetText = NIL THEN
       gad.gadgetText:= AllocMem(SIZE(IntuiText), MemReqSet{memClear})
      END;
      IF gad.gadgetText <> NIL THEN
       WITH gad.gadgetText^ DO
        frontPen:= pen1; drawMode:= jam1;
        iText:= gText
       END
      ELSE
       d^.success:= NoMem
      END
     ELSIF (gText = NIL) AND (gad.gadgetText <> NIL) THEN
      FreeMem(gad.gadgetText, SIZE(IntuiText));
      gad.gadgetText:= NIL
     END
    ELSIF type >= gSTRNG THEN
     IF (gText <> NIL) THEN
      IF gad.specialInfo = NIL THEN
       stringInfo:= AllocMem(SIZE(StringInfo), MemReqSet{memClear})
      ELSE
       stringInfo:= gad.specialInfo
      END;
      IF stringInfo <> NIL THEN
       WITH stringInfo^ DO
        buffer:= gText; longInt:= gIntVal;
        IF type = gINT THEN
         div:= 1000000000; zero:= TRUE; c:= 0;
         IF gIntVal < 0 THEN
          gText^[0]:= "-"; c:= 1;
          gIntVal:= -gIntVal
         END;
         REPEAT
          val:= gIntVal DIV div;
          gIntVal:= gIntVal MOD div;
          div:= div DIV 10;
          IF (val <> 0) OR (div = 0) THEN zero:= FALSE END;
          IF NOT(zero) THEN
           gText^[c]:= CHR(48 + val);
           IF c < INTEGER(gTxtSize) - 1 THEN INC(c) END
          END;
          gText^[c]:= 0C
         UNTIL div = 0
        END;
        IF gTxtSize <> 0 THEN maxChars:= gTxtSize END
       END;
       gad.specialInfo:= stringInfo
      ELSE
       d^.success:= NoMem
      END
     END
    END
   END;
   IF bBorder IN mask THEN
    IF (bBorder IN flgs) AND (gad.gadgetRender = NIL) THEN
     border1:= AllocMem(SIZE(Border) * 4 + 24, MemReqSet{memClear});
     IF border1 <> NIL THEN
      border2:= border1; INC(border2, SIZE(Border));
      border3:= border2; INC(border3, SIZE(Border));
      border4:= border3; INC(border4, SIZE(Border));
      IF (type < 0) THEN
       border1^.frontPen:= pen1; border2^.frontPen:= pen2
      ELSE
       border1^.frontPen:= pen2; border2^.frontPen:= pen1
      END;
      border3^.frontPen:= pen1; border4^.frontPen:= pen2;
      border1^.drawMode:= jam1; border2^.drawMode:= jam1;
      border3^.drawMode:= jam1; border4^.drawMode:= jam1;
      border1^.count:= 3; border2^.count:= 3;
      border3^.count:= 3; border4^.count:= 3;
      border1^.xy:= border4; INC(border1^.xy, SIZE(Border));
      border2^.xy:= border1^.xy; INC(border2^.xy, 12);
      border3^.xy:= border1^.xy; border4^.xy:= border2^.xy;
      border1^.nextBorder:= border2;
      border3^.nextBorder:= border4;
      gad.gadgetRender:= border1;
      gad.selectRender:= border3
     ELSE
      d^.success:= NoMem
     END;
     IF (type = gCHECK) OR (type = gRADIO) OR (type = gSWITCH) THEN
      border4^.frontPen:= pen1; border3^.frontPen:= pen2;
      mark:= AllocMem(SIZE(Border) * 2 + 24, MemReqSet{memClear});
      IF mark <> NIL THEN
       border1:= mark; border2:= mark; INC(border2, SIZE(Border));
       border1^.xy:= border2; INC(border1^.xy, SIZE(Border));
       border2^.xy:= border1^.xy; INC(border2^.xy, 12);
       border1^.frontPen:= pen2; border2^.frontPen:= pen2;
       border1^.drawMode:= jam1; border2^.drawMode:= jam1;
       IF (type = gCHECK) THEN
        border1^.count:= 2; border2^.count:= 2
       ELSE
        IF type = gRADIO THEN
         border1^.frontPen:= pen1
        ELSE
         border2^.frontPen:= pen1
        END;
        border1^.count:= 3; border2^.count:= 3
       END;
       border1^.nextBorder:= border2;
       IF type = gSWITCH THEN (* New shape when selected *)
        gad.selectRender:= border1
       ELSE (* additional shape when selected *)
        border4^.nextBorder:= border1
       END
      ELSE
       d^.success:= NoMem
      END
     END
    ELSIF NOT(bBorder IN flgs) AND (gad.gadgetRender <> NIL) THEN
     FreeMem(gad.gadgetRender, SIZE(Border) * 4 + 24);
     gad.gadgetRender:= NIL;
     gad.selectRender:= NIL
    END
   END;
   IF NOT(cGAP IN changes) THEN
    IF (type > 0) OR (gad.gadgetRender <> NIL) THEN
     gapSize:= 1
    ELSE
     gapSize:= 0
    END
   END
  END
 END MakeItem;

 PROCEDURE UnlinkGadget(d: DialogPtr; VAR gad: Gadget);
  VAR
   prevGadgetPtr{R.A3}: POINTER TO GadgetPtr;
 BEGIN
  prevGadgetPtr:= ADR(d^.firstGadget);
  WHILE prevGadgetPtr^ <> ADR(gad) DO
   prevGadgetPtr:= ADR(prevGadgetPtr^^.nextGadget)
  END;
  prevGadgetPtr^:= gad.nextGadget;
  DEC(d^.nbGadget);
  IF d^.nbGadget = 0 THEN d^.firstGadget:= NIL END
 END UnlinkGadget;

 PROCEDURE LinkGadget(d: DialogPtr; VAR gad: Gadget);
 BEGIN
  gad.nextGadget:= d^.firstGadget;
  d^.firstGadget:= ADR(gad);
  INC(d^.nbGadget)
 END LinkGadget;

 PROCEDURE LinkItem(d: DialogPtr; newItem: ItemPtr);
  VAR
   nextItemPtr{R.A3}: POINTER TO ItemPtr;
 BEGIN
  nextItemPtr:= ADR(d^.currentGroup^.group.firstItem);
  WHILE nextItemPtr^ <> NIL DO
   nextItemPtr:= ADR(nextItemPtr^^.nextItem)
  END;
  nextItemPtr^:= newItem;
  newItem^.nextItem:= NIL;
  INC(d^.currentGroup^.group.nbItem);
  newItem^.group.parentGroup:= d^.currentGroup;
 END LinkItem;

 PROCEDURE BeginGroup(d: DialogPtr; tags: TagItemPtr);
  VAR
   newItem{R.D7}: ItemPtr;
 BEGIN
  newItem:= AllocMem(SIZE(Item), MemReqSet{memClear});
  IF newItem <> NIL THEN
   LinkItem(d, newItem);
   d^.currentGroup:= newItem;
   WITH newItem^ DO
    flags:= SET16{bAutoLeft..bAutoDown};
    gad.flags:= gadgHNone;
    gad.gadgetType:= boolGadget;
    gad.userData:= newItem;
    type:= -1;
    MakeItem(d, newItem, tags);
    LinkGadget(d, gad);
   END
  ELSE
   d^.success:= NoMem
  END
 END BeginGroup;

 PROCEDURE AddGadget(d: DialogPtr; id, type: CARD16; tags: TagItemPtr);
  VAR
   newItem{R.D7}: ItemPtr;
 BEGIN
  newItem:= AllocMem(SIZE(Item), MemReqSet{memClear});
  IF newItem <> NIL THEN
   LinkItem(d, newItem);
   WITH newItem^ DO
    flags:= SET16{bAutoLeft..bAutoDown};
    width:= 1; height:= 1;
    gad.flags:= gadgHNone;
    gad.gadgetType:= boolGadget;
    gad.userData:= newItem;
   END;
   MakeItem(d, newItem, TAG3(gID, id, gTYPE, type, tagMore, ADDRESS(tags)));
   LinkGadget(d, newItem^.gad)
  ELSE
   d^.success:= NoMem
  END
 END AddGadget;

 PROCEDURE EndGroup(d: DialogPtr);
 BEGIN
  WITH d^ DO
   IF currentGroup^.group.parentGroup = NIL THEN
    success:= -3
   ELSE
    currentGroup:= currentGroup^.group.parentGroup
   END
  END
 END EndGroup;

(*$ StackChk:= TRUE *)
 PROCEDURE FindItem(firstItem: ItemPtr; id: CARD16): ItemPtr;
  VAR
   item{R.D7}: ItemPtr;
 BEGIN
  LOOP
   IF firstItem = NIL THEN EXIT END;
   IF firstItem^.id = id THEN RETURN firstItem END;
   IF firstItem^.type = -1 THEN
    item:= FindItem(firstItem^.group.firstItem, id);
    IF item <> NIL THEN RETURN item END
   END;
   firstItem:= firstItem^.nextItem
  END;
  RETURN NIL
 END FindItem;

 PROCEDURE FreeItem(d: DialogPtr; grp, item: ItemPtr);
  VAR
   prevItemPtr{R.D7}: POINTER TO ItemPtr;
 BEGIN
  IF item <> NIL THEN
   WITH item^ DO
    IF type = -1 THEN
     WHILE group.firstItem <> NIL DO
      FreeItem(d, item, group.firstItem)
     END
    END;
    IF gad.specialInfo <> NIL THEN
     IF (type = gSCROLLER) OR (type = gSLIDER) THEN
      FreeMem(gad.specialInfo, SIZE(PropInfo))
     ELSE
      FreeMem(gad.specialInfo, SIZE(StringInfo))
     END;
     gad.specialInfo:= NIL
    END;
    IF gad.gadgetText <> NIL THEN
     FreeMem(gad.gadgetText, SIZE(IntuiText));
     gad.gadgetText:= NIL
    END;
    IF mark <> NIL THEN
     FreeMem(mark, SIZE(Border) * 2 + 24);
     mark:= NIL
    END;
    IF gad.gadgetRender <> NIL THEN
     IF (type = gSCROLLER) OR (type = gSLIDER) THEN
      FreeMem(gad.gadgetRender, SIZE(Image))
     ELSE
      FreeMem(gad.gadgetRender, SIZE(Border) * 4 + 24)
     END;
     gad.gadgetRender:= NIL;
     gad.selectRender:= NIL
    END;
   (* Unlink Gadget *)
    UnlinkGadget(d, gad);
   (* Unlink Item *)
    prevItemPtr:= ADR(grp^.group.firstItem);
    WHILE prevItemPtr^ <> item DO
     prevItemPtr:= ADR(prevItemPtr^^.nextItem)
    END;
    prevItemPtr^:= nextItem
   END;
   FreeMem(item, SIZE(Item))
  END
 END FreeItem;
(*$ POP StackChk *)

 PROCEDURE ModifyGadget(d: DialogPtr; id: CARD16; tags: TagItemPtr);
  VAR
   item{R.D7}: ItemPtr;
   gadPos{R.D6}: INT16;
   rp{R.A3}: RastPortPtr;
   wide{R.D4}, p: CARD16;
 BEGIN
  item:= FindItem(d^.group.group.firstItem, id);
  IF item <> NIL THEN
   IF d^.w <> NIL THEN
    gadPos:= IntuitionL.RemoveGadget(d^.w, ADR(item^.gad))
   END;
   MakeItem(d, item, tags);
   IF d^.w <> NIL THEN
    rp:= d^.w^.rPort;
    SetAPen(rp, 0);
    WITH item^.gad DO
    (*$ OverflowChk:= FALSE *)
     wide:= CARD32(item^.fill * CARD16(width - 1)) DIV 65536;
    (*$ POP OverflowChk *)
     RectFill(rp, leftEdge, topEdge, leftEdge + width - 1, topEdge + height - 1);
     IF (item^.type = gBOOL) AND (wide <> 0) THEN
      SetAPen(rp, d^.fillPen);
      RectFill(rp, leftEdge + 1, topEdge + 1, leftEdge + INT16(wide), topEdge + height - 2)
     ELSIF item^.type = gSWITCH THEN
      wide:= item^.fill;
      WHILE wide > 0 DO
       SetAPen(rp, gadgetText^.frontPen);
       p:= leftEdge + height * INT16(wide * 2 - 1) DIV 2 - 1;
       RectFill(rp, p, topEdge + 1, p, topEdge + height - 2);
       DEC(wide)
      END
     END
    END
   END;
   WITH item^ DO
    IF gad.gadgetText <> NIL THEN
     IF (type = gCHECK) OR (type = gRADIO) OR (type = gSWITCH) THEN
      gad.gadgetText^.leftEdge:= (gad.width - INT16(ta^.ySize * (fill + 1)) - 1
           - IntuiTextLength(gad.gadgetText)) DIV 2
           + INT16(ta^.ySize * (fill + 1)) + 1;
     ELSE
      gad.gadgetText^.leftEdge:= (gad.width - IntuiTextLength(gad.gadgetText)) DIV 2
     END;
     gad.gadgetText^.topEdge:= (gad.height - INT16(ta^.ySize)) DIV 2
    END
   END;
   IF d^.w <> NIL THEN
    IGNORE IntuitionL.AddGadget(d^.w, ADR(item^.gad), gadPos);
    RefreshGList(ADR(item^.gad), d^.w, NIL, 1); WaitBlit
   END
  END
 END ModifyGadget;

 PROCEDURE GetGadgetAttr(d: DialogPtr; id: CARD16; VAR what: TagItem);
  VAR
   item{R.D7}: ItemPtr;
   strInfo{R.D6}: StringInfoPtr;
   propInfo{R.D5}: PropInfoPtr;
 BEGIN
  item:= FindItem(d^.group.group.firstItem, id);
  IF item <> NIL THEN
   WITH item^ DO
    WITH what DO
     IF tag = gTYPE THEN addr:= type
     ELSIF tag = gTEXT THEN
      IF gad.gadgetText <> NIL THEN data:= gad.gadgetText^.iText
      ELSIF gad.specialInfo <> NIL THEN
       strInfo:= gad.specialInfo;
       data:= strInfo^.buffer
      ELSE data:= NIL
      END
     ELSIF tag = gINTVAL THEN
      strInfo:= gad.specialInfo;
      lint:= strInfo^.longInt
     ELSIF tag = gGAPSIZE THEN
      data:= gapSize
     ELSIF tag = gFLAGS THEN
      bset:= SET16{};
      IF NOT(gadgDisabled IN gad.flags) THEN INCL(bset, bActive) END;
      IF (selected IN gad.flags) THEN INCL(bset, bSelect) END;
      IF (gadgImmediate IN gad.activation) OR
         (followMouse IN gad.activation) THEN INCL(bset, bHitEvent) END;
      IF (relVerify IN gad.activation) THEN INCL(bset, bRelEvent) END;
      IF (gad.gadgetRender <> NIL) THEN INCL(bset, bBorder) END
     ELSIF tag = gFILL THEN
      data:= fill;
      IF (type = gSCROLLER) OR (type = gSLIDER) THEN
       propInfo:= gad.specialInfo;
       WITH propInfo^ DO
        IF type = gSLIDER THEN
         IF (bVDir IN item^.flags) THEN data:= vertPot ELSE data:= horizPot END
        ELSE
         IF (bVDir IN item^.flags) THEN
          data:= FindScrollerTop(65535, span, vertPot)
         ELSE
          data:= FindScrollerTop(65535, span, horizPot)
         END
        END
       END
      END
     ELSE
      tag:= 0; lint:= 0
     END
    END
   END
  END
 END GetGadgetAttr;

(*$ StackChk:= TRUE *)
 PROCEDURE GetSize(item: ItemPtr; VAR width, height: CARD16); FORWARD;

 PROCEDURE GetGadgetSize(item: ItemPtr; VAR w, h: CARD16);
 BEGIN
  w:= 0; h:= 0;
  WITH item^ DO
   IF gad.gadgetText <> NIL THEN
    gad.gadgetText^.iTextFont:= ta;
    gad.gadgetText^.leftEdge:= 0; gad.gadgetText^.topEdge:= 0;
    w:= IntuiTextLength(gad.gadgetText);
    h:= ta^.ySize;
    IF (type = gCHECK) OR (type = gRADIO) OR (type = gSWITCH) THEN
     INC(w, h * (fill + 1) + 1)
    END
   END;
   IF gad.specialInfo <> NIL THEN
    IF (type = gSCROLLER) OR (type = gSLIDER) THEN
     INC(w, knobVmin); INC(h, knobHmin)
    ELSE
     INC(w, ta^.ySize * 4);
     INC(h, ta^.ySize)
    END
   END;
   IF gad.gadgetRender <> NIL THEN (* border *)
    IF (type <> gSCROLLER) AND (type <> gSLIDER) THEN
     INC(w, autoLeftEdge);
     INC(h, autoTopEdge * 2)
    END
   END
  END
 END GetGadgetSize;

 PROCEDURE GetGroupSize(item: ItemPtr; VAR w, h: CARD16);
  VAR
   currentItem{R.D7}: ItemPtr;
   iw, ih: CARD16;
 BEGIN
  w:= 0; h:= 0;
  currentItem:= item^.group.firstItem;
  WHILE currentItem <> NIL DO
   GetSize(currentItem, iw, ih);
   IF item^.group.vert THEN
    INC(h, ih);
    IF iw > w THEN w:= iw END
   ELSE
    INC(w, iw);
    IF ih > h THEN h:= ih END
   END;
   currentItem:= currentItem^.nextItem
  END;
  IF item^.gad.gadgetRender <> NIL THEN (* border *)
   INC(w, autoLeftEdge);
   INC(h, autoTopEdge * 2)
  END
 END GetGroupSize;

 PROCEDURE GetSize(item: ItemPtr; VAR width, height: CARD16);
  VAR
   iText: IntuiText;
   minWidth, minHeight: CARD16;
  (*$ EntryClear:= TRUE *)
 BEGIN
  iText.iTextFont:= ta;
  iText.iText:= ADR("W");
  minWidth:= item^.width; minHeight:= item^.height;
  minWidth:= minWidth * CARD16(IntuiTextLength(ADR(iText)));
  minHeight:= minHeight * ta^.ySize;
  IF item^.type = -1 THEN
   GetGroupSize(item, width, height)
  ELSE
   GetGadgetSize(item, width, height)
  END;
  IF minWidth > width THEN width:= minWidth END;
  IF minHeight > height THEN height:= minHeight END;
  INC(width, item^.gapSize * 2); INC(height, item^.gapSize * 2);
  item^.gad.width:= width; item^.gad.height:= height
 END GetSize;

 PROCEDURE SetSize(item: ItemPtr; width, height: CARD16); FORWARD;

 PROCEDURE SetGadgetSize(item: ItemPtr; w, h: CARD16);
  VAR
   coords{R.A3}: POINTER TO ARRAY[0..5] OF RECORD x, y: CARD16 END;
   c{R.D7}: INTEGER;
 BEGIN
  WITH item^ DO
   IF NOT((bAutoLeft IN flags) AND (bAutoRight IN flags)) THEN w:= gad.width END;
   IF NOT((bAutoUp IN flags) AND (bAutoDown IN flags)) THEN h:= gad.height END;
   IF gad.gadgetText <> NIL THEN (* Adjust intuiText *)
    IF (type = gCHECK) OR (type = gRADIO) OR (type = gSWITCH) THEN
     gad.gadgetText^.leftEdge:= h * (fill + 1)
    ELSE
     gad.gadgetText^.leftEdge:= (w - CARD16(gad.width)) DIV 2
    END;
    gad.gadgetText^.topEdge:= (h - CARD16(gad.height)) DIV 2
   END;
   IF (gad.gadgetRender <> NIL) AND
      (type <> gSCROLLER) AND (type <> gSLIDER) THEN (* Adjust border *)
    coords:= gad.gadgetRender;
    INC(coords, SIZE(Border) * 4);
    IF (type = gCHECK) OR (type = gRADIO) OR (type = gSWITCH) THEN
     IF type = gSWITCH THEN
      coords^[0].x:= 1; coords^[0].y:= h * 2 DIV 3 - 1;
      coords^[1].x:= 1; coords^[1].y:= h DIV 3 - 1;
      coords^[2].x:= h - 2; coords^[2].y:= h DIV 2 - 1;
      coords^[3].x:= h - 2; coords^[3].y:= h DIV 2 - 1;
      coords^[4].x:= 2; coords^[4].y:= h * 2 DIV 3 - 1;
      coords^[5].x:= 2; coords^[5].y:= h DIV 3;
      FOR c:= 0 TO 5 DO INC(coords^[c].x, h * fill) END;
      coords:= mark; INC(coords, SIZE(Border) * 2);
      coords^[0].x:= h DIV 2 - 1; coords^[0].y:= h - 2;
      coords^[1].x:= h DIV 3 - 1; coords^[1].y:= 1;
      coords^[2].x:= h * 2 DIV 3 - 1; coords^[2].y:= 1;
      coords^[3].x:= h DIV 3; coords^[3].y:= 2;
      coords^[4].x:= h * 2 DIV 3 - 1; coords^[4].y:= 2;
      coords^[5].x:= h DIV 2 - 1; coords^[5].y:= h - 2
     ELSE
      coords^[0].x:= 1; coords^[0].y:= h - 2;
      coords^[1].x:= 1; coords^[1].y:= 1;
      coords^[2].x:= h - 3; coords^[2].y:= 1;
      coords^[3].x:= h - 2; coords^[3].y:= 1;
      coords^[4].x:= h - 2; coords^[4].y:= h - 2;
      coords^[5].x:= 2; coords^[5].y:= h - 2;
      FOR c:= 0 TO 5 DO INC(coords^[c].x, h * fill) END;
      coords:= mark; INC(coords, SIZE(Border) * 2);
      IF type = gCHECK THEN
       coords^[0].x:= 3; coords^[0].y:= 3;
       coords^[1].x:= h - 4; coords^[1].y:= h - 4;
       coords^[3].x:= 3; coords^[3].y:= h - 4;
       coords^[4].x:= h - 4; coords^[4].y:= 3
      ELSE
       coords^[0].x:= 4; coords^[0].y:= h - 5;
       coords^[1].x:= 4; coords^[1].y:= 4;
       coords^[2].x:= h - 6; coords^[2].y:= 4;
       coords^[3].x:= h - 5; coords^[3].y:= 4;
       coords^[4].x:= h - 5; coords^[4].y:= h - 5;
       coords^[5].x:= 5; coords^[5].y:= h - 5
      END
     END;
     FOR c:= 0 TO 5 DO INC(coords^[c].x, h * fill) END
    ELSE
     coords^[0].x:= 0; coords^[0].y:= h - 1;
     coords^[1].x:= 0; coords^[1].y:= 0;
     coords^[2].x:= w - 2; coords^[2].y:= 0;
     coords^[3].x:= w - 1; coords^[3].y:= 0;
     coords^[4].x:= w - 1; coords^[4].y:= h - 1;
     coords^[5].x:= 1; coords^[5].y:= h - 1
    END;
    IF gad.gadgetText <> NIL THEN
     INC(gad.gadgetText^.leftEdge, autoLeftEdge DIV 2);
     INC(gad.gadgetText^.topEdge, autoTopEdge)
    END
   END;
   (* Set size *)
   gad.width:= w;
   gad.height:= h
  END
 END SetGadgetSize;

 PROCEDURE SetGroupSize(item: ItemPtr; w, h: CARD16);
  VAR
   currentItem{R.D7}: ItemPtr;
   widthleft, heightleft, newwidth{R.D6}, newheight{R.D5}, px, py: CARD16;
   midX, midY, maxX, maxY: CARD16;
   count{R.D4}: CARD16;
 BEGIN
  WITH item^ DO
   IF NOT((bAutoLeft IN flags) AND (bAutoRight IN flags)) THEN w:= gad.width END;
   IF NOT((bAutoUp IN flags) AND (bAutoDown IN flags)) THEN h:= gad.height END;
   midX:= gad.leftEdge; midY:= gad.topEdge;
   maxX:= w + midX; maxY:= h + midY;
   IF gad.gadgetRender <> NIL THEN
    INC(midX, autoLeftEdge DIV 2); DEC(maxX, autoLeftEdge DIV 2);
    INC(midY, autoTopEdge); DEC(maxY, autoTopEdge)
   END;
   count:= group.nbItem;
   widthleft:= (w - CARD16(gad.width));
   heightleft:= (h - CARD16(gad.height));
   currentItem:= group.firstItem;
   WHILE currentItem <> NIL DO
   (* compute dimension *)
    newwidth:= widthleft DIV count; DEC(widthleft, newwidth);
    newheight:= heightleft DIV count; DEC(heightleft, newheight);
    INC(newwidth, currentItem^.gad.width);
    INC(newheight, currentItem^.gad.height);
    IF group.vert THEN newwidth:= maxX - midX ELSE newheight:= maxY - midY END;
   (* compute position *)
    IF currentItem^.group.fromend THEN
     px:= maxX - newwidth;
     py:= maxY - newheight;
     IF group.vert THEN
      DEC(maxY, newheight)
     ELSE
      DEC(maxX, newwidth)
     END
    ELSE
     px:= midX; py:= midY;
     IF group.vert THEN
      INC(midY, newheight)
     ELSE
      INC(midX, newwidth)
     END
    END;
   (* Affect values *)
    WITH currentItem^ DO
     gad.leftEdge:= px;
     gad.topEdge:= py;
     IF NOT((bAutoLeft IN flags) OR (bAutoRight IN flags)) THEN
      INC(gad.leftEdge, (INT16(newwidth) - gad.width) DIV 2)
     ELSIF (bAutoRight IN flags) AND NOT(bAutoLeft IN flags) THEN
      INC(gad.leftEdge, (INT16(newwidth) - gad.width))
     END;
     IF NOT((bAutoUp IN flags) OR (bAutoDown IN flags)) THEN
      INC(gad.topEdge, (INT16(newheight) - gad.height) DIV 2)
     ELSIF (bAutoDown IN flags) AND NOT(bAutoUp IN flags) THEN
      INC(gad.topEdge, (INT16(newheight) - gad.height))
     END
    END;
    SetSize(currentItem, newwidth, newheight);
    currentItem:= currentItem^.nextItem;
    DEC(count)
   END
  END;
  SetGadgetSize(item, w, h)
 END SetGroupSize;

 PROCEDURE SetSize(item: ItemPtr; width, height: CARD16);
 BEGIN
  WITH item^ DO
   DEC(gad.width, gapSize * 2); DEC(gad.height, gapSize * 2);
   INC(gad.leftEdge, gapSize); INC(gad.topEdge, gapSize)
  END;
  DEC(width, item^.gapSize * 2); DEC(height, item^.gapSize * 2);
  IF item^.type = -1 THEN
   SetGroupSize(item, width, height)
  ELSE
   SetGadgetSize(item, width, height)
  END
 END SetSize;
(*$ POP StackChk *)

 PROCEDURE DisplayDialog(d: DialogPtr; tags: TagItemPtr): INT16;
  VAR
   nw: ExtNewWindow;
   tagbuff: ARRAY[0..4] OF CARD32;
   innerWidth, innerHeight: CARD16;
   topaz{R.D7}: BOOLEAN;
  (*$ EntryClear:= TRUE *)
 BEGIN
  WITH d^ DO
   IF (success <> 0) THEN RETURN NoMem END;
   IF s <> NIL THEN
    group.gad.leftEdge:= s^.wBorLeft;
    group.gad.topEdge:= CARD16(s^.wBorTop) + CARD16(s^.font^.ySize) + 1
   ELSE
    group.gad.leftEdge:= 4;
    group.gad.topEdge:= 12
   END;
   topaz:= FALSE;
   LOOP
    GetSize(ADR(group), innerWidth, innerHeight);
    SetSize(ADR(group), innerWidth, innerHeight);
    IF s <> NIL THEN
     nw.nw.width:= innerWidth + CARD16(s^.wBorLeft) + CARD16(s^.wBorRight);
     nw.nw.height:= innerHeight + CARD16(s^.wBorTop) + CARD16(s^.font^.ySize) + 1 + CARD16(s^.wBorBottom);
     nw.nw.leftEdge:= (s^.width - nw.nw.width) DIV 2;
     nw.nw.topEdge:= (s^.height - nw.nw.height) DIV 2;
     nw.nw.type:= customScreen
    ELSE
     nw.nw.width:= innerWidth + 8;
     nw.nw.height:= innerHeight + 16;
     nw.nw.type:= ScreenFlagSet{}
    END;
    IF (nw.nw.leftEdge >= 0) AND (nw.nw.topEdge >= 0) THEN EXIT END;
    IF topaz THEN RETURN TooBig ELSE ta:= ADR(topazAttr); topaz:= TRUE END
   END;
   nw.nw.detailPen:= -1; nw.nw.blockPen:= -1;
   nw.nw.idcmpFlags:= IDCMPFlagSet{};
   nw.nw.flags:= WindowFlagSet{windowDrag, windowDepth, simpleRefresh, nwExtended};
   IF closeBox THEN INCL(nw.nw.flags, windowClose) END;
   IF active THEN INCL(nw.nw.flags, activate) END;
   nw.nw.firstGadget:= firstGadget;
   nw.nw.title:= name;
   nw.nw.screen:= s;
   nw.nw.maxWidth:= -1; nw.nw.maxHeight:= -1;
   nw.extension:= TAG(tagbuff, waInnerWidth, innerWidth, waInnerHeight, innerHeight, 0);
   IF s <> NIL THEN
    IF mainWindow = NIL THEN UnlockPubScreen(NIL, s) END
   END;
   w:= OpenWindow(nw.nw);
   IF w = NIL THEN RETURN NoMem END;
   LinkWindow(w);
   w^.userData:= d
  END;
  RETURN DialogOk
 END DisplayDialog;

 PROCEDURE FreeDialog(VAR d: DialogPtr);
 BEGIN
  IF d = NIL THEN RETURN END;
  WITH d^ DO
   IF w <> NIL THEN
    IGNORE RemoveGList(w, firstGadget, -1);
    UnlinkWindow(w);
    WaitBlit;
    CloseWindow(w);
    w:= NIL
   END;
   WHILE group.group.firstItem <> NIL DO
    FreeItem(d, ADR(group), group.group.firstItem)
   END
  END;
  FreeMem(d, SIZE(Dialog));
  d:= NIL
 END FreeDialog;

END Dialogs.

