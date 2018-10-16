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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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

    @Test
    public void serializeAndDeserializeWithWeakListeners() throws Exception {
        var testObject = new SerializationTestObject();

        testObject.fireEvent("before");
        assertThat(testObject.receivedEvents).containsExactlyInAnyOrder("weak:before", "strong:before");

        var bos = new ByteArrayOutputStream();
        var oos = new ObjectOutputStream(bos);
        oos.writeObject(testObject);

        var bis = new ByteArrayInputStream(bos.toByteArray());
        var ois = new ObjectInputStream(bis);
        var testObjectAfterSerialization = (SerializationTestObject) ois.readObject();

        testObjectAfterSerialization.fireEvent("after");
        assertThat(testObjectAfterSerialization.receivedEvents).containsExactlyInAnyOrder("weak:before",
                "strong:before", "weak:after", "strong:after");

        testObjectAfterSerialization.unregister();
        testObjectAfterSerialization.fireEvent("after unregistration");

        assertThat(testObjectAfterSerialization.receivedEvents).contains("weak:after unregistration");
        assertThat(testObjectAfterSerialization.receivedEvents).doesNotContain("strong:after unregistration");
    }

    @Test
    public void serializeAndDeserializeWithoutListeners() throws Exception {
        // This is just checking that the serialization and deserializing works properly when there are no weak
        // listeners.
        var bos = new ByteArrayOutputStream();
        var oos = new ObjectOutputStream(bos);
        oos.writeObject(new ListenerCollection<>());

        var bis = new ByteArrayInputStream(bos.toByteArray());
        var ois = new ObjectInputStream(bis);
        assertThat(ois.readObject()).isInstanceOf(ListenerCollection.class);

    }

    private static class SerializationTestObject implements Serializable {

        final List<String> receivedEvents = new ArrayList<>();

        private final SerializableConsumer<String> weakListener = this::weakListener;
        private final ListenerCollection<String> listenerCollection = new ListenerCollection<>();
        private final Registration listenerRegistration;

        SerializationTestObject() {
            listenerCollection.addWeakListener(weakListener);
            listenerRegistration = listenerCollection.addListener(this::listener);
        }

        private void weakListener(String s) {
            receivedEvents.add("weak:" + s);
        }

        private void listener(String s) {
            receivedEvents.add("strong:" + s);
        }

        void unregister() {
            listenerRegistration.remove();
        }

        void fireEvent(String event) {
            listenerCollection.fireEvent(event);
        }
    }
}
