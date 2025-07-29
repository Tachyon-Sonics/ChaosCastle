package ch.chaos.grotte;

import java.lang.Runnable;
import java.util.EnumSet;

import ch.chaos.grotte.GrotteSupport.OBJECT;
import ch.chaos.library.Checks;
import ch.chaos.library.Files;
import ch.chaos.library.Files.AccessFlags;
import ch.chaos.library.Memory;
import ch.chaos.library.Sounds;
import ch.chaos.library.Trigo;
import ch.pitchtech.modula.runtime.Runtime;


public class GrotteSounds {

    // Imports
    private final Checks checks;
    private final Files files;
    private final Memory memory;
    private final Sounds sounds;
    private final Trigo trigo;


    private GrotteSounds() {
        instance = this; // Set early to handle circular dependencies
        checks = Checks.instance();
        files = Files.instance();
        memory = Memory.instance();
        sounds = Sounds.instance();
        trigo = Trigo.instance();
    }


    // TYPE

    private static enum SoundList {
        CrashA,
        Crash,
        Cymbales,
        Gong,
        Canon1,
        Canon2,
        Hit1,
        Hit2,
        HHat,
        Panflute,
        Water,
        Pic,
        Noise,
        Clock;
    }

    private static class Snd { // RECORD

        private Object wave;
        private long size;
        private int rate;
        private int pri;


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

        public int getRate() {
            return this.rate;
        }

        public void setRate(int rate) {
            this.rate = rate;
        }

        public int getPri() {
            return this.pri;
        }

        public void setPri(int pri) {
            this.pri = pri;
        }


        public void copyFrom(Snd other) {
            this.wave = other.wave;
            this.size = other.size;
            this.rate = other.rate;
            this.pri = other.pri;
        }

        public Snd newCopy() {
            Snd copy = new Snd();
            copy.copyFrom(this);
            return copy;
        }

    }

    private static class Effect { // RECORD

        private Snd sound /* POINTER */;
        private int length;
        private int freq;
        private int volume;


        public Snd getSound() {
            return this.sound;
        }

        public void setSound(Snd sound) {
            this.sound = sound;
        }

        public int getLength() {
            return this.length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getFreq() {
            return this.freq;
        }

        public void setFreq(int freq) {
            this.freq = freq;
        }

        public int getVolume() {
            return this.volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }


        public void copyFrom(Effect other) {
            this.sound = other.sound;
            this.length = other.length;
            this.freq = other.freq;
            this.volume = other.volume;
        }

        public Effect newCopy() {
            Effect copy = new Effect();
            copy.copyFrom(this);
            return copy;
        }

    }

    private static class Channel { // RECORD

        private Sounds.ChannelPtr chan;
        private Object chanWave;
        private short chanVolume;
        private short chanStereo;


        public Sounds.ChannelPtr getChan() {
            return this.chan;
        }

        public void setChan(Sounds.ChannelPtr chan) {
            this.chan = chan;
        }

        public Object getChanWave() {
            return this.chanWave;
        }

        public void setChanWave(Object chanWave) {
            this.chanWave = chanWave;
        }

        public short getChanVolume() {
            return this.chanVolume;
        }

        public void setChanVolume(short chanVolume) {
            this.chanVolume = chanVolume;
        }

        public short getChanStereo() {
            return this.chanStereo;
        }

        public void setChanStereo(short chanStereo) {
            this.chanStereo = chanStereo;
        }


        public void copyFrom(Channel other) {
            this.chan = other.chan;
            this.chanWave = other.chanWave;
            this.chanVolume = other.chanVolume;
            this.chanStereo = other.chanStereo;
        }

        public Channel newCopy() {
            Channel copy = new Channel();
            copy.copyFrom(this);
            return copy;
        }

    }


    // VAR

    private Channel[] channel = Runtime.initArray(new Channel[16]);
    private int nbChan;
    private Snd[] soundList = Runtime.initArray(new Snd[SoundList.values().length]);
    private Effect[] highGong = Runtime.initArray(new Effect[1]);
    private Effect[] lowCannon = Runtime.initArray(new Effect[1]);
    private Effect[] low2Cannon = Runtime.initArray(new Effect[1]);
    private Effect[] blvEffect = Runtime.initArray(new Effect[1]);
    private Effect[] popupEffect = Runtime.initArray(new Effect[1]);
    private Effect[] bonus1Effect = Runtime.initArray(new Effect[1]);
    private Effect[] ptEffect = Runtime.initArray(new Effect[5]);
    private Effect[] pt2Effect = Runtime.initArray(new Effect[5]);
    private Effect[] bonus2Effect = Runtime.initArray(new Effect[15]);
    private Effect[] createEffect = Runtime.initArray(new Effect[3]);
    private Effect[] fireEffect = Runtime.initArray(new Effect[2]);
    private Effect[] dieEffect2 = Runtime.initArray(new Effect[1]);
    private Effect[] dieEffect1 = Runtime.initArray(new Effect[31]);
    private Effect[] aieEffect = Runtime.initArray(new Effect[6]);
    private Effect[] atEffect = Runtime.initArray(new Effect[6]);
    private Effect[] dieNestEffect = Runtime.initArray(new Effect[24]);
    private Effect[] bumper1Effect = Runtime.initArray(new Effect[3]);
    private Effect[] bumperEffect = Runtime.initArray(new Effect[16]);
    private Effect[] turretEffect = Runtime.initArray(new Effect[16]);
    private Effect[] metalEffect = Runtime.initArray(new Effect[12]);
    private Effect[] picEffect = Runtime.initArray(new Effect[4]);
    private Effect[] gameEffect = Runtime.initArray(new Effect[26]);


