package ch.chaos.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.chaos.library.sounds.ControlledWave;
import ch.chaos.library.sounds.SimilarSoundsDetector;
import ch.chaos.library.sounds.SoundControls;
import ch.chaos.library.sounds.SoundMixer;
import ch.chaos.library.sounds.SoundVoice;
import ch.chaos.library.sounds.SoundWave;
import ch.pitchtech.modula.runtime.Runtime;
import ch.pitchtech.modula.runtime.Runtime.IRef;

public class Sounds {

    private static Sounds instance;


    private Sounds() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Sounds instance() {
        if (instance == null)
            new Sounds(); // will set 'instance'
        return instance;
    }

    // CONST


    public static final int sLENGTH = Memory.tagUser + 0;
    public static final int sWAVE = Memory.tagUser + 1;
    public static final int sOFFSET = Memory.tagUser + 2;
    public static final int sDURATION = Memory.tagUser + 4;
    public static final int sDISTANCE = Memory.tagUser + 6;
    public static final int sNOTE = Memory.tagUser + 8;
    public static final int sFREQ = Memory.tagUser + 9;
    public static final int sRATE = Memory.tagUser + 10;
    public static final int sFM = Memory.tagUser + 12;
    public static final int sVOLUME = Memory.tagUser + 16;
    public static final int sAM = Memory.tagUser + 18;
    public static final int sSTEREO = Memory.tagUser + 22;
    public static final int sBALANCE = Memory.tagUser + 23;
    public static final int sPRI = Memory.tagUser + 24;
    public static final int sWATER = Memory.tagUser + 28;
    public static final int sNUMCHANS = Memory.tagUser + 32;

    // TYPE


    public static interface ChannelPtr { // Opaque type
    }

    // VAR


    public ChannelPtr noChan;


    public ChannelPtr getNoChan() {
        return this.noChan;
    }

    public void setNoChan(ChannelPtr noChan) {
        this.noChan = noChan;
    }


    private final static int NB_CHANNELS = 8;

    private Map<short[], float[]> waves = new HashMap<>();
    private List<Channel> channels = new ArrayList<>();
    private SoundMixer mixer;
    private SimilarSoundsDetector similarSoundsDetector;


    private class Channel implements ChannelPtr {

        private final int voiceIndex;


        public Channel(int voiceIndex) {
            this.voiceIndex = voiceIndex;
        }

        public int getVoiceIndex() {
            return voiceIndex;
        }

    }


    public void GetSoundsSysAttr(/* VAR */ Memory.TagItem what) {
        int result = switch (what.tag) {
            case sNUMCHANS -> NB_CHANNELS;
            case sSTEREO -> 1;
            default -> throw new IllegalArgumentException("Unexpected value: " + what.tag);
        };
        what.data = result;
    }

    public ChannelPtr AllocChannel(Memory.TagItem tags) {
        if (channels.size() >= NB_CHANNELS)
            return null;
        Channel result = new Channel(channels.size());
        channels.add(result);
        return result;
    }

    public void SndDo(ChannelPtr chan, Memory.TagItem params) {
        sndProcess(chan, params, true);
    }

    public void SndQueue(ChannelPtr chan, Memory.TagItem params) {
        sndProcess(chan, params, false);
    }

