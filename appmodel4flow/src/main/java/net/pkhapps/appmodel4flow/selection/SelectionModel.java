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

package net.pkhapps.appmodel4flow.selection;

import net.pkhapps.appmodel4flow.property.WritableObservableValue;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

/**
 * Interface defining a selection model. The model contains a selection of items and notifies its listeners whenever
 * this selection is changed.
 *
 * @param <T> the type of the items in the selection.
 */
public interface SelectionModel<T> extends WritableObservableValue<Selection<T>> {

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
