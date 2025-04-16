package ch.chaos.library.sounds.resample;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ch.chaos.library.sounds.IAudioWave;
import ch.chaos.library.sounds.SoundWave;

/**
 * Sweepable sinc resampling.
 * TODO Paula: https://www.kvraudio.com/forum/viewtopic.php?t=166876
 *  and https://www.linkedin.com/pulse/sound-innovation-exploring-amigas-paula-audio-circuit-luigi-c-filho-zo1mf
 * PAL: 3546895, NTSC: 3579545, period = %N / rate
 */
public class SincResampler {

    private final SincLiBuffer sincBuffer;
    private final WindowLiBuffer windowBuffer;
    private final int nbZeroCross;
    private final float antiAliasing;
    private float stretch;
    private double step;
    private float amplitudeCorrection;

    // Optimization consts
    private final static float fPI = (float) Math.PI;
    private final static float fPIhalf = fPI / 2.0f + 0.001f;

    // State
    private double srcOffset;

    // Buffers cache


    private static record BufferKey(int nbSamplesPerZeroCross, int nbZeroCross) {

    }


    private final static Map<BufferKey, SincLiBuffer> sincBuffersCache = new ConcurrentHashMap<>();
    private final static Map<BufferKey, WindowLiBuffer> windowsCache = new ConcurrentHashMap<>();


    /**
     * @param nbSamplesPerZeroCross numbre of sample between zero crossing for the sinc function
     * @param nbZeroCross number of zero cross (on one side) for the sinc function
     */
    public SincResampler(int nbSamplesPerZeroCross, int nbZeroCross, float antiAliasing, double ratio) {
        this.sincBuffer = getSincBuffer(nbSamplesPerZeroCross, nbZeroCross);
        this.windowBuffer = getWindowBuffer(nbSamplesPerZeroCross, nbZeroCross);
        this.nbZeroCross = nbZeroCross;
        this.antiAliasing = antiAliasing;
        setRatio(ratio);
    }

    private static SincLiBuffer getSincBuffer(int nbSamplesPerZeroCross, int nbZeroCross) {
        BufferKey key = new BufferKey(nbSamplesPerZeroCross, nbZeroCross);
        return sincBuffersCache.computeIfAbsent(key, (k) -> new SincLiBuffer(nbSamplesPerZeroCross, nbZeroCross));
    }

    private static WindowLiBuffer getWindowBuffer(int nbSamplesPerZeroCross, int nbZeroCross) {
        BufferKey key = new BufferKey(nbSamplesPerZeroCross, nbZeroCross);
        return windowsCache.computeIfAbsent(key, (k) -> new WindowLiBuffer(nbSamplesPerZeroCross, nbZeroCross));
    }

    public void setRatio(double ratio) {
        this.step = 1.0 / ratio;
        amplitudeCorrection = (float) Math.min(ratio, 1.0);
        if (ratio != 1.0 && antiAliasing != 0.0)
            amplitudeCorrection *= (1.0 - antiAliasing) + (antiAliasing * getBandWidth(nbZeroCross));
        // When upsampling, sinc is not stretched and lowpass filter is half the source sample rate
        // When downsampling, stretch the sinc so that lowpass filter is half the target sample rate
        stretch = 1.0f / amplitudeCorrection;
        assert stretch >= 1.0f;
    }

    static float getBandWidth(int nbZeroCross) {
        /*
         * Approx: Without window (or rect window), we have 1 / (nbZeroCross * 2 + 1)
         * We assume window widens main lobe width by 4 (quite accurate for 100dB rejection)
         * 1 is added instead of 0.25 to allow some aliasing when nbZeroCross is small
         */
        float rollOff = 1.0f / ((float) nbZeroCross / 2.0f + 1.0f);
        return 1.0f - rollOff;
    }

