package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.function.SerializableConsumer;
import net.pkhapps.appmodel4flow.property.Property;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Interface for a field binding that binds a model to a UI field using a two-way binding where updates the field are
 * reflected in the model and vice versa.
 *
 * @param <MODEL>        the value type of the model.
 * @param <PRESENTATION> the value type of the field.
 */
public interface TwoWayFieldBinding<MODEL, PRESENTATION> extends FieldBinding<MODEL, PRESENTATION> {

    @Nonnull
    @Override
    Property<MODEL> getModel();

    /**
     * Configures the binding to handle any conversion errors using the given error handler. A conversion error occurs
     * when the field value cannot be converted to a model value. The {@link #isPresentationValid()} flag will be
     * updated regardless of the presence of an error handler.
     *
     * @param converterErrorHandler the error handler or {@code null} (the default) if none is needed.
     * @return this binding, to allow for method chaining.
     * @see com.vaadin.flow.data.converter.Converter
     */
    @Nonnull
    TwoWayFieldBinding<MODEL, PRESENTATION> withConverterErrorHandler(SerializableConsumer<String> converterErrorHandler);

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
     * Configures the binding to handle any validation errors using the given error handler. A validation error occurs
     * when any of the {@link #withValidator(Validator) validators} don't accept a value that has been successfully
     * converted. The {@link #isModelValid()} flag will be updated regardless of the presence of an error handler.
     *
     * @param validationErrorHandler the error handler or {@code null} (the default) if none is needed.
     * @return this binding, to allow for method chaining.
     * @see Validator
     */
    @Nonnull
    TwoWayFieldBinding<MODEL, PRESENTATION> withValidationErrorHandler(SerializableConsumer<Collection<ValidationResult>> validationErrorHandler);

    /**
     * By default, the binding will write model values to the underlying model even if they don't pass
     * {@link #withValidator(Validator) validation}. This method will disable this, allowing only valid model values
     * to pass through.
     *
     * @return this binding, to allow for method chaining.
     */
    @Nonnull
    TwoWayFieldBinding<MODEL, PRESENTATION> withWriteInvalidModelValuesDisabled();
}
