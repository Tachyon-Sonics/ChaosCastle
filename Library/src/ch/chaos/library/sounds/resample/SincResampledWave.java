package ch.chaos.library.sounds.resample;

import java.util.Arrays;

import ch.chaos.library.sounds.IAudioWave;

/**
 * {@link IAudioWave} implementation that is a resampled version of another {@link IAudioWave}.
 * <p>
 * Resampling is performed on-demand by small blocks, as {@link #getValue(int)} is invoked.
 * <p>
 * Windowed sinc resampling is used.
 */
public class SincResampledWave implements IAudioWave, IResampledAudioWave {

    /**
     * Amount of audio sample to produce at once.
     * Sample rate changes are only applied between audio blocks of this size.
     * Hence this is a compromise between performance (large block) and
     * latency (small block).
     */
    private final static int BLOCK_SIZE = 32;

    private final IAudioWave source;
    private double length;
    private float[] target;
    private final SincResampler resampler;
    private double ratio;
    private Double nextRatio;

    private double srcIndex = 0.0;
    private int dstIndex = 0;
    private int position = 0;


    public SincResampledWave(IAudioWave source, double ratio) {
        this.source = source;
        this.length = (double) source.getLength() * ratio;
        this.target = new float[(int) (length * 2 + 0.5)]; // margin to allow changing resample ratio without enlarging the buffer
        this.ratio = ratio;
        int nbZeroCRoss = nbZeroCrossFor(ratio);
        resampler = new SincResampler(64, nbZeroCRoss, 1.0f, ratio);
    }

    private static int nbZeroCrossFor(double ratio) {
        int result = 4;
        while (ratio < 1.0) {
            // The idea is to return only powers of two, to minimize the number of different sinc windows we have to create
            ratio *= 2.0;
            result *= 2;
        }
        return result;
    }

    @Override
    public void setResampleRatio(double ratio) {
        // Enqueue the change so that it is applied on the next generated buffer
        this.nextRatio = ratio;
    }

    @Override
    public double getResampleRatio() {
        return this.ratio;
    }

    private void applyNextResampleRatio() {
        if (this.nextRatio != null) {
            double previous = this.ratio;
            this.ratio = this.nextRatio;
            this.nextRatio = null;
            resampler.setRatio(ratio);

            double remaining = length - dstIndex;
            remaining = remaining * ratio / previous;
            length = dstIndex + remaining;
            ensureEnoughCapacity();
        }
    }

    private void ensureEnoughCapacity() {
        int length = getLength();
        if (target.length < length) {
            float[] enlarged = new float[length * 2];
            System.arraycopy(target, 0, enlarged, 0, target.length);
            target = enlarged;
        }
    }

    @Override
    public int getLength() {
        return (int) (length + 0.5);
    }

    @Override
    public float getValue(int index) {
        if (index < 0 || index >= length)
            return 0.0f;
        ensureResampledTo(index + 1);
        return target[index];
    }

    private void ensureResampledTo(int index) {
        if (dstIndex >= index || dstIndex > length)
            return;
        applyNextResampleRatio();
        int toIndex = Math.max(index, dstIndex + BLOCK_SIZE);
        int length = getLength();
        if (toIndex > length)
            toIndex = length;
        int amount = toIndex - dstIndex;
        int written = resampler.resample(source, srcIndex, target, dstIndex, amount);
        if (written < amount) {
            /*
             * Pad with silence.
             * This should not happen, unless there was a roundoff error...
             */
            Arrays.fill(target, dstIndex + written, dstIndex + amount, 0.0f);
        }
        dstIndex += amount;
        srcIndex = resampler.getWaveOffset();
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public float getValue() {
        return getValue(position++);
    }

}
