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

import com.vaadin.flow.component.combobox.ComboBox;
import net.pkhapps.appmodel4flow.selection.DefaultSelectionModel;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link SelectionModelComboBoxBinding}.
 */
public class SelectionModelComboBoxBindingTest {

    private ComboBox<String> field;
    private DefaultSelectionModel<String> selectionModel;
    private SelectionModelComboBoxBinding binding;

    @Before
    public void setUp() {
        field = new ComboBox<>("Label", "item1", "item2", "item3");
        selectionModel = new DefaultSelectionModel<>();
        binding = new SelectionModelComboBoxBinding<>(selectionModel, field);
    }

    @Test
    public void modelSelectionIsReflectedInField() {
        selectionModel.selectOne("item1");
        assertThat(field.getValue()).isEqualTo("item1");
    }

    @Test
    public void fieldSelectionIsReflectedInModel() {
        field.setValue("item2");
        assertThat(selectionModel.getSelection().getFirst()).contains("item2");
    }

    @Test
    public void fieldSelectionIsClearedWhenModelIsCleared() {
        selectionModel.selectOne("item1");
        selectionModel.clear();
        assertThat(field.getValue()).isNull();
    }

    @Test
    public void modelIsClearedWhenFieldSelectionIsCleared() {
        field.setValue("item2");
        field.clear();
        assertThat(selectionModel.getSelection()).isEmpty();
    }

    @Test
    public void breakBinding() {
        binding.remove();
        field.setValue("item1");
        assertThat(selectionModel.getSelection()).isEmpty();
    }
}