    public Channel[] getChannel() {
        return this.channel;
    }

    public void setChannel(Channel[] channel) {
        this.channel = channel;
    }

    public int getNbChan() {
        return this.nbChan;
    }

    public void setNbChan(int nbChan) {
        this.nbChan = nbChan;
    }

    public Snd[] getSoundList() {
        return this.soundList;
    }

    public void setSoundList(Snd[] soundList) {
        this.soundList = soundList;
    }

    public Effect[] getHighGong() {
        return this.highGong;
    }

    public void setHighGong(Effect[] highGong) {
        this.highGong = highGong;
    }

    public Effect[] getLowCannon() {
        return this.lowCannon;
    }

    public void setLowCannon(Effect[] lowCannon) {
        this.lowCannon = lowCannon;
    }

    public Effect[] getLow2Cannon() {
        return this.low2Cannon;
    }

    public void setLow2Cannon(Effect[] low2Cannon) {
        this.low2Cannon = low2Cannon;
    }

    public Effect[] getBlvEffect() {
        return this.blvEffect;
    }

    public void setBlvEffect(Effect[] blvEffect) {
        this.blvEffect = blvEffect;
    }

    public Effect[] getPopupEffect() {
        return this.popupEffect;
    }

    public void setPopupEffect(Effect[] popupEffect) {
        this.popupEffect = popupEffect;
    }

    public Effect[] getBonus1Effect() {
        return this.bonus1Effect;
    }

    public void setBonus1Effect(Effect[] bonus1Effect) {
        this.bonus1Effect = bonus1Effect;
    }

    public Effect[] getPtEffect() {
        return this.ptEffect;
    }

    public void setPtEffect(Effect[] ptEffect) {
        this.ptEffect = ptEffect;
    }

    public Effect[] getPt2Effect() {
        return this.pt2Effect;
    }

    public void setPt2Effect(Effect[] pt2Effect) {
        this.pt2Effect = pt2Effect;
    }

    public Effect[] getBonus2Effect() {
        return this.bonus2Effect;
    }

    public void setBonus2Effect(Effect[] bonus2Effect) {
        this.bonus2Effect = bonus2Effect;
    }

    public Effect[] getCreateEffect() {
        return this.createEffect;
    }

    public void setCreateEffect(Effect[] createEffect) {
        this.createEffect = createEffect;
    }

    public Effect[] getFireEffect() {
        return this.fireEffect;
    }

    public void setFireEffect(Effect[] fireEffect) {
        this.fireEffect = fireEffect;
    }

    public Effect[] getDieEffect2() {
        return this.dieEffect2;
    }

    public void setDieEffect2(Effect[] dieEffect2) {
        this.dieEffect2 = dieEffect2;
    }

    public Effect[] getDieEffect1() {
        return this.dieEffect1;
    }

    public void setDieEffect1(Effect[] dieEffect1) {
        this.dieEffect1 = dieEffect1;
    }

    public Effect[] getAieEffect() {
        return this.aieEffect;
    }

    public void setAieEffect(Effect[] aieEffect) {
        this.aieEffect = aieEffect;
    }

    public Effect[] getAtEffect() {
        return this.atEffect;
    }

    public void setAtEffect(Effect[] atEffect) {
        this.atEffect = atEffect;
    }

    public Effect[] getDieNestEffect() {
        return this.dieNestEffect;
    }

    public void setDieNestEffect(Effect[] dieNestEffect) {
        this.dieNestEffect = dieNestEffect;
    }

    public Effect[] getBumper1Effect() {
        return this.bumper1Effect;
    }

    public void setBumper1Effect(Effect[] bumper1Effect) {
        this.bumper1Effect = bumper1Effect;
    }

    public Effect[] getBumperEffect() {
        return this.bumperEffect;
    }

    public void setBumperEffect(Effect[] bumperEffect) {
        this.bumperEffect = bumperEffect;
    }

    public Effect[] getTurretEffect() {
        return this.turretEffect;
    }

    public void setTurretEffect(Effect[] turretEffect) {
        this.turretEffect = turretEffect;
    }

    public Effect[] getMetalEffect() {
        return this.metalEffect;
    }

    public void setMetalEffect(Effect[] metalEffect) {
        this.metalEffect = metalEffect;
    }

    public Effect[] getPicEffect() {
        return this.picEffect;
    }

    public void setPicEffect(Effect[] picEffect) {
        this.picEffect = picEffect;
    }

    public Effect[] getGameEffect() {
        return this.gameEffect;
    }

    public void setGameEffect(Effect[] gameEffect) {
        this.gameEffect = gameEffect;
    }


    // PROCEDURE

