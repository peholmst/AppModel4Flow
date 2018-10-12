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

package net.pkhapps.appmodel4flow;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.action.AbstractAction;
import net.pkhapps.appmodel4flow.action.Action;
import net.pkhapps.appmodel4flow.action.ActionWithoutResult;
import net.pkhapps.appmodel4flow.action.support.CompositeAction;
import net.pkhapps.appmodel4flow.binding.*;
import net.pkhapps.appmodel4flow.binding.group.BindingGroup;
import net.pkhapps.appmodel4flow.binding.group.FieldBindingGroup;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import net.pkhapps.appmodel4flow.property.Property;
import net.pkhapps.appmodel4flow.selection.DefaultSelectionModel;
import net.pkhapps.appmodel4flow.selection.SelectionModel;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A utility class for making it easier to work with AppModel4Flow. There is nothing you can do with this class that you
 * also can't do without it, but it may make the code a little easier to read.
 */
@SuppressWarnings({"UnusedReturnValue", "WeakerAccess", "unused"})
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
     * Creates and returns a new {@link BindingGroup}.
     *
     * @return the binder, never {@code null}.
     */
    @Nonnull
    public static BindingGroup newBindingGroup() {
        return new BindingGroup();
    }

    /**
     * Creates and returns a new {@link FieldBindingGroup}.
     *
     * @return the binder, never {@code null}.
     */
    @Nonnull
    public static FieldBindingGroup newFieldBindingGroup() {
        return new FieldBindingGroup();
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


    /**
     * TODO document me
     *
     * @param command
     * @return
     */
    @Nonnull
    public static Action<Void> asAction(@Nonnull SerializableRunnable command) {
        return new ActionWithoutResult(command);
    }

    /**
     * TODO document me
     *
     * @param isPerformable
     * @param command
     * @return
     */
    @Nonnull
    public static Action<Void> asAction(@Nonnull ObservableValue<Boolean> isPerformable,
                                        @Nonnull SerializableRunnable command) {
        return new ActionWithoutResult(isPerformable, command);
    }

    /**
     * TODO document me
     *
     * @param command
     * @param <T>
     * @return
     */
    @Nonnull
    public static <T> Action<T> asAction(@Nonnull SerializableSupplier<T> command) {
        Objects.requireNonNull(command, "command must not be null");
        return new AbstractAction<>() {
            @Override
            protected T doPerform() {
                return command.get();
            }
        };
    }

    /**
     * TODO document me
     *
     * @param isPerformable
     * @param command
     * @param <T>
     * @return
     */
    @Nonnull
    public static <T> Action<T> asAction(@Nonnull ObservableValue<Boolean> isPerformable,
                                         @Nonnull SerializableSupplier<T> command) {
        Objects.requireNonNull(command, "command must not be null");
        return new AbstractAction<>(isPerformable) {
            @Override
            protected T doPerform() {
                return command.get();
            }
        };
    }

    /**
     * Binds the given model and field together using a one-way binding where the field is read-only.
     *
     * @param model the model, never {@code null}.
     * @param field the field, never {@code null}.
     * @param <T>   the type of the value in the model and the field.
     * @return the binding, never {@code null}.
     */
    @Nonnull
    public static <T> FieldBinding<T, T> bindOneWay(@Nonnull ObservableValue<T> model,
                                                    @Nonnull HasValue<? extends HasValue.ValueChangeEvent<T>, T> field) {
        return new ObservableValueFieldBinding<>(model, field,
                new ObservableValueFieldBinding.PassThroughConverter<>());
    }

    /**
     * Binds the given model and field together using a one-way binding where the field is read-only.
     *
     * @param model     the model, never {@code null}.
     * @param field     the field, never {@code null}.
     * @param converter the converter to use for converting between model and field values, never {@code null}.
     * @param <M>       the type of the value in the model.
     * @param <P>       the type of the value in the field.
     * @return the binding, never {@code null}.
     */
    @Nonnull
    public static <M, P> FieldBinding<M, P> bindOneWay(@Nonnull ObservableValue<M> model,
                                                       @Nonnull HasValue<? extends HasValue.ValueChangeEvent<P>, P> field,
                                                       @Nonnull Converter<P, M> converter) {
        return new ObservableValueFieldBinding<>(model, field, converter);
    }

    /**
     * Binds the given model and field together using a two-way binding where changes made in the field are reflected
     * in the model and vice versa.
     *
     * @param model the model, never {@code null}.
     * @param field the field, never {@code null}.
     * @param <T>   the type of the value in the model and the field.
     * @return the binding, never {@code null}.
     */
    @Nonnull
    public static <T> TwoWayFieldBinding<T, T> bind(@Nonnull Property<T> model,
                                                    @Nonnull HasValue<? extends HasValue.ValueChangeEvent<T>, T> field) {
        return new PropertyFieldBinding<>(model, field, new ObservableValueFieldBinding.PassThroughConverter<>());
    }

    /**
     * Binds the given model and field together using a two-way binding where changes made in the field are reflected
     * in the model and vice versa.
     *
     * @param model     the model, never {@code null}.
     * @param field     the field, never {@code null}.
     * @param converter the converter to use for converting between model and field values, never {@code null}.
     * @param <M>       the type of the value in the model.
     * @param <P>       the type of the value in the field.
     * @return the binding, never {@code null}.
     */
    @Nonnull
    public static <M, P> TwoWayFieldBinding<M, P> bind(@Nonnull Property<M> model,
                                                       @Nonnull HasValue<? extends HasValue.ValueChangeEvent<P>, P> field,
                                                       @Nonnull Converter<P, M> converter) {
        return new PropertyFieldBinding<>(model, field, converter);
    }
}
