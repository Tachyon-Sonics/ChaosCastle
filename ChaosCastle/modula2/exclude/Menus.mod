IMPLEMENTATION MODULE Menus;

 FROM SYSTEM IMPORT ADR, ADDRESS, BITSET, SHORTSET, SHIFT;
 FROM IntuitionD IMPORT MenuItem, MenuItemPtr, MenuItemFlags, MenuItemFlagSet,
  menuEnabled, WindowPtr, ScreenPtr, IntuiText, IntuiTextPtr, noItem,
  noSub, menuNull, checkWidth, commWidth; IMPORT IntuitionD;
 FROM IntuitionL IMPORT IntuiTextLength, ItemAddress;
 FROM GraphicsD IMPORT TextAttr, TextAttrPtr, FontStyleSet, FontFlags,
  FontFlagSet;
 FROM ExecD IMPORT MemReqs, MemReqSet;
 FROM ExecL IMPORT AllocMem, FreeMem;
 FROM AmigaBase IMPORT globals, GetMenu, SetMenus, SetWindows, FindBestPen;
 FROM Memory IMPORT TagItem, TagItemPtr, tagUser, tagMore, NextTag,
  LockR, LockW, Unlock, StrPtr;
 IMPORT R;

 CONST
  topazAttr = TextAttr{
   name: ADR("topaz.font"),
   ySize: 8,
   style: FontStyleSet{},
   flags: FontFlagSet{romFont}};

 TYPE
  MenuPtr = POINTER TO Menu;
  Menu = RECORD
   menu: IntuitionD.MenuPtr;
   item: MenuItemPtr;
   parent: MenuPtr;
   next: MenuPtr;
   childs: MenuPtr;
  END;

 VAR
  menuBar: MenuPtr;
  topaz, installed: BOOLEAN;


 PROCEDURE RefreshMenus;
  VAR
   oldMenu{R.D7}: IntuitionD.MenuPtr;
 BEGIN
  LockW(globals);
  WITH globals^ DO
   oldMenu:= firstMenu;
   firstMenu:= NIL;
   IF installed THEN SetWindows END;
   firstMenu:= oldMenu
  END;
  installed:= FALSE;
  Unlock(globals)
 END RefreshMenus;

 PROCEDURE AddNewMenu(tags: TagItemPtr): MenuPtr;
  VAR
   me{R.D7}: MenuPtr;
   par{R.D6}: MenuPtr;
   before{R.D5}, after{R.D4}: MenuPtr;
   iText{R.D3}: IntuiTextPtr;
   oldTags: TagItemPtr;
 BEGIN
  oldTags:= tags;
  before:= NIL;
  par:= NIL;
  LOOP
   WITH NextTag(tags)^ DO
    IF tag = 0 THEN EXIT
    ELSIF tag = mPARENT THEN par:= addr
    ELSIF tag = mBEFORE THEN before:= addr
    END
   END
  END;
  RefreshMenus;
  LockW(globals);
  me:= AllocMem(SIZE(Menu), MemReqSet{});
  IF me <> NIL THEN
   WITH me^ DO
    parent:= par;
    IF parent = NIL THEN
     item:= NIL;
     after:= menuBar; (* First brother *)
     menu:= AllocMem(SIZE(IntuitionD.Menu), MemReqSet{memClear});
     IF menu <> NIL THEN
      WITH menu^ DO
       flags:= BITSET{menuEnabled}
      END
     END
    ELSE
     menu:= NIL;
     after:= parent^.childs; (* First brother *)
     item:= AllocMem(SIZE(MenuItem), MemReqSet{memClear});
     IF item <> NIL THEN
      WITH item^ DO
       flags:= MenuItemFlagSet{itemText, itemEnabled, highComp};
       iText:= AllocMem(SIZE(IntuiText), MemReqSet{memClear});
       itemFill:= iText;
       IF iText <> NIL THEN
        iText^.backPen:= 1;
        iText^.frontPen:= FindBestPen(0888888H, 1)
       END
      END
     END
    END;
    IF (menu <> NIL) OR (item <> NIL) THEN
     childs:= NIL;
     WHILE (after <> NIL) AND (after^.next <> before) DO
      after:= after^.next
     END;
     next:= before; (* Link with next *)
     IF menu <> NIL THEN (* Menu *)
      IF after <> NIL THEN (* Link with previous *)
       after^.next:= me;
       after^.menu^.nextMenu:= menu
      END;
      IF menuBar = next THEN (* Link with parent *)
       globals^.firstMenu:= menu;
       menuBar:= me;
      END;
      IF next <> NIL THEN (* Link with next *)
       menu^.nextMenu:= next^.menu
      ELSE
       menu^.nextMenu:= NIL
      END
     ELSE (* MenuItem *)
      IF after <> NIL THEN (* Link with previous *)
       after^.next:= me;
       after^.item^.nextItem:= item
      END;
      IF parent^.childs = next THEN (* Link with parent *)
       parent^.childs:= me;
       IF parent^.menu <> NIL THEN (* parent is a menu *)
        parent^.menu^.firstItem:= item
       ELSE (* parent is an item *)
        parent^.item^.subItem:= item
       END
      END;
      IF next <> NIL THEN (* Link with next *)
       item^.nextItem:= next^.item
      ELSE
       item^.nextItem:= NIL
      END
     END;
     Unlock(globals);
     ModifyMenu(me, oldTags);
     RETURN me
    END;
    IF menu <> NIL THEN FreeMem(menu, SIZE(IntuitionD.Menu)) END;
    IF item <> NIL THEN FreeMem(item, SIZE(MenuItem)) END
   END;
   FreeMem(me, SIZE(Menu))
  END;
  Unlock(globals);
  RETURN NIL
 END AddNewMenu;

 PROCEDURE ModifyMenu(menu: MenuPtr; tags: TagItemPtr);
  CONST
   tName = 0;
   tCheck = 1;
   tChecked = 2;
   tEnable = 3;
   tComm = 4;
   tColor = 5;
  VAR
   name{R.D7}: StrPtr;
   iText{R.D6}: IntuiTextPtr;
   check, vu, enable{R.D5}: BOOLEAN;
   comm: CHAR;
   color: LONGCARD;
   given{R.D4}: BITSET;
   oldMenu: IntuitionD.MenuPtr;
 BEGIN
  RefreshMenus;
  given:= BITSET{};
  LOOP
   WITH NextTag(tags)^ DO
    IF tag = 0 THEN EXIT
    ELSIF tag = mNAME THEN INCL(given, tName); name:= addr
    ELSIF tag = mCHECK THEN INCL(given, tCheck); check:= (data <> 0)
    ELSIF tag = mCHECKED THEN INCL(given, tChecked); vu:= (data <> 0)
    ELSIF tag = mENABLE THEN INCL(given, tEnable); enable:= (data <> 0)
    ELSIF tag = mCOMM THEN INCL(given, tComm); comm:= CHR(data)
    ELSIF tag = mCOLOR THEN INCL(given, tColor); color:= data
    END
   END
  END;
  IF menu^.menu <> NIL THEN
   WITH menu^.menu^ DO
    IF (tName IN given) THEN menuName:= name END;
    IF (tEnable IN given) THEN
     IF enable THEN
      INCL(flags, menuEnabled)
     ELSE
      EXCL(flags, menuEnabled)
     END
    END
   END
  ELSE
   WITH menu^.item^ DO
    IF (tName IN given) THEN
     iText:= itemFill;
     IF iText <> NIL THEN iText^.iText:= name END
    END;
    IF (tCheck IN given) THEN
     IF check THEN
      INCL(flags, menuToggle);
      INCL(flags, checkIt)
     ELSE
      EXCL(flags, menuToggle);
      EXCL(flags, checkIt)
     END
    END;
    IF (tChecked IN given) THEN
     IF vu THEN INCL(flags, checked) ELSE EXCL(flags, checked) END
    END;
    IF (tEnable IN given) THEN
     IF enable THEN
      INCL(flags, itemEnabled); EXCL(flags, highBox)
     ELSE
      EXCL(flags, itemEnabled); INCL(flags, highBox)
     END
    END;
    IF (tComm IN given) THEN
     IF (comm >= "a") AND (comm <= "z") THEN
      command:= CHR(ORD(comm) - ORD("a") + ORD("A"))
     ELSE
      command:= comm
     END
    END;
    IF command <> 0C THEN INCL(flags, commSeq) ELSE EXCL(flags, commSeq) END;
    IF (tColor IN given) THEN
     iText:= itemFill;
     IF iText <> NIL THEN
      WITH iText^ DO
       frontPen:= FindBestPen(color, backPen)
      END
     END
    END
   END
  END
 END ModifyMenu;

 PROCEDURE FreeMenu(VAR menu: MenuPtr);
  VAR
   after{R.D7}: MenuPtr;
   parent{R.D6}: MenuPtr;
 BEGIN
  IF menu <> NIL THEN
   RefreshMenus;
   IF menu^.childs <> NIL THEN HALT END;
   parent:= menu^.parent;
   WITH menu^ DO
    IF parent = NIL THEN after:= menuBar ELSE after:= parent^.childs END
   END;
   IF after = menu THEN (* I am the first child *)
    IF parent = NIL THEN
     menuBar:= menu^.next;
     LockW(globals);
     IF menuBar <> NIL THEN
      globals^.firstMenu:= menuBar^.menu
     ELSE
      globals^.firstMenu:= NIL
     END;
     Unlock(globals)
    ELSE
     parent^.childs:= menu^.next;
     IF parent^.menu <> NIL THEN
      IF menu^.next <> NIL THEN
       parent^.menu^.firstItem:= menu^.next^.item
      ELSE
       parent^.menu^.firstItem:= NIL
      END
     ELSE
      IF menu^.next <> NIL THEN
       parent^.item^.subItem:= menu^.next^.item
      ELSE
       parent^.item^.subItem:= NIL
      END
     END
    END
   ELSE (* I am not the first child *)
    WHILE after^.next <> menu DO after:= after^.next END;
    after^.next:= menu^.next;
    IF after^.menu <> NIL THEN
     IF menu^.next <> NIL THEN
      after^.menu^.nextMenu:= menu^.next^.menu
     ELSE
      after^.menu^.nextMenu:= NIL
     END
    ELSE
     IF menu^.next <> NIL THEN
      after^.item^.nextItem:= menu^.next^.item
     ELSE
      after^.item^.nextItem:= NIL
     END
    END
   END;
   WITH menu^ DO
    IF menu <> NIL THEN
     FreeMem(menu, SIZE(IntuitionD.Menu))
    END;
    IF item <> NIL THEN
     IF item^.itemFill <> NIL THEN
      FreeMem(item^.itemFill, SIZE(IntuiText))
     END;
     FreeMem(item, SIZE(MenuItem))
    END
   END;
   FreeMem(menu, SIZE(Menu));
   menu:= NIL
  END
 END FreeMenu;

 PROCEDURE SetAllMenus;
  VAR
   screen: ScreenPtr;
   ta: TextAttrPtr;
   menuPos: CARDINAL;
   maxWidth, maxHeight: CARDINAL;
   intuiText: IntuiText;
   iText: IntuiTextPtr;
   currentMenu{R.D7}: IntuitionD.MenuPtr;
   ok, toobig: BOOLEAN;

  PROCEDURE SetItems(item: MenuItemPtr; left: CARDINAL);
   VAR
    itemX{R.D6}, itemY{R.D5}, maxItemWidth{R.D4}: CARDINAL;
  BEGIN
   itemY:= 0; itemX:= left; maxItemWidth:= 0;
   WHILE item <> NIL DO
    WITH item^ DO
     leftEdge:= itemX; topEdge:= itemY;
     iText:= itemFill;
     iText^.topEdge:= 1;
     iText^.iTextFont:= ta;
     width:= IntuiTextLength(iText);
     IF (checkIt IN flags) THEN
      iText^.leftEdge:= checkWidth;
      INC(width, checkWidth)
     ELSE
      iText^.leftEdge:= 0
     END;
     IF commSeq IN flags THEN
      INC(width, commWidth);
      INC(width, ta^.ySize)
     END;
     IF CARDINAL(width) > maxItemWidth THEN maxItemWidth:= width END;
     height:= ta^.ySize + 2;
     INC(itemY, height);
     IF itemY >= maxHeight THEN
      INC(itemX, maxItemWidth + ta^.ySize);
      IF itemX + CARDINAL(width) >= maxWidth THEN toobig:= TRUE END;
      itemY:= 0; maxItemWidth:= 0
     END;
     SetItems(subItem, width);
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
     topEdge:= 0;
     IF leftEdge + width >= INTEGER(maxWidth) THEN toobig:= TRUE END;
     INC(menuPos, width); INC(menuPos, ta^.ySize);
     SetItems(firstItem, 0)
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

 PROCEDURE FindMenu(mnum, item, sub: CARDINAL; VAR next: CARDINAL): ADDRESS;
  VAR
   menu{R.D7}: MenuPtr;
 BEGIN
  next:= menuNull;
  IF (mnum = IntuitionD.noMenu) OR (item = noItem) THEN RETURN NIL END;
  menu:= menuBar;
  LOOP
   IF menu = NIL THEN RETURN NIL END;
   IF mnum = 0 THEN EXIT END;
   menu:= menu^.next;
   DEC(mnum)
  END;
  menu:= menu^.childs;
  LOOP
   IF menu = NIL THEN RETURN NIL END;
   IF item = 0 THEN EXIT END;
   menu:= menu^.next;
   DEC(item)
  END;
  IF sub <> noSub THEN
   menu:= menu^.childs;
   LOOP
    IF menu = NIL THEN RETURN NIL END;
    IF sub = 0 THEN EXIT END;
    menu:= menu^.next;
    DEC(sub)
   END
  END;
  IF menu^.item <> NIL THEN
   next:= menu^.item^.nextSelect
  END;
  RETURN menu
 END FindMenu;

 PROCEDURE MenuToAddress(menu{R.D0}: MenuPtr): ADDRESS;
 BEGIN
  RETURN menu
 END MenuToAddress;

 PROCEDURE AddressToMenu(addr{R.D0}: ADDRESS): MenuPtr;
 BEGIN
  RETURN addr
 END AddressToMenu;

BEGIN

 menuBar:= NIL;
 GetMenu:= FindMenu;
 SetMenus:= SetAllMenus;
 installed:= TRUE;

END Menus.
