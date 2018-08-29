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
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.shared.Registration;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Interface defining a simple event bus.
 */
@SuppressWarnings("UnusedReturnValue")
public interface EventBus extends Serializable {

    /**
     * Registers a listener for the specified event type.
     *
     * @param eventType the type of events the listener is interested in, never {@code null}.
     * @param listener  the listener to receive the event, never {@code null}.
     * @param predicate an optional predicate that must be affirmed for the listener to receive the event, may be
     *                  {@code null}.
     * @param <T>       the type of the event.
     * @return a registration handler, never {@code null}.
     */
    <T> Registration registerListener(@Nonnull Class<T> eventType, @Nonnull SerializableConsumer<? super T> listener,
                                      SerializablePredicate<T> predicate);

    /**
     * Registers a listener for the specified event type.
     *
     * @param eventType the type of events the listener is interested in, never {@code null}.
     * @param listener  the listener to receive the event, never {@code null}.
     * @param <T>       the type of the event.
     * @return a registration handler, never {@code null}.
     */
    default <T> Registration registerListener(@Nonnull Class<T> eventType,
                                              @Nonnull SerializableConsumer<? super T> listener) {
        return registerListener(eventType, listener, null);
    }

    /**
     * Publishes the specified event on the event bus.
     *
     * @param event the event to publish, never {@code null}.
     */
    void publish(@Nonnull Object event);
}
