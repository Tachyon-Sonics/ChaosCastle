package ch.chaos.library.sounds;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Audio mixer, using a dedicated Thread.
 * <p>
 * This class can mix sounds comming from one or more {@link SoundVoice}.
 * <p>
 * This class only does the mixing. Resampling to the common sample rate {@link #SAMPLE_RATE}
 * is done by the individual {@link SoundVoice}. {@link SoundVoice#playInto(float[][])}
 * is used to provide audio that has been properly resampled to {@link #SAMPLE_RATE}.
 * <p>
 * Controls (volume, balance, etc) are also applied on, and handled by {@link SoundVoice}.
 */
public class SoundMixer {

    public final static int SAMPLE_RATE = 48000;
    private final static int NB_CHANS = 2;
    private final static int NB_BITS = 16;
    private final static int BYTES_PER_SAMPLE = NB_BITS / 8;
    private final static int AUDIO_BUFFER_SIZE = 1024; // In sample frames
    private final static int MIXING_BUFFER_SIZE = 128; // Some controls such as rate and balance are only applied between audio blocks of this size

    private final List<SoundVoice> voices;
    private final Thread mixingThread;
    private final float correction; // amplitude correction to compensate for the number of voices
    private final AtomicBoolean enabled = new AtomicBoolean();
    private final Exchanger<Void> ready = new Exchanger<>();

    private final static long DUMP_PERIOD = TimeUnit.SECONDS.toMillis(1);
    private long lastDumpTime = System.currentTimeMillis();
    private float peakValue;
    private int peakPolyphony;


    public SoundMixer(int nbVoices) {
        this.voices = new ArrayList<>();
        for (int i = 0; i < nbVoices; i++) {
            voices.add(new SoundVoice());
        }
        correction = 1.0f / (float) nbVoices;
        enabled.set(true);
        mixingThread = new Thread(this::mixLoop, "SoundMixer Loop");
        mixingThread.start();

        // Wait for the mixing thread to be ready
        try {
            ready.exchange(null);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public SoundVoice getVoice(int voiceIndex) {
        return voices.get(voiceIndex);
    }

    private void mixLoop() {
        AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, NB_BITS, NB_CHANS, true, false);
        SourceDataLine sink;
        try {
            sink = AudioSystem.getSourceDataLine(audioFormat);
            sink.open(audioFormat, AUDIO_BUFFER_SIZE * 2 * BYTES_PER_SAMPLE);
        } catch (LineUnavailableException ex) {
            throw new RuntimeException(ex);
        }

        // Prepare buffers
        float[][] voiceBuffer = new float[NB_CHANS][MIXING_BUFFER_SIZE];
        float[][] mixingBuffer = new float[NB_CHANS][MIXING_BUFFER_SIZE];
        short[] interleavedBuffer = new short[MIXING_BUFFER_SIZE * NB_CHANS];
        ByteBuffer byteBuffer = ByteBuffer.allocate(MIXING_BUFFER_SIZE * NB_CHANS * BYTES_PER_SAMPLE);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();

        // Send one buffer of initial silence and start
        byte[] silence = new byte[AUDIO_BUFFER_SIZE * NB_CHANS * BYTES_PER_SAMPLE];
        sink.write(silence, 0, silence.length);
        silence = null;
        sink.start();

        // Notify we are ready
        try {
            ready.exchange(null);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        // Main mixing loop
        while (enabled.get()) {
            // Mix all voices
            for (int chan = 0; chan < NB_CHANS; chan++) {
                Arrays.fill(mixingBuffer[chan], 0.0f);
            }
            for (SoundVoice voice : voices) {
                voice.playInto(voiceBuffer);

                for (int chan = 0; chan < NB_CHANS; chan++) {
                    for (int i = 0; i < MIXING_BUFFER_SIZE; i++) {
                        mixingBuffer[chan][i] += voiceBuffer[chan][i] * correction;
                    }
                }
            }

            // Convert float[][] to interleaved short[] format
            for (int chan = 0; chan < NB_CHANS; chan++) {
                for (int i = 0; i < MIXING_BUFFER_SIZE; i++) {
                    float fValue = mixingBuffer[chan][i];
                    float aValue = Math.abs(fValue);
                    if (aValue > peakValue)
                        peakValue = aValue;
                    if (fValue > 1.0f)
                        fValue = 1.0f;
                    else if (fValue < -1.0f)
                        fValue = -1.0f;
                    short sValue = (short) (fValue * 32767.0f);
                    interleavedBuffer[i * NB_CHANS + chan] = sValue;
                }
            }

            // Play the shit
            shortBuffer.put(0, interleavedBuffer);
            sink.write(byteBuffer.array(), 0, MIXING_BUFFER_SIZE * NB_CHANS * BYTES_PER_SAMPLE);

//            dumpStats();
        }
        sink.drain();
        sink.stop();
        sink.close();
    }

    public boolean isActive() {
        for (SoundVoice voice : voices) {
            if (voice.isActive())
                return true;
        }
        return false;
    }

    public int getPolyphony() {
        int result = 0;
        for (SoundVoice voice : voices) {
            if (voice.isActive())
                result++;
        }
        return result;
    }

    void dumpStats() {
        int polyphony = getPolyphony();
        peakPolyphony = Math.max(polyphony, peakPolyphony);

        long now = System.currentTimeMillis();
        if (now > lastDumpTime + DUMP_PERIOD) {
            System.out.println("Polyphony: " + peakPolyphony + "; peak: " + peakValue);
            peakValue = 0.0f;
            peakPolyphony = 0;
            lastDumpTime = now;
        }
    }

    public void dispose() {
        enabled.set(false);
        try {
            mixingThread.join(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    // Tests

//    static void testSimpleMixing() throws InterruptedException {
//        SoundMixer mixer = new SoundMixer(8);
//        SoundVoice voice = mixer.getVoice(0);
//
//        SoundControls controls = new SoundControls();
//        controls.setRate(48000.0);
//        voice.enqueue(controls);
//
//        {
//            float[] waveData = new float[48000];
//            SoundWave wave = new SoundWave();
//            wave.setWave(waveData);
//            wave.setLength(48000);
//            voice.enqueue(wave);
//        }
//
//        {
//            float[] waveData = new float[48000];
//            Random rnd = new Random();
//            for (int i = 0; i < waveData.length; i++)
//                waveData[i] = rnd.nextFloat() / 5.0f;
//            SoundWave wave = new SoundWave();
//            wave.setWave(waveData);
//            wave.setLength(48000);
//            wave.setOffset(0);
//            voice.enqueue(wave);
//        }
//
//        {
//            voice = mixer.getVoice(1);
//            SoundControls ctrls = new SoundControls();
//            double rate = 22000.0; // TODO review with very low value such as 1000 and sinc (volume lost)
//            ctrls.setRate(rate);
//            voice.enqueue(ctrls);
//
//            double freq = 440.0f;
//            double period = rate / freq;
//            double duration = 2.0f; // s
//            float[] waveData = new float[(int) (rate * duration)];
//            for (int i = 0; i < waveData.length; i++) {
//                waveData[i] = (float) Math.sin((double) i * Math.PI * 2.0 / period);
//            }
//            SoundWave wave = new SoundWave(waveData);
//            voice.enqueue(wave);
//
//            Thread.sleep(1000);
//            ctrls = new SoundControls();
//            ctrls.setRate(rate * 1.25f);
//            voice.control(ctrls);
//        }
//
//        Thread.sleep(2000);
//        mixer.dispose();
//    }
//
//    static void testFmModulation() throws InterruptedException {
//        SoundMixer mixer = new SoundMixer(8);
//        SoundVoice voice = mixer.getVoice(0);
//
//        {
//            SoundControls ctrls = new SoundControls();
//            double rate = 22000.0f;
//            ctrls.setRate(rate);
//            voice.enqueue(ctrls);
//
//            double freq = 440.0;
//            double period = rate / freq;
//            double duration = 4.0; // s
//            float[] waveData = new float[(int) (rate * duration)];
//            for (int i = 0; i < waveData.length; i++) {
//                waveData[i] = (float) Math.sin((double) i * Math.PI * 2.0 / period);
//            }
//            SoundWave wave = new SoundWave(waveData);
//            voice.enqueue(wave);
//
//            final int Steps = 20;
//            Thread.sleep(1000);
//            long lastTime = System.nanoTime();
//            for (int k = 0; k < 10; k++) {
//                for (int i = 0; i < Steps; i++) {
//                    double sin = Math.sin((double) i * Math.PI * 2.0 / (double) Steps);
//                    double fm = 1.0 + (sin / 8.0);
//                    ctrls = new SoundControls();
//                    ctrls.setRate(rate * fm);
//                    ctrls.setVolume((float) (0.5 + sin / 2.0));
////                    ctrls.setBalance((int) (sin * 90.0));
//                    voice.control(ctrls);
//
//                    long now = System.nanoTime();
//                    long next = lastTime + (250000000L / Steps);
//                    if (next > now)
//                        Thread.sleep((next - now) / 1000000, (int) ((next - now) % 1000000));
//                    lastTime = next;
//                }
//                ctrls = new SoundControls();
//                ctrls.setRate(rate);
//                ctrls.setVolume(0.5f);
//                voice.control(ctrls);
//            }
//        }
//
//        while (mixer.isActive()) {
//            Thread.sleep(100);
//        }
//        mixer.dispose();
//    }
//
//    public static void main(String[] args) throws InterruptedException {
//        testFmModulation();
//    }

}
