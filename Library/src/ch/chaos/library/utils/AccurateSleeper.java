package ch.chaos.library.utils;


public class AccurateSleeper { // TODO (0) review, javadoc
    
    private final static int KEEP_MAX_FOR = 60;
    private final static double MAX_DECAY = 0.999; // About 50% after 600 frames, or 10 seconds
    private final static double SECURITY = 1.5;
    
    private long curMax;
    private int countDown;
    
    
    public AccurateSleeper() {
        initialProbe();
    }
    
    private void initialProbe() {
        for (int k = 0; k < 10; k++) {
            long start = System.nanoTime();
            threadSleep(1);
            long stop = System.nanoTime();
            long drift = stop - start - 1;
            if (drift > curMax)
                curMax = drift;
        }
        countDown = KEEP_MAX_FOR;
    }
    
    public static void threadSleep(long nano) {
        try {
            Thread.sleep(nano / 1000000, (int) (nano % 1000000));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public void sleep(long nano) {
        long start = System.nanoTime();
        long busyWait = (long) ((double) curMax * SECURITY);
        if (busyWait > nano)
            busyWait = nano;
        long sleep = nano - busyWait;
        
        // Sleep
        if (sleep > 0) {
            threadSleep(sleep);
            long stop = System.nanoTime();
            
            // Update statistics
            long drift = stop - sleep - start;
            if (drift > curMax) {
//                System.out.println("New max: " + (drift / 1000) + "us, was " + (curMax / 1000) + "us; ratio: " + ((double) drift / (double) curMax));
                curMax = drift;
                countDown = KEEP_MAX_FOR;
            } else if ((double) drift * SECURITY > curMax) {
                // Stabilize here
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
