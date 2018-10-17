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

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link DefaultObservableValue}.
 */
public class DefaultObservableValueTest {

    @Test
    public void defaultConstructor() {
        DefaultObservableValue<String> value = new DefaultObservableValue<>();
        assertThat(value.isEmpty()).isTrue();
        assertThat(value.getValue()).isNull();
    }

    @Test
    public void initializingConstructor() {
        DefaultObservableValue<String> value = new DefaultObservableValue<>("hello");
        assertThat(value.hasValue()).isTrue();
        assertThat(value.getValue()).isEqualTo("hello");
    }

    @Test
    public void setValue_differentValues_eventFired() {
        DefaultObservableValue<String> value = new DefaultObservableValue<>();
        AtomicReference<ObservableValue.ValueChangeEvent> event = new AtomicReference<>();
        value.addValueChangeListener(event::set);
        value.setValue("hello");
        assertThat(value.getValue()).isEqualTo("hello");
        assertThat(event.get()).isNotNull();
        assertThat(event.get().getSender()).isSameAs(value);
        assertThat(event.get().getOldValue()).isNull();
        assertThat(event.get().getValue()).isEqualTo("hello");
    }

    @Test
    public void setValue_sameValue_noEventFired() {
        DefaultObservableValue<String> value = new DefaultObservableValue<>("hello");
        AtomicReference<ObservableValue.ValueChangeEvent> event = new AtomicReference<>();
        value.addValueChangeListener(event::set);
        value.setValue("hello");
        assertThat(event.get()).isNull();
    }

    @Test
    public void map_changesToOriginalAreObservedInMappedValue() {
        DefaultObservableValue<Integer> value = new DefaultObservableValue<>(123);
        ObservableValue<String> mappedValue = value.map(String::valueOf);
        AtomicReference<ObservableValue.ValueChangeEvent> event = new AtomicReference<>();
        mappedValue.addValueChangeListener(event::set);
        value.setValue(456);
        assertThat(mappedValue.getValue()).isEqualTo("456");
        assertThat(event.get().getOldValue()).isEqualTo("123");
        assertThat(event.get().getValue()).isEqualTo("456");
    }

    @Test
    public void withEmptyCheck() {
        DefaultObservableValue<String> value = new DefaultObservableValue<String>().withEmptyCheck(String::isEmpty);
        assertThat(value.isEmpty()).isTrue();
        assertThat(value.getValue()).isNull();
        value.setValue("");
        assertThat(value.isEmpty()).isTrue();
        assertThat(value.getValue()).isEmpty();
    }
}
