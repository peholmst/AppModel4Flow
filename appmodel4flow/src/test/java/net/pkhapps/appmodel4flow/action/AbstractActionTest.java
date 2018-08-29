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
