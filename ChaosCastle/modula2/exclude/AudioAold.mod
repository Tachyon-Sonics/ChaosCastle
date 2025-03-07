IMPLEMENTATION MODULE AudioA;

 FROM SYSTEM IMPORT ADR, ADDRESS, ASSEMBLE;
 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32, TagItem,
  TagItemPtr, NextTag;
 FROM ExecD IMPORT MemReqs, MemReqSet, msgPort, MsgPortPtr, write, quick,
  stop, start, IOFlagSet, Interrupt, InterruptPtr;
 FROM ExecL IMPORT AllocMem, FreeMem, WaitPort, GetMsg, AbortIO,
  OpenDevice, CloseDevice, SetIntVector, RemIntVector;
 FROM ExecSupport IMPORT CreateExtIO, DeleteExtIO, BeginIO;
 FROM Audio IMPORT audioName, free, allocate, noWait, IOAudio, IOAudioPtr;
 FROM GraphicsD IMPORT GfxBase, GfxBasePtr, DisplayFlags, DisplayFlagSet;
 FROM GraphicsL IMPORT graphicsBase;
 FROM Hardware IMPORT CiaaPraFlags, CiaaPraFlagSet, CIAA, ciaa, Custom, custom;
 FROM Checks IMPORT CheckMem, AddTermProc;
 IMPORT R;

 TYPE
  PlayerDataPtr = POINTER TO PlayerData;
  PlayerData = RECORD
   table: ADDRESS;
    (* structure passed to players in A1: *)
   next: PlayerDataPtr;
   gap, length: CARDINAL;
   buffer: ADDRESS;
  END;

 VAR
  ClkCst: CARD32;
  audioDev, curDev: CARD32;
  audioBuffer: ADDRESS;
  size: CARD32;
  queue: ARRAY[0..3] OF PlayerData;
  playing, empty: PlayerData; (* Interrupt.userData *)
  buffers: ARRAY[0..3] OF ADDRESS;
  current: CARD16;
  period, rate: CARDINAL;

 PROCEDURE GetAudioDev(): CARD32;
 BEGIN
  RETURN 0
 END GetAudioDev;

 PROCEDURE SetAudioDev(id: CARD32);
 BEGIN
 END SetAudioDev;

 PROCEDURE GetNextAudioDev(VAR id: CARD32): BOOLEAN;
 BEGIN
  IF curDev > 0 THEN
   curDev:= 0; RETURN FALSE
  ELSE
   id:= curDev; INC(curDev);
   RETURN TRUE
  END
 END GetNextAudioDev;

 PROCEDURE AskAudioDev(VAR id: CARD32): CARD8;
 BEGIN
  id:= 0; RETURN 1
 END AskAudioDev;

 PROCEDURE GetAudioMaxAttrs(id: CARD32; tags: TagItemPtr);
 BEGIN
  IF id <= 0 THEN
   LOOP
    WITH NextTag(tags)^ DO
     IF tag = 0 THEN EXIT
     ELSIF tag = aMIXFREQ THEN data:= 44100
     ELSIF tag = aMIXVOL THEN lint:= 14
     ELSIF tag = aNBCHAN THEN data:= 1
     ELSIF tag = aNBBALANCE THEN data:= 1
     ELSIF tag = aBUFSIZE THEN data:= 65536
     ELSE tag:= 0; data:= 0
    END
   END
  END
 END GetAudioMaxAttrs;

 PROCEDURE SetAudioAttrs(tags: TagItemPtr);
  VAR
   c: CARDINAL;
 BEGIN
  LOOP
   WITH NextTag(tags)^ DO
    IF tag = 0 THEN EXIT
    ELSIF tag = aMIXFREQ THEN
     period:= (ClkCst + data DIV 2) DIV data;
     rate:= (ClkCst + period DIV 2) DIV period;
     data:= rate
    ELSIF tag = aMIXVOL THEN data:= 14
    ELSIF tag = aNBCHAN THEN data:= 1
    ELSIF tag = aNBBALANCE THEN data:= 1
    ELSIF tag = aBUFSIZE THEN
     IF data > 65536 THEN data:= 65536 END;
     FOR c:= 0 TO 3 DO
      IF buffers[c] <> NIL THEN FreeMem(buffers[c], size) END
     END;
     FOR c:= 0 TO 3 DO
      IF data > 0 THEN
       buffers[c]:= AllocMem(data, MemReqSet{public});
       IF buffers[c] = NIL THEN data:= 0 END
      END
     END;
     size:= data
    ELSE tag:= 0; data:= 0
   END
  END
 END SetAudioAttrs;

 PROCEDURE Sample7(length{R.D0}: LONGCARD; ins{R.A0}, out{R.A1}, table{R.A6}: ADDRESS);
  (*$ EntryExitCode:= FALSE *)
  (* Low quality sampling; 7bit -> 7bit only *)
  (* Uses volume tables - very fast *)
 BEGIN
  ASSEMBLE(
	MOVEM.L	D2-D7/A2-A4/A6,-(A7)
	MOVEA.L	A0,A5
nxtch:	MOVEM.L	(A5)+,A0/A2/A3
	MOVE.L	A0,D7
	BEQ	finish
	MOVEQ	#0,D1		(* 0 bytes processed *)
	MOVEQ	#0,D4		(* volume cd = period cd = 0 *)
	MOVEQ	#0,D2
	MOVE.W	10(A5),D2	(* begin offset *)
updt:	CMP.W	6(A5),D2	(* loop end reached *)
	BMI	ok		(* no *)
	MOVE.W	4(A5),D2	(* loop start (hiW = frac: not changed *)
vol:	SUB.W	D1,D4		(* get next volume ? *)
	BNE	per		(* no *)
	MOVEQ	#0,D5
	MOVE.W	(A3)+,D5	(* next volume *)
	LSL.W	#6,D5		(* volume x 64 *)
	MOVE.W	(A3)+,D4	(* counter *)
per:	SWAP	D4
	SUB.W	D1,D4		(* get next rate ? *)
	BNE	len		(* no *)
	MOVE.L	(A2)+,D3	(* new rate *)
	DIVU	rate(A4),D3
	MOVE.L	D3,D7
	SWAP	D3
	CLR.W	D7
	DIVU	rate(A4),D7
	MOVE.W	D7,D3		(* new rate x 65536 DIV sampling rate *)
	MOVE.L	(A2)+,D6	(* counter *)
	SWAP	D3		(* fp | ip part of incrementer *)
len:	SUB.W	D1,D0
	BEQ	nxtch		(* all bytes processed; next chan *)
	 (* # bytes to process = min(loop, length, volume cd, period cd): *)
	MOVE.W	6(A5),D6	(* loop end *)
	MOVE.L	D2,D7
	SWAP	D7		(* current pos *)
	SUB.W	D7,D6		(* # loop length, in src rate *)
	MULU	rate(A4),D6
	DIVU	-8(A2),D6	(* # loop length, in dst sampling rate *)
	BVC	tstp
	MOVEQ	#-1,D6		(* > 65535 ? -> use 65535 *)
tstp:	CMP.W	D4,D6
	BMI	tstv
	MOVE.W	D4,D6		(* period cd < # bytes *)
tstv:	SWAP	D4
	CMP.W	D4,D6
	BMI	tstln
	MOVE.W	D4,D6		(* volume cd < # bytes *)
tstln:	CMP.W	D0,D6
	BMI	rdy
	MOVE.W	D0,D6		(* dst length < # bytes *)
rdy:	MOVE.W	D6,D1
	SWAP	D1
	MOVE.L	A0,D1
	ANDI.W	#$3,D1
	BEQ	rdyl
	MOVE.W	D1,-(A7)
	SUBQ.W	#1,D1
bytei:	MOVE.B	0(A0,D2.W),D7
	ADD.L	D3,D2
	ADDX.W	D0,D2
	MOVE.B	0(A6,D7.L),D6
	ADD.B	D6,(A1)+
	DBRA	D1,bytei
	MOVE.L	D1,D6
	SWAP	D6
	SUB.W	(A7)+,D6
rdyl:	MOVE.W	D6,D1		(* # bytes left *)
	LSR.W	#2,D1		(* # longs *)
	BEQ	rdyr
	SUBQ.W	#1,D1
	SWAP	D0		(* length | 0 (for ADDX) *)
long:	MOVE.L	D5,D7		(* vol * 64 *)
	MOVE.B	0(A0,D2.W),D7	(* input byte + vol * 64 (6 upper bits) *)
	ADD.L	D3,D2		(* faster than SWAP D2/ADD.L D3,D2/SWAP D2 *)
	ADDX.W	D0,D2		(* D2 & D3 are already swapped; D0.W = 0 *)
	MOVE.B	0(A6,D7.L),D6	(* resulting byte (b1) from the table *)
	LSL.W	#8,D6
	MOVE.B	0(A0,D2.W),D7
	ADD.L	D3,D2
	ADDX.W	D0,D2
	MOVE.B	0(A6,D7.L),D6	(* b2 *)
	SWAP	D6
	MOVE.B	0(A0,D2.W),D7
	ADD.L	D3,D2
	ADDX.W	D0,D2
	MOVE.B	0(A6,D7.L),D6	(* b3 *)
	LSL.W	#8,D6
	MOVE.B	0(A0,D2.W),D7
	ADD.L	D3,D2
	ADDX.W	D0,D2
	MOVE.B	0(A6,D7.L),D6	(* D6 = b1b2b3b4 *)
	ADD.L	D6,(A1)+	(* add the 4 7-bit bytes simultaneously *)
	DBRA	D1,long
	MOVE.L	D1,D6
	SWAP	D6
rdyr:	MOVE.W	D6,D1		(* # bytes *)
	ANDI.W	#$3,D1		(* # bytes MOD 4 *)
	BEQ	end
	SUBQ.W	#1,D1		(* process remaining bytes *)
byter:	MOVE.B	0(A0,D2.W),D7
	ADD.L	D3,D2
	ADDX.W	D0,D2
	MOVE.B	0(A6,D7.L),D6
	ADD.B	D6,(A1)+
	DBRA	D1,byter
end:	SWAP	D0		(* length *)
	SWAP	D1		(* # bytes processed *)
	BRA	updt
finish:	MOVEM.L	(A7)+,D2-D7/A2-A4/A6
	RTS
  END)
 END Sample7;

 PROCEDURE SampleMed(length{R.D0}: LONGCARD; ins{R.A0}, out{R.A1}: ADDRESS; flags{R.D5}: BITSET);
  (*$ EntryExitCode:= FALSE *)
  (* Medium quality sampling - volumes adjusted before sampling *)
  (* D0: length | period cd
     D1: right vol | left vol
     D2: input offset
     D3: input offset adder
     D4: volume cd | stereo cd
     D5: flags | loop end
     A0: input
     A1: output
     A2: periods
     A3: volumes
     A5: current data
     A6: stereos
    datas:
     input (32)
     periods (32)
     volumes (32)
     stereos (32)
     loop start (16)
     loop end (16)
     flags (16)
     begin offset (16)
   *)
 BEGIN
  ASSEMBLE(
 	MOVEM.L	D2-D7/A2-A6,-(A7)
	SWAP	D5
	SUBQ.L	#1,D0
	CLR.W	D5
	MOVE.W	D0,-(A7)
	MOVEA.L	A0,A5
nxtch:	MOVEM.L	(A5)+,A0/A2/A3/A6
	MOVE.L	A0,D7
	BEQ	finish
	MOVEQ	#0,D0
	MOVE.W	(A7),D0
	MOVEQ	#0,D2
	MOVE.W	6(A5),D2
	MOVEQ	#0,D4
	SWAP	D2
	MOVE.W	2(A5),D5
	SWAP	D0
	SWAP	D5
	ANDI.W	#$1C,D5
	OR.W	4(A5),D5
byte:	SWAP	D0
	DBRA	D0,addrt
	MOVE.L	(A2)+,D3	(* new rate *)
	MOVEQ	#0,D7
	DIVU	rate(A4),D3
	MOVE.L	D3,D7
	SWAP	D3
	CLR.W	D7
	DIVU	rate(A4),D7
	MOVE.W	D7,D3		(* new adder *)
	MOVE.L	(A2)+,D6
	MOVE.W	D6,D0		(* count *)
addrt:	SWAP	D0
	ADD.L	D3,D2
	SWAP	D4
	DBRA	D4,chkst
	MOVE.W	(A3)+,D1	(* new volume *)
	MOVE.W	(A3)+,D4	(* count *)
	MOVEQ	#0,D7
vols:	MOVE.B	-3(A6),D7	(* stereo *)
	EXT.W	D7
	BMI	left
right:	NEG.W	D7
	ADDI.W	#128,D7
	MULU	D1,D7
	LSR.L	#7,D7
	BRA	comb
left:	ADDI.W	#128,D7
	MULU	D1,D7
	LSR.L	#7,D7
	EXG	D1,D7
comb:	SWAP	D7
	ADD.L	D7,D1
chkst:	SWAP	D4
	DBRA	D4,read
	CLR.W	D4
	MOVE.B	(A6)+,D4	(* new stereo *)
	ADDQ.L	#3,A6
	ADDQ.W	#1,D4
	SWAP	D4
	MOVE.W	-4(A3),D4	(* volume *)
	BRA	vols
read:	MOVEQ	#0,D7
	SWAP	D2
	SWAP	D5
	CMP.W	D5,D2
	BMI	ok
	MOVEQ	#0,D2
	MOVE.W	(A5),D2
ok:	SWAP	D5
	BTST	#0,D5
	BEQ	l16
l8:	MOVE.B	0(A0,D2.W),D7
	EXT.W	D7
	MULS	D1,D7
	BRA	rr
l16:	MOVE.W	0(A0,D2.W),D7
	ADDQ.L	#1,A0
	MULS	D1,D7
	ASR.L	#8,D7
rr:	MOVE.W	D7,D6
	BTST	#1,D5
	BNE	write
	SWAP	D1
	BTST	#0,D5
	BEQ	r16
r8:	MOVE.B	1(A0,D2.W),D6
	EXT.W	D6
	MULS	D1,D6
	BRA	frr
r16:	MOVE.W	1(A0,D2.W),D6
	ADDQ.L	#1,A0
	MULS	D1,D6
	ASR.L	#8,D6
frr:	ADDQ.L	#1,A0
	SWAP	D1
write:	BSR	wr
	BTST	#4,D5
	BNE	next
	MOVE.W	D6,D7
	BSR	wr
next:	DBRA	D0,byte
	ADDQ.L	#8,A5
	BSET	#2,D5
	BRA	nxtch
wr:	BTST	#3,D5
	BEQ	wr16
wr8:	ASR.W	#8,D7
	BTST	#2,D5
	BNE	cp8
	ADD.B	D7,(A1)+
	RTS
cp8:	MOVE.B	D7,(A1)+
	RTS
wr16:	BTST	#2,D5
	BNE	cp16
	ADD.W	D7,(A1)+
	RTS
cp16:	MOVE.W	D7,(A1)+
	RTS
finish:	MOVE.W	(A7)+,D0
	MOVEM.L	(A7)+,D2-D7/A2-A6
	RTS
  END)
 END SampleMed;

 PROCEDURE Mix7(buffer: ADDRESS; VAR sources: ARRAY OF ADDRESS);
  (*$ EntryExitCode:= FALSE *)
  (* low quality 7bit mixing - very fast *)
 BEGIN
  ASSEMBLE(
	LINK	A5,#0
	MOVEM.L	D2-D7/A2-A4/A6,-(A7)
	MOVEA.L	buffer(A5),A1
	MOVE.L	A1,D7
	BNE	ln
	MOVEA.L	audioBuffer(A4),A1
ln:	MOVEA.L	A1,A4
	MOVE.L	-4(A1),D0
	LSR.L	#5,D0
	SUBQ.L	#1,D0
	MOVEA.L	sources(A5),A6
	MOVEQ	#32,D1
	MOVEA.L	(A6)+,A0
	MOVEA.L	D1,A3
copy:	MOVEM.L	(A0)+,D1-D7/A2
	MOVEM.L	D1-D7/A2,(A1)
	ADDA.L	A3,A1
	DBRA	D0,copy
	MOVE.L	sources+4(A5),D1
	MOVE.L	-4(A1),D0
	LSR.L	#5,D0
	SUBQ.L	#1,D0
	MOVEA.L	D0,A5
	DBRA	D1,nxts
	BRA	finish
nxts:	MOVEA.L	A4,A1
	MOVEA.L	(A6)+,A0
	MOVE.L	A5,D0
add:	MOVEM.L	(A0)+,D2-D7/A2-A3
	ADD.L	D2,(A1)+
	ADD.L	D3,(A1)+
	ADD.L	D4,(A1)+
	ADD.L	D5,(A1)+
	ADD.L	D6,(A1)+
	ADD.L	D7,(A1)+
	MOVE.L	A2,D2
	ADD.L	D2,(A1)+
	MOVE.L	A3,D3
	ADD.L	D3,(A1)+
	DBRA	D0,add
	DBRA	D1,nxts
finish:	MOVEA.L	(A7)+,A5
	MOVEM.L	(A7)+,D2-D7/A2-A4/A6
	RTS
  END)
 END Mix7;

 PROCEDURE Play8M;
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	SUBQ.W	#1,(A1)+
	BEQ	next
byte:	MOVEA.L	(A1),A5
	MOVE.W	(A5)+,D0
	MOVE.W	D0,custom.aud[0].acdat(A0)
	MOVE.W	D0,custom.aud[1].acdat(A0)
	MOVE.W	D0,custom.aud[2].acdat(A0)
	MOVE.W	D0,custom.aud[3].acdat(A0)
	MOVE.L	A5,(A1)
	RTS
next:	SUBQ.L	#8,A1
	MOVEA.L	(A1),A6
	MOVE.L	(A6)+,(A1)+
	MOVE.L	(A6)+,(A1)+
	MOVE.L	(A6)+,(A1)+
	BRA	byte
  END) (* ~200 ticks -> 40kHz *)
 END Play8M;

 PROCEDURE Play8S;
  (*$ EntryExitCode:= FALSE *)
 BEGIN
  ASSEMBLE(
	SUBQ.W	#1,(A1)+
	BEQ	next
byte:	MOVEA.L	(A1),A5
	MOVE.L	(A5)+,D0
	ROR.L	#8,D0
	ROR.W	#8,D0
	MOVE.L	A5,(A1)
	ROL.L	#8,D0
	MOVE.W	D0,custom.aud[0].acdat(A0)
	MOVE.W	D0,custom.aud[3].acdat(A0)
	SWAP	D0
	MOVE.W	D0,custom.aud[1].acdat(A0)
	MOVE.W	D0,custom.aud[2].acdat(A0)
	RTS
next:	SUBQ.L	#8,A1
	MOVEA.L	(A1),A6
	MOVE.L	(A6)+,(A1)+
	MOVE.L	(A6)+,(A1)+
	MOVE.L	(A6)+,(A1)+
	BRA	byte
  END) (* ~286 -> 32kHz *)
 END Play8S;

 PROCEDURE Play16S;
  (*$ EntryExitCode:= FALSE *)
  (* 14bit calibrated *)
 BEGIN
  ASSEMBLE(
  	SUBQ.W	#1,(A1)+
  	BEQ	next
  byte:	MOVEA.L	(A1),A5
  	MOVE.L	(A5)+,D1
  	ANDI.L	#$FFFCFFFC,D1
  	MOVE.L	(A5)+,D0
  	ANDI.L	#$FFFCFFFC,D0
  	MOVE.L	A5,(A1)
  	MOVEA.L	D0,A5
  	MOVEA.L	-12(A1),A6
  	MOVE.L	0(A6,D1.W),D0
  	LSL.L	#8,D0
  	MOVE.W	A5,D1
  	ADD.L	0(A6,D1.W),D0
  	MOVE.W	D0,custom.aud[0].acdat(A0)
  	SWAP	D1
  	SWAP	D0
  	MOVEA.W	D1,A5
  	MOVE.W	D0,custom.aud[3].acdat(A0)
	MOVE.L	A5,D1
	MOVE.L	0(A6,D1.W),D0
	LSL.L	#8,D0
	SWAP	D1
	ADD.L	0(A6,D1.W),D0
	MOVE.W	D0,custom.aud[1].acdat(A0)
	SWAP	D0
	MOVE.W	D0,custom.aud[2].acdat(A0)
	RTS
next:	SUBQ.L	#8,A1
	MOVEA.L	(A1),A6
	MOVE.L	(A6)+,(A1)+
	MOVE.L	(A6)+,(A1)+
	MOVE.L	(A6)+,(A1)+
	BRA	byte
  END) (* ~472 -> 22kHz *)
 END Play16S;

 PROCEDURE Close;
  VAR
   c: CARDINAL;
 BEGIN
  FOR c:= 0 TO 3 DO
   IF (size > 0) AND (buffers[c] <> NIL) THEN
    FreeMem(buffers[c], size);
    buffers[c]:= NIL
   END
  END;
  IF empty.size <> 0 THEN
   FreeMem(empty.buffer, size);
   empty.size:= 0
  END
 END Close;

BEGIN

 IF pal IN graphicsBase^.displayFlags THEN
  ClkCst:= 3546895
 ELSE
  ClkCst:= 3579545
 END;
 empty.buffer:= AllocMem(1024, MemReqSet{public, memClear});
 CheckMem(empty.buffer);
 empty.length:= 1024;
 empty.next:= ADR(empty);
 AddTermProc(Close);

END AudioA.
