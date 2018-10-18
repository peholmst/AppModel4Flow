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

package net.pkhapps.appmodel4flow.incubator.context;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Factory interface for creating new {@link Context}s.
 */
@FunctionalInterface
public interface ContextFactory extends Serializable {

    /**
     * Creates a new {@link Context} with the given parent context.
     *
     * @param parentContext the parent context or {@code null} if there is none.
     * @return the new context, never {@code null}.
     */
    @Nonnull
    Context createContext(Context parentContext);
}
