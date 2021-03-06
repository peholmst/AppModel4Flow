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

import com.vaadin.flow.function.SerializablePredicate;
import lombok.ToString;

import javax.annotation.Nonnull;
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
@ToString
public class DefaultObservableValue<T> extends AbstractObservableValue<T> {

    private static final long serialVersionUID = 1L;

    private T value;

    private transient boolean updatingValue = false;

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

    @Nonnull
    @Override
    public DefaultObservableValue<T> withEmptyCheck(@Nonnull SerializablePredicate<T> emptyCheck) {
        return (DefaultObservableValue<T>) super.withEmptyCheck(emptyCheck);
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
            try {
                if (!updatingValue) {
                    updatingValue = true;
                    var old = this.value;
                    this.value = value;
                    fireValueChangeEvent(old, value);
                }
            } finally {
                updatingValue = false;
            }
        }
    }
}
