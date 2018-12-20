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

package net.pkhapps.appmodel4flow.model;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.VaadinSession;
import net.pkhapps.appmodel4flow.binding.PushController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.util.*;

/**
 * TODO Document me!
 */
@ThreadSafe
public abstract class SessionScopedModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final QueuingPushController pushController = new QueuingPushController();
    private final VaadinSession session;

    protected SessionScopedModel(@Nonnull VaadinSession session) {
        this.session = session;
    }

    @Nonnull
    public static <M extends SessionScopedModel> M getInstance(@Nonnull Class<M> modelClass,
                                                               @Nonnull ModelFactory<M> factory) {
        Objects.requireNonNull(modelClass, "modelClass must not be null");
        Objects.requireNonNull(factory, "factory must not be null");
        var session = VaadinSession.getCurrent();
        if (session == null) {
            throw new IllegalStateException("No VaadinSession bound to current thread");
        }
        var model = session.getAttribute(modelClass);
        if (model == null) {
            model = factory.create(session);
            session.setAttribute(modelClass, model);
        }
        return model;
    }

    protected void beginUpdate() {
        session.lock();
        PushController.setCurrent(pushController);
    }

    protected void endUpdate() {
        session.unlock();
        try {
            pushController.push();
        } finally {
            PushController.setCurrent(null);
        }
    }

    protected void access(@Nonnull Command command) {
        beginUpdate();
        try {
            command.execute();
        } finally {
            endUpdate();
        }
    }

    @FunctionalInterface
    public interface ModelFactory<M extends SessionScopedModel> extends Serializable {
        @Nonnull
        M create(@Nonnull VaadinSession session);
    }

    private static class QueuingPushController extends PushController {

        private static final long serialVersionUID = 1L;

        private final Map<UI, List<Command>> commandQueueMap = new HashMap<>();

        @Override
        protected void doPush(@Nonnull Command command, @Nullable UI ui) {
            if (ui != null) {
                getCommandQueue(ui).add(command);
            } else {
                command.execute();
            }
        }

        @Nonnull
        private List<Command> getCommandQueue(@Nonnull UI ui) {
            return commandQueueMap.computeIfAbsent(ui, key -> new ArrayList<>());
        }

        public void push() {
            try {
                commandQueueMap.forEach((ui, commands) -> ui.access(() -> commands.forEach(Command::execute)));
            } finally {
                commandQueueMap.clear();
            }
        }
    }
}
