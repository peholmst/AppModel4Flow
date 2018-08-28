package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.function.SerializableConsumer;
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
    private SerializableConsumer<String> converterErrorHandler;
    private SerializableConsumer<Collection<ValidationResult>> validationErrorHandler;
    private boolean writeInvalidModelValuesEnabled = true;
    private final List<Validator<MODEL>> validators = new ArrayList<>();

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
        var result = getConverter().convertToModel(getField().getValue(), createValueContext());
        setPresentationValid(!result.isError());
        result.handle(this::writePropertyValue, this::handleConverterError);
    }

    private void handleConverterError(String error) {
        if (converterErrorHandler != null) {
            converterErrorHandler.accept(error);
        }
    }

    private void writePropertyValue(MODEL value) {
        if (validators.size() > 0) {
            var valueContext = createValueContext();
            var errors = validators.stream().map(validator -> validator.apply(value, valueContext))
                    .filter(ValidationResult::isError).collect(Collectors.toSet());
            setModelValid(errors.isEmpty());
            if (errors.size() > 0) {
                handleValidationErrors(errors);
                if (!writeInvalidModelValuesEnabled) {
                    return;
                }
            }
        }
        getModel().setValue(value);
    }

    private void handleValidationErrors(Collection<ValidationResult> errors) {
        if (validationErrorHandler != null) {
            validationErrorHandler.accept(errors);
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
    public TwoWayFieldBinding<MODEL, PRESENTATION> withConverterErrorHandler(SerializableConsumer<String> converterErrorHandler) {
        this.converterErrorHandler = converterErrorHandler;
        return this;
    }

    @Nonnull
    @Override
    public TwoWayFieldBinding<MODEL, PRESENTATION> withValidationErrorHandler(SerializableConsumer<Collection<ValidationResult>> validationErrorHandler) {
        this.validationErrorHandler = validationErrorHandler;
        return this;
    }

    @Nonnull
    @Override
    public TwoWayFieldBinding<MODEL, PRESENTATION> withWriteInvalidModelValuesDisabled() {
        writeInvalidModelValuesEnabled = false;
        return this;
    }

    @Override
    public void remove() {
        fieldValueRegistration.remove();
        propertyReadOnlyRegistration.remove();
        super.remove();
    }
}
