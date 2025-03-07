IMPLEMENTATION MODULE Memory;
(*$ DEFINE Debug:=TRUE *)

 FROM SYSTEM IMPORT ADR, ADDRESS, CAST, SETREG, REG, SHIFT, WORD, ASSEMBLE;
 IMPORT ed: ExecD, el: ExecL;
 IMPORT Arts;
 IMPORT R;

 CONST
  SMSemaphoreName = "zFactory Memory";
  NumTagList = 32;

 TYPE
  TagList = ARRAY[0..17] OF ADDRESS;
  TagListPtr = POINTER TO TagList;
  TagListArr = ARRAY[0..NumTagList - 1] OF TagList;
  LockChunk = RECORD
   node: Node;
   base: ADDRESS;
   offset, length: CARD32;
   ss: ed.SignalSemaphore;
   owner: ed.TaskPtr;
   count: CARD16;
   write: BOOLEAN;
 (*$ IF DEBUG *)
   num: LONGCARD;
 (*$ ENDIF *)
  END;
  LockChunkPtr = POINTER TO LockChunk;
  VolatileChunk = RECORD
   node: Node;
   regA4: ADDRESS;
   Handler: MemHandler;
  END;
  VolatileChunkPtr = POINTER TO VolatileChunk;
  SMSemaphore = RECORD
   ss: ed.SignalSemaphore;
   lowMem: ed.SignalSemaphore; (* ~ all the memory *)
   lockList: List;
   listLen: CARD32;
(*$ IF Debug *)
   memList: List;
   allocCnt: LONGCARD;
   lockCnt: LONGCARD;
(*$ ENDIF *)
   count: CARD32;
  END;
  SMSemaphorePtr = POINTER TO SMSemaphore;
(*$ IF Debug *)
  MemHunk = RECORD
   node: Node;
   num: LONGCARD;
   adr: ADDRESS;
  END;
  MemHunkPtr = POINTER TO MemHunk;
(*$ ENDIF *)
 VAR
  tagend: LONGCARD;
  tags: TagListArr;
  ftag, ltag, tagptr: TagListPtr;
  tagcnt: CARD16;
  smss: SMSemaphorePtr; (* Semaphore to arbitrate lockList access *)
  volatileList: List;
  nilHandlerCnt: CARD16;

 PROCEDURE Volatilize(): BOOLEAN;
  VAR
   chunk{R.A3}, tail{R.D7}: VolatileChunkPtr;
   oldA4{R.D5}: ADDRESS;
   result{R.D6}: BOOLEAN;
 BEGIN
  chunk:= First(volatileList);
  tail:= Tail(volatileList);
  WHILE chunk <> tail DO
   oldA4:= REG(R.A4);
   SETREG(R.A4, chunk^.regA4);
   result:= chunk^.Handler();
   SETREG(R.A4, oldA4);
   IF NOT result THEN RETURN FALSE END;
   chunk:= Next(chunk^.node)
  END;
  RETURN TRUE
 END Volatilize;

 PROCEDURE InitSharedMemory;
  VAR
   len{R.D7}: CARD16;
 BEGIN
  el.Forbid;
  smss:= ADDRESS(el.FindSemaphore(ADR(SMSemaphoreName)));
  IF smss = NIL THEN
   smss:= el.AllocMem(SIZE(SMSemaphore), ed.MemReqSet{ed.public, ed.memClear});
   IF smss = NIL THEN
    el.Permit;
    Arts.Error(ADR("Not enough memory"), NIL);
    Arts.Terminate
   END;
   WITH smss^ DO
    InitList(lockList);
    InitList(volatileList);
(*$ IF Debug *)
    InitList(memList);
    allocCnt:= 0;
    lockCnt:= 0;
(*$ ENDIF *)
    el.InitSemaphore(ADR(lowMem));
    listLen:= 0;
    count:= 1;
    len:= StrLength(ADR(SMSemaphoreName)) + 1;
    ss.link.name:= el.AllocMem(len, ed.MemReqSet{ed.public});
    CopyStr(ADR(SMSemaphoreName), ss.link.name, len);
    ss.link.pri:= 0;
    IF el.execBase^.libNode.version < 36 THEN
     ss.link.type:= ed.signalSem;
     el.InitSemaphore(ADR(ss));
     (* el.Forbid; Already done *)
     el.Enqueue(ADR(el.execBase^.semaphoreList), ADR(ss));
     (* el.Permit *)
    ELSE
     el.AddSemaphore(ADR(ss));
    END
   END
  ELSE
   INC(smss^.count)
  END;
  el.Permit
 END InitSharedMemory;

 PROCEDURE AllocMem(size{R.D2}: CARD32): ADDRESS;
  VAR
