MODULE ObjConv;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM Memory IMPORT CARD16, INT16, CARD32, INT32, SET16, TAG1, TAG2, TAG3,
  StrPtr;
 FROM Checks IMPORT Check, AddTermProc;
 FROM Files IMPORT FilePtr, noFile, AccessFlags, AccessFlagSet, fNAME,
  fTEXT, fFLAGS, afNEWFILE, AskFile, OpenFile, ReadFileBytes, WriteFileBytes,
  CloseFile, FileErrorMsg;

 VAR
  in, out: FilePtr;
  name: StrPtr;

 PROCEDURE Close;
 BEGIN
  CloseFile(in);
  CloseFile(out)
 END Close;

 PROCEDURE GetChar(VAR ch: CHAR);
  VAR
   res: INT32;
 BEGIN
  res:= ReadFileBytes(in, ADR(ch), 1);
  IF res <> 1 THEN Check(TRUE, ADR("Error while reading input"), FileErrorMsg()) END
 END GetChar;

 PROCEDURE GetVal(VAR val: INT16);
  VAR
   ch: CHAR;
 BEGIN
  val:= 0;
  REPEAT
   GetChar(ch)
  UNTIL (ch >= "0") AND (ch <= "9");
  REPEAT
   val:= val * 10 + (ORD(ch) - ORD("0"));
   GetChar(ch)
  UNTIL (ch < "0") OR (ch > "9")
 END GetVal;

 PROCEDURE PutChar(ch: CHAR);
  VAR
   res: INT32;
 BEGIN
  res:= WriteFileBytes(out, ADR(ch), 1);
  IF res <> 1 THEN
   Check(TRUE, ADR("Error while writing output"), FileErrorMsg())
  END
 END PutChar;

 PROCEDURE CheckCoords(x, y: INT16; VAR ch: CHAR);
 BEGIN
  IF y >= 256 THEN INC(ch, 128) END;
  IF x >= 256 THEN INC(ch, 32) END
 END CheckCoords;

 PROCEDURE PutVal(val: INT16);
 BEGIN
  PutChar(CHR(val MOD 256))
 END PutVal;

 VAR
  cmd: CHAR;
  p1, p2, p3, p4, p5: INT16;

BEGIN

 in:= noFile; out:= noFile;
 AddTermProc(Close);
 name:= AskFile(TAG1(fTEXT, ADR("Input file:")));
 in:= OpenFile(name, AccessFlagSet{accessRead});
 Check(in = noFile, ADR("Cannot open file"), name);
 name:= AskFile(TAG2(fTEXT, ADR("Output file:"), fFLAGS, afNEWFILE));
 out:= OpenFile(name, AccessFlagSet{accessWrite});
 Check(out = noFile, ADR("Cannot create file"), name);
 LOOP
  GetChar(cmd);
  CASE cmd OF
  "C":
   GetVal(p1); GetVal(p2); GetVal(p3);
   PutChar(cmd); PutVal(p1);
   PutVal(p2); PutVal(p3)
  |"P":
   GetVal(p1); GetVal(p2);
   PutChar(cmd);
   PutVal(p1); PutVal(p2)
  |"X":
   GetVal(p1); GetVal(p2);
   PutChar(cmd);
   PutVal(p1); PutVal(p2)
  |"M":
   GetVal(p1);
   PutChar(cmd); PutVal(p1)
  |"R":
   GetVal(p1); GetVal(p2); GetVal(p3); GetVal(p4);
   CheckCoords(p3, p4, cmd); PutChar(cmd);
   PutVal(p1); PutVal(p2); PutVal(p3); PutVal(p4)
  |"E":
   GetVal(p1); GetVal(p2); GetVal(p3); GetVal(p4);
   CheckCoords(p3, p4, cmd); PutChar(cmd);
   PutVal(p1); PutVal(p2); PutVal(p3); PutVal(p4)
  |"T":
   GetVal(p1); GetVal(p2); GetVal(p3); GetVal(p4); GetVal(p5);
   CheckCoords(p1, p2, cmd); PutChar(cmd);
   PutVal(p1); PutVal(p2); PutVal(p3); PutVal(p4); PutVal(p5);
   REPEAT GetChar(cmd) UNTIL cmd = '"';
   REPEAT GetChar(cmd); PutChar(cmd) UNTIL cmd = '"'
  |"L":
   GetVal(p1); GetVal(p2); GetVal(p3);
   CheckCoords(p1, p2, cmd);
   PutChar(cmd); PutVal(p1);
   PutVal(p2); PutVal(p3)
  |"F":
   PutChar(cmd)
  ELSE
  END
 END;

END ObjConv.
