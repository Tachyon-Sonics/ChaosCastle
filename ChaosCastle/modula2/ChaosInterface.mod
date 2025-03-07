IMPLEMENTATION MODULE ChaosInterface;

 FROM SYSTEM IMPORT ADR, ADDRESS, BYTE;
 FROM Memory IMPORT TagItem, TagItemPtr, Node, NodePtr, List, ListPtr,
  AllocMem, FreeMem, InitList, AddHead, Remove, StrPtr, StrLength, CopyStr,
  First, Last, Next, Tail, Empty, TAG1, TAG2, TAG3, TAG4, TAG5, TAG6, TAG7,
  YES, NO, SET16, CARD8, CARD16, INT16, CARD32, INT32, ADS;
 FROM Languages IMPORT GetLanguageName, SetLanguage, ADL, language;
 FROM Checks IMPORT Check, CheckMem, CheckMemBool, Warn, Ask, Terminate,
  AddTermProc;
 FROM Graphics IMPORT DeleteArea, SetArea, CopyRect, WaitTOF, AreaToFront,
  GetGraphicsSysAttr, aSIZEX, aSIZEY, aCOLOR, GraphicsErr, GetGraphicsErr;
 FROM Sounds IMPORT GetSoundsSysAttr, sSTEREO, sNUMCHANS;
 FROM Dialogs IMPORT GadgetPtr, DialogOk, dFLAGS, dRFLAGS, dfACTIVE, dfSELECT,
  dfCLOSE, dfJUSTIFY, dfVDIR, dTEXT, dTXTLEN, dINTVAL, dDialog, dGroup,
  dButton, dCycle, dCheckbox, dIntEdit, noGadget, CreateGadget, ModifyGadget,
  RefreshGadget, AddNewGadget, DeepFreeGadget, dLabel, dSPAN, dfAUTOLEFT,
  dMASK, dBool, dfBORDER, GetGadgetAttr;
 FROM Input IMPORT eKEYBOARD, eMENU, eGADGET, eREFRESH, eSYS, Event,
  EventTypeSet, AddEvents, RemEvents, WaitEvent, GetEvent, statWaiting,
  statBusy, statReady, SetBusyStat, FlushEvents, BeginRefresh, EndRefresh,
  GetStick, SysMsg, eTIMER, Joy1;
 FROM Menus IMPORT mNAME, mCOMM, mENABLE, mPARENT, mBEFORE, AddNewMenu,
  FreeMenu, ModifyMenu, MenuPtr, MenuToAddress, AddressToMenu;
 FROM Files IMPORT OpenFile, ReadFileBytes, WriteFileBytes, CloseFile,
  FilePtr, AskFile, fNAME, fTEXT, fFLAGS, afNEWFILE, noFile, AccessFlags,
  AccessFlagSet, FileErrorMsg, AskMiscSettings, msGraphic, msSound, msMenus,
  msDialogs, msClock, msInput;
 FROM Registration IMPORT registered;
 FROM ChaosBase IMPORT Anims, FlushAllObjs, attrList, ObjAttr, ObjAttrPtr,
  weaponAttr, Zone, zone, level, powerCountDown, pLife, nbDollar, nbSterling,
  score, GameStat, gameStat, file, d, Weapon, WeaponAttr, specialStage, objList,
  TailObj, FirstObj, NextObj, Obj, ObjPtr, leftObjList, Frac, difficulty, water,
  stages, password, gameSeed;
 FROM ChaosGraphics IMPORT mulS, color, mainArea, explosions, OpenScreen,
  CloseScreen, Explosions, maskArea, imageArea, shapeArea, PW, W, H,
  backpx, backpy, dfltGraphic, dualpf, image2Area;
 FROM ChaosSounds IMPORT sound, music, stereo, nbChannel, musicPri,
  SwitchSoundOn, SwitchSoundOff, dfltSound;
 FROM ChaosImages IMPORT InitImages, RenderObjects, InitPalette;

 VAR
  dfltLanguage: CARD8;
  dfltLang: BOOLEAN;

 CONST
  ConfigFileName = "Config";
  TopScoreFileName = "TopScores";


 PROCEDURE ReadTopScoreList(VAR topScores: TopScoreList);
  VAR
   res: INT32;
   c, d: CARD16;
   z: Zone;
   ch: CHAR;
 BEGIN
  FOR c:= 1 TO 10 DO
   WITH topScores[c] DO
    FOR d:= 0 TO 19 DO name[d]:= "." END;
    score:= 0; seed:= 0;
    FOR z:= Chaos TO Family DO endLevel[z]:= 1 END;
    endZone:= Chaos;
    endDifficulty:= 1
   END
  END;
  file:= OpenFile(ADS(TopScoreFileName), AccessFlagSet{accessRead});
  IF file <> noFile THEN
   FOR c:= 1 TO 10 DO
    WITH topScores[c] DO
     res:= ReadFileBytes(file, ADR(name), SIZE(name));
     res:= ReadFileBytes(file, ADR(score), SIZE(score));
     res:= ReadFileBytes(file, ADR(seed), SIZE(seed));
     FOR z:= Chaos TO Family DO
      res:= ReadFileBytes(file, ADR(ch), 1); endLevel[z]:= ORD(ch)
     END;
     res:= ReadFileBytes(file, ADR(endZone), SIZE(endZone));
     res:= ReadFileBytes(file, ADR(endDifficulty), SIZE(endDifficulty));
     IF res <= 0 THEN CloseFile(file); RETURN END
    END
   END;
   CloseFile(file)
  END
 END ReadTopScoreList;

 PROCEDURE WriteTopScoreList(topScores: TopScoreList);
  VAR
   res: INT32;
   c: CARD16;
   z: Zone;
   ch: CHAR;
 BEGIN
  file:= OpenFile(ADS(TopScoreFileName), AccessFlagSet{accessWrite});
  IF file <> noFile THEN
   FOR c:= 1 TO 10 DO
    WITH topScores[c] DO
     res:= WriteFileBytes(file, ADR(name), SIZE(name));
     res:= WriteFileBytes(file, ADR(score), SIZE(score));
     res:= WriteFileBytes(file, ADR(seed), SIZE(seed));
     FOR z:= Chaos TO Family DO
      ch:= CHR(endLevel[z]); res:= WriteFileBytes(file, ADR(ch), 1)
     END;
     res:= WriteFileBytes(file, ADR(endZone), SIZE(endZone));
     res:= WriteFileBytes(file, ADR(endDifficulty), SIZE(endDifficulty));
     IF res <= 0 THEN
      Warn(TRUE, ADL("Error writing file 'TopScores':"), FileErrorMsg());
      CloseFile(file); RETURN
     END
    END
   END;
   CloseFile(file)
  ELSE
   Warn(TRUE, ADL("Error creating file 'TopScores':"), FileErrorMsg())
  END
 END WriteTopScoreList;

 PROCEDURE DefaultSound;
  VAR
   what: TagItem;
 BEGIN
  dfltSound:= TRUE;
  music:= FALSE; musicPri:= 0;
  what.tag:= sSTEREO; GetSoundsSysAttr(what);
  stereo:= (what.data <> 0);
  what.tag:= sNUMCHANS; GetSoundsSysAttr(what);
  IF what.data < 16 THEN nbChannel:= what.data ELSE nbChannel:= 16 END;
  sound:= (nbChannel >= 1)
 END DefaultSound;

 PROCEDURE DefaultGraphic;
  VAR
   what: TagItem;
   sizex, sizey: INT16;
 BEGIN
  dfltGraphic:= TRUE;
  what.tag:= aSIZEX; GetGraphicsSysAttr(what); sizex:= what.data;
  what.tag:= aSIZEY; GetGraphicsSysAttr(what); sizey:= what.data;
  IF (sizex >= 640) AND (sizey >= 480) THEN mulS:= 2 ELSE mulS:= 1 END;
  what.tag:= aCOLOR; GetGraphicsSysAttr(what);
  color:= what.data >= 16;
  dualpf:= what.data >= 256;
  IF color OR (mulS > 1) THEN explosions:= Medium ELSE explosions:= Low END;
  IF color AND (mulS > 1) THEN explosions:= High END
 END DefaultGraphic;

 PROCEDURE ResetConfig;
 BEGIN
  dfltSound:= FALSE; dfltGraphic:= FALSE;
  sound:= FALSE; music:= FALSE;
  mulS:= 1; color:= FALSE; dualpf:= FALSE;
  SetLanguage(0)
 END ResetConfig;

 PROCEDURE LoadConfig;
  VAR
   data: SET16;
   ch: CHAR;
 BEGIN
  file:= OpenFile(ADS(ConfigFileName), AccessFlagSet{accessRead});
  IF file <> noFile THEN
   IF (ReadFileBytes(file, ADR(data), 2) < 2) OR
      (ReadFileBytes(file, ADR(musicPri), 2) < 2) OR
      (ReadFileBytes(file, ADR(nbChannel), 2) < 2) OR
      (ReadFileBytes(file, ADR(mulS), 2) < 2) OR
      (ReadFileBytes(file, ADR(explosions), 1) < 1) OR
      (ReadFileBytes(file, ADR(ch), 1) < 1) THEN
    Warn(TRUE, ADL("Error reading file 'Config':"), FileErrorMsg());
    DefaultGraphic; DefaultSound
   ELSE
    dfltLang:= (5 IN data);
    IF dfltLang THEN SetLanguage(dfltLanguage) ELSE SetLanguage(ORD(ch)) END;
    dfltGraphic:= FALSE; dfltSound:= FALSE;
    sound:= (0 IN data);
    music:= (1 IN data);
    stereo:= (2 IN data);
    color:= (6 IN data);
    dualpf:= color AND (7 IN data);
    IF (3 IN data) THEN DefaultGraphic END;
    IF (4 IN data) THEN DefaultSound END
   END;
   CloseFile(file)
  ELSE
   DefaultGraphic; DefaultSound
  END
 END LoadConfig;

 PROCEDURE SaveConfig;
  VAR
   data: SET16;
   ch: CHAR;
 BEGIN
  ch:= CHR(language);
  data:= SET16{};
  IF sound THEN INCL(data, 0) END;
  IF music THEN INCL(data, 1) END;
  IF stereo THEN INCL(data, 2) END;
  IF dfltGraphic THEN INCL(data, 3) END;
  IF dfltSound THEN INCL(data, 4) END;
  IF dfltLang THEN INCL(data, 5) END;
  IF color THEN INCL(data, 6) END;
  IF color AND dualpf THEN INCL(data, 7) END;
  LOOP
   file:= OpenFile(ADS(ConfigFileName), AccessFlagSet{accessWrite});
   IF (file <> noFile) AND
      (WriteFileBytes(file, ADR(data), 2) = 2) AND
      (WriteFileBytes(file, ADR(musicPri), 2) = 2) AND
      (WriteFileBytes(file, ADR(nbChannel), 2) = 2) AND
      (WriteFileBytes(file, ADR(mulS), 2) = 2) AND
      (WriteFileBytes(file, ADR(explosions), 1) = 1) AND
      (WriteFileBytes(file, ADR(ch), 1) = 1) THEN
    CloseFile(file);
    EXIT
   ELSE
    IF NOT Ask(ADL("Error writing file 'Config':"), FileErrorMsg(), ADL("Retry"), ADL("Cancel")) THEN
     EXIT
    END
   END
  END;
  CloseFile(file)
 END SaveConfig;

 VAR
  fileMenu, settingsMenu: MenuPtr;
  newMenu, loadMenu, saveMenu, bar1Menu, hideMenu, bar2Menu, quitMenu: MenuPtr;
  graphicsMenu, soundsMenu, languageMenu, miscMenu: MenuPtr;

 PROCEDURE FlushMenus;
 BEGIN
  FreeMenu(miscMenu);
  FreeMenu(languageMenu);
  FreeMenu(soundsMenu);
  FreeMenu(graphicsMenu);
  FreeMenu(quitMenu);
  FreeMenu(bar2Menu);
  FreeMenu(hideMenu);
  FreeMenu(bar1Menu);
  FreeMenu(saveMenu);
  FreeMenu(loadMenu);
  FreeMenu(newMenu);
  FreeMenu(settingsMenu);
  FreeMenu(fileMenu)
 END FlushMenus;

 PROCEDURE InitMenus;
  VAR
   fm, sm: ADDRESS;
 BEGIN
  fileMenu:= AddNewMenu(TAG1(mNAME, ADL("File")));
  fm:= MenuToAddress(fileMenu);
  newMenu:= AddNewMenu(TAG3(mNAME, ADL("New Game "), mPARENT, fm, mCOMM, ORD("N")));
  loadMenu:= AddNewMenu(TAG3(mNAME, ADL("Load Game"), mPARENT, fm, mCOMM, ORD("O")));
  saveMenu:= AddNewMenu(TAG3(mNAME, ADL("Save Game"), mPARENT, fm, mCOMM, ORD("S")));
  bar1Menu:= AddNewMenu(TAG3(mNAME, ADL("---------"), mPARENT, fm, mENABLE, NO));
  hideMenu:= AddNewMenu(TAG3(mNAME, ADL("Hide     "), mPARENT, fm, mCOMM, ORD("H")));
  bar2Menu:= AddNewMenu(TAG3(mNAME, ADL("---------"), mPARENT, fm, mENABLE, NO));
  quitMenu:= AddNewMenu(TAG3(mNAME, ADL("Quit"), mPARENT, fm, mCOMM, ORD("Q")));
  settingsMenu:= AddNewMenu(TAG1(mNAME, ADL("Settings")));
  sm:= MenuToAddress(settingsMenu);
  graphicsMenu:= AddNewMenu(TAG3(mNAME, ADL("Graphics..."), mPARENT, sm, mCOMM, ORD("G")));
  soundsMenu:= AddNewMenu(TAG3(mNAME, ADL("Sounds...  "), mPARENT, sm, mCOMM, ORD("M")));
  languageMenu:= AddNewMenu(TAG3(mNAME, ADL("Language..."), mPARENT, sm, mCOMM, ORD("L")));
  miscMenu:= AddNewMenu(TAG3(mNAME, ADL("Misc...    "), mPARENT, sm, mCOMM, ORD("C")));
 END InitMenus;

 VAR
  fmEnabled: BOOLEAN;

 PROCEDURE EnableFileMenus;
 BEGIN
  fmEnabled:= TRUE;
  ModifyMenu(graphicsMenu, TAG1(mENABLE, YES));
  ModifyMenu(soundsMenu, TAG1(mENABLE, YES));
  ModifyMenu(languageMenu, TAG1(mENABLE, YES));
  ModifyMenu(miscMenu, TAG1(mENABLE, YES));
  ModifyMenu(newMenu, TAG1(mENABLE, YES));
  ModifyMenu(loadMenu, TAG1(mENABLE, ORD(gameStat <> Playing)));
  ModifyMenu(saveMenu, TAG1(mENABLE, ORD(gameStat <> Playing)));
  ModifyMenu(hideMenu, TAG1(mENABLE, YES))
 END EnableFileMenus;

 PROCEDURE DisableFileMenus;
 BEGIN
  fmEnabled:= FALSE;
  ModifyMenu(graphicsMenu, TAG1(mENABLE, NO));
  ModifyMenu(soundsMenu, TAG1(mENABLE, NO));
  ModifyMenu(languageMenu, TAG1(mENABLE, NO));
  ModifyMenu(miscMenu, TAG1(mENABLE, NO));
  ModifyMenu(newMenu, TAG1(mENABLE, NO));
  ModifyMenu(loadMenu, TAG1(mENABLE, NO));
  ModifyMenu(saveMenu, TAG1(mENABLE, NO));
  ModifyMenu(hideMenu, TAG1(mENABLE, NO))
 END DisableFileMenus;

 PROCEDURE ColdInit;
 BEGIN
  SetBusyStat(statBusy);
  dfltLanguage:= language;
  LoadConfig;
  InitMenus;
  AddEvents(EventTypeSet{eKEYBOARD, eMENU, eGADGET, eREFRESH, eTIMER, eSYS})
 END ColdInit;

 PROCEDURE WarnMem;
 BEGIN
  Warn(TRUE, ADL("Not enough memory"), ADL("for requested settings."))
 END WarnMem;

 PROCEDURE InitSounds;
 BEGIN
  IF sound THEN
   RemEvents(EventTypeSet{eMENU});
   IF NOT SwitchSoundOn() THEN
    sound:= FALSE; stereo:= FALSE;
    SwitchSoundOff
   END;
   AddEvents(EventTypeSet{eMENU})
  END
 END InitSounds;

 PROCEDURE InitGraphics;
  VAR
   obj, last: ObjPtr;
   gerr: GraphicsErr;
 BEGIN
  RemEvents(EventTypeSet{eMENU, eREFRESH});
  SetBusyStat(statBusy);
 (* Screen, windows *)
  IF NOT(OpenScreen()) OR NOT(InitImages()) THEN
   gerr:= GetGraphicsErr();
   CloseScreen;
   ResetConfig; SwitchSoundOff;
   IF gerr = gNotSupported THEN
    Warn(TRUE, ADL("Unable to display the"), ADL("requested number of colors"))
   ELSIF gerr = gTooComplex THEN
    Warn(TRUE, ADL("Graphics size too big"), NIL)
   ELSE
    WarnMem
   END;
   CheckMemBool(NOT OpenScreen() OR NOT InitImages())
  END;
  RenderObjects;
