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

package net.pkhapps.appmodel4flow.property;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link ComputedValue}.
 */
public class ComputedValueTest {

    @Test
    public void initialComputedValue() {
        var firstName = new DefaultProperty<>("Joe");
        var lastName = new DefaultProperty<>("Cool");
        var computed = new ComputedValue<>(() -> String.format("%s %s", firstName.getValue(), lastName.getValue()), firstName, lastName);
        assertThat(computed.getValue()).isEqualTo("Joe Cool");
    }

    @Test
    public void computedValueChangesWhenDependencyIsChanged() {
        DefaultProperty<String> firstName = new DefaultProperty<>("Joe");
        DefaultProperty<String> lastName = new DefaultProperty<>("Cool");
        var computed = new ComputedValue<>(() -> String.format("%s %s", firstName.getValue(), lastName.getValue()), firstName, lastName);
        lastName.setValue("Smith");
        assertThat(computed.getValue()).isEqualTo("Joe Smith");
    }
}
