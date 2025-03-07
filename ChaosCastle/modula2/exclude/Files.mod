IMPLEMENTATION MODULE Files;

 FROM SYSTEM IMPORT ADR, ADDRESS, TAG, CAST;
 FROM DosD IMPORT oldFile, FileHandlePtr, FileLockPtr,
  FileInfoBlock, FileInfoBlockPtr, dosFib, readWrite, newFile,
  ProtectionFlags, ProtectionFlagSet;
 FROM DosL IMPORT Open, Seek, Read, Write, Close, Lock, UnLock, CurrentDir,
  Examine, ExNext, AllocDosObject, FreeDosObject, dosVersion, Delay, Fault,
  IoErr, ExamineFH;
 FROM XpkMasterD IMPORT xpkInName, XpkFib, XpkFibPtr, XpkFH;
 FROM OptXpkMasterL IMPORT XpkOpen, XpkRead, XpkClose, XpkSeek, xpkBase;
 FROM AslD IMPORT aslName, aslFileRequest, FileRequester, FileRequesterPtr,
  tfrTitleText, tfrInitialFile, tfrRejectIcons, tfrInitialDrawer,
  tfrDoSaveMode, tfrWindow;
 FROM OptAslL IMPORT AllocAslRequest, AslRequest, FreeAslRequest, aslBase;
 FROM ExecD IMPORT MemReqs, MemReqSet;
 FROM ExecL IMPORT AllocMem, FreeMem, CopyMem;
 FROM IntuitionD IMPORT ScreenPtr;
 FROM IntuitionL IMPORT ScreenToFront, WBenchToFront, LockPubScreen,
  UnlockPubScreen, CloseWorkBench;
 FROM WorkbenchD IMPORT WBArgPtr, WBStartupPtr;
 FROM OptWorkbenchL IMPORT WBInfo, workbenchBase, workbenchVersion;
 FROM Memory IMPORT SET16, CARD16, INT16, CARD32, INT32, StrPtr, TagItem,
  TagItemPtr, NextTag, TAG5, CopyStr, StrLength, REAL32, REAL64, LockR, Unlock;
 FROM AmigaBase IMPORT memLow, globals, programName,
  InitToolTypes, fontName, soundPriority, musicPriority, soundBoost, musicBoost,
  askMode, customMask, maskBM, dbGiven, givenDB, fontGiven, joystick,
  bSmartRefresh, dSmartRefresh, closeWB, filter, filterGiven, speaker, fontSize,
  maskPlaneGiven, givenMaskPlane, joystickGiven, ahiGiven, givenAhi, surround;
 FROM Arts IMPORT startupMsg, wbStarted;
 IMPORT R, DosD, DosL;

 CONST
  XPKF = CAST(CARD32, "XPKF");
  bFile = 0;
  bNewFile = 1;

 TYPE
  File = RECORD
   data: ADDRESS;
   size, blockNum: INT32;
   max, offset: INT32;
   write: BOOLEAN;
   CASE xpk: BOOLEAN OF
     TRUE:  xpkfh: XpkFH;
    |FALSE: fh: FileHandlePtr;
   END;
  END;
  FilePtr = POINTER TO File;
  Directory = RECORD
   path: ARRAY[0..127] OF CHAR;
   pathLength: CARD16;
   lock: FileLockPtr;
   fib: FileInfoBlockPtr;
  END;
  DirectoryPtr = POINTER TO Directory;

  VAR
   tags: ARRAY[0..6] OF ADDRESS;
   fileName: ARRAY[0..127] OF CHAR;
   errorMsg: ARRAY[0..59] OF CHAR;


 PROCEDURE ReadNextBlock(f{R.D2}: FilePtr);
  VAR
   len{R.D7}: INT32;
 BEGIN
  WITH f^ DO
   IF xpk THEN
    len:= xpkfh^.fib.nLen;
    IF data = NIL THEN
     size:= len;
     data:= AllocMem(size, MemReqSet{});
     IF data = NIL THEN max:= 0; RETURN END
    END;
    max:= XpkRead(xpkfh, data, len)
   ELSE
    IF data = NIL THEN
     size:= 4096;
     data:= AllocMem(size, MemReqSet{});
     IF data = NIL THEN max:= 0; RETURN END
    END;
    max:= Read(fh, data, size)
   END;
   INC(blockNum)
  END
 END ReadNextBlock;

 PROCEDURE OpenFile(name: StrPtr; flags: AccessFlagSet): FilePtr;
  VAR
   f{R.D7}: FilePtr;
 BEGIN
  f:= AllocMem(SIZE(File), MemReqSet{memClear});
  IF f = NIL THEN memLow:= TRUE; RETURN NIL END;
  WITH f^ DO
   blockNum:= -1;
   write:= accessWrite IN flags;
   IF write THEN
    IF accessRead IN flags THEN
     fh:= Open(name, readWrite);
     IF (Read(fh, ADR(size), 4) = 4) AND (size = XPKF) THEN
      Close(fh); FreeMem(f, SIZE(File)); RETURN NIL
     END;
     IGNORE Seek(fh, 0, -1)
    ELSE
     fh:= Open(name, newFile)
    END
   ELSE
    xpk:= (xpkBase <> NIL);
    IF xpk THEN
     xpk:= (XpkOpen(xpkfh, TAG(tags, xpkInName, name, 0)) = 0)
    END;
    IF NOT(xpk) THEN
     fh:= Open(name, oldFile);
     IF fh = NIL THEN
      FreeMem(f, SIZE(File)); RETURN NIL
     END;
     IF (Read(fh, ADR(size), 4) = 4) AND (size = XPKF) THEN
      Close(fh); FreeMem(f, SIZE(File)); RETURN NIL
     END;
     IGNORE Seek(fh, 0, -1);
     offset:= 4096; max:= 4096;
     size:= 0
    END
   END
  END;
  RETURN f
 END OpenFile;

 PROCEDURE FileLength(f: FilePtr): INT32;
  VAR
   oldpos{R.D7}: CARD32;
 BEGIN
  WITH f^ DO
   IF xpk THEN
    RETURN xpkfh^.fib.uLen
   ELSE
    oldpos:= Seek(fh, 0, 1);
    RETURN Seek(fh, oldpos, -1)
   END
  END
 END FileLength;

 PROCEDURE ReadFileBytes(f: FilePtr; data: ADDRESS; length: INT32): INT32;
  VAR
   current{R.D7}, templen{R.D6}: INT32;
   inbuffer{R.D5}, outbuffer{R.D4}: ADDRESS;
 BEGIN
  current:= length;
  outbuffer:= data;
  IF f^.write THEN RETURN Read(f^.fh, data, length) END;
  WITH f^ DO
   WHILE current > 0 DO
    IF offset >= max THEN
     ReadNextBlock(f);
     offset:= 0
    END;
    IF max <= 0 THEN RETURN length - current END;
    templen:= current;
    IF offset + templen >= max THEN templen:= max - offset END;
    inbuffer:= data; INC(inbuffer, offset);
    CopyMem(inbuffer, outbuffer, templen);
    INC(outbuffer, templen); DEC(current, templen);
    INC(offset, templen)
   END
  END;
  RETURN length
 END ReadFileBytes;

 PROCEDURE WriteFileBytes(f: FilePtr; data: ADDRESS; length: INT32): INT32;
 BEGIN
  IF f^.write THEN
   RETURN Write(f^.fh, data, length)
  ELSE
   RETURN 0
  END
 END WriteFileBytes;

 PROCEDURE SkipFileBytes(f: FilePtr; count: INT32): INT32;
  VAR
   current{R.D7}, templen{R.D6}: INT32;
 BEGIN
  current:= count;
  WITH f^ DO
   IF write THEN RETURN Seek(fh, count, 0) END;
   WHILE current > 0 DO
    IF offset = max THEN
     ReadNextBlock(f);
     offset:= 0
    END;
    IF max <= 0 THEN RETURN count - current END;
    templen:= current;
    IF offset + templen >= max THEN templen:= max - offset END;
    DEC(current, templen); INC(offset, templen)
   END
  END;
  RETURN count
 END SkipFileBytes;

 PROCEDURE GetFilePos(f: FilePtr): INT32;
 BEGIN
  WITH f^ DO
   IF write THEN
    RETURN Seek(fh, 0, 0)
   ELSIF NOT(xpk) THEN
    RETURN (blockNum * 4096) + offset
   ELSE
    RETURN xpkfh^.fib.uCur
   END
  END
 END GetFilePos;

 PROCEDURE SetFilePos(f: FilePtr; pos: INT32): INT32;
  VAR
   newBlock{R.D7}: LONGINT;
   oldPos{R.D6}: LONGINT;
 BEGIN
  WITH f^ DO
   IF write THEN
    RETURN Seek(fh, pos, -1)
   ELSIF NOT(xpk) THEN
    oldPos:= (blockNum * 4096) + offset;
    newBlock:= pos DIV 4096;
    IF newBlock <> blockNum THEN
     blockNum:= newBlock - 1;
     IGNORE Seek(fh, newBlock * 4096, -1);
     ReadNextBlock(f)
    END;
    offset:= pos MOD 4096;
    RETURN oldPos
   ELSE
    RETURN XpkSeek(xpkfh, pos, -1)
   END
  END
 END SetFilePos;

 PROCEDURE FileErrorMsg(): StrPtr;
  VAR
   val{R.D7}: CARD16;
 BEGIN
  IF dosVersion >= 36 THEN
   IGNORE Fault(IoErr(), NIL, ADR(errorMsg), 60);
  ELSE
    (*$ RangeChk:= FALSE *)
   val:= IoErr();
    (*$ POP RangeChk *)
   IF val = 103 THEN
    errorMsg:= "Not enough memory"
   ELSIF val = 202 THEN
    errorMsg:= "File in use"
   ELSIF val = 203 THEN
    errorMsg:= "File already exists"
   ELSIF (val = 204) OR (val = 205) THEN
    errorMsg:= "File not found"
   ELSIF val = 218 THEN
    errorMsg:= "Device not mounted"
   ELSIF (val = 214) OR (val = 223) THEN
    errorMsg:= "File protected"
   ELSIF val = 221 THEN
    errorMsg:= "Disk full"
   ELSE
    errorMsg:= "File I/O error ###";
    val:= val MOD 1000;
    errorMsg[15]:= CHR(val DIV 100 + 48); val:= val MOD 100;
    errorMsg[16]:= CHR(val DIV 10 + 48);
    errorMsg[17]:= CHR(val MOD 10 + 48)
   END
  END;
  RETURN ADR(errorMsg)
 END FileErrorMsg;

 PROCEDURE CloseFile(VAR f: FilePtr);
 BEGIN
  IF f = NIL THEN RETURN END;
  WITH f^ DO
   IF xpk THEN
    IGNORE XpkClose(xpkfh)
   ELSE
    Close(fh)
   END;
   IF data <> NIL THEN FreeMem(data, size) END
  END;
  FreeMem(f, SIZE(File));
  f:= NIL
 END CloseFile;

 PROCEDURE RenameFile(oldName, newName: StrPtr): BOOLEAN;
 BEGIN
  RETURN DosL.Rename(oldName, newName)
 END RenameFile;

 PROCEDURE DeleteFile(name: StrPtr): BOOLEAN;
 BEGIN
  RETURN DosL.DeleteFile(name)
 END DeleteFile;

 PROCEDURE OpenDirectory(name: StrPtr): DirectoryPtr;
  VAR
   d{R.D7}: DirectoryPtr;
 BEGIN
  d:= AllocMem(SIZE(Directory), MemReqSet{});
  IF d <> NIL THEN
   WITH d^ DO
    CopyStr(name, ADR(path), SIZE(path));
    pathLength:= StrLength(ADR(path));
    IF (pathLength > 0) AND
       (path[pathLength -1] <> ":") AND (path[pathLength-1] <> "/") THEN
     path[pathLength]:= "/"; INC(pathLength)
    END;
    IF dosVersion >= 36 THEN
     fib:= AllocDosObject(dosFib, NIL)
    ELSE
     fib:= AllocMem(SIZE(FileInfoBlock), MemReqSet{memClear})
    END;
    IF fib <> NIL THEN
     lock:= Lock(name, DosD.accessRead);
     IF lock <> NIL THEN
      IF Examine(lock, fib) THEN
       IF fib^.dirEntryType > 0 THEN
        RETURN d
       END
      END;
      UnLock(lock)
     END;
     IF dosVersion >= 36 THEN
      FreeDosObject(dosFib, fib)
     ELSE
      FreeMem(fib, SIZE(FileInfoBlock))
     END
    END
   END;
   FreeMem(d, SIZE(Directory))
  END;
  RETURN NIL
 END OpenDirectory;

 PROCEDURE DirectoryNext(d: DirectoryPtr; tags: TagItemPtr): BOOLEAN;
 BEGIN
  WITH d^ DO
   IF ExNext(lock, fib) THEN
    LOOP
     WITH NextTag(tags)^ DO
      IF tag = 0 THEN EXIT
      ELSIF tag = fNAME THEN
       CopyStr(ADR(fib^.fileName), ADR(path[pathLength]), 128 - pathLength);
       data:= ADR(path)
      ELSIF tag = fLENGTH THEN data:= fib^.size
      ELSIF tag = fFLAGS THEN
       IF fib^.dirEntryType < 0 THEN
        bset:= SET16{bFile}
       ELSE
        bset:= SET16{}
       END;
       IF NOT(execute IN fib^.protection) THEN
        INCL(bset, bfModule)
       END
      END
     END
    END;
    RETURN TRUE
   ELSE
    RETURN FALSE
   END
  END
 END DirectoryNext;

 PROCEDURE CloseDirectory(VAR d: DirectoryPtr);
 BEGIN
  IF d = NIL THEN RETURN END;
  WITH d^ DO
   UnLock(lock);
   IF dosVersion >= 36 THEN
    FreeDosObject(dosFib, fib)
   ELSE
    FreeMem(fib, SIZE(FileInfoBlock))
   END
  END;
  FreeMem(d, SIZE(Directory));
  d:= NIL
 END CloseDirectory;

 VAR
  argIndex, numArg: INTEGER;

 PROCEDURE AskFile(tags: TagItemPtr): StrPtr;
  VAR
   drawer: ARRAY[0..127] OF CHAR;
   tagbuff: ARRAY[0..6] OF TagItem;
   asltag: ADDRESS;
   text, name: StrPtr;
   new, param: BOOLEAN;
   ch, sp: CHAR;
   aslReq{R.D5}: FileRequesterPtr;
   msg: WBStartupPtr;
   f{R.D6}, c{R.D7}: CARD16;
   fh: FileHandlePtr;
   lc, cc: StrPtr;
 BEGIN
  name:= NIL; text:= NIL;
  new:= FALSE; param:= FALSE;
  LOOP
   WITH NextTag(tags)^ DO
    IF tag = 0 THEN EXIT
    ELSIF tag = fNAME THEN name:= addr
    ELSIF tag = fTEXT THEN text:= addr
    ELSIF tag = fFLAGS THEN
     new:= (bNewFile IN bset);
     param:= (bfParam IN bset)
    END
   END
  END;
  IF param THEN
   IF argIndex = 0 THEN
    IF wbStarted THEN
     msg:= startupMsg;
     numArg:= msg^.numArgs - 1
    END
   END;
   IF argIndex >= numArg THEN
    RETURN NIL
   ELSE
    msg:= startupMsg; INC(argIndex);
    RETURN msg^.argList^[argIndex].name
   END
  END;
  IF name = NIL THEN name:= ADR("") END;
  cc:= name; lc:= cc; sp:= 0C; c:= 0; f:= 0;
  WHILE (cc^ <> 0C) DO
   IF (cc^ = ":") OR (cc^ = "/") THEN sp:= cc^; f:= c; lc:= cc END;
   INC(cc); INC(c)
  END;
  IF lc <> name THEN
   INC(f);
   IF lc^ = ":" THEN INC(f) END;
   IF f > 127 THEN f:= 127 END;
   CopyStr(name, ADR(drawer), f);
   cc:= ADR(drawer);
   lc^:= 0C; INC(lc)
  END;
  c:= 0;
  IF aslBase <> NIL THEN
   aslReq:= AllocAslRequest(aslFileRequest, NIL);
   IF aslReq <> NIL THEN
    LockR(globals);
    asltag:= TAG(tagbuff, tfrTitleText, text, tfrInitialFile, lc, tfrInitialDrawer, cc, tfrDoSaveMode, new, tfrRejectIcons, TRUE, tfrWindow, globals^.graphicWindow, 0);
    IF globals^.graphicWindow = NIL THEN tagbuff[5].tag:= 0 END;
    Unlock(globals);
    IF AslRequest(aslReq, asltag) THEN
     f:= 0;
     LOOP
      ch:= aslReq^.dir^[f];
      IF (c >= 127) OR (ch = 0C) THEN EXIT END;
      fileName[c]:= ch;
      INC(c); INC(f)
     END;
     IF c > 0 THEN
      ch:= fileName[c - 1];
      IF (c < 127) AND (ch <> "/") AND (ch <> ":") THEN
       fileName[c]:= "/"; INC(c)
      END
     END;
     f:= 0;
     LOOP
      ch:= aslReq^.file^[f];
      IF (c >= 127) OR (ch = 0C) THEN EXIT END;
      fileName[c]:= ch;
      INC(c); INC(f)
     END
    END;
    fileName[c]:= 0C;
    FreeAslRequest(aslReq);
    IF c = 0 THEN RETURN NIL ELSE RETURN ADR(fileName) END
   END
  ELSE
   fh:= Open(ADR("CON:0/10/640/100/Cannot open asl.library"), newFile);
   IF fh <> NIL THEN
    IGNORE WBenchToFront();
    IGNORE Write(fh, ADR("** Enter a file name **" + 15C + 12C + 12C), 26);
    IF text^ <> 0C THEN
     c:= StrLength(text);
     IGNORE Write(fh, text, c);
     IGNORE Write(fh, ADR("  "), 2)
    END;
    c:= 0;
    WHILE (cc^ <> 0C) AND (c < 126) DO
     fileName[c]:= cc^;
     INC(cc); INC(c)
    END;
    IF sp <> 0C THEN fileName[c]:= sp; INC(c) END;
    REPEAT
     f:= Read(fh, ADR(ch), 1);
     IF (f > 0) AND (ch >= " ") THEN
      fileName[c]:= ch; INC(c)
     ELSIF f <= 0 THEN
      Delay(5)
     END
    UNTIL (c >= 127) OR (ch = 12C);
    fileName[c]:= 0C;
    Close(fh);
    IF closeWB THEN
     Delay(10); IGNORE CloseWorkBench(); Delay(30)
    END;
    LockR(globals);
    IF globals^.graphicWindow <> NIL THEN
     ScreenToFront(globals^.graphicWindow^.wScreen)
    END;
    Unlock(globals);
    IF c <> 0 THEN RETURN ADR(fileName) END
   END
  END;
  RETURN NIL
 END AskFile;

 PROCEDURE AskMiscSettings(which: SET16): SET16;
  VAR
   lock, parent: FileLockPtr;
   progName{R.D7}, rider{R.D6}: StrPtr;
   screen: ScreenPtr;
   c: CARDINAL;
   changes{R.D5}: SET16;
   ok, newFont: BOOLEAN;

   oldFont: ARRAY[0..31] OF CHAR;
   oldSP, oldMP: SHORTINT;
   oldSB, oldMB, oldFontSize: SHORTCARD;
   oldAskMode, oldCustomMask, oldMaskBM, oldDbGiven, oldGivenDB, oldFontGiven,
    oldJoystick, oldBSR, oldDSR, oldCWB, oldFilter, oldFilterGiven, oldSpeaker,
    oldJG, oldMPG, oldGMP, oldSr, oldAhiG, oldGAhi: BOOLEAN;
 BEGIN
  FOR c:= 0 TO 31 DO oldFont[c]:= fontName[c] END;
  oldSP:= soundPriority; oldMP:= musicPriority;
  oldSB:= soundBoost; oldMB:= musicBoost;
  oldGAhi:= givenAhi; oldAhiG:= ahiGiven;
  oldAskMode:= askMode; oldCustomMask:= customMask;
  oldMPG:= maskPlaneGiven; oldGMP:= givenMaskPlane;
  oldMaskBM:= maskBM; oldDbGiven:= dbGiven; oldGivenDB:= givenDB;
  oldFontGiven:= fontGiven; oldJoystick:= joystick; oldJG:= joystickGiven;
  oldBSR:= bSmartRefresh; oldDSR:= dSmartRefresh;
  oldCWB:= closeWB; oldFilter:= filter; oldFontSize:= fontSize;
  oldFilterGiven:= filterGiven; oldSpeaker:= speaker;
  oldSr:= surround;
  IF (workbenchBase <> NIL) AND (workbenchVersion >= 39) THEN
   lock:= Lock(programName, DosD.accessRead);
   IF lock <> NIL THEN
    parent:= DosL.ParentDir(lock);
    UnLock(lock);
    progName:= programName; rider:= progName;
    WHILE rider^ <> 0C DO
     IF (rider^ = ":") OR (rider^ = "/") THEN
      progName:= rider; INC(progName)
     END;
     INC(rider)
    END;
    screen:= LockPubScreen(NIL);
    IF screen <> NIL THEN
     IGNORE WBenchToFront();
     Delay(30);
     ok:= WBInfo(parent, progName, screen);
     UnlockPubScreen(NIL, screen)
    END;
    UnLock(parent);
    IF (screen <> NIL) AND ok THEN
     InitToolTypes
    END
   END
  END;
  newFont:= FALSE;
  FOR c:= 0 TO 31 DO
   IF fontName[c] <> oldFont[c] THEN newFont:= TRUE END
  END;
  changes:= SET16{};
  IF newFont OR (oldAskMode <> askMode) OR (oldCustomMask <> customMask) OR
     (oldMaskBM <> maskBM) OR (oldDbGiven <> dbGiven) OR
     (oldMPG <> maskPlaneGiven) OR (oldGMP <> givenMaskPlane) OR
     (oldGivenDB <> givenDB) OR (oldFontGiven <> fontGiven) OR
     (oldBSR <> bSmartRefresh) OR (oldCWB <> closeWB) OR
     (oldFontSize <> fontSize) THEN
   INCL(changes, msGraphic)
  END;
  IF (oldSP <> soundPriority) OR (oldMP <> musicPriority) OR
     (oldSB <> soundBoost) OR (oldMB <> musicBoost) OR
     (oldFilterGiven <> filterGiven) OR (oldFilter <> filter) OR
     (oldSpeaker <> speaker) OR (oldSr <> surround) OR
     (oldGAhi <> givenAhi) OR (oldAhiG <> ahiGiven) THEN
   INCL(changes, msSound)
  END;
  IF (oldJoystick <> joystick) OR (oldJG <> joystickGiven) THEN
   INCL(changes, msInput)
  END;
  IF (oldDSR <> dSmartRefresh) OR newFont OR
     (fontGiven <> oldFontGiven) OR (oldFontSize <> fontSize) THEN
   INCL(changes, msDialogs)
  END;
  changes:= changes * which;
  IF NOT(msGraphic IN changes) THEN
   IF closeWB THEN
    IGNORE CloseWorkBench()
   END;
   LockR(globals);
   IF globals^.graphicWindow <> NIL THEN
    ScreenToFront(globals^.graphicWindow^.wScreen)
   END;
   Unlock(globals)
  END;
  RETURN changes
 END AskMiscSettings;

 PROCEDURE FileToAddress(file{R.D0}: FilePtr): ADDRESS;
 BEGIN
  RETURN file
 END FileToAddress;

 PROCEDURE AddressToFile(addr{R.D0}: ADDRESS): FilePtr;
 BEGIN
  RETURN addr
 END AddressToFile;

END Files.
