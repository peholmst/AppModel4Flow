package net.pkhapps.appmodel4flow.action;

/**
 * Action to be used in automatic tests.
 */
public class TestAction extends AbstractAction<Integer> {

    private int performCount = 0;

    public void setPerformable(boolean performable) {
        super.setPerformable(performable);
    }

    public int getPerformCount() {
        return performCount;
    }

    @Override
    protected Integer doPerform() {
        return ++performCount;
    }
}
