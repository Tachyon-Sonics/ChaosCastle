IMPLEMENTATION MODULE ChaosDead;

 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32,
  AllocMem, AddTail, First, Next, Tail;
 FROM Checks IMPORT CheckMem;
 FROM ChaosBase IMPORT DeadObj, Message, Anims, Obj, ObjPtr, ObjAttr,
  ObjAttrPtr, MakeProc, AieProc, DieProc, attrList, DisposeObj, BasicTypes,
  step;
 FROM ChaosGraphics IMPORT backpx, backpy, H;
 FROM ChaosSounds IMPORT Channel, channel, nbChannel;
 FROM ChaosActions IMPORT SetObjXY, UpdateXY, priorities, msgObj;


 PROCEDURE MakeDead(d: ObjPtr);
  (* Called when resolution changes *)
 BEGIN
  WITH d^ DO
   IF subKind = Message THEN
     (* Kill it, as message has been erased *)
    width:= 0
   END
  END
 END MakeDead;

 PROCEDURE MoveDead(d: ObjPtr);
  VAR
   py: INT16;
   c: CARD16;
 BEGIN
  WITH d^ DO
   UpdateXY(d);
   IF subKind = Message THEN
    height:= H(10);
    py:= stat; INC(py, backpy);
    SetObjXY(d, backpx, py);
    IF step >= moveSeq THEN
     msgObj[shapeSeq]:= NIL;
     priorities[shapeSeq]:= 0;
     DisposeObj(d)
    ELSE
     DEC(moveSeq, step);
     priorities[shapeSeq]:= (moveSeq + 255) DIV 256
    END
   ELSE
    c:= nbChannel;
    WHILE c > 0 DO
     DEC(c);
     IF channel[c].sndObj = d THEN RETURN END
    END;
    DisposeObj(d)
   END
  END
 END MoveDead;

 PROCEDURE InitParams;
  VAR
   attr: ObjAttrPtr;
 BEGIN
  attr:= AllocMem(SIZE(ObjAttr));
  CheckMem(attr);
  WITH attr^ DO
   Move:= MoveDead;
   Make:= MakeDead;
   basicType:= NotBase;
   priority:= 100;
   toKill:= TRUE
  END;
  AddTail(attrList[DEAD], attr^.node)
 END InitParams;

BEGIN

 InitParams;
 InitParams;

END ChaosDead.
