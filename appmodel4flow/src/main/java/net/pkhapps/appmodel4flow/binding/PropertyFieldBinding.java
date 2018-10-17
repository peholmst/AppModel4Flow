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
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.property.Property;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Binding that binds a {@link Property} (the model) and a {@link HasValue} (the field) together in a two-way binding,
 * where changes made to the field are reflected in the model and vice versa. If the model is marked as read-only,
 * the field will also be marked read-only but not the other way around.
 *
 * @param <MODEL>        the value type of the model.
 * @param <PRESENTATION> the value type of the field.
 */
@NotThreadSafe
public class PropertyFieldBinding<MODEL, PRESENTATION> extends ObservableValueFieldBinding<MODEL, PRESENTATION>
        implements TwoWayFieldBinding<MODEL, PRESENTATION> {

    private static final long serialVersionUID = 1L;

    private final Registration propertyReadOnlyRegistration;
    private final Registration fieldValueRegistration;
    private SerializableConsumer<Result<MODEL>> converterResultHandler;
    private SerializableConsumer<Collection<ValidationResult>> validationResultHandler;
    private boolean writeInvalidModelValuesEnabled = true;
    private final List<Validator<MODEL>> validators = new ArrayList<>();
    private SerializableSupplier<String> requiredErrorMessageSupplier;

    /**
     * Creates a new {@code PropertyFieldBinding}.
     *
     * @param model     the model to bind, never {@code null}.
     * @param field     the field to bind, never {@code null}.
     * @param converter the converter to use, never {@code null}.
     */
    public PropertyFieldBinding(@Nonnull Property<MODEL> model,
                                @Nonnull HasValue<? extends HasValue.ValueChangeEvent<PRESENTATION>, PRESENTATION> field,
                                @Nonnull Converter<PRESENTATION, MODEL> converter) {
        super(model, field, converter);
        propertyReadOnlyRegistration = model.isReadOnly().addValueChangeListener(event -> updateFieldReadOnlyState());
        fieldValueRegistration = field.addValueChangeListener(event -> updatePropertyValue());
        updateFieldReadOnlyState();
    }

    @Nonnull
    @Override
    public Property<MODEL> getModel() {
        return (Property<MODEL>) super.getModel();
    }

    private void updateFieldReadOnlyState() {
        getField().setReadOnly(getModel().isReadOnly().getValue());
    }

    private void updatePropertyValue() {
        var result = getConverter().convertToModel(getField().getValue(), createValueContext());
        setPresentationValid(!result.isError());
        result.ifOk(this::writePropertyValue);
        handleConverterResult(result);
    }

    private void handleConverterResult(@Nonnull Result<MODEL> result) {
        if (converterResultHandler != null) {
            converterResultHandler.accept(result);
        }
    }

    private void writePropertyValue(@Nullable MODEL value) {
        validate(value);
        if (!isModelValid().getValue() && !writeInvalidModelValuesEnabled) {
            return;
        }
        getModel().setValue(value);
    }

    private void handleValidationResults(@Nonnull Collection<ValidationResult> results) {
        if (validationResultHandler != null) {
            validationResultHandler.accept(results);
        }
    }

    @Nonnull
    @Override
    public TwoWayFieldBinding<MODEL, PRESENTATION> withValidator(@Nonnull Validator<MODEL> validator) {
        Objects.requireNonNull(validator, "validator must not be null");
        validators.add(validator);
        return this;
    }

    @Nonnull
    @Override
    public TwoWayFieldBinding<MODEL, PRESENTATION> withConverterResultHandler(@Nullable SerializableConsumer<Result<MODEL>> converterResultHandler) {
        this.converterResultHandler = converterResultHandler;
        return this;
    }

    @Nonnull
    @Override
    public TwoWayFieldBinding<MODEL, PRESENTATION> withValidationResultHandler(@Nullable SerializableConsumer<Collection<ValidationResult>> validationResultHandler) {
        this.validationResultHandler = validationResultHandler;
        return this;
    }

    @Nonnull
    @Override
    public TwoWayFieldBinding<MODEL, PRESENTATION> withWriteInvalidModelValuesDisabled() {
        writeInvalidModelValuesEnabled = false;
        return this;
    }

    @Nonnull
    @Override
    public TwoWayFieldBinding<MODEL, PRESENTATION> asRequired(@Nonnull SerializableSupplier<String> errorMessageSupplier) {
        boolean alreadyMarkedAsRequired = this.requiredErrorMessageSupplier != null;
        this.requiredErrorMessageSupplier = Objects.requireNonNull(errorMessageSupplier, "errorMessageSupplier must not be null");
        getField().setRequiredIndicatorVisible(true);
        if (!alreadyMarkedAsRequired) { // In case somebody would call this method to change the error message supplier.
            validators.add(createRequiredValidator());
        }
        return this;
    }

    @Nonnull
    private Validator<MODEL> createRequiredValidator() {
        return (value, context) -> {
            if (getModel().isEmpty(value)) {
                return ValidationResult.error(requiredErrorMessageSupplier.get());
            } else {
                return ValidationResult.ok();
            }
        };
    }

    @Override
    public void validateModel() {
        validate(getModel().getValue());
    }

    private void validate(@Nullable MODEL value) {
        if (validators.size() > 0) {
            var valueContext = createValueContext();
            var validationResults = validators.stream().map(validator -> validator.apply(value, valueContext)).collect(Collectors.toSet());
            var hasErrors = validationResults.stream().anyMatch(ValidationResult::isError);
            setModelValid(!hasErrors);
            handleValidationResults(validationResults);
        } else {
            setModelValid(true);
        }
    }

    @Override
    public void remove() {
        fieldValueRegistration.remove();
        propertyReadOnlyRegistration.remove();
        super.remove();
    }
}
