package net.pkhapps.appmodel4flow.action;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit test for {@link AbstractAction}.
 */
public class AbstractActionTest {

    @Test
    public void perform_noListener_outputReturned() {
        var action = new TestAction();
        var output = action.perform();
        assertThat(output).isEqualTo(action.getPerformCount());
    }

    @Test
    public void perform_listener_eventFired() {
        var action = new TestAction();
        var listenerNotified = new AtomicBoolean(false);
        action.addPerformListener(event -> {
            assertThat(event.getOutput()).isEqualTo(action.getPerformCount());
            assertThat(event.getAction()).isSameAs(action);
            listenerNotified.set(true);
        });
        action.perform();
        assertThat(listenerNotified).isTrue();
    }

    @Test(expected = IllegalStateException.class)
    public void perform_notPerformable_exceptionThrown() {
        var action = new TestAction();
        action.setPerformable(false);
        action.perform();
    }
}
