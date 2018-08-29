package net.pkhapps.appmodel4flow.incubator.event;

import com.vaadin.flow.function.SerializableConsumer;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link DefaultEventBus}.
 */
@SuppressWarnings("unchecked")
public class DefaultEventBusTest {

    private EventBus eventBus;

    @Before
    public void setUp() {
        eventBus = new DefaultEventBus();
    }

    @Test
    public void publish_listenerOfWrongType_noEventReceived() {
        var listener = new TestListener<String>();
        eventBus.registerListener(String.class, listener);
        eventBus.publish(123);
        assertThat(listener.isInvoked()).isFalse();
    }

    @Test
    public void publish_listenerOfRightType_eventReceived() {
        var listener = new TestListener<String>();
        eventBus.registerListener(String.class, listener);
        eventBus.publish("Hello World");
        assertThat(listener.isInvoked()).isTrue();
    }

    @Test
    public void publish_predicateNotAffirmed_noEventReceived() {
        var listener = new TestListener<String>();
        eventBus.registerListener(String.class, listener, event -> event.endsWith("World"));
        eventBus.publish("Hello");
        assertThat(listener.isInvoked()).isFalse();
    }

    @Test
    public void publish_predicateAffirmed_eventReceived() {
        var listener = new TestListener<String>();
        eventBus.registerListener(String.class, listener, event -> event.endsWith("World"));
        eventBus.publish("Hello World");
        assertThat(listener.isInvoked()).isTrue();
    }

    public static class TestListener<T> implements SerializableConsumer<T> {

        private T event;

        @Override
        public void accept(T t) {
            this.event = t;
        }

        boolean isInvoked() {
            return event != null;
        }
    }
}
