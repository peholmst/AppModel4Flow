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

package net.pkhapps.appmodel4flow.incubator.context.scope;

import com.vaadin.flow.server.VaadinSession;
import net.pkhapps.appmodel4flow.incubator.context.Context;
import net.pkhapps.appmodel4flow.incubator.context.ContextFactory;
import net.pkhapps.appmodel4flow.incubator.context.DefaultContext;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;

/**
 * {@link Scope} that retrieves its context from the {@link VaadinSession}. The retrieved context will be a child of the
 * {@link RootScope#getSingleton() root context}.
 *
 * @see #getSingleton()
 */
@ThreadSafe
public class SessionScope implements Scope {

    private static final SessionScope INSTANCE = new SessionScope();

    private ContextFactory contextFactory = DefaultContext::new;

    private SessionScope() {
    }

    @Nonnull
    @Override
    public Context getContext() {
        var session = VaadinSession.getCurrent();
        if (session == null) {
            throw new IllegalStateException("No VaadinSession bound to current thread");
        }
        session.lock();
        try {
            var context = session.getAttribute(Context.class);
            if (context == null) {
                context = contextFactory.createContext(RootScope.getSingleton().getContext());
                session.setAttribute(Context.class, context);
            }
            return context;
        } finally {
            session.unlock();
        }
    }

    @Override
    public void setContextFactory(ContextFactory contextFactory) {
        synchronized (this) {
            this.contextFactory = Objects.requireNonNullElseGet(contextFactory, () -> DefaultContext::new);
        }
    }

    /**
     * Returns the singleton instance of this {@link Scope}.
     *
     * @return the session scope, never {@code null}.
     */
    @Nonnull
    public static Scope getSingleton() {
        return INSTANCE;
    }
}
