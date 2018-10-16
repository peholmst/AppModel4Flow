/*
 * Copyright (c) 2018 the original authors (see project POM file)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.pkhapps.appmodel4flow.action.support;

import lombok.ToString;
import net.pkhapps.appmodel4flow.action.Action;
import net.pkhapps.appmodel4flow.action.ActionWithoutResult;
import net.pkhapps.appmodel4flow.property.CombinedValue;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import net.pkhapps.appmodel4flow.property.support.Combiners;

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
@ToString(callSuper = true)
public class CompositeAction extends ActionWithoutResult {

    private static final long serialVersionUID = 1L;

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

    @Nonnull
    private static ObservableValue<Boolean> combinedIsPerformable(@Nonnull Collection<Action<?>> actions) {
        Objects.requireNonNull(actions, "actions must not be null");
        if (actions.isEmpty()) {
            throw new IllegalArgumentException("The actions list must contain at least one action");
        }
        var isPerformableCollection = actions.stream().map(Action::isPerformable).collect(Collectors.toList());
        return new CombinedValue<>(Combiners.allTrue(), isPerformableCollection);
    }

    @Override
    protected void doPerformWithoutResult() {
        actions.forEach(Action::perform);
    }
}
