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

package net.pkhapps.appmodel4flow.incubator.property;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import net.pkhapps.appmodel4flow.property.Property;
import net.pkhapps.appmodel4flow.util.ListenerCollection;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * TODO Document me!
 *
 * @param <T>
 */
@NotThreadSafe
public class ObservableValueCollection<T> implements Serializable, Iterable<ObservableValue<T>> {

    private final Map<ObservableValue<T>, Registration> items = new HashMap<>();
    private final ListenerCollection<Property.ValueChangeEvent<T>> valueChangeListeners = new ListenerCollection<>();
    private final SerializableConsumer<Property.ValueChangeEvent<T>> itemValueChangeListener
            = valueChangeListeners::fireEvent;

    /**
     * @param observableValue
     */
    public void add(@Nonnull ObservableValue<T> observableValue) {
        if (!items.containsKey(observableValue)) {
            var registration = observableValue.addValueChangeListener(itemValueChangeListener);
            items.put(observableValue, registration);
        }
    }

    /**
     * @param observableValues
     */
    public void add(@Nonnull Iterable<ObservableValue<T>> observableValues) {
        Objects.requireNonNull(observableValues, "observableValues must not be null");
        observableValues.forEach(this::add);
    }

    /**
     * @param observableValues
     */
    public void add(@Nonnull Stream<ObservableValue<T>> observableValues) {
        Objects.requireNonNull(observableValues, "observableValues must not be null");
        observableValues.forEach(this::add);
    }

    /**
     * @param observableValue
     */
    public void remove(@Nonnull ObservableValue<T> observableValue) {
        Objects.requireNonNull(observableValue, "observableValue must not be null");
        var registration = items.remove(observableValue);
        if (registration != null) {
            registration.remove();
        }
    }

    /**
     * @param observableValues
     */
    public void remove(@Nonnull Iterable<ObservableValue<T>> observableValues) {
        Objects.requireNonNull(observableValues, "observableValues must not be null");
        observableValues.forEach(this::remove);
    }

    /**
     * @param observableValues
     */
    public void remove(@Nonnull Stream<ObservableValue<T>> observableValues) {
        Objects.requireNonNull(observableValues, "observableValues must not be null");
        observableValues.forEach(this::remove);
    }

    /**
     * @param observableValue
     * @return
     */
    @Nonnull
    public ObservableValueCollection<T> withValue(@Nonnull ObservableValue<T> observableValue) {
        add(observableValue);
        return this;
    }

    @Nonnull
    @Override
    public Iterator<ObservableValue<T>> iterator() {
        var iterator = items.keySet().iterator();
        // Wrap iterator to disable the remove() operation.
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public ObservableValue<T> next() {
                return iterator.next();
            }
        };
    }

    /**
     * @return
     */
    @Nonnull
    public Stream<ObservableValue<T>> stream() {
        return items.keySet().stream();
    }

    /**
     * @return
     */
    public int size() {
        return items.size();
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Registers a listener to be notified when any of the observable values in the group changes.
     *
     * @param listener the listener, never {@code null}.
     * @return a registration handle, never {@code null}.
     */
    @Nonnull
    public Registration addValueChangeListener(@Nonnull SerializableConsumer<Property.ValueChangeEvent<T>> listener) {
        return valueChangeListeners.addListener(listener);
    }

    /**
     * Registers a listener to be notified when any of the observable values in the group changes. The listener is
     * registered using a weak reference and will be automatically removed when garbage collected. This means you have
     * to make sure you keep another reference to the listener for as long as you need it or it will become garbage
     * collected too soon.
     *
     * @param listener the listener, never {@code null}.
     */
    public void addWeakValueChangeListener(@Nonnull SerializableConsumer<Property.ValueChangeEvent<T>> listener) {
        valueChangeListeners.addWeakListener(listener);
    }
}
