IMPLEMENTATION MODULE GrotteSounds;
  (*$ OverflowChk:= FALSE *)
 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32, TAG1, TAG2,
  TAG3, TAG4, TAG5, TAG6, TAG7, TagItem, TagItemPtr, YES, NO, ADS;
 FROM Checks IMPORT Warn, AddTermProc;
 FROM Trigo IMPORT SQRT;
 FROM GrotteSupport IMPORT OBJECT;
 FROM Sounds IMPORT ChannelPtr, noChan, sLENGTH, sWAVE, sRATE, sVOLUME,
  sSTEREO, GetSoundsSysAttr, AllocChannel, SndDo, SndQueue, FreeChannel,
  AllocWave, ConvertWave, FreeWave, sNUMCHANS, SndFinish, sPRI, sBALANCE;
 FROM Files IMPORT FilePtr, AccessFlags, AccessFlagSet, noFile, OpenFile,
  ReadFileBytes, CloseFile;


 TYPE
  SoundList = (CrashA, Crash, Cymbales, Gong, Canon1, Canon2, Hit1, Hit2, HHat,
   Panflute, Water, Pic, Noise, Clock);
  Snd = RECORD
   wave: ADDRESS;
   size: CARD32;
   rate: CARD16;
   pri: CARD16;
  END;
  SndPtr = POINTER TO Snd;
  Effect = RECORD
   sound: SndPtr;
   length, freq: CARD16;
   volume: CARD16;
  END;
  Channel = RECORD
   chan: ChannelPtr;
   chanWave: ADDRESS;
   chanVolume: INT16;
   chanStereo: INT16;
  END;

 VAR
  channel: ARRAY[0..15] OF Channel;
  nbChan: CARD16;
  soundList: ARRAY[MIN(SoundList)..MAX(SoundList)] OF Snd;
  highGong, lowCannon, low2Cannon, blvEffect,
   popupEffect, bonus1Effect: ARRAY[0..0] OF Effect;
  ptEffect, pt2Effect: ARRAY[0..4] OF Effect;
  bonus2Effect: ARRAY[0..14] OF Effect;
  createEffect: ARRAY[0..2] OF Effect;
  fireEffect: ARRAY[0..1] OF Effect;
  dieEffect2: ARRAY[0..0] OF Effect;
  dieEffect1: ARRAY[0..30] OF Effect;
  aieEffect: ARRAY[0..5] OF Effect;
  atEffect: ARRAY[0..5] OF Effect;
  dieNestEffect: ARRAY[0..23] OF Effect;
  bumper1Effect: ARRAY[0..2] OF Effect;
  bumperEffect: ARRAY[0..15] OF Effect;
  turretEffect: ARRAY[0..15] OF Effect;
  metalEffect: ARRAY[0..11] OF Effect;
  picEffect: ARRAY[0..3] OF Effect;
  gameEffect: ARRAY[0..25] OF Effect;

 PROCEDURE InitEffects;

  PROCEDURE SetEffect(VAR e: Effect; sound: SoundList;
                      time, freq: CARD16; volume: CARD8);
  BEGIN
   e.sound:= ADR(soundList[sound]);
   IF time <> 0 THEN
    e.length:= (soundList[sound].rate * freq DIV 440) * time DIV 1000
   ELSE
    e.length:= soundList[sound].size
   END;
   e.freq:= freq;
   e.volume:= volume
  END SetEffect;

  VAR
   c, v, f, r1, r2: INT16;
   g: CARD16;
 BEGIN
   (* ptEffect *)
  SetEffect(ptEffect[0], Clock, 50, 880, 255);
  SetEffect(pt2Effect[0], Clock, 50, 587, 255);
  FOR c:= 0 TO 3 DO
   SetEffect(ptEffect[c + 1], Clock, 0, 880, (4 - c) * 24);
   SetEffect(pt2Effect[c + 1], Clock, 0, 587, (4 - c) * 24)
  END;
   (* dieEffect *)
  SetEffect(dieEffect1[0], HHat, 0, 110, 255);
  FOR c:= 1 TO 6 DO
   SetEffect(dieEffect1[c], Noise, 0, 990, c * c * 4)
  END;
  SetEffect(dieEffect1[7], Panflute, 100, 329, 10);
  SetEffect(dieEffect1[8], Panflute, 100, 554, 20);
  SetEffect(dieEffect1[9], Panflute, 150, 370, 30);
  SetEffect(dieEffect1[10], Panflute, 100, 494, 40);
  SetEffect(dieEffect1[11], Panflute, 120, 587, 50);
  SetEffect(dieEffect1[12], Panflute, 100, 415, 60);
  SetEffect(dieEffect1[13], Panflute, 100, 423, 70);
  SetEffect(dieEffect1[14], Panflute, 100, 440, 70);
  SetEffect(dieEffect1[15], Panflute, 140, 368, 70);
  SetEffect(dieEffect1[16], Panflute, 120, 495, 70);
  SetEffect(dieEffect1[17], Panflute, 120, 395, 70);
  SetEffect(dieEffect1[18], Panflute, 100, 329, 70);
  SetEffect(dieEffect1[19], Panflute, 100, 289, 70);
  SetEffect(dieEffect1[20], Panflute, 130, 439, 70);
  SetEffect(dieEffect1[21], Panflute, 100, 552, 70);
  SetEffect(dieEffect1[22], Panflute, 120, 658, 70);
  SetEffect(dieEffect1[23], Panflute, 100, 418, 70);
  SetEffect(dieEffect1[24], Panflute, 100, 492, 70);
  SetEffect(dieEffect1[25], Panflute, 100, 587, 60);
  SetEffect(dieEffect1[26], Panflute, 150, 740, 50);
  SetEffect(dieEffect1[27], Panflute, 100, 440, 40);
  SetEffect(dieEffect1[28], Panflute, 120, 581, 30);
  SetEffect(dieEffect1[29], Panflute, 100, 389, 20);
  SetEffect(dieEffect1[30], Panflute, 100, 342, 10);
  SetEffect(dieEffect2[0], Gong, 0, 220, 255);
   (* aieEffect *)
  SetEffect(aieEffect[0], CrashA, 0, 440, 250);
  FOR c:= 1 TO 5 DO
   SetEffect(aieEffect[c], Crash, 0, 440, (6 - c) * (6 - c) * 10)
  END;
   (* atEffect *)
  SetEffect(atEffect[0], CrashA, 0, 220, 250);
  FOR c:= 1 TO 5 DO
   SetEffect(atEffect[c], Crash, 0, 220, (6 - c) * (6 - c) * 10)
  END;
   (* highGong *)
  SetEffect(highGong[0], Gong, 0, 880, 230);
   (* lowCannon *)
  SetEffect(lowCannon[0], Canon1, 0, 220, 255);
  SetEffect(low2Cannon[0], Canon1, 0, 230, 255);
   (* blvEffect *)
  SetEffect(blvEffect[0], Water, 0, 110, 255);
   (* dieNestEffect *)
  FOR c:= 0 TO 23 DO
   v:= ABS(c MOD 4 - 2) + 1;
   SetEffect(dieNestEffect[c], Noise, 84 - v * 14, 440 + v * 147, (24 - c) * v * 7 DIV 2)
  END;
   (* bonus0Effect *)
  SetEffect(bonus1Effect[0], HHat, 0, 220, 255);
  f:= 440;
  FOR c:= 0 TO 14 DO
   v:= ((16 - c) * 16 - 1) * ((c + 1) MOD 2);
   SetEffect(bonus2Effect[c], Panflute, 30, f, v);
   DEC(f, 5)
  END;
   (* bumper effect *)
  FOR c:= 0 TO 15 DO
   f:= (c MOD 4);
   v:= (16 - c) * 16 - 1; IF f = 3 THEN v:= 0 END;
   IF f = 0 THEN f:= 880 ELSIF f = 1 THEN f:= 440 ELSE f:= 220 END;
   SetEffect(bumperEffect[c], Noise, 50, f, v)
  END;
   (* bumper1 effect *)
  SetEffect(bumper1Effect[0], Noise, 70, 880, 80);
  SetEffect(bumper1Effect[1], Noise, 70, 440, 80);
  SetEffect(bumper1Effect[2], Noise, 70, 220, 80);
   (* create effect *)
  SetEffect(createEffect[0], Clock, 100, 131, 250);
  SetEffect(createEffect[1], Clock, 100, 197, 250);
  SetEffect(createEffect[2], Clock, 100, 164, 250);
   (* fire Effect *)
  SetEffect(fireEffect[0], Clock, 80, 131, 250);
  SetEffect(fireEffect[1], Clock, 80, 262, 250);
   (* popup effect *)
  SetEffect(popupEffect[0], Cymbales, 0, 220, 150);
   (* turretEffect *)
  r1:= 0; r2:= 0;
  FOR c:= 0 TO 15 DO
   r1:= (r1 * 17 + 5) MOD 8;
   r2:= (r2 * 13 + 9) MOD 16;
   v:= (16 - c) * (r1 + 8);
   SetEffect(turretEffect[c], HHat, 55 + r2, 554, v)
  END;
   (* gameEffect *)
  g:= 220; v:= 255;
  SetEffect(gameEffect[0], Panflute, 500, 440, 0);
  SetEffect(gameEffect[1], Pic, 0, 440, 255);
  FOR c:= 2 TO 25 DO
   SetEffect(gameEffect[c], Panflute, 25, g, (26 - c) * 10);
   g:= g * 3118 DIV 2943
  END;
   (* picEffect *)
  FOR c:= 0 TO 3 DO
   SetEffect(picEffect[c], Pic, 0, 440, (4 - c) * 64 - 1)
  END;
   (* metalEffect *)
  FOR c:= 0 TO 11 DO
   SetEffect(metalEffect[c], HHat, 40, 440, (12 - c) * 12)
  END
 END InitEffects;

 PROCEDURE InitSounds;
  CONST
   SamplesFile = "Samples";
  VAR
   f: FilePtr;
   ok: BOOLEAN;
   s: SoundList;

  PROCEDURE LoadSound(which: SoundList; ssize: INT32; srate, spri: CARD16);
  BEGIN
   IF ok THEN
    WITH soundList[which] DO
     size:= ssize; rate:= srate; pri:= spri;
     wave:= AllocWave(size);
     IF wave <> NIL THEN
      IF ReadFileBytes(f, wave, ssize) <> ssize THEN ok:= FALSE END
     END
    END
   END
  END LoadSound;

  TYPE
   SynthWave = ARRAY[0..25739] OF CARD8;
   SynthWavePtr = POINTER TO SynthWave;
  VAR
   attr: TagItem;
   synth: SynthWavePtr;
   c, d: CARD16;
   i, v: INT16;
   stereo: BOOLEAN;

 BEGIN
  attr.tag:= sNUMCHANS;
  GetSoundsSysAttr(attr);
  IF attr.data = 0 THEN RETURN END;
  ok:= TRUE;
  f:= OpenFile(ADR(SamplesFile), AccessFlagSet{accessRead});
  Warn(f = noFile, ADS("Cannot open file"), ADR(SamplesFile));
  IF f <> noFile THEN
   LoadSound(CrashA, 1408, 8363, 3);
   LoadSound(Crash, 3492, 8363, 2);
   LoadSound(Cymbales, 1282, 8363, 1);
   LoadSound(Gong, 23512, 8363, 4);
   LoadSound(Canon1, 12222, 8363, 3);
   LoadSound(Canon2, 14896, 16726, 4);
   LoadSound(Hit1, 1128, 8363, 1);
   LoadSound(Hit2, 784, 8363, 1);
   LoadSound(HHat, 2068, 8363, 2);
   LoadSound(Panflute, 8642, 16726, 4);
   LoadSound(Water, 10748, 16726, 4);
   LoadSound(Pic, 248, 8363, 2);
   LoadSound(Noise, 4326, 8363, 4);
     (* Init Clock *)
   synth:= AllocWave(4096);
   IF synth = NIL THEN
    ok:= FALSE
   ELSE
    FOR c:= 0 TO 7 DO
     i:= c; DEC(i, 3);
     v:= SQRT(16 - i * i) * 8;
     IF v > 127 THEN v:= 127 END;
     FOR d:= 0 TO 255 DO
      synth^[d * 16 + c]:= v; synth^[d * 16 + c + 8]:= 255 - v
     END
    END;
    WITH soundList[Clock] DO
     wave:= synth; size:= 4096;
     rate:= 14080; pri:= 3
    END
   END;
   IF ok THEN
    FOR s:= MIN(SoundList) TO MAX(SoundList) DO
     WITH soundList[s] DO
      IF wave <> NIL THEN ConvertWave(wave, size) END
     END
    END;
     (* Allocate channels *)
    attr.tag:= sNUMCHANS;
    GetSoundsSysAttr(attr);
    c:= attr.data;
    attr.tag:= sSTEREO;
    GetSoundsSysAttr(attr);
    stereo:= (attr.data <> 0);
    IF c > 16 THEN c:= 16 END;
    WHILE (c > 0) AND ok DO
     WITH channel[nbChan] DO
      chan:= AllocChannel(TAG1(sSTEREO, ORD(stereo)));
      IF chan = noChan THEN ok:= FALSE END
     END;
     DEC(c); INC(nbChan)
    END;
    Warn(NOT ok, ADS("Unable to allocate sound channels"), NIL);
    IF NOT ok THEN
     FlushSounds
    ELSE
     InitEffects
    END
   ELSE
    FlushSounds;
    Warn(TRUE, ADS("Error while reading file"), ADR(SamplesFile));
   END;
   CloseFile(f)
  END
 END InitSounds;

 PROCEDURE FlushSounds;
  VAR
   s: SoundList;
 BEGIN
  WHILE nbChan > 0 DO
   DEC(nbChan);
   WITH channel[nbChan] DO
    FreeChannel(chan)
   END
  END;
  FOR s:= MIN(SoundList) TO MAX(SoundList) DO
   WITH soundList[s] DO
    IF wave <> NIL THEN FreeWave(wave) END
   END
  END
 END FlushSounds;

 PROCEDURE BestChan(w: ADDRESS; volume: INT16; stereo: INT16): INT16;
  VAR
   bc, c: INT16;
   best, score: INT16;
 BEGIN
  bc:= -1; best:= 0;
  c:= nbChan;
  WHILE c > 0 DO
   DEC(c);
   WITH channel[c] DO
    IF SndFinish(chan) THEN chanWave:= NIL END;
    IF chanWave = NIL THEN
     score:= 255
    ELSIF chanWave = w THEN
     score:= 320 - ABS(volume - chanVolume) - ABS(stereo - chanStereo) * 2
    ELSE
     score:= volume - chanVolume
    END;
    IF score >= best THEN best:= score; bc:= c END
   END
  END;
  IF bc <> -1 THEN
   WITH channel[bc] DO
    chanWave:= w;
    chanVolume:= volume;
    chanStereo:= stereo
   END
  END;
  RETURN bc
 END BestChan;

 PROCEDURE PlaySound(s: SoundList; volume: CARD8; stereo, balance: INT16);
  VAR
   c: INT16;
   v: CARD16;
 BEGIN
  v:= volume;
  c:= BestChan(ADR(soundList[s]), v * (soundList[s].pri + 4) DIV 8, stereo);
  IF c <> -1 THEN
   WITH soundList[s] DO
    SndDo(channel[c].chan, TAG7(sWAVE, wave, sLENGTH, size, sRATE, rate,
     sPRI, volume DIV 4, sVOLUME, volume, sSTEREO, stereo, sBALANCE, balance))
   END
  END
 END PlaySound;

 PROCEDURE QueueSound(c: CARD16; s: SoundList; freq, time: CARD16;
                      volume: CARD8; stereo: INT16);
  TYPE
   SoundSet = SET OF SoundList;
  VAR
   w: ADDRESS;
   todo, len, sz: CARD16;
   frq: CARD16;
   vol: CARD8;
 BEGIN
  IF c < nbChan THEN
   vol:= volume;
   WITH soundList[s] DO
    w:= wave; sz:= size;
    frq:= rate * freq DIV 440;
    todo:= frq * time DIV 500;
    WHILE todo > 0 DO
     IF todo >= sz THEN len:= sz ELSE len:= todo END;
     DEC(todo, len);
     SndQueue(channel[c].chan, TAG7(sWAVE, w, sLENGTH, len, sRATE, frq,
      sPRI, 16 - c, sVOLUME, vol, sSTEREO, stereo, sBALANCE, 0));
     IF s IN SoundSet{Gong, HHat, Canon1, Canon2, Pic, Water, Hit1, Hit2} THEN
      vol:= 0; w:= soundList[Gong].wave; sz:= soundList[Gong].size
     ELSIF (s = CrashA) THEN
      w:= soundList[Crash].wave; sz:= soundList[Crash].size
     END
    END
   END
  END
 END QueueSound;

 PROCEDURE PlayEffect(VAR effects: ARRAY OF Effect;
                      vol: CARD16; stereo, balance: INT16);
  VAR
   tags: TagItemPtr;
   c, e: INT16;
   v, frq, todo, len: CARD16;
   first: BOOLEAN;
 BEGIN
  IF nbChan <= 0 THEN RETURN END;
  first:= TRUE;
  c:= BestChan(ADR(effects), vol * (effects[0].sound^.pri + 4) DIV 8, stereo);
  IF c <> -1 THEN
   FOR e:= 0 TO HIGH(effects) DO
    WITH effects[e] DO
     frq:= sound^.rate * freq DIV 440;
     v:= (vol * volume + 255) DIV 256;
     todo:= length;
     WHILE todo > 0 DO
      IF todo >= sound^.size THEN
       len:= sound^.size
      ELSE
       len:= todo
      END;
      DEC(todo, len);
      tags:= TAG7(sWAVE, sound^.wave, sLENGTH, len, sRATE, frq,
       sPRI, v DIV 4, sVOLUME, v, sSTEREO, stereo, sBALANCE, balance);
      IF first THEN
       SndDo(channel[c].chan, tags);
       first:= FALSE
      ELSE
       SndQueue(channel[c].chan, tags)
      END
     END
    END
   END
  END
 END PlayEffect;

 PROCEDURE Sound(t1, t2: OBJECT; volume: CARD8; stereo, balance: INT16);
 BEGIN
  IF t2 = BN THEN
    (* Player got a bonus *)
   CASE t1 OF
     K1: PlayEffect(ptEffect, volume, stereo, balance) (* . *)
    |K0: PlayEffect(pt2Effect, volume, stereo, balance) (* % -> # *)
    |K2: PlayEffect(blvEffect, volume, stereo, balance) (* $ *)
    |K3: PlayEffect(atEffect, volume, stereo, balance) (* @ *)
    |EMPTY: PlayEffect(bonus1Effect, volume, stereo, balance) (* % *)
    |K4: PlaySound(HHat, volume, stereo, balance) (* ! *)
   END
  ELSIF t2 = EMPTY THEN
    (* Die *)
   CASE t1 OF
      PLAYER: PlayEffect(lowCannon, volume, stereo, balance);
              PlayEffect(low2Cannon, volume, stereo, balance)
     |EMPTY: PlayEffect(dieEffect1, volume, stereo, balance);
             PlayEffect(dieEffect2, volume, stereo, balance)
     |K0: PlayEffect(bumperEffect, volume, stereo, balance)
     |K1, K2, K3: PlaySound(Gong, volume, stereo, balance)
     |K4: PlaySound(Water, volume, stereo, balance)
     |GN1: PlayEffect(turretEffect, volume, stereo, balance)
     |GN2: PlayEffect(popupEffect, volume, stereo, balance)
     |DL: PlayEffect(createEffect, volume, stereo, balance)
     |L2: PlaySound(Cymbales, volume DIV 4, stereo, balance)
     |BALL: PlaySound(Water, volume DIV 2, stereo, balance)
     |PIC: PlayEffect(metalEffect, volume, stereo, balance)
     |NID, BUB: PlayEffect(dieNestEffect, volume, stereo, balance)
     |BN: PlayEffect(bonus2Effect, volume, stereo, balance)
     |BM: PlaySound(Pic, volume DIV 4, stereo, balance)
    ELSE
   END
  ELSIF t1 = EMPTY THEN
    (* Create *)
   CASE t2 OF
     L1: PlaySound(Canon1, volume DIV 2, stereo, balance)
    |L2: PlaySound(Canon2, volume DIV 3 * 2, stereo, balance)
    |L3: PlayEffect(fireEffect, volume, stereo, balance)
   ELSE
    PlayEffect(createEffect, volume DIV 2, stereo, balance)
   END
  ELSIF t1 = PLAYER THEN
    (* Player action *)
   CASE t2 OF
     GN1: PlaySound(Hit2, volume DIV 4, stereo, balance) (* jump *)
    |GN2: PlaySound(Hit1, volume DIV 4, stereo, balance) (* move *)
    |PLAYER: PlayEffect(gameEffect, volume, stereo, balance) (* start / end *)
    ELSE
   END
  ELSE
    (* Aie *)
   CASE t2 OF
      L1, L3: PlaySound(Cymbales, volume, stereo, balance)
     |K0: PlayEffect(highGong, volume, stereo, balance)
     |K1, K2, K3, K4: PlayEffect(aieEffect, volume, stereo, balance)
     |BM: PlayEffect(bumper1Effect, volume, stereo, balance)
    ELSE PlayEffect(picEffect, volume, stereo, balance)
   END
  END
 END Sound;

 PROCEDURE GameOverSound;
 BEGIN
  PlayEffect(dieEffect1, 255, 45, 45);
  PlayEffect(dieEffect2, 255, -45, -45)
 END GameOverSound;

 PROCEDURE FinalMusic;
 BEGIN
  QueueSound(3, Gong, 440, 3500, 0, -60);
  QueueSound(1, Panflute, 660, 1000, 180, 180); (* mi *)
  QueueSound(0, Gong, 660, 250, 255, 0); (* mi *)
   QueueSound(2, HHat, 330, 125, 180, 60);
   QueueSound(2, HHat, 330, 125, 180, 60);
  QueueSound(0, Gong, 524, 250, 255, 0); (* do *)
   QueueSound(2, HHat, 262, 125, 180, 60);
   QueueSound(2, HHat, 262, 125, 180, 60);
  QueueSound(0, Gong, 588, 500, 255, 0); (* re *)
   QueueSound(2, HHat, 294, 125, 180, 60);
   QueueSound(2, HHat, 294, 125, 180, 60);
   QueueSound(2, HHat, 393, 250, 180, 60);
  QueueSound(1, Panflute, 880, 500, 180, 180); (* la *)
  QueueSound(0, Gong, 880, 250, 255, 0); (* la *)
   QueueSound(2, HHat, 262, 125, 180, 60);
   QueueSound(2, HHat, 262, 125, 180, 60);
  QueueSound(0, Gong, 700, 250, 255, 0); (* fa *)
   QueueSound(2, HHat, 330, 125, 180, 60);
   QueueSound(2, HHat, 330, 125, 180, 60);
  QueueSound(0, Panflute, 785, 333, 180, 180); (* so *)
  QueueSound(1, Gong, 785, 1000, 255, 0); (* so *)
   QueueSound(2, HHat, 392, 125, 180, 60);
   QueueSound(2, HHat, 350, 208, 180, 60);
  QueueSound(0, Panflute, 524, 167, 255, 180); (* do *)
   QueueSound(2, CrashA, 330, 333, 255, 60);
  QueueSound(0, Panflute, 588, 166, 255, 180); (* re *)
  QueueSound(0, Panflute, 660, 167, 255, 180); (* mi *)
   QueueSound(2, CrashA, 330, 334, 255, 60);
  QueueSound(0, Panflute, 785, 167, 255, 180); (* so *)
  QueueSound(1, Gong, 588, 1500, 255, 0); (* re *)
  QueueSound(0, Panflute, 588, 500, 255, 180); (* re *)
  QueueSound(0, Panflute, 588, 1000, 255, 180); (* re *)
   QueueSound(2, CrashA, 294, 500, 255, 60);
   QueueSound(3, Water, 700, 125, 200, -60);
   QueueSound(3, Water, 660, 125, 200, -60);
   QueueSound(3, Water, 588, 125, 200, -60);
   QueueSound(3, Water, 524, 125, 200, -60);
   QueueSound(2, Hit1, 440, 125, 200, -120);
   QueueSound(2, Hit2, 440, 125, 200, -120);
   QueueSound(2, Hit1, 440, 750, 200, -120);

  QueueSound(1, Gong, 660, 250, 180, 180); (* mi *)
  QueueSound(0, Panflute, 660, 250, 255, 0); (* mi *)
   QueueSound(3, Water, 495, 625, 200, -60);
   QueueSound(2, HHat, 660, 125, 180, 60);
   QueueSound(2, HHat, 660, 125, 180, 60);
  QueueSound(1, Gong, 660, 250, 180, 180); (* mi *)
  QueueSound(0, Panflute, 524, 250, 255, 0); (* do *)
   QueueSound(2, HHat, 524, 125, 180, 60);
   QueueSound(2, HHat, 524, 125, 180, 60);
  QueueSound(1, Gong, 588, 500, 180, 180); (* re *)
  QueueSound(0, Panflute, 588, 500, 255, 0); (* re *)
   QueueSound(2, HHat, 588, 125, 180, 60);
   QueueSound(2, HHat, 588, 125, 180, 60);
   QueueSound(2, HHat, 393, 250, 180, 60);
   QueueSound(3, Pic, 440, 250, 240, -60);
   QueueSound(3, Pic, 440, 250, 240, -60);
  QueueSound(1, Gong, 880, 250, 180, 180); (* la *)
  QueueSound(0, Panflute, 880, 250, 255, 0); (* la *)
   QueueSound(2, HHat, 524, 125, 180, 60);
   QueueSound(2, HHat, 524, 125, 180, 60);
   QueueSound(3, Pic, 440, 250, 240, -60);
  QueueSound(1, Gong, 880, 250, 180, 180); (* la *)
  QueueSound(0, Panflute, 700, 250, 255, 0); (* fa *)
   QueueSound(2, HHat, 660, 125, 180, 60);
   QueueSound(2, HHat, 660, 125, 180, 60);
   QueueSound(3, Pic, 440, 250, 240, -60);
  QueueSound(0, Gong, 785, 333, 180, 180); (* so *)
  QueueSound(1, Panflute, 785, 1000, 255, 0); (* so *)
   QueueSound(2, HHat, 784, 125, 180, 60);
   QueueSound(2, HHat, 700, 208, 180, 60);
   QueueSound(3, Pic, 440, 250, 240, -60);
   QueueSound(3, Pic, 440, 250, 240, -60);
  QueueSound(0, Gong, 524, 167, 255, 180); (* do *)
   QueueSound(2, Canon1, 660, 333, 255, 60);
  QueueSound(0, Gong, 588, 166, 255, 180); (* re *)
  QueueSound(0, Gong, 660, 167, 255, 180); (* mi *)
   QueueSound(2, Canon1, 660, 334, 255, 60);
   QueueSound(3, Pic, 440, 250, 240, -60);
   QueueSound(3, Pic, 440, 125, 240, -60);
  QueueSound(0, Gong, 785, 167, 255, 180); (* so *)
  QueueSound(1, Panflute, 524, 500, 255, 0); (* do *)
  QueueSound(0, Gong, 440, 2000, 255, 0); (* la *)
   QueueSound(2, Canon2, 220, 2000, 200, 60);
   QueueSound(3, Water, 880, 125, 200, -60);
   QueueSound(3, Water, 110, 1500, 240, -60);
 END FinalMusic;

 PROCEDURE Init;
  VAR
   c: CARD16;
   s: SoundList;
 BEGIN
  FOR c:= 0 TO 15 DO channel[c].chan:= noChan END;
  FOR s:= MIN(SoundList) TO MAX(SoundList) DO soundList[s].wave:= NIL END;
  nbChan:= 0
 END Init;

BEGIN

 Init;
 AddTermProc(FlushSounds);
 InitSounds;

END GrotteSounds.

