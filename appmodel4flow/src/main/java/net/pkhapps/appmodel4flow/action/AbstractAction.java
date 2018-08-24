package net.pkhapps.appmodel4flow.action;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

/**
 * Base class for {@link Action}s. Developers creating new actions will almost always want to extend this class instead
 * of implementing the interface directly. When implementing {@link Action#isPerformable()}, remember to call
 * {@link #fireStateChangeEvent(StateChangeEvent)} every time the state of that flag is changed.
 *
 * @param <OUTPUT> the output type of the action, can be {@link Void} for actions that don't return any output.
 */
@NotThreadSafe
public abstract class AbstractAction<OUTPUT> implements Action<OUTPUT> {

    private Set<SerializableConsumer<PerformEvent<OUTPUT>>> performListeners;
    private Set<SerializableConsumer<StateChangeEvent>> stateChangeListeners;
    private Map<SerializableConsumer<StateChangeEvent>, Void> weakStateChangeListeners;

    @Override
    public OUTPUT perform() {
        if (isPerformable()) {
            final var output = doPerform();
            if (performListeners != null) {
                final PerformEvent<OUTPUT> event = new PerformEvent<>(this, output);
                new HashSet<>(performListeners).forEach(listener -> listener.accept(event));
            }
            return output;
        } else {
            throw new IllegalStateException("The action is not performable");
        }
    }

    /**
     * Fires the given {@link StateChangeEvent} to all registered
     * {@link #addStateChangeListener(SerializableConsumer) listeners}.
     *
     * @param event the event to fire, never {@code null}.
     */
    @SuppressWarnings("WeakerAccess")
    protected void fireStateChangeEvent(@Nonnull StateChangeEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        if (stateChangeListeners != null) {
            new HashSet<>(stateChangeListeners).forEach(listener -> listener.accept(event));
        }
        if (weakStateChangeListeners != null) {
            new HashSet<>(weakStateChangeListeners.keySet()).forEach(listener -> listener.accept(event));
        }
    }

    /**
     * Performs the action. When this method is called, {@link #isPerformable()} is guaranteed to be true.
     *
     * @return the output of the action, may be {@code null}.
     */
    @SuppressWarnings("WeakerAccess")
    protected abstract OUTPUT doPerform();

    @Nonnull
    @Override
    public Registration addPerformListener(@Nonnull SerializableConsumer<PerformEvent<OUTPUT>> listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        if (performListeners == null) {
            performListeners = new HashSet<>();
        }
        performListeners.add(listener);
        return () -> performListeners.remove(listener);
    }

    @Nonnull
    @Override
    public Registration addStateChangeListener(@Nonnull SerializableConsumer<StateChangeEvent> listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        if (stateChangeListeners == null) {
            stateChangeListeners = new HashSet<>();
        }
        stateChangeListeners.add(listener);
        return () -> stateChangeListeners.remove(listener);
    }

    @Override
    public void addWeakStateChangeListener(@Nonnull SerializableConsumer<StateChangeEvent> listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        if (weakStateChangeListeners == null) {
            weakStateChangeListeners = new WeakHashMap<>();
        }
        weakStateChangeListeners.put(listener, null);
    }
}
