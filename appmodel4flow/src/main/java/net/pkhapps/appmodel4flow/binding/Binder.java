package net.pkhapps.appmodel4flow.binding;

import com.vaadin.flow.shared.Registration;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Base class for a binder that groups a set of bindings together to make it possible to perform collective operations
 * on them.
 */
@SuppressWarnings("WeakerAccess")
public class Binder implements Serializable {

    private Set<Registration> bindings = new HashSet<>();

    /**
     * Registers the given binding with this binder.
     *
     * @param binding the binding to register, never {@code null}.
     * @return this binder to allow for method chaining.
     */
    @Nonnull
    public Binder withBinding(@Nonnull Registration binding) {
        Objects.requireNonNull(binding, "binding must not be null");
        bindings.add(binding);
        return this;
    }

    /**
     * Returns a stream of all bindings currently in the binder.
     *
     * @return the bindings, never {@code null}.
     */
    @Nonnull
    protected Stream<Registration> getBindings() {
        return bindings.stream();
    }

    /**
     * Calls the {@link Registration#remove() remove} method of each binding and removes them from this binder.
     */
    public void dispose() {
        bindings.forEach(Registration::remove);
        bindings.clear();
    }
}
