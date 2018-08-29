/*
 * Copyright (c) 2018 the original authors (see project POM file)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import net.pkhapps.appmodel4flow.action.TestAction;
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
    public void bindingIsRemoved_actionNoLongerPerformedWhenButtonIsClicked() {
        binding.remove();
        button.simulateClick();
        assertThat(action.getPerformCount()).isEqualTo(0);
    }

    public static class TestButton extends Button {
        void simulateClick() {
            fireEvent(new ClickEvent<>(this));
        }
    }
}
