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
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Default implementation of {@link Property}. Developers are free to use as-is or extend.  However, in most cases they
 * should not expose instances of this class directly to other classes but use the {@link Property interface} instead.
 *
 * @param <T> the value type.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@NotThreadSafe
@ToString(callSuper = true)
public class DefaultProperty<T> extends DefaultObservableValue<T> implements Property<T> {

    private final DefaultObservableValue<Boolean> dirty = new DefaultObservableValue<>(false);
    private final DefaultObservableValue<Boolean> readOnly = new DefaultObservableValue<>(false);
    private T cleanValue;

    /**
     * Creates a new, empty {@code DefaultProperty}.
     */
    public DefaultProperty() {
    }

    /**
     * Creates a new {@code DefaultProperty} with the given value.
     *
     * @param value the initial value, may be {@code null}.
     */
    public DefaultProperty(T value) {
        super(value);
        this.cleanValue = value;
    }

    @Override
    public void setValue(T value) {
        if (readOnly.getValue()) {
            throw new ReadOnlyException();
        }
        super.setValue(value);
        dirty.setValue(!Objects.equals(cleanValue, value));
    }

    @Override
    public <E> WritableObservableValue<E> map(@Nonnull SerializableFunction<T, E> mapFunction,
                                              @Nonnull SerializableFunction<E, T> inverseMapFunction) {
        return map(this, mapFunction, inverseMapFunction);
    }

    @Nonnull
    @Override
    public ObservableValue<Boolean> isDirty() {
        return dirty;
    }

    @Override
    public void resetDirtyFlag() {
        this.cleanValue = getValue();
        dirty.setValue(false);
    }

    @Override
    public void discard() {
        super.setValue(cleanValue);
        dirty.setValue(false);
    }

    @Nonnull
    @Override
    public ObservableValue<Boolean> isReadOnly() {
        return readOnly;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly.setValue(readOnly);
    }
}
