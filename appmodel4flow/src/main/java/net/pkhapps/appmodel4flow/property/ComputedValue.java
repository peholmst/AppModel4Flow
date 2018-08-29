package net.pkhapps.appmodel4flow.property;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;

/**
 * An {@link ObservableValue} that is computed dynamically from a set of other {@link ObservableValue}s. A special
 * function is used to compute the value and this function is invoked any time any of the other values change.
 *
 * @param <T> the value type.
 */
public class ComputedValue<T> extends AbstractObservableValue<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputedValue.class);
    private final Set<ObservableValue> dependencies;
    private final SerializableConsumer<ValueChangeEvent> dependencyValueChangeListener = (event) -> updateCachedValue();
    private final SerializableSupplier<T> valueSupplier;

    private T cachedValue;

    /**
     * Creates a new {@code ComputedValue}.
     *
     * @param valueSupplier the function that will be used to compute the value, never {@code null}.
     * @param dependencies  the observable values that this computed value depends on, never {@code null}.
     */
    @SuppressWarnings("unchecked")
    public ComputedValue(@Nonnull SerializableSupplier<T> valueSupplier, @Nonnull ObservableValue... dependencies) {
        this.valueSupplier = Objects.requireNonNull(valueSupplier, "valueSupplier must not be null");
        if (dependencies.length == 0) {
            throw new IllegalArgumentException("Need at least one dependency");
        }
        this.dependencies = Set.of(dependencies);
        this.dependencies.forEach(dependency -> dependency.addWeakValueChangeListener(dependencyValueChangeListener));
        updateCachedValue();
    }

    private void updateCachedValue() {
        var old = cachedValue;
        cachedValue = valueSupplier.get();
        if (!Objects.equals(old, cachedValue)) {
            LOGGER.trace("Updating cached value of {} to {}", this, cachedValue);
            fireValueChangeEvent(old, cachedValue);
        }
    }

    @Override
    public T getValue() {
        return cachedValue;
    }

    @Override
    public boolean isEmpty() {
        return cachedValue == null;
    }

    @Override
    public String toString() {
        return String.format("%s@%x[dependencies=%s]", getClass().getSimpleName(), hashCode(), dependencies);
    }
}
