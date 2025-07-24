package ch.chaos.library.utils;

/**
 * Provide sleeping that is more accurate than {@link Thread#sleep(long, int)}.
 * <p>
 * {@link Thread#sleep(long, int)} always sleeps at least for the given amount of time, however
 * it seems that it can sleep up to about 3 or 4 ms more than requested (probably because of OS
 * context switch). When used as a soft vsync (where we typically have to sleep less than 16.6 ms)
 * this can be sufficient to induce jitter and jagged scrolling.
 * <p>
 * This class continuously monitors by how much {@link Thread#sleep(long)} oversleeps at most.
 * It then multiplies the measured value by a configurable margin ({@link #SECURITY}). This resulting
 * delay will be substracted to the delay that is passed to {@link Thread#sleep(long)}. The reminder
 * delay will then use <i>active sleeping</i> (busy loop).
 * <p>
 * Not that active sleeping uses 100% CPU (one core). This class hence tries to balance CPU usage between
 * minimal ({@link Thread#sleep(long)} alone) and maximal (active sleeping only).
 * <p>
 * If a request to sleep less than the measured active sleeping delay if performed (for instance
 * if the game loop takes close to the frame rate's period), only active sleeping will be used.
 * <p>
 * A single instance must be created and reused for proper operation. It is ok to have one
 * instance per thread if threads have different priorities.
 */
public class AccurateSleeper {
    
    private final static int KEEP_MAX_FOR = 60;
    private final static double MAX_DECAY = 0.999; // About 50% after 600 frames, or 10 seconds
    
    /**
     * If Thread.sleep() suddenly oversleeps by more than the currently calculated value
     * multiplied by this constant, we have to assume it is an isolated event, like a full gc,
     * or some weird stuff from the OS temporarily blocking our app.
     */
    private final static double MIN_DUBIOUS_JUMP = 5.0;
    
    /**
     * In that case, instead of using the dubious oversleep as a new maximum, we just multiply
     * the current maximum by this constant. The idea is to prevent the maximum from reaching
     * values so large that it may take minutes / hours to fall again to a normal value.
     * <p>
     * We still do a reasonable increase in case we have a system-wide change, like turing on
     * energy saver, which may result in significantly changing the avergae oversleep (although
     * I never observed that...). Mind that the given increase may occur repeteadly if there
     * are multiple consecutive dubious oversleeps.
     */
    private final static double DUBIOUS_INCREASE_RATIO = 1.1;
    
    
    private final double security;
    private long curMax;
    private int countDown;
    
    
    public AccurateSleeper(double security) {
        this.security = security;
        initialProbe();
    }
    
    private void initialProbe() {
        curMax = doInitialProbe();
        
        /*
         * There is a small chance that some weird event (gc, OS context switch) occured during our
         * initial probe. Hence we perfor additional probes and takes the minimum. Overall, this
         * will evaluate to min(5 max(10 probes))
         */
        for (int k = 0; k < 5; k++) {
            long max = doInitialProbe();
            if (max < curMax)
                curMax = max;
        }
        
        /*
         * Initial probe is performed under light CPU load, unlike the game loop.
         * Hence add some security to it...
         */
        curMax *= 2.0;
        
        countDown = KEEP_MAX_FOR;
    }
    
    private long doInitialProbe() {
        long result = 0;
        for (int k = 0; k < 10; k++) {
            long start = System.nanoTime();
            threadSleep(1);
            long stop = System.nanoTime();
            long drift = stop - start - 1;
            if (drift > result)
                result = drift;
        }
        return result;
    }
    
    /**
     * Sleep using less accurate {@link Thread#sleep(long)}. Used outside of the game loop,
     * like in the shop, where accuracy is less important. This will also use less CPU.
     * <p>
     * Note that this method will not gather oversleeping statistics for {@link #sleep(long)}.
     */
    public static void threadSleep(long nano) {
        try {
            Thread.sleep(nano / 1000000, (int) (nano % 1000000));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Sleep accurately. Use in the game loop.
     */
    public void sleep(long nano) {
        long start = System.nanoTime();
        long busyWait = (long) ((double) curMax * security);
        if (busyWait > nano)
            busyWait = nano;
        long sleep = nano - busyWait;
        
        // Sleep
        if (sleep > 0) {
            threadSleep(sleep);
            long stop = System.nanoTime();
            
            // Update statistics
            long drift = stop - sleep - start;
            if (drift > curMax * MIN_DUBIOUS_JUMP) {
                // The drift is way more than current calculated may. Assume an isolated event like gc and partially ignore
                System.out.println("Dubious oversleep: " + (drift / 1000) + "us, was " + (curMax / 1000) + "us; ratio: " + ((double) drift / (double) curMax + " - smoothing out"));
                curMax *= DUBIOUS_INCREASE_RATIO;
                countDown = KEEP_MAX_FOR;
            } else if (drift > curMax) {
//                if ((double) drift > (double) curMax * security)
//                    System.out.println("New max: " + (drift / 1000) + "us, was " + (curMax / 1000) + "us; ratio: " + ((double) drift / (double) curMax));
                curMax = drift;
                countDown = KEEP_MAX_FOR;
            } else if ((double) drift * security > curMax) {
                // Stabilize here
                curMax = (long) ((double) drift * security);
            } else if (countDown > 0) {
                countDown--;
            } else {
                // Slowly decrease
                curMax = (long) ((double) curMax * MAX_DECAY);
            }
        }

        // Busy wait
        while (System.nanoTime() < start + nano) {
            
        }
    }
    
}
