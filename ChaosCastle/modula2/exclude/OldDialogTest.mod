MODULE DialogTest;

 FROM SYSTEM IMPORT ADR, ADDRESS, TAG;
 FROM Checks IMPORT Check, CheckMem, CheckMemBool;
 FROM Memory IMPORT TagItem, TagItemPtr, Node, NodePtr, List, ListPtr,
  AllocMem, FreeMem, InitList, AddHead, Remove, StrPtr, StrLength, CopyStr,
  First, Last, Next, Prev, Empty, TAG4;
 FROM Dialogs IMPORT DialogPtr, dNAME, AllocDialog, ModifyDialog, gVDIR,
  gATEND, BeginGroup, gTYPE, gBUTTON, gBOOL, gSTRNG, gID, gACTIVE, gSELECT,
  gTEXT, gBORDER, gTXTSIZE, gHILITE, gFILL, hNONE, hINVBORD, hINVALL, gAUTOSIZE,
  gAUTOCENTER, gAUTORIGHT, AddGadget, EndGroup, RemoveGadget, ModifyGadget,
  GetItemAttr, DialogOk, NoMem, TooBig, DisplayDialog, FreeDialog;
 FROM Graphics IMPORT  AreaPtr, CreateArea, DeleteArea, aSIZEX, aSIZEY,
  aCOLOR, aDISPLAY, AreaToFront, SetPalette;
 FROM Input IMPORT ButtonDown, ButtonUp, eNUL, eGADGET, EventTypeSet,
  Event, AddEvents, RemEvents, WaitEvent, GetEvent, FlushEvents, SetBusyStat;
 FROM SaveBox IMPORT BoxPtr, OpenBox, GetNextEntry, LoadEntry, SaveEntry,
  DeleteEntry, CloseBox;

 TYPE
  Entry = RECORD
   node: Node;
   name: StrPtr;
  END;
  EntryPtr = POINTER TO Entry;
  String = ARRAY[0..15] OF CHAR;

 VAR
  box: BoxPtr;
  area: AreaPtr;
  d: DialogPtr;
  list: List;
  event: Event;
  err: INTEGER;
  entry: EntryPtr;
  c, nbEntry, posEntry: CARDINAL;
  name, data: String;
  tags: ARRAY[0..15] OF LONGCARD;

 PROCEDURE GetEntryName(pos: CARDINAL): StrPtr;
  VAR
   entry: EntryPtr;
   temp: ADDRESS;
   node: NodePtr;
 BEGIN
  entry:= First(list);
  WHILE (pos > 1) AND (entry <> ADR(list.tail)) DO
   DEC(pos);
   entry:= Next(entry^.node)
  END;
  IF entry = ADR(list.tail) THEN RETURN NIL END;
  RETURN entry^.name
 END GetEntryName;

 PROCEDURE SetEntries;
  VAR
   gad, e: CARDINAL;
   name: StrPtr;
 BEGIN
  e:= posEntry;
  gad:= 1;
  LOOP
   name:= GetEntryName(e);
   IF name = NIL THEN EXIT END;
   ModifyGadget(d, gad, TAG(tags, gTEXT, name, gACTIVE, TRUE, 0));
   INC(e); INC(gad);
   IF gad > 5 THEN EXIT END
  END;
  ModifyGadget(d, 6, TAG(tags, gACTIVE, posEntry > 1, 0));
  ModifyGadget(d, 7, TAG(tags, gACTIVE, posEntry + 4 < nbEntry, 0));
  WHILE gad <= 5 DO
   ModifyGadget(d, gad, TAG(tags, gACTIVE, FALSE, gTEXT, ADR(""), 0));
   INC(gad)
  END
 END SetEntries;

 PROCEDURE FreeEntries;
  VAR
   entry: EntryPtr;
 BEGIN
  WHILE NOT(Empty(list)) DO
   entry:= First(list);
   FreeMem(entry^.name);
   Remove(entry^.node);
   FreeMem(entry)
  END
 END FreeEntries;

 PROCEDURE ReadEntries;
  VAR
   c: CARDINAL;
   entry: EntryPtr;
   newName: StrPtr;
 BEGIN
  SetBusyStat(2);
  RemEvents(EventTypeSet{eGADGET});
  FlushEvents;
  nbEntry:= 0; posEntry:= 1;
  FreeEntries;
  LOOP
   newName:= GetNextEntry(box);
   IF newName = NIL THEN EXIT END;
   INC(nbEntry);
   entry:= AllocMem(SIZE(Entry));
   CheckMem(entry);
   entry^.name:= AllocMem(9);
   CheckMem(entry^.name);
   CopyStr(newName, entry^.name, 9);
   AddHead(list, entry^.node)
  END;
  SetEntries;
  AddEvents(EventTypeSet{eGADGET})
 END ReadEntries;

