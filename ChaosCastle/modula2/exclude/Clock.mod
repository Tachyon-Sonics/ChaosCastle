IMPLEMENTATION MODULE Clock;
 (*$ OverflowChk:= FALSE *)

 FROM SYSTEM IMPORT ADR, ADDRESS, LONGSET, SHIFT, REG;
 FROM ExecD IMPORT MemReqs, MemReqSet, Node, NodePtr, Task, TaskPtr, MsgPort,
  MsgPortPtr, Message, MessagePtr, IOFlagSet, quick, IORequest, IORequestPtr,
  read, write, stop, ExecBase, ExecBasePtr, execBase;
 FROM ExecL IMPORT AbortIO, AllocMem, AllocSignal, CheckIO, CloseDevice,
  DoIO, FindTask, FreeMem, GetMsg, OpenDevice, SendIO, SetTaskPri, Signal,
  Wait, WaitIO, WaitPort, Forbid, Permit, PutMsg;
 FROM ExecSupport IMPORT BeginIO, CreatePort, DeletePort, CreateExtIO,
  DeleteExtIO, CreateTask, DeleteTask;
 FROM Timer IMPORT timerName, microHz, TimeVal, TimeValPtr, addRequest,
  getSysTime, setSysTime, TimeRequest, CmpTime, SubTime, TimeRequestPtr;
 FROM AmigaBase IMPORT timerPort, SoundControl;
 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32;
 IMPORT R;

 TYPE
  TimePtr = POINTER TO Time;
  Time = RECORD
   io, eio: TimeRequestPtr;
   mp: MsgPortPtr;
   lasttime: TimeVal;
   period, delmul, delmod: CARD16;
   delcnt: INT16;
  END;

 PROCEDURE DoTimer(io{R.A2}: TimeRequestPtr; comm{R.D4}: CARD16);
 BEGIN
  io^.node.command:= comm;
  io^.node.flags:= IOFlagSet{};
  SendIO(io); WaitIO(io)
 END DoTimer;

 PROCEDURE AllocTime(period: CARD16): TimePtr;
  VAR
   t{R.A3}: TimePtr;
 BEGIN
  t:= AllocMem(SIZE(Time), MemReqSet{memClear});
  IF t <> NIL THEN
   t^.period:= period;
   t^.delmod:= 1000000 MOD period;
  (*$ IF m68020 *)
   t^.delmul:= 1000000 DIV period;
  (*$ ELSE *)
   t^.delmul:= REG(R.D1);
  (*$ ENDIF *)
   t^.mp:= CreatePort(NIL, 0);
   IF t^.mp <> NIL THEN
    t^.eio:= CreateExtIO(t^.mp, SIZE(TimeRequest));
    IF t^.eio <> NIL THEN
     t^.io:= CreateExtIO(t^.mp, SIZE(TimeRequest));
     IF t^.io <> NIL THEN
      OpenDevice(ADR(timerName), microHz, t^.io, LONGSET{});
      IF t^.io^.node.error = 0 THEN
       t^.eio^:= t^.io^;
       StartTime(t);
       RETURN t
      END;
      DeleteExtIO(t^.io)
     END;
     DeleteExtIO(t^.eio)
    END;
    DeletePort(t^.mp)
   END;
   FreeMem(t, SIZE(Time))
  END;
  RETURN NIL
 END AllocTime;

 PROCEDURE StartTime(t: TimePtr);
 BEGIN
  WITH t^ DO
   DoTimer(io, getSysTime);
   lasttime:= io^.time
  END
 END StartTime;

 PROCEDURE PrepareDelay(t{R.D6}: TimePtr; delay{R.D7}: CARD32): BOOLEAN;
  VAR
   temp: TimeVal;
   d{R.D3}: CARD16;
 BEGIN
  WITH t^ DO
   WITH lasttime DO
    d:= delay MOD period;
   (*$ IF m68020 *)
    INC(secs, delay DIV period);
   (*$ ELSE *)
    INC(secs, REG(R.D1));
   (*$ ENDIF *)
    INC(micro, CARD32(d * delmul));
    DEC(delcnt, d);
    IF delcnt < 0 THEN INC(delcnt, period); INC(micro, delmod) END;
    IF micro >= 1000000 THEN INC(secs); DEC(micro, 1000000) END;
   END;
   temp:= lasttime;
   DoTimer(io, getSysTime);
   WITH io^ DO
    IF CmpTime(node.device, ADR(time), ADR(temp)) > 0 THEN
     SubTime(node.device, ADR(temp), ADR(time)); time:= temp;
     IF (time.secs > 0) OR (time.micro > 3) THEN
      RETURN TRUE
     END
    END
   END
  END;
  RETURN FALSE
 END PrepareDelay;

 PROCEDURE WaitTime(t: TimePtr; delay: CARD32): BOOLEAN;
 BEGIN
  IF PrepareDelay(t, delay) THEN
   IF (t^.lasttime.secs > 0) OR (t^.lasttime.micro >= 15000) THEN
    IF SoundControl <> NIL THEN SoundControl END
   END;
   DoTimer(t^.io, addRequest);
   RETURN TRUE
  ELSE
   RETURN FALSE
  END
 END WaitTime;

 PROCEDURE TimeEvent(t: TimePtr; delay: CARD32);
  VAR
   oldtime: TimeVal;
 BEGIN
  WITH t^ DO
   IF (eio^.node.message.replyPort = timerPort) THEN
    IF NOT CheckIO(eio) THEN AbortIO(eio) END;
    WaitIO(eio)
   END;
   oldtime:= lasttime;
   IF PrepareDelay(t, delay) THEN
    WITH eio^ DO
     time:= io^.time;
     node.message.replyPort:= timerPort;
     node.command:= addRequest;
     node.flags:= IOFlagSet{};
     SendIO(eio)
    END
   ELSE
    PutMsg(timerPort, ADR(eio^.node.message))
   END;
   lasttime:= oldtime
  END
 END TimeEvent;

 PROCEDURE TimeCall(t: TimePtr; delay: CARD32;
                    proc: TimeCallBackProc; data: ADDRESS): BOOLEAN;
 BEGIN
 END TimeCall;

 PROCEDURE GetTime(t: TimePtr): CARD32;
 BEGIN
  WITH t^ DO
   DoTimer(io, getSysTime);
   WITH io^ DO
    SubTime(node.device, ADR(time), ADR(lasttime));
    RETURN (CARD16(time.secs) * period) + (time.micro DIV delmul)
   END
  END
 END GetTime;

 PROCEDURE FreeTime(t: TimePtr);
 BEGIN
  IF t <> NIL THEN
   WITH t^ DO
    IF (eio^.node.message.replyPort = timerPort) THEN
     IF NOT CheckIO(eio) THEN AbortIO(eio) END;
     WaitIO(eio)
    END;
    REPEAT UNTIL GetMsg(timerPort) = NIL;
    CloseDevice(io);
    DeleteExtIO(io);
    DeleteExtIO(eio);
    DeletePort(mp);
    FreeMem(t, SIZE(Time))
   END
  END
 END FreeTime;

 PROCEDURE GetSysTime(VAR cnt: CARD32; VAR h, m, s: CARD16);
  VAR
   mp{R.D7}: MsgPortPtr;
   io{R.D6}: TimeRequestPtr;
 BEGIN
  h:= 0; m:= 0; s:= 0;
  mp:= CreatePort(NIL, 0);
  IF mp <> NIL THEN
   io:= CreateExtIO(mp, SIZE(TimeRequest));
   IF io <> NIL THEN
    OpenDevice(ADR(timerName), microHz, io, LONGSET{});
    IF io^.node.error = 0 THEN
     DoTimer(io, getSysTime);
     cnt:= io^.time.secs;
     io^.time.secs:= io^.time.secs MOD 86400;
     s:= io^.time.secs MOD 3600;
    (*$ IF m68020 *)
     h:= io^.time.secs DIV 3600;
    (*$ ELSE *)
     h:= REG(R.D1);
    (*$ ENDIF *)
     m:= s DIV 60;
     s:= s MOD 60;
     CloseDevice(io)
    END;
    DeleteExtIO(io)
   END;
   DeletePort(mp)
  END
 END GetSysTime;

 PROCEDURE GetCurrentTime(VAR h, m, s: CARD16);
  VAR
   cnt: CARD32;
 BEGIN
  GetSysTime(cnt, h, m, s)
 END GetCurrentTime;

 PROCEDURE GetNewSeed(): CARD32;
  VAR
   cnt: CARD32;
   h, m, s: CARD16;
 BEGIN
  GetSysTime(cnt, h, m, s);
  RETURN cnt
 END GetNewSeed;

END Clock.
