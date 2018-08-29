package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.property.DefaultObservableValue;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import net.pkhapps.appmodel4flow.property.Property;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Extended version of {@link BindingGroup} especially designed for {@link FieldBinding}s and {@link TwoWayFieldBinding}s.
 * It provides features for handling {@link #withConverterResultHandler(ConverterResultHandler) converter results} and
 * {@link #withValidationResultHandler(ValidationResultHandler) validation results} in one place and also collectively
 * tracks the status of the {@link Property#isDirty() dirty}, {@link FieldBinding#isPresentationValid() presentationValid}
 * and {@link FieldBinding#isModelValid() modelValid} flags.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@NotThreadSafe
public class FieldBindingGroup extends BindingGroup {

    private final DefaultObservableValue<Boolean> dirty = new DefaultObservableValue<>(false);
    private final DefaultObservableValue<Boolean> presentationValid = new DefaultObservableValue<>(true);
    private final DefaultObservableValue<Boolean> modelValid = new DefaultObservableValue<>(true);
    private final SerializableConsumer<ObservableValue.ValueChangeEvent<Boolean>> dirtyListener = (event) -> updateDirtyFlag();
    private final SerializableConsumer<ObservableValue.ValueChangeEvent<Boolean>> modelValidListener = (event) -> updateModelValidFlag();
    private final SerializableConsumer<ObservableValue.ValueChangeEvent<Boolean>> presentationValidListener = (event) -> updatePresentationValidFlag();
    private ConverterResultHandler converterResultHandler;
    private ValidationResultHandler validationResultHandler;

    @Nonnull
    @Override
    public FieldBindingGroup withBinding(@Nonnull Registration binding) {
        if (binding instanceof TwoWayFieldBinding) {
            var twoWayFieldBinding = (TwoWayFieldBinding<?, ?>) binding;
            twoWayFieldBinding.withConverterResultHandler(result -> handleConverterResult(twoWayFieldBinding, result));
            twoWayFieldBinding.withValidationResultHandler(results -> handleValidationResult(twoWayFieldBinding, results));
            twoWayFieldBinding.getModel().isDirty().addWeakValueChangeListener(dirtyListener);
            if (twoWayFieldBinding.getModel().isDirty().getValue()) {
                dirty.setValue(true);
            }
        }
        if (binding instanceof FieldBinding) {
            var fieldBinding = (FieldBinding<?, ?>) binding;
            fieldBinding.isModelValid().addWeakValueChangeListener(modelValidListener);
            if (!fieldBinding.isModelValid().getValue()) {
                modelValid.setValue(false);
            }
            fieldBinding.isPresentationValid().addWeakValueChangeListener(presentationValidListener);
            if (!fieldBinding.isPresentationValid().getValue()) {
                presentationValid.setValue(false);
            }
        }
        return (FieldBindingGroup) super.withBinding(binding);
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
    public void resetDirtyFlag() {
        getTwoWayBindings().forEach(binding -> binding.getModel().resetDirtyFlag());
    }

    /**
     * Invokes {@link Property#discard()} for all bound properties.
     */
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
     * {@link TwoWayFieldBinding#withConverterResultHandler(SerializableConsumer) converter results} coming from the
     * bindings.
     *
     * @param converterResultHandler the result handler or {@code null} to use none.
     * @return this field binding group, to allow for method chaining.
     */
    @Nonnull
    public FieldBindingGroup withConverterResultHandler(ConverterResultHandler converterResultHandler) {
        this.converterResultHandler = converterResultHandler;
        return this;
    }

    /**
     * Specifies a result handler that is used to collectively handle all
     * {@link TwoWayFieldBinding#withValidationResultHandler(SerializableConsumer) validation results} coming from the
     * bindings.
     *
     * @param validationResultHandler the result handler or {@code null} to use none.
     * @return this field binding group, to allow for method chaining.
     */
    @Nonnull
    public FieldBindingGroup withValidationResultHandler(ValidationResultHandler validationResultHandler) {
        this.validationResultHandler = validationResultHandler;
        return this;
    }

    private void handleConverterResult(@Nonnull TwoWayFieldBinding<?, ?> binding, Result<?> result) {
        if (converterResultHandler != null) {
            converterResultHandler.handleConverterResult(binding, result);
        }
    }

    private void handleValidationResult(@Nonnull TwoWayFieldBinding<?, ?> binding,
                                        @Nonnull Collection<ValidationResult> results) {
        if (validationResultHandler != null) {
            validationResultHandler.handleValidationResult(binding, results);
        }
    }

    /**
     * Functional interface for {@link FieldBindingGroup#withConverterResultHandler(ConverterResultHandler) converter result handlers}.
     */
    @FunctionalInterface
    public interface ConverterResultHandler extends Serializable {

        /**
         * Handles the specified converter result.
         *
         * @param binding the binding that invoked the converter, never {@code null}.
         * @param result  the result of the {@link com.vaadin.flow.data.converter.Converter converter}.
         */
        void handleConverterResult(@Nonnull TwoWayFieldBinding<?, ?> binding, Result<?> result);
    }

    /**
     * Functional interface for {@link FieldBindingGroup#withValidationResultHandler(ValidationResultHandler) validation result handlers}.
     */
    @FunctionalInterface
    public interface ValidationResultHandler extends Serializable {

        /**
         * Handles the specified validation result.
         *
         * @param binding the binding that invoked the validators, never {@code null}.
         * @param results the results of the {@link com.vaadin.flow.data.binder.Validator validators}.
         */
        void handleValidationResult(@Nonnull TwoWayFieldBinding<?, ?> binding,
                                    @Nonnull Collection<ValidationResult> results);
    }
}
