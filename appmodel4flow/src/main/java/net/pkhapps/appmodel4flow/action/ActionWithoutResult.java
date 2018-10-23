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

import com.vaadin.flow.function.SerializableRunnable;
import net.pkhapps.appmodel4flow.property.ObservableValue;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

/**
 * Base class for actions that don't produce any output. This class is provided for developer convenience only and
 * does not introduce any new features.
 */
@NotThreadSafe
public class ActionWithoutResult extends AbstractAction<Void> {

    private static final long serialVersionUID = 1L;

    private final SerializableRunnable command;
    private static final SerializableRunnable DEFAULT_COMMAND = new SerializableRunnable() {

        @Override
        public void run() {
            throw new UnsupportedOperationException("doPerformWithoutResult has not been overridden");
        }

        @Override
        public String toString() {
            return "N/A";
        }
    };

    /**
     * Default constructor that requires the subclass to override {@link #doPerformWithoutResult()}.
     */
    @SuppressWarnings("WeakerAccess")
    protected ActionWithoutResult() {
        this(DEFAULT_COMMAND);
    }

    /**
     * Constructor that wraps a command inside the action. Please note that in order to change the
     * {@link #isPerformable()} flag, you still need to create a subclass.
     *
     * @param command the command to invoke when the action is performed, never {@code null}.
     */
    public ActionWithoutResult(@Nonnull SerializableRunnable command) {
        this.command = Objects.requireNonNull(command, "command must not be null");
    }

    /**
     * Constructor that requires the subclass to override {@link #doPerformWithoutResult()} and uses an external
     * {@link #isPerformable()} observable value.
     *
     * @param isPerformable the observable value that determines whether this action is performable or not, never {@code null}.
     */
    @SuppressWarnings("WeakerAccess")
    public ActionWithoutResult(@Nonnull ObservableValue<Boolean> isPerformable) {
        this(isPerformable, DEFAULT_COMMAND);
    }

    /**
     * Constructor that wraps a command inside the action and uses an external {@link #isPerformable()} observable value.
     *
     * @param isPerformable the observable value that determines whether this action is performable or not, never {@code null}.
     * @param command       the command to invoke when the action is performed, never {@code null}.
     */
    @SuppressWarnings("WeakerAccess")
    public ActionWithoutResult(@Nonnull ObservableValue<Boolean> isPerformable, @Nonnull SerializableRunnable command) {
        super(isPerformable);
        this.command = Objects.requireNonNull(command, "command must not be null");
    }

    @Override
    protected Void doPerform() {
        doPerformWithoutResult();
        return null;
    }

    /**
     * Performs the action. When this method is called, {@link #isPerformable()} is guaranteed to be true.
     */
    protected void doPerformWithoutResult() {
        command.run();
    }

    @Override
    public String toString() {
        return String.format("%s(isPerformable=%s, command=%s)", getClass().getSimpleName(), isPerformable(), command);
    }
}
