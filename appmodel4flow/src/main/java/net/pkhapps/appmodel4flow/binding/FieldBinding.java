package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.property.ObservableValue;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Interface for a field binding that binds a model to a UI field.
 *
 * @param <MODEL>        the value type of the model.
 * @param <PRESENTATION> the value type of the field.
 */
public interface FieldBinding<MODEL, PRESENTATION> extends Serializable, Registration {

    /**
     * Returns the UI field.
     *
     * @return the field, never {@code null}.
     */
    @Nonnull
    HasValue<? extends HasValue.ValueChangeEvent<PRESENTATION>, PRESENTATION> getField();

    /**
     * Returns the model.
     *
     * @return the model, never {@code null}.
     */
    @Nonnull
    ObservableValue<MODEL> getModel();

    /**
     * Returns whether the presentation value is valid. If the presentation value is not valid, it means the
     * value in the UI could not be converted to the value needed by the model.
     *
     * @return true if the presentation is valid, false if it is not.
     * @see #isModelValid()
     */
    @Nonnull
    ObservableValue<Boolean> isPresentationValid();

    /**
     * Returns whether the model value is valid or not. If the model value is not valid, it means the value in the UI
     * was successfully converted to a value needed by the model, but did not pass through some implementation specific
     * validation mechanism.
     *
     * @return true if the model is valid, false if it is not.
     * @see #isPresentationValid()
     */
    @Nonnull
    ObservableValue<Boolean> isModelValid();

    /**
     * Breaks the binding, removing any listeners registered with the model and/or the UI field.
     */
    @Override
    void remove();
}