    private void InitEffects_SetEffect(/* var */ Effect e, SoundList sound, int time, int freq, short volume) {
        e.sound = soundList[sound.ordinal()];
        if (time != 0)
            e.length = (soundList[sound.ordinal()].rate * freq / 440) * time / 1000;
        else
            e.length = (int) soundList[sound.ordinal()].size;
        e.freq = freq;
        e.volume = volume;
    }

    private void InitEffects() {
        // VAR
        short c = 0;
        short v = 0;
        short f = 0;
        short r1 = 0;
        short r2 = 0;
        int g = 0;

        InitEffects_SetEffect(ptEffect[0], SoundList.Clock, 50, 880, (short) 255);
        InitEffects_SetEffect(pt2Effect[0], SoundList.Clock, 50, 587, (short) 255);
        for (c = 0; c <= 3; c++) {
            InitEffects_SetEffect(ptEffect[c + 1], SoundList.Clock, 0, 880, (short) ((4 - c) * 24));
            InitEffects_SetEffect(pt2Effect[c + 1], SoundList.Clock, 0, 587, (short) ((4 - c) * 24));
        }
        InitEffects_SetEffect(dieEffect1[0], SoundList.HHat, 0, 110, (short) 255);
        for (c = 1; c <= 6; c++) {
            InitEffects_SetEffect(dieEffect1[c], SoundList.Noise, 0, 990, (short) (c * c * 4));
        }
        InitEffects_SetEffect(dieEffect1[7], SoundList.Panflute, 100, 329, (short) 10);
        InitEffects_SetEffect(dieEffect1[8], SoundList.Panflute, 100, 554, (short) 20);
        InitEffects_SetEffect(dieEffect1[9], SoundList.Panflute, 150, 370, (short) 30);
        InitEffects_SetEffect(dieEffect1[10], SoundList.Panflute, 100, 494, (short) 40);
        InitEffects_SetEffect(dieEffect1[11], SoundList.Panflute, 120, 587, (short) 50);
        InitEffects_SetEffect(dieEffect1[12], SoundList.Panflute, 100, 415, (short) 60);
        InitEffects_SetEffect(dieEffect1[13], SoundList.Panflute, 100, 423, (short) 70);
        InitEffects_SetEffect(dieEffect1[14], SoundList.Panflute, 100, 440, (short) 70);
        InitEffects_SetEffect(dieEffect1[15], SoundList.Panflute, 140, 368, (short) 70);
        InitEffects_SetEffect(dieEffect1[16], SoundList.Panflute, 120, 495, (short) 70);
        InitEffects_SetEffect(dieEffect1[17], SoundList.Panflute, 120, 395, (short) 70);
        InitEffects_SetEffect(dieEffect1[18], SoundList.Panflute, 100, 329, (short) 70);
        InitEffects_SetEffect(dieEffect1[19], SoundList.Panflute, 100, 289, (short) 70);
        InitEffects_SetEffect(dieEffect1[20], SoundList.Panflute, 130, 439, (short) 70);
        InitEffects_SetEffect(dieEffect1[21], SoundList.Panflute, 100, 552, (short) 70);
        InitEffects_SetEffect(dieEffect1[22], SoundList.Panflute, 120, 658, (short) 70);
        InitEffects_SetEffect(dieEffect1[23], SoundList.Panflute, 100, 418, (short) 70);
        InitEffects_SetEffect(dieEffect1[24], SoundList.Panflute, 100, 492, (short) 70);
        InitEffects_SetEffect(dieEffect1[25], SoundList.Panflute, 100, 587, (short) 60);
        InitEffects_SetEffect(dieEffect1[26], SoundList.Panflute, 150, 740, (short) 50);
        InitEffects_SetEffect(dieEffect1[27], SoundList.Panflute, 100, 440, (short) 40);
        InitEffects_SetEffect(dieEffect1[28], SoundList.Panflute, 120, 581, (short) 30);
        InitEffects_SetEffect(dieEffect1[29], SoundList.Panflute, 100, 389, (short) 20);
        InitEffects_SetEffect(dieEffect1[30], SoundList.Panflute, 100, 342, (short) 10);
        InitEffects_SetEffect(dieEffect2[0], SoundList.Gong, 0, 220, (short) 255);
        InitEffects_SetEffect(aieEffect[0], SoundList.CrashA, 0, 440, (short) 250);
        for (c = 1; c <= 5; c++) {
            InitEffects_SetEffect(aieEffect[c], SoundList.Crash, 0, 440, (short) ((6 - c) * (6 - c) * 10));
        }
        InitEffects_SetEffect(atEffect[0], SoundList.CrashA, 0, 220, (short) 250);
        for (c = 1; c <= 5; c++) {
            InitEffects_SetEffect(atEffect[c], SoundList.Crash, 0, 220, (short) ((6 - c) * (6 - c) * 10));
        }
        InitEffects_SetEffect(highGong[0], SoundList.Gong, 0, 880, (short) 230);
        InitEffects_SetEffect(lowCannon[0], SoundList.Canon1, 0, 220, (short) 255);
        InitEffects_SetEffect(low2Cannon[0], SoundList.Canon1, 0, 230, (short) 255);
        InitEffects_SetEffect(blvEffect[0], SoundList.Water, 0, 110, (short) 255);
        for (c = 0; c <= 23; c++) {
            v = (short) (Math.abs(c % 4 - 2) + 1);
            InitEffects_SetEffect(dieNestEffect[c], SoundList.Noise, 84 - v * 14, 440 + v * 147, (short) ((24 - c) * v * 7 / 2));
        }
        InitEffects_SetEffect(bonus1Effect[0], SoundList.HHat, 0, 220, (short) 255);
        f = 440;
        for (c = 0; c <= 14; c++) {
            v = (short) (((16 - c) * 16 - 1) * ((c + 1) % 2));
            InitEffects_SetEffect(bonus2Effect[c], SoundList.Panflute, 30, f, v);
            f -= 5;
        }
        for (c = 0; c <= 15; c++) {
            f = (short) (c % 4);
            v = (short) ((16 - c) * 16 - 1);
            if (f == 3)
                v = 0;
            if (f == 0)
                f = 880;
            else if (f == 1)
                f = 440;
            else
                f = 220;
            InitEffects_SetEffect(bumperEffect[c], SoundList.Noise, 50, f, v);
        }
        InitEffects_SetEffect(bumper1Effect[0], SoundList.Noise, 70, 880, (short) 80);
        InitEffects_SetEffect(bumper1Effect[1], SoundList.Noise, 70, 440, (short) 80);
        InitEffects_SetEffect(bumper1Effect[2], SoundList.Noise, 70, 220, (short) 80);
        InitEffects_SetEffect(createEffect[0], SoundList.Clock, 100, 131, (short) 250);
        InitEffects_SetEffect(createEffect[1], SoundList.Clock, 100, 197, (short) 250);
        InitEffects_SetEffect(createEffect[2], SoundList.Clock, 100, 164, (short) 250);
        InitEffects_SetEffect(fireEffect[0], SoundList.Clock, 80, 131, (short) 250);
        InitEffects_SetEffect(fireEffect[1], SoundList.Clock, 80, 262, (short) 250);
        InitEffects_SetEffect(popupEffect[0], SoundList.Cymbales, 0, 220, (short) 150);
        r1 = 0;
        r2 = 0;
        for (c = 0; c <= 15; c++) {
            r1 = (short) ((r1 * 17 + 5) % 8);
            r2 = (short) ((r2 * 13 + 9) % 16);
            v = (short) ((16 - c) * (r1 + 8));
            InitEffects_SetEffect(turretEffect[c], SoundList.HHat, 55 + r2, 554, v);
        }
        g = 220;
        v = 255;
        InitEffects_SetEffect(gameEffect[0], SoundList.Panflute, 500, 440, (short) 0);
        InitEffects_SetEffect(gameEffect[1], SoundList.Pic, 0, 440, (short) 255);
        for (c = 2; c <= 25; c++) {
            InitEffects_SetEffect(gameEffect[c], SoundList.Panflute, 25, g, (short) ((26 - c) * 10));
            g = g * 3118 / 2943;
        }
        for (c = 0; c <= 3; c++) {
            InitEffects_SetEffect(picEffect[c], SoundList.Pic, 0, 440, (short) ((4 - c) * 64 - 1));
        }
        for (c = 0; c <= 11; c++) {
            InitEffects_SetEffect(metalEffect[c], SoundList.HHat, 40, 440, (short) ((12 - c) * 12));
        }
    }

