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
import com.vaadin.flow.function.SerializableSupplier;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * An {@link ObservableValue} that is computed dynamically from a set of other {@link ObservableValue}s. A special
 * function is used to compute the value and this function is invoked any time any of the other values change.
 * <p>
 * The difference between this class and {@link CombinedValue} is that this class works with observable values of
 * different types whereas the computed value works with observable values of the same type.
 *
 * @param <T> the value type.
 * @see CombinedValue
 */
public class ComputedValue<T> extends AbstractComputedValue<T> {

    private final Collection<? extends ObservableValue> dependencies;
    private final SerializableSupplier<T> valueSupplier;
    private final SerializableConsumer<ValueChangeEvent> dependencyValueChangeListener = (event) -> updateCachedValue();

    /**
     * Creates a new {@code ComputedValue}.
     *
     * @param valueSupplier the function that will be used to compute the value, never {@code null}.
     * @param dependencies  the observable values that this computed value depends on, never {@code null} but must contain at least one value.
     */
    public ComputedValue(@Nonnull SerializableSupplier<T> valueSupplier, @Nonnull ObservableValue... dependencies) {
        this(valueSupplier, Set.of(dependencies));
    }

    /**
     * Creates a new {@code ComputedValue}.
     *
     * @param valueSupplier the function that will be used to compute the value, never {@code null}.
     * @param dependencies  the observable values that this computed value depends on, never {@code null} but must contain at least one value.
     */
    @SuppressWarnings({"unchecked", "WeakerAccess"})
    public ComputedValue(@Nonnull SerializableSupplier<T> valueSupplier, @Nonnull Collection<? extends ObservableValue> dependencies) {
        this.valueSupplier = Objects.requireNonNull(valueSupplier, "valueSupplier must not be null");
        Objects.requireNonNull(dependencies, "dependencies must not be null");
        if (dependencies.size() == 0) {
            throw new IllegalArgumentException("Need at least one dependency");
        }
        this.dependencies = dependencies;
        this.dependencies.forEach(dependency -> dependency.addWeakValueChangeListener(dependencyValueChangeListener));
        updateCachedValue();
    }

    @Override
    protected T computeValue() {
        return valueSupplier.get();
    }

    @Override
    public String toString() {
        return String.format("%s@%x[dependencies=%s]", getClass().getSimpleName(), hashCode(), dependencies);
    }
}
