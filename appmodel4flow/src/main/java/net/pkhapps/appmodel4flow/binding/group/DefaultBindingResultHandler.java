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

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import net.pkhapps.appmodel4flow.binding.PropertyFieldBinding;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Default implementation of {@link FieldBindingGroup.BindingResultHandler}.
 */
@ThreadSafe
@Slf4j
public class DefaultBindingResultHandler implements FieldBindingGroup.BindingResultHandler {

    private static final long serialVersionUID = 1L;

    /**
     * Clears the error message from the specified field. If the result handler does not know how to do that,
     * nothing happens.
     *
     * @param field the field whose error message should be cleared.
     */
    @SuppressWarnings("WeakerAccess")
    protected void clearErrorMessage(HasValue<?, ?> field) {
        setInvalid(field, false);
    }

    /**
     * Sets the error message of the specified field. If the result handler does not know how to do that, nothing
     * happens.
     *
     * @param field        the field whose error message should be set.
     * @param errorMessage the error message to set.
     */
    @SuppressWarnings("WeakerAccess")
    protected void setErrorMessage(@Nonnull HasValue<?, ?> field, @Nonnull String errorMessage) {
        Objects.requireNonNull(field, "field must not be null");
        Objects.requireNonNull(errorMessage, "errorMessage must not be null");
        try {
            var setErrorMessage = field.getClass().getMethod("setErrorMessage", String.class);
            setErrorMessage.invoke(field, errorMessage);
            log.trace("Set error message of {} to '{}'", field, errorMessage);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            log.debug("Could not invoke setErrorMessage on " + field, ex);
        }
        setInvalid(field, true);
    }

    private void setInvalid(@Nonnull HasValue<?, ?> field, boolean invalid) {
        try {
            var setInvalid = field.getClass().getMethod("setInvalid", Boolean.TYPE);
            setInvalid.invoke(field, invalid);
            log.trace("Set invalid flag of {} to {}", field, invalid);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            log.debug("Could not invoke setInvalid on " + field, ex);
        }
    }

    @Override
    public void handleBindingResult(@Nonnull PropertyFieldBinding<?, ?> binding,
                                    @Nullable Result<?> conversionResult,
                                    @Nullable Collection<ValidationResult> validationResults) {
        Objects.requireNonNull(binding, "binding must not be null");
        var errorMessages = new ArrayList<String>();

        if (conversionResult != null && conversionResult.isError()) {
            conversionResult.getMessage().ifPresent(errorMessages::add);
        }

        if (validationResults != null) {
            validationResults.stream()
                    .filter(ValidationResult::isError)
                    .map(ValidationResult::getErrorMessage)
                    .forEach(errorMessages::add);
        }

        if (errorMessages.isEmpty()) {
            clearErrorMessage(binding.getField());
        } else {
            setErrorMessage(binding.getField(), String.join("\n", errorMessages));
        }
    }
}
