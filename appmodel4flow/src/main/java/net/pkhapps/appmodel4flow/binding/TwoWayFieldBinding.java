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
import com.vaadin.flow.function.SerializableSupplier;
import net.pkhapps.appmodel4flow.property.Property;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

/**
 * Interface for a field binding that binds a model to a UI field using a two-way binding where updates the field are
 * reflected in the model and vice versa.
 *
 * @param <MODEL>        the value type of the model.
 * @param <PRESENTATION> the value type of the field.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface TwoWayFieldBinding<MODEL, PRESENTATION> extends FieldBinding<MODEL, PRESENTATION> {

    @Nonnull
    @Override
    Property<MODEL> getModel();

    /**
     * Configures the binding to validate the model value using the given validator. It is possible to specify
     * multiple validators by calling this method multiple times. If you want to invoke the validator manually before
     * the user has changed the field, call {@link #validateModel()} or {@link #validateModelAndHandleResults()}.
     *
     * @param validator the validator to use, never {@code null}.
     * @return this binding, to allow for method chaining.
     */
    @Nonnull
    TwoWayFieldBinding<MODEL, PRESENTATION> withValidator(@Nonnull Validator<MODEL> validator);

    /**
     * Specifies a {@link BindingResultHandler} that is used to handle validation and conversion errors. This makes
     * it possible to give more detailed feedback to the user than simply relying on the {@link #isModelValid()}
     * and {@link #isPresentationValid()} flags. These flags will still be updated accordingly even when there is no
     * binding result handler.
     *
     * @param bindingResultHandler the binding result handler to use, or {@code null} to not use any handler.
     * @return this binding, to allow for method chaining.
     */
    @Nonnull
    TwoWayFieldBinding<MODEL, PRESENTATION> withBindingResultHandler(
            @Nullable BindingResultHandler<MODEL, PRESENTATION> bindingResultHandler);

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
     * Marks the field as required using the specified error message supplier. If you want to invoke the required value
     * check manually before the user has changed the field, call {@link #validateModel()} or
     * {@link #validateModelAndHandleResults()}.
     *
     * @param errorMessageSupplier the supplier to use for getting the error message to report if the field is empty,
     *                             never {@code null}.
     * @return this binding, to allow for method chaining.
     */
    @Nonnull
    TwoWayFieldBinding<MODEL, PRESENTATION> asRequired(@Nonnull SerializableSupplier<String> errorMessageSupplier);

    /**
     * Marks the field as required using the specified error message. If you want to invoke the required value
     * check manually before the user has changed the field, call {@link #validateModel()} or
     * {@link #validateModelAndHandleResults()}.
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

    /**
     * Does the same as {@link #validateModel()} but reports the results to the
     * {@link #withBindingResultHandler(BindingResultHandler) binding result handler}.
     */
    void validateModelAndHandleResults();

    /**
     * Functional interface for the {@link #withBindingResultHandler(BindingResultHandler) binding result handler}
     * that can be used to update the user interface in case of conversion or validation errors.
     */
    @FunctionalInterface
    interface BindingResultHandler<MODEL, PRESENTATION> extends Serializable {

        /**
         * Called whenever a value conversion or a value validation has taken place inside a binding.
         *
         * @param binding           the binding that invoked the handler, never {@code null}.
         * @param conversionResult  the conversion result when converting from presentation to model, never {@code null}.
         * @param validationResults the validation results, if any, when validating the converted model value, never {@code null}.
         */
        void handleBindingResult(@Nonnull PropertyFieldBinding<MODEL, PRESENTATION> binding,
                                 @Nonnull Result<MODEL> conversionResult,
                                 @Nonnull Collection<ValidationResult> validationResults);
    }
}
