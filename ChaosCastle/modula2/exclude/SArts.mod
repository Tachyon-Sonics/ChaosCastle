	(*                _____________________________________________
 *         //    /                                             \         //
 *        //     \          Amiga Run Time System              /        //
 *    \\ //      /           4.22 / 01.04.94 / bp              \    \\ //
 *     \X/       \_____________________________________________/     \X/
 *
 *   default options for Arts:
 *)


(*$ LargeVars:=FALSE
    StackChk:=FALSE
    OverflowChk:=FALSE
    RangeChk:=FALSE
    ReturnChk:=FALSE
    NilChk:=FALSE
    LongAlign:=FALSE
    Volatile:=FALSE
    StackParms:=FALSE
 *)


(*$ DEFINE Resident:=FALSE *) (* TRUE: Resident Startup *)
(*$ DEFINE Mini:=FALSE *)     (* TRUE: ohne TrapHandler, errorFr *)
(*$ DEFINE English:=TRUE *)  (* FALSE: deutsch *)
(*$ DEFINE Debug:=FALSE *)    (* TRUE: Messages an Debugger-Port *)
(*$ IF Debug Mini:=FALSE ENDIF *) (* Debugger braucht volle Info! *)

(*
 * 6.6.93/bp Bei OpenLib-Fail nun nur noch ok möglich!
 * 3.2.91/bp Angepaßt an neue Amiga-Module
 * 26.1.91/bp kickVersion wird nun aus exec.version gesetzt
 * 16.11.90 Am Schluß Debugger-Infos freigeben (via Message)
 * 13.11.90 Requester wertet pr^.windowPtr aus
 * 17.6.90 pr^.returnAdr richtig gesetzt, kein PanicClose mehr
 * 12.8.90 Exit() nun exportiert, kostet ja nichts.
 * bei wbStart NICHT 4(A7) als stackSize nehmen!
 *  Texte so gelegt, daß sie nur bei Bedarf gelinkt werden!
 * __Stack bei Resident: muß ein abs. Symbol sein (Linker), in
 * dem der Stackbedarf steht.
 * Der Process wird genauso verlassen, wie er vorgefunden wurde.
 * Vor Close wird nochmal spLower,spUpper gesetzt, falls Coroutines
 * aktiv war.
 * 18.3.94 Wenn reserved2 # NIL, dann ist es eine
 *   PROCEDURE(INTEGER):BOOLEAN, die der RT-Debugger gesetzt hat.
 *)

IMPLEMENTATION MODULE Arts;

FROM SYSTEM	IMPORT	ADDRESS,LONGSET,ADR,CAST,ASSEMBLE,REG,
			SETREG,SHIFT,SHORTSET;
IMPORT R;
FROM DosD	IMPORT	fail,FileLockPtr,Process,CommandLineInterface;
FROM DosL	IMPORT	CurrentDir; IMPORT DosL;
FROM ExecD	IMPORT	execBase,ExecBase,AttnFlags,AttnFlagSet,Task,
			Message,MsgPortPtr,MemReqSet,MemReqs,Library;
FROM ExecL	IMPORT	SetSignal,Forbid,Permit,OpenLibrary,
			CloseLibrary,GetMsg,WaitPort,ReplyMsg,
			AllocMem,FreeMem,FindTask;
(*$ IF Debug *)
FROM DebugDef	IMPORT	debPortName,magicDebug, magicOk,magicErr,
			magicForget,DebugMsg,DebugInfoPtr,DebugMsgPtr;
FROM ExecD	IMPORT	MessagePtr,NodeType,MsgPort,MsgPortAction;
FROM ExecL	IMPORT	FindPort,PutMsg,AllocSignal,FreeSignal;
(*$ ENDIF *)

(*$ IF m68010 *) Dieser Text führt absichtlich zu einem Syntax-Fehler!
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
!!  Arts darf niemals für 68010 oder höhere compiliert werden,    !!
!!  weil sonst endlose Requester-Schleife "Ungültige Instruktion",!!
!!  wenn das Programm nicht auf der richtigen CPU läuft!          !!
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	     Einzige Lösung wäre: ALLES in Assembler.
(*$ ENDIF *)

CONST
  vbit=2; (* Bitnummer des Overflow *)
  sb=R.A4; mp=R.A5; sp=R.A7;
  saveStack=800H;
  nilChk=45;
  longChk=46;
  longOvl=47;

TYPE
  Traps=(t0,t1,t2,adrErr,illIns,zeroDiv,chk,trapv,privileg);
  MsgTraps=[adrErr..MAX(Traps)];

  IntuiText=RECORD
    frontPen: CHAR;
    backPen: CHAR;
    drawMode: CHAR;
    leftEdge: INTEGER;
    topEdge: INTEGER;
    iTextFont: ADDRESS;
    iText: ADDRESS;
    nextText: ADDRESS;
  END;
  WBArg=RECORD
    lock: FileLockPtr;
    name: ADDRESS
  END;
  WBStartup=RECORD
    message: Message;
    process: MsgPortPtr;
    segment: ADDRESS;
    numArgs: LONGINT;
    toolWindow: ADDRESS;
    argList: POINTER TO WBArg
  END;

CONST
  minMsgTraps=ORD(MIN(MsgTraps));
  maxMsgTraps=ORD(MAX(MsgTraps));

VAR
  exec[4]: ADDRESS;
  stackTop: LONGINT; (* A7 on startup *)
  dosBase:ADDRESS;
  closeAll:PROC; (* CloseProc JMP(A0) für Terminate *)


(*$ IF NOT Mini *)
  oldTrapC,oldReturn: PROC;
(*$ ENDIF *)


  oldSpUpper, oldSpLower:ADDRESS;
  startSpUpper, startSpLower:ADDRESS;
  panicSp:ADDRESS; (* paras und name noch auf Stack, Exit() ebenso *)

(*$ IF Resident *)
  allocated: LONGINT; (* nur für Resident *)
(*$ ENDIF *)

(*$ IF Debug *)
  (* Das Register D7 als Parameter MUSS bleiben! *)
PROCEDURE Debug(magic{R.D7}:LONGINT);
FORWARD;

VAR
  debInfo:ADDRESS;
  closeModules: BOOLEAN; (* sh. reserved2 *)
(*$ ENDIF *)

(*$ IF NOT Mini *)
(* Weil Startup die allererste Prozedur sein muß, sind einige FORWARDs nötig. *)
PROCEDURE TrapHandler; FORWARD;
(*$ ENDIF *)

(*
 * Dies muß die allererste Prozedur sein!!!
 * Startup initialisiert alles.
 *)
(*$ EntryExitCode:=FALSE *)
PROCEDURE Startup(cl{0}:LONGINT; cb{8}: ADDRESS): LONGINT;
CONST
  memFlags=CAST(LONGINT,MemReqSet{public,memClear});
