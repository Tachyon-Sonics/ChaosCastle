IMPLEMENTATION MODULE GrotteStdIOLib;

(*$ LargeVars:=FALSE StackParms:=FALSE Volatile:=FALSE CopyDyn:= FALSE
    OverflowChk:= FALSE *)

(*$
     StackChk:=FALSE RangeChk:=FALSE OverflowChk:=FALSE
     NilChk:=FALSE CaseChk:=FALSE NameChk:= FALSE  ReturnChk:= FALSE
     EntryClear:= FALSE
 *)

FROM SYSTEM IMPORT ADR, ADDRESS, ASSEMBLE, CAST, REG, SETREG, SHIFT, TAG, LONGSET;
FROM Arts IMPORT dosCmdBuf, dosCmdLen, Terminate;
FROM ExecD IMPORT Library, LibFlags, MemReqs, MemReqSet, Task, TaskPtr,
 Message, MessagePtr, MsgPort, MsgPortPtr;
FROM ExecL IMPORT AllocMem, Remove, FreeMem, GetMsg, WaitPort, ReplyMsg,
 FindTask, Forbid, Permit, CloseLibrary, Wait, Signal, PutMsg;
FROM ExecSupport IMPORT CreatePort, DeletePort;
FROM DosD IMPORT FileHandlePtr, ctrlF;
FROM IconL IMPORT GetDiskObject, FindToolType, FreeDiskObject;
FROM WorkbenchD IMPORT DiskObjectPtr;
IMPORT DosD, DosL;
IMPORT R;

CONST
  revision = 2;

TYPE
 Line = ARRAY[0..79] OF CHAR;
 LinePtr = POINTER TO Line;
 UserPtr = POINTER TO User;
 User = RECORD
  next: UserPtr;
  task: TaskPtr;
  X, Y: SHORTCARD;
  ragain: BOOLEAN;
  oldch: CHAR;
  readPort: MsgPortPtr;
  readProcess: TaskPtr;
  buffer: ARRAY[0..1023] OF CHAR;
  t: ARRAY[0..23] OF LinePtr;
  line: ARRAY[0..23] OF Line;
  input, output: FileHandlePtr;
  bpos: CARDINAL;
  colnum: SHORTCARD;
  fast, color, eof: BOOLEAN;
 END;
 TerminalPtr = UserPtr;
 Terminal = User;


VAR
  (*$ LongAlign:=TRUE *)
  TR: GrotteStdIOBasePtr;

