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

package net.pkhapps.appmodel4flow.binding.group.support;

import com.vaadin.flow.function.SerializableRunnable;
import net.pkhapps.appmodel4flow.action.ActionWithoutResult;
import net.pkhapps.appmodel4flow.binding.group.FieldBindingGroup;
import net.pkhapps.appmodel4flow.property.CombinedValue;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import net.pkhapps.appmodel4flow.property.support.Combiners;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Base class for actions that are performable when a specific {@link FieldBindingGroup} is
 * {@link FieldBindingGroup#isDirty() dirty}, {@link FieldBindingGroup#isPresentationValid() has a valid presentation}
 * and a valid {@link FieldBindingGroup#isModelValid() model} (in other words, whenever the field binding group contains
 * changes that are valid).
 */
@NotThreadSafe
public class FieldBindingGroupAction extends ActionWithoutResult {

    private static final long serialVersionUID = 1L;

    private final FieldBindingGroup fieldBindingGroup;

    /**
     * Creates a new {@code FieldBindingGroupAction} that requires the subclass to override {@link #doPerformWithoutResult()}.
     *
     * @param fieldBindingGroup the field binding group, never {@code null}.
     */
    protected FieldBindingGroupAction(@Nonnull FieldBindingGroup fieldBindingGroup) {
        super(createIsPerformable(fieldBindingGroup));
        this.fieldBindingGroup = fieldBindingGroup;
    }

    /**
     * Creates a new {@code FieldBindingGroupAction} that executes a command when performed.
     *
     * @param fieldBindingGroup the field binding group, never {@code null}.
     * @param command           the command to invoke when the action is performed, never {@code null}.
     */
    public FieldBindingGroupAction(@Nonnull FieldBindingGroup fieldBindingGroup, @Nonnull SerializableRunnable command) {
        super(createIsPerformable(fieldBindingGroup), command);
        this.fieldBindingGroup = fieldBindingGroup;
    }

    private static ObservableValue<Boolean> createIsPerformable(FieldBindingGroup fieldBindingGroup) {
        Objects.requireNonNull(fieldBindingGroup, "fieldBindingGroup must not be null");
        return new CombinedValue<>(Combiners.allTrue(),
                fieldBindingGroup.isDirty(), fieldBindingGroup.isPresentationValid(), fieldBindingGroup.isModelValid());
    }

    /**
     * Returns the field binding group whose state determines whether this action is performable or not.
     *
     * @return the field binding group, never {@code null}.
     */
    @SuppressWarnings("WeakerAccess")
    @Nonnull
    protected FieldBindingGroup getFieldBindingGroup() {
        return fieldBindingGroup;
    }
}
