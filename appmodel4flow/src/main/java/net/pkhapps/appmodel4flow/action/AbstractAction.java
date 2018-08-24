package net.pkhapps.appmodel4flow.action;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.util.ListenerCollection;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Base class for {@link Action}s. Developers creating new actions will almost always want to extend this class instead
 * of implementing the interface directly. When implementing {@link Action#isPerformable()}, remember to call
 * {@link #fireStateChangeEvent(StateChangeEvent)} every time the state of that flag is changed.
 *
 * @param <OUTPUT> the output type of the action, can be {@link Void} for actions that don't return any output.
 */
@NotThreadSafe
public abstract class AbstractAction<OUTPUT> implements Action<OUTPUT> {

    private ListenerCollection<PerformEvent<OUTPUT>> performListeners;
    private ListenerCollection<StateChangeEvent> stateChangeListeners;

    @Override
    public OUTPUT perform() {
        if (isPerformable()) {
            final var output = doPerform();
            if (performListeners != null) {
                final PerformEvent<OUTPUT> event = new PerformEvent<>(this, output);
                performListeners.fireEvent(event);
            }
            return output;
        } else {
            throw new IllegalStateException("The action is not performable");
        }
    }

    /**
     * Fires the given {@link StateChangeEvent} to all registered listeners.
     *
     * @param event the event to fire, never {@code null}.
     */
    @SuppressWarnings("WeakerAccess")
    protected void fireStateChangeEvent(@Nonnull StateChangeEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        if (stateChangeListeners != null) {
            stateChangeListeners.fireEvent(event);
        }
    }

    /**
     * Creates and {@link #fireStateChangeEvent(StateChangeEvent) fires} a new {@link StateChangeEvent}.
     */
    protected void fireStateChangeEvent() {
        if (stateChangeListeners != null) {
            fireStateChangeEvent(new StateChangeEvent(this));
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
        return getPerformListeners().addListener(listener);
    }

    @Override
    public void addWeakPerformListener(@Nonnull SerializableConsumer<PerformEvent<OUTPUT>> listener) {
        getPerformListeners().addWeakListener(listener);
    }

    private ListenerCollection<PerformEvent<OUTPUT>> getPerformListeners() {
        if (performListeners == null) {
            performListeners = new ListenerCollection<>();
        }
        return performListeners;
    }

    @Nonnull
    @Override
    public Registration addStateChangeListener(@Nonnull SerializableConsumer<StateChangeEvent> listener) {
        return getStateChangeListeners().addListener(listener);
    }

    @Override
    public void addWeakStateChangeListener(@Nonnull SerializableConsumer<StateChangeEvent> listener) {
        getStateChangeListeners().addWeakListener(listener);
    }

    private ListenerCollection<StateChangeEvent> getStateChangeListeners() {
        if (stateChangeListeners == null) {
            stateChangeListeners = new ListenerCollection<>();
        }
        return stateChangeListeners;
    }
}
