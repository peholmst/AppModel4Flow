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
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link ComponentBinding}.
 */
@SuppressWarnings("Convert2Diamond") // var and <> seems to produce a raw variable without generics
public class ComponentBindingTest {

    @Test
    public void modelStateIsTransferredWhenBindingIsCreated() {
        var component = new TextField();
        var model = new DefaultObservableValue<Boolean>(true);
        new ComponentBinding<>(model, component, TextField::setReadOnly);
        assertThat(component.isReadOnly()).isTrue();
    }

    @Test
    public void componentIsUpdatedWhenModelIsChanged() {
        var component = new TextField();
        var model = new DefaultObservableValue<Boolean>(false);
        new ComponentBinding<>(model, component, TextField::setReadOnly);
        assertThat(component.isReadOnly()).isFalse();
        model.setValue(true);
        assertThat(component.isReadOnly()).isTrue();
    }

    @Test
    public void breakBinding() {
        var component = new TextField();
        var model = new DefaultObservableValue<Boolean>(true);
        var binding = new ComponentBinding<>(model, component, TextField::setReadOnly);
        model.setValue(false);
        binding.remove();
        model.setValue(true);
        assertThat(component.isReadOnly()).isFalse();
    }
}
