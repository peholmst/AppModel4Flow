package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.property.DefaultObservableValue;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import net.pkhapps.appmodel4flow.property.Property;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Extended version of {@link Binder} especially designed for {@link FieldBinding}s and {@link TwoWayFieldBinding}s.
 * It provides features for handling {@link #withConverterErrorHandler(ConverterErrorHandler) converter errors} and
 * {@link #withValidationErrorHandler(ValidationErrorHandler) validation errors} in one place and also collectively
 * tracks the status of the {@link Property#isDirty() dirty}, {@link FieldBinding#isPresentationValid() presentationValid}
 * and {@link FieldBinding#isModelValid() modelValid} flags.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FieldBinder extends Binder {

    private final DefaultObservableValue<Boolean> dirty = new DefaultObservableValue<>(false);
    private final DefaultObservableValue<Boolean> presentationValid = new DefaultObservableValue<>(true);
    private final DefaultObservableValue<Boolean> modelValid = new DefaultObservableValue<>(true);
    private final SerializableConsumer<ObservableValue.ValueChangeEvent<Boolean>> dirtyListener = (event) -> updateDirtyFlag();
    private final SerializableConsumer<ObservableValue.ValueChangeEvent<Boolean>> modelValidListener = (event) -> updateModelValidFlag();
    private final SerializableConsumer<ObservableValue.ValueChangeEvent<Boolean>> presentationValidListener = (event) -> updatePresentationValidFlag();
    private ConverterErrorHandler converterErrorHandler;
    private ValidationErrorHandler validationErrorHandler;

    @Nonnull
    @Override
    public FieldBinder withBinding(@Nonnull Registration binding) {
        if (binding instanceof TwoWayFieldBinding) {
            var twoWayFieldBinding = (TwoWayFieldBinding<?, ?>) binding;
            twoWayFieldBinding.withConverterErrorHandler(error -> handleConverterError(twoWayFieldBinding, error));
            twoWayFieldBinding.withValidationErrorHandler(errors -> handleValidationError(twoWayFieldBinding, errors));
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
        return (FieldBinder) super.withBinding(binding);
    }

    /**
     * Returns whether there are any {@link Property#isDirty() dirty} properties among the bindings in this binder.
     *
     * @return true if at least one property is dirty, false if all of them are clean.
     */
    @Nonnull
    public ObservableValue<Boolean> isDirty() {
        return dirty;
    }

    private void updateDirtyFlag() {
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
     * Specifies an error handler that is used to collectively handle all
     * {@link TwoWayFieldBinding#withConverterErrorHandler(SerializableConsumer) converter errors} coming from the
     * bindings.
     *
     * @param converterErrorHandler the error handler or {@code null} to use none.
     * @return this field binder, to allow for method chaining.
     */
    @Nonnull
    public FieldBinder withConverterErrorHandler(ConverterErrorHandler converterErrorHandler) {
        this.converterErrorHandler = converterErrorHandler;
        return this;
    }

    /**
     * Specifies an error handler that is used to collectively handle all
     * {@link TwoWayFieldBinding#withValidationErrorHandler(SerializableConsumer) validation errors} coming from the
     * bindings.
     *
     * @param validationErrorHandler the error handler or {@code null} to use none.
     * @return this field binder, to allow for method chaining.
     */
    @Nonnull
    public FieldBinder withValidationErrorHandler(ValidationErrorHandler validationErrorHandler) {
        this.validationErrorHandler = validationErrorHandler;
        return this;
    }

    private void handleConverterError(@Nonnull TwoWayFieldBinding<?, ?> binding, String errorMessage) {
        if (converterErrorHandler != null) {
            converterErrorHandler.handleConverterError(binding, errorMessage);
        }
    }

    private void handleValidationError(@Nonnull TwoWayFieldBinding<?, ?> binding,
                                       @Nonnull Collection<ValidationResult> errors) {
        if (validationErrorHandler != null) {
            validationErrorHandler.handleValidationError(binding, errors);
        }
    }

    /**
     * Functional interface for {@link FieldBinder#withConverterErrorHandler(ConverterErrorHandler) converter error handlers}.
     */
    @FunctionalInterface
    public interface ConverterErrorHandler extends Serializable {

        /**
         * Handles the specified converter error.
         *
         * @param binding      the binding that caused the error, never {@code null}.
         * @param errorMessage the error message reported by the {@link com.vaadin.flow.data.converter.Converter converter}.
         */
        void handleConverterError(@Nonnull TwoWayFieldBinding<?, ?> binding, String errorMessage);
    }

    /**
     * Functional interface for {@link FieldBinder#withValidationErrorHandler(ValidationErrorHandler) validation error handlers}.
     */
    @FunctionalInterface
    public interface ValidationErrorHandler extends Serializable {

        /**
         * Handles the specified validation error.
         *
         * @param binding the binding that caused the error, never {@code null}.
         * @param errors  the validation errors reported by the {@link com.vaadin.flow.data.binder.Validator validators}.
         */
        void handleValidationError(@Nonnull TwoWayFieldBinding<?, ?> binding,
                                   @Nonnull Collection<ValidationResult> errors);
    }
}
