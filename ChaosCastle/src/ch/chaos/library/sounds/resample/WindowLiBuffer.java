package ch.chaos.library.sounds.resample;

/**
 * {@link LinearInterpolatedBuffer} filled with a Hann window function. The buffer
 * is accessed with interpolation using a floating index with {@link #get(float)}.
 * It can also be accessed without interpolation at integer indexes with {@link #get(int)}.
 */
public class WindowLiBuffer extends LinearInterpolatedBuffer {

    private final float scaleFactor;


    public WindowLiBuffer(int nbSamplesPerZeroCross, int nbZeroCross) {
        // We pad with (nbSamplesPerZeroCross / 2) + 2 additional zeroes (to avoid range checking)
        super(nbSamplesPerZeroCross * nbZeroCross + (nbSamplesPerZeroCross / 2) + 2);
        this.scaleFactor = (float) nbSamplesPerZeroCross / (float) Math.PI;
        float[] window = new float[nbSamplesPerZeroCross * nbZeroCross * 2];
        fillHannWindow(window);
        for (int i = 0; i < super.getSize(); i++) {
            float windowCoeff = 0.0f;
            if (i <= window.length / 2)
                windowCoeff = window[window.length / 2 - i];
            super.set(i, windowCoeff);
        }
    }

    static void fillHannWindow(float[] window) {
        final float[] HannCoefs = { 0.5f, -0.25f };
        for (int i = 0; i < window.length; i++) {
            double x = Math.PI * 2.0 * (double) i / (double) window.length;
            double val = HannCoefs[0];
            for (int c = 1; c < HannCoefs.length; c++)
                val += (HannCoefs[c] * 2.0f * Math.cos(x * (double) c));
            window[i] = (float) val;
        }
    }

    @Override
    public float get(float angle) {
        float index = angle * scaleFactor;
        if (index < 0.0f)
            return super.get(-index);
        else
            return super.get(index);
    }

    @Override
    public float get(int index) {
        if (index < 0)
            return super.get(-index);
        else
            return super.get(index);
    }

}