    private void InitSounds_LoadSound(SoundList which, int ssize, int srate, int spri, Files.FilePtr f, /* VAR */ Runtime.IRef<Boolean> ok) {
        if (ok.get()) {
            { // WITH
                Snd _snd = soundList[which.ordinal()];
                _snd.size = ssize;
                _snd.rate = srate;
                _snd.pri = spri;
                _snd.wave = sounds.AllocWave(_snd.size);
                if (_snd.wave != null) {
                    if (files.ReadFileBytes(f, _snd.wave, ssize) != ssize)
                        ok.set(false);
                }
            }
        }
    }

    public void InitSounds() {
        // CONST
        final String SamplesFile = "Samples";

        // VAR
        Runtime.Ref<Files.FilePtr> f = new Runtime.Ref<>(null);
        Runtime.Ref<Boolean> ok = new Runtime.Ref<>(false);
        SoundList s = SoundList.CrashA;
        Memory.TagItem attr = new Memory.TagItem(); /* WRT */
        short[] synth = null;
        int c = 0;
        int d = 0;
        short i = 0;
        short v = 0;
        boolean stereo = false;

        attr.tag = Sounds.sNUMCHANS;
        sounds.GetSoundsSysAttr(attr);
        if (attr.data == 0)
            return;
        ok.set(true);
        f.set(files.OpenFile(new Runtime.Ref<>(SamplesFile), EnumSet.of(AccessFlags.accessRead)));
        checks.Warn(f.get() == files.noFile, Runtime.castToRef(memory.ADS("Cannot open file"), String.class), new Runtime.Ref<>(SamplesFile));
        if (f.get() != files.noFile) {
            InitSounds_LoadSound(SoundList.CrashA, 1408, 8363, 3, f.get(), ok);
            InitSounds_LoadSound(SoundList.Crash, 3492, 8363, 2, f.get(), ok);
            InitSounds_LoadSound(SoundList.Cymbales, 1282, 8363, 1, f.get(), ok);
            InitSounds_LoadSound(SoundList.Gong, 23512, 8363, 4, f.get(), ok);
            InitSounds_LoadSound(SoundList.Canon1, 12222, 8363, 3, f.get(), ok);
            InitSounds_LoadSound(SoundList.Canon2, 14896, 16726, 4, f.get(), ok);
            InitSounds_LoadSound(SoundList.Hit1, 1128, 8363, 1, f.get(), ok);
            InitSounds_LoadSound(SoundList.Hit2, 784, 8363, 1, f.get(), ok);
            InitSounds_LoadSound(SoundList.HHat, 2068, 8363, 2, f.get(), ok);
            InitSounds_LoadSound(SoundList.Panflute, 8642, 16726, 4, f.get(), ok);
            InitSounds_LoadSound(SoundList.Water, 10748, 16726, 4, f.get(), ok);
            InitSounds_LoadSound(SoundList.Pic, 248, 8363, 2, f.get(), ok);
            InitSounds_LoadSound(SoundList.Noise, 4326, 8363, 4, f.get(), ok);
            synth = (short[]) sounds.AllocWave(4096);
            if (synth == null) {
                ok.set(false);
            } else {
                for (c = 0; c <= 7; c++) {
                    i = (short) c;
                    i -= 3;
                    v = (short) (trigo.SQRT(16 - i * i) * 8);
                    if (v > 127)
                        v = 127;
                    for (d = 0; d <= 255; d++) {
                        synth[d * 16 + c] = v;
                        synth[d * 16 + c + 8] = (short) (255 - v);
                    }
                }
                { // WITH
                    Snd _snd = soundList[SoundList.Clock.ordinal()];
                    _snd.wave = synth;
                    _snd.size = 4096;
                    _snd.rate = 14080;
                    _snd.pri = 3;
                }
            }
            if (ok.get()) {
                for (int _s = 0; _s < SoundList.values().length; _s++) {
                    s = SoundList.values()[_s];
                    { // WITH
                        Snd _snd = soundList[s.ordinal()];
                        if (_snd.wave != null)
                            sounds.ConvertWave(new Runtime.FieldRef<>(_snd::getWave, _snd::setWave), _snd.size);
                    }
                }
                attr.tag = Sounds.sNUMCHANS;
                sounds.GetSoundsSysAttr(attr);
                c = (int) attr.data;
                attr.tag = Sounds.sSTEREO;
                sounds.GetSoundsSysAttr(attr);
                stereo = (attr.data != 0);
                if (c > 16)
                    c = 16;
                while ((c > 0) && ok.get()) {
                    { // WITH
                        Channel _channel = channel[nbChan];
                        _channel.chan = sounds.AllocChannel((Memory.TagItem) memory.TAG1(Sounds.sSTEREO, (stereo ? 1 : 0)));
                        if (_channel.chan == sounds.noChan)
                            ok.set(false);
                    }
                    c--;
                    nbChan++;
                }
                checks.Warn(!ok.get(), Runtime.castToRef(memory.ADS("Unable to allocate sound channels"), String.class), null);
                if (!ok.get())
                    FlushSounds();
                else
                    InitEffects();
            } else {
                FlushSounds();
                checks.Warn(true, Runtime.castToRef(memory.ADS("Error while reading file"), String.class), new Runtime.Ref<>(SamplesFile));
            }
            files.CloseFile(f);
        }
    }

