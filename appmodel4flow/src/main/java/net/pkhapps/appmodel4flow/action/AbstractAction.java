package net.pkhapps.appmodel4flow.action;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.context.Context;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Base class for {@link Action}s. Developers creating new actions will almost always want to extend this class instead
 * of implementing the interface directly. When implementing {@link Action#isPerformable()}, remember to call
 * {@link #fireStateChangeEvent(StateChangeEvent)} every time the state of that flag is changed.
 *
 * @param <OUTPUT> the output type of the action, can be {@link Void} for actions that don't return any output.
 */
@NotThreadSafe
public abstract class AbstractAction<OUTPUT> implements Action<OUTPUT> {

    private final Context context;
    private Set<SerializableConsumer<PerformEvent<OUTPUT>>> performListeners;
    private Set<SerializableConsumer<StateChangeEvent>> stateChangeListeners;

    /**
     * Creates a new action.
     *
     * @param context the context that the action will live inside, never {@code null}.
     */
    protected AbstractAction(@Nonnull Context context) {
        this.context = Objects.requireNonNull(context, "context must not be null");
    }

    @Nonnull
    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public OUTPUT perform() {
        if (isPerformable()) {
            final var output = doPerform();
            if (performListeners != null) {
                final PerformEvent<OUTPUT> event = new PerformEvent<>(this, output);
                performListeners.forEach(listener -> listener.accept(event));
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
            stateChangeListeners.forEach(listener -> listener.accept(event));
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
}
