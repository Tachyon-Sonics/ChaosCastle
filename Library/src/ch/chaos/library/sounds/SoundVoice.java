package ch.chaos.library.sounds;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import ch.chaos.library.sounds.resample.IResampledAudioWave;
import ch.chaos.library.sounds.resample.LinearResampledWave;
import ch.chaos.library.sounds.resample.SincResampledWave;

/**
 * A sound voice, capable of playing one {@link SoundWave} at a time, and being
 * controlled by {@link SoundControls}.
 * <p>
 * In general, polyphony is achieved using a {@link SoundMixer}, that can combine
 * multiple {@link SoundVoice}.
 * <p>
 * This class handles a queue of {@link SoundControls} and {@link SoundWave}, and
 * resamples the result at {@link SoundMixer#SAMPLE_RATE} for mixing with other
 * instances.
 * <p>
 * This class also allows a {@link SoundControls} to be applied immediately on the
 * currently playing {@link SoundWave} using {@link #control(SoundControls)}.
 */
public class SoundVoice {

    private static final boolean SINC_RESAMPLING = false;

    private final Queue<ISoundData> soundDataQueue = new ConcurrentLinkedQueue<>();

    private SoundWave currentSoundWave; // Original
    private IResampledAudioWave currentAudioWave; // Resampled view
    private final SoundControls currentControls;
    private boolean rateChanged;
    private final Object lock = new String("SoundVoice.Lock");

    // State variables used to change levels progressively
    private float currentLevel = 0.5f; // volume * correction
    private float currentLeftLevel = 1.0f; // based on balance
    private float currentRightLevel = 1.0f;


    public SoundVoice() {
        currentControls = new SoundControls();
        currentControls.fillDefaults();
    }

    public void enqueue(ISoundData effect) {
        soundDataQueue.add(effect);
    }

    public void control(SoundControls controls) {
        synchronized (lock) {
            double prevRate = currentControls.getModulatedRate();
            currentControls.updateFrom(controls);
            if (currentControls.getModulatedRate() != prevRate)
                rateChanged = true;
        }
    }

    /**
     * Play the current {@link SoundWave} into the given buffer, applying
     * any {@link SoundControls}, either queued by {@link #enqueue(ISoundData)},
     * or applied on the fly by {@link #control(SoundControls)}.
     */
    public void playInto(float[][] buffer) {
        if (buffer.length != 2)
            throw new IllegalArgumentException();
        int bufferSize = buffer[0].length;
        int offset = 0;
        while (offset < bufferSize) {
            // Apply any enqueued controls and retrieve next sound wave
            if (currentAudioWave == null) {
                ISoundData nextData = soundDataQueue.poll();

                while (nextData instanceof SoundControls) {
                    // Update any controls
                    SoundControls controls = (SoundControls) nextData;
                    if (controls != null) {
                        synchronized (lock) {
                            this.currentControls.updateFrom(controls);
                        }
                    }
                    nextData = soundDataQueue.poll();
                }

                // Get next wave if any
                if (nextData != null) {
                    synchronized (lock) {
                        currentSoundWave = (SoundWave) nextData;
                        rebuildResampledWave();
                        currentSoundWave.setOffset(0);
                    }
                }
            }

            SoundControls controls = null;
            IResampledAudioWave audioWave = null;
            if (currentAudioWave != null) {
                // Apply current sound controls
                synchronized (lock) {
                    controls = currentControls.clone();
                    if (rateChanged) {
                        double ratio = (double) SoundMixer.SAMPLE_RATE / controls.getModulatedRate();
                        currentAudioWave.setResampleRatio(ratio);
                        rateChanged = false;
                    }
                    audioWave = currentAudioWave;
                }
            }
            if (audioWave != null) {
                int stereo = controls.getStereo();
                float leftLevel = 1.0f;
                float rightLevel = 1.0f;
                if (stereo < 0) {
                    rightLevel = (float) (stereo + 90) / 90.0f;
                } else if (stereo > 0) {
                    leftLevel = (float) (90 - stereo) / 90.0f;
                }

                if (audioWave.getPosition() == 0) {
                    // It is safe to change levels abruptly when moving from one wave to the next
                    currentLevel = controls.getVolume() * controls.getAm();
                    currentLeftLevel = leftLevel;
                    currentRightLevel = rightLevel;
                }

                // Play as much as possible from current sound wave
                while (offset < bufferSize && audioWave.getPosition() < audioWave.getLength()) {
                    float sampleValue = audioWave.getValue();

                    float nextLevel = controls.getVolume() * controls.getAm();
                    currentLevel = sweepTo(currentLevel, nextLevel);
                    currentLeftLevel = sweepTo(currentLeftLevel, leftLevel);
                    currentRightLevel = sweepTo(currentRightLevel, rightLevel);

                    // Apply volume
                    sampleValue *= currentLevel;

                    // Mix, applying balance
                    buffer[0][offset] = sampleValue * currentLeftLevel;
                    buffer[1][offset] = sampleValue * currentRightLevel;

                    // Next sample
                    offset++;
                }

                if (audioWave.getPosition() >= audioWave.getLength()) {
                    // Finished with the current sound wave. Remove it from the queue
                    synchronized (lock) {
                        currentAudioWave = null;
                    }
                }
            } else {
                // Queue drained. Play silence up to the requested size
                for (int chan = 0; chan < 2; chan++) {
                    Arrays.fill(buffer[chan], offset, bufferSize, 0.0f);
                }
                offset = bufferSize;
            }
        }
    }

    /**
     * Drain any playing / enqeued wave immediately
     */
    public void drain() {
        synchronized (lock) {
            soundDataQueue.clear();
            currentAudioWave = null;
            rateChanged = false;
        }
    }

    /**
     * Adjust the given current level value progressively to the target value.
     * <p>
     * This method is used to prevent audible clicks that would occur if the
     * levels are modified abruptly.
     * @return the adjusted level
     */
    private static float sweepTo(float currentValue, float targetValue) {
        return currentValue * 0.999f + targetValue * 0.001f;
    }

    public boolean isActive() {
        synchronized (lock) {
            return !soundDataQueue.isEmpty() || currentAudioWave != null;
        }
    }

    public SoundControls getCurrentControls() {
        synchronized (lock) {
            return currentControls.clone();
        }
    }

    private void rebuildResampledWave() {
        double resampleRatio = (double) SoundMixer.SAMPLE_RATE / currentControls.getModulatedRate();
        if (SINC_RESAMPLING) {
            currentAudioWave = new SincResampledWave(currentSoundWave, resampleRatio);
        } else {
            currentAudioWave = new LinearResampledWave(currentSoundWave, resampleRatio);
        }
        rateChanged = false;
    }
}