    public void FlushSounds() {
        // VAR
        SoundList s = SoundList.CrashA;

        while (nbChan > 0) {
            nbChan--;
            { // WITH
                Channel _channel = channel[nbChan];
                sounds.FreeChannel(new Runtime.FieldRef<>(_channel::getChan, _channel::setChan));
            }
        }
        for (int _s = 0; _s < SoundList.values().length; _s++) {
            s = SoundList.values()[_s];
            { // WITH
                Snd _snd = soundList[s.ordinal()];
                if (_snd.wave != null)
                    sounds.FreeWave(new Runtime.FieldRef<>(_snd::getWave, _snd::setWave));
            }
        }
    }

    public final Runnable FlushSounds_ref = this::FlushSounds;

    private short BestChan(Object w, short volume, short stereo) {
        // VAR
        short bc = 0;
        short c = 0;
        short best = 0;
        short score = 0;

        bc = -1;
        best = 0;
        c = (short) nbChan;
        while (c > 0) {
            c--;
            { // WITH
                Channel _channel = channel[c];
                if (sounds.SndFinish(_channel.chan))
                    _channel.chanWave = null;
                if (_channel.chanWave == null)
                    score = 255;
                else if (_channel.chanWave == w)
                    score = (short) (320 - Math.abs(volume - _channel.chanVolume) - Math.abs(stereo - _channel.chanStereo) * 2);
                else
                    score = (short) (volume - _channel.chanVolume);
                if (score >= best) {
                    best = score;
                    bc = c;
                }
            }
        }
        if (bc != -1) {
            { // WITH
                Channel _channel = channel[bc];
                _channel.chanWave = w;
                _channel.chanVolume = volume;
                _channel.chanStereo = stereo;
            }
        }
        return bc;
    }

