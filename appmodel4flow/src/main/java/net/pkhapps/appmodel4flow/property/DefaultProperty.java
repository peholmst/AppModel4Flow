package net.pkhapps.appmodel4flow.property;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Default implementation of {@link Property}. Developers are free to use as-is or extend.
 *
 * @param <T> the value type.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
@NotThreadSafe
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
