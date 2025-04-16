package ch.chaos.library.sounds.resample;

/**
 * Array of floats, which can be accessed using a float index (result
 * is interpolated linearly between the two closest values)
 */
public class LinearInterpolatedBuffer {

    private final float[] buffer;


    public LinearInterpolatedBuffer(int size) {
        this.buffer = new float[size];
    }

    public int getSize() {
        return buffer.length;
    }

    public void set(int index, float value) {
        buffer[index] = value;
    }

    void mult(int index, float value) {
        buffer[index] *= value;
    }

    public float get(int index) {
        return buffer[index];
    }

    public float get(float index) {
        assert index >= 0.0f;
        // Avoid Math.floor() that is ultra-slow because of the handling of negative numbers
        int floor = (int) index;
        float w2 = (float) index - floor;
        float v1 = buffer[floor];
        float v2 = buffer[floor + 1];
        return v1 + (v2 - v1) * w2;
    }

}
