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
import java.util.*;
import java.util.stream.Stream;

/**
 * Default implementation of {@link Selection}. Developers should not need to create instances of this directly but
 * it is also not prohibited to do so.
 *
 * @param <T> the type of the items in the selection.
 */
@Immutable
public class DefaultSelection<T> implements Selection<T> {

    private final List<T> items;

    /**
     * Creates a new empty {@code DefaultSelection}.
     */
    @SuppressWarnings("WeakerAccess")
    public DefaultSelection() {
        this(Collections.emptySet());
    }

    /**
     * Creates a new {@code DefaultSelection}.
     *
     * @param items the items in the selection, never {@code null} but can be empty.
     */
    @SuppressWarnings("WeakerAccess")
    public DefaultSelection(@Nonnull Collection<T> items) {
        Objects.requireNonNull(items, "items must not be null");
        this.items = new ArrayList<>(items);
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Nonnull
    @Override
    public Stream<T> stream() {
        return items.stream();
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        var iterator = items.iterator();
        // Don't return the iterator directly as it implements the 'remove()' operation and we don't want to expose that.
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }
        };
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), items);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        } else if (obj == this) {
            return true;
        }
        var other = (DefaultSelection) obj;
        return Objects.equals(items, other.items);
    }
}
