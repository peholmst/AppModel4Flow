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

import com.vaadin.flow.function.SerializableFunction;

import javax.annotation.Nonnull;

/**
 * Interface defining an {@link ObservableValue} whose value can be set by external callers. For more features,
 * have a look at {@link Property}.
 *
 * @param <T> the value type.
 */
public interface WritableObservableValue<T> extends ObservableValue<T> {

    /**
     * Sets the value, notifying the listeners of the change.
     *
     * @param value the value to set.
     */
    void setValue(T value);

    /**
     * Maps this writable observable value to a writable observable value with a different type. Map functions are used
     * to convert between the different value types. If null values are supported, the map functions must also be able
     * to handle null values.
     *
     * @param mapFunction        the function to use when converting the value from this writable observable value to
     *                           the mapped value, never {@code null}.
     * @param inverseMapFunction the function to use when converting the value from the mapped value back to this
     *                           writable observable value, never {@code null}.
     * @param <E>                the type of the mapped observable value.
     * @return a writable observable value that is backed by this writable observable value but has a different type,
     * never {@code null}.
     * @see ObservableValue#map(SerializableFunction)
     */
    <E> WritableObservableValue<E> map(@Nonnull SerializableFunction<T, E> mapFunction,
                                       @Nonnull SerializableFunction<E, T> inverseMapFunction);
}
