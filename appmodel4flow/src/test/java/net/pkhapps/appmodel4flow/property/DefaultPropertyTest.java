package net.pkhapps.appmodel4flow.property;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link DefaultProperty}.
 */
public class DefaultPropertyTest {

    @Test
    public void dirtyFlag_falseByDefault() {
        DefaultProperty<String> property = new DefaultProperty<>();
        assertThat(property.isDirty().getValue()).isFalse();
    }

    @Test
    public void dirtyFlag_trueAfterSettingValue() {
        DefaultProperty<String> property = new DefaultProperty<>();
        property.setValue("hello");
        assertThat(property.isDirty().getValue()).isTrue();
    }

    @Test
    public void dirtyFlag_falseAfterResetting() {
        DefaultProperty<String> property = new DefaultProperty<>();
        property.setValue("hello");
        property.resetDirtyFlag();
        assertThat(property.isDirty().getValue()).isFalse();
    }

    @Test
    public void dirtyFlag_falseAfterSettingValueBackToOriginal() {
        DefaultProperty<String> property = new DefaultProperty<>("hello");
        property.setValue("world");
        assertThat(property.isDirty().getValue()).isTrue();
        property.setValue("hello");
        assertThat(property.isDirty().getValue()).isFalse();
    }

    @Test(expected = Property.ReadOnlyException.class)
    public void readOnlyFlag_settingValueThrowsException() {
        DefaultProperty<String> property = new DefaultProperty<>();
        property.setReadOnly(true);
        assertThat(property.isReadOnly().getValue()).isTrue();
        property.setValue("hello");
    }
}