    /**
     * Resample the given audio wave.
     * <p>
     * This method will produce as much as possible, and stop either if <tt>targetLength</tt>
     * samples have been processed, or if the end of the wave has been reached.
     * <p>
     * In the first case, the result is <tt>targetLength</tt>, {@link #isEndReached(IAudioWave)}
     * is <tt>false</tt>, and {@link #getWaveOffset()} can be used as <tt>waveOffset</tt> on a next
     * call to this method to continue resampling the same wave.
     * <p>
     * In the second case, the returned result is less than <tt>targetLength</tt> and
     * {@link #isEndReached(IAudioWave)} is <tt>true</tt>.
     * @param wave the audio wave to resample
     * @param waveOffset the offset (in samples) in the audio wave to start from
     * @param target the buffer to fill with the resampled version
     * @param targetOffset the starting offset into the buffer
     * @param targetLength the maximum number of samples to produce
     * @return the number of samples produced.
     */
    public int resample(IAudioWave wave, double waveOffset, float[] target, int targetOffset, int targetLength) {
        double srcIndex = waveOffset;
        int dstIndex;
        for (dstIndex = 0; dstIndex < targetLength; dstIndex++) {
            int srcIntIndex = (int) (srcIndex + 0.5); // Nearest index in wave
            if (srcIntIndex > wave.getLength()) {
                break;
            }

            // Deviation from index (used as sample offset in the sinc function)
            float sincShift = (float) (srcIndex - srcIntIndex) * fPI;
            assert sincShift >= -fPIhalf && sincShift <= fPIhalf;

            // Convolve source sample[srcIntIndex]
            float result = 0.0f;
            float sincOffset = (float) -nbZeroCross * fPI + sincShift;
            for (int offset = -nbZeroCross; offset <= nbZeroCross; offset++) {
                float sample = wave.getValue(srcIntIndex - offset);
                float sinc = sincBuffer.get(sincOffset / stretch);
                float window = windowBuffer.get(sincOffset);
                result += sample * sinc * window;
                sincOffset += fPI;
            }
            target[targetOffset + dstIndex] = result * amplitudeCorrection;

            srcIndex += step;
        }
        srcOffset = srcIndex;
        return dstIndex;
    }

    public boolean isEndReached(IAudioWave wave) {
        int srcIntIndex = (int) (srcOffset + 0.5);
        return srcIntIndex > wave.getLength();
    }

    public double getWaveOffset() {
        return srcOffset;
    }

    public static void main(String[] args) {
        float[] square1 = new float[100];
        for (int i = 0; i < 80; i++) {
            square1[i] = (i < 40 ? -0.5f : 0.5f);
        }
        square1[90] = 1.0f;
        IAudioWave audioWave = new SoundWave(square1);

//        {
//            IAudioWave square4 = new SincResampledWave(new SquareExpandedWave(audioWave, 16), 4.0 / 16.0);
//            System.out.println(square4.getLength());
//
//            for (int index = 0; index < square4.getLength(); index++) {
//                float value = square4.getValue(index);
//                int k = (int) ((value + 1.0f) * 60.0f + 0.5f);
//                for (int i = 0; i <= 120; i++) {
//                    if (i == k)
//                        System.out.print("*");
//                    else if (i == 60)
//                        System.out.print("|");
//                    else
//                        System.out.print(" ");
//                }
//                System.out.println(":" + value + " @ " + index);
//            }
//        }

        {
            IAudioWave square4 = new SincResampledWave(audioWave, 4.0);
            System.out.println(square4.getLength());

            for (int index = 0; index < square4.getLength(); index++) {
                float value = square4.getValue(index);
                int k = (int) ((value + 1.0f) * 60.0f + 0.5f);
                for (int i = 0; i <= 120; i++) {
                    if (i == k)
                        System.out.print("*");
                    else if (i == 60)
                        System.out.print("|");
                    else
                        System.out.print(" ");
                }
                System.out.println(":" + value + " @ " + index);
            }
        }

        {
            IAudioWave squareH = new SincResampledWave(audioWave, 0.5);
            System.out.println(squareH.getLength());

            for (int index = 0; index < squareH.getLength(); index++) {
                float value = squareH.getValue(index);
                int k = (int) ((value + 1.0f) * 60.0f + 0.5f);
                for (int i = 0; i <= 120; i++) {
                    if (i == k)
                        System.out.print("*");
                    else if (i == 60)
                        System.out.print("|");
                    else
                        System.out.print(" ");
                }
                System.out.println(":" + value + " @ " + index);
            }
        }

    }

}
