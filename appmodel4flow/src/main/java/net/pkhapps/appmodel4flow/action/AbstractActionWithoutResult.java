package net.pkhapps.appmodel4flow.action;

import net.pkhapps.appmodel4flow.context.Context;
import net.pkhapps.appmodel4flow.context.scope.Scope;

import javax.annotation.Nonnull;

/**
 * Base class for actions that don't produce any output. This class is provided for developer convenience only and
 * does not introduce any new features.
 */
public abstract class AbstractActionWithoutResult extends AbstractAction<Void> {

    /**
     * Creates a new action.
     *
     * @param context the context that the action will live inside, never {@code null}.
     */
    protected AbstractActionWithoutResult(@Nonnull Context context) {
        super(context);
    }

    /**
     * Creates a new action.
     *
     * @param scope the scope from which the context should be taken, never {@code null}.
     */
    protected AbstractActionWithoutResult(@Nonnull Scope scope) {
        super(scope);
    }

    @Override
    protected Void doPerform() {
        doPerformWithoutResult();
        return null;
    }

    /**
     * Performs the action. When this method is called, {@link #isPerformable()} is guaranteed to be true.
     */
    protected abstract void doPerformWithoutResult();
}