(*$ IF Debug *)
   ptr: POINTER TO CARD32;
   hunk: MemHunkPtr;
(*$ ELSE *)
   ptr{R.A3}: POINTER TO CARD32;
(*$ ENDIF *)
 BEGIN
  INC(size, 4);
  REPEAT
   ptr:= el.AllocMem(size, ed.MemReqSet{ed.memClear})
  UNTIL (ptr <> NIL) OR Volatilize();
  IF ptr <> NIL THEN
   ptr^:= size;
   INC(ptr, 4);
(*$ IF Debug *)
   hunk:= el.AllocMem(SIZE(MemHunk), ed.MemReqSet{});
   IF hunk <> NIL THEN
    hunk^.adr:= ptr;
    IF smss = NIL THEN InitSharedMemory END;
    WITH smss^ DO
(*      IF allocCnt = 15 THEN Arts.BreakPoint(ADR("Bad alloc")) END;*)
     hunk^.num:= allocCnt; INC(allocCnt);
     AddHead(memList, hunk^.node)
    END;
   ELSE
    DEC(ptr, 4);
    el.FreeMem(ptr, size);
    ptr:= NIL
   END
(*$ ENDIF *)
  END;
  RETURN ptr
 END AllocMem;

 PROCEDURE AllocShared(size{R.D2}: CARD32): ADDRESS;
  VAR
   ptr{R.A3}: POINTER TO CARD32;
 BEGIN
(*$ IF Debug *)
  RETURN AllocMem(size);
(*$ ENDIF *)
  INC(size, 4);
  REPEAT
   ptr:= el.AllocMem(size, ed.MemReqSet{ed.public, ed.memClear})
  UNTIL (ptr <> NIL) OR Volatilize();
  IF ptr <> NIL THEN
   ptr^:= size;
   INC(ptr, 4)
  END;
  RETURN ptr
 END AllocShared;

 PROCEDURE LockMem(base: ADDRESS; offset, length: CARD32;
                   modify, wait: BOOLEAN): BOOLEAN;
  VAR
   cur{R.A3}, head{R.D7}, new{R.D6}, next{R.D5}: LockChunkPtr;
   me{R.D4}: ed.TaskPtr;
 BEGIN
(*$ IF NOT Debug *)
  IF NOT(multiThread) THEN RETURN TRUE END;
(*$ ENDIF *)
  IF smss = NIL THEN InitSharedMemory END;
  me:= el.FindTask(NIL);
  el.ObtainSemaphore(ADR(smss^.ss));
   (* Build new semaphore *)
  new:= el.AllocMem(SIZE(LockChunk), ed.MemReqSet{});
  IF new <> NIL THEN
   new^.base:= base;
   new^.offset:= offset;
   new^.length:= length;
   new^.owner:= me;
   new^.write:= modify;
   new^.count:= 1;
  (*$ IF DEBUG *)
   new^.num:= smss^.lockCnt;
