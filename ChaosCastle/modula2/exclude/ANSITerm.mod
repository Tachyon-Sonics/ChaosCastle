IMPLEMENTATION MODULE ANSITerm;

 FROM SYSTEM IMPORT ADR, ADDRESS, ASSEMBLE, CAST, REG, SETREG, SHIFT, TAG;
 FROM Arts IMPORT dosCmdBuf, dosCmdLen, Terminate;
 FROM Memory IMPORT CARD8, INT8;
 FROM ExecD IMPORT Library, LibFlags, MemReqs, MemReqSet, Task, TaskPtr;
 FROM ExecL IMPORT AllocMem, Remove, FreeMem, GetMsg, WaitPort, ReplyMsg,
  FindTask, Forbid, Permit;
 IMPORT R;
 FROM IntuitionD IMPORT NewScreen, NewWindow, ScreenFlags, ScreenFlagSet,
  WindowFlags, WindowFlagSet, Screen, Window, ScreenPtr, WindowPtr,
  IDCMPFlags, IDCMPFlagSet, customScreen, IntuiMessagePtr, IntuiText;
 FROM IntuitionL IMPORT OpenScreen, OpenWindow, CloseScreen, CloseWindow,
  AutoRequest, CloseWorkBench, OpenWorkBench;
 FROM InputEvent IMPORT Qualifiers, QualifierSet;
 FROM GraphicsD IMPORT ViewPort, ViewPortPtr, RastPort, RastPortPtr,
  ViewModes, ViewModeSet, TextAttr, FontFlags, FontFlagSet, TextFont,
  TextFontPtr, rptagFont;
 FROM GraphicsL IMPORT Move, Text, SetRast, ScrollRaster, SetAPen, SetBPen,
  RectFill, LoadRGB4, graphicsVersion, GetRPAttrA;

TYPE
 Line = ARRAY[0..79] OF CHAR;
 LinePtr = POINTER TO Line;
 UserPtr = POINTER TO User;
 User = RECORD
  X, Y: SHORTCARD;
  ragain: BOOLEAN;
  oldch: CHAR;
  t: ARRAY[0..23] OF LinePtr;
  line: ARRAY[0..23] OF Line;
 (*  *)
  s: ScreenPtr;
  w: WindowPtr;
  rp: RastPortPtr;
  xSize, baseLine: SHORTCARD;
  color: BOOLEAN;
 END;
 TerminalPtr = UserPtr;
 Terminal = User;
 CollArr = ARRAY[0..7] OF CARDINAL;
 BlackWhite = ARRAY[0..1] OF CARDINAL;

CONST
 colors = ADR(CollArr{0000H, 0F00H, 00F0H, 0FF0H, 022FH, 0F0FH, 00FFH, 0FFFH});
 blackwhite = ADR(BlackWhite{0000H, 0FFFH});

VAR
 TR: User;


PROCEDURE OpenTerminal(userName: ADDRESS): BOOLEAN;
 (*$ EntryClear:= TRUE *)
 VAR tags: ARRAY[0..2] OF ADDRESS;
     font: TextFontPtr;
     ns: NewScreen;
     nw: NewWindow;
     ta: TextAttr;
     term{R.D5}: TerminalPtr;
     AText, BText, CText, TText, FText: IntuiText;
     l: SHORTCARD;
BEGIN
 term:= ADR(TR);
 IF term <> NIL THEN
  WITH ta DO
   name:= ADR("topaz.font");
   ySize:= 8;
   flags:= FontFlagSet{romFont}
  END;
  WITH ns DO
   width:= 640; height:= 203; depth:= 1;
   blockPen:= 1;
   viewModes:= ViewModeSet{hires};
   type:= customScreen + ScreenFlagSet{screenHires};
   font:= ADR(ta)
  END;
  WITH AText DO
   leftEdge:= 6; topEdge:= 3;
   backPen:= 1; iTextFont:= ADR(ta);
   iText:= ADR("TopazTerm.library ask you:");
   nextText:= ADR(BText)
  END;
  WITH BText DO
   leftEdge:= 6; topEdge:= 12;
   backPen:= 1; iTextFont:= ADR(ta);
   iText:= ADR("Please choose mode for");
   nextText:= ADR(CText)
  END;
  WITH CText DO
   leftEdge:= 6; topEdge:= 21;
   backPen:= 1; iTextFont:= ADR(ta);
   iText:= userName
  END;
  WITH TText DO
   leftEdge:= 6; topEdge:= 3;
   backPen:= 1; frontPen:= 3; iTextFont:= ADR(ta);
   iText:= ADR("Color")
  END;
  WITH FText DO
   leftEdge:= 6; topEdge:= 3;
   backPen:= 1; iTextFont:= ADR(ta);
   iText:= ADR("Black & white")
  END;
  WITH term^ DO
   color:= TRUE;
