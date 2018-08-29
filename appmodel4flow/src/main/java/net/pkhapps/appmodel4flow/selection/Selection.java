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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Interface defining a selection of items. A selection can contain no items, exactly one item or multiple items.
 * Selections are always immutable.
 *
 * @param <T> the type of the items in the selection.
 */
@Immutable
public interface Selection<T> extends Iterable<T>, Serializable {

    /**
     * Returns whether this selection is empty.
     *
     * @return true if this selection is empty, false if it contains at least one item.
     * @see #hasValue()
     */
    boolean isEmpty();

    /**
     * Returns whether this selection contains at least one item.
     *
     * @return true if this selection contains at least one item, false if it is empty.
     * @see #isEmpty()
     */
    default boolean hasValue() {
        return !isEmpty();
    }

    /**
     * Returns the first item in this selection.
     *
     * @return the first item, never {@code null}.
     */
    @Nonnull
    default Optional<T> getFirst() {
        return stream().findFirst();
    }

    /**
     * Returns a stream of all the items in the selection.
     *
     * @return a stream, never {@code null}.
     */
    @Nonnull
    Stream<T> stream();
}
