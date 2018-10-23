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

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.binding.FieldBinding;
import net.pkhapps.appmodel4flow.binding.PropertyFieldBinding;
import net.pkhapps.appmodel4flow.binding.TwoWayFieldBinding;
import net.pkhapps.appmodel4flow.property.DefaultObservableValue;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import net.pkhapps.appmodel4flow.property.Property;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Extended version of {@link BindingGroup} especially designed for {@link FieldBinding}s and {@link TwoWayFieldBinding}s.
 * It provides features for handling {@link #withBindingResultHandler(BindingResultHandler) binding results}
 * in one place and also collectively tracks the status of the {@link Property#isDirty() dirty},
 * {@link FieldBinding#isPresentationValid() presentationValid} and {@link FieldBinding#isModelValid() modelValid} flags.
 */
@NotThreadSafe
public class FieldBindingGroup extends BindingGroup {

    private static final long serialVersionUID = 1L;

    private final DefaultObservableValue<Boolean> dirty = new DefaultObservableValue<>(false);
    private final DefaultObservableValue<Boolean> presentationValid = new DefaultObservableValue<>(true);
    private final DefaultObservableValue<Boolean> modelValid = new DefaultObservableValue<>(true);
    private final SerializableConsumer<ObservableValue.ValueChangeEvent<Boolean>> dirtyListener = (event) -> updateDirtyFlag();
    private final SerializableConsumer<ObservableValue.ValueChangeEvent<Boolean>> modelValidListener = (event) -> updateModelValidFlag();
    private final SerializableConsumer<ObservableValue.ValueChangeEvent<Boolean>> presentationValidListener = (event) -> updatePresentationValidFlag();
    private BindingResultHandler bindingResultHandler = new DefaultBindingResultHandler();

    /**
     * {@inheritDoc}
     * <p>
     * This method will automatically invoke the {@link FieldBinding#validateModel()} method on all bindings that
     * implement the {@link FieldBinding} interface, to make sure the {@link #isModelValid()} flag is correct at
     * all times.
     */
    @Nonnull
    @Override
    public FieldBindingGroup withBinding(@Nonnull Registration binding) {
        if (binding instanceof FieldBinding) {
            var fieldBinding = (FieldBinding<?, ?>) binding;
            fieldBinding.validateModel();
            fieldBinding.isModelValid().addWeakValueChangeListener(modelValidListener);
            if (!fieldBinding.isModelValid().getValue()) {
                modelValid.setValue(false);
            }
            fieldBinding.isPresentationValid().addWeakValueChangeListener(presentationValidListener);
            if (!fieldBinding.isPresentationValid().getValue()) {
                presentationValid.setValue(false);
            }
        }
        if (binding instanceof TwoWayFieldBinding) {
            var twoWayFieldBinding = (TwoWayFieldBinding<?, ?>) binding;
            twoWayFieldBinding.withBindingResultHandler(this::handleBindingResult);
            twoWayFieldBinding.getModel().isDirty().addWeakValueChangeListener(dirtyListener);
            if (twoWayFieldBinding.getModel().isDirty().getValue()) {
                dirty.setValue(true);
            }
        }
        return (FieldBindingGroup) super.withBinding(binding);
    }

    @Override
    protected void dispose(@Nonnull Registration binding) {
        super.dispose(binding);
        if (binding instanceof TwoWayFieldBinding) {
            ((TwoWayFieldBinding<?, ?>) binding).withBindingResultHandler(null);
        }
    }

    /**
     * Returns whether there are any {@link Property#isDirty() dirty} properties among the bindings in this group.
     *
     * @return true if at least one property is dirty, false if all of them are clean.
     */
    @Nonnull
    public ObservableValue<Boolean> isDirty() {
        return dirty;
    }

    /**
     * Invokes {@link Property#resetDirtyFlag()} for all bound properties.
     */
    @SuppressWarnings("WeakerAccess")
    public void resetDirtyFlag() {
        getTwoWayBindings().forEach(binding -> binding.getModel().resetDirtyFlag());
    }

    /**
     * Invokes {@link Property#discard()} for all bound properties.
     */
    @SuppressWarnings("WeakerAccess")
    public void discard() {
        getTwoWayBindings().forEach(binding -> binding.getModel().discard());
    }

    private void updateDirtyFlag() {
        // TODO Optimize so that this is only called once when set as a result of resetDirtyFlag or discard
        dirty.setValue(getTwoWayBindings().map(binding -> binding.getModel().isDirty())
                .anyMatch(ObservableValue::getValue));
    }

    private void updatePresentationValidFlag() {
        presentationValid.setValue(getFieldBindings().map(FieldBinding::isPresentationValid)
                .allMatch(ObservableValue::getValue));
    }

    private void updateModelValidFlag() {
        modelValid.setValue(getFieldBindings().map(FieldBinding::isModelValid)
                .allMatch(ObservableValue::getValue));
    }

    @Nonnull
    private Stream<TwoWayFieldBinding<?, ?>> getTwoWayBindings() {
        return getBindings().filter(TwoWayFieldBinding.class::isInstance)
                .map(binding -> (TwoWayFieldBinding<?, ?>) binding);
    }

    @Nonnull
    private Stream<FieldBinding<?, ?>> getFieldBindings() {
        return getBindings().filter(FieldBinding.class::isInstance)
                .map(binding -> (FieldBinding<?, ?>) binding);
    }

    /**
     * Returns whether all bindings have valid presentation values.
     *
     * @return true if all bindings have valid presentation values, false if at least one does not.
     * @see FieldBinding#isPresentationValid()
     */
    @Nonnull
    public ObservableValue<Boolean> isPresentationValid() {
        return presentationValid;
    }

    /**
     * Returns whether all bindings have valid model values.
     *
     * @return true if all bindings have valid model values, false if at least one does not.
     * @see FieldBinding#isModelValid()
     */
    @Nonnull
    public ObservableValue<Boolean> isModelValid() {
        return modelValid;
    }

    /**
     * Specifies a result handler that is used to collectively handle all
     * {@link TwoWayFieldBinding#withBindingResultHandler(TwoWayFieldBinding.BindingResultHandler) binding results} coming from the
     * bindings. By default, a {@link DefaultBindingResultHandler} is used. It can be disabled by passing in
     * {@code null}.
     *
     * @param bindingResultHandler the result handler or {@code null} to use none.
     * @return this field binding group, to allow for method chaining.
     */
    @SuppressWarnings("WeakerAccess")
    @Nonnull
    public FieldBindingGroup withBindingResultHandler(@Nullable BindingResultHandler bindingResultHandler) {
        this.bindingResultHandler = bindingResultHandler;
        return this;
    }

    private <MODEL, PRESENTATION> void handleBindingResult(@Nonnull PropertyFieldBinding<MODEL, PRESENTATION> binding,
                                                           @Nonnull Result<MODEL> conversionResult,
                                                           @Nonnull Collection<ValidationResult> validationResults) {
        if (bindingResultHandler != null) {
            bindingResultHandler.handleBindingResult(binding, conversionResult, validationResults);
        }
    }

    /**
     * Functional interface for collectively handling the validation results of all bindings in a
     * {@link FieldBindingGroup}. This interface is almost identical to {@link TwoWayFieldBinding.BindingResultHandler}
     * with the exception of the generics.
     */
    @FunctionalInterface
    interface BindingResultHandler extends Serializable {

        /**
         * Called whenever a value conversion or a value validation has taken place inside a binding.
         *
         * @param binding           the binding that invoked the handler, never {@code null}.
         * @param conversionResult  the conversion result when converting from presentation to model, never {@code null}.
         * @param validationResults the validation results, if any, when validating the converted model value, never {@code null}.
         */
        void handleBindingResult(@Nonnull PropertyFieldBinding<?, ?> binding,
                                 @Nonnull Result<?> conversionResult,
                                 @Nonnull Collection<ValidationResult> validationResults);
    }
}
