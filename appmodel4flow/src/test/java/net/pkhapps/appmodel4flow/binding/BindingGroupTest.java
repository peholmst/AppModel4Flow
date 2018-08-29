package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.shared.Registration;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit test for {@link BindingGroup}.
 */
public class BindingGroupTest {

    @Test
    public void withBinding_bindingAddedToStream() {
        var binder = new BindingGroup();
        var binding = mock(Registration.class);
        binder.withBinding(binding);
        assertThat(binder.getBindings()).contains(binding);
    }

    @Test
    public void dispose_removeIsCalledAndBindingsAreCleared() {
        var binder = new BindingGroup();
        var binding = mock(Registration.class);
        binder.withBinding(binding);
        binder.dispose();
        verify(binding).remove();
        assertThat(binder.getBindings()).isEmpty();
    }
}
