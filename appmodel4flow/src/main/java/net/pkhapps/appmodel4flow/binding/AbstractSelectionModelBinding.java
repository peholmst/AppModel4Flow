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

package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import net.pkhapps.appmodel4flow.selection.Selection;
import net.pkhapps.appmodel4flow.selection.SelectionModel;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.Objects;

/**
 * Base class for bindings that bind a {@link SelectionModel} and a UI component together.
 *
 * @param <T> the type of items in the UI component and selection model.
 */
@NotThreadSafe
@SuppressWarnings("WeakerAccess")
public abstract class AbstractSelectionModelBinding<T> implements Serializable, Registration {

    private static final long serialVersionUID = 1L;

    private final SelectionModel<T> selectionModel;
    private Registration selectionModelRegistration;
    private Registration componentRegistration;

    /**
     * Subclasses should register a selection listener with the UI component and pass the registration handle to
     * {@link #setComponentRegistration(Registration)}. They should also remember to call
     * {@link #updateSelection(Selection)}, passing in the {@link SelectionModel#getSelection() current selection}
     * once everything has been set up.
     *
     * @param selectionModel the selection model to bind, never {@code null}.
     */
    public AbstractSelectionModelBinding(@Nonnull SelectionModel<T> selectionModel) {
        this.selectionModel = Objects.requireNonNull(selectionModel, "selectionModel must not be null");

        selectionModelRegistration = selectionModel.addValueChangeListener(this::onSelectionChanged);
    }

    private void onSelectionChanged(@Nonnull ObservableValue.ValueChangeEvent<Selection<T>> event) {
        updateSelection(event.getValue());
    }

    /**
     * Updates the UI component with the given selection.
     *
     * @param selection the new selection, never {@code null}.
     */
    protected abstract void updateSelection(@Nonnull Selection<T> selection);

    /**
     * Stores the registration handle for the UI component selection listener. This is used in {@link #remove()} to
     * remove the listener.
     *
     * @param componentRegistration the UI component selection listener registration, never {@code null}.
     */
    protected final void setComponentRegistration(Registration componentRegistration) {
        this.componentRegistration = componentRegistration;
    }

    /**
     * Returns the selection model.
     *
     * @return the selection model, never {@code null}.
     */
    @Nonnull
    protected final SelectionModel<T> getSelectionModel() {
        return selectionModel;
    }

    @Override
    public void remove() {
        if (selectionModelRegistration != null) {
            selectionModelRegistration.remove();
            selectionModelRegistration = null;
        }
        if (componentRegistration != null) {
            componentRegistration.remove();
            componentRegistration = null;
        }
    }
}
