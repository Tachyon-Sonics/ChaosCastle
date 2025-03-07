IMPLEMENTATION MODULE AudioA;

 FROM SYSTEM IMPORT ADR, ADDRESS, ASSEMBLE, LONGSET, CAST;
 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32, SET16,
  TagItem, TagItemPtr, NextTag;
 FROM ExecD IMPORT Interrupt, InterruptPtr, MemReqSet, TaskPtr, IntFlags;
 FROM ExecL IMPORT SetIntVector, AllocMem, FreeMem, Signal, Wait, FindTask;
 FROM Hardware IMPORT Custom, custom, DmaFlags, DmaFlagSet, AdkFlags,
  AdkFlagSet, ciaa, CIAA, CiaaPraFlags, CiaaPraFlagSet, IntFlagSet;
 FROM GraphicsD IMPORT GfxBase, GfxBasePtr, pal, DisplayFlagSet;
 FROM GraphicsL IMPORT graphicsBase;
 FROM DosD IMPORT ctrlE;
 IMPORT R;
  FROM Arts IMPORT BreakPoint;


  (** Non-DMA playing *)
 CONST
  AUDINTF = CAST(CARDINAL, IntFlagSet{aud0i, aud1i, aud2i, aud3i});
  AUDINTM = CAST(CARDINAL, IntFlagSet{aud2i, aud3i});

 TYPE
  NonDmaDataPtr = POINTER TO NonDmaData;
  NonDmaData = RECORD
   next: NonDmaDataPtr;
   num: CARD16; (* Buffer number *)
   size: CARD16; (* Interrupt^.data points here *)
   data: ADDRESS; (* Wave *)
   task: TaskPtr; (* Task to signal when done *)
  END;
  NonDmaArr = ARRAY[0..15] OF NonDmaData;
  NonDmaArrPtr = POINTER TO NonDmaArr;

 VAR
  zeroBuffer, nonDmaData: NonDmaData;
  myInt: Interrupt;
  oldInt: ADDRESS;
  buffer: NonDmaArrPtr;
  bufferPos: CARD16;
  buffCnt: CARD16;
  waveSize: CARD16;
  bits, chans: CARD16;
  NonDmaPlayer: PROC;
  ClkCst: CARD32;


 PROCEDURE Play8M; (* 8-bit mono *)
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	MOVE.W	#AUDINTM,Custom.intreq(A0)	(* Clear interrupts *)
	SUBQ.W	#1,(A1)+			(* DEC(size) *)
	BCS	next				(* IF size <> 0 *)
word:	MOVEA.L	(A1),A5				(* Get data *)
	MOVE.W	(A5)+,D0			(* Next two samples *)
	MOVE.W	D0,Custom.audch2.acdat(A0)	(* Fill audio data registers *)
	MOVE.W	D0,Custom.audch3.acdat(A0)
	MOVE.L	A5,(A1)				(* Update data pointer *)
	RTS
next:	SUBQ.L	#8,A1				(* begin of struct *)
	MOVEA.L	(A1),A5				(* next *)
	MOVE.L	(A5)+,(A1)+			(* copy next *)
	MOVE.L	(A5)+,(A1)+			(* copy gap/size *)
	MOVE.L	(A5),(A1)			(* copy data *)
	BNE	word
	MOVE.W	#AUDINTF,Custom.intena(A0)	(* Disable audio interrupt *)
	MOVE.W	#AUDINTM,Custom.intreq(A0)	(* Clear interrupts *)
	CLR.W	-2(A1)				(* Re-zero length *)
	MOVEA.L	4(A1),A1			(* Task to signal *)
	MOVE.L	#$4000,D0			(* DosD.ctrlE *)
	JSR	Signal(A6)			(* Signal() *)
	RTS
   END)	(* 140 *)
 END Play8M;

 PROCEDURE Play8S; (* 8-bit stereo *)
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	SUBQ.W	#1,(A1)+
	BCS	next
word:	MOVEA.L	(A1),A5
	MOVE.L	(A5)+,D0	(* l1 r1 l2 r2 *)
	MOVE.L	D0,D1		(* l1 r1 l2 r2 *)
	MOVE.L	A5,(A1)
	LSR.L	#8,D1		(* 00 l1 r1 l2 *)
	MOVE.B	D0,D1		(* 00 l1 r1 r2 *)
	MOVE.W	D1,Custom.audch2.acdat(A0)
	MOVE.W	D0,D1		(* 00 l1 l2 r2 *)
	LSR.L	#8,D1		(* 00 00 l1 l2 *)
	MOVE.W	D1,Custom.audch3.acdat(A0)
	MOVE.W	#AUDINTM,Custom.intreq(A0)	(* Clear interrupts, ch 2/3 *)
	RTS