    private void PlaySound(SoundList s, short volume, short stereo, short balance) {
        // VAR
        short c = 0;
        int v = 0;

        v = volume;
        c = BestChan(soundList[s.ordinal()], (short) (v * (soundList[s.ordinal()].pri + 4) / 8), stereo);
        if (c != -1) {
            { // WITH
                Snd _snd = soundList[s.ordinal()];
                sounds.SndDo(channel[c].chan, (Memory.TagItem) memory.TAG7(Sounds.sWAVE, _snd.wave, Sounds.sLENGTH, _snd.size, Sounds.sRATE, _snd.rate, Sounds.sPRI, volume / 4, Sounds.sVOLUME, volume, Sounds.sSTEREO, stereo, Sounds.sBALANCE, balance));
            }
        }
    }

    private void QueueSound(int c, SoundList s, int freq, int time, short volume, short stereo) {
        // VAR
        Object w = null;
        int todo = 0;
        int len = 0;
        int sz = 0;
        int frq = 0;
        short vol = 0;

        if (c < nbChan) {
            vol = volume;
            { // WITH
                Snd _snd = soundList[s.ordinal()];
                w = _snd.wave;
                sz = (int) _snd.size;
                frq = _snd.rate * freq / 440;
                todo = frq * time / 500;
                while (todo > 0) {
                    if (todo >= sz)
                        len = sz;
                    else
                        len = todo;
                    todo -= len;
                    sounds.SndQueue(channel[c].chan, (Memory.TagItem) memory.TAG7(Sounds.sWAVE, w, Sounds.sLENGTH, len, Sounds.sRATE, frq, Sounds.sPRI, 16 - c, Sounds.sVOLUME, vol, Sounds.sSTEREO, stereo, Sounds.sBALANCE, 0));
                    if (EnumSet.of(SoundList.Gong, SoundList.HHat, SoundList.Canon1, SoundList.Canon2, SoundList.Pic, SoundList.Water, SoundList.Hit1, SoundList.Hit2).contains(s)) {
                        vol = 0;
                        w = soundList[SoundList.Gong.ordinal()].wave;
                        sz = (int) soundList[SoundList.Gong.ordinal()].size;
                    } else if ((s == SoundList.CrashA)) {
                        w = soundList[SoundList.Crash.ordinal()].wave;
                        sz = (int) soundList[SoundList.Crash.ordinal()].size;
                    }
                }
            }
        }
    }

    private void PlayEffect(/* var */ Effect[] effects, int vol, short stereo, short balance) {
        // VAR
        Memory.TagItem tags = null;
        short c = 0;
        short e = 0;
        int v = 0;
        int frq = 0;
        int todo = 0;
        int len = 0;
        boolean first = false;

        if (nbChan <= 0)
            return;
        first = true;
        c = BestChan(effects, (short) (vol * (effects[0].sound.pri + 4) / 8), stereo);
        if (c != -1) {
            for (e = 0; e <= (effects.length - 1); e++) {
                { // WITH
                    Effect _effect = effects[e];
                    frq = _effect.sound.rate * _effect.freq / 440;
                    v = (vol * _effect.volume + 255) / 256;
                    todo = _effect.length;
                    while (todo > 0) {
                        if (todo >= _effect.sound.size)
                            len = (int) _effect.sound.size;
                        else
                            len = todo;
                        todo -= len;
                        tags = (Memory.TagItem) memory.TAG7(Sounds.sWAVE, _effect.sound.wave, Sounds.sLENGTH, len, Sounds.sRATE, frq, Sounds.sPRI, v / 4, Sounds.sVOLUME, v, Sounds.sSTEREO, stereo, Sounds.sBALANCE, balance);
                        if (first) {
                            sounds.SndDo(channel[c].chan, tags);
                            first = false;
                        } else {
                            sounds.SndQueue(channel[c].chan, tags);
                        }
                    }
                }
            }
        }
    }

