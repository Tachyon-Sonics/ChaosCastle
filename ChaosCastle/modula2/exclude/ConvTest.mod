MODULE ConvTest;

 FROM Memory IMPORT CARD16, INT16, CARD32, INT32, AllocMem, FreeMem;
 FROM Checks IMPORT CheckMem, AddTermProc;
 FROM Convolve IMPORT SlowConvolve, FastConvolve, Card16ArrPtr, SNTT,
  MediumConvolve, PTPMul, Normalize, ForwFNTT, BackFNTT, Power, Shift,
  FastReConvolve;
 FROM Trigo IMPORT RND;
 FROM Terminal IMPORT Write, WriteInt, WriteString, WriteLn, Read;
 FROM Clock IMPORT TimePtr, AllocTime, FreeTime, StartTime, GetTime;


 CONST
  LEN = 4096;
  LOG = 12;

 VAR
  t: TimePtr;
  a, b, c: Card16ArrPtr;
  i: CARD16;
  ch: CHAR;


 PROCEDURE WriteArr(a: Card16ArrPtr; len: CARD16);
  VAR
   c: CARD16;
 BEGIN
  Write("[");
  FOR c:= 0 TO len - 1 DO
   IF c <> 0 THEN Write(",") END;
   WriteInt(a^[c], -1)
  END;
  Write("]");
  WriteLn
 END WriteArr;

 PROCEDURE BitReverse(a: Card16ArrPtr; len, log: CARD16);
  VAR
   c, t, d, i, swap: CARD16;
 BEGIN
  FOR c:= 0 TO len DIV 2 - 1 DO
   t:= c; d:= 0;
   FOR i:= 1 TO log DO
    d:= d * 2;
    IF ODD(t) THEN INC(d) END;
    t:= t DIV 2
   END;
   swap:= a^[c]; a^[c]:= a^[d]; a^[d]:= swap
  END
 END BitReverse;

 PROCEDURE Close;
 BEGIN
  FreeTime(t);
  FreeMem(c);
  FreeMem(b);
  FreeMem(a)
 END Close;

 PROCEDURE SetArrs;
  VAR
   i: CARD16;
 BEGIN
  FOR i:= 0 TO LEN - 1 DO
   a^[i]:= 0(*RND()*); b^[i]:= 0(*RND()*)
  END;
  a^[0]:= 3; a^[1]:= 5;
  b^[0]:= 1; b^[2]:= 2; b^[3]:= 4;
 END SetArrs;

BEGIN

 a:= NIL; b:= NIL; c:= NIL;
 AddTermProc(Close);
 a:= AllocMem(LEN * SIZE(CARD16));
 CheckMem(a);
 b:= AllocMem(LEN * SIZE(CARD16));
 CheckMem(b);
 c:= AllocMem(LEN * SIZE(CARD16));
 CheckMem(c);

 SetArrs;
 SlowConvolve(a, b, c, 8, 3);
 WriteArr(c, 8);
 SetArrs;
 FastConvolve(a, b, c, 8, 3);
 WriteArr(c, 8);
 SetArrs;
 ForwFNTT(b, 8, 3);
 Normalize(b, 8);
 FastReConvolve(a, b, c, 8, 3);
 WriteArr(c, 8);


 WriteString("Initializing..."); WriteLn;
 SetArrs;
 Shift(b, LEN, -9);
 ForwFNTT(b, LEN, LOG);
 Normalize(b, LEN);
 WriteString("Ready."); WriteLn;
 t:= AllocTime(1000);
 WriteString("Shifting..."); WriteLn;
 StartTime(t);
 FOR i:= 1 TO 20 DO
  Shift(a, LEN, -9);
 END;
 WriteString("Fast Convolve..."); WriteLn;
 FOR i:= 1 TO 20 DO
  FastReConvolve(a, b, c, LEN, LOG);
 END;
 WriteInt(GetTime(t), -1);
 WriteString(" miliseconds"); WriteLn;
(*
 WriteString("Normal Convolve..."); WriteLn;
 StartTime(t);
 SlowConvolve(a, b, c, LEN, LOG);
 WriteInt(GetTime(t), -1);
 WriteString(" miliseconds"); WriteLn;
*)
 WriteString("Done"); WriteLn;

END ConvTest.

