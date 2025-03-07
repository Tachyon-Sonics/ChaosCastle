IMPLEMENTATION MODULE Registration;

 FROM SYSTEM IMPORT ADR, ADDRESS, CAST;
 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32, StrPtr,
  CopyStr, StrLength;
 FROM Files IMPORT AccessFlags, AccessFlagSet, FilePtr, OpenFile, ReadFileBytes,
  CloseFile, noFile, WriteFileBytes, FileErrorMsg;
 FROM Checks IMPORT Warn, CheckMem;
 FROM AmigaBase IMPORT programName;
 FROM ExecL IMPORT FindTask, AllocMem, FreeMem;
 FROM ExecD IMPORT Task, TaskPtr, MemReqs, MemReqSet;
 FROM DosD IMPORT Process, ProcessPtr, FileLock, FileLockPtr, FileInfoBlock,
  FileInfoBlockPtr, dosFib, newFile, FileHandlePtr;
 FROM DosL IMPORT Lock, UnLock, Examine, AllocDosObject, FreeDosObject,
  dosVersion, ParentDir, Open, Read, Write, Close;
 IMPORT DosD;

 VAR
  datas: ARRAY[0..95] OF CHAR;

 PROCEDURE Init;
  VAR
   keyFile: ARRAY[0..191] OF CHAR;
   fullName: ARRAY[0..95] OF CHAR;
   sum1, sum2, sum3, sum4: CARD32;
   res: INT32;
   file: FilePtr;
   oldWin, name: ADDRESS;
   process: ProcessPtr;
   d: INT16;
   ok: BOOLEAN;

  PROCEDURE GetFullName;
   VAR
    fib: FileInfoBlockPtr;
    lock, oldLock: FileLockPtr;
    c, l, d: INT16;
  BEGIN
   IF dosVersion >= 36 THEN
    fib:= AllocDosObject(dosFib, NIL)
   ELSE
    fib:= AllocMem(SIZE(FileInfoBlock), MemReqSet{memClear})
   END;
   CheckMem(fib);
   c:= 96;
   lock:= Lock(programName, DosD.accessRead);
   WHILE (lock <> NIL) AND Examine(lock, fib) DO
    l:= StrLength(ADR(fib^.fileName));
    IF fib^.dirEntryType > 0 THEN DEC(c); fullName[c]:= "/" END;
    DEC(c, l); IF c < 0 THEN c:= 0 END;
    FOR d:= 0 TO l - 1 DO
     fullName[c + d]:= fib^.fileName[d]
    END;
    oldLock:= lock;
    lock:= ParentDir(lock);
    UnLock(oldLock)
   END;
   FOR d:= 0 TO 95 - c DO
    fullName[d]:= fullName[d + c]
   END;
   FOR d:= 96 - c TO 95 DO fullName[d]:= 0C END;
   IF dosVersion >= 36 THEN
    FreeDosObject(dosFib, fib)
   ELSE
    FreeMem(fib, SIZE(FileInfoBlock))
   END
  END GetFullName;

  PROCEDURE GetSums(VAR sum1, sum2, sum3: CARD32);
   VAR
    c, v, d: CARD16;
  BEGIN
    (*$ OverflowChk:= FALSE *)
   sum1:= 0; sum2:= 0; sum3:= 0;
   FOR c:= 0 TO 191 DO
    v:= ORD(keyFile[c]);
    IF c < 96 THEN INC(sum1, v * v + 7) END;
    INC(v, ORD(keyFile[(c + 13) MOD 64]) + 6);
    d:= 2;
    WHILE v > 1 DO
     WHILE v MOD d <> 0 DO INC(d) END;
     v:= v DIV d;
     INC(sum3, d * v);
     IF c < 96 THEN INC(sum2, v * 9 + 1) END
    END
   END;
   sum1:= sum1 MOD 65536;
   sum2:= sum2 MOD 65536;
    (*$ POP OverflowChk *)
  END GetSums;

  PROCEDURE ReadPassword(): BOOLEAN;
   VAR
    password, real: ARRAY[0..7] OF CHAR;
    fh: FileHandlePtr;
    c, s1, s2: CARD16;
  BEGIN
   s1:= sum1; s2:= sum2;
   FOR c:= 0 TO 3 DO
    real[c]:= CHR(s1 MOD 16 + 65); s1:= s1 DIV 16
   END;
   FOR c:= 4 TO 7 DO
    real[c]:= CHR(s2 MOD 16 + 65); s2:= s2 DIV 16
   END;
   c:= 0;
   fh:= Open(ADR("CON:0/10/600/100/Password Request"), newFile);
   IF fh <> NIL THEN
     (*$ StackParms:= TRUE *)
    IGNORE Write(fh, programName, StrLength(programName));
     (*$ POP StackParms *)
    IGNORE Write(fh, ADR(15C + 12C + 12C + "This program is registered to:" + 15C + 12C), 35);
    IGNORE Write(fh, ADR(keyFile[0]), 32);
    IGNORE Write(fh, ADR(15C + 12C), 2);
    IGNORE Write(fh, ADR(keyFile[32]), 32);
    IGNORE Write(fh, ADR(15C + 12C), 2);
    IGNORE Write(fh, ADR(keyFile[64]), 32);
    IGNORE Write(fh, ADR(15C + 12C + 12C + "Please enter your password and press RETURN:" + 15C + 12C), 49);
    LOOP
     IF Read(fh, ADR(password[c]), 1) <> 1 THEN EXIT END;
     IF password[c] < " " THEN EXIT END;
     INC(c); IF c >= 8 THEN EXIT END
    END;
    Close(fh)
   END;
   WHILE c < 8 DO password[c]:= 0C; INC(c) END;
   FOR c:= 0 TO 7 DO
    IF password[c] <> real[c] THEN RETURN FALSE END
   END;
   RETURN TRUE
  END ReadPassword;

 BEGIN
  registered:= TRUE;
  (*
  registered:= FALSE;
  process:= CAST(ProcessPtr, FindTask(NIL));
  oldWin:= process^.windowPtr;
  process^.windowPtr:= CAST(ADDRESS, -1);
  name:= ADR("KEYS:ChaosCastle.key");
  file:= OpenFile(name, AccessFlagSet{accessRead});
  IF file = noFile THEN
   name:= ADR("KEYFILES:ChaosCastle.key");
   file:= OpenFile(name, AccessFlagSet{accessRead})
  END;
  IF file = noFile THEN
   name:= ADR("S:ChaosCastle.key");
   file:= OpenFile(name, AccessFlagSet{accessRead})
  END;
  IF file = noFile THEN
   name:= ADR("L:ChaosCastle.key");
   file:= OpenFile(name, AccessFlagSet{accessRead})
  END;
  IF file <> noFile THEN
   res:= ReadFileBytes(file, ADR(keyFile), 192)
       + ReadFileBytes(file, ADR(sum4), 4);
   CloseFile(file);
   process^.windowPtr:= oldWin;
   IF res = 196 THEN
    GetSums(sum1, sum2, sum3);
    IF (sum3 <> sum4) THEN RETURN END;
    GetFullName;
    ok:= TRUE;
    FOR d:= 0 TO 95 DO
     IF (keyFile[d + 96] <> fullName[d]) THEN ok:= FALSE END
    END;
    IF NOT(ok) THEN
     registered:= ReadPassword();
     IF registered THEN
      FOR d:= 0 TO 95 DO keyFile[d + 96]:= fullName[d] END;
      GetSums(sum1, sum2, sum3); res:= 0;
      file:= OpenFile(name, AccessFlagSet{accessWrite});
      IF file <> noFile THEN
       res:= WriteFileBytes(file, ADR(keyFile), 192)
           + WriteFileBytes(file, ADR(sum3), 4)
      END;
      Warn((res <> 196) OR (file = noFile),
           ADR("Error updating keyfile:"),
           FileErrorMsg());
      CloseFile(file)
     END;
    ELSE
     registered:= TRUE
    END;
    FOR d:= 0 TO 95 DO datas[d]:= keyFile[d] END;
    userName:= ADR(datas[0]);
    userAddress:= ADR(datas[32]);
    userLoc:= ADR(datas[64])
   END
  END;
  process^.windowPtr:= oldWin
  *)
 END Init;

BEGIN

 Init;

END Registration.
