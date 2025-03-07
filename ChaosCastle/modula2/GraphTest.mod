MODULE GraphTest;

 FROM SYSTEM IMPORT ADDRESS, ADR, LONGSET, CAST;
 FROM Memory IMPORT AllocMem, FreeMem, SET16, CARD32, TAG1, TAG2, TAG3, TAG4, TAG5;
 FROM Checks IMPORT CheckMem;
 FROM Graphics IMPORT AreaPtr, CreateArea, DeleteArea, SetArea, SetPalette,
  CopyRect, WaitTOF, SwitchArea, CopyMask, SetCopyMode, Modes, CopyMode, aSIZEX,
  aSIZEY, aCOLOR, aTYPE, atDISPLAY, atBUFFER, atMEMORY, atMASK, ScaleRect, AreaToFront,
  SetBuffer, DrawImage, DrawLine, FillRect, FillEllipse, OpenPoly, AddLine,
  FillPoly, SetPat, SetTextSize, SetTextPos, TextWidth, DrawText, SetPen,
  DrawPixel, Image, ImagePtr, atVIRTUAL;
 FROM Menus IMPORT AddMenu, AddItem, ModifyItem, RemoveMenus, mNAME, mNUM,
  mITEM, mENABLE, mCOMM, mCHK;
 FROM Input IMPORT Event, EventTypes, EventTypeSet, AddEvents, RemEvents,
  WaitEvent, GetEvent, eNUL, eKEYBOARD, eMOUSE, eMENU, eGADGET, eSYS, eTIMER,
  GetStick, GetMouse, SetBusyStat;
 FROM Clock IMPORT AllocTime, StartTime, GetTime, FreeTime, TimePtr,
  TimeEvent, WaitTime;
 IMPORT Dialogs;
 FROM Terminal IMPORT WriteInt, WriteString, WriteLn, Write;

 CONST
  COL = 16;
  W = 320; H = 240;

 TYPE
  StrPtr = POINTER TO ARRAY[0..69] OF CHAR;
  TC = ARRAY[0..239],[0..319] OF RECORD a, r, g, b: SHORTCARD END;
  PIX = ARRAY[0..255],[0..255] OF SHORTCARD;

 VAR
  col: LONGCARD;
  c: CARDINAL;
  x, y, k, l: LONGINT;
  mx, my: INTEGER;
  a1, a2, a3, msk: AreaPtr;
  bob, bobmsk: AreaPtr;
  pix: POINTER TO PIX;
  tc: POINTER TO TC;
  b, ok: BOOLEAN;
  t: TimePtr;
  tags: ARRAY[0..10] OF ADDRESS;
  event: Event;
  image: Image;
  joy: SET16;
  b0: BOOLEAN;

BEGIN

(*
 AddMenu(TAG(tags, mNAME, ADR("Menu1"), 0));
 AddMenu(TAG(tags, mNAME, ADR("Menu2"), 0));
 AddItem(TAG(tags, mNUM, 1, mNAME, ADR("Item1"), 0));
 AddItem(TAG(tags, mNUM, 1, mNAME, ADR("Item2"), mCOMM, "2", 0));
 AddItem(TAG(tags, mNUM, 2, mNAME, ADR("  Item1"), mCHK, TRUE, 0));
 AddItem(TAG(tags, mNUM, 2, mNAME, ADR("  Item2"), mCHK, TRUE, 0));
 AddMenu(TAG(tags, mNAME, ADR("Menu3"), 0));
 AddItem(TAG(tags, mNUM, 3, mNAME, ADR("Test 1"), 0));
 FOR y:= 1 TO 25 DO
  AddItem(TAG(tags, mNUM, 3, mNAME, ADR("Height Test"), 0))
 END;
 ModifyItem(TAG(tags, mNUM, 3, mITEM, 1, mENABLE, FALSE, 0));
 ModifyItem(TAG(tags, mNUM, 2, mITEM, 2, mCHK, TRUE, 0));
*)

 t:= AllocTime(1000);
 IF t = NIL THEN HALT END;
 a1:= CreateArea(TAG4(aSIZEX, W, aSIZEY, H, aCOLOR, 0, aTYPE, atDISPLAY));
 IF a1 = NIL THEN HALT END;
 SetArea(a1);
 AreaToFront;
