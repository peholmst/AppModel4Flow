package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import net.pkhapps.appmodel4flow.property.DefaultProperty;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link FieldBindingGroup}.
 */
public class FieldBindingGroupTest {

    private TextField stringField;
    private TextField integerField;
    private Checkbox booleanField;
    private DefaultProperty<String> stringProperty;
    private DefaultProperty<Integer> integerProperty;
    private DefaultProperty<Boolean> booleanProperty;
    private FieldBindingGroup group;

    @Before
    public void setUp() {
        stringField = new TextField();
        integerField = new TextField();
        booleanField = new Checkbox();

        stringProperty = new DefaultProperty<>();
        integerProperty = new DefaultProperty<>();
        booleanProperty = new DefaultProperty<>(false);

        group = new FieldBindingGroup();
        group.withBinding(new PropertyFieldBinding<>(stringProperty, stringField, new ObservableValueFieldBinding.PassThroughConverter<>())
                .withValidator(new StringLengthValidator("lengthError", 3, 10)));
        group.withBinding(new PropertyFieldBinding<>(integerProperty, integerField, new StringToIntegerConverter("intConversionError")));
        group.withBinding(new PropertyFieldBinding<>(booleanProperty, booleanField, new ObservableValueFieldBinding.PassThroughConverter<>()));
    }

    @Test
    public void dirtyFlag() {
        assertThat(group.isDirty().getValue()).isFalse();
        booleanField.setValue(true);
        assertThat(group.isDirty().getValue()).isTrue();
        booleanProperty.discard();
        assertThat(group.isDirty().getValue()).isFalse();
    }

    @Test
    public void presentationValidFlag() {
        assertThat(group.isPresentationValid().getValue()).isTrue();
        integerField.setValue("this is not a number");
        assertThat(group.isPresentationValid().getValue()).isFalse();
        integerField.setValue("50");
        assertThat(group.isPresentationValid().getValue()).isTrue();
    }

    @Test
    public void modelValidFlag() {
        assertThat(group.isModelValid().getValue()).isTrue();
        stringField.setValue("this string is too long");
        assertThat(group.isModelValid().getValue()).isFalse();
        stringField.setValue("this is OK");
        assertThat(group.isModelValid().getValue()).isTrue();
    }

    @Test
    public void converterResultHandler_error() {
        var handlerInvoked = new AtomicBoolean(false);
        group.withConverterResultHandler((binding, result) -> {
            assertThat(binding.getModel()).isSameAs(integerProperty);
            assertThat(result.isError()).isTrue();
            handlerInvoked.set(true);
        });
        integerField.setValue("this is not a number");
        assertThat(handlerInvoked).isTrue();
    }

    @Test
    public void converterResultHandler_success() {
        var handlerInvoked = new AtomicBoolean(false);
        group.withConverterResultHandler((binding, result) -> {
            assertThat(binding.getModel()).isSameAs(integerProperty);
            assertThat(result.isError()).isFalse();
            handlerInvoked.set(true);
        });
        integerField.setValue("123");
        assertThat(handlerInvoked).isTrue();
    }

    @Test
    public void validationResultHandler_error() {
        var handlerInvoked = new AtomicBoolean(false);
        group.withValidationResultHandler((binding, results) -> {
            assertThat(binding.getModel()).isSameAs(stringProperty);
            assertThat(results).anyMatch(ValidationResult::isError);
            handlerInvoked.set(true);
        });
        stringField.setValue("ts");
        assertThat(handlerInvoked).isTrue();
    }

    @Test
    public void validationResultHandler_success() {
        var handlerInvoked = new AtomicBoolean(false);
        group.withValidationResultHandler((binding, results) -> {
            assertThat(binding.getModel()).isSameAs(stringProperty);
            assertThat(results).noneMatch(ValidationResult::isError);
            handlerInvoked.set(true);
        });
        stringField.setValue("proper");
        assertThat(handlerInvoked).isTrue();
    }
}