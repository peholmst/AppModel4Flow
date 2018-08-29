package net.pkhapps.appmodel4flow.action;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link ActionWithoutResult}.
 */
public class ActionWithoutResultTest {

    @Test
    public void perform() {
        var actionPerformed = new AtomicBoolean(false);
        var action = new ActionWithoutResult() {

            @Override
            protected void doPerformWithoutResult() {
                actionPerformed.set(true);
            }
        };
        var output = action.perform();
        assertThat(output).isNull();
        assertThat(actionPerformed).isTrue();
    }

    @Test
    public void performWithCommand() {
        var actionPerformed = new AtomicBoolean(false);
        var action = new ActionWithoutResult(() -> actionPerformed.set(true));
        action.perform();
        assertThat(actionPerformed).isTrue();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void performWithoutCommandAndOverriddenMethod() {
        new ActionWithoutResult().perform();
    }
}
