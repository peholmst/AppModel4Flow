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

package net.pkhapps.appmodel4flow.action;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;
import net.pkhapps.appmodel4flow.property.DefaultObservableValue;
import net.pkhapps.appmodel4flow.property.ObservableValue;
import net.pkhapps.appmodel4flow.util.ListenerCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Base class for {@link Action}s. Developers creating new actions will almost always want to extend this class instead
 * of implementing the interface directly.
 *
 * @param <OUTPUT> the output type of the action, can be {@link Void} for actions that don't return any output.
 */
@NotThreadSafe
public abstract class AbstractAction<OUTPUT> implements Action<OUTPUT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Action.class);
    private final ObservableValue<Boolean> isPerformable;
    private ListenerCollection<PerformEvent<OUTPUT>> performListeners;

    /**
     * Default constructor with an internal {@link #isPerformable()} observable value. You can use
     * {@link #setPerformable(boolean)} to change the value of the flag (the default is true).
     */
    protected AbstractAction() {
        this(new IsPerformableValue());
    }

    /**
     * Constructor with an external {@link #isPerformable()} observable value. You have to interact with the
     * value directly to change it.
     *
     * @param isPerformable the observable value that determines whether this action is performable or not, never {@code null}.
     */
    @SuppressWarnings("WeakerAccess")
    protected AbstractAction(@Nonnull ObservableValue<Boolean> isPerformable) {
        this.isPerformable = Objects.requireNonNull(isPerformable, "isPerformable must not be null");
    }

    @Override
    public OUTPUT perform() {
        if (isPerformable().getValue()) {
            try {
                final var output = doPerform();
                if (performListeners != null) {
                    final PerformEvent<OUTPUT> event = new PerformEvent<>(this, output);
                    LOGGER.debug("Firing event {}", event);
                    performListeners.fireEvent(event);
                }
                return output;
            } catch (RuntimeException ex) {
                LOGGER.error("An error occurred while performing action " + this, ex);
                throw ex;
            }
        } else {
            LOGGER.warn("Tried to perform action {} even though it is not performable", this);
            throw new IllegalStateException("The action is not performable");
        }
    }

    /**
     * Performs the action. When this method is called, {@link #isPerformable()} is guaranteed to be true.
     *
     * @return the output of the action, may be {@code null}.
     */
    protected abstract OUTPUT doPerform();

    @Nonnull
    @Override
    public ObservableValue<Boolean> isPerformable() {
        return isPerformable;
    }

    /**
     * Sets the value of the {@link #isPerformable()} flag. This is only possible if the action was created using
     * the {@link #AbstractAction() default constructor}.
     *
     * @param performable true if the action is performable, false if it is not.
     * @throws UnsupportedOperationException if this action was created using the {@link #AbstractAction(ObservableValue)} constructor.
     */
    protected void setPerformable(boolean performable) {
        if (isPerformable instanceof IsPerformableValue) {
            ((IsPerformableValue) isPerformable).setValue(performable);
        } else {
            throw new UnsupportedOperationException("The isPerformable value is external and cannot be set using this method");
        }
    }

    @Nonnull
    @Override
    public Registration addPerformListener(@Nonnull SerializableConsumer<PerformEvent<OUTPUT>> listener) {
        return getPerformListeners().addListener(listener);
    }

    @Override
    public void addWeakPerformListener(@Nonnull SerializableConsumer<PerformEvent<OUTPUT>> listener) {
        getPerformListeners().addWeakListener(listener);
    }

    @Nonnull
    private ListenerCollection<PerformEvent<OUTPUT>> getPerformListeners() {
        if (performListeners == null) {
            performListeners = new ListenerCollection<>();
        }
        return performListeners;
    }

    private static class IsPerformableValue extends DefaultObservableValue<Boolean> {
        IsPerformableValue() {
            super(true);
        }
    }
}
