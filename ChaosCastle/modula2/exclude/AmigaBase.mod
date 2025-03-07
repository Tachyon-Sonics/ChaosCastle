IMPLEMENTATION MODULE AmigaBase;

 FROM SYSTEM IMPORT ADDRESS, ADR, TAG, CAST, REG, SETREG, SHIFT;
 FROM DosD IMPORT FileLockPtr, accessRead;
 FROM DosL IMPORT Lock, UnLock, CurrentDir;
 FROM ExecD IMPORT MemReqs, MemReqSet, MsgPort, MsgPortPtr, ExecBase;
 FROM ExecL IMPORT AllocMem, FreeMem, CopyMem, SetTaskPri, FindTask, GetMsg,
  ReplyMsg, Forbid, Permit, execBase;
 FROM ExecSupport IMPORT CreatePort, DeletePort;
 FROM IntuitionD IMPORT Window, WindowPtr, IDCMPFlags, IDCMPFlagSet,
  WindowFlags, WindowFlagSet, WaTags, IntuiMessage, IntuiMessagePtr,
  Screen, ScreenPtr, ScreenFlags, ScreenFlagSet;
 FROM IntuitionL IMPORT ModifyIDCMP, SetMenuStrip, ClearMenuStrip,
  ClearPointer, SetPointer, SetWindowPointerA, intuitionVersion,
  ActivateWindow, LockPubScreen, UnlockPubScreen, GetScreenData;
 FROM GraphicsD IMPORT ChipRevs, ChipRevSet, GfxBase, ColorMapPtr, bmaDepth,
  ViewModes, ViewModeSet;
 FROM GraphicsL IMPORT graphicsBase, graphicsVersion, GetRGB4, GetBitMapAttr;
 FROM OptIconL IMPORT GetDiskObject, FindToolType, FreeDiskObject, iconBase;
 FROM WorkbenchD IMPORT DiskObjectPtr;
 FROM Checks IMPORT CheckMem, Check, AddTermProc;
 FROM Memory IMPORT LockR, LockW, Unlock, multiThread, InitList, List,
  AddTail, Remove, First, Tail, Next, Empty;
 IMPORT Arts, R, ExecL;

 TYPE
  StrPtr = POINTER TO ARRAY[0..127] OF CHAR;
  SSprite = ARRAY[0..51] OF CARDINAL;

 CONST
  Version = "$VER: ChaosCastle 1.0 (20.07.100)";
  BusyPointer = SSprite{
   0, 0, 448, 0, 128, 0, 2032, 1792, 8180, 6664, 16362, 13332, 16380,
   8194, 41090, 40828, 40956, 0, 36856, 448, 23404, 6128,
   16254, 12280, 12154, 8188, 32591, 8188, 32543, 16382, 24125,
   16382, 32639, 16382, 32766, 8189, 12282, 8188, 16380, 4090,
   7144, 6132, 16252, 8642, 13286, 8192, 24578, 16385, 24578, 16385, 0, 0};

 VAR
  lock, oldlock: FileLockPtr;
  oldpri: SHORTINT;
  busy, pointer: ADDRESS;

 PROCEDURE LinkWindow(w: WindowPtr);
  VAR
   wr{R.D7}: WinRecPtr;
 BEGIN
  wr:= AllocMem(SIZE(WinRec), MemReqSet{public});
  CheckMem(wr);
  LockW(globals);
  WITH globals^ DO
   WITH wr^ DO
    win:= w; rA4:= REG(R.A4);
    WinControl:= WindowControl;
    GadControl:= GadgetControl
   END;
   w^.userPort:= windowPort;
   IF Empty(windows) THEN
    mainWindow:= w
   END;
   AddTail(windows, wr^.node)
  END;
  Unlock(globals);
  SetWindows
 END LinkWindow;

 PROCEDURE IsBusyPointer(): BOOLEAN;
 BEGIN
  RETURN (busyStat = 3)
 END IsBusyPointer;

 PROCEDURE SetWindows;
  VAR
   tagbuff: ARRAY[0..4] OF LONGCARD;
   cur{R.D7}, tail{R.D6}: WinRecPtr;
   w{R.A2}: WindowPtr;
 BEGIN
  IF SetMenus <> NIL THEN SetMenus END;
  LockR(globals);
  cur:= First(globals^.windows);
  tail:= Tail(globals^.windows);
  WHILE cur <> tail DO
   w:= cur^.win;
   IF (busyStat = 0) OR (busyStat = 2) THEN
    IF intuitionVersion >= 39 THEN
     SetWindowPointerA(w, TAG(tagbuff, 0))
    ELSE
     ClearPointer(w)
    END
   ELSIF busyStat = 3 THEN
    IF intuitionVersion >= 39 THEN
     SetWindowPointerA(w, TAG(tagbuff, waBusyPointer, TRUE, waPointerDelay, FALSE, 0))
    ELSE
     SetPointer(w, busy, 24, 16, -1, -6)
    END
   ELSE
    SetPointer(w, pointer, 1, 16, 0, 0)
   END;
   ClearMenuStrip(w);
   WITH globals^ DO
    IF menuPick IN idcmp THEN
     EXCL(w^.flags, rmbTrap)
    ELSE
     INCL(w^.flags, rmbTrap)
    END;
    ModifyIDCMP(w, idcmp);
    IF firstMenu <> NIL THEN
     IGNORE SetMenuStrip(w, firstMenu)
    END;
    cur:= Next(cur^.node)
   END
  END;
  Unlock(globals)
 END SetWindows;

 PROCEDURE DoWindowControl(w: WindowPtr): BOOLEAN;
  VAR
   oldA4{R.D7}: ADDRESS;
   res{R.D6}: BOOLEAN;
   cur{R.D5}, tail{R.D4}: WinRecPtr;
 BEGIN
  LockR(globals);
  WITH globals^ DO
   cur:= First(windows);
   tail:= Tail(windows);
   WHILE cur <> tail DO
    WITH cur^ DO
     IF win = w THEN
      IF WinControl = NIL THEN Unlock(globals); RETURN FALSE END;
      oldA4:= REG(R.A4);
      SETREG(R.A4, rA4);
      res:= WinControl(win);
      SETREG(R.A4, oldA4);
      Unlock(globals);
      RETURN res
     END
    END;
    cur:= Next(cur^.node)
   END
  END;
  Unlock(globals)
 END DoWindowControl;

 PROCEDURE DoGadgetControl(msg: IntuiMessagePtr): BOOLEAN;
  VAR
   oldA4{R.D7}: ADDRESS;
   res{R.D6}: BOOLEAN;
   cur{R.D5}, tail{R.D4}: WinRecPtr;
 BEGIN
  LockR(globals);
  WITH globals^ DO
   cur:= First(windows);
   tail:= Tail(windows);
   WHILE cur <> tail DO
    WITH cur^ DO
     IF win = msg^.idcmpWindow THEN
      IF GadControl = NIL THEN Unlock(globals); RETURN FALSE END;
      oldA4:= REG(R.A4);
      SETREG(R.A4, rA4);
      res:= GadControl(msg);
      SETREG(R.A4, oldA4);
      Unlock(globals);
      RETURN res
     END
    END;
    cur:= Next(cur^.node)
   END
  END;
  Unlock(globals)
 END DoGadgetControl;

 PROCEDURE StripIntuiMessage(mp: MsgPortPtr; w: WindowPtr);
  VAR
   msg{R.A2}: IntuiMessagePtr;
   succ{R.D7}: ADDRESS;
 BEGIN
  msg:= CAST(IntuiMessagePtr, mp^.msgList.head);
  LOOP
   succ:= msg^.execMessage.node.succ;
   IF succ = NIL THEN EXIT END;
   IF msg^.idcmpWindow = w THEN
    ExecL.Remove(msg);
    ReplyMsg(msg)
   END;
   msg:= succ
  END
 END StripIntuiMessage;

 PROCEDURE UnlinkWindow(w: WindowPtr);
  VAR
   msg{R.D7}: ADDRESS;
   cur{R.D6}: WinRecPtr;
 BEGIN
  ClearPointer(w);
  ClearMenuStrip(w);
  ModifyIDCMP(w, IDCMPFlagSet{menuPick});
  INCL(w^.flags, rmbTrap);
  Forbid;
  StripIntuiMessage(w^.userPort, w);
  w^.userPort:= NIL;
  ModifyIDCMP(w, IDCMPFlagSet{});
  Permit;
  LockW(globals);
  WITH globals^ DO
   cur:= First(windows);
   WHILE cur^.win <> w DO cur:= Next(cur^.node) END;
   Remove(cur^.node);
   FreeMem(cur, SIZE(WinRec));
   IF mainWindow = w THEN
    IF Empty(windows) THEN mainWindow:= NIL ELSE
     cur:= First(windows);
     mainWindow:= cur^.win
    END
   END
  END;
  Unlock(globals)
 END UnlinkWindow;

 PROCEDURE FindBestPen(color: LONGCARD; avoidPen: SHORTCARD): LONGCARD;
  VAR
   wbench: Screen;
   s: ScreenPtr;
   cm: ColorMapPtr;
   rgb, pen: LONGCARD;
   r, g, b, rc, gc, bc, ra, ga, ba, rd, gd, bd: INTEGER;
   diff, best, depth: INTEGER;
   c, count: CARDINAL;
   lock: BOOLEAN;
 BEGIN
  lock:= FALSE;
  LockR(globals);
  IF globals^.mainWindow <> NIL THEN
   s:= globals^.mainWindow^.wScreen
  ELSIF intuitionVersion >= 36 THEN
   s:= LockPubScreen(NIL); lock:= TRUE
  ELSIF GetScreenData(ADR(wbench), SIZE(wbench), ScreenFlagSet{wbenchScreen}, NIL) THEN
   s:= ADR(wbench)
  ELSE
   RETURN 1
  END;
  Unlock(globals);
  r:= ((color DIV 65536) MOD 256) DIV 16;
  g:= ((color MOD 65536) DIV 256) DIV 16;
  b:= (color MOD 256) DIV 16;
  IF graphicsVersion >= 39 THEN
   depth:= GetBitMapAttr(s^.rastPort.bitMap, bmaDepth)
  ELSE
   depth:= s^.rastPort.bitMap^.depth
  END;
  IF (ham IN s^.viewPort.modes) THEN DEC(depth, 2) END;
  IF depth < 8 THEN
   count:= SHIFT(1, depth)
  ELSE
   count:= 256
  END;
  cm:= s^.viewPort.colorMap;
  IF cm^.count < count THEN count:= cm^.count END;
  rgb:= GetRGB4(cm, avoidPen);
  ra:= rgb DIV 256;
  ga:= (rgb DIV 16) MOD 16;
  ba:= rgb MOD 16;
  IF avoidPen = 1 THEN pen:= 0 ELSE pen:= 1 END;
  best:= MAX(INTEGER);
  FOR c:= 0 TO count - 1 DO
   rgb:= GetRGB4(cm, c);
   rc:= rgb DIV 256;
   gc:= (rgb DIV 16) MOD 16;
   bc:= rgb MOD 16;
   rd:= ABS(rc - ra);
   gd:= ABS(gc - ga);
   bd:= ABS(bc - ba);
   IF (rd > 6) OR (gd > 6) OR (bd > 6) OR (rd + gd + bd > 11) THEN
    diff:= ABS(rc - r) + ABS(gc - g) + ABS(bc - b);
    IF diff < best THEN
     best:= diff; pen:= c
    END
   END
  END;
  IF lock THEN
   UnlockPubScreen(NIL, s)
  END;
  RETURN pen
 END FindBestPen;

 PROCEDURE InitName;
  TYPE
   CARD32Ptr = POINTER TO LONGCARD;
  VAR
   file{R.A3}, dir{R.A2}: StrPtr;
   c{R.D7}, p{R.D6}: CARDINAL;
   ch{R.D5}: CHAR;
 BEGIN
  dir:= ADR(Version);
  programName:= Arts.programName;
  file:= programName;
  c:= 0; p:= 0;
  LOOP
   ch:= file^[c]; INC(c);
   IF ch = 0C THEN EXIT END;
   IF (ch = "/") OR (ch = ":") THEN p:= c END
  END;
  IF p <> 0 THEN
   dir:= AllocMem(p + 1, MemReqSet{});
   IF dir <> NIL THEN
    CopyMem(file, dir, p); dir^[p]:= 0C;
    lock:= Lock(dir, accessRead);
    IF lock <> NIL THEN
     oldlock:= CurrentDir(lock)
    END;
    FreeMem(dir, p + 1)
   END;
   programName:= ADR(file^[p])
  END;
  globals:= AllocMem(SIZE(Globals) + 4, MemReqSet{public, memClear});
  CheckMem(globals);
  CAST(CARD32Ptr, globals)^:= SIZE(Globals);
  INC(globals, 4);
  LockW(globals);
  globals^.windowPort:= CreatePort(NIL, 0);
  CheckMem(globals^.windowPort);
  InitList(globals^.windows);
  Unlock(globals)
 END InitName;

 TYPE
  CharPtr = POINTER TO CHAR;

 PROCEDURE ReadInt(string: CharPtr; min, max: LONGINT): LONGINT;
  VAR
   val{R.D7}: LONGINT;
   z{R.D6}: BOOLEAN;
 BEGIN
  val:= 0; z:= FALSE;
  WHILE string^ <> 0C DO
   IF string^ = "-" THEN z:= NOT z END;
   IF (string^ >= "0") AND (string^ <= "9") AND (val <= 214748363) THEN
    val:= val * 10 + (ORD(string^) - 48)
   END;
   INC(string)
  END;
  IF z THEN val:= -val END;
  IF val < min THEN val:= min ELSIF val > max THEN val:= max END;
  RETURN val
 END ReadInt;

 PROCEDURE InitToolTypes;
  VAR
   diskObject{R.A3}: DiskObjectPtr;
   string{R.A2}: CharPtr;
   c{R.D5}: CARDINAL;
   p{R.D7}: INTEGER;
   z{R.D6}: BOOLEAN;
 BEGIN
  fontName:= "topaz.font"; fontSize:= 8;
  givenSM:= 0; dbGiven:= FALSE;
  givenAM:= 0; amGiven:= FALSE;
  maskPlaneGiven:= FALSE; givenMaskPlane:= TRUE;
  joystick:= TRUE; smGiven:= FALSE;
  bSmartRefresh:= FALSE; dSmartRefresh:= FALSE;
  filterGiven:= FALSE; closeWB:= FALSE;
  musicBoost:= 4; soundBoost:= 4; ahiGiven:= FALSE;
  speaker:= FALSE; joystickGiven:= FALSE;
  surround:= FALSE; givenAhi:= TRUE;
  askAM:= FALSE; dither:= TRUE; ditherGiven:= FALSE;
  globals^.multiThread:= FALSE;
  IF graphicsVersion >= 36 THEN
   customMask:= ((graphicsBase^.chipRevBits0 - ChipRevSet{hrAgnus, hrDenise, aaAlice, aaLisa, aaMLisa}) = ChipRevSet{})
  ELSE
   customMask:= TRUE
  END;
  IF iconBase = NIL THEN RETURN END;
  diskObject:= GetDiskObject(programName);
  IF diskObject = NIL THEN RETURN END;
  string:= FindToolType(diskObject^.toolTypes, ADR("Joystick"));
  IF string <> NIL THEN
   joystickGiven:= TRUE;
   joystick:= (string^ <> "n") AND (string^ <> "N")
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("Font"));
  IF string <> NIL THEN
   c:= 0; fontGiven:= TRUE;
   WHILE (c < 31) AND (string^ <> 0C) DO
    fontName[c]:= string^;
    INC(c); INC(string)
   END;
   fontName[c]:= 0C
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("AskScreenMode"));
  IF (string <> NIL) THEN
   askMode:= (string^ = "y") OR (string^ = "Y")
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("AskAudioMode"));
  IF (string <> NIL) THEN
   askAM:= (string^ = "y") OR (string^ = "Y")
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("ScreenMode"));
  IF string <> NIL THEN
   smGiven:= TRUE;
   givenSM:= ReadInt(string, 0, MAX(LONGINT))
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("AudioMode"));
  IF string <> NIL THEN
   amGiven:= TRUE;
   givenAM:= ReadInt(string, 0, MAX(LONGINT))
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("DoubleBuffer"));
  IF (string <> NIL) THEN
   givenDB:= (string^ = "y") OR (string^ = "Y"); dbGiven:= TRUE
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("MultiThread"));
  IF (string <> NIL) THEN
   globals^.multiThread:= (string^ = "y") OR (string^ = "Y")
  END;
  multiThread:= globals^.multiThread;
  string:= FindToolType(diskObject^.toolTypes, ADR("Dither"));
  IF (string <> NIL) THEN
   dither:= (string^ = "y") OR (string^ = "Y"); ditherGiven:= TRUE
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("CustomMask"));
  IF string <> NIL THEN
   customMask:= (string^ = "y") OR (string^ = "Y");
   maskBM:= customMask
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("bSmartRefresh"));
  IF (string <> NIL) THEN
   bSmartRefresh:= (string^ = "y") OR (string^ = "Y")
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("dSmartRefresh"));
  IF (string <> NIL) THEN
   dSmartRefresh:= (string^ = "y") OR (string^ = "Y")
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("CloseWorkbench"));
  IF (string <> NIL) THEN
   closeWB:= (string^ = "y") OR (string^ = "Y")
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("AudioFilter"));
  IF (string <> NIL) THEN
   filterGiven:= TRUE;
   filter:= (string^ = "y") OR (string^ = "Y")
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("Headphones"));
  IF (string <> NIL) THEN
   speaker:= (string^ = "y") OR (string^ = "Y")
  END;
  IF execBase^.libNode.version < 36 THEN
   filterGiven:= TRUE; filter:= FALSE;
   speaker:= FALSE
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("AHI"));
  IF (string <> NIL) THEN
   ahiGiven:= TRUE;
   givenAhi:= (string^ = "y") OR (string^ = "Y")
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("Prologic"));
  IF (string <> NIL) THEN
   surround:= (string^ = "y") OR (string^ = "Y")
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("MaskPlane"));
  IF (string <> NIL) THEN
   maskPlaneGiven:= TRUE;
   givenMaskPlane:= (string^ = "y") OR (string^ = "Y")
  END;
  string:= FindToolType(diskObject^.toolTypes, ADR("FontSize"));
  IF string <> NIL THEN fontSize:= ReadInt(string, 1, 127) END;
  string:= FindToolType(diskObject^.toolTypes, ADR("SoundBoost"));
  IF string <> NIL THEN soundBoost:= ReadInt(string, 0, 127) END;
  string:= FindToolType(diskObject^.toolTypes, ADR("MusicBoost"));
  IF string <> NIL THEN musicBoost:= ReadInt(string, 0, 127) END;
  string:= FindToolType(diskObject^.toolTypes, ADR("SoundPriority"));
  IF string <> NIL THEN soundPriority:= ReadInt(string, -127, 127) END;
  string:= FindToolType(diskObject^.toolTypes, ADR("MusicPriority"));
  IF string <> NIL THEN musicPriority:= ReadInt(string, -127, 127) END;
  string:= FindToolType(diskObject^.toolTypes, ADR("TaskPriority"));
  IF string <> NIL THEN
   oldpri:= SetTaskPri(FindTask(NIL), ReadInt(string, -127, 127))
  END;
  FreeDiskObject(diskObject)
 END InitToolTypes;

 PROCEDURE CloseAll;
 BEGIN
  IF busy <> NIL THEN
   FreeMem(busy, SIZE(SSprite)); busy:= NIL
  END;
  IF pointer <> NIL THEN
   FreeMem(pointer, 16); pointer:= NIL
  END;
  LockW(globals);
  WITH globals^ DO
   IF windowPort <> NIL THEN
    DeletePort(windowPort);
    windowPort:= NIL
   END
  END;
  Unlock(globals);
  IF timerPort <> NIL THEN
   DeletePort(timerPort);
   timerPort:= NIL
  END;
 (* Flush Pri *)
  IF oldpri <> MIN(SHORTINT) THEN
   IGNORE SetTaskPri(FindTask(NIL), oldpri);
   oldpri:= MIN(SHORTINT)
  END;
 (* Flush Name *)
  IF lock <> NIL THEN
   IGNORE CurrentDir(oldlock);
   UnLock(lock); lock:= NIL
  END;
  IF globals <> NIL THEN
   DEC(globals, 4);
   FreeMem(globals, SIZE(Globals) + 4)
  END
 END CloseAll;

BEGIN

 oldpri:= MIN(SHORTINT);
 SoundControl:= NIL;
 AddTermProc(CloseAll);
 InitName;
 InitToolTypes;
 timerPort:= CreatePort(NIL, 0);
 CheckMem(timerPort);
 globals^.idcmp:= IDCMPFlagSet{closeWindow, refreshWindow, newSize};
 pointer:= AllocMem(16, MemReqSet{public, chip, memClear});
 CheckMem(pointer);
 busy:= AllocMem(SIZE(SSprite), MemReqSet{public, chip});
 CheckMem(busy);
 CopyMem(ADR(BusyPointer), busy, SIZE(SSprite));

END AmigaBase.
