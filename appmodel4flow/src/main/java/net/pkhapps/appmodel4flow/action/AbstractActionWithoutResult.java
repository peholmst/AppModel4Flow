package net.pkhapps.appmodel4flow.action;

/**
 * Base class for actions that don't produce any output. This class is provided for developer convenience only and
 * does not introduce any new features.
 */
public abstract class AbstractActionWithoutResult extends AbstractAction<Void> {

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
