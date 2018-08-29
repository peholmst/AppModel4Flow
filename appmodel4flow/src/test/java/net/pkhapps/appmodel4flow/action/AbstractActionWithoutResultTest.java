package net.pkhapps.appmodel4flow.action;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link AbstractActionWithoutResult}.
 */
public class AbstractActionWithoutResultTest {

    @Test
    public void perform() {
        var actionPerformed = new AtomicBoolean(false);
        var action = new AbstractActionWithoutResult() {

            @Override
            protected void doPerformWithoutResult() {
                actionPerformed.set(true);
            }
        };
        var output = action.perform();
        assertThat(output).isNull();
        assertThat(actionPerformed).isTrue();
    }
}