    public void Sound(OBJECT t1, OBJECT t2, short volume, short stereo, short balance) {
        if (t2 == OBJECT.BN) {
            switch (t1) {
                case K1 -> PlayEffect(ptEffect, volume, stereo, balance);
                case K0 -> PlayEffect(pt2Effect, volume, stereo, balance);
                case K2 -> PlayEffect(blvEffect, volume, stereo, balance);
                case K3 -> PlayEffect(atEffect, volume, stereo, balance);
                case EMPTY -> PlayEffect(bonus1Effect, volume, stereo, balance);
                case K4 -> PlaySound(SoundList.HHat, volume, stereo, balance);
                default -> throw new RuntimeException("Unhandled CASE value " + t1);
            }
        } else if (t2 == OBJECT.EMPTY) {
            switch (t1) {
                case PLAYER -> {
                    PlayEffect(lowCannon, volume, stereo, balance);
                    PlayEffect(low2Cannon, volume, stereo, balance);
                }
                case EMPTY -> {
                    PlayEffect(dieEffect1, volume, stereo, balance);
                    PlayEffect(dieEffect2, volume, stereo, balance);
                }
                case K0 -> {
                    PlayEffect(bumperEffect, volume, stereo, balance);
                }
                case K1, K2, K3 -> {
                    PlaySound(SoundList.Gong, volume, stereo, balance);
                }
                case K4 -> {
                    PlaySound(SoundList.Water, volume, stereo, balance);
                }
                case GN1 -> {
                    PlayEffect(turretEffect, volume, stereo, balance);
                }
                case GN2 -> {
                    PlayEffect(popupEffect, volume, stereo, balance);
                }
                case DL -> {
                    PlayEffect(createEffect, volume, stereo, balance);
                }
                case L2 -> {
                    PlaySound(SoundList.Cymbales, (short) (volume / 4), stereo, balance);
                }
                case BALL -> {
                    PlaySound(SoundList.Water, (short) (volume / 2), stereo, balance);
                }
                case PIC -> {
                    PlayEffect(metalEffect, volume, stereo, balance);
                }
                case NID, BUB -> {
                    PlayEffect(dieNestEffect, volume, stereo, balance);
                }
                case BN -> {
                    PlayEffect(bonus2Effect, volume, stereo, balance);
                }
                case BM -> {
                    PlaySound(SoundList.Pic, (short) (volume / 4), stereo, balance);
                }
                default -> {
                }
            }
        } else if (t1 == OBJECT.EMPTY) {
            switch (t2) {
                case L1 -> PlaySound(SoundList.Canon1, (short) (volume / 2), stereo, balance);
                case L2 -> PlaySound(SoundList.Canon2, (short) (volume / 3 * 2), stereo, balance);
                case L3 -> PlayEffect(fireEffect, volume, stereo, balance);
                default -> PlayEffect(createEffect, volume / 2, stereo, balance);
            }
        } else if (t1 == OBJECT.PLAYER) {
            switch (t2) {
                case GN1 -> {
                    PlaySound(SoundList.Hit2, (short) (volume / 4), stereo, balance);
                }
                case GN2 -> {
                    PlaySound(SoundList.Hit1, (short) (volume / 4), stereo, balance);
                }
                case PLAYER -> {
                    PlayEffect(gameEffect, volume, stereo, balance);
                }
                default -> {
                }
            }
        } else {
            switch (t2) {
                case L1, L3 -> PlaySound(SoundList.Cymbales, volume, stereo, balance);
                case K0 -> PlayEffect(highGong, volume, stereo, balance);
                case K1, K2, K3, K4 -> PlayEffect(aieEffect, volume, stereo, balance);
                case BM -> PlayEffect(bumper1Effect, volume, stereo, balance);
                default -> PlayEffect(picEffect, volume, stereo, balance);
            }
        }
    }

    public void GameOverSound() {
        PlayEffect(dieEffect1, 255, (short) 45, (short) 45);
        PlayEffect(dieEffect2, 255, (short) -45, (short) -45);
    }

