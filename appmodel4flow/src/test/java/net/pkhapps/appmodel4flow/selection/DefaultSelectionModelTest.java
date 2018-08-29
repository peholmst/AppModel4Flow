package net.pkhapps.appmodel4flow.selection;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link DefaultSelectionModel}.
 */
public class DefaultSelectionModelTest {

    @Test
    public void selectOne() {
        var model = new DefaultSelectionModel<String>();
        model.selectOne("hello");
        assertThat(model.getSelection()).containsExactly("hello");
    }

    @Test
    public void selectMultiple() {
        var model = new DefaultSelectionModel<String>();
        model.select(Arrays.asList("hello", "world"));
        assertThat(model.getSelection()).containsExactly("hello", "world");
    }

    @Test
    public void clear() {
        var model = new DefaultSelectionModel<String>();
        model.select(Arrays.asList("hello", "world"));
        model.clear();
        assertThat(model.getSelection()).isEmpty();
    }

    @Test
    public void selectionChangeListener() {
        var model = new DefaultSelectionModel<String>();
        var listenerFired = new AtomicBoolean(false);
        var registration = model.addValueChangeListener(event -> {
            assertThat(event.getSender()).isSameAs(model);
            assertThat(event.getOldValue()).isEmpty();
            assertThat(event.getValue()).containsExactly("hello");
            listenerFired.set(true);
        });
        model.selectOne("hello");
        assertThat(listenerFired).isTrue();

        listenerFired.set(false);
        registration.remove();
        model.clear();
        assertThat(listenerFired).isFalse();
    }
}