(*######################################################################*)
CONST
  delOrd = ORD(delExp);

PROCEDURE LibOpen(myLib{R.A6}: GrotteStdIOBasePtr):ADDRESS;
(*$ EntryExitCode:=FALSE *)
BEGIN
  ASSEMBLE(
	ADDQ.W  #1,Library.openCnt(A6)
	BCLR    #delOrd,Library.flags(A6)
	MOVE.L  A6,D0
	RTS
  END);
END LibOpen;

PROCEDURE LibClose(myLib{R.A6}: GrotteStdIOBasePtr): ADDRESS;
(*$ EntryExitCode:=FALSE *)
BEGIN
  ASSEMBLE(
	MOVEQ   #0,D0
	SUBQ.W  #1,Library.openCnt(A6)
	BNE.S   noExp
	BTST    #delOrd,Library.flags(A6)
	BEQ     noExp
	BSR.S   LibExpunge
noExp:
	RTS
  END);
END LibClose;

PROCEDURE LibExpunge(myLib{R.A6}: GrotteStdIOBasePtr): ADDRESS;
(*$ EntryExitCode:=FALSE *)
VAR exec[4]:ADDRESS;
BEGIN
  ASSEMBLE(
	XREF    _LinkerDB
	TST.W   Library.openCnt(A6)
	BEQ.S   canExp
	BSET    #delOrd,Library.flags(A6)
	MOVEQ   #0,D0
	RTS
canExp: MOVEM.L A4-A6,-(A7)
	LEA     _LinkerDB,A4
	MOVEA.L A6,A5
	MOVEA.L A5,A1
	MOVEA.L exec,A6
	JSR     Remove(A6)
	JSR     Terminate(PC)
	MOVEA.L A5,A1
	MOVEQ   #0,D0
	MOVE.W  Library.negSize(A5),D0
	SUBA.L  D0,A1
	ADD.W   Library.posSize(A5),D0
	JSR     FreeMem(A6)
	MOVE.L  dosCmdBuf(A4),D0
	MOVEM.L (A7)+,A4-A6
	RTS
  END);
END LibExpunge;

PROCEDURE LibExtFunc(myLib{R.A6}: GrotteStdIOBasePtr): ADDRESS;
(*$ EntryExitCode:=FALSE *)
BEGIN
  ASSEMBLE(
	MOVEQ   #0,D0
	RTS
  END);
END LibExtFunc;

(*######################################################################*)

PROCEDURE IFlush(term{R.D4}: TerminalPtr);
BEGIN
 WITH term^ DO
  IF bpos <> 0 THEN
   IGNORE DosL.Write(output, ADR(buffer), bpos);
   bpos:= 0
  END
 END
END IFlush;

PROCEDURE Send(term{R.D2}: TerminalPtr; ch{R.D3}: CHAR);
BEGIN
 IF ch = "±" THEN ch:= "ñ" END;
 WITH term^ DO
  IF bpos > 1023 THEN IFlush(term) END;
  buffer[bpos]:= ch; INC(bpos)
 END
END Send;

PROCEDURE SendCard(term{R.D4}: TerminalPtr; c: SHORTCARD);
BEGIN
 IF c > 9 THEN
  Send(term, CHR(48 + c DIV 10));
  Send(term, CHR(48 + c MOD 10))
 ELSIF c > 1 THEN
  Send(term, CHR(48 + c))
 END
END SendCard;

PROCEDURE SS(term{R.D4}: TerminalPtr; s: ARRAY OF CHAR);
 VAR c{R.D5}: CARDINAL;
(*$ CopyDyn:= FALSE *)
BEGIN
 FOR c:= 0 TO HIGH(s) DO Send(term, s[c]) END
END SS;

PROCEDURE user(): TerminalPtr;
 (*$ EntryExitCode:= FALSE *)
 VAR exec[4]: ADDRESS;
BEGIN
 ASSEMBLE(
	MOVE.L  A6,-(A7)
	MOVE.L  exec,A6
	JSR     Forbid(A6)
	SUBA.L  A1,A1
	JSR     FindTask(A6)
	MOVEA.L TR(A4),A1
	MOVEA.L 34(A1),A1
	BRA     L000002
L000001:
	MOVEA.L (A1),A1
L000002:
	CMP.L   4(A1),D0
	BNE     L000001
	JSR     Permit(A6)
	MOVE.L  A1,D0
	MOVE.L  (A7)+,A6
	RTS
 END)
END user;

PROCEDURE ReadProcess;
 VAR
  term{R.D5}: TerminalPtr;
  inch: CHAR;
  msg{R.D4}: MessagePtr;
 (*$ LoadA4:= TRUE *)
BEGIN
 IGNORE Wait(LONGSET{ctrlF});
 term:= FindTask(NIL)^.userData;
 WITH term^ DO
  REPEAT
   IF DosL.Read(input, ADR(inch), 1) < 1 THEN inch:= 3C END;
   IF (inch = 3C) OR (inch = 34C) THEN eof:= TRUE; inch:= 3C END;
   msg:= AllocMem(SIZE(Message), MemReqSet{public, memClear});
   IF msg <> NIL THEN
    msg^.length:= ORD(inch);
    PutMsg(readPort, msg)
   END
  UNTIL eof;
  readProcess:= NIL
 END
END ReadProcess;

PROCEDURE WaitChar(VAR ch: CHAR);
 (* attend l'echo d'un charactère. Ne renvoie jamais 0C *)
 (*$ LoadA4:= TRUE *)
 VAR
  term{R.D7}: TerminalPtr;
  msg{R.D2}: MessagePtr;
BEGIN
 term:= user();
 IFlush(term);
 WITH term^ DO
  IF eof THEN
   ch:= 3C
  ELSE
   WaitPort(readPort);
   msg:= GetMsg(readPort);
   ch:= CHR(msg^.length);
   FreeMem(msg, SIZE(Message))
  END
 END
END WaitChar;

PROCEDURE OpenTerminal(userName: ADDRESS): BOOLEAN;
 (*$ LoadA4:= TRUE *)
 TYPE STR = POINTER TO ARRAY[0..79] OF CHAR;
 VAR term: TerminalPtr;
     l: SHORTCARD;
     ch: CHAR;
     tagl: ARRAY[0..7] OF LONGCARD;
     diskObject: DiskObjectPtr;
     name: ARRAY[0..31] OF CHAR;
     string: STR;
BEGIN
 IGNORE DosL.GetProgramName(ADR(name), 32);
 string:= NIL;
 term:= AllocMem(SIZE(Terminal), MemReqSet{public, memClear});
 IF term <> NIL THEN
  WITH term^ DO
   task:= FindTask(NIL);
   FOR l:= 0 TO 23 DO
    t[l]:= ADR(line[l])
   END;
   next:= TR^.firstUser;
   TR^.firstUser:= term;
   colnum:= 1; bpos:= 0;
   ragain:= FALSE; X:= 0; Y:= 0;
   eof:= FALSE;
   input:= DosL.Input();
   output:= DosL.Output();
   IF (input <> NIL) AND (output <> NIL) THEN
    readPort:= CreatePort(NIL, 0);
    IF readPort <> NIL THEN
     readProcess:= CAST(TaskPtr, DosL.CreateNewProc(TAG(tagl,
      DosD.npEntry, ADR(ReadProcess),
      DosD.npPriority, FindTask(NIL)^.node.pri + 1,
      DosD.npName, ADR("stdio.readproc"), 0, 0)));
     IF readProcess <> NIL THEN
      readProcess^.userData:= term;
      Signal(readProcess, LONGSET{ctrlF});
      fast:= FALSE;
      ClearScreen;
      diskObject:= GetDiskObject(DosL.FilePart(ADR(name)));
      IF diskObject <> NIL THEN
       string:= FindToolType(diskObject^.toolTypes, ADR("COLOR"))
      END;
      IF string = NIL THEN
       WriteString("Please choose mode for");
       WriteLn;
       l:= 0;
       WHILE (l < 80) AND (CAST(STR, userName)^[l] <> 0C) DO
       Write(CAST(STR, userName)^[l]); INC(l)
       END; WriteLn;
       WriteString(" [1]: Black & White"); WriteLn;
       WriteString(" [2]: Color"); WriteLn;
       WaitChar(ch);
       color:= (ch = "2");
       WriteLn
      ELSE
       color:= ((string^[0] = "Y") OR (string^[0] = "y"))
      END;
      IF diskObject <> NIL THEN
       string:= FindToolType(diskObject^.toolTypes, ADR("FASTMODE"))
      END;
      IF string = NIL THEN
       WriteString("Fast mode [Y/N] ?"); WriteLn;
       WriteString("(Warning: this mode does not work on all terminals)"); WriteLn;
       WaitChar(ch)
      ELSE
       ch:= string^[0]
      END;
      fast:= (ch = "Y") OR (ch = "y");
      IF diskObject <> NIL THEN
       FreeDiskObject(diskObject)
      END;
      ClearScreen;
      RETURN TRUE
     END;
     DeletePort(readPort)
    END
   END
  END;
  FreeMem(term, SIZE(Terminal));
 END;
 RETURN FALSE
END OpenTerminal;
(*$ POP EntryClear *)

PROCEDURE CloseTerminal;
 (*$ LoadA4:= TRUE *)
 VAR
  term{R.D2}, prev{R.D3}: TerminalPtr;
BEGIN
 term:= user();
 SS(term, 33C + "[0m");
 SS(term, 33C + "[1 pHH");
 SS(term, 15C + 12C + "<RETURN>");
 IFlush(term);
 WITH TR^ DO
  WITH term^ DO
   eof:= TRUE;
   REPEAT DosL.Delay(10) UNTIL readProcess = NIL;
   REPEAT UNTIL GetMsg(readPort) = NIL;
   DeletePort(readPort);
   Forbid;
   IF firstUser = term THEN firstUser:= next ELSE
    prev:= firstUser;
    WHILE prev^.next <> term DO prev:= prev^.next END;
    prev^.next:= next
   END;
   Permit
  END
 END
END CloseTerminal;

PROCEDURE Goto(x, y: SHORTINT);
 (*$ LoadA4:= TRUE *)
 VAR nx, ny, mv, gt, dx, dy: SHORTCARD;
     term{R.D5}: TerminalPtr;
BEGIN
 term:= user();
 WITH term^ DO
  IF bpos > 1000 THEN IFlush(term) END; (* Don't cut a goto *)
  IF x >= 0 THEN nx:= x ELSE nx:= X END;
  IF y >= 0 THEN ny:= y ELSE ny:= Y END;
  IF nx > 79 THEN nx:= 79 END;
  IF ny > 23 THEN ny:= 23 END;
  IF (nx = X) AND (ny = Y) THEN RETURN END;
  dx:= ABS(X - nx);
  IF dx > 9 THEN mv:= 5 ELSIF dx > 4 THEN mv:= 4 ELSE
   IF ((nx < X) AND fast) OR ((nx > X) AND NOT(color)) THEN mv:= dx ELSE
    IF dx > 1 THEN mv:= 4 ELSIF dx = 1 THEN mv:= 3 ELSE mv:= 0 END
   END
  END;
  dy:= ABS(Y - ny);
  IF dy > 9 THEN INC(mv, 5) ELSIF dy > 4 THEN INC(mv, 4) ELSE
   IF (ny > Y) AND fast THEN INC(mv, dy) ELSE
    IF dy > 1 THEN INC(mv, 4) ELSIF dy = 1 THEN INC(mv, 3) END
   END
  END;
  IF nx > 8 THEN gt:= 7 ELSE gt:= 6 END;
  IF ny > 8 THEN INC(gt) END;
  IF nx = 0 THEN DEC(gt) END;
  IF ny = 0 THEN DEC(gt) END;
  IF (nx = 0) AND (ny = 0) THEN DEC(gt) END;
  IF (gt <= mv) OR ((bpos = 0) AND NOT(fast)) THEN
 (* Goto *)
   X:= nx; Y:= ny;
   Send(term, 33C); Send(term, "[");
   IF (X <> 0) OR (Y <> 0) THEN
    SendCard(term, Y + 1);
    Send(term, 73C);
    SendCard(term, X + 1);
   END;
   Send(term, 110C)
  ELSE
 (* Move *)
   IF X <= nx THEN
    IF ((dx > 3) OR color) AND (dx <> 0) THEN
     Send(term, 33C); Send(term, "[");
     SendCard(term, dx);
     Send(term, 103C)
    ELSE
     WHILE X < nx DO Send(term, t[Y]^[X]); INC(X) END
    END
   ELSE
    IF (NOT(fast) OR (dx > 3)) AND (dx <> 0) THEN
     Send(term, 33C); Send(term, "[");
     SendCard(term, dx);
     Send(term, 104C)
    ELSE
     WHILE X > nx DO Send(term, 10C); DEC(X) END
    END
   END;
   IF Y < ny THEN
    IF (NOT(fast) OR (dy > 3)) AND (dy <> 0) THEN
     Send(term, 33C); Send(term, "[");
     SendCard(term, dy);
     Send(term, 102C)
    ELSE
     WHILE Y < ny DO Send(term, 12C); INC(Y) END
    END
   ELSE
    IF dy > 0 THEN
     Send(term, 33C); Send(term, "[");
     SendCard(term, dy);
     Send(term, 101C)
    END
   END;
   X:= nx; Y:= ny
  END
 END
END Goto;

PROCEDURE ClearScreen;
 (*$ LoadA4:= TRUE *)
 VAR l, c: CARDINAL;
     term{R.D5}: TerminalPtr;
BEGIN
 term:= user();
 WITH term^ DO
  FOR l:= 0 TO 23 DO
   FOR c:= 0 TO 79 DO
    t[l]^[c]:= " "
   END
  END;
  SS(term, 33C + "[0 pHH");
  Reset;
  Send(term, 33C);
  Send(term, "[");
  Send(term, CHR(04AH));
  X:= 0; Y:= 0
 END
END ClearScreen;

PROCEDURE Read(VAR ch: CHAR);
 (* renvoie 0C si aucun caractère *)
 (*$ LoadA4:= TRUE *)
 VAR
  term{R.D5}: TerminalPtr;
  msg{R.D6}: MessagePtr;
BEGIN
 term:= user();
 WITH term^ DO
  IF eof THEN
   ch:= 3C
  ELSIF ragain THEN
   ch:= oldch;
   ragain:= FALSE
  ELSE
   msg:= GetMsg(readPort);
   IF msg <> NIL THEN
    ch:= CHR(msg^.length);
    FreeMem(msg, SIZE(Message));
   ELSE
    ch:= 0C
   END;
   oldch:= ch
  END
 END
END Read;

PROCEDURE ReadAgain;
 (*$ LoadA4:= TRUE *)
BEGIN
 user()^.ragain:= TRUE
END ReadAgain;

PROCEDURE Report(x, y: SHORTCARD): CHAR;
 (*$ LoadA4:= TRUE *)
BEGIN
 IF (x < 80) AND (y < 24) THEN
  RETURN user()^.t[y]^[x]
 ELSE
  RETURN 0C
 END
END Report;

PROCEDURE Write(ch: CHAR);
 (*$ LoadA4:= TRUE *)
 VAR term{R.D5}: TerminalPtr;
BEGIN
 term:= user();
 WITH term^ DO
  t[Y]^[X]:= ch;
  Send(term, ch);
  INC(X);
  IF X > 79 THEN Goto(X - 1, -1) END
 END
END Write;

PROCEDURE WriteString(st: ARRAY OF CHAR);
 (*$ LoadA4:= TRUE *)
 VAR c{R.D2}: CARDINAL;
     term{R.D3}: TerminalPtr;
 (*$ CopyDyn:= FALSE *)
BEGIN
 c:= 0;
 term:= user();
 WITH term^ DO
  WHILE (c <= CARDINAL(HIGH(st))) AND (st[c] <> 0C) DO
   t[Y]^[X + c]:= st[c]; INC(c)
  END;
  SS(term, st);
  INC(X, c)
 END
END WriteString;

PROCEDURE WriteLn;
 (*$ LoadA4:= TRUE *)
BEGIN
 WITH user()^ DO
  IF Y = 23 THEN
   Scroll(0, -1); Goto(0, 23)
  ELSE
   Goto(0, Y + 1)
  END
 END
END WriteLn;

PROCEDURE WriteAt(x, y: SHORTCARD; ch: CHAR);
 (*$ LoadA4:= TRUE *)
BEGIN
 Goto(x, y);
 Write(ch)
END WriteAt;

PROCEDURE MoveChar(sx, sy: SHORTCARD; sch: CHAR; dx, dy: SHORTCARD; dch: CHAR);
 (*$ LoadA4:= TRUE *)
BEGIN
 IF (dx = sx) AND (dy = sy) THEN
  WriteAt(dx, dy, dch); RETURN
 END;
 IF (dx > sx) OR ((dx = sx) AND (dy > sy)) THEN
  WriteAt(sx, sy, sch);
  WriteAt(dx, dy, dch)
 ELSE
  WriteAt(dx, dy, dch);
  WriteAt(sx, sy, sch)
 END
END MoveChar;

PROCEDURE ReadString(VAR st: ARRAY OF CHAR);
 (*$ LoadA4:= TRUE *)
 VAR pos{R.D2}: CARDINAL;
     ch: CHAR;
     ox, oy: INTEGER;
     term{R.D4}: TerminalPtr;
BEGIN
 term:= user();
 WITH term^ DO
  pos:= 0;
  LOOP
   ox:= X; oy:= Y; Goto(0, 0);
   WaitChar(ch);
   Goto(ox, oy);
   IF (ch = 12C) OR (ch = 15C) OR (ch = 3C) THEN
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

PROCEDURE Ghost(x, y: SHORTCARD; ch: CHAR);
 (*$ LoadA4:= TRUE *)
 VAR term{R.D5}: TerminalPtr;
BEGIN
 term:= user();
 WITH term^ DO
  Goto(x, y);
  Send(term, ch);
  INC(X);
  IF X > 79 THEN Goto(X - 1, -1) END
 END
END Ghost;

PROCEDURE ClearLine(y: SHORTCARD);
 (*$ LoadA4:= TRUE *)
 VAR c, ox{R.D2}, oy{R.D3}: SHORTCARD;
     term{R.D5}: TerminalPtr;
BEGIN
 IF y >= 24 THEN RETURN END;
 term:= user();
 WITH term^ DO
  ox:= X; oy:= Y;
  Goto(0, y);
  SS(term, 33C + "[" + 113C);
  Goto(ox, oy);
  FOR c:= 0 TO 79 DO t[y]^[c]:= " " END
 END
END ClearLine;

PROCEDURE Rubout(n: SHORTCARD);
 (*$ LoadA4:= TRUE *)
 VAR c: SHORTCARD;
     term{R.D4}: TerminalPtr;
BEGIN
 term:= user();
 WITH term^ DO
  FOR c:= X TO 79 - n DO t[Y]^[c]:= t[Y]^[c + n] END;
  FOR c:= 80 - n TO 79 DO t[Y]^[c]:= " " END;
  Send(term, 33C); Send(term, "[");
  SendCard(term, n);
  Send(term, 120C)
 END
END Rubout;

PROCEDURE Delete(n: SHORTCARD);
 (*$ LoadA4:= TRUE *)
BEGIN
 WITH user()^ DO
  IF n > X THEN Goto(0, Y) ELSE Goto(X - n, Y) END;
 END;
 Rubout(n)
END Delete;

PROCEDURE Insert(n: SHORTCARD);
 (*$ LoadA4:= TRUE *)
 VAR v, c: SHORTCARD;
     term{R.D5}: TerminalPtr;
BEGIN
 term:= user();
 WITH term^ DO
  IF X + n > 79 THEN v:= 80 - X ELSE v:= n END;
  FOR c:= 79 TO X + v BY -1 DO
   t[Y]^[c]:= t[Y]^[c - v]
  END;
  FOR c:= X TO X + v - 1 DO t[Y]^[c]:= " " END;
  Send(term, 33C); Send(term, "[");
  SendCard(term, n);
  Send(term, 100C)
 END
END Insert;

PROCEDURE Scroll(dx, dy: SHORTINT);
 (*$ LoadA4:= TRUE *)
 VAR c, l, ox, oy: SHORTINT;
     term{R.D5}: TerminalPtr;
BEGIN
 term:= user();
 WITH term^ DO
  ox:= X; oy:= Y;
  IF dy < 0 THEN
   Send(term, 33C); Send(term, "[");
   SendCard(term, -dy);
   Send(term, 123C);
   FOR l:= 0 TO 23 + dy DO
    t[l]^:= t[l - dy]^
   END;
   FOR l:= 24 + dy TO 23 DO
    FOR c:= 0 TO 79 DO t[l]^[c]:= " " END
   END
  ELSIF dy > 0 THEN
   Send(term, 33C); Send(term, "[");
   SendCard(term, dy);
   Send(term, 124C);
   FOR l:= 23 TO dy BY -1 DO
    t[l]^:= t[l - dy]^
   END;
   FOR l:= 0 TO dy - 1 DO
    FOR c:= 0 TO 79 DO t[l]^[c]:= " " END
   END
  END;
  IF dx < 0 THEN
   FOR l:= 0 TO 23 DO
    Goto(0, l); Rubout(-dx)
   END
  ELSIF dx > 0 THEN
   FOR l:= 0 TO 23 DO
    Goto(0, l); Insert(dx)
   END
  END;
  Goto(ox, oy)
 END
END Scroll;

PROCEDURE Color(c: SHORTCARD);
 (*$ LoadA4:= TRUE *)
 VAR term{R.D5}: TerminalPtr;
BEGIN
 term:= user();
 WITH term^ DO
  IF color AND (colnum <> c) THEN
   Send(term, 33C); Send(term, "[");
   SendCard(term, c + 30);
   Send(term, 155C);
   colnum:= c
  END
 END
END Color;

PROCEDURE RedrawAll;
 (*$ LoadA4:= TRUE *)
 VAR l{R.D2}, ox{R.D3}, oy: SHORTCARD;
     term{R.D5}: TerminalPtr;
BEGIN
 term:= user();
 WITH term^ DO
  ox:= X; oy:= Y;
  FOR l:= 0 TO 23 DO
   Goto(0, l);
   SS(term, t[l]^)
  END;
  Goto(ox, oy)
 END
END RedrawAll;

PROCEDURE SetMode(cursor, beep, busy: BOOLEAN);
(*$ LoadA4:= TRUE *)
BEGIN
END SetMode;

PROCEDURE Flush;
 (*$ LoadA4:= TRUE *)
BEGIN
 IFlush(user())
END Flush;

PROCEDURE Reset;
 (*$ LoadA4:= TRUE *)
 VAR term{R.D5}: TerminalPtr;
BEGIN
 term:= user();
 WITH term^ DO
  SS(term, 33C + "[20l"); (* LF <> newline *)
  SS(term, 33C + "[1;49m");  (* bold, backcolor = default *)
  Send(term, 33C); Send(term, "[");
  Send(term, 110C);
  X:= 0; Y:= 0
 END
END Reset;

(***********)
(**)BEGIN(**)
(***********)

 TR:= CAST(GrotteStdIOBasePtr, dosCmdLen);
 TR^.magicCode:= 62077E02H;

END GrotteStdIOLib.mod
