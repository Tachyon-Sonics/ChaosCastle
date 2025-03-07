IMPLEMENTATION MODULE ModuleLoader;

 FROM SYSTEM IMPORT ADR, ADDRESS, LONGSET, CAST, ASSEMBLE, REG, SETREG;
 FROM Memory IMPORT CARD8, CARD16, INT16, CARD32, INT32, StrPtr, TagItemPtr,
  List, Node, InitList, Next, First, Tail, ListPtr, CopyStr, StrLength,
  AddTail, Empty, Remove;
 FROM AmigaBase IMPORT Globals, GlobalsPtr, globals;
 FROM Checks IMPORT CheckMem, AddTermProc;
 FROM DosD IMPORT noFreeStore, objectInUse, dirNotFound, objectNotFound,
  Process, ProcessPtr, ctrlC, sharedLock;
 FROM DosL IMPORT LoadSeg, UnLoadSeg, CreateProc, IoErr, Lock, UnLock;
 FROM ExecD IMPORT MsgPort, MsgPortPtr, Message, MessagePtr, Task, TaskPtr,
  MemReqs, MemReqSet, SignalSemaphore, SignalSemaphorePtr, NodeType;
 FROM ExecL IMPORT WaitPort, GetMsg, PutMsg, ReplyMsg, AllocMem, FreeMem,
  RemTask, Forbid, Permit, Signal, ObtainSemaphore, ReleaseSemaphore,
  AddSemaphore, RemSemaphore, FindSemaphore, InitSemaphore, Enqueue, execBase;
 FROM ExecSupport IMPORT CreatePort, DeletePort;
 FROM WorkbenchD IMPORT WBStartup, WBArg;
 IMPORT R;


 TYPE
  ModuleSemaphore = RECORD
   ss: SignalSemaphore;
   segList: ADDRESS;
   count: CARD16;
  END;
  ModuleSemaphorePtr = POINTER TO ModuleSemaphore;
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
  Module = RECORD
   data: ModuleData;
   wbMessage: WBStartup;
   wbArg: WBArg;
   finish: MessagePtr;
   token: ProcEntryPtr;
   mp: MsgPortPtr;
   pname: StrPtr;
   ms: ModuleSemaphorePtr;
  END;
  ModulePtr = POINTER TO Module;
  CProc = RECORD
   proc: Proc;
   rA4: LONGINT;
  END;
  CProcPtr = POINTER TO CProc;

 VAR
  myListPtr: ListPtr;


 PROCEDURE LoadModule(name: StrPtr): ModulePtr;
  VAR
   mname: ARRAY[0..49] OF CHAR;
   msg: ModuleMessagePtr;
   m: ModulePtr;
   p: MsgPortPtr;
   c: CARD16;
   allocated: BOOLEAN;
 BEGIN
  lmErr:= NoMemory;
  allocated:= FALSE;
  m:= AllocMem(SIZE(Module), MemReqSet{});
  IF m <> NIL THEN
   WITH m^ DO
    Forbid;
    ms:= ADDRESS(FindSemaphore(name));
    IF ms = NIL THEN
     ms:= AllocMem(SIZE(ModuleSemaphore), MemReqSet{public, memClear});
     IF ms <> NIL THEN
      allocated:= TRUE;
      IF execBase^.libNode.version < 36 THEN
       ms^.ss.link.type:= signalSem;
       InitSemaphore(ADR(ms^.ss));
       Enqueue(ADR(execBase^.semaphoreList), ADR(ms^.ss))
      ELSE
       AddSemaphore(ADR(ms^.ss))
      END
     END
    END;
    IF ms <> NIL THEN INC(ms^.count) END;
    Permit;
    IF ms <> NIL THEN
     ObtainSemaphore(ADR(ms^.ss));
     IF ms^.segList = NIL THEN
      ms^.segList:= LoadSeg(name)
     END;
     IF ms^.segList <> NIL THEN
      CopyStr(name, ADR(mname), 46);
      c:= StrLength(ADR(mname));
      mname[c]:= "."; INC(c);
      IF ms^.count >= 676 THEN mname[c]:= CHR(97 + ms^.count DIV 676); INC(c) END;
      IF ms^.count >= 26 THEN mname[c]:= CHR(97 + (ms^.count MOD 676) DIV 26); INC(c) END;
      mname[c]:= CHR(97 + ms^.count MOD 26); INC(c);
      mname[c]:= 0C; INC(c);
      ReleaseSemaphore(ADR(ms^.ss));
      pname:= AllocMem(c, MemReqSet{public});
      IF pname <> NIL THEN
       CopyStr(ADR(mname), pname, c);
       mp:= CreatePort(pname, 0);
       IF mp <> NIL THEN
        finish:= AllocMem(SIZE(Message), MemReqSet{public, memClear});
        IF finish <> NIL THEN
         p:= CreateProc(pname, 0, ms^.segList, 4096);
         IF p <> NIL THEN
           (* Send workbench startup message *)
          wbMessage.message.node.type:= unknown;
          wbMessage.message.node.pri:= 0;
          wbMessage.message.node.name:= NIL;
          wbMessage.message.replyPort:= mp;
          wbMessage.message.length:= SIZE(WBStartup);
          wbMessage.process:= p;
          wbMessage.segment:= ms^.segList;
          wbMessage.numArgs:= 1;
          wbMessage.toolWindow:= NIL;
          wbMessage.argList:= ADR(wbArg);
          wbArg.lock:= Lock(ADR(""), sharedLock);
          wbArg.name:= pname;
          PutMsg(p, ADR(wbMessage));
           (* Wait reply *)
          REPEAT
           WaitPort(mp);
           msg:= GetMsg(mp)
          UNTIL msg <> NIL;
          data:= msg^.data;
          msg^.parentListPtr:= myListPtr;
          msg^.finish:= finish;
          msg^.parentGlobals:= globals;
          IF data.task <> NIL THEN
           ReplyMsg(msg);
           token:= First(data.procList);
           lmErr:= LoadOk;
           RETURN m
          END;
          Forbid; RemTask(ADDRESS(p)); Permit
         END;
         FreeMem(finish, SIZE(Message))
        END;
        DeletePort(mp)
       END;
       FreeMem(pname, c)
      END;
      ObtainSemaphore(ADR(ms^.ss));
      Forbid;
      DEC(ms^.count);
      IF ms^.count = 0 THEN
       RemSemaphore(ADR(ms^.ss));
       Permit;
       ReleaseSemaphore(ADR(ms^.ss));
       UnLoadSeg(ms^.segList);
       FreeMem(ms, SIZE(ModuleSemaphore))
      ELSE
       Permit;
       ReleaseSemaphore(ADR(ms^.ss))
      END
     ELSE
      lmErr:= NotFound;
      ReleaseSemaphore(ADR(ms^.ss))
     END;
     IF allocated THEN
      RemSemaphore(ADR(ms^.ss));
      FreeMem(ms, SIZE(ModuleSemaphore))
     END
    END
   END;
   FreeMem(m, SIZE(Module))
  END;
  RETURN NIL
 END LoadModule;

 PROCEDURE ModuleVersion(m: ModulePtr): CARD32;
 BEGIN
  RETURN m^.data.version
 END ModuleVersion;

 PROCEDURE ProcVersion(m: ModulePtr; proc: ProcId): CARD32;
  VAR
   cur{R.D6}, tail{R.D7}: ProcEntryPtr;
 BEGIN
  WITH m^ DO
   cur:= First(data.procList);
   tail:= Tail(data.procList);
   LOOP
    IF cur = tail THEN RETURN 0 END;
    IF CAST(LONGCARD, cur^.id) = CAST(LONGCARD, proc) THEN RETURN cur^.version END;
    cur:= Next(cur^.node)
   END
  END
 END ProcVersion;

 PROCEDURE GetNextProcId(m: ModulePtr; VAR proc: ProcId): BOOLEAN;
 BEGIN
  WITH m^ DO
   IF token = Tail(data.procList) THEN
    token:= First(data.procList);
    RETURN FALSE
   ELSE
    proc:= token^.id;
    token:= Next(token^.node);
    RETURN TRUE
   END
  END
 END GetNextProcId;

 PROCEDURE CallProc(m: ModulePtr; proc: ProcId; params: TagItemPtr);
  VAR
   cur{R.D6}, tail{R.D7}: ProcEntryPtr;
   oldA4{R.D5}: LONGINT;
 BEGIN
  WITH m^ DO
   cur:= First(data.procList);
   tail:= Tail(data.procList);
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
  END
 END CallProc;

 PROCEDURE GetProc(m: ModulePtr; proc: ProcId): ProcPtr;
  VAR
   cur{R.D6}, tail{R.D7}: ProcEntryPtr;
 BEGIN
  WITH m^ DO
   cur:= First(data.procList);
   tail:= Tail(data.procList);
   LOOP
    IF cur = tail THEN RETURN NIL END;
    IF CAST(LONGCARD, cur^.id) = CAST(LONGCARD, proc) THEN
     RETURN ADR(cur^.proc)
    END;
    cur:= Next(cur^.node)
   END
  END
 END GetProc;

 PROCEDURE Call(proc{R.A0}: ProcPtr; params{R.A1}: TagItemPtr);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVE.L	A4,-(A7)		(* Save our A4 *)
	MOVEA.L	CProc.rA4(A0),A4	(* Load his A4 *)
	MOVE.L	A1,-(A7)		(* Load params on the stack *)
	MOVEA.L	CProc.proc(A0),A0	(* Load proc *)
	JSR	(A0)			(* Call proc *)
	MOVEA.L	(A7)+,A4		(* Restore our A4 *)
	RTS				(* Result is still in D0 *)
  END)
 END Call;

 PROCEDURE Launch(proc: ProcPtr; params: TagItemPtr);
 BEGIN
  IGNORE Call(proc, params)
 END Launch;

 PROCEDURE FlushModule(VAR m: ModulePtr);
 BEGIN
  WITH m^ DO
   Signal(data.task, LONGSET{data.sigNum});
   REPEAT
    WaitPort(mp)
   UNTIL finish = GetMsg(mp);
   FreeMem(finish, SIZE(Message));
    (* Wait wb return message *)
   REPEAT
    WaitPort(mp)
   UNTIL GetMsg(mp) <> NIL;
   DeletePort(mp);
   FreeMem(pname, StrLength(pname) + 1);
