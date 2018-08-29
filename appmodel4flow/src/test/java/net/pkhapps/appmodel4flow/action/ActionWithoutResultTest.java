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
