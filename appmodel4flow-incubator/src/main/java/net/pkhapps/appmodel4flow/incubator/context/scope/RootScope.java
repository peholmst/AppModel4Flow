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

import net.pkhapps.appmodel4flow.incubator.context.Context;
import net.pkhapps.appmodel4flow.incubator.context.ContextFactory;
import net.pkhapps.appmodel4flow.incubator.context.DefaultContext;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;

/**
 * {@link Scope} that is global to the entire application and always available.
 *
 * @see #getSingleton()
 */
@ThreadSafe
public final class RootScope implements Scope {

    private static final RootScope INSTANCE = new RootScope();

    private ContextFactory contextFactory = DefaultContext::new;

    private Context rootContext;

    private RootScope() {
    }

    @Nonnull
    @Override
    public Context getContext() {
        if (rootContext == null) {
            synchronized (this) {
                if (rootContext == null) {
                    rootContext = contextFactory.createContext(null);
                }
            }
        }
        return rootContext;
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
     * @return the root scope, never {@code null}.
     */
    @Nonnull
    public static Scope getSingleton() {
        return INSTANCE;
    }
}