BEGIN

 SetBusyStat(2);
 area:= CreateArea(TAG(tags, aSIZEX, 320, aSIZEY, 240, aCOLOR, 4, aDISPLAY, TRUE, 0));
 CheckMemBool(area = NIL);
 AreaToFront(area);
 AddEvents(EventTypeSet{eGADGET});
 d:= AllocDialog(TAG(tags, dNAME, ADR("DialogTest"), 0));
 CheckMemBool(d = NIL);
 BeginGroup(d, TAG(tags, gVDIR, FALSE, 0));
  BeginGroup(d, TAG(tags, gVDIR, TRUE, gBORDER, TRUE, 0));
   AddGadget(d, TAG(tags, gTYPE, gBUTTON, gBORDER, FALSE, gTEXT, ADR("Saved Data:"), gAUTOCENTER, TRUE, 0));
   BeginGroup(d, TAG(tags, gVDIR, TRUE, gBORDER, TRUE, 0));
    BeginGroup(d, TAG(tags, gVDIR, TRUE, 0));
     AddGadget(d, TAG(tags, gTYPE, gBUTTON, gID, 6, gTEXT, ADR("reverse"), gHILITE, hINVBORD, 0));
     FOR c:= 1 TO 5 DO
      AddGadget(d, TAG(tags, gTYPE, gBUTTON, gID, c, gTEXT, ADR(""), gBORDER, FALSE, gHILITE, hINVALL, 0))
     END;
     AddGadget(d, TAG(tags, gTYPE, gBUTTON, gID, 7, gTEXT, ADR("forward"), gHILITE, hINVBORD, 0));
    EndGroup(d);
    BeginGroup(d, TAG(tags, gBORDER, TRUE, 0));
     AddGadget(d, TAG(tags, gTYPE, gSTRNG, gID, 8, gTEXT, ADR(name), gTXTSIZE, 9, gBORDER, FALSE, gHILITE, hNONE, 0));
    EndGroup(d);
   EndGroup(d);
  EndGroup(d);
  BeginGroup(d, TAG(tags, gVDIR, TRUE, gBORDER, TRUE, 0));
   AddGadget(d, TAG(tags, gTYPE, gBUTTON, gTEXT, ADR("Current Data:"), gBORDER, FALSE, gHILITE, hNONE, 0));
   AddGadget(d, TAG(tags, gTYPE, gBUTTON, gTEXT, ADR("Password: "), gBORDER, FALSE, gHILITE, hNONE, gAUTOCENTER, TRUE, 0));
   BeginGroup(d, TAG(tags, gBORDER, TRUE, 0));
    AddGadget(d, TAG(tags, gTYPE, gSTRNG, gID, 9, gTEXT, ADR(data), gTXTSIZE, 16, gBORDER, FALSE, gHILITE, hNONE, 0));
   EndGroup(d);
   BeginGroup(d, TAG(tags, gVDIR, FALSE, gBORDER, FALSE, 0));
    BeginGroup(d, TAG(tags, gVDIR, TRUE, gBORDER, FALSE, gATEND, TRUE, 0));
     AddGadget(d, TAG(tags, gTYPE, gBUTTON, gID, 10, gTEXT, ADR("Load"), gHILITE, hINVALL, 0));
     AddGadget(d, TAG4(gTYPE, gBUTTON, gID, 11, gTEXT, ADR("Save"), gHILITE, hINVALL));
     AddGadget(d, TAG(tags, gTYPE, gBUTTON, gID, 12, gTEXT, ADR("Delete"), gHILITE, hINVALL, 0));
    EndGroup(d);
    AddGadget(d, TAG(tags, gTYPE, gBUTTON, gID, 13, gTEXT, ADR("EXIT"), gHILITE, hINVALL, 0));
   EndGroup(d);
  EndGroup(d);
 EndGroup(d);
 err:= DisplayDialog(d, NIL);
 Check(err <> DialogOk, ADR("Cannot open"), ADR("Dialog"));
 InitList(list);

 box:= OpenBox(ADR("DTest"));
 CheckMemBool(box = NIL);

 LOOP
  ReadEntries;
  LOOP
   SetBusyStat(0);
   WaitEvent;
   GetEvent(event);
   IF event.type = eGADGET THEN
    IF (event.code1 = 13) OR (event.code1 = MAX(CARDINAL)) THEN EXIT END;
    IF (event.code1 >= 1) AND (event.code1 <= 5) THEN
     CopyStr(GetEntryName(posEntry + event.code1 - 1), ADR(name), 9);
     ModifyGadget(d, 8, TAG(tags, gTEXT, ADR(name), gTXTSIZE, 9, 0))
    ELSIF event.code1 = 6 THEN
     IF posEntry > 1 THEN
      DEC(posEntry);
      SetEntries
     END
    ELSIF event.code1 = 7 THEN
     IF posEntry + 4 < nbEntry THEN
      INC(posEntry);
      SetEntries
     END
    ELSIF event.code1 = 10 THEN
     SetBusyStat(2);
     IGNORE LoadEntry(box, ADR(name), ADR(data), 16);
     data[15]:= 0C;
     ModifyGadget(d, 9, TAG(tags, gTEXT, ADR(data), gTXTSIZE, 16, 0))
    ELSIF event.code1 = 11 THEN
     SetBusyStat(2);
     IGNORE SaveEntry(box, ADR(name), ADR(data), 16);
     EXIT
    ELSIF event.code1 = 12 THEN
     SetBusyStat(2);
     IGNORE DeleteEntry(box, ADR(name));
     EXIT
    END
   END;
  END;
  IF (event.code1 = 13) OR (event.code1 = MAX(CARDINAL)) THEN EXIT END;
 END;
 SetBusyStat(2);
 FreeEntries;

CLOSE

 CloseBox(box);
 FreeDialog(d);
 DeleteArea(area);

END DialogTest.
