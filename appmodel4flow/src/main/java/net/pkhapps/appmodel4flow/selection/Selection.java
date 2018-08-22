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
     */
    boolean isEmpty();

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
