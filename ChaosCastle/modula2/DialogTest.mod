MODULE DialogTest;

 FROM SYSTEM IMPORT ADR, ADDRESS, VAL;
 IMPORT Memory;
 FROM Dialogs IMPORT GadgetPtr, DialogOk, DialogNoMem, dFLAGS, dMASK,
  dfACTIVE, dfSELECT, dfBORDER, dfCLOSE, dfDOWNEVENT, dfUPEVENT,
  dfAUTOLEFT, dfAUTORIGHT, dfAUTOUP, dfAUTODOWN, dfJUSTIFY, dfVDIR,
  dfSIZE, dTEXT, dRFLAGS, dGAPSIZE, dDialog, dGroup, dButton, dBool, dCycle,
  AllocGadget, ModifyGadget, RefreshGadget, AddGadget, RemoveGadget, FreeGadget, noGadget,
  CreateGadget, AddNewGadget, dSwitch, dFILL, dSPAN, dProgress, dCheckbox,
  DeepFreeGadget, dScroller, dSlider, dTextEdit, dIntEdit, dTXTLEN, dINTVAL,
  dLabel, dWIDTH, dHEIGHT, dfSCROLLY;
 FROM Input IMPORT eKEYBOARD, eGADGET, EventTypes, EventTypeSet, Event,
  AddEvents, RemEvents, WaitEvent, GetEvent;
 FROM Checks IMPORT AddTermProc;
 FROM Terminal IMPORT WriteInt, WriteString, WriteLn;


 VAR
  dialog, button, bool, cycle, group, switch, switch1, switch2,
   progress, cb, scroller, slider, te, int, label: GadgetPtr;
  event: Event;
  fill: CARDINAL;
  tags: ARRAY[0..20] OF LONGCARD;
  buffer: ARRAY[0..19] OF CHAR;
  dummy: INTEGER;


 PROCEDURE Close;
 BEGIN
  DeepFreeGadget(dialog);
(*  FreeGadget(button);
  FreeGadget(bool);
  FreeGadget(cycle);
  FreeGadget(switch);
  FreeGadget(switch1);
  FreeGadget(switch2);
  FreeGadget(progress);
  FreeGadget(group); *)
 END Close;

BEGIN

 AddTermProc(Close);
 WriteString("Creating Dialog"); WriteLn;
 dialog:= AllocGadget(dDialog);
 IF dialog = NIL THEN HALT END;
 ModifyGadget(dialog, Memory.TAG3(dFLAGS, dfCLOSE + dfVDIR + dfSCROLLY + dfSIZE + dfDOWNEVENT, dHEIGHT, 50, dTEXT, ADR("Dialog")));

(* WriteString("Creating Group"); WriteLn;
 group:= AllocGadget(dGroup);
 IF group = NIL THEN HALT END;
 ModifyGadget(group, Memory.TAG2(dFLAGS, dfVDIR, dSPAN, 0));
 AddGadget(dialog, group, noGadget); *)
 group:= dialog;

 WriteString("Creating Button"); WriteLn;
 button:= AllocGadget(dButton);
 IF button = NIL THEN HALT END;
 ModifyGadget(button, Memory.TAG2(dTEXT, ADR("Button"), dFLAGS, dfDOWNEVENT));
 AddGadget(group, button, noGadget);

 bool:= CreateGadget(dBool, Memory.TAG2(dTEXT, ADR("Bool"), dFLAGS, dfAUTOLEFT + dfAUTORIGHT));
 IF bool = NIL THEN HALT END;
 AddGadget(group, bool, noGadget);

 cycle:= AddNewGadget(group, dCycle, Memory.TAG2(dTEXT, ADR("Cycle"), dFLAGS, dfAUTOUP + dfAUTODOWN));
 IF cycle = NIL THEN HALT END;

 switch:= AddNewGadget(group, dSwitch, Memory.TAG2(dTEXT, ADR("Switch"), dFLAGS, dfAUTOLEFT + dfAUTOUP + dfAUTODOWN));
 IF switch = NIL THEN HALT END;

 switch1:= AddNewGadget(group, dSwitch, Memory.TAG3(dTEXT, ADR("Sub-Switch 1"), dFLAGS, dfAUTOLEFT + dfAUTOUP + dfAUTODOWN, dFILL, 1));
 IF switch1 = NIL THEN HALT END;

 switch2:= AddNewGadget(group, dSwitch, Memory.TAG3(dTEXT, ADR("Sub-Switch 2"), dFLAGS, dfAUTOLEFT + dfAUTOUP + dfAUTODOWN, dFILL, 1));
 IF switch2 = NIL THEN HALT END;

 progress:= AddNewGadget(group, dProgress, Memory.TAG2(dTEXT, ADR("Progress"), dFLAGS, dfJUSTIFY));
 IF progress = NIL THEN HALT END;

 cb:= AddNewGadget(group, dCheckbox, Memory.TAG2(dTEXT, ADR("Checkbox"), dFLAGS, dfAUTOLEFT));
 IF cb = NIL THEN HALT END;

 scroller:= AddNewGadget(group, dScroller, NIL);
 IF scroller = NIL THEN HALT END;

 slider:= AddNewGadget(group, dSlider, NIL);
 IF slider = NIL THEN HALT END;

 buffer:= "Text Edit";
 te:= AddNewGadget(group, dTextEdit, Memory.TAG2(dTEXT, ADR(buffer), dTXTLEN, 20));
 IF te = NIL THEN HALT END;

 int:= AddNewGadget(group, dIntEdit, Memory.TAG1(dINTVAL, -142857));
 IF int = NIL THEN HALT END;

 label:= AddNewGadget(group, dLabel, Memory.TAG1(dTEXT, ADR("Label")));
 IF label = NIL THEN HALT END;

 IF RefreshGadget(dialog) <> DialogOk THEN HALT END;

 AddEvents(EventTypeSet{eKEYBOARD, eGADGET});
 LOOP
  WaitEvent;
  GetEvent(event);
  IF (event.type = eGADGET) THEN
   WriteString("Gadget Event: ");
   WriteInt(VAL(LONGINT, event.gadget), -1);
   WriteLn;
   IF (event.gadget = dialog) AND (event.gadgetUp) THEN EXIT END
  ELSIF (event.type = eKEYBOARD) THEN
   WriteString("Keyboard"); WriteLn;
   IF event.ch = "r" THEN
    IF RefreshGadget(dialog) <> DialogOk THEN HALT END
   END;
   IF event.ch = "+" THEN
    ModifyGadget(scroller, Memory.TAG1(dFLAGS, dfACTIVE))
   ELSIF event.ch = "-" THEN
    ModifyGadget(scroller, Memory.TAG1(dRFLAGS, dfACTIVE))
   ELSIF event.ch = "*" THEN
    ModifyGadget(cycle, Memory.TAG1(dFLAGS, dfSELECT))
   ELSIF event.ch = "/" THEN
    ModifyGadget(cycle, Memory.TAG1(dRFLAGS, dfSELECT))
   ELSIF event.ch = "t" THEN
    ModifyGadget(cycle, Memory.TAG1(dTEXT, ADR("----- New Cycle -----")));
    dummy:= RefreshGadget(dialog)
   END;
   IF event.ch = "f" THEN
    IF fill = 65535 THEN fill:= 0 ELSE INC(fill, 4369) END;
    ModifyGadget(progress, Memory.TAG1(dFILL, fill));
   END;
   IF event.ch = "h" THEN HALT END;
   IF event.ch = "q" THEN EXIT END
  END
 END;

 Close;

END DialogTest.