(*   color:= AutoRequest(NIL, ADR(AText), ADR(TText), ADR(FText),
    IDCMPFlagSet{}, IDCMPFlagSet{}, 320, 64); *)
   IF color THEN ns.depth:= 3 END;
   IGNORE CloseWorkBench();
   s:= OpenScreen(ns);
   IF s <> NIL THEN
    IF color THEN
     LoadRGB4(ADR(s^.viewPort), colors, 8)
    ELSE
     LoadRGB4(ADR(s^.viewPort), blackwhite, 2)
    END;
    IF graphicsVersion >= 40 THEN
     GetRPAttrA(ADR(s^.rastPort), TAG(tags, rptagFont, ADR(font), 0))
    ELSE
     font:= s^.rastPort.font
    END;
    xSize:= font^.xSize;
    baseLine:= font^.baseline;
    WITH nw DO
     topEdge:= 11; width:= 640; height:= 192;
     idcmpFlags:= IDCMPFlagSet{vanillaKey, rawKey, inactiveWindow};
     flags:= WindowFlagSet{borderless, activate, rmbTrap, noCareRefresh};
     screen:= s;
     type:= customScreen
    END;
    w:= OpenWindow(nw);
    IF w <> NIL THEN
     rp:= w^.rPort;
     SetAPen(rp, 1);
     SetBPen(rp, 0);
     FOR l:= 0 TO 23 DO
      t[l]:= ADR(line[l])
     END;
     RETURN TRUE
    END;
    CloseScreen(s); s:= NIL
   END
  END
 END;
 IGNORE OpenWorkBench();
 RETURN FALSE
END OpenTerminal;
(*$ POP EntryClear *)

PROCEDURE user(): TerminalPtr;
 VAR exec[4]: ADDRESS;
BEGIN
 RETURN ADR(TR)
END user;

PROCEDURE CloseTerminal;
 VAR term{R.D2}, prev{R.D3}: TerminalPtr;
BEGIN
 term:= user();
 WITH term^ DO
  IF w <> NIL THEN CloseWindow(w) END;
  IF s <> NIL THEN CloseScreen(s) END
 END;
 IGNORE OpenWorkBench()
END CloseTerminal;

PROCEDURE Goto(x, y: INT8);
BEGIN
 WITH TR DO
  IF x >= 0 THEN X:= x END;
  IF y >= 0 THEN Y:= y END;
  IF X > 79 THEN X:= 79 END;
  IF Y > 23 THEN Y:= 23 END;
  Move(rp, X * xSize, SHIFT(Y, 3) + baseLine);
 END
END Goto;

PROCEDURE ClearScreen;
 VAR l{R.D2}, c{R.D3}: CARDINAL;
BEGIN
 WITH TR DO
  FOR l:= 0 TO 23 DO
   FOR c:= 0 TO 79 DO
    t[l]^[c]:= " "
   END
  END;
  Goto(0, 0);
  SetRast(rp, 0)
 END
END ClearScreen;

PROCEDURE Read(VAR ch: CHAR);
 VAR done{R.D2}: BOOLEAN;
     m{R.D3}: IntuiMessagePtr;
BEGIN
 WITH TR DO
  IF ragain THEN
   ch:= oldch;
   ragain:= FALSE
  ELSE
   done:= FALSE;
   REPEAT
    m:= GetMsg(w^.userPort);
    IF m = NIL THEN
     ch:= 0C;
     done:= TRUE
    ELSE
     WITH m^ DO
      IF class = IDCMPFlagSet{vanillaKey} THEN
       ch:= CHR(code);
       done:= TRUE
      ELSIF class = IDCMPFlagSet{rawKey} THEN
       IF (code >= 76) AND (code <= 79) THEN
        ch:= CHR(code - 48);
        IF qualifier * QualifierSet{control, lAlt, rAlt, lCommand, rCommand} <> QualifierSet{} THEN
         DEC(ch, 5)
        END;
        IF (lShift IN qualifier) OR (rShift IN qualifier) THEN
         INC(ch, 128)
        END;
        done:= TRUE
       END
      ELSIF class = IDCMPFlagSet{inactiveWindow} THEN
       ch:= CHR(12);
       done:= TRUE
      END
     END;
     ReplyMsg(m)
    END
   UNTIL done;
   oldch:= ch
  END
 END
