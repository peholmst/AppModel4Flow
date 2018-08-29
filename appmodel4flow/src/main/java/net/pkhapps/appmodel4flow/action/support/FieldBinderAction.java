package net.pkhapps.appmodel4flow.action.support;

import com.vaadin.flow.function.SerializableRunnable;
import net.pkhapps.appmodel4flow.action.ActionWithoutResult;
import net.pkhapps.appmodel4flow.binding.FieldBinder;
import net.pkhapps.appmodel4flow.property.CombinedValue;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import net.pkhapps.appmodel4flow.property.support.Combinators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * TODO Document and test me!
 */
public class FieldBinderAction extends ActionWithoutResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldBinderAction.class);
    private final FieldBinder fieldBinder;

    /**
     * @param fieldBinder
     */
    public FieldBinderAction(@Nonnull FieldBinder fieldBinder) {
        super(createIsPerformable(fieldBinder));
        this.fieldBinder = fieldBinder;
    }

    /**
     * @param fieldBinder
     * @param command
     */
    public FieldBinderAction(@Nonnull FieldBinder fieldBinder, @Nonnull SerializableRunnable command) {
        super(createIsPerformable(fieldBinder), command);
        this.fieldBinder = fieldBinder;
    }

    private static ObservableValue<Boolean> createIsPerformable(FieldBinder fieldBinder) {
        Objects.requireNonNull(fieldBinder, "fieldBinder must not be null");
        return new CombinedValue<>(Combinators.allTrue(),
                fieldBinder.isDirty(), fieldBinder.isPresentationValid(), fieldBinder.isModelValid());
    }

    /**
     * @return
     */
    @Nonnull
    protected FieldBinder getFieldBinder() {
        return fieldBinder;
    }
}