(* SetPalette(0, 0, 0, 0);
 SetPalette(1, 255, 0, 0);
 SetPalette(2, 0, 255, 0);
 SetPalette(3, 0, 0, 255);
 SetPalette(4, 255, 255, 255);
 SetPalette(5, 255, 255, 0);
 FOR c:= 6 TO 15 DO
  SetPalette(c, (c MOD 3) * 127, ((c + 1) MOD 3) * 127, ((c + 2) MOD 3) * 127)
 END; *)

 WriteString("Drawings: ");
 StartTime(t);
 SetPen(0FFFF00H);
 DrawLine(0, 0, 319, 199);
 b:= FALSE;
 FOR c:= 90 TO 2 BY -4 DO
  IF b THEN SetPen(0FF0000H) ELSE SetPen(000FF00H) END;
  b:= NOT(b);
  FillEllipse(160 - c, 100 - c, 160 + c, 100 + c)
 END;
 FOR x:= 20 TO 300 BY 20 DO
  FillEllipse(x - 8, 5, x + 8, 35)
 END;
 SetPen(000FFFFH);
 FillRect(100, 100, 200, 200);
 SetPen(00000FFH);
 FillEllipse(100, 100, 200, 200);
 SetPen(0DD9900H);
 OpenPoly(10, 10);
 AddLine(80, 10);
 AddLine(40, 20);
 AddLine(80, 30);
 AddLine(10, 30);
 FillPoly;
 SetCopyMode(CopyMode{snd, sd, nsd});
 FOR c:= 0 TO 4 DO
  SetPat(c);
  FillRect(160, c * 16, 260, c * 16 + 16)
 END;
 AreaToFront;
 SetCopyMode(CopyMode{nsd, sd, snd});
 SetTextSize(48);
 c:= TextWidth(ADR("Text"));
 SetTextPos((320 - c) DIV 2, 50);
 DrawText(ADR("Text"));
 SetCopyMode(CopyMode{nsd, snd});
 FOR x:= 0 TO 190 BY 10 DO
  DrawLine(0, x, 319, x)
 END;
 SetArea(a1);
 SetCopyMode(CopyMode{snd, sd});
 SetBusyStat(1);
 SetPen(0);
 FillRect(0, 0, 32, 32);
 SetPen(000FF00H); FillEllipse(0, 0, 24, 24);
 SetPen(00044FFH); FillEllipse(6, 6, 18, 18);
 WriteInt(GetTime(t), -1); WriteLn;
 StartTime(t); b0:= WaitTime(t, 2000);

 WriteString("Blit scale: ");
