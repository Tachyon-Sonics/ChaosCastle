package ch.chaos.castle;

import java.lang.Runnable;
import java.util.EnumSet;

import ch.chaos.castle.ChaosBase.Anims;
import ch.chaos.library.Checks;
import ch.chaos.library.Dialogs;
import ch.chaos.library.Files;
import ch.chaos.library.Files.AccessFlags;
import ch.chaos.library.Languages;
import ch.chaos.library.Memory;
import ch.chaos.library.Sounds;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class ChaosSounds {

    // Imports
    private final ChaosBase chaosBase;
    private final Checks checks;
    private final Dialogs dialogs;
    private final Files files;
    private final Languages languages;
    private final Memory memory;
    private final Sounds sounds;
    private final Trigo trigo;


    private ChaosSounds() {
        instance = this; // Set early to handle circular dependencies
        chaosBase = ChaosBase.instance();
        checks = Checks.instance();
        dialogs = Dialogs.instance();
        files = Files.instance();
        languages = Languages.instance();
        memory = Memory.instance();
        sounds = Sounds.instance();
        trigo = Trigo.instance();
    }


    // TYPE

    public static enum SoundList {
        aCrash,
        aPanflute,
        wCrash,
        wPanflute,
        wJans,
        wWhite,
        wNoise,
        wShakuhachi,
        wVoice,
        sCaisse,
        sCannon,
        sCasserole,
        sCymbale,
        sGong,
        sGun,
        sHa,
        sHHat,
        sHurryUp,
        sLaser,
        sMissile,
        sMoney,
        sPoubelle,
        sPouf,
        sVerre;
    }

    public static class Sound { // RECORD

        public Object wave;
        public long size;
        public int offset;
        public int rate;


        public Object getWave() {
            return this.wave;
        }

        public void setWave(Object wave) {
            this.wave = wave;
        }

        public long getSize() {
            return this.size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public int getOffset() {
            return this.offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getRate() {
            return this.rate;
        }

        public void setRate(int rate) {
            this.rate = rate;
        }


        public void copyFrom(Sound other) {
            this.wave = other.wave;
            this.size = other.size;
            this.offset = other.offset;
            this.rate = other.rate;
        }

        public Sound newCopy() {
            Sound copy = new Sound();
            copy.copyFrom(this);
            return copy;
        }

    }

    public static class Effect { // RECORD

        public Sound sound /* POINTER */;
        public int delay;
        public int rate;
        public short volume;
        public byte pri;


        public Sound getSound() {
            return this.sound;
        }

        public void setSound(Sound sound) {
            this.sound = sound;
        }

        public int getDelay() {
            return this.delay;
        }

        public void setDelay(int delay) {
            this.delay = delay;
        }

        public int getRate() {
            return this.rate;
        }

        public void setRate(int rate) {
            this.rate = rate;
        }

        public short getVolume() {
            return this.volume;
        }

        public void setVolume(short volume) {
            this.volume = volume;
        }

        public byte getPri() {
            return this.pri;
        }

        public void setPri(byte pri) {
            this.pri = pri;
        }


        public void copyFrom(Effect other) {
            this.sound = other.sound;
            this.delay = other.delay;
            this.rate = other.rate;
            this.volume = other.volume;
            this.pri = other.pri;
        }

        public Effect newCopy() {
            Effect copy = new Effect();
            copy.copyFrom(this);
            return copy;
        }

    }

    public static class Channel { // RECORD

        public ChaosBase.Obj sndObj /* POINTER */;
        public Object sndWave;
        public short sndPri;
        public short boostPri;


        public ChaosBase.Obj getSndObj() {
            return this.sndObj;
        }

        public void setSndObj(ChaosBase.Obj sndObj) {
            this.sndObj = sndObj;
        }

        public Object getSndWave() {
            return this.sndWave;
        }

        public void setSndWave(Object sndWave) {
            this.sndWave = sndWave;
        }

        public short getSndPri() {
            return this.sndPri;
        }

        public void setSndPri(short sndPri) {
            this.sndPri = sndPri;
        }

        public short getBoostPri() {
            return this.boostPri;
        }

        public void setBoostPri(short boostPri) {
            this.boostPri = boostPri;
        }


        public void copyFrom(Channel other) {
            this.sndObj = other.sndObj;
            this.sndWave = other.sndWave;
            this.sndPri = other.sndPri;
            this.boostPri = other.boostPri;
        }

        public Channel newCopy() {
            Channel copy = new Channel();
            copy.copyFrom(this);
            return copy;
        }

    }


    // VAR

    public Channel[] channel = Runtime.initArray(new Channel[16]);
    public int nbChannel;
    public short musicPri;
    public boolean sound;
    public boolean music;
    public boolean stereo;
    public boolean dfltSound;
    public Sound[] soundList = Runtime.initArray(new Sound[SoundList.values().length]);
    public Sound nulSound = new Sound();


    public Channel[] getChannel() {
        return this.channel;
    }

    public void setChannel(Channel[] channel) {
        this.channel = channel;
    }

    public int getNbChannel() {
        return this.nbChannel;
    }

    public void setNbChannel(int nbChannel) {
        this.nbChannel = nbChannel;
    }

    public short getMusicPri() {
        return this.musicPri;
    }

    public void setMusicPri(short musicPri) {
        this.musicPri = musicPri;
    }

    public boolean isSound() {
        return this.sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isMusic() {
        return this.music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public boolean isStereo() {
        return this.stereo;
    }

    public void setStereo(boolean stereo) {
        this.stereo = stereo;
    }

    public boolean isDfltSound() {
        return this.dfltSound;
    }

    public void setDfltSound(boolean dfltSound) {
        this.dfltSound = dfltSound;
    }

    public Sound[] getSoundList() {
        return this.soundList;
    }

    public void setSoundList(Sound[] soundList) {
        this.soundList = soundList;
    }

    public Sound getNulSound() {
        return this.nulSound;
    }

    public void setNulSound(Sound nulSound) {
        this.nulSound = nulSound;
    }


    // CONST

    private static final String SamplesFile = "AllSamples";


    // VAR

    private Sounds.ChannelPtr[] chans = new Sounds.ChannelPtr[16];
    private int nbChans;
    private short stereoEffect;
    private short plStereo;


    public Sounds.ChannelPtr[] getChans() {
        return this.chans;
    }

    public void setChans(Sounds.ChannelPtr[] chans) {
        this.chans = chans;
    }

    public int getNbChans() {
        return this.nbChans;
    }

    public void setNbChans(int nbChans) {
        this.nbChans = nbChans;
    }

    public short getStereoEffect() {
        return this.stereoEffect;
    }

    public void setStereoEffect(short stereoEffect) {
        this.stereoEffect = stereoEffect;
    }

    public short getPlStereo() {
        return this.plStereo;
    }

    public void setPlStereo(short plStereo) {
        this.plStereo = plStereo;
    }


    // PROCEDURE

    private void FlushWave(/* VAR */ Sound sound) {
        sounds.FreeWave(new Runtime.FieldExprRef<>(sound, Sound::getWave, Sound::setWave));
        sound.size = 0;
    }

    private void SwitchSoundOn_LoadWave(/* VAR */ Sound sound, int size, Dialogs.GadgetPtr progress, /* VAR */ Runtime.IRef<Integer> fill, /* VAR */ Runtime.IRef<Boolean> loadOk) {
        // VAR
        int res = 0;

        if (!loadOk.get())
            return;
        sound.size = size;
        sound.wave = sounds.AllocWave(sound.size);
        if (sound.wave == null) {
            SwitchSoundOff();
            checks.Warn(true, Runtime.castToRef(languages.ADL("Not enough memory"), String.class), Runtime.castToRef(languages.ADL("to load sounds"), String.class));
            loadOk.set(false);
            return;
        }
        res = files.ReadFileBytes(chaosBase.file, sound.wave, (int) sound.size);
        if (res <= 0) {
            checks.Warn(true, Runtime.castToRef(memory.ADS(SamplesFile), String.class), files.FileErrorMsg());
            loadOk.set(false);
        }
        fill.inc(2849);
        if (progress != dialogs.noGadget)
            dialogs.ModifyGadget(progress, (Memory.TagItem) memory.TAG1(Dialogs.dFILL, fill.get()));
        sound.offset = 0;
        sound.rate = 8363;
    }

    public boolean SwitchSoundOn() {
        // CONST
        final String DTitle = "Loading sounds";

        // VAR
        Dialogs.GadgetPtr progress = null;
        short c = 0;
        Runtime.Ref<Integer> fill = new Runtime.Ref<>(0);
        SoundList sndCnt = SoundList.aCrash;
        Runtime.Ref<Boolean> loadOk = new Runtime.Ref<>(false);
        Object dTitle = null;

        SwitchSoundOff();
        if (!sound)
            return true;
        loadOk.set(true);
        fill.set(8);
        dTitle = languages.ADL(DTitle);
        chaosBase.d = dialogs.CreateGadget((short) Dialogs.dDialog, (Memory.TagItem) memory.TAG2(Dialogs.dTEXT, dTitle, Dialogs.dRFLAGS, Dialogs.dfCLOSE));
        progress = dialogs.noGadget;
        if (chaosBase.d != dialogs.noGadget) {
            progress = dialogs.AddNewGadget(chaosBase.d, (short) Dialogs.dProgress, (Memory.TagItem) memory.TAG3(Dialogs.dRFLAGS, Dialogs.dfBORDER, Dialogs.dTEXT, memory.ADS(""), Dialogs.dFLAGS, Dialogs.dfJUSTIFY));
            if ((progress == dialogs.noGadget) || (dialogs.RefreshGadget(chaosBase.d) != Dialogs.DialogOk))
                dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
        }
        chaosBase.file = files.OpenFile(Runtime.castToRef(memory.ADS(SamplesFile), String.class), EnumSet.of(AccessFlags.accessRead));
        if (chaosBase.file == files.noFile) {
            checks.Warn(true, Runtime.castToRef(memory.ADS(SamplesFile), String.class), files.FileErrorMsg());
            loadOk.set(false);
        }
        SwitchSoundOn_LoadWave(soundList[SoundList.aCrash.ordinal()], 1408, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.aPanflute.ordinal()], 4234, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.wCrash.ordinal()], 3492, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.wPanflute.ordinal()], 8642, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.wJans.ordinal()], 10864, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.wShakuhachi.ordinal()], 4004, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.wWhite.ordinal()], 2162, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.wNoise.ordinal()], 4326, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sCaisse.ordinal()], 3928, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sCannon.ordinal()], 15916, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sCasserole.ordinal()], 15218, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sCymbale.ordinal()], 1282, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sGong.ordinal()], 23512, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sGun.ordinal()], 438, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sHa.ordinal()], 2686, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sHHat.ordinal()], 2068, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sHurryUp.ordinal()], 8574, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sLaser.ordinal()], 1522, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sMissile.ordinal()], 2788, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sMoney.ordinal()], 2904, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sPoubelle.ordinal()], 3596, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sPouf.ordinal()], 14480, progress, fill, loadOk);
        SwitchSoundOn_LoadWave(soundList[SoundList.sVerre.ordinal()], 8166, progress, fill, loadOk);
        files.CloseFile(new Runtime.FieldRef<>(chaosBase::getFile, chaosBase::setFile));
        if (!loadOk.get()) {
            dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
            return false;
        }
        soundList[SoundList.aPanflute.ordinal()].rate = 16726;
        soundList[SoundList.wPanflute.ordinal()].rate = 16726;
        { // WITH
            Sound _sound = soundList[SoundList.wVoice.ordinal()];
            _sound.wave = soundList[SoundList.wJans.ordinal()].wave;
            _sound.offset = 2416;
            _sound.size = 4216;
            _sound.rate = 8363;
        }
        for (int _sndCnt = 0; _sndCnt < SoundList.values().length; _sndCnt++) {
            sndCnt = SoundList.values()[_sndCnt];
            if (sndCnt != SoundList.wVoice)
                sounds.ConvertWave(new Runtime.FieldExprRef<>(soundList[sndCnt.ordinal()], Sound::getWave, Sound::setWave), soundList[sndCnt.ordinal()].size);
        }
        if (nbChannel > 16)
            nbChannel = 16;
        nbChans = nbChannel;
        c = (short) nbChannel;
        while (loadOk.get() && (c > 0)) {
            c--;
            chans[c] = sounds.AllocChannel((Memory.TagItem) memory.TAG1(Sounds.sSTEREO, (stereo ? 1 : 0)));
            if (chans[c] == sounds.noChan) {
                checks.Warn(true, Runtime.castToRef(languages.ADL("Unable to allocate"), String.class), Runtime.castToRef(languages.ADL("sound channels"), String.class));
                loadOk.set(false);
            }
            { // WITH
                Channel _channel = channel[c];
                _channel.sndWave = null;
                _channel.sndObj = null;
                _channel.sndPri = (short) Byte.MIN_VALUE /* MIN(SHORTINT) */;
            }
        }
        dialogs.DeepFreeGadget(new Runtime.FieldRef<>(chaosBase::getD, chaosBase::setD));
        if (loadOk.get())
            SimpleSound(soundList[SoundList.sCaisse.ordinal()]);
        return loadOk.get();
    }

    public void SwitchSoundOff() {
        // VAR
        SoundList sound = SoundList.aCrash;
        short c = 0;

        while (nbChans > 0) {
            nbChans--;
            if (chans[nbChans] != sounds.noChan)
                sounds.FreeChannel(new Runtime.ArrayElementRef<>(chans, nbChans));
        }
        { // WITH
            Sound _sound = soundList[SoundList.wVoice.ordinal()];
            _sound.wave = null;
            _sound.size = 0;
        }
        for (int _sound = 0; _sound < SoundList.values().length; _sound++) {
            sound = SoundList.values()[_sound];
            FlushWave(soundList[sound.ordinal()]);
        }
        for (c = 0; c <= 15; c++) {
            channel[c].sndObj = null;
        }
    }

    public final Runnable SwitchSoundOff_ref = this::SwitchSoundOff;

    public void SetEffect(/* VAR */ Effect effect, /* var */ Sound sound, int delay, int rate, short volume, short pri) {
        effect.sound = sound;
        effect.delay = delay;
        effect.rate = rate;
        effect.volume = volume;
        effect.pri = (byte) pri;
    }

    public void StereoEffect() {
        if (stereo)
            stereoEffect = 2;
    }

    public void SimpleSound(Sound sound) {
        if (nbChans == 0)
            return;
        sounds.SndDo(chans[0], (Memory.TagItem) memory.TAG2(Sounds.sPRI, 0, Sounds.sWATER, 0));
        sounds.SndDo(chans[0], (Memory.TagItem) memory.TAG7(Sounds.sWAVE, sound.wave, Sounds.sLENGTH, sound.size, Sounds.sVOLUME, 64, Sounds.sRATE, sound.rate, Sounds.sSTEREO, 0, Sounds.sAM, 128, Sounds.sFM, 256));
    }

    private void ModulateSound(int chan, Object tags) {
        // CONST
        final int AirSpeed = 426;
        final int WaterSpeed = 1843;

        // VAR
        Memory.TagItem tagGadget = new Memory.TagItem(); /* WRT */
        long dx = 0L;
        long dy = 0L;
        long dist = 0L;
        int px = 0;
        int pl = 0;
        short rx = 0;
        short ry = 0;
        short rl = 0;
        short ov = 0;
        short pv = 0;
        int am = 0;
        int fm = 0;
        int os = 0;
        int ps = 0;
        long delay = 0L;
        short stereo = 0;

        { // WITH
            Channel _channel = channel[chan];
            if ((tags == null) && sounds.SndFinish(chans[chan])) {
                _channel.sndWave = null;
                _channel.sndObj = null;
                _channel.sndPri = (short) Byte.MIN_VALUE /* MIN(SHORTINT) */;
                return;
            }
            if (_channel.sndObj == null)
                return;
            rx = (short) ((_channel.sndObj.x - chaosBase.mainPlayer.x) / ChaosBase.Frac);
            rx += _channel.sndObj.cx - chaosBase.mainPlayer.cx;
            ry = (short) ((_channel.sndObj.y - chaosBase.mainPlayer.y) / ChaosBase.Frac);
            ry += _channel.sndObj.cy - chaosBase.mainPlayer.cy;
            px = rx;
            dx = Math.abs(rx);
            dy = Math.abs(ry);
            dist = trigo.SQRT(dx * dx + dy * dy);
            pl = (int) dist;
            if (dist >= 256)
                am = 0;
            else
                am = (int) (128 - (dist / 2));
            if (am > 0) {
                if ((stereoEffect > 0) && (tags != null)) {
                    stereoEffect--;
                    if (plStereo == -181)
                        plStereo = 181;
                    else
                        plStereo = -181;
                    if (nbChans > 1)
                        stereo = plStereo;
                    else
                        stereo = 0;
                } else if ((tags == null) && (_channel.sndObj.kind == Anims.PLAYER)) {
                    tagGadget.tag = Sounds.sSTEREO;
                    sounds.SndGet(chans[chan], tagGadget);
                    stereo = (short) tagGadget.lint;
                } else if (pl != 0) {
                    stereo = (short) ((px * 90) / pl);
                    if (ry > 0) {
                        if (stereo > 0)
                            stereo = (short) (180 - stereo);
                        else
                            stereo = (short) (-180 - stereo);
                    }
                } else {
                    stereo = 0;
                }
                if ((dx > 127) || (dy > 127)) {
                    rx = (short) (rx / 2);
                    ry = (short) (ry / 2);
                }
                rl = (short) trigo.SQRT(rx * rx + ry * ry);
                if (rl != 0) {
                    ov = (short) -(((_channel.sndObj.vx / 32) * rx + (_channel.sndObj.vy / 32) * ry) / rl);
                    pv = (short) (((chaosBase.mainPlayer.vx / 32) * rx + (chaosBase.mainPlayer.vy / 32) * ry) / rl);
                    if (chaosBase.water) {
                        os = WaterSpeed - ov;
                        ps = WaterSpeed + pv;
                        fm = ((ps * 32) / os) * 8;
                    } else {
                        os = AirSpeed - ov;
                        ps = AirSpeed + pv;
                        fm = ((ps * 64) / os) * 4;
                    }
                } else {
                    fm = 256;
                }
            } else {
                fm = 256;
                stereo = 0;
            }
            if (dist < 512)
                delay = dist * 64;
            else
                delay = 32767;
            if (tags != null)
                sounds.SndDo(chans[chan], (Memory.TagItem) memory.TAG6(Sounds.sDISTANCE, delay, Sounds.sAM, am, Sounds.sFM, fm, Sounds.sSTEREO, stereo, Sounds.sWATER, (chaosBase.water ? 1 : 0) * 100, Memory.tagMore, tags));
            else
                sounds.SndDo(chans[chan], (Memory.TagItem) memory.TAG4(Sounds.sDISTANCE, delay, Sounds.sAM, am, Sounds.sFM, fm, Sounds.sSTEREO, stereo));
        }
    }

    public void ModulateSounds() {
        // VAR
        int chan = 0;

        chan = nbChans;
        while (chan > 0) {
            chan--;
            ModulateSound(chan, null);
        }
    }

    public boolean GetChan(/* VAR */ Runtime.IRef<Integer> chan, short pri, Object wave, ChaosBase.Obj obj) {
        // VAR
        int rx = 0;
        int ry = 0;
        long dx = 0L;
        long dy = 0L;
        int dl = 0;
        short boost = 0;

        rx = (obj.x - chaosBase.mainPlayer.x) / ChaosBase.Frac;
        rx += obj.cx - chaosBase.mainPlayer.cx;
        ry = (obj.y - chaosBase.mainPlayer.y) / ChaosBase.Frac;
        ry += obj.cy - chaosBase.mainPlayer.cy;
        dx = Math.abs(rx);
        dy = Math.abs(ry);
        dl = trigo.SQRT(dx * dx + dy * dy);
        if (dl >= 256)
            boost = 0;
        else
            boost = (short) (128 - (dl / 2));
        pri = (short) ((pri * boost + 127) / 128);
        chan.set(nbChans);
        while (true) {
            if (chan.get() == 0) {
                chan.set(nbChans);
                do {
                    if (chan.get() == 0)
                        return false;
                    chan.dec();
                } while (channel[chan.get()].sndPri > pri);
                break;
            }
            chan.dec();
            if (channel[chan.get()].sndPri == Byte.MIN_VALUE /* MIN(SHORTINT) */)
                break;
        }
        { // WITH
            Channel _channel = channel[chan.get()];
            _channel.sndPri = pri;
            _channel.boostPri = boost;
            _channel.sndWave = wave;
            _channel.sndObj = obj;
        }
        return true;
    }

    public void SoundEffect(ChaosBase.Obj obj, /* var */ ChaosSounds.Effect[] effects) {
        // VAR
        Object wave = null;
        Memory.TagItem tags = null;
        int offset = 0;
        int bytes = 0;
        int length = 0;
        int size = 0;
        Runtime.Ref<Integer> chan = new Runtime.Ref<>(0);
        int lrate = 0;
        int rrate = 0;
        short k = 0;
        boolean first = false;

        if (GetChan(chan, effects[0].pri, effects[0].sound.wave, obj)) {
            first = true;
            for (k = 0; k <= (effects.length - 1); k++) {
                { // WITH
                    Effect _effect = effects[k];
                    if (_effect.sound.wave != null) {
                        wave = _effect.sound.wave;
                        size = (int) _effect.sound.size;
                        offset = 0;
                        lrate = _effect.sound.rate;
                    }
                    if (_effect.rate == 0)
                        rrate = lrate;
                    else
                        rrate = _effect.rate;
                    if (chaosBase.water) {
                        rrate = rrate * 2 / 3;
                        if (_effect.delay == 0)
                            bytes = size;
                        else
                            bytes = _effect.delay * 2 / 3;
                    } else {
                        if (_effect.delay == 0)
                            bytes = size;
                        else
                            bytes = _effect.delay;
                    }
                    while (true) {
                        length = size - offset;
                        if (bytes < length)
                            length = bytes;
                        tags = (Memory.TagItem) memory.TAG6(Sounds.sWAVE, wave, Sounds.sLENGTH, length, Sounds.sOFFSET, offset + _effect.sound.offset, Sounds.sVOLUME, _effect.volume, Sounds.sRATE, rrate, Sounds.sPRI, (_effect.pri * channel[chan.get()].boostPri + 127) / 128);
                        if (first) {
                            ModulateSound(chan.get(), tags);
                            first = false;
                        } else {
                            sounds.SndQueue(chans[chan.get()], tags);
                        }
                        bytes -= length;
                        if (bytes == 0)
                            break;
                        offset += length;
                        if (offset >= size)
                            offset -= size;
                    }
                }
            }
        }
    }

    private void Clear() {
        // VAR
        SoundList s = SoundList.aCrash;
        int c = 0;

        for (c = 0; c <= 15; c++) {
            chans[c] = sounds.noChan;
        }
        for (int _s = 0; _s < SoundList.values().length; _s++) {
            s = SoundList.values()[_s];
            { // WITH
                Sound _sound = soundList[s.ordinal()];
                _sound.wave = null;
                _sound.size = 0;
            }
        }
    }


    // Support

    private static ChaosSounds instance;

    public static ChaosSounds instance() {
        if (instance == null)
            new ChaosSounds(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        Clear();
        stereoEffect = 0;
        nulSound.wave = null;
        nbChans = 0;
        checks.AddTermProc(SwitchSoundOff_ref);
    }

    public void close() {
    }

}
