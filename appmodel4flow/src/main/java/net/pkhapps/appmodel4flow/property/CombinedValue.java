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
    private final SerializableFunction<Stream<T>, T> combinator;
    private final SerializableConsumer<ValueChangeEvent<T>> dependencyValueChangeListener = (event) -> updateCachedValue();

    @SafeVarargs
    public CombinedValue(@Nonnull SerializableFunction<Stream<T>, T> combinator, @Nonnull ObservableValue<T>... dependencies) {
        this(combinator, Set.of(dependencies));
    }

    public CombinedValue(@Nonnull SerializableFunction<Stream<T>, T> combinator, @Nonnull Collection<ObservableValue<T>> dependencies) {
        this.combinator = Objects.requireNonNull(combinator, "combinator must not be null");
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
        return combinator.apply(values);
    }

    @Override
    public String toString() {
        return String.format("%s@%x[dependencies=%s]", getClass().getSimpleName(), hashCode(), dependencies);
    }
}
