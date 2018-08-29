package net.pkhapps.appmodel4flow.property.support;

import com.vaadin.flow.function.SerializableFunction;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO Document and test me!
 */
public final class Combinators {

    private Combinators() {
    }

    @Nonnull
    public static SerializableFunction<Stream<Boolean>, Boolean> allTrue() {
        return values -> values.allMatch(Predicates.isTrue());
    }

    @Nonnull
    public static SerializableFunction<Stream<Boolean>, Boolean> anyTrue() {
        return values -> values.anyMatch(Predicates.isTrue());
    }

    @Nonnull
    public static SerializableFunction<Stream<Boolean>, Boolean> allFalse() {
        return values -> values.allMatch(Predicates.isFalse());
    }

    @Nonnull
    public static SerializableFunction<Stream<Boolean>, Boolean> anyFalse() {
        return values -> values.anyMatch(Predicates.isFalse());
    }

    @Nonnull
    public static SerializableFunction<Stream<String>, String> joinStrings() {
        return values -> values.collect(Collectors.joining());
    }

    @Nonnull
    public static SerializableFunction<Stream<String>, String> joinStrings(@Nonnull CharSequence delimiter) {
        return values -> values.collect(Collectors.joining(delimiter));
    }
}
