package net.pkhapps.appmodel4flow.action.support;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableRunnable;
import net.pkhapps.appmodel4flow.action.ActionWithoutResult;
import net.pkhapps.appmodel4flow.binding.FieldBinder;
import net.pkhapps.appmodel4flow.property.ObservableValue;
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
    @SuppressWarnings("FieldCanBeLocal") // We need the reference here, otherwise it will ge GC:d too soon
    private final SerializableConsumer<ObservableValue.ValueChangeEvent<Boolean>> fieldBinderStatusChangeListener = event -> updateActionState();

    /**
     * @param fieldBinder
     */
    public FieldBinderAction(@Nonnull FieldBinder fieldBinder) {
        this.fieldBinder = setUpFieldBinder(fieldBinder);
        updateActionState();
    }

    /**
     * @param fieldBinder
     * @param command
     */
    public FieldBinderAction(@Nonnull FieldBinder fieldBinder, @Nonnull SerializableRunnable command) {
        super(command);
        this.fieldBinder = setUpFieldBinder(fieldBinder);
        updateActionState();
    }

    private FieldBinder setUpFieldBinder(@Nonnull FieldBinder fieldBinder) {
        Objects.requireNonNull(fieldBinder, "fieldBinder must not be null");
        fieldBinder.isDirty().addWeakValueChangeListener(fieldBinderStatusChangeListener);
        fieldBinder.isPresentationValid().addWeakValueChangeListener(fieldBinderStatusChangeListener);
        fieldBinder.isModelValid().addWeakValueChangeListener(fieldBinderStatusChangeListener);
        return fieldBinder;
    }

    private void updateActionState() {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.debug("Updating action state: dirty={}, presentationValid={}, modelValid={}", fieldBinder.isDirty().getValue(),
                    fieldBinder.isPresentationValid().getValue(), fieldBinder.isModelValid().getValue());
        }
        fireStateChangeEvent();
    }

    @Override
    public boolean isPerformable() {
        return fieldBinder.isModelValid().getValue()
                && fieldBinder.isPresentationValid().getValue()
                && fieldBinder.isDirty().getValue();
    }

    /**
     * @return
     */
    @Nonnull
    protected FieldBinder getFieldBinder() {
        return fieldBinder;
    }
}
