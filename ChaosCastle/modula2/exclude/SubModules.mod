IMPLEMENTATION MODULE SubModules;

 FROM SYSTEM IMPORT ADR, ADDRESS, LONGSET, CAST, ASSEMBLE, REG, SETREG;
 FROM Memory IMPORT CARD8, CARD32, List, InitList, First, Tail, Remove, Next,
  AddTail, Empty, TagItem, TagItemPtr, Node, ListPtr, INT32, multiThread;
 FROM AmigaBase IMPORT Globals, GlobalsPtr, globals;
 FROM ExecD IMPORT MsgPort, MsgPortPtr, Message, MessagePtr, Task, TaskPtr,
  MemReqs, MemReqSet;
 FROM ExecL IMPORT FindPort, PutMsg, GetMsg, WaitPort, FindTask, Wait,
  Forbid, Permit, AllocMem, FreeMem, AllocSignal, FreeSignal;
 FROM ExecSupport IMPORT CreatePort, DeletePort;
 FROM DosD IMPORT ctrlC;
 FROM Checks IMPORT CheckMem, Check, AddTermProc;
 IMPORT R;


 TYPE
  Proc = PROCEDURE(TagItemPtr);
  ProcEntry = RECORD
   node: Node;
   proc: Proc;
   rA4: LONGINT;
   id: ProcId;
   version: CARD32;
  END;
  ProcEntryPtr = POINTER TO ProcEntry;
  ModuleData = RECORD
   procList: List;
   version: CARD32;
   task: TaskPtr;
   sigNum: CARD8;
  END;
  ModuleMessage = RECORD
   message: Message;
   data: ModuleData;
   parentListPtr: ListPtr;
   finish: MessagePtr;
   parentGlobals: GlobalsPtr;
  END;
  ModuleMessagePtr = POINTER TO ModuleMessage;
  CProc = RECORD
   proc: Proc;
   rA4: LONGINT;
  END;
  CProcPtr = POINTER TO CProc;

 VAR
  msg: ModuleMessagePtr;
  mp, rp: MsgPortPtr;
  oldGlobals: GlobalsPtr;


 PROCEDURE SetModuleVersion(version: CARD32);
 BEGIN
  msg^.data.version:= version
 END SetModuleVersion;

 PROCEDURE InstallNextProc(id: ProcId; version: CARD32; proc: ADDRESS);
  VAR
   p{R.A3}: ProcEntryPtr;
 BEGIN
  p:= AllocMem(SIZE(ProcEntry), MemReqSet{public});
  CheckMem(p);
  p^.id:= id;
  p^.version:= version;
  p^.proc:= proc;
  p^.rA4:= REG(R.A4);
  AddTail(msg^.data.procList, p^.node)
 END InstallNextProc;

 PROCEDURE FreeProcList;
  VAR
   first{R.A3}: ProcEntryPtr;
 BEGIN
  WHILE NOT Empty(msg^.data.procList) DO
   first:= First(msg^.data.procList);
   Remove(first^.node);
   FreeMem(first, SIZE(ProcEntry))
  END
 END FreeProcList;

 PROCEDURE Close;
  VAR
   finish{R.D7}: MessagePtr;
 BEGIN
  finish:= NIL;
  IF rp <> NIL THEN
   DeletePort(rp); rp:= NIL
  END;
  IF msg <> NIL THEN
   finish:= msg^.finish;
   IF msg^.data.sigNum >= 0 THEN
    FreeSignal(msg^.data.sigNum)
   END;
   FreeMem(msg, SIZE(ModuleMessage));
   msg:= NIL
  END;
  IF (finish <> NIL) AND (mp <> NIL) THEN
   PutMsg(mp, finish)
  END
 END Close;

 PROCEDURE ProcsInstalled;
 BEGIN
  PutMsg(mp, msg);
  REPEAT
   WaitPort(rp);
  UNTIL GetMsg(rp) = msg;
  oldGlobals:= globals;
  globals:= msg^.parentGlobals;
  multiThread:= globals^.multiThread;
  WITH msg^.data DO
   REPEAT (* Sleep *)
   UNTIL sigNum IN Wait(LONGSET{sigNum})
  END;
  globals:= oldGlobals;
  FreeProcList
   (* Cleanup is done by Close *)
 END ProcsInstalled;

 PROCEDURE GetProc(proc: ProcId): ProcPtr;
  VAR
   cur{R.D6}, tail{R.D7}: ProcEntryPtr;
   callProc{R.D5}: CProcPtr;
 BEGIN
  cur:= First(msg^.data.procList);
  tail:= Tail(msg^.data.procList);
  LOOP
   IF cur = tail THEN HALT END;
   IF CAST(LONGCARD, cur^.id) = CAST(LONGCARD, proc) THEN
    RETURN ADR(cur^.proc)
   END;
   cur:= Next(cur^.node)
  END
 END GetProc;

 PROCEDURE CallParentProc(proc: ProcId; params: TagItemPtr);
  VAR
   cur{R.D6}, tail{R.D7}: ProcEntryPtr;
   oldA4{R.D5}: LONGINT;
 BEGIN
  cur:= First(msg^.parentListPtr^);
  tail:= Tail(msg^.parentListPtr^);
  LOOP
   IF cur = tail THEN HALT END;
   IF CAST(LONGCARD, cur^.id) = CAST(LONGCARD, proc) THEN
    oldA4:= REG(R.A4);
    SETREG(R.A4, cur^.rA4);
    cur^.proc(params);
    SETREG(R.A4, oldA4);
    RETURN
   END;
   cur:= Next(cur^.node)
  END
 END CallParentProc;

 PROCEDURE GetParentProc(proc: ProcId): ProcPtr;
  VAR
   cur{R.D6}, tail{R.D7}: ProcEntryPtr;
 BEGIN
  cur:= First(msg^.parentListPtr^);
  tail:= Tail(msg^.parentListPtr^);
  LOOP
   IF cur = tail THEN HALT END;
   IF CAST(LONGCARD, cur^.id) = CAST(LONGCARD, proc) THEN
    RETURN ADR(cur^.proc)
   END;
   cur:= Next(cur^.node)
  END
 END GetParentProc;

 PROCEDURE CallParent(proc{R.A0}: ProcPtr; params{R.A1}: TagItemPtr);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
  	MOVE.L	A0,D0
  	BEQ	nilp
	MOVE.L	A4,-(A7)		(* Save our A4 *)
	MOVEA.L	CProc.rA4(A0),A4	(* Load his A4 *)
	MOVE.L	A1,-(A7)		(* Load params on the stack *)
	MOVEA.L	CProc.proc(A0),A0	(* Load proc *)
	JSR	(A0)			(* Call proc *)
	MOVEA.L	(A7)+,A4		(* Restore our A4 *)
nilp:	RTS
  END)
 END CallParent;

BEGIN

 AddTermProc(Close);
 msg:= AllocMem(SIZE(ModuleMessage), MemReqSet{public, memClear});
 CheckMem(msg);
 WITH msg^ DO
  message.length:= SIZE(ModuleData);
  data.task:= FindTask(NIL);
  data.sigNum:= AllocSignal(-1);
  Check(data.sigNum < 0, ADR("AllocSignal() failed."), NIL);
  InitList(data.procList)
 END;
 rp:= CreatePort(NIL, 0);
 CheckMem(rp);
 msg^.message.replyPort:= rp;
 mp:= FindPort(msg^.data.task^.node.name);
 Check(mp = NIL, ADR("Could not find parent"), ADR("message port"));

END SubModules.
