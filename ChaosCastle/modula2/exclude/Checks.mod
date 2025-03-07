IMPLEMENTATION MODULE Checks;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM Arts IMPORT Requester, Error, programName;
 FROM ExecD IMPORT MemReqs, MemReqSet;
 FROM ExecL IMPORT AllocMem, FreeMem;
 FROM AmigaBase IMPORT memLow;
 FROM Memory IMPORT StrPtr, CopyStr, List, ListPtr, Node, NodePtr,
  AddHead, First, Tail, Next, Remove, InitList;
 IMPORT R, Arts;

 CONST
  Message = "Not enough memory";

 TYPE
  TermProcEntry = RECORD
   node: Node;
   proc: PROC;
  END;
  TermProcEntryPtr = POINTER TO TermProcEntry;

 VAR
  termProcs: List; (* TermProcEntry *)
  returnCode: CARDINAL;
  finalMsg1, finalMsg2: ARRAY[0..39] OF CHAR;
  fmsg, inited: BOOLEAN;

 PROCEDURE Ask(s1, s2, pos, neg: StrPtr): BOOLEAN;
 BEGIN
  RETURN Requester(s1, s2, pos, neg)
 END Ask;

 PROCEDURE Check(badCase: BOOLEAN; s1, s2: StrPtr);
 BEGIN
  CheckMemBool(memLow);
  IF badCase THEN
   returnCode:= 20;
   fmsg:= TRUE;
   CopyStr(s1, ADR(finalMsg1), SIZE(finalMsg1));
   IF s2 <> NIL THEN
    CopyStr(s2, ADR(finalMsg2), SIZE(finalMsg2))
   ELSE
    finalMsg2[0]:= 0C
   END;
   Terminate
  END
 END Check;

 PROCEDURE Warn(badCase: BOOLEAN; s1, s2: StrPtr);
 BEGIN
  IF badCase THEN
   IGNORE Ask(s1, s2, NIL, ADR("Ok"))
  END
 END Warn;

 PROCEDURE CheckMemBool(badCase: BOOLEAN);
 BEGIN
  IF badCase THEN returnCode:= 10; memLow:= TRUE; Terminate END
 END CheckMemBool;

 PROCEDURE CheckMem(adr: ADDRESS);
 BEGIN
  CheckMemBool(adr = NIL)
 END CheckMem;

 PROCEDURE AddTermProc(proc: PROC);
  VAR
   tpe{R.D7}: TermProcEntryPtr;
 BEGIN
  IF inited = FALSE THEN
   InitList(termProcs);
   inited:= TRUE
  END;
  tpe:= AllocMem(SIZE(TermProcEntry), MemReqSet{});
  CheckMem(tpe);
  tpe^.proc:= proc;
  AddHead(termProcs, tpe^.node)
 END AddTermProc;

 PROCEDURE Terminate;
 BEGIN
  Arts.Exit(returnCode)
 END Terminate;

 PROCEDURE CloseProcs;
  VAR
   cur{R.D7}, tail{R.D6}, next{R.D5}: TermProcEntryPtr;
   proc{R.D4}: PROC;
 BEGIN
  cur:= First(termProcs);
  tail:= Tail(termProcs);
  WHILE cur <> tail DO
   next:= Next(cur^.node);
   proc:= cur^.proc;
   Remove(cur^.node);
   FreeMem(cur, SIZE(TermProcEntry));
   proc;
   cur:= next
  END;
  IF memLow THEN
   memLow:= FALSE;
   Error(ADR(Message), NIL)
  ELSIF fmsg THEN
   fmsg:= FALSE;
   Error(ADR(finalMsg1), ADR(finalMsg2))
  END
 END CloseProcs;

BEGIN

CLOSE

 CloseProcs;

END Checks.
