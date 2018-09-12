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

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * An {@link ObservableValue} that is computed dynamically by combining the values of a set of other
 * {@link ObservableValue}s of the same type. A special combiner function is invoked to compute the value every time
 * any of the other values change.
 * <p>
 * The difference between this class and {@link ComputedValue} is that this class works with observable values of the
 * same type whereas the computed value works with observable values of different types.
 *
 * @param <T> the value type.
 * @see ComputedValue
 */
public class CombinedValue<T> extends AbstractComputedValue<T> {

    private final List<ObservableValue<T>> dependencies;
    private final SerializableFunction<Stream<T>, T> combiner;
    private final SerializableConsumer<ValueChangeEvent<T>> dependencyValueChangeListener = (event) -> updateCachedValue();

    /**
     * Creates a new {@code CombinedValue}.
     *
     * @param combiner     the function to use to compute the value, never {@code null}.
     * @param dependencies the observable values that will form the combined value, never {@code null} but must contain
     *                     at least one value.
     */
    @SafeVarargs
    public CombinedValue(@Nonnull SerializableFunction<Stream<T>, T> combiner,
                         @Nonnull ObservableValue<T>... dependencies) {
        this(combiner, List.of(dependencies));
    }

    /**
     * Creates a new {@code CombinedValue}.
     *
     * @param combiner     the function to use to compute the value, never {@code null}.
     * @param dependencies the observable values that will form the combined value, never {@code null} but must contain
     *                     at least one value.
     */
    public CombinedValue(@Nonnull SerializableFunction<Stream<T>, T> combiner,
                         @Nonnull List<ObservableValue<T>> dependencies) {
        this.combiner = Objects.requireNonNull(combiner, "combiner must not be null");
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
        var values = dependencies.stream().map(ObservableValue::getValue);
        return combiner.apply(values);
    }

    @Override
    public String toString() {
        return String.format("%s@%x[dependencies=%s]", getClass().getSimpleName(), hashCode(), dependencies);
    }
}
