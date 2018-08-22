package net.pkhapps.appmodel4flow.selection;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Default implementation of {@link SelectionModel}. Developers are free to use this whenever they need an
 * implementation of the model interface.
 *
 * @param <T> the type of the items in the selection.
 */
@NotThreadSafe
public class DefaultSelectionModel<T> implements SelectionModel<T> {

    private Selection<T> selection = new DefaultSelection<>();
    private Set<SerializableConsumer<SelectionChangeEvent<T>>> selectionChangedListeners;

    @Nonnull
    @Override
    public Selection<T> getSelection() {
        return selection;
    }

    @Override
    public void select(@Nonnull Collection<T> items) {
        var oldSelection = selection;
        selection = new DefaultSelection<>(items);
        if (selectionChangedListeners != null && selectionChangedListeners.size() > 0) {
            SelectionChangeEvent<T> event = new SelectionChangeEvent<>(this, oldSelection);
            selectionChangedListeners.forEach(listener -> listener.accept(event));
        }
    }

    @Nonnull
    @Override
    public Registration addSelectionChangeListener(@Nonnull SerializableConsumer<SelectionChangeEvent<T>> listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        if (selectionChangedListeners == null) {
            selectionChangedListeners = new HashSet<>();
        }
        selectionChangedListeners.add(listener);
        return () -> selectionChangedListeners.remove(listener);
    }
}
