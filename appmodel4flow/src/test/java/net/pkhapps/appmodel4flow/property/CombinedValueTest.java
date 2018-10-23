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

import net.pkhapps.appmodel4flow.property.support.Combiners;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link CombinedValue}.
 */
@SuppressWarnings("Convert2Diamond") // IntelliJ does not work properly with the 'var' keyword and the diamond operator.
public class CombinedValueTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithNoDependencies() {
        new CombinedValue<Boolean>(Combiners.allTrue(), Collections.emptyList());
    }

    @Test
    public void initialCombinedValue() {
        var value1 = new DefaultObservableValue<String>("Hello");
        var value2 = new DefaultObservableValue<String>();
        var combined = new CombinedValue<>(Combiners.joinStrings(","), value1, value2);
        assertThat(combined.getValue()).isEqualTo("Hello");
    }

    @Test
    public void combinedValueChangesWhenDependencyIsChanged() {
        var value1 = new DefaultObservableValue<String>("Hello");
        var value2 = new DefaultObservableValue<String>();
        var combined = new CombinedValue<String>(Combiners.joinStrings(","), value1, value2);
        AtomicReference<ObservableValue.ValueChangeEvent<String>> event = new AtomicReference<>();
        combined.addValueChangeListener(event::set);

        value2.setValue("World");

        assertThat(combined.getValue()).isEqualTo("Hello,World");
        assertThat(event.get().getOldValue()).isEqualTo("Hello");
        assertThat(event.get().getValue()).isEqualTo("Hello,World");
    }
}
