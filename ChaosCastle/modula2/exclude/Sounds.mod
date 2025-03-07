IMPLEMENTATION MODULE Sounds;
  (*$ RangeChk:= FALSE  OverflowChk:= FALSE *)

 FROM SYSTEM IMPORT ADR, ADDRESS, LONGSET, BITSET, SHORTSET, REG, SHIFT, CAST,
  SETREG, TAG;
 FROM ExecD IMPORT MemReqs, MemReqSet, MsgPort, MsgPortPtr, write, quick,
  flush, stop, start, IOFlagSet, Task, TaskPtr, Message, NodeType, read,
  MsgPortAction, reset, AttnFlags, AttnFlagSet;
 FROM ExecL IMPORT AllocMem, FreeMem, WaitPort, GetMsg, AbortIO, OpenDevice,
  CloseDevice, FindTask, PutMsg, ReplyMsg, AllocSignal, FreeSignal, Signal,
  CheckIO, Wait, RemTask, Forbid, Permit, execBase;
 FROM ExecSupport IMPORT CreateExtIO, DeleteExtIO, BeginIO, IsMsgPortEmpty,
  NewList;
 FROM Audio IMPORT audioName, free, setPrec, finish, perVol, allocate,
  pervol, syncCycle, noWait, IOAudio, IOAudioPtr, channelStolen, writeMessage;
 FROM Ahi IMPORT ahiName, AHIAudioCtrl, AHIAudioCtrlPtr, AHISoundMessage,
  AHISoundMessagePtr, AHISampleInfo, AHISampleInfoPtr, AHIAudioModeRequester,
  AHIAudioModeRequesterPtr, AHIAAudioID, AHIAMixFreq, AHIAChannels, AHIASounds,
  AHIASoundFunc, AHIAUserData, AHIPBeginChannel, AHIPEndChannel, AHIPFreq,
  AHIPVol, AHIPPan, AHIPSound, AHIPOffset, AHIPLength, AHIPLoopFreq,
  AHIPLoopVol, AHIPLoopPan, AHIPLoopSound, AHIPLoopOffset, AHIPLoopLength,
  AHIDBAudioID, AHIDBMaxChannels, AHISFIMM, AHISTSAMPLE, AHISTM8S, AHIEOK,
  AHIENOMEM, AHIDEFAULTUNIT, AllocAudioA, KillAudio, ControlAudioA, SetVol,
  SetFreq, SetSound, LoadSound, UnloadSound, AHIRequest, AHIRequestPtr,
  AHINOUNIT, AHINOSOUND, AHICPlay, AHIDEFAULTID, AHIAPlayerFunc,
  AHIAPlayerFreq, AHIAMinPlayerFreq, AHIAMaxPlayerFreq, AHIRTitleText,
  AHIRInitialAudioID, AHIRDoMixFreq, AllocAudioRequestA, FreeAudioRequest,
  AudioRequestA, GetAudioAttrsA, AHIDBPanning, AHIRDoDefaultMode; IMPORT Ahi;
 FROM UtilityD IMPORT Hook, HookPtr;
 FROM GraphicsD IMPORT GfxBase, GfxBasePtr, DisplayFlags, DisplayFlagSet;
 FROM GraphicsL IMPORT graphicsBase;
 FROM Hardware IMPORT CiaaPraFlags, CiaaPraFlagSet, CIAA, ciaa;
 FROM AmigaBase IMPORT SoundControl, filterGiven, soundPriority,
  soundBoost, speaker, surround, ahiGiven, givenAhi, amGiven, givenAM, askAM,
  programName;
 FROM Memory IMPORT TagItem, TagItemPtr, NextTag, CopyStr, StrLength,
  List, Node, InitList, First, Tail, Next, AddHead, Remove, CARD16, INT16,
  CARD32, INT32, INT8, CARD8, AddTail;
 FROM Checks IMPORT AddTermProc, Check;
 IMPORT Memory, R;

 CONST
  MAXBUF = 32;
  Nul = 256;
  NulPri = -128;

 TYPE
  SndCommand = RECORD
   wave: ADDRESS;
   pan: CARD32;
   offset: CARD16;
   snd: CARD16;
   length, delay, rate, distance, volume, am, fm: CARD16;
   stereo, balance: INT16;
   pri: INT8;
   rear: BOOLEAN;
  END;
  SndCmdPtr = POINTER TO SndCommand;
  SndRequest = RECORD
   wiol, wior: IOAudioPtr;
   wmpl, wmpr: MsgPortPtr;
   cmd: SndCommand;
  END;
  Channel = RECORD
   node: Node;
   chnum: CARD16;
   queue: ARRAY[0..MAXBUF-1] OF SndRequest;
   wmp: MsgPortPtr;
   io: IOAudioPtr;
   currentCmd, lastCmd: SndCommand;
   current, last: CARD16;
   offset, step: CARD16;
   leftUnit, rightUnit: SHORTSET;
   allocated, double, stereoMode: BOOLEAN;
  END;
  ChannelPtr = POINTER TO Channel;
  FreqListArr = ARRAY[0..127] OF CARD16;
  WaveEntry = RECORD
   node: Node;
   info: AHISampleInfo;
  END;
  WaveEntryPtr = POINTER TO WaveEntry;
  HashEntry = RECORD
   wavePtr: ADDRESS;
   waveNum: CARD16;
  END;
  HashTable = ARRAY [0..4095] OF HashEntry;
  HashTablePtr = POINTER TO HashTable;

 CONST
  FreqList = FreqListArr{
   0, 6, 7, 7, 8,
   8, 9, 9, 10, 10, 11, 12, 12, 13, 14, 15, 15,
   16, 17, 18, 19, 21, 22, 23, 24, 26, 27, 29, 31,
   33, 35, 37, 39, 41, 44, 46, 49, 52, 55, 58, 62,
   65, 69, 73, 78, 82, 87, 92, 98, 104, 110, 117, 123,
   131, 139, 147, 156, 165, 175, 185, 196, 208, 220, 233, 247,
   262, 277, 294, 311, 330, 349, 370, 392, 415, 440, 466, 494,
   523, 554, 587, 622, 659, 698, 740, 784, 831, 880, 932, 988,
   1047, 1109, 1175, 1245, 1319, 1397, 1480, 1568, 1661, 1760, 1865, 1976,
   2093, 2217, 2349, 2489, 2637, 2794, 2960, 3136, 3322, 3520, 3729, 3951,
   4186, 4435, 4699, 4978, 5274, 5588, 5920, 6272, 6645, 7040, 7459, 7902,
   8372, 8870, 9397};

 CONST
  cPer = 0;
  cVol = 1;
  cFm = 2;
  cAm = 3;
  cPri = 4;
  cSte = 5;

 VAR
  chanList: List;
  waveList: List;
  ClkCst: CARD32;
  pmiddle: ADDRESS;
  chanCnt, waveCnt, water: CARD16;
  thisTask: ADDRESS;
  sig: INT16;
  oldFilter, filter: BOOLEAN;
  hash: HashTablePtr;
  hashSize: CARD16;
  FreeSpace: CARD16;

  ahi, hasStereo: BOOLEAN;
  maxChannels: CARD32;


 PROCEDURE GetSoundsSysAttr(VAR what: TagItem);
 BEGIN
  WITH what DO
   CASE tag OF
     sSTEREO: data:= ORD(hasStereo)
    |sNUMCHANS: data:= maxChannels
    ELSE tag:= 0; data:= 0
   END
  END
 END GetSoundsSysAttr;

 PROCEDURE CreatePort(): MsgPortPtr;
  VAR
   port{R.D7}: MsgPortPtr;
 BEGIN
  port:= AllocMem(SIZE(MsgPort), MemReqSet{memClear, public});
  IF port <> NIL THEN
   WITH port^ DO
    node.name:= NIL;
    node.pri:= 0;
    node.type:= msgPort;
    flags:= signal;
    sigTask:= FindTask(NIL);
    sigBit:= sig;
    NewList(ADR(msgList))
   END
  END;
  RETURN port
 END CreatePort;

 PROCEDURE DeletePort(VAR msgPort{R.A2}: MsgPortPtr);
 BEGIN
  FreeMem(msgPort, SIZE(MsgPort));
  msgPort:= NIL
 END DeletePort;

 PROCEDURE WaveToSnd(wave, addr: ADDRESS): CARD16;
  VAR
   peekl{R.A2}: POINTER TO LONGCARD;
   pos{R.D2}, add{R.D3}: INT16;
 BEGIN
  IF NOT(ahi) OR (hashSize <= 1) THEN RETURN 0 END;
   (* First hashing *)
  pos:= CARD16((LONGCARD(wave) DIV 8) MOD 65536) MOD hashSize;
  peekl:= ADDRESS(wave - 4);
   (* Second hashing *)
  add:= CARD16((peekl^ DIV 2) MOD 65536) MOD (hashSize - 1) + 1;
  WHILE (hash^[pos].wavePtr <> addr) DO
   pos:= CARD16(pos + add) MOD hashSize
  END;
  RETURN pos
 END WaveToSnd;

 PROCEDURE ModifyCmd(VAR new{R.A0}, old{R.A1}: SndCommand): BITSET;
  VAR
   changes{R.D0}: BITSET;
 BEGIN
  changes:= {};
  IF new.wave <> NIL THEN
   old.wave:= new.wave;
   old.snd:= new.snd;
   old.offset:= new.offset;
   old.length:= new.length;
   old.delay:= new.delay
  END;
  IF (new.rate <> 0) AND (new.rate <> old.rate) THEN
   old.rate:= new.rate; INCL(changes, cPer)
  END;
  IF (new.fm <> 0) AND (new.fm <> old.fm) THEN
   old.fm:= new.fm; INCL(changes, cFm)
  END;
  IF (new.distance <> MAX(CARD16)) AND (new.distance <> old.distance) THEN
   old.distance:= new.distance; INCL(changes, cSte)
  END;
  IF (new.volume <> Nul) AND (new.volume <> old.volume) THEN
   old.volume:= new.volume; INCL(changes, cVol)
  END;
  IF (new.am <> Nul) AND (new.am <> old.am) THEN
   old.am:= new.am; INCL(changes, cAm)
  END;
  IF (new.stereo <> Nul) AND ((new.stereo <> old.stereo) OR (new.rear <> old.rear)) THEN
   old.pan:= new.pan;
   old.stereo:= new.stereo; old.rear:= new.rear; INCL(changes, cSte)
  END;
  IF (new.balance <> Nul) AND (new.balance <> old.balance) THEN
   old.pan:= new.pan;
   old.balance:= new.balance; INCL(changes, cSte)
  END;
  IF (new.pri <> NulPri) AND (new.pri <> old.pri) THEN
   old.pri:= new.pri; INCL(changes, cPri)
  END;
  RETURN changes
 END ModifyCmd;

 PROCEDURE NulCmd(VAR snd{R.A0}: SndCommand);
 BEGIN
  snd.wave:= NIL; snd.delay:= 0;
  snd.offset:= 0;
  snd.snd:= 0; snd.pan:= 32768;
  snd.rate:= 0; snd.fm:= 0;
  snd.distance:= MAX(CARD16);
  snd.volume:= Nul; snd.am:= Nul;
  snd.pri:= NulPri; snd.stereo:= Nul;
  snd.balance:= Nul
 END NulCmd;

 PROCEDURE NulStat(VAR snd{R.A0}: SndCommand);
 BEGIN
  snd.wave:= NIL; snd.delay:= 0;
  snd.snd:= 0; snd.offset:= 0;
  snd.rate:= 22050; snd.fm:= 256;
  snd.distance:= 0; snd.pan:= 32768;
  snd.volume:= 64; snd.am:= 128;
  snd.pri:= 0; snd.stereo:= 0;
  snd.balance:= 0; snd.rear:= FALSE
 END NulStat;

 PROCEDURE GetAhiVol(VAR cmd{R.A0}: SndCommand): CARD32;
  VAR
   res{R.D2}: CARD32;
 BEGIN
  res:= CARD32(cmd.volume * cmd.am) * soundBoost DIV 2;
  IF res >= 65536 THEN RETURN 65536 ELSE RETURN res END
 END GetAhiVol;


 PROCEDURE FreeAudio(chan: ChannelPtr);
  VAR
   c{R.D7}: CARD16;
 BEGIN
  IF ahi THEN RETURN END;
  WITH chan^ DO
   WITH io^ DO
    request.command:= free;
    request.flags:= IOFlagSet{}
   END;
   BeginIO(io);
   WaitPort(wmp); IGNORE GetMsg(wmp);
   FOR c:= 0 TO MAXBUF - 1 DO
    WITH queue[c] DO
     REPEAT UNTIL GetMsg(wmpl) = NIL;
     REPEAT UNTIL NOT(double) OR (GetMsg(wmpr) = NIL)
    END
   END;
   allocated:= FALSE
  END
 END FreeAudio;

 PROCEDURE UpdateChannel(chan: ChannelPtr; mayfree: BOOLEAN);
  VAR
   msg{R.D7}: IOAudioPtr;
   newPri{R.D6}: INT16;
 BEGIN
  IF ahi THEN RETURN END;
  newPri:= NulPri;
  WITH chan^ DO
   IF NOT(allocated) THEN RETURN END;
   LOOP
    IF current = last THEN EXIT END;
    WITH queue[current] DO
     IF double THEN
      IF IsMsgPortEmpty(wmpl) AND IsMsgPortEmpty(wmpr) THEN EXIT END;
      IF (wiol^.request.error <> 0) AND IsMsgPortEmpty(wmpr) THEN
       AbortIO(wior); WaitPort(wmpr)
      ELSIF (wior^.request.error <> 0) AND IsMsgPortEmpty(wmpl) THEN
       AbortIO(wiol); WaitPort(wmpl)
      END
     END;
     IF IsMsgPortEmpty(wmpl) OR (double AND IsMsgPortEmpty(wmpr)) THEN EXIT END;
     REPEAT UNTIL GetMsg(wmpl) = NIL;
     IF double THEN REPEAT UNTIL GetMsg(wmpr) = NIL END
    END;
     (* begin of next sound *)
    current:= (current + 1) MOD MAXBUF;
    WITH queue[current] DO
     IF current = last THEN
      lastCmd:= currentCmd;
      newPri:= NulPri
     ELSIF cPri IN ModifyCmd(cmd, currentCmd) THEN
      newPri:= cmd.pri
     END
    END
   END;
   IF mayfree AND (current = last) THEN
    WITH io^ DO
     request.command:= perVol;
     request.flags:= quick;
     volume:= 0
    END;
    BeginIO(io);
    FreeAudio(chan)
   END;
   IF newPri <> NulPri THEN
    WITH io^ DO
     request.command:= setPrec;
     request.flags:= quick;
     request.message.node.pri:= newPri
    END;
    BeginIO(io)
   END
  END
 END UpdateChannel;

 VAR
  ahiHook: Hook;
  ahiBase: ADDRESS;
  amp: MsgPortPtr;
  aio: AHIRequestPtr;
  ctrl: AHIAudioCtrlPtr;

 PROCEDURE NextCmd(): LONGINT;
  VAR
   hook: HookPtr;
   msg: AHISoundMessagePtr;
   chan, tail: ChannelPtr;
   ahivol: CARD32;
   len{R.D5}: CARD16;
  (*$ SaveA4:= TRUE  StackChk:= FALSE *)
 BEGIN
  hook:= ADDRESS(REG(R.A0));
  msg:= ADDRESS(REG(R.A1));
  SETREG(R.A4, hook^.data);
  chan:= First(chanList); tail:= Tail(chanList);
  WHILE (chan^.chnum <> msg^.ahismChannel) DO
   IF chan = tail THEN RETURN -1 END;
   chan:= Next(chan^.node)
  END;
  WITH chan^ DO
   IF current = last THEN
    Signal(thisTask, LONGSET{sig});
    RETURN 0
   END;
   IF offset >= currentCmd.length THEN
    current:= (current + 1) MOD MAXBUF;
    offset:= 0;
    IF current <> last THEN
     step:= (currentCmd.rate + 15) DIV 16;
     IGNORE ModifyCmd(queue[current].cmd, currentCmd)
    ELSE
     SetSound(ahiBase, chnum, AHINOSOUND, 0, 0, ctrl, LONGSET{});
     Signal(thisTask, LONGSET{sig});
     RETURN 0
    END
   END;
   IF currentCmd.length - offset < step * 2 THEN
    len:= currentCmd.length - offset
   ELSE
    len:= step
   END;
   ahivol:= GetAhiVol(currentCmd);
   SetVol(ahiBase, chnum, ahivol, currentCmd.pan, ctrl, LONGSET{});
   SetSound(ahiBase, chnum, currentCmd.snd, currentCmd.offset + offset,
            len, ctrl, LONGSET{});
   SetFreq(ahiBase, chnum, CARD32(currentCmd.rate * currentCmd.fm) DIV 256,
           ctrl, LONGSET{});
   INC(offset, len)
  END;
  Signal(thisTask, LONGSET{sig});
  RETURN 0
 END NextCmd;

 PROCEDURE AllocAudioChannel(tags: TagItemPtr): ChannelPtr;
  VAR
   chan{R.D7}: ChannelPtr;
   ptr{R.D6}: ADDRESS;
   c{R.D5}: CARD16;
   ok{R.D4}: BOOLEAN;
 BEGIN
  chan:= AllocMem(SIZE(Channel), MemReqSet{memClear});
  IF chan <> NIL THEN
   LOOP
    WITH NextTag(tags)^ DO
     IF tag = 0 THEN EXIT
     ELSIF tag = sSTEREO THEN chan^.stereoMode:= (data <> 0)
     END
    END
   END;
   WITH chan^ DO
    wmp:= CreatePort();
    IF wmp <> NIL THEN
     io:= AllocMem(SIZE(IOAudio) * (MAXBUF * (ORD(stereoMode) + 1) + 1),
                   MemReqSet{public, memClear});
     IF io <> NIL THEN
      io^.request.message.replyPort:= wmp;
      ptr:= io; ok:= TRUE;
      FOR c:= 0 TO MAXBUF - 1 DO
       WITH queue[c] DO
        INC(ptr, SIZE(IOAudio)); wiol:= ptr;
        wmpl:= CreatePort();
        IF wmpl = NIL THEN ok:= FALSE END;
        IF stereoMode THEN
         INC(ptr, SIZE(IOAudio)); wior:= ptr;
         wmpr:= CreatePort();
         IF wmpr = NIL THEN ok:= FALSE END
        END
       END
      END;
      IF ok THEN
       OpenDevice(ADR(audioName), 0, io, LONGSET{});
       IF io^.request.error = 0 THEN
        AddHead(chanList, node);
        NulCmd(queue[0].cmd);
        NulStat(lastCmd);
        currentCmd:= lastCmd;
        IF (chanCnt = 0) AND filterGiven THEN
         oldFilter:= led IN ciaa.pra;
         filter:= NOT(oldFilter)
        END;
        INC(chanCnt);
        RETURN chan
       END
      END;
      FOR c:= 0 TO MAXBUF - 1 DO
       WITH queue[c] DO
        IF wmpl <> NIL THEN DeletePort(wmpl) END;
        IF wmpr <> NIL THEN DeletePort(wmpr) END
       END
      END;
      FreeMem(io, SIZE(IOAudio) * (MAXBUF * (ORD(stereoMode) + 1) + 1))
     END;
     DeletePort(wmp)
    END
   END;
   FreeMem(chan, SIZE(Channel))
  END;
  RETURN NIL
 END AllocAudioChannel;

 PROCEDURE OpenAHI;

  PROCEDURE Prime(val{R.D1}: CARD16): BOOLEAN;
   VAR
    d{R.D0}, d2{R.D2}, d2i{R.D3}: CARD16;
  BEGIN
   d:= 2; d2:= 4; d2i:= 5;
   WHILE d2 <= val DO
    IF val MOD d = 0 THEN RETURN FALSE END;
    INC(d); INC(d2, d2i); INC(d2i, 2)
   END;
   RETURN TRUE
  END Prime;

  VAR
   tags: ARRAY[0..12] OF LONGCARD;
   we{R.D7}, tail{R.D6}: WaveEntryPtr;
   wNum{R.D5}: CARDINAL;
 BEGIN
  amp:= CreatePort();
  IF amp <> NIL THEN
   aio:= CreateExtIO(amp, SIZE(AHIRequest));
   IF aio <> NIL THEN
    aio^.ahirVersion:= 3;
    OpenDevice(ADR(ahiName), AHINOUNIT, aio, LONGSET{});
    ahiBase:= aio^.ahirStd.device;
    IF ahiBase <> NIL THEN
     ahiHook.entry:= ADR(NextCmd);
     ahiHook.subEntry:= NIL;
     ahiHook.data:= REG(R.A4);
     ctrl:= AllocAudioA(ahiBase, TAG(tags,
      AHIAAudioID, givenAM,
      AHIAChannels, chanCnt,
      AHIASounds, waveCnt,
      AHIASoundFunc, ADR(ahiHook)));
     IF ctrl <> NIL THEN
      IF (hash = NIL) THEN
       hashSize:= (waveCnt * 3 + 1) DIV 2;
       WHILE NOT Prime(hashSize) DO INC(hashSize) END;
       hash:= Memory.AllocMem(SIZE(HashEntry) * hashSize)
      END;
      IF hash <> NIL THEN
       we:= First(waveList); tail:= Tail(waveList);
       wNum:= 0;
       WHILE we <> tail DO
        IGNORE LoadSound(ahiBase, wNum, AHISTSAMPLE, ADR(we^.info), ctrl);
        WITH hash^[WaveToSnd(we^.info.ahisiAddress, NIL)] DO
         wavePtr:= we^.info.ahisiAddress;
         waveNum:= wNum; INC(wNum)
        END;
        we:= Next(we^.node)
       END;
       IF ControlAudioA(ahiBase, ctrl, TAG(tags, AHICPlay, TRUE, 0)) = AHIEOK THEN
        RETURN
       END
      END;
      Ahi.FreeAudio(ahiBase, ctrl)
     END;
     CloseDevice(aio);
     ahiBase:= NIL
    END;
    DeleteExtIO(aio)
   END;
   DeletePort(amp)
  END
 END OpenAHI;

 PROCEDURE CloseAHI;
  VAR
   tags: ARRAY[0..2] OF LONGCARD;
 BEGIN
  IF ahiBase <> NIL THEN
   IGNORE ControlAudioA(ahiBase, ctrl, TAG(tags, AHICPlay, FALSE, 0));
   Ahi.FreeAudio(ahiBase, ctrl);
   CloseDevice(aio);
   DeleteExtIO(aio);
   DeletePort(amp);
   ahiBase:= NIL
  END
 END CloseAHI;

 PROCEDURE AllocAhiChannel(tags: TagItemPtr): ChannelPtr;
  VAR
   chan{R.D7}: ChannelPtr;
 BEGIN
  chan:= AllocMem(SIZE(Channel), MemReqSet{memClear});
  IF chan <> NIL THEN
   LOOP
    WITH NextTag(tags)^ DO
     IF tag = 0 THEN EXIT
     ELSIF tag = sSTEREO THEN chan^.stereoMode:= (data <> 0)
     END
    END
   END;
   WITH chan^ DO
    AddHead(chanList, node);
    NulCmd(queue[0].cmd);
    NulStat(lastCmd);
    currentCmd:= lastCmd;
    chnum:= chanCnt;
    INC(chanCnt);
    RETURN chan
   END;
   FreeMem(chan, SIZE(Channel))
  END;
  RETURN NIL
 END AllocAhiChannel;

 PROCEDURE AllocChannel(tags: TagItemPtr): ChannelPtr;
 BEGIN
  IF ahi THEN
   RETURN AllocAhiChannel(tags)
  ELSE
   RETURN AllocAudioChannel(tags)
  END
 END AllocChannel;

 PROCEDURE FlushAudioChannel(chan: ChannelPtr; quiet: BOOLEAN);
  VAR
   stop{R.D7}, toflush{R.D6}: CARD16;
 BEGIN
  WITH chan^ DO
   IF (current = last) OR NOT(allocated) THEN RETURN END;
   stop:= (current + CARD16(ORD(NOT quiet))) MOD MAXBUF;
   toflush:= last;
   WHILE toflush <> stop DO
    IF toflush = 0 THEN toflush:= MAXBUF - 1 ELSE DEC(toflush) END;
    WITH queue[toflush] DO
     AbortIO(wiol); WaitPort(wmpl); REPEAT UNTIL GetMsg(wmpl) = NIL;
     IF double THEN
      AbortIO(wior); WaitPort(wmpr); REPEAT UNTIL GetMsg(wmpr) = NIL
     END
    END
   END
  END
 END FlushAudioChannel;

 PROCEDURE FlushAhiChannel(chan: ChannelPtr; quiet: BOOLEAN);
 BEGIN
  IF NOT(quiet) OR (ahiBase = NIL) THEN RETURN END;
  WITH chan^ DO
   last:= current; (* Prevent NextCmd() from running *)
   SetSound(ahiBase, chan^.chnum, AHINOSOUND, 0, 0, ctrl, AHISFIMM);
   last:= current;
   lastCmd:= currentCmd
  END
 END FlushAhiChannel;

 PROCEDURE FlushChannel(chan: ChannelPtr; quiet: BOOLEAN);
 BEGIN
  IF ahi THEN
   FlushAhiChannel(chan, quiet)
  ELSE
   FlushAudioChannel(chan, quiet)
  END
 END FlushChannel;

 PROCEDURE FreeAudioChannel(VAR chan{R.D6}: ChannelPtr);
  VAR
   c{R.D7}: CARD16;
 BEGIN
  IF chan = NIL THEN RETURN END;
  DEC(chanCnt);
  IF (chanCnt = 0) AND filterGiven THEN
   IF oldFilter THEN INCL(ciaa.pra, led) ELSE EXCL(ciaa.pra, led) END
  END;
  UpdateChannel(chan, FALSE);
  FlushChannel(chan, TRUE);
  WITH chan^ DO
   Remove(node);
   IF allocated THEN
    FreeAudio(chan)
   END;
   CloseDevice(io);
   FreeMem(io, SIZE(IOAudio) * (MAXBUF * (ORD(stereoMode) + 1) + 1));
   FOR c:= 0 TO MAXBUF - 1 DO
    WITH queue[c] DO
     DeletePort(wmpl);
     IF stereoMode THEN DeletePort(wmpr) END
    END
   END;
   DeletePort(wmp)
  END;
  FreeMem(chan, SIZE(Channel));
  chan:= NIL
 END FreeAudioChannel;

 PROCEDURE FreeAhiChannel(VAR chan{R.D6}: ChannelPtr);
 BEGIN
  IF chan = NIL THEN RETURN END;
  FlushChannel(chan, TRUE);
  Remove(chan^.node);
  FreeMem(chan, SIZE(Channel));
  DEC(chanCnt);
  IF chanCnt = 0 THEN CloseAHI END
 END FreeAhiChannel;

 PROCEDURE FreeChannel(VAR chan: ChannelPtr);
 BEGIN
  IF ahi THEN
   FreeAhiChannel(chan)
  ELSE
   FreeAudioChannel(chan)
  END
 END FreeChannel;

 PROCEDURE ReleaseChannels;
  VAR
   chan{R.A3}, tail{R.A2}: ChannelPtr;
   finish{R.D7}: BOOLEAN;
 BEGIN
  chan:= First(chanList);
  tail:= Tail(chanList);
  finish:= TRUE;
  WHILE chan <> tail DO
   IF NOT SndFinish(chan) THEN finish:= FALSE END;
   chan:= Next(chan^.node)
  END;
  IF finish THEN SoundControl:= NIL END
 END ReleaseChannels;

 PROCEDURE AllocAudio(chan: ChannelPtr; VAR cmd: SndCommand);
  TYPE
   ChanArr4 = ARRAY[0..3] OF SHORTCARD;
   ChanArr2 = ARRAY[0..1] OF SHORTCARD;
  CONST
   pmiddle1 = ChanArr4{1, 2, 4, 8};
   pmiddle2 = ChanArr4{2, 1, 8, 4};
   pright = ChanArr4{2, 4, 1, 8};
   pleft = ChanArr4{1, 8, 2, 4};
   pstereo = ChanArr4{3, 5, 10, 12};
   ponlyright = ChanArr2{2, 4};
   ponlyleft = ChanArr2{1, 8};
 BEGIN
  IF ahi THEN RETURN END;
  WITH chan^ DO
   WITH io^ DO
    request.command:= allocate;
    request.message.node.pri:= cmd.pri + 1;
    request.flags:= noWait + quick;
    double:= FALSE;
    length:= 4;
    IF stereoMode THEN
     IF (ABS(cmd.stereo) < 90) OR NOT(cmd.rear) THEN
      double:= TRUE;
      data:= ADR(pstereo)
     ELSIF cmd.stereo > 0 THEN
      data:= ADR(ponlyright); length:= 2
     ELSE
      data:= ADR(ponlyleft); length:= 2
     END
    ELSIF cmd.stereo > 0 THEN
     data:= ADR(pright)
    ELSIF cmd.stereo < 0 THEN
     data:= ADR(pleft)
    ELSE
     IF pmiddle = ADR(pmiddle1) THEN
      pmiddle:= ADR(pmiddle2)
     ELSE
      pmiddle:= ADR(pmiddle1)
     END;
     data:= pmiddle
    END
   END;
   BeginIO(io);
   IF io^.request.flags * quick = IOFlagSet{} THEN
     (* wait for other task to unlock the channel *)
    WaitPort(wmp);
    IGNORE GetMsg(wmp);
    IF io^.request.error <> 0 THEN
     io^.request.flags:= IOFlagSet{};
     BeginIO(io);
     WaitPort(wmp);
     IGNORE GetMsg(wmp)
    END
   END;
   IF io^.request.error = 0 THEN
    WITH io^ DO
     request.command:= reset;
     request.flags:= quick
    END;
    BeginIO(io);
    WITH io^ DO
     request.command:= setPrec;
     request.flags:= quick;
     request.message.node.pri:= cmd.pri
    END;
    BeginIO(io);
    SoundControl:= ReleaseChannels;
    leftUnit:= CAST(SHORTSET, SHORTCARD(io^.request.unit)) * SHORTSET{0, 3};
    rightUnit:= CAST(SHORTSET, SHORTCARD(io^.request.unit)) * SHORTSET{1, 2};
    IF NOT(double) THEN leftUnit:= leftUnit + rightUnit END;
    allocated:= TRUE
   END
  END
 END AllocAudio;

 PROCEDURE Send(chan: ChannelPtr; VAR cmd: SndCommand; VAR req: SndRequest; sync: BOOLEAN);
  VAR
   tmp{R.D7}: CARD16;
   balance{R.D6}: INT16;
   ahivol: CARD32;
   left, right: ADDRESS;
   ptr: POINTER TO CARD32;
 BEGIN
  WITH chan^ DO
   IF ahi THEN
    IF ahiBase <> NIL THEN
     IF sync THEN
      step:= (cmd.rate + 15) DIV 16;
      IF (cmd.length < step * 2) AND (cmd.length >= 16) THEN
       offset:= 8
      ELSE
       IF cmd.length - step < step THEN step:= cmd.length END;
       offset:= step
      END;
      ahivol:= GetAhiVol(cmd);
      SetVol(ahiBase, chnum, ahivol, cmd.pan, ctrl, AHISFIMM);
      SetSound(ahiBase, chnum, cmd.snd, cmd.offset, offset, ctrl, AHISFIMM);
      SetFreq(ahiBase, chnum, CARD32(cmd.rate * cmd.fm) DIV 256, ctrl, AHISFIMM)
     END
    END
   ELSE
    IF NOT(allocated) THEN AllocAudio(chan, cmd) END;
    IF NOT(allocated) THEN RETURN END;
    IF NOT(double) THEN sync:= FALSE END;
    IF sync THEN
     WITH io^ DO
      request.command:= stop;
      request.flags:= quick
     END;
     BeginIO(io)
    END;
    left:= cmd.wave;
    right:= left;
    IF surround AND double AND cmd.rear THEN
     ptr:= left;
     DEC(ptr, FreeSpace);
     IF cmd.stereo < 0 THEN
      INC(left, ptr^ DIV 2)
     ELSE
      INC(right, ptr^ DIV 2)
     END
    END;
    IF double AND speaker THEN
     IF cmd.stereo < 0 THEN
      DEC(right, CARD32(CARD16(-cmd.stereo) * cmd.rate) DIV 131072)
     ELSIF cmd.stereo > 0 THEN
      DEC(left, CARD32(CARD16(cmd.stereo) * cmd.rate) DIV 131072)
     END;
     balance:= cmd.balance
    ELSE
     balance:= cmd.stereo
    END;
    req.wiol^:= io^;
    WITH req.wiol^ DO
     request.message.replyPort:= req.wmpl;
     request.unit:= CAST(ADDRESS, ORD(CAST(SHORTCARD, leftUnit)));
     request.command:= write;
     request.flags:= pervol;
     volume:= CARD32(cmd.volume * cmd.am * CARD16(soundBoost)) DIV 512;
     IF volume > 64 THEN volume:= 64 END;
     tmp:= CARD32(cmd.rate * cmd.fm) DIV 256;
     period:= (ClkCst + tmp DIV 2) DIV tmp;
     data:= left + ADDRESS(cmd.offset);
     length:= cmd.length;
     IF cmd.delay = 0 THEN
      cycles:= 1
     ELSE
      cycles:= CARD16(CARD32(tmp * cmd.delay) DIV 1024) DIV length + 1
     END
    END;
    IF double THEN
     req.wior^:= req.wiol^;
     WITH req.wior^ DO
      request.message.replyPort:= req.wmpr;
      request.unit:= CAST(ADDRESS, ORD(CAST(SHORTCARD, rightUnit)));
      data:= right + ADDRESS(cmd.offset);
      IF balance < 0 THEN
       volume:= (volume * CARD16(90 + balance)) DIV 90
      ELSIF balance > 0 THEN
       req.wiol^.volume:= (volume * CARD16(90 - balance)) DIV 90
      END
     END;
     BeginIO(req.wior)
    END;
    BeginIO(req.wiol);
    IF sync THEN
     WITH io^ DO
      request.command:= start;
      request.flags:= quick
     END;
     BeginIO(io)
    END
   END
  END
 END Send;

 PROCEDURE SndMake(chan: ChannelPtr; params: TagItemPtr; now: BOOLEAN);
  VAR
   newCmd: SndCommand;
   note, freq, tmp{R.D6}: CARD16;
   changes{R.D7}: BITSET;
   begin{R.D5}, old: CARD16;
   balance: INT16;
   sync: BOOLEAN;
 BEGIN
  note:= 0; freq:= 0;
  NulCmd(newCmd); begin:= 1; old:= 1;
  LOOP
   WITH NextTag(params)^ DO
    IF tag = 0 THEN EXIT
    ELSIF tag = sWAVE THEN newCmd.wave:= addr
    ELSIF tag = sDURATION THEN newCmd.delay:= data
    ELSIF tag = sDISTANCE THEN newCmd.distance:= SHIFT(data, 12) DIV 34300
    ELSIF tag = sLENGTH THEN newCmd.length:= data
    ELSIF tag = sOFFSET THEN newCmd.offset:= data
    ELSIF tag = sNOTE THEN note:= data
    ELSIF tag = sFREQ THEN freq:= data
    ELSIF tag = sRATE THEN newCmd.rate:= data
    ELSIF tag = sFM THEN newCmd.fm:= data
    ELSIF tag = sVOLUME THEN
     IF data = 0 THEN
      newCmd.volume:= 0
     ELSIF ahi THEN
      newCmd.volume:= data
     ELSE
      newCmd.volume:= (data + 1) DIV 4
     END
    ELSIF tag = sAM THEN newCmd.am:= data
    ELSIF tag = sSTEREO THEN
     IF (chan^.stereoMode) OR NOT(ahi) THEN
      newCmd.stereo:= lint; newCmd.rear:= FALSE;
      IF ABS(newCmd.stereo) = 181 THEN
       newCmd.stereo:= newCmd.stereo / 2; newCmd.rear:= TRUE
      ELSIF newCmd.stereo > 90 THEN
       newCmd.stereo:= 180 - newCmd.stereo; newCmd.rear:= TRUE
      ELSIF newCmd.stereo < -90 THEN
       newCmd.stereo:= -180 - newCmd.stereo; newCmd.rear:= TRUE
      END;
      WITH newCmd DO
       pan:= (SHIFT(CARD32(90 - stereo), 16) + 90) DIV 180;