END Read;

PROCEDURE ReadAgain;
BEGIN
 TR.ragain:= TRUE
END ReadAgain;

PROCEDURE WaitChar(VAR ch: CHAR);
BEGIN
 WITH TR DO
  REPEAT
   WaitPort(w^.userPort);
   Read(ch)
  UNTIL ch <> 0C
 END
END WaitChar;

PROCEDURE Report(x, y: INT8): CHAR;
BEGIN
 IF (x < 80) AND (y < 24) THEN
  RETURN TR.t[y]^[x]
 ELSE
  RETURN 0C
 END
END Report;

PROCEDURE Write(ch: CHAR);
BEGIN
 WITH TR DO
  t[Y]^[X]:= ch;
  Text(rp, ADR(ch), 1); INC(X);
  IF X > 79 THEN Goto(79, Y) END
 END
END Write;

PROCEDURE WriteString(st: ARRAY OF CHAR);
 (*$ CopyDyn:= FALSE *)
 VAR c{R.D2}: CARDINAL;
BEGIN
 c:= 0;
 WITH TR DO
  WHILE (c <= CARDINAL(HIGH(st))) AND (st[c] <> 0C) DO
   t[Y]^[X + c]:= st[c]; INC(c)
  END;
  Text(rp, ADR(st), c);
  INC(X, c)
 END
END WriteString;

PROCEDURE WriteLn;
BEGIN
 WITH TR DO
  IF Y = 23 THEN
   Goto(0, 23)
  ELSE
   Goto(0, Y + 1)
  END
 END
END WriteLn;

PROCEDURE WriteAt(x, y: INT8; ch: CHAR);
BEGIN
 Goto(x, y);
 Write(ch)
END WriteAt;

PROCEDURE MoveChar(sx, sy: INT8; sch: CHAR; dx, dy: INT8; dch: CHAR);
BEGIN
 IF (sx <> dx) OR (sy <> dy) THEN WriteAt(sx, sy, sch) END;
 WriteAt(dx, dy, dch)
END MoveChar;

PROCEDURE ReadString(VAR st: ARRAY OF CHAR);
 VAR pos{R.D2}: CARDINAL;
     ch: CHAR;
BEGIN
 WITH TR DO
  pos:= 0;
  LOOP
   WaitChar(ch);
   IF (ch = 12C) OR (ch = 15C) THEN
    st[pos]:= 0C;
    RETURN
   ELSIF (ch >= " ") AND (ch <= CHR(126)) THEN
    IF pos < CARDINAL(HIGH(st)) THEN
     st[pos]:= ch; INC(pos);
     Write(ch)
    END
   ELSE
    IF pos > 0 THEN
     DEC(pos); Goto(X-1, Y);
     Write(" "); Goto(X-1, Y)
    END
   END
  END
 END
END ReadString;

PROCEDURE Ghost(x, y: INT8; ch: CHAR);
BEGIN
 WITH TR DO
  Goto(x, y);
  Text(rp, ADR(ch), 1); INC(X);
  IF X > 79 THEN Goto(79, Y) END
 END
END Ghost;

PROCEDURE ClearLine(y: INT8);
 VAR c{R.D4}: SHORTCARD;
BEGIN
 IF y >= 24 THEN RETURN END;
 WITH TR DO
  SetAPen(rp, 0);
  RectFill(rp, 0, SHIFT(y,3), w^.width - 1, SHIFT(y,3) + 7);
  SetAPen(rp, 1);
  FOR c:= 0 TO 79 DO t[y]^[c]:= " " END
 END
END ClearLine;

PROCEDURE Color(c: CARD8);
BEGIN
 WITH TR DO
  IF color THEN SetAPen(rp, c) END
 END
END Color;

(***********)
(**)BEGIN(**)
(***********)

 IF NOT OpenTerminal(ADR("Terminal")) THEN HALT END;

CLOSE

 CloseTerminal;

END ANSITerm.
