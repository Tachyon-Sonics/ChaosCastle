package ch.chaos.library.sounds.resample;

import ch.chaos.library.sounds.IAudioWave;

/**
 * {@link IAudioWave} implementation that is a resampled version of another {@link IAudioWave}.
 * <p>
 * Resampling is performed on-the-fly, as {@link #getValue(int)} is invoked.
 * <p>
 * Linear resampling is used.
 * <p>
 * Resampling ratio can be changed on the fly.
 */
public class LinearResampledWave implements IAudioWave, IResampledAudioWave {

    private final IAudioWave source;
    private double ratio;
    private int length;

    private int position;


    public LinearResampledWave(IAudioWave source, double ratio) {
        this.source = source;
        setResampleRatio(ratio);
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public float getValue(int index) {
        double srcIndex = (double) index / ratio;
        int srcIndex1 = (int) srcIndex;
        int srcIndex2 = srcIndex1 + 1;
        float weight2 = (float) (srcIndex - (double) srcIndex1);
        float weight1 = 1.0f - weight2;
        return source.getValue(srcIndex1) * weight1 + source.getValue(srcIndex2) * weight2;
    }

    @Override
    public void setResampleRatio(double ratio) {
        double previous = this.ratio;
        this.ratio = ratio;
        this.length = (int) Math.ceil(source.getLength() * ratio);
        this.position = (int) (position * ratio / previous + 0.5);
    }

    @Override
    public double getResampleRatio() {
        return this.ratio;
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
