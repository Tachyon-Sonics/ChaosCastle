package ch.chaos.library.sounds;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SimilarSoundsDetector {
    
    private static final long SIMILARITY_TIME = TimeUnit.MILLISECONDS.toNanos(20);
    
    private final List<ControlledWave> waves = new LinkedList<>();

    
    public float submit(ControlledWave controlledWave) {
        long now = System.nanoTime();
        
        // Drain old entries
        for (Iterator<ControlledWave> iter = waves.iterator(); iter.hasNext(); ) {
            ControlledWave wave = iter.next();
            if (wave.time() + SIMILARITY_TIME < now) {
                iter.remove();
            }
        }
        
        // Look for a similar entry
        int nbSimilar = 0;
        for (ControlledWave wave : waves) {
            if (wave.isSimilarTo(controlledWave)) {
                nbSimilar++;
            }
        }
        
        // Add new entry
        waves.add(controlledWave);
        
        // Return correction
        if (nbSimilar == 0) {
            return 1.0f;
        } else {
            return 1.0f / (float) Math.sqrt((double) nbSimilar + 1.0);
        }
    }
}
