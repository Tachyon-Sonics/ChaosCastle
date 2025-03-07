IMPLEMENTATION MODULE Languages;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM LocaleD IMPORT Locale, LocalePtr, localeName;
 FROM OptLocaleL IMPORT OpenLocale, CloseLocale, localeBase;
 FROM ExecL IMPORT OpenLibrary, CloseLibrary, execVersion;
 FROM Memory IMPORT CARD8, StrPtr, Node, NodePtr, List, AllocMem, FreeMem,
  StrLength, CopyStr, InitList, First, Tail, Next, AddTail, Remove, Empty;
 FROM Checks IMPORT Check, CheckMem, CheckMemBool, AddTermProc;
 FROM Files IMPORT FilePtr, OpenFile, ReadFileBytes, CloseFile, AccessFlags,
  AccessFlagSet, noFile;
 IMPORT R;

 CONST
  StringsFile = "Strings";

 TYPE
  Language = RECORD
   node: Node;
   name: StrPtr;
  END;
  LanguagePtr = POINTER TO Language;
  String = RECORD
   node: Node;
   original, translation: StrPtr;
  END;
  StringPtr = POINTER TO String;

 VAR
  languageList, stringList: List;


 PROCEDURE GetLanguageName(num: CARD8): StrPtr;
  VAR
   cur{R.A3}, tail{R.A2}: LanguagePtr;
   n{R.D7}: CARD8;
 BEGIN
  n:= num;
  IF n = 0 THEN
   RETURN ADR("English")
  ELSE
   IF Empty(languageList) THEN SetLanguage(255) END;
   cur:= First(languageList);
   tail:= Tail(languageList);
   WHILE (cur <> tail) AND (n > 0) DO
    cur:= Next(cur^.node); DEC(n)
   END;
   IF cur <> tail THEN
    RETURN cur^.name
   ELSE
    RETURN NIL
   END
  END
 END GetLanguageName;

 PROCEDURE FreeData;
  VAR
   l: LanguagePtr;
   s: StringPtr;
 BEGIN
  WHILE NOT Empty(languageList) DO
   l:= First(languageList);
   Remove(l^.node);
   FreeMem(l^.name);
   FreeMem(l)
  END;
  WHILE NOT Empty(stringList) DO
   s:= First(stringList);
   Remove(s^.node);
   FreeMem(s^.original);
   FreeMem(s^.translation);
   FreeMem(s)
  END;
  language:= 0
 END FreeData;

 VAR
  file: FilePtr;

 PROCEDURE SetLanguage(num: CARD8);
  VAR
   lang{R.D7}: LanguagePtr;
   string{R.A3}: StringPtr;
   cnt{R.D6}, tt: CARDINAL;
   data: ARRAY[0..79] OF CHAR;
   ch: CHAR;

  PROCEDURE ReadString(): BOOLEAN;
   VAR
    c{R.D5}: CARDINAL;
  BEGIN
   c:= 0;
   REPEAT
    IF ReadFileBytes(file, ADR(ch), 1) < 1 THEN RETURN FALSE END;
    IF NOT((ch = 12C) AND (c = 0)) THEN
     data[c]:= ch; INC(c)
    ELSE
     ch:= 0C
    END
   UNTIL (ch = "|") OR (ch = 12C) OR (ch = 15C);
   data[c - 1]:= 0C;
   RETURN TRUE
  END ReadString;

 BEGIN
  FreeData;
  IF num = 0 THEN RETURN END;
  file:= OpenFile(ADR(StringsFile), AccessFlagSet{accessRead});
  IF file <> NIL THEN
   tt:= 0;
   REPEAT
    IF NOT ReadString() THEN CloseFile(file); RETURN END;
    lang:= AllocMem(SIZE(Language));
    CheckMem(lang);
    AddTail(languageList, lang^.node);
    lang^.name:= AllocMem(StrLength(ADR(data)) + 1);
    CheckMem(lang^.name);
    CopyStr(ADR(data), lang^.name, 80);
    INC(tt)
   UNTIL (ch = 12C) OR (ch = 15C);
   IF num >= tt THEN CloseFile(file); RETURN END;
   WHILE ReadString() DO
    string:= AllocMem(SIZE(String));
    CheckMem(string);
    AddTail(stringList, string^.node);
    string^.original:= AllocMem(StrLength(ADR(data)) + 1);
    CheckMem(string^.original);
    CopyStr(ADR(data), string^.original, 80);
    cnt:= 1;
    REPEAT
     IF ReadString() THEN
      IF cnt = num THEN
       string^.translation:= AllocMem(StrLength(ADR(data)) + 1);
       CheckMem(string^.translation);
       CopyStr(ADR(data), string^.translation, 80)
      END
     END;
     INC(cnt)
    UNTIL cnt >= tt
   END;
   CloseFile(file);
   language:= num
  END
 END SetLanguage;

 PROCEDURE GetDefaultLanguage;
  VAR
   locale: LocalePtr;
   cur, tail: LanguagePtr;
   s1{R.D7}, s2{R.D6}: StrPtr;
   c: CARDINAL;
   num, dflt: CARD8;
   ch1{R.D5}, ch2{R.D4}: CHAR;
   ok: BOOLEAN;
 BEGIN
  dflt:= 0;
  IF execVersion < 36 THEN RETURN END;
  localeBase:= ADDRESS(OpenLibrary(ADR(localeName), 38));
  IF localeBase <> NIL THEN
   locale:= OpenLocale(NIL);
   IF locale <> NIL THEN
    SetLanguage(255); c:= 0;
    LOOP
     IF locale^.prefLanguages[c] = NIL THEN EXIT END;
     cur:= First(languageList);
     tail:= Tail(languageList);
     num:= 0;
     WHILE (cur <> tail) DO
      s1:= cur^.name;
      s2:= ADDRESS(locale^.prefLanguages[c]);
      ok:= TRUE;
      LOOP
       ch1:= s1^; ch2:= s2^;
       IF ch1 = 0C THEN EXIT END;
       IF (ch1 >= "a") AND (ch1 <= "z") THEN ch1:= CHR(ORD(ch1) - 32) END;
       IF (ch2 >= "a") AND (ch2 <= "z") THEN ch2:= CHR(ORD(ch2) - 32) END;
       IF ch1 <> ch2 THEN ok:= FALSE END;
       INC(s1); INC(s2)
      END;
      IF ok THEN dflt:= num; EXIT END;
      cur:= Next(cur^.node); INC(num)
     END;
     INC(c);
     IF c > 9 THEN EXIT END
    END;
    CloseLocale(locale)
   END;
   CloseLibrary(ADR(localeBase^.libNode));
   localeBase:= NIL;
   FreeData
  END;
  SetLanguage(dflt)
 END GetDefaultLanguage;

 PROCEDURE Compare(s1{R.A0}, s2{R.A1}: StrPtr): BOOLEAN;
  VAR
   ch2{R.D1}: CHAR;
 BEGIN
  LOOP
   ch2:= s2^;
   IF ch2 <> s1^ THEN RETURN FALSE END;
   IF ch2 = 0C THEN RETURN TRUE END;
   INC(s1); INC(s2)
  END
 END Compare;

 PROCEDURE ADL(s: ARRAY OF CHAR): ADDRESS;
  VAR
   cur{R.A3}, tail{R.A2}: StringPtr;
  (*$ CopyDyn:= FALSE *)
 BEGIN
  cur:= First(stringList);
  tail:= Tail(stringList);
  WHILE cur <> tail DO
   IF Compare(ADR(s), cur^.original) THEN
    RETURN cur^.translation
   END;
   cur:= Next(cur^.node)
  END;
  RETURN ADR(s)
 END ADL;

 PROCEDURE Close;
 BEGIN
  FreeData;
  CloseFile(file)
 END Close;

BEGIN

 InitList(languageList);
 InitList(stringList);
 AddTermProc(Close);
 GetDefaultLanguage;

END Languages.
