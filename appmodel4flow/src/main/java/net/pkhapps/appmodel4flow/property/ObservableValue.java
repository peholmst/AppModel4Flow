package net.pkhapps.appmodel4flow.property;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Interface for objects that wrap a value and allow listeners to be notified whenever the value is changed.
 *
 * @param <T> the value type.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface ObservableValue<T> extends Serializable {

    /**
     * Returns the current value.
     *
     * @return the value. Implementations are free to decide whether this can or cannot be {@code null}.
     */
    T getValue();

    /**
     * Returns the current value wrapped in an {@code Optional}.
     *
     * @return the current value or an empty optional if there is no value.
     */
    @Nonnull
    default Optional<T> getOptionalValue() {
        return isEmpty() ? Optional.empty() : Optional.of(getValue());
    }

    /**
     * Returns whether this object is empty or contains a value. Implementations are free to decide when the object
     * is empty (e.g. a {@code null} value, an empty string or empty collection, etc.).
     *
     * @return true if there is no value, false if there is one.
     * @see #hasValue()
     */
    boolean isEmpty();

    /**
     * Returns whether this object has a value or is empty. This is the opposite of {@link #isEmpty()} and is provided
     * to make the code easier to read depending on what you want to test for.
     *
     * @return true if there is a value, false if there is none.
     */
    default boolean hasValue() {
        return !isEmpty();
    }

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
    @SuppressWarnings("WeakerAccess")
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
