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

package net.pkhapps.appmodel4flow.binding.group;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import net.pkhapps.appmodel4flow.binding.PropertyFieldBinding;
import net.pkhapps.appmodel4flow.property.DefaultProperty;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link FieldBindingGroup}.
 */
public class FieldBindingGroupTest {

    private TextField stringField;
    private TextField integerField;
    private Checkbox booleanField;
    private DefaultProperty<String> stringProperty;
    private DefaultProperty<Integer> integerProperty;
    private DefaultProperty<Boolean> booleanProperty;
    private FieldBindingGroup group;

    @Before
    public void setUp() {
        stringField = new TextField();
        integerField = new TextField();
        booleanField = new Checkbox();

        stringProperty = new DefaultProperty<>();
        integerProperty = new DefaultProperty<>();
        booleanProperty = new DefaultProperty<>(false);

        group = new FieldBindingGroup();
        group.withBinding(new PropertyFieldBinding<>(stringProperty, stringField, Converter.identity())
                .withValidator(new StringLengthValidator("lengthError", 3, 10)));
        group.withBinding(new PropertyFieldBinding<>(integerProperty, integerField, new StringToIntegerConverter("intConversionError")));
        group.withBinding(new PropertyFieldBinding<>(booleanProperty, booleanField, Converter.identity()));
    }

    @Test
    public void dirtyFlag() {
        assertThat(group.isDirty().getValue()).isFalse();
        booleanField.setValue(true);
        assertThat(group.isDirty().getValue()).isTrue();
        booleanProperty.discard();
        assertThat(group.isDirty().getValue()).isFalse();
    }

    @Test
    public void presentationValidFlag() {
        assertThat(group.isPresentationValid().getValue()).isTrue();
        integerField.setValue("this is not a number");
        assertThat(group.isPresentationValid().getValue()).isFalse();
        integerField.setValue("50");
        assertThat(group.isPresentationValid().getValue()).isTrue();
    }

    @Test
    public void modelValidFlag() {
        assertThat(group.isModelValid().getValue()).isTrue();
        stringField.setValue("this string is too long");
        assertThat(group.isModelValid().getValue()).isFalse();
        stringField.setValue("this is OK");
        assertThat(group.isModelValid().getValue()).isTrue();
    }

    @Test
    public void converterResultHandler_error() {
        var handlerInvoked = new AtomicBoolean(false);
        group.withBindingResultHandler((binding, conversionResult, validationResults) -> {
            assertThat(binding.getModel()).isSameAs(integerProperty);
            assertThat(conversionResult.isError()).isTrue();
            handlerInvoked.set(true);
        });
        integerField.setValue("this is not a number");
        assertThat(handlerInvoked).isTrue();
    }

    @Test
    public void converterResultHandler_success() {
        var handlerInvoked = new AtomicBoolean(false);
        group.withBindingResultHandler((binding, conversionResult, validationResults) -> {
            assertThat(binding.getModel()).isSameAs(integerProperty);
            assertThat(conversionResult.isError()).isFalse();
            handlerInvoked.set(true);
        });
        integerField.setValue("123");
        assertThat(handlerInvoked).isTrue();
    }

    @Test
    public void validationResultHandler_error() {
        var handlerInvoked = new AtomicBoolean(false);
        group.withBindingResultHandler((binding, conversionResult, validationResults) -> {
            assertThat(binding.getModel()).isSameAs(stringProperty);
            assertThat(validationResults).anyMatch(ValidationResult::isError);
            handlerInvoked.set(true);
        });
        stringField.setValue("ts");
        assertThat(handlerInvoked).isTrue();
    }

    @Test
    public void validationResultHandler_success() {
        var handlerInvoked = new AtomicBoolean(false);
        group.withBindingResultHandler((binding, conversionResult, validationResults) -> {
            assertThat(binding.getModel()).isSameAs(stringProperty);
            assertThat(validationResults).noneMatch(ValidationResult::isError);
            handlerInvoked.set(true);
        });
        stringField.setValue("proper");
        assertThat(handlerInvoked).isTrue();
    }
}
