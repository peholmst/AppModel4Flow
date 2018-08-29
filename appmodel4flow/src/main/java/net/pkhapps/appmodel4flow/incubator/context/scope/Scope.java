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

import javax.annotation.Nonnull;

/**
 * Interface defining a scope in which a particular {@link Context} lives. Scopes can be used by classes to look up
 * the correct context.
 */
public interface Scope {

    /**
     * Returns the context of this scope. If a context does not exist, it is
     * {@link #setContextFactory(ContextFactory) created}.
     *
     * @return the context, never {@code null}.
     */
    @Nonnull
    Context getContext();

    /**
     * Specifies the context factory to use to create a new context if this scope does not contain one yet.
     *
     * @param contextFactory the context factory or {@code null} to use the default.
     */
    void setContextFactory(ContextFactory contextFactory);
}