    public void FinalMusic() {
        QueueSound(3, SoundList.Gong, 440, 3500, (short) 0, (short) -60);
        QueueSound(1, SoundList.Panflute, 660, 1000, (short) 180, (short) 180);
        QueueSound(0, SoundList.Gong, 660, 250, (short) 255, (short) 0);
        QueueSound(2, SoundList.HHat, 330, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 330, 125, (short) 180, (short) 60);
        QueueSound(0, SoundList.Gong, 524, 250, (short) 255, (short) 0);
        QueueSound(2, SoundList.HHat, 262, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 262, 125, (short) 180, (short) 60);
        QueueSound(0, SoundList.Gong, 588, 500, (short) 255, (short) 0);
        QueueSound(2, SoundList.HHat, 294, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 294, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 393, 250, (short) 180, (short) 60);
        QueueSound(1, SoundList.Panflute, 880, 500, (short) 180, (short) 180);
        QueueSound(0, SoundList.Gong, 880, 250, (short) 255, (short) 0);
        QueueSound(2, SoundList.HHat, 262, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 262, 125, (short) 180, (short) 60);
        QueueSound(0, SoundList.Gong, 700, 250, (short) 255, (short) 0);
        QueueSound(2, SoundList.HHat, 330, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 330, 125, (short) 180, (short) 60);
        QueueSound(0, SoundList.Panflute, 785, 333, (short) 180, (short) 180);
        QueueSound(1, SoundList.Gong, 785, 1000, (short) 255, (short) 0);
        QueueSound(2, SoundList.HHat, 392, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 350, 208, (short) 180, (short) 60);
        QueueSound(0, SoundList.Panflute, 524, 167, (short) 255, (short) 180);
        QueueSound(2, SoundList.CrashA, 330, 333, (short) 255, (short) 60);
        QueueSound(0, SoundList.Panflute, 588, 166, (short) 255, (short) 180);
        QueueSound(0, SoundList.Panflute, 660, 167, (short) 255, (short) 180);
        QueueSound(2, SoundList.CrashA, 330, 334, (short) 255, (short) 60);
        QueueSound(0, SoundList.Panflute, 785, 167, (short) 255, (short) 180);
        QueueSound(1, SoundList.Gong, 588, 1500, (short) 255, (short) 0);
        QueueSound(0, SoundList.Panflute, 588, 500, (short) 255, (short) 180);
        QueueSound(0, SoundList.Panflute, 588, 1000, (short) 255, (short) 180);
        QueueSound(2, SoundList.CrashA, 294, 500, (short) 255, (short) 60);
        QueueSound(3, SoundList.Water, 700, 125, (short) 200, (short) -60);
        QueueSound(3, SoundList.Water, 660, 125, (short) 200, (short) -60);
        QueueSound(3, SoundList.Water, 588, 125, (short) 200, (short) -60);
        QueueSound(3, SoundList.Water, 524, 125, (short) 200, (short) -60);
        QueueSound(2, SoundList.Hit1, 440, 125, (short) 200, (short) -120);
        QueueSound(2, SoundList.Hit2, 440, 125, (short) 200, (short) -120);
        QueueSound(2, SoundList.Hit1, 440, 750, (short) 200, (short) -120);
        QueueSound(1, SoundList.Gong, 660, 250, (short) 180, (short) 180);
        QueueSound(0, SoundList.Panflute, 660, 250, (short) 255, (short) 0);
        QueueSound(3, SoundList.Water, 495, 625, (short) 200, (short) -60);
        QueueSound(2, SoundList.HHat, 660, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 660, 125, (short) 180, (short) 60);
        QueueSound(1, SoundList.Gong, 660, 250, (short) 180, (short) 180);
        QueueSound(0, SoundList.Panflute, 524, 250, (short) 255, (short) 0);
        QueueSound(2, SoundList.HHat, 524, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 524, 125, (short) 180, (short) 60);
        QueueSound(1, SoundList.Gong, 588, 500, (short) 180, (short) 180);
        QueueSound(0, SoundList.Panflute, 588, 500, (short) 255, (short) 0);
        QueueSound(2, SoundList.HHat, 588, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 588, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 393, 250, (short) 180, (short) 60);
        QueueSound(3, SoundList.Pic, 440, 250, (short) 240, (short) -60);
        QueueSound(3, SoundList.Pic, 440, 250, (short) 240, (short) -60);
        QueueSound(1, SoundList.Gong, 880, 250, (short) 180, (short) 180);
        QueueSound(0, SoundList.Panflute, 880, 250, (short) 255, (short) 0);
        QueueSound(2, SoundList.HHat, 524, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 524, 125, (short) 180, (short) 60);
        QueueSound(3, SoundList.Pic, 440, 250, (short) 240, (short) -60);
        QueueSound(1, SoundList.Gong, 880, 250, (short) 180, (short) 180);
        QueueSound(0, SoundList.Panflute, 700, 250, (short) 255, (short) 0);
        QueueSound(2, SoundList.HHat, 660, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 660, 125, (short) 180, (short) 60);
        QueueSound(3, SoundList.Pic, 440, 250, (short) 240, (short) -60);
        QueueSound(0, SoundList.Gong, 785, 333, (short) 180, (short) 180);
        QueueSound(1, SoundList.Panflute, 785, 1000, (short) 255, (short) 0);
        QueueSound(2, SoundList.HHat, 784, 125, (short) 180, (short) 60);
        QueueSound(2, SoundList.HHat, 700, 208, (short) 180, (short) 60);
        QueueSound(3, SoundList.Pic, 440, 250, (short) 240, (short) -60);
        QueueSound(3, SoundList.Pic, 440, 250, (short) 240, (short) -60);
        QueueSound(0, SoundList.Gong, 524, 167, (short) 255, (short) 180);
        QueueSound(2, SoundList.Canon1, 660, 333, (short) 255, (short) 60);
        QueueSound(0, SoundList.Gong, 588, 166, (short) 255, (short) 180);
        QueueSound(0, SoundList.Gong, 660, 167, (short) 255, (short) 180);
        QueueSound(2, SoundList.Canon1, 660, 334, (short) 255, (short) 60);
        QueueSound(3, SoundList.Pic, 440, 250, (short) 240, (short) -60);
        QueueSound(3, SoundList.Pic, 440, 125, (short) 240, (short) -60);
        QueueSound(0, SoundList.Gong, 785, 167, (short) 255, (short) 180);
        QueueSound(1, SoundList.Panflute, 524, 500, (short) 255, (short) 0);
        QueueSound(0, SoundList.Gong, 440, 2000, (short) 255, (short) 0);
        QueueSound(2, SoundList.Canon2, 220, 2000, (short) 200, (short) 60);
        QueueSound(3, SoundList.Water, 880, 125, (short) 200, (short) -60);
        QueueSound(3, SoundList.Water, 110, 1500, (short) 240, (short) -60);
    }

    private void Init() {
        // VAR
        int c = 0;
        SoundList s = SoundList.CrashA;

        for (c = 0; c <= 15; c++) {
            channel[c].chan = sounds.noChan;
        }
        for (int _s = 0; _s < SoundList.values().length; _s++) {
            s = SoundList.values()[_s];
            soundList[s.ordinal()].wave = null;
        }
        nbChan = 0;
    }


    // Support

    private static GrotteSounds instance;

    public static GrotteSounds instance() {
        if (instance == null)
            new GrotteSounds(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
        Init();
        checks.AddTermProc(FlushSounds_ref);
        InitSounds();
    }

    public void close() {
    }

}
