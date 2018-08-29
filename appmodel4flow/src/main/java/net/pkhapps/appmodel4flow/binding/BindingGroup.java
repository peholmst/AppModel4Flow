package net.pkhapps.appmodel4flow.binding;

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
@SuppressWarnings("WeakerAccess")
@NotThreadSafe
public class BindingGroup implements Serializable {

    private Set<Registration> bindings = new HashSet<>();

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
    protected Stream<Registration> getBindings() {
        return bindings.stream();
    }

    /**
     * Calls the {@link Registration#remove() remove} method of each binding and removes them from this group.
     */
    public void dispose() {
        bindings.forEach(Registration::remove);
        bindings.clear();
    }
}
