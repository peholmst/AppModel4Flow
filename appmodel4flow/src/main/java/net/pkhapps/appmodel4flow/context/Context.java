package net.pkhapps.appmodel4flow.context;

import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.shared.Registration;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Interface defining a context in which certain UI actions are performed. The context is a container of contextual
 * objects that are shared by multiple UI components, such as the current user, the current locale, current time zone,
 * etc. Contexts can be nested in such a way that child contexts inherit, but also override, parent contexts.
 */
public interface Context extends Serializable {

    /**
     * Returns the parent context of this context.
     *
     * @return the parent context if this context has one.
     */
    @Nonnull
    Optional<Context> getParentContext();

    /**
     * Returns a contextual object from this context or its parent context if it has one.
     *
     * @param type the type of the contextual object to return, never {@code null}.
     * @param <T>  the type of the contextual object to return.
     * @return the contextual object, never {@code null}.
     * @throws IllegalArgumentException if no contextual object of the given class exists inside this context or any of
     *                                  the parent contexts.
     */
    @Nonnull
    default <T> T getContextual(@Nonnull Class<T> type) {
        return getContextualOptional(type)
                .orElseThrow(() -> new IllegalArgumentException("No contextual object of type " + type + " found"));
    }

    /**
     * Returns a contextual object from this context or its parent context if it has one.
     *
     * @param type the type of the contextual object to return, never {@code null}.
     * @param <T>  the type of the contextual object to return.
     * @return the contextual object if found or an empty {@code Optional} if not, never {@code null}.
     */
    @Nonnull
    <T> Optional<T> getContextualOptional(@Nonnull Class<T> type);

    /**
     * Returns a contextual object from this context or its parent context if it has one.
     *
     * @param type            the type of the contextual object to return, never {@code null}.
     * @param defaultSupplier the supplier to invoke if no contextual object of the given class exists inside this
     *                        context or any of the parent contexts, never {@code null}. Also the supplier must never
     *                        return {@code null} but may throw an unchecked exception.
     * @param <T>             the type of the contextual object to return.
     * @return the contextual object, never {@code null}.
     */
    @Nonnull
    default <T> T getContextual(@Nonnull Class<T> type, @Nonnull Supplier<T> defaultSupplier) {
        return getContextualOptional(type).orElseGet(defaultSupplier);
    }

    /**
     * Utility method for retrieving the {@link Clock} from the context. Unless a clock has been added to the context,
     * the {@link Clock#systemDefaultZone() system clock for the default time zone} is returned.
     *
     * @return the clock, never {@code null}.
     */
    @Nonnull
    default Clock getClock() {
        return getContextual(Clock.class, Clock::systemDefaultZone);
    }

    /**
     * Utility method for retrieving the {@link Locale} from the context. Unless a locale has been added to the context,
     * the {@link Locale#getDefault() default locale} is returned.
     *
     * @return the locale, never {@code null}.
     */
    @Nonnull
    default Locale getLocale() {
        return getContextual(Locale.class, Locale::getDefault);
    }

    /**
     * Utility method for retrieving the {@link ZoneId} from the context. Unless a time zone ID has been added to the
     * context, the {@link ZoneId#systemDefault() default zone} is returned.
     *
     * @return the time zone ID, never {@code null}.
     */
    @Nonnull
    default ZoneId getTimeZone() {
        return getContextual(ZoneId.class, ZoneId::systemDefault);
    }

    /**
     * Checks if this context contains a contextual object of the given type.
     *
     * @param type the type to look for, never {@code null}.
     * @return true if the context contains an object of the requested type, false otherwise.
     */
    boolean containsContextual(@Nonnull Class<?> type);

    /**
     * Registers a contextual object of the given type and notifies the listeners. If an existing object exists of the
     * same type, it will be replaced.
     *
     * @param type       the type of the object to register, never {@code null}.
     * @param contextual the actual contextual object, never {@code null}.
     * @param <T>        the type of the contextual object.
     */
    <T> void registerContextual(@Nonnull Class<? super T> type, @Nonnull T contextual);

    /**
     * Unregisters the contextual object of the given type. If there is no object of the given type, nothing happens.
     * If an object exists, it will be removed from the context and the listeners notified.
     *
     * @param type the type of the object to remove, never {@code null}.
     */
    void unregisterContextual(@Nonnull Class<?> type);

    /**
     * Registers a listener to be notified whenever a contextual object is changed. Events fired in any of the parent
     * contexts will propagate down to descending contexts.
     *
     * @param listener the listener, never {@code null}.
     * @return a registration handle, never {@code null}.
     */
    @Nonnull
    Registration addContextualChangeListener(@Nonnull SerializableConsumer<ContextualChangeEvent> listener);

    /**
     * Registers a listener to be notified whenever a contextual object is changed.  Events fired in any of the parent
     * contexts will propagate down to descending contexts. The listener is registered using a weak reference and will
     * be automatically removed when garbage collected. This means you have to make sure you keep another reference to
     * the listener for as long as you need it or it will become garbage collected too soon.
     *
     * @param listener the listener, never {@code null}.
     */
    void addWeakContextualChangeListener(@Nonnull SerializableConsumer<ContextualChangeEvent> listener);

    /**
     * Event published by a {@link Context} whenever a contextual object is changed
     * ({@link Context#registerContextual(Class, Object) registered} or
     * {@link Context#unregisterContextual(Class) unregistered}).
     */
    @Immutable
    @SuppressWarnings("WeakerAccess")
    class ContextualChangeEvent implements Serializable {
        private final Context context;
        private final Class<?> type;
        private final Object oldContextual;
        private final Object contextual;

        public ContextualChangeEvent(@Nonnull Context context, @Nonnull Class<?> type, Object oldContextual, Object contextual) {
            this.context = Objects.requireNonNull(context, "context must not be null");
            this.type = Objects.requireNonNull(type, "type must not be null");
            this.oldContextual = oldContextual;
            this.contextual = contextual;
        }

        /**
         * Returns the context that published this event. Keep in mind that events can originate from a parent context
         * as well as from the context that the listener is actually registered with.
         *
         * @return the context, never {@code null}.
         */
        @Nonnull
        public Context getContext() {
            return context;
        }

        /**
         * Returns the type of the contextual object that has changed.
         *
         * @return the type, never {@code null}.
         */
        @Nonnull
        public Class<?> getType() {
            return type;
        }

        /**
         * Returns the old contextual object.
         *
         * @return the old contextual object or {@code null} if no contextual object of this {@link #getType() type} was
         * registered before.
         */
        public Object getOldContextual() {
            return oldContextual;
        }

        /**
         * Returns the current (new) contextual object.
         *
         * @return the contextual object or {@code null} if this event was fired because it was unregistered.
         */
        public Object getContextual() {
            return contextual;
        }
    }
}
