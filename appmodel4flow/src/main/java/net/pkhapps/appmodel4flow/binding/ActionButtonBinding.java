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

package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import net.pkhapps.appmodel4flow.action.Action;
import net.pkhapps.appmodel4flow.property.ObservableValue;

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
@Slf4j
public class ActionButtonBinding implements Serializable, Registration {

    private static final long serialVersionUID = 1L;

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

    private void onButtonClick(@SuppressWarnings("unused") ClickEvent<Button> event) {
        log.trace("Performing action {} after click on button {}", action, button);
        action.perform();
    }

    private void onActionPerformableChange(@SuppressWarnings("unused") ObservableValue.ValueChangeEvent<Boolean> event) {
        updateButtonState();
    }

    private void updateButtonState() {
        var performable = action.isPerformable().getValue();
        log.trace("Setting enabled state of button {} to {}", button, performable);
        button.setEnabled(performable);
    }
}
