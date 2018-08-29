package net.pkhapps.appmodel4flow.action.support;

import com.vaadin.flow.function.SerializableConsumer;
import net.pkhapps.appmodel4flow.action.Action;
import net.pkhapps.appmodel4flow.action.ActionWithoutResult;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A special action that is a combination of multiple actions. For this action to be performable, all
 * the combined actions must be performable. When performed, this action will perform each combined action
 * in the order they were specified when the composite action was created. If any of the combined actions changes its
 * state, the composite action will also change its state.
 */
@NotThreadSafe
public class CompositeAction extends ActionWithoutResult {

    private final SerializableConsumer<Action.StateChangeEvent> anyActionStateChangeListener
            = this::onAnyActionStateChange;
    private final List<Action<?>> actions;

    /**
     * Creates a new composite action.
     *
     * @param actions a list of at least one action that will be combined into this composite action, never {@code null}.
     */
    @SuppressWarnings("WeakerAccess")
    public CompositeAction(@Nonnull List<Action<?>> actions) {
        Objects.requireNonNull(actions, "actions must not be null");
        if (actions.isEmpty()) {
            throw new IllegalArgumentException("The actions list must contain at least one action");
        }
        this.actions = new ArrayList<>(actions);
        actions.forEach(action -> action.addWeakStateChangeListener(anyActionStateChangeListener));
    }

    /**
     * Creates a new composite action.
     *
     * @param actions an array of at least one action that will be combined into this composite action, never {@code null}.
     */
    public CompositeAction(@Nonnull Action<?>... actions) {
        this(Arrays.asList(actions));
    }

    private void onAnyActionStateChange(Action.StateChangeEvent event) {
        fireStateChangeEvent();
    }

    @Override
    public boolean isPerformable() {
        return actions.stream().allMatch(Action::isPerformable);
    }

    @Override
    protected void doPerformWithoutResult() {
        actions.forEach(Action::perform);
    }

    @Override
    public String toString() {
        return String.format("%s@%x[actions=%s]", getClass().getSimpleName(), hashCode(), actions);
    }
}
