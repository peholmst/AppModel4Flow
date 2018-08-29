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

package net.pkhapps.appmodel4flow.util;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link ListenerCollection}.
 */
public class ListenerCollectionTest {

    @SuppressWarnings("UnusedAssignment")
    @Test
    public void addWeakListener_listenerRemovedAfterGC() {
        ListenerCollection<String> collection = new ListenerCollection<>();
        AtomicReference<String> receivedEvent = new AtomicReference<>();
        SerializableConsumer<String> listener = receivedEvent::set;

        collection.addWeakListener(listener);
        assertThat(collection.containsListeners()).isTrue();

        collection.fireEvent("hello");
        assertThat(receivedEvent.get()).isEqualTo("hello");

        listener = null;
        System.gc();

        collection.fireEvent("world");
        assertThat(receivedEvent.get()).isEqualTo("hello");
    }

    @Test
    public void addListener_listenerRemovableFromRegistrationHandle() {
        ListenerCollection<String> collection = new ListenerCollection<>();
        AtomicReference<String> receivedEvent = new AtomicReference<>();
        SerializableConsumer<String> listener = receivedEvent::set;

        Registration registration = collection.addListener(listener);
        assertThat(collection.containsListeners()).isTrue();

        collection.fireEvent("hello");
        assertThat(receivedEvent.get()).isEqualTo("hello");

        registration.remove();

        collection.fireEvent("world");
        assertThat(receivedEvent.get()).isEqualTo("hello");
        assertThat(collection.containsListeners()).isFalse();
    }

}
