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

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import net.pkhapps.appmodel4flow.selection.Selection;
import net.pkhapps.appmodel4flow.selection.SelectionModel;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Binding that binds a {@link SelectionModel} and a {@link ComboBox} together. When the selection is changed in the
 * model, the combo box is updated and vice versa. Because a combo box is a single-selection control, only the
 * {@link Selection#getFirst()} item will be selected in the combo box even if the selection contains multiple items.
 * Remember to call {@link #remove()} when the binding is no longer needed to avoid memory leaks.
 *
 * @param <T> the type of items in the combo box and selection model.
 */
@NotThreadSafe
public class SelectionModelComboBoxBinding<T> extends AbstractSelectionModelBinding<T> {

    private final ComboBox<T> comboBox;

    private boolean selectionUpdateInProgress = false;

    /**
     * Creates a new {@code SelectionModelComboBoxBinding}.
     *
     * @param selectionModel the model to bind to the combo box, never {@code null}.
     * @param comboBox       the combo box to bind to the model, never {@code null}.
     */
    public SelectionModelComboBoxBinding(@Nonnull SelectionModel<T> selectionModel, ComboBox<T> comboBox) {
        super(selectionModel);
        this.comboBox = Objects.requireNonNull(comboBox, "comboBox must not be null");
        setComponentRegistration(comboBox.addValueChangeListener(this::onComboBoxValueChangeEvent));
        updateSelection(selectionModel.getSelection());
    }

    @Override
    protected void updateSelection(@Nonnull Selection<T> selection) {
        selectionUpdateInProgress = true;
        try {
            comboBox.setValue(selection.getFirst().orElse(comboBox.getEmptyValue()));
        } finally {
            selectionUpdateInProgress = false;
        }
    }

    private void onComboBoxValueChangeEvent(@Nonnull HasValue.ValueChangeEvent<T> event) {
        if (!selectionUpdateInProgress) {
            getSelectionModel().selectOne(event.getValue());
        }
    }
}
