package net.pkhapps.appmodel4flow.property;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.util.ListenerCollection;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Base class for implementations of {@link ObservableValue}.
 *
 * @param <T> the value type.
 */
@NotThreadSafe
public abstract class AbstractObservableValue<T> implements ObservableValue<T> {

    private ListenerCollection<ValueChangeEvent<T>> valueChangeEventListeners;

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

    /**
     * Fires a {@link net.pkhapps.appmodel4flow.property.ObservableValue.ValueChangeEvent} to all registered listeners.
     *
     * @param old   the old value.
     * @param value the current (new) value.
     */
    @SuppressWarnings("WeakerAccess")
    protected void fireValueChangeEvent(T old, T value) {
        if (valueChangeEventListeners != null) {
            valueChangeEventListeners.fireEvent(new ValueChangeEvent<>(this, old, value));
        }
    }
}