(* SetCopyMode(CopyMode{snd, nsd, sd}); *)
 StartTime(t); SetArea(a1);
 FOR y:= 50 TO 149 BY 10 DO
  FOR x:= 1 TO 100 BY 10 DO
   ScaleRect(a1, 0, 0, 24, 24, x, y, x + 100, y + 50)
  END
 END;
 ScaleRect(a1, 0, 0, 240, 200, 240, 0, 320, 200);
 ScaleRect(a1, 0, 0, 240, 150, 0, 150, 240, 200);
 ScaleRect(a1, 0, 0, 240, 150, 240, 150, 320, 200);
 FOR x:= 5 TO 200 BY 5 DO
  ScaleRect(a1, 0, 0, 24, 24, 160 - x, 30, 160 + x, 30 + x)
 END;
 WriteInt(GetTime(t), -1); WriteLn;

 WriteString("Write pixel: ");
 StartTime(t);
 col:= 0;
 FOR y:= 0 TO 127 DO
  FOR x:= 0 TO 255 DO
   col:= VAL(CARD32, x) * 65536 + VAL(CARD32, 255-x) * 256 + VAL(CARD32, y) * 2;
   SetPen(col);
   DrawPixel(x, y)
  END
 END;
 WriteInt(GetTime(t), -1); WriteLn;
 b0:= WaitTime(t, 1000);


 tc:= AllocMem(SIZE(TC));
 CheckMem(tc);
 FOR y:= 0 TO 239 DO
  FOR x:= 0 TO 319 DO
   WITH tc^[y][x] DO
    r:= (x * y) MOD 256;
    g:= (x * 256 / (y + 1)) MOD 256;
    b:= (x * x + y * y) MOD 256;
   END
  END
 END;
 WriteString("True color -> Planar: ");
 StartTime(t);
 image.data:= tc;
 image.bitPerPix:= 32; image.bytePerRow:= 4 * 320;
 image.width:= 320; image.height:= 240;
 image.zw:= 1; image.zh:= 1;
 DrawImage(ADR(image), 0, 0, 0, 0, 320, 240);
 WriteInt(GetTime(t), -1); WriteLn;
 FreeMem(tc);
 AddEvents(EventTypeSet{eMOUSE});
 REPEAT
  WaitEvent;
  GetEvent(event)
 UNTIL event.type = eMOUSE;

 pix:= AllocMem(SIZE(PIX));
 CheckMem(pix);
 FOR y:= 0 TO 255 DO
  FOR x:= 0 TO 255 DO
   pix^[y, x]:= x
  END
 END;
 FillRect(0, 0, 320, 256);
 WriteString("Chunky (8bit) -> Planar: ");
 StartTime(t);
 image.data:= pix;
 image.bitPerPix:= 8; image.bytePerRow:= 256;
 image.width:= 256; image.height:= 256;
 image.zw:= 1; image.zh:= 1;
 DrawImage(ADR(image), 0, 0, 0, 0, 256, 256);
 WriteInt(GetTime(t), -1); WriteLn;
 StartTime(t);
 b0:= WaitTime(t, 1000);
 FillRect(0, 0, 256, 256);
 WriteString("PixMap (4bit) -> Planar: ");
 StartTime(t);
 image.data:= pix;
 image.bitPerPix:= 4; image.bytePerRow:= 256;
 image.width:= 256; image.height:= 256;
 DrawImage(ADR(image), 0, 0, 0, 0, 256, 256);
 WriteInt(GetTime(t), -1); WriteLn;
 StartTime(t);
 b0:= WaitTime(t, 1000);
 FillRect(0, 0, 256, 256);
 WriteString("Pixmap (1bit) -> Planar: ");
 StartTime(t);
 image.data:= pix;
 image.bitPerPix:= 1; image.bytePerRow:= 256;
 image.width:= 256; image.height:= 256;
 DrawImage(ADR(image), 0, 0, 0, 0, 256, 256);
 WriteInt(GetTime(t), -1); WriteLn;
 StartTime(t);
 b0:= WaitTime(t, 1000);
 DeleteArea(a1);
 FreeMem(pix);

 (*ModifyItem(TAG(tags, mNUM, 3, mITEM, 1, mNAME, ADR("Test 2"), 0));*)
 a1:= CreateArea(TAG4(aSIZEX, W, aSIZEY, H, aCOLOR, COL, aTYPE, atBUFFER));
 IF a1 = NIL THEN HALT END;
 SetArea(a1);
 SetPalette(0, 0, 0, 0);
 SetPalette(1, 255, 0, 0);
 SetPalette(2, 0, 255, 0);
 SetPalette(3, 0, 0, 255);
 SetPalette(4, 255, 255, 255);
 SetPalette(5, 255, 255, 0);
 SetPalette(15, 0, 255, 255);
 a2:= CreateArea(TAG3(aSIZEX, 32, aSIZEY, 32, aCOLOR, COL));
 msk:= CreateArea(TAG4(aSIZEX, 32, aSIZEY, 32, aCOLOR, 2, aTYPE, atMASK));
 IF (a1 = NIL) OR (a2 = NIL) OR (msk = NIL) THEN HALT END;
 SetArea(a1); AreaToFront;
 SetArea(a2);
 SetPen(1);
 FillEllipse(0, 0, 32, 32);
 SetArea(msk);
 SetPen(1); SetPat(1);
 FillEllipse(0, 0, 32, 32);
 bob:= CreateArea(TAG3(aSIZEX, 64, aSIZEY, 24, aCOLOR, COL));
 bobmsk:= CreateArea(TAG4(aSIZEX, 64, aSIZEY, 24, aCOLOR, 2, aTYPE, atMASK));
 IF (bob = NIL) OR (bobmsk = NIL) THEN HALT END;
 SetArea(bob); SetPen(5);
 FillEllipse(0, 0, 24, 24);
 SetArea(bobmsk); SetPen(1);
 FillEllipse(0, 0, 24, 24);
 SetArea(a1);
 SetBusyStat(2);
 SetCopyMode(CopyMode{snd, sd});
 a3:= CreateArea(TAG3(aSIZEX, W + 32, aSIZEY, H + 32, aCOLOR, COL));
 IF a3 = NIL THEN HALT END;
 SetArea(a3);
 FillRect(0, 0, W + 32, H + 32);
 FOR y:= 0 TO H + 31 BY 32 DO
  FOR x:= 0 TO W + 31 BY 32 DO
   CopyRect(a2, 0, 0, x, y, 32, 32)
  END
 END;
 SetArea(a1);
 WriteString("Blit test 2: ");
 StartTime(t);
 FOR k:= 0 TO 239 DO
  CopyRect(a3, k MOD 32, k MOD 32, 0, 0, 32, 32);
  CopyRect(a3, 32 + k MOD 32, k MOD 32, 32, 0, W - 32, 32);
  CopyRect(a3, k MOD 32, 32 + k MOD 32, 0, 32, 32, H - 32);
  CopyRect(a3, 32 + k MOD 32, 32 + k MOD 32, 32, 32, W - 32, H - 32);
  SetPalette(1, (k MOD 16) * 16, 0, 255 - (k MOD 16) * 16);
  SetPalette(5, 255 - (k MOD 16) * 16, 255, 0);
  x:= 0;
  FOR y:= 0 TO H - 25 BY 25 DO
