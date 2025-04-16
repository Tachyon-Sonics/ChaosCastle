package ch.chaos.library.graphics.scale;

public class SincKernel extends Kernel {

    private final int length;
    private final int nbZeroCross;
    private final float[][] values;


    public SincKernel(int length, int nbZeroCross) {
        if (length % nbZeroCross != 0)
            throw new IllegalArgumentException();
        this.length = length;
        this.nbZeroCross = nbZeroCross;
        this.values = new float[length][length];
        init();
    }

    private void init() {
        float[] window = new float[length];
        fillHannWindow(window);
        int norm = length / nbZeroCross;
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < length; y++) {
                float distance = (float) Math.hypot((float) x, (float) y);
                float sinc = sinc(distance / norm);
                int winIndex = (int) (distance + 0.5);
                float win = (winIndex < window.length ? window[winIndex] : 0.0f);
                values[x][y] = sinc * win;
            }
        }
    }

    static void fillHannWindow(float[] window) {
        final float[] HannCoefs = { 0.5f, -0.25f };
        for (int i = 0; i < window.length; i++) {
            double x = Math.PI * (1.0 + (double) i / (double) window.length);
            double val = HannCoefs[0];
            for (int c = 1; c < HannCoefs.length; c++)
                val += (HannCoefs[c] * 2.0f * Math.cos(x * (double) c));
            window[i] = (float) val;
        }
    }

    @Override
    public int getLength() {
        return length;
    }

    private static float sinc(float angle) {
        if (angle == 0.0f)
            return 1.0f;
        return (float) (Math.sin(Math.PI * angle) / (Math.PI * angle));
    }

    @Override
    public float getImpl(int x, int y) {
        return values[x][y];
    }

}
