package ch.chaos.library.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Used when one thread eventually produces a result (using {@link #submit(Object)}),
 * and another thread must wait for and get that result (using {@link #retrieve()}).
 * <p>
 * The first thread is typically the EDT, and the other one is the main thread.
 */
public class Async<V> {

    private final BlockingQueue<V> exchanger = new ArrayBlockingQueue<>(1);


    public void submit(V value) {
        exchanger.add(value);
    }

    /**
     * Wait for the other thread to submit the value using {@link #submit(Object)},
     * and then retrieve it.
     */
    public V retrieve() {
        try {
            return exchanger.take();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
