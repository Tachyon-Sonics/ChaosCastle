IMPLEMENTATION MODULE SDebug;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM ExecD IMPORT SignalSemaphore, MemReqs, MemReqSet, ListPtr, signalSem,
  ExecBase, ExecBasePtr;
 FROM ExecL IMPORT FindSemaphore, RemSemaphore, ObtainSemaphore,
  ReleaseSemaphore, AllocMem, FreeMem, execBase, InitSemaphore, Forbid,
  Enqueue, Permit;

 TYPE
  MySemaphore = RECORD
   ss: SignalSemaphore;
   stat: ADDRESS;
  END;

 VAR
  s: POINTER TO MySemaphore;

 PROCEDURE PutStat(str: ARRAY OF CHAR);
  (*$ CopyDyn:= FALSE *)
 BEGIN
  IF s <> NIL THEN
   ObtainSemaphore(ADR(s^.ss));
   s^.stat:= ADR(str);
   ReleaseSemaphore(ADR(s^.ss))
  END
 END PutStat;

BEGIN

 s:= AllocMem(SIZE(MySemaphore), MemReqSet{public, memClear});
 IF s = NIL THEN HALT END;
 WITH s^.ss.link DO pri:= 0; name:= ADR("SDebug"); type:= signalSem END;
 InitSemaphore(ADR(s^.ss));
 Forbid;
 Enqueue(ADR(execBase^.semaphoreList), s);
 Permit;

CLOSE

 IF s <> NIL THEN
  RemSemaphore(ADR(s^.ss));
  FreeMem(s, SIZE(MySemaphore));
  s:= NIL
 END;

END SDebug.
