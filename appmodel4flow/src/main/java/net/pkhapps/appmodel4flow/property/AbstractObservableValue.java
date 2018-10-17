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
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.shared.Registration;
import lombok.ToString;
import net.pkhapps.appmodel4flow.util.ListenerCollection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Base class for implementations of {@link ObservableValue}.
 *
 * @param <T> the value type.
 */
@NotThreadSafe
public abstract class AbstractObservableValue<T> implements ObservableValue<T> {

    private static final long serialVersionUID = 1L;

    private ListenerCollection<ValueChangeEvent<T>> valueChangeEventListeners;
    private SerializablePredicate<T> isEmpty;

    @SuppressWarnings("WeakerAccess")
    protected AbstractObservableValue() {
    }

    @Override
    public boolean isEmpty(@Nullable T value) {
        if (value == null) {
            return true;
        } else {
            return isEmpty != null && isEmpty.test(value);
        }
    }

    /**
     * Specifies a predicate that will be used to check whether a non-{@code null} value can also be considered an
     * {@link #isEmpty(Object) empty} value. If not set, only {@code null}s will be considered
     * empty values.
     *
     * @param emptyCheck the predicate to use, never {@code null}.
     * @return this {@code ObservableValue}, to allow for method chaining. Superclasses probably want to override to
     * return the correct type.
     */
    @Nonnull
    protected AbstractObservableValue<T> withEmptyCheck(@Nonnull SerializablePredicate<T> emptyCheck) {
        this.isEmpty = emptyCheck;
        return this;
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

    @Nonnull
    @Override
    public <E> ObservableValue<E> map(@Nonnull SerializableFunction<T, E> mapFunction) {
        return new MappedObservableValue<>(this, mapFunction);
    }

    /**
     * Method for implementations of {@link WritableObservableValue#map(SerializableFunction, SerializableFunction)}.
     * This method is included in this class to keep the class hierarchy as simple as possible.
     *
     * @param sourceValue        the source value (in most cases {@code this} when the class is implementing
     *                           {@link WritableObservableValue}), never {@code null}.
     * @param mapFunction        the map function to convert from this value to the mapped value, never {@code null}.
     * @param inverseMapFunction the inverse map function to convert from the mapped value to this value, never
     *                           {@code null}.
     * @param <E>                the type of the mapped value.
     * @return a writable observable value that is backed by the {@code sourceValue} but has a different type,
     * never {@code null}.
     */
    @Nonnull
    protected <E> WritableObservableValue<E> map(@Nonnull WritableObservableValue<T> sourceValue,
                                                 @Nonnull SerializableFunction<T, E> mapFunction,
                                                 @Nonnull SerializableFunction<E, T> inverseMapFunction) {
        return new MappedWritableObservableValue<>(sourceValue, mapFunction, inverseMapFunction);
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

    @ToString(of = "sourceValue")
    private static class MappedObservableValue<E, T> extends AbstractObservableValue<E> {

        private static final long serialVersionUID = 1L;

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
            return mapFunction.apply(original);
        }

        @Nonnull
        ObservableValue<T> getSourceValue() {
            return sourceValue;
        }

        @Override
        public E getValue() {
            return mapValue(sourceValue.getValue());
        }
    }

    private static class MappedWritableObservableValue<E, T> extends MappedObservableValue<E, T>
            implements WritableObservableValue<E> {

        private static final long serialVersionUID = 1L;

        private final SerializableFunction<E, T> writeMapFunction;

        MappedWritableObservableValue(@Nonnull WritableObservableValue<T> sourceValue,
                                      @Nonnull SerializableFunction<T, E> mapFunction,
                                      @Nonnull SerializableFunction<E, T> inverseMapFunction) {
            super(sourceValue, mapFunction);
            this.writeMapFunction = Objects.requireNonNull(inverseMapFunction, "inverseMapFunction must not be null");
        }

        @Override
        public void setValue(E value) {
            getSourceValue().setValue(writeMapFunction.apply(value));
        }

        @Nonnull
        @Override
        WritableObservableValue<T> getSourceValue() {
            return (WritableObservableValue<T>) super.getSourceValue();
        }

        @Override
        public <O> WritableObservableValue<O> map(@Nonnull SerializableFunction<E, O> mapFunction,
                                                  @Nonnull SerializableFunction<O, E> inverseMapFunction) {
            return new MappedWritableObservableValue<>(this, mapFunction, inverseMapFunction);
        }
    }
}
