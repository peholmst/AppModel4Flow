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

package net.pkhapps.appmodel4flow.binding.group;

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
