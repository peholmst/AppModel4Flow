package net.pkhapps.appmodel4flow.property;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Interface for objects that wrap a value and allow listeners to be notified whenever the value is changed.
 *
 * @param <T> the value type.
 */
public interface ObservableValue<T> extends Serializable {

    /**
     * Returns the current value.
     *
     * @return the value. Implementations are free to decide whether this can or cannot be {@code null}.
     */
    T getValue();

    /**
     * Registers a listener to be notified when the value changes.
     *
     * @param listener the listener, never {@code null}.
     * @return a registration handle, never {@code null}.
     */
    @Nonnull
    Registration addValueChangeListener(@Nonnull SerializableConsumer<Property.ValueChangeEvent<T>> listener);

    /**
     * Registers a listener to be notified when the value changes. The listener is registered using a weak reference and
     * will be automatically removed when garbage collected. This means you have to make sure you keep another reference
     * to the listener for as long as you need it or it will become garbage collected too soon.
     *
     * @param listener the listener, never {@code null}.
     */
    void addWeakValueChangeListener(@Nonnull SerializableConsumer<Property.ValueChangeEvent<T>> listener);

    /**
     * Event fired by a {@link ObservableValue} when the value changes.
     *
     * @param <T> the value type.
     */
    @Immutable
    class ValueChangeEvent<T> implements Serializable {
        private final ObservableValue<T> sender;
        private final T oldValue;
        private final T value;

        public ValueChangeEvent(@Nonnull ObservableValue<T> sender, T oldValue, T value) {
            this.sender = Objects.requireNonNull(sender, "sender must not be null");
            this.oldValue = oldValue;
            this.value = value;
        }

        /**
         * Returns the {@link ObservableValue} that fired this event.
         *
         * @return the observable value, never {@code null}.
         */
        @Nonnull
        public ObservableValue<T> getSender() {
            return sender;
        }

        /**
         * Returns the old value.
         *
         * @return the old value, may be {@code null}.
         */
        public T getOldValue() {
            return oldValue;
        }

        /**
         * Returns the current (new) value.
         *
         * @return the new value, may be {@code null}.
         */
        public T getValue() {
            return value;
        }
    }
}
