IMPLEMENTATION MODULE GrotteNoSoundLib;

 (*$ LargeVars:= FALSE StackParms:= FALSE Volatile:= FALSE *)

 (*$
      StackChk:= FALSE RangeChk:= FALSE OverflowChk:= FALSE
      NilChk:= FALSE CaseChk:= FALSE
  *)

 FROM SYSTEM IMPORT ADR, ADDRESS, ASSEMBLE, CAST;
 FROM Arts IMPORT dosCmdBuf, dosCmdLen, Terminate;
 FROM ExecD IMPORT Library, LibFlags;
 FROM ExecL IMPORT Remove, FreeMem;
 IMPORT R;
 FROM GrotteBase IMPORT OBJECT;

 CONST
   revision = 0;

 VAR
  (*$ LongAlign:=TRUE *)
  myLib: GrotteNoSoundBasePtr;

 (*######################################################################*)
 CONST
   delOrd = ORD(delExp);

 PROCEDURE LibOpen(myLib{R.A6}: GrotteNoSoundBasePtr): ADDRESS;
  (*$ EntryExitCode:=FALSE *)
 BEGIN
   ASSEMBLE(
	ADDQ.W	#1,Library.openCnt(A6)
	BCLR	#delOrd,Library.flags(A6)
	MOVE.L	A6,D0
	RTS
   END);
 END LibOpen;

 PROCEDURE LibClose(myLib{R.A6}: GrotteNoSoundBasePtr): ADDRESS;
  (*$ EntryExitCode:=FALSE *)
 BEGIN
   ASSEMBLE(
	MOVEQ	#0,D0
	SUBQ.W	#1,Library.openCnt(A6)
	BNE.S	noExp
	BTST	#delOrd,Library.flags(A6)
	BEQ.S	noExp
	BSR.S	LibExpunge
 noExp:
	RTS
   END);
 END LibClose;

 PROCEDURE LibExpunge(myLib{R.A6}: GrotteNoSoundBasePtr): ADDRESS;
  (*$ EntryExitCode:=FALSE *)
 VAR exec[4]:ADDRESS;
 BEGIN
   ASSEMBLE(
	XREF	_LinkerDB
	TST.W	Library.openCnt(A6)
	BEQ.S	canExp
	BSET	#delOrd,Library.flags(A6)
	MOVEQ	#0,D0
	RTS
canExp:	MOVEM.L	A4-A6,-(A7)
	LEA	_LinkerDB,A4
	MOVEA.L	A6,A5
	MOVEA.L	A5,A1
	MOVEA.L	exec,A6
	JSR	Remove(A6)
	JSR	Terminate(PC)
	MOVEA.L	A5,A1
	MOVEQ	#0,D0
	MOVE.W	Library.negSize(A5),D0
	SUBA.L	D0,A1
	ADD.W	Library.posSize(A5),D0
	JSR	FreeMem(A6)
	MOVE.L	dosCmdBuf(A4),D0
	MOVEM.L	(A7)+,A4-A6
	RTS
   END);
 END LibExpunge;

 PROCEDURE LibExtFunc(myLib{R.A6}: GrotteNoSoundBasePtr): ADDRESS;
  (*$ EntryExitCode:=FALSE *)
 BEGIN
   ASSEMBLE(
	MOVEQ	#0,D0 (* Immer NIL *)
	RTS
   END);
 END LibExtFunc;
 (*######################################################################*)

 PROCEDURE InitSound(): BOOLEAN;
  (*$ LoadA4:=TRUE *)
 BEGIN
  RETURN TRUE
 END InitSound;

 PROCEDURE Sound(o1, o2: OBJECT; volume: SHORTCARD; stereo: SHORTINT);
 BEGIN
 END Sound;

 PROCEDURE Switch(sound: BOOLEAN);
 BEGIN
 END Switch;

BEGIN

  myLib:=CAST(GrotteNoSoundBasePtr,dosCmdLen);
  myLib^.magicCode:= 062077E01H;

CLOSE

END GrotteNoSoundLib.mod
