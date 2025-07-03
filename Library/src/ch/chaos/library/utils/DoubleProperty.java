package ch.chaos.library.utils;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public interface DoubleProperty {

    public double getValue();

    public void setValue(double value);

    public void addListener(Runnable listener);

    public void removeListener(Runnable listener);

    public static DoubleProperty create(DoubleSupplier getValue, DoubleConsumer setValue, Consumer<Runnable> addListener, Consumer<Runnable> removeListener) {
        return new DoubleProperty() {

            @Override
            public double getValue() {
                return getValue.getAsDouble();
            }

            @Override
            public void setValue(double value) {
                setValue.accept(value);
            }

            @Override
            public void addListener(Runnable listener) {
                addListener.accept(listener);
            }

            @Override
            public void removeListener(Runnable listener) {
                removeListener.accept(listener);
            }

        };
    }

}
