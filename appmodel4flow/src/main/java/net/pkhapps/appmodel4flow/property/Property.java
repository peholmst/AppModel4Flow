package net.pkhapps.appmodel4flow.property;

import javax.annotation.Nonnull;

/**
 * Interface defining a property whose value can be changed.
 *
 * @param <T> the value type.
 */
@SuppressWarnings("unused")
public interface Property<T> extends ObservableValue<T> {

    /**
     * Sets the value of this property, notifying the listeners of the change.
     *
     * @param value the value to set.
     * @throws ReadOnlyException if the property is {@link #isReadOnly() read-only}.
     */
    void setValue(T value);

    /**
     * Returns whether this property is dirty, i.e. have been modified after the last call to {@link #resetDirtyFlag()}.
     *
     * @return true if the property is dirty, false if it is clean.
     */
    @Nonnull
    ObservableValue<Boolean> isDirty();

    /**
     * Resets the value of the {@link #isDirty() dirty} flag to false. Any changes made to the property value after
     * calling this will switch the dirty flag back to true.
     */
    void resetDirtyFlag();

    /**
     * Sets the value of this property and reset the {@link #isDirty() dirty} flag to false. This is the same as calling
     * {@link #setValue(Object)} followed by {@link #resetDirtyFlag()}.
     *
     * @param value the value to set.
     * @throws ReadOnlyException if the property is {@link #isReadOnly() read-only}.
     */
    default void setCleanValue(T value) {
        setValue(value);
        resetDirtyFlag();
    }

    /**
     * Discards the current value and resets it to the value the property had when {@link #resetDirtyFlag()} was last
     * called. This will also set the {@link #isDirty() dirty} flag to false.
     */
    void discard();

    /**
     * Returns whether this property is read-only or writable.
     *
     * @return true if the property is read-only, false if it is writable.
     */
    @Nonnull
    ObservableValue<Boolean> isReadOnly();

    /**
     * Marks this property as read-only or writable. For properties that are always read-only, consider using
     * {@link ObservableValue} instead.
     *
     * @param readOnly true to make the property read-only, false to make it writable.
     */
    void setReadOnly(boolean readOnly);

    /**
     * Exception thrown by a {@link Property} when attempting to set the value of a read-only property.
     */
    class ReadOnlyException extends IllegalStateException {
        // NOP
    }
}
