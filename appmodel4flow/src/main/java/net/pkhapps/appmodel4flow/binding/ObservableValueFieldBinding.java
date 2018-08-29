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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.property.DefaultObservableValue;
import net.pkhapps.appmodel4flow.property.ObservableValue;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Locale;
import java.util.Objects;

/**
 * Binding that binds a {@link ObservableValue} (the model) and a {@link HasValue} (the field) together in a one-way
 * binding, where the field is marked as read-only. Changes made to the model will be reflected in the field, but not
 * the other way around.
 *
 * @param <MODEL>        the value type of the model.
 * @param <PRESENTATION> the value type of the field.
 */
@SuppressWarnings("WeakerAccess")
@NotThreadSafe
public class ObservableValueFieldBinding<MODEL, PRESENTATION> implements FieldBinding<MODEL, PRESENTATION> {

    private final ObservableValue<MODEL> model;
    private final HasValue<? extends HasValue.ValueChangeEvent<PRESENTATION>, PRESENTATION> field;
    private final Converter<PRESENTATION, MODEL> converter;
    private final Registration modelRegistration;
    private final DefaultObservableValue<Boolean> presentationValid = new DefaultObservableValue<>(true);
    private final DefaultObservableValue<Boolean> modelValid = new DefaultObservableValue<>(true);

    /**
     * Creates a new {@code ObservableValueFieldBinding}.
     *
     * @param model     the model to bind, never {@code null}.
     * @param field     the field to bind, never {@code null}.
     * @param converter the converter to use, never {@code null}.
     */
    public ObservableValueFieldBinding(@Nonnull ObservableValue<MODEL> model,
                                       @Nonnull HasValue<? extends HasValue.ValueChangeEvent<PRESENTATION>, PRESENTATION> field,
                                       @Nonnull Converter<PRESENTATION, MODEL> converter) {
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.field = Objects.requireNonNull(field, "field must not be null");
        this.converter = Objects.requireNonNull(converter, "converter must not be null");
        field.setReadOnly(true);
        modelRegistration = model.addValueChangeListener(event -> updateFieldState());
        updateFieldState();
    }

    @Nonnull
    @Override
    public HasValue<? extends HasValue.ValueChangeEvent<PRESENTATION>, PRESENTATION> getField() {
        return field;
    }

    @Nonnull
    @Override
    public ObservableValue<MODEL> getModel() {
        return model;
    }

    /**
     * Returns the converter that is used to convert between model values and presentation values.
     *
     * @return the converter, never {@code null}.
     */
    @Nonnull
    protected Converter<PRESENTATION, MODEL> getConverter() {
        return converter;
    }

    private void updateFieldState() {
        if (model.isEmpty()) {
            field.setValue(field.getEmptyValue());
        } else {
            field.setValue(converter.convertToPresentation(model.getValue(), createValueContext()));
        }
    }

    /**
     * Creates and returns a new {@link ValueContext} to be used by the {@link #getConverter() converter}.
     *
     * @return the value context, never {@code null}.
     */
    @Nonnull
    protected ValueContext createValueContext() {
        if (field instanceof Component) {
            return new ValueContext((Component) field);
        } else {
            Locale locale = null;
            if (UI.getCurrent() != null) {
                locale = UI.getCurrent().getLocale();
            }
            if (locale == null) {
                locale = Locale.getDefault();
            }
            return new ValueContext(null, field, locale);
        }
    }

    @Override
    public void remove() {
        modelRegistration.remove();
    }

    @Nonnull
    @Override
    public ObservableValue<Boolean> isPresentationValid() {
        return presentationValid;
    }

    /**
     * Sets the value of the {@link #isPresentationValid() presentationValid} flag.
     *
     * @param presentationValid the new flag value.
     */
    protected void setPresentationValid(boolean presentationValid) {
        this.presentationValid.setValue(presentationValid);
    }

    @Nonnull
    @Override
    public ObservableValue<Boolean> isModelValid() {
        return modelValid;
    }

    /**
     * Sets the value of the {@link #isModelValid()} () presentationValid} flag.
     *
     * @param modelValid the new flag value.
     */
    protected void setModelValid(boolean modelValid) {
        this.modelValid.setValue(modelValid);
    }

    /**
     * A special implementation of {@link Converter} to be used when the presentation and model values are of the same
     * type.
     *
     * @param <T> the type of the presentation and model values.
     */
    @ThreadSafe
    public static class PassThroughConverter<T> implements Converter<T, T> {

        @Override
        public Result<T> convertToModel(T value, ValueContext context) {
            return Result.ok(value);
        }

        @Override
        public T convertToPresentation(T value, ValueContext context) {
            return value;
        }
    }
}
