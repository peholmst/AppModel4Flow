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

package net.pkhapps.appmodel4flow.binding.group;

import com.vaadin.flow.shared.Registration;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Base class for a group of bindings to make it possible to perform collective operations on them.
 */
@NotThreadSafe
public class BindingGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Set<Registration> bindings = new HashSet<>();

    /**
     * Registers the given binding with this group.
     *
     * @param binding the binding to register, never {@code null}.
     * @return this binding group to allow for method chaining.
     */
    @Nonnull
    public BindingGroup withBinding(@Nonnull Registration binding) {
        Objects.requireNonNull(binding, "binding must not be null");
        bindings.add(binding);
        return this;
    }

    /**
     * Returns a stream of all bindings currently in the group.
     *
     * @return the bindings, never {@code null}.
     */
    @Nonnull
    @SuppressWarnings("WeakerAccess")
    public Stream<Registration> getBindings() {
        return bindings.stream();
    }

    /**
     * Calls the {@link Registration#remove() remove} method of each binding and removes them from this group.
     */
    public void dispose() {
        bindings.forEach(this::dispose);
        bindings.clear();
    }

    /**
     * Disposes the given binding by calling {@link Registration#remove()} on it. Subclasses may also do other clean up
     * operations.
     *
     * @param binding the binding to dispose of, never {@code null}.
     */
    @SuppressWarnings("WeakerAccess")
    protected void dispose(@Nonnull Registration binding) {
        Objects.requireNonNull(binding, "binding must not be null");
        binding.remove();
    }
}
