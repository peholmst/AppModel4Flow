package net.pkhapps.appmodel4flow.action;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Interface defining an action that can be performed by an actor (typically a user).
 *
 * @param <OUTPUT> the output type of the action, can be {@link Void} for actions that don't return any output.
 */
@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
public interface Action<OUTPUT> extends Serializable {

    /**
     * Checks if this action is performable right now. By default, this methods returns true. Subclasses may override.
     *
     * @return true if the action is performable, false otherwise.
     */
    default boolean isPerformable() { // TODO BEFORE 1.0: Consider turning this into an ObservableValue
        return true;
    }

    /**
     * Performs the action, returning any output.
     *
     * @return the output of the action, may be {@code null}.
     * @throws IllegalStateException if the action is not performable.
     */
    OUTPUT perform();

    /**
     * Registers a listener to be notified whenever this action is performed.
     *
     * @param listener the listener, never {@code null}.
     * @return a registration handle, never {@code null}.
     */
    @Nonnull
    Registration addPerformListener(@Nonnull SerializableConsumer<PerformEvent<OUTPUT>> listener);

    /**
     * Registers a listener to be notified whenever this action is performed. The listener is registered using a weak
     * reference and will be automatically removed when garbage collected. This means you have to make sure you keep
     * another reference to the listener for as long as you need it or it will become garbage collected too soon.
     *
     * @param listener the listener, never {@code null}.
     */
    void addWeakPerformListener(@Nonnull SerializableConsumer<PerformEvent<OUTPUT>> listener);

    /**
     * Registers a listener to be notified whenever the state of this action (e.g. the
     * {@link #isPerformable() performable} flag} is changed.
     *
     * @param listener the listener, never {@code null}.
     * @return a registration handle, never {@code null}.
     */
    @Nonnull
    Registration addStateChangeListener(@Nonnull SerializableConsumer<StateChangeEvent> listener);

    /**
     * Registers a listener to be notified whenever the state of this action (e.g. the
     * {@link #isPerformable() performable} flag} is changed. The listener is registered using a weak reference and will
     * be automatically removed when garbage collected. This means you have to make sure you keep another reference to
     * the listener for as long as you need it or it will become garbage collected too soon.
     *
     * @param listener the listener, never {@code null}.
     */
    void addWeakStateChangeListener(@Nonnull SerializableConsumer<StateChangeEvent> listener);

    /**
     * Base class for events fired by a {@link Action}.
     */
    @Immutable
    abstract class Event<A extends Action<?>> implements Serializable {

        private final A action;

        Event(@Nonnull A action) {
            this.action = Objects.requireNonNull(action, "action must not be null");
        }

        /**
         * Returns the action that fired the event.
         *
         * @return the action, never {@code null}.
         */
        @Nonnull
        public A getAction() {
            return action;
        }
    }

    /**
     * Event fired by an {@link Action} whenever it has been performed.
     */
    @Immutable
    class PerformEvent<OUTPUT> extends Event<Action<OUTPUT>> {

        private final OUTPUT output;

        /**
         * Creates a new {@code ActionPerformedEvent}.
         *
         * @param action the action that was performed, never {@code null}.
         * @param output the output of the action, may be {@code null}.
         */
        public PerformEvent(@Nonnull Action<OUTPUT> action, OUTPUT output) {
            super(action);
            this.output = output;
        }

        /**
         * Returns any output generated by the action.
         *
         * @return the output of the action, may be {@code null}.
         */
        public OUTPUT getOutput() {
            return output;
        }
    }

    /**
     * Event fired by an {@link Action} whenever its state, such as its {@link Action#isPerformable() performable}
     * flag, is changed.
     */
    @Immutable
    class StateChangeEvent extends Event<Action<?>> {

        /**
         * Creates a new {@code ActionStateChangeEvent}.
         *
         * @param action the action whose state has changed, never {@code null}.
         */
        public StateChangeEvent(@Nonnull Action<?> action) {
            super(action);
        }
    }
}