BEGIN
  ASSEMBLE(
	XREF	_LinkerDB,__main, __mainEnd
(* für RESIDENT brauchen wir mehr! *)
(*$ IF Resident *)
	XREF	_RESLEN,_RESBASE,_NEWDATAL,_STACK
(*$ ELSE *)
	XREF	__BSSBAS, __BSSLEN
(*$ ENDIF *)
(*$ IF Debug *)
	XREF	_DEBUG (* LinkerSymbol! *)
(*$ ENDIF *)

(*
 * Parameter merken, A4 und A6 laden.
 *)
 	MOVEM.L	D2-D7/A2-A6,-(A7)
	MOVE.L	A0,A2
	MOVE.L	D0,D2
	LEA	_LinkerDB,A4
	MOVEA.L	exec,A6
	SUBA.L	A1,A1
	JSR	FindTask(A6)
	MOVEA.L	D0,A3
(* D2/A2/A3/A4 belegt! *)
(*
 * Initialisierung Datenbereich und A4
 * NONresident Version:
 *)
(*$ IF NOT Resident *)
	LEA	__BSSBAS,A1
	MOVEQ	#0,D1
	MOVE.L	#__BSSLEN,D0
	BRA.S	clr_lp
clr_bss:MOVE.L	D1,(A1)+	(* BSS-Bereich löschen! Halbresident! *)
clr_lp:	DBRA	D0,clr_bss
	MOVE.L	A7,stackTop(A4)
	MOVE.L	Task.spLower(A3),A0 (* falls WB, schon mal laden *)
	MOVE.L	Process.cli(A3),D5
	BEQ.S	noResOk
	ASL.L	#2,D5
	MOVE.L	D5,A1
	MOVE.L	CommandLineInterface.defaultStack(A1),D4
	ASL.L	#2,D4	(* *4 = Bytes Stack *)
	MOVE.L	A7,A0
	SUB.L	D4,A0
noResOk:ADDA.W	#saveStack,A0
(* A3=Task, D5=APTR Cli A0=new spLower D2/A2=cliargs *)
(*
 * resident version
 * Alloziert den Speicher und initialisiert ihn.
 *)
(*$ ELSE *) (* resident *)
	MOVE.L	#_RESBASE,D6
	MOVE.L	#_RESLEN,D7
	(* Stacksize bestimmen CLI: cliDefaultStack, WB: A7-tcSPLower *)
	MOVE.L	Process.cli(A3),D5
	BEQ.S	fromwbRes
	ASL.L	#2,D5	(* BPTR! *)
	MOVE.L	D5,A0
	MOVE.L	CommandLineInterface.defaultStack(A0),D4
	ASL.L	#2,D4   (* Langworte*4 = Bytes *)
	BRA.S	Res2
fromwbRes:
	MOVE.L	A7,D4
	SUB.L	Task.spLower(A3),D4 (* circa Stackgröße *)

Res2:	(* D4=aktuelle Stackgröße D5=APTR CLI *)
	MOVEQ	#0,D3		(* D3=Flag 0:nicht MEHR alloziert *)
	MOVE.L	D7,D0	(* Gesamtlänge res. Daten *)
	MOVE.L	#_STACK,D1
	CMP.L	D1,D4	(* Daten noch nicht kopiert! *)
	BCC.S	StackOk		(* Stack reicht *)
	MOVE.L	D1,D4		(* minStack>istStack *)
	ADD.L	D4,D0		(* Gesamtlänge =reslen+_STACK *)
	MOVEQ	#1,D3		(* Flag setzen *)
StackOk:
	MOVE.L	#memFlags,D1
	JSR	AllocMem(A6)
	TST.L	D0
	BNE.S	MemOk
(* oh,oh, kein Memory für uns!! *)
	TST.L	D5		(* CLI vorhanden? *)
	BNE.S	errNoWB
	LEA	Process.msgPort(A3),A0
	JSR	WaitPort(A6)
	LEA	Process.msgPort(A3),A0
	JSR	GetMsg(A6)
	MOVE.L	D0,A2
	JSR	Forbid(A6)
	MOVE.L	A2,A1
	JSR	ReplyMsg(A6)
errNoWB:MOVEQ	#127,D0
	BRA	noMem	(* raus, fatal error *)

MemOk:
(* ok, mem bekommen,
 * D0=mem A3=task, D3=Flag, D5=APTR.cli D4=StackSize D2/A2=cliarg
 *)
	MOVE.L	D0,A0
	MOVE.L	D0,A5
	MOVE.L	D0,A1
	MOVE.L	#_NEWDATAL,D0 (* kann NIEMALS über 16K Langworte sein! *)
	SUB.L	D6,A4
	BRA.S	copylp
(* a4 zeigt auf Daten; kopiere in neuen Bereich *)
cpydta:	MOVE.L	(A4)+,(A0)+
copylp:	DBRA	D0,cpydta

(* a4 zeigt nun auf Datareloc-Count *)
	MOVE.L	(A4)+,D0 (* kann auch NIEMALS über 16K Langworte sein! *)
	BRA.S	rellp
relo:	MOVE.L	A1,A0	(* je eine Relokation ausführen *)
	ADD.L	(A4)+,A0
	ADD.L	(A0),A5
	MOVE.L	A5,(A0)
	MOVE.L	A1,A5
rellp:	DBRA	D0,relo

	MOVE.L	A1,A4
	ADDA.L	D6,A4 (* wieder +0 oder 32K *)
(* ok, alles kopiert, reloziert *)
	MOVE.L	D7,allocated(A4) (* erstmal _RESLEN *)
	MOVE.L	A7,stackTop(A4) (* bei Return wichtig, der alte Stack!! *)
	TST.B	D3
	BEQ.S	noNewStack
(* neuen Stack herstellen: *)
	MOVE.L	A4,D0
	SUB.L	D6,D0	(* -_RESBASE=Anfang des Bereichs *)
	ADD.L	D7,D0	(* +_RESLEN=Anfang des Stacks *)
	ADD.L	D4,D0		(* +_STACK = Ende des Stacks *)
	SUBQ.L	#8,D0		(* müßte als Reserve reichen! *)
	MOVE.L	D0,A7	(* NEUER Stack!! *)
	ADD.L	D4,allocated(A4) (* was wir nachher freigeben müssen! *)
noNewStack:
	MOVE.L	A7,A0
	SUBA.L	D4,A0		(* Start des Stacks *)
	ADDA.W	#saveStack,A0
(* A3=Task, D5=APTR Cli A0=new spLower D2/A2=cliargs *)
(*$ ENDIF *)

(*
 * Arts-Variablen belegen
 *)
	MOVE.L	A3,thisTask(A4)
 	MOVE.L	Task.spLower(A3),oldSpLower(A4)
 	MOVE.L	Task.spUpper(A3),oldSpUpper(A4)
(*$ IF NOT Mini *)
	MOVE.L	Task.trapCode(A3),oldTrapC(A4)
 	MOVE.L	Process.returnAddr(A3),oldReturn(A4)
(*$ ENDIF *)

	MOVE.L	Process.currentDir(A3),oldCurrentDir(A4)
	MOVE.W	Library.version(A6),kickVersion(A4)

(*
 * Prozess-Struktur patchen. Vorher nun alle Werte merken!
 * Eventuell ist dies das Problem mit WShell!
 *)
	MOVE.L	A0,Task.spLower(A3)
	MOVE.L	A7,Task.spUpper(A3)

	MOVE.L	A0,startSpLower(A4) (* nur für Coroutines! *)
	MOVE.L	A7,startSpUpper(A4) (* ist NICHT gleich stackTop! *)

(*$ IF NOT Mini *)
	LEA	TrapHandler(PC),A0
	MOVE.L	A0,Task.trapCode(A3)
(*$ ENDIF *)

(*
 * Evtl. vorhandende Break-Signale löschen
 *)
	MOVEQ	#0,D0
	MOVE.L	#$0000F000,D1
	JSR	SetSignal(A6)

(* Dos öffnen *)
	LEA	dosName(PC),A1	(* dosName nun intern, spart 1 Reloc32 *)
	MOVEQ	#0,D0
	JSR	OpenLibrary(A6)
	MOVE.L	D0,dosBase(A4)	(* Schwäche: geht davon aus, daß Dos da ist! *)

	TST.L	D5		(* APTR auf cli *)
	SEQ	wbStarted(A4)
	BEQ	fromWB

(*
 * fromCLI: Programmnamen auf den Stack kopieren
 * D2 = cmdLen, A2=pointer, A3 zeigt auf Prozess/Task
 * Programmnamen von BSTR zu Str auf Stack kopieren
 *)
 (*
  * 	CLR.L	wbStartup(A4) unnötig!
  *)
	MOVE.L	D5,A0
	MOVE.L	CommandLineInterface.commandName(A0),A0
	ADDA.L	A0,A0
	ADDA.L	A0,A0
	MOVEQ	#0,D0
	MOVE.B	(A0)+,D0	(* len BSTR *)
	MOVE.L	D0,D1
	ADDQ.L	#2,D0		(* gerade machen und +1 für 0 am Ende *)
	ANDI.W	#$FFFE,D0
	SUBA.L	D0,A7
	MOVE.L	A7,programName(A4)
	MOVE.L	A7,A1
namLp:	MOVE.B	(A0)+,(A1)+
	DBRA	D1,namLp
	CLR.B	-(A1)		(* 0C am Ende *)

(* Kommandozeile NICHT kopieren! Wofür??
	MOVE.L	D2,D1		(* cmdLen *)
	MOVE.L	D2,dosCmdLen(A4)
	ADDQ.L	#2,D2		(* gerade machen und +1 für 0 am Ende *)
	ANDI.W	#$FFFE,D2
	SUBA.L	D2,A7
	MOVE.L	A7,dosCmdBuf(A4)
	MOVE.L	A7,A1
cmdLp:	MOVE.B	(A2)+,(A1)+
	DBRA	D1,cmdLp
	CLR.B	-(A1)		(* 0C am Ende *)
*)
	MOVE.L	D2,dosCmdLen(A4)
	MOVE.L	A2,dosCmdBuf(A4)
	BRA	cliwb

(*
 * Start von Workbench. Setze CurrentDir und programName
 *)
fromWB:
	LEA	Process.msgPort(A3),A0
	JSR	WaitPort(A6)
	LEA	Process.msgPort(A3),A0
	JSR	GetMsg(A6)
	MOVE.L	D0,startupMsg(A4)
	MOVE.L	D0,A2
	MOVE.L	WBStartup.argList(A2),D0
	BEQ.S	noArgs		(* seltsamer Fall! *)
	MOVEA.L	D0,A0
	MOVE.L	WBArg.lock(A0),D1
	MOVE.L	WBArg.name(A0),programName(A4)
	MOVEA.L	dosBase(A4),A6
	JSR	CurrentDir(A6)
noArgs:

(*
 * Und wieder gemeinsamer Code:
 *)

cliwb:
(*$ IF Debug *)
	MOVE.L	#_DEBUG,debInfo(A4)
	ST	closeModules(A4)	(* __mainEnd aufrufen *)
(*$ ENDIF *)

(* Falls jemand Dos.Exit aufruft (WAS ER NIEMALS TUN SOLLTE!), wird sauber
 * nach Terminate verzweigt. Dos macht bei Exit(d0) folgendes:
 * MOVE.L pr_returnAddr(A?),Rx
 * SUBQ.L #4,Rx
 * MOVE.L Rx,A7
 * RTS
 *)
(*$ IF NOT Mini *)
	MOVE.L	A7,Process.returnAddr(A3)
	PEA	Exit(PC)	(* returnAddr ist ein StackPtr, keine Proc! *)
(*$ ENDIF *)


	LEA	goEnd(PC),A0
	MOVE.L	A0,closeAll(A4)
	MOVE.L	A7,panicSp(A4)
(*$ IF Debug *)
	SUBA.L	A5,A5 (* Ende der ProcChain! *)
(*$ ENDIF *)
(*
 * Und nun kommt das eigentliche Programm!
 *)
	JSR	__main(PC)
(*
 * JEDER Laufzeitfehler landet hier:
 *)
goEnd:
(*
 * Referenz an Coroutines usw. Damit kein ewiger Stack-Overflow! *)
	MOVE.L	thisTask(A4),A3
	MOVE.L	startSpLower(A4),Task.spLower(A3)
	MOVE.L	startSpUpper(A4),Task.spUpper(A3)

(* Schwäche: wenn Fehler in Close wird der Rest nicht geschlossen!
 * TermProcedures waren sicherer!
 * Der Openzähler wird ja erniedrigt, also wird Close für Implementations-
 * module nur EINMAL durchlaufen, das Close des MainMods kann aber MEHRMALS
 * aufgerufen werden, muß also sicherer programmiert sein!
 *)

 	MOVE.L	panicSp(A4),A7 (* Falls Stack Overflow auftrat! *)
 (*$ IF Debug *)
 	SUBA.L	A5,A5
 	TST.B	closeModules(A4) (* Debugger sagt: kein CLOSE *)
 	BEQ.S	noClose
 (*$ ENDIF *)
	JSR	__mainEnd(PC)
noClose:
	MOVE.L	stackTop(A4),A7 (* Auf jeden Fall mein Start-Stack *)

(*$ IF Debug *)
	MOVE.L	#magicForget,D7 (* Debugger Bescheid sagen! *)
	BSR	Debug
(*$ ENDIF *)
	MOVE.L	dosBase(A4),A6
	MOVE.L	oldCurrentDir(A4),D1
	JSR	CurrentDir(A6) (* Lieber so, als direkt zu setzen! *)
	MOVEA.L	exec,A6

(* Process/Task wiederherstellen *)
	MOVEA.L	thisTask(A4),A3
 	MOVE.L	oldSpLower(A4),Task.spLower(A3)
 	MOVE.L	oldSpUpper(A4),Task.spUpper(A3)
(*$ IF NOT Mini *)
	MOVE.L	oldTrapC(A4),Task.trapCode(A3)
 	MOVE.L	oldReturn(A4),Process.returnAddr(A3)
(*$ ENDIF *)


(* close Dos *)
	MOVE.L	dosBase(A4),A1
	JSR	CloseLibrary(A6)

	MOVE.L	startupMsg(A4),D2
	BEQ.S	noWbClose
(*
 * Workbench cleanup:
 *)
	JSR	Forbid(A6)
	MOVE.L	D2,A1
(*
 * returnVal.W in Message.length eintragen!
 *)
(*	MOVE.W	returnVal+2(A4),Message.length(A1) *)
	JSR	ReplyMsg(A6)
noWbClose:

(*$ IF NOT Resident *)
	MOVE.L	returnVal(A4),D0
(*$ ELSE *)
	MOVE.L	returnVal(A4),D2 (* Ist sonst gleich verschollen! *)
	MOVE.L	allocated(A4),D0
	MOVE.L	A4,A1
	SUB.L	#_RESBASE,A1
	JSR	FreeMem(A6)
	MOVE.L	D2,D0
noMem: (* wird direkt angesprungen, wenn kein Mem für resident *)
(*$ ENDIF *)
 	MOVEM.L	(A7)+,D2-D7/A2-A6
	RTS

 (* Wir setzen den Namen nun intern! *)
dosName:
	DC.B	'dos.library',0
	EVEN

	END);