(*
  SetArea(mainArea);
  CopyRect(shapeArea, 0, 0, 20, 0/*-16 * mulS*/, 256 * mulS, 256 * mulS);
  REPEAT
   WaitTOF;
  UNTIL Joy1 IN GetStick();
*)
  obj:= FirstObj(objList);
  last:= TailObj(objList);
  WHILE obj <> last DO
   WITH obj^.attr^ DO IF Make <> NIL THEN Make(obj) END END;
   obj:= NextObj(obj^.objNode)
  END;
  obj:= FirstObj(leftObjList);
  last:= TailObj(leftObjList);
  WHILE obj <> last DO
   WITH obj^.attr^ DO IF Make <> NIL THEN Make(obj) END END;
   obj:= NextObj(obj^.objNode)
  END;
  REPEAT
   BeginRefresh;
  UNTIL EndRefresh();
  IF Refresh <> NIL THEN Refresh END;
  AddEvents(EventTypeSet{eMENU, eREFRESH})
 END InitGraphics;

 PROCEDURE WarmInit;
 BEGIN
  WarmFlush;
  InitGraphics;
  InitSounds;
  EnableFileMenus
 END WarmInit;

 PROCEDURE FlushSounds;
 BEGIN
  SwitchSoundOff
 END FlushSounds;

 PROCEDURE FlushGraphics;
 BEGIN
  DeepFreeGadget(d);
  DeleteArea(image2Area);
  DeleteArea(imageArea);
  (* Screen *)
  CloseScreen
 END FlushGraphics;

 PROCEDURE WarmFlush;
 BEGIN
  DisableFileMenus;
  FlushSounds;
  FlushGraphics
 END WarmFlush;

 PROCEDURE Hide;
  VAR
   e: Event;
   menu: MenuPtr;
 BEGIN
  IF d <> noGadget THEN RETURN END;
  WarmFlush;
  ModifyMenu(hideMenu, TAG2(mNAME, ADL("Continue"), mENABLE, YES));
  LOOP
   WaitEvent;
   GetEvent(e);
   IF e.type = eMENU THEN
    menu:= AddressToMenu(e.menu);
    IF menu = hideMenu THEN
     EXIT
    ELSIF menu = quitMenu THEN
     Terminate
    END
   ELSIF e.type = eSYS THEN
    IF e.msg = pActivate THEN
     EXIT
    ELSIF e.msg = pQuit THEN
     Terminate
    END
   END
  END;
  ModifyMenu(hideMenu, TAG1(mNAME, ADL("Hide     ")));
  WarmInit;
  InitPalette;
  SetArea(mainArea); AreaToFront
 END Hide;

 PROCEDURE Quit;
 BEGIN
  IF (gameStat = Start)
  OR Ask(ADL("Quit program ?"), NIL, ADL("Yes"), ADL("No")) THEN
   gameStat:= Break
  END
 END Quit;

 PROCEDURE NumToString(val: CARD32; VAR str: ARRAY OF CHAR);
  VAR
   c, d: INT16;
   ch: CHAR;
 BEGIN
  str[HIGH(str)]:= 0C;
  c:= HIGH(str);
  REPEAT
   DEC(c);
   str[c]:= CHR(48 + val MOD 10);
   val:= val DIV 10
  UNTIL (c = 0) OR (val = 0);
  d:= 0;
  REPEAT
   ch:= str[c]; str[d]:= ch;
   INC(c); INC(d)
  UNTIL ch = 0C
 END NumToString;

 CONST
  ID = 1128482669;

 VAR
  lsFile: ARRAY[0..95] OF CHAR;

 PROCEDURE LoadGame;
  VAR
   dflt: ARRAY[0..95] OF CHAR;
   fileName: StrPtr;
   id: CARD32;
   v: CARD16;
   z: Zone;
   w: Weapon;
   ch: CHAR;
   ok: BOOLEAN;

  PROCEDURE Get(VAR data: ARRAY OF BYTE);
   VAR
    size: INT32;
  BEGIN
   IF ok THEN
    size:= HIGH(data) + 1;
    ok:= (ReadFileBytes(file, ADR(data), size) = size)
   END
  END Get;

  PROCEDURE RangeChk(val, min, max: INT16);
  BEGIN
   IF (val < min) OR (val > max) THEN ok:= FALSE END
  END RangeChk;

 BEGIN
  IF NOT(registered) THEN
   Warn(TRUE, ADL("Game loading is not possible"), ADL("with the demo version."));
   IF Ask(ADL("Would you like to start"), ADL("directly at level 5 ?"), ADL("Yes"), ADL("No")) THEN
    level[Chaos]:= 10; level[Castle]:= 5;
    level[Family]:= 3; level[Special]:= 1;
    nbDollar:= 150; nbSterling:= 10;
    powerCountDown:= 6;
    specialStage:= 0;
    FOR w:= MIN(Weapon) TO MAX(Weapon) DO
     WITH weaponAttr[w] DO
      nbBullet:= 0; nbBomb:= 0; power:= ORD(w = GUN)
     END
    END;
    weaponAttr[GUN].nbBullet:= 99;
    weaponAttr[LASER].power:= 2; weaponAttr[LASER].nbBullet:= 10;
    weaponAttr[BALL].power:= 2; weaponAttr[BALL].nbBullet:= 10;
    weaponAttr[FIRE].power:= 4; weaponAttr[LASER].nbBomb:= 1;
    IF (gameStat <> Start) AND (Refresh <> NIL) THEN Refresh END;
    gameStat:= Finish
   END;
   RETURN
  END;
  ok:= TRUE;
  CopyStr(ADR(lsFile), ADR(dflt), SIZE(dflt));
  fileName:= AskFile(TAG2(fNAME, ADR(dflt), fTEXT, ADL("Load game:")));
  IF fileName <> NIL THEN
   CopyStr(fileName, ADR(lsFile), SIZE(lsFile));
   file:= OpenFile(fileName, AccessFlagSet{accessRead});
   Warn(file = noFile, fileName, FileErrorMsg());
   IF file <> noFile THEN
    Get(id);
    IF id <> ID THEN
     Warn(TRUE, ADL("Invalid format"), fileName)
    ELSE
     Get(score);
     Get(ch); pLife:= ORD(ch); RangeChk(pLife, 1, 239);
     pLife:= pLife MOD 30;
     Get(ch); nbDollar:= ORD(ch); RangeChk(nbDollar, 0, 200);
     Get(ch); nbSterling:= ORD(ch); RangeChk(nbSterling, 0, 200);
     Get(ch);
     powerCountDown:= ORD(ch) DIV 10;
     difficulty:= ORD(ch) MOD 10;
     IF difficulty = 0 THEN difficulty:= 10 END;
     Get(ch); specialStage:= ORD(ch);
     stages:= specialStage MOD 6;
     specialStage:= specialStage DIV 6;
     zone:= Chaos;
     FOR z:= MIN(Zone) TO MAX(Zone) DO
      Get(ch); level[z]:= ORD(ch)
     END;
     IF level[Family] > 10 THEN
      DEC(level[Family], 10); password:= TRUE
     ELSE
      password:= FALSE
     END;
     RangeChk(level[Chaos], 1, 100);
     RangeChk(level[Castle], 1, 20);
     RangeChk(level[Family], 1, 10);
     RangeChk(level[Special], 1, 24);
     FOR w:= MIN(Weapon) TO MAX(Weapon) DO
      WITH weaponAttr[w] DO
       Get(v);
       nbBullet:= v MOD 100; v:= v DIV 100;
       nbBomb:= v MOD 10; power:= v DIV 10;
       RangeChk(power, 0, 4)
      END
     END;
     Get(gameSeed)
    END;
    CloseFile(file);
    IF (gameStat <> Start) AND (Refresh <> NIL) THEN Refresh END;
    Warn(NOT ok, ADR(lsFile), ADL("Unable to load game's infos"));
    IF ok THEN gameStat:= Finish END
   END
  END
 END LoadGame;

 PROCEDURE SaveGame;
  VAR
   dflt: ARRAY[0..95] OF CHAR;
   fileName: StrPtr;
   id: CARD32;
   v: CARD16;
   z: Zone;
   w: Weapon;
   ch: CHAR;
   ok: BOOLEAN;

  PROCEDURE Put(data: ARRAY OF BYTE);
   VAR
    size: INT32;
  BEGIN
   IF ok THEN
    size:= HIGH(data) + 1;
    ok:= (WriteFileBytes(file, ADR(data), size) = size)
   END
  END Put;

 BEGIN
  IF NOT(registered) THEN
   Warn(TRUE, ADL("Game saving is not possible"), ADL("with the demo version."));
   RETURN
  END;
  ok:= TRUE;
  id:= ID;
  CopyStr(ADR(lsFile), ADR(dflt), SIZE(dflt));
  fileName:= AskFile(TAG3(fNAME, ADR(dflt), fTEXT, ADL("Save game:"), fFLAGS, afNEWFILE));
  IF fileName <> NIL THEN
   CopyStr(fileName, ADR(lsFile), SIZE(lsFile));
   file:= OpenFile(fileName, AccessFlagSet{accessWrite});
   Warn(file = noFile, fileName, FileErrorMsg());
   IF file <> noFile THEN
    Put(id);
    Put(score);
    ch:= CHR(pLife); Put(ch);
    ch:= CHR(nbDollar); Put(ch);
    ch:= CHR(nbSterling); Put(ch);
    ch:= CHR(powerCountDown * 10 + difficulty MOD 10); Put(ch);
    ch:= CHR(specialStage * 6 + stages); Put(ch);
    FOR z:= MIN(Zone) TO MAX(Zone) DO
     IF (z = Family) AND password THEN
      ch:= CHR(level[z] + 10)
     ELSE
      ch:= CHR(level[z])
     END;
     Put(ch)
    END;
    FOR w:= MIN(Weapon) TO MAX(Weapon) DO
     WITH weaponAttr[w] DO
      v:= power; v:= v * 10;
      INC(v, nbBomb); v:= v * 100;
      INC(v, nbBullet);
      Put(v)
     END
    END;
    Put(gameSeed);
    CloseFile(file);
    IF (gameStat <> Start) AND (Refresh <> NIL) THEN Refresh END;
    Warn(NOT ok, fileName, FileErrorMsg())
   END
  END
 END SaveGame;

 PROCEDURE ColdFlush;
 BEGIN
  SetBusyStat(statBusy);
  WarmFlush;
  FlushAllObjs;
  RemEvents(EventTypeSet{eKEYBOARD, eMENU, eGADGET, eREFRESH, eSYS});
  FlushMenus
 END ColdFlush;

 PROCEDURE AskGraphicSettings;
  CONST
   Busy = "---------------";
  VAR
   gadget, autoconf, expl, render, size, save, use, cancel: GadgetPtr;
   group1, group2, group3: GadgetPtr;
   event: Event;
   tagItem: TagItem;
   expTxt, renderTxt, busy: ADDRESS;
   oldRefresh: PROC;
   oldcolor, olddflt, olddual: BOOLEAN;
   oldexplosions: Explosions;
   oldmulS, c: INT16;
   change: BOOLEAN;
 BEGIN
  DisableFileMenus;
  SetBusyStat(statBusy);
  FlushEvents;
  oldmulS:= mulS; olddflt:= dfltGraphic; olddual:= dualpf;
  oldcolor:= color; oldexplosions:= explosions;
  oldRefresh:= Refresh; Refresh:= NIL;
  busy:= ADL(Busy);
  d:= CreateGadget(dDialog, TAG2(dTEXT, ADL("Graphics settings:"), dFLAGS, dfCLOSE + dfSELECT));
  group1:= AddNewGadget(d, dGroup, TAG1(dFLAGS, dfVDIR));
   autoconf:= AddNewGadget(group1, dBool, TAG2(dTEXT, ADL("Auto configure"), dFLAGS, dfJUSTIFY));
   group2:= AddNewGadget(group1, dGroup, TAG2(dSPAN, 3, dFLAGS, dfBORDER + dfVDIR));
    gadget:= AddNewGadget(group2, dLabel, TAG2(dTEXT, ADL("Explosions:"), dRFLAGS, dfAUTOLEFT));
    gadget:= AddNewGadget(group2, dLabel, TAG2(dTEXT, ADL("Rendering:"), dRFLAGS, dfAUTOLEFT));
    gadget:= AddNewGadget(group2, dLabel, TAG2(dTEXT, ADL("Size:"), dRFLAGS, dfAUTOLEFT));
    expl:= AddNewGadget(group2, dCycle, TAG2(dTEXT, busy, dFLAGS, dfJUSTIFY));
    render:= AddNewGadget(group2, dCycle, TAG2(dTEXT, busy, dFLAGS, dfJUSTIFY));
    size:= AddNewGadget(group2, dIntEdit, TAG3(dINTVAL, mulS, dTXTLEN, 2, dFLAGS, dfJUSTIFY));
   group3:= AddNewGadget(group1, dGroup, NIL);
    save:= AddNewGadget(group3, dButton, TAG1(dTEXT, ADL("Save")));
    use:= AddNewGadget(group3, dButton, TAG1(dTEXT, ADL("Use")));
    cancel:= AddNewGadget(group3, dButton, TAG1(dTEXT, ADL("Cancel")));
  CheckMemBool(RefreshGadget(d) <> DialogOk);
  REPEAT
 (* Update Gadget *)
   IF explosions = Low THEN expTxt:= ADL("Low")
   ELSIF explosions = Medium THEN expTxt:= ADL("Medium")
   ELSE expTxt:= ADL("High")
   END;
   ModifyGadget(expl, TAG1(dTEXT, expTxt));
   IF color THEN
    IF dualpf THEN
     renderTxt:= ADL("Colors x2")
    ELSE
     renderTxt:= ADL("Colors")
    END
   ELSE
    renderTxt:= ADL("Black&White")
   END;
   ModifyGadget(render, TAG1(dTEXT, renderTxt));
   ModifyGadget(expl, TAG2(dFLAGS, ORD(NOT dfltGraphic) * dfACTIVE, dMASK, dfACTIVE));
   ModifyGadget(render, TAG2(dFLAGS, ORD(NOT dfltGraphic) * dfACTIVE, dMASK, dfACTIVE));
   ModifyGadget(size, TAG2(dFLAGS, ORD(NOT dfltGraphic) * dfACTIVE, dMASK, dfACTIVE));
   ModifyGadget(autoconf, TAG2(dFLAGS, ORD(dfltGraphic) * dfSELECT, dMASK, dfSELECT));
   REPEAT
    IF gameStat = Break THEN RETURN END;
    SetBusyStat(statWaiting);
    WaitEvent;
    GetEvent(event);
    IF event.type = eGADGET THEN
     IF event.gadget = expl THEN
      IF explosions = High THEN
       explosions:= Low
      ELSE
       INC(explosions)
      END
     ELSIF event.gadget = render THEN
      IF color THEN
       IF dualpf THEN
        color:= FALSE; dualpf:= FALSE
       ELSE
        dualpf:= TRUE
       END
      ELSE
       color:= TRUE; dualpf:= FALSE
      END
     ELSIF event.gadget = autoconf THEN
      dfltGraphic:= NOT(dfltGraphic);
      IF dfltGraphic THEN
       DefaultGraphic;
       ModifyGadget(size, TAG1(dINTVAL, mulS))
      END
     END
    ELSE
     CommonEvent(event)
    END;
   UNTIL (event.type = eGADGET);
   tagItem.tag:= dINTVAL;
   GetGadgetAttr(size, tagItem);
   mulS:= tagItem.data;
   IF (mulS < 1) OR (mulS > 9) THEN mulS:= 1 END
  UNTIL (event.gadget = save) OR (event.gadget = use) OR
        (event.gadget = cancel) OR (event.gadget = d);
  SetBusyStat(statBusy);
  change:= (mulS <> oldmulS) OR (color <> oldcolor) OR (dualpf <> olddual);
  IF (event.gadget <> cancel) AND (event.gadget <> d) THEN
   FlushEvents;
   IF change THEN
    FlushGraphics;
    Refresh:= oldRefresh;
    InitGraphics
   ELSIF oldRefresh <> NIL THEN
    Refresh:= oldRefresh;
    Refresh
   END
  ELSE
   mulS:= oldmulS; dfltGraphic:= olddflt; dualpf:= olddual;
   color:= oldcolor; explosions:= oldexplosions;
   Refresh:= oldRefresh;
   Refresh
  END;
  EnableFileMenus;
  IF event.gadget = save THEN
   SaveConfig
  END;
  DeepFreeGadget(d)
 END AskGraphicSettings;

 PROCEDURE AskSoundSettings;
  VAR
   gadget, autoconf, cbsound, cbstereo, tchan, save, use, cancel: GadgetPtr;
   group1, group2, group3: GadgetPtr;
   event: Event;
   tagItem: TagItem;
   oldsound, oldmusic, oldstereo, olddflt: BOOLEAN;
   oldmusicPri: INT16;
   oldnbChannel: CARD16;
   change: BOOLEAN;
 BEGIN
  DisableFileMenus;
  SetBusyStat(statBusy);
  FlushEvents;
  oldsound:= sound; oldmusic:= music; oldstereo:= stereo;
  oldmusicPri:= musicPri; oldnbChannel:= nbChannel;
  olddflt:= dfltSound;
  d:= CreateGadget(dDialog, TAG2(dTEXT, ADL("Sounds settings"), dFLAGS, dfCLOSE + dfSELECT));
  group1:= AddNewGadget(d, dGroup, TAG1(dFLAGS, dfVDIR));
   autoconf:= AddNewGadget(group1, dBool, TAG2(dTEXT, ADL("Auto configure"), dFLAGS, dfJUSTIFY));
   group3:= AddNewGadget(group1, dGroup, TAG1(dFLAGS, dfBORDER + dfVDIR));
    group2:= AddNewGadget(group3, dGroup, NIL);
     cbsound:= AddNewGadget(group2, dCheckbox, TAG1(dTEXT, ADL("Sounds")));
     cbstereo:= AddNewGadget(group2, dCheckbox, TAG1(dTEXT, ADL("Stereo")));
    group2:= AddNewGadget(group3, dGroup, NIL);
     gadget:= AddNewGadget(group2, dLabel, TAG1(dTEXT, ADL("Number of channels:")));
     tchan:= AddNewGadget(group2, dIntEdit, TAG2(dINTVAL, nbChannel, dTXTLEN, 3));
   group3:= AddNewGadget(group1, dGroup, NIL);
    save:= AddNewGadget(group3, dButton, TAG1(dTEXT, ADL("Save")));
    use:= AddNewGadget(group3, dButton, TAG1(dTEXT, ADL("Use")));
    cancel:= AddNewGadget(group3, dButton, TAG1(dTEXT, ADL("Cancel")));
  CheckMemBool(RefreshGadget(d) <> DialogOk);
  REPEAT
   ModifyGadget(cbsound, TAG2(dFLAGS, dfACTIVE * ORD(NOT dfltSound) + dfSELECT * ORD(sound), dMASK, dfSELECT + dfACTIVE));
   ModifyGadget(cbstereo, TAG2(dFLAGS, dfACTIVE * ORD(sound AND NOT(dfltSound)) + dfSELECT * ORD(stereo), dMASK, dfACTIVE + dfSELECT));
   ModifyGadget(tchan, TAG2(dFLAGS, dfACTIVE * ORD(NOT dfltSound), dMASK, dfACTIVE));
   ModifyGadget(autoconf, TAG2(dFLAGS, dfSELECT * ORD(dfltSound), dMASK, dfSELECT));
   REPEAT
    IF gameStat = Break THEN RETURN END;
    SetBusyStat(statWaiting);
    WaitEvent;
    GetEvent(event);
    IF event.type = eGADGET THEN
     IF event.gadget = cbsound THEN
      sound:= NOT(sound);
      stereo:= stereo AND sound
     ELSIF event.gadget = cbstereo THEN
      stereo:= NOT(stereo)
     ELSIF event.gadget = autoconf THEN
      dfltSound:= NOT(dfltSound);
      IF dfltSound THEN
       DefaultSound;
       ModifyGadget(tchan, TAG1(dINTVAL, nbChannel))
      END
     END
    ELSE
     CommonEvent(event)
    END
   UNTIL event.type = eGADGET;
   tagItem.tag:= dINTVAL;
   GetGadgetAttr(tchan, tagItem);
   nbChannel:= tagItem.data;
   IF nbChannel < 1 THEN sound:= FALSE END
  UNTIL (event.gadget = save) OR (event.gadget = use) OR
        (event.gadget = cancel) OR (event.gadget = d);
  SetBusyStat(statBusy);
  change:= (sound <> oldsound) OR (stereo <> oldstereo) OR
           (nbChannel <> oldnbChannel) OR
           (music <> oldmusic) OR (musicPri <> oldmusicPri);
  IF (event.gadget <> cancel) AND (event.gadget <> d) THEN
   IF nbChannel > 16 THEN nbChannel:= 16 END;
   IF change THEN
    DeepFreeGadget(d);
    FlushSounds;
    InitSounds
   END
  ELSE
   sound:= oldsound; music:= oldmusic; stereo:= oldstereo;
   musicPri:= oldmusicPri; nbChannel:= oldnbChannel;
   dfltSound:= olddflt;
  END;
  EnableFileMenus;
  IF event.gadget = save THEN
   SaveConfig
  END;
  DeepFreeGadget(d)
 END AskSoundSettings;

 PROCEDURE AskLanguage;
  CONST
   Busy = "----------------------";
  VAR
   group1, group2, cycle, save, use, cancel: GadgetPtr;
   event: Event;
   name: ADDRESS;
   newlanguage: CARD8;
   change: BOOLEAN;
 BEGIN
  DisableFileMenus;
  SetBusyStat(statBusy);
  FlushEvents;
  newlanguage:= language;
  d:= CreateGadget(dDialog, TAG1(dTEXT, ADL("Language:")));
  group1:= AddNewGadget(d, dGroup, TAG1(dFLAGS, dfVDIR));
   cycle:= AddNewGadget(group1, dCycle, TAG1(dTEXT, ADL(Busy)));
   group2:= AddNewGadget(group1, dGroup, NIL);
    save:= AddNewGadget(group2, dButton, TAG1(dTEXT, ADL("Save")));
    use:= AddNewGadget(group2, dButton, TAG1(dTEXT, ADL("Use")));
    cancel:= AddNewGadget(group2, dButton, TAG1(dTEXT, ADL("Cancel")));
  CheckMemBool(RefreshGadget(d) <> DialogOk);
  REPEAT
   LOOP
    SetBusyStat(statBusy);
    IF dfltLang THEN
     name:= ADL("<Default>")
    ELSE
     name:= GetLanguageName(newlanguage)
    END;
    IF name <> NIL THEN EXIT END;
    dfltLang:= TRUE;
    newlanguage:= 0
   END;
   ModifyGadget(cycle, TAG1(dTEXT, name));
   REPEAT
    IF gameStat = Break THEN RETURN END;
    SetBusyStat(statWaiting);
    WaitEvent;
    GetEvent(event);
    IF event.type = eGADGET THEN
     IF event.gadget = cycle THEN
      IF dfltLang THEN dfltLang:= FALSE ELSE INC(newlanguage) END
     END
    ELSE
     CommonEvent(event)
    END
   UNTIL event.type = eGADGET
  UNTIL event.gadget <> cycle;
  SetBusyStat(statBusy);
  IF dfltLang THEN newlanguage:= dfltLanguage END;
  change:= (newlanguage <> language);
  IF (event.gadget <> cancel) AND (event.gadget <> d) THEN
   IF change THEN
    SetLanguage(newlanguage);
    InitMenus;
    IF Refresh <> NIL THEN Refresh END
   END
  END;
  EnableFileMenus;
  IF event.gadget = save THEN
   SaveConfig
  END;
  DeepFreeGadget(d)
 END AskLanguage;

 PROCEDURE CommonEvent(event: Event);
  VAR
   menu: MenuPtr;
   miscChanges: SET16;
 BEGIN
  IF event.type = eMENU THEN
   menu:= AddressToMenu(event.menu);
   IF menu = newMenu THEN
    IF (gameStat = Playing) OR (gameStat = Finish) THEN
     gameStat:= Gameover; water:= FALSE
    END
   ELSIF menu = loadMenu THEN
    IF (d = noGadget) AND fmEnabled THEN LoadGame END
   ELSIF menu = saveMenu THEN
    IF (d = noGadget) AND fmEnabled THEN SaveGame END
   ELSIF menu = hideMenu THEN
    IF d = noGadget THEN Hide END
   ELSIF menu = quitMenu THEN
    Quit
   ELSIF menu = graphicsMenu THEN
    IF (d = noGadget) AND fmEnabled THEN AskGraphicSettings END
   ELSIF menu = soundsMenu THEN
    IF (d = noGadget) AND fmEnabled THEN AskSoundSettings END
   ELSIF menu = languageMenu THEN
    IF (d = noGadget) AND fmEnabled THEN AskLanguage END
   ELSIF menu = miscMenu THEN
    IF (d = noGadget) AND fmEnabled THEN
     SetBusyStat(statBusy);
     DisableFileMenus;
     FlushSounds;
     miscChanges:= AskMiscSettings(SET16{msGraphic, msSound, msInput, msClock, msDialogs, msMenus});
     IF (msGraphic IN miscChanges) THEN FlushGraphics END;
     IF (msMenus IN miscChanges) THEN FlushMenus; InitMenus END;
     IF (msGraphic IN miscChanges) THEN InitGraphics END;
     InitSounds;
     EnableFileMenus
    END
   END
  ELSIF event.type = eKEYBOARD THEN
   IF (event.ch = "Q") OR (event.ch = "q") THEN Quit END
  ELSIF event.type = eREFRESH THEN
   REPEAT
    BeginRefresh;
    IF Refresh <> NIL THEN Refresh END
   UNTIL EndRefresh()
  ELSIF event.type = eSYS THEN
   IF (event.msg = pQuit) OR (event.msg = pKill) THEN Quit END
  END
 END CommonEvent;

BEGIN

 lsFile:= "Games/";
 Refresh:= NIL;
 AddTermProc(ColdFlush);
 ColdInit;

END ChaosInterface.
