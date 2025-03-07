IMPLEMENTATION MODULE Menus;

 FROM SYSTEM IMPORT ADR, ADDRESS, BITSET, SHORTSET, SHIFT;
 FROM IntuitionD IMPORT Menu, MenuPtr, MenuItem, MenuItemPtr, MenuItemFlags,
  MenuItemFlagSet, menuEnabled, WindowPtr, IntuiText, IntuiTextPtr, ScreenPtr;
 FROM IntuitionL IMPORT IntuiTextLength, ItemAddress;
 FROM AmigaBase IMPORT globals, SetMenus, SetWindows;
 FROM Memory IMPORT TagItem, TagItemPtr, tagUser, NextTag, tagMore, TAG3,
  LockR, LockW, Unlock;
 FROM GraphicsD IMPORT TextAttr, TextAttrPtr, FontStyleSet, FontFlags,
  FontFlagSet;
 FROM ExecD IMPORT MemReqs, MemReqSet;
 FROM ExecL IMPORT AllocMem, FreeMem;
 IMPORT R;

 CONST
  topazAttr = TextAttr{
   name: ADR("topaz.font"),
   ySize: 8,
   style: FontStyleSet{},
   flags: FontFlagSet{romFont}};

 VAR
  topaz, installed: BOOLEAN;

 PROCEDURE SetAllMenus;
  CONST
   gap = "OOOOO";
  VAR
   screen: ScreenPtr;
   ta: TextAttrPtr;
   menuPos: CARDINAL;
   maxWidth, maxHeight: CARDINAL;
   intuiText: IntuiText;
   iText: IntuiTextPtr;
   currentMenu{R.D7}: MenuPtr;
   ok, toobig: BOOLEAN;

  PROCEDURE SetItems(item: MenuItemPtr);
   VAR
    itemX{R.D6}, itemY{R.D5}, maxItemWidth{R.D4}: CARDINAL;
  BEGIN
   itemY:= 0; itemX:= 0; maxItemWidth:= 0;
   WHILE item <> NIL DO
    WITH item^ DO
     leftEdge:= itemX; topEdge:= itemY;
     iText:= itemFill;
     iText^.topEdge:= 1;
     iText^.iTextFont:= ta;
     width:= IntuiTextLength(iText);
     IF commSeq IN flags THEN
      intuiText.iText:= ADR(gap);
      INC(width, IntuiTextLength(ADR(intuiText)))
     END;
     IF CARDINAL(width) > maxItemWidth THEN maxItemWidth:= width END;
     height:= ta^.ySize + 2;
     INC(itemY, height);
     IF itemY >= maxHeight THEN
      INC(itemX, maxItemWidth + ta^.ySize);
      IF itemX + CARDINAL(width) >= maxWidth THEN toobig:= TRUE END;
      itemY:= 0; maxItemWidth:= 0
     END;
     SetItems(subItem);
     item:= nextItem
    END
   END
  END SetItems;

  (*$ EntryClear:= TRUE *)
 BEGIN
  LockW(globals);
  WITH globals^ DO
   IF (mainWindow = NIL) OR installed THEN Unlock(globals); RETURN END;
   screen:= mainWindow^.wScreen;
   IF topaz THEN
    ta:= ADR(topazAttr)
   ELSE
    ta:= screen^.font
   END;
   toobig:= FALSE;
   intuiText.backPen:= 1;
   intuiText.iTextFont:= ta;
   maxWidth:= screen^.width;
   maxHeight:= screen^.height;
   DEC(maxHeight, screen^.barHeight + INTEGER(ta^.ySize) * 2);
   menuPos:= 0;
   currentMenu:= firstMenu;
   WHILE currentMenu <> NIL DO
    WITH currentMenu^ DO
     intuiText.iText:= menuName;
     width:= IntuiTextLength(ADR(intuiText));
     INC(width, ta^.ySize);
     height:= ta^.ySize + 2;
     leftEdge:= menuPos;
     IF leftEdge + width >= INTEGER(maxWidth) THEN toobig:= TRUE END;
     INC(menuPos, width); INC(menuPos, ta^.ySize);
     SetItems(firstItem)
    END;
    currentMenu:= currentMenu^.nextMenu
   END;
   IF toobig AND NOT(topaz) THEN
    topaz:= TRUE;
    SetAllMenus
   END;
   installed:= (firstMenu <> NIL);
   topaz:= FALSE
  END;
  Unlock(globals)
 END SetAllMenus;
