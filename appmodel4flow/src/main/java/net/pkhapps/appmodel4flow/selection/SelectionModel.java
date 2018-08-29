package net.pkhapps.appmodel4flow.selection;

import net.pkhapps.appmodel4flow.property.ObservableValue;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

/**
 * Interface defining a selection model. The model contains a selection of items and notifies its listeners whenever
 * this selection is changed.
 *
 * @param <T> the type of the items in the selection.
 */
public interface SelectionModel<T> extends ObservableValue<Selection<T>> {

    /**
     * Returns the current selection. This is an alias of {@link #getValue()} introduced to make the code easier
     * to understand.
     *
     * @return the selection, never {@code null}.
     */
    @Nonnull
    default Selection<T> getSelection() {
        return getValue();
    }

    /**
     * Clears the selection.
     */
    default void clear() {
        selectOne(null);
    }

    /**
     * Selects exactly one item.
     *
     * @param item the item to select or {@code null} to clear the selection.
     */
    default void selectOne(T item) {
        if (item == null) {
            select(Collections.emptySet());
        } else {
            select(Collections.singleton(item));
        }
    }

    /**
     * Selects the specified items. If the collection contains no items, this method will clear the selection.
     *
     * @param items the items to select, never {@code null}.
     */
    void select(@Nonnull Collection<T> items);
}