next:	SUBQ.L	#8,A1				(* begin of struct *)
	MOVEA.L	(A1),A5				(* next *)
	MOVE.L	(A5)+,(A1)+			(* copy next *)
	MOVE.L	(A5)+,(A1)+			(* copy gap/size *)
	MOVE.L	(A5),(A1)			(* copy data *)
	BNE	word
	MOVE.W	#AUDINTF,Custom.intena(A0)	(* Disable audio interrupt *)
	MOVE.W	#AUDINTM,Custom.intreq(A0)	(* Clear interrupts *)
	CLR.W	-2(A1)				(* Re-zero length *)
	MOVEA.L	4(A1),A1			(* Task to signal *)
	MOVE.L	#$4000,D0			(* DosD.ctrlE *)
	JSR	Signal(A6)			(* Signal() *)
	RTS
  END)	(* 214 *)
 END Play8S;

 PROCEDURE Play16M; (* 16-bit mono *)
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	SUBQ.W	#1,(A1)+
	BCS	next
word:	MOVEA.L	(A1),A5
	MOVE.L	(A5)+,D0	(* V1 v1 V2 v2 *)
	MOVE.L	D0,D1		(* V1 v1 V2 v2 *)
	MOVE.L	A5,(A1)
	LSR.L	#8,D1		(* 00 V1 v1 V2 *)
	MOVE.B	D0,D1		(* 00 V1 v1 v2 *)
	ASR.W	#2,D1		(* 16bit -> 14bit *)
	MOVE.W	D1,Custom.audch0.acdat(A0)
	MOVE.W	D1,Custom.audch1.acdat(A0)
	MOVE.W	D0,D1		(* 00 V1 V2 v2 *)
	LSR.L	#8,D1		(* 00 00 V1 V2 *)
	MOVE.W	D1,Custom.audch2.acdat(A0)
	MOVE.W	D1,Custom.audch3.acdat(A0)
	MOVE.W	#AUDINTF,Custom.intreq(A0)	(* Clear interrupts *)
	RTS
next:	SUBQ.L	#8,A1				(* begin of struct *)
	MOVEA.L	(A1),A5				(* next *)
	MOVE.L	(A5)+,(A1)+			(* copy next *)
	MOVE.L	(A5)+,(A1)+			(* copy gap/size *)
	MOVE.L	(A5),(A1)			(* copy data *)
	BNE	word
	MOVE.W	#AUDINTF,Custom.intena(A0)	(* Disable audio interrupt *)
	MOVE.W	#AUDINTF,Custom.intreq(A0)	(* Clear interrupts *)
	CLR.W	-2(A1)				(* Re-zero length *)
	MOVEA.L	4(A1),A1			(* Task to signal *)
	MOVE.L	#$4000,D0			(* DosD.ctrlE *)
	JSR	Signal(A6)			(* Signal() *)
	RTS
  END)	(* 260 *)
 END Play16M;

 PROCEDURE Play16S; (* 16-bit stereo *)
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	SUBQ.W	#1,(A1)+
	BCS	next
word:	MOVEA.L	(A1),A5
	MOVE.L	(A5)+,D0	(* L1 l1 R1 r1 *)
	MOVE.L	(A5)+,D1	(* L2 l2 R2 r2 *)
	MOVE.L	A5,(A1)
	MOVEA.L	D2,A5		(* Save this register *)
	ROL.W	#8,D0		(* L1 l1 r1 R1 *)
	MOVE.W	D1,D2		 (* R2 r2 *)
	MOVE.B	D0,D2		 (* R2 R1 *)
	ROL.W	#8,D2		 (* R1 R2 *)
	MOVE.W	D2,Custom.audch2.acdat(A0)
	MOVE.B	D1,D0		 (* r1 r2 *)
	ASR.W	#2,D0		 (* 16bit -> 14bit *)
	MOVE.W	D0,Custom.audch1.acdat(A0)
	SWAP	D0		 (* L1 l1 *)
	SWAP	D1		 (* L2 l2 *)
	ROL.W	#8,D0		(* l1 L1 *)
	MOVE.W	D1,D2		 (* L2 l2 *)
	MOVE.B	D0,D2		 (* L2 L1 *)
	MOVE.B	D1,D0		 (* l1 l2 *)
	ASR.W	#2,D0		 (* 16bit -> 14bit *)
	MOVE.W	D0,Custom.audch0.acdat(A0)
	ROL.W	#8,D2		 (* L1 L2 *)
	MOVE.W	D2,Custom.audch3.acdat(A0)
	MOVE.L	A5,D2		(* Restore this register *)
	MOVE.W	#AUDINTF,Custom.intreq(A0)	(* Clear interrupts *)
	RTS
