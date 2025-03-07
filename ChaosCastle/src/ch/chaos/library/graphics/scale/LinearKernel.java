package ch.chaos.library.graphics.scale;

public class LinearKernel extends Kernel {

    private final int length;


    public LinearKernel(int length) {
        this.length = length;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    protected float getImpl(int x, int y) {
        float distance = (float) Math.hypot((float) x, (float) y);
        if (distance >= length)
            return 0.0f;
        return ((float) length - distance) / (float) length;
    }

}