(*    IF (smss^.lockCnt >= 249) AND (smss^.lockCnt <= 249) THEN Arts.BreakPoint(ADR("Bad lock")) END;*)
   INC(smss^.lockCnt);
  (*$ ENDIF *)
   el.InitSemaphore(ADR(new^.ss));
   IF modify OR (el.execBase^.libNode.version < 36) THEN
    el.ObtainSemaphore(ADR(new^.ss))
   ELSE
    el.ObtainSemaphoreShared(ADR(new^.ss))
   END;
   AddTail(smss^.lockList, new^.node);
    (* Check for lowMem user *)
   el.ReleaseSemaphore(ADR(smss^.ss));
   el.ObtainSemaphore(ADR(smss^.lowMem));
   el.ObtainSemaphore(ADR(smss^.ss));
   el.ReleaseSemaphore(ADR(smss^.lowMem));
   INC(smss^.listLen)
  ELSE (* Horror! Not enough memory -> use fallback method *)
   el.ReleaseSemaphore(ADR(smss^.ss));
   el.ObtainSemaphore(ADR(smss^.lowMem));
   RETURN TRUE
  END;
   (* Now, no other task can lock overlaping memory before me *)
  cur:= Prev(new^.node);
  head:= Head(smss^.lockList);
   (* Check all previous semaphores *)
  WHILE cur <> head DO
   next:= Prev(cur^.node);
   IF (cur^.base = base) AND (cur^.owner <> me) THEN
    IF (cur^.offset + cur^.length > offset) AND
       (offset + length > cur^.offset) THEN (* overlaping block *)
     IF NOT(wait) AND (modify OR cur^.write) THEN
       (* Failed. Return immediatly *)
      el.ReleaseSemaphore(ADR(new^.ss));
      DEC(new^.count);
      IF new^.count = 0 THEN (* Noone else waiting on it *)
       Remove(new^.node);
       el.FreeMem(new, SIZE(LockChunk))
      END;
      el.ReleaseSemaphore(ADR(smss^.ss));
      RETURN FALSE
     END;
     INC(cur^.count); (* Then it cannot be removed *)
      (* Wait for the semaphore *)
     el.ReleaseSemaphore(ADR(smss^.ss));
     IF modify OR (el.execBase^.libNode.version < 36) THEN
      el.ObtainSemaphore(ADR(cur^.ss))
     ELSE
      el.ObtainSemaphoreShared(ADR(cur^.ss))
     END;
     el.ReleaseSemaphore(ADR(cur^.ss));
     el.ObtainSemaphore(ADR(smss^.ss));
     DEC(cur^.count);
     IF cur^.count = 0 THEN
      Remove(cur^.node);
      el.FreeMem(cur, SIZE(LockChunk))
     END
    END
   END;
   cur:= next
  END;
  el.ReleaseSemaphore(ADR(smss^.ss));
  RETURN TRUE
 END LockMem;

 PROCEDURE UnlockMem(base: ADDRESS; offset, length: CARD32);
  VAR
   cur{R.A3}, tail{R.D7}, next{R.D6}: LockChunkPtr;
   owner{R.D5}, me{R.D4}: ed.TaskPtr;
 BEGIN
  IF smss = NIL THEN RETURN END;
  me:= el.FindTask(NIL);
  el.ObtainSemaphore(ADR(smss^.ss));
  cur:= Last(smss^.lockList);
  tail:= Head(smss^.lockList);
  LOOP
   IF cur = tail THEN (* LowMem method was used *)
     (*$ IF Debug *)
    Arts.BreakPoint(ADR("Bad Unlock"));
     (*$ ELSE *)
    el.ReleaseSemaphore(ADR(smss^.lowMem));
    el.ReleaseSemaphore(ADR(smss^.ss));
     (*$ ENDIF *)
    RETURN
   END;
   IF (cur^.owner = me) AND (cur^.base = base) AND
      (cur^.length = length) AND (cur^.offset = offset) THEN
    EXIT
   END;
   cur:= Prev(cur^.node)
  END;
  el.ReleaseSemaphore(ADR(cur^.ss));
  DEC(cur^.count);
  IF cur^.count = 0 THEN (* noone else waiting on it *)
   Remove(cur^.node);
   DEC(smss^.listLen);
   el.FreeMem(cur, SIZE(LockChunk))
  END;
  el.ReleaseSemaphore(ADR(smss^.ss))
 END UnlockMem;

 TYPE
  CARD32Ptr = POINTER TO CARD32;

 PROCEDURE TryLock(base: ADDRESS; modify: BOOLEAN): BOOLEAN;
 BEGIN
  RETURN LockMem(base, 0, CAST(CARD32Ptr, base - 4)^ - 4, modify, FALSE)
 END TryLock;

 PROCEDURE LockR(base: ADDRESS);
 BEGIN
  IGNORE LockMem(base, 0, CAST(CARD32Ptr, base - 4)^ - 4, FALSE, TRUE)
 END LockR;

 PROCEDURE LockW(base: ADDRESS);
 BEGIN
  IGNORE LockMem(base, 0, CAST(CARD32Ptr, base - 4)^ - 4, TRUE, TRUE)
 END LockW;

 PROCEDURE Unlock(base: ADDRESS);
 BEGIN
  UnlockMem(base, 0, CAST(CARD32Ptr, base - 4)^ - 4)
 END Unlock;

 PROCEDURE AddMemHandler(Handler: MemHandler);
  VAR
   chunk{R.D7}: VolatileChunkPtr;
 BEGIN
  chunk:= el.AllocMem(SIZE(VolatileChunk), ed.MemReqSet{ed.public});
  IF chunk <> NIL THEN
   chunk^.Handler:= Handler;
   chunk^.regA4:= REG(R.A4);
   AddTail(volatileList, chunk^.node)
  ELSE
   REPEAT UNTIL Handler()
  END
 END AddMemHandler;

 PROCEDURE RemMemHandler(Handler: MemHandler);
  VAR
   chunk{R.D7}, tail{R.D6}: VolatileChunkPtr;
 BEGIN
  chunk:= First(volatileList);
  tail:= Tail(volatileList);
  WHILE chunk <> tail DO
   IF chunk^.Handler = Handler THEN
    Remove(chunk^.node);
    el.FreeMem(chunk, SIZE(VolatileChunk));
    RETURN
   END;
   chunk:= Next(chunk^.node)
  END
 END RemMemHandler;

 PROCEDURE FreeMem(VAR ptr{R.A2}: ADDRESS);
  TYPE
   PTR = POINTER TO LONGCARD;
