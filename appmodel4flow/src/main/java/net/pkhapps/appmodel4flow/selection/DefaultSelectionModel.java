package net.pkhapps.appmodel4flow.selection;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.util.ListenerCollection;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collection;

/**
 * Default implementation of {@link SelectionModel}. Developers are free to use this whenever they need an
 * implementation of the model interface.
 *
 * @param <T> the type of the items in the selection.
 */
@NotThreadSafe
public class DefaultSelectionModel<T> implements SelectionModel<T> {

    private Selection<T> selection = new DefaultSelection<>();
    private ListenerCollection<SelectionChangeEvent<T>> selectionChangedListeners;

    @Nonnull
    @Override
    public Selection<T> getSelection() {
        return selection;
    }

    @Override
    public void select(@Nonnull Collection<T> items) {
        var oldSelection = selection;
        selection = new DefaultSelection<>(items);
        if (selectionChangedListeners != null && selectionChangedListeners.containsListeners()) {
            SelectionChangeEvent<T> event = new SelectionChangeEvent<>(this, oldSelection);
            selectionChangedListeners.fireEvent(event);
        }
    }

    @Nonnull
    @Override
    public Registration addSelectionChangeListener(@Nonnull SerializableConsumer<SelectionChangeEvent<T>> listener) {
        return getSelectionChangedListeners().addListener(listener);
    }

    @Override
    public void addWeakSelectionChangeListener(@Nonnull SerializableConsumer<SelectionChangeEvent<T>> listener) {
        getSelectionChangedListeners().addWeakListener(listener);
    }

    private ListenerCollection<SelectionChangeEvent<T>> getSelectionChangedListeners() {
        if (selectionChangedListeners == null) {
            selectionChangedListeners = new ListenerCollection<>();
        }
        return selectionChangedListeners;
    }
}