(*   FOR x:= 0 TO 100 BY 25 DO *)
    CopyMask(bob, bobmsk, 0, 0, k(* + x*), y, 24, 24)
(*   END *)
  END;
  SwitchArea
 END;
 WriteInt(GetTime(t), -1); WriteLn;
 SetBusyStat(0);
 StartTime(t);
 LOOP
  k:= GetTime(t) DIV 20;
  IF k >= 240 THEN EXIT END;
  CopyRect(a3, k MOD 32, k MOD 32, 0, 0, 32, 32);
  CopyRect(a3, 32 + k MOD 32, k MOD 32, 32, 0, W - 32, 32);
  CopyRect(a3, k MOD 32, 32 + k MOD 32, 0, 32, 32, H - 32);
  CopyRect(a3, 32 + k MOD 32, 32 + k MOD 32, 32, 32, W - 32, H - 32);
  SetPalette(1, (k MOD 16) * 16, 0, 255 - (k MOD 16) * 16);
  SetPalette(5, 255 - (k MOD 16) * 16, 255, 0);
  x:= 0;
  FOR y:= 0 TO H - 25 BY 25 DO
   FOR x:= 0 TO 100 BY 25 DO
    CopyMask(bob, bobmsk, 0, 0, k + x, y, 24, 24)
   END
  END;
  SwitchArea
 END;


 SetBuffer(TRUE, FALSE);
 AddEvents(EventTypeSet{eKEYBOARD, eMOUSE, eMENU, eSYS, eTIMER});
 StartTime(t);
 TimeEvent(t, 400);
 REPEAT
  WaitEvent;
  GetEvent(event);
  CASE event.type OF
    eNUL: WriteString("NUL")
   |eKEYBOARD: WriteString("KEYBOARD")
   |eMOUSE: WriteString("MOUSE")
   |eMENU: WriteString("MENU")
   |eSYS: WriteString("SYS")
   |eTIMER: WriteString("TIMER")
   ELSE
  END;
  WriteInt(event.menu, 4);
  (*WriteInt(event.item, 4);*) WriteLn;
  joy:= GetStick();
  FOR c:= 0 TO 15 DO
   IF c IN joy THEN Write("*") ELSE Write("-") END
  END;
  GetMouse(mx, my);
  WriteInt(mx, 4); WriteInt(my, 4);
  WriteLn; WriteLn;
 UNTIL (event.type = eKEYBOARD) AND (event.ch = 3C);
 RemEvents(EventTypeSet{eKEYBOARD, eMOUSE, eSYS, eTIMER});

(*
CLOSE

 IF t <> NIL THEN FreeTime(t) END;
 IF a1 <> NIL THEN DeleteArea(a1) END;
 IF a2 <> NIL THEN DeleteArea(a2) END;
 IF a3 <> NIL THEN DeleteArea(a3) END;
 IF msk <> NIL THEN DeleteArea(msk) END;
 IF bob <> NIL THEN DeleteArea(bob) END;
 IF bobmsk <> NIL THEN  DeleteArea(bobmsk) END;
 RemoveMenus(NIL);
 *)

END GraphTest.
