package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.action.Action;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.Objects;

/**
 * Binding that binds an {@link Action} and a {@link Button} together. When the button is clicked, the action is
 * performed. If the action is not performable, the button is disabled. Remember to call {@link #remove()} when the
 * binding is no longer needed to avoid memory leaks.
 */
@NotThreadSafe
public class ActionButtonBinding implements Serializable, Registration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionButtonBinding.class);
    private final Action<?> action;
    private final Button button;
    private final Registration buttonRegistration;
    private final Registration actionRegistration;

    /**
     * Creates a new {@code ActionButtonBinding}.
     *
     * @param action the action to bind to the button, never {@code null}.
     * @param button the button to bind to, never {@code null}.
     */
    public ActionButtonBinding(@Nonnull Action<?> action, @Nonnull Button button) {
        this.action = Objects.requireNonNull(action, "action must not be null");
        this.button = Objects.requireNonNull(button, "button must not be null");

        buttonRegistration = button.addClickListener(this::onButtonClick);
        actionRegistration = action.isPerformable().addValueChangeListener(this::onActionPerformableChange);
        updateButtonState();
    }

    @Override
    public void remove() {
        buttonRegistration.remove();
        actionRegistration.remove();
    }

    private void onButtonClick(ClickEvent<Button> event) {
        LOGGER.trace("Performing action {} after click on button {}", action, button);
        action.perform();
    }

    private void onActionPerformableChange(ObservableValue.ValueChangeEvent<Boolean> event) {
        updateButtonState();
    }

    private void updateButtonState() {
        var performable = action.isPerformable().getValue();
        LOGGER.trace("Setting enabled state of button {} to {}", button, performable);
        button.setEnabled(performable);
    }
}
