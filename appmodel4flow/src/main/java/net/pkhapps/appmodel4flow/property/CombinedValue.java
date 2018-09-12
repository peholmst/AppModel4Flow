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
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * TODO Document and test me!
 *
 * @param <T>
 */
public class CombinedValue<T> extends AbstractComputedValue<T> {

    private final Collection<ObservableValue<T>> dependencies;
    private final SerializableFunction<Stream<T>, T> combiner;
    private final SerializableConsumer<ValueChangeEvent<T>> dependencyValueChangeListener = (event) -> updateCachedValue();

    @SafeVarargs
    public CombinedValue(@Nonnull SerializableFunction<Stream<T>, T> combiner, @Nonnull ObservableValue<T>... dependencies) {
        this(combiner, Set.of(dependencies));
    }

    public CombinedValue(@Nonnull SerializableFunction<Stream<T>, T> combiner, @Nonnull Collection<ObservableValue<T>> dependencies) {
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
