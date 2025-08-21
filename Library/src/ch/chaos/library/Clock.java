package ch.chaos.library;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import ch.chaos.library.Input.Event;
import ch.pitchtech.modula.runtime.Runtime;

public class Clock {

    private final static long MAX_SKEW = 200000000L;

    private static Clock instance;


    private Clock() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Clock instance() {
        if (instance == null)
            new Clock(); // will set 'instance'
        return instance;
    }

    // TYPE


    public static interface TimePtr { // Opaque type
    }

    @FunctionalInterface
    public static interface TimeCallBackProc { // PROCEDURE Type

        public void invoke(TimePtr arg1, Object arg2);
    }

    // VAR


    public TimePtr noTime;


    public TimePtr getNoTime() {
        return this.noTime;
    }

    public void setNoTime(TimePtr noTime) {
        this.noTime = noTime;
    }

    // IMPL


    private static class Time implements TimePtr {

        private final int period;
        private long startTime;


        public Time(int period) {
            this.period = period;
        }

    }


    private final List<Runnable> idleListeners = new ArrayList<>();
    private final PriorityQueue<Long> timeEvents = new PriorityQueue<>();
    
    private long vsyncTime;
    private long vsyncExpiration;
    private long refreshPeriod;


    public void addIdleListener(Runnable listener) {
        idleListeners.add(listener);
    }

    public void removeIdleListener(Runnable listener) {
        idleListeners.remove(listener);
    }

    public TimePtr AllocTime(int period) {
        return new Time(period);
    }

    public void StartTime(TimePtr t) {
        ((Time) t).startTime = System.nanoTime();
    }
    
    /**
     * Forces the clock to the given value until the given expiration time.
     * <p>
     * Both values use the same time as {@link System#nanoTime()}.
     * <p>
     * This method can be used to replace usage of {@link System#nanoTime()}
     * for a short period. See the caller for explanations.
     * @param vsyncTime the clock value to use until the expiration time
     * @param expires the expiration time until which the value must be used
     */
    public void setVsyncTime(long vsyncTime, long expires, long refreshPeriod) {
        this.vsyncTime = vsyncTime;
        this.vsyncExpiration = expires;
        this.refreshPeriod = refreshPeriod;
    }
    
    private long nanoTime() {
        long result = System.nanoTime();
        if (result < vsyncExpiration)
            result = vsyncTime;
        else if (vsyncTime > 0 && refreshPeriod > 0) {
            // Round to refresh period
            long elapsed = result - vsyncTime;
            elapsed -= (elapsed % refreshPeriod);
            result = vsyncTime + elapsed;
        }
        return result;
    }

    public boolean WaitTime(TimePtr t0, long delay) {
        for (Runnable listener : idleListeners)
            listener.run();

        Time t = (Time) t0;
        long now = System.nanoTime();
        long deadline = t.startTime + delay * 1000000000L / t.period;
        if (now < deadline) {
            long toSleep = deadline - now;
            try {
                Thread.sleep(toSleep / 1000000, (int) (toSleep % 1000000));
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            t.startTime = deadline;
            return true;
        } else {
            if (deadline + MAX_SKEW >= now) {
                t.startTime = deadline;
            } else {
                t.startTime = now;
            }
            return false;
        }
    }


    private Thread timeThread;
    private boolean timeThreadEnabled;


    public void TimeEvent(TimePtr t, long delay) {
        Time time = (Time) t;
        long now = System.nanoTime();
        long deadline = now + delay * 1000000000L / time.period;
        synchronized (timeEvents) {
            timeEvents.add(deadline);
            timeEvents.notify();
        }
    }

    private void timeEventLoop() {
        while (true) {
            synchronized (timeEvents) {
                while (timeEvents.isEmpty()) {
                    try {
                        timeEvents.wait();
                    } catch (InterruptedException ex) {

                    }
                }
                if (!timeThreadEnabled)
                    break;
                long now = System.nanoTime();
                long next = timeEvents.peek();
                if (next >= now) {
                    // Fire event now
                    next = timeEvents.remove();
                    Event event = new Event();
                    event.type = Input.eTIMER;
                    Input.instance().queueEvent(event);
                } else {
                    // Sleep until next event, or until we are notified
                    assert next < now;
                    long toSleep = now - next;
                    try {
                        timeEvents.wait(toSleep / 1000000, (int) (toSleep % 1000000));
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    if (!timeThreadEnabled)
                        break;
                }
            }
        }
    }

    public boolean TimeCall(TimePtr t, long delay, TimeCallBackProc proc, Object data) {
        // not used
        throw new UnsupportedOperationException("Not implemented: TimeCall");
    }

    public long GetTime(TimePtr t0) {
        Time t = (Time) t0;
        long elapsed = nanoTime() - t.startTime;
        return elapsed * t.period / 1000000000L;
    }

    public void FreeTime(TimePtr t) {

    }

    public void GetCurrentTime(/* VAR */ Runtime.IRef<Integer> h,
            /* VAR */ Runtime.IRef<Integer> m,
            /* VAR */ Runtime.IRef<Integer> s) {
        LocalTime localTime = LocalTime.now();
        h.set(localTime.getHour());
        m.set(localTime.getMinute());
        s.set(localTime.getSecond());
    }

    public long GetNewSeed() {
        return Trigo.instance().RND();
    }

    public void begin() {
        timeThreadEnabled = true;
        timeThread = new Thread(this::timeEventLoop, "Time Event Loop");
        timeThread.setDaemon(true);
        timeThread.start();
    }

    public void close() {
        synchronized (timeEvents) {
            timeThreadEnabled = false;
            timeEvents.clear();
            timeEvents.notify();
        }
        if (timeThread != null) {
            try {
                timeThread.join(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            timeThread = null;
        }
    }
}
