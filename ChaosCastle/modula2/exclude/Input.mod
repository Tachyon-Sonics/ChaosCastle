IMPLEMENTATION MODULE Input;

 FROM SYSTEM IMPORT ADR, ADDRESS, LONGSET, CAST, TAG, SHORTSET, SHIFT;
 FROM IntuitionD IMPORT IDCMPFlags, IDCMPFlagSet, IntuiMessage, IntuiMessagePtr,
  selectUp, selectDown, menuUp, menuDown, middleUp, middleDown, WindowFlags,
  Gadget, GadgetPtr, Window, WindowPtr, ScreenPtr, menuNull;
 FROM IntuitionL IMPORT ModifyIDCMP, OpenWorkBench, DoubleClick;
 FROM IntuiMacros IMPORT MenuNum, ItemNum, SubNum;
 FROM InputEvent IMPORT Class, Qualifiers, QualifierSet, InputEvent,
  InputEventPtr, lButton, rButton, noButton, upPrefix;
 FROM ExecD IMPORT MemReqs, MemReqSet, MsgPort, MsgPortPtr, Message, MessagePtr,
  IORequest, IORequestPtr, IOStdReq, IOStdReqPtr, flush, quick, IOFlagSet,
  nonstd;
 FROM ExecL IMPORT AllocMem, FreeMem, SendIO, AbortIO, WaitIO, WaitPort, Wait,
  GetMsg, ReplyMsg, DoIO, CheckIO, OpenDevice, CloseDevice, Forbid, Permit;
 FROM ExecSupport IMPORT CreatePort, DeletePort, IsMsgPortEmpty,
  CreateExtIO, DeleteExtIO;
 FROM Keyboard IMPORT readMatrix, keyboardName;
 FROM GamePort IMPORT gamePortName, readEvent, setCType, setTrigger, Controller,
  GamePortTrigger, askCType, Keys, KeySet;
 FROM LowlevelD IMPORT sjaTypeGameCtlr, JP, JPSet, jpbButtonBlue,
  jpbButtonRed, jpbButtonYellow, jpbButtonGreen, jpbButtonForward,
  jpbButtonReverse, jpbButtonPlay, jpbJoyUp, jpbJoyDown, jpbJoyLeft,
  jpbJoyRight, sjaType, sjaReinitialize, jpDirectionMask;
 FROM OptLowlevelL IMPORT ReadJoyPort, SetJoyPortAttrsA, lowlevelBase;
 FROM DosD IMPORT ctrlC, ctrlD;
 FROM DosL IMPORT Delay;
 FROM Timer IMPORT timerName, microHz, addRequest, TimeRequest, TimeRequestPtr;
 FROM WorkbenchD IMPORT AppMenuItemPtr;
 FROM OptWorkbenchL IMPORT workbenchBase, AddAppMenuItemA, RemoveAppMenuItem;
 FROM AmigaBase IMPORT globals, timerPort, GetMenu, SetWindows,
  busyStat, programName, joystick, joystickGiven, SoundControl,
  DoWindowControl, DoGadgetControl;
 FROM Memory IMPORT SET16, CARD8, INT8, CARD16, INT16, CARD32, INT32, LockR,
  LockW, Unlock;
 FROM Checks IMPORT CheckMem, Check, Ask, Terminate, AddTermProc;
 IMPORT R, IntuitionL;

  (* Input from AmigaOS (input.device) *)
 CONST
  inputName = "input.device";
  writeEvent = nonstd+2;
  (* *)

 VAR
  gameEvent: InputEvent;
  oldjoy, stick: SET16;
  currenttypes: EventTypeSet;
  low, gInUse, break, flushing, waiting, plug: BOOLEAN;
  nextEvent: Event;
  nextMenu: CARD16;
  lastModifiers: ModifierSet;
  tags: ARRAY[0..2] OF CARD32;
  keys: ARRAY[0..12] OF SHORTSET;
  keymp, tmp, gamemp, inpmp, wbPort: MsgPortPtr;
  keyio, gameio, inpio: IOStdReqPtr;
  tio: TimeRequestPtr;
  appMenu: AppMenuItemPtr;
  lastseconds, lastmicros: LONGCARD;
  useStick: BOOLEAN;


 PROCEDURE AddEvents(types: EventTypeSet);
  VAR
   add{R.D7}: EventTypeSet;
 BEGIN
  add:= types;
  currenttypes:= currenttypes + add;
  LockW(globals);
  WITH globals^ DO
   IF eKEYBOARD IN add THEN INCL(idcmp, vanillaKey) END;
   IF eMOUSE IN add THEN INCL(idcmp, mouseButtons) END;
   IF eMENU IN add THEN INCL(idcmp, menuPick) END;
   IF eGADGET IN add THEN
    idcmp:= idcmp + IDCMPFlagSet{gadgetDown, gadgetUp, activeWindow, inactiveWindow}
   END
  END;
  Unlock(globals);
  SetWindows
 END AddEvents;

 PROCEDURE RemEvents(types: EventTypeSet);
  VAR
   rem{R.D7}: EventTypeSet;
 BEGIN
  rem:= types;
  currenttypes:= currenttypes - rem;
  LockW(globals);
  WITH globals^ DO
   IF eKEYBOARD IN rem THEN EXCL(idcmp, vanillaKey) END;
   IF eMOUSE IN rem THEN EXCL(idcmp, mouseButtons) END;
   IF eMENU IN rem THEN EXCL(idcmp, menuPick) END;
   IF eGADGET IN rem THEN
    idcmp:= idcmp - IDCMPFlagSet{gadgetDown, gadgetUp, activeWindow, inactiveWindow}
   END
  END;
  Unlock(globals);
  SetWindows
 END RemEvents;

 PROCEDURE WaitEvent;
  VAR
   sigs{R.D7}, ts{R.D6}, rcv{R.D5}: LONGSET;
   oldstick{R.D4}: SET16;
 BEGIN
  IF (nextEvent.type <> eNUL) OR (nextMenu <> menuNull) THEN RETURN END;
  IF ((timerPort <> NIL) AND NOT(IsMsgPortEmpty(timerPort))) OR
     ((globals^.windowPort <> NIL) AND NOT(IsMsgPortEmpty(globals^.windowPort))) OR
     ((wbPort <> NIL) AND NOT(IsMsgPortEmpty(wbPort))) THEN
   RETURN
  END;
  IF (globals^.mainWindow = NIL) AND (appMenu = NIL) THEN
   Delay(10);
   IF (workbenchBase = NIL) OR (OpenWorkBench() = NIL) THEN RETURN END;
   Delay(30);
   wbPort:= CreatePort(NIL, 0);
   IF wbPort <> NIL THEN
    appMenu:= AddAppMenuItemA(0, NIL, programName, wbPort, NIL);
    IF appMenu = NIL THEN
     DeletePort(wbPort); wbPort:= NIL; RETURN
    END
   END
  END;
  IF waiting THEN
   busyStat:= statWaiting;
   SetWindows;
   waiting:= FALSE
  END;
  ts:= LONGSET{tmp^.sigBit};
  sigs:= LONGSET{ctrlC, ctrlD} + ts;
  IF timerPort <> NIL THEN INCL(sigs, timerPort^.sigBit) END;
  IF globals^.windowPort <> NIL THEN INCL(sigs, globals^.windowPort^.sigBit) END;
  IF wbPort <> NIL THEN INCL(sigs, wbPort^.sigBit) END;
  LOOP
   IF useStick THEN
    tio^.node.command:= addRequest;
    tio^.node.flags:= IOFlagSet{};
    tio^.time.secs:= 0;
    tio^.time.micro:= 20000;
    SendIO(tio)
   END;
   rcv:= Wait(sigs);
   IF ctrlD IN rcv THEN break:= TRUE END;
   IF ctrlC IN rcv THEN Terminate END;
   IF (rcv - ts <> LONGSET{}) THEN
    IF useStick THEN
     IF CheckIO(tio) THEN AbortIO(tio) END; WaitIO(tio)
    END;
    EXIT
   END;
   IF useStick THEN
    WaitIO(tio); oldstick:= oldjoy;
    IF (oldstick <> GetStick()) THEN EXIT END
   END;
   IF SoundControl <> NIL THEN SoundControl END
  END
 END WaitEvent;

 PROCEDURE GetModifiers(qualifier{R.D1}: QualifierSet): ModifierSet;
  VAR
   modifiers{R.D0}: ModifierSet;
 BEGIN
  modifiers:= ModifierSet{};
  IF qualifier * QualifierSet{lShift, rShift} <> QualifierSet{} THEN
   INCL(modifiers, emSHIFT)
  END;
  IF control IN qualifier THEN INCL(modifiers, emCTRL) END;
  IF qualifier * QualifierSet{lAlt, rAlt} <> QualifierSet{} THEN
   INCL(modifiers, emALT)
  END;
  IF qualifier * QualifierSet{lCommand, rCommand} <> QualifierSet{} THEN
   INCL(modifiers, emCOMMAND)
  END;
  lastModifiers:= modifiers;
  RETURN modifiers
 END GetModifiers;

 PROCEDURE GetEvent(VAR e: Event);
  VAR
   message{R.D7}: IntuiMessagePtr;
 BEGIN
  IF nextEvent.type <> eNUL THEN
   e:= nextEvent;
   nextEvent.type:= eNUL;
   RETURN
  END;
  IF break THEN
   e.type:= eSYS;
   e.msg:= pQuit;
   break:= FALSE;
   RETURN
  END;
  e.type:= eNUL;
  e.dialog:= NIL;
  message:= NIL;
  IF timerPort <> NIL THEN
   message:= GetMsg(timerPort);
   IF message <> NIL THEN
    e.type:= eTIMER;
    RETURN
   END
  END;
  IF wbPort <> NIL THEN
   message:= GetMsg(wbPort)
  END;
  IF (message <> NIL) OR
     (NOT(flushing) AND (wbPort = NIL) AND (globals^.mainWindow = NIL)) THEN
   e.type:= eSYS;
   e.msg:= pActivate;
   IF appMenu <> NIL THEN
    IGNORE RemoveAppMenuItem(appMenu);
    REPEAT UNTIL GetMsg(wbPort) = NIL;
    DeletePort(wbPort);
    appMenu:= CAST(AppMenuItemPtr, NIL); wbPort:= NIL
   END;
   RETURN
  END;
  IF nextMenu <> menuNull THEN
   e.type:= eMENU;
   e.modifiers:= lastModifiers;
   IF GetMenu <> NIL THEN
    e.menu:= GetMenu(MenuNum(nextMenu), ItemNum(nextMenu), SubNum(nextMenu), nextMenu)
   ELSE
    e.menu:= NIL
   END;
   RETURN
  END;
  message:= GetMsg(globals^.windowPort);
  IF message <> NIL THEN
   WITH message^ DO
    e.modifiers:= GetModifiers(qualifier);
    IF idcmpWindow <> NIL THEN
     e.dialog:= idcmpWindow^.userData
    END;
    IF class = IDCMPFlagSet{vanillaKey} THEN
     e.type:= eKEYBOARD;
     e.ch:= CHR(code)
    ELSIF class = IDCMPFlagSet{mouseButtons} THEN
     e.type:= eMOUSE;
     IF DoubleClick(lastseconds, lastmicros, seconds, micros) THEN
      INCL(e.modifiers, emDOUBLE)
     END;
     GetMouse(e.mx, e.my);
     IF code = selectUp THEN e.mouseUp:= TRUE; e.button:= LeftButton
     ELSIF code = selectDown THEN e.mouseUp:= FALSE; e.button:= LeftButton
     ELSIF code = menuUp THEN e.mouseUp:= TRUE; e.button:= RightButton
     ELSIF code = menuDown THEN e.mouseUp:= FALSE; e.button:= RightButton
     ELSIF code = middleUp THEN e.mouseUp:= TRUE; e.button:= MiddleButton
     ELSIF code = middleDown THEN e.mouseUp:= FALSE; e.button:= MiddleButton
     END;
     lastseconds:= seconds;
     lastmicros:= micros
    ELSIF class = IDCMPFlagSet{gadgetDown} THEN
     IF NOT(DoGadgetControl(message)) THEN
      IF DoubleClick(lastseconds, lastmicros, seconds, micros) THEN
       INCL(e.modifiers, emDOUBLE)
      END;
      e.type:= eGADGET;
      e.gadget:= CAST(GadgetPtr, iAddress)^.userData;
      e.bNum:= LeftButton;
      e.gadgetUp:= FALSE;
      lastseconds:= seconds;
      lastmicros:= micros
     END
    ELSIF class = IDCMPFlagSet{gadgetUp} THEN
     IF NOT(DoGadgetControl(message)) THEN
      e.type:= eGADGET;
      e.gadget:= CAST(GadgetPtr, iAddress)^.userData;
      e.bNum:= LeftButton;
      e.gadgetUp:= TRUE
     END
    ELSIF class = IDCMPFlagSet{activeWindow} THEN
     IF DoGadgetControl(message) THEN
      e.type:= eGADGET;
      e.gadget:= idcmpWindow^.userData;
      e.bNum:= LeftButton;
      e.gadgetUp:= FALSE
     END
    ELSIF class = IDCMPFlagSet{inactiveWindow} THEN
     IF DoGadgetControl(message) THEN
      e.type:= eGADGET;
      e.gadget:= idcmpWindow^.userData;
      e.bNum:= LeftButton;
      e.gadgetUp:= TRUE
     END
    ELSIF class * IDCMPFlagSet{intuiTicks, newSize} <> IDCMPFlagSet{} THEN
     IGNORE DoGadgetControl(message)
    ELSIF class = IDCMPFlagSet{closeWindow} THEN
     e.type:= eGADGET;
     e.gadget:= idcmpWindow^.userData;
     e.bNum:= SysEvent;
     e.gadgetUp:= TRUE
    ELSIF class = IDCMPFlagSet{refreshWindow} THEN
     IF idcmpWindow^.userData = NIL THEN
      IF NOT(eREFRESH IN currenttypes) THEN (* main window *)
       IntuitionL.BeginRefresh(idcmpWindow);
       IntuitionL.EndRefresh(idcmpWindow, TRUE);
       e.type:= eNUL
      ELSE
       e.type:= eREFRESH;
       e.refreshGadget:= idcmpWindow^.userData
      END
     ELSE
      IntuitionL.BeginRefresh(idcmpWindow);
      IntuitionL.EndRefresh(idcmpWindow, TRUE);
      IF DoWindowControl(idcmpWindow) THEN
       e.type:= eREFRESH;
       e.refreshGadget:= NIL
      ELSE
       e.type:= eNUL
      END
     END
    ELSIF class = IDCMPFlagSet{menuPick} THEN
     IF code <> MAX(CARD16) THEN
      e.type:= eMENU;
      IF GetMenu <> NIL THEN
       e.menu:= GetMenu(MenuNum(code), ItemNum(code), SubNum(code), nextMenu)
      ELSE
       e.menu:= NIL
      END
     END
    END
   END;
   ReplyMsg(message)
  END;
  IF NOT(e.type IN currenttypes) THEN e.type:= eNUL END
 END GetEvent;

 PROCEDURE SendEvent(e: Event);
 BEGIN
  nextEvent:= e
 END SendEvent;

 PROCEDURE FlushEvents;
  VAR
   e: Event;
 BEGIN
  flushing:= TRUE;
  REPEAT
   GetEvent(e);
   IF e.type = eREFRESH THEN
    REPEAT BeginRefresh UNTIL EndRefresh()
   END
  UNTIL e.type = eNUL;
  flushing:= FALSE
 END FlushEvents;

 PROCEDURE BeginRefresh;
 BEGIN
  LockR(globals);
  WITH globals^ DO
   IF graphicWindow <> NIL THEN
    IntuitionL.BeginRefresh(graphicWindow)
   END
  END
 END BeginRefresh;

 PROCEDURE EndRefresh(): BOOLEAN;
 BEGIN
  WITH globals^ DO
   IF graphicWindow <> NIL THEN
    IntuitionL.EndRefresh(graphicWindow, TRUE)
   END
  END;
  Unlock(globals);
  RETURN TRUE
 END EndRefresh;

 PROCEDURE SetBusyStat(stat: CARD8);
 BEGIN
  IF stat = statWaiting THEN
   IF busyStat <> statWaiting THEN waiting:= TRUE END
  ELSIF stat <> busyStat THEN
   busyStat:= stat;
   SetWindows
  END
 END SetBusyStat;

 PROCEDURE Beep;
  VAR
   s{R.D7}: ScreenPtr;
 BEGIN
  LockR(globals);
  IF globals^.mainWindow <> NIL THEN
   s:= globals^.mainWindow^.wScreen
  ELSE
   s:= NIL
  END;
  Unlock(globals);
  IntuitionL.DisplayBeep(s)
 END Beep;

 PROCEDURE GetMouse(VAR x, y: INT16);
 BEGIN
  LockR(globals);
  WITH globals^ DO
   IF graphicWindow <> NIL THEN
    x:= graphicWindow^.mouseX;
    y:= graphicWindow^.mouseY
   ELSE
    x:= -1; y:= -1
   END
  END;
  Unlock(globals)
 END GetMouse;

 PROCEDURE SendGamePortReq;
 BEGIN
  gameio^.command:= readEvent; gameio^.flags:= IOFlagSet{};
  gameio^.data:= ADR(gameEvent); gameio^.length:= SIZE(gameEvent);
  SendIO(gameio)
 END SendGamePortReq;

 PROCEDURE GetStick(): SET16;
  VAR
   ret{R.D7}: SET16;
   joy{R.D6}: JPSet;
   pad{R.D5}: SHORTSET;
 BEGIN
  useStick:= TRUE;
  LockR(globals);
  IF (keyio <> NIL) AND (globals^.mainWindow <> NIL) AND
     (windowActive IN globals^.mainWindow^.flags) THEN
   keyio^.command:= readMatrix;
   keyio^.flags:= quick;
   keyio^.data:= ADR(keys);
   keyio^.length:= 13;
   DoIO(keyio);
   ret:= CAST(SET16, CARDINAL(SHIFT(CAST(SHORTCARD, keys[10]), 4))) (* F1..F8 *)
       + CAST(SET16, CARDINAL(SHIFT(CAST(SHORTCARD, keys[9] * SHORTSET{4..7}), -4))); (* Arrow *)
   IF keys[8] * SHORTSET{0, 3, 4} <> SHORTSET{} THEN INCL(ret, Joy1) END; (* SPACE, ENTER, RETURN *)
   IF (7 IN keys[1]) OR (0 IN keys[0]) OR (4 IN keys[12]) THEN INCL(ret, Joy1) END; (* 0, ~, LALT *)
   IF (4 IN keys[7]) OR (2 IN keys[8]) THEN INCL(ret, Joy2) END; (* ., TAB *)
   IF (keys[12] * SHORTSET{0, 1}) <> SHORTSET{} THEN INCL(ret, JoyShift) END; (* SHIFT *)
   ret:= ret + CAST(SET16, CARDINAL(SHIFT(CAST(SHORTCARD, keys[12] * SHORTSET{3, 4..7}), 2))); (* Modifiers *)
   IF (6 IN keys[11]) OR (1 IN keys[11]) THEN INCL(ret, JoyForward) END; (* F10, + *)
   IF (2 IN keys[9]) OR (0 IN keys[11]) THEN INCL(ret, JoyReverse) END; (* F9, - *)
   IF keys[7] * SHORTSET{5..7} <> SHORTSET{} THEN INCL(ret, JoyUp) END;
   IF keys[3] * SHORTSET{5..7} <> SHORTSET{} THEN INCL(ret, JoyDown) END;
   pad:= keys[7] + keys[5] + keys[3];
   IF 5 IN pad THEN INCL(ret, JoyLeft) END;
   IF 7 IN pad THEN INCL(ret, JoyRight) END
  ELSE
   ret:= SET16{}
  END;
  Unlock(globals);
  IF plug THEN
   IF low THEN
    joy:= ReadJoyPort(1);
    IF jpbJoyUp IN joy THEN INCL(ret, JoyUp) END;
    IF jpbJoyDown IN joy THEN INCL(ret, JoyDown) END;
    IF jpbJoyLeft IN joy THEN INCL(ret, JoyLeft) END;
    IF jpbJoyRight IN joy THEN INCL(ret, JoyRight) END;
    IF jpbButtonRed IN joy THEN INCL(ret, Joy1) END;
    IF jpbButtonBlue IN joy THEN INCL(ret, Joy2) END;
    IF jpbButtonYellow IN joy THEN INCL(ret, Joy3) END;
    IF jpbButtonGreen IN joy THEN INCL(ret, Joy4) END;
    IF jpbButtonPlay IN joy THEN INCL(ret, JoyPause) END;
    IF jpbButtonForward IN joy THEN INCL(ret, JoyForward) END;
    IF jpbButtonReverse IN joy THEN INCL(ret, JoyReverse) END
   ELSIF joystick AND CheckIO(gameio) THEN
    WaitIO(gameio);
    IF gameEvent.code = lButton THEN
     INCL(stick, Joy1)
    ELSIF gameEvent.code = lButton + upPrefix THEN
     EXCL(stick, Joy1)
    ELSIF gameEvent.code = rButton THEN
     INCL(stick, Joy2)
    ELSIF gameEvent.code = rButton + upPrefix THEN
     EXCL(stick, Joy2)
    ELSIF gameEvent.code = noButton THEN
     IF gameEvent.x < 0 THEN
      INCL(stick, JoyLeft); EXCL(stick, JoyRight)
     ELSIF gameEvent.x > 0 THEN
      INCL(stick, JoyRight); EXCL(stick, JoyLeft)
     ELSE
      EXCL(stick, JoyLeft); EXCL(stick, JoyRight)
     END;
     IF gameEvent.y < 0 THEN
      INCL(stick, JoyUp); EXCL(stick, JoyDown)
     ELSIF gameEvent.y > 0 THEN
      INCL(stick, JoyDown); EXCL(stick, JoyUp)
     ELSE
      EXCL(stick, JoyUp); EXCL(stick, JoyDown)
     END
    END;
    SendGamePortReq;
    ret:= ret + stick
   END
  END;
  oldjoy:= ret;
  RETURN ret
 END GetStick;

 PROCEDURE InitGamePort;
  VAR
   c: Controller;
   trig: GamePortTrigger;
 BEGIN
  gameio^.command:= askCType; gameio^.flags:= quick;
  gameio^.data:= ADR(c); gameio^.length:= 1;
  Forbid;
  DoIO(gameio);
  IF c <> noController THEN
   Permit;
   gInUse:= TRUE;
  (*
   IF Ask(ADR(gamePortName), ADR("already in use."), ADR("Continue"), ADR("Abort")) THEN
    RETURN
   ELSE
    Terminate
   END
  *)
  END;
  c:= absJoystick;
  gameio^.command:= setCType; gameio^.flags:= quick;
  gameio^.data:= ADR(c); gameio^.length:= 1;
  DoIO(gameio);
  Permit;
  trig.keys:= KeySet{downKeys, upKeys};
  trig.timeout:= MAX(CARD16);
  trig.xDelta:= 1;
  trig.yDelta:= 1;
  gameio^.command:= setTrigger; gameio^.flags:= quick;
  gameio^.data:= ADR(trig); gameio^.length:= SIZE(trig);
  DoIO(gameio);
  gameio^.command:= flush; gameio^.flags:= quick;
  gameio^.data:= NIL; gameio^.length:= 0;
  DoIO(gameio)
 END InitGamePort;

 PROCEDURE FlushGamePort;
  VAR
   c: Controller;
 BEGIN
  stick:= SET16{};
  IF gameio = NIL THEN RETURN END;
  IF NOT CheckIO(gameio) THEN AbortIO(gameio) END;
  WaitIO(gameio);
  IF NOT(gInUse) THEN
   c:= noController;
   gameio^.command:= setCType; gameio^.flags:= quick;
   gameio^.data:= ADR(c); gameio^.length:= 1;
   DoIO(gameio)
  END
 END FlushGamePort;

 PROCEDURE OpenIODevice(VAR mp{R.A2}: MsgPortPtr; size{R.D3}: INT32;
   name{R.D4}: ADDRESS; unit{R.D5}: INT32): ADDRESS;
  VAR
   nio{R.D7}: IORequestPtr;
 BEGIN
  mp:= CreatePort(NIL, 0);
  CheckMem(mp);
  nio:= CreateExtIO(mp, size);
  CheckMem(nio);
  OpenDevice(name, unit, nio, LONGSET{});
  Check(nio^.device = NIL, ADR("Cannot open"), name);
  RETURN nio
 END OpenIODevice;

 TYPE
  IORequestPtrPtr = POINTER TO IORequestPtr;

 PROCEDURE CloseIODevice(VAR mp{R.A2}: MsgPortPtr; io{R.A3}: IORequestPtrPtr);
 BEGIN
  IF io^ <> NIL THEN
   IF io^^.device <> NIL THEN
    CloseDevice(io^); io^^.device:= NIL
   END;
   DeleteExtIO(io^); io^:= NIL
  END;
  IF mp <> NIL THEN
   DeletePort(mp); mp:= NIL
  END
 END CloseIODevice;

 PROCEDURE Close;
 BEGIN
  FlushEvents;
  IF appMenu <> NIL THEN
   IGNORE RemoveAppMenuItem(appMenu);
   REPEAT UNTIL GetMsg(wbPort) = NIL;
   DeletePort(wbPort);
   appMenu:= CAST(AppMenuItemPtr, NIL); wbPort:= NIL
  END;
  IF low THEN
   IGNORE SetJoyPortAttrsA(1, TAG(tags, sjaReinitialize, 0, 0));
   low:= FALSE
  ELSIF joystick THEN
   FlushGamePort;
   CloseIODevice(gamemp, ADR(gameio))
  END;
  CloseIODevice(keymp, ADR(keyio));
  CloseIODevice(tmp, ADR(tio));
  CloseIODevice(inpmp, ADR(inpio))
 END Close;

BEGIN

 AddTermProc(Close);
 nextMenu:= menuNull;
 stick:= SET16{};
 IF NOT(joystickGiven) THEN joystick:= TRUE END;
 tio:= OpenIODevice(tmp, SIZE(TimeRequest), ADR(timerName), microHz);
 keyio:= OpenIODevice(keymp, SIZE(IOStdReq), ADR(keyboardName), 0);
 nextEvent.type:= eNUL;
 IF joystick THEN
  IF lowlevelBase <> NIL THEN
   low:= SetJoyPortAttrsA(1, TAG(tags, sjaType, sjaTypeGameCtlr, 0))
  END;
  IF NOT(low) THEN
   gameio:= OpenIODevice(gamemp, SIZE(IOStdReq), ADR(gamePortName), 1);
   InitGamePort;
   SendGamePortReq
  END;
  plug:= TRUE;
  IF NOT(joystickGiven) THEN plug:= GetStick() = SET16{} END;
  useStick:= FALSE
 END;

END Input.
