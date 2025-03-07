package ch.chaos.library.graphics.scale;

public class BicubicKernel extends Kernel {

    private final int length;
    private final float[][] values;


    public BicubicKernel(int length) {
        this.length = length;
        this.values = new float[length][length];
        init();
    }

    private void init() {
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < length; y++) {
                float d = (float) Math.hypot((float) x, (float) y);
                d = d * 2.0f / (float) length; // [0..2]
                float value = 0.0f;
                float d2 = d * d;
                float d3 = d2 * d;
                if (d <= 1.0f) {
                    value = 1.5f * d3 - 2.5f * d2 + 1.0f;
                } else if (d < 2.0f) {
                    value = -0.5f * d3 + 2.5f * d2 - 4.0f * d + 2.0f;
                }
                values[x][y] = value;
            }
        }
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    protected float getImpl(int x, int y) {
        return values[x][y];
    }

}
