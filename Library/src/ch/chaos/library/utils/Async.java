package ch.chaos.library.utils;

import java.util.concurrent.Exchanger;

/**
 * Used when one thread eventually produces a result (using {@link #submit(Object)}),
 * and another thread must wait for and get that result (using {@link #retrieve()}).
 * <p>
 * The first thread is typically the EDT, and the other one is the main thread.
 * <p>
 * Warning: does not work with a single thread.
 */
public class Async<V> {

    private final Exchanger<V> exchanger = new Exchanger<>();


    public void submit(V value) {
        try {
            exchanger.exchange(value);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Wait for the other thread to submit the value using {@link #submit(Object)},
     * and then retrieve it.
     */
    public V retrieve() {
        try {
            return exchanger.exchange(null);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
