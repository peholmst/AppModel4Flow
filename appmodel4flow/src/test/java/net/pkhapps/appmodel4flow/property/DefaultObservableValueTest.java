package net.pkhapps.appmodel4flow.property;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link DefaultObservableValue}.
 */
public class DefaultObservableValueTest {

    @Test
    public void defaultConstructor() {
        DefaultObservableValue<String> value = new DefaultObservableValue<>();
        assertThat(value.isEmpty()).isTrue();
        assertThat(value.getValue()).isNull();
        assertThat(value.getOptionalValue()).isEmpty();
    }

    @Test
    public void initializingConstructor() {
        DefaultObservableValue<String> value = new DefaultObservableValue<>("hello");
        assertThat(value.hasValue()).isTrue();
        assertThat(value.getValue()).isEqualTo("hello");
        assertThat(value.getOptionalValue()).contains("hello");
    }

    @Test
    public void setValue_differentValues_eventFired() {
        DefaultObservableValue<String> value = new DefaultObservableValue<>();
        AtomicReference<ObservableValue.ValueChangeEvent> event = new AtomicReference<>();
        value.addValueChangeListener(event::set);
        value.setValue("hello");
        assertThat(value.getValue()).isEqualTo("hello");
        assertThat(event.get()).isNotNull();
        assertThat(event.get().getSender()).isSameAs(value);
        assertThat(event.get().getOldValue()).isNull();
        assertThat(event.get().getValue()).isEqualTo("hello");
    }

    @Test
    public void setValue_sameValue_noEventFired() {
        DefaultObservableValue<String> value = new DefaultObservableValue<>("hello");
        AtomicReference<ObservableValue.ValueChangeEvent> event = new AtomicReference<>();
        value.addValueChangeListener(event::set);
        value.setValue("hello");
        assertThat(event.get()).isNull();
    }
}
