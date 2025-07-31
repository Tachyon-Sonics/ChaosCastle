IMPLEMENTATION MODULE GrotteSupport;
  (*$ CStrings:= FALSE *)
 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM Memory IMPORT SET16, CARD8, INT8, CARD16, INT16, CARD32, INT32,
  AllocMem, FreeMem, StrPtr, CopyStr, TagItem, TagItemPtr, StrLength,
  ADS;
 FROM Checks IMPORT Check, CheckMem, Terminate, CheckMemBool, AddTermProc;
 FROM ANSITerm IMPORT Read, ReadAgain,
  ReadString, Report, Write, WriteString, WriteLn, WriteAt, MoveChar,
  Ghost, Goto, ClearScreen, Color, ClearLine, WaitChar;
 FROM Clock IMPORT TimePtr, AllocTime, StartTime, WaitTime, FreeTime,
  GetTime, noTime, GetCurrentTime;
 FROM Trigo IMPORT RND;
 FROM Files IMPORT FilePtr, noFile, OpenFile, ReadFileBytes, SkipFileBytes,
  WriteFileBytes, CloseFile, AccessFlags, AccessFlagSet, DirectoryPtr,
  noDir, OpenDirectory, DirectoryNext, CloseDirectory, fNAME, fFLAGS, afFILE;
 FROM GrotteSounds IMPORT FinalMusic, GameOverSound;
 FROM GrotteBonus IMPORT BonusLevel;

 CONST
  DecorFile = "Decors";

 TYPE
  GrCh = ARRAY[0..71],[0..31] OF CHAR;
  Arr7 = ARRAY[0..6] OF CARD8;

 VAR
  fh: FilePtr;
  termopen, chkesc, dm, no10, first: BOOLEAN;
  lastgame, lastlevel: CARD8;
  rndcount, randomcount: CARD16;
  g: POINTER TO GrCh;
  cols: Arr7;

 PROCEDURE WL(s: ARRAY OF CHAR);
 BEGIN
  WriteString(s);
  WriteLn;
 END WL;

 PROCEDURE WaitAChar(VAR ch: CHAR);
 BEGIN
  WaitChar(ch);
  IF (ch = 33C) OR (ch = 3C) OR (ch = "q") OR (ch = "Q") THEN Terminate END
 END WaitAChar;

 PROCEDURE WA(x, y: INT8; s: ARRAY OF CHAR);
 BEGIN
  Goto(x, y);
  WriteString(s)
 END WA;

 PROCEDURE Random(): CARD16;
  (*$ OverflowChk:= FALSE *)
 BEGIN
  randomcount:= (randomcount * 13077 + 6925) MOD 32768;
  RETURN randomcount
 END Random;
  (*$ POP OverflowChk *)

 PROCEDURE Rnd(): CARD16;
  (*$ OverflowChk:= FALSE  RangeChk:= FALSE *)
 BEGIN
  rndcount:= (rndcount * 13077 + 6925 + RND()) MOD 32768;
  RETURN rndcount
 END Rnd;
  (*$ POP OverflowChk  POP RangeChk *)

 PROCEDURE ReadDir(VAR ch: CHAR);
 BEGIN
  Read(ch);
  IF ch = 33C THEN
   IF chkesc THEN ch:= 3C; RETURN ELSE chkesc:= TRUE END;
   Read(ch)
  END;
  IF (ch = 233C) OR (ch = "[") THEN Read(ch) END;
  IF (ch <> 0C) AND (ch <> 33C) THEN chkesc:= FALSE END
 END ReadDir;

 PROCEDURE BigInit;
  VAR
   t: OBJECT;
 BEGIN
  fh:= noFile; time:= noTime;
  dm:= FALSE; first:= TRUE; no10:= TRUE;
  stat:= Finish;
  cols[0]:= 6; cols[1]:= 4; cols[2]:= 5;
  cols[3]:= 1; cols[4]:= 3; cols[5]:= 2; cols[6]:= 7;
  score:= 0; followscore:= 0; oldpvie:= 1;
  l2count:= 0; blvcount:= 0;
  lastgame:= 0; lastlevel:= 0;
  chkesc:= FALSE; termopen:= FALSE;
  FOR t:= MIN(OBJECT) TO MAX(OBJECT) DO
   WITH attr[t] DO
    nb:= 0; dt:= 0; nt:= 0; timeout:= 0; ct:= 0; co:= 0
   END
  END;
  ClearScreen; Color(1);
  time:= AllocTime(1024);
  CheckMemBool(time = noTime);
  StartTime(time);
  randomcount:= Rnd()
 END BigInit;

 PROCEDURE BigFlush;
 BEGIN
  CloseFile(fh);
  FreeTime(time)
 END BigFlush;

 PROCEDURE WriteCard(c: CARD32);
  VAR z: BOOLEAN;
      q, v: CARD32;
 BEGIN
  z:= FALSE;
  q:= 1000000000;
  REPEAT
   v:= c DIV q;
   c:= c MOD q;
   q:= q DIV 10;
   IF v = 0 THEN
    IF z OR (q = 0) THEN Write("0") END
   ELSE
    Write(CHR(v + 48)); z:= TRUE
   END
  UNTIL q = 0
 END WriteCard;

 PROCEDURE Gameover(score: CARD32);
  CONST
   l1 = "TTTTT  OOO  PPPPP        SSSS  CCCC  OOO  RRRRR EEEEE  SSSS";
   l2 = "  T   O   O P   P       S     C     O   O R   R E     S    ";
   l3 = "  T   O   O PPPPP        SSS  C     O   O RRRRR EEEE   SSS ";
   l4 = "  T   O   O P               S C     O   O RR    E         S";
   l5 = "  T    OOO  P           SSSS   CCCC  OOO  R RRR EEEEE SSSS ";
  VAR x, y: INT8;
      ch: CHAR;
      lines: ARRAY[0..4] OF POINTER TO ARRAY[0..58] OF CHAR;
 BEGIN
  lines[0]:= ADR(l1); lines[1]:= ADR(l2); lines[2]:= ADR(l3);
  lines[3]:= ADR(l4); lines[4]:= ADR(l5);
  Goto(0, 0); Color(1);
  FOR y:= 0 TO 19 DO
   FOR x:= 0 TO 71 DO
    Write("#")
   END;
   WriteLn
  END;
  StartTime(time);
  FOR x:= 6 TO 0 BY -1 DO
   Color(cols[x]);
   WA(7, 6, " &&&&& &&&&& && && &&&&&        &&&  &   & &&&&& &&&&& ");
   WA(7, 7, " &     &   & &&&&& &           &   & &   & &     &   & ");
   WA(7, 8, " & &&& &&&&& & & & &&&&        &   &  & &  &&&&  &&&&& ");
   WA(7, 9, " &   & &   & &   & &           &   &  & &  &     &&    ");
   WA(7,10, " &&&&& &   & &   & &&&&&        &&&    &   &&&&& & &&& ");
   WriteLn;
   (* Flush; *)
   IF NOT(WaitTime(time, 512)) THEN StartTime(time) END
  END;
  IF WaitTime(time, 4096) THEN END;
  FOR x:= 1 TO 10 DO
   IF ODD(x) THEN Color(7) ELSE Color(6) END;
   WA(0, 21, "Score: "); WriteCard(score);
   IF WaitTime(time, 100) THEN END
  END;
  REPEAT Read(ch) UNTIL ch <= 3C;
  WaitAChar(ch);
  Terminate;
 END Gameover;

 TYPE
  PassWord = ARRAY[0..11] OF SET16;
  PassPtr = POINTER TO PassWord;

 PROCEDURE Set(VAR p: CARD16; pass: PassPtr; v: BOOLEAN);
 BEGIN
  IF v THEN INCL(pass^[p DIV 16], p MOD 16) END;
  INC(p)
 END Set;

 PROCEDURE SetVal(VAR p: CARD16; pass: PassPtr; val, cnt: CARD32);
 BEGIN
  WHILE cnt > 0 DO
   Set(p, pass, (val MOD 2) = 1);
   val:= val DIV 2; DEC(cnt)
  END
 END SetVal;

 PROCEDURE Get(VAR p: CARD16; pass: PassPtr): BOOLEAN;
  VAR v: BOOLEAN;
 BEGIN
  v:= (p MOD 16) IN pass^[p DIV 16];
  INC(p);
  RETURN v
 END Get;

 PROCEDURE GetVal(VAR p: CARD16; pass: PassPtr; cnt: CARD32): CARD32;
  VAR
   s, v: CARD32;
 BEGIN
  (*$ OverflowChk:= FALSE  RangeChk:= FALSE *)
  s:= 1; v:= 0;
  WHILE cnt > 0 DO
   IF Get(p, pass) THEN INC(v, s) END;
   INC(s, s); DEC(cnt)
  END;
  RETURN v
 END GetVal;
  (*$ POP OverflowChk  POP RangeChk *)

 PROCEDURE WriteCode(c, s: CARD16);
  VAR
   i: INT16;
 BEGIN
   (* It is very unfortunate to dicover that most text font will
    * not properly distinguish 0 and O, or 1, l and I !
    *)
  c:= (c + s) MOD 64;
  i:= c;
  IF i = ORD("O") - 65 THEN
   Write(".")
  ELSIF i = ORD("I") - 65 THEN
   Write("/")
  ELSIF i = ORD("l") - 97 + 26 THEN
   Write("!")
  ELSIF i = ORD("0") - 48 + 52 THEN
   Write("-")
  ELSIF i = ORD("1") - 48 + 52 THEN
   Write("+")
  ELSIF i < 26 THEN
   Write(CHR(c + 65))
  ELSIF i < 52 THEN
   Write(CHR(c - 26 + 97))
  ELSIF i < 62 THEN
   Write(CHR(c - 52 + 48))
  ELSIF i = 62 THEN
   Write(":")
  ELSE
   Write("?")
  END
 END WriteCode;

 PROCEDURE ReadCode(ch: CHAR; s: CARD16): CARD16;
  VAR v: CARD16;
 BEGIN
  IF ch = "." THEN
   v:= ORD("O") - 65
  ELSIF ch = "/" THEN
   v:= ORD("I") - 65
  ELSIF ch = "!" THEN
   v:= ORD("l") - 97 + 26
  ELSIF ch = "-" THEN
   v:= ORD("0") - 48 + 52
  ELSIF ch = "+" THEN
   v:= ORD("1") - 48 + 52
  ELSIF (ch >= "A") AND (ch <= "Z") THEN
   v:= ORD(ch) - 65
  ELSIF (ch >= "a") AND (ch <= "z") THEN
   v:= ORD(ch) - 97 + 26
  ELSIF (ch >= "0") AND (ch <= "9") THEN
   v:= ORD(ch) - 48 + 52
  ELSIF ch = ":" THEN
   v:= 62
  ELSIF ch <> "?" THEN
   v:= 0
  ELSE
   v:= 63
  END;
  RETURN (v + 64 - s) MOD 64
 END ReadCode;

 PROCEDURE GameToPass;
  VAR
   pass: PassWord;
   x, y, p, sum: CARD16;
   ch: CHAR;
 BEGIN
  FOR x:= 0 TO 11 DO pass[x]:= SET16{} END;
  ClearScreen;
  Color(1);
  Goto(30, 1); WriteString("************");
  Goto(30, 3); WriteString("************");
  WriteAt(30, 2, "*"); WriteAt(41, 2, "*");
  Color(3); p:= 0;
  Goto(32, 2); WriteString("PASSWORD");
  FOR y:= 0 TO 9 DO
   FOR x:= 0 TO 10 DO
    Set(p, ADR(pass), x IN made[y])
   END
  END;
  Set(p, ADR(pass), no10);
  IF blvcount > 63 THEN blvcount:= 63 END;
  SetVal(p, ADR(pass), blvcount, 6);
  SetVal(p, ADR(pass), score, 32);
  SetVal(p, ADR(pass), pvie, 7);
  SetVal(p, ADR(pass), l2count, 6);
  p:= 0; Goto(21, 8); Color(6);
  sum:= 17;
  FOR y:= 0 TO 26 DO
   x:= GetVal(p, ADR(pass), 6);
   WriteCode(x, y);
   INC(sum, x);
   IF sum >= 64 THEN DEC(sum, 63) END
  END;
  WriteCode(sum, 27);
  Goto(4, 12); Color(7); (* Flush; *)
  StartTime(time); IF WaitTime(time, 2048) THEN END;
  REPEAT Read(ch) UNTIL ch <= 3C;
  WriteString("Press any key");
  REPEAT
   Read(ch);
   IF WaitTime(time, 64) THEN END
  UNTIL ch <> 0C
 END GameToPass;

 PROCEDURE PassToGame;
  VAR
   x, y, p, sum: CARD16;
   pass: PassWord;
   str: ARRAY[0..28] OF CHAR;
   ch: CHAR;
 BEGIN
  FOR x:= 0 TO 11 DO pass[x]:= SET16{} END;
  ClearScreen; Color(1);
  Goto(26, 1); WriteString("*******************");
  Goto(26, 3); WriteString("*******************");
  WriteAt(26, 2, "*"); WriteAt(44, 2, "*");
  Color(2);
  Goto(28, 2); WriteString("Enter Password:");
  Goto(21, 8); Color(4);
  FOR x:= 0 TO 27 DO Write("#") END;
  Goto(21, 8); Color(6);
  ReadString(str);
  sum:= 17; p:= 0;
  FOR x:= 0 TO 26 DO
   y:= ReadCode(str[x], x);
   INC(sum, y);
   IF sum >= 64 THEN DEC(sum, 63) END;
   SetVal(p, ADR(pass), y, 6)
  END;
  IF sum = ReadCode(str[27], 27) THEN
   p:= 0;
   FOR y:= 0 TO 9 DO
    FOR x:= 0 TO 10 DO
     IF Get(p, ADR(pass)) THEN
      INCL(made[y], x)
     ELSE
      EXCL(made[y], x)
     END
    END
   END;
   no10:= Get(p, ADR(pass));
   blvcount:= GetVal(p, ADR(pass), 6);
   IF blvcount > 0 THEN
    EXCL(made[10], 0)
   ELSE
    INCL(made[10], 0)
   END;
   score:= GetVal(p, ADR(pass), 32);
   followscore:= ((score + 1000) DIV 1000) * 1000;
   pvie:= GetVal(p, ADR(pass), 7);
   l2count:= GetVal(p, ADR(pass), 6)
  ELSE
   Goto(25, 10); Color(3);
   WriteString("* Invalid password *");
   Goto(25, 11); Color(7);
   WriteString("Press any key");
   WaitAChar(ch)
  END;
  IF pvie <= 0 THEN Gameover(0) END
 END PassToGame;

 PROCEDURE ReadGame(VAR level, game: CARD8);
  VAR on: BOOLEAN;
      gc, c1, c2: CARD8;
      ch: CHAR;
      best, left: CARD16;

  PROCEDURE Made(level, game: CARD8): BOOLEAN;
  BEGIN
   RETURN game IN made[level]
  END Made;

  PROCEDURE ViewGame(level, game: CARD8; on: BOOLEAN);
   VAR
    test1, test2: BOOLEAN;
  BEGIN
   IF level <> 10 THEN
    Goto(game * 3 + 2, level + 8)
   ELSE
    Goto(2, 18);
    IF on THEN
     IF NOT Made(10, 0) THEN Color(2); WriteString("BONUS LEVEL") END
    ELSE
     WriteString("           ")
    END;
    RETURN
   END;
   IF Made(level, game) THEN Color(1) ELSE
    test1:= (game = 9); test2:= (level >= 7);
    IF test1 <> test2 THEN Color(3) ELSE Color(2) END;
    IF test1 AND test2 THEN Color(7) END
   END;
   IF on THEN
    IF Made(level, game) THEN
     WriteString("[]")
    ELSE
     Write(CHR((level+1) MOD 10 + 48));
     Write(CHR(game + 65))
    END
   ELSE
    WriteString("  ")
   END
  END ViewGame;

  PROCEDURE DisplayGrotte(x: INT8);
  BEGIN
   WA(x, 0, " 00000  00000   000   00000  00000  00000");
   WA(x, 1, " 0      0   0  0   0    0      0    0");
   WA(x, 2, " 0 000  00000  0   0    0      0    0000");
   WA(x, 3, " 0   0  00     0   0    0      0    0");
   WA(x, 4, " 00000  0 000   000     0      0    00000");
   Goto(0, 0);
  END DisplayGrotte;

  PROCEDURE Display;
   VAR
    c1, c2: CARD8;
  BEGIN
   ClearScreen; StartTime(time);
   IF first THEN
    FOR c1:= 1 TO 3 DO
     Color(c1);
     DisplayGrotte(0);
     IF NOT WaitTime(time, 512) THEN StartTime(time) END
    END;
    FOR c1:= 1 TO 14 DO
     DisplayGrotte(c1);
     IF NOT(WaitTime(time, 200)) THEN StartTime(time) END
    END;
    first:= FALSE
   END;
   Color(3); DisplayGrotte(15);
   Color(1);
   WA(28, 5, "(C) 2000 Nicky");
   Color(5);
   WA(38, 7, "Use the following keys from the numeric");
   WA(38, 8, "pad (Num Lock) to move during the game:");
   WA(40, 9, "{4}: start moving to the left");
   WA(40, 10, "{6}: start moving to the right");
   WA(40, 11, "{5}: stop moving");
   WA(40, 12, "<SPACE> or {0}: jump");
   WA(40, 13, "{1}, {3}, {8}, {2}:");
   WA(40, 14, " fire left, right, up and down");
   WA(40, 15, "{7}, {9}: big fire left and right");
   Color(4);
   WA(38, 16, "{q}: quit      {e}: give up level");
   WA(38, 17, "{r}: refresh   {m}: choose random");
   WA(38, 18, "{p}: pause     {s}, {l}: save, load");
   WA(38, 19, "{arrow}: move  {shift arrow}: fire");
   Color(7);
   WA(0, 7, "Choose a level and press <RETURN>:");
   WA(0, 21, "Score: "); WriteCard(score);
   WA(36, 21, "Lives: "); Color(3); WriteCard(pvie);
   ShowClock;
   left:= 0;
   FOR c1:= 0 TO 9 DO
    FOR c2:= 0 TO 9 DO
     IF NOT Made(c1, c2) THEN INC(left) END;
     ViewGame(c1, c2, TRUE)
    END
   END;
   IF NOT Made(10, 0) THEN ViewGame(10, 0, TRUE) END;
  END Display;

 BEGIN
  on:= TRUE;
  REPEAT Read(ch) UNTIL ch <= 3C;
  level:= lastlevel; game:= lastgame;
  IF level > 10 THEN level:= 0 END;
  IF oldpvie = 0 THEN
   EXCL(made[level], game);
   oldpvie:= 1;
   IF blvcount <= 0 THEN INCL(made[10], 0) END
  END;
  Display;
  gc:= 102;
  WHILE Made(level, game) AND (gc > 0) DO
   INC(game); DEC(gc);
   IF (game > 9) OR ((game > 0) AND (level = 10)) THEN
    game:= 0; INC(level);
    IF level > 10 THEN level:= 0 END
   END
  END;
  IF gc = 0 THEN
   ClearScreen;
   StartTime(time);
   IF WaitTime(time, 3072) THEN END;
   Color(7); Goto(0, 20);
   WriteString("**** YOU'VE FINISHED THE GAME !!! ****");
   Goto(0, 0); Color(3);
   FOR c1:= 0 TO 19 DO
    FOR c2:= 0 TO 71 DO
     Write("%")
    END;
    WriteLn
   END;
   FinalMusic;
   StartTime(time);
   IF WaitTime(time, 16000) THEN END;
   REPEAT
    INC(score, 1000); DEC(pvie);
    Color(7);
    WA(0, 21, "Score: "); WriteCard(score);
    WA(36, 21, "Lives: "); Color(3); WriteCard(pvie);
    Write(" ");
    IF WaitTime(time, 1800) THEN END
   UNTIL pvie = 0;
   GameOverSound;
   Gameover(score)
  END;
  StartTime(time);
  c1:= 3; c2:= 0;
  REPEAT
   Goto(0, 22);
   IF Random() = 0 THEN END;
   ReadDir(ch);
   IF (ch = "A") OR (ch = 34C) THEN ch:= "8" END;
   IF (ch = "B") OR (ch = 35C) THEN ch:= "2" END;
   IF (ch = "C") OR (ch = 36C) THEN ch:= "6" END;
   IF (ch = "D") OR (ch = 37C) THEN ch:= "4" END;
   IF ch <> 0C THEN ViewGame(level, game, TRUE); on:= TRUE; c1:= 1 END;
   IF (ch = "4") AND (game > 0) THEN DEC(game) END;
   IF (ch = "6") AND (game < 9) THEN INC(game) END;
   IF (ch = "2") AND (level < 10) THEN INC(level) END;
   IF (level = 10) AND (Made(10, 0)) THEN DEC(level) END;
   IF (ch = "8") AND (level > 0) THEN DEC(level) END;
   IF (ch = "s") OR (ch = "S") THEN GameToPass; Display END;
   IF (ch = "l") OR (ch = "L") THEN PassToGame; Display END;
   IF (ch = "r") OR (ch = "R") OR (ch = "w") OR (ch = "W") THEN Display END;
   IF (ch = "M") OR (ch = "m") THEN
    IF NOT (Made(10, 0)) THEN level:= 10; game:= 0 ELSE
     IF pvie > 25 THEN level:= 125 ELSE level:= pvie * 5 END;
     INC(level, Rnd() MOD 32);
     IF level <> 0 THEN DEC(level) END;
     best:= level;
     best:= (left * best) DIV 160;
     level:= 0; game:= 0;
     WHILE Made(level, game) DO
      INC(game);
      IF game >= 10 THEN game:= 0; level:= (level + 1) MOD 10 END
     END;
     WHILE best > 0 DO
      REPEAT
       INC(game);
       IF game >= 10 THEN game:= 0; level:= (level + 1) MOD 10 END
      UNTIL NOT Made(level, game);
      DEC(best)
     END
    END
   END;
   IF (ch = "q") OR (ch = "Q") OR (ch = 3C) OR (ch = 33C) THEN
    ClearScreen; Terminate
   END;
   IF (ch = "I") OR (ch = "i") THEN DisplayInstructions; Display END;
   IF NOT WaitTime(time, 64) THEN StartTime(time) END;
   IF c2 = 0 THEN
    ShowClock;
    Goto(1, 19); Color(6); c2:= 40;
    WriteString("Press {I} for more instructions")
   ELSIF c2 = 1 THEN
    Goto(1, 19); WriteString("                               ")
   END;
   DEC(c1);
   IF c1 = 0 THEN on:= NOT on; ViewGame(level, game, on); c1:= 6; DEC(c2) END
  UNTIL ((ch = 15C) OR (ch = 12C)) AND (NOT Made(level, game));
  IF level = 10 THEN game:= 0 END;
  IF NOT(10 IN made[level]) THEN
   INCL(made[level], 10); EXCL(made[level], 9)
  END;
  IF (game = 9) AND no10 THEN
   no10:= FALSE;
   made[7]:= SET16{9}; made[8]:= SET16{9}; made[9]:= SET16{9}
  END;
  IF level < 10 THEN lastlevel:= level; lastgame:= game END
 END ReadGame;

 PROCEDURE ShowClock;
  VAR
   h, m, s: CARD16;
 BEGIN
  GetCurrentTime(h, m ,s);
  Goto(67, 20); Color(7);
  Write(CHR(h DIV 10 + 48)); Write(CHR(h MOD 10 + 48));
  Write(":");
  Write(CHR(m DIV 10 + 48)); Write(CHR(m MOD 10 + 48))
 END ShowClock;

 PROCEDURE TypeOf(ch: CHAR): OBJECT;
  VAR t: OBJECT;
 BEGIN
  CASE ch OF
    "*": t:= PLAYER
   |" ": t:= EMPTY
   |"&": t:= K0
   |"+": t:= K1
   |"X": t:= K2
   |"#": t:= K3
   |"x": t:= K4
   |">", "<", "Y": t:= GN1
   |"£": t:= GN2
   |"=": t:= ASC
   |"-", "|": t:= L1
   |")", "(": t:= L2
   |"}", "{": t:= L3
   |"O": t:= BALL
   |"V": t:= PIC
   |"T": t:= PLAT
   |"W": t:= WOOF
   |"U": t:= TPLAT
   |"Z": t:= NID
   |"N": t:= BUB
   |"%": t:= BN
   |"s": t:= DL
   |"H": t:= BM
   |".", "$", "@": t:= SBN
   ELSE t:= MR
  END;
  RETURN t
 END TypeOf;

 PROCEDURE SetColor(ch: CHAR);
 BEGIN
  CASE TypeOf(ch) OF
    K1..K4, L3: Color(1)
   |K0: Color(3)
   |BM, BALL: Color(6)
   |PLAYER, BN, DL, L1, L2, SBN: Color(2)
   |ASC, PLAT, WOOF, TPLAT: Color(4)
   |GN1, GN2, NID, BUB, PIC: Color(5)
   ELSE
    IF (ch = "A") OR (ch = "!") THEN Color(3) ELSE Color(7) END
  END
 END SetColor;

 PROCEDURE Complete(t: OBJECT; nvie: INT8; count: CARD8; ch: CHAR);
  VAR nx, ny, ns: INT8;
 BEGIN
  IF (t = K4) OR (t = PIC) THEN ns:= 2 ELSE ns:= 0 END;
  WHILE attr[t].nb < count DO
   REPEAT
    IF Random() = 0 THEN END;
    nx:= Random() MOD 72;
    ny:= Random() MOD 20;
   UNTIL Report(nx, ny) = " ";
   Create(t, nx, ny, nvie, ns, ch)
  END
 END Complete;

 PROCEDURE NoMem;
 BEGIN
  WA(0, 20, "***** Not enough memory *****");
  StartTime(time);
  IF WaitTime(time, 1024) THEN END
 END NoMem;

 PROCEDURE DosErr;
 BEGIN
  WA(0, 20, "***** File input error *****");
  StartTime(time);
  IF WaitTime(time, 1024) THEN END
 END DosErr;

 TYPE
  ChoicePtr = POINTER TO Choice;
  Choice = RECORD
   name: ARRAY[0..127] OF CHAR;
   drawer: BOOLEAN;
   next: ChoicePtr;
  END;

 PROCEDURE DisplayFile(name: StrPtr);
  VAR fh: FilePtr;
      ch, fch: CHAR;
      y: CARD16;
 BEGIN
  ch:= 0C;
  ClearScreen; fch:= 0C; Color(7); y:= 0;
  fh:= OpenFile(name, AccessFlagSet{accessRead});
  IF fh = noFile THEN DosErr; RETURN END;
  WHILE (ReadFileBytes(fh, ADR(fch), 1) = 1) AND
        (ch <> 3C) AND (ch <> 33C) AND (ch <> "Q") AND (ch <> "q") DO
   IF fch < " " THEN WriteLn; INC(y) ELSE Write(fch) END;
   IF y >= 22 THEN
    WaitChar(ch);
    ClearScreen; y:= 0
   END
  END;
  CloseFile(fh)
 END DisplayFile;

 PROCEDURE DisplayChoice(drawer: StrPtr);
  VAR
   d: DirectoryPtr;
   tags: ARRAY[0..2] OF TagItem;
   first, new, current: ChoicePtr;
   count, length, c: INT16;
   ch: CHAR;
 BEGIN
  count:= 0;
  first:= NIL; current:= NIL;
  d:= OpenDirectory(drawer);
  IF d = noDir THEN RETURN END;
  LOOP
   tags[0].tag:= fNAME;
   tags[1].tag:= fFLAGS;
   tags[2].tag:= 0;
   IF NOT DirectoryNext(d, ADR(tags)) THEN EXIT END;
   new:= AllocMem(SIZE(Choice));
   IF new = NIL THEN NoMem; EXIT END;
   IF current <> NIL THEN current^.next:= new END;
   current:= new; current^.next:= NIL;
   IF first = NIL THEN first:= new END;
   CopyStr(tags[0].addr, ADR(new^.name), SIZE(new^.name));
   new^.drawer:= (tags[1].data DIV afFILE) MOD 2 = 0
  END;
  CloseDirectory(d);
  REPEAT
   ClearScreen; Color(7);
   WL("{0}: <---"); count:= 0;
   LOOP
    INC(count);
    IF count < 10 THEN ch:= CHR(48 + count) ELSE ch:= CHR(55 + count) END;
    current:= first;
    LOOP
     IF current = NIL THEN EXIT END;
     length:= StrLength(ADR(current^.name));
     IF current^.name[length - 1] = ch THEN EXIT END;
     current:= current^.next
    END;
    IF current = NIL THEN EXIT END;
    Write("{"); Write(ch);
    WriteString("}: "); length:= StrLength(ADR(current^.name));
    c:= length - 1;
    LOOP
     IF c <= 0 THEN c:= 0; EXIT END;
     DEC(c); ch:= current^.name[c];
     IF (ch = ":") OR (ch = "/") OR (ch = "\") THEN INC(c); EXIT END
    END;
    WHILE (c < length-2) AND (current^.name[c] <> 0C) DO
     Write(current^.name[c]); INC(c)
    END;
    WriteLn
   END;
   WaitChar(ch);
   IF (ch >= "a") AND (ch <= "k") THEN ch:= CHR(ORD(ch) - 32) END;
   Color(6); Write(ch);(* Flush;*)
   IF ch > "0" THEN
    current:= first;
    WHILE (current <> NIL) AND (current^.name[StrLength(ADR(current^.name))-1] <> ch) DO
     current:= current^.next
    END;
    IF current <> NIL THEN
     IF current^.drawer THEN
      DisplayChoice(ADR(current^.name))
     ELSE
      DisplayFile(ADR(current^.name))
     END
    END
   END
  UNTIL (ch <= "0") OR (ch = "q") OR (ch = "Q");
  current:= first;
  WHILE current <> NIL DO
   new:= current;
   current:= current^.next;
   FreeMem(new)
  END
 END DisplayChoice;

 PROCEDURE DisplayInstructions;
 BEGIN
  dm:= FALSE;
  DisplayChoice(ADS("Docs"))
 END DisplayInstructions;

 PROCEDURE InitK2Dirs;
  VAR ch, d: CHAR;
      x, y: INT16;
      t: OBJECT;

  PROCEDURE InitDir(up: BOOLEAN);
   VAR ppy, px, py, x, y: INT16;
       H: BOOLEAN;
       dd, gd, cd, ch, ch1, ch2, ch3: CHAR;
       sd, from, to, by: INT16;
       c: CARD16;
  BEGIN
   IF up THEN
    dd:= " "; from:= 1; to:= 18; by:= 1
   ELSE
    dd:= "|"; from:= 18; to:= 1; by:= -1
   END;
   H:= FALSE;
   (* fill with JUMP up to any H *);
   FOR x:= 0 TO 71 DO
    FOR y:= 19 TO 0 BY -1 DO
     ch:= g^[x, y];
     IF ch = "H" THEN H:= TRUE ELSIF ch = "0" THEN H:= FALSE END;
     IF (ch <> "H") AND (ch <> "0") THEN
      IF H THEN g^[x, y]:= dd ELSE g^[x, y]:= " " END
     END
    END
   END;
   (* where JUMP and MR, go on it *)
   IF d = "<" THEN sd:= -1 ELSE sd:= 1 END;
   gd:= d;
   FOR c:= 1 TO 2 DO
    y:= from - by;
    REPEAT
     INC(y, by);
     FOR x:= 1 TO 70 DO
      ch1:= g^[x + sd, y + 1];
      ch2:= g^[x, y + 1];
      IF (g^[x, y] = dd) AND (ch1 <> "H") AND (ch2 <> "0") AND
         (NOT(up) OR (ch1 = "0") OR (ch2 = "H"))
       AND (g^[x + sd, y] = " ")
      THEN
       px:= x; py:= y; ch:= g^[px + sd, py];
       WHILE (py > 0 ) AND (px > 0) AND (px < 71) AND
             (g^[px, py] = dd) AND
             ( (ch = " ") OR (ch = gd) ) AND
             (g^[px + sd, py + 1] <> "H") DO
        g^[px, py]:= gd;
        ppy:= py - 1;
        WHILE NOT(up) AND (g^[px, ppy] = "|") AND (ppy > 0) DO
         g^[px, ppy]:= " "; DEC(ppy)
        END;
        IF (g^[px, py - 1] = " ") AND (g^[px + sd, py] <> "H") THEN
         g^[px, py - 1]:= gd
        END;
        DEC(py); DEC(px, sd)
       END
      END
     END
    UNTIL y = to;
    sd:= -sd; cd:= gd;
    IF gd = "<" THEN gd:= ">" ELSE gd:= "<" END
   END;
   (* filling *)
   FOR y:= 1 TO 18 DO
    FOR x:= 1 TO 70 DO
     ch:= g^[x, y];
     IF (ch <> "0") AND (ch <> "H") THEN
      ch1:= g^[x, y - 1];
      IF up AND (g^[x, y + 1] = "0") AND (ch1 <> "0") AND (ch1 <> "H") THEN
       g^[x, y]:= " "
      ELSE
       ch2:= g^[x + sd, y];
       ch3:= g^[x + sd, y + 1];
       IF (up AND ((ch1 = "0") OR (ch1 = "H")))
       OR ((ch2 <> cd) AND (ch2 <> "0") AND (ch2 <> "H") AND (ch = " ")
           AND (ch3 <> "H") AND (ch3 <> "|"))
       THEN
        g^[x, y]:= gd
       END
      END
     END;
     ch:= g^[x, y];
     IF ch = "<" THEN px:= 1 ELSIF ch = ">" THEN px:= 3 ELSE px:= 0 END;
     drK2[y]^[x]:= (drK2[y]^[x] * 4) + VAL(CARD8, px)
    END
   END
  END InitDir;

 BEGIN

  REPEAT
   g:= AllocMem(SIZE(GrCh));
   IF g = NIL THEN
    ClearLine(20); ClearLine(21);
    Goto(0, 20); Color(3);
    WL("***** Not enough memory *****");
    WL(" Retry [Y/N] ?");
    REPEAT WaitChar(ch) UNTIL (ch <> 14C);
    ClearLine(20); ClearLine(21);
    IF (ch <> "y") AND (ch <> "Y") THEN RETURN END
   END
  UNTIL g <> NIL;
  FOR y:= 0 TO 19 DO
   FOR x:= 0 TO 71 DO
    drK2[y]^[x]:= 0;
    t:= TypeOf(Report(x, y));
    IF t = BM THEN g^[x, y]:= "H"
     ELSIF t = MR THEN g^[x, y]:= "0"
     ELSE g^[x, y]:= " "
    END
   END
  END;
  d:= "<"; InitDir(FALSE);
  d:= ">"; InitDir(FALSE);
  d:= "<"; InitDir(TRUE);
  d:= ">"; InitDir(TRUE);
  FreeMem(g)
 END InitK2Dirs;

(***** Niveaux Bonus *****)

 PROCEDURE Find(VAR x, y: INT16; sx, sy, dx, dy: INT16);
  VAR
   w, h: CARD16;
 BEGIN
  w:= dx - sx + 1; h:= dy - sy + 1;
  REPEAT
   x:= Rnd() MOD w; INC(x, sx);
   y:= Rnd() MOD h; INC(y, sy)
  UNTIL Report(x, y) = " "
 END Find;

 PROCEDURE PutObj(t: OBJECT; n, sx, sy, dx, dy, nvie, nseq: INT16; ch: CHAR);
  VAR x, y, c: INT16;
 BEGIN
  FOR c:= 1 TO n DO
   Find(x, y, sx, sy, dx, dy);
   Create(t, x, y, nvie, nseq, ch)
  END
 END PutObj;

 PROCEDURE LoadGame(C: CreateProc; VAR ax, ay: INT8);
  VAR
   posch: ARRAY[0..3] OF CHAR;
   pos: INT32;
   bcount, t: CARD8;
   c, c1, c2: INT16;
   ch, tmp: CHAR;
 BEGIN
  Create:= C;
  ClearScreen;
  Goto(0, 20); Color(7);
  WriteString("Loading, please wait... ");
  Goto(0, 0);
  ndoor:= 0;
  IF level < 10 THEN
 (* Load *)
  fh:= OpenFile(ADR(DecorFile), AccessFlagSet{accessRead});
  Check(fh = noFile, ADS("Can't open data file:"), ADS(DecorFile));
  c1:= level * 10 + game + 1;
  c2:= c1;
  WHILE c1 > 0 DO
   Check(ReadFileBytes(fh, ADR(posch), 4) <> 4, ADS("Error reading table in file"), ADR(DecorFile));
   pos:= ((ORD(posch[0]) * 256 + ORD(posch[1])) * 256
         + ORD(posch[2])) * 256 + ORD(posch[3]);
   DEC(c1)
  END;
  INC(pos, 400 - c2 * 4);
  Check(SkipFileBytes(fh, pos) <> pos, ADS(DecorFile), ADS("bad jump table"));
  bcount:= 0;
  FOR c1:= 0 TO 19 DO
   FOR c2:= 0 TO 71 DO
    IF bcount = 0 THEN
     Check(ReadFileBytes(fh, ADR(ch), 1) <> 1, ADS("Read error in file"), ADR(DecorFile));
     IF ch > 177C THEN
      ch:= CHR(ORD(ch) - 128);
      Check(ReadFileBytes(fh, ADR(tmp), 1) <> 1, ADS("Read error in file"), ADR(DecorFile));
      bcount:= ORD(tmp)
     ELSE
      bcount:= 1
     END
    END;
    DEC(bcount);
    Goto(c2, c1);
    IF ch = "g" THEN ch:= "£" END;
    CASE TypeOf(ch) OF
      PLAYER: Create(PLAYER, c2, c1, pvie, 0, "*")
     |K0: Create(K0, c2, c1, level + 2, 2, "&")
     |K1: Create(K1, c2, c1, level + 1, 0, "+")
     |K2: Create(K2, c2, c1, level DIV 2 + 1, 0, "X")
     |K3: Create(K3, c2, c1, level + 1, 0, "#")
     |K4: Create(K4, c2, c1, 4, 2, "x")
     |GN1: Create(GN1, c2, c1, 3, 0, ch)
     |GN2: Create(GN2, c2, c1, 1, 0, ch)
     |DL: Create(DL, c2, c1, 1, 0, " ")
     |ASC: Create(ASC, c2, c1, 1, 0, "=")
     |PIC: Create(PIC, c2, c1, level, 2, "V")
     |WOOF: Create(WOOF, c2, c1, 1, 0, "W")
     |TPLAT: Create(TPLAT, c2, c1, 1, 0, " ")
     |NID: Create(NID, c2, c1, 2, 0, "Z")
     |BUB: Create(BUB, c2, c1, 4, 0, "N")
     |BN, SBN: Color(2); Write(ch)
    ELSE
     IF ch = "!" THEN INC(ndoor) END;
     IF ch = "A" THEN
      ax:= c2; ay:= c1; Color(7); Write("0")
     ELSIF ch = "a" THEN
      Create(PLAT, c2, c1, 1, 0, "T");
      ppos[0].x:= c2; ppos[0].y:= c1
     ELSIF (ch >= "b") AND (ch <= "d") THEN
      c:= ORD(ch) - ORD("a"); Write(" ");
      ppos[c].x:= c2; ppos[c].y:= c1;
      t:= c;
      IF pcount < t THEN pcount:= t END
     ELSE
      SetColor(ch); Write(ch)
     END
    END
   END;
   IF c1 < 19 THEN
    IF ReadFileBytes(fh, ADR(ch), 1) = 1 THEN END;
    Check((bcount <> 0) OR (ch <> 12C), ADS(DecorFile), ADS("wrong format"));
    WriteLn
   END
  END;
  CloseFile(fh);
  c1:= 20;
  IF game MOD 3 = 0 THEN c1:= 25 END;
  IF game MOD 2 = 0 THEN c1:= 15 END;
  Complete(K3, level + 1, c1, "#");
  c1:= level DIV 2 + 1;
  c2:= 0;
  IF game = 6 THEN c2:= 2
  ELSIF game = 7 THEN c2:= 4
  ELSIF game>=5 THEN c2:= 6
  END;
  IF pvie > 6 THEN INC(c2) END;
  Complete(K2, c1, c2, "X");
  IF game = 9 THEN
   Complete(K1, level + 1, 6, "+")
  ELSE
   Complete(K1, level + 1, (game MOD 3) * 3, "+")
  END;
  IF (game > 1) AND (game <> 5) AND (game <> 8) THEN
   Complete(K4, 4, 1, "x")
  END;
  ELSE
   BonusLevel(ax, ay)
  END;
  ClearLine(20);
  Goto(0, 20); Color(7);
 END LoadGame;

BEGIN

 made[0]:= SET16{9}; made[1]:= SET16{9};
 made[2]:= SET16{9}; made[3]:= SET16{9};
 made[4]:= SET16{9}; made[5]:= SET16{9};
 made[6]:= SET16{9}; made[7]:= SET16{0..9};
 made[8]:= SET16{0..9}; made[9]:= SET16{0..9};
 made[10]:= SET16{0, 10};
 AddTermProc(BigFlush);
 BigInit;

END GrotteSupport.