(*   Forbid; RemTask(data.task); Permit; *)
   UnLock(wbArg.lock);
   ObtainSemaphore(ADR(ms^.ss));
   Forbid;
   DEC(ms^.count);
   IF ms^.count = 0 THEN
    RemSemaphore(ADR(ms^.ss));
    Permit;
    ReleaseSemaphore(ADR(ms^.ss));
    UnLoadSeg(ms^.segList);
    FreeMem(ms, SIZE(ModuleSemaphore))
   ELSE
    Permit;
    ReleaseSemaphore(ADR(ms^.ss))
   END
  END;
  FreeMem(m, SIZE(Module));
  m:= NIL
 END FlushModule;

 PROCEDURE AddProc(id: ProcId; proc: ADDRESS);
  VAR
   p{R.D7}: ProcEntryPtr;
 BEGIN
  p:= AllocMem(SIZE(ProcEntry), MemReqSet{public});
  CheckMem(p);
  p^.id:= id;
  p^.version:= 0;
  p^.proc:= proc;
  p^.rA4:= REG(R.A4);
  AddTail(myListPtr^, p^.node)
 END AddProc;

 PROCEDURE Close;
  VAR
   first{R.D7}: ProcEntryPtr;
 BEGIN
  IF myListPtr <> NIL THEN
   WHILE NOT Empty(myListPtr^) DO
    first:= First(myListPtr^);
    Remove(first^.node);
    FreeMem(first, SIZE(ProcEntry))
   END;
   FreeMem(myListPtr, SIZE(List));
   myListPtr:= NIL
  END
 END Close;

BEGIN

 AddTermProc(Close);
 myListPtr:= AllocMem(SIZE(List), MemReqSet{public});
 CheckMem(myListPtr);
 InitList(myListPtr^);

END ModuleLoader.
