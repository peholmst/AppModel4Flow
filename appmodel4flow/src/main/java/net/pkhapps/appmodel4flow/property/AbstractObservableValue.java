package net.pkhapps.appmodel4flow.property;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.util.ListenerCollection;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

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

    @Nonnull
    @Override
    public <E> ObservableValue<E> map(@Nonnull SerializableFunction<T, E> mapFunction) {
        return new MappedObservableValue<>(this, mapFunction);
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

    private static class MappedObservableValue<E, T> extends AbstractObservableValue<E> {

        private final ObservableValue<T> sourceValue;
        private final SerializableFunction<T, E> mapFunction;
        private final SerializableConsumer<ValueChangeEvent<T>> sourceValueListener = this::onSourceValueChangeEvent;

        MappedObservableValue(@Nonnull ObservableValue<T> sourceValue,
                              @Nonnull SerializableFunction<T, E> mapFunction) {
            this.sourceValue = Objects.requireNonNull(sourceValue, "sourceValue must not be null");
            this.mapFunction = Objects.requireNonNull(mapFunction, "mapFunction must not be null");
            sourceValue.addWeakValueChangeListener(sourceValueListener);
        }

        private void onSourceValueChangeEvent(ValueChangeEvent<T> event) {
            fireValueChangeEvent(mapValue(event.getOldValue()), mapValue(event.getValue()));
        }

        private E mapValue(T original) {
            try {
                return mapFunction.apply(original);
            } catch (NullPointerException ex) {
                // The map function does not know how to deal with nulls.
                return null;
            }
        }

        @Override
        public E getValue() {
            return mapValue(sourceValue.getValue());
        }

        @Override
        public boolean isEmpty() {
            return sourceValue.isEmpty();
        }
    }
}