END Startup;

(* Routinen ohne Import des Moduls *)
PROCEDURE CloseLibrary0(exec{14},n{9}: ADDRESS); CODE -414;
PROCEDURE OpenLibrary0(exec{14},n{9}:ADDRESS;v{0}:LONGINT):ADDRESS;CODE -552;
PROCEDURE SetExcept0(exec{14}: ADDRESS;n{0},msk{1}: LONGSET):LONGSET;CODE -312;
PROCEDURE AutoRequest0(intu{14},w{8},b{9},p{10},n{11},pf{0},nf{1}:ADDRESS;
                       w{2},h{3}: INTEGER): LONGINT; CODE -348;

(*$ IF Debug *)

PROCEDURE CreatePort(): MsgPortPtr;
CONST
  portSize=SIZE(MsgPort);
  ordMsgPort=ORD(msgPort);
  myReqs=CAST(LONGINT,MemReqSet{memClear,public});
  ordSignal=ORD(signal);

(*$ EntryExitCode:=FALSE *)
BEGIN
  ASSEMBLE(
	MOVEM.L	D7/A6,-(A7)
	MOVEA.L	exec,A6
	MOVEQ	#-1,D0
	JSR	AllocSignal(A6)
	TST.L	D0
	BLT.S	noSig
	MOVE.L	D0,D7
	MOVEQ	#portSize,D0
	MOVE.L	#myReqs,D1
	JSR	AllocMem(A6)
	TST.L	D0
	BEQ.S	noMem
	MOVE.L	D0,A0
	MOVE.B	#ordMsgPort,MsgPort.node.type(A0)
	MOVE.B	#ordSignal,MsgPort.flags(A0)
	MOVE.L	thisTask(A4),MsgPort.sigTask(A0)
	MOVE.B	D7,MsgPort.sigBit(A0)
	LEA	MsgPort.msgList(A0),A0
	MOVE.L	A0,(A0) (* Macro NewList *)
	ADDQ.L	#4,(A0)
	CLR.L	4(A0)
	MOVE.L	A0,8(A0)
	BRA.S	ready (* RETURN D0 = Port *)

 noMem:	MOVE.L	D7,D0
	JSR	FreeSignal(A6)
 noSig:	MOVEQ	#0,D0
 ready:	MOVEM.L	(A7)+,D7/A6
	RTS
  END);
