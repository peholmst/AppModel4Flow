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

import com.vaadin.flow.component.UI;
import com.vaadin.flow.internal.CurrentInstance;
import com.vaadin.flow.server.Command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * TODO Document me
 */
@ThreadSafe
public class PushController implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final PushController DEFAULT = new PushController();

    @Nonnull
    public static PushController getCurrent() {
        var current = CurrentInstance.get(PushController.class);
        return current != null ? current : DEFAULT;
    }

    public static void setCurrent(@Nullable PushController pushController) {
        CurrentInstance.set(PushController.class, pushController);
    }

    public final void push(@Nonnull Command command, @Nonnull Supplier<Optional<UI>> uiSupplier) {
        Objects.requireNonNull(uiSupplier, "uiSupplier must not be null");
        push(command, uiSupplier.get().orElse(null));
    }

    public final void push(@Nonnull Command command, @Nullable UI ui) {
        Objects.requireNonNull(command, "command must not be null");
        doPush(command, ui);
    }

    protected void doPush(@Nonnull Command command, @Nullable UI ui) {
        if (ui != null && isPushNeeded(ui)) {
            ui.access(command);
        } else {
            command.execute();
        }
    }

    private boolean isPushNeeded(@Nonnull UI ui) {
        return !Objects.equals(UI.getCurrent(), ui) || !isSessionLocked(ui);
    }

    private boolean isSessionLocked(@Nonnull UI ui) {
        var session = ui.getSession();
        return session != null && session.hasLock();
    }
}
