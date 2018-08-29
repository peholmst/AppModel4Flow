package net.pkhapps.appmodel4flow.property;

import java.util.Objects;

/**
 * TODO Document me!
 *
 * @param <T>
 */
public abstract class AbstractComputedValue<T> extends AbstractObservableValue<T> {

    private T cachedValue;

    protected void updateCachedValue() {
        var old = cachedValue;
        cachedValue = computeValue();
        if (!Objects.equals(old, cachedValue)) {
            fireValueChangeEvent(old, cachedValue);
        }
    }

    protected abstract T computeValue();

    @Override
    public T getValue() {
        return cachedValue;
    }

    @Override
    public boolean isEmpty() {
        return cachedValue == null;
    }
}
