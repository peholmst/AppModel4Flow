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
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.property.ObservableValue;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Interface for a field binding that binds a model to a UI field.
 *
 * @param <MODEL>        the value type of the model.
 * @param <PRESENTATION> the value type of the field.
 */
public interface FieldBinding<MODEL, PRESENTATION> extends Serializable, Registration {

    /**
     * Returns the UI field.
     *
     * @return the field, never {@code null}.
     */
    @Nonnull
    HasValue<? extends HasValue.ValueChangeEvent<PRESENTATION>, PRESENTATION> getField();

    /**
     * Returns the model.
     *
     * @return the model, never {@code null}.
     */
    @Nonnull
    ObservableValue<MODEL> getModel();

    /**
     * Returns whether the presentation value is valid. If the presentation value is not valid, it means the
     * value in the UI could not be converted to the value needed by the model. This value is always true initially,
     * after the binding has been made but before any changes have been made to the field or the model.
     *
     * @return true if the presentation is valid, false if it is not.
     * @see #isModelValid()
     */
    @Nonnull
    ObservableValue<Boolean> isPresentationValid();

    /**
     * Returns whether the model value is valid or not. If the model value is not valid, it means the value in the UI
     * was successfully converted to a value needed by the model, but did not pass through some implementation specific
     * validation mechanism. This value is always true initially, after the binding has been made but before any changes
     * have been made to the field or the model, regardless of what the actual model value is. This is because this flag
     * is controlled by the binding, based on any input that the user enters into the field.
     *
     * @return true if the model is valid, false if it is not.
     * @see #isPresentationValid()
     */
    @Nonnull
    ObservableValue<Boolean> isModelValid();

    /**
     * Breaks the binding, removing any listeners registered with the model and/or the UI field.
     */
    @Override
    void remove();
}
