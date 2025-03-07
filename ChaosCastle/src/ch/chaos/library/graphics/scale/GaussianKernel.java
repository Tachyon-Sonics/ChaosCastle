package ch.chaos.library.graphics.scale;

public class GaussianKernel extends Kernel {

    private final int length;
    private final float[][] values;


    public GaussianKernel(int length, int mult) {
        this.length = length;
        this.values = new float[length][length];
        init(mult);
    }

    private void init(float mult) {
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < length; y++) {
                float distance = (float) Math.hypot((float) x, (float) y);
                float value = gauss(distance / mult);
                values[x][y] = value;
            }
        }
    }

    private float gauss(float value) {
        return (float) Math.exp(-value * value);
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public float getImpl(int x, int y) {
        return values[x][y];
    }

}
