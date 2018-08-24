package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.selection.SelectionEvent;
import net.pkhapps.appmodel4flow.selection.Selection;
import net.pkhapps.appmodel4flow.selection.SelectionModel;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Binding that binds a {@link SelectionModel} and a {@link Grid} together. When the selection is changed in the model,
 * the grid is updated and vice versa. Remember to call {@link #remove()} when the binding is no longer needed to avoid
 * memory leaks.
 *
 * @param <T> the type of items in the grid and selection model.
 */
@NotThreadSafe
public class SelectionModelGridBinding<T> extends AbstractSelectionModelBinding<T> {

    private final Grid<T> grid;

    private boolean selectionUpdateInProgress = false;

    /**
     * Creates a new {@code SelectionModelGridBinding}.
     *
     * @param selectionModel the model to bind to the grid, never {@code null}.
     * @param grid           the grid to bind to the model, never {@code null}.
     */
    public SelectionModelGridBinding(@Nonnull SelectionModel<T> selectionModel, @Nonnull Grid<T> grid) {
        super(selectionModel);
        this.grid = Objects.requireNonNull(grid, "grid must not be null");
        setComponentRegistration(grid.addSelectionListener(this::onGridSelectionEvent));
        updateSelection(selectionModel.getSelection());
    }

    @Override
    protected void updateSelection(@Nonnull Selection<T> selection) {
        selectionUpdateInProgress = true;
        try {
            grid.deselectAll();
            selection.forEach(grid::select);
        } finally {
            selectionUpdateInProgress = false;
        }
    }

    private void onGridSelectionEvent(@Nonnull SelectionEvent<Grid<T>, T> event) {
        if (!selectionUpdateInProgress) {
            getSelectionModel().select(event.getAllSelectedItems());
        }
    }
}
