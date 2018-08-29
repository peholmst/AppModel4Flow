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
        if (requiredErrorMessageSupplier != null && getField().isEmpty()) {
            // TODO Write test for this and make sure it works properly (don't show any errors until the user has actually changed anything).
            setPresentationValid(false);
            handleConverterResult(Result.error(requiredErrorMessageSupplier.get()));
        } else {
            var result = getConverter().convertToModel(getField().getValue(), createValueContext());
            setPresentationValid(!result.isError());
            result.ifOk(this::writePropertyValue);
            handleConverterResult(result);
        }
    }

    private void handleConverterResult(@Nonnull Result<MODEL> result) {
        if (converterResultHandler != null) {
            converterResultHandler.accept(result);
        }
    }

    private void writePropertyValue(MODEL value) {
        if (validators.size() > 0) {
            var valueContext = createValueContext();
            var validationResults = validators.stream().map(validator -> validator.apply(value, valueContext)).collect(Collectors.toSet());
            var hasErrors = validationResults.stream().anyMatch(ValidationResult::isError);
            setModelValid(!hasErrors);
            handleValidationResults(validationResults);
            if (hasErrors && !writeInvalidModelValuesEnabled) {
                return;
            }
        }
        getModel().setValue(value);
    }

    private void handleValidationResults(Collection<ValidationResult> results) {
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
    public TwoWayFieldBinding<MODEL, PRESENTATION> withConverterResultHandler(SerializableConsumer<Result<MODEL>> converterResultHandler) {
        this.converterResultHandler = converterResultHandler;
        return this;
    }

    @Nonnull
    @Override
    public TwoWayFieldBinding<MODEL, PRESENTATION> withValidationResultHandler(SerializableConsumer<Collection<ValidationResult>> validationResultHandler) {
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
        this.requiredErrorMessageSupplier = Objects.requireNonNull(errorMessageSupplier, "errorMessageSupplier must not be null");
        getField().setRequiredIndicatorVisible(true);
        return this;
    }

    @Override
    public void remove() {
        fieldValueRegistration.remove();
        propertyReadOnlyRegistration.remove();
        super.remove();
    }
}
