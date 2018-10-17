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
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import net.pkhapps.appmodel4flow.property.DefaultProperty;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link PropertyFieldBinding}.
 */
public class PropertyFieldBindingTest {

    private TextField field;
    private DefaultProperty<Integer> model;
    private PropertyFieldBinding<Integer, String> binding;

    @Before
    public void setUp() {
        field = new TextField() {
            private boolean requiredVisible = false;

            @Override
            public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
                // Need to override because the real method requires access to the current Page.
                requiredVisible = requiredIndicatorVisible;
            }

            @Override
            public boolean isRequiredIndicatorVisible() {
                return requiredVisible;
            }
        };
        model = new DefaultProperty<>();
        binding = new PropertyFieldBinding<>(model, field, new StringToIntegerConverter("converterError"));
    }

    @Test
    public void initialStateAfterCreation() {
        assertThat(model.isEmpty()).isTrue();
        assertThat(model.isReadOnly().getValue()).isFalse();
        assertThat(field.getValue()).isEmpty();
        assertThat(field.isReadOnly()).isFalse();
        assertThat(binding.isModelValid().getValue()).isTrue();
        assertThat(binding.isPresentationValid().getValue()).isTrue();
    }

    @Test
    public void setReadOnly_fieldIsUpdated() {
        model.setReadOnly(true);
        assertThat(field.isReadOnly()).isTrue();
    }

    @Test
    public void setFieldValue_valueIsValid_modelIsUpdated() {
        field.setValue("123");
        assertThat(model.getValue()).isEqualTo(123);
        assertThat(binding.isPresentationValid().getValue()).isTrue();
    }

    @Test
    public void setFieldValue_valueIsEmpty_modelIsUpdated() {
        model.setValue(123);
        field.setValue("");
        assertThat(model.isEmpty()).isTrue();
    }

    @Test
    public void setFieldValue_valueIsValid_handlerIsInvoked() {
        var resultHandler = new AtomicReference<Result<Integer>>();
        binding.withConverterResultHandler(resultHandler::set);
        field.setValue("123");
        assertThat(resultHandler.get().isError()).isFalse();
    }

    @Test
    public void setFieldValue_valueIsInvalid_handlerIsInvoked() {
        var resultHandler = new AtomicReference<Result<Integer>>();
        binding.withConverterResultHandler(resultHandler::set);
        field.setValue("this is not a number");
        assertThat(resultHandler.get().isError()).isTrue();
    }

    @Test
    public void setFieldValue_valueIsInvalid_modelIsNotUpdated() {
        field.setValue("this is not a number");
        assertThat(model.isEmpty()).isTrue();
        assertThat(binding.isPresentationValid().getValue()).isFalse();
    }

    @Test
    public void setFieldValue_validatorPresentAndPasses_modelIsUpdated() {
        binding.withValidator(new IntegerRangeValidator("intError", 0, 100));
        field.setValue("50");
        assertThat(model.getValue()).isEqualTo(50);
        assertThat(binding.isModelValid().getValue()).isTrue();
    }

    @Test
    public void setFieldValue_validatorPresentAndPasses_handlerIsInvoked() {
        var resultHandler = new AtomicReference<Collection<ValidationResult>>();
        binding.withValidationResultHandler(resultHandler::set);
        binding.withValidator(new IntegerRangeValidator("intError", 0, 100));
        field.setValue("50");
        assertThat(resultHandler.get()).noneMatch(ValidationResult::isError);
    }

    @Test
    public void setFieldValue_validatorPresentAndBlocks_modelIsStillUpdated() {
        binding.withValidator(new IntegerRangeValidator("intError", 0, 100));
        field.setValue("110");
        assertThat(model.getValue()).isEqualTo(110);
        assertThat(binding.isModelValid().getValue()).isFalse();
    }

    @Test
    public void setFieldValue_validatorPresentAndBlocks_handlerIsInvoked() {
        var resultHandler = new AtomicReference<Collection<ValidationResult>>();
        binding.withValidationResultHandler(resultHandler::set);
        binding.withValidator(new IntegerRangeValidator("intError", 0, 100));
        field.setValue("110");
        assertThat(resultHandler.get()).allMatch(ValidationResult::isError);
    }

    @Test
    public void setFieldValue_validatorPresentAndBlocksAndWriteThroughDisabled_modelIsNotUpdated() {
        binding.withValidator(new IntegerRangeValidator("intError", 0, 100));
        binding.withWriteInvalidModelValuesDisabled();
        field.setValue("110");
        assertThat(model.isEmpty()).isTrue();
        assertThat(binding.isModelValid().getValue()).isFalse();
    }

    @Test
    public void asRequired_initialState_noErrorsReportedBeforeUserHasTouchedTheField() {
        var resultHandler = new AtomicReference<Collection<ValidationResult>>();
        binding.withValidationResultHandler(resultHandler::set);
        binding.asRequired("reqError");

        assertThat(model.isEmpty()).isTrue();
        assertThat(field.isRequiredIndicatorVisible()).isTrue();
        assertThat(field.isEmpty()).isTrue();
        assertThat(resultHandler.get()).isNull();
        assertThat(binding.isModelValid().getValue()).isTrue();
        assertThat(binding.isPresentationValid().getValue()).isTrue();
    }

    @Test
    public void asRequired_userHasLeftTheFieldEmpty_validationErrorIsReported() {
        var resultHandler = new AtomicReference<Collection<ValidationResult>>();
        binding.withValidationResultHandler(resultHandler::set);
        binding.asRequired("reqError");

        field.setValue("110");

        assertThat(model.isEmpty()).isFalse();

        field.setValue("");

        assertThat(model.isEmpty()).isTrue();
        assertThat(binding.isModelValid().getValue()).isFalse();
        assertThat(resultHandler.get()).isNotEmpty();
        assertThat(resultHandler.get()).anyMatch(r -> r.isError() && r.getErrorMessage().equals("reqError"));
    }

    @Test
    public void asRequired_userCorrectedEmptyValue_validationErrorIsRemoved() {
        var resultHandler = new AtomicReference<Collection<ValidationResult>>();
        binding.withValidationResultHandler(resultHandler::set);
        binding.asRequired("reqError");
        field.setValue("110");
        field.setValue("");
        field.setValue("123");

        assertThat(model.isEmpty()).isFalse();
        assertThat(binding.isModelValid().getValue()).isTrue();
        assertThat(resultHandler.get()).isNotEmpty();
        assertThat(resultHandler.get()).noneMatch(ValidationResult::isError);
    }

    @Test
    public void validateModel_requiredValueThatHasNotBeenChangedYet_validationErrorIsReported() {
        var resultHandler = new AtomicReference<Collection<ValidationResult>>();
        binding.withValidationResultHandler(resultHandler::set);
        binding.asRequired("reqError");

        binding.validateModel();

        assertThat(model.isEmpty()).isTrue();
        assertThat(binding.isModelValid().getValue()).isFalse();
        assertThat(resultHandler.get()).isNotEmpty();
        assertThat(resultHandler.get()).anyMatch(r -> r.isError() && r.getErrorMessage().equals("reqError"));
    }
}
