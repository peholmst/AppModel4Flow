package net.pkhapps.appmodel4flow.property.support;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * TODO Document and test me!
 */
public final class Predicates {

    private Predicates() {
    }

    @Nonnull
    public static Predicate<Boolean> isTrue() {
        return value -> value;
    }

    @Nonnull
    public static Predicate<Boolean> isFalse() {
        return value -> !value;
    }
}