(*$ IF Debug *)
  VAR
   hunk, tail: MemHunkPtr;
(*$ ENDIF *)
 BEGIN
  IF ptr = NIL THEN RETURN END;
(*$ IF Debug *)
  IF smss = NIL THEN InitSharedMemory END;
  WITH smss^ DO
   hunk:= First(memList); tail:= Tail(memList);
   LOOP
    IF hunk = tail THEN Arts.BreakPoint(ADR("Bad FreeMem")); EXIT END;
    IF hunk^.adr = ptr THEN EXIT END;
    hunk:= Next(hunk^.node)
   END;
   IF hunk <> tail THEN
    Remove(hunk^.node);
    el.FreeMem(hunk, SIZE(MemHunk))
   END
  END;
(*$ ENDIF *)
  DEC(ptr, 4);
  el.FreeMem(ptr, CAST(PTR, ptr)^);
  ptr:= NIL
 END FreeMem;

 PROCEDURE NextList(): TagListPtr;
 BEGIN
  IF tagptr = ftag THEN tagptr:= ltag ELSE DEC(tagptr, SIZE(TagList)) END;
  RETURN tagptr
 END NextList;

 PROCEDURE TAG1(t1{R.D2}, v1{R.D3}: LONGINT): ADDRESS;
  VAR
   taglist{R.A2}: TagListPtr;
 BEGIN
  taglist:= NextList();
  taglist^[0]:= t1; taglist^[1]:= v1;
  taglist^[2]:= 0;
  RETURN taglist
 END TAG1;

 PROCEDURE TAG2(t1{R.D2}, v1{R.D3}, t2{R.D4}, v2{R.D5}: LONGINT): ADDRESS;
  VAR
   taglist{R.A2}: TagListPtr;
 BEGIN
  taglist:= NextList();
  taglist^[0]:= t1; taglist^[1]:= v1;
  taglist^[2]:= t2; taglist^[3]:= v2;
  taglist^[4]:= 0;
  RETURN taglist
 END TAG2;

 PROCEDURE TAG3(t1{R.D2}, v1{R.D3}, t2{R.D4}, v2{R.D5}, t3{R.D6}, v3{R.A2}: LONGINT): ADDRESS;
  VAR
   taglist{R.A3}: TagListPtr;
 BEGIN
  taglist:= NextList();
  taglist^[0]:= t1; taglist^[1]:= v1;
  taglist^[2]:= t2; taglist^[3]:= v2;
  taglist^[4]:= t3; taglist^[5]:= v3;
  taglist^[6]:= 0;
  RETURN taglist
 END TAG3;

 PROCEDURE TAG4(t1, v1, t2, v2, t3, v3, t4, v4: LONGINT): ADDRESS;
  VAR
   taglist{R.A2}: TagListPtr;
 BEGIN
  taglist:= NextList();
  taglist^[0]:= t1; taglist^[1]:= v1;
  taglist^[2]:= t2; taglist^[3]:= v2;
  taglist^[4]:= t3; taglist^[5]:= v3;
  taglist^[6]:= t4; taglist^[7]:= v4;
  taglist^[8]:= 0;
  RETURN taglist
 END TAG4;

 PROCEDURE TAG5(t1, v1, t2, v2, t3, v3, t4, v4, t5, v5: LONGINT): ADDRESS;
  VAR
   taglist{R.A2}: TagListPtr;
 BEGIN
  taglist:= NextList();
  taglist^[0]:= t1; taglist^[1]:= v1;
  taglist^[2]:= t2; taglist^[3]:= v2;
  taglist^[4]:= t3; taglist^[5]:= v3;
  taglist^[6]:= t4; taglist^[7]:= v4;
  taglist^[8]:= t5; taglist^[9]:= v5;
  taglist^[10]:= 0;
  RETURN taglist
 END TAG5;

 PROCEDURE TAG6(t1, v1, t2, v2, t3, v3, t4, v4, t5, v5, t6, v6: LONGINT): ADDRESS;
  VAR
   taglist{R.A2}: TagListPtr;
 BEGIN
  taglist:= NextList();
  taglist^[0]:= t1; taglist^[1]:= v1;
  taglist^[2]:= t2; taglist^[3]:= v2;
  taglist^[4]:= t3; taglist^[5]:= v3;
  taglist^[6]:= t4; taglist^[7]:= v4;
  taglist^[8]:= t5; taglist^[9]:= v5;
  taglist^[10]:= t6; taglist^[11]:= v6;
  taglist^[12]:= 0;
  RETURN taglist
 END TAG6;

 PROCEDURE TAG7(t1, v1, t2, v2, t3, v3, t4, v4, t5, v5, t6, v6, t7, v7: LONGINT): ADDRESS;
  VAR
   taglist{R.A2}: TagListPtr;
 BEGIN
  taglist:= NextList();
  taglist^[0]:= t1; taglist^[1]:= v1;
  taglist^[2]:= t2; taglist^[3]:= v2;
  taglist^[4]:= t3; taglist^[5]:= v3;
  taglist^[6]:= t4; taglist^[7]:= v4;
  taglist^[8]:= t5; taglist^[9]:= v5;
  taglist^[10]:= t6; taglist^[11]:= v6;
  taglist^[12]:= t7; taglist^[13]:= v7;
  taglist^[14]:= 0;
  RETURN taglist
 END TAG7;

 PROCEDURE TAG8(t1, v1, t2, v2, t3, v3, t4, v4, t5, v5, t6, v6, t7, v7, t8, v8: LONGINT): ADDRESS;
  VAR
   taglist{R.A2}: TagListPtr;
 BEGIN
  taglist:= NextList();
  taglist^[0]:= t1; taglist^[1]:= v1;
  taglist^[2]:= t2; taglist^[3]:= v2;
  taglist^[4]:= t3; taglist^[5]:= v3;
  taglist^[6]:= t4; taglist^[7]:= v4;
  taglist^[8]:= t5; taglist^[9]:= v5;
  taglist^[10]:= t6; taglist^[11]:= v6;
  taglist^[12]:= t7; taglist^[13]:= v7;
  taglist^[14]:= t8; taglist^[15]:= v8;
  taglist^[16]:= 0;
  RETURN taglist
 END TAG8;

 PROCEDURE NextTag(VAR tags{R.A0}: TagItemPtr): TagItemPtr;
  VAR
   nextTag{R.A1}: TagItemPtr;
 BEGIN
  nextTag:= tags;
  IF nextTag = NIL THEN RETURN ADR(tagend) END;
  WHILE nextTag^.tag >= 0 DO
   IF nextTag^.tag = 0 THEN RETURN nextTag
   ELSIF nextTag^.tag = tagMore THEN nextTag:= nextTag^.addr
   ELSIF nextTag^.tag = tagSkip THEN
    INC(nextTag, SHIFT(nextTag^.data + 1, 3))
   ELSE INC(nextTag, SIZE(TagItem))
   END;
   tags:= nextTag
  END;
  INC(tags, SIZE(TagItem));
  RETURN nextTag;
 END NextTag;

 PROCEDURE StrLength(str{R.A0}: StrPtr): CARD16;
  VAR
   length{R.D0}: CARD16;
 BEGIN
  length:= 0;
  WHILE str^ <> 0C DO
   INC(length); INC(str)
  END;
  RETURN length
 END StrLength;

 PROCEDURE CopyStr(src{R.A0}, dst{R.A1}: StrPtr; maxLength{R.D0}: CARD16);
 BEGIN
  LOOP
   dst^:= src^; DEC(maxLength);
   IF maxLength = 0 THEN dst^:= 0C; EXIT END;
   IF src^ = 0C THEN EXIT END;
   INC(src); INC(dst)
  END
 END CopyStr;

 PROCEDURE ADS(str: ARRAY OF CHAR): ADDRESS;
  (*$ CopyDyn:= FALSE *)
 BEGIN
  RETURN ADR(str)
 END ADS;

 PROCEDURE InitList(VAR list{R.A0}: List);
  VAR
   ptr{R.A1}: NodePtr;
 BEGIN
  ptr:= ADR(list.tail);
  list.head.next:= ptr;
  list.head.prev:= NIL;
  list.tail.next:= NIL;
  ptr:= ADR(list.head);
  list.tail.prev:= ptr
 END InitList;

 PROCEDURE First(VAR list{R.A0}: List): ADDRESS;
 BEGIN
  RETURN list.head.next
 END First;

 PROCEDURE Last(VAR list{R.A0}: List): ADDRESS;
 BEGIN
  RETURN list.tail.prev
 END Last;

 PROCEDURE Head(VAR list{R.D0}: List): ADDRESS;
 BEGIN
  RETURN ADDRESS(REG(R.D0))
 END Head;

 PROCEDURE Tail(VAR list{R.A0}: List): ADDRESS;
  VAR
   tail{R.A1}: ADDRESS;
 BEGIN
  tail:= ADR(list.tail);
  RETURN tail
 END Tail;

 PROCEDURE Empty(VAR list{R.A0}: List): BOOLEAN;
  VAR
   tmp{R.A1}: NodePtr;
   last{R.D1}: NodePtr;
 BEGIN
  tmp:= ADR(list.tail); last:= tmp;
  RETURN last = list.head.next
 END Empty;

 PROCEDURE Prev(VAR node{R.A1}: Node): ADDRESS;
 BEGIN
  RETURN node.prev
 END Prev;

 PROCEDURE Next(VAR node{R.A1}: Node): ADDRESS;
 BEGIN
  RETURN node.next
 END Next;

 PROCEDURE AddHead(VAR list{R.A0}: List; VAR node{R.A1}: Node);
 BEGIN
  node.prev:= ADDRESS(REG(R.A0));
  node.next:= list.head.next;
  list.head.next^.prev:= ADDRESS(REG(R.A1));
  list.head.next:= ADDRESS(REG(R.A1))
 END AddHead;

 PROCEDURE AddTail(VAR list{R.A0}: List; VAR node{R.A1}: Node);
 BEGIN
  node.next:= ADR(list.tail);
  node.prev:= list.tail.prev;
  list.tail.prev^.next:= ADDRESS(REG(R.A1));
  list.tail.prev:= ADDRESS(REG(R.A1))
 END AddTail;

 PROCEDURE AddBefore(VAR before{R.A0}, node{R.A1}: Node);
 BEGIN
  node.prev:= before.prev;
  node.next:= ADDRESS(REG(R.A0));
  before.prev^.next:= ADDRESS(REG(R.A1));
  before.prev:= ADDRESS(REG(R.A1))
 END AddBefore;

 PROCEDURE Remove(VAR node{R.A1}: Node);
  VAR
   tmp{R.A0}: NodePtr;
 BEGIN
  tmp:= node.prev; tmp^.next:= node.next;
  tmp:= node.next; tmp^.prev:= node.prev;
  node.prev:= NIL; node.next:= NIL
 END Remove;


 PROCEDURE sReadInt8(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32): INT8;
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVEQ	#0,D0
	MOVE.B	0(A0,D1.L),D0
	RTS
  END)
 END sReadInt8;

 PROCEDURE sWriteInt8(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32; value{R.D0}: INT8);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVE.B	D0,0(A0,D1.L)
	RTS
  END)
 END sWriteInt8;

 PROCEDURE sReadMInt16(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32): INT16;
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVEQ	#0,D0
	MOVE.W	0(A0,D1.L),D0
	RTS
  END)
 END sReadMInt16;

 PROCEDURE sWriteMInt16(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32; value{R.D0}: INT16);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVE.W	D0,0(A0,D1.L)
	RTS
  END)
 END sWriteMInt16;

 PROCEDURE sReadLInt16(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32): INT16;
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
  	MOVEQ	#0,D0
	MOVE.W	0(A0,D1.L),D1
	MOVE.B	D1,D0
	LSR.W	#8,D1
	LSL.W	#8,D0
	MOVE.B	D1,D0
	RTS
  END)
 END sReadLInt16;

 PROCEDURE sWriteLInt16(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32; value{R.D0}: INT16);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	ADDA.L	D1,A0
	MOVE.B	D0,D1
	LSR.W	#8,D0
	LSL.W	#8,D1
	MOVE.B	D0,D1
	MOVE.W	D1,(A0)
	RTS
  END)
 END sWriteLInt16;

 PROCEDURE sReadMInt32(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32): INT32;
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVE.L	0(A0,D1.L),D0
	RTS
  END)
 END sReadMInt32;

 PROCEDURE sWriteMInt32(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32; value{R.D0}: INT32);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVE.L	D0,0(A0,D1.L)
	RTS
  END)
 END sWriteMInt32;

 PROCEDURE sReadLInt32(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32): INT32;
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVE.L	0(A0,D1.L),D0
	MOVE.L	D0,D1
	ROR.L	#8,D1
	ROR.L	#8,D0
	SWAP	D1
	AND.L	#$FF00FF00,D0
	AND.L	#$00FF00FF,D1
	OR.L	D1,D0
	RTS
  END)
 END sReadLInt32;

 PROCEDURE sWriteLInt32(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32; value{R.D0}: INT32);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	ADDA.L	D1,A0
	MOVE.L	D0,D1
	ROR.L	#8,D1
	ROR.L	#8,D0
	SWAP	D1
	AND.L	#$FF00FF00,D0
	AND.L	#$00FF00FF,D1
	OR.L	D1,D0
	MOVE.L	D0,(A0)
	RTS
  END)
 END sWriteLInt32;

 PROCEDURE sReadMReal32(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32): REAL32;
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVE.L	0(A0,D1.L),D0
	RTS
  END)
 END sReadMReal32;

 PROCEDURE sWriteMReal32(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32; value{R.D0}: REAL32);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVE.L	D0,0(A0,D1.L)
	RTS
  END)
 END sWriteMReal32;

 PROCEDURE sReadLReal32(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32): REAL32;
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	BRA	sReadLInt32(PC)
  END)
 END sReadLReal32;

 PROCEDURE sWriteLReal32(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32; value{R.D0}: REAL32);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	BRA	sWriteLInt32(PC)
  END)
 END sWriteLReal32;

 PROCEDURE sReadMReal64(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32): REAL64;
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVEM.L	0(A0,D2.L),D0-D1
	RTS
  END)
 END sReadMReal64;

 PROCEDURE sWriteMReal64(buffer{R.A0}: ADDRESS; offset{R.D2}: CARD32; value{R.D0}: REAL64);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVEM.L	D0-D1,0(A0,D2.L)
	RTS
  END)
 END sWriteMReal64;

 PROCEDURE sReadLReal64(buffer{R.A0}: ADDRESS; offset{R.D1}: CARD32): REAL64;
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVE.L	D1,A1			(* Save offset *)
	BSR	sReadLInt32(PC)		(* Does not change A0/A1 *)
	EXG	A1,D1			(* Save first long, restore offset *)
	ADDQ.L	#4,D1			(* Offset of 2nd long *)
	BSR	sReadLInt32(PC)		(* D0 has 2nd long *)
	MOVE.L	A1,D1			(* D1 has 1st long *)
	RTS
  END)
 END sReadLReal64;

 PROCEDURE sWriteLReal64(buffer{R.A0}: ADDRESS; offset{R.D2}: CARD32; value{R.D0}: REAL64);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	EXG	D0,D1
	EXG	D1,D2			(* Save 1st long, load offset *)
	BSR	sWriteLInt32(PC)	(* Write 2nd long, set A0 *)
	MOVE.L	D2,D0			(* Load 1st long *)
	MOVEQ	#4,D1			(* Offset of 1st long *)
	BRA	sWriteLInt32(PC)	(* Write 1st long *)
  END)
 END sWriteLReal64;


 PROCEDURE GetBitField(src{R.A0}: ADDRESS; offset{R.D1}: CARD32; size{R.D2}: CARD8): CARD8;
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
  	MOVE.L	D1,D0		(* copy offset *)
	MOVE.L	D3,A1		(* save register *)
  	LSR.L	#3,D0		(* get byte offset *)
	ANDI.W	#$7,D1		(* get bit offset: 0..7 *)
  	ADDA.L	D0,A0		(* add byte offset to address *)
	MOVEQ	#16,D3		(* 16 *)
	MOVEQ	#0,D0		(* 0 *)
	ANDI.W	#$F,D2		(* size: 0..8 *)
	SUB.W	D1,D3		(* 16 - offset *)
	BSET.L	D2,D0		(* 2^size *)
	SUB.W	D2,D3		(* 16 - offset - size *)
	MOVE.B	(A0)+,D1	(* read first byte *)
	SUBQ.W	#1,D0		(* 2^size - 1 (mask) *)
	LSL.W	#8,D1		(* shift first byte *)
	LSL.W	D3,D0		(* shifted mask *)
	MOVE.B	(A0),D1		(* append second byte *)
	AND.W	D1,D0		(* mask the full word *)
	LSR.W	D3,D0		(* unshift the result *)
	MOVE.L	A1,D3		(* restore register *)
	RTS
	END);
 END GetBitField;

 PROCEDURE SetBitField(dst{R.A0}: ADDRESS; offset{R.D1}: CARD32; size{R.D2}: CARD8; data{R.D0}: CARD8);
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVE.L	D3,A1		(* save register *)
  	MOVE.L	D1,D3		(* copy offset *)
  	LSR.L	#3,D3		(* get byte offset *)
	ANDI.W	#$7,D1		(* get bit offset: 0..7 *)
  	ADDA.L	D3,A0		(* add byte offset to address *)
	MOVEQ	#16,D3		(* 16 *)
	ANDI.W	#$F,D2		(* size: 0..8 *)
	SUB.W	D1,D3		(* 16 - offset *)
	MOVEQ	#0,D1		(* 0 *)
	SUB.W	D2,D3		(* 16 - offset - size *)
	BSET.L	D2,D1		(* 2^size *)
	SUBQ.W	#1,D1		(* 2^size - 1 (mask *)
	AND.W	D1,D0		(* mask the word *)
	LSL.W	D3,D1		(* shift mask *)
	NOT.W	D1		(* reverse mask *)
	ROR.W	#8,D1		(* get first byte of mask *)
	AND.B	(A0)+,D1	(* get first byte and mask it *)
	LSL.W	D3,D0		(* shift new data *)
	ROL.W	#8,D1		(* get second byte of mask *)
	AND.B	(A0),D1		(* get second byte and mask it *)
	OR.W	D0,D1		(* append mask *)
	MOVE.B	D1,(A0)		(* write new second byte *)
	LSR.W	#8,D1		(* get new first byte *)
	MOVE.L	A1,D3		(* restore register *)
	MOVE.B	D1,-(A0)	(* write new first byte *)
	RTS
	END)
 END SetBitField;

 PROCEDURE Close;
  VAR
  (*$ IF Debug *)
   lockChunk: LockChunkPtr;
   lockNum: CARD16;
   memHunk: MemHunkPtr;
   memNum: CARD16;
  (*$ ENDIF *)
 BEGIN
   (* Shared Memory *)
  IF smss <> NIL THEN
   el.ObtainSemaphore(ADR(smss^.ss));
   el.Forbid; DEC(smss^.count);
   IF smss^.count = 0 THEN
    el.Permit;
   (*$ IF Debug *)
    IF NOT Empty(smss^.lockList) THEN
     lockChunk:= First(smss^.lockList);
     lockNum:= lockChunk^.num;
     Arts.BreakPoint(ADR("Unlocked ptrs"))
    END;
    WHILE NOT Empty(smss^.lockList) DO
     lockChunk:= First(smss^.lockList);
     UnlockMem(lockChunk^.base, lockChunk^.offset, lockChunk^.length)
    END;
    WITH smss^ DO
     IF NOT Empty(memList) THEN
      memHunk:= First(smss^.memList);
      memNum:= memHunk^.num;
      Arts.BreakPoint(ADR("Unfreed memory"))
     END;
     WHILE NOT Empty(memList) DO
      FreeMem(CAST(MemHunkPtr, First(memList))^.adr)
     END
    END;
   (*$ ENDIF *)
    el.RemSemaphore(ADR(smss^.ss));
    el.ReleaseSemaphore(ADR(smss^.ss));
    el.FreeMem(smss^.ss.link.name, StrLength(ADR(SMSemaphoreName)) + 1);
    el.FreeMem(smss, SIZE(SMSemaphore));
    smss:= NIL
   ELSE
    el.Permit;
    el.ReleaseSemaphore(ADR(smss^.ss))
   END
  END
 END Close;

BEGIN

 ftag:= ADR(tags[0]);
 ltag:= ADR(tags[NumTagList - 1]);
 tagptr:= ftag;
 InitList(volatileList);
 isMsb:= TRUE;

CLOSE

 Close;

END Memory.

