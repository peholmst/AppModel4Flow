package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import net.pkhapps.appmodel4flow.property.DefaultProperty;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link FieldBinder}.
 */
public class FieldBinderTest {

    private TextField stringField;
    private TextField integerField;
    private Checkbox booleanField;
    private DefaultProperty<String> stringProperty;
    private DefaultProperty<Integer> integerProperty;
    private DefaultProperty<Boolean> booleanProperty;
    private FieldBinder binder;

    @Before
    public void setUp() {
        stringField = new TextField();
        integerField = new TextField();
        booleanField = new Checkbox();

        stringProperty = new DefaultProperty<>();
        integerProperty = new DefaultProperty<>();
        booleanProperty = new DefaultProperty<>(false);

        binder = new FieldBinder();
        binder.withBinding(new PropertyFieldBinding<>(stringProperty, stringField, new ObservableValueFieldBinding.PassThroughConverter<>())
                .withValidator(new StringLengthValidator("lengthError", 3, 10)));
        binder.withBinding(new PropertyFieldBinding<>(integerProperty, integerField, new StringToIntegerConverter("intConversionError")));
        binder.withBinding(new PropertyFieldBinding<>(booleanProperty, booleanField, new ObservableValueFieldBinding.PassThroughConverter<>()));
    }

    @Test
    public void dirtyFlag() {
        assertThat(binder.isDirty().getValue()).isFalse();
        booleanField.setValue(true);
        assertThat(binder.isDirty().getValue()).isTrue();
        booleanProperty.discard();
        assertThat(binder.isDirty().getValue()).isFalse();
    }

    @Test
    public void presentationValidFlag() {
        assertThat(binder.isPresentationValid().getValue()).isTrue();
        integerField.setValue("this is not a number");
        assertThat(binder.isPresentationValid().getValue()).isFalse();
        integerField.setValue("50");
        assertThat(binder.isPresentationValid().getValue()).isTrue();
    }

    @Test
    public void modelValidFlag() {
        assertThat(binder.isModelValid().getValue()).isTrue();
        stringField.setValue("this string is too long");
        assertThat(binder.isModelValid().getValue()).isFalse();
        stringField.setValue("this is OK");
        assertThat(binder.isModelValid().getValue()).isTrue();
    }

    @Test
    public void converterErrorHandler() {
        var handlerInvoked = new AtomicBoolean(false);
        binder.withConverterErrorHandler((binding, errorMessage) -> {
            assertThat(binding.getModel()).isSameAs(integerProperty);
            assertThat(errorMessage).isEqualTo("intConversionError");
            handlerInvoked.set(true);
        });
        integerField.setValue("this is not a number");
        assertThat(handlerInvoked).isTrue();
    }

    @Test
    public void validationErrorHandler() {
        var handlerInvoked = new AtomicBoolean(false);
        binder.withValidationErrorHandler((binding, errors) -> {
            assertThat(binding.getModel()).isSameAs(stringProperty);
            assertThat(errors).anyMatch(result -> result.getErrorMessage().equals("lengthError"));
            handlerInvoked.set(true);
        });
        stringField.setValue("ts");
        assertThat(handlerInvoked).isTrue();
    }
}