package net.pkhapps.appmodel4flow.action;

/**
 * Action to be used in automatic tests.
 */
public class TestAction extends AbstractAction<Integer> {

    private boolean performable = true;
    private int performCount = 0;

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

    @Override
    protected Integer doPerform() {
        return ++performCount;
    }
}
