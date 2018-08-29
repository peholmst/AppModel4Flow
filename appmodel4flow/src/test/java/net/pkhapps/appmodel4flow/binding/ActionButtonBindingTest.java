package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import net.pkhapps.appmodel4flow.action.AbstractActionWithoutResult;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link ActionButtonBinding}.
 */
public class ActionButtonBindingTest {

    private TestAction action;
    private TestButton button;
    private ActionButtonBinding binding;

    @Before
    public void setUp() {
        action = new TestAction();
        button = new TestButton();
        binding = new ActionButtonBinding(action, button);
    }

    @Test
    public void performableFlagChanges_buttonEnabledStateFollows() {
        action.setPerformable(false);
        assertThat(button.isEnabled()).isFalse();
        action.setPerformable(true);
        assertThat(button.isEnabled()).isTrue();
    }

    @Test
    public void buttonIsClicked_actionIsPerformed() {
        button.simulateClick();
        assertThat(action.getPerformCount()).isEqualTo(1);
    }

    @Test
    public void bindingIsRemoved_buttonEnabledStateDoesNotChangeAnyMore() {
        binding.remove();
        action.setPerformable(false);
        assertThat(button.isEnabled()).isTrue();
    }

    @Test
    public void bindingIsRemoved_actionNoLongerPerfomedWhenButtonIsClicked() {
        binding.remove();
        button.simulateClick();
        assertThat(action.getPerformCount()).isEqualTo(0);
    }

    public static class TestAction extends AbstractActionWithoutResult {

        private boolean performable = true;
        private int performCount = 0;

        @Override
        protected void doPerformWithoutResult() {
            performCount++;
        }

        @Override
        public boolean isPerformable() {
            return performable;
        }

        void setPerformable(boolean performable) {
            this.performable = performable;
            fireStateChangeEvent();
        }

        int getPerformCount() {
            return performCount;
        }
    }

    public static class TestButton extends Button {
        void simulateClick() {
            fireEvent(new ClickEvent<>(this));
        }
    }
}
