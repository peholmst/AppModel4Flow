package net.pkhapps.appmodel4flow;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.action.Action;
import net.pkhapps.appmodel4flow.action.support.CompositeAction;
import net.pkhapps.appmodel4flow.binding.ActionButtonBinding;
import net.pkhapps.appmodel4flow.binding.SelectionModelComboBoxBinding;
import net.pkhapps.appmodel4flow.binding.SelectionModelGridBinding;
import net.pkhapps.appmodel4flow.selection.DefaultSelectionModel;
import net.pkhapps.appmodel4flow.selection.SelectionModel;

import javax.annotation.Nonnull;

/**
 * A utility class for making it easier to work with AppModel4Flow. There is nothing you can do with this class that you
 * also can't do without it, but it may make the code a little easier to read.
 */
@SuppressWarnings("UnusedReturnValue")
public final class AppModel {

    private AppModel() {
    }

    /**
     * Creates and returns a new {@link SelectionModel}.
     *
     * @param <T> the type of the items in the selection.
     * @return the selection model, never {@code null}.
     */
    @Nonnull
    public static <T> SelectionModel<T> newSelectionModel() {
        return new DefaultSelectionModel<>();
    }

    /**
     * Binds the specified {@link SelectionModel} and {@link Grid} together.
     *
     * @param model the model to bind, never {@code null}.
     * @param grid  the grid to bind, never {@code null}.
     * @param <T>   the type of the items in the selection and grid.
     * @return a registration handle for the binding, never {@code null}.
     */
    @Nonnull
    public static <T> Registration bind(@Nonnull SelectionModel<T> model, @Nonnull Grid<T> grid) {
        return new SelectionModelGridBinding<>(model, grid);
    }

    /**
     * Binds the specified {@link SelectionModel} and {@link ComboBox} together.
     *
     * @param model    the model to bind, never {@code null}.
     * @param comboBox the combo box to bind, never {@code null}.
     * @param <T>      the type of the items in the selection and combo box.
     * @return a registration handle for the binding, never {@code null}.
     */
    @Nonnull
    public static <T> Registration bind(@Nonnull SelectionModel<T> model, @Nonnull ComboBox<T> comboBox) {
        return new SelectionModelComboBoxBinding<>(model, comboBox);
    }

    /**
     * Binds the given {@link Action} and {@link Button} together.
     *
     * @param action the action to bind, never {@code null}.
     * @param button the button to bind, never {@code null}.
     * @return a registration handle for the binding, never {@code null}.
     */
    @Nonnull
    public static Registration bind(@Nonnull Action<?> action, @Nonnull Button button) {
        return new ActionButtonBinding(action, button);
    }

    /**
     * Creates a new {@link Action} by composing the listed actions together.
     *
     * @param actions the actions to compose, never {@code null} and must contain at least one action.
     * @return the composite action, never {@code null}.
     */
    @Nonnull
    public static Action<?> compose(@Nonnull Action<?>... actions) {
        return new CompositeAction(actions);
    }
}
