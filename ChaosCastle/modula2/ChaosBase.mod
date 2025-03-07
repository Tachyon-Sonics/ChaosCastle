IMPLEMENTATION MODULE ChaosBase;

 FROM SYSTEM IMPORT ADR, ADDRESS, VAL;
 FROM Memory IMPORT TagItem, TagItemPtr, Node, NodePtr, List, ListPtr,
  AllocMem, FreeMem, InitList, First, Tail, Empty, Prev, Next, AddHead,
  AddBefore, Remove, CARD8, INT8, CARD16, INT16, CARD32, INT32;
 FROM Checks IMPORT CheckMem, AddTermProc;
 FROM Files IMPORT CloseFile, noFile;
 FROM Dialogs IMPORT DeepFreeGadget, noGadget;
 IMPORT Registration;

 CONST
  MAXOBJ = MAX(CARD8);
  MAXALIEN = MAXOBJ - 32;


 PROCEDURE GetAnimAttr(kind: Anims; subKind: CARD8): ObjAttrPtr;
  VAR
   list: ListPtr;
   objAttr: ObjAttrPtr;
   tail: ObjAttrPtr;
 BEGIN
  list:= ADR(attrList[kind]);
  objAttr:= First(list^);
  tail:= Tail(list^);
  LOOP
   IF objAttr = tail THEN RETURN NIL END;
   IF subKind = 0 THEN EXIT END;
   objAttr:= Next(objAttr^.node);
   DEC(subKind)
  END;
  RETURN objAttr
 END GetAnimAttr;

 PROCEDURE NewObj(kind: Anims; subKind: CARD8): ObjPtr;
  VAR
   attr: ObjAttrPtr;
   obj, at, tail: ObjPtr;
   max: CARD8;
   pri: INT8;
 BEGIN
  IF kind IN AnimAlienSet + AnimSet{STONE, DEADOBJ} - AnimSet{ALIEN3} THEN max:= MAXALIEN ELSE max:= MAXOBJ END;
  IF nbObj >= max THEN RETURN NIL END;
  attr:= GetAnimAttr(kind, subKind);
  IF attr = NIL THEN RETURN NIL END;
  IF attr^.toKill THEN INC(nbToKill) END;
  pri:= attr^.priority;
  INC(attr^.nbObj);
  obj:= FirstObj(emptyObjList);
  Remove(obj^.objNode);
  DEC(nbEmpty); INC(nbObj);
  obj^.kind:= kind; obj^.subKind:= subKind;
  obj^.attr:= attr;
  at:= FirstObj(objList); tail:= TailObj(objList);
  WHILE (at <> tail) AND (pri > at^.priority) DO
   at:= NextObj(at^.objNode)
  END;
  obj^.priority:= pri;
  AddBefore(at^.objNode, obj^.objNode);
  AddHead(animList[kind], obj^.animNode);
  INC(nbAnim[kind]);
  RETURN obj
 END NewObj;

 PROCEDURE DisposeObj(obj: ObjPtr);
 BEGIN
  IF obj = nextObj THEN nextObj:= Next(obj^.animNode) END;
  IF obj^.attr^.toKill THEN
   DEC(nbToKill);
   IF (nbToKill = 0) AND (gameStat = Playing) THEN gameStat:= Finish END
  END;
  DEC(obj^.attr^.nbObj);
  DEC(nbAnim[obj^.kind]);
  Remove(obj^.objNode);
  INC(nbEmpty); DEC(nbObj);
  Remove(obj^.animNode);
  obj^.kind:= DEAD;
  obj^.attr:= NIL;
  AddHead(emptyObjList, obj^.objNode)
 END DisposeObj;

 PROCEDURE ConvertObj(obj: ObjPtr; kind: Anims; subKind: CARD8);
  VAR
   attr: ObjAttrPtr;
 BEGIN
  IF obj = nextObj THEN nextObj:= Next(obj^.animNode) END;
  attr:= obj^.attr;
  DEC(attr^.nbObj);
  IF obj^.attr^.toKill THEN DEC(nbToKill) END;
  DEC(nbAnim[obj^.kind]);
  Remove(obj^.animNode);
  attr:= GetAnimAttr(kind, subKind);
  obj^.kind:= kind; obj^.subKind:= subKind;
  obj^.attr:= attr; INC(attr^.nbObj);
  IF attr^.toKill THEN INC(nbToKill) END;
  AddHead(animList[kind], obj^.animNode);
  INC(nbAnim[kind]);
  IF nbToKill = 0 THEN gameStat:= Finish END
 END ConvertObj;

 PROCEDURE LeaveObj(obj: ObjPtr);
 BEGIN
  IF obj = nextObj THEN nextObj:= Next(obj^.animNode) END;
  Remove(obj^.animNode);
  Remove(obj^.objNode);
  AddHead(leftObjList, obj^.objNode)
 END LeaveObj;

 PROCEDURE RestartObj(obj: ObjPtr);
  VAR
   at, tail: ObjPtr;
   pri: INT8;
 BEGIN
  Remove(obj^.objNode);
  AddHead(animList[obj^.kind], obj^.animNode);
  pri:= obj^.priority;
  at:= FirstObj(objList); tail:= TailObj(objList);
  WHILE (at <> tail) AND (pri > at^.priority) DO
   at:= NextObj(at^.objNode)
  END;
  AddBefore(at^.objNode, obj^.objNode)
 END RestartObj;

 PROCEDURE FirstObj(VAR list: List): ADDRESS;
 BEGIN
  RETURN First(list) - VAL(ADDRESS, SIZE(Node))
 END FirstObj;

 PROCEDURE NextObj(VAR node: Node): ADDRESS;
 BEGIN
  RETURN Next(node) - VAL(ADDRESS, SIZE(Node))
 END NextObj;

 PROCEDURE PrevObj(VAR node: Node): ADDRESS;
 BEGIN
  RETURN Prev(node) - VAL(ADDRESS, SIZE(Node))
 END PrevObj;

 PROCEDURE TailObj(VAR list: List): ADDRESS;
 BEGIN
  RETURN Tail(list) - VAL(ADDRESS, SIZE(Node))
 END TailObj;

 PROCEDURE FlushAllObjs;
 BEGIN
  nextObj:= NIL;
  WHILE NOT(Empty(leftObjList)) DO
   RestartObj(FirstObj(leftObjList))
  END;
  WHILE NOT(Empty(objList)) DO
   DisposeObj(FirstObj(objList))
  END
 END FlushAllObjs;

 PROCEDURE InitLists;
  VAR
   a: Anims;
   c: CARD8;
   newObj: ObjPtr;
 BEGIN
  InitList(objList);
  InitList(leftObjList);
  InitList(emptyObjList);
  FOR a:= MIN(Anims) TO MAX(Anims) DO
   InitList(attrList[a]);
   InitList(animList[a]);
   nbAnim[a]:= 0
  END;
  FOR c:= 0 TO MAXOBJ DO
   newObj:= AllocMem(SIZE(Obj));
   CheckMem(newObj);
   AddHead(emptyObjList, newObj^.objNode)
  END;
  nextObj:= NIL;
  nbObj:= 0; nbEmpty:= MAXOBJ;
  nbToKill:= 0;
  file:= noFile;
  d:= noGadget
 END InitLists;

 PROCEDURE FlushLists;
  VAR
   a: Anims;
   freeObj: ObjPtr;
   freeAttr: ObjAttrPtr;
 BEGIN
  FlushAllObjs;
  WHILE NOT(Empty(emptyObjList)) DO
   freeObj:= FirstObj(emptyObjList);
   Remove(freeObj^.objNode);
   FreeMem(freeObj)
  END;
  FOR a:= MIN(Anims) TO MAX(Anims) DO
   WHILE NOT(Empty(attrList[a])) DO
    freeAttr:= First(attrList[a]);
    Remove(freeAttr^.node);
    FreeMem(freeAttr)
   END
  END
 END FlushLists;

 PROCEDURE Close;
 BEGIN
  FlushLists;
  CloseFile(file);
  DeepFreeGadget(d)
 END Close;

BEGIN

 password:= FALSE;
 InitLists;
 AddTermProc(Close);

END ChaosBase.
