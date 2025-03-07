
 VAR
  numBits: ARRAY[0..255] OF CARD8;

 PROCEDURE InitNumBits;
  VAR
   c, d, s, v: CARD8;
 BEGIN
  numBits[0]:= 0;
  s:= 1; v:= 1; d:= 1;
  FOR c:= 1 TO 255 DO
   numBits[c]:= v;
   DEC(d);
   IF d = 0 THEN
    INC(v);
    s:= s * 2;
    d:= s
   END
  END
 END InitNumBits;


 PROCEDURE MuLawEncode(s: INT16 (*AudioSample*)): CARD8 (*AudioByte*);
  VAR
   sign, exponent, mantissa: CARD8;
   adjusted:  CARD16;
 BEGIN
  IF s < 0 THEN sign:= 0; s:= -s ELSE sign:= 080H END;
  adjusted:= s; (* adjusted:= SHIFT(adjusted, 16 - SIZE(AudioSample) * 8); *)
  INC(adjusted, 128 + 4);
  IF adjusted > 32767 THEN adjusted:= 32767 END;
  exponent:= numBits[adjusted DIV 128] - 1;
  mantissa:= SHIFT(adjusted, -(exponent + 3)) MOD 16 (* MSB=1 is dropped *);
  RETURN 255 - (sign + exponent * 16 + mantissa)
 END MuLawEncode;

 PROCEDURE MuLawDecode(ulaw: CARD8): INT16;
  VAR
   exponent, mantissa: CARD8;
   adjusted: INT16;
 BEGIN
  ulaw:= 255 - ulaw;
  exponent:= (ulaw DIV 16) MOD 8;
  mantissa:= (ulaw MOD 16) + 16 (* MSB *);
  adjusted:= mantissa;
  adjusted:= SHIFT(adjusted, (exponent + 3));
  DEC(adjusted, 128 + 4);
  IF ulaw >= 128) THEN RETURN adjusted ELSE RETURN -adjusted END
 END MuLawDecode;


 PROCEDURE XOR16(v1, v2: CARD16): CARD16;
 BEGIN
  RETURN CARD16(SET16(v1) / SET16(v2))
 END XOR16;

 PROCEDURE ALawEncode(s: INT16): CARD8;
  VAR
   sign, exponent, mantisse: CARD8;
   adjusted: CARD16;
 BEGIN
  IF s < 0 THEN sign:= 0; s:= -s ELSE sign:= 080H END;
  adjusted:= s; INC(s, 8);
  IF adjusted > 32767 THEN adjusted:= 32767 END;
  exponent:= numBits[adjusted DIV 256];
  mantissa:= SHIFT(adjusted, - (exponent + 4)) MOD 16;
  return XOR16(sign + exponent * 16 + mantissa, 055H)
 END ALawEncode;

 PROCEDURE ALawDecode(alaw: CARD8): INT16;
  VAR
   exponent, mantissa: CARD8;
   adjusted: INT16;
 BEGIN
  alaw:= XOR16(alaw, 055H);
  exponent:= (alaw DIV 16) MOD 8;
  mantissa:= (alaw MOD 16);
  IF exponent > 0 THEN INC(mantissa, 16) END;
  adjusted:= SHIFT(mantissa, exponent + 4);
  IF alaw >= 128 THEN RETURN -adjusted ELSE RETURN adjusted END
 END ALawDecode;


 TYPE
  StepSizeTable = ARRAY[0..88] OF CARD16;
  IndexAdjustTable = ARRAY[0..15] OF INT16;
  ImaState = RECORD
   index: INT16;
   previousValue: INT32;
  END;

 VAR
  stepSizeTable:= StepSizeTable{
   7, 8, 9, 10, 11, 12, 13, 14, 16, 17, 19, 21, 23, 25, 28, 31, 34,
   37, 41, 45, 50, 55, 60, 66, 73, 80, 88, 97, 107, 118, 130, 143,
   157, 173, 190, 209, 230, 253, 279, 307, 337, 371, 408, 449, 494,
   544, 598, 658, 724, 796, 876, 963, 1060, 1166, 1282, 1411, 1552,
   1707, 1878, 2066, 2272, 2499, 2749, 3024, 3327, 3660, 4026,
   4428, 4871, 5358, 5894, 6484, 7132, 7845, 8630, 9493, 10442,
   11487, 12635, 13899, 15289, 16818, 18500, 20350, 22385, 24623,
   27086, 29794, 32767
  };
  indexAdjustTable:= IndexAdjustTable{
   -1, -1, -1, -1,
   2, 4, 6, 8,
   -1, -1, -1, -1,
   2, 4,6, 8
  };

 PROCEDURE ImaAdpcmDecode(deltaCode: CARD8; VAR state: ImaState): INT16;
  VAR
   deltaSet: SET16;
   step, difference: INT16;
 BEGIN
  deltaSet:= SET16(deltaCode);
   (* Get the current step size *)
  step:= stepSizeTable[state.index];
   (* Compute the difference *)
  difference:= step DIV 8;
  IF (0 IN deltaSet) THEN INC(difference, step DIV 4) END;
  IF (1 IN deltaSet) THEN INC(difference, step DIV 2) END;
  IF (2 IN deltaSet) THEN INC(difference, step) END;
  IF (3 IN deltaSet) THEN difference:= -difference END;
   (* Build the new sample *)
  INC(state.previousValue, difference);
  IF (state.previousValue > 32767) THEN state.previousValue:= 32767 END;
  IF (state.previousValue < -32768) THEN state.previousValue:= -32768 END;
   (* Update the step for the next sample *)
  INC(state.index, indexAdjustTable[deltaCode]);
  IF (state.index < 0) THEN
   state.index:= 0
  ELSIF (state.index > 88) THEN
   state.index:= 88
  END;
  RETURN state.previousValue
 END ImaAdpcmDecode;

 PROCEDURE ImaAdpcmEncode(sample: INT16; VAR state: ImaState): CARD8;
  VAR
   diff: INT32;
   step, deltaCode: CARD16;
 BEGIN
  diff:= sample; DEC(diff, state.previousValue);
  step:= stepSizeTable[state.index];
  deltaCode:= 0;
  IF (diff < 0) THEN deltaCode:= 8; diff:= -diff END;
  IF diff >= step THEN INC(deltaCode, 4); DEC(diff, step) END;
  step:= step DIV 2;
  IF diff >= step THEN INC(deltaCode, 2); DEC(diff, step) END;
  step:= step DIV 2;
  IF diff >= step THEN INC(deltaCode) END;
  ImaAdpcmDecode(deltaCode, state); (* update state *)
  RETURN deltaCode
 END ImaAdpcmEncode;

