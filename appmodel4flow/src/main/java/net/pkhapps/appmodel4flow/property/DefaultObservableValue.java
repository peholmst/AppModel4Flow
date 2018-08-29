package net.pkhapps.appmodel4flow.property;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Default implementation of {@link ObservableValue} that considers {@code null} values to be {@link #isEmpty() empty}.
 * Developers are free to use as-is or extend. However, in most cases they should not expose instances of this class
 * directly to other classes but use the {@link ObservableValue interface} instead.
 *
 * @param <T> the value type.
 */
@NotThreadSafe
@SuppressWarnings("WeakerAccess")
public class DefaultObservableValue<T> extends AbstractObservableValue<T> {

    private T value;

    /**
     * Creates a new, empty {@code DefaultObservableValue}.
     */
    public DefaultObservableValue() {
    }

    /**
     * Creates a new {@code DefaultObservableValue} with the given value.
     *
     * @param value the initial value, may be {@code null}.
     */
    public DefaultObservableValue(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    /**
     * Sets the value, notifying the listeners if the new value is different from the old one.
     *
     * @param value the new value to set.
     */
    public void setValue(T value) {
        if (!Objects.equals(this.value, value)) {
            var old = this.value;
            this.value = value;
            fireValueChangeEvent(old, value);
        }
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public String toString() {
        return String.format("%s@%x[value=%s]", getClass().getSimpleName(), hashCode(), value);
    }
}
