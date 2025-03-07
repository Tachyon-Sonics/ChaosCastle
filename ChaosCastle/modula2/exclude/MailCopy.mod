MODULE MailCopy;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM Memory IMPORT CARD16, INT16, CARD32, INT32, StrLength, CopyStr,
  TAG1, TAG2, TAG3, TAG4, StrPtr;
 FROM Checks IMPORT Check, CheckMem, CheckMemBool, AddTermProc;
 FROM Files IMPORT FilePtr, OpenFile, ReadFileBytes, WriteFileBytes,
  CloseFile, AccessFlags, AccessFlagSet, AskFile, noFile, fNAME, fTEXT,
  SetFilePos;
 FROM Terminal IMPORT WriteString, WriteLn;


 VAR
  fileName: StrPtr;
  emailName, adrsName, outName: ARRAY[0..79] OF CHAR;
  address: ARRAY[0..79] OF CHAR;
  line: ARRAY[0..511] OF CHAR;
  email, adrs, out: FilePtr;
  ch: CHAR;
  c, res: INT32;
  base, count: INT32;


 PROCEDURE Close;
 BEGIN
  CloseFile(out);
  CloseFile(email);
  CloseFile(adrs);
 END Close;

 PROCEDURE ReadLine(file: FilePtr; VAR line: ARRAY OF CHAR): BOOLEAN;
  VAR
   res: INT32;
   c: CARD16;
   ch: CHAR;
 BEGIN
  c:= 0;
  REPEAT
   res:= ReadFileBytes(file, ADR(ch), 1);
   IF res <> 1 THEN line[c]:= 0C; RETURN (c <> 0) END;
   line[c]:= ch;
   INC(c);
  UNTIL ch >= " ";
  REPEAT
   res:= ReadFileBytes(file, ADR(ch), 1);
   IF res <> 1 THEN line[c]:= 0C; RETURN (c <> 0) END;
   line[c]:= ch;
   INC(c);
  UNTIL ch < " ";
  line[c]:= 0C;
  RETURN TRUE;
 END ReadLine;

 PROCEDURE WriteLine(file: FilePtr; VAR line: ARRAY OF CHAR): BOOLEAN;
  VAR
   length, res: INT32;
 BEGIN
  length:= StrLength(ADR(line));
  res:= WriteFileBytes(file, ADR(line), length);
  RETURN (res = length);
 END WriteLine;

 PROCEDURE StartsWith(VAR line: ARRAY OF CHAR; prefix: ARRAY OF CHAR): BOOLEAN;
  VAR
   b, c: CARD16;
   l, p: CARD16;
  (*$ CopyDyn:= FALSE *)
 BEGIN
  l:= StrLength(ADR(line));
  p:= StrLength(ADR(prefix));
  b:= 0; c:= 0;
  WHILE (b < l) AND (line[b] < " ") DO INC(b) END;
  WHILE (c < p) AND (b < l) DO
   IF line[b] <> prefix[c] THEN RETURN FALSE END;
   INC(b); INC(c);
  END;
  RETURN c = p;
 END StartsWith;

 PROCEDURE ReplaceDestination(VAR line: ARRAY OF CHAR; dest: ARRAY OF CHAR);
  VAR
   sp: CARD16;
   len: CARD16;
  (*$ CopyDyn:= FALSE *)
 BEGIN
  sp:= 4;
  len:= StrLength(ADR(dest));
  CopyStr(ADR(dest), ADR(line[sp]), len + 1);
  line[sp + len + 0]:= 12C;
  line[sp + len + 1]:= 0C;
 END ReplaceDestination;

 PROCEDURE ReplaceId(VAR line: ARRAY OF CHAR; add: INT32);
  VAR
   c: CARD16;
   v, n: INT32;
 BEGIN
  c:= 0;
  WHILE (line[c] <> "@") DO INC(c) END;
  REPEAT
   DEC(c);
   v:= ORD(line[c]) - 48;
   n:= (v + add) MOD 10;
   add:= add DIV 10;
   IF (n < v) THEN INC(add) END;
   line[c]:= CHR(n + 48);
  UNTIL add = 0;
 END ReplaceId;


BEGIN

 adrs:= noFile;
 email:= noFile;
 out:= noFile;
 AddTermProc(Close);

 fileName:= AskFile(TAG1(fTEXT, ADR("E-Mail to copy")));
 CheckMemBool(fileName = NIL);
 CopyStr(fileName, ADR(emailName), SIZE(emailName));
 email:= OpenFile(ADR(emailName), AccessFlagSet{accessRead});
 CheckMemBool(email = noFile);

 fileName:= AskFile(TAG1(fTEXT, ADR("E-mail addresses")));
 CheckMemBool(fileName = NIL);
 CopyStr(fileName, ADR(adrsName), SIZE(adrsName));
 adrs:= OpenFile(ADR(adrsName), AccessFlagSet{accessRead});
 CheckMemBool(adrs = noFile);

 count:= 0;
 LOOP
   (* Read the next e-mail address *)
  c:= 0;
  REPEAT
   res:= ReadFileBytes(adrs, ADR(ch), 1);
   IF res <> 1 THEN EXIT END;
   address[c]:= ch;
   IF ch > " " THEN INC(c) END;
  UNTIL (ch <= " ") AND (c > 0);
  address[c]:= 0C;
  WriteLn;
  WriteString("Address: ");
  WriteString(address);
  WriteLn;

 (* Create a new e-mail file *)
  CopyStr(ADR(emailName), ADR(outName), SIZE(outName));
  WriteString("Out file: ");
  c:= StrLength(ADR(outName)) - 3;
  base:= (ORD(outName[c]) - 48) * 100
       + (ORD(outName[c+1]) - 48) * 10
       + (ORD(outName[c+2]) - 48 + 1);
  outName[c+0]:= CHR((base + count) / 100 + 48);
  outName[c+1]:= CHR(((base + count) / 10) MOD 10 + 48);
  outName[c+2]:= CHR((base + count) MOD 10 + 48);
  WriteString(outName); WriteLn;
  out:= OpenFile(ADR(outName), AccessFlagSet{accessWrite});
  Check(out = noFile, ADR("Failed to create file"), ADR(outName));
  IGNORE SetFilePos(email, 0);

 (* Copy email *)
  WHILE ReadLine(email, line) DO
   IF StartsWith(line, "To:") THEN
    ReplaceDestination(line, address);
    WriteString(line); WriteLn;
   ELSIF StartsWith(line, "Message-ID:") THEN
    ReplaceId(line, count + 1);
    WriteString(line); WriteLn;
   END;
   IF NOT WriteLine(out, line) THEN END;
  END;
  CloseFile(out);

  INC(count);
 END;
 Close;

END MailCopy.