(*
Exec ist geschlossen!!!! Deshalb ASSEMBLE!
  sig:=AllocSignal(-1);
  IF sig>=0 THEN
    port:=AllocMem(SIZE(MsgPort),MemReqSet{memClear,public});
    IF port#NIL THEN
      (*name:=NIL;*)
      (*pri:=0;*)
      port^.node.type:=msgPort;
      port^.flags:=signal;
      port^.sigTask:=thisTask;
      port^.sigBit:=sig;
      NewList(ADR(port^.msgList));
      RETURN port
    ELSE
      FreeSignal(sig)
    END
  END;
  RETURN NIL
*)
END CreatePort;

(* Wenn Exec nicht geöffnet ist, können wir sowieso einpacken! *)
(* Trotzdem müssen wir am Programmende hier durch! *)
PROCEDURE Debug(magic{R.D7}:LONGINT);
CONST
  msgSize=SIZE(DebugMsg);
  portSize=SIZE(MsgPort);
  ordMessage=ORD(message);
  myReqs=CAST(LONGINT,MemReqSet{public,memClear});
(*
VAR
  debPort{R.A0},
  myPort{R.A3}: MsgPortPtr;
  oldDebug{R.D5}:DebugInfoPtr;
  msg{R.A2},got:DebugMsgPtr;
  error{R.D6}:LONGINT;
*)
(*$ EntryExitCode:=FALSE *)
BEGIN
  ASSEMBLE(
	MOVEM.L	D5/D6/A2/A3/A6,-(A7)
	MOVE.L	debInfo(A4),D5
	BEQ	raus
	CLR.L	debInfo(A4)
	MOVEQ	#magicErr,D6
	MOVEA.L	exec,A6
	MOVEQ	#msgSize,D0
	MOVE.L	#myReqs,D1
	JSR	AllocMem(A6)
	TST.L	D0
	BEQ	noMsg

	MOVE.L	D0,A2
	BSR	CreatePort
	TST.L	D0
	BEQ.S	noReplyPort

	MOVE.L	D0,A3
	MOVE.B	#ordMessage,DebugMsg.msg.node.type(A2)
	MOVE.L	A3,DebugMsg.msg.replyPort(A2)
	MOVE.L	D7,DebugMsg.magic(A2)
	MOVE.L	thisTask(A4),DebugMsg.process(A2)
	MOVE.L	D5,DebugMsg.info(A2)
	LEA	errorFrame(A4),A0
	MOVE.L	A0,DebugMsg.frame(A2)
	JSR	Forbid(A6)
	LEA	debPortName(PC),A1 (* importiert von noImp!! *)
	JSR	FindPort(A6)
	TST.L	D0
	BEQ.S	noDebPort

	MOVEA.L	D0,A0
	MOVEA.L	A2,A1
	JSR	PutMsg(A6)
	JSR	Permit(A6)
  waitLp:
	MOVEA.L	A3,A0
	JSR	WaitPort(A6)
	MOVEA.L	A3,A0
	JSR	GetMsg(A6)
	CMP.L	A2,D0
	BNE.S	waitLp
	MOVE.L	DebugMsg.magic(A2),D6
	BRA.S	ok
  noDebPort:
	JSR	Permit(A6)
  ok:
	MOVEQ	#0,D0
	MOVE.B	MsgPort.sigBit(A3),D0
	JSR	FreeSignal(A6)
	MOVEA.L	A3,A1
	MOVEQ	#portSize,D0
	JSR	FreeMem(A6)
  noReplyPort:
	MOVEA.L	A2,A1
	MOVEQ	#msgSize,D0
	JSR	FreeMem(A6)
  noMsg:
	MOVEQ	#magicErr,D0
	CMP.L	D6,D0
	BEQ.S	raus
	MOVE.L	D5,debInfo(A4)
  raus:	MOVEM.L	(A7)+,D5/D6/A2/A3/A6
  	RTS
  END);
