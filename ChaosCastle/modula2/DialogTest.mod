MODULE DialogTest;

 FROM SYSTEM IMPORT ADR, ADDRESS, TAG;
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
 ModifyGadget(dialog, TAG(tags, dFLAGS, dfCLOSE + dfVDIR + dfSCROLLY + dfSIZE + dfDOWNEVENT, dHEIGHT, 50, dTEXT, ADR("Dialog")));

(* WriteString("Creating Group"); WriteLn;
 group:= AllocGadget(dGroup);
 IF group = NIL THEN HALT END;
 ModifyGadget(group, TAG(tags, dFLAGS, dfVDIR, dSPAN, 0, 0));
 AddGadget(dialog, group, noGadget); *)
 group:= dialog;

 WriteString("Creating Button"); WriteLn;
 button:= AllocGadget(dButton);
 IF button = NIL THEN HALT END;
 ModifyGadget(button, TAG(tags, dTEXT, ADR("Button"), dFLAGS, dfDOWNEVENT, 0));
 AddGadget(group, button, noGadget);

 bool:= CreateGadget(dBool, TAG(tags, dTEXT, ADR("Bool"), dFLAGS, dfAUTOLEFT + dfAUTORIGHT, 0));
 IF bool = NIL THEN HALT END;
 AddGadget(group, bool, noGadget);

 cycle:= AddNewGadget(group, dCycle, TAG(tags, dTEXT, ADR("Cycle"), dFLAGS, dfAUTOUP + dfAUTODOWN, 0));
 IF cycle = NIL THEN HALT END;

 switch:= AddNewGadget(group, dSwitch, TAG(tags, dTEXT, ADR("Switch"), dFLAGS, dfAUTOLEFT + dfAUTOUP + dfAUTODOWN, 0));
 IF switch = NIL THEN HALT END;

 switch1:= AddNewGadget(group, dSwitch, TAG(tags, dTEXT, ADR("Sub-Switch 1"), dFLAGS, dfAUTOLEFT + dfAUTOUP + dfAUTODOWN, dFILL, 1, 0));
 IF switch1 = NIL THEN HALT END;

 switch2:= AddNewGadget(group, dSwitch, TAG(tags, dTEXT, ADR("Sub-Switch 2"), dFLAGS, dfAUTOLEFT + dfAUTOUP + dfAUTODOWN, dFILL, 1, 0));
 IF switch2 = NIL THEN HALT END;

 progress:= AddNewGadget(group, dProgress, TAG(tags, dTEXT, ADR("Progress"), dFLAGS, dfJUSTIFY, 0));
 IF progress = NIL THEN HALT END;

 cb:= AddNewGadget(group, dCheckbox, TAG(tags, dTEXT, ADR("Checkbox"), dFLAGS, dfAUTOLEFT, 0));
 IF cb = NIL THEN HALT END;

 scroller:= AddNewGadget(group, dScroller, NIL);
 IF scroller = NIL THEN HALT END;

 slider:= AddNewGadget(group, dSlider, NIL);
 IF slider = NIL THEN HALT END;

 buffer:= "Text Edit";
 te:= AddNewGadget(group, dTextEdit, TAG(tags, dTEXT, ADR(buffer), dTXTLEN, 20, 0));
 IF te = NIL THEN HALT END;

 int:= AddNewGadget(group, dIntEdit, TAG(tags, dINTVAL, -142857, 0));
 IF int = NIL THEN HALT END;

 label:= AddNewGadget(group, dLabel, TAG(tags, dTEXT, ADR("Label"), 0));
 IF label = NIL THEN HALT END;

 IF RefreshGadget(dialog) <> DialogOk THEN HALT END;

 AddEvents(EventTypeSet{eKEYBOARD, eGADGET});
 LOOP
  WaitEvent;
  GetEvent(event);
  IF (event.type = eGADGET) THEN
   WriteString("Gadget Event: ");
   WriteInt(LONGINT(event.gadget), -1);
   WriteLn;
   IF (event.gadget = dialog) AND (event.gadgetUp) THEN EXIT END
  ELSIF (event.type = eKEYBOARD) THEN
   WriteString("Keyboard"); WriteLn;
   IF event.ch = "r" THEN
    IF RefreshGadget(dialog) <> DialogOk THEN HALT END
   END;
   IF event.ch = "+" THEN
    ModifyGadget(scroller, TAG(tags, dFLAGS, dfACTIVE, 0))
   ELSIF event.ch = "-" THEN
    ModifyGadget(scroller, TAG(tags, dRFLAGS, dfACTIVE, 0))
   ELSIF event.ch = "*" THEN
    ModifyGadget(cycle, TAG(tags, dFLAGS, dfSELECT, 0))
   ELSIF event.ch = "/" THEN
    ModifyGadget(cycle, TAG(tags, dRFLAGS, dfSELECT, 0))
   ELSIF event.ch = "t" THEN
    ModifyGadget(cycle, TAG(tags, dTEXT, ADR("----- New Cycle -----"), 0));
    IGNORE RefreshGadget(dialog)
   END;
   IF event.ch = "f" THEN
    IF fill = 65535 THEN fill:= 0 ELSE INC(fill, 4369) END;
    ModifyGadget(progress, TAG(tags, dFILL, fill, 0));
   END;
   IF event.ch = "h" THEN HALT END;
   IF event.ch = "q" THEN EXIT END
  END
 END;

 Close;

END DialogTest.
