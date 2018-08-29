package net.pkhapps.appmodel4flow.action.support;

import net.pkhapps.appmodel4flow.action.Action;
import net.pkhapps.appmodel4flow.action.ActionWithoutResult;
import net.pkhapps.appmodel4flow.property.CombinedValue;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import net.pkhapps.appmodel4flow.property.support.Combinators;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A special action that is a combination of multiple actions. For this action to be performable, all
 * the combined actions must be performable. When performed, this action will perform each combined action
 * in the order they were specified when the composite action was created. If any of the combined actions changes its
 * state, the composite action will also change its state.
 */
@NotThreadSafe
public class CompositeAction extends ActionWithoutResult {

    private final List<Action<?>> actions;

    /**
     * Creates a new composite action.
     *
     * @param actions a list of at least one action that will be combined into this composite action, never {@code null}.
     */
    @SuppressWarnings("WeakerAccess")
    public CompositeAction(@Nonnull List<Action<?>> actions) {
        super(combinedIsPerformable(actions));
        this.actions = actions;
    }

    /**
     * Creates a new composite action.
     *
     * @param actions an array of at least one action that will be combined into this composite action, never {@code null}.
     */
    public CompositeAction(@Nonnull Action<?>... actions) {
        this(Arrays.asList(actions));
    }

    private static ObservableValue<Boolean> combinedIsPerformable(@Nonnull Collection<Action<?>> actions) {
        Objects.requireNonNull(actions, "actions must not be null");
        if (actions.isEmpty()) {
            throw new IllegalArgumentException("The actions list must contain at least one action");
        }
        var isPerformableCollection = actions.stream().map(Action::isPerformable).collect(Collectors.toSet());
        return new CombinedValue<>(Combinators.allTrue(), isPerformableCollection);
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
