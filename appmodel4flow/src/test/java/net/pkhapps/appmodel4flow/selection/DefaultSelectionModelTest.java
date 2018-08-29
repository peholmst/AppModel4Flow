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

package net.pkhapps.appmodel4flow.selection;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link DefaultSelectionModel}.
 */
public class DefaultSelectionModelTest {

    @Test
    public void selectOne() {
        var model = new DefaultSelectionModel<String>();
        model.selectOne("hello");
        assertThat(model.getSelection()).containsExactly("hello");
    }

    @Test
    public void selectMultiple() {
        var model = new DefaultSelectionModel<String>();
        model.select(Arrays.asList("hello", "world"));
        assertThat(model.getSelection()).containsExactly("hello", "world");
    }

    @Test
    public void clear() {
        var model = new DefaultSelectionModel<String>();
        model.select(Arrays.asList("hello", "world"));
        model.clear();
        assertThat(model.getSelection()).isEmpty();
    }

    @Test
    public void selectionChangeListener() {
        var model = new DefaultSelectionModel<String>();
        var listenerFired = new AtomicBoolean(false);
        var registration = model.addValueChangeListener(event -> {
            assertThat(event.getSender()).isSameAs(model);
            assertThat(event.getOldValue()).isEmpty();
            assertThat(event.getValue()).containsExactly("hello");
            listenerFired.set(true);
        });
        model.selectOne("hello");
        assertThat(listenerFired).isTrue();

        listenerFired.set(false);
        registration.remove();
        model.clear();
        assertThat(listenerFired).isFalse();
    }
}
