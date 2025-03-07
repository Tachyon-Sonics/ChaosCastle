package ch.chaos.library.sounds.resample;

/**
 * {@link LinearInterpolatedBuffer} filled with the sinc function. The buffer
 * is accessed with interpolation using a floating index with {@link #get(float)}.
 * It can also be accessed without interpolation at integer indexes with {@link #get(int)}.
 */
public class SincLiBuffer extends LinearInterpolatedBuffer {

    /**
     * Multiplier that converts from an angle to an offset in the buffer
     */
    private final float scaleFactor;


    /**
     * Create a new buffer with a sinc function, not windowed
     */
    public SincLiBuffer(int nbSamplesPerZeroCross, int nbZeroCross) {
        super(nbSamplesPerZeroCross * nbZeroCross + (nbSamplesPerZeroCross / 2) + 2);
        this.scaleFactor = (float) nbSamplesPerZeroCross / (float) Math.PI;
        for (int i = 0; i < super.getSize(); i++) {
            float angle = (float) i / (float) nbSamplesPerZeroCross;
            super.set(i, sinc(angle));
        }
    }

    private float sinc(float angle) {
        if (angle == 0.0f)
            return 1.0f;
        return (float) (Math.sin(Math.PI * angle) / (Math.PI * angle));
    }

    @Override
    public float get(float angle) {
        float index = angle * scaleFactor;
        if (index < 0.0f)
            return super.get(-index);
        else
            return super.get(index);
    }

    public float getPreScaled(float angle) {
        return super.get(angle);
    }

    @Override
    public float get(int index) {
        if (index < 0)
            return super.get(-index);
        else
            return super.get(index);
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

}
