package net.pkhapps.appmodel4flow.event;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.shared.Registration;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link EventBus}. This implementation is thread-safe, but only using standard
 * synchronization, meaning it may perform badly when there are multiple threads interacting with it frequently.
 */
@ThreadSafe
public class DefaultEventBus implements EventBus {

    private final Set<ListenerEntry<?>> listeners = new HashSet<>();

    @Override
    public <T> Registration registerListener(@Nonnull Class<T> eventType,
                                             @Nonnull SerializableConsumer<? super T> listener,
                                             SerializablePredicate<T> predicate) {
        var entry = new ListenerEntry<>(eventType, listener, predicate);
        synchronized (listeners) {
            listeners.add(entry);
        }
        return () -> {
            synchronized (listeners) {
                listeners.remove(entry);
            }
        };
    }

    @Override
    public void publish(@Nonnull Object event) {
        Set<ListenerEntry<?>> affectedListeners;
        synchronized (listeners) {
            affectedListeners = listeners.stream()
                    .filter(entry -> entry.isInterestedIn(event))
                    .collect(Collectors.toSet());
        }
        affectedListeners.forEach(entry -> entry.notifyListener(event));
    }

    private static class ListenerEntry<T> implements Serializable {
        private final Class<T> eventType;
        private final SerializableConsumer<? super T> listener;
        private final SerializablePredicate<T> predicate;

        ListenerEntry(@Nonnull Class<T> eventType, @Nonnull SerializableConsumer<? super T> listener,
                      SerializablePredicate<T> predicate) {
            this.eventType = Objects.requireNonNull(eventType, "eventType must not be null");
            this.listener = Objects.requireNonNull(listener, "listener must not be null");
            this.predicate = predicate;
        }

        boolean isInterestedIn(@Nonnull Object event) {
            return eventType.isInstance(event) && (predicate == null || predicate.test(eventType.cast(event)));
        }

        void notifyListener(@Nonnull Object event) {
            listener.accept(eventType.cast(event));
        }
    }
}
