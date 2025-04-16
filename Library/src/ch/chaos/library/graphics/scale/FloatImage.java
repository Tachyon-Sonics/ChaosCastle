package ch.chaos.library.graphics.scale;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FloatImage {

    private final static float CIRCULARITY = 0.0f;

    private final int width;
    private final int height;
    private final float[][] data;


    public FloatImage(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new float[width][height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float get(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return 0.0f;
        return data[x][y];
    }

    public float getExt(int x, int y) {
        float correction = 1.0f;
        while (x < 0) {
            x += width;
            correction *= CIRCULARITY;
        }
        while (x >= width) {
            x -= width;
            correction *= CIRCULARITY;
        }
        while (y < 0) {
            y += height;
            correction *= CIRCULARITY;
        }
        while (y >= height) {
            y -= height;
            correction *= CIRCULARITY;
        }
        return data[x][y] * correction;
    }

    public void set(int x, int y, float value) {
        data[x][y] = value;
    }

    public void add(int x, int y, float value) {
        float correction = 1.0f;
        while (x < 0) {
            x += width;
            correction *= CIRCULARITY;
        }
        while (x >= width) {
            x -= width;
            correction *= CIRCULARITY;
        }
        while (y < 0) {
            y += height;
            correction *= CIRCULARITY;
        }
        while (y >= height) {
            y -= height;
            correction *= CIRCULARITY;
        }
        data[x][y] += value * correction;
    }

    @Override
    public String toString() {
        NumberFormat formatter = new DecimalFormat("+#0.000;-#0.000");
        StringBuilder result = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float value = get(x, y);
                result.append(formatter.format(value) + " ");
            }
            result.append("\n");
        }
        return result.toString();
    }

}
