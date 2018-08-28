package net.pkhapps.appmodel4flow.property;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.util.ListenerCollection;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Default implementation of {@link ObservableValue} that considers {@code null} values to be {@link #isEmpty() empty}.
 * Developers are free to use as-is or extend.
 *
 * @param <T> the value type.
 */
@NotThreadSafe
@SuppressWarnings("WeakerAccess")
public class DefaultObservableValue<T> implements ObservableValue<T> {

    private ListenerCollection<ValueChangeEvent<T>> valueChangeEventListeners;
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

    public void setValue(T value) {
        if (!Objects.equals(this.value, value)) {
            var old = this.value;
            this.value = value;
            if (valueChangeEventListeners != null) {
                valueChangeEventListeners.fireEvent(new ValueChangeEvent<>(this, old, value));
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Nonnull
    @Override
    public Registration addValueChangeListener(@Nonnull SerializableConsumer<ValueChangeEvent<T>> listener) {
        return getValueChangeEventListeners().addListener(listener);
    }

    @Override
    public void addWeakValueChangeListener(@Nonnull SerializableConsumer<ValueChangeEvent<T>> listener) {
        getValueChangeEventListeners().addWeakListener(listener);
    }

    @Nonnull
    private ListenerCollection<ValueChangeEvent<T>> getValueChangeEventListeners() {
        if (valueChangeEventListeners == null) {
            valueChangeEventListeners = new ListenerCollection<>();
        }
        return valueChangeEventListeners;
    }
}
