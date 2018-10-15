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
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Helper class for managing listeners and firing events to them. Both strong and weak listener references are supported
 * and the class is thread-safe.
 */
@ThreadSafe
@Slf4j
public class ListenerCollection<EVENT> implements Serializable {

    private final ReentrantReadWriteLock listenerLock = new ReentrantReadWriteLock();
    private Set<SerializableConsumer<EVENT>> listeners;
    private Map<SerializableConsumer<EVENT>, Void> weakListeners;

    /**
     * Fires the given event to all registered listeners.
     *
     * @param event the event to fire, never {@code null}.
     */
    public void fireEvent(@Nonnull EVENT event) {
        Objects.requireNonNull(event, "event must not be null");
        Set<SerializableConsumer<EVENT>> listeners = new HashSet<>();
        listenerLock.readLock().lock();
        try {
            if (this.listeners != null) {
                listeners.addAll(this.listeners);
            }
            if (this.weakListeners != null) {
                listeners.addAll(this.weakListeners.keySet());
            }
        } finally {
            listenerLock.readLock().unlock();
        }
        log.trace("Firing event {} to {} listener(s)", event, listeners.size());
        listeners.forEach(listener -> listener.accept(event));
    }

    /**
     * Registers the given listener to be notified when events are {@link #fireEvent(Object) fired}.
     *
     * @param listener the listener, never {@code null}.
     * @return a registration handle, never {@code null}.
     */
    @Nonnull
    public Registration addListener(@Nonnull SerializableConsumer<EVENT> listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        listenerLock.writeLock().lock();
        try {
            if (listeners == null) {
                listeners = new HashSet<>();
            }
            listeners.add(listener);
        } finally {
            listenerLock.writeLock().unlock();
        }
        return () -> {
            listenerLock.writeLock().lock();
            try {
                listeners.remove(listener);
            } finally {
                listenerLock.writeLock().unlock();
            }
        };
    }

    /**
     * Registers the given listener to be notified when events are {@link #fireEvent(Object) fired}. The listener is
     * registered using a weak reference and will be automatically removed when garbage collected. This means you have
     * to make sure you keep another reference to the listener for as long as you need it or it will become garbage
     * collected too soon.
     *
     * @param listener the listener, never {@code null}.
     */
    public void addWeakListener(@Nonnull SerializableConsumer<EVENT> listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        listenerLock.writeLock().lock();
        try {
            if (weakListeners == null) {
                weakListeners = new WeakHashMap<>();
            }
            weakListeners.put(listener, null);
        } finally {
            listenerLock.writeLock().unlock();
        }
    }

    /**
     * Returns whether the listener collection currently contains any listeners.
     *
     * @return true if there is at least one listener registered, false if there are none.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean containsListeners() {
        listenerLock.readLock().lock();
        try {
            if (listeners != null && listeners.size() > 0) {
                return true;
            }
            return weakListeners != null && weakListeners.size() > 0;
        } finally {
            listenerLock.readLock().unlock();
        }
    }
}
