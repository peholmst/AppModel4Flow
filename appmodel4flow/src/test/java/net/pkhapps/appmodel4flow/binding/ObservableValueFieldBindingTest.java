package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.component.textfield.TextField;
import net.pkhapps.appmodel4flow.property.DefaultObservableValue;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link ObservableValueFieldBinding}.
 */
public class ObservableValueFieldBindingTest {

    private TextField field;
    private DefaultObservableValue<String> model;
    private ObservableValueFieldBinding<String, String> binding;

    @Before
    public void setUp() {
        field = new TextField();
        model = new DefaultObservableValue<>();
        binding = new ObservableValueFieldBinding<>(model, field, new ObservableValueFieldBinding.PassThroughConverter<>());
    }

    @Test
    public void initialStateAfterCreation() {
        assertThat(field.getValue()).isEmpty();
        assertThat(field.isReadOnly()).isTrue();
        assertThat(model.getValue()).isNull();
        assertThat(binding.getModel()).isSameAs(model);
        assertThat(binding.getField()).isSameAs(field);
        assertThat(binding.isModelValid().getValue()).isTrue();
        assertThat(binding.isPresentationValid().getValue()).isTrue();
    }

    @Test
    public void setFieldValue_nothingHappensToModel() {
        field.setValue("hello");
        assertThat(model.getValue()).isNull();
    }

    @Test
    public void setModelValue_fieldIsUpdated() {
        model.setValue("hello");
        assertThat(field.getValue()).isEqualTo("hello");
    }

    @Test
    public void remove_fieldIsNoLongerUpdated() {
        binding.remove();
        model.setValue("hello");
        assertThat(field.getValue()).isEmpty();
    }
}
