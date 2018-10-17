/*
 * Copyright (c) 2018 the original authors (see project POM file)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.pkhapps.appmodel4flow.property;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.shared.Registration;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Objects;

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
     * Returns whether this object is empty or contains a value.
     *
     * @return true if there is no value, false if there is one.
     * @see #hasValue()
     */
    default boolean isEmpty() {
        return isEmpty(getValue());
    }

    /**
     * Checks whether the given value is considered empty or not. By default, only {@code null} is considered empty.
     * Implementations are free to decide when the object is empty (e.g. an empty string or empty collection, etc.).
     *
     * @param value the value to check.
     * @return true if the value is considered empty, false if not.
     */
    default boolean isEmpty(@Nullable T value) {
        return value == null;
    }

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
     * Maps this observable value to an observable value with a different type. A map function is used to
     * convert the value. If null values are supported, the map function must also be able to handle null values.
     *
     * @param mapFunction the function to use when converting the value from this observable value to the mapped value,
     *                    never {@code null}.
     * @param <E>         the type of the mapped observable value.
     * @return an observable value that is backed by this observable value but has a different type, never {@code null}.
     */
    @Nonnull
    <E> ObservableValue<E> map(@Nonnull SerializableFunction<T, E> mapFunction);

    /**
     * Maps this observable value to an observable value with a different type, using a function that takes this
     * {@code ObservableValue} instance and produces another {@link ObservableValue} instance. This gives the function
     * full control of the returned instance as opposed to {@link #map(SerializableFunction)} which is implementation
     * specific.
     * <p>
     * The name is inspired by the {@code flatMap} methods found in {@code Optional} and {@code Stream}, even though
     * they are semantically slightly different.
     *
     * @param mapFunction the function to use when converting this {@code ObservableValue} to the mapped
     *                    {@code ObservableValue}, never {@code null}.
     * @param <E>         the type of the mapped observable value.
     * @return an observable value that is backed by this observable value but has a different type, never {@code null}.
     */
    @Nonnull
    default <E> ObservableValue<E> flatMap(@Nonnull SerializableFunction<ObservableValue<T>,
            ObservableValue<E>> mapFunction) {
        Objects.requireNonNull(mapFunction, "mapFunction must not be null");
        return mapFunction.apply(this);
    }

    /**
     * Event fired by a {@link ObservableValue} when the value changes.
     *
     * @param <T> the value type.
     */
    @SuppressWarnings("WeakerAccess")
    @Immutable
    @ToString
    class ValueChangeEvent<T> implements Serializable {

        private static final long serialVersionUID = 1L;

        private final ObservableValue<T> sender;
        private final T oldValue;
        private final T value;

        public ValueChangeEvent(@Nonnull ObservableValue<T> sender, @Nullable T oldValue, @Nullable T value) {
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
        @Nullable
        public T getOldValue() {
            return oldValue;
        }

        /**
         * Returns the current (new) value.
         *
         * @return the new value, may be {@code null}.
         */
        @Nullable
        public T getValue() {
            return value;
        }
    }
}
