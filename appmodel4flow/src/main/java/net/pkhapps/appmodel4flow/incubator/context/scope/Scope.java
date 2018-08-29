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