(*$ POP EntryClear *)

 PROCEDURE AddMenu(tags: TagItemPtr);
  VAR
   lastMenuPtr{R.D7}: POINTER TO MenuPtr;
   newMenu{R.D6}: MenuPtr;
   name{R.D5}: ADDRESS;
 BEGIN
  name:= NIL;
  LOOP
   WITH NextTag(tags)^ DO
    IF tag = 0 THEN EXIT
    ELSIF tag = mNAME THEN name:= data
    END
   END
  END;
  IF name = NIL THEN RETURN END;
  LockW(globals);
  WITH globals^ DO
   newMenu:= firstMenu; firstMenu:= NIL;
   IF installed THEN SetWindows END;
   firstMenu:= newMenu;
   lastMenuPtr:= ADR(firstMenu);
   WHILE lastMenuPtr^ <> NIL DO
    lastMenuPtr:= ADR(lastMenuPtr^^.nextMenu)
   END;
   newMenu:= AllocMem(SIZE(Menu), MemReqSet{memClear});
   IF newMenu <> NIL THEN
    WITH newMenu^ DO
     flags:= BITSET{menuEnabled};
     menuName:= name
    END;
    lastMenuPtr^:= newMenu
   END;
   installed:= FALSE
  END;
  Unlock(globals)
 END AddMenu;

 PROCEDURE AddItem(tags: TagItemPtr);
  VAR
   currentMenu{R.A3}: MenuPtr;
   lastItemPtr{R.A2}: POINTER TO MenuItemPtr;
   newItem{R.D5}: MenuItemPtr;
   newIText{R.D4}: IntuiTextPtr;
   mnum, inum, snum, isub, num{R.D3}: CARDINAL;
   orgTags{R.D6}: ADDRESS;
 BEGIN
  orgTags:= tags;
  snum:= 0;
  LOOP
   WITH NextTag(tags)^ DO
    IF tag = 0 THEN EXIT
    ELSIF tag = mNUM THEN mnum:= data
    ELSIF tag = mITEM THEN num:= data
    ELSIF tag = mSUB THEN snum:= data
    END
   END
  END;
  LockW(globals);
  currentMenu:= globals^.firstMenu; globals^.firstMenu:= NIL;
  IF installed THEN SetWindows END;
  globals^.firstMenu:= currentMenu;
  WHILE mnum > 1 DO
   IF currentMenu = NIL THEN Unlock(globals); RETURN END;
   currentMenu:= currentMenu^.nextMenu;
   DEC(mnum)
  END;
  IF currentMenu = NIL THEN Unlock(globals); RETURN END;
  lastItemPtr:= ADR(currentMenu^.firstItem);
  inum:= 1;
  IF num > 0 THEN DEC(num) END;
  WHILE (lastItemPtr^ <> NIL) AND (num > 0) DO
   DEC(num); INC(inum);
   lastItemPtr:= ADR(lastItemPtr^^.nextItem)
  END;
  isub:= 0;
  IF snum > 0 THEN
   lastItemPtr:= ADR(lastItemPtr^^.subItem);
   isub:= 1; DEC(snum);
   WHILE (lastItemPtr^ <> NIL) AND (snum > 0) DO
    DEC(snum); INC(isub);
    lastItemPtr:= ADR(lastItemPtr^^.nextItem)
   END
  END;
  newItem:= AllocMem(SIZE(MenuItem), MemReqSet{memClear});
  IF newItem <> NIL THEN
   newIText:= AllocMem(SIZE(IntuiText), MemReqSet{memClear});
   IF newIText <> NIL THEN
    WITH newIText^ DO
     backPen:= 1;
     iTextFont:= NIL
    END;
    WITH newItem^ DO
     flags:= MenuItemFlagSet{itemText, itemEnabled, highComp};
     itemFill:= newIText
    END;
    newItem^.nextItem:= lastItemPtr^;
    lastItemPtr^:= newItem;
    installed:= FALSE;
    ModifyItem(TAG3(mITEM, inum, mSUB, isub, tagMore, orgTags));
   ELSE
    FreeMem(newItem, SIZE(MenuItem))
   END
  END;
  Unlock(globals);
 END AddItem;

 PROCEDURE ModifyItem(tags: TagItemPtr);
  VAR
   tmp: MenuPtr;
   menuItem{R.D7}: MenuItemPtr;
   iText: IntuiTextPtr;
   name: ADDRESS;
   menu{R.D6}, item{R.D5}, sub: CARDINAL;
   given{R.D4}: BITSET;
   comm: CHAR;
   enable, chk: BOOLEAN;
 BEGIN
  IF tags = NIL THEN RETURN END;
  LockW(globals);
  WITH globals^ DO
   tmp:= firstMenu; firstMenu:= NIL;
   IF installed THEN SetWindows END;
   firstMenu:= tmp
  END;
  given:= BITSET{};
  sub:= 0;
  LOOP
   WITH NextTag(tags)^ DO
    IF tag = 0 THEN EXIT END;
   (*$ OverflowChk:= FALSE *)
    INCL(given, tag - tagUser);
   (*$ POP OverflowChk *)
    IF tag = mNAME THEN name:= data
    ELSIF tag = mNUM THEN menu:= data
    ELSIF tag = mITEM THEN item:= data
    ELSIF tag = mSUB THEN sub:= data
    ELSIF tag = mENABLE THEN enable:= (data <> 0)
    ELSIF tag = mCOMM THEN comm:= CHR(data)
    ELSIF tag = mCHK THEN chk:= (data <> 0)
    END
   END
  END;
  menuItem:= ItemAddress(globals^.firstMenu,
   SHIFT(sub, 11) + SHIFT(item - 1, 5) + menu - 1);
  IF menuItem <> NIL THEN
   WITH menuItem^ DO
    iText:= itemFill;
    IF 0 IN given THEN iText^.iText:= name END;
    IF 1 IN given THEN
     IF chk THEN INCL(flags, checked) ELSE EXCL(flags, checked) END
    END;
    IF 2 IN given THEN
     command:= comm;
     IF comm <> 0C THEN INCL(flags, commSeq) ELSE EXCL(flags, commSeq) END
    END;
    IF 3 IN given THEN
     IF enable THEN
      INCL(flags, itemEnabled);
      EXCL(flags, highBox)
     ELSE
      EXCL(flags, itemEnabled);
      INCL(flags, highBox)
     END
    END
   END
  END;
  Unlock(globals);
  IF installed THEN SetWindows END
 END ModifyItem;
