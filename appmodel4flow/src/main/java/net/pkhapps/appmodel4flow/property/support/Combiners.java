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

package net.pkhapps.appmodel4flow.property.support;

import com.vaadin.flow.function.SerializableFunction;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class with different combiner functions primarily intended to be used with
 * {@link net.pkhapps.appmodel4flow.property.CombinedValue}.
 */
@SuppressWarnings("WeakerAccess")
public final class Combiners {

    private Combiners() {
    }

    /**
     * Combiner that <em>ANDs</em> all the booleans in the stream together. If the stream is empty, this method returns
     * true.
     *
     * @return true if all of the booleans in the stream are true, false otherwise.
     */
    @Nonnull
    public static SerializableFunction<Stream<Boolean>, Boolean> allTrue() {
        return values -> values.allMatch(Predicates.isTrue());
    }

    /**
     * Combiner that <em>ORs</em> all the booleans in the stream together. If the stream is empty, this method returns
     * false.
     *
     * @return true if at least one of the booleans in the stream is true, false otherwise.
     */
    @Nonnull
    public static SerializableFunction<Stream<Boolean>, Boolean> anyTrue() {
        return values -> values.anyMatch(Predicates.isTrue());
    }

    /**
     * Combiner that <em>inverse ANDs</em> all of the booleans in the stream together. If the stream is empty, this
     * method returns true.
     *
     * @return true if all of the booleans in the stream are false, false otherwise.
     */
    @Nonnull
    public static SerializableFunction<Stream<Boolean>, Boolean> allFalse() {
        return values -> values.allMatch(Predicates.isFalse());
    }

    /**
     * Combiner that <em>inverse ORs</em> all of the booleans in the stream together. If the stream is empty, this
     * method returns false.
     *
     * @return true if at least one of the booleans in the stream is false, false otherwise.
     */
    @Nonnull
    public static SerializableFunction<Stream<Boolean>, Boolean> anyFalse() {
        return values -> values.anyMatch(Predicates.isFalse());
    }

    /**
     * Combiner that joins all the strings in the stream together without any delimiter. If the stream is empty, this
     * method returns an empty string.
     *
     * @return the joined string, never {@code null}.
     */
    @Nonnull
    public static SerializableFunction<Stream<String>, String> joinStrings() {
        return values -> values.collect(Collectors.joining());
    }

    /**
     * Combiner that joins all the strings in the stream together with the given delimiter. If the stream is empty, this
     * method returns an empty string.
     *
     * @param delimiter the delimiter to put between strings, never {@code null}.
     * @return the joined string, never {@code null}.
     */
    @Nonnull
    public static SerializableFunction<Stream<String>, String> joinStrings(@Nonnull CharSequence delimiter) {
        return values -> values.collect(Collectors.joining(delimiter));
    }
}
