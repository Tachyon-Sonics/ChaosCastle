package ch.chaos.library.graphics.xbrz;

import java.util.ArrayList;
import java.util.List;

/**
 * Help decompose scale factors greater than 6 into multiple scale factors.
 * <p>
 * Not all scale factors can be expressed. {@link #getNearestScale(int)} gives
 * the highest scale factor that can be expressed and that is less than or equal
 * to the given one.
 */
public class XbrzHelper {
    
    private final static int MIN_SCALE = 2;
    private final static int MAX_SCALE = 6;
    
    
    public static int getNearestScale(int targetScale) {
        List<Integer> scales = getScales(targetScale);
        int result = 1;
        for (int scale : scales) {
            result *= scale;
        }
        return result;
    }
    
    public static List<Integer> getScales(int targetScale) {
        if (targetScale == 1) {
            return List.of(1);
        }
        
        int initScale = targetScale;
        List<Integer> result = new ArrayList<>();
        while (targetScale > 1) {
            // First try with a root
            int nthRoot = 1;
            int scale = (int) Math.round(Math.pow(targetScale, 1.0 / (double) nthRoot));
            while (scale > MAX_SCALE) {
                nthRoot++;
                scale = (int) Math.round(Math.pow(targetScale, 1.0 / (double) nthRoot));
            }
            
            // Then iterate on available scales
            if (targetScale % scale != 0) {
                for (scale = MAX_SCALE; scale >= MIN_SCALE; scale--) {
                    if (targetScale % scale == 0)
                        break;
                }
            }
            
            if (scale >= MIN_SCALE && scale <= MAX_SCALE && (targetScale % scale == 0)) {
                result.add(scale);
                targetScale = targetScale / scale;
            } else {
                // targetScale is not divisible by any of the supported scales.
                // try again with next lower scale
                return getScales(initScale - 1);
            }
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        for (int k = 1; k < 50; k++) {
            int target = getNearestScale(k);
            List<Integer> scales = getScales(k);
            System.out.println("" + k + " -> " + target + ": " + scales);
        }
    }

}
