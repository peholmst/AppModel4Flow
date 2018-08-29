package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import net.pkhapps.appmodel4flow.property.DefaultProperty;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link PropertyFieldBinding}.
 */
public class PropertyFieldBindingTest {

    private TextField field;
    private DefaultProperty<Integer> model;
    private PropertyFieldBinding<Integer, String> binding;

    @Before
    public void setUp() {
        field = new TextField();
        model = new DefaultProperty<>();
        binding = new PropertyFieldBinding<>(model, field, new StringToIntegerConverter("converterError"));
    }

    @Test
    public void initialStateAfterCreation() {
        assertThat(field.getValue()).isEmpty();
        assertThat(field.isReadOnly()).isFalse();
        assertThat(binding.isModelValid().getValue()).isTrue();
        assertThat(binding.isPresentationValid().getValue()).isTrue();
    }

    @Test
    public void setReadOnly_fieldIsUpdated() {
        model.setReadOnly(true);
        assertThat(field.isReadOnly()).isTrue();
    }

    @Test
    public void setFieldValue_valueIsValid_modelIsUpdated() {
        field.setValue("123");
        assertThat(model.getValue()).isEqualTo(123);
        assertThat(binding.isPresentationValid().getValue()).isTrue();
    }

    @Test
    public void setFieldValue_valueIsEmpty_modelIsUpdated() {
        model.setValue(123);
        field.setValue("");
        assertThat(model.isEmpty()).isTrue();
    }

    @Test
    public void setFieldValue_valueIsValid_handlerIsInvoked() {
        var resultHandler = new AtomicReference<Result<Integer>>();
        binding.withConverterResultHandler(resultHandler::set);
        field.setValue("123");
        assertThat(resultHandler.get().isError()).isFalse();
    }

    @Test
    public void setFieldValue_valueIsInvalid_handlerIsInvoked() {
        var resultHandler = new AtomicReference<Result<Integer>>();
        binding.withConverterResultHandler(resultHandler::set);
        field.setValue("this is not a number");
        assertThat(resultHandler.get().isError()).isTrue();
    }

    @Test
    public void setFieldValue_valueIsInvalid_modelIsNotUpdated() {
        field.setValue("this is not a number");
        assertThat(model.isEmpty()).isTrue();
        assertThat(binding.isPresentationValid().getValue()).isFalse();
    }

    @Test
    public void setFieldValue_validatorPresentAndPasses_modelIsUpdated() {
        binding.withValidator(new IntegerRangeValidator("intError", 0, 100));
        field.setValue("50");
        assertThat(model.getValue()).isEqualTo(50);
        assertThat(binding.isModelValid().getValue()).isTrue();
    }

    @Test
    public void setFieldValue_validatorPresentAndPasses_handlerIsInvoked() {
        var resultHandler = new AtomicReference<Collection<ValidationResult>>();
        binding.withValidationResultHandler(resultHandler::set);
        binding.withValidator(new IntegerRangeValidator("intError", 0, 100));
        field.setValue("50");
        assertThat(resultHandler.get()).noneMatch(ValidationResult::isError);
    }

    @Test
    public void setFieldValue_validatorPresentAndBlocks_modelIsStillUpdated() {
        binding.withValidator(new IntegerRangeValidator("intError", 0, 100));
        field.setValue("110");
        assertThat(model.getValue()).isEqualTo(110);
        assertThat(binding.isModelValid().getValue()).isFalse();
    }

    @Test
    public void setFieldValue_validatorPresentAndBlocks_handlerIsInvoked() {
        var resultHandler = new AtomicReference<Collection<ValidationResult>>();
        binding.withValidationResultHandler(resultHandler::set);
        binding.withValidator(new IntegerRangeValidator("intError", 0, 100));
        field.setValue("110");
        assertThat(resultHandler.get()).allMatch(ValidationResult::isError);
    }

    @Test
    public void setFieldValue_validatorPresentAndBlocksAndWriteThroughDisabled_modelIsNotUpdated() {
        binding.withValidator(new IntegerRangeValidator("intError", 0, 100));
        binding.withWriteInvalidModelValuesDisabled();
        field.setValue("110");
        assertThat(model.isEmpty()).isTrue();
        assertThat(binding.isModelValid().getValue()).isFalse();
    }
}
