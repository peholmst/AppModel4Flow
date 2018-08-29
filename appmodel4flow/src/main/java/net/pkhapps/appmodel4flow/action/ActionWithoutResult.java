package net.pkhapps.appmodel4flow.action;

import com.vaadin.flow.function.SerializableRunnable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Base class for actions that don't produce any output. This class is provided for developer convenience only and
 * does not introduce any new features.
 */
@NotThreadSafe
public class ActionWithoutResult extends AbstractAction<Void> {

    private final SerializableRunnable command;

    /**
     * Default constructor that requires the subclass to override {@link #doPerformWithoutResult()}.
     */
    public ActionWithoutResult() {
        command = () -> {
            throw new UnsupportedOperationException("doPerformWithoutResult has not been overridden");
        };
    }

    /**
     * Constructor that wraps a command inside the action. Please note that in order to change the
     * {@link #isPerformable()} flag, you still need to create a subclass.
     *
     * @param command the command to invoke when the action is performed, never {@code null}.
     */
    public ActionWithoutResult(@Nonnull SerializableRunnable command) {
        this.command = Objects.requireNonNull(command, "command must not be null");
    }

    @Override
    protected Void doPerform() {
        doPerformWithoutResult();
        return null;
    }

    /**
     * Performs the action. When this method is called, {@link #isPerformable()} is guaranteed to be true.
     */
    protected void doPerformWithoutResult() {
        command.run();
    }

    @Override
    public String toString() {
        if (command == null) {
            return String.format("%s@%x[overridden method]", getClass().getSimpleName(), hashCode());
        } else {
            return String.format("%s@%x[command=%s]", getClass().getSimpleName(), hashCode(), command);
        }
    }
}
