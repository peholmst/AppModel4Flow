package net.pkhapps.appmodel4flow.context;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.util.ListenerCollection;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link Context}.
 */
@ThreadSafe
public class DefaultContext implements Context {

    private final WeakReference<Context> parent;
    private final Map<Class<?>, Object> contextualObjects = new ConcurrentHashMap<>();
    private final ListenerCollection<ContextualChangeEvent> listeners = new ListenerCollection<>();
    // Keep a reference to the method pointer here since it will be registered using a weak reference
    @SuppressWarnings("FieldCanBeLocal")
    private final SerializableConsumer<ContextualChangeEvent> parentContextListener = this::fireContextualChangeEvent;

    /**
     * Creates a new {@code DefaultContext} without a parent context.
     */
    public DefaultContext() {
        this(null);
    }

    /**
     * Creates a new {@code DefaultContext} with an optional parent context.
     *
     * @param parent the parent context or {@code null} if the parent has no context.
     */
    public DefaultContext(Context parent) {
        if (parent == null) {
            this.parent = null;
        } else {
            this.parent = new WeakReference<>(parent);
            parent.addWeakContextualChangeListener(parentContextListener);
        }
    }

    @Override
    @Nonnull
    public Optional<Context> getParentContext() {
        return Optional.ofNullable(parent).map(WeakReference::get);
    }

    @Nonnull
    @Override
    public <T> Optional<T> getContextualOptional(@Nonnull Class<T> type) {
        Objects.requireNonNull(type, "type must not be null");
        var contextual = Optional.ofNullable(contextualObjects.get(type)).map(type::cast);
        if (contextual.isPresent()) {
            return contextual;
        } else {
            return getParentContext().flatMap(parent -> parent.getContextualOptional(type));
        }
    }

    @Override
    public boolean containsContextual(@Nonnull Class<?> type) {
        Objects.requireNonNull(type, "type must not be null");
        return contextualObjects.containsKey(type);
    }

    @Override
    public <T> void registerContextual(@Nonnull Class<? super T> type, @Nonnull T contextual) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(contextual, "contextual must not be null");
        var old = contextualObjects.put(type, contextual);
        fireContextualChangeEvent(new ContextualChangeEvent(this, type, old, contextual));
    }

    @Override
    public void unregisterContextual(@Nonnull Class<?> type) {
        Objects.requireNonNull(type, "type must not be null");
        var old = contextualObjects.remove(type);
        if (old != null) {
            fireContextualChangeEvent(new ContextualChangeEvent(this, type, old, null));
        }
    }

    @Nonnull
    @Override
    public Registration addContextualChangeListener(@Nonnull SerializableConsumer<ContextualChangeEvent> listener) {
        return listeners.addListener(listener);
    }

    @Override
    public void addWeakContextualChangeListener(@Nonnull SerializableConsumer<ContextualChangeEvent> listener) {
        listeners.addWeakListener(listener);
    }

    private void fireContextualChangeEvent(@Nonnull ContextualChangeEvent event) {
        listeners.fireEvent(event);
    }
}
