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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.property.ObservableValue;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * One-way binding that binds an {@link ObservableValue} to any {@link Component} property so that when the observable
 * value is changed, the component property is updated as well. This binding was originally designed for binding
 * boolean observable values to {@link Component#setVisible(boolean) visible} and/or
 * {@link com.vaadin.flow.component.HasEnabled#setEnabled(boolean) enabled} but can be used for other properties
 * as well.
 *
 * @param <MODEL>     the value type of the model.
 * @param <COMPONENT> the type of the component.
 */
@NotThreadSafe
public class ComponentBinding<MODEL, COMPONENT extends Component> implements Registration {

    private static final long serialVersionUID = 1L;

    private final Registration modelRegistration;
    private final ObservableValue<MODEL> model;
    private final COMPONENT component;
    private final SerializableBiConsumer<COMPONENT, MODEL> setterMethod;

    /**
     * Creates a new {@code ComponentBinding}.
     *
     * @param model        the model to bind, never {@code null}.
     * @param component    the component to bind, never {@code null}.
     * @param setterMethod the method to invoke to transfer the model value to the component, never {@code null}.
     */
    public ComponentBinding(@Nonnull ObservableValue<MODEL> model,
                            @Nonnull COMPONENT component,
                            @Nonnull SerializableBiConsumer<COMPONENT, MODEL> setterMethod) {
        this.model = Objects.requireNonNull(model, "model must not be null");
        this.component = Objects.requireNonNull(component, "component must not be null");
        this.setterMethod = Objects.requireNonNull(setterMethod, "setterMethod must not be null");
        modelRegistration = model.addValueChangeListener(event -> updateComponentState());
        updateComponentState();
    }

    private void updateComponentState() {
        setterMethod.accept(component, model.getValue());
    }

    @Override
    public void remove() {
        modelRegistration.remove();
    }
}
