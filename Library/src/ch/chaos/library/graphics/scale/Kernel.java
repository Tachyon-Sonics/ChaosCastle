package ch.chaos.library.graphics.scale;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public abstract class Kernel {

    public abstract int getLength();

    public final float get(int x, int y) {
        if (x < 0)
            x = -x;
        if (y < 0)
            y = -y;
        int length = getLength();
        if (x >= length || y >= length)
            return 0.0f;
        return getImpl(x, y);
    }

    protected abstract float getImpl(int x, int y);

    @Override
    public String toString() {
        NumberFormat formatter = new DecimalFormat("+#0.000;-#0.000");
        int length = getLength();
        StringBuilder result = new StringBuilder();
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < length; x++) {
                result.append(formatter.format(get(x, y)) + " ");
            }
            result.append("\n");
        }
        return result.toString();
    }
}
