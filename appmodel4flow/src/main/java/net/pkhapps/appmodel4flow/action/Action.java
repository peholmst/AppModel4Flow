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
import lombok.ToString;
import net.pkhapps.appmodel4flow.property.ObservableValue;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Interface defining an action that can be performed by an actor (typically a user).
 *
 * @param <OUTPUT> the output type of the action, can be {@link Void} for actions that don't return any output.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public interface Action<OUTPUT> extends Serializable {

    /**
     * Checks if this action is performable right now.
     *
     * @return true if the action is performable, false otherwise.
     */
    @Nonnull
    ObservableValue<Boolean> isPerformable();

    /**
     * Performs the action, returning any output.
     *
     * @return the output of the action, may be {@code null}.
     * @throws IllegalStateException if the action is not performable.
     */
    OUTPUT perform();

    /**
     * Registers a listener to be notified whenever this action is performed.
     *
     * @param listener the listener, never {@code null}.
     * @return a registration handle, never {@code null}.
     */
    @Nonnull
    Registration addPerformListener(@Nonnull SerializableConsumer<PerformEvent<OUTPUT>> listener);

    /**
     * Registers a listener to be notified whenever this action is performed. The listener is registered using a weak
     * reference and will be automatically removed when garbage collected. This means you have to make sure you keep
     * another reference to the listener for as long as you need it or it will become garbage collected too soon.
     *
     * @param listener the listener, never {@code null}.
     */
    void addWeakPerformListener(@Nonnull SerializableConsumer<PerformEvent<OUTPUT>> listener);

    /**
     * Event fired by an {@link Action} whenever it has been performed.
     */
    @SuppressWarnings("WeakerAccess")
    @Immutable
    @ToString
    class PerformEvent<OUTPUT> implements Serializable {

        private static final long serialVersionUID = 1L;

        private final Action<OUTPUT> action;
        private final OUTPUT output;

        /**
         * Creates a new {@code ActionPerformedEvent}.
         *
         * @param action the action that was performed, never {@code null}.
         * @param output the output of the action, may be {@code null}.
         */
        public PerformEvent(@Nonnull Action<OUTPUT> action, OUTPUT output) {
            this.action = Objects.requireNonNull(action, "action must not be null");
            this.output = output;
        }

        /**
         * Returns the action that was performed.
         *
         * @return the action, never {@code null}.
         */
        @Nonnull
        public Action<OUTPUT> getAction() {
            return action;
        }

        /**
         * Returns any output generated by the action.
         *
         * @return the output of the action, may be {@code null}.
         */
        public OUTPUT getOutput() {
            return output;
        }
    }
}
