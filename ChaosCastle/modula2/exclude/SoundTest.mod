MODULE SoundTest;

 FROM SYSTEM IMPORT ADR, ADDRESS, TAG;
 FROM Sounds IMPORT sWAVE, sLENGTH, sFREQ, sRATE, sVOLUME, AllocChannel,
  FreeChannel, ChannelPtr, SndDo, SndQueue, AllocWave, SndFinish, sSTEREO,
  FreeWave, noChan, sAM, sFM;
 FROM Files IMPORT FilePtr, AskFile, OpenFile, AccessFlags, AccessFlagSet,
  FileLength, ReadFileBytes, CloseFile, fNAME, fTEXT;
 FROM Memory IMPORT FreeMem, TAG2, TAG1, YES, NO;
 FROM Trigo IMPORT SIN;
 FROM Terminal IMPORT Read, Write, WriteString, WriteInt, WriteLn, Flush;
 FROM Clock IMPORT TimePtr, AllocTime, FreeTime, StartTime, GetTime, WaitTime;

 VAR
  t: TimePtr;
  file: FilePtr;
  fileName: ADDRESS;
  sin: POINTER TO ARRAY[0..63] OF SHORTINT;
  wave: ADDRESS;
  length: LONGCARD;
  chan1, chan2: ChannelPtr;
  c: INTEGER;
  ch: CHAR;
  tags: ARRAY[0..20] OF LONGCARD;


BEGIN

 t:= AllocTime(10000);
 IF t = NIL THEN RETURN END;
 WriteString("Allocating 2 channels"); WriteLn;
 StartTime(t);
 chan1:= AllocChannel(TAG(tags, sSTEREO, TRUE, 0));
 IF chan1 = noChan THEN HALT END;
 chan2:= AllocChannel(TAG(tags, sSTEREO, TRUE, 0));
 IF chan2 = noChan THEN HALT END;
 WriteInt(GetTime(t), -1); WriteLn;
 WriteString("Asking sample file"); WriteLn;
 fileName:= AskFile(TAG2(fTEXT, ADR("Select a sample"), fNAME, ADR("Samples/")));
 IF fileName = NIL THEN HALT END;
 WriteString("Reading sample"); WriteLn;
 file:= OpenFile(fileName, AccessFlagSet{accessRead});
 IF file = NIL THEN HALT END;
 length:= FileLength(file);
 wave:= AllocWave(length);
 IF wave = NIL THEN HALT END;
 IF ReadFileBytes(file, wave, length) < LONGINT(length) THEN HALT END;
 CloseFile(file);
 WriteInt(length, -1); WriteLn;

 WriteString("SndDo 1"); WriteLn;
 StartTime(t);
 SndQueue(chan1, TAG(tags, sWAVE, wave, sLENGTH, length, sRATE, 8363, sVOLUME, 255, sSTEREO, 64, 0));
 WriteInt(GetTime(t), -1); WriteLn;
 SndQueue(chan1, TAG(tags, sWAVE, wave, sLENGTH, length, sRATE, 10537, sVOLUME, 255, sSTEREO, 64, 0));
 SndQueue(chan1, TAG(tags, sWAVE, wave, sLENGTH, length, sRATE, 6265, sVOLUME, 128, sSTEREO, 64, 0));
 SndQueue(chan1, TAG(tags, sWAVE, wave, sLENGTH, length, sRATE, 8363, sVOLUME, 255, sSTEREO, 64, 0));

 StartTime(t);
 FOR c:= 128 TO 32 BY -8 DO
  SndDo(chan1, TAG(tags, sAM, c, sFM, 384 - c, 0));
  IF c = 128 THEN WriteInt(GetTime(t), -1); WriteLn END;
  IGNORE WaitTime(t, 800);
 END;
 FOR c:= 40 TO 128 BY 8 DO
  IGNORE WaitTime(t, 800);
  SndDo(chan1, TAG(tags, sAM, c, sFM, 384 - c, 0))
 END;

 IGNORE WaitTime(t, 10000);
 SndDo(chan2, TAG(tags, sWAVE, wave, sLENGTH, length, sRATE, 4181, sVOLUME, 250, sSTEREO, 127, 0));
 IGNORE WaitTime(t, 10000);
 SndDo(chan1, TAG(tags, sWAVE, wave, sLENGTH, length, sRATE, 16726, sVOLUME, 255, sSTEREO, -64, 0));
 IGNORE WaitTime(t, 10000);
 FOR c:= 126 TO -124 BY -4 DO
  IGNORE WaitTime(t, 320);
  SndDo(chan2, TAG(tags, sSTEREO, c, 0))
 END;

 WriteString("Busy waiting for finish..."); Flush;
 REPEAT
 UNTIL SndFinish(chan1) AND SndFinish(chan2);
 WriteString("ok"); WriteLn;

CLOSE

 WriteString("<RETURN>"); Read(ch);
 WriteString("Close"); WriteLn;
 IF file <> NIL THEN CloseFile(file) END;
 IF wave <> NIL THEN FreeWave(wave); wave:= NIL END;
 IF sin <> NIL THEN FreeMem(sin); sin:= NIL END;
 IF t <> NIL THEN FreeTime(t) END;
 IF chan1 <> noChan THEN FreeChannel(chan1) END;
 IF chan2 <> noChan THEN FreeChannel(chan2) END;

END SoundTest.