(*$ POP EntryClear *)

 PROCEDURE RemoveMenus(tags: TagItemPtr);
  VAR
   curMenu{R.D7}, nextMenu: MenuPtr;
   curItem{R.D6}, curSub, nextItem, nextSub: MenuItemPtr;
   lastMenuPtr: POINTER TO MenuPtr;
   lastItemPtr, lastSubPtr: POINTER TO MenuItemPtr;
   menu, item, sub, cmenu, citem, csub: CARDINAL;
 BEGIN
  menu:= 0; item:= 0; sub:= 0;
  LOOP
   WITH NextTag(tags)^ DO
    IF tag = 0 THEN EXIT
    ELSIF tag = mNUM THEN menu:= data
    ELSIF tag = mITEM THEN item:= data
    ELSIF tag = mSUB THEN sub:= data
    END
   END
  END;
  LockW(globals);
  WITH globals^ DO
   curMenu:= firstMenu; firstMenu:= NIL;
   SetWindows; firstMenu:= curMenu;
   lastMenuPtr:= ADR(firstMenu)
  END;
  cmenu:= 0;
  WHILE curMenu <> NIL DO
   nextMenu:= curMenu^.nextMenu;
   INC(cmenu);
   IF (cmenu = menu) OR (menu = 0) THEN
    curItem:= curMenu^.firstItem;
    lastItemPtr:= ADR(curMenu^.firstItem);
    citem:= 0;
    WHILE curItem <> NIL DO
     nextItem:= curItem^.nextItem;
     INC(citem);
     IF (citem = item) OR (item = 0) THEN
      curSub:= curItem^.subItem;
      lastSubPtr:= ADR(curItem^.subItem);
      csub:= 0;
      WHILE curSub <> NIL DO
       nextSub:= curSub^.nextItem;
       INC(csub);
       IF (csub = sub) OR (sub = 0) THEN (* Free SubItem *)
        lastSubPtr^:= nextSub;
        IF curSub^.itemFill <> NIL THEN
         FreeMem(curSub^.itemFill, SIZE(IntuiText))
        END;
        FreeMem(curSub, SIZE(MenuItem))
       ELSE
        lastSubPtr:= ADR(curSub^.nextItem)
       END;
       curSub:= nextSub
      END;
      IF curItem^.subItem = NIL THEN (* Free Item *)
       lastItemPtr^:= nextItem;
       IF curItem^.itemFill <> NIL THEN
        FreeMem(curItem^.itemFill, SIZE(IntuiText))
       END;
       FreeMem(curItem, SIZE(MenuItem))
      END
     ELSE
      lastItemPtr:= ADR(curItem^.nextItem)
     END;
     curItem:= nextItem
    END;
    IF curMenu^.firstItem = NIL THEN (* Free Menu *)
     lastMenuPtr^:= curMenu^.nextMenu;
     FreeMem(curMenu, SIZE(Menu))
    ELSE
     lastMenuPtr:= ADR(curMenu^.nextMenu)
    END
   END;
   curMenu:= nextMenu
  END;
  IF globals^.firstMenu <> NIL THEN SetWindows END;
  Unlock(globals)
 END RemoveMenus;

BEGIN

 SetMenus:= SetAllMenus;
 installed:= TRUE;

END Menus.
