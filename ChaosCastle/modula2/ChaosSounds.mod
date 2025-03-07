IMPLEMENTATION MODULE ChaosSounds;

 FROM SYSTEM IMPORT ADR, ADDRESS;
 FROM Checks IMPORT CheckMem, CheckMemBool, Check, Warn, AddTermProc;
 FROM Memory IMPORT CARD8, INT8, CARD16, INT16, CARD32, INT32, YES, NO,
  TAG1, TAG2, TAG3, TAG4, TAG5, TAG6, TAG7, FreeMem, TagItem, TagItemPtr,
  StrPtr, StrLength, tagMore, ADS;
 FROM Languages IMPORT ADL;
 FROM Files IMPORT FilePtr, OpenFile, FileLength, ReadFileBytes, CloseFile,
  noFile, AccessFlags, AccessFlagSet, FileErrorMsg;
 FROM Trigo IMPORT SIN, SQRT, RND;
 FROM Sounds IMPORT sFM, sAM, sSTEREO, sPRI, sWAVE, sLENGTH, sDISTANCE, sFREQ,
  sRATE, sNOTE, sVOLUME, AllocChannel, FreeChannel, noChan, SndDo, SndQueue,
  AllocWave, ConvertWave, FreeWave, SndFinish, SndGet, ChannelPtr, sWATER,
  sOFFSET;
 FROM Dialogs IMPORT GadgetPtr, DialogOk, dFLAGS, dRFLAGS, dfBORDER, dfCLOSE,
  dTEXT, dFILL,  dDialog, dProgress, CreateGadget, AddNewGadget, ModifyGadget,
  RefreshGadget, DeepFreeGadget, noGadget, dfJUSTIFY;
 FROM ChaosBase IMPORT Frac, Period, Obj, ObjPtr, mainPlayer, file, d, Anims,
  water;


 CONST
  SamplesFile = "AllSamples";

 VAR
  chans: ARRAY[0..15] OF ChannelPtr;
  nbChans: CARD16;


 PROCEDURE FlushWave(VAR sound: Sound);
 BEGIN
  FreeWave(sound.wave);
  sound.size:= 0
 END FlushWave;

 PROCEDURE SwitchSoundOn(): BOOLEAN;
  VAR
   progress: GadgetPtr;
   c: INT16;
   fill: CARD16;
   sndCnt: SoundList;
   loadOk: BOOLEAN;

  PROCEDURE LoadWave(VAR sound: Sound; size: CARD16);
   VAR
    res: INT32;
  BEGIN
   IF NOT(loadOk) THEN RETURN END;
   sound.size:= size;
   sound.wave:= AllocWave(sound.size);
   IF sound.wave = NIL THEN
    SwitchSoundOff;
    Warn(TRUE, ADL("Not enough memory"), ADL("to load sounds"));
    loadOk:= FALSE; RETURN
   END;
   res:= ReadFileBytes(file, sound.wave, sound.size);
   IF res <= 0 THEN
    Warn(TRUE, ADS(SamplesFile), FileErrorMsg());
    loadOk:= FALSE
   END;
   INC(fill, 2849);
   IF progress <> noGadget THEN ModifyGadget(progress, TAG1(dFILL, fill)) END;
   sound.offset:= 0;
   sound.rate:= 8363
  END LoadWave;

  CONST
   DTitle = "Loading sounds";
  VAR
   dTitle: ADDRESS;

 BEGIN
  SwitchSoundOff;
  IF NOT(sound) THEN RETURN TRUE END;
  loadOk:= TRUE;
  fill:= 8;
  dTitle:= ADL(DTitle);
  d:= CreateGadget(dDialog, TAG2(dTEXT, dTitle, dRFLAGS, dfCLOSE));
  progress:= noGadget;
  IF d <> noGadget THEN
   progress:= AddNewGadget(d, dProgress, TAG3(dRFLAGS, dfBORDER, dTEXT, ADS(""), dFLAGS, dfJUSTIFY));
   IF (progress = noGadget) OR (RefreshGadget(d) <> DialogOk) THEN DeepFreeGadget(d) END
  END;
  file:= OpenFile(ADS(SamplesFile), AccessFlagSet{accessRead});
  IF file = noFile THEN
   Warn(TRUE, ADS(SamplesFile), FileErrorMsg());
   loadOk:= FALSE
  END;
 (* Samples *)
  LoadWave(soundList[aCrash], 1408);
  LoadWave(soundList[aPanflute], 4234);
  LoadWave(soundList[wCrash], 3492);
  LoadWave(soundList[wPanflute], 8642);
  LoadWave(soundList[wJans], 10864);
  LoadWave(soundList[wShakuhachi], 4004);
  LoadWave(soundList[wWhite], 2162);
  LoadWave(soundList[wNoise], 4326);
  LoadWave(soundList[sCaisse], 3928);
  LoadWave(soundList[sCannon], 15916);
  LoadWave(soundList[sCasserole], 15218);
  LoadWave(soundList[sCymbale], 1282);
  LoadWave(soundList[sGong], 23512);
  LoadWave(soundList[sGun], 438);
  LoadWave(soundList[sHa], 2686);
  LoadWave(soundList[sHHat], 2068);
  LoadWave(soundList[sHurryUp], 8574);
  LoadWave(soundList[sLaser], 1522);
  LoadWave(soundList[sMissile], 2788);
  LoadWave(soundList[sMoney], 2904);
  LoadWave(soundList[sPoubelle], 3596);
  LoadWave(soundList[sPouf], 14480);
  LoadWave(soundList[sVerre], 8166);
  CloseFile(file);
  IF NOT(loadOk) THEN DeepFreeGadget(d); RETURN FALSE END;
  soundList[aPanflute].rate:= 16726;
  soundList[wPanflute].rate:= 16726;
  WITH soundList[wVoice] DO
   wave:= soundList[wJans].wave; offset:= 2416;
   size:= 4216; rate:= 8363
  END;
  FOR sndCnt:= MIN(SoundList) TO MAX(SoundList) DO
   IF sndCnt <> wVoice THEN
    ConvertWave(soundList[sndCnt].wave, soundList[sndCnt].size)
   END
  END;
  IF nbChannel > 16 THEN nbChannel:= 16 END;
  nbChans:= nbChannel;
  c:= nbChannel;
  WHILE loadOk AND (c > 0) DO
   DEC(c);
   chans[c]:= AllocChannel(TAG1(sSTEREO, ORD(stereo)));
   IF chans[c] = noChan THEN
    Warn(TRUE, ADL("Unable to allocate"), ADL("sound channels"));
    loadOk:= FALSE
   END;
   WITH channel[c] DO
    sndWave:= NIL;
    sndObj:= NIL;
    sndPri:= MIN(INT8)
   END
  END;
  DeepFreeGadget(d);
  IF loadOk THEN
   SimpleSound(soundList[sCaisse])
  END;
  RETURN loadOk
 END SwitchSoundOn;

 PROCEDURE SwitchSoundOff;
  VAR
   sound: SoundList;
   c: INT16;
 BEGIN
  WHILE nbChans > 0 DO
   DEC(nbChans);
   IF chans[nbChans] <> noChan THEN FreeChannel(chans[nbChans]) END
  END;
  WITH soundList[wVoice] DO wave:= NIL; size:= 0 END;
  FOR sound:= MIN(SoundList) TO MAX(SoundList) DO
   FlushWave(soundList[sound])
  END;
  FOR c:= 0 TO 15 DO
   channel[c].sndObj:= NIL
  END
 END SwitchSoundOff;

 PROCEDURE SetEffect(VAR effect: Effect; VAR sound: Sound; delay, rate: CARD16;
                     volume: CARD8; pri: INT16);
 BEGIN
  effect.sound:= ADR(sound);
  effect.delay:= delay;
  effect.rate:= rate;
  effect.volume:= volume;
  effect.pri:= pri
 END SetEffect;

 VAR
  stereoEffect: CARD8;

 PROCEDURE StereoEffect;
 BEGIN
  IF stereo THEN stereoEffect:= 2 END
 END StereoEffect;

 PROCEDURE SimpleSound(sound: Sound);
 BEGIN
  IF nbChans = 0 THEN RETURN END;
  WITH sound DO
   SndDo(chans[0], TAG2(sPRI, 0, sWATER, 0));
   SndDo(chans[0], TAG7(sWAVE, wave, sLENGTH, size, sVOLUME, 64, sRATE, rate,
    sSTEREO, 0, sAM, 128, sFM, 256))
  END
 END SimpleSound;

 VAR
  plStereo: INT16;

 PROCEDURE ModulateSound(chan: CARD16; tags: ADDRESS);
  CONST
   AirSpeed = 426; (* (1000 * Frac DIV Period) DIV 32 *)
    (* 1000 pixels / sec *)
   WaterSpeed = 1843; (* (4320 * Frac DIV Period) DIV 32 *)
    (* 4320 pixels / sec *)
  VAR
   tagGadget: TagItem;
   dx, dy, dist: CARD32;
   px, pl: INT32;
   rx, ry, rl, ov, pv: INT16;
   am, fm, os, ps: CARD16;
   delay: CARD32;
   stereo: INT16;
 BEGIN
  WITH channel[chan] DO
   IF (tags = NIL) AND SndFinish(chans[chan]) THEN
    sndWave:= NIL;
    sndObj:= NIL;
    sndPri:= MIN(INT8);
    RETURN
   END;
   IF sndObj = NIL THEN RETURN END;
  (* Volume *)
   rx:= (sndObj^.x - mainPlayer^.x) DIV Frac; INC(rx, sndObj^.cx - mainPlayer^.cx);
   ry:= (sndObj^.y - mainPlayer^.y) DIV Frac; INC(ry, sndObj^.cy - mainPlayer^.cy);
   px:= rx; dx:= ABS(rx); dy:= ABS(ry);
   dist:= SQRT(dx * dx + dy * dy); pl:= dist; (* dist in pixels *)
   IF dist >= 256 THEN am:= 0 ELSE am:= 128 - (dist DIV 2) END;
   IF am > 0 THEN
   (* Stereo *)
    IF (stereoEffect > 0) AND (tags <> NIL) THEN
     DEC(stereoEffect);
     IF plStereo = -181 THEN plStereo:= 181 ELSE plStereo:= -181 END;
     IF nbChans > 1 THEN stereo:= plStereo ELSE stereo:= 0 END;
    ELSIF (tags = NIL) AND (sndObj^.kind = PLAYER) THEN
     tagGadget.tag:= sSTEREO;
     SndGet(chans[chan], tagGadget);
     stereo:= tagGadget.lint
    ELSIF pl <> 0 THEN
     stereo:= (px * 90) DIV pl; (* Cos(a) = adj/hyp *)
     IF ry > 0 THEN (* rear => surround *)
      IF stereo > 0 THEN stereo:= 180 - stereo ELSE stereo:= -180 - stereo END
     END
    ELSE
     stereo:= 0
    END;
   (* Frequence (Doppler effect) *)
    IF (dx > 127) OR (dy > 127) THEN rx:= rx DIV 2; ry:= ry DIV 2 END;
    rl:= SQRT(rx * rx + ry * ry);
    IF rl <> 0 THEN
     ov:= -((sndObj^.vx DIV 32) * rx + (sndObj^.vy DIV 32) * ry) DIV rl;
     pv:= ((mainPlayer^.vx DIV 32) * rx + (mainPlayer^.vy DIV 32) * ry) DIV rl;
     IF water THEN
      os:= WaterSpeed - ov; ps:= WaterSpeed + pv;
      fm:= ((ps * 32) DIV os) * 8
     ELSE
      os:= AirSpeed - ov; ps:= AirSpeed + pv;
      fm:= ((ps * 64) DIV os) * 4
     END
    ELSE
     fm:= 256
    END
   ELSE
    fm:= 256; stereo:= 0
   END;
   IF dist < 512 THEN delay:= dist * 64 ELSE delay:= 32767 END;
   IF tags <> NIL THEN
    SndDo(chans[chan], TAG6(sDISTANCE, delay, sAM, am, sFM, fm,
           sSTEREO, stereo, sWATER, ORD(water) * 100, tagMore, tags))
   ELSE
    SndDo(chans[chan], TAG4(sDISTANCE, delay, sAM, am, sFM, fm, sSTEREO, stereo))
   END
  END
 END ModulateSound;

 PROCEDURE ModulateSounds;
  VAR
   chan: CARD16;
 BEGIN
  chan:= nbChans;
  WHILE chan > 0 DO
   DEC(chan);
   ModulateSound(chan, NIL)
  END
 END ModulateSounds;

 PROCEDURE GetChan(VAR chan: CARD16; pri: INT16; wave: ADDRESS; obj: ObjPtr): BOOLEAN;
  VAR
   rx, ry: INT32;
   dx, dy: CARD32;
   dl: CARD16;
   boost: INT16;
 BEGIN
  rx:= (obj^.x - mainPlayer^.x) DIV Frac; INC(rx, obj^.cx - mainPlayer^.cx);
  ry:= (obj^.y - mainPlayer^.y) DIV Frac; INC(ry, obj^.cy - mainPlayer^.cy);
  dx:= ABS(rx); dy:= ABS(ry);
  dl:= SQRT(dx * dx + dy * dy);
  IF dl >= 256 THEN boost:= 0 ELSE boost:= 128 - (dl DIV 2) END;
  pri:= (pri * boost + 127) DIV 128;
  chan:= nbChans;
  LOOP
   IF chan = 0 THEN
    chan:= nbChans;
    REPEAT
     IF chan = 0 THEN RETURN FALSE END;
     DEC(chan)
    UNTIL channel[chan].sndPri <= pri;
    EXIT
   END;
   DEC(chan);
   IF channel[chan].sndPri = MIN(INT8) THEN EXIT END
  END;
  WITH channel[chan] DO
   sndPri:= pri; boostPri:= boost;
   sndWave:= wave;
   sndObj:= obj
  END;
  RETURN TRUE
 END GetChan;

 PROCEDURE SoundEffect(obj: ObjPtr; VAR effects: ARRAY OF Effect);
  VAR
   wave: ADDRESS;
   tags: TagItemPtr;
   offset: CARD16;
   bytes, length, size, chan, lrate, rrate: CARD16;
   k: INT16;
   first: BOOLEAN;
 BEGIN
  IF GetChan(chan, effects[0].pri, effects[0].sound^.wave, obj) THEN
   first:= TRUE;
   FOR k:= 0 TO HIGH(effects) DO
    WITH effects[k] DO
     IF sound^.wave <> NIL THEN
      wave:= sound^.wave; size:= sound^.size; offset:= 0; lrate:= sound^.rate
     END;
     IF rate = 0 THEN rrate:= lrate ELSE rrate:= rate END;
     IF water THEN
      rrate:= rrate * 2 DIV 3;
      IF delay = 0 THEN bytes:= size ELSE bytes:= delay * 2 DIV 3 END
     ELSE
      IF delay = 0 THEN bytes:= size ELSE bytes:= delay END
     END;
     LOOP
      length:= size - offset;
      IF bytes < length THEN length:= bytes END;
      tags:= TAG6(sWAVE, wave, sLENGTH, length, sOFFSET, offset + sound^.offset,
                  sVOLUME, volume, sRATE, rrate,
                  sPRI, (pri * channel[chan].boostPri + 127) DIV 128);
      IF first THEN
       ModulateSound(chan, tags);
       first:= FALSE
      ELSE
       SndQueue(chans[chan], tags)
      END;
      DEC(bytes, length);
      IF bytes = 0 THEN EXIT END;
      INC(offset, length);
      IF offset >= size THEN DEC(offset, size) END
     END
    END
   END
  END
 END SoundEffect;

 PROCEDURE Clear;
  VAR
   s: SoundList;
   c: CARD16;
 BEGIN
  FOR c:= 0 TO 15 DO chans[c]:= noChan END;
  FOR s:= MIN(SoundList) TO MAX(SoundList) DO
   WITH soundList[s] DO wave:= NIL; size:= 0 END
  END
 END Clear;

BEGIN

 Clear;
 stereoEffect:= 0;
 nulSound.wave:= NIL;
 nbChans:= 0;
 AddTermProc(SwitchSoundOff);

END ChaosSounds.
