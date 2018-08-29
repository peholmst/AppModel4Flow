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

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableSupplier;
import net.pkhapps.appmodel4flow.property.Property;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;

/**
 * Interface for a field binding that binds a model to a UI field using a two-way binding where updates the field are
 * reflected in the model and vice versa.
 *
 * @param <MODEL>        the value type of the model.
 * @param <PRESENTATION> the value type of the field.
 */
@SuppressWarnings("UnusedReturnValue")
public interface TwoWayFieldBinding<MODEL, PRESENTATION> extends FieldBinding<MODEL, PRESENTATION> {

    @Nonnull
    @Override
    Property<MODEL> getModel();

    /**
     * Configures the binding to invoke the given handler whenever a conversion from field to model is performed.
     * The result handler can be used to e.g. show error messages or clear them. The {@link #isPresentationValid()} flag
     * will be updated regardless of the presence of a handler.
     *
     * @param converterResultHandler the result handler or {@code null} (the default) if none is needed.
     * @return this binding, to allow for method chaining.
     * @see com.vaadin.flow.data.converter.Converter
     */
    @Nonnull
    TwoWayFieldBinding<MODEL, PRESENTATION> withConverterResultHandler(SerializableConsumer<Result<MODEL>> converterResultHandler);

    /**
     * Configures the binding to validate the model value using the given validator. It is possible to specify
     * multiple validators by calling this method multiple times.
     *
     * @param validator the validator to use, never {@code null}.
     * @return this binding, to allow for method chaining.
     */
    @Nonnull
    TwoWayFieldBinding<MODEL, PRESENTATION> withValidator(@Nonnull Validator<MODEL> validator);

    /**
     * Configures the binding to invoke the given handler whenever a validation of a model value is performed.
     * The result handler can be used to e.g. show error messages or clear them. The {@link #isModelValid()} flag will
     * be updated regardless of the presence of a handler.
     *
     * @param validationResultHandler the result handler or {@code null} (the default) if none is needed.
     * @return this binding, to allow for method chaining.
     * @see Validator
     */
    @Nonnull
    TwoWayFieldBinding<MODEL, PRESENTATION> withValidationResultHandler(SerializableConsumer<Collection<ValidationResult>> validationResultHandler);

    /**
     * By default, the binding will write model values to the underlying model even if they don't pass
     * {@link #withValidator(Validator) validation}. This method will disable this, allowing only valid model values
     * to pass through.
     *
     * @return this binding, to allow for method chaining.
     */
    @Nonnull
    TwoWayFieldBinding<MODEL, PRESENTATION> withWriteInvalidModelValuesDisabled();

    /**
     * Marks the field as required using the specified error message supplier.
     *
     * @param errorMessageSupplier the supplier to use for getting the error message to report if the field is empty, never {@code null}.
     * @return this binding, to allow for method chaining.
     */
    @Nonnull
    TwoWayFieldBinding<MODEL, PRESENTATION> asRequired(@Nonnull SerializableSupplier<String> errorMessageSupplier);

    /**
     * Marks the field as required using the specified error message.
     *
     * @param errorMessage the error message to report if the field is empty, never {@code null}.
     * @return this binding, to allow for method chaining.
     */
    @Nonnull
    default TwoWayFieldBinding<MODEL, PRESENTATION> asRequired(@Nonnull String errorMessage) {
        Objects.requireNonNull(errorMessage, "errorMessage must not be null");
        return asRequired(() -> errorMessage);
    }


    /**
     * Marks the field as required using a default error message.
     *
     * @return this binding, to allow for method chaining.
     * @see #asRequired(String)
     */
    @Nonnull
    default TwoWayFieldBinding<MODEL, PRESENTATION> asRequired() {
        return asRequired("Please provide a value");
    }

}