(*
BEGIN
  IF debInfo=NIL THEN RETURN END; (* nicht zweimal! *)
  oldDebug:=debInfo;
  debInfo:=NIL;
  error:=magicErr;
  msg:=AllocMem(SIZE(msg^),MemReqSet{memClear,public});
  IF msg#NIL THEN
    myPort:=CreatePort();
    IF myPort#NIL THEN
      msg^.msg.node.type:=message;
      msg^.msg.replyPort:=myPort;
      msg^.magic:=magic;
      msg^.process:=thisTask;
      msg^.info:=oldDebug;
      msg^.frame:=ADR(errorFrame);
      Forbid;
      debPort:=FindPort(ADR(debPortName));
      IF debPort#NIL THEN
	PutMsg(debPort,msg);
	Permit;
	REPEAT
	  WaitPort(myPort);
	  got:=GetMsg(myPort);
	UNTIL got=msg; (* wer weiß, was da kommt? *)
	error:=got^.magic;
      ELSE
	Permit;
      END;
      FreeSignal(myPort^.sigBit);
      FreeMem(myPort,SIZE(myPort^));
    END; (* myPort#NIL *)
    FreeMem(msg,SIZE(msg^));
  END; (* msg#NIL *)
  IF error#magicErr THEN
    debInfo:=oldDebug; (* alles ok: wieder einsetzen! *)
  END;
*)
END Debug;
(*$ ENDIF *)

(********************************************************************
 * the following procedures are used as base for of run time system *
 ********************************************************************
 * Requester displays a requester containing the
 * given text. if body or pos are NIL the size of the requester
 * is adjusted in its height.
 * If breakPoint is TRUE the text in
 * the "abort" gadget is changed to "continue". the program is not terminated
 * by this procedure. Try Terminate!
 *)
(*$ EntryExitCode:=FALSE *)
PROCEDURE InitIntuiText(VAR it{8}: IntuiText; left{0},top{1}: INTEGER;
			 font{9}, txt{2}: ADDRESS);
BEGIN
  ASSEMBLE(
	MOVE.L	#$00010100,(A0)+ (* front, back, drawmode *)
	MOVE.W	D0,(A0)+ (* leftEdge *)
	MOVE.W	D1,(A0)+ (* topEdge *)
	MOVE.L	A1,(A0)+ (* iTextFont *)
	MOVE.L	D2,(A0)+ (* iText *)
	CLR.L	(A0)+	(* nextText *)
	RTS
  END);
END InitIntuiText;

PROCEDURE Requester(header,body,pos,neg: ADDRESS): BOOLEAN;

   (*$ EntryExitCode:=FALSE *)
   PROCEDURE IntuiName;
   BEGIN
     ASSEMBLE( DC.B 'intuition.library',0 EVEN END);
   END IntuiName;

   (*$ EntryExitCode:= FALSE *)
   PROCEDURE TopazName;
   BEGIN
     ASSEMBLE( DC.B 'topaz.font',0 EVEN END);
   END TopazName;

TYPE TextAttr = RECORD
      name: ADDRESS;
      ySize: CARDINAL;
      style, flags: SHORTSET;
     END;

VAR
  pn, ht,bt,nt,pt: IntuiText;
  ta: TextAttr;
  posAdr: ADDRESS;
  intuition: ADDRESS;
  oldExcept: LONGSET;
  win:ADDRESS;
  reqRes: BOOLEAN;

BEGIN
  oldExcept:=SetExcept0(exec,LONGSET{},LONGSET{0..31}); (* alle aus *)
  intuition:=OpenLibrary0(exec,ADR(IntuiName),0);
  IF intuition#NIL THEN
    WITH ta DO
     name:=ADR(TopazName); ySize:= 8;
     style:= SHORTSET{}; flags:= SHORTSET{0};
    END;
    InitIntuiText(pn,12,5,ADR(ta),programName);
    InitIntuiText(ht,12,14,ADR(ta),header); pn.nextText:= ADR(ht);
    (* how many lines do you want? *)
    IF body#NIL THEN
      InitIntuiText(bt,12,23,ADR(ta),body); ht.nextText:=ADR(bt);
    END;
    InitIntuiText(nt,6,3,NIL,neg);
    IF pos=NIL THEN
      posAdr:=NIL
    ELSE
      InitIntuiText(pt,6,3,NIL,pos); posAdr:=ADR(pt)
    END;
    ASSEMBLE(
	MOVE.L	thisTask(A4),A0
	MOVEQ	#0,D0
	MOVE.L	Process.windowPtr(A0),D1
	BMI.S	negOr0
	MOVE.L	D1,D0
    negOr0:
	MOVE.L	D0,win(A5)
    END);
    reqRes:= AutoRequest0(intuition,win,ADR(pn),posAdr,ADR(nt),NIL,NIL,320,72)#0;
    CloseLibrary0(exec,intuition)
  END;
  SETREG(R.D0,SetExcept0(exec,oldExcept,LONGSET{0..31})); (* alte wieder *)
  RETURN reqRes
END Requester;

(* bei ~breakpoint: Terminate! *)
PROCEDURE ArtsRequest(h, b: ADDRESS; breakPoint: BOOLEAN);

  (*$ EntryExitCode:=FALSE *)
  PROCEDURE continue;
  BEGIN
    ASSEMBLE(
    (*$ IF English *)
	DC.B ' Continue ',0 EVEN
    (*$ ELSE *)
	DC.B ' weiter ',0 EVEN
    (*$ ENDIF *)
    END);
  END continue;

  (*$ EntryExitCode:=FALSE *)
  PROCEDURE abort;
  BEGIN
    ASSEMBLE(
    (*$ IF English *)
	DC.B ' Quit ',0 EVEN
    (*$ ELSE *)
	DC.B ' abbrechen ',0 EVEN
    (*$ ENDIF *)
    END);
  END abort;

(*$ IF Debug *)
  (*$ EntryExitCode:=FALSE *)
  PROCEDURE debug;
  BEGIN
    ASSEMBLE(
	DC.B ' debug ',0 EVEN
    END);
  END debug;

VAR
  neg{8+2}: ADDRESS;
BEGIN
  errorFrame.body:=b;
  errorFrame.header:=h;

  IF reserved2#NIL THEN (* Runtime-Debugger 18.3.94/bp *)
    ASSEMBLE(		(* PROCEDURE(BOOLEAN):BOOLEAN; *)
	CLR.W	-(A7)	(* momentan immer 0, Rest steht in errorFrame *)
	MOVEA.L	reserved2(A4),A0
	JSR	(A0)		(* kommt mit D0 zurueck, FALSE=no Close *)
	MOVE.B	D0,closeModules(A4)
    END);
  ELSE (* alter Debugger *)
    IF breakPoint THEN neg:=ADR(continue);
    ELSE neg:=ADR(abort);
    END;
    (* Der Benutzer hat nun die Chance, den Debugger zu starten. *)
    IF Requester(h,b,ADR(debug),neg) THEN
      Debug(magicDebug);
      IF breakPoint THEN
        IF Requester(h,b,ADR(abort),ADR(continue)) THEN
          Terminate
        END
      END
    END;
  END;
  IF ~breakPoint THEN Terminate END;

(*$ ELSE *) (* Debug *)

VAR
  pos{8+2}: ADDRESS;
  out{7}: POINTER TO CHAR;
  len{6}: LONGCARD;
BEGIN
  errorFrame.body:=b;
  errorFrame.header:=h;
  IF breakPoint THEN
    pos:=ADR(continue);
  ELSE
    pos:=NIL;
  END;
  IF ~Requester(h,b,pos,ADR(abort)) THEN
    out:= h; len:= 0;
    WHILE out^ <> 0C DO INC(out); INC(len) END;
    out:= ADDRESS(DosL.Output());
    IF (out <> NIL) AND (len > 0) THEN
     IGNORE DosL.Write(ADDRESS(out), h, len);
     IGNORE DosL.Write(ADDRESS(out), ADR(15C + 12C), 2)
    END;
    IF b <> NIL THEN
     out:= b; len:= 0;
     WHILE out^ <> 0C DO INC(out); INC(len) END;
     out:= ADDRESS(DosL.Output());
     IF (out <> NIL) AND (len > 0) THEN
      IGNORE DosL.Write(ADDRESS(out), b, len);
      IGNORE DosL.Write(ADDRESS(out), ADR(15C + 12C), 2)
     END
    END;
    Terminate
  END;
(*$ ENDIF *)
END ArtsRequest;

(*$ IF Debug *)
PROCEDURE FillErrorFrame(errType{0}: ErrorType);
(*$ EntryExitCode:=FALSE *)
BEGIN
  ASSEMBLE(
	MOVE.L	(A5),errorFrame.aRegs+5*4(A4) (* NICHT von ArtsProc! *)
	MOVE.L	A4,errorFrame.aRegs+4*4(A4)
	MOVE.L	4(A5),errorFrame.pc(A4)
	MOVE.B	D0,errorFrame.error(A4)
	RTS
   END);
END FillErrorFrame;
(*$ ENDIF *)

(*$ EntryExitCode:=FALSE *)
PROCEDURE HeaderText;
BEGIN
  ASSEMBLE(
  	(*$ IF English *)
	DC.B	"Program failed:",0 EVEN
	(*$ ELSE *)
	DC.B	"Modula-2 Laufzeitfehler",0 EVEN
	(*$ ENDIF *)
  END);
END HeaderText;

(*$ IF NOT Mini *)
(*
 * Trap Handlers for 68000/68010/68020/68030
 *)
(*$ EntryExitCode:=FALSE *)
PROCEDURE TrapStub;
(*VAR help:ARRAY[0..23] OF CHAR;*)
BEGIN
  ASSEMBLE(
	MOVEQ	#0,D0 (* gleich auf Langwort für divu *)
	MOVE.W	errorFrame.trapNr(A4),D0
	CMPI.W	#minMsgTraps,D0
	BLO.S	extra
	CMPI.W	#maxMsgTraps,D0
	BHI.S	extra
	SUBQ.W	#minMsgTraps,D0
	ADD.W	D0,D0
	LEA	trapTable(PC),A0
	ADDA.W	D0,A0
	ADDA.W	(A0),A0
	BRA.S	msgOk
extra:	LEA	trapnil(PC),A0
	CMPI.W	#nilChk,D0
	BEQ.S	msgOk
	LEA	trapchk(PC),A0
	CMPI.W	#longChk,D0
	BEQ.S	msgOk
	LEA	traptrapv(PC),A0
	CMPI.W	#longOvl,D0
	BEQ.S	msgOk
	LEA	trapFormat(PC),A1
	LEA	-20(A7),A7	(* Stack für Msg *)
	MOVE.L	A7,A0
lp:	MOVE.B	(A1)+,(A0)+
	BNE.S	lp
	SUBQ.L	#1,A0 (* hinter '000 *)
	MOVEQ	#2,D1 (* 3 Ziffern ausgeben *)
lp2:	DIVU.W	#10,D0
	SWAP	D0
	ADD.B	D0,-(A0)
	CLR.W	D0
	SWAP	D0
	DBRA	D1,lp2
	MOVE.L	A7,A0
msgOk:	MOVE.L	A0,errorFrame.body(A4)
	PEA	HeaderText(PC)
	MOVE.L	A0,-(A7)
	CLR.B	-(A7)
	BSR	ArtsRequest
(* kommt nie zurück, TROTZDEM BSR wg. Parametern!! *)

(* EVEN ist merkwürdigerweise NOTWENDIG! *)
(*$ IF English *)
trapadr:	DC.B 'Address Error',0 EVEN
trapill:	DC.B 'Illegal Instruction',0 EVEN
trapzero:	DC.B 'Division by Zero',0 EVEN
trapchk:	DC.B 'Range Error',0 EVEN
traptrapv:	DC.B 'Overflow',0 EVEN
trappriv:	DC.B 'Privilege Violation',0 EVEN

trapnil:	DC.B 'NIL Address',0 EVEN
(*$ ELSE *)
trapadr:	DC.B 'Adressfehler',0 EVEN
trapill:	DC.B 'Ungültige Instruktion',0 EVEN
trapzero:	DC.B 'Division durch 0',0 EVEN
trapchk:	DC.B 'Bereichsfehler (CHK)',0 EVEN
traptrapv:	DC.B 'Überlauf (TRAPV od. cpTRAPcc)',0 EVEN
trappriv:	DC.B 'Privilegverletzung',0 EVEN

trapnil:	DC.B 'Zeiger ist NIL (Trap 13)',0 EVEN
traplongchk:	DC.B 'Bereichsfehler (TRAP 14)',0 EVEN
traplongovfl:	DC.B 'Überlauf (TRAP 15)',0 EVEN
(*$ ENDIF *)
(* Diesen String NICHT ändern!!!!!!!!~~~~" *)
trapFormat:	DC.B "Processor Trap #000",0 EVEN

trapTable:
	DC.W trapadr-*,trapill-*,trapzero-*,trapchk-*,traptrapv-*,trappriv-*
  END);
(*
 (* We are a normal procedure, no problems except there is NO return
  * address on the stack. Create text and call requester, terminate.
  *)
 WITH errorFrame DO
  IF (INTEGER(MIN(MsgTraps))<=trapNr)
   & (trapNr<=INTEGER(MAX(MsgTraps))) THEN
   body:=trapText[MsgTraps(trapNr)]
  ELSIF trapNr=longChk THEN body:=longChkText
  ELSIF trapNr=longOvl THEN body:=longOvlText
  ELSIF trapNr=nilChk  THEN body:=nilChkText
  ELSE
   body:=ADR(trapFormat)
  END
 END;
 ArtsRequest(ADR(HeaderText),errorFrame.body,FALSE);
 (* Terminate; kann entfallen, da ArtsRequest hier Terminate macht! *)
*)
(*
 * NEVER do a RTS in this procedure!
 *)
END TrapStub;

(*
 * This is the trap handler called from exec for this task. it copies all the
 * required values from the superuser stack to errorFrame and then patches
 * the return address and finally does return from exception (rte).
 * this should work with all 680x0 processors known at this moment.
 * It is tested with vanilla 68000/68010/68020. If you have another processor
 * you're welcome to check this trap code.
 *
 * 30.4.90/bp ich habe hier noch ALLE Register, also in Aregs und Dregs
 * retten!
 * Fehler bei >68000 Busfehler etc. Es muß jeweils das Format-Word
 * gepatcht werden, sonst macht der Prozessor weiter und hängt sich
 * auf!!
 * 68030 seite 8-24 ff
 * 68000:
 * sr, pc,pc
 * fc, adr,adr, bef, sr, pc,pc (bei bus/address-fehler)
 * 68010 etc:
 * format 0:
 * sr, pc,pc, formo
 * format 7 (68040, access error )
 * sr, pc,pc, formo, 26 weitere worte
 * format 8:
 * sr, pc,pc, formo, 25 weitere worte
 * format 9:
 * sr, pc,pc,formo, 6 weitere worte
 * format 2:
 * sr, pc,pc, formo, 2 worte
 * format 1: wichtig: ist throwaway isp, anderer muß auch gepatcht werden
 * sr, pc,ps, formo und was dann? ist msp initialisiert?
 * hier macht er 2*RTE, nachdem er das SR geladen hat!! Also 2*patchen!
 * format 10:
 * sr, pc,pc, formo, 12 worte
 * format 11:
 * sr, pc,pc, formo, 42 worte
 * format 3 (68040, fp post instruction)
 * wie #2
 *)
PROCEDURE TrapHandler; (* installed in our Task structure *)
(* Achtung: verändert Register! D6-D7/A2 *)
TYPE
  ADDRESSPtr=POINTER TO ADDRESS;
  SFrame= RECORD
    sr: CARDINAL;
    ip: ADDRESS;
    form: CARDINAL;
  END;
  SFramePtr = POINTER TO SFrame;
VAR
  sf{9}: SFramePtr;
  p{8}:ADDRESSPtr;
  oldSR{0},fNr{1}: CARDINAL;
BEGIN
(*$ IF Debug *)
  ASSEMBLE(
 	MOVE.L	A4,-(A7)
	LEA	errorFrame.dRegs(A4),A4
	MOVEM.L	D0-D7/A0-A7,(A4) (* a4,a5 werden gleich richtig gesetzt! *)
	MOVEA.L	(A7)+,A4 (* A4 wieder richtig! *)
  END);
(*$ ENDIF *)
(* our stack looks now like this (each line represents a WORD):
 * HIGH:| oldPC   |
 *      | oldPC   |  (normal 680x0 error frame)
 *      | SR      |
 *       | IR      |
 *       | cyclAdr | (extended frame: only pushed if BUS or ADDRESS error)
 *       | cyclAdr |
 *       | access  |
 *      | trapNr  |  (pushed by ROM)
 *      | trapNr  |
 *      | oldMP   |  (pushed by procedure entry)
 *      | oldMP   | <-- MP
 *      |  room   |
 * LOW: | for 'p' | <-- SP
 *)
  p:=CAST(ADDRESS,REG(mp)); (* get trap info *)
  errorFrame.aRegs[mp]:=p^; INC(CAST(ADDRESS,p),4);
  errorFrame.aRegs[sb]:=REG(sb);
  errorFrame.trapNr:=p^;
  errorFrame.error:=trap;
  (* 68000 has some pecularities *)
  (* 20.10.90/bp 68000 nur, wenn low byte attnflags = 0! *)
  IF execBase^.attnFlags*AttnFlagSet{m68010..af7}=AttnFlagSet{} THEN
    IF (errorFrame.trapNr<=3) THEN
      INC(CAST(ADDRESS,p),8) (* 0,1 = mc68000 BUS/ADDRESS Errors *)
    END;
    INC(CAST(ADDRESS,p),6);
    (* replace return address, no problems with priorities, stacks etc,
     * just RTE
     *)
    errorFrame.pc:=p^; p^:=ADR(TrapStub);
    DEC(p,2);
    SETREG(sp,CAST(ADDRESS,p));
    ASSEMBLE(RTE END)
  ELSE (* Nicht 68000. Kleinen StackFrame herstellen und RTE! *)
    (* sr holen *)
    INC(p,4); (* nun auf SR, pc,pc ... *)
    sf:=CAST(SFramePtr,p);
    errorFrame.pc:=sf^.ip;
    REPEAT (* nur für throwaway! *)
      oldSR:=sf^.sr;
      (* formatnr bestimmen *)
      fNr:=sf^.form DIV (256*16); (* höchste 4 bits *)
      (*IF fNr=0 THEN*) (* kurzer Frame, nichts tun *)
      IF fNr=1 THEN (* throwaway!!!!!! *)
      (* hier müßten wir auch den MSP-Frame patchen, je nach SR *)
      (* wenn aktSP#FrameSP dann sp umschalten! *)
      (* beide gleich: 2. frame direkt hierüber *)
      (* sonst: hier ISP, anderer auf MSP oder ISP! *)
      (* 20.10.90/bp Also, den throwaway habe ich auch nach x-maligem
         Lesen der diversen Beschreibungen nicht ganz verstanden!
         Normalerweise dürfte der Frame HIER gar nicht ankommen,
         da er nur bei einem Interrupt erzeugt wird.
         Ich glaube, diese Lösung ist nun perfekt:
       *)
        INC(sf,8); (* Frame abbauen *)
        ASSEMBLE(
          MOVE.L  A1,SP (* A1=sf, DIESEN Stack abbauen! *)
          MOVE    D0,SR (* D0=oldSR, schaltet nun ISP auf ISP/MSP um! *)
          MOVE.L  SP,A1 (* nun auf dem richtigen Frame *)
        END);
      ELSIF (fNr=2)OR(fNr=3) THEN INC(sf,2*2)
      ELSIF fNr=7 THEN INC(sf,26*2) (* nur 68040 access error *)
      ELSIF fNr=8 THEN INC(sf,25*2) (* nur 68010 bus/address *)
      ELSIF fNr=9 THEN INC(sf,6*2) (* 9,10,11 nur 68020/30 *)
      ELSIF fNr=10 THEN INC(sf,12*2)
      ELSIF fNr=11 THEN INC(sf,42*2)
      (* ELSE was dann?? müssen wir so lassen, gibt bei vielen evtl StackOvfl! *)
      END;
    UNTIL fNr#1;
    sf^.sr:=oldSR;
    sf^.ip:=ADR(TrapStub);
    sf^.form:=0; (* format 0, rest egal *)
  END;
  SETREG(sp,sf);
  ASSEMBLE(RTE END);
END TrapHandler;
(*$ ENDIF *)


(*
 * relacement for Dos.Exit's return Call
 *)
(*$ EntryExitCode:=FALSE *)
PROCEDURE Exit(retVal{R.D0}:LONGINT);
BEGIN
  ASSEMBLE(
	MOVE.L	D0,returnVal(A4)
	BSR	Terminate
	END);
END Exit;

(*
 * Terminate immediatly terminates the program.
 *)
(*$ EntryExitCode:=FALSE *)
PROCEDURE Terminate;
BEGIN
  ASSEMBLE(
	MOVE.L	closeAll(A4),A0
	JMP	(A0)
	END);
END Terminate;

(*
 * Compiler Support
 * calls to these procedures are generated by the compiler.
 *
 * SystemError is used by runtime checks.
 * Diese Proc muß A5 linken, sonst errorFrame falsch!
 *)
PROCEDURE SystemError(err{0}: SysErr);
VAR
  msg: ADDRESS; (* locVar muß sein, sonst A5 nicht gelinkt! *)
BEGIN
  ASSEMBLE(
(*$ IF Debug *)
	MOVE.L	A4,-(A7)
	LEA	errorFrame.dRegs(A4),A4
	MOVEM.L	D0-D7/A0-A7,(A4)
	MOVEA.L	(A7)+,A4
(*$ ENDIF *)
	MOVE.B	D0,errorFrame.sysErr(A4)
	LEA	MsgHalt(PC),A0
	SUBQ.B	#1,D0
	BMI.S	ok
	LEA	MsgIllCase(PC),A0
	SUBQ.B	#1,D0
	BMI.S	ok
	LEA	MsgFctReturn(PC),A0
	SUBQ.B	#1,D0
	BMI.S	ok
	LEA	MsgStkOvl(PC),A0
	SUBQ.B	#1,D0
	BMI.S	ok
	LEA	MsgIllCall(PC),A0
  ok:	MOVE.L	A0,msg(A5)
	BRA.S	weiter
  (*$ IF English *)
MsgHalt:	DC.B	"Programmed HALT",0 EVEN
MsgIllCase:	DC.B	"Illegal CASE Index",0 EVEN
MsgFctReturn:	DC.B	"Function Return Error",0 EVEN
MsgStkOvl:	DC.B	"STACK OVERFLOW",0 EVEN
MsgIllCall:	DC.B	"Illegal Call",0 EVEN
  (*$ ELSE *)
MsgHalt:	DC.B	"Programmiertes HALT",0 EVEN
MsgIllCase:	DC.B	"Ungültiger CASE Index",0 EVEN
MsgFctReturn:	DC.B	"Funktion ohne RETURN beendet",0 EVEN
MsgStkOvl:	DC.B	"STAPEL ÜBERLAUF",0 EVEN
MsgIllCall:	DC.B	"Ungültiger Aufruf von Call",0 EVEN
  (*$ ENDIF *)

  weiter:
  END);

 (* fill crash info into errorFrame mp/sb/pc *)
(*$ IF Debug *)
 FillErrorFrame(system);
(*$ ENDIF *)
 returnVal:=fail;
 (* ArtRequest kommt nie zurueck, MUSS also aufgerufen werden!! *)
 ArtsRequest(ADR(HeaderText),msg,FALSE);

 ASSEMBLE(RTS END); (* löscht den Rest der Proc, da unnötig! *)
 (* Terminate; kann entfallen, da FALSE *)
END SystemError;

(*
 * StkChk kills the calling program if there is not enough stackspace left.
 *
 * 12.8.89/ms
 *   Es dürfen keine Register, ausser D0, verändert werden, da der Compiler
 *   diesen Prozedur Aufruf im Prozedur Eingangscode selbständig einfügt.
 * 10.7.89/ms
 *   Neuer StackCheck Code.
 *)
(*$ EntryExitCode:=FALSE *)
PROCEDURE StkChk(need{0}: LONGINT);
CONST stkOvlOrd=ORD(stkOvl);
BEGIN
 ASSEMBLE(
	ADD.L	SP,D0
	SUB.L	#20,D0
	MOVEM.L	A0-A1/A6/D1-D2,-(A7)
	MOVE.L	D0,D2
	MOVE.L	exec,A6
	SUBA.L	A1,A1
	JSR	FindTask(A6)
	MOVEA.L	D0,A0
	CMP.L	Task.spLower(A0),D2 (* 58 *)
	BLT.S	SystemErr
	CMP.L	Task.spUpper(A0),D2 (* 62 *)
	BLT.S	done
SystemErr:
	MOVEQ	#stkOvlOrd,D0
	BSR	SystemError
done:	MOVEM.L	(A7)+,A0-A1/A6/D1-D2
	RTS
(*	ADD.L	SP,D0
	MOVE.L	A0,-(A7)
	MOVE.L	exec,A0
	MOVE.L	ExecBase.thisTask(A0),A0
	CMP.L	Task.spLower(A0),D0
	BLT.S	SystemErr
	CMP.L	Task.spUpper(A0),D0
	BLT.S	done
SystemErr:
	MOVEQ	#stkOvlOrd,D0
	BSR	SystemError
done:	MOVE.L	(A7)+,A0
	RTS  *)
 END);
END StkChk;

PROCEDURE Assert(condition: BOOLEAN; msg: ADDRESS);

  (*$ EntryExitCode:=FALSE *)
  PROCEDURE assertTitle;
  BEGIN
    ASSEMBLE(
	DC.B 0 EVEN
    END);
  END assertTitle;

BEGIN
 IF ~condition THEN
   (*$ IF Debug *)
   FillErrorFrame(assertion);
   (*$ ENDIF *)
   ArtsRequest(msg,ADR(assertTitle),FALSE);
   (* Terminate; kann entfallen, da FALSE *)
 END
END Assert;

PROCEDURE BreakPoint(msg: ADDRESS);

  (*$ EntryExitCode:=FALSE *)
  PROCEDURE breakPointTitle;
  BEGIN
    ASSEMBLE(
	DC.B 'Modula-2 BreakPoint',0 EVEN
    END);
  END breakPointTitle;

BEGIN
  (*$ IF Debug *)
  FillErrorFrame(breakPoint);
  (*$ ENDIF *)
  ArtsRequest(ADR(breakPointTitle),msg,TRUE)
END BreakPoint;

PROCEDURE Error(header,body: ADDRESS);
BEGIN
  (*$ IF Debug *)
  FillErrorFrame(explicit);
  (*$ ENDIF *)
  ArtsRequest(header,body,FALSE);
  ASSEMBLE(RTS END); (* löscht den Rest der Proc, da unnötig! *)
  (* Terminate; kann entfallen, da FALSE *)
END Error;

(* 32-bit arithmetic subroutines for LONGINT and LONGCARD *)
CONST
  X=R.D0; Y=R.D1;

(*$ IF DEBUG *)
(*$ EntryExitCode:=FALSE *)
PROCEDURE Mulu32(x{X}, y{Y}: LONGINT): LONGINT;
(*
 * [A*hi + B]*[C*hi + D] = [A*C*hi^2 + (A*D + B*C)*hi + B*D]
 *)
(* CONST T1=d2; A=d3; C=d4;*)
BEGIN
  ASSEMBLE(
	MOVEM.L D2-D4,-(A7)
	MOVE.L  D0,D2
	MOVE.L  D0,D3
	MOVE.L  D1,D4
	SWAP    D3
	SWAP    D4
	MULU    D1,D0
	MULU    D3,D1
	MULU    D4,D2
	MULU    D4,D3
	SWAP    D0
	MOVEQ   #0,D4
	ADD.W   D1,D0
	ADDX.L  D4,D4
	ADD.W   D2,D0
	ADDX.L  D4,D3
	SWAP    D0
	CLR.W   D1
	CLR.W   D2
	SWAP    D1
	SWAP    D2
	ADD.L   D2,D1
	ADD.L   D3,D1
	BEQ.S   Mulu32a
	ORI     #vbit,CCR
  Mulu32a:
	MOVEM.L (A7)+,D2-D4
	RTS
  END);
END Mulu32;

(*$ EntryExitCode:=FALSE *)
PROCEDURE Muls32(x{X}, y{Y}: LONGINT): LONGINT;
(* CONST X1=d2; Y1=d3;*)
BEGIN
  ASSEMBLE(
	MOVEM.L D2-D3,-(A7)
	MOVE.L  D0,D2
	MOVE.L  D1,D3
	BSR.S   Mulu32
	TST.L   D2
	BPL.S   L000029
	SUB.L   D3,D1
  L000029:
	TST.L   D3
	BPL.S   L000030
	SUB.L   D2,D1
  L000030:
	TST.L   D0
	BPL.S   L000031
	NOT.L   D1
  L000031:
	TST.L   D1
	BEQ.S   L000032
	ORI     #vbit,CCR
  L000032:
	MOVEM.L (A7)+,D2-D3
	RTS
  END);
END Muls32;
(*$ ELSE *)
(*$ EntryExitCode:=FALSE *)
PROCEDURE Mulu32(x{X}, y{Y}: LONGINT): LONGINT;
(*
 * [A*hi + B]*[C*hi + D] = [A*C*hi^2 + (A*D + B*C)*hi + B*D]
 * No overflow checking !
 *)
(* CONST T1=d2; A=d3; C=d4;*)
BEGIN
  ASSEMBLE(
	MOVEM.L D2-D3,-(A7)
	MOVE.L	D1,D2
	MOVE.L	D0,D3
	SWAP	D2
	SWAP	D3
	MULU	D0,D2
	MULU	D1,D0
	MULU	D1,D3
	SWAP	D0
	ADD.W	D2,D3
	ADD.W	D3,D0
	SWAP	D0
	MOVEM.L	(A7)+,D2-D3
	RTS
  END);
END Mulu32;

(*$ EntryExitCode:=FALSE *)
PROCEDURE Muls32(x{X}, y{Y}: LONGINT): LONGINT;
(* CONST X1=d2; Y1=d3;*)
BEGIN
  ASSEMBLE(
  	BRA.S	Mulu32
  END);
END Muls32;
(*$ ENDIF *)

(*$ EntryExitCode:=FALSE *)
PROCEDURE Divu32(x{X}, y{Y}: LONGINT): LONGINT;
(*
 * [A*hi + B] DIV y = [(A DIV y)*hi + (A MOD y*hi + B) DIV y]
 *)
(* CONST QUO=d2; T1=d3;*)
BEGIN
  ASSEMBLE(
	MOVEM.L D2-D3,-(A7)
	MOVEQ   #0,D2
	CMP.L   #$0000FFFF,D1
	BHI.S   L000025
	DIVU    D1,D0
	BVC.S   L000024
	MOVE.W  D0,D3
	CLR.W   D0
	SWAP    D0
	DIVU    D1,D0
	MOVE.W  D0,D2
	MOVE.W  D3,D0
	SWAP    D2
	DIVU    D1,D0
  L000024:
	MOVE.W  D0,D2
	CLR.W   D0
	SWAP    D0
	BRA.S   L000028
  L000025:
	MOVE.W  D0,D2
	CLR.W   D0
	SWAP    D2
	SWAP    D0
	MOVEQ	#15,D3
  L000026:
	LSL.L   #1,D2
	ROXL.L  #1,D0
	CMP.L   D1,D0
	BCS.S   L000027
	SUB.L   D1,D0
	ADDQ.W  #1,D2
  L000027:
	DBRA    D3,L000026
  L000028:
	MOVE.L  D2,D1
	MOVEM.L (A7)+,D2-D3
	RTS		                 (* d0=REM, d1=QUO *)
  END);
END Divu32;

(*$ EntryExitCode:=FALSE *)
PROCEDURE Divs32(x{X}, y{Y}: LONGINT): LONGINT;
(* CONST sX=d2; sY=d3;*)
BEGIN
  ASSEMBLE(
	MOVEM.L D2-D3,-(A7)
	TST.L   D0
	SMI     D2
	BPL.S   L000033
	NEG.L   D0
  L000033:
	TST.L   D1
	SMI     D3
	BPL.S   L000034
	NEG.L   D1
  L000034:
	BSR.S   Divu32
	CMP.B   D2,D3	 (* adjust DIV *)
	BEQ.S   L000035
	NEG.L   D1
  L000035:
	TST.B   D2	 (* adjust MOD *)
	BEQ.S   L000036
	NEG.L   D0
  L000036:
	MOVEM.L (A7)+,D2-D3
	RTS
  END);
END Divs32;

(*$ EntryExitCode:=FALSE *)
PROCEDURE OpenLib(version{0}:LONGINT; name{9}:ADDRESS):ADDRESS;
BEGIN

  (*$ IF NOT Mini *)

  ASSEMBLE(
	MOVEM.L	D2/A2-A3/A6,-(A7)
	MOVE.L	A1,A2
	MOVE.L	D0,D2
LibOp:	MOVE.L  A2,A1
	MOVE.L	D2,D0
	MOVEA.L	exec,A6
	JSR	OpenLibrary(A6)
	TST.L	D0
	BNE.S	ok
	LEA	libOpenErr(PC),A0
	ADD.L	#22,A0
	MOVE.L	D2,D0
loopd:	DIVU.W	#10,D0
	SWAP	D0
	ADD.B	#48,D0
	MOVE.B	D0,-(A0)
	CLR.W	D0
	SWAP	D0
	TST.B	D0
	BNE	loopd
	PEA	libOpenErr(PC)
	MOVE.L	A2,-(A7)
	PEA	libRetry(PC)
	PEA	libCancel(PC)
	BSR	Requester
	TST.L	D0
	BNE	LibOp
	MOVEQ	#20,D0
	MOVE.L	D0,returnVal(A4)
	BRA	Terminate (* und weg *)
  ok:	MOVEM.L	(A7)+,D2/A2-A3/A6
	RTS
  libRetry: DC.B "Retry",0 EVEN
  libCancel: DC.B "Cancel",0 EVEN

  (*$ IF English *)
  libOpenErr: DC.B "Cannot open version    of",0 EVEN
  (*$ ELSE *)
  libOpenErr: DC.B "Fehler beim Öffnen V   von",0 EVEN
  (*$ ENDIF *)

  END);

  (*$ ELSE *) (* Mini: Kein Requester *)

  ASSEMBLE(
	MOVE.L	A6,-(A7)
	MOVEA.L	exec,A6
	JSR	OpenLibrary(A6)
	TST.L	D0
	BNE.S	ok
	MOVEQ	#20,D0
	MOVE.L	D0,returnVal(A4)
	BRA	Terminate (* und weg *)
  ok:	MOVE.L	(A7)+,A6
	RTS
  END);
  (*$ ENDIF *)
END OpenLib;

(*$ EntryExitCode:=FALSE *)
PROCEDURE CloseLib(base{9}:ADDRESS);
(* Wenn Fehler während der Openphase kann hier bei Close NIL kommen! *)
BEGIN
  ASSEMBLE(
	MOVE.L	A1,D0
	BEQ.S	nono
	MOVE.L	A6,-(A7)
	MOVEA.L	exec,A6
	JSR	CloseLibrary(A6)
	MOVE.L	(A7)+,A6
  nono:	RTS
	END);
END CloseLib;


(*$ IF NOT Debug *) (* Bei Debug wird m2d importiert! *)
(*$ EntryExitCode:=FALSE *)
BEGIN (* Arts tut nichts! *)
  ASSEMBLE(RTS END);
CLOSE
  ASSEMBLE(RTS END);
(*$ ENDIF *)
END Arts.mod
