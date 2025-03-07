MODULE AudioTest;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM AudioA IMPORT SetAudioMode, AllocWave, FreeWave, PlayBuffer,
  WaitAudioQueue, aReadFreq, aReadBits, aReadChans, aBufferLength, aBufferCount;
 FROM Memory IMPORT TAG5;
 FROM Trigo IMPORT SIN, COS, SQRT;
 FROM Terminal IMPORT WriteString, WriteLn;

 VAR
  wave: ADDRESS;
  wr: POINTER TO SHORTINT;
  c: INTEGER;
  done, queued: BOOLEAN;

BEGIN

 WriteString("Setting audio mode"); WriteLn;
 IF SetAudioMode(TAG5(aReadFreq, 32000, aReadBits, 8, aReadChans, 1,
                      aBufferLength, 16000, aBufferCount, 2)) THEN
  WriteString("Allocating wave"); WriteLn;
  wave:= AllocWave();
  IF wave <> NIL THEN
   WriteString("Initialising wave"); WriteLn;
   wr:= wave;
   FOR c:= 0 TO 15999 DO
    wr^:= SIN((c MOD 40) * 9) / 9;
    INC(wr);
   END;
   WriteString("Playing buffer"); WriteLn;
   PlayBuffer(wave, done, queued);
   IF done THEN WriteString("done"); WriteLn END;
   IF queued THEN WriteString("queued"); WriteLn END;
   WriteString("Waiting termination"); WriteLn;
   WaitAudioQueue;
   WriteString("Freeing wave"); WriteLn;
   FreeWave(wave)
  END;
  WriteString("Freeing ressources"); WriteLn;
  IGNORE SetAudioMode(NIL);
 END;

END AudioTest.


