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
        assertThat(collection.containsListeners()).isFalse();
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
