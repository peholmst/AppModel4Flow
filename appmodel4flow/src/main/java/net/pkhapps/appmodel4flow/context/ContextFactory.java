package net.pkhapps.appmodel4flow.context;

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
