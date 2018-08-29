package net.pkhapps.appmodel4flow.action.support;

import com.vaadin.flow.function.SerializableRunnable;
import net.pkhapps.appmodel4flow.action.ActionWithoutResult;
import net.pkhapps.appmodel4flow.binding.FieldBindingGroup;
import net.pkhapps.appmodel4flow.property.CombinedValue;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import net.pkhapps.appmodel4flow.property.support.Combinators;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * TODO Document and test me!
 */
public class FieldBindingGroupAction extends ActionWithoutResult {

    private final FieldBindingGroup fieldBindingGroup;

    /**
     * @param fieldBindingGroup
     */
    public FieldBindingGroupAction(@Nonnull FieldBindingGroup fieldBindingGroup) {
        super(createIsPerformable(fieldBindingGroup));
        this.fieldBindingGroup = fieldBindingGroup;
    }

    /**
     * @param fieldBindingGroup
     * @param command
     */
    public FieldBindingGroupAction(@Nonnull FieldBindingGroup fieldBindingGroup, @Nonnull SerializableRunnable command) {
        super(createIsPerformable(fieldBindingGroup), command);
        this.fieldBindingGroup = fieldBindingGroup;
    }

    private static ObservableValue<Boolean> createIsPerformable(FieldBindingGroup fieldBinder) {
        Objects.requireNonNull(fieldBinder, "fieldBindingGroup must not be null");
        return new CombinedValue<>(Combinators.allTrue(),
                fieldBinder.isDirty(), fieldBinder.isPresentationValid(), fieldBinder.isModelValid());
    }

    /**
     * @return
     */
    @Nonnull
    protected FieldBindingGroup getFieldBindingGroup() {
        return fieldBindingGroup;
    }
}
