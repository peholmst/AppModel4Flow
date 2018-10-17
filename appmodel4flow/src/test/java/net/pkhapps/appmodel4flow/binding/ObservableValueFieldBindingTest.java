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

import com.vaadin.flow.component.textfield.TextField;
import net.pkhapps.appmodel4flow.property.DefaultObservableValue;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link ObservableValueFieldBinding}.
 */
public class ObservableValueFieldBindingTest {

    private TextField field;
    private DefaultObservableValue<String> model;
    private ObservableValueFieldBinding<String, String> binding;

    @Before
    public void setUp() {
        field = new TextField();
        model = new DefaultObservableValue<>();
        binding = new ObservableValueFieldBinding<>(model, field, new ObservableValueFieldBinding.PassThroughConverter<>());
    }

    @Test
    public void initialStateAfterCreation() {
        assertThat(field.getValue()).isEmpty();
        assertThat(field.isReadOnly()).isTrue();
        assertThat(model.getValue()).isNull();
        assertThat(binding.getModel()).isSameAs(model);
        assertThat(binding.getField()).isSameAs(field);
        assertThat(binding.isModelValid().getValue()).isTrue();
        assertThat(binding.isPresentationValid().getValue()).isTrue();
    }

    @Test
    public void initialStateAfterCreationWhenModelContainsAnExistingValue() {
        model = new DefaultObservableValue<>("hello");
        field = new TextField();
        binding = new ObservableValueFieldBinding<>(model, field, new ObservableValueFieldBinding.PassThroughConverter<>());
        assertThat(field.getValue()).isEqualTo("hello");
        assertThat(binding.isModelValid().getValue()).isTrue();
        assertThat(binding.isPresentationValid().getValue()).isTrue();
    }

    @Test
    public void setFieldValue_nothingHappensToModel() {
        field.setValue("hello");
        assertThat(model.getValue()).isNull();
    }

    @Test
    public void setModelValue_fieldIsUpdated() {
        model.setValue("hello");
        assertThat(field.getValue()).isEqualTo("hello");
    }

    @Test
    public void remove_fieldIsNoLongerUpdated() {
        binding.remove();
        model.setValue("hello");
        assertThat(field.getValue()).isEmpty();
    }
}