(*       IF rear AND surround AND (pan <> 65536) THEN
        pan:= 0FFFFFFFFH - pan + 1
       END *)
      END
     END
    ELSIF tag = sBALANCE THEN newCmd.balance:= lint
    ELSIF tag = sPRI THEN
     newCmd.pri:= lint + soundPriority;
     IF newCmd.pri < -127 THEN newCmd.pri:= -127 ELSIF newCmd.pri > 127 THEN newCmd.pri:= 127 END
    ELSIF tag = sWATER THEN
     water:= data; IF water > 100 THEN water:= 100 END;
     IF filterGiven AND ((data >= 80) <> filter) AND NOT(ahi) THEN
      filter:= NOT(filter);
      IF filter THEN EXCL(ciaa.pra, led) ELSE INCL(ciaa.pra, led) END
     END
    END
   END
  END;
  UpdateChannel(chan, FALSE);
  WITH chan^ DO
    (* compute distance delay *)
   IF (newCmd.distance < 2622) AND (newCmd.distance > 0) AND (water > 0) THEN
    newCmd.distance:= (newCmd.distance * 25 DIV (water * 83 DIV 100 + 25))
   END;
    (* Compute frequencies *)
   IF (note <> 0) AND (note <= 127) THEN
    freq:= FreqList[note]
   END;
   IF (freq <> 0) THEN
    IF newCmd.length = 0 THEN newCmd.length:= lastCmd.length END;
    newCmd.rate:= newCmd.length * freq
   END;
    (* Compute snd *)
   IF ahi AND (newCmd.wave <> NIL) THEN
    IF ahiBase = NIL THEN OpenAHI END;
    IF ahiBase <> NIL THEN
     newCmd.snd:= hash^[WaveToSnd(newCmd.wave, newCmd.wave)].waveNum
    END
   END;
   IF now AND (newCmd.wave <> NIL) AND
      (newCmd.distance >= 4) AND (newCmd.distance <> MAX(CARD16)) THEN
    begin:= 2; old:= 2
   END;
   IF current = last THEN now:= FALSE END;
   IF now AND (newCmd.wave <> NIL) THEN
     (* stop playing *)
    FlushChannel(chan, TRUE);
    IF stereoMode AND (NOT(double) OR ((ABS(newCmd.stereo) = 90) AND (newCmd.rear))) THEN
     FreeAudio(chan)
    END;
    NulStat(lastCmd);
    last:= current; now:= FALSE;
    NulCmd(queue[last].cmd)
   END;
   IF (newCmd.delay = 0) AND (newCmd.length < 2) THEN newCmd.wave:= NIL END;
   IF NOT(now) THEN
     (* queue new values *)
    IGNORE ModifyCmd(newCmd, queue[last].cmd);
    IF queue[last].cmd.wave <> NIL THEN
     IGNORE ModifyCmd(queue[last].cmd, lastCmd);
     IF last = current THEN
      currentCmd:= lastCmd;
      IF allocated THEN
        (* No Allocation, so set priority there *)
       WITH io^ DO
        request.command:= setPrec;
        request.flags:= quick;
        request.message.node.pri:= currentCmd.pri
       END;
       BeginIO(io)
      END
     END;
     WHILE begin > 0 DO
      tmp:= (last + 1) MOD MAXBUF;
      WHILE (tmp = current) DO
       IF ahi THEN
        IGNORE Wait(LONGSET{sig})
       ELSE
        WaitPort(queue[current].wmpl);
        IF double THEN WaitPort(queue[current].wmpr) END;
        UpdateChannel(chan, FALSE)
       END
      END;
      INC(tmp, last);
      last:= tmp - last;
      DEC(tmp, last);
      IF begin = 2 THEN
        (* send delay *)
       currentCmd.rate:= 4096;
       currentCmd.length:= newCmd.distance;
       currentCmd.volume:= 0;
       IF NOT(ahi) THEN Send(chan, currentCmd, queue[tmp], TRUE) END;
       queue[last].cmd:= queue[tmp].cmd
      ELSE
       Send(chan, lastCmd, queue[tmp], current = tmp)
      END;
      IF NOT(allocated OR ahi) THEN
       last:= tmp (* wave not sent *)
      END;
      DEC(begin);
      IF begin = 0 THEN NulCmd(queue[last].cmd) END
     END;
     IF ahi AND (old = 2) THEN (* Send pause *)
      Send(chan, currentCmd, queue[current], TRUE)
     END
    END
   ELSE
     (* modify on the fly *)
    changes:= ModifyCmd(newCmd, currentCmd);
    IF NOT(double) THEN EXCL(changes, cSte) END;
    IF allocated AND ((changes * {cPer, cVol, cFm, cAm, cSte}) <> {}) THEN
      (* change volume and period *)
     WITH io^ DO
      request.unit:= CAST(ADDRESS, ORD(CAST(SHORTCARD, leftUnit)));
      request.command:= perVol;
      request.flags:= quick;
      volume:= CARD32(currentCmd.volume * currentCmd.am * CARD16(soundBoost)) DIV 512;
      IF volume > 64 THEN volume:= 64 END;
      tmp:= CARD32(currentCmd.rate * currentCmd.fm) DIV 256;
      period:= (ClkCst + tmp DIV 2) DIV tmp;
      IF double THEN
       IF NOT(speaker) THEN
        balance:= currentCmd.stereo
       ELSE
        balance:= currentCmd.balance
       END;
       tmp:= volume; freq:= period;
       IF balance > 0 THEN
        volume:= (volume * CARD16(90 - balance) DIV 90)
       END;
       BeginIO(io);
       request.unit:= CAST(ADDRESS, ORD(CAST(SHORTCARD, rightUnit)));
       request.command:= perVol;
       request.flags:= quick;
       volume:= tmp; period:= freq;
       IF balance < 0 THEN
        volume:= (volume * CARD16(90 + balance) DIV 90)
       END
      END;
      BeginIO(io);
      request.unit:= CAST(ADDRESS, ORD(CAST(SHORTCARD, leftUnit + rightUnit)))
     END
    END;
    IF allocated AND (cPri IN changes) THEN
      (* set new priority *)
     WITH io^ DO
      request.command:= setPrec;
      request.flags:= quick;
      request.message.node.pri:= currentCmd.pri
     END;
     BeginIO(io)
    END;
     (* Does the change affect pending cmds ? *)
    tmp:= (current + 1) MOD MAXBUF;
    IF (tmp <> last) THEN
     EXCL(changes, cPri);
     WITH queue[tmp].cmd DO
      IF rate <> 0 THEN EXCL(changes, cPer) END;
      IF volume <> Nul THEN EXCL(changes, cVol) END;
      IF fm <> 0 THEN EXCL(changes, cFm) END;
      IF am <> Nul THEN EXCL(changes, cAm) END;
      IF (stereo <> Nul) OR (balance <> Nul) THEN EXCL(changes, cSte) END
     END;
     IF NOT(ahi) THEN
      sync:= (NOT IsMsgPortEmpty(queue[current].wmpl)) OR
             (double AND NOT IsMsgPortEmpty(queue[current].wmpr))
     ELSE
      sync:= FALSE
     END;
     IF (changes <> {}) OR sync THEN
       (* rebuild pending commands *)
      FlushChannel(chan, FALSE);
      lastCmd:= currentCmd;
      REPEAT
       IGNORE ModifyCmd(queue[tmp].cmd, lastCmd);
       IF allocated THEN Send(chan, lastCmd, queue[tmp], sync) END;
       sync:= FALSE;
       tmp:= (tmp + 1) MOD MAXBUF
      UNTIL tmp = last
     END
    END (* IF requeue *)
   END (* IF now *)
  END (* WITH chan *)
 END SndMake;

 PROCEDURE SndDo(chan: ChannelPtr; params: TagItemPtr);
 BEGIN
  SndMake(chan, params, TRUE)
 END SndDo;

 PROCEDURE SndQueue(chan: ChannelPtr; params: TagItemPtr);
 BEGIN
  SndMake(chan, params, FALSE)
 END SndQueue;

 PROCEDURE SndFinish(chan: ChannelPtr): BOOLEAN;
 BEGIN
  UpdateChannel(chan, TRUE);
  RETURN chan^.current = chan^.last
 END SndFinish;

 PROCEDURE SndGet(chan: ChannelPtr; VAR what: TagItem);
 BEGIN
  WITH chan^ DO
   WITH what DO
    IF tag = sLENGTH THEN data:= currentCmd.length
    ELSIF tag = sWAVE THEN data:= currentCmd.wave
    ELSIF tag = sOFFSET THEN data:= currentCmd.offset
    ELSIF tag = sFREQ THEN data:= currentCmd.rate DIV currentCmd.length
    ELSIF tag = sRATE THEN data:= currentCmd.rate
    ELSIF tag = sVOLUME THEN data:= currentCmd.volume
    ELSIF tag = sAM THEN data:= currentCmd.am
    ELSIF tag = sFM THEN data:= currentCmd.fm
    ELSIF tag = sSTEREO THEN lint:= currentCmd.stereo
    ELSIF tag = sBALANCE THEN lint:= currentCmd.balance
    ELSIF tag = sPRI THEN lint:= currentCmd.pri - soundPriority
    ELSE tag:= 0
    END
   END
  END
 END SndGet;

 PROCEDURE AllocWave(size: CARD32): ADDRESS;
  VAR
   ptr{R.A3}: POINTER TO CARD32;
   mrs{R.D7}: MemReqSet;
   we{R.D6}: WaveEntryPtr;
 BEGIN
  INC(size, FreeSpace);
  IF surround AND NOT(ahi) THEN size:= size * 2 END;
  mrs:= MemReqSet{public, memClear};
  IF NOT(ahi) THEN INCL(mrs, chip) END;
  ptr:= AllocMem(size, mrs);
  IF ptr <> NIL THEN
   ptr^:= size;
   INC(ptr, FreeSpace);
   IF ahi THEN
    we:= AllocMem(SIZE(WaveEntry), MemReqSet{});
    IF we <> NIL THEN
     INC(waveCnt);
     we^.info.ahisiType:= AHISTM8S;
     we^.info.ahisiAddress:= ptr;
     we^.info.ahisiLength:= size - FreeSpace;
     AddTail(waveList, we^.node)
    ELSE
     DEC(ptr, FreeSpace);
     FreeMem(ptr, size);
     ptr:= NIL
    END
   END
  END;
  RETURN ptr
 END AllocWave;

 PROCEDURE FreeWave(VAR wave: ADDRESS);
  TYPE
   PTR = POINTER TO CARD32;
  VAR
   we{R.D7}, next{R.D6}, tail{R.D5}: WaveEntryPtr;
 BEGIN
  IF wave = NIL THEN RETURN END;
  we:= First(waveList); tail:= Tail(waveList);
  WHILE we <> tail DO
   next:= Next(we^.node);
   IF we^.info.ahisiAddress = wave THEN
    DEC(waveCnt);
    Remove(we^.node);
    FreeMem(we, SIZE(WaveEntry))
   END;
   we:= next
  END;
  DEC(wave, FreeSpace);
  FreeMem(wave, CAST(PTR, wave)^);
  IF waveCnt = 0 THEN Memory.FreeMem(hash); hashSize:= 0 END;
  wave:= NIL
 END FreeWave;

 PROCEDURE ConvertWave(VAR wave: ADDRESS; size: CARD32);
  VAR
   pos1{R.D4}, pos2{R.D5}: POINTER TO INT8;
   tmp{R.D3}: INT16;
 BEGIN
  IF NOT(ahi) AND surround THEN
   pos1:= wave; pos2:= pos1;
   INC(pos2, size + FreeSpace);
   WHILE size > 0 DO
    tmp:= pos1^;
    pos2^:= -(tmp + 1);
    INC(pos1); INC(pos2); DEC(size)
   END
  END
 END ConvertWave;

 PROCEDURE LookForAhi;
  VAR
   tags: ARRAY[0..8] OF ADDRESS;
   bool: CARD32;
   mp: MsgPortPtr;
   io{R.D7}: AHIRequestPtr;
   req{R.D6}: AHIAudioModeRequesterPtr;
 BEGIN
  hasStereo:= (speaker OR surround);
  maxChannels:= 4;
  IF ahiGiven AND NOT(givenAhi) THEN RETURN END;
  mp:= CreatePort();
  IF mp <> NIL THEN
   io:= CreateExtIO(mp, SIZE(AHIRequest));
   IF io <> NIL THEN
    io^.ahirVersion:= 3;
    OpenDevice(ADR(ahiName), AHINOUNIT, io, LONGSET{});
    ahiBase:= io^.ahirStd.device;
    IF NOT(ahiGiven) THEN
     givenAhi:= (ahiBase <> NIL) AND (m68030 IN execBase^.attnFlags)
    END;
    IF ahiBase <> NIL THEN
     IF askAM THEN
      req:= AllocAudioRequestA(ahiBase,
       TAG(tags, AHIRTitleText, programName,
                 AHIRInitialAudioID, AHIDEFAULTID,
                 AHIRDoDefaultMode, TRUE,
                 AHIRDoMixFreq, TRUE, 0));
      IF req <> NIL THEN
       IF AudioRequestA(ahiBase, req, NIL) THEN
        givenAM:= req^.ahiamAudioID
       END;
       FreeAudioRequest(ahiBase, req)
      END
     END;
     IF NOT GetAudioAttrsA(ahiBase, givenAM, NIL,
      TAG(tags, AHIDBPanning, ADR(bool),
          AHIDBMaxChannels, ADR(maxChannels), 0)) THEN
      IF NOT(ahiGiven) THEN givenAhi:= FALSE END
     ELSE
      IF (maxChannels > 2) AND NOT(m68030 IN execBase^.attnFlags) THEN
       maxChannels:= 2
      ELSIF (maxChannels > 4) AND NOT(m68040 IN execBase^.attnFlags) THEN
       maxChannels:= 4
      ELSIF maxChannels > 8 THEN
       maxChannels:= 8
      END
     END;
     hasStereo:= (bool <> 0);
     CloseDevice(io);
     ahiBase:= NIL
    ELSIF ahiGiven AND givenAhi THEN
     maxChannels:= 0
    END;
    DeleteExtIO(io)
   END;
   DeletePort(mp)
  END
 END LookForAhi;

 PROCEDURE Close;
 BEGIN
  CloseAHI;
  IF sig <> -1 THEN
   FreeSignal(sig);
   sig:= -1
  END
 END Close;

BEGIN

 InitList(chanList);
 InitList(waveList);
 thisTask:= FindTask(NIL);
 sig:= AllocSignal(-1);
 Check(sig < 0, ADR("Sounds:"), ADR("AllocSignal failed"));
 IF speaker THEN FreeSpace:= 16 + 4 ELSE FreeSpace:= 4 END;
 AddTermProc(Close);
 LookForAhi;
 ahi:= givenAhi;
 IF pal IN graphicsBase^.displayFlags THEN
  ClkCst:= 3546895
 ELSE
  ClkCst:= 3579545
 END;
 water:= 0;

END Sounds.