next:	SUBQ.L	#8,A1				(* begin of struct *)
	MOVEA.L	(A1),A5				(* next *)
	MOVE.L	(A5)+,(A1)+			(* copy next *)
	MOVE.L	(A5)+,(A1)+			(* copy gap/size *)
	MOVE.L	(A5),(A1)			(* copy data *)
	BNE	word
	MOVE.W	#AUDINTF,Custom.intena(A0)	(* Disable audio interrupt *)
	MOVE.W	#AUDINTF,Custom.intreq(A0)	(* Clear interrupts *)
	CLR.W	-2(A1)				(* Re-zero length *)
	MOVEA.L	4(A1),A1			(* Task to signal *)
	MOVE.L	#$4000,D0			(* DosD.ctrlE *)
	JSR	Signal(A6)			(* Signal() *)
	RTS
  END)	(* 366 *)
 END Play16S;


 PROCEDURE QueryAudioMode(tags: TagItemPtr; index: CARD16;
            VAR what: TagItem; VAR write: LONGINT);
 BEGIN
 END QueryAudioMode;

 PROCEDURE SetAudioMode(tags: TagItemPtr): BOOLEAN;
  VAR
   freq, period: CARD16;
   c: CARD16;
 BEGIN
   (* Free memory *)
  IF buffer <> NIL THEN
   FreeMem(buffer, SIZE(NonDmaData) * buffCnt);
   buffer:= NIL; buffCnt:= 0;
    (* Release interrupt vector *)
   IGNORE SetIntVector(ORD(aud3i), oldInt)
  END;
  IF tags = NIL THEN RETURN TRUE END;
  freq:= 44100;
  bits:= 16;
  chans:= 2;
  waveSize:= 4096;
  buffCnt:= 2;
  LOOP
   WITH NextTag(tags)^ DO
    IF tag = 0 THEN EXIT
    ELSIF tag = aReadFreq THEN freq:= data
    ELSIF tag = aReadBits THEN bits:= data
    ELSIF tag = aReadChans THEN chans:= data
    ELSIF tag = aBufferLength THEN waveSize:= data
    ELSIF tag = aBufferCount THEN buffCnt:= data
    END
   END
  END;
  IF (freq < 2000) OR (freq > 100000) OR ((bits <> 8) AND (bits <> 16)) OR
     (chans < 1) OR (chans > 2) OR (waveSize < 16) OR ODD(waveSize) OR
     (buffCnt < 2) THEN
   RETURN FALSE (* Not acceptable *)
  END;
  waveSize:= waveSize DIV 2;
  period:= (ClkCst + freq DIV 2) DIV freq;
   (* Alloc memory *)
  buffer:= AllocMem(SIZE(NonDmaData) * buffCnt, MemReqSet{});
  IF buffer = NIL THEN RETURN FALSE END;
  FOR c:= 0 TO buffCnt - 1 DO
   buffer^[c].task:= FindTask(NIL);
   buffer^[c].num:= c
  END;
  bufferPos:= 0;
   (* Init structs *)
  zeroBuffer.task:= FindTask(NIL);
  zeroBuffer.next:= ADR(zeroBuffer);
  zeroBuffer.num:= MAX(CARD16);
  zeroBuffer.size:= 0;
  zeroBuffer.data:= NIL;
  nonDmaData:= zeroBuffer;
  IF bits = 8 THEN
   IF chans = 1 THEN
    NonDmaPlayer:= Play8M
   ELSE
    NonDmaPlayer:= Play8S
   END
  ELSIF chans = 1 THEN
   NonDmaPlayer:= Play16M
  ELSE
   NonDmaPlayer:= Play16S
  END;
 (* Init Interrupt handler *)
  myInt.node.name:= ADR("Non-DMA paula audio player V1.0 by Nicky");
  myInt.node.pri:= 0;
  myInt.data:= ADR(nonDmaData.size);
  myInt.code:= NonDmaPlayer;
  oldInt:= SetIntVector(ORD(aud3i), ADR(myInt));
 (* Init Audio Hardware *)
   (* Disable audio interrupts - if not done by handling a spurious interrupt *)
  custom.intena:= IntFlagSet{aud0i, aud1i, aud2i, aud3i};
   (* Disable audio DMA *)
  custom.dmacon:= DmaFlagSet{aud0, aud1, aud2, aud3};
   (* Init channels *)
  custom.audch0.aclen:= 0; custom.audch0.acper:= period;
  custom.audch0.aclen:= 1; custom.audch1.acper:= period;
  custom.audch0.aclen:= 2; custom.audch2.acper:= period;
  custom.audch0.aclen:= 3; custom.audch3.acper:= period;
  custom.audch0.acvol:= 1;
  custom.audch1.acvol:= 1;
  custom.audch2.acvol:= 64;
  custom.audch3.acvol:= 64;
   (* Init modulations *)
  custom.adkcon:= AdkFlagSet{use0v1, use1v2, use2v3, use3vn, use0p1, use1p2, use2p3, use3pn};
   (* Disable audio filter *)
  INCL(ciaa.pra, led);
  RETURN TRUE
 END SetAudioMode;

 PROCEDURE AllocWave(): ADDRESS;
  VAR
   size{R.D7}: CARD16;
 BEGIN
  size:= waveSize * 2;
  IF bits = 16 THEN INC(size, size) END;
  IF chans = 2 THEN INC(size, size) END;
  RETURN AllocMem(size, MemReqSet{})
 END AllocWave;

 PROCEDURE FreeWave(VAR wave: ADDRESS);
  VAR
   size{R.D7}: CARD16;
 BEGIN
  size:= waveSize * 2;
  IF bits = 16 THEN INC(size, size) END;
  IF chans = 2 THEN INC(size, size) END;
  IF wave <> NIL THEN
   FreeMem(wave, size);
   wave:= NIL
  END
 END FreeWave;

 PROCEDURE PlayBuffer(wave: ADDRESS; VAR done, queued: BOOLEAN);
  VAR
   buff{R.A2}: NonDmaDataPtr;
   prev{R.D7}: CARD16;
 BEGIN
  prev:= bufferPos;
  IF bufferPos = 0 THEN bufferPos:= buffCnt END;
  DEC(bufferPos);
  IF nonDmaData.num = bufferPos THEN
   done:= FALSE; bufferPos:= prev; RETURN
  END;
  buff:= ADR(buffer^[bufferPos]);
  WITH buff^ DO
   next:= ADR(zeroBuffer);
   size:= waveSize;
   data:= wave
  END;
  buffer^[prev].next:= buff; (* Link at the end *)
  IF nonDmaData.num = prev THEN
    (* Currently playing last one *)
   nonDmaData.next:= buff (* Link next *)
  END;
  IF nonDmaData.num = MAX(CARD16) THEN
    (* Not started *)
   queued:= FALSE;
   nonDmaData:= buff^;
    (* Enable interrupt, channel 3 *)
   custom.intena:= IntFlagSet{aud3i, intSet};
   custom.audch0.acdat:= 0;
   custom.audch1.acdat:= 0;
   custom.audch2.acdat:= 0;
   custom.audch3.acdat:= 0 (* This launch Non-Dma audio *)
  ELSE
   queued:= TRUE
  END
 END PlayBuffer;

 PROCEDURE StopPlay;
 BEGIN
   (* Disable interrupt *)
  custom.intena:= IntFlagSet{aud0i, aud1i, aud2i, aud3i};
  nonDmaData:= zeroBuffer;
  custom.audch0.acdat:= 0;
  custom.audch1.acdat:= 0;
  custom.audch2.acdat:= 0;
  custom.audch3.acdat:= 0
 END StopPlay;

 PROCEDURE WaitAudioQueue;
 BEGIN
  WHILE nonDmaData.num <> MAX(CARD16) DO
   IGNORE Wait(LONGSET{ctrlE})
  END
 END WaitAudioQueue;

 PROCEDURE AudioQueueFinished(): BOOLEAN;
 BEGIN
  RETURN nonDmaData.num = MAX(CARD16)
 END AudioQueueFinished;

BEGIN

 IF pal IN graphicsBase^.displayFlags THEN
  ClkCst:= 3546895
 ELSE
  ClkCst:= 3579545
 END;

END AudioA.
