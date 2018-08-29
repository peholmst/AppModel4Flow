package net.pkhapps.appmodel4flow.action;

/**
 * Action to be used in automatic tests.
 */
public class TestAction extends AbstractActionWithoutResult {

    private boolean performable = true;
    private int performCount = 0;

    @Override
    protected void doPerformWithoutResult() {
        performCount++;
    }

    @Override
    public boolean isPerformable() {
        return performable;
    }

    public void setPerformable(boolean performable) {
        this.performable = performable;
        fireStateChangeEvent();
    }

    public int getPerformCount() {
        return performCount;
    }
}
