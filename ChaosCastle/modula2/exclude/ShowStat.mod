MODULE ShowStat;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM ExecD IMPORT SignalSemaphore, SignalSemaphorePtr;
 FROM ExecL IMPORT FindSemaphore, ObtainSemaphoreShared, ReleaseSemaphore;
 FROM Terminal IMPORT WriteString, WriteLn;
 FROM Memory IMPORT StrPtr, CopyStr;

 TYPE
  MySemaphore = RECORD
   ss: SignalSemaphore;
   stat: StrPtr;
  END;

 VAR
  s: POINTER TO MySemaphore;
  str: ARRAY[0..59] OF CHAR;

BEGIN

 s:= ADDRESS(FindSemaphore(ADR("SDebug")));
 IF s <> NIL THEN
  ObtainSemaphoreShared(ADR(s^.ss));
  CopyStr(s^.stat, ADR(str), 59);
  ReleaseSemaphore(ADR(s^.ss));
  WriteString(str)
 ELSE
  WriteString("'SDebug': Semaphore not found")
 END;
 WriteLn;

END ShowStat.
