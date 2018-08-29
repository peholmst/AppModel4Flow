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