    private void sndProcess(ChannelPtr chan, Memory.TagItem params, boolean immediate) {
        Channel channel = (Channel) chan;
        SoundVoice soundVoice = mixer.getVoice(channel.getVoiceIndex());

        // Read arguments
        short[] shortWave = (short[]) Memory.tagObject(params, sWAVE, null);
        int length = Memory.tagInt(params, sLENGTH, -1);
        int offset = Memory.tagInt(params, sOFFSET, 0);
        Integer rate = Memory.tagInteger(params, sRATE);
        Integer volume = Memory.tagInteger(params, sVOLUME);
        Integer fm = Memory.tagInteger(params, sFM);
        Integer am = Memory.tagInteger(params, sAM);
        Integer stereo = Memory.tagInteger(params, sSTEREO);
        Integer balance = Memory.tagInteger(params, sBALANCE);

        // Create SoundControls
        SoundControls controls = null;
        if (rate != null || volume != null || fm != null || am != null || stereo != null || balance != null) {
            controls = new SoundControls();
            if (rate != null)
                controls.setRate((double) rate);
            if (volume != null)
                controls.setVolume((float) volume / 255.0f);
            if (fm != null)
                controls.setFm((double) fm / 256.0);
            if (am != null)
                controls.setAm((float) (am / 128.0f));
            if (stereo != null) {
                /*
                 * Handle -181 and 181 that are supposed to play exclusively on the left or right channel
                 * and allow sharing the same stereo channel. Here we do not use channel sharing, but we
                 * have 8 channels, which should be enough anyway.
                 */
                if (stereo < -180) {
                    stereo = -90;
                } else if (stereo > 180) {
                    stereo = 90;
                }
                controls.setStereo(stereo);
            }
            if (balance != null)
                controls.setBalance(balance);
        }

        // Create SoundWave
        SoundWave soundWave = null;
        if (shortWave != null) {
            float[] wave = waves.get(shortWave);
            soundWave = new SoundWave(wave);
            if (length > 0) {
                soundWave.setLength(length);
            } else {
                soundWave.setLength(wave.length);
            }
            soundWave.setOffset(offset);
        }

        if (immediate) {
            if (soundWave != null) {
                // Drain so we can play the new sound wave immediately
                soundVoice.drain();
                // Detect similarity with a recent sound
                ControlledWave cWave = new ControlledWave(soundWave, controls);
                float correction = similarSoundsDetector.submit(cWave);
                soundVoice.setCorrection(correction);
                // Apply controls if any
                if (controls != null)
                    soundVoice.enqueue(controls);
                // Then send wave
                soundVoice.enqueue(soundWave);
            } else {
                // No wave specified. Just apply any controls immediately
                if (controls != null)
                    soundVoice.control(controls);
            }
        } else {
            // Enqueue controls if any
            if (controls != null)
                soundVoice.enqueue(controls);
            // Enqueue wave if any
            if (soundWave != null)
                soundVoice.enqueue(soundWave);
        }
    }

    public boolean SndFinish(ChannelPtr chan) {
        Channel channel = (Channel) chan;
        return !mixer.getVoice(channel.getVoiceIndex()).isActive();
    }

    public void SndGet(ChannelPtr chan, /* VAR */ Memory.TagItem what) {
        Channel channel = (Channel) chan;
        SoundVoice soundVoice = mixer.getVoice(channel.getVoiceIndex());
        SoundControls controls = soundVoice.getCurrentControls();
        if (what.tag == sSTEREO) {
            what.lint = controls.getStereo();
        } else if (what.tag == sVOLUME) {
            what.lint = (int) (controls.getVolume() * 255.0f + 0.5f);
        } else if (what.tag == sBALANCE) {
            what.lint = controls.getBalance();
        } else if (what.tag == sRATE) {
            what.lint = (int) (controls.getRate() + 0.5);
        } else if (what.tag == sAM) {
            what.lint = (int) (controls.getAm() * 128.0f + 0.5f);
        } else if (what.tag == sFM) {
            what.lint = (int) (controls.getFm() * 256.0 + 0.5);
        } else {
            throw new UnsupportedOperationException("Not implemented: SndGet " + what);
        }
    }

    public void FreeChannel(IRef<ChannelPtr> chan) {
        Channel channel = (Channel) chan.get();
        if (channel != null)
            channels.remove(channel);
        chan.set(null);
    }

    public Object AllocWave(long size) {
        return new short[(int) size];
    }

    public void FreeWave(/* VAR */ Runtime.IRef<Object> wave) {
        if (wave.get() != null) {
            short[] shortData = (short[]) wave.get();
            waves.remove(shortData);
        }
        wave.set(null);
    }

    public void ConvertWave(/* VAR */ Runtime.IRef<Object> wave, long size) {
        short[] shortData = (short[]) wave.get();
        float[] floatData = new float[shortData.length];
        for (int i = 0; i < shortData.length; i++) {
            int value = (byte) shortData[i];
            float sample = (float) value / 128.0f;
            floatData[i] = sample;
        }
        waves.put(shortData, floatData);
    }

    public void begin() {
        mixer = new SoundMixer(NB_CHANNELS);
        similarSoundsDetector = new SimilarSoundsDetector();
    }

    public void close() {
        if (mixer != null) {
            mixer.dispose();
            mixer = null;
            similarSoundsDetector = null;
        }
    }
}
