package net.pkhapps.appmodel4flow.property;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link ComputedValue}.
 */
public class ComputedValueTest {

    @Test
    public void initialComputedValue() {
        var firstName = new DefaultProperty<>("Joe");
        var lastName = new DefaultProperty<>("Cool");
        var computed = new ComputedValue<>(() -> String.format("%s %s", firstName.getValue(), lastName.getValue()), firstName, lastName);
        assertThat(computed.getValue()).isEqualTo("Joe Cool");
    }

    @Test
    public void computedValueChangesWhenDependencyIsChanged() {
        DefaultProperty<String> firstName = new DefaultProperty<>("Joe");
        DefaultProperty<String> lastName = new DefaultProperty<>("Cool");
        var computed = new ComputedValue<>(() -> String.format("%s %s", firstName.getValue(), lastName.getValue()), firstName, lastName);
        lastName.setValue("Smith");
        assertThat(computed.getValue()).isEqualTo("Joe Smith");
    }
}
