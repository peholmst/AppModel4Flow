package net.pkhapps.appmodel4flow.selection;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Interface defining a selection model. The model contains a selection of items and notifies its listeners whenever
 * this selection is changed.
 *
 * @param <T> the type of the items in the selection.
 */
public interface SelectionModel<T> extends Serializable {

    /**
     * Returns the current selection.
     *
     * @return the selection, never {@code null}.
     */
    @Nonnull
    Selection<T> getSelection();

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

    /**
     * Registers a listener to be notified whenever the selection changes.
     *
     * @param listener the listener, never {@code null}.
     * @return a registration handle, never {@code null}.
     */
    @Nonnull
    Registration addSelectionChangeListener(@Nonnull SerializableConsumer<SelectionChangeEvent<T>> listener);

    /**
     * Registers a listener to be notified whenever the selection changes. The listener is registered using a weak
     * reference and will be automatically removed when garbage collected. This means you have to make sure you keep
     * another reference to the listener for as long as you need it or it will become garbage collected too soon.
     *
     * @param listener the listener, never {@code null}.
     */
    void addWeakSelectionChangeListener(@Nonnull SerializableConsumer<SelectionChangeEvent<T>> listener);

    /**
     * Event fired by a {@link SelectionModel} when its selection is changed.
     *
     * @param <T> the type of the items in the selection.
     */
    @Immutable
    class SelectionChangeEvent<T> implements Serializable {

        private final SelectionModel<T> sender;
        private final Selection<T> oldSelection;
        private final Selection<T> selection;

        /**
         * Creates a new {@code SelectionChangedEvent}. When this constructor is invoked,
         * {@link SelectionModel#getSelection()} must already contain the new selection.
         *
         * @param sender       the model that will fire the event, never {@code null}.
         * @param oldSelection the old selection, never {@code null}.
         */
        @SuppressWarnings("WeakerAccess")
        public SelectionChangeEvent(@Nonnull SelectionModel<T> sender, @Nonnull Selection<T> oldSelection) {
            this.sender = Objects.requireNonNull(sender);
            this.oldSelection = Objects.requireNonNull(oldSelection);
            this.selection = sender.getSelection();
        }

        /**
         * Returns the model that fired this event.
         *
         * @return the model, never {@code null}.
         */
        @Nonnull
        @SuppressWarnings("WeakerAccess")
        public SelectionModel<T> getSender() {
            return sender;
        }

        /**
         * Returns the old selection of the model.
         *
         * @return the old selection, never {@code null}.
         */
        @Nonnull
        @SuppressWarnings("WeakerAccess")
        public Selection<T> getOldSelection() {
            return oldSelection;
        }

        /**
         * Returns the current (new) selection of the model.
         *
         * @return the selection, never {@code null}.
         */
        @Nonnull
        public Selection<T> getSelection() {
            return selection;
        }
    }
}
