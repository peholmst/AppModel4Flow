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

package net.pkhapps.appmodel4flow.action.support;

import net.pkhapps.appmodel4flow.action.TestAction;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link CompositeAction}.
 */
public class CompositeActionTest {

    @Test
    public void isPerformable_allActionsArePerformable_compositeActionIsPerformable() {
        var action1 = new TestAction();
        var action2 = new TestAction();
        var composite = new CompositeAction(action1, action2);
        assertThat(composite.isPerformable().getValue()).isTrue();
    }

    @Test
    public void isPerformable_oneActionIsNotPerformable_compositeActionIsNotPerformable() {
        var action1 = new TestAction();
        var action2 = new TestAction();
        var action3 = new TestAction();
        var composite = new CompositeAction(action1, action2, action3);

        action3.setPerformable(false);
        assertThat(composite.isPerformable().getValue()).isFalse();
    }

    @Test
    public void perform_allActionsArePerformed() {
        var action1 = new TestAction();
        var action2 = new TestAction();
        var composite = new CompositeAction(action1, action2);
        composite.perform();
        assertThat(action1.getPerformCount()).isEqualTo(1);
        assertThat(action2.getPerformCount()).isEqualTo(1);
    }
}
